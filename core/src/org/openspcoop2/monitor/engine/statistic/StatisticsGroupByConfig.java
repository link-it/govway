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
package org.openspcoop2.monitor.engine.statistic;

/**
 * StatisticsGroupByConfig
 *
 * Configurazione per definire quali colonne includere nel GROUP BY
 * e nella popolazione dell'oggetto StatisticBean durante la generazione delle statistiche.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticsGroupByConfig {

	private boolean tipoMittente = true;
	private boolean nomeMittente = true;
	private boolean tipoDestinatario = true;
	private boolean nomeDestinatario = true;
	private boolean tipoServizio = true;
	private boolean servizio = true;
	private boolean versioneServizio = true;
	private boolean azione = true;
	private boolean servizioApplicativo = true;
	private boolean trasportoMittente = true;
	private boolean tokenIssuer = true;
	private boolean tokenClientId = true;
	private boolean tokenSubject = true;
	private boolean tokenUsername = true;
	private boolean tokenMail = true;
	private boolean esito = true;
	private boolean esitoContesto = true;
	private boolean gruppo = true;
	private boolean api = true;
	private boolean clusterId = true;
	private boolean clientAddress = true;

	// Costruttore di default: tutti i campi abilitati
	public StatisticsGroupByConfig() {
		// Default: tutti true
	}

	// Getters e Setters

	public boolean isTipoMittente() {
		return this.tipoMittente;
	}

	public void setTipoMittente(boolean tipoMittente) {
		this.tipoMittente = tipoMittente;
	}

	public boolean isNomeMittente() {
		return this.nomeMittente;
	}

	public void setNomeMittente(boolean nomeMittente) {
		this.nomeMittente = nomeMittente;
	}

	public boolean isTipoDestinatario() {
		return this.tipoDestinatario;
	}

	public void setTipoDestinatario(boolean tipoDestinatario) {
		this.tipoDestinatario = tipoDestinatario;
	}

	public boolean isNomeDestinatario() {
		return this.nomeDestinatario;
	}

	public void setNomeDestinatario(boolean nomeDestinatario) {
		this.nomeDestinatario = nomeDestinatario;
	}

	public boolean isTipoServizio() {
		return this.tipoServizio;
	}

	public void setTipoServizio(boolean tipoServizio) {
		this.tipoServizio = tipoServizio;
	}

	public boolean isServizio() {
		return this.servizio;
	}

	public void setServizio(boolean servizio) {
		this.servizio = servizio;
	}

	public boolean isVersioneServizio() {
		return this.versioneServizio;
	}

	public void setVersioneServizio(boolean versioneServizio) {
		this.versioneServizio = versioneServizio;
	}

	public boolean isAzione() {
		return this.azione;
	}

	public void setAzione(boolean azione) {
		this.azione = azione;
	}

	public boolean isServizioApplicativo() {
		return this.servizioApplicativo;
	}

	public void setServizioApplicativo(boolean servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;
	}

	public boolean isTrasportoMittente() {
		return this.trasportoMittente;
	}

	public void setTrasportoMittente(boolean trasportoMittente) {
		this.trasportoMittente = trasportoMittente;
	}

	public boolean isTokenIssuer() {
		return this.tokenIssuer;
	}

	public void setTokenIssuer(boolean tokenIssuer) {
		this.tokenIssuer = tokenIssuer;
	}

	public boolean isTokenClientId() {
		return this.tokenClientId;
	}

	public void setTokenClientId(boolean tokenClientId) {
		this.tokenClientId = tokenClientId;
	}

	public boolean isTokenSubject() {
		return this.tokenSubject;
	}

	public void setTokenSubject(boolean tokenSubject) {
		this.tokenSubject = tokenSubject;
	}

	public boolean isTokenUsername() {
		return this.tokenUsername;
	}

	public void setTokenUsername(boolean tokenUsername) {
		this.tokenUsername = tokenUsername;
	}

	public boolean isTokenMail() {
		return this.tokenMail;
	}

	public void setTokenMail(boolean tokenMail) {
		this.tokenMail = tokenMail;
	}

	public boolean isEsito() {
		return this.esito;
	}

	public void setEsito(boolean esito) {
		this.esito = esito;
	}

	public boolean isEsitoContesto() {
		return this.esitoContesto;
	}

	public void setEsitoContesto(boolean esitoContesto) {
		this.esitoContesto = esitoContesto;
	}

	public boolean isGruppo() {
		return this.gruppo;
	}

	public void setGruppo(boolean gruppo) {
		this.gruppo = gruppo;
	}

	public boolean isApi() {
		return this.api;
	}

	public void setApi(boolean api) {
		this.api = api;
	}

	public boolean isClusterId() {
		return this.clusterId;
	}

	public void setClusterId(boolean clusterId) {
		this.clusterId = clusterId;
	}

	public boolean isClientAddress() {
		return this.clientAddress;
	}

	public void setClientAddress(boolean clientAddress) {
		this.clientAddress = clientAddress;
	}

}
