package org.openspcoop2.web.monitor.statistiche.bean;

import java.io.Serializable;

import org.openspcoop2.generic_project.beans.IEnumeration;

public enum NumeroDimensioni implements IEnumeration , Serializable , Cloneable{

	DIMENSIONI_2 ("2d"), DIMENSIONI_3 ("3d");

	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}

	/** Official Constructor */
	NumeroDimensioni(String value)
	{
		this.value = value;
	}

	@Override
	public String toString(){
		return this.value;
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
		for (NumeroDimensioni tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (NumeroDimensioni tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (NumeroDimensioni tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}

	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}

	public static NumeroDimensioni toEnumConstant(String value){
		NumeroDimensioni res = null;
		if(NumeroDimensioni.DIMENSIONI_2.getValue().equals(value)){
			res = NumeroDimensioni.DIMENSIONI_2;
		}else if(NumeroDimensioni.DIMENSIONI_3.getValue().equals(value)){
			res = NumeroDimensioni.DIMENSIONI_3;
		}  

		return res;
	}

	public static IEnumeration toEnumConstantFromString(String value){
		NumeroDimensioni res = null;
		if(NumeroDimensioni.DIMENSIONI_2.toString().equals(value)){
			res = NumeroDimensioni.DIMENSIONI_2;
		}else if(NumeroDimensioni.DIMENSIONI_3.toString().equals(value)){
			res = NumeroDimensioni.DIMENSIONI_3;
		}  
		return res;
	}

}
