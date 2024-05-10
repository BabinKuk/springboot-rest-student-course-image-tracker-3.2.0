package org.babinkuk.diff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.babinkuk.exception.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@AutoConfigureMockMvc
public class DateFormatterTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(DateFormatterTest.class);
	
	@Test
	void prepareDate_fail() {
		
		// assert exception
		Exception exception = assertThrows(RuntimeException.class, () -> {
			DateFormatter.prepareDate(null, "");
		});
		
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains("date must not be null"));
	    
	    // assert exception
		exception = assertThrows(RuntimeException.class, () -> {
			DateFormatter.prepareDate(new Date(), "time");
		});
		
		actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains("timestamp_format_error"));
	}
	
	@Test
	void prepareDate_succes() throws ParseException {
		
		String dateString = "26.09.1989 15:55:00";
		//Instantiating the SimpleDateFormat class
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		//Parsing the given String to Date object
		Date date = formatter.parse(dateString); 
		
		Date result = DateFormatter.prepareDate(date, "");
		
		Calendar c = Calendar.getInstance();
		c.setTime(result);
		
		// assert
		assertNotNull(result,"result null");
		assertEquals(1989, c.get(Calendar.YEAR), "Calendar.YEAR NOK");
		assertEquals(Calendar.SEPTEMBER, c.get(Calendar.MONTH), "Calendar.MONTH NOK");
		assertEquals(26, c.get(Calendar.DAY_OF_MONTH), "Calendar.DAY_OF_MONTH NOK");
		assertEquals(0, c.get(Calendar.HOUR_OF_DAY), "Calendar.HOUR_OF_DAY NOK");
		assertEquals(0, c.get(Calendar.MINUTE), "Calendar.MINUTE NOK");
		
		result = DateFormatter.prepareDate(date, "22:30");
		
		c = Calendar.getInstance();
		c.setTime(result);
		
		// assert
		assertNotNull(result,"result null");
		assertEquals(1989, c.get(Calendar.YEAR), "Calendar.YEAR NOK");
		assertEquals(Calendar.SEPTEMBER, c.get(Calendar.MONTH), "Calendar.MONTH NOK");
		assertEquals(26, c.get(Calendar.DAY_OF_MONTH), "Calendar.DAY_OF_MONTH NOK");
		assertEquals(22, c.get(Calendar.HOUR_OF_DAY), "Calendar.HOUR_OF_DAY NOK");
		assertEquals(30, c.get(Calendar.MINUTE), "Calendar.MINUTE NOK");
	}
	
	@Test
	void formatDate_null() throws ParseException {
		
		String result = DateFormatter.formatDate(null);
		
		// assert
		assertNull(result,"result not null");
	}
	
	@Test
	void formatDate_not_null() throws ParseException {
		
		String dateString = "26.09.1989 22:23:24";
		//Instantiating the SimpleDateFormat class
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		//Parsing the given String to Date object
		Date date = formatter.parse(dateString); 
		
		String result = DateFormatter.formatDate(date);

		// assert
		assertNotNull(result,"result null");
		assertEquals("26.09.1989.", result, "result NOK");
	}
	
	@Test
	void formatDateTime_null() throws ParseException {
		
		String result = DateFormatter.formatDateTime(null);
		
		// assert
		assertNull(result,"result not null");
	}
	
	@Test
	void formatDateTime_not_null() throws ParseException {
		
		String dateString = "26.09.1989 22:23:24";
		//Instantiating the SimpleDateFormat class
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		//Parsing the given String to Date object
		Date date = formatter.parse(dateString);
		
		String result = DateFormatter.formatDateTime(date);

		// assert
		assertNotNull(result,"result null");
		assertEquals("26.09.1989. 22:23", result, "result NOK");
		
		dateString = "26.09.1989";
		//Instantiating the SimpleDateFormat class
		formatter = new SimpleDateFormat("dd.MM.yyyy");
		//Parsing the given String to Date object
		date = formatter.parse(dateString); 
		
		result = DateFormatter.formatDateTime(date);

		// assert
		assertNotNull(result,"result null");
		assertEquals("26.09.1989. 00:00", result, "result NOK");
	}
	
	@Test
	void getDateFromString_null() throws ParseException {
		
		Date result = DateFormatter.getDateFromString(null);
		
		// assert
		assertNull(result,"result not null");
		
		result = DateFormatter.getDateFromString("");
		
		// assert
		assertNull(result,"result not null");
		
		// assert exception
		Exception exception = assertThrows(ParseException.class, () -> {
			DateFormatter.getDateFromString("string");
		});
		
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains("Unparseable date"));
	}
	
	@Test
	void getDateFromString_not_null() throws ParseException {
		
		String dateString = "26.09.1989.";

		Date result = DateFormatter.getDateFromString(dateString);
		
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
		
		dateString = "26.09.1989";

		result = DateFormatter.getDateFromString(dateString);
		
		c = Calendar.getInstance();
		c.setTime(result);
		
		// assert
		assertNotNull(result,"result null");
		assertEquals(1989, c.get(Calendar.YEAR), "Calendar.YEAR NOK");
		assertEquals(Calendar.SEPTEMBER, c.get(Calendar.MONTH), "Calendar.MONTH NOK");
		assertEquals(26, c.get(Calendar.DAY_OF_MONTH), "Calendar.DAY_OF_MONTH NOK");
		assertEquals(0, c.get(Calendar.HOUR_OF_DAY), "Calendar.HOUR_OF_DAY NOK");
		assertEquals(0, c.get(Calendar.MINUTE), "Calendar.MINUTE NOK");
		assertEquals(0, c.get(Calendar.SECOND), "Calendar.SECOND NOK");
		
		dateString = "22.12.1990. 22:23";

		result = DateFormatter.getDateFromString(dateString);
		
		c = Calendar.getInstance();
		c.setTime(result);
		
		// assert
		assertNotNull(result,"result null");
		assertEquals(1990, c.get(Calendar.YEAR), "Calendar.YEAR NOK");
		assertEquals(Calendar.DECEMBER, c.get(Calendar.MONTH), "Calendar.MONTH NOK");
		assertEquals(22, c.get(Calendar.DAY_OF_MONTH), "Calendar.DAY_OF_MONTH NOK");
		assertEquals(0, c.get(Calendar.HOUR_OF_DAY), "Calendar.HOUR_OF_DAY NOK");
		assertEquals(0, c.get(Calendar.MINUTE), "Calendar.MINUTE NOK");
		assertEquals(0, c.get(Calendar.SECOND), "Calendar.SECOND NOK");
	}
	
	@Test
	void formatDateDash_null() throws ParseException {
		
		String result = DateFormatter.formatDateDash(null);
		
		// assert
		assertNull(result,"result not null");
	}
	
	@Test
	void formatDateDash_not_null() throws ParseException {
		
		String dateString = "26.09.1989 22:23:24";
		//Instantiating the SimpleDateFormat class
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		//Parsing the given String to Date object
		Date date = formatter.parse(dateString); 
		
		String result = DateFormatter.formatDateDash(date);

		// assert
		assertNotNull(result,"result null");
		assertEquals("26-09-1989", result, "result NOK");
	}
	
	@Test
	void formatMonthAndYear_null() throws ParseException {
		
		String result = DateFormatter.formatMonthAndYear(null);
		
		// assert
		assertNull(result,"result not null");
	}
	
	@Test
	void formatMonthAndYear_not_null() throws ParseException {
		
		String dateString = "26.09.1989 22:23:24";
		//Instantiating the SimpleDateFormat class
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		//Parsing the given String to Date object
		Date date = formatter.parse(dateString); 
		
		String result = DateFormatter.formatMonthAndYear(date);

		// assert
		assertNotNull(result,"result null");
		assertEquals("09. 1989.", result, "result NOK");
	}
	
	@Test
	void getFirstMomentOfDay_null() {
		
		// assert exception
		Exception exception = assertThrows(NullPointerException.class, () -> {
			DateFormatter.getFirstMomentOfDay(null);
		});
		
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains("date must not be null"));
	}
	
	@Test
	void getFirstMomentOfDay_not_null() throws ParseException {
		
		String dateString = "26.09.1989 22:23:24";
		//Instantiating the SimpleDateFormat class
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		//Parsing the given String to Date object
		Date date = formatter.parse(dateString);
		
		Date result = DateFormatter.getFirstMomentOfDay(date);
		
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
		assertEquals(0, c.get(Calendar.MILLISECOND), "Calendar.MILLISECOND NOK");
	}
	
	@Test
	void getLastMomentOfDay_null() {
		
		// assert exception
		Exception exception = assertThrows(NullPointerException.class, () -> {
			DateFormatter.getLastMomentOfDay(null);
		});
		
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains("date must not be null"));
	}
	
	@Test
	void getLastMomentOfDay_not_null() throws ParseException {
		
		String dateString = "26.09.1989 22:23:24";
		//Instantiating the SimpleDateFormat class
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		//Parsing the given String to Date object
		Date date = formatter.parse(dateString);
		
		Date result = DateFormatter.getLastMomentOfDay(date);
		
		Calendar c = Calendar.getInstance();
		c.setTime(result);
		
		// assert
		assertNotNull(result,"result null");
		assertEquals(1989, c.get(Calendar.YEAR), "Calendar.YEAR NOK");
		assertEquals(Calendar.SEPTEMBER, c.get(Calendar.MONTH), "Calendar.MONTH NOK");
		assertEquals(26, c.get(Calendar.DAY_OF_MONTH), "Calendar.DAY_OF_MONTH NOK");
		assertEquals(23, c.get(Calendar.HOUR_OF_DAY), "Calendar.HOUR_OF_DAY NOK");
		assertEquals(59, c.get(Calendar.MINUTE), "Calendar.MINUTE NOK");
		assertEquals(59, c.get(Calendar.SECOND), "Calendar.SECOND NOK");
		assertEquals(999, c.get(Calendar.MILLISECOND), "Calendar.MILLISECOND NOK");
	}
	
	@Test
	void formatLocalDate_null() {
		
		String result = DateFormatter.formatLocalDate(null);
		
		// assert
		assertNull(result,"result not null");
	}
	
	@Test
	void dateToLocalDate_not_null() throws ParseException {
		
		LocalDate date = LocalDate.of(2017, 2, 13);
		
		String result = DateFormatter.formatLocalDate(date);
		
		// assert
		assertNotNull(result,"result null");
		assertEquals("13.02.2017.", result, "result NOK");
	}
}