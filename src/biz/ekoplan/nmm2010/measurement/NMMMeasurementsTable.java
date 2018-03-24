/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.measurement;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.table.TableModel;
import nmm2010.NMMProject;

/**
 *
 * @author jarek
 */
public class NMMMeasurementsTable extends javax.swing.JDialog {

    TableModel tableModel;    
    private Image nmmImg;
    private NMMProject nmmProj;
    
    /**
     * Creates new form NMMMeasurementsTable
     */
    public NMMMeasurementsTable(java.awt.Frame parent, boolean modal, TableModel _tm,
            NMMProject _nmmProj) {
        super(parent, modal);
        this.tableModel=_tm;
        this.nmmProj=_nmmProj;
        
        try {
            nmmImg = ImageIO.read(new File("nmm.png"));
        } catch (IOException e) {
            System.out.println("Błąd podczas czytania pliku ikony nmm.png:  "+e.toString());
        }
        
        initComponents();    
        NMMColorTableCellRenderer cr = new NMMColorTableCellRenderer(true);
        NMMImageTableCellRenderer cri = new NMMImageTableCellRenderer(true);
        NMMColorTableCellEdytor ce = new NMMColorTableCellEdytor();
        this.tableMeasurements.setDefaultRenderer(Color.class, cr);
        this.tableMeasurements.setDefaultEditor(Color.class, ce);
        this.tableMeasurements.setDefaultRenderer(ImageIcon.class, cri);
        this.tableMeasurements.setRowHeight(20);
        

        
    }
            
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tableMeasurements = new javax.swing.JTable();
        buttonClose = new javax.swing.JButton();
        buttonCopyProperty = new javax.swing.JButton();
        buttonLoadCoordinates = new javax.swing.JButton();
        buttonCounter = new javax.swing.JButton();
        buttonColour = new javax.swing.JButton();
        buttonPhotos = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations_pl_PL"); // NOI18N
        setTitle(bundle.getString("MEASUREMENT'S PROPERTIES.")); // NOI18N
        setIconImage(nmmImg);
        setName("MeasurementsTabelDialog"); // NOI18N

        tableMeasurements.setModel(this.tableModel);
        jScrollPane1.setViewportView(tableMeasurements);

        buttonClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Stop16.gif"))); // NOI18N
        buttonClose.setText(bundle.getString("CLOSE")); // NOI18N
        buttonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCloseActionPerformed(evt);
            }
        });

        buttonCopyProperty.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Copy16.gif"))); // NOI18N
        buttonCopyProperty.setText(bundle.getString("COPY PROPERTY")); // NOI18N
        buttonCopyProperty.setToolTipText(bundle.getString("COPY VALUE TO REMAINING CELLS IN CURRENT COLUMN.")); // NOI18N
        buttonCopyProperty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCopyPropertyActionPerformed(evt);
            }
        });

        buttonLoadCoordinates.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Import16.gif"))); // NOI18N
        buttonLoadCoordinates.setText(bundle.getString("IMPORT COORDINATES")); // NOI18N
        buttonLoadCoordinates.setToolTipText(bundle.getString("IMPORT COORDINATES OF MEASUREMENT'S LOCATIONS FROM TEXT FILE ...")); // NOI18N
        buttonLoadCoordinates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLoadCoordinatesActionPerformed(evt);
            }
        });

        buttonCounter.setText(bundle.getString("COUNTER")); // NOI18N
        buttonCounter.setToolTipText(bundle.getString("ADD COUNTER TO ANY TEXT COLUMN.")); // NOI18N
        buttonCounter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCounterActionPerformed(evt);
            }
        });

        buttonColour.setMnemonic('S');
        buttonColour.setText(bundle.getString("COLOR")); // NOI18N
        buttonColour.setToolTipText(bundle.getString("CHOOSE COLOR FOR ALL MEASUREMENTS")); // NOI18N
        buttonColour.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonColourActionPerformed(evt);
            }
        });

        buttonPhotos.setText(bundle.getString("PHOTOS")); // NOI18N
        buttonPhotos.setToolTipText(bundle.getString("PRESS TO ADD PHOTOS TO MEASUREMENTS")); // NOI18N
        buttonPhotos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPhotosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 898, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(buttonCopyProperty)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonCounter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonLoadCoordinates)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonColour)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonPhotos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonClose)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCopyProperty)
                    .addComponent(buttonClose)
                    .addComponent(buttonLoadCoordinates)
                    .addComponent(buttonCounter)
                    .addComponent(buttonColour)
                    .addComponent(buttonPhotos))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCloseActionPerformed
        this.tableModel=null;        
        this.dispose();
    }//GEN-LAST:event_buttonCloseActionPerformed

    private void buttonCopyPropertyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCopyPropertyActionPerformed
        String cs;
        double csd;
        int selCol=this.tableMeasurements.getSelectedColumn();
        System.out.println("Wybrana kolumna tabeli do kopiowania: "+selCol);
        if (selCol==0 || selCol==3 || selCol==9 || selCol==11) {
            //kolumny tekstowe
            cs = (String)this.tableMeasurements.getValueAt(this.tableMeasurements.getSelectedRow(),
                selCol);
            for (int i=0;i<this.tableMeasurements.getRowCount();i++) {
                this.tableMeasurements.setValueAt(cs,i,selCol);
            }
        } else if (selCol==1 || selCol==2 || (selCol>=4 && selCol<=9)) {
            //kolumny liczbowe
            csd = Double.parseDouble(String.valueOf(this.tableMeasurements.getValueAt(this.tableMeasurements.getSelectedRow(),
                selCol)));
            for (int i=0;i<this.tableMeasurements.getRowCount();i++) {
                this.tableMeasurements.setValueAt(String.valueOf(csd),i,selCol);
            }
        } else if (selCol==13) {
            //kolumna wyboru koloru
            Color col = (Color) tableMeasurements.getValueAt(this.tableMeasurements.getSelectedRow(),
                selCol);
            for (int i=0;i<this.tableMeasurements.getRowCount();i++) {
                this.tableMeasurements.setValueAt(col,i,selCol);
            }
        }
                
    }//GEN-LAST:event_buttonCopyPropertyActionPerformed

    private void buttonCounterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCounterActionPerformed
        String cs;        
        int selCol=this.tableMeasurements.getSelectedColumn();
        System.out.println("Wybrana kolumna tabeli do kopiowania: "+selCol);
        if (selCol==0) {
            //kolumna numeracji punktów
            cs = (String)this.tableMeasurements.getValueAt(this.tableMeasurements.getSelectedRow(),
                selCol);
            for (int i=0;i<this.tableMeasurements.getRowCount();i++) {
                this.tableMeasurements.setValueAt(cs+" "+(i+1),i,selCol);
            }
        }
    }//GEN-LAST:event_buttonCounterActionPerformed

    private void buttonLoadCoordinatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLoadCoordinatesActionPerformed
        //import współrzednych punktów pomiarowych
        
    }//GEN-LAST:event_buttonLoadCoordinatesActionPerformed

    private void buttonColourActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonColourActionPerformed
        
        Color newColor = JColorChooser.showDialog(
                     this,
                     java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations_pl_PL").getString("CHOOSE BACKGROUND COLOR"),
                     Color.BLUE);
        int iw = this.tableModel.getRowCount();
        for (int i=0; i<iw; i++) {
            this.tableModel.setValueAt(newColor, i, 12);    
        }        
    }//GEN-LAST:event_buttonColourActionPerformed

    private void buttonPhotosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPhotosActionPerformed
        
        ImageIcon imgIcon;
        
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            File tmpFile;
            
            tmpFile = new File(file.toString());
            //lecimy po pomiarach i przypisujemy fotki, bądź obrakzek
            //"BRAK DOUMENTACJI FOTOGRAFICZNEJ" jeżeli nie ma odpowiednieg
            //obrazka dla danego pomiaru.
            for (int i=1; i<=this.tableModel.getRowCount(); i++) {                                                                  
                file = new File(fc.getSelectedFile().toString()+"\\P"+i+".jpg");
                if (file.exists()) {
                    imgIcon = new ImageIcon(file.toString());
                    this.nmmProj.getMeasurement(i-1).setPicture(imgIcon);                                     
                } else {
                    imgIcon = new ImageIcon(tmpFile.toString()+"\\BDF.png");
                    this.nmmProj.getMeasurement(i-1).setPicture(imgIcon); 
                }
            }                              
        }
    }//GEN-LAST:event_buttonPhotosActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClose;
    private javax.swing.JButton buttonColour;
    private javax.swing.JButton buttonCopyProperty;
    private javax.swing.JButton buttonCounter;
    private javax.swing.JButton buttonLoadCoordinates;
    private javax.swing.JButton buttonPhotos;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableMeasurements;
    // End of variables declaration//GEN-END:variables
}
