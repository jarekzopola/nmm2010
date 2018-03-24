/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.presentations;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Jarek
 */
public class NMMPresentations implements ListModel, Serializable {

    private static final long serialVersionUID=1L;
    
    ArrayList<NMMPresentation> projectPresentations = new ArrayList<NMMPresentation>();

    public int getSize() {
        return this.projectPresentations.size();
    }

    public NMMPresentation getElementAt(int index) {
        return this.projectPresentations.get(index);
    }

    public void addElement(NMMPresentation _element) {
        this.projectPresentations.add(_element);
    }

    public boolean removeElement(int _index) {
        boolean succeeded=true;
        Object removed = this.projectPresentations.remove(_index);
        if (removed==null) {
            succeeded=false;
        }
        return succeeded;
    }

    public void addListDataListener(ListDataListener l) {
        
    }

    public void removeListDataListener(ListDataListener l) {
        
    }
}
