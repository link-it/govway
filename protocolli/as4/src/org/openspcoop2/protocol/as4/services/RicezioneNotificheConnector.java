/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import javax.jms.Message;
import javax.jms.TextMessage;

import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.utils.threads.RunnableLogger;

/**
 * RicezioneNotificheConnector
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13576 $, $Date: 2018-01-26 12:39:34 +0100 (Fri, 26 Jan 2018) $
 */
public class RicezioneNotificheConnector extends AbstractRicezioneConnector{

	
	public RicezioneNotificheConnector(RunnableLogger runnableLog,AS4Properties asProperties) throws Exception {
		super(runnableLog, asProperties, true);
	}
	
	@Override
	protected void check(Message mapParam) throws Exception {
		
		TextMessage textMsg = null;
		if(mapParam instanceof TextMessage) {
			textMsg = (TextMessage) mapParam;
		}
		else {
			throw new Exception("Tipo di messaggio ["+mapParam.getClass().getName()+"] non atteso");
		}
		
		System.out.println("AAAAAAAAAAAAAAAAAAAAAA ["+textMsg.getText()+"]");
		
		RicezioneNotificheConnettoreUtils utils = new RicezioneNotificheConnettoreUtils(this.log, this.asProperties);
		
		utils.updateTraccia(textMsg);
		
	}
	
}
