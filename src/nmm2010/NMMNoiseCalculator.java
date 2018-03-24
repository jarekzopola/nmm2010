/*
 * NoiseCalculator.java
 *
 * Created on 27 stycze� 2007, 22:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package nmm2010;

import biz.ekoplan.nmm2010.enums.TimePeriods;
import java.lang.Math.*;
import java.util.UUID;

/**
 *
 * @author user
 */
public class NMMNoiseCalculator {
    
    /** Creates a new instance of NoiseCalculator */
    public NMMNoiseCalculator() {
    }

    static public double UR(double[] _tablica) {

        double uR=0;
        for (int i=0; i<_tablica.length;i++) {
            uR=uR+_tablica[i]*_tablica[i];
        }
        uR=Math.sqrt(uR);
        return uR;
    }

    //odejmowanie logarytmiczne
    static public float roznica_log(float poziom_imisji, float poziom_tla) {        
        float rl = 10 * (float)Math.log10(Math.pow(10,poziom_imisji/10)-Math.pow(10,poziom_tla/10));
        return rl;
    }

    static public double LEQ(double _level, int _time, int _ref_time) {

        double le;
        le = 10*Math.log10((_time * Math.pow(10, (_level / 10)))/_ref_time);
        return le;
    }


    //odejmowanie logarytmiczne
    static public double roznica_log(double poziom_imisji, double poziom_tla) {
        double rl = 10 * (float)Math.log10(Math.pow(10,poziom_imisji/10)-Math.pow(10,poziom_tla/10));
        return rl;
    }
    
    //niepewność wynikowa różnicy logarytmicznej
    static public float u_roznica_log(float L_imisji, float U_imisji, float L_tla, float U_tla) {

        float url = (float)Math.sqrt((Math.pow(U_imisji * Math.pow(10,(0.1 * L_imisji)),2)+ Math.pow(U_tla * Math.pow(10,(0.1 * L_tla)),2))/
                                ((Math.pow((Math.pow(10,(0.1 * L_imisji)) - Math.pow(10,(0.1 * L_tla))),2))));
        return url;
    }

    static public double u_roznica_log(double L_imisji, double U_imisji, double L_tla, double U_tla) {

        double url = (float)Math.sqrt((Math.pow(U_imisji * Math.pow(10,(0.1 * L_imisji)),2)+ Math.pow(U_tla * Math.pow(10,(0.1 * L_tla)),2))/
                                ((Math.pow((Math.pow(10,(0.1 * L_imisji)) - Math.pow(10,(0.1 * L_tla))),2))));
        return url;
    }
    
    static public float tStudent(int liczba_probek) {
    
        double[] rozklad_tStudenta={12.70620473,4.30265273,3.182446305,2.776445105,2.570581835,2.446911846,2.364624251,                                
                                    2.306004133,2.262157158,2.228138842,2.200985159,2.178812827,2.160368652,2.144786681,
                                    2.131449536,2.119905285,2.109815559,2.100922037,2.09302405,2.085963441,2.079613837,
                                    2.073873058,2.068657599,2.063898547,2.059538536,2.055529418,2.051830493,2.048407115,
                                    2.045229611,2.042272449,2.039513438,2.036933334,2.034515287,2.032244498,2.030107915,
                                    2.028093987,2.026192447,2.024394147,2.022690901,2.02107537,2.019540948,2.018081679,
                                    2.016692173,2.015367547,2.014103359,2.012895567,2.01174048,2.010634722,2.009575199,
                                    2.008559072,2.007583728,2.006646761,2.005745949,2.004879275,2.004044769,2.003240704,
                                    2.002465444,2.001717468,2.000995361,2.000297804,1.999623567,1.998971498,1.998340522,
                                    1.997729633,1.997137887,1.996564396,1.996008331,1.995468907,1.99494539,1.994437086,
                                    1.993943341,1.993463539,1.992997097,1.992543466,1.992102124,1.991672579,1.991254363,
                                    1.990847036,1.990450177,1.990063387,1.989686288,1.989318521,1.988959743,1.988609629,
                                    1.988267868,1.987934166,1.987608241,1.987289823,1.986978657,1.986674497,1.98637711,
                                    1.986086272,1.985801768,1.985523395,1.985250956,1.984984263,1.984723136,1.984467404,
                                    1.9842169,1.983971466,1.98373095,1.983495205,1.98326409,1.983037471,1.982815217,
                                    1.982597204,1.982383312,1.982173424,1.98196743,1.981765221,1.981566695,1.981371752,
                                    1.981180296,1.980992234,1.980807476,1.980625937,1.980447532,1.980272226,1.980099853,
                                    1.979930381,1.979763738,1.979599854,1.97943866,1.979280091,1.979124084,1.978970576,
                                    1.978819508,1.978670823,1.978524465,1.978380378,1.978238512,1.978098814,1.977961236,
                                    1.97782573,1.977692248,1.977560747,1.977431183,1.977303512,1.977177694,1.977053689,
                                    1.976931458,1.976810963,1.976692167,1.976575034,1.976459531,1.976345623,1.976233277,
                                    1.976122461,1.976013145,1.975905298,1.97579889,1.975693894,1.975590281,1.975488024,
                                    1.975387096,1.975287473,1.975189128,1.975092037,1.974996177,1.974901524,1.974808055,
                                    1.974715749,1.974624584,1.974534539,1.974445593,1.974357726,1.974270919,1.974185153,
                                    1.974100409,1.974016669,1.973933915,1.97385213,1.973771297,1.9736914,1.973612422,
                                    1.973534347,1.973457161,1.973380848,1.973305393,1.973230782,1.973157001,1.973084036,
                                    1.973011873,1.9729405,1.972869904,1.972800071,1.97273099,1.972662649,1.972595036,
                                    1.972528138,1.972461946,1.972396447,1.972331631,1.972267488,1.972204006,1.972141177,
                                    1.972078988,1.972017432,1.971956498,1.971896178};
        float wynik=0;
        wynik=(float)rozklad_tStudenta[liczba_probek-1];
        return wynik;
    }
    
    
    static public float Lord95(float[] skladniki) {
        double[] rozklad_Lorda95 = {0, 0, 1.3, 0.7, 0.5, 0.4, 0.3333, 0.3, 0.25, 0.23};
        float maks=skladniki[0];
        float min=skladniki[0];
        float lord;
        double lordCoef;
   
        for (int i=0; i<skladniki.length;i++) {
            if (skladniki[i]>maks) {
                maks=skladniki[i];
            }
            if (skladniki[i]<min) {
                min=skladniki[i];
            }
        }
        if (skladniki.length<=10) {
            lordCoef=rozklad_Lorda95[skladniki.length-1];
        } else {
            lordCoef=rozklad_Lorda95[9];
        }
        lord =(float)((maks-min)*lordCoef);        
        return lord;
    }
    
    
    static public float SredniaLog(float[] skladniki) {
    
        float sumaPoteg=0;
        float srednia=0;
        
        for (int i=0;i<skladniki.length; i++) {
            sumaPoteg=(float)(sumaPoteg+Math.pow(10,(skladniki[i]/10)));
        }
        sumaPoteg=sumaPoteg/skladniki.length;
        srednia = (float)(10*Math.log10(sumaPoteg));
        return srednia;
    }
    

    
    
    
    /**
     * 
     * @param laeqi
     * @param times
     * @param ref_time
     * @return
     * @depricated
     */    
    static public float calculateLeq(float[] laeqi, int[] times, int ref_time) {
                           
        float laeq=0;
        double suma_nawiasow=0;
        int rozmiar=laeqi.length;        
        for (int i=0; i<rozmiar; i++) {            
            suma_nawiasow=suma_nawiasow+(times[i]*Math.pow(10,(double)laeqi[i]/10));
        }
        suma_nawiasow=suma_nawiasow/ref_time;
        laeq=10*(float)Math.log10(suma_nawiasow);
        return laeq;
    }

    static public double calculateLeq(double[] laeqi, int[] times, int ref_time) {
                   
        float laeq=0;
        double suma_nawiasow=0;
        int rozmiar=laeqi.length;
        for (int i=0; i<rozmiar; i++) {
            suma_nawiasow=suma_nawiasow+(times[i]*Math.pow(10,(double)laeqi[i]/10));
        }
        suma_nawiasow=suma_nawiasow/ref_time;
        laeq=10*(float)Math.log10(suma_nawiasow);
        return laeq;
    }
    
    //Niepewność standardowa UAk wartości średniej ekspozycyjnego poziomu dźwięku(LAEk) dla k-tej klasy
    static public float calculateLAEkUncertainty(float[] values) {
        
        float wynik=0;
        float UA = 0;  
        float stDev=0;
        stDev=calculateStandardDeviation(values);        
        UA=calculateUA(values);
        wynik=(float)((stDev/values.length)*Math.sqrt(UA));
        return wynik;
    }
    
    //Niepewnosc rozszerzona UA95k  warto�ci �redniej ekspozycyjnego poziomu d�wi�ku (LAEk) dla k-tej klasy        
    static public float calculateRLAEkUncertainty(float[] values) {
        
        float wynik=0;
        float UA = 0;  
        float stDev=0;
        stDev=calculateStandardDeviation(values);        
        UA=calculateUA(values);
        wynik=(float)((stDev/values.length)*Math.sqrt(UA));
        wynik=tStudent(values.length)*wynik;
        return wynik;
    }
    
    //
    static public float calculateUA(float[] values) {
        
        float wynik=0;
        float tmpSum=0;
        float LAEk = 0; //�rednia warto�� poziomu ekspozycyjnego
        LAEk=SredniaLog(values);
        
        for (int i=0; i<values.length;i++) {
            tmpSum=tmpSum+(float)Math.pow(Math.pow(10,(0.1*(values[i]-LAEk))),2);
        }
        wynik=tmpSum;
        return wynik;
    }
    
    //Obliczanie odchylenia standardowego
    static public float calculateStandardDeviation(float[] values) {
        
        float wynik=0;
        float srednia=0;
        float suma=0;
        
        srednia=NMMNoiseCalculator.SredniaLog(values);
        for (int i=1;i<values.length;i++) {
            suma=suma+(float)Math.pow((values[i-1]-srednia),2);
        }
        wynik=(float)Math.sqrt(suma/(values.length-1));
        return wynik;
    }
    
    //Obliczanie niepewności całkowitej
    static public float calculateRUncertainty(float[] unc) {
        
        float wynik=0;
        int rozmiar = unc.length;
                
        for (int i=0; i<rozmiar; i++) {
            wynik=wynik+(unc[i]*unc[i]);
        }
        wynik=(float)Math.sqrt(wynik);
        return wynik;        
    }
    
    //Obliczanie niepewności całkowitej dla dwóch zmiennych
    static public float calculateRUncertainty(double u1, double u2) {
        
        float wynik;
        wynik=(float)Math.sqrt((u2*u2)+(u1*u1));
        return wynik;        
    }

    
}


