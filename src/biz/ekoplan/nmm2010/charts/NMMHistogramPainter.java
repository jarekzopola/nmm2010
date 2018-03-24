/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.charts;

import biz.ekoplan.nmm2010.enums.AxisesTypes;
import biz.ekoplan.nmm2010.enums.NoiseModelResult;
import biz.ekoplan.nmm2010.surcemodel.NMMNoiseSourceModel;
import biz.ekoplan.nmm2010.surcemodel.result.NMMModelResultHistogram;
import biz.ekoplan.nmm2010.surcemodel.result.NMMNoiseSourceModelResult;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.util.Locale;
import nmm2010.NMMProject;

/**
 *
 * @author Jarek
 */
public class NMMHistogramPainter implements NMMChart{

    private NMMProject nmmProj;
    private NMMNoiseSourceModel nsm;
    boolean onlyEventsInMeasurement=true;
    private final PageFormat pf;
    private final Graphics2D g2;
    
    int viewportTopMargin=265;
    int viewportBottomMargin=395;
    int viewportLeftMargin=100;
    int viewportRightMargin=40;
    
    int verticalScaleMin=0;
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
    public NMMHistogramPainter(Graphics2D _g2, NMMProject _nmmp, PageFormat _pf,
            long _start, long _stop, int _measSel, int _vpLeftMargin, 
            int _vpRightMargin, int _vpTopMargin, int _vpBottomMargin, 
            NMMNoiseSourceModel _nsm, Locale _locale) {

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
                     
        //Czyszczenie obszaru rysowania
        //g2.setBackground(Color.WHITE);        

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
        nmmca.setXAxisDescription("Klasy");
        nmmca.setYAxisDescription("Udział [%]");
        nmmca.setType(AxisesTypes.HISTOGRAM);
        nmmca.drawAxises(); 
        
        //rysowanie histogramu
                
        //NMMNoiseSourceModelResult nsmr=this.nsm.getNoiseModelComplexResult(NoiseModelResult.LStats); 
        double szerokoscKlasy=(pf.getWidth()-(this.viewportLeftMargin+this.viewportRightMargin))/140d;
        System.out.println("Szerokość klasy: "+szerokoscKlasy);
        NMMModelResultHistogram h = (NMMModelResultHistogram)this.nsm.getNoiseModelComplexResult(NoiseModelResult.LStats);
        
        
        //ile piskli przypada na jednostkę częstotliwości pojawiania się
        //poziomu hałasu w danej klasie
        double wpw=pf.getHeight()-(this.viewportTopMargin+this.viewportBottomMargin);
        double ppf=wpw/100d;
        
        for (int i=0; i<140;i++) {                        
            //współrzędne lewego górnego narożnika belki histogramu                        
            //częstotliwość klasy
            double cwk = h.getFrequency(i);
            //wysokość uwzględniająca rozmiar viewportu
            double wysh=cwk*ppf;
            double x1=((double)i*szerokoscKlasy+(double)this.viewportLeftMargin);
            double y1=((double)this.pf.getHeight()-(double)this.viewportBottomMargin-wysh);                        
                                    
            System.out.println("Wysokość klasy ["+i+"]: "+wysh);
            System.out.println("X1: "+i*(int)szerokoscKlasy);
            System.out.println("Y1: "+(int)(this.pf.getHeight()
                    -this.viewportBottomMargin));
            
            g2.setColor(this.nmmProj.getMeasurement(this.nsm.getMeasurementUUID()).getMeasurementColor());
            g2.drawRect((int)x1, (int)(y1+0.5), (int)szerokoscKlasy, 
                    (int)(wysh));
            
        }                
    }
}
