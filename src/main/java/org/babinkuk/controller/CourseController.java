package org.babinkuk.controller;

import org.babinkuk.service.CourseService;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorFactory;
import org.babinkuk.validator.ValidatorRole;
import org.babinkuk.validator.ValidatorType;
import org.babinkuk.vo.CourseVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.babinkuk.config.Api.*;

import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ROOT + COURSES)
public class CourseController {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	// services
	private CourseService courseService;
	
	@Autowired
	private ValidatorFactory validatorFactory;
	
	@Autowired
	private ObjectMapper mapper;
	
	public CourseController() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public CourseController(CourseService courseService) {
		this.courseService = courseService;
	}

	/**
	 * expose GET "/courses"
	 * get course list
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("")
	public ResponseEntity<Iterable<CourseVO>> getAllCourses() {

		return ResponseEntity.of(Optional.ofNullable(courseService.getAllCourses()));
	}
	
	/**
	 * expose GET "/courses/{courseId}"
	 * get specific course
	 * 
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("/{courseId}")
	public ResponseEntity<CourseVO> getCourse(@PathVariable int courseId) {
		
		return ResponseEntity.of(Optional.ofNullable(courseService.findById(courseId)));
	}
	
	/**
	 * expose POST "/courses"
	 * add new course
	 * 
	 * @param courseVO
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("")
	public ResponseEntity<ApiResponse> addCourse(
			@RequestBody CourseVO courseVO,
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws JsonProcessingException {
		
		// in case id is passed in json, set to 0
		// this is to force a save of new item ... instead of update
		courseVO.setId(0);
		
		validatorFactory.getValidator(validationRole).validate(courseVO, ActionType.CREATE, ValidatorType.COURSE);
		
		return ResponseEntity.of(Optional.ofNullable(courseService.saveCourse(courseVO)));
	}
	
	/**
	 * expose PUT "/courses/{courseId}"
	 * update course title
	 * 
	 * @param courseId
	 * @param courseTitle
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("/{courseId}")
	public ResponseEntity<ApiResponse> updateCourse(
			@PathVariable int courseId,
			@RequestParam(name=COURSE_TITLE, required = true) String courseTitle,
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws JsonProcessingException {

		// first find course
		CourseVO courseVO = courseService.findById(courseId);
		
		// next set new title
		courseVO.setTitle(courseTitle);
		
		validatorFactory.getValidator(validationRole).validate(courseVO, ActionType.UPDATE, ValidatorType.COURSE);

		return ResponseEntity.of(Optional.ofNullable(courseService.saveCourse(courseVO)));
	}
	
	/**
	 * expose DELETE "/{courseId}"
	 * 
	 * @param courseId
	 * @param validationRole
	 * @return
	 */
	@DeleteMapping("/{courseId}")
	public ResponseEntity<ApiResponse> deleteCourse(
			@PathVariable int courseId, 
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) {
		
		validatorFactory.getValidator(validationRole).validate(courseId, ActionType.DELETE, ValidatorType.COURSE);
		
		return ResponseEntity.of(Optional.ofNullable(courseService.deleteCourse(courseId)));
	}	
}
