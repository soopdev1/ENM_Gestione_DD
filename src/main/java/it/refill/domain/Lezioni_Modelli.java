/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.persistence.Transient;

/**
 *
 * @author dolivo
 */
@Entity
@Table(name = "lezioni_modelli")
@NamedQueries(value = {
    @NamedQuery(name = "lezmod.ByModello", query = "SELECT l FROM Lezioni_Modelli l WHERE l.modello=:modello"),})
public class Lezioni_Modelli implements Serializable {

    @Id
    @Column(name = "id_lezionimodelli")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "giorno")
    @Temporal(TemporalType.DATE)
    private Date giorno;

    @Temporal(TemporalType.TIME)
    @Column(name = "orario_start")
    private Date orario_start;

    @Temporal(TemporalType.TIME)
    @Column(name = "orario_end")
    private Date orario_end;

    @Column(name = "data_modifica", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date data_modifica;

    @Transient
    private int idprogettoformativo;
    @Transient
    private String codice_ud;

    @ManyToOne
    @JoinColumn(name = "id_modelli_progetto")
    private ModelliPrg modello;

    @ManyToOne
    @JoinColumn(name = "id_lezionecalendario")
    private LezioneCalendario lezione_calendario;

    @ManyToOne
    @JoinColumn(name = "id_docente")
    private Docenti docente;

    //Gruppo modello 4
    @Column(name = "gruppo_faseB")
    private int gruppo_faseB;

    @Transient
    private String orainizio;
    @Transient
    private String orafine;

    public String getOrainizio() {
        return Utility.sdfHHMM.format(this.orario_start);
    }

    public void setOrainizio(String orainizio) {
        this.orainizio = Utility.sdfHHMM.format(this.orario_start);
    }

    public String getOrafine() {
        return Utility.sdfHHMM.format(this.orario_end);
    }

    public void setOrafine(String orafine) {
        this.orafine = Utility.sdfHHMM.format(this.orario_end);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getOrario_start() {
        return orario_start;
    }

    public void setOrario_start(Date orario_start) {
        this.orario_start = orario_start;
    }

    public Date getOrario_end() {
        return orario_end;
    }

    public void setOrario_end(Date orario_end) {
        this.orario_end = orario_end;
    }

    public Date getData_modifica() {
        return data_modifica;
    }

    public void setData_modifica(Date data_modifica) {
        this.data_modifica = data_modifica;
    }

    public ModelliPrg getModello() {
        return modello;
    }

    public void setModello(ModelliPrg modello) {
        this.modello = modello;
    }

    public LezioneCalendario getLezione_calendario() {
        return lezione_calendario;
    }

    public void setLezione_calendario(LezioneCalendario lezione_calendario) {
        this.lezione_calendario = lezione_calendario;
    }

    public Date getGiorno() {
        return giorno;
    }

    public void setGiorno(Date giorno) {
        this.giorno = giorno;
    }

    public Docenti getDocente() {
        return docente;
    }

    public void setDocente(Docenti docente) {
        this.docente = docente;
    }

    public int getIdprogettoformativo() {
        return idprogettoformativo;
    }

    public void setIdprogettoformativo(int idprogettoformativo) {
        this.idprogettoformativo = idprogettoformativo;
    }

    public String getCodice_ud() {
        return codice_ud;
    }

    public void setCodice_ud(String codice_ud) {
        this.codice_ud = codice_ud;
    }

    public int getGruppo_faseB() {
        return gruppo_faseB;
    }

    public void setGruppo_faseB(int gruppo_faseB) {
        this.gruppo_faseB = gruppo_faseB;
    }

    public Lezioni_Modelli() {

    }

    public Lezioni_Modelli(Date giorno, Date orario_start, Date orario_end, Date data_modifica, ModelliPrg modello, LezioneCalendario lezione_calendario, Docenti docente) {
        this.giorno = giorno;
        this.orario_start = orario_start;
        this.orario_end = orario_end;
        this.data_modifica = data_modifica;
        this.modello = modello;
        this.lezione_calendario = lezione_calendario;
        this.docente = docente;
    }

    public Lezioni_Modelli(Date giorno, Date orario_start, Date orario_end, Date data_modifica, ModelliPrg modello, LezioneCalendario lezione_calendario, Docenti docente, int gruppo_faseB) {
        this.giorno = giorno;
        this.orario_start = orario_start;
        this.orario_end = orario_end;
        this.data_modifica = data_modifica;
        this.modello = modello;
        this.lezione_calendario = lezione_calendario;
        this.docente = docente;
        this.gruppo_faseB = gruppo_faseB;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.id);
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
        final Lezioni_Modelli other = (Lezioni_Modelli) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Lezioni_Modelli{" + "id=" + id + ", orario_start=" + orario_start + ", orario_end=" + orario_end + ", data_modifica=" + data_modifica + ", modello=" + modello + ", lezione_calendario=" + lezione_calendario + '}';
    }

}
