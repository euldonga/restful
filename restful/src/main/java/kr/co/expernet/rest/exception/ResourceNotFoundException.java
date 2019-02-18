package kr.co.expernet.rest.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus
public class ResourceNotFoundException extends RuntimeException {

}
