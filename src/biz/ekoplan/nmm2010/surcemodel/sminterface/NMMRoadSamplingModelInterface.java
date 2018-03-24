/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.surcemodel.sminterface;

import biz.ekoplan.nmm2010.enums.NMMMonths;
import biz.ekoplan.nmm2010.enums.NMMRoadTrafficTypes;
import biz.ekoplan.nmm2010.nmmproject.NMMProjectModelsChanged;
import biz.ekoplan.nmm2010.nmmproject.NMMProjectModelsListener;
import biz.ekoplan.nmm2010.surcemodel.NMMRoadSamplingModel;
import biz.ekoplan.nmm2010.toolbox.NMMToolbox;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.DefaultCellEditor;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import nmm2010.NMMEventType;
import nmm2010.NMMProject;
import nmm2010.Setup;

/**
 *
 * @author jarek
 */
public class NMMRoadSamplingModelInterface extends javax.swing.JDialog 
    implements NMMProjectModelsListener, TableModelListener, NMMAbstractNoiseSourceModelInterface {

    
    final boolean DEBUG=true;
    NMMProject nmmProj;
    NMMRoadSamplingModel roadSamplingModel;
    Setup setup;
    
    /**
     * Creates new form NMMRoadSamplingModelInterface
     */
    public NMMRoadSamplingModelInterface(java.awt.Frame _parent, boolean _modal,
           NMMProject _nmmProj, NMMRoadSamplingModel _rsm, Setup _setup) {
        super(_parent, _modal);
        initComponents();
        this.nmmProj=_nmmProj;        
        this.setup=_setup;
        roadSamplingModel=_rsm;                              
        TableModel m=_rsm;
        this.checkboxNearFacade.setSelected(_rsm.getFacadeCorrection());
        this.checkboxUseL95.setSelected(_rsm.getBackgroundNoiseAsL95());
        if (!(m==null)) {            
            this.tableRoadNoiseData.setModel(m);
            this.tableRoadNoiseData.getModel().addTableModelListener(this);                   
            setUpEventTypesColumn();          
        }
                        
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
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableRoadNoiseData = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        buttonCancel = new javax.swing.JButton();
        buttonReport = new javax.swing.JButton();
        textModelName = new javax.swing.JTextField();
        buttonOK = new javax.swing.JButton();
        labelCurrentMeasurement = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        panelMeasurementCircumstances = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        checkboxNearFacade = new javax.swing.JCheckBox();
        checkboxUseL95 = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        comboTrafficType = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        comboMeasurementMonth = new javax.swing.JComboBox();
        panel24HNoiseLevels = new javax.swing.JPanel();
        labelLAeqD = new javax.swing.JLabel();
        labelLAeqN = new javax.swing.JLabel();
        panelAnnualNoiseLevels = new javax.swing.JPanel();
        labelLD = new javax.swing.JLabel();
        labelLW = new javax.swing.JLabel();
        labelLN = new javax.swing.JLabel();
        labelLDWN = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        comboBackgroundNoiseLevelEvent = new javax.swing.JComboBox();

        ed.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Road sampling model definition.");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText("Measurement:");

        tableRoadNoiseData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1"
            }
        ));
        tableRoadNoiseData.setColumnSelectionAllowed(true);
        tableRoadNoiseData.setFillsViewportHeight(true);
        jScrollPane1.setViewportView(tableRoadNoiseData);
        tableRoadNoiseData.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jLabel2.setText("Model name:");

        buttonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Stop16.gif"))); // NOI18N
        buttonCancel.setText("Close");
        buttonCancel.setToolTipText("Abandon model and close dialog");
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/History16.gif"))); // NOI18N
        buttonReport.setText("Report");
        buttonReport.setToolTipText("Save calculation report to disc.");
        buttonReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonReportActionPerformed(evt);
            }
        });

        textModelName.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        buttonOK.setText("OK");
        buttonOK.setToolTipText("Create noise source model and close dialog.");
        buttonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOKActionPerformed(evt);
            }
        });

        labelCurrentMeasurement.setText("-");

        jPanel1.setMaximumSize(new java.awt.Dimension(520, 98301));
        jPanel1.setMinimumSize(new java.awt.Dimension(460, 116));
        jPanel1.setPreferredSize(new java.awt.Dimension(460, 100));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        panelMeasurementCircumstances.setMaximumSize(new java.awt.Dimension(520, 32767));
        panelMeasurementCircumstances.setMinimumSize(new java.awt.Dimension(520, 60));
        panelMeasurementCircumstances.setPreferredSize(new java.awt.Dimension(520, 60));
        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.LEFT);
        flowLayout1.setAlignOnBaseline(true);
        panelMeasurementCircumstances.setLayout(flowLayout1);

        jLabel3.setText("Measurement circumstances:");
        panelMeasurementCircumstances.add(jLabel3);

        checkboxNearFacade.setText("d<2m from facade");
        checkboxNearFacade.setToolTipText("Check this if microphone located closer then 2m from reflecting surface");
        checkboxNearFacade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxNearFacadeActionPerformed(evt);
            }
        });
        panelMeasurementCircumstances.add(checkboxNearFacade);

        checkboxUseL95.setText("L95 as background noise");
        checkboxUseL95.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxUseL95ActionPerformed(evt);
            }
        });
        panelMeasurementCircumstances.add(checkboxUseL95);

        jLabel4.setText("Traffic type:");
        panelMeasurementCircumstances.add(jLabel4);

        comboTrafficType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Local", "Tourist", "TouristSummer", "Recreational" }));
        comboTrafficType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboTrafficTypeActionPerformed(evt);
            }
        });
        panelMeasurementCircumstances.add(comboTrafficType);

        jLabel5.setText("Measurement month :");
        panelMeasurementCircumstances.add(jLabel5);

        comboMeasurementMonth.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        comboMeasurementMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboMeasurementMonthActionPerformed(evt);
            }
        });
        panelMeasurementCircumstances.add(comboMeasurementMonth);

        jPanel1.add(panelMeasurementCircumstances);

        panel24HNoiseLevels.setToolTipText("Based on recorded data only.");
        panel24HNoiseLevels.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        labelLAeqD.setText("<HTML>L<sub>AeqD</sub>= n.a. ± n.a. dB</HTML>");
        panel24HNoiseLevels.add(labelLAeqD);

        labelLAeqN.setText("<HTML>L<sub>AeqN</sub>= n.a. ± n.a. dB</HTML>");
        panel24HNoiseLevels.add(labelLAeqN);

        jPanel1.add(panel24HNoiseLevels);

        panelAnnualNoiseLevels.setToolTipText("Based on recorded data and corrected according to specified circumstances (traffic type and month)");
        panelAnnualNoiseLevels.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        labelLD.setText("<HTML>L<sub>D</sub>= n.a. ± n.a. dB</HTML>");
        panelAnnualNoiseLevels.add(labelLD);

        labelLW.setText("<HTML>L<sub>W</sub>= n.a. ± n.a. dB</HTML>");
        panelAnnualNoiseLevels.add(labelLW);

        labelLN.setText("<HTML>L<sub>DWN</sub>= n.a. ± n.a. dB</HTML>");
        panelAnnualNoiseLevels.add(labelLN);

        labelLDWN.setText("<HTML>L<sub>N</sub>= n.a. ± n.a. dB</HTML>");
        panelAnnualNoiseLevels.add(labelLDWN);

        jPanel1.add(panelAnnualNoiseLevels);

        jLabel6.setText("Background noise level:");

        comboBackgroundNoiseLevelEvent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBackgroundNoiseLevelEventActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(textModelName))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonOK)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonReport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelCurrentMeasurement, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(comboBackgroundNoiseLevelEvent, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                    .addComponent(jLabel1)
                    .addComponent(labelCurrentMeasurement))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(comboBackgroundNoiseLevelEvent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCancel)
                    .addComponent(buttonReport)
                    .addComponent(buttonOK))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Writes calculation report into text file. This can be used to export
     * calculation into measurement report.
     * @param evt 
     */
    private void buttonReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonReportActionPerformed
        
        File file=null;
        
        JFileChooser fc = new JFileChooser(this.setup.getProperty("NMM_SETUP_PROJECT_PATH", null));
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "HTML Documents", "html");
        fc.setFileFilter(filter);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();         
        }                    
        try {
            FileWriter fr = new FileWriter(file);
            BufferedWriter br = new BufferedWriter(fr);    
            
            br.write(this.roadSamplingModel.getCalculationReport().toString());
            br.flush();
            br.close();
            JOptionPane.showMessageDialog(null, "Writing report successfull !!!", "Error", JOptionPane.INFORMATION_MESSAGE);
         } catch(IOException ex) {
            JOptionPane.showMessageDialog(null, "Writing report unsuccessfull !!!", "Error", JOptionPane.WARNING_MESSAGE);
         }
    }//GEN-LAST:event_buttonReportActionPerformed
      
    private void setUpEventTypesColumn() {
        
        ed.removeAllItems();
        for (int i=0; i<this.nmmProj.getEventTypes().length;i++) {
            ed.addItem(this.nmmProj.getEventTypes()[i]);
        }
        this.tableRoadNoiseData.getColumn("Event type").setCellEditor(new DefaultCellEditor(ed));
    }
    
    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        this.tableRoadNoiseData.getModel().removeTableModelListener(this);
        this.dispose();
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.textModelName.setText(roadSamplingModel.getModelName());
        this.comboMeasurementMonth.setSelectedIndex(this.roadSamplingModel.getMeasurementMonth().ordinal());
        this.comboTrafficType.setSelectedIndex(this.roadSamplingModel.getTrafficType().ordinal());
        this.labelCurrentMeasurement.setText(this.nmmProj.getMeasurement(roadSamplingModel.getMeasurementUUID()).getDescription());
        
        //dodajemy wszystkie typy zdarzeń do listy wyboru zdarzenia reprezentujacego tło akustyczne
        NMMEventType[] ets = this.nmmProj.getEventTypeTypes();
        for (int i=0;i<ets.length;i++) {
            this.comboBackgroundNoiseLevelEvent.addItem(ets[i]);
        }        
        this.roadSamplingModel.fireTableDataChanged();
    }//GEN-LAST:event_formWindowOpened

    private void buttonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOKActionPerformed
        NMMRoadSamplingModel rsm=(NMMRoadSamplingModel)this.tableRoadNoiseData.getModel();
        rsm.setModelName(this.textModelName.getText());
        this.dispose();
    }//GEN-LAST:event_buttonOKActionPerformed

    private void checkboxNearFacadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxNearFacadeActionPerformed
        NMMRoadSamplingModel rsm = (NMMRoadSamplingModel) this.tableRoadNoiseData.getModel();
        rsm.setFacadeCorrection(this.checkboxNearFacade.isSelected());        
    }//GEN-LAST:event_checkboxNearFacadeActionPerformed

    private void checkboxUseL95ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxUseL95ActionPerformed
        NMMRoadSamplingModel rsm = (NMMRoadSamplingModel) this.tableRoadNoiseData.getModel();
        rsm.setBackgroundNoiseAsL95(this.checkboxUseL95.isSelected());
        this.comboBackgroundNoiseLevelEvent.setEnabled(!this.checkboxUseL95.isSelected());
    }//GEN-LAST:event_checkboxUseL95ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        this.tableRoadNoiseData.getModel().removeTableModelListener(this);
    }//GEN-LAST:event_formWindowClosed

    private void comboTrafficTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboTrafficTypeActionPerformed
        NMMRoadSamplingModel rsm = (NMMRoadSamplingModel) this.tableRoadNoiseData.getModel();
        rsm.setTrafficType(NMMRoadTrafficTypes.valueOf(this.comboTrafficType.getSelectedItem().toString()));
    }//GEN-LAST:event_comboTrafficTypeActionPerformed

    private void comboMeasurementMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboMeasurementMonthActionPerformed
        NMMRoadSamplingModel rsm = (NMMRoadSamplingModel) this.tableRoadNoiseData.getModel();
        rsm.setMeasurementMonth(NMMMonths.valueOf(this.comboMeasurementMonth.getSelectedItem().toString()));
    }//GEN-LAST:event_comboMeasurementMonthActionPerformed

    private void comboBackgroundNoiseLevelEventActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBackgroundNoiseLevelEventActionPerformed
        this.roadSamplingModel.setBackgroundNoiseEvent((NMMEventType)this.comboBackgroundNoiseLevelEvent.getSelectedItem());
    }//GEN-LAST:event_comboBackgroundNoiseLevelEventActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonOK;
    private javax.swing.JButton buttonReport;
    private javax.swing.JCheckBox checkboxNearFacade;
    private javax.swing.JCheckBox checkboxUseL95;
    private javax.swing.JComboBox comboBackgroundNoiseLevelEvent;
    private javax.swing.JComboBox comboMeasurementMonth;
    private javax.swing.JComboBox comboTrafficType;
    private javax.swing.JComboBox ed;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelCurrentMeasurement;
    private javax.swing.JLabel labelLAeqD;
    private javax.swing.JLabel labelLAeqN;
    private javax.swing.JLabel labelLD;
    private javax.swing.JLabel labelLDWN;
    private javax.swing.JLabel labelLN;
    private javax.swing.JLabel labelLW;
    private javax.swing.JPanel panel24HNoiseLevels;
    private javax.swing.JPanel panelAnnualNoiseLevels;
    private javax.swing.JPanel panelMeasurementCircumstances;
    private javax.swing.JTable tableRoadNoiseData;
    private javax.swing.JTextField textModelName;
    // End of variables declaration//GEN-END:variables

    public void dispatchNMMProjectModelsChanged(NMMProjectModelsChanged _mEvent) {
        if (this.DEBUG) {
            System.out.println("Noise source models in current project have changed."
                    + "I have to update current dialog!");
        }        
    }        

    public void tableChanged(TableModelEvent e) {
    
        NMMRoadSamplingModel rtm = (NMMRoadSamplingModel)e.getSource();
        if (rtm.isRecalculated()) {
            this.labelLAeqD.setText("<HTML>L<sub>AeqD</sub>= "+ 
                    NMMToolbox.formatDouble(rtm.getLAeqD()) +" ± "+
                    NMMToolbox.formatDouble(rtm.getURAB95d())+ " dB</HTML>");
            this.labelLAeqN.setText("<HTML>L<sub>AeqN</sub>= "+
                    NMMToolbox.formatDouble(rtm.getLAeqN()) +" ± "+
                    NMMToolbox.formatDouble(rtm.getURAB95n())+ " dB</HTML>");
            this.labelLD.setText("<HTML>L<sub>D</sub>= "+
                    NMMToolbox.formatDouble(rtm.getLD()) +" ± "+
                    NMMToolbox.formatDouble(rtm.getURAB95_dwn_d())+ " dB</HTML>");
            this.labelLW.setText("<HTML>L<sub>W</sub>= "+
                    NMMToolbox.formatDouble(rtm.getLW()) +" ± "+
                    NMMToolbox.formatDouble(rtm.getURAB95_dwn_w())+ " dB</HTML>");
            this.labelLN.setText("<HTML>L<sub>N</sub>= "+
                    NMMToolbox.formatDouble(rtm.getLN()) +" ± "+
                    NMMToolbox.formatDouble(rtm.getURAB95_dwn_n())+ " dB</HTML>");
            this.labelLDWN.setText("<HTML>L<sub>DWN</sub>= "+
                    NMMToolbox.formatDouble(rtm.getLDWN()) +" ± "+
                    NMMToolbox.formatDouble(rtm.getURAB95_dwn())+ " dB</HTML>");
        } else {
            this.labelLAeqD.setText("<HTML>L<sub>AeqD</sub>= n.a. ± n.a.</HTML>");
            this.labelLAeqN.setText("<HTML>L<sub>AeqN</sub>= n.a. ± n.a.</HTML>");
            this.labelLD.setText("<HTML>L<sub>D</sub>= n.a. ± n.a.</HTML>");
            this.labelLW.setText("<HTML>L<sub>W</sub>= n.a. ± n.a.</HTML>");
            this.labelLN.setText("<HTML>L<sub>N</sub>= n.a. ± n.a.</HTML>");
            this.labelLDWN.setText("<HTML>L<sub>DWN</sub>= n.a. ± n.a.</HTML>");
        }
    }

    @Override
    public boolean isComplete() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}