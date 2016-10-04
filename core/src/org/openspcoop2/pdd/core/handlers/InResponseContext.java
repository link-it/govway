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

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.IntegrationContext;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreUscita;
import org.openspcoop2.protocol.sdk.IProtocolFactory;


/**
 * InResponseContext
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InResponseContext extends BaseContext {

	public InResponseContext(Logger logger,IProtocolFactory protocolFactory){
		super.setLogCore(logger);
		super.setProtocolFactory(protocolFactory);
	}
	
	/** Informazioni sul connettore di uscita */
	private InfoConnettoreUscita connettore;
	
	/** Informazioni protocollo */
	private ProtocolContext protocollo;
	
	/** Informazioni di integrazione */
	private IntegrationContext integrazione;
	
	/** ReturnCode */
	private int returnCode;
	
	/** Eventuale errore di Consegna */
	private String erroreConsegna;
	
	/** Proprieta' di trasporto della risposta */
	private java.util.Properties propertiesRispostaTrasporto;
	
	public InfoConnettoreUscita getConnettore() {
		return this.connettore;
	}

	public void setConnettore(InfoConnettoreUscita connettore) {
		this.connettore = connettore;
	}

	public ProtocolContext getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(ProtocolContext p) {
		this.protocollo = p;
	}

	public IntegrationContext getIntegrazione() {
		return this.integrazione;
	}

	public void setIntegrazione(IntegrationContext integrazione) {
		this.integrazione = integrazione;
	}

	public int getReturnCode() {
		return this.returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public String getErroreConsegna() {
		return this.erroreConsegna;
	}

	public void setErroreConsegna(String erroreConsegna) {
		this.erroreConsegna = erroreConsegna;
	}

	public java.util.Properties getPropertiesRispostaTrasporto() {
		return this.propertiesRispostaTrasporto;
	}

	public void setPropertiesRispostaTrasporto(
			java.util.Properties propertiesRispostaTrasporto) {
		this.propertiesRispostaTrasporto = propertiesRispostaTrasporto;
	}

}
