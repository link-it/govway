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
package org.openspcoop2.pdd.services.connector.messages;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.utils.io.notifier.unblocked.IPipedUnblockedStream;

/**
 * HttpServletConnectorAsyncInMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpServletConnectorAsyncInMessage extends HttpServletConnectorInMessage {

	public HttpServletConnectorAsyncInMessage(RequestInfo requestInfo, HttpServletRequest req,
			IDService idModuloAsIDService, String idModulo) throws ConnectorException {
		super(requestInfo, req, idModuloAsIDService, idModulo);
	}

	private IPipedUnblockedStream _pipe;
	
	public void updateInputStream(InputStream is) {
		this.is = is; // questo viene poi wrappato
		if(is!=null && is instanceof IPipedUnblockedStream) {
			this._pipe = (IPipedUnblockedStream) is;
		}
	}
	
	@Override
	public void setRequestReadTimeout(int timeout) {
		super.setRequestReadTimeout(timeout);
		if(this._pipe!=null) {
			this._pipe.setTimeout(timeout);
		}
	}
	
}
