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
package org.openspcoop2.core.registry.beans;
import java.io.Serializable;

import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;


/** 
 * AccordoServizioParteComuneServizioCompostoServizioComponenteSintetico
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */
public class AccordoServizioParteComuneServizioCompostoServizioComponenteSintetico extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AccordoServizioParteComuneServizioCompostoServizioComponenteSintetico() {
	}
	public AccordoServizioParteComuneServizioCompostoServizioComponenteSintetico(AccordoServizioParteComuneServizioCompostoServizioComponente param) {
		this.id = param.getId();
		this.idServizioComponente = param.getIdServizioComponente();
		this.tipoSoggetto = param.getTipoSoggetto();
		this.nomeSoggetto = param.getNomeSoggetto();
		this.tipo = param.getTipo();
		this.nome = param.getNome();
		this.versione = param.getVersione();
		this.azione = param.getAzione();
	}

	private Long id;

	private java.lang.Long idServizioComponente;

	private java.lang.String tipoSoggetto;

	private java.lang.String nomeSoggetto;

	private java.lang.String tipo;

	private java.lang.String nome;

	private java.lang.Integer versione = java.lang.Integer.valueOf("1");

	private java.lang.String azione;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public java.lang.Long getIdServizioComponente() {
		return this.idServizioComponente;
	}

	public void setIdServizioComponente(java.lang.Long idServizioComponente) {
		this.idServizioComponente = idServizioComponente;
	}

	public java.lang.String getTipoSoggetto() {
		return this.tipoSoggetto;
	}

	public void setTipoSoggetto(java.lang.String tipoSoggetto) {
		this.tipoSoggetto = tipoSoggetto;
	}

	public java.lang.String getNomeSoggetto() {
		return this.nomeSoggetto;
	}

	public void setNomeSoggetto(java.lang.String nomeSoggetto) {
		this.nomeSoggetto = nomeSoggetto;
	}

	public java.lang.String getTipo() {
		return this.tipo;
	}

	public void setTipo(java.lang.String tipo) {
		this.tipo = tipo;
	}

	public java.lang.String getNome() {
		return this.nome;
	}

	public void setNome(java.lang.String nome) {
		this.nome = nome;
	}

	public java.lang.Integer getVersione() {
		return this.versione;
	}

	public void setVersione(java.lang.Integer versione) {
		this.versione = versione;
	}

	public java.lang.String getAzione() {
		return this.azione;
	}

	public void setAzione(java.lang.String azione) {
		this.azione = azione;
	}

}
