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

package org.openspcoop2.web.ctrlstat.servlet.sa;

import org.openspcoop2.core.id.IDSoggetto;

/**
 * ServiziApplicativiGeneralInfo
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ServiziApplicativiGeneralInfo {

	private String[] soggettiList = null;
	private String[] soggettiListLabel = null;
	private String tipoProtocollo = null;
	private String tipoENomeSoggetto = "";
	private String provider = null;
	private IDSoggetto idSoggetto = null;
	
	public IDSoggetto getIdSoggetto() {
		return this.idSoggetto;
	}
	public void setIdSoggetto(IDSoggetto idSoggetto) {
		this.idSoggetto = idSoggetto;
	}
	public String[] getSoggettiList() {
		return this.soggettiList;
	}
	public void setSoggettiList(String[] soggettiList) {
		this.soggettiList = soggettiList;
	}
	public String[] getSoggettiListLabel() {
		return this.soggettiListLabel;
	}
	public void setSoggettiListLabel(String[] soggettiListLabel) {
		this.soggettiListLabel = soggettiListLabel;
	}
	public String getTipoProtocollo() {
		return this.tipoProtocollo;
	}
	public void setTipoProtocollo(String tipoProtocollo) {
		this.tipoProtocollo = tipoProtocollo;
	}
	public String getTipoENomeSoggetto() {
		return this.tipoENomeSoggetto;
	}
	public void setTipoENomeSoggetto(String tipoENomeSoggetto) {
		this.tipoENomeSoggetto = tipoENomeSoggetto;
	}
	public String getProvider() {
		return this.provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
}
