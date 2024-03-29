package org.babinkuk.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.babinkuk.entity.Course;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.Review;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ReviewVO;
import org.mapstruct.BeanMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

/**
 * mapper for the entity @link {@link Course} and its DTO {@link CourseVO}
 * 
 * @author BabinKuk
 */
@Mapper
(
	componentModel = "spring",
	unmappedSourcePolicy = ReportingPolicy.WARN,
	imports = {StringUtils.class, Objects.class},
	//if needed add uses = {add different classes for complex objects}
	uses = {ReviewMapper.class, InstructorMapper.class, StudentMapper.class, StudentMapper.class} 
)
public interface CourseMapper {
	
	public CourseMapper courseMapperInstance = Mappers.getMapper(CourseMapper.class);
	public ReviewMapper reviewMapperInstance = Mappers.getMapper(ReviewMapper.class);
	public StudentMapper studentMapperInstance = Mappers.getMapper(StudentMapper.class);
	public InstructorMapper instructorMapperInstance = Mappers.getMapper(InstructorMapper.class);
	
	@BeforeMapping
	default void beforeMap(@MappingTarget Course entity, CourseVO courseVO) {
		System.out.println(StringUtils.stripToEmpty("@BeforeMapping course: " + new Throwable().getStackTrace()[0].getFileName() + ":" + (new Throwable().getStackTrace()[0].getLineNumber())));
		// instructor
		if (courseVO.getInstructorVO() != null) {
			Instructor instructor = instructorMapperInstance.toEntity(courseVO);
			//instructor.add(entity);
			entity.setInstructor(instructor);
		}
		
//		System.out.println("beforeMapStudents");
//		// students
//		if (!CollectionUtils.isEmpty(courseVO.getStudentsVO())) {
//			Set<Student> students = new HashSet<Student>();
//			for (StudentVO studentVO : courseVO.getStudentsVO()) {
//				Student student = new Student();
//				student.setId(studentVO.getId());
//				student.addCourse(entity);
//				students.add(student);
//			}
//			entity.setStudents(students);
//		}
		
		//System.out.println("beforeMapReviews");
		// reviews
		if (!CollectionUtils.isEmpty(courseVO.getReviewsVO())) {
			List<Review> reviewList = new ArrayList<Review>();
			for (ReviewVO reviewVO : courseVO.getReviewsVO()) {
				Review review = reviewMapperInstance.toEntity(reviewVO);
				reviewList.add(review);
			}
			entity.setReviews(reviewList);
		}
		//System.out.println(entity.toString());
	}
//	
//	@AfterMapping
//	default void afterMap(@MappingTarget CourseVO courseVO, Course entity) {
//		System.out.println("@AfterMapping mapInstructor");
//		// instructor
//		if (entity.getInstructor() != null) {
//			InstructorVO instructorVO = instructorMapperInstance.toVO(entity.getInstructor());
//			courseVO.setInstructorVO(instructorVO);
//		}
//		
//		System.out.println("AfterMapping mapStudents");
//		// students
//		if (!CollectionUtils.isEmpty(entity.getStudents())) {
//			Set<StudentVO> students = new HashSet<StudentVO>();
//			for (Student student : entity.getStudents()) {
//				StudentVO studentVO = studentMapperInstance.toVO(student);
//				students.add(studentVO);
//			}
//			courseVO.setStudentsVO(students);
//		}
//	}
	
	// for insert
	@Named("toEntity")
	@Mapping(source = "instructorVO", target = "instructor")
	@Mapping(source = "reviewsVO", target = "reviews")
	@Mapping(source = "studentsVO", target = "students")
	Course toEntity(CourseVO courseVO);
	
	// for update
	@Named("toEntity")
	@Mapping(source = "instructorVO", target = "instructor")
	@Mapping(source = "reviewsVO", target = "reviews")
	@Mapping(source = "studentsVO", target = "students")
	Course toEntity(CourseVO courseVO, @MappingTarget Course course);
	
	@Named("toVO")
	@Mapping(source = "instructor", target = "instructorVO")
	@Mapping(source = "reviews", target = "reviewsVO")
	@Mapping(source = "students", target = "studentsVO")
	CourseVO toVO(Course course);
	
	@IterableMapping(qualifiedByName = "toVO")
	@BeanMapping(ignoreByDefault = true)
	Iterable<CourseVO> toVO(Iterable<Course> courseList);
}