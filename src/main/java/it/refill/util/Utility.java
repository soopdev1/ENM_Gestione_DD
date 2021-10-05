/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Splitter.on;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import it.refill.db.Action;
import it.refill.db.FileDownload;
import it.refill.domain.Allievi;
import it.refill.domain.Docenti;
import it.refill.domain.DocumentiPrg;
import it.refill.domain.Documenti_Allievi;
import it.refill.domain.Documenti_Allievi_Pregresso;
import it.refill.domain.Documenti_UnitaDidattiche;
import it.refill.domain.LezioneCalendario;
import it.refill.domain.Lezioni_Modelli;
import it.refill.domain.MascheraM5;
import it.refill.domain.ModelliPrg;
import it.refill.domain.ProgettiFormativi;
import it.refill.domain.SoggettiAttuatori;
import it.refill.domain.StaffModelli;
import it.refill.domain.StatiPrg;
import it.refill.domain.TipoDoc;
import it.refill.domain.TipoDoc_Allievi;
import it.refill.entity.Item;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import static java.util.Locale.ITALY;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import static org.joda.time.format.DateTimeFormat.forPattern;
import org.joda.time.format.DateTimeFormatter;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Paths.get;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.joda.time.DateTimeComparator;
import org.joda.time.Period;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author agodino
 */
public class Utility {

    // TEST //
    public static boolean test = false;
    //////////

    //ADD RAF
    public static final int maxQueryResult = 5000;
    public static final String patternComplete = "yyMMddHHmmssSSS";
    public static final String patternSql = "yyyy-MM-dd";
    public static final String patternITA = "dd/MM/yyyy";
    public static final String patternFile = "yyyyMMdd";
    public static final String patternITACOMPLETE = "dd/MM/yyyy HH:mm:ss";
    public static final String patternHHMM = "HH:mm";
    public static final SimpleDateFormat sdfITA = new SimpleDateFormat(patternITA);
    public static final SimpleDateFormat sdfITAC1 = new SimpleDateFormat(patternITACOMPLETE);
    public static final SimpleDateFormat sdfHHMM = new SimpleDateFormat(patternHHMM);
    public static final NumberFormat numITA = NumberFormat.getCurrencyInstance(Locale.ITALY);
    public static boolean pregresso = false;

    //END RAF
    public static void redirect(HttpServletRequest request, HttpServletResponse response, String destination) throws ServletException, IOException {
        if (response.isCommitted()) {
            RequestDispatcher dispatcher = request.getRequestDispatcher(destination);
            dispatcher.forward(request, response);
        } else {
            response.sendRedirect(destination);
        }
    }

    public static void printRequest(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            for (String paramValue : paramValues) {
                System.out.println(paramName + " : " + new String(paramValue.getBytes(Charsets.ISO_8859_1), Charsets.UTF_8));
            }
        }
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            try {
                FileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                List items = upload.parseRequest((RequestContext) request);
                Iterator iterator = items.iterator();
                while (iterator.hasNext()) {
                    FileItem item = (FileItem) iterator.next();
                    if (item.isFormField()) {
                        String fieldName = item.getFieldName();
                        String value = new String(item.getString().getBytes(Charsets.ISO_8859_1), Charsets.UTF_8);
                        System.out.println("MULTIPART FIELD - " + fieldName + " : " + value);
                    } else {
                        String fieldName = item.getFieldName();
                        String fieldValue = item.getName();
                        System.out.println("MULTIPART FILE - " + fieldName + " : " + fieldValue);
                    }
                }
            } catch (FileUploadException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String formatStringtoStringDate(String dat, String pattern1, String pattern2) {
        try {
            return new SimpleDateFormat(pattern2).format(new SimpleDateFormat(pattern1).parse(dat));
        } catch (ParseException e) {
        }
        return "No correct date";
    }

    public static List<Date> getListDates(Date startDate, Date endDate) {
        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        while (calendar.before(endCalendar) || calendar.equals(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }

    public static String convMd5(String psw) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(psw.getBytes());
            byte byteData[] = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString().trim();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "-";
        }
    }

    public static String getRandomString(int length) {
        boolean useLetters = true;
        boolean useNumbers = false;
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }

    public static String correctName(String ing) {
        ing = correggiusername(ing);
        return ing.replaceAll("-", "");
    }

    public static String[] stylePF(Map<String, String[]> map, ProgettiFormativi pf) {
        String[] values = map.get(pf.getStato().getTipo()) == null ? (new String[]{"<b style='color: #363a90;'>Stato progetto</b> : @stato", "color: #363a90;", "background-color: rgba(93, 120, 255, 0.07) !important;"})
                : map.get(pf.getStato().getTipo());
        return values;
    }

    public static Map<String, String[]> mapStyles() {
        Map<String, String[]> ms = new HashMap();
        ms.put("chiuso", new String[]{"<b style='color: #363a90;'>Stato progetto</b> : @stato<br>Procedere con l'invio della documentazione di chiusura<br><b>@giorni</b>", "color: #0abb87;", "background-color: rgba(10, 187, 135, 0.07) !important;"});
        ms.put("errore", new String[]{"<b style='color: #363a90;'>Stato progetto</b> : @stato<br>Progetto formativo in stato di errore", "color: #c30041;", "background-color: rgba(253, 57, 122, 0.07) !important;"});
        ms.put("controllare", new String[]{"<b style='color: #363a90;'>Stato progetto</b> : @stato<br>In attesa di verifica da parte del Microcredito", "color: #eaa21c;", "background-color: rgba(255, 184, 34, 0.07) !important;"});
        return ms;
    }

    public static String[] styleMicro(StatiPrg sp) {
        Map<String, String> ms = new HashMap();
        /*fa-times-circle fa-exclamation-circle fa-check-circle fa-clock*/
        ms.put("chiuso", "color: #0abb87;");
        ms.put("errore", "color: #c30041;");
        ms.put("controllare", "color: #eaa21c;");
        return new String[]{ms.get(sp.getTipo()) == null ? "color: #363a90;" : ms.get(sp.getTipo()), sp.getDescrizione().equalsIgnoreCase(sp.getDe_tipo()) ? sp.getDescrizione() : sp.getDescrizione() + " - " + sp.getDe_tipo()};
    }

    public static String correggiusername(String ing) {
        if (ing != null) {
            ing = ing.replaceAll("\\\\", "");
            ing = ing.replaceAll("\n", "");
            ing = ing.replaceAll("\r", "");
            ing = ing.replaceAll("\t", "");
            ing = ing.replaceAll("'", "");
            ing = ing.replaceAll("“", "");
            ing = ing.replaceAll("‘", "");
            ing = ing.replaceAll("”", "");
            ing = ing.replaceAll("\"", "");
            ing = ing.replaceAll(" ", "_");
            return ing.replaceAll("\"", "");
        } else {
            return "-";
        }
    }

    public static String correggi(String ing) {
        if (ing != null) {
            ing = ing.replaceAll("\\\\", "/");
            ing = ing.replaceAll("\n", "");
            ing = ing.replaceAll("\r", "");
            ing = ing.replaceAll("\t", "");
            ing = ing.replaceAll("'", "\'");
            ing = ing.replaceAll("“", "\'");
            ing = ing.replaceAll("‘", "\'");
            ing = ing.replaceAll("”", "\'");
            ing = ing.replaceAll("\"", "/");
            return ing.replaceAll("\"", "\'");
        } else {
            return "-";
        }
    }

    public static String CaratteriSpeciali(String ing) {
        if (ing != null) {
            ing = StringUtils.replace(ing, "Ã©", "è");
            ing = StringUtils.replace(ing, "Ã¨", "é");
            ing = StringUtils.replace(ing, "Ã¬", "ì");
            ing = StringUtils.replace(ing, "Ã²", "ò");
            ing = StringUtils.replace(ing, "Ã¹", "ù");
            ing = StringUtils.replace(ing, "Ã", "à");
            return ing;
        } else {
            return "-";
        }
    }

    public static String ctrlCheckbox(String check) {
        return check == null ? "NO" : "SI";
    }

    public static String UniqueUser(Map<String, String> usernames, String username) {
        Random rand = new Random();
        boolean ok = usernames.get(username) == null;
        while (!ok) {
            username += String.valueOf(rand.nextInt(10));
            ok = usernames.get(username) == null;
        }
        return username;
    }

    public static Map<StatiPrg, Long> GroupByAndCount(SoggettiAttuatori s) {
        return s.getProgettiformativi().stream().collect(Collectors.groupingBy(ProgettiFormativi::getStato, Collectors.counting()));
    }

    public static void sortDoc(List<DocumentiPrg> documeti) {
        documeti.sort((p1, p2) -> p1.getTipo().getId().compareTo(p2.getTipo().getId()));
    }

    public static void sortDoc_Pregresso(List<Documenti_Allievi_Pregresso> documeti) {
        documeti.sort((p1, p2) -> p1.getTipo().getId().compareTo(p2.getTipo().getId()));
    }

    /**
     * CONVERTE LA DATA IN FORMATO UTILIZZABILE IN JAVA UTIL A PARTIRE DALLA
     * DATA IN INGRESSO
     *
     * @param dat - DATA IN INGRESSO
     * @param pattern1 - PATTERN DATA
     * @return Date
     */
    public static Date getUtilDate(String dat, String pattern1) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern1);
            return formatter.parse(dat);
        } catch (ParseException ex) {
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
        }
        return null;
    }

    public static void writeJsonResponseR(HttpServletResponse response, Object list) {
        try {
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
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void setOreLezioni(Allievi a) {
        if (a.getProgetto() != null) {
            //ore Fase A
            for (DocumentiPrg d : a.getProgetto().getDocumenti().stream().filter(doc -> doc.getGiorno() != null && doc.getDeleted() == 0).collect(Collectors.toList())) {
                d.getPresenti_list().stream().filter((p) -> (a.getId() == p.getId())).forEachOrdered((p) -> {
                    a.setOre_fa(a.getOre_fa() + p.getOre_riconosciute());
                });
            }
            //ore Fase B
            for (Documenti_Allievi d : a.getDocumenti().stream().filter(doc -> doc.getGiorno() != null && doc.getDeleted() == 0).collect(Collectors.toList())) {
                a.setOre_fb(a.getOre_fb() + (d.getOrericonosciute() == null ? 0 : d.getOrericonosciute()));
            }
        }
    }

    public static String formatStringtoStringDate(String dat, String pattern1, String pattern2, boolean timestamp) {
        try {
            if (timestamp) {
                dat = dat.substring(0, dat.length() - 2);
            }
            if (dat.length() == pattern1.length()) {
                DateTimeFormatter fmt = forPattern(pattern1);
                DateTime dtout = fmt.parseDateTime(dat);
                return dtout.toString(pattern2, ITALY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "DATA ERRATA";
    }

    public static String estraiEccezione(Exception ec1) {
        try {
            String stack_nam = ec1.getStackTrace()[0].getMethodName();
            String stack_msg = ExceptionUtils.getStackTrace(ec1);
            return stack_nam + " - " + stack_msg;
        } catch (Exception e) {
        }
        return ec1.getMessage();

    }

    public static String getRequestValue(HttpServletRequest request, String fieldname) {
        String out = request.getParameter(fieldname);
        if (out == null || out.trim().equals("null")) {
            out = "";
        } else {
            out = out.trim();
        }
        return out;
    }

    public static String getRequestCheckbox(HttpServletRequest request, String fieldname) {
        String out = request.getParameter(fieldname);
        if (out == null) {
            return "NO";
        }
        return "SI";
    }

    public static boolean checkPDF(File pdffile) {
        if (pdffile.exists()) {
            try {
                int pag;
                try (InputStream is = new FileInputStream(pdffile); PdfReader pdfReader = new PdfReader(is)) {
                    PdfDocument pd = new PdfDocument(pdfReader);
                    pag = pd.getNumberOfPages();
                    pd.close();
                }
                return pag > 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static FileDownload preparefilefordownload(String path) {
        List<String> spl = on("###").splitToList(path);
        if (spl.size() == 3) {
            return new FileDownload(spl.get(0), spl.get(1), spl.get(2));
        }
        return null;
    }

    public static void createDir(String path) {
        try {
            createDirectories(get(path));
        } catch (Exception e) {
        }
    }

    public static List<Documenti_UnitaDidattiche> UDOrderByDate(List<Documenti_UnitaDidattiche> s) {
        s = s.stream().filter(o -> o.getDeleted() == 0).collect(Collectors.toList());
        s.sort(Comparator.comparing(o -> o.getData_modifica()));
        return s;
    }

    public static boolean[] LinksDocs_UD(List<Documenti_UnitaDidattiche> s, int maxfiles, int maxlinks) {
        s = s.stream().filter(o -> o.getDeleted() == 0).collect(Collectors.toList());
        int pdfs = maxfiles - (int) s.stream().filter(d -> d.getTipo().equalsIgnoreCase("PDF")).count();
        int links = maxlinks - (int) s.stream().filter(d -> d.getTipo().equalsIgnoreCase("LINK")).count();
        boolean verify[] = {pdfs == 0, links == 0};
        return verify;
    }

    public static String cp_toUTF(String ing) {
        try {
            String t = new String(ing.getBytes("Windows-1252"), "UTF-8");
            return t.trim();
        } catch (Exception ex) {

        }
        return ing;
    }

    public static int get_eta(Date nascita) {
        try {
            Period period = new Period(new DateTime(nascita.getTime()).withMillisOfDay(0), new DateTime().withMillisOfDay(0));
            return period.getYears();
        } catch (Exception e) {
        }
        return 0;
    }

    public static Map<Integer, List<LezioneCalendario>> groupByLezione(List<LezioneCalendario> l) {
        Map<Integer, List<LezioneCalendario>> byLezione = l.stream().collect(Collectors.groupingBy(t -> t.getLezione()));
        return byLezione;
    }

    public static List<LezioneCalendario> grouppedByLezione(List<LezioneCalendario> l) {
        Map<Integer, List<LezioneCalendario>> byLezione = l.stream().collect(Collectors.groupingBy(t -> t.getLezione()));

        List<LezioneCalendario> grouppedByLezione = new ArrayList();
        LezioneCalendario cl;
        for (Map.Entry<Integer, List<LezioneCalendario>> lez : byLezione.entrySet()) {
            cl = new LezioneCalendario();
            cl.setId(lez.getValue().get(0).getId());
            cl.setLezione(lez.getKey());
            cl.setModello(lez.getValue().get(0).getModello());
            if (lez.getValue().size() > 1) {
                cl.setDoppia(true);
                cl.setId_cal1(lez.getValue().get(0).getId());
                cl.setId_cal2(lez.getValue().get(1).getId());
                cl.setOre1(lez.getValue().get(0).getOre());
                cl.setOre2(lez.getValue().get(1).getOre());
                cl.setUd1(lez.getValue().get(0).getUnitadidattica().getCodice());
                cl.setUd2(lez.getValue().get(1).getUnitadidattica().getCodice());
            } else {
                cl.setDoppia(false);
                cl.setOre1(lez.getValue().get(0).getOre());
                cl.setUd1(lez.getValue().get(0).getUnitadidattica().getCodice());
            }
            grouppedByLezione.add(cl);
        }
        return grouppedByLezione;
    }

    public static String createJson(Object list) {
        try {
            JsonObject jMembers = new JsonObject();
            StringWriter sw = new StringWriter();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(sw, list);
            String json_s = sw.toString();
            JsonParser parser = new JsonParser();
            JsonElement tradeElement = parser.parse(json_s);
            jMembers.add("aaData", tradeElement.getAsJsonArray());
            System.out.println(jMembers.toString());
            return jMembers.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static ModelliPrg filterModello3(List<ModelliPrg> m) {
        ModelliPrg m3 = m.stream().filter(s -> s.getModello() == 3).findFirst().orElse(null);
        return m3;
    }

    public static ModelliPrg filterModello4(List<ModelliPrg> m) {
        ModelliPrg m3 = m.stream().filter(s -> s.getModello() == 4).findFirst().orElse(null);
        return m3;
    }

    public static ModelliPrg filterModello6(List<ModelliPrg> m) {
        ModelliPrg m3 = m.stream().filter(s -> s.getModello() == 6).findFirst().orElse(null);
        return m3;
    }

    public static boolean lessonPresent(List<Lezioni_Modelli> l, Long id) {
        return l.stream().filter(o -> o.getLezione_calendario().getId() == id).findFirst().isPresent();
    }

    public static Lezioni_Modelli lezioneFiltered(List<Lezioni_Modelli> l, Long id) {
        return l.stream().filter(o -> o.getLezione_calendario().getId() == id).findFirst().orElse(null);
    }

    public static int numberGroupsModello4(ProgettiFormativi ps) {
        Set<Integer> statesAsSet = new HashSet<Integer>();
        for (Allievi p : ps.getAllievi().stream().filter(a -> a.getGruppo_faseB() > 0).collect(Collectors.toList())) {
            statesAsSet.add(p.getGruppo_faseB());
        }
        return statesAsSet.size();
    }

    public static int maxGroupsCreation(ProgettiFormativi ps) {
        Long cnt = ps.getAllievi().stream().filter(a -> !a.getStatopartecipazione().getId().equalsIgnoreCase("03") && a.getGruppo_faseB() != -1).count();
        return cnt.intValue();
    }

    public static Lezioni_Modelli lezioneFilteredByGroup(List<Lezioni_Modelli> l, Long id, int gruppo) {
        return l.stream().filter(o -> o.getLezione_calendario().getId() == id && o.getGruppo_faseB() == gruppo).findFirst().orElse(null);
    }

    public static boolean isPresent_LessonGroup(List<Lezioni_Modelli> l, Long id, int gruppo) {
        return l.stream().anyMatch(lc -> lc.getLezione_calendario().getId() == id && lc.getGruppo_faseB() == gruppo);
    }

    public static DocumentiPrg filterM2(ProgettiFormativi p) {
        return p.getDocumenti().stream().filter(d -> d.getTipo().getId() == 1L).findFirst().orElse(null);
    }

    public static String getStartPath(String path) {
        if (test && SystemUtils.IS_OS_WINDOWS && !path.startsWith("E:")) {
            return "E:\\" + path;
        }
        return path;
    }

    public static long membriAttivi(List<StaffModelli> s) {
        return s.stream().filter(a -> a.getAttivo() == 1).count();
    }

    public static boolean iswindows() {
        return SystemUtils.IS_OS_WINDOWS;
    }

    public static boolean invioEmailComunicazione(String stato1, String stato2) {
        String key = stato1 + "_" + stato2;
        Set s = statiEmail();
        return s.contains(key);
    }

    private static Set statiEmail() {
        Set s = new HashSet<String>();
        s.add("DV_P"); //Accettazione Modello 2
        s.add("DV_DVE"); //Rifiuto Modello 2
        s.add("DC_ATA"); //Accettazione Modello 3
        s.add("DC_DCE"); //Rifiuto Modello 3
        s.add("DVA_ATB"); //Accettato Modello 4
        s.add("DVA_DVAE"); //Rifiutato Modello 

        return s;
    }

    public static Map<Integer, String> bando_SE() {
        Map s = new HashMap();
        s.put(1, "Microcredito");
        s.put(2, "Microcredito esteso");
        s.put(3, "Piccoli prestiti");
        return s;
    }

    public static Map<Integer, String> bando_SUD() {
        Map s = new HashMap();
        s.put(1, "Finanziamento più consistente");
        s.put(2, "Procedure più semplici");
        s.put(3, "Criteri di selezione meno stringenti");
        s.put(4, "Tempi di istruttoria più veloci");
        s.put(5, "Piano di ammortamento e restituzione più conveniente");
        s.put(6, "Presenza di quota a fondo perduto");
        return s;
    }

    public static Map<Integer, String> no_agenvolazione() {
        Map s = new HashMap();
        s.put(1, "L'investimento iniziale non raggiunge l'investimento minimo per accedere alle agevolazioni di legge");
        s.put(2, "La copertura è assicurata interamente con fondi propri");
        s.put(3, "Ricordo al criterio ordinario - non ci sono bandi attivi o l'iniziativa non rientra tra le iniziative ammissibili a finanziamento dei bandi attivi");
        return s;
    }

    public static Map<Long, Long> allieviM5_loaded(List<MascheraM5> m5) {
        Map<Long, Long> ids = new HashMap();

        for (MascheraM5 m : m5) {
            ids.put(m.getAllievo().getId(), m.getId());
        }
        return ids;
    }

    public static Map<Long, Boolean> allieviM5_premialita(List<MascheraM5> m5, int idpf) {
        Long hh64 = new Long(230400000);
        Map<Long, Long> oreRendicontabili = Action.OreRendicontabiliAlunni(idpf);
        Map<Long, Boolean> ids = new HashMap();
        int i = 1;
        for (MascheraM5 m : m5) {
            if (oreRendicontabili.get(m.getAllievo().getId()) != null && oreRendicontabili.get(m.getAllievo().getId()).compareTo(hh64) > 0) {
                if (m.isTabella_premialita() && m.getTabella_premialita_punteggio() > 0) {
                    ids.put(m.getAllievo().getId(), true);
                }
            }
            i++;
        }
        return ids;

    }

    public static TipoDoc filterDocById(List<TipoDoc> docs, Long id) {
        return docs.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }

    public static Map<Allievi, Documenti_Allievi> Modello5Allievi(List<MascheraM5> m5) {
        Map<Allievi, Documenti_Allievi> ret = new HashMap();
        Documenti_Allievi d = null;
        for (Allievi a : listAllieviM5_loaded(m5)) {
            d = a.getDocumenti().stream().filter(doc -> doc.getDeleted() == 0 && doc.getTipo().getId() == 20L).findFirst().orElse(null);
            if (d != null) {
                ret.put(a, d);
            }
        }
        return ret;
    }

    public static Map<Allievi, Documenti_Allievi> Modello7Allievi(List<MascheraM5> m5) {
        Map<Allievi, Documenti_Allievi> ret = new HashMap();
        Documenti_Allievi d = null;
        for (Allievi a : listAllieviM5_loaded(m5)) {
            d = a.getDocumenti().stream().filter(doc -> doc.getDeleted() == 0 && doc.getTipo().getId() == 22L).findFirst().orElse(null);
            if (d != null) {
                ret.put(a, d);
            }
        }
        return ret;
    }

    public static List<Allievi> listAllieviM5_loaded(List<MascheraM5> m5) {
        List<Allievi> al = new ArrayList();
        for (MascheraM5 m : m5) {
            al.add(m.getAllievo());
        }
        return al;
    }

    public static String calcoladurata(long millis) {
        if (millis == 0 || millis < 0) {
            return "0h 0min 0sec";
        }
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        StringBuilder sb = new StringBuilder(64);
        sb.append(hours);
        sb.append("h ");
        sb.append(minutes);
        sb.append("min ");
        sb.append(seconds);
        sb.append("sec");
        return sb.toString();
    }

    public static String convertbooleantostring(boolean ing) {
        try {
            if (ing) {
                return "SI";
            }
        } catch (Exception e) {
        }
        return "NO";
    }

    public static DocumentiPrg filterByTipo(ProgettiFormativi p, TipoDoc t) {
        return p.getDocumenti().stream().filter(d -> d.getTipo().getId() == t.getId()).findFirst().orElse(null);
    }

    public static TipoDoc_Allievi filterDocAllievoById(List<TipoDoc_Allievi> docs, Long id) {
        return docs.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }

    public static String roundFloatAndFormat(float f, boolean converttoHours) {
        try {

            if (converttoHours) {
                double hours = f / 1000.0 / 60.0 / 60.0;
                BigDecimal bigDecimal = new BigDecimal(hours);
                bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_EVEN);
                return numITA.format(bigDecimal).replaceAll("[^0123456789.,()-]", "").trim();
            } else {
                BigDecimal bigDecimal = new BigDecimal(Float.toString(f));
                bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_EVEN);
                return numITA.format(bigDecimal).replaceAll("[^0123456789.,()-]", "").trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";

    }

    //cscrfl86e19c3520
    public static String estraiSessodaCF(String cf) {
        try {
            if (cf.trim().length() == 16) {
                String data = StringUtils.substring(cf, 9, 11);
                if (Integer.parseInt(data) > 31) {
                    return "F";
                } else {
                    return "M";
                }
            }
        } catch (Exception e) {
        }
        return "M";
    }

    public static List<Item> unitamisura() {
        List<Item> al = new ArrayList<>();
        al.add(new Item("gg", "GIORNI"));
        al.add(new Item("mm", "MESI"));
        al.add(new Item("aa", "ANNI"));
        return al;
    }

    public static String formatTarget(String t1) {
        try {
            if (t1.equalsIgnoreCase("D1")) {
                return "Donna inattiva";
            } else if (t1.equalsIgnoreCase("D2")) {
                return "Disoccupato/a di lunga durata";
            }
        } catch (Exception e) {
        }
        return t1;
    }

    public static List<Item> listTargets() {
        List<Item> t = new ArrayList();
        t.add(new Item("D1", "Donna inattiva"));
        t.add(new Item("D2", "Disoccupato/a di lunga durata"));

        return t;
    }

    public static boolean isEditableModel(List<Lezioni_Modelli> list) throws ParseException {
        Date today = new Date();
//solo per test        today = new SimpleDateFormat("yyyy-MM-dd").parse("2021-07-18");
        DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();
        for (Lezioni_Modelli l : list) {
            if (dateTimeComparator.compare(l.getGiorno(), today) > -1) {
                return true;
            }
        }
        return false;
    }

    public static int allieviOK(long idp, List<Allievi> l) {
        Long hh36 = new Long(129600000);
//        Long hh64 = new Long(230400000);
//        Map<Long, Long> oreRendicontabili = Action.OreRendicontabiliAlunni((int) (long) idp);
        Map<Long, Long> oreRendicontabili_faseA = Action.OreRendicontabiliAlunni_faseA((int) (long) idp);
        int count = l.size();
        for (Allievi a : l) {
            if (oreRendicontabili_faseA.get(a.getId()) != null && oreRendicontabili_faseA.get(a.getId()).compareTo(hh36) < 0) {
                count--;
            }
        }
        return count;
    }

    public static List<Allievi> allievi_fa(long idp, List<Allievi> l) {
        Long hh36 = new Long(129600000);
        Map<Long, Long> oreRendicontabili_faseA = Action.OreRendicontabiliAlunni_faseA((int) (long) idp);
        return l.stream().filter(a -> oreRendicontabili_faseA.get(a.getId()) != null && oreRendicontabili_faseA.get(a.getId()).compareTo(hh36) > 0).collect(Collectors.toList());
    }

    public static List<Allievi> allievi_fb(long idp, List<Allievi> l) {
        Long hh64 = new Long(230400000);
        Map<Long, Long> oreRendicontabili_faseB = Action.OreRendicontabiliAlunni((int) (long) idp);
        return l.stream().filter(a -> oreRendicontabili_faseB.get(a.getId()) != null && oreRendicontabili_faseB.get(a.getId()).compareTo(hh64) > 0).collect(Collectors.toList());
    }

    public static List<Docenti> docenti_ore(long idp, List<Docenti> l) {
        Map<Long, Long> oreRendicontabili_docenti = Action.OreRendicontabiliDocenti((int) (long) idp);
        return l.stream().filter(a -> oreRendicontabili_docenti.get(a.getId()) != null).collect(Collectors.toList());
    }

    public static String roundTwoDigits(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        String ret = String.valueOf(bd.doubleValue());
        if (ret.split("\\.")[1].length() == 1) {
            ret += "0";
        }
        return ret;
    }

    public static Map<String, String> mapCoeffDocenti(String fasciaA, String fasciaB) {
        Map<String, String> m = new HashMap();
        m.put("FA", fasciaA);
        m.put("FB", fasciaB);
        return m;
    }

    public static String createJsonCL(String message) {
        JSONArray array = new JSONArray();
        JSONObject item;
        String[] alunni = message.split(";");
        for (String a : alunni) {
            item = new JSONObject();
            item.put("id", a.split("=")[0]);
            item.put("ore", a.split("=")[1]);
            item.put("totale", a.split("=")[2]);
            array.put(item);
        }
        return array.toString();
    }

    public static String createJsonCL_Mappatura(String message) {
        JSONArray array = new JSONArray();
        JSONObject item;
        String[] alunni = message.split(";");
        for (String a : alunni) {
            item = new JSONObject();
            item.put("id", a.split("=")[0]);
            item.put("mappato", a.split("=")[1]);
            array.put(item);
        }
        return array.toString();
    }

    public static String roundDoubleAndFormat(double f) {
        try {
            BigDecimal bigDecimal = new BigDecimal(Double.toString(f));
            bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_EVEN);
            return numITA.format(bigDecimal).replaceAll("[^0123456789.,()-]", "").trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";

    }

    public static double parseDouble(String f) {
        try {
            BigDecimal bigDecimal = new BigDecimal(f);
            bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_EVEN);
            return bigDecimal.doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;

    }

    public static String createJsonCL_Output(String message) {
        JSONArray array = new JSONArray();
        JSONObject item;
        String[] alunni = message.split(";");
        for (String a : alunni) {
            item = new JSONObject();
            item.put("id", a.split("=")[0]);
            item.put("output", a.split("=")[1]);
            array.put(item);
        }
        return array.toString();
    }

    public static Date getUltimoGiornoLezioneM4(ProgettiFormativi p) {
        ModelliPrg m4 = filterModello4(p.getModelli());
        Lezioni_Modelli ultima_lezione = m4.getLezioni().stream().max(Comparator.comparing(d -> d.getGiorno())).orElse(null);
        return ultima_lezione.getGiorno();
    }

    public static Date getPrimoGiornoLezioneM3(ProgettiFormativi pf) {
        ModelliPrg m3 = filterModello3(pf.getModelli());
        Lezioni_Modelli prima_lezione = m3.getLezioni().stream().min(Comparator.comparing(d -> d.getGiorno())).orElse(null);
        return prima_lezione.getGiorno();
    }

    public static boolean copyR(File source, File dest) {
        boolean es;
        try {
            long byteing = source.length();
            try (OutputStream out = new FileOutputStream(dest)) {
                long contenuto = FileUtils.copyFile(source, out);
                es = byteing == contenuto;
            }
        } catch (Exception e) {
            es = false;
        }
        return es;
    }
}
