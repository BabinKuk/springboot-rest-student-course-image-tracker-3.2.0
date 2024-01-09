package org.babinkuk.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.entity.Course;
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
public class StudentRepositoryTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(StudentRepositoryTest.class);
	
	@Test
	void getAllStudents() {
		
		// get all students
		Iterable<Student> students = studentRepository.findAll();
		
		// assert
		assertNotNull(students,"students null");
		
		if (students instanceof Collection) {
			assertEquals(1, ((Collection<?>) students).size(), "students size not 1");
		}
		
		List<Student> studentList = new ArrayList<Student>();
		students.forEach(studentList::add);

		assertTrue(studentList.stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME)// && student.getId() == 2
		));
	}
	
	@Test
	void getStudentById() {
		
		// get student
		Student student = getStudent();
		
		assertTrue(student != null, "student null");
		
		// get student by id
		Optional<Student> dbStudent = studentRepository.findById(student.getId());
		
		// assert
		assertTrue(dbStudent.isPresent());
		ApplicationTestUtils.validateExistingStudent(dbStudent.get());
		
		// get non-existing student id=222
		dbStudent = studentRepository.findById(222);
		
		// assert
		assertFalse(dbStudent.isPresent());
	}
	
	@Test
	void getStudentByEmail() {
		
		// get student
		Optional<Student> student = studentRepository.findByEmail(STUDENT_EMAIL);
		
		// assert
		assertTrue(student.isPresent());
		ApplicationTestUtils.validateExistingStudent(student.get());
		
		// get non-existing student
		student = studentRepository.findByEmail(INSTRUCTOR_EMAIL_NEW);
		
		// assert
		assertFalse(student.isPresent());
	}
	
	@Test
	void updateStudent() {
		
		// get student
		Student student = getStudent();
		
		assertTrue(student != null, "student null");
		
		// get student by id
		Optional<Student> dbStudent = studentRepository.findById(student.getId());
		
		// assert
		assertTrue(dbStudent.isPresent());
		ApplicationTestUtils.validateExistingStudent(dbStudent.get());
		
		// update
		// set id: this is to force an update of existing item
		Student updatedStudent = new Student();
		updatedStudent = ApplicationTestUtils.updateStudent(dbStudent.get());
		
		Student savedStudent = studentRepository.save(updatedStudent);
		
		// assert
		assertNotNull(savedStudent,"savedStudent null");
		ApplicationTestUtils.validateUpdatedStudent(savedStudent);
	}
	
	@Test
	void addStudent() {
		
		// create student
		// set id=0: this is to force a save of new item
		Student student = ApplicationTestUtils.createStudent();
		
		Student savedStudent = studentRepository.save(student);
		
		// assert
		assertNotNull(savedStudent,"savedStudent null");
		ApplicationTestUtils.validateNewStudent(savedStudent);
	}

	@Test
	void deleteStudent() {
		
		// get student
		Student student = getStudent();
		
		assertTrue(student != null, "student null");
		
		// get student by id
		Optional<Student> dbStudent = studentRepository.findById(student.getId());
		
		// assert
		assertTrue(dbStudent.isPresent());
		ApplicationTestUtils.validateExistingStudent(dbStudent.get());
		
		// delete student
		studentRepository.deleteById(dbStudent.get().getId());
		
		dbStudent = studentRepository.findById(dbStudent.get().getId());
		
		// assert
		assertFalse(dbStudent.isPresent());
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// check other cascading entities
		// get course with id=1
		Optional<Course> course = courseRepository.findByTitle(COURSE);
		
		// assert
		// course must be unchanged except students (null)
		assertTrue(course.isPresent());
//		assertEquals(1, course.get().getId(), "course.get().getId()");
//		assertEquals(COURSE, course.get().getTitle(), "course.get().getTitle()");
//		assertEquals(1, course.get().getReviews().size(), "course.get().getReviews().size()");
//		assertEquals(INSTRUCTOR_FIRSTNAME, course.get().getInstructor().getFirstName(), "course.get().getInstructor().getFirstName()");
//		assertEquals(0, course.get().getStudents().size(), "course.get().getStudents().size()");
//		assertTrue(course.get().getStudents().stream().anyMatch(stud ->
//			stud.getFirstName().equals(STUDENT_FIRSTNAME) && stud.getId() == 2
//		));
	}
	
	private Student getStudent() {
		
		// get all students
		Iterable<Student> students = studentRepository.findAll();
		
		// assert
		assertNotNull(students,"students null");
		
		if (students instanceof Collection) {
			assertEquals(1, ((Collection<?>) students).size(), "students size not 1");
		}
		
		List<Student> studentList = new ArrayList<Student>();
		students.forEach(studentList::add);
	
		return studentList.stream()
				.filter(rev -> rev.getFirstName().equals(STUDENT_FIRSTNAME))
				.findAny()
				.orElse(null);
	}
}