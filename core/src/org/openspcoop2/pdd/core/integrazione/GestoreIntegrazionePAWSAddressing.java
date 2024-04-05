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

package org.openspcoop2.pdd.core.integrazione;

import jakarta.xml.soap.SOAPHeaderElement;

import org.slf4j.Logger;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;


/**
 * Classe utilizzata per la spedizione di informazioni di integrazione 
 * dalla porta di dominio verso i servizi applicativi.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreIntegrazionePAWSAddressing extends AbstractCore implements IGestoreIntegrazionePASoap{

	/** Utility per l'integrazione */
	UtilitiesIntegrazioneWSAddressing utilities = null;
	
	/** OpenSPCoopProperties */
	OpenSPCoop2Properties openspcoopProperties = OpenSPCoop2Properties.getInstance();
	
	/** Logger utilizzato per debug. */
	private Logger log = null;

	
	public GestoreIntegrazionePAWSAddressing(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(this.log==null){
			this.log = LoggerWrapperFactory.getLogger(GestoreIntegrazionePAWSAddressing.class);
		}

		try{
			this.utilities = UtilitiesIntegrazioneWSAddressing.getInstance(this.log);
		}catch(Exception e){
			this.log.error("Errore durante l'inizializzazione delle UtilitiesIntegrazioneWSAddressing: "+e.getMessage(),e);
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
			
			this.utilities.readHeader(soapMsg, integrazione, UtilitiesIntegrazioneWSAddressing.INTERPRETA_COME_ID_BUSTA, this.openspcoopProperties.getHeaderSoapActorIntegrazione()+"/wsa");
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
			
			this.utilities.deleteHeader(soapMsg, this.openspcoopProperties.getHeaderSoapActorIntegrazione()+"/wsa");
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
					idMessaggio, servizioApplicativo, correlazioneApplicativa, 
					UtilitiesIntegrazione.getIdTransazione(this.getPddContext()),
					this.openspcoopProperties.getHeaderSoapActorIntegrazione()+"/wsa");
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
			
			if(soapMsg.getSOAPHeader() == null){
				soapMsg.getSOAPPart().getEnvelope().addHeader();
			}
			
			if(integrazione.getBusta()!=null){
				
				HeaderIntegrazioneBusta hBusta = integrazione.getBusta();
					
				if(hBusta.getDestinatario()!=null && hBusta.getServizio()!=null){
					
					// To
					SOAPHeaderElement wsaTO = UtilitiesIntegrazioneWSAddressing.buildWSATo(soapMsg,this.openspcoopProperties.getHeaderSoapActorIntegrazione()+"/wsa",
							hBusta.getTipoDestinatario(),hBusta.getDestinatario(), 
							hBusta.getTipoServizio(), hBusta.getServizio(), hBusta.getVersioneServizio());
					soapMsg.addHeaderElement(soapMsg.getSOAPHeader(), wsaTO);
										
					// Action
					if(hBusta.getAzione()!=null){
						SOAPHeaderElement wsaAction = UtilitiesIntegrazioneWSAddressing.buildWSAAction(soapMsg,this.openspcoopProperties.getHeaderSoapActorIntegrazione()+"/wsa",
								hBusta.getTipoDestinatario(),hBusta.getDestinatario(), 
								hBusta.getTipoServizio(), hBusta.getServizio(),hBusta.getVersioneServizio(), hBusta.getAzione());
						soapMsg.addHeaderElement(soapMsg.getSOAPHeader(), wsaAction);
					}
				}
				
				if(hBusta.getMittente()!=null){
					SOAPHeaderElement wsaFROM = UtilitiesIntegrazioneWSAddressing.buildWSAFrom(soapMsg,this.openspcoopProperties.getHeaderSoapActorIntegrazione()+"/wsa",integrazione.getServizioApplicativo(),hBusta.getTipoMittente(),hBusta.getMittente());
					soapMsg.addHeaderElement(soapMsg.getSOAPHeader(), wsaFROM);
				}
					
				if(hBusta.getID()!=null){
					SOAPHeaderElement wsaID = UtilitiesIntegrazioneWSAddressing.buildWSAID(soapMsg,this.openspcoopProperties.getHeaderSoapActorIntegrazione()+"/wsa",hBusta.getID());
					soapMsg.addHeaderElement(soapMsg.getSOAPHeader(), wsaID);
				}
				
				if(hBusta.getRiferimentoMessaggio()!=null || hBusta.getIdCollaborazione()!=null){
					String rif = hBusta.getRiferimentoMessaggio();
					if(rif==null){
						rif = hBusta.getIdCollaborazione();
					}
					SOAPHeaderElement wsaRelatesTo = UtilitiesIntegrazioneWSAddressing.buildWSARelatesTo(soapMsg,this.openspcoopProperties.getHeaderSoapActorIntegrazione()+"/wsa",rif);
					soapMsg.addHeaderElement(soapMsg.getSOAPHeader(), wsaRelatesTo);
				}
			}
			
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
			
			this.utilities.readHeader(soapMsg, integrazione, UtilitiesIntegrazioneWSAddressing.INTERPRETA_COME_ID_APPLICATIVO, this.openspcoopProperties.getHeaderSoapActorIntegrazione()+"/wsa");
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
			
			this.utilities.deleteHeader(soapMsg, this.openspcoopProperties.getHeaderSoapActorIntegrazione()+"/wsa");
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
	
	@Override
	public void updateInResponseHeader(InResponsePAMessage inResponsePAMessage,
			String idMessageRequest,String idMessageResponse,String servizioApplicativo,String correlazioneApplicativa,String riferimentoCorrelazioneApplicativaRichiesta) throws HeaderIntegrazioneException{
		try{
			OpenSPCoop2Message msg = inResponsePAMessage.getMessage();
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())==false){
				throw new Exception("Non utilizzabile con un Service Binding Rest");
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			this.utilities.updateHeader(soapMsg, 
					inResponsePAMessage.getSoggettoMittente(),
					inResponsePAMessage.getServizio(),
					idMessageRequest, idMessageResponse, servizioApplicativo, correlazioneApplicativa, this.openspcoopProperties.getHeaderSoapActorIntegrazione()+"/wsa");
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