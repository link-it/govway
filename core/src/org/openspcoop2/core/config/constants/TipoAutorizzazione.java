/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
 * Enumeration TipoAutorizzazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipoAutorizzazione implements IEnumeration , Serializable , Cloneable {

	DISABILITATO (CostantiConfigurazione.AUTORIZZAZIONE_NONE,"disabilitato"),
	AUTHENTICATED (CostantiConfigurazione.AUTORIZZAZIONE_AUTHENTICATED,"authenticated"),
	ROLES (CostantiConfigurazione.AUTORIZZAZIONE_ROLES,"roles"),
	INTERNAL_ROLES (CostantiConfigurazione.AUTORIZZAZIONE_INTERNAL_ROLES,"internalRoles"),
	EXTERNAL_ROLES (CostantiConfigurazione.AUTORIZZAZIONE_EXTERNAL_ROLES,"externalRoles"),
	AUTHENTICATED_ROLES (CostantiConfigurazione.AUTORIZZAZIONE_AUTHENTICATED_OR_ROLES,"authenticatedOrRoles"),
	AUTHENTICATED_INTERNAL_ROLES (CostantiConfigurazione.AUTORIZZAZIONE_AUTHENTICATED_OR_INTERNAL_ROLES,"authenticatedOrInternalRoles"),
	AUTHENTICATED_EXTERNAL_ROLES (CostantiConfigurazione.AUTORIZZAZIONE_AUTHENTICATED_OR_EXTERNAL_ROLES,"authenticatedOrExternalRoles"),
	XACML_POLICY (CostantiConfigurazione.AUTORIZZAZIONE_XACML_POLICY,"xacmlPolicy"),
	INTERNAL_XACML_POLICY (CostantiConfigurazione.AUTORIZZAZIONE_INTERNAL_XACML_POLICY,"internalXacmlPolicy"),
	EXTERNAL_XACML_POLICY (CostantiConfigurazione.AUTORIZZAZIONE_EXTERNAL_XACML_POLICY,"externalXacmlPolicy"),
	TOKEN (CostantiConfigurazione.AUTORIZZAZIONE_TOKEN,"token");

	
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
	TipoAutorizzazione(String value,String label)
	{
		this.value = value;
		this.label = label;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(TipoAutorizzazione object){
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
		if( !(object instanceof TipoAutorizzazione) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((TipoAutorizzazione)object));
	}
	public String toString(boolean reportHTML){
		return toString();
	}
  	public String toString(boolean reportHTML,List<String> fieldsNotIncluded){
  		return toString();
  	}
  	public String diff(Object object,StringBuffer bf,boolean reportHTML){
		return bf.toString();
	}
	public String diff(Object object,StringBuffer bf,boolean reportHTML,List<String> fieldsNotIncluded){
		return bf.toString();
	}
	
	
	/** Utilities */
	
	public static boolean isInternalRolesRequired(String autorizzazione){
		return isInternalRolesRequired(TipoAutorizzazione.toEnumConstant(autorizzazione));
	}
	public static boolean isInternalRolesRequired(TipoAutorizzazione autorizzazione){
		if(TipoAutorizzazione.INTERNAL_ROLES.equals(autorizzazione)	||
				TipoAutorizzazione.AUTHENTICATED_INTERNAL_ROLES.equals(autorizzazione) 
				){
			return true;
		}
		return false;
	}
	
	public static boolean isExternalRolesRequired(String autorizzazione){
		return isExternalRolesRequired(TipoAutorizzazione.toEnumConstant(autorizzazione));
	}
	public static boolean isExternalRolesRequired(TipoAutorizzazione autorizzazione){
		if(TipoAutorizzazione.EXTERNAL_ROLES.equals(autorizzazione) ||
				TipoAutorizzazione.AUTHENTICATED_EXTERNAL_ROLES.equals(autorizzazione) 
				 ){
			return true;
		}
		return false;
	}
	
	public static boolean isRolesRequired(String autorizzazione){
		return isRolesRequired(TipoAutorizzazione.toEnumConstant(autorizzazione));
	}
	public static boolean isRolesRequired(TipoAutorizzazione autorizzazione){
		if(TipoAutorizzazione.ROLES.equals(autorizzazione) ||
				TipoAutorizzazione.AUTHENTICATED_ROLES.equals(autorizzazione) ||
				TipoAutorizzazione.INTERNAL_ROLES.equals(autorizzazione) ||
				TipoAutorizzazione.AUTHENTICATED_INTERNAL_ROLES.equals(autorizzazione) ||
				TipoAutorizzazione.EXTERNAL_ROLES.equals(autorizzazione) ||
				TipoAutorizzazione.AUTHENTICATED_EXTERNAL_ROLES.equals(autorizzazione) ){
			return true;
		}
		return false;
	}
	
	public static boolean isAuthenticationRequired(String autorizzazione){
		return isAuthenticationRequired(TipoAutorizzazione.toEnumConstant(autorizzazione));
	}
	public static boolean isAuthenticationRequired(TipoAutorizzazione autorizzazione){
		if(TipoAutorizzazione.AUTHENTICATED.equals(autorizzazione) ||
				TipoAutorizzazione.AUTHENTICATED_EXTERNAL_ROLES.equals(autorizzazione)  ||
				TipoAutorizzazione.AUTHENTICATED_INTERNAL_ROLES.equals(autorizzazione)  ||
				TipoAutorizzazione.AUTHENTICATED_ROLES.equals(autorizzazione)  
				){
			return true;
		}
		return false;
	}
	
	public static boolean isXacmlPolicyRequired(String autorizzazione){
		return isXacmlPolicyRequired(TipoAutorizzazione.toEnumConstant(autorizzazione));
	}
	public static boolean isXacmlPolicyRequired(TipoAutorizzazione autorizzazione){
		if(TipoAutorizzazione.XACML_POLICY.equals(autorizzazione) ||
				TipoAutorizzazione.EXTERNAL_XACML_POLICY.equals(autorizzazione)  ||
				TipoAutorizzazione.INTERNAL_XACML_POLICY.equals(autorizzazione)  
				){
			return true;
		}
		return false;
	}
	

	
	public static List<String> getAllValues(){
		List<String> l = new ArrayList<String>();
		for (TipoAutorizzazione tmp : values()) {
			l.add(tmp.getValue());
		}
		return l;
	}
	public static List<String> getValues(boolean authenticated,boolean optionalAuthenticated){
		List<String> l = new ArrayList<String>();
		for (TipoAutorizzazione tmp : values()) {
			
			if(isAuthenticationRequired(tmp)){
				// se l'autenticazione non è attivata, le autorizzazioni basate sull'autenticazione non hanno senso
				if(!authenticated){
					continue;
				}
			}
			
			if(isAuthenticationRequired(tmp) && isRolesRequired(tmp)){
				// i metodi con sia autenticazione che roles sono permessi solo se l'autenticazione è opzionale, altrimenti i ruoli non verranno mai presi in considerazione
				if(optionalAuthenticated==false){
					continue;
				}
			}
			else if(isInternalRolesRequired(tmp) || INTERNAL_XACML_POLICY.equals(tmp)){
				// sia per i ruoli interni che per la xacml policy interna è richiesta l'autenticazione obbligatoria. Altrimenti la xacml policy request non sarebbe valorizzabile.
				if(optionalAuthenticated){
					continue;
				}
			}
			
			l.add(tmp.getValue());
		}
		return l;
	}
	public static List<String> getLabels(){
		List<String> l = new ArrayList<String>();
		for (TipoAutorizzazione tmp : values()) {
			l.add(tmp.getLabel());
		}
		return l;
	}
	
	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoAutorizzazione tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoAutorizzazione tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoAutorizzazione tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static TipoAutorizzazione toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static TipoAutorizzazione toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		TipoAutorizzazione res = null;
		for (TipoAutorizzazione tmp : values()) {
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
		TipoAutorizzazione res = null;
		for (TipoAutorizzazione tmp : values()) {
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
