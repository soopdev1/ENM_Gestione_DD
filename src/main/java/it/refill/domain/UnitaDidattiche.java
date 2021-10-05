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
@Table(name = "unita_didattiche")
@NamedQueries(value = {
    @NamedQuery(name = "unita_didattiche.Elenco", query = "select ud from UnitaDidattiche ud ORDER BY ud.ordine"),
    @NamedQuery(name = "unita_didattiche.Fasi", query = "select ud.fase from UnitaDidattiche ud GROUP BY ud.fase")
})
public class UnitaDidattiche implements Serializable {

    @Id
    @Column(name = "codice")
    private String codice;
    @Column(name = "descrizione")
    private String descrizione;
    @Column(name = "ordine")
    private int ordine;
    @Column(name = "ore")
    private double ore;
    @Column(name = "fase")
    private String fase;
    @OneToMany(mappedBy = "unita_didattica", fetch = FetchType.LAZY)
    @JsonIgnore        
    List<Documenti_UnitaDidattiche> documenti_ud;
    
    @Transient
    private int countDocs;
    
    public UnitaDidattiche() {
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public int getOrdine() {
        return ordine;
    }

    public void setOrdine(int ordine) {
        this.ordine = ordine;
    }

    public double getOre() {
        return ore;
    }

    public void setOre(double ore) {
        this.ore = ore;
    }

    public List<Documenti_UnitaDidattiche> getDocumenti_ud() {
        List<Documenti_UnitaDidattiche> docs = new ArrayList<>();//per fixare il bug dello stream  per le lazy list di EclipseLink
        docs.addAll(this.documenti_ud);
        return docs;
    }

    public void setDocumenti_ud(List<Documenti_UnitaDidattiche> documenti_ud) {
        this.documenti_ud = documenti_ud;
    }

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    public int getCountDocs() {
        return countDocs;
    }

    public void setCountDocs(int countDocs) {
        this.countDocs = countDocs;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.codice);
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
        final UnitaDidattiche other = (UnitaDidattiche) obj;
        if (!Objects.equals(this.codice, other.codice)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UnitaDidattiche{" + "codice=" + codice + ", descrizione=" + descrizione + ", ordine=" + ordine + ", ore=" + ore + ", fase=" + fase + ", documenti_ud=" + documenti_ud + ", countDocs=" + countDocs + '}';
    }

 
   

}
