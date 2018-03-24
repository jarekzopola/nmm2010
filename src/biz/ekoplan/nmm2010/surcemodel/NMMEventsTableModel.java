/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.surcemodel;

import biz.ekoplan.nmm2010.toolbox.TimeConverter;
import java.text.DateFormat;
import java.util.Locale;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import nmm2010.NMMEvent;
import nmm2010.NMMEventType;
import nmm2010.NMMProject;

/**
 * 
 * @author jarek
 */
public class NMMEventsTableModel extends AbstractTableModel implements ListSelectionListener {

    
    NMMEvent[] etrs;
    NMMEventType currentEventType;
    NMMProject nmmProj;
    NMMEventsFrequencyTableModel eftm;
    NMMSingleEventsMethodModel semm;
            
    String[] columnNames = {"Begining","End","SEL","Enabled"};

    public NMMEventsTableModel(NMMProject _nmmProj, NMMEventsFrequencyTableModel _eftm, 
            NMMSingleEventsMethodModel _semm) {
       
        this.nmmProj=_nmmProj;
        this.currentEventType=null;
        eftm=_eftm;        
        semm=_semm;
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        if (col==3) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public int getRowCount() {
        
        int rc;
        //jezeli nie ma określonego typu zdarzenia, to nie może być żadnego
        //wiersza do wyświetlenia i tym samym w modelu danych
        if (this.currentEventType==null) {
            rc=0;
        } else {
            rc = etrs.length;
        }        
        return rc;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Class getColumnClass(int col) {
        
        return this.getValueAt(0,col).getClass();
    }       
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        
        switch (col+1) {
                
            case 4:
                this.etrs[row].setEnabled((boolean)value);
            break;
        }
        fireTableCellUpdated(row,col);        
    }
    
    @Override
    public Object getValueAt(int row, int col) {
       
       Object rv=null;
       
       switch (col) {
           case 0:
               rv=TimeConverter.LongToTimeString(this.etrs[row].getStart(), DateFormat.SHORT, Locale.FRENCH); 
               break;
           case 1:
               rv=TimeConverter.LongToTimeString(this.etrs[row].getEnd(), DateFormat.SHORT, Locale.FRENCH); 
               break;
           case 2:
               rv=this.nmmProj.getMeasurement(this.semm.getMeasurementUUID()).getSEL(
                       this.etrs[row].getStart(), this.etrs[row].getEnd()) ;
               break;
           case 3:
               rv=(Boolean)this.etrs[row].isEnabled();
               break;
       }                                   
       return rv;
    }

    @Override
    public String getColumnName(int col) {
        return this.columnNames[col];
    }
    
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        
        int numberOfEvents;
        this.currentEventType=(NMMEventType)(this.eftm.getCurrentEventType());
        if (this.currentEventType!=null) {
            numberOfEvents=this.nmmProj.getEventsNumber(currentEventType);
            etrs=new NMMEvent[numberOfEvents];
            for (int i=0;i<numberOfEvents;i++) {
                etrs=this.nmmProj.getEvents(currentEventType);
            }
            System.out.println("Do drugiej tabeli dodaję : "+numberOfEvents+" zdarzeń");
        }
        this.fireTableDataChanged(); 
    }            
}