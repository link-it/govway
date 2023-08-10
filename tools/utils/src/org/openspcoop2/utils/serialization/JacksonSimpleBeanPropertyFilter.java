/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.SerializeExceptFilter;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class JacksonSimpleBeanPropertyFilter extends SerializeExceptFilter {

	
	private static final long serialVersionUID = 1L;
	private transient PropertyFilterCore core;
	private List<String> excludes;
	private List<Class<?>> excludesByClass;
	
	public JacksonSimpleBeanPropertyFilter(SerializationConfig config,ISerializer serializer){
		super(getHashSet(config.getFilter(), config.getExcludes()));
		this.core = new PropertyFilterCore(config.getFilter(), config.getIdBuilder(), serializer);
		this.excludes = config.getExcludes();
		if(config.getFilter()!=null && config.getFilter().getFilterByValue()!=null) {
			this.excludesByClass = config.getFilter().getFilterByValue();
		} else {
			this.excludesByClass = new ArrayList<>();
		}
	}

	private static Set<String> getHashSet(Filter filter, List<String> excludes) {
		Set<String> set = new HashSet<>();
		if(filter!= null && filter.sizeFiltersByName()>0)
			set.addAll(filter.getFilterByName());
		if(excludes !=null && !excludes.isEmpty()) {
			set.addAll(excludes);
		}

		return set;
	}

    @Override
    public void serializeAsField(Object pojo, JsonGenerator gen, SerializerProvider provider, PropertyWriter writer)
            throws Exception {
    	
        if (include(writer) && !this.excludesByClass.contains(writer.getType().getRawClass())) {
        	writer.serializeAsField(pojo, gen, provider);
        } else {
        	if (gen.canOmitFields()) { // since 2.3
        		writer.serializeAsOmittedField(pojo, gen, provider);
        	}
            if(this.excludes == null || !this.excludes.contains(writer.getName()))
            	this.core.applicaFiltro(pojo, writer.getName(), pojo, writer.getType().getRawClass());
        }
    }

}
