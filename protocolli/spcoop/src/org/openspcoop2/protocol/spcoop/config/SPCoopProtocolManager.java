/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import org.slf4j.Logger;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.constants.SOAPFaultIntegrationGenericInfoMode;
import org.openspcoop2.protocol.sdk.constants.TipoIntegrazione;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.resources.TransportRequestContext;
import org.openspcoop2.utils.resources.TransportResponseContext;

/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.config.IProtocolManager} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopProtocolManager implements IProtocolManager {
	
	protected SPCoopProperties spcoopProperties = null;
	protected IProtocolFactory protocolFactory = null;
	protected Logger logger = null;
	public SPCoopProtocolManager(IProtocolFactory protocolFactory) throws ProtocolException{
		this.protocolFactory = protocolFactory;
		this.logger = this.protocolFactory.getLogger();
		this.spcoopProperties = SPCoopProperties.getInstance(this.logger);
	}

	@Override
	public IProtocolFactory getProtocolFactory() {
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
	
	
	
	
	/* *********** SOAP Fault della Porta ******************* */
	
	@Override
	public boolean isGenerazioneDetailsSOAPFaultProtocollo_EccezioneValidazione(){
		return this.spcoopProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione();
	}
	
	@Override
	public boolean isGenerazioneDetailsSOAPFaultProtocollo_EccezioneProcessamento(){
		return this.spcoopProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento();
	}
		
	@Override
	public boolean isGenerazioneDetailsSOAPFaultProtocolloConStackTrace(){
		return this.spcoopProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace();
	}
	
	@Override
	public boolean isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche(){
		return this.spcoopProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche();
	}
	
	@Override
	public boolean isGenerazioneDetailsSOAPFaultIntegratione_erroreServer(){
		return this.spcoopProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError();
	}
	
	@Override
	public boolean isGenerazioneDetailsSOAPFaultIntegratione_erroreClient(){
		return this.spcoopProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError();
	}
	
	@Override
	public boolean isGenerazioneDetailsSOAPFaultIntegrationeConStackTrace(){
		return this.spcoopProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace();
	}
	
	@Override
	public SOAPFaultIntegrationGenericInfoMode getModalitaGenerazioneInformazioniGeneriche_DetailsSOAPFaultIntegrazione(){
		Boolean value = this.spcoopProperties.isGenerazioneDetailsSOAPFaultIntegrazionConInformazioniGeneriche();
		if(value==null){
			return SOAPFaultIntegrationGenericInfoMode.SERVIZIO_APPLICATIVO;
		}
		else if(value){
			return SOAPFaultIntegrationGenericInfoMode.ABILITATO;
		}else{
			return SOAPFaultIntegrationGenericInfoMode.DISABILITATO;
		}
	}
	
	@Override
	public Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativo() {
		return this.spcoopProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo();
	}

	@Override
	public Boolean isAggiungiDetailErroreApplicativo_SoapFaultPdD() {
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
	public OpenSPCoop2Message updateOpenSPCoop2MessageRequest(SOAPVersion soapVersion,OpenSPCoop2Message msg, Busta busta) throws ProtocolException{
    	return msg;
    }
    
    @Override
	public OpenSPCoop2Message updateOpenSPCoop2MessageResponse(SOAPVersion soapVersion,OpenSPCoop2Message msg, Busta busta, 
    		NotifierInputStreamParams notifierInputStreamParams, 
    		TransportRequestContext transportRequestContext, TransportResponseContext transportResponseContext) throws ProtocolException{
    	return msg;
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
