/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.utils.serialization;

import java.util.ArrayList;
import java.util.List;

/**	
 * Contiene le informazioni sul filtro da effettuare durante la serializzazione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Filter {

	private List<Class<?>> filterByValue = new ArrayList<Class<?>>();
		
	public List<Class<?>> getFilterByValue() {
		return this.filterByValue;
	}

	public void setFilterByValue(List<Class<?>> filter) {
		this.filterByValue = filter;
	}

	public void addFilterByValue(Class<?> c){
		this.filterByValue.add(c);
	}
	
	public Class<?> getFilterByValue(int index){
		return this.filterByValue.get(index);
	}
	
	public Class<?> removeFilterByValue(int index){
		return this.filterByValue.remove(index);
	}
	
	public void clearFiltersByValue(){
		this.filterByValue.clear();
	}
	
	public int sizeFiltersByValue(){
		return this.filterByValue.size();
	}
	
	
	
	
	
	private List<String> filterByName = new ArrayList<String>();
	
	public List<String> getFilterByName() {
		return this.filterByName;
	}

	public void setFilterByName(List<String> filter) {
		this.filterByName = filter;
	}

	public void addFilterByName(String c){
		this.filterByName.add(c);
	}
	
	public String getFilterByName(int index){
		return this.filterByName.get(index);
	}
	
	public String removeFilterByName(int index){
		return this.filterByName.remove(index);
	}
	
	public void clearFiltersByName(){
		this.filterByName.clear();
	}
	
	public int sizeFiltersByName(){
		return this.filterByName.size();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	private FilterChecksumTypes filterChecksumType = FilterChecksumTypes.CRC;
	
	public FilterChecksumTypes getFilterChecksumType() {
		return this.filterChecksumType;
	}

	public void setFilterChecksumType(FilterChecksumTypes filterChecksumType) {
		this.filterChecksumType = filterChecksumType;
	}


	
	
	

	private List<FilteredObject> oggettiFiltrati = new ArrayList<FilteredObject>();
	
	public void addFilteredObject(FilteredObject o){
		this.oggettiFiltrati.add(o);
	}
	
	public int sizeFilteredObjects(){
		return this.oggettiFiltrati.size();
	}
	
	public void clearFilteredObjects(){
		this.oggettiFiltrati.clear();
	}
	
	public FilteredObject getFilteredObject(int index){
		return this.oggettiFiltrati.get(index);
	}
	
	public FilteredObject removeFilteredObject(int index){
		return this.oggettiFiltrati.remove(index);
	}
	
	public boolean existsFilteredObject(String id){
		for (FilteredObject filter : this.oggettiFiltrati) {
			if(id.equals(filter.getId())){
				return true;
			}
		}
		return false;
	}
	
	public List<FilteredObject> getOggettiFiltrati() {
		return this.oggettiFiltrati;
	}

	public void setOggettiFiltrati(List<FilteredObject> oggettiFiltrati) {
		this.oggettiFiltrati = oggettiFiltrati;
	}
}
