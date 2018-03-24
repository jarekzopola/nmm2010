/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.events;

import biz.ekoplan.nmm2010.measurement.*;

/**
 * Interfejs implementowany przez obiekty obsługujące zdarzenia polegające na
 * zmianie właściwości pojednczych zdarzeń akustycznych.
 * @author Jarek
 */
public interface NMMEventChangedListener {

    public void dispatchNMMEventChangedEvent(NMMEventChangedEvent _mEvent);

}
