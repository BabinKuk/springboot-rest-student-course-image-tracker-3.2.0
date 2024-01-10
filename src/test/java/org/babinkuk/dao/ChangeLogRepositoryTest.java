package org.babinkuk.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.entity.ChangeLog;
import org.babinkuk.entity.Course;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.Review;
import org.babinkuk.entity.Student;
import org.babinkuk.utils.ApplicationTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;

import java.util.*;

@Transactional
@AutoConfigureMockMvc
public class ChangeLogRepositoryTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(ChangeLogRepositoryTest.class);
			
	@Test
	void getAllChangeLogs() {
		
		// when
		Iterable<ChangeLog> chLogs = changeLogRepository.findAll();
		
		// then assert
		assertNotNull(chLogs,"chLogs null");
		
		if (chLogs instanceof Collection) {
			assertEquals(1, ((Collection<?>) chLogs).size(), "chLogs size not 1");
		}
		
		List<ChangeLog> chLogList = new ArrayList<ChangeLog>();
		chLogs.forEach(chLogList::add);

		assertTrue(chLogList.stream().anyMatch(chLog ->
			chLog.getChloUserId().equals("user") && chLog.getChloId() == 1
		));
	}
	
	@Test
	void getChangeLogById() {
		
		// given
		ChangeLog chLog = getChangeLog();
		
		assertTrue(chLog != null, "ChangeLog null");
		
		// when
		Optional<ChangeLog> dbChLog = changeLogRepository.findById(chLog.getChloId());
				
		// then assert
		assertTrue(dbChLog.isPresent());
		ApplicationTestUtils.validateExistingChangeLog(dbChLog.get());
		
		// get non-existing course id=2222
		dbChLog = changeLogRepository.findById(2222);
		
		// assert
		assertFalse(dbChLog.isPresent());
	}
	
//	@Test
//	void updateCourse() {
//		
//		// when
//		Course course = getChangeLog();
//				
//		// then assert
//		assertTrue(course != null, "course null");
//		
//		ApplicationTestUtils.validateExistingCourse(course);
//		
//		// update
//		// set id: this is to force an update of existing item
//		Course updatedCourse = new Course();
//		updatedCourse = ApplicationTestUtils.updateCourse(course);
//		
//		// save course
//		entityManager.persist(updatedCourse);
//		entityManager.flush();
//		
//		Course savedCourse = courseRepository.save(updatedCourse);
//		
//		// assert
//		assertNotNull(savedCourse,"savedCourse null");
//		ApplicationTestUtils.validateUpdatedCourse(savedCourse);
//	}
//	
//	@Test
//	void addCourse() {
//		
//		// create course
//		// set id=0: this is to force a save of new item
//		Course course = new Course(COURSE_NEW);
//		course.setId(0);
//		
//		Course savedCourse = courseRepository.save(course);
//		
//		// assert
//		assertNotNull(savedCourse,"savedCourse null");
//		ApplicationTestUtils.validateNewCourse(savedCourse);
//	}
//
//	@Test
//	void deleteCourse() {
//		
//		// when
//		Course course = getChangeLog();
//				
//		// then assert
//		assertTrue(course != null, "course null");
//		
//		ApplicationTestUtils.validateExistingCourse(course);
//				
//		// delete course
//		courseRepository.deleteById(course.getId());
//		
//		Optional<Course> deletedCourse = courseRepository.findById(course.getId());
//		
//		// assert
//		assertFalse(deletedCourse.isPresent());
//		
//		// get instructors
//		Iterable<Instructor> instructors = instructorRepository.findAll();
//		
//		// assert - must be unchanged
//		assertNotNull(instructors,"courses null");
//		
//		if (instructors instanceof Collection) {
//			assertEquals(1, ((Collection<?>) instructors).size(), "instructors size not 1");
//		}
//		
//		// get students
//		Iterable<Student> students = studentRepository.findAll();
//		
//		// assert - must be unchanged
//		assertNotNull(students, "students null");
//		
//		if (students instanceof Collection) {
//			assertEquals(1, ((Collection<?>) students).size(), "students size not 1");
//		}
//		
//		// get reviews
//		Iterable<Review> reviews = reviewRepository.findAll();
//		
//		// assert - must be deleted
//		assertNotNull(reviews, "reviews null");
//		
//		if (reviews instanceof Collection) {
//			assertEquals(0, ((Collection<?>) reviews).size(), "reviews size not 0");
//		}
//	}
	
	public ChangeLog getChangeLog() {
		
		// get all
		Iterable<ChangeLog> changeLogs = changeLogRepository.findAll();
		
		// assert
		assertNotNull(changeLogs,"ChangeLog null");
		
		if (changeLogs instanceof Collection) {
			assertEquals(1, ((Collection<?>) changeLogs).size(), "ChangeLog size not 1");
		}
		
		List<ChangeLog> chloList = new ArrayList<ChangeLog>();
		changeLogs.forEach(chloList::add);
	
		return chloList.stream()
				.filter(obj -> obj.getChloUserId().equals("user"))
				.findAny()
				.orElse(null);
	}
	
}