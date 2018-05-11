package org.openspcoop2.monitor.sdk.parameters;

import java.lang.reflect.Method;

import org.openspcoop2.monitor.sdk.constants.ParameterType;
import org.openspcoop2.monitor.sdk.exceptions.ParameterException;

/**
 * CheckBoxParameter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CheckBoxParameter extends Parameter<Boolean> {

	public CheckBoxParameter(String id) {
		super(id, ParameterType.CHECK_BOX);
	}

	@Override
	public void setValueAsString(String value) throws ParameterException{
		if(value!=null){
			try{
				Class<?> c = Class.forName("org.openspcoop2.monitor.framework.ContentFormatter");
				Method m = c.getMethod("toBoolean", String.class);
				Boolean b = (Boolean) m.invoke(null, value);
				this.setValue(b);
			}
			catch(Exception e){
				throw new ParameterException("Value ["+value+"] uncorrected: "+e.getMessage(),e);
			}
		}
	}
	
	@Override
	public String getValueAsString() throws ParameterException{
		if(this.getValue()!=null){
			try{
				Class<?> c = Class.forName("org.openspcoop2.monitor.framework.ContentFormatter");
				Method m = c.getMethod("toString", Boolean.class);
				String s = (String) m.invoke(null, this.getValue());
				return s;
			}
			catch(Exception e){
				throw new ParameterException("Error occurs: "+e.getMessage(),e);
			}
		}
		return null;
	}
}
