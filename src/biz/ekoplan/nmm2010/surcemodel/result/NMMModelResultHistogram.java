/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.surcemodel.result;

import java.io.Serializable;

/**
 *
 * @author jarek
 */
public class NMMModelResultHistogram implements NMMNoiseSourceModelResult, Serializable {
    
    double[] frequency;
    
    public NMMModelResultHistogram() {
        frequency= new double[140];
    }
    
    public void setFerquency(int _range, double _frequency) {
        this.frequency[_range]=_frequency;
    }
    
    public double getFrequency(int _range) {
        return this.frequency[_range];
    }

    public void increaseFerquency(int _range) {
        //System.out.println("Range:"+_range);
        //System.out.println("Długośc tablicy:"+this.frequency.length);
        this.frequency[_range]++;
    }
    
    private double getMaxFrequency() {
        
        double maxf=0;
        
        for (int i=0; i<140; i++) {
            if (maxf<this.frequency[i]) {
                maxf=this.frequency[i];
            }
        }        
        return maxf;
    }
    
    private double getSumFrequency() {
        
        double sum=0;
        for (int i=0; i<140; i++) {
            sum=sum+this.frequency[i];
        }        
        return sum;
    }
    
    public void normalize(int _maxScaleValue) {
        
        double maxValue=this.getMaxFrequency();
        double coefficient=_maxScaleValue/maxValue;
        
        for (int i=0; i<140; i++) {
            this.frequency[i]=this.frequency[i]*coefficient;
        }
    }
    
    public void normalizeTo100Percent() {
        
        double sum=0;
        
        double sumt=this.getSumFrequency();
        for (int i=0; i<140; i++) {
            this.frequency[i]=100*(this.frequency[i]/sumt);
            sum=sum+this.frequency[i];
        }
        System.out.println("Suma: "+sum+" (powinno być dokładnie 100%");
        
    }
    
}
