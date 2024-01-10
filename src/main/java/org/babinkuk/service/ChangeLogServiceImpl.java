package org.babinkuk.service;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.babinkuk.config.MessagePool;
import org.babinkuk.dao.ChangeLogRepository;
import org.babinkuk.dao.CourseRepository;
import org.babinkuk.entity.ChangeLog;
import org.babinkuk.entity.Course;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.Student;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.mapper.CourseMapper;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.CourseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static org.babinkuk.config.Api.*;

@Service
public class ChangeLogServiceImpl implements ChangeLogService {
	
	private final Logger log = LogManager.getLogger(getClass());
	
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
	public ChangeLog findById(int id) throws ObjectNotFoundException {
		
		Optional<ChangeLog> result = changeLogRepository.findById(id);
		
		ChangeLog chlo = null;
		
		if (result.isPresent()) {
			chlo = result.get();
			log.info("chlo ({})", chlo);
					
			return chlo;
		} else {
			// not found
			String message = String.format(MessagePool.getMessage("Chlo with id=%s not found"), id);
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
	}

	@Override
	public void saveChangeLog(ChangeLog changeLog) throws ObjectException {
		// TODO Auto-generated method stub
		changeLogRepository.save(changeLog);
	}

	@Override
	public void deleteChangeLog(int id) throws ObjectNotFoundException {
		// TODO Auto-generated method stub
		changeLogRepository.deleteById(id);
	}

}
