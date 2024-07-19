package aisaac.payload.apierror;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import aisaac.utils.CustomDateTimeSerializer;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class ApiErrorResponse {
	private Integer status;
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date timestamp;
	private String message;
	private String debugMessage;
	private List<ApiError> apiErrors;

	private ApiErrorResponse() {
		timestamp = new Date();
	}

	public ApiErrorResponse(Integer status) {
		this();
		this.status = status;
	}

	public ApiErrorResponse(Integer status, Throwable ex) {
		this();
		this.status = status;
		this.message = "Unexpected error";
		this.debugMessage = ex.getLocalizedMessage();
	}

	public ApiErrorResponse(Integer status, String message, Throwable ex) {
		this();
		this.status = status;
		this.message = message;
		this.debugMessage = ex.getLocalizedMessage();
	}

	public ApiErrorResponse(Integer status, Date timestamp, String message, String debugMessage,
			List<ApiError> apiErrors) {
		this();
		this.status = status;
		this.timestamp = timestamp;
		this.message = message;
		this.debugMessage = debugMessage;
		this.apiErrors = apiErrors;
	}

	private void addApiError(ApiError apiError) {
		if (apiError == null) {
			apiErrors = new ArrayList<>();
		}
		apiErrors.add(apiError);
	}

	private void addValidationError(String object, String field, Object rejectedValue, String message) {
		addApiError(new ApiValidationError(object, field, rejectedValue, message));
	}

	private void addValidationError(String object, String message) {
		addApiError(new ApiValidationError(object, message));
	}

	private void addValidationError(FieldError fieldError) {
		this.addValidationError(fieldError.getObjectName(), fieldError.getField(), fieldError.getRejectedValue(),
				fieldError.getDefaultMessage());
	}

	public void addValidationErrors(List<FieldError> fieldErrors) {
		fieldErrors.forEach(this::addValidationError);
	}

	private void addValidationError(ObjectError objectError) {
		this.addValidationError(objectError.getObjectName(), objectError.getDefaultMessage());
	}

	public void addValidationError(List<ObjectError> globalErrors) {
		globalErrors.forEach(this::addValidationError);
	}

	/**
	 * Utility method for adding error of ConstraintViolation. Usually when
	 * a @Validated validation fails.
	 *
	 * @param cv the ConstraintViolation
	 */
	private void addValidationError(ConstraintViolation<?> cv) {
		this.addValidationError(cv.getRootBeanClass().getSimpleName(),
				((PathImpl) cv.getPropertyPath()).getLeafNode().asString(), cv.getInvalidValue(), cv.getMessage());
	}

	public void addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) {
		constraintViolations.forEach(this::addValidationError);
	}
}
