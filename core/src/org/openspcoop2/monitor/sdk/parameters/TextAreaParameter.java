package org.openspcoop2.monitor.sdk.parameters;

import org.openspcoop2.monitor.sdk.constants.ParameterType;
import org.openspcoop2.monitor.sdk.exceptions.ParameterException;

/**
 * TextAreaParameter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TextAreaParameter extends Parameter<String> {

	public TextAreaParameter(String id) {
		super(id, ParameterType.TEXT_AREA);
	}

	@Override
	public void setValueAsString(String value) {
		super.setValue(value);
	}
	
	@Override
	public String getValueAsString() throws ParameterException{
		return this.getValue();
	}
}
