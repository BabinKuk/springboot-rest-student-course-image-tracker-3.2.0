package org.babinkuk.service;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.babinkuk.config.MessagePool;
import org.babinkuk.config.Api.RestModule;
import org.babinkuk.dao.CourseRepository;
import org.babinkuk.dao.StudentRepository;
import org.babinkuk.entity.ChangeLog;
import org.babinkuk.entity.Course;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.Student;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.mapper.StudentMapper;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.InstructorVO;
import org.babinkuk.vo.StudentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static org.babinkuk.config.Api.*;

@Service
public class StudentServiceImpl implements StudentService {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private ChangeLogService changeLogService;

	private StudentMapper studentMapper;
	
	@Autowired
	public StudentServiceImpl(StudentRepository studentRepository, CourseRepository courseRepository, StudentMapper studentMapper, ChangeLogService changeLogService) {
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository;
		this.studentMapper = studentMapper;
		this.changeLogService = changeLogService;
	}
	
	public StudentServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	@Transactional
	public StudentVO findById(int id) throws ObjectNotFoundException {
		
		Optional<Student> result = studentRepository.findById(id);
		
		Student student = null;
		StudentVO studentVO = null;
		
		if (result.isPresent()) {
			student = result.get();
			//log.info("student ({})", student);
			
			// mapping
			studentVO = studentMapper.toVO(student);
			//log.info("studentVO ({})", studentVO);
			
			return studentVO;
		} else {
			// not found
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), id);
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
	}
	
	@Override
	@Transactional
	public StudentVO findByEmail(String email) {
		
		Optional<Student> result = studentRepository.findByEmail(email);
		
		Student student = null;
		StudentVO studentVO = null;
		
		if (result.isPresent()) {
			student = result.get();
			
			// mapping
			studentVO = studentMapper.toVO(student);
			//log.info("studentVO ({})", studentVO);
		} else {
			// not found
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_EMAIL_NOT_FOUND.getMessage()), email);
			log.warn(message);
			//throw new ObjectNotFoundException(message);
		}

		return studentVO;
	}
		
	@Override
	@Transactional
	public ApiResponse saveStudent(StudentVO studentVO) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(MessagePool.getMessage(STUDENT_SAVE_SUCCESS));
		
		Optional<Student> entity = studentRepository.findById(studentVO.getId());
		
		Student student = null;
		StudentVO originalStudentVO = null;
		
		if (entity.isPresent()) {
			student = entity.get();
			//log.info("mapping for update");
			
			originalStudentVO = studentMapper.toVO(student);
			
			// mapping
			student = studentMapper.toEntity(studentVO, student);
		} else {
			// instructor not found
			//log.info("mapping for insert");
			
			// mapping
			student = studentMapper.toEntity(studentVO);
		}
		
		studentRepository.save(student);
		
		// create ChangeLog
		final ChangeLog changeLog = ChangeLogServiceImpl.createChangeLog(RestModule.STUDENT);
		// save ChangeLog
		changeLogService.saveChangeLog(changeLog, originalStudentVO, studentVO);
		
		return response;
	}
	
	@Override
	@Transactional
	public ApiResponse deleteStudent(int id) throws ObjectNotFoundException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(MessagePool.getMessage(STUDENT_DELETE_SUCCESS));
		
		// retrieve student
		Optional<Student> result = studentRepository.findById(id);
		
		Student student = null;
		StudentVO originalStudentVO = null;
		
		if (result.isPresent()) {
			student = result.get();
			
			originalStudentVO = studentMapper.toVO(student);
			
			// get courses for the student
			List<Course> courses = student.getCourses();
			
			// break association of all courses for the student
			// if student is deleted DO NOT delete course
			if (courses != null) {
				for (Course course : courses) {
					course.removeStudent(student);
				}
			}
		}
		
		studentRepository.deleteById(id);
		
		// create ChangeLog
		final ChangeLog changeLog = ChangeLogServiceImpl.createChangeLog(RestModule.STUDENT);
		// save ChangeLog
		changeLogService.saveChangeLog(changeLog, originalStudentVO, null);

		return response;
	}

	@Override
	@Transactional
	public Iterable<StudentVO> getAllStudents() {
		return studentMapper.toVO(studentRepository.findAll());
	}

	@Override
	@Transactional
	public ApiResponse setCourse(StudentVO studentVO, CourseVO courseVO, ActionType action) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(MessagePool.getMessage(STUDENT_SAVE_SUCCESS));
		
		Optional<Student> result = studentRepository.findById(studentVO.getId());
		
		Student student = null;
		
		if (result.isPresent()) {
			student = result.get();
			//log.info("student ({})", student);
		} else {
			// not found
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), studentVO.getId());
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
			student.addCourse(course);
		}
		
		if (action.equals(ActionType.WITHDRAW)) {
			student.removeCourse(course);
		}
		
		studentRepository.save(student);
		
		StudentVO originalStudentVO = studentVO;
		StudentVO currentStudentVO = studentMapper.toVO(student);
		
		// create ChangeLog
		final ChangeLog changeLog = ChangeLogServiceImpl.createChangeLog(RestModule.STUDENT);
		// save ChangeLog
		changeLogService.saveChangeLog(changeLog, originalStudentVO, currentStudentVO);
		
		return response;
	}
}