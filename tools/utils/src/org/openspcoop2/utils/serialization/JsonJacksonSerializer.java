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
import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationIntrospector;



/**	
 * Contiene utility per effettuare la serializzazione di un oggetto
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class JsonJacksonSerializer implements ISerializer {


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

						}, new JakartaXmlBindAnnotationIntrospector(TypeFactory.defaultInstance())
						)
				);

		if(config.isSerializeEnumAsString())
			mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);

    	mapper.setDateFormat(config.getDf());		
		
		if(config.getIgnoreNullValues() == null || config.getIgnoreNullValues())
			mapper.setSerializationInclusion(Include.NON_NULL);
		
		SimpleFilterProvider filters = new SimpleFilterProvider();
		if( (
				config.getFilter() != null 
				&& 
				( (config.getFilter().sizeFiltersByName()>0) || (config.getFilter().sizeFiltersByValue()>0))
			)
				|| 
			config.getExcludes() != null) {
			filters = filters.addFilter(DEFAULT, new JacksonSimpleBeanPropertyFilter(config, new JsonJacksonSerializer()));
		} else if(config.getIncludes()!=null) {
			HashSet<String> hashSet = new HashSet<>();
			hashSet.addAll(config.getIncludes());
			filters = filters.addFilter(DEFAULT, SimpleBeanPropertyFilter.filterOutAllExcept(hashSet));
		}
		
		filters = filters.setFailOnUnknownId(false);

		mapper.setFilterProvider(filters);
		if(config.isPrettyPrint()) {
			this.writer = mapper.writer().withDefaultPrettyPrinter();
		}
		else {
			this.writer = mapper.writer();
		}
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
