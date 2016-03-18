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

import org.openspcoop2.utils.checksum.ChecksumAdler;
import org.openspcoop2.utils.checksum.ChecksumCRC;

/**	
 * Contiene classe per effettuare un filtro JSON
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class PropertyFilter implements net.sf.json.util.PropertyFilter{

	private IDBuilder idBuilder = null;
	private Filter filter = null;
	private ISerializer serializer = null;
	
	public PropertyFilter(Filter filter,IDBuilder idBuilder,ISerializer serializer){
		this.idBuilder = idBuilder;
		this.filter = filter;
		this.serializer = serializer;
	}
	public PropertyFilter(Filter filter,ISerializer serializer){
		this.filter = filter;
		this.serializer = serializer;
	}
	
	@Override
	public boolean apply( Object source, String name, Object value ) {  
		
		// Filtri per valore
		if(value!=null){
			for(int i=0; i<this.filter.sizeFiltersByValue(); i++){
				Class<?> classFilter = this.filter.getFilterByValue(i);
				if(value.getClass().getName().equals(classFilter.getName())){
					applicaFiltro(source, name, value, classFilter);	
					return true; 
				}
			}
		}
		
		
		// Filtri per nome field
		String nomeField = source.getClass().getName()+"."+name;
		for(int i=0; i<this.filter.sizeFiltersByName(); i++){
			String filterName = this.filter.getFilterByName(i);
			if(nomeField.equals(filterName)){			
				if(value!=null){
					applicaFiltro(source, name, value, value.getClass());
					return true; 
				}
			}
		}
		
		return (value == null); // questa riga e' l'implementazione del NullPropertyFilter
	}  
	
	
	private void applicaFiltro(Object source, String name, Object value, Class<?> classFilter){
		try{
			//System.out.println("EXCLUDE["+name+"] ID["+this.idBuilder.toID(source,name)+"]");
			FilteredObject oggettoFiltrato = new FilteredObject();
			
			// Identificatore unico risorsa
			String id = null;
			try{
				id = this.idBuilder.toID(source,name);
			}catch(Exception e){
				// id non esistente per l'oggetto source
				return;
			}
			if(this.filter.existsFilteredObject(id)){
				// La libreria di serializzazione invoca piu' volte il solito oggetto
				return;
			}
			if(this.idBuilder!=null)
				oggettoFiltrato.setId(id);
			
			// Checksum risorsa filtrata
			long checksum = -1;
			byte[] byteOggetto = null;
			if(value instanceof byte[]){
				byteOggetto = (byte[])value;
			}else{
				// Uso serializzazione
				 byteOggetto = this.serializer.getObject(value).getBytes();
			}
			if(FilterChecksumTypes.ADLER.toString().equals(this.filter.getFilterChecksumType().toString())){
				checksum = ChecksumAdler.checksumAdler32(byteOggetto);
			}else{
				checksum = ChecksumCRC.checksumCRC32(byteOggetto);
			}
			oggettoFiltrato.setChecksum(checksum);
			
			// ClassType
			oggettoFiltrato.setClassType(classFilter);
			
			this.filter.addFilteredObject(oggettoFiltrato);
			
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}
}
