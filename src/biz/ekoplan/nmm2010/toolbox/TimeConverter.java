/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.toolbox;

import biz.ekoplan.nmm2010.enums.TimePeriods;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 *
 * @author Jaroslaw Kowalczyk
 */
public class TimeConverter {

//    //Konwersja godziny (dd:mm:rrrr hh:mm:ss) do wartości long
//    public static long StringToLong(String _date, String _time) {        
//        long longTime;
//        GregorianCalendar cal = new GregorianCalendar();
//        cal.setTimeInMillis(0);
//        String rok = _date.substring(0,4);
//        String miesiac = _date.substring(5,7);
//        String dzien = _date.substring(8,10);
//        String godz = _time.substring(0, 2);
//        String min = _time.substring(3, 5);
//        String sek = _time.substring(6, 8);
//        //System.out.println("Rok:"+rok+" miesiąc:"+miesiac+" dzien:"+dzien);
//        //System.out.println("Godzina:"+godz+" minuta:"+min+" sekunda:"+sek);
//        cal.set(Integer.parseInt(rok),Integer.parseInt(miesiac)-1,Integer.parseInt(dzien),Integer.parseInt(godz),Integer.parseInt(min),(int)Integer.parseInt(sek));
//        longTime=cal.getTimeInMillis();        
//        return longTime;
//    }
    
    //Konwersja godziny (dd:mm:rrrr hh:mm:ss) do wartości long
//    public static long StringToLong(String _date, String _time, Locale _locale) {        
//        
//        Date myDate;
//        
//        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, _locale);
//        
//        try {
//            myDate = df.parse(_date+" "+_time);
//        } catch (ParseException pe) {
//            return 0;
//        }        
//        
//        System.out.println("Data jako long = "+myDate.getTime());
//        
//        long longTime=0;
//        GregorianCalendar cal = new GregorianCalendar();
//        cal.setTimeInMillis(0);
//        String rok = _date.substring(0,4);
//        String miesiac = _date.substring(5,7);
//        String dzien = _date.substring(8,10);
//        String godz = _time.substring(0, 2);
//        String min = _time.substring(3, 5);
//        String sek = _time.substring(6, 8);
//        cal.set(Integer.parseInt(rok),Integer.parseInt(miesiac)-1,Integer.parseInt(dzien),Integer.parseInt(godz),Integer.parseInt(min),(int)Integer.parseInt(sek));
//        longTime=cal.getTimeInMillis();        
//        
//        return longTime;
//    }
    
    public static long StringToLong(String _date, String _time, Locale _locale) {        
        
        Date myDate;
        Long longTime;
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, _locale);
        
        try {
            myDate = df.parse(_date+" "+_time);
        } catch (ParseException pe) {
            return 0;
        }                        
        longTime=+myDate.getTime();        
        return longTime;
    }
    
    public static long StringToLong(String _date, String _time, Locale _locale, int _shift) {        
        
        Date myDate;
        Long longTime;
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, _locale);
        
        try {
            myDate = df.parse(_date+" "+_time);
        } catch (ParseException pe) {
            return 0;
        }                        
        longTime=+myDate.getTime()+_shift;        
        return longTime;
    }

    /**
     * Convert long to string time representation
     * @param _time
     * @param _dateFormat DateFormat.MEDIUM etc.
     * @return
     */
    public static String LongToTimeString(long _time, int _dateFormat, Locale _locale) {

        String localStringDateTime="";
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(_time);
        //DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, _locale);
        DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM, _locale);
        localStringDateTime=df.format(cal.getTime());
        return localStringDateTime;
    }
    
    /**
     * 
     * @param _time
     * @param _dateFormat
     * @return 
     */
    public static String LongToDateString(long _time, int _dateFormat, Locale _locale) {

        String localStringTime;
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(_time);
        DateFormat df = DateFormat.getDateInstance(_dateFormat, _locale);
        localStringTime=df.format(cal.getTime());
        return localStringTime;
    }

    /**
     * Convrts strings from "hh:mm:ss" to seconds in the following manner
     * long = hh*3600 + mm*60 + ss
     * @param _time - string representing length of time "hh:mm:ss"
     * @return - int, number of seconds, or -1 if string is in bad format
     */
    public static int StringToSeconds(String _time) {
        int s=0;
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(0);
        try {
            String godz = _time.substring(0, 2);
            String min = _time.substring(3, 5);
            String sek = _time.substring(6, 8);
            System.out.println("Godzin:"+godz+" minut:"+min+" sekund:"+sek);
            int godz2=Integer.valueOf(godz);
            int min2=Integer.valueOf(min);
            int sek2=Integer.valueOf(sek);
            s=godz2*3600+min2*60+sek2;
        } catch (Exception e) {
            s=-1;
        }
        return s;
    }

    public static String SeondsToString(long _seconds) {

        long hours=_seconds/3600;
        long minutes = (_seconds-(hours*3600))/60;
        long sec=_seconds-(hours*3600)-(minutes*60);
        String sHours=String.valueOf(hours);
        if (sHours.length()<2) {
            sHours="0".concat(sHours);
        }
        String sMinutes=String.valueOf(minutes);
        if (sMinutes.length()<2) {
            sMinutes="0".concat(sMinutes);
        }
        String sSeconds=String.valueOf(sec);
        if (sSeconds.length()<2) {
            sSeconds="0".concat(sSeconds);
        }
        String s = sHours+":"+sMinutes+":"+sSeconds;
        return s;
    }

    public static boolean isTimeInPeriod(long _time, TimePeriods _tPeriod) {

        boolean isOrNot=false;

        Calendar c = Calendar.getInstance();
            c.setTimeInMillis(_time);
            switch (_tPeriod) {
                case SDAY:
                    if ((c.get(Calendar.HOUR_OF_DAY)>5) && (c.get(Calendar.HOUR_OF_DAY)<18)) {
                        isOrNot=true;
                    }
                    break;
                case NIGHT:
                    if ((c.get(Calendar.HOUR_OF_DAY)<6) ||
                            ((c.get(Calendar.HOUR_OF_DAY)<24)) && (c.get(Calendar.HOUR_OF_DAY)>21)) {
                        isOrNot=true;
                    }
                    break;
                case DAY:
                    if ((c.get(Calendar.HOUR_OF_DAY)>5) && (c.get(Calendar.HOUR_OF_DAY)<22)) {
                        isOrNot=true;
                    }
                    break;
                case EVENING:
                    if ((c.get(Calendar.HOUR_OF_DAY)>17) && (c.get(Calendar.HOUR_OF_DAY)<22)) {
                        isOrNot=true;
                    }
                    break;
                case ALL:
                        isOrNot=true;
                    break;
            }
        return isOrNot;

    }
}
