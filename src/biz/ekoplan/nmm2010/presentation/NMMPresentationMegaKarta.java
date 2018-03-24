/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.presentation;

import biz.ekoplan.nmm2010.charts.NMMChartPainter;
import biz.ekoplan.nmm2010.charts.NMMPresentationTextPainter;
import biz.ekoplan.nmm2010.enums.NoiseLevelIndicators;
import biz.ekoplan.nmm2010.enums.NoiseSourceModelType;
import biz.ekoplan.nmm2010.enums.PresentationType;
import biz.ekoplan.nmm2010.presentations.NMMPresentation;
import biz.ekoplan.nmm2010.surcemodel.NMMNoiseSourceModel;
import biz.ekoplan.nmm2010.toolbox.NMMToolbox;
import biz.ekoplan.nmm2010.toolbox.TimeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
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
import nmm2010.NMMSetup;
import nmm2010.Setup;

/**
 *
 * @author Jarek
 */
public class NMMPresentationMegaKarta implements Printable, NMMPresentation, Serializable {

    static final long serialVersionUID =1L;
    
    String presentationTitle;
    NMMProject nmmProj;
    long startTime;
    long endTime;
    int noiseIndicator;
    NMMEventType eventType;    
    NMMNoiseSourceModel nsm;
    Locale locale;
    Setup nmmSetup;

    final int METRIC_COLUMN_WIDTH = 370;    // width of metric column in 1/72in
    final int BINDING_AREA_WIDTH=50;        // strip of paper for binding

    public NMMPresentationMegaKarta(NMMProject _nmmProj, long _stTime, 
            long _endTime, Setup _setup, NMMNoiseSourceModel _nsm, 
            Locale _locale) {
        nmmProj=_nmmProj;
        this.startTime=_stTime;
        this.endTime=_endTime;
        this.nsm=_nsm;
        this.locale=_locale;
        this.nmmSetup=_setup;
    }

//    public void setEventType(NMMEventType _et) {
//        this.eventType=_et;
//    } 

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {

        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }
        BufferedImage img;
        Graphics2D g2d = (Graphics2D)g;
        
        float metricColumnOffset=((float)pageFormat.getImageableWidth()
                -this.METRIC_COLUMN_WIDTH+20);

//        System.out.println("Drukuje na papierze: ImageableWidth="+((int)pageFormat.getImageableWidth()-70)+" ImageableHeight"+
//                ((int)pageFormat.getImageableHeight()-60));
        
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
//        System.out.println("Translacja układu strony druku do dX="+pageFormat.getImageableX()+" dY="+pageFormat.getImageableY());
        g2d.drawRect(this.BINDING_AREA_WIDTH, (int)0,
                (int)pageFormat.getImageableWidth()-this.BINDING_AREA_WIDTH, (int)pageFormat.getImageableHeight());
        
        // Nagłowek karty -----------------------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("KARTA POMIAROWA POZIOMU HAŁASU. ")
                + java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("POMIAR W CZASIE ODNIESIENIA."), 150f, 30f);
        g2d.setFont(new Font(Font.SERIF, 1, 16));
        g2d.drawString(this.presentationTitle, 150f, 45f);

        // logo programu NMM --------------------------------------------------
        try {
             img = ImageIO.read(new File("nmm_big.png"));
             g2d.drawImage(img, 65,20, 76, 25,null);       
        } catch (IOException e) {
             System.out.println("Nie odnaleziono loga programu NMM.");
        }

        // MAIN CHART ---------------------------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("ZAPIS ZMIAN POZIOMU HAŁASU W PUNKCIE POMIAROWYM"),
                65f, 80);
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("LEGENDA:"), 65f, 675f);
        g2d.drawLine(70,690, 90, 690);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.setColor(Color.BLACK);
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("WARTOSCI POZIOMU EKWIWALENTNEGO W OKRESACH ")
                + java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("1-SEKUNDOWYCH"), 110f, 692f);                
        NMMChartPainter nmmChP;
        nmmChP = new NMMChartPainter(g2d, nmmProj, pageFormat, this.startTime,
                this.endTime, 1, 100,  
                (int)pageFormat.getImageableWidth()-(int)metricColumnOffset+70, 
                100, 200, this.nsm, true, false, this.locale);

        // METRIC COLUMN ------------------------------------------------------
        // identyfikator karty pomirowej
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLACK);
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("KARTA:"), metricColumnOffset, 40f);
        g2d.setFont(new Font(Font.SERIF, 1, 18));
        g2d.setColor(Color.BLUE);
        g2d.drawString(this.getPresentationTitle(), metricColumnOffset+65, 40f);

        // charakterystyka projektu -------------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("PROJEKT"),
                metricColumnOffset, 75);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.setColor(Color.BLACK);
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("TYTUŁ:"), metricColumnOffset, 90f);
        g2d.drawString(this.nmmProj.getProjectTitle(), metricColumnOffset+65, 90f);
        g2d.drawString(this.nmmProj.getSubtitle(),metricColumnOffset+65, 105f);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("OPRACOWANIE: "), metricColumnOffset, 125f);
        g2d.drawString(this.nmmProj.getProjectAuthor(), metricColumnOffset+65, 125f);
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("RAPORT NR:"),metricColumnOffset, 140f);
        g2d.drawString(this.nmmProj.getReportNumber(),metricColumnOffset+65,140f);
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("ZLECENIE:"),metricColumnOffset, 155f);
        g2d.drawString(this.nmmProj.getProjectCommisionNumber(), metricColumnOffset+65, 155f);
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("UWAGI:"), metricColumnOffset, 170f);
        NMMPresentationTextPainter nmmtp0 = new NMMPresentationTextPainter(g2d,
                pageFormat, (int) (metricColumnOffset + 65), 170, 40, 170, this.nmmProj);
        nmmtp0.drawText(this.nmmProj.getProjectRemarks());
                
        // charakterystyka zapisu historii czasowej ---------------------------

        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("POMIAR"),
                metricColumnOffset, 205);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.setColor(Color.BLACK);        

        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("OPERATOR:"), metricColumnOffset, 225f);
        g2d.drawString(this.nmmProj.getMeasurement(this.nsm.getMeasurementUUID())
                .getOperator(),
                metricColumnOffset+65, 225f);

        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("START:"), metricColumnOffset, 240f);
        g2d.drawString(TimeConverter.LongToDateString(
                this.startTime,DateFormat.LONG, locale),metricColumnOffset+65,240f);
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("KONIEC:"), metricColumnOffset+145, 240f);
        g2d.drawString(TimeConverter.LongToDateString(
                this.endTime,DateFormat.LONG, locale),metricColumnOffset+65+130,240f);
        
        // wyniki pomiaru -----------------------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);        
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("WYNIKI POMIARU:"), metricColumnOffset, 265f);
        g2d.setFont(new Font(Font.SERIF, 1, 12));
        g2d.setColor(Color.RED);
        
        // te wartości maja być pobierane z przeliczonego modelu                                
        double laeqd=this.nsm.getNoiseModelResult(NoiseLevelIndicators.LAeqD);
        double laeqn=this.nsm.getNoiseModelResult(NoiseLevelIndicators.LAeqN);
        
        if (!((Double.isNaN(laeqd)) || (Double.isInfinite(laeqd)))) {
            g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("LAEQD:"), metricColumnOffset+65, 280f);
            g2d.drawString(NMMToolbox.formatDouble(laeqd)+java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString(" DB"), metricColumnOffset+110, 280f);
        }                
        if (!((Double.isNaN(laeqn)) || (Double.isInfinite(laeqn)))) {
            g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("LAEQN:"), metricColumnOffset+200, 280f);
            g2d.drawString(NMMToolbox.formatDouble(laeqn)+java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString(" DB"), metricColumnOffset+245, 280f);
        }                
        
        // informacja o mierniku poziomu dźwieku ------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("APARATURA POMIAROWA."),
                metricColumnOffset, 305);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("MIERNIK:"), metricColumnOffset, 320f);
        nmmtp0 = new NMMPresentationTextPainter(g2d,
                pageFormat, (int) (metricColumnOffset + 65), 350, 40, 320, this.nmmProj);
        nmmtp0.drawText(this.nmmProj.getMeasurement(this.nsm.getMeasurementUUID()).getMeasurementSet().getMeasurementSetDescription());

        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("WZORCOWANIE"), metricColumnOffset, 355f);
        NMMPresentationTextPainter nmmtp1 = new NMMPresentationTextPainter(g2d,
                pageFormat, (int) (metricColumnOffset + 65), 300, 40, 355, this.nmmProj);
        nmmtp1.drawText(this.nmmProj.getMeasurement(this.nsm.getMeasurementUUID()).getMeasurementSet().getMeasurementSetDescription());

        // kalibracja sprzętu pomiarowego -------------------------------------
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("KALIBRACJA:"), metricColumnOffset, 410);
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("PRZED POMIAREM: "), metricColumnOffset, 425);
        g2d.drawString(NMMToolbox.formatDouble(this.nmmProj.getMeasurement(this.nsm.getMeasurementUUID()).
                getInitialCalibration()), metricColumnOffset+65, 425);
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("PO POMIARZE: "), metricColumnOffset, 435);
        g2d.drawString(NMMToolbox.formatDouble(this.nmmProj.getCurrentMeasurement()
                .getFinalCalibration()), metricColumnOffset+65, 435);

        // picture section ----------------------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("FOTOGRAFIA PUNKTU POMIAROWEGO"),
                metricColumnOffset, 460);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(Font.SERIF, 1, 8));

        if (this.nmmProj.getMeasurement(this.nsm.getMeasurementUUID()).getPicture()!=null) {
            Image tmpImage=this.nmmProj.getMeasurement(this.nsm.getMeasurementUUID()).getPicture().
                getImage();
            g2d.drawImage(tmpImage,(int)metricColumnOffset+65,470,(int)metricColumnOffset+275,610,0,0,tmpImage.getWidth(null),
                tmpImage.getHeight(null),null);            
        }                
        
        // uwagi do pomiaru ---------------------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("LOKALIZACJA PUNKTU POMIAROWEGO"), metricColumnOffset, 625);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.setColor(Color.BLACK);
        g2d.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("WSPÓŁRZĘDNE:    X=")+NMMToolbox.formatDouble(this.nmmProj
                .getMeasurement(this.nsm.getMeasurementUUID()).getCoordinateX())+java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("M    Y=")+
                NMMToolbox.formatDouble(this.nmmProj
                .getMeasurement(this.nsm.getMeasurementUUID()).getCoordinateY())
                +java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("M     H=")+NMMToolbox.formatDouble(
                this.nmmProj.getMeasurement(this.nsm.getMeasurementUUID()).getHeight())+java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations",locale).getString("M"),metricColumnOffset, 640);
        NMMPresentationTextPainter nmmtp = new NMMPresentationTextPainter(g2d, 
                pageFormat, (int) metricColumnOffset, 60, 40, 655, this.nmmProj);
        nmmtp.drawText(this.nmmProj.getMeasurement(this.nsm.getMeasurementUUID()).getRemarks());
        
        // stopka -------------------------------------------------------------        
        Stroke stroke = new BasicStroke(0.2f);
        g2d.setStroke(stroke);
        g2d.drawLine(this.BINDING_AREA_WIDTH,
                (int)pageFormat.getImageableHeight()-20,
                (int)pageFormat.getImageableWidth(),
                (int)pageFormat.getImageableHeight()-20);

        g2d.drawString("Noise Measurement Manager (c)2007-2015 Ekoprojekt http://www.ekoprojekt.biz/", 65,
                (int)pageFormat.getImageableHeight()-7);
        try {
            System.out.println(this.nmmSetup.getProperty("NMM_SETUP_LOGO", "ecoplan.png"));
            img = ImageIO.read(new File(this.nmmSetup.getProperty("NMM_SETUP_LOGO", "ecoplan.png")));
            g2d.drawImage(img,(int)pageFormat.getImageableWidth()-90,(int)pageFormat.getImageableHeight()-18,
                    77, 14,null);
         } catch (IOException e) {
             System.out.println("Nie odnaleziono loga firmy ...");
         }               
        return PAGE_EXISTS;
    }

//    public void setNoiseIndicator(int _ni) {
//        this.noiseIndicator=_ni;
//    }


    @Override
    public void setPresentationTitle(String _presentationTitle) {
        this.presentationTitle=_presentationTitle;
    }

    @Override
    public String getPresentationTitle() {
        return this.presentationTitle;
    }

    @Override
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

    @Override
    public PageFormat getPageFormat() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return this.presentationTitle;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public boolean containsNoiseSourceModel() {
        return true;
    }

    @Override
    public NoiseSourceModelType getNoiseSourceModelType() {
        return this.nsm.getNoiseModelType();
    }

    @Override
    public NMMNoiseSourceModel getNoiseSourceModel() {
        return this.nsm;
    }

    @Override
    public void setNoiseSourceModel(NMMNoiseSourceModel _sm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PresentationType getPresentationType() {
        return PresentationType.DAY_AND_NIGHT;
    }

}
