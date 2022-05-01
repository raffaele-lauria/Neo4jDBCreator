/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.mythesis.view;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author raffa
 */
public class PDFsTable extends AbstractTableModel{
    
    private List<File> pdfList;
    
    public PDFsTable(List<File> list){
        this.pdfList = list;
    }
    
    public boolean isVuota(){
        if(pdfList.isEmpty() || pdfList == null){
            return true;
        }
        return false;
    }

    @Override
    public int getRowCount() {
        if(this.pdfList == null){
            return 0;
        }
        return pdfList.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }
    
    @Override
    public String getColumnName(int column){
        if (column == 0) {
            return "File name";
        }
        if(column == 1){
            return "Author";
        }
        return "";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(pdfList == null){
            return "";
        }
        PDDocumentInformation metadata = getMetadata(rowIndex);
        File file = pdfList.get(rowIndex);
        if (columnIndex == 0) {
            return file.getName();
        }
        if(columnIndex == 1){
            return metadata.getAuthor();
        }
        return "";
    }
    
    private PDDocumentInformation getMetadata(int index){
        try {
            var pdf = PDDocument.load(pdfList.get(index));
            PDDocumentInformation metadata = pdf.getDocumentInformation();
            pdf.close();
            return metadata;
        } catch (IOException ex) {
            Logger.getLogger(PDFsTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void update() {
        this.fireTableDataChanged();
    }
    
}
