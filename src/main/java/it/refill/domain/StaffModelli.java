/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.domain;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.ColumnDefault;

/**
 *
 * @author dolivo
 */
@Entity
@Table(name = "staff_modelli")
@JsonIgnoreProperties(value = {"docenti"})
public class StaffModelli implements Serializable {

    @Id
    @Column(name = "id_staff")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Column(name = "nome")
    private String nome;
    
    @Column(name = "cognome")
    private String cognome;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "telefono")
    private String telefono;
    
    @Column(name = "ruolo")
    @ColumnDefault(value="Ospite")
    private String ruolo;
    
    @Column(name = "data_modifica", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date data_modifica;
    
    @Column(name = "attivo", columnDefinition = "INT(1) DEFAULT 1")
    private int attivo;
    
    @ManyToOne
    @JoinColumn(name = "id_progettoformativo")
    private ProgettiFormativi progetto;

    public StaffModelli() {
    }

    public StaffModelli(String nome, String cognome, String email, String telefono, ProgettiFormativi p, int attivo, String ruolo, Date data_modifica) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.telefono = telefono;
        this.progetto = p;
        this.attivo = attivo;
        this.ruolo = ruolo;
        this.data_modifica = data_modifica;
    }
    
    public ProgettiFormativi getProgetto() {
        return progetto;
    }

    public void setProgetto(ProgettiFormativi progetto) {
        this.progetto = progetto;
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

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public Date getData_modifica() {
        return data_modifica;
    }

    public void setData_modifica(Date data_modifica) {
        this.data_modifica = data_modifica;
    }

    public int getAttivo() {
        return attivo;
    }

    public void setAttivo(int attivo) {
        this.attivo = attivo;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final StaffModelli other = (StaffModelli) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StaffModelli{" + "id=" + id + ", nome=" + nome + ", cognome=" + cognome + ", email=" + email + ", telefono=" + telefono + ", ruolo=" + ruolo + ", data_modifica=" + data_modifica + ", attivo=" + attivo + ", progetto=" + progetto + '}';
    }

}
