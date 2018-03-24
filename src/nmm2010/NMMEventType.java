/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nmm2010;

import java.awt.Color;
import java.io.Serializable;

/**
 *
 * @author Jarek
 */
public class NMMEventType implements Comparable<NMMEventType>, Serializable {

    private Color color = Color.BLACK;
    private String  type="";
    private String  description="";

    public NMMEventType(String _type) {
        type=_type;
        this.description="noname";
        this.color=Color.BLACK;
    }
    
    public NMMEventType() {
        
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color _color) {
        this.color=_color;
    }

    public void setDescription(String _description) {
        this.description=_description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setType(String _type) {
        this.type=_type;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return type;
    }

    public int compareTo(NMMEventType o) {
        return this.toString().compareTo(o.toString());
    }

    public boolean compareToBoolean(NMMEventType o) {

        boolean toSamo=false;

        if (this.toString().compareTo(o.toString())==0) {
            toSamo=true;
        }
        return toSamo;
    }
}
