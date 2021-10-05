<%@page import="java.util.Date"%>
<%@page import="it.refill.domain.TipoDoc"%>
<%@page import="it.refill.domain.Lezioni_Modelli"%>
<%@page import="it.refill.domain.ModelliPrg"%>
<%@page import="java.util.Map"%>
<%@page import="it.refill.util.Utility"%>
<%@page import="it.refill.domain.LezioneCalendario"%>
<%@page import="it.refill.domain.ProgettiFormativi"%>
<%@page import="it.refill.domain.SoggettiAttuatori"%>
<%@page import="it.refill.db.Action"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="it.refill.db.Entity"%>
<%@page import="it.refill.entity.Item"%>
<%@page import="it.refill.domain.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    User us = (User) session.getAttribute("user");
    if (us == null) {
    } else {
        String uri_ = request.getRequestURI();
        String pageName_ = uri_.substring(uri_.lastIndexOf("/") + 1);
        if (!Action.isVisibile(String.valueOf(us.getTipo()), pageName_)) {
            response.sendRedirect(request.getContextPath() + "/page_403.jsp");
        } else {
            String src = session.getAttribute("src").toString();
            Entity e = new Entity();
            ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("id")));
            List<LezioneCalendario> lezioniCalendariom3 = e.getLezioniByModello(3);
            ModelliPrg m3 = Utility.filterModello3(p.getModelli());
            List<Lezioni_Modelli> lezionim3 = m3.getLezioni();
            List<LezioneCalendario> grouppedByLezionem3 = Utility.grouppedByLezione(lezioniCalendariom3);

            List<LezioneCalendario> lezioniCalendariom4 = e.getLezioniByModello(4);
            ModelliPrg m4 = Utility.filterModello4(p.getModelli());
            List<Lezioni_Modelli> lezionim4 = m4.getLezioni();
            List<LezioneCalendario> grouppedByLezionem4 = Utility.grouppedByLezione(lezioniCalendariom4);
            int gruppi = Utility.numberGroupsModello4(p);
            e.close();
            boolean noloaded = true;
%>
<html>
    <head>
        <meta charset="utf-8" />
        <title>YES I Start Up - Donne e Disoccupati - Modello 3</title>
        <meta name="description" content="Updates and statistics">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
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
        <!-- this page -->
        <link href="<%=src%>/assets/vendors/general/bootstrap-datepicker/dist/css/bootstrap-datepicker3.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/select2/dist/css/select2.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/bootstrap-select/dist/css/bootstrap-select.css" rel="stylesheet" type="text/css" />
        <!-- - -->
        <link href="<%=src%>/assets/vendors/general/perfect-scrollbar/css/perfect-scrollbar.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/owl.carousel/dist/assets/owl.carousel.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/owl.carousel/dist/assets/owl.theme.default.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/sweetalert2/dist/sweetalert2.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/line-awesome/css/line-awesome.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/flaticon/flaticon.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/flaticon2/flaticon.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/fontawesome5/css/all.min.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/bootstrap-timepicker/css/bootstrap-timepicker.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/resource/animate.css" rel="stylesheet" type="text/css"/>
        <link href="<%=src%>/assets/demo/default/skins/header/base/light.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/header/menu/light.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/brand/light.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/aside/light.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/base/style.bundle.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/resource/custom.css" rel="stylesheet" type="text/css" />

        <link rel="shortcut icon" href="<%=src%>/assets/media/logos/favicon.ico" />
        <style type="text/css">
            .form-group {
                margin-bottom: 1rem;
            }

            .custom-file-label::after {
                color:#fff;
                background-color: #eaa21c;
            }

            .kt-radio > span::after {
                border: solid #363a90;
                background: #363a90;
            }

            .kt-radio > input:checked ~ span {
                border: 1px solid #363a90;
            }

            a.disablelink > i {
                color: #7c7fb7!important;
            }

            .datepicker table tr td.highlighted.disabled, .datepicker table tr td.highlighted.disabled:active {
                background: #d9edf7;
                color: #d08902 !important;
            }

            .datepicker tbody tr > td.day {
                background: #ebedf2;
                color: #1d32a6 !important;
            }

            .datepicker tbody tr > td.day.active, .datepicker tbody tr > td.day.active {
                background: #363a90;
                color: #ffffff !important;
            }

            .datepicker table tr td.disabled, .datepicker table tr td.disabled {
                background: none;
                color: #777;
                cursor: default;
                color: #756c6e !important;
            }

            .select2-results__option[aria-selected=true] {
                display: none;
            }
            .select2-selection__clear{
                display: none;
            }

        </style>
    </head>
    <body class="kt-header--fixed kt-header-mobile--fixed kt-subheader--fixed kt-subheader--enabled kt-subheader--solid kt-aside--enabled kt-aside--fixed">
        <!-- begin:: Page -->
        <div class="kt-grid kt-grid--hor kt-grid--root">
            <div class="kt-grid__item kt-grid__item--fluid kt-grid kt-grid--ver kt-page">
                <div class="kt-grid__item kt-grid__item--fluid kt-grid kt-grid--hor">
                    <div class="kt-content  kt-grid__item kt-grid__item--fluid" id="kt_content">
                        <div class="kt-portlet kt-portlet--mobile">
                            <div class="kt-portlet__head" style="display: none;">
                                <div class="kt-portlet__head-label">
                                    <h3 class="kt-portlet__head-title">
                                        Crea:
                                    </h3>
                                </div>
                                <input type="hidden" id="m3Id" name="m3Id" value="<%=m3.getId()%>">
                                <input type="hidden" id="m3Total" name="m3Total" value="<%=grouppedByLezionem3.size()%>">
                            </div>
                            <div class="kt-portlet__body">
                                <div class="kt-section kt-section--space-md">
                                    <div class="form-group form-group-sm row">
                                        <div class="col-12">
                                            <h5>Modello 3 - Calendario </h5>
                                            <div class="kt-separator kt-separator--border kt-separator--space-xs"></div>
                                            <div class="progress">
                                                <div id="progressM3" class="progress-bar progress-bar-striped progress-bar-animated  bg-success" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" data-container="body" data-html="true" data-toggle="kt-tooltip" title="" data-original-title="" style="width: 0%"></div>
                                            </div>
                                            <div class="kt-separator kt-separator--border kt-separator--space-xs"></div>
                                            <div class="form-row">
                                                <%for (LezioneCalendario lez : grouppedByLezionem3) {
                                                        if (Utility.lezioneFiltered(lezionim3, lez.getId()) != null) {
                                                            noloaded = false;%>
                                                <div class='col-lg-2 col-md-4 col-sm-6'>
                                                    <div class='row'>
                                                        <%if (lez.isDoppia()) {%>
                                                        <div class="col-6 paddig_0_r" data-container="body" data-html="true" data-original-title="Visualizza lezione" data-toggle="kt-tooltip" title="" style="text-align: center;">
                                                            <a href="javascript:void(0)" id="a_lez<%=lez.getLezione()%>" onclick="showLezioneDoubleM3(<%=lez.getId_cal1()%>,<%=lez.getId_cal2()%>, <%=lez.getLezione()%>)" class='btn-icon kt-font-io document lezioni'>
                                                                <i class='fa fa-file-invoice kt-font-io' style='font-size: 100px;'></i>
                                                            </a>
                                                        </div>
                                                        <%} else {%>
                                                        <div class='col-6 paddig_0_r' data-container="body" data-html="true" data-original-title="Visualizza lezione" data-toggle="kt-tooltip" title="" style="text-align: center;">
                                                            <a href="javascript:void(0)" id="a_lez<%=lez.getLezione()%>" onclick="showLezioneSingleM3(<%=lez.getId()%>, <%=lez.getLezione()%>)" class='btn-icon kt-font-io document lezioni'>
                                                                <i class='fa fa-file-invoice kt-font-io' style='font-size: 100px;'></i>
                                                            </a>
                                                        </div>

                                                        <%}%> 
                                                        <div class='col-6 paddig_0_l' style='text-align: left;'>
                                                            <a class="btn btn-icon btn-sm btn-success" data-container="body" data-html="true" data-toggle="kt-tooltip" title="">
                                                                <i class="fa fa-check"></i>
                                                            </a>
                                                        </div>
                                                        <div class='offset-1 row'>
                                                            <h5 class='kt-font-io-n'>Lezione <%=lez.getLezione()%></h5>
                                                        </div>
                                                    </div>
                                                </div>
                                                <%}
                                                    }%>
                                                <%if (noloaded) {%>
                                                <h5 class='kt-font-io-n' style='text-align: center;'>Ancora nessuna lezione caricata</h5>
                                                <%}%>
                                            </div>
                                        </div>    
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="kt-portlet kt-portlet--mobile">
                            <div class="kt-portlet__body">
                                <input type="hidden" id="pId" name="pId" value="<%=p.getId()%>">
                                <input type="hidden" id="m4Stato" name="m4Stato" value="<%=m4.getStato()%>">
                                <input type="hidden" id="m4Id" name="m4Id" value="<%=m4.getId()%>">
                                <input type="hidden" id="m4Total" name="m4Total" value="<%=grouppedByLezionem4.size() * gruppi%>">
                                <div class="kt-section kt-section--space-md">
                                    <div class="form-group form-group-sm row">
                                        <div class="col-12">
                                            <h5>Modello 4 <%if (!m4.getStato().equalsIgnoreCase("S") && !m3.getStato().equalsIgnoreCase("OK")) {%>- Gruppi <i id="neet_excluded" style="display:none;" class="fa fa-exclamation-circle kt-font-warning" 
                                                                      data-container="body" 
                                                                      data-toggle="kt-popover" 
                                                                      data-html="true"
                                                                      data-placement="bottom"
                                                                      data-original-title="Allievi Esclusi"
                                                                      data-content=""></i><%}%></h5>
                                            <div class="kt-separator kt-separator--border kt-separator--space-xs"></div>
                                            <div class="progress">
                                                <div id="progressM4" class="progress-bar progress-bar-striped progress-bar-animated  bg-success" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" data-container="body" data-html="true" data-toggle="kt-tooltip" title="" data-original-title="" style="width: 0%"></div>
                                            </div>
                                            <div class="kt-separator kt-separator--border kt-separator--space-xs"></div>
                                            <%if (!m4.getStato().equalsIgnoreCase("S")) {%>
                                            <div class="form-inline">
                                                <%for (int i = 1; i <= gruppi; i++) {%>
                                                <div class="col-md-6 col-lg-6">
                                                    <div class="form-group">
                                                        <label class="col-form-label">Gruppo <%=i%></label>
                                                        <select class="form-control kt-select2 obbligatory"  id="param_<%=i%>" name="param_<%=i%>" multiple="multiple">
                                                        </select>
                                                    </div>
                                                </div>
                                                <%}%>
                                            </div>
                                            <br>
                                            <%} else if(!m3.getStato().equalsIgnoreCase("OK")){%>
                                            <h5 class='kt-font-io-n' >In attesa di completamento del modello 3</h5>
                                            <%}else{%>
                                            <h5 class='kt-font-io-n' >In attesa della creazione dei gruppi</h5>
                                            <%}%>
                                        </div> 
                                        <%if (!m4.getStato().equalsIgnoreCase("S")) {%>
                                        <div class="col-12" id="lezioni_m4" style="display:none;">
                                            <h5>Modello 4 - Calendario</h5>
                                            <div class="kt-separator kt-separator--border kt-separator--space-xs"></div>
                                            <%for (int i = 1; i <= gruppi; i++) {
                                                    noloaded = true;%>
                                            <h5 class="kt-font-io"><i class="fa fa-users kt-font-io"></i> Gruppo <%=i%></h5>
                                            <div class="form-row">
                                                <%for (LezioneCalendario lez : grouppedByLezionem4) {
                                                        if (Utility.lezioneFilteredByGroup(lezionim4, lez.getId(), i) != null) {
                                                            noloaded = false;%>
                                                <div class='col-lg-2 col-md-4 col-sm-6'>
                                                    <div class='row'>
                                                        <%if (lez.isDoppia()) {%>
                                                        <div class="col-6 paddig_0_r" id="msgItem_<%=lez.getLezione()%>_<%=i%>" data-container="body" data-html="true" data-toggle="kt-tooltip" title="" data-original-title="Visualizza lezione" style="text-align: center;">
                                                            <a href="javascript:void(0)" id="a_lez<%=lez.getLezione()%>_<%=i%>" onclick="showLezioneDoubleM4(<%=lez.getId_cal1()%>,<%=lez.getId_cal2()%>, <%=lez.getLezione()%>, <%=i%>)" class='btn-icon kt-font-io document gruppi'>
                                                                <i class='fa fa-file-invoice kt-font-io' style='font-size: 100px;'></i>
                                                            </a>
                                                        </div>
                                                        <%} else {%>
                                                        <div class='col-6 paddig_0_r' id="msgItem_<%=lez.getLezione()%>_<%=i%>" data-container="body" data-html="true" data-toggle="kt-tooltip" title="" data-original-title="Visualizza lezione" style="text-align: center;">
                                                            <a href="javascript:void(0)" id="a_lez<%=lez.getLezione()%>_<%=i%>" onclick="showLezioneSingleM4(<%=lez.getId()%>, <%=lez.getLezione()%>, <%=i%>)" class='btn-icon kt-font-io document gruppi'>
                                                                <i class='fa fa-file-invoice kt-font-io' style='font-size: 100px;'></i>
                                                            </a>
                                                        </div>
                                                        <%}%> 
                                                        <div class='col-6 paddig_0_l' style='text-align: left;'>
                                                            <a class="btn btn-icon btn-sm btn-success" data-container="body" data-html="true" data-toggle="kt-tooltip" title="">
                                                                <i class="fa fa-check"></i>
                                                            </a>
                                                        </div>
                                                        <div class='offset-1 row'>
                                                            <h5 class='kt-font-io-n'>Lezione <%=lez.getLezione()%></h5>
                                                        </div>
                                                    </div>
                                                </div>
                                                <%}
                                                    }%>
                                                <%if (noloaded) {%>
                                                <h5 class='kt-font-io-n' style='text-align: center;'>Ancora nessuna lezione caricata</h5>
                                                <%}%>
                                            </div>
                                            <%if (i != gruppi) {%><div class="kt-separator kt-separator--border kt-separator--space-xs"></div>
                                            <%}
                                                }%>
                                        </div>
                                       <%}%> 
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>
</div>
<div id="kt_scrolltop" class="kt-scrolltop">
    <i class="fa fa-arrow-up"></i>
</div>
<script src="<%=src%>/assets/vendors/general/jquery/dist/jquery.js" type="text/javascript"></script>
<script src="<%=src%>/assets/vendors/general/popper.js/dist/umd/popper.js" type="text/javascript"></script>
<script src="<%=src%>/assets/vendors/general/tooltip.js/dist/umd/tooltip.min.js" type="text/javascript"></script>
<script src="<%=src%>/assets/vendors/general/sticky-js/dist/sticky.min.js" type="text/javascript"></script>
<script src="<%=src%>/assets/vendors/general/bootstrap/dist/js/bootstrap.min.js" type="text/javascript"></script>
<script src="<%=src%>/assets/vendors/general/js-cookie/src/js.cookie.js" type="text/javascript"></script>
<script src="<%=src%>/assets/vendors/general/moment/min/moment.min.js" type="text/javascript"></script>
<script src="<%=src%>/assets/vendors/general/perfect-scrollbar/dist/perfect-scrollbar.js" type="text/javascript"></script>
<script src="<%=src%>/assets/demo/default/base/scripts.bundle.js" type="text/javascript"></script>
<script src="<%=src%>/assets/vendors/general/jquery-form/dist/jquery.form.min.js" type="text/javascript"></script>
<script src="<%=src%>/assets/app/custom/general/components/extended/blockui1.33.js" type="text/javascript"></script>
<script src="<%=src%>/assets/vendors/general/sweetalert2/dist/sweetalert2.js" type="text/javascript"></script>
<script src="<%=src%>/assets/refill/js/utility.js" type="text/javascript"></script>
<script src="<%=src%>/assets/app/bundle/app.bundle.js" type="text/javascript"></script>
<!--this page -->
<script src="<%=src%>/assets/vendors/general/select2/dist/js/select2.full.js" type="text/javascript"></script>
<script src="<%=src%>/assets/app/custom/general/crud/forms/widgets/select2.js" type="text/javascript"></script>
<script src="<%=src%>/assets/vendors/general/bootstrap-select/dist/js/bootstrap-select.js" type="text/javascript"></script>
<script src="<%=src%>/assets/app/custom/general/crud/forms/widgets/bootstrap-datepicker.js" type="text/javascript"></script>
<script src="<%=src%>/assets/vendors/general/bootstrap-datepicker/dist/js/bootstrap-datepicker.js" type="text/javascript"></script>
<script src="<%=src%>/assets/app/custom/general/crud/forms/widgets/bootstrap-timepicker.js" type="text/javascript"></script>
<script src="<%=src%>/assets/vendors/general/bootstrap-timepicker/js/bootstrap-timepicker.js" type="text/javascript"></script>
<input type="hidden" id="systemtype" value="<%=Utility.iswindows()%>" />
<script id="showModelli" defer src="<%=src%>/page/mc/js/showModelli.js<%="?dummy=" + String.valueOf(new Date().getTime())%>" 
data-context="<%=request.getContextPath()%>" type="text/javascript"></script>
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
</body>
</html>
<%}
    }%>
