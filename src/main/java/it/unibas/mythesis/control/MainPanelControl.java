/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.mythesis.control;

import it.unibas.mythesis.Application;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *
 * @author raffa
 */
public class MainPanelControl {
    
    private Action summaryAction = new SummaryAction();
    private Action closeAction = new CloseAction();
    private Action dbManagementAction = new DBManagementAction();
    
    public Action getDBManagementAction(){
        return dbManagementAction;
    }
    
    public Action getSummaryAction(){
        return summaryAction;
    }
    
    public Action getCloseAction(){
        return closeAction;
    }
    
    private class DBManagementAction extends AbstractAction{
        
        public DBManagementAction(){
            this.putValue(Action.NAME, "DB Management");
            this.putValue(Action.SHORT_DESCRIPTION, "Manage the graph database");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            Application.getInstance().getDBManagementPanel().open();
        }
        
    }
    
    private class SummaryAction extends AbstractAction{
        
        public SummaryAction(){
            this.putValue(Action.NAME, "Extract Metadata");
            this.putValue(Action.SHORT_DESCRIPTION, "Show the metadata of a PDF file");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            Application.getInstance().getMetadataPanel().open();
        }
    }
    
    private class CloseAction extends AbstractAction{
        
        public CloseAction(){
            this.putValue(Action.NAME, "Close");
            this.putValue(Action.SHORT_DESCRIPTION, "Close this application");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}
