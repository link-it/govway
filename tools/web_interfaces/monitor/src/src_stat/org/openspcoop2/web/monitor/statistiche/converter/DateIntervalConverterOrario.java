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
package org.openspcoop2.web.monitor.statistiche.converter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.openspcoop2.web.monitor.core.bean.ApplicationBean;

/**
 * DateIntervalConverterOrario
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DateIntervalConverterOrario implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent component, Object value) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH", ApplicationBean.getInstance().getLocale());
		SimpleDateFormat sdf_last_hour = new SimpleDateFormat("HH", ApplicationBean.getInstance().getLocale());

		Calendar c = Calendar.getInstance();
		c.setTime((Date)value);
		c.add(Calendar.HOUR, +1);
		return sdf.format(value)+"-"+sdf_last_hour.format(c.getTime());
	}

}
