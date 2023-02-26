
import it.refill.db.Action;
import it.refill.db.Entity;
import it.refill.domain.Allievi;
import it.refill.domain.ProgettiFormativi;
import it.refill.util.Pdf_new;
import it.refill.util.Utility;
import java.io.File;
import java.util.Map;
import org.joda.time.DateTime;

//import com.mailjet.client.errors.MailjetException;
//import it.refill.util.SendMailJet;
//import static it.refill.util.Utility.estraiSessodaCF;
//import static it.refill.util.Utility.roundFloatAndFormat;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import it.refill.db.Entity;
/**
 *
 * @author rcosco
 */
public class Test {

    public static void main(String[] args) {

        String idpr = "586";
        String idall = "1893";
        String usernameSA = "INFO96";

        Entity e = new Entity();
        e.begin();
        ProgettiFormativi prg = e.getEm().find(ProgettiFormativi.class,
                Long.valueOf(idpr));
        Allievi al = e.getEm().find(Allievi.class,
                Long.valueOf(idall));

        Map<Long, Long> oreRendicontabili = Action.OreRendicontabiliAlunni((int) (long) prg.getId());
        File f7 = Pdf_new.MODELLO7(e, usernameSA, al, Utility.roundFloatAndFormat(oreRendicontabili.get(al.getId()), true),
                new DateTime(), true);
        System.out.println(f7.getPath());
        e.close();

//        Pdf_
//        System.out.println(estraiSessodaCF("CPSBGI93M09A512I"));
    }

//    public static void main(String[] args) {
//        System.out.println(roundFloatAndFormat(100000L, true));
//    }
//    public static void main(String[] args) {
//        try {
//            SendMailJet.sendMail("testing", new String[]{"raffaele.cosco@faultless.it"}, "TESTO", "testing oggetto");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//    public static void main(String[] args) {
//        Entity e = new Entity();
//        ProgettiFormativi prg = e.getEm().find(ProgettiFormativi.class, 3L);
////            prg.setImporto(Double.valueOf(prezzo.replaceAll("[._]", "").replace(",", ".").trim()));
////            e.merge(prg);
////            e.commit();
//
//        //14-10-2020 MODIFICA - TOGLIERE IMPORTO CHECKLIST
//        double ore_convalidate = 0;
//        for (DocumentiPrg d : prg.getDocumenti().stream().filter(p -> p.getGiorno() != null).collect(Collectors.toList())) {
//            ore_convalidate += d.getOre_convalidate();
//        }
//        for (Allievi a : prg.getAllievi()) {
//            for (Documenti_Allievi d : a.getDocumenti().stream().filter(p -> p.getGiorno() != null).collect(Collectors.toList())) {
//                try {
//                    ore_convalidate += d.getOrericonosciute() == null ? 0 : d.getOrericonosciute();
//                } catch (Exception ex) {
//                    System.out.println(d.getId() + " " + d.getAllievo().getCognome() + " " + d.getGiorno() + " " + d.getOrericonosciute());
//                }
//            }
//        }
//        e.close();
//
//        System.out.println(ore_convalidate);
//    }
}
