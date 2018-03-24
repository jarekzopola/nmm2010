/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.charts;

import biz.ekoplan.nmm2010.measurement.NMMMeasurement;
import biz.ekoplan.nmm2010.surcemodel.NMMNoiseSourceModel;
import biz.ekoplan.nmm2010.toolbox.NMMToolbox;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import nmm2010.NMMEvent;
import nmm2010.NMMProject;

/**
 *
 * @author Jarek
 */
public class NMMChartPainter {

    int verticalScaleMinDb=20;
    int verticalScaleMaxDb=100;
    long viewPortStartTime=0;
    long viewPortEndTime=0;
    int chartPixWidth=0;

    int viewportTopMargin=265;
    int viewportBottomMargin=395;
    int viewportLeftMargin=100;
    int viewportRightMargin=40;

    NMMProject nmmProj;
    int measurementSlector;
    NMMNoiseSourceModel nsm;
    boolean onlyEventsInMeasurement=true;
    private boolean drawEvents;
    Locale locale;

    public void setVerticalScale(int _lower, int _upper) {

        boolean change = true;
        if (_lower>=_upper) { change=false; }
        if (_lower<0) { change=false; }
        if (_upper>160) { change=false; }

        if (change) {
            this.verticalScaleMinDb=_lower;
            this.verticalScaleMaxDb=_upper;
        }
    }

    /**
      * Ile pikseli przypada na jeden rekord danych
      * @return
      */
    public float getPixNumberOnDataRecord() {
        float pnodr;
        pnodr=(float)chartPixWidth/(float)getNumberOfRecordsInViewport();
        return pnodr;
    }

    /**
    * ile rekordów jest pokazanych na wykresie
    * @return
    */
    public int getNumberOfRecordsInViewport() {
        int numberOfRecordsToDraw=(int)((this.viewPortEndTime-this.viewPortStartTime)/this.nmmProj.getProjectTimeResolution());
        return numberOfRecordsToDraw;
    }

    /**
     * Zwraca wspólrzędną X na wykresie czasu podanego metodzie jako parametr
     * @param time
     * @return  long, x-coord
     */
    private long getTimeXCoord(long time) {
        long xcoord;
        long timeDif=time-this.viewPortStartTime;
        // obliczamy liczbę rekordów
        long drn=timeDif/this.nmmProj.getProjectTimeResolution();
        //System.out.print("Liczba rekordów:"+drn);
        //obliczamy pikselową szerokość rekordów
        float pw = (float)drn*this.getPixNumberOnDataRecord();
        //System.out.println("    pix on data ="+this.getPixNumberOnDataRecord());
        //obliczamy wspólrzedną X czasu na wykresie
        xcoord=this.viewportLeftMargin+(long)pw;
        return xcoord;
    }

    /**
     * Returns time (as long) for x coordinate in chart window
     * @param xcoord - x coordinate on chart
     * @return
     */
    public long getXCoordTime(int xcoord, int _szer) {

        long localTime=-1;
        double chartLWidth = this.viewPortEndTime-this.viewPortStartTime;
        double milisecOnPixel = (chartLWidth)/(double)(_szer-(this.viewportLeftMargin+this.viewportRightMargin));
        long sek = (long) (milisecOnPixel * (xcoord - this.viewportLeftMargin));
        /*System.out.println("Szerokość okna w sekundach:"+chartLWidth/1000+
                " Szerokosc okna w pikselach: "+(this.getWidth()-(this.viewportLeftMargin+this.viewportRightMargin))+
                " Pozycja:"+(xcoord-this.viewportLeftMargin)+
                " Sekunda:"+sek+
                " Milisekund na piksel: "+milisecOnPixel);
         */
        if (xcoord>this.viewportLeftMargin) {
            localTime=(this.viewPortStartTime+(sek));
        } else if (xcoord==this.viewportLeftMargin) {
            localTime=this.viewPortStartTime;
        } else {
            localTime=(long)(this.viewPortStartTime-(milisecOnPixel*(this.viewportLeftMargin-xcoord)));
        }
        String localStringTime="";
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(localTime);
        localStringTime=cal.getTime().toString();
        //System.out.println("Godzina:"+localStringTime+ " ("+localTime+")");

        return localTime;
    }

    public String getXCoordStringTime(int xcoord, int _szer) {

        long localTime=0;
        String localStringTime="";
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(this.getXCoordTime(xcoord, _szer));
        DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM);
        localStringTime=df.format(cal.getTime());
        return localStringTime;
    }

    private void paintChart(Graphics g, PageFormat _pf) {


        boolean otherMeasurementsInGray=false;

        Graphics2D g2 = (Graphics2D)g;

        float wys =(float)_pf.getHeight();
        float szer =(float)_pf.getWidth();
                
        //Clean drawing area (make it white)
        g2.setBackground(Color.WHITE);        

        //leave method if project doesn't exist
        if (nmmProj==null) {
            System.out.println("Brak projektu, nie rysuję wykresu!");
            return;
        }

        //Drawing chart
        //Drawing can be performed if measurement is longer then 0 records
        if (nmmProj.isDravable()) {
            
            int firstRecordToDraw=0;
            long firstTimeToDraw=0;
            int lastRecordToDraw=0;
            long lastTimeToDraw=0;

            float poczatek=0;
            float koniec=0;
            
            long se=0;

            //ile pikseli w pionie ma obszar w ramce do rysowania wykresu
            float chartPixHeight=(wys-(viewportBottomMargin+viewportTopMargin));

            //ile pikseli w poziomie ma obszar do rysowania wykresu
            chartPixWidth=(int)szer-(viewportRightMargin+viewportLeftMargin);
            
            //ile decybeli przypada na jeden piksel na osi pionowej
            float dbOnPixel=((float)this.verticalScaleMaxDb-(float)this.verticalScaleMinDb)/(float)chartPixHeight;
            

            //rysowanie zdarzeń
            
            //rysuje jeżeli w projekcie są zdarzenia
            if ((this.nmmProj.getEventsNumber()>0) && (this.drawEvents)) {
                //petla po wszystkich zdarzeniach w projekcie
                for (int i=0; i<nmmProj.getEventsNumber();i++) {
                    NMMEvent tmpNMMEvent;
                    tmpNMMEvent=nmmProj.getEvent(i);

                    //zdarzenie bedzie rysowane tylko wtedy jeżeli:
                    // - jest przynajmniej częsciowo widoczne w wiewporcie
                    // - występuje całkowicie w granicy pomiaru którego dotyczy
                    //   prezentacja
                    if (((tmpNMMEvent.getStart()>this.viewPortStartTime) &&
                            (tmpNMMEvent.getStart()<this.viewPortEndTime) ||
                            ((tmpNMMEvent.getEnd()>this.viewPortStartTime) &&
                            (tmpNMMEvent.getEnd()<this.viewPortEndTime))) && 
                            (this.nmmProj.getMeasurement(this.nsm.
                            getMeasurementUUID()).isWithinMeasurement(tmpNMMEvent)))
                    {
                        //if begining of event is earlier then viewport starttime then draw it
                        //form vievport start time, otherwise draw it from begining
                        if (tmpNMMEvent.getStart()<this.viewPortStartTime) {
                            poczatek=this.getTimeXCoord(this.viewPortStartTime);
                        } else {
                            poczatek=this.getTimeXCoord(tmpNMMEvent.getStart());
                        }
                        if (tmpNMMEvent.getEnd()>this.viewPortEndTime) {
                            koniec=this.getTimeXCoord(this.viewPortEndTime);
                        } else {
                            koniec=this.getTimeXCoord(tmpNMMEvent.getEnd()+this.nmmProj.getProjectTimeResolution());
                        }
                        g2.setColor(tmpNMMEvent.getColor());
                        
                        
                        BufferedImage bufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g3 = bufferedImage.createGraphics();
                        g3.setColor(Color.WHITE);
                        g3.fillRect(0, 0, 200, 200);
                        g3.setColor(tmpNMMEvent.getColor());
                        for (int s=0; s<400; s=s+5) {
                            g3.drawLine(0, 400-s, 200, 200-s);
                        }
                        //g3.drawLine(0, 0, 100, 100); // \
                        //g3.drawLine(0, 100, 100, 0); // /

                        // paint with the texturing brush
                        Rectangle2D rect = new Rectangle2D.Double((int)poczatek, this.viewportTopMargin+1, (int)koniec-(int)poczatek,                        
                        ((int)wys-(this.viewportBottomMargin+this.viewportTopMargin)-1));
                        g2.setPaint(new TexturePaint(bufferedImage, rect));
                        g2.fill(rect);                        
                        
//                        g2.fillRect((int)poczatek, this.viewportTopMargin+1, (int)koniec-(int)poczatek,                        
//                        ((int)wys-(this.viewportBottomMargin+this.viewportTopMargin)-1));
                    }                    
                }
            }

            //pętla po wszystkich pomiarach w projekcie
            for (int m=0;m<=this.nmmProj.getMeasurementsNumber()-1;m++) {               
                //
                if (this.nmmProj.getMeasurement(m).getDrawLabels()) {
                    String etykieta=NMMToolbox.formatDouble(this.nmmProj.getMeasurementRecord(0,nmmProj.getMeasurement(0).getRecordTime(0)));
                    FontMetrics fm= g2.getFontMetrics();
                    se = fm.stringWidth(etykieta);
                } else {
                    se=1000000;
                }
                //sprawdzamy czy kolejny do rysowania pomiar w ogóle mieści się
                //w przedziale czasowym jaki ma byc widoczny na wykresie
                System.out.println("Rozpoczynam rysowanie wykresu: "+m);
                NMMMeasurement ms = this.nmmProj.getMeasurement(m);
                boolean drawMe=false;
                //System.out.println("Pomiar od: "+ms.measurementBeginTime+" do: "+ms.getMeasurementEndTime()+"\n"+
                        //"Wykres od: "+this.viewPortStartTime+" do: "+this.viewPortEndTime);
                if ((ms.getMeasurementBeginTime()<this.viewPortStartTime) &&
                        (ms.getMeasurementEndTime()>this.viewPortEndTime)) {                    
                    firstRecordToDraw = ms.getRecordIndex(this.viewPortStartTime);
                    firstTimeToDraw=this.viewPortStartTime;
                    lastRecordToDraw = ms.getRecordIndex(this.viewPortEndTime
                            -this.nmmProj.getProjectTimeResolution());
                    lastTimeToDraw=this.viewPortEndTime
                            -this.nmmProj.getProjectTimeResolution();
                    //System.out.println("firstRecordToDraw = "+firstRecordToDraw+" lastRecordToDraw="+lastRecordToDraw);
                    drawMe=true;
                }
                if ((ms.getMeasurementBeginTime()>=this.viewPortStartTime) &&
                        (ms.getMeasurementBeginTime()<=(this.viewPortEndTime))) {
                    //System.out.print("Wykres przeszedł warunek 1:");
                    //firstRecordToDraw = ms.getRecordIndex(this.viewPortStartTime);
                    firstRecordToDraw = 0;
                    //firstTimeToDraw=this.viewPortStartTime;
                    firstTimeToDraw=ms.getMeasurementStartTime();
                    if (this.viewPortEndTime<ms.getMeasurementEndTime()) {
                        //lastRecordToDraw = ms.getRecordIndex(this.viewPortEndTime-this.nmmProj.getProjectTimeResolution());
                        //lastTimeToDraw=this.viewPortEndTime-this.nmmProj.getProjectTimeResolution();
                        lastRecordToDraw = ms.getRecordIndex(this.viewPortEndTime);
                        lastTimeToDraw=this.viewPortEndTime;
                        //System.out.println("Ten wykres wychodzi poza prawą krawęź okna. Prawa krawędź to:"+this.viewPortEndTime);
                    } else {
                        lastRecordToDraw = ms.getMeasurementLength()-1;
                        lastTimeToDraw=ms.getMeasurementEndTime();
                        //System.out.println("Ten wykres konczy się w oknie.");
                    }
                    //System.out.println("firstRecordToDraw = "+firstRecordToDraw+" lastRecordToDraw="+lastRecordToDraw);
                    drawMe=true;
                }
                if ((ms.getMeasurementEndTime()>=this.viewPortStartTime) &&
                        (ms.getMeasurementEndTime()<=(this.viewPortEndTime))) {
                    //System.out.print("Wykres przeszedł warunek 2:");
                    lastRecordToDraw = ms.getMeasurementLength()-1;
                    lastTimeToDraw=ms.getMeasurementEndTime();
                    if (this.viewPortStartTime<ms.getMeasurementStartTime()) {
                        firstRecordToDraw = 0;
                        firstTimeToDraw = ms.getMeasurementStartTime();
                    } else {
                        firstRecordToDraw = ms.getRecordIndex(viewPortStartTime);
                        firstTimeToDraw = viewPortStartTime;
                    }
                    //System.out.println("firstRecordToDraw = "+firstRecordToDraw+" lastRecordToDraw="+lastRecordToDraw);
                    drawMe=true;
                }

                /*if (drawMe) {
                    System.out.println("Ten wykres bedzie przynajmniej częściowo rysowany!");
                } else {
                    System.out.println("Ten wykres nie bedzie rysowany, jest w innym czasie!");
                }*/


                // rysowanie układu współrzednych -------------------------------------
                // coordinate system axises can be drawn only for main measurement
                System.out.println("MUID prezentowanego modelu: "+this.nsm.getMeasurementUUID().toString());
                System.out.println("MUID aktualnego pomiaru: "+this.nmmProj.getMeasurement(m).getMUID().toString());
                if (this.nsm.getMeasurementUUID().equals(this.nmmProj.getMeasurement(m).getMUID())) {
                    NMMChartAxises nmmca = new NMMChartAxises(g2, _pf, this.viewportLeftMargin,
                        this.viewportBottomMargin, this.viewportRightMargin, this.viewportTopMargin,
                        this.verticalScaleMinDb, this.verticalScaleMaxDb,
                        firstTimeToDraw, lastTimeToDraw, this.locale);
                    nmmca.drawAxises();
                }
                
                //draw measurement "m" if measurement's "m" selector=1 and "m" is also current measurement
//                if ((this.measurementSlector==1) && (this.nmmProj.getCurrentMeasurementNumber()!=m)) {
//                    drawMe=false;
//                }
                if ((this.measurementSlector==3) && (this.nmmProj.getCurrentMeasurementNumber()!=m)) {
                    otherMeasurementsInGray=true;
                }

                //pętla po rekordach w pomiarze, jeżeli pomiar ma być drukowany (drawMe=true)
                if (drawMe) {
                    //System.out.println("Rysuję rekordy pomiaru od: "+firstRecordToDraw+" do "+lastRecordToDraw);
                    int step=(int)((lastRecordToDraw-firstRecordToDraw)/chartPixWidth);
                    if (step<1) { step=1; }
                    step=1;

                    //ustalanie grubości dla linii wartości i linii pomocniczej
                    //BasicStroke wideStroke = new BasicStroke(2);
                    //BasicStroke helperStroke= new BasicStroke(1);

                    // ustalanie kolorów rysowania wykresów
                    Color valueColor=ms.getMeasurementColor();
                    Color helperColor=valueColor.brighter().brighter().brighter();

                    if (otherMeasurementsInGray) {
                        valueColor=Color.GRAY;
                    }

                    for (int n=firstRecordToDraw;n<=lastRecordToDraw;n=n+step) {
                        //mapujemy wartośc rekordu pomiarowego do polożenia punktu w viewporcie
                        float y=(float)((((1/dbOnPixel)*(this.nmmProj.
                                getMeasurementRecord(m,ms.getRecordTime(n))
                                -this.verticalScaleMinDb))));

                        //rysujemy linię poziomą określającą wartośc odczytu z rekordu                        
                        float dlX=((this.getTimeXCoord(firstTimeToDraw))+(n-firstRecordToDraw)*this.getPixNumberOnDataRecord());
                        
                        //float dlY= (float)(wys-(this.viewportBottomMargin+y));
                        float dlY=(float)(this.viewportTopMargin+chartPixHeight-(y));

                        float dlXp1=((this.getTimeXCoord(firstTimeToDraw))+
                                (n-firstRecordToDraw+1)*this.getPixNumberOnDataRecord());

                        if (dlY>wys-this.viewportBottomMargin) {
                            dlY=wys-this.viewportBottomMargin;
                        }

                        g2.setStroke(new BasicStroke(0.1f));
                        g2.setColor(valueColor);
                        Line2D l2d = new Line2D.Float(dlX,dlY,
                                dlXp1,
                                dlY); //wys-y-this.viewportBottomMargin);
                        g2.draw(l2d);

                        //rysujemy linię pionową łączącą linie wartości rekordów
                        //z wyjątkiem ostatniej wartości w pomiarze
                        if (n!=lastRecordToDraw) {                            
                            //g2.setColor(helperColor);
                            float y2=(float)((1/dbOnPixel)*
                                    (this.nmmProj.getMeasurementRecord(m, ms.getRecordTime(n+1))-(float)this.verticalScaleMinDb));
                            
                            //float y2p=wys-(this.viewportBottomMargin+y2);
                            float y2p=(float)(this.viewportTopMargin+chartPixHeight-(y2));
                            
                            if (y2p>wys-this.viewportBottomMargin) {
                                y2p=wys-this.viewportBottomMargin;
                            }

                            Line2D l2da = new Line2D.Float(
                                    ((this.getTimeXCoord(firstTimeToDraw))+(n-firstRecordToDraw+1)*this.getPixNumberOnDataRecord()),
                                    dlY, 
                                    dlXp1, 
                                    y2p);
                            g2.draw(l2da);
                        }
                    }
                }
            }            
        } else {
            System.out.println("Projekt jest ale nie ma danych i nie może być renderowany !");
        }
    }

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
    public NMMChartPainter(Graphics2D g2, NMMProject _nmmp, PageFormat _pf,
            long _start, long _stop, int _measSel, int _vpLeftMargin, 
            int _vpRightMargin, int _vpTopMargin, int _vpBottomMargin, 
            NMMNoiseSourceModel _nsm, boolean _onlyEventsInMeasurement, boolean _drawEvents, Locale _locale) {

        this.viewPortStartTime=_start;
        this.viewPortEndTime=_stop;
        this.nmmProj=_nmmp;
        this.viewportBottomMargin=_vpBottomMargin;
        this.viewportLeftMargin=_vpLeftMargin;
        this.viewportRightMargin=_vpRightMargin;
        this.viewportTopMargin=_vpTopMargin;
        this.measurementSlector=_measSel;
        this.nsm=_nsm;
        this.onlyEventsInMeasurement=_onlyEventsInMeasurement;
        this.drawEvents=_drawEvents;
        this.locale=_locale;
        
        this.paintChart(g2, _pf);
    }
}
