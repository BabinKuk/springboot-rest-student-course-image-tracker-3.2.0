package org.babinkuk;

import static org.babinkuk.utils.ApplicationTestConstants.COURSE;
import static org.babinkuk.utils.ApplicationTestConstants.DATA_1;
import static org.babinkuk.utils.ApplicationTestConstants.DATA_2;
import static org.babinkuk.utils.ApplicationTestConstants.FILE_1;
import static org.babinkuk.utils.ApplicationTestConstants.FILE_2;
import static org.babinkuk.utils.ApplicationTestConstants.INSTRUCTOR_EMAIL;
import static org.babinkuk.utils.ApplicationTestConstants.INSTRUCTOR_FIRSTNAME;
import static org.babinkuk.utils.ApplicationTestConstants.INSTRUCTOR_HOBBY;
import static org.babinkuk.utils.ApplicationTestConstants.INSTRUCTOR_LASTNAME;
import static org.babinkuk.utils.ApplicationTestConstants.INSTRUCTOR_SALARY;
import static org.babinkuk.utils.ApplicationTestConstants.INSTRUCTOR_STATUS;
import static org.babinkuk.utils.ApplicationTestConstants.INSTRUCTOR_YOUTUBE;
import static org.babinkuk.utils.ApplicationTestConstants.REVIEW;
import static org.babinkuk.utils.ApplicationTestConstants.STUDENT_CITY;
import static org.babinkuk.utils.ApplicationTestConstants.STUDENT_EMAIL;
import static org.babinkuk.utils.ApplicationTestConstants.STUDENT_FIRSTNAME;
import static org.babinkuk.utils.ApplicationTestConstants.STUDENT_LASTNAME;
import static org.babinkuk.utils.ApplicationTestConstants.STUDENT_STATUS;
import static org.babinkuk.utils.ApplicationTestConstants.STUDENT_STREET;
import static org.babinkuk.utils.ApplicationTestConstants.STUDENT_ZIPCODE;

import java.sql.SQLException;
import java.text.MessageFormat;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.dao.ChangeLogRepository;
import org.babinkuk.dao.CourseRepository;
import org.babinkuk.dao.ImageRepository;
import org.babinkuk.dao.InstructorRepository;
import org.babinkuk.dao.ReviewRepository;
import org.babinkuk.dao.StudentRepository;
import org.babinkuk.entity.Address;
import org.babinkuk.entity.Course;
import org.babinkuk.entity.Image;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.InstructorDetail;
import org.babinkuk.entity.Review;
import org.babinkuk.entity.Student;
import org.babinkuk.service.ChangeLogService;
import org.babinkuk.service.CourseService;
import org.babinkuk.service.ImageService;
import org.babinkuk.service.InstructorService;
import org.babinkuk.service.ReviewService;
import org.babinkuk.service.StudentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(ApplicationTest.class);
	
	public static final MediaType APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON;
	
	@Autowired
	protected MockMvc mockMvc;
	
	@Autowired
	protected JdbcTemplate jdbc;
	
	@Autowired
	protected WebApplicationContext webApplicationContext;
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	@Autowired
	protected ObjectMapper objectMApper;
	
	@Autowired
	protected CourseService courseService;
	
	@Autowired
	protected ReviewService reviewService;
	
	@Autowired
	protected InstructorService instructorService;
	
	@Autowired
	protected StudentService studentService;
	
	@Autowired
	protected ImageService imageService;
	
	@Autowired
	protected CourseRepository courseRepository;
	
	@Autowired
	protected StudentRepository studentRepository;
	
	@Autowired
	protected InstructorRepository instructorRepository;
	
	@Autowired
	protected ReviewRepository reviewRepository;
	
	@Autowired
	protected ImageRepository imageRepository;
	
	@Autowired
	protected ChangeLogRepository changeLogRepository;
	
	@Autowired
	protected ChangeLogService changeLogService;
	
	@Value("${info.app.name}")
	String appName;
	
	@Value("${spring.profiles.active}")
	String profiles;

	@Value("${info.app.version}")
	String version;

	@Value("${info.app.build-timestamp}")
	String buildTime;
	
	@Value("${info.app.author}")
	String author;
	
	@Value("${test.message}")
	String customMessage;
	
	@Value("${sql.script.reset.template}")
	private String sqlResetTemplate;
	
	@Value("${sql.script.review.insert}")
	private String sqlAddReview;
	
	@Value("${sql.script.review.delete}")
	private String sqlDeleteReview;
	
	@Value("${sql.script.course.insert}")
	private String sqlAddCourse;
	
	@Value("${sql.script.course.delete}")
	private String sqlDeleteCourse;
	
	@Value("${sql.script.course.update}")
	protected String sqlUpdateCourse;
	
	@Value("${sql.script.user.insert-instructor}")
	private String sqlAddUserInstructor;
	
	@Value("${sql.script.user.insert-student}")
	private String sqlAddUserStudent;
	
	@Value("${sql.script.user.delete}")
	private String sqlDeleteUser;
	
	@Value("${sql.script.instructor.insert}")
	private String sqlAddInstructor;
	
	@Value("${sql.script.instructor.delete}")
	private String sqlDeleteInstructor;
	
	@Value("${sql.script.instructor-detail.insert}")
	private String sqlAddInstructorDetail;
	
	@Value("${sql.script.instructor-detail.delete}")
	private String sqlDeleteInstructorDetail;
	
	@Value("${sql.script.student.insert}")
	private String sqlAddStudent;
	
	@Value("${sql.script.student.delete}")
	private String sqlDeleteStudent;
	
	@Value("${sql.script.course-student.insert}")
	private String sqlAddCourseStudent;
	
	@Value("${sql.script.course-student.delete}")
	private String sqlDeleteCourseStudent;
	
	@Value("${sql.script.image.insert-instructor}")
	private String sqlAddImageInstructor;
	
	@Value("${sql.script.image.insert-student}")
	private String sqlAddImageStudent;
	
	@Value("${sql.script.image.delete}")
	private String sqlDeleteImage;
	
	@Value("${sql.script.change-log.insert}")
	private String sqlAddChangeLog;
	
	@Value("${sql.script.change-log.delete}")
	private String sqlDeleteChangeLog;
	
	@BeforeAll
	public static void setup() {
		
	}
	
//	@Sql("/createTables.sql")
	@BeforeEach
    public void setupDatabase() {
		
		insertData();
		
		jdbc.execute(sqlAddChangeLog);
		
//		// check
//		List<Map<String,Object>> userList = new ArrayList<Map<String,Object>>();
//		userList = jdbc.queryForList("select * from \"user\"");
//		log.info("user size() " + userList.size());
//		for (Map m : userList) {
//			m.forEach((key, value) -> log.info(key + " : " + value));
//		}
//		
//		List<Map<String,Object>> studentList = new ArrayList<Map<String,Object>>();
//		studentList = jdbc.queryForList("select * from student");
//		log.info("student size() " + studentList.size());
//		for (Map m : studentList) {
//			m.forEach((key, value) -> log.info(key + " : " + value));
//		}
//		
//		List<Map<String,Object>> instructorList = new ArrayList<Map<String,Object>>();
//		instructorList = jdbc.queryForList("select * from instructor");
//		log.info("instructor size() " + instructorList.size());
//		for (Map m : instructorList) {
//			m.forEach((key, value) -> log.info(key + " : " + value));
//		}
//		
//		List<Map<String,Object>> courseList = new ArrayList<Map<String,Object>>();
//		courseList = jdbc.queryForList("select * from course");
//		log.info("course size() " + courseList.size());
//		for (Map m : courseList) {
//			m.forEach((key, value) -> log.info(key + " : " + value));
//		}
//		
//		List<Map<String,Object>> reviewList = new ArrayList<Map<String,Object>>();
//		reviewList = jdbc.queryForList("select * from review");
//		log.info("review size() " + reviewList.size());
//		for (Map m : reviewList) {
//			m.forEach((key, value) -> log.info(key + " : " + value));
//		}
	}
	
//	@Sql("/createTables.sql")
	@AfterEach
	public void setupAfterTransaction() throws SQLException {
		
		// delete table data
		jdbc.execute(sqlDeleteCourseStudent);
		jdbc.execute(sqlDeleteStudent);
		jdbc.execute(sqlDeleteReview);
		jdbc.execute(sqlDeleteCourse);
		jdbc.execute(sqlDeleteInstructor);
		jdbc.execute(sqlDeleteInstructorDetail);
		jdbc.execute(sqlDeleteImage);
		jdbc.execute(sqlDeleteUser);
		jdbc.execute(sqlDeleteChangeLog);
		
		// reset id column sequence
		String[] tables = { "\"user\"", "course", "image", "review", "instructor_detail" };
		//DbTestUtil.resetAutoIncrementColumns(webApplicationContext, sqlResetTemplate, tables);
		resetAutoIncrementColumns(sqlResetTemplate, tables);
	}	
	
	@Test
	void basicTest() {
		log.info(MessageFormat.format(
				"\n--------------------------------------------------------------------" + 
				"\nESB microservice {0} is running!" +
				"\nProfile(s): {1}" +
				"\nVersion: {2}" +
				"\nAuthor: {3}" +
				"\nBuilt on: {4}" +
				"\nsomething extra: {5}" +
				"\n--------------------------------------------------------------------",
				appName, profiles, version, author, buildTime, customMessage));
		
	}
	
	public void insertData() {
		
		// create course
		// set id=0: this is to force a save of new item
		Course course = new Course(COURSE);
		course.setId(0);
		
		// create review
		// set id=0: this is to force a save of new item
		Review review = new Review(REVIEW);
		review.setId(0);
		
		course.addReview(review);
		
		// create instructor
		// set id=0: this is to force a save of new item
		Instructor instructor = new Instructor(
				INSTRUCTOR_FIRSTNAME, 
				INSTRUCTOR_LASTNAME, 
				INSTRUCTOR_EMAIL, 
				INSTRUCTOR_STATUS, 
				INSTRUCTOR_SALARY);
		instructor.setId(0);
		
		InstructorDetail instructorDetail = new InstructorDetail();
		instructorDetail.setYoutubeChannel(INSTRUCTOR_YOUTUBE);
		instructorDetail.setHobby(INSTRUCTOR_HOBBY);
		
		instructor.setInstructorDetail(instructorDetail);
		
		// create image
		// set id=0: this is to force a save of new item
		Image instructorImage = new Image();
		instructorImage.setFileName(FILE_1);
		instructorImage.setData(DATA_1);
		instructorImage.setId(0);
		
		instructor.addImage(instructorImage);
		
		// create student
		// set id 0: this is to force a save of new item
		Student student = new Student(
				STUDENT_FIRSTNAME, 
				STUDENT_LASTNAME, 
				STUDENT_EMAIL, 
				STUDENT_STATUS);
		student.setId(0);
		
		Address address = new Address();
		address.setStreet(STUDENT_STREET);
		address.setCity(STUDENT_CITY);
		address.setZipCode(STUDENT_ZIPCODE);
		
		student.setAddress(address);
		
		// create image
		// set id=0: this is to force a save of new item
		Image studentImage = new Image();
		studentImage.setFileName(FILE_2);
		studentImage.setData(DATA_2);
		studentImage.setId(0);
		
		student.addImage(studentImage);
		
		// course enroll instructor
		course.setInstructor(instructor);
		course.addStudent(student);
		
		// save course
		entityManager.persist(course);
		entityManager.flush();
		entityManager.clear();
	}
	
	public void resetAutoIncrementColumns(String sqlResetTemplate, String... tableNames) throws SQLException {
		
		// Create and envoke SQL statements that reset the auto increment columns
		for (String sqlResetArgument: tableNames) {
			//log.info(sqlResetArgument);
			String resetSql = String.format(sqlResetTemplate, sqlResetArgument);
			jdbc.execute(resetSql);
		}
	}
}
