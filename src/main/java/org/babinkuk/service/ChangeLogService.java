package org.babinkuk.service;

import org.babinkuk.common.ApiResponse;
import org.babinkuk.entity.ChangeLog;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;

public interface ChangeLogService {
	
	/**
	 * get change log list
	 * 
	 * @return Iterable<ChangeLog>
	 */
	public Iterable<ChangeLog> getAllChangeLogs();
	
	/**
	 * get Change Log
	 * 
	 * @param id
	 * @return ChangeLog
	 * @throws ObjectNotFoundException
	 */
	public ChangeLog findById(int id) throws ObjectNotFoundException;
	
	/**
	 * save ChangeLog (on insert/update)
	 * 
	 * @param changeLog
	 * @throws ObjectException
	 */
	public void saveChangeLog(ChangeLog changeLog) throws ObjectException;
	
	/**
	 * delete ChangeLog
	 * 
	 * @param id
	 * @throws ObjectNotFoundException
	 */
	public void deleteChangeLog(int id) throws ObjectNotFoundException;
}
