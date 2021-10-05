"use strict";
var contextW = document.getElementById("wizCp").getAttribute("data-context");
let redirect = contextW + '/page/sa/searchProgettiFormativi.jsp?icip=' + $('#cip').val();

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
        if ($('#currentstep') != "") {
            setinitialstep = Number($('#currentstep').val());
        }
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
            if (finalCheck_Step4()) {
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
                            swalSuccessRedirect("Conclusione Progetto", "La dichiarazione di chiusura percorso è stata caricata correttamente!", redirect);
                        } else {
                            swalError('Errore', "Non è stato possibile conclude il progetto. Se l'errore persiste richiedere assistenza");
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
    let m5modules_alunni = $('button[load=toload]').length;
    let allieviDaCaricare = $('.countAtoL').length;
    let cond_allieviRendicontati = allieviDaCaricare > 0 ? true : false;
    let cond_m5Caricati = m5modules_alunni > 0 ? true : false;
    if (cond_allieviRendicontati || cond_m5Caricati) {
        if (cond_allieviRendicontati && cond_m5Caricati) {
            fastSwalShow("<h5>Per poter procedere rendiconta tutti gli allievi e carica i rispetti modelli 5.<br>Si ricorda che i modelli 5 generati per ogni singolo alunno, vanno firmati digitalmente e caricati nuovamente.</h5>", "wobble");
        } else if (cond_allieviRendicontati) {
            fastSwalShow("<h5>Per poter procedere rendiconta tutti gli allievi e successivamente carica i rispettivi modelli 5.</h5>", "wobble");
        } else {
            fastSwalShow("<h5>Per poter procedere caricare, per ogni allievo, i modelli 5.<br>Si ricorda che tali modelli vanno scaricati, firmati digitalmente e caricati nuovamente.</h5>", "wobble");
        }
        wizardObj.stop(); // don't go to the next step
        $('html, body').animate({scrollTop: $('body').offset().top}, 300);
        return false;
    }
    return true;
}

function checkStep2(wizardObj) {
    let registroComplessivo = $('button[registro=toload]').length;
    var err = false;
    if (registroComplessivo === 1) {
        fastSwalShow("<h5>Per poter procedere carica il registro complessivo delle presenze.</h5>", "wobble");
        err = true;
    }
    if (err) {
        wizardObj.stop();
        $('html, body').animate({scrollTop: $('body').offset().top}, 300);
        return false;
    }
    return true;
}

function checkStep3(wizardObj) {
    let registroComplessivo = $('button[declar=toload]').length;
    var err = false;
    if (registroComplessivo === 1) {
        fastSwalShow("<h5>Per poter procedere salvare i dati della dichiarazione.</h5>", "wobble");
        err = true;
    }
    if (err) {
        wizardObj.stop();
        $('html, body').animate({scrollTop: $('body').offset().top}, 300);
        return false;
    }
    return true;
}

function checkStep4(wizardObj) {
    var err = false;
    err = !checkRequiredFileContent($('#step4')) ? true : err;
    if (err) {
        fastSwalShow("<h5>Per poter procedere con la conclusione del progetto formativo, carica il modello 6 firmato digitalmente.</h5>", "wobble");
        wizardObj.stop();
        $('html, body').animate({scrollTop: $('body').offset().top}, 300);
        return false;
    }
    return true;
}

function finalCheck_Step4() {
    var err = false;
    err = !checkRequiredFileContent($('#step4')) ? true : err;
    if (err) {
        fastSwalShow("<h5>Per poter procedere con la conclusione del progetto formativo, carica il modello 6 firmato digitalmente.</h5>", "wobble");
        $('html, body').animate({scrollTop: $('body').offset().top}, 300);
        return false;
    }
    return true;
}


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

