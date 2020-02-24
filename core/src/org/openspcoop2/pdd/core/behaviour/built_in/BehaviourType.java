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
package org.openspcoop2.pdd.core.behaviour.built_in;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * BehaviourType
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum BehaviourType implements IEnumeration , Serializable , Cloneable {

	CONSEGNA_LOAD_BALANCE (Costanti.CONSEGNA_LOAD_BALANCE ,"Load Balance"), // per tutti
	CONSEGNA_MULTIPLA (Costanti.CONSEGNA_MULTIPLA ,"Più Destinatari"), // per oneway-soap
	CONSEGNA_CONDIZIONALE (Costanti.CONSEGNA_CONDIZIONALE ,"Consegna Condizionale"), // per !oneway-soap e rest
	CONSEGNA_CON_NOTIFICHE (Costanti.CONSEGNA_CON_NOTIFICHE ,"Consegna con Notifiche"), // per !oneway-soap e rest
	CUSTOM (Costanti.CONSEGNA_CUSTOM ,"Personalizzata");


	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}

	/** Label */
	private String label;
	public String getLabel()
	{
		return this.label;
	}


	/** Official Constructor */
	BehaviourType(String value, String label)
	{
		this.value = value;
		this.label = label;
	}


	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(BehaviourType object){
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
		if( !(object instanceof BehaviourType) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((BehaviourType)object));
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

	public static List<BehaviourType> getEnums(boolean soapOneway){
		List<BehaviourType> l = new ArrayList<BehaviourType>();
		l.add(BehaviourType.CONSEGNA_LOAD_BALANCE);
		l.add(BehaviourType.CONSEGNA_CONDIZIONALE);
		if(soapOneway) {
			l.add(BehaviourType.CONSEGNA_MULTIPLA);
		}
		else {
			l.add(BehaviourType.CONSEGNA_CON_NOTIFICHE);
		}
		l.add(BehaviourType.CUSTOM);
		return l;
	}
	public static List<String> getLabels(boolean soapOneway){
		List<BehaviourType> l = getEnums(soapOneway);
		List<String> newL = new ArrayList<String>();
		for (BehaviourType behaviourType : l) {
			newL.add(behaviourType.getLabel());
		}
		return newL;
	}
	public static List<String> getValues(boolean soapOneway){
		List<BehaviourType> l = getEnums(soapOneway);
		List<String> newL = new ArrayList<String>();
		for (BehaviourType behaviourType : l) {
			newL.add(behaviourType.getValue());
		}
		return newL;
	}

	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (BehaviourType tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (BehaviourType tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (BehaviourType tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}

	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}

	public static BehaviourType toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static BehaviourType toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		BehaviourType res = null;
		for (BehaviourType tmp : values()) {
			if(tmp.getValue().equals(value)){
				res = tmp;
				break;
			}
		}
		if(res==null && value!=null && !"".equals(value)) {
			return BehaviourType.CUSTOM;
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
		BehaviourType res = null;
		for (BehaviourType tmp : values()) {
			if(tmp.toString().equals(value)){
				res = tmp;
				break;
			}
		}
		if(res==null && value!=null && !"".equals(value)) {
			return BehaviourType.CUSTOM;
		}
		if(res==null && throwNotFoundException){
			throw new NotFoundException("Enum with value ["+value+"] not found");
		}
		return res;
	}
}
