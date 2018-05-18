/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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


package org.openspcoop2.pdd.core.handlers;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.IntegrationContext;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * InRequestContext
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InRequestProtocolContext extends InRequestContext {

	public InRequestProtocolContext(Logger logger,IProtocolFactory<?> protocolFactory, IState state){
		super(logger,protocolFactory,state);
	}
	public InRequestProtocolContext(InRequestContext inRequestContext){
		super(inRequestContext.getLogCore(),inRequestContext.getProtocolFactory(),inRequestContext.getStato());
		super.setConnettore(inRequestContext.getConnettore());
		super.setDataAccettazioneRichiesta(inRequestContext.getDataAccettazioneRichiesta());
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
