/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.security.message.wss4j;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.apache.wss4j.dom.engine.WSSecurityEngineResult;
import org.apache.wss4j.dom.handler.WSHandlerResult;

/**
 * WSSUtilities
 *
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSSUtilities {

	public static void printWSResult(Logger log,List<?> results){
		if(results!=null){
			Iterator<?> it = results.iterator();
			while (it.hasNext()) {
				Object object = it.next();
				if(object instanceof WSHandlerResult){
					WSHandlerResult wsResult = (WSHandlerResult) object;
					log.debug("Actor ["+wsResult.getActor()+"]");
					List<WSSecurityEngineResult> wsResultList =  wsResult.getResults();
					if(wsResultList!=null){
						for (int i = 0; i < wsResultList.size(); i++) {
							log.debug("WSResult["+i+"]="+wsResultList.get(i).toString());
						}
					}
				}
			}
		}
	}
	
}
