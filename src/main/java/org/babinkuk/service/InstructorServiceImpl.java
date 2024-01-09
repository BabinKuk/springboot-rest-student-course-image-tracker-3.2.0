package org.babinkuk.service;

import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.babinkuk.config.MessagePool;
import org.babinkuk.dao.CourseRepository;
import org.babinkuk.dao.InstructorRepository;
import org.babinkuk.entity.Course;
import org.babinkuk.entity.Instructor;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.mapper.InstructorMapper;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.InstructorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import static org.babinkuk.config.Api.*;

@Service
public class InstructorServiceImpl implements InstructorService {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private InstructorRepository instructorRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	private InstructorMapper instructorMapper;
	
	@Autowired
	public InstructorServiceImpl(InstructorRepository instructorRepository, CourseRepository courseRepository, InstructorMapper instructorMapper) {
		this.instructorRepository = instructorRepository;
		this.courseRepository = courseRepository;
		this.instructorMapper = instructorMapper;
	}
	
	public InstructorServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	@Transactional
	public InstructorVO findById(int id) throws ObjectNotFoundException {
		
		Optional<Instructor> result = instructorRepository.findById(id);
		
		Instructor instructor = null;
		InstructorVO instructorVO = null;
		
		if (result.isPresent()) {
			instructor = result.get();
			//log.info("instructor ({})", instructor);
			
			// mapping
			instructorVO = instructorMapper.toVO(instructor);
			//log.info("instructorVO ({})", instructorVO);
			
			return instructorVO;
		} else {
			// not found
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), id);
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
	}
	
	@Override
	@Transactional
	public InstructorVO findByEmail(String email) {
		
		InstructorVO instructorVO = null;
		
		Optional<Instructor> result = instructorRepository.findByEmail(email);
		
		Instructor instructor = null;
		
		if (result.isPresent()) {
			instructor = result.get();
			
			// mapping
			instructorVO = instructorMapper.toVO(instructor);
			//log.info("instructorVO ({})", instructorVO);
		} else {
			// not found
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_EMAIL_NOT_FOUND.getMessage()), email);
			log.warn(message);
			//throw new ObjectNotFoundException(message);
		}

		return instructorVO;
	}
	
	@Override
	@Transactional
	public ApiResponse saveInstructor(InstructorVO instructorVO) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(MessagePool.getMessage(INSTRUCTOR_SAVE_SUCCESS));
		
		Optional<Instructor> entity = instructorRepository.findById(instructorVO.getId());
		
		Instructor instructor = null;
		
		if (entity.isPresent()) {
			instructor = entity.get();
			//log.info("instructor ({})", entity);
			//log.info("mapping for update");
			
			// mapping
			instructor = instructorMapper.toEntity(instructorVO, instructor);
		} else {
			// instructor not found
			//log.info("mapping for insert");
			
			// mapping
			instructor = instructorMapper.toEntity(instructorVO);
		}
		
		instructorRepository.save(instructor);
		
		return response;
	}
	
	@Override
	@Transactional
	public ApiResponse deleteInstructor(int id) throws ObjectNotFoundException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(MessagePool.getMessage(INSTRUCTOR_DELETE_SUCCESS));
		
		// retrieve instructor
		Optional<Instructor> result = instructorRepository.findById(id);
		
		Instructor instructor = null;
		
		if (result.isPresent()) {
			
			instructor = result.get();
			
			// get courses for the instructor
			List<Course> courses = instructor.getCourses();
			log.info("courses " + courses);
			
			// break association of all courses for the instructor
			// if instructor is deleted DO NOT delete course
			if (courses != null) {
				for (Course course : courses) {
					course.setInstructor(null);
				}
			}
		}
		
		instructorRepository.deleteById(id);
		
		return response;
	}

	@Override
	@Transactional
	public Iterable<InstructorVO> getAllInstructors() {
		return instructorMapper.toVO(instructorRepository.findAll());
	}

	@Override
	@Transactional
	public ApiResponse setCourse(InstructorVO instructorVO, CourseVO courseVO, ActionType action) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(MessagePool.getMessage(INSTRUCTOR_SAVE_SUCCESS));
		
		Optional<Instructor> instructorEntity = instructorRepository.findById(instructorVO.getId());
		
		Instructor instructor = null;
		
		if (instructorEntity.isPresent()) {
			instructor = instructorEntity.get();
			//log.info("instructor ({})", entity);
			
			// mapping
			//instructor = instructorMapper.toEntity(instructorVO, instructor);
		} else {
			// not found
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), instructorVO.getId());
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
		
		Optional<Course> entity = courseRepository.findById(courseVO.getId());
		
		Course course = null;
		
		if (entity.isPresent()) {
			course = entity.get();
			//log.info("courseVO ({})", courseVO);
		} else {
			// not found
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), courseVO.getId());
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
		
		if (action.equals(ActionType.ENROLL)) {
			instructor.addCourse(course);
		}
		
		if (action.equals(ActionType.WITHDRAW)) {
			instructor.removeCourse(course);
		}
		
		instructorRepository.save(instructor);
		
		return response;
	}
}
