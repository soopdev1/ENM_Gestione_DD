/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.entity;

/**
 *
 * @author rcosco
 */
public class OreId {

    String id, ore, totale;

    public OreId() {
    }

    public OreId(String id, String ore, String totale) {
        this.id = id;
        this.ore = ore;
        this.totale = totale;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOre() {
        return ore;
    }

    public void setOre(String ore) {
        this.ore = ore;
    }

    public String getTotale() {
        return totale;
    }

    public void setTotale(String totale) {
        this.totale = totale;
    }
    
}
