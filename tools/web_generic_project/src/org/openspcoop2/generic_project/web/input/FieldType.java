/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.generic_project.web.input;

import java.io.Serializable;
import org.openspcoop2.generic_project.beans.IEnumeration;

/**
 * FieldType Enum che elenca i tipi di input disponibili.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum FieldType implements IEnumeration , Serializable , Cloneable{

	TEXT ("text"), TEXT_AREA ("textarea"),SECRET ("secret") , SELECT_LIST("selectList"),
	DATE("date"), NUMBER ("number") , BOOLEAN_CHECKBOX ("booleanCheckBox") ,PICKLIST ("pickList"), MULTIPLE_CHOICE("multipleChoice"), 
	MULTIPLE_CHECKBOX ("multipleCheckBox"),SINGLE_CHOICE ("singleChoice"), RADIO_BUTTON("radioButton"),
	MULTIPLE_LISTBOX ("multipleListBox"),SINGLE_LISTBOX ("singleListBox"), SLIDER("slider"), SPINNER("spinner");

	//,TEXT_WITH_SUGGESTION ("textWithSuggestion")

	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	FieldType(String value)
	{
		this.value = value;
	}

	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(FieldType object){
		if(object==null)
			return false;
		if(object.getValue()==null)
			return false;
		return object.getValue().equals(this.getValue());	
	}
	public boolean equals(String object){
		if(object==null)
			return false;
		return object.equals(this.getValue());	
	}

	/** Utilities */

	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (FieldType tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (FieldType tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (FieldType tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}

	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}

	public static FieldType toEnumConstant(String value){
		FieldType res = null;
		if(FieldType.TEXT.getValue().equals(value)){
			res = FieldType.TEXT;
		}else if(FieldType.TEXT_AREA.getValue().equals(value)){
			res = FieldType.TEXT_AREA;
		}else if(FieldType.SECRET.getValue().equals(value)){
			res = FieldType.SECRET;
		}else if(FieldType.BOOLEAN_CHECKBOX.getValue().equals(value)){
			res = FieldType.BOOLEAN_CHECKBOX;
		}else if(FieldType.DATE.getValue().equals(value)){
			res = FieldType.DATE;
		}else if(FieldType.NUMBER.getValue().equals(value)){
			res = FieldType.NUMBER;
		}else if(FieldType.PICKLIST.getValue().equals(value)){
			res = FieldType.PICKLIST;
		}else if(FieldType.SELECT_LIST.getValue().equals(value)){
			res = FieldType.SELECT_LIST;
//		}else if(FieldType.TEXT_WITH_SUGGESTION.getValue().equals(value)){
//			res = FieldType.TEXT_WITH_SUGGESTION;
		}else if(FieldType.MULTIPLE_CHOICE.getValue().equals(value)){
			res = FieldType.MULTIPLE_CHOICE;
		}else if(FieldType.SINGLE_CHOICE.getValue().equals(value)){
			res = FieldType.SINGLE_CHOICE;
		}else if(FieldType.RADIO_BUTTON.getValue().equals(value)){
			res = FieldType.RADIO_BUTTON;
		}else if(FieldType.MULTIPLE_CHECKBOX.getValue().equals(value)){
			res = FieldType.MULTIPLE_CHECKBOX;
		}else if(FieldType.MULTIPLE_LISTBOX.getValue().equals(value)){
			res = FieldType.MULTIPLE_LISTBOX;
		}else if(FieldType.SINGLE_LISTBOX.getValue().equals(value)){
			res = FieldType.SINGLE_LISTBOX;
		}else if(FieldType.SLIDER.getValue().equals(value)){
			res = FieldType.SLIDER;
		}else if(FieldType.SPINNER.getValue().equals(value)){
			res = FieldType.SPINNER;
		} 
		
		
		return res;
	}

	public static IEnumeration toEnumConstantFromString(String value){
		FieldType res = null;
		if(FieldType.TEXT.toString().equals(value)){
			res = FieldType.TEXT;
		}else if(FieldType.TEXT_AREA.toString().equals(value)){
			res = FieldType.TEXT_AREA;
		}else if(FieldType.SECRET.toString().equals(value)){
			res = FieldType.SECRET;
		}else if(FieldType.BOOLEAN_CHECKBOX.toString().equals(value)){
			res = FieldType.BOOLEAN_CHECKBOX;
		}else if(FieldType.DATE.toString().equals(value)){
			res = FieldType.DATE;
		}else if(FieldType.NUMBER.toString().equals(value)){
			res = FieldType.NUMBER;
		}else if(FieldType.PICKLIST.toString().equals(value)){
			res = FieldType.PICKLIST;
		}else if(FieldType.SELECT_LIST.toString().equals(value)){
			res = FieldType.SELECT_LIST;
//		}else if(FieldType.TEXT_WITH_SUGGESTION.toString().equals(value)){
//			res = FieldType.TEXT_WITH_SUGGESTION;
		}else if(FieldType.MULTIPLE_CHOICE.toString().equals(value)){
			res = FieldType.MULTIPLE_CHOICE;
		}else if(FieldType.SINGLE_CHOICE.toString().equals(value)){
			res = FieldType.SINGLE_CHOICE;
		}else if(FieldType.RADIO_BUTTON.toString().equals(value)){
			res = FieldType.RADIO_BUTTON;
		}else if(FieldType.MULTIPLE_CHECKBOX.toString().equals(value)){
			res = FieldType.MULTIPLE_CHECKBOX;
		}else if(FieldType.MULTIPLE_LISTBOX.toString().equals(value)){
			res = FieldType.MULTIPLE_LISTBOX;
		}else if(FieldType.SINGLE_LISTBOX.toString().equals(value)){
			res = FieldType.SINGLE_LISTBOX;
		}else if(FieldType.SLIDER.toString().equals(value)){
			res = FieldType.SLIDER;
		}else if(FieldType.SPINNER.toString().equals(value)){
			res = FieldType.SPINNER;
		}
		return res;
	}
}
