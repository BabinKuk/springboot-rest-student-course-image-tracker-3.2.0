package org.babinkuk.validator;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.config.MessagePool;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ObjectValidationException;
import org.babinkuk.vo.UserVO;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ImageVO;
import org.babinkuk.vo.ReviewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * if special validations are required depending on the role
 * implementation class for Instructor role
 * 
 * @author BabinKuk
 *
 */
@Component("validator.ROLE_INSTRUCTOR")
public class ValidatorImplRoleInstructor implements Validator {

private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private ValidatorHelper validatorHelper;
	
	@Override
	public UserVO validate(UserVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException {
		log.info("ROLE_INSTRUCTOR Validating {} {} (vo={})", action, validatorType, vo);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		if (ActionType.DELETE == action) {
			//log.info("delete action disabled");
			
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		
		} else {
			exceptionList.addAll(validatorHelper.validate(vo, action, validatorType));
			
			String message = String.format(MessagePool.getMessage(ValidatorCodes.VALIDATION_FAILED.getMessage()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			
			for (ValidatorException validationException : exceptionList) {
				e.addValidationError(MessagePool.getMessage(validationException.getErrorCode().getMessage()));
			}
			
			if (e.hasErrors()) {
				throw e;
			}
		}
		
		return vo;
	}
	
	@Override
	public void validate(CourseVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException {
		log.info("ROLE_INSTRUCTOR Validating {} {} (vo={})", action, validatorType, vo);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// READ/CREATE/UPDATE actions enabled
		if (ActionType.READ == action || ActionType.CREATE == action || ActionType.UPDATE == action) {
			//log.info("read/create/update actions only");
			exceptionList.addAll(validatorHelper.validate(vo, action, validatorType));
			
			String message = String.format(MessagePool.getMessage(ValidatorCodes.VALIDATION_FAILED.getMessage()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			
			for (ValidatorException validationException : exceptionList) {
				e.addValidationError(MessagePool.getMessage(validationException.getErrorCode().getMessage()));
			}
			
			if (e.hasErrors()) {
				throw e;
			}
		
		// ENROLL/WITHDRAW STUDENT actions enabled
		} else if (ValidatorType.STUDENT == validatorType && (ActionType.ENROLL == action || ActionType.WITHDRAW == action)) {
			// intentionally empty
			log.info("intentionally empty {} {}", action, validatorType);
		
		// DELETE, ENROLL/WITHDRAW INSTRUCTOR actions disabled
		} else {
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		}
	}

	@Override
	public void validate(ReviewVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException {
		log.info("ROLE_INSTRUCTOR Validating {} {} (vo={})", action, validatorType, vo);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// READ/CREATE/UPDATE actions enabled
		if (ActionType.READ == action || ActionType.CREATE == action || ActionType.UPDATE == action) {
			//log.info("read/create/update actions only");
			exceptionList.addAll(validatorHelper.validate(vo, action, validatorType));
			
			String message = String.format(MessagePool.getMessage(ValidatorCodes.VALIDATION_FAILED.getMessage()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			
			for (ValidatorException validationException : exceptionList) {
				e.addValidationError(MessagePool.getMessage(validationException.getErrorCode().getMessage()));
			}
			
			if (e.hasErrors()) {
				throw e;
			}

		} else {
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		}
	}
	
	@Override
	public void validate(ImageVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException {
		log.info("ROLE_INSTRUCTOR Validating {} {} (vo={})", action, validatorType, vo);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// only READ and UPLOAD actions enabled
		if (ActionType.READ == action || ActionType.UPLOAD == action) {
			//log.info("read/upload actions only");
			exceptionList.addAll(validatorHelper.validate(vo, action, validatorType));
			
			String message = String.format(MessagePool.getMessage(ValidatorCodes.VALIDATION_FAILED.getMessage()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			
			for (ValidatorException validationException : exceptionList) {
				e.addValidationError(MessagePool.getMessage(validationException.getErrorCode().getMessage()));
			}
			
			if (e.hasErrors()) {
				throw e;
			}

		} else {
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		}
	}

	@Override
	public void validate(int id, ActionType action, ValidatorType validatorType) throws ObjectNotFoundException {
		log.info("ROLE_INSTRUCTOR Validating {} {} (id={})", action, validatorType, id);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// DELETE action disabled
		if (ActionType.DELETE == action) {
			//log.info("delete action disabled");
			
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		
		} else {
			exceptionList.addAll(validatorHelper.validate(id, validatorType));
			
			String message = String.format(MessagePool.getMessage(ValidatorCodes.VALIDATION_FAILED.getMessage()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			
			for (ValidatorException validationException : exceptionList) {
				e.addValidationError(MessagePool.getMessage(validationException.getErrorCode().getMessage()));
			}
			
			if (e.hasErrors()) {
				throw e;
			}
		}
	}

}
