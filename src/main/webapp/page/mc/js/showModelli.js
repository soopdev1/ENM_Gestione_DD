/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


var context = document.getElementById("showModelli").getAttribute("data-context");
$.getScript(context + '/page/partialView/partialView.js', function () {});

let lezioniModello3;
let lezioniModello4;
let mapLezioniModello3 = new Map();
let mapLezioniModello4 = new Map();

let allievi_total;
let numberGroups = new Set();
let msg_neet_excluded = "I seguenti allievi sono stati esclusi durante la creazione dei gruppi:<br>";


function loadLezioniM3() {
    let temp = "";
    $.ajax({
        type: "GET",
        async: false,
        url: context + "/QueryMicro?type=getLezioniByProgetto&idmodello=" + $('#m3Id').val(),
        success: function (resp) {
            if (resp !== null)
                temp = JSON.parse(resp);
            mapLezioniModello3 = new Map(temp.map(i => [i.lezione_calendario.id, i]));
        }
    });
    return temp;
}

function loadLezioniM4() {
    let temp = "";
    $.ajax({
        type: "GET",
        async: false,
        url: context + "/QueryMicro?type=getLezioniByProgetto&idmodello=" + $('#m4Id').val(),
        success: function (resp) {
            if (resp !== null)
                temp = JSON.parse(resp);
            mapLezioniModello4 = new Map(temp.map(i => [i.lezione_calendario.id + "_" + i.gruppo_faseB, i]));
        }
    });
    return temp;
}

function showLezioneSingleM3(idlezione, l) {
    let lez = mapLezioniModello3.get(idlezione);
    swal.fire({
        title: 'Visualizza Lezione ' + l + '<br>(Unità didattica ' + lez.codice_ud + ')',
        html: getHtml("swalShowLezioneCalendarioSingle", context),
        animation: false,
        width:'50%',
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        showCancelButton: false,
        customClass: {
            popup: 'animated bounceInUp'
        },
        onOpen: function () {
            $("#tot_hh1").html('Totale ore di lezione da effettuare: <b>' + lez.lezione_calendario.ore + '</b>');
            $("#docente1_msg").html('Docente');
            $("#orari1_msg").html('Orari di inizio e fine ' + lez.codice_ud);
            $('#orario1_start').val(lez.orainizio);
            $('#orario1_end').val((lez.orafine));
            $('#giorno').val(formattedDate(new Date(lez.giorno)));
            $('#docente').val(lez.docente.nome + " " + lez.docente.cognome);
        }
    });
}

function showLezioneDoubleM3(idlezione1, idlezione2, l) {
    let lez = mapLezioniModello3.get(idlezione1);
    let lez2 = mapLezioniModello3.get(idlezione2);
    swal.fire({
        title: 'Visualizza Lezione ' + l + '<br>(Unità didattiche ' + lez.codice_ud + ' e ' + lez2.codice_ud + ')',
        html: getHtml("swalShowLezioneCalendarioDouble", context),
        animation: false,
        width:'75%',
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        showCancelButton: false,
        customClass: {
            popup: 'animated bounceInUp'
        },
        onOpen: function () {
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

function showLezioneSingleM4(idlezione, l, grp) {
    let lez = mapLezioniModello4.get(idlezione + '_' + grp);
    swal.fire({
        title: 'Visualizza Lezione ' + l + ' - Gruppo ' + grp + '<br>(Unità didattica ' + lez.codice_ud + ')',
        html: getHtml("swalShowLezioneCalendarioSingle", context),
        animation: false,
        width:'50%',
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        showCancelButton: false,
        customClass: {
            popup: 'animated bounceInUp'
        },
        onOpen: function () {
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

function showLezioneDoubleM4(idlezione1, idlezione2, l, grp) {
    let lez = mapLezioniModello4.get(idlezione1 + '_' + grp);
    let lez2 = mapLezioniModello4.get(idlezione2 + '_' + grp);
    swal.fire({
        title: 'Visualizza Lezione ' + l + ' - Gruppo ' + grp + '<br>(Unità didattiche ' + lez.codice_ud + ' e ' + lez2.codice_ud + ')',
        html: getHtml("swalShowLezioneCalendarioDouble", context),
        animation: false,
        width:'75%',
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        showCancelButton: false,
        customClass: {
            popup: 'animated bounceInUp'
        },
        onOpen: function () {
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

function setMultiselect() {
    $('.kt-select2').select2({
        placeholder: "Seleziona Allievi",
        maximumSelectionLength: 3,
        language: {
            maximumSelected: function (e) {
                return "Puoi selezionare massimo " + e.maximum + " allievi per gruppo";
            },
            noResults: function () {
                return "Nessun Allievo assegnabile";
            }
        }
    });
}

function loadGroups() {
    allievi_total = getNeets("si");
    let excludedPresent = false;
    for (let a of allievi_total) {
        if (a[0] !== 0) {
            numberGroups.add(a[0]);
            $('#param_' + a[0]).append('<option selected="selected">' + a[1] + '</option>');
        } else {
            excludedPresent = true;
            msg_neet_excluded += '<b>' + a[1] + '</b><br>';
        }
    }
    if (excludedPresent) {
        $("#neet_excluded").attr('data-content', msg_neet_excluded);
        $("#neet_excluded").show();
    }
    numberGroups.delete("0");
}

function getNeets(load) {
    let temp = "";
    $.ajax({
        type: "GET",
        async: false,
        url: context + "/QueryMicro?type=getAllieviByProgetto&id=" + $('#pId').val() + "&load=" + load,
        success: function (resp) {
            if (resp !== null)
                temp = JSON.parse(resp);
        }
    });
    return temp;
}

function disableOptions() {
    $('#lezioni_m4').show();
    setTimeout(function () {
        $('span.select2-selection__choice__remove').remove();
    }, 500);
}

function percentage(percent, total) {
    return ((percent / total) * 100).toFixed(1);
}

jQuery(document).ready(function () {
    setMultiselect();

    lezioniModello3 = loadLezioniM3();

    let progressM3 = percentage($("a.lezioni").length, Number($("#m3Total").val()));
    $('#progressM3').css('width', progressM3 + '%').attr('aria-valuenow', progressM3);
    $("#progressM3").attr('data-original-title', 'Caricamento lezioni modello 3: ' + progressM3 + '%');

    if ($('#m4Stato').val() !== null && $('#m4Stato').val() !== "S") {
        lezioniModello4 = loadLezioniM4();
        loadGroups();
        disableOptions();
        let progressM4 = percentage($("a.gruppi").length, Number($("#m4Total").val()));
        $('#progressM4').css('width', progressM4 + '%').attr('aria-valuenow', progressM4);
        $("#progressM4").attr('data-original-title', 'Caricamento lezioni modello 4: ' + progressM4 + '%');
    }
});