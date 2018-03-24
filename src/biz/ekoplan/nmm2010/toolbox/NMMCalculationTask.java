/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.toolbox;

import biz.ekoplan.nmm2010.surcemodel.NMMNoiseSourceModel;
import biz.ekoplan.nmm2010.surcemodel.NMMSimpleContinuousModel;
import javax.swing.SwingWorker;
import nmm2010.NMMCalculationReport;

/**
 *
 * @author User
 */
public class NMMCalculationTask extends SwingWorker {

    NMMNoiseSourceModel noiseSourceModel;
    
    public NMMCalculationTask(NMMNoiseSourceModel _nsm) {
        this.noiseSourceModel=_nsm;
    }

    public NMMCalculationTask(NMMSimpleContinuousModel aThis, NMMCalculationReport calcReport) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected Object doInBackground() throws Exception {
        this.noiseSourceModel.recalculateModel();
        Object res=null;
        return res;
    }
    
    /**
     *
     * @return
     */
    public int setProgress() {
        return 50;
    }
}
