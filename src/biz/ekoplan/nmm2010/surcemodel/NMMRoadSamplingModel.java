/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.surcemodel;

import biz.ekoplan.nmm2010.enums.NMMMonths;
import biz.ekoplan.nmm2010.enums.NMMRoadTrafficTypes;
import biz.ekoplan.nmm2010.enums.NoiseLevelIndicators;
import biz.ekoplan.nmm2010.enums.NoiseModelResult;
import biz.ekoplan.nmm2010.enums.NoiseSourceModelType;
import biz.ekoplan.nmm2010.surcemodel.result.NMMNoiseSourceModelResult;
import biz.ekoplan.nmm2010.toolbox.NMMToolbox;
import java.io.Serializable;
import java.util.TreeSet;
import java.util.UUID;
import javax.swing.table.AbstractTableModel;
import nmm2010.NMMCalculationReport;
import nmm2010.NMMEventType;
import nmm2010.NMMNoiseCalculator;
import nmm2010.NMMProject;

/**
 *  Model II generacji
 * @author jarek
 */
public class NMMRoadSamplingModel extends AbstractTableModel implements NMMNoiseSourceModel, Serializable {
    
    static final long serialVersionUID =1L;
    
    String[] headers = {"Hour","Event type"};
    final boolean DEBUG=false;
    NMMProject nmmProject;
    UUID muid;
    boolean isRecalculated = false;
    double LAeqD=0;
    double LAeqN=0;   
    private double LD=0;
    private double LW=0;
    private double LN=0;
    private double LDWN=0;
    double ltlo=0;
    boolean facadeCorrection=false;
    boolean backgroundNoiseAsL95=true;
    private NMMCalculationReport calcReport = new NMMCalculationReport();
    private NMMRoadTrafficTypes trafficType=NMMRoadTrafficTypes.Local;
    private NMMMonths measurementMonth=NMMMonths.May;
        
    double URAB95d;    
    double URAB95n;    
    double URAB95_dwn_d;
    double URAB95_dwn_w;
    double URAB95_dwn_n;
    double URAB95_dwn;
    
    final int TBL_ROWS = 24;
    final int TBL_COLS = 2;
    
    String modelName="New road sampling model";
    String[] h={"06-07","07-08","08-09","09-10","10-11","11-12","12-13",
        "13-14","14-15","15-16","16-17","17-18","18-19","19-20","20-21",
        "21-22","22-23","23-24","00-01","01-02","02-03","03-04","04-05",
        "05-06"};
    NMMEventType[] eventTypes = {null,null,null,null,null,null,null,null,null,
        null,null,null,null,null,null,null,null,null,null,null,null,null,null,
        null};
  
    //--- START: dla procedury rekalkulacji modelu ----------------------------
    private double[] r;                       // rozstępy dla dla poszczególnych rodzajów zdarzeń        
    private int[] counter_d;    
    private int[] counter_n;
    private int[] counter_dwn_d;
    private int[] counter_dwn_w;
    private int[] counter_dwn_n;
    private double[] laeqtk;  
    private double laeq0t_d=0;
    private double laeq0t_n=0;
    private double laeq0t_dwn_d=0;
    private double laeq0t_dwn_w=0;
    private double laeq0t_dwn_n=0;    
    private double[] LAeqImTd;
    private double[] LAeqImTn; 
    private double[] LAeqImT_dwn_d;
    private double[] LAeqImT_dwn_w;
    private double[] LAeqImT_dwn_n;
    private double[] UA95zm;
    private double[] UA95im;
    private double[] UA95imTd;
    private double[] UA95imTn;  
    private double[] UA95imT_dwn_d;
    private double[] UA95imT_dwn_w;
    private double[] UA95imT_dwn_n;
    private NMMEventType zdarzenieTla;
    private TreeSet<NMMEventType> typesTree = new TreeSet<NMMEventType>();
    //--- KONIEC: dla procedury rekalkulacji modelu ---------------------------
    
    @Override
    public String toString() {
        return getModelName();
    }
    
    public NMMRoadSamplingModel() {       
    }
    
    public NMMRoadSamplingModel(String _modelName, NMMProject _nmmProject, UUID _muid) {
        this.modelName=_modelName;
        this.nmmProject = _nmmProject;
        this.muid=_muid;
        this.isRecalculated=false;
    }
    
        public NMMRoadTrafficTypes getTrafficType() {
        return trafficType;
    }

    public void setTrafficType(NMMRoadTrafficTypes trafficType) {
        this.trafficType = trafficType;
        this.recalculateModel();
        fireTableDataChanged();
    }

    public NMMMonths getMeasurementMonth() {
        return measurementMonth;
    }

    public void setMeasurementMonth(NMMMonths measurementMonth) {
        this.measurementMonth = measurementMonth;
        this.recalculateModel();
        fireTableDataChanged();
    }
    
    public double getLAeqD() {
        return this.LAeqD;
    }
    
    public double getLAeqN() {
        return this.LAeqN;
    }
    
    public double getLD() {
        return LD;
    }
    
    public double getLW() {
        return LW;
    }
    
    public double getLN() {
        return LN;
    }
    
    public double getLDWN() {        
        return LDWN;
    }
    
    @Override
    public void setModelName(String _newModelName) {
        this.modelName=_newModelName;
    }
    
    @Override
    public String getModelName() {
        return this.modelName;
    }
    
    @Override
    public NoiseSourceModelType getNoiseModelType() {
        return NoiseSourceModelType.POLISH_REFERENCE_METHOD_ROADS_SAMPLING;
    }
    
    public int[][] getEventsModel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getRowCount() {
        return this.TBL_ROWS;
    }

    @Override
    public int getColumnCount() {
        return this.TBL_COLS;
    }

    @Override
    public String getColumnName(int i) {
        return this.headers[i];
    }

    @Override
    public Class getColumnClass(int i) {
        if (this.DEBUG) {
            System.out.println("Zapytanie o klasę kolumny tabeli: i="+i);
        }                       
        switch (i) {
            case 0: 
                return String.class;                
            case 1:
                return NMMEventType.class;                               
            default:                         
                return String.class;
        }                
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        
        boolean isEditable = false;        
        if (i1==1) {
            isEditable=true;
        }        
        return isEditable;        
    }

    @Override
    public Object getValueAt(int row, int col) {
        
        Object cellContent=null;
        
        switch (col) {
            case 0: 
                cellContent=this.h[row];
                break;
            case 1:
                cellContent=this.eventTypes[row];
                break;
        }        
        return cellContent;
    }

    /** 
     * 
     * 
     * @return
     */
    
    public boolean isRecalculated() {
        return this.isRecalculated;
    }
    
    public double getURAB95d() {
        return URAB95d;
    }
    
    public double getURAB95n() {
        return URAB95n;
    }
    
    public double getURAB95_dwn_n() {
        return URAB95_dwn_n;
    }
    
    public double getURAB95_dwn_w() {
        return URAB95_dwn_w;
    }
    
    public double getURAB95_dwn_d() {
        return URAB95_dwn_d;
    } 
    
    public double getURAB95_dwn() {
        return URAB95_dwn;
    }
    
    public boolean recalculateModel() {
        
        boolean successful=true;

        //assume recalculation will succeed
        this.isRecalculated=true;
        //identyfikowanie indywidualnych typów zdarzeń przypisanych do 
        //kolejnych godzin doby
        for (int i=0; i<24;i++) {
            if (this.eventTypes[i]!=null) {
                if (typesTree.add(this.eventTypes[i])) {
                    System.out.println("Brak takiego typu, dodaję. Rozmiar="+typesTree.size());
                }
            }
        }
        System.out.println("Liczba typów zdarzeń: "+typesTree.size());        
        
        laeqtk = new double[typesTree.size()];
        r = new double[typesTree.size()];
        UA95zm = new double[typesTree.size()];
        UA95im = new double[typesTree.size()];
        UA95imTd = new double[typesTree.size()];
        UA95imT_dwn_d = new double[typesTree.size()];
        UA95imT_dwn_w = new double[typesTree.size()];
        UA95imT_dwn_n = new double[typesTree.size()];
        UA95imTn = new double[typesTree.size()];
        LAeqImTd = new double[typesTree.size()];
        LAeqImTn = new double[typesTree.size()];
        LAeqImT_dwn_d = new double[typesTree.size()];
        LAeqImT_dwn_w  = new double[typesTree.size()];
        LAeqImT_dwn_n = new double[typesTree.size()];                        
        counter_d = new int[typesTree.size()];  // ile razy dany typ wystepuje w ciagu dnia
        counter_n = new int[typesTree.size()];  // ile razy dany typ wystepuje w ciągu nocy
        
        counter_dwn_d = new int[typesTree.size()];  // ile razy dany typ wystepuje w ciagu dnia (średniorocznego)
        counter_dwn_w = new int[typesTree.size()];  // ile razy dany typ wystepuje w ciagu wieczoru (średniorocznego
        counter_dwn_n = new int[typesTree.size()];  // ile razy dany typ wystepuje w ciagu nocy (średniorocznej)
        
        double[] uTable_d= new double[typesTree.size()];
        double[] uTable_n= new double[typesTree.size()];      
        double[] uTable_dwn_d= new double[typesTree.size()];
        double[] uTable_dwn_w= new double[typesTree.size()];
        double[] uTable_dwn_n= new double[typesTree.size()];
        
        if (this.backgroundNoiseAsL95) {
            System.out.println("Przyjmuję tło na podstawie L95 pomiaru");
            ltlo=this.nmmProject.getL95(muid);
        } else {
            System.out.println("Przyjmuję tło na podstawie zidentyfikowanych zdarzeń tła akustycznego.");
            ltlo=this.nmmProject.getMeasurement(muid).getLeq(this.nmmProject.getEvents(zdarzenieTla));
        }                        
        
        //checking if R is not too big, calculate laeqtk
        int cntr=0;        
        for (NMMEventType et : typesTree) {            
            //counting number of hours for current et for day and night
            for (int i=0; i<24;i++) {
                if (this.eventTypes[i]!=null) {
                    if (this.eventTypes[i].compareToBoolean(et)) {
                        if (i<16) {
                            counter_d[cntr]++;
                            if(i<12) {
                                counter_dwn_d[cntr]++;
                            } else {
                                counter_dwn_w[cntr]++;
                            }
                        } else {
                            counter_n[cntr]++;
                            counter_dwn_n[cntr]++;
                        }
                    }   
                } else {
                    //recalculation fails if there is no event type assigned 
                    //to any hour
                    this.isRecalculated=false;
                }
                System.out.println("Typ "+et.toString()+" występuje "+
                        counter_d[cntr]+" i "+ counter_n[cntr]+" razy odpowiednio "
                        + "w dzień i w nocy, a dla D W N wystepuje odpowiednio:"+
                        counter_dwn_d[cntr]+" "+counter_dwn_w[cntr]+" "+ counter_dwn_n[cntr]+" razy.");
                
            }            
            r[cntr] = this.nmmProject.getLeqR(et, this.muid);
            UA95zm[cntr]=NMMNoiseCalculator.Lord95(this.nmmProject.getLeqArray(et, muid));                        
            System.out.println("Rozstęp dla typu: "+et.toString()+" wynosi = "+r[cntr]+" a niepewnosc = "+UA95zm[cntr]);            
            laeqtk[cntr]=this.nmmProject.getLeqLogAverage(et, this.muid);                 
            
            UA95im[cntr]=NMMNoiseCalculator.u_roznica_log(laeqtk[cntr], UA95zm[cntr],
                        ltlo,0);
            //TODO: Uwaga ! Tutaj przyjmuje się że tło nie wnosi nic do niepewności pomiaru                                                           
            
            System.out.println("Wartość LAeqtk dla typu: "+et.toString()+" wynosi = "+laeqtk[cntr]+ " ± "+UA95im[cntr]);            
            cntr++;
        }
        
        double skladnik_d=0;
        double skladnik_n=0;
        double skladnik_dwn_d=0;
        double skladnik_dwn_w=0;
        double skladnik_dwn_n=0;        
        
        cntr=0;
        for (NMMEventType et : typesTree) {                        
            //dla dobowych
            LAeqImTd[cntr]=(counter_d[cntr]*Math.pow(10,0.1*laeqtk[cntr]));
            //System.out.println("LAeqImTd = "+LAeqImTd[cntr]);
            LAeqImTn[cntr]=(counter_n[cntr]*Math.pow(10,0.1*laeqtk[cntr]));            
            //System.out.println("LAeqImTn = "+LAeqImTn[cntr]);
            skladnik_d=skladnik_d+LAeqImTd[cntr];
            skladnik_n=skladnik_n+LAeqImTn[cntr];
            
            //dla średniorocznych
            LAeqImT_dwn_d[cntr]=(counter_dwn_d[cntr]*Math.pow(10,0.1*laeqtk[cntr]));
            LAeqImT_dwn_w[cntr]=(counter_dwn_w[cntr]*Math.pow(10,0.1*laeqtk[cntr]));            
            LAeqImT_dwn_n[cntr]=(counter_dwn_n[cntr]*Math.pow(10,0.1*laeqtk[cntr]));            
            skladnik_dwn_d=skladnik_dwn_d+LAeqImT_dwn_d[cntr];
            skladnik_dwn_w=skladnik_dwn_w+LAeqImT_dwn_w[cntr];
            skladnik_dwn_n=skladnik_dwn_n+LAeqImT_dwn_n[cntr];            
            cntr++;
        }                                        
        laeq0t_d = 10*Math.log10((1d/16d)*skladnik_d);
        laeq0t_n = 10*Math.log10((1d/8d)*skladnik_n);
        //System.out.println("Poziom hałasu dla dnia z tłem to: "+laeq0t_d);
        //System.out.println("Poziom hałasu dla nocy z tłem to: "+laeq0t_n);
        laeq0t_dwn_d = 10*Math.log10((1d/12d)*skladnik_dwn_d);
        laeq0t_dwn_w = 10*Math.log10((1d/4d)*skladnik_dwn_w);
        laeq0t_dwn_n = 10*Math.log10((1d/8d)*skladnik_dwn_n);
        
        this.LAeqD = NMMNoiseCalculator.roznica_log(laeq0t_d, ltlo);
        this.LAeqN = NMMNoiseCalculator.roznica_log(laeq0t_n, ltlo);
        this.LD = NMMNoiseCalculator.roznica_log(laeq0t_dwn_d, ltlo);
        this.LW = NMMNoiseCalculator.roznica_log(laeq0t_dwn_w, ltlo);
        this.LN = NMMNoiseCalculator.roznica_log(laeq0t_dwn_n, ltlo);
        
        if (this.facadeCorrection) {
            this.LAeqD=this.LAeqD-3;
            this.LAeqN=this.LAeqN-3;
            this.LD=this.LD-3;
            this.LW=this.LW-3;
            this.LN=this.LN-3;
        }     
        
        //obliczenie poprawki dla LDWN ze względu na rodzaj drogi i okres wykonania pomiaru hałasu
        double[] tablica_p1 = {0.93, 0.95, 1.06, 1.11};
        double[][] tablica_p2 = {{1.25, 1.47, 1.47, 1.39},
            {1.14, 1.32, 1.32, 1.23},
                {1.1,1.18,1.18,1.18},
                {1.02,1.1,1.1,1.14},
                {0.97,1.03,1.03,0.96},
                {0.93,0.89,0.89,0.86},
                {0.86,0.7,0.7,0.78},
                {0.86,0.7,0.7,0.76},
                {0.93,0.93,0.93,0.91},
                {0.97,0.98,0.98,0.95},
                {1.02,1.1,1.1,1.08},
                {1.09,1.16,1.16,1.18}};        
                
        double p1=tablica_p1[this.trafficType.ordinal()];
        double p2=tablica_p2[this.measurementMonth.ordinal()][this.trafficType.ordinal()];
        double poprawka = 10*Math.log10(p1*p2);        
        this.LD=this.LD+poprawka;
        this.LW=this.LW+poprawka;
        this.LN=this.LN+poprawka;
                        
        double skl1=(12d/24d)*Math.pow(10d, 0.1d*this.LD);
        double skl2=(4d/24d)*Math.pow(10d, 0.1d*(this.LW+5));
        double skl3=(8d/24d)*Math.pow(10d, 0.1d*(this.LN+10));
        
        this.LDWN=10*Math.log10(skl1+skl2+skl3);

        //System.out.println("Poziom tła to: "+ltlo);
        //System.out.println("Poziom hałasu dla dnia bez tła to: "+this.LAeqD);
        //System.out.println("Poziom hałasu dla nocy bez tła to: "+this.LAeqN);                                                               
        
        System.out.println("Szacowanie niepewności.");
        cntr=0;
        for (NMMEventType et : typesTree) {                        
            //dla dobowych
            UA95imTd[cntr]=UA95im[cntr]*3600d*(double)counter_d[cntr]*Math.pow(10d,0.1*((laeqtk[cntr]-laeq0t_d)))/(16d*3600d);          
            UA95imTn[cntr]=UA95im[cntr]*3600d*(double)counter_n[cntr]*Math.pow(10d,0.1*((laeqtk[cntr]-laeq0t_n)))/(8d*3600d);
            //System.out.println("Niepewnośc dla dnia i dla nocy: "+UA95imTd[cntr]+ " - "+UA95imTn[cntr]);
            //dla średniorocznych
            UA95imT_dwn_d[cntr]=UA95im[cntr]*3600d*(double)counter_dwn_d[cntr]*Math.pow(10d,0.1*((laeqtk[cntr]-laeq0t_dwn_d)))/(12d*3600d);
            UA95imT_dwn_w[cntr]=UA95im[cntr]*3600d*(double)counter_dwn_w[cntr]*Math.pow(10d,0.1*((laeqtk[cntr]-laeq0t_dwn_w)))/(4d*3600d);
            UA95imT_dwn_n[cntr]=UA95im[cntr]*3600d*(double)counter_dwn_n[cntr]*Math.pow(10d,0.1*((laeqtk[cntr]-laeq0t_dwn_n)))/(8d*3600d);
            cntr++;
        }               
        
        for (int l=0;l<typesTree.size();l++) {
            uTable_d[l]=UA95imTd[l];
            uTable_n[l]=UA95imTn[l];            
            uTable_dwn_d[l]=UA95imT_dwn_d[l];
            uTable_dwn_w[l]=UA95imT_dwn_w[l];
            uTable_dwn_n[l]=UA95imT_dwn_n[l];            
        }        
        double UA95d=NMMNoiseCalculator.UR(uTable_d);        
        double UA95n=NMMNoiseCalculator.UR(uTable_n);
        double UA95_dwn_d=NMMNoiseCalculator.UR(uTable_dwn_d);
        double UA95_dwn_w=NMMNoiseCalculator.UR(uTable_dwn_w);
        double UA95_dwn_n=NMMNoiseCalculator.UR(uTable_dwn_n);                
        
        double[] dane_d = new double[2];
        double[] dane_n = new double[2];
        double[] dane_dwn_d = new double[2];
        double[] dane_dwn_w = new double[2];
        double[] dane_dwn_n = new double[2];
        double[] dane_dwn = new double[3];
        
        dane_d[0]=UA95d; dane_d[1]=this.nmmProject.getMeasurement(this.muid).getUncertaintyB();
        dane_n[0]=UA95n; dane_n[1]=this.nmmProject.getMeasurement(this.muid).getUncertaintyB();
        dane_dwn_d[0]=UA95_dwn_d; dane_dwn_d[1]=this.nmmProject.getMeasurement(this.muid).getUncertaintyB();
        dane_dwn_w[0]=UA95_dwn_w; dane_dwn_w[1]=this.nmmProject.getMeasurement(this.muid).getUncertaintyB();
        dane_dwn_n[0]=UA95_dwn_n; dane_dwn_n[1]=this.nmmProject.getMeasurement(this.muid).getUncertaintyB();        
        
        URAB95d=NMMNoiseCalculator.UR(dane_d);
        URAB95n=NMMNoiseCalculator.UR(dane_n);
        
        URAB95_dwn_d=NMMNoiseCalculator.UR(dane_dwn_d);
        URAB95_dwn_w=NMMNoiseCalculator.UR(dane_dwn_w);
        URAB95_dwn_n=NMMNoiseCalculator.UR(dane_dwn_n);
        
        dane_dwn[0]=URAB95_dwn_d;
        dane_dwn[1]=URAB95_dwn_w;
        dane_dwn[2]=URAB95_dwn_n;
        
        URAB95_dwn=NMMNoiseCalculator.UR(dane_dwn);
        
        return successful;
    }
    
    
    @Override
    public void setValueAt(Object o, int i, int i1) {       
        if ((i1==1) && (o!=null)) {
            System.out.println("Ustawiam komórkę: " +i + " "+ i1+ " na "+o.toString());
            //this.eventTypes[i]=new NMMEventType(o.toString());  
            this.eventTypes[i]=(NMMEventType)o;
            
            //fill out following cells automatically if empty
            for (int r=1; r<(TBL_ROWS-i);r++) {
                if (this.eventTypes[i+r]==null) {
                    System.out.println("... i dodatkowo wiersz: "+i+r);
                    //this.eventTypes[i+r]=new NMMEventType(o.toString());
                    this.eventTypes[i+r]=(NMMEventType)o;
                }                
            }            
            //each change to table data model, try to recalculate noise source model
            if (!this.recalculateModel()) {
                if (this.DEBUG) {
                    System.out.println("Still cannot recalculate model");
                }
            }                                                
            fireTableDataChanged();
        }                
    }
    
    public boolean getFacadeCorrection() {
        return this.facadeCorrection;
    }
        
    public void setFacadeCorrection(boolean _fc) {
        this.facadeCorrection=_fc;
        this.recalculateModel();
        fireTableDataChanged();
    }
    
    public boolean getBackgroundNoiseAsL95() {
        return this.backgroundNoiseAsL95;
    }
    
    public void setBackgroundNoiseAsL95(boolean _fc) {
        this.backgroundNoiseAsL95=_fc;
        this.recalculateModel();
        fireTableDataChanged();
    }

    public String getReport() {
        
        String report = "No report yet!";
        //TODO: tutaj ma byc opisany mechanizm generowania raportu z obliczeń
        return report;        
    }

    @Override
    public long getStartTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getEndTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UUID getMeasurementUUID() {
        return this.muid;
    }
  
    public void setMeasurementUUID() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NMMCalculationReport getCalculationReport() {
        
        //This variable will store calculation report;
        String cR;
        
        //
        cR="<html><head><title></title><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'></head>"
                + "<h1>Raport z obliczeń</h1>";        
        cR=cR+"<h2>Założenia do przeprowadzenia pomiaru.</h2>";
        cR=cR+"<p>Podstawą do określenia godzin w jakich przeprowadzono badanie"
                + " było natężenie ruchu pojazdów w poszczególnych kategoriach, w"
                + "oparciu o które to określone zostały godziny w jakich hałas"
                + "powodowany przez ruch pojazdów nie różni się o więcej niż 3dB."
                + "Przyjęto iż godziny doby można zgrupować w sposób następujący:<p>";
        cR=cR+"<table border='1'>"
                + "<tr>"
                + "<td>Godzina</td>"
                + "<td>Reprezentatywny podokres</td>"
                + "</tr>";                
        for (int i=0; i<24; i++) {
            cR=cR + "<tr><td>"+(i+6)+"-"+(i+7)+"</td><td>a"+this.eventTypes[i].getDescription()+"</td></tr>";
        }        
        cR=cR+ "</table>";
        cR=cR+"<p>W oparciu o tak przyjęte grupowanie podjęto decyzję o przeprowadzeniu"
                + "badań w następujacych godzinach:</p>";
        cR=cR+"<p>W każdej z godzin wykonano pomiar w conajmniej trzech 15-minutowych"
                + "podokresach. Wyniki badań dla poszczególnych godzin przedstawiają"
                + "się następująco:</p>";
        
        cR=cR+"<table border='1'>"
                + "<tr>"
                + "<td>Podokres</td>"
                + "<td>Próbki</td>"
                + "<td>Rozstęp</td>"               
                + "</tr>";                
         int t=0;
         for (NMMEventType et : typesTree) {                             
            cR=cR + "<tr>"
                    + "<td>"+et.getDescription()+"</td>"
                    + "<td>"+this.r[t]+"</td>"
                    + "<td>"+this.UA95zm[t]+"</td>"                    
                    + "<td></td></tr>";
            t++;
        }                        
        cR=cR+"<p>W oparciu o uzyskane wyniki pomiarów jednostkowych określono poziomy"
                + "ekwiwalentne dla poszczególnych godzin. Uzyskano nastepujące"
                + "wyniki:</p>";
        if (this.facadeCorrection) {
            cR=cR+"<p>Badanie przeprowadzono przy elewacji budynku, dlatego też "
                    + "od uzyskanego poziomu dźwieku odjęto 3dB</p>";
        } else {
            cR=cR+"<p>Badanie przeprowadzono w odległości od najbliższej powierzchni"
                    + " odbijającej większej niż 3m.";
        }
        if (this.backgroundNoiseAsL95) {
            cR=cR+"<p>Poziom tła akustycznego określono jako wartośc statystyczna "
                    + "U<sub>95</sub>="+NMMToolbox.formatDouble(this.ltlo,"0.0")+"</p>";
        } else {
            cR=cR+"<p>Poziom tła akustycznego określono w drodze odrębnych pomiarów.</p>";
        }
        cR=cR+"<p>Ostatecznie uzyskano nastepujące wyniki obliczeń dla pory dziennej"
                + "oraz pory nocnej:</p>";
        cR=cR+"<p>L<sub>AeqD</sub>="+NMMToolbox.formatDouble(this.LAeqD, "0.0")+" dB</p>";
        cR=cR+"<p>L<sub>AeqN</sub>="+NMMToolbox.formatDouble(this.LAeqN, "0.0")+" dB</p>";
        
        this.calcReport.addParagraph(cR);
        
        System.out.println(this.calcReport.toString());
        
        return this.calcReport;    
    }

    @Override
    public double getNoiseModelResult(NoiseLevelIndicators _nli) {
        
        double result=-1;
        
        if (_nli==NoiseLevelIndicators.LAeqD) {
            result = this.LAeqD;
        } else if (_nli==NoiseLevelIndicators.LAeqN) {
            result = this.LAeqN;
        }
        return result;
    }

    @Override
    public NMMNoiseSourceModelResult getNoiseModelComplexResult(NoiseModelResult _nli) {
        return null;
    }

    @Override
    public void recreateListenersArray() {
        
    }

    @Override
    public void addNoiseSourceModelChangedListener(NMMNoiseSourceModelChangedListener _l) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeNoiseSourceModelChangedListener(NMMNoiseSourceModelChangedListener _l) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isComplete() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setBackgroundNoiseEvent(NMMEventType nmmEventType) {
        this.zdarzenieTla=nmmEventType;
        this.recalculateModel();
        this.fireTableDataChanged();
    }
}
