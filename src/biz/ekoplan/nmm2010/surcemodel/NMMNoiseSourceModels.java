/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.surcemodel;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Jarek
 */
public class NMMNoiseSourceModels implements ListModel, Serializable {

    private static final long serialVersionUID=1L;
    
    ArrayList<NMMNoiseSourceModel> projectNoiseSourceModels = new ArrayList<NMMNoiseSourceModel>();

    @Override
    public int getSize() {
        return this.projectNoiseSourceModels.size();
    }

    @Override
    public NMMNoiseSourceModel getElementAt(int index) {
        return this.projectNoiseSourceModels.get(index);
    }

    public void addElement(NMMNoiseSourceModel _element) {
        this.projectNoiseSourceModels.add(_element);            
    }    
    
    public boolean contains(NMMNoiseSourceModel _nsm) {
        return this.projectNoiseSourceModels.contains(_nsm);
    }

    public boolean removeModel(NMMNoiseSourceModel _nsm) {        
        boolean success=false;        
        success=this.projectNoiseSourceModels.remove(_nsm);
        return success;
    }
    
    public boolean removeElement(int _index) {
        boolean succeeded=true;
        Object removed = this.projectNoiseSourceModels.remove(_index);
        if (removed==null) {
            succeeded=false;
        }
        return succeeded;
    }

    @Override
    public void addListDataListener(ListDataListener ll) {

    }

    @Override
    public void removeListDataListener(ListDataListener ll) {

    }
}
