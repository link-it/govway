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
package org.openspcoop2.core.controllo_traffico.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 *  TipoRisorsaPolicyAttiva
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipoRisorsaPolicyAttiva implements IEnumeration , Serializable , Cloneable {

	NUMERO_RICHIESTE_SIMULTANEE ("NumeroRichiesteSimultanee"),
	NUMERO_RICHIESTE (TipoRisorsa.NUMERO_RICHIESTE.getValue()),
	DIMENSIONE_MASSIMA_MESSAGGIO (TipoRisorsa.DIMENSIONE_MASSIMA_MESSAGGIO.getValue()),
	OCCUPAZIONE_BANDA (TipoRisorsa.OCCUPAZIONE_BANDA.getValue()),
	TEMPO_COMPLESSIVO_RISPOSTA (TipoRisorsa.TEMPO_COMPLESSIVO_RISPOSTA.getValue()),
	TEMPO_MEDIO_RISPOSTA (TipoRisorsa.TEMPO_MEDIO_RISPOSTA.getValue()),
	NUMERO_RICHIESTE_FALLITE (TipoRisorsa.NUMERO_RICHIESTE_FALLITE.getValue()),
	NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO (TipoRisorsa.NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO.getValue()),
	NUMERO_FAULT_APPLICATIVI (TipoRisorsa.NUMERO_FAULT_APPLICATIVI.getValue()),
	NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI (TipoRisorsa.NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI.getValue());
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	TipoRisorsaPolicyAttiva(String value)
	{
		this.value = value;
	}

	
	public static TipoRisorsaPolicyAttiva getTipo(String tipo, boolean richiesteSimultanee) throws NotFoundException {
		return getTipo(TipoRisorsa.toEnumConstant(tipo, true), richiesteSimultanee);
	}
	public static TipoRisorsaPolicyAttiva getTipo(TipoRisorsa tipo, boolean richiesteSimultanee) throws NotFoundException {
		if(richiesteSimultanee) {
			return TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_SIMULTANEE;
		}
		else {
			return TipoRisorsaPolicyAttiva.toEnumConstant(tipo.getValue(), true);
		}
	}

	
	public TipoRisorsa getTipoRisorsa(boolean throwNotFoundException) throws NotFoundException {
		if(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_SIMULTANEE.equals(this)) {
			return TipoRisorsa.NUMERO_RICHIESTE;
		}
		else {
			return TipoRisorsa.toEnumConstant(this.value, throwNotFoundException);
		}
	}
	public boolean isRichiesteSimultanee() {
		return TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_SIMULTANEE.equals(this);
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
		if( !(object instanceof TipoRisorsaPolicyAttiva) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((TipoRisorsaPolicyAttiva)object));
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
	
	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoRisorsaPolicyAttiva tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoRisorsaPolicyAttiva tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoRisorsaPolicyAttiva tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static TipoRisorsaPolicyAttiva toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static TipoRisorsaPolicyAttiva toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		TipoRisorsaPolicyAttiva res = null;
		for (TipoRisorsaPolicyAttiva tmp : values()) {
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
		TipoRisorsaPolicyAttiva res = null;
		for (TipoRisorsaPolicyAttiva tmp : values()) {
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
