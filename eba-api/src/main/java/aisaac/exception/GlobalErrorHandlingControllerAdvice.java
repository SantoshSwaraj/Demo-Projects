package aisaac.exception;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;

import aisaac.payload.apierror.ApiErrorResponse;

@RestControllerAdvice
public class GlobalErrorHandlingControllerAdvice {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException ex) {
		Map<String, String> errorMap = new HashMap<String, String>();
		ex.getBindingResult().getFieldErrors().forEach(error -> {
			errorMap.put(error.getField(), error.getDefaultMessage());
		});
		return errorMap;
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
		ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
				.status(ResponseEntity.status(HttpStatus.NOT_FOUND).build().getStatusCode().value())
				.timestamp(new Date()).message(ex.getMessage()).debugMessage(ex.getLocalizedMessage())
				.apiErrors(new ArrayList<>()).build();
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(DuplicateEntityException.class)
	public ResponseEntity<ApiErrorResponse> handleDuplicateEntityException(DuplicateEntityException ex) {
		ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
				.status(ResponseEntity.status(HttpStatus.CONFLICT).build().getStatusCode().value())
				.timestamp(new Date()).message(ex.getMessage()).debugMessage(ex.getLocalizedMessage())
				.apiErrors(new ArrayList<>()).build();
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(NotModifiedFieldException.class)
	public ResponseEntity<ApiErrorResponse> handleNotModifiedFieldException(NotModifiedFieldException ex) {
		ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
				.status(ResponseEntity.status(HttpStatus.CONFLICT).build().getStatusCode().value())
				.timestamp(new Date()).message(ex.getMessage()).debugMessage(ex.getLocalizedMessage())
				.apiErrors(new ArrayList<>()).build();
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(NotModifiedEntityException.class)
	public ResponseEntity<ApiErrorResponse> handleNotModifiedEntityException(NotModifiedEntityException ex) {
		ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
				.status(ResponseEntity.status(HttpStatus.CONFLICT).build().getStatusCode().value())
				.timestamp(new Date()).message(ex.getMessage()).debugMessage(ex.getLocalizedMessage())
				.apiErrors(new ArrayList<>()).build();
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(MethodNotAllowedException.class)
	public ResponseEntity<ApiErrorResponse> handleMethodNotAllowedException(MethodNotAllowedException ex) {
		ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
				.status(ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build().getStatusCode().value())
				.timestamp(new Date()).message(ex.getMessage()).debugMessage(ex.getLocalizedMessage())
				.apiErrors(new ArrayList<>()).build();
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ApiErrorResponse> handleValidationException(ValidationException ex) {
		ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
				.status(ResponseEntity.status(HttpStatus.BAD_REQUEST).build().getStatusCode().value())
				.timestamp(new Date()).message(ex.getMessage()).debugMessage(ex.getLocalizedMessage())
				.apiErrors(new ArrayList<>()).build();
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EmailException.class)
	public ResponseEntity<ApiErrorResponse> handleEmailException(EmailException ex) {
		ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
				.status(ResponseEntity.status(HttpStatus.CONFLICT).build().getStatusCode().value())
				.timestamp(new Date()).message(ex.getMessage()).debugMessage(ex.getLocalizedMessage())
				.apiErrors(new ArrayList<>()).build();
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
		ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
				.status(ResponseEntity.status(HttpStatus.NOT_FOUND).build().getStatusCode().value())
				.timestamp(new Date()).message(ex.getMessage()).debugMessage(ex.getLocalizedMessage())
				.apiErrors(new ArrayList<>()).build();
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiErrorResponse> handleRuntimeException(RuntimeException ex) {
		ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
				.status(ResponseEntity.status(HttpStatus.BAD_REQUEST).build().getStatusCode().value())
				.timestamp(new Date()).message(ex.getMessage()).debugMessage(ex.getLocalizedMessage())
				.apiErrors(new ArrayList<>()).build();
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
	}

}
