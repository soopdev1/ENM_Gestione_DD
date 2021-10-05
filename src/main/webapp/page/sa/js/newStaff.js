/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


var context = document.getElementById("newStaff").getAttribute("data-context");
let pId = document.currentScript.getAttribute('pId');
let nro_membri = document.currentScript.getAttribute('nro');
let membri;

function ctrlFormGeneric(fid, index) {
    var err = false;
    err = checkObblFieldsContent($(fid)) ? true : err;

    err = !checkEmail_custom($('#email' + index)) ? err : true;

    err = checkSecondMember($(fid), index) ? true : err;

    return err ? false : true;
}

function checkEmail_custom(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    if (email.val() === '' || !re.test(email.val().toLowerCase())) {
        email.removeClass("is-valid").addClass("is-invalid");
        return true;
    } else {
        email.removeClass("is-invalid").addClass("is-valid");
        return false;
    }
}


function checkSecondMember(form, index) {
    let err = false;
    //nel caso in cui è inserito solo un membro
    if (membri !== "" && membri.length === 1) {
        err = lowerNtrimmed(membri[0].nome) == lowerNtrimmed($('#nome'+index).val()) &&
              lowerNtrimmed(membri[0].cognome) == lowerNtrimmed($('#cognome'+index).val()) &&
              lowerNtrimmed(membri[0].email) == lowerNtrimmed($('#email'+index).val()) &&
              lowerNtrimmed(membri[0].telefono) == lowerNtrimmed($('#telefono'+index).val()) ? true : false;
    }else if(membri.length === 2){
        let actual = index == 1 ? 0 : 1;
        err = lowerNtrimmed(membri[actual].nome) == lowerNtrimmed($('#nome'+index).val()) &&
              lowerNtrimmed(membri[actual].cognome) == lowerNtrimmed($('#cognome'+index).val()) &&
              lowerNtrimmed(membri[actual].email) == lowerNtrimmed($('#email'+index).val()) &&
              lowerNtrimmed(membri[actual].telefono) == lowerNtrimmed($('#telefono'+index).val()) ? true : false;
    }
    
    if (err) {
        form.find('input.obbligatory').each(function () {
            $(this).removeClass("is-valid").addClass("is-invalid");
        });
        $('#sameMember'+index).show();
    } else {
        form.find('input.obbligatory').each(function () {
            $(this).removeClass("is-invalid").addClass("is-valid");
        });
        $('#sameMember'+index).hide();
    }
    return err;
}

function lowerNtrimmed(v){
    return v.toLowerCase().trim();
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

$('[id^=submit]').on('click', function () {
    let form_id = "#kt_form_" + this.id.split("_")[1];
    if (ctrlFormGeneric(form_id, this.id.split("_")[1])) {
        showLoad();
        $(form_id).ajaxSubmit({
            error: function () {
                closeSwal();
                swalError("Errore", "Riprovare, se l'errore persiste contattare l'assistenza");
            },
            success: function (resp) {
                var json = JSON.parse(resp);
                closeSwal();
                if (json.result) {
                    resetInput();
                    swalSuccessReload(json.title, json.message);
                } else {
                    swalError("Errore!", json.message);
                }
            }
        });
    }
});

function loadStaff() {
    let temp = "";
    $.ajax({
        type: "GET",
        async: false,
        url: context + "/QuerySA?type=getMembriStaff&idprogetto=" + pId,
        success: function (resp) {
            if (resp != null)
                temp = JSON.parse(resp);
        }
    });
    return temp;
}

function setObjects(m) {
    for (let i = 0; i < nro_membri; i++) {
        if (m !== "" && m[i] !== undefined) {
            $('#mid_' + i).val(m[i].id).change();
            $('#nome' + i).val(m[i].nome).change();
            $('#cognome' + i).val(m[i].cognome).change();
            $('#email' + i).val(m[i].email).change();
            $('#telefono' + i).val(m[i].telefono).change();
            $('#delete_' + i).show().change();
        } else {
            $('#mid_' + i).val("").change();
            $('#nome' + i).val("").change();
            $('#cognome' + i).val("").change();
            $('#email' + i).val("").change();
            $('#telefono' + i).val("").change();
            $('#delete_' + i).hide();
        }
    }

    if (m === "" || m.length === 0) {
        $('#nome1').attr("disabled", true);
        $('#cognome1').attr("disabled", true);
        $('#email1').attr("disabled", true);
        $('#telefono1').attr("disabled", true);
        $('#submit_1').addClass("disablelink");
    } else {
        $('#nome1').attr("disabled", false);
        $('#cognome1').attr("disabled", false);
        $('#email1').attr("disabled", false);
        $('#telefono1').attr("disabled", false);
        $('#submit_1').removeClass("disablelink");
    }
}

function deleteMembro(i) {
    let id = membri[i].id;
    swal.fire({
        title: '<h3 class="kt-font-io-n"><b>Conferma Eliminazione</b></h3><br>',
        html: "<h5 style='text-align:center;'>Sicuro di voler eliminare questo membro?</h5>",
        animation: false,
        showCancelButton: true,
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        customClass: {
            popup: 'large-swal animated bounceInUp',
        },
    }).then((result) => {
        if (result.value) {
            executeDelete(id);
        } else {
            swal.close();
        }
    });
}

function executeDelete(id) {
    showLoad();
    $.ajax({
        type: "POST",
        url: context + '/OperazioniSA?type=deleteMembroStaff&id=' + id,
        success: function (data) {
            closeSwal();
            var json = JSON.parse(data);
            if (json.result) {
                swalSuccessReload("Membro eliminato", "Operazione effettuata con successo");
            } else {
                swalError("Errore", json.message);
            }
        },
        error: function () {
            swalError("Errore", "Non è stato possibile procedere con l'operazione");
        }
    });
}

jQuery(document).ready(function () {
    membri = loadStaff();
    setObjects(membri);
//    if (membri !== "" && membri.length === 2) {
//        $('#submit_total').attr("disabled", false);
//    } else {
//        $('#submit_total').attr("disabled", true);
//    }
//    lezioniModello3 = loadLezioni();
//    calendarioModello3 = loadCalendario();
//    setDateInizioFine(lezioniModello3);
//    buttonsControl(countLezioneEffettive(lezioniModello3), mapCalendario.size);
});