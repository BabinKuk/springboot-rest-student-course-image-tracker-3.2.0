package org.babinkuk.diff;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
 
import org.apache.commons.lang3.time.DateUtils;

import static java.util.Objects.isNull;

public class DateHelper {
	
	public static Date clearTimestamp(Date date) {
		
		Date result = null;
		
		if (date != null) {
			result = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		}
		
		return result;
	}
	
	public static Date getNextDay(Date date) {
		
		Date result = null;
		
		if (date != null) {
			if (date.getTime() == Long.MAX_VALUE) {
				return date;
			}
			else {
				// calculate one day time span for query
				Calendar cal = new GregorianCalendar();
				cal.setTime(date);
				cal.add(Calendar.DAY_OF_MONTH, 1);
				result = DateUtils.truncate(cal.getTime(),Calendar.DAY_OF_MONTH);
			}
		}
		
		return result;          
	}
	
	public static Date localDateToDate(LocalDate localDate) {
		if (isNull(localDate)) return null;
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}
	
	public static Date localDateTimeToDate(LocalDateTime localDateTime) {
		if (isNull(localDateTime)) return null;
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}
	
	public static LocalDate dateToLocalDate(Date date) {
		
		if (isNull(date)) return null;
		if (date instanceof java.sql.Date)
			date = new Date(date.getTime());
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	public static LocalDateTime dateToLocalDateTime(Date date) {
		
		if (isNull(date)) return null;
		if (date instanceof java.sql.Date)
			date = new Date(date.getTime());
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}
