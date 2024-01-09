package org.babinkuk.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
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
public class CourseRepositoryTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(CourseRepositoryTest.class);
			
	@Test
	void getAllCourses() {
		
		// when
		Iterable<Course> courses = courseRepository.findAll();
		
		// then assert
		assertNotNull(courses,"courses null");
		
		if (courses instanceof Collection) {
			assertEquals(1, ((Collection<?>) courses).size(), "courses size not 1");
		}
		
		List<Course> courseList = new ArrayList<Course>();
		courses.forEach(courseList::add);

		assertTrue(courseList.stream().anyMatch(course ->
			course.getTitle().equals(COURSE)// && course.getId() == 1
		));
	}
	
	@Test
	void getCourseById() {
		
		// given
		Course course = getCourse();
		
		assertTrue(course != null, "course null");
		
		// when
		Optional<Course> dbCourse = courseRepository.findById(course.getId());
				
		// then assert
		assertTrue(dbCourse.isPresent());
		ApplicationTestUtils.validateExistingCourse(dbCourse.get());
		
		// get non-existing course id=2222
		dbCourse = courseRepository.findById(2222);
		
		// assert
		assertFalse(dbCourse.isPresent());
	}
	
	@Test
	void getCourseByTitle() {
				
		// when
		Optional<Course> course = courseRepository.findByTitle(COURSE);
				
		// then assert
		assertTrue(course.isPresent());
		ApplicationTestUtils.validateExistingCourse(course.get());
		
		// get non-existing course id=22
		course = courseRepository.findByTitle(COURSE_NEW);
		
		// assert
		assertFalse(course.isPresent());
	}
	
	@Test
	void updateCourse() {
		
		// when
		Course course = getCourse();
				
		// then assert
		assertTrue(course != null, "course null");
		
		ApplicationTestUtils.validateExistingCourse(course);
		
		// update
		// set id: this is to force an update of existing item
		Course updatedCourse = new Course();
		updatedCourse = ApplicationTestUtils.updateCourse(course);
		
		// save course
		entityManager.persist(updatedCourse);
		entityManager.flush();
		
		Course savedCourse = courseRepository.save(updatedCourse);
		
		// assert
		assertNotNull(savedCourse,"savedCourse null");
		ApplicationTestUtils.validateUpdatedCourse(savedCourse);
	}
	
	@Test
	void addCourse() {
		
		// create course
		// set id=0: this is to force a save of new item
		Course course = new Course(COURSE_NEW);
		course.setId(0);
		
		Course savedCourse = courseRepository.save(course);
		
		// assert
		assertNotNull(savedCourse,"savedCourse null");
		ApplicationTestUtils.validateNewCourse(savedCourse);
	}

	@Test
	void deleteCourse() {
		
		// when
		Course course = getCourse();
				
		// then assert
		assertTrue(course != null, "course null");
		
		ApplicationTestUtils.validateExistingCourse(course);
				
		// delete course
		courseRepository.deleteById(course.getId());
		
		Optional<Course> deletedCourse = courseRepository.findById(course.getId());
		
		// assert
		assertFalse(deletedCourse.isPresent());
		
		// get instructors
		Iterable<Instructor> instructors = instructorRepository.findAll();
		
		// assert - must be unchanged
		assertNotNull(instructors,"courses null");
		
		if (instructors instanceof Collection) {
			assertEquals(1, ((Collection<?>) instructors).size(), "instructors size not 1");
		}
		
		// get students
		Iterable<Student> students = studentRepository.findAll();
		
		// assert - must be unchanged
		assertNotNull(students, "students null");
		
		if (students instanceof Collection) {
			assertEquals(1, ((Collection<?>) students).size(), "students size not 1");
		}
		
		// get reviews
		Iterable<Review> reviews = reviewRepository.findAll();
		
		// assert - must be deleted
		assertNotNull(reviews, "reviews null");
		
		if (reviews instanceof Collection) {
			assertEquals(0, ((Collection<?>) reviews).size(), "reviews size not 0");
		}
	}
	
	public Course getCourse() {
		
		// get all courses
		Iterable<Course> courses = courseRepository.findAll();
		
		// assert
		assertNotNull(courses,"courses null");
		
		if (courses instanceof Collection) {
			assertEquals(1, ((Collection<?>) courses).size(), "courses size not 1");
		}
		
		List<Course> courseList = new ArrayList<Course>();
		courses.forEach(courseList::add);
	
		return courseList.stream()
				.filter(rev -> rev.getTitle().equals(COURSE))
				.findAny()
				.orElse(null);
	}
	
}