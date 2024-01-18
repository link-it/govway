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

package org.openspcoop2.core.config.utils;

import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;

/**
 * UpdateProprietaOggetto
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UpdateProprietaOggetto {

	private String username;
	private IDPortaDelegata idPortaDelegata;
	private IDPortaApplicativa idPortaApplicativa;
	private IDServizioApplicativo idServizioApplicativo;
	
	public UpdateProprietaOggetto(IDPortaDelegata idPortaDelegata, String username) {
		this(username);
		this.idPortaDelegata = idPortaDelegata;
	}
	public UpdateProprietaOggetto(IDPortaApplicativa idPortaApplicativa, String username) {
		this(username);
		this.idPortaApplicativa = idPortaApplicativa;
	}
	public UpdateProprietaOggetto(IDServizioApplicativo idServizioApplicativo, String username) {
		this(username);
		this.idServizioApplicativo = idServizioApplicativo;
	}
	private UpdateProprietaOggetto(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public IDPortaDelegata getIdPortaDelegata() {
		return this.idPortaDelegata;
	}
	public void setIdPortaDelegata(IDPortaDelegata idPortaDelegata) {
		this.idPortaDelegata = idPortaDelegata;
	}
	public IDPortaApplicativa getIdPortaApplicativa() {
		return this.idPortaApplicativa;
	}
	public void setIdPortaApplicativa(IDPortaApplicativa idPortaApplicativa) {
		this.idPortaApplicativa = idPortaApplicativa;
	}
	public IDServizioApplicativo getIdServizioApplicativo() {
		return this.idServizioApplicativo;
	}
	public void setIdServizioApplicativo(IDServizioApplicativo idServizioApplicativo) {
		this.idServizioApplicativo = idServizioApplicativo;
	}
	
}
