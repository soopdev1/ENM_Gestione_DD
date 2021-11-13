
import it.refill.db.Entity;
import it.refill.domain.ModelliPrg;
import it.refill.domain.ProgettiFormativi;
import it.refill.util.Pdf_new;
import static it.refill.util.Utility.filterModello6;
import java.io.File;
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
public class Pdf {

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
        
        String o = Pdf_new.checkFirmaQRpdfA("MODELLO1", "", new File("C:\\Users\\Administrator\\Desktop\\da caricare\\INFO05_MOISE_CLAUDIASILVIA_041120211144476.M1_pdfA.pdf"), "", "20;0;60;60");
        System.out.println(o);
    }
                    
}
