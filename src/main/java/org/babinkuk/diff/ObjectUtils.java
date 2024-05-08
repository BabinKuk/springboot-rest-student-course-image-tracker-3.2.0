package org.babinkuk.diff;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.Id;

/**
 * util class for accessing object fields
 * 
 * @author BabinKuk
 *
 */
public class ObjectUtils {
	
	private final static Logger log = LogManager.getLogger(ObjectUtils.class);
	
	/**
	 * return the list of all fields in the class
	 * superclasses included
	 * 
	 * @param objectClass
	 * @return
	 */
	public static List<Field> getAllFields(Class objectClass) {
		List<Field> fieldList = new ArrayList<Field>();
		
		for (Class clazz = objectClass; clazz != null; clazz = clazz.getSuperclass()) {
			fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
		}
		return fieldList;
	}

	/**
	 * reflective attempt to access field
	 * 
	 * The method first tries to use traditional naming convention. If it fails and the field is boolean/Boolean, it tries
	 * get-prefixed getter (as opposed to the conventional is-prefixed geter). Finally it tries to access the field directly.
	 * 
	 * @param field access field
	 * @param obj object on which to acces field
	 * @return value of the field
	 * @throws InvocationTargetException if no getter was found, or the field is not accessible, or the getter was found but was not accessible
	 * @throws IllegalAccessException if the getter throws exception
	 */
	public static Object getValueForField(Field field, Object obj) throws IllegalAccessException, InvocationTargetException {

		String boolPrefix = "is";
		String normalPrefix = "get";
		String methodTail = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
		
		if (field.getType() == Boolean.class || field.getType() == boolean.class) {
			try {
				String methodName = boolPrefix + methodTail;
				Method getter = obj.getClass().getMethod(methodName);
				return getter.invoke(obj);
			} catch (NoSuchMethodException e) {
				log.info("Didn't find propper boolean getter, trying normal : " + e);
			}
		}
		
		String methodName = normalPrefix + methodTail;
		try {
			Method getter = obj.getClass().getMethod(methodName);
			return getter.invoke(obj);
		} catch (NoSuchMethodException e) {
			if (field.isAccessible()) {
				return field.get(obj);
			} else {
				return null;
			}
		}
	}
	
	/**
	 * Attempts to access a field given by its name.
	 *
	 * @param fieldName name of the field to access
	 * @param object the object on which to access the field
	 * @return the value of the field on that object
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Object getValueForFieldName(String fieldName, Object object) throws IllegalAccessException, InvocationTargetException {
		
		Field field = null;

		for (Field tempField : getAllFields(object.getClass())) {
			if (tempField.getName().equalsIgnoreCase(fieldName)) {
				field = tempField;
				break;
			}
		}
		
		if (field != null) {
			return getValueForField(field, object);
		} else {
			return null;
		}
	}

	/**
	 * attempts to set value for the given field in the given object
	 * 
	 * @param field for which to set new value
	 * @param obj for which to access the field
	 * @param newValue
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public static void setValueForField(Field field, Object obj, Object newValue) throws IllegalAccessException, InvocationTargetException {

		String normalPrefix = "set";
		String methodTail = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
		String methodName = normalPrefix + methodTail;
		
		try {
			Method setter = obj.getClass().getMethod(methodName, field.getType());
			setter.invoke(obj, newValue);
		} catch (NoSuchMethodException e) {
			if (field.isAccessible()) {
				field.set(obj, newValue);
			}
		}
	}
	
	/**
	 * Attempts to set given String value for given field in given object.
	 *
	 * @param field
	 * @param object
	 * @param newValue
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void setStringValueForField(Field field, Object object, String newValue) throws IllegalAccessException, InvocationTargetException {
		
		String normalPrefix = "set";
		String methodTail = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
		String methodName = normalPrefix + methodTail;
		
		try {
			Method setter = object.getClass().getMethod(methodName, String.class);
			setter.invoke(object, newValue);
		} catch (NoSuchMethodException e) {
        	if(field.isAccessible()){
        		field.set(object, newValue);
        	}
		}
	}
	
	/**
	 * Find field by name in object given by its name.
	 *
	 * @param fieldName
	 * @param objectName
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static boolean findFieldInClass(String fieldName, String objectName) throws ClassNotFoundException{

		Class<?> clas = Class.forName(objectName);
		
		for (Field tempField : getAllFields(clas)) {
			if (tempField.getName().equalsIgnoreCase(fieldName)) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * In given class find id field.
	 *
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Field findIdFieldForClass(String className) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException{
		
		Field idField= null;
		Class clazz = Class.forName(className);
		
		if (clazz != null) {
			for (Field field : clazz.getDeclaredFields()) {
				Method getterMethod = ObjectUtils.getGetterMethodForField(field, clazz);
				if (getterMethod != null && getterMethod.isAnnotationPresent(Id.class)) {
					idField = field;
					break;
				}
			}
		}
		
		return idField;
	}
	
	/**
	 * Find getter method for given field in given class.
	 *
	 * @param field
	 * @param clazz
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Method getGetterMethodForField(Field field, Class clazz) throws IllegalAccessException, InvocationTargetException {
		
		String boolPrefix = "is";
		String normalPrefix = "get";
		String methodTail = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
		
		if (field.getType() == Boolean.class || field.getType() == boolean.class)
			try {
				String methodName = boolPrefix + methodTail;
				Method getter = clazz.getMethod(methodName);
				return getter;
			} catch (NoSuchMethodException noSuchMethodException) {
				log.debug("Didn't find proper boolean getter, trying normal getter.");
			}
		
		String methodName = normalPrefix + methodTail;
		
		try {
			Method getter = clazz.getMethod(methodName);
			return getter;
		} catch (NoSuchMethodException e) {
			return null;
		}
    }
	
	/**
	 * For given object create string containing values from fields with DiffField annotation.
	 * @param object
	 * @return
	 */
	public static String toString(Object object) {
		String string = "";
		
		try {
			if (object.getClass() != null) {
				for (Field field : object.getClass().getDeclaredFields()) {
					if (field.isAnnotationPresent(DiffField.class)) {
						Object tempValue = ObjectUtils.getValueForField(field, object);
						
						if (tempValue != null) {
							string += tempValue.toString() + " ";
						}
					}
				}
			}
		} catch (SecurityException e) {
			log.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			log.error(e.getMessage(), e);
		}
		
		return string;
	}
	
	/**
	 * For given object create string containing values from fields with DiffField annotation with name of field.
	 * @param object
	 * @return
	 */
	public static String toStringExt(Object object) {
		
		String string = "";
		
		try {
			if (object.getClass() != null) {
				for (Field field : object.getClass().getDeclaredFields()) {
					if (field.isAnnotationPresent(DiffField.class)) {
						Object tempValue = ObjectUtils.getValueForField(field, object);
						
						if (tempValue != null) {
							string += field.getName() + ": " +  tempValue.toString() + ", ";
						}
					}
				}
			}

		} catch (SecurityException e) {
			log.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			log.error(e.getMessage(), e);
		}
		
		return string;
	}
	
	/**
	 * For given object create string containing values from fields with DiffField annotation with name of field for every field (null or not null).
	 * @param object
	 * @return
	 */
	public static String toStringExtEach(Object object) {
		
		String string = "";
		
		try {
			if (object.getClass() != null) {
				for (Field field : object.getClass().getDeclaredFields()) {
					if (field.isAnnotationPresent(DiffField.class)) {
						Object tempValue = ObjectUtils.getValueForField(field, object);
						
						if (tempValue != null) {
							string += field.getName() + ": " +  tempValue.toString() + ", ";
						} else {
							string += field.getName() + ": null, ";
						}
					}
				}
			}
		} catch (SecurityException e) {
			log.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			log.error(e.getMessage(), e);
		}
		
		return string;
	}
}
