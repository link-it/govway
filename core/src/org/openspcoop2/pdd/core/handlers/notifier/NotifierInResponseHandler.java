/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.handlers.notifier;

import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InResponseContext;
import org.openspcoop2.pdd.core.handlers.InResponseHandler;

/**
 * NotifierInResponseHandler
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NotifierInResponseHandler implements InResponseHandler {

	@Override
	public void invoke(InResponseContext context) throws HandlerException {
		
		try{
		
			NotifierUtilities.updateNotifierState(context.getMessaggio(), NotifierType.IN_RESPONSE, context);
			
		}catch(Exception e){
			throw new HandlerException(e.getMessage(),e);
		}
	}

}
