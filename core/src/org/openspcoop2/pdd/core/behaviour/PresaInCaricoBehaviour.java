/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.core.behaviour;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * PresaInCaricoBehaviour
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PresaInCaricoBehaviour implements IBehaviour {

	@Override
	public Behaviour behaviour(GestoreMessaggi gestoreMessaggioRichiesta, Busta busta,
			RequestInfo requestInfo) throws CoreException {
		try{
			Behaviour behaviour = new Behaviour();
			
			BehaviourResponseTo responseTo = new BehaviourResponseTo();
			responseTo.setResponseTo(true);
			behaviour.setResponseTo(responseTo);
			
			BehaviourForwardTo forwardTo = new BehaviourForwardTo();
			forwardTo.setMessage(gestoreMessaggioRichiesta.getMessage());
			behaviour.getForwardTo().add(forwardTo);
			
			return behaviour;
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
		
	}

}
