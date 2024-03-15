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

package org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento.classes;

import java.io.File;

import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.OutResponseContext;
import org.openspcoop2.protocol.sdk.state.RequestInfo;

/**
* TracciamentoOutResponseHandler
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class TracciamentoOutResponseHandler extends AbstractTracciamentoHandler implements org.openspcoop2.pdd.core.handlers.OutResponseHandler{

	@Override
	public void invoke(OutResponseContext context) throws HandlerException {
		
		try {
			if(context.getMessaggio()==null) {
				throw new HandlerException("Messaggio non presente");
			}
			RequestInfo requestInfo = null;
			if(context.getPddContext()!=null && context.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
				requestInfo = (RequestInfo) context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
			}
			if(requestInfo==null) {
				throw new HandlerException("RequestInfo non presente");
			}
			if(requestInfo.getProtocolContext()==null) {
				throw new HandlerException("RequestInfo.protocolContext non presente");
			}
			
			String hdr = requestInfo.getProtocolContext().getHeaderFirstValue(AbstractTracciamentoHandler.RES_FILE);
			if(hdr==null) {
				throw new HandlerException("Header '"+AbstractTracciamentoHandler.RES_FILE+"' non presente");
			}
			this.invoke(new File(hdr));
		}catch(Exception e) {
			throw new HandlerException(e.getMessage(),e);
		}
		
	}

}
