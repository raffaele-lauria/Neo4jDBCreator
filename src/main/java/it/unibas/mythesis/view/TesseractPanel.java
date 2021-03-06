/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.mythesis.view;

import it.unibas.mythesis.Application;
import it.unibas.mythesis.Constant;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author raffa
 */
public class TesseractPanel extends javax.swing.JDialog {

    /**
     * Creates new form TesseractPanel
     * @param frameView 
     */
    public TesseractPanel(FrameView frameView) {
        super(frameView);
    }
    
    public void initialize(){
        this.setModal(true);
        initComponents();
        initActions();
    }
    
    public void open() {
        this.setLocationRelativeTo(this.getParent());
        this.setVisible(true);
    }
    
    public void close(){
        this.setVisible(false);
    }
    
    private void initLabels(){
        this.filePathLabel.setText("Drop here your PDF!");
        this.welcomeLabel.setText("*** TESSERACT OCR ***");
        this.filePathLabel.setText("Drop HERE the image!");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        welcomeLabel = new javax.swing.JLabel();
        dropPanel = new JPanel();
        filePathLabel = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();
        tesseractButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        welcomeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        welcomeLabel.setText("Trascina qui sotto il PDF da leggere!");

        filePathLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        filePathLabel.setText("Trascina qui l'immagine!");

        javax.swing.GroupLayout dropPanelLayout = new javax.swing.GroupLayout(dropPanel);
        dropPanel.setLayout(dropPanelLayout);
        dropPanelLayout.setHorizontalGroup(
            dropPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dropPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(filePathLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        dropPanelLayout.setVerticalGroup(
            dropPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dropPanelLayout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(filePathLabel)
                .addContainerGap(91, Short.MAX_VALUE))
        );

        closeButton.setText("Close");

        tesseractButton.setText("Tesseract");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(closeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dropPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(welcomeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(tesseractButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(welcomeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dropPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tesseractButton, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private JPanel dropPanel;
    private javax.swing.JLabel filePathLabel;
    private javax.swing.JButton tesseractButton;
    private javax.swing.JLabel welcomeLabel;
    // End of variables declaration//GEN-END:variables

    private void initActions(){
        this.closeButton.setAction(Application.getInstance().getTesseractPanelControl().getCloseAction());
        this.tesseractButton.setAction(Application.getInstance().getTesseractPanelControl().getTesseractAction());
        
        var mdtl = new MyDropTargetListener(dropPanel);
    }

    public JPanel getDropPanel(){
        return dropPanel;
    }

    public void setLabelPercorsoFile(String text){
        this.filePathLabel.setText(text);
    }
    
    private class MyDropTargetListener extends DropTargetAdapter {

        private final DropTarget dropTarget;
        private final JPanel panel;

        public MyDropTargetListener(JPanel panel) {
            this.panel = panel;

            dropTarget = new DropTarget(panel, DnDConstants.ACTION_COPY, this, true, null);
        }

        @Override
        public void drop(DropTargetDropEvent event) {
            event.acceptDrop(DnDConstants.ACTION_COPY);
            Transferable transferable = event.getTransferable();
            DataFlavor[] flavors = transferable.getTransferDataFlavors();
            for(DataFlavor flavor : flavors){
                if (flavor.isFlavorJavaFileListType()) {
                List<File> files = null;
                try {
                    files = (List<File>) transferable.getTransferData(flavor);
                    for (File file : files) {
                        String percorso = file.getPath();
                        int lunghezza = percorso.length();
                        System.out.println(lunghezza);
                        if (percorso.charAt(lunghezza - 1) != 'f' ||
                            percorso.charAt(lunghezza - 2) != 'd' ||
                            percorso.charAt(lunghezza - 3) != 'p') {
                            Application.getInstance().getFrameView().showErrorMessage("The application only interacts with PDF format files, try again.");
                            return;
                        }
                        Application.getInstance().getModel().putBean(Constant.PATH, file.getPath());
                        System.out.println(Application.getInstance().getModel().getBean(Constant.PATH));
                        Application.getInstance().getTesseractPanel().setLabelPercorsoFile(file.getPath());
                        }
                    } catch (UnsupportedFlavorException ex) {
                        System.out.println("file not supported");
                    } catch (IOException ex) {
                        System.out.println("Loading problems.");
                    }
                }
            }
        }
    }
    
    
}
