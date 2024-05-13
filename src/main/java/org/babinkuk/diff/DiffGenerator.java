package org.babinkuk.diff;

import java.lang.reflect.Field;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.babinkuk.config.Api;
import org.babinkuk.entity.ChangeLogItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Method of this class are used to find difference between two objects.
 * 
 * @author BabinKuk
 *
 */
public class DiffGenerator {

	private final Logger log = LogManager.getLogger(getClass());
	
	private final Map<String, DataResolver> resolvers = new HashMap<String, DataResolver>();
	
	public DiffGenerator() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Registers a {@link DataResolver DataResolver} to resolve data of type <code>forType<code>.
	 * <p/>
	 * The <code>org.babinkuk.diff.DiffGenerator.difference(Object, Object, Object, Object, Set<ChangeLogItem>, String)</code> method can resolve data, using a <code>DataResolver</code>.  The field's
	 * {@link DiffField} annotation can define a data type for the field, which the
	 * <code>org.babinkuk.diff.DiffGenerator.difference(Object, Object, Object, Object, Set<ChangeLogItem>, String)</code> method will then lookup in its registered resolvers, and pass the value
	 * found in the actual field to this resolver, and use the result for the actual difference calculation.
	 *
	 * @param forType  the user-defined and application-specific field/data type to register a resolver for
	 * @param resolver the resolver to register for the field/data type
	 * @return the <code>DataResolver</code> previously registered for this data type, if any, or <code>null</code> otherwise
	 * @see DataResolver
	 * @see DiffGenerator#unregisterDataResolver(String)
	 * @see DiffField
	 */
	public synchronized DataResolver registerDataResolver(String forType, DataResolver resolver) {
		DataResolver old = resolvers.get(forType);
		resolvers.put(forType, resolver);
		return old;
	}

    /**
	 * Unregisters a {@link DataResolver DataResolver}.
	 *
	 * @param forType the field/data type for which to unregister the resolver
	 * @see DataResolver
	 * @see DiffGenerator#registerDataResolver(String, DataResolver)
	 * @see DiffField
	 */
	public synchronized void unregisterDataResolver(String forType) {
		resolvers.remove(forType);
	}

    /**
	 * Compare original and current object, add differences to ChangeLogItem set.
	 *
	 * @param original original object from database for comparison
	 * @param current current object for comparison
	 * @param originalId id of original object
	 * @param currentId id of current object
	 * @param itemSet ChangeLogItem set filled with differences
	 * @param tag specifies place of distinction
	 * @return
	 */
    public Set<ChangeLogItem> difference(Object original, Object current, Object originalId, Object currentId, Set<ChangeLogItem> itemSet, String tag) {
    	log.info("original {}", original);
    	log.info("current {}", current);
    	
    	Set<ChangeLogItem> returnValue = itemSet;
    	
    	if (original != null && current != null && original.getClass() != current.getClass()) {
    		throw new RuntimeException("'original' and 'current' arguments not same, this usually happens with" +
    				" persistent collections (since they are accessed with a proxy object)." +
    				" Original:" + original.getClass().getName() + " Current:" + current.getClass().getName());
    	}
    	
    	DiffField annotation;
    	String idField;
    	
    	// case when both values are not null:
    	if (original != null && current != null) {
    		log.info("case when both values are not null");
    		
    		if (tag == null) {
    			tag = original.getClass().getSimpleName();
    		}
    		
    		if(!tag.endsWith(".")) {
    			tag += ".";
    		}
    		
    		final String prefix = tag;
    		final Class<?> objectClass = original.getClass();
    		log.info("Diffing objects of type: " + objectClass.getSimpleName());
    		
    		// Check whether the class is Diffable
    		if (objectClass.isAnnotationPresent(Diffable.class)) {
    			log.info(objectClass.getSimpleName() + " is Diffable");

    			for (Field field : ObjectUtils.getAllFields(objectClass)) {
    				// Only check fields annotated with DiffField
    				if (field.isAnnotationPresent(DiffField.class)) {
    					
    					Object originalFieldValue;
    					Object currentFieldValue;
    					
    					try {
    						originalFieldValue = ObjectUtils.getValueForField(field, original);
    						currentFieldValue = ObjectUtils.getValueForField(field, current);
    					} catch (IllegalAccessException e) {
    						log.error("Error accessing field " + field.getName() + ": " + e);
    						log.error("e.getCause().getMessage()= " + e.getCause().getMessage());
    						//e.printStackTrace();
    						continue;
    					} catch (InvocationTargetException e) {
    						log.error("Error accessing field " + field.getName() + ": " + e);
    						log.error("e.getCause().getMessage()= " + e.getCause().getMessage());
    						//e.printStackTrace();
    						continue;
    					}
    					
    					annotation = field.getAnnotation(DiffField.class);
    					String dataType = annotation.type();
    					DataResolver dataResolver = null;
    					
    					if (StringUtils.isNotBlank(dataType)) {
    						synchronized (this) {
    							dataResolver = resolvers.get(dataType);
    						}
    					}
    					
    					if (dataResolver != null) {
    						originalFieldValue = dataResolver.resolve(originalFieldValue);
    						currentFieldValue = dataResolver.resolve(currentFieldValue);
    					}
    					
    					idField = annotation.id();
    					
    					if (StringUtils.isBlank(idField)) {
    						Diffable diffable = objectClass.getAnnotation(Diffable.class);
    						idField = diffable.id();
    					}
    					
    					Object originalFieldIdValue = null;
    					Object currentFieldIdValue = null;
    					
    					try {
    						originalFieldIdValue = ObjectUtils.getValueForFieldName(idField, original);
                            currentFieldIdValue = ObjectUtils.getValueForFieldName(idField, current);
                        } catch (IllegalAccessException e) {
							log.error("Error accessing field " + idField + ": " + e);
							log.error("e.getCause().getMessage()= " + e.getCause().getMessage());
							//e.printStackTrace();
                        } catch (InvocationTargetException e) {
                        	log.error("Error accessing field " + idField + ": " + e);
                        	log.error("e.getCause().getMessage()= " + e.getCause().getMessage());
                        	//e.printStackTrace();
                        }

    					// Recursively call diff.DiffGenerator.difference() on the two values, appending the field name to the tag.
    					log.info("Recursively call diff.DiffGenerator.difference() {}", prefix + field.getName());
    					returnValue = difference(originalFieldValue, currentFieldValue, originalFieldIdValue, currentFieldIdValue, returnValue, prefix + field.getName());
    				}
    			}
    		} 
    		else {
    			// For non-Diffable classes...
    			log.info(objectClass.getSimpleName() + " is not Diffable.");
    			
    			// Iterate through iterable objects
    			if (original instanceof Iterable) {//collection, list, queue, set
    				//log.info(objectClass.getSimpleName() + " is Iterable.");
    				Collection<?> orgColl = (Collection<?>) original;
    				Collection<?> curColl = (Collection<?>) current;
    				
    				//log.info("orgColl {}", orgColl);
    				//log.info("curColl {}", curColl);
    				
    				// copy current list
    				Collection<?> addedElements = curColl;
    				// remove original elements -> only added alements remain
    				addedElements = subtract(addedElements, orgColl);
    				//addedElements.removeAll(orgColl);
    				//log.info("addedElements {}", addedElements);
    				
    				// copy original list
    				Collection<?> deletedElements = orgColl;
    				// remove current elements -> only deleted lements remain
    				deletedElements = subtract(deletedElements, curColl);
    				//deletedElements.removeAll(curColl);
    				//Collection<?> commonElements = orgColl;
    				//commonElements.retainAll(curColl);
    				//log.info("deletedElements {}", deletedElements);
    				
    				for(Object added : addedElements) {
    					returnValue = difference(null, added, null, null, returnValue, prefix);
    				}
    				
    				for(Object deleted : deletedElements) {
    					returnValue = difference(deleted, null, null, null, returnValue, prefix);
    				}
    				
    			// Iterate through map keys
    			} else if (original instanceof Map) {
    				Map<?, ?> oMap = (Map<?, ?>) original;
    				Map<?, ?> cMap = (Map<?, ?>) current;
    				Set<Object> allKeys = new HashSet<Object>();
    				
    				for (Object key : oMap.keySet()) {
    					allKeys.add(key);
    				}
    				
    				for (Object key : cMap.keySet()) {
    					allKeys.add(key);
    				}
    				
    				for (Object key : allKeys) {
    					Object oObj = oMap.get(key);
    					Object cObj = cMap.get(key);
    					
    					// Recursively call diff.DiffGenerator.difference() on the corresponding values,
    					// without appending prefix.
    					returnValue = difference(oObj, cObj, key, key, returnValue, prefix);
    				}
    			// If class isn't Diffable, not iterable, and not a map, simply use equals() to find any differences
    			} else if (!original.equals(current)) {
    				log.info("class isn't Diffable");
    				boolean addChangeLogItem = true;
    				// special check for BigDecimals because
    				// equals considers two BigDecimal objects equal only if they are equal in value and scale
    				// (thus 2.0 is not equal to 2.00 when compared by this method). Use compareTo
    				if (original instanceof java.math.BigDecimal && current instanceof java.math.BigDecimal) {
    					if (((BigDecimal)original).compareTo((BigDecimal)current) == 0 )
    						addChangeLogItem = false;// they're equal!
    				}
    				
    				String originalString;
    				String currentString;
    				
    				if (original instanceof java.util.Date) {
    					originalString = DateFormatter.formatDate((Date) original);
    					currentString = DateFormatter.formatDate((Date) current);
    				} else if (original instanceof LocalDate) {
    					originalString = DateFormatter.formatLocalDate((LocalDate) original);
                        currentString = DateFormatter.formatLocalDate((LocalDate) current);
    				} else {
    					originalString = original.toString();
    					currentString = current.toString();
    				}
    				
    				if (addChangeLogItem)
    					addChangeLogItem(tag, Api.CHANGE_LOG_DATA_UPDATE_SUFIX, originalId, originalString, currentId, currentString, itemSet);
    			}
    			else if (original.equals(current)) {
    				log.info("(" + original + ").equals(" + current + ") {}", original.equals(current));
    			}
    		}
    	// Special case when either, but not both, is null. If both are null, there is no difference to record.
    	} else if (original != current) {
    		log.info("Special case when either, but not both, is null");
    		boolean addChLogItem = true;
    		
    		if (original == null) { // --> insert
    			if (tag == null) {
    				tag = current.getClass().getSimpleName();
    			}
    			
    			if (currentId == null) {
    				annotation = current.getClass().getAnnotation(DiffField.class);
    				
    				if (annotation != null) {
    					idField = annotation.id();
    				} else {
						idField = null;
					}
    				
    				if (StringUtils.isBlank(idField)) {
    					Diffable diffable = current.getClass().getAnnotation(Diffable.class);

    					if (diffable != null) {
    						idField = diffable.id();
    					}
    				}
    				
    				try {
    					currentId = ObjectUtils.getValueForFieldName(idField, current);
    				} catch (IllegalAccessException e) {
    					log.error("Error accessing field " + idField + ": " + e);
                        log.error("e.getCause().getMessage()= " + e.getCause().getMessage());
                        //e.printStackTrace();
    				} catch (InvocationTargetException e) {
    					log.error("Error accessing field " + idField + ": " + e);
    					log.error("e.getCause().getMessage()= " + e.getCause().getMessage());
    					//e.printStackTrace();
    				}
    			}

    			String currentString = "";
    			if (current instanceof java.util.Date) {
    				currentString = DateFormatter.formatDate((Date) current);
    			}
    			else if (current instanceof LocalDate) {
    				currentString = DateFormatter.formatLocalDate((LocalDate) current);
    			}
    			else if(current instanceof java.util.Map) {
    				Map currentMap = (Map)current;
					Collection currentMapValues = currentMap.values();

					if ((currentMapValues != null) && (currentMapValues.size() > 0)) {
						Object[] currentMapValueArray = currentMapValues.toArray();
						
						for (int i = 0; i < currentMapValueArray.length; i++) {
							currentString += currentMapValueArray[i].toString();
							
							if (i < (currentMapValueArray.length - 1)) {
								currentString += "; ";        
							}                                                                                       
						}                                                         
					} else {
						// currentString = current.toString();
						addChLogItem = false;
					}
    			} else {
    				currentString = current.toString();
                }
    			
    			if (addChLogItem) {
    				addChangeLogItem(tag, Api.CHANGE_LOG_DATA_ENTRY_SUFIX, null, null, currentId, currentString, itemSet);
    			}
    		} else { //current is null --> delete
    			if (tag == null) {
    				tag = original.getClass().getSimpleName();
                }

    			String originalString = null;

                if (original instanceof java.util.Date) {
                	originalString = DateFormatter.formatDate((Date) original);
                }
                else if (original instanceof LocalDate) {
                    originalString = DateFormatter.formatLocalDate((LocalDate) original);
                }
                else if (original instanceof java.util.Map) {
                	Map originalMap = (Map) original;
                	if ((originalMap == null) || (originalMap.isEmpty()))
                		addChLogItem = false;
                	
                	originalString = original.toString();
                }
                // Iterate through iterable objects
                else if (original instanceof Iterable) {//collection, list, queue, set
    				Collection<?> orgColl = (Collection<?>) original;
    				
    				if ((orgColl == null) || (orgColl.isEmpty()))
                		addChLogItem = false;
                	
                	originalString = original.toString();
    			}
				else {
					originalString = original.toString();
				}

                if (addChLogItem) {
                	addChangeLogItem(tag, Api.CHANGE_LOG_DATA_DELETE_SUFIX, originalId, originalString, null, null, itemSet);
                }
            }
        }
    	
        return returnValue;
	}

	/**
	 * Helper method to add new change log item to change log item set
	 *
	 * @param tag
	 * @param suffix
	 * @param oldId
	 * @param oldValue
	 * @param newId
	 * @param newValue
	 * @param itemSet
	 */
	private void addChangeLogItem(String tag, String suffix, Object oldId, String oldValue, Object newId, String newValue, Set<ChangeLogItem> itemSet) {
		
		if (!tag.endsWith("."))
			tag += ".";
		
		String fieldName = tag + suffix;
		
		if ((fieldName != null) && (fieldName.length() > 120))
			fieldName = fieldName.substring(0, 119);
		
		ChangeLogItem item = new ChangeLogItem();
		item.setChliFieldName(fieldName);
		item.setChliOldValue((oldValue != null) ? StringUtils.substring(oldValue, 0, 255) : "-");
		item.setChliOldValueId((oldId != null) ? (int) oldId : 0);
		item.setChliNewValue(StringUtils.isNotBlank(newValue) ? StringUtils.substring(newValue, 0, 255) : "-");
		item.setChliNewValueId((newId != null) ? (int) newId : 0);
		
		//log.info(item.toString());
		itemSet.add(item);
	}
	
	/**
	 * remove elements in collection b from Collection a
	 * result = a - b
	 * 
	 * @param a
	 * @param b
	 * @return a - b
	 */
	public Collection subtract(final Collection a, final Collection b) {
		
		ArrayList list = new ArrayList(a);
		Iterator it = b.iterator();
		
		while (it.hasNext()) {
			list.remove(it.next());
		}
		
		return list;

	}
}