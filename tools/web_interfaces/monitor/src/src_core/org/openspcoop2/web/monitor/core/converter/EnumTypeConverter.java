package org.openspcoop2.web.monitor.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.lang.StringUtils;

public class EnumTypeConverter implements Converter {

	@Override
	@SuppressWarnings({ "unchecked" })
	public Object getAsObject(FacesContext context, UIComponent comp,
			String value) throws ConverterException {
		if(StringUtils.isEmpty(value))
			return null;
		
		@SuppressWarnings("rawtypes")
		Class enumType = comp
							.getValueExpression("value")
							.getType(context.getELContext());
		return Enum.valueOf(enumType, value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object object) throws ConverterException {

		if (object == null) {
			return null;
		}
		
		if(object instanceof String)
			return object.toString();
		
		if(object instanceof Enum){
			@SuppressWarnings("rawtypes")
			Enum type = (Enum) object;
			return type.toString();
		}
		
		return object.toString();
	}

}
