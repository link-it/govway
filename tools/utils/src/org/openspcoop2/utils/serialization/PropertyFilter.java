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


package org.openspcoop2.utils.serialization;

/**	
 * Contiene classe per effettuare un filtro JSON
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class PropertyFilter implements net.sf.json.util.PropertyFilter{

	
	private PropertyFilterCore core;
	
	public PropertyFilter(Filter filter,IDBuilder idBuilder,ISerializer serializer){
		this.core = new PropertyFilterCore(filter, idBuilder, serializer);
	}
	public PropertyFilter(Filter filter,ISerializer serializer){
		this.core = new PropertyFilterCore(filter, serializer);
	}
	
	@Override
	public boolean apply( Object source, String name, Object value ) {  
		
		// Filtri per valore
		if(value!=null){
			for(int i=0; i<this.core.getFilter().sizeFiltersByValue(); i++){
				Class<?> classFilter = this.core.getFilter().getFilterByValue(i);
				if(value.getClass().getName().equals(classFilter.getName())){
					this.core.applicaFiltro(source, name, value, classFilter);	
					return true; 
				}
			}
		}
		
		
		// Filtri per nome field
		String nomeField = source.getClass().getName()+"."+name;
		for(int i=0; i<this.core.getFilter().sizeFiltersByName(); i++){
			String filterName = this.core.getFilter().getFilterByName(i);
			if(nomeField.equals(filterName)){			
				if(value!=null){
					this.core.applicaFiltro(source, name, value, value.getClass());
					return true; 
				}
			}
		}
		
		return (value == null); // questa riga e' l'implementazione del NullPropertyFilter
	}  
}
