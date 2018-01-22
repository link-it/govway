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

import org.openspcoop2.pdd.core.handlers.ExitContext;

/**
 * ExitHandler
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13384 $, $Date: 2017-10-26 12:24:53 +0200 (Thu, 26 Oct 2017) $
 */
public class ExitHandler implements org.openspcoop2.pdd.core.handlers.ExitHandler {


	@Override
	public void invoke(ExitContext context) {
		
		try {
			InitHandler.gestoreMessaggi.setStop(true);
			context.getLogCore().info("AS4MessageReceiver richiesto stop");
			
		}catch(Exception e) {
			
		}
		
	}

}
