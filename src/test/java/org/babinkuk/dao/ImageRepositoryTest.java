package org.babinkuk.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.entity.Image;
import org.babinkuk.entity.Instructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;
import java.util.*;

@Transactional
@AutoConfigureMockMvc
public class ImageRepositoryTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(ImageRepositoryTest.class);
	
	@Test
	void getAllImages() {
		
		// get all images
		Iterable<Image> images = imageRepository.findAll();
		
		// assert
		assertNotNull(images,"images null");
		
		if (images instanceof Collection) {
			assertEquals(2, ((Collection<?>) images).size(), "images size not 2");
		}
		
		List<Image> imageList = new ArrayList<Image>();
		images.forEach(imageList::add);

		assertTrue(imageList.stream().anyMatch(image ->
			image.getFileName().equals(FILE_1)// && image.getId() == 1
		));
		
		assertTrue(imageList.stream().anyMatch(image ->
			image.getFileName().equals(FILE_2)// && image.getId() == 2
		));
	}
	
	@Test
	void getImageById() {
		
		// image
		Image image = getImage();
		
		assertTrue(image != null, "image null");
		
		// get image id
		Optional<Image> dbImage = imageRepository.findById(image.getId());
		
		// assert
		assertTrue(dbImage.isPresent());
		assertNotNull(dbImage,"image null");
		assertEquals(FILE_1, dbImage.get().getFileName(),"getFileName() failure");
		assertEquals(image.getId(), dbImage.get().getId(),"getId() failure");
		assertEquals(image.getData(), dbImage.get().getData(),"getData() failure");
		
		// get non-existing image id=222
		dbImage = imageRepository.findById(222);
		
		// assert
		assertFalse(dbImage.isPresent());
	}

	@Test
	void updateImage() {
		
		// image
		Image image = getImage();
		
		assertTrue(image != null, "image null");
		
		// get image id
		Optional<Image> dbImage = imageRepository.findById(image.getId());
		
		// assert
		assertTrue(dbImage.isPresent());
		assertNotNull(dbImage,"image null");
		assertEquals(FILE_1, dbImage.get().getFileName(),"getFileName() failure");
		assertEquals(image.getId(), dbImage.get().getId(),"getId() failure");
		assertEquals(image.getData(), dbImage.get().getData(),"getData() failure");
		
		// create image
		// set id: this is to force an update of existing item
		Image updatedImage = new Image();
		updatedImage = dbImage.get();
		updatedImage.setFileName(FILE_UPDATED);
		
		Image savedImage = imageRepository.save(updatedImage);
		
		// assert
		assertNotNull(savedImage,"savedImage null");
		assertEquals(FILE_UPDATED, savedImage.getFileName(),"savedImage.getFileName() failure");
		assertEquals(updatedImage.getId(), savedImage.getId(),"savedImage.getId() failure");
		assertEquals(updatedImage.getData(), savedImage.getData(),"getData() failure");
	}
	
	@Test
	void addImage() {
		
		// create image
		// set id=0: this is to force a save of new item
		Image image = new Image();
		image.setFileName(FILE_NEW);
		image.setData(DATA_NEW);
		image.setId(0);
		
		// get user/instructor with id=1
		Optional<Instructor> instructor = instructorRepository.findByEmail(INSTRUCTOR_EMAIL);
		
		// assert
		assertTrue(instructor.isPresent());
		assertEquals(INSTRUCTOR_FIRSTNAME, instructor.get().getFirstName(), "course.get().getFirstName()");
		assertEquals(1, instructor.get().getImages().size(), "course.get().getImages().size()");
		
		// add image to instructor
		instructor.get().addImage(image);
		
		// save instructor
		instructorRepository.save(instructor.get());
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// get all images
		Iterable<Image> images = imageRepository.findAll();
		
		// assert
		assertNotNull(images,"images null");
		
		if (images instanceof Collection) {
			assertEquals(3, ((Collection<?>) images).size(), "images size not 3");
		}
		
		List<Image> imageList = new ArrayList<Image>();
		images.forEach(imageList::add);
		
		assertTrue(imageList.stream().anyMatch(img ->
			img.getFileName().equals(FILE_1)// && img.getId() == 1
		));
		assertTrue(imageList.stream().anyMatch(rev ->
			rev.getFileName().equals(FILE_2)// && rev.getId() == 2
		));
		assertTrue(imageList.stream().anyMatch(rev ->
			rev.getFileName().equals(FILE_NEW)// && rev.getId() == 2
		));
	}

	@Test
	void deleteImage() {
		
		// image
		Image image = getImage();
		
		assertTrue(image != null, "image null");
		
		// get image id
		Optional<Image> dbImage = imageRepository.findById(image.getId());
		
		// assert
		assertTrue(dbImage.isPresent());
		
		// delete image
		imageRepository.deleteById(dbImage.get().getId());
		
		dbImage = imageRepository.findById(dbImage.get().getId());
		
		// assert
		assertFalse(dbImage.isPresent());
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// check other cascading entities
		// get instructor with id=1
		Optional<Instructor> instructor = instructorRepository.findByEmail(INSTRUCTOR_EMAIL);
		
		// assert
		// instructor must be unchanged
		assertTrue(instructor.isPresent());
//		assertNotNull(instructor.get().getFirstName(),"getFirstName() null");
//		assertNotNull(instructor.get().getLastName(),"getLastName() null");
//		assertNotNull(instructor.get().getEmail(),"getEmail() null");
//		assertNotNull(instructor.get().getSalary(),"getSalary() null");
//		assertNotNull(instructor.get().getStatus(),"getStatus() null");
//		assertNotNull(instructor.get().getImages(),"getImages() null");
//		assertNotNull(instructor.get().getInstructorDetail().getYoutubeChannel(),"getYoutubeChannel() null");
//		assertNotNull(instructor.get().getInstructorDetail().getHobby(),"getHobby() null");
//		assertEquals(INSTRUCTOR_FIRSTNAME, instructor.get().getFirstName(),"getFirstName() NOK");
//		assertEquals(INSTRUCTOR_LASTNAME, instructor.get().getLastName(),"getLastName() NOK");
//		assertEquals(INSTRUCTOR_EMAIL, instructor.get().getEmail(),"getEmail() NOK");
//		assertEquals(INSTRUCTOR_SALARY, instructor.get().getSalary(),"getSalary() NOK");
//		assertEquals(INSTRUCTOR_STATUS, instructor.get().getStatus(),"getStatus() NOK");
//		assertEquals(0, instructor.get().getImages().size(), "getImages size not 2");
//		assertEquals(INSTRUCTOR_YOUTUBE, instructor.get().getInstructorDetail().getYoutubeChannel(),"getYoutubeChannel() NOK");
//		assertEquals(INSTRUCTOR_HOBBY, instructor.get().getInstructorDetail().getHobby(),"getHobby() NOK");
//		// not necessary
//		assertNotEquals("test hobb", instructor.get().getInstructorDetail().getHobby(),"getHobby() NOK");
	}
	
	private Image getImage() {
		
		// get all images
		Iterable<Image> images = imageRepository.findAll();
		
		// assert
		assertNotNull(images,"images null");
		
		if (images instanceof Collection) {
			assertEquals(2, ((Collection<?>) images).size(), "images size not 2s");
		}
		
		List<Image> imageList = new ArrayList<Image>();
		images.forEach(imageList::add);
	
		return imageList.stream()
				.filter(rev -> rev.getFileName().equals(FILE_1))
				.findAny()
				.orElse(null);
	}
}