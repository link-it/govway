package org.openspcoop2.monitor.sdk.parameters;

import org.openspcoop2.monitor.sdk.constants.ParameterType;
import org.openspcoop2.monitor.sdk.exceptions.ParameterException;

/**
 * SelectListParameter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SelectListParameter extends Parameter<String> {

	public SelectListParameter(String id) {
		super(id, ParameterType.SELECT_LIST);
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
