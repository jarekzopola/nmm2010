/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nmm2010;

import biz.ekoplan.nmm2010.enums.NoiseLevelIndicators;
import biz.ekoplan.nmm2010.enums.RecordValueType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author samsung
 */
public class MeasurementRecord implements Serializable {
    
    private static final long serialVersionUID=1L;
        
    HashMap recordValues;
    private boolean isExcluded;
       
    public MeasurementRecord() {        
        recordValues = new HashMap();
        this.isExcluded=false;
    }
    
    public MeasurementRecord(RecordValueType _i, double _val) {        
        recordValues.put(_i, _val);        
    }    

    public void setRecordValue(RecordValueType _i, double _val) {
        if (this.recordValues.get(_i)==null) {
            recordValues.put(_i, _val);
        } else {
            this.recordValues.replace(_i, _val);
        }        
    }
    
    public double getRecordValue(RecordValueType _i) {
        double d = (double)this.recordValues.get(_i);
        return d;
    }
    
    public Set getMetrics() {        
        return this.recordValues.keySet();                
    }
    
    public boolean isExcluded() {
        return this.isExcluded;
    }
    
    public void setExcluded(boolean _isExcluded) {
        this.isExcluded=_isExcluded;
    }
}
