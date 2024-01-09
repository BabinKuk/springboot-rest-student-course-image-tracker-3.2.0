package org.babinkuk.validator;

public enum ValidatorCodes {

	VALIDATION_FAILED("validation_failed"),
	ERROR_CODE_STUDENT_ID_NOT_FOUND("error_code_student_id_not_found"),
	ERROR_CODE_COURSE_ID_NOT_FOUND("error_code_course_id_not_found"),
	ERROR_CODE_INSTRUCTOR_ID_NOT_FOUND("error_code_instructor_id_not_found"),
	ERROR_CODE_REVIEW_ID_NOT_FOUND("error_code_review_id_not_found"),
	ERROR_CODE_IMAGE_ID_NOT_FOUND("error_code_image_id_not_found"),
	ERROR_CODE_STUDENT_EMAIL_NOT_FOUND("error_code_student_email_not_found"),
	ERROR_CODE_INSTRUCTOR_EMAIL_NOT_FOUND("error_code_instructor_email_not_found"),
	ERROR_CODE_COURSE_TITLE_NOT_FOUND("error_code_course_title_not_found"),
	ERROR_CODE_TITLE_EMPTY("error_code_title_empty"),
	ERROR_CODE_REVIEW_EMPTY("error_code_review_empty"),
	ERROR_CODE_FIRST_NAME_EMPTY("error_code_first_name_empty"),
	ERROR_CODE_LAST_NAME_EMPTY("error_code_last_name_empty"),
	ERROR_CODE_EMAIL_EMPTY("error_code_email_empty"),
	ERROR_CODE_EMAIL_INVALID("error_code_email_invalid"),
	ERROR_CODE_EMAIL_ALREADY_EXIST("error_code_email_already_exist"),
	ERROR_CODE_INSTRUCTOR_INVALID("error_code_instructor_invalid"),
	ERROR_CODE_STUDENT_INVALID("error_code_student_invalid"),
	ERROR_CODE_COURSE_INVALID("error_code_course_invalid"),
	ERROR_CODE_REVIEW_INVALID("error_code_review_invalid"),
	ERROR_CODE_STATUS_INVALID("error_code_status_invalid"),
	ERROR_CODE_ACTION_INVALID("error_code_action_invalid"),
	ERROR_CODE_IMAGE_INVALID("error_code_image_invalid"),
	ERROR_CODE_TITLE_ALREADY_EXIST("error_code_title_already_exist"),
	ERROR_CODE_FILE_NAME_EMPTY("error_code_file_name_empty"),
	ERROR_CODE_FILE_DATA_EMPTY("error_code_file_data_empty"),
	ERROR_CODE_FILE_SIZE_INVALID("error_code_file_size_invalid");
	
	private String message;
	
	ValidatorCodes(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public static ValidatorCodes fromMessage(String message) {
		
		for (ValidatorCodes code : ValidatorCodes.values()) {
			if(code.message.equalsIgnoreCase(message)) return code;
		}
		
		return null;
	}

}
