package org.babinkuk.service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.config.MessagePool;
import org.babinkuk.dao.ChangeLogRepository;
import org.babinkuk.diff.DiffField;
import org.babinkuk.diff.DiffGenerator;
import org.babinkuk.diff.ObjectUtils;
import org.babinkuk.entity.ChangeLog;
import org.babinkuk.entity.ChangeLogItem;
import org.babinkuk.exception.ApplicationServiceException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChangeLogServiceImpl implements ChangeLogService {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	private DiffGenerator diffGenerator;
	
	@Autowired
	private ChangeLogRepository changeLogRepository;

	@Autowired
	public ChangeLogServiceImpl(ChangeLogRepository changeLogRepository) {
		this.changeLogRepository = changeLogRepository;
	}
	
	public ChangeLogServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Iterable<ChangeLog> getAllChangeLogs() {
		return changeLogRepository.findAll();
	}

	@Override
	public Optional<ChangeLog> findById(int id) throws ObjectNotFoundException {
		
		Optional<ChangeLog> result = changeLogRepository.findById(id);
		
		ChangeLog chlo = null;
		
		if (result.isPresent()) {
			chlo = result.get();
			log.info("chlo ({})", chlo);	
		} else {
			// not found
			String message = String.format(MessagePool.getMessage("Chlo with id=%s not found"), id);
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
		
		return result;
	}

	@Override
	public void saveChangeLog(ChangeLog changeLog, Object original, Object current) throws ApplicationServiceException {
		
		Set<ChangeLogItem> itemSet = new HashSet<ChangeLogItem>();
		
		// prepare changeLog
		if (original != null && current != null) {
			try {
				this.removeBlanksFromObject(current);
			} catch (ApplicationServiceException e) {
				log.error("Failed to prepare change log : ", e.getMessage());
			}
		}
		
		itemSet = diffGenerator.difference(original, current, null, null, itemSet, null);
		
		if (!itemSet.isEmpty()) {
			for (ChangeLogItem item : itemSet) {
				item.setChangeLog(changeLog);
			}
			
			changeLog.setChangeLogItems(itemSet);
			
			changeLogRepository.save(changeLog);
		}	
	}

	@Override
	public void deleteChangeLog(int id) throws ObjectNotFoundException {
		// TODO Auto-generated method stub
		changeLogRepository.deleteById(id);
	}
	
	private Object removeBlanksFromObject(Object obj) throws ApplicationServiceException {
		try {
			final Class<?> objectClass = obj.getClass();
			for (Field field : ObjectUtils.getAllFields(objectClass)) {
				// check field annotations and type
				if (field.isAnnotationPresent(DiffField.class) && field.getType().equals(String.class)) {
					Object fieldValue = ObjectUtils.getValueForField(field, obj);
					String strFieldValue = fieldValue != null ? fieldValue.toString() : "";
					if (StringUtils.isBlank(strFieldValue)) {
						ObjectUtils.setValueForField(field, obj, null);
					}
				}
			}
		} catch (IllegalAccessException e) {
			throw new ApplicationServiceException(e.getMessage());
		}  catch (InvocationTargetException e) {
			throw new ApplicationServiceException(e.getMessage());
		}
		
		return obj;
	}
}
