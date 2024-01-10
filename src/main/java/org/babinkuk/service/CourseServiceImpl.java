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
public class CourseServiceImpl implements CourseService {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private ChangeLogRepository changeLogRepository;
	
	@Autowired
	private CourseMapper courseMapper;
	
	@Autowired
	public CourseServiceImpl(CourseRepository courseRepository, ChangeLogRepository changeLogRepository, CourseMapper courseMapper) {
		this.courseRepository = courseRepository;
		this.changeLogRepository = changeLogRepository;
		this.courseMapper = courseMapper;
	}
	
	public CourseServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public CourseVO findById(int id) throws ObjectNotFoundException {
		
		// create and save ChangeLog
		getChangeLog();
				
		Optional<Course> result = courseRepository.findById(id);
		
		Course course = null;
		CourseVO courseVO = null;
		
		if (result.isPresent()) {
			course = result.get();
			//log.info("course ({})", course);
			
			// mapping
			courseVO = courseMapper.toVO(course);
			//log.info("courseVO ({})", courseVO);
			
			return courseVO;
		} else {
			// not found
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), id);
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
	}
		
	@Override
	public ApiResponse saveCourse(CourseVO courseVO) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(MessagePool.getMessage(COURSE_SAVE_SUCCESS));
		
		Optional<Course> entity = courseRepository.findById(courseVO.getId());
		
		Course course = null;
		
		if (entity.isPresent()) {
			course = entity.get();
			//log.info("courseVO ({})", courseVO);
			//log.info("mapping for update");
			
			// mapping
			//course = courseMapper.toEntity(courseVO, course);
			course.setTitle(courseVO.getTitle());
		} else {
			// course not found
			//log.info("mapping for insert");
			
			// mapping
			course = courseMapper.toEntity(courseVO);
		}
		
		// create and save ChangeLog
		getChangeLog();
		
		courseRepository.save(course);
		
		return response;
	}
	
	@Override
	public ApiResponse deleteCourse(int id) throws ObjectNotFoundException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(MessagePool.getMessage(COURSE_DELETE_SUCCESS));
		
		// retrieve course
		Optional<Course> result = courseRepository.findById(id);
		
		Course course = null;
		
		if (result.isPresent()) {
			course = result.get();
			
			// break association for the instructor
			// if course is deleted DO NOT delete instructor
			Instructor instructor = course.getInstructor();
			
			// break association of all courses for the instructor
			// if instructor is deleted DO NOT delete course
			if (instructor != null) {
				instructor.removeCourse(course);
			}
			
			// get students for the course
			List<Student> students = course.getStudents();
			
			// break association of all students for the course
			// if course is deleted DO NOT delete student
			if (students != null) {
				for (Student student : students) {
					student.removeCourse(course);
				}
			}
		}
		
		courseRepository.deleteById(id);
		
		// create and save ChangeLog
		getChangeLog();
				
		return response;
	}

	@Override
	public Iterable<CourseVO> getAllCourses() {
		// create and save ChangeLog
		getChangeLog();
				
		return courseMapper.toVO(courseRepository.findAll());
	}

	@Override
	public CourseVO findByTitle(String title) {
		
		Optional<Course> result = courseRepository.findByTitle(title);
		
		Course course = null;
		CourseVO courseVO = null;
		
		if (result.isPresent()) {
			course = result.get();
			//log.info("course ({})", course);
			
			// mapping
			courseVO = courseMapper.toVO(course);
			//log.info("courseVO ({})", courseVO);
		} else {
			// not found
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_TITLE_NOT_FOUND.getMessage()), title);
			log.warn(message);
			//throw new ObjectNotFoundException(message);
		}
		
		return courseVO;
	}
	
	private ChangeLog getChangeLog() {
		
		Optional<ChangeLog> chLogEntity = changeLogRepository.findById(1);
		ChangeLog changeLog = null;
		
		if (chLogEntity.isPresent()) {
			changeLog = chLogEntity.get();
			log.info("changeLog {}", changeLog);
		} else {
			// changeLog not found
			log.info("changeLog not found");
		}
		
		return changeLog;
	}
}
