/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


var context = document.getElementById("modello3").getAttribute("data-context");

$.getScript(context + '/page/partialView/partialView.js', function () {});

let calendarioModello3;
let lezioniModello3;
let dataStart;
let dataEnd;
let today = new Date(new Date().toDateString());
let mapLezioni = new Map();
let mapCalendario = new Map();
let mapDateLezioni = new Map();
let maplezioniD = new Map();
let sunday;
let firstLesson = false;
let isEditable = false;

function setRangeDatesDay(giornoLezione, lezione) {
    let rangeDate_min = dataStart;
    let rangeDate_max = dataEnd;
    let days;
    if (typeof (mapDateLezioni.get(lezione - 1)) !== 'undefined') {
        days = 1;
        if (moment(new Date(mapDateLezioni.get(lezione - 1))).format('YYYY-MM-DD') <= moment(new Date()).format('YYYY-MM-DD')) {
            days++;
        }
        rangeDate_min = moment(new Date(mapDateLezioni.get(lezione - 1))).add(days, 'd')._d;
    } else if (giornoLezione != null && rangeDate_min > giornoLezione) {
        rangeDate_min = giornoLezione;
    }
    sunday = rangeDate_min.getDay() == 0 ? 1 : 0;//se è domenica vado al giorno successivo;
    rangeDate_min = moment(rangeDate_min).add(sunday, 'd')._d;

    if (typeof (mapDateLezioni.get(lezione + 1)) !== 'undefined') {
        rangeDate_max = moment(new Date(mapDateLezioni.get(lezione + 1))).subtract(1, 'd')._d;
    }
    sunday = rangeDate_max.getDay() == 0 ? 1 : 0;//se è domenica vado al giorno successivo;
    rangeDate_max = moment(rangeDate_max).subtract(sunday, 'd')._d;

    /*Controllo dell'orario, per lezioni a distanza di un giorno e superate le 12 aggiungo +2 lavorativi*/
    if (checkDateAndHour(rangeDate_min)) {
        rangeDate_min = moment(rangeDate_min).add(1, 'd')._d;
        if (rangeDate_min.getDay() == 0) {
            //se è domenica, aggiungo un ulteriore giorno
            rangeDate_min = moment(rangeDate_min).add(1, 'd')._d;
        }
    }
    giornoLezione = giornoLezione == null ? rangeDate_min : giornoLezione;
    return [rangeDate_min, rangeDate_max, giornoLezione];
}

function changeLezione(idlezione, l) {
    let lez = mapLezioni.get(idlezione);
    let days = setRangeDatesDay(new Date(lez.giorno), l);
    swal.fire({
        title: 'Visualizza/Modifica Lezione ' + l + '<br>(Unità didattica ' + lez.codice_ud + ')',
        html: getHtml("swalLezioneCalendarioSingle", context),
        animation: false,
        showCancelButton: true,
        width: '50%',
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        customClass: {
            popup: 'animated bounceInUp'
        },
        onOpen: function () {
            $('input.time').timepicker({
                showMeridian: false,
                interval: 5,
                showInputs: false,
                snapToStep: true,
                icons: {
                    up: 'la la-angle-up',
                    down: 'la la-angle-down'
                }
            });

            var arrows = {
                leftArrow: '<i class="la la-angle-left"></i>',
                rightArrow: '<i class="la la-angle-right"></i>'
            }

            $('#giorno').datepicker({
                orientation: "bottom left",
                todayHighlight: false,
                templates: arrows,
                autoclose: true,
                format: 'dd/mm/yyyy',
                endDate: days[1],
                startDate: days[0],
                daysOfWeekHighlighted: "0",
                daysOfWeekDisabled: [0]
//                datesDisabled: ['2/4/2021'],
            });
            $("#tot_hh").html('Totale ore di lezione da effettuare: <b>' + lez.lezione_calendario.ore + '</b>');
            $('#orario1_start').val(lez.orainizio);
            $('#orario1_start').timepicker("setTime", lez.orainizio);
            $('#orario1_end').val(lez.orafine);
            $('#orario1_end').timepicker("setTime", lez.orafine);
            $('#giorno').datepicker("setDate", formattedDate(days[2]));//
            $("#giorno").datepicker("update");
            $.get(context + "/QuerySA?type=getDocentiByPrg&idprogetto=" + lez.idprogettoformativo, function (resp) {
                var json = JSON.parse(resp);
                for (var i = 0; i < json.length; i++) {
                    if (lez.docente.id === json[i].id) {
                        $("#docente").append('<option selected value="' + json[i].id + '">' + json[i].cognome + " " + json[i].nome + '</option>');
                    } else {
                        $("#docente").append('<option value="' + json[i].id + '">' + json[i].cognome + " " + json[i].nome + '</option>');
                    }
                }
            });
            $('#docente').select2({
                dropdownCssClass: "select2-on-top",
                minimumResultsForSearch: -1,
            });
            $('#orario1_start').change(function (e) {
                $('#orario1_start').val(checktime($('#orario1_start').val(), '8:00', $('#orario1_end').val()));
            });
            $('#orario1_end').change(function (e) {
                $('#orario1_end').val(checktime($('#orario1_end').val(), $('#orario1_start').val(), '21:00'));
            });
        },
        preConfirm: function () {
            var err = false;
            err = checkObblFieldsContent($('#swalLezioneCalendarioSingle')) ? true : err;
            err = !check_limit_hh($('#orario1_start').val(), $('#orario1_end').val(), lez.lezione_calendario.ore, lez.codice_ud, false) ? true : err;
            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "giorno": $('#giorno').val(),
                        "docente": $('#docente').val(),
                        "orario1_start": $('#orario1_start').val(),
                        "orario1_end": $('#orario1_end').val(),
                    });
                });
            } else {
                return false;
            }
        },
    }).then((result) => {
        if (result.value) {
            showLoad();
            var fdata = new FormData();
            fdata.append("giorno", result.value.giorno);
            fdata.append("docente1", result.value.docente);
            fdata.append("orario1_start", result.value.orario1_start);
            fdata.append("orario1_end", result.value.orario1_end);
            fdata.append("tipo_modello", "m3_single");
            fdata.append("id1", lez.id);

            $.ajax({
                type: "POST",
                url: context + '/OperazioniSA?type=updateLezione',
                data: fdata,
                processData: false,
                contentType: false,
                success: function (data) {
                    closeSwal();
                    var json = JSON.parse(data);
                    if (json.result) {
                        swalSuccessReload("Lezione Aggiornata", "Lezione aggiornata con successo.");
                    } else {
                        swalError("Errore", json.message);
                    }
                },
                error: function () {
                    swalError("Errore", "Non è stato possibile aggiornare la lezione");
                }
            });
        } else {
            swal.close();
        }
    });
}

function changeLezioneDouble(idlezione1, idlezione2, l) {
    let lez = mapLezioni.get(idlezione1);
    let lez2 = mapLezioni.get(idlezione2);

    let days = setRangeDatesDay(new Date(lez.giorno), l);

    swal.fire({
        title: 'Visualizza/Modifica Lezione ' + l + '<br>(Unità didattiche ' + lez.codice_ud + ' e ' + lez2.codice_ud + ')',
        html: getHtml("swalLezioneCalendarioDouble", context),
        animation: false,
        showCancelButton: true,
        width: '75%',
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        customClass: {
            popup: 'animated bounceInUp'
        },
        onOpen: function () {
            $('input.time').timepicker({
                showMeridian: false,
                interval: 5,
                showInputs: false,
                snapToStep: true,
                icons: {
                    up: 'la la-angle-up',
                    down: 'la la-angle-down'
                }
            });

            var arrows = {
                leftArrow: '<i class="la la-angle-left"></i>',
                rightArrow: '<i class="la la-angle-right"></i>'
            }

            $('#giorno').datepicker({
                orientation: "bottom left",
                todayHighlight: true,
                templates: arrows,
                autoclose: true,
                format: 'dd/mm/yyyy',
                endDate: days[1],
                startDate: days[0],
                daysOfWeekHighlighted: "0",
                daysOfWeekDisabled: [0]
            });
            $("#tot_hh1").html('Totale ore di lezione da effettuare per ' + lez.codice_ud + ': <b>' + lez.lezione_calendario.ore + '</b>');
            $("#tot_hh2").html('Totale ore di lezione da effettuare per ' + lez2.codice_ud + ': <b>' + lez2.lezione_calendario.ore + '</b>');
            $("#docente1_msg").html('Docente ' + lez.codice_ud);
            $("#docente2_msg").html('Docente ' + lez2.codice_ud);
            $("#orari1_msg").html('Orari di inizio e fine ' + lez.codice_ud);
            $("#orari2_msg").html('Orari di inizio e fine ' + lez2.codice_ud);

            $('#orario1_start').val(lez.orainizio);
            $('#orario1_end').val(lez.orafine);
            $('#orario2_start').val(lez2.orainizio);
            $('#orario2_end').val(lez2.orafine);
            $('#orario1_start').timepicker("setTime", lez.orainizio);
            $('#orario1_end').timepicker("setTime", lez.orafine);
            $('#orario2_start').timepicker("setTime", lez2.orainizio);
            $('#orario2_end').timepicker("setTime", lez2.orafine);
            $('#giorno').datepicker("setDate", formattedDate(days[2]));
            $("#giorno").datepicker("update");
            $.get(context + "/QuerySA?type=getDocentiByPrg&idprogetto=" + lez.idprogettoformativo, function (resp) {
                var json = JSON.parse(resp);
                for (var i = 0; i < json.length; i++) {
                    if (lez.docente.id === json[i].id) {
                        $("#docente1").append('<option selected value="' + json[i].id + '">' + json[i].cognome + " " + json[i].nome + '</option>');
                    } else {
                        $("#docente1").append('<option value="' + json[i].id + '">' + json[i].cognome + " " + json[i].nome + '</option>');
                    }
                    if (lez2.docente.id === json[i].id) {
                        $("#docente2").append('<option selected value="' + json[i].id + '">' + json[i].cognome + " " + json[i].nome + '</option>');
                    } else {
                        $("#docente2").append('<option value="' + json[i].id + '">' + json[i].cognome + " " + json[i].nome + '</option>');
                    }
                }
            });
            $('#docente1').select2({
                dropdownCssClass: "select2-on-top",
                minimumResultsForSearch: -1,
            });
            $('#docente2').select2({
                dropdownCssClass: "select2-on-top",
                minimumResultsForSearch: -1,
            });

            $('#orario1_start').change(function (e) {
                $('#orario1_start').val(checktime($('#orario1_start').val(), '8:00', $('#orario1_end').val()));
            });
            $('#orario1_end').change(function (e) {
                $('#orario1_end').val(checktime($('#orario1_end').val(), $('#orario1_start').val(), $('#orario2_start').val()));
            });
            $('#orario2_start').change(function (e) {
                $('#orario2_start').val(checktime($('#orario2_start').val(), $('#orario1_end').val(), $('#orario2_end').val()));
            });
            $('#orario2_end').change(function (e) {
                $('#orario2_end').val(checktime($('#orario2_end').val(), $('#orario2_start').val(), '21:00'));
            });
        },
        preConfirm: function () {
            var err = false;
            err = checkObblFieldsContent($('#swalLezioneCalendarioSingle')) ? true : err;
            err = !check_limit_hh($('#orario1_start').val(), $('#orario1_end').val(), lez.lezione_calendario.ore, lez.codice_ud, false) ? true : err;
            err = !check_limit_hh($('#orario2_start').val(), $('#orario2_end').val(), lez2.lezione_calendario.ore, lez2.codice_ud, true) ? true : err;
            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "giorno": $('#giorno').val(),
                        "docente1": $('#docente1').val(),
                        "docente2": $('#docente2').val(),
                        "orario1_start": $('#orario1_start').val(),
                        "orario1_end": $('#orario1_end').val(),
                        "orario2_start": $('#orario2_start').val(),
                        "orario2_end": $('#orario2_end').val(),
                    });
                });
            } else {
                return false;
            }
        },
    }).then((result) => {
        if (result.value) {
            showLoad();
            var fdata = new FormData();
            fdata.append("giorno", result.value.giorno);
            fdata.append("docente1", result.value.docente1);
            fdata.append("docente2", result.value.docente2);
            fdata.append("orario1_start", result.value.orario1_start);
            fdata.append("orario1_end", result.value.orario1_end);
            fdata.append("orario2_start", result.value.orario2_start);
            fdata.append("orario2_end", result.value.orario2_end);
            fdata.append("tipo_modello", "m3_double");
            fdata.append("id1", lez.id);
            fdata.append("id2", lez2.id);

            $.ajax({
                type: "POST",
                url: context + '/OperazioniSA?type=updateLezione',
                data: fdata,
                processData: false,
                contentType: false,
                success: function (data) {
                    closeSwal();
                    var json = JSON.parse(data);
                    if (json.result) {
                        swalSuccessReload("Lezione Aggiornata", "Lezione aggiornata con successo.");
                    } else {
                        swalError("Errore", json.message);
                    }
                },
                error: function () {
                    swalError("Errore", "Non è stato possibile aggiornare la lezione");
                }
            });
        } else {
            swal.close();
        }
    });
}

function uploadLezione(idprogetto, idm, idl) {
    let t = mapCalendario.get(idl);
    let days = setRangeDatesDay(null, idl);
//    if (firstLesson) {
//        days[1] = days[0];
//    }

    let orario_default_start = '9:00';
    let orario_default_end = sumHHMM(orario_default_start, doubletoHHmm(t.ore1 + t.ore2));
    swal.fire({
        title: 'Carica Lezione ' + t.lezione + '<br>(Unità didattica ' + t.ud1 + ')',
        html: getHtml("swalLezioneCalendarioSingle", context),
        animation: false,
        showCancelButton: true,
        width: '50%',
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        customClass: {
            popup: 'animated bounceInUp'
        },
        onOpen: function () {
            $('input.time').timepicker({
                showMeridian: false,
                interval: 5,
                showInputs: false,
                snapToStep: true,
                icons: {
                    up: 'la la-angle-up',
                    down: 'la la-angle-down'
                }
            });

            var arrows = {
                leftArrow: '<i class="la la-angle-left"></i>',
                rightArrow: '<i class="la la-angle-right"></i>'
            }

            $('#giorno').datepicker({
                orientation: "bottom left",
                todayHighlight: false,
                templates: arrows,
                autoclose: true,
                format: 'dd/mm/yyyy',
                endDate: days[1],
                startDate: days[0],
                daysOfWeekHighlighted: "0",
                daysOfWeekDisabled: [0]
            });
            $('#orario1_start').val(orario_default_start);
            $('#orario1_end').val(orario_default_end);
            $('#orario1_start').timepicker("setTime", orario_default_start);
            $('#orario1_end').timepicker("setTime", orario_default_end);

            $('#giorno').val(formattedDate(days[2]));
            $("#giorno").datepicker("update");
            $("#tot_hh").html('Totale ore di lezione da effettuare per: <b>' + (t.ore1 + t.ore2) + '</b>');
            $('#docente').select2({
                dropdownCssClass: "select2-on-top",
                minimumResultsForSearch: -1,
            });
            $.get(context + "/QuerySA?type=getDocentiByPrg&idprogetto=" + idprogetto, function (resp) {
                var json = JSON.parse(resp);
                for (var i = 0; i < json.length; i++) {
                    $("#docente").append('<option value="' + json[i].id + '">' + json[i].cognome + " " + json[i].nome + '</option>');
                }
            });

            $('#orario1_start').change(function (e) {
                $('#orario1_start').val(checktime($('#orario1_start').val(), '8:00', $('#orario1_end').val()));
            });
            $('#orario1_end').change(function (e) {
                $('#orario1_end').val(checktime($('#orario1_end').val(), $('#orario1_start').val(), '21:00'));
            });
        },
        preConfirm: function () {
            var err = false;
            err = checkObblFieldsContent($('#swalLezioneCalendarioSingle')) ? true : err;
            err = !check_limit_hh($('#orario1_start').val(), $('#orario1_end').val(), t.ore1 + t.ore2, t.ud1, false) ? true : err;

            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "giorno": $('#giorno').val(),
                        "docente": $('#docente').val(),
                        "orario1_start": $('#orario1_start').val(),
                        "orario1_end": $('#orario1_end').val(),
                    });
                });
            } else {
                return false;
            }
        },
    }).then((result) => {
        if (result.value) {
            showLoad();
            var fdata = new FormData();
            fdata.append("tipo_modello", "m3_single");
            fdata.append("giorno", result.value.giorno);
            fdata.append("docente", result.value.docente);
            fdata.append("orario1_start", result.value.orario1_start);
            fdata.append("orario1_end", result.value.orario1_end);
            fdata.append("id_calendariolezione", t.id);
            fdata.append("id_modello", idm);
            $.ajax({
                type: "POST",
                url: context + '/OperazioniSA?type=uploadLezione',
                data: fdata,
                processData: false,
                contentType: false,
                success: function (data) {
                    closeSwal();
                    var json = JSON.parse(data);
                    if (json.result) {
                        swalSuccessReload("Lezione Caricata", json.message);
                    } else {
                        swalError("Errore", json.message);
                    }
                },
                error: function () {
                    swalError("Errore", "Non è stato possibile caricare la lezione");
                }
            });
        } else {
            swal.close();
        }
    });
}

function uploadLezioneDouble(idprogetto, idm, idl) {
    let t = calendarioModello3.find(({lezione}) => lezione == idl);
    let ora_def_1_s = "9:00";
    let ora_def_1_f = sumHHMM(ora_def_1_s, doubletoHHmm(t.ore1));
    let ora_def_2_s = sumHHMM(ora_def_1_f, "1:00");
    let ora_def_2_f = sumHHMM(ora_def_2_s, doubletoHHmm(t.ore2));

    let days = setRangeDatesDay(null, idl);

//    if (firstLesson) {
//        days[1] = days[0];
//    }
    swal.fire({
        title: 'Carica Lezione ' + t.lezione + '<br>(Unità didattiche ' + t.ud1 + ' e ' + t.ud2 + ')',
        html: getHtml("swalLezioneCalendarioDouble", context),
        animation: false,
        showCancelButton: true,
        width: '75%',
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        customClass: {
            popup: 'animated bounceInUp'
        },
        onOpen: function () {
            $('input.time').timepicker({
                showMeridian: false,
                interval: 5,
                showInputs: false,
                snapToStep: true,
                icons: {
                    up: 'la la-angle-up',
                    down: 'la la-angle-down'
                }
            });

            var arrows = {
                leftArrow: '<i class="la la-angle-left"></i>',
                rightArrow: '<i class="la la-angle-right"></i>'
            }

            $('#giorno').datepicker({
                orientation: "bottom left",
                todayHighlight: false,
                templates: arrows,
                autoclose: true,
                format: 'dd/mm/yyyy',
                endDate: days[1],
                startDate: days[0],
                daysOfWeekHighlighted: "0",
                daysOfWeekDisabled: [0]
            });

            $('#orario1_start').val(ora_def_1_s);
            $('#orario1_end').val(ora_def_1_f);
            $('#orario2_start').val(ora_def_2_s);
            $('#orario2_end').val(ora_def_2_f);
            $('#orario1_start').timepicker("setTime", ora_def_1_s);
            $('#orario1_end').timepicker("setTime", ora_def_1_f);
            $('#orario2_start').timepicker("setTime", ora_def_2_s);
            $('#orario2_end').timepicker("setTime", ora_def_2_f);
            $('#giorno').val(formattedDate(days[2]));
            $('#giorno').datepicker("update");
            $("#tot_hh1").html('Totale ore di lezione da effettuare per ' + t.ud1 + ': <b>' + t.ore1 + '</b>');
            $("#tot_hh2").html('Totale ore di lezione da effettuare per ' + t.ud2 + ': <b>' + t.ore2 + '</b>');
            $("#docente1_msg").html('Docente ' + t.ud1);
            $("#docente2_msg").html('Docente ' + t.ud2);
            $("#orari1_msg").html('Orari di inizio e fine ' + t.ud1);
            $("#orari2_msg").html('Orari di inizio e fine ' + t.ud2);

            $('#docente1').select2({
                dropdownCssClass: "select2-on-top",
                minimumResultsForSearch: -1,
            });
            $('#docente2').select2({
                dropdownCssClass: "select2-on-top",
                minimumResultsForSearch: -1,
            });
            $.get(context + "/QuerySA?type=getDocentiByPrg&idprogetto=" + idprogetto, function (resp) {
                var json = JSON.parse(resp);
                for (var i = 0; i < json.length; i++) {
                    $("#docente1").append('<option value="' + json[i].id + '">' + json[i].cognome + " " + json[i].nome + '</option>');
                    $("#docente2").append('<option value="' + json[i].id + '">' + json[i].cognome + " " + json[i].nome + '</option>');
                }
            });
            $('#orario1_start').change(function (e) {
                $('#orario1_start').val(checktime($('#orario1_start').val(), '8:00', $('#orario1_end').val()));
            });
            $('#orario1_end').change(function (e) {
                $('#orario1_end').val(checktime($('#orario1_end').val(), $('#orario1_start').val(), $('#orario2_start').val()));
            });
            $('#orario2_start').change(function (e) {
                $('#orario2_start').val(checktime($('#orario2_start').val(), $('#orario1_end').val(), $('#orario2_end').val()));
            });
            $('#orario2_end').change(function (e) {
                $('#orario2_end').val(checktime($('#orario2_end').val(), $('#orario2_start').val(), '21:00'));
            });
//            $('#giorno').change(function (e) {
//                checkRegistroAlievoExist(idallievo, $(this).val());
//            });
        },
        preConfirm: function () {
            var err = false;
            err = checkObblFieldsContent($('#swalLezioneCalendarioDouble')) ? true : err;
            err = !check_limit_hh($('#orario1_start').val(), $('#orario1_end').val(), t.ore1, t.ud1, false) ? true : err;
            err = !check_limit_hh($('#orario2_start').val(), $('#orario2_end').val(), t.ore2, t.ud2, true) ? true : err;
//            err = checkRegistroAlievoExist(idallievo, $("#giorno").val()) ? true : err;
            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "giorno": $('#giorno').val(),
                        "docente1": $('#docente1').val(),
                        "docente2": $('#docente2').val(),
                        "orario1_start": $('#orario1_start').val(),
                        "orario1_end": $('#orario1_end').val(),
                        "orario2_start": $('#orario2_start').val(),
                        "orario2_end": $('#orario2_end').val(),
                    });
                });
            } else {
                return false;
            }
        },
    }).then((result) => {
        if (result.value) {
            showLoad();
            var fdata = new FormData();
            fdata.append("tipo_modello", "m3_double");
            fdata.append("giorno", result.value.giorno);
            fdata.append("docente1", result.value.docente1);
            fdata.append("docente2", result.value.docente2);
            fdata.append("orario1_start", result.value.orario1_start);
            fdata.append("orario1_end", result.value.orario1_end);
            fdata.append("orario2_start", result.value.orario2_start);
            fdata.append("orario2_end", result.value.orario2_end);
            fdata.append("id_calendariolezione1", t.id_cal1);
            fdata.append("id_calendariolezione2", t.id_cal2);
            fdata.append("id_modello", idm);
            $.ajax({
                type: "POST",
                url: context + '/OperazioniSA?type=uploadLezione',
                data: fdata,
                processData: false,
                contentType: false,
                success: function (data) {
                    closeSwal();
                    var json = JSON.parse(data);
                    if (json.result) {
                        swalSuccessReload("Lezione Caricata", json.message);
                    } else {
                        swalError("Errore", json.message);
                    }
                },
                error: function () {
                    swalError("Errore", "Non è stato possibile caricare la lezione");
                }
            });
        } else {
            swal.close();
        }
    });
}


function buttonsControl(lezioni, calendario) {
    lezioni = lezioni < calendario ? lezioni + 1 : lezioni;
    for (let i = 1; i <= lezioni; i++) {
        $('#a_lez' + i).removeClass("disablelink");
    }
    $('a.disablelink').removeAttr("onclick");
    cssPage();
    if (isEditable) {
        $('#revert').removeAttr('disabled');
    } else {
        $('#revert').attr('disabled', 'disabled');
    }
    if (maplezioniD.size > 0) {
        $('#deleteBy').removeAttr('disabled');
        $('#deleteAll').removeAttr('disabled');
    } else {
        $('#deleteBy').attr('disabled', 'disabled');
        $('#deleteAll').attr('disabled', 'disabled');
    }
}

function loadLezioni() {
    let temp = "";
    $.ajax({
        type: "GET",
        async: false,
        url: context + "/QuerySA?type=getLezioniByProgetto&idmodello=" + $('#m3Id').val(),
        success: function (resp) {
            if (resp != null)
                temp = JSON.parse(resp);
            mapLezioni = new Map(temp.map(i => [i.lezione_calendario.id, i]));
            mapDateLezioni = new Map(temp.map(i => [i.lezione_calendario.lezione, i.giorno]));
            maplezioniD = new Map(filterAndGroup(temp).map(i => [i.id, moment(new Date(i.giorno)).format("DD-MM-YYYY") + " ( Lezione " + i.lezione_calendario.lezione + ")"]));
        }
    });
    return temp;
}

function filterAndGroup(options) {
    return options.reduce(function (res, option) {
        if (new Date(new Date(option.giorno).toDateString()) >= today && res.filter(e => e.giorno == option.giorno).length == 0) {
            res.push(option);
        }
        return res;
    }, []);
}

function loadCalendario() {
    let temp = "";
    $.ajax({
        type: "GET",
        async: false,
        url: context + "/QuerySA?type=getCalendarioModello&modello=3",
        success: function (resp) {
            if (resp != null)
                temp = JSON.parse(resp);
            mapCalendario = new Map(temp.map(i => [i.lezione, i]));
        }
    });
    return temp;
}

function setDateInizioFine(lez) {
    let t = Number($('#endPrg').val());
    // let t_s = Number($('#startPrg').val());
    dataEnd = new Date(t);
    try {
        dataEnd = moment(dataEnd).add(30, 'd')._d;
    } catch (error) {
        console.log(error);
    }
    //setto come data di inizio il giorno successivo a quello del giorno dell'ultima lezione (se non ci sono lezioni, da domani)
    if (lez.length > 0) {
        dataStart = new Date(Math.max.apply(Math, lez.map(function (o) {
            return o.giorno;
        })));
        dataStart.setDate(dataStart.getDate() + 1);
        //14-07-21 partenza indipendente dal giorno di inizio PRG        
//    } else if (moment(new Date(t_s)).format('YYYY-MM-DD') > moment(new Date()).format('YYYY-MM-DD')) {
//        dataStart = new Date(t_s);
    } else {
        dataStart = new Date();
        dataStart.setDate(dataStart.getDate() + 1);
    }
    firstLesson = lez.length === 0;
}

function countLezioneEffettive(l) {
    let cnt = l.reduce(function (values, v) {
        if (!values.set[v.lezione_calendario.lezione]) {
            values.set[v.lezione_calendario.lezione] = 1;
            values.count++;
        }
        return values;
    }, {set: {}, count: 0}).count;
    return cnt;
}

function check_limit_hh(hh_start, hh_end, hhmax, ud, double) {
    let err = false;
    let msg = "";
    let hhregistro;
    let split_start = hh_start.split(':');
    let split_end = hh_end.split(':');
    hhregistro = new Date('00', '00', '00', split_end[0], split_end[1]).getTime() - new Date('00', '00', '00', split_start[0], split_start[1]).getTime();
    if ((hhregistro / 3600000) > hhmax) {
        err = true;
        msg = "Attenzione, il totale delle ore di lezione ha superato le " + hhmax + " ore per <b>" + ud + "</b>.";
    } else if ((hhregistro / 3600000) < hhmax) {
        err = true;
        msg = "Attenzione, il totale delle ore di lezione è minore delle " + hhmax + " ore previste per <b>" + ud + "</b>.";
    }
    if (err) {
        if (double) {
            $('#alertmsg2').html(msg);
            $('#warning_hh2').css("display", "");
            $('input.time.second').removeClass('is-valid').addClass('is-invalid');
        } else {
            $('#alertmsg').html(msg);
            $('#warning_hh').css("display", "");
            $('input.time').removeClass('is-valid').addClass('is-invalid');
        }
    } else {
        if (double) {
            $('#alertmsg2').html("");
            $('#warning_hh2').css("display", "none");
            $('input.time.second').removeClass('is-invalid').addClass('is-valid');
        } else {
            $('#alertmsg').html("");
            $('#warning_hh').css("display", "none");

            $('input.time').removeClass('is-invalid').addClass('is-valid');
        }
    }
    return err ? false : true;
}

function cssPage() {
    let day_temp;
    let lezioneAttuale = mapDateLezioni.size + 1;
    for (let i = 1; i <= mapCalendario.size; i++) {
        if (typeof (mapDateLezioni.get(i)) !== 'undefined') {
            day_temp = new Date(new Date(mapDateLezioni.get(i)).toDateString());
            if (day_temp < today) {
                $('#icon_' + i).attr("data-original-title", "Lezione effettuata, non modificabile");
                $('#msgItem_' + i).attr("data-original-title", "Visualizza lezione");
                $('#icon_' + i).addClass("btn-primary").removeClass(["btn-success", "btn-io", "btn-io-n"]);
            } else {
                isEditable = true;
                $('#icon_' + i).attr("data-original-title", "Lezione caricata");
                $('#msgItem_' + i).attr("data-original-title", "Visualizza/Modifica lezione");
                $('#icon_' + i).addClass("btn-success").removeClass(["btn-danger", "btn-io", "btn-io-n"]);
            }
            $('#c_icon' + i).show();
            $('#q_icon' + i).hide();
        } else {
            $('#msgItem_' + i).attr("data-original-title", "Carica le lezioni in ordine sequenziale");
            $('#icon_' + i).attr("data-original-title", "In attesa lezioni precedenti");
            $('#icon_' + i).addClass("btn-io-n").removeClass(["btn-danger", "btn-io", "btn-io-success"]);
            $('#q_icon' + i).show();
            $('#c_icon' + i).hide();
        }
    }
    $('#msgItem_' + lezioneAttuale).attr("data-original-title", "Carica lezione");
    $('#icon_' + lezioneAttuale).attr("data-original-title", "Carica lezione");
    $('#icon_' + lezioneAttuale).addClass("btn-io").removeClass(["btn-danger", "btn-success", "btn-io-n"]);
    $('#q_icon' + lezioneAttuale).show();
    $('#c_icon' + lezioneAttuale).hide();
    $('#mainIcon_' + lezioneAttuale).removeClass("kt-font-io").addClass("kt-font-io-ultra");

}

function lezioniDouble(idlezione1, idlezione2, l) {
    let gg = new Date(new Date(mapDateLezioni.get(l)).toDateString());
    if (gg < today || $('#statoPrg').val() === "OK") {
        showLezioneDouble(idlezione1, idlezione2, l);
    } else {
        changeLezioneDouble(idlezione1, idlezione2, l);
    }
}

function lezioniSingle(idlezione, l) {
    let gg = new Date(new Date(mapDateLezioni.get(l)).toDateString());
    if (gg < today || $('#statoPrg').val() === "OK") {
        showLezioneSingle(idlezione, l);
    } else {
        changeLezione(idlezione, l);
    }
}

function showLezioneSingle(idlezione, l) {
    let lez = mapLezioni.get(idlezione);
    swal.fire({
        title: 'Visualizza Lezione ' + l + '<br>(Unità didattica ' + lez.codice_ud + ')',
        html: getHtml("swalShowLezioneCalendarioSingle", context),
        animation: false,
        width: '50%',
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        showCancelButton: false,
        customClass: {
            popup: 'animated bounceInUp'
        },
        onOpen: function () {
            $("#alertmsg_day").html("La modifica è disabilitata in quanto la data della lezione è antecedente ad oggi.");
            $("#warning_day").show();
            $("#tot_hh1").html('Totale ore di lezione da effettuare : <b>' + lez.lezione_calendario.ore + '</b>');
            $("#docente1_msg").html('Docente');
            $("#orari1_msg").html('Orari di inizio e fine ' + lez.codice_ud);
            $('#orario1_start').val(lez.orainizio);
            $('#orario1_end').val(lez.orafine);
            $('#giorno').val(formattedDate(new Date(lez.giorno)));
            $('#docente').val(lez.docente.nome + " " + lez.docente.cognome);
        }
    });
}

function showLezioneDouble(idlezione1, idlezione2, l) {
    let lez = mapLezioni.get(idlezione1);
    let lez2 = mapLezioni.get(idlezione2);
    swal.fire({
        title: 'Visualizza Lezione ' + l + '<br>(Unità didattiche ' + lez.codice_ud + ' e ' + lez2.codice_ud + ')',
        html: getHtml("swalShowLezioneCalendarioDouble", context),
        animation: false,
        width: '75%',
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        showCancelButton: false,
        customClass: {
            popup: 'animated bounceInUp'
        },
        onOpen: function () {
            $("#alertmsg_day").html("La modifica è disabilitata in quanto la data della lezione è antecedente ad oggi.");
            $("#warning_day").show();
            $("#tot_hh1").html('Totale ore di lezione da effettuare per ' + lez.codice_ud + ': <b>' + lez.lezione_calendario.ore + '</b>');
            $("#tot_hh2").html('Totale ore di lezione da effettuare per ' + lez2.codice_ud + ': <b>' + lez2.lezione_calendario.ore + '</b>');
            $("#docente1_msg").html('Docente ' + lez.codice_ud);
            $("#docente2_msg").html('Docente ' + lez2.codice_ud);
            $("#orari1_msg").html('Orari di inizio e fine ' + lez.codice_ud);
            $("#orari2_msg").html('Orari di inizio e fine ' + lez2.codice_ud);
            $('#orario1_start').val(lez.orainizio);
            $('#orario1_end').val(lez.orafine);
            $('#orario2_start').val(lez2.orainizio);
            $('#orario2_end').val(lez2.orafine);
            $('#giorno').val(formattedDate(new Date(lez.giorno)));
            $('#docente1').val(lez.docente.nome + " " + lez.docente.cognome);
            $('#docente2').val(lez2.docente.nome + " " + lez2.docente.cognome);
        }
    });
}

$('button[id=revert]').on('click', function () {
    swal.fire({
        title: '<h3 class="kt-font-io-n"><b>Modifica Calendario</b></h3><br>',
        html: "<h5>Attenzione, procedendo il progetto formativo verrà riportato allo step precente.<br></h5>",
        animation: false,
        showCancelButton: true,
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        customClass: {
            popup: 'animated bounceInUp'
        },

    }).then((result) => {
        if (result.value) {
            showLoad();
            let fdata = new FormData();
            fdata.append("pf", $('#pId').val());
            fdata.append("modello", $('#m3Id').val());
            $.ajax({
                type: "POST",
                url: context + '/OperazioniSA?type=abilitaModificaCalendarM3',
                data: fdata,
                processData: false,
                contentType: false,
                success: function (data) {
                    closeSwal();
                    var json = JSON.parse(data);
                    if (json.result) {
                        swalSuccessReload("Calendario lezioni", "Modifica calendario abilitata");
                    } else {
                        swalError("Errore", "Non è stato possibile abilitare la modifica del calendario");
                    }
                },
                error: function () {
                    swalError("Errore", "Non è stato possibile abilitare la modifica del calendario");
                }
            });
        } else {
            swal.close();
        }
    });
});

$('button[id=deleteAll]').on('click', function () {
    swal.fire({
        title: '<h3 class="kt-font-io-n"><b>Elimina lezioni</b></h3><br>',
        html: "<h5>Attenzione, procedendo tutte le lezioni non ancora effettuate verranno eliminate.<br>Sicuro di voler proseguire?</h5>",
        animation: false,
        showCancelButton: true,
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        customClass: {
            popup: 'animated bounceInUp'
        },

    }).then((result) => {
        if (result.value) {
            showLoad();
            let fdata = new FormData();
            fdata.append("pf", $('#pId').val());
            fdata.append("modello", $('#m3Id').val());
            fdata.append("tipo", "M3");
            $.ajax({
                type: "POST",
                url: context + '/OperazioniSA?type=deleteAllLessons',
                data: fdata,
                processData: false,
                contentType: false,
                success: function (data) {
                    closeSwal();
                    var json = JSON.parse(data);
                    if (json.result) {
                        swalSuccessReload("Elimina lezioni", "Eliminazione avvenuta con successo");
                    } else {
                        swalError("Errore", "Non è stato possibile procedere con l'eliminazione delle lezioni");
                    }
                },
                error: function () {
                    swalError("Errore", "Non è stato possibile procedere con l'eliminazione delle lezioni");
                }
            });
        } else {
            swal.close();
        }
    });
});

$('button[id=deleteBy]').on('click', function () {
    swal.fire({
        title: '<h3 class="kt-font-io-n"><b>Elimina lezioni</b></h3><br>',
        html: '<div id="swal_deleteLessons">Elimina lezioni a partire da:<div class="select-div" id="lezionid_div">' +
                '<select class="form-control kt-select2-general obbligatory" id="lezionid" name="lezionid"  style="width: 100%"><option value="-">Seleziona una lezione</option></select></div></div>',
        animation: false,
        showCancelButton: true,
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        customClass: {
            popup: 'animated bounceInUp'
        },
        onOpen: function () {
            $('#lezionid').select2({
                dropdownCssClass: "select2-on-top",
                minimumResultsForSearch: -1,
            });
            maplezioniD.forEach(function (value, key) {
                $("#lezionid").append('<option value="' + key + '">' + value + '</option>');
            });
        },
        preConfirm: function () {
            var err = false;
            err = checkObblFieldsContent($('#swal_deleteLessons')) ? true : err;
            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "lezione": $('#lezionid').val(),
                    });
                });
            } else {
                return false;
            }
        },
    }).then((result) => {
        if (result.value) {
            showLoad();
            let fdata = new FormData();
            fdata.append("pf", $('#pId').val());
            fdata.append("modello", $('#m3Id').val());
            fdata.append("idlezione", result.value.lezione);
            $.ajax({
                type: "POST",
                url: context + '/OperazioniSA?type=deleteByLesson',
                data: fdata,
                processData: false,
                contentType: false,
                success: function (data) {
                    closeSwal();
                    var json = JSON.parse(data);
                    if (json.result) {
                        swalSuccessReload("Elimina lezioni", "Eliminazione avvenuta con successo");
                    } else {
                        swalError("Errore", "Non è stato possibile procedere con l'eliminazione delle lezioni");
                    }
                },
                error: function () {
                    swalError("Errore", "Non è stato possibile procedere con l'eliminazione delle lezioni");
                }
            });
        } else {
            swal.close();
        }
    });
});

jQuery(document).ready(function () {
    lezioniModello3 = loadLezioni();
    calendarioModello3 = loadCalendario();
    setDateInizioFine(lezioniModello3);
    buttonsControl(countLezioneEffettive(lezioniModello3), mapCalendario.size);
});