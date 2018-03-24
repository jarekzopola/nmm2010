/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.projectmanager;

import biz.ekoplan.nmm2010.measurement.NMMMeasurement;
import biz.ekoplan.nmm2010.nmmproject.NMMProjectChangedEvent;
import biz.ekoplan.nmm2010.nmmproject.NMMProjectChangedListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import nmm2010.NMMEvent;
import nmm2010.NMMProject;

/**
 *
 * @author jarek
 */
public class ProjectManager extends JTree implements NMMProjectChangedListener {

    NMMProject _nmmProject;
    DefaultMutableTreeNode top;
    DefaultMutableTreeNode measurements;
    DefaultMutableTreeNode events;
    DefaultMutableTreeNode mDevices;
    DefaultMutableTreeNode calcModels;
    DefaultMutableTreeNode presentations;
    DefaultTreeModel model2;
        
    public ProjectManager() {
        
    }
        
    public ProjectManager(DefaultMutableTreeNode _top, DefaultMutableTreeNode _measurements, DefaultMutableTreeNode _events,
            DefaultMutableTreeNode _mDevices, DefaultMutableTreeNode _calcModels,
            DefaultMutableTreeNode _presentations, DefaultTreeModel _model) {    
        
        measurements = _measurements;
        events = _events;
        mDevices = _mDevices;
        calcModels = _calcModels;
        presentations = _presentations;        
        top = _top;        
        model2=(DefaultTreeModel) _model;        
    }
    
    
    public boolean addUniqueNode(DefaultMutableTreeNode childNode, DefaultMutableTreeNode parentNode)
    {    
        // Check each node
        boolean isUnique = true;
        int liczbaListkow;
    
        liczbaListkow=parentNode.getChildCount();

        for (int i = 0; i < (liczbaListkow); i++)
        {
            Object compUserObj = ((DefaultMutableTreeNode) parentNode.getChildAt(i)).getUserObject();
            if (compUserObj.equals(childNode.getUserObject()))
            {
                isUnique = false;
                break;
            }
        }
        // If Unique, insert
        if (isUnique) {
            //System.out.println("Dodaje ...............................");
            model2.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
            
        }    
        return isUnique;
    }
    
    private void printEnumeration(Enumeration e, String label) {    
    while (e.hasMoreElements()) {
      System.out.println(e.nextElement());
    }
    
  }
        
    public void cleanNonExistingNodes() {    
               
        Enumeration<DefaultMutableTreeNode> enum1;                
        enum1 = measurements.children();
        while (enum1.hasMoreElements()) {            
            DefaultMutableTreeNode compUserObj = (DefaultMutableTreeNode)enum1.nextElement();
            //System.out.println("W węźle jest: "+compUserObj.getUserObject().toString());
            NMMMeasurement _ms = (NMMMeasurement) compUserObj.getUserObject();
            if (!this._nmmProject.containsMeasurement(_ms))
            {
                //jezeli pomiaru nie ma w obiekcie projektu to go usuwamy z modelu
                //danych drzewka managera projektu
                DefaultTreeModel dtm = (DefaultTreeModel) this.getModel();
                dtm.removeNodeFromParent(compUserObj);                  
            }
        }
        
        //usuwamy węzły zdarzeń akustycznych z drzewka projektu        
        ArrayList<DefaultMutableTreeNode> zdarzenia;
        ArrayList<DefaultMutableTreeNode> typyZdarzen;
        DefaultMutableTreeNode cet;
        DefaultMutableTreeNode dmtn;
        DefaultTreeModel dtm =(DefaultTreeModel) this.getModel();
        if (!events.children().hasMoreElements()) {
            return;
        }
        typyZdarzen = Collections.list(events.children());
        Iterator itrTypyZdarzen = typyZdarzen.iterator();
        while (itrTypyZdarzen.hasNext()) {
            dmtn = (DefaultMutableTreeNode)itrTypyZdarzen.next();
            zdarzenia = Collections.list(dmtn.children());
            if (!zdarzenia.isEmpty()) {
            Iterator itrZdarzenia = zdarzenia.iterator();
            while (itrZdarzenia.hasNext()) {
                DefaultMutableTreeNode compUserObj = (DefaultMutableTreeNode)itrZdarzenia.next();                
                NMMEvent _ms = (NMMEvent) compUserObj.getUserObject();
                System.out.println(this._nmmProject.containsEvent(_ms));
                if (!this._nmmProject.containsEvent(_ms))                
                {
                    //jezeli pomiaru nie ma w obiekcie projektu to go usuwamy z modelu
                    //danych drzewka managera projektu                    
                    dtm.removeNodeFromParent(compUserObj);                  
                    if (!itrZdarzenia.hasNext()) {
                        dtm.removeNodeFromParent(dmtn);
                    }
                }
            }    
            } else {
                dtm.removeNodeFromParent(dmtn);
            }                         
        }
    }
        
    @Override
    public void dispatchNMMProjectChangedEvent(NMMProjectChangedEvent _mEvent) {
        
        _nmmProject=(NMMProject)_mEvent.getSource();                
        
        //Usuwamy pomiary i zdarzenia nieistniejące w projekcie
        this.cleanNonExistingNodes();
        
        //Dodajemy pomiary        
        int pWProj=_nmmProject.getMeasurementsNumber();
        for (int i=0; i<pWProj; i++) {
            this.addUniqueNode(new DefaultMutableTreeNode(_nmmProject.getMeasurement(i)), measurements);
        }                    
                
        //Dodajemy typy zdarzeń
        int tzWProj=_nmmProject.getEventTypeTypes().length;
        for (int i=0; i<tzWProj; i++) {
            //bierzemy z projektu jeden typ zdarzenia
            DefaultMutableTreeNode etn = new DefaultMutableTreeNode(_nmmProject.getEventTypeTypes()[i]);                                                            
            //Dodajemy zdarzenia danego typu
            int zWProj=_nmmProject.getEventsNumber();
            for (int i2=0; i2<zWProj; i2++) {
                if (_nmmProject.getEvent(i2).getEventType().compareToBoolean(_nmmProject.getEventTypeTypes()[i]))
                this.addUniqueNode(new DefaultMutableTreeNode(_nmmProject.getEvent(i2)), etn);
                //etn.add(new DefaultMutableTreeNode(_nmmProject.getEvent(i2)));
            }
            this.addUniqueNode(etn, events);
        }             
        this.top.setUserObject(this._nmmProject.getProjectTitle());
        
    }            
}
