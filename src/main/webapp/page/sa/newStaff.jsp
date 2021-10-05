
<%@page import="java.util.Date"%>
<%@page import="it.refill.domain.StaffModelli"%>
<%@page import="it.refill.domain.ProgettiFormativi"%>
<%@page import="it.refill.entity.Item"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="it.refill.domain.User"%>
<%@page import="it.refill.db.Entity"%>
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
            String src = session.getAttribute("src").toString();
            int nro_staff = 2;
%>
<html>
    <head>
        <meta charset="utf-8" />
        <title>YES I Start Up - Donne e Disoccupati - Membri Staff</title>
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
        <!-- - -->
        <link href="<%=src%>/assets/vendors/general/owl.carousel/dist/assets/owl.carousel.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/owl.carousel/dist/assets/owl.theme.default.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/sweetalert2/dist/sweetalert2.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/line-awesome/css/line-awesome.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/flaticon/flaticon.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/flaticon2/flaticon.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/fontawesome5/css/all.min.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/resource/animate.css" rel="stylesheet" type="text/css"/>
        <link href="<%=src%>/assets/demo/default/base/style.bundle.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/resource/custom.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/header/base/light.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/header/menu/light.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/brand/light.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/aside/light.css" rel="stylesheet" type="text/css" />


        <link rel="shortcut icon" href="<%=src%>/assets/media/logos/favicon.ico" />
        <style type="text/css">
            .kt-section__title {
                font-size: 1.2rem!important;
            }

            .form-group {
                margin-bottom: 1rem;
            }

            .custom-file-label::after {
                color:#fff;
                background-color: #eaa21c;
            }

            a.disablelink {
                color: #aaa!important;
                cursor: default;
                pointer-events: none;
                background-color: #686dd5!important;
                border-color: #686dd5!important;
            }
        </style>
    </head>
    <body class="kt-header--fixed kt-header-mobile--fixed kt-subheader--fixed kt-subheader--enabled kt-subheader--solid kt-aside--enabled kt-aside--fixed">
        <div class="kt-grid kt-grid--hor kt-grid--root">
            <div class="kt-grid__item kt-grid__item--fluid kt-grid kt-grid--ver kt-page">
                <div class="kt-grid__item kt-grid__item--fluid kt-grid kt-grid--hor">
                    <div class="kt-content  kt-grid__item kt-grid__item--fluid" id="kt_content">
                        <div class="row">
                            <div class="col-lg-12">
                                <div class="kt-portlet" id="kt_portlet" data-ktportlet="true">
                                    <div class="kt-portlet__body">
                                        <h5>Inserimento membri dello Staff Soggetto Attuatore per accesso alla FAD (Fase A - Fase B)</h5>
                                    </div>
                                    <div class="kt-portlet__foot">
                                        <div class="kt-form__actions">
                                            <div class="row">
                                                    L'inserimento di membri dello staff non è un requisito obbligatorio per la prosecuzione del Progetto Formativo.<br>
                                                    Tuttavia, si ricorda che un eventuale caricamento degli stessi (massimo 2) è possibile solamente prima del caricamento del modello 3. 
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <%for (int i = 0; i < nro_staff; i++) {
                                int cnt = i + 1;%>
                        <div class="row">
                            <div class="col-lg-12">
                                <div class="kt-portlet" id="kt_portlet" data-ktportlet="true">
                                    <form id="kt_form_<%=i%>" action="<%=request.getContextPath()%>/OperazioniSA?type=manageMembriStaff&row=<%=i%>" class="kt-form kt-form--label-right" method="post" >
                                        <div class="kt-portlet__body" style="padding: 25px 25px 5px 25px;">
                                            <input type="hidden" name="mid_<%=i%>" id="mid_<%=i%>" />
                                            <input type="hidden" name="pf<%=i%>" value="<%=request.getParameter("id")%>" />
                                            <h5>Membro #<%=cnt%></h5> 
                                            <div class="kt-separator kt-separator--border kt-separator--space-xs"></div>
                                            <div class="kt-section kt-section--first">
                                                <div class="kt-section__body">
                                                    <div class="form-group row">
                                                        <div class="col-xl-3 col-lg-3 col-md-3 col-sm-6">
                                                            <label for="nome<%=i%>">Nome </label><label class="kt-font-danger kt-font-boldest">*</label>
                                                            <input class="form-control obbligatory" name="nome<%=i%>" id="nome<%=i%>">
                                                        </div>
                                                        <div class="col-xl-3 col-lg-3 col-md-3 col-sm-6">
                                                            <label for="cognome<%=i%>">Cognome </label><label class="kt-font-danger kt-font-boldest">*</label>
                                                            <input class="form-control obbligatory" name="cognome<%=i%>" id="cognome<%=i%>">
                                                        </div>
                                                        <div class="col-xl-3 col-lg-3 col-md-3 col-sm-6">
                                                            <label for="email<%=i%>">Email </label><label class="kt-font-danger kt-font-boldest">*</label>
                                                            <input class="form-control obbligatory" name="email<%=i%>" id="email<%=i%>">
                                                        </div>
                                                        <div class="col-xl-3 col-lg-3 col-md-3 col-sm-6">
                                                            <label for="telefono<%=i%>">Telefono </label><label class="kt-font-danger kt-font-boldest">*</label>
                                                            <input type="text" class="form-control obbligatory" id="telefono<%=i%>" name="telefono<%=i%>" onkeypress="return isNumber(event);" />
                                                        </div>
                                                    </div>
                                                        <div class="row kt-font-danger" id="sameMember<%=i%>" style="margin-left: 0px; display: none;">
                                                            <b>Il membro che si sta tentando di inserire/modificare è identico a quello già caricato.</b>
                                                        </div>
                                                </div>        
                                            </div>  
                                            <div class="kt-portlet__foot" style="padding: 10px;">
                                                <div class="kt-form__actions">
                                                    <div class="row">
                                                        <div class="col-12 kt-align-center">
                                                            <a id="submit_<%=i%>" href="javascript:void(0);" class="btn btn-io" style="width:12%;"><font color='white'>Salva</font></a>
                                                            <a id="delete_<%=i%>" onclick="deleteMembro(<%=i%>)" href="javascript:void(0);" class="btn btn-danger" style="width:12%;display: none;"><font color='white'>Elimina</font></a>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                        <%}%>
                    </div>
                </div>
            </div>
        </div>
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
        <script src="<%=src%>/assets/vendors/general/jquery-form/dist/jquery.form.min.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/demo/default/base/scripts.bundle.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/app/bundle/app.bundle.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/sweetalert2/dist/sweetalert2.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/refill/js/utility.js" type="text/javascript"></script>
        <!-- this page -->
        <script src="<%=src%>/assets/app/custom/general/crud/forms/widgets/bootstrap-datepicker.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/bootstrap-datepicker/dist/js/bootstrap-datepicker.js" type="text/javascript"></script>
        <script id="newStaff" src="<%=src%>/page/sa/js/newStaff.js?<%="?dummy=" + String.valueOf(new Date().getTime())%>" data-context="<%=request.getContextPath()%>" defer pId="<%=request.getParameter("id")%>" nro="2" type="text/javascript"></script> 
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
<%
        }
    }
%>