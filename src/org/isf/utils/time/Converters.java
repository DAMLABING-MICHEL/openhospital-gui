package org.isf.utils.time;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by nicosalvato on 2016-08-25.
 * Contact: nicosalvato@gmail.com
 */
public class Converters {
    /**
     * Returns a {@link String} representing the date in format <code>yyyy-MM-dd HH:mm:ss</code>.
     * @param datetime {@link GregorianCalendar} object.
     * @return the date in format <code>yyyy-MM-dd HH:mm:ss</code>.
     */
    public static String convertToSQLDate(GregorianCalendar datetime) {
        if (datetime == null)
            return null;
        return convertToSQLDate(datetime.getTime());
    }

    /**
     * Returns a {@link String} representing the date in format <code>yyyy-MM-dd HH:mm:ss</code>.
     * @param datetime {@link Date} input.
     * @return the date in format <code>yyyy-MM-dd HH:mm:ss</code>.
     */
    public static String convertToSQLDate(Date datetime) {
        if (datetime == null)
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(datetime);
    }

    /**
     * Returns a {@link String} representing the date in format <code>yyyy-MM-dd</code>.
     * @param date {@link Date} object.
     * @return the date in format <code>yyyy-MM-dd</code>.
     */
    public static String convertToSQLDateLimited(GregorianCalendar date) {
        if (date == null)
            return null;
        return convertToSQLDateLimited(date.getTime());
    }

    /**
     * Returns a {@link String} representing the date in format <code>yyyy-MM-dd</code>.
     * @param date {@link Date} object.
     * @return the date in format <code>yyyy-MM-dd</code>.
     */
    public static String convertToSQLDateLimited(Date date) {
        if (date == null)
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * Converts a {@link GregorianCalendar} to a {@link Date}.
     * @param calendar the calendar to convert.
     * @return the converted value or <code>null</code> if the passed value is <code>null</code>.
     */
    public static Date toDate(GregorianCalendar calendar) {
        if (calendar == null)
            return null;
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * Converts the specified {@link java.sql.Date} to a {@link GregorianCalendar}.
     * @param date the date to convert.
     * @return the converted date.
     */
    public static GregorianCalendar toCalendar(Date date){
        if (date == null)
            return null;
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }
    
    /**
     * Converts the specified {@link java.sql.Timestamp} to a {@link GregorianCalendar}.
     * @param date the date to convert.
     * @return the converted date.
     */
    public static GregorianCalendar toCalendar(Timestamp datetime){
        if (datetime == null)
            return null;
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(datetime.getTime());
        return calendar;
    }
    
    /**
	 * Converts the passed {@link String} value to a {@link Float} value.
	 * @param value the value to convert
	 * @return the converted float or <code>null</code> if the passed value is <code>null</code> or is not parsable.
	 */
	public static Float toFloat(String value)
	{
		if (value == null) return null;
		try {
			return Float.valueOf(value);
		}catch(NumberFormatException nfe)
		{
			//the value is not parsable
			return null;
		}
	}
}
