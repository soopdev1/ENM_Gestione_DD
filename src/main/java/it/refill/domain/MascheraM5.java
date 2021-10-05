/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author dolivo
 */
@Entity
@Table(name = "maschera_m5")
@NamedQueries(value = {
    @NamedQuery(name = "m5.byPF", query = "SELECT m FROM MascheraM5 m WHERE m.progetto_formativo=:progetto_formativo"),
    @NamedQuery(name = "m5.byAllievo", query = "SELECT m FROM MascheraM5 m WHERE m.allievo=:allievo")
})
public class MascheraM5 implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "progetto_formativo")
    private ProgettiFormativi progetto_formativo;

    @ManyToOne
    @JoinColumn(name = "allievo")
    private Allievi allievo;

    @ManyToOne
    @JoinColumn(name = "comune_localizzazione")
    private Comuni comune_localizzazione;

    @ManyToOne
    @JoinColumn(name = "forma_giuridica")
    private Formagiuridica forma_giuridica;
    
    @ManyToOne
    @JoinColumn(name = "ateco")
    private Ateco ateco;

    @Column(name = "ragione_sociale")
    private String ragione_sociale;
    @Column(name = "idea_impresa")
    private String idea_impresa;
    @Column(name = "motivazione")
    private String motivazione;

    @Column(name = "domanda_ammissione")
    private String domanda_ammissione;
    @Column(name = "domanda_ammissione_presente")
    private boolean domanda_ammissione_presente;

    @Column(name = "fabbisogno_finanziario")
    private double fabbisogno_finanziario;
    @Column(name = "finanziamento_richiesto_agevolazione")
    private double finanziamento_richiesto_agevolazione;

    @Column(name = "sede")
    private boolean sede;
    @Column(name = "colloquio")
    private boolean colloquio;

    @Column(name = "bando_se")
    private boolean bando_se;
    @Column(name = "bando_sud")
    private boolean bando_sud;
    @Column(name = "bando_reg")
    private boolean bando_reg;
    @Column(name = "no_agevolazione")
    private boolean no_agevolazione;

    @Column(name = "bando_se_opzione")
    private String bando_se_opzione;
    @Column(name = "bando_sud_opzione")
    private String bando_sud_opzione;
    @Column(name = "bando_reg_nome")
    private String bando_reg_nome;
    @Column(name = "no_agevolazione_opzione")
    private String no_agevolazione_opzione;

    @Column(name = "tabella_valutazionefinale_val")
    private String tabella_valutazionefinale_val;
    @Column(name = "tabella_valutazionefinale_totale")
    private double tabella_valutazionefinale_totale;
    @Column(name = "tabella_valutazionefinale_punteggio")
    private double tabella_valutazionefinale_punteggio;

    @Column(name = "tabella_premialita")
    private boolean tabella_premialita;

    @Column(name = "tabella_premialita_val")
    private String tabella_premialita_val;
    @Column(name = "tabella_premialita_totale")
    private double tabella_premialita_totale;
    @Column(name = "tabella_premialita_punteggio")
    private double tabella_premialita_punteggio;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", name = "timestamp", insertable = false)
    private Date timestamp;

    public MascheraM5() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Allievi getAllievo() {
        return allievo;
    }

    public void setAllievo(Allievi allievo) {
        this.allievo = allievo;
    }

    public Comuni getComune_localizzazione() {
        return comune_localizzazione;
    }

    public void setComune_localizzazione(Comuni comune_localizzazione) {
        this.comune_localizzazione = comune_localizzazione;
    }

    public Formagiuridica getForma_giuridica() {
        return forma_giuridica;
    }

    public void setForma_giuridica(Formagiuridica forma_giuridica) {
        this.forma_giuridica = forma_giuridica;
    }

    public String getRagione_sociale() {
        return ragione_sociale;
    }

    public void setRagione_sociale(String ragione_sociale) {
        this.ragione_sociale = ragione_sociale;
    }

    public String getIdea_impresa() {
        return idea_impresa;
    }

    public void setIdea_impresa(String idea_impresa) {
        this.idea_impresa = idea_impresa;
    }

    public String getMotivazione() {
        return motivazione;
    }

    public void setMotivazione(String motivazione) {
        this.motivazione = motivazione;
    }

    public String getDomanda_ammissione() {
        return domanda_ammissione;
    }

    public void setDomanda_ammissione(String domanda_ammissione) {
        this.domanda_ammissione = domanda_ammissione;
    }

    public double getFabbisogno_finanziario() {
        return fabbisogno_finanziario;
    }

    public void setFabbisogno_finanziario(double fabbisogno_finanziario) {
        this.fabbisogno_finanziario = fabbisogno_finanziario;
    }

    public double getFinanziamento_richiesto_agevolazione() {
        return finanziamento_richiesto_agevolazione;
    }

    public void setFinanziamento_richiesto_agevolazione(double finanziamento_richiesto_agevolazione) {
        this.finanziamento_richiesto_agevolazione = finanziamento_richiesto_agevolazione;
    }

    public boolean isSede() {
        return sede;
    }

    public void setSede(boolean sede) {
        this.sede = sede;
    }

    public boolean isColloquio() {
        return colloquio;
    }

    public void setColloquio(boolean colloquio) {
        this.colloquio = colloquio;
    }

    public boolean isBando_se() {
        return bando_se;
    }

    public void setBando_se(boolean bando_se) {
        this.bando_se = bando_se;
    }

    public boolean isBando_sud() {
        return bando_sud;
    }

    public void setBando_sud(boolean bando_sud) {
        this.bando_sud = bando_sud;
    }

    public boolean isBando_reg() {
        return bando_reg;
    }

    public void setBando_reg(boolean bando_reg) {
        this.bando_reg = bando_reg;
    }

    public boolean isNo_agevolazione() {
        return no_agevolazione;
    }

    public void setNo_agevolazione(boolean no_agevolazione) {
        this.no_agevolazione = no_agevolazione;
    }

    public String getBando_se_opzione() {
        return bando_se_opzione;
    }

    public void setBando_se_opzione(String bando_se_opzione) {
        this.bando_se_opzione = bando_se_opzione;
    }

    public String getBando_sud_opzione() {
        return bando_sud_opzione;
    }

    public void setBando_sud_opzione(String bando_sud_opzione) {
        this.bando_sud_opzione = bando_sud_opzione;
    }

    public String getBando_reg_nome() {
        return bando_reg_nome;
    }

    public void setBando_reg_nome(String bando_reg_nome) {
        this.bando_reg_nome = bando_reg_nome;
    }

    public String getNo_agevolazione_opzione() {
        return no_agevolazione_opzione;
    }

    public void setNo_agevolazione_opzione(String no_agevolazione_opzione) {
        this.no_agevolazione_opzione = no_agevolazione_opzione;
    }

    public String getTabella_valutazionefinale_val() {
        return tabella_valutazionefinale_val;
    }

    public void setTabella_valutazionefinale_val(String tabella_valutazionefinale_val) {
        this.tabella_valutazionefinale_val = tabella_valutazionefinale_val;
    }

    public double getTabella_valutazionefinale_totale() {
        return tabella_valutazionefinale_totale;
    }

    public void setTabella_valutazionefinale_totale(double tabella_valutazionefinale_totale) {
        this.tabella_valutazionefinale_totale = tabella_valutazionefinale_totale;
    }

    public double getTabella_valutazionefinale_punteggio() {
        return tabella_valutazionefinale_punteggio;
    }

    public void setTabella_valutazionefinale_punteggio(double tabella_valutazionefinale_punteggio) {
        this.tabella_valutazionefinale_punteggio = tabella_valutazionefinale_punteggio;
    }

    public boolean isTabella_premialita() {
        return tabella_premialita;
    }

    public void setTabella_premialita(boolean tabella_premialita) {
        this.tabella_premialita = tabella_premialita;
    }

    public String getTabella_premialita_val() {
        return tabella_premialita_val;
    }

    public void setTabella_premialita_val(String tabella_premialita_val) {
        this.tabella_premialita_val = tabella_premialita_val;
    }

    public double getTabella_premialita_totale() {
        return tabella_premialita_totale;
    }

    public void setTabella_premialita_totale(double tabella_premialita_totale) {
        this.tabella_premialita_totale = tabella_premialita_totale;
    }

    public double getTabella_premialita_punteggio() {
        return tabella_premialita_punteggio;
    }

    public void setTabella_premialita_punteggio(double tabella_premialita_punteggio) {
        this.tabella_premialita_punteggio = tabella_premialita_punteggio;
    }

    public boolean isDomanda_ammissione_presente() {
        return domanda_ammissione_presente;
    }

    public void setDomanda_ammissione_presente(boolean domanda_ammissione_presente) {
        this.domanda_ammissione_presente = domanda_ammissione_presente;
    }

    public ProgettiFormativi getProgetto_formativo() {
        return progetto_formativo;
    }

    public void setProgetto_formativo(ProgettiFormativi progetto_formativo) {
        this.progetto_formativo = progetto_formativo;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Ateco getAteco() {
        return ateco;
    }

    public void setAteco(Ateco ateco) {
        this.ateco = ateco;
    }
    
    
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.id);
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
        final MascheraM5 other = (MascheraM5) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MascheraM5{id=").append(id);
        sb.append(", progetto_formativo=").append(progetto_formativo);
        sb.append(", allievo=").append(allievo);
        sb.append(", comune_localizzazione=").append(comune_localizzazione);
        sb.append(", forma_giuridica=").append(forma_giuridica);
        sb.append(", ateco=").append(ateco);
        sb.append(", ragione_sociale=").append(ragione_sociale);
        sb.append(", idea_impresa=").append(idea_impresa);
        sb.append(", motivazione=").append(motivazione);
        sb.append(", domanda_ammissione=").append(domanda_ammissione);
        sb.append(", domanda_ammissione_presente=").append(domanda_ammissione_presente);
        sb.append(", fabbisogno_finanziario=").append(fabbisogno_finanziario);
        sb.append(", finanziamento_richiesto_agevolazione=").append(finanziamento_richiesto_agevolazione);
        sb.append(", sede=").append(sede);
        sb.append(", colloquio=").append(colloquio);
        sb.append(", bando_se=").append(bando_se);
        sb.append(", bando_sud=").append(bando_sud);
        sb.append(", bando_reg=").append(bando_reg);
        sb.append(", no_agevolazione=").append(no_agevolazione);
        sb.append(", bando_se_opzione=").append(bando_se_opzione);
        sb.append(", bando_sud_opzione=").append(bando_sud_opzione);
        sb.append(", bando_reg_nome=").append(bando_reg_nome);
        sb.append(", no_agevolazione_opzione=").append(no_agevolazione_opzione);
        sb.append(", tabella_valutazionefinale_val=").append(tabella_valutazionefinale_val);
        sb.append(", tabella_valutazionefinale_totale=").append(tabella_valutazionefinale_totale);
        sb.append(", tabella_valutazionefinale_punteggio=").append(tabella_valutazionefinale_punteggio);
        sb.append(", tabella_premialita=").append(tabella_premialita);
        sb.append(", tabella_premialita_val=").append(tabella_premialita_val);
        sb.append(", tabella_premialita_totale=").append(tabella_premialita_totale);
        sb.append(", tabella_premialita_punteggio=").append(tabella_premialita_punteggio);
        sb.append(", timestamp=").append(timestamp);
        sb.append('}');
        return sb.toString();
    }


}
