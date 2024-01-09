package org.babinkuk.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.MessagePool;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.vo.CourseVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.babinkuk.config.Api.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
public class CourseValidatorTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(CourseValidatorTest.class);
	
	@Test
	void addEmptyCourseRoleAdmin() throws Exception {

		addEmptyCourse(ROLE_ADMIN);
	}
	
	@Test
	void addEmptyCourseRoleInstructor() throws Exception {

		addEmptyCourse(ROLE_INSTRUCTOR);
	}

	private void addEmptyCourse(String validationRole) throws Exception {
		
		// create invalid course (empty title)
		CourseVO courseVO = new CourseVO();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + COURSES)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(courseVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_TITLE_EMPTY.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all courses
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
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
	void addCourseTitleNotUniqueRoleAdmin() throws Exception {

		addCourseTitleNotUnique(ROLE_ADMIN);
	}
	
	@Test
	void addCourseTitleNotUniqueRoleInstructor() throws Exception {

		addCourseTitleNotUnique(ROLE_INSTRUCTOR);
	}
	
	private void addCourseTitleNotUnique(String validationRole) throws Exception {
		
		int id = 1;
		
		// check if course id=1 exists
		CourseVO courseVO = courseService.findById(id);
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		assertEquals(COURSE, courseVO.getTitle(),"courseVO.getTitle() NOK");
		
		// create invalid course (title already exists in db)
		CourseVO newCourseVO = new CourseVO(courseVO.getTitle());
		newCourseVO.setId(0);
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + COURSES)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(newCourseVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_TITLE_ALREADY_EXIST.getMessage()), ActionType.CREATE))))
			;
		
		// additional check 
		// get all courses
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			;
	}
	
	@Test
	void updateCourseInvalidIdRoleAdmin() throws Exception {

		updateCourseInvalidId(ROLE_ADMIN);
	}
	
	@Test
	void updateCourseInvalidIdRoleInstructor() throws Exception {

		updateCourseInvalidId(ROLE_INSTRUCTOR);
	}
	
	private void updateCourseInvalidId(String validationRole) throws Exception {
		
		// check if course id=1 exists
		CourseVO courseVO = courseService.findById(1);
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		assertEquals(COURSE, courseVO.getTitle(),"courseVO.getTitle() NOK");
		
		// check if course id=2 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), 2)))) // verify json element
			;
		
		// update course with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
				.param(COURSE_TITLE, "courseTitle")
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), 2)))) // verify json element
			;
	}
	
	@Test
	void updateEmptyCourseRoleAdmin() throws Exception {

		updateEmptyCourse(ROLE_ADMIN);
	}
	
	@Test
	void updateEmptyCourseRoleInstructor() throws Exception {

		updateEmptyCourse(ROLE_INSTRUCTOR);
	}
	
	private void updateEmptyCourse(String validationRole) throws Exception {
		
		int id = 1;
		
		// check if course id=1 exists
		CourseVO courseVO = courseService.findById(id);
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		assertEquals(COURSE, courseVO.getTitle(),"courseVO.getTitle() NOK");
		
		String courseTitle = "";
		
		// update course with empty title
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
				.param(COURSE_TITLE, courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_TITLE_EMPTY.getMessage()), ActionType.CREATE))))
			;
	}
	
	@Test
	void updateCourseTitleNotUniqueRoleAdmin() throws Exception {
		
		updateCourseTitleNotUnique(ROLE_ADMIN);
	}
	
	@Test
	void updateCourseTitleNotUniqueRoleInstructor() throws Exception {
		
		updateCourseTitleNotUnique(ROLE_INSTRUCTOR);
	}
	
	private void updateCourseTitleNotUnique(String validationRole) throws Exception {
		
		int id = 1;
		
		// check if course id=1 exists
		CourseVO courseVO = courseService.findById(id);
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"getTitle() null");
		assertEquals(COURSE, courseVO.getTitle(),"getTitle() NOK");
		
		// create new course
		CourseVO newCourseVO = ApplicationTestUtils.createCourseVO();
		
		// save new course
		courseService.saveCourse(newCourseVO);
		
		// check if new course exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + COURSES)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		CourseVO dbNewCourseVO = courseService.findByTitle(newCourseVO.getTitle());
		
		assertNotNull(dbNewCourseVO,"dbNewCourseVO null");
		assertNotNull(dbNewCourseVO.getTitle(),"dbNewCourseVO.getTitle() null");
		assertEquals(newCourseVO.getTitle(), dbNewCourseVO.getTitle(),"dbNewCourseVO.getTitle() failure");
		
		// update course title (same value as course id=1)
		String courseTitle = courseVO.getTitle();
		
		// update course with new title
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + COURSES + "/{id}", dbNewCourseVO.getId())
				.param(VALIDATION_ROLE, validationRole)
				.param(COURSE_TITLE, courseTitle)
				.contentType(APPLICATION_JSON_UTF8)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
			.andExpect(jsonPath("$.errors", hasItem(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_TITLE_ALREADY_EXIST.getMessage()), ActionType.CREATE))))
			;
	}
}
