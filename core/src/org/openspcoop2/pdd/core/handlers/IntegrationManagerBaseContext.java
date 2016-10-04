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

import java.util.Date;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.services.skeleton.Operazione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;

/**
 * Informazioni di consultazione del servizio di IntegrationManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class IntegrationManagerBaseContext {

	/** Operazione Richiesta */
	private Operazione tipoOperazione;
	
	/** ID del messaggio richiesto */
	private String idMessaggio;
		
	/** Tempo di richiesta dell'operazione */
	private Date dataRichiestaOperazione;
	
	/** PdDContext */
	private PdDContext pddContext;
	
	/** Logger */
	private Logger logger;

	/** ProtocolFactory */
	private IProtocolFactory protocolFactory;
	
	/** Costruttori */
	public IntegrationManagerBaseContext(Date dataRichiestaOperazione,Operazione tipoOperazione,
			PdDContext pddContext,Logger logger,IProtocolFactory protocolFactory) {
		this.dataRichiestaOperazione = dataRichiestaOperazione;
		this.tipoOperazione = tipoOperazione;
		this.pddContext = pddContext;	
		this.logger = logger;
		this.protocolFactory = protocolFactory;
	}
	
	
	public Date getDataRichiestaOperazione() {
		return this.dataRichiestaOperazione;
	}
	public void setDataRichiestaOperazione(Date dataRichiestaOperazione) {
		this.dataRichiestaOperazione = dataRichiestaOperazione;
	}
	public Operazione getTipoOperazione() {
		return this.tipoOperazione;
	}
	public void setTipoOperazione(Operazione tipoOperazione) {
		this.tipoOperazione = tipoOperazione;
	}
	public String getIdMessaggio() {
		return this.idMessaggio;
	}
	public void setIdMessaggio(String idMessaggio) {
		this.idMessaggio = idMessaggio;
	}
	public PdDContext getPddContext() {
		return this.pddContext;
	}
	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}


	public Logger getLogger() {
		return this.logger;
	}


	public void setLogger(Logger logger) {
		this.logger = logger;
	}


	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}


	public void setProtocolFactory(IProtocolFactory protocolFactory) {
		this.protocolFactory = protocolFactory;
	}

}
