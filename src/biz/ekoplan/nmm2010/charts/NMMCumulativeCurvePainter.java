/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.charts;

import biz.ekoplan.nmm2010.enums.AxisesTypes;
import biz.ekoplan.nmm2010.enums.NoiseModelResult;
import biz.ekoplan.nmm2010.surcemodel.NMMNoiseSourceModel;
import biz.ekoplan.nmm2010.surcemodel.result.NMMModelResultHistogram;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.util.Locale;
import nmm2010.NMMProject;

/**
 *
 * @author Jarek
 */
public class NMMCumulativeCurvePainter implements NMMChart{

    private NMMProject nmmProj;
    private NMMNoiseSourceModel nsm;
    boolean onlyEventsInMeasurement=true;
    private final PageFormat pf;
    private final Graphics2D g2;
    
    int viewportTopMargin=265;
    int viewportBottomMargin=395;
    int viewportLeftMargin=100;
    int viewportRightMargin=40;
    
    int verticalScaleMin=00;
    int verticalScaleMax=100;
    long viewPortStartTime=0;
    long viewPortEndTime=0;
    int chartPixWidth=0;
    Locale locale;

    /**
     * 
     * @param g2
     * @param _nmmp
     * @param _pf
     * @param _start
     * @param _stop
     * @param _measSel
     * @param _vpLeftMargin
     * @param _vpRightMargin
     * @param _vpTopMargin
     * @param _vpBottomMargin
     * @param _nsm
     * @param _onlyEventsInMeasurement - 
     * @param _drawEvents - indicate if events are to be drawn or not
     */
    public NMMCumulativeCurvePainter(Graphics2D _g2, NMMProject _nmmp, PageFormat _pf,
            long _start, long _stop, int _measSel, int _vpLeftMargin, 
            int _vpRightMargin, int _vpTopMargin, int _vpBottomMargin, 
            NMMNoiseSourceModel _nsm, boolean _onlyEventsInMeasurement, 
            boolean _drawEvents, Locale _locale) {

        this.g2=_g2;
        this.nmmProj=_nmmp;
        this.nsm=_nsm;
        this.pf=_pf;
                
        this.viewportBottomMargin=_vpBottomMargin;
        this.viewportLeftMargin=_vpLeftMargin;
        this.viewportRightMargin=_vpRightMargin;
        this.viewportTopMargin=_vpTopMargin;  
        this.locale=_locale;
        
        this.drawChart();
    }

    @Override
    public void drawChart() {
        
        float wys =(float)pf.getHeight()-(this.viewportBottomMargin
                +this.viewportTopMargin);   // wysokośc pola rysowania wykresu
        float szer =(float)pf.getWidth()-(this.viewportLeftMargin
                +this.viewportRightMargin);   // szerokośc pola rysowania wykresu
                                
        //Czyszczenie obszaru rysowania
        g2.setBackground(Color.WHITE);        

        //leave method if project doesn't exist
        if (nmmProj==null) {
            System.out.println("Brak projektu, nie rysuję wykresu!");
            return;
        }
        
        //rysowanie osi wykresu
        NMMChartAxises nmmca = new NMMChartAxises(g2, pf, this.viewportLeftMargin,
            this.viewportBottomMargin, this.viewportRightMargin, this.viewportTopMargin,
            this.verticalScaleMin, this.verticalScaleMax,
            5, 100, this.locale);
        nmmca.setType(AxisesTypes.CUMULATIVE_CURVE);
        nmmca.setXAxisDescription("Klasy");
        nmmca.setYAxisDescription("Krzwa skumulowana [%]");
        nmmca.drawAxises(); 
        
        //rysowanie krzywej gęstości skumulowanej        
        
        double wysh=0;
        
        
        double szerokoscKlasy=(pf.getWidth()-(this.viewportLeftMargin+this.viewportRightMargin))/140;
        NMMModelResultHistogram h = (NMMModelResultHistogram)this.nsm.getNoiseModelComplexResult(NoiseModelResult.LStats);
        for (int i=139; i>=0;i--) {            
            //współrzędne lewego górnego narożnika belki histogramu
            wysh=wysh+(h.getFrequency(i)*(wys/100));
            System.out.println("Wyshh="+wysh+" dla częstotliwoąsci = "+h.getFrequency(i)+" ["+i+"]");
            double x1=i*szerokoscKlasy+this.viewportLeftMargin;
            double y1=(this.pf.getHeight()-this.viewportBottomMargin-wysh);
            g2.setColor(this.nmmProj.getMeasurement(this.nsm.getMeasurementUUID()).getMeasurementColor());
            g2.drawRect((int)x1, (int)y1, (int)szerokoscKlasy, 
                    (int)wysh);        
        }
    }
}
