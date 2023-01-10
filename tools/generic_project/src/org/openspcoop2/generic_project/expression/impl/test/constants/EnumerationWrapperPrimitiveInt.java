/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.expression.impl.test.constants;

import org.openspcoop2.generic_project.beans.IEnumeration;

/**
 * EnumerationWrapperPrimitiveInt
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum EnumerationWrapperPrimitiveInt implements IEnumeration {

	_1 (java.lang.Integer.parseInt("1")),
	_2 (java.lang.Integer.parseInt("2")),
	_3 (java.lang.Integer.parseInt("3"));
	
	
	/** Value */
	private java.lang.Integer value;
	@Override
	public java.lang.Integer getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	EnumerationWrapperPrimitiveInt(java.lang.Integer value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value.toString();
	}
	public boolean equals(IEnumeration object){
		if(object==null)
			return false;
		if(! (object instanceof EnumerationWrapperPrimitiveInt)) {
			return false;
		}
		EnumerationWrapperPrimitiveInt en = (EnumerationWrapperPrimitiveInt) object;
		if(en.getValue()==null || this.getValue()==null) {
			return false; // anomalia
		}
		return en.getValue().intValue() == this.getValue().intValue();	
	}
	
		
	
	/** compatibility with the generated bean (reflection) */
	public boolean equals(Object object,boolean checkID){
		if( !(object instanceof EnumerationWrapperPrimitiveInt) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((EnumerationWrapperPrimitiveInt)object));
	}
	public String toString(boolean reportHTML){
		return toString();
	}
  	public String toString(boolean reportHTML,boolean notIncludeID){
  		return toString();
  	}
  	public String diff(Object object,StringBuilder bf,boolean reportHTML){
		return bf.toString();
	}
	public String diff(Object object,StringBuilder bf,boolean reportHTML,boolean checkID){
		return bf.toString();
	}
	
	
	
	/** Utilities */
	
	public static java.lang.Integer[] toArray(){
		java.lang.Integer[] res = new java.lang.Integer[EnumerationWrapperPrimitiveInt.values().length];
		int i=0;
		for (EnumerationWrapperPrimitiveInt tmp : EnumerationWrapperPrimitiveInt.values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[EnumerationWrapperPrimitiveInt.values().length];
		int i=0;
		for (EnumerationWrapperPrimitiveInt tmp : EnumerationWrapperPrimitiveInt.values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[EnumerationWrapperPrimitiveInt.values().length];
		int i=0;
		for (EnumerationWrapperPrimitiveInt tmp : EnumerationWrapperPrimitiveInt.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static EnumerationWrapperPrimitiveInt toEnumConstant(java.lang.Integer value){
		if(value==null) {
			return null;
		}
		EnumerationWrapperPrimitiveInt res = null;
		if(EnumerationWrapperPrimitiveInt._1.getValue().intValue() == value.intValue()){
			res = EnumerationWrapperPrimitiveInt._1;
		}else if(EnumerationWrapperPrimitiveInt._2.getValue().intValue() == value.intValue()){
			res = EnumerationWrapperPrimitiveInt._2;
		}else if(EnumerationWrapperPrimitiveInt._3.getValue().intValue() == value.intValue()){
			res = EnumerationWrapperPrimitiveInt._3;
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		EnumerationWrapperPrimitiveInt res = null;
		if(EnumerationWrapperPrimitiveInt._1.toString().equals(value)){
			res = EnumerationWrapperPrimitiveInt._1;
		}else if(EnumerationWrapperPrimitiveInt._2.toString().equals(value)){
			res = EnumerationWrapperPrimitiveInt._2;
		}else if(EnumerationWrapperPrimitiveInt._3.toString().equals(value)){
			res = EnumerationWrapperPrimitiveInt._3;
		}
		return res;
	}
}