package org.openspcoop2.web.monitor.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class NoneAllConverter implements Converter {
	
	private final static String NONE_STRING = "[Nessuno]";
	private final static String ALL_STRING  = "[Qualsiasi]";
	
	@Override
	public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
		
		if(value==null || NoneAllConverter.NONE_STRING.equals(value))
			return null;
		
		if(NoneAllConverter.ALL_STRING.equals(value))
			return "*";
			
		return value;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent component, Object value) {
		
		if(value==null || "".equals(value))
			return NoneAllConverter.NONE_STRING;
		
		if("*".equals(value))
			return NoneAllConverter.ALL_STRING;
		
		return value.toString();
	}

}
