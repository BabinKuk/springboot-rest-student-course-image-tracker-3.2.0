package org.babinkuk.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.entity.Address;
import org.babinkuk.entity.ChangeLog;
import org.babinkuk.entity.Course;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.InstructorDetail;
import org.babinkuk.entity.Student;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ImageVO;
import org.babinkuk.vo.InstructorVO;
import org.babinkuk.vo.StudentVO;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.babinkuk.utils.ApplicationTestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplicationTestUtils {
	
	public static final Logger log = LogManager.getLogger(ApplicationTestUtils.class);
	
	public static void validateExistingCourse(Course course) {
		
		assertNotNull(course,"course null");
		assertNotNull(course.getTitle(),"getTitle() null");
		assertNotNull(course.getStudents(),"getStudents() null");
		assertNotNull(course.getReviews(),"getReviews() null");
		assertNotNull(course.getInstructor(),"getInstructor() null");
		assertEquals(1, course.getId());
		assertEquals(COURSE, course.getTitle(),"getTitle() NOK");
		assertEquals(INSTRUCTOR_FIRSTNAME, course.getInstructor().getFirstName(),"getInstructor().getFirstName() NOK");
		assertEquals(1, course.getInstructor().getImages().size(),"getInstructor().getImages().size() NOK");
		assertEquals(1, course.getReviews().size(), "getReviews size not 1");
		assertTrue(course.getReviews().stream().anyMatch(review ->
			review.getComment().equals(REVIEW) && review.getId() == 1
		));
		assertEquals(1, course.getStudents().size(), "getStudents size not 1");
		assertTrue(course.getStudents().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getImages().size() == 1 && student.getId() == 2
		));
	}
	
	public static void validateUpdatedCourse(Course course) {
		
		assertNotNull(course,"course null");
		assertNotNull(course.getTitle(),"getTitle() null");
		assertNotNull(course.getStudents(),"getStudents() null");
		assertNotNull(course.getReviews(),"getReviews() null");
		assertNotNull(course.getInstructor(),"getInstructor() null");
		assertEquals(1, course.getId());
		assertEquals(COURSE_UPDATED, course.getTitle(),"getTitle() NOK");
		assertEquals(INSTRUCTOR_FIRSTNAME, course.getInstructor().getFirstName(),"getInstructor().getFirstName() NOK");
		assertEquals(1, course.getInstructor().getImages().size(),"getInstructor().getImages().size() NOK");
		assertEquals(1, course.getReviews().size(), "getReviews size not 1");
		assertTrue(course.getReviews().stream().anyMatch(review ->
			review.getComment().equals(REVIEW) && review.getId() == 1
		));
		assertEquals(1, course.getStudents().size(), "getStudents size not 1");
		assertTrue(course.getStudents().stream().anyMatch(student ->
			student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getImages().size() == 1 && student.getId() == 2
		));
	}
	
	public static void validateNewCourse(Course course) {
		
		assertNotNull(course,"course null");
		assertNotNull(course.getTitle(),"getTitle() null");
		assertNotNull(course.getStudents(),"getStudents() null");
		assertNull(course.getReviews(),"getRevews() null");
		assertNull(course.getInstructor(),"getInstructor() null");
		assertEquals(COURSE_NEW, course.getTitle(),"getTitle() NOK");
		assertEquals(0, course.getStudents().size(), "getStudents size not 1");
	}
	
	public static Course updateCourse(Course course) {
				
		// update with new data
		course.setTitle(COURSE_UPDATED);
		
		return course;
	}
	
	public static void validateExistingInstructor(Instructor instructor) {
		
		assertNotNull(instructor,"instructor null");
		assertEquals(1, instructor.getId());
		assertNotNull(instructor.getFirstName(),"getFirstName() null");
		assertNotNull(instructor.getLastName(),"getLastName() null");
		assertNotNull(instructor.getEmail(),"getEmail() null");
		assertNotNull(instructor.getSalary(),"getSalary() null");
		assertNotNull(instructor.getStatus(),"getStatus() null");
		assertNotNull(instructor.getImages(),"getImages() null");
		assertNotNull(instructor.getInstructorDetail().getYoutubeChannel(),"getYoutubeChannel() null");
		assertNotNull(instructor.getInstructorDetail().getHobby(),"getHobby() null");
		assertEquals(INSTRUCTOR_FIRSTNAME, instructor.getFirstName(),"getFirstName() NOK");
		assertEquals(INSTRUCTOR_LASTNAME, instructor.getLastName(),"getLastName() NOK");
		assertEquals(INSTRUCTOR_EMAIL, instructor.getEmail(),"getEmail() NOK");
		assertEquals(INSTRUCTOR_SALARY, instructor.getSalary(),"getSalary() NOK");
		assertEquals(INSTRUCTOR_STATUS, instructor.getStatus(),"getStatus() NOK");
		assertEquals(1, instructor.getImages().size(), "getImages size not 1");
		//assertThat(instructor.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_1, INSTRUCTOR_IMAGE_1));
		//assertThat(instructor.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_11, INSTRUCTOR_IMAGE_11));
		assertTrue(instructor.getImages().stream().anyMatch(img ->
			img.getFileName().equals(FILE_1) && img.getId() == 1
		));
		assertEquals(INSTRUCTOR_YOUTUBE, instructor.getInstructorDetail().getYoutubeChannel(),"getYoutubeChannel() NOK");
		assertEquals(INSTRUCTOR_HOBBY, instructor.getInstructorDetail().getHobby(),"getHobby() NOK");
		// not neccessary
		assertNotEquals("test hobb", instructor.getInstructorDetail().getHobby(),"getHobby() NOK");
	}
	
	public static void validateUpdatedInstructor(Instructor instructor) {
		
		assertNotNull(instructor,"instructor null");
		assertEquals(1, instructor.getId());
		assertNotNull(instructor.getFirstName(),"getFirstName() null");
		assertNotNull(instructor.getLastName(),"getLastName() null");
		assertNotNull(instructor.getEmail(),"getEmail() null");
		assertNotNull(instructor.getSalary(),"getSalary() null");
		assertNotNull(instructor.getStatus(),"getStatus() null");
		assertNotNull(instructor.getImages(),"getImages() null");
		assertNotNull(instructor.getInstructorDetail().getYoutubeChannel(),"getYoutubeChannel() null");
		assertNotNull(instructor.getInstructorDetail().getHobby(),"getHobby() null");
		assertEquals(INSTRUCTOR_FIRSTNAME_UPDATED, instructor.getFirstName(),"getFirstName() NOK");
		assertEquals(INSTRUCTOR_LASTNAME_UPDATED, instructor.getLastName(),".getLastName() NOK");
		assertEquals(INSTRUCTOR_EMAIL_UPDATED, instructor.getEmail(),"getEmail() NOK");
		assertEquals(INSTRUCTOR_SALARY_UPDATED, instructor.getSalary(),"getSalary() NOK");
		assertEquals(INSTRUCTOR_STATUS_UPDATED, instructor.getStatus(),"getStatus() NOK");
		assertEquals(1, instructor.getImages().size(), "getImages size not 1");
//		assertThat(instructor.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_1, INSTRUCTOR_IMAGE_1));
//		assertThat(instructor.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_11, INSTRUCTOR_IMAGE_11));
		assertTrue(instructor.getImages().stream().anyMatch(img ->
			img.getFileName().equals(FILE_1)// && img.getId() == 1
		));
		assertEquals(INSTRUCTOR_YOUTUBE_UPDATED, instructor.getInstructorDetail().getYoutubeChannel(),"getYoutubeChannel() NOK");
		assertEquals(INSTRUCTOR_HOBBY_UPDATED, instructor.getInstructorDetail().getHobby(),"getHobby() NOK");
		// not neccessary
		assertNotEquals("test hobb", instructor.getInstructorDetail().getHobby(),"getHobby() NOK");
	}
	
	public static void validateNewInstructor(Instructor instructor) {
		
		assertNotNull(instructor,"instructor null");
		//assertEquals(1, instructor.getId());
		assertNotNull(instructor.getFirstName(),"getFirstName() null");
		assertNotNull(instructor.getLastName(),"getLastName() null");
		assertNotNull(instructor.getEmail(),"getEmail() null");
		assertNotNull(instructor.getSalary(),"getSalary() null");
		assertNotNull(instructor.getStatus(),"getStatus() null");
		assertNull(instructor.getImages(),"getImages() null");
		assertNotNull(instructor.getInstructorDetail().getYoutubeChannel(),"getYoutubeChannel() null");
		assertNotNull(instructor.getInstructorDetail().getHobby(),"getHobby() null");
		assertEquals(INSTRUCTOR_FIRSTNAME_NEW, instructor.getFirstName(),"getFirstName() NOK");
		assertEquals(INSTRUCTOR_LASTNAME_NEW, instructor.getLastName(),"getLastName() NOK");
		assertEquals(INSTRUCTOR_EMAIL_NEW, instructor.getEmail(),"getEmail() NOK");
		assertEquals(INSTRUCTOR_SALARY_NEW, instructor.getSalary(),"getSalary() NOK");
		assertEquals(INSTRUCTOR_STATUS_NEW, instructor.getStatus(),"getStatus() NOK");
		//assertEquals(1, instructor.getImages().size(), "getImages size not 1");
		//assertThat(instructor.getImages(), IsMapContaining.hasEntry(INSTRUCTOR_FILE_NEW, INSTRUCTOR_IMAGE_NEW));
		//assertTrue(instructor.getImages().stream().anyMatch(img ->
		//	img.getFileName().equals(FILE_1) && img.getId() == 1
		//));
		assertEquals(INSTRUCTOR_YOUTUBE_NEW, instructor.getInstructorDetail().getYoutubeChannel(),"getYoutubeChannel() NOK");
		assertEquals(INSTRUCTOR_HOBBY_NEW, instructor.getInstructorDetail().getHobby(),"getHobby() NOK");
		// not neccessary
		assertNotEquals("test hobb", instructor.getInstructorDetail().getHobby(),"getHobby() NOK");
	}
	
	public static Instructor updateInstructor(Instructor instructor) {
		
		InstructorDetail instructorDetail = new InstructorDetail();
		instructorDetail.setYoutubeChannel(INSTRUCTOR_YOUTUBE_UPDATED);
		instructorDetail.setHobby(INSTRUCTOR_HOBBY_UPDATED);
				
		// update with new data
		instructor.setFirstName(INSTRUCTOR_FIRSTNAME_UPDATED);
		instructor.setLastName(INSTRUCTOR_LASTNAME_UPDATED);
		instructor.setEmail(INSTRUCTOR_EMAIL_UPDATED);
		instructor.setSalary(INSTRUCTOR_SALARY_UPDATED);
		instructor.setStatus(INSTRUCTOR_STATUS_UPDATED);
		//instructor.getImages().put(INSTRUCTOR_FILE_UPDATED, INSTRUCTOR_IMAGE_UPDATED);
		instructor.setInstructorDetail(instructorDetail);
		
		return instructor;
	}
	
	public static Instructor createInstructor() {
		
		// set id=0: this is to force a save of new item ... instead of update
		Instructor instructor = new Instructor(
				INSTRUCTOR_FIRSTNAME_NEW, 
				INSTRUCTOR_LASTNAME_NEW, 
				INSTRUCTOR_EMAIL_NEW, 
				INSTRUCTOR_STATUS_NEW, 
				INSTRUCTOR_SALARY_NEW);
		instructor.setId(0);
		
		InstructorDetail instructorDetail = new InstructorDetail();
		instructorDetail.setYoutubeChannel(INSTRUCTOR_YOUTUBE_NEW);
		instructorDetail.setHobby(INSTRUCTOR_HOBBY_NEW);
		
		instructor.setInstructorDetail(instructorDetail);
		
		return instructor;
	}
	
	public static void validateExistingStudent(Student student) {
		
		assertNotNull(student,"student null");
		assertEquals(2, student.getId());
		assertNotNull(student.getFirstName(),"getFirstName() null");
		assertNotNull(student.getLastName(),"getLastName() null");
		assertNotNull(student.getEmail(),"getEmail() null");
		assertNotNull(student.getStatus(),"getStatus() null");
		assertNotNull(student.getImages(),"getImages() null");
		assertNotNull(student.getAddress().getCity(),"getCity() null");
		assertNotNull(student.getAddress().getStreet(),"getStreet() null");
		assertNotNull(student.getAddress().getZipCode(),"getZipCode() null");
		assertEquals(STUDENT_FIRSTNAME, student.getFirstName(),"getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME, student.getLastName(),"getLastName() NOK");
		assertEquals(STUDENT_EMAIL, student.getEmail(),"getEmail() NOK");
		assertEquals(STUDENT_STATUS, student.getStatus(),"getStatus() NOK");
		assertEquals(1, student.getImages().size(), "getImages size not 1");
		//assertThat(student.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_2, STUDENT_IMAGE_2));
		//assertThat(student.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_22, STUDENT_IMAGE_22));
		assertTrue(student.getImages().stream().anyMatch(img ->
			img.getFileName().equals(FILE_2) && img.getId() == 2
		));
		assertEquals(STUDENT_STREET, student.getAddress().getStreet(),"getStreet() NOK");
		assertEquals(STUDENT_CITY, student.getAddress().getCity(),"getCity() NOK");
		assertEquals(STUDENT_ZIPCODE, student.getAddress().getZipCode(),"getZipCode() NOK");
	}
	
	public static void validateUpdatedStudent(Student student) {
		
		assertNotNull(student,"student null");
		assertEquals(2, student.getId());
		assertNotNull(student.getFirstName(),"getFirstName() null");
		assertNotNull(student.getLastName(),"getLastName() null");
		assertNotNull(student.getEmail(),"getEmail() null");
		assertNotNull(student.getStatus(),"getStatus() null");
		assertNotNull(student.getImages(),"getImages() null");
		assertNotNull(student.getAddress().getCity(),"getCity() null");
		assertNotNull(student.getAddress().getStreet(),"getStreet() null");
		assertNotNull(student.getAddress().getZipCode(),"getZipCode() null");
		assertEquals(STUDENT_FIRSTNAME_UPDATED, student.getFirstName(),"getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_UPDATED, student.getLastName(),"getLastName() NOK");
		assertEquals(STUDENT_EMAIL_UPDATED, student.getEmail(),"getEmail() NOK");
		assertEquals(STUDENT_STATUS_UPDATED, student.getStatus(),"getStatus() NOK");
		assertEquals(1, student.getImages().size(), "getImages size not 1");
		//assertThat(student.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_2, STUDENT_IMAGE_2));
		//assertThat(student.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_22, STUDENT_IMAGE_22));
		//assertThat(student.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_UPDATED, STUDENT_IMAGE_UPDATED));
		assertTrue(student.getImages().stream().anyMatch(img ->
			img.getFileName().equals(FILE_2) && img.getId() == 2
		));
		assertEquals(STUDENT_STREET_UPDATED, student.getAddress().getStreet(),"getStreet() NOK");
		assertEquals(STUDENT_CITY_UPDATED, student.getAddress().getCity(),"getCity() NOK");
		assertEquals(STUDENT_ZIPCODE_UPDATED, student.getAddress().getZipCode(),"getZipCode() NOK");
	}
	
	public static void validateNewStudent(Student student) {
		
		assertNotNull(student,"student null");
		//assertEquals(1, student.getId());
		assertNotNull(student.getFirstName(),"getFirstName() null");
		assertNotNull(student.getLastName(),"getLastName() null");
		assertNotNull(student.getEmail(),"getEmail() null");
		assertNotNull(student.getStatus(),"getStatus() null");
		assertNull(student.getImages(),"getImages() null");
		assertNotNull(student.getAddress().getCity(),"getCity() null");
		assertNotNull(student.getAddress().getStreet(),"getStreet() null");
		assertNotNull(student.getAddress().getZipCode(),"getZipCode() null");
		assertEquals(STUDENT_FIRSTNAME_NEW, student.getFirstName(),"getFirstName() NOK");
		assertEquals(STUDENT_LASTNAME_NEW, student.getLastName(),"getLastName() NOK");
		assertEquals(STUDENT_EMAIL_NEW, student.getEmail(),"getEmail() NOK");
		assertEquals(STUDENT_STATUS_NEW, student.getStatus(),"getStatus() NOK");
		//assertEquals(1, student.getImages().size(), "getImages size not 1");
		//assertThat(student.getImages(), IsMapContaining.hasEntry(STUDENT_FILE_NEW, STUDENT_IMAGE_NEW));
		//assertTrue(student.getImages().stream().anyMatch(img ->
		//	img.getFileName().equals(FILE_2) && img.getId() == 2
		//));
		assertEquals(STUDENT_STREET_NEW, student.getAddress().getStreet(),"getStreet() NOK");
		assertEquals(STUDENT_CITY_NEW, student.getAddress().getCity(),"getCity() NOK");
		assertEquals(STUDENT_ZIPCODE_NEW, student.getAddress().getZipCode(),"getZipCode() NOK");
	}
	
	public static Student updateStudent(Student student) {
		
		Address address = new Address();
		address.setStreet(STUDENT_STREET_UPDATED);
		address.setCity(STUDENT_CITY_UPDATED);
		address.setZipCode(STUDENT_ZIPCODE_UPDATED);
				
		// update with new data
		student.setFirstName(STUDENT_FIRSTNAME_UPDATED);
		student.setLastName(STUDENT_LASTNAME_UPDATED);
		student.setEmail(STUDENT_EMAIL_UPDATED);
		student.setStatus(STUDENT_STATUS_UPDATED);
		student.setAddress(address);
		
		return student;
	}
	
	public static Student createStudent() {
		
		// set id 0: this is to force a save of new item ... instead of update
		Student student = new Student(
				STUDENT_FIRSTNAME_NEW, 
				STUDENT_LASTNAME_NEW, 
				STUDENT_EMAIL_NEW, 
				STUDENT_STATUS_NEW);
		student.setId(0);
		
		Address address = new Address();
		address.setStreet(STUDENT_STREET_NEW);
		address.setCity(STUDENT_CITY_NEW);
		address.setZipCode(STUDENT_ZIPCODE_NEW);
		
		student.setAddress(address);
		
		return student;
	}
	
	public static InstructorVO updateExistingInstructorVO(InstructorVO instructorVO) {
		
		// update with new data
		instructorVO.setFirstName(INSTRUCTOR_FIRSTNAME_UPDATED);
		instructorVO.setLastName(INSTRUCTOR_LASTNAME_UPDATED);
		instructorVO.setEmail(INSTRUCTOR_EMAIL_UPDATED);
		instructorVO.setYoutubeChannel(INSTRUCTOR_YOUTUBE_UPDATED);
		instructorVO.setHobby(INSTRUCTOR_HOBBY_UPDATED);
		instructorVO.setSalary(INSTRUCTOR_SALARY_UPDATED);
		instructorVO.setStatus(INSTRUCTOR_STATUS_UPDATED);
		//instructorVO.getImages().put(INSTRUCTOR_FILE_UPDATED, INSTRUCTOR_IMAGE_UPDATED);
		
		return instructorVO;
	}
	
	public static InstructorVO createInstructorVO() {
		
		// set id 0: this is to force a save of new item ... instead of update
		InstructorVO instructorVO = new InstructorVO(
				INSTRUCTOR_FIRSTNAME_NEW, 
				INSTRUCTOR_LASTNAME_NEW, 
				INSTRUCTOR_EMAIL_NEW, 
				INSTRUCTOR_YOUTUBE_NEW, 
				INSTRUCTOR_HOBBY_NEW,
				INSTRUCTOR_STATUS_NEW);
		instructorVO.setId(0);
		instructorVO.setSalary(INSTRUCTOR_SALARY_NEW);
		
		return instructorVO;
	}
	
	public static StudentVO updateExistingStudentVO(StudentVO studentVO) {
		
		// update with new data
		studentVO.setFirstName(STUDENT_FIRSTNAME_UPDATED);
		studentVO.setLastName(STUDENT_LASTNAME_UPDATED);
		studentVO.setEmail(STUDENT_EMAIL_UPDATED);
		studentVO.setStreet(STUDENT_STREET_UPDATED);
		studentVO.setCity(STUDENT_CITY_UPDATED);
		studentVO.setZipCode(STUDENT_ZIPCODE_UPDATED);
		studentVO.setStatus(STUDENT_STATUS_UPDATED);
		//studentVO.getImages().put(STUDENT_FILE_UPDATED, STUDENT_IMAGE_UPDATED);
		
		return studentVO;
	}
	
	public static StudentVO createStudentVO() {
		
		// set id 0: this is to force a save of new item ... instead of update
		StudentVO studentVO = new StudentVO(
				STUDENT_FIRSTNAME_NEW, 
				STUDENT_LASTNAME_NEW, 
				STUDENT_EMAIL_NEW, 
				STUDENT_STATUS_NEW);
		
		studentVO.setId(0);
		studentVO.setStreet(STUDENT_STREET_NEW);
		studentVO.setCity(STUDENT_CITY_NEW);
		studentVO.setZipCode(STUDENT_ZIPCODE_NEW);
		
		return studentVO;
	}
	
	public static CourseVO createCourseVO() {
		
		// create course
		// set id 0: this is to force a save of new item ... instead of update
		CourseVO courseVO = new CourseVO(COURSE_NEW);
		courseVO.setId(0);
		
		return courseVO;
	}
	
	public static CourseVO updateExistingCourseVO(CourseVO courseVO) {
		
		// update with new data
		courseVO.setTitle(COURSE_UPDATED);
		
		return courseVO;
	}
	
	public static MockMultipartFile createMultipartFile() {
		
		return new MockMultipartFile(FILE_NEW, FILE_NEW, MediaType.TEXT_PLAIN_VALUE, DATA_NEW);
	}
	
	public static ImageVO createImageVO() {
		
		// create course
		// set id 0: this is to force a save of new item ... instead of update
		ImageVO imageVO = new ImageVO();
		imageVO.setFileName(FILE_NEW);
		imageVO.setData(DATA_NEW);
		imageVO.setId(0);
		
		return imageVO;
	}
	public static ImageVO updateExistingImageVO(ImageVO imageVO) {
		
		// update with new data
		imageVO.setFileName(FILE_UPDATED);
		return imageVO;
	}
	
	public static void validateExistingChangeLog(ChangeLog chLog) {
		//nastaviti sutra!!!
		assertNotNull(chLog,"ChangeLog null");
		//assertNotNull(chLog.getTitle(),"getTitle() null");
		//assertNotNull(chLog.getStudents(),"getStudents() null");
		//assertNotNull(chLog.getReviews(),"getReviews() null");
		//assertNotNull(chLog.getInstructor(),"getInstructor() null");
		assertEquals(1, chLog.getChloId());
		//assertEquals(COURSE, chLog.getTitle(),"getTitle() NOK");
		//assertEquals(INSTRUCTOR_FIRSTNAME, chLog.getInstructor().getFirstName(),"getInstructor().getFirstName() NOK");
		//assertEquals(1, chLog.getInstructor().getImages().size(),"getInstructor().getImages().size() NOK");
		//assertEquals(1, chLog.getReviews().size(), "getReviews size not 1");
		//assertTrue(chLog.getReviews().stream().anyMatch(review ->
		//	review.getComment().equals(REVIEW) && review.getId() == 1
		//));
		//assertEquals(1, chLog.getStudents().size(), "getStudents size not 1");
		//assertTrue(chLog.getStudents().stream().anyMatch(student ->
		//	student.getFirstName().equals(STUDENT_FIRSTNAME) && student.getImages().size() == 1 && student.getId() == 2
		//));
	}
}
