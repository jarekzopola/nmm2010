/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.toolbox;

import java.io.Serializable;

/**
 *
 * @author Jarek
 */
public class NMMPoint3D implements Serializable {
    
    //współrzędne rzeczywiste na poligonie pomiarowym (metry)
    double x;   //coordinate x
    double y;   //coordinate y
    double z;   //coordinate z

    //współrzędne na mapie lokalizacji punktów pomiarowych w 2D (piksele)
    int mapaX;
    int mapaY;
    
    String locSymbol;
    
    public NMMPoint3D (double _x, double _y, double _z, int _mapX, int _mapY) {
        
        this.x=_x;
        this.y=_y;
        this.z=_z;
        this.mapaX=_mapX;
        this.mapaY=_mapY;
        this.locSymbol="-";               
    }
    
    public int getMapX() {
        return this.mapaX;
    }

    public String getLocSymbol() {
        return locSymbol;
    }

    public void setLocSymbol(String locSymbol) {
        this.locSymbol = locSymbol;
    }
    
    public int getMapY() {
        return this.mapaY;
    }
                
    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }
    
    public double getX() {
        return this.x;
    }
    
    public void setMapaX(int _mapaX) {
        this.mapaX=_mapaX;
    }
    
    public void setMapaY(int _mapaY) {
        this.mapaX=_mapaY;
    }    
}
