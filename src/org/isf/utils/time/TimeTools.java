package org.isf.utils.time;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

/**
 * 
 * @author Mwithi
 * 
 * Some useful functions for time calculation.
 *
 */
public class TimeTools {
	
	
	public static void main(String[] args) {
		GeneralData.getGeneralData();
		MessageBundle.initialize();
		GregorianCalendar dateFrom = new GregorianCalendar(2014, 10, 1);
		GregorianCalendar dateTo = new GregorianCalendar();
		System.out.println("Formatted Age: " + getFormattedAge(dateFrom.getTime()));
		System.out.println("Days between: " + getDaysBetweenDates(dateFrom, dateTo, true));
		System.out.println("Weeks between: " + getWeeksBetweenDates(dateFrom, dateTo, true));
		System.out.println("Months between: " + getMonthsBetweenDates(dateFrom, dateTo, true));
	}
	
	/**
	 * @author Mwithi
	 * 
	 * returns <code>true</code> if the DATE part is the same (no matter the time)
	 * @param aDate
	 * @param today
	 * @return
	 */
	public static boolean isSameDay(GregorianCalendar aDate, GregorianCalendar today) {
		return (aDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)) &&
			   (aDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) &&
			   (aDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * @author Mwithi
	 * 
	 * returns the difference in days between two dates
	 * @param from
	 * @param to
	 * @param ignoreTime - if <code>True</code> only dates will be compared
	 * @return the number of days, negative if from is after to
	 */
	public static int getDaysBetweenDates(GregorianCalendar from, GregorianCalendar to, boolean ignoreTime) {
		
		if (ignoreTime) {
			from.set(GregorianCalendar.HOUR_OF_DAY, 0);
			from.set(GregorianCalendar.MINUTE, 0);
			from.set(GregorianCalendar.SECOND, 0);
			to.set(GregorianCalendar.HOUR_OF_DAY, 0);
			to.set(GregorianCalendar.MINUTE, 0);
			to.set(GregorianCalendar.SECOND, 0);
		}
		
		DateTime dateFrom = new DateTime(from);
		DateTime dateTo = new DateTime(to);
		
		Period period;
		if (dateTo.isAfter(dateFrom)) {
			period = new Period(dateFrom, dateTo, PeriodType.days());
			return period.getDays();
		} else {
			period = new Period(dateTo, dateFrom, PeriodType.days());
			return -period.getDays();
		}
			
	}
	
	/**
	 * @author Mwithi
	 * 
	 * returns the difference in days between two dates
	 * @param from
	 * @param to
	 * @param ignoreTime - if <code>True</code> only dates will be compared
	 * @return the number of days, negative if from is after to
	 */
	public static int getDaysBetweenDates(Date from, Date to, boolean ignoreTime) {
		
		if (ignoreTime) {
			GregorianCalendar dateFrom = new GregorianCalendar(); 
			GregorianCalendar dateTo = new GregorianCalendar();
			dateFrom.setTime(from);
			dateFrom.set(GregorianCalendar.HOUR_OF_DAY, 0);
			dateFrom.set(GregorianCalendar.MINUTE, 0);
			dateFrom.set(GregorianCalendar.SECOND, 0);
			
			dateTo.setTime(to);
			dateTo.set(GregorianCalendar.HOUR_OF_DAY, 0);
			dateTo.set(GregorianCalendar.MINUTE, 0);
			dateTo.set(GregorianCalendar.SECOND, 0);
			
			from = dateFrom.getTime();
			to = dateFrom.getTime();
		}
		
		DateTime dateFrom = new DateTime(from);
		DateTime dateTo = new DateTime(to);
		Period period;
		if (dateTo.isAfter(dateFrom)) {
			period = new Period(dateFrom, dateTo, PeriodType.days());
			return period.getDays();
		} else {
			period = new Period(dateTo, dateFrom, PeriodType.days());
			return -period.getDays();
		}
	}
	
	/**
	 * @author Mwithi
	 * 
	 * returns the difference in weeks between two dates
	 * @param from
	 * @param to
	 * @param ignoreTime - if <code>True</code> only dates will be compared
	 * @return the number of days, negative if from is after to
	 */
	public static int getWeeksBetweenDates(GregorianCalendar from, GregorianCalendar to, boolean ignoreTime) {

		if (ignoreTime) {
			from.set(GregorianCalendar.HOUR_OF_DAY, 0);
			from.set(GregorianCalendar.MINUTE, 0);
			from.set(GregorianCalendar.SECOND, 0);
			to.set(GregorianCalendar.HOUR_OF_DAY, 0);
			to.set(GregorianCalendar.MINUTE, 0);
			to.set(GregorianCalendar.SECOND, 0);
		}
		
		DateTime dateFrom = new DateTime(from);
		DateTime dateTo = new DateTime(to);
		Period period = new Period(dateFrom, dateTo, PeriodType.weeks());
		return period.getWeeks();
	}
	
	/**
	 * @author Mwithi
	 * 
	 * returns the difference in months between two dates
	 * @param from
	 * @param to
	 * @param ignoreTime - if <code>True</code> only dates will be compared
	 * @return the number of days, negative if from is after to
	 */
	public static int getMonthsBetweenDates(GregorianCalendar from, GregorianCalendar to, boolean ignoreTime) {
		
		if (ignoreTime) {
			from.set(GregorianCalendar.HOUR_OF_DAY, 0);
			from.set(GregorianCalendar.MINUTE, 0);
			from.set(GregorianCalendar.SECOND, 0);
			to.set(GregorianCalendar.HOUR_OF_DAY, 0);
			to.set(GregorianCalendar.MINUTE, 0);
			to.set(GregorianCalendar.SECOND, 0);
		}
		
		DateTime dateFrom = new DateTime(from);
		DateTime dateTo = new DateTime(to);
		Period period = new Period(dateFrom, dateTo, PeriodType.months());
		return period.getMonths();
	}
	
	/**
	 * Return the age in the format {years}y {months}m {days}d or with other locale pattern
	 * 
	 * @author Mwithi 
	 * @param birthDate - the birthdate
	 * @return string with the formatted age
	 */
	public static String getFormattedAge(Date birthDate) {
		GregorianCalendar birthday = new GregorianCalendar();
		String pattern = MessageBundle.getMessage("angal.common.agepattern");
		String age = "";
		if (birthDate != null) {
			birthday.setTime(birthDate);
			DateTime now = new DateTime();
			DateTime birth = new DateTime(birthday.getTime());
			Period period = new Period(birth, now, PeriodType.yearMonthDay());
			age = MessageFormat.format(pattern, period.getYears(), period.getMonths(), period.getDays());
		}
		return age;
	}
	
	/**
	 * Return a string representation of the dateTime with the given pattern
	 * @param dateTime - a GregorianCalendar object
	 * @param pattern - the pattern. If <code>null</code> "yyyy-MM-dd HH:mm:ss" will be used
	 * @return the String represetation of the GregorianCalendar
	 */
	public static String formatDateTime(GregorianCalendar dateTime, String pattern) {
		if (pattern == null) pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat format = new SimpleDateFormat(pattern);  //$NON-NLS-1$
		return format.format(dateTime.getTime());
	}
	
	/**
	 * Return a {@link GregorianCalendar} representation of the string using the given pattern
	 * @param string - a String object to be passed
	 * @param pattern - the pattern. If <code>null</code> "yyyy-MM-dd HH:mm:ss" will be used
	 * @param noTime - if <code>True</code> the time will be 00:00:00, actual time otherwise.
	 * @return the String represetation of the GregorianCalendar
	 * @throws ParseException 
	 */
	public static GregorianCalendar parseDate(String string, String pattern, boolean noTime) throws ParseException {
		if (pattern == null) pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat format = new SimpleDateFormat(pattern);  //$NON-NLS-1$
		Date date = format.parse(string);
		GregorianCalendar calendar = new GregorianCalendar();
		if (noTime) {
			calendar.setTime(date);
			System.out.println(formatDateTime(calendar, null));
		} else {
			calendar.setTimeInMillis(date.getTime());
			System.out.println(formatDateTime(calendar, null));
		}
		
		return calendar;
	}
	
	/**
	 * Return a {@link GregorianCalendar} representation of today date
	 * @param midnight - if <code>True</code> the time will be 23:59:59, 00:00:00 otherwise. 
	 * @return the today date
	 */
	public static GregorianCalendar today(boolean midnight) {
		
		GregorianCalendar today = new GregorianCalendar();
		if (midnight) {
			today.set(GregorianCalendar.HOUR_OF_DAY, 23);
			today.set(GregorianCalendar.MINUTE, 59);
			today.set(GregorianCalendar.SECOND, 59);
		} else {
			today.set(GregorianCalendar.HOUR_OF_DAY, 0);
			today.set(GregorianCalendar.MINUTE, 0);
			today.set(GregorianCalendar.SECOND, 0);
			today.set(GregorianCalendar.MILLISECOND, 0);
		}
		
		return today;
	}
	
	/**
	 * Return the actual date and time of the server
	 * 
	 * @author hadesthanos 
	 * @return DateTime 
	 * @throws OHException 
	 * @throws ParseException 
	 */
	public static GregorianCalendar getServerDateTime()  {
		GregorianCalendar serverDate=new GregorianCalendar();
		String query = " SELECT NOW( ) as time ";

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getData(query, true);
			while (resultSet.next()) {
				String date = resultSet.getString("time");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				java.util.Date utilDate = new java.util.Date();
				utilDate = sdf.parse(date);
				serverDate.setTime(utilDate);				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (OHException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}  finally {
			try {
				dbQuery.releaseConnection();
			} catch (OHException e) {
				e.printStackTrace();
			}
		}
		return serverDate;
	}
}
