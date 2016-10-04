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
 * EnumerationDouble
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum EnumerationDouble implements IEnumeration {

	_1_1 (java.lang.Double.parseDouble("1.1")),
	_2_2 (java.lang.Double.parseDouble("2.2")),
	_3_3 (java.lang.Double.parseDouble("3.3"));
	
	
	/** Value */
	private double value;
	@Override
	public Double getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	EnumerationDouble(double value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value+"";
	}
	public boolean equals(EnumerationDouble object){
		if(object==null)
			return false;
		return object.getValue().toString().equals(this.getValue().toString());	
	}
	
		
	
	/** compatibility with the generated bean (reflection) */
	public boolean equals(Object object,boolean checkID){
		if( !(object instanceof EnumerationDouble) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((EnumerationDouble)object));
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
	
	public static double[] toArray(){
		double[] res = new double[EnumerationDouble.values().length];
		int i=0;
		for (EnumerationDouble tmp : EnumerationDouble.values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[EnumerationDouble.values().length];
		int i=0;
		for (EnumerationDouble tmp : EnumerationDouble.values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[EnumerationDouble.values().length];
		int i=0;
		for (EnumerationDouble tmp : EnumerationDouble.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static EnumerationDouble toEnumConstant(double value){
		EnumerationDouble res = null;
		if(EnumerationDouble._1_1.getValue() == value){
			res = EnumerationDouble._1_1;
		}else if(EnumerationDouble._2_2.getValue() == value){
			res = EnumerationDouble._2_2;
		}else if(EnumerationDouble._3_3.getValue() == value){
			res = EnumerationDouble._3_3;
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		EnumerationDouble res = null;
		if(EnumerationDouble._1_1.toString().equals(value)){
			res = EnumerationDouble._1_1;
		}else if(EnumerationDouble._2_2.toString().equals(value)){
			res = EnumerationDouble._2_2;
		}else if(EnumerationDouble._3_3.toString().equals(value)){
			res = EnumerationDouble._3_3;
		}
		return res;
	}
}