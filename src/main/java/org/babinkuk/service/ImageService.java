package org.babinkuk.service;

import org.babinkuk.common.ApiResponse;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.vo.ImageVO;
import org.babinkuk.vo.UserVO;

public interface ImageService {
	
	/**
	 * get all images
	 * 
	 * @return Iterable<ImageVO>
	 */
	public Iterable<ImageVO> getAllImages();
	
	/**
	 * get image
	 * 
	 * @param id
	 * @return ImageVO
	 * @throws ObjectNotFoundException
	 */
	public ImageVO findById(int id) throws ObjectNotFoundException;
	
	/**
	 * adding new image (related to existing user)
	 * 
	 * @param userVO
	 * @return
	 * @throws ObjectException
	 */
	public ApiResponse saveImage(UserVO userVO, ImageVO imageVO) throws ObjectException;
	
	/**
	 * update existing image
	 * 
	 * @param imageVO
	 * @return
	 * @throws ObjectException
	 */
	public ApiResponse saveImage(ImageVO imageVO) throws ObjectException;
	
	/**
	 * delete image
	 * 
	 * @param id
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ApiResponse deleteImage(int id) throws ObjectNotFoundException;
		
}
