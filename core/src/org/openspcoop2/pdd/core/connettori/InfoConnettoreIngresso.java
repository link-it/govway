/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.pdd.core.connettori;

import java.io.Serializable;

import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;

/**
 * InfoConnettoreIngresso
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InfoConnettoreIngresso implements Serializable {

    private static final long serialVersionUID = 1L;
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
