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
package org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.behaviour.AbstractBehaviour;
import org.openspcoop2.pdd.core.behaviour.Behaviour;
import org.openspcoop2.pdd.core.behaviour.BehaviourEmitDiagnosticException;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardTo;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardToFilter;
import org.openspcoop2.pdd.core.behaviour.BehaviourResponseTo;
import org.openspcoop2.pdd.core.behaviour.IBehaviour;
import org.openspcoop2.pdd.core.behaviour.built_in.BehaviourType;
import org.openspcoop2.pdd.core.behaviour.conditional.ConditionalFilterResult;
import org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.Busta;
import org.slf4j.Logger;

/**
 * MultiDeliverBehaviour
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MultiDeliverBehaviour extends AbstractBehaviour implements IBehaviour {

	private BehaviourType bt;
	public MultiDeliverBehaviour(BehaviourType bt) {
		this.bt = bt;
	}
	
	
	@Override
	public Behaviour behaviour(GestoreMessaggi gestoreMessaggioRichiesta, Busta busta, 
			PortaApplicativa pa, RequestInfo requestInfo) throws BehaviourException,BehaviourEmitDiagnosticException {
		
		try{

			ConfigurazioneMultiDeliver configurazione = MultiDeliverUtils.read(pa, OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
			
			List<IDServizioApplicativo> listaServiziApplicativiAll = new ArrayList<IDServizioApplicativo>();
			
			List<IDServizioApplicativo> listaServiziApplicativi_consegnaSenzaRisposta = new ArrayList<IDServizioApplicativo>();
			IDServizioApplicativo idServizioApplicativoResponder = null;
			
			for (PortaApplicativaServizioApplicativo servizioApplicativo : pa.getServizioApplicativoList()) {
				
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setIdSoggettoProprietario(new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
				idSA.setNome(servizioApplicativo.getNome());
				listaServiziApplicativiAll.add(idSA);
				
				if(servizioApplicativo.getDatiConnettore()==null || servizioApplicativo.getDatiConnettore().getStato()==null || 
						StatoFunzionalita.ABILITATO.equals(servizioApplicativo.getDatiConnettore().getStato())) {
					
					if(BehaviourType.CONSEGNA_CON_NOTIFICHE.equals(this.bt)) {
					
						String nomeConnettore =  org.openspcoop2.pdd.core.behaviour.built_in.Costanti.NOME_CONNETTORE_DEFAULT;
						if(servizioApplicativo.getDatiConnettore()!=null) {
							nomeConnettore = servizioApplicativo.getDatiConnettore().getNome();
						}
						
						if(configurazione.getTransazioneSincrona_nomeConnettore()!=null && 
								configurazione.getTransazioneSincrona_nomeConnettore().equals(nomeConnettore)) {
							idServizioApplicativoResponder = idSA;
						}
						else {
							listaServiziApplicativi_consegnaSenzaRisposta.add(idSA);
						}
						
					}
					else {
						listaServiziApplicativi_consegnaSenzaRisposta.add(idSA);
					}
				}
			}
			
			if(!listaServiziApplicativi_consegnaSenzaRisposta.isEmpty()) {
				
				Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
				

				OpenSPCoop2Message msg = null;
				try {
					msg = gestoreMessaggioRichiesta.getMessage();
				}catch(Exception e) {
					throw new BehaviourException(e.getMessage(), e);
				}
				
				ConditionalFilterResult filterResult = ConditionalUtils.filter(pa, msg, busta, 
						requestInfo, this.getPddContext(), 
						this.msgDiag, log, 
						this.bt);
				
				if(filterResult!=null) { 
						// && !filterResult.getListServiziApplicativi().isEmpty()) { NOTA! la lista vuota viene appunto usata per far si che non siano utilizzati alcun applicativi
					listaServiziApplicativi_consegnaSenzaRisposta.clear();
				
					for (PortaApplicativaServizioApplicativo servizioApplicativo : filterResult.getListServiziApplicativi()) {
						
						if(servizioApplicativo.getDatiConnettore()==null || servizioApplicativo.getDatiConnettore().getStato()==null || 
								StatoFunzionalita.ABILITATO.equals(servizioApplicativo.getDatiConnettore().getStato())) {
						
							String nomeConnettore = org.openspcoop2.pdd.core.behaviour.built_in.Costanti.NOME_CONNETTORE_DEFAULT;
							if(servizioApplicativo.getDatiConnettore()!=null) {
								nomeConnettore = servizioApplicativo.getDatiConnettore().getNome();
							}
							
							if(configurazione.getTransazioneSincrona_nomeConnettore()==null ||
									!configurazione.getTransazioneSincrona_nomeConnettore().equals(nomeConnettore)) {
								IDServizioApplicativo idSA = new IDServizioApplicativo();
								idSA.setIdSoggettoProprietario(new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
								idSA.setNome(servizioApplicativo.getNome());
								listaServiziApplicativi_consegnaSenzaRisposta.add(idSA);
							}
							
						}
					}
				}
				
			}
			
			
			Behaviour behaviour = new Behaviour();
			behaviour.setApplicativeSyncResponder(idServizioApplicativoResponder);
			BehaviourForwardTo forwardTo = new BehaviourForwardTo();
			
			if(idServizioApplicativoResponder!=null) {
				boolean connettoriPerNotifiche = listaServiziApplicativi_consegnaSenzaRisposta.size()>0;
				if(connettoriPerNotifiche) {
					this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONSEGNA_MULTIPLA_SINCRONA_CONFIGURAZIONE, configurazione);
				}	
			}
						
			
			boolean forceSalvataggioMessaggio = false;
			
			if( (listaServiziApplicativi_consegnaSenzaRisposta.size()==1 && idServizioApplicativoResponder==null) 
					||
				(listaServiziApplicativi_consegnaSenzaRisposta.size()==0 && idServizioApplicativoResponder!=null) 
				) {
				
				// se l'unico servizio applicativo ma comunque richiede la memorizzazione via integration manager devo prenderlo in carico.
				IDServizioApplicativo idSA = null;
				if(idServizioApplicativoResponder!=null) {
					idSA = idServizioApplicativoResponder;
				}else {
					idSA = listaServiziApplicativi_consegnaSenzaRisposta.get(0);
				}
				ServizioApplicativo sa = null;
				boolean uniqueSA_integrationManager = false;
				boolean uniqueSA_connettore = false;
				try {
					sa = ConfigurazionePdDManager.getInstance().getServizioApplicativo(idSA);
					uniqueSA_integrationManager = ConfigurazionePdDManager.getInstance().invocazioneServizioConGetMessage(sa);
					uniqueSA_connettore = ConfigurazionePdDManager.getInstance().invocazioneServizioConConnettore(sa);
				}catch(Exception e) {
					throw new BehaviourException(e.getMessage(), e);
				}
				
				if(BehaviourType.CONSEGNA_CON_NOTIFICHE.equals(this.bt) || BehaviourType.CONSEGNA_CONDIZIONALE.equals(this.bt)) {
					if(!uniqueSA_connettore) {
						if(uniqueSA_integrationManager) {
							throw new BehaviourEmitDiagnosticException(this.msgDiag, MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
									"connettoriMultipli.servizioSincrono.consegnaIntegrationManager");
						}
						else {
							throw new BehaviourEmitDiagnosticException(this.msgDiag, MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
									"connettoriMultipli.servizioSincrono.consegnaNonTrasparente");
						}
					}
				}
				
				forceSalvataggioMessaggio = uniqueSA_integrationManager && uniqueSA_connettore;
			}
			
			boolean saveMessage = false;
			if(forceSalvataggioMessaggio) {
				saveMessage = true;
			}
			else {
				if(idServizioApplicativoResponder!=null) {
					saveMessage = listaServiziApplicativi_consegnaSenzaRisposta.size()>0;
				}
				else {
					saveMessage = listaServiziApplicativi_consegnaSenzaRisposta.size()>1;
				}
			}
			if(saveMessage) {
				OpenSPCoop2Message msg = null;
				try {
					msg = gestoreMessaggioRichiesta.getMessage();
				}catch(Exception e) {
					throw new BehaviourException(e.getMessage(), e);
				}
				forwardTo.setMessage(msg);
				
				if(idServizioApplicativoResponder==null) {
					BehaviourResponseTo responseTo = new BehaviourResponseTo();
					responseTo.setResponseTo(true);
					behaviour.setResponseTo(responseTo);
					OpenSPCoop2Message replyTo = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(msg.getMessageType(),MessageRole.RESPONSE);
					behaviour.getResponseTo().setMessage(replyTo);
				}
			}
						
			// Indico quali servizi applicativi devono ricevere il messaggio
			BehaviourForwardToFilter filter = new BehaviourForwardToFilter();
			for (IDServizioApplicativo idServizioApplicativo : listaServiziApplicativi_consegnaSenzaRisposta) {
				filter.getAccessListServiziApplicativi().add(idServizioApplicativo);
			}
			if(listaServiziApplicativi_consegnaSenzaRisposta.size()<=0) {
				// devo creare una deny list senno viene consegnato a tutti
				filter.setDenyListServiziApplicativi(listaServiziApplicativiAll);
			}
			forwardTo.setFilter(filter);
			behaviour.getForwardTo().add(forwardTo);
			
			return behaviour;
			
		}
		catch(BehaviourEmitDiagnosticException e){
			throw e;
		}
		catch(BehaviourException e){
			throw e;
		}
		
	}

}
