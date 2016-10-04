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
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.protocol.sdk.IProtocolFactory;

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
	private IProtocolFactory protocolFactory;
	
	/** Logger */
	private Logger logCore;
	
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

	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}

	public void setProtocolFactory(IProtocolFactory protocolFactory) {
		this.protocolFactory = protocolFactory;
	}
	
	public String getIdModulo() {
		return this.idModulo;
	}

	public void setIdModulo(String idModulo) {
		this.idModulo = idModulo;
	}
}
