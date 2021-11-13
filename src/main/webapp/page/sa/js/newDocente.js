/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


var context = document.getElementById("newDocente").getAttribute("data-context");


function checkObblFieldsContentVisible(content) {
    var err = false;
    content.find('input.obbligatory:visible').each(function () {
        if ($(this).val() === '') {
            err = true;
            $(this).removeClass("is-valid").addClass("is-invalid");
        } else {
            $(this).removeClass("is-invalid").addClass("is-valid");
        }
    });
    content.find('textarea.obbligatory:visible').each(function () {
        if ($(this).val() === '') {
            err = true;
            $(this).removeClass("is-valid").addClass("is-invalid");
        } else {
            $(this).removeClass("is-invalid").addClass("is-valid");
        }
    });
    content.find('select.obbligatory:visible').each(function () {
        if ($(this).val() === '' || $(this).val() === '-') {
            err = true;
            $('#' + this.id + '_div').removeClass("is-valid-select").addClass("is-invalid-select");
        } else {
            $('#' + this.id + '_div').removeClass("is-invalid-select").addClass("is-valid-select");
        }
    });
    return err;
}

function ctrlFormNF() {
    var err = false;
    err = checkObblFieldsContentVisible($('#kt_form')) ? true : err;
    if (checkCF($('#cf'))) {
        err = checkinfoCF_simplified() ? true : err;
        err = Docente_CheckCF() ? true : err;
    } else {
        err = true;
    }
    if (!checkEmail($('#email'))) {
        err = Docente_CheckEmail() ? true : err;
    } else {
        err = true;
    }

    if (checkEmail($('#pecmail'))) {
        err = true;
    }

    return err ? false : true;
}

function ctrlForm() {
    var err = false;
    err = checkObblFieldsContentVisible($('#kt_form')) ? true : err;
    err = !checkRequiredFileContent($('#kt_form')) ? true : err;

    if (checkCF($('#cf'))) {
        err = checkinfoCF_simplified() ? true : err;
        err = Docente_CheckCF() ? true : err;
    } else {
        err = true;
    }
    if (!checkEmail($('#email'))) {
        err = Docente_CheckEmail() ? true : err;
    } else {
        err = true;
    }

    if (checkEmail($('#pecmail'))) {
        err = true;
    }

    return err ? false : true;
}

$('#email').on("change", function () {
    if (!checkEmail($('#email'))) {
        Docente_CheckEmail();
    }
});

$('#pecmail').on("change", function () {
    if (checkEmail($('#pecmail'))) {
        $('#pecmail').attr("class", "form-control is-invalid");
    } else {
        $('#pecmail').attr("class", "form-control is-valid");
    }
});

$('#cf').on("change", function () {
    if (checkCF($('#cf'))) {
        Docente_CheckCF();
    }
});

function Docente_CheckCF() {
    var err;
    $.ajax({
        type: "GET",
        async: false,
        url: context + '/OperazioniSA?type=checkCF_Docente&cf=' + $('#cf').val(),
        success: function (data) {
            if (data !== null && data !== 'null') {
                swal.fire({
                    "title": 'Errore',
                    "html": "<h3>Codice fiscale già presente</h3>",
                    "type": "error",
                    cancelButtonClass: "btn btn-io-n"
                });
                fastSwal("Attenzione!", "Codice fiscale già presente", "wobble");
                $('#cf').attr("class", "form-control is-invalid");
                err = true;
            } else {
                $('#cf').attr("class", "form-control is-valid");
                err = false;
            }
        }
    });
    return err;
}

function Docente_CheckEmail() {
    var err;
    $.ajax({
        type: "GET",
        async: false,
        url: context + '/OperazioniSA?type=checkEmail_Docente&email=' + $('#email').val(),
        success: function (data) {
            if (data !== null && data !== 'null') {
                swal.fire({
                    "title": 'Errore',
                    "html": "<h3>Email già presente</h3>",
                    "type": "error",
                    cancelButtonClass: "btn btn-io-n"
                });
                fastSwal("Attenzione!", "Email già presente", "wobble");
                $('#email').attr("class", "form-control is-invalid");
                err = true;
            } else {
                $('#email').attr("class", "form-control is-valid");
                err = false;
            }
        }
    });
    return err;
}

function resetInput() {
    $('.form-control').val('');
    $('.form-control').removeClass('is-valid');
    $('.custom-file-input').val('');
    $('.custom-file-label').html('');
    $('.custom-file-input').removeClass('is-valid');
    $('select').val('-');
    $('select').trigger('change');
    $('div.dropdown.bootstrap-select.form-control.kt-').removeClass('is-valid is-valid-select');
}

function model_funct(codice) {
    if (ctrlFormNF()) {
        document.getElementById('save').value = "1";
        document.getElementById("kt_form").target = "_blank";
        document.getElementById("kt_form").submit();
    }
}

$('#submit').on('click', function () {
    if (ctrlForm()) {
        showLoad();
        document.getElementById('save').value = '0';
        $('#kt_form').ajaxSubmit({
            error: function () {
                closeSwal();
                swalError("Errore", "Riprovare, se l'errore persiste contattare l'assistenza");
            },
            success: function (resp) {
                var json = JSON.parse(resp);
                closeSwal();
                if (json.result) {
                    resetInput();
                    swalSuccess("Docente aggiunto!", "Operazione effettuata con successo.");
                } else {
                    swalError("Errore!", json.message);
                }
            }
        });
    }
});

function checkinfoCF_simplified() {
    var cf = $('#cf').val().toUpperCase();
    var nome = $('#nome').val().replace(/[^a-zA-Z ]/g, "").toUpperCase();
    var cognome = $('#cognome').val().replace(/[^a-zA-Z ]/g, "").toUpperCase();
    var data = $('#datanascita').val();
    var checkdata = false;
    var err = false;
    var msg = "<b style='padding:10px;color: #fd397a!important;'>Attenzione i seguenti dati anagrafici non sono conformi con il codice fiscale inserito (";
    //CONTROLLO NOME ---> 1,3,4 CONSONANTI SE CE NE SONO ALMENO 4, ALTRIMENTI AGGIUNGO VOCALI NEL LORO ORDINE 
    if (!check_nome_CF(nome, cf.substring(3, 6))) {
        msg += "Nome";
        $('#nome').removeClass("is-valid").addClass("is-invalid");
        err = true;
    } else {
        $('#nome').removeClass("is-invalid").addClass("is-valid");
    }
    //CONTROLLO COGNOME ---> 1,2,3 CONSONANTI SE CE NE SONO ALMENO 3, ALTRIMENTI AGGIUNGO VOCALI NEL LORO ORDINE 
    if (!check_cognome_CF(cognome, cf.substring(0, 3))) {
        msg += err ? ", Cognome" : "Cognome";
        $('#cognome').removeClass("is-valid").addClass("is-invalid");
        err = true;
    } else {
        $('#cognome').removeClass("is-invalid").addClass("is-valid");
    }
    //CONTROLLO MESE - GIORNO - ANNO
    if (!check_mese_CF(data.substring(3, 5), cf.substring(8, 9))) {
        msg += err ? ", Mese di nascita" : "Mese di nascita";
        checkdata = true;
    }
    if (!check_giorno_CF(Number(data.substring(0, 2)), Number(cf.substring(9, 11)))) {
        msg += err ? ", Giorno di nascita" : (checkdata ? ", Giorno di nascita" : "Giorno di nascita");
        checkdata = true;
    }
    if (data.substring(8) !== cf.substring(6, 8)) {
        msg += err ? ", Anno di nascita" : (checkdata ? ", Anno di nascita" : "Anno di nascita");
        checkdata = true;
    }
    if (checkdata) {
        err = true;
        $('#datanascita').removeClass("is-valid").addClass("is-invalid");
    } else {
        $('#datanascita').removeClass("is-invalid").addClass("is-valid");
    }

    if (err) {
        msg += ").</b>";
        $("#msg_cf").html(msg);
        $("#msgrow").css("display", "");
    } else {
        $("#msg_cf").html("<b style='padding:10px;color: #0abb87!important;'>Dati anagrafici coerenti con il codice fiscale inserito.</b>");
        $("#msgrow").css("display", "");
    }
    return err;
}


function setperiodo(iddate) {
    var newid = iddate.replace("da", "a");
    var currentDate = $("#" + iddate).datepicker("getDate");
    $('#' + newid).val("");
    //$('#' + newid).datepicker('remove');
    if (currentDate === null || currentDate === "") {

    } else {
        $('#' + newid).datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true,
            orientation: "bottom",
            startDate: currentDate,
            endDate: new Date()
        });
    }
}

function fieldOnlyNumber(fieldid) {
    var stringToReplace = document.getElementById(fieldid).value;
    stringToReplace = stringToReplace.replace(/\D/g, '');
    document.getElementById(fieldid).value = stringToReplace;
}

function showAttivita() {
    let at_visibili = $('div[id^=docattivita_]:visible').length + 1;
    showAndObbl(at_visibili);
    delAttivitaButtons();
    checkLimitAttivita();
}

function checkLimitAttivita() {
    let temp = $('div[id^=docattivita_]').length - $('div[id^=docattivita_]:visible').length;
    if (temp === 0) {
        $('#add_attivita').addClass('disablelink');
        $('#add_attivita').removeAttr("onclick");
//        $('#warning').show();
    } else {
        delAttivitaButtons();
        $('#add_attivita').removeClass('disablelink');
        $('#add_attivita').attr('onClick', 'showAttivita();');
//        $('#warning').hide();
    }
}

function delAttivitaButtons() {
    let v = $('div[id^=docattivita_]:visible').length;
    if (v > 1) {
        $('a[id^=delAttivita_]').hide();
        $('#delAttivita_' + v).show();
    }
}

function delAttivita(i) {
    $("#docattivita_" + i).hide();
    $('#docattivita_' + i).find('input').removeClass('obbligatory').removeClass("is-invalid").removeClass("is-valid");
    $('#docattivita_' + i).find('select').removeClass('obbligatory');
    $('#docattivita_' + i).find('div[id*=' + i + '_div]').removeClass("is-valid-select").removeClass("is-invalid-select");
    $("label[id*=_" + i + "_obl").hide();
    $("#attivita_vis_" + i).val('0');
    delAttivitaButtons();
    checkLimitAttivita();
}

function showAndObbl(i) {
    $("#docattivita_" + i).show();
    $('#docattivita_' + i).find('input').addClass('obbligatory');
    $('#docattivita_' + i).find('select').addClass('obbligatory');
    $("label[id*=_" + i + "_obl").show();
    $("#attivita_vis_" + i).val('1');
}

jQuery(document).ready(function () {
    $('.datepicker-custom').datepicker({
        format: 'dd/mm/yyyy',
        keyboardNavigation: false,
        forceParse: false,
        calendarWeeks: false,
        autoclose: true,
        todayHighlight: true,
        endDate: new Date()
    });

    $("label[id*=_obl").hide();
    showAndObbl(1);
    checkLimitAttivita();
});



$('input[id^=data_inizio_]').on("changeDate", function (e) {
    let fine = this.id.replace("inizio", "fine");
    $('#' + fine).datepicker('setStartDate', e.date);
});
$('input[id^=data_fine_]').on("changeDate", function (e) {
    let inizio = this.id.replace("fine", "inizio");
    $('#' + inizio).datepicker('setEndDate', e.date);
});