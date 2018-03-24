/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.print.PageFormat;
import java.text.DateFormat;
import nmm2010.NMMEventType;
import nmm2010.NMMNoiseCalculator;
import nmm2010.NMMProject;
import biz.ekoplan.nmm2010.surcemodel.NMMIndustrialModel;
import biz.ekoplan.nmm2010.surcemodel.NMMRoadSamplingModel;
import biz.ekoplan.nmm2010.toolbox.NMMToolbox;
import biz.ekoplan.nmm2010.toolbox.TimeConverter;
import java.util.Locale;
import java.util.UUID;

/**
 *
 * @author samsung
 */
public class NMMRoadNoiseClassificationTable {

    private int viewportBottomMargin;
    private int viewportTopMargin;
    private int viewportLeftMargin;
    private int viewportRightMargin;
    private int verticalScaleMaxDb;
    private int verticalScaleMinDb;
    private Graphics2D g;
    private PageFormat pf;
    private NMMProject nmmProject;
    private String[] columnHeaders=null;
    private Locale locale;
    private NMMRoadSamplingModel rsm;

     public NMMRoadNoiseClassificationTable(Graphics _g, PageFormat _pf, int _vpLeftMargin,
            int _vpBottomMargin, int _vpRightMargin, int _vpTopMargin, 
            NMMProject _nmmProj, NMMRoadSamplingModel _rsm, Locale _locale) {
        this.viewportLeftMargin=_vpLeftMargin;
        this.viewportRightMargin=_vpRightMargin;
        this.viewportBottomMargin=_vpBottomMargin;
        this.viewportTopMargin=_vpTopMargin;
        this.g=(Graphics2D)_g;
        this.pf=_pf;
        this.nmmProject=_nmmProj;
        this.locale=_locale;
        this.rsm=_rsm;
    }    

    public void drawRoadNoiseClassificationTable() {

            // width and height of whole page
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

            int wysTekstu =g.getFontMetrics().getHeight()+2;

            // rysowanie wierszy tabeli (wiersze poziome)

            // liczba wierszy
            int ileWierszy=24;            
            double wysWiersza=wys/(ileWierszy+1);
            int godz1=0;
            int godz2=0;
            double laeq=0;
            for (int i=1; i<=ileWierszy; i++) {
                //linia rozdzielająca wiersze w tabeli
                g.drawLine(this.viewportLeftMargin,
                        this.viewportTopMargin+(int)(i*wysWiersza),
                        (int)(viewportWidth-this.viewportRightMargin),
                        this.viewportTopMargin+(int)(i*wysWiersza));
                if (i<=ileWierszy) {
                    // rysowanie koloru eventu
                    NMMEventType et = (NMMEventType)this.rsm.getValueAt(i-1, 1);
                    System.out.println("Typ zdarzenia: "+et.getType()+" a kolor: "+et.getColor().toString());
                    this.g.setColor(et.getColor());                    
                    this.g.fillRect(this.viewportLeftMargin+65,
                            this.viewportTopMargin+(int)(i*wysWiersza)+8, 6, 6);
                    this.g.setColor(Color.BLACK);

                    // typ eventu
                    this.g.drawString(et.getType(),
                            this.viewportLeftMargin+75,
                            this.viewportTopMargin+(int)(i*wysWiersza)+13);
                    
                    // wpisanie godzinę w pierwszą kolumnę
                    if (i<=18) {
                        godz1=i+5;
                        godz2=i+6;
                    } else {
                        godz1=i+5-24;
                        godz2=i+6-24;
                    }                         
                    this.g.drawString(godz1 + " - "+godz2, this.viewportLeftMargin+5,this.viewportTopMargin+(int)(i*wysWiersza)+12);
                    
                    // printing equivalent noise level fo each hour
                    laeq=this.nmmProject.getMeasurement(this.rsm.getMeasurementUUID()).getLeq(this.nmmProject.getEvents(et));
                    this.g.drawString(NMMToolbox.formatDouble(laeq), this.viewportLeftMargin+163,this.viewportTopMargin+(int)(i*wysWiersza)+12);                    
                }
            }           

            // pionowe przegrody tabeli  
            g.setColor(Color.BLACK);
            //po godzinie doby
            g.drawLine(this.viewportLeftMargin+60, 
                    this.viewportTopMargin,
                    this.viewportLeftMargin+60,
                    (int)viewportHeight-this.viewportBottomMargin);
            //po przydzielonej klasie
            g.drawLine(this.viewportLeftMargin+155, 
                    this.viewportTopMargin,
                    this.viewportLeftMargin+155,
                    (int)viewportHeight-this.viewportBottomMargin);
            
            // nagłówki tabeli
            g.drawString("Godzina", this.viewportLeftMargin+5, this.viewportTopMargin+13);
            g.drawString("Klasyfikacja", this.viewportLeftMargin+65, this.viewportTopMargin+13);            
            g.drawString("LAeq", this.viewportLeftMargin+160, this.viewportTopMargin+13);
    }    
}
