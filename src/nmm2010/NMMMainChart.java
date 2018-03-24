/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nmm2010;

import biz.ekoplan.nmm2010.measurement.NMMMeasurement;
import biz.ekoplan.nmm2010.nmmproject.NMMProjectChangedEvent;
import biz.ekoplan.nmm2010.nmmproject.NMMProjectChangedListener;
import biz.ekoplan.nmm2010.toolbox.NMMToolbox;
import biz.ekoplan.nmm2010.toolbox.TimeConverter;
import java.awt.*;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.JScrollBar;

/**
 *
 * @author Jarek
 */
public class NMMMainChart extends JComponent implements NMMProjectChangedListener {

    final int ZOOM_ALL=0;
    final int ZOOM_100=1;
    final int VIEWPORT_TOP_MARGIN=20;
    final int VIEWPORT_BOTTOM_MARGIN=40;
    final int VIEWPORT_LEFT_MARGIN=80;
    final int VIEWPORT_RIGHT_MARGIN=20;
    int verticalScaleMinDb=20;
    int verticalScaleMaxDb=100;
    long viewPortStartTime=0;
    long viewPortEndTime=0;
    int chartPixWidth=0;
    NMMProject nmmProj;
    JScrollBar sb;
    
    final boolean DEBUG = true; 

    public NMMMainChart() {
    }

    /**
     * Set new scale on vertical (dB) axis
     * @param _lower, integer
     * @param _upper, integer
     */
    public void setVeticalScale(int _lower, int _upper) {
        
        boolean change = true;
        if (_lower>=_upper) { change=false; }
        if (_lower<0) { change=false; }
        if (_upper>160) { change=false; }

        if (change) {
            this.verticalScaleMinDb=_lower;
            this.verticalScaleMaxDb=_upper;
        }
    }

    public void setNMMProject(NMMProject nmmproj) {
        nmmProj=nmmproj;
    }

    public void setTimeSelector(JScrollBar _sb) {
        this.sb=_sb;
    }

    /**
     * ile rekordów jest pokazanych na wykresie
     * @return
     */
    public int getNumberOfRecordsInViewport() {
        int numberOfRecordsToDraw=(int)((this.viewPortEndTime-this.viewPortStartTime)/this.nmmProj.getProjectTimeResolution());
        return numberOfRecordsToDraw;
    }

    public long getViewportStartTime() {
        return this.viewPortStartTime;
    }

    public long getViewportEndTime() {
        return this.viewPortEndTime;
    }

    /**
     * Zoom out twice
     */
    public void zoomOut() {
        long tw = (this.viewPortEndTime-this.viewPortStartTime)/2;
        long newStart = this.viewPortStartTime-tw;
        long newEnd = this.viewPortEndTime+tw;

        // tutaj można dopisać sprawdzaie, czy nie wyskakujemy poza projekt,
        // aczkolwiek wyskok poza projekt nie jest groźny z tego co do teraz
        // widzę
        
        this.viewPortStartTime=newStart;
        this.viewPortEndTime=newEnd;        
        this.paintComponent(this.getGraphics());

    }

     /** 
      * Ile pikseli przypada na jeden rekord danych
      * @return
      */
    public float getPixNumberOnDataRecord() {
        float pnodr=0;
        pnodr=(float)chartPixWidth/(float)getNumberOfRecordsInViewport();
        return pnodr;
    }
    
    /**
     * Zwraca wspólrzędną X na wykresie czasu podanego metodzie jako parametr
     * @param time
     * @return  long, x-coord
     */
    private long getTimeXCoord(long time) {
        long xcoord=-1;
        long timeDif=time-this.viewPortStartTime;
        // obliczamy liczbę rekordów
        long drn=timeDif/this.nmmProj.getProjectTimeResolution();
        //System.out.print("Liczba rekordów:"+drn);
        //obliczamy pikselową szerokość rekordów
        float pw = (float)drn*this.getPixNumberOnDataRecord();
        //System.out.println("    pix on data ="+this.getPixNumberOnDataRecord());
        //obliczamy wspólrzedną X czasu na wykresie
        xcoord=this.VIEWPORT_LEFT_MARGIN+(long)pw;
        return xcoord;
    }

    /**
     * Get time of the n-th record in project. n is based on project width, not
     * measurement width.
     * @param _recNumber    -   this is the number of record on whole project
     *                          wide basis. If there are 2 measurements in project
     *                          each 30 sec long, and the first starts at 01:00 
     *                          and the second starts at 01:01 then project is
     *                          90 records wide.                          
     * @return
     */
    public long getProjectWideRecordTime (int _recNumber) {

        long tm=0;
            tm=this.nmmProj.getProjectBeginTime()+_recNumber*
                    this.nmmProj.getProjectTimeResolution();
        return tm;
    }


    /**
     * Returns time (as long) for x coordinate in chart window
     * @param xcoord - x coordinate on chart
     * @return
     */
    public long getXCoordTime(int xcoord) {
        
        long localTime=-1;
        double chartLWidth = this.viewPortEndTime-this.viewPortStartTime;
        double milisecOnPixel = (chartLWidth)/(double)(this.getWidth()-(this.VIEWPORT_LEFT_MARGIN+this.VIEWPORT_RIGHT_MARGIN));
        long sek = (long) (milisecOnPixel * (xcoord - this.VIEWPORT_LEFT_MARGIN));
//        System.out.println("Szerokość okna w sekundach:"+chartLWidth/1000+
//                " Szerokosc okna w pikselach: "+(this.getWidth()-(this.VIEWPORT_LEFT_MARGIN+this.VIEWPORT_RIGHT_MARGIN))+
//                " Pozycja:"+(xcoord-this.VIEWPORT_LEFT_MARGIN)+
//                " Sekunda:"+sek+
//                " Milisekund na piksel: "+milisecOnPixel);
        
        if (xcoord>this.VIEWPORT_LEFT_MARGIN) {
            localTime=(this.viewPortStartTime+(sek));
        } else if (xcoord==this.VIEWPORT_LEFT_MARGIN) {
            localTime=this.viewPortStartTime;
        } else {
            localTime=(long)(this.viewPortStartTime-(milisecOnPixel*(this.VIEWPORT_LEFT_MARGIN-xcoord)));
        }
        String localStringTime="";
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(localTime);
        localStringTime=cal.getTime().toString();
        //System.out.println("Godzina:"+localStringTime+ " ("+localTime+")");
        return localTime;
    }

    public String getXCoordStringTime(int xcoord) {

        long localTime=0;
        String localStringTime;
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(this.getXCoordTime(xcoord));
        DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM);
        localStringTime=df.format(cal.getTime());
        //localStringTime=cal.getTime().toString();
        return localStringTime;
    }

    public int getViewportHeight() {
        return this.getHeight();
    }
    
    public int getViewportTopMargin() {
        return this.VIEWPORT_TOP_MARGIN;
    }
    
    public int getViewportBottomMargin() {
        return this.VIEWPORT_BOTTOM_MARGIN;
    }
    
    @Override
    public void paintComponent(Graphics g) {        

        Image nowaRamka;
        Graphics2D g2;
        
        Graphics2D ekran = (Graphics2D)g;
        super.paintComponent(ekran);

        //tworzenie bufora ramki
        nowaRamka = createImage( this.getWidth(), this.getHeight());
        g2 = (Graphics2D) nowaRamka.getGraphics();                 
        
        //Czyszczenie obszaru rysowania
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, this.getWidth(), this.getHeight());

        
        //Rysowanie ramki wykresu (jeżeli jest na nia miejsce)
        if (((this.getHeight()-(VIEWPORT_BOTTOM_MARGIN+VIEWPORT_TOP_MARGIN))>10) &
                (this.getWidth()-(VIEWPORT_RIGHT_MARGIN+VIEWPORT_LEFT_MARGIN))>10) {
            g2.drawRect(VIEWPORT_LEFT_MARGIN,VIEWPORT_TOP_MARGIN,this.getWidth()-
                (VIEWPORT_RIGHT_MARGIN+VIEWPORT_LEFT_MARGIN),
                this.getHeight()-(VIEWPORT_BOTTOM_MARGIN+VIEWPORT_TOP_MARGIN));
        } else {
            return;
        }

        //Wychodzimy jeżeli nie ma jeszcze projektu który można narysować
        if (nmmProj==null) {
            if (this.DEBUG) {
                NMMToolbox.debugMessage("Brak projektu, nie rysuję wykresu!", DEBUG);                
            }            
            return;
        }

        //Rysowanie wykresu
        //Wykres może być rysowany jeżeli projekt ma niezerową długość
        if (nmmProj.isDravable()) {
            //System.out.println("---Rysowanie-------------------------------------------------------");

            int firstRecordToDraw=0;
            long firstTimeToDraw=0;
            int lastRecordToDraw=0;
            long lastTimeToDraw=0;

            //DecimalFormat df = new DecimalFormat("###.0");
            long se=0;

            //ile pikseli w pionie ma obszar w ramce do rysowania wykresu
            int chartPixHeight=this.getHeight()-(VIEWPORT_BOTTOM_MARGIN+VIEWPORT_TOP_MARGIN);

            //ile pikseli w poziomie ma obsza do rysowania wykresu
            chartPixWidth=this.getWidth()-(VIEWPORT_RIGHT_MARGIN+VIEWPORT_LEFT_MARGIN);
            //System.out.println("Szerokość obszaru do rysowania (w pikselach)"+chartPixWidth);

            //ile decybeli przypada na jeden piksel na osi pionowej
            float dbOnPixel=((float)this.verticalScaleMaxDb-(float)this.verticalScaleMinDb)/(float)chartPixHeight;
            //System.out.println("Wysokośc obszaru rysowania wykresu w pikselach: "+chartPixHeight+" a dbOnPixel="+dbOnPixel);

            
             //rysowanie zdarzeń
            double modyfikator;
            int poczatek=0;
            int koniec=0;
            
            //if there are events in the project then print them
            if (this.nmmProj.getEventsNumber()>0) {                
                //check all events
                for (int i=0; i<nmmProj.getEventsNumber();i++) {
                    modyfikator=1.0;
                    NMMEvent tmpNMMEvent;
                    tmpNMMEvent=nmmProj.getEvent(i);

                    //if event tmpNMMEvent is at least partly within viewport then it can be painted
                    if ((tmpNMMEvent.getStart()>this.viewPortStartTime) &&
                            (tmpNMMEvent.getStart()<this.viewPortEndTime) || 
                            ((tmpNMMEvent.getEnd()>this.viewPortStartTime) &&
                            (tmpNMMEvent.getEnd()<this.viewPortEndTime))) 
                    {
                        //if begining of event is earlier then viewport starttime then draw it
                        //form vievport start time, otherwise draw it from begining
                        if (tmpNMMEvent.getStart()<this.viewPortStartTime) {
                            poczatek=(int)this.getTimeXCoord(this.viewPortStartTime);
                        } else {
                            poczatek=(int)this.getTimeXCoord(tmpNMMEvent.getStart());
                        }
                        if (tmpNMMEvent.getEnd()>this.viewPortEndTime) {
                            koniec=(int)this.getTimeXCoord(this.viewPortEndTime);
                        } else {
                            koniec=(int)this.getTimeXCoord(tmpNMMEvent.getEnd()+1000);
                        }                          
                        g2.setColor(tmpNMMEvent.getColor());
                    
                        // jeżeli event jest dezaktywowany, to będzie rysowany tylko
                        // jako belka, a nie cały pionowy pas
                        if (!tmpNMMEvent.isEnabled()) {
                            modyfikator=0.1;
                        }
                        g2.fillRect(poczatek, this.VIEWPORT_TOP_MARGIN+1, koniec-poczatek,
                            (int)((modyfikator*(this.getHeight()-(this.VIEWPORT_BOTTOM_MARGIN+this.VIEWPORT_TOP_MARGIN)))-1));
                    }                     
                }
            }
            //go throught all measurements in the project
            for (int m=0;m<=this.nmmProj.getMeasurementsNumber()-1;m++) {
                //check if measurement is to be drawn
                if (this.nmmProj.getMeasurement(m).getVisible()) {
                    if (this.nmmProj.getMeasurement(m).getDrawLabels()) {
                        String etykieta=NMMToolbox.formatDouble(this.nmmProj.getMeasurementRecord(0,nmmProj.getMeasurement(0).getRecordTime(0)));
                        FontMetrics fm= g2.getFontMetrics();
                        se = fm.stringWidth(etykieta);
                    } else {
                        se=1000000;
                    }
                    //sprawdzamy czy kolejny do rysowania pomiar w ogóle mieści się
                    //w przedziale czasowym jaki ma byc widoczny na wykresie
                    //System.out.println("Rozpoczynam rysowanie wykresu: "+m);
                    NMMMeasurement ms = this.nmmProj.getMeasurement(m);
                    boolean drawMe=false;
//                    System.out.println("Pomiar od: "+ms.getMeasurementBeginTime()+" "+
//                            TimeConverter.LongToTimeString(ms.getMeasurementBeginTime(), DateFormat.MEDIUM, Locale.ENGLISH)+
//                            " do: "+ms.getMeasurementEndTime()+" "+TimeConverter.LongToTimeString(ms.getMeasurementEndTime(), DateFormat.MEDIUM, Locale.ENGLISH)+"\n"+
//                            "Wykres od: "+this.viewPortStartTime+" do: "+this.viewPortEndTime);
                    if ((ms.getMeasurementBeginTime()<this.viewPortStartTime) &&
                            (ms.getMeasurementEndTime()>this.viewPortEndTime)) {
                        System.out.print("Wykres przeszedł warunek 0:");
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
                        System.out.print("Wykres przeszedł warunek 1:");
                        firstRecordToDraw = 0;
                        firstTimeToDraw=ms.getMeasurementStartTime();
                        if (this.viewPortEndTime<ms.getMeasurementEndTime()) {
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

                    if (drawMe) {
                        //System.out.println("Ten wykres bedzie przynajmniej częściowo rysowany!");
                    } else {
                        //System.out.println("Ten wykres nie bedzie rysowany, jest w innym czasie!");
                    }

                    //pętla po rekordach w pomiarze, jeżeli pomiar ma być drukowany (drawMe=true)
                    if (drawMe) {
                        //System.out.println("Rysuję rekordy pomiaru od: "+firstRecordToDraw+" do "+lastRecordToDraw);
                        int step=(int)((lastRecordToDraw-firstRecordToDraw)/chartPixWidth);
                        if (step<1) { step=1; }
                        step=1;;
                        // ustalanie kolorów rysowania wykresów
                        Color valueColor=ms.getMeasurementColor();
                        Color helperColor=valueColor.brighter();

                        for (int n=firstRecordToDraw;n<=lastRecordToDraw;n=n+step) {

                            //mapujemy wartośc rekordu pomiarowego do polożenia punktu w viewporcie
                            int y=(int)((1/dbOnPixel)*(this.nmmProj.
                                    getMeasurementRecord(m,ms.getRecordTime(n))
                                    -this.verticalScaleMinDb));
                            //System.out.println("y="+y+" dbOnPixel="+dbOnPixel+" a w dB było: "+this.nmmProj.getMeasurementRecord(-1, this.viewPortStartTime+(n*this.nmmProj.getProjectTimeResolution())-this.verticalScaleMinDb));

                            //rysujemy linię poziomą określającą wartośc odczytu z rekordu
                            int dlX=(int)((this.getTimeXCoord(firstTimeToDraw))+(n-firstRecordToDraw)*this.getPixNumberOnDataRecord());
                            int dlY= this.getHeight()-y-this.VIEWPORT_BOTTOM_MARGIN;
                            if (!ms.getRecord(n).isExcluded()) {
                                g2.setColor(valueColor);
                            } else {
                                g2.setColor(Color.LIGHT_GRAY);
                            }                            
                            g2.drawLine(dlX,dlY,
                                    (int)((this.getTimeXCoord(firstTimeToDraw))+(n-firstRecordToDraw+1)*this.getPixNumberOnDataRecord()),
                                    this.getHeight()-y-this.VIEWPORT_BOTTOM_MARGIN);
                                                                                   
                            //rysujemy linię pionową łączącą linie wartości rekordów
                            //z wyjątkiem ostatniej wartości w pomiarze
                            if (n!=lastRecordToDraw) {
                                if (!ms.getRecord(n).isExcluded()) {
                                    g2.setColor(helperColor);
                                } else {
                                    g2.setColor(Color.LIGHT_GRAY);
                                }                                   
                                int y2=(int)((1/dbOnPixel)*(this.nmmProj.getMeasurementRecord(m, ms.getRecordTime(n+1))-this.verticalScaleMinDb));
                                g2.drawLine((int)((this.getTimeXCoord(firstTimeToDraw))+(n-firstRecordToDraw+1)*this.getPixNumberOnDataRecord()),
                                        this.getHeight()-y-this.VIEWPORT_BOTTOM_MARGIN,
                                        (int)((this.getTimeXCoord(firstTimeToDraw))+(n-firstRecordToDraw+1)*this.getPixNumberOnDataRecord()),
                                        this.getHeight()-y2-this.VIEWPORT_BOTTOM_MARGIN);
                            }
                            //rysujemy etykiety
                            g2.setColor(Color.BLACK);
                            if (se<=this.getPixNumberOnDataRecord()) {
                                String etykieta1=NMMToolbox.formatDouble(this.nmmProj.getMeasurementRecord(m,nmmProj.getMeasurement(m).getRecordTime(n)));
                                FontMetrics fm= g2.getFontMetrics();
                                etykieta1=n+":"+etykieta1;
                                g2.drawString(etykieta1, dlX+1, dlY-1);
                                g2.drawOval(dlX, dlY, 3, 3);
                            }
                            
                            //dla aktywnego pomiary rysujemy gwiazdkę
                            if (this.nmmProj.getCurrentMeasurementNumber()==m && n==firstRecordToDraw) {
                                g2.setColor(Color.RED);
                                g2.fillOval(dlX,dlY, 10, 10);
                                g2.setColor(valueColor);    
                            }
                        }
                    }  
                }                                
            }
            //System.out.println("Wszystkie wykresy narysowane.");

            //Rysowanie aktualnej selekcji
            if (nmmProj.getCurrentSelection().isSet()) {
                //System.out.println("Rysuję selekcję od: "+TimeConverter.LongToString(nmmProj.getCurrentSelection().getStart())+
                //        " do: "+TimeConverter.LongToString(nmmProj.getCurrentSelection().getEnd()));

                //if event tmpNMMEvent is at least partly within viewport then it can be painted
                NMMEvent tmpNMMEvent = this.nmmProj.getCurrentSelection();
                if ((tmpNMMEvent.getStart()>this.viewPortStartTime) &&
                        (tmpNMMEvent.getStart()<this.viewPortEndTime) ||
                        ((tmpNMMEvent.getEnd()>this.viewPortStartTime) &&
                        (tmpNMMEvent.getEnd()<this.viewPortEndTime)))
                {
                    //if begining of event is earlier then viewport starttime then draw it
                    //form vievport start time, otherwise draw it from begining
                    if (tmpNMMEvent.getStart()<this.viewPortStartTime) {
                        poczatek=(int)this.getTimeXCoord(this.viewPortStartTime);
                    } else {
                        poczatek=(int)this.getTimeXCoord(tmpNMMEvent.getStart());
                    }
                    if (tmpNMMEvent.getEnd()>this.viewPortEndTime) {
                        koniec=(int)this.getTimeXCoord(this.viewPortEndTime);
                    } else {
                        koniec=(int)this.getTimeXCoord(tmpNMMEvent.getEnd()+1000);
                    }
                }
                //System.out.println("Czyli: od "+nmmProj.getCurrentSelection().getStart()+
                //        " do "+nmmProj.getCurrentSelection().getEnd());
                //           System.out.println("A w pikselach to: od "+poczatek+" "+koniec);
                g2.setColor(Color.red);
                g2.setXORMode(Color.blue);
                g2.fillRect(poczatek, this.VIEWPORT_TOP_MARGIN, koniec-poczatek,
                        (this.getHeight()-(this.VIEWPORT_BOTTOM_MARGIN+this.VIEWPORT_TOP_MARGIN)));
                g2.setPaintMode();
            }           

            //rysowanie opisów wykresu (godziny i opisy osi)

            //oś pozioma
            String xAxisDescription="Time [hh:mm:ss]";
            int descriptionXCoord = (this.getWidth()-
                    g2.getFontMetrics().stringWidth(xAxisDescription))/2;
            g2.setColor(Color.BLACK);
            g2.drawString(xAxisDescription, descriptionXCoord,
                    this.getHeight()-10);

            //oś pionowa - opis osi
            String yAxisDescription="Noise level [dB]";
            int translateY=((this.getHeight()-(this.VIEWPORT_BOTTOM_MARGIN+
                    this.VIEWPORT_TOP_MARGIN))/2)+this.VIEWPORT_TOP_MARGIN+
                    g2.getFontMetrics().stringWidth(yAxisDescription)/2;
            g2.translate(20,translateY);
            g2.rotate(-Math.PI/2);
            
            int descriptionYCoord = (this.getHeight()
                    -g2.getFontMetrics().stringWidth(yAxisDescription))/2;
            g2.drawString(yAxisDescription, 0,0);
            g2.rotate(Math.PI/2);
            g2.translate(-20,-translateY);

            // oś pionowa - wartości

            int liczbaPrzedzialow=(this.verticalScaleMaxDb-this.verticalScaleMinDb)/10;
            for (int i=0; i<liczbaPrzedzialow+1;i++) {
                double ety=this.verticalScaleMinDb+i*10;
                String etykieta = NMMToolbox.formatDouble(ety);
                int igrek=this.getHeight()-this.VIEWPORT_BOTTOM_MARGIN-
                        (i*((this.getHeight()-this.VIEWPORT_BOTTOM_MARGIN
                        -this.VIEWPORT_TOP_MARGIN)/liczbaPrzedzialow));
                g2.drawString(etykieta, 36, igrek);
            }

            //rysowanie godzin
            float lo=this.getNumberOfRecordsInViewport()/4;
            Font f=new Font("Dialog", Font.PLAIN, 9);
            g2.setFont(f);
            for (int kg=0; kg<=4; kg++) {
                int ofset=(int)(this.VIEWPORT_LEFT_MARGIN+kg*lo*this.getPixNumberOnDataRecord());
                String label = this.getXCoordStringTime(ofset);
                g2.drawString(label,
                        (int)(ofset-g.getFontMetrics().stringWidth(label)/2)
                        ,(int)(this.getHeight()-this.VIEWPORT_BOTTOM_MARGIN/(1.5)));
            }
            
            //wkopiowanie ramki w ekran
            ekran.drawImage(nowaRamka, 0, 0, this); 
            
        } else {
            //System.out.println("Projekt jest ale nie ma danych i nie może być rysowany !");
        }
    }

    /*public void moveViewport(int _numberOfRecords) {
        System.out.println("-moveViewport------------------------------------------------------------------");
        long newStart=this.viewPortStartTime+_numberOfRecords*this.nmmProj.getProjectTimeResolution();
        long newEnd = this.viewPortEndTime+_numberOfRecords*this.nmmProj.getProjectTimeResolution();
        if ((newStart>=this.nmmProj.getProjectBeginTime()) && (newStart<=this.nmmProj.getProjectEndTime()) &&
                ((newEnd<=this.nmmProj.getProjectEndTime()) && (newEnd>=this.nmmProj.getProjectBeginTime()))) {
            this.viewPortStartTime=newStart;
            this.viewPortEndTime=newEnd;
            this.paintComponent(this.getGraphics());
            System.out.println("Zmieniam granice czasowe ViewPortu:");
            System.out.println("nowy start:"+newStart+" tj. "+TimeConverter.LongToString(newStart));
            System.out.println("nowy koniec:"+newEnd+" tj. "+TimeConverter.LongToString(newEnd));
        } else {
            System.out.println("Nie moge wykonać polecenia moveViewPort()!");
            System.out.println("Aktualnie projekt od: "+this.nmmProj.getProjectBeginTime()+" do "+this.nmmProj.getProjectEndTime()+
                    " tj. od: "+TimeConverter.LongToString(this.nmmProj.getProjectBeginTime())+" do "
                    +TimeConverter.LongToString(this.nmmProj.getProjectEndTime()));
            System.out.println("nowy start:"+newStart+" tj. "+TimeConverter.LongToString(newStart));
            System.out.println("nowy koniec:"+newEnd+" tj. "+TimeConverter.LongToString(newEnd));
        }
    }*/

    /**
     * Zoom to specified viewport width
     * @param percent - zoom in percent (100% - 1 pixel per record, 200% - 2 pixels, etc.)
     */
    public void setViewPortPercentRange(int percent) {
        long midleTime=this.getViewportStartTime()+((this.getViewportEndTime()-this.getViewportStartTime())/2);
        long newNumberOfRecords=(this.getWidth()-this.VIEWPORT_LEFT_MARGIN-this.VIEWPORT_RIGHT_MARGIN)/(2*(percent/100));
        this.setViewPortTimeRange(midleTime-newNumberOfRecords*this.nmmProj.getProjectTimeResolution()
                , midleTime+newNumberOfRecords*this.nmmProj.getProjectTimeResolution());
    }

    /**
     * Ustawia zakres widoczności dla viewportu na cały projekt. Działa tak samo
     * jak "Zoom all" wybrane z menu programu
     */
    public void setViewPortFullTimeRange() {
        this.setViewPortTimeRange(this.nmmProj.getProjectBeginTime(), 
                this.nmmProj.getProjectEndTime());
    }
    
    /**
     * 
     * @param start
     * @param stop 
     */
    public void setViewPortTimeRange(long start, long stop) {
        this.viewPortStartTime=start;
        this.viewPortEndTime=stop;        
        long viewportWidth=(stop-start)/this.nmmProj.getProjectTimeResolution();        
        this.sb.setMaximum(this.nmmProj.getProjectRecordsSpan()-(int)viewportWidth);        
        this.sb.setValue(this.nmmProj.getProjectWideRecordNumber(start));
        if (this.DEBUG) {
            System.out.println("------Ustawianie paska nawigacji --------------");
            System.out.println("Szerokośc viewportu (rekordy):"+viewportWidth);
            System.out.println("Max paska: "+(this.nmmProj.getProjectRecordsSpan()-(int)viewportWidth));
            System.out.println("Aktualne ustawienie paska:"+this.nmmProj.getProjectWideRecordNumber(start));
        }
        this.paintComponent(this.getGraphics());
    }
    
    @Override
    public void dispatchNMMProjectChangedEvent(NMMProjectChangedEvent _mEvent) {
        this.nmmProj=(NMMProject)_mEvent.getSource();
        if (this.DEBUG) {
            System.out.println("Zmiana danych w projekcie powoduje zmiane zakresu czasowego pola rysowania!");
            System.out.println("Dotychczasowy zakres czasowy: "+ this.viewPortStartTime+" - "+ this.viewPortEndTime);
        }                        
        this.setEnabled(false);
        this.paintComponent(this.getGraphics());
    }
}
