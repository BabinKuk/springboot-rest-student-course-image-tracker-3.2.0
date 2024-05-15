package org.babinkuk.vo;

import java.util.Arrays;

import org.babinkuk.diff.DiffField;
import org.babinkuk.diff.Diffable;

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
	
	@DiffField
	private String fileName;
	
	@DiffField
	private byte[] data;
	
	@DiffField
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + id;
		result = prime * result + (int) (size ^ (size >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageVO other = (ImageVO) obj;
		if (!Arrays.equals(data, other.data))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (id != other.id)
			return false;
		if (size != other.size)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ImageVO [id=" + id + ", fileName=" + fileName + "]";
	}
}