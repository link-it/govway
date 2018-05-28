package org.openspcoop2.pdd.core.transazioni;

public class TransactionStatefulNotSupportedException extends Exception {

	 public TransactionStatefulNotSupportedException(String message, Throwable cause)
		{
			super(message, cause);
		}
		public TransactionStatefulNotSupportedException(Throwable cause)
		{
			super(cause);
		}
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 1L;
		
		public TransactionStatefulNotSupportedException() {
			super();
	    }
		public TransactionStatefulNotSupportedException(String msg) {
	        super(msg);
	    }
}
