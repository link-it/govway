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

package org.openspcoop2.utils;

import java.util.Locale;

/**
 * StringWrapper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StringWrapper {

	private String value;
	
	public StringWrapper(String value){
		this.value = value;
	}
	
	public void replace(Character oldChar, Character newChar){
		this.value = this.value.replace(oldChar, newChar);
	}	
	public void replace(CharSequence target, CharSequence replacement){
		this.value = this.value.replace(target, replacement);
	}	
	public void replaceAll(String regex, String replacement){
		this.value = this.value.replaceAll(regex, replacement);
	}	
	public void replaceFirst(String regex, String replacement){
		this.value = this.value.replaceFirst(regex, replacement);
	}
	
	
	public void substring(int beginIndex){
		this.value = this.value.substring(beginIndex);
	}
	public void substring(int beginIndex, int endIndex){
		this.value = this.value.substring(beginIndex, endIndex);
	}
	
	
	public void toLowerCase(){
		this.value = this.value.toLowerCase();
	}
	public void toLowerCase(Locale locale){
		this.value = this.value.toLowerCase(locale);
	}
	
	public void toUpperCase(){
		this.value = this.value.toUpperCase();
	}
	public void toUpperCase(Locale locale){
		this.value = this.value.toUpperCase(locale);
	}
	
	
	public void concat(String str){
		this.value = this.value.concat(str);
	}
	
	
	public void trim(){
		this.value = this.value.trim();
	}
	
	
	@Override
	public String toString(){
		return this.value;
	}
}
