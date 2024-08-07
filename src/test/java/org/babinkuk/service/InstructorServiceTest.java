package org.babinkuk.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.MessagePool;
import org.babinkuk.config.Api.RestModule;
import org.babinkuk.entity.ChangeLog;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.Status;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.InstructorVO;
import org.hamcrest.beans.HasProperty;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@AutoConfigureMockMvc
public class InstructorServiceTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(InstructorServiceTest.class);
	
	@Test
	void getAllInstructors() {
		
		// get all instructors
		Iterable<InstructorVO> instructors = instructorService.getAllInstructors();
		
		// assert
		if (instructors instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) instructors).size(), "instructors size not 1");
		}
		
		// add another instructor
		// set id=0: this is to force a save of new item ... instead of update
		InstructorVO instructorVO = ApplicationTestUtils.createInstructorVO();
		
		instructorService.saveInstructor(instructorVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		instructors = instructorService.getAllInstructors();
		
		// assert
		if (instructors instanceof Collection<?>) {
			assertEquals(2, ((Collection<?>) instructors).size(), "instructors size not 2 after insert");
		}
	}
	
	@Test
	void getInstructorById() {
		
		// get instructor id=1
		InstructorVO instructorVO = instructorService.findById(1);
		
		validateExistingInstructor(instructorVO);
		
		// assert not existing instructor
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			instructorService.findById(11);
		});
				
		String expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), 11);
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void getInstructorByEmail() {
		
		// get instructor
		InstructorVO instructorVO = instructorService.findByEmail(INSTRUCTOR_EMAIL);
		
		validateExistingInstructor(instructorVO);
		
		// get not existing instructor
		instructorVO = instructorService.findByEmail(INSTRUCTOR_EMAIL_NEW);
		
		// assert
		assertNull(instructorVO, "instructorVO null");
	}
	
	@Test
	void addInstructor() {
		
		// create instructor
		InstructorVO instructorVO = ApplicationTestUtils.createInstructorVO();
		
		instructorService.saveInstructor(instructorVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		instructorVO = instructorService.findByEmail(INSTRUCTOR_EMAIL_NEW);
		
		// assert
		validateNewInstructor(instructorVO);
		
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
//		[[ChangeLog [chloId=1, chloTimestamp=2024-05-15 13:19:57.719, 
//			chloUserId=INSTRUCTOR, 
//			logModule=LogModule [lmId=2, lmDescription=INSTRUCTOR, lmEntityName=org.babinkuk.entity.Instructor], 
//			chloTableId=2, 
//			changeLogItems=[ChangeLogItem [chliId=1, chliFieldName=InstructorVO.insert, chliOldValueId=0, chliOldValue=-, chliNewValue=InstructorVO [id=0, firstName=firstNameInstrNew, lastName=lastNameInstrNew, email=InstrNew@babinkuk.com, images=null, status=ACTIVE, salary=1500.0, youtubeChannel=youtubeChannel, hobby=hobby], chliNewValueId=0]]]]
		
		assertTrue(chLogList.stream().anyMatch(chLog ->
			chLog.getChloId() == 1
			&& chLog.getChloUserId().equals(RestModule.INSTRUCTOR.getLabel())
			&& chLog.getChloTableId() == (RestModule.INSTRUCTOR.getModuleId())
			&& chLog.getLogModule().getLmId() == (RestModule.INSTRUCTOR.getModuleId())
			&& chLog.getLogModule().getLmDescription().equals(RestModule.INSTRUCTOR.getLabel())
			&& chLog.getLogModule().getLmEntityName().equals("org.babinkuk.entity.Instructor")
			&& chLog.getChangeLogItems().size() == 1
			&& chLog.getChangeLogItems().stream().anyMatch(item ->
				item.getChliId() == 1
				&& item.getChliFieldName().equals("InstructorVO.insert")
				&& item.getChliNewValueId() == 0
				&& item.getChliOldValueId() == 0
				&& item.getChliOldValue().equals("-")
				&& StringUtils.isNotBlank(item.getChliNewValue())
				&& StringUtils.contains(item.getChliNewValue(), "InstructorVO [")
				&& StringUtils.contains(item.getChliNewValue(), INSTRUCTOR_FIRSTNAME_NEW)
				&& StringUtils.contains(item.getChliNewValue(), INSTRUCTOR_LASTNAME_NEW)
				&& StringUtils.contains(item.getChliNewValue(), INSTRUCTOR_EMAIL_NEW)
				&& StringUtils.contains(item.getChliNewValue(), INSTRUCTOR_YOUTUBE_NEW)
				&& StringUtils.contains(item.getChliNewValue(), INSTRUCTOR_SALARY_NEW.toString())
				&& StringUtils.contains(item.getChliNewValue(), INSTRUCTOR_STATUS_NEW.label)
			)
		));
	}
	
	@Test
	void updateInstructor() {
		
		// get instructor id=1
		InstructorVO instructorVO = instructorService.findById(1);
				
		// update with new data
		instructorVO = ApplicationTestUtils.updateExistingInstructorVO(instructorVO);
		
		instructorService.saveInstructor(instructorVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again
		instructorVO = instructorService.findById(1);
		
		// assert
		validateUpdatedInstructor(instructorVO);
		
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
//		[ChangeLog [chloId=1, chloTimestamp=2024-05-15 13:41:25.976, chloUserId=INSTRUCTOR, logModule=LogModule [lmId=2, lmDescription=INSTRUCTOR, lmEntityName=org.babinkuk.entity.Instructor], chloTableId=2, 
//				changeLogItems=[ChangeLogItem [chliId=5, chliFieldName=InstructorVO.firstName.update, chliOldValueId=1, chliOldValue=firstNameInstr, chliNewValue=firstNameInstrUpdate, chliNewValueId=1], 
//		                ChangeLogItem [chliId=2, chliFieldName=InstructorVO.lastName.update, chliOldValueId=1, chliOldValue=lastNameInstr, chliNewValue=lastNameInstrUpdate, chliNewValueId=1], 
//		                ChangeLogItem [chliId=1, chliFieldName=InstructorVO.hobby.update, chliOldValueId=1, chliOldValue=test hobby, chliNewValue=hobi, chliNewValueId=1], 
//		                ChangeLogItem [chliId=4, chliFieldName=InstructorVO.email.update, chliOldValueId=1, chliOldValue=firstNameInstr@babinkuk.com, chliNewValue=InstrUpdate@babinkuk.com, chliNewValueId=1], 
//		                ChangeLogItem [chliId=7, chliFieldName=InstructorVO.status.update, chliOldValueId=1, chliOldValue=ACTIVE, chliNewValue=INACTIVE, chliNewValueId=1], 
//		                ChangeLogItem [chliId=3, chliFieldName=InstructorVO.salary.update, chliOldValueId=1, chliOldValue=1000.0, chliNewValue=500.0, chliNewValueId=1], 
//		                ChangeLogItem [chliId=6, chliFieldName=InstructorVO.youtubeChannel.update, chliOldValueId=1, chliOldValue=ytb test, chliNewValue=jutub, chliNewValueId=1]]]]
		
		assertTrue(chLogList.stream().anyMatch(chLog ->
			chLog.getChloId() == 1
			&& chLog.getChloUserId().equals(RestModule.INSTRUCTOR.getLabel())
			&& chLog.getChloTableId() == (RestModule.INSTRUCTOR.getModuleId())
			&& chLog.getLogModule().getLmId() == (RestModule.INSTRUCTOR.getModuleId())
			&& chLog.getLogModule().getLmDescription().equals(RestModule.INSTRUCTOR.getLabel())
			&& chLog.getLogModule().getLmEntityName().equals("org.babinkuk.entity.Instructor")
			&& chLog.getChangeLogItems().size() == 7
			&& chLog.getChangeLogItems().stream().anyMatch(item ->
				item.getChliId() > 0
				&& item.getChliFieldName().equals("InstructorVO.firstName.update")
				&& item.getChliNewValueId() == item.getChliOldValueId()
				&& StringUtils.isNotBlank(item.getChliOldValue())
				&& StringUtils.contains(item.getChliOldValue(), INSTRUCTOR_FIRSTNAME)
				&& StringUtils.isNotBlank(item.getChliNewValue())
				&& StringUtils.contains(item.getChliNewValue(), INSTRUCTOR_FIRSTNAME_UPDATED)
			)
			&& chLog.getChangeLogItems().stream().anyMatch(item ->
				item.getChliId() > 0
				&& item.getChliFieldName().equals("InstructorVO.lastName.update")
				&& item.getChliNewValueId() == item.getChliOldValueId()
				&& StringUtils.isNotBlank(item.getChliOldValue())
				&& StringUtils.contains(item.getChliOldValue(), INSTRUCTOR_LASTNAME)
				&& StringUtils.isNotBlank(item.getChliNewValue())
				&& StringUtils.contains(item.getChliNewValue(), INSTRUCTOR_LASTNAME_UPDATED)
			)
			&& chLog.getChangeLogItems().stream().anyMatch(item ->
				item.getChliId() > 0
				&& item.getChliFieldName().equals("InstructorVO.email.update")
				&& item.getChliNewValueId() == item.getChliOldValueId()
				&& StringUtils.isNotBlank(item.getChliOldValue())
				&& StringUtils.contains(item.getChliOldValue(), INSTRUCTOR_EMAIL)
				&& StringUtils.isNotBlank(item.getChliNewValue())
				&& StringUtils.contains(item.getChliNewValue(), INSTRUCTOR_EMAIL_UPDATED)
			)
			&& chLog.getChangeLogItems().stream().anyMatch(item ->
				item.getChliId() > 0
				&& item.getChliFieldName().equals("InstructorVO.hobby.update")
				&& item.getChliNewValueId() == item.getChliOldValueId()
				&& StringUtils.isNotBlank(item.getChliOldValue())
				&& StringUtils.contains(item.getChliOldValue(), INSTRUCTOR_HOBBY)
				&& StringUtils.isNotBlank(item.getChliNewValue())
				&& StringUtils.contains(item.getChliNewValue(), INSTRUCTOR_HOBBY_UPDATED)
			)
			&& chLog.getChangeLogItems().stream().anyMatch(item ->
				item.getChliId() > 0
				&& item.getChliFieldName().equals("InstructorVO.status.update")
				&& item.getChliNewValueId() == item.getChliOldValueId()
				&& StringUtils.isNotBlank(item.getChliOldValue())
				&& StringUtils.contains(item.getChliOldValue(), INSTRUCTOR_STATUS.label)
				&& StringUtils.isNotBlank(item.getChliNewValue())
				&& StringUtils.contains(item.getChliNewValue(), INSTRUCTOR_STATUS_UPDATED.label)
			)
			&& chLog.getChangeLogItems().stream().anyMatch(item ->
				item.getChliId() > 0
				&& item.getChliFieldName().equals("InstructorVO.salary.update")
				&& item.getChliNewValueId() == item.getChliOldValueId()
				&& StringUtils.isNotBlank(item.getChliOldValue())
				&& StringUtils.contains(item.getChliOldValue(), INSTRUCTOR_SALARY.toString())
				&& StringUtils.isNotBlank(item.getChliNewValue())
				&& StringUtils.contains(item.getChliNewValue(), INSTRUCTOR_SALARY_UPDATED.toString())
			)
		));
	}
	
	@Test
	void deleteInstructor() {
		
		// get instructor
		InstructorVO instructorVO = instructorService.findById(1);
		
		// assert
		assertNotNull(instructorVO, "return true");
		assertEquals(1, instructorVO.getId());
		
		// delete instructor
		instructorService.deleteInstructor(1);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// assert not existing instructor
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			instructorService.findById(1);
		});
		
		String expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), 1);
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));
	    
		// check other cascading entities
		// get course with id=1
		CourseVO courseVO = courseService.findById(1);
		
		// assert
		// course must be unchanged (except instructor=null)
		assertNotNull(courseVO, "courseVO null");
		assertEquals(1, courseVO.getId(), "course.getId()");
		assertEquals(COURSE, courseVO.getTitle(), "course.getTitle()");
		assertEquals(1, courseVO.getReviewsVO().size(), "course.getReviews().size()");
		assertTrue(courseVO.getReviewsVO().stream().anyMatch(review ->
			review.getComment().equals(REVIEW) && review.getId() == 1
		));
		assertNull(courseVO.getInstructorVO(), "course.getInstructor() null");
		assertEquals(1, courseVO.getStudentsVO().size(), "course.getStudents().size()");
		assertTrue(courseVO.getStudentsVO().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getId() == 2
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
		
		assertTrue(chLogList.stream().anyMatch(chLog ->
			chLog.getChloId() == 1
			&& chLog.getChloUserId().equals(RestModule.INSTRUCTOR.getLabel())
			&& chLog.getChloTableId() == (RestModule.INSTRUCTOR.getModuleId())
			&& chLog.getLogModule().getLmId() == (RestModule.INSTRUCTOR.getModuleId())
			&& chLog.getLogModule().getLmDescription().equals(RestModule.INSTRUCTOR.getLabel())
			&& chLog.getLogModule().getLmEntityName().equals("org.babinkuk.entity.Instructor")
			&& chLog.getChangeLogItems().size() == 1
			&& chLog.getChangeLogItems().stream().anyMatch(item ->
				item.getChliId() == 1
				&& item.getChliFieldName().equals("InstructorVO.delete")
				&& item.getChliNewValueId() == item.getChliOldValueId()
				&& item.getChliNewValue().equals("-")
				&& StringUtils.isNotBlank(item.getChliOldValue())
				&& StringUtils.contains(item.getChliOldValue(), "InstructorVO [")
				&& StringUtils.contains(item.getChliOldValue(), INSTRUCTOR_FIRSTNAME)
				&& StringUtils.contains(item.getChliOldValue(), INSTRUCTOR_LASTNAME)
				&& StringUtils.contains(item.getChliOldValue(), INSTRUCTOR_EMAIL)
				&& StringUtils.contains(item.getChliOldValue(), INSTRUCTOR_YOUTUBE)
				&& StringUtils.contains(item.getChliOldValue(), INSTRUCTOR_SALARY.toString())
				&& StringUtils.contains(item.getChliOldValue(), INSTRUCTOR_STATUS.label)
			)
		));
	}
	
	@Test
	void setCourse() {
		
		// get instructor
		InstructorVO instructorVO = instructorService.findById(1);
		
		validateExistingInstructor(instructorVO);
		
		// get course
		CourseVO courseVO = courseService.findById(1);
		
		// assert
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO() null");
		assertNotNull(courseVO.getStudentsVO(),"getStudentsVO() null");
		//assertNull(courseVO.getInstructorVO(),"getInstructorVO() not null");
		assertEquals(COURSE, courseVO.getTitle(),"getTitle() NOK");
		
		// set course for instructor
		instructorService.setCourse(instructorVO, courseVO, ActionType.ENROLL);

		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again
		instructorVO = instructorService.findById(1);
		
		// assert
		validateExistingInstructor(instructorVO);
		
		// assert instructor -> course
		assertEquals(1, instructorVO.getCourses().size(), "instrucors.getCourses size not 1");
		assertTrue(instructorVO.getCourses().stream().anyMatch(course ->
			course.getTitle().equals(COURSE) && course.getId() == 1
		));
		
		// get course again
		courseVO = courseService.findById(1);
		
		// assert
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO() null");
		assertNotNull(courseVO.getStudentsVO(),"getStudentsVO() null");
		assertNotNull(courseVO.getInstructorVO(),"getInstructorVO() not null");
		assertEquals(COURSE, courseVO.getTitle(),"getTitle() NOK");
		assertEquals(INSTRUCTOR_FIRSTNAME, courseVO.getInstructorVO().getFirstName(),"getTitle() NOK");
		
		// add another course
		CourseVO courseVO2 = ApplicationTestUtils.createCourseVO();
		
		courseService.saveCourse(courseVO2);
		
		courseVO2 = courseService.findByTitle(COURSE_NEW);

		// set course 
		instructorService.setCourse(instructorVO, courseVO2, ActionType.ENROLL);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again
		instructorVO = instructorService.findById(1);
		
		// assert
		validateExistingInstructor(instructorVO);
		
		// assert course
		assertEquals(2, instructorVO.getCourses().size(), "instructors.getCourses size not 2");
		assertTrue(instructorVO.getCourses().stream().anyMatch(course ->
			course.getTitle().equals(COURSE) && course.getId() == 1
		));
		assertTrue(instructorVO.getCourses().stream().anyMatch(course ->
			course.getTitle().equals(COURSE_NEW)// && course.getId() == 2
		));
		
		// get new course again
		courseVO2 = courseService.findByTitle(COURSE_NEW);
		
		// assert
		assertNotNull(courseVO2,"courseVO null");
		//assertEquals(2, courseVO2.getId());
		assertNotNull(courseVO2.getTitle(),"courseVO2() null");
		assertNotNull(courseVO2.getStudentsVO(),"getStudentsVO() null");
		assertNotNull(courseVO2.getInstructorVO(),"getInstructorVO() not null");
		assertEquals(COURSE_NEW, courseVO2.getTitle(),"getTitle() NOK");
		assertEquals(INSTRUCTOR_FIRSTNAME, courseVO2.getInstructorVO().getFirstName(),"getTitle() NOK");
		
		// now withdraw courses
		instructorService.setCourse(instructorVO, courseVO, ActionType.WITHDRAW);
		instructorService.setCourse(instructorVO, courseVO2, ActionType.WITHDRAW);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again
		instructorVO = instructorService.findById(1);
		
		// assert
		validateExistingInstructor(instructorVO);
		
		// assert course
		assertEquals(0, instructorVO.getCourses().size(), "instructor.getCourses");
		
		// get courses again
		courseVO = courseService.findById(1);
		courseVO2 = courseService.findByTitle(COURSE_NEW);

		// assert
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO() null");
		assertNotNull(courseVO.getStudentsVO(),"getStudentsVO() null");
		assertNull(courseVO.getInstructorVO(),"getInstructorVO() not null");
		assertEquals(COURSE, courseVO.getTitle(),"getTitle() NOK");
		
		assertNotNull(courseVO2,"courseVO null");
		//assertEquals(1, courseVO.getId());
		assertNotNull(courseVO2.getTitle(),"courseVO() null");
		assertNotNull(courseVO2.getStudentsVO(),"getStudentsVO() null");
		assertNull(courseVO2.getInstructorVO(),"getInstructorVO() not null");
		assertEquals(COURSE_NEW, courseVO2.getTitle(),"getTitle() NOK");
		
		// not mandatory
		// setting non existing course
		CourseVO courseVO3 = new CourseVO("non existing course");
		courseVO3.setId(3);
		
		// for avoiding Local variable instructorVO defined in an enclosing scope must be final or effectively final
		final InstructorVO insVO = instructorVO;
		
		// assert non-existing course
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			instructorService.setCourse(insVO, courseVO3, ActionType.ENROLL);
		});
		
		String expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), 3);
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	    
	    // not mandatory
 		// setting course to non existing instructor
 		// for avoiding Local variable instructorVO defined in an enclosing scope must be final or effectively final
 		final InstructorVO insVO2 = new InstructorVO("ime", "prezime", "ime@babinkuk.com");
 		insVO2.setId(22);
 		
 		final CourseVO crsVO2 = courseVO;
 		// assert non-existing instructor
 		exception = assertThrows(ObjectNotFoundException.class, () -> {
 			instructorService.setCourse(insVO2, crsVO2, ActionType.ENROLL);
 		});
 		
 		expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), 22);
		actualMessage = exception.getMessage();

 	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void testStreams() {
		InstructorVO[] instructorArray = {
			new InstructorVO("firstName1", "lastName1", "email1"),
			new InstructorVO("firstName2", "lastName2", "email2"),
			new InstructorVO("firstName3", "lastName3", "email3")
		};

		List<InstructorVO> instructorList = Arrays.asList(instructorArray);
		instructorList.stream().forEach(i -> log.info(i));
		
		assertTrue(instructorList.stream().anyMatch(i ->
			i.getEmail().equals("email3") && i.getFirstName().equals("firstName3")
		));
	}

	@Test
	void testStreamsForEach() {
		InstructorVO[] instructorArray = {
			new InstructorVO("firstName1", "lastName1", "email1"),
			new InstructorVO("firstName2", "lastName2", "email2"),
			new InstructorVO("firstName3", "lastName3", "email3")
		};

		List<InstructorVO> instructorList = Arrays.asList(instructorArray);
		instructorList.stream().forEach(i -> i.setEmail(i.getEmail() + i.getEmail()));
		
		assertTrue(instructorList.stream().anyMatch(i ->
			i.getFirstName().equals("firstName1") && i.getEmail().equals("email1email1")
		));
		assertTrue(instructorList.stream().anyMatch(i ->
			i.getFirstName().equals("firstName2") && i.getEmail().equals("email2email2")
		));
		assertTrue(instructorList.stream().anyMatch(i ->
			i.getFirstName().equals("firstName3") && i.getEmail().equals("email3email3")
		));	
	}
	
	@Test
	void testStreams_findFirst() {
		Integer[] instructorIds = {1};
		
		// map() produces a new stream after applying a function to each element of the original stream. 
		// collect(Collectors.toList() accumulate names into a List
		// The new stream could be of different type.
		List<InstructorVO> instructorList = Stream.of(instructorIds)
				.map(instructorService::findById)
				.collect(Collectors.toList());
		instructorList.stream().forEach(i -> log.info(i));
		
		// Filter element
		InstructorVO instructor = Stream.of(instructorIds)
				.map(instructorService::findById)
				.filter(i -> i.getEmail().equals(INSTRUCTOR_EMAIL_NEW))
				.findFirst()
				.orElse(null)
				;
		//log.info(instructor);
		
		assertNull(instructor, "instructor not null");
	}
	
	@Test
	void testStreams_toArray() {
		InstructorVO[] instructorArray = {
			new InstructorVO("firstName1", "lastName1", "email1"),
			new InstructorVO("firstName2", "lastName2", "email2"),
			new InstructorVO("firstName3", "lastName3", "email3")
		};

		List<InstructorVO> instructorList = Arrays.asList(instructorArray);
		instructorList.stream().forEach(i -> log.info(i));
		
		assertTrue(instructorList.stream().anyMatch(i ->
			i.getEmail().equals("email3") && i.getFirstName().equals("firstName3")
		));
		
		// The syntax InstructorVO[]::new creates an empty array of InstructorVO
		// which is then filled with elements from the stream
		InstructorVO[] instructors = instructorList.stream().toArray(InstructorVO[]::new);
		
		assertEquals(instructorList.size(), instructors.length);
	}
	
	@Test
	void whenFlatMapNames_thenGetNameStream() {
		List<List<String>> namesNested = Arrays.asList(
				Arrays.asList("firstName1", "lastName1", "email1"),
				Arrays.asList("firstName2", "lastName2", "email2"),
				Arrays.asList("firstName3", "lastName3", "email3"));
		//log.info(namesNested);
		
		// convert the Stream<List<String>> to a simpler Stream<String> – using the flatMap() API.
		List<String> namesFlatStream = namesNested.stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		log.info(namesFlatStream);
		
		assertEquals(namesFlatStream.size(), namesNested.size() * 3);
	}
	
	@Test
	void whenUsingPeek_thenApplyNewfieldValues() {
		
		InstructorVO[] instructorArray = {
			new InstructorVO("firstName1", "lastName1", "email1"),
			new InstructorVO("firstName2", "lastName2", "email2"),
			new InstructorVO("firstName3", "lastName3", "email3")
		};
		
		List<InstructorVO> instructorList = Arrays.asList(instructorArray);
		
		// peek() can be useful for performing multiple operations on each element of the stream before any terminal operation is applied.
		instructorList.stream()
			.peek(e -> e.setFirstName(e.getFirstName() + e.getFirstName()))
			.peek(e -> e.setLastName(e.getLastName() + e.getLastName()))
			.peek(log::info)
			.collect(Collectors.toList());

		assertTrue(instructorList.stream().anyMatch(i ->
			i.getLastName().equals("lastName1lastName1") && i.getFirstName().equals("firstName1firstName1")
		));	
	}
	
	@Test
	void whenLimitInfiniteStream_thenGetFiniteElements() {
		Stream<Integer> infiniteStream = Stream.iterate(2, i -> i * 2);
		
		List<Integer> collect = infiniteStream
			.skip(2)
			.limit(8)
			.collect(Collectors.toList());
		
		assertEquals(collect, Arrays.asList(8, 16, 32, 64, 128, 256, 512, 1024));
	}
	
	@Test
	void whenFindFirst_thenGetFirstInstructorInStream() {
		Integer[] ids = { 1, 2, 3, 4 };
		
		// find first with salary>500
		Optional<Instructor> instructor = Stream.of(ids)
				.map(instructorRepository::findById)
				.filter(e -> e.get() != null)
				.filter(e -> e.get().getSalary() > 500)
				.findFirst()
				.orElse(null);
		
		assertEquals(instructor.get().getSalary(), new Double(1000));
	}

	@Test
	void whenSortStream_thenGetSortedStream() {
		InstructorVO[] instructorArray = {
			new InstructorVO("Tito", "lastName2", "email2"),
			new InstructorVO("Zlata", "lastName1", "email1"),
			new InstructorVO("Gina", "lastName3", "email3")
		};

		List<InstructorVO> instructorList = Arrays.asList(instructorArray);
		instructorList.stream().forEach(i -> log.info(i));
		
		// sort by firstame
		List<InstructorVO> sortedInstructorList = instructorList.stream()
				.sorted((i1, i2) -> i1.getFirstName().compareTo(i2.getFirstName()))
				.collect(Collectors.toList());
			
		assertEquals(sortedInstructorList.get(0).getFirstName(), "Gina");
		assertEquals(sortedInstructorList.get(1).getFirstName(), "Tito");
		assertEquals(sortedInstructorList.get(2).getFirstName(), "Zlata");
	}
	
	@Test
	void whenCollectByJoining_thenGetJoinedString() {
		InstructorVO[] instructorArray = {
			new InstructorVO("firstName2", "lastName2", "email2"),
			new InstructorVO("firstName1", "lastName1", "email1"),
			new InstructorVO("firstName3", "lastName3", "email3")
		};

		List<InstructorVO> instructorList = Arrays.asList(instructorArray);
		instructorList.stream().forEach(i -> log.info(i));
		
		// create a string of all firstNames
		String instructorNames = instructorList.stream()
				 .map(InstructorVO::getFirstName)
				 .collect(Collectors.joining(", "))
				 .toString();
			    
		assertEquals(instructorNames, "firstName2, firstName1, firstName3");
	}
	
	@Test
	void whenCollectBySet_thenGetSet() {
		InstructorVO[] instructorArray = {
			new InstructorVO("firstName2", "lastName2", "email2"),
			new InstructorVO("firstName1", "lastName1", "email1"),
			new InstructorVO("firstName3", "lastName3", "email3")
		};

		List<InstructorVO> instructorList = Arrays.asList(instructorArray);
		instructorList.stream().forEach(i -> log.info(i));
		
		// set of first names
		Set<String> instructorNames = instructorList.stream()
				 .map(InstructorVO::getFirstName)
				 .collect(Collectors.toSet());
			    
		assertEquals(instructorNames.size(), 3);
	}
	
	@Test
	void whenToVectorCollection_thenGetVector() {
		InstructorVO[] instructorArray = {
			new InstructorVO("firstName2", "lastName2", "email2"),
			new InstructorVO("firstName1", "lastName1", "email1"),
			new InstructorVO("firstName3", "lastName3", "email3")
		};

		List<InstructorVO> instructorList = Arrays.asList(instructorArray);
		instructorList.stream().forEach(i -> log.info(i));
		
		Vector<String> instructorNames = instructorList.stream()
				 .map(InstructorVO::getFirstName)
				 .collect(Collectors.toCollection(Vector::new));
		log.info(instructorList);
		
		assertEquals(instructorNames.size(), 3);
	}
	
	@Test
	void whenApplyMatch_thenReturnBoolean() {
		List<Integer> intList = Arrays.asList(2, 4, 5, 6, 8);
		
		boolean allEven = intList.stream().allMatch(i -> i % 2 == 0); // check if true for all elements 
		boolean oneEven = intList.stream().anyMatch(i -> i % 2 == 0); // check if true for any element
		boolean noneMultipleOfThree = intList.stream().noneMatch(i -> i % 3 == 0); // check if no element is matching this predicate
		
		assertEquals(allEven, false);
		assertEquals(oneEven, true);
		assertEquals(noneMultipleOfThree, false);
	}
	
	@Test
	void whenStreamPartition_thenGetMap() {
		List<Integer> intList = Arrays.asList(2, 4, 5, 6, 8);
		
		// the stream is partitioned into a Map, with even and odds stored as true and false keys.
		Map<Boolean, List<Integer>> isEven = intList.stream()
				.collect(Collectors.partitioningBy(i -> i % 2 == 0));
		
		for (Boolean key : isEven.keySet()) {
	        log.info(key + ":" + isEven.get(key));
	    }
		
		assertEquals(isEven.get(true).size(), 4);
		assertEquals(isEven.get(false).size(), 1);
	}
	
	@Test
	void whenStreamGroupingBy_thenGetMap() {
		
		InstructorVO[] instructorArray = {
			new InstructorVO("firstName2", "lastName2", "email2"),
			new InstructorVO("firstName1", "lastName1", "email1"),
			new InstructorVO("firstName3", "lastName3", "email3")
		};
		
		// grouping based on the last character in the firstName field (1, 2, 3)
		Map<Character, List<InstructorVO>> groupByAlphabet = Arrays.asList(instructorArray).stream()
				.collect(Collectors.groupingBy(e -> Character.valueOf(e.getFirstName().charAt(9))));
		
		for (Character key : groupByAlphabet.keySet()) {
	        log.info(key + ":" + groupByAlphabet.get(key));
	    }
		
	    assertEquals(groupByAlphabet.get('1').get(0).getFirstName(), "firstName1");
	    assertEquals(groupByAlphabet.get('2').get(0).getFirstName(), "firstName2");
	    assertEquals(groupByAlphabet.get('3').get(0).getFirstName(), "firstName3");
	}
	
	@Test
	void whenStreamMapping_thenGetMap() {
		
		InstructorVO[] instructorArray = {
			new InstructorVO("firstName2", "lastName2", "email2"),
			new InstructorVO("firstName1", "lastName1", "email1"),
			new InstructorVO("firstName3", "lastName3", "email3")
		};
		
		// maps the stream element InstructorVO into just the instructor id (Integer) using the getId() mapping function. 
		// These ids are still grouped based on the initial character of employee first name.
		Map<Character, List<Integer>> idGroupedByAlphabet = Arrays.asList(instructorArray).stream()
				.collect(
					Collectors.groupingBy(e -> Character.valueOf(e.getFirstName().charAt(9)), 
					Collectors.mapping(InstructorVO::getId, Collectors.toList())));
		
		for (Character key : idGroupedByAlphabet .keySet()) {
	        log.info(key + ":" + idGroupedByAlphabet .get(key));
	    }
		
	    assertEquals(idGroupedByAlphabet.get('1').get(0), 0);
	    assertEquals(idGroupedByAlphabet.get('2').get(0), 0);
	    assertEquals(idGroupedByAlphabet.get('3').get(0), 0);
	}
	
	@Test
	void whenStreamGroupingAndReducing_thenGetMap() {
		
		InstructorVO[] instructorArray = {
			new InstructorVO("Toninho", "lastName2", "email2"),
			new InstructorVO("Tompa", "lastName3", "email3"),
			new InstructorVO("Maroni", "lastName1", "email1"),
			new InstructorVO("Maki", "lastName4", "email4")
		};
		
		Comparator<InstructorVO> byNameLength = Comparator.comparing(InstructorVO::getFirstName);
		
		// group the instructors based on the initial character of their first name
		// Within each group, find the instructor with the longest name
		Map<Character, Optional<InstructorVO>> longestNameByAlphabet = Arrays.asList(instructorArray).stream()
				.collect(
					Collectors.groupingBy(e -> Character.valueOf(e.getFirstName().charAt(0)),
					Collectors.reducing(BinaryOperator.maxBy(byNameLength))));
		
		for (Character key : longestNameByAlphabet.keySet()) {
	        log.info(key + ":" + longestNameByAlphabet.get(key));
	    }
		
	    assertEquals("Toninho", longestNameByAlphabet.get('T').get().getFirstName());
	    assertEquals("Maroni", longestNameByAlphabet.get('M').get().getFirstName());
	}
	
	private void validateExistingInstructor(InstructorVO instructorVO) {
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"getFirstName() null");
		assertNotNull(instructorVO.getLastName(),"getLastName() null");
		assertNotNull(instructorVO.getEmail(),"getEmail() null");
		assertNotNull(instructorVO.getSalary(),"getSalary() null");
		assertNotNull(instructorVO.getStatus(),"getStatus() null");
		assertNotNull(instructorVO.getImages(),"getImages() null");
		assertNotNull(instructorVO.getYoutubeChannel(),"getYoutubeChannel() null");
		assertNotNull(instructorVO.getHobby(),"getHobby() null");
		assertEquals(INSTRUCTOR_FIRSTNAME, instructorVO.getFirstName(),"getFirstName() NOK");
		assertEquals(INSTRUCTOR_LASTNAME, instructorVO.getLastName(),"getLastName() NOK");
		assertEquals(INSTRUCTOR_EMAIL, instructorVO.getEmail(),"getEmail() NOK");
		assertEquals(1000, instructorVO.getSalary(),"getSalary() NOK");
		assertEquals(Status.ACTIVE, instructorVO.getStatus(),"getStatus() NOK");
		assertEquals(1, instructorVO.getImages().size(), "getImages size not 1");
		//assertThat(instructorVO.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_1, INSTRUCTOR_IMAGE_1));
		//assertThat(instructorVO.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_11, INSTRUCTOR_IMAGE_11));
		assertEquals(INSTRUCTOR_YOUTUBE, instructorVO.getYoutubeChannel(),"getYoutubeChannel() NOK");
		assertEquals(INSTRUCTOR_HOBBY, instructorVO.getHobby(),"getHobby() NOK");
		// not neccessary
		assertNotEquals("test hobb", instructorVO.getHobby(),"getHobby() NOK");
		
		// assert non-existing instructor
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			instructorService.findById(22);
			
		});
		
		String expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), 22);
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	private void validateUpdatedInstructor(InstructorVO instructorVO) {
		
		assertNotNull(instructorVO,"instructor null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"getFirstName() null");
		assertNotNull(instructorVO.getLastName(),"getLastName() null");
		assertNotNull(instructorVO.getEmail(),"getEmail() null");
		assertNotNull(instructorVO.getSalary(),"getSalary() null");
		assertNotNull(instructorVO.getStatus(),"getStatus() null");
		assertNotNull(instructorVO.getImages(),"getImages() null");
		assertNotNull(instructorVO.getYoutubeChannel(),"getYoutubeChannel() null");
		assertNotNull(instructorVO.getHobby(),"getHobby() null");
		assertEquals(INSTRUCTOR_FIRSTNAME_UPDATED, instructorVO.getFirstName(),"getFirstName() NOK");
		assertEquals(INSTRUCTOR_LASTNAME_UPDATED, instructorVO.getLastName(),".getLastName() NOK");
		assertEquals(INSTRUCTOR_EMAIL_UPDATED, instructorVO.getEmail(),"getEmail() NOK");
		assertEquals(INSTRUCTOR_SALARY_UPDATED, instructorVO.getSalary(),"getSalary() NOK");
		assertEquals(INSTRUCTOR_STATUS_UPDATED, instructorVO.getStatus(),"getStatus() NOK");
		assertEquals(1, instructorVO.getImages().size(), "getImages size not 1");
		//assertThat(instructorVO.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_1, INSTRUCTOR_IMAGE_1));
		//assertThat(instructorVO.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_11, INSTRUCTOR_IMAGE_11));
		//assertThat(instructorVO.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_UPDATED, INSTRUCTOR_IMAGE_UPDATED));
		assertTrue(instructorVO.getImages().stream().anyMatch(img ->
			img.getFileName().equals(FILE_1) && img.getId() == 1
		));
		assertEquals(INSTRUCTOR_YOUTUBE_UPDATED, instructorVO.getYoutubeChannel(),"getYoutubeChannel() NOK");
		assertEquals(INSTRUCTOR_HOBBY_UPDATED, instructorVO.getHobby(),"getHobby() NOK");
		// not neccessary
		assertNotEquals("test hobb", instructorVO.getHobby(),"getHobby() NOK");
	}
	
	private void validateNewInstructor(InstructorVO instructorVO) {
		
		assertNotNull(instructorVO,"instructor null");
		//assertEquals(1, instructor.getId());
		assertNotNull(instructorVO.getFirstName(),"getFirstName() null");
		assertNotNull(instructorVO.getLastName(),"getLastName() null");
		assertNotNull(instructorVO.getEmail(),"getEmail() null");
		assertNotNull(instructorVO.getSalary(),"getSalary() null");
		assertNotNull(instructorVO.getStatus(),"getStatus() null");
		assertNotNull(instructorVO.getImages(),"getImages() null");
		assertNotNull(instructorVO.getYoutubeChannel(),"getYoutubeChannel() null");
		assertNotNull(instructorVO.getHobby(),"getHobby() null");
		assertEquals(INSTRUCTOR_FIRSTNAME_NEW, instructorVO.getFirstName(),"getFirstName() NOK");
		assertEquals(INSTRUCTOR_LASTNAME_NEW, instructorVO.getLastName(),"getLastName() NOK");
		assertEquals(INSTRUCTOR_EMAIL_NEW, instructorVO.getEmail(),"getEmail() NOK");
		assertEquals(INSTRUCTOR_SALARY_NEW, instructorVO.getSalary(),"getSalary() NOK");
		assertEquals(INSTRUCTOR_STATUS_NEW, instructorVO.getStatus(),"getStatus() NOK");
		assertEquals(0, instructorVO.getImages().size(), "getImages size not 0");
		//assertThat(instructorVO.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_NEW, INSTRUCTOR_IMAGE_NEW));
		assertEquals(INSTRUCTOR_YOUTUBE_NEW, instructorVO.getYoutubeChannel(),"getYoutubeChannel() NOK");
		assertEquals(INSTRUCTOR_HOBBY_NEW, instructorVO.getHobby(),"getHobby() NOK");
		// not neccessary
		assertNotEquals("test hobb", instructorVO.getHobby(),"getHobby() NOK");
	}
}