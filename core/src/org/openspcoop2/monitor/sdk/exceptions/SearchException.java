

package org.openspcoop2.monitor.sdk.exceptions;


/**
 * SearchException
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SearchException extends Exception {
    
	public SearchException(String message, Throwable cause)
	{
		super(message, cause);
	}
	public SearchException(Throwable cause)
	{
		super(cause);
	}
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public SearchException() {
		super();
    }
	public SearchException(String msg) {
        super(msg);
    }
}

