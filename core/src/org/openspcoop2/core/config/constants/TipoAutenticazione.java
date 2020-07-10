/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * Enumeration TipoAutenticazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipoAutenticazione implements IEnumeration , Serializable , Cloneable {

	DISABILITATO (CostantiConfigurazione.AUTENTICAZIONE_NONE,CostantiConfigurazione.LABEL_CREDENZIALE_DISABILITATO),
	SSL (CostantiConfigurazione.AUTENTICAZIONE_SSL,CostantiConfigurazione.LABEL_CREDENZIALE_SSL),
	BASIC (CostantiConfigurazione.AUTENTICAZIONE_BASIC,CostantiConfigurazione.LABEL_CREDENZIALE_BASIC),
	APIKEY (CostantiConfigurazione.AUTENTICAZIONE_APIKEY,CostantiConfigurazione.LABEL_CREDENZIALE_APIKEY),
	PRINCIPAL (CostantiConfigurazione.AUTENTICAZIONE_PRINCIPAL,CostantiConfigurazione.LABEL_CREDENZIALE_PRINCIPAL);
	
	/** Value */
	private String value;
	private String label;
	public String getLabel() {
		return this.label;
	}
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	TipoAutenticazione(String value,String label)
	{
		this.value = value;
		this.label = label;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(TipoAutenticazione object){
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
	
		
	
	/** compatibility with the generated bean (reflection) */
	public boolean equals(Object object,List<String> fieldsNotCheck){
		if( !(object instanceof TipoAutenticazione) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((TipoAutenticazione)object));
	}
	public String toString(boolean reportHTML){
		return toString();
	}
  	public String toString(boolean reportHTML,List<String> fieldsNotIncluded){
  		return toString();
  	}
  	public String diff(Object object,StringBuilder bf,boolean reportHTML){
		return bf.toString();
	}
	public String diff(Object object,StringBuilder bf,boolean reportHTML,List<String> fieldsNotIncluded){
		return bf.toString();
	}
	
	
	/** Utilities */
	
	public static List<String> getValues(){
		List<String> l = new ArrayList<>();
		for (TipoAutenticazione tmp : values()) {
			l.add(tmp.getValue());
		}
		return l;
	}
	public static List<String> getLabels(){
		List<String> l = new ArrayList<>();
		for (TipoAutenticazione tmp : values()) {
			l.add(tmp.getLabel());
		}
		return l;
	}
	
	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoAutenticazione tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoAutenticazione tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoAutenticazione tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static TipoAutenticazione toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static TipoAutenticazione toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		TipoAutenticazione res = null;
		for (TipoAutenticazione tmp : values()) {
			if(tmp.getValue().equals(value)){
				res = tmp;
				break;
			}
		}
		if(res==null && throwNotFoundException){
			throw new NotFoundException("Enum with value ["+value+"] not found");
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		try{
			return toEnumConstantFromString(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static IEnumeration toEnumConstantFromString(String value, boolean throwNotFoundException) throws NotFoundException{
		TipoAutenticazione res = null;
		for (TipoAutenticazione tmp : values()) {
			if(tmp.toString().equals(value)){
				res = tmp;
				break;
			}
		}
		if(res==null && throwNotFoundException){
			throw new NotFoundException("Enum with value ["+value+"] not found");
		}
		return res;
	}
}
