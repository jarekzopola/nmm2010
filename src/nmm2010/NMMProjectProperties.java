/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NMMProjectProperties.java
 *
 * Created on 2011-01-10, 15:51:21
 */

package nmm2010;

import biz.ekoplan.nmm2010.toolbox.NMMNumberInputVerifier;
import java.util.Locale;

/**
 *
 * @author Jarek
 */
public class NMMProjectProperties extends javax.swing.JDialog {

    NMMProject nmmProject;
    Locale locale;
    NMMNumberInputVerifier niv;


    /** Creates new form NMMProjectProperties */
    public NMMProjectProperties(java.awt.Frame parent, boolean modal, NMMProject _nmmProject, Locale _locale) {
        super(parent, modal);
        locale=_locale;
        niv = new NMMNumberInputVerifier();
        initComponents();
        this.nmmProject=_nmmProject;
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
        textProjectTitle = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        textAuthor = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        textProjectSubtitle = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        buttonCancel = new javax.swing.JButton();
        buttonOK = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        textareaRemarks = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        textReporNumber = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        textCommissionNumber = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        labelJREVersion = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        textTimeResolution = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale); // NOI18N
        setTitle(bundle.getString("PROJECT PROPERTIES")); // NOI18N
        setMinimumSize(new java.awt.Dimension(341, 400));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText(bundle.getString("PROJECT TITLE")); // NOI18N

        jLabel2.setText(bundle.getString("AUTHOR")); // NOI18N

        jLabel3.setText(bundle.getString("PROJECT SUBTITLE")); // NOI18N

        jLabel4.setText(bundle.getString("REMARKS")); // NOI18N

        buttonCancel.setText(bundle.getString("CANCEL")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonOK.setText(bundle.getString("OK")); // NOI18N
        buttonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOKActionPerformed(evt);
            }
        });

        textareaRemarks.setColumns(20);
        textareaRemarks.setRows(5);
        textareaRemarks.setText("Opracowano w ramach zlecenia nr ..... z dnia .....");
        jScrollPane1.setViewportView(textareaRemarks);

        jLabel5.setText(bundle.getString("REPORT NUMBER")); // NOI18N

        textReporNumber.setText(bundle.getString("NN/MM/YYYY")); // NOI18N

        jLabel6.setText(bundle.getString("COMMISION NUMBER")); // NOI18N

        textCommissionNumber.setText(bundle.getString("ECP/LA/NN/YYYY")); // NOI18N

        jLabel7.setText(bundle.getString("JRE V.")); // NOI18N

        labelJREVersion.setText("-");

        jLabel8.setText(bundle.getString("(E.G. COMMISSION NUMBER)")); // NOI18N

        jLabel9.setText("Time resolution");

        textTimeResolution.setText("-");
        textTimeResolution.setInputVerifier(niv);

        jLabel10.setText("ms");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelJREVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonOK)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonCancel))
                    .addComponent(jScrollPane1)
                    .addComponent(textAuthor, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(textProjectTitle)
                    .addComponent(textProjectSubtitle)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(textTimeResolution, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textReporNumber, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(textCommissionNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel10))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(textProjectTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(textProjectSubtitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textReporNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(textCommissionNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(textTimeResolution, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonCancel)
                            .addComponent(buttonOK))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(labelJREVersion))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        
    }//GEN-LAST:event_formWindowActivated

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOKActionPerformed

        //remember data in a from in NMMProject object
        this.nmmProject.setProjectTitle(this.textProjectTitle.getText());
        this.nmmProject.setSubtitle(this.textProjectSubtitle.getText());
        this.nmmProject.setProjectAuthor(this.textAuthor.getText());
        this.nmmProject.setRemarks(this.textareaRemarks.getText());
        this.nmmProject.setReportNumber(this.textReporNumber.getText());
        this.nmmProject.setPrjectCommisionNumber(this.textCommissionNumber.getText());
        this.nmmProject.setProjectTimeResolution(Long.parseLong(this.textTimeResolution.getText()));
        this.nmmProject.setSaved(false);
        
        //having all remembered, dispose dialog and quit
        this.dispose();
    }//GEN-LAST:event_buttonOKActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.textCommissionNumber.setText(this.nmmProject.getProjectCommisionNumber());
        this.textReporNumber.setText(this.nmmProject.getReportNumber());
        this.textProjectTitle.setText(this.nmmProject.getProjectTitle());
        this.textAuthor.setText(this.nmmProject.getProjectAuthor());
        this.textProjectSubtitle.setText(this.nmmProject.getSubtitle());
        this.textareaRemarks.setText(this.nmmProject.getProjectRemarks());
        this.labelJREVersion.setText(this.nmmProject.getJVMV());
        this.textTimeResolution.setText(String.valueOf(this.nmmProject.getProjectTimeResolution()));
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonOK;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelJREVersion;
    private javax.swing.JTextField textAuthor;
    private javax.swing.JTextField textCommissionNumber;
    private javax.swing.JTextField textProjectSubtitle;
    private javax.swing.JTextField textProjectTitle;
    private javax.swing.JTextField textReporNumber;
    private javax.swing.JTextField textTimeResolution;
    private javax.swing.JTextArea textareaRemarks;
    // End of variables declaration//GEN-END:variables

}
