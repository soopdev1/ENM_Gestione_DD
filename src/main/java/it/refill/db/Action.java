/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.db;

import it.refill.domain.Docenti;
import it.refill.domain.ProgettiFormativi;
import it.refill.domain.SoggettiAttuatori;
import it.refill.domain.User;
import it.refill.entity.FadCalendar;
import static it.refill.util.Utility.pregresso;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.persistence.TypedQuery;
import static org.apache.commons.io.FilenameUtils.separatorsToSystem;

/**
 *
 * @author rcosco
 */
public class Action {

    public static File createFile_R(String path) {
        try {
            File out = new File(separatorsToSystem(path));
            if (out.exists() && out.canRead()) {
                return out;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static boolean isVisibile(String gruppo, String page) {//(tipo, pagina)
        Entity e = new Entity();
        boolean out = e.isVisible(gruppo, page);
        e.close();
        return out;
    }

    public static boolean isModifiable(String modificabile, String stato) {//usato anche per la visualizzazione dei modelli
        if (modificabile != null) {
            StringTokenizer st = new StringTokenizer(modificabile, "-");
            while (st.hasMoreTokens()) {
                if (stato.equals(st.nextToken())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int countPregresso() {
        if (!pregresso) {
            return 0;
        }
        Database db = new Database(false, false);
        if (db.getC() == null) {
            return 0;
        }
        int c = db.countPregresso();
        db.closeDB();
        return c;
    }

    public static List<FadCalendar> calendarioFAD(String id) {
        List<FadCalendar> out = new ArrayList<>();
        Database db = new Database(false, false);
        if (db.getC() == null) {
            return out;
        }
        out = db.calendarioFAD(id);
        db.closeDB();
        return out;
    }

    public static String linkFAD() {
        Database db = new Database(false, false);
        if (db.getC() == null) {
            return "https://accreditamento.diventaimprenditore.eu/fad_ded/";
        }
        String out = db.getPathtemp("linkfad");
        db.closeDB();
        return out;
    }
    
    public static String[] contatoriHomeSA(User us) {
        String[] out = {
            "0", "0", "0",
            "0"
        };

        AtomicInteger out_0 = new AtomicInteger(0);
        AtomicInteger out_1 = new AtomicInteger(0);
        Long hh36 = new Long(129600000);

        Entity e = new Entity();
        e.begin();
        List<ProgettiFormativi> allpr = e.ProgettiSAOrdered(us.getSoggettoAttuatore());
        e.close();

        allpr.forEach(pr1 -> {
            out_0.addAndGet(pr1.getAllievi().size());
        });

        List<ProgettiFormativi> conclusi = allpr.stream().filter(pr
                -> pr.getStato().getId().equalsIgnoreCase("F")
                || pr.getStato().getId().equalsIgnoreCase("DVB")
                || pr.getStato().getId().equalsIgnoreCase("MA")
                || pr.getStato().getId().equalsIgnoreCase("IV")
                || pr.getStato().getId().equalsIgnoreCase("CK")
                || pr.getStato().getId().equalsIgnoreCase("EVI")
                || pr.getStato().getId().contains("CO")
        ).collect(Collectors.toList());

        conclusi.forEach(pr1 -> {
            Map<Long, Long> orealunni = OreRendicontabiliAlunni_faseA(Integer.parseInt(String.valueOf(pr1.getId())));
            pr1.getAllievi().forEach(al1 -> {
                if (al1.getStatopartecipazione().getId().equals("01")) {
                    if (orealunni.get(al1.getId()) != null) {
                        if (orealunni.get(al1.getId()) >= hh36) {
                            out_1.addAndGet(1);
                        }
                    }
                }
            });
        });

        out[0] = String.valueOf(out_0.get());
        out[1] = String.valueOf(out_1.get());
        out[2] = String.valueOf(allpr.size());
        out[3] = String.valueOf(conclusi.size());

        return out;
    }

    public static String[] contatoriHome() {

        String[] out = {
            "0", "0", "0",
            "0", "0", "0",
            "0", "0", "0",
            "0", "0", "0",
            "0", "0", "0",
            "0", "0", "0",
            "0", "0", "0"
        };
        Long hh36 = new Long(129600000);
        Entity e = new Entity();
        e.begin();

        TypedQuery<ProgettiFormativi> q = e.getEm().createQuery("SELECT p FROM ProgettiFormativi p", ProgettiFormativi.class);
        TypedQuery<SoggettiAttuatori> q2 = e.getEm().createNamedQuery("sa.listaSA", SoggettiAttuatori.class);
        TypedQuery<Docenti> q3 = e.getEm().createNamedQuery("d.All", Docenti.class);
        List<ProgettiFormativi> allpr = q.getResultList().isEmpty() ? new ArrayList() : q.getResultList();
        List<SoggettiAttuatori> allsa = q2.getResultList().isEmpty() ? new ArrayList() : q2.getResultList();
        List<Docenti> alldo = q3.getResultList().isEmpty() ? new ArrayList() : q3.getResultList();

        List<ProgettiFormativi> davalidare = allpr.stream().filter(pr
                -> (pr.getStato().getId().equalsIgnoreCase("DV")
                || pr.getStato().getId().equalsIgnoreCase("DC")))
                .collect(Collectors.toList());

        List<ProgettiFormativi> validati = allpr.stream().filter(pr
                -> !pr.getStato().getId().equalsIgnoreCase("DV")
                && !pr.getStato().getId().equalsIgnoreCase("DC")
                && !pr.getStato().getId().contains("E")
                && !pr.getStato().getTipo().equalsIgnoreCase("errore")
                && !pr.getStato().getTipo().equalsIgnoreCase("chiuso")
                && !pr.getStato().getTipo().equalsIgnoreCase("sospeso")
        )
                .collect(Collectors.toList());
        
        List<ProgettiFormativi> conclusi = allpr.stream().filter(pr
                -> pr.getStato().getId().equalsIgnoreCase("F")
                || pr.getStato().getId().equalsIgnoreCase("DVB")
                || pr.getStato().getId().equalsIgnoreCase("MA")
                || pr.getStato().getId().equalsIgnoreCase("IV")
                || pr.getStato().getId().equalsIgnoreCase("CK")
                || pr.getStato().getId().equalsIgnoreCase("EVI")
                || pr.getStato().getId().contains("CO")
        ).collect(Collectors.toList());

        AtomicInteger out_0 = new AtomicInteger(0);
        AtomicInteger out_1 = new AtomicInteger(0);
        AtomicInteger out_2 = new AtomicInteger(0);
        AtomicInteger out_3 = new AtomicInteger(0);
        AtomicInteger out_5 = new AtomicInteger(0);
        AtomicInteger out_6 = new AtomicInteger(0);
        AtomicInteger out_9 = new AtomicInteger(0);
        AtomicInteger out_10 = new AtomicInteger(0);
        AtomicInteger out_11 = new AtomicInteger(0);
        AtomicInteger out_12 = new AtomicInteger(0);
        AtomicInteger out_13 = new AtomicInteger(0);
        AtomicInteger out_14 = new AtomicInteger(0);
        AtomicInteger out_15 = new AtomicInteger(0);
        AtomicInteger out_16 = new AtomicInteger(0);
        try {

            allpr.forEach(pr1 -> {
                if (pr1.getStato().getId().equals("DV")) {
                    out_9.addAndGet(1);
                } else if (pr1.getStato().getId().equals("P")) {
                    out_10.addAndGet(1);
                } else if (pr1.getStato().getId().equals("DC")) {
                    out_11.addAndGet(1);
                } else if (pr1.getStato().getId().equals("ATA")) {
                    out_12.addAndGet(1);
                } else if (pr1.getStato().getId().equals("DVA")) {
                    out_13.addAndGet(1);
                } else if (pr1.getStato().getId().equals("ATB")) {
                    out_14.addAndGet(1);
                } else if (pr1.getStato().getId().equals("DVB")) {
                    out_15.addAndGet(1);
                } else if (pr1.getStato().getTipo().equals("chiuso")) {
                    out_16.addAndGet(1);
                }
            });

            alldo.forEach(pr1 -> {
                if (pr1.getStato().equals("DV")) {
                    out_5.addAndGet(1);
                } else if (pr1.getStato().equals("A")) {
                    out_6.addAndGet(1);
                }
            });

            conclusi.forEach(pr1 -> {
                Map<Long, Long> orealunni = OreRendicontabiliAlunni_faseA(Integer.parseInt(String.valueOf(pr1.getId())));
                pr1.getAllievi().forEach(al1 -> {
                    if (al1.getStatopartecipazione().getId().equals("01")) {
                        if (orealunni.get(al1.getId()) != null) {
                            if (orealunni.get(al1.getId()) >= hh36) {
                                out_2.addAndGet(1);
                            }
                        }
                    }
                });
            });

            validati.forEach(pr1 -> {
                pr1.getAllievi().forEach(al1 -> {
                    if (al1.getStatopartecipazione().getId().equals("01")) {
                        out_1.addAndGet(1);
                    }
                });
            });

            davalidare.forEach(pr1 -> {

//                    int totale = pr1.getAllievi().size();
                pr1.getAllievi().forEach(al1 -> {
                    if (al1.getEsclusione_prg() == null || !al1.getEsclusione_prg().equals("APPROVATO")) {
                        if (al1.getStatopartecipazione().getId().equals("01")) {
                            out_0.addAndGet(1);
                        }
                    } else {
                        out_1.addAndGet(1);
                    }
                });
            });

            Database db1 = new Database(true, false);
            List<SoggettiAttuatori> list = db1.estrai_SA_accettare(e);
            db1.closeDB();
            List<String> list2 = e.getSoggettiAttuatori().stream().map(r -> r.getPiva()).collect(Collectors.toList());
            list.forEach(s1 -> {
                if (!list2.contains(s1.getPiva())) {
                    out_3.addAndGet(1);
                }
            });

        } catch (Exception ex) {
            //ex.printStackTrace();
        }

        out[0] = String.valueOf(out_0.get());
        out[1] = String.valueOf(out_1.get());
        out[2] = String.valueOf(out_2.get());
        out[3] = String.valueOf(out_3.get());
        out[4] = String.valueOf(allsa.size());
        out[5] = String.valueOf(out_5.get());
        out[6] = String.valueOf(out_6.get());
        out[7] = String.valueOf(validati.size());
        out[8] = String.valueOf(conclusi.size());
        out[9] = String.valueOf(out_9.get());
        out[10] = String.valueOf(out_10.get());
        out[11] = String.valueOf(out_11.get());
        out[12] = String.valueOf(out_12.get());
        out[13] = String.valueOf(out_13.get());
        out[14] = String.valueOf(out_14.get());
        out[15] = String.valueOf(out_15.get());
        out[16] = String.valueOf(out_16.get());
        e.close();
        return out;

    }

    public static Map<Long, Long> OreRendicontabiliDocentiFASEA(int pf) {
        Database db = new Database(false, false);
        Map<Long, Long> r = db.OreRendicontabiliDocentiFASEA(pf);
        db.closeDB();
        return r;
    }
    //Totale Ore rendicontabili per Docenti

    public static Map<Long, Long> OreRendicontabiliDocenti(int pf) {
        Database db = new Database(false, false);
        Map<Long, Long> r = db.OreRendicontabiliDocenti(pf);
        db.closeDB();
        return r;
    }

    //Totale Ore rendicontabili per Maschera Modello 5
    public static Map<Long, Long> OreRendicontabiliAlunni(int pf) {
        Database db = new Database(false, false);
        Map<Long, Long> r = db.OreRendicontabiliAlunni(pf);
        db.closeDB();
        return r;
    }

    public static Map<Long, Long> OreRendicontabiliAlunni_faseB(int pf) {
        Database db = new Database(false, false);
        Map<Long, Long> r = db.OreRendicontabiliAlunni_faseB(pf);
        db.closeDB();
        return r;
    }

    public static Map<Long, Long> OreRendicontabiliAlunni_faseA(int pf) {
        Database db = new Database(false, false);
        Map<Long, Long> r = db.OreRendicontabiliAlunni_faseA(pf);
        db.closeDB();
        return r;
    }

    public static String[] dati_modello5_neet(String idded, String idsa, String pf) {
        Database db = new Database(false, false);
        String[] r = db.dati_modello5_neet(idded, idsa, pf);
        db.closeDB();
        return r;
    }

}
