package org.babinkuk.mapper;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.babinkuk.entity.Address;
import org.babinkuk.entity.Student;
import org.babinkuk.vo.ImageVO;
import org.babinkuk.vo.StudentVO;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * mapper for the entity @link {@link Student} and its DTO {@link StudentVO}
 * 
 * @author BabinKuk
 */
@Mapper
(
	componentModel = "spring",
	unmappedSourcePolicy = ReportingPolicy.WARN,
	imports = {StringUtils.class, Objects.class}
	//if needed add uses = {add different classes for complex objects} 
)
public interface StudentMapper {
	
	public StudentMapper studentMapperInstance = Mappers.getMapper(StudentMapper.class);
	public AddressMapper addressMapperInstance = Mappers.getMapper(AddressMapper.class);
	
	@AfterMapping
	default void afterMapStudent(@MappingTarget Student entity, StudentVO studentVO) {
		
		// address
		Address address = addressMapperInstance.toEntity(studentVO, entity);
		entity.setAddress(address);
	}
	
	@AfterMapping
	default void afterMapStudent(@MappingTarget StudentVO studentVO, Student entity) {
		
		// image size
		if (studentVO.getImages() != null) {
			for (ImageVO imageVO : studentVO.getImages()) {
				imageVO.setSize(imageVO.getData().length);
			}
		}
	}
	
	// for insert
	@Named("toEntity")
//	@Mapping(source = "email", target = "email")
	@Mapping(target = "images", ignore = true) // images are not modified here
	Student toEntity(StudentVO studentVO);
	
	// for update
	@Named("toEntity")
//	@Mapping(source = "email", target = "email")
	@Mapping(target = "images", ignore = true) // images are not modified here
	Student toEntity(StudentVO studentVO, @MappingTarget Student entity);
	
	@Named("toVO")
	@Mapping(source = "address.street", target = "street")
	@Mapping(source = "address.city", target = "city")
	@Mapping(source = "address.zipCode", target = "zipCode")
	@Mapping(source = "courses", target = "coursesVO")
	StudentVO toVO(Student student);
	
//	@Named("toVODetails")
//	@Mapping(source = "email", target = "email")
//	@Mapping(source = "student", target= "coursesVO", qualifiedByName = "setCourses")
//	StudentVO toVODetails(Student student);
	
	@IterableMapping(qualifiedByName = "toEntity")
	@BeanMapping(ignoreByDefault = true)
	Iterable<Student> toEntity(Iterable<StudentVO> studentList);
	
	@IterableMapping(qualifiedByName = "toVO")
	@BeanMapping(ignoreByDefault = true)
	Iterable<StudentVO> toVO(Iterable<Student> studentList);
	
//	@Named("setCourses")
//	default Set<CourseVO> setCourses(Student entity) {
//		Set<CourseVO> coursesVO = new HashSet<CourseVO>();
//		// courses
//		if (!CollectionUtils.isEmpty(entity.getCourses())) {
//			for (Course course : entity.getCourses()) {
//				CourseVO courseVO = new CourseVO();
//				courseVO.setId(course.getId());
//				courseVO.setTitle(course.getTitle());
//				coursesVO.add(courseVO);
//			}
//		}
//		return coursesVO;
//	}
}