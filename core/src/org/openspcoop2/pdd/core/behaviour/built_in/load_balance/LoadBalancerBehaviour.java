/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.core.behaviour.built_in.load_balance;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.behaviour.AbstractBehaviour;
import org.openspcoop2.pdd.core.behaviour.Behaviour;
import org.openspcoop2.pdd.core.behaviour.BehaviourEmitDiagnosticException;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardTo;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardToFilter;
import org.openspcoop2.pdd.core.behaviour.BehaviourLoadBalancer;
import org.openspcoop2.pdd.core.behaviour.IBehaviour;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * LoadBalancerBehaviour
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoadBalancerBehaviour extends AbstractBehaviour implements IBehaviour {

	@Override
	public Behaviour behaviour(GestoreMessaggi gestoreMessaggioRichiesta, Busta busta,
			PortaApplicativa pa, RequestInfo requestInfo) throws BehaviourException,BehaviourEmitDiagnosticException {
		
		Behaviour behaviour = null;
		try{
			behaviour = new Behaviour();
			
			OpenSPCoop2Message msg = null;
			try {
				msg = gestoreMessaggioRichiesta.getMessage();
			}catch(Exception e) {
				throw new BehaviourException(e.getMessage(), e);
			}
			
			ConfigurazioneLoadBalancer config = ConfigurazioneLoadBalancer.read(pa, msg, busta, 
					requestInfo, this.getPddContext(), 
					this.msgDiag, OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
			if(config.getPool().isEmpty()) {
				throw new BehaviourException("Nessun connettore selezionabile");	
			}
			if(config.getConnectorSelected()==null || "".equals(config.getConnectorSelected())) {
				throw new BehaviourException("Nessun connettore selezionato");
			}
			
			String nomeConnettore = config.getConnectorSelected();
					
			for (PortaApplicativaServizioApplicativo servizioApplicativo : pa.getServizioApplicativoList()) {
				if(servizioApplicativo.getDatiConnettore()==null || servizioApplicativo.getDatiConnettore().getStato()==null || 
						StatoFunzionalita.ABILITATO.equals(servizioApplicativo.getDatiConnettore().getStato())) {
					
					String nomeConnettoreSA = org.openspcoop2.pdd.core.behaviour.built_in.Costanti.NOME_CONNETTORE_DEFAULT;
					if(servizioApplicativo.getDatiConnettore()!=null && servizioApplicativo.getDatiConnettore().getNome()!=null) {
						nomeConnettoreSA = servizioApplicativo.getDatiConnettore().getNome();
					}
					if(nomeConnettore.equals(nomeConnettoreSA)) {
						
						BehaviourForwardTo forwardTo = new BehaviourForwardTo();
						BehaviourForwardToFilter filter = new BehaviourForwardToFilter();
						forwardTo.setFilter(filter);
						IDServizioApplicativo id = new IDServizioApplicativo();
						id.setIdSoggettoProprietario(new IDSoggetto(busta.getTipoDestinatario(),busta.getDestinatario()));
						id.setNome(servizioApplicativo.getNome());
						filter.getAccessListServiziApplicativi().add(id);
						behaviour.getForwardTo().add(forwardTo);
			
						this.getPddContext().addObject(CostantiPdD.CONNETTORE_MULTIPLO_SELEZIONATO, servizioApplicativo.getNome());
						
						BehaviourLoadBalancer c = new BehaviourLoadBalancer();
						c.setLoadBalancerPool(config.getPool());
						c.setConnectorName(nomeConnettore);
						behaviour.setLoadBalancer(c);
						
						break;
						
					}
				}
			}

		}
		catch(BehaviourEmitDiagnosticException e){
			throw e;
		}
		catch(BehaviourException e){
			throw e;
		}
		
		return behaviour;
	}

}
