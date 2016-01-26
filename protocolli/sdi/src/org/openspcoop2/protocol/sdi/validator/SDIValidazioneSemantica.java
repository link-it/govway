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



package org.openspcoop2.protocol.sdi.validator;

import java.util.Vector;

import javax.xml.soap.SOAPElement;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.protocol.basic.validator.ValidazioneSemantica;
import org.openspcoop2.protocol.sdi.config.SDIProperties;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviFile;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviNotifica;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRicezioneFatture;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioTrasmissioneFatture;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.sdk.validator.ValidazioneSemanticaResult;



/**
 * Classe che implementa, in base al protocollo SdI, l'interfaccia {@link org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica}
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SDIValidazioneSemantica extends ValidazioneSemantica {

	/** ValidazioneUtils */
	protected SDIValidazioneUtils validazioneUtils;
	/** Properties */
	protected SDIProperties sdiProperties;
	
	/** Errori di validazione riscontrati sulla busta */
	protected java.util.Vector<Eccezione> erroriValidazione = new Vector<Eccezione>();
	/** Errori di processamento riscontrati sulla busta */
	protected java.util.Vector<Eccezione> erroriProcessamento = new Vector<Eccezione>();
	
	public SDIValidazioneSemantica(IProtocolFactory factory) throws ProtocolException {
		super(factory);
		this.sdiProperties = SDIProperties.getInstance(this.log);
		this.validazioneUtils = new SDIValidazioneUtils(factory);
	}

	@Override
	public ValidazioneSemanticaResult valida(OpenSPCoop2Message msg, Busta busta, 
			IState state, ProprietaValidazione proprietaValidazione, 
			RuoloBusta tipoBusta) throws ProtocolException{
		
		this.valida(msg,busta,tipoBusta);
		
		java.util.Vector<Eccezione> erroriValidazione = null;
		if(this.erroriValidazione.size()>0){
			erroriValidazione = this.erroriValidazione;
		}
		java.util.Vector<Eccezione> erroriProcessamento = null;
		if(this.erroriProcessamento.size()>0){
			erroriValidazione = this.erroriProcessamento;
		}
		ValidazioneSemanticaResult validazioneSemantica = new ValidazioneSemanticaResult(erroriValidazione, erroriProcessamento, null, null, null);
		return validazioneSemantica;
		
	}

	private void valida(OpenSPCoop2Message msg,Busta busta, RuoloBusta tipoBusta) throws ProtocolException{
		try{
			
			boolean isRichiesta = RuoloBusta.RICHIESTA.equals(tipoBusta);
			
			if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE.equals(busta.getServizio())){
				this.validaServizioTrasmissione(msg, busta,isRichiesta,SoapUtils.getNotEmptyFirstChildSOAPElement(msg.getSOAPBody()));
			}
			else if(SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE.equals(busta.getServizio())){
				this.validaServizioRicezione(msg, busta,isRichiesta,SoapUtils.getNotEmptyFirstChildSOAPElement(msg.getSOAPBody()));			
			}
			else if(SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_RICEVI_NOTIFICA.equals(busta.getServizio())){
				this.validaServizioRiceviNotifica(msg, busta,isRichiesta,SoapUtils.getNotEmptyFirstChildSOAPElement(msg.getSOAPBody()));			
			}
			else if(SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE.equals(busta.getServizio())){
				this.validaServizioRiceviFile(msg, busta,isRichiesta,SoapUtils.getNotEmptyFirstChildSOAPElement(msg.getSOAPBody()));			
			}
			
		}catch(Exception e){
			this.erroriProcessamento.add(this.validazioneUtils.newEccezioneProcessamento(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO, 
					e.getMessage(),e));
			return;
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
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.AZIONE_NON_VALIDA));
			return;
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
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.AZIONE_NON_VALIDA));
			return;
		}	
	}
	
	private void validaServizioRiceviNotifica(OpenSPCoop2Message msg, Busta busta, boolean isRichiesta, SOAPElement sdiMessage) throws Exception{
		
		String azione = busta.getAzione();
		
		SDIValidatoreServizioRiceviNotifica validatore = new SDIValidatoreServizioRiceviNotifica(this, msg, isRichiesta,sdiMessage,busta);
		
		if(SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_NOTIFICA_ESITO_AZIONE_NOTIFICA_ESITO.equals(azione)){
			validatore.validaNotificaEsito();
		}
		else{
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.AZIONE_NON_VALIDA));
			return;
		}	
	}
	
	private void validaServizioRiceviFile(OpenSPCoop2Message msg, Busta busta, boolean isRichiesta, SOAPElement sdiMessage) throws Exception{
		
		String azione = busta.getAzione();
		
		SDIValidatoreServizioRiceviFile validatore = new SDIValidatoreServizioRiceviFile(this, msg, isRichiesta,sdiMessage,busta);
		
		if(SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE_AZIONE_RICEVI_FILE.equals(azione)){
			validatore.validaRiceviFile();
		}
		else{
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.AZIONE_NON_VALIDA));
			return;
		}	
	}
}
