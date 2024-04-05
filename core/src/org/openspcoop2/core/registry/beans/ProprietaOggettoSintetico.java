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

import org.openspcoop2.core.registry.ProprietaOggetto;


/** 
 * ProprietaOggettoSintetico
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

public class ProprietaOggettoSintetico extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {

	private static final long serialVersionUID = 1L;

	private java.lang.String utenteRichiedente;

	private java.util.Date dataCreazione;

	private java.lang.String utenteUltimaModifica;

	private java.util.Date dataUltimaModifica;

	public ProprietaOggettoSintetico() {
		super();
	}
	public ProprietaOggettoSintetico(ProprietaOggetto proprieta) {
		if(proprieta!=null) {
			this.utenteRichiedente = proprieta.getUtenteRichiedente();
			this.dataCreazione = proprieta.getDataCreazione();
			this.utenteUltimaModifica = proprieta.getUtenteUltimaModifica();
			this.dataUltimaModifica = proprieta.getDataUltimaModifica();
		}
	}
	
	public java.lang.String getUtenteRichiedente() {
		return this.utenteRichiedente;
	}

	public void setUtenteRichiedente(java.lang.String utenteRichiedente) {
		this.utenteRichiedente = utenteRichiedente;
	}

	public java.util.Date getDataCreazione() {
		return this.dataCreazione;
	}

	public void setDataCreazione(java.util.Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public java.lang.String getUtenteUltimaModifica() {
		return this.utenteUltimaModifica;
	}

	public void setUtenteUltimaModifica(java.lang.String utenteUltimaModifica) {
		this.utenteUltimaModifica = utenteUltimaModifica;
	}

	public java.util.Date getDataUltimaModifica() {
		return this.dataUltimaModifica;
	}

	public void setDataUltimaModifica(java.util.Date dataUltimaModifica) {
		this.dataUltimaModifica = dataUltimaModifica;
	}

}
