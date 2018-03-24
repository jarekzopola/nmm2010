/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nmm2010;

import biz.ekoplan.nmm2010.toolbox.TimeConverter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author samsung
 */
public class NMMEventsTableModel extends AbstractTableModel {

    ArrayList<String> columns = new ArrayList<String>();
    Object[][] data;
    NMMEventType et;
    int indicator;
    NMMProject np;
    int colsNumber;
    int numberOfAcceptedEvents=0;
    Locale locale;

    public NMMEventsTableModel(int cn, NMMProject _nmmproj, NMMEventType _et,
            Locale _locale) {
        colsNumber=cn;
        indicator=Setup.NOISE_INDICATOR_LEQ;
        this.et=_et;
        np = _nmmproj;
        this.locale=_locale;
        this.rebuildTableModel();

    }

    private void rebuildTableModel() {

        numberOfAcceptedEvents=0;

        DecimalFormat df = new DecimalFormat("###.0");

        // here we find out number of events of selected type
        for (int i=0; i<np.getEventsNumber();i++) {
            if (np.getEvent(i).getEventType().toString().equals(et.toString())) {
                numberOfAcceptedEvents++;             
            }
        }

        // array must have place for following columns:
        // 1). start of event
        // 2). end of event
        // 3). description of the event
        // 4). values for 1st measurement
        // 5). values for 2nd measurement (if in the project)
        // 6). values for 3rd measurement (if in the project)
        // 7). etc.
        data=new Object[numberOfAcceptedEvents][colsNumber];

        //liczymy wartości poziomu ekwiwalentnego dla każdego z pomiarów
        //i dla każdego z okresów wybranego typu eventów
        NMMEvent event;
        int counter=0;

        // dla każdego pomiaru
        for (int pomiar=0; pomiar<this.np.getMeasurementsNumber();pomiar++) {
            // dla każdego zdarzenia
            for (int ev=0; ev<numberOfAcceptedEvents;ev++) {
                do {
                    event=np.getEvent(counter);
                    counter++;
                }
                while (!event.getEventType().toString().equals(et.toString()));
                this.setValueAt(ev, 0, TimeConverter.LongToTimeString(event.getStart(), DateFormat.MEDIUM, this.locale));
                this.setValueAt(ev, 1, TimeConverter.LongToTimeString(event.getEnd(), DateFormat.MEDIUM, this.locale));
                this.setValueAt(ev, 2, event.getEventType().toString());
                double cellValue=-1;

                // cellValue can be calculated only if event happended within
                // measurement time range
                String etykieta;
                if (np.getMeasurement(pomiar).isWithinMeasurement(event)) {
                    switch (indicator) {
                        case Setup.NOISE_INDICATOR_SEL:
                            System.out.println("Przebudowanie dla SEL");
                            cellValue=np.getMeasurement(pomiar).getSEL(event.getStart(),event.getEnd());
                            break;
                        case Setup.NOISE_INDICATOR_LEQ:
                            System.out.println("Przebudowanie dla LEQ");
                            cellValue=np.getMeasurement(pomiar).getLeq(event.getStart(),event.getEnd());
                            break;
                    }
                    etykieta=df.format(cellValue);
                } else {
                    etykieta="-";
                }
                this.setValueAt(ev, 3+pomiar, etykieta);
            }
            counter=0;
        }
        
    }

    public void setEventType(NMMEventType _et) {
        this.et=_et;
        this.rebuildTableModel();
    }

    public void setIndicatorType(int _indicator) {
        indicator=_indicator;
        this.rebuildTableModel();
    }

    public void setColumns(String[] colName) {
        for (String colName1 : colName) {
            columns.add(colName1);
            System.out.println("Dodaję w modelu tabeli kolumnę:" + colName1);
        }
    }
    
    public void setValueAt(int rowIndex, int columnIndex, Object _obj) {
        data[rowIndex][columnIndex]=_obj;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        /*System.out.println("Pobieram z modelu dane dla ["+rowIndex+"]["+
                columnIndex+"[");*/
        return data[rowIndex][columnIndex];
    }

    public float[] getColumn(int _columnNumber) {

        float[] column = new float[this.numberOfAcceptedEvents];
        for (int i=0; i<this.numberOfAcceptedEvents; i++) {
            String fl=this.data[i][_columnNumber].toString();
            fl=fl.replace(',', '.');
            try {
                column[i]=Float.parseFloat(fl);
            } catch (NumberFormatException e) {
                column[i]=0;
            }
            
        }
        return column;
    }

    @Override
    public String toString() {
        String str="";
        int x=this.getColumnCount();
        int y=this.getRowCount();
        for (int i=0; i<y;i++) {
            for (int j=0; j<x; j++) {
                System.out.println("Pobieram z : y="+y+" x="+x);
                str=str+"\t"+this.getValueAt(i,j);
            }
            str=str+"\n";
        }
        return str;
    }

}
