/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.surcemodel;

import biz.ekoplan.nmm2010.enums.NoiseSourceModelType;

/**
 *
 * @author jarek
 */
public class NMMNoiseSourceModelTypeDescriptor {
    
    public static String getNoiseSourceModelName(NoiseSourceModelType _nsmt) {
        
        String name=null;               
        
        switch (_nsmt) {
            case POLISH_REFERENCE_METHOD_INDUSTRIAL_SAMPLING:
                name="Metoda próbkowania hałasu od źródeł stacjonarnych";
                break;
            case POLISH_REFERENCE_METHOD_EVENTS:
                name="Metoda pojedynczych zdarzeń od źródeł komunikacyjnych";
                break;
            case POLISH_REFERENCE_METHOD_ROADS_SAMPLING:
                name="Metoda próbkowania hałasu od źródeł komunikacyjnych";
                break;
            case SIMPLE_EBASED_MEASUREMENT:
                name="Prosty pomiar w oparciu o zarejestrowane próbki";
                break;
            case SIMPLE_CONTINOUS_MODEL:
                name="Prosty pomiar w oparciu o cały zapis zmian poziomu hałasu";
                break;
            case SOUND_POWER_LEVEL_MODEL:
                name="Sound power level calculation model.";
                break;
            case OCCUPATIONAL_NOISE_ACTIVITIES:
                name="Work exposition to noise";
        }        
        return name;
    }
    
    public static NoiseSourceModelType getNoiseSourceModelType(String _nsmn) {
        
        NoiseSourceModelType type=null;
        
        switch (_nsmn) {
            case "Metoda próbkowania hałasu od źródeł stacjonarnych":
                type=NoiseSourceModelType.POLISH_REFERENCE_METHOD_INDUSTRIAL_SAMPLING;
                break;
            case "Metoda pojedynczych zdarzeń od źródeł komunikacyjnych":
                type=NoiseSourceModelType.POLISH_REFERENCE_METHOD_EVENTS;
                break;
            case "Metoda próbkowania hałasu od źródeł komunikacyjnych":
                type=NoiseSourceModelType.POLISH_REFERENCE_METHOD_ROADS_SAMPLING;
                break;
            case "Prosty pomiar w oparciu o zarejestrowane próbki":
                type=NoiseSourceModelType.SIMPLE_EBASED_MEASUREMENT;
                break;
            case "Prosty pomiar w oparciu o cały zapis zmian poziomu hałasu":
                type=NoiseSourceModelType.SIMPLE_CONTINOUS_MODEL;
                break;
            case "Sound power level calculation model.":
                type=NoiseSourceModelType.SOUND_POWER_LEVEL_MODEL;
                break;
        }        
        return type;
    }
    
}
