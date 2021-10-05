
var context = document.getElementById("dUnit").getAttribute("data-context");

$.getScript(context + '/page/partialView/partialView.js', function () {});

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
//            responsive: true,
            searchDelay: 500,
            ScrollX: "100%",
            sScrollXInner: "110%",
            processing: true,
            pageLength: 10,
            ajax: context + '/QueryMicro?type=searchUnitaDidattiche&codiceud=' + $('#codiceud').val() + '&fase=' + $('#fase').val(),
            order: [],
            columns: [
                {defaultContent: ''},
                {data: 'codice'},
                {data: 'fase'},
                {data: 'descrizione'},
                {data: 'ore'},
                {data: 'countDocs'},
            ],
            drawCallback: function () {
                $('[data-toggle="kt-tooltip"]').tooltip();
            },
            rowCallback: function (row, data) {
                $(row).attr("id", "row_" + data.id);
            },
            columnDefs: [
                {"className": "dt-center", "targets": "_all"},
                {
                    targets: 0,
                    orderable: false,
                    render: function (data, type, row, meta) {

                        var option = '<div class="dropdown dropdown-inline">'
                                + '<button type="button" class="btn btn-icon btn-sm btn-icon-md btn-circle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">'
                                + '   <i class="flaticon-more-1"></i>'
                                + '</button>'
                                + '<div class="dropdown-menu dropdown-menu-left">';
                        option += '<a class="dropdown-item fancyProfile" href="schedaUD.jsp?codice=' + row.codice + '"><i class="fa fa-pencil-alt"></i> Visualizza/Modifica Unità Didattica</a>';
                        option += '</div></div>';
                        return option;
                    }
                },
                {
                    targets: 5,
                    render: function (data, type, row, meta) {
                        let msg;
                        switch (data) {
                            case 0:
                                msg = "Nessun documento caricato";
                                break;
                            case 1:
                                msg = data + " documento";
                                break;
                            default:
                                msg = data + " documenti";
                                break;
                        }
                        return msg;
                    }
                },
            ]
        }).columns.adjust();
    };
    return {
        init: function () {
            initTable1();
        },
    };
}();

jQuery(document).ready(function () {
    KTDatatablesDataSourceAjaxServer.init();
    $('.kt-scroll').each(function () {
        const ps = new PerfectScrollbar($(this)[0]);
    });
    $('.kt-scroll-x').each(function () {
        const ps = new PerfectScrollbar($(this)[0], {suppressScrollY: true});
    });
});

function refresh() {
    $('html, body').animate({scrollTop: $('#offsetresult').offset().top}, 500);
    load_table($('#kt_table_1'), context + '/QueryMicro?type=searchUnitaDidattiche&codiceud=' + $('#codiceud').val() + '&fase=' + $('#fase').val());
}

function reload() {
    $('html, body').animate({scrollTop: $('#offsetresult').offset().top}, 500);
    reload_table($('#kt_table_1'));
}
