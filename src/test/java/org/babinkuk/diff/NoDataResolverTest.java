package org.babinkuk.diff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.Api;
import org.babinkuk.entity.Course;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@AutoConfigureMockMvc
public class NoDataResolverTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(NoDataResolverTest.class);
	
	@Test
	void resolve_null() {
		
		NoDataResolver resolver = new NoDataResolver();
		
		String result = resolver.resolve(null);
		
		// assert
		assertEquals("", result,"result not empty");
	}
	
	@Test
	void resolve_not_null() {
		
		NoDataResolver resolver = new NoDataResolver();
		
		String result = resolver.resolve(new String("Test"));
		
		// assert
		assertEquals("", result,"result not empty");
		
		result = resolver.resolve(new Course(Api.COURSE_TITLE));
		
		// assert
		assertEquals("", result,"result not empty");
	}
	
}