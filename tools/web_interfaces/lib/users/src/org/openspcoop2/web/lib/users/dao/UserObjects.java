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

package org.openspcoop2.web.lib.users.dao;

/**
 * UpdateUserResults
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class UserObjects {

	public int pdd;
	public int gruppi;
	public int ruoli;
	public int scope;
	public int soggetti;
	public int accordi_parte_comune;
	public int accordi_accoperazione;
	public int accordi_parte_specifica;
	
	public String toString(boolean cooperazione) {
		StringBuilder sb = new StringBuilder();
		if(cooperazione) {
			sb.append("accordi-cooperazione:").append(this.accordi_accoperazione);
			sb.append(" api:").append(this.accordi_parte_comune);
		}
		else {
			sb.append("pdd:").append(this.pdd);
			sb.append(" gruppi:").append(this.gruppi);
			sb.append(" ruoli:").append(this.ruoli);
			sb.append(" scope:").append(this.scope);
			sb.append(" soggetti:").append(this.soggetti);
			sb.append(" api:").append(this.accordi_parte_comune);
			sb.append(" servizi:").append(this.accordi_parte_specifica);
		}
		return sb.toString();
	}
	
}
