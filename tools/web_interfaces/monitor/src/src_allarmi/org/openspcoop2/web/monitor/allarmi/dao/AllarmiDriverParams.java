/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

package org.openspcoop2.web.monitor.allarmi.dao;

import java.util.List;

import org.openspcoop2.core.allarmi.constants.StatoAllarme;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;

/**     
 * AllarmiDriverParams
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmiDriverParams {

	private String tipologiaRicerca;
	private Boolean enabled;
	private StatoAllarme stato;
	private Boolean acknowledged;
	private String nomeAllarme;
	private List<IDSoggetto> listSoggettiProprietariAbilitati;
	private List<IDServizio> listIDServizioAbilitati;
	private List<String> tipoSoggettiByProtocollo;
	private List<String> tipoServiziByProtocollo; 
	private IDSoggetto idSoggettoProprietario;
	private List<IDServizio> listIDServizio;
	
	public String getTipologiaRicerca() {
		return this.tipologiaRicerca;
	}
	public void setTipologiaRicerca(String tipologiaRicerca) {
		this.tipologiaRicerca = tipologiaRicerca;
	}
	public Boolean getEnabled() {
		return this.enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public StatoAllarme getStato() {
		return this.stato;
	}
	public void setStato(StatoAllarme stato) {
		this.stato = stato;
	}
	public Boolean getAcknowledged() {
		return this.acknowledged;
	}
	public void setAcknowledged(Boolean acknowledged) {
		this.acknowledged = acknowledged;
	}
	public String getNomeAllarme() {
		return this.nomeAllarme;
	}
	public void setNomeAllarme(String nomeAllarme) {
		this.nomeAllarme = nomeAllarme;
	}
	public List<IDSoggetto> getListSoggettiProprietariAbilitati() {
		return this.listSoggettiProprietariAbilitati;
	}
	public void setListSoggettiProprietariAbilitati(List<IDSoggetto> listSoggettiProprietariAbilitati) {
		this.listSoggettiProprietariAbilitati = listSoggettiProprietariAbilitati;
	}
	public List<IDServizio> getListIDServizioAbilitati() {
		return this.listIDServizioAbilitati;
	}
	public void setListIDServizioAbilitati(List<IDServizio> listIDServizioAbilitati) {
		this.listIDServizioAbilitati = listIDServizioAbilitati;
	}
	public List<String> getTipoSoggettiByProtocollo() {
		return this.tipoSoggettiByProtocollo;
	}
	public void setTipoSoggettiByProtocollo(List<String> tipoSoggettiByProtocollo) {
		this.tipoSoggettiByProtocollo = tipoSoggettiByProtocollo;
	}
	public List<String> getTipoServiziByProtocollo() {
		return this.tipoServiziByProtocollo;
	}
	public void setTipoServiziByProtocollo(List<String> tipoServiziByProtocollo) {
		this.tipoServiziByProtocollo = tipoServiziByProtocollo;
	}
	public IDSoggetto getIdSoggettoProprietario() {
		return this.idSoggettoProprietario;
	}
	public void setIdSoggettoProprietario(IDSoggetto idSoggettoProprietario) {
		this.idSoggettoProprietario = idSoggettoProprietario;
	}
	public List<IDServizio> getListIDServizio() {
		return this.listIDServizio;
	}
	public void setListIDServizio(List<IDServizio> listIDServizio) {
		this.listIDServizio = listIDServizio;
	}
}
