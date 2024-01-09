package org.babinkuk.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.MessagePool;
import org.babinkuk.service.ImageService;
import org.babinkuk.service.InstructorService;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.ImageVO;
import org.babinkuk.vo.InstructorVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.babinkuk.config.Api.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
public class ImageControllerTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(ImageControllerTest.class);
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ImageService imageService;
	
	@Autowired
	private InstructorService instructorService;

	@Test
	void getAllImages() throws Exception {
		
		// get all images
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + IMAGES)
				.param(VALIDATION_ROLE, ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is size 2
			;

		// create image
		// set id=0: this is to force a save of new item
		ImageVO imageVO = new ImageVO();
		imageVO.setFileName(FILE_NEW);
		imageVO.setData(DATA_NEW);
		imageVO.setId(0);
		
		// add to instructor
		InstructorVO instructorVO = instructorService.findById(1);
		
		imageService.saveImage(instructorVO, imageVO);
		
		// get all images (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + IMAGES)
				.param(VALIDATION_ROLE, ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(3))) // verify that json root element $ is now size 3
			;
		
		// get all images (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + IMAGES)
				.param(VALIDATION_ROLE, ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(3))) // verify that json root element $ is now size 3
			;
		
		// get all images (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + IMAGES)
				.param(VALIDATION_ROLE, "")
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(3))) // verify that json root element $ is now size 3
			;
		
		// get all images (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + IMAGES)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(3))) // verify that json root element $ is now size 3
			;
	}
	
	@Test
	void getImageRoleAdmin() throws Exception {
		
		getImage(ROLE_ADMIN);
	}
	
	@Test
	void getImageRoleInstructor() throws Exception {
		
		getImage(ROLE_INSTRUCTOR);
	}
	
	@Test
	void getImageRoleStudent() throws Exception {
		
		getImage(ROLE_STUDENT);
	}
	
	@Test
	void getImageNoRole() throws Exception {
		
		getImage("");
	}
	
	@Test
	void getImageRoleNotExist() throws Exception {
		
		getImage(ROLE_NOT_EXIST);
	}
	
	private void getImage(String validationRole) throws Exception {
		
		// get image with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + IMAGES + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.fileName", is(FILE_1))) // verify json element
			;

		// get image with id=22 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + IMAGES + "/{id}", 22)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_IMAGE_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
	}
	
//	@Test
//	void addReviewRoleAdmin() throws Exception {
//
//		addReview(ROLE_ADMIN);
//	}
//	
//	@Test
//	void addReviewRoleInstructor() throws Exception {
//
//		addReview(ROLE_INSTRUCTOR);
//	}
//	
//	@Test
//	void addReviewRoleStudent() throws Exception {
//
//		addReview(ROLE_STUDENT);
//	}
//	
//	@Test
//	void addReviewNoRole() throws Exception {
//			
//		addReview(null);
//	}
//	
//	@Test
//	void addReviewRoleNotExist() throws Exception {
//			
//		addReview(ROLE_NOT_EXIST);
//	}
//	
//	private void addReview(String validationRole) throws Exception {
//		
//		// create review
//		ReviewVO reviewVO = new ReviewVO(REVIEW_NEW);
//		
//		if (StringUtils.isBlank(validationRole) || validationRole.equals(ROLE_ADMIN) || validationRole.equals(ROLE_INSTRUCTOR) || validationRole.equals(ROLE_STUDENT)) {
//			mockMvc.perform(MockMvcRequestBuilders.post(ROOT + IMAGES + "/{courseId}", 1)
//					.param(VALIDATION_ROLE, validationRole)
//					.contentType(APPLICATION_JSON_UTF8)
//					.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
//				)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(status().isOk())
//				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(REVIEW_SAVE_SUCCESS)))) // verify json element
//				;
//			
//			// additional check
//			// get all reviews
//			mockMvc.perform(MockMvcRequestBuilders.get(ROOT + IMAGES)
//					.param(VALIDATION_ROLE, validationRole)
//				)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
//				.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
//				.andDo(MockMvcResultHandlers.print())
//				;
//		} else {
//			// non existing role
//			mockMvc.perform(MockMvcRequestBuilders.post(ROOT + IMAGES + "/{courseId}", 1)
//					.param(VALIDATION_ROLE, validationRole)
//					.contentType(APPLICATION_JSON_UTF8)
//					.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
//				)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(status().is4xxClientError())
//				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
//				;
//			
//			// additional check
//			// get all reviews
//			mockMvc.perform(MockMvcRequestBuilders.get(ROOT + IMAGES)
//					.param(VALIDATION_ROLE, validationRole)
//				)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
//				.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is stil size 1
//				;
//		}
//	}
	
	@Test
	void updateImageRoleAdmin() throws Exception {

		updateImage(ROLE_ADMIN);
	}
	
	@Test
	void updateImageRoleInstructor() throws Exception {

		updateImage(ROLE_INSTRUCTOR);
	}
	
	@Test
	void updateImageRoleStudent() throws Exception {

		updateImage(ROLE_STUDENT);
	}
	
	@Test
	void updateImageNoRole() throws Exception {
		
		updateImage("");
	}
	
	@Test
	void updateImageRoleNotExist() throws Exception {
		
		updateImage(ROLE_NOT_EXIST);
	}
	
	private void updateImage(String validationRole) throws Exception {
		
		// check if image id=1 exists
		ImageVO imageVO = imageService.findById(1);
		
		assertNotNull(imageVO,"imageVO null");
		assertEquals(1, imageVO.getId());
		assertNotNull(imageVO.getFileName(),"getFileName() null");
		assertEquals(FILE_1, imageVO.getFileName(),"getFileName() failure");
		
		if (validationRole.equals(ROLE_ADMIN)) {
			mockMvc.perform(MockMvcRequestBuilders.put(ROOT + IMAGES + "/{id}", 1)
					.param(VALIDATION_ROLE, validationRole)
					.param(FILE_NAME, FILE_UPDATED)
					.contentType(APPLICATION_JSON_UTF8)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(IMAGE_SAVE_SUCCESS)))) // verify json element
				;
			
			// additional check
			// get image with id=1
			mockMvc.perform(MockMvcRequestBuilders.get(ROOT + IMAGES + "/{id}", 1)
					.param(VALIDATION_ROLE, validationRole)
				)
				.andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
				.andExpect(jsonPath("$.fileName", is(FILE_UPDATED))) // verify json element
				;

		} else if (StringUtils.isBlank(validationRole) || validationRole.equals(ROLE_STUDENT) || validationRole.equals(ROLE_INSTRUCTOR)) {

			mockMvc.perform(MockMvcRequestBuilders.put(ROOT + IMAGES + "/{id}", 1)
					.param(VALIDATION_ROLE, validationRole)
					.param(FILE_NAME, FILE_UPDATED)
					.contentType(APPLICATION_JSON_UTF8)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
				;

		} else {
			// non existing role
			mockMvc.perform(MockMvcRequestBuilders.put(ROOT + IMAGES + "/{id}", 1)
					.param(VALIDATION_ROLE, validationRole)
					.param(FILE_NAME, FILE_UPDATED)
					.contentType(APPLICATION_JSON_UTF8)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
				;
		}
	}
	
	@Test
	void deleteImageRoleAdmin() throws Exception {
		
		deleteImage(ROLE_ADMIN);
	}
	
	@Test
	void deleteImageRoleInstructor() throws Exception {
		
		deleteImage(ROLE_INSTRUCTOR);
	}
	
	@Test
	void deleteImageRoleStudent() throws Exception {
		
		deleteImage(ROLE_STUDENT);
	}
	
	@Test
	void deleteImageNoRole() throws Exception {
		
		deleteImage(null);
	}
	
	@Test
	void deleteImageRoleNotExist() throws Exception {
		
		deleteImage(ROLE_NOT_EXIST);
	}

	private void deleteImage(String validationRole) throws Exception {
		
		// check if image id=1 exists
		int id = 1;
		ImageVO imageVO = imageService.findById(id);
		
		assertNotNull(imageVO,"imageVO null");
		assertEquals(id, imageVO.getId());
		assertNotNull(imageVO.getFileName(),"getFileName() null");
		assertEquals(FILE_1, imageVO.getFileName(),"getFileName() failure");
		
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
				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(IMAGE_DELETE_SUCCESS)))) // verify json element
				;
			
			// get image with id=1 (non existing)
			mockMvc.perform(MockMvcRequestBuilders.get(ROOT + IMAGES + "/{id}", id)
					.param(VALIDATION_ROLE, validationRole)
				).andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_IMAGE_ID_NOT_FOUND.getMessage()), id)))) //verify json element
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
				.andExpect(jsonPath("$.images").doesNotExist()) // verify json element
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
