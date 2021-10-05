/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



var context = document.getElementById("schedaUD").getAttribute("data-context");

$.getScript(context + '/page/partialView/partialView.js', function () {});


function deleteDocUD(id) {
    swal.fire({
        title: '<h3 class="kt-font-io-n"><b>Conferma Eliminazione</b></h3><br>',
        html: "<h5 style='text-align:center;'>Sicuro di voler eliminare questo documento?</h5>",
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
            deleteConfirmed(id);
        } else {
            swal.close();
        }
    });
}

function deleteConfirmed(id) {
    showLoad();
    $.ajax({
        type: "POST",
        url: context + '/OperazioniMicro?type=deleteDocUnitaDidattica&id=' + id,
        success: function (data) {
            closeSwal();
            var json = JSON.parse(data);
            if (json.result) {
                swalSuccessReload("Documento eliminato", "Documento eliminato con successo");
            } else {
                swalError("Errore", json.message);
            }
        },
        error: function () {
            swalError("Errore", "Non è stato possibile eliminare il documento");
        }
    });
}


function updateDocUD(idud, estensione, mime_type) {
    var ext = estensione.split('"').join("&quot;");
    var swalDoc = getHtml("swalDoc", context).replace("@func", "checkFileExtAndDim(" + ext + ");").replace("@mime", mime_type);
    swal.fire({
        title: 'Modifica Documento',
        html: swalDoc,
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
            $('#file').change(function (e) {
                if (e.target.files.length != 0) {
                    if (e.target.files[0].name.length > 20) {
                        $('#label_doc').html(e.target.files[0].name.substring(0, 20) + "...");
                    } else {
                        $('#label_doc').html(e.target.files[0].name);
                    }
                } else {
                    $('#label_doc').html("Seleziona File");
                }
            });
        },
        preConfirm: function () {
            var err = false;
            err = !checkRequiredFileContent($('#swalDoc')) ? true : err;
            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "file": $('#file')[0].files[0]
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
            fdata.append("file", result.value.file);
            updateConfirmed(idud, fdata);
        } else {
            swal.close();
        }
    });
}

function updateConfirmed(idud, fdata) {
    $.ajax({
        type: "POST",
        url: context + '/OperazioniMicro?type=updateDocUnitaDidattica&idud=' + idud,
        data: fdata,
        processData: false,
        contentType: false,
        success: function (data) {
            closeSwal();
            var json = JSON.parse(data);
            if (json.result) {
                swalSuccessReload("Documento Modificato", "Documento modificato con successo");
            } else {
                swalError("Errore", json.message);
            }
        },
        error: function () {
            swalError("Errore", "Non è stato possibile modificare il documento");
        }
    });
}

function uploadDocUD(codice, estensione, mime_type) {
    var ext = estensione.split('"').join("&quot;");
    var swalDoc = getHtml("swalDoc", context).replace("@func", "checkFileExtAndDim(" + ext + ");").replace("@mime", mime_type);
    swal.fire({
        title: 'Carica Documento',
        html: swalDoc,
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
            $('#file').change(function (e) {
                if (e.target.files.length !== 0) {
                    if (e.target.files[0].name.length > 20) {
                        $('#label_doc').html(e.target.files[0].name.substring(0, 20) + "...");
                    } else {
                        $('#label_doc').html(e.target.files[0].name);
                    }
                } else {
                    $('#label_doc').html("Seleziona File");
                }
            });
        },
        preConfirm: function () {
            var err = false;
            err = !checkRequiredFileContent($('#swalDoc')) ? true : err;
            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "file": $('#file')[0].files[0]
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
            fdata.append("tipo", "F");
            fdata.append("file", result.value.file);
            $.ajax({
                type: "POST",
                url: context + '/OperazioniMicro?type=uploadDocUnitaDidattica&codice=' + codice,
                data: fdata,
                processData: false,
                contentType: false,
                success: function (data) {
                    closeSwal();
                    var json = JSON.parse(data);
                    if (json.result) {
                        swalSuccessReload("Documento Caricato", "Documento caricato con successo.");
                    } else {
                        swalError("Errore", json.message);
                    }
                },
                error: function () {
                    swalError("Errore", "Non è stato possibile caricare il documento");
                }
            });
        } else {
            swal.close();
        }
    });
}


function updateLinkUD(idud, path) {
    let current_path = path;
    let html = '<input type="text" placeholder="Inserisci un link youtube" value="' + path + '" class="form-control" name="url_link" id="url_link">';
    let link;
    swal.fire({
        title: 'Carica Link Youtube',
        html: html,
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
//            if (current_path !== $('#url_link').val()) {
            link = matchYoutubeUrl($('#url_link').val());
            if (!link) {
                $('#url_link').removeClass('is-valid').addClass('is-invalid');
                $('#url_link').val("");
                $('#url_link').attr("placeholder", "Inserisci un link valido");
                return false;
            } else {
                $('#url_link').removeClass('is-invalid').addClass('is-valid');
            }
//            }else{
//                $('#url_link').removeClass('is-valid').addClass('is-invalid');
//                    $('#url_link').val("");
//                    $('#url_link').attr("placeholder", "Inserisci un link valido");
//                    return false;
//            }
        }
    }).then((result) => {
        if (result.value) {
            showLoad();
            var fdata = new FormData();
            fdata.append("link", link);
            $.ajax({
                type: "POST",
                url: context + '/OperazioniMicro?type=updateDocUnitaDidattica&idud=' + idud,
                data: fdata,
                processData: false,
                contentType: false,
                success: function (data) {
                    closeSwal();
                    var json = JSON.parse(data);
                    if (json.result) {
                        swalSuccessReload("Link Caricato", "Link modificato con successo.");
                    } else {
                        swalError("Errore", json.message);
                    }
                },
                error: function () {
                    swalError("Errore", "Non è stato possibile modificare il link");
                }
            });
        } else {
            swal.close();
        }
    });
}

function uploadLinkUD(codice) {
    let html = '<input type="text" placeholder="Inserisci un link youtube" class="form-control" name="url_link" id="url_link">';
    let link;
    swal.fire({
        title: 'Carica Link Youtube',
        html: html,
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
            link = matchYoutubeUrl($('#url_link').val());
            if (!link) {
                $('#url_link').removeClass('is-valid').addClass('is-invalid');
                $('#url_link').val("");
                $('#url_link').attr("placeholder", "Inserisci un link valido");
                return false;
            } else {
                $('#url_link').removeClass('is-invalid').addClass('is-valid');
            }
        }
    }).then((result) => {
        if (result.value) {
            showLoad();
            var fdata = new FormData();
            fdata.append("tipo", "L");
            fdata.append("link", link);
            $.ajax({
                type: "POST",
                url: context + '/OperazioniMicro?type=uploadDocUnitaDidattica&codice=' + codice,
                data: fdata,
                processData: false,
                contentType: false,
                success: function (data) {
                    closeSwal();
                    var json = JSON.parse(data);
                    if (json.result) {
                        swalSuccessReload("Link Caricato", "Link caricato con successo.");
                    } else {
                        swalError("Errore", json.message);
                    }
                },
                error: function () {
                    swalError("Errore", "Non è stato possibile caricare il link");
                }
            });
        } else {
            swal.close();
        }
    });
}

function saveDescription() {
    let desc = $('#descrizione').val();
    if (desc === '') {
        swalWarning("Descrizione Unità didattica", "La descrizione è un campo obbligatorio");
    } else if (desc == $('#old_desc').val()) {
        swalWarning("Descrizione Unità didattica", "Inserisci una descrizione diversa da quella già presente");
    } else {
        showLoad();
        let fdata = new FormData();
        fdata.append("desc", desc);
        fdata.append("codice", $('#codice').val());
        $.ajax({
            type: "POST",
            url: context + '/OperazioniMicro?type=updateDescrizioneUD',
            data: fdata,
            processData: false,
            contentType: false,
            success: function (data) {
                closeSwal();
                var json = JSON.parse(data);
                if (json.result) {
                    swalSuccessReload("Descrizione Unità didattica", "Descrizione modificata con successo");
                } else {
                    swalError("Errore", json.message);
                }
            },
            error: function () {
                swalError("Errore", "Non è stato possibile modificare la descrizione");
            }
        });
    }
}

function counterCharacters() {
    $('span#numChar').text($('#descrizione').val().length);
}

function checkUploads() {
    if ($('#checkTotal').val() === 'true') {
        $('#btnGroupDrop1').prop('disabled', true);
        $('#alertFiles').show();
    } else {
        $('#btnGroupDrop1').prop('disabled', false);
        $('#alertFiles').hide();

        if ($('#checkFiles').val() === 'true') {
            $('#uDoc').prop('disabled', true);
            $('#alertDoc').show();
        } else {
            $('#uDoc').prop('disabled', false);
            $('#alertDoc').hide();
        }
        if ($('#checkLinks').val() === 'true') {
            $('#uLink').prop('disabled', true);
            $('#alertLink').show();
        } else {
            $('#uLink').prop('disabled', false);
            $('#alertLink').hide();
        }

    }
}

$(document).ready(function () {
    counterCharacters();
    checkUploads();
});