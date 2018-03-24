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
import biz.ekoplan.nmm2010.surcemodel.NMMSimpleModel;
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
import nmm2010.NMMProject;
import nmm2010.Setup;

/**
 *
 * @author Jarek
 */
public class NMMPresentationSimpleMeasurement implements Printable, NMMPresentation, Serializable {

    static final long serialVersionUID =1L;
    
    String presentationTitle;
    //PageFormat presentationPageFormat;
    
    NMMProject nmmProj;
    long startTime;
    long endTime;
    Setup nmmSetup;
    NMMSimpleModel noiseSourceModel;
    Locale locale;

    public NMMPresentationSimpleMeasurement(NMMProject _nmmProj, long _stTime, long _endTime, Setup _nmmSetup,
            NMMSimpleModel _nmmEventType, Locale _locale) {
        nmmProj=_nmmProj;                       
        this.nmmSetup=_nmmSetup;
        this.noiseSourceModel=_nmmEventType;
        this.startTime=this.noiseSourceModel.getStartTime();
        this.endTime=this.noiseSourceModel.getEndTime();
        this.locale=_locale;
    }

    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {

        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Image img;
        Graphics2D g2d = (Graphics2D)g;

        g2d.translate(pageFormat.getImageableX()+20, pageFormat.getImageableY()+30);
        System.out.println("Translacja układu strony druku do dX="+pageFormat.getImageableX()+" dY="+pageFormat.getImageableY());
        g2d.drawRect((int)pageFormat.getImageableX()+50, (int)pageFormat.getImageableY()+30,
                (int)pageFormat.getImageableWidth()-70, (int)pageFormat.getImageableHeight()-60);        
        
        // Nagłowek karty (część opisowa) -------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.drawString("Karta pomiarowa poziomu hałasu", 150f, 50f);

        g2d.setFont(new Font(Font.SERIF, 1, 16));
        g2d.drawString(this.presentationTitle, 150f, 65f);
        
        // charakterystyka projektu -------------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        
        g2d.drawString("Projekt:", 65f, 90f);
        g2d.drawString(this.nmmProj.getProjectTitle(), 130f, 90f);
        g2d.drawString(this.nmmProj.getSubtitle(),130f, 105f);

        g2d.drawString("Raport nr:",420f, 65f);
        g2d.drawString(this.nmmProj.getReportNumber(),480f,65f);
        g2d.drawString("Zlecenie:",420f, 50f);
        g2d.drawString(this.nmmProj.getProjectCommisionNumber(), 480f, 50f);

        // linia oddzielająca nagłówek karty pomiarowej
        Stroke stroke = new BasicStroke(0.2f);
        g2d.setStroke(stroke);
        g2d.drawLine((int)pageFormat.getImageableX()+50,
                (int)pageFormat.getImageableHeight()-765,
                (int)pageFormat.getImageableWidth()-20,
                (int)pageFormat.getImageableHeight()-765);

        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.drawString("Opracował: ", 65f, 125f);
        g2d.drawString(this.nmmProj.getProjectAuthor(), 130f, 125f);

        // kalibracja sprzętu pomiarowego -------------------------------------
        
        g2d.drawString("Kalibracja:", 420f, 95);
        g2d.drawString("Przed pomiarem: ", 420f, 110);
        g2d.drawString(NMMToolbox.formatDouble(this.nmmProj.getCurrentMeasurement().
                getInitialCalibration()), 485f, 110);
        g2d.drawString("Po pomiarze: ", 420f, 125);
        
        g2d.drawString(NMMToolbox.formatDouble(this.nmmProj.getMeasurement(this.noiseSourceModel.getMeasurementUUID())
                .getFinalCalibration()), 485f, 125);

        // charakterystyka zapisu historii czasowej ---------------------------
        g2d.drawString("Uwagi:", 65f, 140f);
        String tmpRemarks=this.nmmProj.getProjectRemarks().toString()+"...";
        if (this.nmmProj.getProjectRemarks()!=null) {
            if (g2d.getFontMetrics().stringWidth(this.nmmProj.getProjectRemarks())>430) {
                while (g2d.getFontMetrics().stringWidth(tmpRemarks+"...")>430) {
                    tmpRemarks=tmpRemarks.substring(0, tmpRemarks.length()-1);
                }
                tmpRemarks=tmpRemarks + "...";
            } else {
                tmpRemarks=this.nmmProj.getProjectRemarks();
            }
        g2d.drawString(tmpRemarks, 130f, 140f);
        }
        
        g2d.drawString("Operator:", 65f, 155f);
        g2d.drawString(this.nmmProj.getMeasurement(this.noiseSourceModel.getMeasurementUUID()).getOperator()!=null ? (this.nmmProj.getCurrentMeasurement().getOperator()) : ("-"), 130f, 155f);

        g2d.drawString("Start:", 65f, 170f);
        g2d.drawString(TimeConverter.LongToTimeString(
                this.startTime,DateFormat.MEDIUM,locale),130f,170f);
        g2d.drawString("Koniec:", 200f, 170f);
        g2d.drawString(TimeConverter.LongToTimeString(
                this.endTime,DateFormat.MEDIUM, locale),250f,170f);

        // informacja o mierniku poziomu dźwieku ------------------------------
        g2d.drawString("Miernik:", 65f, 185f);
        NMMPresentationTextPainter nmmtp0 = new NMMPresentationTextPainter(g2d, pageFormat,
                130, 640, 40, 182, this.nmmProj);
        nmmtp0.drawText(this.nmmProj.getMeasurement(this.noiseSourceModel.getMeasurementUUID()).getMeasurementSet().getMeasurementSetDescription());

        
        g2d.drawString("Wzorcowanie", 65f, 205f);
        NMMPresentationTextPainter nmmtp1 = new NMMPresentationTextPainter(g2d, pageFormat,
                130, 620, 40, 205, this.nmmProj);
        nmmtp1.drawText(this.nmmProj.getMeasurement(this.noiseSourceModel.getMeasurementUUID()).getMeasurementSet().getMeasurementSetDescription());        
        
        // linia rozdzielająca przentację ponad wykresem zapisu zmian poziomu
        // hałasu w środowisku ------------------------------------------------
        g2d.setStroke(stroke);
        g2d.drawLine((int)pageFormat.getImageableX()+50,
                (int)pageFormat.getImageableHeight()-610,
                (int)pageFormat.getImageableWidth()-20,
                (int)pageFormat.getImageableHeight()-610);

        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString("Zapis zmian poziomu hałasu w punkcie", 65f, 246);
        g2d.setColor(Color.BLACK);

        // linia rozdzielająca przentację ponad dok fotograficzną i tabelą
        // zdarzeń akustycznych (eventów)--------------------------------------
        g2d.setStroke(stroke);
        g2d.drawLine((int)pageFormat.getImageableX()+50,
                (int)pageFormat.getImageableHeight()-360,
                (int)pageFormat.getImageableWidth()-20,
                (int)pageFormat.getImageableHeight()-360);
        
        g2d.setFont(new Font(Font.SERIF, 1, 8));

        // dokumentacja fotograficzna punktu pomiarowego ----------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString("Fotografia punktu pomiarowego", 65, 500);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        if (this.nmmProj.getCurrentMeasurement().getPicture()!=null) {
            g2d.drawImage(this.nmmProj.getMeasurement(this.noiseSourceModel.getMeasurementUUID()).getPicture().
                    getImage(),65,510,210,140,null);
        }

        // uwagi do pomiaru ---------------------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString("Opis punktu pomiarowego:", 65, 665);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Współrzędne: X="+NMMToolbox.formatDouble(this.nmmProj.getMeasurement(this.noiseSourceModel.getMeasurementUUID()).getCoordinateX(), "#0.0000")+" Y="+
                NMMToolbox.formatDouble(this.nmmProj.getMeasurement(this.noiseSourceModel.getMeasurementUUID()).getCoordinateY(), "#0.0000")
                +" h="+NMMToolbox.formatDouble(
                this.nmmProj.getMeasurement(this.noiseSourceModel.getMeasurementUUID()).getHeight()),65, 678);
        NMMPresentationTextPainter nmmtp = new NMMPresentationTextPainter(g2d, pageFormat,
                65, 60, 320, 695, this.nmmProj);
        nmmtp.drawText(this.nmmProj.getCurrentMeasurement().getRemarks());

        // logo programu NMM --------------------------------------------------
        try {    
             img = ImageIO.read(new File("nmm_big.png"));
             g2d.drawImage(img,65,40, 76, 25,null);
         } catch (IOException e) {
             System.out.println("Nie odnaleziono loga programu NMM. Poszukiwano:");
         }

        // stopka -------------------------------------------------------------
        
        g2d.setStroke(stroke);

        g2d.drawLine((int)pageFormat.getImageableX()+50,
                (int)pageFormat.getImageableHeight()-50,
                (int)pageFormat.getImageableWidth()-20,
                (int)pageFormat.getImageableHeight()-50);
        
        g2d.drawString(this.nmmProj.getManufacturer(), 65,
                (int)pageFormat.getImageableHeight()-37);
        try {
             img = ImageIO.read(new File(this.nmmSetup.getProperty("NMM_SETUP_LOGO", "ecoplan.png")));
             g2d.drawImage(img,485,(int)pageFormat.getImageableHeight()-48,
                     77, 14,null);
         } catch (IOException e) {
             System.out.println("Nie odnaleziono loga Ecoplan...");
         }
        
        // wykres zmian poziomu hałasu w czasie rejestracji pomiaru        
        NMMChartPainter nmmChP = new NMMChartPainter(g2d,nmmProj,pageFormat, this.startTime, 
                this.endTime, 1,100, 50, 265, 395, this.noiseSourceModel, true, true, this.locale);
        
        // tabela zdarzeń -----------------------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString("Próbki / zdarzenia akustyczne", 320, 500);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.setColor(Color.BLACK);
        NMMEventsTablePainter nmmca = new NMMEventsTablePainter(g2d, pageFormat,
                320, 70, 40, 510, this.nmmProj, this.locale);
        if (this.nmmProj.getEventsNumber()>2) {
            nmmca.drawSelectiveTable(this.noiseSourceModel.getEventType(),
                    this.noiseSourceModel.getMeasurementUUID());
        } else {
            // do something if there is events
        }
        nmmca=null;        
        return PAGE_EXISTS;
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
        return false;
    }

    public NoiseSourceModelType getNoiseSourceModelType() {
        return null;
    }

    public NMMNoiseSourceModel getNoiseSourceModel() {
        return null;
    }

    public void setNoiseSourceModel(NMMNoiseSourceModel _sm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PresentationType getPresentationType() {
        return PresentationType.SIMPLE_PRESENTATION;
    }

}
