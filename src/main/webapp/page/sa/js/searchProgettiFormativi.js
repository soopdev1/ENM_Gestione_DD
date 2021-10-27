/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var context = document.getElementById("searchProgettiFormativi").getAttribute("data-context");
$.getScript(context + '/page/partialView/partialView.js', function () {});
var ore_max_daily = document.getElementById("ore_max").getAttribute("data-context");
var mapM4_start;
var mapConclusionePrg;
var today = moment(new Date()).format('YYYY-MM-DD');
var KTDatatablesDataSourceAjaxServer = function () {
    var initTable1 = function () {
        var table = $('#kt_table_1');
        table.DataTable({
            dom: `<'row'<'col-sm-12'ftr>><'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7 dataTables_pager'lp>>`,
            lengthMenu: [5, 10, 25, 50],
            language: {
                "lengthMenu": "Mostra _MENU_",
                "infoEmpty": "Mostrati 0 di 0 per 0",
                "loadingRecords": "Caricamento...",
                "search": "Cerca:",
                "zeroRecords": "Nessun risultato trovato",
                "info": "Mostrati _START_ di _TOTAL_ ",
                "emptyTable": "Nessun risultato",
                "sInfoFiltered": "(filtrato su _MAX_ risultati totali)"
            },
//            responsive: true,
            ScrollX: "100%",
            sScrollXInner: "110%",
            searchDelay: 500,
            processing: true,
            pageLength: 10,
            ajax: context + '/QuerySA?type=searchProgetti&cip=' + $('#cip').val()
                    + '&stato=' + $('#stato').val(),
            order: [],
            columns: [
                {defaultContent: ''},
                {data: 'id'},
                {data: 'descrizione', className: 'text-center text-uppercase'},
                {data: 'start'},
                {data: 'end'},
                {data: 'cip'},
                {defaultContent: ''},
                {data: 'stato.descrizione', className: 'text-center text-uppercase'},
                {data: 'motivo', className: 'text-center text-uppercase'},
                {data: 'stato.de_tipo', className: 'text-center text-uppercase'}
            ],
            drawCallback: function () {
                $('[data-toggle="kt-tooltip"]').tooltip();
            },
            rowCallback: function (row, data) {
                $(row).attr("id", "row_" + data.id);
            },
            columnDefs: [
                {
                    targets: 0,
                    className: 'text-center',
                    orderable: false,
                    render: function (data, type, row, meta) {
                        var option = '<div class="dropdown dropdown-inline">'
                                + '<button type="button" class="btn btn-icon btn-sm btn-icon-md btn-circle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">'
                                + '   <i class="flaticon-more-1"></i>'
                                + '</button>'
                                + '<div class="dropdown-menu dropdown-menu-left">';
                        option += '<a class="dropdown-item" href="javascript:void(0);" onclick="swalTableAllievi(' + row.id + ')"><i class="flaticon-users-1"></i> Visualizza Allievi</a>';
                        option += '<a class="dropdown-item" href="javascript:void(0);" onclick="swalTableDocenti(' + row.id + ')"><i class="fa fa-chalkboard-teacher"></i> Visualizza Docenti</a>';
                        option += '<a class="dropdown-item" href="javascript:void(0);" onclick="swalDocumentPrg(' + row.id + ')"><i class="fa fa-file-alt"></i> Visualizza Documenti</a>';
                        if (row.stato.controllare === 0) {
                            if (row.stato.modificabile === 1) {
                                if (row.modello2_check === 0) {
                                    if (row.stato.id === "P") {
                                        option += '<a class="dropdown-item fancyBoxReload" href="modifyProgetto.jsp?id=' + row.id + '"><i class="fa fa-pencil-alt"></i> Modifica Modello 2</a>';
                                    } else {
                                        option += '<a class="dropdown-item fancyBoxReload" href="modifyProgetto.jsp?id=' + row.id + '"><i class="fa fa-pencil-alt"></i> Modifica</a>';
                                    }
                                } else {
                                    option += '<a class="dropdown-item fancyBoxReload kt-font-danger" href="modifyProgetto.jsp?id=' + row.id + '"><i class="fa fa-pencil-alt kt-font-danger"></i> Modifica</a>';
                                }
                            }

                            if (row.stato.modifiche.docenti === 1) {
                                option += '<a class="dropdown-item fancyBoxAntoRef" href="modifyDocentiProgetto.jsp?id=' + row.id + '"><i class="fas fa-user-edit"></i> Modifica Docenti</a>';
                            }
                            if (row.controllable === 1) {
                                option += '<a class="dropdown-item" href="javascript:void(0);" onclick="confirmNext(' + row.id + ',\'' + row.stato.id + '\')"> Manda avanti la pratica &nbsp;<i class="fa fa-angle-double-right" style="margin-top:-2px"></i></a>';
                            }

                            if (row.stato.id === "P" || row.stato.id === "SOA" || row.stato.id === "SOB") {
                                option += '<a class="dropdown-item fancyBoxFull" href="newStaff.jsp?id=' + row.id + '"><i class="flaticon-users-1"></i> Inserisci membri Staff</a>';
                            }
                            if ((row.stato.id === "P" || row.stato.id === "DCE" || row.stato.id === "SOA") && row.modello2_check === 0) {
                                //modello 3 con invio a MC
                                option += '<a class="dropdown-item fancyBoxFullReload" href="modello3.jsp?id=' + row.id + '"><i class="fa fa-calendar-check"></i> Carica Modello 3 e Conferma</a>';
                            } else if (row.stato.id === "ATA") {
                                option += '<a class="dropdown-item fancyBoxFullReload" href="modello3.jsp?id=' + row.id + '"><i class="fa fa-calendar-check"></i> Visualizza/Modifica Calendario Modello 3</a>';
                            }

                            //TEST modello 4
                            if (row.stato.id === "ATA" || row.stato.id === "SOB") {
                                if (mapM4_start.has(row.id) && (today >= moment(new Date(mapM4_start.get(row.id))).format('YYYY-MM-DD'))) {
                                    option += '<a class="dropdown-item fancyBoxFullReload" href="modello4.jsp?id=' + row.id + '"><i class="fa fa-calendar-check"></i> Carica Modello 4 e Conferma</a>';
                                } else {
                                    option += '<a class="dropdown-item kt-font-danger" href="#" style="cursor:not-allowed!important" data-container="body" data-html="true" data-toggle="kt-tooltip" title="Il caricamento del Modello 4 sarà disponibile una volta completata la fase A (' + formattedDate(new Date(mapM4_start.get(row.id))) + ')"><i class="fa fa-calendar-check kt-font-danger"></i> Carica Modello 4 e Conferma</a>';
                                }
                            } else if (row.stato.id === "ATB") {
                                option += '<a class="dropdown-item fancyBoxFullReload" href="modello4.jsp?id=' + row.id + '"><i class="fa fa-calendar-check"></i> Visualizza/Modifica Calendario Modello 4</a>';
                            } else if (row.stato.id === "F") {
                                option += '<a class="dropdown-item fancyBoxFullReload" href="modello4.jsp?id=' + row.id + '"><i class="fa fa-calendar-check"></i> Visualizza Calendario Modello 4</a>';
                                option += '<a class="dropdown-item" href="concludiPrg.jsp?id=' + row.id + '"><i class="fa fa-angle-double-right"></i> Concludi Progetto</a>';
                            }

                        }



                        if (row.fadroom !== null && (row.stato.id === "ATA" || row.stato.id === "ATB")) {

                            for (var x1 = 0; x1 < row.fadroom.length; x1++) {
                                var formnuovo = '<form target="_blank" id="frfad_' +
                                        row.fadroom[x1].nomestanza + '" method="post" action="' +
                                        row.fadlink + '">' +
                                        '<input type="hidden" name="type" value="login_fad_mcn"/> ' +
                                        '<input type="hidden" name="roomname" value="' + row.fadroom[x1].nomestanza + '"/> ' +
                                        '<input type="hidden" name="corso" value="' + row.fadroom[x1].numerocorso + '"/> ' +
                                        '<input type="hidden" name="codfisc" value="' + row.usermc + '"/> ' +
                                        '<input type="hidden" name="progetto" value="' + row.id + '"/> ' +
                                        '<input type="hidden" name="view" value="2"/> ' +
                                        '</form>';
                                option += '<a class="dropdown-item" href="javascript:void(0);" onclick="return document.getElementById(\'frfad_' + row.fadroom[x1].nomestanza +
                                        '\').submit();"><i class="fa fa-video" style="margin-top:-2px"></i>Apri FAD - CORSO ' + row.fadroom[x1].numerocorso + '</a>' + formnuovo;
                            }


                        }
                        option += '</div></div>';
                        return option;
                    }
                }
                , {
                    targets: 3,
                    type: 'date-it',
                    render: function (data, type, row, meta) {
                        return formattedDate(new Date(data));
                    }
                }, {
                    targets: 4,
                    type: 'date-it',
                    render: function (data, type, row, meta) {
                        return formattedDate(new Date(data));
                    }
                }, {
                    targets: 6,
                    render: function (data, type, row, meta) {
                        return row.allievi_ok + ' - ' + row.allievi_total;
                    }
                }
            ]
        }).columns.adjust();
    };
    return {
        init: function () {
            initTable1();
        }
    };
}();

function refresh() {
    $('html, body').animate({scrollTop: $('#offsetresult').offset().top}, 500);
    load_table($('#kt_table_1'), context + '/QuerySA?type=searchProgetti&cip=' + $('#cip').val()
            + '&stato=' + $('#stato').val(), );
}

function reload() {
    $('html, body').animate({scrollTop: $('#kt_table_1').offset().top}, 500);
    reload_table($('#kt_table_1'));
}

var DatatablesAllievi = function () {
    var initTableAllievi = function () {
        var table = $('#kt_table_allievi');
        table.DataTable({
            dom: `<'row'<'col-sm-12'ftr>><'row'<'col-sm-12 col-md-2'i><'col-sm-12 col-md-10 dataTables_pager'lp>>`,
            lengthMenu: [15, 25, 50],
            language: {
                "lengthMenu": "Mostra _MENU_",
                "infoEmpty": "Mostrati 0 di 0 per 0",
                "loadingRecords": "Caricamento...",
                "search": "Cerca:",
                "zeroRecords": "Nessun risultato trovato",
                "info": "Mostrati _END_ di _TOTAL_ ",
                "emptyTable": "Nessun risultato",
                "sInfoFiltered": "(filtrato su _MAX_ risultati totali)"
            },
            order: [],
            columns: [
                {defaultContent: ''},
                {data: 'nome'},
                {data: 'cognome'},
                {data: 'codicefiscale'},
                {data: 'target',
                    render: function (data, type, row) {
                        switch (row.target) {
                            case "D1":
                                tgt = "Donna inattiva";
                                break;
                            case "D2":
                                tgt = "Disoccupato/a di lunga durata";
                                break;
                            default:
                                tgt = "Non Definito";
                                break;
                        }
                        return tgt;
                    }
                },
                {data: 'statopartecipazione.descrizione'},
                {data: 'esclusione_prg'}
            ],
            drawCallback: function () {
                $('[data-toggle="kt-tooltip"]').tooltip();
            },
            columnDefs: [
                {
                    targets: 0,
                    className: 'text-center',
                    orderable: false,
                    render: function (data, type, row, meta) {
//                        var sp;
                        var option = '<div class="dropdown dropdown-inline">'
                                + '<button type="button" class="btn btn-icon btn-sm btn-icon-md btn-circle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">'
                                + '   <i class="flaticon-more-1"></i>'
                                + '</button>'
                                + '<div class="dropdown-menu dropdown-menu-left">';
                        option += '<a class="dropdown-item" href="javascript:void(0);" onclick="swalDocumentAllievo(' + row.id + ')"><i class="fa fa-file-alt"></i> Visualizza Documenti</a>';

                        if (row.statopartecipazione.id === "01") {
                            if (row.progetto.stato.controllare === 0) {
                                option += '<a class="dropdown-item " href="javascript:void(0);" onclick="swalSigma(' + row.id + ',\'' + row.statopartecipazione.id +
                                        '\')"><i class="fa fa-user-check" data-container="body" data-html="true" data-toggle="kt-tooltip" title="Stato '
                                        + row.statopartecipazione.descrizione + '"></i>Cambia stato di partecipazione</a>';

                            }
                        }
                        option += '</div></div>';
                        return option;
                    }
                }]
        });
    };
    return {
        init: function () {
            initTableAllievi();
        }
    };
}();

function swalTableAllievi(idprogetto) {
    clear_table($('#kt_table_allievi'));
    load_table($('#kt_table_allievi'), context + '/QuerySA?type=searchAllieviProgetti&idprogetto=' + idprogetto);
    $('#allievi_table').modal('show');
    $('#allievi_table').on('shown.bs.modal', function () {
        $('.kt-scroll').each(function () {
            const ps = new PerfectScrollbar($(this)[0]);
        });
        $('#kt_table_allievi').DataTable().columns.adjust();
    });
}

jQuery(document).ready(function () {
    checkStartM4();
    checkConclusionePrg();
    KTDatatablesDataSourceAjaxServer.init();
    DatatablesAllievi.init();
    $('.kt-scroll').each(function () {
        const ps = new PerfectScrollbar($(this)[0]);
    });
    $('.kt-scroll-x').each(function () {
        const ps = new PerfectScrollbar($(this)[0], {suppressScrollY: true});
    });
});

function swalTableDocenti(idprogetto) {
    swal.fire({
        html: '<table class="table table-bordered" id="kt_table_docenti">'
                + '<thead>'
                + '<tr>'
                + '<th class="text-uppercase text-center">Nome</th>'
                + '<th class="text-uppercase text-center">Cognome</th>'
                + '<th class="text-uppercase text-center">Codice Fiscale</th>'
                + '<th class="text-uppercase text-center">Fascia</th>'
                + '</tr>'
                + '</thead>'
                + '</table>',
        width: '95%',
        scrollbarPadding: true,
        showCloseButton: true,
        showCancelButton: false,
        showConfirmButton: false,
        animation: false,
        customClass: {
            popup: 'animated bounceInDown'
        },
        onOpen: function () {
            $("#kt_table_docenti").DataTable({
                dom: `<'row'<'col-sm-12'ftr>><'row'<'col-sm-12 col-md-2'i><'col-sm-12 col-md-10 dataTables_pager'lp>>`,
                lengthMenu: [15, 25, 50],
                language: {
                    "lengthMenu": "Mostra _MENU_",
                    "infoEmpty": "Mostrati 0 di 0 per 0",
                    "loadingRecords": "Caricamento...",
                    "search": "Cerca:",
                    "zeroRecords": "Nessun risultato trovato",
                    "info": "Mostrati _END_ di _TOTAL_ ",
                    "emptyTable": "Nessun risultato",
                    "sInfoFiltered": "(filtrato su _MAX_ risultati totali)"
                },
                scrollY: "40vh",
                ajax: context + '/QuerySA?type=searchDocentiProgetti&idprogetto=' + idprogetto,
                order: [],
                columns: [
                    {data: 'nome'},
                    {data: 'cognome'},
                    {data: 'codicefiscale'},
                    {data: 'fascia.descrizione'}
                ]
            });
        }
    });
}

var registri_aula = new Map();
function swalDocumentPrg(idprogetto) {
    $("#prg_docs").empty();
    $.get(context + "/QuerySA?type=getDocPrg&idprogetto=" + idprogetto, function (resp) {
        var json = JSON.parse(resp);
        var docente;
        var scadenza;
        var doc_prg = getHtml("documento_prg", context);
        $.each(json, function (i, j) {
            docente = j.docente !== null ? " " + j.docente.nome + " " + j.docente.cognome : "";
            scadenza = j.scadenza !== null ? "<br>SCADENZA: " + formattedDate(new Date(j.scadenza)) : "";
            if (j.tipo.visible_sa === 1) {
                var ext = j.tipo.estensione;
                if (ext === null || ext === undefined || typeof ext === 'undefined' || ext === "p7m" || ext.includes("pdf")) {
                    ext = "pdf";
                }
                $("#prg_docs").append(doc_prg.replace("@href", context + "/OperazioniGeneral?type=showDoc&path=" + j.path)
                        .replace("#ex", ext)
                        .replace("@nome",
                                j.nome + docente + scadenza));
            }
        });
        $('#doc_modal').modal('show');
        $('[data-toggle="kt-tooltip"]').tooltip();
        $('.kt-scroll').each(function () {
            const ps = new PerfectScrollbar($(this)[0]);
        });
    });
}

var registri = new Map();
function swalDocumentAllievo(idallievo) {
    $("#prg_docs").empty();
    var giorno;
    //var doc_registro_aula = getHtml("documento_registro", context);
    var doc_prg = getHtml("documento_prg", context);
    $.get(context + "/QuerySA?type=getDocAllievo&idallievo=" + idallievo, function (resp) {
        var json = JSON.parse(resp);
        for (var i = 0; i < json.length; i++) {
            //        registri.set(json[i].id, json[i]);
            giorno = json[i].giorno !== null ? " del " + formattedDate(new Date(json[i].giorno)) : "";
            var ext = json[i].tipo.estensione;
            if (ext === null || ext === undefined || typeof ext === 'undefined' || ext === "p7m" || ext.includes("pdf")) {
                ext = "pdf";
            }
            $("#prg_docs").append(
                    doc_prg.replace("@href",
                            context + "/OperazioniGeneral?type=showDoc&path=" + json[i].path)
                    .replace("#ex", ext)
                    .replace("@nome", json[i].tipo.descrizione + giorno)
                    );
            //  }
        }
        $('#doc_modal').modal('show');
        $('.kt-scroll').each(function () {
            const ps = new PerfectScrollbar($(this)[0]);
        });
    });
}


function confirmNext(id, stato) {
    var msg = "Sicuro di voler inviare a controllo il Progetto Formativo?";
    if (stato === 'FA') {
        msg += "<br><br><b class='kt-font-danger'>Stai per terminare la FASE A.<br>&Egrave; necessario selezionare tutti gli allievi che accederanno alla FASE B; in caso contrario, non potranno essere caricati i registri individuali per gli allievi."
                + "<br><br>Inoltre, una volta terminata La FASE A, non potranno essere caricati altri registri relativi ad essa.</b>"
                + "<br>";
    } else if (stato === 'FB') {
        msg += "<br><br><b class='kt-font-danger'>Stai per terminare la FASE B.<br>"
                + "Inoltre, una volta terminata La FASE B, non potranno essere caricati altri registri individuali relativi ad essa.</b>"
                + "<br><br><b>Ti ricordiamo che non potranno essere effettuate modifiche fino ad avvenuto controllo da parte dell'ente Microcredito.</b>";
    } else {
        msg += "<br><br><b>Ti ricordiamo che non potranno essere effettuate modifiche fino ad avvenuto controllo da parte dell'ente Microcredito.</b>";
    }

    swalConfirm("Conferma Invio Progetto", msg, function goNext() {
        showLoad();
        $.ajax({
            type: "POST",
            url: context + '/OperazioniSA?type=goNext&id=' + id,
            success: function (data) {
                closeSwal();
                var json = JSON.parse(data);
                if (json.result) {
                    swalSuccess("Progetto inviato", "Progetto inviato con successo al controllo del Microcredito");
                    reload();
                } else {
                    swalError("Errore", json.message);
                }
            },
            error: function () {
                swalError("Errore", "Non è stato possibile inviare il progetto a controllo");
            }
        });
    });
}

function setEsito(id, esito) {

    swalConfirm('Cambio esito',
            "Sicuro di voler confermare l'esito, <b class='kt-font-danger'>asseganto alla " + esito + "</b>, per il seguente allievo?",
            function setValueEsito() {
                showLoad();
                $.ajax({
                    type: "POST",
                    url: context + '/OperazioniSA?type=setEsitoAllievo&id=' + id + '&esito=' + esito,
                    success: function (data) {
                        closeSwal();
                        var json = JSON.parse(data);
                        if (json.result) {
                            swalSuccess("Esito allievo", "Esito allievo impostato");
                            reload_table($('#kt_table_allievi'));
                        } else {
                            swalError("Errore", json.message);
                        }
                    },
                    error: function () {
                        swalError("Errore", "Non è stato possibile impostare l'esito per l'attuale allievo");
                    }
                });
            });
}

function checkpm() {
    if ($('#check').is(":checked")) {
        $("#orario2_start").removeAttr("disabled");
        $("#orario2_end").removeAttr("disabled");
        $('#div_pm').css("display", "");
    } else {
        $("#orario2_start").attr("disabled", true);
        $("#orario2_end").attr("disabled", true);
        $('#div_pm').css("display", "none");
    }
}

function remainingHH(total) {
    var hh20 = 20;
    return (hh20 - total);
}

function checkTotalHH(m_s, m_e, p_s, p_e, check, totalms) {
    var err = false;
    var msg = "";
    var hhregistro;
    hhregistro = new Date('00', '00', '00', m_e.split(':')[0], m_e.split(':')[1]).getTime() - new Date('00', '00', '00', m_s.split(':')[0], m_s.split(':')[1]).getTime();
    if (check) {
        hhregistro += new Date('00', '00', '00', p_e.split(':')[0], p_e.split(':')[1]).getTime() - new Date('00', '00', '00', p_s.split(':')[0], p_s.split(':')[1]).getTime();
    }

    if ((hhregistro / 3600000) > remainingHH(totalms)) {
        err = true;
        msg = "Attenzione, il totale delle ore di lezione per la Fase B ha superato le 20 ore.";
    }
    if ((hhregistro / 3600000) > ore_max_daily) {
        err = true;
        msg = "Attenzione, la lezione giornaliera non può superare le 5 ore.";
    }
    if (err) {
        $('#alertmsg').html(msg);
        $('#warning_hh').css("display", "");
        $('input.time').removeClass('is-valid').addClass('is-invalid');
    } else {
        $('#alertmsg').html("");
        $('#warning_hh').css("display", "none");
        $('input.time').removeClass('is-invalid').addClass('is-valid');
    }
    return err ? false : true;
}

function returnTotalHHbyAllievo(idallievo) {
    var totaleore = 0;
    var today = false;
    $.ajax({
        type: "GET",
        async: false,
        url: context + "/OperazioniSA?type=getTotalHoursRegistriByAllievo&idallievo=" + idallievo,
        success: function (resp) {
            var json = JSON.parse(resp);
            if (resp !== null)
                totaleore = json.totale;
            today = json.today;
        }
    });
    return [totaleore, today];
}

function warningSigma() {
    if ($("#sigma").val() !== "-" || $("#sigma").val() !== "01") {
        $('#warning_sp').css("display", "");
        $('#warningmsg').html('Impostando l\'alunno come \'' + $("#sigma option:selected").text() + '\', questo verrà escluso definitivamente.');
    } else {
        $('#warning_sp').css("display", "none");
    }
}

function swalSigma(id, idsp) {
    swal.fire({
        title: 'Stato di partecipazione (Codice SIGMA)',
        html: '<div id="swalDoc">'
                + '<div id="warning_sp" class="form-group kt-font-io-n row col" style="margin-left: 0px;margin-right: 0px; display: none;" ><div class="col-1"><i class="fa fa-exclamation-triangle" style="position: absolute;top: 50%;left: 50%;transform: translate(-50%,-50%);font-size:20px;" ></i></div><div id="warningmsg" class="col-10" ></div><div class="col-1"><i class="fa fa-exclamation-triangle" style="position: absolute;top: 50%;left: 50%;transform: translate(-50%,-50%);font-size:20px;"></i></div></div>'
                + '<div class="select-div" id="sigma_div">'
                + '<select class="form-control kt-select2-general obbligatory" id="sigma" name="sigma"  style="width: 100%" onchange="return warningSigma();">'
                + '<option value="-">Seleziona stato di partecipazione</option>'
                + '</select></div><br>'
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
            $('#sigma').select2({
                dropdownCssClass: "select2-on-top",
                minimumResultsForSearch: -1
            });
            $.get(context + "/QuerySA?type=getSIGMA", function (resp) {
                var json = JSON.parse(resp);
                for (var i = 0; i < json.length; i++) {
                    if (json[i].id === idsp) {
                        $("#sigma").append('<option selected value="' + json[i].id + '">' + json[i].descrizione + '</option>');
                    } else {
                        $("#sigma").append('<option value="' + json[i].id + '">' + json[i].descrizione + '</option>');
                    }
                }
            });
        },
        preConfirm: function () {
            var err = false;
            err = checkObblFieldsContent($('#swalDoc')) ? true : err;
            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "sigma": $('#sigma').val()
                    });
                });
            } else {
                return false;
            }
        }
    }).then((result) => {
        if (result.value) {
            setValueStato(id, result.value.sigma);
        } else {
            swal.close();
        }
    }
    );
}

function setValueStato(id, sigma) {
    showLoad();
    $.ajax({
        type: "POST",
        url: context + '/OperazioniSA?type=setSIGMA&id=' + id + '&sigma=' + sigma,
        success: function (data) {
            closeSwal();
            var json = JSON.parse(data);
            if (json.result) {
                swalSuccess("Codice SIGMA", "Stato di partecipazione assegnato correttamente");
                reload_table($('#kt_table_allievi'));
            } else {
                swalError("Errore", json.message);
            }
        },
        error: function () {
            swalError("Errore", "Non è stato possibile impostare lo stato di partecipazione");
        }
    });
}

function checkStartM4() {
    let temp = "";
    $.ajax({
        type: "GET",
        async: false,
        url: context + "/QuerySA?type=checkModello4Start",
        success: function (resp) {
            if (resp !== null) {
                temp = JSON.parse(resp);
                mapM4_start = new Map(temp.map(i => [i.id_prg, i.max_date_m3]));
            }
        }
    });
    return temp;
}

function checkConclusionePrg() {
    let temp = "";
    $.ajax({
        type: "GET",
        async: false,
        url: context + "/QuerySA?type=checkModello4End",
        success: function (resp) {
            if (resp !== null)
                temp = JSON.parse(resp);
            mapConclusionePrg = new Map(temp.map(i => [i.id_prg, i.max_date_m3]));
        }
    });
    return temp;
}
