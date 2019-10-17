/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.pdd.core.behaviour.built_in;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.behaviour.Behaviour;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardTo;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardToFilter;
import org.openspcoop2.pdd.core.behaviour.BehaviourResponseTo;
import org.openspcoop2.pdd.core.behaviour.IBehaviour;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * MultiDeliverBehaviour
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MultiDeliverBehaviour implements IBehaviour {

	@Override
	public Behaviour behaviour(GestoreMessaggi gestoreMessaggioRichiesta, Busta busta, 
			PortaApplicativa pa, RequestInfo requestInfo) throws CoreException {
		
		try{

			List<IDServizioApplicativo> listaServiziApplicativi = new ArrayList<IDServizioApplicativo>();
			for (PortaApplicativaServizioApplicativo servizioApplicativo : pa.getServizioApplicativoList()) {
				if(servizioApplicativo.getDatiConnettore()==null || servizioApplicativo.getDatiConnettore().getStato()==null || 
						StatoFunzionalita.ABILITATO.equals(servizioApplicativo.getDatiConnettore().getStato())) {
					IDServizioApplicativo idSA = new IDServizioApplicativo();
					idSA.setIdSoggettoProprietario(new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
					idSA.setNome(servizioApplicativo.getNome());
					listaServiziApplicativi.add(idSA);
				}
			}
			
//			if(listaServiziApplicativi.size()<=1) {
//				Behaviour behaviour = new Behaviour();
//				BehaviourForwardTo forwardTo = new BehaviourForwardTo();
//				behaviour.getForwardTo().add(forwardTo);
//				return behaviour;
//			}
			
			Behaviour behaviour = new Behaviour();
			BehaviourForwardTo forwardTo = new BehaviourForwardTo();
			
			
			if(listaServiziApplicativi.size()>1) {
				
				// Vi e' la presa in carico.
				
				OpenSPCoop2Message msg = gestoreMessaggioRichiesta.getMessage();
				forwardTo.setMessage(msg);
				
				BehaviourResponseTo responseTo = new BehaviourResponseTo();
				responseTo.setResponseTo(true);
				behaviour.setResponseTo(responseTo);
				OpenSPCoop2Message replyTo = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(msg.getMessageType(),MessageRole.RESPONSE);
				behaviour.getResponseTo().setMessage(replyTo);
			}

			
			// Indico quali servizi applicativi devono ricevere il messaggio
			BehaviourForwardToFilter filter = new BehaviourForwardToFilter();
			for (IDServizioApplicativo idServizioApplicativo : listaServiziApplicativi) {
				filter.getAccessListServiziApplicativi().add(idServizioApplicativo);
			}
			forwardTo.setFilter(filter);
			behaviour.getForwardTo().add(forwardTo);
			
			return behaviour;
			
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
		
	}

}
