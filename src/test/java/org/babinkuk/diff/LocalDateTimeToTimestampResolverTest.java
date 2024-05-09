package org.babinkuk.diff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.text.ParseException;
import java.time.LocalDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@AutoConfigureMockMvc
public class LocalDateTimeToTimestampResolverTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(LocalDateTimeToTimestampResolverTest.class);
	
	@Test
	void resolve_null() {
		
		LocalDateTimeToTimestampDataResolver resolver = new LocalDateTimeToTimestampDataResolver();
		
		String result = resolver.resolve(null);
		
		// assert
		assertNull(result,"result not null");
	}
	
	@Test
	void resolve_not_null() throws ParseException {
		
		LocalDateTimeToTimestampDataResolver resolver = new LocalDateTimeToTimestampDataResolver();
		
		LocalDateTime date = LocalDateTime.of(2017, 2, 13, 15, 56, 00);    

		String result = resolver.resolve(date);
		
		// assert
		assertNotNull(result,"result null");
		assertEquals("13.02.2017. 15:56:00", result, "result NOK");
	}
	
}