package org.openspcoop2.pdd.core.transazioni;

public class TransactionNotExistsException extends Exception {

	 public TransactionNotExistsException(String message, Throwable cause)
		{
			super(message, cause);
		}
		public TransactionNotExistsException(Throwable cause)
		{
			super(cause);
		}
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 1L;
		
		public TransactionNotExistsException() {
			super();
	    }
		public TransactionNotExistsException(String msg) {
	        super(msg);
	    }
}
