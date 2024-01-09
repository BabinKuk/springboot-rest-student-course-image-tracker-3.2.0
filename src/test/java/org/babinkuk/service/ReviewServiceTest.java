package org.babinkuk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.MessagePool;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ReviewVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.babinkuk.utils.ApplicationTestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@AutoConfigureMockMvc
public class ReviewServiceTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(ReviewServiceTest.class);
	
	@Test
	void getAllReviews() {
		
		// get all reviews
		Iterable<ReviewVO> reviews = reviewService.getAllReviews();
		
		// assert
		if (reviews instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) reviews).size(), "reviews size not 1");
		}
		
		List<ReviewVO> reviewList = new ArrayList<ReviewVO>();
		reviews.forEach(reviewList::add);
		
		assertTrue(reviewList.stream().anyMatch(rev ->
			rev.getComment().equals(REVIEW) && rev.getId() == 1
		));
	}
	
	@Test
	void getReview() {
		
		// get review id=1
		ReviewVO reviewVO = reviewService.findById(1);
		
		assertNotNull(reviewVO,"reviewVO null");
		assertEquals(1, reviewVO.getId());
		assertNotNull(reviewVO.getComment(),"getComment() null");
		assertEquals(REVIEW, reviewVO.getComment(),"getComment() failure");
		
		assertNotEquals("test review ", reviewVO.getComment(),"getComment() intentional failure");
		
		// assert not existing review
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			reviewService.findById(2);
		});
		
		String expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_REVIEW_ID_NOT_FOUND.getMessage()), 2);
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void addReview() {
		
		// first find course
		CourseVO courseVO = courseService.findById(1);
		
		// create review
		// set id 0: this is to force a save of new item ... instead of update
		ReviewVO reviewVO = new ReviewVO(REVIEW_NEW);
		reviewVO.setId(0);
		
		// add to course
		reviewService.saveReview(courseVO, reviewVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// get all reviews
		Iterable<ReviewVO> reviews = reviewService.getAllReviews();
		//log.info(reviews);
		
		// assert
		assertNotNull(reviews,"reviews null");
		
		if (reviews instanceof Collection) {
			assertEquals(2, ((Collection<?>) reviews).size(), "reviews size not 2");
		}
		
		List<ReviewVO> reviewList = new ArrayList<ReviewVO>();
		reviews.forEach(reviewList::add);
		
		assertTrue(reviewList.stream().anyMatch(rev ->
			rev.getComment().equals(REVIEW) && rev.getId() == 1
		));
		assertTrue(reviewList.stream().anyMatch(rev ->
			rev.getComment().equals(REVIEW_NEW)
		));
	}
	
	@Test
	void updateReview() {
		
		// create review
		// set id=1: this is to force an update of existing item
		ReviewVO review = new ReviewVO(REVIEW_UPDATE);
		review.setId(1);
		
		reviewService.saveReview(review);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		ReviewVO savedReview = reviewService.findById(1);
		
		// assert
		assertNotNull(savedReview,"savedReview null");
		assertEquals(REVIEW_UPDATE, savedReview.getComment(),"savedReview.getComment() failure");
		assertEquals(1, savedReview.getId(),"savedReview.getId() failure");
	}
	
	@Test
	void deleteReview() {
		
		// first get review
		ReviewVO reviewVO = reviewService.findById(1);
		
		// assert
		assertNotNull(reviewVO, "return null");
		assertEquals(1, reviewVO.getId());
		assertNotNull(reviewVO.getComment(),"getComment() null");
		
		// delete review
		reviewService.deleteReview(1);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// check other cascading entities
		// get course with id=1
		CourseVO courseVO = courseService.findById(1);
		
		// assert
		// course must be unchanged except reviews (size=0)
		assertNotNull(courseVO, "courseVO null");
		assertEquals(1, courseVO.getId(), "course.getId()");
		assertEquals(COURSE, courseVO.getTitle(), "course.getTitle()");
		assertEquals(0, courseVO.getReviewsVO().size(), "course.getReviews().size()");
		assertEquals(INSTRUCTOR_FIRSTNAME, courseVO.getInstructorVO().getFirstName(), "course.getInstructor().getFirstName()");
		assertEquals(1, courseVO.getStudentsVO().size(), "course.getStudents().size()");
		assertTrue(courseVO.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
		));
		
		// assert not existing review
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			reviewService.findById(1);
		});
				
		String expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_REVIEW_ID_NOT_FOUND.getMessage()), 1);
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));
	}
}