package aisaac.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadParameterException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1845425690231387823L;

	public BadParameterException(String message){
         super(message);
    }
}
