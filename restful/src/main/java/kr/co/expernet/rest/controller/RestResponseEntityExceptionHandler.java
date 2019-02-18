package kr.co.expernet.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import kr.co.expernet.rest.domain.RestError;
import kr.co.expernet.rest.exception.ResourceNotFoundException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

	@ExceptionHandler(value = { ResourceNotFoundException.class })
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ResponseBody
	public RestError handleResourceNotFount(ResourceNotFoundException e) {
		RestError error = new RestError(404, "해당 자원을 찾을 수 없습니다.");
		return error;
	}
	
	/*	
	@ExceptionHandler(value = { ResourceNotFoundException.class })
	protected ResponseEntity<RestError> handleResourceNotFound(ResourceNotFoundException e, WebRequest request) {
		RestError error = new RestError(404, "해당 자원을 찾을 수 없습니다.");
		return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.NOT_FOUND);
	}
	*/
}
