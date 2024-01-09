package org.babinkuk.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "course")
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "title")
	private String title;
	
	// bi-directional
	@ManyToOne(cascade = {
			CascadeType.PERSIST,
			CascadeType.DETACH,
			CascadeType.MERGE,
			CascadeType.REFRESH})
	// cascade.REMOVE not used, if course is deleted, do not delete associated instructor!!!
	@JoinColumn(name = "instructor_id") 
	private Instructor instructor;
	
	// uni-directional
	@OneToMany(fetch = FetchType.LAZY,
			cascade = CascadeType.ALL) // if course is deleted, delete all associated reviews
	@JoinColumn(name = "course_id") // refers to course_id in review table
	private List<Review> reviews;
	
	@ManyToMany(fetch = FetchType.LAZY,
			cascade = {
				CascadeType.PERSIST,
				CascadeType.DETACH,
				CascadeType.MERGE,
				CascadeType.REFRESH})
	// cascade.REMOVE not used, if course is deleted, do not delete associated students!!!
	@JoinTable(
			name = "course_student",
			joinColumns = @JoinColumn(name = "course_id"),
			inverseJoinColumns = @JoinColumn(name = "student_id"))
	private List<Student> students = new ArrayList<Student>();
	
	public Course(String title, Instructor instructor) {
		this.title = title;
		this.instructor = instructor;
	}
	
	public Course(String title) {
		this.title = title;
	}
	
	public Course() {
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

	public Instructor getInstructor() {
		return instructor;
	}

	public void setInstructor(Instructor instructor) {
		this.instructor = instructor;
	}
	
	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	
	// convenience method for bi-directional relationship
	public void addReview(Review review) {
		if (reviews == null) {
			reviews = new ArrayList<Review>();
		}
		
		reviews.add(review);
	}
	
	public void removeReview(Review review) {
		if (reviews == null) {
			reviews = new ArrayList<Review>();
		}
		
		reviews.remove(review);
	}
	
	// convenience method for bi-directional relationship
	public void addStudent(Student student) {
		if (students == null) {
			students = new ArrayList<Student>();
		}
		
		students.add(student);
	}
	
	// convenience method for bi-directional relationship
	public void removeStudent(Student student) {
		if (students == null) {
			students = new ArrayList<Student>();
		}
		
		students.remove(student);
	}

	@Override
	public String toString() {
		return "Course [id=" + id + ", title=" + title
				//+ ", instructor=" + instructor
				+ "]";
	}
	
}