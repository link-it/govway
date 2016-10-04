/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */



package org.openspcoop2.pdd.core.handlers;

/**	
 * Contiene la definizione di una eccezione lanciata dalle classi del package org.openspcoop.pdd.core.handlers
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class HandlerException extends Exception {

	private boolean setErrorMessageInFault=false;
	public boolean isSetErrorMessageInFault() {
		return this.setErrorMessageInFault;
	}
	public void setSetErrorMessageInFault(boolean setErrorMessageInFault) {
		this.setErrorMessageInFault = setErrorMessageInFault;
	}
	
	private boolean emettiDiagnostico=true;
	public boolean isEmettiDiagnostico() {
		return this.emettiDiagnostico;
	}
	public void setEmettiDiagnostico(boolean emettiDiagnostico) {
		this.emettiDiagnostico = emettiDiagnostico;
	}

	private String identitaHandler = null;
	public String getIdentitaHandler() {
		return this.identitaHandler;
	}
	// Impostato dal gestore degli handler
	protected void setIdentitaHandler(String identitaHandler) {
		this.identitaHandler = identitaHandler;
	}
	
	 public HandlerException(String message, Throwable cause)
		{
			super(message, cause);
			// TODO Auto-generated constructor stub
		}
		public HandlerException(Throwable cause)
		{
			super(cause);
			// TODO Auto-generated constructor stub
		}
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 1L;
		
		public HandlerException() {
			super();
	    }
		public HandlerException(String msg) {
	        super(msg);
	    }

}
