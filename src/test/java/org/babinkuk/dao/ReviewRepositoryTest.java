package org.babinkuk.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.entity.Course;
import org.babinkuk.entity.Review;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;
import java.util.*;

@Transactional
@AutoConfigureMockMvc
public class ReviewRepositoryTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(ReviewRepositoryTest.class);
	
	@Test
	void getAllReviews() {
		
		// get all reviews
		Iterable<Review> reviews = reviewRepository.findAll();
		
		// assert
		assertNotNull(reviews,"reviews null");
		
		if (reviews instanceof Collection) {
			assertEquals(1, ((Collection<?>) reviews).size(), "reviews size not 1");
		}
		
		List<Review> reviewList = new ArrayList<Review>();
		reviews.forEach(reviewList::add);

		assertTrue(reviewList.stream().anyMatch(review ->
			review.getComment().equals(REVIEW) && review.getId() == 1
		));
	}
	
	@Test
	void getReviewById() {
		
		// get review
		Review review = getReview();
		
		assertTrue(review != null, "review null");
		
		// get review id
		Optional<Review> dbReview = reviewRepository.findById(review.getId());
		
		// assert
		assertTrue(dbReview.isPresent());
		assertNotNull(dbReview,"review null");
		assertEquals(REVIEW, dbReview.get().getComment(),"getComment() failure");
		assertEquals(review.getId(), dbReview.get().getId(),"getId() failure");
		
		// get non-existing review id=222
		dbReview = reviewRepository.findById(222);
		
		// assert
		assertFalse(dbReview.isPresent());
	}

	@Test
	void updateReview() {
		
		// get review
		Review review = getReview();
		
		assertTrue(review != null, "review null");
		
		// update review
		review.setComment(REVIEW_UPDATE);
		
		Review savedReview = reviewRepository.save(review);
		
		// assert
		assertNotNull(savedReview,"savedReview null");
		assertEquals(REVIEW_UPDATE, savedReview.getComment(),"savedReview.getComment() failure");
		assertEquals(review.getId(), savedReview.getId(),"savedReview.getId() failure");
	}
	
	@Test
	void addReview() {
		
		// create review
		// set id=0: this is to force a save of new item
		Review review = new Review(REVIEW_NEW);
		review.setId(0);
		
		// get course
		Optional<Course> course = courseRepository.findByTitle(COURSE);
		
		// assert
		assertTrue(course.isPresent());
		//assertEquals(1, course.get().getId(), "course.get().getId()");
		assertEquals(COURSE, course.get().getTitle(), "course.get().getTitle()");
		assertEquals(1, course.get().getReviews().size(), "course.get().getReviews().size()");
		
		// add review to course
		course.get().addReview(review);
		
		// save course
		courseRepository.save(course.get());
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// get all reviews
		Iterable<Review> reviews = reviewRepository.findAll();
		
		// assert
		assertNotNull(reviews,"reviews null");
		
		if (reviews instanceof Collection) {
			assertEquals(2, ((Collection<?>) reviews).size(), "reviews size not 2");
		}
		
		List<Review> reviewList = new ArrayList<Review>();
		reviews.forEach(reviewList::add);
		
		assertTrue(reviewList.stream().anyMatch(rev ->
			rev.getComment().equals(REVIEW)// && rev.getId() == 1
		));
		assertTrue(reviewList.stream().anyMatch(rev ->
			rev.getComment().equals(REVIEW_NEW)// && rev.getId() == 2
		));
	}

	@Test
	void deleteReview() {
		
		// get review
		Review review = getReview();
		
		assertTrue(review != null, "review null");
		
		// delete review
		reviewRepository.deleteById(review.getId());
		
		Optional<Review> deletedReview = reviewRepository.findById(review.getId());
		
		// assert
		assertFalse(deletedReview.isPresent());
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// check other cascading entities
		// get course with id=1
		Optional<Course> course = courseRepository.findByTitle(COURSE);
		
		// assert
		// course must be unchanged
		assertTrue(course.isPresent());
//		//assertEquals(1, course.get().getId(), "course.get().getId()");
		assertEquals(COURSE, course.get().getTitle(), "course.get().getTitle()");
//		//assertEquals(0, course.get().getReviews().size(), "course.get().getReviews().size()");
//		assertFalse(course.get().getReviews().stream().anyMatch(rev ->
//			rev.getComment().equals(REVIEW)// && student.getId() == 2
//		));
//		assertEquals(INSTRUCTOR_FIRSTNAME, course.get().getInstructor().getFirstName(), "course.get().getInstructor().getFirstName()");
//		assertEquals(1, course.get().getStudents().size(), "course.get().getStudents().size()");
//		assertTrue(course.get().getStudents().stream().anyMatch(student ->
//			student.getFirstName().equals(STUDENT_FIRSTNAME)// && student.getId() == 2
//		));
	}
	
	private Review getReview() {
	
		// get all reviews
		Iterable<Review> reviews = reviewRepository.findAll();
		
		// assert
		assertNotNull(reviews,"reviews null");
		
		if (reviews instanceof Collection) {
			assertEquals(1, ((Collection<?>) reviews).size(), "reviews size not 1");
		}
		
		List<Review> reviewList = new ArrayList<Review>();
		reviews.forEach(reviewList::add);
	
		return reviewList.stream()
				.filter(rev -> rev.getComment().equals(REVIEW))
				.findAny()
				.orElse(null);
	}
}