package org.babinkuk.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.MessagePool;
import org.babinkuk.config.Api.RestModule;
import org.babinkuk.entity.ChangeLog;
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
		
		// assert change log
		// when
		Iterable<ChangeLog> chLogs = changeLogService.getAllChangeLogs();
		
		// then assert
		assertNotNull(chLogs,"chLogs null");
				
		if (chLogs instanceof Collection) {
			assertEquals(1, ((Collection<?>) chLogs).size(), "chLogs size not 1");
		}
		
		List<ChangeLog> chLogList = new ArrayList<ChangeLog>();
		chLogs.forEach(chLogList::add);
//		log.info(chLogs);
//		[ChangeLog [chloId=1, chloTimestamp=2024-05-15 15:44:26.436, chloUserId=COURSE, 
//					logModule=LogModule [lmId=3, lmDescription=COURSE, lmEntityName=org.babinkuk.entity.Course], 
//					chloTableId=3, 
//					changeLogItems=[ChangeLogItem [chliId=1, chliFieldName=CourseVO.reviewsVO.insert, chliOldValueId=0, chliOldValue=-, chliNewValue=ReviewVO [id=0, comment=new review], chliNewValueId=0]]]]
		
		assertTrue(chLogList.stream().anyMatch(chLog ->
			chLog.getChloId() == 1
			&& chLog.getChloUserId().equals(RestModule.COURSE.getLabel())
			&& chLog.getChloTableId() == (RestModule.COURSE.getModuleId())
			&& chLog.getLogModule().getLmId() == (RestModule.COURSE.getModuleId())
			&& chLog.getLogModule().getLmDescription().equals(RestModule.COURSE.getLabel())
			&& chLog.getLogModule().getLmEntityName().equals("org.babinkuk.entity.Course")
			&& chLog.getChangeLogItems().size() == 1
			&& chLog.getChangeLogItems().stream().anyMatch(item ->
				item.getChliId() == 1
				&& item.getChliFieldName().equals("CourseVO.reviewsVO.insert")
				&& item.getChliNewValueId() == 0
				&& item.getChliOldValueId() == 0
				&& item.getChliOldValue().equals("-")
				&& StringUtils.isNotBlank(item.getChliNewValue())
				&& StringUtils.contains(item.getChliNewValue(), "ReviewVO [")
				&& StringUtils.contains(item.getChliNewValue(), REVIEW_NEW)
			)
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
		
		// assert change log
		// when
		Iterable<ChangeLog> chLogs = changeLogService.getAllChangeLogs();
		
		// then assert
		assertNotNull(chLogs,"chLogs null");
				
		if (chLogs instanceof Collection) {
			assertEquals(1, ((Collection<?>) chLogs).size(), "chLogs size not 1");
		}
		
		List<ChangeLog> chLogList = new ArrayList<ChangeLog>();
		chLogs.forEach(chLogList::add);
//		log.info(chLogs);
//		[ChangeLog [chloId=1, chloTimestamp=2024-05-15 15:52:10.467, chloUserId=REVIEW, 
//					logModule=LogModule [lmId=4, lmDescription=REVIEW, lmEntityName=org.babinkuk.entity.Review], 
//					chloTableId=4, 
//					changeLogItems=[ChangeLogItem [chliId=1, chliFieldName=ReviewVO.comment.update, chliOldValueId=1, chliOldValue=test review, chliNewValue=update test review, chliNewValueId=1]]]]
		
		assertTrue(chLogList.stream().anyMatch(chLog ->
			chLog.getChloId() == 1
			&& chLog.getChloUserId().equals(RestModule.REVIEW.getLabel())
			&& chLog.getChloTableId() == (RestModule.REVIEW.getModuleId())
			&& chLog.getLogModule().getLmId() == (RestModule.REVIEW.getModuleId())
			&& chLog.getLogModule().getLmDescription().equals(RestModule.REVIEW.getLabel())
			&& chLog.getLogModule().getLmEntityName().equals("org.babinkuk.entity.Review")
			&& chLog.getChangeLogItems().size() == 1
			&& chLog.getChangeLogItems().stream().anyMatch(item ->
				item.getChliId() == 1
				&& item.getChliFieldName().equals("ReviewVO.comment.update")
				&& item.getChliNewValueId() == item.getChliOldValueId()
				&& StringUtils.isNotBlank(item.getChliOldValue())
				&& StringUtils.contains(item.getChliOldValue(), REVIEW)
				&& StringUtils.isNotBlank(item.getChliNewValue())
				&& StringUtils.contains(item.getChliNewValue(), REVIEW_UPDATE)
			)
		));
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
	    
		// assert change log
		// when
		Iterable<ChangeLog> chLogs = changeLogService.getAllChangeLogs();
		
		// then assert
		assertNotNull(chLogs,"chLogs null");
				
		if (chLogs instanceof Collection) {
			assertEquals(1, ((Collection<?>) chLogs).size(), "chLogs size not 1");
		}
		
		List<ChangeLog> chLogList = new ArrayList<ChangeLog>();
		chLogs.forEach(chLogList::add);
//		log.info(chLogs);
//		[ChangeLog [chloId=1, chloTimestamp=2024-05-15 15:59:06.096, chloUserId=REVIEW, 
//					logModule=LogModule [lmId=4, lmDescription=REVIEW, lmEntityName=org.babinkuk.entity.Review], 
//					chloTableId=4, 
//					changeLogItems=[ChangeLogItem [chliId=1, chliFieldName=ReviewVO.delete, chliOldValueId=0, chliOldValue=ReviewVO [id=1, comment=test review], chliNewValue=-, chliNewValueId=0]]]]
		
		assertTrue(chLogList.stream().anyMatch(chLog ->
			chLog.getChloId() == 1
			&& chLog.getChloUserId().equals(RestModule.REVIEW.getLabel())
			&& chLog.getChloTableId() == (RestModule.REVIEW.getModuleId())
			&& chLog.getLogModule().getLmId() == (RestModule.REVIEW.getModuleId())
			&& chLog.getLogModule().getLmDescription().equals(RestModule.REVIEW.getLabel())
			&& chLog.getLogModule().getLmEntityName().equals("org.babinkuk.entity.Review")
			&& chLog.getChangeLogItems().size() == 1
			&& chLog.getChangeLogItems().stream().anyMatch(item ->
				item.getChliId() == 1
				&& item.getChliFieldName().equals("ReviewVO.delete")
				&& item.getChliNewValueId() == item.getChliOldValueId()
				&& item.getChliNewValue().equals("-")
				&& StringUtils.isNotBlank(item.getChliOldValue())
				&& StringUtils.contains(item.getChliOldValue(), "ReviewVO [")
				&& StringUtils.contains(item.getChliOldValue(), REVIEW)
			)
		));
	}
}