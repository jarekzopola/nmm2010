/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.enums;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 *
 * @author jarek
 */
public class NMMModelPresentationMap {

    ArrayList <NMMModelPresentationRelation> mpr = new ArrayList<NMMModelPresentationRelation>();
    final int MAX_NUMBER_OF_AVAILABLE_MODELS=10;
    
    public NMMModelPresentationMap() {
        
        mpr.add(new NMMModelPresentationRelation(NoiseSourceModelType.SIMPLE_CONTINOUS_MODEL,
                PresentationType.DAY_AND_NIGHT));
        mpr.add(new NMMModelPresentationRelation(NoiseSourceModelType.SIMPLE_CONTINOUS_MODEL,
                PresentationType.SIMPLE_STATS_PRESENTATION));
        mpr.add(new NMMModelPresentationRelation(NoiseSourceModelType.SIMPLE_CONTINOUS_MODEL,
                PresentationType.SIMPLE_CONTINUOUS));
        mpr.add(new NMMModelPresentationRelation(NoiseSourceModelType.SIMPLE_CONTINOUS_MODEL,
                PresentationType.A4_STATS_PRESENTATON));
        mpr.add(new NMMModelPresentationRelation(NoiseSourceModelType.SIMPLE_EBASED_MEASUREMENT,
                PresentationType.SIMPLE_PRESENTATION));
        mpr.add(new NMMModelPresentationRelation(NoiseSourceModelType.SIMPLE_EBASED_MEASUREMENT,
                PresentationType.SIMPLE_CONTINUOUS));
        mpr.add(new NMMModelPresentationRelation(NoiseSourceModelType.SIMPLE_EBASED_MEASUREMENT,
                PresentationType.SIMPLE_STATS_PRESENTATION));
        mpr.add(new NMMModelPresentationRelation(NoiseSourceModelType.SIMPLE_EBASED_MEASUREMENT,
                PresentationType.A4_STATS_PRESENTATON));
        mpr.add(new NMMModelPresentationRelation(NoiseSourceModelType.POLISH_REFERENCE_METHOD_ROADS_SAMPLING,
                PresentationType.DAY_AND_NIGHT));
        mpr.add(new NMMModelPresentationRelation(NoiseSourceModelType.POLISH_REFERENCE_METHOD_EVENTS,
                PresentationType.DAY_AND_NIGHT));
        mpr.add(new NMMModelPresentationRelation(NoiseSourceModelType.POLISH_REFERENCE_METHOD_EVENTS,
                PresentationType.MEGA_SINGLE_EVENTS));
        mpr.add(new NMMModelPresentationRelation(NoiseSourceModelType.SIMPLE_CONTINOUS_MODEL,
                PresentationType.DAY_AND_NIGHT0));
        mpr.add(new NMMModelPresentationRelation(NoiseSourceModelType.SOUND_POWER_LEVEL_MODEL,
                PresentationType.A4_SOUNDPOWERLEVEL_BASIC));
        mpr.add(new NMMModelPresentationRelation(NoiseSourceModelType.NOISE_WALL_ATTENUATION,
                PresentationType.A4_NOISE_WALL_ATTENUATION));
        mpr.add(new NMMModelPresentationRelation(NoiseSourceModelType.POLISH_REFERENCE_METHOD_ROADS_SAMPLING,
                PresentationType.ROAD_NOISE_SAMPLING));
        mpr.add(new NMMModelPresentationRelation(NoiseSourceModelType.POLISH_REFERENCE_METHOD_ROADS_SAMPLING,
                PresentationType.ROAD_NOISE_SAMPLING0));
    }
    
    public ArrayList<NoiseSourceModelType> getAvailableModels(PresentationType _pt) {
        
        ArrayList<NoiseSourceModelType> nsmt = new ArrayList<NoiseSourceModelType>();
        int cntr=0;
        
        for (NMMModelPresentationRelation tmpNpr : mpr) {
            if (tmpNpr.getPresentationType()==_pt) {
                nsmt.add(tmpNpr.getNoiseSourceModelType());
            }
        }                        
        return nsmt;        
    }
    
        public ArrayList<PresentationType> getAvailablePresentations(NoiseSourceModelType _nsmt) {
        
        ArrayList<PresentationType> nsmt = new ArrayList<PresentationType>();
        int cntr=0;
        
        for (NMMModelPresentationRelation tmpNpr : mpr) {
            if (tmpNpr.getNoiseSourceModelType()==_nsmt) {
                nsmt.add(tmpNpr.getPresentationType());
            }
        }                        
        return nsmt;        
    } 
}
