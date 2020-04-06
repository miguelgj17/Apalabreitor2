package edu.uclm.esi.apalabreitor.apalabreitor.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.FORBIDDEN)
public class LoginException extends Exception {
	private static final long serialVersionUID = 1L;

	public LoginException() {
		super("Credenciales inválidas");
	}
}
