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
public class NMMMeasurementPointData implements Serializable {
    
    private String id;
    private double CoordX;
    private double CoordY;
    private double CoordZ;
    private NMMEventType Lbg;
    private NMMEventType Ls;

    public NMMMeasurementPointData(String _id) {
        this.CoordX=-1;
        this.CoordY=-1;
        this.CoordZ=-1;
        this.id=_id;
        this.Lbg=null;
        this.Ls=null;
    }
      
    public void setCoordX(Double _CoordX) {
        this.CoordX=_CoordX;
    }
    
    public void setCoordY(Double _CoordY) {
        this.CoordY=_CoordY;
    }
        
    public void setCoordZ(Double _CoordZ) {
        this.CoordZ=_CoordZ;
    }
    
    public void setLbg(NMMEventType _lbg) {
        this.Lbg=_lbg;
    }
    
    public void setLs(NMMEventType _ls) {
        this.Ls=_ls;
    }

    public double getCoordX() {
        return this.CoordX;
    }
    
    public double getCoordY() {
        return this.CoordY;
    }
        
    public double getCoordZ() {
        return this.CoordZ;
    }
    
    public NMMEventType getLbg() {
        return this.Lbg;
    }
    
    public NMMEventType getLs() {
        return this.Ls;
    }
    
    @Override
    public String toString() {
        return "P "+this.id;
    }

    void setPointID(String _pointID) {
        this.id=_pointID;
    }

    public String getPointID() {
        return  this.id;
    }
}
