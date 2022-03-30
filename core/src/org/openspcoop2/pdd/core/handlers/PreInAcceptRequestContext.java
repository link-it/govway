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

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.Context;
import org.slf4j.Logger;

/**
 * PreInAcceptRequestContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PreInAcceptRequestContext {

	/** Tipo porta di dominio */
	private TipoPdD tipoPorta;
	
	/** IDModulo */
	private String idModulo;
	
	/** Logger */
	private Logger logCore;
	
	/** RequestInfo */
	RequestInfo requestInfo;
	
	/** ConnectorInMessage */
	private ConnectorInMessage req;

	/** PreContext */
	private Context preContext;

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
	
	public String getIdModulo() {
		return this.idModulo;
	}

	public void setIdModulo(String idModulo) {
		this.idModulo = idModulo;
	}
	
	public RequestInfo getRequestInfo() {
		return this.requestInfo;
	}

	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}
	
	public ConnectorInMessage getReq() {
		return this.req;
	}

	public void setReq(ConnectorInMessage req) {
		this.req = req;
	}
	
	public Context getPreContext() {
		return this.preContext;
	}

	public void setPreContext(Context preContext) {
		this.preContext = preContext;
	}

}
