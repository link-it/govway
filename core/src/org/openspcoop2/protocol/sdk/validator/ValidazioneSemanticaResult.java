/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.sdk.validator;

import java.util.List;

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
	private List<Eccezione> erroriValidazione;
	/** Errori di processamento riscontrati sulla busta */
	private List<Eccezione> erroriProcessamento;
	/** ServizioCorrelato */
	private String servizioCorrelato;
	/** Tipo ServizioCorrelato */
	private String tipoServizioCorrelato;
	/** Versione ServizioCorrelato */
	private Integer versioneServizioCorrelato;
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
	public ValidazioneSemanticaResult(List<Eccezione> erroriValidazione, List<Eccezione> erroriProcessamento,
			String servizioCorrelato, String tipoServizioCorrelato, Integer versioneServizioCorrelato,
			Servizio infoServizio) {
		this.erroriProcessamento = erroriProcessamento;
		this.erroriValidazione = erroriValidazione;
		this.infoServizio = infoServizio;
		this.tipoServizioCorrelato = tipoServizioCorrelato;
		this.versioneServizioCorrelato = versioneServizioCorrelato;
		this.servizioCorrelato = servizioCorrelato;
	}
	
	public List<Eccezione> getErroriValidazione() {
		return this.erroriValidazione;
	}
	
	public List<Eccezione> getErroriProcessamento() {
		return this.erroriProcessamento;
	}
	
	public String getServizioCorrelato() {
		return this.servizioCorrelato;
	}
	
	public String getTipoServizioCorrelato() {
		return this.tipoServizioCorrelato;
	}
	
	public Integer getVersioneServizioCorrelato() {
		return this.versioneServizioCorrelato;
	}
	
	public Servizio getInfoServizio() {
		return this.infoServizio;
	}
}
