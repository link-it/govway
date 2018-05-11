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


package org.openspcoop2.utils.serialization;

import java.io.OutputStream;
import java.io.Writer;
import java.util.HashSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;



/**	
 * Contiene utility per effettuare la serializzazione di un oggetto
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13574 $, $Date: 2018-01-26 12:24:34 +0100(ven, 26 gen 2018) $
 */

public class JsonJacksonSerializer implements ISerializer {


////	@JsonIgnoreType
//	public class MyMixInForIgnoreType {}
	private static final String DEFAULT = "__default";
	private ObjectWriter writer;

	
	public JsonJacksonSerializer() {
		this(new SerializationConfig());
	}
	public JsonJacksonSerializer(SerializationConfig config) {
		ObjectMapper mapper = new ObjectMapper().setAnnotationIntrospector(
				new AnnotationIntrospectorPair(
						new JacksonAnnotationIntrospector() {

							private static final long serialVersionUID = 1L;

							@Override
							public String findFilterId(Annotated a) {
								return DEFAULT;
							}

						}, new JaxbAnnotationIntrospector(TypeFactory.defaultInstance())
						)
				);

		mapper.setDateFormat(config.getDf());		

		SimpleFilterProvider filters = new SimpleFilterProvider();
		if((config.getFilter() != null && config.getFilter().sizeFiltersByName()>0) || config.getExcludes() != null) {
			filters = filters.addFilter(DEFAULT, new JacksonSimpleBeanPropertyFilter(config, new JsonJacksonSerializer()));
		} else if(config.getIncludes()!=null) {
			HashSet<String> hashSet = new HashSet<String>();
			hashSet.addAll(config.getIncludes());
			filters = filters.addFilter(DEFAULT, SimpleBeanPropertyFilter.filterOutAllExcept(hashSet));
		}

//		if(config.getFilter() != null && config.getFilter().sizeFiltersByValue()>0) {
//			for(Class<?> value: config.getFilter().getFilterByValue()) {
//				mapper.addMixIn(value, MyMixInForIgnoreType.class);
//			}
//		}

		filters = filters.setFailOnUnknownId(false);

		mapper.setFilterProvider(filters);
		this.writer = mapper.writer();
	}

	@Override
	public String getObject(Object o) throws IOException{
		try{
			return this.writer.writeValueAsString(o);
		}catch(Exception e){
			throw new IOException(e.getMessage(),e);
		}
	}

	@Override
	public void writeObject(Object o,OutputStream out) throws IOException{
		try{
			this.writer.writeValue(out, o);
		}catch(Exception e){
			throw new IOException(e.getMessage(),e);
		}
	}

	@Override
	public void writeObject(Object o,Writer out) throws IOException{
		try{
			this.writer.writeValue(out, o);
		}catch(Exception e){
			throw new IOException(e.getMessage(),e);
		}
	}

}
