/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.pdd.core.handlers.notifier;

import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InRequestContext;
import org.openspcoop2.pdd.core.handlers.InRequestHandler;

/**
 * NotifierInRequestHandler
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NotifierInRequestHandler implements InRequestHandler {

	@Override
	public void invoke(InRequestContext context) throws HandlerException {
		
		try{
		
			NotifierUtilities.updateNotifierState(context.getMessaggio(), NotifierType.IN_REQUEST, context);
			
		}catch(Exception e){
			throw new HandlerException(e.getMessage(),e);
		}
	}

}
