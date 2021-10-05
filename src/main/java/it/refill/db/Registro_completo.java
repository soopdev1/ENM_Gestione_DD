/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.db;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;
import org.joda.time.DateTime;

/**
 *
 * @author rcosco
 */
public class Registro_completo {

    int id, idprogetti_formativi, idsoggetti_attuatori;
    String cip;
    DateTime data;
    String idriunione;
    int numpartecipanti;
    String orainizio, orafine;
    long durata;
    String nud, fase;
    int gruppofaseb;
    String ruolo, cognome, nome, email, orelogin, orelogout;
    long totaleore, totaleorerendicontabili;
    int idutente;

    //modello6
    //docenti
    String cf,fascia;
    long data1, data2, data3, data4, data5, data6, data7, data8, data9, data10, data11, data12;
    
    //allieviB
    int gruppoB;
    
    //allieviA
    String target,domandaammissione,modello5;
    
    public Registro_completo() {
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getDomandaammissione() {
        return domandaammissione;
    }

    public void setDomandaammissione(String domandaammissione) {
        this.domandaammissione = domandaammissione;
    }

    public String getModello5() {
        return modello5;
    }

    public void setModello5(String modello5) {
        this.modello5 = modello5;
    }
    
    
    
    public int getGruppoB() {
        return gruppoB;
    }

    public void setGruppoB(int gruppoB) {
        this.gruppoB = gruppoB;
    }

    
    
    public String getCf() {
        return cf;
    }

    public void setCf(String cf) {
        this.cf = cf;
    }
    
    public String getFascia() {
        return fascia;
    }

    public void setFascia(String fascia) {
        this.fascia = fascia;
    }

    public long getData1() {
        return data1;
    }

    public void setData1(long data1) {
        this.data1 = data1;
    }

    public long getData2() {
        return data2;
    }

    public void setData2(long data2) {
        this.data2 = data2;
    }

    public long getData3() {
        return data3;
    }

    public void setData3(long data3) {
        this.data3 = data3;
    }

    public long getData4() {
        return data4;
    }

    public void setData4(long data4) {
        this.data4 = data4;
    }

    public long getData5() {
        return data5;
    }

    public void setData5(long data5) {
        this.data5 = data5;
    }

    public long getData6() {
        return data6;
    }

    public void setData6(long data6) {
        this.data6 = data6;
    }

    public long getData7() {
        return data7;
    }

    public void setData7(long data7) {
        this.data7 = data7;
    }

    public long getData8() {
        return data8;
    }

    public void setData8(long data8) {
        this.data8 = data8;
    }

    public long getData9() {
        return data9;
    }

    public void setData9(long data9) {
        this.data9 = data9;
    }

    public long getData10() {
        return data10;
    }

    public void setData10(long data10) {
        this.data10 = data10;
    }

    public long getData11() {
        return data11;
    }

    public void setData11(long data11) {
        this.data11 = data11;
    }

    public long getData12() {
        return data12;
    }

    public void setData12(long data12) {
        this.data12 = data12;
    }

    public Registro_completo(int id, int idprogetti_formativi, int idsoggetti_attuatori, String cip, DateTime data, String idriunione, int numpartecipanti, String orainizio, String orafine, long durata, String nud, String fase, int gruppofaseb, String ruolo, String cognome, String nome, String email, String orelogin, String orelogout, long totaleore, long totaleorerendicontabili, int idutente) {
        this.id = id;
        this.idprogetti_formativi = idprogetti_formativi;
        this.idsoggetti_attuatori = idsoggetti_attuatori;
        this.cip = cip;
        this.data = data;
        this.idriunione = idriunione;
        this.numpartecipanti = numpartecipanti;
        this.orainizio = orainizio;
        this.orafine = orafine;
        this.durata = durata;
        this.nud = nud;
        this.fase = fase;
        this.gruppofaseb = gruppofaseb;
        this.ruolo = ruolo;
        this.cognome = cognome;
        this.nome = nome;
        this.email = email;
        this.orelogin = orelogin;
        this.orelogout = orelogout;
        this.totaleore = totaleore;
        this.totaleorerendicontabili = totaleorerendicontabili;
        this.idutente = idutente;
    }

    public int getIdutente() {
        return idutente;
    }

    public void setIdutente(int idutente) {
        this.idutente = idutente;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdprogetti_formativi() {
        return idprogetti_formativi;
    }

    public void setIdprogetti_formativi(int idprogetti_formativi) {
        this.idprogetti_formativi = idprogetti_formativi;
    }

    public int getIdsoggetti_attuatori() {
        return idsoggetti_attuatori;
    }

    public void setIdsoggetti_attuatori(int idsoggetti_attuatori) {
        this.idsoggetti_attuatori = idsoggetti_attuatori;
    }

    public String getCip() {
        return cip;
    }

    public void setCip(String cip) {
        this.cip = cip;
    }

    public DateTime getData() {
        return data;
    }

    public void setData(DateTime data) {
        this.data = data;
    }

    public String getIdriunione() {
        return idriunione;
    }

    public void setIdriunione(String idriunione) {
        this.idriunione = idriunione;
    }

    public int getNumpartecipanti() {
        return numpartecipanti;
    }

    public void setNumpartecipanti(int numpartecipanti) {
        this.numpartecipanti = numpartecipanti;
    }

    public String getOrainizio() {
        return orainizio;
    }

    public void setORainizio(String rainizio) {
        this.orainizio = rainizio;
    }

    public String getOrafine() {
        return orafine;
    }

    public void setOrafine(String orafine) {
        this.orafine = orafine;
    }

    public long getDurata() {
        return durata;
    }

    public void setDurata(long durata) {
        this.durata = durata;
    }

    public String getNud() {
        return nud;
    }

    public void setNud(String nud) {
        this.nud = nud;
    }

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    public int getGruppofaseb() {
        return gruppofaseb;
    }

    public void setGruppofaseb(int gruppofaseb) {
        this.gruppofaseb = gruppofaseb;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrelogin() {
        return orelogin;
    }

    public void setOrelogin(String orelogin) {
        this.orelogin = orelogin;
    }

    public String getOrelogout() {
        return orelogout;
    }

    public void setOrelogout(String orelogout) {
        this.orelogout = orelogout;
    }

    public long getTotaleore() {
        return totaleore;
    }

    public void setTotaleore(long totaleore) {
        this.totaleore = totaleore;
    }

    public long getTotaleorerendicontabili() {
        return totaleorerendicontabili;
    }

    public void setTotaleorerendicontabili(long totaleorerendicontabili) {
        this.totaleorerendicontabili = totaleorerendicontabili;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, JSON_STYLE);
    }
}
