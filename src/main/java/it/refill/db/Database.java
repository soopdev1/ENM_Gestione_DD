/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.db;

import com.google.common.base.Splitter;
import it.refill.domain.Comuni;
import it.refill.domain.Docenti;
import it.refill.domain.FasceDocenti;
import it.refill.domain.SediFormazione;
import it.refill.domain.SoggettiAttuatori;
import it.refill.entity.FadCalendar;
import it.refill.entity.Item;
import it.refill.util.Fadroom;
import it.refill.util.Utility;
import static it.refill.util.Utility.calcoladurata;
import static it.refill.util.Utility.createDir;
import static it.refill.util.Utility.estraiEccezione;
import static it.refill.util.Utility.formatStringtoStringDate;
import static it.refill.util.Utility.getUtilDate;
import static it.refill.util.Utility.patternFile;
import static it.refill.util.Utility.patternITA;
import static it.refill.util.Utility.patternSql;
import static it.refill.util.Utility.preparefilefordownload;
import static it.refill.util.Utility.test;
import java.io.File;
import static java.lang.Class.forName;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import static org.apache.commons.codec.binary.Base64.decodeBase64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.lang3.StringUtils.right;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;

/**
 *
 * @author rcosco
 */
public class Database {

    public Connection c = null;

    public Database(boolean bando, boolean neet) {
        String driver = "com.mysql.cj.jdbc.Driver";
        String user = "bando";
        String password = "bando";

        String host;

        if (bando) {
            if (neet) {
                if (test) {
                    host = "clustermicrocredito.cluster-c6m6yfqeypv3.eu-south-1.rds.amazonaws.com:3306/enm_neet";
                } else {
                    host = "clustermicrocredito.cluster-c6m6yfqeypv3.eu-south-1.rds.amazonaws.com:3306/enm_neet_prod";
                }
            } else {
                if (test) {
                    host = "clustermicrocredito.cluster-c6m6yfqeypv3.eu-south-1.rds.amazonaws.com:3306/enm_dd";
                } else {
                    host = "clustermicrocredito.cluster-c6m6yfqeypv3.eu-south-1.rds.amazonaws.com:3306/enm_dd_prod";
                }
            }
        } else {
            if (test) {
                host = "clustermicrocredito.cluster-c6m6yfqeypv3.eu-south-1.rds.amazonaws.com:3306/enm_gestione_dd";
            } else {
                host = "clustermicrocredito.cluster-c6m6yfqeypv3.eu-south-1.rds.amazonaws.com:3306/enm_gestione_dd_prod";
            }
        }

        try {
            forName(driver).newInstance();
            Properties p = new Properties();
            p.put("user", user);
            p.put("password", password);
            p.put("characterEncoding", "UTF-8");
            p.put("passwordCharacterEncoding", "UTF-8");
            p.put("useSSL", "false");
            p.put("connectTimeout", "1000");
            p.put("useUnicode", "true");
            this.c = DriverManager.getConnection("jdbc:mysql://" + host, p);
//            boolean ok = connesso(this.c);
//            System.out.println("HOST: " + host + " - CONNESSO " + ok + " - ISDBTEST: " + test);
        } catch (Exception ex) {
            System.err.println("ERROR SYSTEM: " + estraiEccezione(ex));
            if (this.c != null) {
                try {
                    this.c.close();
                } catch (Exception ex1) {
                }
            }
            this.c = null;
        }
    }

    public Connection Db_Neet() {
        String driver = "com.mysql.cj.jdbc.Driver";
        String user = "bando";
        String password = "bando";
        String host;
        try {
            if (test) {
                host = "clustermicrocredito.cluster-c6m6yfqeypv3.eu-south-1.rds.amazonaws.com:3306/enm_neet";
            } else {
                host = "clustermicrocredito.cluster-c6m6yfqeypv3.eu-south-1.rds.amazonaws.com:3306/enm_neet_prod";
            }

        } catch (Exception ex) {
            System.err.println("ERROR SYSTEM: " + estraiEccezione(ex));
        }

        return null;
    }

    public boolean connesso(Connection con) {
        try {
            return con != null && !con.isClosed();
        } catch (Exception ignored) {
        }
        return false;
    }

    public void closeDB() {
        try {
            if (c != null) {
                this.c.close();
            }
        } catch (SQLException ex) {
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
        }
    }

    public Connection getC() {
        return c;
    }

    public void setC(Connection c) {
        this.c = c;
    }

    public int countPregresso() {
        int count = 0;
        String sql = "SELECT COUNT(idallievi_pregresso) FROM allievi_pregresso";
        try {
            Statement st = this.c.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            count = 0;
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
        }
        return count;
    }

    public List<Fadroom> listStanzeOGGI() {
        List<Fadroom> out = new ArrayList<>();
        try {
            String sql = "SELECT room,idprogetti_formativi FROM fad_access WHERE DATA LIKE CONCAT(CURDATE(),'%') GROUP BY idprogetti_formativi,room";
            try (Statement st = this.c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    out.add(new Fadroom(rs.getString(1), String.valueOf(rs.getInt(2)),
                            right(rs.getString(1), 1),
                            "0"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
        }
        return out;
    }

    public List<Fadroom> listStanzeOGGISA(Long idsa) {
        List<Fadroom> out = new ArrayList<>();
        try {
            String sql = "SELECT room,idprogetti_formativi FROM fad_access "
                    + "WHERE data LIKE CONCAT(CURDATE(),'%') AND idprogetti_formativi IN "
                    + "(SELECT idprogetti_formativi FROM progetti_formativi WHERE (stato = 'ATA' OR stato = 'ATB') AND idsoggetti_attuatori = " + idsa + ")"
                    + " GROUP BY idprogetti_formativi,room";
            System.out.println(sql);
            try (Statement st = this.c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    out.add(new Fadroom(rs.getString(1), String.valueOf(rs.getInt(2)),
                            right(rs.getString(1), 1),
                            "0"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
        }
        return out;
    }

    public List<Fadroom> listStanze() {
        List<Fadroom> out = new ArrayList<>();
        try {
            String sql = "SELECT * FROM fad_multi a WHERE stato='0'";
            Statement st = this.c.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                out.add(new Fadroom(rs.getString(1), String.valueOf(rs.getInt(2)), rs.getString(3), rs.getString(5)));
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
        }

        return out;
    }

    public List<Item> listReport() {
        List<Item> out = new ArrayList<>();
        try {
            String sql = "SELECT * FROM fad_report";
            Statement st = this.c.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                out.add(new Item(rs.getString(1), rs.getString(2), ""));
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
        }

        return out;
    }

    public String getBase64Report(int idpr) {
        String out = null;
        try {
            String sql = "SELECT base64 FROM fad_report WHERE idprogetti_formativi = " + idpr;
            Statement st = this.c.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                out = rs.getString(1);
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
            out = null;
        }
        return out;
    }

    public List<Item> listStanza() {
        List<Item> out = new ArrayList<>();
        try {
            String sql = "SELECT idprogetti_formativi,nomestanza FROM fad a WHERE stato='0'";
            Statement st = this.c.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                out.add(new Item(String.valueOf(rs.getInt(1)), rs.getString(2), rs.getString(2)));
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
        }
        return out;
    }

    public String getPathtemp(String id) {
        String p1 = "/mnt/mcn/test/temp/";
        try {
            String sql = "SELECT url FROM path WHERE id = ?";
            PreparedStatement ps = this.c.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                p1 = rs.getString(1);
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
        }
        return p1;
    }

    public String getNomePR_F(String id) {
        String p1 = "Progetto Formativo";
        try {
            String sql = "SELECT descrizione FROM progetti_formativi WHERE idprogetti_formativi = ?";
            PreparedStatement ps = this.c.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                p1 = rs.getString(1);
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
        }
        return p1;
    }

    public List<FadCalendar> calendarioFAD(String id) {
        List<FadCalendar> out = new ArrayList<>();
        try {
            String sql = "SELECT * FROM fad_calendar f WHERE f.idprogetti_formativi = ? ORDER BY numerocorso,DATA";
            PreparedStatement ps = this.c.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("orainizio").contains(";")) {
                    List<String> orainizio = Splitter.on(";").splitToList(rs.getString("orainizio"));
                    List<String> orafine = Splitter.on(";").splitToList(rs.getString("orafine"));
                    for (int x = 0; x < orainizio.size(); x++) {
                        out.add(new FadCalendar(
                                id,
                                rs.getString("numerocorso"),
                                formatStringtoStringDate(rs.getString("data"), patternSql, patternITA, false),
                                //                                rs.getString("data"),
                                orainizio.get(x),
                                orafine.get(x))
                        );
                    }
                } else {
                    out.add(new FadCalendar(id, rs.getString("numerocorso"), formatStringtoStringDate(rs.getString("data"), patternSql, patternITA, false),
                            rs.getString("orainizio"), rs.getString("orafine")));
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
        }
        return out;
    }

    public boolean insertcalendarioFAD(String idpr, String corso, String data, String orainizio, String orafine) {
        boolean out = false;
        try {
            String sql = "SELECT * FROM fad_calendar WHERE idprogetti_formativi = ? AND numerocorso = ? AND data = ?";
            PreparedStatement ps = this.c.prepareStatement(sql);
            ps.setString(1, idpr);
            ps.setString(2, corso);
            ps.setString(3, data);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String orainizio_old = rs.getString("orainizio");
                String orafine_old = rs.getString("orafine");
                String orainizio_new = orainizio_old + ";" + orainizio;
                String orafine_new = orafine_old + ";" + orafine;
                String update = "UPDATE fad_calendar SET orainizio = ?, orafine = ? "
                        + "WHERE idprogetti_formativi = ? AND numerocorso = ? AND data = ? AND orainizio = ? AND orafine = ?";
                PreparedStatement ps1 = this.c.prepareStatement(update);
                ps1.setString(1, orainizio_new);
                ps1.setString(2, orafine_new);
                ps1.setString(3, idpr);
                ps1.setString(4, corso);
                ps1.setString(5, data);
                ps1.setString(6, orainizio_old);
                ps1.setString(7, orafine_old);
                ps1.executeUpdate();
                out = true;
                ps1.close();

            } else {
                String del = "INSERT INTO fad_calendar VALUES (?,?,?,?,?)";
                PreparedStatement ps1 = this.c.prepareStatement(del);
                ps1.setString(1, idpr);
                ps1.setString(2, corso);
                ps1.setString(3, data);
                ps1.setString(4, orainizio);
                ps1.setString(5, orafine);
                ps1.execute();
                out = true;
                ps1.close();
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
            out = false;
        }

        return out;
    }

    public boolean removecalendarioFAD(String idpr, String corso, String inizio, String data) {
        boolean out = false;
        try {
            String sql = "SELECT * FROM fad_calendar WHERE idprogetti_formativi = ? AND numerocorso = ? AND data = ? AND orainizio LIKE ?";
            PreparedStatement ps = this.c.prepareStatement(sql);
            ps.setString(1, idpr);
            ps.setString(2, corso);
            ps.setString(3, data);
            ps.setString(4, "%" + inizio + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String orainizio = rs.getString("orainizio");
                String orafine = rs.getString("orafine");
                if (orainizio.contains(";")) {
                    LinkedList<String> orainizio_list = new LinkedList<>();
                    orainizio_list.addAll(Splitter.on(";").splitToList(orainizio));
                    LinkedList<String> orafine_list = new LinkedList<>();
                    orafine_list.addAll(Splitter.on(";").splitToList(orafine));
                    int indice = orainizio_list.indexOf(inizio);
                    if (indice >= 0) {
                        orainizio_list.remove(indice);
                        orafine_list.remove(indice);
                        String orainizio_new = String.join(";", orainizio_list);
                        String orafine_new = String.join(";", orafine_list);
                        String update = "UPDATE fad_calendar SET orainizio = ?, orafine = ? "
                                + "WHERE idprogetti_formativi = ? AND numerocorso = ? AND data = ? AND orainizio = ? AND orafine = ?";
                        PreparedStatement ps1 = this.c.prepareStatement(update);
                        ps1.setString(1, orainizio_new);
                        ps1.setString(2, orafine_new);
                        ps1.setString(3, idpr);
                        ps1.setString(4, corso);
                        ps1.setString(5, data);
                        ps1.setString(6, orainizio);
                        ps1.setString(7, orafine);
                        ps1.executeUpdate();
                        out = true;
                        ps1.close();
                    }
                } else {
                    String del = "DELETE FROM fad_calendar WHERE idprogetti_formativi = ? AND numerocorso = ? AND data = ? AND orainizio = ? AND orafine = ?";
                    PreparedStatement ps1 = this.c.prepareStatement(del);
                    ps1.setString(1, idpr);
                    ps1.setString(2, corso);
                    ps1.setString(3, data);
                    ps1.setString(4, orainizio);
                    ps1.setString(5, orafine);
                    System.out.println(ps1.toString());
                    ps1.execute();
                    out = true;
                    ps1.close();
                }
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
            out = false;
        }
        return out;
    }

    public SoggettiAttuatori estrai_SA_accettare(Entity en, String id) {
        SoggettiAttuatori sa = null;
        try {
            String sql = "SELECT a.id,a.username,a.sedecap,a.cellulare,a.cf,a.cognome,a.data,a.datadecreto,a.mail,a.sedeindirizzo,a.nome,"
                    + "a.docric,a.pec,a.pivacf,a.protocollo,a.societa,a.scadenzadoc,a.cellulare,a.sedecomune,a.dataupconvenzionefinale,"
                    + "a.decreto,a.datadecreto,a.caricasoc,a.accreditato"
                    + " FROM bando_dd_mcn a WHERE id = " + id;
            try (PreparedStatement ps = this.c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sa = new SoggettiAttuatori();
                    sa.setId(rs.getLong("a.id"));
                    sa.setUsernameaccr(rs.getString("a.username"));
                    sa.setRagionesociale(rs.getString("a.societa"));
                    sa.setPiva(rs.getString("a.pivacf"));
                    sa.setCodicefiscale(rs.getString("a.cf"));
                    sa.setComune(en.getComune(Long.parseLong(rs.getString("a.sedecomune"))));
                    sa.setIndirizzo(rs.getString("a.sedeindirizzo"));
                    sa.setNome(rs.getString("a.nome"));
                    sa.setNome_refente(rs.getString("a.nome"));
                    sa.setCognome(rs.getString("a.cognome"));
                    sa.setCognome_referente(rs.getString("a.cognome"));
                    sa.setTelefono_sa(rs.getString("a.cellulare"));
                    sa.setTelefono_Ad(rs.getString("a.cellulare"));
                    sa.setTelefono_referente(rs.getString("a.cellulare"));
                    sa.setProtocollo(rs.getString("a.protocollo"));
                    sa.setDataprotocollo(getUtilDate(rs.getString("a.dataupconvenzionefinale"), patternITA));
                    sa.setVisual_dataprotocollo(rs.getString("a.datadecreto"));
                    //////////////////////////////////////////////////
                    sa.setEmail(rs.getString("a.mail"));
                    sa.setPec(rs.getString("a.pec"));
                    sa.setCell_sa(rs.getString("a.cellulare"));
                    sa.setCap(rs.getString("a.sedecap"));
                    sa.setDatanascita(getUtilDate(rs.getString("a.data"), patternITA));
                    sa.setScadenza(getUtilDate(rs.getString("a.scadenzadoc"), patternITA));
                    sa.setNro_documento(rs.getString("a.docric"));
                    sa.setDd(rs.getString("a.decreto") + " DEL " + rs.getString("a.datadecreto"));
                    sa.setCarica(rs.getString("a.caricasoc"));
                    sa.setAccreditatoneet(rs.getString("a.accreditato"));
                    return sa;
                }
            }
        } catch (Exception ex) {
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
        }
        return sa;
    }

    public List<SoggettiAttuatori> estrai_SA_accettare(Entity en) {
        List<SoggettiAttuatori> out = new LinkedList<>();

        try {
            String sql = "SELECT a.id,a.username,a.sedecap,a.cellulare,a.cf,a.cognome,a.`data`,a.datadecreto,a.mail,a.sedeindirizzo,a.nome,"
                    + "a.docric,a.pec,a.pivacf,a.protocollo,a.societa,a.scadenzadoc,a.cellulare,a.sedecomune,a.accreditato"
                    + " FROM bando_dd_mcn a WHERE stato_domanda='A' AND dataupconvenzionefinale<>'-'";
            try (PreparedStatement ps = this.c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SoggettiAttuatori sa = new SoggettiAttuatori();

                    sa.setId(rs.getLong("a.id"));
                    sa.setUsernameaccr(rs.getString("a.username"));
                    sa.setRagionesociale(rs.getString("a.societa"));
                    sa.setPiva(rs.getString("a.pivacf"));
                    sa.setCodicefiscale(rs.getString("a.cf"));
                    sa.setComune(en.getComune(Long.parseLong(rs.getString("a.sedecomune"))));
                    sa.setIndirizzo(rs.getString("a.sedeindirizzo"));
                    sa.setNome(rs.getString("a.nome"));
                    sa.setNome_refente(rs.getString("a.nome"));
                    sa.setCognome(rs.getString("a.cognome"));
                    sa.setCognome_referente(rs.getString("a.cognome"));
                    sa.setTelefono_sa(rs.getString("a.cellulare"));
                    sa.setTelefono_Ad(rs.getString("a.cellulare"));
                    sa.setTelefono_referente(rs.getString("a.cellulare"));
                    sa.setProtocollo(rs.getString("a.protocollo"));
                    sa.setDataprotocollo(getUtilDate(rs.getString("a.datadecreto"), patternITA));
                    sa.setVisual_dataprotocollo(rs.getString("a.datadecreto"));
                    sa.setAccreditatoneet(rs.getString("a.accreditato"));
                    out.add(sa);
                }
            }
        } catch (Exception ex) {
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
        }

        return out;
    }

    public FileDownload getDocumentoIdentitaSA(String username) {
        FileDownload out = null;
        try {
            String sql = "SELECT path FROM docuserbandi WHERE username = '" + username + "' AND codicedoc = 'DOCR' AND stato='1' ORDER BY datacar DESC LIMIT 1";
            try (PreparedStatement ps = this.c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    out = preparefilefordownload(rs.getString(1));
                }
            }
        } catch (Exception ex) {
            out = null;
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
        }
        return out;

    }

    public List<SediFormazione> estrai_SEDI_SA(SoggettiAttuatori sa, Entity en, String ragionesociale) {
        String username = sa.getUsernameaccr();
        List<SediFormazione> out = new LinkedList<>();
        try {

            String sql = "SELECT numaule,"
                    + "mailresponsabile1,indirizzo1,responsabile1,telresponsabile1,citta1,"
                    + "mailresponsabile2,indirizzo2,responsabile2,telresponsabile2,citta2,"
                    + "mailresponsabile3,indirizzo3,responsabile3,telresponsabile3,citta3,"
                    + "mailresponsabile4,indirizzo4,responsabile4,telresponsabile4,citta4,"
                    + "mailresponsabile5,indirizzo5,responsabile5,telresponsabile5,citta5 "
                    + "FROM allegato_a a WHERE username='" + username + "'";

            try (PreparedStatement ps = this.c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int numaule = Integer.parseInt(rs.getString("numaule"));
                    for (int i = 1; i <= numaule; i++) {
                        String denominazione = ragionesociale + " - SEDE " + i;
                        String via = rs.getString("indirizzo" + i);
                        String referente = rs.getString("responsabile" + i);
                        String telefono = rs.getString("telresponsabile" + i);
                        String cellulare = rs.getString("telresponsabile" + i);
                        String email = rs.getString("mailresponsabile" + i);
                        Comuni c = en.getEm().find(Comuni.class, Long.parseLong(rs.getString("citta" + i)));
                        SediFormazione sf = new SediFormazione(denominazione, via, referente, telefono, cellulare, email, c);
                        sf.setSoggetto(sa);
                        out.add(sf);
                    }
                }
            }
        } catch (Exception ex) {
            out = null;
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
        }
        return out;

    }

    public List<Docenti> estrai_DOCENTI_SA(SoggettiAttuatori sa, Entity en) {
        List<Docenti> out = new LinkedList<>();
        try {
            String username = sa.getUsernameaccr();
            String date = new DateTime().toString(patternFile);

            String sql = "SELECT id,UPPER(nome),UPPER(cognome),UPPER(cf),datanascita,CONCAT('F',fascia),LOWER(mail) "
                    + "FROM allegato_b WHERE username = '" + username + "' GROUP BY cf ORDER BY id";

            try (PreparedStatement ps = this.c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString(1);
                    String nome = rs.getString(2);
                    String cognome = rs.getString(3);
                    String cf = rs.getString(4);
                    Date data_nascita = getUtilDate(rs.getString(5), patternITA);
                    String fascia = rs.getString(6);
                    String email = rs.getString(7);

                    Docenti d = new Docenti(nome, cognome, cf, data_nascita, email);
                    d.setSoggetto(sa);
                    d.setFascia(en.getEm().find(FasceDocenti.class, fascia));

                    String sql1 = "SELECT allegatocv,allegatodr FROM allegato_b1 WHERE username='" + username + "' AND idallegato_b1 = '" + id + "'";

                    try (PreparedStatement ps1 = this.c.prepareStatement(sql1); ResultSet rs1 = ps1.executeQuery()) {
                        if (rs1.next()) {
                            String path = en.getPath("pathDoc_Docenti").replace("@docente", d.getCodicefiscale());
                            createDir(path);

                            FileDownload allegatocv = preparefilefordownload(rs1.getString(1));
                            FileDownload allegatodr = preparefilefordownload(rs1.getString(2));

                            if (allegatocv != null && allegatodr != null) {
                                String ext1 = "." + FilenameUtils.getExtension(allegatocv.getName());
                                String ext2 = "." + FilenameUtils.getExtension(allegatodr.getName());

                                File dest1 = new File(path + "Curriculum_" + sa.getId() + "_" + date + "_" + d.getCodicefiscale() + ext1);
                                File dest2 = new File(path + "Doc_id_" + sa.getId() + "_" + date + "_" + d.getCodicefiscale() + ext2);

                                FileUtils.writeByteArrayToFile(dest1, decodeBase64(allegatocv.getContent()));
                                FileUtils.writeByteArrayToFile(dest2, decodeBase64(allegatodr.getContent()));

                                if (dest1.length() > 0 && dest2.length() > 0) {
                                    //DOCUMENTI
                                    d.setScadenza_doc(new SimpleDateFormat(patternITA).parse("31/12/2031"));
                                    d.setCurriculum(dest1.getPath());
                                    d.setDocId(dest2.getPath());
                                    d.setStato("DV");
                                    out.add(d);
                                }
                            }
                        }
                    }

                }
            }

        } catch (Exception ex) {
            System.err.println("METHOD: " + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName());
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(ex));
        }
        return out;

    }

    public Map<Long, Long> OreRendicontabiliDocentiFASEA(int pf) {
        Map result = new HashMap();
        try {
            String sql = "SELECT sum(totaleorerendicontabili) as totOre,idutente FROM registro_completo WHERE ruolo = 'DOCENTE' AND fase='A' AND idprogetti_formativi = ? GROUP BY idutente";
            PreparedStatement ps = this.c.prepareStatement(sql);
            ps.setInt(1, pf);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getLong("idutente"), rs.getLong("totOre"));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(e));
        }
        return result;
    }

    public Map<Long, Long> OreRendicontabiliDocenti(int pf) {
        Map result = new HashMap();
        try {
            String sql = "SELECT sum(totaleorerendicontabili) as totOre,idutente FROM registro_completo WHERE ruolo = 'DOCENTE' AND idprogetti_formativi = ? GROUP BY idutente;";
            PreparedStatement ps = this.c.prepareStatement(sql);
            ps.setInt(1, pf);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getLong("idutente"), rs.getLong("totOre"));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(e));
        }
        return result;
    }

    //Totale Ore rendicontabili per Maschera Modello 5
    public Map<Long, Long> OreRendicontabiliAlunni(int pf) {
        Map result = new HashMap();
        try {
            String sql = "SELECT sum(totaleorerendicontabili) as totOre,idutente FROM registro_completo WHERE idprogetti_formativi = ? GROUP BY idutente;";
            PreparedStatement ps = this.c.prepareStatement(sql);
            ps.setInt(1, pf);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getLong("idutente"), rs.getLong("totOre"));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(e));
        }
        return result;
    }

    public Map<Long, Long> OreRendicontabiliAlunni_faseB(int pf) {
        Map result = new HashMap();
        try {
            String sql = "SELECT sum(totaleorerendicontabili) as totOre,idutente FROM registro_completo WHERE fase = 'B' AND  idprogetti_formativi = ? GROUP BY idutente;";
            PreparedStatement ps = this.c.prepareStatement(sql);
            ps.setInt(1, pf);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getLong("idutente"), rs.getLong("totOre"));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(e));
        }
        return result;
    }

    //Totale Ore rendicontabili FASE A per Maschera Modello 5
    public Map<Long, Long> OreRendicontabiliAlunni_faseA(int pf) {
        Map result = new HashMap();
        try {
            String sql = "SELECT sum(totaleorerendicontabili) as totOre,idutente FROM registro_completo WHERE fase = 'A' AND idprogetti_formativi = ? GROUP BY idutente;";
            PreparedStatement ps = this.c.prepareStatement(sql);
            ps.setInt(1, pf);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getLong("idutente"), rs.getLong("totOre"));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(e));
        }
        return result;
    }

    public String[] dati_modello5_neet(String idneet, String idsa, String pf) {
        String datafinepercorso = "";
        AtomicLong orefrequenza = new AtomicLong(0L);
        try {
            String sql = "SELECT * FROM registro_completo "
                    + "WHERE idutente = " + idneet + " AND idprogetti_formativi = " + pf + " AND idsoggetti_attuatori = " + idsa
                    + " AND ruolo = 'ALLIEVO NEET' ORDER BY data";
            try (Statement st = this.c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    datafinepercorso = Utility.sdfITA.format(rs.getDate("data"));
                    orefrequenza.addAndGet(rs.getLong("totaleorerendicontabili"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(e));
        }
        return new String[]{datafinepercorso, calcoladurata(orefrequenza.get())};
    }

    public List<Registro_completo> registro_modello6(String idpr) {
        List<Registro_completo> registro = new ArrayList<>();
        try {
            String sql = "SELECT * FROM registro_completo WHERE idprogetti_formativi = " + idpr + " ORDER BY data";
            try (Statement st = this.c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    Registro_completo rc = new Registro_completo(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getInt(3),
                            rs.getString(4),
                            new DateTime(rs.getDate(5).getTime()),
                            rs.getString(6),
                            rs.getInt(7),
                            rs.getString(8),
                            rs.getString(9),
                            rs.getLong(10),
                            rs.getString(11),
                            rs.getString(12),
                            rs.getInt(13),
                            rs.getString(14),
                            rs.getString(15),
                            rs.getString(16),
                            rs.getString(17),
                            rs.getString(18),
                            rs.getString(19),
                            rs.getLong(20),
                            rs.getLong(21),
                            rs.getInt(23));
                    registro.add(rc);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(e));
        }
        return registro;
    }

    public List<Item> area_qualificazione() {
        List<Item> out = new ArrayList<>();
        try {
            String sql = "SELECT * FROM qualificazione_rc";
            try (PreparedStatement ps1 = this.c.prepareStatement(sql); ResultSet rs1 = ps1.executeQuery()) {
                while (rs1.next()) {
                    out.add(new Item(rs1.getInt(1), rs1.getString(2)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(e));
        }
        return out;
    }

    public List<Item> inquadramento() {
        List<Item> out = new ArrayList<>();
        try {
            String sql = "SELECT * FROM inquadramento_rc";
            try (PreparedStatement ps1 = this.c.prepareStatement(sql); ResultSet rs1 = ps1.executeQuery()) {
                while (rs1.next()) {
                    out.add(new Item(rs1.getInt(1), rs1.getString(2)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(e));
        }
        return out;
    }

    public ArrayList<Item> attivita_docenti() {
        ArrayList<Item> out = new ArrayList<>();
        try {
            String sql = "SELECT * FROM attivita_docenti_rc";
            try (PreparedStatement ps1 = this.c.prepareStatement(sql); ResultSet rs1 = ps1.executeQuery()) {
                while (rs1.next()) {
                    out.add(new Item(rs1.getInt(1), rs1.getString(2)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(e));
        }
        return out;
    }

    public ArrayList<Item> disponibilita() {
        ArrayList<Item> out = new ArrayList<>();
        try {
            String sql = "SELECT * FROM disponibilita_rc";
            try (PreparedStatement ps1 = this.c.prepareStatement(sql); ResultSet rs1 = ps1.executeQuery()) {
                while (rs1.next()) {
                    out.add(new Item(rs1.getInt(1), rs1.getString(2)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(e));
        }
        return out;
    }

    public ArrayList<Item> fontifin() {
        ArrayList<Item> out = new ArrayList<>();
        try {
            String sql = "SELECT * FROM fontifin_rc";
            try (PreparedStatement ps1 = this.c.prepareStatement(sql); ResultSet rs1 = ps1.executeQuery()) {
                while (rs1.next()) {
                    out.add(new Item(rs1.getInt(1), rs1.getString(2)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR: " + ExceptionUtils.getStackTrace(e));
        }
        return out;
    }

}
