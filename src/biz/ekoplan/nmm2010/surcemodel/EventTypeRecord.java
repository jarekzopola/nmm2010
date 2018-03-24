/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.surcemodel;

import java.io.Serializable;
import nmm2010.NMMEventType;

/**
 *
 * @author jarek
 */
public class EventTypeRecord implements Serializable {
    
    NMMEventType ets;
    int dayFrequency;
    int eveningFrequency;
    int nightFrequency;

    public EventTypeRecord() {
        this.dayFrequency=0;
        this.eveningFrequency=0;
        this.nightFrequency=0;
        ets=new NMMEventType();
        ets.setDescription("empty event");
    }

    /**
     * 
     * @param _et 
     */
    public void setEventType(NMMEventType _et) {
        this.ets=_et;
    }
    
    /**
     * 
     * @return 
     */
    public NMMEventType getEventType() {
        return this.ets;
    }
}
