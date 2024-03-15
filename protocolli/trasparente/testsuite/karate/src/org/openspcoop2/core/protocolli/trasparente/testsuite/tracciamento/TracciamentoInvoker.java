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

package org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento;

import org.openspcoop2.utils.threads.BaseThread;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* TracciamentoInvoker
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class TracciamentoInvoker extends BaseThread {

	private HttpRequest request;
	private HttpResponse response = null;
	private  Throwable t = null;
	
	public TracciamentoInvoker(HttpRequest request) {
		this.request = request;
	}
	
	@Override
	protected void process() {
		try {
			this.response = HttpUtilities.httpInvoke(this.request);
		}catch(Throwable t) {
			this.t = t;
		}finally {
			this.setStop(true);
		}		
	}
	
	public HttpResponse getResponse() {
		return this.response;
	}

	public Throwable getT() {
		return this.t;
	}

}
