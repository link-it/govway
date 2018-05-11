

package org.openspcoop2.monitor.sdk.exceptions;


/**
 * FilterFactoryException
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FilterFactoryException extends Exception {
    
	public FilterFactoryException(String message, Throwable cause)
	{
		super(message, cause);
	}
	public FilterFactoryException(Throwable cause)
	{
		super(cause);
	}
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public FilterFactoryException() {
		super();
    }
	public FilterFactoryException(String msg) {
        super(msg);
    }
}

