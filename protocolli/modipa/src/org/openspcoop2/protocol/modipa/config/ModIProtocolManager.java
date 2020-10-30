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


package org.openspcoop2.protocol.modipa.config;

import java.util.Map;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.config.BasicManager;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.FaultIntegrationGenericInfoMode;
import org.openspcoop2.protocol.sdk.constants.TipoIntegrazione;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.slf4j.Logger;

/**
 * Classe che implementa, in base al protocollo ModI, l'interfaccia {@link org.openspcoop2.protocol.sdk.config.IProtocolManager} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIProtocolManager extends BasicManager {
	
	protected ModIProperties modipaProperties = null;
	protected Logger logger = null;
	public ModIProtocolManager(IProtocolFactory<?> protocolFactory) throws ProtocolException{
		super(protocolFactory);
		this.logger = this.getProtocolFactory().getLogger();
		this.modipaProperties = ModIProperties.getInstance();
	}
	
	
	
	/* *********** VALIDAZIONE/GENERAZIONE BUSTE ******************* */
	
	@Override
	public boolean isGenerazioneListaEccezioniErroreProcessamento(){
		return true; // l'eccezione viene utilizzata per produrre un errore applicativo e/o per impostare un codice nel soap fault
	}
	
	
	
	/* *********** Fault della Porta (Protocollo, Porta Applicativa) ******************* */
	
	@Override
	public boolean isGenerazioneDetailsFaultProtocollo_EccezioneValidazione(){
		return this.modipaProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione();
	}
	
	@Override
	public boolean isGenerazioneDetailsFaultProtocollo_EccezioneProcessamento(){
		return this.modipaProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento();
	}
		
	@Override
	public boolean isGenerazioneDetailsFaultProtocolloConStackTrace(){
		return this.modipaProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace();
	}
	
	@Override
	public boolean isGenerazioneDetailsFaultProtocolloConInformazioniGeneriche(){
		return this.modipaProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche();
	}
	
	
	
	/* *********** Fault della Porta (Integrazione, Porta Delegata) ******************* */
	
	@Override
	public boolean isGenerazioneDetailsFaultIntegratione_erroreServer(){
		return this.modipaProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError();
	}
	
	@Override
	public boolean isGenerazioneDetailsFaultIntegratione_erroreClient(){
		return this.modipaProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError();
	}
	
	@Override
	public boolean isGenerazioneDetailsFaultIntegrationeConStackTrace(){
		return this.modipaProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace();
	}
	
	@Override
	public FaultIntegrationGenericInfoMode getModalitaGenerazioneInformazioniGeneriche_DetailsFaultIntegrazione(){
		Boolean value = this.modipaProperties.isGenerazioneDetailsSOAPFaultIntegrazionConInformazioniGeneriche();
		if(value==null){
			return FaultIntegrationGenericInfoMode.SERVIZIO_APPLICATIVO;
		}
		else if(value){
			return FaultIntegrationGenericInfoMode.ABILITATO;
		}else{
			return FaultIntegrationGenericInfoMode.DISABILITATO;
		}
	}
	
	
	
	/* *********** SOAP Fault della Porta (Generati dagli attori esterni) ******************* */
	
	@Override
	public Boolean isAggiungiDetailErroreApplicativo_FaultApplicativo() {
		return this.modipaProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo();
	}

	@Override
	public Boolean isAggiungiDetailErroreApplicativo_FaultPdD() {
		return this.modipaProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD();
	}
	
	
	
	/* *********** INTEGRAZIONE ******************* */
	
	@Override
	public Map<String, String> buildIntegrationProperties(Busta busta,
			boolean isRichiesta, TipoIntegrazione tipoIntegrazione)
			throws ProtocolException {
		
		return null;

	}
	
	@Override
	public OpenSPCoop2Message updateOpenSPCoop2MessageResponse(OpenSPCoop2Message msg, Busta busta, 
    		NotifierInputStreamParams notifierInputStreamParams, 
    		TransportRequestContext transportRequestContext, TransportResponseContext transportResponseContext,
    		IRegistryReader registryReader,
    		boolean integration) throws ProtocolException{
    	
		if(integration) {
			try {
			
				boolean createCorrelationIdIfNotExists = false;
				if(msg!=null) {
					if(ServiceBinding.REST.equals(msg.getServiceBinding())) {
						createCorrelationIdIfNotExists = this.modipaProperties.isRestSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists();
					}
					else {
						createCorrelationIdIfNotExists = this.modipaProperties.isSoapSecurityTokenPushCorrelationIdUseTransactionIdIfNotExists();
					}
				}
				
				if(busta!=null && createCorrelationIdIfNotExists) {
					String asyncInteractionType = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_TIPO);
					if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(asyncInteractionType)) {
						String asyncInteractionRole = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_RUOLO);
						if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
							
							if(ServiceBinding.REST.equals(msg.getServiceBinding())) {
								String headerCorrelationId = this.modipaProperties.getRestCorrelationIdHeader();
								String correlationIdFound = msg.getTransportResponseContext().getParameterTrasporto(headerCorrelationId);
								if(correlationIdFound==null || "".equals(correlationIdFound)) {
									msg.getTransportResponseContext().getParametersTrasporto().put(headerCorrelationId, ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_ID_CORRELAZIONE_AGGIUNTO_PER_CONSENTIRE_VALIDAZIONE_CONTENUTI);
								}
							}
							else {
								// TODO: add SOAPHeader
								//       fino a che non viene implementata la validazione dell'header SOAP l'aggiunta non serve
							}
							
						}
					}
				}
				
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}
		
		return super.updateOpenSPCoop2MessageResponse(msg, busta, 
				notifierInputStreamParams,transportRequestContext,transportResponseContext,registryReader,integration);
	}
	
	
	/* *********** CONNETTORE ******************* */
	
	@Override
	public boolean isSuccessfulHttpRedirectStatusCode(ServiceBinding serviceBinding) throws ProtocolException{
		return ServiceBinding.REST.equals(serviceBinding);
	}
	
}
