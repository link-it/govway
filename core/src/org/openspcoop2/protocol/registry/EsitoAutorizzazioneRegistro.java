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



package org.openspcoop2.protocol.registry;

/**
 * Esito di un processo di autorizzazione.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EsitoAutorizzazioneRegistro implements java.io.Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Indicazione se il servizio e' autorizzato */
	private boolean servizioAutorizzato;
	
	/** Messaggio di errore */
	private String details;
	
	public String getDetails() {
		return this.details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	/**
	 * Ritorna l'indicazione se il servizio e' autorizzato
	 * 
	 * @return indicazione se il servizio e' autorizzato
	 */
	public boolean isServizioAutorizzato() {
		return this.servizioAutorizzato;
	}
	
	/**
	 * Imposta l'indicazione se il servizio e' autorizzato
	 * 
	 * @param servizioAutorizzato indicazione se il servizio e' autorizzato
	 */
	public void setServizioAutorizzato(boolean servizioAutorizzato) {
		this.servizioAutorizzato = servizioAutorizzato;
	}
	

}
