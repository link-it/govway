/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

import java.util.Hashtable;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;

/**
 * PreInRequestContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PreInRequestContext {

	public final static String SERVLET_REQUEST = "SERVLET_REQUEST";
	public final static String SERVLET_RESPONSE = "SERVLET_RESPONSE";
	public final static String CONTEXT = "CONTEXT";
	
	/** Trasporto */
	private Hashtable<String, Object> transportContext = new Hashtable<String, Object>();

	/** Tipo porta di dominio */
	private TipoPdD tipoPorta;
	
	/** IDModulo */
	private String idModulo;
	
	/** Logger */
	private Logger logCore;

	/** PdDContext */
	private PdDContext pddContext = null;
	
	/** ProtocolFactory */
	private IProtocolFactory protocolFactory;
	
	/** NotifierInputStreamParameter */
	private NotifierInputStreamParams notifierInputStreamParams;
	
	
	public PreInRequestContext(PdDContext pddContext){
		this.pddContext = pddContext;
	}
	
	public PdDContext getPddContext() {
		return this.pddContext;
	}

	public Hashtable<String, Object> getTransportContext() {
		return this.transportContext;
	}

	public void setTransportContext(Hashtable<String, Object> transportContext) {
		this.transportContext = transportContext;
	}

	public TipoPdD getTipoPorta() {
		return this.tipoPorta;
	}

	public void setTipoPorta(TipoPdD tipoPorta) {
		this.tipoPorta = tipoPorta;
	}

	public Logger getLogCore() {
		return this.logCore;
	}

	public void setLogCore(Logger logCore) {
		this.logCore = logCore;
	}
	
	public NotifierInputStreamParams getNotifierInputStreamParams() {
		return this.notifierInputStreamParams;
	}

	public void setNotifierInputStreamParams(
			NotifierInputStreamParams notifierInputStreamParams) {
		this.notifierInputStreamParams = notifierInputStreamParams;
	}

	public String getIdModulo() {
		return this.idModulo;
	}

	public void setIdModulo(String idModulo) {
		this.idModulo = idModulo;
	}
	
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}

	public void setProtocolFactory(IProtocolFactory protocolFactory) {
		this.protocolFactory = protocolFactory;
	}
}
