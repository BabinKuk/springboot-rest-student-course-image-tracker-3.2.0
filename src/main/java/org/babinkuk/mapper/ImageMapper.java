package org.babinkuk.mapper;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.babinkuk.entity.Image;
import org.babinkuk.vo.ImageVO;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * mapper for the entity @link {@link Image} and its DTO {@link ImageVO}
 * 
 * @author BabinKuk
 */
@Mapper
(
	componentModel = "spring",
	unmappedSourcePolicy = ReportingPolicy.WARN,
	imports = {StringUtils.class, Objects.class},
	//if needed add uses = {add different classes for complex objects}
	uses = {/*CourseMapper.class*/} 
)
public interface ImageMapper {
	
	public ImageMapper imageMapperInstance = Mappers.getMapper(ImageMapper.class);
	
	// for insert
	@Named("toEntity")
	Image toEntity(ImageVO imageVO);
	
	// for update
	@Named("toEntity")
	Image toEntity(ImageVO imageVO, @MappingTarget Image image);
	
	@Named("toVO")
	@Mapping(target = "size", expression = "java(image.getData().length)")
	ImageVO toVO(Image image);
	
	@IterableMapping(qualifiedByName = "toVO")
	@BeanMapping(ignoreByDefault = true)
	Iterable<ImageVO> toVO(Iterable<Image> imageList);
	
}