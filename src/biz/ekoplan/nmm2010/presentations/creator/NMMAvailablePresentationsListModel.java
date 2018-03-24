/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.presentations.creator;

import biz.ekoplan.nmm2010.enums.PresentationType;
import java.util.ArrayList;
import javax.swing.AbstractListModel;

/**
 *
 * @author jarek
 */
public class NMMAvailablePresentationsListModel extends AbstractListModel {

    ArrayList<PresentationType> pt = new ArrayList<PresentationType>();
    
    public NMMAvailablePresentationsListModel(ArrayList<PresentationType> _l) {
        pt=_l;
    }
    
    
    @Override
    public int getSize() {
        return pt.size();
    }

    @Override
    public Object getElementAt(int i) {
        return pt.get(i);
    }
    
}
