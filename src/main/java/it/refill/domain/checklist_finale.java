/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author dolivo
 */
@Entity
@Table(name = "checklist_finale")
@NamedQueries(value = {
    @NamedQuery(name = "cl.byPF", query = "SELECT c FROM checklist_finale c WHERE c.progetto_formativo=:progetto_formativo"),
})
//@JsonIgnoreProperties(value = {"progetto_formativo"})
public class checklist_finale implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "file")
    private String file;

    @Column(name = "tot_contributo_indennita_frequenza_fa")
    private double tot_contributo_indennita_frequenza_fa;

    @Column(name = "tot_contributo_fb")
    private double tot_contributo_fb;

    @Column(name = "tab_neet_fa")
    private String tab_neet_fa;

    @Column(name = "tab_neet_fb")
    private String tab_neet_fb;

    @Column(name = "tot_docenza_fa")
    private double tot_docenza_fa;

    @Column(name = "nota_controllore")
    private String nota_controllore;

    @Column(name = "tab_completezza_output_neet")
    private String tab_completezza_output_neet;
    
    @Column(name = "tab_mappatura_neet")
    private String tab_mappatura_neet;

    @Column(name = "tot_output_conformi")
    private int tot_output_conformi;

    @Column(name = "tot_massimo_ammissibile")
    private double tot_massimo_ammissibile;

    @Column(name = "condizionalita_30perc")
    private double condizionalita_30perc;

    @Column(name = "vcr_70perc")
    private double vcr_70perc;

    @Column(name = "valore_unitario_condizionalita")
    private double valore_unitario_condizionalita;

    @Column(name = "tot_contributo_ammesso")
    private double tot_contributo_ammesso;

    @Column(name = "step")
    private int step;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", name = "timestamp", insertable = false)
    private Date timestamp;

    @ManyToOne
    @JoinColumn(name = "revisore")
    private Revisori revisore;

    @OneToOne(mappedBy = "checklist_finale")
    private ProgettiFormativi progetto_formativo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public double getTot_contributo_indennita_frequenza_fa() {
        return tot_contributo_indennita_frequenza_fa;
    }

    public void setTot_contributo_indennita_frequenza_fa(double tot_contributo_indennita_frequenza_fa) {
        this.tot_contributo_indennita_frequenza_fa = tot_contributo_indennita_frequenza_fa;
    }

    public double getTot_contributo_fb() {
        return tot_contributo_fb;
    }

    public void setTot_contributo_fb(double tot_contributo_fb) {
        this.tot_contributo_fb = tot_contributo_fb;
    }

    public String getTab_neet_fa() {
        return tab_neet_fa;
    }

    public void setTab_neet_fa(String tab_neet_fa) {
        this.tab_neet_fa = tab_neet_fa;
    }

    public String getTab_neet_fb() {
        return tab_neet_fb;
    }

    public void setTab_neet_fb(String tab_neet_fb) {
        this.tab_neet_fb = tab_neet_fb;
    }

    public double getTot_docenza_fa() {
        return tot_docenza_fa;
    }

    public void setTot_docenza_fa(double tot_docenza_fa) {
        this.tot_docenza_fa = tot_docenza_fa;
    }

    public String getNota_controllore() {
        return nota_controllore;
    }

    public void setNota_controllore(String nota_controllore) {
        this.nota_controllore = nota_controllore;
    }

    public String getTab_completezza_output_neet() {
        return tab_completezza_output_neet;
    }

    public void setTab_completezza_output_neet(String tab_completezza_output_neet) {
        this.tab_completezza_output_neet = tab_completezza_output_neet;
    }

    public int getTot_output_conformi() {
        return tot_output_conformi;
    }

    public void setTot_output_conformi(int tot_output_conformi) {
        this.tot_output_conformi = tot_output_conformi;
    }

    public double getTot_massimo_ammissibile() {
        return tot_massimo_ammissibile;
    }

    public void setTot_massimo_ammissibile(double tot_massimo_ammissibile) {
        this.tot_massimo_ammissibile = tot_massimo_ammissibile;
    }

    public double getCondizionalita_30perc() {
        return condizionalita_30perc;
    }

    public void setCondizionalita_30perc(double condizionalita_30perc) {
        this.condizionalita_30perc = condizionalita_30perc;
    }

    public double getVcr_70perc() {
        return vcr_70perc;
    }

    public void setVcr_70perc(double vcr_70perc) {
        this.vcr_70perc = vcr_70perc;
    }

    public double getValore_unitario_condizionalita() {
        return valore_unitario_condizionalita;
    }

    public void setValore_unitario_condizionalita(double valore_unitario_condizionalita) {
        this.valore_unitario_condizionalita = valore_unitario_condizionalita;
    }

    public double getTot_contributo_ammesso() {
        return tot_contributo_ammesso;
    }

    public void setTot_contributo_ammesso(double tot_contributo_ammesso) {
        this.tot_contributo_ammesso = tot_contributo_ammesso;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Revisori getRevisore() {
        return revisore;
    }

    public void setRevisore(Revisori revisore) {
        this.revisore = revisore;
    }

    public ProgettiFormativi getProgetto_formativo() {
        return progetto_formativo;
    }

    public void setProgetto_formativo(ProgettiFormativi progetto_formativo) {
        this.progetto_formativo = progetto_formativo;
    }

    public String getTab_mappatura_neet() {
        return tab_mappatura_neet;
    }

    public void setTab_mappatura_neet(String tab_mappatura_neet) {
        this.tab_mappatura_neet = tab_mappatura_neet;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("checklist_finale{id=").append(id);
        sb.append(", tipo=").append(tipo);
        sb.append(", file=").append(file);
        sb.append(", tot_contributo_indennita_frequenza_fa=").append(tot_contributo_indennita_frequenza_fa);
        sb.append(", tot_contributo_fb=").append(tot_contributo_fb);
        sb.append(", tab_neet_fa=").append(tab_neet_fa);
        sb.append(", tab_neet_fb=").append(tab_neet_fb);
        sb.append(", tot_docenza_fa=").append(tot_docenza_fa);
        sb.append(", nota_controllore=").append(nota_controllore);
        sb.append(", tab_completezza_output_neet=").append(tab_completezza_output_neet);
        sb.append(", tab_mappatura_neet=").append(tab_mappatura_neet);
        sb.append(", tot_output_conformi=").append(tot_output_conformi);
        sb.append(", tot_massimo_ammissibile=").append(tot_massimo_ammissibile);
        sb.append(", condizionalita_30perc=").append(condizionalita_30perc);
        sb.append(", vcr_70perc=").append(vcr_70perc);
        sb.append(", valore_unitario_condizionalita=").append(valore_unitario_condizionalita);
        sb.append(", tot_contributo_ammesso=").append(tot_contributo_ammesso);
        sb.append(", step=").append(step);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", revisore=").append(revisore);
        sb.append(", progetto_formativo=").append(progetto_formativo);
        sb.append('}');
        return sb.toString();
    }
    
    
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
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
        final checklist_finale other = (checklist_finale) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

}
