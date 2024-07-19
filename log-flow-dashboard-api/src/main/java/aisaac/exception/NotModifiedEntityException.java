package aisaac.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_MODIFIED)
public class NotModifiedEntityException extends RuntimeException{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotModifiedEntityException(String message){
         super(message);
    }
}