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

package org.openspcoop2.pdd.core.integrazione.backward_compatibility;

import javax.xml.soap.SOAPHeaderElement;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazioneException;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePDSoap;
import org.openspcoop2.pdd.core.integrazione.InRequestPDMessage;
import org.openspcoop2.pdd.core.integrazione.InResponsePDMessage;
import org.openspcoop2.pdd.core.integrazione.OutRequestPDMessage;
import org.openspcoop2.pdd.core.integrazione.OutResponsePDMessage;
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
public abstract class AbstractGestoreIntegrazionePDSoapBC extends AbstractCore implements IGestoreIntegrazionePDSoap{

	/** Utility per l'integrazione */
	UtilitiesIntegrazioneBC utilitiesRequestBC = null;
	UtilitiesIntegrazioneBC utilitiesResponseBC = null;

	/** OpenSPCoopProperties */
	OpenSPCoop2Properties openspcoopProperties = OpenSPCoop2Properties.getInstance();
	
	/** Logger utilizzato per debug. */
	private Logger log = null;

	protected boolean openspcoop2;
	
	public AbstractGestoreIntegrazionePDSoapBC(boolean openspcoop2){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		this.openspcoop2 = openspcoop2;
		if(this.log==null){
			this.log = LoggerWrapperFactory.getLogger(AbstractGestoreIntegrazionePDSoapBC.class);
		}
		try{
			this.utilitiesRequestBC = UtilitiesIntegrazioneBC.getInstancePDRequest(this.log, openspcoop2, false);
			this.utilitiesResponseBC = UtilitiesIntegrazioneBC.getInstancePDResponse(this.log, openspcoop2, false);
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
			
			String protocollo = this.getProtocolFactory()!=null ? this.getProtocolFactory().getProtocol() : null;
			
			this.utilitiesRequestBC.readHeader(soapMsg, integrazione,
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1(),
							protocollo);
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
			
			this.utilitiesRequestBC.deleteHeader(soapMsg,
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1());
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
			
			String protocollo = this.getProtocolFactory()!=null ? this.getProtocolFactory().getProtocol() : null;
			
			this.utilitiesRequestBC.updateHeader(soapMsg, 
					inRequestPDMessage.getSoggettoPropeprietarioPortaDelegata(), idServizio, idMessaggio, 
					servizioApplicativo, correlazioneApplicativa, null, 
					UtilitiesIntegrazioneBC.getIdTransazione(this.getPddContext()),
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1(),  // actor
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop1(),  // header name 
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop1(),  // prefix
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1(), // namespace
					this.openspcoop2 ?
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop1()	, // nomeElemento ExtInfoProtocol
					this.openspcoop2 ?
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop1()	, // nomeAttributo ExtInfoProtocol
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(inRequestPDMessage.getBustaRichiesta(), true, TipoIntegrazione.SOAP),
					protocollo
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
			
			String protocollo = this.getProtocolFactory()!=null ? this.getProtocolFactory().getProtocol() : null;
			
			this.utilitiesResponseBC.readHeader(soapMsg, integrazione,this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1(),
							protocollo);
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
			
			this.utilitiesResponseBC.deleteHeader(soapMsg,this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1());
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
			
			String protocollo = this.getProtocolFactory()!=null ? this.getProtocolFactory().getProtocol() : null;
			
			this.utilitiesResponseBC.updateHeader(soapMsg, 
					inResponsePDMessage.getSoggettoMittente(),
					inResponsePDMessage.getServizio(), idMessageRequest, idMessageResponse, 
					servizioApplicativo, correlazioneApplicativa, riferimentoCorrelazioneApplicativaRichiesta,
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1(),  // actor
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop1(),  // header name 
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop1(),  // prefix
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1(),  // namespace
					this.openspcoop2 ?
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop1()	, // nomeElemento ExtInfoProtocol
					this.openspcoop2 ?
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop1()	, // nomeAttributo ExtInfoProtocol
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(inResponsePDMessage.getBustaRichiesta(), false, TipoIntegrazione.SOAP),
					protocollo
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
			
			String protocollo = this.getProtocolFactory()!=null ? this.getProtocolFactory().getProtocol() : null;
			
			SOAPHeaderElement header = this.utilitiesResponseBC.buildHeader(integrazione, 
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop1(), // header name 
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop1(), // prefix
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1(), // namespace
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1(), // actor
					soapMsg,
					this.openspcoop2 ?
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop1()	, // nomeElemento ExtInfoProtocol
					this.openspcoop2 ?
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop1()	, // nomeAttributo ExtInfoProtocol
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(outResponsePDMessage.getBustaRichiesta(), false, TipoIntegrazione.SOAP),
					protocollo
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
