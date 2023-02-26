
var typeuser = document.getElementById("searchPFMicro").getAttribute("data-typeuser");
var context = document.getElementById("searchPFMicro").getAttribute("data-context");
$.getScript(context + '/page/partialView/partialView.js', function () {});

let allieviPrg;
let addInputExclusion = 1;
let neetSel;
let itemToReplace;
let neetPromise;

var KTDatatablesDataSourceAjaxServer = function () {
    var initTable1 = function () {
        var table = $('#kt_table_1');
        table.DataTable({
            dom: `<'row'<'col-sm-12'ftr>><'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7 dataTables_pager'lp>>`,
            lengthMenu: [5, 10, 25, 50],
            language: {
                'lengthMenu': 'Mostra _MENU_',
                "infoEmpty": "Mostrati 0 di 0 per 0",
                "loadingRecords": "Caricamento...",
                "search": "Cerca:",
                "zeroRecords": "Nessun risultato trovato",
                "info": "Mostrati _START_ di _TOTAL_ ",
                "emptyTable": "Nessun risultato",
                "sInfoFiltered": "(filtrato su _MAX_ risultati totali)"
            },
            searchDelay: 500,
            ScrollX: "100%",
            sScrollXInner: "110%",
            processing: true,
            pageLength: 10,
            ajax: context + '/QueryMicro?type=searchProgetti&soggettoattuatore=' + $('#soggettoattuatore').val() + '&cip=' + $('#cip').val()
                    + '&stato=' + $('#stato').val() + '&rendicontato=' + $('#rendicontato').val(),
            order: [],
            columns: [
                {defaultContent: ''},
                {data: 'id'},
                {data: 'soggetto.ragionesociale', className: 'text-center text-uppercase'},
                {data: 'start'},
                {data: 'end'},
                {data: 'cip'},
                {defaultContent: ''},
                {data: 'sede.denominazione', className: 'text-center text-uppercase'},
                {data: 'stato.descrizione', className: 'text-center text-uppercase'},
                {data: 'assegnazione', className: 'text-center text-uppercase'}
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
                        if (typeuser === "2") {
                            option += '<a class="dropdown-item" href="javascript:void(0);" onclick="assegna(' + row.id + ')"><i class="fa fa-user"></i> Assegnazione</a>';
                            option += '<a class="dropdown-item" href="javascript:void(0);" onclick="uploadDocGenerico(' + row.id + ')"><i class="fa fa-upload" style="margin-top:-2px"></i> Carica Altra Documentazione</a>';
                            option += '<a class="dropdown-item" href="javascript:void(0);" onclick="caricaexcelcontrollo(' + row.id + ')"><i class="fa fa-file-excel" style="margin-top:-2px"></i>Carica Documento di Controllo</a>';
                            option += '<a class="dropdown-item" href="javascript:void(0);" onclick="modifyDate(' + row.id + ',' + row.start + ',' + row.end + ',' + row.end_fa + ')"><i class="fa fa-calendar-alt"></i> Modifica Date</a>';
                        }
                        option += '<a class="dropdown-item" href="javascript:void(0);" onclick="swalTableAllievi(' + row.id + ')"><i class="flaticon-users-1"></i> Visualizza Allievi</a>';
                        option += '<a class="dropdown-item" href="javascript:void(0);" onclick="swalTableDocenti(' + row.id + ')"><i class="fa fa-chalkboard-teacher"></i> Visualizza Docenti</a>';
                        option += '<a class="dropdown-item" href="javascript:void(0);" onclick="swalDocumentPrg(' + row.id + ')"><i class="fa fa-file-alt"></i> Visualizza Documenti</a>';




                        if (row.stato.id === "P" || row.stato.id === "DC" || row.stato.id === "ATA" || row.stato.id === "ATB" || row.stato.id === "SOA" || row.stato.id === "SOB") {
                            option += '<a class="dropdown-item fancyBoxNoRef" href="showModelli.jsp?id=' + row.id + '"><i class="fa fa-file-alt"></i> Visualizza Lezioni Modelli 3 e 4</a>';
                        }

                        option += '<a class="dropdown-item" href="javascript:void(0);" onclick="swalTableStory(' + row.id + ')"><i class="fa fa-clipboard-list"></i> Visualizza Storico Progetto</a>';

                        if (row.pdfunico !== null) {
                            option += '<a class="dropdown-item" href="javascript:void(0);" onclick="swalPdfUnicoAllievi(' + row.id + ')"><i class="fa fa-file-pdf" style="margin-top:-2px"></i> Scarica PDF per ANPAL</a>';
                        }
                        if (typeuser === "2") {
                            if (row.stato.id === "MA") {
                                option += '<a class="dropdown-item kt-font-success" href="javascript:void(0);" onclick="swalMappaAllievi(' + row.id + ')"><i class="fa fa-check kt-font-success" style="margin-top:-2px"></i> Mappatura</a>';
                            } else if (row.stato.id === "IV") {
                                option += '<a class="dropdown-item" href="javascript:void(0);" onclick="swalMappaAllievi(' + row.id + ')"><i class="fa fa-check" style="margin-top:-2px"></i> Mappatura</a>';
                                option += '<a class="dropdown-item kt-font-success" href="compileCL.jsp?id=' + row.id + '" ><i class="fa fa-file-excel kt-font-success"></i> Compila Checklist Finale</a>';
                            } else if (row.stato.id === "CK") {
                                option += '<a class="dropdown-item" href="javascript:void(0);" onclick="swalMappaAllievi(' + row.id + ')"><i class="fa fa-check" style="margin-top:-2px"></i> Mappatura</a>';
                                option += '<a class="dropdown-item kt-font-success" href="javascript:void(0);" onclick="sendMailEsito(' + row.id + ')"><i class="flaticon2-envelope kt-font-success" style="margin-top:-2px"></i> Invia Esito a ENM</a>';
                            } else if (row.stato.id === "EVI") {
                                option += '<a class="dropdown-item" href="javascript:void(0);" onclick="swalMappaAllievi(' + row.id + ')"><i class="fa fa-check" style="margin-top:-2px"></i> Mappatura</a>';
                                option += '<a class="dropdown-item kt-font-success" href="javascript:void(0);" onclick="uploadEsito(' + row.id + ')"><i class="fa fa-upload kt-font-success" style="margin-top:-2px"></i> Upload Esito Firmato</a>';
                            } else if (row.stato.controllare === 1) {
                                option += '<a class="dropdown-item kt-font-success" href="javascript:void(0);" onclick="valitdatePrg(' + row.id + ',&quot;' + row.stato.id + '&quot;)"><i class="fa fa-check kt-font-success" style="margin-top:-2px"></i> Convalida Progetto</a>';
                                option += '<a class="dropdown-item kt-font-danger" href="javascript:void(0);" onclick="rejectPrg(' + row.id + ')"><i class="flaticon2-delete kt-font-danger" style="margin-top:-2px"></i> Rigetta Progetto</a>';
                            } else if (row.stato.id === "CO") {
                                option += '<a class="dropdown-item" href="javascript:void(0);" onclick="swalMappaAllievi(' + row.id + ')"><i class="fa fa-check" style="margin-top:-2px"></i> Mappatura</a>';
                            }

                            if (
                                    row.stato.id === "ATA" ||
                                    row.stato.id === "ATB" ||
                                    row.stato.id === "F" ||
                                    row.stato.id === "MA" ||
                                    row.stato.id === "IV" ||
                                    row.stato.id === "CK" ||
                                    row.stato.id === "EVI" ||
                                    row.stato.id === "CO" ||
                                    row.stato.id === "SOA" ||
                                    row.stato.id === "SOB"
                                    ) {
                                option += '<a class="dropdown-item kt-font-danger" href="javascript:void(0);" onclick="annullaPrg(' +
                                        row.id + ')"><i class="flaticon2-delete kt-font-danger" style="margin-top:-2px"></i>Annulla Progetto</a>';
                            }
                        }


                        if (row.fadroom !== null) {
                            for (var x1 = 0; x1 < row.fadroom.length; x1++) {
                                var formnuovo = '<form target="_blank" id="frfad_' + row.fadroom[x1].nomestanza + '" method="post" action="' + row.fadlink + '">' +
                                        '<input type="hidden" name="type" value="login_fad_mcn"/> ' +
                                        '<input type="hidden" name="roomname" value="' + row.fadroom[x1].nomestanza + '"/> ' +
                                        '<input type="hidden" name="corso" value="' + row.fadroom[x1].numerocorso + '"/> ' +
                                        '<input type="hidden" name="codfisc" value="' + row.usermc + '"/> ' +
                                        '<input type="hidden" name="progetto" value="' + row.id + '"/> ' +
                                        '<input type="hidden" name="view" value="1"/> ' +
                                        '</form>';
                                option += '<a class="dropdown-item" href="javascript:void(0);" onclick="return document.getElementById(\'frfad_' + row.fadroom[x1].nomestanza +
                                        '\').submit();"><i class="fa fa-video" style="margin-top:-2px"></i>Apri FAD - CORSO ' + row.fadroom[x1].numerocorso + '</a>' + formnuovo;
                            }

                        }

                        option += '</div></div>';
                        return option;

                    }
                },
                {
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
//            scrollY: "40vh",
            scrollX: true,
            sScrollXInner: "110%",
            order: [],
            columns: [
                {defaultContent: ''},
                {data: 'nome', className: 'text-center text-uppercase'},
                {data: 'cognome', className: 'text-center text-uppercase'},
                {data: 'codicefiscale', className: 'text-center text-uppercase'},
                {data: 'target',
                    render: function (data, type, row) {
                        var tgt = "";
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
                {data: 'statopartecipazione.descrizione', className: 'text-center text-uppercase'},
                {data: 'esclusione_prg', className: 'text-center'},
                {data: '', className: 'text-center',
                    render: function (data, type, row) {
                        var quest = "";
                        if (row.surveyin === null || !row.surveyin) {
                            quest += "ING. KO";
                        } else {
                            quest += "ING. OK";
                        }
                        quest += " - ";
                        if (row.surveyout === null || !row.surveyout) {
                            quest += "USC. KO";
                        } else {
                            quest += "USC. OK";
                        }
                        return quest;
                    }},
                {defaultContent: ''}
            ],
            drawCallback: function () {
                $('[data-toggle="kt-tooltip"]').tooltip();
            },
            columnDefs: [
                {
                    targets: 0,
                    className: 'text-center text-uppercase',
                    orderable: false,
                    render: function (data, type, row, meta) {
                        var option = '<div class="dropdown dropdown-inline">'
                                + '<button type="button" class="btn btn-icon btn-sm btn-icon-md btn-circle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">'
                                + '<i class="flaticon-more-1"></i>'
                                + '</button>'
                                + '<div class="dropdown-menu dropdown-menu-left">';
                        option += '<a class="dropdown-item" href="javascript:void(0);" onclick="swalDocumentAllievo(' 
                                + row.id + ');"><i class="fa fa-file-alt"></i> Visualizza Documenti</a>';
                        if (row.statopartecipazione.id === "01") {
                            option += '<a class="dropdown-item " href="javascript:void(0);" onclick="swalSigma(' + row.id + ',\'' 
                                    + row.statopartecipazione.id +
                                    '\')"><i class="fa fa-user-check" data-container="body" data-html="true" data-toggle="kt-tooltip" title="Stato '
                                    + row.statopartecipazione.descrizione + '"></i>Cambia stato di partecipazione</a>';
                        }
                        option += '</div></div>';
                        return option;
                    }
                }, {
                    targets: 8,
                    className: 'text-center',
                    orderable: false,
                    render: function (data, type, row, meta) {
                        var option = '<a target="_blank" href="' + context + '/OperazioniGeneral?type=showDoc&path=' + row.docid + '" class="btn btn-io fa fa-address-card" style="font-size: 20px;"'
                                + 'data-container="body" data-html="true" data-toggle="kt-tooltip"'
                                + 'data-placement="top" title="<h6>Scadenza:</h6><h5>' + formattedDate(new Date(row.scadenzadocid)) + '</h5>"></a>';
                        if (new Date(row.scadenzadocid) <= new Date()) {
                            option = '<a target="_blank" href="' + context + '/OperazioniGeneral?type=showDoc&path=' + row.docid + '" class="btn btn-io-n " style="font-size: 20px"'
                                    + 'data-container="body" data-html="true" data-toggle="kt-tooltip"'
                                    + 'data-placement="top" title="<h6>Scadenza:</h6><h5>'
                                    + formattedDate(new Date(row.scadenzadocid)) + '</h5>">&nbsp;<i class="fa fa-exclamation-triangle"></i></a>';
                        }
                        return option;
                    }
                }]
        });
    };

    var initPdfAllievi = function () {
        var table = $('#kt_table_pdfallievi');
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
//            scrollY: "40vh",
            scrollX: true,
            sScrollXInner: "110%",
            order: [],
            columns: [
                {defaultContent: ''},
                {data: 'nome'},
                {data: 'cognome'},
                {data: 'codicefiscale'},
                {data: 'statopartecipazione.descrizione'}
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
                        var option = '<a target="_blank" href="' + context + '/OperazioniMicro?type=scaricapdfunico&idallievo=' + row.id
                                + '" class="btn btn-icon btn-sm btn-icon-md btn-circle">'
                                + '<i class="flaticon-download"></i>'
                                + '</a>';

                        return option;
                    }
                }]
        });
    };
    var initMappaAllievi = function () {
        var table = $('#kt_table_allievi_mappa');
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
            scrollX: true,
            order: [],
            columns: [
                {data: 'nome'},
                {data: 'cognome'},
                {data: 'codicefiscale'},
                {data: 'statopartecipazione.descrizione'},
                {data: 'orerendicontabili', width: "60px"},
                {defaultContent: ''},
                {defaultContent: ''}
            ],
            drawCallback: function () {
                $('[data-toggle="kt-tooltip"]').tooltip();
            },
            columnDefs: [
                {
                    targets: 5,
                    className: 'text-center',
                    orderable: false,
                    width: "50px",
                    render: function (data, type, row, meta) {
                        if (row.progetto.stato.id === "MA") {
                            var opt1 = '<span class="kt-switch kt-switch--outline kt-switch--icon kt-switch--success">'
                                    + '<label>'
                                    + '<input type="checkbox" data-toggle="toggle" name="mappatura_' + row.id + '" id="mappatura_' + row.id + '"/>'
                                    + '<span></span> </label></span>';
                            return opt1;
                        } else {
                            if (row.mappatura === 1 || row.mappatura === '1') {
                                return "SI";
                            } else {
                                return "NO";
                            }
                        }
                    }
                }, {
                    targets: 6,
                    className: 'text-center',
                    orderable: false,
                    width: "250px",
                    render: function (data, type, row, meta) {
                        if (row.progetto.stato.id === "MA") {
                            $("#salvamappatura").toggle(true);
                            var ta_as = '<textarea class="form-control" placeholder="Inserire eventuali note PRIMA di salvare" name="notesmappatura_'
                                    + row.id + '" id="notesmappatura_' + row.id
                                    + '" rows="3" style="overflow: hidden; overflow-wrap: break-word; resize: none; height: 71px;" onchange="return fieldNOSPecial_2(this.id)"></textarea>';
                            return ta_as;
                        } else {
                            $("#salvamappatura").toggle(false);
                            if (row.mappatura_note === null || row.mappatura_note === 'null') {
                                return "";
                            } else {
                                return row.mappatura_note;
                            }
                        }
                    }
                }
            ]
        });
    };

    return {
        init: function () {
            initTableAllievi();
            initMappaAllievi();
            initPdfAllievi();
        }
    };
}();

function swalSigma(id, idsp) {
    swal.fire({
        title: 'Stato di partecipazione',
        html: '<div id="swalModificaStato">'
                + '<div id="warning_sp" class="form-group kt-font-io-n row col" style="margin-left: 0px;margin-right: 0px; display: none;" ><div class="col-1"><i class="fa fa-exclamation-triangle" style="position: absolute;top: 50%;left: 50%;transform: translate(-50%,-50%);font-size:20px;" ></i></div><div id="warningmsg" class="col-10" ></div><div class="col-1"><i class="fa fa-exclamation-triangle" style="position: absolute;top: 50%;left: 50%;transform: translate(-50%,-50%);font-size:20px;"></i></div></div>'
                + '<div class="select-div" id="sigma_div">'
                + '<select class="form-control kt-select2-general obbligatory" id="sigma" name="sigma"  style="width: 100%" >'
                + '<option value="-">Seleziona stato di partecipazione</option>'
                + '</select></div><br>'
                + '</div>',
        animation: false,
        showCancelButton: true,
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',

        customClass: {
            popup: 'animated bounceInUp',
            cancelButton: "btn btn-io-n",
            confirmButton: "btn btn-io"
        },
        onOpen: function () {
            $('#sigma').select2({
                dropdownCssClass: "select2-on-top",
                minimumResultsForSearch: -1
            });
            $.get(context + "/QueryMicro?type=getSIGMA", function (resp) {
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
            err = checkObblFieldsContent($('#swalModificaStato')) ? true : err;
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
        url: context + '/OperazioniMicro?type=setSIGMA&id=' + id + '&sigma=' + sigma,
        success: function (data) {
            closeSwal();
            console.log(data);
            var json = JSON.parse(data);
            if (json.result) {
                swalSuccess("Modifica Stato Allievo", "Stato di partecipazione impostato correttamente");
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



function mappaprogetto() {
    var idpr = $('#idprmappa').val();
    var content = "";
    var notemap = "";
    $('#kt_table_allievi_mappa input:checkbox').each(function () {
        var idallievo = this.id.replace('mappatura_', '');
        if (this.checked) {
            content += idallievo + ";";
        }
        notemap += idallievo + "###" + $('#notesmappatura_' + idallievo).val() + "$$$";
    });

    $.ajax({
        type: "POST",
        async: false,
        url: context + '/OperazioniMicro?type=mappatura&idprogetto=' + idpr,
        data: {idpr: idpr, allievimappati: content, notemappatura: notemap},
        success: function (resp) {
            var json = JSON.parse(resp);
            if (json.result) {
                $('#allievi_table_mappa').modal('hide');
                reload();
                swalSuccess("Progetto mappato!", "Operazione effettuata con successo.");
            } else {
                swalError("Errore!", json.message);
            }
        }
    });

}

jQuery(document).ready(function () {
    KTDatatablesDataSourceAjaxServer.init();
    DatatablesAllievi.init();
    $('.kt-scroll').each(function () {
        const ps = new PerfectScrollbar($(this)[0]);
    });
    $('.kt-scroll-x').each(function () {
        const ps = new PerfectScrollbar($(this)[0], {suppressScrollY: true});
    });

});

function refresh() {
    $('html, body').animate({scrollTop: $('#offsetresult').offset().top}, 500);
    load_table($('#kt_table_1'), context + '/QueryMicro?type=searchProgetti&soggettoattuatore=' + $('#soggettoattuatore').val() + '&cip=' + $('#cip').val()
            + '&stato=' + $('#stato').val() + '&rendicontato=' + $('#rendicontato').val());
}

function reload() {
    $('html, body').animate({scrollTop: $('#offsetresult').offset().top}, 500);
    reload_table($('#kt_table_1'));
}

function swalTableAllievi(idprogetto) {
    clear_table($('#kt_table_allievi'));
    load_table($('#kt_table_allievi'), context + '/QueryMicro?type=searchAllieviProgetti&idprogetto=' + idprogetto);
    $('#allievi_table').modal('show');
    $('#allievi_table').on('shown.bs.modal', function () {
        $('.kt-scroll').each(function () {
            const ps = new PerfectScrollbar($(this)[0]);
        });
        $('#kt_table_allievi').DataTable().columns.adjust();
        $(".dataTables_scrollHead").css("overflow", "visible");
    });
}

function swalPdfUnicoAllievi(idprogetto) {
    clear_table($('#kt_table_pdfallievi'));
    load_table($('#kt_table_pdfallievi'), context + '/QueryMicro?type=searchAllieviProgetti&idprogetto=' + idprogetto);
    $('#pdfallievi_table').modal('show');
    $('#pdfallievi_table').on('shown.bs.modal', function () {
        $('.kt-scroll').each(function () {
            const ps = new PerfectScrollbar($(this)[0]);
        });
        $('#kt_table_pdfallievi').DataTable().columns.adjust();
        $(".dataTables_scrollHead").css("overflow", "visible");
    });
}

function swalMappaAllievi(idprogetto) {
    clear_table($('#kt_table_allievi_mappa'));
    load_table($('#kt_table_allievi_mappa'), context + '/QueryMicro?type=searchMappaAllievi&idprogetto=' + idprogetto);
    $('#idprmappa').val(idprogetto);
    $('#allievi_table_mappa').modal('show');
    $('#allievi_table_mappa').on('shown.bs.modal', function () {
        $('.kt-scroll').each(function () {
            const ps = new PerfectScrollbar($(this)[0]);
        });
        $('#kt_table_allievi_mappa').DataTable().columns.adjust();
        $(".dataTables_scrollHead").css("overflow", "visible");
    });
}

var registri_aula = new Map();
function swalDocumentPrg(idprogetto) {
    $("#prg_docs").empty();
    var docente;
    var scadenza;
    $.get(context + "/QueryMicro?type=getDocPrg&idprogetto=" + idprogetto, function (resp)
    {
        var json = JSON.parse(resp);
        var doc_prg = getHtml("documento_prg", context);
        $.each(json, function (i, j) {
            docente = j.docente !== null ? " " + j.docente.nome + " " + j.docente.cognome : "";
            scadenza = j.scadenza !== null ? "<br>SCADENZA: " + formattedDate(new Date(j.scadenza)) : "";
            var ext = j.tipo.estensione;
            if (ext === null || ext === undefined || typeof ext === 'undefined' || ext === "p7m" || ext.includes("pdf") || ext.includes("p7m")) {
                ext = "pdf";
            } else if (ext.includes("xls")) {
                ext = "excel";
            }
            $("#prg_docs").append(doc_prg
                    .replace("@href", context + "/OperazioniGeneral?type=showDoc&path=" + j.path)
                    .replace("#ex", ext)
                    .replace("@nome",
                            j.nome + docente + scadenza)
                    );
        });
        $('#doc_modal').modal('show');
        $('[data-toggle="kt-tooltip"]').tooltip();
    });
}

var registri = new Map();
function swalDocumentAllievo(idallievo) {
    $("#prg_docs").empty();
    var giorno, color;
    //var doc_registro_aula = getHtml("documento_registro", context);
    var doc_prg = getHtml("documento_prg", context);
    $.get(context + "/QueryMicro?type=getDocAllievo&idallievo=" + idallievo, function (resp) {
        var json = JSON.parse(resp);
        for (var i = 0; i < json.length; i++) {
            //registri.set(json[i].id, json[i]);
            giorno = json[i].giorno !== null ? " del " + formattedDate(new Date(json[i].giorno)) : "";
            //color = json[i].allievo.progetto.stato.controllare == 0 ? "io" : (json[i].orericonosciute == null ? "warning" : "success");
            //if (json[i].giorno != null) {
            //  registri_aula.set(json[i].id, json[i]);
            //  $("#prg_docs").append(doc_registro_aula.replace("@href", context + "/OperazioniGeneral?type=showDoc&path=" + json[i].path)
            //          .replace("@func", "showRegistro(" + json[i].id + "," + json[i].allievo.progetto.stato.controllare + ")")
            //          .replace("@nome", json[i].tipo.descrizione + giorno)
            //          .replace("#color", color));
            //} else {
            //  
            var ext = json[i].tipo.estensione;
            if (ext === null || ext === undefined || typeof ext === 'undefined' || ext === "p7m" || ext.includes("pdf")) {
                ext = "pdf";
            }
            $("#prg_docs").append(doc_prg.replace("@href", context + "/OperazioniGeneral?type=showDoc&path=" + json[i].path)
                    .replace("#ex", ext)
                    .replace("@nome", json[i].tipo.descrizione + giorno));
            //}
        }
        $('#doc_modal').modal('show');
    });
}

function showRegistro(idregistro, controllare) {
    var registro = registri.get(idregistro);
    var doc_registro = "";
    var totalhh = calculateHoursRegistro(registro.orariostart_mattina, registro.orarioend_mattina, registro.orariostart_pom, registro.orarioend_pom).replace(":0", ":00");
    totalhh = totalhh.indexOf(":") === 1 ? "0" + totalhh : totalhh;

    if (registro.orariostart_pom !== null) {
        doc_registro += getHtml("doc_registro_individiale_pomeriggio", context);
        doc_registro = doc_registro.replace("@start_pome", formattedTime(registro.orariostart_pom).replace(":0", ":00"))
                .replace("@end_pome", formattedTime(registro.orarioend_pom).replace(":0", ":00"));
    } else {
        doc_registro = getHtml("doc_registro_individiale_mattina", context);
    }
    doc_registro = controllare == 1 ? (doc_registro + getHtml("doc_registro_individiale_ctrlore", context)) : doc_registro;
    doc_registro = doc_registro.replace("@start_pome", formattedTime(registro.orariostart_pom).replace(":0", ":00"))
            .replace("@date", formattedDate(new Date(registro.giorno)))
            .replace("@docente", registro.docente.cognome + " " + registro.docente.nome)
            .replace("@start_mattina", formattedTime(registro.orariostart_mattina).replace(":0", ":00"))
            .replace("@end_mattina", formattedTime(registro.orarioend_mattina).replace(":0", ":00"))
            .replace("@tot_ore", totalhh);
    ;
    doc_registro = registro.orericonosciute == null ? doc_registro.replace('@hh', totalhh).replace('@max', totalhh).replace("@msg", "Ore da riconoscere") : doc_registro.replace('@hh', doubletoHHmm(registro.orericonosciute)).replace('@max', totalhh).replace("@msg", "Ore riconosciute");

    swal.fire({
        title: 'Informazioni Registro',
        html: doc_registro,
        animation: false,
        showCancelButton: false,
        confirmButtonText: '<i class="fa fa-check"></i> Conferma ore',
        showConfirmButton: controllare == 1,
        showCloseButton: true,
        customClass: {
            popup: 'animated bounceInUp',
            container: 'my-swal'
        },
        onOpen: function () {
            $(document).off('focusin.modal');
        },
        preConfirm: function () {
            var err = false;
            err = checkObblFieldsContent($('#doc_registro_individiale_ctrlore')) ? true : err;
            err = !checkTime($("#orericonosciute"), totalhh) ? true : err;
            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "orericonosciute": $('#orericonosciute').val(),
                        "id": idregistro
                    });
                });
            } else {
                return false;
            }
        },
    }).then((result) => {
        if (result.value) {
            showLoad();
            $.ajax({
                url: context + "/OperazioniMicro?type=setHoursRegistro",
                type: 'POST',
                data: result.value,
                error: function () {
                    closeSwal();
                    swalError("Errore", "Riprovare, se l'errore persiste contattare l'assistenza");
                },
                success: function (resp) {
                    var json = JSON.parse(resp);
                    closeSwal();
                    if (json.result) {
                        swalDocumentAllievo(registro.allievo.id);
                        swalSuccess("Ore Convalidate", "Le ore del registro sono state convalidate con successo");
                    } else {
                        swalError("Errore!", json.message);
                    }
                }
            });
        } else {
            swal.close();
        }
    });
}

function swalTableStory(idprogetto) {
    swal.fire({
        html: '<table class="table table-bordered" id="kt_table_story">'
                + '<thead>'
                + '<tr>'
                + '<th class="text-uppercase text-center">Descrizione</th>'
                + '<th class="text-uppercase text-center">Data</th>'
                + '<th class="text-uppercase text-center">Stato</th>'
                + '</tr>'
                + '</thead>'
                + '</table>',
        width: '80%',
        scrollbarPadding: true,
        showCloseButton: true,
        showCancelButton: false,
        showConfirmButton: false,
        onOpen: function () {
            $("#kt_table_story").DataTable({
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
                ajax: context + '/QueryMicro?type=getStoryPrg&idprogetto=' + idprogetto,
                order: [],
                columns: [
                    {data: 'motivo'},
                    {data: 'data'},
                    {data: 'stato.descrizione'}
                ],
                columnDefs: [
                    {
                        targets: 1,
                        type: 'date-it',
                        render: function (data, type, row, meta) {
                            return formattedDate(new Date(data));
                        }
                    }]
            });
        }
    });
}

function generateCIP(id) {
    var cip = "N_" + id + "_C1";
    $.ajax({
        type: "POST",
        async: false,
        url: context + '/QueryMicro?type=generatecip&idprogetto=' + id,
        success: function (data) {
            cip = data;
        },
        error: function () {
            cip = "";
        }
    });
    return cip;
}

//UPLOAD DOC CONTROLLO - 11/11/2021
function caricaexcelcontrollo(id) {
    var swalDocControllo = getHtml("swalDocControllo", context).replace("@func",
            "checkFileExtAndDim(&quot;xls,xlsx&quot;)").replace("@mime",
            'application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
    swal.fire({
        title: 'Carica Documento di Controllo',
        html: swalDocControllo,
        animation: false,
        width: '40%',
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
                    if (e.target.files[0].name.length > 30) {
                        $('#label_doc').html(e.target.files[0].name.substring(0, 30) + "...");
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
            err = !checkRequiredFileContent($('#swalDocControllo')) ? true : err;
            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "file": $('#file')[0].files[0],
                        "target": $('#targetfile').val()
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
            fdata.append("target", result.value.target);
            uploadexcelcontrollo(id, fdata);
        } else {
            swal.close();
        }
    });
}

function uploadexcelcontrollo(id, fdata) {
    $.ajax({
        type: "POST",
        url: context + '/OperazioniMicro?type=caricadocumentocontrollo&idpr=' + id,
        data: fdata,
        processData: false,
        contentType: false,
        success: function (data) {
            closeSwal();
            var json = JSON.parse(data);
            if (json.result) {
                swalSuccessReload("Operazione Effettuata!", "Documento caricato con successo.");
            } else {
                swalError("Errore", json.message);
            }
        },
        error: function () {
            swalError("Errore", "Non è stato possibile caricare il documento.");
        }
    });
}

//UPLOAD DOC GENERICO - 04-10-21
function uploadDocGenerico(id) {
    var swalDoc = getHtml("swalDoc", context).replace("@func",
            "checkFileExtAndDim(&quot;pdf,p7m&quot;)").replace("@mime", 'application/pkcs7-mime,application/pdf');
    swal.fire({
        title: 'Carica Altra Documentazione',
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
                    if (e.target.files[0].name.length > 30) {
                        $('#label_doc').html(e.target.files[0].name.substring(0, 30) + "...");
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
        }
    }).then((result) => {
        if (result.value) {
            showLoad();
            var fdata = new FormData();
            fdata.append("file", result.value.file);
            uploadnewdoc(id, fdata);
        } else {
            swal.close();
        }
    });
}



function uploadnewdoc(id, fdata) {
    $.ajax({
        type: "POST",
        url: context + '/OperazioniMicro?type=caricanuovodocumento&idpr=' + id,
        data: fdata,
        processData: false,
        contentType: false,
        success: function (data) {
            closeSwal();
            var json = JSON.parse(data);
            if (json.result) {
                swalSuccessReload("Operazione Effettuata!", "Documento caricato con successo.");
            } else {
                swalError("Errore", json.message);
            }
        },
        error: function () {
            swalError("Errore", "Non è stato possibile caricare il documento.");
        }
    });
}
////////////////////////////////////////////

function uploadEsito(id) {
    var swalDoc = getHtml("swalDoc", context).replace("@func", "checkFileExtAndDim(&quot;pdf&quot;,&quot;p7m&quot;)").replace("@mime", 'application/pkcs7-mime,application/pdf');
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
                if (e.target.files.length !== 0) {
                    if (e.target.files[0].name.length > 30) {
                        $('#label_doc').html(e.target.files[0].name.substring(0, 30) + "...");
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
        }
    }).then((result) => {
        if (result.value) {
            showLoad();
            var fdata = new FormData();
            fdata.append("file", result.value.file);
            cambiaDocEsito(id, fdata);
        } else {
            swal.close();
        }
    });
}

function cambiaDocEsito(id, fdata) {
    $.ajax({
        type: "POST",
        url: context + '/OperazioniMicro?type=caricaesitovalutazione&idpr=' + id,
        data: fdata,
        processData: false,
        contentType: false,
        success: function (data) {
            closeSwal();
            var json = JSON.parse(data);
            if (json.result) {
                swalSuccessReload("Progetto Concluso!", "esito di valutazione caricato con successo.");
            } else {
                swalError("Errore", json.message);
            }
        },
        error: function () {
            swalError("Errore", "Non è stato possibile caricare l'esito di valutazione.");
        }
    });
}

function sendMailEsito(id) {
    var html = "<div class='form-group' id='swal_mailR'>"
            + "<label>Indirizzo Mail destinatario:</label>"
            + "<input class='form-control obbligatory' id='mailR' name='mailR' />"
            + "</div>";
    html += '<div class="dropdown-divider"></div>';

    html += "<div class='form-group' id='swal_mailccR'>"
            + "<label>Indirizzo/i Mail CC: (è possibile inserire un massimo di 3 indirizzi; separati da virgola)</label>"
            + "<input class='form-control' id='mailccR' name='mailccR'>"
            + "</div>";

    swal.fire({
        title: '<h2 class="kt-font-io-n"><b>Invio Esito Valutazione a ENM</b></h2><br>',
        html: html,
        animation: false,
        showCancelButton: true,
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        width: '750px',
        customClass: {
            popup: 'animated bounceInUp'
        },
        onOpen: function () {
            $('#mailR').on("change", function () {
                checkEmail($('#mailR'));
            });
            $('#mailccR').on("change", function () {
                checkEmail_CC($('#mailccR'), 3);
            });
        },
        preConfirm: function () {


            var mailok1 = !checkEmail($('#mailR'));
            var mailok2 = !checkEmail_CC($('#mailccR'));

            if (mailok1 && mailok2) {
                return new Promise(function (resolve) {
                    resolve({
                        "mail": $('#mailR').val(),
                        "mailcc": $('#mailccR').val()
                    });
                });
            } else {
                return false;
            }

        }
    }).then((result) => {
        if (result.value) {
            sendmailesito(id, result.value);
        } else {
            swal.close();
        }
    });

}

function sendmailesito(id, result) {
    showLoad();
    $.ajax({
        type: "POST",
        url: context + '/OperazioniMicro?type=sendmailesitovalutazione&idpr=' + id,
        data: result,
        success: function (data) {
            closeSwal();
            var json = JSON.parse(data);
            if (json.result) {
                reload();
                swalSuccess("Esito Valutazione Inviato", "Operazione effettuata correttamente.");
            } else {
                swalError("Errore", json.message);
            }
        },
        error: function () {
            swalError("Errore", "Non è stato possibile inviare l'esito valutazione.");
        }
    });
}

function valitdatePrg(id, stato) {
    var html = "<h4 style='text-align:center;'>Sicuro di voler validare il Progetto Formativo?</h4>";
    if (stato === "DC") {
        var cip = generateCIP(id);
        html = "<div class='form-group' id='swal_cip'>"
                + "<label>CIP:</label>"
                + "<input class='form-control obbligatory' id='new_cip' name='cip' placeholder='Codice Identificativo Percorso (CIP)' readonly value='" + cip + "' />"
                + "</div>";
    } else if (stato === "DV") {
        allieviPrg = getNeetsByPrg(id);
        //13/07/21 Possibilità di escludere anche tutti i NEETS dal PF (in caso di numero minimo non raggiunto lo stesso verrà rigettato)        
//        if (allieviPrg.length > 4) {
        html = getHtml("swal_escludi_neet", context);
//        }
    }

    swal.fire({
        title: '<h2 class="kt-font-io-n"><b>Valida Progetto</b></h2><br>',
        html: html,
        animation: false,
        showCancelButton: true,
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        width: '750px',
        customClass: {
            popup: 'animated bounceInUp'
        },
        onOpen: function () {
            if (stato === "DV") {
                checkLimitAlunni();
            }
        },
        preConfirm: function () {
            if (stato === "DC") {
                var err = false;
                err = checkObblFieldsContent($('#swal_cip')) ? true : err;
                if (!err) {
                    return new Promise(function (resolve) {
                        resolve({
                            "cip": $('#new_cip').val()
                        });
                    });
                } else {
                    return false;
                }
            } else if (stato === "DV") {
                var err = false;
                err = checkObblFieldsContent($('#swal_escludi_neet')) ? true : err;
                if (!err) {
                    neetPromise = [];
                    $("select[id^=neet_]").each(function () {
                        let actual = {
                            "neet": $(this).find("option:selected").val(),
                            "motivo": $("#motivo_" + $(this).prop("id").split("_")[1]).val(),
                        }
                        neetPromise.push(actual);
                    });
                    return new Promise(function (resolve) {
                        resolve({
                            "neets": JSON.stringify(neetPromise)
                        });
                    });
                } else {
                    return false;
                }
            }
        }
    }).then((result) => {
        if (result.value) {
            validate(id, result.value);
        } else {
            swal.close();
        }
    });
}

function validate(id, result) {
    showLoad();
    $.ajax({
        type: "POST",
        url: context + '/OperazioniMicro?type=validatePrg&id=' + id,
        data: result,
        success: function (data) {
            closeSwal();
            var json = JSON.parse(data);
            if (json.result) {
                reload();
                if (json.message == null) {
                    swalSuccess("Progetto Validato", "Progetto formativo validato con successo");
                } else {
                    swalSuccess("Progetto Rigettato", json.message);
                }
            } else {
                swalError("Errore", json.message);
            }
        },
        error: function () {
            swalError("Errore", "Non è stato possibile validare il progetto formativo");
        }
    });
}

function rejectPrg(id) {
//    var html = "<div class='form-group' id='swal_motivo'>" +
//                "<div class='select-div' id='studenti_div'><select class='form-control kt-select2-general obbligatory' id='studenti' name='studenti' style='width: 100%'></select></div>"
//                + "<textarea class='form-control obbligatory' id='motivo' placeholder='Motivazione del rigetto'></textarea>"
//                + "</div>";
    var html = "<div class='form-group' id='swal_motivo'><textarea class='form-control obbligatory' id='motivo' placeholder='Motivazione del rigetto'></textarea></div>";
    swal.fire({
        title: '<h2 class="kt-font-io-n"><b>Segnala Progetto Errato</b></h2><br>',
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
//        onOpen: function () {
//            $('#studenti').select2({
//                dropdownCssClass: "select2-on-top",
//                minimumResultsForSearch: -1,
//            });
//            $.get(context + "/QuerySA?type=getDocentiByPrg&idprogetto=" + id, function (resp) {
//                var json = JSON.parse(resp);
//                for (var i = 0; i < json.length; i++) {
//                    $("#docente").append('<option value="' + json[i].id + '">' + json[i].cognome + " " + json[i].nome + '</option>');
//                }
//            });
//        },
        preConfirm: function () {
            var err = false;
            err = checkObblFieldsContent($('#swal_motivo')) ? true : err;
            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "motivo": $('#motivo').val(),
                    });
                });
            } else {
                return false;
            }
        },
    }).then((result) => {
        if (result.value) {
            reject(id, result.value);
        } else {
            swal.close();
        }
    });
}

function reject(id, result) {
    showLoad();
    $.ajax({
        type: "POST",
        url: context + '/OperazioniMicro?type=rejectPrg&id=' + id,
        data: result,
        success: function (data) {
            closeSwal();
            var json = JSON.parse(data);
            if (json.result) {
                reload();
                swalSuccess("Progetto Errato Segnalato", json.message != null ? json.message : "Progetto formativo segnalato con successo");
            } else {
                swalError("Errore", json.message);
            }
        },
        error: function () {
            swalError("Errore", "Non è stato possibile segnalare il progetto formativo");
        }
    });
}

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
                ajax: context + '/QueryMicro?type=searchDocentiProgetti&idprogetto=' + idprogetto,
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

function showRegistroAula(id, control, idprogetto) {
    var registro = registri_aula.get(id);
    var doc = getHtml("doc_registro_aula", context);
    var presenze = getHtml("div_presenza", context);

    $('#register_docs_modal').empty();

    var form = "<form id='kt_form_reg' action='" + context + "/OperazioniMicro?type=validateHourRegistroAula' mothod='post'>"
            + "<input type='hidden' value='" + id + "' name='id'>"
            + "<input type='hidden' value='" + idprogetto + "' id='idprogetto'>";
    var end_form = "<div class='input-group'><a class='btn btn-io' id='submit_reg' href='javascript:void(0);'>Convalida</a></div></form>";

    $('#register_docs_modal').append((control == 1 ? form : "") +
            doc.replace("@date", formattedDate(new Date(registro.giorno)))
            .replace("@start", formattedTime(registro.orariostart).replace(":0", ":00"))
            .replace("@end", formattedTime(registro.orarioend).replace(":0", ":00"))
            .replace("@docente", registro.docente.cognome + " " + registro.docente.nome)
            .replace("@ore", doubletoHHmm(registro.ore))
            .replace("@ore_conv", registro.ore_convalidate == "00:00" ? "" : doubletoHHmm(registro.ore_convalidate))
            .replace("@readonly", control == 1 ? "" : "readonly")
            .replace("@presenti",
                    registro.presenti_list.map(p => {
                        return presenze.replace("@nome", p.cognome + " " + p.nome)
                                .replace("@in", formattedTime(p.start).replace(":0", ":00"))
                                .replace("@out", formattedTime(p.end).replace(":0", ":00"))
                                .replace("@ore", doubletoHHmm(p.ore))
                                .replace("@max", p.ore)
                                .replace("@readonly", control == 1 ? "" : "readonly")
                                .replace("@ore_riconosciute", p.ore_riconosciute == "00:00" ? "" : doubletoHHmm(p.ore_riconosciute))
                                .replace("@id", p.id);
                    }))
            .split(",").join("")
            + (control == 1 ? end_form : ""));

    $('#register_modal').modal('show');

    controlHourRegister();

    $("#submit_reg").click(function () {
        if (!checkObblFieldsContent($("#kt_form_reg")) && !checkAllHour()) {
            showLoad();
            $("#kt_form_reg").ajaxSubmit({
                error: function () {
                    closeSwal();
                    swalError("Errore", "Riprovare, se l'errore persiste contattare l'assistenza");
                },
                success: function (resp) {
                    var json = JSON.parse(resp);
                    closeSwal();
                    if (json.result) {
                        swalDocumentPrg(Number($("#idprogetto").val()));
                        swalSuccess("Ore Convalidate", "Le ore del registro sono state convalidate con successo");
                    } else {
                        swalError("Errore!", json.message);
                    }
                }
            });
        }
    });

}

function confirmNext(id, stato) {
    var msg = "Sicuro di voler archiviare questo progetto formativo?"
            + "<br><br> Archiviando il progetto potrà essere scaricato il pacchetto con tutti i files da inviare.";

    swal.fire({
        title: '<h2 class="kt-font-io-n"><b>Conferma Archiviazione Progetto</b></h2><br>',
        html: "<h4 style='text-align:left;'>" + msg + "</h4>",
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
            goNext(id);
        } else {
            swal.close();
        }
    });
}

function goNext(id) {
    showLoad();
    $.ajax({
        type: "POST",
        url: context + '/OperazioniMicro?type=validatePrg&id=' + id,
        success: function (data) {
            closeSwal();
            var json = JSON.parse(data);
            if (json.result) {
                swalSuccess("Progetto Archiviato", json.message != null ? json.message : "Progetto archiviato con successo");
                reload();
            } else {
                swalError("Errore", json.message);
            }
        },
        error: function () {
            swalError("Errore", "Non è stato possibile archiviare il progetto");
        }
    });
}//	

function downloadArchive(id, cip) {
    showLoad();
    $.ajax({
        type: "POST",
        url: context + '/OperazioniMicro?type=downloadTarGz_only',
        data: {id: id},
        success: function (data) {
            closeSwal();
            download(cip + ".tar.gz", data, "application/x-tar");
        },
        error: function () {
            swalError("Errore", "Non è stato possibile scaricare i files");
        }
    });
}

//ASSEGNAZIONE 04-10-21
function getAssegnazione(id) {
    var cip = "N_" + id + "_C1";
    $.ajax({
        type: "POST",
        async: false,
        url: context + '/QueryMicro?type=verificaassegnazione&idprogetto=' + id,
        success: function (data) {
            cip = data;
        },
        error: function () {
            cip = "";
        }
    });
    return cip;
}

function assegna(id) {
    var titolo = '<h2 class="kt-font-io-n"><b>Assegnazione Progetto Formativo</b></h2><br>';
    var html = "<div class='form-group-marginless' id='swal_assegnazione'>"
            + "<input class='form-control obbligatory' id='assegnazione' placeholder='Assegna Progetto A' maxlength='30' value = \""
            + getAssegnazione(id) + "\" />"
            + "</div>";
    swal.fire({
        title: titolo,
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
            var err = false;
            err = checkObblFieldsContent($('#swal_assegnazione')) ? true : err;
            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "assegnazione": $('#assegnazione').val()
                    });
                });
            } else {
                return false;
            }
        }
    }).then((result) => {
        if (result.value) {
            assegnaPrg(id, result.value);
        } else {
            swal.close();
        }
    });

}

function assegnaPrg(id, result) {
    showLoad();
    $.ajax({
        type: "POST",
        url: context + '/OperazioniMicro?type=assegnaPrg&id=' + id,
        data: result,
        success: function (data) {
            closeSwal();
            var json = JSON.parse(data);
            if (json.result) {
                reload();
                swalSuccess("Assegnazione progetto", "Progetto formativo assegnato con successo");
            } else {
                swalError("Errore", json.message);
            }
        },
        error: function () {
            swalError("Errore", "Non è stato possibile validare il progetto formativo");
        }
    });
}

////////////////////////////////////////////

function modifyDate(id, start, end, fb) {
    swal.fire({
        title: '<h2 class="kt-font-io-n"><b>Modifica Date</b></h2><br>',
        html: "<div class='form-group' id='mod_date'>"
                + '<label>Date Inizio e Fine Progetto</label>'
                + '<input type="text" class="form-control obbligatory" id="kt_daterange"  name="date_progetto" autocomplete="off"><br>'
                + (fb !== null ? '<label>Data Fine Fase A e Inizio Fase B</label>' : '')
                + (fb !== null ? "<input class='form-control dp obbligatory' id='data_s_fb' name='data_s_fb' placeholder='data protocollo' value='" + formattedDate(new Date(fb)) + "'>" : "")
                + "<div class='form-group' id='data_err'></div>"
                + "</div>",
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
            $('#kt_daterange').daterangepicker({
                autoApply: true,
                startDate: new Date(start),
                endDate: new Date(end),
                locale: {
                    format: 'DD/MM/YYYY',
                    "daysOfWeek": [
                        "Do",
                        "Lu",
                        "Ma",
                        "Me",
                        "Gi",
                        "Ve",
                        "Sa"
                    ],
                    "monthNames": [
                        "Gen",
                        "Feb",
                        "Mar",
                        "Apr",
                        "Mag",
                        "Giu",
                        "Lug",
                        "Ago",
                        "Set",
                        "Ott",
                        "Nov",
                        "Dic"
                    ], }, });

            $('input.dp').datepicker({
                rtl: KTUtil.isRTL(),
                orientation: "bottom left",
                todayHighlight: true,
                autoclose: true,
                format: 'dd/mm/yyyy',
            });
        },
        preConfirm: function () {
            var err = false;
            err = checkObblFieldsContent($('#mod_date')) ? true : err;
            if (fb != null) {
                var split = $("#kt_daterange").val().split("-");
                var start = getDate(split[0].trim());
                var end = getDate(split[1].trim());
                var data_fb = getDate($("#data_s_fb").val());
                if (start > data_fb || data_fb > end) {
                    err = true;
                    $("#data_err").empty();
                    $("#kt_daterange").removeClass("is-valid").addClass("is-invalid")
                    $("#data_s_fb").removeClass("is-valid").addClass("is-invalid")
                    $("#data_err").append('<label class="kt-font-danger">Data inizio Fase B errata, fuori range.</label>');
                }
            }
            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "id": id,
                        "date": $("#kt_daterange").val(),
                        "fb": ($("#data_s_fb").val() != undefined ? $("#data_s_fb").val() : null),
                    });
                });
            } else {
                return false;
            }
        },
    }).then((result) => {
        showLoad();
        if (result.value) {
            $.post(context + '/OperazioniMicro?type=updateDateProgetto', result.value, function (data) {
                if (data.result) {
                    swalSuccess("Progetto Aggiornato", "Date del progetto aggiornate con successo");
                    reload();
                }
            });
        } else {
            swal.close();
        }
    });
}

function getDate(data) {
    var arr = data.split("/");
    return new Date(arr[2] + "/" + arr[1] + "/" + arr[0]);
}

function rendiconta(idPrg) {
    swalConfirm("Rendiconta Progetto", "Sicuro di voler rendicontare questo progetto?", function rendicontaPrg() {
        showLoad();
        $.ajax({
            type: "POST",
            url: context + '/OperazioniMicro?type=rendicontaProgetto&id=' + idPrg,
            success: function (data) {
                closeSwal();
                if (data.result) {
                    swalSuccess('Successo', 'Progetto rendicontato con successo')
                    reload();
                } else {
                    swalError('Errore', data.message);
                }
            }
        });
    });
}

function liquida(id) {
    var html = "<div class='form-group-marginless' id='swal_liquida'>"
            + "<input class='form-control obbligatory' id='importo' placeholder='importo ente'>"
            + "</div>";
    swal.fire({
        title: '<h2 class="kt-font-io-n"><b>Liquida Progetto</b></h2><br>',
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
        onOpen: function () {
            $("#importo").inputmask('€ 999.999.999,99', {
                numericInput: true
            });
        },
        preConfirm: function () {
            var err = false;
            err = checkObblFieldsContent($('#swal_liquida')) ? true : err;
            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "importo": $('#importo').val(),
                    });
                });
            } else {
                return false;
            }
        },
    }).then((result) => {
        if (result.value) {
            liquidaPrg(id, result.value);
        } else {
            swal.close();
        }
    });
}

function liquidaPrg(id, result) {
    showLoad();
    $.ajax({
        type: "POST",
        url: context + '/OperazioniMicro?type=liquidaPrg&id=' + id,
        data: result,
        success: function (data) {
            closeSwal();
            var json = JSON.parse(data);
            if (json.result) {
                reload();
                swalSuccess("Progetto Validato", "Progetto formativo validato con successo");
            } else {
                swalError("Errore", json.message);
            }
        },
        error: function () {
            swalError("Errore", "Non è stato possibile validare il progetto formativo");
        }
    });
}

function getNeetsByPrg(id) {
    let temp = "";
    $.ajax({
        type: "GET",
        async: false,
        url: context + "/QueryMicro?type=getAllieviByPrg&id=" + id,
        success: function (resp) {
            if (resp != null)
                temp = JSON.parse(resp);
        }
    });
    return temp;
}

function reEvaluateOptions() {
    $('select[id*="neet_"] option').prop('disabled', false);

    $('select[id*="neet_"]').each(function () {
        var $this = $(this);
        $('select[id*="neet_"]').not($this).find('option').each(function () {
            if ($(this).attr('value') == $this.val()) {
                $(this).prop('disabled', true);
            }
        });
    });
    setTimeout(function () {
        $('select[id*="neet_"]').select2("destroy").select2({
            dropdownCssClass: "select2-on-top",
            minimumResultsForSearch: -1,
        });
    }, 0);
}

function remove_excludeNeet(id) {
    $('#exclude_row_' + id).remove();
    reEvaluateOptions();
    checkLimitAlunni();
}

function add_excludeNeet() {
    let html = '<div class="row form-group" style="margin-bottom: 0rem!important;" id="exclude_row_' + addInputExclusion + '">' +
            '<div class="form-group col-md-3" style="margin-bottom: 0.5rem!important;">' +
            '<div class="select-div" id="neet_' + addInputExclusion + '_div">' +
            '<select class="form-control kt-select2-general obbligatory neet-combo" id="neet_' + addInputExclusion + '" name="neet_' + addInputExclusion + '" style="width: 100%;font-family: Poppins;"></select>' +
            '</div>' +
            '</div>' +
            '<div class="form-group col-md-8" style="margin-bottom: 0.5rem!important;">' +
            '<input type="text" class="form-control obbligatory" name="motivo_' + addInputExclusion + '" id="motivo_' + addInputExclusion + '" placeholder="Motivazione esclusione" maxlength="254" autocomplete="off" />' +
            '</div>' +
            '<div class="form-group col-md-1" style="margin-bottom: 0.5rem!important;">' +
            '<a href="javascript:void(0);" onclick="remove_excludeNeet(' + addInputExclusion + ')" class="btn btn-icon btn-danger btn-circle" style="margin: 0px; height: 2rem; width: 2rem;"><i class="fa fa-user-minus" style="display: table-cell; vertical-align: middle;font-size: 1rem;"></i>' +
            '</a>' +
            '</div>' +
            '</div>';
    $('#dinamic_select').append(html);

    $('#neet_' + addInputExclusion).select2({
        dropdownCssClass: "select2-on-top",
        minimumResultsForSearch: -1,
    });

    allieviPrg = orderListAllievi(allieviPrg);

    for (let a of allieviPrg) {
        if (neetSel.includes(a.id)) {
            $('#neet_' + addInputExclusion).append('<option value="' + a.id + '">' + a.nome + " " + a.cognome + '</option>');
        } else {
            $('#neet_' + addInputExclusion).append('<option value="' + a.id + '">' + a.nome + " " + a.cognome + '</option>');
        }
    }

    reEvaluateOptions();

    $('select[id*="neet_"]').change(function () {
        reEvaluateOptions();
    });

    checkLimitAlunni();
    addInputExclusion++;
}



function checkLimitAlunni() {
    let allieviAttivi = allieviPrg.length - $('div[id^=exclude_row_]').length;
    if (allieviAttivi > 0) {
        $('#add_exclusion').removeClass('disablelink');
        $('#add_exclusion').attr('onClick', 'add_excludeNeet();');
//        $('#warning').hide();
    } else {
//13/07/21 Possibilità di escludere anche tutti i NEETS dal PF (in caso di numero minimo non raggiunto lo stesso verrà rigettato)        
        $('#add_exclusion').addClass('disablelink');
        $('#add_exclusion').removeAttr("onclick");
        //    $('#warning').show();    
    }
    $('#warning_msg').html("Si ricorda che il numero minimo di allievi per avviare un Progetto Formativo è " + minAllievi + ".<br>Se non verrà raggiunta la quota, il Progetto verrà automaticamente rigettato.<br>");
    let color = allieviAttivi >= minAllievi ? "style='color: #384ad7 !important;'" : "style='color: #fb0c0c !important;'";
    $('#warning_msg').append("<div " + color + ">Allievi attualmente attivi <b>" + allieviAttivi + "</b></div>");
    $('#warning').show();
}


function alreadySelected() {
    let allieviSelezionati = [];
    $("select[id^=neet_]").each(function () {
        if (!allieviSelezionati.includes(Number($(this).find("option:selected").val()))) {
            allieviSelezionati.push(Number($(this).find("option:selected").val()));
        }
    });
    return allieviSelezionati;
}

function orderListAllievi(a) {
    neetSel = alreadySelected();
    for (let el of a) {
        if (neetSel.includes(el.id)) {
            itemToReplace = a.splice(a.indexOf(el), 1);
            a = a.concat(itemToReplace);
        }
    }
    return a;
}


function annullaPrg(id) {
    var html = "<div class='form-group' id='swal_motivo'><textarea class='form-control obbligatory' id='motivo' placeholder='Motivazione'></textarea></div>";
    swal.fire({
        title: '<h2 class="kt-font-io-n"><b>Annulla Progetto</b></h2><br>',
        html: html,
        animation: false,
        width: '50%',
        showCancelButton: true,
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        customClass: {
            popup: 'animated bounceInUp'
        },
        preConfirm: function () {
            var err = false;
            err = checkObblFieldsContent($('#swal_motivo')) ? true : err;
            if (!err) {
                return new Promise(function (resolve) {
                    resolve({
                        "motivo": $('#motivo').val()
                    });
                });
            } else {
                return false;
            }
        }
    }).then((result) => {
        if (result.value) {
            annulla(id, result.value);
        } else {
            swal.close();
        }
    });
}

function annulla(id, result) {
    showLoad();
    $.ajax({
        type: "POST",
        url: context + '/OperazioniMicro?type=annullaPrg&id=' + id,
        data: result,
        success: function (data) {
            closeSwal();
            var json = JSON.parse(data);
            if (json.result) {
                reload();
                swalSuccess("Progetto Annullato", json.message !== null ? json.message : "Progetto formativo annullato con successo");
            } else {
                swalError("Errore", json.message);
            }
        },
        error: function () {
            swalError("Errore", "Non è stato possibile annullare il progetto formativo");
        }
    });
}