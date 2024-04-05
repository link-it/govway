/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
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



package org.openspcoop2.protocol.sdk.validator;

/**	
 * Contiene la definizione di una eccezione lanciata in seguito ad una malformazione della struttura dell'header del protocollo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class StrutturaBustaException extends Exception {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	private String codice = null;
	
	public StrutturaBustaException(String msg,String codice) {
		super(msg);
		this.codice = codice;
	}
	public StrutturaBustaException(String message, String codice , Throwable cause)
	{
		super(message, cause);
		this.codice = codice;
		// TODO Auto-generated constructor stub
	}
	
	public String getCodice() {
		return this.codice;
	}
	public void setCodice(String codice) {
		this.codice = codice;
	}
}
