/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.spcoop.backward_compatibility.integrazione;

import javax.xml.soap.SOAPHeaderElement;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
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
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreIntegrazionePDSoap extends AbstractCore implements IGestoreIntegrazionePDSoap{

	/** Utility per l'integrazione */
	private UtilitiesIntegrazione utilities = null;
	
	/** BackwardCompatibilityProperties */
	private BackwardCompatibilityProperties backwardCompatibilityProperties = null;
	
	/** GestoreIntegrazionePD Originale */
	private org.openspcoop2.pdd.core.integrazione.GestoreIntegrazionePDSoap gestoreIntegrazioneOpenSPCoopV2 = null;
	
	/** Logger utilizzato per debug. */
	private Logger log = null;

	
	public GestoreIntegrazionePDSoap(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(this.log==null){
			this.log = LoggerWrapperFactory.getLogger(GestoreIntegrazionePDSoap.class);
		}
		try{
			this.backwardCompatibilityProperties = BackwardCompatibilityProperties.getInstance(true);
			this.utilities = UtilitiesIntegrazione.getInstance(this.log,true);
		}catch(Exception e){
			this.log.error("Errore durante l'inizializzazione delle UtilitiesIntegrazione: "+e.getMessage(),e);
		}
		
		try{
			this.gestoreIntegrazioneOpenSPCoopV2 = new org.openspcoop2.pdd.core.integrazione.GestoreIntegrazionePDSoap();
		}catch(Exception e){
			this.log.error("Errore durante l'instanziazione del gestoreIntegrazioneOpenSPCoopV2: "+e.getMessage(),e);
		}
	}
	@Override
	public void init(PdDContext pddContext, IProtocolFactory<?> protocolFactory,
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
				this.utilities.readHeader(inRequestPDMessage.getMessage().castAsSoap(), integrazione, this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.readInRequestHeader(integrazione, inRequestPDMessage);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDSoap, "+e.getMessage(),e);
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
				this.utilities.deleteHeader(inRequestPDMessage.getMessage().castAsSoap(),this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.deleteInRequestHeader(inRequestPDMessage);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDSoap, "+e.getMessage(),e);
		}
	}

	
	@Override
	public void updateInRequestHeader(InRequestPDMessage inRequestPDMessage,
			IDServizio idServizio,
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
				this.utilities.updateHeader(inRequestPDMessage.getMessage().castAsSoap(), 
						inRequestPDMessage.getSoggettoPropeprietarioPortaDelegata(), idServizio, idMessaggio, 
						servizioApplicativo, correlazioneApplicativa, null,
						org.openspcoop2.pdd.core.integrazione.UtilitiesIntegrazione.getIdTransazione(this.getPddContext()),
						this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(),  // actor
						this.backwardCompatibilityProperties.getHeaderSoapNameIntegrazione(),  // header name 
						this.backwardCompatibilityProperties.getHeaderSoapPrefixIntegrazione(),  // prefix
						this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());  // namespace
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.updateInRequestHeader(inRequestPDMessage, idServizio, idMessaggio, servizioApplicativo, correlazioneApplicativa);
			}
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
			if( 
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
			){	
				this.utilities.readHeader(inResponsePDMessage.getMessage().castAsSoap(), integrazione, this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.readInResponseHeader(integrazione, inResponsePDMessage);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDSoap, "+e.getMessage(),e);
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
				this.utilities.deleteHeader(inResponsePDMessage.getMessage().castAsSoap(),this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
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
				this.utilities.updateHeader(inResponsePDMessage.getMessage().castAsSoap(), 
						inResponsePDMessage.getSoggettoMittente(),
						inResponsePDMessage.getServizio(), idMessageRequest, idMessageResponse, 
						servizioApplicativo, correlazioneApplicativa, riferimentoCorrelazioneApplicativaRichiesta,
						this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(),  // actor
						this.backwardCompatibilityProperties.getHeaderSoapNameIntegrazione(),  // header name 
						this.backwardCompatibilityProperties.getHeaderSoapPrefixIntegrazione(),  // prefix
						this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());  // namespace
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.updateInResponseHeader(inResponsePDMessage, idMessageRequest, idMessageResponse, servizioApplicativo, correlazioneApplicativa, riferimentoCorrelazioneApplicativaRichiesta);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDSoap, "+e.getMessage(),e);
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
				SOAPHeaderElement header = this.utilities.buildHeader(integrazione, 
						this.backwardCompatibilityProperties.getHeaderSoapNameIntegrazione(), // header name 
						this.backwardCompatibilityProperties.getHeaderSoapPrefixIntegrazione(), // prefix
						this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(), // namespace
						this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(), // actor
						outResponsePDMessage.getMessage().castAsSoap());
				
				OpenSPCoop2Message msgParam = outResponsePDMessage.getMessage();
				OpenSPCoop2SoapMessage msg = null;
				if(msgParam!=null){
					msg = msgParam.castAsSoap();
				}
				
				//System.out.println((new org.openspcoop.dao.message.OpenSPCoopMessageFactory().createMessage().getAsString(header)));
				if(msg.getSOAPHeader() == null){
					msg.getSOAPPart().getEnvelope().addHeader();
				}
				msg.addHeaderElement(msg.getSOAPHeader(), header);
						
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.setOutResponseHeader(integrazione, outResponsePDMessage);
			}			
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDSoap, "+e.getMessage(),e);
		}
		
	}
}
