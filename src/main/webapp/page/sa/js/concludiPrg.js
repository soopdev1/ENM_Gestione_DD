/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var context = document.getElementById("concludiPrg").getAttribute("data-context");
var doc_docenti = new Map();

$('select[id^=regione_]').on('change', function (e) {
    let alr = this.id.split("_")[1];
    $("#provincia_" + alr).empty();
    $("#comune_" + alr).empty();
    $("#comune_" + alr).append('<option value="-">Seleziona Comune</option>');
    if ($('#regione_' + alr).val() !== '-') {
        startBlockUILoad("#provincia_" + alr + "_div");
        $("#provincia_" + alr).append('<option value="-">Seleziona Provincia</option>');
        $.get(context + '/Login?type=getProvincia&regione=' + $('#regione_' + alr).val(), function (resp) {
            var json = JSON.parse(resp);
            for (var i = 0; i < json.length; i++) {
                $("#provincia_" + alr).append('<option value="' + json[i].value + '">' + json[i].desc + '</option>');
            }
            stopBlockUI("#provincia_" + alr + "_div");
        });
    } else {
        $("#provincia_" + alr).append('<option value="-">Seleziona Provincia</option>');
    }
});

$('select[id^=provincia_]').on('change', function (e) {
    let alp = this.id.split("_")[1];
    $("#comune_" + alp).empty();
    if ($('#provincia_' + alp).val() !== '-') {
        startBlockUILoad("#comune_" + alp + "_div");
        $("#comune_" + alp).append('<option value="-">Seleziona Comune</option>');
        $.get(context + '/Login?type=getComune&provincia=' + $('#provincia_' + alp).val(), function (resp) {
            var json = JSON.parse(resp);
            for (var i = 0; i < json.length; i++) {
                $("#comune_" + alp).append('<option value="' + json[i].value + '">' + json[i].desc + '</option>');
            }
            stopBlockUI("#comune_" + alp + "_div");
        });
    } else {
        $("#comune_" + alp).append('<option value="-">Seleziona Comune</option>');
    }
});

function deleteModello(id) {
    swal.fire({
        title: '<h3 class="kt-font-io-n"><b>Conferma Eliminazione</b></h3><br>',
        html: "<h5 style='text-align:center;'>Sicuro di voler eliminare il modello 5 dell'alunno?</h5>",
        animation: false,
        showCancelButton: true,
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        customClass: {
            popup: 'large-swal animated bounceInUp'
        }
    }).then((result) => {
        if (result.value) {
            deleteConfirmedM5(id);
        } else {
            swal.close();
        }
    });
}

function deleteConfirmedM5(id) {
    showLoad();
    $.ajax({
        type: "POST",
        url: context + '/OperazioniSA?type=deleteModello5Alunno&id=' + id,
        success: function (data) {
            closeSwal();
            var json = JSON.parse(data);
            if (json.result) {
                swalSuccessReload("Modello eliminato", "Modello eliminato con successo");
                $('#currentstep').val('');
            } else {
                swalError("Errore", json.message);
            }
        },
        error: function () {
            swalError("Errore", "Non è stato possibile eliminare il modello.");
        }
    });
}


function domAmm_check(id) {
    $('#doc_' + id).removeClass("is-invalid");
    $('#doc_' + id).removeClass("is-valid");
    if ($('#domanda_a_' + id).is(":checked")) {
        $('#doc_' + id).removeAttr('disabled');
        $('#doc_' + id).attr('tipo', 'obbligatory');
        $('#cont_daok_' + id).toggle(true);
        $('#file_daok_' + id).toggle(true);
    } else {
        $('#doc_' + id).removeAttr('tipo');
        $('#doc_' + id).attr('disabled', 'disabled');
        $('#cont_daok_' + id).toggle(false);
        $('#file_daok_' + id).toggle(false);
    }
}

function disableRadioGroup(nome, val) {
    if (val) {
        $('label.' + nome).removeClass("kt-radio--disabled");
        $('input.' + nome).removeAttr('disabled');
    } else {
        $('label.' + nome).addClass("kt-radio--disabled");
        $('input.' + nome).attr('disabled', 'disabled');
    }
}

function disableBandi(nome, val) {
    if (val) {
        $('label.' + nome).removeClass("kt-radio--disabled");
        $('input[name=' + nome + ']').removeAttr('disabled');
    } else {
        $('label.' + nome).addClass("kt-radio--disabled");
        $('input[name=' + nome + ']').attr('disabled', 'disabled');
    }
}

function initRadioButtons() {
    $('input[type=radio]').each(function () {
        $(this).prop('checked', false);
        this.checked = false;
    });
}

$('input[type=radio][name^=bando]').click(function () {
    if (this.previous) {
        this.checked = false;
    }
    disableRadioGroup(this.name, this.checked);
    this.previous = this.checked;

});

//$('input[type=button][id^=loadM5_]').click(function () {
//
//});

$('input[type=radio][name^=sud_option_]').click(function () {
    if (this.previous) {
        this.checked = false;
    }
    this.previous = this.checked;

});

$('input[type=radio][class^=bandosud]').click(function () {
    let allievo_id = this.name.split("_")[3];
    $('input[name^="sud_option_"][name$="' + allievo_id + '"]:checked').length === 3 ? $('input[name^="sud_option_"][name$="' + allievo_id + '"]').not(':checked').attr('disabled', true) : $('input[name^="sud_option_"][name$="' + allievo_id + '"]').not(':checked').attr('disabled', false);
});

$('input[type=radio][name^=noagevolazione]').click(function () {
    if (this.previous) {
        this.checked = false;
    }
    let isDisabled = false;
    disableRadioGroup(this.name, this.checked);
    this.previous = this.checked;
    let a_id = this.name.split("_")[1];
    if ($('input[type=radio][name^=bandoreg_' + a_id + ']:checked').length === 0) {
        isDisabled = true;
    }
    disableBandi("bandose_" + a_id, !this.checked);
    disableRadioGroup("bandose_" + a_id, !this.checked);
    disableBandi("bandosud_" + a_id, !this.checked);
    disableRadioGroup("bandosud_" + a_id, !this.checked);
    disableBandi("bandoreg_" + a_id, !this.checked);
    disableRadioGroup("bandoreg_" + a_id, !this.checked);
    if (isDisabled) {
        $('input[type=text][name^=bando_reg_nome_' + a_id + ']').attr('disabled', 'disabled');
    }
});

$('.decimal_custom.ctrl').on('change', function () {
    let val;
    if (this.value.startsWith("9.")) {
        this.value = "9.0";
    }
    ;
    val = this.value;
    if (this.value === "") {
        val = 0;
    }
    let colA = "#A_" + this.id.split("_")[1] + "_" + this.id.split("_")[2];
    let colC = "#C_" + this.id.split("_")[1] + "_" + this.id.split("_")[2];

    let res = (Number($(colA).val()) * Number(val)).toFixed(1);
    $(colC).val(res);
    calcTotals(colC);
});

function setTotals() {
    let peso, voto, alunno;
    let totalB = 0;
    $('div.accordion[id^=al_]').each(function () {
        alunno = this.id.split("_")[1];
        $(this).find(".decimal_custom[id^=C_]").each(function () {
            peso = this.id.replace("C", "A");
            voto = this.id.replace("C", "B");
            $('#' + voto).val("0.00");
            this.value = Number($('#' + peso).val()) * Number($('#' + voto).val()).toFixed(1);
            totalB += Number(this.value);
        });
        totalB = Number(totalB).toFixed(1);
        $('#totalB_' + alunno).val(totalB);
        let final = 0;

        if (totalB > 0 && totalB <= 3) {
            final = 3;
        } else if (totalB > 3 && totalB <= 6) {
            final = 6;
        } else if (totalB > 6) {
            final = 9;
        } else {
            final = 0;
        }
        $('#final_' + alunno).val(final);
    });
}

function calcTotals(col) {
    let idAl = col.split("_")[2];
    let table = col.split("_")[0].replace('#', '');
    let totalB = 0;
    let idTotal = "#totalB_" + idAl;
    let idFinal = "#final_" + idAl;
    $('input[id^="' + table + '"][id$="' + idAl + '"]').each(function () {
        totalB += Number(this.value);
    });
    totalB = totalB.toFixed(1);
    $(idTotal).val(totalB);
    let final = 0;
    if (totalB > 0 && totalB <= 3) {
        final = 3;
    } else if (totalB > 3 && totalB <= 6) {
        final = 6;
    } else if (totalB > 6) {
        final = 9;
    } else {
        final = 0;
    }

    $(idFinal).val(final);
}

jQuery(document).ready(function () {
    $(".decimal_custom").inputmask({
        'alias': 'decimal',
        'mask': "9[.9]",
        'rightAlign': true
    });
    resetForm();
    initRadioButtons();
    disableRadioGroup("bandose", false);
    disableRadioGroup("bandosud", false);
    disableRadioGroup("bandoreg", false);
    disableRadioGroup("noagevolazione", false);
    setTotals();
    setStep3();
});


function resetForm() {
    $('#kt_form').find('select').select2("val", "-");
    $("#kt_form").find('input[type=text]:not([id^=A_],.hidden), select, textarea').val('');
    $("#kt_form").find('input:radio, input:checkbox').removeAttr('checked').removeAttr('selected');
    $('#kt_form').find('input[type=file]').val("");
    $("input[name^='domanda_a_']").prop('checked', true);
}

function ctrlForm(id) {
    var err = false;
    if ($('#domanda_a_' + id).is(":checked")) {
        err = checkObblFields_Allievo(id) ? true : err;
        err = !checkRequiredFileAlunno(id) ? true : err;
    }
    err = !checkRequiredM7Alunno(id) ? true : err;
    return !err;
}

function ctrlFormStep3() {
    var err = false;
    if ($("input:radio[name=scelta_step3]:checked").val() === 2) {
        err = checkObblFieldsContent($('#step3')) ? true : err;
    }
    return !err;
}

function checkObblFields_Allievo(id) {
    var err = false;
    $('input.obbligatory[id$=' + id + ']').each(function () {
        if (!$(this).is(':disabled')) {
            if ($(this).val() === '') {
                err = true;
                $(this).removeClass("is-valid").addClass("is-invalid");
            } else {
                $(this).removeClass("is-invalid").addClass("is-valid");
                //Valori della tabella possono essere 0.0?
//                if ($(this).hasClass("ctrl") && $(this).val() == "0.0") {
//                    err = true;
//                    $(this).removeClass("is-valid").addClass("is-invalid");
//                }
                if ($(this).hasClass("currencymask") && cleanCurrency($(this).val()) === 0) {
                    err = true;
                    $(this).removeClass("is-valid").addClass("is-invalid");
                }
            }
        } else {
            $(this).removeClass("is-valid").removeClass("is-invalid");
        }
    });
//    $('textarea.obbligatory[id$=' + id + ']').each(function () {
//        var testo1 = tinymce.get($(this).attr('id')).getContent({format: 'text'});
//        if (testo1 === '') {
//            err = true;
//            alert("VERIFICARE TUTTI I CAMPI DI TESTO.")
//            $(this).removeClass("is-valid").addClass("is-invalid");
//        } else {
//            $(this).removeClass("is-invalid").addClass("is-valid");
//        }
//    });
    $('select.obbligatory[id$=' + id + ']').each(function () {
        if ($(this).val() === '' || $(this).val() === '-' || $(this).val() === null) {
            err = true;
            $('#' + this.id + '_div').removeClass("is-valid-select").addClass("is-invalid-select");
        } else {
            $('#' + this.id + '_div').removeClass("is-invalid-select").addClass("is-valid-select");
        }
    });
    //Check radio SI/NO
    $('div.radioGroup_' + id).each(function () {
        let ctrl = false;
        let label;
        $(this).find('input[type=radio][name$=' + id + ']').each(function () {
            label = 'label[name=' + $(this).attr("name") + ']';
            if ($(this).is(":checked") && !ctrl) {
                ctrl = true;
            }
        });
        if (ctrl) {
            $(label).removeClass("kt-radio-invalid").addClass("kt-radio-valid");
        } else {
            $(label).removeClass("kt-radio-valid").addClass("kt-radio-invalid");
            err = true;
        }
    });
    //Check opzione radio Bandi
    let countOpzioniSel = $('input[type=radio].optionsRadio' + id + ':checked').length;
    if (countOpzioniSel === 0) {
        $('#alert_radio' + id).show();
        $('#alert_noagevolazione' + id).hide();
        $('#alert_bandosud' + id).hide();
        $('#alert_bandose' + id).hide();
        err = true;
    } else {
        $('#alert_radio' + id).hide();
        if ($('input[type=radio][name=noagevolazione_' + id + '].optionsRadio' + id + ':checked').length === 1 && $('input[type=radio][name=noagevolazione_' + id + '].optionsRadio' + id + ':enabled').length === 1 && $('input[type=radio][name=no_option_' + id + ']:enabled:checked').length === 0) {
            $('#alert_noagevolazione' + id).show();
            $('#alert_bandose' + id).hide();
            $('#alert_bandosud' + id).hide();
            $('#bando_reg_nome_' + id).removeClass("is-invalid").removeClass("is-valid");
            err = true;
        } else {
            $('#alert_noagevolazione' + id).hide();
        }
        if ($('input[type=radio][name=bandose_' + id + '].optionsRadio' + id + ':checked').length === 1 && $('input[type=radio][name=bandose_' + id + '].optionsRadio' + id + ':enabled').length === 1 && $('input[type=radio][name=se_option_' + id + ']:enabled:checked').length === 0) {
            $('#alert_bandose' + id).show();
            err = true;
        } else {
            $('#alert_bandose' + id).hide();
        }
        if ($('input[type=radio][name=bandosud_' + id + '].optionsRadio' + id + ':checked').length === 1 && $('input[type=radio][name=bandosud_' + id + '].optionsRadio' + id + ':enabled').length === 1 && $('input[type=radio][name^=sud_option_][name$=_' + id + ']:enabled:checked').length === 0) {
            $('#alert_bandosud' + id).show();
            err = true;
        } else {
            $('#alert_bandosud' + id).hide();
        }
    }

    return err;
}

function checkRequiredFileAlunno(id) {
    var err = false;
    $('input:file[tipo=obbligatory][id=doc_' + id + ']').each(function () {
        if ($(this)[0].files.length === 0) {
            err = true;
            $(this).attr("class", "custom-file-input is-invalid");
        } else {
            $(this).attr("class", "custom-file-input is-valid");
        }
    });
    return !err;
}

function checkRequiredM7Alunno(id) {
    var err = false;
    $('input:file[tipo=obbligatory][id=m7_' + id + ']').each(function () {
        if ($(this)[0].files.length === 0) {
            err = true;
            $(this).attr("class", "custom-file-input is-invalid");
        } else {
            $(this).attr("class", "custom-file-input is-valid");
        }
    });
    return !err;
}

function cleanCurrency(v) {
    v = v.substring(v.lastIndexOf("_") + 1);
    v = v.replaceAll(".", "");
    v = v.replaceAll(",", ".");
    return Number(v);
}


//Rendiconto Allievo
$('a[id^=rendiconta_]').on('click', function () {
    let idal = this.id.split("_")[1];
    if (ctrlForm(idal)) {

        swal.fire({
            title: '<h3 class="kt-font-io-n"><b>Rendiconta Allievo</b></h3><br>',
            html: "<h5>Attenzione, dopo la conferma non sarà più possibile modificare il modello 5 creato.<br>" +
                    "In caso di errore sarà necessario eliminare il modello in questione e ricompilare l'intera sezione.</h5>",
            animation: false,
            showCancelButton: true,
            confirmButtonText: '&nbsp;<i class="la la-check"></i>',
            cancelButtonText: '&nbsp;<i class="la la-close"></i>',
            cancelButtonClass: "btn btn-io-n",
            confirmButtonClass: "btn btn-io",
            customClass: {
                popup: 'animated bounceInUp'
            }
        }).then((result) => {
            if (result.value) {
                let ragioneSociale = $('#rs_' + idal).val();
                let formaGiuridica = $('#fg_' + idal).val();

                let comune = $('#comune_' + idal).val();
                let ateco = $('#ateco_' + idal).val();


                let motivazione2 = $('#motivazione_' + idal).val();
//                let motivazione2 = tinymce.get('motivazione_' + idal).getContent({format: 'text'});
                let ideaImpresa2 = $('#ideaimpresa_' + idal).val();
//                let ideaImpresa2 = tinymce.get('ideaimpresa_' + idal).getContent({format: 'text'});



                let tff = cleanCurrency($('#tff_' + idal).val());
                let tfra = cleanCurrency($('#tfra_' + idal).val());

                let sede = $('input[type=radio][name=check_sede_' + idal + ']:checked').val();
                let colloquio = $('input[type=radio][name=check_colloquio_' + idal + ']:checked').val();

                let bando_se = false;
                let bando_se_option = "";
                let bando_sud = false;
                let bando_sud_options = "";
                let bando_reg = false;
                let bando_reg_option = "";

                let no_agevolazione = $('input[type=radio][name=noagevolazione_' + idal + ']').is(":checked");
                let no_agevolazione_option = "";
                if (no_agevolazione) {
                    no_agevolazione_option = $('input[type=radio][name=no_option_' + idal + ']:checked').val();
                } else {
                    bando_se = $('input[type=radio][name=bandose_' + idal + ']').is(":checked");
                    if (bando_se) {
                        bando_se_option = $('input[type=radio][name=se_option_' + idal + ']:checked').val();
                    }
                    bando_sud = $('input[type=radio][name=bandosud_' + idal + ']').is(":checked");
                    if (bando_sud) {
                        $('input[type=radio][name^=sud_option_][name$=_' + idal + ']:checked').each(function () {
                            bando_sud_options += $(this).val() + ";";
                        });
                    }
                    bando_reg = $('input[type=radio][name=bandoreg_' + idal + ']').is(":checked");
                    if (bando_reg) {
                        bando_reg_option = $('#bando_reg_nome_' + idal).val();
                    }

                }

                let tab1 = "";
                let temp_multi = "";
                $('div#val_finale' + idal + ' :input.ctrl').each(function () {
                    temp_multi = $(this).attr("id").replace("B_", "C_");
                    tab1 += $(this).attr("id").split("_")[1] + "=" + $(this).val() + "=" + $('#' + temp_multi).val() + ";";
                });
                let punteggio_tab1 = $('div#val_finale' + idal + ' :input#totalB_' + idal).val();
                let valfinale_tab1 = $('div#val_finale' + idal + ' :input#final_' + idal).val();

                let hh64 = $("div.hh64_" + idal).length > 0;

                showLoad();
                let fdata = new FormData();

                fdata.append("id_allievo", idal);
                fdata.append("tab1", tab1);
                fdata.append("punteggio_tab1", punteggio_tab1);
                fdata.append("valfinale_tab1", valfinale_tab1);
                fdata.append("hh64", hh64);
                fdata.append("no_agevolazione", no_agevolazione);
                fdata.append("no_agevolazione_option", no_agevolazione_option);
                fdata.append("bando_se", bando_se);
                fdata.append("bando_se_option", bando_se_option);
                fdata.append("bando_sud", bando_sud);
                fdata.append("bando_sud_options", bando_sud_options);
                fdata.append("bando_reg", bando_reg);
                fdata.append("bando_reg_option", bando_reg_option);
                fdata.append("ragioneSociale", ragioneSociale);
                fdata.append("formaGiuridica", formaGiuridica);
                fdata.append("ideaImpresa", ideaImpresa2);
                fdata.append("comune", comune);
                fdata.append("ateco", ateco);
                fdata.append("motivazione", motivazione2);
                fdata.append("tff", tff);
                fdata.append("tfra", tfra);
                fdata.append("sede", sede);
                fdata.append("colloquio", colloquio);
                fdata.append("doc", $('#doc_' + idal)[0].files[0]);
                fdata.append("domanda_ammissione", $('#domanda_a_' + idal).is(":checked"));
                fdata.append("doc_modello7", $('#m7_' + idal)[0].files[0]);
                $.ajax({
                    type: "POST",
                    url: context + '/OperazioniSA?type=rendicontaAllievo',
                    data: fdata,
                    processData: false,
                    contentType: false,
                    success: function (data) {
                        closeSwal();
                        var json = JSON.parse(data);
                        if (json.result) {
                            swalSuccessReload("Rendicontazione allievo", "Operazione effettuata con successo");
                        } else {
                            swalError("Errore", json.message);
                        }
                    },
                    error: function () {
                        swalError("Errore", "Non è stato possibile rendicontare l'allievo");
                    }
                });
            } else {
                swal.close();
            }
        });
    }
});



function uploadModello5(idallievo, title) {
    swal.fire({
        title: title,
        html: '<div id="swalM5' + idallievo + '">'
                + '<div class="custom-file">'
                + '<input type="file" tipo="obbligatory" class="custom-file-input" accept="application/pkcs7-mime,application/pdf" name="m5_' + idallievo + '" id="m5_' + idallievo
                + '" onchange="return checkFileExtAndDim([&quot;pdf,p7m&quot;]);">'
                + '<label class="custom-file-label selected" id="label_' + idallievo + '" style="text-align: left;">Seleziona File</label>'
                + '</div>'
                + '</div>',
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
            $('#m5_' + idallievo).change(function (e) {
                if (e.target.files.length !== 0)
                    //$('#label_file').html(e.target.files[0].name);
                    if (e.target.files[0].name.length > 30)
                        $('#label_' + idallievo).html(e.target.files[0].name.substring(0, 30) + "...");
                    else
                        $('#label_' + idallievo).html(e.target.files[0].name);
                else
                    $('#label_' + idallievo).html("Seleziona File");
            });
        },
        preConfirm: function () {
            var err = false;
            err = !checkRequiredFileContent($('#swalM5' + idallievo)) ? true : err;
            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "file": $('#m5_' + idallievo)[0].files[0]
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
            fdata.append("file", result.value.file);
            $.ajax({
                type: "POST",
                url: context + '/OperazioniSA?type=uploadM5Alunno&id=' + idallievo,
                data: fdata,
                processData: false,
                contentType: false,
                success: function (data) {
                    closeSwal();
                    var json = JSON.parse(data);
                    if (json.result) {
                        swalSuccessReload("Modello 5", "Modello 5 caricato con successo");
                        $('#currentstep').val('');
                    } else {
                        swalError("Errore", json.message);
                    }
                },
                error: function () {
                    swalError("Errore", "Non è stato possibile caricare il modello 5");
                }
            });
        } else {
            swal.close();
        }
    });
}

$('button[id^=loadM5_]').click(function () {
    let idalunno = this.id.split("_")[1];
    uploadModello5(idalunno, $(this).attr('data-original-title'));
});


function setStep3() {
    let m6 = loadInformazioniStep3();
    if (m6 === null || m6 === "") {
        $("input:radio[name=scelta_step3]:first").prop('checked', 'checked');
        $('[name=regione_step3]').val('-');
        $('[name=provincia_step3]').val('-');
        $('[name=comune_step3]').val('-');
        $('#info_step3').hide();
    } else {
        if (m6.scelta_modello6 === 1) {
            $("input:radio[name=scelta_step3]:first").prop('checked', 'checked');
            $('#info_step3').hide();
            $('[name=regione_step3]').val('-');
            $('[name=provincia_step3]').val('-');
            $('[name=comune_step3]').val('-');
        } else {
            $("input:radio[name=scelta_step3]:last").prop('checked', 'checked');
            $('#info_step3').show();
            $('[name=regione_step3]').val(m6.comune_modello6.regione).trigger('change');
            setProvinciaStep3(m6.comune_modello6.regione, m6.comune_modello6.nome_provincia);
            setComuneStep3(m6.comune_modello6.nome_provincia, m6.comune_modello6.id);
            $('#indirizzo_step3').val(m6.indirizzo_modello6);
            $('#civico_step3').val(m6.civico_modello6);

        }
    }
}

$("input:radio[name=scelta_step3]").change(function () {
    if (this.value === 2 || this.value === "2") {
        $('#info_step3').show();
    } else {
        $('#info_step3').hide();
    }
});


function uploadRegistroComplessivo(pf) {
    swal.fire({
        title: 'Carica Registro complessivo presenze',
        html: '<div id="swal_regC">'
                + '<div class="custom-file">'
                + '<input type="file" tipo="obbligatory" class="custom-file-input" accept="application/pkcs7-mime,application/pdf" name="rcp_'
                + pf + '" id="rcp_' + pf + '" onchange="return checkFileExtAndDim([&quot;pdf,p7m&quot;]);">'
                + '<label class="custom-file-label selected" id="label_rcp_' + pf + '" style="text-align: left;">Seleziona File</label>'
                + '</div>'
                + '</div>',
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
            $('#rcp_' + pf).change(function (e) {
                if (e.target.files.length !== 0)
                    //$('#label_file').html(e.target.files[0].name);
                    if (e.target.files[0].name.length > 30)
                        $('#label_rcp_' + pf).html(e.target.files[0].name.substring(0, 30) + "...");
                    else
                        $('#label_rcp_' + pf).html(e.target.files[0].name);
                else
                    $('#label_rcp_' + pf).html("Seleziona File");
            });
        },
        preConfirm: function () {
            var err = false;
            err = !checkRequiredFileContent($('#swal_regC')) ? true : err;
            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "file": $('#rcp_' + pf)[0].files[0]
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
            fdata.append("file", result.value.file);
            $.ajax({
                type: "POST",
                url: context + '/OperazioniSA?type=uploadRegistroComplessivo&id=' + pf,
                data: fdata,
                processData: false,
                contentType: false,
                success: function (data) {
                    closeSwal();
                    var json = JSON.parse(data);
                    if (json.result) {
                        $('#currentstep').val('2');
                        swalSuccessReload("Registro Complessivo Presenze", "Registro caricato con successo");
                    } else {
                        swalError("Errore", json.message);
                    }
                },
                error: function () {
                    swalError("Errore", "Non è stato possibile caricare il registro.");
                }
            });
        } else {
            swal.close();
        }
    });
}

$('button[id^=registro_]').click(function () {
    uploadRegistroComplessivo(this.id.split("_")[1]);
});


function loadInformazioniStep3() {
    let temp = "";
    $.ajax({
        type: "GET",
        async: false,
        url: context + "/QuerySA?type=loadInfoM6&id=" + $('#pf').val(),
        success: function (resp) {
            if (resp !== null)
                temp = JSON.parse(resp);
        }
    });
    return temp;
}

function setComuneStep3(provincia, comune) {
    $("#comune_step3").empty();
    startBlockUILoad("#comune_step3_div");
    $("#comune_step3").append('<option value="-">Seleziona Comune</option>');
    $.get(context + '/Login?type=getComune&provincia=' + provincia, function (resp) {
        var json = JSON.parse(resp);
        for (var i = 0; i < json.length; i++) {
            if (comune === json[i].value) {
                $("#comune_step3").append('<option selected value="' + json[i].value + '">' + json[i].desc + '</option>');
            } else {
                $("#comune_step3").append('<option value="' + json[i].value + '">' + json[i].desc + '</option>');
            }
        }
        stopBlockUI("#comune_step3_div");
    });
}

function setProvinciaStep3(regione, provincia) {
    $("#provincia_step3").empty();
    startBlockUILoad("#provincia_step3_div");
    $("#provincia_step3").append('<option value="-">Seleziona Provincia</option>');
    $.get(context + '/Login?type=getProvincia&regione=' + regione, function (resp) {
        var json = JSON.parse(resp);
        for (var i = 0; i < json.length; i++) {
            if (provincia.toLowerCase() === json[i].value.toLowerCase()) {
                $("#provincia_step3").append('<option selected value="' + json[i].value + '">' + json[i].desc + '</option>');
            } else {
                $("#provincia_step3").append('<option value="' + json[i].value + '">' + json[i].desc + '</option>');
            }
        }
        stopBlockUI("#provincia_step3_div");
    });
}

$('button[id^=dichiarazione_]').click(function () {
    if (ctrlFormStep3()) {
        let scelta_step3 = $('input[type=radio][name=scelta_step3]:checked').val();
        let indirizzo_step3 = $('#indirizzo_step3').val();
        let civico_step3 = $('#indirizzo_step3').val();
        let comune_step3 = $('#comune_step3').val();

        showLoad();
        let fdata = new FormData();
        fdata.append("pf", this.id.split("_")[1]);
        fdata.append("scelta_step3", scelta_step3);
        fdata.append("indirizzo_step3", indirizzo_step3);
        fdata.append("civico_step3", civico_step3);
        fdata.append("comune_step3", comune_step3);

        $.ajax({
            type: "POST",
            url: context + '/OperazioniSA?type=uploadDichiarazioneM6',
            data: fdata,
            processData: false,
            contentType: false,
            success: function (data) {
                closeSwal();
                var json = JSON.parse(data);
                if (json.result) {
                    swalSuccessReload("Dichiarazione di chiusura percorso", "Dati salvati con successo");
                    $('#currentstep').val('3');
                } else {
                    swalError("Errore", "Non è stato possibile procedere con il salvataggio dei dati");
                }
            },
            error: function () {
                swalError("Errore", "Non è stato possibile procedere con il salvataggio dei dati");
            }
        });

    }
});


