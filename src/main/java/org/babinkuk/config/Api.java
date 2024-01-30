package org.babinkuk.config;

public final class Api {

	public static final String ROOT = "/api/v1";
	public static final String STUDENTS = "/students";
	public static final String COURSES = "/courses";
	public static final String INSTRUCTORS = "/instructors";
	public static final String REVIEWS = "/reviews";
	public static final String IMAGES = "/images";
	public static final String VALIDATION_ROLE = "validationRole";
	public static final String COURSE_TITLE = "title";
	public static final String FILE = "file";
	public static final String FILE_NAME = "fileName";
	
	public static final String DEFAULT = "DEFAULT";
	
	public static final String COURSE_SAVE_SUCCESS = "course_save_success";
	public static final String COURSE_DELETE_SUCCESS = "course_delete_success";
	
	public static final String INSTRUCTOR_SAVE_SUCCESS = "instructor_save_success";
	public static final String INSTRUCTOR_DELETE_SUCCESS = "instructor_delete_success";
	
	public static final String REVIEW_SAVE_SUCCESS = "review_save_success";
	public static final String REVIEW_DELETE_SUCCESS = "review_delete_success";
	
	public static final String IMAGE_SAVE_SUCCESS = "image_save_success";
	public static final String IMAGE_DELETE_SUCCESS = "image_delete_success";
	
	public static final String STUDENT_SAVE_SUCCESS = "student_save_success";
	public static final String STUDENT_DELETE_SUCCESS = "student_delete_success";
	
	public static final String CHANGE_LOG_DATA_ENTRY_SUFIX = "insert";
	public static final String CHANGE_LOG_DATA_DELETE_SUFIX = "delete";
	public static final String CHANGE_LOG_DATA_UPDATE_SUFIX = "update";
	
	public static enum RestModule {
		
		STUDENT(1),
		INSTRUCTOR(2),
		COURSE(3),
		REVIEW(4),
		IMAGE(4);
		
		private int moduleId;
		
		private RestModule(int moduleId) {
			this.moduleId = moduleId;
		}
		
		public int getModuleId() {
			return moduleId;
		}
	}
	
	public Api() {
		// TODO Auto-generated constructor stub
	}

}
