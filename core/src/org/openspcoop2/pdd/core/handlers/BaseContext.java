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


package org.openspcoop2.pdd.core.handlers;

import java.util.Date;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * BaseContext
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class BaseContext {
	
	/** Data */
	private Date dataElaborazioneMessaggio;
	
	/** Messaggio */
	private OpenSPCoop2Message messaggio;

	/** Tipo porta di dominio */
	private TipoPdD tipoPorta;
	
	/** IDModulo */
	private String idModulo;
	
	/** PdDContext */
	private PdDContext pddContext;
	
	/** ProtocolFactory */
	private IProtocolFactory<?> protocolFactory;
	
	/** Logger */
	private Logger logCore;
	
	/** Stato */
	private IState stato;
	
	public IState getStato() {
		return this.stato;
	}

	public void setStato(IState stato) {
		this.stato = stato;
	}

	public OpenSPCoop2Message getMessaggio() {
		return this.messaggio;
	}

	public void setMessaggio(OpenSPCoop2Message messaggio) {
		this.messaggio = messaggio;
	}

	public Date getDataElaborazioneMessaggio() {
		return this.dataElaborazioneMessaggio;
	}

	public void setDataElaborazioneMessaggio(Date dataElaborazioneMessaggio) {
		this.dataElaborazioneMessaggio = dataElaborazioneMessaggio;
	}

	public TipoPdD getTipoPorta() {
		return this.tipoPorta;
	}

	public void setTipoPorta(TipoPdD tipoPorta) {
		this.tipoPorta = tipoPorta;
	}

	public PdDContext getPddContext() {
		return this.pddContext;
	}

	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}

	public Logger getLogCore() {
		return this.logCore;
	}

	public void setLogCore(Logger logger) {
		this.logCore = logger;
	}

	public IProtocolFactory<?> getProtocolFactory() {
		return this.protocolFactory;
	}

	public void setProtocolFactory(IProtocolFactory<?> protocolFactory) {
		this.protocolFactory = protocolFactory;
	}
	
	public String getIdModulo() {
		return this.idModulo;
	}

	public void setIdModulo(String idModulo) {
		this.idModulo = idModulo;
	}
}
