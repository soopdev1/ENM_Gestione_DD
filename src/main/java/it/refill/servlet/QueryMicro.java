/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.servlet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.refill.db.Database;
import it.refill.db.Entity;
import it.refill.domain.Allievi;
import it.refill.domain.Attivita;
import it.refill.domain.TipoFaq;
import it.refill.domain.Comuni;
import it.refill.domain.CpiUser;
import it.refill.domain.Docenti;
import it.refill.domain.DocumentiPrg;
import it.refill.domain.Documenti_Allievi;
import it.refill.domain.Estrazioni;
import it.refill.domain.FadMicro;
import it.refill.domain.Faq;
import it.refill.domain.Lezioni_Modelli;
import it.refill.domain.MascheraM5;
import it.refill.domain.ModelliPrg;
import it.refill.domain.ProgettiFormativi;
import it.refill.domain.SediFormazione;
import it.refill.domain.SoggettiAttuatori;
import it.refill.domain.StatiPrg;
import it.refill.domain.Storico_ModificheInfo;
import it.refill.domain.Storico_Prg;
import it.refill.domain.TipoDoc;
import it.refill.domain.TipoDoc_Allievi;
import it.refill.domain.UnitaDidattiche;
import it.refill.domain.User;
import it.refill.util.Fadroom;
import it.refill.util.Utility;
import static it.refill.util.Utility.writeJsonResponseR;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author agodino
 */
public class QueryMicro extends HttpServlet {

    protected void searchSA(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            String ragionesociale = request.getParameter("ragionesociale") == null ? "" : request.getParameter("ragionesociale");
            String protocollo = request.getParameter("protocollo") == null ? "" : request.getParameter("protocollo");
            String piva = request.getParameter("piva") == null ? "" : request.getParameter("piva");
            String cf = request.getParameter("cf") == null ? "" : request.getParameter("cf");
            String protocollare = request.getParameter("protocollare");
            String nome = request.getParameter("nome") == null ? "" : request.getParameter("nome");
            String cognome = request.getParameter("cognome") == null ? "" : request.getParameter("cognome");
            List<SoggettiAttuatori> list = e.getSoggettiAttuatori(ragionesociale, protocollo, piva, cf, protocollare, nome, cognome);
            writeJsonResponse(response, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void searchnuoviSA(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();

        try {

            Database db1 = new Database(true, false);

            List<SoggettiAttuatori> list = db1.estrai_SA_accettare(e);
            db1.closeDB();

            List<String> list2 = e.getSoggettiAttuatori().stream().map(r -> r.getPiva()).collect(Collectors.toList());

            List<SoggettiAttuatori> listDEF = new ArrayList<>();

            list.forEach(s1 -> {
                if (!list2.contains(s1.getPiva())) {
                    listDEF.add(s1);
                }
            });

//            List<SoggettiAttuatori> list = e.getSoggettiAttuatori(ragionesociale, protocollo, piva, cf, protocollare, nome, cognome);
            writeJsonResponse(response, listDEF);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void searchAllievo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            SoggettiAttuatori sa = request.getParameter("soggettoattuatore").equals("-") ? null : e.getEm().find(SoggettiAttuatori.class, Long.parseLong(request.getParameter("soggettoattuatore")));
            String nome = request.getParameter("nome") == null ? "" : request.getParameter("nome");
            String cognome = request.getParameter("cognome") == null ? "" : request.getParameter("cognome");
            String cf = request.getParameter("cf") == null ? "" : request.getParameter("cf");
            String flag_pregresso = request.getParameter("pregresso");
            List<Allievi> list = new ArrayList();

            if (flag_pregresso.equals("0") || flag_pregresso.equals("")) {
                list = e.getAllievi(sa, nome, cognome, cf);
                list.stream().forEach((l) -> {
                    Utility.setOreLezioni(l);
                });
            }

            writeJsonResponse(response, list);

        } catch (IOException | NumberFormatException | ServletException ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void searchDocenti(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            String nome = request.getParameter("nome") == null ? "" : request.getParameter("nome");
            String cognome = request.getParameter("cognome") == null ? "" : request.getParameter("cognome");
            String cf = request.getParameter("cf") == null ? "" : request.getParameter("cf");
            List<Docenti> list = e.getDocenti(nome, cognome, cf);
            writeJsonResponse(response, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void searchProgetti(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User us = (User) request.getSession().getAttribute("user");

        Entity e = new Entity();
        try {
            SoggettiAttuatori sa = request.getParameter("soggettoattuatore").equals("-") ? null : e.getEm().find(SoggettiAttuatori.class, Long.parseLong(request.getParameter("soggettoattuatore")));
            String stato = request.getParameter("stato").equals("-") ? "" : request.getParameter("stato");
            String cip = request.getParameter("cip") == null ? "" : request.getParameter("cip");
            String rendicontato = request.getParameter("rendicontato").equals("-") ? "" : request.getParameter("rendicontato");
            List<StatiPrg> sp = null;
            if (!stato.equals("")) {
                if (stato.equals("errore") || stato.equals("controllare")) {
                    sp = e.getStatibyTipo(stato);
                } else {
                    sp = e.getStatibyDescrizione(stato);
                }
            }
            List<ProgettiFormativi> list = e.getProgettiFormativi(sa, sp, cip, rendicontato);

            Database db1 = new Database(false, false);
            String linkfad = db1.getPathtemp("linkfad") + "/Login";
            List<Fadroom> lst = db1.listStanzeOGGI();
            db1.closeDB();

            list.forEach((ProgettiFormativi prog) -> {

                prog.setAllievi_ok(prog.getAllievi().stream().filter(a1 -> a1.getStatopartecipazione().getId().equals("01")).collect(Collectors.toList()).size());
                prog.setAllievi_total(prog.getAllievi().size());

                prog.setUsermc(us.getUsername());
                prog.setFadlink(linkfad);
                prog.setFadroom(null);
                prog.setFadreport(false);

                try {

                    List<Fadroom> lst2 = lst.stream().filter(s1 -> s1.getIdpr().equals(String.valueOf(prog.getId()))).collect(Collectors.toList());
                    if (!lst2.isEmpty()) {
                        prog.setFadroom(lst2);
                    }
//                    List<Fadroom> lst2 = lst.stream().filter(ite -> String.valueOf(ite.getIdpr()).equals(String.valueOf(prog.getId()))).collect(Collectors.toList());
//                    if (!lst2.isEmpty()) {
//                        prog.setFadroom(lst2);
////                        prog.setFadreport(rep.stream().anyMatch(r1 -> r1.getValue().equals(String.valueOf(prog.getId()))));
//                    }
//                    Item it = lst.stream().filter(ite -> String.valueOf(ite.getValue()).equals(String.valueOf(prog.getId()))).findAny().get();
//                    if (it != null) {
//                        prog.setFadroom(it.getDesc());
//                    } else {
//                        prog.setFadroom("NO");
//                    }
                } catch (Exception ex) {
                    prog.setFadroom(null);
                }
            });
            writeJsonResponseR(response, list);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void searchProgettiDocente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            Docenti d = e.getEm().find(Docenti.class, Long.parseLong(request.getParameter("iddocente")));
            List<ProgettiFormativi> list = e.getProgettiFormativiDocenti(d);
            writeJsonResponse(response, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void searchAllieviProgetti(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("idprogetto")));
            List<Allievi> list = e.getAllieviProgettiFormativiAll(p);
            writeJsonResponse(response, list);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void searchSedi(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<Comuni> comuni = new ArrayList<>();

            if (!request.getParameter("regione").equals("-")) {
                if (!request.getParameter("provincia").equals("-")) {
                    if (!request.getParameter("comune").equals("-")) {//singolo comune
                        comuni.add((Comuni) e.getEm().find(Comuni.class, Long.parseLong(request.getParameter("comune"))));
                    } else {//lista comuni provincia
                        comuni = e.listaComunibyProvincia(request.getParameter("provincia"));
                    }
                } else {// lista comuni regione
                    comuni = e.listaComunibyRegione(request.getParameter("regione"));
                }
            }

            List<SediFormazione> list = e.getSediFormazione(request.getParameter("referente"), comuni);
            writeJsonResponse(response, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getDocPrg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<DocumentiPrg> documenti = e.getDocPrg(e.getEm().find(ProgettiFormativi.class,
                    Long.parseLong(request.getParameter("idprogetto"))));
            int indice = 0;
            for (int x = 0; x < documenti.size(); x++) {
                DocumentiPrg d1 = documenti.get(x);
                TipoDoc t1 = d1.getTipo();
                String desc = t1.getDescrizione();
                d1.setNome(desc);
                if (x > 0) {
                    if (documenti.get(x - 1).getTipo().equals(d1.getTipo())) {
                        if (d1.getDocente() == null) {
                            indice++;
                        } else {
                            indice = 0;
                        }
                    } else {
                        indice = 0;
                    }

                    if (indice > 0) {
                        d1.setNome(desc + "_V" + (indice + 1));
                    } else {
                        indice = 0;
                    }
                }
            }

            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(documenti));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getStoryPrg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("idprogetto")));
            List<Storico_Prg> list = e.getStoryPrg(p);
            writeJsonResponse(response, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getDocAllievo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            Allievi a = e.getEm().find(Allievi.class, Long.parseLong(request.getParameter("idallievo")));
            List<Documenti_Allievi> docs = e.getDocAllievo(a);
            MascheraM5 m5_allievo = e.getM5_byAllievo(a);
            if (m5_allievo != null && m5_allievo.getDomanda_ammissione() != null) {
                TipoDoc_Allievi dA = new TipoDoc_Allievi();
                dA.setDescrizione("DOMANDA AMMISSIONE");
                dA.setEstensione("pdf");
                dA.setMimetype("application/pdf");
                Documenti_Allievi domandaAmmissione = new Documenti_Allievi(m5_allievo.getDomanda_ammissione(), dA, null, a);
                docs.add(domandaAmmissione);
            }
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(docs));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void searchDocentiProgetti(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<Docenti> list = e.getDocentiPrg(e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("idprogetto"))));
            writeJsonResponse(response, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getEstrazioni(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<Estrazioni> list = e.getEstazioniDesc();
            writeJsonResponse(response, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getPec(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            SoggettiAttuatori sa = e.getEm().find(SoggettiAttuatori.class, Long.parseLong(request.getParameter("id")));
            List<Storico_ModificheInfo> list = e.getPec(sa);
            writeJsonResponse(response, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getConversationSA(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<Faq> list = e.getFaqSoggetto(e.getEm().find(SoggettiAttuatori.class, Long.parseLong(request.getParameter("idsoggetto"))));
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(list));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void geFaqAnswer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            SoggettiAttuatori sa = request.getParameter("soggettoattuatore").equals("-") ? null : e.getEm().find(SoggettiAttuatori.class, Long.parseLong(request.getParameter("soggettoattuatore")));
            TipoFaq tipo = request.getParameter("tipo").equals("-") ? null : e.getEm().find(TipoFaq.class, Long.parseLong(request.getParameter("tipo")));
            List<Faq> list = e.faqAnswer(sa, tipo);
            writeJsonResponse(response, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getTipoFaq(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<TipoFaq> list = e.findAll(TipoFaq.class);
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(list));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getFaq(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            Faq faq = e.getEm().find(Faq.class, Long.parseLong(request.getParameter("id")));
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(faq));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getMyConference(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            String nomestanza = request.getParameter("nome") == null ? "" : request.getParameter("nome");
            String stato = request.getParameter("stato");
            List<FadMicro> list = e.getMyConference((User) request.getSession().getAttribute("user"), nomestanza, stato);
            writeJsonResponse(response, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getFAD(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            FadMicro faq = e.getEm().find(FadMicro.class, Long.parseLong(request.getParameter("id")));
            //aggiungo 2 parametri al json
            StringWriter sw = new StringWriter();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(sw, faq);
            JsonObject jMembers = new JsonParser().parse(sw.toString()).getAsJsonObject();
            jMembers.addProperty("link", e.getPath("linkfad"));
            response.setContentType("application/json");
            response.setHeader("Content-Type", "application/json");
            response.getWriter().print(jMembers.toString());
            response.getWriter().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void searchActivity(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<Comuni> comuni = new ArrayList<>();

            if (!request.getParameter("regione").equals("-")) {
                if (!request.getParameter("provincia").equals("-")) {
                    if (!request.getParameter("comune").equals("-")) {//singolo comune
                        comuni.add((Comuni) e.getEm().find(Comuni.class, Long.parseLong(request.getParameter("comune"))));
                    } else {//lista comuni provincia
                        comuni = e.listaComunibyProvincia(request.getParameter("provincia"));
                    }
                } else {// lista comuni regione
                    comuni = e.listaComunibyRegione(request.getParameter("regione"));
                }
            }

            List<Attivita> list = e.getAttivita(request.getParameter("nome"), comuni);
            writeJsonResponse(response, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getDocente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        Entity e = new Entity();
        try {
            Docenti d = e.getEm().find(Docenti.class, Long.parseLong(request.getParameter("id")));
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().print(mapper.writeValueAsString(d));
            response.getWriter().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void generatecip(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class, Long.parseLong(Utility.getRequestValue(request, "idprogetto")));
        e.close();
        String cip = p.getSoggetto().getProtocollo();
        int start = (p.getSoggetto().getProgettiformativi().stream().filter(p1 -> p1.getCip() != null).collect(Collectors.toList()).size()) + 1;
        cip = StringUtils.remove(cip, "YISU_");
        if (!cip.startsWith("N")) {
            cip = "N_" + cip;
        }
        cip = cip + "_C" + start;
        try (PrintWriter out = response.getWriter();) {
            out.print(cip);
        }
    }

    protected void searchCpiUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<CpiUser> list = e.findAll(CpiUser.class);
            writeJsonResponse(response, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void searchUnitaDidattiche(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<UnitaDidattiche> list = new ArrayList();
            if (request.getParameter("codiceud").equals("-")) {
                list = e.getUD();
            } else {
                UnitaDidattiche ud = e.getEm().find(UnitaDidattiche.class, request.getParameter("codiceud"));
                list.add(ud);
            }
            if (!request.getParameter("fase").equalsIgnoreCase("-")) {
                list = list.stream().filter(u -> u.getFase().equalsIgnoreCase(request.getParameter("fase"))).collect(Collectors.toList());
            }
            for (UnitaDidattiche u : list) {
                u.setCountDocs((int) u.getDocumenti_ud().stream().filter(o -> o.getDeleted() == 0).count());
            }

            writeJsonResponse(response, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getAllieviByPrg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<Allievi> a = e.getAllieviProgettiFormativi(e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("id"))));
            List<Allievi> list = new ArrayList();
            for (Allievi al : a) {
                list.add(new Allievi(al.getId(), al.getNome(), al.getCognome()));
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            response.getWriter().write(mapper.writeValueAsString(list));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getLezioniByProgetto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            ModelliPrg m = e.getEm().find(ModelliPrg.class, Long.parseLong(request.getParameter("idmodello")));
            List<Lezioni_Modelli> list = e.getLezioniByProgetto(m);
            for (Lezioni_Modelli lm : list) {
                lm.setCodice_ud(lm.getLezione_calendario().getUnitadidattica().getCodice());
                lm.setIdprogettoformativo((int) (long) lm.getModello().getProgetto().getId());
            }
            for (Lezioni_Modelli lm : list) {
                lm.getLezione_calendario().setUnitadidattica(null);
                lm.getModello().setProgetto(null);
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            response.getWriter().write(mapper.writeValueAsString(list));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getAllieviByProgetto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<Allievi> a = e.getAllieviProgettiFormativi(e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("id"))));
            if (request.getParameter("load").equalsIgnoreCase("si")) {
                List<String[]> list = new ArrayList();
                String[] lneet;
                Map<Integer, List<Allievi>> byGruppi = a.stream().collect(Collectors.groupingBy(t -> t.getGruppo_faseB()));
                for (Map.Entry<Integer, List<Allievi>> it : byGruppi.entrySet()) {
                    lneet = new String[it.getValue().size()];
                    for (int j = 0; j < it.getValue().size(); j++) {
                        lneet[j] = it.getValue().get(j).getNome() + " " + it.getValue().get(j).getCognome();
                    }
                    list.add(new String[]{String.valueOf(it.getKey()), String.join(", ", lneet)});
                }
                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                response.getWriter().write(mapper.writeValueAsString(list));
            } else {
                List<Allievi> list = new ArrayList();
                for (Allievi al : a) {
                    list.add(new Allievi(al.getId(), al.getNome(), al.getCognome(), al.getGruppo_faseB()));
                }
                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                response.getWriter().write(mapper.writeValueAsString(list));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void verificaassegnazione(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class, Long.parseLong(Utility.getRequestValue(request, "idprogetto")));
        e.close();
        String assegn = "";
        if (p != null && p.getAssegnazione() != null) {
            assegn = p.getAssegnazione().toUpperCase();
        }
        try (PrintWriter out = response.getWriter();) {
            out.print(assegn);
        }

    }

    protected void getChecklistfinale(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        ProgettiFormativi d = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("pf")));

        JSONArray array = new JSONArray();
        JSONObject item;
        MascheraM5 m5;
        for (Allievi a : d.getAllievi()) {
            m5 = e.getM5_byAllievo(a);
            if (m5 != null && m5.getDomanda_ammissione() != null) {
                item = new JSONObject();
                item.put("id", a.getId());
                item.put("da", m5.getDomanda_ammissione());
                array.put(item);
            }
        }
        e.close();
        Map mr = new HashMap();
        mr.put("cl", d.getChecklist_finale());
        mr.put("allievi", array.toString());

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(mr);
        response.getWriter().write(json);
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        User us = (User) request.getSession().getAttribute("user");
        if (us != null && (us.getTipo() == 2 || us.getTipo() == 5)) {
            String type = request.getParameter("type");
            switch (type) {
                case "verificaassegnazione":
                    verificaassegnazione(request, response);
                    break;
                case "generatecip":
                    generatecip(request, response);
                    break;
                case "searchSA":
                    searchSA(request, response);
                    break;
                case "nuoviSA":
                    searchnuoviSA(request, response);
                    break;
                case "searchAllievo":
                    searchAllievo(request, response);
                    break;
                case "searchDocenti":
                    searchDocenti(request, response);
                    break;
                case "searchProgetti":
                    searchProgetti(request, response);
                    break;
                case "searchProgettiDocente":
                    searchProgettiDocente(request, response);
                    break;
                case "searchAllieviProgetti":
                    searchAllieviProgetti(request, response);
                    break;
                case "searchSedi":
                    searchSedi(request, response);
                    break;
                case "getDocPrg":
                    getDocPrg(request, response);
                    break;
                case "getStoryPrg":
                    getStoryPrg(request, response);
                    break;
                case "getDocAllievo":
                    getDocAllievo(request, response);
                    break;
                case "searchDocentiProgetti":
                    searchDocentiProgetti(request, response);
                    break;
                case "getEstrazioni":
                    getEstrazioni(request, response);
                    break;
                case "getPec":
                    getPec(request, response);
                    break;
                case "getConversationSA":
                    getConversationSA(request, response);
                    break;
                case "geFaqAnswer":
                    geFaqAnswer(request, response);
                    break;
                case "getTipoFaq":
                    getTipoFaq(request, response);
                    break;
                case "getFaq":
                    getFaq(request, response);
                    break;
                case "getMyConference":
                    getMyConference(request, response);
                    break;
                case "getFAD":
                    getFAD(request, response);
                    break;
                case "searchActivity":
                    searchActivity(request, response);
                    break;
                case "getDocente":
                    getDocente(request, response);
                    break;
                case "searchCpiUser":
                    searchCpiUser(request, response);
                    break;
                case "searchUnitaDidattiche":
                    searchUnitaDidattiche(request, response);
                    break;
                case "getAllieviByPrg":
                    getAllieviByPrg(request, response);
                    break;
                case "getLezioniByProgetto":
                    getLezioniByProgetto(request, response);
                    break;
                case "getAllieviByProgetto":
                    getAllieviByProgetto(request, response);
                    break;
                case "getChecklistfinale":
                    getChecklistfinale(request, response);
                    break;
                default:
                    break;
            }
        }
    }

    /* metodi */
    private void writeJsonResponse(HttpServletResponse response, Object list)
            throws ServletException, IOException {
        JsonObject jMembers = new JsonObject();
        StringWriter sw = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(sw, list);
        String json_s = sw.toString();
        JsonParser parser = new JsonParser();
        JsonElement tradeElement = parser.parse(json_s);
        jMembers.add("aaData", tradeElement.getAsJsonArray());
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        response.getWriter().print(jMembers.toString());
        response.getWriter().close();
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
