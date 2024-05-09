package org.babinkuk.diff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
public class DateHelperTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(DateHelperTest.class);
	
	@Test
	void clearTimestamp_null() {
		
		Date result = DateHelper.clearTimestamp(null);
		
		// assert
		assertNull(result,"result not null");
	}
	
	@Test
	void clearTimestamp_not_null() throws ParseException {
		
		String dateString = "26.09.1989 15:55:00";
		//Instantiating the SimpleDateFormat class
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		//Parsing the given String to Date object
		Date date = formatter.parse(dateString); 
		
		Date result = DateHelper.clearTimestamp(date);
		
		Calendar c = Calendar.getInstance();
		c.setTime(result);
		
		// assert
		assertNotNull(result,"result null");
		assertEquals(1989, c.get(Calendar.YEAR), "Calendar.YEAR NOK");
		assertEquals(Calendar.SEPTEMBER, c.get(Calendar.MONTH), "Calendar.MONTH NOK");
		assertEquals(26, c.get(Calendar.DAY_OF_MONTH), "Calendar.DAY_OF_MONTH NOK");
		assertEquals(0, c.get(Calendar.HOUR_OF_DAY), "Calendar.HOUR_OF_DAY NOK");
		assertEquals(0, c.get(Calendar.MINUTE), "Calendar.MINUTE NOK");
		assertEquals(0, c.get(Calendar.SECOND), "Calendar.SECOND NOK");
	}
	
	@Test
	void getNextDay_null() {
		
		Date result = DateHelper.getNextDay(null);
		
		// assert
		assertNull(result,"result not null");
	}
	
	@Test
	void getNextDay_not_null() throws ParseException {
		
		String dateString = "26.09.1989";
		//Instantiating the SimpleDateFormat class
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		//Parsing the given String to Date object
		Date date = formatter.parse(dateString); 
		
		Date result = DateHelper.getNextDay(date);
		
		Calendar c = Calendar.getInstance();
		c.setTime(result);
		
		// assert
		assertNotNull(result,"result null");
		assertEquals(1989, c.get(Calendar.YEAR), "Calendar.YEAR NOK");
		assertEquals(Calendar.SEPTEMBER, c.get(Calendar.MONTH), "Calendar.MONTH NOK");
		assertEquals(27, c.get(Calendar.DAY_OF_MONTH), "Calendar.DAY_OF_MONTH NOK");
		assertEquals(0, c.get(Calendar.HOUR_OF_DAY), "Calendar.HOUR_OF_DAY NOK");
		assertEquals(0, c.get(Calendar.MINUTE), "Calendar.MINUTE NOK");
		assertEquals(0, c.get(Calendar.SECOND), "Calendar.SECOND NOK");
		
		dateString = "31.12.1989";
		//Parsing the given String to Date object
		date = formatter.parse(dateString); 
		
		result = DateHelper.getNextDay(date);
		
		c = Calendar.getInstance();
		c.setTime(result);
		
		// assert
		assertNotNull(result,"result null");
		assertEquals(1990, c.get(Calendar.YEAR), "Calendar.YEAR NOK");
		assertEquals(Calendar.JANUARY, c.get(Calendar.MONTH), "Calendar.MONTH NOK");
		assertEquals(1, c.get(Calendar.DAY_OF_MONTH), "Calendar.DAY_OF_MONTH NOK");
		assertEquals(0, c.get(Calendar.HOUR_OF_DAY), "Calendar.HOUR_OF_DAY NOK");
		assertEquals(0, c.get(Calendar.MINUTE), "Calendar.MINUTE NOK");
		assertEquals(0, c.get(Calendar.SECOND), "Calendar.SECOND NOK");
	}
	
	@Test
	void localDateToDate_null() {
		
		Date result = DateHelper.localDateToDate(null);
		
		// assert
		assertNull(result,"result not null");
	}
	
	@Test
	void localDateToDate_not_null() {
		
		LocalDate date = LocalDate.of(2017, 2, 13);

		Date result = DateHelper.localDateToDate(date);
		Calendar c = Calendar.getInstance();
		c.setTime(result);
		
		// assert
		assertNotNull(result,"result null");
		assertEquals(2017, c.get(Calendar.YEAR), "Calendar.YEAR NOK");
		assertEquals(Calendar.FEBRUARY, c.get(Calendar.MONTH), "Calendar.MONTH NOK");
		assertEquals(13, c.get(Calendar.DAY_OF_MONTH), "Calendar.DAY_OF_MONTH NOK");
		assertEquals(0, c.get(Calendar.HOUR_OF_DAY), "Calendar.HOUR_OF_DAY NOK");
		assertEquals(0, c.get(Calendar.MINUTE), "Calendar.MINUTE NOK");
		assertEquals(0, c.get(Calendar.SECOND), "Calendar.SECOND NOK");
	}
	
	@Test
	void localDateTimeToDate_null() {
		
		Date result = DateHelper.localDateTimeToDate(null);
		
		// assert
		assertNull(result,"result not null");
	}
	
	@Test
	void localDateTimeToDate_not_null() {
		
		LocalDateTime date = LocalDateTime.of(2017, 2, 13, 15, 55, 20);

		Date result = DateHelper.localDateTimeToDate(date);
		Calendar c = Calendar.getInstance();
		c.setTime(result);
		
		// assert
		assertNotNull(result,"result null");
		assertEquals(2017, c.get(Calendar.YEAR), "Calendar.YEAR NOK");
		assertEquals(Calendar.FEBRUARY, c.get(Calendar.MONTH), "Calendar.MONTH NOK");
		assertEquals(13, c.get(Calendar.DAY_OF_MONTH), "Calendar.DAY_OF_MONTH NOK");
		assertEquals(15, c.get(Calendar.HOUR_OF_DAY), "Calendar.HOUR_OF_DAY NOK");
		assertEquals(55, c.get(Calendar.MINUTE), "Calendar.MINUTE NOK");
		assertEquals(20, c.get(Calendar.SECOND), "Calendar.SECOND NOK");
	}
	
	@Test
	void dateToLocalDate_null() {
		
		LocalDate result = DateHelper.dateToLocalDate(null);
		
		// assert
		assertNull(result,"result not null");
	}
	
	@Test
	void dateToLocalDate_not_null() throws ParseException {
		
		String dateString = "26.09.1989";
		//Instantiating the SimpleDateFormat class
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		//Parsing the given String to Date object
		Date date = formatter.parse(dateString);
		
		LocalDate result = DateHelper.dateToLocalDate(date);
		
		// assert
		assertNotNull(result,"result null");
		assertEquals(1989, result.getYear(), "YEAR NOK");
		assertEquals(Month.SEPTEMBER, result.getMonth(), "MONTH NOK");
		assertEquals(26, result.getDayOfMonth(), "DAY_OF_MONTH NOK");
	}
	
	@Test
	void dateToLocalDateTime_null() {
		
		LocalDateTime result = DateHelper.dateToLocalDateTime(null);
		
		// assert
		assertNull(result,"result not null");
	}
	
	@Test
	void dateToLocalDateTime_not_null() throws ParseException {
		
		String dateString = "26.09.1989 23:24:25";
		//Instantiating the SimpleDateFormat class
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		//Parsing the given String to Date object
		Date date = formatter.parse(dateString);
		
		LocalDateTime result = DateHelper.dateToLocalDateTime(date);
		
		// assert
		assertNotNull(result,"result null");
		assertEquals(1989, result.getYear(), "YEAR NOK");
		assertEquals(Month.SEPTEMBER, result.getMonth(), "MONTH NOK");
		assertEquals(26, result.getDayOfMonth(), "DAY_OF_MONTH NOK");
		assertEquals(23, result.getHour(), "DAY_OF_MONTH NOK");
		assertEquals(24, result.getMinute(), "DAY_OF_MONTH NOK");
		assertEquals(25, result.getSecond(), "DAY_OF_MONTH NOK");
	}
}