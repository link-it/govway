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
import org.openspcoop2.protocol.sdk.IProtocolFactory;

/**
 * InRequestContext
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InRequestProtocolContext extends InRequestContext {

	public InRequestProtocolContext(Logger logger,IProtocolFactory protocolFactory){
		super(logger,protocolFactory);
	}
	public InRequestProtocolContext(InRequestContext inRequestContext){
		super(inRequestContext.getLogCore(),inRequestContext.getProtocolFactory());
		super.setConnettore(inRequestContext.getConnettore());
		super.setDataElaborazioneMessaggio(inRequestContext.getDataElaborazioneMessaggio());
		super.setMessaggio(inRequestContext.getMessaggio());
		super.setPddContext(inRequestContext.getPddContext());
		super.setTipoPorta(inRequestContext.getTipoPorta());
		super.setIdModulo(inRequestContext.getIdModulo());
	}
	
	/** Informazioni protocollo */
	private ProtocolContext protocollo;
	
	/** Informazioni di integrazione */
	private IntegrationContext integrazione;

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
	
}
