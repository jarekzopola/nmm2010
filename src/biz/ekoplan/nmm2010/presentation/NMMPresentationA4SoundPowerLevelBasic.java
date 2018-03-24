/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.presentation;

import biz.ekoplan.nmm2010.charts.NMMChartPainter;
import biz.ekoplan.nmm2010.charts.NMMCumulativeCurvePainter;
import biz.ekoplan.nmm2010.charts.NMMEventsTablePainter;
import biz.ekoplan.nmm2010.charts.NMMHistogramPainter;
import biz.ekoplan.nmm2010.charts.NMMPresentationTextPainter;
import biz.ekoplan.nmm2010.enums.NoiseLevelIndicators;
import biz.ekoplan.nmm2010.enums.NoiseSourceModelType;
import biz.ekoplan.nmm2010.enums.PresentationType;
import biz.ekoplan.nmm2010.presentations.NMMPresentation;
import biz.ekoplan.nmm2010.surcemodel.NMMNoiseSourceModel;
import biz.ekoplan.nmm2010.surcemodel.NMMSoundPowerLevelModel;
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
public class NMMPresentationA4SoundPowerLevelBasic implements Printable, NMMPresentation, Serializable {

    static final long serialVersionUID =1L;
    
    String presentationTitle;
    //PageFormat presentationPageFormat;
    
    NMMProject nmmProj;
    long startTime;
    long endTime;
    Setup nmmSetup;
    NMMSoundPowerLevelModel noiseSourceModel;
    Locale locale;

    public NMMPresentationA4SoundPowerLevelBasic(NMMProject _nmmProj, 
            long _stTime, long _endTime, Setup _nmmSetup, 
            NMMNoiseSourceModel _noiseSourceModel,
            Locale _loc) {
        nmmProj=_nmmProj;               
        this.startTime=_stTime;
        this.endTime=_endTime;
        this.nmmSetup=_nmmSetup;            
        this.noiseSourceModel=(NMMSoundPowerLevelModel)_noiseSourceModel;
        this.locale=_loc;
    }

    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {

        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Image img;
        Graphics2D g2d = (Graphics2D)g;

        g2d.translate(pageFormat.getImageableX()+20, pageFormat.getImageableY()+30);
        //System.out.println("Translacja układu strony druku do dX="+pageFormat.getImageableX()+" dY="+pageFormat.getImageableY());
        g2d.drawRect((int)pageFormat.getImageableX()+50, (int)pageFormat.getImageableY()+30,
                (int)pageFormat.getImageableWidth()-70, (int)pageFormat.getImageableHeight()-60);        
        
        // Nagłowek karty (część opisowa) -------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.drawString("Sound power level (LW) measurement chart", 150f, 50f);

        g2d.setFont(new Font(Font.SERIF, 1, 16));
        g2d.drawString(this.presentationTitle, 150f, 65f);
        
        // charakterystyka projektu -------------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        
        g2d.drawString("Project:", 65f, 90f);
        g2d.drawString(this.nmmProj.getProjectTitle(), 130f, 90f);
        g2d.drawString(this.nmmProj.getSubtitle(),130f, 105f);

        g2d.drawString("Report #:",420f, 65f);
        g2d.drawString(this.nmmProj.getReportNumber(),480f,65f);
        g2d.drawString("Commision:",420f, 50f);
        g2d.drawString(this.nmmProj.getProjectCommisionNumber(), 480f, 50f);

        // linia oddzielająca nagłówek karty pomiarowej
        Stroke stroke = new BasicStroke(0.2f);
        g2d.setStroke(stroke);
        g2d.drawLine((int)pageFormat.getImageableX()+50,
                (int)pageFormat.getImageableHeight()-765,
                (int)pageFormat.getImageableWidth()-20,
                (int)pageFormat.getImageableHeight()-765);

        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.drawString("Author: ", 65f, 125f);
        g2d.drawString(this.nmmProj.getProjectAuthor(), 130f, 125f);

        // kalibracja sprzętu pomiarowego -------------------------------------
        
        g2d.drawString("Calibration:", 420f, 95);
        g2d.drawString("Before measurement: ", 420f, 110);
        g2d.drawString(NMMToolbox.formatDouble(this.nmmProj.getCurrentMeasurement().
                getInitialCalibration()), 485f, 110);
        g2d.drawString("After measurement: ", 420f, 125);
        g2d.drawString(NMMToolbox.formatDouble(this.nmmProj.getCurrentMeasurement()
                .getFinalCalibration()), 485f, 125);

        // charakterystyka zapisu historii czasowej ---------------------------
        g2d.drawString("Remarks:", 65f, 140f);
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
        g2d.drawString(this.nmmProj.getCurrentMeasurement().getOperator()!=null ? (this.nmmProj.getCurrentMeasurement().getOperator()) : ("-"), 130f, 155f);

        g2d.drawString("Start:", 65f, 170f);
        g2d.drawString(TimeConverter.LongToTimeString(
                this.startTime,DateFormat.MEDIUM, this.locale),130f,170f);
        g2d.drawString("End:", 200f, 170f);
        g2d.drawString(TimeConverter.LongToTimeString(
                this.endTime,DateFormat.MEDIUM, this.locale),250f,170f);

        // informacja o mierniku poziomu dźwieku ------------------------------
        g2d.drawString("Sonometer:", 65f, 185f);
        NMMPresentationTextPainter nmmtp0 = new NMMPresentationTextPainter(g2d, pageFormat,
                130, 640, 40, 182, this.nmmProj);
        nmmtp0.drawText(this.nmmProj.getMeasurement(this.noiseSourceModel.getMeasurementUUID()).getMeasurementSet().getMeasurementSetDescription());
        
        g2d.drawString("Calibration", 65f, 205f);
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

        // linia rozdzielająca przentację ponad dok fotograficzną i tabelą
        // zdarzeń akustycznych (eventów)--------------------------------------
        g2d.setStroke(stroke);
        g2d.drawLine((int)pageFormat.getImageableX()+50,
                (int)pageFormat.getImageableHeight()-320,
                (int)pageFormat.getImageableWidth()-20,
                (int)pageFormat.getImageableHeight()-320);
        
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        
        // logo programu NMM --------------------------------------------------
        try {    
             img = ImageIO.read(new File("nmm_big.png"));
             g2d.drawImage(img,65,40, 76, 25,null);
         } catch (IOException e) {
             System.out.println("NMM logo not found.");
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
             System.out.println("Company logo not found...");
         }                
        
        // wykres zmian poziomu hałasu w czasie rejestracji pomiaru  
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString("Measurement history", 60, 250);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.setColor(Color.BLACK);                
        NMMChartPainter nmmChP = new NMMChartPainter(g2d,nmmProj,pageFormat, this.startTime, 
                this.endTime, 1,100, 50, 265, 420, this.noiseSourceModel, true, true, this.locale);
                       
        // tabela zdarzeń -----------------------------------------------------
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString("Acoustic events", 60, 460);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.setColor(Color.BLACK);
        NMMEventsTablePainter nmmca = new NMMEventsTablePainter(g2d, pageFormat,
                60, 60, 350, 480, this.nmmProj, this.locale);
        if (this.nmmProj.getEventsNumber()>2) {
            nmmca.drawTable();
        } else {
            // do something if there is events
        }
        
        //opis pomierzonego źródła dźwięku
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString("Noise source & measurement conditions", 320, 460);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.setColor(Color.BLACK);

        //dane źrodła hałasu (mierzonego urządzenia)
        g2d.drawString("Noise source name, type and model:", 320, 470);
        nmmtp0 = new NMMPresentationTextPainter(g2d, pageFormat,
                320, 620, 40, 480, this.nmmProj);
        nmmtp0.drawText(this.noiseSourceModel.getSoundSourceName()+ " / " +
                this.noiseSourceModel.getSoundSourceType()+" / "+
                this.noiseSourceModel.getSoundSourceModel());        
        //dane warunków pomiarowych
        nmmtp0 = new NMMPresentationTextPainter(g2d, pageFormat,
                320, 600, 40, 500, this.nmmProj);
        nmmtp0.drawText(this.noiseSourceModel.getNoiseSourceWorkDuringMeasurement());  
                
        //wyniki obliczeń mocy akustycznej i wartości pośrednich + ostrzeżenia
        g2d.setFont(new Font(Font.SERIF, 1, 10));
        g2d.setColor(Color.BLUE);
        g2d.drawString("Calculation results", 320, 600);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.setColor(Color.BLACK);
        
        g2d.drawString("Number of measurement locations: "+
                this.noiseSourceModel.getMeasurementPointsNumber(), 320, 620);
        g2d.drawString("Average noise level on measurement surface: "
                +this.noiseSourceModel.getAverageNoiseLevel(), 320, 640);
        g2d.drawString("Average background level on measurement surface: "+
                this.noiseSourceModel.getAverageBackgroundLevel(), 320, 680);
        g2d.drawString("Average netto noise level on measurement surface: "+
                this.noiseSourceModel.getAverageNettoNoiseLevel(), 320, 700);
        g2d.drawString("Sound power level uncertainty: "+
                this.noiseSourceModel.getModelUncertainty(), 320, 720);
        g2d.setFont(new Font(Font.SERIF, 1, 16));
        g2d.setColor(Color.RED);
        g2d.drawString("Sound power level (SPL): "+
                NMMToolbox.formatDouble(this.noiseSourceModel.getNoiseModelResult(NoiseLevelIndicators.LW)), 320, 770);
        g2d.setFont(new Font(Font.SERIF, 1, 8));
        g2d.setColor(Color.BLACK);
        
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

    @Override
    public boolean containsNoiseSourceModel() {
        return false;
    }

    @Override
    public NoiseSourceModelType getNoiseSourceModelType() {
        return null;
    }

    @Override
    public NMMNoiseSourceModel getNoiseSourceModel() {
        return null;
    }

    @Override
    public void setNoiseSourceModel(NMMNoiseSourceModel _sm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PresentationType getPresentationType() {
        return PresentationType.A4_SOUNDPOWERLEVEL_BASIC;
    }

}
