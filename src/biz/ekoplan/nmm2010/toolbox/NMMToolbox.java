/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.toolbox;

import java.text.DecimalFormat;

/**
 *
 * @author Jarek
 */
public class NMMToolbox {

    public static String formatDouble(double _valueToFormat) {

        String formattedValue;

        DecimalFormat df = new DecimalFormat("##0.0");
        formattedValue=df.format(_valueToFormat);
        return formattedValue;
    }

    public static String formatDouble(double _valueToFormat, String _format) {

        String formattedValue;

        DecimalFormat df = new DecimalFormat(_format);
        formattedValue=df.format(_valueToFormat);
        return formattedValue;
    }


    public static void debugMessage(String _message, boolean _onoff) {
        if (_onoff) {
            System.out.println(_message);
        }
    }

}
