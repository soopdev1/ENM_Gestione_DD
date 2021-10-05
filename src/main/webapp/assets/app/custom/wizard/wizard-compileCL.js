"use strict";
var contextW = document.getElementById("wizCl").getAttribute("data-context");
let redirect = contextW + '/page/mc/searchPFMicro.jsp?icip=' + $('#cip').val();

// Class definition
var KTWizard1 = function () {
    // Base elements
    var wizardEl;
    var formEl;
    var wizard;
    // Private functions
    var initWizard = function () {
        // Initialize form wizard
        let setinitialstep = 1;
        if($(location).attr('hash') != ""){
            setinitialstep = Number($(location).attr('hash').substring(1)) + 1;
        }
//        if ($('#currentstep').val() !== "") {
//            setinitialstep = Number($('#currentstep').val());
//        }
        wizard = new KTWizard('kt_wizard_v1', {
            startStep: setinitialstep
        });
        // Validation before going to next page
        wizard.on('beforeNext', function (wizardObj) {
            if (wizard.currentStep === 1) {
                checkStep1(wizardObj);
            } else if (wizard.currentStep === 2) {
                checkStep2(wizardObj);
            } else if (wizard.currentStep === 3) {
                checkStep3(wizardObj);
            }
//            else if (wizard.currentStep === 4) {
//                checkStep4(wizardObj);
//            }
        });
        wizard.on('change', function (wizardObj) {
            var step = getStep(wizard, wizardObj);
            if (step > 0) {
                wizard.goTo(step);
                wizard.goTo(step); /*fatto x2 perchè uno non va*/
            }

            setTimeout(function () {
                KTUtil.scrollTop();
            }, 300);
        });
    }

    var initSubmit = function () {
        var btn = formEl.find('[data-ktwizard-type="action-submit"]');
        btn.on('click', function (e) {
            if (checkStep4()) {
                formEl.prop("target", "");
                showLoad('Attendi qualche secondo...');
                e.preventDefault();
                KTApp.progress(btn);
                formEl.ajaxSubmit({
                    error: function () {
                        KTApp.unprogress(btn);
                        swal.close();
                        swalError('Errore', "Riprovare, se l'errore persiste richiedere assistenza");
                    },
                    success: function (resp) {
                        var json = JSON.parse(resp);
                        KTApp.unprogress(btn);
                        swal.close();
                        if (json.result) {
                            swalSuccessRedirect("Checklist Finale", "Salvataggio dati completato con successo!", redirect);
                        } else {
                            swalError('Errore', "Non è stato possibile eseguire l'operazione richiesta. Se l'errore persiste richiedere assistenza");
                        }
                    }
                });
            }
        });
    }

    return {
        init: function () {
            wizardEl = KTUtil.get('kt_wizard_v1');
            formEl = $('#kt_form');
            initWizard();
            initSubmit();
        }
    };
}();
jQuery(document).ready(function () {
    KTWizard1.init();
});
function checkStep1(wizardObj) {
    if (ctrlFormStep1() && Number($('#currentstep').val()) > 1) {
        return true;
    }
    fastSwalShow("<h5>Per poter accedere allo Step 2, procedere con il caricamento dei dati.</h5>", "wobble");
    wizardObj.stop(); // don't go to the next step
    return false;
}

function checkStep2(wizardObj) {
    if (Number($('#currentstep').val()) > 2) {
        return true;
    }
    fastSwalShow("<h5>Per poter accedere allo Step 3, procedere con il salvataggio dei dati.</h5>", "wobble");
    wizardObj.stop(); // don't go to the next step
    return false;
}

function checkStep3(wizardObj) {
    if (Number($('#currentstep').val()) > 3) {
        return true;
    }
    fastSwalShow("<h5>Per poter accedere allo Step 4, procedere con il salvataggio dei dati.</h5>", "wobble");
    wizardObj.stop(); // don't go to the next step
    return false;
}

function checkStep4() {
    var err = false;
    err = checkObblFieldsContent($('#step4')) ? true : err;
    if (err) {
        fastSwalShow("<h5>Per poter procedere con la conclusione, selezionare un controllore.</h5>", "wobble");
//        $('html, body').animate({scrollTop: $('body').offset().top}, 300);
        return false;
    }
    return true;
}

//function finalCheck_Step4() {
//    var err = false;
//    err = !checkRequiredFileContent($('#step4')) ? true : err;
//    if (err) {
//        fastSwalShow("<h5>Per poter procedere con la conclusione del progetto formativo, carica il modello 6 firmato (.p7m CAdES, .pdf PAdES) digitalmente.</h5>", "wobble");
//        $('html, body').animate({scrollTop: $('body').offset().top}, 300);
//        return false;
//    }
//    return true;
//}


function getStep(wizard, wizardObj) {
    if (wizard.currentStep === 3) {
        if (!checkStep2(wizardObj)) {
            return 2;
        }
    }
    if (wizard.currentStep === 4) {
        if (!checkStep2(wizardObj)) {
            return 2;
        }
        if (!checkStep3(wizardObj)) {
            return 3;
        }
//        if (!checkStep4(wizardObj)) {
//            return 4;
//        }
    }

    return 0;
}

