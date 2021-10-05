/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.entity;

import java.util.Date;

/**
 *
 * @author dolivo
 */
public class ProgettiLezioniModelli {
    
   private Long id_prg;
   private Date max_date_m3;

    public ProgettiLezioniModelli() {
    }

    public ProgettiLezioniModelli(Long id_prg, Date max_date_m3) {
        this.id_prg = id_prg;
        this.max_date_m3 = max_date_m3;
    }

    public Long getId_prg() {
        return id_prg;
    }

    public void setId_prg(Long id_prg) {
        this.id_prg = id_prg;
    }

    public Date getMax_date_m3() {
        return max_date_m3;
    }

    public void setMax_date_m3(Date max_date_m3) {
        this.max_date_m3 = max_date_m3;
    }

    @Override
    public String toString() {
        return "ProgettiLezioniM3{" + "id_prg=" + id_prg + ", max_date_m3=" + max_date_m3 + '}';
    }
   
   
   
}
