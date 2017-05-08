/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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


package org.openspcoop2.core.commons;

import java.io.Serializable;

import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;

/**
 * MappingFruizionePortaDelegata
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class MappingFruizionePortaDelegata implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long tableId;
	
	private IDServizio idServizio;
	private IDSoggetto idFruitore;
	private IDPortaDelegata idPortaDelegata;

	
	public long getTableId() {
		return this.tableId;
	}
	public void setTableId(long tableId) {
		this.tableId = tableId;
	}

	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public void setIdServizio(IDServizio idServizio) {
		this.idServizio = idServizio;
	}
	public IDSoggetto getIdFruitore() {
		return this.idFruitore;
	}
	public void setIdFruitore(IDSoggetto idFruitore) {
		this.idFruitore = idFruitore;
	}
	public IDPortaDelegata getIdPortaDelegata() {
		return this.idPortaDelegata;
	}
	public void setIdPortaDelegata(IDPortaDelegata idPortaDelegata) {
		this.idPortaDelegata = idPortaDelegata;
	}
}
