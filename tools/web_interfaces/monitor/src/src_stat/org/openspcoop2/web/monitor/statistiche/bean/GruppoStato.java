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
package org.openspcoop2.web.monitor.statistiche.bean;

import java.io.Serializable;

/**
 * GruppoStato - Rappresenta lo stato di un gruppo/mapping di una porta
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class GruppoStato implements Serializable {

	private static final long serialVersionUID = 1L;

	private String nomeGruppo;
	private boolean abilitato;
	private long idPorta;
	private String nomePorta;
	private boolean isDefault;

	public GruppoStato() {
	}

	public GruppoStato(String nomeGruppo, boolean abilitato, long idPorta, String nomePorta, boolean isDefault) {
		this.nomeGruppo = nomeGruppo;
		this.abilitato = abilitato;
		this.idPorta = idPorta;
		this.nomePorta = nomePorta;
		this.isDefault = isDefault;
	}

	public String getNomeGruppo() {
		return this.nomeGruppo;
	}

	public void setNomeGruppo(String nomeGruppo) {
		this.nomeGruppo = nomeGruppo;
	}

	public boolean isAbilitato() {
		return this.abilitato;
	}

	public void setAbilitato(boolean abilitato) {
		this.abilitato = abilitato;
	}

	public long getIdPorta() {
		return this.idPorta;
	}

	public void setIdPorta(long idPorta) {
		this.idPorta = idPorta;
	}

	public String getNomePorta() {
		return this.nomePorta;
	}

	public void setNomePorta(String nomePorta) {
		this.nomePorta = nomePorta;
	}

	public boolean isDefault() {
		return this.isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
}
