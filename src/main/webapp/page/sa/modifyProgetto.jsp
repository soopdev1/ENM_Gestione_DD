<%-- 
    Document   : uploadDocumet
    Created on : 29-gen-2020, 12.39.45
    Author     : agodino
--%>

<%@page import="it.refill.util.Utility"%>
<%@page import="it.refill.domain.DocumentiPrg"%>
<%@page import="it.refill.domain.NomiProgetto"%>
<%@page import="it.refill.domain.Allievi"%>
<%@page import="it.refill.domain.SediFormazione"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="it.refill.domain.ProgettiFormativi"%>
<%@page import="it.refill.db.Entity"%>
<%@page import="it.refill.db.Action"%>
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
            List<SediFormazione> sedi = e.findAll(SediFormazione.class);
            List<Allievi> alunni = e.getAllieviSoggettoNoPrg(us.getSoggettoAttuatore());
            List<Allievi> alunni_prg = e.getAllieviProgettiFormativi(p);
            List<NomiProgetto> nomi = e.findAll(NomiProgetto.class);

            String ckstato = p.getStato().getId();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            int n_allievi = Integer.parseInt(e.getPath("min_allievi"));
            int max_allievi = Integer.parseInt(e.getPath("max_alunni"));
            e.close();
%>
<html>
    <head>
        <meta charset="utf-8" />
        <title>YES I Start Up - Donne e Disoccupati - Progetti Formativi</title>
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
        <!--this page-->
        <link href="<%=src%>/assets/vendors/general/bootstrap-daterangepicker/daterangepicker.css" rel="stylesheet" type="text/css"/>
        <link href="<%=src%>/assets/vendors/general/select2/dist/css/select2.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/bootstrap-select/dist/css/bootstrap-select.css" rel="stylesheet" type="text/css" />
        <!-- - -->
        <link href="<%=src%>/assets/vendors/general/perfect-scrollbar/css/perfect-scrollbar.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/bootstrap-touchspin/dist/jquery.bootstrap-touchspin.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/general/sweetalert2/dist/sweetalert2.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/line-awesome/css/line-awesome.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/flaticon/flaticon.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/flaticon2/flaticon.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/vendors/custom/vendors/fontawesome5/css/all.min.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/base/style.bundle.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/resource/custom.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/header/base/light.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/header/menu/light.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/brand/light.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/assets/demo/default/skins/aside/light.css" rel="stylesheet" type="text/css" />
        <link href="<%=src%>/resource/animate.css" rel="stylesheet" type="text/css"/>
        <link rel="shortcut icon" href="<%=src%>/assets/media/logos/favicon.ico" />
        <style>
            a.disablelink {
                color: #7c7fb7!important;
                pointer-events: none;
                cursor: default;
            }
        </style>
    </head>
    <body class="kt-header--fixed kt-header-mobile--fixed kt-subheader--fixed kt-subheader--enabled kt-subheader--solid kt-aside--enabled kt-aside--fixed">
        <div class="kt-grid kt-grid--hor kt-grid--root">
            <div class="kt-grid__item kt-grid__item--fluid kt-grid kt-grid--ver kt-page">
                <div class="kt-grid__item kt-grid__item--fluid kt-grid kt-grid--hor">
                    <div class="kt-grid__item kt-grid__item--fluid kt-grid kt-grid--hor">
                        <div class="kt-content  kt-grid__item kt-grid__item--fluid" id="kt_content">
                            <div class="kt-portlet kt-portlet--mobile">
                                <div class="kt-portlet__head">
                                    <div class="kt-portlet__head-label">
                                        <h3 class="kt-portlet__head-title">
                                            Modifica Progetto:
                                        </h3>
                                    </div>
                                </div>
                                <%
                                    DocumentiPrg m2 = Utility.filterM2(p);
                                    if (m2 != null && p.getModello2_check() == 1) {
                                %>
                                <div class="kt-portlet__body" style="padding-bottom: 0px!important">
                                    <div class="alert alert-warning" role="alert">
                                        <form id="kt_form2" name="kt_form2" action="<%=request.getContextPath()%>/OperazioniSA?type=manageM2" class="kt-form kt-form--label-right" method="post" enctype="multipart/form-data" style="width:100%;">
                                            <input type="hidden" id="idfile" name="idfile" value="<%=m2.getId()%>">
                                            <input type="hidden" id="id_prg" name="id_prg" value="<%=p.getId()%>">
                                            <input type="hidden" id="modello" name="modello" value="<%=m2.getTipo().getId()%>"/>
                                            <input type="hidden" id="tipo_op" name="tipo_op"/>
                                            <div class="alert-text">
                                                <h4 class="alert-heading">Modello 2</h4>
                                                <p>Le modifiche effettuate al progetto hanno richiesto la generazione di un nuovo modello.<br>Affinchè sia validato il modello stesso deve essere scaricato, firmato digitalmente e caricato nel campo sottostante.</p>
                                                <hr>
                                                <div class="row">
                                                    <div class="col-sm-5 col-md-4 col-lg-4">
                                                        <button class="btn btn-io btn-md btn-wide kt-font-bold" type="button" target="_blank" onclick="return download_m2();">Scarica nuovo Modello 2</button>                                                                
                                                    </div>
                                                    <div class="col-sm-5 col-md-6 col-lg-6">
                                                        <div class="custom-file" style="float: right;">
                                                            <input type="file" 
                                                                   class="custom-file-input" 
                                                                   accept="<%=m2.getTipo().getMimetype()%>" name="file_m2"  id="file_m2"
                                                                   onchange="return checkFileExtAndDim('<%=m2.getTipo().getEstensione()%>');">
                                                            <label class="custom-file-label selected" 
                                                                   style="color: #a7abc3; text-align: left;">Scegli File</label>
                                                        </div>
                                                    </div>
                                                    <div class="col-sm-2 col-md-2 col-lg-2">
                                                        <button style="float: right; "class="btn btn-io btn-md btn-wide kt-font-bold" type="button" id="upfile"><i class="fa fa-cloud-upload-alt"></i>Carica</button>                                                                
                                                    </div>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                                <%}%>
                                <div class="kt-portlet__body">
                                    <form id="kt_form" action="<%=request.getContextPath()%>/OperazioniSA?type=modifyPrg" class="kt-form kt-form--label-right" accept-charset="ISO-8859-1" method="post">
                                        <input type="hidden" id="id_progetto" name="id_progetto" value="<%=p.getId()%>">
                                        <div class="form-group">
                                            <div class="col-12">
                                                <label>Nome</label><%=p.getStato().getModifiche().getNome() == 1 ? "<label class='kt-font-danger kt-font-boldest'>*</label>" : ""%>
                                                <div class="dropdown bootstrap-select form-control kt- paddig_0" id="nome_pf_div">
                                                    <select class="form-control kt-select2-general obbligatory" <%=p.getStato().getModifiche().getNome() == 1 ? "" : "disabled"%> id="nome_pf" name="nome_pf">
                                                        <option value="-">Seleziona Nome</option>
                                                        <%for (NomiProgetto s : nomi) {
                                                                if (p.getNome().equals(s)) {%>
                                                        <option selected value="<%=s.getId()%>"><%=s.getDescrizione()%></option>
                                                        <%} else {%>
                                                        <option value="<%=s.getId()%>"><%=s.getDescrizione()%></option>
                                                        <%}
                                                            }%>
                                                    </select>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <div class="col-12">
                                                <label>Descrizione</label>
                                                <textarea class="form-control" <%=p.getStato().getModifiche().getDescrizione() == 1 ? "" : "disabled"%> id="descrizione_pf" name="descrizione_pf" rows="5"><%=p.getDescrizione()%></textarea>
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <div class="col-12">
                                                <label>Date Inizio - Fine </label><%=p.getStato().getModifiche().getDate() == 1 ? "<label class='kt-font-danger kt-font-boldest'>*</label>" : ""%>
                                                <input type="text" class="form-control obbligatory" <%=p.getStato().getModifiche().getDate() == 1 ? "id='kt_daterange' readonly" : "disabled"%>  name="date"  value="<%=sdf.format(p.getStart()) + " - " + sdf.format(p.getEnd())%>" autocomplete="off">
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <div class="col-12">
                                                <label>Sede di Formazione</label><%=p.getStato().getModifiche().getSede() == 1 ? "<label class='kt-font-danger kt-font-boldest'>*</label>" : ""%>
                                                <div class="dropdown bootstrap-select form-control kt-" id="sede_div" style="padding: 0;">
                                                    <select class="form-control kt-select2-general obbligatory" id="sede" name="sede"  style="width: 100%" <%=p.getStato().getModifiche().getSede() == 1 ? "" : "disabled"%>>
                                                        <option value="-">Seleziona Sede</option>
                                                        <%for (SediFormazione s : sedi) {
                                                                if (s.equals(p.getSede())) {%>
                                                        <option selected value="<%=s.getId()%>"><%=s.getDenominazione()%></option>
                                                        <%} else {%>
                                                        <option value="<%=s.getId()%>"><%=s.getDenominazione()%></option>
                                                        <%}
                                                            }%>
                                                    </select>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="form-group row col">
                                            <div class="col-12">
                                                <label>Allievi</label><%=p.getStato().getModifiche().getAllievi() == 1 ? "<label class='kt-font-danger kt-font-boldest'>*</label>" : ""%>
                                                <div class="select-div" id="allievi_div">
                                                    <select class="form-control kt-select2 obbligatory" id="allievi" name="allievi[]" multiple="multiple" style="width: 100%" <%=p.getStato().getModifiche().getAllievi() == 1 ? "" : "disabled"%>>
                                                        <%for (Allievi a : alunni_prg) {%>
                                                        <option selected value="<%=a.getId()%>"><%=a.getCognome()%> <%=a.getNome()%></option>
                                                        <%}%>
                                                        <%if (p.getStato().getModifiche().getAllievi() == 1) {
                                                                for (Allievi a : alunni) {%>
                                                        <option value="<%=a.getId()%>"><%=a.getCognome()%> <%=a.getNome()%></option>
                                                        <%}%>
                                                        <%}%>
                                                    </select>
                                                </div>
                                            </div>
                                            <!--                                            <div class="col-6">
                                                                                            <label style="color:#fff;">-</label>
                                                                                        </div>-->
                                        </div>
                                        <label class="kt-font-danger kt-font-bold"><font size="2">* Campi Obbligatori</font></label>
                                        <div class="kt-portlet__foot">
                                            <div class="kt-form__actions">
                                                <div class="row" style="align-items: center;justify-content: center; display:flex;">
                                                    <a id="submit" href="javascript:void(0);" class="btn btn-io ">Salva modifiche</a>
                                                </div>
                                            </div>
                                        </div>
                                    </form> 
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
        <script src="<%=src%>/assets/vendors/general/bootstrap/dist/js/bootstrap.min.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/js-cookie/src/js.cookie.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/moment/min/moment.min.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/tooltip.js/dist/umd/tooltip.min.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/perfect-scrollbar/dist/perfect-scrollbar.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/sticky-js/dist/sticky.min.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/demo/default/base/scripts.bundle.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/app/bundle/app.bundle.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/app/bundle/app.bundle.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/sweetalert2/dist/sweetalert2.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/app/custom/general/components/extended/blockui1.33.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/refill/js/utility.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/jquery-form/dist/jquery.form.min.js" type="text/javascript"></script>
        <!--this page-->
        <script src="<%=src%>/assets/vendors/general/select2/dist/js/select2.full.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/app/custom/general/crud/forms/widgets/select2.js" type="text/javascript"></script>
        <script src="<%=src%>/assets/vendors/general/bootstrap-daterangepicker/daterangepicker.js" type="text/javascript"></script>
        <script id="docenti_allievi" src="<%=src%>/page/sa/js/docenti_allievi.js" data-context="<%=request.getContextPath()%>" type="text/javascript"></script>
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
            var min_allievi = <%=n_allievi%>;
            var max_allievi = <%=max_allievi%>;

            $('#kt_daterange').daterangepicker({
                minDate: new Date(<%=p.getStart().getTime()%>).toLocaleDateString(),
                autoApply: true,
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
                    ]
                }
            });

            function download_m2() {
                $('#tipo_op').val('download');
                $('#kt_form2').submit();
            }

            function checkPdf() {
                let err = false;
                $('#file_m2').each(function () {
                    if ($(this).val() === '') {
                        err = true;
                        $(this).removeClass("is-valid").addClass("is-invalid");
                    } else {
                        $(this).removeClass("is-invalid").addClass("is-valid");
                    }
                });
                return err;
            }

            $('#upfile').on("click", function () {
                $('#tipo_op').val('');
                if (!checkPdf()) {
                    showLoad();
                    $('#kt_form2').ajaxSubmit({
                        error: function () {
                            closeSwal();
                            swalError('Errore', "Riprovare, se l'errore persiste richiedere assistenza");
                        },
                        success: function (resp) {
                            var json = JSON.parse(resp);
                            closeSwal();
                            if (json.result) {
                                swalSuccessCloseFancy("Caricamento modello 2", "Operazione effettuata con successo.");
                            } else {
                                swalError('Errore', "<h4>" + json.message + "</h4>");
                            }
                        }
                    });
                }
            });

            function checkStato() {
                let val = '<%=ckstato%>';
                if (val === "DV") {
                    $('#submit').addClass('disablelink');
                    $('#submit').removeAttr("href");
                }
            }

            jQuery(document).ready(function () {
                checkStato();
            <%=p.getStato().getModifiche().getAllievi() == 1 ? "conoscenzeAllevi();" : ""%>
            });

            function ctrlForm() {
                var err = checkObblFields();
                if ($('#allievi').val().length < min_allievi || $('#allievi').val().length > max_allievi) {//chek num max e min allievi
                    err = true;
                    $('#allievi_div').removeClass("is-valid-select").addClass("is-invalid-select");
                    fastSwalShow("<h3>Numero minimo di allievi non raggiunto</h3>", "wobble");
                }
                return err;
            }

            $('#submit').on("click", function () {
                if (!ctrlForm()) {
                    showLoad();
                    $('#kt_form').ajaxSubmit({
                        error: function () {
                            closeSwal();
                            swalError('Errore', "Riprovare, se l'errore persiste richiedere assistenza");
                        },
                        success: function (resp) {
                            var json = JSON.parse(resp);
                            closeSwal();
                            if (json.result) {
                                swalSuccessReload("Progetto Modificato", "Operazione effettuata con successo.");
                            } else {
                                swalError('Errore', "<h4>" + json.message + "</h4>");
                            }
                        }
                    });
                }
            });

        </script>
    </body>
</html>
<%}
    }%>