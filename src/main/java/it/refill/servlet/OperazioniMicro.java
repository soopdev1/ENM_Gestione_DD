/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.gson.JsonObject;
import it.refill.db.Action;
import it.refill.db.Database;
import it.refill.db.Entity;
import it.refill.db.FileDownload;
import it.refill.domain.Allievi;
import it.refill.domain.Allievi_Pregresso;
import it.refill.domain.Attivita;
import it.refill.domain.Cloud;
import it.refill.domain.Comuni;
import it.refill.domain.CpiUser;
import it.refill.domain.Docenti;
import it.refill.domain.DocumentiPrg;
import it.refill.domain.Documenti_Allievi;
import it.refill.domain.Documenti_Allievi_Pregresso;
import it.refill.domain.Documenti_UnitaDidattiche;
import it.refill.domain.Email;
import it.refill.domain.Estrazioni;
import it.refill.domain.FadMicro;
import it.refill.domain.Faq;
import it.refill.domain.FasceDocenti;
import it.refill.domain.ProgettiFormativi;
import it.refill.domain.Revisori;
import it.refill.domain.SediFormazione;
import it.refill.domain.SoggettiAttuatori;
import it.refill.domain.StatiPrg;
import it.refill.domain.StatoPartecipazione;
import it.refill.domain.Storico_ModificheInfo;
import it.refill.domain.Storico_Prg;
import it.refill.domain.TipoDoc;
import it.refill.domain.TipoDoc_Allievi_Pregresso;
import it.refill.domain.TipoFaq;
import it.refill.domain.UnitaDidattiche;
import it.refill.domain.User;
import it.refill.domain.checklist_finale;
import it.refill.entity.Presenti;
import it.refill.util.CompilePdf;
import it.refill.util.Pdf_new;
import static it.refill.util.Pdf_new.checkFirmaQRpdfA;
import it.refill.util.SendMailJet;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import it.refill.util.Utility;
import static it.refill.util.Utility.checkPDF;
import static it.refill.util.Utility.createDir;
import static it.refill.util.Utility.getRequestValue;
import static it.refill.util.Utility.getstatoannullato;
import static it.refill.util.Utility.patternITA;
import static it.refill.util.Utility.patternSql;
import static it.refill.util.Utility.redirect;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import static java.lang.String.format;
import java.nio.file.Files;
import static java.nio.file.Files.probeContentType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import static org.apache.commons.codec.binary.Base64.decodeBase64;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.remove;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import org.apache.commons.validator.routines.EmailValidator;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author rcosco
 */
public class OperazioniMicro extends HttpServlet {

    protected void setProtocollo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            e.begin();

            SoggettiAttuatori sa = e.getEm().find(SoggettiAttuatori.class, Long.parseLong(request.getParameter("id")));
            sa.setProtocollo(request.getParameter("protocollo"));
            sa.setDataprotocollo(new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("data")));

            User us = e.getUserbySA(sa);
            if (us.getTipo() != 1) {
                if (e.updateuserTipo(1, sa)) {
                    Email email = (Email) e.getEmail("abilitate");
                    String email_txt = email.getTesto()
                            .replace("@email_tec", e.getPath("emailtecnico"))
                            .replace("@email_am", e.getPath("emailamministrativo"));
                    SendMailJet.sendMail(e.getPath("mailsender"), new String[]{sa.getEmail()}, email_txt, email.getOggetto());
//                    resp.addProperty("result", true);
                }
            }
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro setProtocollo: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile inserire il protocollo.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void addDocente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            e.begin();
            String nome = request.getParameter("nome");
            String cognome = request.getParameter("cognome");
            String cf = request.getParameter("cf");
            String email = request.getParameter("email");
            Date data_nascita = null;
            if (!request.getParameter("data").equals("")) {
                data_nascita = new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("data"));
            }
            Docenti d = new Docenti(nome, cognome, cf, data_nascita, email);
            d.setFascia(e.getEm().find(FasceDocenti.class, request.getParameter("fascia")));
            e.persist(d);
            e.commit();

            resp.addProperty("result", true);
        } catch (PersistenceException | ParseException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro addDocente: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile aggiungere il docente.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void addDocenteFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        JsonObject resp = new JsonObject();
//        Part p = request.getPart("file");
//        boolean out = false;
//        Entity e = new Entity();
//        User us = (User) request.getSession().getAttribute("user");
//        try {
//            if (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0) {
//                out = ImportExcel.importSedi(p.getInputStream(), us.getId().toString());
//            }
//            resp.addProperty("result", out);
//            if (out) {
//                resp.addProperty("message", "Errore: non &egrave; stato possibile caricare i docenti.");
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            e.insertTracking(String.valueOf(us.getId()), "OperazioniMicro addDocenteFile: " + ex.getMessage());
//            resp.addProperty("result", false);
//            resp.addProperty("message", "Errore: non &egrave; stato possibile caricare i docenti.");
//        } finally {
//            e.close();
//        }
//
//        response.getWriter().write(resp.toString());
//        response.getWriter().flush();
//        response.getWriter().close();
    }

    protected void addAuleFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject resp = new JsonObject();

        resp.addProperty("result", false);
        resp.addProperty("message", "Errore: non &egrave; stato possibile aggiungere le aule.");

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void addAula(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            e.begin();

            String denominazione = request.getParameter("denom");
            String via = request.getParameter("via");
            String referente = request.getParameter("referente");
            String telefono = request.getParameter("phone").equals("") ? null : request.getParameter("phone");
            String cellulare = request.getParameter("cellulare").equals("") ? null : request.getParameter("cellulare");
            String email = request.getParameter("email").equals("") ? null : request.getParameter("email");

            Comuni c = e.getEm().find(Comuni.class, Long.parseLong(request.getParameter("comune")));
            SediFormazione s = new SediFormazione(denominazione, via, referente, telefono, cellulare, email, c);

            e.persist(s);
            e.commit();

            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro addAula: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile aggiungere l'aula.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void validateAula(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        String status = getRequestValue(request, "status");
        try {
            e.begin();

            SediFormazione p = e.getEm().find(SediFormazione.class, Long.parseLong(getRequestValue(request, "id")));
            if (status.equals("OK")) {
                p.setStato("A");
            } else if (status.equals("OK1")) {
                p.setStato("A1");
            } else if (status.equals("KO")) {
                p.setStato("R");
            } else {
                p.setStato("DV");
            }
            e.merge(p);
            e.commit();
            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()),
                    "OperazioniMicro validateAula: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile effettuare l'operazione scelta sulla sede di formazione.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();

    }

    protected void validatePrg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        Utility.printRequest(request);
//
//        if (true) {
//            return;
//        }
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        boolean check = true;
        int min_allievi;
        boolean rigettaPrg = false;
        String rigettaPrg_msg = null;
        String fineFa = "";
        try {
            String stato_prec, stato_succ;
            e.begin();
            min_allievi = Integer.parseInt(e.getPath("min_allievi"));
            ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class, Long.parseLong(getRequestValue(request, "id")));
            stato_prec = p.getStato().getId();
            if (p.getStato().getId().equals("DC")) {
                p.setCip(getRequestValue(request, "cip"));
                //start fase a - 
                //14 05 21 Escludere dalla FASE B gli alunni con ore rendicontate <= 36 durante la FASE A
                Long hh36 = new Long(129600000);
                Map<Long, Long> oreRendicontateFaseA = Action.OreRendicontabiliAlunni_faseA(p.getId().intValue());
                for (Allievi a : p.getAllievi()) {
                    if (oreRendicontateFaseA.get(a.getId()) != null && oreRendicontateFaseA.get(a.getId()).compareTo(hh36) < 0) {
                        a.setGruppo_faseB(-1);
                        e.merge(a);
                    }
                }
                e.merge(p);
            } else if (p.getStato().getId().equalsIgnoreCase("DV")) {
                p.setModello2_check(0);
                String neets = getRequestValue(request, "neets");
                if (!neets.equalsIgnoreCase("[]") && !neets.equalsIgnoreCase("")) {
                    Allievi temp;
                    JSONArray jsonarray = new JSONArray(neets);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        temp = e.getEm().find(Allievi.class, Long.parseLong(jsonobject.getString("neet")));
                        temp.setEsclusione_prg(jsonobject.getString("motivo"));
                        temp.setStatopartecipazione((StatoPartecipazione) e.getEm().find(StatoPartecipazione.class, "03"));
                        e.merge(temp);
                    }
                }
                p.getAllievi().stream().filter(al -> al.getStatopartecipazione().getId().equalsIgnoreCase("01")).collect(Collectors.toList()).forEach(a -> a.setEsclusione_prg("APPROVATO"));
                e.merge(p);

                rigettaPrg = (int) (long) p.getAllievi().stream().filter(a -> a.getStatopartecipazione().getId().equalsIgnoreCase("01")).count() < min_allievi;
            }
            if (!rigettaPrg) {
                p.setStato(e.getStatiByOrdineProcesso(p.getStato().getOrdine_processo() + 1));
            } else {
                p.setStato(e.getEm().find(StatiPrg.class, p.getStato().getId().replace("1", "") + "E"));
                e.persist(new Storico_Prg("Rigettato automaticamente: numero minimo di allievi (" + min_allievi + ") non raggiunto",
                        new Date(), p, p.getStato()));//storico progetto
                rigettaPrg_msg = "Numero minimo di allievi (" + min_allievi + ") per avviare il Progetto Formativo non raggiunto";
            }

//            if (p.getStato().getId().equals("FB1")) {//check registri songoli e aula
//                if (!checkValidateRegisterAllievo(e.getRegistriAllievi(e.getAllieviProgettiFormativi(p))) || !checkValidateRegister(e.getregisterPrg(p))) {
//                    check = false;
//                    resp.addProperty("message", "Ci sono ancora registri allievi o d'aula da controllare, il progetto non può essere validato.");
//                } else {
//                    p.setStato(e.getStatiByOrdineProcesso(p.getStato().getOrdine_processo() + 1));//STATO fase successiva
//                }
//            } else {
//                p.setStato(e.getStatiByOrdineProcesso(p.getStato().getOrdine_processo() + 1));//STATO fase successiva
//            }
            if (check) {
                if (!rigettaPrg) {
                    e.persist(new Storico_Prg((p.getStato().getId().equals("AR") ? "Archiviato" : "Convalidato") + fineFa, new Date(), p, p.getStato()));//storico progetto
                }
                p.setControllable(0);
                p.setArchiviabile(0);
                p.setMotivo(null);
                e.merge(p);
                e.commit();
            }

//            if (p.getStato().getId().equals("C")) {
//                ExportExcel.compileTabella1(p.getId());
//            }
            stato_succ = p.getStato().getId();
            if (Utility.invioEmailComunicazione(stato_prec, stato_succ)) {
                //Invio Mail convalida modello 2 / modello 3 / modello 4 
                Email email_txt = (Email) e.getEmail("comunicazione_pf");
                SendMailJet.sendMail(e.getPath("mailsender"), new String[]{p.getSoggetto().getEmail()}, email_txt.getTesto().replace("@SA", p.getSoggetto().getRagionesociale())
                        .replace("@email_tec", e.getPath("emailtecnico"))
                        .replace("@email_am", e.getPath("emailamministrativo")), email_txt.getOggetto());
            }
            //RAF 29/06 mail di istruzioni sa
            if (stato_prec.equals("DC") && stato_succ.equals("ATA")) {
                Email email_txt = e.getEmail("sa_start");
                SendMailJet.sendMail(
                        e.getPath("mailsender"),
                        new String[]{p.getSoggetto().getEmail()},
                        email_txt.getTesto(),
                        email_txt.getOggetto());
            }

            resp.addProperty("result", check);
            resp.addProperty("message", rigettaPrg_msg);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro validatePrg: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile validare il progetto formativo.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    
    protected void annullaPrg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            e.begin();
            ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("id")));
            String stato_prec = p.getStato().getId();
            p.setMotivo(request.getParameter("motivo"));
            e.persist(new Storico_Prg("Rigettato: " + request.getParameter("motivo"), new Date(), p, p.getStato()));//storico progetto
            String stato_succ = getstatoannullato(stato_prec);
            p.setStato(e.getEm().find(StatiPrg.class, stato_succ));
            p.getAllievi().forEach(al1 -> {
                if (al1.getStatopartecipazione().getId().equals("01")) {
                    al1.setStatopartecipazione(e.getEm().find(StatoPartecipazione.class, "03"));
                    e.merge(al1);
                }
            });
            e.merge(p);
            e.commit();
            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro annullaPrg: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile annullare il progetto formativo.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }
    
    protected void rejectPrg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            String stato_prec, stato_succ;
            e.begin();
            ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("id")));
            stato_prec = p.getStato().getId();
            p.setMotivo(request.getParameter("motivo"));
            e.persist(new Storico_Prg("Rigettato: " + request.getParameter("motivo"), new Date(), p, p.getStato()));//storico progetto
            p.setStato(e.getEm().find(StatiPrg.class, p.getStato().getId().replace("1", "") + "E"));
            e.merge(p);
            e.commit();
            stato_succ = p.getStato().getId();

            if (Utility.invioEmailComunicazione(stato_prec, stato_succ)) {
                Email email_txt = (Email) e.getEmail("comunicazione_pf");
                SendMailJet.sendMail(e.getPath("mailsender"), new String[]{p.getSoggetto().getEmail()}, email_txt.getTesto().replace("@SA", p.getSoggetto().getRagionesociale())
                        .replace("@email_tec", e.getPath("emailtecnico"))
                        .replace("@email_am", e.getPath("emailamministrativo")), email_txt.getOggetto());
            }

            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro rejectPrg: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile segnalare il progetto formativo.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void validateHourRegistroAula(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            e.begin();
            String[] hhmm = request.getParameter("ore_conv").split(":");
            double ore_ric = Double.parseDouble(hhmm[0]) + (Double.parseDouble(hhmm[1]) / 60);
            DocumentiPrg doc = e.getEm().find(DocumentiPrg.class, Long.parseLong(request.getParameter("id")));
            doc.setOre_convalidate(ore_ric);
            doc.setValidate(1);
            List<Presenti> presenti = doc.getPresenti_list();

            for (Presenti p : presenti) {
                hhmm = request.getParameter("ore_riconsciute_" + p.getId()).split(":");
                ore_ric = Double.parseDouble(hhmm[0]) + (Double.parseDouble(hhmm[1]) / 60);
                p.setOre_riconosciute(ore_ric);
            }
            ObjectMapper mapper = new ObjectMapper();
            doc.setPresenti(mapper.writeValueAsString(presenti));
            e.merge(doc);
            e.commit();
            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro validateHourRegistroAula: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile validare le ore del registro");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private boolean checkFaseAllievi(List<Allievi> allievi) {
        for (Allievi a : allievi) {
            if (a.getEsito().equals("Fase B")) {
                return true;
            }
        }
        return false;
    }

    protected void setHoursRegistro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            String[] hhmm = request.getParameter("orericonosciute").split(":");
            double ore_ric = Double.parseDouble(hhmm[0]) + (Double.parseDouble(hhmm[1]) / 60);
            e.begin();
            Documenti_Allievi doc = e.getEm().find(Documenti_Allievi.class, Long.parseLong(request.getParameter("id")));
            doc.setOrericonosciute(ore_ric);
            e.merge(doc);
            e.commit();
            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro setHoursRegistro: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile validare le ore del registro");
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
            DocumentiPrg d = e.getEm().find(DocumentiPrg.class, Long.parseLong(request.getParameter("id")));
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

    protected void addCloud(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject resp = new JsonObject();
        Part p = request.getPart("file");
        int tipo = Integer.parseInt(request.getParameter("tf"));
        Entity e = new Entity();
        try {
            e.begin();
            if (p != null
                    && p.getSubmittedFileName() != null
                    && p.getSubmittedFileName().length() > 0) {
                String path = e.getPath("output_excel_archive");
                File dir = new File(path);
                createDir(path);
                String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());
                String destination_name = StringUtils.replace(p.getSubmittedFileName(), " ", "_");
                String nameToShow = new String(destination_name.getBytes(Charsets.ISO_8859_1), Charsets.UTF_8);
                String ext = destination_name.substring(destination_name.lastIndexOf("."));
//                destination_name = destination_name.substring(0, destination_name.lastIndexOf(".")) + "_" + today + ext;
                destination_name = tipo == 1 ? "materialeDidatticoDD_" + today + ext : "modelliFacSimileDD_" + today + ext;
                String file_path = dir.getAbsolutePath() + File.separator + destination_name;
                p.write(file_path);

                Cloud cl = new Cloud();
                cl.setAttivo(1);
                cl.setVisible("1-2");
                cl.setNome(nameToShow);
                cl.setPath(file_path);
                cl.setTipo(tipo);
                e.persist(cl);

                e.commit();
                resp.addProperty("result", true);
            } else {
                resp.addProperty("result", false);
                resp.addProperty("message", "Errore: non &egrave; stato possibile caricare il documento.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile caricare il documento.");
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
            ProgettiFormativi prg = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("idprogetto")));
            TipoDoc tipo = e.getEm().find(TipoDoc.class, Long.parseLong(request.getParameter("id_tipo")));
            List<TipoDoc> tipo_obb = e.getTipoDocObbl(prg.getStato());

            tipo_obb.remove(tipo);
//            List<DocumentiPrg> documenti = prg.getDocumenti();
            for (DocumentiPrg d : prg.getDocumenti()) {
                tipo_obb.remove(d.getTipo());
            }

            e.begin();

            //creao il path
            String path = e.getPath("pathDocSA_Prg").replace("@rssa", prg.getSoggetto().getId().toString()).replace("@folder", prg.getId().toString());
            File dir = new File(path);
            createDir(path);

            String file_path;
            String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());

            //scrivo il file su disco
            if (p != null
                    && p.getSubmittedFileName() != null
                    && p.getSubmittedFileName().length() > 0) {
                file_path = dir.getAbsolutePath() + File.separator + tipo.getDescrizione() + "_" + today + p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                p.write(file_path);
                DocumentiPrg doc = new DocumentiPrg();
                doc.setPath(file_path);
                doc.setTipo(tipo);
                doc.setProgetto(prg);
                e.persist(doc);
                if (tipo.getId() == 25) {//se sta caricando la check2
                    CompilePdf.compileValutazione(prg);
                }
            }
            //se caricato tutti i doc obbligatori setto il progetto come idoneo per la prossima fase
            if (tipo_obb.isEmpty()) {
                prg.setArchiviabile(1);
                e.merge(prg);
                resp.addProperty("message", "Hai caricato tutti i documenti necessari per questa fase. Ora il progetto può essere archiviato.");
            } else {
                resp.addProperty("message", "");
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

    protected void compileCL2(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        Check2 cl2 = new Check2();
//        Gestione g = new Gestione();
//        Fascicolo f = new Fascicolo();
//
//        Entity e = new Entity();
//        JsonObject resp = new JsonObject();
//        try {
//            e.begin();
//            //29-04-2020 MODIFICA - TOGLIERE IMPORTO CHECKLIST
////            String prezzo = request.getParameter("kt_inputmask_7").substring(request.getParameter("kt_inputmask_7").lastIndexOf("_"));
//            ProgettiFormativi prg = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("idprogetto")));
////            prg.setImporto(Double.valueOf(prezzo.replaceAll("[._]", "").replace(",", ".").trim()));
////            e.merge(prg);
////            e.commit();
//
//            //14-10-2020 MODIFICA - TOGLIERE IMPORTO CHECKLIST
//            double ore_convalidate = 0;
//            for (DocumentiPrg d : prg.getDocumenti().stream().filter(p -> p.getGiorno() != null && p.getDeleted() == 0).collect(Collectors.toList())) {
//                ore_convalidate += d.getOre_convalidate();
//            }
//            for (Allievi a : prg.getAllievi()) {
//                for (Documenti_Allievi d : a.getDocumenti().stream().filter(p -> p.getGiorno() != null && p.getDeleted() == 0).collect(Collectors.toList())) {
//                    ore_convalidate += d.getOrericonosciute() == null ? 0 : d.getOrericonosciute();
//                }
//            }
//            double prezzo_ore = Double.parseDouble(e.getPath("euro_ore"));
//            prg.setImporto(ore_convalidate * prezzo_ore);
//            e.merge(prg);
//            e.commit();
//
//            cl2.setAllievi_tot(Integer.valueOf(request.getParameter("allievi_start")));
//            cl2.setAllievi_ended(Integer.valueOf(request.getParameter("allievi_end")));
//            cl2.setProgetto(prg);
//            cl2.setNumero_min(ctrlCheckbox(request.getParameter("check_valid")));
//            g.setSwat(ctrlCheckbox(request.getParameter("check_swot")));
//            g.setM9(ctrlCheckbox(request.getParameter("check_m9_1")));
//            g.setConseganto(request.getParameter("m9_input"));
//            g.setCv(ctrlCheckbox(request.getParameter("check_cvdoc")));
//            g.setM13(ctrlCheckbox(request.getParameter("check_m13")));
//            g.setRegistro(ctrlCheckbox(request.getParameter("check_regdoc")));
//            g.setStato(ctrlCheckbox(request.getParameter("check_chiuso")));
//
//            cl2.setGestione(g);
//            f.setNote(request.getParameter("note") == null ? "" : new String(request.getParameter("note").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
//            f.setNote_esito(request.getParameter("note_esito") == null ? "" : new String(request.getParameter("note_esito").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
//            f.setAllegati_fa(ctrlCheckbox(request.getParameter("check_m6_2")));
//            f.setFa(ctrlCheckbox(request.getParameter("check_m6_1")));
//            f.setAllegati_fb(ctrlCheckbox(request.getParameter("check_m7_2")));
//            f.setFb(ctrlCheckbox(request.getParameter("check_m7_1")));
//            f.setM2(ctrlCheckbox(request.getParameter("check_m2")));
//            f.setM9(ctrlCheckbox(request.getParameter("check_m9_2")));
//            cl2.setFascicolo(f);
//
//            VerificheAllievo ver;
//            List<VerificheAllievo> list_al = new ArrayList();
//            for (String s : request.getParameterValues("allievi[]")) {
//                ver = new VerificheAllievo();
//                ver.setAllievo(e.getEm().find(Allievi.class, Long.parseLong(s)));
//                ver.setM1(ctrlCheckbox(request.getParameter("m1_" + s)));
//                ver.setM8(ctrlCheckbox(request.getParameter("m8_" + s)));
//                ver.setPi(ctrlCheckbox(request.getParameter("idim_" + s)));
//                ver.setRegistro(ctrlCheckbox(request.getParameter("reg_" + s)));
//                ver.setSe(ctrlCheckbox(request.getParameter("se_" + s)));
//                list_al.add(ver);
//            }
//            cl2.setVerifiche_allievi(list_al);
//
//            File checklist = ExportExcel.compileCL2(cl2);
//            if (checklist != null) {
//                resp.addProperty("message", "Checklist compilata con successo.");
//                resp.addProperty("result", true);
//                resp.addProperty("filedl", checklist.getAbsolutePath().replaceAll("\\\\", "/"));
//            } else {
//                resp.addProperty("result", false);
//                resp.addProperty("message", "Errore: non &egrave; stato possibile compilare la checklist.");
//            }
//        } catch (PersistenceException ex) {
//            ex.printStackTrace();
//            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA compileCL2: " + ex.getMessage());
//            resp.addProperty("result", false);
//            resp.addProperty("message", "Errore: non &egrave; stato possibile compilare la checklist.");
//        } finally {
//            e.close();
//        }
//
//        response.getWriter().write(resp.toString());
//        response.getWriter().flush();
//        response.getWriter().close();
    }

    protected void downloadExcelDocente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        Entity e = new Entity();
//        Docenti d = e.getEm().find(Docenti.class, Long.parseLong(request.getParameter("id")));
//        e.close();
//
//        ByteArrayOutputStream out = lezioniDocente(d);
//
//        byte[] encoded = Base64.getEncoder().encode(out.toByteArray());
//        out.close();
//
//        response.getWriter().write(new String(encoded));
//        response.getWriter().flush();
//        response.getWriter().close();
    }

    protected void downloadTarGz_only(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        Entity e = new Entity();
//        ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("id")));
//        e.close();
//
//        List<ProgettiFormativi> prgs = new ArrayList<>();
//        prgs.add(p);
//
//        ByteArrayOutputStream out = createTarArchive(prgs);
//
//        byte[] encoded = Base64.getEncoder().encode(out.toByteArray());
//        out.close();
//
//        response.getWriter().write(new String(encoded));
//        response.getWriter().flush();
//        response.getWriter().close();
    }

    protected void crearendicontazione(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        e.begin();
        JsonObject resp = new JsonObject();
        try {
            Date today = new Date();
            String[] progetti = request.getParameterValues("progetti[]");
            List<ProgettiFormativi> prgs = new ArrayList<>();
            ArrayList<String> cip = new ArrayList<>();
            ProgettiFormativi prg;
            for (String s : progetti) {
                prg = e.getEm().find(ProgettiFormativi.class, Long.parseLong(s));
                prgs.add(prg);
                cip.add(prg.getCip());
            }

            ObjectMapper mapper = new ObjectMapper();
            String path = null;
            e.persist(new Estrazioni(today, mapper.writeValueAsString(cip), path));

            for (ProgettiFormativi p : prgs) {
                p.setExtract(2);
                e.merge(p);
            }
            e.commit();
            resp.addProperty("result", true);
            resp.addProperty("path", path);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniSA crearendicontazione: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile creare il file.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void checkPiva(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        String piva = request.getParameter("piva");
        Entity e = new Entity();
        SoggettiAttuatori sa = e.getUserPiva(piva);
        e.close();
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(sa));
    }

    protected void checkCF(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        String cf = request.getParameter("cf");
        Entity e = new Entity();
        SoggettiAttuatori sa = e.getUserCF(cf);
        e.close();
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(sa));
    }

    protected void uploadPec(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject resp = new JsonObject();

        Entity e = new Entity();
        try {
            SoggettiAttuatori sa = e.getEm().find(SoggettiAttuatori.class, Long.parseLong(request.getParameter("idsa")));
            String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());
            e.begin();
            String piva = request.getParameter("piva_sa");
            String cf = request.getParameter("cf_sa");
            boolean check_piva = e.getUserPiva(piva) == null || piva.equalsIgnoreCase(sa.getPiva() == null ? "" : sa.getPiva());
            boolean check_cf = e.getUserCF(cf) == null || cf.equalsIgnoreCase(sa.getCodicefiscale() == null ? "" : sa.getCodicefiscale());
            String rs_nota = !request.getParameter("rs_sa").equalsIgnoreCase(sa.getRagionesociale()) ? "Rag. Sociale: " + request.getParameter("rs_sa") + " (" + sa.getRagionesociale() + "); " : "";
            String cf_nota = !cf.equalsIgnoreCase(sa.getCodicefiscale() == null ? "" : sa.getCodicefiscale()) ? ("C.F.: " + (cf == null || cf.equals("") ? "-" : cf) + " (" + (sa.getCodicefiscale() == null ? "-" : sa.getCodicefiscale()) + "); ") : "";
            String piva_nota = !piva.equalsIgnoreCase(sa.getPiva() == null ? "" : sa.getPiva()) ? ("P.IVA: " + (piva == null || piva.equals("") ? "-" : piva) + " (" + (sa.getPiva() == null ? "-" : sa.getPiva()) + "); ") : "";
            if (check_cf || check_piva) {
                Part p = request.getPart("file");
                if (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0) {
                    String ext = p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                    String path = e.getPath("pathDocSA").replace("@folder", String.valueOf(sa.getId()));
                    createDir(path);
                    path += Utility.correctName("pec_" + today) + ext;
                    p.write(path);
                    Storico_ModificheInfo st = new Storico_ModificheInfo();
                    st.setPath(path);
                    st.setSoggetto(sa);
                    st.setData(new Date());
                    st.setOperazione(rs_nota + cf_nota + piva_nota);
                    e.persist(st);
                }
                sa.setRagionesociale(request.getParameter("rs_sa"));
                sa.setPiva(!piva.equalsIgnoreCase("") ? piva : null);
                sa.setCodicefiscale(!cf.equalsIgnoreCase("") ? cf : null);
                e.merge(sa);
                e.flush();
                e.commit();
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
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.rollBack();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro uploadPec: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile caricare il documento.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void uploadDocPregresso(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            Part p = request.getPart("file");
            e.begin();
            Allievi_Pregresso a = e.getEm().find(Allievi_Pregresso.class, Long.parseLong(request.getParameter("idallievo")));
            TipoDoc_Allievi_Pregresso tipo = e.getEm().find(TipoDoc_Allievi_Pregresso.class, Long.parseLong(request.getParameter("id_tipo")));
            //creao il path
            String path = e.getPath("pathDoc_pregresso").replace("@cf", a.getCodice_fiscale());
            String file_path;
            String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());
            File dir = new File(path);
            createDir(path);
            //scrivo il file su disco
            if (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0) {
                file_path = dir.getAbsolutePath() + File.separator + tipo.getDescrizione() + "_" + today + p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                p.write(file_path);
                Documenti_Allievi_Pregresso doc = new Documenti_Allievi_Pregresso();
                doc.setPath(file_path);
                doc.setTipo(tipo);
                doc.setAllievo(a);
                e.persist(doc);
            }
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.rollBack();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro uploadDocPregresso: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile caricare il documento.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void modifyDocPregresso(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            Part p = request.getPart("file");
            Documenti_Allievi_Pregresso d = e.getEm().find(Documenti_Allievi_Pregresso.class, Long.parseLong(request.getParameter("id")));
            if (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0) {
                p.write(d.getPath());
            }
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.rollBack();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro modifyDocPregresso: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile modificare il documento.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void modifyDocIdPregresso(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {

            Part p = request.getPart("file");
            Allievi_Pregresso a = e.getEm().find(Allievi_Pregresso.class, Long.parseLong(request.getParameter("id")));
            //creao il path
            String path = e.getPath("pathDoc_pregresso").replace("@cf", a.getCodice_fiscale());
            File dir = new File(path);
            createDir(path);
            String file_path;
            String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());

            //scrivo il file su disco
            if (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0) {
                file_path = a.getDocid() == null ? (dir.getAbsolutePath() + File.separator + "DocId_" + today + p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."))) : a.getDocid();
                p.write(file_path);
                if (a.getDocid() == null) {//se path è null
                    e.begin();
                    a.setDocid(file_path);
                    e.persist(a);
                    e.commit();
                }
            }
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.rollBack();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro modifyDocIdPregresso: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile modificare il documento.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void sendAnswer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();

        try {
            String answer = request.getParameter("text");
            e.begin();
            Faq f = e.getLastFaqSoggetto(e.getEm().find(SoggettiAttuatori.class, Long.parseLong(request.getParameter("idsoggetto"))));
            f.setRisposta(answer);
            f.setDate_answer(new Date());
            e.merge(f);
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro sendAnswer: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile inviare il messaggio.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void setTipoFaq(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            e.begin();
            Faq f = e.getEm().find(Faq.class, Long.parseLong(request.getParameter("id")));
            f.setTipo(e.getEm().find(TipoFaq.class, Long.parseLong(request.getParameter("tipo"))));
            e.merge(f);
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro setTipoFaq: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile inviare il messaggio.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void modifyFaq(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            e.begin();
            Faq f = e.getEm().find(Faq.class, Long.parseLong(request.getParameter("id")));
            f.setDomanda_mod(request.getParameter("domanda"));
            f.setRisposta(request.getParameter("risposta"));
            e.merge(f);
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro modifyFaq: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile moodificare la FAQ.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void creaFAD(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            String nome_fad = request.getParameter("name_fad").trim();//.replaceAll("\\s+", "_");rimuove tutti gli spazi
            String pwd = Utility.getRandomString(8);
            String[] emails = request.getParameterValues("email[]");
            String[] date = request.getParameter("range").split("-");
            String note = request.getParameter("note") == null || request.getParameter("note").trim().isEmpty()
                    ? ""
                    : "Note:<br>" + request.getParameter("note");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            ObjectMapper mapper = new ObjectMapper();

            String mailjet_api = e.getPath("mailjet_api");
            String mailjet_secret = e.getPath("mailjet_secret");
            String link = e.getPath("linkfad");
            String dominio;
            if (request.getContextPath().contains("Enm_DD")) {
                dominio = "http://172.31.224.48:8080/Enm_DD/";
            } else {
                dominio = e.getPath("dominio");
            }

            e.begin();
            FadMicro f = new FadMicro();
            f.setNomestanza(nome_fad);
            f.setPassword(pwd);
            f.setPartecipanti(mapper.writeValueAsString(emails));
            f.setUser((User) request.getSession().getAttribute("user"));
            f.setDatacreazione(new Date());
            f.setInizio(sdf.parse(date[0].trim()));
            f.setFine(sdf.parse(date[1].trim()));
            e.persist(f);
            e.flush();
            e.commit();

            Email email = e.getEmail("conferenza");
            email.setTesto(email.getTesto()
                    .replace("@redirect", dominio + "redirect_out.jsp")
                    .replace("@link", link)
                    .replace("@id", f.getId().toString())
                    .replace("@pwd", pwd)
                    .replace("@start", date[0].trim())
                    .replace("@end", date[1].trim())
                    .replace("@note", note)
                    .replace("@email_tec", e.getPath("emailtecnico"))
                    .replace("@email_am", e.getPath("emailamministrativo")));

            for (String s : emails) {
                SendMailJet.sendMail(e.getPath("mailsender"), new String[]{s},
                        email.getTesto().replace("@user", s),
                        email.getOggetto());
            }

            resp.addProperty("result", true);
            resp.addProperty("pwd", pwd);
            resp.addProperty("id", f.getId());
            resp.addProperty("email", ((User) request.getSession().getAttribute("user")).getEmail());
            resp.addProperty("link", link);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro creaFAD: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile creare il convegno.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void modifyFAD(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            String nome_fad = request.getParameter("name_fad").trim();//.replaceAll("\\s+", "_");rimuove tutti gli spazi
            String pwd = Utility.getRandomString(8);
            String[] emails = request.getParameterValues("email[]");
            String[] date = request.getParameter("range").split("-");
            String note = request.getParameter("note") == null || request.getParameter("note").trim().isEmpty()
                    ? ""
                    : "Note:<br>" + request.getParameter("note");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            ObjectMapper mapper = new ObjectMapper();
            e.begin();

            FadMicro f = e.getEm().find(FadMicro.class, Long.parseLong(request.getParameter("idFad")));
            f.setNomestanza(nome_fad);
            f.setPartecipanti(mapper.writeValueAsString(emails));
            f.setInizio(sdf.parse(date[0].trim()));
            f.setFine(sdf.parse(date[1].trim()));

            e.merge(f);
            e.flush();
            e.commit();

            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro creaFAD: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile modificare il convegno.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void closeFAd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            e.begin();
            FadMicro f = e.getEm().find(FadMicro.class, Long.parseLong(request.getParameter("id")));
            f.setStato(Integer.parseInt(request.getParameter("stato")));
            e.merge(f);
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro closeFAd: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile modificare la stanza.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void reinvitaFAD(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            String mailjet_api = e.getPath("mailjet_api");
            String mailjet_secret = e.getPath("mailjet_secret");
            String link = e.getPath("linkfad");
            String dominio;
            if (request.getContextPath().contains("Enm_DD")) {
                dominio = "http://172.31.224.48:8080/Enm_DD/";
            } else {
                dominio = e.getPath("dominio");
            }

            FadMicro f = e.getEm().find(FadMicro.class, Long.parseLong(request.getParameter("id")));

            Email email = e.getEmail("conferenza");
            email.setTesto(email.getTesto()
                    .replace("@redirect", dominio + "redirect_out.jsp")
                    .replace("@link", link)
                    .replace("@id", f.getId().toString())
                    .replace("@pwd", f.getPassword())
                    .replace("@email_tec", e.getPath("emailtecnico"))
                    .replace("@email_am", e.getPath("emailamministrativo")));
            for (String s : f.getList_partecipanti()) {
                SendMailJet.sendMail(e.getPath("mailsender"), new String[]{s},
                        email.getTesto().replace("@user", s), email.getOggetto());
            }
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro reinvitaFAD: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile mandare gli inviti le mail.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void addActivity(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            e.begin();
            Attivita a = new Attivita();
            a.setName(request.getParameter("nome"));
            a.setComune(e.getEm().find(Comuni.class, Long.parseLong(request.getParameter("comune"))));
            if (a.getComune().getCoordinate() != null) {
                a.setLatitutdine(a.getComune().getCoordinate().getLatitudine() + (getRandomNumber(-30, 30) / 10000));
                a.setLongitudine(a.getComune().getCoordinate().getLongitudine() + (getRandomNumber(-30, 30) / 10000));
            }
            e.persist(a);
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro addActivity: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile aggiungere l'attività.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void deleteActivity(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            e.begin();
            Attivita a = e.getEm().find(Attivita.class, Long.parseLong(request.getParameter("id")));
            e.getEm().remove(a);
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro deteleActivity: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile eliminare l'attività.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void rejectDocente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            e.begin();
            Docenti d = e.getEm().find(Docenti.class, Long.parseLong(request.getParameter("id")));
            d.setMotivo(request.getParameter("motivo"));
            d.setStato("R");
            e.getEm().merge(d);
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro rejectDocente: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile rigettare il docente.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();

    }

    protected void modifyDocente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            e.begin();
            Docenti d = e.getEm().find(Docenti.class, Long.parseLong(request.getParameter("id")));
            d.setNome(request.getParameter("nome"));
            d.setCognome(request.getParameter("cognome"));
            d.setCodicefiscale(request.getParameter("cf"));
            d.setEmail(request.getParameter("email"));
            d.setDatanascita(new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("data")));
            d.setDatawebinair(new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("dataweb")));
            d.setFascia(e.getEm().find(FasceDocenti.class, request.getParameter("fascia")));
            d.setStato("A");
            e.getEm().merge(d);
            e.commit();
            resp.addProperty("result", true);

        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro modifyDocente: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile modificare il docente.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void updateDateProgetto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String[] date = request.getParameter("date").split("-");
        try {
            e.begin();
            ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("id")));
            p.setStart(sdf.parse(date[0].trim()));
            p.setEnd(sdf.parse(date[1].trim()));

            if (p.getEnd_fb() != null) {
                p.setEnd_fb(p.getEnd());
            }

            if (request.getParameter("fb") != null) {
                Date fb = sdf.parse(request.getParameter("fb"));
                p.setStart_fb(fb);
                p.setEnd_fa(fb);
            }
            e.getEm().merge(p);
            e.persist(new Storico_Prg("Date del progetto modificate", new Date(), p, p.getStato()));
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro deteleActivity: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile eliminare l'attività.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void rendicontaProgetto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            e.begin();
            ProgettiFormativi prg = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("id")));
            prg.setRendicontato(1);
            e.merge(prg);

            double ore_convalidate = 0;
            double euro_ore = Double.parseDouble(e.getPath("euro_ore"));

            List<DocumentiPrg> registri = prg.getDocumenti().stream().filter(p -> p.getGiorno() != null && p.getDeleted() == 0).collect(Collectors.toList());

            for (Allievi a : prg.getAllievi()) {
                for (Documenti_Allievi d : a.getDocumenti().stream().filter(p -> p.getGiorno() != null && p.getDeleted() == 0).collect(Collectors.toList())) {
                    ore_convalidate += d.getOrericonosciute();
                }
                for (DocumentiPrg r : registri) {
                    ore_convalidate += r.getPresenti_list().stream().filter(x -> x.getId() == a.getId()).findFirst().orElse(new Presenti()).getOre_riconosciute();
                }

                a.setImporto(ore_convalidate * euro_ore);
                e.merge(a);
                ore_convalidate = 0;
            }

            e.persist(new Storico_Prg("Progetto Rendicontato", new Date(), prg, prg.getStato()));
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro rendicontaProgetto: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile rendicontare progetto.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void addCpiUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            HashMap<String, String> listusernames = e.getUsersSA();
            String em = request.getParameter("email");
            e.begin();
            String pwd = Utility.getRandomString(8);
            User u = new User();
            u.setUsername(Utility.UniqueUser(listusernames, em.substring(0, em.lastIndexOf("@"))));
            u.setPassword(Utility.convMd5(pwd));
            u.setTipo(4);
            u.setEmail(em);

            e.persist(u);
            e.flush();

            CpiUser cu = new CpiUser();
            cu.setId(u);
            cu.setNome(request.getParameter("nome"));
            cu.setCognome(request.getParameter("cognome"));
            cu.setEmail(em);
//            cu.setCpi(e.getEm().find(CPI.class, request.getParameter("cpi")));

            e.persist(cu);
            e.flush();
            e.commit();
            try {
                Email email = (Email) e.getEmail("registration_cpi");
                String email_txt = email.getTesto().replace("@username", u.getUsername())
                        .replace("@password", pwd)
                        .replace("@email_tec", e.getPath("emailtecnico"))
                        .replace("@email_am", e.getPath("emailamministrativo"));
                SendMailJet.sendMail(e.getPath("mailsender"), new String[]{em}, email_txt, email.getOggetto());
                resp.addProperty("result", true);
            } catch (Exception ex) {
                e.insertTracking(null, "forgotPwd Errore Invio Mail: " + ex.getMessage());
                resp.addProperty("result", false);
                resp.addProperty("messagge", "Non è stato possibile inviare la mail, contattare l'assistenza per farsi inviare le credenziali.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro addCpiUser: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile creare utente CPI.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void addlez(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Utility.printRequest(request);

        String idpr1 = request.getParameter("idpr1");
        String corso = request.getParameter("corso");
        String orainizio = request.getParameter("orainizio");
        if (orainizio.length() == 4) {
            orainizio = "0" + orainizio;
        }
        String orafine = request.getParameter("orafine");
        if (orafine.length() == 4) {
            orafine = "0" + orafine;
        }
        String datalezione = Utility.formatStringtoStringDate(request.getParameter("datalezione"), patternITA, patternSql, false);

        Database db = new Database(false, false);
        db.insertcalendarioFAD(idpr1, corso, datalezione, orainizio, orafine);
        db.closeDB();
        Utility.redirect(request, response, "page/mc/fad_calendar.jsp?id=" + idpr1);

    }

    protected void removelez(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String idpr1 = request.getParameter("idpr1");
        String corso1 = request.getParameter("corso1");
        String inizio1 = request.getParameter("inizio1");
        String data1 = Utility.formatStringtoStringDate(request.getParameter("data1"), patternITA, patternSql, false);
        Database db = new Database(false, false);
        db.removecalendarioFAD(idpr1, corso1, inizio1, data1);
        db.closeDB();

        Utility.redirect(request, response, "page/mc/fad_calendar.jsp?id=" + idpr1);

    }

    protected void accreditaSA(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter pw = response.getWriter();

        JsonObject resp = new JsonObject();

        try {
            String idsa = getRequestValue(request, "idsa");

            //caricare tutti i dati SA
            Entity en = new Entity();
            en.begin();
            String mailsender = en.getPath("mailsender");
            HashMap<String, String> listusernames = en.getUsersSA();

            //  DATI SOGGETTO
            Database db1 = new Database(true, false);
            SoggettiAttuatori sa = db1.estrai_SA_accettare(en, idsa);
            db1.closeDB();

            boolean isneet = false;

            if (sa.getAccreditatoneet() != null && sa.getAccreditatoneet().equals("SI")) {
                isneet = true;
            }

            if (sa != null) {
                //  DOCUMENTO SOGGETTO
                Database dbinfo1 = new Database(true, isneet);
                FileDownload fw = dbinfo1.getDocumentoIdentitaSA(sa.getUsernameaccr());
                dbinfo1.closeDB();
                if (fw != null) {
                    String path = en.getPath("pathDocSA").replace("@folder", Utility.correctName(sa.getRagionesociale()));
                    createDir(path);

                    String ext = "." + FilenameUtils.getExtension(fw.getName());
                    path += Utility.correctName(sa.getNome() + "_" + sa.getCognome()) + ext;
                    File dest = new File(path);

                    FileUtils.writeByteArrayToFile(dest, decodeBase64(fw.getContent()));
                    if (checkPDF(dest)) {
                        sa.setCartaid(path);
                        en.persist(sa);
                        //DATI DOCENTI
                        Database dbinfo2 = new Database(true, isneet);
                        List<Docenti> docenti = dbinfo2.estrai_DOCENTI_SA(sa, en);
                        docenti.forEach(d1 -> {
                            //if (!cf_docentipresenti.contains(d1.getCodicefiscale())) {
                            en.persist(d1);
                            //}
                        });
                        //DATI SEDI
                        List<SediFormazione> sedi = dbinfo2.estrai_SEDI_SA(sa,
                                en, Utility.correctName(sa.getRagionesociale()));
                        sedi.forEach(s1 -> {
                            en.persist(s1);
                        });
                        dbinfo2.closeDB();
                        //CREARE UTENZA
                        String pwd = Utility.getRandomString(8);
                        User u = new User();
                        u.setUsername(Utility.UniqueUser(listusernames, sa.getEmail().substring(0, sa.getEmail().lastIndexOf("@"))));
                        u.setPassword(Utility.convMd5(pwd));
                        u.setTipo(1);
                        u.setStato(1);
                        u.setSoggettoAttuatore(sa);
                        u.setEmail(sa.getEmail());
                        en.persist(u);
                        en.commit();

                        //SALVARE TUTTO
                        //INVIA MAIL REGISTRAZIONE
                        String linkweb = en.getPath("dominio");
                        String linknohttpweb = remove(linkweb, "https://");
                        linknohttpweb = removeEnd(linknohttpweb, "/");
                        Email email = (Email) en.getEmail("registration");
                        String email_txt = email.getTesto()
                                .replace("@username", u.getUsername())
                                .replace("@password", pwd)
                                .replace("@linkweb", linkweb)
                                .replace("@linknohttpweb", linknohttpweb);

                        String[] dest_mail = {sa.getEmail()};

                        SendMailJet.sendMail(mailsender, dest_mail, email_txt, email.getOggetto());

                        resp.addProperty("result", true);
                    } else {
                        //ERROR
                        resp.addProperty("result", false);
                        resp.addProperty("message", "Errore: 1");
                    }
                } else {
                    //ERROR
                    resp.addProperty("result", false);
                    resp.addProperty("message", "Errore: 2");
                }
            } else {
                //ERROR
                resp.addProperty("result", false);
                resp.addProperty("message", "Errore: 3");
            }
            en.close();

        } catch (Exception e) {
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore:4 - " + Utility.estraiEccezione(e));
        }

        pw.write(resp.toString());
        pw.flush();
        pw.close();

    }

    protected void liquidaPrg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            double importo = Double.parseDouble(request.getParameter("importo")
                    .substring(request.getParameter("importo").lastIndexOf("_"))
                    .replaceAll("[._]", "")
                    .replace(",", ".").trim());
            e.begin();
            ProgettiFormativi prg = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("id")));
            prg.setRendicontato(2);
            prg.setImporto_ente(importo);
            e.merge(prg);
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro liquidaPrg: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato liquidare il progettto.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void uploadDocUnitaDidattica(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject resp = new JsonObject();
        Entity e = new Entity();

        boolean isDoc = request.getParameter("tipo") != null && request.getParameter("tipo").equalsIgnoreCase("F");
        try {
            UnitaDidattiche ud = e.getEm().find(UnitaDidattiche.class, request.getParameter("codice"));
            if (isDoc) {
                Part p = request.getPart("file");
                e.begin();
                String path = e.getPath("pathUnitaDidatticaMC").replace("@fase", ud.getFase()).replace("@codice", ud.getCodice());
                File dir = new File(path);
                createDir(path);
                String file_path;
                String savepath;
                String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());

//                createDir(path);
                if (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0) {
                    file_path = dir.getAbsolutePath() + File.separator + "file_" + today + p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                    savepath = dir.getAbsolutePath() + File.separator + "file_" + today + p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                    p.write(file_path);
                    Documenti_UnitaDidattiche doc = new Documenti_UnitaDidattiche(savepath, "PDF", 0, new Date(), ud);
                    e.persist(doc);
                }
                e.commit();
            } else {
                e.begin();
                Documenti_UnitaDidattiche doc = new Documenti_UnitaDidattiche(new String(request.getParameter("link").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8), "LINK", 0, new Date(), ud);
                e.persist(doc);
                e.commit();
            }

            resp.addProperty("message", "");
            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.rollBack();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro uploadDocUnitaDidattica (" + (isDoc ? "file)" : "link)") + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile caricare il " + (isDoc ? "documento." : "link."));
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void updateDocUnitaDidattica(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        Documenti_UnitaDidattiche doc = e.getEm().find(Documenti_UnitaDidattiche.class, Long.parseLong(request.getParameter("idud")));
        String tipo = doc.getTipo().equalsIgnoreCase("link") ? "link" : "documento";
        String abs_file_path;
        try {
            e.begin();

            if (doc.getTipo().equalsIgnoreCase("LINK")) {
                doc.setData_modifica(new Date());
                doc.setPath(new String(request.getParameter("link").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
            } else {
                Part p = request.getPart("file");
                if (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0) {
                    abs_file_path = "C:/" + doc.getPath(); //MODIFICARE
                    System.out.println(abs_file_path);
                    p.write(abs_file_path);
                }
                doc.setData_modifica(new Date());
            }
            e.merge(doc.getUnita_didattica());
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.rollBack();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro updateDocUnitaDidattica (" + tipo + "): " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile modificare il " + tipo + ".");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void deleteDocUnitaDidattica(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();

        Entity e = new Entity();

        try {
            e.begin();
            Documenti_UnitaDidattiche doc = e.getEm().find(Documenti_UnitaDidattiche.class, Long.parseLong(request.getParameter("id")));
            doc.setDeleted(1);
            e.persist(doc);
            e.persist(doc.getUnita_didattica());
            e.commit();
            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro deleteDocUnitaDidattica: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile eliminare il documento.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void saveanpal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();

        try {
            e.begin();
            String idallievo = getRequestValue(request, "idallievo");
            Allievi a = e.getEm().find(Allievi.class, Long.parseLong(idallievo));
            a.setData_anpal(request.getParameter("datanpal"));
            e.persist(a);
            e.commit();
            Utility.redirect(request, response, request.getContextPath() + "/page/mc/editANPAL.jsp?id=" + idallievo);

        } catch (Exception ex) {
            ex.printStackTrace();
            e.rollBack();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro updateDescrizioneUD: " + ex.getMessage());
        } finally {
            e.close();
        }

    }

    protected void updateDescrizioneUD(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            e.begin();
            UnitaDidattiche u = e.getEm().find(UnitaDidattiche.class, request.getParameter("codice"));
            u.setDescrizione(new String(request.getParameter("desc").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
            e.persist(u);
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.rollBack();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro updateDescrizioneUD: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile modificare la descrizione.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void deleteDocCloud(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();

        Entity e = new Entity();

        try {
            e.begin();
            Cloud doc = e.getEm().find(Cloud.class, Long.parseLong(request.getParameter("id")));
            doc.setAttivo(0);
            e.persist(doc);
            e.commit();
            resp.addProperty("result", true);
        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro deleteDocCloud: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile eliminare il documento.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void mappatura(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();

        String idpr = getRequestValue(request, "idpr");
        List<String> allievimappati = Splitter.on(";").splitToList(getRequestValue(request, "allievimappati"));

        List<String> notemappatura = Splitter.on("$$$").splitToList(getRequestValue(request, "notemappatura"));
        Map<Long, String> noteallievi = new HashMap<>();
        notemappatura.forEach(nota1 -> {
            List<String> nota2 = Splitter.on("###").splitToList(nota1);
            if (!nota2.isEmpty()) {
                if (!nota2.get(0).trim().equals("")) {
                    long idal = Long.parseLong(nota2.get(0));
                    if (nota2.size() > 1) {
                        noteallievi.put(idal, nota2.get(1));
                    } else {
                        noteallievi.put(idal, "");
                    }
                }
            }
        });

        Entity e = new Entity();
        try {
            e.begin();
            ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class, Long.parseLong(idpr));
            pf.getAllievi().forEach(al1 -> {
                if (allievimappati.contains(String.valueOf(al1.getId()))) {
                    al1.setMappatura(1);
                }
                try {
                    if (noteallievi.get(al1.getId()) == null) {
                        al1.setMappatura_note("");
                    } else {
                        al1.setMappatura_note(noteallievi.get(al1.getId()).trim());
                    }
                } catch (Exception ex2) {
                    al1.setMappatura_note("");
                    e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro mappatura: " + ex2.getMessage());
                }
                e.merge(al1);
            });

            pf.setStato(e.getStatiByOrdineProcesso(pf.getStato().getOrdine_processo() + 1));
            e.merge(pf);
            e.persist(new Storico_Prg("Mappatura Allievi Progetto", new Date(), pf, pf.getStato()));
            e.commit();
            resp.addProperty("result", true);

        } catch (PersistenceException ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro mappatura: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile mappare gli allievi.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void scaricaFileAssenza(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        File downloadFile = null;
        try {
            User us = (User) request.getSession().getAttribute("user");
            Entity e = new Entity();
            ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class, Long.parseLong(getRequestValue(request, "idpr")));
            downloadFile = Pdf_new.CERTIFICAZIONEASSENZA(e, us.getUsername(), pf.getSoggetto(), new DateTime(), true);
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

    protected void scaricapdfunico(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            Allievi a1 = e.getEm().find(Allievi.class, Long.parseLong(request.getParameter("idallievo")));
            if (a1 != null) {
                String nomepdf = a1.getCodicefiscale().toUpperCase().trim() + "_" + a1.getProgetto().getCip() + ".pdf";
                File downloadFile = new File(a1.getProgetto().getPdfunico());
                if (downloadFile.exists() && downloadFile.canRead()) {
                    OutputStream outStream;
                    try (FileInputStream inStream = new FileInputStream(downloadFile)) {
                        String mimeType = Files.probeContentType(downloadFile.toPath());
                        if (mimeType == null) {
                            mimeType = "application/pdf";
                        }
                        response.setContentType(mimeType);
                        String headerKey = "Content-Disposition";
                        String headerValue = String.format("inline; filename=\"%s\"", nomepdf);
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
                    User us = (User) request.getSession().getAttribute("user");
                    String page = "page/";
                    if (us.getTipo() == 1) {
                        page += "sa/indexSoggettoAttuatore.jsp";
                    } else if (us.getTipo() == 4) {
                        page += "ci/indexCi.jsp";
                    } else {
                        page += "mc/indexMicrocredito.jsp";
                    }
                    response.sendRedirect("redirect.jsp?page=" + page + "&fileNotFound=true");
                }
            } else {
                User us = (User) request.getSession().getAttribute("user");
                String page = "page/";
                if (us.getTipo() == 1) {
                    page += "sa/indexSoggettoAttuatore.jsp";
                } else if (us.getTipo() == 4) {
                    page += "ci/indexCi.jsp";
                } else {
                    page += "mc/indexMicrocredito.jsp";
                }
                response.sendRedirect("redirect.jsp?page=" + page + "&fileNotFound=true");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro deleteDocCloud: " + ex.getMessage());
            User us = (User) request.getSession().getAttribute("user");
            String page = "page/";
            if (us.getTipo() == 1) {
                page += "sa/indexSoggettoAttuatore.jsp";
            } else if (us.getTipo() == 4) {
                page += "ci/indexCi.jsp";
            } else {
                page += "mc/indexMicrocredito.jsp";
            }
            response.sendRedirect("redirect.jsp?page=" + page + "&fileNotFound=true");
        }
    }

    protected void checklistFinale(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        User us = (User) request.getSession().getAttribute("user");
        e.begin();
        String qrcrop = e.getPath("qr_crop");
        try {
            ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("pf")));
            checklist_finale cfinal = new checklist_finale();
            String step = request.getParameter("step") != null ? request.getParameter("step") : "";
            boolean isNew = true;
            if (pf.getChecklist_finale() != null) {
                cfinal = pf.getChecklist_finale();
                isNew = false;
            }

            boolean errore = false;
            String messaggioerrore = "";

            switch (step) {
                case "1":
                    if (isNew) {
                        cfinal.setProgetto_formativo(pf);
                        cfinal.setStep(2);
                    }
                    cfinal.setTipo(request.getParameter("tipo").toUpperCase());
                    Part p = request.getPart("file");
                    if (p != null && p.getSubmittedFileName() != null && p.getSubmittedFileName().length() > 0) {
                        String path = e.getPath("pathDocSA_Prg").replace("@rssa", pf.getSoggetto().getId().toString()).replace("@folder", pf.getId().toString());
                        File dir = new File(path);
                        createDir(path);
                        if (request.getParameter("tipo").toUpperCase().equals("ASSENZA")) {
                            ///
                            String ext = p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                            path += request.getParameter("tipo").toUpperCase() + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + pf.getId().toString() + ext;

                            String filedest = dir.getAbsolutePath() + File.separator + request.getParameter("tipo").toUpperCase() + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + pf.getId().toString() + ext;
                            p.write(filedest);

                            File pdfdest = new File(filedest);
                            String res = checkFirmaQRpdfA(
                                    "ASSENZA POSIZIONE",
                                    us.getUsername(),
                                    pdfdest,
                                    pf.getSoggetto().getCodicefiscale(),
                                    qrcrop);
                            cfinal.setFile(path.replace("\\", "/"));

                            if (!res.equals("OK")) {
                                errore = true;
                                messaggioerrore = res;
                                break;
                            }

                        } else {
                            String ext = p.getSubmittedFileName().substring(p.getSubmittedFileName().lastIndexOf("."));
                            path += request.getParameter("tipo").toUpperCase() + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + pf.getId().toString() + ext;
                            p.write(dir.getAbsolutePath() + File.separator + request.getParameter("tipo").toUpperCase() + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + pf.getId().toString() + ext);
                            cfinal.setFile(path.replace("\\", "/"));
                        }
                    }
                    if (cfinal.getStep() < 2) {
                        cfinal.setStep(2);
                    }
                    break;
                case "2":
                    cfinal.setTot_contributo_indennita_frequenza_fa(Double.parseDouble(request.getParameter("fa_total")));
                    cfinal.setTot_contributo_fb(Double.parseDouble(request.getParameter("fb_total")));
                    cfinal.setTot_docenza_fa(Double.parseDouble(request.getParameter("dc_total")));
                    cfinal.setTot_massimo_ammissibile(Double.parseDouble(request.getParameter("maxammissibile")));
                    cfinal.setCondizionalita_30perc(Double.parseDouble(request.getParameter("cond30")));
                    cfinal.setVcr_70perc(Double.parseDouble(request.getParameter("vcr70")));
                    cfinal.setValore_unitario_condizionalita(Double.parseDouble(request.getParameter("valunitario")));
                    cfinal.setTab_neet_fa(Utility.createJsonCL(request.getParameter("fa_controllo_ore")));
                    cfinal.setTab_neet_fb(Utility.createJsonCL(request.getParameter("fb_controllo_ore")));
                    if (cfinal.getStep() < 3) {
                        cfinal.setStep(3);
                    }
                    break;
                case "3":
                    cfinal.setTot_output_conformi(Integer.parseInt(request.getParameter("allievi_output_ok")));
                    cfinal.setTot_contributo_ammesso(Double.parseDouble(request.getParameter("tot_contributo_ammesso")));
                    if (request.getParameter("nota_controllore") != null) {
                        cfinal.setNota_controllore(new String(request.getParameter("nota_controllore").getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
                    }
                    cfinal.setTab_completezza_output_neet(Utility.createJsonCL_Output(request.getParameter("outputs")));
                    cfinal.setTab_mappatura_neet(Utility.createJsonCL_Mappatura(request.getParameter("mappatura")));
                    if (cfinal.getStep() < 4) {
                        cfinal.setStep(4);
                    }
                    break;
                default:
                    cfinal.setRevisore(e.getEm().find(Revisori.class, request.getParameter("controllore")));
                    pf.setStato((StatiPrg) e.getEm().find(StatiPrg.class, "CK"));

                    String path = e.getPath("pathDocSA_Prg").replace("@rssa", pf.getSoggetto().getId().toString()).replace("@folder", pf.getId().toString());
                    createDir(path);
                    //CREAZIONE 2 FILE E SALVATAGGIO
                    //CHECKLIST
                    File check_pdf = Pdf_new.CHECKLIST(path, e, us.getUsername(), pf.getSoggetto(), pf, new DateTime(), true);
                    DocumentiPrg check1 = new DocumentiPrg();
                    check1.setPath(check_pdf.getPath().replace("\\", "/"));
                    check1.setTipo(e.getEm().find(TipoDoc.class, 37L));
                    check1.setProgetto(pf);
                    e.persist(check1);

                    //ESITO VALUTAZIONE
                    File ev_pdf = Pdf_new.ESITOVALUTAZIONE(path, e, us.getUsername(), pf.getSoggetto(), pf, new DateTime(), true);
                    DocumentiPrg esval = new DocumentiPrg();
                    esval.setPath(ev_pdf.getPath().replace("\\", "/"));
                    esval.setTipo(e.getEm().find(TipoDoc.class, 36L));
                    esval.setProgetto(pf);
                    e.persist(esval);

                    break;
            }

            if (errore) {
                e.rollBack();
                resp.addProperty("result", false);
                resp.addProperty("message", messaggioerrore);
            } else {
                cfinal.setTimestamp(new Date());
                if (isNew) {
                    e.persist(cfinal);
                    pf.setChecklist_finale(cfinal);
                }
                e.merge(pf);
                e.commit();
                resp.addProperty("result", true);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            e.rollBack();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro checklistFinale: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile procedere con il salvataggio dei dati.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void caricaesitovalutazione(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        String idpr = getRequestValue(request, "idpr");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            Part part = request.getPart("file");
            if (part != null && part.getSubmittedFileName() != null && part.getSubmittedFileName().length() > 0) {
                e.begin();
                ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class, Long.parseLong(idpr));
                DocumentiPrg esitovalutazione = pf.getDocumenti().stream().filter(d1 -> d1.getTipo().getId() == 36L).findAny().orElse(null);
                String destpath = esitovalutazione.getPath() + "SIGNED" + part.getSubmittedFileName().substring(part.getSubmittedFileName().lastIndexOf("."));;
                part.write(destpath);
                esitovalutazione.setDeleted(1);
                e.merge(esitovalutazione);
                DocumentiPrg nuovoesitovalutazione = new DocumentiPrg(destpath, esitovalutazione.getTipo(), pf);
                e.persist(nuovoesitovalutazione);
                pf.setStato((StatiPrg) e.getEm().find(StatiPrg.class, "CO"));
                e.merge(pf);
                e.persist(new Storico_Prg("PROGETTO CONCLUSO", new Date(), pf, pf.getStato()));
                e.commit();
                resp.addProperty("result", true);
            } else {
                resp.addProperty("result", false);
                resp.addProperty("message", "Errore: file corrotto o non conforme.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro caricaesitovalutazione: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile caricare l'esito di valutazione firmato.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void sendmailesitovalutazione(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();

        String idpr = getRequestValue(request, "idpr");
        String maildest = getRequestValue(request, "mail");
        String[] mailcc = getRequestValue(request, "mailcc").trim().split(",");
        if (mailcc.length == 1) {
            if (!EmailValidator.getInstance().isValid(mailcc[0])) {
                mailcc = new String[]{};
            }
        }
        Entity e = new Entity();
        try {
            e.begin();
            ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class, Long.parseLong(idpr));
//            DocumentiPrg esitovalutazione = pf.getDocumenti().stream().filter(d1 -> d1.getTipo().getId() == 33L).findAny().orElse(null); //R^TEST
            DocumentiPrg esitovalutazione = pf.getDocumenti().stream().filter(d1 -> d1.getTipo().getId() == 36L).findAny().orElse(null);
            if (esitovalutazione != null) {
//                File content = new File("F:\\mnt\\mcn\\BECONSULTING260720211205764.M2_pdfA.pdf");//R^TEST
                File content = new File(esitovalutazione.getPath());
                if (content.exists()) {

                    Email email_txt = (Email) e.getEmail("esito_valutazione");

                    String oggetto = email_txt.getOggetto() + " " + pf.getCip();
                    String testo = StringUtils.replace(email_txt.getTesto(), "@cipcorso", pf.getCip());
                    testo = StringUtils.replace(testo, "@nomesa", pf.getSoggetto().getRagionesociale().toUpperCase());
                    boolean ok = SendMailJet.sendMail(e.getPath("mailsender"), new String[]{maildest}, mailcc, testo, oggetto, content);

                    if (ok) {
                        pf.setStato((StatiPrg) e.getEm().find(StatiPrg.class, "EVI"));
                        e.merge(pf);
                        e.persist(new Storico_Prg("ESITO VALUTAZIONE INVIATO A: " + maildest, new Date(), pf, pf.getStato()));
                        e.commit();
                        resp.addProperty("result", true);
                    } else {
                        resp.addProperty("result", false);
                        resp.addProperty("message", "IMPOSSIBILE INVIARE LA MAIL");
                    }
                } else {
                    resp.addProperty("result", false);
                    resp.addProperty("message", "DOCUMENTO NON TROVATO");
                }
            } else {
                resp.addProperty("result", false);
                resp.addProperty("message", "DOCUMENTO NON TROVATO");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro mappatura: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: " + ex.getMessage());
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void caricadocumentocontrollo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        String idpr = getRequestValue(request, "idpr");
        String target = getRequestValue(request, "target");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            e.begin();
            Part part = request.getPart("file");
            if (part != null && part.getSubmittedFileName() != null && part.getSubmittedFileName().length() > 0) {
                ProgettiFormativi prg = e.getEm().find(ProgettiFormativi.class, Long.parseLong(idpr));
                
                TipoDoc tipo;
                if(target.equals("D1")){
                    tipo = e.getEm().find(TipoDoc.class, 12L);
                } else {
                    tipo = e.getEm().find(TipoDoc.class, 13L);
                }
                
                String path = e.getPath("pathDocSA_Prg").replace("@rssa",
                        prg.getSoggetto().getId().toString()).replace("@folder", prg.getId().toString());
                File dir = new File(path);
                createDir(path);
                String file_path;
                String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());
                //scrivo il file su disco
                file_path = dir.getAbsolutePath() + File.separator + tipo.getDescrizione() + "_"
                        + today + part.getSubmittedFileName().substring(part.getSubmittedFileName().lastIndexOf("."));
                part.write(file_path);
                DocumentiPrg doc = new DocumentiPrg();
                doc.setPath(file_path);
                doc.setTipo(tipo);
                doc.setProgetto(prg);
                e.persist(doc);
                e.commit();
                resp.addProperty("result", true);
            } else {
                resp.addProperty("result", false);
                resp.addProperty("message", "Errore: file corrotto o non conforme.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            e.rollBack();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro caricanuovodocumento: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile caricare il documento selezionato.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
        
        
    }
    
    protected void caricanuovodocumento(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        String idpr = getRequestValue(request, "idpr");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            e.begin();
            Part part = request.getPart("file");
            if (part != null && part.getSubmittedFileName() != null && part.getSubmittedFileName().length() > 0) {
                ProgettiFormativi prg = e.getEm().find(ProgettiFormativi.class, Long.parseLong(idpr));
                TipoDoc tipo = e.getEm().find(TipoDoc.class, 11L);
                //creao il path
                String path = e.getPath("pathDocSA_Prg").replace("@rssa",
                        prg.getSoggetto().getId().toString()).replace("@folder", prg.getId().toString());
                File dir = new File(path);
                createDir(path);
                String file_path;
                String today = new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());
                //scrivo il file su disco
                file_path = dir.getAbsolutePath() + File.separator + tipo.getDescrizione() + "_"
                        + today + part.getSubmittedFileName().substring(part.getSubmittedFileName().lastIndexOf("."));
                part.write(file_path);
                DocumentiPrg doc = new DocumentiPrg();
                doc.setPath(file_path);
                doc.setTipo(tipo);
                doc.setProgetto(prg);
                e.persist(doc);
                e.commit();
                resp.addProperty("result", true);
            } else {
                resp.addProperty("result", false);
                resp.addProperty("message", "Errore: file corrotto o non conforme.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            e.rollBack();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro caricanuovodocumento: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile caricare il documento selezionato.");
        } finally {
            e.close();
        }
        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void assegnaPrg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JsonObject resp = new JsonObject();
        Entity e = new Entity();
        try {
            String assegnazione = getRequestValue(request, "assegnazione").toUpperCase();
            e.begin();
            ProgettiFormativi prg = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("id")));
            prg.setAssegnazione(assegnazione);
            e.merge(prg);
            e.commit();
            resp.addProperty("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()), "OperazioniMicro assegnaPrg: " + ex.getMessage());
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore: non &egrave; stato possibile assegnare il progettto.");
        } finally {
            e.close();
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();

    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        User us = (User) request.getSession().getAttribute("user");
        if (us != null && us.getTipo() == 2) {
            String type = request.getParameter("type");
            switch (type) {
                case "saveanpal":
                    saveanpal(request, response);
                    break;
                case "addlez":
                    addlez(request, response);
                    break;
                case "removelez":
                    removelez(request, response);
                    break;
                case "setProtocollo":
                    setProtocollo(request, response);
                    break;
                case "addDocente":
                    addDocente(request, response);
                    break;
                case "addDocenteFile":
                    addDocenteFile(request, response);
                    break;
                case "addAuleFile":
                    addAuleFile(request, response);
                    break;
                case "addAula":
                    addAula(request, response);
                    break;
                case "validatePrg":
                    validatePrg(request, response);
                    break;
                case "validateAula":
                    validateAula(request, response);
                    break;
                case "rejectPrg":
                    rejectPrg(request, response);
                    break;
                case "annullaPrg":
                    annullaPrg(request, response);
                    break;
                case "validateHourRegistroAula":
                    validateHourRegistroAula(request, response);
                    break;
                case "setHoursRegistro":
                    setHoursRegistro(request, response);
                    break;
                case "modifyDoc":
                    modifyDoc(request, response);
                    break;
                case "uploadDocPrg":
                    uploadDocPrg(request, response);
                    break;
                case "compileCL2":
                    compileCL2(request, response);
                    break;
                case "downloadExcelDocente":
                    downloadExcelDocente(request, response);
                    break;
                case "downloadTarGz_only":
                    downloadTarGz_only(request, response);
                    break;
                case "crearendicontazione":
                    crearendicontazione(request, response);
                    break;
                case "checkPiva":
                    checkPiva(request, response);
                    break;
                case "checkCF":
                    checkCF(request, response);
                    break;
                case "uploadPec":
                    uploadPec(request, response);
                    break;
                case "uploadDocPregresso":
                    uploadDocPregresso(request, response);
                    break;
                case "modifyDocPregresso":
                    modifyDocPregresso(request, response);
                    break;
                case "modifyDocIdPregresso":
                    modifyDocIdPregresso(request, response);
                    break;
                case "sendAnswer":
                    sendAnswer(request, response);
                    break;
                case "setTipoFaq":
                    setTipoFaq(request, response);
                    break;
                case "modifyFaq":
                    modifyFaq(request, response);
                    break;
                case "creaFAD":
                    creaFAD(request, response);
                    break;
                case "closeFAd":
                    closeFAd(request, response);
                    break;
                case "reinvitaFAD":
                    reinvitaFAD(request, response);
                    break;
                case "addActivity":
                    addActivity(request, response);
                    break;
                case "deleteActivity":
                    deleteActivity(request, response);
                    break;
                case "modifyDocente":
                    modifyDocente(request, response);
                    break;
                case "rejectDocente":
                    rejectDocente(request, response);
                    break;
                case "updateDateProgetto":
                    updateDateProgetto(request, response);
                    break;
                case "rendicontaProgetto":
                    rendicontaProgetto(request, response);
                    break;
                case "modifyFAD":
                    modifyFAD(request, response);
                    break;
                case "addCpiUser":
                    addCpiUser(request, response);
                    break;
                case "liquidaPrg":
                    liquidaPrg(request, response);
                    break;
                case "accreditaSA":
                    accreditaSA(request, response);
                    break;
                case "uploadDocUnitaDidattica":
                    uploadDocUnitaDidattica(request, response);
                    break;
                case "updateDocUnitaDidattica":
                    updateDocUnitaDidattica(request, response);
                    break;
                case "deleteDocUnitaDidattica":
                    deleteDocUnitaDidattica(request, response);
                    break;
                case "updateDescrizioneUD":
                    updateDescrizioneUD(request, response);
                    break;
                case "addCloud":
                    addCloud(request, response);
                    break;
                case "deleteDocCloud":
                    deleteDocCloud(request, response);
                    break;
                case "scaricaFileAssenza":
                    scaricaFileAssenza(request, response);
                    break;
                case "checklistFinale":
                    checklistFinale(request, response);
                    break;
                case "scaricapdfunico":
                    scaricapdfunico(request, response);
                    break;
                case "mappatura":
                    mappatura(request, response);
                    break;
                case "sendmailesitovalutazione":
                    sendmailesitovalutazione(request, response);
                    break;
                case "caricaesitovalutazione":
                    caricaesitovalutazione(request, response);
                    break;
                case "caricanuovodocumento":
                    caricanuovodocumento(request, response);
                    break;
                case "caricadocumentocontrollo":
                    caricadocumentocontrollo(request, response);
                    break;
                case "assegnaPrg":
                    assegnaPrg(request, response);
                    break;
                default:
                    break;
            }
        }
    }

    private double getRandomNumber(int min, int max) {
        return ((Math.random() * (max - min)) + min);
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
