/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.mythesis.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author raffa
 */
public class Model {
    
    private Map<String, Object> beansMap = new HashMap<>();
    
    public void putBean(String key, Object value){
        this.beansMap.put(key, value);
    }
    
    public Object getBean(String key){
        return this.beansMap.get(key);
    }
    
    public boolean giaPresente(String key){
        if(beansMap.containsKey(key)){
            return true;
        }
        return false;
    }
    
}
