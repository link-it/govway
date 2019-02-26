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

import org.openspcoop2.utils.checksum.ChecksumAdler;
import org.openspcoop2.utils.checksum.ChecksumCRC;

/**	
 * Contiene classe per effettuare un filtro JSON
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class PropertyFilterCore {

	private IDBuilder idBuilder = null;
	private Filter filter = null;
	private ISerializer serializer = null;
	
	public IDBuilder getIdBuilder() {
		return this.idBuilder;
	}
	public void setIdBuilder(IDBuilder idBuilder) {
		this.idBuilder = idBuilder;
	}
	public Filter getFilter() {
		return this.filter;
	}
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	public ISerializer getSerializer() {
		return this.serializer;
	}
	public void setSerializer(ISerializer serializer) {
		this.serializer = serializer;
	}
	public PropertyFilterCore(Filter filter,IDBuilder idBuilder,ISerializer serializer){
		this.idBuilder = idBuilder;
		this.filter = filter;
		this.serializer = serializer;
	}
	public PropertyFilterCore(Filter filter,ISerializer serializer){
		this.filter = filter;
		this.serializer = serializer;
	}
	
	
	protected void applicaFiltro(Object source, String name, Object value, Class<?> classFilter){
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
