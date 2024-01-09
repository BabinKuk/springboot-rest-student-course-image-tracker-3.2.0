package org.babinkuk.validator;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.entity.Status;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.service.CourseService;
import org.babinkuk.service.ImageService;
import org.babinkuk.service.InstructorService;
import org.babinkuk.service.ReviewService;
import org.babinkuk.service.StudentService;
import org.babinkuk.vo.UserVO;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ImageVO;
import org.babinkuk.vo.InstructorVO;
import org.babinkuk.vo.ReviewVO;
import org.babinkuk.vo.StudentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * implementation class for different field validations
 *  
 * @author Nikola
 *
 */
/**
 * @author Nikola
 *
 */
@Component
public class BusinessValidator {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private InstructorService instructorService;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private ImageService imageService;
	
	/**
	 * @param title
	 * @throws ValidationException
	 */
	public void validateTitle(CourseVO vo) throws ValidatorException {
		validateStringIsBlank(vo.getTitle(), ValidatorCodes.ERROR_CODE_TITLE_EMPTY);
		titleExists(vo);
	}
	
	/**
	 * @param review
	 * @throws ValidationException
	 */
	public void validateReview(String review) throws ValidatorException {
		validateStringIsBlank(review, ValidatorCodes.ERROR_CODE_REVIEW_EMPTY);
	}
	
	/**
	 * @param vo
	 * @throws ValidatorException
	 */
	public void validateImage(ImageVO vo) throws ValidatorException {
		validateStringIsBlank(vo.getFileName(), ValidatorCodes.ERROR_CODE_FILE_NAME_EMPTY);
		validateDataIsEmpty(vo.getData(), ValidatorCodes.ERROR_CODE_FILE_DATA_EMPTY);
	}

	/**
	 * @param name
	 * @throws ValidationException
	 */
	public void validateFirstName(String name) throws ValidatorException {
		validateStringIsBlank(name, ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY);
	}
	
	/**
	 * @param name
	 * @throws ValidationException
	 */
	public void validateLastName(String name) throws ValidatorException {
		validateStringIsBlank(name, ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY);
	}
	
	/**
	 * @param email
	 * @throws ValidationException
	 */
	public void validateEmail(UserVO vo) throws ValidatorException {
		validateStringIsBlank(vo.getEmail(), ValidatorCodes.ERROR_CODE_EMAIL_EMPTY);
		validateEmailAddress(vo.getEmail(), ValidatorCodes.ERROR_CODE_EMAIL_INVALID);
		emailExists(vo);
	}
	
	/**
	 * @param status
	 * @throws ValidationException
	 */
	public void validateStatus(UserVO vo) throws ValidatorException {
		validateStatusIsNull(vo.getStatus(), ValidatorCodes.ERROR_CODE_STATUS_INVALID);
	}
	
	/**
	 * @param email
	 * @param errorCode
	 * @throws ValidatorException
	 */
	public void validateEmailAddress(String email, ValidatorCodes errorCode) throws ValidatorException {
		if (!validateEmailFormat(email)) {
			throw new ValidatorException(errorCode);
		}
	}

	/**
	 * validate email format
	 * @param email
	 * @return
	 */
	private boolean validateEmailFormat(String email) {
		if (StringUtils.isNotBlank(email)) {
			email = StringUtils.upperCase(StringUtils.replace(email, " ", ""));
			for (String pattern : Arrays.asList("[A-Z0-9._%+-]+@[A-Z0-9.-]+")) {
				if (Pattern.matches(pattern, email)) {
					return true;
				}
			}
			return false;
		} else {
			// if empty return true
			return true;
		}
	}

	/**
	 * validate if email already exist 
	 * must be unique (call repository findByEmail)
	 * 
	 * @param vo
	 * @param isInsert
	 * @return
	 * @throws ValidatorException
	 */
	public void emailExists(UserVO vo) throws ValidatorException {
		//log.info("email " + vo.toString());
		UserVO dbVO = null;
		if (vo instanceof InstructorVO) {
			dbVO = instructorService.findByEmail(vo.getEmail());
		} else if (vo instanceof StudentVO) {
			dbVO = studentService.findByEmail(vo.getEmail());
		}
		 
		if (dbVO == null) {
			// email not found
			// that's ok
			log.info("email not found");
		} else {
			log.info("email found");
			if (dbVO.getId() == vo.getId()) {
				// same employee, email has not changed
				log.info("belongs to same instructor/student, email has not changed");
			} else {
				// another employee with same email already exists in db
				log.error(ValidatorCodes.ERROR_CODE_EMAIL_ALREADY_EXIST.getMessage());
				throw new ValidatorException(ValidatorCodes.ERROR_CODE_EMAIL_ALREADY_EXIST);
			}
		}
	}
	
	/**
	 * validate if course title already exist 
	 * must be unique (call repository findByTitle)
	 * 
	 * @param vo
	 * @param isInsert
	 * @return
	 * @throws ValidatorException
	 */
	public void titleExists(CourseVO vo) throws ValidatorException {
		//log.info("course " + vo.toString());
		CourseVO dbVO = null;
		
		dbVO = courseService.findByTitle(vo.getTitle());
		 
		if (dbVO == null) {
			// title not found
			// that's ok
			//log.info("title not found");
		} else {
			//log.info("title found");
			if (dbVO.getId() == vo.getId()) {
				// same course, title has not changed
				log.info("belongs to same course, title has not changed");
			} else {
				// another course with same title already exists in db
				log.error(ValidatorCodes.ERROR_CODE_TITLE_ALREADY_EXIST.getMessage());
				throw new ValidatorException(ValidatorCodes.ERROR_CODE_TITLE_ALREADY_EXIST);
			}
		}
	}
	
	/**
	 * validate if object already exist
	 * @param vo
	 * @param isInsert
	 * @return
	 * @throws ValidatorException
	 */
	public void objectExists(Object vo, ValidatorType validatorType) throws ValidatorException {
		
		Object result;
		if (vo instanceof InstructorVO) {
			//log.info("validate instructor on update");
			result = objectExists(((UserVO) vo).getId(), validatorType);
			
			if (result != null) {
				// id found
				log.info("instructor id found");
			} else {
				// id not found
				//log.error("result.notPresent");
				throw new ValidatorException(ValidatorCodes.ERROR_CODE_INSTRUCTOR_INVALID);
			}
		} else if (vo instanceof StudentVO) {
			//log.info("validate student on update");
			result = objectExists(((UserVO) vo).getId(), validatorType);
			
			if (result != null) {
				// id found
				log.info("student id found");
			} else {
				// id not found
				//log.error("result.notPresent");
				throw new ValidatorException(ValidatorCodes.ERROR_CODE_STUDENT_INVALID);
			}
		} else if (vo instanceof CourseVO) {
			//log.info("validate course on update");
			result = objectExists(((CourseVO) vo).getId(), validatorType);
			
			if (result != null) {
				// id found
				log.info("course id found");
			} else {
				// id not found
				//log.error("result.notPresent");
				throw new ValidatorException(ValidatorCodes.ERROR_CODE_COURSE_INVALID);
			}
		} else if (vo instanceof ReviewVO) {
			//log.info("validate review on update");
			result = objectExists(((ReviewVO) vo).getId(), validatorType);
			
			if (result != null) {
				// id found
				log.info("review id found");
			} else {
				// id not found
				//log.error("result.notPresent");
				throw new ValidatorException(ValidatorCodes.ERROR_CODE_REVIEW_INVALID);
			}
		} else if (vo instanceof ImageVO) {
			//log.info("validate review on update");
			result = objectExists(((ImageVO) vo).getId(), validatorType);
			
			if (result != null) {
				// id found
				log.info("image id found");
			} else {
				// id not found
				//log.error("result.notPresent");
				throw new ValidatorException(ValidatorCodes.ERROR_CODE_IMAGE_INVALID);
			}
		}
	}

	/**
	 * @param str
	 * @param errorCode
	 * @throws ValidatorException
	 */
	private void validateStringIsBlank(String str, ValidatorCodes errorCode) throws ValidatorException {
		if (StringUtils.isBlank(str)) {
			throw new ValidatorException(errorCode);
		}
	}
	
	/**
	 * @param data
	 * @param errorCode
	 * @throws ValidatorException
	 */
	private void validateDataIsEmpty(byte[] data, ValidatorCodes errorCode) throws ValidatorException {
		byte[] empty = new byte[data.length];
		
		if (Arrays.equals(data, empty)) {
			throw new ValidatorException(errorCode);
		}
	}
	
	/**
	 * @param status
	 * @param errorCode
	 * @throws ValidatorException
	 */
	private void validateStatusIsNull(Status status, ValidatorCodes errorCode) throws ValidatorException {
		if (status == null) {
			throw new ValidatorException(errorCode);
		}
	}
	
	/**
	 * @param id
	 * @param validatorType
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public Object objectExists(int id, ValidatorType validatorType) throws ObjectNotFoundException {
		
		Object dbVO = null;
		
		switch (validatorType) {
		case STUDENT:
			dbVO = studentService.findById(id);
			break;
		case INSTRUCTOR:
			dbVO = instructorService.findById(id);
			break;
		case COURSE:
			dbVO = courseService.findById(id);
			break;
		case REVIEW:
			dbVO = reviewService.findById(id);
			break;
		case IMAGE:
			dbVO = imageService.findById(id);
			break;
		default:
			break;
		} 
		
		return dbVO;
	}

}
