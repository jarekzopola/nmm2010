/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.devices;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author samsung
 */
public class NMMNoiseMeasurementSets implements ComboBoxModel, Serializable {

    private static final long serialVersionUID=1L;    
    
    ArrayList<NMMNoiseMeasurementSet> measurementSets =
            new ArrayList<NMMNoiseMeasurementSet>();
    NMMNoiseMeasurementSet selectedObject;

    public boolean removeMeasuringSet(String _index) {
        boolean remove = measurementSets.remove(_index);
        return remove;
    }

    @Override
    public int getSize() {
        return measurementSets.size();
    }

    @Override
    public Object getElementAt(int index) {
        return measurementSets.get(index);
    }

    public void addElement(NMMNoiseMeasurementSet nms) {
        this.measurementSets.add(nms);
    }

    @Override
    public void setSelectedItem(Object anItem) {
        this.selectedObject=(NMMNoiseMeasurementSet)anItem;
    }    

    /**
     *
     * @return
     */
    @Override
    public Object getSelectedItem() {
        return this.selectedObject;
    }

    public NMMNoiseMeasurementSet getMeasuringSet(String setID) {
        NMMNoiseMeasurementSet nmsR=null;
        for (NMMNoiseMeasurementSet nms : this.measurementSets) {
            if (nms.getLaboratoryID().equals(setID)) {
                nmsR=nms;
            }
        }        
        return nmsR;
    }

    @Override
    public void addListDataListener(ListDataListener ll) {
        //
    }

    @Override
    public void removeListDataListener(ListDataListener ll) {
        //
    }


}
