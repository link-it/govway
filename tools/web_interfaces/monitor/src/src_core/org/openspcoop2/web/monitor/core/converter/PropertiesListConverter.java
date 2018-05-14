package org.openspcoop2.web.monitor.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class PropertiesListConverter implements Converter {

	
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String val) {
		
		if(val!=null){
			String res = val.replaceAll("\\r\\n", ",");
							
			
			return res;
		}
		
		return null;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object val) {
		if(val!=null){
			
			String s = (String)val;
			String res = s.replaceAll(",", "\n");
			return res;
		}
		return null;
	}

}
