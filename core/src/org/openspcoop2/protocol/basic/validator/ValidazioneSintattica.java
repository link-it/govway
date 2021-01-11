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

package org.openspcoop2.protocol.basic.validator;

import java.util.Date;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.BasicStateComponentFactory;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.builder.IBustaBuilder;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
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
public class ValidazioneSintattica<BustaRawType> extends BasicStateComponentFactory implements
		org.openspcoop2.protocol.sdk.validator.IValidazioneSintattica<BustaRawType> {

	private IBustaBuilder<BustaRawType> bustaBuilder = null;
	protected Context context;
		
	public ValidazioneSintattica(IProtocolFactory<BustaRawType> factory,IState state) throws ProtocolException{
		super(factory,state);
		this.bustaBuilder = factory.createBustaBuilder(state);
	}

	@Override
	public void setContext(Context context) {
		this.context = context;
	}
	
	@Override
	public ValidazioneSintatticaResult<BustaRawType> validaRichiesta(OpenSPCoop2Message msg, Busta datiBustaLettiURLMappingProperties, ProprietaValidazioneErrori proprietaValidazioneErrori) throws ProtocolException{
		
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
		
		return new ValidazioneSintatticaResult<BustaRawType>(null, null, null, busta, null, null, null, true);
	}
	
	@Override
	public ValidazioneSintatticaResult<BustaRawType> validaRisposta(OpenSPCoop2Message msg, Busta bustaRichiesta, 
			ProprietaValidazioneErrori proprietaValidazioneErrori) throws ProtocolException{
		
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
		bustaRisposta.setID(this.bustaBuilder.newID(new IDSoggetto(bustaRisposta.getTipoMittente(), bustaRisposta.getMittente(), bustaRisposta.getIdentificativoPortaMittente()),
				idSenzaData, RuoloMessaggio.RISPOSTA));
		bustaRisposta.setInoltro(bustaRichiesta.getInoltro(), bustaRichiesta.getInoltroValue());
		bustaRisposta.setCollaborazione(bustaRichiesta.getCollaborazione());
		
		// Se e' presente UNA lista trasmissione la inverto
		if(bustaRichiesta.sizeListaTrasmissioni()==1){
			bustaRisposta.addTrasmissione(bustaRichiesta.getTrasmissione(0).invertiTrasmissione(bustaRichiesta.getTipoOraRegistrazione(), bustaRichiesta.getTipoOraRegistrazioneValue()));
		}
		
		boolean hasFault = false;
		try {
			if(msg!=null && ServiceBinding.SOAP.equals(msg.getServiceBinding())){
				hasFault = msg.castAsSoap().getSOAPBody().hasFault();
			}
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
			ValidatoreErrori validatoreErrori = new ValidatoreErrori(this.protocolFactory,this.state);
			if(bustaRisposta.getProfiloDiCollaborazione().equals(ProfiloDiCollaborazione.ONEWAY))
				if(validatoreErrori.isBustaErrore(bustaRichiesta, msg, proprietaValidazioneErrori)) {
					return new ValidazioneSintatticaResult<BustaRawType>(null, null, null, bustaRisposta, null, null, null, true);
				}
				else {
					return new ValidazioneSintatticaResult<BustaRawType>(null, null, null, null, ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.
							getErroreProcessamento("Analizzato un messaggio fault"), bustaRisposta, null, false);
				}
				
			if(bustaRisposta.getProfiloDiCollaborazione().equals(ProfiloDiCollaborazione.SINCRONO))
				if(validatoreErrori.isBustaErrore(bustaRichiesta, msg, proprietaValidazioneErrori)) {
					return new ValidazioneSintatticaResult<BustaRawType>(null, null, null, bustaRisposta, ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.
							getErroreProcessamento("Analizzato un messaggio fault"), bustaRisposta, null, true);
				}
				else {
					return new ValidazioneSintatticaResult<BustaRawType>(null, null, null, bustaRisposta, null, null, null, true);
				}
		}
		return new ValidazioneSintatticaResult<BustaRawType>(null, null, null, bustaRisposta, null, null, null, true);
		
	}
	
	@Override
	public boolean verifyProtocolPresence(TipoPdD tipoPdD, ProfiloDiCollaborazione profilo, RuoloMessaggio ruoloMessaggio,
			OpenSPCoop2Message msg) throws ProtocolException{
		if(TipoPdD.DELEGATA.equals(tipoPdD)){
			if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)){
				return false;
			}else{
				return !ProfiloDiCollaborazione.ONEWAY.equals(profilo);
			}
		}
		else if(TipoPdD.APPLICATIVA.equals(tipoPdD)){
			if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)){
				return true;
			}else{
				return false;
			}
		}
		else if(TipoPdD.ROUTER.equals(tipoPdD)){
			if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)){
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
	public ValidazioneSintatticaResult<BustaRawType> validazioneFault(OpenSPCoop2Message msg) {
		return null;
	}

	@Override
	public ValidazioneSintatticaResult<BustaRawType> validazioneManifestAttachments(
			OpenSPCoop2Message msg,
			ProprietaManifestAttachments proprietaManifestAttachments) {
		return null;
	}

	@Override
	public BustaRawContent<BustaRawType> getBustaRawContent_senzaControlli(
			OpenSPCoop2Message msg) throws ProtocolException {
		return null;
	}

	@Override
	public Busta getBusta_senzaControlli(OpenSPCoop2Message msg) throws ProtocolException{
		return null;
	}

}
