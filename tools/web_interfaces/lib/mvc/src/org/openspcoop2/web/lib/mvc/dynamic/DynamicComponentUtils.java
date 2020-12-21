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
package org.openspcoop2.web.lib.mvc.dynamic;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.parameters.CalendarParameter;
import org.openspcoop2.monitor.sdk.parameters.CheckBoxParameter;
import org.openspcoop2.monitor.sdk.parameters.InputSecretParameter;
import org.openspcoop2.monitor.sdk.parameters.InputTextParameter;
import org.openspcoop2.monitor.sdk.parameters.OutputTextParameter;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.monitor.sdk.parameters.RadioButtonParameter;
import org.openspcoop2.monitor.sdk.parameters.SelectListParameter;
import org.openspcoop2.monitor.sdk.parameters.TextAreaParameter;
import org.openspcoop2.web.lib.mvc.dynamic.components.BaseComponent;
import org.openspcoop2.web.lib.mvc.dynamic.components.Calendar;
import org.openspcoop2.web.lib.mvc.dynamic.components.CheckBox;
import org.openspcoop2.web.lib.mvc.dynamic.components.InputSecret;
import org.openspcoop2.web.lib.mvc.dynamic.components.InputText;
import org.openspcoop2.web.lib.mvc.dynamic.components.OutputText;
import org.openspcoop2.web.lib.mvc.dynamic.components.RadioButton;
import org.openspcoop2.web.lib.mvc.dynamic.components.SelectList;
import org.openspcoop2.web.lib.mvc.dynamic.components.TextArea;

/**
 * DynamicComponentUtils
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DynamicComponentUtils {

	private static Logger log = LoggerWrapperFactory.getLogger(DynamicComponentUtils.class);
		
	public static <T extends BaseComponent<?>> Parameter<?> createDynamicComponentParameter(
			Parameter<?> parameter, IDynamicLoader bl)
			throws Exception {

		// ComponentType type = component.getType();

		// component.setLoader(bl);
		
		log.debug("Id Componente ["+parameter.getId()+"]");
		log.debug("Label Componente ["+parameter.getRendering().getLabel()+"]");
		log.debug("Tipo Componente ["+parameter.getType()+"]"); 
		
		if(parameter.getType() == null){
			log.debug("Tipo Componente Null");
		}
			

		switch (parameter.getType()) {
		case CALENDAR:
			return new Calendar((CalendarParameter)parameter, bl);
		case CHECK_BOX:
			return new CheckBox((CheckBoxParameter)parameter, bl);
		case INPUT_SECRET:
			return new InputSecret((InputSecretParameter)parameter, bl);
		case INPUT_TEXT:
			return new InputText((InputTextParameter)parameter, bl);
		case OUTPUT_TEXT:
			return new OutputText((OutputTextParameter)parameter, bl);
		case RADIO_BUTTON:
			return new RadioButton((RadioButtonParameter)parameter, bl);
		case SELECT_LIST:
			return new SelectList((SelectListParameter)parameter, bl);
		case TEXT_AREA:
			return new TextArea((TextAreaParameter)parameter, bl);
		default:
			throw new Exception("Tipo del componente ["+parameter.getType()+"] non gestito");
		}
	}


}
