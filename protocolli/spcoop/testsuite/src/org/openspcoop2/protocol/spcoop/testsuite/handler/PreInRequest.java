/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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


package org.openspcoop2.protocol.spcoop.testsuite.handler;

import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.PreInRequestContext;
import org.openspcoop2.pdd.core.handlers.PreInRequestHandler;

/**
 * Libreria per handler testsuite
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PreInRequest implements PreInRequestHandler {

	@Override
	public void invoke(PreInRequestContext context) {
		try{
			
			if(context==null){
				throw new HandlerException("Context is null");
			}
			if(context.getLogCore()==null){
				throw new HandlerException("Logger is null");
			}
			if(context.getTipoPorta()==null){
				throw new HandlerException("TipoPdD is null");
			}
			if(context.getTransportContext()==null || context.getTransportContext().size()<=0){
				throw new HandlerException("TransportContext is null");
			}
			
		}catch(Exception e){
			throw new RuntimeException(Costanti.TEST_CONTEXT_PREFISSO_ERRORE+": "+e.getMessage(),e);
		}
	}

	
}
