package org.babinkuk.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.MessagePool;
import org.babinkuk.service.CourseService;
import org.babinkuk.service.InstructorService;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.InstructorVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
public class InstructorControllerTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(InstructorControllerTest.class);
	
	@Autowired
	private InstructorService instructorService;
	
	@Autowired
	private CourseService courseService;
	
	@Test
	void getAllInstructors() throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			;

		// add another instructor
		// set id 0: this is to force a save of new item ... instead of update
		InstructorVO instructorVO = ApplicationTestUtils.createInstructorVO();
		
		instructorService.saveInstructor(instructorVO);
				
		// get all instructors (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all instructors (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, ROLE_STUDENT)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all instructors (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
			//	.param(TestUtils.VALIDATION_ROLE, "TestUtils.ROLE_STUDENT")
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// get all instructors (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
	}
	
	@Test
	void getInstructorRoleAdmin() throws Exception {

		getInstructor(ROLE_ADMIN);
	}
	
	@Test
	void getInstructorRoleInstructor() throws Exception {

		getInstructor(ROLE_INSTRUCTOR);
	}
	
	@Test
	void getInstructorRoleStudent() throws Exception {

		getInstructor(ROLE_STUDENT);
	}
	
	@Test
	void getInstructorNoRole() throws Exception {

		getInstructor("");
	}
	
	@Test
	void getInstructorRoleNotExist() throws Exception {

		getInstructor(ROLE_NOT_EXIST);
	}
	
	private void getInstructor(String validationRole) throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is(INSTRUCTOR_FIRSTNAME))) // verify json element
			;

		// get instructor with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", 2)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), 2)))) // verify json element
			;
	}
	
	@Test
	void addInstructorRoleAdmin() throws Exception {

		addInstructorSuccess(ROLE_ADMIN);
	}

	@Test
	void addInstructorRoleInstructor() throws Exception {

		addInstructorSuccess(ROLE_INSTRUCTOR);
	}
	
	private void addInstructorSuccess(String validationRole) throws Exception {
		
		// create instructor
		InstructorVO instructorVO = ApplicationTestUtils.createInstructorVO();
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(INSTRUCTOR_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// additional check
		instructorVO = instructorService.findByEmail(INSTRUCTOR_EMAIL_NEW);
		
		assertNotNull(instructorVO,"instructorVO null");
		assertNotNull(instructorVO.getFirstName(),"getFirstName() null");
		assertNotNull(instructorVO.getLastName(),"getLastName() null");
		assertNotNull(instructorVO.getEmail(),"getEmail() null");
		assertEquals(INSTRUCTOR_FIRSTNAME_NEW, instructorVO.getFirstName(),"getFirstName() NOK");
		assertEquals(INSTRUCTOR_LASTNAME_NEW, instructorVO.getLastName(),"getLastName() NOK");
	}
	
	@Test
	void addInstructorRoleStudent() throws Exception {

		addInstructorFail(ROLE_STUDENT);
	}
	
	@Test
	void addInstructorNoRole() throws Exception {
		
		addInstructorFail(null);
	}
	
	@Test
	void addInstructorRoleNotExist() throws Exception {
		
		addInstructorFail(ROLE_NOT_EXIST);
	}
	
	private void addInstructorFail(String validationRole) throws Exception {
		
		// create instructor
		InstructorVO instructorVO = ApplicationTestUtils.createInstructorVO();
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is still size 1
			;
	}
	
	@Test
	void updateInstructorRoleAdmin() throws Exception {

		updateInstructorSuccess(ROLE_ADMIN);
	}
	
	@Test
	void updateInstructorRoleInstructor() throws Exception {

		updateInstructorSuccess(ROLE_INSTRUCTOR);
	}
	
	private void updateInstructorSuccess(String validationRole) throws Exception {
		
		// check if instructor id=1 exists
		InstructorVO instructorVO = instructorService.findById(1);
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"getFirstName() null");
		assertEquals(INSTRUCTOR_FIRSTNAME, instructorVO.getFirstName(),"getFirstName() failure");
		
		// update instructor
		instructorVO = ApplicationTestUtils.updateExistingInstructorVO(instructorVO);
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(INSTRUCTOR_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is(INSTRUCTOR_FIRSTNAME_UPDATED))) // verify json element
			.andExpect(jsonPath("$.lastName", is(INSTRUCTOR_LASTNAME_UPDATED))) // verify json element
			.andExpect(jsonPath("$.email", is(INSTRUCTOR_EMAIL_UPDATED))) // verify json element
			.andExpect(jsonPath("$.youtubeChannel", is(INSTRUCTOR_YOUTUBE_UPDATED))) // verify json element
			.andExpect(jsonPath("$.hobby", is(INSTRUCTOR_HOBBY_UPDATED))) // verify json element
			.andExpect(jsonPath("$.status", is(INSTRUCTOR_STATUS_UPDATED.label))) // verify json element
			.andExpect(jsonPath("$.salary", is(INSTRUCTOR_SALARY_UPDATED))) // verify json element
			.andExpect(jsonPath("$.images", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.images[0].id", is(1))) // verify json element
			.andExpect(jsonPath("$.images[0].fileName", is(FILE_1))) // verify json element
			;
	}
	
	@Test
	void updateInstructorRoleStudent() throws Exception {

		updateInstructorFail(ROLE_STUDENT);
	}
	
	@Test
	void updateInstructorNoRole() throws Exception {

		updateInstructorFail("");
	}
	
	@Test
	void updateInstructorRoleNotExist() throws Exception {

		updateInstructorFail(ROLE_NOT_EXIST);
	}
	
	private	void updateInstructorFail(String validationRole) throws Exception {
		
		// check if instructor id=1 exists
		InstructorVO instructorVO = instructorService.findById(1);
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"getFirstName() null");
		assertEquals(INSTRUCTOR_FIRSTNAME, instructorVO.getFirstName(),"getFirstName() failure");
		
		// update instructor
		instructorVO = ApplicationTestUtils.updateExistingInstructorVO(instructorVO);
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(instructorVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// additional check
		// get instructor with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is(INSTRUCTOR_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.lastName", is(INSTRUCTOR_LASTNAME))) // verify json element
			.andExpect(jsonPath("$.email", is(INSTRUCTOR_EMAIL))) // verify json element
			.andExpect(jsonPath("$.youtubeChannel", is(INSTRUCTOR_YOUTUBE))) // verify json element
			.andExpect(jsonPath("$.hobby", is(INSTRUCTOR_HOBBY))) // verify json element
			.andExpect(jsonPath("$.images", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.images[0].id", is(1))) // verify json element
			.andExpect(jsonPath("$.images[0].fileName", is(FILE_1))) // verify json element
			;
	}
	
	@Test
	void deleteInstructorRoleAdmin() throws Exception {

		deleteInstructorSuccess(ROLE_ADMIN);
	}
	
	@Test
	void deleteInstructorRoleInstructor() throws Exception {

		deleteInstructorFail(ROLE_INSTRUCTOR);
	}
	
	@Test
	void deleteInstructorRoleStudent() throws Exception {

		deleteInstructorFail(ROLE_STUDENT);
	}

	@Test
	void deleteInstructorNoRole() throws Exception {
		
		deleteInstructorFail(null);
	}
	
	@Test
	void deleteInstructorRoleNotExist() throws Exception {

		deleteInstructorFail(ROLE_NOT_EXIST);
	}
	
	private void deleteInstructorSuccess(String validationRole) throws Exception {
		
		// check if instructor id=1 exists
		int id = 1;
		InstructorVO instructorVO = instructorService.findById(id);
		//log.info(instructorVO.toString());
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"getFirstName() null");
		assertEquals(INSTRUCTOR_FIRSTNAME, instructorVO.getFirstName(),"getFirstName() failure");
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// delete instructor
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + INSTRUCTORS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(INSTRUCTOR_DELETE_SUCCESS)))) // verify json element
			;
		
		// get instructor with id=1 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), id)))) //verify json element
			;
	}
	
	private void deleteInstructorFail(String validationRole) throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// check if instructor id=1 exists
		int id = 1;
		InstructorVO instructorVO = instructorService.findById(id);
		
		assertNotNull(instructorVO,"instructorVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"getFirstName() null");
		assertEquals(INSTRUCTOR_FIRSTNAME, instructorVO.getFirstName(),"getFirstName() failure");
				
		// delete instructor
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + INSTRUCTORS + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}
	
	@Test
	void enrollCourseRoleAdmin() throws Exception {
		
		enrollCourseSuccess(ROLE_ADMIN);
	}
	
	private void enrollCourseSuccess(String validationRole) throws Exception {
		
		int id = 1;
		
		validateCourse();
		
		validateInstructor();
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// set course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS + INSTRUCTOR_ENROLL, id, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(INSTRUCTOR_SAVE_SUCCESS)))) // verify json element
			;
		
		// check course
		CourseVO courseVO = courseService.findById(id);
		
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() not null");
		assertEquals(1, courseVO.getInstructorVO().getId(),"courseVO.getInstructorVO().getId()");
		assertEquals(INSTRUCTOR_FIRSTNAME, courseVO.getInstructorVO().getFirstName(),"courseVO.getInstructorVO().getFirstName()");
		
		// check instructor
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is(INSTRUCTOR_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.courses", hasSize(1))) // verify that json root element $ is now size 1
			.andExpect(jsonPath("$.courses[0].id", is(1)))
			.andExpect(jsonPath("$.courses[0].title", is(COURSE)))
			;
		
		// enroll instructor (non existing course courseId=2)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS + INSTRUCTOR_ENROLL, id, 2)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), 2)))) // verify json element
			;
		
		// enroll non existing instructor id=2
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS + INSTRUCTOR_ENROLL, 2, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), 2)))) // verify json element
			;
	}
	
	@Test
	void enrollCourseRoleInstructor() throws Exception {

		enrollCourseFail(ROLE_INSTRUCTOR);
	}
	
	@Test
	void enrollCourseRoleStudent() throws Exception {

		enrollCourseFail(ROLE_STUDENT);
	}
	
	@Test
	void enrollInstructorNoRole() throws Exception {

		enrollCourseFail(null);
	}
	
	private void enrollCourseFail(String validationRole) throws Exception {
		
		int id = 1;
		
		validateCourse();
		
		validateInstructor();
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// set course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS + INSTRUCTOR_ENROLL, id, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.ENROLL)))) // verify json root element message
			;
		
		// check course
		CourseVO courseVO = courseService.findById(id);
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		assertEquals(COURSE, courseVO.getTitle(),"courseVO.getTitle() failure");
		assertNotNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// check instructor
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is(INSTRUCTOR_FIRSTNAME))) // verify json element
			;
		
		// enroll instructor (non existing course courseId=2)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS + INSTRUCTOR_ENROLL, id, 2)		
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), 2)))) // verify json element
			;
		
		// enroll non existing instructor id=2
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS + INSTRUCTOR_ENROLL, 2, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), 2)))) // verify json element
			;
	}
	
	@Test
	void enrollInstructorRoleNotExist() throws Exception {
		
		int id = 1;
		
		validateCourse();
		
		validateInstructor();
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// set course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS + INSTRUCTOR_ENROLL, id, id)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.ENROLL)))) // verify json root element message
			;
		
		// check course
		CourseVO courseVO = courseService.findById(id);
		//log.info(courseVO.toString());
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"courseVO.getTitle() null");
		assertEquals(COURSE, courseVO.getTitle(),"courseVO.getTitle() failure");
		assertNotNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() null");
		assertEquals(1, courseVO.getStudentsVO().size());
		
		// check instructor
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", 1)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is(INSTRUCTOR_FIRSTNAME))) // verify json element
			;
		
		// enroll instructor (non existing course courseId=2)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS + INSTRUCTOR_ENROLL, id, 2)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
		
		// enroll non existing instructor id=2
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS + INSTRUCTOR_ENROLL, 2, id)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
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
	
	private void withdrawCourseSuccess(String validationRole) throws Exception {
		
		int id = 1;
		
		validateCourse();
		
		validateInstructor();
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// set course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS + INSTRUCTOR_ENROLL, id, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(INSTRUCTOR_SAVE_SUCCESS)))) // verify json element
			;
		
		// check course
		CourseVO courseVO = courseService.findById(id);
		//log.info(courseVO.toString());
		
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() not null");
		assertEquals(1, courseVO.getInstructorVO().getId(),"courseVO.getInstructorVO().getId()");
		assertEquals(INSTRUCTOR_FIRSTNAME, courseVO.getInstructorVO().getFirstName(),"courseVO.getInstructorVO().getFirstName()");
		
		// check instructor
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is(INSTRUCTOR_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.courses", hasSize(1))) // verify that json root element $ is now size 1
			.andExpect(jsonPath("$.courses[0].id", is(1)))
			.andExpect(jsonPath("$.courses[0].title", is(COURSE)))
			;
		
		// now withdraw instructor from course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS + INSTRUCTOR_WITHDRAW, id, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(MessagePool.getMessage(INSTRUCTOR_SAVE_SUCCESS)))) // verify json element
			;
		
		// check course
		validateCourse();
		
		// check instructor
		validateInstructor();

		// withdraw instructor (non existing course courseId=2)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS + INSTRUCTOR_WITHDRAW, id, 2)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), 2)))) // verify json element
			;
		
		// withdraw non existing instructor id=2
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS + INSTRUCTOR_WITHDRAW, 2, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), 2)))) // verify json element
			;
	}
	
	@Test
	void withdrawCourseRoleInstructor() throws Exception {

		withdrawCourseFail(ROLE_INSTRUCTOR);
	}

	@Test
	void withdrawCourseRoleStudent() throws Exception {

		withdrawCourseFail(ROLE_STUDENT);
	}
	
	private void withdrawCourseFail(String validationRole) throws Exception {
		
		int id = 1;
		
		InstructorVO instructorVO = instructorService.findById(id);
		
		validateInstructor();
		
		CourseVO courseVO = courseService.findById(id);
		
		validateCourse();

		// set course
		instructorService.setCourse(instructorVO, courseVO, ActionType.ENROLL);
		
		// fetch again
		instructorVO = instructorService.findById(1);
		
		// assert
		validateInstructor();
		
		// assert course
		assertEquals(1, instructorVO.getCourses().size(), "instructors.getCourses size not 1");
		assertTrue(instructorVO.getCourses().stream().anyMatch(course ->
			course.getTitle().equals(COURSE) && course.getId() == 1
		));
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// now withdraw instructor from course
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS + INSTRUCTOR_WITHDRAW, id, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.WITHDRAW)))) // verify json root element message
			;
		
		// check course
		courseVO = courseService.findById(id);
		
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getInstructorVO(),"courseVO.getInstructorVO() not null");
		assertEquals(1, courseVO.getInstructorVO().getId(),"courseVO.getInstructorVO().getId()");
		assertEquals(INSTRUCTOR_FIRSTNAME, courseVO.getInstructorVO().getFirstName(),"courseVO.getInstructorVO().getFirstName()");
		
		// check instructor
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is(INSTRUCTOR_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.courses", hasSize(1))) // verify that json root element $ is now size 1
			.andExpect(jsonPath("$.courses[0].id", is(1)))
			.andExpect(jsonPath("$.courses[0].title", is(COURSE)))
			;
		
		// withdraw instructor (non existing course courseId=2)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS + INSTRUCTOR_WITHDRAW, id, 2)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_COURSE_ID_NOT_FOUND.getMessage()), 2)))) // verify json element
			;
		
		// withdraw non existing instructor id=2
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + INSTRUCTORS + INSTRUCTOR_WITHDRAW, 2, id)
				.param(VALIDATION_ROLE, validationRole)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), 2)))) // verify json element
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
		
		validateInstructor();
		
		// create mock file
		MockMultipartFile file = ApplicationTestUtils.createMultipartFile();
				
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// upload file
		mockMvc.perform(MockMvcRequestBuilders.multipart(ROOT + INSTRUCTORS + "/{id}" + "/upload", 1)
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
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + INSTRUCTORS + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 2
			.andExpect(jsonPath("$.firstName", is(INSTRUCTOR_FIRSTNAME))) // verify json element
			.andExpect(jsonPath("$.lastName", is(INSTRUCTOR_LASTNAME))) // verify json element
			.andExpect(jsonPath("$.images", hasSize(2))) // verify json element
			;
		
		// upload file (non existing student)
		mockMvc.perform(MockMvcRequestBuilders.multipart(ROOT + INSTRUCTORS + "/{id}" + "/upload", 22)
				.file(FILE, file.getBytes())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath(API_RESPONSE_MESSAGE, is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
	}
	
	@Test
	void uploadImageRoleNotExist() throws Exception {
		
		validateInstructor();
		
		String validationRole = ROLE_NOT_EXIST;
		
		// create mock file
		MockMultipartFile file = ApplicationTestUtils.createMultipartFile();
				
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// upload file
		mockMvc.perform(MockMvcRequestBuilders.multipart(ROOT + INSTRUCTORS + "/{id}" + "/upload", 2)
				.file(FILE, file.getBytes())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			;
	}
	
	private void validateCourse() {
		
		// check if course id=1 exists
		int id = 1;
		CourseVO courseVO = courseService.findById(id);
		
		assertNotNull(courseVO,"courseVO null");
		assertEquals(1, courseVO.getId());
		assertNotNull(courseVO.getTitle(),"getTitle() null");
		assertEquals(COURSE, courseVO.getTitle(),"getTitle() failure");
		assertEquals(1, courseVO.getStudentsVO().size());
	}
	
	private void validateInstructor() {
		
		// check if instructor id=1 exists
		int id = 1;
		InstructorVO instructorVO = instructorService.findById(id);
		
		assertNotNull(instructorVO,"courseVO null");
		assertEquals(1, instructorVO.getId());
		assertNotNull(instructorVO.getFirstName(),"getFirstName() null");
		assertEquals(INSTRUCTOR_FIRSTNAME, instructorVO.getFirstName(),"getFirstName() failure");
	}
}
