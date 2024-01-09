package org.babinkuk.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.MessagePool;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.StudentVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import static org.babinkuk.config.Api.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
public class StudentControllerTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(StudentControllerTest.class);
	
	@Test
	void getAllStudents() throws Exception {

		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			;

		// add another student
		StudentVO studentVO = ApplicationTestUtils.createStudentVO();
		
		studentService.saveStudent(studentVO);
				
		// get all students (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all students (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all students (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
			//	.param(TestUtils.VALIDATION_ROLE, "TestUtils.ROLE_STUDENT")
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all students (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
	}
	
	@Test
	void getStudentRoleAdmin() throws Exception {

		getStudent(ROLE_ADMIN);
	}
	
	@Test
	void getStudentRoleInstructor() throws Exception {

		getStudent(ROLE_INSTRUCTOR);
	}
	
	@Test
	void getStudentRoleStudent() throws Exception {

		getStudent(ROLE_STUDENT);
	}
	
	@Test
	void getStudentNoRole() throws Exception {

		getStudent("");
	}
	
	@Test
	void getStudentRoleNotExist() throws Exception {

		getStudent(ROLE_NOT_EXIST);
	}
	
	private void getStudent(String validationRole) throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// get student with id=2
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(2))) // verify json root element id is 2
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME))) // verify json element
			;

		// get student with id=22 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 22)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
	}
	
	@Test
	void addStudentRoleAdmin() throws Exception {
	
		addStudentSuccess(ROLE_ADMIN);
	}

	@Test
	void addStudentRoleInstructor() throws Exception {

		addStudentSuccess(ROLE_INSTRUCTOR);
	}
	
	private void addStudentSuccess(String validationRole) throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// create student
		StudentVO studentVO = ApplicationTestUtils.createStudentVO();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(STUDENT_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// additional check
		studentVO = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		assertNotNull(studentVO,"studentVO null");
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertNotNull(studentVO.getLastName(),"studentVO.getLastName() null");
		assertNotNull(studentVO.getEmail(),"studentVO.getEmail() null");
		assertEquals(STUDENT_FIRSTNAME_NEW, studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_NEW, studentVO.getLastName(),"studentVO.getLastName() NOK");
		assertEquals(STUDENT_STATUS_NEW, studentVO.getStatus(),"studentVO.getStatus() NOK");
		assertEquals(STUDENT_STREET_NEW, studentVO.getStreet(),"studentVO.getStreet() NOK");
		assertEquals(STUDENT_CITY_NEW, studentVO.getCity(),"studentVO.getCity() NOK");
		assertEquals(STUDENT_ZIPCODE_NEW, studentVO.getZipCode(),"studentVO.getZipCode() NOK");
	}
	
	@Test
	void addStudentRoleStudent() throws Exception {

		addStudentFail(ROLE_STUDENT);
	}
	
	@Test
	void addStudentNoRole() throws Exception {

		addStudentFail("");
	}
	
	private void addStudentFail(String validationRole) throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// create student
		StudentVO studentVO = ApplicationTestUtils.createStudentVO();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is still size 1
			;
	}
	
	@Test
	void addStudentRoleNotExist() throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		String validationRole = ROLE_NOT_EXIST;
		
		// create student
		StudentVO studentVO = ApplicationTestUtils.createStudentVO();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is stil size 1
			;
	}
	
	@Test
	void updateStudentRoleAdmin() throws Exception {

		updateStudentSuccess(ROLE_ADMIN);
	}
	
	@Test
	void updateStudentRoleInstructor() throws Exception {

		updateStudentSuccess(ROLE_INSTRUCTOR);
	}
	
	private void updateStudentSuccess(String validationRole) throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// check if student id 2 exists
		StudentVO studentVO = studentService.findById(2);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(2, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"studentVO.getFirstName() failure");
		
		// update student
		studentVO = ApplicationTestUtils.updateExistingStudentVO(studentVO);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(STUDENT_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get student with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(2))) // verify json root element id is 2
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME_UPDATED))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME_UPDATED))) // verify json element
			.andExpect(jsonPath("$.email", is(STUDENT_EMAIL_UPDATED))) // verify json element
			.andExpect(jsonPath("$.status", is(STUDENT_STATUS_UPDATED.label))) // verify json element
			.andExpect(jsonPath("$.street", is(STUDENT_STREET_UPDATED))) // verify json element
			.andExpect(jsonPath("$.city", is(STUDENT_CITY_UPDATED))) // verify json element
			.andExpect(jsonPath("$.zipCode", is(STUDENT_ZIPCODE_UPDATED))) // verify json element
			.andExpect(jsonPath("$.images", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.images[0].id", is(2))) // verify json element
			.andExpect(jsonPath("$.images[0].fileName", is(FILE_2))) // verify json element
			;
	}
	
	@Test
	void updateStudentRoleStudent() throws Exception {

		updateStudentFail(ROLE_STUDENT);
	}
	
	@Test
	void updateStudentNoRole() throws Exception {

		updateStudentFail(null);
	}
	
	@Test
	void updateStudentRoleNotExist() throws Exception {

		updateStudentFail(ROLE_NOT_EXIST);
	}
	
	private void updateStudentFail(String validationRole) throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// check if student id 2 exists
		StudentVO studentVO = studentService.findById(2);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(2, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"studentVO.getFirstName() failure");
		
		// update student
		studentVO = ApplicationTestUtils.updateExistingStudentVO(studentVO);
						
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// additional check
		// get student with id=2
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(2))) // verify json root element id is 2
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME))) // verify json element
			.andExpect(jsonPath("$.email", is(STUDENT_EMAIL))) // verify json element
			;
	}
	
	@Test
	void deleteStudentRoleAdmin() throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		String validationRole = ROLE_ADMIN;
		
		// check if student id 2 exists
		int id = 2;
		StudentVO studentVO = studentService.findById(id);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(id, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"studentVO.getFirstName() failure");
				
		// delete student
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + STUDENTS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(STUDENT_DELETE_SUCCESS)))) // verify json element
			;
		
		// get student with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), id)))) //verify json element
			;
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id=1
			.andExpect(jsonPath("$.firstName", is(INSTRUCTOR_FIRSTNAME))) // verify json element
			;
		
		// get course with id=1 (validationRole ROLE_INSTRUCTOR)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 1)
				.param(VALIDATION_ROLE, ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.title", is(COURSE))) // verify json element
			.andExpect(jsonPath("$.reviewsVO", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.reviewsVO[0].comment", is(REVIEW)))
			.andExpect(jsonPath("$.studentsVO").doesNotExist()) // verify json element
			;
		
		// get review with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.comment", is(REVIEW))) // verify json element
			;
	}
	
	@Test
	void deleteStudentRoleInstructor() throws Exception {

		deleteStudentFail(ROLE_INSTRUCTOR);
	}
	
	@Test
	void deleteStudentRoleStudent() throws Exception {

		deleteStudentFail(ROLE_STUDENT);
	}

	@Test
	void deleteStudentNoRole() throws Exception {

		deleteStudentFail("");
	}
	
	@Test
	void deleteStudentRoleNotExist() throws Exception {

		deleteStudentFail(ROLE_NOT_EXIST);
	}
	
	private void deleteStudentFail(String validationRole) throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// check if student id 2 exists
		int id = 2;
		StudentVO studentVO = studentService.findById(id);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(id, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"getFirstName() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"getFirstName() failure");
				
		// delete student
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + STUDENTS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}
	
	@Test
	void enrollCourseRoleAdmin() throws Exception {

		enrollCourseSuccess(ROLE_ADMIN);
	}

	@Test
	void enrollCourseRoleInstructor() throws Exception {

		enrollCourseSuccess(ROLE_INSTRUCTOR);
	}
	
	/**
	 * testing scenario
	 * 1. validate existing course and student
	 * 2. create new student
	 * 3. associate new student with course
	 * 4. validate
	 */
	private void enrollCourseSuccess(String validationRole) throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		
		validateStudent();
		
		validateCourse(courseVO);
		
		// assert student list
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// create student
		StudentVO studentVO = ApplicationTestUtils.createStudentVO();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(STUDENT_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// additional check
		studentVO = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		assertNotNull(studentVO,"studentVO null");
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertNotNull(studentVO.getLastName(),"studentVO.getLastName() null");
		assertNotNull(studentVO.getEmail(),"studentVO.getEmail() null");
		assertEquals(STUDENT_FIRSTNAME_NEW, studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_NEW, studentVO.getLastName(),"studentVO.getLastName() NOK");
		assertEquals(STUDENT_STATUS_NEW, studentVO.getStatus(),"studentVO.getStatus() NOK");
		assertEquals(STUDENT_STREET_NEW, studentVO.getStreet(),"studentVO.getStreet() NOK");
		assertEquals(STUDENT_CITY_NEW, studentVO.getCity(),"studentVO.getCity() NOK");
		assertEquals(STUDENT_ZIPCODE_NEW, studentVO.getZipCode(),"studentVO.getZipCode() NOK");
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", studentVO.getId())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(studentVO.getId()))) // verify json root element
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.coursesVO").doesNotExist()) // verify that json root element
			;
		
		// set course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, studentVO.getId(), id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(STUDENT_SAVE_SUCCESS)))) // verify json element
			;
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", studentVO.getId())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(studentVO.getId()))) // verify json root element
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.coursesVO", hasSize(1))) // verify that json root element
			;
		
		// set course (non existing course)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, studentVO.getId(), 22)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
				
		// set course (non existing student)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, 22, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
	}
	
	@Test
	void enrollCourseRoleStudent() throws Exception {

		enrollCourseFail(ROLE_STUDENT);
	}
	
	@Test
	void enrollCourseNoRole() throws Exception {

		enrollCourseFail(null);
	}
	
	/**
	 * testing scenario
	 * 1. validate existing course and student
	 * 2. create new student
	 * 3. associate new student with course
	 * 4. validate
	 */
	private void enrollCourseFail(String validationRole) throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		
		validateStudent();
		
		validateCourse(courseVO);
		
		// assert student list
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// create student
		StudentVO studentVO = ApplicationTestUtils.createStudentVO();
		
		// save student
		studentService.saveStudent(studentVO);
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// additional check
		studentVO = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		assertNotNull(studentVO,"studentVO null");
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertNotNull(studentVO.getLastName(),"studentVO.getLastName() null");
		assertNotNull(studentVO.getEmail(),"studentVO.getEmail() null");
		assertEquals(STUDENT_FIRSTNAME_NEW, studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_NEW, studentVO.getLastName(),"studentVO.getLastName() NOK");
		assertEquals(STUDENT_STATUS_NEW, studentVO.getStatus(),"studentVO.getStatus() NOK");
		assertEquals(STUDENT_STREET_NEW, studentVO.getStreet(),"studentVO.getStreet() NOK");
		assertEquals(STUDENT_CITY_NEW, studentVO.getCity(),"studentVO.getCity() NOK");
		assertEquals(STUDENT_ZIPCODE_NEW, studentVO.getZipCode(),"studentVO.getZipCode() NOK");
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", studentVO.getId())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(studentVO.getId()))) // verify json root element
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.coursesVO").doesNotExist()) // verify that json root element
			;
		
		// set course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, studentVO.getId(), id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.ENROLL)))) // verify json root element message
			;
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", studentVO.getId())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(studentVO.getId()))) // verify json root element
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.coursesVO").doesNotExist()) // verify that json root element
			;
		
		// set course (non existing course)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, studentVO.getId(), 22)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
				
		// set course (non existing student)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, 22, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
	}
	
	@Test
	void enrollCourseRoleNotExist() throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		String validationRole = ROLE_NOT_EXIST;
		
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		
		validateStudent();
		
		validateCourse(courseVO);
		
		// assert student list
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// create student
		StudentVO studentVO = ApplicationTestUtils.createStudentVO();
		
		// save student
		studentService.saveStudent(studentVO);
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// additional check
		studentVO = studentService.findByEmail(STUDENT_EMAIL_NEW);
		
		assertNotNull(studentVO,"studentVO null");
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertNotNull(studentVO.getLastName(),"studentVO.getLastName() null");
		assertNotNull(studentVO.getEmail(),"studentVO.getEmail() null");
		assertEquals(STUDENT_FIRSTNAME_NEW, studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_NEW, studentVO.getLastName(),"studentVO.getLastName() NOK");
		assertEquals(STUDENT_STATUS_NEW, studentVO.getStatus(),"studentVO.getStatus() NOK");
		assertEquals(STUDENT_STREET_NEW, studentVO.getStreet(),"studentVO.getStreet() NOK");
		assertEquals(STUDENT_CITY_NEW, studentVO.getCity(),"studentVO.getCity() NOK");
		assertEquals(STUDENT_ZIPCODE_NEW, studentVO.getZipCode(),"studentVO.getZipCode() NOK");
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", studentVO.getId())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(studentVO.getId()))) // verify json root element id
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.coursesVO").doesNotExist()) // verify that json root element
			;
		
		// set course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, studentVO.getId(), id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", studentVO.getId())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(studentVO.getId()))) // verify json root element id
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME_NEW))) // verify json element
			.andExpect(jsonPath("$.coursesVO").doesNotExist()) // verify that json root element
			;
		
		// set course (non existing course)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, studentVO.getId(), 22)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
		
		// set course (non existing student)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_ENROLL, 22, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
	}
	
	@Test
	void withdrawCourseRoleAdmin() throws Exception {

		withdrawCourseSuccess(ROLE_ADMIN);
	}
	
	@Test
	void withdrawCourseRoleInstructor() throws Exception {

		withdrawCourseSuccess(ROLE_INSTRUCTOR);
	}
	
	/**
	 * testing scenario
	 * 1. validate existing course and student
	 * 2. withdraw new student from course
	 * 3. validate
	 */
	private void withdrawCourseSuccess(String validationRole) throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		
		validateStudent();
		
		validateCourse(courseVO);
		
		// assert student list
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// set course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 2, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(STUDENT_SAVE_SUCCESS)))) // verify json element
			;
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(2))) // verify json root element
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME))) // verify json element
			.andExpect(jsonPath("$.coursesVO").doesNotExist()) // verify that json root element
			;
		
		// set course (non existing course)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 2, 22)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
				
		// set course (non existing student)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 22, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
	}
	
	@Test
	void withdrawCourseRoleStudent() throws Exception {

		withdrawCourseFail(ROLE_STUDENT);
	}
	
	@Test
	void withdrawCourseNoRole() throws Exception {

		withdrawCourseFail(null);
	}
	
	/**
	 * testing scenario
	 * 1. validate existing course and student
	 * 2. withdraw new student from course
	 * 3. validate
	 */
	private void withdrawCourseFail(String validationRole) throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		
		validateStudent();
		
		validateCourse(courseVO);
		
		// assert student list
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// set course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 2, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.WITHDRAW)))) // verify json root element message
			;
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(2))) // verify json root element
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME))) // verify json element
			.andExpect(jsonPath("$.coursesVO", hasSize(1))) // verify that json root element
			;
		
		// set course (non existing course)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 2, 22)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
				
		// set course (non existing student)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 22, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
	}
	
	@Test
	void withdrawCourseRoleNotExist() throws Exception {
		
		String validationRole = ROLE_NOT_EXIST;
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		
		validateStudent();
		
		validateCourse(courseVO);
		
		// assert student list
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// set course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 2, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
		
		// check student
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(2))) // verify json root element
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME))) // verify json element
			.andExpect(jsonPath("$.coursesVO", hasSize(1))) // verify that json root element
			;
		
		// set course (non existing course)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 2, 22)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
				
		// set course (non existing student)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS + STUDENT_WITHDRAW, 22, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
	}
	
	@Test
	void uploadImageRoleAdmin() throws Exception {

		uploadImageSuccess(ROLE_ADMIN);
	}

	@Test
	void uploadImageRoleInstructor() throws Exception {

		uploadImageSuccess(ROLE_INSTRUCTOR);
	}
	
	@Test
	void uploadImageRoleStudent() throws Exception {

		uploadImageSuccess(ROLE_STUDENT);
	}
	
	@Test
	void uploadImageNoRole() throws Exception {

		uploadImageSuccess(null);
	}
	
	private void uploadImageSuccess(String validationRole) throws Exception {
		
		validateStudent();
		
		// create mock file
		MockMultipartFile file = ApplicationTestUtils.createMultipartFile();
				
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// upload file
		mockMvc.perform(MockMvcRequestBuilders.multipart(ROOT + STUDENTS + "/{id}" + "/upload", 2)
				.file(FILE, file.getBytes())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(IMAGE_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get all files
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + IMAGES)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(3))) // verify that json root element $ is now size 3
			.andExpect(jsonPath("$[0].fileName", is(FILE_1)))
			.andExpect(jsonPath("$[1].fileName", is(FILE_2)))
			.andExpect(jsonPath("$[2].fileName", is(DEFAULT)))
			;
		
		// get student with id=2
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(2))) // verify json root element id is 2
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME))) // verify json element
			.andExpect(jsonPath("$.images", hasSize(2))) // verify json element
			;
		
		// upload file (non existing student)
		mockMvc.perform(MockMvcRequestBuilders.multipart(ROOT + STUDENTS + "/{id}" + "/upload", 22)
				.file(FILE, file.getBytes())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
	}
	
	@Test
	void uploadImageRoleNotExist() throws Exception {
		
		validateStudent();
		
		String validationRole = ROLE_NOT_EXIST;
		
		// create mock file
		MockMultipartFile file = ApplicationTestUtils.createMultipartFile();
				
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// upload file
		mockMvc.perform(MockMvcRequestBuilders.multipart(ROOT + STUDENTS + "/{id}" + "/upload", 2)
				.file(FILE, file.getBytes())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
	}
	
	private void validateStudent() {

		StudentVO studentVO = studentService.findById(2);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(2, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
		assertNotNull(studentVO.getLastName(),"studentVO.getLastName() null");
		assertNotNull(studentVO.getEmail(),"studentVO.getEmail() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"studentVO.getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME, studentVO.getLastName(),"studentVO.getLastName() NOK");
		assertEquals(STUDENT_EMAIL, studentVO.getEmail(),"studentVO.getEmail() NOK");
		assertEquals(STUDENT_STREET, studentVO.getStreet(),"studentVO.getStreet() NOK");
		assertEquals(STUDENT_CITY, studentVO.getCity(),"studentVO.getCity() NOK");
		assertEquals(STUDENT_ZIPCODE, studentVO.getZipCode(),"studentVO.getZipCode() NOK");
		assertEquals(STUDENT_STATUS, studentVO.getStatus(),"studentVO.getStatus() NOK");
		assertEquals(1, studentVO.getImages().size(), "studentVO.getImages size not 2");
		assertTrue(studentVO.getImages().stream().anyMatch(image ->
			image.getFileName().equals(FILE_2) && image.getId() == 2
		));
		assertEquals(1, studentVO.getCoursesVO().size(), "studentVO.getCourses size not 1");
		assertTrue(studentVO.getCoursesVO().stream().anyMatch(course ->
			course.getTitle().equals(COURSE) && course.getId() == 1
		));
	}
	
	private void validateCourse(CourseVO courseVO) {
		
		// check if course id=1 exists
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		assertEquals(COURSE, courseVO.getTitle(),"assertEquals courseVO.getTitle() failure");
		assertNotNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		//assertEquals(1, courseVO.getStudentsVO().size());
	}	
}
