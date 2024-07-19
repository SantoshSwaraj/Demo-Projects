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
	
	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ApiErrorResponse> handleValidationException(ValidationException ex) {
		ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
				.status(ResponseEntity.status(HttpStatus.BAD_REQUEST).build().getStatusCode().value())
				.timestamp(new Date()).message(ex.getMessage()).debugMessage(ex.getLocalizedMessage())
				.apiErrors(new ArrayList<>()).build();
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
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
