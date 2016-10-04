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

package org.openspcoop2.protocol.spcoop.backward_compatibility.integrazione;


import javax.xml.soap.SOAPHeaderElement;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazioneException;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePASoap;
import org.openspcoop2.pdd.core.integrazione.InRequestPAMessage;
import org.openspcoop2.pdd.core.integrazione.InResponsePAMessage;
import org.openspcoop2.pdd.core.integrazione.OutRequestPAMessage;
import org.openspcoop2.pdd.core.integrazione.OutResponsePAMessage;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.spcoop.backward_compatibility.config.BackwardCompatibilityProperties;
import org.openspcoop2.protocol.spcoop.backward_compatibility.config.Costanti;
import org.openspcoop2.protocol.spcoop.backward_compatibility.services.BackwardCompatibilityStartup;
import org.openspcoop2.utils.LoggerWrapperFactory;


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
	private UtilitiesIntegrazione utilities = null;
	
	/** BackwardCompatibilityProperties */
	private BackwardCompatibilityProperties backwardCompatibilityProperties = null;
	
	/** Logger utilizzato per debug. */
	private Logger log = null;

	/** GestoreIntegrazionePA Originale */
	private org.openspcoop2.pdd.core.integrazione.GestoreIntegrazionePASoap gestoreIntegrazioneOpenSPCoopV2 = null;
		
	public GestoreIntegrazionePASoap(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(this.log==null){
			this.log = LoggerWrapperFactory.getLogger(GestoreIntegrazionePASoap.class);
		}
		try{
			this.backwardCompatibilityProperties = BackwardCompatibilityProperties.getInstance(true);
			this.utilities = UtilitiesIntegrazione.getInstance(this.log, true);
		}catch(Exception e){
			this.log.error("Errore durante l'inizializzazione delle UtilitiesIntegrazione: "+e.getMessage(),e);
		}
		try{
			this.gestoreIntegrazioneOpenSPCoopV2 = new org.openspcoop2.pdd.core.integrazione.GestoreIntegrazionePASoap();
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
			InRequestPAMessage inRequestPAMessage) throws HeaderIntegrazioneException{
		try{
			if(
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
				){
				this.utilities.readHeader(inRequestPAMessage.getMessage(), integrazione, this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.readInRequestHeader(integrazione, inRequestPAMessage);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
	
	@Override
	public void deleteInRequestHeader(InRequestPAMessage inRequestPAMessage) throws HeaderIntegrazioneException{
		try{
			if(
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
				){
				this.utilities.deleteHeader(inRequestPAMessage.getMessage(),this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.deleteInRequestHeader(inRequestPAMessage);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
	
	@Override
	public void updateInRequestHeader(InRequestPAMessage inRequestPAMessage,
			String idMessaggio,String servizioApplicativo,String correlazioneApplicativa) throws HeaderIntegrazioneException{
		try{
			if(
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
				){
				this.utilities.updateHeader(inRequestPAMessage.getMessage(), 
						inRequestPAMessage.getSoggettoMittente(), 
						inRequestPAMessage.getServizio(), 
						idMessaggio, 
						servizioApplicativo, correlazioneApplicativa, null,
						this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(),  // actor
						this.backwardCompatibilityProperties.getHeaderSoapNameIntegrazione(),  // header name 
						this.backwardCompatibilityProperties.getHeaderSoapPrefixIntegrazione(),  // prefix
						this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());  // namespace
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.updateInRequestHeader(inRequestPAMessage, idMessaggio, servizioApplicativo, correlazioneApplicativa);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
	
	
	// OUT - Request
	
	@Override
	public void setOutRequestHeader(HeaderIntegrazione integrazione,
			OutRequestPAMessage outRequestPAMessage) throws HeaderIntegrazioneException{
		try{
			if(
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
				){
				SOAPHeaderElement header = this.utilities.buildHeader(integrazione,
						this.backwardCompatibilityProperties.getHeaderSoapNameIntegrazione(), // header name 
						this.backwardCompatibilityProperties.getHeaderSoapPrefixIntegrazione(), // prefix
						this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(), // namespace
						this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(), // actor
						outRequestPAMessage.getMessage());
				
				if(outRequestPAMessage.getMessage().getSOAPHeader() == null){
					outRequestPAMessage.getMessage().getSOAPPart().getEnvelope().addHeader();
				}
				//outRequestPAMessage.getMessage().getSOAPHeader().addChildElement(header);
				outRequestPAMessage.getMessage().addHeaderElement(outRequestPAMessage.getMessage().getSOAPHeader(), header);
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.setOutRequestHeader(integrazione, outRequestPAMessage);
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
			if(
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
				){
				this.utilities.readHeader(inResponsePAMessage.getMessage(), integrazione, this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.readInResponseHeader(integrazione, inResponsePAMessage);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
	
	@Override
	public void deleteInResponseHeader(InResponsePAMessage inResponsePAMessage) throws HeaderIntegrazioneException{
		try{
			if(
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
				){
				this.utilities.deleteHeader(inResponsePAMessage.getMessage(),this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.deleteInResponseHeader(inResponsePAMessage);
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
	}
		
	@Override
	public void updateInResponseHeader(InResponsePAMessage inResponsePAMessage,
			String idMessaggioRichiesta,String idMessaggioRisposta,String servizioApplicativo,String correlazioneApplicativa,String riferimentoCorrelazioneApplicativaRichiesta) throws HeaderIntegrazioneException{
		try{
			if(
					BackwardCompatibilityStartup.initialized &&
					(
						(!this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa())
						||
						(this.backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaApplicativa() && this.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
					)
				){
				this.utilities.updateHeader(inResponsePAMessage.getMessage(), 
						inResponsePAMessage.getSoggettoMittente(), 
						inResponsePAMessage.getServizio(),
						idMessaggioRichiesta, idMessaggioRisposta,
						servizioApplicativo, correlazioneApplicativa, riferimentoCorrelazioneApplicativaRichiesta, 
						this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione(),  // actor
						this.backwardCompatibilityProperties.getHeaderSoapNameIntegrazione(),  // header name 
						this.backwardCompatibilityProperties.getHeaderSoapPrefixIntegrazione(),  // prefix
						this.backwardCompatibilityProperties.getHeaderSoapActorIntegrazione());  // namespace
			}
			else{
				this.gestoreIntegrazioneOpenSPCoopV2.updateInResponseHeader(inResponsePAMessage, idMessaggioRichiesta, idMessaggioRisposta, servizioApplicativo, correlazioneApplicativa, riferimentoCorrelazioneApplicativaRichiesta);
			}
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