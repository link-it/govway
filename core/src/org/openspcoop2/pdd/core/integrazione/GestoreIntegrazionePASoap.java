/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.TipoIntegrazione;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;


/**
 * Classe utilizzata per la spedizione di informazioni di integrazione 
 * dalla porta di dominio verso i servizi applicativi.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreIntegrazionePASoap extends AbstractCore implements IGestoreIntegrazionePASoap{

	/** Utility per l'integrazione */
	UtilitiesIntegrazione utilities = null;
	
	/** OpenSPCoopProperties */
	OpenSPCoop2Properties openspcoopProperties = OpenSPCoop2Properties.getInstance();
	
	/** Logger utilizzato per debug. */
	private Logger log = null;

	
	public GestoreIntegrazionePASoap(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(this.log==null){
			this.log = LoggerWrapperFactory.getLogger(GestoreIntegrazionePASoap.class);
		}
		try{
			this.utilities = UtilitiesIntegrazione.getInstancePA(this.log);
		}catch(Exception e){
			this.log.error("Errore durante l'inizializzazione delle UtilitiesIntegrazione: "+e.getMessage(),e);
		}
	}
	
	// IN - Request
	
	@Override
	public void readInRequestHeader(HeaderIntegrazione integrazione,
			InRequestPAMessage inRequestPAMessage) throws HeaderIntegrazioneException{
		try{
			OpenSPCoop2Message msg = inRequestPAMessage.getMessage();
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())==false){
				throw new Exception("Non utilizzabile con un Service Binding Rest");
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			this.utilities.readHeader(soapMsg, integrazione, this.openspcoopProperties.getHeaderSoapActorIntegrazione());
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
	
	@Override
	public void deleteInRequestHeader(InRequestPAMessage inRequestPAMessage) throws HeaderIntegrazioneException{
		try{
			OpenSPCoop2Message msg = inRequestPAMessage.getMessage();
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())==false){
				throw new Exception("Non utilizzabile con un Service Binding Rest");
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			this.utilities.deleteHeader(soapMsg,this.openspcoopProperties.getHeaderSoapActorIntegrazione());
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
	
	@Override
	public void updateInRequestHeader(InRequestPAMessage inRequestPAMessage,
			String idMessaggio,String servizioApplicativo,String correlazioneApplicativa) throws HeaderIntegrazioneException{
		try{
			OpenSPCoop2Message msg = inRequestPAMessage.getMessage();
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())==false){
				throw new Exception("Non utilizzabile con un Service Binding Rest");
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			this.utilities.updateHeader(soapMsg, 
					inRequestPAMessage.getSoggettoMittente(), 
					inRequestPAMessage.getServizio(), 
					idMessaggio, 
					servizioApplicativo, correlazioneApplicativa, null,
					UtilitiesIntegrazione.getIdTransazione(this.getPddContext()),
					this.openspcoopProperties.getHeaderSoapActorIntegrazione(),  // actor
					this.openspcoopProperties.getHeaderSoapNameIntegrazione(),  // header name 
					this.openspcoopProperties.getHeaderSoapPrefixIntegrazione(),  // prefix
					this.openspcoopProperties.getHeaderSoapActorIntegrazione(),  // namespace
					this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione(), // nomeElemento ExtInfoProtocol
					this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione(), // nomeAttributo ExtInfoProtocol
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(inRequestPAMessage.getBustaRichiesta(), true, TipoIntegrazione.SOAP)
				);
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
	
	
	// OUT - Request
	
	@Override
	public void setOutRequestHeader(HeaderIntegrazione integrazione,
			OutRequestPAMessage outRequestPAMessage) throws HeaderIntegrazioneException{
		try{
			
			OpenSPCoop2Message msg = outRequestPAMessage.getMessage();
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())==false){
				throw new Exception("Non utilizzabile con un Service Binding Rest");
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			SOAPHeaderElement header = this.utilities.buildHeader(integrazione,
					this.openspcoopProperties.getHeaderSoapNameIntegrazione(), // header name 
					this.openspcoopProperties.getHeaderSoapPrefixIntegrazione(), // prefix
					this.openspcoopProperties.getHeaderSoapActorIntegrazione(), // namespace
					this.openspcoopProperties.getHeaderSoapActorIntegrazione(), // actor
					soapMsg,
					this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione(), // nomeElemento ExtInfoProtocol
					this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione(), // nomeAttributo ExtInfoProtocol
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(outRequestPAMessage.getBustaRichiesta(), true, TipoIntegrazione.SOAP)
				);
						
			if(soapMsg.getSOAPHeader() == null){
				soapMsg.getSOAPPart().getEnvelope().addHeader();
			}
			soapMsg.addHeaderElement(soapMsg.getSOAPHeader(), header);
			
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
	
	
	// IN - Response
		
	@Override
	public void readInResponseHeader(HeaderIntegrazione integrazione,
			InResponsePAMessage inResponsePAMessage) throws HeaderIntegrazioneException{
		try{
			OpenSPCoop2Message msg = inResponsePAMessage.getMessage();
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())==false){
				throw new Exception("Non utilizzabile con un Service Binding Rest");
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			this.utilities.readHeader(soapMsg, integrazione, this.openspcoopProperties.getHeaderSoapActorIntegrazione());
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
	
	@Override
	public void deleteInResponseHeader(InResponsePAMessage inResponsePAMessage) throws HeaderIntegrazioneException{
		try{
			OpenSPCoop2Message msg = inResponsePAMessage.getMessage();
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())==false){
				throw new Exception("Non utilizzabile con un Service Binding Rest");
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			this.utilities.deleteHeader(soapMsg,this.openspcoopProperties.getHeaderSoapActorIntegrazione());
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
		
	@Override
	public void updateInResponseHeader(InResponsePAMessage inResponsePAMessage,
			String idMessaggioRichiesta,String idMessaggioRisposta,String servizioApplicativo,String correlazioneApplicativa,String riferimentoCorrelazioneApplicativaRichiesta) throws HeaderIntegrazioneException{
		try{
			OpenSPCoop2Message msg = inResponsePAMessage.getMessage();
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())==false){
				throw new Exception("Non utilizzabile con un Service Binding Rest");
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			this.utilities.updateHeader(soapMsg, 
					inResponsePAMessage.getSoggettoMittente(), 
					inResponsePAMessage.getServizio(),
					idMessaggioRichiesta, idMessaggioRisposta,
					servizioApplicativo, correlazioneApplicativa, riferimentoCorrelazioneApplicativaRichiesta, 
					this.openspcoopProperties.getHeaderSoapActorIntegrazione(),  // actor
					this.openspcoopProperties.getHeaderSoapNameIntegrazione(),  // header name 
					this.openspcoopProperties.getHeaderSoapPrefixIntegrazione(),  // prefix
					this.openspcoopProperties.getHeaderSoapActorIntegrazione(),  // namespace
					this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione(), // nomeElemento ExtInfoProtocol
					this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione(), // nomeAttributo ExtInfoProtocol
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(inResponsePAMessage.getBustaRichiesta(), false, TipoIntegrazione.SOAP)
				);
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
		
	// OUT - Response
	
	@Override
	public void setOutResponseHeader(HeaderIntegrazione integrazione,
			OutResponsePAMessage outResponsePAMessage) throws HeaderIntegrazioneException{
		
		// nop;
		
	}
}