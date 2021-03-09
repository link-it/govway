/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.slf4j.Logger;

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
	private Map<String, Object> transportContext = new HashMap<String, Object>();

	/** Tipo porta di dominio */
	private TipoPdD tipoPorta;
	
	/** IDModulo */
	private String idModulo;
	
	/** Logger */
	private Logger logCore;

	/** PdDContext */
	private PdDContext pddContext = null;
	
	/** ProtocolFactory */
	private IProtocolFactory<?> protocolFactory;
	
	/** NotifierInputStreamParameter */
	private NotifierInputStreamParams notifierInputStreamParams;
	
	/** RequestInfo */
	RequestInfo requestInfo;
	
	
	public PreInRequestContext(PdDContext pddContext){
		this.pddContext = pddContext;
	}
	
	public PdDContext getPddContext() {
		return this.pddContext;
	}

	public Map<String, Object> getTransportContext() {
		return this.transportContext;
	}

	public void setTransportContext(Map<String, Object> transportContext) {
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
	
	public IProtocolFactory<?> getProtocolFactory() {
		return this.protocolFactory;
	}

	public void setProtocolFactory(IProtocolFactory<?> protocolFactory) {
		this.protocolFactory = protocolFactory;
	}
	
	public RequestInfo getRequestInfo() {
		return this.requestInfo;
	}

	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}
}
