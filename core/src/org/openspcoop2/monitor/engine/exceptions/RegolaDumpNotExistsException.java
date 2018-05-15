package org.openspcoop2.monitor.engine.exceptions;

import java.io.Serializable;

public class RegolaDumpNotExistsException extends Exception implements Serializable {

	 public RegolaDumpNotExistsException(String message, Throwable cause)
		{
			super(message, cause);
		}
		public RegolaDumpNotExistsException(Throwable cause)
		{
			super(cause);
		}
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 1L;
		
		public RegolaDumpNotExistsException() {
			super();
	    }
		public RegolaDumpNotExistsException(String msg) {
	        super(msg);
	    }
}
