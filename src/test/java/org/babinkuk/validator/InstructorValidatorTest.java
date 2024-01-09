package org.babinkuk.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.MessagePool;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.vo.InstructorVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.babinkuk.config.Api.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;

@Transactional
@AutoConfigureMockMvc
public class InstructorValidatorTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(InstructorValidatorTest.class);
	
	@Test
	void addEmptyInstructorRoleAdmin() throws Exception {

		addEmptyInstructor(ROLE_ADMIN);
	}
	
	@Test
	void addEmptyInstructorRoleInstructor() throws Exception {

		addEmptyInstructor(ROLE_INSTRUCTOR);
	}

	private void addEmptyInstructor(String validationRole) throws Exception {
		
		// create invalid instructor (empty fields)
		InstructorVO instructorVO = new InstructorVO();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_EMAIL_EMPTY.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STATUS_INVALID.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
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
	void addInstructorInvalidEmailRoleAdmin() throws Exception {

		addInstructorInvalidEmail(ROLE_ADMIN);
	}
	
	@Test
	void addInstructorInvalidEmailRoleIstructor() throws Exception {

		addInstructorInvalidEmail(ROLE_INSTRUCTOR);
	}
	
	private void addInstructorInvalidEmail(String validationRole) throws Exception {
		
		// create invalid instructor (empty fields)
		InstructorVO instructorVO = new InstructorVO();
		String emailAddress = "this is invalid email";
		instructorVO.setEmail(emailAddress);
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_EMAIL_INVALID.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
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
	void addInstructorEmailNotUniqueRoleAdmin() throws Exception {

		addInstructorEmailNotUnique(ROLE_ADMIN);
	}
	
	@Test
	void addInstructorEmailNotUniqueRoleInstructor() throws Exception {

		addInstructorEmailNotUnique(ROLE_INSTRUCTOR);
	}
	
	private void addInstructorEmailNotUnique(String validationRole) throws Exception {
		
		// create instructor
		InstructorVO instructorVO = ApplicationTestUtils.createInstructorVO();
		// this email already exists in db
		String emailAddress = INSTRUCTOR_EMAIL;
		instructorVO.setEmail(emailAddress);
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_EMAIL_ALREADY_EXIST.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
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
	void updateInstructorInvalidIdRoleAdmin() throws Exception {

		updateInstructorInvalidId(ROLE_ADMIN);
	}
	
	@Test
	void updateInstructorInvalidIdRoleInstructor() throws Exception {

		updateInstructorInvalidId(ROLE_INSTRUCTOR);
	}
	
	private void updateInstructorInvalidId(String validationRole) throws Exception {
		
		int id = 22;
		
		// check if instructor id=22 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), id)))) // verify json element
			;
		
		// create instructor (invalid id=22)
		InstructorVO instructorVO = ApplicationTestUtils.createInstructorVO();
		instructorVO.setId(id);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), id)))) // verify json element
			;
	}
	
	@Test
	void updateEmptyInstructorRoleAdmin() throws Exception {

		updateEmptyInstructor(ROLE_ADMIN);
	}
	
	@Test
	void updateEmptyInstructorRoleInstructor() throws Exception {

		updateEmptyInstructor(ROLE_INSTRUCTOR);
	}
	
	private void updateEmptyInstructor(String validationRole) throws Exception {
		
		int id = 1;
		
		// check if instructor id=1 exists
		InstructorVO instructorVO = instructorService.findById(id);
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"getFirstName() null");
		assertEquals(INSTRUCTOR_FIRSTNAME, instructorVO.getFirstName(),"getFirstName() failure");
		
		// update instructor
		instructorVO.setFirstName("");
		instructorVO.setLastName("");
		instructorVO.setEmail("");
		instructorVO.setYoutubeChannel("");
		instructorVO.setHobby("");
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
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
	void updateInstructorInvalidEmailRoleAdmin() throws Exception {

		updateInstructorInvalidEmail(ROLE_ADMIN);
	}
	
	@Test
	void updateInstructorInvalidEmailRoleInstructor() throws Exception {

		updateInstructorInvalidEmail(ROLE_INSTRUCTOR);
	}
	
	private void updateInstructorInvalidEmail(String validationRole) throws Exception {
		
		int id = 1;
		
		// check if instructor id=1 exists
		InstructorVO instructorVO = instructorService.findById(id);
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"getFirstName() null");
		assertEquals(INSTRUCTOR_FIRSTNAME, instructorVO.getFirstName(),"getFirstName() failure");
		
		// update instructor email
		instructorVO.setEmail("this is invalid email");
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
	        .andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_EMAIL_INVALID.getMessage()), ActionType.CREATE))))
			;
	}
	
	@Test
	void updateInstructorEmailNotUniqueRoleAdmin() throws Exception {
		
		updateInstructorEmailNotUnique(ROLE_ADMIN);
	}
	
	@Test
	void updateInstructorEmailNotUniqueRoleInstructor() throws Exception {
		
		updateInstructorEmailNotUnique(ROLE_INSTRUCTOR);
	}
	
	private void updateInstructorEmailNotUnique(String validationRole) throws Exception {
		
		int id = 1;
		
		// check if instructor id=1 exists
		InstructorVO instructorVO = instructorService.findById(id);
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"instructorVO.getFirstName() null");
		assertEquals(INSTRUCTOR_FIRSTNAME, instructorVO.getFirstName(),"getFirstName() failure");
		
		// create new instructor
		InstructorVO newInstructorVO = ApplicationTestUtils.createInstructorVO();
		
		// save new instructor
		instructorService.saveInstructor(newInstructorVO);
		
		// check if new instructor exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is size 2
			;
		
		InstructorVO dbNewInstructorVO = instructorService.findByEmail(newInstructorVO.getEmail());
		
		assertNotNull(dbNewInstructorVO,"dbNewInstructorVO null");
		assertNotNull(dbNewInstructorVO.getFirstName(),"dbNewInstructorVO.getFirstName() null");
		assertEquals(newInstructorVO.getFirstName(), dbNewInstructorVO.getFirstName(),"dbNewInstructorVO.getFirstName() failure");
		assertEquals(newInstructorVO.getEmail(), dbNewInstructorVO.getEmail(),"dbNewInstructorVO.getEmail() failure");
		
		// update instructor email (value belong to other instructor id 1)
		dbNewInstructorVO.setEmail(instructorVO.getEmail());
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(dbNewInstructorVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ size 1
	        .andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_EMAIL_ALREADY_EXIST.getMessage()), ActionType.CREATE))))
			;
	}
	
	@Test
	void updateInstructorNotExistRoleAdmin() throws Exception {
		
		updateInstructorNotExist(ROLE_ADMIN);
	}
	
	@Test
	void updateInstructorNotExistRoleInstructor() throws Exception {
		
		updateInstructorNotExist(ROLE_INSTRUCTOR);
	}
	
	private void updateInstructorNotExist(String validationRole) throws Exception {
		
		int id = 22;
		
		// get instructor with id=22 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), id)))) // verify json element
			;
		
		// create new instructor
		InstructorVO instructorVO = ApplicationTestUtils.createInstructorVO();
		instructorVO.setId(id);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), id)))) // verify json element
			;
	}
}