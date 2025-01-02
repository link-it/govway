/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.integrazione;

import javax.xml.soap.SOAPHeaderElement;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.TipoIntegrazione;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * Classe utilizzata per la ricezione di informazioni di integrazione 
 * dai servizi applicativi verso la porta di dominio.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreIntegrazionePDSoap extends AbstractCore implements IGestoreIntegrazionePDSoap{

	/** Utility per l'integrazione */
	UtilitiesIntegrazione utilitiesRequest = null;
	UtilitiesIntegrazione utilitiesResponse = null;

	/** OpenSPCoopProperties */
	OpenSPCoop2Properties openspcoopProperties = OpenSPCoop2Properties.getInstance();
	
	/** Logger utilizzato per debug. */
	private Logger log = null;

	
	public GestoreIntegrazionePDSoap(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(this.log==null){
			this.log = LoggerWrapperFactory.getLogger(GestoreIntegrazionePDSoap.class);
		}
		try{
			this.utilitiesRequest = UtilitiesIntegrazione.getInstancePDRequest(this.log);
			this.utilitiesResponse = UtilitiesIntegrazione.getInstancePDResponse(this.log);
		}catch(Exception e){
			this.log.error("Errore durante l'inizializzazione delle UtilitiesIntegrazione: "+e.getMessage(),e);
		}
	}
	
	// IN - Request
	
	@Override
	public void readInRequestHeader(HeaderIntegrazione integrazione,
			InRequestPDMessage inRequestPDMessage) throws HeaderIntegrazioneException{
		try{
			OpenSPCoop2Message msg = inRequestPDMessage.getMessage();
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())==false){
				throw new Exception("Non utilizzabile con un Service Binding Rest");
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			this.utilitiesRequest.readHeader(soapMsg, integrazione,this.openspcoopProperties.getHeaderSoapActorIntegrazione());
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDSoap, "+e.getMessage(),e);
		}
	}	
	
	@Override
	public void deleteInRequestHeader(InRequestPDMessage inRequestPDMessage) throws HeaderIntegrazioneException{
		try{
			OpenSPCoop2Message msg = inRequestPDMessage.getMessage();
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())==false){
				throw new Exception("Non utilizzabile con un Service Binding Rest");
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			this.utilitiesRequest.deleteHeader(soapMsg,this.openspcoopProperties.getHeaderSoapActorIntegrazione());
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDSoap, "+e.getMessage(),e);
		}
	}

	
	@Override
	public void updateInRequestHeader(InRequestPDMessage inRequestPDMessage,
			IDServizio idServizio,
			String idMessaggio,String servizioApplicativo,String correlazioneApplicativa) throws HeaderIntegrazioneException{
		try{
			OpenSPCoop2Message msg = inRequestPDMessage.getMessage();
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())==false){
				throw new Exception("Non utilizzabile con un Service Binding Rest");
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			this.utilitiesRequest.updateHeader(soapMsg, 
					inRequestPDMessage.getSoggettoPropeprietarioPortaDelegata(), idServizio, idMessaggio, 
					servizioApplicativo, correlazioneApplicativa, null, 
					UtilitiesIntegrazione.getIdTransazione(this.getPddContext()),
					this.openspcoopProperties.getHeaderSoapActorIntegrazione(),  // actor
					this.openspcoopProperties.getHeaderSoapNameIntegrazione(),  // header name 
					this.openspcoopProperties.getHeaderSoapPrefixIntegrazione(),  // prefix
					this.openspcoopProperties.getHeaderSoapActorIntegrazione(), // namespace
					this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione(), // nomeElemento ExtInfoProtocol
					this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione(), // nomeAttributo ExtInfoProtocol
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(inRequestPDMessage.getBustaRichiesta(), true, TipoIntegrazione.SOAP)
				);  
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDSoap, "+e.getMessage(),e);
		}
	}

	
	// OUT - Request
	
	@Override
	public void setOutRequestHeader(HeaderIntegrazione integrazione,
			OutRequestPDMessage outRequestPDMessage) throws HeaderIntegrazioneException{
	
		// nop;
		
	}
	
	
	// IN - Response
	
	@Override
	public void readInResponseHeader(HeaderIntegrazione integrazione,
			InResponsePDMessage inResponsePDMessage) throws HeaderIntegrazioneException{
		try{
			OpenSPCoop2Message msg = inResponsePDMessage.getMessage();
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())==false){
				throw new Exception("Non utilizzabile con un Service Binding Rest");
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			this.utilitiesResponse.readHeader(soapMsg, integrazione,this.openspcoopProperties.getHeaderSoapActorIntegrazione());
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDSoap, "+e.getMessage(),e);
		}
	}
	
	@Override
	public void deleteInResponseHeader(InResponsePDMessage inResponsePDMessage) throws HeaderIntegrazioneException{
		try{
			OpenSPCoop2Message msg = inResponsePDMessage.getMessage();
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())==false){
				throw new Exception("Non utilizzabile con un Service Binding Rest");
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			this.utilitiesResponse.deleteHeader(soapMsg,this.openspcoopProperties.getHeaderSoapActorIntegrazione());
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDSoap, "+e.getMessage(),e);
		}
	}
	
	@Override
	public void updateInResponseHeader(InResponsePDMessage inResponsePDMessage,
			String idMessageRequest,String idMessageResponse,String servizioApplicativo,String correlazioneApplicativa,String riferimentoCorrelazioneApplicativaRichiesta) throws HeaderIntegrazioneException{
		try{
			OpenSPCoop2Message msg = inResponsePDMessage.getMessage();
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())==false){
				throw new Exception("Non utilizzabile con un Service Binding Rest");
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			this.utilitiesResponse.updateHeader(soapMsg, 
					inResponsePDMessage.getSoggettoMittente(),
					inResponsePDMessage.getServizio(), idMessageRequest, idMessageResponse, 
					servizioApplicativo, correlazioneApplicativa, riferimentoCorrelazioneApplicativaRichiesta,
					this.openspcoopProperties.getHeaderSoapActorIntegrazione(),  // actor
					this.openspcoopProperties.getHeaderSoapNameIntegrazione(),  // header name 
					this.openspcoopProperties.getHeaderSoapPrefixIntegrazione(),  // prefix
					this.openspcoopProperties.getHeaderSoapActorIntegrazione(),  // namespace
					this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione(), // nomeElemento ExtInfoProtocol
					this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione(), // nomeAttributo ExtInfoProtocol
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(inResponsePDMessage.getBustaRichiesta(), false, TipoIntegrazione.SOAP)
				);
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDSoap, "+e.getMessage(),e);
		}
	}
	
	// OUT - Response
	
	@Override
	public void setOutResponseHeader(HeaderIntegrazione integrazione,
			OutResponsePDMessage outResponsePDMessage) throws HeaderIntegrazioneException{
		
		try{
			OpenSPCoop2Message msg = outResponsePDMessage.getMessage();
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())==false){
				throw new Exception("Non utilizzabile con un Service Binding Rest");
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			SOAPHeaderElement header = this.utilitiesResponse.buildHeader(integrazione, 
					this.openspcoopProperties.getHeaderSoapNameIntegrazione(), // header name 
					this.openspcoopProperties.getHeaderSoapPrefixIntegrazione(), // prefix
					this.openspcoopProperties.getHeaderSoapActorIntegrazione(), // namespace
					this.openspcoopProperties.getHeaderSoapActorIntegrazione(), // actor
					soapMsg,
					this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione(), // nomeElemento ExtInfoProtocol
					this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione(), // nomeAttributo ExtInfoProtocol
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(outResponsePDMessage.getBustaRichiesta(), false, TipoIntegrazione.SOAP)
				);
			//System.out.println((new org.openspcoop.dao.message.OpenSPCoopMessageFactory().createMessage().getAsString(header)));
			if(soapMsg.getSOAPHeader() == null){
				soapMsg.getSOAPPart().getEnvelope().addHeader();
			}
			//outResponsePDMessage.getMessage().getSOAPHeader().addChildElement(header);
			soapMsg.addHeaderElement(soapMsg.getSOAPHeader(), header);
			
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDSoap, "+e.getMessage(),e);
		}
		
	}
}
