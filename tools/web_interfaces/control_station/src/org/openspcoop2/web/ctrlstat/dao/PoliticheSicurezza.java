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


package org.openspcoop2.web.ctrlstat.dao;

import java.io.Serializable;

import org.openspcoop2.utils.beans.BaseBean;

/**
 * PoliticheSicurezza
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PoliticheSicurezza extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private long idServizio;
	private long idFruitore;
	private long idServizioApplicativo;
	private String nomeServizioApplicativo;

	public String getNomeServizioApplicativo() {
		return this.nomeServizioApplicativo;
	}

	public void setNomeServizioApplicativo(String nomeServizioApplicativo) {
		this.nomeServizioApplicativo = nomeServizioApplicativo;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getIdServizio() {
		return this.idServizio;
	}

	public void setIdServizio(long idServizio) {
		this.idServizio = idServizio;
	}

	public long getIdFruitore() {
		return this.idFruitore;
	}

	public void setIdFruitore(long idFruitore) {
		this.idFruitore = idFruitore;
	}

	public long getIdServizioApplicativo() {
		return this.idServizioApplicativo;
	}

	public void setIdServizioApplicativo(long idServizioApplicativo) {
		this.idServizioApplicativo = idServizioApplicativo;
	}

}
