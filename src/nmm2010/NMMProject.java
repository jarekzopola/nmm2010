/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nmm2010;

import biz.ekoplan.nmm2010.devices.NMMNoiseMeasurementSet;
import biz.ekoplan.nmm2010.devices.NMMNoiseMeasurementSets;
import biz.ekoplan.nmm2010.enums.NoiseSourceModelType;
import biz.ekoplan.nmm2010.enums.RecordValueType;
import biz.ekoplan.nmm2010.enums.TimePeriods;
import biz.ekoplan.nmm2010.events.NMMEventChangedEvent;
import biz.ekoplan.nmm2010.events.NMMEventChangedListener;
import biz.ekoplan.nmm2010.measurement.NMMMeasurement;
import biz.ekoplan.nmm2010.measurement.NMMMeasurementChangedEvent;
import biz.ekoplan.nmm2010.measurement.NMMMeasurementListener;
import biz.ekoplan.nmm2010.measurement.NMMMeasurements;
import biz.ekoplan.nmm2010.nmmproject.NMMProjectChangedEvent;
import biz.ekoplan.nmm2010.nmmproject.NMMProjectChangedListener;
import biz.ekoplan.nmm2010.nmmproject.NMMProjectModelsChanged;
import biz.ekoplan.nmm2010.nmmproject.NMMProjectModelsListener;
import biz.ekoplan.nmm2010.presentations.NMMPresentation;
import biz.ekoplan.nmm2010.presentations.NMMPresentations;
import biz.ekoplan.nmm2010.surcemodel.NMMNoiseSourceModel;
import biz.ekoplan.nmm2010.surcemodel.NMMNoiseSourceModels;
import biz.ekoplan.nmm2010.surcemodel.sminterface.NMMAbstractNoiseSourceModelInterface;
import biz.ekoplan.nmm2010.toolbox.NMMToolbox;
import biz.ekoplan.nmm2010.toolbox.TimeConverter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.awt.Color;
import java.io.Serializable;
import java.util.*;
import static java.util.Objects.isNull;
import javax.swing.JOptionPane;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Jaroslaw Kowalczyk
 */
public class NMMProject extends Observable implements Serializable,     
    NMMMeasurementListener, NMMEventChangedListener, TreeModel {
    
    private static final boolean DEBUG = true;
    private static final long serialVersionUID=1L;
    private String jreVersion = System.getProperty("java.version");
    
    
    private String nmmManufacturer = "NMM 2018   (c)EKOPROJEKT 2007-2017    www.ekoprojekt.biz";

    private NMMEventType lastUsedEventType= new NMMEventType("noname");
    
    //project basic data
    private long projectBeginTime=Long.MAX_VALUE;
    private long projectEndTime=Long.MIN_VALUE;
    private long projectTimeResolution=1000;
    private String projectAuthor="Project author not set";
    private String projectTitle="Project title not set";
    private String projectSubtitle="Project subtitle not set";
    private String projectRemarks="Project remarks not set";
    private String projectCommisionNumber="ECP/LA/nn/yyyy";
    private String raportNumber="nn/mm/yyyy";    
    private NMMMeasurements projectMeasurements = new NMMMeasurements();
    ArrayList<NMMEvent> projectEvents = new ArrayList<>();
    TreeSet<NMMEventType> eventTypes = new TreeSet<>();
    int currentMeasurement=-1;
    boolean projectSaved = false;
    NMMEvent currentSelection= new NMMEvent("cs",0,0,null,Color.GRAY);
    
    //
    private transient ArrayList<NMMAbstractNoiseSourceModelInterface> NMMProjectModelsListeners = new ArrayList<>();
    private transient ArrayList<NMMProjectChangedListener> NMMProjectChangedListeners = new ArrayList<>();
    
    //prezentations
    private NMMPresentations nmmPresentations = new NMMPresentations();
    private NMMNoiseSourceModels nmmNoiseSourceModels = new NMMNoiseSourceModels();

    //noise monitoring sets
    private NMMNoiseMeasurementSets nmmNMSets =
            new NMMNoiseMeasurementSets();
    
    /**
     * Constructs NMMProject object.
     */
    public NMMProject() {                                
        NMMNoiseMeasurementSet nms0=new NMMNoiseMeasurementSet("Miernik poziomu dźwięku",
               "Nowy całkujący miernik poziomu dźwięku.",
               "Brak informacji o świadectwie wzorcowania.",0.9);               
        this.nmmNMSets.addElement(nms0);
        System.out.println("Czy jestem pusty : "+this.NMMProjectModelsListeners.isEmpty());
    }            

    public boolean saveAs(String _path) {
        boolean saved=false;        
        XStream xstream = new XStream(new DomDriver());
        String xml = xstream.toXML(this);
        System.out.println(xml);
        return saved;
    }
                
    /**
     * Check if measurement identified by MUID is used in any noise nource model
     * @param _muid
     * @return 
     */    
    public boolean isMeasurementUsed(UUID _muid) {
    
        boolean isUsed=false;
        
        for (int i=0;i<this.nmmNoiseSourceModels.getSize();i++) {
            if ((this.nmmNoiseSourceModels.getElementAt(i).getMeasurementUUID().compareTo(_muid))==0) {
                isUsed=true;
            }
        }                      
        return isUsed;        
    }
        
    /**
     * Use this method to find out what noise source model types have been used
     * so far in current project. This is method required by noise source model
     * manager.
     * @return arrat
     */
       
    public TreeSet<NoiseSourceModelType> getNoiseSourceModelTypes() {
                
        TreeSet<NoiseSourceModelType> nsmts = new TreeSet<NoiseSourceModelType>();
        
        for (int i=0; i<this.nmmNoiseSourceModels.getSize();i++) {
            nsmts.add(this.nmmNoiseSourceModels.getElementAt(i).getNoiseModelType());            
        }                        
        return nsmts;                
    }
    
    public Object[] getNoiseSourceModelTypesArray() {
                
        TreeSet<NoiseSourceModelType> nsmts = new TreeSet<>();
        
        for (int i=0; i<this.nmmNoiseSourceModels.getSize();i++) {
            nsmts.add(this.nmmNoiseSourceModels.getElementAt(i).getNoiseModelType());            
        }                        
        return nsmts.toArray();                
    }
    
    public String getManufacturer() {
        return this.nmmManufacturer;
    }
        
    /**
     * Returns array of NMMEventType
     * @return NMMEventType[]
     */
    public Object[] getEventTypes() {
        return this.eventTypes.toArray();
    }
    
    public NMMEventType[] getEventTypeTypes() {
        
        NMMEventType[] eta = new NMMEventType[this.eventTypes.size()];
        Iterator itr=this.eventTypes.iterator();
        int counter=0;
        while (itr.hasNext()) {
            eta[counter++]=(NMMEventType)itr.next();
        }                        
        return eta;               
    }
    
    /**
     * Divide measurement history into two measurement histories. Copy properties
     * and recalculates Laeq values for both new measurement histories. 
     * @param _time
     * @return UUID (unique identifier of new measurement)
     */
    public UUID splitMeasurement(long _time) {
    
        UUID newUUID = null; 
        NMMMeasurement cMs;
        
        cMs=this.getCurrentMeasurement();
        
        //measurement history division is possible only if division time is
        //within measurement history time range (obviously)
        if (cMs.isWithinMeasurement(_time-1000, _time+1000)) {                   
            int startIndex=cMs.getRecordIndex(_time);
            NMMMeasurement newMs = new NMMMeasurement(_time);
            newUUID=newMs.getMUID();
            //copy records from split time to the end of current measurement
            for (int i=startIndex;i<cMs.getMeasurementLength();i++) {
                newMs.addRecord(RecordValueType.LAeq, cMs.getRecordValue(i, RecordValueType.LAeq));
            }        
            //copy measurement properties
            newMs.setBackgroundNoiseLevel(cMs.getBackgroundNoiseLevel());
            newMs.setCoordinateX(cMs.getCoordinateX());
            newMs.setCoordinateY(cMs.getCoordinateY());
            newMs.setDescription(cMs.getDescription());
            newMs.setDrawLabels(cMs.getDrawLabels());
            newMs.setFInalCalibration(cMs.getFinalCalibration());
            newMs.setInitialCalibration(cMs.getInitialCalibration());
            newMs.setHeight(cMs.getHeight());
            newMs.setMeasurementColor(Color.GRAY);
            newMs.setMeasurementSet(cMs.getMeasurementSet());
            newMs.setOperator(cMs.getOperator());
            newMs.setPicture(cMs.getPicture());
            newMs.setRemarks(cMs.getRemarks());
            newMs.setUncertaintyB(cMs.getUncertaintyB());
            newMs.setVisible(true);
            
            //delete copied records form current measurement
            cMs.deleteRecords(startIndex, cMs.getMeasurementLength()-1);
            
            //if one measurement history is being divided, new Leq values
            //must be calculatet for both measurement histories
            newMs.updateLeq();            
            cMs.updateLeq();
            
            //now we can add this new measurement to the project
            this.addMeasurement(newMs);  
            
            //inform listeners about new measurement in project
            this.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this));
            
        } else {
            JOptionPane.showMessageDialog(null, "You can split only current measurement!", 
                    "Split error.", JOptionPane.WARNING_MESSAGE);
        }             
        return newUUID;                
    }
    
    public synchronized void addProjectModelsListener(NMMAbstractNoiseSourceModelInterface l)
    {
        
        if (this.NMMProjectModelsListeners==null) {
            NMMProjectModelsListeners = new ArrayList<NMMAbstractNoiseSourceModelInterface>();
        }
        if (!NMMProjectModelsListeners.contains(l)) {
            NMMProjectModelsListeners.add(l);
            NMMToolbox.debugMessage("Liczba nasłuchiwaczy zmian w modelach projektu: "+this.NMMProjectModelsListeners.size(), DEBUG);
        }
    }
    
    public synchronized void addProjectChangedListener(NMMProjectChangedListener l)
    {
        
        System.out.println("Dodaję nasłuchiwacza zmian projektu:"+l.toString());
        if (this.NMMProjectChangedListeners==null) {
            NMMProjectChangedListeners = new ArrayList<NMMProjectChangedListener>();
        }
        if (!NMMProjectChangedListeners.contains(l)) {
            NMMProjectChangedListeners.add(l);
            NMMToolbox.debugMessage("Liczba nasłuchiwaczy zmian w projekcie: "+this.NMMProjectChangedListeners.size(), DEBUG);
        }
    }

    public synchronized void removeMeasurementListener(NMMMeasurementListener l) {
        NMMProjectModelsListeners.remove(l);
    }
    
    protected void fireNMMProjectChangedEvent(NMMProjectChangedEvent _mEvent) {
       Object[] listeners = NMMProjectChangedListeners.toArray();        
        int numListeners = listeners.length;
        for (int i = 0; i<numListeners; i+=1) {
          if (listeners[i] instanceof NMMProjectChangedListener) {
              System.out.println("Generuję zdarzenie zmian w obiekcie projektu dla: "+listeners[i].toString());
               ((NMMProjectChangedListener)listeners[i]).dispatchNMMProjectChangedEvent(_mEvent);
          }
        }        
    }
    
    protected void fireNMMProjectModelsChangedEvent(NMMProjectModelsChanged _mEvent) {

        //TODO Tutaj się wywala, bo po odczytaniu z pliku nie ma żadnych nasłuchiwaczy.
        //Nasłuchiwacze mudzą się dodawać po odczytaniu projektu z pliku !!!
        
        System.out.println("Czy jestem pusty : "+this.NMMProjectModelsListeners.isEmpty());
        
        if (!this.NMMProjectModelsListeners.isEmpty()) {
            Object[] listeners = this.NMMProjectModelsListeners.toArray();        
            int numListeners = listeners.length;
            for (int i = 0; i<numListeners; i+=2) {
                if (listeners[i] instanceof NMMProjectModelsListener) {
                    ((NMMProjectModelsListener)listeners[i]).dispatchNMMProjectModelsChanged(_mEvent);
                }
            }    
        }                
    }
    
    public void addNoiseSourceModel(NMMNoiseSourceModel _model) {                        
        this.nmmNoiseSourceModels.addElement(_model);        
        fireNMMProjectModelsChangedEvent(new NMMProjectModelsChanged(this));                
    }
    
    public boolean deleteNoiseSourceModel(NMMNoiseSourceModel _nsm) {
        
        boolean success = false;
        success=this.nmmNoiseSourceModels.removeModel(_nsm);
        return success;
    }
    
    public boolean deleteNoiseSourceModel(String _name, NoiseSourceModelType _nsmt) {
        
        boolean modelRemoved = false;
        for (int i=0; i<this.nmmNoiseSourceModels.getSize();i++) {
            if (this.nmmNoiseSourceModels.getElementAt(i).getModelName().equals(_name) && 
                    (this.nmmNoiseSourceModels.getElementAt(i).getNoiseModelType().equals(_nsmt))) {
                this.nmmNoiseSourceModels.removeElement(i);
                modelRemoved=true;
            }
        }        
        return modelRemoved;        
    }
    
    /** 
     * Use this method to get ArrayList of noise source models of specyfic type
     * @param _nsmt - noise sourec model type (NoiseSourceModelType)
     * @return ArrayList of matching noise source models
     */    
    public Object[] getNoiseSourceModels(NoiseSourceModelType _nsmt) {
        
        ArrayList<NMMNoiseSourceModel> nsms = new ArrayList<NMMNoiseSourceModel>();
        
        System.out.println("Poszukuję modeli typu: "+ _nsmt);
        
        for (int i=0; i<this.nmmNoiseSourceModels.getSize();i++) {
            
            System.out.println("Sprawdzam model: "+this.nmmNoiseSourceModels.getElementAt(i).getNoiseModelType());
            if (this.nmmNoiseSourceModels.getElementAt(i).getNoiseModelType().equals(_nsmt)) {
                nsms.add(this.nmmNoiseSourceModels.getElementAt(i));
            }            
        }        
        return nsms.toArray();
    }
    
        /** 
     * Use this method to get ArrayList of all noise source models     
     * @return ArrayList of all noise source models
     */    
    public Object[] getNoiseSourceModels() {
        
        ArrayList<NMMNoiseSourceModel> nsms = new ArrayList<NMMNoiseSourceModel>();
        
        for (int i=0; i<this.nmmNoiseSourceModels.getSize();i++) {            
                nsms.add(this.nmmNoiseSourceModels.getElementAt(i));
            }                    
        return nsms.toArray();
    }
    
    public NMMNoiseSourceModel getNoiseSourceModel(int _index) {
        return this.nmmNoiseSourceModels.getElementAt(_index);
    }
    
    public int getNumberOfNoiseSourceModels() {
        return this.nmmNoiseSourceModels.getSize();
    }
    
    public boolean deleteMeasurement(int _index) {

        // assume we will succeed
        boolean success=true;

        try {
            // trying
            this.projectMeasurements.removeMeasurement(_index);
            this.updateProjectTimeRanges();
            // if delete current measurement, then assign new measurement
            if (_index==this.currentMeasurement) {
                this.currentMeasurement=this.getMeasurementsNumber()-1;
            }
        } catch (IndexOutOfBoundsException e) {
            // unfortunately havn't succeeded
            success=false;
        }
        return success;
    }

    public boolean deleteEvent(int[] _indexes) {
        
        // assume we will succeed
        boolean success=true;

        try {        
            for (int i=_indexes.length-1; i>=0; i--) {
                success=this.deleteEvent(_indexes[i]);
            }
        } catch (IndexOutOfBoundsException e) {
            // unfortunately havn't succeeded
            success=false;
        }        
        //poinformuj że usunięto jedno zdarzenie z listy zdarzeń projektu
        this.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this));
        return success;
    }

    public boolean deleteEvent(int _index) {

        // assume we will succeed
        boolean success=true;

        try {
            // tryig            
            this.projectEvents.remove(_index);
        } catch (IndexOutOfBoundsException e) {
            // unfortunately havn't succeeded
            success=false;
        }
        //poinformuj że zostało usunięte jedno zdarzenie z listy zdarzeń 
        //projektu
        this.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this));
        return success;
    }
    
    public boolean deleteEvent(NMMEvent nmmEvent) {

        // assume we succeed
        boolean success=true;

        try {
            // tryig            
            this.projectEvents.remove(nmmEvent);
        } catch (IndexOutOfBoundsException e) {
            // unfortunately havn't succeeded
            success=false;
        }
        //poinformuj że zostało usunięte jedno zdarzenie z listy zdarzeń 
        //projektu
        this.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this));
        return success;
    }

    /**
     * Get collection of measuring sets in this project
     * @return
     */
    public NMMNoiseMeasurementSets getMeasuringSets() {                
        return this.nmmNMSets;        
    } 
    
    /**
     * Add measurement set to the project
     * @return 
     */
    public void addMeasurementSet(NMMNoiseMeasurementSet _nms) {
        if (_nms!=null) {
            this.nmmNMSets.addElement(_nms);
        }
    }

    public NMMPresentations getPresentations() {
        return this.nmmPresentations;
    }

    public void addPresentation(NMMPresentation _nmmp) {
        this.nmmPresentations.addElement(_nmmp);        
    }
    
    public NMMPresentation getPresentation(int _index) {
        return (NMMPresentation) this.nmmPresentations.getElementAt(_index);
    }

    public void setPresentation(NMMPresentation _p, int _index) {
       this.nmmPresentations.removeElement(_index);
       this.nmmPresentations.addElement(_p);
    }

    public String getReportNumber() {
        return this.raportNumber;
    }

    public void setReportNumber(String _rn) {
        this.raportNumber=_rn;
    }

    public String getProjectCommisionNumber() {
        return this.projectCommisionNumber;
    }

    public void setPrjectCommisionNumber(String _pcn) {
        this.projectCommisionNumber=_pcn;
    }

    public String getProjectRemarks() {
        return this.projectRemarks;
    }
    
    public void setRemarks(String _remarks) {
        this.projectRemarks=_remarks;
        this.setSaved(false);
    }

    public void setSubtitle(String _subtitle) {
        this.projectSubtitle=_subtitle;
        this.setSaved(true);
        this.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this));
    }

     /**
     *
     * @return time of earliest record in whole project
     */
    private long getAllMeasurementsRightTime() {
        long amlt=Long.MIN_VALUE;
        for (int i=0; i<this.getMeasurementsNumber();i++) {            
            if (amlt<this.getMeasurement(i).getMeasurementEndTime()) {                
                amlt=this.getMeasurement(i).getMeasurementEndTime();
            } else {                
            }
        }
        return amlt;
    }

    /**
     * 
     * @return time of earliest record in whole project
     */
    private long getAllMeasurementsLeftTime() {
        long amlt=Long.MAX_VALUE;
        for (int i=0; i<this.getMeasurementsNumber();i++) {
            if (amlt>this.getMeasurement(i).getMeasurementBeginTime()) {
                amlt=this.getMeasurement(i).getMeasurementBeginTime();
            }
        }
        return amlt;
    }

    /**
     * Start this method to update project time ranges (if not updated after
     * addition new measurement data, this data may not be visible. 
     * @return 
     */
    public boolean updateProjectTimeRanges() {

        boolean updated=false;        
        this.projectBeginTime=this.getAllMeasurementsLeftTime();
        this.projectEndTime=this.getAllMeasurementsRightTime();               
        return updated;
    }

    public String getSubtitle() {
        return this.projectSubtitle;
    }

    /**
    * Returns reference to current measurement or null if index out of bounds    
    * @return referrence to NMMMeasurement
    */
    public NMMMeasurement getCurrentMeasurement() {
        return getMeasurement(currentMeasurement);
    }

    /**
     * Use this method to set information if data in the project has been
     * saved on disc or not. This can be used to check if user can leave
     * software safely, or needs to save project first.
     * @param _isSaved 
     */    
    public void setSaved(boolean _isSaved) {
        this.projectSaved=_isSaved;
        //this.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this));
    }

    /**
     * Sets number of current/active measurement ( counting fom 0....)
     * NMMMeasurement becomes active
     * @param _measurement - number of measurement in the project (e.g. 0)
     */
    public void setCurrentMeasurementNumber(int _measurement) {
        if ((_measurement>=0) && (_measurement<this.getMeasurementsNumber())) {
            this.currentMeasurement=_measurement;
        } else {
            NMMToolbox.debugMessage("Błąd!!! Wymóg uaktywnienia nieistniejącego pomiaru:"+_measurement, DEBUG);
        }
        this.setSaved(false);
        //this.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this));
    }

    /**
     * Returns number of current measurement, or -1 if there are no measurements
     * in project.
     * @return int (current measurement number)
     */
    public int getCurrentMeasurementNumber() {
        return this.currentMeasurement;
    }

    /**
     * 
     * @param time
     * @return 
     */
    public int getProjectWideRecordNumber(long time) {
        int rn;
        if ((time>=this.projectBeginTime) && (time<=this.projectEndTime)) {
            long dif = time-this.projectBeginTime;
            rn=(int)(dif/this.projectTimeResolution);
        } else {
            rn=-1;
        }
        return rn;
    }

    public NMMEvent getCurrentSelection() {
        return currentSelection;
    }

    /**
     * Set project title. Title can be placed on various printouts
     * from NMM (like charst), outputs to files, and anywhere else.
     * @param _projectTitle
     */
    public void setProjectTitle(String _projectTitle) {
        projectTitle=_projectTitle;
        this.projectSaved=false;
        this.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this));
    }

    /**
     * Get NMMProject title.
     * @return
     */
    public String getProjectTitle() {
                
        return this.projectTitle;
    }

    /**
     * 
     * @param _projectAuthor
     */
    public void setProjectAuthor(String _projectAuthor) {
        projectAuthor=_projectAuthor;
        this.setSaved(false);
        this.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this));
    }

    /**
     * Returns project author
     * @return
     */
    public String getProjectAuthor() {
        return projectAuthor;
    }

    public void addEvent(NMMEvent _ev) {
        
        //do projektu dodaję zdarzenie        
        //zmian w zdarzeniu będzie nasłuchiwał projekt
        _ev.addEventChangedListener(this);
        //zdarzenie dodaję do proejktu
        this.projectEvents.add(_ev);
        //projekt będzie teraz nie zapisany
        this.setSaved(false);        
        //informuję obserwatorów o zdarzeniu dodania (aktualizacja interfejsu)                        
        this.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this));
    }
    
    public NMMEvent getEvent(int _index) {
        return this.projectEvents.get(_index);
    }

    /**
     * Returns array of events of certain type (NMMEventType), or null
     * @param _et - NMMEventType
     * @return NMMEvent[]
     */
    public NMMEvent[] getEvents(NMMEventType _et) {

        int cntr=0;
        NMMEvent[] nmmes=null;
        
        if (!isNull(_et)) {
            nmmes = new NMMEvent[this.getEventsNumber(_et)];
            for (int i=0; i<this.getEventsNumber();i++) {
                if (this.getEvent(i).getEventType().toString().equals(_et.toString())) {
                    nmmes[cntr]=this.getEvent(i);
                    cntr++;
                }
            }
        }
        return nmmes;
    }

    /**
     * Returns number of events in project.
     * @return integer
     */
    public int getEventsNumber() {
        return this.projectEvents.size();
    }

    /**
     * Returs number of _et type events in project
     * @param _et
     * @return integer
     */    
    public int getEventsNumber(NMMEventType _et) {

        int numberOfAcceptedEvents=0;
        if (!isNull(_et)) {
            for (int i=0; i<this.getEventsNumber();i++) {
                if (this.getEvent(i).getEventType().getType().equals(_et.getType())) {
                    numberOfAcceptedEvents++;
                }
            }
        }        
        return numberOfAcceptedEvents;
    }
    
    /**
     * Returs number of _et type events in project and for specific measurement
     * @param _et - event type
     * @param _MUID - measurement identificator
     * @return integer
     */    
    public int getEventsNumber(NMMEventType _et, UUID _MUID) {

        int numberOfAcceptedEvents=0;
        for (int i=0; i<this.getEventsNumber();i++) {
            NMMEvent tmpEvent;
            tmpEvent=this.getEvent(i);
            if ((tmpEvent.getEventType().getType().equals(_et.getType())) 
                    && (this.getMeasurement(_MUID).isWithinMeasurement(tmpEvent))) {
                numberOfAcceptedEvents++;
            }
        }
        return numberOfAcceptedEvents;
    }    
    
    
    public double getLeqR(NMMEventType _et, UUID _measurementID) {
        
        double min=1000;
        double max=0;
        double l=0;
        double r=0;
        NMMEvent[] eventsList;        
        eventsList=this.getEvents(_et);        
        for (int i=0; i<eventsList.length;i++) {
            l=this.getLeq(_measurementID, eventsList[i]);
            if (l<min) {min=l;}
            if (l>max) {max=l;}
        }        
        r=max-min;
        return r;
    }
    
     /**
     * Returns Leqs array for for _et event type and _measurementID
     * @param _et - event type
     * @param _measurementIS - measurement identificator
     * @return array of double values
     */
    public float[] getLeqArray(NMMEventType _et, UUID _measurementID) {
          
        float[] laeqs;
        
        NMMEvent[] eventsList;
        if (_et!=null) {
            eventsList=this.getEvents(_et);
            laeqs=new float[eventsList.length];
            for (int i=0; i<eventsList.length;i++) {
                laeqs[i]=(float)this.getLeq(_measurementID, eventsList[i]);
            }
        } else {
            int ml=this.getMeasurement(_measurementID).getMeasurementLength();
            laeqs=new float[ml];
            for (int i=0; i<ml;i++) {
                laeqs[i]=(float)this.getMeasurement(_measurementID).getRecord(i).getRecordValue(RecordValueType.LAeq);
            }
        }                        
        return laeqs;
    }
            
    /**
     * Returns Leq logarythmic average value for _et event type
     * and _measurementID
     * @param _et
     * @return 
     */
    public double getLeqLogAverage(NMMEventType _et, UUID _measurementID) {
        
        double average=0;
        NMMEvent[] eventsList;        
        eventsList=this.getEvents(_et);
        if (!isNull(eventsList)) {
            if (eventsList.length>0) {
                float[] laeqs=new float[eventsList.length];
                for (int i=0; i<eventsList.length;i++) {
                    laeqs[i]=(float)this.getLeq(_measurementID, eventsList[i]);
                    average=NMMNoiseCalculator.SredniaLog(laeqs);
                }
            } else {                    
                average=0;
            }
        }        
        return average;
    }
    
    public double getL95(UUID _measurementMUID) {
        
        double l95=0;
        for (int i=0; i<this.getMeasurementsNumber();i++) {
            if (this.getMeasurement(i).getMUID()==_measurementMUID) {
                l95=this.getMeasurement(i).getL95();                
            }
        }                     
        return l95;
    }
    
    
    /**
     * Returns equivalent noise level based on _event period and
     * _measurementMUID mesurement
     * 
     * @param _measurementMUID - measurement identifier
     * @param _event - event specyfyig start and end time
     * @return double
     */
    public double getLeq(UUID _measurementMUID, NMMEvent _event) {
        
        double laeq=0;
        for (int i=0; i<this.getMeasurementsNumber();i++) {
            if (this.getMeasurement(i).getMUID()==_measurementMUID) {
                laeq=this.getMeasurement(i).getLeq(_event.getStart(), 
                        _event.getEnd());
                i=this.getMeasurementsNumber();
            }
        }                
        return laeq;
    }       

    /**
     * Dodaj nowy pomiar do projektu
     * @param _ms - obiekt nowego pomiaru
     */
    public void addMeasurement (NMMMeasurement _ms) {
        
        //TODO: nie wiem czy to jest konieczne
        _ms.addMeasurementChangedListener(this);
        
        projectMeasurements.addMeasurement(_ms);
        this.currentMeasurement=projectMeasurements.getSize()-1;
        this.updateProjectTimeRanges();
        
        //Poinformuj obserwatorów o zmianie w projekcie
        this.dispatchNMMMeasurementChangedEvent(new NMMMeasurementChangedEvent(_ms));

        //Po zaimportowaniu danych do projektu, oznacz status projektu jako nie zapisany
        this.projectSaved=false;
        
        //wygeneruj zdarzenie informujące o zmianie zawartości projektu, tak aby
        //odświeżył sie managerProjektu
        this.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this));
    }

    /**
     *
     * @return number of measurements in the project
     */
    public int getMeasurementsNumber() {
        //System.out.println("NMMProject: W projekcie jest nastepująca liczba pomiarów: "+this.projectMeasurements.size());
        return this.projectMeasurements.getSize();
    }

    /**
     * Returns project begin time as long. This is read only property.
     * @return long
     */
    public long getProjectBeginTime() {
        return this.projectBeginTime;
    }

     /**
     * Returns project end time as long. This is read only property.
     * @return long
     */
    public long getProjectEndTime() {
        return this.projectEndTime;
    }

    /**
     * Returns project length in miliseconds
     * @return
     */
    public long getProjecTimeSpan() {
        return this.projectEndTime-this.projectBeginTime;
    }

    /**
     * Returns number of records from begining to end including empty records
     * @return
     */
    public int getProjectRecordsSpan() {
        int answer=(int)(this.projectEndTime-this.projectBeginTime)/1000;
        return answer;
    }

    /**
     * Time resolution is length of one record in miliseconds.
     * @return long
     */
    public long getProjectTimeResolution() {
        return this.projectTimeResolution;
    }

    /**
     * Set project time resolution.
     * @param _tr   time resolution in miliseconds
     */
    public void setProjectTimeResolution(long _tr) {
        this.projectTimeResolution=_tr;
        this.setSaved(false);
    }

    /**
     *
     */
    public boolean isDravable() {
        return (this.getMeasurementsNumber()>0);
    }

    /**
     * Use this method to get single record aquired at "time" from
     * certain measurement. Set measurement to "-1" to get value from
     * current (active) measurement.
     * @param measurement - (e.g. 0 (first measurement), 1, 2, etc.)
     * @param time - time as long (in miliseconds)
     * @return double, value recorded at certain time during certain measurement
     */
    public double getMeasurementRecord(int measurement, long time) {
        double value=-1;
            if (measurement==-1) {
                value = this.projectMeasurements.getMeasurement(this.currentMeasurement).getRecordValue(time, RecordValueType.LAeq);
            } else {
                value = this.projectMeasurements.getMeasurement(measurement).getRecordValue(time, RecordValueType.LAeq);
            }
        return value;
    }

    /**
     * Returns reference to measurement or null if MUID is unknown
     * @param measurement MUID 
     * @return
     */
    public NMMMeasurement getMeasurement(UUID _muid) {
        NMMMeasurement ms=null;
        for (int i=0; i<this.getMeasurementsNumber();i++) {
            if (this.projectMeasurements.getMeasurement(i).getMUID()==_muid) {
                ms=this.projectMeasurements.getMeasurement(i);
                return ms;
            }
        }                        
        return ms;
    }    
    
    /**
     * Returns reference to measurement or null if index out of bounds
     * @param measurement_number (e.g. 0 (first one), 1, 2, 3, etc.)
     * @return
     */
    public NMMMeasurement getMeasurement(int measurement_number) {
        NMMMeasurement ms=null;
        try {
            ms = this.projectMeasurements.getMeasurement(measurement_number);
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.toString());
        }
        return ms;
    }
    
     /**
     * Find out if open or currently creaded project has been saved or not
     * @return boolean, true - projecr has been saved recently, false - it
      * hasn't been saved since it had changed.
     */
    public boolean isSaved() {
        return this.projectSaved;
    }

    public void deleteEventType(NMMEventType ettr) {
        if (this.eventTypes.remove(ettr)) {
            this.deleteEvents(ettr);
            NMMToolbox.debugMessage("Type has been removed !", DEBUG);            
        } else {
            NMMToolbox.debugMessage("Type unrecognized!", DEBUG);           
        }
        this.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this));
    }

    public NMMNoiseSourceModel getNoiseSourceModel(String _modelName, NoiseSourceModelType _nsmt) {
        
        NMMNoiseSourceModel tm=null;
        
        for (int i=0; i<this.getNumberOfNoiseSourceModels();i++) {
            NMMNoiseSourceModel nsm=this.getNoiseSourceModel(i);
            if ((nsm.getNoiseModelType()==_nsmt) &&
                    (nsm.getModelName().equals(_modelName))) {
               tm=nsm; 
            }
        }        
        return tm;        
    }

    /**
     *
     * @param _mEvent
     */
    @Override
    public void dispatchNMMMeasurementChangedEvent(NMMMeasurementChangedEvent _mEvent) {
        
        //Zmiany w obiektach NMMMeasurement moga mieć wpływ na obiekt NMMProjekt.
        
        //Na przykład przesunięcie pomiaru w czasie może wpłynąć na początek lub
        //koniecz czasu projektu. Tutaj to sprawdzamy i generujemy zdarzenie zmiany
        //projektu jeżeli w istocie ono wystapi.
        
        NMMMeasurement _ms=(NMMMeasurement)_mEvent.getSource();
        if (this.getProjectBeginTime()>_ms.getMeasurementBeginTime()) {
            this.projectBeginTime=_ms.getMeasurementBeginTime();
        }
        if (this.getProjectEndTime()<_ms.getMeasurementEndTime()) {
            this.projectEndTime=_ms.getMeasurementEndTime();
        }
        this.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this));
        this.updateProjectTimeRanges();   
    }

    @Override
    public void dispatchNMMEventChangedEvent(NMMEventChangedEvent _mEvent) {
        //nastąpiła zmiana w definicji zdarzeń pomiarowych. Trzeba 
        //zaktualizować interfejs        
        this.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this));
    }

    public void removeProjectModelsListener(NMMAbstractNoiseSourceModelInterface _rsmi) {
        NMMProjectModelsListeners.remove(_rsmi);        
    }

    String getJVMV() {
        return this.jreVersion;
    }
    
    public NMMNoiseSourceModels getNoiseSourceModelsContainer() {
        return this.nmmNoiseSourceModels;
    }
    
    public boolean contains(NMMNoiseSourceModel _nsm) {
        return this.nmmNoiseSourceModels.contains(_nsm);
    }

    /**
     * 
     * @param _tp
     * @param muid
     * @return 
     */
    public float[] getLeqArray(TimePeriods _tp, UUID muid) {
 
        float[] laeqs;
        List<Float> laeqsv=new ArrayList<Float>();
                                                                                                                     
                                       
        for (int i=0; i<this.getMeasurement(muid).getMeasurementLength(); i++) {
            long t=this.getMeasurement(muid).getRecordTime(i);
            //if t is within DAY, NIGHT, etc. then take it into account
            if (TimeConverter.isTimeInPeriod(t, _tp)) {
                double rv=this.getMeasurement(muid).getRecordValue(i, RecordValueType.LAeq);
                laeqsv.add((float)rv);                
            }
        }
        laeqs=new float[laeqsv.size()];
        for (int i=0; i<laeqsv.size();i++) {
            laeqs[i]=(float)laeqsv.get(i);
        }        
        return laeqs;
    }
    
//    public TableModel getMeasurementsTableModel() {
//        return this.projectMeasurements;
//    }

    Iterable<NMMMeasurement> getMeasurements() {
        return this.projectMeasurements.getMeasurements();
    }

    void deleteMeasurement(NMMMeasurement nmmMeas) {
        
        this.projectMeasurements.removeMeasurement(nmmMeas);        
        
        //if current measurement is being removed, then new current measurement
        //must be set (if there are still any)
        if (this.getMeasurementsNumber()>0) {
            this.currentMeasurement=0;
        } else {
            this.currentMeasurement=-1;
        }                
    }

    @Override
    public Object getRoot() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getChild(Object parent, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getChildCount(Object parent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isLeaf(Object node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void setCurrentMeasurement(NMMMeasurement nmmMeasurement) {        
        
        System.out.println("W projekcie jest "+this.getMeasurementsNumber()+" pomiarów.");
        for (int i=0; i<this.getMeasurementsNumber();i++) {
            if (nmmMeasurement==this.getMeasurement(i)) {
                this.currentMeasurement=i;
                System.out.println("Wybrano pomiar "+this.getCurrentMeasurementNumber());
            }
        }
        this.setSaved(false);
        //this.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this));
    }

    public boolean containsMeasurement(NMMMeasurement _ms) {
        
        boolean contains=false;
        
        for (int i=0; i<this.getMeasurementsNumber();i++) {
            if (this.projectMeasurements.getMeasurement(i)==_ms) {
                contains=true;
                break;
            }
        }        
        return contains;
    }

    public ArrayList getAllMeasurements() {
        return this.projectMeasurements.getMeasurements();
    }    

    void setLastUsedEventType(NMMEventType _eType) {
        this.lastUsedEventType=_eType;
    }

    NMMEventType getLastUsedEventType() {
        return this.lastUsedEventType;
    }

    public boolean containsEvent(NMMEvent _ms) {
        
        boolean contains=false;
       
        contains = this.projectEvents.contains(_ms);
              
        return contains;
    }

    private void deleteEvents(NMMEventType ettr) {
        System.out.println("Liczba zdarzeń przed wykasowaniem: "+this.getEventsNumber());
        boolean rpe=true;
        while (rpe) {
            rpe=false;
           for (int cntr=0; cntr<this.getEventsNumber();cntr++) {
            System.out.println("Indeks:"+cntr);
            if (this.projectEvents.get(cntr).getEventType().equals(ettr)) {
                this.projectEvents.remove(cntr);
                rpe=true;
            }
        } 
        }
        
        System.out.println("Liczba zdarzeń po wykasowaniu: "+this.getEventsNumber());
        this.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this));
    }
}