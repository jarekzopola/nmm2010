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
public class NMMEventsFrequencyTableModel extends AbstractTableModel implements ListSelectionListener {

    //dane wyświetlane w JTable
    EventTypeRecord[] etrs;
    
    //numer aktualnego wiersza
    int selectedRow;
    
    String[] columnNames = {"Event","Day","Evening","Night"};
    private transient ArrayList<NMMNoiseSourceModelInputDataChangedListener> NMMNoiseSourceModelInputDataChangedListeners=new ArrayList<>();

    public EventTypeRecord[] getModelDataSource() {
        return this.etrs;
    }
    
    
    public NMMEventsFrequencyTableModel(NMMProject _nmmProj) {
       
        //liczba typów zdarzeń w projekcie
        //TODO: W zasadzie powinna być liczba typów zdarzeń w katywnym zapisie
        int net = _nmmProj.getEventTypeTypes().length;
        etrs = new EventTypeRecord[net];
        etrs[0]=new EventTypeRecord();
        this.selectedRow=-1;
        
        for (int j=0;j<net;j++) {
            etrs[j]=new EventTypeRecord();
        }        
        for (int i=0;i<net;i++) {           
           etrs[i].setEventType(_nmmProj.getEventTypeTypes()[i]);                  
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
        return 4;
    }
    
    @Override
    public String getColumnName(int col) {
        return this.columnNames[col];
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        if (col>0) {
            return true;
        } else {
            return false;
        }
    }
    
    
    @Override
    public Class getColumnClass(int col) {
        return this.getValueAt(0,col).getClass();
    }
    
    @Override
    public void setValueAt(Object _value, int row, int col) {        
        
    switch (col+1) {
        case 2: this.etrs[row].dayFrequency=Integer.parseInt(_value.toString());
                break;
        case 3: this.etrs[row].eveningFrequency=Integer.parseInt(_value.toString());
                break;
        case 4: this.etrs[row].nightFrequency=Integer.parseInt(_value.toString());
                break;
    }    
    fireTableCellUpdated(row,col);
    fireNoiseSourceModelInputDataChanged(this);
    }

    private void fireNoiseSourceModelInputDataChanged(NMMEventsFrequencyTableModel _mEvent) {
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
        
        System.out.println("Pobranie danych z i="+row+" i1="+col);
                
        switch (col) {
            case 0: ro=this.etrs[row].ets;
                    break;
            case 1: ro=this.etrs[row].dayFrequency;
                    break;
            case 2: ro=this.etrs[row].eveningFrequency;
                    break;
            case 3: ro=this.etrs[row].nightFrequency;
                    break;
        } 
        return ro;
    }  
    
    public NMMEventType getCurrentEventType() {
        int selRow=this.selectedRow;
        
        NMMEventType et;
        if (selRow>-1) {
            et=this.etrs[selRow].ets;
        } else {
            et=null;
        }        
        return et;
    }
 
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        
        ListSelectionModel lsm = (ListSelectionModel)lse.getSource();
        this.selectedRow=lsm.getMinSelectionIndex();
    }

    public void setModelDataSource(EventTypeRecord[] modelDataSource) {
        this.etrs=modelDataSource;
    }
    
}
