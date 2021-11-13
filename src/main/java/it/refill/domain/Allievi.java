/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

/**
 *
 * @author rcosco
 */
@Entity
@Table(name = "allievi")
@NamedQueries(value = {
    @NamedQuery(name = "a.byCF", query = "SELECT a FROM Allievi a WHERE a.codicefiscale=:codicefiscale AND a.statopartecipazione.id <> '03'"),
    @NamedQuery(name = "a.bySoggettoAttuatore", query = "SELECT a FROM Allievi a WHERE a.soggetto=:soggetto"),
    @NamedQuery(name = "a.bySoggettoAttuatoreNoProgetto", query = "SELECT a FROM Allievi a WHERE a.soggetto=:soggetto and a.progetto=null"),
    @NamedQuery(name = "a.bySoggettoAttuatoreNoProgettoAttivi", query = "SELECT a FROM Allievi a WHERE a.soggetto=:soggetto and a.progetto=null and a.stato = 'A'"),
    @NamedQuery(name = "a.byProgetto", query = "SELECT a FROM Allievi a WHERE a.progetto=:progetto AND a.statopartecipazione.id = '01'"),
    @NamedQuery(name = "a.byProgettoAll", query = "SELECT a FROM Allievi a WHERE a.progetto=:progetto"),
    @NamedQuery(name = "a.byEmail", query = "SELECT a FROM Allievi a WHERE a.email=:email AND a.statopartecipazione.id <> '03' ")
})
@JsonIgnoreProperties(value = {"documenti"})
public class Allievi implements Serializable {

    @Id
    @Column(name = "idallievi")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "nome")
    private String nome;
    @Column(name = "cognome")
    private String cognome;
    @Column(name = "codicefiscale")
    private String codicefiscale;
    @Temporal(TemporalType.DATE)
    @Column(name = "datanascita")
    private Date datanascita;
    @Column(name = "indirizzodomicilio")
    private String indirizzodomicilio;
    @Column(name = "indirizzoresidenza")
    private String indirizzoresidenza;
    @Column(name = "civicodomicilio")
    private String civicodomicilio;
    @Column(name = "civicoresidenza")
    private String civicoresidenza;
    @Column(name = "capdomicilio")
    private String capdomicilio;
    @Column(name = "capresidenza")
    private String capresidenza;
    @Column(name = "protocollo")
    private String protocollo;
    @Column(name = "esito")
    private String esito = "-";

    @Column(name = "esclusione_prg")
    private String esclusione_prg;


    @Column(name = "neet")
    private String neet;
    @Column(name = "docid")
    private String docid;
    @Column(name = "scadenzadocid")
    @Temporal(TemporalType.DATE)
    private Date scadenzadocid;
    @Column(name = "stato")
    private String stato;
    @Column(name = "idea_impresa")
    private String idea_impresa;
    @Column(name = "email")
    private String email;
    @Column(name = "sesso")
    private String sesso;
    @Column(name = "telefono")
    private String telefono;
    @Column(name = "importo")
    private double importo;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_up")
    private Date data_up;

    @ManyToOne
    @JoinColumn(name = "cittadinanza")
    private Nazioni_rc cittadinanza;
    @ManyToOne
    @JoinColumn(name = "comune_nascita")
    private Comuni comune_nascita;
    @ManyToOne
    @JoinColumn(name = "comune_residenza")
    private Comuni comune_residenza;
    @ManyToOne
    @JoinColumn(name = "comune_domicilio")
    private Comuni comune_domicilio;
    @ManyToOne
    @JoinColumn(name = "titolo_studio")
    private TitoliStudio titoloStudio;
    @ManyToOne
    @JoinColumn(name = "idprogetti_formativi")
    ProgettiFormativi progetto;
    @ManyToOne
    @JoinColumn(name = "idsoggetto_attuatore")
    SoggettiAttuatori soggetto;

    @Column(name = "stato_nascita")
    private String stato_nascita;
    @Column(name = "privacy2")
    private String privacy2;
    @Column(name = "privacy3")
    private String privacy3;
    
    @Column(name = "target")
    private String target;

    @Column(name = "data_anpal")
    private String data_anpal;

    @ManyToOne
    @JoinColumn(name = "idcondizione_mercato")
    Condizione_Mercato condizione_mercato;
    @ManyToOne
    @JoinColumn(name = "id_selfiemployement")
    Selfiemployment_Prestiti selfiemployement;
    @ManyToOne
    @JoinColumn(name = "id_statopartecipazione")
    StatoPartecipazione statopartecipazione;
    @ManyToOne
    @JoinColumn(name = "idcondizione_lavorativa")
    Condizione_Lavorativa condizione_lavorativa;

    @ManyToOne
    @JoinColumn(name = "motivazione")
    Motivazione motivazione;

    @ManyToOne
    @JoinColumn(name = "idcanale")
    Canale canale;

    @OneToMany(mappedBy = "allievo", fetch = FetchType.LAZY)
    List<Documenti_Allievi> documenti;

    @Transient
    private boolean pregresso;
    @Transient
    private double ore_fa = 0.0;
    @Transient
    private double ore_fb = 0.0;
    @Transient
    private String orerendicontabili  = "";
    
    
    //Gruppo modello 4
    @Column(name = "gruppo_faseB")
    private int gruppo_faseB;

     // 0 - DA SALVARE (NO) - 1 SI
    @Column(name = "mappatura")
    private int mappatura;
    
    @Column(name = "mappatura_note")
    private String mappatura_note;
    
    
    
    public Allievi() {
        this.pregresso = false;
    }

    public String getMappatura_note() {
        return mappatura_note;
    }

    public void setMappatura_note(String mappatura_note) {
        this.mappatura_note = mappatura_note;
    }
    
    
    
    public String getOrerendicontabili() {
        return orerendicontabili;
    }

    public void setOrerendicontabili(String orerendicontabili) {
        this.orerendicontabili = orerendicontabili;
    }

    public int getMappatura() {
        return mappatura;
    }

    public void setMappatura(int mappatura) {
        this.mappatura = mappatura;
    }
    
    public String getData_anpal() {
        return data_anpal;
    }

    public void setData_anpal(String data_anpal) {
        this.data_anpal = data_anpal;
    }

    public Allievi(boolean preg) {
        this.pregresso = preg;
        if (preg) {
            this.id = 0L;
            this.nome = "";
            this.cognome = "";
            this.codicefiscale = "";
            this.datanascita = new Date();
            this.indirizzodomicilio = "";
            this.indirizzoresidenza = "";
            this.civicodomicilio = "";
            this.civicoresidenza = "";
            this.capdomicilio = "";
            this.capresidenza = "";
            this.protocollo = "";
            this.target = "-";
            this.motivazione = new Motivazione();
            this.neet = "";
            this.docid = "";
            this.scadenzadocid = new Date();
            this.stato = "";
            this.idea_impresa = "";
            this.email = "";
            this.sesso = "";
            this.telefono = "";
            this.data_up = new Date();
            this.cittadinanza = new Nazioni_rc();
            this.comune_nascita = new Comuni();
            this.comune_residenza = new Comuni();
            this.comune_domicilio = new Comuni();
            this.titoloStudio = new TitoliStudio();
            this.progetto = new ProgettiFormativi();
            this.soggetto = new SoggettiAttuatori();
            this.condizione_mercato = new Condizione_Mercato();
            this.selfiemployement = new Selfiemployment_Prestiti();
            this.statopartecipazione = new StatoPartecipazione();
            this.condizione_lavorativa = new Condizione_Lavorativa();
            this.canale = new Canale();
            this.documenti = new ArrayList<>();
        }
    }

    public Allievi(Long id, String nome, String cognome, int gruppo_faseB) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.gruppo_faseB = gruppo_faseB;
        this.codicefiscale = "";
    }

    public Allievi(Long id, String nome, String cognome) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.codicefiscale = "";
    }

    public String getEsclusione_prg() {
        return esclusione_prg;
    }

    public void setEsclusione_prg(String esclusione_prg) {
        this.esclusione_prg = esclusione_prg;
    }

    public int getGruppo_faseB() {
        return gruppo_faseB;
    }

    public void setGruppo_faseB(int gruppo_faseB) {
        this.gruppo_faseB = gruppo_faseB;
    }

    public Canale getCanale() {
        return canale;
    }

    public void setCanale(Canale canale) {
        this.canale = canale;
    }

    public boolean isPregresso() {
        return pregresso;
    }

    public void setPregresso(boolean pregresso) {
        this.pregresso = pregresso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome.toUpperCase();
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome.toUpperCase();
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCodicefiscale() {
        return codicefiscale.toUpperCase();
    }

    public void setCodicefiscale(String codicefiscale) {
        this.codicefiscale = codicefiscale;
    }

    public Date getDatanascita() {
        return datanascita;
    }

    public void setDatanascita(Date datanascita) {
        this.datanascita = datanascita;
    }

    public String getSesso() {
        return sesso;
    }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    public String getIndirizzodomicilio() {
        return indirizzodomicilio;
    }

    public void setIndirizzodomicilio(String indirizzodomicilio) {
        this.indirizzodomicilio = indirizzodomicilio;
    }

    public String getIndirizzoresidenza() {
        return indirizzoresidenza;
    }

    public void setIndirizzoresidenza(String indirizzoresidenza) {
        this.indirizzoresidenza = indirizzoresidenza;
    }

    public String getCivicodomicilio() {
        return civicodomicilio;
    }

    public void setCivicodomicilio(String civicodomicilio) {
        this.civicodomicilio = civicodomicilio;
    }

    public String getCivicoresidenza() {
        return civicoresidenza;
    }

    public void setCivicoresidenza(String civicoresidenza) {
        this.civicoresidenza = civicoresidenza;
    }

    public String getCapdomicilio() {
        return capdomicilio;
    }

    public void setCapdomicilio(String capdomicilio) {
        this.capdomicilio = capdomicilio;
    }

    public String getCapresidenza() {
        return capresidenza;
    }

    public void setCapresidenza(String capresidenza) {
        this.capresidenza = capresidenza;
    }

    public String getProtocollo() {
        return protocollo;
    }

    public void setProtocollo(String protocollo) {
        this.protocollo = protocollo;
    }

    public String getEsito() {
        return esito;
    }

    public void setEsito(String esito) {
        this.esito = esito;
    }

    public Comuni getComune_nascita() {
        return comune_nascita;
    }

    public void setComune_nascita(Comuni comune_nascita) {
        this.comune_nascita = comune_nascita;
    }

    public Comuni getComune_residenza() {
        return comune_residenza;
    }

    public void setComune_residenza(Comuni comune_residenza) {
        this.comune_residenza = comune_residenza;
    }

    public Comuni getComune_domicilio() {
        return comune_domicilio;
    }

    public void setComune_domicilio(Comuni comune_domicilio) {
        this.comune_domicilio = comune_domicilio;
    }

    public SoggettiAttuatori getSoggetto() {
        return soggetto;
    }

    public void setSoggetto(SoggettiAttuatori soggetto) {
        this.soggetto = soggetto;
    }

    public TitoliStudio getTitoloStudio() {
        return titoloStudio;
    }

    public void setTitoloStudio(TitoliStudio titoloStudio) {
        this.titoloStudio = titoloStudio;
    }

    public ProgettiFormativi getProgetto() {
        return progetto;
    }

    public void setProgetto(ProgettiFormativi progetto) {
        this.progetto = progetto;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public Date getScadenzadocid() {
        return scadenzadocid;
    }

    public void setScadenzadocid(Date scadenzadocid) {
        this.scadenzadocid = scadenzadocid;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public Motivazione getMotivazione() {
        return motivazione;
    }

    public void setMotivazione(Motivazione motivazione) {
        this.motivazione = motivazione;
    }

    public String getNeet() {
        return neet;
    }

    public void setNeet(String neet) {
        this.neet = neet;
    }

    public List<Documenti_Allievi> getDocumenti() {
        List<Documenti_Allievi> docs = new ArrayList<>();//per fixare il bug dello stream  per le lazy list di EclipseLink
        docs.addAll(this.documenti);
        return docs;
    }

    public void setDocumenti(List<Documenti_Allievi> documenti) {
        this.documenti = documenti;
    }

    public Nazioni_rc getCittadinanza() {
        return cittadinanza;
    }

    public void setCittadinanza(Nazioni_rc cittadinanza) {
        this.cittadinanza = cittadinanza;
    }

    public Condizione_Mercato getCondizione_mercato() {
        return condizione_mercato;
    }

    public void setCondizione_mercato(Condizione_Mercato condizione_mercato) {
        this.condizione_mercato = condizione_mercato;
    }

    public Selfiemployment_Prestiti getSelfiemployement() {
        return selfiemployement;
    }

    public void setSelfiemployement(Selfiemployment_Prestiti selfiemployement) {
        this.selfiemployement = selfiemployement;
    }

    public StatoPartecipazione getStatopartecipazione() {
        return statopartecipazione;
    }

    public void setStatopartecipazione(StatoPartecipazione statopartecipazione) {
        this.statopartecipazione = statopartecipazione;
    }

    public String getIdea_impresa() {
        return idea_impresa;
    }

    public void setIdea_impresa(String idea_impresa) {
        this.idea_impresa = idea_impresa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getData_up() {
        return data_up;
    }

    public void setData_up(Date data_up) {
        this.data_up = data_up;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Condizione_Lavorativa getCondizione_lavorativa() {
        return condizione_lavorativa;
    }

    public void setCondizione_lavorativa(Condizione_Lavorativa condizione_lavorativa) {
        this.condizione_lavorativa = condizione_lavorativa;
    }

    public double getOre_fa() {
        return ore_fa;
    }

    public double getOre_fb() {
        return ore_fb;
    }

    public void setOre_fa(double ore_fa) {
        this.ore_fa = ore_fa;
    }

    public void setOre_fb(double ore_fb) {
        this.ore_fb = ore_fb;
    }

    public double getImporto() {
        return importo;
    }

    public void setImporto(double importo) {
        this.importo = importo;
    }

    public String getPrivacy2() {
        return privacy2;
    }

    public void setPrivacy2(String privacy2) {
        this.privacy2 = privacy2;
    }

    public String getPrivacy3() {
        return privacy3;
    }

    public void setPrivacy3(String privacy3) {
        this.privacy3 = privacy3;
    }

    public String getStato_nascita() {
        return stato_nascita;
    }

    public void setStato_nascita(String stato_nascita) {
        this.stato_nascita = stato_nascita;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Allievi)) {
            return false;
        }
        Allievi other = (Allievi) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, JSON_STYLE);
    }
}
