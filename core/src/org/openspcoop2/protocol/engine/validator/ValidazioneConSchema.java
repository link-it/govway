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



package org.openspcoop2.protocol.engine.validator;


import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;

import org.slf4j.Logger;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;

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
	private java.util.Vector<Eccezione> erroriValidazione;
	/** Errori di processamento riscontrati sulla busta */
	private java.util.Vector<Eccezione> erroriProcessamento;

	/** Messaggio intero */
	private OpenSPCoop2Message message;
	
	/** Busta */
	private SOAPElement  busta;
	
	/** ManifestAttachments */
	private SOAPBody soapBody;
	
	/** IsErrore */
	private boolean isErroreProcessamento = false;
	private boolean isErroreIntestazione = false;
	
	private boolean validazioneManifestAttachments = false;
	
	private IProtocolFactory protocolFactory;
	
	/**
	 * Costruttore.
	 *
	 * @param header Busta da validare.
	 * @param soapBody  BodySOAP contenente il manifest degli attachments
	 * 
	 */
	public ValidazioneConSchema(OpenSPCoop2Message message, SOAPElement header,SOAPBody soapBody,boolean isErroreProcessamento, boolean isErroreIntestazione, boolean validazioneManifestAttachments, IProtocolFactory protocolFactory){
		this(message,header,soapBody,isErroreProcessamento,isErroreIntestazione,validazioneManifestAttachments,Configurazione.getLibraryLog(), protocolFactory);
	}
	/**
	 * Costruttore.
	 *
	 * @param header Busta da validare.
	 * @param soapBody  BodySOAP contenente il manifest degli attachments
	 * 
	 */
	public ValidazioneConSchema(OpenSPCoop2Message message, SOAPElement header,SOAPBody soapBody,boolean isErroreProcessamento, boolean isErroreIntestazione, boolean validazioneManifestAttachments,Logger aLog, IProtocolFactory protocolFactory){
		this.message = message;
		this.busta = header;
		this.soapBody = soapBody;
		this.isErroreProcessamento = isErroreProcessamento;
		this.isErroreIntestazione = isErroreIntestazione;
		this.validazioneManifestAttachments = validazioneManifestAttachments;
		this.protocolFactory = protocolFactory;
	}

	public IProtocolFactory getProtocolFactory(){
		return this.protocolFactory;
	}

	/**
	 * Ritorna un vector contenente eventuali eccezioni di validazione riscontrate nella busta.   
	 *
	 * @return Eccezioni riscontrate nella busta.
	 * 
	 */
	public java.util.Vector<Eccezione> getEccezioniValidazione(){
		return this.erroriValidazione;
	}
	/**
	 * Ritorna un vector contenente eventuali eccezioni di processamento riscontrate nella busta.   
	 *
	 * @return Eccezioni riscontrate nella busta.
	 * 
	 */
	public java.util.Vector<Eccezione> getEccezioniProcessamento(){
		return this.erroriProcessamento;
	}

	/**
	 * Metodo che effettua la validazione dei soggetti di una busta, controllando la loro registrazione nel registro dei servizi. 
	 *
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *   {@link Eccezione}, e viene inserito nel Vector <var>errors</var>.
	 *
	 * 
	 */
	public void valida(boolean isMessaggioConAttachments) throws Exception {
		org.openspcoop2.protocol.sdk.validator.IValidazioneConSchema validazione = this.protocolFactory.createValidazioneConSchema();
		validazione.valida(this.message, this.busta, this.soapBody, this.isErroreProcessamento, this.isErroreIntestazione, isMessaggioConAttachments,this.validazioneManifestAttachments);
		this.erroriProcessamento = validazione.getEccezioniProcessamento();
		this.erroriValidazione = validazione.getEccezioniValidazione();
	}

}
