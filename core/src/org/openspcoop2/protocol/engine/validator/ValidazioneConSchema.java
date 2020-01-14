/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.engine.validator;


import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.IState;
import org.slf4j.Logger;

/**
 * Classe utilizzata per effettuare una validazione con schema xsd.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ValidazioneConSchema  {

	/** Logger utilizzato per debug. */
	//private Logger log = null;


	/** Errori di validazione riscontrati sulla busta */
	private java.util.List<Eccezione> erroriValidazione;
	/** Errori di processamento riscontrati sulla busta */
	private java.util.List<Eccezione> erroriProcessamento;

	/** Messaggio intero */
	private OpenSPCoop2Message message;
	
	/** IsErrore */
	private boolean isErroreProcessamento = false;
	private boolean isErroreIntestazione = false;
	
	private boolean validazioneManifestAttachments = false;
	
	private IProtocolFactory<?> protocolFactory;
	
	private IState state;
	
	public ValidazioneConSchema(OpenSPCoop2Message message, boolean isErroreProcessamento, boolean isErroreIntestazione, boolean validazioneManifestAttachments, 
			IProtocolFactory<?> protocolFactory,IState state){
		this(message,isErroreProcessamento,isErroreIntestazione,validazioneManifestAttachments,Configurazione.getLibraryLog(), protocolFactory, state);
	}
	public ValidazioneConSchema(OpenSPCoop2Message message, boolean isErroreProcessamento, boolean isErroreIntestazione, boolean validazioneManifestAttachments,
			Logger aLog, IProtocolFactory<?> protocolFactory,IState state){
		this.message = message;
		this.isErroreProcessamento = isErroreProcessamento;
		this.isErroreIntestazione = isErroreIntestazione;
		this.validazioneManifestAttachments = validazioneManifestAttachments;
		this.protocolFactory = protocolFactory;
		this.state = state;
	}

	public IProtocolFactory<?> getProtocolFactory(){
		return this.protocolFactory;
	}

	/**
	 * Ritorna una lista contenente eventuali eccezioni di validazione riscontrate nella busta.   
	 *
	 * @return Eccezioni riscontrate nella busta.
	 * 
	 */
	public java.util.List<Eccezione> getEccezioniValidazione(){
		return this.erroriValidazione;
	}
	/**
	 * Ritorna una lista contenente eventuali eccezioni di processamento riscontrate nella busta.   
	 *
	 * @return Eccezioni riscontrate nella busta.
	 * 
	 */
	public java.util.List<Eccezione> getEccezioniProcessamento(){
		return this.erroriProcessamento;
	}

	/**
	 * Metodo che effettua la validazione dei soggetti di una busta, controllando la loro registrazione nel registro dei servizi. 
	 *
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *   {@link Eccezione}, e viene inserito nella lista <var>errors</var>.
	 *
	 * 
	 */
	public void valida(boolean isMessaggioConAttachments) throws Exception {
		org.openspcoop2.protocol.sdk.validator.IValidazioneConSchema validazione = this.protocolFactory.createValidazioneConSchema(this.state);
		validazione.valida(this.message, this.isErroreProcessamento, this.isErroreIntestazione, isMessaggioConAttachments,this.validazioneManifestAttachments);
		this.erroriProcessamento = validazione.getEccezioniProcessamento();
		this.erroriValidazione = validazione.getEccezioniValidazione();
	}

}
