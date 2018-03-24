/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.measurement;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author jarek
 */
public class NMMMeasurements implements Serializable{

    ArrayList<NMMMeasurement> projectMeasurements= new ArrayList<>();
    String[] columnNames = {"Pomiar","X[m]","Y[m]","Operator","Kalib.1","Kalib.2","h[m]","UB","Tło","Lokalizacja","Foto","Tor pomiarowy","Color"};
    boolean[] columnEditable = {true,true,true,true,true,true,true,true,true,true,true,true,true};
    
    public void removeMeasurement(int _index) {
        this.projectMeasurements.remove(_index);
    }

    public void addMeasurement(NMMMeasurement _ms) {
        this.projectMeasurements.add(_ms);
    }

    public int getSize() {
        return this.projectMeasurements.size();
    }

    /**
     * Returns reference to indicated measurement
     * @param  measurement (0-...
     * @return 
     */
    public NMMMeasurement getMeasurement(int measurement) {
        return this.projectMeasurements.get(measurement);        
    }

    public ArrayList<NMMMeasurement> getMeasurements() {
        return this.projectMeasurements;
    }

    public void removeMeasurement(NMMMeasurement nmmMeas) {
        this.projectMeasurements.remove(nmmMeas);
    }    
}
