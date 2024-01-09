package org.babinkuk.controller;

import org.babinkuk.service.CourseService;
import org.babinkuk.service.ReviewService;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorFactory;
import org.babinkuk.validator.ValidatorRole;
import org.babinkuk.validator.ValidatorType;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ReviewVO;
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

import static org.babinkuk.config.Api.REVIEWS;
import static org.babinkuk.config.Api.ROOT;
import static org.babinkuk.config.Api.VALIDATION_ROLE;

import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ROOT + REVIEWS)
public class ReviewController {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	// services
	private ReviewService reviewService;
	
	private CourseService courseService;
	
	@Autowired
	private ValidatorFactory validatorFactory;
	
	@Autowired
	private ObjectMapper mapper;
	
	public ReviewController() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public ReviewController(ReviewService reviewService, CourseService courseService) {
		this.reviewService = reviewService;
		this.courseService = courseService;
	}

	/**
	 * expose GET "/reviews"
	 * get review list
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("")
	public ResponseEntity<Iterable<ReviewVO>> getAllReviews() {

		return ResponseEntity.of(Optional.ofNullable(reviewService.getAllReviews()));
	}
	
	/**
	 * expose GET "/reviews/{reviewId}"
	 * get specific review
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("/{reviewId}")
	public ResponseEntity<ReviewVO> getReview(@PathVariable int reviewId) {
		
		return ResponseEntity.of(Optional.ofNullable(reviewService.findById(reviewId)));
	}
	
	/**
	 * expose POST "/reviews"
	 * add new review
	 * 
	 * @param courseId
	 * @param reviewVO
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/{courseId}")
	public ResponseEntity<ApiResponse> addReview(
			@PathVariable int courseId,
			@RequestBody ReviewVO reviewVO,
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws JsonProcessingException {
		
		// first find course
		CourseVO courseVO = courseService.findById(courseId);
		
		// in case id is passed in json, set to 0
		// this is to force a save of new item ... instead of update
		reviewVO.setId(0);
		
		validatorFactory.getValidator(validationRole).validate(reviewVO, ActionType.CREATE, ValidatorType.REVIEW);
		
		return ResponseEntity.of(Optional.ofNullable(reviewService.saveReview(courseVO, reviewVO)));
	}
	
	/**
	 * expose PUT "/reviews"
	 * update existing review
	 * 
	 * @param reviewVO
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("")
	public ResponseEntity<ApiResponse> updateReview(
			@RequestBody ReviewVO reviewVO,
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws JsonProcessingException {
		
		validatorFactory.getValidator(validationRole).validate(reviewVO, ActionType.UPDATE, ValidatorType.REVIEW);

		return ResponseEntity.of(Optional.ofNullable(reviewService.saveReview(reviewVO)));
	}
	
	/**
	 * expose DELETE "/{reviewId}"
	 * 
	 * @param reviewId
	 * @param validationRole
	 * @return
	 */
	@DeleteMapping("/{reviewId}")
	public ResponseEntity<ApiResponse> deleteReview(
			@PathVariable int reviewId, 
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) {
		
		validatorFactory.getValidator(validationRole).validate(reviewId, ActionType.DELETE, ValidatorType.REVIEW);
		
		return ResponseEntity.of(Optional.ofNullable(reviewService.deleteReview(reviewId)));
	}

}