/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nmm2010;

import java.io.Serializable;

/**
 *
 * @author Jarek
 */
public class NMMCalculationReport implements Serializable {
    
    private static final long serialVersionUID=1L;
    
    String calculationReport = "";
    
    /**
     * Append _newText to calculation report
     * @param _newText - new text to be added to calculation report
     */
    public void addParagraph(String _newText) {
        
        this.calculationReport=this.calculationReport+_newText;        
    }
    
    /**
     * Save report as HTML file.
     */
    void saveAsHTMLPage () {
        //TODO:
    }
    
    public String toString() {
        return this.calculationReport;  
    }
    
    /**
     * Removes any text from calculation report.
     */
    public void purgeReport() {
        this.calculationReport="";
    }
    
}
