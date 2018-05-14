package org.openspcoop2.web.monitor.statistiche.converter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class DateIntervalConverterOrario implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent component, Object value) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH", Locale.ITALIAN);
		SimpleDateFormat sdf_last_hour = new SimpleDateFormat("HH",Locale.ITALIAN);

		Calendar c = Calendar.getInstance();
		c.setTime((Date)value);
		c.add(Calendar.HOUR, +1);
		return sdf.format(value)+"-"+sdf_last_hour.format(c.getTime());
	}

}
