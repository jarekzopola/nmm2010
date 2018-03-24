/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.surcemodel;

import biz.ekoplan.nmm2010.enums.NoiseLevelIndicators;
import biz.ekoplan.nmm2010.enums.NoiseModelResult;
import biz.ekoplan.nmm2010.enums.NoiseSourceModelType;
import biz.ekoplan.nmm2010.surcemodel.result.NMMNoiseSourceModelResult;
import java.util.UUID;
import nmm2010.NMMCalculationReport;

/**
 *
 * @author Jarosław Kowalczyk
 */
public interface NMMNoiseSourceModel {        
    
    //
    public void recreateListenersArray();
    
    public NoiseSourceModelType getNoiseModelType();
    
    /**
     * Metoda zwraca opis (nazwę) modelu obliczeniowego. Opis wykorzytywany jest
     * jedynie informacyjnie, ewentualnie do wyświetlenia na prezentacjach
     * @return String
     */
    
    public String getModelName();    
    
    
    /**
     * Metoda ustawia opis (nazwę) modelu obliczeniowego. Opis wykorzytywany jest
     * jedynie informacyjnie, ewentualnie do wyświetlenia na prezentacjach
     * @return -
     */
    public void setModelName(String text);
    
    
    /**
     * Metoda zwraca początek viewportu jaki był ustawiony podczas towrzenia
     * modelu źródła hałasu. Nie każdy model źródła hałasu bierze pod uwagę
     * tę wartość. Wartośc ta jest natiomiast brana pod uwagę podczas rysowania
     * wykresu na prezentacji, tj. wykres zaczyna się o tej godzinie nawet je-
     * żeli pomiar rozpoczął się wcześniej
     * @return long
     */
    public long getStartTime();
    
    /**
     * Analogicznie jak getgetStartTime();
     * @return 
     */
    public long getEndTime();
    
    /**
     * Metoda zwraca niepowtarzalny identyfgikator zapisu z miernika. Jest to
     * identyfikator pomiaru aktywnego podczas tworzenia modelu źródła hałasu
     * @return UUID
     */
    public UUID getMeasurementUUID();
    
    /**
     * Zwraca wartośc wskaźnika określonego parametrm _nil
     * @param _nli - wskazuje rodzaj wskaźnika poziomu dźwięku, lub innego
     * @return zwraca wartośc wskaźnika którego typ określony jest parametrem
     *         _nil, albo zwraca -1 jeżeli model tego rodzaju wskaźnika nie
     *         oblicza.
     */
    public double getNoiseModelResult(NoiseLevelIndicators _nli);        
    
    /**
     * Metoda zwraca żłożony wynik modelu źródła hałasu. Może to byc na przykład
     * histogram, krzywa skumulowana, albo coś innego określonego parametrem _nil
     * @param NoiseModelResult
     * @return NMMNoiseSourceModelResult
     */
    public NMMNoiseSourceModelResult getNoiseModelComplexResult(NoiseModelResult _nli);        
    
    /**
     * This method returns string describing calculation procedure that can be
     * part of measurement report. 
     * @return String
     */
    public NMMCalculationReport getCalculationReport();      
    
    /**
     * Dodaje do modelu obliczeniowego obiekt okna dialogowego, które nasłuchuje
     * zmian w modelu obliczeniowym w celu zaktualizowania prezentowanej na ekranie treści
     * @param _l NMMNoiseSourceModelChangedListener
     */
    public void addNoiseSourceModelChangedListener(NMMNoiseSourceModelChangedListener _l);
    
    /**
     * Usuwa z modelu obliczeniowego obiekt okna dialogowego, które nasłuchiwało
     * zmian w modelu obliczeniowym w celu zaktualizowania prezentowanej na ekranie treści
     * @param _l NMMNoiseSourceModelChangedListener
     */
    public void removeNoiseSourceModelChangedListener(NMMNoiseSourceModelChangedListener _l);
    
    /**
     * Sprawdza czy wciąż istnieją dane niezbędne do wykonania obliczeń przez model,
     * ponieważ w międzyczasie ktoś mógł wykasować na przykład jakiś rodzaj zdarzeń
     * akustycznych itp. i modelu nie da się obliczyć
     * @return 
     */
    public boolean isComplete();
    
    public boolean recalculateModel();
}
