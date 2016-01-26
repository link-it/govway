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

package org.openspcoop2.protocol.sdk.validator;

import java.util.Vector;

import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.Servizio;


/**
 * Wrapper per i risultati prodotti dal processo di validazione semantica.
 * <ul>
 * <li><i>erroriValidazione</i> - Vettore con gli errori di validazione riscontrati
 * <li><i>erroriProcessamento</i> - Vettore con gli errori di processamento incorsi durante la validazione
 * <li><i>servizioCorrelato</i> - Eventuale servizio correlato a quello richiesto nella busta
 * <li><i>tipoServizioCorrelato</i> - Eventuale tipo del servizio correlato a quello richiesto nella busta
 * <li><i>infoServizio</i> - Informazioni sul servizio
 * </ul>
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ValidazioneSemanticaResult {

	/** Errori di validazione riscontrati sulla busta */
	private Vector<Eccezione> erroriValidazione;
	/** Errori di processamento riscontrati sulla busta */
	private Vector<Eccezione> erroriProcessamento;
	/** ServizioCorrelato */
	private String servizioCorrelato;
	/** Tipo ServizioCorrelato */
	private String tipoServizioCorrelato;
	/** informazioni Servizio */
	private Servizio infoServizio;

	/**
	 * Imposta i risultati del processo di validazione semantica
	 * 
	 * @param erroriValidazione Vettore con gli errori di validazione riscontrati
	 * @param erroriProcessamento Vettore con gli errori di processamento incorsi durante la validazione
	 * @param servizioCorrelato Eventuale servizio correlato a quello richiesto nella busta
	 * @param tipoServizioCorrelato Eventuale tipo del servizio correlato a quello richiesto nella busta
	 * @param infoServizio Informazioni sul servizio
	 */
	public ValidazioneSemanticaResult(Vector<Eccezione> erroriValidazione, Vector<Eccezione> erroriProcessamento,
			String servizioCorrelato, String tipoServizioCorrelato, Servizio infoServizio) {
		this.erroriProcessamento = erroriProcessamento;
		this.erroriValidazione = erroriValidazione;
		this.infoServizio = infoServizio;
		this.tipoServizioCorrelato = tipoServizioCorrelato;
		this.servizioCorrelato = servizioCorrelato;
	}
	
	public Vector<Eccezione> getErroriValidazione() {
		return this.erroriValidazione;
	}
	
	public Vector<Eccezione> getErroriProcessamento() {
		return this.erroriProcessamento;
	}
	
	public String getServizioCorrelato() {
		return this.servizioCorrelato;
	}
	
	public String getTipoServizioCorrelato() {
		return this.tipoServizioCorrelato;
	}
	
	public Servizio getInfoServizio() {
		return this.infoServizio;
	}
}
