/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * DateIntervalConverterMensile
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DateIntervalConverterMensile implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent component, Object value) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.ITALIAN);

		return sdf.format(value);
	}

}
