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
@Table(name = "documenti_unitadidattiche")
public class Documenti_UnitaDidattiche implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_docud")
    private Long id_docud;

    @Column(name = "path")
    private String path;
    
    @Column(name = "tipo")
    private String tipo;

    @Column(name = "deleted", columnDefinition = "INT(1) DEFAULT 0")
    private int deleted;
    
    @Column(name="data_modifica", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date data_modifica;
    @ManyToOne
    @JoinColumn(name = "codice_ud")
    UnitaDidattiche unita_didattica;

    public Long getId_docud() {
        return id_docud;
    }

    public void setId_docud(Long id_docud) {
        this.id_docud = id_docud;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public Date getData_modifica() {
        return data_modifica;
    }

    public void setData_modifica(Date data_modifica) {
        this.data_modifica = data_modifica;
    }

    public UnitaDidattiche getUnita_didattica() {
        return unita_didattica;
    }

    public void setUnita_didattica(UnitaDidattiche unita_didattica) {
        this.unita_didattica = unita_didattica;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.id_docud);
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
        final Documenti_UnitaDidattiche other = (Documenti_UnitaDidattiche) obj;
        if (!Objects.equals(this.id_docud, other.id_docud)) {
            return false;
        }
        return true;
    }

    public Documenti_UnitaDidattiche() {
    }

    public Documenti_UnitaDidattiche(String path, String tipo, int deleted, Date data_modifica, UnitaDidattiche unita_didattica) {
        this.path = path;
        this.tipo = tipo;
        this.deleted = deleted;
        this.data_modifica = data_modifica;
        this.unita_didattica = unita_didattica;
    }
    
    

    @Override
    public String toString() {
        return "Documenti_UnitaDidattiche{" + "id_docud=" + id_docud + ", path=" + path + ", tipo=" + tipo + ", deleted=" + deleted + ", data_modifica=" + data_modifica + ", unita_didattica=" + unita_didattica + '}';
    }

}
