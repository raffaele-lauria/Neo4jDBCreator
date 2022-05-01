/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.mythesis.control;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 *
 * @author raffa
 */
public class MenuControl {
    
    private Action exitAction = new ExitAction();

    public Action getExitAction() {
        return exitAction;
    }
    
    private class ExitAction extends AbstractAction{
        
        public ExitAction(){
            this.putValue(Action.NAME, "Exit");
            this.putValue(Action.SHORT_DESCRIPTION, "Close the application");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl X"));
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
    
}
