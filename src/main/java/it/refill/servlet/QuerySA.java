/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.servlet;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.refill.db.Action;
import it.refill.db.Database;
import it.refill.db.Entity;
import it.refill.domain.Allievi;
import it.refill.domain.Docenti;
import it.refill.domain.DocumentiPrg;
import it.refill.domain.Documenti_Allievi;
import it.refill.domain.Faq;
import it.refill.domain.LezioneCalendario;
import it.refill.domain.Lezioni_Modelli;
import it.refill.domain.MascheraM5;
import it.refill.domain.ModelliPrg;
import it.refill.domain.ProgettiFormativi;
import it.refill.domain.Selfiemployment_Prestiti;
import it.refill.domain.StaffModelli;
import it.refill.domain.StatiPrg;
import it.refill.domain.StatoPartecipazione;
import it.refill.domain.TipoDoc;
import it.refill.domain.TipoDoc_Allievi;
import it.refill.domain.User;
import it.refill.entity.ProgettiLezioniModelli;
import it.refill.util.Fadroom;
import it.refill.util.Utility;
import static it.refill.util.Utility.writeJsonResponseR;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author dolivo
 */
public class QuerySA extends HttpServlet {

    protected void searchAllievi(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            User us = (User) request.getSession().getAttribute("user");
            String nome = request.getParameter("nome") == null ? "" : request.getParameter("nome");
            String cognome = request.getParameter("cognome") == null ? "" : request.getParameter("cognome");
            String cf = request.getParameter("cf") == null ? "" : request.getParameter("cf");
            String stato = request.getParameter("stato");

            List<Allievi> list = e.getAllieviSA(nome, cognome, cf, stato, us.getSoggettoAttuatore());
            writeJsonResponse(response, list);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void searchDocenti(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            User us = (User) request.getSession().getAttribute("user");

            String nome = request.getParameter("nome") == null ? "" : request.getParameter("nome");
            String cognome = request.getParameter("cognome") == null ? "" : request.getParameter("cognome");
            String cf = request.getParameter("cf") == null ? "" : request.getParameter("cf");
//            List<Docenti> list = e.li;

            List<Docenti> list = e.getDocenti(nome, cognome, cf, us.getSoggettoAttuatore());

            writeJsonResponse(response, list);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void searchProgetti(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            User us = (User) request.getSession().getAttribute("user");
            String stato = request.getParameter("stato").equals("-") ? "" : request.getParameter("stato");
            String cip = request.getParameter("cip") == null ? "" : request.getParameter("cip");
            List<StatiPrg> sp = null;
            if (!stato.equals("")) {
                if (stato.equals("errore") || stato.equals("controllare")) {
                    sp = e.getStatibyTipo(stato);
                } else {
                    sp = e.getStatibyDescrizione(stato);
                }
            }
            List<ProgettiFormativi> list = e.getProgettiFormativi(us.getSoggettoAttuatore(), sp, cip);

            Database db1 = new Database(false,false);
            String linkfad = db1.getPathtemp("linkfad") + "/Login";
            List<Fadroom> lst = db1.listStanzeOGGISA(us.getSoggettoAttuatore().getId());
            db1.closeDB();

            list.forEach((ProgettiFormativi prog) -> {

                List<DocumentiPrg> getDocumenti = prog.getDocumenti();
                DocumentiPrg regfaseA = getDocumenti.stream().filter(doc -> doc.getTipo().getId() == 29L).findAny().orElse(null);

                prog.setAllievi_ok(prog.getAllievi().stream().filter(a1 -> a1.getStatopartecipazione().getId().equals("01")).collect(Collectors.toList()).size());
                prog.setAllievi_total(prog.getAllievi().size());

                prog.setUsermc(us.getUsername());

                prog.setFadlink(linkfad);
                prog.setFadreport(false);
                prog.setFadreportpath(null);
                if (regfaseA != null) {
                    prog.setFadreport(true);
                    prog.setFadreportpath(regfaseA.getPath());
                }

                //STANZE FAD
                prog.setFadroom(null);
                try {
                    List<Fadroom> lst2 = lst.stream().filter(s1 -> s1.getIdpr().equals(String.valueOf(prog.getId()))).collect(Collectors.toList());
                    if (!lst2.isEmpty()) {
                        prog.setFadroom(lst2);
                    }
                } catch (Exception ex) {
                    prog.setFadroom(null);
                }

            });

            writeJsonResponseR(response, list);

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
//            List<Allievi> list = e.getAllieviProgettiFormativi(p);
            writeJsonResponse(response, p.getAllievi());

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

    protected void getDocPrg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<DocumentiPrg> documenti = e.getDocPrg(e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("idprogetto"))));
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

     protected void getDocAllievo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            Allievi a = e.getEm().find(Allievi.class, Long.parseLong(request.getParameter("idallievo")));
            List<Documenti_Allievi> docs = e.getDocAllievo(a);
            MascheraM5 m5_allievo = e.getM5_byAllievo(a);
            if(m5_allievo != null && m5_allievo.getDomanda_ammissione() != null){
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

    protected void getDocentiByPrg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<Docenti> docenti = e.getDocentiPrg(e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("idprogetto"))));
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(docenti));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getDocAllievoById(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            Documenti_Allievi doc = e.getEm().find(Documenti_Allievi.class, Long.parseLong(request.getParameter("iddocumento")));
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(doc));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getSE_Prestiti(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<Selfiemployment_Prestiti> se_p = e.listaSE_P();
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(se_p));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getSIGMA(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<StatoPartecipazione> sp = e.lista_StatoPartecipazione();
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(sp));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void searchProgettiDocente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            Docenti d = e.getEm().find(Docenti.class, Long.parseLong(request.getParameter("iddocente")));
            List<ProgettiFormativi> list = d.getProgetti().stream().sorted((x, y) -> x.getEnd().compareTo(y.getEnd())).limit(50).collect(Collectors.toList());
            writeJsonResponse(response, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getRegistriDay(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<DocumentiPrg> list = e.getRegistriDay(e.getEm()
                    .find(ProgettiFormativi.class, Long.parseLong(request.getParameter("idprogetto"))),
                    new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("giorno")));
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(list));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void checkRegistroAlievoExist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            Documenti_Allievi registro = e.registriByAllievoAndDay(e.getEm().find(Allievi.class, Long.parseLong(request.getParameter("idallievo"))),
                    new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("giorno")));
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(registro));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getConversationSA(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<Faq> list = e.getFaqSoggetto(((User) request.getSession().getAttribute("user")).getSoggettoAttuatore());
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(list));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getCalendarioModello(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<LezioneCalendario> red = Utility.grouppedByLezione(e.getLezioniByModello(Integer.parseInt(request.getParameter("modello"))));
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Include.NON_NULL);
            response.getWriter().write(mapper.writeValueAsString(red));
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
            mapper.setSerializationInclusion(Include.NON_NULL);
            response.getWriter().write(mapper.writeValueAsString(list));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }
    
    protected void loadInfoM6(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("id")));
            ModelliPrg m6 = Utility.filterModello6(p.getModelli());
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Include.NON_NULL);
            response.getWriter().write(mapper.writeValueAsString(m6));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getAllieviByProgetto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        e.begin();
        try {
            ProgettiFormativi p  = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("id")));
            List<Allievi> a = e.getAllieviProgettiFormativi(p);
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
                mapper.setSerializationInclusion(Include.NON_NULL);
                response.getWriter().write(mapper.writeValueAsString(list));
            } else {
                //SETTO I NEETS CHE NON RAGGIUNGONO LE 36 HH CON GRUPPO -1
                Long hh36 = new Long(129600000);
                Map<Long, Long> oreRendicontateFaseA = Action.OreRendicontabiliAlunni_faseA(p.getId().intValue());
                for (Allievi as : a) {
                    if (oreRendicontateFaseA.get(as.getId()) != null && oreRendicontateFaseA.get(as.getId()).compareTo(hh36) < 0) {
                        as.setGruppo_faseB(-1);
                        e.merge(as);
                    }
                }
                e.merge(p);
                e.commit();
                
                List<Allievi> list = new ArrayList();
                for (Allievi al : a) {
                    list.add(new Allievi(al.getId(), al.getNome(), al.getCognome(), al.getGruppo_faseB()));
                }
                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(Include.NON_NULL);
                response.getWriter().write(mapper.writeValueAsString(list));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void getMembriStaff(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class, Long.parseLong(request.getParameter("idprogetto")));
            List<StaffModelli> list = pf.getStaff_modelli().stream().filter(m -> m.getAttivo() == 1).collect(Collectors.toList());
            for (StaffModelli s : list) {
                s.setProgetto(null);
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Include.NON_NULL);
            response.getWriter().write(mapper.writeValueAsString(list));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void checkModello4Start(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<ProgettiLezioniModelli> list = new ArrayList();
            User us = (User) request.getSession().getAttribute("user");
            ModelliPrg m3;
            int nroLezioniCalendario = e.getLezioniByModello(3).size();
            for (ProgettiFormativi p : us.getSoggettoAttuatore().getProgettiformativi()) {
                m3 = Utility.filterModello3(p.getModelli());
                if (m3 != null && (m3.getStato().equalsIgnoreCase("R") || m3.getStato().equalsIgnoreCase("OK")) && m3.getLezioni().size() == nroLezioniCalendario) {
                    Date filter_lastDay = m3.getLezioni().stream().map(Lezioni_Modelli::getGiorno).max(Date::compareTo).get();
                    list.add(new ProgettiLezioniModelli(p.getId(), filter_lastDay));
                }
            }
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(list));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void checkModello4End(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        try {
            List<ProgettiLezioniModelli> list = new ArrayList();
            User us = (User) request.getSession().getAttribute("user");
            ModelliPrg m4;
            int nroLezioniCalendario = e.getLezioniByModello(4).size();
            int nroGruppi;
            for (ProgettiFormativi p : us.getSoggettoAttuatore().getProgettiformativi()) {
                nroGruppi = Utility.numberGroupsModello4(p);
                m4 = Utility.filterModello4(p.getModelli());
                if (m4 != null && (m4.getStato().equalsIgnoreCase("R") || m4.getStato().equalsIgnoreCase("OK")) && m4.getLezioni().size() == (nroLezioniCalendario * nroGruppi)) {
                    Date filter_lastDay = m4.getLezioni().stream().map(Lezioni_Modelli::getGiorno).max(Date::compareTo).get();
                    list.add(new ProgettiLezioniModelli(p.getId(), filter_lastDay));
                }
            }
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(list));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            e.close();
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        User us = (User) request.getSession().getAttribute("user");
        if (us != null && (us.getTipo() == 1 || us.getTipo() == 3)) {
            String type = request.getParameter("type");
            switch (type) {
                case "searchAllievi":
                    searchAllievi(request, response);
                    break;
                case "searchDocenti":
                    searchDocenti(request, response);
                    break;
                case "searchProgetti":
                    searchProgetti(request, response);
                    break;
                case "searchAllieviProgetti":
                    searchAllieviProgetti(request, response);
                    break;
                case "getDocPrg":
                    getDocPrg(request, response);
                    break;
                case "searchDocentiProgetti":
                    searchDocentiProgetti(request, response);
                    break;
                case "getDocAllievo":
                    getDocAllievo(request, response);
                    break;
                case "searchProgettiDocente":
                    searchProgettiDocente(request, response);
                    break;
                case "getDocentiByPrg":
                    getDocentiByPrg(request, response);
                    break;
                case "getDocAllievoById":
                    getDocAllievoById(request, response);
                    break;
                case "getSE_Prestiti":
                    getSE_Prestiti(request, response);
                    break;
                case "getSIGMA":
                    getSIGMA(request, response);
                    break;
                case "getRegistriDay":
                    getRegistriDay(request, response);
                    break;
                case "checkRegistroAlievoExist":
                    checkRegistroAlievoExist(request, response);
                    break;
                case "getConversationSA":
                    getConversationSA(request, response);
                    break;
                case "getCalendarioModello":
                    getCalendarioModello(request, response);
                    break;
                case "getLezioniByProgetto":
                    getLezioniByProgetto(request, response);
                    break;
                case "getAllieviByProgetto":
                    getAllieviByProgetto(request, response);
                    break;
                case "getMembriStaff":
                    getMembriStaff(request, response);
                    break;
                case "checkModello4Start":
                    checkModello4Start(request, response);
                    break;
                case "checkModello4End":
                    checkModello4End(request, response);
                    break;
                case "loadInfoM6":
                    loadInfoM6(request, response);
                    break;
                default:
                    break;
            }
        }
    }

    /*  metodi  */
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
