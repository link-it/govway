/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.as4.services;

import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InitContext;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.utils.threads.GestoreRunnable;
import org.slf4j.Logger;

/**
 * InitHandler
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13384 $, $Date: 2017-10-26 12:24:53 +0200 (Thu, 26 Oct 2017) $
 */
public class InitHandler implements org.openspcoop2.pdd.core.handlers.InitHandler {

	public static GestoreRunnable gestoreMessaggi; 
	
	@Override
	public void invoke(InitContext context) throws HandlerException {
		
		try {
			Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
			AS4Properties as4Properties = AS4Properties.getInstance(log);
			
			gestoreMessaggi = new GestoreRunnable("AS4MessageReceiver", as4Properties.getDomibusGatewayJMS_threadsPoolSize(), 
					new RicezioneBusteConnector_GestoreThreads(), ProtocolFactoryManager.getInstance().getProtocolFactoryByName(AS4Costanti.PROTOCOL_NAME).getProtocolLogger());
			gestoreMessaggi.start();
			log.info("AS4MessageReceiver avviato correttamente");
			
		}catch(Exception e) {
			throw new HandlerException(e.getMessage(),e);
		}
		
	}

}
