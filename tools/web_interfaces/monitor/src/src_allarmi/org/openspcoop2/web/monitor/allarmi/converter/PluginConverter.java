/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

package org.openspcoop2.web.monitor.allarmi.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.openspcoop2.monitor.engine.config.base.Plugin;

/**     
 * PluginConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PluginConverter extends org.openspcoop2.web.monitor.core.converter.PluginConverter {
	
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value == null) {
			return org.openspcoop2.web.monitor.core.converter.PluginConverter.NONE_STRING;
		}

		if(value instanceof Plugin){
			Plugin facesSelectItem = (Plugin) value;
			return facesSelectItem.getLabel();
		}
		else{
			return super.getAsString(context, component, value);
		}

	}

}
