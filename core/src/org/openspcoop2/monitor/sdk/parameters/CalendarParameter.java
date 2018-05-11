package org.openspcoop2.monitor.sdk.parameters;

import java.lang.reflect.Method;
import java.util.Date;

import org.openspcoop2.monitor.sdk.constants.ParameterType;
import org.openspcoop2.monitor.sdk.exceptions.ParameterException;

/**
 * CalendarParameter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CalendarParameter extends Parameter<Date> {

	public CalendarParameter(String id) {
		super(id, ParameterType.CALENDAR);
	}

	@Override
	public void setValueAsString(String value) throws ParameterException{
		if(value!=null){
			try{
				Class<?> c = Class.forName("org.openspcoop2.monitor.engine.ContentFormatter");
				Method m = c.getMethod("toDate", String.class);
				Date d = (Date) m.invoke(null, value);
				this.setValue(d);
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
				Class<?> c = Class.forName("org.openspcoop2.monitor.engine.ContentFormatter");
				Method m = c.getMethod("toString", Date.class);
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
