
import static it.refill.util.Pdf_new.checkFirmaQRpdfA;
import java.io.File;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rcosco
 */
public class R {
    public static void main(String[] args) {
        System.out.println(checkFirmaQRpdfA("MODELLO3", "", new File("C:\\Users\\raf\\Desktop\\SEGRETERIAGENERALE_ULE-UNIONELAVORATORIEUROPEI_131020211011244.M3_pdfA-signed.pdf"), "", "20;0;60;60"));
    }
}
