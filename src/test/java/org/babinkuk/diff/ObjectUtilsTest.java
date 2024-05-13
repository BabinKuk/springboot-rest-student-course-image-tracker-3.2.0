package org.babinkuk.diff;

import static org.babinkuk.utils.ApplicationTestConstants.COURSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.weaver.NewConstructorTypeMunger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.Api;
import org.babinkuk.entity.Course;
import org.babinkuk.exception.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@AutoConfigureMockMvc
public class ObjectUtilsTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(ObjectUtilsTest.class);
	
	@Test
	void getAllFields() {
		
		Course course = new Course();
		Class<?> objectClass = course.getClass();
		
		List<Field> fieldList = ObjectUtils.getAllFields(objectClass);
		log.info(fieldList);
		
		// assert
		assertNotNull(fieldList,"fieldList null");
		assertEquals(5, fieldList.size(), "fieldList.size() NOK");
		
		assertTrue(fieldList.stream().anyMatch(field ->
			field.getType().getTypeName().equals("int")
				&& field.getName().equals("id") 
				&& field.getDeclaringClass().getTypeName().equals("org.babinkuk.entity.Course")
		));
		assertTrue(fieldList.stream().anyMatch(field ->
			field.getType().getTypeName().equals("java.lang.String")
				&& field.getName().equals("title") 
				&& field.getDeclaringClass().getTypeName().equals("org.babinkuk.entity.Course")
		));
		assertTrue(fieldList.stream().anyMatch(field ->
		field.getType().getTypeName().equals("org.babinkuk.entity.Instructor")
			&& field.getName().equals("instructor") 
			&& field.getDeclaringClass().getTypeName().equals("org.babinkuk.entity.Course")
		));
		assertTrue(fieldList.stream().anyMatch(field ->
			field.getType().getTypeName().equals("java.util.List")
				&& field.getName().equals("students") 
				&& field.getDeclaringClass().getTypeName().equals("org.babinkuk.entity.Course")
		));
		assertTrue(fieldList.stream().anyMatch(field ->
			field.getType().getTypeName().equals("java.util.List")
				&& field.getName().equals("reviews") 
				&& field.getDeclaringClass().getTypeName().equals("org.babinkuk.entity.Course")
		));
	}
	
	@Test
	void getValueForField() throws IllegalAccessException, InvocationTargetException, NoSuchFieldException, SecurityException {
		
		Course course = new Course(Api.COURSE_TITLE);
		Class<?> objectClass = course.getClass();
		Object fieldValue;
		
		for (Field field : ObjectUtils.getAllFields(objectClass)) {
			fieldValue = ObjectUtils.getValueForField(field, course);
			if (field.getName().equals("title")) {
				assertEquals(Api.COURSE_TITLE, fieldValue, "fieldValue NOK");
			}
			if (field.getName().equals("id")) {
				assertEquals(0, fieldValue, "fieldValue NOK");
			}
			if (field.getName().equals("instructor")) {
				assertNull(fieldValue, "fieldValue NOK");
			}
			if (field.getName().equals("reviews")) {
				assertNull(fieldValue, "fieldValue NOK");
			}
			if (field.getName().equals("students")) {
				assertNotNull(fieldValue, "fieldValue NOK");
			}
		}
	}
	
	@Test
	void getValueForFieldName() throws IllegalAccessException, InvocationTargetException, NoSuchFieldException, SecurityException {
		
		Course course = new Course(Api.COURSE_TITLE);
		
		Object fieldValue = ObjectUtils.getValueForFieldName("title", course);
		
		assertEquals(Api.COURSE_TITLE, fieldValue, "fieldValue NOK");
		
		fieldValue = ObjectUtils.getValueForFieldName("id", course);
		
		assertEquals(0, fieldValue, "fieldValue NOK");
		
		fieldValue = ObjectUtils.getValueForFieldName("instructor", course);
		
		assertEquals(null, fieldValue, "fieldValue NOK");
	}

	@Test
	void setValueForField() throws IllegalAccessException, InvocationTargetException {
		
		Course course = new Course(Api.COURSE_TITLE);
		Class<?> objectClass = course.getClass();
		Object fieldValue;
		
		for (Field field : ObjectUtils.getAllFields(objectClass)) {
			fieldValue = ObjectUtils.getValueForField(field, course);
			if (field.getName().equals("title")) {
				assertEquals(Api.COURSE_TITLE, fieldValue, "fieldValue NOK");
				
				ObjectUtils.setValueForField(field, (Object) course, (Object) new String("NEW COURSE"));
				
				fieldValue = ObjectUtils.getValueForField(field, course);
				
				assertEquals("NEW COURSE", fieldValue, "fieldValue NOK");
			}
			if (field.getName().equals("id")) {
				assertEquals(0, fieldValue, "fieldValue NOK");
				
				ObjectUtils.setValueForField(field, (Object) course, (Object) Integer.valueOf(33));
				
				fieldValue = ObjectUtils.getValueForField(field, course);
				
				assertEquals(Integer.valueOf(33), fieldValue, "fieldValue NOK");
			}
		}
	}
	
	@Test
	void setStringValueForField() throws IllegalAccessException, InvocationTargetException {
		
		Course course = new Course(Api.COURSE_TITLE);
		Class<?> objectClass = course.getClass();
		Object fieldValue;
		
		for (Field field : ObjectUtils.getAllFields(objectClass)) {
			fieldValue = ObjectUtils.getValueForField(field, course);
			if (field.getName().equals("title")) {
				assertEquals(Api.COURSE_TITLE, fieldValue, "fieldValue NOK");
				
				ObjectUtils.setStringValueForField(field, (Object) course, "NEW COURSE");
				
				fieldValue = ObjectUtils.getValueForField(field, course);
				
				assertEquals("NEW COURSE", fieldValue, "fieldValue NOK");
			}
		}
	}
	
	@Test
	void findFieldInClass() throws ClassNotFoundException {
		
		String fieldName = "title";
		String objectName = "org.babinkuk.entity.Course";
		
		boolean result = ObjectUtils.findFieldInClass(fieldName, objectName);
		
		// assert
		assertTrue(result,"result not true");
		
		// not existing fields
		fieldName = "titl";
		
		result = ObjectUtils.findFieldInClass(fieldName, objectName);
		
		// assert
		assertFalse(result,"result not false");
		
		// assert exception not existing class
		Exception exception = assertThrows(ClassNotFoundException.class, () -> {
			ObjectUtils.findFieldInClass("title", "org.babinkuk.entity.CourseClassNotExist");
		});
		
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains("org.babinkuk.entity.CourseClassNotExist"));		
	}
	
	@Test
	void findIdFieldForClass() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		
		String objectName = "org.babinkuk.entity.Course";
		
		Field result = ObjectUtils.findIdFieldForClass(objectName);
		
		// assert
		assertNotNull(result,"result null");
		assertEquals("id", result.getName(), "getName NOK");
		
		// another class
		objectName = "org.babinkuk.entity.ChangeLog";
		
		result = ObjectUtils.findIdFieldForClass(objectName);
		
		// assert
		assertNotNull(result,"result null");
		assertEquals("chloId", result.getName(), "getName NOK");
		
		// not existing id field in class
		objectName = "org.babinkuk.entity.Address";
		
		result = ObjectUtils.findIdFieldForClass(objectName);
		
		// assert
		assertNull(result,"result not null");
		
		// assert exception not existing class
		Exception exception = assertThrows(ClassNotFoundException.class, () -> {
			ObjectUtils.findIdFieldForClass("org.babinkuk.entity.CourseClassNotExist");
		});
		
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains("org.babinkuk.entity.CourseClassNotExist"));
	}
	
	@Test
	void getGetterMethodForField() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		
		Course course = new Course();
		Class<?> objectClass = course.getClass();
		Field field = null;
		
		List<Field> fieldList = ObjectUtils.getAllFields(objectClass);
		log.info(fieldList);
		
		for (Field fld : fieldList) {
			if (fld.getName().equals("title")) {
				field = fld;
				
				Method result = ObjectUtils.getGetterMethodForField(field, objectClass);
				
				// assert
				assertNotNull(result,"result null");
				assertEquals("getTitle", result.getName(), "getName() NOK");
			}
			
			if (fld.getName().equals("reviews")) {
				field = fld;
				
				Method result = ObjectUtils.getGetterMethodForField(field, objectClass);
				
				// assert
				assertNotNull(result,"result null");
				assertEquals("getReviews", result.getName(), "getName() NOK");
			}
		}
	}
	
//	@Test
//	void formatDate_not_null() throws ParseException {
//		
//		String dateString = "26.09.1989 22:23:24";
//		//Instantiating the SimpleDateFormat class
//		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
//		//Parsing the given String to Date object
//		Date date = formatter.parse(dateString); 
//		
//		String result = DateFormatter.formatDate(date);
//
//		// assert
//		assertNotNull(result,"result null");
//		assertEquals("26.09.1989.", result, "result NOK");
//	}
//	
//	@Test
//	void formatDateTime_null() throws ParseException {
//		
//		String result = DateFormatter.formatDateTime(null);
//		
//		// assert
//		assertNull(result,"result not null");
//	}
//	
//	@Test
//	void formatDateTime_not_null() throws ParseException {
//		
//		String dateString = "26.09.1989 22:23:24";
//		//Instantiating the SimpleDateFormat class
//		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
//		//Parsing the given String to Date object
//		Date date = formatter.parse(dateString);
//		
//		String result = DateFormatter.formatDateTime(date);
//
//		// assert
//		assertNotNull(result,"result null");
//		assertEquals("26.09.1989. 22:23", result, "result NOK");
//		
//		dateString = "26.09.1989";
//		//Instantiating the SimpleDateFormat class
//		formatter = new SimpleDateFormat("dd.MM.yyyy");
//		//Parsing the given String to Date object
//		date = formatter.parse(dateString); 
//		
//		result = DateFormatter.formatDateTime(date);
//
//		// assert
//		assertNotNull(result,"result null");
//		assertEquals("26.09.1989. 00:00", result, "result NOK");
//	}
//	
//	@Test
//	void getDateFromString_null() throws ParseException {
//		
//		Date result = DateFormatter.getDateFromString(null);
//		
//		// assert
//		assertNull(result,"result not null");
//		
//		result = DateFormatter.getDateFromString("");
//		
//		// assert
//		assertNull(result,"result not null");
//		
//		// assert exception
//		Exception exception = assertThrows(ParseException.class, () -> {
//			DateFormatter.getDateFromString("string");
//		});
//		
//		String actualMessage = exception.getMessage();
//		
//	    assertTrue(actualMessage.contains("Unparseable date"));
//	}
//	
//	@Test
//	void getDateFromString_not_null() throws ParseException {
//		
//		String dateString = "26.09.1989.";
//
//		Date result = DateFormatter.getDateFromString(dateString);
//		
//		Calendar c = Calendar.getInstance();
//		c.setTime(result);
//		
//		// assert
//		assertNotNull(result,"result null");
//		assertEquals(1989, c.get(Calendar.YEAR), "Calendar.YEAR NOK");
//		assertEquals(Calendar.SEPTEMBER, c.get(Calendar.MONTH), "Calendar.MONTH NOK");
//		assertEquals(26, c.get(Calendar.DAY_OF_MONTH), "Calendar.DAY_OF_MONTH NOK");
//		assertEquals(0, c.get(Calendar.HOUR_OF_DAY), "Calendar.HOUR_OF_DAY NOK");
//		assertEquals(0, c.get(Calendar.MINUTE), "Calendar.MINUTE NOK");
//		assertEquals(0, c.get(Calendar.SECOND), "Calendar.SECOND NOK");
//		
//		dateString = "26.09.1989";
//
//		result = DateFormatter.getDateFromString(dateString);
//		
//		c = Calendar.getInstance();
//		c.setTime(result);
//		
//		// assert
//		assertNotNull(result,"result null");
//		assertEquals(1989, c.get(Calendar.YEAR), "Calendar.YEAR NOK");
//		assertEquals(Calendar.SEPTEMBER, c.get(Calendar.MONTH), "Calendar.MONTH NOK");
//		assertEquals(26, c.get(Calendar.DAY_OF_MONTH), "Calendar.DAY_OF_MONTH NOK");
//		assertEquals(0, c.get(Calendar.HOUR_OF_DAY), "Calendar.HOUR_OF_DAY NOK");
//		assertEquals(0, c.get(Calendar.MINUTE), "Calendar.MINUTE NOK");
//		assertEquals(0, c.get(Calendar.SECOND), "Calendar.SECOND NOK");
//		
//		dateString = "22.12.1990. 22:23";
//
//		result = DateFormatter.getDateFromString(dateString);
//		
//		c = Calendar.getInstance();
//		c.setTime(result);
//		
//		// assert
//		assertNotNull(result,"result null");
//		assertEquals(1990, c.get(Calendar.YEAR), "Calendar.YEAR NOK");
//		assertEquals(Calendar.DECEMBER, c.get(Calendar.MONTH), "Calendar.MONTH NOK");
//		assertEquals(22, c.get(Calendar.DAY_OF_MONTH), "Calendar.DAY_OF_MONTH NOK");
//		assertEquals(0, c.get(Calendar.HOUR_OF_DAY), "Calendar.HOUR_OF_DAY NOK");
//		assertEquals(0, c.get(Calendar.MINUTE), "Calendar.MINUTE NOK");
//		assertEquals(0, c.get(Calendar.SECOND), "Calendar.SECOND NOK");
//	}
//	
//	@Test
//	void formatDateDash_null() throws ParseException {
//		
//		String result = DateFormatter.formatDateDash(null);
//		
//		// assert
//		assertNull(result,"result not null");
//	}
//	
//	@Test
//	void formatDateDash_not_null() throws ParseException {
//		
//		String dateString = "26.09.1989 22:23:24";
//		//Instantiating the SimpleDateFormat class
//		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
//		//Parsing the given String to Date object
//		Date date = formatter.parse(dateString); 
//		
//		String result = DateFormatter.formatDateDash(date);
//
//		// assert
//		assertNotNull(result,"result null");
//		assertEquals("26-09-1989", result, "result NOK");
//	}
//	
//	@Test
//	void formatMonthAndYear_null() throws ParseException {
//		
//		String result = DateFormatter.formatMonthAndYear(null);
//		
//		// assert
//		assertNull(result,"result not null");
//	}
//	
//	@Test
//	void formatMonthAndYear_not_null() throws ParseException {
//		
//		String dateString = "26.09.1989 22:23:24";
//		//Instantiating the SimpleDateFormat class
//		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
//		//Parsing the given String to Date object
//		Date date = formatter.parse(dateString); 
//		
//		String result = DateFormatter.formatMonthAndYear(date);
//
//		// assert
//		assertNotNull(result,"result null");
//		assertEquals("09. 1989.", result, "result NOK");
//	}
//	
//	@Test
//	void getFirstMomentOfDay_null() {
//		
//		// assert exception
//		Exception exception = assertThrows(NullPointerException.class, () -> {
//			DateFormatter.getFirstMomentOfDay(null);
//		});
//		
//		String actualMessage = exception.getMessage();
//		
//	    assertTrue(actualMessage.contains("date must not be null"));
//	}
//	
//	@Test
//	void getFirstMomentOfDay_not_null() throws ParseException {
//		
//		String dateString = "26.09.1989 22:23:24";
//		//Instantiating the SimpleDateFormat class
//		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
//		//Parsing the given String to Date object
//		Date date = formatter.parse(dateString);
//		
//		Date result = DateFormatter.getFirstMomentOfDay(date);
//		
//		Calendar c = Calendar.getInstance();
//		c.setTime(result);
//		
//		// assert
//		assertNotNull(result,"result null");
//		assertEquals(1989, c.get(Calendar.YEAR), "Calendar.YEAR NOK");
//		assertEquals(Calendar.SEPTEMBER, c.get(Calendar.MONTH), "Calendar.MONTH NOK");
//		assertEquals(26, c.get(Calendar.DAY_OF_MONTH), "Calendar.DAY_OF_MONTH NOK");
//		assertEquals(0, c.get(Calendar.HOUR_OF_DAY), "Calendar.HOUR_OF_DAY NOK");
//		assertEquals(0, c.get(Calendar.MINUTE), "Calendar.MINUTE NOK");
//		assertEquals(0, c.get(Calendar.SECOND), "Calendar.SECOND NOK");
//		assertEquals(0, c.get(Calendar.MILLISECOND), "Calendar.MILLISECOND NOK");
//	}
//	
//	@Test
//	void getLastMomentOfDay_null() {
//		
//		// assert exception
//		Exception exception = assertThrows(NullPointerException.class, () -> {
//			DateFormatter.getLastMomentOfDay(null);
//		});
//		
//		String actualMessage = exception.getMessage();
//		
//	    assertTrue(actualMessage.contains("date must not be null"));
//	}
//	
//	@Test
//	void getLastMomentOfDay_not_null() throws ParseException {
//		
//		String dateString = "26.09.1989 22:23:24";
//		//Instantiating the SimpleDateFormat class
//		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
//		//Parsing the given String to Date object
//		Date date = formatter.parse(dateString);
//		
//		Date result = DateFormatter.getLastMomentOfDay(date);
//		
//		Calendar c = Calendar.getInstance();
//		c.setTime(result);
//		
//		// assert
//		assertNotNull(result,"result null");
//		assertEquals(1989, c.get(Calendar.YEAR), "Calendar.YEAR NOK");
//		assertEquals(Calendar.SEPTEMBER, c.get(Calendar.MONTH), "Calendar.MONTH NOK");
//		assertEquals(26, c.get(Calendar.DAY_OF_MONTH), "Calendar.DAY_OF_MONTH NOK");
//		assertEquals(23, c.get(Calendar.HOUR_OF_DAY), "Calendar.HOUR_OF_DAY NOK");
//		assertEquals(59, c.get(Calendar.MINUTE), "Calendar.MINUTE NOK");
//		assertEquals(59, c.get(Calendar.SECOND), "Calendar.SECOND NOK");
//		assertEquals(999, c.get(Calendar.MILLISECOND), "Calendar.MILLISECOND NOK");
//	}
//	
//	@Test
//	void formatLocalDate_null() {
//		
//		String result = DateFormatter.formatLocalDate(null);
//		
//		// assert
//		assertNull(result,"result not null");
//	}
//	
//	@Test
//	void dateToLocalDate_not_null() throws ParseException {
//		
//		LocalDate date = LocalDate.of(2017, 2, 13);
//		
//		String result = DateFormatter.formatLocalDate(date);
//		
//		// assert
//		assertNotNull(result,"result null");
//		assertEquals("13.02.2017.", result, "result NOK");
//	}
}