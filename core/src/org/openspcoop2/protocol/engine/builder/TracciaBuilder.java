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



package org.openspcoop2.protocol.engine.builder;

import javax.xml.soap.SOAPElement;

import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.tracciamento.IXMLTracciaBuilder;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * Classe per la costruzione delle traccie
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TracciaBuilder  {


	/** Logger utilizzato per debug. */
	protected Logger log = null;

	private IProtocolFactory protocolFactory;
	private IXMLTracciaBuilder tracciaProtocolBuilder;
	
	public TracciaBuilder(IProtocolFactory protocolFactory) {
		this(Configurazione.getLibraryLog(), protocolFactory);
	}
	public TracciaBuilder(Logger aLog, IProtocolFactory protocolFactory) {
		if(aLog!=null)
			this.log = aLog;
		else
			this.log = LoggerWrapperFactory.getLogger(TracciaBuilder.class);
		this.protocolFactory = protocolFactory;
		try{
			this.tracciaProtocolBuilder = this.protocolFactory.createXMLTracciaBuilder();
		}catch(Exception e){
			this.log.error("Errore durante la creazione dell'XMLTracciaBuilder: "+e.getMessage(),e);
		}
	}

	public IProtocolFactory getProtocolFactory(){
		return this.protocolFactory;
	}

	/* --------------------- TRACCIAMENTO BUSTE -----------------------*/

	/**
	 * Costruisce un SOAPElement contenente un Tracciamento come definito da specifica del protocollo 
	 *
	 * @param traccia conterra' i valori della traccia
	 * @return SOAPElement contenente il  tracciamento in caso di successo, null altrimenti. 
	 * 
	 */
	public SOAPElement toElement(Traccia traccia)throws ProtocolException{
		return this.tracciaProtocolBuilder.toElement(traccia);
	}
	
	/**
	 * Costruisce un array di byte contenenti un Tracciamento (formato XML) come definito da specifica del protocollo 
	 *
	 * @param traccia conterra' i valori della traccia
	 * @return array di byte contenente il codice XML del tracciamento in caso di successo, null altrimenti. 
	 * 
	 */
	public byte[] toByteArray(Traccia traccia)throws ProtocolException{
		return this.tracciaProtocolBuilder.toByteArray(traccia);
	}

	/**
	 * Costruisce un pezzo di codice XML contenente un Tracciamento come definito da specifica del protocollo 
	 *
	 * @param traccia conterra' i valori della traccia
	 * @return String contenente il codice XML del tracciamento in caso di successo, null altrimenti. 
	 * 
	 */
	public String toString(Traccia traccia) throws ProtocolException{
		return this.tracciaProtocolBuilder.toString(traccia);
	}

}