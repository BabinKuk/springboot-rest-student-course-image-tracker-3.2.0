package org.babinkuk.diff;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.babinkuk.diff.DateHelper;

public class DateFormatter {

	private static final String DOT = ".";
	
	public static Date prepareDate(Date date, String time) {
		
		Date retDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy. HH:mm");
		String [] dateString = sdf.format(date).split(" ");
		
		try {
			if(!StringUtils.isEmpty(time))
               retDate = sdf.parse(dateString[0] + " " + time);
			else
               retDate = sdf.parse(dateString[0] + " 00:00");
		} catch (ParseException e) {
			throw new RuntimeException("timestamp_format_error");
		}
		
		return retDate;
	}
	
	public static String formatDate(Date inputDate) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
		return inputDate == null ?  null : sdf.format(inputDate);
	}
	
	public static String formatDateTime(Date inputDate) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy. HH:mm");
		return inputDate == null ?  null : sdf.format(inputDate);
	}
	
	/**
	 * Method that converts a string date in dd.mm.yyyy format to date
	 *
	 * @param inputDate
	 * @return
	 * @throws ParseException
	 */
	public static Date getDateFromString(String inputDate) throws ParseException {
		
		if (!StringUtils.isEmpty(inputDate)) {
			// format input date (add dot at end)
			inputDate = StringUtils.removeEnd(inputDate, DOT);
			inputDate = inputDate + DOT;
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
			Date date = sdf.parse(inputDate);
			
			return date;
		} else 
			return null;
	}
	
	/**
	 * Method that converts a date in dd-mm-yyyy format to string
     *
     * @param inputDate
     * @return
     * @throws ParseException
     */
	public static String formatDateDash(Date inputDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy.");
		return sdf.format(inputDate);
	}
	
	/**
	 * Method that converts a date in dd-mm-yyyy format to string without dot at the end
	 *
	 * @param inputDate
	 * @return
	 * @throws ParseException
	 */
	public static String formatDateDashNew(Date inputDate) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		return sdf.format(inputDate);
	}
	
	public static String formatMonthAndYear(Date date) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM. yyyy.", new Locale("hr"));
		return sdf.format(date);
	}
	
	public static java.util.Date getFirstMomentOfDay(java.util.Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.getTime();                          
	}
  
   public static java.util.Date getLastMomentOfDay(java.util.Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		
		return c.getTime();                          
   }
   
public static String formatLocalDate(LocalDate inputLocalDate) {
	   
	   Date date = DateHelper.localDateToDate(inputLocalDate);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
		return date == null ?  null : sdf.format(date);
               }
}
