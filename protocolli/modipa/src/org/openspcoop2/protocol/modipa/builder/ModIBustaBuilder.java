/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.modipa.builder;

import java.util.Date;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.builder.BustaBuilder;
import org.openspcoop2.protocol.modipa.AbstractModISecurityToken;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.validator.ModIRESTSecurity;
import org.openspcoop2.protocol.modipa.validator.ModISOAPSecurity;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.FaseImbustamento;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;

/**
 * Classe che implementa, in base al protocollo ModI, l'interfaccia {@link org.openspcoop2.protocol.sdk.builder.IBustaBuilder} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIBustaBuilder extends BustaBuilder<AbstractModISecurityToken<?>> {

	private ModIProperties modipaProperties;
	public ModIBustaBuilder(IProtocolFactory<?> factory,IState state) throws ProtocolException {
		super(factory,state);
		this.modipaProperties = ModIProperties.getInstance();
	}

	@Override
	public ProtocolMessage imbustamento(OpenSPCoop2Message msg, Context context, 
			Busta busta, Busta bustaRichiesta,
			RuoloMessaggio ruoloMessaggio,
			ProprietaManifestAttachments proprietaManifestAttachments,
			FaseImbustamento faseImbustamento)
			throws ProtocolException {
		
		if(FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO.equals(faseImbustamento)) {
			ProtocolMessage protocolMessage = new ProtocolMessage();
			protocolMessage.setPhaseUnsupported(true);
			return protocolMessage;
		}
		
		RequestInfo requestInfo = null;
		if(context!=null && context.containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
			requestInfo = (RequestInfo) context.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		}
		
		ProtocolMessage protocolMessage = super.imbustamento(msg, context,
				busta, bustaRichiesta,
				ruoloMessaggio, proprietaManifestAttachments, faseImbustamento);
		
		boolean imbusta = true;
		if(RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio) &&
			busta.sizeListaEccezioni()>0) {
			boolean ignoraEccezioniNonGravi = this.protocolFactory.createProtocolManager().isIgnoraEccezioniNonGravi();
			if(ignoraEccezioniNonGravi){
				if(busta.containsEccezioniGravi() ){
					imbusta = false;
				}
			}
			else{
				imbusta = false;
			}
		}
		
		if(imbusta) {
			ModIImbustamento imbustamento = new ModIImbustamento(this.getLog());
			protocolMessage = imbustamento.buildMessage(msg, context, busta, bustaRichiesta,
					ruoloMessaggio,  
					this.getProtocolFactory().getCachedRegistryReader(this.state, requestInfo), 
					this.getProtocolFactory().getCachedConfigIntegrationReader(this.state, requestInfo),
					this.getProtocolFactory(), this.state);
		}
			
		if(RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio) && busta.sizeListaEccezioni()>0 ){
			
			// le eccezioni vengono tornate anche per gli errori di processamento poiche' in ModIProtocolVersionManager
			// e' stato cablato il metodo isGenerazioneListaEccezioniErroreProcessamento al valore 'true'
			
			// Per quanto riguarda la generazione dei codici SOAPFault personalizzati e/o la generazione dell'elemento errore-applicativo
			// la scelta e' delegata a due proprieta' nel file di proprieta'.
			//
			// Infine la scelta della presenza o meno dell'elemento OpenSPCoop2Details lo stesso viene pilotata dalle proprieta' presenti nel file di proprieta'
		
			boolean ignoraEccezioniNonGravi = this.protocolFactory.createProtocolManager().isIgnoraEccezioniNonGravi();
			if(ignoraEccezioniNonGravi){
				if(busta.containsEccezioniGravi() ){
					this.enrichFault(msg, busta, ignoraEccezioniNonGravi,
							this.modipaProperties.isPortaApplicativaBustaErrorePersonalizzaElementiFault(),
							this.modipaProperties.isPortaApplicativaBustaErroreAggiungiErroreApplicativo());
				}	
			}
			else{
				this.enrichFault(msg, busta, ignoraEccezioniNonGravi,
						this.modipaProperties.isPortaApplicativaBustaErrorePersonalizzaElementiFault(),
						this.modipaProperties.isPortaApplicativaBustaErroreAggiungiErroreApplicativo());
			}

		}
			
		return protocolMessage;
	}
	
	@Override
	public ProtocolMessage sbustamento(OpenSPCoop2Message msg, Context context, 
			Busta busta, RuoloMessaggio ruoloMessaggio,
			ProprietaManifestAttachments proprietaManifestAttachments, FaseSbustamento faseSbustamento,
			ServiceBinding integrationServiceBinding, ServiceBindingConfiguration serviceBindingConfiguration)
			throws ProtocolException {
		
		ProtocolMessage protocolMessage = null;
		
		if(FaseSbustamento.PRE_CONSEGNA_RICHIESTA.equals(faseSbustamento) || FaseSbustamento.PRE_CONSEGNA_RISPOSTA.equals(faseSbustamento) ) {
		
			protocolMessage = new ProtocolMessage();
			if(msg!=null) {
				
				Object soapInfo = msg.getContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_SBUSTAMENTO_SOAP);
				Object restInfo = msg.getContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_SBUSTAMENTO_REST);
				
				if(soapInfo!=null) {
					
					// sbustamento SOAP
					
					ModISOAPSecurity soapSecurity = (ModISOAPSecurity) soapInfo;
					try {
						soapSecurity.clean(msg.castAsSoap());
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}
					
				}
				else if(restInfo!=null) {
					
					// sbustamento REST
					
					ModIRESTSecurity restSecurity = (ModIRESTSecurity) restInfo;
					try {
						restSecurity.clean(msg);
					}catch(Exception e) {
						throw new ProtocolException(e.getMessage(),e);
					}

				}
			}
			protocolMessage.setMessage(msg);
			
		}
		else {
			protocolMessage = super.sbustamento(msg, context,
					busta, ruoloMessaggio, proprietaManifestAttachments, faseSbustamento,
				integrationServiceBinding, serviceBindingConfiguration);
		}
		
		protocolMessage.setUseBustaRawContentReadByValidation(true);
		return protocolMessage;
	}
	
	@Override
	public String newID(IDSoggetto idSoggetto, String idTransazione, RuoloMessaggio ruoloMessaggio) throws ProtocolException {
		return super.newID( idSoggetto, idTransazione, ruoloMessaggio, this.modipaProperties.generateIDasUUID());
	}
	
	
	@Override
	public Date extractDateFromID(String id) throws ProtocolException {
		return extractDateFromID(id, this.modipaProperties.generateIDasUUID());
		
	}
	
}
