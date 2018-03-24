/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.surcemodel;

import biz.ekoplan.nmm2010.enums.NoiseLevelIndicators;
import biz.ekoplan.nmm2010.enums.NoiseModelResult;
import biz.ekoplan.nmm2010.enums.NoiseSourceModelType;
import biz.ekoplan.nmm2010.surcemodel.result.NMMNoiseSourceModelResult;
import java.io.Serializable;
import java.util.UUID;
import nmm2010.NMMCalculationReport;
import nmm2010.NMMEventType;
import nmm2010.NMMNoiseCalculator;
import nmm2010.NMMProject;

/**
 * Model II generacji
 * @author jarek
 */
public class NMMSimpleModel implements NMMNoiseSourceModel, Serializable {
    
    static final long serialVersionUID =1L;
    
    final boolean DEBUG=false;
    NMMProject nmmProject;
    private UUID muid;
    private boolean facadeCorrection;         
    String modelName="New simple model";
    private double LAeq;    
    private double uncertainty=0;
    private boolean backgroundNoiseAsL95;
    boolean isRecalculated=false;
    private long startTime=0;
    private long endTime=0;
    private NMMEventType et=null;
    private NMMCalculationReport report;
    
    @Override
    public String toString() {
        return getModelName();
    }
    
    public NMMSimpleModel() {       
    }
    
    public NMMSimpleModel(String _modelName, NMMProject _nmmProject, UUID _muid,
            long _startTime, long _endTime) {
        this.modelName=_modelName;
        this.nmmProject = _nmmProject;
        this.muid=_muid;
        this.startTime=_startTime;
        this.endTime=_endTime;
        this.report=new NMMCalculationReport();
    }
    
    public boolean isRecalculated() {
        return this.isRecalculated;
    }
    
    public double getLAeq() {
        return this.LAeq;
    }
    
    public void setModelName(String _newModelName) {
        this.modelName=_newModelName;
    }
    
    @Override
    public String getModelName() {
        return this.modelName;
    }
    
    public boolean getFacadeCorrection() {
        return this.facadeCorrection;
    }
    
    public boolean getBackgroundNoiseAsL95() {
        return this.backgroundNoiseAsL95;
    }
    
    public void setFacadeCorrection(boolean _fc) {
        this.facadeCorrection = _fc;
        this.isRecalculated=false;
        this.recalculateModel();        
    }
    
    public void setBackgroundNoiseAsL95(boolean _b95) {
        this.backgroundNoiseAsL95 = _b95;
        this.isRecalculated=false;
        this.recalculateModel();
    }
    
    @Override
    public NoiseSourceModelType getNoiseModelType() {
        return NoiseSourceModelType.SIMPLE_EBASED_MEASUREMENT;
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public long getEndTime() {
        return this.endTime;
    }

    public void setEventType(NMMEventType selectedItem) {
        this.et=selectedItem;
        this.isRecalculated=false;
        this.recalculateModel();
    }
    
    public NMMEventType getEventType() {
        return this.et;
    }

    @Override
    public UUID getMeasurementUUID() {
        return this.muid;
    }

    public void setMeasurementUUID(UUID _mUUID) {
        this.muid=_mUUID;
        this.isRecalculated=false;
        this.recalculateModel();
    }
    
    /**
     * Recalculates model an returns true if recalculation has succeeded
     * otherwise it returns false
     * 
     * @return boolean
     */
    @Override
    public boolean recalculateModel() {
        
        //I assume I'll succeed
        boolean succ=true;
        //conditions that must be fulfille in order to recalculate model
        if (this.et!=null) {
            this.LAeq=this.nmmProject.getLeqLogAverage(this.et, 
                this.muid);            
            this.uncertainty=NMMNoiseCalculator.calculateRUncertainty(
                    (double)NMMNoiseCalculator.Lord95(this.nmmProject.getLeqArray(et, muid)),
                    (double)this.nmmProject.getMeasurement(muid).getUncertaintyB());
            if (this.LAeq==0) {
                succ=false;            
            } else {
                this.isRecalculated=true;
            }
            if (this.facadeCorrection) {
                this.LAeq=this.LAeq-3;
            }
            if (this.backgroundNoiseAsL95) {            
                this.LAeq=NMMNoiseCalculator.roznica_log(this.LAeq, 
                        this.nmmProject.getMeasurement(this.muid).
                        getL95());
            }
            System.out.println(">"+this.report);
            report.addParagraph("<H1>Simple events based noise level calculation report</H1>"+
                    "<p>Event type:"+this.et.getDescription()+"</p>"+
                    "<p>Measurement:"+this.nmmProject.getMeasurement(muid).getDescription()+"</p>"+
                    "<p>LAeq = "+this.LAeq+" ... to be continued ...");
        } else {
//            report.concat("Event type isn't specifier. Calculations terminated.");
        }
        this.isRecalculated=true;
        return succ;
    }

    @Override
    public NMMCalculationReport getCalculationReport() {
        
        //Report is prepared in recalculateModel() method.
        return this.report;
    }

    public double getUncertainty() {
        return this.uncertainty;
    }

    @Override
    public double getNoiseModelResult(NoiseLevelIndicators _nli) {
        
        double res=-1;        
        if (_nli==NoiseLevelIndicators.LAeq) {
            res=this.getLAeq();        
        } else {
            res=-1;
        }
        return res;
    }

    @Override
    public NMMNoiseSourceModelResult getNoiseModelComplexResult(NoiseModelResult _nli) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void recreateListenersArray() {
        //ten model nie musi chyba tego implementować
    }

    @Override
    public void addNoiseSourceModelChangedListener(NMMNoiseSourceModelChangedListener _l) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeNoiseSourceModelChangedListener(NMMNoiseSourceModelChangedListener _l) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isComplete() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



}
