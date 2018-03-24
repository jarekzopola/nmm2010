/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.surcemodel;

import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import nmm2010.NMMEventType;
import nmm2010.NMMProject;

/**
 *
 * @author jarek
 */
public class NMMPointLocationTableModel extends AbstractTableModel implements ListSelectionListener {

    //dane wyświetlane w JTable
    NMMMeasurementPointData[] etrs;
    
    //numer aktualnego wiersza
    int selectedRow;
    
    String[] columnNames = {"Point ID","x[m]","y[m]","z[m]","L (source)","L (background)"};
    private transient ArrayList<NMMNoiseSourceModelInputDataChangedListener> NMMNoiseSourceModelInputDataChangedListeners=new ArrayList<>();

    public NMMMeasurementPointData[] getModelDataSource() {
        return this.etrs;
    }
    
    public NMMPointLocationTableModel(NMMProject _nmmProj) {
       etrs = new NMMMeasurementPointData[0];
    }
    
    public void setNumberOfMeasurementPoints(int _numberOfMeasPoints) {
        etrs = new NMMMeasurementPointData[_numberOfMeasPoints];
        for (int i=0; i<_numberOfMeasPoints; i++) {
            etrs[i]=new NMMMeasurementPointData(String.valueOf(i));
            etrs[i].setLbg(null);
            etrs[i].setLs(null);
        }
    }

    public void addInputDataListener(NMMNoiseSourceModelInputDataChangedListener idl) {
        this.NMMNoiseSourceModelInputDataChangedListeners.add(idl);
    }
    
    public void removeInputDataListener(NMMNoiseSourceModelInputDataChangedListener idl) {
        this.NMMNoiseSourceModelInputDataChangedListeners.remove(idl);
    }    
    
    @Override
    public int getRowCount() {
        return etrs.length;
    }

    @Override
    public int getColumnCount() {
        return this.columnNames.length;
    }
    
    @Override
    public String getColumnName(int col) {
        return this.columnNames[col];
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        if (col>3) {
            return true;
        } else {
            return false;
        }
    }
        
    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case 0: 
                return String.class;               
            case 1:
                return Double.class;
            case 2:
                return Double.class;
            case 3:
                return Double.class;
            case 4:
                return NMMEventType.class;
            case 5:
                return NMMEventType.class;                               
            default:                         
                return String.class;
        }
    }
    
    @Override
    public void setValueAt(Object _value, int row, int col) {        
        
    switch (col) {
        case 0: 
            this.etrs[row].setPointID(_value.toString());
            break;
        case 1:
            this.etrs[row].setCoordX((double)_value);
            break;                
        case 2:
            this.etrs[row].setCoordY((double)_value);
            break;
        case 3:
            this.etrs[row].setCoordZ((double)_value);
            break;    
        case 4: 
            this.etrs[row].setLbg((NMMEventType)_value);
            System.out.println("Nastąpisła zmiana modelu !!!!!!!");            
            break;
        case 5: 
            this.etrs[row].setLs((NMMEventType)_value);    
            System.out.println("Nastąpisła zmiana modelu !!!!!!!");            
            break;
    }    
    fireTableCellUpdated(row,col);
    fireNoiseSourceModelInputDataChanged(this);
    }

    private void fireNoiseSourceModelInputDataChanged(NMMPointLocationTableModel _mEvent) {
        Object[] listeners = NMMNoiseSourceModelInputDataChangedListeners.toArray();        
        int numListeners = listeners.length;
        for (int i = 0; i<numListeners; i+=2) {
          if (listeners[i] instanceof NMMNoiseSourceModelInputDataChangedListener) {
               ((biz.ekoplan.nmm2010.surcemodel.NMMNoiseSourceModelInputDataChangedListener)listeners[i]).dispatchNMMNoiseSourceModelInputDataChanged(_mEvent);
          }
        }
    }    
    
    @Override
    public Object getValueAt(int row, int col) {
        
        Object ro=null;        
        //System.out.println("Pobranie danych z i="+row+" i1="+col);
        
        switch (col) {
            case 0: ro=this.etrs[row].getPointID();            
                    break;
            case 1: ro=this.etrs[row].getCoordX();
                    break;
            case 2: ro=this.etrs[row].getCoordY();
                    break;
            case 3: ro=this.etrs[row].getCoordZ();
                    break;
            case 4: ro=this.etrs[row].getLbg();
                    break;
            case 5: ro=this.etrs[row].getLs();
                    break;
        } 
        return ro;
    }  
 
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        
        ListSelectionModel lsm = (ListSelectionModel)lse.getSource();
        this.selectedRow=lsm.getMinSelectionIndex();
    }
}
