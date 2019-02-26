/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.web.ctrlstat.costanti;

import java.util.List;

import org.openspcoop2.generic_project.exception.NotFoundException;

/**
*
* TipoOggettoDaSmistare
* 
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
* 
*/
public enum MultitenantSoggettiFruizioni {

	SOLO_SOGGETTI_ESTERNI("Solo Soggetti Esterni"), 
	ESCLUDI_SOGGETTO_FRUITORE("Escludi Soggetto Fruitore"), 
	TUTTI("Tutti");
	
	/** Value */
	private String value;
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	MultitenantSoggettiFruizioni(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(MultitenantSoggettiFruizioni object){
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
		if( !(object instanceof MultitenantSoggettiFruizioni) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((MultitenantSoggettiFruizioni)object));
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
	
	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (MultitenantSoggettiFruizioni tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (MultitenantSoggettiFruizioni tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (MultitenantSoggettiFruizioni tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static MultitenantSoggettiFruizioni toEnumConstant(String value){
		try{
			return toEnumConstant(value,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static MultitenantSoggettiFruizioni toEnumConstant(String value, boolean throwNotFoundException) throws NotFoundException{
		MultitenantSoggettiFruizioni res = null;
		for (MultitenantSoggettiFruizioni tmp : values()) {
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

}
