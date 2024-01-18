/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.io.OutputStream;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;


/**	
 * Contiene utility per effettuare la serializzazione di un oggetto
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class XMLSerializer implements ISerializer {

	private JsonConfig jsonConfig;
	private net.sf.json.xml.XMLSerializer xmlSerializer;

	public XMLSerializer(){
		this(new SerializationConfig());
	}
	
	public XMLSerializer(SerializationConfig config){
		XMLSerializer filtroInternoOggettiFiltratiDiversiByteArray = null;
		if(config.getFilter()!=null) {
			if(config.getFilter().sizeFiltersByValue()>0 || config.getFilter().sizeFiltersByName()>0) {
				
				SerializationConfig conf = new SerializationConfig();
				conf.setFilter(new Filter());
				conf.setExcludes(config.getExcludes());
				conf.setIdBuilder(config.getIdBuilder());
				filtroInternoOggettiFiltratiDiversiByteArray = new XMLSerializer(conf);
			}
		}
		
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setJsonPropertyFilter(new PropertyFilter(config.getFilter(),config.getIdBuilder(),filtroInternoOggettiFiltratiDiversiByteArray));
		if(config.getExcludes()!=null){
			jsonConfig.setExcludes(config.getExcludes().toArray(new String[]{}));
		}
		this.jsonConfig = jsonConfig;
		this.xmlSerializer = new net.sf.json.xml.XMLSerializer();
	}
	
	
	@Override
	public String getObject(Object o) throws IOException{
		try{
			Utilities.normalizeDateObjects(o);
			if( (o instanceof Enum) ){
				JSONArray jsonArray = JSONArray.fromObject( o , this.jsonConfig );
				return this.xmlSerializer.write(jsonArray);
			}
			else if( (o != null && o.getClass().isArray()) ){
				JSONArray jsonArray = new JSONArray();
				Object[] array = (Object[]) o;
				for (int i = 0; i < array.length; i++) {
					Object object = array[i];
					jsonArray.add(JSONObject.fromObject( object , this.jsonConfig ));
				}
				return this.xmlSerializer.write(jsonArray);
			}
			else if( (o instanceof List) ){
				JSONArray jsonArray = new JSONArray();
				List<?> list = (List<?>) o;
				for (int i = 0; i < list.size(); i++) {
					Object object = list.get(i);
					jsonArray.add(JSONObject.fromObject( object , this.jsonConfig ));
				}
				return this.xmlSerializer.write(jsonArray);
			}
			else if( (o instanceof Set) ){
				Set<?> set = (Set<?>) o;
				return this.getObject(set.toArray());
			}
			else if((o instanceof Annotation) || o != null && o.getClass().isAnnotation())
				throw new IOException("'object' is an Annotation.");
			else {
				JSONObject jsonObject = JSONObject.fromObject( o , this.jsonConfig );
				return this.xmlSerializer.write(jsonObject);
			}
		}catch(Exception e){
			throw new IOException(e.getMessage(),e);
		}
	}
	
	@Override
	public void writeObject(Object o,OutputStream out) throws IOException{
		try{
			//Utilities.normalizeDateObjects(o);
			String s = this.getObject(o);
			out.write(s.getBytes());
		}catch(Exception e){
			throw new IOException(e.getMessage(),e);
		}
	}
	
	@Override
	public void writeObject(Object o,Writer out) throws IOException{
		try{
			//Utilities.normalizeDateObjects(o);
			String s = this.getObject(o);
			out.write(s);
		}catch(Exception e){
			throw new IOException(e.getMessage(),e);
		}
	}
}
