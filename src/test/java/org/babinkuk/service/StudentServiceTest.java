package org.babinkuk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.MessagePool;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.CourseVO;
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
public class StudentServiceTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(StudentServiceTest.class);
	
	@Test
	void getAllStudents() {
		
		// get all students
		Iterable<StudentVO> students = studentService.getAllStudents();
		
		// assert
		if (students instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) students).size(), "students size not 1");
		}
		
		List<StudentVO> studentList = new ArrayList<StudentVO>();
		students.forEach(studentList::add);
		
		assertTrue(studentList.stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
		));
	}
	
	@Test
	void getStudentById() {
		
		// get student id=2
		StudentVO studentVO = studentService.findById(2);
		
		// assert
		validateExistingStudent(studentVO);
		
		// assert non existing student
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			studentService.findById(11);
		});
				
		String expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), 11);
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void getStudentByEmail() {
		
		// get student
		StudentVO studentVO = studentService.findByEmail(STUDENT_EMAIL);

		validateExistingStudent(studentVO);
		
		// get non existing student
		studentVO = studentService.findByEmail(STUDENT_EMAIL_NEW);
	
		// assert
		assertNull(studentVO, "studentVO null");
	}
	
	@Test
	void addStudent() {
		
		// first create student
		// set id 0: this is to force a save of new item ... instead of update
		StudentVO studentVO = ApplicationTestUtils.createStudentVO();
		
		studentService.saveStudent(studentVO);

		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();

		studentVO = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		// assert
		validateNewStudent(studentVO);
	}
	
	@Test
	void updateStudent() {
		
		// get student
		StudentVO studentVO = studentService.findById(2);
		
		// update with new data
		studentVO = ApplicationTestUtils.updateExistingStudentVO(studentVO);
		
		studentService.saveStudent(studentVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again
		studentVO = studentService.findById(2);
		
		// assert
		validateUpdatedStudent(studentVO);
	}
	
	@Test
	void deleteStudent() {
		
		// get student
		StudentVO studentVO = studentService.findById(2);
		
		// assert
		assertNotNull(studentVO, "return true");
		assertEquals(2, studentVO.getId());
		
		// delete
		studentService.deleteStudent(2);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// assert non existing student
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			studentService.findById(2);
		});
				
		String expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), 2);
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));
	    
		// check other cascading entities
	    // get course with id=1
 		CourseVO courseVO = courseService.findById(1);
 			    
	    // course must be unchanged except students (size=0)
		assertNotNull(courseVO, "courseVO null");
		assertEquals(1, courseVO.getId(), "course.getId()");
		assertEquals(COURSE, courseVO.getTitle(), "course.getTitle()");
		assertEquals(1, courseVO.getReviewsVO().size(), "course.getReviews().size()");
		assertEquals(INSTRUCTOR_FIRSTNAME, courseVO.getInstructorVO().getFirstName(), "course.getInstructor().getFirstName()");
		assertEquals(0, courseVO.getStudentsVO().size(), "course.getStudents().size()");	
	}
	
	/**
	 * testing scenario
	 * 1. create new student
	 * 2. create new course
	 * 3. associate both students with both courses
	 * 4. withdraw both students from both courses
	 * 5. associate non existing student
	 * 6. associate non existing course 
	 */
	@Test
	void setCourse() {
		
		// check existing student
		StudentVO studentVO = studentService.findById(2);
		
		validateExistingStudent(studentVO);
		
		// check existing course
		CourseVO courseVO = courseService.findById(1);
		
		// assert course
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.courseVO() null");
		assertNotNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() not null");
		assertEquals(COURSE, courseVO.getTitle(),"courseVO.getTitle() NOK");
		assertNotNull(courseVO.getStudentsVO(),"courseVO.getStudentsVO() null");
		assertEquals(1, courseVO.getStudentsVO().size(), "courseVO.getStudentsVO() size not 1");
		assertTrue(courseVO.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
		));
		
		// 1. create new student
		StudentVO studentVO2 = ApplicationTestUtils.createStudentVO();
		
		studentService.saveStudent(studentVO2);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		studentVO2 = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		// assert
		validateNewStudent(studentVO2);
		//assertEquals(2, studentVO2.getId());
		assertEquals(STUDENT_FIRSTNAME_NEW, studentVO2.getFirstName(),"getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_NEW, studentVO2.getLastName(),"getLastName() NOK");
		assertEquals(STUDENT_EMAIL_NEW, studentVO2.getEmail(),"getEmail() NOK");
		
		// 2. create course
		CourseVO courseVO2 = ApplicationTestUtils.createCourseVO();
		
		courseService.saveCourse(courseVO2);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		courseVO2 = courseService.findByTitle(COURSE_NEW);
		
		// assert new course
		//assertEquals(2, courseVO2.getId());
		assertNotNull(courseVO2,"courseVO2 null");
		assertEquals(COURSE_NEW, courseVO2.getTitle(),"courseVO.getTitle() NOK");
		
		// 3. associate both students with both courses
		// set old course id=1 for new student
		studentService.setCourse(studentVO2, courseVO, ActionType.ENROLL);
		// set new course id=2 for new student
		studentService.setCourse(studentVO2, courseVO2, ActionType.ENROLL);
		// set new course id=2 for old student
		studentService.setCourse(studentVO, courseVO2, ActionType.ENROLL);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again old student
		studentVO = studentService.findById(2);
		
		// assert
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME, studentVO.getLastName(),"getLastName() NOK");
		assertEquals(STUDENT_EMAIL, studentVO.getEmail(),"getEmail() NOK");
		assertEquals(2, studentVO.getCoursesVO().size(), "getCourses size not 2");
		assertTrue(studentVO.getCoursesVO().stream().anyMatch(course ->
			course.getTitle().equals(COURSE) && course.getId() == 1
		));
		assertTrue(studentVO.getCoursesVO().stream().anyMatch(course ->
			course.getTitle().equals(COURSE_NEW)// && course.getId() == 2
		));
		
		// fetch again new student
		studentVO2 = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		// assert
		validateNewStudent(studentVO2);
		assertEquals(2, studentVO2.getCoursesVO().size(), "getCourses size not 2");
		assertTrue(studentVO2.getCoursesVO().stream().anyMatch(course ->
			course.getTitle().equals(COURSE) && course.getId() == 1
		));
		assertTrue(studentVO2.getCoursesVO().stream().anyMatch(course ->
			course.getTitle().equals(COURSE_NEW)// && course.getId() == 2
		));
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again courses
		courseVO = courseService.findById(1);
		courseVO2 = courseService.findByTitle(COURSE_NEW);
		
		// assert old course
		assertEquals(COURSE, courseVO.getTitle(),"getTitle() NOK");
		assertNotNull(courseVO.getStudentsVO(),"getStudentsVO() null");
		assertEquals(2, courseVO.getStudentsVO().size(), "getStudentsVO() size not 2");
		assertTrue(courseVO.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
		));
		assertTrue(courseVO.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME_NEW) && student.getLastName().equals(STUDENT_LASTNAME_NEW)
		));
		
		// assert new course
		assertEquals(COURSE_NEW, courseVO2.getTitle(),"getTitle() NOK");
		assertNotNull(courseVO2.getStudentsVO(),"getStudentsVO() null");
		assertEquals(2, courseVO2.getStudentsVO().size(), "getStudentsVO() size not 2");
		assertTrue(courseVO2.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
		));
		assertTrue(courseVO2.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME_NEW) && student.getLastName().equals(STUDENT_LASTNAME_NEW)
		));
		
		// withdraw both students from both courses
		studentService.setCourse(studentVO2, courseVO2, ActionType.WITHDRAW);
		studentService.setCourse(studentVO2, courseVO, ActionType.WITHDRAW);
		studentService.setCourse(studentVO, courseVO2, ActionType.WITHDRAW);
		studentService.setCourse(studentVO, courseVO, ActionType.WITHDRAW);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again old student
		studentVO = studentService.findById(2);
		
		// assert
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME, studentVO.getLastName(),"studentVO.getLastName() NOK");
		assertEquals(STUDENT_EMAIL, studentVO.getEmail(),"studentVO.getEmail() NOK");
		assertEquals(0, studentVO.getCoursesVO().size(), "studentVO.getCourses size not 0");
		
		// fetch again new student
		studentVO2 = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		// assert
		assertEquals(STUDENT_FIRSTNAME_NEW, studentVO2.getFirstName(),"studentVO2.getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_NEW, studentVO2.getLastName(),"studentVO2.getLastName() NOK");
		assertEquals(STUDENT_EMAIL_NEW, studentVO2.getEmail(),"studentVO2.getEmail() NOK");
		assertEquals(0, studentVO2.getCoursesVO().size(), "studentVO2.getCourses size not 0");
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again courses
		courseVO = courseService.findById(1);
		courseVO2 = courseService.findByTitle(COURSE_NEW);
		
		// assert old course
		assertEquals(COURSE, courseVO.getTitle(),"courseVO.getTitle() NOK");
		assertNotNull(courseVO.getStudentsVO(),"courseVO.getStudentsVO() null");
		assertEquals(0, courseVO.getStudentsVO().size(), "courseVO.getStudentsVO() size not 0");
		
		// assert new course
		assertEquals(COURSE_NEW, courseVO2.getTitle(),"courseVO2.getTitle() NOK");
		assertNotNull(courseVO2.getStudentsVO(),"courseVO2.getStudentsVO() null");
		assertEquals(0, courseVO2.getStudentsVO().size(), "courseVO2.getStudentsVO() size not 0");
		
		// not mandatory
		// 5. associate non existing student
		StudentVO nonExistingStudent = new StudentVO("firstName", "lastName", "email");
		nonExistingStudent.setId(33);
		
		// for avoiding Local variable instructorVO defined in an enclosing scope must be final or effectively final
		final CourseVO finalCourseVO = courseVO;
		
		// assert non existing course
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			studentService.setCourse(nonExistingStudent, finalCourseVO, ActionType.ENROLL);
		});
		
		String expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), 33);
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));

		// 6. associate non existing course
		CourseVO nonExistingCourseVO = new CourseVO("non existing course");
		nonExistingCourseVO.setId(3);
		
		// for avoiding Local variable instructorVO defined in an enclosing scope must be final or effectively final
		final StudentVO finalStudentVO = studentVO;
		
		// assert non existing course
		exception = assertThrows(ObjectNotFoundException.class, () -> {
			studentService.setCourse(finalStudentVO, nonExistingCourseVO, ActionType.ENROLL);
		});
		
		expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), 3);
		actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));	    
	}
	
	private void validateExistingStudent(StudentVO studentVO) {
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(2, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"getFirstName() null");
		assertNotNull(studentVO.getLastName(),"getLastName() null");
		assertNotNull(studentVO.getEmail(),"getEmail() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME, studentVO.getLastName(),"getLastName() NOK");
		assertEquals(STUDENT_EMAIL, studentVO.getEmail(),"getEmail() NOK");
		assertEquals(STUDENT_STREET, studentVO.getStreet(),"getStreet() NOK");
		assertEquals(STUDENT_CITY, studentVO.getCity(),"getCity() NOK");
		assertEquals(STUDENT_ZIPCODE, studentVO.getZipCode(),"getZipCode() NOK");
		assertEquals(STUDENT_STATUS, studentVO.getStatus(),"getStatus() NOK");
		assertEquals(1, studentVO.getImages().size(), "getImages size not 1");
		//assertThat(studentVO.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_2, STUDENT_IMAGE_2));
		//assertThat(studentVO.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_22, STUDENT_IMAGE_22));
		assertTrue(studentVO.getImages().stream().anyMatch(img ->
			img.getFileName().equals(FILE_2) && img.getId() == 2
		));
		assertEquals(1, studentVO.getCoursesVO().size(), "getCourses size not 1");
//		assertThat(studentVO.getCoursesVO(), contains(
//		    hasProperty("id", is(1))
//		));
//		assertThat(studentVO.getCoursesVO(), contains(
//			hasProperty("title", is("test course"))
//		));
		assertTrue(studentVO.getCoursesVO().stream().anyMatch(course ->
			course.getTitle().equals(COURSE) && course.getId() == 1
		));
		
		// not neccessary
		assertNotEquals("test", studentVO.getFirstName(),"getFirstName() NOK");
	}
	
	private void validateNewStudent(StudentVO studentVO) {
		
		assertNotNull(studentVO,"studentVO null");
		//assertEquals(2, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"getFirstName() null");
		assertNotNull(studentVO.getLastName(),"getLastName() null");
		assertNotNull(studentVO.getEmail(),"getEmail() null");
		assertEquals(STUDENT_FIRSTNAME_NEW, studentVO.getFirstName(),"getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_NEW, studentVO.getLastName(),"getLastName() NOK");
		assertEquals(STUDENT_EMAIL_NEW, studentVO.getEmail(),"getEmail() NOK");
		assertEquals(STUDENT_STREET_NEW, studentVO.getStreet(),"getStreet() NOK");
		assertEquals(STUDENT_CITY_NEW, studentVO.getCity(),"getCity() NOK");
		assertEquals(STUDENT_ZIPCODE_NEW, studentVO.getZipCode(),"getZipCode() NOK");
		assertEquals(STUDENT_STATUS_NEW, studentVO.getStatus(),"getStatus() NOK");
		assertEquals(0, studentVO.getImages().size(), "getImages size not 0");
		//assertThat(studentVO.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_NEW, STUDENT_IMAGE_NEW));
//		assertEquals(1, studentVO.getCoursesVO().size(), "studentVO.getCourses size not 1");
//		assertThat(studentVO.getCoursesVO(), contains(
//		    hasProperty("id", is(1))
//		));
//		assertThat(studentVO.getCoursesVO(), contains(
//			hasProperty("title", is("test course"))
//		));
//		assertTrue(studentVO.getCoursesVO().stream().anyMatch(course ->
//			course.getTitle().equals(COURSE) && course.getId() == 1
//		));
		
		// not neccessary
		assertNotEquals("test", studentVO.getFirstName(),"getFirstName() NOK");
	}
	
	private void validateUpdatedStudent(StudentVO studentVO) {
		
		assertNotNull(studentVO,"studentVO null");
		assertNotNull(studentVO.getFirstName(),"getFirstName() null");
		assertNotNull(studentVO.getLastName(),"getLastName() null");
		assertNotNull(studentVO.getEmail(),"getEmail() null");
		assertEquals(2, studentVO.getId());
		assertEquals(STUDENT_FIRSTNAME_UPDATED, studentVO.getFirstName(),"getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_UPDATED, studentVO.getLastName(),"getLastName() NOK");
		assertEquals(STUDENT_EMAIL_UPDATED, studentVO.getEmail(),"getEmailAddress() NOK");
		assertEquals(STUDENT_STREET_UPDATED, studentVO.getStreet(),"getStreet() NOK");
		assertEquals(STUDENT_CITY_UPDATED, studentVO.getCity(),"getCity() NOK");
		assertEquals(STUDENT_ZIPCODE_UPDATED, studentVO.getZipCode(),"getZipCode() NOK");
		assertEquals(STUDENT_STATUS_UPDATED, studentVO.getStatus(),"getStatus() NOK");
		assertEquals(1, studentVO.getImages().size(), "studentVO.getImages size not 1");
		//assertThat(studentVO.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_2, STUDENT_IMAGE_2));
		//assertThat(studentVO.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_22, STUDENT_IMAGE_22));
		//assertThat(studentVO.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_UPDATED, STUDENT_IMAGE_UPDATED));
		assertTrue(studentVO.getImages().stream().anyMatch(img ->
			img.getFileName().equals(FILE_2) && img.getId() == 2
		));
		assertEquals(1, studentVO.getCoursesVO().size(), "getCourses size not 1");		
	}
}
