package org.babinkuk.diff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@AutoConfigureMockMvc
public class YesNoDataResolverTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(YesNoDataResolverTest.class);
	
	@Test
	void resolve_null() {
		
		YesNoDataResolver resolver = new YesNoDataResolver();
		
		String result = resolver.resolve(null);
		
		// assert
		assertNull(result,"result not null");
		
		result = resolver.resolve("NULL");
		
		// assert
		assertNull(result,"result not null");
	}
	
	@Test
	void resolve_not_null() {
		
		YesNoDataResolver resolver = new YesNoDataResolver();
		
		String result = resolver.resolve("Y");
		
		// assert
		assertNotNull(result,"result not null");
		assertEquals("DA", result, "result YES NOK");
		
		result = resolver.resolve("N");
		
		// assert
		assertNotNull(result,"result not null");
		assertEquals("NE", result, "result NE NOK");
	}
	
}