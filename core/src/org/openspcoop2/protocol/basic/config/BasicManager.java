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

package org.openspcoop2.protocol.basic.config;

import java.util.Hashtable;
import java.util.Map;

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
 * BasicManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class BasicManager implements IProtocolManager {

	private IProtocolFactory protocolFactory = null;
	public BasicManager(IProtocolFactory protocolFactory){
		this.protocolFactory = protocolFactory;
	}
	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}
	

	
	/* *********** VALIDAZIONE/GENERAZIONE BUSTE ******************* */
	
	@Override
	public boolean isBustaServizio(Busta busta){
		return false;
	}
	
    @Override
	public String getKeywordTipoMittenteSconosciuto(){
    	return "Sconosciuto";
    }
    
    @Override
	public String getKeywordMittenteSconosciuto(){
    	return "Sconosciuto";
    }
	
	@Override
	public long getIntervalloScadenzaBuste(){
		return 7200;
	}
	
	@Override
	public boolean isGenerazioneElementiNonValidabiliRispettoXSD(){
		return false;
	}
	
	@Override
	public boolean isIgnoraEccezioniNonGravi(){
		return false;
	}
	
	@Override
	public boolean isGenerazioneListaEccezioniErroreProcessamento(){
		return false;
	}
	
	
	
	
	/* *********** SOAP Fault della Porta ******************* */
	
	@Override
	public boolean isGenerazioneDetailsSOAPFaultProtocollo_EccezioneValidazione(){
		return false;
	}
	
	@Override
	public boolean isGenerazioneDetailsSOAPFaultProtocollo_EccezioneProcessamento(){
		return true;
	}
		
	@Override
	public boolean isGenerazioneDetailsSOAPFaultProtocolloConStackTrace(){
		return false;
	}
	
	@Override
	public boolean isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche(){
		return true;
	}
	
	@Override
	public boolean isGenerazioneDetailsSOAPFaultIntegratione_erroreServer(){
		return true;
	}
	
	@Override
	public boolean isGenerazioneDetailsSOAPFaultIntegratione_erroreClient(){
		return false;
	}
	
	@Override
	public boolean isGenerazioneDetailsSOAPFaultIntegrationeConStackTrace(){
		return false;
	}
	
	@Override
	public SOAPFaultIntegrationGenericInfoMode getModalitaGenerazioneInformazioniGeneriche_DetailsSOAPFaultIntegrazione(){
		return SOAPFaultIntegrationGenericInfoMode.SERVIZIO_APPLICATIVO;
	}
	
	@Override
	public Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativo(){
		return null; // default in openspcoop2.properties
	}
	
	@Override
	public Boolean isAggiungiDetailErroreApplicativo_SoapFaultPdD(){
		return null; // default in openspcoop2.properties
	}
	
	
	
	
	
	/* *********** INTEGRAZIONE ******************* */
	
	@Override
	public Map<String, String> buildIntegrationProperties(Busta busta,
			boolean isRichiesta, TipoIntegrazione tipoIntegrazione)
			throws ProtocolException {
		if(busta!=null && busta.sizeProperties()>0){
			Map<String, String> map = new Hashtable<String, String>();
			String[]names = busta.getPropertiesNames();
			for (int i = 0; i < names.length; i++) {
				String nomeProprieta = names[i];
				String valoreProprieta = busta.getProperty(nomeProprieta);
				if(TipoIntegrazione.TRASPORTO.equals(tipoIntegrazione)){
					map.put("X-"+busta.getProtocollo().toUpperCase()+"-"+nomeProprieta, valoreProprieta);
				}else if(TipoIntegrazione.URL.equals(tipoIntegrazione)){
					map.put(busta.getProtocollo().toUpperCase()+nomeProprieta, valoreProprieta);
				}else{
					map.put(nomeProprieta, valoreProprieta);
				}
			}
			return map;
		}
		else{
			return null;
		}
		
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
		return true;
	}
	
    @Override
	public Integer getHttpReturnCodeEmptyResponseOneWay(){
    	return 200;
    }
	
    @Override
	public boolean isHttpOneWay_PD_HTTPEmptyResponse(){
    	return true;
    }
    
	@Override
	public boolean isBlockedTransaction_responseMessageWithTransportCodeError(){
		return false;
	}
    
    
}
