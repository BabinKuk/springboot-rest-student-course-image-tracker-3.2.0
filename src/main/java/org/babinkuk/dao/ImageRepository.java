package org.babinkuk.dao;

import org.babinkuk.entity.Image;
import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<Image, Integer> {
	
}
