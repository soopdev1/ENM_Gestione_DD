<%-- 
    Document   : profile
    Created on : 18-set-2019, 12.31.26
    Author     : agodino
--%>
<%@page import="java.util.Date"%>
<%@page import="it.refill.util.Utility"%>
<%@page import="it.refill.domain.Documenti_UnitaDidattiche"%>
<%@page import="it.refill.domain.UnitaDidattiche"%>
<%@page import="java.util.List"%>
<%@page import="it.refill.db.Action"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="it.refill.db.Entity"%>
<%@page import="it.refill.domain.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
            String src = session.getAttribute("src").toString();
            Entity e = new Entity();
            UnitaDidattiche u = e.getEm().find(UnitaDidattiche.class, request.getParameter("codice"));
            int files = Integer.parseInt(e.getPath("UD_max_files"));
            int links = Integer.parseInt(e.getPath("UD_max_links"));
            boolean checkUpload[] = Utility.LinksDocs_UD(u.getDocumenti_ud(), Integer.parseInt(e.getPath("UD_max_files")), Integer.parseInt(e.getPath("UD_max_links")));
            e.close();
            int maxfiles = links + files;
%>
<html>
    <head>
        <meta charset="utf-8" />
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

        <!--end::Fonts -->

        <!--begin::Page Vendors Styles(used by this page) -->

        <!--end::Page Vendors Styles -->

        <!--begin:: Global Mandatory Vendors -->
        <link href="<%=src%>/assets/vendors/general/perfect-scrollbar/css/perfect-scrollbar.css" rel="stylesheet" type="text/css" />

        <!--end:: Global Mandatory Vendors -->

        <!--begin:: Global Optional Vendors -->
        <link href="<%=src%>/assets/vendors/general/bootstrap-touchspin/dist/jquery.bootstrap-touchspin.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/bootstrap-select/dist/css/bootstrap-select.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/bootstrap-switch/dist/css/bootstrap3/bootstrap-switch.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/owl.carousel/dist/assets/owl.carousel.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/owl.carousel/dist/assets/owl.theme.default.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/sweetalert2/dist/sweetalert2.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/socicon/css/socicon.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/line-awesome/css/line-awesome.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/flaticon/flaticon.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/flaticon2/flaticon.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/fontawesome5/css/all.min.css" rel="stylesheet" type="text/css" />

        <!--end:: Global Optional Vendors -->

        <!--begin::Global Theme Styles(used by all pages) -->
        <link href="<%=src%>/assets/demo/default/base/style.bundle.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/resource/custom.css" rel="stylesheet" type="text/css" />

        <!--end::Global Theme Styles -->
        <link href="<%=src%>/assets/vendors/general/bootstrap-datepicker/dist/css/bootstrap-datepicker3.css" rel="stylesheet" type="text/css" />
        <!--begin::Layout Skins(used by all pages) -->
        <link href="<%=src%>/assets/demo/default/skins/header/base/light.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/header/menu/light.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/brand/light.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/aside/light.css" rel="stylesheet" type="text/css" />

        <link href="<%=src%>/resource/animate.css" rel="stylesheet" type="text/css"/>
        <!--end::Layout Skins -->
        <link rel="shortcut icon" href="<%=src%>/assets/media/logos/favicon.ico" />
        <style>
            th { font-size: 13px; }
            td { font-size: 12px; }
            tr:hover {
                background-color: #ffdaa2;
            }
            tr:hover td {
                background-color: transparent;
            }

            .btn-io:not(:disabled):not(.disabled):active, .btn-io:not(:disabled):not(.disabled).active, .show > .btn-io.dropdown-toggle {
                background-color: #4b4e92!important;
            }
        </style>
        <!--end::countDown -->
    </head>
    <body class="kt-header--fixed kt-header-mobile--fixed kt-subheader--fixed kt-subheader--enabled kt-subheader--solid kt-aside--enabled kt-aside--fixed">
        <!-- begin:: Page -->
        <div class="kt-grid kt-grid--hor kt-grid--root">
            <div class="kt-grid__item kt-grid__item--fluid kt-grid kt-grid--ver kt-page">
                <!-- end:: Aside -->
                <div class="kt-grid__item kt-grid__item--fluid kt-grid kt-grid--hor">
                    <!-- begin:: Footer -->
                    <div class="kt-grid__item kt-grid__item--fluid kt-grid kt-grid--hor">
                        <!-- begin:: Content Head -->
                        <!-- end:: Content Head -->
                        <!-- begin:: Content -->
                        <div class="kt-content  kt-grid__item kt-grid__item--fluid" id="kt_content">
                            <div class="kt-portlet kt-portlet--mobile">
                                <div class="kt-portlet__body">
                                    <!--begin:: Widgets/Best Sellers-->
                                    <div class="kt-portlet__head">
                                        <div class="kt-portlet__head kt-portlet__head--noborder">
                                            <div class="kt-portlet__head-label">
                                                <h3 class="kt-portlet__head-title kt-font-io">
                                                    <i class="flaticon2-list-2"></i> Scheda Unità didattica&emsp;|&emsp;Codice:  <b> <%=u.getCodice()%></b>&emsp;Fase:  <b> <%=u.getFase()%></b>&emsp;Ore:  <b> <%=u.getOre()%></b>
                                                    <input type="hidden" id="checkTotal" name="checkTotal" value="<%=checkUpload[0] && checkUpload[1]%>">
                                                    <input type="hidden" id="checkFiles" name="checkFiles" value="<%=checkUpload[0]%>">
                                                    <input type="hidden" id="checkLinks" name="checkLinks" value="<%=checkUpload[1]%>">
                                                </h3>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="kt-portlet__body" style="padding-bottom: 5px!important;">
                                        <div class="tab-content">
                                            <div class="row">
                                                <div class="col-md-12 col-sm-12">
                                                    <div class="form-group">
                                                        <h6><label style="font-weight: 500!important; font-size:1.15rem" class="kt-font-io">Descrizione</label>&nbsp;<label class="kt-font-danger kt-font-boldest">*</label></h6>
                                                        <div class="input-group">
                                                            <input type="hidden" id="codice" name="codice" value="<%=u.getCodice()%>">
                                                            <input type="hidden" id="old_desc" name="old_desc" value="<%=u.getDescrizione() == null ? "" : u.getDescrizione()%>">
                                                            <input type="text" class="form-control" maxlength="255" onkeypress="counterCharacters()" id="descrizione" name="descrizione" value="<%=u.getDescrizione() == null ? "" : u.getDescrizione()%>">
                                                            <div class="input-group-append">
                                                                <button class="btn btn-io-n" type="button" onclick="saveDescription()">Salva</button>
                                                            </div>
                                                        </div>
                                                        <p style="padding-bottom:10px;">Caratteri: <b><span id="numChar"></span></b> (Massimo 255 caratteri)</p>
                                                    </div>
                                                </div>
                                                <div id="alertFiles" class="kt-font-danger kt-align-right col-md-12 col-sm-12" style="display: none;"><h6><i class="fa fa-exclamation-triangle"></i>&nbsp;Numero massimo di documenti raggiunto (<%=maxfiles%>)</h6></div>
                                                <div id="alertDoc" class="kt-font-danger kt-align-right col-md-12 col-sm-12" style="display: none;"><h6><i class="fa fa-exclamation-triangle"></i>&nbsp;Numero massimo di files raggiunto (<%=files%>)</h6></div>
                                                <div id="alertLink" class="kt-font-danger kt-align-right col-md-12 col-sm-12" style="display: none;"><h6><i class="fa fa-exclamation-triangle"></i>&nbsp;Numero massimo di links raggiunto (<%=links%>)</h6></div>
                                                <div class="col-md-12 col-sm-12">
                                                    <div class="kt-section kt-section--space-md">
                                                        <div class="form-group form-group-sm row">
                                                            <div class="col-md-9">
                                                                <h6 class="kt-font-io" style="font-size:1.15rem">Documenti (numero massimo di files caricabili: <b><%=maxfiles%></b>)</h6><br>
                                                                <%if (u.getDocumenti_ud().isEmpty()) {%>
                                                                Nessun documento caricato
                                                                <%} else {%> 
                                                                <div class="kt-timeline-v2">
                                                                    <div class="kt-timeline-v2__items  kt-padding-top-25 kt-padding-bottom-30">
                                                                        <table style="width:100%;">
                                                                            <%for (Documenti_UnitaDidattiche doc : Utility.UDOrderByDate(u.getDocumenti_ud())) {%>
                                                                            <tr>
                                                                                <td widht="85%">
                                                                                    <div class="kt-timeline-v2__item " style="margin-bottom: 1.25rem!important;">
                                                                                        <span class="kt-timeline-v2__item-time kt-font-io"><%=doc.getTipo()%></span>
                                                                                        <div class="kt-timeline-v2__item-cricle" style="border-color: #ffffff00;" >
                                                                                            <i class="<%=doc.getTipo().equalsIgnoreCase("PDF") ? "fa fa-file-pdf kt-font-info" : "fa fa-file-video kt-font-danger"%>"></i>
                                                                                        </div>
                                                                                        <div class="kt-timeline-v2__item-text  kt-padding-top-5 kt-font-io">
                                                                                            <b><%=new SimpleDateFormat("HH:mm dd/MM/yy").format(doc.getData_modifica())%></b> - <%=doc.getTipo().equalsIgnoreCase("PDF") ? doc.getPath().substring(doc.getPath().lastIndexOf("/") + 1) : doc.getPath()%>
                                                                                        </div>
                                                                                    </div>
                                                                                </td>
                                                                                <%if (doc.getTipo().equalsIgnoreCase("PDF")) {%>
                                                                                <td widht="5%"><a target="_blank" href="<%=request.getContextPath()%>/OperazioniGeneral?type=showDoc&path=<%=doc.getPath()%>" class="btn" data-container="body" data-html="true" data-toggle="kt-tooltip" data-placement="top" title="<h5>Visualizza documento</h5>"><i style="font-size: 15px;" class="fa fa-expand kt-font-io"></i></a> </td>
                                                                                <td widht="5%"><a href="javascript:void(0);" onclick="updateDocUD(<%=doc.getId_docud()%>, '[&quot;pdf&quot;]', 'application/pdf')" class="btn" data-container="body" data-html="true" data-toggle="kt-tooltip" data-placement="top" title="<h5>Modifica documento</h5>"><i style="font-size: 15px;" class="fa fa-pen kt-font-io-n"></i></a></td>
                                                                                        <%} else {%>
                                                                                <td widht="5%"><a target="_blank" href="<%=doc.getPath()%>" class="btn" data-container="body" data-html="true" data-toggle="kt-tooltip" data-placement="top" title="<h5>Apri link in un'altra scheda</h5>"><i style="font-size: 15px;" class="fa fa-expand kt-font-io"></i></a> </td>
                                                                                <td widht="5%"><a href="javascript:void(0);" onclick='updateLinkUD(<%=doc.getId_docud()%>, "<%=doc.getPath()%>")' class="btn" data-container="body" data-html="true" data-toggle="kt-tooltip" data-placement="top" title="<h5>Modifica Link</h5>"><i style="font-size: 15px;" class="fa fa-pen kt-font-io-n"></i></a></td>
                                                                                        <%}%>
                                                                                <td widht="5%"><a href="javascript:void(0);" onclick="deleteDocUD(<%=doc.getId_docud()%>)" class="btn" data-container="body" data-html="true" data-toggle="kt-tooltip" data-placement="top" title="<h5>Elimina documento</h5>"><i style="font-size: 15px;" class="fa fa-times kt-font-danger"></i></a></td>
                                                                            </tr>
                                                                            <%}%>
                                                                        </table>
                                                                    </div>
                                                                </div>
                                                                <%}%>    
                                                            </div>
                                                            <div class="col-md-3 kt-align-right">
                                                                <div class="btn-group" role="group">
                                                                    <button id="btnGroupDrop1" type="button" class="btn btn-io font-weight-bold dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                                                        Carica documento
                                                                    </button>
                                                                    <div class="dropdown-menu" aria-labelledby="btnGroupDrop1">
                                                                        <button class="btn dropdown-item kt-font-io" id="uDoc" onclick="uploadDocUD('<%=u.getCodice()%>', '[&quot;pdf&quot;]', 'application/pdf')"><b>PDF</b></button>
                                                                        <button class="btn dropdown-item kt-font-io" id="uLink" onclick="uploadLinkUD('<%=u.getCodice()%>')"><b>Link</b></button>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <!--</form>-->     
                                </div>
                            </div>
                        </div>
                    </div>	
                </div>
                <!-- end:: Content -->
            </div>
        </div>

        <!-- end:: Page -->

        <!-- begin::Quick Panel -->


        <!-- end::Quick Panel -->

        <!-- begin::Scrolltop -->
        <div id="kt_scrolltop" class="kt-scrolltop">
            <i class="fa fa-arrow-up"></i>
        </div>

        <!--begin:: Global Mandatory Vendors -->
        <script src="<%=src%>/assets/vendors/general/jquery/dist/jquery.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/popper.js/dist/umd/popper.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/bootstrap/dist/js/bootstrap.min.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/js-cookie/src/js.cookie.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/moment/min/moment.min.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/tooltip.js/dist/umd/tooltip.min.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/perfect-scrollbar/dist/perfect-scrollbar.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/sticky-js/dist/sticky.min.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/demo/default/base/scripts.bundle.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/jquery-form/dist/jquery.form.min.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/custom/vendors/bootstrap-multiselectsplitter/bootstrap-multiselectsplitter.min.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/bootstrap-select/dist/js/bootstrap-select.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/jquery-validation/dist/jquery.validate.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/jquery-validation/dist/additional-methods.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/custom/components/vendors/jquery-validation/init.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/app/custom/general/components/extended/blockui1.33.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/sweetalert2/dist/sweetalert2.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/refill/js/utility.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/app/bundle/app.bundle.js" type="text/javascript"></script>

        <script src="<%=src%>/assets/vendors/custom/datatables/datatables.bundle.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/refill/js/loadTable.js" type="text/javascript"></script>
        <script id="schedaUD" defer src="<%=src%>/page/mc/js/schedaUD.js<%="?dummy=" + String.valueOf(new Date().getTime())%>" data-context="<%=request.getContextPath()%>" type="text/javascript"></script>
        <script>
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
    </body>
</html>
<%}
    }%>
