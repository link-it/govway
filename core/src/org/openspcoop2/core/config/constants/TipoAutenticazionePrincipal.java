/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
public enum TipoAutenticazionePrincipal implements IEnumeration , Serializable , Cloneable {

	CONTAINER (CostantiConfigurazione.AUTENTICAZIONE_PRINCIPAL_CONTAINER,CostantiConfigurazione.LABEL_AUTENTICAZIONE_PRINCIPAL_CONTAINER),
	HEADER (CostantiConfigurazione.AUTENTICAZIONE_PRINCIPAL_HEADER,CostantiConfigurazione.LABEL_AUTENTICAZIONE_PRINCIPAL_HEADER),
	FORM (CostantiConfigurazione.AUTENTICAZIONE_PRINCIPAL_FORM,CostantiConfigurazione.LABEL_AUTENTICAZIONE_PRINCIPAL_FORM),
	URL (CostantiConfigurazione.AUTENTICAZIONE_PRINCIPAL_URL,CostantiConfigurazione.LABEL_AUTENTICAZIONE_PRINCIPAL_URL),
	INDIRIZZO_IP (CostantiConfigurazione.AUTENTICAZIONE_PRINCIPAL_INDIRIZZO_IP,CostantiConfigurazione.LABEL_AUTENTICAZIONE_PRINCIPAL_INDIRIZZO_IP),
	INDIRIZZO_IP_X_FORWARDED_FOR (CostantiConfigurazione.AUTENTICAZIONE_PRINCIPAL_INDIRIZZO_IP_X_FORWARDED_FOR,CostantiConfigurazione.LABEL_AUTENTICAZIONE_PRINCIPAL_INDIRIZZO_IP_X_FORWARDED_FOR),
	// Ho levato il contenuto, poichè senno devo fare il digest per poterlo poi cachare
	// CONTENT (CostantiConfigurazione.AUTENTICAZIONE_PRINCIPAL_CONTENT,CostantiConfigurazione.LABEL_AUTENTICAZIONE_PRINCIPAL_CONTENT),
	TOKEN (CostantiConfigurazione.AUTENTICAZIONE_PRINCIPAL_TOKEN,CostantiConfigurazione.LABEL_AUTENTICAZIONE_PRINCIPAL_TOKEN);
	
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
	TipoAutenticazionePrincipal(String value,String label)
	{
		this.value = value;
		this.label = label;
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
	
		
	
	/** compatibility with the generated bean (reflection) */
	public boolean equals(Object object,List<String> fieldsNotCheck){
		if( !(object instanceof TipoAutenticazionePrincipal) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((TipoAutenticazionePrincipal)object));
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
		return getValues(true);
	}
	public static List<String> getValues(boolean token){
		List<String> l = new ArrayList<>();
		for (TipoAutenticazionePrincipal tmp : values()) {
			if(!token) {
				if(TipoAutenticazionePrincipal.TOKEN.equals(tmp)) {
					continue;
				}
			}
			l.add(tmp.getValue());
		}
		return l;
	}
	public static List<String> getLabels(){
		return getLabels(true);
	}
	public static List<String> getLabels(boolean token){
		List<String> l = new ArrayList<>();
		for (TipoAutenticazionePrincipal tmp : values()) {
			if(!token) {
				if(TipoAutenticazionePrincipal.TOKEN.equals(tmp)) {
					continue;
				}
			}
			l.add(tmp.getLabel());
		}
		return l;
	}
	
	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoAutenticazionePrincipal tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoAutenticazionePrincipal tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoAutenticazionePrincipal tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static TipoAutenticazionePrincipal toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static TipoAutenticazionePrincipal toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		TipoAutenticazionePrincipal res = null;
		for (TipoAutenticazionePrincipal tmp : values()) {
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
		TipoAutenticazionePrincipal res = null;
		for (TipoAutenticazionePrincipal tmp : values()) {
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
