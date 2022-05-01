/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.mythesis.control;

import it.unibas.mythesis.Application;
import it.unibas.mythesis.Constant;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;

/**
 *
 * @author raffa
 */
public class TesseractPanelControl {

    private Action closeAction = new CloseAction();
    private Action tesseractAction = new TesseractAction();

    public Action getCloseAction() {
        return closeAction;
    }

    public Action getTesseractAction() {
        return tesseractAction;
    }

    private static class CloseAction extends AbstractAction {

        public CloseAction() {
            this.putValue(Action.NAME, "Close");
            this.putValue(Action.SHORT_DESCRIPTION, "Close this window.");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl X"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Application.getInstance().getTesseractPanel().close();
        }
    }

    private static class TesseractAction extends AbstractAction {
        
        private static int totPages;
        private static String destinationDir;

        public TesseractAction() {
            this.putValue(Action.NAME, "Tesseract");
            this.putValue(Action.SHORT_DESCRIPTION, "Use Tesseract OCR on this image");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String percorsoPDF = (String) Application.getInstance().getModel().getBean(Constant.PATH);
            if(percorsoPDF.isEmpty() || percorsoPDF == null){
                System.out.println("No path found!");
                return;
            }
            //Generate images corresponding to the pages of the PDF
            System.out.println("Image generation in progress ...");
            generaImmagini(percorsoPDF);
            System.out.println("Image generation complete!");
            System.out.println("Launch Tesseract OCR.");
            //Using Tesseract OCR
            Tesseract tesseract = new Tesseract();
            try {
                if(destinationDir == null || destinationDir.isEmpty()){
                    System.out.println("No destination folder indicated!");
                    return;
                }
                //tesseract.setDatapath((String) Application.getInstance().getModello().getBean(Constant.CARTELLA_IMMAGINI));
                tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
                //tesseract.setDatapath("C:\\Users\\raffa\\Desktop\\Converted_PdfFiles_to_Image");
                System.out.println("Datapath fissato.");
                if(totPages == 0){
                    System.out.println("Numero pagine = 0");
                    return;
                }
                //Fonte: https://medium.com/@rahulvaish/simple-tesseract-ocr-java-be261e343c5b
                for(int i = 0; i < totPages; i++){
                    String fileName = "Pagina_" + (i + 1);
                    System.out.println("OCR sul file PNG.");
                    String text = tesseract.doOCR(new File((i + 1) + ".png"));
                    //System.out.print(text);
                    System.out.println("Creo il file TXT.");
                    File output = new File("C:\\Users\\raffa\\Desktop\\Converted_PdfFiles_to_Image\\Text\\" + fileName + ".txt");
                    salvaTesto(output, text);
                }
            } catch (TesseractException ex) {		
		ex.printStackTrace();
            }
            
        }
        
        private void salvaTesto(File output, String text){
            try {
                PrintWriter writer = new PrintWriter(output);
                writer.println(text);
                writer.close();
            } catch (Exception ex) {
                System.err.println("ERRORE: " + ex.getLocalizedMessage());
            }
        }
        
        private void generaImmagini(String percorsoImmagine){
            //Fonte: https://stackoverflow.com/questions/18189314/convert-a-pdf-file-to-image
            try {
                String immaginePath = percorsoImmagine;
                destinationDir = "C:\\Users\\raffa\\Desktop\\Converted_PdfFiles_to_Image/"; // converted images from pdf document are saved here
                //Application.getInstance().getModello().putBean(Constant.CARTELLA_IMMAGINI, destinationDir);
                //System.out.println(Constant.CARTELLA_IMMAGINI);
                System.out.println(destinationDir);
                
                File sourceFile = new File(immaginePath);
                File destinationFile = new File(destinationDir);
                if (!destinationFile.exists()) {
                    destinationFile.mkdir();
                    System.out.println("Folder Created -> " + destinationFile.getAbsolutePath());
                }
                if (sourceFile.exists()) {
                    System.out.println("Images copied to Folder Location: " + destinationFile.getAbsolutePath());
                    PDDocument document = PDDocument.load(sourceFile);
                    PDFRenderer pdfRenderer = new PDFRenderer(document);
                    
                    int numberOfPages = document.getNumberOfPages();
                    System.out.println("Total files to be converting -> " + numberOfPages);

                    String fileName = sourceFile.getName().replace(".pdf", "");
                    String fileExtension = "png";
                    /*
                         * 600 dpi give good image clarity but size of each image is 2x times of 300 dpi.
                         * Ex:  1. For 300dpi 04-Request-Headers_2.png expected size is 797 KB
                         *      2. For 600dpi 04-Request-Headers_2.png expected size is 2.42 MB
                     */
                    int dpi = 300;// use less dpi for to save more space in harddisk. For professional usage you can use more than 300dpi 

                    totPages = numberOfPages;
                    for (int i = 0; i < numberOfPages; ++i) {
                        //File outPutFile = new File(destinationDir + fileName + "_" + (i + 1) + "." + fileExtension);
                        File outPutFile = new File("Furanco" + (i + 1) + "." + fileExtension);
                        if(outPutFile == null){
                            System.err.println("ERRORE: outPutFile d' null!");
                        }
                        BufferedImage bImage = pdfRenderer.renderImageWithDPI(i, 300, ImageType.RGB);
                        if(bImage == null){
                            System.err.println("ERRORE: bufferedImage vuota!");
                        }
                        //Utilizzo ImageIOUtil perché sto usando la versione 2.0 di pdfbox
                        ImageIOUtil.writeImage(bImage, (i + 1) + ".png", 300);
                        //ImageIO.write(bImage, fileExtension, outPutFile);
                        if(!ImageIO.write(bImage, fileExtension, outPutFile)){
                            System.err.println("ERRORE: Non sono riuscito a creare le immagini!");
                        }
                        //Application.getInstance().getModello().putBean(Constant.PERCORSO_IMMAGINE, outPutFile.getPath());
                        //System.out.println(Application.getInstance().getModello().getBean(Constant.PERCORSO_IMMAGINE));
                        //Application.getInstance().getPannelloTesseract().setLabelPercorsoFile(outPutFile.getPath());
                    }
                    
                    document.close();
                    System.out.println("Converted Images are saved at -> " + destinationFile.getAbsolutePath());
                } else {
                    System.err.println(sourceFile.getName() + " File not exists");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
