

package org.openspcoop2.monitor.engine.exceptions;


/**
 * EngineException
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EngineException extends Exception {
    
	public EngineException(String message, Throwable cause)
	{
		super(message, cause);
	}
	public EngineException(Throwable cause)
	{
		super(cause);
	}
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public EngineException() {
		super();
    }
	public EngineException(String msg) {
        super(msg);
    }
}

