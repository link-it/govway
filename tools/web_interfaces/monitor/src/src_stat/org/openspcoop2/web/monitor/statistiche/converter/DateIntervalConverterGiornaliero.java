package org.openspcoop2.web.monitor.statistiche.converter;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class DateIntervalConverterGiornaliero implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent component, Object value) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ITALIAN);

		return sdf.format(value);
	}

}
