/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var context = document.getElementById("compileCL").getAttribute("data-context");
let step1_loaded = false;
let allievi_fa = new Map();
let allievi_fb = new Map();
let allievi_mappati = new Map();
let allievi_outputs = new Map();
let allievi_domanda = new Map();

function completeOre(hh, max) {
    hh = hh.replaceAll("_", "").replace(",", ".");
    if (hh === "") {
        hh += "0";
    }
    hh = Number(hh).toFixed(2);
    if (Number(hh) > CommaToDot(max)) {
        return max;
    }
    return DotToComma(hh);
}

function DotToComma(n) {
    //console.log("IN " + n);
    //console.log("OUT " + formatMoney(n, 2, ",", "."));
    return formatMoney(n, 2, ",", ".");
    //n = Number(n).toFixed(2).toString().replace(".", ",");
    //return n;
}

function CommaToDot(n) {

    var out = n.toString();
    out = out.replace(".", "");
    out = out.replace(",", ".");
    return Number(out).toFixed(2);
    // n = n.toString().replace(".", "").replace(",", ".");
    // return Number(n).toFixed(2);
}

$('.decimal_custom.ctrl[id^=fa_controllo_ore_]').on('change', function () {
    let alunno = this.id.split("_")[3];
    this.value = completeOre(this.value, $('#fa_ore_' + alunno).val());
    let totalA = 0;
    let calcAlunno = CommaToDot($('#fa_coeff_' + alunno).val()) * CommaToDot($('#fa_controllo_ore_' + alunno).val());
    $('#fa_tot_' + alunno).val(DotToComma(calcAlunno));
    $(".decimal_custom[id^=fa_tot_]").each(function () {
        totalA += Number(CommaToDot(this.value));
    });
    $('#fa_total').val(DotToComma(totalA));
});
$('.decimal_custom.ctrl[id^=fb_controllo_ore_]').on('change', function () {
    let alunno = this.id.split("_")[3];
    this.value = completeOre(this.value, $('#fb_ore_' + alunno).val());
    let totalB = 0;
    let calcAlunno = CommaToDot($('#fb_coeff_' + alunno).val()) * CommaToDot($('#fb_controllo_ore_' + alunno).val());
    $('#fb_tot_' + alunno).val(DotToComma(calcAlunno));
    $(".decimal_custom[id^=fb_tot_]").each(function () {
        totalB += Number(CommaToDot(this.value));
    });
    $('#fb_total').val(DotToComma(totalB));
});
$('.decimal_custom.ctrl').on('change', function () {
    setTotals();
});
function setTotals() {
    let maxammissibile = Number(CommaToDot($('#dc_total').val())) + Number(CommaToDot($('#fa_total').val())) + Number(CommaToDot($('#fb_total').val()));
    let cond30 = (maxammissibile / 100) * 30;
    let vcr70 = Number(maxammissibile) - cond30;
    let valunitario = cond30 / Number($('div[id^=farow_]').length);
    $('#maxammissibile').val(DotToComma(maxammissibile));
    $('#cond30').val(DotToComma(cond30));
    $('#vcr70').val(DotToComma(vcr70));
    $('#valunitario').val(DotToComma(valunitario));
    $('#maxammissibile_step3').val(DotToComma(maxammissibile));
    $('#cond30_step3').val(DotToComma(cond30));
    $('#vcr70_step3').val(DotToComma(vcr70));
    $('#valunitario_step3').val(DotToComma(valunitario));
}

function setTableFaseA() {
    let alunno;
    let temp;
    $('div[id^=farow_]').each(function () {
        alunno = this.id.split("_")[1];
        temp = CommaToDot($('#fa_coeff_' + alunno).val()) * CommaToDot($('#fa_controllo_ore_' + alunno).val());
        $('#fa_tot_' + alunno).val(DotToComma(temp));
    });
    let totalA = 0;
    $(".decimal_custom[id^=fa_tot_]").each(function () {
        totalA += Number(CommaToDot(this.value));
    });
    $('#fa_total').val(DotToComma(totalA));
}

function setTableFaseB() {
    let alunno;
    let temp;
    $('div[id^=fbrow_]').each(function () {
        alunno = this.id.split("_")[1];
        temp = CommaToDot($('#fb_coeff_' + alunno).val()) * CommaToDot($('#fb_controllo_ore_' + alunno).val());
        $('#fb_tot_' + alunno).val(DotToComma(temp));
    });
    let totalB = 0;
    $(".decimal_custom[id^=fb_tot_]").each(function () {
        totalB += Number(CommaToDot(this.value));
    });
    $('#fb_total').val(DotToComma(totalB));
}

function setTableDocenti() {
    let alunno;
    let temp;
    $('div[id^=dcrow_]').each(function () {
        alunno = this.id.split("_")[1];
        temp = CommaToDot($('#dc_coeff_' + alunno).val()) * CommaToDot($('#dc_ore_' + alunno).val());
        $('#dc_tot_' + alunno).val(DotToComma(temp));
    });
    let totalB = 0;
    $(".decimal_custom[id^=dc_tot_]").each(function () {
        totalB += Number(CommaToDot(this.value));
    });
    $('#dc_total').val(DotToComma(totalB));
}

jQuery(document).ready(function () {
    $('.decimal_custom').each(function () {
        this.value = DotToComma(this.value);
    });

    $(".decimal_custom").maskMoney({prefix: '', allowNegative: true, thousands: '.', decimal: ',', affixesStay: false});

    //$(".decimal_custom").inputmask({
    //      mask: "9{0,4}[,]9{2}",
//        definitions: {
//            "Z": {
//                validator: "[,]",
//            }
//        },
    //       radixPoint: ',',
//        clearIncomplete: true,
//        autoUnmask: true,
    //       greedy: false,
    //       rightAlign: true,
    //      allowMinus: false
    //  });

    $(window).scrollTop(0);
    let dati = load_Checklist();
    step1_load(dati);
    setTableFaseA();
    setTableFaseB();
    setTableDocenti();
    step2_load(dati);
    setTotals();
    step3_load(dati);
    step4_load(dati);
    recap();
    window.location.hash = "";
});

function recap() {
    allievi_fa.forEach((v, k) => {
        $('#recapfa_' + k).val($('#fa_tot_' + k).val());
    });
    allievi_fb.forEach((v, k) => {
        $('#recapfb_' + k).val($('#fb_tot_' + k).val());
    });
    allievi_outputs.forEach((v, k) => {
        if (v === "1") {
            $('#recapmap_' + k).val("SI");
        }
    });
    $('div[id^=dcrow_]').each(function () {
        $('#recapdc_' + this.id.split("_")[1]).val($('#dc_tot_' + this.id.split("_")[1]).val());
    });

    $('#recap_totfa_allievi').val($('#fa_total').val());
    $('#recap_totfb_allievi').val($('#fb_total').val());
    $('#recap_totfa_docenti').val($('#dc_total').val());

    $('#recap_maxammissibile').val($('#maxammissibile').val());
    $('#recap_cond30').val($('#cond30').val());
    $('#recap_vcr70').val($('#vcr70').val());
    $('#recap_valunitario').val($('#valunitario').val());

    $('#recap_allievi_output_ok').val($('#allievi_output_ok').val());
    $('#recap_tot_contributo_ammesso').val($('#tot_contributo_ammesso').val());
}

$('input[type="checkbox"][id^=output_]').on('change', function () {
    step3_calc();
});

function step3_calc() {
    let allievi_output_ok = $('input[type="checkbox"][id^=output_]:checked').length;
    $('#allievi_output_ok').val(allievi_output_ok);
    let tot_contributo_ammesso = Number(CommaToDot($('#vcr70_step3').val())) + (Number(CommaToDot($('#valunitario_step3').val())) * allievi_output_ok);
    $('#tot_contributo_ammesso').val(DotToComma(tot_contributo_ammesso));
}

function resizeNota() {
    let heightN = $("div[id^=mappaturarow_]").length > 0 ? $("div[id^=mappaturarow_]").length : 2;
    $("#nota_controllore").attr("rows", heightN + 2);
}

function step1_load(data) {
    if (data.cl !== null && data.cl !== "") {
        $('#currentstep').val(data.cl.step);
        $('a[id^=show_file_]').hide();
        $("input:radio[name=step1_switch][value=" + data.cl.tipo + "]").prop('checked', true);
        $('#show_file_' + data.cl.tipo).attr("href", context + '/OperazioniGeneral?type=showDoc&path=' + data.cl.file);
        $('#show_file_' + data.cl.tipo).show();
        $('#step1_ok').show();
        step1_loaded = true;
    } else {
        $('#currentstep').val(1);
        $("input:radio[name=step1_switch][value=DURC]").prop('checked', true);
        $('#step1_ko').show();
    }
    step1_init();
}

function step2_load(dati) {
    if (dati.cl !== null && dati.cl !== "" && dati.cl.tab_neet_fa !== null && dati.cl.tab_neet_fa !== "" && dati.cl.tab_neet_fb !== null && dati.cl.tab_neet_fb !== "") {
        let tempID;
        let calc;
        $('.decimal_custom.ctrl[id^=fa_controllo_ore_]').each(function () {
            tempID = this.id.split("_")[3];
            this.value = DotToComma(allievi_fa.get(tempID));
            calc = Number(CommaToDot($('#fa_coeff_' + tempID).val())) * Number(allievi_fa.get(tempID));
            $('#fa_tot_' + tempID).val(DotToComma(calc));
        });
        $('.decimal_custom.ctrl[id^=fb_controllo_ore_]').each(function () {
            tempID = this.id.split("_")[3];
            this.value = DotToComma(allievi_fb.get(tempID));
            calc = Number(CommaToDot($('#fb_coeff_' + tempID).val())) * Number(allievi_fb.get(tempID));
            $('#fb_tot_' + tempID).val(DotToComma(calc));
        });
        setTableFaseA();
        setTableFaseB();
        $('#step2_ok').show();
    } else {
        $('#step2_ko').show();
    }
}

function step3_load(dati) {
    let tempID = 0;
    $('a[id^=ammissione_]').each(function () {
        tempID = Number(this.id.split("_")[1]);
        if (typeof (allievi_domanda.get(tempID)) !== 'undefined') {
            $(this).attr("href", context + '/OperazioniGeneral?type=showDoc&path=' + allievi_domanda.get(tempID));
            $(this).attr("data-original-title", "<h5>Visualizza domanda d'ammissione</h5>");
            $(this).addClass("btn-success");
            $(this).html("<i class='fa fa-file-pdf'></i>Domanda d'ammissione");

            $('#output_' + tempID).attr('disabled', false);

            if (allievi_outputs.get(tempID.toString()) === "1") {
                $('#output_' + tempID).attr('checked', 'checked');
            } else {
                $('#output_' + tempID).removeAttr('checked');
            }
            $('#output_' + tempID).addClass("daoutput");
        } else {
            $(this).removeAttr("href");
            $(this).attr("data-original-title", "<h5>Domanda d'ammissione assente, mappatura non disponibile</h5>");
            $(this).addClass("btn-danger").removeClass("daoutput");
            $(this).html("<i class='fa fa-times'></i> Domanda d'ammissione");

            $('#output_' + tempID).attr('disabled', true);
            $('#output_' + tempID).removeAttr('checked');
            $('#output_' + tempID);
        }
    });

    if (dati.cl !== null && dati.cl !== "" && dati.cl.nota_controllore !== null && dati.cl.tab_mappatura_neet !== "null" && dati.cl.tab_mappatura_neet !== "") {
        $('div[id^=mappaturarow_]').each(function () {
            tempID = Number(this.id.split("_")[1]);
            if (allievi_mappati.get(tempID.toString()) === "1") {
                $('#mappatura_' + tempID).attr('checked', 'checked');
            } else {
                $('#mappatura_' + tempID).removeAttr('checked');
            }
        });
    }

    if (dati.cl !== null && dati.cl !== "" && dati.cl.nota_controllore !== null && dati.cl.nota_controllore !== "null" && dati.cl.nota_controllore !== "") {
        $('#nota_controllore').val(dati.cl.nota_controllore);
    }
    if (dati.allievi !== null && dati.allievi !== "" && dati.cl !== null && dati.cl !== "" && dati.cl.tab_completezza_output_neet !== null && dati.cl.tab_completezza_output_neet !== "") {
        $('#step3_ok').show();
    } else {
        $('#step3_ko').show();
    }
    step3_calc();
    resizeNota();
}

function step4_load(dati) {
    if (dati.cl !== null && dati.cl !== "" && dati.cl.revisore !== null) {
        $("#controllore").val(dati.cl.revisore.codice).change();
        $('[data-ktwizard-type="action-submit"]').text("Aggiorna");
        $('#step4_ok').show();
    } else {
        $('#step4_ko').show();
    }
}

function step1_init() {
    if ($("input:radio[name=step1_switch]:checked").val() === "DURC") {
        $('#DURC_file').attr('disabled', false);
    } else {
        $('#DURC_file').attr('disabled', true);
    }
    if ($("input:radio[name=step1_switch]:checked").val() === "ASSENZA") {
        $('#scarica_ASSENZA_file').attr("href", context + '/OperazioniMicro?type=scaricaFileAssenza&idpr=' + $('#pf').val());
        $('#scarica_ASSENZA_file').removeClass("disablelink");
        $('#ASSENZA_file').attr('disabled', false);
    } else {
        $('#scarica_ASSENZA_file').removeAttr("href");
        $('#scarica_ASSENZA_file').addClass("disablelink");
        $('#ASSENZA_file').attr('disabled', true);
    }
}

$("input:radio[name=step1_switch]").change(function () {
    step1_init();
    ctrlFormStep1(false);
});
function ctrlFormStep1(isPress) {
    if (!step1_loaded || isPress) {
        let err = checkObblFieldsContent($('#step1')) ? true : false;
        let tipo = $("input:radio[name=step1_switch]:checked").val();
        err = !checkRequiredSpecificFile(tipo + "_file") ? true : err;
        if (tipo === "ASSENZA") {
            $('#DURC_file').removeClass('is-invalid is-valid');
        }
        if (tipo === "DURC") {
            $('#ASSENZA_file').removeClass('is-invalid is-valid');
        }
        return !err;
    } else {
        return true;
    }
}


//Rendiconto Allievo
$('a[id=save_step1]').on('click', function () {
    if (ctrlFormStep1(true)) {
        swal.fire({
            title: '<h3 class="kt-font-io-n"><b>Checklist Finale</b></h3><br>',
            html: "<h5>Salvare la prima parte della checklist finale?</h5>",
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
                showLoad();
                let fdata = new FormData();
                fdata.append("pf", $("#pf").val());
                fdata.append("tipo", $("input:radio[name=step1_switch]:checked").val());
                fdata.append("file", $('#' + $("input:radio[name=step1_switch]:checked").val() + '_file')[0].files[0]);
                fdata.append("step", 1);
                $.ajax({
                    type: "POST",
                    url: context + '/OperazioniMicro?type=checklistFinale',
                    data: fdata,
                    processData: false,
                    contentType: false,
                    success: function (data) {
                        closeSwal();
                        var json = JSON.parse(data);
                        if (json.result) {
                            swalSuccessReloadWithHash("Checklist Finale", "Salvataggio dei dati effettuato con successo", "#1");
                        } else {
                            swalError("Errore", json.message);
                        }
                    },
                    error: function () {
                        swalError("Errore", "Non è stato possibile procedere con il salvataggio dei dati");
                    }
                });
            } else {
                swal.close();
            }
        });
    }
});

$('a[id=save_step2]').on('click', function () {
    swal.fire({
        title: '<h3 class="kt-font-io-n"><b>Checklist Finale</b></h3><br>',
        html: "<h5>Salvare la seconda parte della checklist finale?</h5>",
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
            showLoad();
            let tempID;
            let fa_controllo_ore = "";
            $('input[id^=fa_controllo_ore_]').each(function () {
                tempID = this.id.split("_")[3];
                fa_controllo_ore += tempID + "=" + CommaToDot(this.value) + "=" + CommaToDot($('#fa_tot_' + tempID).val()) + ";";
            });
            let fb_controllo_ore = "";
            $('input[id^=fb_controllo_ore_]').each(function () {
                tempID = this.id.split("_")[3];
                fb_controllo_ore += tempID + "=" + CommaToDot(this.value) + "=" + CommaToDot($('#fb_tot_' + tempID).val()) + ";";
            });
            let fdata = new FormData();
            fdata.append("pf", $("#pf").val());
            fdata.append("fa_controllo_ore", fa_controllo_ore);
            fdata.append("fb_controllo_ore", fb_controllo_ore);
            fdata.append("fa_total", CommaToDot($("#fa_total").val()));
            fdata.append("fb_total", CommaToDot($("#fb_total").val()));
            fdata.append("dc_total", CommaToDot($("#dc_total").val()));
            fdata.append("maxammissibile", CommaToDot($("#maxammissibile").val()));
            fdata.append("cond30", CommaToDot($("#cond30").val()));
            fdata.append("vcr70", CommaToDot($("#vcr70").val()));
            fdata.append("valunitario", CommaToDot($("#valunitario").val()));
            fdata.append("step", 2);
            $.ajax({
                type: "POST",
                url: context + '/OperazioniMicro?type=checklistFinale',
                data: fdata,
                processData: false,
                contentType: false,
                success: function (data) {
                    closeSwal();
                    var json = JSON.parse(data);
                    if (json.result) {
                        swalSuccessReloadWithHash("Checklist Finale", "Salvataggio dei dati effettuato con successo", "#2");
                    } else {
                        swalError("Errore", json.message);
                    }
                },
                error: function () {
                    swalError("Errore", "Non è stato possibile procedere con il salvataggio dei dati");
                }
            });
        } else {
            swal.close();
        }
    });
});

$('a[id=save_step3]').on('click', function () {
    swal.fire({
        title: '<h3 class="kt-font-io-n"><b>Checklist Finale</b></h3><br>',
        html: "<h5>Salvare la terza parte della checklist finale?</h5>",
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
            showLoad();
            let tempID;
            let outputs = "";
            $('input[type="checkbox"][id^=output_]').each(function () {
                tempID = this.id.split("_")[1];
                if ($(this).is(":checked") && $(this).hasClass("daoutput")) {
                    outputs += tempID + "=1;";
                } else {
                    outputs += tempID + "=0;";
                }
            });
            let mappati = "";
            $('input[type="checkbox"][id^=mappatura_]').each(function () {
                tempID = this.id.split("_")[1];
                if ($(this).is(":checked")) {
                    mappati += tempID + "=1;";
                } else {
                    mappati += tempID + "=0;";
                }
            });
            let fdata = new FormData();
            fdata.append("pf", $("#pf").val());
            fdata.append("outputs", outputs);
            fdata.append("mappatura", mappati);
            fdata.append("allievi_output_ok", $("#allievi_output_ok").val());
            fdata.append("tot_contributo_ammesso", CommaToDot($("#tot_contributo_ammesso").val()));
            
            fdata.append("nota_controllore", tinymce.get("nota_controllore").getContent({ format: 'text' }));
//            fdata.append("nota_controllore", $("#nota_controllore").val());
            
            fdata.append("step", 3);
            $.ajax({
                type: "POST",
                url: context + '/OperazioniMicro?type=checklistFinale',
                data: fdata,
                processData: false,
                contentType: false,
                success: function (data) {
                    closeSwal();
                    var json = JSON.parse(data);
                    if (json.result) {
                        swalSuccessReloadWithHash("Checklist Finale", "Salvataggio dei dati effettuato con successo", "#3");
                    } else {
                        swalError("Errore", json.message);
                    }
                },
                error: function () {
                    swalError("Errore", "Non è stato possibile procedere con il salvataggio dei dati");
                }
            });
        } else {
            swal.close();
        }
    });
});

function checkRequiredSpecificFile(id) {
    var err = false;
    $('input:file[tipo=obbligatory][id=' + id + ']').each(function () {
        if ($(this)[0].files.length === 0) {
            err = true;
            $(this).attr("class", "custom-file-input is-invalid");
        } else {
            $(this).attr("class", "custom-file-input is-valid");
        }
    });
    return !err;
}

function load_Checklist() {
    let ret = "";
    $.ajax({
        type: "GET",
        async: false,
        url: context + "/QueryMicro?type=getChecklistfinale&pf=" + $('#pf').val(),
        success: function (resp) {
            if (resp !== null && resp.length > 0) {
                ret = JSON.parse(resp);
            }
        }
    });
    if (ret.cl !== null && ret.cl !== "" && ret.cl.tab_neet_fa !== null && ret.cl.tab_neet_fa !== "" && ret.cl.tab_neet_fb !== null && ret.cl.tab_neet_fb !== "") {
        allievi_fa = new Map(JSON.parse(ret.cl.tab_neet_fa).map(i => [i.id, i.ore]));
        allievi_fb = new Map(JSON.parse(ret.cl.tab_neet_fb).map(i => [i.id, i.ore]));
    }
    if (ret.allievi !== null && ret.allievi !== "") {
        allievi_domanda = new Map(JSON.parse(ret.allievi).map(i => [i.id, i.da]));
    }
    if (ret.cl !== null && ret.cl !== "" && ret.cl.tab_mappatura_neet !== null && ret.cl.tab_mappatura_neet !== "") {
        allievi_mappati = new Map(JSON.parse(ret.cl.tab_mappatura_neet).map(i => [i.id, i.mappato]));
    }
    if (ret.cl !== null && ret.cl !== "" && ret.cl.tab_completezza_output_neet !== null && ret.cl.tab_completezza_output_neet !== "") {
        allievi_outputs = new Map(JSON.parse(ret.cl.tab_completezza_output_neet).map(i => [i.id, i.output]));
    }

    return ret;
}
function formatMoney(number, decPlaces, decSep, thouSep) {
    decPlaces = isNaN(decPlaces = Math.abs(decPlaces)) ? 2 : decPlaces,
            decSep = typeof decSep === "undefined" ? "." : decSep;
    thouSep = typeof thouSep === "undefined" ? "," : thouSep;
    var sign = number < 0 ? "-" : "";
    var i = String(parseInt(number = Math.abs(Number(number) || 0).toFixed(decPlaces)));
    var j = (j = i.length) > 3 ? j % 3 : 0;
    return sign +
            (j ? i.substr(0, j) + thouSep : "") +
            i.substr(j).replace(/(\decSep{3})(?=\decSep)/g, "$1" + thouSep) +
            (decPlaces ? decSep + Math.abs(number - i).toFixed(decPlaces).slice(2) : "");
}
