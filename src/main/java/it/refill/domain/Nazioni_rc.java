/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.domain;

import static it.refill.util.Utility.cp_toUTF;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author rcosco
 */
@Entity
@Table(name = "nazioni_rc")
@NamedQueries(value = {
    @NamedQuery(name = "nazioni_rc.cf", query = "select n from Nazioni_rc n where n.codicefiscale <> '-' ORDER BY n.nome"),
    @NamedQuery(name = "nazioni_rc.NazioniTotale", query = "select n.nome from Nazioni_rc n where n.codicefiscale <> '-' ORDER BY n.nome"),
    @NamedQuery(name = "nazioni_rc.byCodiceFiscale", query = "select n from Nazioni_rc n where n.codicefiscale=:codicefiscale"),
})
public class Nazioni_rc implements Serializable {

    @Id
    @Column(name = "idnazione")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "ue")
    private String ue;
    @Column(name = "istat")
    private String istat;
    @Column(name = "nome")
    private String nome;
    @Column(name = "codicefiscale")
    private String codicefiscale;
    @Column(name = "unsd")
    private String unsd;
    @Column(name = "iso2")
    private String iso2;
    @Column(name = "iso3")
    private String iso3;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUe() {
        return ue;
    }

    public void setUe(String ue) {
        this.ue = ue;
    }

    public String getIstat() {
        return istat;
    }

    public void setIstat(String istat) {
        this.istat = istat;
    }

    public String getNome() {
        return cp_toUTF(nome);
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodicefiscale() {
        return codicefiscale;
    }

    public void setCodicefiscale(String codicefiscale) {
        this.codicefiscale = codicefiscale;
    }

    public String getUnsd() {
        return unsd;
    }

    public void setUnsd(String unsd) {
        this.unsd = unsd;
    }

    public String getIso2() {
        return iso2;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Comuni)) {
            return false;
        }
        Nazioni_rc other = (Nazioni_rc) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Nazioni_rc{" + "id=" + id + ", nome=" + nome + "}";
    }

}
