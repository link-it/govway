/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazioneException;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePASoap;
import org.openspcoop2.pdd.core.integrazione.InRequestPAMessage;
import org.openspcoop2.pdd.core.integrazione.InResponsePAMessage;
import org.openspcoop2.pdd.core.integrazione.OutRequestPAMessage;
import org.openspcoop2.pdd.core.integrazione.OutResponsePAMessage;
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
public abstract class AbstractGestoreIntegrazionePASoapBC extends AbstractCore implements IGestoreIntegrazionePASoap{

	/** Utility per l'integrazione */
	protected UtilitiesIntegrazioneBC utilitiesRequestBC = null;
	protected UtilitiesIntegrazioneBC utilitiesResponseBC = null;
	
	/** OpenSPCoopProperties */
	protected OpenSPCoop2Properties openspcoopProperties = OpenSPCoop2Properties.getInstance();
	
	/** Logger utilizzato per debug. */
	private Logger log = null;

	protected boolean openspcoop2;
	
	public AbstractGestoreIntegrazionePASoapBC(boolean openspcoop2){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		this.openspcoop2 = openspcoop2;
		if(this.log==null){
			this.log = LoggerWrapperFactory.getLogger(AbstractGestoreIntegrazionePASoapBC.class);
		}
		try{
			this.utilitiesRequestBC = UtilitiesIntegrazioneBC.getInstancePARequest(this.log, openspcoop2, false);
			this.utilitiesResponseBC = UtilitiesIntegrazioneBC.getInstancePAResponse(this.log, openspcoop2, false);
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
			
			this.utilitiesRequestBC.readHeader(soapMsg, integrazione, 
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1());
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
			
			this.utilitiesRequestBC.deleteHeader(soapMsg,
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1());
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
			
			this.utilitiesRequestBC.updateHeader(soapMsg, 
					inRequestPAMessage.getSoggettoMittente(), 
					inRequestPAMessage.getServizio(), 
					idMessaggio, 
					servizioApplicativo, correlazioneApplicativa, null,
					UtilitiesIntegrazioneBC.getIdTransazione(this.getPddContext()),
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1(),  // actor
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop1()	,  // header name 
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop1()	,  // prefix
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1(),  // namespace
					this.openspcoop2 ?
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop1()	, // nomeElemento ExtInfoProtocol
					this.openspcoop2 ?
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop1()	, // nomeAttributo ExtInfoProtocol
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
			
			SOAPHeaderElement header = this.utilitiesRequestBC.buildHeader(integrazione,
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop1()	,  // header name 
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop1()	,  // prefix
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1(),  // namespace
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1(),  // actor
					soapMsg,
					this.openspcoop2 ?
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop1()	, // nomeElemento ExtInfoProtocol
					this.openspcoop2 ?
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop1()	, // nomeAttributo ExtInfoProtocol
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
			
			this.utilitiesResponseBC.readHeader(soapMsg, integrazione, 
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1());
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
			
			this.utilitiesResponseBC.deleteHeader(soapMsg,
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1());
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
			
			this.utilitiesResponseBC.updateHeader(soapMsg, 
					inResponsePAMessage.getSoggettoMittente(), 
					inResponsePAMessage.getServizio(),
					idMessaggioRichiesta, idMessaggioRisposta,
					servizioApplicativo, correlazioneApplicativa, riferimentoCorrelazioneApplicativaRichiesta, 
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1(),  // actor
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop1()	,  // header name 
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop1()	,  // prefix
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1(),  // namespace
					this.openspcoop2 ?
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop1()	, // nomeElemento ExtInfoProtocol
					this.openspcoop2 ?
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop1()	, // nomeAttributo ExtInfoProtocol
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