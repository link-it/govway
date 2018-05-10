/**
 * 
 */
package org.openspcoop2.utils.serialization;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.SerializeExceptFilter;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 09 mag 2018 $
 * 
 */
public class JacksonSimpleBeanPropertyFilter extends SerializeExceptFilter {

	
	private static final long serialVersionUID = 1L;
	private PropertyFilterCore core;
	private List<String> excludes;
	
	public JacksonSimpleBeanPropertyFilter(Filter filter, String[] excludes, IDBuilder idBuilder,ISerializer serializer){
		super(getHashSet(filter, excludes));
		this.core = new PropertyFilterCore(filter, idBuilder, serializer);
		if(excludes!=null)
			this.excludes = Arrays.asList(excludes);
	}

	public JacksonSimpleBeanPropertyFilter(Filter filter, String[] excludes, ISerializer serializer){
		this(filter, excludes, null, serializer);
	}

	private static Set<String> getHashSet(Filter filter, String[] excludes) {
		Set<String> set = new HashSet<String>();
		if(filter!= null && filter.sizeFiltersByName()>0)
			set.addAll(filter.getFilterByName());
		if(excludes !=null && excludes.length > 0) {
			set.addAll(Arrays.asList(excludes));
		}

		return set;
	}

    @Override
    public void serializeAsField(Object pojo, JsonGenerator gen, SerializerProvider provider, PropertyWriter writer)
            throws Exception {
    	
        if (include(writer)) {
            writer.serializeAsField(pojo, gen, provider);
        } else if (!gen.canOmitFields()) { // since 2.3
            writer.serializeAsOmittedField(pojo, gen, provider);
            if(this.excludes == null || !this.excludes.contains(writer.getName()))
            	this.core.applicaFiltro(pojo, writer.getName(), pojo, writer.getClass());
        }
    }

}
