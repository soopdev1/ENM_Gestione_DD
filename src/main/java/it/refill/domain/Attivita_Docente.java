package it.refill.domain;

import it.refill.util.Utility;
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
@Entity
@Table(name = "attivita_docente")
@NamedQueries(value = {
    @NamedQuery(name = "attivitadocente.ByDocente", query = "SELECT a FROM Attivita_Docente a WHERE a.docente=:docente"),})
public class Attivita_Docente implements Serializable {

    @Id
    @Column(name = "id_attivita_docente")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "tipologia_di_attivita")
    private int tipologia_di_attivita;

    @Column(name = "committente")
    private String committente;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_inizio_periodo_di_riferimento")
    private Date data_inizio_periodo_di_riferimento;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_fine_periodo_di_riferimento")
    private Date data_fine_periodo_di_riferimento;

    @Column(name = "durata")
    private int durata;

    @Column(name = "unita_di_misura")
    private String unita_di_misura;

    @Column(name = "tipologia_di_incarico")
    private int tipologia_di_incarico;

    @Column(name = "fonte_di_finanziamento")
    private int fonte_di_finanziamento;

    @Column(name = "riferimento")
    private int riferimento;

    @ManyToOne
    @JoinColumn(name = "id_docente")
    private Docenti docente;

    public Docenti getDocente() {
        return docente;
    }

    public void setDocente(Docenti docente) {
        this.docente = docente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTipologia_di_attivita() {
        return tipologia_di_attivita;
    }

    public void setTipologia_di_attivita(int tipologia_di_attivita) {
        this.tipologia_di_attivita = tipologia_di_attivita;
    }

    public String getCommittente() {
        return committente;
    }

    public void setCommittente(String committente) {
        this.committente = committente;
    }

    public Date getData_inizio_periodo_di_riferimento() {
        return data_inizio_periodo_di_riferimento;
    }

    public void setData_inizio_periodo_di_riferimento(Date data_inizio_periodo_di_riferimento) {
        this.data_inizio_periodo_di_riferimento = data_inizio_periodo_di_riferimento;
    }

    public Date getData_fine_periodo_di_riferimento() {
        return data_fine_periodo_di_riferimento;
    }

    public void setData_fine_periodo_di_riferimento(Date data_fine_periodo_di_riferimento) {
        this.data_fine_periodo_di_riferimento = data_fine_periodo_di_riferimento;
    }

    public int getDurata() {
        return durata;
    }

    public void setDurata(int durata) {
        this.durata = durata;
    }

    public String getUnita_di_misura() {
        return unita_di_misura;
    }

    public void setUnita_di_misura(String unita_di_misura) {
        this.unita_di_misura = unita_di_misura;
    }

    public int getTipologia_di_incarico() {
        return tipologia_di_incarico;
    }

    public void setTipologia_di_incarico(int tipologia_di_incarico) {
        this.tipologia_di_incarico = tipologia_di_incarico;
    }

    public int getFonte_di_finanziamento() {
        return fonte_di_finanziamento;
    }

    public void setFonte_di_finanziamento(int fonte_di_finanziamento) {
        this.fonte_di_finanziamento = fonte_di_finanziamento;
    }

    public int getRiferimento() {
        return riferimento;
    }

    public void setRiferimento(int riferimento) {
        this.riferimento = riferimento;
    }

    public Attivita_Docente() {
    }

    public Attivita_Docente(int tipologia_di_attivita, String committente, Date data_inizio_periodo_di_riferimento, Date data_fine_periodo_di_riferimento, int durata, String unita_di_misura, int tipologia_di_incarico, int fonte_di_finanziamento, int riferimento, Docenti docente) {
        this.tipologia_di_attivita = tipologia_di_attivita;
        this.committente = committente;
        this.data_inizio_periodo_di_riferimento = data_inizio_periodo_di_riferimento;
        this.data_fine_periodo_di_riferimento = data_fine_periodo_di_riferimento;
        this.durata = durata;
        this.unita_di_misura = unita_di_misura;
        this.tipologia_di_incarico = tipologia_di_incarico;
        this.fonte_di_finanziamento = fonte_di_finanziamento;
        this.riferimento = riferimento;
        this.docente = docente;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final Attivita_Docente other = (Attivita_Docente) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Attivita_Docente{id=").append(id);
        sb.append(", tipologia_di_attivita=").append(tipologia_di_attivita);
        sb.append(", committente=").append(committente);
        sb.append(", data_inizio_periodo_di_riferimento=").append(data_inizio_periodo_di_riferimento);
        sb.append(", data_fine_periodo_di_riferimento=").append(data_fine_periodo_di_riferimento);
        sb.append(", durata=").append(durata);
        sb.append(", unita_di_misura=").append(unita_di_misura);
        sb.append(", tipologia_di_incarico=").append(tipologia_di_incarico);
        sb.append(", fonte_di_finanziamento=").append(fonte_di_finanziamento);
        sb.append(", riferimento=").append(riferimento);
        sb.append(", docente=").append(docente);
        sb.append('}');
        return sb.toString();
    }

    

}

