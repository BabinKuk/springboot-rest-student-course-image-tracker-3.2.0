package org.babinkuk.mapper;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.InstructorDetail;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ImageVO;
import org.babinkuk.vo.InstructorVO;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * mapper for the entity @link {@link Instructor} and its DTO {@link InstructorVO}
 * 
 * @author BabinKuk
 */
@Mapper
(
	componentModel = "spring",
	unmappedSourcePolicy = ReportingPolicy.WARN,
	imports = {StringUtils.class, Objects.class},
	uses = {InstructorDetailMapper.class}
)
public interface InstructorMapper {
	
	public InstructorMapper instructorMapperInstance = Mappers.getMapper(InstructorMapper.class);
	public InstructorDetailMapper instructorDetailMapperInstance = Mappers.getMapper(InstructorDetailMapper.class);
	public ImageMapper imageMapperInstance = Mappers.getMapper(ImageMapper.class);
	
	@BeforeMapping
	default void beforeMap(@MappingTarget Instructor entity, InstructorVO instructorVO) {
//		// images are not modified here
//		if (!CollectionUtils.isEmpty(instructor.getImages())) {
//			List<Image> imageList = new ArrayList<Image>();
//			for (ImageVO imageVO : instructorVO.getImages()) {
//				Image image = imageMapperInstance.toEntity(imageVO);
//				imageList.add(image);
//			}
//			entity.setImages(imageList);
//		}
	}
	
	@Named("setDetails")
	default InstructorDetail setDetails(InstructorVO instructorVO) {
		// instructor details
		InstructorDetail instructorDetail = instructorDetailMapperInstance.toEntity(instructorVO);
		Instructor entity = new Instructor();
		entity.setId(instructorVO.getId());
		instructorDetail.setInstructor(entity);
		System.out.println(instructorDetail.toString());
		return instructorDetail;
	}
	
	@AfterMapping
	default void afterMapInstructor(@MappingTarget Instructor entity, InstructorVO instructorVO) {
		// instructor details
		InstructorDetail instructorDetail = instructorDetailMapperInstance.toEntity(instructorVO, entity);
		instructorDetail.setInstructor(entity);
		entity.setInstructorDetail(instructorDetail);
	}
	
	// for insert
	@Named("toEntity")
//	@Mapping(source = "email", target = "email")
	@Mapping(source = "instructorVO", target = "instructorDetail", qualifiedByName = "setDetails")
	@Mapping(target = "images", ignore = true) // images are not modified here
	Instructor toEntity(InstructorVO instructorVO);
	
	// for update
	@Named("toEntity")
//	@Mapping(source = "email", target = "email")
	@Mapping(target = "images", ignore = true) // images are not modified here
	Instructor toEntity(InstructorVO instructorVO, @MappingTarget Instructor instructor);
	
	// when saving course
	@Named("toEntity")
	@Mapping(target = "firstName", ignore = true)
	@Mapping(target = "lastName", ignore = true)
	@Mapping(target = "email", ignore = true)
	@Mapping(target = "instructorDetail", ignore = true)
	Instructor toEntity(CourseVO courseVO);
    
	@Named("toVO")
//	@Mapping(source = "email", target = "email")
	//@Mapping(target = "courses.instructorVO", ignore= true)
	//@Mapping(target = "courses.reviewsVO", ignore= true)
//	@Mapping(target = "courses.studentsVO", ignore= true)
	InstructorVO toVO(Instructor instructor);
	
	@IterableMapping(qualifiedByName = "toVO")
	@BeanMapping(ignoreByDefault = true)
	Iterable<InstructorVO> toVO(Iterable<Instructor> instructorLst);
	
	@AfterMapping
	default void setDetails(@MappingTarget InstructorVO instructorVO, Instructor entity) {
		// instructor details
		if (entity.getInstructorDetail() != null) {
			//System.out.println(entity.getInstructorDetail());
			instructorVO.setYoutubeChannel(entity.getInstructorDetail().getYoutubeChannel());
			instructorVO.setHobby(entity.getInstructorDetail().getHobby());
		}
		
		// image size
		if (instructorVO.getImages() != null) {
			for (ImageVO imageVO : instructorVO.getImages()) {
				imageVO.setSize(imageVO.getData().length);
			}
		}
	}
}