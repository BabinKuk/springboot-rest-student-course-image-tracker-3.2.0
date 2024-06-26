package org.babinkuk.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.MessagePool;
import org.babinkuk.config.Api.RestModule;
import org.babinkuk.entity.ChangeLog;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.ImageVO;
import org.babinkuk.vo.InstructorVO;
import org.babinkuk.vo.StudentVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.babinkuk.utils.ApplicationTestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@AutoConfigureMockMvc
public class ImageServiceTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(ImageServiceTest.class);
	
	@Test
	void getAllImages() {
		
		// get all images
		Iterable<ImageVO> images = imageService.getAllImages();
		
		// assert
		assertNotNull(images,"reviews null");
		
		if (images instanceof Collection) {
			assertEquals(2, ((Collection<?>) images).size(), "reviews size not 2");
		}
		
		List<ImageVO> imageList = new ArrayList<ImageVO>();
		images.forEach(imageList::add);
		
		assertTrue(imageList.stream().anyMatch(img ->
			img.getFileName().equals(FILE_1) && img.getId() == 1
		));
		assertTrue(imageList.stream().anyMatch(img ->
			img.getFileName().equals(FILE_2) && img.getId() == 2
		));
	}
	
	@Test
	void getImageById() {
		
		// image
		ImageVO img = getImage();
		
		assertTrue(img != null, "image null");
		
		// get image id
		ImageVO imageVO = imageService.findById(img.getId());
		
		assertNotNull(imageVO,"imageVO null");
		assertEquals(img.getId(), imageVO.getId());
		assertNotNull(imageVO.getFileName(),"getFileName() null");
		assertEquals(FILE_1, imageVO.getFileName(),"getFileName() failure");
		assertTrue(Arrays.equals(DATA_1, imageVO.getData()), "savedImage.getData() failure");
		assertTrue(imageVO.getData().length > 0,"getData().length failure");
		
		// get image id=2
		imageVO = imageService.findById(2);
		
		assertNotNull(imageVO,"imageVO null");
		assertEquals(2, imageVO.getId());
		assertNotNull(imageVO.getFileName(),"getFileName() null");
		assertEquals(FILE_2, imageVO.getFileName(),"getFileName() failure");
		assertTrue(imageVO.getData().length > 0,"getData().length failure");
		
		assertNotEquals("test ", imageVO.getFileName(),"getFileName() intentional failure");
		
		// assert not existing image
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			imageService.findById(22);
		});
		
		String expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_IMAGE_ID_NOT_FOUND.getMessage()), 22);
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void addImage() {
		
		// first find student
		StudentVO studentVO = studentService.findByEmail(STUDENT_EMAIL);
		
		// create image
		// set id 0: this is to force a save of new item ... instead of update
		ImageVO imageVO = new ImageVO();
		imageVO.setFileName(FILE_NEW);
		imageVO.setData(DATA_NEW);
		imageVO.setId(0);
		
		// add to image to student
		imageService.saveImage(studentVO, imageVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// get all images
		Iterable<ImageVO> images = imageService.getAllImages();
		
		// assert
		assertNotNull(images,"images null");
		
		if (images instanceof Collection) {
			assertEquals(3, ((Collection<?>) images).size(), "reviews size not 3");
		}
		
		List<ImageVO> imageList = new ArrayList<ImageVO>();
		images.forEach(imageList::add);
		
		assertTrue(imageList.stream().anyMatch(img ->
			img.getFileName().equals(FILE_1) && img.getId() == 1
		));
		assertTrue(imageList.stream().anyMatch(img ->
			img.getFileName().equals(FILE_2) && img.getId() == 2
		));
		assertTrue(imageList.stream().anyMatch(img ->
			img.getFileName().equals(FILE_NEW)
		));
		
		// assert change log
		// when
		Iterable<ChangeLog> chLogs = changeLogService.getAllChangeLogs();
		
		// then assert
		assertNotNull(chLogs,"chLogs null");
				
		if (chLogs instanceof Collection) {
			assertEquals(1, ((Collection<?>) chLogs).size(), "chLogs size not 1");
		}
		
		List<ChangeLog> chLogList = new ArrayList<ChangeLog>();
		chLogs.forEach(chLogList::add);
//		log.info(chLogs);
//				[ChangeLog [chloId=1, chloTimestamp=2024-05-15 12:10:00.53, chloUserId=STUDENT, 
//					logModule=LogModule [lmId=1, lmDescription=STUDENT, lmEntityName=org.babinkuk.entity.Student],
//					chloTableId=1, 
//					changeLogItems=[ChangeLogItem [chliId=1, chliFieldName=StudentVO.images.insert, chliOldValueId=0, chliOldValue=-, chliNewValue=ImageVO [id=3, fileName=FileNew.txt], chliNewValueId=3]]]]	

		assertTrue(chLogList.stream().anyMatch(chLog ->
			chLog.getChloId() == 1
			&& chLog.getChloUserId().equals(RestModule.STUDENT.getLabel())
			&& chLog.getChloTableId() == (RestModule.STUDENT.getModuleId())
			&& chLog.getLogModule().getLmId() == (RestModule.STUDENT.getModuleId())
			&& chLog.getLogModule().getLmDescription().equals(RestModule.STUDENT.getLabel())
			&& chLog.getLogModule().getLmEntityName().equals("org.babinkuk.entity.Student")
			&& chLog.getChangeLogItems().size() == 1
			&& chLog.getChangeLogItems().stream().anyMatch(item ->
				item.getChliId() == 1
				&& item.getChliFieldName().equals("StudentVO.images.insert")
				&& item.getChliNewValueId() > 0
				&& item.getChliOldValueId() == 0
				&& item.getChliOldValue().equals("-")
				&& StringUtils.isNotBlank(item.getChliNewValue())
				&& StringUtils.contains(item.getChliNewValue(), "ImageVO [")
				&& StringUtils.contains(item.getChliNewValue(), FILE_NEW)
			)
		));
	}
	
	@Test
	void updateImage() {
		
		// image
		ImageVO image = getImage();
		
		assertTrue(image != null, "image null");
		
		// get image id
		ImageVO imageVO = imageService.findById(image.getId());
		
		// update image
		imageVO.setFileName(FILE_UPDATED);
		
		imageService.saveImage(imageVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		ImageVO savedImage = imageService.findById(image.getId());
		
		// assert
		assertNotNull(savedImage,"savedImage null");
		assertEquals(FILE_UPDATED, savedImage.getFileName(),"savedImage.getFileName() failure");
		assertEquals(image.getId(), savedImage.getId(),"savedImage.getId() failure");
		assertTrue(Arrays.equals(imageVO.getData(), savedImage.getData()), "savedImage.getData() failure");
		
		// assert change log
		// when
		Iterable<ChangeLog> chLogs = changeLogService.getAllChangeLogs();
		
		// then assert
		assertNotNull(chLogs,"chLogs null");
				
		if (chLogs instanceof Collection) {
			assertEquals(1, ((Collection<?>) chLogs).size(), "chLogs size not 1");
		}
		
		List<ChangeLog> chLogList = new ArrayList<ChangeLog>();
		chLogs.forEach(chLogList::add);
		log.info(chLogList);
//		[ChangeLog [chloId=1, chloTimestamp=2024-05-15 12:56:49.938, chloUserId=IMAGE, 
//		logModule=LogModule [lmId=5, lmDescription=IMAGE, lmEntityName=org.babinkuk.entity.Image], 
//		chloTableId=5, 
//		changeLogItems=[ChangeLogItem [chliId=2, chliFieldName=ImageVO.data.update, chliOldValueId=1, chliOldValue=[B@4edd8b5b, chliNewValue=[B@3366a998, chliNewValueId=1], 
//						ChangeLogItem [chliId=1, chliFieldName=ImageVO.fileName.update, chliOldValueId=1, chliOldValue=file1.jpg, chliNewValue=FileUpdate.txt, chliNewValueId=1]]]]

		assertTrue(chLogList.stream().anyMatch(chLog ->
			chLog.getChloId() == 1
			&& chLog.getChloUserId().equals(RestModule.IMAGE.getLabel())
			&& chLog.getChloTableId() == (RestModule.IMAGE.getModuleId())
			&& chLog.getLogModule().getLmId() == (RestModule.IMAGE.getModuleId())
			&& chLog.getLogModule().getLmDescription().equals(RestModule.IMAGE.getLabel())
			&& chLog.getLogModule().getLmEntityName().equals("org.babinkuk.entity.Image")
//			&& chLog.getChangeLogItems().size() == 1
			&& chLog.getChangeLogItems().stream().anyMatch(item ->
				item.getChliId() == 1
				&& item.getChliFieldName().equals("ImageVO.fileName.update")
				&& item.getChliNewValueId() == item.getChliOldValueId()
				&& StringUtils.isNotBlank(item.getChliOldValue())
				&& StringUtils.contains(item.getChliOldValue(), FILE_1)
				&& StringUtils.isNotBlank(item.getChliNewValue())
				&& StringUtils.contains(item.getChliNewValue(), FILE_UPDATED)
			)
		));
	}
	
	@Test
	void deleteImage() {
		
		// image
		ImageVO image = getImage();
		
		assertTrue(image != null, "image null");
		
		// get image id
		ImageVO imageVO = imageService.findById(image.getId());
		
		// assert
		assertNotNull(imageVO, "return null");
		assertEquals(image.getId(), imageVO.getId());
		assertNotNull(imageVO.getFileName(),"getFileName() null");
		
		// delete image
		imageService.deleteImage(image.getId());
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// assert non existing image
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			imageService.findById(image.getId());
		});
				
		String expectedMessage = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_IMAGE_ID_NOT_FOUND.getMessage()), image.getId());
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));
	    
	    // check other cascading entities
 		// get instructor
 		InstructorVO instructorVO = instructorService.findByEmail(INSTRUCTOR_EMAIL);
 		
 		// assert
 		// instructor must be unchanged except images (size=0)
 		assertNotNull(instructorVO, "instructorVO null");
 		//assertEquals(1, instructorVO.getId(), "instructorVO.getId()");
 		assertEquals(INSTRUCTOR_FIRSTNAME, instructorVO.getFirstName(),"getFirstName() NOK");
		assertEquals(INSTRUCTOR_LASTNAME, instructorVO.getLastName(),"getLastName() NOK");
		assertEquals(INSTRUCTOR_EMAIL, instructorVO.getEmail(),"getEmail() NOK");
		assertEquals(INSTRUCTOR_SALARY, instructorVO.getSalary(),"getSalary() NOK");
		assertEquals(INSTRUCTOR_STATUS, instructorVO.getStatus(),"getStatus() NOK");
		assertEquals(0, instructorVO.getImages().size(), "getImages size not 0");
		
		// assert change log
		// when
		Iterable<ChangeLog> chLogs = changeLogService.getAllChangeLogs();
		
		// then assert
		assertNotNull(chLogs,"chLogs null");
				
		if (chLogs instanceof Collection) {
			assertEquals(1, ((Collection<?>) chLogs).size(), "chLogs size not 1");
		}
		
		List<ChangeLog> chLogList = new ArrayList<ChangeLog>();
		chLogs.forEach(chLogList::add);
		//log.info(chLogs);

		assertTrue(chLogList.stream().anyMatch(chLog ->
			chLog.getChloId() == 1
			&& chLog.getChloUserId().equals(RestModule.IMAGE.getLabel())
			&& chLog.getChloTableId() == (RestModule.IMAGE.getModuleId())
			&& chLog.getLogModule().getLmId() == (RestModule.IMAGE.getModuleId())
			&& chLog.getLogModule().getLmDescription().equals(RestModule.IMAGE.getLabel())
			&& chLog.getLogModule().getLmEntityName().equals("org.babinkuk.entity.Image")
			&& chLog.getChangeLogItems().size() == 1
			&& chLog.getChangeLogItems().stream().anyMatch(item ->
				item.getChliId() == 1
				&& item.getChliFieldName().equals("ImageVO.delete")
				&& item.getChliNewValueId() == 0
				&& item.getChliOldValueId() >= 0
				&& item.getChliNewValue().equals("-")
				&& StringUtils.isNotBlank(item.getChliOldValue())
				&& StringUtils.contains(item.getChliOldValue(), "ImageVO [")
				&& StringUtils.contains(item.getChliOldValue(), FILE_1)
			)
		));
	}
	
	public ImageVO getImage() {
		
		// get all images
		Iterable<ImageVO> images = imageService.getAllImages();
		
		// assert
		if (images instanceof Collection<?>) {
			assertEquals(2, ((Collection<?>) images).size(), "images size not 2");
		}
		
		List<ImageVO> imageList = new ArrayList<ImageVO>();
		images.forEach(imageList::add);
	
		return imageList.stream()
				.filter(obj -> obj.getFileName().equals(FILE_1))
				.findAny()
				.orElse(null);
	}
}