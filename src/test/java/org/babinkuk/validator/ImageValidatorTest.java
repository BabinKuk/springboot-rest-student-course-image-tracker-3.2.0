package org.babinkuk.validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.MessagePool;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.babinkuk.utils.ApplicationTestConstants.*;
import static org.babinkuk.config.Api.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
public class ImageValidatorTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(ImageValidatorTest.class);
	
	@Autowired
	ObjectMapper objectMApper;
	
	@Test
	void uploadFileFileNameEmptyRoleAdmin() throws Exception {

		uploadFileFileNameEmpty(ROLE_ADMIN);
	}
	
	@Test
	void uploadFileFileNameEmptyRoleInstructor() throws Exception {

		uploadFileFileNameEmpty(ROLE_INSTRUCTOR);
	}
	
	@Test
	void uploadFileFileNameEmptyRoleStudent() throws Exception {

		uploadFileFileNameEmpty(ROLE_STUDENT);
	}
	
	@Test
	void uploadFileFileNameEmptyNoRole() throws Exception {

		uploadFileFileNameEmpty(null);
	}
	
	private void uploadFileFileNameEmpty(String validationRole) throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		MockMultipartFile file = new MockMultipartFile(FILE_NEW, "", MediaType.TEXT_PLAIN_VALUE, DATA_NEW);
		
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
	}
	
	@Test
	void uploadFileDataEmptyRoleAdmin() throws Exception {

		uploadFileDataEmpty(ROLE_ADMIN);
	}
	
	@Test
	void uploadFileDataEmptyRoleInstructor() throws Exception {

		uploadFileDataEmpty(ROLE_INSTRUCTOR);
	}
	
	@Test
	void uploadFileDataEmptyRoleStudent() throws Exception {

		uploadFileDataEmpty(ROLE_STUDENT);
	}
	
	@Test
	void uploadFileDataEmptyNoRole() throws Exception {

		uploadFileDataEmpty(null);
	}
	
	private void uploadFileDataEmpty(String validationRole) throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		byte[] emptyData = new byte[0];
		
		MockMultipartFile file = new MockMultipartFile(FILE_NEW, FILE_NEW, MediaType.TEXT_PLAIN_VALUE, emptyData);
		
		// upload file
		mockMvc.perform(MockMvcRequestBuilders.multipart(ROOT + STUDENTS + "/{id}" + "/upload", 2)
				.file(FILE, file.getBytes())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
		.andExpect(status().is4xxClientError())
		.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
		.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
		.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 4
		.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_FILE_DATA_EMPTY.getMessage()), ActionType.CREATE))))
		;
		
		// additional check
		// get all files
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + IMAGES)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			.andExpect(jsonPath("$[0].fileName", is(FILE_1)))
			.andExpect(jsonPath("$[1].fileName", is(FILE_2)))
			;
		
		// get student with id=2
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + STUDENTS + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(2))) // verify json root element id is 2
			.andExpect(jsonPath("$.firstName", is(STUDENT_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.lastName", is(STUDENT_LASTNAME))) // verify json element
			.andExpect(jsonPath("$.images", hasSize(1))) // verify json element
			;
	}
	
	@Test
	void uploadFileInvalidUserRoleAdmin() throws Exception {

		uploadFileInvalidUser(ROLE_ADMIN);
	}
	
	@Test
	void uploadFileInvalidUserRoleInstructor() throws Exception {

		uploadFileInvalidUser(ROLE_INSTRUCTOR);
	}
	
	@Test
	void uploadFileInvalidUserRoleStudent() throws Exception {

		uploadFileInvalidUser(ROLE_STUDENT);
	}
	
	@Test
	void uploadFileInvalidUserNoRole() throws Exception {

		uploadFileInvalidUser(null);
	}
	
	private void uploadFileInvalidUser(String validationRole) throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		MockMultipartFile file = new MockMultipartFile(FILE_NEW, FILE_NEW, MediaType.TEXT_PLAIN_VALUE, DATA_NEW);
		
		// upload file (non existing student)
		mockMvc.perform(MockMvcRequestBuilders.multipart(ROOT + STUDENTS + "/{id}" + "/upload", 22)
				.file(FILE, file.getBytes())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;

		// additional check
		// get all files
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + IMAGES)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is size 2
			.andExpect(jsonPath("$[0].fileName", is(FILE_1)))
			.andExpect(jsonPath("$[1].fileName", is(FILE_2)))
			;
	}
	
	@Test
	void updateInvalidImageRoleAdmin() throws Exception {
		
		updateInvalidImage(ROLE_ADMIN);
	}
	
	@Test
	void updateInvalidReviewRoleInstructor() throws Exception {
		
		updateInvalidImage(ROLE_INSTRUCTOR);
	}
	
	@Test
	void updateInvalidReviewRoleStudent() throws Exception {
		
		updateInvalidImage(ROLE_STUDENT);
	}
	
	private void updateInvalidImage(String validationRole) throws Exception {
		
		int id = 22;
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// get image with id=22 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + IMAGES + "/{id}", 22)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_IMAGE_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
		
		// update invalid image (set invalid id=22)
		if (validationRole.equals(ROLE_ADMIN)) {
			mockMvc.perform(MockMvcRequestBuilders.put(ROOT + IMAGES + "/{id}", id)
					.param(VALIDATION_ROLE, validationRole)
					.param(FILE_NAME, FILE_UPDATED)
					.contentType(APPLICATION_JSON_UTF8)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_IMAGE_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
				;
			
		} else {
			mockMvc.perform(MockMvcRequestBuilders.put(ROOT + IMAGES + "/{id}", id)
					.param(VALIDATION_ROLE, validationRole)
					.param(FILE_NAME, FILE_UPDATED)
					.contentType(APPLICATION_JSON_UTF8)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_IMAGE_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
				;
		}	
	}
	
	@Test
	void deleteInvalidImageRoleAdmin() throws Exception {
		
		deleteInvalidImage(ROLE_ADMIN);
	}
	
	@Test
	void deleteInvalidImageRoleInstructor() throws Exception {
		
		deleteInvalidImage(ROLE_INSTRUCTOR);
	}
	
	@Test
	void deleteInvalidImageRoleStudent() throws Exception {
		
		deleteInvalidImage(ROLE_STUDENT);
	}

	private void deleteInvalidImage(String validationRole) throws Exception {
		
		int id = 22;
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// get image with id=22 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + IMAGES + "/{id}", 22)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_IMAGE_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
		
		// delete image
		if (StringUtils.isBlank(validationRole) || validationRole.equals(ROLE_STUDENT) || validationRole.equals(ROLE_INSTRUCTOR)) {
			mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + IMAGES + "/{id}", id)
					.param(VALIDATION_ROLE, validationRole)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage()), ActionType.DELETE)))) // verify json root element message
				;

		} else if (validationRole.equals(ROLE_ADMIN)) {
			mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + IMAGES + "/{id}", id)
					.param(VALIDATION_ROLE, validationRole)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_IMAGE_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
				;
			
		} else {
			// non existing role
			mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + IMAGES + "/{id}", id)
					.param(VALIDATION_ROLE, validationRole)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
				;
		}
	}
}
