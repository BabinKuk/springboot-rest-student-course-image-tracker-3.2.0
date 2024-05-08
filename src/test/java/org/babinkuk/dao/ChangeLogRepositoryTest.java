package org.babinkuk.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.ApplicationTest;
import org.babinkuk.config.Api.RestModule;
import org.babinkuk.entity.ChangeLog;
import org.babinkuk.entity.LogModule;
import org.babinkuk.utils.ApplicationTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
public class ChangeLogRepositoryTest extends ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(ChangeLogRepositoryTest.class);
			
	@Test
	void addChangeLog() {
		
		// create changeLog
		ChangeLog changeLog = createChangeLog();
		
		ChangeLog savedChLog = changeLogRepository.save(changeLog);
		
		// assert
		assertNotNull(savedChLog,"savedChLog null");
		ApplicationTestUtils.validateNewChangeLog(savedChLog);
	}
	
	@Test
	void getAllChangeLogs() {
		
		// when
		Iterable<ChangeLog> chLogs = changeLogRepository.findAll();
		
		// then assert
		assertNotNull(chLogs,"chLogs null");
		
		if (chLogs instanceof Collection) {
			assertEquals(0, ((Collection<?>) chLogs).size(), "chLogs size not 0");
		}
		
		// create chLog
		ChangeLog changeLog = createChangeLog();
		
		ChangeLog savedChLog = changeLogRepository.save(changeLog);
		
		// assert
		assertNotNull(savedChLog,"savedChLog null");
		ApplicationTestUtils.validateNewChangeLog(savedChLog);
		
		chLogs = changeLogRepository.findAll();
		
		// then assert
		assertNotNull(chLogs,"chLogs null");
		
		if (chLogs instanceof Collection) {
			assertEquals(1, ((Collection<?>) chLogs).size(), "chLogs size not 1");
		}
		
		List<ChangeLog> chLogList = new ArrayList<ChangeLog>();
		chLogs.forEach(chLogList::add);

		assertTrue(chLogList.stream().anyMatch(chLog ->
			chLog.getChloUserId().equals(RestModule.COURSE.getLabel())
				&& chLog.getChloTableId() == (RestModule.COURSE.getModuleId())
				&& chLog.getChloId() == 1
		));
	}
	
	@Test
	void getChangeLogById() {

		// create changeLog
		ChangeLog changeLog = createChangeLog();
		
		ChangeLog savedChLog = changeLogRepository.save(changeLog);
		
		// assert
		assertNotNull(savedChLog,"savedChLog null");
		ApplicationTestUtils.validateNewChangeLog(savedChLog);
		
		// when
		Optional<ChangeLog> dbChLog = changeLogRepository.findById(savedChLog.getChloId());
				
		// then assert
		assertTrue(dbChLog.isPresent());
		ApplicationTestUtils.validateNewChangeLog(dbChLog.get());
		
		// get non-existing course id=2222
		dbChLog = changeLogRepository.findById(2222);
		
		// assert
		assertFalse(dbChLog.isPresent());
	}
	
	@Test
	void deleteChangeLog() {
		
		// create changeLog
		ChangeLog changeLog = createChangeLog();
		
		ChangeLog savedChLog = changeLogRepository.save(changeLog);
		
		// assert
		assertNotNull(savedChLog,"savedChLog null");
		ApplicationTestUtils.validateNewChangeLog(savedChLog);
				
		// delete changeLog
		changeLogRepository.deleteById(savedChLog.getChloId());
		
		// when
		Optional<ChangeLog> deletedChLog = changeLogRepository.findById(savedChLog.getChloId());
						
		// assert
		assertFalse(deletedChLog.isPresent());
		
	}
	
	public static ChangeLog createChangeLog() {
		
		ChangeLog changeLog = new ChangeLog();
		// this is to force a save of new item ... instead of update 
		changeLog.setChloId(0);
		
		// TODO in the future set real user
		changeLog.setChloUserId(RestModule.COURSE.getLabel());
		
		LogModule logModule = new LogModule();
		logModule.setLmId(RestModule.COURSE.getModuleId());
		
		changeLog.setLogModule(logModule);
		
		// TODO in the future set real course id
		changeLog.setChloTableId(RestModule.COURSE.getModuleId());
		
		changeLog.setChloTimestamp(new Date());
		
		return changeLog;
	}

	public ChangeLog getChangeLog() {
		
		// get all
		Iterable<ChangeLog> changeLogs = changeLogRepository.findAll();
		
		// assert
		assertNotNull(changeLogs,"ChangeLog null");
		
		if (changeLogs instanceof Collection) {
			assertEquals(1, ((Collection<?>) changeLogs).size(), "ChangeLog size not 1");
		}
		
		List<ChangeLog> chloList = new ArrayList<ChangeLog>();
		changeLogs.forEach(chloList::add);
	
		return chloList.stream()
				.filter(obj -> obj.getChloUserId().equals("user"))
				.findAny()
				.orElse(null);
	}
	
}