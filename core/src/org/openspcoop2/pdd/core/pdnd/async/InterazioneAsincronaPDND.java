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
 * Stato di un'interazione relativa ad uno scambio di dati asincrono PDND
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InterazioneAsincronaPDND implements Serializable {

	private static final long serialVersionUID = 1L;

	// Identificativo della riga
	private long id;
	// Identificativo dell'interazione emesso dalla PDND
	private String interactionId;
	// Ruolo di GovWay nell'interazione (fruitore: delegata; erogatore: applicativa)
	private TipoPdD ruolo;
	// Ultima fase completata
	private FaseInterazioneAsincronaPDND fase;
	// Soggetto fruitore dell'e-service
	private IDSoggetto fruitore;
	// Servizio (con soggetto erogatore) associato all'e-service
	private IDServizio servizio;
	// Servizio associato all'API di callback
	private IDServizio servizioCallback;
	private String purposeId;
	private String consumerId;
	private String clientId;
	private String urlCallback;
	private Integer entityNumber;
	// Tempo massimo di callback (secondi) definito nell'API
	private Long tempoMaxCallback;
	// Tempo di disponibilità della risorsa (secondi) definito nell'API
	private Long tempoDisponibilita;
	private boolean confermaRichiesta;
	private Integer limiteEntita;
	private int numeroGetResource;
	private String idTransazioneStart;
	private Date dataStart;
	private Date dataCallback;
	private Date dataGetResource;
	private Date dataConfirmation;
	private Date dataScadenza;
	private Date dataAggiornamento;

	public long getId() {
		return this.id;
	}
	public void setId(long id) {
		this.id = id;
	}
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
	public IDServizio getServizioCallback() {
		return this.servizioCallback;
	}
	public void setServizioCallback(IDServizio servizioCallback) {
		this.servizioCallback = servizioCallback;
	}
	public String getPurposeId() {
		return this.purposeId;
	}
	public void setPurposeId(String purposeId) {
		this.purposeId = purposeId;
	}
	public String getConsumerId() {
		return this.consumerId;
	}
	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}
	public String getClientId() {
		return this.clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getUrlCallback() {
		return this.urlCallback;
	}
	public void setUrlCallback(String urlCallback) {
		this.urlCallback = urlCallback;
	}
	public Integer getEntityNumber() {
		return this.entityNumber;
	}
	public void setEntityNumber(Integer entityNumber) {
		this.entityNumber = entityNumber;
	}
	public Long getTempoMaxCallback() {
		return this.tempoMaxCallback;
	}
	public void setTempoMaxCallback(Long tempoMaxCallback) {
		this.tempoMaxCallback = tempoMaxCallback;
	}
	public Long getTempoDisponibilita() {
		return this.tempoDisponibilita;
	}
	public void setTempoDisponibilita(Long tempoDisponibilita) {
		this.tempoDisponibilita = tempoDisponibilita;
	}
	public boolean isConfermaRichiesta() {
		return this.confermaRichiesta;
	}
	public void setConfermaRichiesta(boolean confermaRichiesta) {
		this.confermaRichiesta = confermaRichiesta;
	}
	public Integer getLimiteEntita() {
		return this.limiteEntita;
	}
	public void setLimiteEntita(Integer limiteEntita) {
		this.limiteEntita = limiteEntita;
	}
	public int getNumeroGetResource() {
		return this.numeroGetResource;
	}
	public void setNumeroGetResource(int numeroGetResource) {
		this.numeroGetResource = numeroGetResource;
	}
	public String getIdTransazioneStart() {
		return this.idTransazioneStart;
	}
	public void setIdTransazioneStart(String idTransazioneStart) {
		this.idTransazioneStart = idTransazioneStart;
	}
	public Date getDataStart() {
		return this.dataStart;
	}
	public void setDataStart(Date dataStart) {
		this.dataStart = dataStart;
	}
	public Date getDataCallback() {
		return this.dataCallback;
	}
	public void setDataCallback(Date dataCallback) {
		this.dataCallback = dataCallback;
	}
	public Date getDataGetResource() {
		return this.dataGetResource;
	}
	public void setDataGetResource(Date dataGetResource) {
		this.dataGetResource = dataGetResource;
	}
	public Date getDataConfirmation() {
		return this.dataConfirmation;
	}
	public void setDataConfirmation(Date dataConfirmation) {
		this.dataConfirmation = dataConfirmation;
	}
	public Date getDataScadenza() {
		return this.dataScadenza;
	}
	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}
	public Date getDataAggiornamento() {
		return this.dataAggiornamento;
	}
	public void setDataAggiornamento(Date dataAggiornamento) {
		this.dataAggiornamento = dataAggiornamento;
	}
	
	public boolean isCallbackRicevuta() {
		return this.dataCallback!=null;
	}
	public boolean isConfermata() {
		return this.dataConfirmation!=null;
	}
}
