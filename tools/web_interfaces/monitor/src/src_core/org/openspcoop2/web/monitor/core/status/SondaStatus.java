package org.openspcoop2.web.monitor.core.status;

import java.io.Serializable;

import org.openspcoop2.generic_project.beans.IEnumeration;

public enum SondaStatus implements IEnumeration , Serializable , Cloneable{

	OK ("ok"), WARNING ("warning"), ERROR ("error"), UNDEFINED("undefined");

	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	SondaStatus(String value)
	{
		this.value = value;
	}

	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(SondaStatus object){
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
		for (SondaStatus tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (SondaStatus tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (SondaStatus tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}

	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}

	public static SondaStatus toEnumConstant(String value){
		SondaStatus res = null;
		if(SondaStatus.OK.getValue().equals(value)){
			res = SondaStatus.OK;
		}else if(SondaStatus.WARNING.getValue().equals(value)){
			res = SondaStatus.WARNING;
		}else if(SondaStatus.ERROR.getValue().equals(value)){
			res = SondaStatus.ERROR;
		} else if(SondaStatus.UNDEFINED.getValue().equals(value)){
			res = SondaStatus.UNDEFINED;
		} 


		return res;
	}

	public static IEnumeration toEnumConstantFromString(String value){
		SondaStatus res = null;
		if(SondaStatus.OK.toString().equals(value)){
			res = SondaStatus.OK;
		}else if(SondaStatus.WARNING.toString().equals(value)){
			res = SondaStatus.WARNING;
		}else if(SondaStatus.ERROR.toString().equals(value)){
			res = SondaStatus.ERROR;
		} else if(SondaStatus.UNDEFINED.toString().equals(value)){
			res = SondaStatus.UNDEFINED;
		} 
		return res;
	}

}
