package org.openspcoop2.web.monitor.core.exception;

/***
 * 
 * Exception che viene lanciata quando l'utente letto dal db non rispetta le condizioni previste.  
 * 
 * @author pintori
 *
 */
public class UserInvalidException extends Exception {

	public UserInvalidException(String message, Throwable cause)
	{
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	public UserInvalidException(Throwable cause)
	{
		super(cause);
		// TODO Auto-generated constructor stub
	}
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public UserInvalidException() {
		super();
	}
	public UserInvalidException(String msg) {
		super(msg);
	}
}
