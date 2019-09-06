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


package org.openspcoop2.protocol.modipa.config;

import java.util.Map;

import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.config.BasicManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.FaultIntegrationGenericInfoMode;
import org.openspcoop2.protocol.sdk.constants.TipoIntegrazione;
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
	
	/* *********** CONNETTORE ******************* */
	
	@Override
	public boolean isSuccessfulHttpRedirectStatusCode(ServiceBinding serviceBinding) throws ProtocolException{
		return ServiceBinding.REST.equals(serviceBinding);
	}
	
}
