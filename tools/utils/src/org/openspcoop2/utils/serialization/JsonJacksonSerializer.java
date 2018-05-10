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
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
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


	/**
	 * 
	 */
	private static final String DEFAULT = "__default";
	private ObjectWriter writer;

	public JsonJacksonSerializer(Filter filter){

		this(filter,null,null);
	}
	public JsonJacksonSerializer(Filter filter,IDBuilder idBuilder){
		this(filter,idBuilder,null);
	}
	public JsonJacksonSerializer(Filter filter,String [] excludes){
		this(filter,null,excludes);
	}
	
	@JsonIgnoreType
	private class MyMixInForIgnoreType {}

	public JsonJacksonSerializer(Filter filter,IDBuilder idBuilder, String [] excludes){

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

		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));		

		SimpleFilterProvider filters = new SimpleFilterProvider();
		if((filter != null && filter.sizeFiltersByName()>0) || excludes != null) {
			filters = filters.addFilter(DEFAULT, new JacksonSimpleBeanPropertyFilter(filter, excludes, idBuilder, new JsonJacksonSerializer(new Filter())));
		}

		if(filter != null && filter.sizeFiltersByValue()>0) {
			for(Class<?> value: filter.getFilterByValue()) {
				mapper.addMixIn(value, MyMixInForIgnoreType.class);
			}
		}

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
