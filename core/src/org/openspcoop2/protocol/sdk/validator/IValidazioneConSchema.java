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



package org.openspcoop2.protocol.sdk.validator;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * Classe utilizzata per effettuare una validazione con schema xsd.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IValidazioneConSchema  {

	
	public IProtocolFactory getProtocolFactory();

	/**
	 * Ritorna un vector contenente eventuali eccezioni di validazione riscontrate nella busta.   
	 *
	 * @return Eccezioni riscontrate nella busta.
	 * 
	 */
	
	public java.util.Vector<Eccezione> getEccezioniValidazione();
	
	/**
	 * Ritorna un vector contenente eventuali eccezioni di processamento riscontrate nella busta.   
	 *
	 * @return Eccezioni riscontrate nella busta.
	 * 
	 */
	public java.util.Vector<Eccezione> getEccezioniProcessamento();

	
	/**
	 * Metodo che effettua la validazione dei soggetti di una busta, controllando la loro registrazione nel registro dei servizi. 
	 *
	 * Mano mano che sono incontrati errori di validazione, vengono salvati in oggetti di tipo {@link Eccezione} recuperabili tramite i metodi getEccezioniValidazione e getEccezioniProcessamento.
	 *
	 * 
	 */
	public void valida(OpenSPCoop2Message message, SOAPElement header, SOAPBody soapBody, boolean isErroreProcessamento, boolean isErroreIntestazione,
			boolean isMessaggioConAttachments, boolean validazioneManifestAttachments) throws ProtocolException;

	/**
	 * Metodo che viene chiamato in fase di startup della PdS per l'eventuale inizializzazione di validatori XSD
	 */
	
	public boolean initialize();
}
