package org.babinkuk.controller;

import org.babinkuk.service.CourseService;
import org.babinkuk.service.ImageService;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorFactory;
import org.babinkuk.validator.ValidatorRole;
import org.babinkuk.validator.ValidatorType;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.ImageVO;
import org.babinkuk.vo.UserVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.babinkuk.config.Api.*;

import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ROOT + IMAGES)
public class ImageController {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	// services
	private ImageService imageService;
	
	@Autowired
	private ValidatorFactory validatorFactory;
	
	@Autowired
	private ObjectMapper mapper;
	
	public ImageController() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public ImageController(ImageService imageService) {
		this.imageService = imageService;
	}

	/**
	 * expose GET "/images"
	 * get image list
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("")
	public ResponseEntity<Iterable<ImageVO>> getAllImages() {

		return ResponseEntity.of(Optional.ofNullable(imageService.getAllImages()));
	}
	
	/**
	 * expose GET "/images/{imageId}"
	 * get specific image
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("/{imageId}")
	public ResponseEntity<ImageVO> getImage(@PathVariable int imageId) {
		
		return ResponseEntity.of(Optional.ofNullable(imageService.findById(imageId)));
	}
	
//	/**
//	 * expose POST "/images"
//	 * add new image
//	 * 
//	 * @param userId
//	 * @param imageVO
//	 * @param validationRole
//	 * @return
//	 * @throws JsonProcessingException
//	 */
//	@PostMapping("/{courseId}")
//	public ResponseEntity<ApiResponse> addImage(
//			@PathVariable int courseId,
//			@Valid @RequestBody ImageVO imageVO,
//			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws JsonProcessingException {
//		
//		// first find course
//		UserVO userVO = new UserVO();
//		
//		// in case id is passed in json, set to 0
//		// this is to force a save of new item ... instead of update
//		imageVO.setId(0);
//		
//		validatorFactory.getValidator(validationRole).validate(imageVO, ActionType.CREATE, ValidatorType.IMAGE);
//		
//		return ResponseEntity.of(Optional.ofNullable(imageService.saveImage(courseVO, imageVO)));
//	}
	
	/**
	 * expose PUT "/images/{imageId}""
	 * update existing image
	 * 
	 * @param imageVO
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("/{imageId}")
	public ResponseEntity<ApiResponse> updateImage(
			@PathVariable int imageId,
			@RequestParam(name=FILE_NAME, required = true) String fileName,
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws JsonProcessingException {

		// first find image
		ImageVO imageVO = imageService.findById(imageId);
		
		// next set new title
		imageVO.setFileName(fileName);
		
		validatorFactory.getValidator(validationRole).validate(imageVO, ActionType.UPDATE, ValidatorType.IMAGE);

		return ResponseEntity.of(Optional.ofNullable(imageService.saveImage(imageVO)));
	}
	
	/**
	 * expose DELETE "/{imageId}"
	 * 
	 * @param imageId
	 * @param validationRole
	 * @return
	 */
	@DeleteMapping("/{imageId}")
	public ResponseEntity<ApiResponse> deleteImage(
			@PathVariable int imageId, 
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) {
		
		validatorFactory.getValidator(validationRole).validate(imageId, ActionType.DELETE, ValidatorType.IMAGE);
		
		return ResponseEntity.of(Optional.ofNullable(imageService.deleteImage(imageId)));
	}
}
