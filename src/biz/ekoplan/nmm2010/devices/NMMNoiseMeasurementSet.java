/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.devices;

import java.io.Serializable;

/**
 *
 * @author Jarek
 */
public class NMMNoiseMeasurementSet implements Serializable {

    private static final long serialVersionUID=1L;
    
    private String labratoryID="00";
    private String measurementSetDescription="New measurement set";
    private String calibrationInfo="Davice calibration Info";
    private double uncertaintyB;

    /**
     * Tworzy obiekt zawierający pełną identyfikację toru pomiarowego, wraz z
     * całkowita niepewnością typu B dla tego zestawu pomiarowego.
     * @param setLabID  - identyfikator zestawu pomiarowego (unikalny w projekcie)
     * @param msSetDesc - opis zestawu pomiarowego
     * @param calibInfo - opis dokumentów wzorcowania
     * @param _uncertaintyB  - wartość niepewności typu B toru pomiarowego
     */
        
    public NMMNoiseMeasurementSet(String setLabID, String msSetDesc, String calibInfo, double _uncertaintyB) {
        this.labratoryID=setLabID;
        this.measurementSetDescription=msSetDesc;
        this.calibrationInfo=calibInfo;
        this.uncertaintyB=_uncertaintyB;
    }

    /**
     * Pozwlala tworzyć obiekt, który nie zawiera rzeczywistej informacji o
     * zestawie pomiarowym, a jedynie pola do ustawienia.
     */
    public NMMNoiseMeasurementSet() {
        this.labratoryID="00";
        this.measurementSetDescription="New measuring set";
        this.calibrationInfo="Device calibration info";
        this.uncertaintyB=0.9;
    }

    @Override
    public String toString() {
        return this.labratoryID;
    }

    public void setLaboratoryID(String _labID) {
        this.labratoryID=_labID;
    }

    public void setMeasureentSetDescription(String _msmSetDsc) {
        this.measurementSetDescription=_msmSetDsc;
    }

    public void setCalibraionInfo(String _calibInfo) {
        this.calibrationInfo=_calibInfo;
    }

    public String getLaboratoryID() {
        return this.labratoryID;
    }

    public String getMeasurementSetDescription() {
        return this.measurementSetDescription;
    }

    public String getCalibrationInfo() {
        return this.calibrationInfo;
    }

    public double getUncertaintyB() {
        return this.uncertaintyB;
    }
}
