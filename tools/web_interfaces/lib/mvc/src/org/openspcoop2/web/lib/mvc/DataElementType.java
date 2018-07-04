/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
public enum DataElementType {

	HIDDEN("hidden"),
	TEXT("text"),TEXT_EDIT("textedit"), 
	TEXT_AREA("textarea"),TEXT_AREA_NO_EDIT("textarea-noedit"),
	SELECT("select"), MULTI_SELECT("multi-select"),
	TITLE("title"),SUBTITLE("subtitle"),
	CRYPT("crypt"),
	CHECKBOX("checkbox"),
	LINK("link"),
	FILE("file"),
	NOTE("note"),
	RADIO("radio"),
	IMAGE("image"), BUTTON("button"),
	NUMBER("number");
	
	private String value;
	DataElementType(String value){
		this.value = value;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
}
