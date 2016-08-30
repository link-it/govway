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

package org.openspcoop2.protocol.spcoop.backward_compatibility.integrazione;

import javax.xml.soap.SOAPHeaderElement;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazioneBusta;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazioneException;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePDSoap;
import org.openspcoop2.pdd.core.integrazione.InRequestPDMessage;
import org.openspcoop2.pdd.core.integrazione.InResponsePDMessage;
import org.openspcoop2.pdd.core.integrazione.OutRequestPDMessage;
import org.openspcoop2.pdd.core.integrazione.OutResponsePDMessage;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.spcoop.backward_compatibility.config.BackwardCompatibilityProperties;
import org.openspcoop2.protocol.spcoop.backward_compatibility.config.Costanti;
import org.openspcoop2.protocol.spcoop.backward_compatibility.services.BackwardCompatibilityStartup;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * Classe utilizzata per la ricezione di informazioni di integrazione 
 * dai servizi applicativi verso la porta di dominio.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreIntegrazionePDWSAddressing extends AbstractCore implements IGestoreIntegrazionePDSoap{

	/** Utility per l'integrazione */
	UtilitiesIntegrazioneWSAddressing utilities = null;

	/** BackwardCompatibilityProperties */
	private BackwardCompatibilityProperties backwardCompatibilityProperties = null;
	
	/** Logger utilizzato per debug. */
	private Logger log = null;

	/** GestoreIntegrazionePD Originale */
	private org.openspcoop2.pdd.core.integrazione.GestoreIntegrazionePDWSAddressing gestoreIntegrazioneOpenSPCoopV2 = null;
	
	public GestoreIntegrazionePDWSAddressing(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(this.log==null){
			this.log = LoggerWrapperFactory.getLogger(GestoreIntegrazionePDWSAddressing.class);
		}
		try{
			this.backwardCompatibilityProperties = BackwardCompatibilityProperties.getInstance(true);
			this.utilities = UtilitiesIntegrazioneWSAddressing.getInstance(this.log);
		}catch(Exception e){
			this.log.error("Errore durante l'inizializzazione delle UtilitiesIntegrazioneWSAddressing: "+e.getMessage(),e);
		}
		try{
			this.gestoreIntegrazioneOpenSPCoopV2 = new org.openspcoop2.pdd.core.integrazione.GestoreIntegrazionePDWSAddressing();
		}catch(Exception e){
			this.log.error("Errore durante l'instanziazione del gestoreIntegrazioneOpenSPCoopV2: "+e.getMessage(),e);
		}
	}
	@Override
	public void init(PdDContext pddContext, IProtocolFactory protocolFactory,
			Object... args) {
		super.init(pddContext, protocolFactory, args);
		try{
			if(this.gestoreIntegrazioneOpenSPCoopV2!=null){
				this.gestoreIntegrazioneOpenSPCoopV2.init(this.getPddContext(), this.getProtocolFactory(), this.getArgs());
			}
		}catch(Exception e){
			this.log.error("Errore durante l'instanziazione del gestoreIntegrazioneOpenSPCoopV2: "+e.getMessage(),e);
		}
	}
	
	
	// IN - Request
	
	@Override
	public void readInRequestHeader(HeaderIntegrazione integrazione,
			InRequestPDMessage inRequestPDMessage) throws HeaderIntegrazioneException{
		try{
			if( 
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
			){
				this.utilities.readHeader(inRequestPDMessage.getMessage(), integrazione,
						UtilitiesIntegrazioneWSAddressing.INTERPRETA_COME_ID_APPLICATIVO, 
						this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.readInRequestHeader(integrazione, inRequestPDMessage);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDWSAddressing, "+e.getMessage(),e);
		}
	}

	@Override
	public void deleteInRequestHeader(InRequestPDMessage inRequestPDMessage) throws HeaderIntegrazioneException{
		try{
			if( 
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
			){
				this.utilities.deleteHeader(inRequestPDMessage.getMessage(),this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.deleteInRequestHeader(inRequestPDMessage);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDSoap, "+e.getMessage(),e);
		}
	}
	
	@Override
	public void updateInRequestHeader(InRequestPDMessage inRequestPDMessage,IDServizio idServizio,
			String idMessaggio,String servizioApplicativo,String correlazioneApplicativa) throws HeaderIntegrazioneException{
		try{
			if( 
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
			){
				this.utilities.updateHeader(inRequestPDMessage.getMessage(), 
					inRequestPDMessage.getSoggettoPropeprietarioPortaDelegata(), idServizio, idMessaggio, 
					servizioApplicativo, correlazioneApplicativa, this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());  // namespace
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.updateInRequestHeader(inRequestPDMessage, idServizio, idMessaggio, servizioApplicativo, correlazioneApplicativa);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDWSAddressing, "+e.getMessage(),e);
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
			if( 
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
			){
				this.utilities.readHeader(inResponsePDMessage.getMessage(), integrazione,
					UtilitiesIntegrazioneWSAddressing.INTERPRETA_COME_ID_BUSTA, 
					this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.readInResponseHeader(integrazione, inResponsePDMessage);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDWSAddressing, "+e.getMessage(),e);
		}
	}
	
	@Override
	public void deleteInResponseHeader(InResponsePDMessage inResponsePDMessage) throws HeaderIntegrazioneException{
		try{
			if( 
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
			){
				this.utilities.deleteHeader(inResponsePDMessage.getMessage(),this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.deleteInResponseHeader(inResponsePDMessage);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDSoap, "+e.getMessage(),e);
		}
	}
	
	@Override
	public void updateInResponseHeader(InResponsePDMessage inResponsePDMessage,
			String idMessageRequest,String idMessageResponse,String servizioApplicativo,String correlazioneApplicativa,String riferimentoCorrelazioneApplicativaRichiesta) throws HeaderIntegrazioneException{
		try{
			if( 
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
			){
				this.utilities.updateHeader(inResponsePDMessage.getMessage(),
					inResponsePDMessage.getSoggettoMittente(),
					inResponsePDMessage.getServizio(),
					idMessageRequest,  idMessageResponse,
					servizioApplicativo, correlazioneApplicativa, this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());  // namespace
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.updateInResponseHeader(inResponsePDMessage, idMessageRequest, idMessageResponse, servizioApplicativo, correlazioneApplicativa, riferimentoCorrelazioneApplicativaRichiesta);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDWSAddressing, "+e.getMessage(),e);
		}
	}
	
	
	
	// OUT - Response
	
	@Override
	public void setOutResponseHeader(HeaderIntegrazione integrazione,
			OutResponsePDMessage outResponsePDMessage) throws HeaderIntegrazioneException{
		
		try{
			if( 
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
			){
				
				OpenSPCoop2Message message = outResponsePDMessage.getMessage();
				if(message.getSOAPHeader() == null){
					message.getSOAPPart().getEnvelope().addHeader();
				}
				
				if(integrazione.getBusta()!=null){
					
					HeaderIntegrazioneBusta hBusta = integrazione.getBusta();
						
					if(hBusta.getDestinatario()!=null && hBusta.getServizio()!=null){
						
						// To
						SOAPHeaderElement wsaTO = UtilitiesIntegrazioneWSAddressing.buildWSATo(message,this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(), hBusta.getTipoDestinatario(),hBusta.getDestinatario(), hBusta.getTipoServizio(), hBusta.getServizio());
						message.addHeaderElement(message.getSOAPHeader(), wsaTO);
						
						// Action
						if(hBusta.getAzione()!=null){
							SOAPHeaderElement wsaAction = UtilitiesIntegrazioneWSAddressing.buildWSAAction(message,this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(),hBusta.getTipoDestinatario(),hBusta.getDestinatario(), hBusta.getTipoServizio(), hBusta.getServizio(),hBusta.getAzione());
							message.addHeaderElement(message.getSOAPHeader(), wsaAction);
						}
					}
					
					if(hBusta.getMittente()!=null){
						SOAPHeaderElement wsaFROM = UtilitiesIntegrazioneWSAddressing.buildWSAFrom(message,this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(),integrazione.getServizioApplicativo(),hBusta.getTipoMittente(),hBusta.getMittente());
						message.addHeaderElement(message.getSOAPHeader(), wsaFROM);
					}
						
					if(hBusta.getID()!=null){
						SOAPHeaderElement wsaID = UtilitiesIntegrazioneWSAddressing.buildWSAID(message,this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(),hBusta.getID());
						message.addHeaderElement(message.getSOAPHeader(), wsaID);
					}
					
					if(hBusta.getRiferimentoMessaggio()!=null || hBusta.getIdCollaborazione()!=null){
						String rif = hBusta.getRiferimentoMessaggio();
						if(rif==null){
							rif = hBusta.getIdCollaborazione();
						}
						SOAPHeaderElement wsaRelatesTo = UtilitiesIntegrazioneWSAddressing.buildWSARelatesTo(message,this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(),rif);
						message.addHeaderElement(message.getSOAPHeader(), wsaRelatesTo);
					}
				}
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.setOutResponseHeader(integrazione, outResponsePDMessage);
			}
			
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDWSAddressing, "+e.getMessage(),e);
		}
		
	}
	

}