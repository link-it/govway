/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.protocol.sdi.config;

import java.util.Map;

import org.slf4j.Logger;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.protocol.basic.config.BasicManager;
import org.openspcoop2.protocol.sdi.constants.SDICostanti;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviFile;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviNotifica;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRicezioneFatture;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioTrasmissioneFatture;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.TipoIntegrazione;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.resources.TransportRequestContext;
import org.openspcoop2.utils.resources.TransportResponseContext;

/**
 * Classe che implementa, in base al protocollo SdI, l'interfaccia {@link org.openspcoop2.protocol.sdk.config.IProtocolManager} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDIProtocolManager extends BasicManager {
	


	protected SDIProperties sdiProperties = null;
	protected Logger logger = null;
	public SDIProtocolManager(IProtocolFactory protocolFactory) throws ProtocolException{
		super(protocolFactory);
		this.logger = this.getProtocolFactory().getLogger();
		this.sdiProperties = SDIProperties.getInstance(this.logger);
	}
	
	@Override
	public boolean isIgnoraEccezioniNonGravi(){
		return true;
	}
	
	@Override
	public Map<String, String> buildIntegrationProperties(Busta busta,
			boolean isRichiesta, TipoIntegrazione tipoIntegrazione)
			throws ProtocolException {
		
		if(busta==null){
			return null;
		}
		
		boolean buildInfo = false;
		
		if(busta!=null && busta.sizeProperties()>0){
			busta.removeProperty(SDICostanti.SDI_BUSTA_EXT_ESITO_COMMITTENTE);
			busta.removeProperty(SDICostanti.SDI_BUSTA_EXT_ERRORI);
		}
		
		if(isRichiesta){
			
			// Servizio
			if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE.equals(busta.getServizio())){
				if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_RICEVUTA_CONSEGNA.equals(busta.getAzione())){
					buildInfo = true;
				}
				else if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_NOTIFICA_MANCATA_CONSEGNA.equals(busta.getAzione())){
					buildInfo = true;
				}
				else if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_NOTIFICA_SCARTO.equals(busta.getAzione())){
					buildInfo = true;
				}
				else if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_NOTIFICA_ESITO.equals(busta.getAzione())){
					buildInfo = true;
				}
				else if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_NOTIFICA_DECORRENZA_TERMINI.equals(busta.getAzione())){
					buildInfo = true;
				}
				else if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_ATTESTAZIONE_TRASMISSIONE_FATTURA.equals(busta.getAzione())){
					buildInfo = true;
				}
				else{
					throw new ProtocolException("Servizio["+busta.getServizio()+"] con Azione["+busta.getAzione()+"] non gestita dal protocollo durante la fase di richiesta");
				}
			}
			else if(SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE.equals(busta.getServizio())){
				if(SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_AZIONE_RICEVI_FATTURE.equals(busta.getAzione())){
					buildInfo = true;
				}
				else if(SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_AZIONE_NOTIFICA_DECORRENZA_TERMINI.equals(busta.getAzione())){
					buildInfo = true;
				}
				else{
					throw new ProtocolException("Servizio["+busta.getServizio()+"] con Azione["+busta.getAzione()+"] non gestita dal protocollo durante la fase di richiesta");
				}
			}
			else{
				boolean whiteList = false;
				if(busta.getServizio()!=null && this.sdiProperties.getServiziWhiteList().contains(busta.getServizio())){
					if(busta.getAzione()!=null && this.sdiProperties.getAzioniWhiteList().contains(busta.getAzione())){
						this.logger.debug("Servizio ["+busta.getServizio()+"] e Azione ["+busta.getAzione()+"] in white list");
						whiteList = true;
					}
				}
				if(!whiteList){
					throw new ProtocolException("Servizio["+busta.getServizio()+"] non gestite dal protocollo durante la fase di richiesta");
				}
			}
			
		}
		else{

			// Servizio
			if(SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE.equals(busta.getServizio())
					&& SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE_AZIONE_RICEVI_FILE.equals(busta.getAzione())){
				buildInfo = true;
			}
			else if(SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_RICEVI_NOTIFICA.equals(busta.getServizio())
					&& SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_NOTIFICA_ESITO_AZIONE_NOTIFICA_ESITO.equals(busta.getAzione())){
				buildInfo = true;
			}
			else{
				boolean whiteList = false;
				if(busta.getServizio()!=null && this.sdiProperties.getServiziWhiteList().contains(busta.getServizio())){
					if(busta.getAzione()!=null && this.sdiProperties.getAzioniWhiteList().contains(busta.getAzione())){
						this.logger.debug("Servizio ["+busta.getServizio()+"] e Azione ["+busta.getAzione()+"] in white list");
						whiteList = true;
					}
				}
				if(!whiteList){
					throw new ProtocolException("Servizio["+busta.getServizio()+"] e Azione["+busta.getAzione()+"] non gestite dal protocollo durante la fase di risposta");
				}
			}
			
		}
		
		if(buildInfo){
			return super.buildIntegrationProperties(busta, isRichiesta, tipoIntegrazione);
		}
		else{
			return null;
		}
	}
	
	
	
	
	@Override
	public OpenSPCoop2Message updateOpenSPCoop2MessageResponse(SOAPVersion soapVersion,OpenSPCoop2Message msg, Busta busta, 
    		NotifierInputStreamParams notifierInputStreamParams, 
    		TransportRequestContext transportRequestContext, TransportResponseContext transportResponseContext) throws ProtocolException{
    			
		if(SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE.equals(busta.getServizio())){
			if(SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_AZIONE_RICEVI_FATTURE.equals(busta.getAzione())){
				if(transportResponseContext==null || transportResponseContext.getErrore()==null){
					// creo un nuovo messaggio, l'imbustamento si occupera' di creare la struttura
					OpenSPCoop2Message msgR = OpenSPCoop2MessageFactory.getMessageFactory().createEmptySOAPMessage(soapVersion,notifierInputStreamParams);
					msgR.setTransportRequestContext(transportRequestContext);
					msgR.setTransportResponseContext(transportResponseContext);
					return msgR;
				}
			}
		}
		
		return super.updateOpenSPCoop2MessageResponse(soapVersion, msg, busta, 
				notifierInputStreamParams,transportRequestContext,transportResponseContext);
	}
	
	
	
	
	
    @Override
	public Integer getHttpReturnCodeEmptyResponseOneWay(){
    	return 202;
    }
	
}
