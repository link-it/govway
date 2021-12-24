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

package org.openspcoop2.web.lib.mvc;

/**
 * DataElementType
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum CheckboxStatusType {

	ABILITATO(Costanti.CHECK_BOX_ENABLED),
	DISABILITATO(Costanti.CHECK_BOX_DISABLED),
	WARNING_ONLY(Costanti.CHECK_BOX_WARN),
	CONFIG_ENABLE(Costanti.CHECK_BOX_CONFIG_ENABLE),
	CONFIG_WARNING(Costanti.CHECK_BOX_CONFIG_WARNING),
	CONFIG_ERROR(Costanti.CHECK_BOX_CONFIG_ERROR),
	CONFIG_DISABLE(Costanti.CHECK_BOX_CONFIG_DISABLE);
	
	private String value;
	CheckboxStatusType(String value){
		this.value = value;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
}
