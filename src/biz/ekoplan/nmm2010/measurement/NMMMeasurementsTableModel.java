/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.measurement;

import biz.ekoplan.nmm2010.devices.NMMNoiseMeasurementSet;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;
import nmm2010.NMMProject;

/**
 *
 * @author Jarek
 */
public class NMMMeasurementsTableModel extends AbstractTableModel {

    String[] columnNames = {"Pomiar","X[m]","Y[m]","Operator","Kalib.1","Kalib.2","h[m]","UB","Tło","Lokalizacja","Foto","Tor pomiarowy","Color"};
    boolean[] columnEditable = {true,true,true,true,true,true,true,true,true,true,true,true,true};
    NMMProject nmmProject;
    
    public NMMMeasurementsTableModel(NMMProject _nmmProject) {
        this.nmmProject=_nmmProject;
    }    
    
    @Override
    public int getRowCount() {
        return this.nmmProject.getMeasurementsNumber();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        System.out.println("Ta kolumna jest edytowalna: "+this.columnEditable[column]);
        return true;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public void setValueAt(Object _o, int row, int col) {        
        
        NMMMeasurement ms=this.nmmProject.getMeasurement(row);        
        
        switch (col) {           
            case 0:
                ms.setDescription(_o.toString());                
                break;
            case 1:
                ms.setCoordinateX(Double.parseDouble(_o.toString()));
                break;
            case 2:
                ms.setCoordinateY(Double.parseDouble(_o.toString()));
                break;
            case 3:
                ms.setOperator(_o.toString());                
                break;
            case 4:
                ms.setInitialCalibration(Double.parseDouble(_o.toString()));
                break;
            case 5:
                ms.setFInalCalibration(Double.parseDouble(_o.toString()));
                break;
            case 6:
                ms.setHeight(Double.parseDouble(_o.toString()));
                break;
            case 7:
                ms.setUncertaintyB(Double.parseDouble(_o.toString()));
                break;
            case 8:
                ms.setBackgroundNoiseLevel(Double.parseDouble(_o.toString()));
                break;
            case 9:
                //lokalizacja
                ms.setRemarks((String) _o);                
                break;
            case 10:
                //foto
                //nic na razie nie dajemy
                break;
            case 11:
                //tor pomiarowy
                ms.setMeasurementSet((NMMNoiseMeasurementSet) _o);                
                break;
            case 12:
                ms.setMeasurementColor((Color) _o);
                break;                                
        }
        ms.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(ms));        
        this.fireTableDataChanged();
    }
        
    @Override
    public Object getValueAt(int row, int col) {
        
        Object ret=null;
        NMMMeasurement ms;
        ms=this.nmmProject.getMeasurement(row);
        
        switch (col) {
            case 0:
                ret=ms.getDescription();                
                break;
            case 1:
                ret=ms.getCoordinateX();
                break;
            case 2:
                ret=ms.getCoordinateY();
                break;  
            case 3:
                ret=ms.getOperator();
                break;
            case 4:
                ret=ms.getInitialCalibration();
                break;
            case 5:
                ret=ms.getFinalCalibration();
                break;
            case 6:
                ret=ms.getHeight();
                break;
            case 7:
                ret=ms.getUncertaintyB();
                break;
            case 8:
                ret=ms.getBackgroundNoiseLevel();
                break;
            case 9:
                ret=ms.getRemarks();
                break;
            case 10:
                ret=ms.getPicture();
                break;
            case 11:
                ret=ms.getMeasurementSet();
                break;
            case 12:
                ret=ms.getMeasurementColor();
                break;
        }        
        return ret;
    }

    @Override
    public Class getColumnClass(int col) {
        
        Class ret=null;
        
        switch (col) {
            case 0:
                ret=String.class;
                break;
            case 1:
                ret=Double.class;
                break;
            case 2:
                ret=Double.class;
                break;  
            case 3:
                ret=String.class;
                break;
            case 4:
                ret=Double.class;
                break;
            case 5:
                ret=Double.class;
                break;
            case 6:
                ret=Double.class;
                break;
            case 7:
                ret=Double.class;
                break;
            case 8:
                ret=Double.class;
                break;
            case 9:
                ret=String.class;
                break;
            case 10:
                ret=ImageIcon.class;
                break;
            case 11:
                ret=String.class;
                break;
            case 12:
                ret=Color.class;                
                break;
        }        
        return ret;
    }
    
}
