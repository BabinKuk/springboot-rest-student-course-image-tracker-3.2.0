package org.babinkuk.vo;

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
 * instance of this class is used to represent instructor data
 * 
 * @author BabinKuk
 *
 */
@Diffable(id = "id")
@JsonInclude(value = Include.NON_EMPTY)
public class InstructorVO extends UserVO {

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
	private Double salary;
	
	@DiffField
	private String youtubeChannel;
	
	@DiffField
	private String hobby;
	
	@DiffField
	private List<CourseVO> courses;
		
	public InstructorVO() {
		// TODO Auto-generated constructor stub
	}

	public InstructorVO(String firstName, String lastName, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	public InstructorVO(String firstName, String lastName, String email, String youtubeChannel, String hobby) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.youtubeChannel = youtubeChannel;
		this.hobby = hobby;
	}

	public InstructorVO(String firstName, String lastName, String email, String youtubeChannel, String hobby, Status status) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.youtubeChannel = youtubeChannel;
		this.hobby = hobby;
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
	
	public String getYoutubeChannel() {
		return youtubeChannel;
	}

	public void setYoutubeChannel(String youtubeChannel) {
		this.youtubeChannel = youtubeChannel;
	}

	public String getHobby() {
		return hobby;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
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
	
	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}
	
	public List<CourseVO> getCourses() {
		return courses;
	}

	public void setCourses(List<CourseVO> courses) {
		this.courses = courses;
	}
	
	@Override
	public String toString() {
		return "InstructorVO [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", images=" + images + ", status=" + status + ", salary=" + salary
				//+ ", courses=" + courses
				+ ", youtubeChannel=" + youtubeChannel+ ", hobby=" + hobby
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((courses == null) ? 0 : courses.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((hobby == null) ? 0 : hobby.hashCode());
		result = prime * result + id;
		result = prime * result + ((images == null) ? 0 : images.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((salary == null) ? 0 : salary.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((youtubeChannel == null) ? 0 : youtubeChannel.hashCode());
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
		InstructorVO other = (InstructorVO) obj;
		if (courses == null) {
			if (other.courses != null)
				return false;
		} else if (!courses.equals(other.courses))
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
		if (hobby == null) {
			if (other.hobby != null)
				return false;
		} else if (!hobby.equals(other.hobby))
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
		if (salary == null) {
			if (other.salary != null)
				return false;
		} else if (!salary.equals(other.salary))
			return false;
		if (status != other.status)
			return false;
		if (youtubeChannel == null) {
			if (other.youtubeChannel != null)
				return false;
		} else if (!youtubeChannel.equals(other.youtubeChannel))
			return false;
		return true;
	}
	
}