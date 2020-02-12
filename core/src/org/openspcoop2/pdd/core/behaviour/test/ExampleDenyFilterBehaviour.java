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
package org.openspcoop2.pdd.core.behaviour.test;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.behaviour.Behaviour;
import org.openspcoop2.pdd.core.behaviour.BehaviourEmitDiagnosticException;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardTo;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardToFilter;
import org.openspcoop2.pdd.core.behaviour.IBehaviour;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * ExampleDenyFilterBehaviour
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExampleDenyFilterBehaviour extends AbstractCore implements IBehaviour {

	@Override
	public Behaviour behaviour(GestoreMessaggi gestoreMessaggioRichiesta, Busta busta,
			PortaApplicativa pa, RequestInfo requestInfo) throws BehaviourException,BehaviourEmitDiagnosticException {
		try{
			Behaviour behaviour = new Behaviour();
			BehaviourForwardTo forwardTo = new BehaviourForwardTo();
			
			BehaviourForwardToFilter filter = new BehaviourForwardToFilter();
			IDServizioApplicativo id = new IDServizioApplicativo();
			id.setIdSoggettoProprietario(new IDSoggetto(busta.getTipoDestinatario(),busta.getDestinatario()));
			id.setNome("silGop3");
			filter.getDenyListServiziApplicativi().add(id);
			forwardTo.setFilter(filter);
			
			behaviour.getForwardTo().add(forwardTo);
			return behaviour;
		}catch(Exception e){
			throw new BehaviourException(e.getMessage(),e);
		}
		
	}

}
