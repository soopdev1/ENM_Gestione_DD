
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
