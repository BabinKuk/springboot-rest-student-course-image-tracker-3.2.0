package org.babinkuk.dao;

import java.util.Optional;

import org.babinkuk.entity.ChangeLog;
import org.springframework.data.repository.CrudRepository;

public interface ChangeLogRepository extends CrudRepository<ChangeLog, Integer> {

}
