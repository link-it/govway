/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.connettori.httpcore5.nio;

import org.openspcoop2.pdd.services.connector.AsyncResponseCallbackClientEvent;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.IAsyncResponseCallback;

/**
 * ConnettoreHTTPCORERedirectCallback
 *
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCORERedirectCallback implements IAsyncResponseCallback {

	private final ConnettoreHTTPCOREResponseCallback responseCB;
	private final IAsyncResponseCallback prevCB;
	
	public ConnettoreHTTPCORERedirectCallback(ConnettoreHTTPCOREResponseCallback responseCB, IAsyncResponseCallback prevCB) {
		this.responseCB = responseCB;
		this.prevCB = prevCB;
	}
	
	@Override
	public void asyncComplete(AsyncResponseCallbackClientEvent clientEvent, Object... args) throws ConnectorException {
		this.responseCB.handlePostRedirect(this.prevCB);
	}

}
