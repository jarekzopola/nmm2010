/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.surcemodel;

import biz.ekoplan.nmm2010.enums.NoiseLevelIndicators;
import biz.ekoplan.nmm2010.enums.NoiseModelResult;
import biz.ekoplan.nmm2010.enums.NoiseSourceModelType;
import biz.ekoplan.nmm2010.surcemodel.result.NMMNoiseSourceModelResult;
import biz.ekoplan.nmm2010.toolbox.NMMPoint3D;
import biz.ekoplan.nmm2010.toolbox.NMMToolbox;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import nmm2010.NMMCalculationReport;
import nmm2010.NMMEventType;
import nmm2010.NMMNoiseCalculator;
import nmm2010.NMMProject;

/**
 *
 * @author Jarek
 */
public class NMMSoundPowerLevelModel implements NMMNoiseSourceModel, Serializable, 
        NMMNoiseSourceModelInputDataChangedListener {

    final boolean DEBUG = false;

    //opisy i dane tekstowe    
    private String soundSourceName="-";
    private String soundSourceType="-";
    private String soundSourceModel="-";
    private String soundSourceDimension="-";
    private String soundSourceManufacturer="-";
    private String soundSourceYear="-";
    private String soundSourceSerialNumber="-";   
    
    private String noiseSourceWorkingConditions="-";
    private String noiseSourceMounting="-";
    private String measurementEnvironment="-";
    private String noiseSourceWorkDuringMeasurement="-";
        
    //identyfikator zapisu zmian poziomu dźwięku
    private UUID measurementMUID;
    
    //nazwa opisowa modelu (przyjazna dla użytkownika)
    private String modelName="New sound power level calculation model";
    
    //ostateczny wynik modelu obliczeniowego - moc akustyczna
    private double soundPowerLevel=0;
    
    //współczynnik alfa
    private double alfa;
    
    private double Sv;
    private double A;
    private double measurementTime=0;
    
    private double avgn;
    private double avgt;
    private double avsnl;
    
    //pole powierzchni pomiarowej
    private double S;
    
    //ostrzeżenia z procesu przeliczania modelu
    String warnings;
    
    //raport z obliczen
    NMMCalculationReport calcReport = new NMMCalculationReport();
    
    //wymiary prostokątu odniesienia
    private double l1=2;
    private double l2=2;
    private double l3=2;
    
    //odległość powierzchni pomiarowej od prostopadłościanu odniesienia
    private double d=1;
    
    //wymiary powierzchni pomiarowej
    private double a;
    private double b;
    private double c;
    
    //liczba segmentów powierzchni pomiarowej
    private double sA;
    private double sB;
    private double sC;
    
    //krok na powierzchni pomiarowej
    private double kA;
    private double kB;
    private double kC;
    
    /**wymiar l1 przestrzeni zamkniętej w jakiej zlokalizowane jest źródło hałasu
     * 
     */
    private double P_l1=10;    
    private double P_l2=10;
    private double P_l3=10;
    
    private double K1A=0;
    private double K2A=0;
    
    private NMMProject nmmProj;    
    private long startTime;
    private long endTime;
    
    private double measurementUncertainty=3;
    
    //rodzaj powierzchni pomiarowej: 
    private int measurementSurface;
    
    //rodzaj przestrzeni pomiarowej: 0 - otwarta, 1 - zamknieta
    private int measurementSpace=0;
    
    //liczba powierzchni odbijających w czasie badania
    //1 - tylko podloga
    //2 - podłoga + ściana
    //3 - podłoga + 2 ściany
    private int reflectiveSurfacesNumber=1;
        
    //zmienne wykorzystywane przy tworzeniu mapy 2D punktów pomiarowych
    double mapWidth;
    double mapHeight;
    double skala;
    int newMapWidth;
    int newMapHeight;
    int xPO;
    int yPO;
        
    ArrayList<NMMPoint3D> coordinates;
    private final ArrayList<Object> NMMNoiseSourceModelChangedListeners;
    private NMMPointLocationTableModel pltm;
    public String getAverageNettoNoiseLevel;
    
    public NMMSoundPowerLevelModel(NMMProject _nmmProj, UUID _muid, 
            long _startTime, long _endTime, NMMPointLocationTableModel _pltm) {
        
        this.soundPowerLevel = 0;
        this.pltm=_pltm;
        NMMNoiseSourceModelChangedListeners = new ArrayList<>();        
        this.nmmProj=_nmmProj;
        this.measurementMUID =_muid;        
        this.startTime=_startTime;
        this.endTime=_endTime;
        this.coordinates=new ArrayList<>();
        
        
        
        this.fireNoiseSourceModelChangedEvent(this);
    }
    
    public int getMeasurementPointsNumber() {
        return this.coordinates.size();
    }

    public String getNoiseSourceWorkingConditions() {
        return noiseSourceWorkingConditions;
    }

    public void setNoiseSourceWorkingConditions(String noiseSourceWorkingConditions) {
        this.noiseSourceWorkingConditions = noiseSourceWorkingConditions;
    }

    public String getNoiseSourceMounting() {
        return noiseSourceMounting;
    }

    public void setNoiseSourceMounting(String noiseSourceMounting) {
        this.noiseSourceMounting = noiseSourceMounting;
    }

    public String getMeasurementEnvironment() {
        return measurementEnvironment;
    }

    public void setMeasurementEnvironment(String measurementEnvironment) {
        this.measurementEnvironment = measurementEnvironment;
    }

    public String getNoiseSourceWorkDuringMeasurement() {
        return noiseSourceWorkDuringMeasurement;
    }

    public void setNoiseSourceWorkDuringMeasurement(String noiseSourceWorkDuringMeasurement) {
        this.noiseSourceWorkDuringMeasurement = noiseSourceWorkDuringMeasurement;
    }
    
    public void setSoundSourceType(String soundSourceType) {
        this.soundSourceType = soundSourceType;
    }

    public void setSoundSourceModel(String soundSourceModel) {
        this.soundSourceModel = soundSourceModel;
    }

    public void setSoundSourceDimension(String soundSourceDimension) {
        this.soundSourceDimension = soundSourceDimension;
    }

    public void setSoundSourceManufacturer(String soundSourceManufacturer) {
        this.soundSourceManufacturer = soundSourceManufacturer;
    }

    public void setSoundSourceYear(String soundSourceYear) {
        this.soundSourceYear = soundSourceYear;
    }

    public void setSoundSourceSerialNumber(String soundSourceSerialNumber) {
        this.soundSourceSerialNumber = soundSourceSerialNumber;
    }

    public String getSoundSourceName() {
        return soundSourceName;
    }

    public String getSoundSourceType() {
        return soundSourceType;
    }

    public String getSoundSourceModel() {
        return soundSourceModel;
    }

    public String getSoundSourceDimension() {
        return soundSourceDimension;
    }

    public String getSoundSourceManufacturer() {
        return soundSourceManufacturer;
    }

    public String getSoundSourceYear() {
        return soundSourceYear;
    }

    public String getSoundSourceSerialNumber() {
        return soundSourceSerialNumber;
    }
            
    @Override
    public void recreateListenersArray() {
        
    }

    @Override
    public NoiseSourceModelType getNoiseModelType() {
        return NoiseSourceModelType.SOUND_POWER_LEVEL_MODEL;
    }
    
    /**
     * Get model name (description, friendly for NMM user)
     */
    @Override
    public String getModelName() {
        return this.modelName;
    }

    /**
     * Set model name (description, friendly for NMM user)
     * @param _modelName String
     */
    @Override
    public void setModelName(String _modelName) {
        this.modelName=_modelName;
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public long getEndTime() {
        return this.endTime;
    }

    @Override
    public UUID getMeasurementUUID() {
        return this.measurementMUID;
    }

    @Override
    public double getNoiseModelResult(NoiseLevelIndicators _nli) {
                
        double res;        
        if (_nli==NoiseLevelIndicators.LW) {
            res=this.getSoundPowerLevel();
        } else {
            res=-1;
        }
        return res;
    }

    @Override
    public NMMNoiseSourceModelResult getNoiseModelComplexResult(NoiseModelResult _nli) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NMMCalculationReport getCalculationReport() {
        return this.calcReport;
    }
    
    public double getPointsMapScale(int _width, int _height) {
                
        //wymiary mapy w metrach (powierzchnia pomiarowa rozłożona na płasko
        //na mapie
        mapWidth=this.a+this.c*2+0.4;
        mapHeight=this.b+this.c*2+0.4;
                
        //szukamy wspólnej skali w zaleźności który bok mapy nie mieści się
        //bardziej w panelu do rysowania
        double wspZmieszczeniaW = mapWidth/_width;
        double wspZmieszczeniaH = mapHeight/_height;
        if (wspZmieszczeniaW<wspZmieszczeniaH) {
            //to dopasowujemy wysokość mapy do obrazu
            //skala to nowa ilość piksli na metr 
            skala=1/wspZmieszczeniaH;
        } else {
            //to dopasowujemy wserokość mapy do obrazu
            //skala to nowa ilość piksli na metr
            skala=1/wspZmieszczeniaW;
        }                
        return this.skala;
    }
    
    /**
     * Method draws map of measurement points in BufferedImage and returns it
     * @return BufferedImage
     */
    public BufferedImage getMeasurementPointsMap(int _width, int _height) {                
                  
        this.getPointsMapScale(_width, _height);
        
        //obliczamy wymiary nowego obszaru niezbędnego na narysowania mapy
        //która zmieści się w panelu bez zniekształcenia
        newMapWidth=(int)(mapWidth*skala);
        newMapHeight=(int)(mapHeight*skala);
        
        //tworzymy nowy obrazek buforowany o wymaganych rozmiarach i rysujemy
        //jego krawędzie
        BufferedImage pointsMap;
        pointsMap = new BufferedImage(newMapWidth,newMapHeight,BufferedImage.TYPE_INT_RGB);
        Graphics2D gr2d = (Graphics2D)pointsMap.getGraphics();        
        gr2d.setBackground(Color.white);
        gr2d.clearRect(0,0,newMapWidth,newMapHeight);
        
        gr2d.setColor(Color.BLACK);
        gr2d.drawRect(3,3,newMapWidth-6,newMapHeight-6);
        
        //rysowanie powierzchni odniesienia
        xPO=(int)((newMapWidth-l1*skala)/2);
        yPO=(int)((newMapHeight-l2*skala)/2);
        gr2d.drawRect(xPO,yPO,(int)(l1*skala),(int)(l2*skala));
        gr2d.drawString("Reference surface", xPO, yPO-6);
        
        //rysowanie powierzchni pomiarowych                
        gr2d.setColor(Color.GRAY);
        
        //powierzchnia ponad badanym obiektem        
        gr2d.drawRect(xPO-(int)(this.d*skala), yPO-(int)(this.d*skala), 
                (int)(l1*skala+(2*this.d*skala)), 
                (int)(l2*skala+(2*this.d*skala)));
        
        //po wschodniej stronie badanego obiektu
        gr2d.drawRect(xPO+(int)((this.d+this.l1)*skala), 
                yPO-(int)(this.d*skala), 
                (int)((this.l3+this.d)*skala), 
                (int)(skala*(this.l2+2*this.d)));
        
        //po zachodniej stronie badanego obiektu
        gr2d.drawRect(xPO-(int)((2*this.d+this.l3)*skala),
            yPO-(int)(this.d*skala),
            (int)((this.l3+this.d)*skala) ,
            (int)(skala*(this.l2+2*this.d)));
        
        //po północnej stronie badanego obiektu
        gr2d.drawRect(xPO-(int)(this.d*skala), yPO-(int)((2*this.d+this.l3)*skala), 
                (int)(skala*(this.l1+this.d*2)), (int)(skala*(this.d+this.l3)));        
        
        //po południowej stronie badanego obiektu
        gr2d.drawRect(xPO-(int)(this.d*skala), yPO+(int)((this.d+this.l2)*skala), 
                (int)(skala*(this.l1+this.d*2)), (int)(skala*(this.d+this.l3)));                 
        
        //rysujemy punkty pomiarowe na powierzchniach        
        gr2d.setColor(Color.BLUE);
        for (NMMPoint3D coordinate : this.coordinates) {            
            gr2d.drawOval(coordinate.getMapX(),coordinate.getMapY(),5,5);
            gr2d.drawString(coordinate.getLocSymbol(),coordinate.getMapX()+10,coordinate.getMapY()+10);
        }
        
        //zwracamy obrazek do obiektu interfejsu, który wstawi go sobie tam
        //gdzie będzie potrzebował.
        return pointsMap;        
    }
    
    
    public void rearrangeDataTable() {
                
        System.out.println("Układam na nowo tabelę punktów pomiarowych ...");                
        coordinates.clear();
        
        //liczymy wymiary powierzchni odniesienia
        a=l1+2*d;
        b=l2+2*d;
        c=l3+d;
        
        //liczymy wymiary powierzchni pomiarowej dla prostopadłościanu
        // - przypadek jednej powierzchni odbijającej               
        double _a=.5*l1+d;
        double _b=.5*l2+d;
        double _c=l3+d;        
        S=4*(_a*_b+_b*_c+_a*_c);        

        // - przypadek dwóch powierzchni odbijających
        // TODO:
        
        // - przypadek trzech powierzchni odbijajacych
        // TODO:
        
        //liczymy ...
        
        double sA1=(a/(3*d));
        if (sA1==Math.floor(a/(3*d))) {
            sA=sA1;
        } else {
            sA=Math.floor(sA1)+1;
        }
        double sB1=(b/(3*d));
        if (sB1==Math.floor(b/(3*d))) {
            sB=sB1;
        } else {
            sB=Math.floor(sB1)+1;
        }
        
        
        double sC1=(c/(3*d));
        if (sC1==Math.floor(c/(3*d))) {
            sC=sC1;
        } else {
            sC=Math.floor(sC1)+1;
        }
        
        //wyliczamy długośc boku segmentu
        kA=a/sA;
        kB=b/sB;
        kC=c/sC;                  
        
        NMMPoint3D p3d;
        
        //punkty na szerokości powierzchni pomiarowej
        for (int k_a=0; k_a<sA; k_a++) {
            for (int k_c=0; k_c<sC; k_c++) {
                double y=(kA/2)+(k_a*kA);
                double z=(kC/2)+(k_c*kC);
                p3d = new NMMPoint3D(0,y,z,0,0);
                coordinates.add(p3d);
                p3d = new NMMPoint3D(2*d+l2,y,z,0,0);
                coordinates.add(p3d);
            }
        }
        
        //punkty na długości powierzchni pomiarowej
        for (int k_b=0; k_b<sB; k_b++) {
            for (int k_c=0; k_c<sC; k_c++) {
                double x=(kB/2)+(k_b*kA);
                double z=(kC/2)+(k_c*kC);
                p3d = new NMMPoint3D(x,0,z,0,0);
                coordinates.add(p3d);
                p3d = new NMMPoint3D(x,2*d+l1,z,0,0);
                coordinates.add(p3d);
            }
        }
        
        //punkty na "suficie" powierzchni pomiarowej
        for (int k_a=0; k_a<sA; k_a++) {
            for (int k_b=0; k_b<sB; k_b++) {
                double x=(kA/2)+(k_a*kA);
                double y=(kB/2)+(k_b*kB);
                p3d = new NMMPoint3D(x,y,l3+d,0,0);
                coordinates.add(p3d);
            }
        }        
        
        int measurementPointsNumber=coordinates.size();                
        this.pltm.setNumberOfMeasurementPoints(measurementPointsNumber);
                        
        for (int k=0; k<coordinates.size(); k++) {
            //System.out.println("Wypełniam kolejny wiersz tabeli: "+k);
            this.pltm.setValueAt("P"+k, k, 0);
            this.pltm.setValueAt(coordinates.get(k).getX(), k, 1);
            this.pltm.setValueAt(coordinates.get(k).getY(), k, 2);            
            this.pltm.setValueAt(coordinates.get(k).getZ(), k, 3);            
        }         
        
        this.fireNoiseSourceModelChangedEvent(this);
        this.pltm.fireTableChanged(new TableModelEvent(this.pltm));
    }
    
    
    @Override
    public boolean recalculateModel() {
        
        boolean recalculated = true;
        
        System.out.println("Przeliczam model obliczeniowy mocy akustycznej ...");
                
        this.calcReport.purgeReport();
        
        //zaczynamy układać sprawozdanie z procesu obliczeniowego
        this.calcReport.addParagraph("<h1>Sound power level "
                + "calculation report</h1>");
        this.calcReport.addParagraph("<p>This report is based on "
                + "calculation procedure described in PN-EN ISO 3746:2011 standard.</p>");        
        this.calcReport.addParagraph("<p>Calculation model name: "
                +this.modelName+"</p>");        
        this.calcReport.addParagraph("<h2>Sound source</h2>");
        this.calcReport.addParagraph("<p>Sound source name: "+
                this.soundSourceName+"</p>");
        this.calcReport.addParagraph("<p>Sound source type: "+
                this.soundSourceType+"</p>");
        this.calcReport.addParagraph("<p>Sound source model: "+
                this.soundSourceModel+"</p>");
        this.calcReport.addParagraph("<p>Sound source dimensions: "+
                this.soundSourceDimension+"</p>");
        this.calcReport.addParagraph("<p>Sound source manucacturer: "+
                this.soundSourceManufacturer+"</p>");
        this.calcReport.addParagraph("<p>Sound source serial number: "+
                this.soundSourceSerialNumber+"</p>");
                
        //na początku zakładamy ze modelu w ogóle nie da się przeliczyć
        //i przypiszemy wynik 0
        this.soundPowerLevel=0;
        
        //w tej zmiennej przechowujemy wygenerowane przez model ostrzeżenia
        //co do danych wprowadzonych przez interfejs modelu (np. informacje o
        //nielogiczności danych itp.
        warnings="";
        
        coordinates.clear();
                
        //sprawdzamy czy pomieszczenie w jakim jest źródło spełnia
        //wymagania normy
        if (this.P_l1>(this.P_l3*3)) {
            warnings=warnings.concat("Pomieszczenie pomiarowe zbyt szerokie!\n");
            this.calcReport.addParagraph("<p>Width of measurement room too large!</p>");
        } else {
            warnings=warnings.concat("Szerokość pomieszczenia pomiarowego O.K.");
            this.calcReport.addParagraph("<p>Width of measurement room O.K.</p>");
        }
        
        if (this.P_l2>(this.P_l3*3)) {
            warnings=warnings.concat("Pomieszczenie pomiarowe zbyt długie!\n");
            this.calcReport.addParagraph("<p>Length of measurement room too large!</p>");
        } else {
            warnings=warnings.concat("Długość pomieszczenia pomiarowego O.K.");
            this.calcReport.addParagraph("<p>Length of measurement room O.K.</p>");
        }        
        
        Sv = 2*this.P_l1*this.P_l3+
                2*this.P_l1*this.P_l2+
                2*this.P_l2*this.P_l3;
        A = Sv * this.alfa;
        
        this.calcReport.addParagraph("<p>Sv="+Sv+"</p>");
        this.calcReport.addParagraph("<p>A="+A+"</p>");
        
        //liczymy wymiary powierzchni odniesienia
        a=l1+2*d;
        b=l2+2*d;
        c=l3+d;        
        this.calcReport.addParagraph("<h3>Reference surface size</h3>");
        this.calcReport.addParagraph("<p>a="+a+" b="+b+" c="+c+"</p>");
                
        //liczymy wymiary powierzchni pomiarowej dla prostopadłościanu        
        if (this.reflectiveSurfacesNumber==1) {
            // - przypadek jednej powierzchni odbijającej (tylko podłoga)       
            double _a=.5*l1+d;
            double _b=.5*l2+d;
            double _c=l3+d;        
            S=4*(_a*_b+_b*_c+_a*_c);
            this.calcReport.addParagraph("<p>S[m2]="+S+"</p>");
        } else if (this.reflectiveSurfacesNumber==2) {
            // - przypadek dwóch powierzchni odbijających (podłoga + 1 ściana)  
            double _a=.5*l1+d;
            double _b=.5*l2+d;
            double _c=l3+d;        
            S=4*(_a*_b+_b*_c+_a*_c);
            this.calcReport.addParagraph("<p>S[m2]="+S+"</p>");
        } else if (this.reflectiveSurfacesNumber==3) {
            // - przypadek trzech powierzchni odbijajacych (podłoga + 2 ściany)
            // TODO:
        }
               
        double sA1=(a/(3*d));
        if (sA1==Math.floor(a/(3*d))) {
            sA=sA1;
        } else {
            sA=Math.floor(sA1)+1;
        }
        double sB1=(b/(3*d));
        if (sB1==Math.floor(b/(3*d))) {
            sB=sB1;
        } else {
            sB=Math.floor(sB1)+1;
        }
        double sC1=(c/(3*d));
        if (sC1==Math.floor(c/(3*d))) {
            sC=sC1;
        } else {
            sC=Math.floor(sC1)+1;
        }
        
        //wyliczamy długośc boku segmentu
        kA=a/sA;
        kB=b/sB;
        kC=c/sC;    
        this.calcReport.addParagraph("<h3>Segment size:</h3>");
        this.calcReport.addParagraph("<p>kA="+kA+" kB="+kB+" kC="+kC+"</p>");     
                        
        //wyliczamy współczynnik K2A środowiska pomiarowego
        if (this.measurementSpace==0) {
            this.K2A=0;
        } else {
            this.K2A=10*Math.log10(1+4*(S/A));
        }
        this.calcReport.addParagraph("<p>K2A="+this.K2A+"</p>");
                
        NMMPoint3D p3d;
        int symbolsCounter=0;
        
        
        //punkty na szerokości powierzchni pomiarowej
        for (int k_a=0; k_a<sA; k_a++) {
            for (int k_c=0; k_c<sC; k_c++) {
                double y=(kA/2)+(k_a*kA);
                double z=(kC/2)+(k_c*kC);                
                p3d = new NMMPoint3D(0,y,z,this.xPO-(int)(skala*(this.d+z)),this.yPO+(int)(skala*(this.l2+this.d)-skala*y));
                p3d.setLocSymbol("W" + (++symbolsCounter));                
                coordinates.add(p3d);
                p3d = new NMMPoint3D(2*d+l2,y,z,
                        this.xPO+(int)(skala*(this.l2+this.d+z)),
                        this.yPO+(int)(skala*(this.l2+this.d)-(int)(y*skala))
                        );
                p3d.setLocSymbol("E" + (++symbolsCounter));
                coordinates.add(p3d);
            }
        }        
        
        //punkty na długości powierzchni pomiarowej
        for (int k_b=0; k_b<sB; k_b++) {
            for (int k_c=0; k_c<sC; k_c++) {
                double x=(kB/2)+(k_b*kB);
                double z=(kC/2)+(k_c*kC);
                p3d = new NMMPoint3D(x,0,z,this.xPO-(int)(skala*this.d)+(int)(skala*x),this.yPO+(int)((this.d+this.l2)*skala)+(int)(skala*z));
                p3d.setLocSymbol("S" + (++symbolsCounter));
                coordinates.add(p3d);
                p3d = new NMMPoint3D(x,2*d+l1,z,this.xPO-(int)(skala*this.d)+(int)(skala*x),this.yPO-(int)(this.d*skala)-(int)(skala*z));
                p3d.setLocSymbol("N" + (++symbolsCounter));
                coordinates.add(p3d);
            }
        }
        
        //punkty na "suficie" powierzchni pomiarowej
        for (int k_a=0; k_a<sA; k_a++) {
            for (int k_b=0; k_b<sB; k_b++) {
                double x=(kA/2)+(k_a*kA);
                double y=(kB/2)+(k_b*kB);
                p3d = new NMMPoint3D(x,y,l3+d,
                        this.xPO-(int)(this.d*skala)+(int)(x*skala),
                        this.yPO-(int)(this.d*skala)+(int)(y*skala));
                p3d.setLocSymbol("S" + (++symbolsCounter));
                coordinates.add(p3d);
            }
        }
        
        int measurementPointsNumber=coordinates.size();                                                        
        this.calcReport.addParagraph("<p>Number of required measurement locations: "+coordinates.size()+"</p>");
        
        //obliczenie poziomu średniego hałasu i tła w punktach pomiarowych
        float pavgn[] = new float[measurementPointsNumber];
        float pavgt[] = new float[measurementPointsNumber];
        this.calcReport.addParagraph("<table border=1><tr><td>X[m]</td><td>Y[m]</td><td>Z[m]</td><td>Noise level[dB]</td><td>Background noise[dB]</td></tr>");
        for (int np=0; np<measurementPointsNumber;np++) {
            pavgn[np]=(float)this.nmmProj.getLeqLogAverage((NMMEventType)this.pltm.getValueAt(np, 4), this.measurementMUID);
            pavgt[np]=(float)this.nmmProj.getLeqLogAverage((NMMEventType)this.pltm.getValueAt(np, 5), this.measurementMUID);                        
            this.calcReport.addParagraph("<tr><td>"+this.coordinates.get(np).getX()+"</td><td>"+this.coordinates.get(np).getY()
                    +"</td><td>"+this.coordinates.get(np).getZ()+"</td><td>"+
                    NMMToolbox.formatDouble(pavgn[np])+"</td><td>"+
                    NMMToolbox.formatDouble(pavgt[np])+"</td></tr>");
        }
        this.calcReport.addParagraph("</table>");
        //obliczanie śrdniego poziomuhałasu i średniego poziomu tła przy
        //źródle hałasu

        avgn=NMMNoiseCalculator.SredniaLog(pavgn);
        avgt=NMMNoiseCalculator.SredniaLog(pavgt);
        
        this.calcReport.addParagraph("<p>Average background "
                + "noise level: "+NMMToolbox.formatDouble(avgt)+"dB</p>");
        this.calcReport.addParagraph("<p>Average noise level: "
                +NMMToolbox.formatDouble(avgn)+"dB</p>");
        
        //jezeli tło jest conajmniej 10dB niższe niz hałas to K1A = 0 a inaczej
        //to ...
        if ((avgn-avgt)>10) {
            this.K1A=0;
        } else if ((avgn-avgt)<3){
            this.K1A=-1;
            warnings=warnings.concat("Difference between background noise and measured noise is below 3dB!\n");
            this.calcReport.addParagraph("<p>Difference between background noise and measured noise is below 3dB!</p>");
        } else {
            this.K1A=-10*Math.log10(1-Math.pow((double)10,(-0.1*(avgn-avgt))));
        }
        this.calcReport.addParagraph("<p>K1A = "+this.K1A+"</p>");
        
        //obliczemy średni poziom hałasu na powierzchni pomiarowej
        double avsnl = avgn-this.K1A-this.K2A;
        this.calcReport.addParagraph("<p>Average noise level on "
                + "measurement surface = "+
                NMMToolbox.formatDouble(avsnl)+"</p>");
                
        //obliczemy finalny poziom mocy akustycznej (to co będzie zwracane przez
        //model obliczeniowy (cel obliczeń)
        this.soundPowerLevel=avsnl+10*Math.log10(this.S);
        this.calcReport.addParagraph("<p>Sound power level (SPL) = "+
                NMMToolbox.formatDouble(this.soundPowerLevel)+"</p>");
        
        //kończymy pisać raport z obliczeń
        this.calcReport.addParagraph("<p>----------------------------------------------<br>"
                + "NMM 2014 (c) Ekoprojekt www.nmm.org</p>");        
        
        //powiadamiamy słuchacy tego modelu, że model został przeliczony
        //i może być wymagane przeliczenie interfejsu obliczeniowego
        this.fireNoiseSourceModelChangedEvent(this);
        return true;
    }
    
    public double getSoundPowerLevel() {
        return this.soundPowerLevel;
    }
    
    @Override
    public void dispatchNMMNoiseSourceModelInputDataChanged(AbstractTableModel _mEvent) {
        
    }

    @Override
    public void addNoiseSourceModelChangedListener(NMMNoiseSourceModelChangedListener _l) {
        this.NMMNoiseSourceModelChangedListeners.add(_l);
    }
    
    @Override
    public void removeNoiseSourceModelChangedListener(NMMNoiseSourceModelChangedListener _l) {
        this.NMMNoiseSourceModelChangedListeners.remove(_l);
    }

    public void setRefBox_l1(Double _RefBoxL1) {
        if (_RefBoxL1>0) {
            this.l1=_RefBoxL1;
            System.out.println("Ustawiam l1 na "+this.l1);
            this.rearrangeDataTable();
            this.recalculateModel();
        } else {
            System.out.println("L1 musi być wieksze od 0");
        }
    }
    
    public void setRefBox_l2(Double _RefBoxL2) {
        if (_RefBoxL2>0) {
            this.l2=_RefBoxL2;
            System.out.println("Ustawiam l2 na "+this.l2);
            this.rearrangeDataTable();
            this.recalculateModel();
        } else {
            System.out.println("L2 musi być wieksze od 0");
        }
    }
    
    public void setRefBox_l3(Double _RefBoxL3) {
        if (_RefBoxL3>0) {
            this.l3=_RefBoxL3;
            System.out.println("Ustawiam l3 na "+this.l3);
            this.rearrangeDataTable();
            this.recalculateModel();
        } else {
            System.out.println("L3 musi być wieksze od 0");
        }        
    }

    public void setMeasurementSurfaceType(int selectedIndex) {
                
        this.measurementSurface=selectedIndex;
        this.recalculateModel();
    }
    
    private void fireNoiseSourceModelChangedEvent(NMMNoiseSourceModel _mEvent) {

        //jeżeli model źródła hałasu ma słuchaczy, to wyślij powiadomienia
        if (this.NMMNoiseSourceModelChangedListeners!=null) {
            Object[] listeners = NMMNoiseSourceModelChangedListeners.toArray(); 
            
            int numListeners = listeners.length;
            for (int i = 0; i<numListeners; i+=2) {
                if (listeners[i] instanceof NMMNoiseSourceModelChangedListener) {
                    ((NMMNoiseSourceModelChangedListener)listeners[i]).dispatchNMMNoiseSourceModelChanged(_mEvent);
                }
            }    
        }        
    }

    public void setClosedSpaceDimensionL1(double _Pl1) {
        this.P_l1=_Pl1;
        this.recalculateModel();
    }
    
    public void setClosedSpaceDimensionL2(double _Pl2) {
        this.P_l2=_Pl2;
        this.recalculateModel();
    }
    
    public void setClosedSpaceDimensionL3(double _Pl3) {
        this.P_l3=_Pl3;
        this.recalculateModel();
    }
    
    public double getClosedSpaceDimensionL1() {
        return this.P_l1;
    }
    
    public double getClosedSpaceDimensionL2() {
        return this.P_l2;
    }
        
    public double getClosedSpaceDimensionL3() {
        return this.P_l3;
    }    
    
    public void setAlpha(double _alpha) {
        this.alfa=_alpha;
        this.recalculateModel();
    }
    
    public double getAlpha() {
        return this.alfa;
    }

    public void setMeasurementTime(double _mt) {
        this.measurementTime=_mt;
        this.recalculateModel();
    }
    
    public double getMeasurementTime() {
        return this.measurementTime;
    }
    
    public double getK1A() {
        return this.K1A;
    }
    
    public double getK2A() {
        return this.K2A;
    }    
    
    /**
     * Ustawianie rodzaju przestrzeni (0-otwarta, 1-zamknięta) w jakiej wy-
     * konywany jest pomiar mocy akustycznej
     * @param _mSpace int
     */
    public void setMeasurementSpace(int _mSpace) {
        this.measurementSpace=_mSpace;
    }
    
    /**
     * Pobieranie informacji o rodzaju środowiska pomiarowego mocy akustycznej
     * (0-otwarta, 1-zamknięta)
     * @return int
     */
    public int getMeasurementSpace() {
        return this.measurementSpace;
    }

    public double getMeasurementSurfaceArea() {
        return this.S;
    }

    public void setMeasurementDistance(Double _measurementDistance) {
        this.d=_measurementDistance;
        this.rearrangeDataTable();
        this.recalculateModel();
    }

    public String getWarnings() {
        return this.warnings;
    }
        
    @Override
    public String toString() {
        return this.modelName;
    }

    public void setSoundSourceName(String text) {
        this.soundSourceName=text;
    }

    public double getAverageNoiseLevel() {
        return this.avgn;
    }

    public double getAverageBackgroundLevel() {
        return this.avgt;
    }

    public double getAverageNettoNoiseLevel() {
        return this.avsnl;
    }

    public double getModelUncertainty() {
        return this.measurementUncertainty;
    }

    public void setNumberOfReflectiveSurfaces(int i) {
        this.reflectiveSurfacesNumber=i;
    }

    @Override
    public boolean isComplete() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
