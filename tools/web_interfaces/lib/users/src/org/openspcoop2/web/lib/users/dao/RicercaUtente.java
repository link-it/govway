/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.lib.users.dao;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Stato
 * 
 * @author Giuliano Pintori (giuliano.pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class RicercaUtente implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	private long id_utente;
	private String label;
	private String modulo;
	private String modalitaRicerca;
	private String visibilita;
	private String ricerca;
	private String descrizione;
	private Date dataCreazione;
	private String protocollo;
	private String soggetto;
	
	@JsonIgnore
	public long getId() {
		return this.id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	@JsonIgnore
	public long getId_utente() {
		return this.id_utente;
	}
	public void setId_utente(long id_utente) {
		this.id_utente = id_utente;
	}
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getModulo() {
		return this.modulo;
	}
	public void setModulo(String modulo) {
		this.modulo = modulo;
	}
	public String getModalitaRicerca() {
		return this.modalitaRicerca;
	}
	public void setModalitaRicerca(String modalitaRicerca) {
		this.modalitaRicerca = modalitaRicerca;
	}
	public String getVisibilita() {
		return this.visibilita;
	}
	public void setVisibilita(String visibilita) {
		this.visibilita = visibilita;
	}
	public String getRicerca() {
		return this.ricerca;
	}
	public void setRicerca(String ricerca) {
		this.ricerca = ricerca;
	}
	public String getDescrizione() {
		return this.descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public Date getDataCreazione() {
		return this.dataCreazione;
	}
	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}
	public String getProtocollo() {
		return this.protocollo;
	}
	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}
	public String getSoggetto() {
		return this.soggetto;
	}
	public void setSoggetto(String soggetto) {
		this.soggetto = soggetto;
	}
}
