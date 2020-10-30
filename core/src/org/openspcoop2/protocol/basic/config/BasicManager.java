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

package org.openspcoop2.protocol.basic.config;

import java.util.Hashtable;
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
 * BasicManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class BasicManager extends BasicComponentFactory implements IProtocolManager {

	public BasicManager(IProtocolFactory<?> protocolFactory) throws ProtocolException{
		super(protocolFactory);
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
	
	
	
	
	/* *********** SOAP Fault della Porta (Protocollo, Porta Applicativa) ******************* */

	@Override
	public boolean isGenerazioneDetailsFaultProtocollo_EccezioneValidazione(){
		return false;
	}
	
	@Override
	public boolean isGenerazioneDetailsFaultProtocollo_EccezioneProcessamento(){
		return true;
	}
		
	@Override
	public boolean isGenerazioneDetailsFaultProtocolloConStackTrace(){
		return false;
	}
	
	@Override
	public boolean isGenerazioneDetailsFaultProtocolloConInformazioniGeneriche(){
		return true;
	}
	
	
	/* *********** SOAP Fault della Porta (Integrazione, Porta Delegata) ******************* */
	
	@Override
	public boolean isGenerazioneDetailsFaultIntegratione_erroreServer(){
		return true;
	}
	
	@Override
	public boolean isGenerazioneDetailsFaultIntegratione_erroreClient(){
		return false;
	}
	
	@Override
	public boolean isGenerazioneDetailsFaultIntegrationeConStackTrace(){
		return false;
	}
	
	@Override
	public FaultIntegrationGenericInfoMode getModalitaGenerazioneInformazioniGeneriche_DetailsFaultIntegrazione(){
		return FaultIntegrationGenericInfoMode.SERVIZIO_APPLICATIVO;
	}
	
	
	/* *********** SOAP Fault della Porta (Generati dagli attori esterni) ******************* */
	
	@Override
	public Boolean isAggiungiDetailErroreApplicativo_FaultApplicativo(){
		return null; // default in govway.properties
	}
	
	@Override
	public Boolean isAggiungiDetailErroreApplicativo_FaultPdD(){
		return null; // default in govway.properties
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
					// X- e' deprecato
					//map.put("X-"+busta.getProtocollo().toUpperCase()+"-"+nomeProprieta, valoreProprieta);
					// Il prefisso viene deciso da files di properties
					//map.put("GovWay-"+busta.getProtocollo().toUpperCase()+"-"+nomeProprieta, valoreProprieta);
					map.put(busta.getProtocollo().toUpperCase()+"-"+nomeProprieta, valoreProprieta);
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
		return ServiceBinding.REST.equals(serviceBinding);
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
