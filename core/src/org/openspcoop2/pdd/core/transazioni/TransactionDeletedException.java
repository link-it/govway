package org.openspcoop2.pdd.core.transazioni;

public class TransactionDeletedException extends Exception {

	 public TransactionDeletedException(String message, Throwable cause)
		{
			super(message, cause);
		}
		public TransactionDeletedException(Throwable cause)
		{
			super(cause);
		}
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 1L;
		
		public TransactionDeletedException() {
			super();
	    }
		public TransactionDeletedException(String msg) {
	        super(msg);
	    }
}
