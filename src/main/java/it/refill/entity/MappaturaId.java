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
public class MappaturaId {
    String id,mappato;

    public MappaturaId() {
    }

    
    public MappaturaId(String id, String mappato) {
        this.id = id;
        this.mappato = mappato;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMappato() {
        return mappato;
    }

    public void setMappato(String mappato) {
        this.mappato = mappato;
    }
    
    
}
