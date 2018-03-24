/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.presentations;

import biz.ekoplan.nmm2010.enums.NoiseSourceModelType;
import biz.ekoplan.nmm2010.enums.PresentationType;
import biz.ekoplan.nmm2010.surcemodel.NMMNoiseSourceModel;
import java.awt.print.PageFormat;

/**
 *
 * @author Jarek
 */
public interface NMMPresentation {
    
    public void setPresentationTitle(String _presentationTitle);
    public String getPresentationTitle();
    public void setPageFormat(int _pageFormat);
    public PageFormat getPageFormat();
    public NMMNoiseSourceModel getNoiseSourceModel();
    public PresentationType getPresentationType();
    
    /**
     * Presentation is editable if user can edit presentation variables stored in presentation object directly
     * @return
     */
    public boolean isEditable();

    /**
     * Presentation may contain noise source model (eg. industrial mathematical model according to Polish
     * measurement reference method. 
     * @return
     */
    public boolean containsNoiseSourceModel();

    public NoiseSourceModelType getNoiseSourceModelType();
    public void setNoiseSourceModel(NMMNoiseSourceModel _sm);

    @Override
    public String toString();
    //public long getStartTime();
    //public long getEndTme();
    //public void setStartTime(long _stTime);
    //public void setEndTime(long _enTime);

}
