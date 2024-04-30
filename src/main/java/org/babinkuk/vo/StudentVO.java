package org.babinkuk.vo;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.babinkuk.diff.DiffField;
import org.babinkuk.diff.Diffable;
import org.babinkuk.entity.Status;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * instance of this class is used to represent student data
 * 
 * @author BabinKuk
 *
 */
@Diffable(id = "id")
@JsonInclude(value = Include.NON_EMPTY)
public class StudentVO extends UserVO {
	
	private int id;
	
	@DiffField
	@NotBlank(message = "error_code_first_name_empty")
	private String firstName;
	
	@DiffField
	@NotBlank(message = "error_code_last_name_empty")
	private String lastName;
	
	@DiffField
	@NotBlank(message = "error_code_email_empty")
	@Email(message = "error_code_email_invalid", regexp="[A-Za-z0-9._%-+]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
	private String email;
	
	@DiffField
	@NotNull(message = "error_code_status_invalid")
	private Status status;
	
	@DiffField
	private List<ImageVO> images;
	
	@DiffField
	private String street;
	
	@DiffField
	private String city;
	
	@DiffField
	private String zipCode;
	
	@DiffField
	private List<CourseVO> coursesVO = new ArrayList<CourseVO>();
	
	public StudentVO() {
		// TODO Auto-generated constructor stub
	}
	
	public StudentVO(String firstName, String lastName, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	public StudentVO(String firstName, String lastName, String email, Status status) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.status = status;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<CourseVO> getCoursesVO() {
		return coursesVO;
	}

	public void setCoursesVO(List<CourseVO> coursesVO) {
		this.coursesVO = coursesVO;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public void setImages(List<ImageVO> images) {
		this.images = images;
	}

	public List<ImageVO> getImages() {
		return images;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	@Override
	public String toString() {
		return "StudentVO [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", images=" + images	+ ", status=" + status
				+ ", street=" + street + ", city=" + city + ", zipCode=" + zipCode
				//+ ", courses=" + coursesVO
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((coursesVO == null) ? 0 : coursesVO.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + id;
		result = prime * result + ((images == null) ? 0 : images.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((street == null) ? 0 : street.hashCode());
		result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
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
		StudentVO other = (StudentVO) obj;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (coursesVO == null) {
			if (other.coursesVO != null)
				return false;
		} else if (!coursesVO.equals(other.coursesVO))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id != other.id)
			return false;
		if (images == null) {
			if (other.images != null)
				return false;
		} else if (!images.equals(other.images))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (status != other.status)
			return false;
		if (street == null) {
			if (other.street != null)
				return false;
		} else if (!street.equals(other.street))
			return false;
		if (zipCode == null) {
			if (other.zipCode != null)
				return false;
		} else if (!zipCode.equals(other.zipCode))
			return false;
		return true;
	}
	
}
