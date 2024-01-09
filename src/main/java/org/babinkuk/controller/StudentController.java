package org.babinkuk.controller;

import org.babinkuk.service.CourseService;
import org.babinkuk.service.ImageService;
import org.babinkuk.service.StudentService;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorFactory;
import org.babinkuk.validator.ValidatorRole;
import org.babinkuk.validator.ValidatorType;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ImageVO;
import org.babinkuk.vo.InstructorVO;
import org.babinkuk.vo.StudentVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.babinkuk.config.Api.*;

import java.io.IOException;
import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ROOT + STUDENTS)
public class StudentController {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	// service
	private StudentService studentService;
	
	private CourseService courseService;
	
	private ImageService imageService;
	
	@Autowired
	private ValidatorFactory validatorFactory;
	
	@Autowired
	private ObjectMapper mapper;
	
	public StudentController() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public StudentController(StudentService studentService, CourseService courseService, ImageService imageService) {
		this.studentService = studentService;
		this.courseService = courseService;
		this.imageService = imageService;
	}

	/**
	 * expose GET "/students"
	 * get student list
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("")
	public ResponseEntity<Iterable<StudentVO>> getAllStudents() {

		return ResponseEntity.of(Optional.ofNullable(studentService.getAllStudents()));
	}
	
	/**
	 * expose GET "/students/{studentId}"
	 * get student
	 * 			
	 * @param studentId
	 * @return
	 */
	@GetMapping("/{studentId}")
	public ResponseEntity<StudentVO> getStudent(@PathVariable int studentId) {
		
		return ResponseEntity.of(Optional.ofNullable(studentService.findById(studentId)));
	}
	
	/**
	 * expose POST "/students"
	 * add new student
	 * 
	 * @param studentVO
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("")
	public ResponseEntity<ApiResponse> addStudent(
			@RequestBody StudentVO studentVO,
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws JsonProcessingException {
		
		// in case id is passed in json, set to 0
		// this is to force a save of new item ... instead of update
		studentVO.setId(0);
		
		validatorFactory.getValidator(validationRole).validate(studentVO, ActionType.CREATE, ValidatorType.STUDENT);
		
		return ResponseEntity.of(Optional.ofNullable(studentService.saveStudent(studentVO)));
	}
	
	/**
	 * expose PUT "/students"
	 * update student
	 * 
	 * @param studentVO
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("")
	public ResponseEntity<ApiResponse> updateStudent(
			@RequestBody StudentVO studentVO,
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws JsonProcessingException {

		validatorFactory.getValidator(validationRole).validate(studentVO, ActionType.UPDATE, ValidatorType.STUDENT);

		return ResponseEntity.of(Optional.ofNullable(studentService.saveStudent(studentVO)));
	}
	
	/**
	 * expose DELETE "/{studentId}"
	 * 
	 * @param studentId
	 * @param validationRole
	 * @return
	 */
	@DeleteMapping("/{studentId}")
	public ResponseEntity<ApiResponse> deleteStudent(
			@PathVariable int studentId, 
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) {
		
		validatorFactory.getValidator(validationRole).validate(studentId, ActionType.DELETE, ValidatorType.STUDENT);
		
		return ResponseEntity.of(Optional.ofNullable(studentService.deleteStudent(studentId)));
	}
	
	/**
	 * enroll student on a course
	 * expose PUT "/{studentId}/enroll/{courseId}"
	 * 
	 * @param studentId
	 * @param courseId
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("/{studentId}/enroll/{courseId}")
	public ResponseEntity<ApiResponse> enrollStudent(
			@PathVariable int studentId,
			@PathVariable int courseId,
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws JsonProcessingException {
		
		// first find course
		CourseVO courseVO = courseService.findById(courseId);
		
		// next find student
		StudentVO studentVO = studentService.findById(studentId);
		
		validatorFactory.getValidator(validationRole).validate(courseVO, ActionType.ENROLL, ValidatorType.STUDENT);
		
		return ResponseEntity.of(Optional.ofNullable(studentService.setCourse(studentVO, courseVO, ActionType.ENROLL)));
	}

	/**
	 * withdraw student from a course
	 * expose PUT "/{studentId}/withdraw/{courseId}"
	 * 
	 * @param studentId
	 * @param courseId
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("/{studentId}/withdraw/{courseId}")
	public ResponseEntity<ApiResponse> withdrawStudent(
			@PathVariable int studentId,
			@PathVariable int courseId,
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws JsonProcessingException {
		
		// first find course
		CourseVO courseVO = courseService.findById(courseId);
		
		// next find student
		StudentVO studentVO = studentService.findById(studentId);
		
		validatorFactory.getValidator(validationRole).validate(courseVO, ActionType.WITHDRAW, ValidatorType.STUDENT);
		
		return ResponseEntity.of(Optional.ofNullable(studentService.setCourse(studentVO, courseVO, ActionType.WITHDRAW)));
	}
	
	/**
	 * expose POST "/{studentId}/upload"
	 * upload new image
	 * 
	 * @param file
	 * @param validationRole
	 * @return
	 * @throws IOException 
	 * @throws JsonProcessingException
	 */
	@PostMapping("/{studentId}/upload")
	public ResponseEntity<ApiResponse> uploadFile(
			@PathVariable int studentId,
			@RequestParam(FILE) MultipartFile file,
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws IOException {
		
		String fileName = StringUtils.isEmpty(StringUtils.cleanPath(file.getOriginalFilename())) ? DEFAULT: StringUtils.cleanPath(file.getOriginalFilename());
		
		// first find student
		StudentVO studentVO = studentService.findById(studentId);
		
		// in case id is passed in json, set to 0
		// this is to force a save of new item ... instead of update
		ImageVO imageVO = new ImageVO();
		imageVO.setId(0);
		imageVO.setFileName(fileName);
		imageVO.setData(file.getBytes());
		
		validatorFactory.getValidator(validationRole).validate(imageVO, ActionType.UPLOAD, ValidatorType.STUDENT);
		
		return ResponseEntity.of(Optional.ofNullable(imageService.saveImage(studentVO, imageVO)));
	}
}