/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unibas.mythesis.model;

import it.unibas.mythesis.Application;


/**
 *
 * @author raffa
 */
public class Metadata {
    
    private String metadataKey;
    private Object metadataValue;
    
    public Metadata(String key, Object value){
        this.metadataKey = key;
        this.metadataValue = value;
    }

    public String getMetadataKey() {
        return metadataKey;
    }

    public void setMetadataKey(String metadataKey) {
        this.metadataKey = metadataKey;
    }

    public Object getMetadataValue() {
        return metadataValue;
    }

    public void setMetadataValue(Object metadataValue) {
        this.metadataValue = metadataValue;
    }
    
    public boolean isEmpty(String metadataKey){
        Object ob = Application.getInstance().getModel().getBean(metadataKey);
        if(ob == null){
            return true;
        }
        return false;
    }
    
}
