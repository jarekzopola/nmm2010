/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.surcemodel.sminterface;

import biz.ekoplan.nmm2010.enums.NoiseSourceModelType;
import biz.ekoplan.nmm2010.surcemodel.NMMNoiseSourceModel;
import biz.ekoplan.nmm2010.surcemodel.NMMRoadSamplingModel;
import biz.ekoplan.nmm2010.surcemodel.NMMSimpleModel;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import nmm2010.NMMProject;

/**
 *
 * @author jarek
 */
public class NMMSourceModelsChooser extends javax.swing.JDialog {
    
    NMMProject nmmProj;    

    /**
     * Creates new form NMMSourceModelsManager
     */
    public NMMSourceModelsChooser(java.awt.Frame parent, boolean modal, 
            NMMProject _nmmProj, NoiseSourceModelType _nsmt) {
        super(parent, modal);
        this.nmmProj=_nmmProj;
        initComponents();
        
        
        DefaultListModel dlm = new DefaultListModel();
        
        Object[] l= this.nmmProj.getNoiseSourceModels(_nsmt.SIMPLE_EBASED_MEASUREMENT);
        
        for (int i=0; i<l.length; i++) {
            NMMNoiseSourceModel n= (NMMNoiseSourceModel)l[i];
            dlm.addElement(n);            
        }                
        this.listNoiseSourceModels.setModel(dlm);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listNoiseSourceModels = new javax.swing.JList();
        buttonChoose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("NMM 2010 * Noise source models manager.");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jLabel1.setText("Choose model:");

        listNoiseSourceModels.setToolTipText("Noise source models in current project.");
        jScrollPane1.setViewportView(listNoiseSourceModels);

        buttonChoose.setText("O.K.");
        buttonChoose.setToolTipText("Close this dialog.");
        buttonChoose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonChoose)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonChoose)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        
          
        
    }//GEN-LAST:event_formWindowActivated

    public NMMNoiseSourceModel getNoiseSourceModel() {
        return (NMMNoiseSourceModel)this.listNoiseSourceModels.getSelectedValue();
    }
    
    private void buttonChooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_buttonChooseActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonChoose;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList listNoiseSourceModels;
    // End of variables declaration//GEN-END:variables
}
