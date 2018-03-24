/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nmm2010;

import biz.ekoplan.nmm2010.events.NMMEventChangedEvent;
import biz.ekoplan.nmm2010.events.NMMEventChangedListener;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author samsung
 */
public class NMMEvent implements Serializable {

    private String description="just another event";
    private NMMEventType eventType;
    private long start=0;
    private long end=0;
    private Color color;
    private boolean enabled = true;
    private transient ArrayList<NMMEventChangedListener> NMMEventChangedListeners; 

    public NMMEvent(String _description, long _start, long _end, NMMEventType _eventType, Color _col) {
        description=_description;
        start=_start;
        end=_end;
        eventType=_eventType;
        color=_col;
    }
    
    public NMMEvent() {
        description="Generic event";
        start=0;
        end=0;
        eventType=null;
        color=new Color(0,0,0);
    }
    
    public String toString() {
        return this.getDescription();        
    }

    public synchronized void addEventChangedListener(NMMEventChangedListener l)
    {
        
        if (this.NMMEventChangedListeners==null) {
            NMMEventChangedListeners = new ArrayList<NMMEventChangedListener>();
        }
        if (!NMMEventChangedListeners.contains(l)) {
            NMMEventChangedListeners.add(l);
            System.out.println("Liczba nasłuchiwaczy zmian w zdarzeniu: "+this.NMMEventChangedListeners.size());
        }
    }
    
    public synchronized void removeEventChangedListener(NMMEventChangedListener l) {
        NMMEventChangedListeners.remove(l);
    }
    
    protected void fireNMMEventChangedEvent(NMMEventChangedEvent _mEvent) {
       Object[] listeners = NMMEventChangedListeners.toArray();        
        int numListeners = listeners.length;
        for (int i = 0; i<numListeners; i+=2) {
          if (listeners[i] instanceof NMMEventChangedListener) {
               ((NMMEventChangedListener)listeners[i]).dispatchNMMEventChangedEvent(_mEvent);
          }
        }
    }
    
    
    
    /**
     * Returns number of miliseconds in selection, or -1 if selection is not set
     * @return long
     */
    public long getMilisLength() {
        long rl = 0;
        if (this.isSet()) {
            rl = this.getEnd()-this.getStart();
        } else {
            rl=-1;
        }
        return rl;
    }

    public long getStart() {
        return this.start;
    }

    /**
     * Returns end of event.
     * @return
     */
    public long getEnd() {
        return this.end;
    }

    public void setStart(long _start) {
        this.start=_start;
    }

    public void setEnd(long _end) {
        this.end = _end;
    }

    public boolean isSet() {
        boolean tn=false;
        if (this.start!=this.end) {
            tn=true;
        }
        return tn;
    }

    /**
     * Set event description (description is not an event type, so use it to
     * store specific information on this event.
     * @param _eventDescription
     */
    public void setDescription(String _eventDescription) {
        this.description=_eventDescription;
        this.fireNMMEventChangedEvent(new NMMEventChangedEvent(this));
    }

    /**
     * Get event description (description is not an event type, so this is
     * just pure comment
     * @return String
     */
    public String getDescription() {
        return this.description;
    }
    
    public NMMEventType getEventType() {
        return this.eventType;
    }
    
    public void setEventType(NMMEventType _et) {
        this.eventType=_et;
    }

    public Color getColor() {
        return this.color;
    }
    
    public void setColor( Color _cl) {
        this.color=_cl;
        this.fireNMMEventChangedEvent(new NMMEventChangedEvent(this));
    }
    
    public void setEnabled(boolean _enabled) {
        this.enabled=_enabled;
        this.fireNMMEventChangedEvent(new NMMEventChangedEvent(this));
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
}
