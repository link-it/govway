/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.core.controllo_traffico;

import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;

/**
 * ReadTimeoutContextParam
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ReadTimeoutContextParam {

	private RequestInfo requestInfo;
	private IProtocolFactory<?> protocolFactory;
	private Context context;
	private IState state;
	
	public ReadTimeoutContextParam(RequestInfo requestInfo, IProtocolFactory<?> protocolFactory, Context context, IState state){
		this.requestInfo = requestInfo;
		this.protocolFactory = protocolFactory;
		this.context = context;
		this.state = state;
	}

	public RequestInfo getRequestInfo() {
		return this.requestInfo;
	}

	public IProtocolFactory<?> getProtocolFactory() {
		return this.protocolFactory;
	}

	public Context getContext() {
		return this.context;
	}

	public IState getState() {
		return this.state;
	}
}
