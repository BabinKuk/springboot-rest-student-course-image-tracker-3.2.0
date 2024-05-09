package org.babinkuk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.MessagePool;
import org.babinkuk.dao.ChangeLogRepositoryTest;
import org.babinkuk.config.Api.RestModule;
import org.babinkuk.entity.ChangeLog;
import org.babinkuk.entity.Course;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.InstructorVO;
import org.babinkuk.vo.StudentVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Transactional
@AutoConfigureMockMvc
public class ChangeLogServiceTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(ChangeLogServiceTest.class);
	
	@Test
	void getAllChangeLogs() {
		
 		// when
		Iterable<ChangeLog> chLogs = changeLogService.getAllChangeLogs();
		
		// then assert
		assertNotNull(chLogs,"chLogs null");
		
		if (chLogs instanceof Collection) {
			assertEquals(0, ((Collection<?>) chLogs).size(), "chLogs size not 0");
		}
		
		// create chLog
		ChangeLog changeLog = ChangeLogRepositoryTest.createChangeLog();
		// save
		ChangeLog savedChLog = changeLogRepository.save(changeLog);
		
		// assert
		assertNotNull(savedChLog,"savedChLog null");
		ApplicationTestUtils.validateNewChangeLog(savedChLog);
		
		chLogs = changeLogService.getAllChangeLogs();
		
		// then assert
		assertNotNull(chLogs,"chLogs null");
		
		if (chLogs instanceof Collection) {
			assertEquals(1, ((Collection<?>) chLogs).size(), "chLogs size not 1");
		}
		
		List<ChangeLog> chLogList = new ArrayList<ChangeLog>();
		chLogs.forEach(chLogList::add);

		assertTrue(chLogList.stream().anyMatch(chLog ->
			chLog.getChloUserId().equals(RestModule.COURSE.getLabel())
				&& chLog.getChloTableId() == (RestModule.COURSE.getModuleId())
				&& chLog.getChloId() == 1
		));
	}
	
	@Test
	void getChangeLogById() {
		
		// create changeLog
		ChangeLog changeLog = ChangeLogRepositoryTest.createChangeLog();
		// save
		ChangeLog savedChLog = changeLogRepository.save(changeLog);
		
		// assert
		assertNotNull(savedChLog,"savedChLog null");
		ApplicationTestUtils.validateNewChangeLog(savedChLog);
		
		// when
		Optional<ChangeLog> dbChLog = changeLogService.findById(savedChLog.getChloId());
				
		// then assert
		assertTrue(dbChLog.isPresent());
		ApplicationTestUtils.validateNewChangeLog(dbChLog.get());
		
		// assert non existing changeLog
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			changeLogService.findById(222);
		});
		
		String expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_CHANGELOG_ID_NOT_FOUND.getMessage()), 222);
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void saveChangeLog() {
		
		// create changeLog
		ChangeLog changeLog = ChangeLogRepositoryTest.createChangeLog();
		
		// when
		int id = 1;
		
		// get course
		CourseVO originalCourseVO = courseService.findById(id);
		
		// assert
		validateExistingCourse(originalCourseVO);
					
		// current course 
		CourseVO currentCourseVO = courseService.findById(id);
		
		// should not save change log because original=current
		changeLogService.saveChangeLog(changeLog, originalCourseVO, currentCourseVO);
		
		// when
		Iterable<ChangeLog> chLogs = changeLogService.getAllChangeLogs();
		
		// then assert
		assertNotNull(chLogs,"chLogs null");
		
		if (chLogs instanceof Collection) {
			assertEquals(0, ((Collection<?>) chLogs).size(), "chLogs size not 0");
		}
		
		log.info(originalCourseVO.getTitle());
		// update with new data
		currentCourseVO = ApplicationTestUtils.updateExistingCourseVO(currentCourseVO);
		log.info(currentCourseVO.getTitle());
		
		// should save change log because original=current
		changeLogService.saveChangeLog(changeLog, originalCourseVO, currentCourseVO);
	
		chLogs = changeLogService.getAllChangeLogs();
		
		// then assert
		assertNotNull(chLogs,"chLogs null");
		
		if (chLogs instanceof Collection) {
			assertEquals(1, ((Collection<?>) chLogs).size(), "chLogs size not 1");
		}
		
		List<ChangeLog> chLogList = new ArrayList<ChangeLog>();
		chLogs.forEach(chLogList::add);
		
		assertTrue(chLogList.stream().anyMatch(chLog ->
			chLog.getChloUserId().equals(RestModule.COURSE.getLabel())
				&& chLog.getChloTableId() == (RestModule.COURSE.getModuleId())
				&& chLog.getChloId() == 1
				&& chLog.getChangeLogItems().size() == 1
				&& chLog.getChangeLogItems().stream().anyMatch(item -> 
					COURSE.equals(item.getChliOldValue()) 
						&& COURSE_UPDATED.equals(item.getChliNewValue())
						&& item.getChliFieldName().equals("CourseVO.title.update"))
		));
	}
	
	@Test
	void deleteChangeLog() {
		
		// create changeLog
		ChangeLog changeLog = ChangeLogRepositoryTest.createChangeLog();
		// save
		ChangeLog savedChLog = changeLogRepository.save(changeLog);
		
		// assert
		assertNotNull(savedChLog,"savedChLog null");
		ApplicationTestUtils.validateNewChangeLog(savedChLog);
				
		// delete changeLog
		changeLogService.deleteChangeLog(savedChLog.getChloId());
		
		// assert non existing changeLog
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			changeLogService.findById(savedChLog.getChloId());
		});
		
		String expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_CHANGELOG_ID_NOT_FOUND.getMessage()), savedChLog.getChloId());
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));		
	}
	
	private void validateExistingCourse(CourseVO courseVO) {
		
		assertNotNull(courseVO,"course null");
		assertNotNull(courseVO.getTitle(),"getTitle() null");
		assertNotNull(courseVO.getStudentsVO(),"getStudents() null");
		assertNotNull(courseVO.getReviewsVO(),"getReviews() null");
		//assertNotNull(courseVO.getInstructorVO(),"getInstructor() null");
		assertEquals(1, courseVO.getId());
		assertEquals(COURSE, courseVO.getTitle(),"getTitle() NOK");
		assertEquals(INSTRUCTOR_EMAIL, courseVO.getInstructorVO().getEmail(),"getInstructor().getEmail() NOK");
		assertEquals(1, courseVO.getReviewsVO().size(), "getReviews size not 1");
		assertTrue(courseVO.getReviewsVO().stream().anyMatch(review ->
			review.getComment().equals(REVIEW)// && review.getId() == 1
		));
		assertEquals(1, courseVO.getStudentsVO().size(), "getStudents size not 1");
		assertTrue(courseVO.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME)// && student.getId() == 2
		));
	}
	
	private void validateUpdatedCourse(CourseVO courseVO) {
		
		assertNotNull(courseVO,"course null");
		assertNotNull(courseVO.getTitle(),"getTitle() null");
		assertNotNull(courseVO.getStudentsVO(),"getStudents() null");
		assertNotNull(courseVO.getReviewsVO(),"getReviews() null");
		//assertNotNull(courseVO.getInstructorVO(),"getInstructor() null");
		assertEquals(1, courseVO.getId());
		assertEquals(COURSE_UPDATED, courseVO.getTitle(),"getTitle() NOK");
		assertEquals(INSTRUCTOR_EMAIL, courseVO.getInstructorVO().getEmail(),"getInstructor().getEmail() NOK");
		assertEquals(1, courseVO.getReviewsVO().size(), "getReviews size not 1");
		assertTrue(courseVO.getReviewsVO().stream().anyMatch(review ->
			review.getComment().equals(REVIEW)// && review.getId() == 1
		));
		assertEquals(1, courseVO.getStudentsVO().size(), "getStudents size not 1");
		assertTrue(courseVO.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME)// && student.getId() == 2
		));
	}
	
	private void validateNewCourse(CourseVO courseVO) {
		
		assertNotNull(courseVO,"course null");
		assertNotNull(courseVO.getTitle(),"getTitle() null");
		assertNotNull(courseVO.getStudentsVO(),"getStudents() null");
		assertNull(courseVO.getInstructorVO(),"getInstructor() null");
		//assertEquals(1, course.getId());
		assertEquals(COURSE_NEW, courseVO.getTitle(),"getTitle() NOK");
		assertEquals(0, courseVO.getStudentsVO().size(), "getStudents size not 0");
		assertEquals(0, courseVO.getReviewsVO().size(), "getReviews size not 0");
	}
	
	public CourseVO getChangeLog() {
		
		// get all courses
		Iterable<CourseVO> courses = courseService.getAllCourses();
		
		// assert
		if (courses instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) courses).size(), "courses size not 1");
		}
		
		List<CourseVO> courseList = new ArrayList<CourseVO>();
		courses.forEach(courseList::add);
	
		return courseList.stream()
				.filter(obj -> obj.getTitle().equals(COURSE))
				.findAny()
				.orElse(null);

	}
}
