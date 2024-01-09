package org.babinkuk.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.entity.Instructor;
import org.babinkuk.utils.ApplicationTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;
import java.util.*;

@Transactional
@AutoConfigureMockMvc
public class InstructorRepositoryTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(InstructorRepositoryTest.class);
	
	@Test
	void getAllInstructors() {
		
		// get all instructors
		Iterable<Instructor> instructors = instructorRepository.findAll();
		
		// assert
		assertNotNull(instructors,"instructors null");
		
		if (instructors instanceof Collection) {
			assertEquals(1, ((Collection<?>) instructors).size(), "instructors size not 1");
		}
		
		List<Instructor> instructorList = new ArrayList<Instructor>();
		instructors.forEach(instructorList::add);

		assertTrue(instructorList.stream().anyMatch(instructor ->
			instructor.getFirstName().equals(INSTRUCTOR_FIRSTNAME)// && instructor.getId() == 1
		));
	}
	
	@Test
	void getInstructorById() {
		
		// get instructor
		Instructor instructor = getInstructor();
		
		assertTrue(instructor != null, "instructor null");
		
		// get instructor id
		Optional<Instructor> dbInstructor = instructorRepository.findById(instructor.getId());
		
		// assert
		assertTrue(dbInstructor.isPresent());
		ApplicationTestUtils.validateExistingInstructor(dbInstructor.get());
		
		// get non-existing instructor id=222
		dbInstructor = instructorRepository.findById(222);
		
		// assert
		assertFalse(dbInstructor.isPresent());
	}
	
	@Test
	void getInstructorByEmail() {
		
		// get instructor
		Instructor instructor = getInstructor();
		
		assertTrue(instructor != null, "instructor null");
		
		// get instructor id
		Optional<Instructor> dbInstructor = instructorRepository.findById(instructor.getId());
		
		// assert
		assertTrue(dbInstructor.isPresent());
		ApplicationTestUtils.validateExistingInstructor(dbInstructor.get());
		
		// get non-existing instructor
		dbInstructor = instructorRepository.findByEmail(INSTRUCTOR_EMAIL_NEW);
		
		// assert
		assertFalse(dbInstructor.isPresent());
	}
	
	@Test
	void updateInstructor() {
		
		// get instructor
		Instructor instructor = getInstructor();
		
		assertTrue(instructor != null, "instructor null");
		
		// get instructor id
		Optional<Instructor> dbInstructor = instructorRepository.findById(instructor.getId());
		
		// assert
		assertTrue(dbInstructor.isPresent());
		ApplicationTestUtils.validateExistingInstructor(dbInstructor.get());
		
		// update
		// set id=1: this is to force an update of existing item
		Instructor updatedInstructor = new Instructor();
		updatedInstructor = ApplicationTestUtils.updateInstructor(dbInstructor.get());
		
		Instructor savedInstructor = instructorRepository.save(updatedInstructor);
		
		// assert
		assertNotNull(savedInstructor,"savedInstructor null");
		ApplicationTestUtils.validateUpdatedInstructor(savedInstructor);
	}
	
	@Test
	void addInstructor() {
		
		// create instructor
		// set id=0: this is to force a save of new item
		Instructor instructor = ApplicationTestUtils.createInstructor();
		
		Instructor savedInstructor = instructorRepository.save(instructor);
		
		// assert
		assertNotNull(savedInstructor,"savedInstructor null");
		ApplicationTestUtils.validateNewInstructor(savedInstructor);
	}

	@Test
	void deleteInstructor() {
		
		// get instructor
		Instructor instructor = getInstructor();
		
		assertTrue(instructor != null, "instructor null");
		
		// get instructor id
		Optional<Instructor> dbInstructor = instructorRepository.findById(instructor.getId());
		
		// assert
		assertTrue(dbInstructor.isPresent());
		ApplicationTestUtils.validateExistingInstructor(dbInstructor.get());
		
		// delete instructor
		instructorRepository.deleteById(dbInstructor.get().getId());
		
		dbInstructor = instructorRepository.findById(dbInstructor.get().getId());
		
		// assert
		assertFalse(dbInstructor.isPresent());
		
		// check other cascading entities executed in service test
		// because of explicitly setting inside InstructorServiceImpl:
	}
	
	private Instructor getInstructor() {
		
		// get all instructors
		Iterable<Instructor> instructors = instructorRepository.findAll();
		
		// assert
		assertNotNull(instructors,"instructors null");
		
		if (instructors instanceof Collection) {
			assertEquals(1, ((Collection<?>) instructors).size(), "instructors size not 1");
		}
		
		List<Instructor> instructorList = new ArrayList<Instructor>();
		instructors.forEach(instructorList::add);
	
		return instructorList.stream()
				.filter(rev -> rev.getFirstName().equals(INSTRUCTOR_FIRSTNAME))
				.findAny()
				.orElse(null);
	}
}