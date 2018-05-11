package org.openspcoop2.monitor.sdk.exceptions;

/**
 * TransactionException
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionException extends Exception {
	private TransactionExceptionCode CODE;

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	

	public TransactionException(TransactionExceptionCode code,String msg) {
       this(code, msg, null);
    }

	public TransactionException(TransactionExceptionCode code) {
		this(code, code.getMessage(), null);
	}
	
	public TransactionException(TransactionExceptionCode code,String msg, Throwable cause) {
        super("["+code.getMessage()+"] "+msg,cause);
        this.CODE = code;
    }

	public TransactionException(TransactionExceptionCode code, Throwable cause) {
		super(code.getMessage(),cause);
		this.CODE = code;
	}
	
	public TransactionExceptionCode getCode() {
		return this.CODE;
	}
}
