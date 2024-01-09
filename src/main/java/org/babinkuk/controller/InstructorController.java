package org.babinkuk.controller;

import org.babinkuk.service.CourseService;
import org.babinkuk.service.ImageService;
import org.babinkuk.service.InstructorService;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorFactory;
import org.babinkuk.validator.ValidatorRole;
import org.babinkuk.validator.ValidatorType;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ImageVO;
import org.babinkuk.vo.InstructorVO;
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

import jakarta.validation.Valid;;

@RestController
@RequestMapping(ROOT + INSTRUCTORS)
public class InstructorController {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	// service
	private InstructorService instructorService;
	
	private CourseService courseService;
	
	private ImageService imageService;
	
	private ValidatorFactory validatorFactory;
	
	@Autowired
	private ObjectMapper mapper;
	
	public InstructorController() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public InstructorController(InstructorService instructorService, CourseService courseService, ImageService imageService, ValidatorFactory validatorFactory) {
		this.instructorService = instructorService;
		this.courseService = courseService;
		this.imageService = imageService;
		this.validatorFactory = validatorFactory;
	}

	/**
	 * get instructor list
	 * expose GET "/instructors"
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("")
	public ResponseEntity<Iterable<InstructorVO>> getAllInstructors() {
		
		return ResponseEntity.of(Optional.ofNullable(instructorService.getAllInstructors()));
	}
	
	/**
	 * get instructor
	 * expose GET "/instructors/{instructorId}"
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("/{instructorId}")
	public ResponseEntity<InstructorVO> getInstructor(@PathVariable int instructorId) {
		
		return ResponseEntity.of(Optional.ofNullable(instructorService.findById(instructorId)));
	}
	
	/**
	 * expose POST "/instructors"
	 * add new instructor
	 * 
	 * @param instructorVO
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("")
	public ResponseEntity<ApiResponse> addInstructor(
			@RequestBody InstructorVO instructorVO,
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws JsonProcessingException {
		
		// in case id is passed in json, set to 0
		// this is to force a save of new item ... instead of update
		instructorVO.setId(0);
		
		validatorFactory.getValidator(validationRole).validate(instructorVO, ActionType.CREATE, ValidatorType.INSTRUCTOR);
		
		return ResponseEntity.of(Optional.ofNullable(instructorService.saveInstructor(instructorVO)));
	}
	
	/**
	 * expose PUT "/instructors"
	 * update instructor
	 * 
	 * @param instructorVO
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("")
	public ResponseEntity<ApiResponse> updateInstructor(
			@RequestBody InstructorVO instructorVO,
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws JsonProcessingException {

		validatorFactory.getValidator(validationRole).validate(instructorVO, ActionType.UPDATE, ValidatorType.INSTRUCTOR);

		return ResponseEntity.of(Optional.ofNullable(instructorService.saveInstructor(instructorVO)));
	}
	
	/**
	 * expose DELETE "instructors/{instructorId}"
	 * 
	 * @param instructorId
	 * @param validationRole
	 * @return
	 */
	@DeleteMapping("/{instructorId}")
	public ResponseEntity<ApiResponse> deleteInstructor(
			@PathVariable int instructorId, 
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) {
		
		validatorFactory.getValidator(validationRole).validate(instructorId, ActionType.DELETE, ValidatorType.INSTRUCTOR);
		
		return ResponseEntity.of(Optional.ofNullable(instructorService.deleteInstructor(instructorId)));
	}
	
	/**
	 * enroll instructor on a course
	 * expose PUT "/{instructorId}/enroll/{courseId}"
	 * 
	 * @param instructorId
	 * @param courseId
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("/{instructorId}/enroll/{courseId}")
	public ResponseEntity<ApiResponse> enrollCourse(
			@PathVariable int instructorId,
			@PathVariable int courseId,
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws JsonProcessingException {
		
		// first find course
		CourseVO courseVO = courseService.findById(courseId);
		
		// next find instructor
		InstructorVO instructorVO = instructorService.findById(instructorId);
		
		validatorFactory.getValidator(validationRole).validate(courseVO, ActionType.ENROLL, ValidatorType.INSTRUCTOR);
		
		return ResponseEntity.of(Optional.ofNullable(instructorService.setCourse(instructorVO, courseVO, ActionType.ENROLL)));
	}
	
	/**
	 * withdraw instructor from a course
	 * expose PUT "/{instructorId}/withdraw/{courseId}"
	 * 
	 * @param instructorId
	 * @param courseId
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("/{instructorId}/withdraw/{courseId}")
	public ResponseEntity<ApiResponse> withdrawCourse(
			@PathVariable int instructorId,
			@PathVariable int courseId,
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws JsonProcessingException {
		//log.info("Called CourseController.withdrawInstructor(id={}) for courseId={}", instructorId, courseId);
		
		// first find course
		CourseVO courseVO = courseService.findById(courseId);
		
		// next find instructor
		InstructorVO instructorVO = instructorService.findById(instructorId);

		validatorFactory.getValidator(validationRole).validate(courseVO, ActionType.WITHDRAW, ValidatorType.INSTRUCTOR);

		return ResponseEntity.of(Optional.ofNullable(instructorService.setCourse(instructorVO, courseVO, ActionType.WITHDRAW)));
	}
	
	/**
	 * expose POST "/{instructorId}/upload"
	 * upload new image
	 * 
	 * @param file
	 * @param validationRole
	 * @return
	 * @throws IOException 
	 * @throws JsonProcessingException
	 */
	@PostMapping("/{instructorId}/upload")
	public ResponseEntity<ApiResponse> uploadFile(
			@PathVariable int instructorId,
			@RequestParam(FILE) MultipartFile file,
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws IOException {
		
		String fileName = StringUtils.isEmpty(StringUtils.cleanPath(file.getOriginalFilename())) ? DEFAULT: StringUtils.cleanPath(file.getOriginalFilename());
		
		// first find instructor
		InstructorVO instructorVO = instructorService.findById(instructorId);
		
		// in case id is passed in json, set to 0
		// this is to force a save of new item ... instead of update
		ImageVO imageVO = new ImageVO();
		imageVO.setId(0);
		imageVO.setFileName(fileName);
		imageVO.setData(file.getBytes());
		
		validatorFactory.getValidator(validationRole).validate(imageVO, ActionType.UPLOAD, ValidatorType.INSTRUCTOR);
		
		return ResponseEntity.of(Optional.ofNullable(imageService.saveImage(instructorVO, imageVO)));
	}
}
