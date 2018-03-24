/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.reports;

import biz.ekoplan.nmm2010.surcemodel.NMMNoiseSourceModel;
import biz.ekoplan.nmm2010.surcemodel.NMMSoundPowerLevelModel;
import biz.ekoplan.nmm2010.toolbox.NMMToolbox;
import biz.ekoplan.nmm2010.toolbox.TimeConverter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import static java.util.Objects.isNull;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import nmm2010.NMMSetup;
import nmm2010.Setup;

/**
 *
 * @author Jarek
 */
public class NMMReportWriter {
    
    NMMNoiseSourceModel calcModel;
    Setup nmmSetup;
    
    public NMMReportWriter(Setup _setup, NMMNoiseSourceModel _nsm) {
        this.nmmSetup=_setup;
        this.calcModel=_nsm;
    }

    /**
     * Set calculation model which will provide report from calculation process
     * @param calculationModel 
     */
    public void setModel(NMMSoundPowerLevelModel _calculationModel) {
        if (!isNull(_calculationModel)) {
            this.calcModel = _calculationModel;
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }        
    }

    /**
     * Write report to disc in required format
     */
    public void writeReport() {
        
        JFileChooser fc = new JFileChooser(this.nmmSetup.getProperty("NMM_SETUP_PROJECT_PATH", null));
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "HTML files", "html", "htm");
        fc.setFileFilter(filter);
        int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {             
            FileWriter fr = null;
            try {
                fr = new FileWriter(fc.getSelectedFile());
                BufferedWriter br = new BufferedWriter(fr);
                br.write(this.calcModel.getCalculationReport().toString());                        
                br.flush();
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(NMMReportWriter.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fr.close();
                } catch (IOException ex) {
                    Logger.getLogger(NMMReportWriter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
        }                      
    }
    
}
