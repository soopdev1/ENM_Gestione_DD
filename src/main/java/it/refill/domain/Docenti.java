/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "docenti")
@NamedQueries(value = {
    @NamedQuery(name = "d.Active", query = "SELECT d FROM Docenti d WHERE d.stato='A'"),
    @NamedQuery(name = "d.All", query = "SELECT d FROM Docenti d"),
    @NamedQuery(name = "d.byProgetto", query = "SELECT d FROM Docenti d WHERE d.progetti=:progetto"),
    @NamedQuery(name = "d.bySA", query = "SELECT d FROM Docenti d WHERE d.soggetto=:soggetto"),
    @NamedQuery(name = "d.byCf", query = "SELECT d FROM Docenti d WHERE d.codicefiscale=:cf"),
    @NamedQuery(name = "d.byEmail", query = "SELECT d FROM Docenti d WHERE d.email=:email AND d.soggetto=:soggetto AND d.stato <> 'R'"),
    @NamedQuery(name = "d.byCf_SA", query = "SELECT d FROM Docenti d WHERE d.codicefiscale=:cf AND d.soggetto=:soggetto AND d.stato <> 'R'"),
    @NamedQuery(name = "d.bySA_Active", query = "SELECT d FROM Docenti d WHERE d.soggetto=:soggetto AND d.stato = 'A'"),
})
    

@JsonIgnoreProperties(value = {"progetti", "registri_aula", "registri_allievi","attivita"})
public class Docenti implements Serializable {

    @Id
    @Column(name = "iddocenti")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "nome")
    private String nome;
    @Column(name = "cognome")
    private String cognome;
    @Column(name = "codicefiscale")
    private String codicefiscale;
    @Column(name = "datanascita")
    @Temporal(TemporalType.DATE)
    private Date datanascita;
    @Column(name = "curriculum")
    private String curriculum;
    @Column(name = "docId")
    private String docId;
    @Column(name = "richiesta_accr")
    private String richiesta_accr;
    @Column(name = "stato")
    private String stato = "DV";
    
    
    @Column(name = "scadenza_doc")
    @Temporal(TemporalType.DATE)
    private Date scadenza_doc;
    @Column(name = "email")
    private String email;

    @Column(name = "datawebinair")
    @Temporal(TemporalType.DATE)
    private Date datawebinair;

    @ManyToOne
    @JoinColumn(name = "fascia")
    private FasceDocenti fascia;
    
    
    @ManyToOne
    @JoinColumn(name = "idsoggetti_attuatori")
    SoggettiAttuatori soggetto;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "progetti_docenti",
            joinColumns = @JoinColumn(name = "iddocenti"),
            inverseJoinColumns = @JoinColumn(name = "idprogetti_formativi"))
    List<ProgettiFormativi> progetti;

    @OneToMany(mappedBy = "docente", fetch = FetchType.LAZY)
    List<DocumentiPrg> registri_aula;
    @OneToMany(mappedBy = "docente", fetch = FetchType.LAZY)
    List<Documenti_Allievi> registri_allievi;

    @OneToMany(mappedBy = "docente", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Lezioni_Modelli> lezioni;
    
    @Transient
    String descrizionestato;
    
    
    @Column(name = "pec")
    private String pec;
    @Column(name = "cellulare")
    private String cellulare;
    @Column(name = "regione_di_residenza")
    private String regione_di_residenza;
    @Column(name = "comune_di_nascita")
    private String comune_di_nascita;
    @Column(name = "titolo_di_studio")
    private int titolo_di_studio;
    @Column(name = "area_prevalente_di_qualificazione")
    private int area_prevalente_di_qualificazione;
    @Column(name = "inquadramento")
    private int inquadramento;
    
    /*Per un eventuale rigetto del docente da parte del MC*/
    @Column(name = "motivo")
    private String motivo;
    /*Per differenziare inserimento (automatico - da accreditamento/manuale - da piattaforma) */
    @Column(name = "tipo_inserimento", columnDefinition = "VARCHAR(255) default 'AUTOMATICO (PIATTAFORMA ACCREDITAMENTO)'")
    private String tipo_inserimento;
    
    
    @OneToMany(mappedBy = "docente", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Attivita_Docente> attivita;
    
    
    public Docenti(String nome, String cognome, String codicefiscale, Date datanascita) {
        this.nome = nome;
        this.cognome = cognome;
        this.codicefiscale = codicefiscale;
        this.datanascita = datanascita;
    }

    public Docenti(String nome, String cognome, String codicefiscale, Date datanascita, String email) {
        this.nome = nome;
        this.cognome = cognome;
        this.codicefiscale = codicefiscale;
        this.datanascita = datanascita;
        this.email = email;
    }

    public Docenti(Long id, String nome, String cognome, String codicefiscale, Date datanascita, String curriculum, String docId, String stato, List<ProgettiFormativi> progetti) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.codicefiscale = codicefiscale;
        this.datanascita = datanascita;
        this.curriculum = curriculum;
        this.docId = docId;
        this.stato = stato;
        
        this.progetti = progetti;
    }

    public Docenti() {
    }
    
    public String getDescrizionestato() {
        if(this.stato == null){
            return "";
        }else if(this.stato.equals("A")){
            return "ACCREDITATO";
        }else if(this.stato.equals("DV")){
            return "DA VALIDARE";
        }else if(this.stato.equals("R")){
            return "RIGETTATO";
        }
        return "";
    }

    public void setDescrizionestato() {
        if(this.stato == null){
            this.descrizionestato = "";
        }else if(this.stato.equals("A")){
            this.descrizionestato = "ACCREDITATO";
        }else if(this.stato.equals("DV")){
            this.descrizionestato = "DA VALIDARE";
        }else if(this.stato.equals("R")){
            this.descrizionestato = "RIGETTATO";
        }
        this.descrizionestato = "";
    }
    
    public SoggettiAttuatori getSoggetto() {
        return soggetto;
    }

    public void setSoggetto(SoggettiAttuatori soggetto) {
        this.soggetto = soggetto;
    }
    
    public Date getDatawebinair() {
        return datawebinair;
    }

    public void setDatawebinair(Date datawebinair) {
        this.datawebinair = datawebinair;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getRichiesta_accr() {
        return richiesta_accr;
    }

    public void setRichiesta_accr(String richiesta_accr) {
        this.richiesta_accr = richiesta_accr;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCodicefiscale() {
        return codicefiscale;
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

    public String getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
        setDescrizionestato();
    }

    public Date getScadenza_doc() {
        return scadenza_doc;
    }

    public void setScadenza_doc(Date scadenza_doc) {
        this.scadenza_doc = scadenza_doc;
    }

    public List<ProgettiFormativi> getProgetti() {
        List<ProgettiFormativi> progetti_list = new ArrayList<>();
        progetti_list.addAll(progetti);
        return progetti_list;
    }

    public void setProgetti(List<ProgettiFormativi> progetti) {
        this.progetti = progetti;
    }

    public List<DocumentiPrg> getRegistri_aula() {
        return registri_aula;
    }

    public void setRegistri_aula(List<DocumentiPrg> registri_aula) {
        this.registri_aula = registri_aula;
    }

    public List<Documenti_Allievi> getRegistri_allievi() {
        return registri_allievi;
    }

    public void setRegistri_allievi(List<Documenti_Allievi> registri_allievi) {
        this.registri_allievi = registri_allievi;
    }

    public FasceDocenti getFascia() {
        return fascia;
    }

    public void setFascia(FasceDocenti fascia) {
        this.fascia = fascia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Lezioni_Modelli> getLezioni() {
        List<Lezioni_Modelli> lezioni_list = new ArrayList<>();
        lezioni_list.addAll(this.lezioni);
        return lezioni_list;
    }

    public void setLezioni(List<Lezioni_Modelli> lezioni) {
        this.lezioni = lezioni;
    }

    public String getPec() {
        return pec;
    }

    public void setPec(String pec) {
        this.pec = pec;
    }

    public String getCellulare() {
        return cellulare;
    }

    public void setCellulare(String cellulare) {
        this.cellulare = cellulare;
    }

    public String getRegione_di_residenza() {
        return regione_di_residenza;
    }

    public void setRegione_di_residenza(String regione_di_residenza) {
        this.regione_di_residenza = regione_di_residenza;
    }

    public String getComune_di_nascita() {
        return comune_di_nascita;
    }

    public void setComune_di_nascita(String comune_di_nascita) {
        this.comune_di_nascita = comune_di_nascita;
    }

    public int getTitolo_di_studio() {
        return titolo_di_studio;
    }

    public void setTitolo_di_studio(int titolo_di_studio) {
        this.titolo_di_studio = titolo_di_studio;
    }

    public int getArea_prevalente_di_qualificazione() {
        return area_prevalente_di_qualificazione;
    }

    public void setArea_prevalente_di_qualificazione(int area_prevalente_di_qualificazione) {
        this.area_prevalente_di_qualificazione = area_prevalente_di_qualificazione;
    }

    public int getInquadramento() {
        return inquadramento;
    }

    public void setInquadramento(int inquadramento) {
        this.inquadramento = inquadramento;
    }

    public List<Attivita_Docente> getAttivita() {
        List<Attivita_Docente> a_list = new ArrayList<>();
        a_list.addAll(this.attivita);
        return a_list;
    }

    public void setAttivita(List<Attivita_Docente> attivita) {
        this.attivita = attivita;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getTipo_inserimento() {
        return tipo_inserimento;
    }

    public void setTipo_inserimento(String tipo_inserimento) {
        this.tipo_inserimento = tipo_inserimento;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Docenti other = (Docenti) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, JSON_STYLE);
    }

    

}
