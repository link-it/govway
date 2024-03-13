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
import org.openspcoop2.pdd.core.handlers.OutRequestContext;

/**
* TracciamentoOutRequestHandler
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class TracciamentoOutRequestHandler extends AbstractTracciamentoHandler implements org.openspcoop2.pdd.core.handlers.OutRequestHandler{

	@Override
	public void invoke(OutRequestContext context) throws HandlerException {
		
		try {
			if(context.getMessaggio()==null) {
				throw new HandlerException("Messaggio non presente");
			}
			if(context.getMessaggio().getTransportRequestContext()==null) {
				throw new HandlerException("TransportRequestContext non presente");
			}
			String hdr = context.getMessaggio().getTransportRequestContext().getHeaderFirstValue(AbstractTracciamentoHandler.REQ_FILE);
			if(hdr==null) {
				throw new HandlerException("Header '"+AbstractTracciamentoHandler.REQ_FILE+"' non presente");
			}
			this.invoke(new File(hdr));
		}catch(Exception e) {
			throw new HandlerException(e.getMessage(),e);
		}
		
	}

}
