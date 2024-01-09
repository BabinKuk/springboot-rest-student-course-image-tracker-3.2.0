package org.babinkuk.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.MessagePool;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ReviewVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.babinkuk.config.Api.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
public class ReviewControllerTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(ReviewControllerTest.class);
	
	@Test
	void getAllReviews() throws Exception {
		
		// get all reviews
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				.param(VALIDATION_ROLE, ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			;

		// add another review
		// first find course
		CourseVO courseVO = courseService.findById(1);
		
		// create review
		// set id 0: this is to force a save of new item ... instead of update
		ReviewVO reviewVO = new ReviewVO(REVIEW_NEW);
		reviewVO.setId(0);
		
		// add to course
		reviewService.saveReview(courseVO, reviewVO);
		
		// get all reviews (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				.param(VALIDATION_ROLE, ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all reviews (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				.param(VALIDATION_ROLE, ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all reviews (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				.param(VALIDATION_ROLE, "")
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all reviews (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
	}
	
	@Test
	void getReviewRoleAdmin() throws Exception {
		
		getReview(ROLE_ADMIN);
	}
	
	@Test
	void getReviewRoleInstructor() throws Exception {
		
		getReview(ROLE_INSTRUCTOR);
	}
	
	@Test
	void getReviewRoleStudent() throws Exception {
		
		getReview(ROLE_STUDENT);
	}
	
	@Test
	void getReviewNoRole() throws Exception {
		
		getReview("");
	}
	
	@Test
	void getReviewRoleNotExist() throws Exception {
		
		getReview(ROLE_NOT_EXIST);
	}
	
	private void getReview(String validationRole) throws Exception {
		
		// get review with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.comment", is(REVIEW))) // verify json element
			;

		// get review with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", 2)
				.param(VALIDATION_ROLE, ROLE_ADMIN)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_REVIEW_ID_NOT_FOUND.getMessage()), 2)))) // verify json element
			;
	}
	
	@Test
	void addReviewRoleAdmin() throws Exception {

		addReview(ROLE_ADMIN);
	}
	
	@Test
	void addReviewRoleInstructor() throws Exception {

		addReview(ROLE_INSTRUCTOR);
	}
	
	@Test
	void addReviewRoleStudent() throws Exception {

		addReview(ROLE_STUDENT);
	}
	
	@Test
	void addReviewNoRole() throws Exception {
			
		addReview(null);
	}
	
	@Test
	void addReviewRoleNotExist() throws Exception {
			
		addReview(ROLE_NOT_EXIST);
	}
	
	private void addReview(String validationRole) throws Exception {
		
		// create review
		ReviewVO reviewVO = new ReviewVO(REVIEW_NEW);
		
		if (StringUtils.isBlank(validationRole) || validationRole.equals(ROLE_ADMIN) || validationRole.equals(ROLE_INSTRUCTOR) || validationRole.equals(ROLE_STUDENT)) {
			mockMvc.perform(MockMvcRequestBuilders.post(ROOT + REVIEWS + "/{courseId}", 1)
					.param(VALIDATION_ROLE, validationRole)
					.contentType(APPLICATION_JSON_UTF8)
					.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(REVIEW_SAVE_SUCCESS)))) // verify json element
				;
			
			// additional check
			// get all reviews
			mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
					.param(VALIDATION_ROLE, validationRole)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
				.andDo(MockMvcResultHandlers.print())
				;
		} else {
			// non existing role
			mockMvc.perform(MockMvcRequestBuilders.post(ROOT + REVIEWS + "/{courseId}", 1)
					.param(VALIDATION_ROLE, validationRole)
					.contentType(APPLICATION_JSON_UTF8)
					.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
				;
			
			// additional check
			// get all reviews
			mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS)
					.param(VALIDATION_ROLE, validationRole)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is stil size 1
				;
		}
	}
	
//	@Test
//	void updateReviewRoleAdmin() throws Exception {
//
//		updateReview(ROLE_ADMIN);
//	}
//	
//	@Test
//	void updateReviewRoleInstructor() throws Exception {
//
//		updateReview(ROLE_INSTRUCTOR);
//	}
//	
//	@Test
//	void updateReviewRoleStudent() throws Exception {
//
//		updateReview(ROLE_STUDENT);
//	}
//	
//	@Test
//	void updateReviewNoRole() throws Exception {
//		
//		updateReview("");
//	}
//	
//	@Test
//	void updateReviewRoleNotExist() throws Exception {
//		
//		updateReview(ROLE_NOT_EXIST);
//	}
//	
//	private void updateReview(String validationRole) throws Exception {
//		
//		// check if review id=1 exists
//		ReviewVO reviewVO = reviewService.findById(1);
//		
//		assertNotNull(reviewVO,"reviewVO null");
//		assertEquals(1, reviewVO.getId());
//		assertNotNull(reviewVO.getComment(),"getComment() null");
//		assertEquals(REVIEW, reviewVO.getComment(),"getComment() failure");
//		
//		// update review
//		reviewVO.setComment(REVIEW_UPDATE);
//		
//		if (validationRole.equals(ROLE_ADMIN) || validationRole.equals(ROLE_INSTRUCTOR)) {
//			mockMvc.perform(MockMvcRequestBuilders.put(ROOT + REVIEWS)
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
//			// get review with id=1
//			mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", 1)
//					.param(VALIDATION_ROLE, validationRole)
//				)
//				.andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
//				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
//				.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
//				.andExpect(jsonPath("$.comment", is(REVIEW_UPDATE))) // verify json element
//				;
//		} else if (StringUtils.isBlank(validationRole) || validationRole.equals(ROLE_STUDENT)) {
//			mockMvc.perform(MockMvcRequestBuilders.put(ROOT + REVIEWS)
//					.param(VALIDATION_ROLE, validationRole)
//					.contentType(APPLICATION_JSON_UTF8)
//					.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
//				)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(status().is4xxClientError())
//				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
//				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
//				;
//		} else {
//			// non existing role
//			mockMvc.perform(MockMvcRequestBuilders.put(ROOT + REVIEWS)
//					.param(VALIDATION_ROLE, validationRole)
//					.contentType(APPLICATION_JSON_UTF8)
//					.content(objectMApper.writeValueAsString(reviewVO)) // generate json from java object
//				)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(status().is4xxClientError())
//				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
//				;
//		}
//	}
//	
//	@Test
//	void deleteReviewRoleAdmin() throws Exception {
//		
//		deleteReview(ROLE_ADMIN);
//	}
//	
//	@Test
//	void deleteReviewRoleInstructor() throws Exception {
//		
//		deleteReview(ROLE_INSTRUCTOR);
//	}
//	
//	@Test
//	void deleteReviewRoleStudent() throws Exception {
//		
//		deleteReview(ROLE_STUDENT);
//	}
//	
//	@Test
//	void deleteReviewNoRole() throws Exception {
//		
//		deleteReview(null);
//	}
//	
//	@Test
//	void deleteReviewRoleNotExist() throws Exception {
//		
//		deleteReview(ROLE_NOT_EXIST);
//	}
//
//	private void deleteReview(String validationRole) throws Exception {
//		
//		// check if review id=1 exists
//		int id = 1;
//		ReviewVO reviewVO = reviewService.findById(id);
//		
//		assertNotNull(reviewVO,"reviewVO null");
//		assertEquals(1, reviewVO.getId());
//		assertNotNull(reviewVO.getComment(),"getComment() null");
//		assertEquals(REVIEW, reviewVO.getComment(),"reviewVO.getComment() failure");
//		
//		// delete review
//		if (StringUtils.isBlank(validationRole) || validationRole.equals(ROLE_STUDENT) || validationRole.equals(ROLE_INSTRUCTOR)) {
//			mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + REVIEWS + "/{id}", id)
//					.param(VALIDATION_ROLE, validationRole)
//				)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(status().is4xxClientError())
//				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
//				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
//				;
//		} else if (validationRole.equals(ROLE_ADMIN)) {
//			mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + REVIEWS + "/{id}", id)
//					.param(VALIDATION_ROLE, validationRole)
//				)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
//				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(REVIEW_DELETE_SUCCESS)))) // verify json element
//				;
//			
//			// get review with id=1 (non existing)
//			mockMvc.perform(MockMvcRequestBuilders.get(ROOT + REVIEWS + "/{id}", id)
//					.param(VALIDATION_ROLE, validationRole)
//				).andDo(MockMvcResultHandlers.print())
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(APPLICATION_JSON_UTF8))
//				.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_REVIEW_ID_NOT_FOUND.getMessage()), id)))) //verify json element
//				;
//		} else {
//			// non existing role
//			mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + REVIEWS + "/{id}", id)
//					.param(VALIDATION_ROLE, validationRole)
//				)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(status().is4xxClientError())
//				.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
//				;
//		}
//	}
}
