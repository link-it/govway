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


package org.openspcoop2.protocol.spcoop.config;

import java.util.Map;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.BasicComponentFactory;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.constants.FaultIntegrationGenericInfoMode;
import org.openspcoop2.protocol.sdk.constants.TipoIntegrazione;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportResponseContext;

/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.config.IProtocolManager} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopProtocolManager extends BasicComponentFactory implements IProtocolManager {
	
	protected SPCoopProperties spcoopProperties = null;
	public SPCoopProtocolManager(IProtocolFactory<?> protocolFactory) throws ProtocolException{
		super(protocolFactory);
		this.spcoopProperties = SPCoopProperties.getInstance(this.log);
	}

	@Override
	public IProtocolFactory<?> getProtocolFactory() {
		return this.protocolFactory;
	}	
	
	
	
	
	
	
	/* *********** VALIDAZIONE/GENERAZIONE BUSTE ******************* */
	
	/**
	 * Indicazione se la busta e' una busta di servizio
	 *   
	 * @return Indicazione se la busta e' una busta di servizio
	 */
	@Override
	public boolean isBustaServizio(Busta busta){
		//	Una busta e' una busta di servizio, se:
		// - non possiede un servizio (non e' quindi una richiesta)
		// - non possiede un riferimentoMsg (non e' quindi una risposta)
		return (busta.getServizio()==null && busta.getRiferimentoMessaggio()==null) ;
	}
	
    @Override
	public String getKeywordTipoMittenteSconosciuto(){
    	return this.spcoopProperties.getKeywordTipoMittenteSconosciuto();
    }
    
    @Override
	public String getKeywordMittenteSconosciuto(){
    	return this.spcoopProperties.getKeywordMittenteSconosciuto();
    }
	
	@Override
	public long getIntervalloScadenzaBuste(){
		try{
			return this.spcoopProperties.getIntervalloScadenzaBuste();
		}catch(Exception e){
			return -1;
		}
	}

	@Override
	public boolean isGenerazioneElementiNonValidabiliRispettoXSD(){
		return this.spcoopProperties.isGenerazioneElementiNonValidabiliRispettoXSD();
	}
	
	@Override
	public boolean isIgnoraEccezioniNonGravi(){
		return this.spcoopProperties.isIgnoraEccezioniNonGravi();
	}
	
	@Override
	public boolean isGenerazioneListaEccezioniErroreProcessamento(){
		return this.spcoopProperties.isGenerazioneListaEccezioniErroreProcessamento();
	}
	
	
	
	
	/* *********** SOAP Fault della Porta (Protocollo, Porta Applicativa) ******************* */
	
	@Override
	public boolean isGenerazioneDetailsFaultProtocollo_EccezioneValidazione(){
		return this.spcoopProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione();
	}
	
	@Override
	public boolean isGenerazioneDetailsFaultProtocollo_EccezioneProcessamento(){
		return this.spcoopProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento();
	}
		
	@Override
	public boolean isGenerazioneDetailsFaultProtocolloConStackTrace(){
		return this.spcoopProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace();
	}
	
	@Override
	public boolean isGenerazioneDetailsFaultProtocolloConInformazioniGeneriche(){
		return this.spcoopProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche();
	}
	
	
	
	/* *********** SOAP Fault della Porta (Integrazione, Porta Delegata) ******************* */
	
	@Override
	public boolean isGenerazioneDetailsFaultIntegratione_erroreServer(){
		return this.spcoopProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError();
	}
	
	@Override
	public boolean isGenerazioneDetailsFaultIntegratione_erroreClient(){
		return this.spcoopProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError();
	}
	
	@Override
	public boolean isGenerazioneDetailsFaultIntegrationeConStackTrace(){
		return this.spcoopProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace();
	}
	
	@Override
	public FaultIntegrationGenericInfoMode getModalitaGenerazioneInformazioniGeneriche_DetailsFaultIntegrazione(){
		Boolean value = this.spcoopProperties.isGenerazioneDetailsSOAPFaultIntegrazionConInformazioniGeneriche();
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
		return this.spcoopProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo();
	}

	@Override
	public Boolean isAggiungiDetailErroreApplicativo_FaultPdD() {
		return this.spcoopProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD();
	}
	
	
	
	
	/* *********** INTEGRAZIONE ******************* */
	
    @Override
	public Map<String, String> buildIntegrationProperties(Busta busta,
			boolean isRichiesta, TipoIntegrazione tipoIntegrazione)
			throws ProtocolException {
    	// SPCoop non possiede informazioni aggiuntive di protocollo
    	return null;
    }
    
    @Override
	public OpenSPCoop2Message updateOpenSPCoop2MessageRequest(OpenSPCoop2Message msg, Busta busta,
    		IRegistryReader registryReader) throws ProtocolException{
    	return msg;
    }
    
    @Override
	public OpenSPCoop2Message updateOpenSPCoop2MessageResponse(OpenSPCoop2Message msg, Busta busta, 
    		NotifierInputStreamParams notifierInputStreamParams, 
    		TransportRequestContext transportRequestContext, TransportResponseContext transportResponseContext,
    		IRegistryReader registryReader,
    		boolean integration) throws ProtocolException{
    	return msg;
    }
	
	
	
	
	
	/* *********** CONNETTORE ******************* */
	
    @Override
	public boolean isStaticRoute()  throws ProtocolException{
    	return false;
    }
    
	@Override
	public org.openspcoop2.core.registry.Connettore getStaticRoute(IDSoggetto idSoggettoMittente, IDServizio idServizio,
			IRegistryReader registryReader) throws ProtocolException{
		return null;
	}
	
	@Override
	public boolean isSuccessfulHttpRedirectStatusCode(ServiceBinding serviceBinding) throws ProtocolException{
		return false;
	}
	
	
	
	
	/* *********** ALTRO ******************* */
	
    @Override
	public boolean isHttpEmptyResponseOneWay(){
    	return this.spcoopProperties.isHttpEmptyResponseOneWay();
    }
	
    @Override
	public Integer getHttpReturnCodeEmptyResponseOneWay(){
    	return this.spcoopProperties.getHttpReturnCodeEmptyResponseOneWay();
    }
    
    @Override
	public boolean isHttpOneWay_PD_HTTPEmptyResponse(){
    	return this.spcoopProperties.isHttpOneWay_PD_HTTPEmptyResponse();
    }

    @Override
	public boolean isBlockedTransaction_responseMessageWithTransportCodeError(){
    	return this.spcoopProperties.isResponseMessageWithTransportCodeError_blockedTransaction();
    }


}
