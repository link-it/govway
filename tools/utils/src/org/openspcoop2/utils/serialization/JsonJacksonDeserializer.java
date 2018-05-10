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

import java.io.InputStream;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

/**	
 * Contiene utility per effettuare la de-serializzazione di un oggetto
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13574 $, $Date: 2018-01-26 12:24:34 +0100(ven, 26 gen 2018) $
 */

public class JsonJacksonDeserializer implements IDeserializer{

	
	private static class BeanDeserializerModifierForIgnorables extends BeanDeserializerModifier {

        private List<String> ignorables;

        public BeanDeserializerModifierForIgnorables(String... properties) {
            this.ignorables = new ArrayList<>();
            if(properties != null) {
                for(String property : properties) {
                    this.ignorables.add(property);
                }
            }
        }

        @Override
        public BeanDeserializerBuilder updateBuilder(
                DeserializationConfig config, BeanDescription beanDesc,
                BeanDeserializerBuilder builder) {

            for(String ignorable : this.ignorables) {
                builder.addIgnorable(ignorable);                
            }

            return builder;
        }

        @Override
        public List<BeanPropertyDefinition> updateProperties(
                DeserializationConfig config, BeanDescription beanDesc,
                List<BeanPropertyDefinition> propDefs) {

            List<BeanPropertyDefinition> newPropDefs = new ArrayList<>();
            for(BeanPropertyDefinition propDef : propDefs) {
                if(!this.ignorables.contains(propDef.getName())) {
                    newPropDefs.add(propDef);
                }
            }
            return newPropDefs;
        }
    }


	ObjectMapper mapper;

	public JsonJacksonDeserializer(){
		this(null);
	}
	
	public JsonJacksonDeserializer(String [] excludes){		
		BeanDeserializerModifier modifier = new BeanDeserializerModifierForIgnorables(excludes);
		DeserializerFactory dFactory = BeanDeserializerFactory.instance.withDeserializerModifier(modifier);

		this.mapper = new ObjectMapper(null, null, new DefaultDeserializationContext.Impl(dFactory));
		this.mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));		

	}

	@Override
	public Object getObject(String s, Class<?> classType) throws IOException{
		try {
			return this.mapper.readValue(s, classType);
		} catch (java.io.IOException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Object readObject(InputStream is, Class<?> classType) throws IOException {
		try {
			return this.mapper.readValue(is, classType);
		} catch (java.io.IOException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Object readObject(Reader reader, Class<?> classType) throws IOException {
		try {
			return this.mapper.readValue(reader, classType);
		} catch (java.io.IOException e) {
			throw new IOException(e);
		}
	}
}
