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

import org.slf4j.Logger;
import org.apache.wss4j.dom.engine.WSSConfig;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.IMessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityContext;

/**
 * WSSContext_wss4j
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageSecurityContext_wss4j implements IMessageSecurityContext{

	private Logger log = null;

	@Override
	public void init(MessageSecurityContext wssContext) throws SecurityException {

		this.log = wssContext.getLog();
		
    	try{ 		
    		MessageSecurityContext_wss4j.initWsuIdAllocator(wssContext.getPrefixWsuId(),this.log);
    	}catch(Exception e){
    		this.log.error("Inizializzazione wsu id allocator non riuscita: "+e.getMessage(),e);
    		throw new SecurityException(e.getMessage(),e);
    	}

	}


	/** WSS Id Allocator */
	private static org.apache.wss4j.dom.WsuIdAllocator wsuIdAllocator = null;
	private static String prefixWsuId = null;
	private static synchronized void initWsuIdAllocator(String prefixWsuIdParam,Logger log) throws Exception{
		if(MessageSecurityContext_wss4j.wsuIdAllocator==null){
			WSSConfig config = WSSConfig.getNewInstance();
			MessageSecurityContext_wss4j.prefixWsuId=prefixWsuIdParam;
			if(prefixWsuIdParam==null || "".equals(prefixWsuIdParam)){
				MessageSecurityContext_wss4j.wsuIdAllocator = config.getIdAllocator(); // Default di wss4j
			}
			else{
				MessageSecurityContext_wss4j.wsuIdAllocator = new org.openspcoop2.security.message.WsuIdAllocator(prefixWsuIdParam);
				config.setIdAllocator(MessageSecurityContext_wss4j.wsuIdAllocator);
			}
			log.info("WsuIdAllocator="+config.getIdAllocator().getClass().getName());
		}
		else{
			if(MessageSecurityContext_wss4j.prefixWsuId==null){
				throw new Exception("WsuIdAllocator istanziato con la classe ["+MessageSecurityContext_wss4j.wsuIdAllocator.getClass().getName()+"] e variabile prefixWsuId non istanziata??");
			}
			else if(!MessageSecurityContext_wss4j.prefixWsuId.equals(prefixWsuIdParam)){
				throw new Exception("WsuIdAllocator gia' istanziato con la classe ["+MessageSecurityContext_wss4j.wsuIdAllocator.getClass().getName()
						+"] e variabile prefixWsuId uguale al valore ["+MessageSecurityContext_wss4j.prefixWsuId+"]. Nuovo valore ["+prefixWsuIdParam+"] non impostabile.");
			}
		}
	}

}