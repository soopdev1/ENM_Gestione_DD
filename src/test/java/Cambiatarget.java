
import it.refill.db.Action;
import it.refill.db.Entity;
import it.refill.domain.Allievi;
import it.refill.domain.ModelliPrg;
import it.refill.domain.ProgettiFormativi;
import it.refill.util.Pdf_new;
import it.refill.util.Utility;
import java.io.File;
import java.util.Map;
import org.joda.time.DateTime;

//import it.refill.db.Entity;
//import it.refill.domain.ModelliPrg;
//import it.refill.domain.ProgettiFormativi;
//import it.refill.util.Pdf_new;
//import static it.refill.util.Utility.filterModello6;
//import java.io.File;
//import org.joda.time.DateTime;
//import it.refill.db.Entity;
//import it.refill.domain.Lezioni_Modelli;
//import it.refill.domain.ModelliPrg;
//import it.refill.domain.ProgettiFormativi;
//import it.refill.domain.StaffModelli;
//import static it.refill.util.Pdf_new.MODELLO4_BASE;
//import static it.refill.util.Pdf_new.checkFirmaQRpdfA;
//import it.refill.util.Utility;
//import java.io.File;
//import java.util.List;
//import java.util.stream.Collectors;
//import org.joda.time.DateTime;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rcosco
 */
public class Cambiatarget {

//    public static void main(String[] args) {
//        
//        
////        File downloadFile = null;
////        try {
////            Entity e = new Entity();
////            ProgettiFormativi pf = e.getEm().find(ProgettiFormativi.class,
////                    110L);
////            
////            System.out.println("Pdf.main() "+pf.getModelli());
////            
////            ModelliPrg m6 = filterModello6(pf.getModelli());
////            if (m6 != null) {
////                downloadFile = Pdf_new.MODELLO6(e,
////                        "AMMINISTRAZIONE",
////                        pf.getSoggetto(),
////                        pf, m6, new DateTime(), true);
////                System.out.println("Pdf.main() "+downloadFile.getPath());
////            }
////        } catch (Exception ex) {
////            ex.printStackTrace();
////        }        
////        Entity e = new Entity();
////
////        ProgettiFormativi p = e.getEm().find(ProgettiFormativi.class, Long.parseLong("82"));
////        List<StaffModelli> staff = p.getStaff_modelli().stream().filter(m -> m.getAttivo() == 1).collect(Collectors.toList());
////        ModelliPrg m4 = Utility.filterModello4(p.getModelli());
////        List<Lezioni_Modelli> lezioni = m4.getLezioni();
////
////        File f1 = MODELLO4_BASE(e,
////                "ASSEIMPRENDITORI", p.getSoggetto(),
////                p,
////                p.getAllievi().stream().filter(al1 -> al1.getGruppo_faseB() > 0).collect(Collectors.toList()),
////                p.getDocenti(),
////                lezioni,
////                staff,
////                new DateTime(), true);
////
////        System.out.println(f1.getPath());
////
//    }
    public static void main(String[] args) {

        String idpr = "537";
//        String idall = "1260";
        String usernameSA = "GIANLUCAMILLESIMI147";

        Entity e = new Entity();
        e.begin();
        ProgettiFormativi prg = e.getEm().find(ProgettiFormativi.class,
                Long.parseLong(idpr));
//        Allievi al = e.getEm().find(Allievi.class,
//                Long.parseLong(idall));

//        ModelliPrg m3 = Utility.filterModello3(prg.getModelli());
//        ModelliPrg m4 = Utility.filterModello4(prg.getModelli());
//        Map<Long, Long> allievi_m5 = Utility.allieviM5_loaded(e.getM5Loaded_byPF(prg));
//        MascheraM5 m5 = e.getEm().find(MascheraM5.class, allievi_m5.get(al.getId()));
//        TipoDoc_Allievi tipodoc_m5;
//        if (m5.isTabella_premialita()) {
//            tipodoc_m5 = e.getEm().find(TipoDoc_Allievi.class, 21L);
//        } else {
//            tipodoc_m5 = e.getEm().find(TipoDoc_Allievi.class, 20L);
//        }
//
//        String[] datifrequenza = Action.dati_modello5_neet(
//                String.valueOf(al.getId()),
//                String.valueOf(prg.getSoggetto().getId()),
//                String.valueOf(m5.getProgetto_formativo().getId()));
//
//        File f5 = Pdf_new.MODELLO5(e,
//                tipodoc_m5.getModello(),
//                usernameSA,
//                prg.getSoggetto(),
//                al,
//                datifrequenza,
//                m5,
//                new DateTime(), true);
//
//        System.out.println(f5.getPath());
//        File f1 = Pdf_new.MODELLO1(e, "3", usernameSA, prg.getSoggetto(), al, new DateTime(), true, true);
//        System.out.println(f1.getPath());
//        File f2 = Pdf_new.MODELLO2(e,
//                            "1",
//                            usernameSA, prg.getSoggetto(),
//                            prg,
//                            prg.getAllievi().stream().filter(a1-> a1.getStatopartecipazione().getId().equals("01")).collect(Collectors.toList()) , new DateTime(), true);
//        
//        System.out.println(f2.getPath());
//        File f3 = Pdf_new.MODELLO3(e,
//                            usernameSA,
//                            prg.getSoggetto(),
//                            prg,
//                            prg.getAllievi().stream().filter(p1 -> p1.getStatopartecipazione().getId().equals("01")).collect(Collectors.toList()),
//                            prg.getDocenti(), m3.getLezioni(), prg.getStaff_modelli().stream().filter(m -> m.getAttivo() == 1).collect(Collectors.toList()),
//                            new DateTime(), true);
//        System.out.println(f3.getPath());
//        File f4 = Pdf_new.MODELLO4(e, usernameSA, prg.getSoggetto(), prg, prg.getAllievi().stream().filter(p1 -> p1.getStatopartecipazione().getId().equals("01")).collect(Collectors.toList()),
//                prg.getDocenti(), m4.getLezioni(), prg.getStaff_modelli().stream().filter(m -> m.getAttivo() == 1).collect(Collectors.toList()), new DateTime(), true);
//        
//        System.out.println(f4.getPath());
//        ModelliPrg m6 = Utility.filterModello6(prg.getModelli());
//        if (m6 != null) {
//            File f6 = Pdf_new.MODELLO6(e,
//                    usernameSA,
//                    prg.getSoggetto(),
//                    prg, m6, new DateTime(), true);
//            System.out.println(f6.getPath());
//        }

//        Map<Long, Long> oreRendicontabili = Action.OreRendicontabiliAlunni((int) (long) prg.getId());
//        File f7 = Pdf_new.MODELLO7(e, usernameSA, al, Utility.roundFloatAndFormat(oreRendicontabili.get(al.getId()), true),
//                new DateTime(), true);
//        System.out.println(f7.getPath());
        e.close();
//        
//        String o = Pdf_new.checkFirmaQRpdfA("MODELLO1", "", new File("C:\\Users\\Administrator\\Desktop\\da caricare\\INFO05_MOISE_CLAUDIASILVIA_041120211144476.M1_pdfA.pdf"), "", "20;0;60;60");
//        System.out.println(o);
    }

}
