package org.babinkuk.controller;

import javax.lang.model.element.UnknownElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ObjectValidationException;
import org.babinkuk.validator.ValidatorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private MessageSource messageSource;
	
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException exception,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {
		
		log.warn("Handling MethodArgumentNotValidException", exception);
		BindingResultApiResponse apiResponse = new BindingResultApiResponse(exception.getBindingResult(), messageSource);
		apiResponse.setMessage(messageSource.getMessage(ValidatorCodes.VALIDATION_FAILED.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()));
		apiResponse.setStatus(status);
		
		return handleExceptionInternal(exception, apiResponse, headers, status, request);
	}
	
	@ExceptionHandler
	public ResponseEntity<ApiResponse> handleException(ObjectException exc) {

		return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, exc.getMessage()).toEntity();
	}

	@ExceptionHandler
	public ResponseEntity<ApiResponse> handleException(ObjectNotFoundException exc) {

		return new ApiResponse(HttpStatus.OK, exc.getMessage()).toEntity();
	}
	
	@ExceptionHandler
	public ResponseEntity<ApiResponse> handleException(ObjectValidationException exc) {
		
		ApiResponse apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST, exc.getMessage());
		apiResponse.setErrors(exc.getValidationErrors());
		return apiResponse.toEntity();
	}
	
	@ExceptionHandler(UnknownElementException.class)
	public ResponseEntity<ApiResponse> handleException(MaxUploadSizeExceededException exc) {

		String message = messageSource.getMessage(ValidatorCodes.ERROR_CODE_FILE_SIZE_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale());
		return new ApiResponse(HttpStatus.EXPECTATION_FAILED, message).toEntity();
	}
	
}
