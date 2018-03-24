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
import biz.ekoplan.nmm2010.toolbox.NMMToolbox;
import biz.ekoplan.nmm2010.toolbox.TimeConverter;
import java.util.Locale;
import java.util.UUID;

/**
 *
 * @author samsung
 */
public class NMMEventsTablePainter {

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

     public NMMEventsTablePainter(Graphics _g, PageFormat _pf, int _vpLeftMargin,
            int _vpBottomMargin, int _vpRightMargin, int _vpTopMargin, 
            NMMProject _nmmProj, Locale _locale) {
        this.viewportLeftMargin=_vpLeftMargin;
        this.viewportRightMargin=_vpRightMargin;
        this.viewportBottomMargin=_vpBottomMargin;
        this.viewportTopMargin=_vpTopMargin;
        this.g=(Graphics2D)_g;
        this.pf=_pf;
        this.nmmProject=_nmmProj;
        this.locale=_locale;
    }

    public void drawDuplicatedTable(String[][] tableContent,
            String[] tableHeaders, double[] tableColsSizes) {

        // width and height of whole page
        double viewportWidth=pf.getWidth();
        double viewportHeight=pf.getHeight();

        final int headerHeight=15;

        // height and width of table area
        double wys = viewportHeight-(this.viewportBottomMargin
               +this.viewportTopMargin);
        double szer =viewportWidth-(this.viewportRightMargin+
               this.viewportLeftMargin);
        Stroke stroke = new BasicStroke(0.5f);
        g.setStroke(stroke);
        this.g.drawRect(this.viewportLeftMargin,this.viewportTopMargin,
               (int)szer, (int)wys);
        Stroke stroke2 = new BasicStroke(0.2f);
        g.setStroke(stroke2);

        //tabel header
        this.g.drawRect(this.viewportLeftMargin,this.viewportTopMargin-headerHeight,
               (int)szer, (int)headerHeight);

        // maximum number of rows in table based on assumption 12 points is
        // needed to keep one line of text
        int maxRows = (int)(wys/12);
        int duplications=tableContent.length/maxRows;
        if ((szer%duplications)!=0) {
          duplications++;
        }
        double duplicationWidth=szer/duplications;
        double summedWidth=0;
        double dupOffset;
        //draw wertical lines
        for (int d=0; d<duplications;d++) {
            dupOffset=0;
            for (int c=0; c<tableContent[0].length;c++) {
                int offset=this.viewportLeftMargin+(int)dupOffset+
                        (int)(d*duplicationWidth)+
                        (int)((duplicationWidth*tableColsSizes[c]));
                g.drawLine(offset, this.viewportTopMargin-headerHeight,
                         offset, this.viewportTopMargin+(int)wys);
                dupOffset=dupOffset+duplicationWidth*tableColsSizes[c];
                g.drawString(tableHeaders[c], offset-(int)(duplicationWidth*tableColsSizes[c])+5, 
                        this.viewportTopMargin-headerHeight+10);
                // fill column
                int maxCell=maxRows+(d*maxRows);
                if (maxCell>tableContent.length) {
                    maxCell=tableContent.length;
                }
                for (int cell=d*maxRows; cell<maxCell; cell++) {
                    g.drawString(tableContent[cell][c],
                            (int)(offset-(duplicationWidth*tableColsSizes[c])+5),
                            10+this.viewportTopMargin+(cell-(d*maxRows))*12);
                }
            }
        }
        // draw horizontal lines
        for (int r=0; r<maxRows; r++) {
            g.drawLine(this.viewportLeftMargin, this.viewportTopMargin+r*12,
                    this.viewportLeftMargin+(int)szer, this.viewportTopMargin+r*12);
        }
    }

    public void drawTable() {

            // width and height of whole page
            double viewportWidth=pf.getWidth();
            double viewportHeight=pf.getHeight();

            // height and width of chart area (without titles and labels on
            // axes
            double wys = viewportHeight-(this.viewportBottomMargin
                    +this.viewportTopMargin);
            double szer =viewportWidth-(this.viewportRightMargin+
                    this.viewportLeftMargin);

            this.g.drawRect(this.viewportLeftMargin,this.viewportTopMargin,
                    (int)szer, (int)wys);

            int wysTekstu =g.getFontMetrics().getHeight()+2;

            // rysowanie wierszy tabeli (wiersze poziome)

            // liczba wierszy powiekszona o jeden dla podsumowania (średnie)
            int ileWierszy=this.nmmProject.getEventsNumber()+1;
            float[] wartosci = new float[ileWierszy-1];
            double wysWiersza=wys/(ileWierszy+1);
            for (int i=1; i<=ileWierszy; i++) {
                //linia rozdzielająca wiersze w tabeli
                g.drawLine(this.viewportLeftMargin,
                        this.viewportTopMargin+(int)(i*wysWiersza),
                        (int)(viewportWidth-this.viewportRightMargin),
                        this.viewportTopMargin+(int)(i*wysWiersza));
                if (i<ileWierszy) {
                    // rysowanie koloru eventu
                    g.setColor(this.nmmProject.getEvent(i-1).getColor());
                    g.fillRect(this.viewportLeftMargin+3,
                            this.viewportTopMargin+(int)(i*wysWiersza)+5, 5, 5);
                    g.setColor(Color.BLACK);

                    // typ eventu
                    g.drawString(this.nmmProject.getEvent(i-1).getEventType().toString(),
                            this.viewportLeftMargin+10,
                            this.viewportTopMargin+(int)(i*wysWiersza)+10);

                    // wpisanie początku eventu
                    String evStart=TimeConverter.LongToTimeString(
                            this.nmmProject.getEvent(i-1).getStart(),
                            DateFormat.SHORT, this.locale);
                    g.drawString(evStart, this.viewportLeftMargin+110,
                            this.viewportTopMargin+(int)(i*wysWiersza)+10);

                    //wpisanie końca eventu
                    String evStop=TimeConverter.LongToTimeString(
                            this.nmmProject.getEvent(i-1).getEnd(),
                            DateFormat.SHORT, this.locale);
                    g.drawString(evStop, this.viewportLeftMargin+160,
                            this.viewportTopMargin+(int)(i*wysWiersza)+10);

                    //wpisanie wartości eventu
                    //zapamietanie wartosci eventu do obliczenia sredniej
                    double evDoubleValue=this.nmmProject.getCurrentMeasurement()
                            .getLeq(this.nmmProject.getEvent(i-1).getStart(),
                            this.nmmProject.getEvent(i-1).getEnd());
                    String evValue=NMMToolbox.formatDouble(evDoubleValue);
                    g.drawString(evValue, this.viewportLeftMargin+200,
                            this.viewportTopMargin+(int)(i*wysWiersza)+10);
                    wartosci[i-1]=(float)evDoubleValue;
                }
            }

            // wpisanie wartości średniej i niepewnosci pomiaru
            g.drawString("LAeq,śr = "+NMMToolbox.formatDouble(NMMNoiseCalculator.
                    SredniaLog(wartosci))+", UA= "+
                    NMMToolbox.formatDouble(NMMNoiseCalculator.Lord95(wartosci)),
                    this.viewportLeftMargin+10, 
                    this.viewportTopMargin+(int)wys-(int)wysTekstu/2-(int)(wysWiersza-wysTekstu)/2);
            System.out.println("Wys. wiersz = "+wysWiersza+" wysokość tekstu ="+wysTekstu);

            // pionowe przegrody tabeli                        
            int threeThird=this.viewportLeftMargin+3*(int)(szer/4);
            g.drawLine(this.viewportLeftMargin+105, 
                    this.viewportTopMargin,
                    this.viewportLeftMargin+105,
                    (int)viewportHeight-this.viewportBottomMargin-(int)wysWiersza);
            g.drawLine(this.viewportLeftMargin+155, 
                    this.viewportTopMargin,
                    this.viewportLeftMargin+155,
                    (int)viewportHeight-this.viewportBottomMargin-(int)wysWiersza);
            g.drawLine(this.viewportLeftMargin+195,
                    this.viewportTopMargin,
                    this.viewportLeftMargin+195,
                    (int)viewportHeight-this.viewportBottomMargin-(int)wysWiersza);

            // nagłówki tabeli
            g.drawString("Zdarzenie", this.viewportLeftMargin+10, this.viewportTopMargin+10);
            g.drawString("Początek", this.viewportLeftMargin+110, this.viewportTopMargin+10);
            g.drawString("Koniec", this.viewportLeftMargin+160, this.viewportTopMargin+10);
            g.drawString("LAeq", this.viewportLeftMargin+200, this.viewportTopMargin+10);
    }


    /**
     * 
     * @param _nmmEventTypes
     * @param _MUID 
     */
    public void drawSelectiveTable(NMMEventType[] _nmmEventTypes, UUID _MUID) {

            // width and height of whole page
            double viewportWidth=pf.getWidth();
            double viewportHeight=pf.getHeight();

            // height and width of chart area (without titles and labels on
            // axes
            double wys = viewportHeight-(this.viewportBottomMargin
                    +this.viewportTopMargin);
            double szer =viewportWidth-(this.viewportRightMargin+
                    this.viewportLeftMargin);

            this.g.drawRect(this.viewportLeftMargin,this.viewportTopMargin,
                    (int)szer, (int)wys);

            int wysTekstu =g.getFontMetrics().getHeight()+2;

            // rysowanie wierszy tabeli (wiersze poziome)

            // liczba typów zdarzeń jakie mają ostatecznie znaleźć się w tabeli zdarzeń
            int eventTypesNumber=_nmmEventTypes.length;

            // liczba wierszy powiekszona o jeden dla podsumowania (średnie)
            int ileWierszy=1;
            for (int i=0; i<eventTypesNumber;i++) {
                ileWierszy=ileWierszy+this.nmmProject.getEventsNumber(_nmmEventTypes[i]);
            }
            System.out.println("Liczba typów zdarzeń: "+eventTypesNumber);
            System.out.println("Liczba wyselekcjonowanych zdarzeń: "+ileWierszy);

            float[] wartosci = new float[ileWierszy-1];
            double wysWiersza=wys/(ileWierszy+1);
            int i=0;
            for (int ktz=0; ktz<_nmmEventTypes.length;ktz++) {
                System.out.println("Rozpoczynam pisywanie w tabelę zdarzeń typu: "+_nmmEventTypes[ktz]);
                int lzdt=this.nmmProject.getEventsNumber(_nmmEventTypes[ktz]);       //liczba zdarzeń określonego typu
                for (int kz=0; kz<this.nmmProject.getEventsNumber(); kz++) {
                    System.out.println("Sprawdzam typ zdarzenia nr: "+kz);
                    System.out.println("Sprawdzam: "+this.nmmProject.getEvent(kz).getEventType().getType()+""+_nmmEventTypes[ktz].getType());
                    if (this.nmmProject.getEvent(kz).getEventType().getType().equals(_nmmEventTypes[ktz].getType())) {
                        //linia rozdzielająca wiersze w tabeli
                        i++;
                        System.out.println("Typ zdarzenia się zgadza");
                        g.drawLine(this.viewportLeftMargin,
                            this.viewportTopMargin+(int)((i)*wysWiersza),
                            (int)(viewportWidth-this.viewportRightMargin),
                            this.viewportTopMargin+(int)((i)*wysWiersza));
                        if (i<ileWierszy) {
                            // rysowanie koloru eventu
                            g.setColor(this.nmmProject.getEvent(kz).getColor());
                            g.fillRect(this.viewportLeftMargin+3,
                                    this.viewportTopMargin+(int)(i*wysWiersza)+5, 5, 5);
                            g.setColor(Color.BLACK);

                            // typ eventu
                            g.drawString(this.nmmProject.getEvent(kz).getEventType().toString(),
                                    this.viewportLeftMargin+10,
                                    this.viewportTopMargin+(int)(i*wysWiersza)+10);

                            // wpisanie początku eventu
                            String evStart=TimeConverter.LongToTimeString(
                                    this.nmmProject.getEvent(kz).getStart(),
                                    DateFormat.SHORT, this.locale);
                            g.drawString(evStart, this.viewportLeftMargin+110,
                                    this.viewportTopMargin+(int)(i*wysWiersza)+10);

                            //wpisanie końca eventu
                            String evStop=TimeConverter.LongToTimeString(
                                    this.nmmProject.getEvent(kz).getEnd(),
                                    DateFormat.SHORT, this.locale);
                            g.drawString(evStop, this.viewportLeftMargin+160,
                                    this.viewportTopMargin+(int)(i*wysWiersza)+10);

                            //wpisanie wartości eventu
                            //zapamietanie wartosci eventu do obliczenia sredniej
                            double evDoubleValue=this.nmmProject.getCurrentMeasurement()
                                    .getLeq(this.nmmProject.getEvent(kz).getStart(),
                                    this.nmmProject.getEvent(kz).getEnd());
                            String evValue=NMMToolbox.formatDouble(evDoubleValue);
                            g.drawString(evValue, this.viewportLeftMargin+200,
                                    this.viewportTopMargin+(int)(i*wysWiersza)+10);
                            wartosci[i-1]=(float)evDoubleValue;
                        }
                    }
                }
            }           
            // wrysowanie linii rozdzielającej venty od linii podsumowania
            g.drawLine(this.viewportLeftMargin,
                            this.viewportTopMargin+(int)((i+1)*wysWiersza),
                            (int)(viewportWidth-this.viewportRightMargin),
                            this.viewportTopMargin+(int)((i+1)*wysWiersza));
            // wpisanie wartości średniej i niepewnosci pomiaru
            g.drawString("LAeq,śr = "+NMMToolbox.formatDouble(NMMNoiseCalculator.
                    SredniaLog(wartosci))+", UA= "+
                    NMMToolbox.formatDouble(NMMNoiseCalculator.Lord95(wartosci)),
                    this.viewportLeftMargin+10,
                    this.viewportTopMargin+(int)wys-(int)wysTekstu/2-(int)(wysWiersza-wysTekstu)/2);
            System.out.println("Wys. wiersz = "+wysWiersza+" wysokość tekstu ="+wysTekstu);

            // pionowe przegrody tabeli
            int threeThird=this.viewportLeftMargin+3*(int)(szer/4);
            g.drawLine(this.viewportLeftMargin+105,
                    this.viewportTopMargin,
                    this.viewportLeftMargin+105,
                    (int)viewportHeight-this.viewportBottomMargin-(int)wysWiersza);
            g.drawLine(this.viewportLeftMargin+155,
                    this.viewportTopMargin,
                    this.viewportLeftMargin+155,
                    (int)viewportHeight-this.viewportBottomMargin-(int)wysWiersza);
            g.drawLine(this.viewportLeftMargin+195,
                    this.viewportTopMargin,
                    this.viewportLeftMargin+195,
                    (int)viewportHeight-this.viewportBottomMargin-(int)wysWiersza);

            // nagłówki tabeli
            g.drawString("Zdarzenie", this.viewportLeftMargin+10, this.viewportTopMargin+10);
            g.drawString("Początek", this.viewportLeftMargin+110, this.viewportTopMargin+10);
            g.drawString("Koniec", this.viewportLeftMargin+160, this.viewportTopMargin+10);
            g.drawString("LAeq", this.viewportLeftMargin+200, this.viewportTopMargin+10);
    }
    
    public void drawSelectiveTable(NMMEventType _nmmEventTypes, UUID _MUID) {

            // width and height of whole page
            double viewportWidth=pf.getWidth();
            double viewportHeight=pf.getHeight();

            // height and width of chart area (without titles and labels on
            // axes
            double wys = viewportHeight-(this.viewportBottomMargin
                    +this.viewportTopMargin);
            double szer =viewportWidth-(this.viewportRightMargin+
                    this.viewportLeftMargin);

            this.g.drawRect(this.viewportLeftMargin,this.viewportTopMargin,
                    (int)szer, (int)wys);

            int wysTekstu =g.getFontMetrics().getHeight()+2;

            // rysowanie wierszy tabeli (wiersze poziome)            

            // liczba wierszy            
            int ileWierszy;            
            ileWierszy=this.nmmProject.getEventsNumber(_nmmEventTypes, _MUID);
          
            float[] wartosci = new float[ileWierszy];
            double wysWiersza=wys/(ileWierszy+2);
            int i=0;
                            
            int lzdt=this.nmmProject.getEventsNumber(_nmmEventTypes, _MUID);       //liczba zdarzeń określonego typu
            for (int kz=0; kz<this.nmmProject.getEventsNumber(); kz++) {                                
                if ((this.nmmProject.getEvent(kz).getEventType().getType().equals(_nmmEventTypes.getType())) &&
                        (this.nmmProject.getMeasurement(_MUID).isWithinMeasurement(this.nmmProject.getEvent(kz)))) {
                    //linia rozdzielająca wiersze w tabeli
                    i++;
                    System.out.println("Typ zdarzenia się zgadza");
                    g.drawLine(this.viewportLeftMargin,
                        this.viewportTopMargin+(int)((i)*wysWiersza),
                        (int)(viewportWidth-this.viewportRightMargin),
                        this.viewportTopMargin+(int)((i)*wysWiersza));
                    if (i<=ileWierszy) {
                        // rysowanie koloru eventu
                        g.setColor(this.nmmProject.getEvent(kz).getColor());
                        g.fillRect(this.viewportLeftMargin+3,
                                this.viewportTopMargin+(int)(i*wysWiersza)+5, 5, 5);
                        g.setColor(Color.BLACK);

                        // typ eventu
                        g.drawString(this.nmmProject.getEvent(kz).getEventType().toString(),
                                this.viewportLeftMargin+10,
                                this.viewportTopMargin+(int)(i*wysWiersza)+10);

                        // wpisanie początku eventu
                        String evStart=TimeConverter.LongToTimeString(
                                this.nmmProject.getEvent(kz).getStart(),
                                DateFormat.MEDIUM, this.locale);
                        g.drawString(evStart, this.viewportLeftMargin+110,
                                this.viewportTopMargin+(int)(i*wysWiersza)+10);

                        //wpisanie końca eventu
                        String evStop=TimeConverter.LongToTimeString(
                                this.nmmProject.getEvent(kz).getEnd(),
                                DateFormat.MEDIUM, this.locale);
                        g.drawString(evStop, this.viewportLeftMargin+160,
                                this.viewportTopMargin+(int)(i*wysWiersza)+10);

                        //wpisanie wartości eventu
                        //zapamietanie wartosci eventu do obliczenia sredniej
                        double evDoubleValue=this.nmmProject.getCurrentMeasurement()
                                .getLeq(this.nmmProject.getEvent(kz).getStart(),
                                this.nmmProject.getEvent(kz).getEnd());
                        String evValue=NMMToolbox.formatDouble(evDoubleValue);
                        g.drawString(evValue, this.viewportLeftMargin+200,
                                this.viewportTopMargin+(int)(i*wysWiersza)+10);
                        wartosci[i-1]=(float)evDoubleValue;
                    }
                }
            }
             
            // wrysowanie linii rozdzielającej eventy od linii podsumowania
            g.drawLine(this.viewportLeftMargin,
                            this.viewportTopMargin+(int)((i+1)*wysWiersza),
                            (int)(viewportWidth-this.viewportRightMargin),
                            this.viewportTopMargin+(int)((i+1)*wysWiersza));
            // wpisanie wartości średniej i niepewnosci pomiaru
            g.drawString("LAeq,śr = "+NMMToolbox.formatDouble(NMMNoiseCalculator.
                    SredniaLog(wartosci))+", UA= "+
                    NMMToolbox.formatDouble(NMMNoiseCalculator.Lord95(wartosci)),
                    this.viewportLeftMargin+10,
                    this.viewportTopMargin+(int)wys-(int)wysTekstu/2-(int)(wysWiersza-wysTekstu)/2);
            System.out.println("Wys. wiersz = "+wysWiersza+" wysokość tekstu ="+wysTekstu);

            // pionowe przegrody tabeli
            int threeThird=this.viewportLeftMargin+3*(int)(szer/4);
            g.drawLine(this.viewportLeftMargin+105,
                    this.viewportTopMargin,
                    this.viewportLeftMargin+105,
                    (int)viewportHeight-this.viewportBottomMargin-(int)wysWiersza);
            g.drawLine(this.viewportLeftMargin+155,
                    this.viewportTopMargin,
                    this.viewportLeftMargin+155,
                    (int)viewportHeight-this.viewportBottomMargin-(int)wysWiersza);
            g.drawLine(this.viewportLeftMargin+195,
                    this.viewportTopMargin,
                    this.viewportLeftMargin+195,
                    (int)viewportHeight-this.viewportBottomMargin-(int)wysWiersza);

            // nagłówki tabeli
            g.drawString("Zdarzenie", this.viewportLeftMargin+10, this.viewportTopMargin+10);
            g.drawString("Początek", this.viewportLeftMargin+110, this.viewportTopMargin+10);
            g.drawString("Koniec", this.viewportLeftMargin+160, this.viewportTopMargin+10);
            g.drawString("LAeq", this.viewportLeftMargin+200, this.viewportTopMargin+10);
    }


    public void drawSamplingResultsTable(NMMIndustrialModel nmmim) {

        // width and height of whole page
        double viewportWidth=pf.getWidth();
        double viewportHeight=pf.getHeight();

        // height and width of chart area (without titles and labels on
        // axes
        double wys = viewportHeight-(this.viewportBottomMargin
                +this.viewportTopMargin);
        double szer =viewportWidth-(this.viewportRightMargin+
                this.viewportLeftMargin);

        this.g.drawRect(this.viewportLeftMargin,this.viewportTopMargin,
                (int)szer, (int)wys);

        // rysowanie wierszy tabeli (wiersze poziome)

        // liczba wierszy powiekszona o jeden dla podsumowania (średnie)
        int ileWierszy=this.nmmProject.getEventTypes().length;
        float[] wartosci = new float[ileWierszy-1];
        double wysWiersza=wys/(ileWierszy);
        for (int i=1; i<ileWierszy; i++) {
            //linia rozdzielająca wiersze w tabeli
            g.drawLine(this.viewportLeftMargin,
                    this.viewportTopMargin+(int)(i*wysWiersza),
                    (int)(viewportWidth-this.viewportRightMargin),
                    this.viewportTopMargin+(int)(i*wysWiersza));
            if (i<ileWierszy) {
                // rysowanie koloru typu eventu
                NMMEventType tmpET=(NMMEventType)this.nmmProject.getEventTypes()[i-1];
                g.setColor(tmpET.getColor());
                g.fillRect(this.viewportLeftMargin+3,
                        this.viewportTopMargin+(int)(i*wysWiersza)+5, 5, 5);
                g.setColor(Color.BLACK);

                // typ eventu
                g.drawString(tmpET.getType(),
                        this.viewportLeftMargin+10,
                        this.viewportTopMargin+(int)(i*wysWiersza)+10);

                //wpisanie wartości ŚREDNIEJ dla typów zdarzeń
                double evDoubleValue=nmmim.getLAek_zm_sr(i-1);
                String evValue=NMMToolbox.formatDouble(evDoubleValue);
                g.drawString(evValue, this.viewportLeftMargin+115,
                        this.viewportTopMargin+(int)(i*wysWiersza)+10);
                //wpisanie ROZSTĘPU
                evDoubleValue=nmmim.getR(i-1);
                evValue=NMMToolbox.formatDouble(evDoubleValue);
                g.drawString(evValue, this.viewportLeftMargin+150,
                        this.viewportTopMargin+(int)(i*wysWiersza)+10);
                //wpisanie UA95
                evDoubleValue=nmmim.getUA95_zm(i-1);
                evValue=NMMToolbox.formatDouble(evDoubleValue);
                g.drawString(evValue, this.viewportLeftMargin+180,
                        this.viewportTopMargin+(int)(i*wysWiersza)+10);
                //wpisanie MIN
                evDoubleValue=nmmim.getLAeq_zm_min(i-1);
                evValue=NMMToolbox.formatDouble(evDoubleValue);
                g.drawString(evValue, this.viewportLeftMargin+210,
                        this.viewportTopMargin+(int)(i*wysWiersza)+10);
                //wpisanie MAX
                evDoubleValue=nmmim.getLAeq_zm_max(i-1);
                evValue=NMMToolbox.formatDouble(evDoubleValue);
                g.drawString(evValue, this.viewportLeftMargin+240,
                        this.viewportTopMargin+(int)(i*wysWiersza)+10);
                //wpisanie Imisji
                evDoubleValue=nmmim.getLAeq_im(i-1);
                evValue=NMMToolbox.formatDouble(evDoubleValue);
                g.drawString(evValue, this.viewportLeftMargin+270,
                        this.viewportTopMargin+(int)(i*wysWiersza)+10);

                //wpisanie ImisjiT
                evDoubleValue=nmmim.getLAeq_im_T(i-1, 0);                
                if (!Double.isInfinite(evDoubleValue)) {
                    evValue=NMMToolbox.formatDouble(evDoubleValue);
                } else {
                    evValue="-";
                }                
                double evDoubleValue2=nmmim.getLAeq_im_T(i-1, 2);
                String evValue2=null;
                if (!Double.isInfinite(evDoubleValue2)) {
                    evValue2=NMMToolbox.formatDouble(evDoubleValue2);
                } else {
                    evValue2="-";
                }

                g.drawString(evValue+" / "+evValue2, this.viewportLeftMargin+300,
                        this.viewportTopMargin+(int)(i*wysWiersza)+10);

                //wpisanie U95T
                evDoubleValue=nmmim.getUA95_im_T(i-1,0);
                evValue=NMMToolbox.formatDouble(evDoubleValue);
                evDoubleValue2=nmmim.getUA95_im_T(i-1,2);
                evValue2=NMMToolbox.formatDouble(evDoubleValue2);
                g.drawString(evValue+" / "+evValue2, this.viewportLeftMargin+360,
                        this.viewportTopMargin+(int)(i*wysWiersza)+10);
            }
        }

        // pionowe przegrody tabeli
        int oneNineth=this.viewportLeftMargin+3*(int)(szer/4);
        g.drawLine(this.viewportLeftMargin+105,
                this.viewportTopMargin,
                this.viewportLeftMargin+105,
                (int)viewportHeight-this.viewportBottomMargin);
        for (int s=0;s<6; s++) {
            g.drawLine(this.viewportLeftMargin+145+(30*s),
                this.viewportTopMargin,
                this.viewportLeftMargin+145+(30*s),
                (int)viewportHeight-this.viewportBottomMargin);    
        }
        g.drawLine(this.viewportLeftMargin+355,
                this.viewportTopMargin,
                this.viewportLeftMargin+355,
                (int)viewportHeight-this.viewportBottomMargin);

        // nagłówki tabeli
        g.drawString("Zdarzenie", this.viewportLeftMargin+10, this.viewportTopMargin+10);
        g.drawString("Średnia", this.viewportLeftMargin+110, this.viewportTopMargin+10);
        g.drawString("R", this.viewportLeftMargin+150, this.viewportTopMargin+10);
        g.drawString("UA95", this.viewportLeftMargin+180, this.viewportTopMargin+10);
        g.drawString("Min", this.viewportLeftMargin+210, this.viewportTopMargin+10);
        g.drawString("Max", this.viewportLeftMargin+240, this.viewportTopMargin+10);
        g.drawString("Imisja", this.viewportLeftMargin+270, this.viewportTopMargin+10);
        g.drawString("Imisja(D/N)", this.viewportLeftMargin+300, this.viewportTopMargin+10);
        g.drawString("UA95T(D/N)", this.viewportLeftMargin+360, this.viewportTopMargin+10);
    }
}
