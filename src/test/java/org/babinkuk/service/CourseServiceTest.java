package org.babinkuk.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.MessagePool;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Transactional
@AutoConfigureMockMvc
public class CourseServiceTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(CourseServiceTest.class);
	
	@Test
	void getAllCourses() {
		
		// get all courses
		Iterable<CourseVO> coursesVO = courseService.getAllCourses();
		
		List<CourseVO> courseList = new ArrayList<CourseVO>();
		coursesVO.forEach(courseList::add);
		
		// assert
		if (coursesVO instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) coursesVO).size(), "courses size not 1");
		}
		
		assertTrue(courseList.stream().anyMatch(course ->
			course.getTitle().equals(COURSE) && course.getId() == 1
		));
	}
	
	@Test
	void getCourseById() {
		
		int id = 1;
		
		// get course
		CourseVO courseVO = courseService.findById(id);
		
		// assert
		validateExistingCourse(courseVO);
		
		// assert non existing course
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			courseService.findById(222);
		});
		
		String expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), 222);
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void getCourseByTitle() {
		
		// get course
		CourseVO courseVO = courseService.findByTitle(COURSE);
		
		// assert
		validateExistingCourse(courseVO);
				
		// assert not existing course
		courseVO = courseService.findByTitle(COURSE_NEW);
				
		// assert
		assertNull(courseVO, "courseVO null");
	}
	
	@Test
	void addCourse() {
		
		// create course
		CourseVO courseVO = ApplicationTestUtils.createCourseVO();
		
		courseService.saveCourse(courseVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
				
		courseVO = courseService.findByTitle(COURSE_NEW);
		
		// assert
		validateNewCourse(courseVO);
		
		// get all courses
		Iterable<CourseVO> courses = courseService.getAllCourses();
		
		// assert
		if (courses instanceof Collection<?>) {
			assertEquals(2, ((Collection<?>) courses).size(), "courses size not 2");
		}
		
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
//		[ChangeLog [chloId=1, chloTimestamp=2024-05-14 12:38:40.087, chloUserId=COURSE, 
//			logModule=LogModule [lmId=3, lmDescription=COURSE, lmEntityName=org.babinkuk.entity.Course], 
//			chloTableId=3, 
//			changeLogItems=[ChangeLogItem [chliId=1, chliFieldName=CourseVO.insert, chliOldValueId=0, chliOldValue=-, chliNewValue=CourseVO [id=0, title=new test, instructorVO=null, studentsVO=[], reviewsVO=[]], chliNewValueId=0]]]]
		
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
				&& item.getChliFieldName().equals("CourseVO.insert")
				&& item.getChliNewValueId() == 0
				&& item.getChliOldValueId() == 0
				&& item.getChliOldValue().equals("-")
				&& StringUtils.isNotBlank(item.getChliNewValue())
				&& StringUtils.contains(item.getChliNewValue(), "CourseVO [")
			)
		));
	}
	
	@Test
	void updateCourse() {
		
		int id = 1;
		Course course = new Course(COURSE);
		course.setId(id);
		
		// get course
		CourseVO courseVO = courseService.findById(id);
		
		// assert
		validateExistingCourse(courseVO);
		
		// update with new data
		courseVO = ApplicationTestUtils.updateExistingCourseVO(courseVO);
		
		courseService.saveCourse(courseVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again
		courseVO = courseService.findById(id);
		
		// assert
		validateUpdatedCourse(courseVO);
		
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
		log.info(chLogList);
		
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
				&& item.getChliFieldName().equals("CourseVO.title.update")
				&& item.getChliNewValueId() == 1
				&& item.getChliOldValueId() == 1
				&& item.getChliNewValue().equals(COURSE_UPDATED)
				//&& StringUtils.isNotBlank(item.getChliOldValue())
				&& item.getChliOldValue().equals(COURSE)
			)
		));
	}
	
	@Test
	void deleteCourse() {
		
		int id = 1;
		
		// get course
		CourseVO courseVO = courseService.findById(id);
		
		// assert
		validateExistingCourse(courseVO);
		
		// delete
		courseService.deleteCourse(id);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// assert not existing course
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			courseService.findById(id);
		});
		
		String expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), id);
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
		
		// check other cascading entities
		// get instructor
		InstructorVO instructorVO = instructorService.findById(1);
	 	
		// assert
		// instructor must be unchanged except courses=0
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertEquals(INSTRUCTOR_FIRSTNAME, instructorVO.getFirstName(),"getFirstName() NOK");
		assertEquals(1, instructorVO.getImages().size(), "getImages size not 1");
		assertTrue(instructorVO.getImages().stream().anyMatch(img ->
			img.getFileName().equals(FILE_1) && img.getId() == 1
		));
		assertEquals(0, instructorVO.getCourses().size(), "getCourses size not 0");
				
		// get student
		StudentVO studentVO = studentService.findById(2);
		
		// assert
		// student must be unchanged except courses=0
		assertNotNull(studentVO,"studentVO null");
		assertEquals(2, studentVO.getId());
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"getFirstName() NOK");
		assertEquals(1, studentVO.getImages().size(), "getImages size not 1");
		assertTrue(studentVO.getImages().stream().anyMatch(img ->
			img.getFileName().equals(FILE_2) && img.getId() == 2
		));
		assertEquals(0, studentVO.getCoursesVO().size(), "getCourses size not 0");
		
		// assert non existing review
		// if course is deleted, delete all associated reviews (jpa uni-directional)
		exception = assertThrows(ObjectNotFoundException.class, () -> {
			reviewService.findById(1);
		});
				
		expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_REVIEW_ID_NOT_FOUND.getMessage()), 1);
		actualMessage = exception.getMessage();
		
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
				&& item.getChliFieldName().equals("CourseVO.delete")
				&& item.getChliNewValueId() == 0
				&& item.getChliOldValueId() == 0
				&& item.getChliNewValue().equals("-")
				&& StringUtils.isNotBlank(item.getChliOldValue())
				&& StringUtils.contains(item.getChliOldValue(), "CourseVO [")
			)
		));
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
}
