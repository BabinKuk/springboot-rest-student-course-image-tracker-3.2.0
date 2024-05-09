package org.babinkuk.diff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@AutoConfigureMockMvc
public class DateToTimestampResolverTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(DateToTimestampResolverTest.class);
	
	@Test
	void resolve_null() {
		
		DateToTimestampDataResolver resolver = new DateToTimestampDataResolver();
		
		String result = resolver.resolve(null);
		
		// assert
		assertNull(result,"result not null");
	}
	
	@Test
	void resolve_not_null() throws ParseException {
		
		DateToTimestampDataResolver resolver = new DateToTimestampDataResolver();
		
		String dateString = "26.09.1989";
		//Instantiating the SimpleDateFormat class
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		//Parsing the given String to Date object
		Date date = formatter.parse(dateString); 
		
		String result = resolver.resolve(date);
		
		// assert
		assertNotNull(result,"result null");
		assertEquals("26.09.1989. 00:00:00", result, "result NOK");
	}
	
}