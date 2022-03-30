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
package org.openspcoop2.pdd.core.behaviour.built_in.load_balance;

import java.io.Serializable;
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
public enum LoadBalancerType implements IEnumeration , Serializable , Cloneable {

	ROUND_ROBIN (Costanti.ROUND_ROBIN ,"Round Robin", 
			"Il gruppo di connettori definiti verranno invocati a rotazione, in modo sequenziale.\nSi assume che ciascuno backend sia in grado di elaborare lo stesso numero di richieste.\nNon si tiene conto delle connessioni attive.",
			true),
	WEIGHT_ROUND_ROBIN (Costanti.WEIGHT_ROUND_ROBIN ,"Weight Round Robin",
		"Il gruppo di connettori definiti verranno invocati a rotazione, in modo sequenziale, tenendo conto della quantità relativa di richieste che è il backend in grado di elaborare.\nNon si tiene conto delle connessioni attive.",
		true),
	RANDOM (Costanti.RANDOM , "Random",
			"Il connettore viene selezionato tra quelli definiti casualmente.\nNon si tiene conto delle connessioni attive.",
			true),
	WEIGHT_RANDOM (Costanti.WEIGHT_RANDOM ,"Weight Random",
			"Il connettore viene selezionato tra quelli definiti casualmente, tenendo conto della quantità relativa di richieste che è il backend in grado di elaborare.\nNon si tiene conto delle connessioni attive.",
			true),
	IP_HASH (Costanti.IP_HASH ,"Source IP hash",
			"Combina l'indirizzo IP del client e l'eventuale indirizzo IP portato in un header 'Forwarded-For' o 'Client-IP' per generare una chiave hash che viene designata per un connettore specifico.",
			false),
	LEAST_CONNECTIONS(Costanti.LEAST_CONNECTIONS, "Least Connections",
			"Il connettore selezionato è quello che ha il numero minimo di connessioni attive.",
			true)
	;
	

	public boolean isTypeWithWeight() {
		return WEIGHT_ROUND_ROBIN.equals(this) || WEIGHT_RANDOM.equals(this);
	}

	
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
	
	/** Descrizione */
	private String descrizione;
	public String getDescrizione()
	{
		return this.descrizione;
	}
	
	/** Support Sticky */
	private boolean sticky;
	public boolean isSticky() {
		return this.sticky;
	}
	public void setSticky(boolean sticky) {
		this.sticky = sticky;
	}


	/** Official Constructor */
	LoadBalancerType(String value, String label, String descrizione,boolean sticky)
	{
		this.value = value;
		this.label = label;
		this.descrizione = descrizione;
		this.sticky = sticky;
	}


	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(LoadBalancerType object){
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
		if( !(object instanceof LoadBalancerType) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((LoadBalancerType)object));
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
	
	public static String[] getValues(){
		return toArray();
	}
	public static String[] getLabels(){
		String[] res = new String[values().length];
		int i=0;
		for (LoadBalancerType tmp : values()) {
			res[i]=tmp.getLabel();
			i++;
		}
		return res;
	}
	public static String[] getDescrizioni(){
		String[] res = new String[values().length];
		int i=0;
		for (LoadBalancerType tmp : values()) {
			res[i]=tmp.getDescrizione();
			i++;
		}
		return res;
	}
	
	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (LoadBalancerType tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (LoadBalancerType tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (LoadBalancerType tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static LoadBalancerType toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static LoadBalancerType toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		LoadBalancerType res = null;
		for (LoadBalancerType tmp : values()) {
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
		LoadBalancerType res = null;
		for (LoadBalancerType tmp : values()) {
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
