package org.openspcoop2.generic_project.web.impl.jsf1.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.openspcoop2.generic_project.web.impl.jsf1.converter.SelectListItemConverter;

public class RadioButtonConverter extends SelectListItemConverter{
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Object o = super.getAsObject(context, component, value);
		
		return o;
		
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String s = super.getAsString(context, component, value);
		
		return s;
	}

}
