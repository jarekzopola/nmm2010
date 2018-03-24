/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nmm2010;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JScrollBar;

/**
 *
 * @author Jarek
 */
public class TimeSelector extends JScrollBar implements Observer {

    public TimeSelector() {
        
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.red);
        //g.drawString(String.valueOf(this.getMaximum())+" value:"+this.getValue(), 100, 10);
    }

    public void update(Observable o, Object arg) {
        NMMProject o2;
        o2=(NMMProject)o;
        this.setMinimum(0);
        this.setMaximum(o2.getProjectRecordsSpan());        
    }

}
