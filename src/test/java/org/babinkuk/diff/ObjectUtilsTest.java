package org.babinkuk.diff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.Api;
import org.babinkuk.entity.Course;
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
}