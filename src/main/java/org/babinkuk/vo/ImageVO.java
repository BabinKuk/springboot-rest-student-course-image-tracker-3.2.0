package org.babinkuk.vo;

import org.babinkuk.vo.diff.Diffable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * instance of this class is used to represent image data
 * 
 * @author BabinKuk
 *
 */
@Diffable(id = "id")
@JsonInclude(value = Include.NON_EMPTY)
public class ImageVO {

	private int id;
	
	private String fileName;
	
	private byte[] data;
	
	private long size;

	public ImageVO() {
	}

	public ImageVO(String fileName, String fileDesc, byte[] data) {
		this.fileName = fileName;
		this.data = data;
	}

	public ImageVO(String fileName,	byte[] data, long size) {
		this.fileName = fileName;
		this.data = data;
		this.size = size;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
	@Override
	public String toString() {
		return "ImageVO [id=" + id + ", fileName=" + fileName + "]";
	}
}