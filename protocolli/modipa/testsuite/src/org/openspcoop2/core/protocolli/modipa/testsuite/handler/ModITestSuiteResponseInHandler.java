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

package org.openspcoop2.core.protocolli.modipa.testsuite.handler;

import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InResponseContext;
import org.openspcoop2.pdd.core.handlers.InResponseHandler;

/**
 * ModITestSuiteResponseInHandler
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModITestSuiteResponseInHandler implements InResponseHandler {

	@Override
	public void invoke(InResponseContext context) throws HandlerException {
		
		try {
			if(context!=null && context.getMessaggio()!=null && MessageType.JSON.equals(context.getMessaggio().getMessageType())){
				
				OpenSPCoop2RestJsonMessage restMessage = context.getMessaggio().castAsRestJson();
				String content = restMessage.getContent();
				if("{}".equals(content) || "{ }".equals(content)) {
					restMessage.updateContent(null);
				}
				
			}
		}catch(Exception e) {
			throw new HandlerException(e.getMessage(),e);
		}
		
		
	}

}
