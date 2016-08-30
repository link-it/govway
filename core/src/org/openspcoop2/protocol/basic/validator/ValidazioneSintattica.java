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

package org.openspcoop2.protocol.basic.validator;

import java.util.Date;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.builder.IBustaBuilder;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;
import org.openspcoop2.protocol.sdk.validator.ValidazioneSintatticaResult;
import org.openspcoop2.utils.date.DateManager;

/**
 * ValidazioneSintattica
 * 
 * @author Nardi Lorenzo (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ValidazioneSintattica implements
		org.openspcoop2.protocol.sdk.validator.IValidazioneSintattica {

	protected IProtocolFactory protocolFactory;
	private IBustaBuilder bustaBuilder = null;
	protected Logger log;
		
	public ValidazioneSintattica(IProtocolFactory factory) throws ProtocolException{
		this.log = factory.getLogger();
		this.protocolFactory = factory;
		this.bustaBuilder = this.protocolFactory.createBustaBuilder();
	}

	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}

	
	@Override
	public ValidazioneSintatticaResult validaRichiesta(IState state, OpenSPCoop2Message msg, Busta datiBustaLettiURLMappingProperties, ProprietaValidazioneErrori proprietaValidazioneErrori) throws ProtocolException{
		
		Busta busta = null;
		if(datiBustaLettiURLMappingProperties!=null)
			busta = datiBustaLettiURLMappingProperties; 
		else
			busta = new Busta(this.protocolFactory.getProtocol());
		
		Date oraRegistrazione = DateManager.getDate();
		busta.setOraRegistrazione(oraRegistrazione);
		if(busta.getTipoOraRegistrazione()==null){
			busta.setTipoOraRegistrazione(TipoOraRegistrazione.LOCALE,this.protocolFactory.createTraduttore().toString(busta.getTipoOraRegistrazione()));
		}
		if(busta.sizeListaTrasmissioni()>0){
			for (Trasmissione trasmissione : busta.getListaTrasmissioni()) {
				if(trasmissione.getOraRegistrazione()==null){
					trasmissione.setOraRegistrazione(oraRegistrazione);
				}
				if(trasmissione.getTempo()==null){
					trasmissione.setTempo(TipoOraRegistrazione.LOCALE,this.protocolFactory.createTraduttore().toString(busta.getTipoOraRegistrazione()));
				}
			}
		}
		
		return new ValidazioneSintatticaResult(null, null, null, busta, null, null, null, true);
	}
	
	@Override
	public ValidazioneSintatticaResult validaRisposta(IState state, OpenSPCoop2Message msg, Busta bustaRichiesta, ProprietaValidazioneErrori proprietaValidazioneErrori) throws ProtocolException{
		
		Busta bustaRisposta = bustaRichiesta.invertiBusta(bustaRichiesta.getTipoOraRegistrazione(), bustaRichiesta.getTipoOraRegistrazioneValue());
		bustaRisposta.setRiferimentoMessaggio(bustaRichiesta.getID());
		bustaRisposta.setProfiloDiCollaborazione(bustaRichiesta.getProfiloDiCollaborazione(), bustaRichiesta.getProfiloDiCollaborazioneValue());
		bustaRisposta.setTipoServizio(bustaRichiesta.getTipoServizio());
		bustaRisposta.setServizio(bustaRichiesta.getServizio());
		bustaRisposta.setVersioneServizio(bustaRichiesta.getVersioneServizio());
		bustaRisposta.setAzione(bustaRichiesta.getAzione());
		// estraggo l'identificativo di transazione dalla richiesta, eliminandone la data iniziale se il protocollo la supporta
		String idSenzaData = null;
		try{
			Date d = this.bustaBuilder.extractDateFromID(bustaRichiesta.getID());
			if(d!=null){
				String [] split = bustaRichiesta.getID().split("-");
				idSenzaData = bustaRichiesta.getID().substring((split[0].length()+1));
			}
			else{
				idSenzaData = bustaRichiesta.getID();
			}
		}catch(Exception e){
			throw new ProtocolException("Identificativo di richiesta in un formato errato, estrazione della data non riuscita: "+e.getMessage(),e);
		}
		bustaRisposta.setID(this.bustaBuilder.newID(state, new IDSoggetto(bustaRisposta.getTipoMittente(), bustaRisposta.getMittente(), bustaRisposta.getIdentificativoPortaMittente()),
				idSenzaData, false));
		bustaRisposta.setInoltro(bustaRichiesta.getInoltro(), bustaRichiesta.getInoltroValue());
		
		// Se e' presente UNA lista trasmissione la inverto
		if(bustaRichiesta.sizeListaTrasmissioni()==1){
			bustaRisposta.addTrasmissione(bustaRichiesta.getTrasmissione(0).invertiTrasmissione(bustaRichiesta.getTipoOraRegistrazione(), bustaRichiesta.getTipoOraRegistrazioneValue()));
		}
		
		boolean hasFault = false;
		try {
			hasFault = msg.getSOAPBody().hasFault();
		} catch (Exception e) {
			throw new ProtocolException(e); 
		}
		
		/*
		 * ONEWAY:
		 * Se e' un errore di cooperazione, ovvero isBustaErrore > e' una busta
		 * Se non e' un errore di cooperazione, non e' imbustato
		 * 
		 * SINCRONO:
		 * E' sempre imbustato 
		 */
		
		if(hasFault) {
			ValidatoreErrori validatoreErrori = new ValidatoreErrori(this.protocolFactory);
			if(bustaRisposta.getProfiloDiCollaborazione().equals(ProfiloDiCollaborazione.ONEWAY))
				if(validatoreErrori.isBustaErrore(bustaRichiesta, msg, proprietaValidazioneErrori)) {
					return new ValidazioneSintatticaResult(null, null, null, bustaRisposta, null, null, null, true);
				}
				else {
					return new ValidazioneSintatticaResult(null, null, null, null, ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.
							getErroreProcessamento("Analizzato un messaggio fault"), bustaRisposta, null, false);
				}
				
			if(bustaRisposta.getProfiloDiCollaborazione().equals(ProfiloDiCollaborazione.SINCRONO))
				if(validatoreErrori.isBustaErrore(bustaRichiesta, msg, proprietaValidazioneErrori)) {
					return new ValidazioneSintatticaResult(null, null, null, bustaRisposta, ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.
							getErroreProcessamento("Analizzato un messaggio fault"), bustaRisposta, null, true);
				}
				else {
					return new ValidazioneSintatticaResult(null, null, null, bustaRisposta, null, null, null, true);
				}
		}
		return new ValidazioneSintatticaResult(null, null, null, bustaRisposta, null, null, null, true);
		
	}
	
	@Override
	public boolean verifyProtocolPresence(TipoPdD tipoPdD, ProfiloDiCollaborazione profilo, boolean isRichiesta,
			OpenSPCoop2Message msg) throws ProtocolException{
		if(TipoPdD.DELEGATA.equals(tipoPdD)){
			if(isRichiesta){
				return false;
			}else{
				return !ProfiloDiCollaborazione.ONEWAY.equals(profilo);
			}
		}
		else if(TipoPdD.APPLICATIVA.equals(tipoPdD)){
			if(isRichiesta){
				return true;
			}else{
				return false;
			}
		}
		else if(TipoPdD.ROUTER.equals(tipoPdD)){
			if(isRichiesta){
				return true;
			}else{
				return !ProfiloDiCollaborazione.ONEWAY.equals(profilo);
			}
		}
		else{
			throw new ProtocolException("TipoPdD ["+tipoPdD+"] non gestito");
		}
	}

	@Override
	public ValidazioneSintatticaResult validazioneFault(SOAPBody body) {
		return null;
	}

	@Override
	public ValidazioneSintatticaResult validazioneManifestAttachments(
			OpenSPCoop2Message msg,
			ProprietaManifestAttachments proprietaManifestAttachments) {
		return null;
	}

	@Override
	public SOAPElement getHeaderProtocollo_senzaControlli(
			OpenSPCoop2Message msg) throws ProtocolException {
		return null;
	}

	@Override
	public SOAPElement getHeaderProtocollo(Busta busta) {
		// TODO Auto-generated method stub
		return null;
	}

}
