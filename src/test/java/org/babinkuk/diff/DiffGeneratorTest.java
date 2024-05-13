package org.babinkuk.diff;

import static org.babinkuk.utils.ApplicationTestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.entity.ChangeLogItem;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.vo.StudentVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@AutoConfigureMockMvc
public class DiffGeneratorTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(DiffGeneratorTest.class);
	
	public DiffGenerator diffGenerator;
	
	protected DiffGenerator createDiffGenerator() {
		final DiffGenerator diffGenerator = new DiffGenerator();
		
		// register data resolvers
		YesNoDataResolver yesNoDataResolver = new YesNoDataResolver();
		diffGenerator.registerDataResolver("yesno", yesNoDataResolver);
		
		BooleanDataResolver booleanDataResolver = new BooleanDataResolver();
		diffGenerator.registerDataResolver("boolean", booleanDataResolver);
		
		DateToTimestampDataResolver dateToTimestampDataResolver = new DateToTimestampDataResolver();
		diffGenerator.registerDataResolver("dateToTimestamp", dateToTimestampDataResolver);
		
		LocalDateTimeToTimestampDataResolver localDateTimeToTimestampDataResolver = new LocalDateTimeToTimestampDataResolver();
		diffGenerator.registerDataResolver("localDateTimeToTimestamp", localDateTimeToTimestampDataResolver);
		
		NoDataResolver noDataResolver = new NoDataResolver();
		diffGenerator.registerDataResolver("optionalBoolean", noDataResolver);
		
		return diffGenerator;
	}
	
	@Test
	void subtract() {
		
		/**
		 * remove elements in collection b from Collection a
		 * result = a - b
		 * 
		 * @param a
		 * @param b
		 * @return a - b
		 */
		List<String> listA = new ArrayList<String>();
		// Adding elements in the List
		listA.add("Mango");
		listA.add("Apple");
		listA.add("Banana");
		listA.add("Grapes");
		
		List<String> listB = new ArrayList<String>();
		// Adding elements in the List
		listB.add("Mango");
		listB.add("Apple");
				
		this.diffGenerator = createDiffGenerator();
		Collection<?> result =  this.diffGenerator.subtract(listA, listB);
		
		// assert
		assertNotNull(result, "result null");
		assertEquals(2, result.size(), "fieldList.size() NOK");
		assertTrue(result.stream().anyMatch(item ->
			item.equals("Banana")
		));
		assertTrue(result.stream().anyMatch(item ->
			item.equals("Grapes")
		));
		
	}

	@Test
	void difference_items_no_difference() {

		Set<ChangeLogItem> itemSet = new HashSet<>();

		// current == original
		StudentVO originalStudent = ApplicationTestUtils.createStudentVO();
		StudentVO currentStudent = ApplicationTestUtils.createStudentVO();
		
		this.diffGenerator = createDiffGenerator();
		itemSet = this.diffGenerator.difference(originalStudent, currentStudent, null, null, itemSet, null);
		//log.info(itemSet);
		
		// assert
		assertNotNull(itemSet, "itemSet null");
		assertEquals(0, itemSet.size(), "itemSet.size() NOK");
	}
	
	@Test
	void difference_add_item() {

		Set<ChangeLogItem> itemSet = new HashSet<>();

		// current not null, original null --> action add
		StudentVO originalStudent = null;
		StudentVO currentStudent = ApplicationTestUtils.createStudentVO();
		
		this.diffGenerator = createDiffGenerator();
		itemSet = this.diffGenerator.difference(originalStudent, currentStudent, null, null, itemSet, null);
		
		// assert
		assertNotNull(itemSet, "itemSet null");
		assertEquals(1, itemSet.size(), "itemSet.size() NOK");
		assertTrue(itemSet.stream().anyMatch(item ->
			//ChangeLogItem [chliId=0, chliFieldName=StudentVO.insert, chliOldValueId=0, chliOldValue=StudentVO [firstName=firstNameStudentNew, lastName=lastNameStudentNew, email=StudentNew@babinkuk.com, images=null, status=ACTIVE, street=New Street, city=New City, zipCode=New ZipCode], chliNewValue=-, chliNewValueId=0]]
			item.getChliId() == 0
			&& item.getChliFieldName().equals("StudentVO.insert")
			&& item.getChliNewValueId() == 0
			&& item.getChliOldValueId() == 0
			&& item.getChliOldValue().equals("-")
			&& StringUtils.isNotBlank(item.getChliNewValue())
			&& item.getChliNewValue().equals(currentStudent.toString())
		));
	}
	
	@Test
	void difference_delete_item() {

		Set<ChangeLogItem> itemSet = new HashSet<>();

		// original not null, current null --> action delete
		StudentVO originalStudent = ApplicationTestUtils.createStudentVO();
		StudentVO currentStudent = null;
		
		this.diffGenerator = createDiffGenerator();
		itemSet = this.diffGenerator.difference(originalStudent, currentStudent, null, null, itemSet, null);
		//log.info(itemSet);
		
		// assert
		assertNotNull(itemSet, "itemSet null");
		assertEquals(1, itemSet.size(), "itemSet.size() NOK");
		assertTrue(itemSet.stream().anyMatch(item ->
			//ChangeLogItem [chliId=0, chliFieldName=StudentVO.delete, chliOldValueId=0, chliOldValue=StudentVO [firstName=firstNameStudentNew, lastName=lastNameStudentNew, email=StudentNew@babinkuk.com, images=null, status=ACTIVE, street=New Street, city=New City, zipCode=New ZipCode], chliNewValue=-, chliNewValueId=0]]
			item.getChliId() == 0
			&& item.getChliFieldName().equals("StudentVO.delete")
			&& item.getChliNewValueId() == 0
			&& item.getChliOldValueId() == 0
			&& item.getChliNewValue().equals("-")
			&& StringUtils.isNotBlank(item.getChliOldValue())
			&& item.getChliOldValue().equals(originalStudent.toString())
		));
	}
	
	@Test
	void difference_update_item() {

		Set<ChangeLogItem> itemSet = new HashSet<>();

		// original not null, current not null --> action update
		StudentVO originalStudent = ApplicationTestUtils.createStudentVO();
		StudentVO currentStudent = ApplicationTestUtils.createStudentVO();
		currentStudent = ApplicationTestUtils.updateExistingStudentVO(currentStudent);
		
		this.diffGenerator = createDiffGenerator();
		itemSet = this.diffGenerator.difference(originalStudent, currentStudent, null, null, itemSet, null);
		//log.info(itemSet.size());
		//log.info(itemSet);
		
		// assert
		assertNotNull(itemSet, "itemSet null");
		assertEquals(7, itemSet.size(), "itemSet.size() NOK");
		assertTrue(itemSet.stream().anyMatch(item ->
			//[ChangeLogItem [chliId=0, chliFieldName=StudentVO.zipCode.update, chliOldValueId=0, chliOldValue=New ZipCode, chliNewValue=Update ZipCode, chliNewValueId=0], 
			item.getChliId() == 0
			&& item.getChliFieldName().equals("StudentVO.zipCode.update")
			&& item.getChliNewValueId() == 0
			&& item.getChliOldValueId() == 0
			&& item.getChliNewValue().equals(STUDENT_ZIPCODE_UPDATED)
			&& StringUtils.isNotBlank(item.getChliOldValue())
			&& item.getChliOldValue().equals(STUDENT_ZIPCODE_NEW)
		));
		assertTrue(itemSet.stream().anyMatch(item ->
			// ChangeLogItem [chliId=0, chliFieldName=StudentVO.status.update, chliOldValueId=0, chliOldValue=ACTIVE, chliNewValue=INACTIVE, chliNewValueId=0],
			item.getChliId() == 0
			&& item.getChliFieldName().equals("StudentVO.status.update")
			&& item.getChliNewValueId() == 0
			&& item.getChliOldValueId() == 0
			&& item.getChliNewValue().equals(STUDENT_STATUS_UPDATED.getValue())
			&& StringUtils.isNotBlank(item.getChliOldValue())
			&& item.getChliOldValue().equals(STUDENT_STATUS_NEW.getValue())
		));
		assertTrue(itemSet.stream().anyMatch(item ->
			// ChangeLogItem [chliId=0, chliFieldName=StudentVO.city.update, chliOldValueId=0, chliOldValue=New City, chliNewValue=Update City, chliNewValueId=0],
			item.getChliId() == 0
			&& item.getChliFieldName().equals("StudentVO.city.update")
			&& item.getChliNewValueId() == 0
			&& item.getChliOldValueId() == 0
			&& item.getChliNewValue().equals(STUDENT_CITY_UPDATED)
			&& StringUtils.isNotBlank(item.getChliOldValue())
			&& item.getChliOldValue().equals(STUDENT_CITY_NEW)
		));
		assertTrue(itemSet.stream().anyMatch(item ->
			// ChangeLogItem [chliId=0, chliFieldName=StudentVO.street.update, chliOldValueId=0, chliOldValue=New Street, chliNewValue=Update Street, chliNewValueId=0],
			item.getChliId() == 0
			&& item.getChliFieldName().equals("StudentVO.street.update")
			&& item.getChliNewValueId() == 0
			&& item.getChliOldValueId() == 0
			&& item.getChliNewValue().equals(STUDENT_STREET_UPDATED)
			&& StringUtils.isNotBlank(item.getChliOldValue())
			&& item.getChliOldValue().equals(STUDENT_STREET_NEW)
		));
		assertTrue(itemSet.stream().anyMatch(item ->
			// ChangeLogItem [chliId=0, chliFieldName=StudentVO.lastName.update, chliOldValueId=0, chliOldValue=lastNameStudentNew, chliNewValue=lastNameStudentUpdate, chliNewValueId=0], 
			item.getChliId() == 0
			&& item.getChliFieldName().equals("StudentVO.lastName.update")
			&& item.getChliNewValueId() == 0
			&& item.getChliOldValueId() == 0
			&& item.getChliNewValue().equals(STUDENT_LASTNAME_UPDATED)
			&& StringUtils.isNotBlank(item.getChliOldValue())
			&& item.getChliOldValue().equals(STUDENT_LASTNAME_NEW)
		));
		assertTrue(itemSet.stream().anyMatch(item ->
			// ChangeLogItem [chliId=0, chliFieldName=StudentVO.firstName.update, chliOldValueId=0, chliOldValue=firstNameStudentNew, chliNewValue=firstNameStudentUpdate, chliNewValueId=0]]
			item.getChliId() == 0
			&& item.getChliFieldName().equals("StudentVO.firstName.update")
			&& item.getChliNewValueId() == 0
			&& item.getChliOldValueId() == 0
			&& item.getChliNewValue().equals(STUDENT_FIRSTNAME_UPDATED)
			&& StringUtils.isNotBlank(item.getChliOldValue())
			&& item.getChliOldValue().equals(STUDENT_FIRSTNAME_NEW)
		));
		assertTrue(itemSet.stream().anyMatch(item ->
			// ChangeLogItem [chliId=0, chliFieldName=StudentVO.email.update, chliOldValueId=0, chliOldValue=StudentNew@babinkuk.com, chliNewValue=StudentUpdate@babinkuk.com, chliNewValueId=0], 
			item.getChliId() == 0
			&& item.getChliFieldName().equals("StudentVO.email.update")
			&& item.getChliNewValueId() == 0
			&& item.getChliOldValueId() == 0
			&& item.getChliNewValue().equals(STUDENT_EMAIL_UPDATED)
			&& StringUtils.isNotBlank(item.getChliOldValue())
			&& item.getChliOldValue().equals(STUDENT_EMAIL_NEW)
		));
	}
}