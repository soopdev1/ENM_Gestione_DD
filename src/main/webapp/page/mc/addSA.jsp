
<%@page import="it.refill.domain.EstensioniFile"%>
<%@page import="it.refill.domain.TipoDoc"%>
<%@page import="it.refill.domain.TipoDoc_Allievi"%>
<%@page import="it.refill.db.Entity"%>
<%@page import="it.refill.domain.User"%>
<%@page import="it.refill.db.Action"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    User us = (User) session.getAttribute("user");
    if (us == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    } else {
        String uri_ = request.getRequestURI();
        String pageName_ = uri_.substring(uri_.lastIndexOf("/") + 1);
        if (!Action.isVisibile(String.valueOf(us.getTipo()), pageName_)) {
            response.sendRedirect(request.getContextPath() + "/page_403.jsp");
        } else {
            
            Entity e = new Entity();
            //EstensioniFile ext = e.getEm().find(EstensioniFile.class, "pdf");
            e.close();
            String src = session.getAttribute("src").toString();
            //String iva = request.getParameter("piva") != null ? request.getParameter("piva") : "";
%>
<html>
    <head>
        <meta charset="utf-8" />
        <title>YES I Start Up - Donne e Disoccupati - Soggetti Attuatori Cerca</title>
        <meta name="description" content="Updates and statistics">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

        <!--begin::Fonts -->
        <script src="<%=src%>/resource/webfont.js"></script>
        <script>
            WebFont.load({
                google: {
                    "families": ["Poppins:300,400,500,600,700", "Roboto:300,400,500,600,700"]
                },
                active: function () {
                    sessionStorage.fonts = true;
                }
            });
        </script>

        <link href="<%=src%>/assets/vendors/general/select2/dist/css/select2.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/owl.carousel/dist/assets/owl.carousel.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/owl.carousel/dist/assets/owl.theme.default.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/sweetalert2/dist/sweetalert2.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/socicon/css/socicon.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/line-awesome/css/line-awesome.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/flaticon/flaticon.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/flaticon2/flaticon.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/fontawesome5/css/all.min.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/resource/animate.css" rel="stylesheet" type="text/css"/>
        <link href="<%=src%>/resource/datatbles.bundle.css" rel="stylesheet" type="text/css"/>
        <link href="<%=src%>/assets/demo/default/base/style.bundle.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/resource/custom.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/header/base/light.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/header/menu/light.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/brand/light.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/aside/light.css" rel="stylesheet" type="text/css" />
        <link rel="shortcut icon" href="<%=src%>/assets/media/logos/favicon.ico" />
        <style>
            .kt-section__title {
                font-size: 1.2rem!important;
            }
        </style>


    </head>
    <body class="kt-header--fixed kt-header-mobile--fixed kt-subheader--fixed kt-subheader--enabled kt-subheader--solid kt-aside--enabled kt-aside--fixed">
        <!-- begin:: Page -->
        <%@ include file="menu/head1.jsp"%>
        <div class="kt-grid kt-grid--hor kt-grid--root">
            <div class="kt-grid__item kt-grid__item--fluid kt-grid kt-grid--ver kt-page">
                <%@ include file="menu/menu.jsp"%>
                <!-- end:: Aside -->
                <div class="kt-grid__item kt-grid__item--fluid kt-grid kt-grid--hor kt-wrapper" id="kt_wrapper">
                    <%@ include file="menu/head.jsp"%>
                    <!-- begin:: Footer -->
                    <div class="kt-grid__item kt-grid__item--fluid kt-grid kt-grid--hor">
                        <!-- begin:: Content Head -->
                        <div class="kt-subheader   kt-grid__item" id="kt_subheader">
                            <div class="kt-subheader   kt-grid__item" id="kt_subheader">
                                <div class="kt-subheader__main">
                                    <h3 class="kt-subheader__title">Soggetti Attuatori</h3>
                                    <span class="kt-subheader__separator kt-subheader__separator--v"></span>
                                    <a class="kt-subheader__breadcrumbs-link">Gestisci Nuovi</a>
                                </div>
                            </div>
                        </div>
                        <div class="kt-content  kt-grid__item kt-grid__item--fluid" id="kt_content">
                            <div class="row" id="offsetresult">
                                <div class="col-lg-12">
                                    <div class="kt-portlet" id="kt_portlet" data-ktportlet="true">
                                        <div class="kt-portlet__head">
                                            <div class="kt-portlet__head-label col-lg-8">
                                                    <h3 class="kt-portlet__head-title text" >
                                                        Soggetti Attuatore Accreditati, da confermare:</h3>
                                            </div>
                                            <div class="kt-portlet__head-toolbar">
                                                <div class="kt-portlet__head-group">
                                                    <a href="#" data-ktportlet-tool="toggle" class="btn btn-sm btn-icon btn-clean btn-icon-md"><i class="la la-angle-down" id="toggle_search"></i></a>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="kt-portlet__body kt-scroll-x">
                                            <table class="table table-striped table-bordered " cellspacing="0" id="kt_table_1"style="width:100%;border-collapse: collapse;"> 
                                                <thead>
                                                    <tr>
                                                        <th class="text-uppercase text-center">Azioni</th>
                                                        <th class="text-uppercase text-center">Ragionesociale</th>
                                                        <th class="text-uppercase text-center">P.Iva</th>
                                                        <th class="text-uppercase text-center">Cod. Fiscale</th>
                                                        <th class="text-uppercase text-center">Provincia</th>
                                                        <th class="text-uppercase text-center">Comune</th>
                                                        <th class="text-uppercase text-center">Via</th>
                                                        <th class="text-uppercase text-center">Nome Amministratore</th>
                                                        <th class="text-uppercase text-center">Cognome Amministratore</th>
                                                        <th class="text-uppercase text-center">Telefono</th>
                                                        <th class="text-uppercase text-center">N. Protocollo</th>
                                                        <th class="text-uppercase text-center">Data</th>
                                                    </tr>
                                                </thead>
                                            </table>  
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!-- end:: Content Head -->
                    </div>
                    <%@ include file="menu/footer.jsp"%>
                    <!-- end:: Footer -->
                    <!-- end:: Content -->
                </div>
            </div>
        </div>

        <!-- begin::Scrolltop -->
        <div id="kt_scrolltop" class="kt-scrolltop">
            <i class="fa fa-arrow-up"></i>
        </div>
        <script src="<%=src%>/assets/vendors/general/perfect-scrollbar/dist/perfect-scrollbar.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/jquery/dist/jquery.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/popper.js/dist/umd/popper.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/bootstrap/dist/js/bootstrap.min.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/js-cookie/src/js.cookie.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/moment/min/moment.min.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/tooltip.js/dist/umd/tooltip.min.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/sticky-js/dist/sticky.min.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/demo/default/base/scripts.bundle.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/sweetalert2/dist/sweetalert2.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/refill/js/utility.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/app/bundle/app.bundle.js" type="text/javascript"></script>
        <!-- this page -->
        <script src="<%=src%>/assets/vendors/custom/datatables/datatables.bundle.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/refill/js/loadTable.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/app/custom/general/crud/forms/widgets/bootstrap-datepicker.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/bootstrap-datepicker/dist/js/bootstrap-datepicker.js" type="text/javascript"></script>

        <script type="text/javascript">
                                                                        var KTAppOptions = {
                                                                            "colors": {
                                                                                "state": {
                                                                                    "brand": "#5d78ff",
                                                                                    "dark": "#282a3c",
                                                                                    "light": "#ffffff",
                                                                                    "primary": "#5867dd",
                                                                                    "success": "#34bfa3",
                                                                                    "info": "#36a3f7",
                                                                                    "warning": "#ffb822"
                                                                                },
                                                                                "base": {
                                                                                    "label": ["#c5cbe3", "#a1a8c3", "#3d4465", "#3e4466"],
                                                                                    "shape": ["#f0f3ff", "#d9dffa", "#afb4d4", "#646c9a"]
                                                                                }
                                                                            }
                                                                        };
        </script>

        <script>
            $.getScript('<%=request.getContextPath()%>/page/partialView/partialView.js', function () {});

            var KTDatatablesDataSourceAjaxServer = function () {
                var initTable1 = function () {
                    var table = $('#kt_table_1');
                    table.DataTable({
                        dom: `<'row'<'col-sm-12'Bftr>><'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7 dataTables_pager'lp>>`,
                        buttons: [
                            {
                                extend: 'excelHtml5',
                                text: '<i class="fa fa-file-excel-o"></i> Esporta Excel',
                                columns: ':not(.noVis)'
                            }
                        ],
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
//                        responsive: true,
                        ScrollX: "100%",
                        sScrollXInner: "110%",
                        searchDelay: 500,
                        processing: true,
                        pageLength: 10,
                        ajax: '<%=request.getContextPath()%>/QueryMicro?type=nuoviSA',
                        order: [],
                        columns: [
                            {defaultContent: ''},
                            {data: 'ragionesociale'},
                            {data: 'piva'},
                            {data: 'codicefiscale'},
                            {data: 'comune.nome_provincia'},
                            {data: 'comune.nome'},
                            {data: 'indirizzo'},
                            {data: 'nome'},
                            {data: 'cognome'},
                            {data: 'telefono_sa'},
                            {data: 'protocollo'},
                            {data: 'visual_dataprotocollo'}
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
                                            + '<i class="flaticon-more-1"></i>'
                                            + '</button>'
                                            + '<div class="dropdown-menu dropdown-menu-left">';
                                    //opzione ACCETTA
                                    option += '<a class="dropdown-item" href="javascript:void(0);" onclick="return accettaSA(' + row.id + ');"><i class="la la-check-square"></i> CONFERMA </a>';
                                    option += '</div></div>';
                                    return option;
                                }
                            },
                            {
                                targets: 1,
                                title: "RAGIONE SOCIALE"
                            },
                            {
                                targets: 9,
                                orderable: false
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
            jQuery(document).ready(function () {
                KTDatatablesDataSourceAjaxServer.init();
                $('.kt-scroll-x').each(function () {
                    const ps = new PerfectScrollbar($(this)[0], {suppressScrollY: true});
                });
            });
            
            function accettaSA(idsa) {
                $.ajax({
                    async: false,
                    type: "POST",
                    url: '<%=request.getContextPath()%>/OperazioniMicro?type=accreditaSA&idsa=' + idsa,
                    success: function (data) {
                        var json = JSON.parse(data);
                        if (json.result) {
                            swalSuccessReload("Operazione Completata", "Soggetto Attuatore caricato correttamente a sistema.");
                        } else {
                            swalError("Errore", json.message);
                        }
                    },
                    error: function () {
                        swalError("Errore", "Non è stato possibile effettuare l'operazione scelta. Riprovare.");
                    }
                });
            }
            
            function refresh() {
                $("#toolbar").css("display", "none");
                $('html, body').animate({scrollTop: $('#offsetresult').offset().top}, 500);
                load_table($('#kt_table_1'), '<%=request.getContextPath()%>/QueryMicro?type=nuoviSA');
            }

            
        </script>
    </body>
</html>
<%
        }
    }
%>