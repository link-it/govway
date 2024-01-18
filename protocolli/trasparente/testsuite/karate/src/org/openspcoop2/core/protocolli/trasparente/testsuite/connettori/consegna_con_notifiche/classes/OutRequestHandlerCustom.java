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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_con_notifiche.classes;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.OutRequestContext;

/**
* InRequestHandlerCustom
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class OutRequestHandlerCustom implements org.openspcoop2.pdd.core.handlers.OutRequestHandler {

	@Override
	public void invoke(OutRequestContext context) throws HandlerException {
		
		try {
			if(context.getMessaggio()==null) {
				throw new HandlerException("Messaggio non presente");
			}
			if(context.getMessaggio().getTransportRequestContext()==null) {
				throw new HandlerException("TransportRequestContext non presente");
			}
			if(context.getMessaggio().getTransportRequestContext().getInterfaceName()==null || StringUtils.isEmpty(context.getMessaggio().getTransportRequestContext().getInterfaceName())) {
				throw new HandlerException("InterfaceName non presente");
			}
		}catch(Exception e) {
			throw new HandlerException(e.getMessage(),e);
		}
		
	}

}
