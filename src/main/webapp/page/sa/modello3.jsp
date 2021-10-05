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
            List<LezioneCalendario> lezioniCalendario = e.getLezioniByModello(3);
            TipoDoc modello = e.getEm().find(TipoDoc.class, 2L);
            int checkStaff = (int) Utility.membriAttivi(p.getStaff_modelli());
            Lezioni_Modelli temp = null;
            ModelliPrg m3 = Utility.filterModello3(p.getModelli());
            List<Lezioni_Modelli> lezioni = m3.getLezioni();
            List<LezioneCalendario> grouppedByLezione = Utility.grouppedByLezione(lezioniCalendario);
            boolean isEditable = Utility.isEditableModel(lezioni);
            e.close();
%>
<html>
    <head>
        <meta charset="utf-8" />
        <title>YES I Start Up - NEET - Modello 3</title>
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
                                <input type="hidden" id="pId" name="pId" value="<%=p.getId()%>">
                                <input type="hidden" id="endPrg" name="endPrg" value="<%=p.getEnd().getTime()%>">
                                <input type="hidden" id="startPrg" name="startPrg" value="<%=p.getStart().getTime()%>">
                                <input type="hidden" id="statoPrg" name="statoPrg" value="<%=m3.getStato()%>">
                            </div>
                            <div class="kt-portlet__body">
                                <div class="kt-section kt-section--space-md">
                                    <div class="form-group form-group-sm row">
                                        <div class="col-12">
                                            <div class="row">
                                                <div class="col-4">
                                                    <h5>Modello 3 - Calendario <i class="fa fa-info-circle kt-font-io" 
                                                                                  data-container="body" 
                                                                                  data-toggle="kt-popover" 
                                                                                  data-html="true"
                                                                                  data-placement="bottom"
                                                                                  data-original-title="Calendario"
                                                                                  data-content="Eventuali variazioni, di date e docenti, dovranno essere <u>preventivamente e 
                                                                                  tempestivamente</u> comunicate all'ENM tramite apposita funzionalit&#224; della piattaforma.
                                                                                  <b>Le modifiche saranno autorizzate mediante aggiornamento del calendario corso validato presente in piattaforma.</b>"></i></h5>

                                                </div>                                               
                                                <div class="col-8">
                                                    <%if (!m3.getStato().equalsIgnoreCase("OK")) {%>
                                                    <div class="btn-group btn-group" role="group" style="float:right">
                                                        <button id="deleteBy" disabled="disabled" class="btn btn-danger kt-font-bold" >Elimina da una data lezione</button>
                                                        <button id="deleteAll" disabled="disabled" class="btn btn-danger kt-font-bold" >Elimina tutte le lezioni</button>
                                                    </div>
                                                    <%}%>
                                                </div>
                                                <div class="kt-separator kt-separator--border kt-separator--space-xs"></div>
                                                <div class="col-12">
                                                    <div class="alert alert-info" role="alert">
                                                        <div class="alert-text">
                                                            <i class="fa fa-info-circle"></i> N.B. Prima di caricare il modello è necessario controllare attentamente che le informazioni riportate nel modello siano le medesime dei calendari (data, ora, docenti).
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="kt-separator kt-separator--border kt-separator--space-xs"></div>
                                            <%
                                                String esito = (String) session.getAttribute("esito3");
                                                if (esito != null && !esito.trim().equals("") && !esito.trim().equals("OK")) {%>
                                            <div class="form-row">
                                                <div class="col-12">
                                                    <div class="alert alert-danger">
                                                        <%=esito%>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="kt-separator kt-separator--border kt-separator--space-xs"></div>
                                            <%}%>
                                            <div class="form-row">
                                                <%for (LezioneCalendario lez : grouppedByLezione) {
                                                        temp = Utility.lezioneFiltered(lezioni, lez.getId());%>
                                                <div class='col-lg-2 col-md-4 col-sm-6'>
                                                    <div class='row'>
                                                        <%if (lez.isDoppia()) {%>
                                                        <div class="col-6 paddig_0_r" id="msgItem_<%=lez.getLezione()%>" data-container="body" data-html="true" data-toggle="kt-tooltip" title="" style="text-align: center;">
                                                            <%if (temp == null) {%>
                                                            <a href="javascript:void(0)" id="a_lez<%=lez.getLezione()%>" onclick="uploadLezioneDouble(<%=p.getId()%>,<%=m3.getId()%>,<%=lez.getLezione()%>)" class='btn-icon kt-font-io document disablelink' >
                                                                <i id="mainIcon_<%=lez.getLezione()%>" class='fa fa-file-invoice kt-font-io' style='font-size: 100px;'></i>
                                                            </a>
                                                            <%} else {%>
                                                            <a href="javascript:void(0)" id="a_lez<%=lez.getLezione()%>" onclick="lezioniDouble(<%=lez.getId_cal1()%>,<%=lez.getId_cal2()%>, <%=lez.getLezione()%>)" class='btn-icon kt-font-io document disablelink'>
                                                                <i class='fa fa-file-invoice' style='font-size: 100px;'></i>
                                                            </a>
                                                            <%}%>
                                                        </div>

                                                        <%} else {%>
                                                        <div class='col-6 paddig_0_r' id="msgItem_<%=lez.getLezione()%>" data-container="body" data-html="true" data-toggle="kt-tooltip" title="" style="text-align: center;">
                                                            <%if (temp == null) {%>
                                                            <a href="javascript:void(0)" id="a_lez<%=lez.getLezione()%>" onclick="uploadLezione(<%=p.getId()%>, <%=m3.getId()%>, <%=lez.getLezione()%>)" class='btn-icon kt-font-io document disablelink' >
                                                                <i id="mainIcon_<%=lez.getLezione()%>" class='fa fa-file-invoice kt-font-io' style='font-size: 100px;'></i>
                                                            </a>
                                                            <%} else {%>
                                                            <a href="javascript:void(0)" id="a_lez<%=lez.getLezione()%>" onclick="lezioniSingle(<%=lez.getId()%>, <%=lez.getLezione()%>)" class='btn-icon kt-font-io document disablelink'>
                                                                <i class='fa fa-file-invoice kt-font-io' style='font-size: 100px;'></i>
                                                            </a>
                                                            <%}%>
                                                        </div>

                                                        <%}%> 
                                                        <div class='col-6 paddig_0_l' style='text-align: left;'>
                                                            <a class="btn btn-icon btn-sm" id="icon_<%=lez.getLezione()%>" data-container="body" data-html="true" data-toggle="kt-tooltip" title="">
                                                                <i id="c_icon<%=lez.getLezione()%>" class="fa fa-check" style="display:none;"></i>
                                                                <i id="q_icon<%=lez.getLezione()%>" class="fa fa-question" style="display:none;"></i>
                                                            </a>
                                                        </div>
                                                        <div class='offset-1 row'>
                                                            <h5 class='kt-font-io-n'>Lezione <%=lez.getLezione()%></h5>
                                                        </div>
                                                    </div>
                                                </div>
                                                <%}%>
                                            </div>

                                            <%if (m3.getStato().equals("R")) {%>
                                            <%if (checkStaff < 2) {%>
                                            <br>
                                            <div class="alert alert-warning" role="alert">
                                                <div class="alert-text">
                                                    <h5 class="alert-heading">Membri Staff - Soggetto Attuatore per accesso alla FAD (Fase A - Fase B)</h5>
                                                    <p>Attenzione, si ricorda che l'inserimento dei membri può essere effettuato solamente prima del caricamento del modello 3.<br>
                                                        Per procedere con l'inserimento di eventuali membri: <br>
                                                        <i class="fa fa-file-alt"></i>&nbsp;<b>Progetti Formativi</b>&emsp;<i class="flaticon2-fast-next"></i>&emsp;<i class="fa fa-search"></i>&nbsp;<b>Cerca</b>&emsp;<i class="flaticon2-fast-next"></i>&emsp;<i class="flaticon-more-1"></i>&nbsp;<b>Azioni (Progetto Formativo)</b>&emsp;<i class="flaticon2-fast-next"></i>&emsp;<i class="flaticon-users-1"></i>&nbsp;<b>Inserisci membri Staff</b>
                                                    </p>
                                                </div>
                                            </div>
                                            <%}%>
                                            <div class="kt-portlet__foot" style="padding-left: 10px;">
                                                <div class="kt-form__actions">
                                                    <form action="<%=request.getContextPath()%>/OperazioniSA" method="POST" target="_blank">
                                                        <input type="hidden" name="type" value="scaricamodello3" />
                                                        <input type="hidden" name="idpr" value="<%=request.getParameter("id")%>" />
                                                        <div class="row">
                                                            <label>Scaricare il modello con il calendario inserito per poi caricarlo
                                                                firmato digitalmente (.p7m CAdES, .pdf PAdES) nel campo sottostante.</label>
                                                            <div class='col-lg-2 col-md-4 col-sm-6'>
                                                                <button class="btn btn-io btn-md btn-tall btn-wide kt-font-bold kt-font-transform-u" 
                                                                        type="submit" 
                                                                        >
                                                                    Scarica
                                                                </button>
                                                            </div>
                                                        </div>
                                                    </form>
                                                    <div class="row">
                                                        <hr>
                                                    </div>
                                                    <form action="<%=request.getContextPath()%>/OperazioniSA" 
                                                          method="POST"
                                                          enctype="multipart/form-data">
                                                        <input type="hidden" name="type" value="salvamodello3" />
                                                        <input type="hidden" name="idpr" value="<%=request.getParameter("id")%>" />
                                                        <input type="hidden" name="idmodello" value="<%=m3.getId()%>" />
                                                        <div class="row">
                                                            <div class='col-lg-4 col-md-4 col-sm-6'>
                                                                <div class="custom-file">
                                                                    <input type="file" required tipo='obbligatory' <%=modello.getObbligatorio() == 1 ? "tipo='obbligatory'" : ""%> 
                                                                           class="custom-file-input" 
                                                                           accept="<%=modello.getMimetype()%>" 
                                                                           name="doc_<%=modello.getId()%>" 
                                                                           onchange="return checkFileExtAndDim('<%=modello.getEstensione()%>');">
                                                                    <label class="custom-file-label selected" 
                                                                           style="color: #a7abc3; text-align: left;">Scegli File</label>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div class="row">
                                                            <hr>
                                                        </div>           
                                                        <div class="row">
                                                            <div class='col-lg-2 col-md-4 col-sm-6'>
                                                                <button type="submit"  class="btn btn-io" onclick=""
                                                                        style="font-family: Poppins"><i class="flaticon2-plus-1"></i> 
                                                                    Carica Modello 3</button>
                                                            </div>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
                                            <%}%>
                                        </div>    
                                    </div>
                                </div>
                            </div>
                        </div>
                        <%if (p.getStato().getId().equalsIgnoreCase("ATA") && isEditable) {%>
                        <div class="kt-portlet kt-portlet--mobile">
                            <div class="kt-portlet__body">
                                <div class="kt-section kt-section--space-md" style="margin-bottom: 0rem;">
                                    <div class="form-group form-group-sm row">
                                        <div class="col-12 text-center">
                                            <p>Un'eventuale modifica del calendario comporterebbe il passaggio del progetto nello step precedente, <br>
                                                con conseguente generazione di un nuovo Modello 3 da firmare digitalmente e caricare sulla piattaforma.</p>
                                            <button id="revert" disabled="disabled" class="btn btn-danger kt-font-bold">Modifica calendario</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <%}%>
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
<script id="modello3" defer src="<%=src%>/page/sa/js/modello3.js" data-context="<%=request.getContextPath()%>" type="text/javascript"></script>
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
