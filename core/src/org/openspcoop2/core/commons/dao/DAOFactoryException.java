

package org.openspcoop2.core.commons.dao;


/**
 * DAOFactoryException
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DAOFactoryException extends Exception {

	 public DAOFactoryException(String message, Throwable cause)
		{
			super(message, cause);
		}
		public DAOFactoryException(Throwable cause)
		{
			super(cause);
		}
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 1L;
		
		public DAOFactoryException() {
			super();
	    }
		public DAOFactoryException(String msg) {
	        super(msg);
	    }
}
