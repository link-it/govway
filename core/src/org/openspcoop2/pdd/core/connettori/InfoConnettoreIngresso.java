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


package org.openspcoop2.pdd.core.connettori;

import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.protocol.engine.URLProtocolContext;

/**
 * InfoConnettoreIngresso
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InfoConnettoreIngresso  {

    /** FromLocation */
    private String fromLocation;
    /** Credenziali */
	private Credenziali credenziali ;
	/** SOAP Action */
	private String soapAction = null;

	/** Parametri di Invocazione */
	URLProtocolContext urlProtocolContext = null;
	
	public Credenziali getCredenziali() {
		return this.credenziali;
	}
	public void setCredenziali(Credenziali credenziali) {
		this.credenziali = credenziali;
	}
	public String getSoapAction() {
		return this.soapAction;
	}
	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}
	public String getFromLocation() {
		return this.fromLocation;
	}
	public void setFromLocation(String fromLocation) {
		this.fromLocation = fromLocation;
	}
	public URLProtocolContext getUrlProtocolContext() {
		return this.urlProtocolContext;
	}
	public void setUrlProtocolContext(URLProtocolContext urlProtocolContext) {
		this.urlProtocolContext = urlProtocolContext;
	}

	
}
