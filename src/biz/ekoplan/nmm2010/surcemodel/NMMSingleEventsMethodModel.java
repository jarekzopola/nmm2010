/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.surcemodel;

import biz.ekoplan.nmm2010.enums.NoiseLevelIndicators;
import biz.ekoplan.nmm2010.enums.NoiseModelResult;
import biz.ekoplan.nmm2010.enums.NoiseSourceModelType;
import biz.ekoplan.nmm2010.surcemodel.result.NMMNoiseSourceModelResult;
import biz.ekoplan.nmm2010.toolbox.NMMToolbox;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.table.AbstractTableModel;
import nmm2010.NMMCalculationReport;
import nmm2010.NMMEvent;
import nmm2010.NMMEventType;
import nmm2010.NMMNoiseCalculator;
import nmm2010.NMMProject;

    
/**
 * Model II generacji
 * @author Jarek
 */
public class NMMSingleEventsMethodModel implements Serializable, 
        NMMNoiseSourceModel, NMMNoiseSourceModelInputDataChangedListener{

    final boolean DEBUG=false;
    
    private EventTypeRecord[] eventsModel2;
    private NMMProject nmmProj;
    private double UR_LAeqtD=0;
    private double UR_LAeqtN=0;
    private double LAeqT_D=0;
    private double LAeqT_N=0;
    private double[] stdDev;
    private double[] UA_LAek;
    private double[] UA95_LAek;
    private double[] LAekTkD;
    private double[] LAekTkN;
    private double[] laek;   
    private boolean facadeCorrection=false;
    private transient ArrayList<NMMNoiseSourceModelChangedListener> NMMNoiseSourceModelChangedListeners; 
    private UUID MUID;
    private long startTime;
    private long endTime;
    private NMMCalculationReport calculationReport = new NMMCalculationReport();
    
    String modelName="New single events model";

    public NMMSingleEventsMethodModel(NMMProject _nmmProj, UUID muid, long _startTime, long _endTime) {
        
        NMMNoiseSourceModelChangedListeners = new ArrayList<>();
        
        this.nmmProj=_nmmProj;
        this.MUID=muid;        
        this.startTime=_startTime;
        this.endTime=_endTime;
        
        if (this.DEBUG) {
            System.out.println("Tworzę obiekt modelu źródła hałasu oparty o"
                    + "pojedyncze zdarzenia (np. kolejowe)");
        }
        
        //tworzymy tablice przechowującą dane o ilości wystąpień określonego
        //zdarzenia w poszczególnych podokresach doby                        
        int liz = this.nmmProj.getEventTypeTypes().length;
        NMMEventType[] etsa= this.nmmProj.getEventTypeTypes();        
        this.eventsModel2 =new EventTypeRecord[liz];        
        for (int i=0; i<liz;i++) {
            this.eventsModel2[i]=new EventTypeRecord();
            this.eventsModel2[i].ets=etsa[i];
            this.eventsModel2[i].eveningFrequency=0;
            this.eventsModel2[i].dayFrequency=0;
            this.eventsModel2[i].eveningFrequency=0;
        }
    }
    
    @Override
    public void recreateListenersArray() {
        this.NMMNoiseSourceModelChangedListeners=new ArrayList<>();
    }
    
    @Override
    public String toString() {
        return this.modelName;
    }
    
    public void setModelDataSource(EventTypeRecord[] etr) {
        this.eventsModel2=etr;
    }
    
    public void setFacadeCorrection(boolean _set) {
        this.facadeCorrection=_set;
        this.recalculateModel();
        this.fireNoiseSourceModelChangedEvent(this);
    }
    
    public EventTypeRecord[] getModelDataSource() {
        return this.eventsModel2;
    }
    
    @Override
    public NoiseSourceModelType getNoiseModelType() {
        return NoiseSourceModelType.POLISH_REFERENCE_METHOD_EVENTS;
    }

    @Override
    public String getModelName() {
        return this.modelName;
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
        return this.MUID;
    }

    @Override
    public double getNoiseModelResult(NoiseLevelIndicators _nli) {
        
        double res;     
        
        if (_nli==NoiseLevelIndicators.LAeqD) {
            res=this.getLAeqD();
        } else if (_nli==NoiseLevelIndicators.LAeqN) {
            res=this.getLAeqN();
        } else {
            res=-1;
        }
        return res;  
    }

    @Override
    public NMMNoiseSourceModelResult getNoiseModelComplexResult(NoiseModelResult _nli) {
        
        NMMNoiseSourceModelResult res;     
        
        if (_nli==NoiseModelResult.LStats) {
            res=null;
        } else if (_nli==NoiseModelResult.LStatsCumulated) {
            //TODO: Poniżej trzeba jeszcze uzupełnić, bo raczej nie chodzi o null
            res=null;
        } else {
            res=null;
        }
        return res;
    }

    @Override
    public NMMCalculationReport getCalculationReport() {
        return this.calculationReport;
    }

    @Override
    public void dispatchNMMNoiseSourceModelInputDataChanged(AbstractTableModel _mEvent) {
        System.out.println("Przeliczam model pojedycznych zdarzeń w związku ze zmianą danych");        
        this.recalculateModel();
    }

    
    public NMMSingleEventsMethodModel() {        
    }
    
    public NMMProject getNMMProject() {
        return this.nmmProj;
    }
    
    public double getLAeqD() {
        return this.LAeqT_D;
    }

    public double getUR_D() {
        return this.UR_LAeqtD;
    }

    public double getUR_N() {
        return this.UR_LAeqtN;
    }

    public double getLAeqN() {
        return this.LAeqT_N;
    }
    
    public double getUA95(int _eventTypeIndex) {
        return this.UA95_LAek[ _eventTypeIndex];
    }

    public double getUA(int _eventTypeIndex) {
        return this.UA_LAek[ _eventTypeIndex];
    }

    public boolean recalculateModel() {
        
        boolean recalculatedSuccesfully=true;
        
        int eventsTypesNumber=this.eventsModel2.length;
        int eventsNumber=0;
        double va;
        double[] sum1=new double[eventsTypesNumber];
        double[] sum2=new double[eventsTypesNumber];
        double[] sum3=new double[eventsTypesNumber];
        laek = new double[eventsTypesNumber];
        stdDev=new double[eventsTypesNumber];
        UA_LAek=new double[eventsTypesNumber];
        UA95_LAek=new double[eventsTypesNumber];
        LAekTkD=new double[eventsTypesNumber];
        LAekTkN=new double[eventsTypesNumber];
        double UA95_LAeqtD=0;
        double UA95_LAeqtN=0;
        int night_events=0;
        int evening_events=0;
        int day_events=0;
        NMMEvent[] etb;                        
        
        this.calculationReport.addParagraph("Railway noise measurement report");
        this.calculationReport.addParagraph("Prepared by: "+this.nmmProj.getProjectAuthor());
        this.calculationReport.addParagraph("-------------------------------------------------------------");
        
        for (int et=0; et<eventsTypesNumber; et++) {
            night_events=night_events+this.eventsModel2[et].nightFrequency;
            day_events=day_events+this.eventsModel2[et].dayFrequency;
            evening_events=evening_events+this.eventsModel2[et].eveningFrequency;
        }
                
        // analizuemy dla każdego typu zdarzenia
        for (int et=0; et<eventsTypesNumber; et++) {
            eventsNumber=this.nmmProj.getEventsNumber((NMMEventType)nmmProj.getEventTypes()[et]);        
            //table of single type events
            etb=this.nmmProj.getEvents((NMMEventType)nmmProj.getEventTypes()[et]);

            int enabledEvents=0;
            for (int ne=0; ne<etb.length;ne++) {
                if (etb[ne].isEnabled()) {
                    va = Math.pow(10, 0.1*this.nmmProj.getCurrentMeasurement().getSEL(etb[ne].getStart(), etb[ne].getEnd()));
                    NMMToolbox.debugMessage("Składnik sum1: "+va, DEBUG);
                    sum1[et]=sum1[et]+va;
                    enabledEvents++;
                }
            }
            sum1[et]=sum1[et]/enabledEvents;
            NMMToolbox.debugMessage("sum1 dla zdarzeń typu: "+et+" - "+sum1[et], DEBUG);
            laek[et]=10*Math.log10(sum1[et]);

            for (int ne=0; ne<etb.length;ne++) {
                if (etb[ne].isEnabled()) {
                    va=Math.pow(this.nmmProj.getCurrentMeasurement().getSEL(etb[ne].getStart(),
                            etb[ne].getEnd())-laek[et], 2);
                    NMMToolbox.debugMessage("Składnik sum2: "+va, DEBUG);
                    sum2[et]=sum2[et]+va;
                }
            }
            NMMToolbox.debugMessage("sum2 dla zdarzeń typu: "+et+" - "+sum2[et], DEBUG);

            stdDev[et]=Math.sqrt(sum2[et]/(enabledEvents-1));
            NMMToolbox.debugMessage("odchylenie dla zdarzeń typu: "+et+" - "+stdDev[et], DEBUG);
            for (int ne=0; ne<etb.length;ne++) {
                if (etb[ne].isEnabled()) {
                    sum3[et]=sum3[et]+Math.pow(Math.pow(10,0.1*((this.nmmProj.getCurrentMeasurement().getSEL(etb[ne].getStart(),
                            etb[ne].getEnd())-laek[et]))),2);
                }
            }
            NMMToolbox.debugMessage("sum3 dla zdarzeń typu: "+et+" - "+sum3[et], DEBUG);

            UA_LAek[et]=stdDev[et]*Math.sqrt(sum3[et]/(enabledEvents*enabledEvents));
            NMMToolbox.debugMessage("UA_LAek dla zdarzenia typu"+et+" - "+UA_LAek[et], DEBUG);
            this.calculationReport.addParagraph("UA_LAek dla zdarzenia typu"+et+" - "+UA_LAek[et]);
            
            
            if (enabledEvents>0) {
                UA95_LAek[et]=UA_LAek[et]*NMMNoiseCalculator.tStudent(enabledEvents);
            } else {
                UA95_LAek[et]=Double.NaN;
            }
            NMMToolbox.debugMessage("UA95_LAek dla zdarzenia typu"+et+" - "+UA95_LAek[et], DEBUG);
            this.calculationReport.addParagraph("UA95_LAek dla zdarzenia typu"+et+" - "+UA95_LAek[et]);
            
            
            if (this.eventsModel2[et].dayFrequency==0) {
                LAekTkD[et]=0;
            } else {
                LAekTkD[et]=laek[et]+10*Math.log10(this.eventsModel2[et].dayFrequency)-10*Math.log10(16*60*60);                                                           
            }            
            NMMToolbox.debugMessage("LAekTkD dla zdarzenia typu"+et+" - "+LAekTkD[et], DEBUG);
            this.calculationReport.addParagraph("LAekTkD dla zdarzenia typu"+et+" - "+LAekTkD[et]);
                        
            LAeqT_D=LAeqT_D+Math.pow(10, 0.1*LAekTkD[et]);

            if (this.eventsModel2[et].nightFrequency==0) {
                LAekTkN[et]=0;
            } else {
                LAekTkN[et]=laek[et]+10*Math.log10(this.eventsModel2[et].nightFrequency)-10*Math.log10(8*60*60);                
            }
            LAeqT_N=LAeqT_N+Math.pow(10, 0.1*LAekTkN[et]);
        }
        if (day_events>0) {
            LAeqT_D=10*Math.log10(LAeqT_D);
            if (this.facadeCorrection) {
                this.LAeqT_D=this.LAeqT_D-3;
            }
        } else {
            LAeqT_D=0;
        }
        if (night_events>0) {
            LAeqT_N=10*Math.log10(LAeqT_N);
            if (this.facadeCorrection) {
                this.LAeqT_N=this.LAeqT_N-3;
            }
        } else {
            LAeqT_N=0;
        }
                        
        double skl;
        for (int et=0; et<eventsTypesNumber; et++) {
            skl=(UA95_LAek[et]*this.eventsModel2[et].dayFrequency*Math.pow(10, (0.1*(laek[et]-LAeqT_D))));
            skl=skl*skl;
            UA95_LAeqtD=UA95_LAeqtD+skl;            
            NMMToolbox.debugMessage("Składowa UA95_LAeqtD: "+skl, DEBUG);
            skl=(UA95_LAek[et]*this.eventsModel2[et].nightFrequency*Math.pow(10, (0.1*(laek[et]-LAeqT_N))));
            skl=skl*skl;
            UA95_LAeqtN=UA95_LAeqtN+skl;
            NMMToolbox.debugMessage("Składowa UA95_LAeqtN: "+skl, DEBUG);
        }
        NMMToolbox.debugMessage("Krytyczny UA95_LAeqtD :"+UA95_LAeqtD, DEBUG);
        UA95_LAeqtD=Math.sqrt(UA95_LAeqtD/(Math.pow((16*60*60),2)));
        UA95_LAeqtN=Math.sqrt(UA95_LAeqtN/((8*60*60)*(8*60*60)));
        
        NMMToolbox.debugMessage("UA95_LAeqtD ="+UA95_LAeqtD, DEBUG);
        NMMToolbox.debugMessage("UA95_LAeqtN ="+UA95_LAeqtN, DEBUG);

        UR_LAeqtD=Math.sqrt(UA95_LAeqtD*UA95_LAeqtD+Math.pow(this.nmmProj.getCurrentMeasurement().getUncertaintyB(),2));
        UR_LAeqtN=Math.sqrt(UA95_LAeqtN*UA95_LAeqtN+Math.pow(this.nmmProj.getCurrentMeasurement().getUncertaintyB(),2));
        
        NMMToolbox.debugMessage("UR_LAeqtD ="+UR_LAeqtD, DEBUG);
        NMMToolbox.debugMessage("UR_LAeqtN ="+UR_LAeqtN, DEBUG);
        
        this.fireNoiseSourceModelChangedEvent(this);
        
        return recalculatedSuccesfully;
    }

    public double getStDev(int selectedIndex) {
        return this.stdDev[selectedIndex];
    }

    public double getLAek(int selectedIndex) {
        return this.laek[selectedIndex];
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
    
    public void addNoiseSourceModelChangedListener(NMMNoiseSourceModelChangedListener _l) {
        boolean add = this.NMMNoiseSourceModelChangedListeners.add(_l);
    }
    
    public void removeNoiseSourceModelChangedListener(NMMNoiseSourceModelChangedListener _l) {
        this.NMMNoiseSourceModelChangedListeners.remove(_l);
    }

    public void setModelName(String text) {
        this.modelName=text;
        this.fireNoiseSourceModelChangedEvent(this);
    }

    @Override
    public boolean isComplete() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
