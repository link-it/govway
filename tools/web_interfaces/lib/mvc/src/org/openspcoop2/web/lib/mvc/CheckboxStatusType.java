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

package org.openspcoop2.web.lib.mvc;

/**
 * DataElementType
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author: pintori $
 * @version $Rev: 12946 $, $Date: 2017-05-03 12:40:45 +0200 (Wed, 03 May 2017) $
 */
public enum CheckboxStatusType {

	ABILITATO("yes"),
	DISABILITATO("no"),
	WARNING_ONLY("warn"),;
	
	private String value;
	CheckboxStatusType(String value){
		this.value = value;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
}
