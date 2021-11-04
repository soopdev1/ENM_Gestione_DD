/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import it.refill.db.Action;
import it.refill.db.Entity;
import it.refill.domain.Allievi;
import it.refill.domain.Ateco;
import it.refill.domain.Attivita_Docente;
import it.refill.domain.Canale;
import it.refill.domain.Comuni;
import it.refill.domain.Condizione_Lavorativa;
import it.refill.domain.Condizione_Mercato;
import it.refill.domain.Docenti;
import it.refill.domain.DocumentiPrg;
import it.refill.domain.Documenti_Allievi;
import it.refill.domain.Faq;
import it.refill.domain.FasceDocenti;
import it.refill.domain.Formagiuridica;
import it.refill.domain.LezioneCalendario;
import it.refill.domain.Lezioni_Modelli;
import it.refill.domain.MascheraM5;
import it.refill.domain.ModelliPrg;
import it.refill.domain.Motivazione;
import it.refill.domain.Nazioni_rc;
import it.refill.domain.NomiProgetto;
import it.refill.domain.ProgettiFormativi;
import it.refill.domain.SediFormazione;
import it.refill.domain.Selfiemployment_Prestiti;
import it.refill.domain.SoggettiAttuatori;
import it.refill.domain.StaffModelli;
import it.refill.domain.StatiPrg;
import it.refill.domain.StatoPartecipazione;
import it.refill.domain.Storico_Prg;
import it.refill.domain.TipoDoc;
import it.refill.domain.TipoDoc_Allievi;
import it.refill.domain.TipoFaq;
import it.refill.domain.TitoliStudio;
import it.refill.domain.Tracking;
import it.refill.domain.User;
import it.refill.entity.Presenti;
import it.refill.util.Pdf_new;
import static it.refill.util.Pdf_new.checkFirmaQRpdfA;
import it.refill.util.Utility;
import static it.refill.util.Utility.copyR;
import static it.refill.util.Utility.createDir;
import static it.refill.util.Utility.getRequestValue;
import static it.refill.util.Utility.getStartPath;
import static it.refill.util.Utility.patternComplete;
import static it.refill.util.Utility.redirect;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import static java.lang.String.format;
import static java.nio.file.Files.probeContentType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import javax.persistence.PersistenceException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

/**
 *
 * @author dolivo
 */
public class OperazioniSA extends HttpServlet {

    protected void updtProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        JsonObject resp = new JsonObject();
        User us = (User) request.getSession().getAttribute("user");
        try {
            e.begin();
            String piva = request.getParameter("piva");
            String cf = request.getParameter("cf");
            boolean check_piva = e.getUserPiva(piva) == null || piva.equalsIgnoreCase(us.getSoggettoAttuatore().getPiva() == null ? "" : us.getSoggettoAttuatore().getPiva());
            boolean check_cf = e.getUserCF(cf) == null || cf.equalsIgnoreCase(us.getSoggettoAttuatore().getCodicefiscale() == null ? "" : us.getSoggettoAttuatore().getCodicefiscale());
            if (check_cf || check_piva) {
                Part p = request.getPart("cartaid");
                String path = request.getParameter("cartaidpath");
                if (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0) {
                    String ext = p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                    path = e.getPath("pathDocSA").replace("@folder", Utility.correctName(request.getParameter("ragionesociale")));
                    File dir = new File(path);
                    dir.mkdirs();
                    path += Utility.correctName(request.getParameter("nome_ad") + "_" + request.getParameter("cognome_ad")) + ext;
                    p.write(path);
                }
                us.getSoggettoAttuatore().setCartaid(path);
                us.getSoggettoAttuatore().setRagionesociale(request.getParameter("ragionesociale"));
                us.getSoggettoAttuatore().setPiva(!piva.equalsIgnoreCase("") ? piva : null);
                us.getSoggettoAttuatore().setCodicefiscale(!cf.equalsIgnoreCase("") ? cf : null);
                us.getSoggettoAttuatore().setEmail(request.getParameter("email"));
                us.getSoggettoAttuatore().setPec(request.getParameter("pec"));
                us.getSoggettoAttuatore().setTelefono_sa(request.getParameter("telefono_sa"));
                us.getSoggettoAttuatore().setCell_sa(request.getParameter("cell_sa"));
                us.getSoggettoAttuatore().setIndirizzo(request.getParameter("indirizzo"));
                us.getSoggettoAttuatore().setCap(request.getParameter("cap"));
                us.getSoggettoAttuatore().setComune((Comuni) e.getEm().find(Comuni.class, Long.parseLong(request.getParameter("comune"))));
                us.getSoggettoAttuatore().setNome(request.getParameter("nome"));
                us.getSoggettoAttuatore().setCognome(request.getParameter("cognome"));
                us.getSoggettoAttuatore().setNro_documento(request.getParameter("nrodocumento"));
                us.getSoggettoAttuatore().setDatanascita(sdf.parse(request.getParameter("datanascita")));
                us.getSoggettoAttuatore().setNome_refente(request.getParameter("nome_ref"));
                us.getSoggettoAttuatore().setCognome_referente(request.getParameter("cognome_ref"));
                us.getSoggettoAttuatore().setTelefono_referente(request.getParameter("tel_ref"));
                us.getSoggettoAttuatore().setScadenza(sdf.parse(request.getParameter("scadenza")));
                us.setEmail(us.getSoggettoAttuatore().getEmail());
                e.merge(us);
                e.flush();
                e.commit();
                request.getSession().setAttribute("user", us);
                resp.addProperty("result", true);
            } else {
                resp.addProperty("result", false);
                if (!check_cf) {
                    resp.addProperty("message", "Errore: non &egrave; stato possibile registrarsi.<br>Codice Fiscale gi&agrave; presente");
                } else if (!check_piva) {
                    resp.addProperty("message", "Errore: non &egrave; stato possibile registrarsi.<br>P.IVA gi&agrave; presente");
                }
                resp.addProperty("message", "Errore: non &egrave; stato possibile registrarsi.<br>CF o P.IVA gi&agrave; presente");
            }
        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
            e.insertTracking(null, "saveSoggettoAttuatore Errore: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile aggiornare le informazioni del profilo.<br>Riprovare, se l'errore persiste contattare l'assistenza");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void checkCF(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        String cf = request.getParameter("cf");
        Entity e = new Entity();
        Allievi a = e.getAllievoCF(cf);
        e.close();
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(a));
    }

    protected void getCodiceCatastaleComune(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        String idcomune = request.getParameter("idcomune");
        String cod;
        Entity e = new Entity();
        Comuni comune = e.getComune(Long.parseLong(idcomune));
        e.close();
        if (comune != null) {
            cod = comune.getIstat();
            cod += comune.getCodicicatastali_altri() != null ? ("-" + comune.getCodicicatastali_altri()) : "";
        } else {
            cod = "-";
        }
        response.getWriter().write(cod);
    }

    protected void newAllievo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String save = getRequestValue(request, "save");

        boolean salvataggio = false;
        if (save.equals("1")) {//MODELLO
            salvataggio = false;
        } else if (save.equals("0")) {
            salvataggio = true;
        }
        File downloadFile = null;

        Entity e = new Entity();
        e.begin();

        String qrcrop = e.getPath("qr_crop");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        JsonObject resp = new JsonObject();
        User us = (User) request.getSession().getAttribute("user");
        try {

            List<Documenti_Allievi> documenti = new ArrayList<>();
            List<TipoDoc_Allievi> tipo_doc = e.getTipoDocAllievi_ALL(e.getEm().find(StatiPrg.class, "DV"));

            String codicefiscale = request.getParameter("codicefiscale");
            if (e.getAllievoCF(codicefiscale) == null) {
                String path = e.getPath("pathDocSA_Allievi")
                        .replace("@rssa", Utility.correctName(us.getSoggettoAttuatore().getId() + ""))
                        .replace("@folder", Utility.correctName(request.getParameter("codicefiscale")));
                File dir = new File(path);
                createDir(path);
                Date d_today = new Date();
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
                String today = sdf2.format(d_today);
                Allievi a = new Allievi();

                if (salvataggio) {
                    Part p = request.getPart("docid");
                    String ext = p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                    String pathid = dir.getAbsolutePath() + File.separator + "docId_" + today + "_" + request.getParameter("codicefiscale") + ext;
                    p.write(pathid);
                    a.setDocid(pathid);
                }

                Nazioni_rc nazionenascita = (Nazioni_rc) e.getEm().find(Nazioni_rc.class, Long.parseLong(request.getParameter("cittadinanza")));

                a.setCittadinanza(nazionenascita);

                a.setStato_nascita(request.getParameter("stato"));
                if (a.getStato_nascita().equals("100")) {
                    a.setComune_nascita(e.getEm().find(Comuni.class, Long.parseLong(request.getParameter("comunenascita"))));
                } else {
//                    a.setComune_nascita(e.getComunibyIstat(nazionenascita));
                    Comuni statoEstero = e.byIstatEstero(a.getStato_nascita());
                    if (statoEstero == null) {
                        Nazioni_rc n = e.byCodiceFiscale(a.getStato_nascita());
                        statoEstero = new Comuni();
                        statoEstero.setCittadinanza(1);
                        statoEstero.setCod_comune("0");
                        statoEstero.setCod_provincia("0");
                        statoEstero.setCod_regione("0");
                        statoEstero.setCodicicatastali_altri(null);
                        statoEstero.setIstat(n.getCodicefiscale());
                        statoEstero.setNome(n.getNome());
                        statoEstero.setNome_provincia(n.getNome());
                        statoEstero.setProvincia(n.getUe());
                        statoEstero.setRegione(n.getNome());
                    }
                    a.setComune_nascita(statoEstero);
                }

                if (request.getParameter("prv2") != null) {
                    a.setPrivacy2("SI");
                } else {
                    a.setPrivacy2("NO");
                }
                if (request.getParameter("prv3") != null) {
                    a.setPrivacy3("SI");
                } else {
                    a.setPrivacy3("NO");
                }

                a.setNome(new String(request.getParameter("nome").toUpperCase().getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                a.setCognome(new String(request.getParameter("cognome").toUpperCase().getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                a.setCodicefiscale(request.getParameter("codicefiscale").toUpperCase());
                a.setTelefono(request.getParameter("telefono"));
                a.setDatanascita(sdf.parse(request.getParameter("datanascita")));
                a.setIndirizzoresidenza(new String(request.getParameter("indirizzores").toUpperCase().getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                a.setCivicoresidenza(request.getParameter("civicores").toUpperCase());
                a.setCapresidenza(request.getParameter("capres"));
                a.setComune_residenza((Comuni) e.getEm().find(Comuni.class, Long.parseLong(request.getParameter("comuneres"))));

                boolean domiciliouguale = request.getParameter("checkind") != null;

                if (domiciliouguale) {
                    a.setIndirizzodomicilio(new String(request.getParameter("indirizzores").toUpperCase().getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                    a.setCivicodomicilio(request.getParameter("civicores").toUpperCase());
                    a.setCapdomicilio(request.getParameter("capres"));
                    a.setComune_domicilio((Comuni) e.getEm().find(Comuni.class, Long.parseLong(request.getParameter("comuneres"))));
                } else {
                    a.setCapdomicilio(request.getParameter("capdom"));
                    a.setIndirizzodomicilio(new String(request.getParameter("indirizzodom").toUpperCase().getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                    a.setCivicodomicilio(request.getParameter("civicodom").toUpperCase());
                    a.setComune_domicilio((Comuni) e.getEm().find(Comuni.class, Long.parseLong(request.getParameter("comunedom"))));
                }

                a.setScadenzadocid(sdf.parse(request.getParameter("scadenzadoc")));
                a.setTitoloStudio((TitoliStudio) e.getEm().find(TitoliStudio.class, request.getParameter("titolo_studio")));
                a.setSoggetto((SoggettiAttuatori) us.getSoggettoAttuatore());

                a.setMotivazione((Motivazione) e.getEm().find(Motivazione.class, Integer.parseInt(request.getParameter("motivazione"))));
                a.setCanale((Canale) e.getEm().find(Canale.class, Integer.parseInt(request.getParameter("canale"))));
                a.setCondizione_mercato((Condizione_Mercato) e.getEm().find(Condizione_Mercato.class, request.getParameter("condizione")));
                //a.setTarget(formatTarget(request.getParameter("target")));
                a.setTarget(request.getParameter("target"));

                //29-04-2020 MODIFICA - CONDIZIONE LAVORATIVA PRECEDENTE
                a.setCondizione_lavorativa((Condizione_Lavorativa) e.getEm().find(Condizione_Lavorativa.class, Integer.parseInt(request.getParameter("condizione_lavorativa"))));
                a.setNeet(e.getEm().find(Condizione_Lavorativa.class, Integer.parseInt(request.getParameter("condizione_lavorativa"))).getDescrizione());
                a.setStatopartecipazione((StatoPartecipazione) e.getEm().find(StatoPartecipazione.class, "01"));
                a.setEmail(request.getParameter("email"));
                a.setSesso(Integer.parseInt(request.getParameter("codicefiscale").substring(9, 11)) > 40 ? "F" : "M");
                a.setData_up(d_today);
                a.setData_anpal("");

                if (salvataggio) {
                    boolean modello1OK = false;
                    String erroremodello1OK = "MODELLO 1 ERRATO. CONTROLLARE.";
                    try {
                        TipoDoc_Allievi modello1 = tipo_doc.stream().filter(m1 -> m1.getId() == 3L).findAny().orElse(null);
                        Part p = request.getPart("doc_" + modello1.getId());

                        if (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0) {
                            a.setStato("A");
                            try {
                                String ext = p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                                String destpath = dir.getAbsolutePath() + File.separator + modello1.getDescrizione() + today + "_" + request.getParameter("codicefiscale") + ext;
                                p.write(destpath);
                                File pdfdest = new File(destpath);
                                String res = checkFirmaQRpdfA("MODELLO1", us.getUsername(), pdfdest, us.getSoggettoAttuatore().getCodicefiscale(), qrcrop);
                                if (!res.equals("OK")) {
                                    modello1OK = false;
                                    erroremodello1OK = res;
                                } else {
                                    modello1OK = true;
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                modello1OK = false;
                                erroremodello1OK = "MODELLO 1 ERRATO. " + ex.getMessage() + ". CONTROLLARE.";
                            }
                        } else {
                            a.setStato("I");
                        }

                        if (modello1OK || a.getStato().equalsIgnoreCase("I")) {
                            e.persist(a);
                            String ext, destpath;
                            for (TipoDoc_Allievi t : tipo_doc) {
                                try {
                                    Part pa = request.getPart("doc_" + t.getId());
                                    ext = pa.getSubmittedFileName().substring(pa.getSubmittedFileName().lastIndexOf("."));
                                    destpath = dir.getAbsolutePath() + File.separator + t.getDescrizione() + today + "_" + request.getParameter("codicefiscale") + ext;
                                    pa.write(destpath);
                                    documenti.add(new Documenti_Allievi(destpath, t, null, a));
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }

                            a.setDocumenti(documenti);
                            e.merge(a);
                            e.flush();
                            e.commit();
                            resp.addProperty("result", true);
                        } else {
                            resp.addProperty("result", false);
                            resp.addProperty("message", erroremodello1OK);
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    e.rollBack();
                    if (save.equals("1")) { //MODELLO
                        downloadFile
                                = Pdf_new.MODELLO1(e, "3", us.getUsername(),
                                        us.getSoggettoAttuatore(), a,
                                        new DateTime(), domiciliouguale,
                                        true);
                    }
                    resp.addProperty("result", true);
                }
            } else {
                resp.addProperty("result", false);
                resp.addProperty("message", "Errore: non &egrave; stato possibile aggiungere l'allievo.<br>Il seguente codice fiscale gi&agrave; presente");
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
            e.insertTracking(null, "newAllievo Errore: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile aggiungere l'allievo.<br>Riprovare, se l'errore persiste contattare l'assistenza");
        } finally {
            e.close();
        }

        if (salvataggio) {
            response.getWriter()
                    .write(resp.toString());
            response.getWriter()
                    .flush();
            response.getWriter()
                    .close();
        } else {
            if (downloadFile != null && downloadFile.exists()) {
                OutputStream outStream;
                try (FileInputStream inStream = new FileInputStream(downloadFile)) {
                    String mimeType = probeContentType(downloadFile.toPath());
                    if (mimeType == null) {
                        mimeType = "application/octet-stream";
                    }
                    response.setContentType(mimeType);
                    String headerKey = "Content-Disposition";
                    String headerValue = format("attachment; filename=\"%s\"", downloadFile.getName());
                    response.setHeader(headerKey, headerValue);
                    outStream = response.getOutputStream();
                    byte[] buffer = new byte[4096 * 4096];
                    int bytesRead;
                    while ((bytesRead = inStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }
                }
                outStream.close();
            } else {
                redirect(request, response, request.getContextPath() + "/404.jsp");
            }
        }
    }

    protected void salvamodello4(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.getSession().setAttribute("esito4", null);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        Entity e = new Entity();
        e.begin();
        String qrcrop = e.getPath("qr_crop");
        ModelliPrg m = e.getEm().find(ModelliPrg.class, Long.parseLong(getRequestValue(request, "idmodello")));
        String idpr = getRequestValue(request, "idpr");
        ProgettiFormativi pr = e.getEm().find(ProgettiFormativi.class, Long.parseLong(idpr));

        TipoDoc modello = e.getEm().find(TipoDoc.class, 6L);
        User us = (User) request.getSession().getAttribute("user");
        String path = e.getPath("pathDocSA_Prg").replace("@rssa",
                us.getSoggettoAttuatore().getId().toString()).replace("@folder",
                pr.getId().toString());

        String file_path;
        String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());
        File dir = new File(path);
        createDir(path);
        Part part = request.getPart("doc_" + modello.getId());
        if (part != null && part.getSubmittedFileName() != null && part.getSubmittedFileName().length() > 0) {
            file_path = dir.getAbsolutePath()
                    + File.separator
                    + modello.getDescrizione()
                    + "_"
                    + today
                    + part.getSubmittedFileName().substring(part.getSubmittedFileName().lastIndexOf("."));
            part.write(file_path);
            File pdfdest = new File(file_path);
            String res = checkFirmaQRpdfA("MODELLO4", us.getUsername(), pdfdest, us.getSoggettoAttuatore().getCodicefiscale(), qrcrop);
            if (res.equals("OK")) {
                DocumentiPrg doc = new DocumentiPrg();
                doc.setPath(file_path);
                doc.setTipo(modello);
                doc.setProgetto(pr);
                e.persist(doc);
                List<DocumentiPrg> documenti = pr.getDocumenti();
                documenti.add(doc);
                pr.setDocumenti(documenti);
                //29-06-21 Richiesta di passare lo stato direttamente in accettato da parte del micro (si salta da ATA ad ATB)
                pr.setStato(e.getEm().find(StatiPrg.class,
                        "ATB"));//pr.setStato(e.getEm().find(StatiPrg.class, "DVA"));
                //Ri-setto il giorno di fine progetto, inserisco infatti quello dell'ultima lezione del modello m4 
                Date ultimaLezM4 = Utility.getUltimoGiornoLezioneM4(pr);
                if (ultimaLezM4 != null) {
                    pr.setEnd(ultimaLezM4);
                }
                m.setStato("OK");
                e.merge(pr);
                e.merge(m);
                e.flush();
                e.commit();
                e.close();
                redirect(request, response, request.getContextPath() + "/page/sa/modello4.jsp?id=" + idpr);
            } else {
                e.close();
                pdfdest.delete();
                request.getSession().setAttribute("esito4", res);
                redirect(request, response, request.getContextPath() + "/page/sa/modello4.jsp?id=" + idpr + "&esito=ko");
            }
        } else {
            request.getSession().setAttribute("esito4", "ERRORE DURANTE IL CARICAMENTO DEL FILE. RIPROVARE.");
            e.close();
            redirect(request, response, request.getContextPath() + "/page/sa/modello4.jsp?id=" + idpr + "&esito=ko");
        }

    }

    protected void salvamodello3(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        Entity e = new Entity();
        e.begin();
        String qrcrop = e.getPath("qr_crop");
        ModelliPrg m = e.getEm().find(ModelliPrg.class, Long.parseLong(getRequestValue(request, "idmodello")));
        String idpr = getRequestValue(request, "idpr");
        ProgettiFormativi pr = e.getEm().find(ProgettiFormativi.class, Long.parseLong(idpr));
        TipoDoc modello = e.getEm().find(TipoDoc.class, 2L);
        User us = (User) request.getSession().getAttribute("user");
        String path = e.getPath("pathDocSA_Prg").replace("@rssa",
                us.getSoggettoAttuatore().getId().toString()).replace("@folder",
                pr.getId().toString());

        String file_path;
        String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());
        File dir = new File(path);
        createDir(path);
        Part part = request.getPart("doc_" + modello.getId());
        if (part != null && part.getSubmittedFileName() != null && part.getSubmittedFileName().length() > 0) {
            file_path = dir.getAbsolutePath()
                    + File.separator
                    + modello.getDescrizione()
                    + "_"
                    + today
                    + part.getSubmittedFileName().substring(part.getSubmittedFileName().lastIndexOf("."));
            part.write(file_path);

            File pdfdest = new File(file_path);
            String res = checkFirmaQRpdfA("MODELLO3", us.getUsername(), pdfdest, us.getSoggettoAttuatore().getCodicefiscale(), qrcrop);
            if (res.equals("OK")) {
                DocumentiPrg doc = new DocumentiPrg();
                doc.setPath(file_path);
                doc.setTipo(modello);
                doc.setProgetto(pr);
                e.persist(doc);
                List<DocumentiPrg> documenti = pr.getDocumenti();
                documenti.add(doc);
                pr.setDocumenti(documenti);

                //29-06-21 Richiesta di passare lo stato direttamente in accettato in caso di modifica (SOA -> ATA)
                if (pr.getStato().getId().equalsIgnoreCase("SOA")
                        || pr.getStato().getId().equalsIgnoreCase("ATA")) {
                    pr.setStato(e.getEm().find(StatiPrg.class,
                            "ATA"));
                } else {
                    pr.setStato(e.getEm().find(StatiPrg.class,
                            "DC"));
                }
                //Ri-setto il giorno di inizio progetto, inserisco infatti quello della prima lezione del modello m3 
                Date primaLezM3 = Utility.getPrimoGiornoLezioneM3(pr);
                if (primaLezM3 != null) {
                    pr.setStart(primaLezM3);
                }

                m.setStato("OK");
                e.merge(pr);
                e.merge(m);
                e.flush();
                e.commit();
                redirect(request, response, request.getContextPath() + "/page/sa/modello3.jsp?id=" + idpr);
            } else {
                e.close();
                pdfdest.delete();
                request.getSession().setAttribute("esito3", res);
                redirect(request, response, request.getContextPath() + "/page/sa/modello3.jsp?id=" + idpr + "&esito=ko");
            }
        } else {
            request.getSession().setAttribute("esito3", "ERRORE DURANTE IL CARICAMENTO DEL FILE. RIPROVARE.");
            e.close();
            redirect(request, response, request.getContextPath() + "/page/sa/modello3.jsp?id=" + idpr + "&esito=ko");
        }

    }

    protected void newProgettoFormativo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String save = getRequestValue(request, "save");
        String modellodacompilare = null;
        boolean salvataggio = false;
        if (save.equals("1")) {//MODELLO
            modellodacompilare = getRequestValue(request, "modello");
            salvataggio = false;
        } else if (save.equals("0")) {
            salvataggio = true;
        }

        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        e.begin();
        String qrcrop = e.getPath("qr_crop");
        File downloadFile = null;

        String[] date = request.getParameter("date").split("-");
        ArrayList<Docenti> docenti_list = new ArrayList<>();
        User us = (User) request.getSession().getAttribute("user");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            ProgettiFormativi p = new ProgettiFormativi();
            p.setNome(e.getEm().find(NomiProgetto.class,
                    Long.parseLong(request.getParameter("nome_pf"))));
            p.setDescrizione(new String(request.getParameter("descrizione_pf").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
            p.setStart(sdf.parse(date[0].trim()));
            p.setEnd(sdf.parse(date[1].trim()));
            p.setSede(e.getEm().find(SediFormazione.class,
                    Long.parseLong(request.getParameter("sede"))));
            p.setSoggetto(us.getSoggettoAttuatore());
            p.setStato(e.getEm().find(StatiPrg.class, "DV"));
            p.setControllable(1);
            p.setData_up(new Date());
            e.persist(p);
            e.flush();

            //Add 30/03/21 ---> Creazione modelli M3 ed M4
            ModelliPrg m3 = new ModelliPrg("S", 3, new Date(), p);
            e.persist(m3);
            ModelliPrg m4 = new ModelliPrg("S", 4, new Date(), p);
            e.persist(m4);
            e.merge(p);

            //setto allievi
            List<Allievi> list_allievi = new LinkedList<>();
            for (String s : request.getParameterValues("allievi[]")) {
                Allievi a = e.getEm().find(Allievi.class,
                        Long.parseLong(s));
                a.setProgetto(p);

                list_allievi.add(a);
                e.merge(a);
            }
            //setto ai documenti
            List<TipoDoc> tipo_doc = e.getTipoDoc(e.getEm().find(StatiPrg.class, "DV"));

            ArrayList<DocumentiPrg> documenti = new ArrayList<>();
            DocumentiPrg doc;
            String path = e.getPath("pathDocSA_Prg").replace("@rssa", us.getSoggettoAttuatore().getId().toString()).replace("@folder", p.getId().toString());

            String file_path;
            String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());
            File dir = new File(path);
            createDir(path);
            boolean copyfile1 = true;
            boolean copyfile2 = true;
            Part part;
            if (salvataggio) {
                for (TipoDoc t : tipo_doc) {
                    part = request.getPart("doc_" + t.getId());
                    if (part != null && part.getSubmittedFileName() != null && part.getSubmittedFileName().length() > 0) {
                        file_path = dir.getAbsolutePath() + File.separator + t.getDescrizione() + "_"
                                + today + part.getSubmittedFileName().substring(part.getSubmittedFileName().lastIndexOf("."));
                        part.write(file_path);
                        doc = new DocumentiPrg();
                        doc.setPath(file_path);
                        doc.setTipo(t);
                        doc.setProgetto(p);
                        documenti.add(doc);

                        //controllare modello2
                    }
                }
            }

            //setto docenti e documenti docenti
            Docenti d;
            for (String s : request.getParameterValues("docenti[]")) {
                d = e.getEm().find(Docenti.class,
                        Long.parseLong(s));
                docenti_list.add(d);
                doc = new DocumentiPrg();
                file_path = dir.getAbsolutePath() + File.separator + "DocId_" + today + "_" + d.getId() + d.getDocId().substring(d.getDocId().lastIndexOf("."));
                if (salvataggio) {
                    copyfile1 = copyR(new File(d.getDocId()), new File(file_path));
                }
                doc.setPath(file_path);
                doc.setTipo(e.getEm().find(TipoDoc.class,
                        20L));//docId
                doc.setDocente(d);
                doc.setScadenza(d.getScadenza_doc());
                doc.setProgetto(p);
                documenti.add(doc);
                doc = new DocumentiPrg();
                file_path = dir.getAbsolutePath() + File.separator + "Curriculum_" + today + "_" + d.getId() + d.getCurriculum().substring(d.getCurriculum().lastIndexOf("."));
                if (salvataggio) {
                    copyfile2 = copyR(new File(d.getCurriculum()), new File(file_path));
                }
                doc.setPath(file_path);
                doc.setTipo(e.getEm().find(TipoDoc.class, 21L));//curriculum
                doc.setDocente(d);
                doc.setProgetto(p);
                documenti.add(doc);
            }

            p.setDocenti(docenti_list);
            p.setDocumenti(documenti);

            e.merge(p);
            e.persist(new Storico_Prg("Creato", new Date(), p, p.getStato()));//storico progetto

            if (salvataggio) {
                TipoDoc modello2 = tipo_doc.stream().filter(m1 -> m1.getId() == 1L).findAny().orElse(null);
                boolean modello2OK;
                String erroremodello2OK = "MODELLO 2 ERRATO. CONTROLLARE.";
                try {
                    Part p1 = request.getPart("doc_" + modello2.getId());
                    file_path = dir.getAbsolutePath() + File.separator + modello2.getDescrizione() + "_"
                            + today + p1.getSubmittedFileName().substring(p1.getSubmittedFileName().lastIndexOf("."));
                    p1.write(file_path);
                    File pdfdest = new File(file_path);
                    String res = checkFirmaQRpdfA("MODELLO2", us.getUsername(), pdfdest, us.getSoggettoAttuatore().getCodicefiscale(), qrcrop);
                    if (!res.equals("OK")) {
                        modello2OK = false;
                        erroremodello2OK = res;
                    } else {
                        modello2OK = true;
                    }
                } catch (Exception ex) {
                    modello2OK = false;
                    erroremodello2OK = "MODELLO 2 ERRATO. " + ex.getMessage() + ". CONTROLLARE.";
                }
                if (modello2OK) {
                    if (copyfile1 && copyfile2) {
                        e.flush();
                        e.commit();
                        resp.addProperty("result", true);
                    } else {
                        e.rollBack();
                        resp.addProperty("result", false);
                        resp.addProperty("message", "IMPOSSIBILE SALVARE DOCUMENTI DEL DOCENTE. RIPROVARE.");
                    }

                } else {
                    e.rollBack();
                    resp.addProperty("result", false);
                    resp.addProperty("message", erroremodello2OK);
                }
            } else {
                e.rollBack();
                if (save.equals("1")) {//MODELLO
                    downloadFile = Pdf_new.MODELLO2(e,
                            modellodacompilare,
                            us.getUsername(),
                            us.getSoggettoAttuatore(),
                            p,
                            list_allievi, new DateTime(), salvataggio);
                }
                resp.addProperty("result", true);
            }
        } catch (PersistenceException | ParseException ex) {
            e.rollBack();
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA newProgettoFormativo: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile salvare il progetto formativo.");
        } finally {
            e.close();
        }

        if (salvataggio) {
            response.getWriter().write(resp.toString());
            response.getWriter().flush();
            response.getWriter().close();
        } else {//MODELLO
            if (downloadFile != null && downloadFile.exists()) {
                OutputStream outStream;
                try (FileInputStream inStream = new FileInputStream(downloadFile)) {
                    String mimeType = probeContentType(downloadFile.toPath());
                    if (mimeType == null) {
                        mimeType = "application/octet-stream";
                    }
                    response.setContentType(mimeType);
                    String headerKey = "Content-Disposition";
                    String headerValue = format("attachment; filename=\"%s\"", downloadFile.getName());
                    response.setHeader(headerKey, headerValue);
                    outStream = response.getOutputStream();
                    byte[] buffer = new byte[4096 * 4096];
                    int bytesRead;
                    while ((bytesRead = inStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }
                }
                outStream.close();
            } else {
                redirect(request, response, request.getContextPath() + "/404.jsp");
            }
        }
    }

    protected void scaricamodello4(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        File downloadFile = null;
        try {
            User us = (User) request.getSession().getAttribute("user");
            Entity e = new Entity();
            ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class, Long.parseLong(getRequestValue(request, "idpr")));
            List<StaffModelli> staff = p.getStaff_modelli().stream().filter(m -> m.getAttivo() == 1).collect(Collectors.toList());
            ModelliPrg m4 = Utility.filterModello4(p.getModelli());
            if (m4.getStato().equals("R")) {
                List<Lezioni_Modelli> lezioni = m4.getLezioni();
                downloadFile = Pdf_new.MODELLO4(e,
                        us.getUsername(),
                        us.getSoggettoAttuatore(),
                        p,
                        p.getAllievi().stream().filter(al1 -> al1.getGruppo_faseB() > 0).collect(Collectors.toList()),
                        p.getDocenti(),
                        lezioni,
                        staff,
                        new DateTime(), true);

                //}
            }
            e.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (downloadFile != null && downloadFile.exists()) {
            OutputStream outStream;
            try (FileInputStream inStream = new FileInputStream(downloadFile)) {
                String mimeType = probeContentType(downloadFile.toPath());
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
                response.setContentType(mimeType);
                String headerKey = "Content-Disposition";
                String headerValue = format("attachment; filename=\"%s\"", downloadFile.getName());
                response.setHeader(headerKey, headerValue);
                outStream = response.getOutputStream();
                byte[] buffer = new byte[4096 * 4096];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }
            outStream.close();
        } else {
            redirect(request, response, request.getContextPath() + "/404.jsp");
        }
    }

    protected void scaricaregistrotemp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        File downloadFile = null;
        String idpr = getRequestValue(request, "idpr");
        try {
            Entity e = new Entity();
            e.begin();
            DocumentiPrg mod = e.getEm().find(ProgettiFormativi.class,
                    Long.parseLong(idpr)).getDocumenti().stream().filter(d1
                    -> d1.getTipo().getId() == 33L).findAny().orElse(null);
            System.out.println("2) " + (mod != null));
            if (mod != null) {
                downloadFile = new File(mod.getPath());
                System.out.println("3) " + mod.getPath());
            }
            e.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (downloadFile != null && downloadFile.exists()) {
            OutputStream outStream;
            try (FileInputStream inStream = new FileInputStream(downloadFile)) {
                String mimeType = probeContentType(downloadFile.toPath());
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
                response.setContentType(mimeType);
                String headerKey = "Content-Disposition";
                String headerValue = format("attachment; filename=\"%s\"", downloadFile.getName());
                response.setHeader(headerKey, headerValue);
                outStream = response.getOutputStream();
                byte[] buffer = new byte[4096 * 4096];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }
            outStream.close();
        } else {
            redirect(request, response, request.getContextPath() + "/404.jsp");
        }
    }

    protected void scaricamodello6temp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String idpr = getRequestValue(request, "idpr");

        File downloadFile = null;
        try {
            User us = (User) request.getSession().getAttribute("user");
            Entity e = new Entity();
            ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class, Long.parseLong(idpr));

            ModelliPrg m6 = Utility.filterModello6(pf.getModelli());
            if (m6 != null) {
                downloadFile = Pdf_new.MODELLO6(e,
                        us.getUsername(),
                        us.getSoggettoAttuatore(),
                        pf, m6, new DateTime(), true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (downloadFile != null && downloadFile.exists()) {
            OutputStream outStream;
            try (FileInputStream inStream = new FileInputStream(downloadFile)) {
                String mimeType = probeContentType(downloadFile.toPath());
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
                response.setContentType(mimeType);
                String headerKey = "Content-Disposition";
                String headerValue = format("attachment; filename=\"%s\"", downloadFile.getName());
                response.setHeader(headerKey, headerValue);
                outStream = response.getOutputStream();
                byte[] buffer = new byte[4096 * 4096];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }
            outStream.close();
        } else {
            redirect(request, response, request.getContextPath() + "/404.jsp");
        }

    }

    protected void scaricaModello5(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        File downloadFile = null;
        try {

            String idallievo = getRequestValue(request, "iduser");
            String idmodello = getRequestValue(request, "idmodello");

            Entity e = new Entity();

            Allievi a = e.getEm().find(Allievi.class, Long.parseLong(idallievo));
            MascheraM5 m5 = e.getEm().find(MascheraM5.class, Long.parseLong(idmodello));

            if (a.getId().equals(m5.getAllievo().getId())) {
                TipoDoc_Allievi tipodoc_m5;
                if (m5.isTabella_premialita()) {
                    tipodoc_m5 = e.getEm().find(TipoDoc_Allievi.class, 21L);
                } else {
                    tipodoc_m5 = e.getEm().find(TipoDoc_Allievi.class, 20L);
                }

                User us = (User) request.getSession().getAttribute("user");
                String[] datifrequenza = Action.dati_modello5_neet(
                        String.valueOf(a.getId()),
                        String.valueOf(us.getSoggettoAttuatore().getId()),
                        String.valueOf(m5.getProgetto_formativo().getId()));

                downloadFile = Pdf_new.MODELLO5(e,
                        tipodoc_m5.getModello(),
                        us.getUsername(),
                        us.getSoggettoAttuatore(),
                        a,
                        datifrequenza,
                        m5,
                        new DateTime(), true);

            }
            e.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (downloadFile != null && downloadFile.exists()) {
            OutputStream outStream;
            try (FileInputStream inStream = new FileInputStream(downloadFile)) {
                String mimeType = probeContentType(downloadFile.toPath());
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
                response.setContentType(mimeType);
                String headerKey = "Content-Disposition";
                String headerValue = format("attachment; filename=\"%s\"", downloadFile.getName());
                response.setHeader(headerKey, headerValue);
                outStream = response.getOutputStream();
                byte[] buffer = new byte[4096 * 4096];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }
            outStream.close();
        } else {
            redirect(request, response, request.getContextPath() + "/404.jsp");
        }

    }

    protected void scaricamodello3(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        File downloadFile = null;
        try {
            User us = (User) request.getSession().getAttribute("user");
            Entity e = new Entity();
            ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class, Long.parseLong(getRequestValue(request, "idpr")));
            List<StaffModelli> staff = p.getStaff_modelli().stream().filter(m -> m.getAttivo() == 1).collect(Collectors.toList());
            ModelliPrg m3 = Utility.filterModello3(p.getModelli());
            if (m3.getStato().equals("R")) {
                Lezioni_Modelli prima_lezione = m3.getLezioni().stream().min(Comparator.comparing(d -> d.getGiorno())).orElse(null);
                if (prima_lezione.getGiorno() != null) {
                    if (!prima_lezione.getGiorno().equals(p.getStart())) {
                        p.setStart(prima_lezione.getGiorno());
                        e.begin();
                        e.merge(p);
                        e.commit();
                    }
                }
                List<Lezioni_Modelli> lezioni = m3.getLezioni();
                if (lezioni.size() == 16) {
                    downloadFile = Pdf_new.MODELLO3(e,
                            us.getUsername(),
                            us.getSoggettoAttuatore(),
                            p,
                            p.getAllievi().stream().filter(p1 -> p1.getStatopartecipazione().getId().equals("01")).collect(Collectors.toList()),
                            p.getDocenti(), lezioni, staff,
                            new DateTime(), true);

                }
            }
            e.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (downloadFile != null && downloadFile.exists()) {
            OutputStream outStream;
            try (FileInputStream inStream = new FileInputStream(downloadFile)) {
                String mimeType = probeContentType(downloadFile.toPath());
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
                response.setContentType(mimeType);
                String headerKey = "Content-Disposition";
                String headerValue = format("attachment; filename=\"%s\"", downloadFile.getName());
                response.setHeader(headerKey, headerValue);
                outStream = response.getOutputStream();
                byte[] buffer = new byte[4096 * 4096];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }
            outStream.close();
        } else {
            redirect(request, response, request.getContextPath() + "/404.jsp");
        }

    }

    protected void updtAllievo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        JsonObject resp = new JsonObject();
        try {
            e.begin();

            Allievi a = e.getEm().find(Allievi.class,
                    Long.parseLong(request.getParameter("id")));
            String codicefiscale = request.getParameter("codicefiscale");
            if (e.getAllievoCF(codicefiscale) == null || codicefiscale.equals(a.getCodicefiscale())) {

                Part p = request.getPart("cartaid");
                String path = request.getParameter("cartaidpath");
                if (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0) {
                    path = a.getDocid();
                    p.write(path);
                }

                Nazioni_rc nazionenascita = (Nazioni_rc) e.getEm().find(Nazioni_rc.class,
                        Long.parseLong(request.getParameter("cittadinanza")));

                a.setCittadinanza(nazionenascita);

                if (request.getParameter("stato").equals("100")) {
                    a.setComune_nascita((Comuni) e.getEm().find(Comuni.class,
                            Long.parseLong(request.getParameter("comunenascita"))));
                } else {
                    a.setComune_nascita(e.getComunibyIstat(nazionenascita));
                }

                a.setNome(new String(request.getParameter("nome").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                a.setCognome(new String(request.getParameter("cognome").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                a.setCodicefiscale(request.getParameter("codicefiscale"));
                a.setDatanascita(sdf.parse(request.getParameter("datanascita")));
                a.setTelefono(request.getParameter("telefono"));
                a.setIndirizzoresidenza(new String(request.getParameter("indirizzores").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                a.setCapresidenza(request.getParameter("capres"));
                a.setComune_residenza((Comuni) e.getEm().find(Comuni.class,
                        Long.parseLong(request.getParameter("comuneres"))));

                if (request.getParameter("checkind") != null) {
                    a.setIndirizzodomicilio(new String(request.getParameter("indirizzores").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                    a.setCivicodomicilio(request.getParameter("civicores"));
                    a.setCapdomicilio(request.getParameter("capres"));
                    a.setComune_domicilio((Comuni) e.getEm().find(Comuni.class,
                            Long.parseLong(request.getParameter("comuneres"))));
                } else {
                    a.setCapdomicilio(request.getParameter("capdom"));
                    a.setIndirizzodomicilio(new String(request.getParameter("indirizzodom").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                    a.setCivicodomicilio(request.getParameter("civicodom"));
                    a.setComune_domicilio((Comuni) e.getEm().find(Comuni.class,
                            Long.parseLong(request.getParameter("comunedom"))));
                }

                a.setDocid(path);
                a.setScadenzadocid(sdf.parse(request.getParameter("scadenzadoc")));
                a.setTitoloStudio((TitoliStudio) e.getEm().find(TitoliStudio.class,
                        request.getParameter("titolo_studio")));
                a.setCondizione_mercato((Condizione_Mercato) e.getEm().find(Condizione_Mercato.class,
                        request.getParameter("condizione")));
                a.setTarget(request.getParameter("target"));
                //29-04-2020 MODIFICA - CONDIZIONE LAVORATIVA PRECEDENTE
                a.setCondizione_lavorativa((Condizione_Lavorativa) e.getEm().find(Condizione_Lavorativa.class,
                        Integer.parseInt(request.getParameter("condizione_lavorativa"))));
                a.setNeet(e.getEm().find(Condizione_Lavorativa.class,
                        Integer.parseInt(request.getParameter("condizione_lavorativa"))).getDescrizione());
//                a.setNeet(new String(request.getParameter("neet").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                a.setEmail(request.getParameter("email"));
                a.setSesso(Integer.parseInt(request.getParameter("codicefiscale").substring(9, 11)) > 40 ? "F" : "M");

                e.merge(a);
                e.flush();
                e.commit();

                resp.addProperty("result", true);
            } else {
                resp.addProperty("result", false);
                resp.addProperty("message", "Errore: non &egrave; stato possibile aggiornare le informazioni dell'allievo.<br>Il seguente codice fiscale gi&agrave; già presente");
            }

        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
            e.insertTracking(null, "saveSoggettoAttuatore Errore: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile aggiornare le informazioni dell'allievo.<br>Riprovare, se l'errore persiste contattare l'assistenza");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void upAllievoM1(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        Utility.printRequest(request);

        Entity e = new Entity();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        JsonObject resp = new JsonObject();
        try {
            e.begin();

            Allievi a = e.getEm().find(Allievi.class, Long.parseLong(request.getParameter("idal")));

            String codicefiscale = request.getParameter("codicefiscale");
            if (e.getAllievoCF(codicefiscale) == null || codicefiscale.equalsIgnoreCase(a.getCodicefiscale())) {
                Nazioni_rc nazionenascita = (Nazioni_rc) e.getEm().find(Nazioni_rc.class, Long.parseLong(request.getParameter("cittadinanza")));
                a.setCittadinanza(nazionenascita);

//                if (request.getParameter("stato").equalsIgnoreCase("99")) {
//                    a.setComune_nascita((Comuni) e.getEm().find(Comuni.class, Long.parseLong(request.getParameter("comunenascita"))));
//                } else {
//                    a.setComune_nascita(e.getComunibyIstat(nazionenascita));
//                }
                a.setStato_nascita(request.getParameter("stato").equalsIgnoreCase("-") ? "100" : request.getParameter("stato"));
                if (a.getStato_nascita().equals("100")) {
                    a.setComune_nascita(e.getEm().find(Comuni.class, Long.parseLong(request.getParameter("comunenascita"))));
                } else {
                    Comuni statoEstero = e.byIstatEstero(a.getStato_nascita());
                    if (statoEstero == null) {
                        Nazioni_rc n = e.byCodiceFiscale(a.getStato_nascita());
                        statoEstero = new Comuni();
                        statoEstero.setCittadinanza(1);
                        statoEstero.setCod_comune("0");
                        statoEstero.setCod_provincia("0");
                        statoEstero.setCod_regione("0");
                        statoEstero.setCodicicatastali_altri(null);
                        statoEstero.setIstat(n.getCodicefiscale());
                        statoEstero.setNome(n.getNome());
                        statoEstero.setNome_provincia(n.getNome());
                        statoEstero.setProvincia(n.getUe());
                        statoEstero.setRegione(n.getNome());
                    }
                    a.setComune_nascita(statoEstero);
                }

                a.setNome(new String(request.getParameter("nome").toUpperCase().getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                a.setCognome(new String(request.getParameter("cognome").toUpperCase().getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                a.setCodicefiscale(request.getParameter("codicefiscale").toUpperCase());
                a.setDatanascita(sdf.parse(request.getParameter("datanascita")));
                a.setTelefono(request.getParameter("telefono"));
                a.setIndirizzoresidenza(new String(request.getParameter("indirizzores").toUpperCase().getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                a.setCapresidenza(request.getParameter("capres"));
                a.setComune_residenza((Comuni) e.getEm().find(Comuni.class, Long.parseLong(request.getParameter("comuneres"))));

                if (request.getParameter("checkind") != null) {
                    a.setIndirizzodomicilio(new String(request.getParameter("indirizzores").toUpperCase().getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                    a.setCivicodomicilio(request.getParameter("civicores").toUpperCase());
                    a.setCapdomicilio(request.getParameter("capres"));
                    a.setComune_domicilio((Comuni) e.getEm().find(Comuni.class, Long.parseLong(request.getParameter("comuneres"))));
                } else {
                    a.setCapdomicilio(request.getParameter("capdom"));
                    a.setIndirizzodomicilio(new String(request.getParameter("indirizzodom").toUpperCase().getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                    a.setCivicodomicilio(request.getParameter("civicodom").toUpperCase());
                    a.setComune_domicilio((Comuni) e.getEm().find(Comuni.class, Long.parseLong(request.getParameter("comunedom"))));
                }

                a.setTitoloStudio((TitoliStudio) e.getEm().find(TitoliStudio.class, request.getParameter("titolo_studio")));
                a.setTarget(request.getParameter("target"));
                a.setCondizione_lavorativa((Condizione_Lavorativa) e.getEm().find(Condizione_Lavorativa.class, Integer.parseInt(request.getParameter("condizione_lavorativa"))));
                a.setNeet(e.getEm().find(Condizione_Lavorativa.class, Integer.parseInt(request.getParameter("condizione_lavorativa"))).getDescrizione());
                a.setEmail(request.getParameter("email"));
                a.setSesso(Integer.parseInt(request.getParameter("codicefiscale").substring(9, 11)) > 40 ? "F" : "M");

                a.setCanale((Canale) e.getEm().find(Canale.class, Integer.parseInt(request.getParameter("conoscenza"))));
                a.setMotivazione((Motivazione) e.getEm().find(Motivazione.class, Integer.parseInt(request.getParameter("motivazione"))));
                a.setPrivacy2(request.getParameter("prv2") != null ? "SI" : "NO");
                a.setPrivacy3(request.getParameter("prv3") != null ? "SI" : "NO");

                e.merge(a);
                e.flush();
                e.commit();

                resp.addProperty("result", true);
            } else {
                resp.addProperty("result", false);
                resp.addProperty("message", "Errore: non &egrave; stato possibile aggiornare le informazioni dell'allievo.<br>Il seguente codice fiscale gi&agrave; già presente");
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
            e.insertTracking(null, "updateAllievoM1 Errore: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile aggiornare le informazioni dell'allievo.<br>Riprovare, se l'errore persiste contattare l'assistenza");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void updtCartaID(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            e.begin();

            Allievi a = e.getEm().find(Allievi.class,
                    Long.parseLong(request.getParameter("id")));
            Part p = request.getPart("cartaid");
            String path = a.getDocid();
            File dir = new File(path);
            dir.mkdirs();
            p.write(path);

            a.setScadenzadocid(new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("datascadenza")));
            e.commit();

            resp.addProperty("result", true);
        } catch (PersistenceException | ParseException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA updtCartaId: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile aggiornare il documento d'identità.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void updtCartaIDAD(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        User us = (User) request.getSession().getAttribute("user");
        try {
            e.begin();
            Part p = request.getPart("cartaid");
            us.getSoggettoAttuatore().setScadenza(new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("datascadenza")));
            us.getSoggettoAttuatore().setNro_documento(request.getParameter("numerodoc"));
            e.merge(us);
            p.write(us.getSoggettoAttuatore().getCartaid());
            e.commit();
            resp.addProperty("result", true);
            resp.addProperty("message", "Operazione effettuata con successo.");
        } catch (PersistenceException | ParseException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(us.getId()), "OperazioniSA updtCartaIdAd: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile aggiornare il documento d'identità.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void uploadDocIdDocente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject resp = new JsonObject();
        String scadenza = request.getParameter("scadenza");
        Part p = request.getPart("file");
        Entity e = new Entity();
        try {
            e.begin();
            Docenti d = e.getEm().find(Docenti.class,
                    Long.parseLong(request.getParameter("id")));
            d.setScadenza_doc(new SimpleDateFormat("dd/MM/yyyy").parse(scadenza));

            String path = e.getPath("pathDoc_Docenti").replace("@docente", d.getCodicefiscale());
            new File(path).mkdirs();//create folder

            String ext = p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
            path += "Doc_id_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + d.getCodicefiscale() + ext;

            p.write(path);
            d.setDocId(path);

            e.commit();
            resp.addProperty("result", true);
            resp.addProperty("path", path);
            resp.addProperty("scadenza", d.getScadenza_doc().getTime());
        } catch (PersistenceException | ParseException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA uploadDocIdDocente: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile aggiornare il documento d'identità.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void uploadCurriculumDocente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject resp = new JsonObject();

        Part p = request.getPart("file");
        Entity e = new Entity();
        try {
            e.begin();
            Docenti d = e.getEm().find(Docenti.class,
                    Long.parseLong(request.getParameter("id")));

            String path = e.getPath("pathDoc_Docenti").replace("@docente", d.getCodicefiscale());
            new File(path).mkdirs();//create folder

            String ext = p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
            path += "Curriculum_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + d.getCodicefiscale() + ext;

            p.write(path);
            d.setCurriculum(path);

            e.commit();
            resp.addProperty("result", true);
            resp.addProperty("path", path);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA uploadCurriculumDocente: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile aggiornare il documento d'identità.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void modifyDoc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject resp = new JsonObject();

        Part p = request.getPart("file");
        Entity e = new Entity();
        try {
            e.begin();
            Date scadenza = request.getParameter("scadenza") != null ? new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("scadenza")) : null;
            DocumentiPrg d = e.getEm().find(DocumentiPrg.class,
                    Long.parseLong(request.getParameter("id")));
            p.write(d.getPath());
            d.setScadenza(scadenza);

            if (scadenza != null) {
                List<DocumentiPrg> doc_mod = e.getDocIdModifiableDocente(((User) request.getSession().getAttribute("user")).getSoggettoAttuatore(), d.getDocente());
                doc_mod.remove(d);
                for (DocumentiPrg doc : doc_mod) {//aggiorna id doc. a progetti modificabili
                    p.write(doc.getPath());
                    doc.setScadenza(scadenza);
                }
            }

            e.commit();
            resp.addProperty("result", true);
        } catch (PersistenceException | ParseException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA uploadCurriculumDocente: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile aggiornare il documento d'identità.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void uploadDocPrg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject resp = new JsonObject();

        Part p = request.getPart("file");
        Entity e = new Entity();
        try {
            ProgettiFormativi prg = e.getEm().find(ProgettiFormativi.class,
                    Long.parseLong(request.getParameter("idprogetto")));
            TipoDoc tipo = e.getEm().find(TipoDoc.class,
                    Long.parseLong(request.getParameter("id_tipo")));
            List<TipoDoc> tipo_obb = e.getTipoDocObbl(prg.getStato());
            List<DocumentiPrg> doc_list = e.getDocPrg(prg);
            User us = (User) request.getSession().getAttribute("user");

            tipo_obb.remove(tipo);

            doc_list.forEach(d -> {
                tipo_obb.remove(d.getTipo());
            });

            e.begin();
            //creao il path
            String path = e.getPath("pathDocSA_Prg").replace("@rssa", us.getSoggettoAttuatore().getId().toString()).replace("@folder", prg.getId().toString());
            File dir = new File(path);
            createDir(path);
            String file_path;
            String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());

            //scrivo il file su disco
            if (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0) {
                file_path = dir.getAbsolutePath() + File.separator + tipo.getDescrizione() + "_" + today + p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                p.write(file_path);
                DocumentiPrg doc = new DocumentiPrg();
                doc.setPath(file_path);
                doc.setTipo(tipo);
                doc.setProgetto(prg);
                e.persist(doc);
            }
            //se caricato tutti i doc obbligatori setto il progetto come idoneo per la prossima fase
            if (prg.getStato().getId().equals("FB")) {
                List<TipoDoc_Allievi> tipo_obb_all = e.getTipoDocAllieviObbl(prg.getStato());
                List<TipoDoc_Allievi> doc_allievo;
                StringBuilder msg = new StringBuilder();
                StringBuilder warning = new StringBuilder();
                double totale, hh;
                boolean checkdocs = true, checkregistri = true;
                msg.append("Sono stati caricati tutti i documenti necessari per questa fase. Ora il progetto può essere inviato al Microcredito per essere controllato.<br>");
                warning.append("Tuttavia, i seguenti allievi non hanno effettuato le ore necessarie per la Fase B:<br>");

                for (Allievi allievo : prg.getAllievi().stream().filter(all -> all.getStatopartecipazione().getId().equals("01")).collect(Collectors.toList())) {//solo allievi regolarmente iscritti
                    doc_allievo = new ArrayList<>();
                    doc_allievo.addAll(tipo_obb_all);
                    totale = 0;
                    for (Documenti_Allievi doc : allievo.getDocumenti()) {
                        if (allievo.getEsito().equalsIgnoreCase("Fase B")) {
                            if (doc.getTipo().getId() == 5 && doc.getDeleted() == 0) {
                                hh = (double) (doc.getOrarioend_mattina().getTime() - doc.getOrariostart_mattina().getTime());
                                if (doc.getOrariostart_pom() != null && doc.getOrarioend_pom() != null) {
                                    hh += (double) (doc.getOrarioend_pom().getTime() - doc.getOrariostart_pom().getTime());
                                }
                                totale += hh / 3600000;
                            }
                        }
                        doc_allievo.remove(doc.getTipo());
                    }
                    if (!doc_allievo.isEmpty()) {
                        checkdocs = false;
                    }
                    if (allievo.getEsito().equalsIgnoreCase("Fase B")) {
                        if (totale < 20) {
                            checkregistri = false;
                            warning.append("• ").append(allievo.getCognome()).append(" ").append(allievo.getNome()).append(" (").append(String.valueOf(totale).replace(".0", "")).append("/20h)<br>");
                        }
                    }
                }
                prg.getDocumenti().forEach(dprg -> {
                    tipo_obb.remove(dprg.getTipo());
                });
                //se sono stati caricati tutti i doc obbligatori per il progetto e per gli alunni, setto il progetto come idoneo per la prossima fase
                if (tipo_obb.isEmpty() && checkdocs) {
                    prg.setControllable(1);
                    e.merge(prg);
                    if (checkregistri) {
                        resp.addProperty("message", msg.toString());
                    } else {
                        resp.addProperty("message", msg.append(warning).toString());
                    }
                } else {
                    resp.addProperty("message", "");
                }
            } else {
                if (tipo_obb.isEmpty()) {
                    prg.setControllable(1);
                    e.merge(prg);
                    resp.addProperty("message", "Hai caricato tutti i documenti necessari per questa fase. Ora il progetto può essere inviato al Microcredito per essere controllato.");
                } else {
                    resp.addProperty("message", "");
                }
            }

            e.commit();
            resp.addProperty("result", true);

        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.rollBack();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA uploadDocPrg: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile caricare il documento.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void modifyPrg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        e.begin();

        //check se il numero di alunni o le date sono state modificate
        boolean modified = false;
        //check se la modifica riguarda le date, in tal caso non va ricaricato il modello 2, ma vanno eliminate le lezioni del modello 3 (setto l'id del progetto a null)
        boolean dates_modified = false;
        try {
            ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class,
                    Long.parseLong(request.getParameter("id_progetto")));

            if (p.getStato().getModifiche().getNome() == 1) {
                p.setNome(e.getEm().find(NomiProgetto.class,
                        Long.parseLong(request.getParameter("nome_pf"))));
            }
            if (p.getStato().getModifiche().getDescrizione() == 1) {
                p.setDescrizione(request.getParameter("descrizione_pf"));
            }
            if (p.getStato().getModifiche().getDate() == 1) {
                String[] date = request.getParameter("date").split("-");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                dates_modified = !p.getStart().equals(sdf.parse(date[0].trim()));
                dates_modified = dates_modified ? true : !p.getEnd().equals(sdf.parse(date[1].trim()));
                p.setStart(sdf.parse(date[0].trim()));
                p.setEnd(sdf.parse(date[1].trim()));
            }
            if (p.getStato().getModifiche().getSede() == 1) {
                p.setSede(e.getEm().find(SediFormazione.class,
                        Long.parseLong(request.getParameter("sede"))));
            }
            if (p.getStato().getModifiche().getAllievi() == 1) {
                List<Allievi> allievi_old = e.getAllieviProgettiFormativi(p);
                int prev_allievi = allievi_old.size();
                int now_allievi = 0;
                for (String s : request.getParameterValues("allievi[]")) {
                    now_allievi++;
                    Allievi a = e.getEm().find(Allievi.class,
                            Long.parseLong(s));
                    a.setProgetto(p);
                    allievi_old.remove(a);
                    e.merge(a);
                }
                for (Allievi al : allievi_old) {
                    al.setProgetto(null);
                    e.merge(al);
                }
                modified = modified ? true : allievi_old.size() > 0;
                modified = modified ? true : now_allievi > prev_allievi;
            }

            if (modified) {
                p.setModello2_check(1);
            }

            e.merge(p);
            e.commit();

            resp.addProperty("result", true);
        } catch (PersistenceException | ParseException ex) {
            e.rollBack();
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA modifyPrg: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile modificare il progetto formativo.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void modifyDocenti(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();

        User us = (User) request.getSession().getAttribute("user");
        Entity e = new Entity();
        e.begin();

        try {
            ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("id_progetto")));
            List<Docenti> docenti_old = e.getDocentiPrg(p);
            List<Docenti> docenti_list = new ArrayList();
            ArrayList<DocumentiPrg> documenti = new ArrayList();

            String path = e.getPath("pathDocSA_Prg").replace("@rssa", us.getSoggettoAttuatore().getId().toString()).replace("@folder", p.getId().toString());
            File dir = new File(path);
            createDir(path);
            boolean copy = true;
            String file_path;
            String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());
            String pathSave;
            String name;
            Docenti d;
            DocumentiPrg doc;

            for (String s : request.getParameterValues("docenti[]")) {
                d = e.getEm().find(Docenti.class, Long.parseLong(s));
                docenti_list.add(d);
                if (!docenti_old.contains(d)) {
                    name = "DocId_" + today + "_" + d.getId() + d.getDocId().substring(d.getDocId().lastIndexOf("."));
                    file_path = dir.getAbsolutePath() + name;
                    pathSave = path + name;
                    File fileDocente = new File(d.getDocId());
                    copy = copyR(fileDocente, new File(file_path));
                    if (!copy) {
                        break;
                    }

                    doc = new DocumentiPrg(pathSave.replace("\\", "/"), d.getScadenza_doc(), e.getEm().find(TipoDoc.class, 20L), d, p);
                    e.persist(doc);
                    documenti.add(doc);

                    name = "Curriculum_" + today + "_" + d.getId() + d.getCurriculum().substring(d.getCurriculum().lastIndexOf("."));
                    file_path = dir.getAbsolutePath() + name;
                    pathSave = path + name;
                    fileDocente = new File(d.getCurriculum());
                    copy = copyR(fileDocente, new File(file_path));
                    if (!copy) {
                        break;
                    }

                    doc = new DocumentiPrg(pathSave.replace("\\", "/"), null, e.getEm().find(TipoDoc.class, 21L), d, p);
                    e.persist(doc);
                    documenti.add(doc);

                } else {
                    docenti_old.remove(d);
                }
            }

            for (Docenti old : docenti_old) {//setto i doc dei docenti eliminati come deleted
                e.deleteDocPrg(p, old);
            }

            p.setDocenti(docenti_list);
            p.getDocumenti().addAll(documenti);
            e.merge(p);
            if (copy) {
                e.commit();
                resp.addProperty("result", true);
            } else {
                e.rollBack();
                resp.addProperty("result", false);
                resp.addProperty("message", "IMPOSSIBILE SALVARE DOCUMENTI DEL DOCENTE. RIPROVARE.");
            }
        } catch (PersistenceException ex) {
            e.rollBack();
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA modifyDocenti: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato modificare i docenti del progetto formativo.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void goNext(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        e.begin();

        try {
            ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class,
                    Long.parseLong(request.getParameter("id")));
            Date today = new Date();
            checkResult check = checkScadenze(p);
            if (p.getStato().getId().equals("ATA") || check.result) {
                String stato = p.getStato().getId().replace("E", "");
                boolean to_fb = false;
                if (p.getStato().getId().equals("ATA")) {
                    p.setEnd_fa(today);//fine fa
                    p.setStart_fb(today);//inizio fb
                    to_fb = true;
                    p.setStato(e.getEm().find(StatiPrg.class,
                            "ATB"));
                    p.setControllable(0);
                    e.persist(new Storico_Prg("Avviata Fase B", new Date(), p, p.getStato()));//storico progetto
                    if (!checkFaseAllievi(p.getAllievi())) {
                        p.setEnd_fb(today);//se fb non parte setta fine fb alla stessa data di FA
                    }
                } else if (p.getStato().getId().equals("ATB") && p.getEnd_fb() == null) {
                    p.setEnd_fb(today);//setta data fine fase FB solo se non precedentemente settata
                }
                if (!to_fb) {
                    p.setStato(e.getEm().find(StatiPrg.class,
                            stato + "1"));
                    e.persist(new Storico_Prg("Inviato a controllo", new Date(), p, p.getStato()));//storico progetto
                }
                e.merge(p);
                e.commit();
                resp.addProperty("result", true);
            } else {
                resp.addProperty("result", false);
                resp.addProperty("message", "<h5>" + check.message + "</h5>");
            }
        } catch (PersistenceException ex) {
            e.rollBack();
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA goNext: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato modificare i docenti del progetto formativo.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void setEsitoAllievo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        e.begin();

        try {
            Allievi a = e.getEm().find(Allievi.class,
                    Long.parseLong(request.getParameter("id")));
            //String stato = p.getStato().getId().replace("E", "");
            a.setEsito(request.getParameter("esito"));
            e.merge(a);
            //e.persist(new Storico_Prg("Inviato a controllo", new Date(), p, p.getStato()));//storico progetto
            e.commit();
            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            e.rollBack();
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA setEsitoAllievo: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile modificare l'esito dell'allievo.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void uploadRegistro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject resp = new JsonObject();
        Part p = request.getPart("file");
        Entity e = new Entity();
        try {
            Allievi a = e.getEm().find(Allievi.class,
                    Long.parseLong(request.getParameter("idallievo")));
            TipoDoc_Allievi tipo = e.getEm().find(TipoDoc_Allievi.class,
                    Long.parseLong("5"));  //da cambiare
            List<TipoDoc_Allievi> tipo_obb = e.getTipoDocAllieviObbl(a.getProgetto().getStato());
            List<TipoDoc> tipo_obb_prg = e.getTipoDocObbl(a.getProgetto().getStato());
            Documenti_Allievi doc_a = new Documenti_Allievi();

            Date giorno = request.getParameter("giorno") != null ? new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("giorno")) : null;
            Date orariostart_mattina = request.getParameter("orario1_start") != null ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario1_start")) : null;
            Date orarioend_mattina = request.getParameter("orario1_end") != null ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario1_end")) : null;
            Date orariostart_pomeriggio = request.getParameter("orario2_start") != null && Boolean.parseBoolean(request.getParameter("check")) ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario2_start")) : null;
            Date orarioend_pomeriggio = request.getParameter("orario2_end") != null && Boolean.parseBoolean(request.getParameter("check")) ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario2_end")) : null;
            Docenti docente = e.getEm().find(Docenti.class,
                    Long.parseLong(request.getParameter("docente")));

            User us = (User) request.getSession().getAttribute("user");

            e.begin();
            //creao il path
            String path = e.getPath("pathDocSA_Prg_RegistriIndividuali").replace("@rssa", us.getSoggettoAttuatore().getId().toString()).replace("@folder", a.getProgetto().getId().toString());
            File dir = new File(path);
            createDir(path);
            String file_path;
            String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());

            //scrivo il file su disco
            if (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0) {
                file_path = dir.getAbsolutePath() + File.separator + tipo.getDescrizione() + "_" + today + "_" + a.getCodicefiscale() + p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                p.write(file_path);
                doc_a.setPath(file_path);
            }

            doc_a.setTipo(tipo);
            doc_a.setGiorno(giorno);
            doc_a.setDocente(docente);
            doc_a.setOrariostart_mattina(orariostart_mattina);
            doc_a.setOrarioend_mattina(orarioend_mattina);
            doc_a.setOrariostart_pom(orariostart_pomeriggio);
            doc_a.setOrarioend_pom(orarioend_pomeriggio);
            doc_a.setAllievo(a);
            e.persist(doc_a);

            e.commit();

            e.begin();
            double hh, totale;
            boolean checkregistri = true;
            boolean checkdocs = true;

            List<TipoDoc_Allievi> doc_allievo;
            StringBuilder msg = new StringBuilder();
            StringBuilder warning = new StringBuilder();
            msg.append("Sono stati caricati tutti i documenti necessari per questa fase. Ora il progetto può essere inviato al Microcredito per essere controllato.<br>");
            warning.append("Tuttavia, i seguenti allievi non hanno effettuato le ore necessarie per la Fase B:<br>");
            for (Allievi allievo : a.getProgetto().getAllievi()) {
                doc_allievo = new ArrayList<>();
                doc_allievo.addAll(tipo_obb);
                totale = 0;
                if (allievo.getEsito().equalsIgnoreCase("Fase B")) {
                    for (Documenti_Allievi doc : allievo.getDocumenti()) {
                        if (doc.getTipo().getId() == 5 && doc.getDeleted() == 0) {
                            hh = (double) (doc.getOrarioend_mattina().getTime() - doc.getOrariostart_mattina().getTime());
                            if (doc.getOrariostart_pom() != null && doc.getOrarioend_pom() != null) {
                                hh += (double) (doc.getOrarioend_pom().getTime() - doc.getOrariostart_pom().getTime());
                            }
                            totale += hh / 3600000;
                        }
                        doc_allievo.remove(doc.getTipo());
                    }
                }
                if (!doc_allievo.isEmpty()) {
                    checkdocs = false;
                }
                if (allievo.getEsito().equalsIgnoreCase("Fase B")) {
                    if (totale < 20) {
                        checkregistri = false;
                        warning.append("• ").append(allievo.getCognome()).append(" ").append(allievo.getNome()).append(" (").append(String.valueOf(totale).replace(".0", "")).append("/20h)<br>");
                    }
                }
            }
            for (DocumentiPrg dprg : a.getProgetto().getDocumenti()) {
                tipo_obb_prg.remove(dprg.getTipo());
            }
            //se sono stati caricati tutti i doc obbligatori per il progetto e per gli alunni, setto il progetto come idoneo per la prossima fase
            if (tipo_obb_prg.isEmpty() && checkdocs) {
                a.getProgetto().setControllable(1);
                e.merge(a.getProgetto());
                if (checkregistri) {
                    resp.addProperty("message", msg.toString());
                } else {
                    resp.addProperty("message", msg.append(warning).toString());
                }
            } else {
                resp.addProperty("message", "");
            }
            e.commit();
            resp.addProperty("result", true);

        } catch (PersistenceException | ParseException ex) {
            ex.printStackTrace();
            e.rollBack();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA uploadRegistro: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile caricare il registro.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void modifyRegistro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject resp = new JsonObject();
        Part p = request.getPart("file");
        Entity e = new Entity();
        try {
            Date giorno = request.getParameter("giorno") != null ? new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("giorno")) : null;
            Date orariostart_mattina = request.getParameter("orario1_start") != null ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario1_start")) : null;
            Date orarioend_mattina = request.getParameter("orario1_end") != null ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario1_end")) : null;

            Date orariostart_pomeriggio = request.getParameter("orario2_start") != null && Boolean.parseBoolean(request.getParameter("check")) ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario2_start")) : null;
            Date orarioend_pomeriggio = request.getParameter("orario2_end") != null && Boolean.parseBoolean(request.getParameter("check")) ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario2_end")) : null;
            Docenti docente = e.getEm().find(Docenti.class,
                    Long.parseLong(request.getParameter("docente")));

            e.begin();
            Documenti_Allievi doc = e.getEm().find(Documenti_Allievi.class,
                    Long.parseLong(request.getParameter("iddocumento")));
            List<Allievi> allieviprg = e.getAllieviProgettiFormativi(doc.getAllievo().getProgetto());
            List<TipoDoc> tipo_obb_prg = e.getTipoDocObbl(doc.getAllievo().getProgetto().getStato());
            //se è cambiato, scrivo il file su disco
            if (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0) {
                p.write(doc.getPath());
            }
            doc.setGiorno(giorno);
            doc.setDocente(docente);
            doc.setOrariostart_mattina(orariostart_mattina);
            doc.setOrarioend_mattina(orarioend_mattina);
            doc.setOrariostart_pom(orariostart_pomeriggio);
            doc.setOrarioend_pom(orarioend_pomeriggio);
            e.merge(doc);
            e.commit();

            e.begin();
            double hh, totale;
            boolean checkregistri = true;
            boolean checkdocs = true;

            List<TipoDoc_Allievi> doc_allievo;
            StringBuilder msg = new StringBuilder();
            StringBuilder warning = new StringBuilder();
            msg.append("Sono stati caricati tutti i documenti necessari per questa fase. Ora il progetto può essere inviato al Microcredito per essere controllato.<br>");
            warning.append("Tuttavia, i seguenti allievi non hanno effettuato le ore necessarie per la Fase B:<br>");
            for (Allievi allievo : allieviprg) {
                if (allievo.getEsito().equalsIgnoreCase("Fase B")) {
                    doc_allievo = e.getTipoDocAllievi(allievo.getProgetto().getStato());
                    //List<TipoDoc_Allievi> tipo_obb = e.getTipoDocAllieviObbl(a.getProgetto().getStato());
                    totale = 0;
                    for (Documenti_Allievi doc_a : allievo.getDocumenti()) {
                        if (doc_a.getTipo().getId() == 5 && doc_a.getDeleted() == 0) {
                            hh = (double) (doc_a.getOrarioend_mattina().getTime() - doc_a.getOrariostart_mattina().getTime());
                            if (doc_a.getOrariostart_pom() != null && doc_a.getOrarioend_pom() != null) {
                                hh += (double) (doc_a.getOrarioend_pom().getTime() - doc_a.getOrariostart_pom().getTime());
                            }
                            totale += hh / 3600000;
                        }
                        doc_allievo.remove(doc_a.getTipo());
                    }
                    if (!doc_allievo.isEmpty()) {
                        checkdocs = false;
                    }
                    if (totale < 20) {
                        checkregistri = false;
                        warning.append("• ").append(allievo.getCognome()).append(" ").append(allievo.getNome()).append(" (").append(String.valueOf(totale).replace(".0", "")).append("/20h)<br>");
                    }
                }
            }
            for (DocumentiPrg dprg : doc.getAllievo().getProgetto().getDocumenti()) {
                tipo_obb_prg.remove(dprg.getTipo());
            }
            //se sono stati caricati tutti i doc obbligatori per il progetto e per gli alunni, setto il progetto come idoneo per la prossima fase
            if (tipo_obb_prg.isEmpty() && checkdocs) {
                doc.getAllievo().getProgetto().setControllable(1);
                e.merge(doc.getAllievo().getProgetto());
                if (checkregistri) {
                    resp.addProperty("message", msg.toString());
                } else {
                    resp.addProperty("message", msg.append(warning).toString());
                }
            } else {
                resp.addProperty("message", "");
            }
            e.commit();
            resp.addProperty("result", true);
        } catch (PersistenceException | ParseException ex) {
            ex.printStackTrace();
            e.rollBack();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA modifyRegistro: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile modificare il registro.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void uploadDocPrg_FaseB(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject resp = new JsonObject();

        Part p = request.getPart("file");
        Entity e = new Entity();
        try {
            Allievi a = e.getEm().find(Allievi.class,
                    Long.parseLong(request.getParameter("idallievo")));
            //ProgettiFormativi prg = a.getProgetto();
            TipoDoc_Allievi tipo = e.getEm().find(TipoDoc_Allievi.class,
                    Long.parseLong(request.getParameter("id_tipo")));
            List<TipoDoc_Allievi> tipo_obb = e.getTipoDocAllieviObbl(a.getProgetto().getStato());
            List<TipoDoc> tipo_obb_prg = e.getTipoDocObbl(a.getProgetto().getStato());
            User us = (User) request.getSession().getAttribute("user");

            e.begin();
            //creao il path
            String path = e.getPath("pathDocSA_Allievi").replace("@rssa", Utility.correctName(us.getSoggettoAttuatore().getId().toString() + "")).replace("@folder", Utility.correctName(a.getCodicefiscale()));
            File dir = new File(path);
            createDir(path);
            String file_path;
            String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());

            //scrivo il file su disco
            if (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0) {
                file_path = dir.getAbsolutePath() + File.separator + tipo.getDescrizione() + "_" + today + p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                p.write(file_path);
                Documenti_Allievi doc = new Documenti_Allievi();
                doc.setPath(file_path);
                doc.setTipo(tipo);
                doc.setAllievo(a);
                e.persist(doc);
            }
            //se carico il MODELLO SE setto il valore in Allievo
            if (request.getParameter("prestiti") != null && request.getParameter("protocollo") != null) {
                a.setProtocollo(request.getParameter("protocollo"));
                a.setSelfiemployement((Selfiemployment_Prestiti) e.getEm().find(Selfiemployment_Prestiti.class,
                        Long.parseLong(request.getParameter("prestiti"))));
                e.merge(a);
            }
            //se carico il MODELLO 8 setto il valore in Allievo
            if (request.getParameter("idea") != null) {
                a.setIdea_impresa(request.getParameter("idea"));
                e.merge(a);
            }
            e.commit();

            e.begin();
            double hh, totale;
            boolean checkregistri = true;
            boolean checkdocs = true;

            List<TipoDoc_Allievi> doc_allievo;
            StringBuilder msg = new StringBuilder();
            StringBuilder warning = new StringBuilder();
            msg.append("Sono stati caricati tutti i documenti necessari per questa fase. Ora il progetto può essere inviato al Microcredito per essere controllato.<br>");
            warning.append("Tuttavia, i seguenti allievi non hanno effettuato le ore necessarie per la Fase B:<br>");
            for (Allievi allievo : a.getProgetto().getAllievi().stream().filter(all -> all.getStatopartecipazione().getId().equals("01")).collect(Collectors.toList())) {//solo allievi regolarmente iscritti
                doc_allievo = new ArrayList<>();
                doc_allievo.addAll(tipo_obb);
                totale = 0;
                for (Documenti_Allievi doc : allievo.getDocumenti()) {
                    if (allievo.getEsito().equalsIgnoreCase("Fase B")) {
                        if (doc.getTipo().getId() == 5 && doc.getDeleted() == 0) {
                            hh = (double) (doc.getOrarioend_mattina().getTime() - doc.getOrariostart_mattina().getTime());
                            if (doc.getOrariostart_pom() != null && doc.getOrarioend_pom() != null) {
                                hh += (double) (doc.getOrarioend_pom().getTime() - doc.getOrariostart_pom().getTime());
                            }
                            totale += hh / 3600000;
                        }
                    }
                    doc_allievo.remove(doc.getTipo());
                }
                if (!doc_allievo.isEmpty()) {
                    checkdocs = false;
                }
                if (allievo.getEsito().equalsIgnoreCase("Fase B")) {
                    if (totale < 20) {
                        checkregistri = false;
                        warning.append("• ").append(allievo.getCognome()).append(" ").append(allievo.getNome()).append(" (").append(String.valueOf(totale).replace(".0", "")).append("/20h)<br>");
                    }
                }
            }

            for (DocumentiPrg dprg : a.getProgetto().getDocumenti()) {
                tipo_obb_prg.remove(dprg.getTipo());
            }
            //se sono stati caricati tutti i doc obbligatori per il progetto e per gli alunni, setto il progetto come idoneo per la prossima fase
            if (tipo_obb_prg.isEmpty() && checkdocs) {
                a.getProgetto().setControllable(1);
                e.merge(a.getProgetto());
                if (checkregistri) {
                    resp.addProperty("message", msg.toString());
                } else {
                    resp.addProperty("message", msg.append(warning).toString());
                }
            } else {
                resp.addProperty("message", "");
            }
            e.commit();
            resp.addProperty("result", true);

        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.rollBack();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA uploadDocPrg_FaseB: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile caricare il documento.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void modifyDocPrg_FaseB(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject resp = new JsonObject();

        Part p = request.getPart("file");
        Entity e = new Entity();
        try {

            e.begin();
            //se carico il MODELLO SE setto il valore in Allievo
            Documenti_Allievi d = e.getEm().find(Documenti_Allievi.class,
                    Long.parseLong(request.getParameter("id")));
            Allievi a = e.getEm().find(Allievi.class,
                    d.getAllievo().getId());
            if (request.getParameter("prestiti") != null && request.getParameter("protocollo") != null) {
                a.setSelfiemployement((Selfiemployment_Prestiti) e.getEm().find(Selfiemployment_Prestiti.class,
                        Long.parseLong(request.getParameter("prestiti"))));
                a.setProtocollo(request.getParameter("protocollo"));
                e.merge(a);
            }
            //se carico il MODELLO 8 setto il valore in Allievo
            if (request.getParameter("idea") != null) {
                a.setIdea_impresa(request.getParameter("idea"));
                e.merge(a);
            }
            p.write(d.getPath());

            e.commit();
            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA modifyDocPrg_FaseB: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile aggiornare il documento.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void getTotalHoursRegistriByAllievo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        double totale = 0;
        double hh;
        JsonObject resp = new JsonObject();
        boolean today = false;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            List<Documenti_Allievi> docs = e.getDocAllievo(e.getEm().find(Allievi.class,
                    Long.parseLong(request.getParameter("idallievo"))));
            for (Documenti_Allievi registro : docs) {
                if (registro.getTipo().getId() == 5 && registro.getDeleted() == 0) {
                    if (sdf.format(registro.getGiorno()).equals(sdf.format(new Date()))) {
                        today = true;
                    }
                    hh = (double) (registro.getOrarioend_mattina().getTime() - registro.getOrariostart_mattina().getTime());
                    if (registro.getOrariostart_pom() != null && registro.getOrarioend_pom() != null) {
                        hh += (double) (registro.getOrarioend_pom().getTime() - registro.getOrariostart_pom().getTime());
                    }
                    totale += hh / 3600000;
                }
            }
            resp.addProperty("today", today);
            resp.addProperty("totale", totale);
            response.getWriter().write(resp.toString());
        } catch (PersistenceException ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void uploadRegistrioAula(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        User us = (User) request.getSession().getAttribute("user");
        try {
            e.begin();

            TipoDoc t = e.getEm().find(TipoDoc.class,
                    10L);//10 id tipo registro aula.
            ProgettiFormativi prg = e.getEm().find(ProgettiFormativi.class,
                    Long.parseLong(request.getParameter("idprogetto")));
            Docenti docente = e.getEm().find(Docenti.class,
                    Long.parseLong(request.getParameter("docente")));
            String[] date = request.getParameter("range").split("-");

            SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
            SimpleDateFormat sdf_date = new SimpleDateFormat("dd/MM/yyyy");

            DocumentiPrg doc = new DocumentiPrg();

            doc.setProgetto(prg);
            doc.setDocente(docente);

            if (date.length == 3) {
                doc.setOrariostart(sdf_time.parse(date[1].trim()));
                doc.setOrarioend(sdf_time.parse(date[2].trim()));
                doc.setGiorno(sdf_date.parse(date[0].trim()));
            } else {
                doc.setOrariostart(sdf_time.parse(date[0].trim()));
                doc.setOrarioend(sdf_time.parse(date[1].trim()));
                doc.setGiorno(sdf_date.parse(request.getParameter("giorno")));
            }

            doc.setValidate(0);
            doc.setOre((double) (doc.getOrarioend().getTime() - doc.getOrariostart().getTime()) / 3600000);//calcolo ore
            prg.setOre(prg.getOre() + doc.getOre());
            doc.setTipo(t);

            Part p = request.getPart("registro");

            if (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0) {
                String path = e.getPath("pathDocSA_Prg").replace("@rssa", us.getSoggettoAttuatore().getId().toString()).replace("@folder", prg.getId().toString());
                File dir = new File(path);
                createDir(path);
                String file_path = dir.getAbsolutePath() + File.separator + t.getDescrizione() + "_" + new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date()) + p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                p.write(file_path);
                doc.setPath(file_path);
            }

            List<Presenti> presenti = new ArrayList<>();
            Allievi a;
            Date in, out;
            sdf_time.setTimeZone(TimeZone.getTimeZone("CET"));//per fixare l'ora dei presenti
            for (String s : request.getParameterValues("allievi[]")) {
                a = e.getEm().find(Allievi.class,
                        Long.parseLong(s));
                in = sdf_time.parse(request.getParameter("time_start_" + a.getId()));
                out = sdf_time.parse(request.getParameter("time_end_" + a.getId()));
                presenti.add(new Presenti(a.getId(), a.getNome(), a.getCognome(), in, out, getHour(in, out)));
            }
            ObjectMapper mapper = new ObjectMapper();
            doc.setPresenti(mapper.writeValueAsString(presenti));
            e.persist(doc);
            e.merge(prg);
            /* controllo se tutti i documenti sono stati caricati per poter mandare il progetto avanti */
            List<TipoDoc> tipo_obb = e.getTipoDocObbl(prg.getStato());
            List<DocumentiPrg> doc_list = e.getDocPrg(prg);

            for (DocumentiPrg doc_prg : doc_list) {
                tipo_obb.remove(doc_prg.getTipo());
            }
            if (tipo_obb.isEmpty()) {
                prg.setControllable(1);
                e.merge(prg);
                resp.addProperty("message", "Hai caricato tutti i documenti necessari per questa fase. Ora il progetto può essere inviato al Microcredito per essere controllato.");
            } else {
                resp.addProperty("message", "");
            }

            e.commit();
            resp.addProperty("result", true);
        } catch (PersistenceException | ParseException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA uploadRegistrioAula: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile caricare il registro.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void setSIGMA(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        e.begin();

        try {
            Allievi a = e.getEm().find(Allievi.class,
                    Long.parseLong(request.getParameter("id")));
            if (request.getParameter("sigma") != null) {
                a.setStatopartecipazione((StatoPartecipazione) e.getEm().find(StatoPartecipazione.class,
                        request.getParameter("sigma")));
                e.merge(a);
            }
            e.commit();
            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            e.rollBack();
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA setSIGMA: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile impostare lo stato di partecipazione dell'allievo.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void modifyRegistrioAula(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            e.begin();
            Docenti docente = e.getEm().find(Docenti.class,
                    Long.parseLong(request.getParameter("docente")));
            String[] date = request.getParameter("range").split("-");

            SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
            SimpleDateFormat sdf_date = new SimpleDateFormat("dd/MM/yyyy");

            DocumentiPrg doc = e.getEm().find(DocumentiPrg.class,
                    Long.parseLong(request.getParameter("iddoc")));

            doc.setDocente(docente);
            doc.setOrariostart(sdf_time.parse(date[1].trim()));
            doc.setOrarioend(sdf_time.parse(date[2].trim()));
            doc.setGiorno(sdf_date.parse(date[0].trim()));
            doc.setValidate(0);
            ProgettiFormativi prg = doc.getProgetto();
            prg.setOre(prg.getOre() - doc.getOre());
            doc.setOre((double) (doc.getOrarioend().getTime() - doc.getOrariostart().getTime()) / 3600000);//calcolo ore
            prg.setOre(prg.getOre() + doc.getOre());
            Part p = request.getPart("registro");

            if (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0) {
                p.write(doc.getPath());
            }

            List<Presenti> presenti = new ArrayList<>();
            sdf_time.setTimeZone(TimeZone.getTimeZone("CET"));//per fixare l'ora dei presenti
            Allievi a;
            Date in, out;
            for (String s : request.getParameterValues("allievi[]")) {
                a = e.getEm().find(Allievi.class,
                        Long.parseLong(s));
                in = sdf_time.parse(request.getParameter("time_start_" + a.getId()));
                out = sdf_time.parse(request.getParameter("time_end_" + a.getId()));
                presenti.add(new Presenti(a.getId(), a.getNome(), a.getCognome(), in, out, getHour(in, out)));
            }
            ObjectMapper mapper = new ObjectMapper();
            doc.setPresenti(mapper.writeValueAsString(presenti));
            e.merge(doc);
            e.merge(prg);
            e.commit();
            resp.addProperty("result", true);
        } catch (PersistenceException | ParseException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA modifyRegistrioAula: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile modificare il registro.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void checkEmail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        Entity e = new Entity();
        Allievi a = e.getAllievoEmail(email);
        e.close();
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(a));
    }

    protected void deleteRegister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();

        Entity e = new Entity();

        try {
            e.begin();
            ProgettiFormativi p;
            DocumentiPrg registro = e.getEm().find(DocumentiPrg.class,
                    Long.parseLong(request.getParameter("id")));
            p = registro.getProgetto();

            p.setOre(p.getOre() - registro.getOre());
            registro.setDeleted(1);
            registro.setOre(0);

            registro.setOrarioend(registro.getOrariostart());
            List<DocumentiPrg> registri_day = e.getRegisterProgetto_by_Day(registro.getGiorno(), registro.getProgetto());

            registri_day.stream().filter(r -> !r.getId().equals(registro.getId())).forEach(d -> {
                p.setOre(p.getOre() - d.getOre());
                d.setDeleted(1);
                d.setOre(0);
                d.setOrarioend(d.getOrariostart());
                e.persist(d);
            });
            e.persist(registro);
            e.persist(p);
            e.commit();
            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA modifyRegistrioAula: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile modificare il registro.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void sendAsk(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();

        try {
            String ask = request.getParameter("ask");
            e.begin();
            Faq f = new Faq();
            f.setDomanda(ask);
            f.setDomanda_mod(ask);
            f.setDate_ask(new Date());
            f.setTipo(e.getEm().find(TipoFaq.class,
                    1L));
            f.setSoggetto(((User) request.getSession().getAttribute("user")).getSoggettoAttuatore());
            e.persist(f);
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA sendAsk: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile inviare il messaggio.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void uploadLezione(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            String msg = "Lezione caricata con successo.";
            String tipo_modello = request.getParameter("tipo_modello");
            e.begin();

            boolean modellotrovato = true;
            Docenti d;
            LezioneCalendario l;
            ModelliPrg m = e.getEm().find(ModelliPrg.class, Long.parseLong(request.getParameter("id_modello")));
            Lezioni_Modelli lm;
            Date giorno, orariostart, orarioend;
            if (tipo_modello.equalsIgnoreCase("m3_single") || tipo_modello.equalsIgnoreCase("m4_single")) {
                d = e.getEm().find(Docenti.class, Long.parseLong(request.getParameter("docente")));
                l = e.getEm().find(LezioneCalendario.class, Long.parseLong(request.getParameter("id_calendariolezione")));
                giorno = request.getParameter("giorno") != null ? new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("giorno")) : null;
                orariostart = request.getParameter("orario1_start") != null ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario1_start")) : null;
                orarioend = request.getParameter("orario1_end") != null ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario1_end")) : null;
                //Se modello m4, setto il ID del gruppo
                lm = tipo_modello.equalsIgnoreCase("m3_single") ? new Lezioni_Modelli(giorno, orariostart, orarioend, new Date(), m, l, d) : new Lezioni_Modelli(giorno, orariostart, orarioend, new Date(), m, l, d, Integer.parseInt(request.getParameter("idgruppo")));
                e.persist(lm);
            } else if (tipo_modello.equalsIgnoreCase("m3_double") || tipo_modello.equalsIgnoreCase("m4_double")) {
                boolean unico_docente = request.getParameter("docente1").equalsIgnoreCase(request.getParameter("docente2"));
                giorno = request.getParameter("giorno") != null ? new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("giorno")) : null;
                //Parte 1 Lezione
                d = e.getEm().find(Docenti.class, Long.parseLong(request.getParameter("docente1")));
                l = e.getEm().find(LezioneCalendario.class, Long.parseLong(request.getParameter("id_calendariolezione1")));
                orariostart = request.getParameter("orario1_start") != null ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario1_start")) : null;
                orarioend = request.getParameter("orario1_end") != null ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario1_end")) : null;
                //Se modello m4, setto il ID del gruppo
                lm = tipo_modello.equalsIgnoreCase("m3_double") ? new Lezioni_Modelli(giorno, orariostart, orarioend, new Date(), m, l, d) : new Lezioni_Modelli(giorno, orariostart, orarioend, new Date(), m, l, d, Integer.parseInt(request.getParameter("idgruppo")));

                //Parte 2 Lezione
                d = unico_docente ? d : e.getEm().find(Docenti.class, Long.parseLong(request.getParameter("docente2")));
                l = e.getEm().find(LezioneCalendario.class, Long.parseLong(request.getParameter("id_calendariolezione2")));
                orariostart = request.getParameter("orario2_start") != null ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario2_start")) : null;
                orarioend = request.getParameter("orario2_end") != null ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario2_end")) : null;
                Lezioni_Modelli lm2 = tipo_modello.equalsIgnoreCase("m3_double") ? new Lezioni_Modelli(giorno, orariostart, orarioend, new Date(), m, l, d) : new Lezioni_Modelli(giorno, orariostart, orarioend, new Date(), m, l, d, Integer.parseInt(request.getParameter("idgruppo")));

                e.persist(lm);
                e.persist(lm2);
            } else {
                modellotrovato = false;
            }

            //M3 controllo se tutti le lezioni sono caricate
            if (modellotrovato && tipo_modello.startsWith("m3")) {
                List<LezioneCalendario> listLezioneCalendario = e.getLezioniByModello(3);
                List<Lezioni_Modelli> listLezioniCaricate = e.getLezioniByProgetto(m);
                boolean completa = true;
                for (LezioneCalendario cal : listLezioneCalendario) {
                    if (!listLezioniCaricate.stream().anyMatch(lc -> lc.getLezione_calendario().getId().equals(cal.getId()))) {
                        completa = false;
                        break;
                    }
                }
                if (completa) {
                    m.setStato("R");
                    e.merge(m);
                    msg = "Tutte le lezioni relative al modello 3 sono state caricate, puoi procedere al caricamento dell'intero modulo.";
                }
            }
            //M4 controllo se tutti le lezioni sono caricate
            if (modellotrovato && tipo_modello.startsWith("m4")) {
                List<LezioneCalendario> listLezioneCalendario = e.getLezioniByModello(4);
                List<Lezioni_Modelli> listLezioniCaricate = e.getLezioniByProgetto(m);
                int gruppi = Utility.numberGroupsModello4(m.getProgetto());
                boolean completa = true;

                for (int i = 1; i <= gruppi; i++) {
                    for (LezioneCalendario cal : listLezioneCalendario) {
                        if (!Utility.isPresent_LessonGroup(listLezioniCaricate, cal.getId(), i)) {
                            completa = false;
                            break;
                        }
                    }
                }
                if (completa) {
                    m.setStato("R");
                    e.merge(m);
                    msg = "Tutte le lezioni relative al modello 4 sono state caricate, puoi procedere al caricamento dell'intero modulo.";
                }
            }

            e.commit();

            if (modellotrovato) {
                resp.addProperty("result", true);
                resp.addProperty("message", msg);
            } else {
                resp.addProperty("result", false);
                resp.addProperty("message", "Errore: non &egrave; stato possibile aggiornare la lezione.");
            }
        } catch (Exception ex) {
            e.rollBack();
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA uploadLezione: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile aggiornare la lezione.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void updateLezione(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            String tipo_modello = request.getParameter("tipo_modello");
            e.begin();

            Docenti d;
            Lezioni_Modelli lm;
            Date giorno, orariostart, orarioend;
            if (tipo_modello.equalsIgnoreCase("m3_single") || tipo_modello.equalsIgnoreCase("m4_single")) {
                d = e.getEm().find(Docenti.class, Long.parseLong(request.getParameter("docente1")));
                giorno = request.getParameter("giorno") != null ? new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("giorno")) : null;
                orariostart = request.getParameter("orario1_start") != null ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario1_start")) : null;
                orarioend = request.getParameter("orario1_end") != null ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario1_end")) : null;
                lm = e.getEm().find(Lezioni_Modelli.class, Long.parseLong(request.getParameter("id1")));
                lm.setDocente(d);
                lm.setGiorno(giorno);
                lm.setOrario_start(orariostart);
                lm.setOrario_end(orarioend);
                e.merge(lm);
            } else if (tipo_modello.equalsIgnoreCase("m3_double") || tipo_modello.equalsIgnoreCase("m4_double")) {
                boolean unico_docente = request.getParameter("docente1").equalsIgnoreCase(request.getParameter("docente2"));
                giorno = request.getParameter("giorno") != null ? new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("giorno")) : null;
                //Parte 1 Lezione
                d = e.getEm().find(Docenti.class, Long.parseLong(request.getParameter("docente1")));
                orariostart = request.getParameter("orario1_start") != null ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario1_start")) : null;
                orarioend = request.getParameter("orario1_end") != null ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario1_end")) : null;

                lm = e.getEm().find(Lezioni_Modelli.class, Long.parseLong(request.getParameter("id1")));
                lm.setDocente(d);
                lm.setGiorno(giorno);
                lm.setOrario_start(orariostart);
                lm.setOrario_end(orarioend);
                //Parte 2 Lezione
                d = unico_docente ? d : e.getEm().find(Docenti.class, Long.parseLong(request.getParameter("docente2")));
                orariostart = request.getParameter("orario2_start") != null ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario2_start")) : null;
                orarioend = request.getParameter("orario2_end") != null ? new SimpleDateFormat("HH:mm").parse(request.getParameter("orario2_end")) : null;
                Lezioni_Modelli lm2 = e.getEm().find(Lezioni_Modelli.class, Long.parseLong(request.getParameter("id2")));
                lm2.setDocente(d);
                lm2.setGiorno(giorno);
                lm2.setOrario_start(orariostart);
                lm2.setOrario_end(orarioend);

                e.merge(lm);
                e.merge(lm2);
            }
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            e.rollBack();
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA updateLezione: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile aggiornare la lezione.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void creaGruppi(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            String[] gruppi = request.getParameter("gruppi[]").split(",");
            e.begin();
            ModelliPrg m = e.getEm().find(ModelliPrg.class, Long.parseLong(request.getParameter("id_modello")));
            Allievi a;
            for (String neet : gruppi) {
                int gruppo = Integer.parseInt(neet.split("_")[0]);
                int allievo = Integer.parseInt(neet.split("_")[1]);
                a = e.getEm().find(Allievi.class, (long) allievo);
                a.setGruppo_faseB(gruppo);
                e.merge(a);
            }
            m.setStato("L"); //L, pronto per la creazione delle lezioni
            e.merge(m);

            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            e.rollBack();
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA creaGruppi: " + ex.getMessage());
            resp.addProperty("result", false);
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void manageM2(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tipo_op = request.getParameter("tipo_op");

        if (tipo_op != null && tipo_op.equalsIgnoreCase("download")) {
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");

            User us = (User) request.getSession().getAttribute("user");

            Entity e = new Entity();
            e.begin();
            ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("id_prg")));

            File downloadFile = Pdf_new.MODELLO2(e, getRequestValue(request, "modello"), us.getUsername(),
                    us.getSoggettoAttuatore(), p,
                    p.getAllievi().stream().filter(p1 -> p1.getStatopartecipazione().getId().equals("01")).collect(Collectors.toList()),
                    new DateTime(), true);
            e.close();

            if (downloadFile != null && downloadFile.exists()) {
                OutputStream outStream;
                try (FileInputStream inStream = new FileInputStream(downloadFile)) {
                    String mimeType = probeContentType(downloadFile.toPath());
                    if (mimeType == null) {
                        mimeType = "application/octet-stream";
                    }
                    response.setContentType(mimeType);
                    String headerKey = "Content-Disposition";
                    String headerValue = format("attachment; filename=\"%s\"", downloadFile.getName());
                    response.setHeader(headerKey, headerValue);
                    outStream = response.getOutputStream();
                    byte[] buffer = new byte[4096 * 4096];
                    int bytesRead;
                    while ((bytesRead = inStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }
                }
                outStream.close();
            } else {
                redirect(request, response, request.getContextPath() + "/404.jsp");
            }
        } else {
            JsonObject resp = new JsonObject();
            Part p = request.getPart("file_m2");

            Entity e = new Entity();
            try {
                String ext = p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                e.begin();
                ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("id_prg")));
                DocumentiPrg d = e.getEm().find(DocumentiPrg.class, Long.parseLong(request.getParameter("idfile")));
                String newpath = d.getPath() + "_v_" + new DateTime().toString(patternComplete) + "."
                        + ext;

                String directory = getStartPath(FilenameUtils.getFullPath(d.getPath()));
                newpath = getStartPath(newpath);
//                createDir(getStartPath() + FilenameUtils.getFullPath(d.getPath()));
//                p.write(getStartPath() + newpath);
                createDir(directory);
                p.write(newpath);
                d.setPath(newpath);
                pf.setStato(e.getEm().find(StatiPrg.class, "DV"));
                e.merge(d);
                e.merge(pf);
                e.commit();
                resp.addProperty("result", true);
            } catch (PersistenceException ex) {
                e.rollBack();
                ex.printStackTrace();
                e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA updateM2: " + ex.getMessage());
                resp.addProperty("result", false);
                resp.addProperty("message", "Errore: non &egrave; stato possibile aggiornare il modello 2.");
            } finally {
                e.close();
            }

            response.getWriter().write(resp.toString());
            response.getWriter().flush();
            response.getWriter().close();
        }

    }

    protected void addDocente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String save = getRequestValue(request, "save");
        String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());
        File downloadFile = null;
        boolean salvataggio = false;
        if (save.equals("1")) {//MODELLO
            salvataggio = false;
        } else if (save.equals("0")) {
            salvataggio = true;
        }

        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        User us = (User) request.getSession().getAttribute("user");
        String cf = getRequestValue(request, "cf").toUpperCase();

        Docenti verificaold = e.getDocenteByCF_SA(cf, us.getSoggettoAttuatore());

        if (verificaold == null) {

            try {
                e.begin();

                String nome = getRequestValue(request, "nome").toUpperCase();
                String cognome = getRequestValue(request, "cognome").toUpperCase();

                String email = getRequestValue(request, "email");

                Part docid = request.getPart("docid");
                String scadenza = getRequestValue(request, "scadenzadoc");
                Part cv = request.getPart("cv");

                Date data_nascita = null;
                if (!request.getParameter("data").equals("")) {
                    data_nascita = new SimpleDateFormat("dd/MM/yyyy").parse(getRequestValue(request, "data"));
                }

                String qrcrop = e.getPath("qr_crop");

                Docenti d = new Docenti(nome, cognome, cf, data_nascita, email);
                d.setFascia(e.getEm().find(FasceDocenti.class, getRequestValue(request, "fascia")));
                d.setStato("DV");
                d.setSoggetto(us.getSoggettoAttuatore());

                d.setScadenza_doc(new SimpleDateFormat("dd/MM/yyyy").parse(scadenza));
                String path = e.getPath("pathDoc_Docenti").replace("@docente", cf);
                String pathcv = path;
                File dir = new File(path);
                createDir(path);
                if (salvataggio) {
                    String ext = docid.getSubmittedFileName().substring(docid.getSubmittedFileName().lastIndexOf("."));
                    path += "Doc_id_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + cf + ext;
                    docid.write(dir.getAbsolutePath() + File.separator + "Doc_id_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + cf + ext);
                    d.setDocId(path.replace("\\", "/"));
                    ext = cv.getSubmittedFileName().substring(cv.getSubmittedFileName().lastIndexOf("."));
                    pathcv += "Curriculum_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + cf + ext;
                    if (salvataggio) {
                        cv.write(dir.getAbsolutePath() + File.separator + "Curriculum_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + cf + ext);
                    }
                    d.setCurriculum(pathcv.replace("\\", "/"));
                }
                /*Modifica 14 06 21 - Nuovi campi ed attivita*/
                String comune_nascita = new String(getRequestValue(request, "com_nas").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8);
                String reg_res = getRequestValue(request, "reg_res");
                String pec = getRequestValue(request, "pecmail");
                String cell = getRequestValue(request, "telefono");
                int titolo_studio = Integer.parseInt(getRequestValue(request, "tit_stu"));
                int area_studio = Integer.parseInt(getRequestValue(request, "area_stu"));
                int inquad = Integer.parseInt(getRequestValue(request, "inquad"));

                d.setComune_di_nascita(comune_nascita);
                d.setRegione_di_residenza(reg_res);
                d.setPec(pec);
                d.setCellulare(cell);
                d.setTitolo_di_studio(titolo_studio);
                d.setArea_prevalente_di_qualificazione(area_studio);
                d.setInquadramento(inquad);
                d.setTipo_inserimento("MANUALE (PIATTAFORMA GESTIONALE)");

                e.persist(d);
                try {
                    /*Modifica 14 06 21 - Singole Attivita*/
                    int nroAttivita_max = Integer.parseInt(e.getPath("numAttivita_docente"));
                    List<Attivita_Docente> list_attivita = new ArrayList();
                    Attivita_Docente temp;
                    for (int i = 1; i <= nroAttivita_max; i++) {
                        if (Integer.parseInt(getRequestValue(request, "attivita_vis_" + i)) == 1) {
                            temp = new Attivita_Docente(Integer.parseInt(getRequestValue(request, "tipo_att_" + i)),
                                    new String(getRequestValue(request, "committente_" + i).getBytes(Charsets.ISO_8859_1), Charsets.UTF_8),
                                    new SimpleDateFormat("dd/MM/yyyy").parse(getRequestValue(request, "data_inizio_" + i)),
                                    new SimpleDateFormat("dd/MM/yyyy").parse(getRequestValue(request, "data_fine_" + i)),
                                    Integer.parseInt(getRequestValue(request, "durata_" + i)),
                                    getRequestValue(request, "unita_" + i),
                                    Integer.parseInt(getRequestValue(request, "incarico_" + i)),
                                    Integer.parseInt(getRequestValue(request, "fonte_" + i)),
                                    Integer.parseInt(getRequestValue(request, "progr_" + i)),
                                    d);
                            list_attivita.add(temp);
                            e.persist(temp);
                        }
                    }
                    d.setAttivita(list_attivita);
                } catch (Exception ex) {
                    e.rollBack();
                    resp.addProperty("result", false);
                    resp.addProperty("message", "RICHIESTA ACCREDITAMENTO DOCENTE ERRATA ERRORE NELLE ATTIVITA'. " + ex.getMessage() + ". CONTROLLARE.");
                    ex.printStackTrace();
                }
                
                //CREA DOCUMENTO
                if (salvataggio) {

                    boolean all1OK;
                    String erroreall1OK = "RICHIESTA ACCREDITAMENTO DOCENTE ERRATA. CONTROLLARE.";

                    try {
                        TipoDoc richiesta = e.getEm().find(TipoDoc.class, 34L);
                        Part p = request.getPart("doc_" + richiesta.getId());
                        String ext1 = p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                        String destpath = dir.getAbsolutePath() + File.separator
                                + Utility.correctName(richiesta.getDescrizione())
                                + today + "_" + cf + ext1;
                        p.write(destpath);
                        File pdfdest = new File(destpath);
                        String res = checkFirmaQRpdfA("ALLEGATOB1", us.getUsername(),
                                pdfdest, us.getSoggettoAttuatore().getCodicefiscale(), qrcrop);
                        if (!res.equals("OK")) {
                            all1OK = false;
                            erroreall1OK = res;
                        } else {
                            all1OK = true;
                        }

                        if (all1OK) {
                            d.setRichiesta_accr(destpath.replace("\\", "/"));
                            //  UPLOAD
                            e.merge(d);
                            e.commit();
                            resp.addProperty("result", true);
                        } else {
                            e.rollBack();
                            resp.addProperty("result", false);
                            resp.addProperty("message", erroreall1OK);
                        }

                    } catch (Exception ex) {
                        e.rollBack();
                        resp.addProperty("result", false);
                        resp.addProperty("message", "RICHIESTA ACCREDITAMENTO DOCENTE ERRATA. " + ex.getMessage() + ". CONTROLLARE.");
                        ex.printStackTrace();
                    }
                } else {
                    String richiesta_accr = dir.getAbsolutePath() + File.separator + "RICH_ACCR_"
                            + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + cf + ".pdf";
                    e.rollBack();
                    downloadFile = Pdf_new.ALLEGATOB1(richiesta_accr, e, us.getUsername(), d, new DateTime());
                    resp.addProperty("result", true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                e.insertTracking(String.valueOf(us.getId()), "OperazioniSA addDocente: " + ex.getMessage());
                resp.addProperty("result", false);
                resp.addProperty("message", "Errore: non &egrave; stato possibile aggiungere il docente.");
            } finally {
                e.close();
            }

        } else {
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: Codice fiscale già presente.");
        }

        if (salvataggio) {
            response.getWriter()
                    .write(resp.toString());
            response.getWriter()
                    .flush();
            response.getWriter()
                    .close();
        } else {
            if (downloadFile != null && downloadFile.exists()) {
                OutputStream outStream;
                try (FileInputStream inStream = new FileInputStream(downloadFile)) {
                    String mimeType = probeContentType(downloadFile.toPath());
                    if (mimeType == null) {
                        mimeType = "application/octet-stream";
                    }
                    response.setContentType(mimeType);
                    String headerKey = "Content-Disposition";
                    String headerValue = format("attachment; filename=\"%s\"", downloadFile.getName());
                    response.setHeader(headerKey, headerValue);
                    outStream = response.getOutputStream();
                    byte[] buffer = new byte[4096 * 4096];
                    int bytesRead;
                    while ((bytesRead = inStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }
                }
                outStream.close();
            } else {
                redirect(request, response, request.getContextPath() + "/404.jsp");
            }
        }
    }

    protected void checkEmail_Docente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        User us = (User) request.getSession().getAttribute("user");
        Entity e = new Entity();
        Docenti a = e.getDocenteByEmail(email, us.getSoggettoAttuatore());
        e.close();
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(a));
    }

    protected void checkCF_Docente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String cf = request.getParameter("cf");
        User us = (User) request.getSession().getAttribute("user");
        Entity e = new Entity();
        Docenti a = e.getDocenteByCF_SA(cf, us.getSoggettoAttuatore());
        e.close();
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(a));

//        if(a == null){
//            
//        }else{
//            response.getWriter().write(mapper.writeValueAsString(a));
//        }
    }

    protected void manageMembriStaff(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        String title;
        try {
            e.begin();
            String row = request.getParameter("row");
            ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("pf" + row)));
            if (request.getParameter("mid_" + row) != null && !request.getParameter("mid_" + row).equalsIgnoreCase("")) {
                //Aggiornamento Membro
                title = "Modifica Membro";
                StaffModelli s = e.getEm().find(StaffModelli.class, Long.parseLong(request.getParameter("mid_" + row)));
                s.setNome(request.getParameter("nome" + row));
                s.setCognome(request.getParameter("cognome" + row));
                s.setEmail(request.getParameter("email" + row));
                s.setTelefono(request.getParameter("telefono" + row));
                e.merge(s);
            } else {
                //Inserimento Membro
                title = "Inserimento Membro";
                StaffModelli n = new StaffModelli(request.getParameter("nome" + row), request.getParameter("cognome" + row), request.getParameter("email" + row), request.getParameter("telefono" + row), pf, 1, "Ospite", new Date());
                e.persist(n);
            }
            e.commit();

            resp.addProperty("result", true);
            resp.addProperty("title", title);
            resp.addProperty("message", "Operazione effettuata con successo");
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA manageMembriStaff: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("title", "Errore");
            resp.addProperty("message", "Errore: operazione fallita");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void downloadregistro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        String path = 
    }

    protected void deleteMembroStaff(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            e.begin();
            StaffModelli sm = e.getEm().find(StaffModelli.class, Long.parseLong(request.getParameter("id")));
            sm.setAttivo(0);
            e.merge(sm);
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro deleteMembroStaff: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile procedere con l'operazione.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();

    }

    protected void rendicontaAllievo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        String qrcrop = e.getPath("qr_crop");
        JsonObject resp = new JsonObject();
        User us = (User) request.getSession().getAttribute("user");
        e.begin();

        try {
            MascheraM5 mask = new MascheraM5();
            Allievi a = e.getEm().find(Allievi.class, Long.parseLong(request.getParameter("id_allievo")));
            mask.setAllievo(a);
            mask.setProgetto_formativo(a.getProgetto());
            mask.setForma_giuridica(e.getEm().find(Formagiuridica.class, Integer.parseInt(request.getParameter("formaGiuridica"))));
            mask.setComune_localizzazione(e.getEm().find(Comuni.class, Long.parseLong(request.getParameter("comune"))));
            mask.setSede(request.getParameter("sede").equalsIgnoreCase("SI"));
            mask.setColloquio(request.getParameter("colloquio").equalsIgnoreCase("SI"));
            mask.setFabbisogno_finanziario(Double.parseDouble(request.getParameter(("tff"))));
            mask.setFinanziamento_richiesto_agevolazione(Double.parseDouble(request.getParameter(("tfra"))));
            mask.setRagione_sociale(new String(request.getParameter("ragioneSociale").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
            mask.setIdea_impresa(new String(request.getParameter("ideaImpresa").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
            mask.setMotivazione(new String(request.getParameter("motivazione").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
            mask.setAteco(e.getEm().find(Ateco.class, request.getParameter("ateco")));

            Part p = request.getPart("doc");
            if (Boolean.parseBoolean(request.getParameter("domanda_ammissione")) && (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0)) {
                mask.setDomanda_ammissione_presente(true);

                String path = e.getPath("pathDocSA_Allievi").replace("@rssa", Utility.correctName(us.getSoggettoAttuatore().getId() + "")).replace("@folder", a.getCodicefiscale());
                File dir = new File(path);
                createDir(path);

                String ext = p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                path += "domanda_ammissione_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + a.getCodicefiscale() + ext;
                p.write(dir.getAbsolutePath() + File.separator + "domanda_ammissione_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + a.getCodicefiscale() + ext);
                mask.setDomanda_ammissione(path.replace("\\", "/"));
            } else {
                mask.setDomanda_ammissione_presente(false);
            }

            if (Boolean.parseBoolean(request.getParameter("no_agevolazione"))) {
                mask.setNo_agevolazione(true);
                mask.setNo_agevolazione_opzione(request.getParameter("no_agevolazione_option"));
                mask.setBando_reg(false);
                mask.setBando_se(false);
                mask.setBando_sud(false);
            } else {
                mask.setNo_agevolazione(false);

                if (Boolean.parseBoolean(request.getParameter("bando_se"))) {
                    mask.setBando_se(true);
                    mask.setBando_se_opzione(request.getParameter("bando_se_option"));
                } else {
                    mask.setBando_se(false);
                }
                if (Boolean.parseBoolean(request.getParameter("bando_sud"))) {
                    mask.setBando_sud(true);
                    mask.setBando_sud_opzione(request.getParameter("bando_sud_options"));
                } else {
                    mask.setBando_sud(false);
                }
                if (Boolean.parseBoolean(request.getParameter("bando_reg"))) {
                    mask.setBando_reg(true);
                    mask.setBando_reg_nome(request.getParameter("bando_reg_option"));
                } else {
                    mask.setBando_reg(false);
                }

            }
            mask.setTabella_valutazionefinale_val(request.getParameter("tab1"));
            mask.setTabella_valutazionefinale_punteggio(Double.parseDouble(request.getParameter("punteggio_tab1")));
            mask.setTabella_valutazionefinale_totale(Double.parseDouble(request.getParameter("valfinale_tab1")));

            boolean modello7OK;
            String erroremodello7OK = "MODELLO 7 ERRATO. CONTROLLARE.";

            //Modello 7 - Attestato di frequenza, necessario per la tabella della premialità            
            Part p7 = request.getPart("doc_modello7");
            if (p7 != null && p7.getSubmittedFileName() != null && p7.getSubmittedFileName().length() > 0) {
                //Modifica 28/05/21
                //Se il modello 7 è stato caricato ed il numero delle ore effettuate tra fase A e fase B è minimo 64, allora la tabella di premialità si popola con i dati della tabella finale
                if (Boolean.parseBoolean(request.getParameter("hh64"))) {
                    mask.setTabella_premialita(true);
                    mask.setTabella_premialita_val(request.getParameter("tab1"));
                    mask.setTabella_premialita_punteggio(Double.parseDouble(request.getParameter("punteggio_tab1")));
                    mask.setTabella_premialita_totale(Double.parseDouble(request.getParameter("valfinale_tab1")));
                } else {
                    mask.setTabella_premialita(false);
                }

                TipoDoc_Allievi tipodoc_m7 = e.getEm().find(TipoDoc_Allievi.class, 22L);
                Documenti_Allievi modello7_allievo = a.getDocumenti().stream().filter(dc -> dc.getDeleted() == 0
                        && dc.getTipo().getId().equals(tipodoc_m7.getId())).findFirst().orElse(null);
                if (modello7_allievo != null) {
                    File dir = new File(modello7_allievo.getPath());
                    p7.write(dir.getAbsolutePath());

                    File pdfdest = new File(dir.getAbsolutePath());

                    String res = checkFirmaQRpdfA("MODELLO7", us.getUsername(),
                            pdfdest, us.getSoggettoAttuatore().getCodicefiscale(), qrcrop);
                    if (!res.equals("OK")) {
                        modello7OK = false;
                        erroremodello7OK = res;
                    } else {
                        modello7OK = true;
                    }

                } else {

                    String path = e.getPath("pathDocSA_Allievi").replace("@rssa", Utility.correctName(us.getSoggettoAttuatore().getId() + "")).replace("@folder", Utility.correctName(a.getCodicefiscale()));
                    File dir = new File(path);
                    createDir(path);
                    String ext = p7.getSubmittedFileName().substring(p7.getSubmittedFileName().lastIndexOf("."));
                    String namefile = "Modello7_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + a.getCodicefiscale() + ext;
                    path += namefile;
                    String destpath = dir.getAbsolutePath() + File.separator + namefile;
                    p7.write(destpath);

                    File pdfdest = new File(destpath);

                    String res = checkFirmaQRpdfA("MODELLO7", us.getUsername(),
                            pdfdest, us.getSoggettoAttuatore().getCodicefiscale(), qrcrop);
                    if (!res.equals("OK")) {
                        modello7OK = false;
                        erroremodello7OK = res;
                    } else {
                        modello7OK = true;
                    }

                    Documenti_Allievi m7 = new Documenti_Allievi(path.replace("\\", "/"), tipodoc_m7, null, a);
                    e.persist(m7);
                    a.getDocumenti().add(m7);
                }
                e.merge(a);
            } else {
                modello7OK = false;
            }

            if (modello7OK) {
                e.persist(mask);
                e.commit();
                resp.addProperty("result", true);
            } else {
                e.rollBack();
                resp.addProperty("result", false);
                resp.addProperty("message", erroremodello7OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA rendicontaAllievo: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile rendicontare l'allievo.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void concludiPrg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();

        Entity e = new Entity();
        e.begin();
        User us = (User) request.getSession().getAttribute("user");

        try {
            ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("pf")));
            String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());

            //MODELLO 6
            String qrcrop = e.getPath("qr_crop");
            TipoDoc modello6 = e.getEm().find(TipoDoc.class, 31L);
            String path_m6 = e.getPath("pathDocSA_Prg").replace("@rssa", us.getSoggettoAttuatore().getId().toString()).replace("@folder", pf.getId().toString());
            File dir = new File(path_m6);
            createDir(path_m6);
            Part partm6 = request.getPart("doc_step4_" + modello6.getId());
            String ext = partm6.getSubmittedFileName().substring(partm6.getSubmittedFileName().lastIndexOf("."));
            path_m6 += modello6.getDescrizione() + "_" + today + ext;
            String file_path = dir.getAbsolutePath() + File.separator + modello6.getDescrizione() + "_" + today + ext;
            partm6.write(file_path);

            File pdfdest = new File(file_path);
            String res = checkFirmaQRpdfA("MODELLO6", us.getUsername(), pdfdest, us.getSoggettoAttuatore().getCodicefiscale(), qrcrop);

            if (res.equals("OK")) {
                DocumentiPrg doc_m6 = new DocumentiPrg();
                doc_m6.setPath(path_m6.replace("\\", "/"));
                doc_m6.setTipo(modello6);
                doc_m6.setProgetto(pf);
                e.persist(doc_m6);

                List<DocumentiPrg> documenti = pf.getDocumenti();
                documenti.add(doc_m6);
                pf.setDocumenti(documenti);
                pf.setStato(e.getEm().find(StatiPrg.class, "DVB"));
                e.merge(pf);
                e.flush();
                e.commit();
                resp.addProperty("result", true);
            } else {
                e.rollBack();
                resp.addProperty("result", false);
                resp.addProperty("message", res);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            e.rollBack();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA concludiPrg: " + ex.getMessage());
            resp.addProperty("result", false);
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();

    }

    protected void modifyEmail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        String iddocente = getRequestValue(request, "id");
        String email = getRequestValue(request, "email");

        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {

            e.begin();
            Docenti d = e.getEm().find(Docenti.class,
                    Long.parseLong(iddocente));
            d.setEmail(email);
            e.merge(d);
            e.commit();
            resp.addProperty("result", true);
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()),
                    "OperazioniSA Modifica MAIL docente: " + iddocente + " - " + email);
        } catch (Exception ex) {
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()),
                    "ERRORE: OperazioniSA Modifica MAIL docente: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile modificare la mail.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();

    }

    protected void deleteModello5Alunno(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();

        Entity e = new Entity();

        try {
            e.begin();
            MascheraM5 m5 = e.getEm().find(MascheraM5.class, Long.parseLong(request.getParameter("id")));
            Allievi a = m5.getAllievo();
            TipoDoc_Allievi tipodoc_m5 = e.getEm().find(TipoDoc_Allievi.class, 20L);
            TipoDoc_Allievi tipodoc_m7 = e.getEm().find(TipoDoc_Allievi.class, 22L);
            Documenti_Allievi modello5_allievo = a.getDocumenti().stream().filter(dc -> dc.getDeleted() == 0
                    && dc.getTipo().getId().equals(tipodoc_m5.getId())).findFirst().orElse(null);
            if (modello5_allievo != null) {
                modello5_allievo.setDeleted(1);
            }
            Documenti_Allievi modello7_allievo = a.getDocumenti().stream().filter(dc
                    -> dc.getDeleted() == 0
                    && dc.getTipo().getId().equals(tipodoc_m7.getId())
            ).findFirst().orElse(null);
            if (modello7_allievo != null) {
                modello7_allievo.setDeleted(1);
            }
            e.remove(m5);
            e.merge(a);
            e.commit();
            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA deleteModello5Alunno: " + ex.getMessage());
            resp.addProperty("result", false);
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void uploadM5Alunno(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TO DO 
        JsonObject resp = new JsonObject();
        User us = (User) request.getSession().getAttribute("user");
        Part p = request.getPart("file");
        Entity e = new Entity();
        try {
            e.begin();
            String qrcrop = e.getPath("qr_crop");
            Allievi a = e.getEm().find(Allievi.class, Long.parseLong(request.getParameter("id")));
            TipoDoc_Allievi tipodoc_m5 = e.getEm().find(TipoDoc_Allievi.class, 20L);
            Documenti_Allievi modello5_allievo = a.getDocumenti().stream().filter(dc -> dc.getDeleted() == 0
                    && dc.getTipo().getId().equals(tipodoc_m5.getId())
            ).findFirst().orElse(null);

            boolean modello5OK = false;
            String erroremodello5OK = "MODELLO 5 ERRATO. CONTROLLARE.";

            try {

                if (modello5_allievo != null) {
                    File dir = new File(modello5_allievo.getPath());
                    p.write(dir.getAbsolutePath());

                    File pdfdest = new File(dir.getAbsolutePath());
                    String res = checkFirmaQRpdfA("MODELLO5", us.getUsername(),
                            pdfdest, us.getSoggettoAttuatore().getCodicefiscale(), qrcrop);
                    if (!res.equals("OK")) {
                        modello5OK = false;
                        erroremodello5OK = res;
                    }

                } else {
                    String path = e.getPath("pathDocSA_Allievi").replace("@rssa", Utility.correctName(us.getSoggettoAttuatore().getId() + "")).replace("@folder", Utility.correctName(a.getCodicefiscale()));
                    File dir = new File(path);
                    createDir(path);
                    String ext = p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                    String namefile = "Modello5_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + a.getCodicefiscale() + ext;
                    path += namefile;
                    String destpath = dir.getAbsolutePath() + File.separator + namefile;
                    p.write(destpath);
                    File pdfdest = new File(destpath);
                    String res = checkFirmaQRpdfA("MODELLO5", us.getUsername(), pdfdest, us.getSoggettoAttuatore().getCodicefiscale(), qrcrop);
                    if (!res.equals("OK")) {
                        modello5OK = false;
                        erroremodello5OK = res;
                    } else {
                        modello5OK = true;
                    }
                    Documenti_Allievi m5 = new Documenti_Allievi(path.replace("\\", "/"), tipodoc_m5, null, a);
                    e.persist(m5);
                    a.getDocumenti().add(m5);
                }
            } catch (Exception ex) {
                modello5OK = false;
                erroremodello5OK = "MODELLO 5 ERRATO. " + ex.getMessage() + ". CONTROLLARE.";
            }
            if (modello5OK) {
                e.merge(a);
                e.commit();
                resp.addProperty("result", true);
            } else {
                e.rollBack();
                resp.addProperty("result", false);
                resp.addProperty("message", erroremodello5OK);
            }
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA uploadM5Alunno: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibilecaricare il modello 5.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void scaricaModello7(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        User us = (User) request.getSession().getAttribute("user");

        String idallievo = getRequestValue(request, "iduser");
        String orerendicontabili = Utility.roundFloatAndFormat(Long.parseLong(getRequestValue(request, "orerendicontabili")), true);

        File downloadFile = null;
        try {

            Entity e = new Entity();
            e.begin();
            Allievi a = e.getEm().find(Allievi.class, Long.parseLong(idallievo));
            downloadFile = Pdf_new.MODELLO7(e, us.getUsername(), a, orerendicontabili,
                    new DateTime(), true);
            e.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (downloadFile != null && downloadFile.exists()) {
            OutputStream outStream;
            try (FileInputStream inStream = new FileInputStream(downloadFile)) {
                String mimeType = probeContentType(downloadFile.toPath());
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
                response.setContentType(mimeType);
                String headerKey = "Content-Disposition";
                String headerValue = format("attachment; filename=\"%s\"", downloadFile.getName());
                response.setHeader(headerKey, headerValue);
                outStream = response.getOutputStream();
                byte[] buffer = new byte[4096 * 4096];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }
            outStream.close();
        } else {
            redirect(request, response, request.getContextPath() + "/404.jsp");
        }

    }

    protected void uploadRegistroComplessivo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject resp = new JsonObject();
        User us = (User) request.getSession().getAttribute("user");
        Part p = request.getPart("file");
        Entity e = new Entity();
        try {
            e.begin();
            String qrcrop = e.getPath("qr_crop");
            ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class,
                    Long.parseLong(request.getParameter("id")));
            DocumentiPrg registroComplessivoPresente = pf.getDocumenti().stream().filter(dc -> dc.getDeleted() == 0 && dc.getTipo().getId() == 30L).findFirst().orElse(null);

            boolean registroOK = false;
            String erroreregistroOK = "REGISTRO COMPLESSIVO ERRATO. CONTROLLARE.";

            if (registroComplessivoPresente != null) {
                File dir = new File(registroComplessivoPresente.getPath());
                p.write(dir.getAbsolutePath());

                String res = checkFirmaQRpdfA("REGISTRO COMPLESSIVO", String.valueOf(pf.getId()),
                        dir, us.getSoggettoAttuatore().getCodicefiscale(), qrcrop);
                if (!res.equals("OK")) {
                    registroOK = false;
                    erroreregistroOK = res;
                } else {
                    registroOK = true;
                }

            } else {
                TipoDoc complessivo = e.getEm().find(TipoDoc.class,
                        30L);

                String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());
                String path = e.getPath("pathDocSA_Prg").replace("@rssa", us.getSoggettoAttuatore().getId().toString()).replace("@folder", pf.getId().toString());

                File dir = new File(path);
                createDir(path);
                Part part = request.getPart("file");
                String ext = part.getSubmittedFileName().substring(part.getSubmittedFileName().lastIndexOf("."));

                path += complessivo.getDescrizione() + "_" + today + ext;
                part.write(dir.getAbsolutePath() + File.separator + complessivo.getDescrizione() + "_" + today + ext);

                File filedest = new File(dir.getAbsolutePath() + File.separator + complessivo.getDescrizione() + "_" + today + ext);

                String res = checkFirmaQRpdfA("REGISTRO COMPLESSIVO", String.valueOf(pf.getId()),
                        filedest, us.getSoggettoAttuatore().getCodicefiscale(), qrcrop);
                if (!res.equals("OK")) {
                    registroOK = false;
                    erroreregistroOK = res;
                } else {
                    registroOK = true;
                }

                DocumentiPrg comp = new DocumentiPrg();
                comp.setPath(path.replace("\\", "/"));
                comp.setTipo(complessivo);
                comp.setProgetto(pf);
                e.persist(comp);

                List<DocumentiPrg> docs = pf.getDocumenti();
                docs.add(comp);
                pf.setDocumenti(docs);
            }

            if (registroOK) {
                e.merge(pf);
                e.flush();
                e.commit();
                resp.addProperty("result", true);
            } else {
                e.rollBack();
                resp.addProperty("result", false);
                resp.addProperty("message", erroreregistroOK);
            }

        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA uploadRegistroComplessivo: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile caricare il registro complessivo.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void uploadDichiarazioneM6(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();

        Entity e = new Entity();
        e.begin();

        try {
            ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("pf")));
            boolean m6_exist = true;
            int step3_scelta = Integer.parseInt(request.getParameter("scelta_step3"));
            ModelliPrg mod6 = pf.getModelli().stream().filter(s -> s.getModello() == 6).findFirst().orElse(null);
            if (mod6 == null) {
                m6_exist = false;
                mod6 = new ModelliPrg();
                mod6.setModello(6);
                mod6.setData_modifica(new Date());
                mod6.setProgetto(pf);
            }
            mod6.setScelta_modello6(step3_scelta);
            if (step3_scelta == 2) {
                mod6.setIndirizzo_modello6(new String(request.getParameter("indirizzo_step3").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                mod6.setCivico_modello6(request.getParameter("civico_step3") != null ? request.getParameter("civico_step3") : "");
                mod6.setComune_modello6(e.getComune(Long.parseLong(request.getParameter("comune_step3"))));
            } else {
                mod6.setIndirizzo_modello6("");
                mod6.setCivico_modello6("");
                mod6.setComune_modello6(null);
            }
            if (m6_exist) {
                e.merge(mod6);
            } else {
                e.persist(mod6);
            }
            e.merge(pf);
            e.flush();
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA uploadDichiarazioneM6: " + ex.getMessage());
            resp.addProperty("result", false);
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();

    }

    protected void abilitaModificaCalendarM3(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            e.begin();
            ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class,
                    Long.parseLong(request.getParameter("pf")));
            ModelliPrg m3 = e.getEm().find(ModelliPrg.class,
                    Long.parseLong(request.getParameter("modello")));
            StatiPrg SOA = e.getEm().find(StatiPrg.class,
                    "SOA");
            pf.setStato(SOA);
            m3.setStato("R");

            e.merge(pf);
            e.commit();

            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA abilitaModificaCalendarM3: " + ex.getMessage());
            resp.addProperty("result", false);
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void abilitaModificaCalendarM4(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            e.begin();
            ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class,
                    Long.parseLong(request.getParameter("pf")));
            ModelliPrg m4 = e.getEm().find(ModelliPrg.class,
                    Long.parseLong(request.getParameter("modello")));
            StatiPrg SOA = e.getEm().find(StatiPrg.class,
                    "SOB");
            pf.setStato(SOA);
            m4.setStato("R");

            e.merge(pf);
            e.commit();

            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA abilitaModificaCalendarM4: " + ex.getMessage());
            resp.addProperty("result", false);
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void deleteAllLessons(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Date today = new Date();
        DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();

        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            e.begin();
            String tipo = request.getParameter("tipo");
            ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class,
                    Long.parseLong(request.getParameter("pf")));
            ModelliPrg m = e.getEm().find(ModelliPrg.class,
                    Long.parseLong(request.getParameter("modello")));
            String track = "deleteAllLessons. Cancellazione lezioni del PF (" + pf.getId() + " - " + tipo + " " + m.getId() + "): ";

            for (Lezioni_Modelli l : m.getLezioni()) {
                if (dateTimeComparator.compare(l.getGiorno(), today) > -1) {
                    track += l.getId() + " ";
                    l.setModello(null);
                }
            }
            track += "SET NULL";
            if (tipo.equalsIgnoreCase("M4")) {
                m.setStato("L");
            } else {
                m.setStato("S");
            }
            e.merge(pf);

            e.persist(new Tracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), track));

            e.commit();

            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA deleteAllLessons: " + ex.getMessage());
            resp.addProperty("result", false);
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void deleteByLesson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();

        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            e.begin();
            ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class,
                    Long.parseLong(request.getParameter("pf")));
            ModelliPrg m3 = e.getEm().find(ModelliPrg.class,
                    Long.parseLong(request.getParameter("modello")));
            Date cancellaDa = e.getEm().find(Lezioni_Modelli.class,
                    Long.parseLong(request.getParameter("idlezione"))).getGiorno();
            String track = "deleteByLesson. Cancellazione lezioni del PF (" + pf.getId() + " - M3 " + m3.getId() + "): ";
            for (Lezioni_Modelli l : m3.getLezioni()) {
                if (dateTimeComparator.compare(l.getGiorno(), cancellaDa) > -1) {
                    track += l.getId() + " ";
                    l.setModello(null);
                }
            }
            track += "SET NULL";
            m3.setStato("S");
            e.merge(pf);

            e.persist(new Tracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), track));

            e.commit();

            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA abilitaModificaCalendarM3: " + ex.getMessage());
            resp.addProperty("result", false);
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void deleteByGroup(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();

        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            e.begin();
            String tipo = request.getParameter("tipo");
            int gruppo = Integer.parseInt(request.getParameter("gruppo"));
            ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class,
                    Long.parseLong(request.getParameter("pf")));
            ModelliPrg m4 = e.getEm().find(ModelliPrg.class,
                    Long.parseLong(request.getParameter("modello")));
            String track = "deleteByGroup. Cancellazione lezioni del PF (" + pf.getId() + " - M4 " + m4.getId() + "): ";
            List<Lezioni_Modelli> lezioni_gruppo = m4.getLezioni().stream().filter(l -> l.getGruppo_faseB() == gruppo).collect(Collectors.toList());
            Date cancellaDa = new Date();
            if (tipo.equalsIgnoreCase("Forward")) {
                cancellaDa = e.getEm().find(Lezioni_Modelli.class,
                        Long.parseLong(request.getParameter("idlezione"))).getGiorno();
            }
            for (Lezioni_Modelli l : lezioni_gruppo) {
                if (dateTimeComparator.compare(l.getGiorno(), cancellaDa) > -1) {
                    track += l.getId() + " ";
                    l.setModello(null);
                }
            }
            track += "SET NULL";
            m4.setStato("L");
            e.merge(pf);

            e.persist(new Tracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), track));

            e.commit();

            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA abilitaModificaCalendarM3: " + ex.getMessage());
            resp.addProperty("result", false);
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void scaricaModello1(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        e.begin();
        Allievi a = e.getEm().find(Allievi.class, Long.parseLong(getRequestValue(request, "id")));
        boolean domiciliouguale = a.getIndirizzodomicilio().equalsIgnoreCase(a.getIndirizzoresidenza())
                && a.getCivicodomicilio().equalsIgnoreCase(a.getCivicoresidenza())
                && a.getComune_domicilio().getId() == a.getComune_residenza().getId();
        File downloadFile = null;
        User us = (User) request.getSession().getAttribute("user");
        downloadFile = Pdf_new.MODELLO1(e, "3", us.getUsername(), us.getSoggettoAttuatore(), a, new DateTime(), domiciliouguale, true);

        e.close();
        if (downloadFile != null && downloadFile.exists()) {
            OutputStream outStream;
            try (FileInputStream inStream = new FileInputStream(downloadFile)) {
                String mimeType = probeContentType(downloadFile.toPath());
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
                response.setContentType(mimeType);
                String headerKey = "Content-Disposition";
                String headerValue = format("attachment; filename=\"%s\"", downloadFile.getName());
                response.setHeader(headerKey, headerValue);
                outStream = response.getOutputStream();
                byte[] buffer = new byte[4096 * 4096];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }
            outStream.close();
        } else {
            redirect(request, response, request.getContextPath() + "/404.jsp");
        }
    }

    protected void uploadModello1(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        e.begin();

        String qrcrop = e.getPath("qr_crop");

        JsonObject resp = new JsonObject();
        User us = (User) request.getSession().getAttribute("user");

        String path = e.getPath("pathDocSA_Allievi")
                .replace("@rssa", Utility.correctName(us.getSoggettoAttuatore().getId() + ""))
                .replace("@folder", Utility.correctName(request.getParameter("codicefiscale")));
        File dir = new File(path);
        createDir(path);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
        String today = sdf2.format(new Date());

        Allievi a = e.getEm().find(Allievi.class, Long.parseLong(getRequestValue(request, "id")));

        boolean modello1OK = false;
        String erroremodello1OK = "MODELLO 1 ERRATO. CONTROLLARE.";
        try {
            TipoDoc_Allievi modello1 = e.getEm().find(TipoDoc_Allievi.class, 3L);
            Part p = request.getPart("file");
            String ext = p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
            String destpath = dir.getAbsolutePath() + File.separator + modello1.getDescrizione() + today + "_" + a.getCodicefiscale() + ext;
            p.write(destpath);
            File pdfdest = new File(destpath);
            String res = checkFirmaQRpdfA("MODELLO1", us.getUsername(), pdfdest, us.getSoggettoAttuatore().getCodicefiscale(), qrcrop);
            if (res.equals("OK")) {
                Documenti_Allievi doc = new Documenti_Allievi();
                doc.setPath(destpath);
                doc.setTipo(modello1);
                doc.setAllievo(a);
                e.persist(doc);
                modello1OK = true;
            } else {
                erroremodello1OK = res;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            erroremodello1OK = "MODELLO 1 ERRATO. " + ex.getMessage() + ". CONTROLLARE.";
            e.insertTracking(us.getUsername(), "uploadModello1: " + erroremodello1OK + " (allievo " + a.getId() + ")");
            resp.addProperty("result", false);
            resp.addProperty("message", modello1OK);
        }

        if (modello1OK) {
            a.setStato("A");
            e.merge(a);
            e.flush();
            e.commit();
            resp.addProperty("result", true);
        } else {
            e.insertTracking(us.getUsername(), "uploadModello1: " + erroremodello1OK + " (allievo " + a.getId() + ")");
            resp.addProperty("result", false);
            resp.addProperty("message", erroremodello1OK);
        }

        e.close();

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User us = (User) request.getSession().getAttribute("user");
        if (us != null && (us.getTipo() == 1 || us.getTipo() == 3)) {
            String type = request.getParameter("type");
            response.setContentType("text/html;charset=UTF-8");
            switch (type) {
                case "modifyEmail":
                    modifyEmail(request, response);
                    break;
                case "downloadregistro":
                    downloadregistro(request, response);
                    break;
                case "salvamodello3":
                    salvamodello3(request, response);
                    break;
                case "scaricamodello3":
                    scaricamodello3(request, response);
                    break;
                case "salvamodello4":
                    salvamodello4(request, response);
                    break;
                case "scaricamodello4":
                    scaricamodello4(request, response);
                    break;
                case "updtProfile":
                    updtProfile(request, response);
                    break;
                case "checkCF":
                    checkCF(request, response);
                    break;
                case "newAllievo":
                    newAllievo(request, response);
                    break;
                case "updtCartaID":
                    updtCartaID(request, response);
                    break;
                case "updtAllievo":
                    updtAllievo(request, response);
                    break;
                case "upAllievoM1":
                    upAllievoM1(request, response);
                    break;
                case "uploadDocIdDocente":
                    uploadDocIdDocente(request, response);
                    break;
                case "uploadCurriculumDocente":
                    uploadCurriculumDocente(request, response);
                    break;
                case "newProgettoFormativo":
                    newProgettoFormativo(request, response);
                    break;
                case "modifyDoc":
                    modifyDoc(request, response);
                    break;
                case "uploadDocPrg":
                    uploadDocPrg(request, response);
                    break;
                case "modifyPrg":
                    modifyPrg(request, response);
                    break;
                case "modifyDocenti":
                    modifyDocenti(request, response);
                    break;
                case "goNext":
                    goNext(request, response);
                    break;
                case "setEsitoAllievo":
                    setEsitoAllievo(request, response);
                    break;
                case "uploadRegistro":
                    uploadRegistro(request, response);
                    break;
                case "modifyRegistro":
                    modifyRegistro(request, response);
                    break;
                case "getTotalHoursRegistriByAllievo":
                    getTotalHoursRegistriByAllievo(request, response);
                    break;
                case "uploadDocPrg_FaseB":
                    uploadDocPrg_FaseB(request, response);
                    break;
                case "modifyDocPrg_FaseB":
                    modifyDocPrg_FaseB(request, response);
                    break;
                case "uploadRegistrioAula":
                    uploadRegistrioAula(request, response);
                    break;
                case "setSIGMA":
                    setSIGMA(request, response);
                    break;
                case "modifyRegistrioAula":
                    modifyRegistrioAula(request, response);
                    break;
                case "checkEmail":
                    checkEmail(request, response);
                    break;
                case "updtCartaIDAD":
                    updtCartaIDAD(request, response);
                    break;
                case "getCodiceCatastaleComune":
                    getCodiceCatastaleComune(request, response);
                    break;
                case "deleteRegister":
                    deleteRegister(request, response);
                    break;
                case "sendAsk":
                    sendAsk(request, response);
                    break;
                case "uploadLezione":
                    uploadLezione(request, response);
                    break;
                case "updateLezione":
                    updateLezione(request, response);
                    break;
                case "creaGruppi":
                    creaGruppi(request, response);
                    break;
                case "manageM2":
                    manageM2(request, response);
                    break;
                case "addDocente":
                    addDocente(request, response);
                    break;
                case "checkEmail_Docente":
                    checkEmail_Docente(request, response);
                    break;
                case "checkCF_Docente":
                    checkCF_Docente(request, response);
                    break;
                case "manageMembriStaff":
                    manageMembriStaff(request, response);
                    break;
                case "deleteMembroStaff":
                    deleteMembroStaff(request, response);
                    break;
                case "rendicontaAllievo":
                    rendicontaAllievo(request, response);
                    break;
                case "concludiPrg":
                    concludiPrg(request, response);
                    break;
                case "deleteModello5Alunno":
                    deleteModello5Alunno(request, response);
                    break;
                case "uploadM5Alunno":
                    uploadM5Alunno(request, response);
                    break;
                case "scaricaModello5":
                    scaricaModello5(request, response);
                    break;
                case "scaricaregistrotemp":
                    scaricaregistrotemp(request, response);
                    break;
                case "uploadRegistroComplessivo":
                    uploadRegistroComplessivo(request, response);
                    break;
                case "scaricamodello6temp":
                    scaricamodello6temp(request, response);
                    break;
                case "uploadDichiarazioneM6":
                    uploadDichiarazioneM6(request, response);
                    break;
                case "scaricaModello7":
                    scaricaModello7(request, response);
                    break;
                case "abilitaModificaCalendarM3":
                    abilitaModificaCalendarM3(request, response);
                    break;
                case "abilitaModificaCalendarM4":
                    abilitaModificaCalendarM4(request, response);
                    break;
                case "deleteAllLessons":
                    deleteAllLessons(request, response);
                    break;
                case "deleteByLesson":
                    deleteByLesson(request, response);
                    break;
                case "deleteByGroup":
                    deleteByGroup(request, response);
                    break;
                case "scaricaModello1":
                    scaricaModello1(request, response);
                    break;
                case "uploadModello1":
                    uploadModello1(request, response);
                    break;
                default:
                    break;

            }
        }
    }

    private class checkResult {

        boolean result;
        String message = "";

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    private double getHour(Date in, Date out) {
        return ((double) (out.getTime() - in.getTime()) / 3600000);
    }

    private boolean checkFaseAllievi(List<Allievi> allievi) {
        for (Allievi a : allievi) {
            if (a.getEsito().equals("Fase B")) {
                return true;
            }
        }
        return false;
    }

    private checkResult checkScadenze(ProgettiFormativi prg) {
        checkResult out = new checkResult();
        Date today = new Date();
        out.result = true;
        prg.getDocumenti().stream().filter(d -> d.getScadenza() != null && d.getDeleted() == 0).forEach(d -> {
            if (d.getScadenza().before(today)) {
                out.result = false;
                out.setMessage(out.getMessage() + d.getDocente().getCognome() + ", ");
            }
        });

        if (!out.getMessage().equals("")) {
            out.message = "Docenti col documento scaduto:<br>" + out.message.substring(0, out.getMessage().lastIndexOf(","));
        }
        String allievi = "";
        for (Allievi a : prg.getAllievi()) {
            if (a.getScadenzadocid().before(today)) {
                out.result = false;
                allievi += a.getCognome() + ", ";
            }
        }

        if (!allievi.equals("")) {
            out.message = out.message + "<br><br>Allievi col documento scaduto:<br>" + allievi.substring(0, allievi.lastIndexOf(","));
        }

        return out;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
