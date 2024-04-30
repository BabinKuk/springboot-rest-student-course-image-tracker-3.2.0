package org.babinkuk.service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.config.MessagePool;
import org.babinkuk.config.Api.RestModule;
import org.babinkuk.dao.ChangeLogRepository;
import org.babinkuk.diff.BooleanDataResolver;
import org.babinkuk.diff.DateToTimestampDataResolver;
import org.babinkuk.diff.DiffField;
import org.babinkuk.diff.DiffGenerator;
import org.babinkuk.diff.LocalDateTimeToTimestampDataResolver;
import org.babinkuk.diff.NoDataResolver;
import org.babinkuk.diff.ObjectUtils;
import org.babinkuk.diff.YesNoDataResolver;
import org.babinkuk.entity.ChangeLog;
import org.babinkuk.entity.ChangeLogItem;
import org.babinkuk.entity.LogModule;
import org.babinkuk.exception.ApplicationServiceException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.validator.ValidatorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChangeLogServiceImpl implements ChangeLogService {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	private DiffGenerator diffGenerator;
	
	private ChangeLogRepository changeLogRepository;

	@Autowired
	public ChangeLogServiceImpl(ChangeLogRepository changeLogRepository) {
		this.changeLogRepository = changeLogRepository;
		this.diffGenerator = createDiffGenerator();
	}
	
	@Override
	public Iterable<ChangeLog> getAllChangeLogs() {
		return changeLogRepository.findAll();
	}
	
	protected DiffGenerator createDiffGenerator() {
		final DiffGenerator diffGenerator = new DiffGenerator();
		
		// register data resolvers
		YesNoDataResolver yesNoDataResolver = new YesNoDataResolver();
		diffGenerator.registerDataResolver("yesno", yesNoDataResolver);
		
		BooleanDataResolver booleanDataResolver = new BooleanDataResolver();
		diffGenerator.registerDataResolver("boolean", booleanDataResolver);
		
		DateToTimestampDataResolver dateToTimestampDataResolver = new DateToTimestampDataResolver();
		diffGenerator.registerDataResolver("dateToTimestamp", dateToTimestampDataResolver);
		
		LocalDateTimeToTimestampDataResolver localDateTimeToTimestampDataResolver = new LocalDateTimeToTimestampDataResolver();
		diffGenerator.registerDataResolver("localDateTimeToTimestamp", localDateTimeToTimestampDataResolver);
		
		NoDataResolver noDataResolver = new NoDataResolver();
		diffGenerator.registerDataResolver("optionalBoolean", noDataResolver);
		
		return diffGenerator;
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
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_CHANGELOG_ID_NOT_FOUND.getMessage()), id);
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
		
		return result;
	}

	@Override
	public void saveChangeLog(ChangeLog changeLog, Object original, Object current) throws ApplicationServiceException {
		
		Set<ChangeLogItem> itemSet = new HashSet<>();
		
		// prepare changeLog
		if (original != null && current != null) {
			try {
				this.removeBlanksFromObject(current);
			} catch (ApplicationServiceException e) {
				log.error("Failed to prepare change log : ", e.getMessage());
				throw new RuntimeException("Failed to prepare ChangeLog", e);
			}
		}
		
		itemSet = diffGenerator.difference(original, current, null, null, itemSet, null);
		
		log.info(itemSet.isEmpty());
		
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
		log.info("removeBlanksFromObject {}", obj);
		
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
		} catch (InvocationTargetException e) {
			throw new ApplicationServiceException(e.getMessage());
		}
		
		return obj;
	}
	
	public static ChangeLog createChangeLog(RestModule restModule) {
		
		final ChangeLog changeLog = new ChangeLog();
		// this is to force a save of new item ... instead of update 
		changeLog.setChloId(0);
		
		// TODO in the future set real user
		changeLog.setChloUserId(restModule.getLabel());
		
		final LogModule logModule = new LogModule();
		logModule.setLmId(restModule.getModuleId());
		
		changeLog.setLogModule(logModule);
		
		// TODO in the future set real course id
		changeLog.setChloTableId(restModule.getModuleId());
		
		changeLog.setChloTimestamp(new Date());
		
		return changeLog;
	}
}
