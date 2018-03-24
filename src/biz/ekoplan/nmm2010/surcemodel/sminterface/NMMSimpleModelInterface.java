/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.surcemodel.sminterface;

import biz.ekoplan.nmm2010.nmmproject.NMMProjectModelsChanged;
import biz.ekoplan.nmm2010.nmmproject.NMMProjectModelsListener;
import biz.ekoplan.nmm2010.surcemodel.NMMSimpleModel;
import biz.ekoplan.nmm2010.toolbox.NMMToolbox;
import nmm2010.NMMEventType;
import nmm2010.NMMProject;

/**
 *
 * @author jarek
 */
public class NMMSimpleModelInterface extends javax.swing.JDialog 
    implements NMMProjectModelsListener, NMMAbstractNoiseSourceModelInterface {

    
    final boolean DEBUG=true;
    NMMProject nmmProj;
    NMMSimpleModel simpleNoiseSourceModel;
    
    /**
     * Creates new form NMMsimpleNoiseSourceModelInterface
     */
    public NMMSimpleModelInterface(java.awt.Frame _parent, boolean _modal,
           NMMProject _nmmProj, NMMSimpleModel _sm) {
        super(_parent, _modal);        
        this.nmmProj=_nmmProj;
        simpleNoiseSourceModel=_sm;
        initComponents();
        this.checkboxNearFacade.setSelected(_sm.getFacadeCorrection());
        this.checkboxUseL95.setSelected(_sm.getBackgroundNoiseAsL95());         
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ed = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        buttonCancel = new javax.swing.JButton();
        buttonReport = new javax.swing.JButton();
        labelLAeqD = new javax.swing.JLabel();
        checkboxNearFacade = new javax.swing.JCheckBox();
        checkboxUseL95 = new javax.swing.JCheckBox();
        textModelName = new javax.swing.JTextField();
        buttonOK = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        comboEventType = new javax.swing.JComboBox(this.nmmProj.getEventTypes());
        jLabel4 = new javax.swing.JLabel();
        labelMeasurement = new javax.swing.JLabel();

        ed.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations"); // NOI18N
        setTitle(bundle.getString("SIMPLE MEASUREMENT MODEL")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel2.setText(bundle.getString("MODEL")); // NOI18N

        buttonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Stop16.gif"))); // NOI18N
        buttonCancel.setText(bundle.getString("CLOSE")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/History16.gif"))); // NOI18N
        buttonReport.setText(bundle.getString("REPORT")); // NOI18N
        buttonReport.setToolTipText(bundle.getString("SAVE CALCULATION REPORT TO DISC.")); // NOI18N
        buttonReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonReportActionPerformed(evt);
            }
        });

        labelLAeqD.setText(bundle.getString("<HTML>L<SUB>AEQ</SUB>= N.A. ± N.A. DB</HTML>")); // NOI18N

        checkboxNearFacade.setText(bundle.getString("D<2M FROM FACADE")); // NOI18N
        checkboxNearFacade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxNearFacadeActionPerformed(evt);
            }
        });

        checkboxUseL95.setText(bundle.getString("L95 AS BACKGROUND NOISE")); // NOI18N
        checkboxUseL95.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxUseL95ActionPerformed(evt);
            }
        });

        buttonOK.setText(bundle.getString("OK")); // NOI18N
        buttonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOKActionPerformed(evt);
            }
        });

        jLabel3.setText(bundle.getString("EVENT TYPE")); // NOI18N

        comboEventType.setToolTipText(bundle.getString("CHOOSE EVENT TYPE.")); // NOI18N
        comboEventType.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                comboEventTypeComponentShown(evt);
            }
        });
        comboEventType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboEventTypeActionPerformed(evt);
            }
        });

        jLabel4.setText(bundle.getString("MEASUREMENT")); // NOI18N

        labelMeasurement.setText(bundle.getString("N.N.")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(checkboxNearFacade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 46, Short.MAX_VALUE)
                                .addComponent(buttonOK)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonReport)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonCancel))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(checkboxUseL95)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textModelName)
                            .addComponent(comboEventType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelLAeqD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelMeasurement, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(textModelName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(comboEventType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(labelMeasurement))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkboxNearFacade)
                    .addComponent(checkboxUseL95))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(labelLAeqD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCancel)
                    .addComponent(buttonReport)
                    .addComponent(buttonOK))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonReportActionPerformed
        
        System.out.println(this.simpleNoiseSourceModel.getCalculationReport().toString());
    }//GEN-LAST:event_buttonReportActionPerformed
      

    
    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.textModelName.setText(simpleNoiseSourceModel.getModelName());
        this.labelMeasurement.setText(this.nmmProj.getCurrentMeasurement().getDescription());
        this.updateInterface();
    }//GEN-LAST:event_formWindowOpened

    private void buttonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOKActionPerformed
        
        //update noise source model model befor leaving dialog box
        
        simpleNoiseSourceModel.setModelName(this.textModelName.getText());
        simpleNoiseSourceModel.setBackgroundNoiseAsL95(this.checkboxUseL95.isSelected());
        simpleNoiseSourceModel.setFacadeCorrection(this.checkboxNearFacade.isSelected());
        simpleNoiseSourceModel.setEventType((NMMEventType)this.comboEventType.getSelectedItem());
        //now leave dialog box
        this.dispose();
    }//GEN-LAST:event_buttonOKActionPerformed

    private void checkboxNearFacadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxNearFacadeActionPerformed
        simpleNoiseSourceModel.setFacadeCorrection(this.checkboxNearFacade.isSelected());
        this.updateInterface();
    }//GEN-LAST:event_checkboxNearFacadeActionPerformed

    private void checkboxUseL95ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxUseL95ActionPerformed
        simpleNoiseSourceModel.setBackgroundNoiseAsL95(this.checkboxUseL95.isSelected());
        this.updateInterface();
    }//GEN-LAST:event_checkboxUseL95ActionPerformed

    private void comboEventTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboEventTypeActionPerformed
      
        this.simpleNoiseSourceModel.setEventType(
                (NMMEventType)this.comboEventType.getSelectedItem());        
        this.updateInterface();
    }//GEN-LAST:event_comboEventTypeActionPerformed

    private void comboEventTypeComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_comboEventTypeComponentShown

    }//GEN-LAST:event_comboEventTypeComponentShown
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonOK;
    private javax.swing.JButton buttonReport;
    private javax.swing.JCheckBox checkboxNearFacade;
    private javax.swing.JCheckBox checkboxUseL95;
    private javax.swing.JComboBox comboEventType;
    private javax.swing.JComboBox ed;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel labelLAeqD;
    private javax.swing.JLabel labelMeasurement;
    private javax.swing.JTextField textModelName;
    // End of variables declaration//GEN-END:variables

    public void dispatchNMMProjectModelsChanged(NMMProjectModelsChanged _mEvent) {
        if (this.DEBUG) {
            System.out.println("Noise source models in current project have changed."
                    + "I have to update current dialog!");
        }        
    }        

    public void updateInterface() {

        if (this.simpleNoiseSourceModel.isRecalculated()) {
            this.labelLAeqD.setText(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("<HTML>L<SUB>AEQ</SUB>= ")+
                    NMMToolbox.formatDouble(this.simpleNoiseSourceModel.getLAeq()) +" ± "+
                    NMMToolbox.formatDouble(this.simpleNoiseSourceModel.getUncertainty())+
                     " dB</HTML>");
        } else {
            this.labelLAeqD.setText(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("<HTML>L<SUB>AEQ</SUB>= N.A. ± N.A. DB</HTML>"));
        }
    }

    @Override
    /**
     * Returns true id dialog window is filled up correctly
     */
    public boolean isComplete() {
        
        boolean isComplete=true;
        if (this.textModelName.getText().length()<1) {
            isComplete=false;
        }
        if (this.comboEventType.getSelectedIndex()<0) {
            isComplete=false;
        }
        return isComplete;
    }
}