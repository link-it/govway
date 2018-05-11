package org.openspcoop2.monitor.sdk.parameters;

import org.openspcoop2.monitor.sdk.constants.ParameterType;
import org.openspcoop2.monitor.sdk.exceptions.ParameterException;

/**
 * OutputTextParameter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OutputTextParameter extends Parameter<String> {

	public OutputTextParameter(String id) {
		super(id, ParameterType.OUTPUT_TEXT);
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
