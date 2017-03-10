package org.openspcoop2.utils.credential;


/***
 * 
 * PrincipalReaderException eccezione lanciata dal reader.
 * 
 * @author pintori
 *
 */
public class PrincipalReaderException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PrincipalReaderException(String msg) {
		super(msg);
	}
	
	public PrincipalReaderException(Throwable e) {
		super(e);
	}
	
	public PrincipalReaderException(String msg, Throwable e) {
		super(msg, e);
	}

}
