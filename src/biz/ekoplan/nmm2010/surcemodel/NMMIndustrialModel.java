/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.surcemodel;

import biz.ekoplan.nmm2010.enums.NoiseLevelIndicators;
import biz.ekoplan.nmm2010.enums.NoiseModelResult;
import biz.ekoplan.nmm2010.enums.NoiseSourceModelType;
import biz.ekoplan.nmm2010.surcemodel.result.NMMNoiseSourceModelResult;
import java.io.Serializable;
import nmm2010.NMMEvent;
import nmm2010.NMMEventType;
import nmm2010.NMMNoiseCalculator;
import nmm2010.NMMProject;
import biz.ekoplan.nmm2010.toolbox.NMMToolbox;
import java.util.UUID;
import nmm2010.NMMCalculationReport;

/**
 * Model I generacji
 * @author Jarek
 */
public class NMMIndustrialModel implements Serializable, NMMNoiseSourceModel {

    final boolean DEBUG=true;

    String modelName="New industrial source model";
    
    int[][] eventsModel;
    NMMProject nmmProj;
    double UR_LAeqtD=0;
    double UR_LAeqtN=0;    
    double[] LAeq_zm_sr;
    double[] LAeq_zm_max;
    double[] LAeq_zm_min;
    double[] R_zm;
    double[] UA95_zm;
    double[] LAeq_im;
    double[][] LAeq_im_T;
    double[] UA95_im;
    double[][] UA95_im_T;
    double[] LAeqT;
    double[] UA95;
    double[] U_AB_R_95;
    
    NMMCalculationReport calcReport = new NMMCalculationReport();

    public NMMIndustrialModel(NMMProject _nmmProj, int[][] _eventsModel) {
        this.nmmProj=_nmmProj;
        this.eventsModel=_eventsModel;
    }

    public int[][] getEventsModel() {
        return this.eventsModel;
    }

    public double getU_AB_R_95(int _index) {
        return this.U_AB_R_95[_index];
    }

    public double getR(int _index) {
        return R_zm[_index];
    }

    public double getLAeq_zm_max(int _index) {
        return LAeq_zm_max[_index];
    }

    public double getLAeq_zm_min(int _index) {
        return LAeq_zm_min[_index];
    }

    public double getLAeq_im(int _index) {
        return LAeq_im[_index];
    }

    public double getLAeq_im_T(int _index, int _timeSlice) {
        return LAeq_im_T[_index][_timeSlice];
    }
    
    public double getUA95_zm(int _eventTypeIndex) {
        return this.UA95_zm[ _eventTypeIndex];
    }

    public double getUA95_im(int _eventTypeIndex) {
        return this.UA95_im[ _eventTypeIndex];
    }

    public double getUA95_im_T(int _eventTypeIndex, int _timeSlice) {
        return this.UA95_im_T[ _eventTypeIndex][_timeSlice];
    }

    public double getLAek_zm_sr(int selectedIndex) {
        return this.LAeq_zm_sr[selectedIndex];
    }

    public double getLAeqT(int _index) {
        return this.LAeqT[_index];
    }

    public double getUA95(int _index) {
        return this.UA95[_index];
    }

    public String toString() {
        return this.modelName;
    }
    
    public boolean recalculateModel() {

        boolean recalculatedSuccessfuly=true;
        
        int eventsTypesNumber=this.nmmProj.getEventTypes().length;
        int eventsNumber=0;
        double va;
        double[] sum1=new double[eventsTypesNumber];        
        LAeq_zm_sr = new double[eventsTypesNumber];
        R_zm = new double[eventsTypesNumber];
        LAeq_zm_min = new double[eventsTypesNumber];
        LAeq_zm_max = new double[eventsTypesNumber];
        LAeq_im = new double[eventsTypesNumber];
        LAeq_im_T= new double[eventsTypesNumber][3];
        UA95_im_T = new double[eventsTypesNumber][3];        
        UA95_zm=new double[eventsTypesNumber];
        UA95_im=new double[eventsTypesNumber];
        UA95 =new double[3];
        LAeqT = new double[3];
        U_AB_R_95 = new double[3];
        NMMEvent[] etb;
        int bgNoiseIndex=-1;

        if (this.eventsModel==null) {
            return false;
        }
                
        // analizuemy dla każdego typu zdarzenia
        for (int et=0; et<eventsTypesNumber; et++) {
            eventsNumber=this.nmmProj.getEventsNumber((NMMEventType)nmmProj.getEventTypes()[et]);
            NMMToolbox.debugMessage("Liczba zdarzeń typu: "+nmmProj.getEventTypes()[et].toString()+" ("+et+") : "+eventsNumber, DEBUG);
            //czy zdarzenie to tło?
            if (new NMMEventType("tło").compareToBoolean((NMMEventType)nmmProj.getEventTypes()[et])) {
                bgNoiseIndex=et;
            }
            //table of single type events
            etb=this.nmmProj.getEvents((NMMEventType)nmmProj.getEventTypes()[et]);
            float[] etv = new float[etb.length];
            int enabledEvents=0;
            LAeq_zm_min[et]=this.nmmProj.getCurrentMeasurement().getLeq(etb[0].getStart(), etb[0].getEnd());
            NMMToolbox.debugMessage("War. wstępne Min zmierzone Leqk: "+LAeq_zm_min[et], DEBUG);
            LAeq_zm_max[et]=LAeq_zm_min[et];
            NMMToolbox.debugMessage("War. wstępne Max zmierzone Leqk: "+LAeq_zm_max[et], DEBUG);

            double tmpLeq;
            for (int ne=0; ne<etb.length;ne++) {
                if (etb[ne].isEnabled()) {
                    tmpLeq=this.nmmProj.getCurrentMeasurement().getLeq(etb[ne].getStart(), etb[ne].getEnd());
                    va = Math.pow(10, 0.1*tmpLeq);
                    etv[ne]=(float)tmpLeq;
                    //NMMToolbox.debugMessage("Składnik sum1: "+va, DEBUG);
                    sum1[et]=sum1[et]+va;
                    if (LAeq_zm_min[et]>tmpLeq) { LAeq_zm_min[et]=tmpLeq; }
                    if (LAeq_zm_max[et]<tmpLeq) { LAeq_zm_max[et]=tmpLeq; }
                    enabledEvents++;
                }
            }
            R_zm[et]=LAeq_zm_max[et]-LAeq_zm_min[et];
            NMMToolbox.debugMessage("Min = "+LAeq_zm_min[et]+"Max = "+LAeq_zm_max[et]+" Rozstęp: "+R_zm[et], this.DEBUG);

            sum1[et]=sum1[et]/enabledEvents;
            //NMMToolbox.debugMessage("sum1 dla zdarzeń typu: "+et+" - "+sum1[et], DEBUG);
            LAeq_zm_sr[et]=10*Math.log10(sum1[et]);
            this.UA95_zm[et]=NMMNoiseCalculator.Lord95(etv);
            NMMToolbox.debugMessage("Emisja średnia = "+LAeq_zm_sr[et]+" UA95 (Lord) = "+this.UA95_zm[et], this.DEBUG);
        }

        NMMToolbox.debugMessage("------OBLICZENIA KOŃCOWE ----------------------------------", this.DEBUG);
        for (int et=0; et<eventsTypesNumber; et++) {
            if (et!=bgNoiseIndex) {
                // obiczenie hałasu dla poszczególnych podokresów
                LAeq_im[et]=NMMNoiseCalculator.roznica_log(LAeq_zm_sr[et],LAeq_zm_sr[bgNoiseIndex]);
                NMMToolbox.debugMessage("Imisja = "+LAeq_im[et], this.DEBUG);

                UA95_im[et]=NMMNoiseCalculator.u_roznica_log(LAeq_zm_sr[et], UA95_zm[et],
                        LAeq_zm_sr[bgNoiseIndex], UA95_zm[bgNoiseIndex]);
                NMMToolbox.debugMessage("Niepewnosc = "+UA95_im[et], this.DEBUG);

                // obliczenie poziomu ekwiwalentnego dla podokresów
                LAeq_im_T[et][0]=NMMNoiseCalculator.LEQ((float)LAeq_im[et], this.eventsModel[et][0], 8*3600);
                LAeq_im_T[et][1]=NMMNoiseCalculator.LEQ((float)LAeq_im[et], this.eventsModel[et][1], 2*3600);
                LAeq_im_T[et][2]=NMMNoiseCalculator.LEQ((float)LAeq_im[et], this.eventsModel[et][2], 1*3600);
                NMMToolbox.debugMessage("LAeq uwzgl. czasy pracy w trybach", this.DEBUG);
                NMMToolbox.debugMessage("LAeq dzień   = "+LAeq_im_T[et][0], this.DEBUG);
                NMMToolbox.debugMessage("LAeq wieczor = "+LAeq_im_T[et][1], this.DEBUG);
                NMMToolbox.debugMessage("LAeq noc     = "+LAeq_im_T[et][2], this.DEBUG);
            } else {
                LAeq_im_T[et][0]=-1;
                LAeq_im_T[et][1]=-1;
                LAeq_im_T[et][2]=-1;
                UA95_im_T[et][0]=-1; 
                UA95_im_T[et][1]=-1;
                UA95_im_T[et][2]=-1;
                LAeq_im[et]=-1;
                UA95_im[et]=-1;
            }
        }
        int[] secondsTable_1 = new int[eventsTypesNumber];
        int[] secondsTable_2 = new int[eventsTypesNumber];
        int[] secondsTable_3 = new int[eventsTypesNumber];
        for (int l=0;l<this.eventsModel.length;l++) {
            secondsTable_1[l]=this.eventsModel[l][0];
            secondsTable_2[l]=this.eventsModel[l][1];
            secondsTable_3[l]=this.eventsModel[l][2];
        }        

        this.LAeqT[0]=NMMNoiseCalculator.calculateLeq(this.LAeq_im, secondsTable_1, 8*3600);
        this.LAeqT[1]=NMMNoiseCalculator.calculateLeq(this.LAeq_im, secondsTable_2, 0);
        this.LAeqT[2]=NMMNoiseCalculator.calculateLeq(this.LAeq_im, secondsTable_3, 1*3600);
        NMMToolbox.debugMessage("LAeq finalne !!!", this.DEBUG);
        NMMToolbox.debugMessage("LAeq dzień   = "+LAeqT[0], this.DEBUG);
        NMMToolbox.debugMessage("LAeq wieczor = "+LAeqT[1], this.DEBUG);
        NMMToolbox.debugMessage("LAeq noc     = "+LAeqT[2], this.DEBUG);

        for (int et=0; et<eventsTypesNumber; et++) {
            if (et!=bgNoiseIndex) {
                UA95_im_T[et][0]=UA95_im[et]*this.eventsModel[et][0]*Math.pow(10, 0.1*(LAeq_im[et]-this.LAeqT[0]))/(8*3600);
                UA95_im_T[et][1]=UA95_im[et]*this.eventsModel[et][1]*Math.pow(10, 0.1*(LAeq_im[et]-this.LAeqT[1]))/(16*3600);
                UA95_im_T[et][2]=UA95_im[et]*this.eventsModel[et][2]*Math.pow(10, 0.1*(LAeq_im[et]-this.LAeqT[2]))/(1*3600);
                NMMToolbox.debugMessage("Niepewności do LAeq uwzględn. pracę w trybach", this.DEBUG);
                NMMToolbox.debugMessage("Dzien   = "+UA95_im_T[et][0], this.DEBUG);
                NMMToolbox.debugMessage("Wieczor = "+UA95_im_T[et][1], this.DEBUG);
                NMMToolbox.debugMessage("Noc     = "+UA95_im_T[et][2], this.DEBUG);
            }
        }
        double[] uTable_1= new double[eventsTypesNumber];
        double[] uTable_2= new double[eventsTypesNumber];
        double[] uTable_3= new double[eventsTypesNumber];
        for (int l=0;l<eventsTypesNumber;l++) {
            uTable_1[l]=this.UA95_im_T[l][0];
            uTable_2[l]=this.UA95_im_T[l][1];
            uTable_3[l]=this.UA95_im_T[l][2];
        }
        this.UA95[0]=NMMNoiseCalculator.UR(uTable_1);
        this.UA95[1]=NMMNoiseCalculator.UR(uTable_2);
        this.UA95[2]=NMMNoiseCalculator.UR(uTable_3);
        NMMToolbox.debugMessage("Niepewności finalne A wyniku końcowego", this.DEBUG);
        NMMToolbox.debugMessage("Dzien   = "+this.UA95[0], this.DEBUG);
        NMMToolbox.debugMessage("Wieczor = "+this.UA95[1], this.DEBUG);
        NMMToolbox.debugMessage("Noc     = "+this.UA95[2], this.DEBUG);

        double[] Tab1 = new double[2];
        double[] Tab2 = new double[2];
        double[] Tab3 = new double[2];

        Tab1[0]=this.nmmProj.getCurrentMeasurement().getUncertaintyB();
        Tab1[1]=this.UA95[0];

        Tab2[0]=this.nmmProj.getCurrentMeasurement().getUncertaintyB();
        Tab2[1]=this.UA95[1];

        Tab3[0]=this.nmmProj.getCurrentMeasurement().getUncertaintyB();
        Tab3[1]=this.UA95[2];

        this.U_AB_R_95[0]=NMMNoiseCalculator.UR(Tab1);
        this.U_AB_R_95[1]=NMMNoiseCalculator.UR(Tab2);
        this.U_AB_R_95[2]=NMMNoiseCalculator.UR(Tab3);
        NMMToolbox.debugMessage("Niepewności finalne U 95 A i B wyniku końcowego", this.DEBUG);
        NMMToolbox.debugMessage("Dzien   = "+this.U_AB_R_95[0], this.DEBUG);
        NMMToolbox.debugMessage("Wieczor = "+this.U_AB_R_95[1], this.DEBUG);
        NMMToolbox.debugMessage("Noc     ="+this.U_AB_R_95[2], this.DEBUG);
        
        return recalculatedSuccessfuly;
        
    }

    public NoiseSourceModelType getNoiseModelType() {
        return NoiseSourceModelType.POLISH_REFERENCE_METHOD_INDUSTRIAL_SAMPLING;
    }

    public String getModelName() {
        return this.modelName;
    }

    public String getReport() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getStartTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getEndTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public UUID getMeasurementUUID() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setMeasurementUUID() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public NMMCalculationReport getCalculationReport() {
        return this.calcReport;
    }

    @Override
    public double getNoiseModelResult(NoiseLevelIndicators _nli) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NMMNoiseSourceModelResult getNoiseModelComplexResult(NoiseModelResult _nli) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void recreateListenersArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setModelName(String text) {
        throw new UnsupportedOperationException("Not supported yet.");
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
}
