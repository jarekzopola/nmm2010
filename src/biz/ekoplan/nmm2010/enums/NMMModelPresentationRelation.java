/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.enums;

/**
 *
 * @author jarek
 */
public class NMMModelPresentationRelation {

    private NoiseSourceModelType nsmt;
    private PresentationType pt;
    
    public NMMModelPresentationRelation(NoiseSourceModelType _nsmt, PresentationType _pt) {

            nsmt=_nsmt;
            pt=_pt;    
    }
    public NoiseSourceModelType getNoiseSourceModelType() {
        return this.nsmt;
    }

    public PresentationType getPresentationType() {
        return this.pt;
    }


}
