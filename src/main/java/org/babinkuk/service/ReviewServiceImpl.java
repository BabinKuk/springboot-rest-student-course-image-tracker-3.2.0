package org.babinkuk.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.babinkuk.config.MessagePool;
import org.babinkuk.dao.CourseRepository;
import org.babinkuk.dao.ReviewRepository;
import org.babinkuk.entity.Course;
import org.babinkuk.entity.Review;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.mapper.ReviewMapper;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ReviewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static org.babinkuk.config.Api.*;

@Service
public class ReviewServiceImpl implements ReviewService {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	private ReviewMapper reviewMapper;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	public ReviewServiceImpl(ReviewRepository reviewRepository, CourseRepository courseRepository, ReviewMapper reviewMapper) {
		this.reviewRepository = reviewRepository;
		this.courseRepository = courseRepository;
		this.reviewMapper = reviewMapper;
	}
	
	@Override
	public ReviewVO findById(int id) throws ObjectNotFoundException {
		
		Optional<Review> result = reviewRepository.findById(id);
		
		Review review = null;
		ReviewVO reviewVO = null;
		
		if (result.isPresent()) {
			review = result.get();
			//log.info("review ({})", review);
			
			// mapping
			reviewVO = reviewMapper.toVO(review);
			//log.info("reviewVO ({})", reviewVO);
			
			return reviewVO;
		} else {
			// not found
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_REVIEW_ID_NOT_FOUND.getMessage()), id);
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
	}
	
	@Override
	public ApiResponse saveReview(ReviewVO reviewVO) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(MessagePool.getMessage(REVIEW_SAVE_SUCCESS));
		
		Optional<Review> entity = reviewRepository.findById(reviewVO.getId());
		
		Review review = null;
		
		if (entity.isPresent()) {
			review = entity.get();
			//log.info("review ({})", entity);
			//log.info("mapping for update");
			
			// mapping
			review = reviewMapper.toEntity(reviewVO, review);
		} else {
			// review not found
			//log.info("mapping for insert");
			
			// mapping
			review = reviewMapper.toEntity(reviewVO);
		}
		
		//log.info("review ({})", review);

		reviewRepository.save(review);
		
		return response;
	}
	
	@Override
	public ApiResponse saveReview(CourseVO courseVO, ReviewVO reviewVO) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(MessagePool.getMessage(REVIEW_SAVE_SUCCESS));
		
		Optional<Course> entity = courseRepository.findById(courseVO.getId());
		
		Course course = null;
		
		if (entity.isPresent()) {
			course = entity.get();
			//log.info("courseVO ({})", courseVO);
			
			// add review
			course.addReview(reviewMapper.toEntity(reviewVO));
		} else {
			// not found
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), courseVO.getId());
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
		
		courseRepository.save(course);
		
		return response;
	}
	
	@Override
	public ApiResponse deleteReview(int id) throws ObjectNotFoundException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(MessagePool.getMessage(REVIEW_DELETE_SUCCESS));
		
		reviewRepository.deleteById(id);
		
		return response;
	}

	@Override
	public Iterable<ReviewVO> getAllReviews() {
		return reviewMapper.toVO(reviewRepository.findAll());
	}
}
