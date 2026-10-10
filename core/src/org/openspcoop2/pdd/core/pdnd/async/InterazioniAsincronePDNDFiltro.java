/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.pdnd.async;

import java.io.Serializable;
import java.util.Date;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;

/**
 * Filtro utilizzato per la ricerca delle interazioni relative agli scambi di dati asincroni PDND
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InterazioniAsincronePDNDFiltro implements Serializable {

	private static final long serialVersionUID = 1L;

	// Ricerca 'contains' sull'identificativo dell'interazione
	private String interactionId;
	private TipoPdD ruolo;
	private FaseInterazioneAsincronaPDND fase;
	private IDSoggetto fruitore;
	// Possono essere valorizzati anche solamente il soggetto erogatore o il tipo/nome del servizio
	private IDServizio servizio;
	// Se valorizzata vengono restituite solamente le interazioni scadute rispetto alla data indicata (true) o non scadute (false)
	private Boolean scadute;
	private Date dataRiferimentoScadenza;
	
	private int offset = 0;
	private int limit = 0;
	
	public String getInteractionId() {
		return this.interactionId;
	}
	public void setInteractionId(String interactionId) {
		this.interactionId = interactionId;
	}
	public TipoPdD getRuolo() {
		return this.ruolo;
	}
	public void setRuolo(TipoPdD ruolo) {
		this.ruolo = ruolo;
	}
	public FaseInterazioneAsincronaPDND getFase() {
		return this.fase;
	}
	public void setFase(FaseInterazioneAsincronaPDND fase) {
		this.fase = fase;
	}
	public IDSoggetto getFruitore() {
		return this.fruitore;
	}
	public void setFruitore(IDSoggetto fruitore) {
		this.fruitore = fruitore;
	}
	public IDServizio getServizio() {
		return this.servizio;
	}
	public void setServizio(IDServizio servizio) {
		this.servizio = servizio;
	}
	public Boolean getScadute() {
		return this.scadute;
	}
	public void setScadute(Boolean scadute) {
		this.scadute = scadute;
	}
	public Date getDataRiferimentoScadenza() {
		return this.dataRiferimentoScadenza;
	}
	public void setDataRiferimentoScadenza(Date dataRiferimentoScadenza) {
		this.dataRiferimentoScadenza = dataRiferimentoScadenza;
	}
	public int getOffset() {
		return this.offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getLimit() {
		return this.limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
}
