

package org.openspcoop2.monitor.sdk.exceptions;

import java.util.HashMap;
import java.util.Map;


/**
 * ValidationException
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidationException extends Exception {
    
	private Map<String, String> errors;
	
	public void addFieldErrorMessage(String paramId, String errorMessage){
		if(this.errors==null)
			this.errors = new HashMap<String, String>();
		
		this.errors.put(paramId, errorMessage);
	}
	
	public ValidationException(String message, Throwable cause)
	{
		super(message, cause);
	}
	public ValidationException(Throwable cause)
	{
		super(cause);
	}
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public ValidationException() {
		super();
    }
	public ValidationException(String msg) {
        super(msg);
    }
	
	public Map<String, String> getErrors() {
		return this.errors;
	}
}

