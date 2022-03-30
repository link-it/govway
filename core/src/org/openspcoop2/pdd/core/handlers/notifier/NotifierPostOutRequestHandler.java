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
package org.openspcoop2.pdd.core.handlers.notifier;

import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.PostOutRequestContext;
import org.openspcoop2.pdd.core.handlers.PostOutRequestHandler;

/**
 * NotifierPostOutRequestHandler
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NotifierPostOutRequestHandler implements PostOutRequestHandler {

	@Override
	public void invoke(PostOutRequestContext context) throws HandlerException {
		
		try{
		
			NotifierUtilities.updateNotifierState(context.getMessaggio(), NotifierType.POST_OUT_REQUEST, context);
			
		}catch(Exception e){
			throw new HandlerException(e.getMessage(),e);
		}
	}

}
