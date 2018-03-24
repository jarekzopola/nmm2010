/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.surcemodel;

import biz.ekoplan.nmm2010.enums.RecordValueType;
import biz.ekoplan.nmm2010.enums.TimePeriods;
import biz.ekoplan.nmm2010.surcemodel.result.NMMModelResultHistogram;
import biz.ekoplan.nmm2010.surcemodel.sminterface.NMMSimpleContinousModelInterface;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import nmm2010.NMMNoiseCalculator;

/**
 *
 * @author JarekK
 */
public class NMMSimpleContinousModelThreadCalc extends SwingWorker <NMMSimpleContinuousModel, Void> {

    NMMSimpleContinuousModel calcModel;
    NMMSimpleContinousModelInterface vInterface;
    
    public NMMSimpleContinousModelThreadCalc(NMMSimpleContinuousModel _calcModel, NMMSimpleContinousModelInterface _interface) {
        calcModel=_calcModel;
        vInterface=_interface;
    }
    
    @Override
    protected NMMSimpleContinuousModel doInBackground() throws Exception {
        
        System.out.println("Zaczynam liczenie w nowym wątku ...");
        
        this.setProgress(10);        
        
        calcModel.addCalcReportParagraph("Calculation report");
                
        calcModel.setLAeqD(calcModel.getNMMProject().getMeasurement(calcModel.getMeasurementUUID()).getPeriodLeq(TimePeriods.DAY, true));
        calcModel.setLAeqN(calcModel.getNMMProject().getMeasurement(calcModel.getMeasurementUUID()).getPeriodLeq(TimePeriods.NIGHT, true));
        
        if (!this.isCancelled() && calcModel.isCalculate18()) {
            calcModel.setLAeqN_n1h(calcModel.getNMMProject().getMeasurement(calcModel.getMeasurementUUID()).calculateWorstPeriodLAeq(3600, TimePeriods.NIGHT));  
        } else {
            System.out.println("Obliczenia przerwane...");
        }
        this.setProgress(20);
        
        if (!this.isCancelled() && calcModel.isCalculate18()) {
            calcModel.setLAeqD_n8h(calcModel.getNMMProject().getMeasurement(calcModel.getMeasurementUUID()).calculateWorstPeriodLAeq(8*3600, TimePeriods.DAY));
        } else {
            System.out.println("Obliczenia przerwane...");
        }
        this.setProgress(90);
                        
        calcModel.addCalcReportParagraph("Noise imision (background noise + noise from source)");
        calcModel.addCalcReportParagraph("Day hours equivalent noise level LAeqD = "+calcModel.getLAeqD());
        calcModel.addCalcReportParagraph("Night hours equivalent noise level LAeqN = "+calcModel.getLAeqN());
                
        if (calcModel.getBackgroundNoiseAsL95()) {                        
            calcModel.setLAeqD(NMMNoiseCalculator.roznica_log(calcModel.getLAeqD(), 
                    calcModel.getNMMProject().getMeasurement(calcModel.getMeasurementUUID()).
                    getL95()));
            calcModel.setLAeqN(NMMNoiseCalculator.roznica_log(calcModel.getLAeqN(), 
                    calcModel.getNMMProject().getMeasurement(calcModel.getMeasurementUUID()).
                    getL95()));
            calcModel.addCalcReportParagraph("Background noise level determined as L95 = "+calcModel.getNMMProject().getMeasurement(calcModel.getMeasurementUUID()).
                    getL95());
        }        
        if (calcModel.getFacadeCorrection()) {
            calcModel.addCalcReportParagraph("Fasade correction -3dB has been applied additionally.");
            calcModel.setLAeqD(calcModel.getLAeqD()-3);
            calcModel.setLAeqN(calcModel.getLAeqN()-3);
        } else {
            calcModel.addCalcReportParagraph("Fasade correction hasn't been applied.");
        }
        
        //przeliczanie histogramu
        calcModel.setMrh(new NMMModelResultHistogram());
        for (int i=0; i<calcModel.getNMMProject().getMeasurement(calcModel.getMeasurementUUID()).getMeasurementLength(); i++) {
            int range=(int)Math.floor(calcModel.getNMMProject().getMeasurement(calcModel.getMeasurementUUID()).getRecord(i).getRecordValue(RecordValueType.LAeq));
            calcModel.getMrh().increaseFerquency(range);
        }
        calcModel.getMrh().normalizeTo100Percent();
       
        float[] leqs; 
        
        leqs=calcModel.getNMMProject().getLeqArray(TimePeriods.DAY, calcModel.getMeasurementUUID());
        
        if (leqs.length>0) {
            calcModel.setUncertaintyD(NMMNoiseCalculator.calculateRUncertainty(
                    (double)NMMNoiseCalculator.Lord95(leqs),
                    (double)calcModel.getNMMProject().getMeasurement(calcModel.getMeasurementUUID()).getUncertaintyB()));            
        } else {
            calcModel.setUncertaintyD(-1);
        }
                
        leqs=calcModel.getNMMProject().getLeqArray(TimePeriods.NIGHT, calcModel.getMeasurementUUID());
        if (leqs.length>0) {
            calcModel.setUncertaintyN(NMMNoiseCalculator.calculateRUncertainty(                   
                    (double)NMMNoiseCalculator.Lord95(leqs),
                    (double)calcModel.getNMMProject().getMeasurement(calcModel.getMeasurementUUID()).getUncertaintyB()));
        } else {
            calcModel.setUncertaintyN(-1);
        }
        
        calcModel.addCalcReportParagraph("Uncertainty for day period is "+calcModel.getUncertaintyD()+" and "
                + "for night period is: "+calcModel.getUncertaintyN());

        calcModel.addCalcReportParagraph("Final results:");
        calcModel.addCalcReportParagraph("LAeqD = "+calcModel.getLAeqD()+"±"+calcModel.getUncertaintyD());
        calcModel.addCalcReportParagraph("LAeqN = "+calcModel.getLAeqN()+"±"+calcModel.getUncertaintyN());                        
        
        System.out.println("Zakończyłem obliczanie modelu ...");        
        
        calcModel.setIsRecalculated(true);
        
        return calcModel;
    }
    
     @Override
        public void done(){
            this.vInterface.updateInterface();
        }
    
}
