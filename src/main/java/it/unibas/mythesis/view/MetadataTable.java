/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.mythesis.view;

import it.unibas.mythesis.model.Metadata;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 *
 * @author raffa
 */
public class MetadataTable extends AbstractTableModel{
    
    private List<Metadata> pdfMetadata;
    
    public MetadataTable(List<Metadata> metadata){
        this.pdfMetadata = metadata;
    }

    public boolean isVuota(){
        if(pdfMetadata.isEmpty() || pdfMetadata == null){
            return true;
        }
        return false;
    }

    @Override
    public int getRowCount() {
        if(this.pdfMetadata == null){
            return 0;
        }
        return pdfMetadata.size();
    }
    
    @Override
    public int getColumnCount() {
        return 2;
    }
    
    @Override
    public String getColumnName(int column){
        if (column == 0) {
            return "Property";
        }
        if (column == 1) {
            return "Value";
        }
        return "";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(pdfMetadata == null){
            return "";
        }
        Metadata metadata = this.pdfMetadata.get(rowIndex);
        if (columnIndex == 0) {
            return metadata.getMetadataKey();
        }
        if (columnIndex == 1) {
            return metadata.getMetadataValue();
        }
        return "";
    }
    
    public void update() {
        this.fireTableDataChanged();
    }
    
}
