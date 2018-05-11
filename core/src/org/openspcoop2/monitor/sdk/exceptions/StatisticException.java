

package org.openspcoop2.monitor.sdk.exceptions;


/**
 * StatisticException
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticException extends Exception {
    
	public StatisticException(String message, Throwable cause)
	{
		super(message, cause);
	}
	public StatisticException(Throwable cause)
	{
		super(cause);
	}
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public StatisticException() {
		super();
    }
	public StatisticException(String msg) {
        super(msg);
    }
}

