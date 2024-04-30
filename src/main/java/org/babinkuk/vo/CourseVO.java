package org.babinkuk.vo;

import java.util.ArrayList;
import java.util.List;

import org.babinkuk.diff.DiffField;
import org.babinkuk.diff.Diffable;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * instance of this class is used to represent course data
 * 
 * @author BabinKuk
 *
 */
@Diffable(id = "id")
@JsonInclude(value = Include.NON_EMPTY)
public class CourseVO {

	private int id;

	@DiffField
	@NotBlank(message = "error_code_title_empty")
	private String title;
	
	@DiffField(id = "id")
	private InstructorVO instructorVO;
	
	@DiffField(id = "id")
	private List<ReviewVO> reviewsVO = new ArrayList<ReviewVO>();
	
	@DiffField(id = "id")
	private List<StudentVO> studentsVO = new ArrayList<StudentVO>();
	
	public CourseVO() {
		// TODO Auto-generated constructor stub
	}
	
	public CourseVO(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public InstructorVO getInstructorVO() {
		return instructorVO;
	}

	public void setInstructorVO(InstructorVO instructorVO) {
		this.instructorVO = instructorVO;
	}
	
	public List<ReviewVO> getReviewsVO() {
		return reviewsVO;
	}

	public void setReviewsVO(List<ReviewVO> reviewsVO) {
		this.reviewsVO = reviewsVO;
	}
	
	public List<StudentVO> getStudentsVO() {
		return studentsVO;
	}

	public void setStudentsVO(List<StudentVO> students) {
		this.studentsVO = students;
	}

//	// convenience methods
//	public void addReviewVO(ReviewVO review) {
//		if (reviewsVO == null) {
//			reviewsVO = new ArrayList<ReviewVO>();
//		}
//		
//		reviewsVO.add(review);
//	}
//	
//	public void addStudentVO(StudentVO student) {
//		studentsVO.add(student);
//	}
//	
//	public void removeStudentVO(StudentVO student) {
//		System.out.println("studentsVO " + studentsVO);
//		
//		List<StudentVO> tempStudentsVO = getStudentsVO();
//		for (StudentVO vo : tempStudentsVO) {
//			System.out.println(vo);
//			if (vo.getId() == student.getId()) {
//				System.out.println("remove " + vo);
//				tempStudentsVO.remove(vo);
//			}
//		}
//
//		setStudentsVO(tempStudentsVO);
//	}
		
	@Override
	public String toString() {
		return "CourseVO [id=" + id + ", title=" + title + ", instructorVO=" + instructorVO + ", studentsVO=" + studentsVO + ", reviewsVO=" + reviewsVO + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((instructorVO == null) ? 0 : instructorVO.hashCode());
		result = prime * result + ((reviewsVO == null) ? 0 : reviewsVO.hashCode());
		result = prime * result + ((studentsVO == null) ? 0 : studentsVO.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		CourseVO other = (CourseVO) obj;
		if (id != other.id)
			return false;
		if (instructorVO == null) {
			if (other.instructorVO != null)
				return false;
		} else if (!instructorVO.equals(other.instructorVO))
			return false;
		if (reviewsVO == null) {
			if (other.reviewsVO != null)
				return false;
		} else if (!reviewsVO.equals(other.reviewsVO))
			return false;
		if (studentsVO == null) {
			if (other.studentsVO != null)
				return false;
		} else if (!studentsVO.equals(other.studentsVO))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
}
