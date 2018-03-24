/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.measurement;

/**
 *
 * @author jarek
 */
public class NMMResult {
    
    private double result=0;
    private double uncertainty=0;
    
    public NMMResult(double _result, double _uncertainty) {
        this.result=_result;
        this.uncertainty=_uncertainty;
    }
    
    public void setResult(double _result) {
        this.result=_result;
    }
    
    public void setUncertainty(double _uncertainty) {
        this.uncertainty=_uncertainty;
    }
    
    public double getResult() {
        return this.result;
    }
    
    public double getUncertainty() {
        return this.uncertainty;
    }
}
