/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.mythesis.control;

import it.unibas.mythesis.Application;
import it.unibas.mythesis.Constant;
import it.unibas.mythesis.model.Metadata;
import it.unibas.mythesis.view.MetadataPanel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class MetadataPanelControl {
    
    private Action tesseractAction = new TesseractAction();
    private Action analizePDFAction = new AnalizePDFAction();
    private Action closeAction = new CloseAction();
    private static Logger logger = LoggerFactory.getLogger(MetadataPanelControl.class);

    public Action getAnalizePDFAction(){
        return analizePDFAction;
    }
    
    public Action getCloseAction(){
        return closeAction;
    }
    
    public Action getTesseractAction(){
        return tesseractAction;
    }
    
    public class AnalizePDFAction extends AbstractAction{
        
        public AnalizePDFAction(){
            this.putValue(Action.NAME, "Extract metadata");
            this.putValue(Action.SHORT_DESCRIPTION, "Extract metadata from the selected PDF");
        }
        
        @Override
        public void actionPerformed(ActionEvent e){
            //Create a new File
            String filePath = (String) Application.getInstance().getModel().getBean(Constant.PATH);
            String parentPath = (String) Application.getInstance().getModel().getBean(Constant.PARENT_PATH);
            MetadataPanel mp = Application.getInstance().getMetadataPanel();
            if(filePath == null){
                logger.info("No file specified!");
                mp.setFilePathLabel("No file specified! Try again.");
                return;
            }
            logger.info("Folder containing the file: " + parentPath);
            File file = new File(filePath);
            boolean isReadable = file.canRead();
            logger.info("Is the file readable? " + isReadable);
            try {
                PDDocument pdf = PDDocument.load(file);
                logger.info("Is it encrypted? " + pdf.isEncrypted());
                logger.info("Number of pages = " + pdf.getNumberOfPages());
                extractMetadataPDF(pdf);
                Application.getInstance().getMetadataTablePanel().open();
                pdf.close();
            } catch (IOException ex) {
                System.out.println("Problem: " + ex);
                java.util.logging.Logger.getLogger(MetadataPanelControl.class.getName()).log(Level.SEVERE, "File non caricato", ex);
            }
        }
        
        //Extract metadata
        private void extractMetadataPDF(PDDocument pdf) {
            //Application.getInstance().getModel().putBean(Constant.PDF_METADATA, null);
            List<Metadata> metadataPDF = new ArrayList<>();
            PDDocumentInformation metadati = pdf.getDocumentInformation();
            metadataPDF.add(new Metadata("Title", metadati.getTitle()));
            metadataPDF.add(new Metadata("Author", metadati.getAuthor()));
            metadataPDF.add(new Metadata("Subject", metadati.getSubject()));
            metadataPDF.add(new Metadata("Keywords", metadati.getKeywords()));
            metadataPDF.add(new Metadata("Creator", metadati.getKeywords()));
            metadataPDF.add(new Metadata("Producer", metadati.getProducer()));
            metadataPDF.add(new Metadata("Trapped", metadati.getTrapped()));
            SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm");
            if (metadati.getCreationDate() == null) {
                metadataPDF.add(new Metadata("Creation Date", null));
                metadataPDF.add(new Metadata("Modification Date", null));
            } else {
                metadataPDF.add(new Metadata("Creation Date", df.format(metadati.getCreationDate().getTime())));
                if(metadati.getModificationDate() == null){
                    metadataPDF.add(new Metadata("Modification Date", null));
                } else {
                    metadataPDF.add(new Metadata("Modification Date", df.format(metadati.getModificationDate().getTime())));
                }
            }
            Application.getInstance().getModel().putBean(Constant.PDF_METADATA, metadataPDF);
            if(metadataPDF.isEmpty()){
                logger.info("Metadata list empty!");
            } else {
                logger.info("Metadata list NOT empty.");
            }
        }
    }
    
    public class CloseAction extends AbstractAction{

        public CloseAction(){
            this.putValue(Action.NAME, "Close");
            this.putValue(Action.SHORT_DESCRIPTION, "Close this window.");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            Application.getInstance().getMetadataPanel().close();
        }
        
    }
    
    public class TesseractAction extends AbstractAction{

        public TesseractAction(){
            this.putValue(Action.NAME, "Tesseract");
            this.putValue(Action.SHORT_DESCRIPTION, "Tesseract OCR.");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            /*Tesseract tesseract = new Tesseract();
            try {
                String percorso = (String) Application.getInstance().getModello().getBean(Constant.PATH);
                if(percorso == null){
                    System.out.println("Nessun percorso specificato!");
                    return;
                }
                tesseract.setDatapath(percorso);
                String estratto = tesseract.doOCR(new File(percorso));
                System.out.println("Estratto: " + estratto);
            } catch (TesseractException te) {
                te.printStackTrace();
            }*/
        }
        
    }
    
}
