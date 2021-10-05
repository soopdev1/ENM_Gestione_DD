/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author dolivo
 */
@Entity
@Table(name = "modelli_progetti")
public class ModelliPrg implements Serializable {

    @Id
    @Column(name = "id_modello")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "stato")
    private String stato;
    @Column(name = "modello")
    private int modello;
    @Column(name = "data_modifica", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date data_modifica;

    @ManyToOne
    @JoinColumn(name = "id_progettoformativo")
    private ProgettiFormativi progetto;

    @OneToMany(mappedBy = "modello", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Lezioni_Modelli> lezioni;
    
    @Column(name = "scelta_modello6")
    private int scelta_modello6;
    
    @Column(name = "indirizzo_modello6")
    private String indirizzo_modello6;
    
    @Column(name = "civico_modello6")
    private String civico_modello6;
    
    @ManyToOne
    @JoinColumn(name = "comune_modello6")
    private Comuni comune_modello6;

    public ModelliPrg() {
    }

    public ModelliPrg(String stato, int modello, Date data_modifica, ProgettiFormativi progetto) {
        this.stato = stato;
        this.modello = modello;
        this.data_modifica = data_modifica;
        this.progetto = progetto;
    }

    public ModelliPrg(int modello, Date data_modifica, ProgettiFormativi progetto, int scelta_modello6, String indirizzo_modello6, String civico_modello6, Comuni comune_modello6) {
        this.modello = modello;
        this.data_modifica = data_modifica;
        this.progetto = progetto;
        this.scelta_modello6 = scelta_modello6;
        this.indirizzo_modello6 = indirizzo_modello6;
        this.civico_modello6 = civico_modello6;
        this.comune_modello6 = comune_modello6;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public int getModello() {
        return modello;
    }

    public void setModello(int modello) {
        this.modello = modello;
    }

    public Date getData_modifica() {
        return data_modifica;
    }

    public void setData_modifica(Date data_modifica) {
        this.data_modifica = data_modifica;
    }

    public ProgettiFormativi getProgetto() {
        return progetto;
    }

    public void setProgetto(ProgettiFormativi progetto) {
        this.progetto = progetto;
    }

    public List<Lezioni_Modelli> getLezioni() {
        List<Lezioni_Modelli> lezioni_list = new LinkedList<>();//per fixare il bub dello stream  per le lazy list di EclipseLink
        lezioni_list.addAll(this.lezioni);
        return lezioni_list;
    }

    public void setLezioni(List<Lezioni_Modelli> lezioni) {
        this.lezioni = lezioni;
    }

    public int getScelta_modello6() {
        return scelta_modello6;
    }

    public void setScelta_modello6(int scelta_modello6) {
        this.scelta_modello6 = scelta_modello6;
    }

    public String getIndirizzo_modello6() {
        return indirizzo_modello6;
    }

    public void setIndirizzo_modello6(String indirizzo_modello6) {
        this.indirizzo_modello6 = indirizzo_modello6;
    }

    public String getCivico_modello6() {
        return civico_modello6;
    }

    public void setCivico_modello6(String civico_modello6) {
        this.civico_modello6 = civico_modello6;
    }

    public Comuni getComune_modello6() {
        return comune_modello6;
    }

    public void setComune_modello6(Comuni comune_modello6) {
        this.comune_modello6 = comune_modello6;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
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
        final ModelliPrg other = (ModelliPrg) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    

    @Override
    public String toString() {
        return "ModelliPrg{" + "id=" + id + ", stato=" + stato + ", modello=" + modello + ", data_modifica=" + data_modifica + ", progetto=" + progetto + '}';
    }

}
