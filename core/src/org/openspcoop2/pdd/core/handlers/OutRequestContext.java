/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.pdd.core.IntegrationContext;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreUscita;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * OutRequestContext
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OutRequestContext extends BaseContext {

	public OutRequestContext(Logger logger,IProtocolFactory<?> protocolFactory, IState state){
		super.setLogCore(logger);
		super.setProtocolFactory(protocolFactory);
		super.setStato(state);
	}
	
	/** Informazioni sul connettore di uscita */
	private InfoConnettoreUscita connettore;
	
	/** Informazioni protocollo */
	private ProtocolContext protocollo;
	
	/** Informazioni di integrazione */
	private IntegrationContext integrazione;

	/** Servizio Applicativo Erogatore */
	private TransazioneApplicativoServer transazioneApplicativoServer = null;
	
	public TransazioneApplicativoServer getTransazioneApplicativoServer() {
		return this.transazioneApplicativoServer;
	}

	public void setTransazioneApplicativoServer(TransazioneApplicativoServer transazioneApplicativoServer) {
		this.transazioneApplicativoServer = transazioneApplicativoServer;
	}

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
	

}
