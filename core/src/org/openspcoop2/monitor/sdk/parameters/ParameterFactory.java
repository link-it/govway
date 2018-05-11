package org.openspcoop2.monitor.sdk.parameters;

import org.openspcoop2.monitor.sdk.constants.ParameterType;
import org.openspcoop2.monitor.sdk.exceptions.ParameterException;

/**
 * ParameterFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ParameterFactory {

	public static Parameter<?> createParameter(ParameterType type,String id) throws ParameterException{
		switch (type) {
		case CHECK_BOX:
			return new CheckBoxParameter(id);			
		case CALENDAR:
			return new CalendarParameter(id);
		case INPUT_SECRET:
			return new InputSecretParameter(id);
		case INPUT_TEXT:
			return new InputTextParameter(id);
		case OUTPUT_TEXT:
			return new OutputTextParameter(id);
		case RADIO_BUTTON:
			return new RadioButtonParameter(id);
		case SELECT_LIST:
			return new SelectListParameter(id);
		case TEXT_AREA:
			return new TextAreaParameter(id);			
		}
		throw new ParameterException("Unsupported type ["+type+"] for parameter "+id);
	}
	
}
