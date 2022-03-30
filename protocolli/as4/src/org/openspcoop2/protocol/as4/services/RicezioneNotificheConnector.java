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



package org.openspcoop2.protocol.as4.services;

import javax.jms.Message;
import javax.jms.TextMessage;

import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.utils.threads.RunnableLogger;

/**
 * RicezioneNotificheConnector
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
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
		
		RicezioneNotificheConnettoreUtils utils = new RicezioneNotificheConnettoreUtils(this.log, this.asProperties);
		
		utils.updateTraccia(textMsg);
		
	}
	
}
