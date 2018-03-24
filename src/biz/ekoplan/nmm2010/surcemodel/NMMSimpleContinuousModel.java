/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.surcemodel;

import biz.ekoplan.nmm2010.enums.NoiseLevelIndicators;
import biz.ekoplan.nmm2010.enums.NoiseModelResult;
import biz.ekoplan.nmm2010.enums.NoiseSourceModelType;
import biz.ekoplan.nmm2010.enums.RecordValueType;
import biz.ekoplan.nmm2010.enums.TimePeriods;
import biz.ekoplan.nmm2010.surcemodel.result.NMMModelResultHistogram;
import biz.ekoplan.nmm2010.surcemodel.result.NMMNoiseSourceModelResult;
import biz.ekoplan.nmm2010.surcemodel.sminterface.NMMSimpleContinousModelInterface;
import biz.ekoplan.nmm2010.toolbox.NMMCalculationTask;
import java.io.Serializable;
import java.util.UUID;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import nmm2010.NMMCalculationReport;
import nmm2010.NMMNoiseCalculator;
import nmm2010.NMMProject;

/**
 * Model II generacji.
 * 
 * W tym modelu ciągły fragment zapisu z miernika (NMMMeasurement) jest trakto-
 * wany jako jeden pomiar. Nie wyróżnia się tutaj zdarzeń o określonych typach
 * w czasie tego zapisu.
 * 
 * Model wylicza parametry LAeqD i LAeqN, najgorszą godzinę i najgorsze 8h, histogram. Moga one być pobrane
 * przez obiekt prezentacji.
 * 
 * W obliczeniach nie są uwzględniane fragmenty pomiaru które zostały wyłączone poleceniem "Exclude"
 * 
 * @author Jarosław Kowalczyk
 */
public class NMMSimpleContinuousModel implements NMMNoiseSourceModel, Serializable{
    
    static final long serialVersionUID =1L;
    
    final boolean DEBUG=false;
    public NMMProject nmmProject;
    private UUID muid;
    private boolean facadeCorrection;         
    String modelName="New simple continuous model";
    public double LAeqD=0;
    private double LAeqN=0;
    private double LAeqD_n8h=0;
    private double LAeqN_n1h=0;
    private boolean backgroundNoiseAsL95;
    boolean isRecalculated=false;
    private boolean calculate18=false;

    public boolean isCalculate18() {
        return calculate18;
    }

    public void setCalculate18(boolean calculate18) {
        this.calculate18 = calculate18;
    }

    public boolean isIsRecalculated() {
        return isRecalculated;
    }

    public void setIsRecalculated(boolean isRecalculated) {
        this.isRecalculated = isRecalculated;
    }
    private long startTime=0;
    private long endTime=0;
    private NMMModelResultHistogram mrh;

    public NMMModelResultHistogram getMrh() {
        return mrh;
    }

    public void setMrh(NMMModelResultHistogram mrh) {
        this.mrh = mrh;
    }
    private float uncertaintyD;

    public double getLAeqD_n8h() {
        return LAeqD_n8h;
    }

    public void setLAeqD_n8h(double LAeqD_n8h) {
        this.LAeqD_n8h = LAeqD_n8h;
    }

    public double getLAeqN_n1h() {
        return LAeqN_n1h;
    }

    public void setLAeqN_n1h(double LAeqN_n1h) {
        this.LAeqN_n1h = LAeqN_n1h;
    }

    public float getUncertaintyD() {
        return uncertaintyD;
    }

    public void setUncertaintyD(float uncertaintyD) {
        this.uncertaintyD = uncertaintyD;
    }

    public float getUncertaintyN() {
        return uncertaintyN;
    }

    public void setUncertaintyN(float uncertaintyN) {
        this.uncertaintyN = uncertaintyN;
    }
    private float uncertaintyN;
    private NMMCalculationReport calcReport;    
    private JProgressBar pb;
    
    @Override
    public String toString() {
        return getModelName();
    }
    
    public NMMProject getNMMProject() {
        return this.nmmProject;
    }
    
    public NMMSimpleContinuousModel() {       
    }
    
    public void addCalcReportParagraph(String _s) {
        this.calcReport.addParagraph(_s);
    }
    
    public void setLAeqD(double _laeqd) {
        this.LAeqD=_laeqd;
    }
    public void setLAeqN(double _laeqn) {
        this.LAeqN=_laeqn;
    }
    
    public NMMSimpleContinuousModel(String _modelName, NMMProject _nmmProject, UUID _muid,
            long _startTime, long _endTime) {
        
        this.calcReport=new NMMCalculationReport();
        this.modelName=_modelName;
        this.nmmProject = _nmmProject;
        this.muid=_muid;
        
        //te dwie poniższe właściwości nie są brane pod uwagę przy liczeniu
        //wartości LAeq a jedynie do wybrania odcinka pomiaru pokazywanego na
        //prezentacji
        this.startTime=_startTime;
        this.endTime=_endTime;
    }
    
    public boolean isRecalculated() {
        return this.isRecalculated;
    }
    
    public double getLAeqD() {
        return this.LAeqD;
    }
    
    public double getLAeqN() {
        return this.LAeqN;
    }
    
    public void setModelName(String _newModelName) {
        this.isRecalculated=false;
        if (_newModelName.equals("")) {
            this.modelName="Model name not set!";
        } else {
            this.modelName=_newModelName;
        }                
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
    }
    
    public void setBackgroundNoiseAsL95(boolean _b95) {
        this.backgroundNoiseAsL95 = _b95;
        this.isRecalculated=false;        
    }
    
    @Override
    public NoiseSourceModelType getNoiseModelType() {
        return NoiseSourceModelType.SIMPLE_CONTINOUS_MODEL;
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

    public void setMeasurementUUID(UUID _mUUID) {
        this.muid=_mUUID;
        this.isRecalculated=false;        
    }
    
    /**
     * Recalculates model an returns true if recalculation has succeeded
     * otherwise it returns false
     * 
     * @return boolean
     */
    public boolean startRecalcThread() {
        
        //I assume I'll succeed
        boolean succ=true;             
        //bedziemy liczyc model w osobnym wątku        
        
        
        return succ;
    }

    @Override
    public NMMCalculationReport getCalculationReport() {
                
        return this.calcReport;
    }

    @Override
    public double getNoiseModelResult(NoiseLevelIndicators _nli) {
        double res;     
        
        if (_nli==NoiseLevelIndicators.LAeqD) {
            res=this.getLAeqD();
        } else if (_nli==NoiseLevelIndicators.LAeqN) {
            res=this.getLAeqN();
        } else if (_nli==NoiseLevelIndicators.UA_D) {
            res=this.uncertaintyD;
        } else if (_nli==NoiseLevelIndicators.UA_N) {
            res=this.uncertaintyN;
        } else if (_nli==NoiseLevelIndicators.LAeqN_n1h) {
            res = this.LAeqN_n1h;
        } else if (_nli==NoiseLevelIndicators.LAeqD_n8h) {
            res = this.LAeqD_n8h;
        } else {
            res=-1;
        }
        return res;    
    }

    @Override
    public NMMNoiseSourceModelResult getNoiseModelComplexResult(NoiseModelResult _nli) {
        
        NMMNoiseSourceModelResult res;     
        
        if (_nli==NoiseModelResult.LStats) {
            res=this.mrh;
        } else if (_nli==NoiseModelResult.LStatsCumulated) {
            //TODO: Poniżej trzeba jeszcze uzupełnić, bo raczej nie chodzi o null
            res=null;
        } else {
            res=null;
        }
        return res;
    }

    @Override
    public void recreateListenersArray() {
        //ten nie musi tego implementować
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

    public void run() {
                               
        System.out.println("Zaczynam liczenie w nowym wątku ...");
        
        this.calcReport.addParagraph("Calculation report");
                
        this.LAeqD=this.nmmProject.getMeasurement(this.muid).getPeriodLeq(TimePeriods.DAY, true);
        this.LAeqN=this.nmmProject.getMeasurement(this.muid).getPeriodLeq(TimePeriods.NIGHT, true);         
        this.LAeqN_n1h=this.nmmProject.getMeasurement(this.muid).calculateWorstPeriodLAeq(3600, TimePeriods.NIGHT);
                
        
        this.LAeqD_n8h=this.nmmProject.getMeasurement(this.muid).calculateWorstPeriodLAeq(8*3600, TimePeriods.DAY);
                                
        this.calcReport.addParagraph("Noise imision (background noise + noise from source)");
        this.calcReport.addParagraph("Day hours equivalent noise level LAeqD = "+this.LAeqD);
        this.calcReport.addParagraph("Night hours equivalent noise level LAeqN = "+this.LAeqN);
                
        if (this.backgroundNoiseAsL95) {                        
            this.LAeqD=NMMNoiseCalculator.roznica_log(this.LAeqD, 
                    this.nmmProject.getMeasurement(this.muid).
                    getL95());
            this.LAeqN=NMMNoiseCalculator.roznica_log(this.LAeqN, 
                    this.nmmProject.getMeasurement(this.muid).
                    getL95());
            this.calcReport.addParagraph("Background noise level determined as L95 = "+this.nmmProject.getMeasurement(this.muid).
                    getL95());
        }        
        if (this.facadeCorrection) {
            this.calcReport.addParagraph("Fasade correction -3dB has been applied additionally.");
            this.LAeqD=this.LAeqD-3;
            this.LAeqN=this.LAeqN-3;
        } else {
            this.calcReport.addParagraph("Fasade correction hasn't been applied.");
        }
        
        //przeliczanie histogramu
        this.mrh=new NMMModelResultHistogram();
        for (int i=0; i<this.nmmProject.getMeasurement(this.muid).getMeasurementLength(); i++) {
            int range=(int)Math.floor(this.nmmProject.getMeasurement(this.muid).getRecord(i).getRecordValue(RecordValueType.LAeq));
            this.mrh.increaseFerquency(range);
        }
        this.mrh.normalizeTo100Percent();
       
        float[] leqs; 
        
        leqs=this.nmmProject.getLeqArray(TimePeriods.DAY, muid);
        
        if (leqs.length>0) {
            this.uncertaintyD=NMMNoiseCalculator.calculateRUncertainty(
                    (double)NMMNoiseCalculator.Lord95(leqs),
                    (double)this.nmmProject.getMeasurement(muid).getUncertaintyB());            
        } else {
            this.uncertaintyD=-1;
        }
                
        leqs=this.nmmProject.getLeqArray(TimePeriods.NIGHT, muid);
        if (leqs.length>0) {
            this.uncertaintyN=NMMNoiseCalculator.calculateRUncertainty(                   
                    (double)NMMNoiseCalculator.Lord95(leqs),
                    (double)this.nmmProject.getMeasurement(muid).getUncertaintyB());
        } else {
            this.uncertaintyN=-1;
        }
        
        this.calcReport.addParagraph("Uncertainty for day period is "+this.uncertaintyD+" and "
                + "for night period is: "+this.uncertaintyN);

        this.calcReport.addParagraph("Final results:");
        this.calcReport.addParagraph("LAeqD = "+this.LAeqD+"±"+this.uncertaintyD);
        this.calcReport.addParagraph("LAeqN = "+this.LAeqN+"±"+this.uncertaintyN);                
        this.isRecalculated=true;        
        System.out.println("Zakończyłem obliczanie modelu ...");                
    }

    @Override
    public boolean recalculateModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
