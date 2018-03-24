/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.presentation;

import biz.ekoplan.nmm2010.charts.NMMChartPainter;
import biz.ekoplan.nmm2010.charts.NMMEventsTablePainter;
import biz.ekoplan.nmm2010.charts.NMMPresentationTextPainter;
import biz.ekoplan.nmm2010.enums.NoiseSourceModelType;
import biz.ekoplan.nmm2010.enums.PresentationType;
import biz.ekoplan.nmm2010.presentations.NMMPresentation;
import biz.ekoplan.nmm2010.surcemodel.NMMNoiseSourceModel;
import biz.ekoplan.nmm2010.surcemodel.NMMSingleEventsMethodModel;
import biz.ekoplan.nmm2010.toolbox.NMMToolbox;
import biz.ekoplan.nmm2010.toolbox.TimeConverter;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Locale;
import javax.imageio.ImageIO;
import nmm2010.NMMEventType;
import nmm2010.NMMProject;
import nmm2010.Setup;
//import biz.ekoplan.nmm2010.surcemodel.NMMSingleEventsModel;

/**
 *
 * @author Jarek
 */
public class NMMPresentationSingleEventsMethod implements Printable, NMMPresentation, Serializable {

    static final long serialVersionUID =1L;
    
    String presentationTitle;    
    NMMProject nmmProj;
    long startTime;
    long endTime;
    int noiseIndicator;
    NMMEventType eventType;
    int measurementSelector=-1;
    int[][] eventsModel;
    NMMSingleEventsMethodModel sem;
    Locale locale;
    Setup nmmSetup;

    final int METRIC_COLUMN_WIDTH = 370;    // width of metric column in 1/72in
    final int BINDING_AREA_WIDTH=50;        // strip of paper for binding
    final int SINGLE_EVENTS_COLUMN_WIDTH = 100; // widht of one colun
    final int SINGLE_EVENTS_IN_COLUMN=50;       // each column may hold up to 50 events


    public NMMPresentationSingleEventsMethod(NMMProject _nmmProj, long _stTime, long _endTime, 
            int _measSelector, NMMSingleEventsMethodModel _sem, Locale _locale, Setup _setup) {
        nmmProj=_nmmProj;
        this.startTime=_stTime;
        this.endTime=_endTime;
        this.measurementSelector=_measSelector;
        this.sem=_sem;
        this.locale=_locale;
        this.nmmSetup=_setup;
    }

    public void setEventType(NMMEventType _et) {
        this.eventType=_et;
    } 

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {

        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }
        Image img;               
        Graphics2D g2d = (Graphics2D)g;
        NMMChartPainter nmmChP;
        
        int footHeight=20;

        float metricColumnOffset=((float)pageFormat.getImageableWidth()
                -this.METRIC_COLUMN_WIDTH+20);

        System.out.println("Drukuje na papierze: ImageableWidth="+((int)pageFormat.getImageableWidth()-70)+" ImageableHeight"+
                ((int)pageFormat.getImageableHeight()-60));
        
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        System.out.println("Translacja układu strony druku do dX="+pageFormat.getImageableX()+" dY="+pageFormat.getImageableY());
        g2d.drawRect(this.BINDING_AREA_WIDTH, (int)0,
                (int)pageFormat.getImageableWidth()-this.BINDING_AREA_WIDTH, (int)pageFormat.getImageableHeight());
        
        // Nagłowek karty -----------------------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.drawString("Karta pomiarowa poziomu hałasu. "
                + "Pomiar metodą pojedynczych zdarzeń.", 150f, 30f);
        g2d.setFont(new Font(Font.SERIF, 1, 16));
        g2d.drawString(this.presentationTitle, 150f, 45f);

        // logo programu NMM --------------------------------------------------
        try {
             img = ImageIO.read(new File("nmm_big.png"));
             g2d.drawImage(img,65,20, 76, 25,null);
        } catch (IOException e) {
             System.out.println("Nie odnaleziono loga programu NMM. Poszukiwano:");
        }

        // EVENTS TABLE -------------------------------------------------------
        int reqCols=this.nmmProj.getEventsNumber()/this.SINGLE_EVENTS_IN_COLUMN;
        if (this.nmmProj.getEventsNumber()%this.SINGLE_EVENTS_IN_COLUMN!=0) {
            reqCols++;
        }
        System.out.println("Liczba niezbędnych kolumn: "+reqCols);

        // MAIN CHART ---------------------------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString("Zapis zmian poziomu hałasu w punkcie pomiarowym", 65f, 80);
        g2d.drawString("Legenda:", 65f, 675f);
        g2d.drawLine(70,690, 90, 690);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.setColor(Color.BLACK);
        g2d.drawString("wartosci poziomu ekwiwalentnego w okresach 1-sekundowych", 110f, 692f);
        nmmChP = new NMMChartPainter(g2d,nmmProj,pageFormat, this.startTime,
                this.endTime, this.measurementSelector, 100,  
                (this.SINGLE_EVENTS_COLUMN_WIDTH*(reqCols+1))+this.METRIC_COLUMN_WIDTH+70,
                100,
                200,
                this.sem, true, true, new Locale("pl","PL"));

        // events legend
        int evTypesNumber=this.nmmProj.getEventTypes().length;
        for (int i=0; i<evTypesNumber; i++) {
            NMMEventType et = (NMMEventType)this.nmmProj.getEventTypes()[i];
            g2d.setColor(et.getColor());
            g2d.fillRect(400+(i*150), 686, 5, 5);
            g2d.setColor(Color.black);
            g2d.drawString(et.getDescription(), 410f+(i*150), 692f);
        }
        

        // EVENTS TABLE
        // vertical line separating acoustic events from measurement history
        // chart
        g2d.setColor(Color.BLACK);
        g2d.drawLine((int)metricColumnOffset-((1+reqCols)*this.SINGLE_EVENTS_COLUMN_WIDTH),
                0,
                (int)metricColumnOffset-((1+reqCols)*this.SINGLE_EVENTS_COLUMN_WIDTH),
                (int)pageFormat.getImageableHeight()-footHeight);
        // events table

        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString("Zdarzenia akustyczne:", (int)metricColumnOffset-((1+reqCols)*this.SINGLE_EVENTS_COLUMN_WIDTH)+10,
                40f);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.setColor(Color.BLACK);
        
        NMMEventsTablePainter nmmetp=new NMMEventsTablePainter(g2d,pageFormat,
                (int)metricColumnOffset-((1+reqCols)*this.SINGLE_EVENTS_COLUMN_WIDTH)+10
                ,137,
                this.METRIC_COLUMN_WIDTH+30
                ,68,
                this.nmmProj, this.locale);

        String[] colNames1={"Zdarzenie","SEL"};
        double[] colWidth1={0.6, 0.4};
        String[][] tabCont = new String[this.nmmProj.getEventsNumber()][2];
        for (int row=0; row<this.nmmProj.getEventsNumber();row++) {
            tabCont[row][0]=this.nmmProj.getEvent(row).getEventType().toString()+" ("+
                    TimeConverter.LongToTimeString(this.nmmProj.getEvent(row).getStart(), 
                    DateFormat.SHORT, this.locale)+")";
            tabCont[row][1]=NMMToolbox.formatDouble(this.nmmProj.getCurrentMeasurement().
                    getSEL(this.nmmProj.getEvent(row).getStart(),
                    this.nmmProj.getEvent(row).getEnd()));
        }
        
        nmmetp.drawDuplicatedTable(tabCont, colNames1, colWidth1);

        // METRIC COLUMN ------------------------------------------------------

        // vertical line separating metric columne from table of acoustic
        // events
        g2d.drawLine((int)metricColumnOffset-10, 0, (int)metricColumnOffset-10,
                (int)pageFormat.getImageableHeight()-footHeight);
        
        // identyfikator karty pomirowej
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Karta:", metricColumnOffset, 40f);
        g2d.setFont(new Font(Font.SERIF, 1, 18));
        g2d.setColor(Color.BLUE);
        g2d.drawString(this.getPresentationTitle(), metricColumnOffset+65, 40f);

        // charakterystyka projektu -------------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString("Projekt",
                metricColumnOffset, 75);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Tytuł:", metricColumnOffset, 90f);
        g2d.drawString(this.nmmProj.getProjectTitle(), metricColumnOffset+65, 90f);
        g2d.drawString(this.nmmProj.getSubtitle(),metricColumnOffset+65, 105f);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.drawString("Opracowanie: ", metricColumnOffset, 125f);
        g2d.drawString(this.nmmProj.getProjectAuthor(), metricColumnOffset+65, 125f);
        g2d.drawString("Raport nr:",metricColumnOffset, 140f);
        g2d.drawString(this.nmmProj.getReportNumber(),metricColumnOffset+65,140f);
        g2d.drawString("Zlecenie:",metricColumnOffset, 155f);
        g2d.drawString(this.nmmProj.getProjectCommisionNumber(), metricColumnOffset+65, 155f);
        g2d.drawString("Uwagi:", metricColumnOffset, 170f);
        g2d.drawString(this.nmmProj.getProjectRemarks(),
                metricColumnOffset+65, 170f);

        // charakterystyka zapisu historii czasowej ---------------------------

        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString("Pomiar",
                metricColumnOffset, 205);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.setColor(Color.BLACK);        

        g2d.drawString("Operator:", metricColumnOffset, 225f);
        g2d.drawString(this.nmmProj.getCurrentMeasurement().getOperator(),
                metricColumnOffset+65, 225f);

        g2d.drawString("Start:", metricColumnOffset, 240f);
        g2d.drawString(TimeConverter.LongToDateString(
                this.startTime,DateFormat.LONG, this.locale),metricColumnOffset+65,240f);
        g2d.drawString("Koniec:", metricColumnOffset+145, 240f);
        g2d.drawString(TimeConverter.LongToDateString(
                this.endTime,DateFormat.LONG, this.locale),metricColumnOffset+65+130,240f);
        
        // wyniki pomiaru -----------------------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);        
        g2d.drawString("Wyniki pomiaru:", metricColumnOffset, 265f);
        g2d.setFont(new Font(Font.SERIF, 1, 12));
        g2d.setColor(Color.RED);        
        
        //sem = new NMMSingleEventsMethodModel(this.nmmProj, this.eventsModel);
        sem.recalculateModel();

        g2d.drawString("LAeqD:", metricColumnOffset+65, 280f);
        System.out.println("Poziom hałasu dla dnia: "+sem.getLAeqD());
        if (sem.getLAeqD()>0) {
            g2d.drawString(NMMToolbox.formatDouble(sem.getLAeqD())+"±"+NMMToolbox.formatDouble(sem.getUR_D())
                +"dB", metricColumnOffset+110, 280f);
        } else {
            g2d.drawString("n.d.", metricColumnOffset+110, 280f);
        }

        g2d.drawString("LAeqN:", metricColumnOffset+200, 280f);
        System.out.println("Poziom hałasu dla nocy: "+sem.getLAeqN());
        if (sem.getLAeqN()>0) {
            g2d.drawString(NMMToolbox.formatDouble(sem.getLAeqN())+"±"+NMMToolbox.formatDouble(sem.getUR_N())
                +"dB", metricColumnOffset+245, 280f);
        } else {
            g2d.drawString("n.d.", metricColumnOffset+245, 280f);
        }
        
        // informacja o mierniku poziomu dźwieku ------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString("Aparatura pomiarowa.",
                metricColumnOffset, 305);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.drawString("Miernik:", metricColumnOffset, 320f);
        NMMPresentationTextPainter nmmtp0 = new NMMPresentationTextPainter(g2d,
                pageFormat, (int) (metricColumnOffset + 65), 350, 40, 320, this.nmmProj);
        nmmtp0.drawText(this.nmmProj.getMeasurement(sem.getMeasurementUUID()).getMeasurementSet().getMeasurementSetDescription());

        g2d.drawString("Wzorcowanie", metricColumnOffset, 355f);
        NMMPresentationTextPainter nmmtp1 = new NMMPresentationTextPainter(g2d,
                pageFormat, (int) (metricColumnOffset + 65), 300, 40, 355, this.nmmProj);
        nmmtp1.drawText(this.nmmProj.getMeasurement(sem.getMeasurementUUID()).getMeasurementSet().getMeasurementSetDescription());

        // kalibracja sprzętu pomiarowego -------------------------------------
        g2d.drawString("Kalibracja:", metricColumnOffset, 410);
        g2d.drawString("Przed pomiarem: ", metricColumnOffset, 425);
        g2d.drawString(NMMToolbox.formatDouble(this.nmmProj.getCurrentMeasurement().
                getInitialCalibration()), metricColumnOffset+65, 425);
        g2d.drawString("Po pomiarze: ", metricColumnOffset, 435);
        g2d.drawString(NMMToolbox.formatDouble(this.nmmProj.getCurrentMeasurement()
                .getFinalCalibration()), metricColumnOffset+65, 435);

        // picture section ----------------------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString("Fotografia punktu pomiarowego",
                metricColumnOffset, 460);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        
        if (this.nmmProj.getCurrentMeasurement().hasPicture()) {
            g2d.drawImage(this.nmmProj.getCurrentMeasurement().getPicture().
                getImage(),(int)metricColumnOffset+65,470,210,140,null);
        }        
        // uwagi do pomiaru ---------------------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString("Lokalizacja punktu pomiarowego", metricColumnOffset, 625);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Współrzędne:    X="+NMMToolbox.formatDouble(this.nmmProj
                .getCurrentMeasurement().getCoordinateX())+"m    Y="+
                NMMToolbox.formatDouble(this.nmmProj
                .getCurrentMeasurement().getCoordinateY())
                +"m     h="+NMMToolbox.formatDouble(
                this.nmmProj.getCurrentMeasurement().getHeight())+"m",metricColumnOffset, 640);
        NMMPresentationTextPainter nmmtp = new NMMPresentationTextPainter(g2d, 
                pageFormat, (int) metricColumnOffset, 60, 40, 655, this.nmmProj);
        nmmtp.drawText(this.nmmProj.getCurrentMeasurement().getRemarks());
        
        // stopka -------------------------------------------------------------        
        Stroke stroke = new BasicStroke(0.2f);
        g2d.setStroke(stroke);
        g2d.drawLine(this.BINDING_AREA_WIDTH,
                (int)pageFormat.getImageableHeight()-footHeight,
                (int)pageFormat.getImageableWidth(),
                (int)pageFormat.getImageableHeight()-footHeight);

        g2d.drawString("Noise Measurement Manager (c)Ekoprojekt 2007-2015", 65,
                (int)pageFormat.getImageableHeight()-7);
        try {
             img = ImageIO.read(new File(this.nmmSetup.getProperty("NMM_SETUP_LOGO", "logo_firmy.png")));
             g2d.drawImage(img,(int)pageFormat.getImageableWidth()-90,(int)pageFormat.getImageableHeight()-18,
                     77, 14,null);
         } catch (IOException e) {
             System.out.println("Nie odnaleziono loga firmy...");
         }               
        return PAGE_EXISTS;
    }

    public void setNoiseIndicator(int _ni) {
        this.noiseIndicator=_ni;
    }


    public void setPresentationTitle(String _presentationTitle) {
        this.presentationTitle=_presentationTitle;
    }

    public String getPresentationTitle() {
        return this.presentationTitle;
    }

    public void setPageFormat(int _pageFormat) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getStartTime() {
       return this.startTime;
    }

    public long getEndTme() {
        return this.endTime;
    }

    public void setStartTime(long _stTime) {
        this.startTime=_stTime;
    }

    public void setEndTime(long _enTime) {
        this.endTime=_enTime;
    }

    public PageFormat getPageFormat() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return this.presentationTitle;
    }

    public boolean isEditable() {
        return true;
    }

    public boolean containsNoiseSourceModel() {
        return true;
    }

    public NoiseSourceModelType getNoiseSourceModelType() {
        return NoiseSourceModelType.POLISH_REFERENCE_METHOD_EVENTS;
    }

    public NMMNoiseSourceModel getNoiseSourceModel() {
        return this.sem;
    }

    public void setNoiseSourceModel(NMMNoiseSourceModel _sm) {
        this.sem=(NMMSingleEventsMethodModel)_sm;
    }

    @Override
    public PresentationType getPresentationType() {
        return PresentationType.MEGA_SINGLE_EVENTS;
    }
}
