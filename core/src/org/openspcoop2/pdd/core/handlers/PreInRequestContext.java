/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
public class PreInRequestContext extends PreInAcceptRequestContext {

	public final static String SERVLET_REQUEST = "SERVLET_REQUEST";
	public final static String SERVLET_RESPONSE = "SERVLET_RESPONSE";
	public final static String CONTEXT = "CONTEXT";
	
	/** Trasporto */
	private Map<String, Object> transportContext = new HashMap<String, Object>();

	/** PdDContext */
	private PdDContext pddContext = null;
	
	/** ProtocolFactory */
	private IProtocolFactory<?> protocolFactory;
	
	/** NotifierInputStreamParameter */
	private NotifierInputStreamParams notifierInputStreamParams;
	
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

	public NotifierInputStreamParams getNotifierInputStreamParams() {
		return this.notifierInputStreamParams;
	}

	public void setNotifierInputStreamParams(
			NotifierInputStreamParams notifierInputStreamParams) {
		this.notifierInputStreamParams = notifierInputStreamParams;
	}

	public IProtocolFactory<?> getProtocolFactory() {
		return this.protocolFactory;
	}

	public void setProtocolFactory(IProtocolFactory<?> protocolFactory) {
		this.protocolFactory = protocolFactory;
	}
	
}
