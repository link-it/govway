package org.openspcoop2.utils.credential;

import java.io.Serializable;

/***
 * 
 * Enum dei tipi di reader disponibili
 *  
 * @author pintori
 *
 */
public enum PrincipalReaderType implements  Serializable , Cloneable {
	
	PRINCIPAL ("principal"), COOKIE("cookie");

	/** Value */
	private String value;
	public String getValue()
	{
		return this.value;
	}

	/** Official Constructor */
	PrincipalReaderType(String value)
	{
		this.value = value;
	}
	
	@Override
	public String toString(){
		return this.value;
	}

	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static PrincipalReaderType toEnumConstant(String value){
		PrincipalReaderType res = null;
		for (PrincipalReaderType tmp : values()) {
			if(tmp.getValue().equals(value)){
				res = tmp;
				break;
			}
		}
		return res;
	}
}
