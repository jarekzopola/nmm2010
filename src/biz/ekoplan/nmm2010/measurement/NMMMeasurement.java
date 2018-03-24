/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.measurement;


import biz.ekoplan.nmm2010.devices.NMMNoiseMeasurementSet;
import biz.ekoplan.nmm2010.enums.RecordValueType;
import biz.ekoplan.nmm2010.enums.TimePeriods;
import biz.ekoplan.nmm2010.toolbox.NMMToolbox;
import biz.ekoplan.nmm2010.toolbox.TimeConverter;
import java.awt.Color;
import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.*;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import nmm2010.MeasurementRecord;
import nmm2010.NMMEvent;

/**
 *
 * @author Jarosław Kowalczyk
 */
public class NMMMeasurement implements Serializable {

    private static final long serialVersionUID=1L;
    
    final boolean DEBUG = true;

    private final int INITIAL_MEASUREMENT_LENGTH=3600;
    private ArrayList<MeasurementRecord> measurementRecords = new ArrayList<MeasurementRecord>(INITIAL_MEASUREMENT_LENGTH);
    private long measurementBeginTime;
    private long measurementTimeResolution=1000;
    private Color measurementColor;
    private File measurementAudio;
    private String measurementOperator="unknown";
    private boolean drawLabels = true;
    private String measurementDescription="new measurement";
    private String measurementRemarks="";
    private double firstCalibration=0;
    private double lastCalibration=0;
    private double coordinateX=0;
    private double coordinateY=0;
    private double measurementHeight=4;
    private double measurementUncertaintyB=0.86;
    static int measurementsCounter;
    private double backgroundNoiseLevel=30;
    private ImageIcon imageIcon;
    private double totalLAeq=0;
    private boolean measurementVisible=true;
    Object[] buffer;
    private ArrayList<MeasurementRecord> measurementInternalBuffer=new ArrayList<MeasurementRecord>(INITIAL_MEASUREMENT_LENGTH);
    private NMMNoiseMeasurementSet measurementSet;
    private UUID MUID;    
    private transient ArrayList<NMMMeasurementListener> NMMMeasurementListeners = new ArrayList();
    
    public synchronized void addMeasurementListener(NMMMeasurementListener l)
    {
        if (!NMMMeasurementListeners.contains(l)) {
            NMMMeasurementListeners.add(l);
        }
    }

    public synchronized void removeMeasurementListener(NMMMeasurementListener l) {
        NMMMeasurementListeners.remove(l);
    }
    
    public synchronized void addMeasurementChangedListener(NMMMeasurementListener l)
    {
        
        if (this.NMMMeasurementListeners==null) {
            NMMMeasurementListeners = new ArrayList<NMMMeasurementListener>();
        }
        if (!NMMMeasurementListeners.contains(l)) {
            NMMMeasurementListeners.add(l);
            System.out.println("Liczba nasłuchiwaczy zmian w projekcie: "+this.NMMMeasurementListeners.size());
        }
    }
    
    @Override
    public String toString() {
        return this.measurementDescription;        
    }
    
    public synchronized void removeMeasurementChangedListener(NMMMeasurementListener l) {
        NMMMeasurementListeners.remove(l);
    }
    
    protected void fireNMMMeasurementChangedEvent(NMMMeasurementChangedEvent _mEvent) {

        if (!NMMMeasurementListeners.isEmpty()) {
            Object[] listeners = NMMMeasurementListeners.toArray();        
            int numListeners = listeners.length;
            for (int i = 0; i<numListeners; i+=2) {
              if (listeners[i] instanceof NMMMeasurementListener) {
                   ((NMMMeasurementListener)listeners[i]).dispatchNMMMeasurementChangedEvent(_mEvent);
              }
            }    
        }        
    }
    
    public long getMeasurementTimeResolution() {
        return measurementTimeResolution;
    }

    public void setMeasurementTimeResolution(long measurementTimeResolution) {
        this.measurementTimeResolution = measurementTimeResolution;
    }
    
    
    /**
     * Use this method to assign to measurement, identifization of measurement devices set used during measurement.
     * @param _ms - Reference to object containing identification of device measurement set.
     */
    public void setMeasurementSet(NMMNoiseMeasurementSet _ms) {
        this.measurementSet=_ms;
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }

    /**
     * Rse this method to get identification of measurement devices set used during measurement
     * @return - String rpresenting devices measurement set
     */
    public NMMNoiseMeasurementSet getMeasurementSet() {
        return this.measurementSet;
    }

    /**
     * Set measurement not visible. I will not be drawn during viewport refresh
     */
    public void setVisible(boolean _isVisible) {
        this.measurementVisible=_isVisible;
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }

    /**
     *
     * @return
     */
    public boolean getVisible() {
        return this.measurementVisible;
    }

    public File getMeasurementAudio() {
        return measurementAudio;
    }

    public void setMeasurementAudio(File measurementAudio) {
        this.measurementAudio = measurementAudio;        
    }    
    
    /**
     * 
     * @return - uncertainy B value
     */
    public double getUncertaintyB() {
        return this.measurementUncertaintyB;
    }

    public void setUncertaintyB(double _uncertaintyB) {
        if (_uncertaintyB>=0) {
            this.measurementUncertaintyB = _uncertaintyB;
        } else {            
        }
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }

    public double getCoordinateX() {
        return this.coordinateX;
    }

    public double getCoordinateY() {
        return this.coordinateY;
    }
    
    public void setCoordinateX(double _coordinateX) {
        this.coordinateX = _coordinateX;
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }
    
    public void setCoordinateY(double _coordinateY) {
        this.coordinateY = _coordinateY;
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }

    public void setRemarks(String _remarks) {
        this.measurementRemarks=_remarks;
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }

    public String getRemarks() {
        return this.measurementRemarks;
    }
    
    public long getMeasurementBeginTime() {
        return this.measurementBeginTime;
    }

    public void setInitialCalibration(double _initialCalibration) {
        this.firstCalibration=_initialCalibration;
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }

    public void setFInalCalibration(double _finalCalibration) {
        this.lastCalibration=_finalCalibration;
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }

    public double getInitialCalibration() {
        return this.firstCalibration;
    }

    public double getFinalCalibration() {
        return this.lastCalibration;
    }

    public void setPicture(ImageIcon _imageIcon) {
        this.imageIcon=_imageIcon;
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }

    public ImageIcon getPicture() {
        return this.imageIcon;
    }

    /**
     *
     * @param _newBeginTime
     */
    public void setMeasurementBeginTime(long _newBeginTime) {
        this.measurementBeginTime=_newBeginTime;    
        //inform NMMProject to make necessary updates
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }
    


    /**
     * Set if measurement's labels are to be drawn on the screen or not.
     * @param _drawLabels - boolean
     */
    public void setDrawLabels(boolean _drawLabels) {
        this.drawLabels=_drawLabels;
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }

    
    //TODO:  Tutaj wywala się błąd !!! Tylko nie wiem jeszcze dlaczego? (Index out of bounds)
    public double getRecordValue(int _index, RecordValueType _nli) {
       
        return this.measurementRecords.get(_index).getRecordValue(_nli);
    }

    /**
     * 
     * @param _address integer (record number)
     * @param _newValue double (noise value)
     */
    public void setRecordValue(int _address, double _newValue, RecordValueType _nli) {
        this.measurementRecords.get(_address).setRecordValue(_nli,_newValue);
        this.updateLeq();
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }

    /**
     * Check if an event happened during measurement time range
     * @param _anEvent
     * @return
     */
    public boolean isWithinMeasurement(NMMEvent _anEvent) {
        boolean isWithin=false;
        if ((_anEvent.getStart()>=this.measurementBeginTime) &
                (_anEvent.getEnd()<=this.getMeasurementEndTime())) {
            isWithin=true;
        }
        return isWithin;
    }

    /**
     * 
     * @param
     * @return
     */
    public boolean isWithinMeasurement(long st, long en) {
        boolean isWithin=false;
        if ((st>=this.measurementBeginTime) &
                (en<=this.getMeasurementEndTime())) {
            isWithin=true;
        }
        return isWithin;
    }

    public double getHeight() {
        return this.measurementHeight;
    }

    public void setHeight(double _measurementHeight) {
        this.measurementHeight=_measurementHeight;
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }

    public void updateLeq() {
                
        long end=this.getMeasurementEndTime()-this.measurementTimeResolution;
        this.totalLAeq=this.getLeq(measurementBeginTime, end);
    }

    public double getTotalLeq() {
        this.updateLeq();
        return this.totalLAeq;
    }

    public int getNHourIndex(int _hour) {
        int timeIndex=-1;

        String localStringTime="";
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(this.measurementBeginTime);
        int fgg=cal.get(Calendar.HOUR_OF_DAY);
        int fmm=cal.get(Calendar.MINUTE);
        int fss=cal.get(Calendar.SECOND);

        long szukanyMoment=this.measurementBeginTime+
                    (_hour*3600*1000)+((24-fgg)*3600*1000)-fmm*60*1000-fss*1000;
        timeIndex=this.getRecordIndex(szukanyMoment);
        //System.out.println(TimeConverter.LongToDateString(szukanyMoment, DateFormat.LONG));
        return timeIndex;
    }

    /**
     * Returns milisecond that starts full hour in measurement, or -1 if there is no such an hour
     * @param _hour - full hour
     * @return long - milisecond that starts _hour in measurement
     */
    public long getNHourMilisecond(int _hour) {

        String localStringTime="";
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(this.measurementBeginTime);
        int fgg=cal.get(Calendar.HOUR_OF_DAY);
        int fmm=cal.get(Calendar.MINUTE);
        int fss=cal.get(Calendar.SECOND);

        long szukanyMoment=-1;

        //assume that measuremnt is 24h long, or shorter

        //consider following cases:
        // 1.
        // 06.00 --|----------------22.00---0.00-----------06.00--|---------------------
        //         fgg(07.13.41)   szukana                        K(07.17.43)
        //
        // 2.
        // 06.00 --|----------------22.00-|-0.00-----------06.00--|---------------------
        //         fgg(07.13.41)                           szuk.  K(07.17.43)

        // S - measurement starts

        // case 1 (see explanation above)
        if (_hour>fgg) {
            szukanyMoment=this.measurementBeginTime+((_hour-fgg)*3600000)-fmm*60*1000-fss*1000;
        }

        if (_hour<fgg) {
            szukanyMoment=this.measurementBeginTime+
                    (_hour*3600*1000)+((24-fgg)*3600*1000)-fmm*60*1000-fss*1000;
        }
        if (_hour==fgg) {
            szukanyMoment=this.measurementBeginTime;
        }
        
//        System.out.println(TimeConverter.LongToDateString(szukanyMoment, 
//                DateFormat.LONG) + " sekund:"+szukanyMoment/1000+" (początek pomiaru [sek]:"+
//                this.measurementBeginTime/1000);
        return szukanyMoment;
    }

    /**
     * Return statistical noise level L95 based on all measurement
     * @return - double
     */
    
    public double getL95() {
        double l95=0;
        ArrayList<Double> al = new ArrayList<Double>();
        for (int i=0; i<this.getMeasurementLength();i++) {
            al.add(this.getRecordValue(i, RecordValueType.LAeq));
        }
        Collections.sort(al);
        int ind=(al.size()/100)*5;
        l95=al.get(ind);
        return l95;
        /* TODO Zmień sposób obliczania l95, ponieważ ten jest chyba nie do końca pewny, a w każdym razie nie 
         * będzie działał na małych zbiorach.
         */
    }
    

    /**
     * Check if specified time range is entirely within measurement time
     * range and calculates laeq value. Otherwise it returns -1
     * @param _startTime
     * @param _endTime
     * @return leq value for period from _startTime to _endTime, or -1
     */
    public double getLeq(long _startTime, long _endTime) {

        double leq=0;
        int el;
        int fi;
        double suma=0;

        if (!this.isWithinMeasurement(_startTime, _endTime)) {
            System.out.println("Próba liczenia LAeq poza godzinami pomiaru !");
            return -1;
        }

        //indeks pierwszego rekordu
        fi=this.getRecordIndex(_startTime);
        //ostatni rekord
        el=fi+(int)((_endTime-_startTime)/this.measurementTimeResolution);

        for (int i=fi; i<=el; i++) {
            double rv=this.getRecordValue(i, RecordValueType.LAeq);
            //System.out.println("Liczę Leq z: "+rv);
            suma=suma+Math.pow(10, 0.1*rv);
        }
        long l=1+((_endTime-_startTime)/this.measurementTimeResolution);
        suma=suma/(l);
        //System.out.println("Długość zdarzenia: "+l);
        leq=10*Math.log10(suma);
        return leq;
    }
    
    /**
     * Check if specified time range is entirely within measurement time
     * range and calculates laeq value. Otherwise it returns -1
     * @param _startTime
     * @param _endTime
     * @return leq value for period from _startTime to _endTime, or -1
     */
    public double getLeq(long _startTime, long _endTime, boolean _Exclusions) {

        double leq=0;
        int el;
        int fi;
        double suma=0;

        if (!this.isWithinMeasurement(_startTime, _endTime)) {
            System.out.println("Próba liczenia LAeq poza godzinami pomiaru !");
            return -1;
        }

        //indeks pierwszego rekordu
        fi=this.getRecordIndex(_startTime);
        //ostatni rekord
        el=fi+(int)((_endTime-_startTime)/this.measurementTimeResolution);

        //excluded counter
        long exclCounter=0;
        
        for (int i=fi; i<=el; i++) {
            if (!this.getRecord(i).isExcluded()) {
                double rv=this.getRecordValue(i, RecordValueType.LAeq);                
                suma=suma+Math.pow(10, 0.1*rv);
            } else {
                exclCounter++;
            }
        }
        long l=1+((_endTime-_startTime)/this.measurementTimeResolution);
        suma=suma/(l-exclCounter);
        //System.out.println("Długość zdarzenia: "+l);
        leq=10*Math.log10(suma);
        return leq;
    }
    
    /**
     * 
     * @param _uuid         Measurement identificator
     * @param _period       length of worst period in seconds
     * @param _tp           day period to be analysed
     * @return              result
     */
    
    public double calculateWorstPeriodLAeq(int _period, TimePeriods _tp) {
        
        double res=-1;
        double tmpRes=-1;
        long periodMSec;
        long offset;
        
        periodMSec=_period*this.measurementTimeResolution;
        int finalIndex=-1;
        
        finalIndex=this.getMeasurementLength()-_period;
        
        for (int i=0; i<finalIndex; i++) {
            offset=i*this.measurementTimeResolution;            
            if ((TimeConverter.isTimeInPeriod(this.measurementBeginTime+periodMSec+offset, _tp)) && 
                    (TimeConverter.isTimeInPeriod(this.measurementBeginTime+offset, _tp))) {
                tmpRes=this.getLeq(this.measurementBeginTime+offset
                    ,this.measurementBeginTime+periodMSec+offset, true);
                if (tmpRes>res) {
                    res=tmpRes;
                    //System.out.println("Najgorszy podokres (" + _period + " sek) zaczyna się o "+ TimeConverter.LongToTimeString(this.measurementBeginTime+offset, DateFormat.MEDIUM, Locale.ENGLISH));
                }
            } else {
                res=-1;
            }            
        }                        
        return res;
    }
    

    /**
     * @param _startTime
     * @param _endTime     
     */
    public double getMaxRecordValue(long _startTime, long _endTime) {

        double maxValue=0;
        int el;
        int fi;        

        if (!this.isWithinMeasurement(_startTime, _endTime)) {
            System.out.println("Próba znalezienia wartości poza godzinami pomiaru !(NMMMeasurement/getMaxRecordValue)");
            return -1;
        }

        //indeks pierwszego rekordu
        fi=this.getRecordIndex(_startTime);

        //ostatni rekord
        el=fi+(int)((_endTime-_startTime)/this.measurementTimeResolution);

        maxValue=this.getRecordValue(fi+1, RecordValueType.LAeq);
        for (int i=fi+1; i<=el; i++) {
            if (this.getRecordValue(i, RecordValueType.LAeq)>maxValue) {
                maxValue=this.getRecordValue(i, RecordValueType.LAeq);
            }            
        }        
        return maxValue;
    }
    
    /**
     * 
     * @return sound equivalent noise level for all day records
     */
     
    
    /**
     * Check if specified time range is entirely within measurement time
     * range and calculates laeq value. Otherwise it returns -1
     * @param _tp - time period (TimePeriod enum)
     * @return leq value for period from _startTime to _endTime, or -1
     */
    public double getPeriodLeq(TimePeriods _tp) {

        double leq=0;        
        double suma=0;
        int counter=0;

        for (int i=0; i<this.getMeasurementLength(); i++) {
            long t=this.getRecordTime(i);
            //if t is within DAY, NIGHT, etc. then take it into account
            if (TimeConverter.isTimeInPeriod(t, _tp)) {
                double rv=this.getRecordValue(i, RecordValueType.LAeq);
                suma=suma+Math.pow(10, 0.1*rv);
                counter++;
            }
        }
        long l=1+counter;
        suma=suma/(l);       
        leq=10*Math.log10(suma);
        //System.out.println("Wykonano obliczenia dla: "+_tp+" LAeq="+leq+" w oparciu o liczbę rekordów:"+counter);
        return leq;
    }
    
    /**
     * Check if specified time range is entirely within measurement time
     * range and calculates laeq value. Otherwise it returns -1
     *  
     * Calculation takes into account exclusions in measurement. Excluded records 
     * are simply skipped like non existent
     * 
     * @param _tp - time period (TimePeriod enum)
     * @return leq value for period from _startTime to _endTime, or -1
     */
    public double getPeriodLeq(TimePeriods _tp, boolean _Exclusions) {

        double leq=0;        
        double suma=0;
        int counter=0;

        for (int i=0; i<this.getMeasurementLength(); i++) {
            long t=this.getRecordTime(i);
            //if t is within DAY, NIGHT, etc. and is NOT excluded, then take it into account
            if ((TimeConverter.isTimeInPeriod(t, _tp)) && (!this.getRecord(i).isExcluded())) {
                double rv=this.getRecordValue(i, RecordValueType.LAeq);
                suma=suma+Math.pow(10, 0.1*rv);
                counter++;
            }
        }
        long l=1+counter;
        suma=suma/(l);       
        leq=10*Math.log10(suma);
        //System.out.println("Wykonano obliczenia dla: "+_tp+" LAeq="+leq+" w oparciu o liczbę rekordów:"+counter);
        return leq;
    }
    
    /**
     * Check if specified time ranges are entirely within measurement time
     * range and calculates laeq value. Otherwise it returns -1
     * @param array of time ranges as NNMEvents
     * @return leq value for periods from _startTime to _endTime, or -1
     */
    public double getLeq(NMMEvent[] _timeRanges) {

        double leq=0;
        int el;
        int fi;
        double suma=0;
        int periodsNumber=_timeRanges.length;

        //if any of the periods is outsied measurement time, then calculation is impossible, method returns -1
        for (int i=0; i<periodsNumber; i++) {
            if (!this.isWithinMeasurement(_timeRanges[i].getStart(), _timeRanges[i].getEnd())) {
                return -1;
            }
        }
        //now calculate LAeq in loop over all periods
        long l=0;
        for (int i=0; i<periodsNumber; i++) {
            //getting first period
            NMMEvent ev = _timeRanges[i];
            //getting first record index
            fi=this.getRecordIndex(ev.getStart());
            //getting last record index
            el=fi+(int)((ev.getMilisLength())/this.measurementTimeResolution);
            for (int i2=fi; i2<el; i2++) {  // czy tu powinno być mniejsze (jak jest) czy może <=
                double rv=this.getRecordValue(i2, RecordValueType.LAeq);                
                suma=suma+Math.pow(10, 0.1*rv);
            }
            l=l+1+((ev.getMilisLength())/this.measurementTimeResolution);
        }                     
        suma=suma/(l);        
        leq=10*Math.log10(suma);
        return leq;
    }




     public double getSEL(long _startTime, long _endTime) {

        double leq=0;
        double sel=0;
        leq=this.getLeq(_startTime, _endTime);
        if (leq==-1) {
            sel=-1;
        } else {
            long l=1+((_endTime-_startTime)/this.measurementTimeResolution);
            sel=leq+10*Math.log10(l);
         }
        return sel;
    }

    public MeasurementRecord getRecord(int _index) {
        
        MeasurementRecord mr;
        mr = this.measurementRecords.get(_index);
        return mr;
    }


     /**
     * Get measurement description
     */
    public String getDescription() {
        return this.measurementDescription;
    }

    public void setDescription(String _description) {
        this.measurementDescription=_description;
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }
    
    /**
     * 
     */
    public void copyRecords(int _indexStart, int _indexEnd) {
        
        measurementInternalBuffer.clear();
        measurementInternalBuffer.addAll(this.measurementRecords.subList(_indexStart, _indexEnd));
        this.updateLeq();
    }

    
    /**
     * Delete selected range of records from measurement
     * @param _indexStart - first record has index equal to 0, second to 1, etc.
     * @param _indexEnd  - last measurement record has index equal to 
     * measurement size minus 1
     */
    
    public void deleteRecords(int _indexStart, int _indexEnd) {
        
        NMMToolbox.debugMessage("Delete part of the measurement: from="+_indexStart+" to="+_indexEnd+" last record #="
                +(this.getMeasurementLength()-1), DEBUG);

        if ((_indexStart<0) || (_indexEnd>this.getMeasurementLength()-1)) {
            int dec=JOptionPane.showConfirmDialog(null,"Cannot cut outsied measurement!",
                    "NMM Information",JOptionPane.OK_OPTION);
            return;
        }
        if ((_indexStart==0) && (_indexEnd<this.getMeasurementLength()-1)) {
            /* if one delete records from the very begining than there must
             * be decision on what to do with measurement time
             */
            int keepMsTime=JOptionPane.showConfirmDialog(null, "Do you want to keep measurement time?", "NMM Question",
                    JOptionPane.YES_NO_OPTION);
            this.measurementRecords.subList(_indexStart, _indexEnd+1).clear();
            if (keepMsTime==JOptionPane.OK_OPTION) {
                this.measurementBeginTime =_indexEnd*this.measurementTimeResolution+this.measurementBeginTime;
            }
        } else if ((_indexStart!=0) && (_indexEnd<this.getMeasurementLength()-1)) {
            /*  if one cuts records from the middle of the measurement, then
             *  there is no choice. Record's time of part of the records must
             *  change. Display warning only.
             */
            int keepMsTime=JOptionPane.showConfirmDialog(null, "This will change record's time!", "NMM Question",
                    JOptionPane.YES_NO_OPTION);
            if (keepMsTime==JOptionPane.OK_OPTION) {
                this.measurementRecords.subList(_indexStart, _indexEnd+1).clear();
            }
        } else {
            // if the final part of the measurement is cleared, then there is
            // no need to recalculate anything
            this.measurementRecords.subList(_indexStart, _indexEnd+1).clear();
            this.measurementRecords.remove(this.measurementRecords.size()-1);
        }
        this.updateLeq();
    }

    public void cutRecords(int _indexStart, int _indexEnd) {
        this.copyRecords(_indexStart, _indexEnd);
        this.deleteRecords(_indexStart, _indexEnd);
        //tutaj nie trzeba aktualizować LAeq, bo deleteRecords to juz robi
    }

    /**
     *
     * @param _indexStart -
     */
    public void pasteRecords(int _indexStart) {
        
        System.out.println("Mam wstawić w pomiar, w pozycję "+_indexStart+" bufor"
                + "o długości: "+measurementInternalBuffer.size());
        System.out.println("Przed: "+this.measurementRecords.size());
        this.measurementRecords.addAll(_indexStart, measurementInternalBuffer);
        System.out.println("Po: "+this.measurementRecords.size());
        if (_indexStart==0) {
            System.out.println("Start pomiaru przed: "+this.measurementBeginTime);
            this.measurementBeginTime=this.measurementBeginTime-
                    (this.measurementTimeResolution*measurementInternalBuffer.size());
            System.out.println("Start pomiaru po: "+this.measurementBeginTime);
        }
        this.updateLeq();
    }

    /**
     *  trim measurement to full hours - generally this cuts records before
     *  first full hour, and after the last full hour
     */
    public void trimMeasurement() {
        
        long before_full_hour=(3600*this.measurementTimeResolution)
                -(this.getMeasurementStartTime()%(3600*this.measurementTimeResolution));
        int records_before_full_hour=(int)(before_full_hour/this.measurementTimeResolution);
        long after_full_hour=this.getMeasurementEndTime()%(3600000);
        int records_after_full_hour=(int)(after_full_hour/this.measurementTimeResolution);        

        if ((records_before_full_hour==3600000/this.measurementTimeResolution)
                && (records_after_full_hour==0)) {
            JOptionPane.showMessageDialog(null, "This measurement seems to be trimmed already" , "NMM Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        //delete records before full hour
        this.measurementRecords.subList(0,records_before_full_hour).clear();

        //delete records after full hour
        this.measurementRecords.subList(this.measurementRecords.size()-
                records_after_full_hour,this.measurementRecords.size()).clear();

        //change measurement start time, because several records have been deleted
        this.measurementBeginTime=this.measurementBeginTime+before_full_hour;

        this.updateLeq();
        
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }

    /**
     * Find out if measurement's labels will be drawn
     * @return boolean
     */
    public boolean getDrawLabels() {
        return this.drawLabels;
    }

    public void setBackgroundNoiseLevel(double _bnl) {
        this.backgroundNoiseLevel=_bnl;
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }

    public double getBackgroundNoiseLevel() {
        return this.backgroundNoiseLevel;
    }

    public NMMMeasurement(long _MeasurementBeginTime) {
        measurementBeginTime=_MeasurementBeginTime;
        measurementTimeResolution=1000;
        this.measurementColor=new Color(0,0,0);
        NMMMeasurement.measurementsCounter++;        
        this.MUID=UUID.randomUUID();
        if (this.DEBUG) {
            System.out.println("Just created new Measurement (MUID="+this.MUID.toString());
        }
        this.imageIcon=null;
        this.measurementVisible=true;
        this.measurementAudio=null;
    }    
    
    /**
     * 
     * @param _measurementDate
     * @param _measurementBeginTime
     * @param _mtr - measurement time resolution in miliseconds
     */
    public NMMMeasurement(String _measurementDate, String _measurementBeginTime, long _mtr, Locale _locale) {
        measurementBeginTime=TimeConverter.StringToLong(_measurementDate, _measurementBeginTime, _locale);
        measurementTimeResolution=_mtr;
        this.measurementVisible=true;
        this.measurementColor=new Color(0,0,255);
        NMMMeasurement.measurementsCounter++;
        this.imageIcon=null;
        this.measurementDescription=this.measurementDescription+String.valueOf(NMMMeasurement.measurementsCounter);
        this.MUID=UUID.randomUUID();
        this.measurementAudio=null;
        if (this.DEBUG) {
            System.out.println("Just created new Measurement (MUID="+this.MUID.toString());
        }
    }

    /**
     * Use this method to find out if measurement has picture or not.
     * @return 
     */
    public boolean hasPicture() {                
        return (this.imageIcon!=null);
    }
    
    public void setOperator(String mo) {
        this.measurementOperator=mo;
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }
    
    public String getOperator() {
        return this.measurementOperator;
    }

    public void setMeasurementColor(Color c) {
        this.measurementColor=c;
        this.fireNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(this));
    }

    public Color getMeasurementColor() {
        return this.measurementColor;
    }

    /**
     * Use this method to get start time of the measurement
     * @return long, time of the first record
     */
    public long getMeasurementStartTime () {
        return this.measurementBeginTime;        
    }
    
    /**
     * Use this method to get end time of the measurement
     *
     * This is 5 records long measurement
     * if ms1=0 then met=5000 (miliseonds)
     *
     *       rl
     *    |------|------|------|------|------|
     *    ms1
     *    rs1
     *
     *    rl - record length (in milisconds, default 1000ms)
     *    ms1 - measurement begin time (time at which first record starts)
     *    rs1 - first record start time in miliseconds
     *    met - start time in miliseconds of last record
     * 
     * @return long, start time of the last record
     */
    public long getMeasurementEndTime() {
        long met;
        met=this.measurementBeginTime+this.getMeasurementLength()*this.measurementTimeResolution;
        return met;
    }

    /**
     * Returns number of records in measurement
     * @return int, number of records
     */
    public int getMeasurementLength() {
        int ml = this.measurementRecords.size();
        return ml;
    }

    public UUID getMUID() {
        return this.MUID;
    }
    
    public void addRecord(RecordValueType _nli, double measuredValue) {
        MeasurementRecord mr = new MeasurementRecord();
        mr.setRecordValue(_nli, measuredValue);
        measurementRecords.add(mr);
    }

    /**
     * Use this method to find out number of record (0....n) corresponding
     * to time given as method parameter. 
     * @param time (as long)
     * @return - index of measurement record corresponding to given time, or -1
     * if time is out of measurement time boundaries
     */
    public int getRecordIndex(long time) {
        int indeks=-1;     //  in case time is outside measurement time boundaries
        indeks=(int)((time - this.measurementBeginTime)/this.measurementTimeResolution);
        return indeks;
    }

    /**
     * 
     * @param rec
     * @return
     */
    public long getRecordTime(int rec) {
        long tm=0;
        tm=this.getMeasurementStartTime()+rec*this.measurementTimeResolution;
        return tm;
    }

    /**
     *
     * @param _time
     * @return
     */
    public double getRecordValue(long _time, RecordValueType _nli) {
        double value=0;
        int indeks=(int)((_time - this.measurementBeginTime)/this.measurementTimeResolution);
        try {
            value=measurementRecords.get(indeks).getRecordValue(_nli);
            //System.out.println("START POMIARU:"+this.measurementBeginTime+" ŻADANIE:"+_time);
        }
        catch (IndexOutOfBoundsException e) {
            System.out.println("!_____Błąd podczas odwołania do rekordu pomiaru: "+e.toString());
            System.out.println("START POMIARU:"+this.measurementBeginTime+" ŻADANIE:"+_time);
        }
        return value;
    }    

    public void addToRecord(RecordValueType recordValueType, float dane, int recordNumber) {
        
        MeasurementRecord mr;
        mr = this.getRecord(recordNumber);
        mr.setRecordValue(recordValueType, dane);        
    }
}
