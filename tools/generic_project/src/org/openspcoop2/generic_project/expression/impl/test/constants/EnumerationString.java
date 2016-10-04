/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.generic_project.expression.impl.test.constants;

import org.openspcoop2.generic_project.beans.IEnumeration;

/**
 * EnumerationString
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum EnumerationString implements IEnumeration {

	AMMINISTRATIVO ("Amministrativo"),
	CIVILE ("Civile"),
	ALTRO ("Altro"),
	VALORE_GIA_MAIUSCOLO ("VALORE_GIA_MAIUSCOLO"),
	VALORE_MINUSCOLO ("valore_minuscolo"),
	_VALORE ("_valore"),
	VALORE_ ("valore_"),
	_VALORE_ ("_valore_");
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	EnumerationString(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(EnumerationString object){
		if(object==null)
			return false;
		if(object.getValue()==null)
			return false;
		return object.getValue().equals(this.getValue());	
	}
	
		
	
	/** compatibility with the generated bean (reflection) */
	public boolean equals(Object object,boolean checkID){
		if( !(object instanceof EnumerationString) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((EnumerationString)object));
	}
	public String toString(boolean reportHTML){
		return toString();
	}
  	public String toString(boolean reportHTML,boolean notIncludeID){
  		return toString();
  	}
  	public String diff(Object object,StringBuffer bf,boolean reportHTML){
		return bf.toString();
	}
	public String diff(Object object,StringBuffer bf,boolean reportHTML,boolean checkID){
		return bf.toString();
	}
	
	
	
	/** Utilities */
	
	public static String[] toArray(){
		String[] res = new String[EnumerationString.values().length];
		int i=0;
		for (EnumerationString tmp : EnumerationString.values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[EnumerationString.values().length];
		int i=0;
		for (EnumerationString tmp : EnumerationString.values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[EnumerationString.values().length];
		int i=0;
		for (EnumerationString tmp : EnumerationString.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static EnumerationString toEnumConstant(String value){
		EnumerationString res = null;
		if(EnumerationString.AMMINISTRATIVO.getValue().equals(value)){
			res = EnumerationString.AMMINISTRATIVO;
		}else if(EnumerationString.CIVILE.getValue().equals(value)){
			res = EnumerationString.CIVILE;
		}else if(EnumerationString.ALTRO.getValue().equals(value)){
			res = EnumerationString.ALTRO;
		}else if(EnumerationString.VALORE_GIA_MAIUSCOLO.getValue().equals(value)){
			res = EnumerationString.VALORE_GIA_MAIUSCOLO;
		}else if(EnumerationString.VALORE_MINUSCOLO.getValue().equals(value)){
			res = EnumerationString.VALORE_MINUSCOLO;
		}else if(EnumerationString._VALORE.getValue().equals(value)){
			res = EnumerationString._VALORE;
		}else if(EnumerationString.VALORE_.getValue().equals(value)){
			res = EnumerationString.VALORE_;
		}else if(EnumerationString._VALORE_.getValue().equals(value)){
			res = EnumerationString._VALORE_;
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		EnumerationString res = null;
		if(EnumerationString.AMMINISTRATIVO.toString().equals(value)){
			res = EnumerationString.AMMINISTRATIVO;
		}else if(EnumerationString.CIVILE.toString().equals(value)){
			res = EnumerationString.CIVILE;
		}else if(EnumerationString.ALTRO.toString().equals(value)){
			res = EnumerationString.ALTRO;
		}else if(EnumerationString.VALORE_GIA_MAIUSCOLO.toString().equals(value)){
			res = EnumerationString.VALORE_GIA_MAIUSCOLO;
		}else if(EnumerationString.VALORE_MINUSCOLO.toString().equals(value)){
			res = EnumerationString.VALORE_MINUSCOLO;
		}else if(EnumerationString._VALORE.toString().equals(value)){
			res = EnumerationString._VALORE;
		}else if(EnumerationString.VALORE_.toString().equals(value)){
			res = EnumerationString.VALORE_;
		}else if(EnumerationString._VALORE_.toString().equals(value)){
			res = EnumerationString._VALORE_;
		}
		return res;
	}
}