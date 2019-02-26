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

import java.util.Date;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
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
public class InRequestContext extends BaseContext {

	public InRequestContext(Logger logger,IProtocolFactory<?> protocolFactory, IState state){
		super.setLogCore(logger);
		super.setProtocolFactory(protocolFactory);
		super.setStato(state);
	}
	
	/** Informazioni sul connettore di ingresso */
	private InfoConnettoreIngresso connettore;

	/** Data accettazione Richiesta */
	private Date dataAccettazioneRichiesta;
	
	public Date getDataAccettazioneRichiesta() {
		return this.dataAccettazioneRichiesta;
	}

	public void setDataAccettazioneRichiesta(Date dataAccettazioneRichiesta) {
		this.dataAccettazioneRichiesta = dataAccettazioneRichiesta;
	}

	public InfoConnettoreIngresso getConnettore() {
		return this.connettore;
	}

	public void setConnettore(InfoConnettoreIngresso connettore) {
		this.connettore = connettore;
	}
	
}
