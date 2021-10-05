/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author rcosco
 */
@Entity
@Table(name = "formagiuridica")
public class Formagiuridica {
    @Id
    @Column(name = "idformagiuridica")
    private int id;
    @Column(name = "descrizione")
    private String descrizione;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final Formagiuridica other = (Formagiuridica) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "Formagiuridica{" + "id=" + id + ", descrizione=" + descrizione + '}';
    }
}
