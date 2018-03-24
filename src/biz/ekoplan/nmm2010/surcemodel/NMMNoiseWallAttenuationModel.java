/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.surcemodel;

import biz.ekoplan.nmm2010.enums.NoiseLevelIndicators;
import biz.ekoplan.nmm2010.enums.NoiseModelResult;
import biz.ekoplan.nmm2010.enums.NoiseSourceModelType;
import biz.ekoplan.nmm2010.measurement.NMMMeasurement;
import biz.ekoplan.nmm2010.surcemodel.result.NMMNoiseSourceModelResult;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.table.AbstractTableModel;
import nmm2010.NMMCalculationReport;
import nmm2010.NMMEventType;
import nmm2010.NMMProject;

/**
 *
 * @author Jarek
 */
public class NMMNoiseWallAttenuationModel implements NMMNoiseSourceModel, Serializable, 
        NMMNoiseSourceModelInputDataChangedListener {

    final boolean DEBUG = false;
    
    //nazwa opisowa modelu (przyjazna dla użytkownika)
    private String modelName="New noise wall attenuation model";
    
    //zmienne pomiarów w punktach przed i po referencyjnych i odniesienia
    private NMMMeasurement measBeforeRef;
    private NMMMeasurement measBeforeImi;
    private NMMMeasurement measAfterRef;
    private NMMMeasurement measAfterImi;
    
    //zdarzenia w punktach przed i po referencyjnych i odniesienia
    private NMMEventType eventsBeforeRef;
    private NMMEventType eventsBeforeImi;
    private NMMEventType eventsAfterRef;
    private NMMEventType eventsAfterImi;
    
    //ostateczny wynik modelu obliczeniowego - moc akustyczna
    private double DILp=0;

    //raport z obliczen
    NMMCalculationReport calcReport;
    
    private final ArrayList<Object> NMMNoiseSourceModelChangedListeners;
    
    private NMMProject nmmProj;    
    private long startTime;
    private long endTime;
    
    public NMMNoiseWallAttenuationModel(NMMProject _nmmProj, 
            long _startTime, long _endTime) {
        
        this.DILp = 0;        
        NMMNoiseSourceModelChangedListeners = new ArrayList<>();        
        this.nmmProj=_nmmProj;        
        this.startTime=_startTime;
        this.endTime=_endTime;                
        this.fireNoiseSourceModelChangedEvent(this);
    }
              
    public NMMProject getProject() {
        return this.nmmProj;
    }
    
    public void recreateListenersArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NoiseSourceModelType getNoiseModelType() {
        return NoiseSourceModelType.SOUND_POWER_LEVEL_MODEL;
    }
    
    /**
     * Get model name (description, friendly for NMM user)
     */
    @Override
    public String getModelName() {
        return this.modelName;
    }

    /**
     * Set model name (description, friendly for NMM user)
     * @param _modelName String
     */
    @Override
    public void setModelName(String _modelName) {
        this.modelName=_modelName;
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public long getEndTime() {
        return this.endTime;
    }

    @Override
    public double getNoiseModelResult(NoiseLevelIndicators _nli) {
                
        double res;        
        if (_nli==NoiseLevelIndicators.DILp) {
            res=this.DILp;
        } else {
            res=-1;
        }
        return res;
    }

    @Override
    public NMMNoiseSourceModelResult getNoiseModelComplexResult(NoiseModelResult _nli) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean recalculateModel() {
        
        boolean recalculatedSuccessfully=true;
        
        System.out.println("Przeliczam model obliczeniowy skuteczności ekranu akustycznego ...");

        try {
            //czyszczę stary raport z obliczeń
            this.calcReport = new NMMCalculationReport();                        
            double beforeDifference=0;
            double afterDifference=0;        
            beforeDifference = this.nmmProj.getLeqLogAverage(this.eventsBeforeRef, this.measBeforeRef.getMUID())-
                    this.nmmProj.getLeqLogAverage(this.eventsBeforeImi, this.measBeforeImi.getMUID());
            System.out.println("Różnica przed zainstalowaniem ekranu: "+beforeDifference);
            
            afterDifference = this.nmmProj.getLeqLogAverage(this.eventsAfterRef, this.measAfterRef.getMUID())-
                    this.nmmProj.getLeqLogAverage(this.eventsAfterImi, this.measAfterImi.getMUID());                
            
            System.out.println("Punkt ref, po ekranie: "+this.nmmProj.getLeqLogAverage(this.eventsAfterRef, this.measAfterRef.getMUID()));
            System.out.println("Punkt imi, po ekranie: "+this.nmmProj.getLeqLogAverage(this.eventsAfterImi, this.measAfterImi.getMUID()));                                
            System.out.println("Różnica po zainstalowaniu ekranu: "+afterDifference);
            
            this.DILp=afterDifference-beforeDifference;
            System.out.println("skuteczność ekranu: "+this.DILp);
            //powiadamiamy słuchacy tego modelu, że model został przeliczony
            //i może być wymagane przeliczenie interfejsu obliczeniowego
            this.fireNoiseSourceModelChangedEvent(this);
        } catch (Exception e) {
            this.DILp=-1;
        }      
        return recalculatedSuccessfully;
    }


    
    @Override
    public void dispatchNMMNoiseSourceModelInputDataChanged(AbstractTableModel _mEvent) {
        
    }

    @Override
    public void addNoiseSourceModelChangedListener(NMMNoiseSourceModelChangedListener _l) {
        this.NMMNoiseSourceModelChangedListeners.add(_l);
    }

    private void fireNoiseSourceModelChangedEvent(NMMNoiseSourceModel _mEvent) {

        //jeżeli model źródła hałasu ma słuchaczy, to wyślij powiadomienia
        if (this.NMMNoiseSourceModelChangedListeners!=null) {
            Object[] listeners = NMMNoiseSourceModelChangedListeners.toArray(); 
            
            int numListeners = listeners.length;
            for (int i = 0; i<numListeners; i+=2) {
                if (listeners[i] instanceof NMMNoiseSourceModelChangedListener) {
                    ((NMMNoiseSourceModelChangedListener)listeners[i]).dispatchNMMNoiseSourceModelChanged(_mEvent);
                }
            }    
        }        
    }

    @Override
    public String toString() {
        return this.modelName;
    }

    @Override
    public UUID getMeasurementUUID() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setMeasurementBeforeReference(Object selectedItem) {
        this.measBeforeRef=(NMMMeasurement)selectedItem;
        this.recalculateModel();
    }

    public void setMeasurementBeforeImision(Object selectedItem) {
        this.measBeforeImi=(NMMMeasurement)selectedItem;
        this.recalculateModel();
    }

    public void setMeasurementAfterReference(Object selectedItem) {
        this.measAfterRef=(NMMMeasurement)selectedItem;
        this.recalculateModel();
    }

    public void setMeasurementAfterImision(Object selectedItem) {
        this.measAfterImi=(NMMMeasurement)selectedItem;
        this.recalculateModel();
    }

    public void setEventsBeforeReference(Object selectedItem) {
        this.eventsBeforeRef=(NMMEventType)selectedItem;
        this.recalculateModel();
    }

    public void setEventsBeforeImision(Object selectedItem) {
        this.eventsBeforeImi=(NMMEventType)selectedItem;
        this.recalculateModel();
    }

    public void setEventsAfterImision(Object selectedItem) {
        this.eventsAfterImi=(NMMEventType)selectedItem;
        this.recalculateModel();
    }

    public void setEventsAfterReference(Object selectedItem) {
        this.eventsAfterRef=(NMMEventType)selectedItem;
        this.recalculateModel();
    }

    @Override
    public void removeNoiseSourceModelChangedListener(NMMNoiseSourceModelChangedListener _l) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NMMCalculationReport getCalculationReport() {
        return this.calcReport;
    }

    @Override
    public boolean isComplete() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
