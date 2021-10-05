/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author dolivo
 */
@Entity
@Table(name = "lezione_calendario")
@NamedQueries(value = {
    @NamedQuery(name = "lezioni.byModello", query = "SELECT l FROM LezioneCalendario l WHERE l.modello=:modello")
}
)
public class LezioneCalendario implements Serializable {

    @Id
    @Column(name = "id_lezionecalendario")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "lezione")
    private int lezione;
    @Column(name = "modello")
    private int modello;
    @Column(name = "ore")
    private double ore;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codice_ud")
    private UnitaDidattiche unitadidattica;
    
    @Transient
    private boolean doppia;
    @Transient 
    private Long id_cal1;
    @Transient 
    private Long id_cal2;
    @Transient 
    private double ore1;
    @Transient 
    private double ore2;
    @Transient
    private String ud1;
    @Transient
    private String ud2;
    
    @OneToMany(mappedBy = "lezione_calendario", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Lezioni_Modelli> lezioni;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLezione() {
        return lezione;
    }

    public void setLezione(int lezione) {
        this.lezione = lezione;
    }

    public int getModello() {
        return modello;
    }

    public void setModello(int modello) {
        this.modello = modello;
    }

    public double getOre() {
        return ore;
    }

    public void setOre(double ore) {
        this.ore = ore;
    }

    public UnitaDidattiche getUnitadidattica() {
        return unitadidattica;
    }

    public void setUnitadidattica(UnitaDidattiche unitadidattica) {
        this.unitadidattica = unitadidattica;
    }

    public List<Lezioni_Modelli> getLezioni() {
        List<Lezioni_Modelli> lezioni_list = new ArrayList<>();//per fixare il bub dello stream  per le lazy list di EclipseLink
        lezioni_list.addAll(this.lezioni);
        return lezioni_list;
    }

    public void setLezioni(List<Lezioni_Modelli> lezioni) {
        this.lezioni = lezioni;
    }

    public boolean isDoppia() {
        return doppia;
    }

    public void setDoppia(boolean doppia) {
        this.doppia = doppia;
    }

    public double getOre1() {
        return ore1;
    }

    public void setOre1(double ore1) {
        this.ore1 = ore1;
    }

    public double getOre2() {
        return ore2;
    }

    public void setOre2(double ore2) {
        this.ore2 = ore2;
    }

    public String getUd1() {
        return ud1;
    }

    public void setUd1(String ud1) {
        this.ud1 = ud1;
    }

    public String getUd2() {
        return ud2;
    }

    public void setUd2(String ud2) {
        this.ud2 = ud2;
    }

    public Long getId_cal1() {
        return id_cal1;
    }

    public void setId_cal1(Long id_cal1) {
        this.id_cal1 = id_cal1;
    }

    public Long getId_cal2() {
        return id_cal2;
    }

    public void setId_cal2(Long id_cal2) {
        this.id_cal2 = id_cal2;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.id);
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
        final LezioneCalendario other = (LezioneCalendario) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LezioneCalendario{" + "id=" + id + ", lezione=" + lezione + ", modello=" + modello + ", ore=" + ore + ", unitadidattica=" + unitadidattica + '}';
    }

}
