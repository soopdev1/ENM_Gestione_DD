/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


var context = document.getElementById("modello4").getAttribute("data-context");
$.getScript(context + '/page/partialView/partialView.js', function () {});

let calendarioModello4;
let lezioniModello4;
let dataStart;
let dataEnd;
let today = new Date(new Date().toDateString());
let mapLezioni = new Map();
let mapCalendario = new Map();
let mapDateLezioni = new Map();
let sunday;
let isEditable = false;
let lessonsEditable = false;
let maplezioniD = new Map();
let tempLezioni = "";


let allievi_total;
let numberGroups = new Set();
let msg_neet_excluded = "I seguenti NEET sono stati esclusi durante la creazione dei gruppi:<br>";

function setRangeDatesDay(giornoLezione, lezione, gruppo) {
    setDateInizioFine(lezioniModello4.filter(i => i.gruppo_faseB === gruppo));

    let rangeDate_min = dataStart;
    let rangeDate_max = dataEnd;
    let days;
    let prevLez = (lezione - 1) + '_' + gruppo;
    let nextLez = (lezione + 1) + '_' + gruppo;
    if (typeof (mapDateLezioni.get(prevLez)) !== 'undefined') {
        days = 1;
        if (moment(new Date(mapDateLezioni.get(prevLez))).format('YYYY-MM-DD') <= moment(new Date()).format('YYYY-MM-DD')) {
            days++;
        }
        rangeDate_min = moment(new Date(mapDateLezioni.get(prevLez))).add(days, 'd')._d;
    } else if (giornoLezione !== null && rangeDate_min > giornoLezione) {
        rangeDate_min = giornoLezione;
    }
    sunday = rangeDate_min.getDay() === 0 ? 1 : 0;//se è domenica vado al giorno successivo;
    rangeDate_min = moment(rangeDate_min).add(sunday, 'd')._d;

    if (typeof (mapDateLezioni.get(nextLez)) !== 'undefined') {
        rangeDate_max = moment(new Date(mapDateLezioni.get(nextLez))).subtract(1, 'd')._d;
    }
    sunday = rangeDate_max.getDay() === 0 ? 1 : 0;//se è domenica, vado al giorno precedente;
    rangeDate_max = moment(rangeDate_max).subtract(sunday, 'd')._d;

    /*Controllo dell'orario, per lezioni a distanza di un giorno e superate le 12 aggiungo +2 lavorativi*/
    if (checkDateAndHour(rangeDate_min)) {
        rangeDate_min = moment(rangeDate_min).add(1, 'd')._d;
        if (rangeDate_min.getDay() === 0) {
            //se è domenica, aggiungo un ulteriore giorno
            rangeDate_min = moment(rangeDate_min).add(1, 'd')._d;
        }
    }

    giornoLezione = giornoLezione === null ? rangeDate_min : giornoLezione;
    return [rangeDate_min, rangeDate_max, giornoLezione];
}

function changeLezione(idlezione, l, grp) {
    let lez = mapLezioni.get(idlezione + '_' + grp);
    let days = setRangeDatesDay(new Date(lez.giorno), l, grp);
    swal.fire({
        title: 'Visualizza/Modifica Lezione ' + l + ' - Gruppo ' + grp + '<br>(Unità didattica ' + lez.codice_ud + ')',
        html: getHtml("swalLezioneCalendarioSingle", context),
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
                daysOfWeekDisabled: [0],
            });
            $("#tot_hh").html('Totale ore di lezione da effettuare: <b>' + lez.lezione_calendario.ore + '</b>');
            $('#orario1_start').val(lez.orainizio);
            $('#orario1_start').timepicker("setTime", lez.orainizio);
            $('#orario1_end').val(lez.orafine);
            $('#orario1_end').timepicker("setTime", lez.orafine);
            $('#giorno').datepicker("setDate", days[2]);//
            $("#giorno").datepicker("update");
            $.get(context + "/QuerySA?type=getDocentiByPrg&idprogetto=" + lez.idprogettoformativo, function (resp) {
                var json = JSON.parse(resp);
                for (var i = 0; i < json.length; i++) {
                    if (lez.docente.id == json[i].id) {
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
            fdata.append("tipo_modello", "m4_single");
            fdata.append("idgruppo", grp);
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

function changeLezioneDouble(idlezione1, idlezione2, l, grp) {
    let lez = mapLezioni.get(idlezione1 + '_' + grp);
    let lez2 = mapLezioni.get(idlezione2 + '_' + grp);

    let days = setRangeDatesDay(new Date(lez.giorno), l, grp);
    swal.fire({
        title: 'Visualizza/Modifica Lezione ' + l + ' - Gruppo ' + grp + '<br>(Unità didattiche ' + lez.codice_ud + ' e ' + lez2.codice_ud + ')',
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
            };

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
            $("#tot_hh1").html('Totale ore di lezione ' + lez.codice_ud + ': <b>' + lez.lezione_calendario.ore + '</b>');
            $("#tot_hh2").html('Totale ore di lezione ' + lez2.codice_ud + ': <b>' + lez2.lezione_calendario.ore + '</b>');
            $("#docente1_msg").html('<b>' + lez.codice_ud + '</b><br>Docente:');
            $("#docente2_msg").html('<b>' + lez2.codice_ud + '</b><br>Docente:');
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
            $('#giorno').datepicker("setDate", formattedDate(new Date(days[2])));
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
                minimumResultsForSearch: -1
            });
            $('#docente2').select2({
                dropdownCssClass: "select2-on-top",
                minimumResultsForSearch: -1
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
                        "orario2_end": $('#orario2_end').val()
                    });
                });
            } else {
                return false;
            }
        }
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
            fdata.append("tipo_modello", "m4_double");
            fdata.append("idgruppo", grp);
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

function uploadLezione(idprogetto, idm, idl, grp) {
    let t = mapCalendario.get(idl);
    let days = setRangeDatesDay(null, idl, grp);

    let orario_default_start = '9:00';
    let orario_default_end = sumHHMM(orario_default_start, doubletoHHmm(t.ore1 + t.ore2));
    swal.fire({
        title: 'Carica Lezione ' + t.lezione + ' - Gruppo ' + grp + '<br>(Unità didattica ' + t.ud1 + ')',
        html: getHtml("swalLezioneCalendarioSingle", context),
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
            fdata.append("tipo_modello", "m4_single");
            fdata.append("giorno", result.value.giorno);
            fdata.append("docente", result.value.docente);
            fdata.append("orario1_start", result.value.orario1_start);
            fdata.append("orario1_end", result.value.orario1_end);
            fdata.append("id_calendariolezione", t.id);
            fdata.append("idgruppo", grp);
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

function uploadLezioneDouble(idprogetto, idm, idl, grp) {
    let t = calendarioModello4.find(({lezione}) => lezione === idl);
    let ora_def_1_s = "9:00";
    let ora_def_1_f = sumHHMM(ora_def_1_s, doubletoHHmm(t.ore1));
    let ora_def_2_s = sumHHMM(ora_def_1_f, "1:00");
    let ora_def_2_f = sumHHMM(ora_def_2_s, doubletoHHmm(t.ore2));

    let days = setRangeDatesDay(null, idl, grp);

    swal.fire({
        title: 'Carica Lezione ' + t.lezione + ' - Gruppo ' + grp + '<br>(Unità didattiche ' + t.ud1 + ' e ' + t.ud2 + ')',
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
            };

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
            $("#tot_hh1").html('Totale ore di lezione ' + t.ud1 + ':  <b> ' + t.ore1 + '</b>');
            $("#tot_hh2").html('Totale ore di lezione ' + t.ud2 + ':  <b> ' + t.ore2 + '</b>');
            $("#docente1_msg").html('<b>' + t.ud1 + '</b><br>Docente:');
            $("#docente2_msg").html('<b>' + t.ud2 + '</b><br>Docente:');
            $("#orari1_msg").html('Orari di inizio e fine ' + t.ud1);
            $("#orari2_msg").html('Orari di inizio e fine ' + t.ud2);

            $('#docente1').select2({
                dropdownCssClass: "select2-on-top",
                minimumResultsForSearch: -1
            });
            $('#docente2').select2({
                dropdownCssClass: "select2-on-top",
                minimumResultsForSearch: -1
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
                        "orario2_end": $('#orario2_end').val()
                    });
                });
            } else {
                return false;
            }
        }
    }).then((result) => {
        if (result.value) {
            showLoad();
            var fdata = new FormData();
            fdata.append("tipo_modello", "m4_double");
            fdata.append("giorno", result.value.giorno);
            fdata.append("docente1", result.value.docente1);
            fdata.append("docente2", result.value.docente2);
            fdata.append("orario1_start", result.value.orario1_start);
            fdata.append("orario1_end", result.value.orario1_end);
            fdata.append("orario2_start", result.value.orario2_start);
            fdata.append("orario2_end", result.value.orario2_end);
            fdata.append("id_calendariolezione1", t.id_cal1);
            fdata.append("id_calendariolezione2", t.id_cal2);
            fdata.append("idgruppo", grp);
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


function buttonsControl(lezioni, calendario, gruppo) {
    let nrolezioni = lezioni + 1;
    lezioni = lezioni < calendario ? lezioni + 1 : lezioni;
    for (let i = 1; i <= lezioni; i++) {
        $('#a_lez' + i + '_' + gruppo).removeClass("disablelink");
    }
    cssPage(nrolezioni, gruppo);
    maplezioniD = new Map();
    maplezioniD = new Map(filterAndGroupByG(tempLezioni, gruppo).map(i => [i.id, moment(new Date(i.giorno)).format("DD-MM-YYYY") + " ( Lezione " + i.lezione_calendario.lezione + ")"]));
    if (maplezioniD.size > 0) {
        lessonsEditable = true;
        $('#deleteByGroup_' + gruppo).removeAttr('disabled');
        $('#deleteAllGroup_' + gruppo).removeAttr('disabled');
    } else {
        $('#deleteByGroup_' + gruppo).attr('disabled', 'disabled');
        $('#deleteAllGroup_' + gruppo).attr('disabled', 'disabled');
    }
}

function loadLezioni() {
    let temp = "";
    $.ajax({
        type: "GET",
        async: false,
        url: context + "/QuerySA?type=getLezioniByProgetto&idmodello=" + $('#m4Id').val(),
        success: function (resp) {
            if (resp != null) {
                temp = JSON.parse(resp);
                mapLezioni = new Map(temp.map(i => [i.lezione_calendario.id + "_" + i.gruppo_faseB, i]));
                mapDateLezioni = new Map(temp.map(i => [i.lezione_calendario.lezione + "_" + i.gruppo_faseB, i.giorno]));
                tempLezioni = temp;
            }
        }
    });
    return temp;
}

function filterAndGroupByG(options, group) {
    return options.reduce(function (res, option) {
        if (new Date(new Date(option.giorno).toDateString()) >= today && option.gruppo_faseB == group && res.filter(e => e.giorno == option.giorno).length == 0) {
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
        url: context + "/QuerySA?type=getCalendarioModello&modello=4",
        success: function (resp) {
            if (resp != null)
                temp = JSON.parse(resp);
            mapCalendario = new Map(temp.map(i => [i.lezione, i]));
        }
    });
    return temp;
}

function setDateInizioFine(lez) {
    let t_e = Number($('#endPrg').val());
    dataEnd = new Date(t_e);
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
        dataStart = moment(dataStart).add(1, 'd')._d;
    } else {
        dataStart = moment(new Date()).add(1, 'd')._d;
        //setto la data massima che corrisponde a 45 gg dopo la data di inizio del progetto
        //let t_s = Number($('#startPrg').val());
        //dataStart = new Date(t_s);
        //dataStart = moment(new Date(t_s)).add(45, 'd')._d;
        //controllo se la data è inferiore ad oggi
        //if(new Date(dataStart.toDateString()) <= today){
        // dataStart = moment(new Date()).add(1, 'd')._d;
        //}
    }
}

function countLezioneEffettive(l, gr) {
    let filteredByGroup = l.filter(i => i.gruppo_faseB == gr);
    let cnt = filteredByGroup.reduce(function (values, v) {
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

function cssPage(nrolez, gruppo) {
    let day_temp;
    let key;
    for (let i = 1; i <= mapCalendario.size; i++) {
        key = i + '_' + gruppo;
        if (typeof (mapDateLezioni.get(key)) !== 'undefined') {
            day_temp = new Date(new Date(mapDateLezioni.get(key)).toDateString());
            if (day_temp < today) {
                $('#icon_' + key).attr("data-original-title", "Lezione effettuata, non modificabile");
                $('#msgItem_' + key).attr("data-original-title", "Visualizza lezione");
                $('#icon_' + key).addClass("btn-primary").removeClass(["btn-success", "btn-io", "btn-io-n"]);
            } else {
                isEditable = true;
                $('#icon_' + key).attr("data-original-title", "Lezione caricata");
                $('#msgItem_' + key).attr("data-original-title", "Visualizza/Modifica lezione");
                $('#icon_' + key).addClass("btn-success").removeClass(["btn-danger", "btn-io", "btn-io-n"]);
            }
            $('#c_icon' + key).show();
            $('#q_icon' + key).hide();
        } else {
            $('#msgItem_' + key).attr("data-original-title", "Carica le lezioni in ordine sequenziale");
            $('#icon_' + key).attr("data-original-title", "In attesa lezioni precedenti");
            $('#icon_' + key).addClass("btn-io-n").removeClass(["btn-danger", "btn-io", "btn-io-success"]);
            $('#q_icon' + key).show();
            $('#c_icon' + key).hide();
        }
    }
    let lezioneAttuale = nrolez + '_' + gruppo;
    $('#msgItem_' + lezioneAttuale).attr("data-original-title", "Carica lezione");
    $('#icon_' + lezioneAttuale).attr("data-original-title", "Carica lezione");
    $('#icon_' + lezioneAttuale).addClass("btn-io").removeClass(["btn-danger", "btn-success", "btn-io-n"]);
    $('#q_icon' + lezioneAttuale).show();
    $('#c_icon' + lezioneAttuale).hide();
    $('#mainIcon_' + lezioneAttuale).removeClass("kt-font-io").addClass("kt-font-io-ultra");

}

function lezioniDouble(idlezione1, idlezione2, l, grp) {
    let gg = new Date(new Date(mapDateLezioni.get(l + '_' + grp)).toDateString());
    if (gg < today) {
        showLezioneDouble(idlezione1, idlezione2, l, grp);
    } else {
        changeLezioneDouble(idlezione1, idlezione2, l, grp);
    }
}

function lezioniSingle(idlezione, l, grp) {
    let gg = new Date(new Date(mapDateLezioni.get(l + '_' + grp)).toDateString());
    if (gg < today) {
        showLezioneSingle(idlezione, l, grp);
    } else {
        changeLezione(idlezione, l, grp);
    }
}

function showLezioneSingle(idlezione, l, grp) {
    let lez = mapLezioni.get(idlezione + '_' + grp);
    swal.fire({
        title: 'Visualizza Lezione ' + l + ' - Gruppo ' + grp + '<br>(Unità didattica ' + lez.codice_ud + ')',
        html: getHtml("swalShowLezioneCalendarioSingle", context),
        animation: false,
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

function showLezioneDouble(idlezione1, idlezione2, l, grp) {
    let lez = mapLezioni.get(idlezione1 + '_' + grp);
    let lez2 = mapLezioni.get(idlezione2 + '_' + grp);
    swal.fire({
        title: 'Visualizza Lezione ' + l + ' - Gruppo ' + grp + '<br>(Unità didattiche ' + lez.codice_ud + ' e ' + lez2.codice_ud + ')',
        html: getHtml("swalShowLezioneCalendarioDouble", context),
        animation: false,
        showCancelButton: false,
        width: '75%',
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        customClass: {
            popup: 'animated bounceInUp'
        },
        onOpen: function () {
            $("#alertmsg_day").html("La modifica è disabilitata in quanto la data della lezione è antecedente ad oggi.");
            $("#warning_day").show();
            $("#tot_hh1").html('Totale ore di lezione ' + lez.codice_ud + ':  <b> ' + lez.lezione_calendario.ore + '</b>');
            $("#tot_hh2").html('Totale ore di lezione ' + lez2.codice_ud + ':  <b> ' + lez2.lezione_calendario.ore + '</b>');
            $("#docente1_msg").html('<b>' + lez.codice_ud + '</b><br>Docente:');
            $("#docente2_msg").html('<b>' + lez2.codice_ud + '</b><br>Docente:');
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

$('#createGroups').on("click", function () {
    let allievi_ok = allievi_total.filter(al => al.gruppo_faseB !== -1);
    let diff = allievi_ok.length - $("[id^=param_] :selected").length;
    let msg = diff !== 0 ? ('Attenzione, non hai selezionato <b>' + diff + '</b> NEET durante la creazione dei gruppi.<br> Se procedi, non sarà possibile una loro assegnazione secondaria.') : 'Vuoi procedere con la creazione dei gruppi?';
    swal.fire({
        title: 'Creazione Gruppi',
        html: '<h5>' + msg + '</h5>',
        animation: false,
        showCancelButton: true,
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        customClass: {
            popup: 'animated bounceInUp'
        },
        preConfirm: function () {
            if ($("[id^=param_] :selected").length > 0) {
                let customIterator = 1;
                let groups = [];
                $('[id^=param_]').each(function () {
                    if ($(this).find('option:selected').length > 0) {
                        $(this).find('option:selected').each(function () {
                            groups.push(customIterator + "_" + $(this).val());
                        });
                        customIterator++;
                    }
                });
                return new Promise(function (resolve) {
                    resolve({
                        "gruppi": groups,
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
            fdata.append("gruppi[]", result.value.gruppi);
            fdata.append("id_modello", $('#m4Id').val());
            $.ajax({
                type: "POST",
                url: context + '/OperazioniSA?type=creaGruppi',
                data: fdata,
                processData: false,
                contentType: false,
                success: function (data) {
                    closeSwal();
                    var json = JSON.parse(data);
                    if (json.result) {
                        swalSuccessReload("Gruppi modello 4", "Gruppi creati con successo.");
                    } else {
                        swalError("Errore", json.message);
                    }
                },
                error: function () {
                    swalError("Errore", "Non è stato possibile creare i gruppi.");
                }
            });
        } else {
            swal.close();
        }
    });
});

function getNeets(load) {
    let temp = "";
    $.ajax({
        type: "GET",
        async: false,
        url: context + "/QuerySA?type=getAllieviByProgetto&id=" + $('#pId').val() + "&load=" + load,
        success: function (resp) {
            if (resp != null)
                temp = JSON.parse(resp);
        }
    });
    return temp;
}

function buttonCreateGroups() {
    $('#createGroups').prop("disabled", $("[id^=param_] :selected").length === 0);
    $('#createGroups').show();
}

function disableOptions() {
    $('#lezioni_m4').show();
    setTimeout(function () {
        $('span.select2-selection__choice__remove').remove()
    }, 500);
}

function createGroups() {
    let current, data;
    allievi_total = getNeets("no");
    let allievi_ko = allievi_total.filter(al => al.gruppo_faseB == -1);
    let allievi_ok = allievi_total.filter(al => al.gruppo_faseB != -1);
    let mapAllievi = new Map(allievi_ok.map(i => [i.id, (i.nome + " " + i.cognome)]));
    msg_neet_excluded = "I seguenti NEET sono stati esclusi dalla creazione dei gruppi in quanto non hanno effettuato le 36 ore durante la Fase A:<br>";

    let excludedPresent = false;
    for (let a of allievi_ko) {
        excludedPresent = true;
        msg_neet_excluded += '<b>' + a.nome + " " + a.cognome + '</b><br>';
    }
    if (excludedPresent) {
        $("#neet_excluded").attr('data-content', msg_neet_excluded);
        $("#neet_excluded").show();
    }


    for (let [key, value] of mapAllievi) {
        $('[id^=param_]').append('<option value="' + key + '">' + value + '</option>');
    }

    $('[id^=param_]').val(['']);
    $('[id^=param_]').trigger('change');

    $('[id^=param_]').on("select2:select", function (e) {
        data = this.id.split("_")[1];
        $('[id^=param_]').each(function () {
            current = this.id.split("_")[1];
            if (data != current) {
                $('#param_' + current + ' option[value=' + e.params.data.id + ']').remove();
            }
            buttonCreateGroups();
        });
    });
    $('[id^=param_]').on('select2:unselect', function (e) {
        data = this.id.split("_")[1];
        $('[id^=param_]').each(function () {
            current = this.id.split("_")[1];
            if (data != current) {
                $("#param_" + current).append('<option value="' + e.params.data.id + '">' + mapAllievi.get(Number(e.params.data.id)) + '</option>');
            }
            buttonCreateGroups();
        });

    });
}

function loadGroups() {
    allievi_total = getNeets("si");
    let excludedPresent = false;
    msg_neet_excluded = "I seguenti NEET sono stati esclusi durante la creazione dei gruppi:<br>";
    for (let a of allievi_total) {
        if (a[0] > 0) {
            numberGroups.add(a[0]);
            $('#param_' + a[0]).append('<option selected="selected">' + a[1] + '</option>');
        } else {
            excludedPresent = true;
            if (a[0] == -1) {
                msg_neet_excluded += '<b>' + a[1] + ' (36 ore non raggiunte durante la Fase A)</b><br>';
            } else {
                msg_neet_excluded += '<b>' + a[1] + '</b><br>';
            }
        }
    }
    if (excludedPresent) {
        $("#neet_excluded").attr('data-content', msg_neet_excluded);
        $("#neet_excluded").show();
    }
    numberGroups.delete("0");
}

function setMultiselect() {
    $('.kt-select2').select2({
        placeholder: "Seleziona NEET",
        maximumSelectionLength: 3,
        language: {
            maximumSelected: function (e) {
                return "Puoi selezionare massimo " + e.maximum + " NEET per gruppo";
            },
            noResults: function () {
                return "Nessun NEET assegnabile";
            }
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
            fdata.append("modello", $('#m4Id').val());
            $.ajax({
                type: "POST",
                url: context + '/OperazioniSA?type=abilitaModificaCalendarM4',
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
            fdata.append("modello", $('#m4Id').val());
            fdata.append("tipo", "M4");
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

$('button[id^=deleteByGroup_]').on('click', function () {
    let grp = this.id.split("_")[1];
    swal.fire({
        title: '<h3 class="kt-font-io-n"><b>Elimina lezioni Gruppo ' + grp + '</b></h3><br>',
        html: '<div id="swal_deleteLessons_' + grp + '">Elimina lezioni a partire da:<div class="select-div" id="lezionid_' + grp + '_div">' +
                '<select class="form-control kt-select2-general obbligatory" id="lezionid_' + grp + '" name="lezionid_' + grp + '"  style="width: 100%"><option value="-">Seleziona una lezione</option></select></div></div>',
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
            $('#lezionid_' + grp).select2({
                dropdownCssClass: "select2-on-top",
                minimumResultsForSearch: -1,
            });
            maplezioniD = new Map(filterAndGroupByG(tempLezioni, grp).map(i => [i.id, moment(new Date(i.giorno)).format("DD-MM-YYYY") + " ( Lezione " + i.lezione_calendario.lezione + ")"]));
            maplezioniD.forEach(function (value, key) {
                $("#lezionid_" + grp).append('<option value="' + key + '">' + value + '</option>');
            });
        },
        preConfirm: function () {
            var err = false;
            err = checkObblFieldsContent($('#swal_deleteLessons_' + grp)) ? true : err;
            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "lezione": $('#lezionid_' + grp).val(),
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
            fdata.append("modello", $('#m4Id').val());
            fdata.append("idlezione", result.value.lezione);
            fdata.append("tipo", "Forward");
            fdata.append("gruppo", grp);
            $.ajax({
                type: "POST",
                url: context + '/OperazioniSA?type=deleteByGroup',
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

$('button[id^=deleteAllGroup_]').on('click', function () {
    let grp = this.id.split("_")[1];
    swal.fire({
        title: '<h3 class="kt-font-io-n"><b>Elimina lezioni Gruppo ' + grp + '</b></h3><br>',
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
            fdata.append("modello", $('#m4Id').val());
            fdata.append("tipo", "All");
            fdata.append("gruppo", grp);
            $.ajax({
                type: "POST",
                url: context + '/OperazioniSA?type=deleteByGroup',
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
    setMultiselect();
    if ($('#m4Stato').val() === "S") {
        //creazione gruppi
        createGroups();
        buttonCreateGroups();
    } else if ($('#m4Stato').val() === "L" || $('#m4Stato').val() === "R" || $('#m4Stato').val() === "OK") {
        //create groups disabled
        loadGroups();
        disableOptions();

        lezioniModello4 = loadLezioni();
        calendarioModello4 = loadCalendario();
//        setDateInizioFine(lezioniModello4);
        for (let gr of numberGroups) {
            buttonsControl(countLezioneEffettive(lezioniModello4, gr), mapCalendario.size, gr);
        }
        $('a.disablelink').removeAttr("onclick");
        if ($('#m4Stato').val() === "R") {
            $('#download_m4').prop("disabled", false);
            $('#upload_m4').prop("disabled", false);
        } else {
            $('#download_m4').removeAttr("onclick");
            $('#upload_m4').removeAttr("onclick");
        }
        if (isEditable) {
            $('#revert').removeAttr('disabled');
        } else {
            $('#revert').attr('disabled', 'disabled');
        }
        if (lessonsEditable) {
            $('#deleteAll').removeAttr('disabled');
        } else {
            $('#deleteAll').attr('disabled', 'disabled');
        }
    }
});