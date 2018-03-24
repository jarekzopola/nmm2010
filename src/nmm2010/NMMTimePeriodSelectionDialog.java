/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NMMTimePeriodSelectionDialog.java
 *
 * Created on 2011-01-26, 17:27:57
 */

package nmm2010;

import biz.ekoplan.nmm2010.toolbox.TimeConverter;
import java.text.DateFormat;
import java.util.Locale;

/**
 *
 * @author Jarek
 */
public class NMMTimePeriodSelectionDialog extends javax.swing.JDialog {

    NMMProject nmmProj;
    Locale locale;
    
    
    /** Creates new form NMMTimePeriodSelectionDialog */
    public NMMTimePeriodSelectionDialog(java.awt.Frame parent, boolean modal, 
            NMMProject _nmmProj, Locale _locale) {
        super(parent, modal);
        this.nmmProj=_nmmProj;
        this.locale=_locale;
        initComponents();        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        textStart = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        textEnd = new javax.swing.JTextField();
        buttonCancel = new javax.swing.JButton();
        buttonSelect = new javax.swing.JButton();
        buttonMeasStartTime = new javax.swing.JButton();
        buttonMeasEndTime = new javax.swing.JButton();
        textStartDate = new javax.swing.JTextField();
        textEndDate = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Manual time rage selection");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText("Begining");

        textStart.setText("hh:mm:ss");

        jLabel2.setText("End");

        textEnd.setText("hh:mm:ss");

        buttonCancel.setText("Cancel");
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonSelect.setText("Select");
        buttonSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectActionPerformed(evt);
            }
        });

        buttonMeasStartTime.setText("Measurement start");
        buttonMeasStartTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMeasStartTimeActionPerformed(evt);
            }
        });

        buttonMeasEndTime.setText("Measurement end");
        buttonMeasEndTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMeasEndTimeActionPerformed(evt);
            }
        });

        textStartDate.setText("dd:mm:yyyy");

        textEndDate.setText("dd:mm:yyyy");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(buttonSelect)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buttonCancel))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(textEndDate)
                                .addComponent(textStartDate, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(textStart, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(textEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(buttonMeasEndTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buttonMeasStartTime, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))))
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(textStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonMeasStartTime))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(textEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonMeasEndTime))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCancel)
                    .addComponent(buttonSelect))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonMeasStartTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMeasStartTimeActionPerformed
        
        this.textStartDate.setText(TimeConverter.LongToDateString(
                this.nmmProj.getCurrentMeasurement().getMeasurementStartTime(),
                DateFormat.MEDIUM, locale));
        
        this.textStart.setText(TimeConverter.LongToTimeString(
                this.nmmProj.getCurrentMeasurement().getMeasurementStartTime(),
                DateFormat.MEDIUM, this.locale));
    }//GEN-LAST:event_buttonMeasStartTimeActionPerformed

    private void buttonMeasEndTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMeasEndTimeActionPerformed

        this.textEndDate.setText(TimeConverter.LongToDateString(
                this.nmmProj.getCurrentMeasurement().getMeasurementEndTime(),
                DateFormat.MEDIUM, this.locale));
        this.textEnd.setText(TimeConverter.LongToTimeString(
                this.nmmProj.getCurrentMeasurement().getMeasurementEndTime(),
                DateFormat.MEDIUM, this.locale));
    }//GEN-LAST:event_buttonMeasEndTimeActionPerformed

    private void buttonSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectActionPerformed

        long st;        //begining of the selected period
        long end;       //end of the selected period
        long ms_st;     //begining of the current measurement

        st=TimeConverter.StringToLong(this.textStartDate.getText(),
                this.textStart.getText(), this.locale);
        end=TimeConverter.StringToLong(this.textEndDate.getText(),
                this.textEnd.getText(), this.locale);
        this.nmmProj.currentSelection.setStart(st);
        this.nmmProj.currentSelection.setEnd(end);
        this.dispose();
    }//GEN-LAST:event_buttonSelectActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.textStartDate.setText(TimeConverter.LongToDateString(
                this.nmmProj.getCurrentMeasurement().getMeasurementStartTime(),
                DateFormat.MEDIUM,
                this.locale));
        this.textStart.setText(TimeConverter.LongToTimeString(
                this.nmmProj.getCurrentMeasurement().getMeasurementStartTime(),
                DateFormat.MEDIUM,
                this.locale));
        this.textEndDate.setText(TimeConverter.LongToDateString(
                this.nmmProj.getCurrentMeasurement().getMeasurementEndTime(),
                DateFormat.MEDIUM,
                this.locale));
        this.textEnd.setText(TimeConverter.LongToTimeString(
                this.nmmProj.getCurrentMeasurement().getMeasurementEndTime(),
                DateFormat.MEDIUM,
                this.locale));
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonMeasEndTime;
    private javax.swing.JButton buttonMeasStartTime;
    private javax.swing.JButton buttonSelect;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField textEnd;
    private javax.swing.JTextField textEndDate;
    private javax.swing.JTextField textStart;
    private javax.swing.JTextField textStartDate;
    // End of variables declaration//GEN-END:variables

}
