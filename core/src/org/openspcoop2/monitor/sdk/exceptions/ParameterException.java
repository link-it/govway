

package org.openspcoop2.monitor.sdk.exceptions;

import java.util.HashMap;
import java.util.Map;



/**
 * ParameterException
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ParameterException extends Exception {
    
	private Map<String, String> errors;
	
	public void addFieldErrorMessage(String paramId, String errorMessage){
		if(this.errors==null)
			this.errors = new HashMap<String, String>();
		
		this.errors.put(paramId, errorMessage);
	}
	
	public ParameterException(String message, Throwable cause)
	{
		super(message, cause);
	}
	public ParameterException(Throwable cause)
	{
		super(cause);
	}
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public ParameterException() {
		super();
    }
	public ParameterException(String msg) {
        super(msg);
    }
	
	public Map<String, String> getErrors() {
		return this.errors;
	}
}

