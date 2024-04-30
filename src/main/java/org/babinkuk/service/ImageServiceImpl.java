package org.babinkuk.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.babinkuk.config.MessagePool;
import org.babinkuk.config.Api.RestModule;
import org.babinkuk.dao.ImageRepository;
import org.babinkuk.dao.InstructorRepository;
import org.babinkuk.dao.StudentRepository;
import org.babinkuk.entity.ChangeLog;
import org.babinkuk.entity.Image;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.Student;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.mapper.ImageMapper;
import org.babinkuk.mapper.InstructorMapper;
import org.babinkuk.mapper.StudentMapper;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.ImageVO;
import org.babinkuk.vo.InstructorVO;
import org.babinkuk.vo.StudentVO;
import org.babinkuk.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static org.babinkuk.config.Api.*;

@Service
public class ImageServiceImpl implements ImageService {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private ImageRepository imageRepository;
	
	private ImageMapper imageMapper;
	
	private InstructorMapper instructorMapper;
	
	private StudentMapper studentMapper;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private InstructorRepository instructorRepository;
	
	@Autowired
	private ChangeLogService changeLogService;
	
	@Autowired
	public ImageServiceImpl(ImageRepository imageRepository, InstructorRepository instructorRepository, StudentRepository studentRepository, ImageMapper imageMapper, InstructorMapper instructorMapper, StudentMapper studentMapper, ChangeLogService changeLogService) {
		this.imageRepository = imageRepository;
		this.instructorRepository = instructorRepository;
		this.studentRepository = studentRepository;
		this.imageMapper = imageMapper;
		this.instructorMapper = instructorMapper;
		this.studentMapper = studentMapper;
		this.changeLogService = changeLogService;
	}
	
	@Override
	public ImageVO findById(int id) throws ObjectNotFoundException {
		
		Optional<Image> result = imageRepository.findById(id);
		
		Image image = null;
		ImageVO imageVO = null;
		
		if (result.isPresent()) {
			image = result.get();
			//log.info("image ({})", image);
			
			// mapping
			imageVO = imageMapper.toVO(image);
			//log.info("imageVO ({})", imageVO);
			
			return imageVO;
		} else {
			// not found
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_IMAGE_ID_NOT_FOUND.getMessage()), id);
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
	}
	
	@Override
	public ApiResponse saveImage(ImageVO imageVO) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(MessagePool.getMessage(IMAGE_SAVE_SUCCESS));
		
		Optional<Image> entity = imageRepository.findById(imageVO.getId());
		
		Image image = null;
		ImageVO originalImageVO = null;
		
		if (entity.isPresent()) {
			image = entity.get();
			//log.info("image ({})", entity);
			//log.info("mapping for update");
			
			originalImageVO = imageMapper.toVO(image);
			
			// mapping
			image = imageMapper.toEntity(imageVO, image);
		} else {
			// image not found
			//log.info("mapping for insert");
			
			// mapping
			image = imageMapper.toEntity(imageVO);
		}
		
		//log.info("image ({})", image);

		imageRepository.save(image);
		
		// create ChangeLog
		final ChangeLog changeLog = ChangeLogServiceImpl.createChangeLog(RestModule.IMAGE);
		// save ChangeLog
		changeLogService.saveChangeLog(changeLog, originalImageVO, imageVO);
		
		return response;
	}
	
	@Override
	public ApiResponse saveImage(UserVO userVO, ImageVO imageVO) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(MessagePool.getMessage(IMAGE_SAVE_SUCCESS));
		
		Student student = null;
		Instructor instructor = null;
		
		if (userVO instanceof InstructorVO) {
			
			Optional<Instructor> entity = instructorRepository.findById(userVO.getId());
			
			if (entity.isPresent()) {
				instructor = entity.get();
				
				InstructorVO originalInstructorVO = instructorMapper.toVO(instructor);
				
				// add image
				instructor.addImage(imageMapper.toEntity(imageVO));
				
				instructorRepository.save(instructor);
				
				InstructorVO currentInstructorVO = instructorMapper.toVO(instructor);
				
				// create ChangeLog
				final ChangeLog changeLog = ChangeLogServiceImpl.createChangeLog(RestModule.INSTRUCTOR);
				// save ChangeLog
				changeLogService.saveChangeLog(changeLog, originalInstructorVO, currentInstructorVO);

			} else {
				// not found
				String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND.getMessage()), userVO.getId());
				log.warn(message);
				throw new ObjectNotFoundException(message);
			}
		}
		
		if (userVO instanceof StudentVO) {
			
			Optional<Student> entity = studentRepository.findById(userVO.getId());
			
			if (entity.isPresent()) {
				student = entity.get();
				
				StudentVO originalStudentVO = studentMapper.toVO(student);
				
				// add image
				student.addImage(imageMapper.toEntity(imageVO));
				
				studentRepository.save(student);
				
				StudentVO currentStudentVO = studentMapper.toVO(student);
				
				// create ChangeLog
				final ChangeLog changeLog = ChangeLogServiceImpl.createChangeLog(RestModule.STUDENT);
				// save ChangeLog
				changeLogService.saveChangeLog(changeLog, originalStudentVO, currentStudentVO);
				
			} else {
				// not found
				String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_STUDENT_ID_NOT_FOUND.getMessage()), userVO.getId());
				log.warn(message);
				throw new ObjectNotFoundException(message);
			}
		}

		return response;
	}
	
	@Override
	public ApiResponse deleteImage(int id) throws ObjectNotFoundException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(MessagePool.getMessage(IMAGE_DELETE_SUCCESS));
		
		Optional<Image> entity = imageRepository.findById(id);
		
		Image image = null;
		ImageVO originalImageVO = null;
		
		if (entity.isPresent()) {
			image = entity.get();
			//log.info("image ({})", entity);
			//log.info("mapping for update");
			
			originalImageVO = imageMapper.toVO(image);
		}
		
		imageRepository.deleteById(id);
				
		// create ChangeLog
		final ChangeLog changeLog = ChangeLogServiceImpl.createChangeLog(RestModule.IMAGE);
		// save ChangeLog
		changeLogService.saveChangeLog(changeLog, originalImageVO, null);
		
		return response;
	}

	@Override
	public Iterable<ImageVO> getAllImages() {
		return imageMapper.toVO(imageRepository.findAll());
	}
}
