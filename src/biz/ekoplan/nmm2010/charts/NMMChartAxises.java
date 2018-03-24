/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.charts;

import biz.ekoplan.nmm2010.enums.AxisesTypes;
import biz.ekoplan.nmm2010.toolbox.NMMToolbox;
import biz.ekoplan.nmm2010.toolbox.TimeConverter;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.text.DateFormat;
import java.util.Locale;

/**
 *
 * @author Jarek
 */
public class NMMChartAxises {
    
    int viewportBottomMargin;
    int viewportTopMargin;
    int viewportLeftMargin;
    int viewportRightMargin;
    int verticalScaleMaxDb;
    int verticalScaleMinDb;
    long firstTimeToDraw;
    long lastTimeToDraw;
    String yAxisDescription="Poziom hałasu A [dB]";
    String xAxisDescription="Godzina [gg:mm:ss]";
    AxisesTypes axisesTypes=AxisesTypes.TIME_HISTORY;
    Locale locale;

    Graphics2D g;
    PageFormat pf;
    
    public NMMChartAxises(Graphics _g, PageFormat _pf, int _vpLeftMargin, 
            int _vpBottomMargin, int _vpRightMargin, int _vpTopMargin,
            int _vScaleMinDb, int _vScaleMaxDb,
            long _firstTimeToDraw, long _lastTimeToDraw, Locale _locale) {
        this.viewportLeftMargin=_vpLeftMargin;
        this.viewportRightMargin=_vpRightMargin;
        this.viewportBottomMargin=_vpBottomMargin;
        this.viewportTopMargin=_vpTopMargin;
        this.g=(Graphics2D)_g;
        this.pf=_pf;
        this.verticalScaleMaxDb=_vScaleMaxDb;
        this.verticalScaleMinDb=_vScaleMinDb;
        this.firstTimeToDraw=_firstTimeToDraw;
        this.lastTimeToDraw=_lastTimeToDraw;
        this.locale=_locale;
    }

    public void setXAxisDescription(String _descrption) {
        this.xAxisDescription=_descrption;
    }
    
    public void setYAxisDescription(String _descrption) {
        this.yAxisDescription=_descrption;
    }
    
    public void drawAxises() {

            // width and height of whole page, or viewport
            double viewportWidth=pf.getWidth();
            double viewportHeight=pf.getHeight();

            // height and width of chart area (without titles and labels on
            // axes
            double wys = viewportHeight-(this.viewportBottomMargin
                    +this.viewportTopMargin);
            double szer =viewportWidth-(this.viewportRightMargin+
                    this.viewportLeftMargin);

            this.g.setColor(Color.BLACK);
            this.g.drawRect(this.viewportLeftMargin,this.viewportTopMargin,
                    (int)szer, (int)wys);
                        
            //oś pozioma            
            int descriptionXCoord = this.viewportLeftMargin+((int)szer-
                    g.getFontMetrics().stringWidth(xAxisDescription))/2;
            g.setColor(Color.BLACK);
            g.drawString(xAxisDescription, descriptionXCoord,
                    (int)(this.viewportTopMargin+wys+18));

            //oś pionowa - opis osi
            int translateY=((this.viewportTopMargin+(int)wys/2)
                    +g.getFontMetrics().stringWidth(yAxisDescription)/2);
            g.translate(this.viewportLeftMargin-10,translateY);
            g.rotate(-Math.PI/2);

            g.drawString(yAxisDescription, 0,-20);
            
            g.rotate(Math.PI/2);
            g.translate(-(this.viewportLeftMargin-10),-translateY);

            // oś pionowa - wartości
            int liczbaPrzedzialow=(this.verticalScaleMaxDb-this.verticalScaleMinDb)/10;
            for (int i=0; i<liczbaPrzedzialow+1;i++) {
                double ety=this.verticalScaleMinDb+i*10;
                String etykieta = NMMToolbox.formatDouble(ety);
                float igrek=(float)(this.viewportTopMargin+wys-(i*((wys)/liczbaPrzedzialow)));
                g.drawString(etykieta,this.viewportLeftMargin-26, igrek);
                if (i>0) {
                    g.setColor(Color.GRAY);
                    g.drawLine(this.viewportLeftMargin, (int)igrek,
                        (int)(this.viewportLeftMargin+szer), (int)igrek);
                    g.setColor(Color.BLACK);
                }
            }

            
            //rysowanie klas dla histogramu
            if (this.axisesTypes==AxisesTypes.HISTOGRAM || 
                    this.axisesTypes==AxisesTypes.CUMULATIVE_CURVE) {                
                double horizontalSpacing=szer/140;                
                for (int kg=1; kg<=14; kg++) {
                    int ofset=(int)(this.viewportLeftMargin+10*kg*horizontalSpacing);
                    String label = Integer.toString(10*kg);                
                    g.drawString(label,
                            (int)((ofset-g.getFontMetrics().stringWidth(label)/2)-horizontalSpacing)
                            ,(int)(viewportHeight-this.viewportBottomMargin)+10);
                }
            }
            
            //rysowanie godzin
            if (this.axisesTypes==AxisesTypes.TIME_HISTORY) {
                long timeSpan=this.lastTimeToDraw-this.firstTimeToDraw;
                double horizontalSpacing=szer/8;
                long lo=timeSpan/8;
                for (int kg=0; kg<=8; kg++) {
                    int ofset=(int)(this.viewportLeftMargin+kg*horizontalSpacing);
                    String label = TimeConverter.LongToTimeString(this.firstTimeToDraw+lo*kg, 
                            DateFormat.MEDIUM, locale);                
                    g.drawString(label,
                            (int)(ofset-g.getFontMetrics().stringWidth(label)/2)
                            ,(int)(viewportHeight-this.viewportBottomMargin)+10);
                }
            }
    }

    void setType(AxisesTypes _axisesType) {
        this.axisesTypes=_axisesType;
    }
}
