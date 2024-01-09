package org.babinkuk.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.MessagePool;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.vo.StudentVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.babinkuk.utils.ApplicationTestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.babinkuk.config.Api.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
public class StudentValidatorTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(StudentValidatorTest.class);
	
	@Test
	void addEmptyStudentRoleAdmin() throws Exception {

		addEmptyStudent(ROLE_ADMIN);
	}
	
	@Test
	void addEmptyStudentRoleInstructor() throws Exception {

		addEmptyStudent(ROLE_INSTRUCTOR);
	}

	private void addEmptyStudent(String validationRole) throws Exception {
		
		// create invalid student (empty fields)
		StudentVO studentVO = new StudentVO();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(4))) // verify that json root element $ is size 4
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_EMAIL_EMPTY.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STATUS_INVALID.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}

	@Test
	void addStudentInvalidEmailRoleAdmin() throws Exception {

		addStudentInvalidEmail(ROLE_ADMIN);
	}
	
	@Test
	void addStudentInvalidEmailRoleIstructor() throws Exception {

		addStudentInvalidEmail(ROLE_INSTRUCTOR);
	}
	
	private void addStudentInvalidEmail(String validationRole) throws Exception {
		
		// create invalid student (empty fields)
		StudentVO studentVO = new StudentVO();
		String emailAddress = "this is invalid email";
		studentVO.setEmail(emailAddress);
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(4))) // verify that json root element $ is size 4
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_EMAIL_INVALID.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STATUS_INVALID.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is now size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}
	
	@Test
	void addStudentEmailNotUniqueRoleAdmin() throws Exception {

		addStudentEmailNotUnique(ROLE_ADMIN);
	}
	
	@Test
	void addStudentEmailNotUniqueRoleInstructor() throws Exception {

		addStudentEmailNotUnique(ROLE_INSTRUCTOR);
	}
	
	private void addStudentEmailNotUnique(String validationRole) throws Exception {
		
		// create student
		StudentVO studentVO = ApplicationTestUtils.createStudentVO();
		// this email already exists in db
		String emailAddress = STUDENT_EMAIL;
		studentVO.setEmail(emailAddress);
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_EMAIL_ALREADY_EXIST.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all students
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is now size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}
	
	@Test
	void updateStudentInvalidIdRoleAdmin() throws Exception {

		updateStudentInvalidId(ROLE_ADMIN);
	}
	
	@Test
	void updateStudentInvalidIdRoleInstructor() throws Exception {

		updateStudentInvalidId(ROLE_INSTRUCTOR);
	}
	
	private void updateStudentInvalidId(String validationRole) throws Exception {
		
		int id = 22;
		
		// check if student id=22 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), id)))) // verify json element
			;
		
		// create invalid student 
		StudentVO studentVO = ApplicationTestUtils.createStudentVO();
		studentVO.setId(id);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), id)))) // verify json element
			;
	}
	
	@Test
	void updateEmptyStudentRoleAdmin() throws Exception {

		updateEmptyStudent(ROLE_ADMIN);
	}
	
	@Test
	void updateEmptyStudentRoleInstructor() throws Exception {

		updateEmptyStudent(ROLE_INSTRUCTOR);
	}
	
	private void updateEmptyStudent(String validationRole) throws Exception {
		
		int id = 2;
		
		// check if student id=2 exists
		StudentVO studentVO = studentService.findById(id);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(id, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"getFirstName() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"getFirstName() failure");
		
		// update student
		studentVO.setFirstName("");
		studentVO.setLastName("");
		studentVO.setEmail("");
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(3))) // verify that json root element $ is size 3
	        .andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_EMAIL_EMPTY.getMessage()), ActionType.CREATE))))
			;
	}
	
	@Test
	void updateStudentInvalidEmailRoleAdmin() throws Exception {

		updateStudentInvalidEmail(ROLE_ADMIN);
	}
	
	@Test
	void updateStudentInvalidEmailRoleInstructor() throws Exception {

		updateStudentInvalidEmail(ROLE_INSTRUCTOR);
	}
	
	private void updateStudentInvalidEmail(String validationRole) throws Exception {
		
		int id = 2;
		
		// check if student id=2 exists
		StudentVO studentVO = studentService.findById(id);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(id, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"getFirstName() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"getFirstName() failure");
		
		// update student
		studentVO.setEmail("this is invalid email");
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
	        .andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_EMAIL_INVALID.getMessage()), ActionType.CREATE))))
			;
	}
	
	@Test
	void updateStudentEmailNotUniqueRoleAdmin() throws Exception {
		
		updateStudentEmailNotUnique(ROLE_ADMIN);
	}
	
	@Test
	void updateStudentEmailNotUniqueRoleInstructor() throws Exception {
		
		updateStudentEmailNotUnique(ROLE_INSTRUCTOR);
	}
	
	private void updateStudentEmailNotUnique(String validationRole) throws Exception {
		
		int id = 2;
		
		// check if student id=2 exists
		StudentVO studentVO = studentService.findById(id);
		
		assertNotNull(studentVO,"studentVO null");
		assertEquals(id, studentVO.getId());
		assertNotNull(studentVO.getFirstName(),"getFirstName() null");
		assertEquals(STUDENT_FIRSTNAME, studentVO.getFirstName(),"getFirstName() failure");
		
		// create new student
		StudentVO newStudentVO = ApplicationTestUtils.createStudentVO();
		
		// save new student
		studentService.saveStudent(newStudentVO);
		
		// check if new student exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is size 2
			;
		
		StudentVO dbNewStudentVO = studentService.findByEmail(newStudentVO.getEmail());
		
		assertNotNull(dbNewStudentVO,"dbNewStudentVO null");
		//assertEquals(1, dbNewStudentVO.getId());
		assertNotNull(dbNewStudentVO.getFirstName(),"dbNewStudentVO.getFirstName() null");
		assertEquals(dbNewStudentVO.getFirstName(), dbNewStudentVO.getFirstName(),"dbNewStudentVO.getFirstName() failure");
		assertEquals(dbNewStudentVO.getEmail(), dbNewStudentVO.getEmail(),"dbNewStudentVO.getEmail() failure");
		
		// update student email (value belong to other instructor id 2)
		dbNewStudentVO.setEmail(studentVO.getEmail());
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(dbNewStudentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ size
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_EMAIL_ALREADY_EXIST.getMessage()), ActionType.CREATE))))
			;
	}
	
	@Test
	void updateStudentNotExistRoleAdmin() throws Exception {
		
		updateStudentNotExist(ROLE_ADMIN);
	}
	
	@Test
	void updateStudentNotExistRoleInstructor() throws Exception {
		
		updateStudentNotExist(ROLE_INSTRUCTOR);
	}
	
	private void updateStudentNotExist(String validationRole) throws Exception {
		
		int id = 22;
		
		// get student with id=22 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), id)))) // verify json element
			;
		
		// create new student
		StudentVO studentVO = ApplicationTestUtils.createStudentVO();
		studentVO.setId(id);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + STUDENTS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), id)))) // verify json element
			;
	}
}