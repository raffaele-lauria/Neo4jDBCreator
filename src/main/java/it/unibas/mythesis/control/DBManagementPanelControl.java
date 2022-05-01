/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.mythesis.control;

import it.unibas.mythesis.Application;
import it.unibas.mythesis.Constant;
import it.unibas.mythesis.backend.DesktopBackend;
import it.unibas.mythesis.model.Metadata;
import it.unibas.mythesis.view.DBManagementPanel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.neo4j.driver.Driver;
import org.neo4j.exceptions.Neo4jException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author raffa
 */
public class DBManagementPanelControl {

    private static final Logger logger = LoggerFactory.getLogger(DBManagementPanelControl.class);
    private Driver driver;

    private Action closeAction = new CloseAction();
    private Action summaryAction = new SummaryAction();
    private Action createDBAction = new CreateDBAction();
    private Action dropDBAction = new DropDBAction();
    private Action detachDBAction = new DetachDBAction();
    private Action resetAction = new ResetAction();

    private Action azioneRiepilogoDB = new AzioneRiepilogoDB();
    
    public Action getResetAction(){
        return resetAction;
    }

    public Action getAzioneRiepilogoDB() {
        return azioneRiepilogoDB;
    }

    public Action getDetachDBAction() {
        return detachDBAction;
    }

    public Action getDropDBAction() {
        return dropDBAction;
    }

    public Action getAzioneCreaDB() {
        return createDBAction;
    }

    public Action getCloseAction() {
        return closeAction;
    }

    public Action getAzioneRiepilogo() {
        return summaryAction;
    }

    private class SummaryAction extends AbstractAction {

        public SummaryAction() {
            this.putValue(Action.NAME, "Summary");
            this.putValue(Action.SHORT_DESCRIPTION, "Show which PDFs you have chosen to insert");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            List<File> PDFList = (List<File>) Application.getInstance().getModel().getBean(Constant.PDF_LIST);
            logger.info("TOTAL PDFs INSERTED: " + PDFList.size());
            if (isPDFListEmpty(PDFList)) {
                Application.getInstance().getDBManagementPanel().setDropLabel("ERROR! No PDF file inserted! Try again. Drop the PDF files you want to insert HERE.");
                return;
            }
            extractAuthors(PDFList);
            Application.getInstance().getSummaryPanel().updateTable(PDFList);
            Application.getInstance().getSummaryPanel().open();
            List<String> authorsList = (List<String>) Application.getInstance().getModel().getBean(Constant.AUTHOR_LIST);
            if (authorsList.isEmpty()) {
                logger.info("Authors list empty!");
                return;
            }
            int i = 0;
            for (String author : authorsList) {
                i++;
                System.out.println("Author n. " + i + ", name: " + author);
            }
        }
    }

    private class CreateDBAction extends AbstractAction {

        public CreateDBAction() {
            this.putValue(Action.NAME, "Create DB");
            this.putValue(Action.SHORT_DESCRIPTION, "Create the database with the inserted PDFs");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //Check PDF list in Constant.PDF_LIST
            List<File> PDFList = (List<File>) Application.getInstance().getModel().getBean(Constant.PDF_LIST);
            DBManagementPanel ptp = Application.getInstance().getDBManagementPanel();
            if (isPDFListEmpty(PDFList)) {
                ptp.setUpdatesLabel("PDF list is empty!");
                logger.debug("ERROR: can't create the database if the PDF list is empty!");
                return;
            }
            extractAuthors(PDFList);
            //Connection with DBMS
            try {
                DesktopBackend db = Application.getInstance().getDesktopBackend();
                if (db == null) {
                    ptp.setUpdatesLabel("Connection failed!");
                    logger.error("Connection failed!");
                    return;
                }
                ptp.setUpdatesLabel("Start of database creation...");
                logger.info("Start of database creation...");
                db.createDB();
                ptp.setUpdatesLabel("Database successfully created!");
                logger.info("Database successfully created!");
                logger.info("How many PDFs are there?" + PDFList.size());
                //***NODES***
                //AUTHOR NODEs
                transazioneAutori(db);
                //PDF NODEs
                transazionePDF(db, PDFList);
                //YEAR OF CREATION NODEs
                transazioneAnnoDiCreazione(db, PDFList);
                //***RELATIONSHIP***
                //RELATIONSHIP AUTHOR - PDF
                createRelationshipsAP(db);
                //RELATIONSHIP CREATIONYEAR - PDF
                createRelationshipsCYP(db);
            } catch (Neo4jException ex) {
                ptp.setUpdatesLabel("Can't create the database!");
                logger.error("ERROR: " + ex.getLocalizedMessage());
            }
        }
    }

    private class DropDBAction extends AbstractAction {

        public DropDBAction() {
            this.putValue(Action.NAME, "Drop DB");
            this.putValue(Action.SHORT_DESCRIPTION, "Delete the database (if it exists)");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DBManagementPanel ptp = Application.getInstance().getDBManagementPanel();
            try {
                DesktopBackend db = Application.getInstance().getDesktopBackend();
                ptp.setUpdatesLabel("Begin drop DB phase...");
                db.dropDB();
                ptp.setUpdatesLabel("Database successfully deleted!");
            } catch (Exception ex) {
                ptp.setUpdatesLabel("ERROR: unable to delete the database!");
                ex.getLocalizedMessage();
            }
        }

    }

    private class DetachDBAction extends AbstractAction {

        public DetachDBAction() {
            this.putValue(Action.NAME, "Detach DB");
            this.putValue(Action.SHORT_DESCRIPTION, "Delete all nodes and relationships of the database");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DBManagementPanel ptp = Application.getInstance().getDBManagementPanel();
            try {
                DesktopBackend db = Application.getInstance().getDesktopBackend();
                ptp.setUpdatesLabel("Begin detach DB phase...");
                db.detachDB();
                ptp.setUpdatesLabel("Data successfully deleted!");
            } catch (Exception ex) {
                ptp.setUpdatesLabel("ERROR: Unable to delete data!");
                logger.error("ERROR: " + ex.getLocalizedMessage());
            }
        }
    }

    private class CloseAction extends AbstractAction {

        public CloseAction() {
            this.putValue(Action.NAME, "Close");
            this.putValue(Action.SHORT_DESCRIPTION, "Close this window");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl X"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Application.getInstance().getDBManagementPanel().close();
        }
    }

    //DA CANCELLARE?
    private class AzioneRiepilogoDB extends AbstractAction {

        public AzioneRiepilogoDB() {
            this.putValue(Action.NAME, "Riepilogo DB");
            this.putValue(Action.SHORT_DESCRIPTION, "Controlla lo stato della base di dati");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                DesktopBackend db = Application.getInstance().getDesktopBackend();
                db.controllaDB();
            } catch (Exception ex) {
                logger.debug("ERRORE: " + ex.getLocalizedMessage());
            }
        }
    }
    
    private class ResetAction extends AbstractAction {

        public ResetAction() {
            this.putValue(Action.NAME, "Reset");
            this.putValue(Action.SHORT_DESCRIPTION, "Delete the PDFs you have already inserted");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            List<File> PDFList = (List<File>) Application.getInstance().getModel().getBean(Constant.PDF_LIST);
            DBManagementPanel ptp = Application.getInstance().getDBManagementPanel();
            if(PDFList == null || PDFList.isEmpty()){
                ptp.setDropLabel("You have not inserted any PDF file!");
                return;
            }
            PDFList.clear();
            Application.getInstance().getModel().putBean(Constant.PDF_LIST, PDFList);
            logger.debug("PDF List correctly emptied. (controlPanelTantiPDF)");
            ptp.setDropLabel("You have deleted the uploaded PDFs!");
        }
    }

    //*********** METODI GENERICI **********************
    
    private static void createRelationshipsCYP(DesktopBackend db) {
        try {
            //CREATE RELATIONSHIPS Year <- PDF
            db.createRelationshipCYP();
            logger.info("RELATIONSHIPS Year - PDF created!");
        } catch (Neo4jException ex) {
            logger.error("ERROR: can't execute the transaction to create the Year - PDF RELATIONSHIPS.");
            logger.error(ex.getLocalizedMessage());
        }
    }
    
    private static void createRelationshipsAP(DesktopBackend db) {
        try {
            //CREATE RELATIONSHIPS Author -> PDF
            db.createRelationshipAP();
            logger.info("RELATIONSHIPS Author - PDF created!");
        } catch (Neo4jException ex) {
            logger.error("ERROR: can't execute the transaction to create the Author - PDF RELATIONSHIPS.");
            logger.error(ex.getLocalizedMessage());
        }
    }

    private static void transazioneAnnoDiCreazione(DesktopBackend db, List<File> PDFList) {
        try {
            for (File pdf : PDFList) {
                //YEARS OF CREATION ADDED TO THE MODEL
                List<Integer> creationYearsList = (List<Integer>) Application.getInstance().getModel().getBean(Constant.CREATION_YEARS_LIST);
                if (creationYearsList == null) {
                    creationYearsList = new ArrayList<>();
                }
                Integer creationYear = getPDFCreationYear(pdf);
                if(creationYear != null){
                    if(!(creationYearsList.contains(creationYear))){
                       creationYearsList.add(creationYear);
                    }
                }
                Application.getInstance().getModel().putBean(Constant.CREATION_YEARS_LIST, creationYearsList);
            }
            //CHECK ON LIST OF CREATION YEARS
            List<Integer> creationYearsList = (List<Integer>) Application.getInstance().getModel().getBean(Constant.CREATION_YEARS_LIST);
            if(creationYearsList == null || creationYearsList.isEmpty()){
                logger.debug("creationYearsList is EMPTY or null -> IMPOSSIBLE TO CREATE THE YEAR NODES!");
                return;
            }
            for(Integer annoCreazione : creationYearsList){
                db.createYearNode(annoCreazione);
                logger.info("YEAR nodes created!");
            }
        } catch (Neo4jException ex) {
            logger.error("ERROR: can't execute the transaction to create the YEAR nodes.");
            logger.error(ex.getLocalizedMessage());
        }
    }

    private static void transazionePDF(DesktopBackend db, List<File> PDFList) {
        try {
            for (File pdf : PDFList) {
                String PDFAuthor = findPDFAuthor(pdf);
                Integer creationYear = getPDFCreationYear(pdf);
                String fileName = pdf.getName();
                //CHECK fileName
                if (!(fileName == null)
                        && !(fileName.isEmpty())
                        && !(fileName.isBlank())) {
                    db.createPDFNode(fileName, PDFAuthor, creationYear);
                    logger.info("PDF nodes created! -> " + fileName + ", author: " + PDFAuthor);
                }
            }
        } catch (Neo4jException ex) {
            logger.error("ERROR: can't execute the transaction to create the PDF nodes.");
            logger.error(ex.getLocalizedMessage());
        }
    }

    private static void transazioneAutori(DesktopBackend db) {
        try {
            List<String> authorsList = (List<String>) Application.getInstance().getModel().getBean(Constant.AUTHOR_LIST);
            for (String autore : authorsList) {
                db.createAuthorNode(autore);
                logger.info("AUTHOR nodes created! -> " + autore);
            }
        } catch (Neo4jException ex) {
            logger.error("ERROR: can't execute the transaction to create the AUTHOR nodes.");
            logger.error(ex.getLocalizedMessage());
        }

    }

    private static Integer getPDFCreationYear(File file) {
        try {
            var pdf = PDDocument.load(file);
            var metadata = pdf.getDocumentInformation();
            Calendar creationDate = metadata.getCreationDate();
            if (creationDate != null) {
                Integer creationYear = creationDate.get(Calendar.YEAR);
                return creationYear;
            }
            pdf.close();
        } catch (Exception ex) {
            logger.error("ERROR at getPDFCreationYear: " + ex.getLocalizedMessage());
        }
        return null;
    }

    private static String findPDFAuthor(File file) {
        try {
            var pdf = PDDocument.load(file);
            String author = pdf.getDocumentInformation().getAuthor();
            if (!author.isEmpty() || author == null) {
                return author;
            }
            pdf.close();
        } catch (IOException ex) {
            logger.info("ERROR: I could not extract the author of the PDF file. -> " + ex.getLocalizedMessage());
        }
        return null;
    }

    private static boolean isPDFListEmpty(List<File> PDFList) {
        if (PDFList == null || PDFList.isEmpty()) {
            logger.error("No PDF list found!");
            return true;
        }
        return false;
    }

    private static void extractAuthors(List<File> PDFList) {
        List<String> authorsList = new ArrayList<>();
        try {
            for (File file : PDFList) {
                var pdf = PDDocument.load(file);
                logger.info("Is it encrypted? " + pdf.isEncrypted());
                logger.info("Number of pages = " + pdf.getNumberOfPages());
                String author = findPDFAuthor(file);
                if (!authorsList.contains(author)) {
                    authorsList.add(author);
                    logger.info("Author " + author + " added to the list.");
                }
                pdf.close();
            }
            Application.getInstance().getModel().putBean(Constant.AUTHOR_LIST, authorsList);
        } catch (IOException ex) {
            logger.debug("Problem: " + ex);
        }
    }

    //Copiato da ControlloPannelloPDF
    //Estrazione metadati
    private static void estraiMetadatiPdf(PDDocument pdf) {
        Application.getInstance().getModel().putBean(Constant.PDF_METADATA, null);
        List<Metadata> metadatiPdf = new ArrayList<>();
        PDDocumentInformation metadati = pdf.getDocumentInformation();
        metadatiPdf.add(new Metadata("Title", metadati.getTitle()));
        metadatiPdf.add(new Metadata("Author", metadati.getAuthor()));
        metadatiPdf.add(new Metadata("Subject", metadati.getSubject()));
        metadatiPdf.add(new Metadata("Keywords", metadati.getKeywords()));
        metadatiPdf.add(new Metadata("Creator", metadati.getKeywords()));
        metadatiPdf.add(new Metadata("Producer", metadati.getProducer()));
        metadatiPdf.add(new Metadata("Trapped", metadati.getTrapped()));
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        if (metadati.getCreationDate() == null) {
            metadatiPdf.add(new Metadata("Creation Date", null));
            metadatiPdf.add(new Metadata("Modification Date", null));
        } else {
            metadatiPdf.add(new Metadata("Creation Date", df.format(metadati.getCreationDate().getTime())));
            if (metadati.getModificationDate() == null) {
                metadatiPdf.add(new Metadata("Modification Date", null));
            } else {
                metadatiPdf.add(new Metadata("Modification Date", df.format(metadati.getModificationDate().getTime())));
            }
        }
        //System.out.println("NOME FILE: " + metadati.getTitle()); //Restituisce NULL oppure " " per alcuni file.
        /*
            try {
                List<PDSignature> listaSignature = pdf.getSignatureDictionaries();
                if(listaSignature == null){
                    return;
                }
                String nomeFirmatario;
                Iterator<PDSignature> iteratore = listaSignature.iterator();
                while(iteratore.hasNext()){
                    nomeFirmatario = iteratore.
                }
                for(int i = 0; i < listaSignature.size(); i++){
                    nomeFirmatario = iteratore.
                    metadatiPdf.add(new Metadata("Firmatario n." + i, nomeFirmatario));
                }
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(ControlloPannelloPDF.class.getName()).log(Level.SEVERE, null, ex);
            }
         */
        if (metadatiPdf.isEmpty()) {
            logger.debug("La lista metadati e' vuota!");
        } else {
            logger.debug("La lista NON e' vuota!");
        }
    }
}
