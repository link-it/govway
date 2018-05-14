package org.openspcoop2.web.monitor.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class AllConverter implements Converter {
	
	
	public final static String ALL_STRING  = "[Qualsiasi]";
	//private final static String NONE_STRING = ALL_STRING;
	
	@Override
	public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
		
		if(value==null)
			return AllConverter.ALL_STRING;
		
		if(AllConverter.ALL_STRING.equals(value))
			return "*";
			
		return value;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent component, Object value) {
		
		if(value==null || "".equals(value) || "*".equals(value))
			return AllConverter.ALL_STRING;
		
		
		return value.toString();
	}

}
