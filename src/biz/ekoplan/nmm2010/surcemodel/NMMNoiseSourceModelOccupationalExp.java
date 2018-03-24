/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.surcemodel;

import biz.ekoplan.nmm2010.enums.NoiseLevelIndicators;
import biz.ekoplan.nmm2010.enums.NoiseModelResult;
import biz.ekoplan.nmm2010.enums.NoiseSourceModelType;
import biz.ekoplan.nmm2010.surcemodel.result.NMMModelResultHistogram;
import biz.ekoplan.nmm2010.surcemodel.result.NMMNoiseSourceModelResult;
import java.io.Serializable;
import java.util.UUID;
import nmm2010.NMMCalculationReport;
import nmm2010.NMMProject;

/**
 *
 * @author Jarek
 */
public class NMMNoiseSourceModelOccupationalExp implements NMMNoiseSourceModel, Serializable {

    static final long serialVersionUID =1L;
    
    final boolean DEBUG=false;
    
    NMMProject nmmProject;
    private UUID muid;    
    String modelName="New occupational exposure model";
    boolean isRecalculated=false;
    private long startTime=0;
    private long endTime=0;
    private NMMModelResultHistogram mrh;
    private NMMCalculationReport calcReport;
    
    public NMMNoiseSourceModelOccupationalExp(String _modelName, NMMProject _nmmProject, UUID _muid, long _viewportStartTime, long _viewportEndTime) {
        
        this.modelName=_modelName;
        this.nmmProject = _nmmProject;
        this.muid=_muid;
        this.startTime=_viewportStartTime;
        this.endTime=_viewportEndTime;
    }

    @Override
    public void recreateListenersArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NoiseSourceModelType getNoiseModelType() {
        return NoiseSourceModelType.OCCUPATIONAL_NOISE_ACTIVITIES;
    }

    @Override
    public String getModelName() {
        return this.getModelName();
    }

    @Override
    public void setModelName(String text) {
        this.modelName=text;
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
    public UUID getMeasurementUUID() {
        return this.muid;
    }

    @Override
    public double getNoiseModelResult(NoiseLevelIndicators _nli) {
        return 0;
    }

    @Override
    public NMMNoiseSourceModelResult getNoiseModelComplexResult(NoiseModelResult _nli) {
        return null;
    }

    @Override
    public NMMCalculationReport getCalculationReport() {
        return this.calcReport;
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

    @Override
    public boolean recalculateModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
