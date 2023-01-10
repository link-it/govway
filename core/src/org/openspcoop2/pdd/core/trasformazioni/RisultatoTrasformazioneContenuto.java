/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.core.trasformazioni;

/**
 * RisultatoTrasformazioneContenuto
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RisultatoTrasformazioneContenuto {

	private boolean empty;
	private byte[] contenuto;
	private String contenutoAsString;
	private TipoTrasformazione tipoTrasformazione;
	
	public boolean isEmpty() {
		return this.empty;
	}
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
	public byte[] getContenuto() {
		return this.contenuto;
	}
	public String getContenutoAsString() {
		return this.contenutoAsString;
	}
	public void setContenuto(byte[] contenuto, String contenutoAsString) {
		this.contenuto = contenuto;
		this.contenutoAsString = contenutoAsString;
	}
	public TipoTrasformazione getTipoTrasformazione() {
		return this.tipoTrasformazione;
	}
	public void setTipoTrasformazione(TipoTrasformazione tipoTrasformazione) {
		this.tipoTrasformazione = tipoTrasformazione;
	}
}

