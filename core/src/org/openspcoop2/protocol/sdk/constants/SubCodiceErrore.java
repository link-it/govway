/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.sdk.constants;

import java.io.Serializable;

/**
 * SubCodiceErrore
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SubCodiceErrore implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer subCodice = null;
	
	public Integer getSubCodice() {
		return this.subCodice;
	}
	public void setSubCodice(Integer subCodice) {
		if(subCodice!=null && subCodice>=0){
			// non devo impostare un subcode minore di 0
			this.subCodice = subCodice;
		}
	}
	
	public SubCodiceErrore newInstance(){
		SubCodiceErrore sub = new SubCodiceErrore();
		sub.setSubCodice(this.subCodice!=null ? Integer.valueOf(this.subCodice.intValue()+"") : null);
		return sub;
	}
}
