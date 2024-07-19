package aisaac.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEntityException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1845425690231387823L;

	public DuplicateEntityException(String message){
         super(message);
    }
}
