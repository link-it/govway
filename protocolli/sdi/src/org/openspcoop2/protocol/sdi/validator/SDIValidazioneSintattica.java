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



package org.openspcoop2.protocol.sdi.validator;


import java.util.Vector;

import javax.xml.soap.SOAPElement;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.protocol.basic.validator.ValidazioneSintattica;
import org.openspcoop2.protocol.sdi.config.SDIProperties;
import org.openspcoop2.protocol.sdi.constants.SDICostanti;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviFile;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviNotifica;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRicezioneFatture;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioTrasmissioneFatture;
import org.openspcoop2.protocol.sdi.utils.SDIUtils;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;
import org.openspcoop2.protocol.sdk.validator.ValidazioneSintatticaResult;

/**
 * Classe che implementa, in base al protocollo SdI, l'interfaccia {@link org.openspcoop2.protocol.sdk.validator.IValidazioneSintattica}
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDIValidazioneSintattica extends ValidazioneSintattica{

	/** ValidazioneUtils */
	protected SDIValidazioneUtils validazioneUtils;
	/** Properties */
	protected SDIProperties sdiProperties;
	/** Config */
	protected IProtocolConfiguration protocolConfiguration;
	
	/** Errori di validazione riscontrati sulla busta */
	protected java.util.Vector<Eccezione> erroriValidazione = new Vector<Eccezione>();
	/** Errori di processamento riscontrati sulla busta */
	protected java.util.Vector<Eccezione> erroriProcessamento = new Vector<Eccezione>();
	/** Eventuale codice di errore avvenuto durante il processo di validazione  */
	private CodiceErroreCooperazione codiceErrore;
	/** Eventuale messaggio di errore avvenuto durante il processo di validazione */
	private String msgErrore;
	/** SOAPElement senza il contenuto (SoloProtocolloSDI) */
	protected SOAPElement headerElement;
	
	public SDIValidazioneSintattica(IProtocolFactory factory) throws ProtocolException {
		super(factory);
		this.validazioneUtils = new SDIValidazioneUtils(factory);
		this.sdiProperties = SDIProperties.getInstance(this.log);
		this.protocolConfiguration = factory.createProtocolConfiguration();
	}

	
	@Override
	public SOAPElement getHeaderProtocollo_senzaControlli(
			OpenSPCoop2Message msg) throws ProtocolException {
		try{
			return SDIUtils.readHeader(msg);
		}catch(Exception e){
			this.log.debug("getHeaderProtocollo_senzaControlli error: "+e.getMessage(),e);
			return null;
		}
	}
	

	@Override
	public ValidazioneSintatticaResult validaRichiesta(IState state, OpenSPCoop2Message msg, Busta datiBustaLettiURLMappingProperties, ProprietaValidazioneErrori proprietaValidazioneErrori) throws ProtocolException{
		
		ValidazioneSintatticaResult basicResult = super.validaRichiesta(state, msg, datiBustaLettiURLMappingProperties, proprietaValidazioneErrori);
		
		this.headerElement = this.getHeaderProtocollo_senzaControlli(msg);
		
		boolean isValido = this.valida(msg,basicResult.getBusta(), true,null);
		
		ErroreCooperazione errore = null;
		if(this.msgErrore!=null && this.codiceErrore!=null){
			errore = new ErroreCooperazione(this.msgErrore, this.codiceErrore);
		}
		java.util.Vector<Eccezione> erroriValidazione = null;
		if(this.erroriValidazione.size()>0){
			erroriValidazione = this.erroriValidazione;
		}
		java.util.Vector<Eccezione> erroriProcessamento = null;
		if(this.erroriProcessamento.size()>0){
			erroriValidazione = this.erroriProcessamento;
		}
		ValidazioneSintatticaResult result = new ValidazioneSintatticaResult(erroriValidazione, erroriProcessamento, null, 
				basicResult.getBusta(), errore, null, this.headerElement, isValido);
		return result;
		
	}
	
	@Override
	public ValidazioneSintatticaResult validaRisposta(IState state, OpenSPCoop2Message msg, Busta bustaRichiesta, ProprietaValidazioneErrori proprietaValidazioneErrori) throws ProtocolException{
		ValidazioneSintatticaResult basicResult = super.validaRisposta(state, msg, bustaRichiesta, proprietaValidazioneErrori);
		
		boolean hasFault = false;
		try {
			hasFault = msg.getSOAPBody().hasFault();
		} catch (Exception e) {
			throw new ProtocolException(e); 
		}
		if(hasFault){
			return basicResult;
		}
		
		this.headerElement = this.getHeaderProtocollo_senzaControlli(msg);
		
		boolean isValido = this.valida(msg,basicResult.getBusta(),false,bustaRichiesta);
		
		ErroreCooperazione errore = null;
		if(this.msgErrore!=null && this.codiceErrore!=null){
			errore = new ErroreCooperazione(this.msgErrore, this.codiceErrore);
		}
		java.util.Vector<Eccezione> erroriValidazione = null;
		if(this.erroriValidazione.size()>0){
			erroriValidazione = this.erroriValidazione;
		}
		java.util.Vector<Eccezione> erroriProcessamento = null;
		if(this.erroriProcessamento.size()>0){
			erroriValidazione = this.erroriProcessamento;
		}
		ValidazioneSintatticaResult result = new ValidazioneSintatticaResult(erroriValidazione, erroriProcessamento, null, 
				basicResult.getBusta(), errore, null, this.headerElement, isValido);
		return result;
	}
		
	@Override
	public boolean verifyProtocolPresence(TipoPdD tipoPdD, ProfiloDiCollaborazione profilo, boolean isRichiesta,
			OpenSPCoop2Message msg) throws ProtocolException{
		try{
			if(msg==null){
				return false;
			}
			if(msg.getSOAPBody()==null){
				return false;
			}
			SOAPElement child = SoapUtils.getNotEmptyFirstChildSOAPElement(msg.getSOAPBody());
			if(child==null){
				return false;
			}
			String namespace = child.getNamespaceURI();
			if(namespace==null){
				return false;
			}
			boolean verify = (SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE_NAMESPACE.equals(namespace)) ||
					(SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_RICEVI_NOTIFICA_NAMESPACE.equals(namespace)) ||
					(SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_NAMESPACE.equals(namespace)) ||
					(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_NAMESPACE.equals(namespace));
			if(!verify){
				// per sondaPdD
				if(!isRichiesta && TipoPdD.DELEGATA.equals(tipoPdD)){
					if(this.sdiProperties.getNamespaceWhiteList().contains(namespace)){
						return true;
					}
					else{
						return false;
					}
				}else{
					return false;
				}
			}
			else{
				return true;
			}
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}

	
	private boolean valida(OpenSPCoop2Message msg, Busta busta, boolean isRichiesta, Busta bustaRichiesta){
		try{
			this._valida(msg, busta, isRichiesta);
			
			// riporto le properties lette nella richiesta
			if(bustaRichiesta!=null && busta.sizeProperties()>0){
				String [] propertiesNames = busta.getPropertiesNames();
				if(propertiesNames!=null){
					for (int i = 0; i < propertiesNames.length; i++) {
						String key = propertiesNames[i];
						String value = busta.getProperty(key);
						if(key!=null && value!=null){
							bustaRichiesta.addProperty(key, value);
						}
					}
				}
			}
			
			// riporto eventuale nome file nella busta di risposta
			if(SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE.equals(busta.getServizio())){
				if(bustaRichiesta!=null && bustaRichiesta.getProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE)!=null){
					busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE, bustaRichiesta.getProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE));
				}
			}
			
			return true;
		}catch(Exception e) {
			this.msgErrore =  "[ErroreInterno]: "+e.getMessage();
			this.log.error(this.msgErrore,e);
			this.codiceErrore = CodiceErroreCooperazione.FORMATO_NON_CORRETTO;
			return false;
		}
	}
	
	private void _valida(OpenSPCoop2Message msg, Busta busta, boolean isRichiesta) throws Exception{
		
		if(msg==null){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_NON_CORRETTO, 
					"Messaggio non presente"));
			return;
		}
		if(msg.getSOAPBody()==null){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_PRESENTE));
			return;
		}
		Vector<SOAPElement> soapBodyChilds = SoapUtils.getNotEmptyChildSOAPElement(msg.getSOAPBody());
		if(soapBodyChilds==null || soapBodyChilds.size()<=0){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_PRESENTE));
			return;			
		}
		if(soapBodyChilds.size()>1){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					"Sono presenti più elementi ("+soapBodyChilds.size()+") all'interno del SoapBody"));
			return;			
		}
		SOAPElement sdiMessage = soapBodyChilds.get(0);
		if(sdiMessage==null){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_PRESENTE));
			return;
		}
		
		if(busta==null){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_PRESENTE, 
					"Informazioni di protocollo non presenti"));
			return;
		}
		
		// mittente
		if(busta.getTipoMittente()==null){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.TIPO_MITTENTE_NON_PRESENTE));
			return;
		}
		if(this.protocolConfiguration.getTipiSoggetti().contains(busta.getTipoMittente())==false){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.TIPO_MITTENTE_SCONOSCIUTO));
			return;			
		}
		if(busta.getTipoMittente().equals(this.sdiProperties.getTipoSoggettoSDI())==false){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.TIPO_MITTENTE_NON_VALIDO));
			return;
		}
		if(busta.getMittente()==null){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_PRESENTE));
			return;
		}
		if(busta.getMittente().equals(this.sdiProperties.getNomeSoggettoSDI())==false){
			boolean whiteList = false;
			if(busta.getMittente()!=null && this.sdiProperties.getSoggettiWhiteList().contains(busta.getMittente())){
				this.log.debug("Mittente ["+busta.getMittente()+"] in white list");
				whiteList = true;
			}
			if(!whiteList){
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_VALIDO));
				return;
			}
		}
		
		// destinatario
		if(busta.getTipoDestinatario()==null){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.TIPO_DESTINATARIO_NON_PRESENTE));
			return;
		}
		if(this.protocolConfiguration.getTipiSoggetti().contains(busta.getTipoDestinatario())==false){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.TIPO_DESTINATARIO_SCONOSCIUTO));
			return;			
		}
		if(busta.getDestinatario()==null){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.DESTINATARIO_NON_PRESENTE));
			return;
		}
		
		// azione
		if(busta.getAzione()==null){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.AZIONE_NON_PRESENTE));
			return;
		}
		
		// servizio
		if(busta.getServizio()==null){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SERVIZIO_NON_PRESENTE));
			return;
		}	
		if(this.protocolConfiguration.getTipiServizi().contains(busta.getTipoServizio())==false){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.TIPO_SERVIZIO_SCONOSCIUTO));
			return;			
		}
		if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE.equals(busta.getServizio())){
			this.validaServizioTrasmissione(msg, busta,isRichiesta,sdiMessage);
		}
		else if(SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE.equals(busta.getServizio())){
			this.validaServizioRicezione(msg, busta,isRichiesta,sdiMessage);			
		}
		else if(SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_RICEVI_NOTIFICA.equals(busta.getServizio())){
			this.validaServizioRiceviNotifica(msg, busta,isRichiesta,sdiMessage);			
		}
		else if(SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE.equals(busta.getServizio())){
			this.validaServizioRiceviFile(msg, busta,isRichiesta,sdiMessage);			
		}
		else{
			boolean whiteList = false;
			if(busta.getServizio()!=null && this.sdiProperties.getServiziWhiteList().contains(busta.getServizio())){
				this.log.debug("Servizio ["+busta.getServizio()+"] in white list");
				whiteList = true;
			}
			if(!whiteList){
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SERVIZIO_NON_VALIDO));
				return;
			}
		}
			
	}
	
	private void validaServizioTrasmissione(OpenSPCoop2Message msg, Busta busta, boolean isRichiesta,SOAPElement sdiMessage) throws Exception{
		
		String azione = busta.getAzione();
		
		SDIValidatoreServizioTrasmissioneFatture validatore = new SDIValidatoreServizioTrasmissioneFatture(this,msg,isRichiesta,sdiMessage,busta);
		
		if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_RICEVUTA_CONSEGNA.equals(azione)){
			validatore.validaRicevutaConsegna();
		}
		else if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_NOTIFICA_MANCATA_CONSEGNA.equals(azione)){
			validatore.validaNotificaMancataConsegna();
		}
		else if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_NOTIFICA_SCARTO.equals(azione)){
			validatore.validaNotificaScarto();
		}
		else if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_NOTIFICA_ESITO.equals(azione)){
			validatore.validaNotificaEsito();
		}
		else if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_NOTIFICA_DECORRENZA_TERMINI.equals(azione)){
			validatore.validaNotificaDecorrenzaTermini();
		}
		else if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_ATTESTAZIONE_TRASMISSIONE_FATTURA.equals(azione)){
			validatore.validaAttestazioneTrasmissioneFattura();
		}
		else{
			boolean whiteList = false;
			if(busta.getAzione()!=null && this.sdiProperties.getServiziWhiteList().contains(busta.getAzione())){
				this.log.debug("Azione ["+busta.getAzione()+"] in white list");
				whiteList = true;
			}
			if(!whiteList){
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.AZIONE_NON_VALIDA));
				return;
			}
		}
	}
		
	private void validaServizioRicezione(OpenSPCoop2Message msg, Busta busta, boolean isRichiesta, SOAPElement sdiMessage) throws Exception{
		
		String azione = busta.getAzione();
		
		SDIValidatoreServizioRicezioneFatture validatore = new SDIValidatoreServizioRicezioneFatture(this, msg, isRichiesta,sdiMessage,busta);
		
		if(SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_AZIONE_RICEVI_FATTURE.equals(azione)){
			validatore.validaRiceviFatture();
		}
		else if(SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_AZIONE_NOTIFICA_DECORRENZA_TERMINI.equals(azione)){
			validatore.validaNotificaDecorrenzaTermini();	
		}
		else{
			boolean whiteList = false;
			if(busta.getAzione()!=null && this.sdiProperties.getServiziWhiteList().contains(busta.getAzione())){
				this.log.debug("Azione ["+busta.getAzione()+"] in white list");
				whiteList = true;
			}
			if(!whiteList){
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.AZIONE_NON_VALIDA));
				return;
			}
		}	
	}
	
	private void validaServizioRiceviNotifica(OpenSPCoop2Message msg, Busta busta, boolean isRichiesta, SOAPElement sdiMessage) throws Exception{
		
		String azione = busta.getAzione();
		
		SDIValidatoreServizioRiceviNotifica validatore = new SDIValidatoreServizioRiceviNotifica(this, msg, isRichiesta,sdiMessage,busta);
		
		if(SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_NOTIFICA_ESITO_AZIONE_NOTIFICA_ESITO.equals(azione)){
			validatore.validaNotificaEsito();
		}
		else{
			boolean whiteList = false;
			if(busta.getAzione()!=null && this.sdiProperties.getServiziWhiteList().contains(busta.getAzione())){
				this.log.debug("Azione ["+busta.getAzione()+"] in white list");
				whiteList = true;
			}
			if(!whiteList){
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.AZIONE_NON_VALIDA));
				return;
			}
		}	
	}
	
	private void validaServizioRiceviFile(OpenSPCoop2Message msg, Busta busta, boolean isRichiesta, SOAPElement sdiMessage) throws Exception{
		
		String azione = busta.getAzione();
		
		SDIValidatoreServizioRiceviFile validatore = new SDIValidatoreServizioRiceviFile(this, msg, isRichiesta,sdiMessage,busta);
		
		if(SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE_AZIONE_RICEVI_FILE.equals(azione)){
			validatore.validaRiceviFile();
		}
		else{
			boolean whiteList = false;
			if(busta.getAzione()!=null && this.sdiProperties.getServiziWhiteList().contains(busta.getAzione())){
				this.log.debug("Azione ["+busta.getAzione()+"] in white list");
				whiteList = true;
			}
			if(!whiteList){
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.AZIONE_NON_VALIDA));
				return;
			}
		}	
	}
}
