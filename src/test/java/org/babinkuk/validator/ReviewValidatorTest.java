package org.babinkuk.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.MessagePool;
import org.babinkuk.vo.ReviewVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.babinkuk.utils.ApplicationTestConstants.*;
import static org.babinkuk.config.Api.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
public class ReviewValidatorTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(ReviewValidatorTest.class);
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMApper;
	
	@Test
	void addEmptyReviewRoleAdmin() throws Exception {

		addEmptyReview(ROLE_ADMIN);
	}
	
	@Test
	void addEmptyReviewRoleInstructor() throws Exception {

		addEmptyReview(ROLE_INSTRUCTOR);
	}
	
	@Test
	void addEmptyReviewRoleStudent() throws Exception {

		addEmptyReview(ROLE_STUDENT);
	}
	
	private void addEmptyReview(String validationRole) throws Exception {
		
		// create invalid review 
		ReviewVO reviewVO = new ReviewVO();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + REVIEWS + "/{courseId}", 1)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
			.andExpect(jsonPath("$.errors[0]", is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_REVIEW_EMPTY.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all reviews
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
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
	void addEmptyReviewNoRole() throws Exception {
		
		//String validationRole = ROLE_STUDENT;
		
		// create invalid review 
		ReviewVO reviewVO = new ReviewVO("");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + REVIEWS + "/{courseId}", 1)
				//.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
			.andExpect(jsonPath("$.errors[0]", is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_REVIEW_EMPTY.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all reviews
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				//.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is now size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}
	
	@Test
	void addReviewInvalidCourseRoleAdmin() throws Exception {

		addReviewInvalidCourse(ROLE_ADMIN);
	}
	
	@Test
	void addReviewInvalidCourseRoleInstructor() throws Exception {

		addReviewInvalidCourse(ROLE_INSTRUCTOR);
	}
	
	@Test
	void addReviewInvalidCourseRoleStudent() throws Exception {

		addReviewInvalidCourse(ROLE_STUDENT);
	}
	
	private void addReviewInvalidCourse(String validationRole) throws Exception {
		
		// create invalid review 
		ReviewVO reviewVO = new ReviewVO("review");
		
		// invalid course id=2
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + REVIEWS + "/{courseId}", 2)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), 2)))) // verify json element.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all reviews
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
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
	void addReviewRoleInvalidCourseNoRole() throws Exception {
		
		// create invalid review 
		ReviewVO reviewVO = new ReviewVO("review");
		
		// invalid course id=2
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + REVIEWS + "/{courseId}", 2)
				//.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), 2)))) // verify json element.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all reviews
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				//.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is now size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}
	
	@Test
	void updateInvalidReviewRoleAdmin() throws Exception {
		
		updateInvalidReview(ROLE_ADMIN);
	}
	
	@Test
	void updateInvalidReviewRoleInstructor() throws Exception {
		
		updateInvalidReview(ROLE_INSTRUCTOR);
	}
	
	@Test
	void updateInvalidReviewRoleStudent() throws Exception {
		
		updateInvalidReview(ROLE_STUDENT);
	}
	
	private void updateInvalidReview(String validationRole) throws Exception {
		
		int id = 2;
		
		// check if review id=2 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_REVIEW_ID_NOT_FOUND.getMessage()), id)))) // verify json element
			;
		
		// create invalid review (set invalid id=2)
		ReviewVO reviewVO = new ReviewVO("review");
		reviewVO.setId(id);
		
		if (validationRole.equals(ROLE_STUDENT)) {
			mockMvc.perform(MockMvcRequestBuilders.put(ROOT + REVIEWS)
					.param(VALIDATION_ROLE, validationRole)
					.contentType(APPLICATION_JSON_UTF8)
					.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage()), ActionType.UPDATE)))) // verify json root element message
				;
		} else {
			mockMvc.perform(MockMvcRequestBuilders.put(ROOT + REVIEWS)
					.param(VALIDATION_ROLE, validationRole)
					.contentType(APPLICATION_JSON_UTF8)
					.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_REVIEW_ID_NOT_FOUND.getMessage()), id)))) // verify json element
				;
		}	
	}
	
	@Test
	void updateInvalidReviewNoRole() throws Exception {
		
		int id = 2;
		
		// check if review id=2 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", id)
			//	.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_REVIEW_ID_NOT_FOUND.getMessage()), id)))) // verify json element
			;
		
		// create invalid review (set invalid id 2)
		ReviewVO reviewVO = new ReviewVO("review");
		reviewVO.setId(id);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + REVIEWS)
				//.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage()), ActionType.UPDATE)))) // verify json root element message
			;
	}
	
	@Test
	void deleteInvalidReviewRoleAdmin() throws Exception {
		
		deleteInvalidReview(ROLE_ADMIN);
	}
	
	@Test
	void deleteInvalidReviewRoleInstructor() throws Exception {
		
		deleteInvalidReview(ROLE_INSTRUCTOR);
	}
	
	@Test
	void deleteInvalidReviewRoleStudent() throws Exception {
		
		deleteInvalidReview(ROLE_STUDENT);
	}

	private void deleteInvalidReview(String validationRole) throws Exception {
		
		int id = 2;
		
		// check if review id =2 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_REVIEW_ID_NOT_FOUND.getMessage()), id)))) // verify json element
			;
		
		// delete review
		if (validationRole.equals(ROLE_ADMIN)) {
			mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + REVIEWS + "/{id}", id)
					.param(VALIDATION_ROLE, validationRole)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_REVIEW_ID_NOT_FOUND.getMessage()), id)))) //verify json element
				;
		} else {
			mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + REVIEWS + "/{id}", id)
					.param(VALIDATION_ROLE, validationRole)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage()), ActionType.DELETE)))) // verify json root element message
				;
		}
	}
	
	@Test
	void deleteInvalidReviewNoRole() throws Exception {
		
		int id = 2;
		
		// check if review id=2 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", id)
				//.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_REVIEW_ID_NOT_FOUND.getMessage()), id)))) // verify json element
			;
				
		// delete review
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + REVIEWS + "/{id}", id)
			//	.param(VALIDATION_ROLE, ROLE_STUDENT)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage()), ActionType.DELETE)))) // verify json root element message
			;
	}
}
