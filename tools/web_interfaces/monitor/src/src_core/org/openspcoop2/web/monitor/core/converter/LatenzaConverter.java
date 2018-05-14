package org.openspcoop2.web.monitor.core.converter;

import org.openspcoop2.web.monitor.core.logger.LoggerManager;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.slf4j.Logger;

public class LatenzaConverter  implements Converter {

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
		return null;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent component, Object value) {

		try{
			if(value!=null){
				if(value instanceof Date){
					long t = ((Date)value).getTime();
					if(t >=0)
						return DurataConverter.convertSystemTimeIntoString_millisecondi(t, true);
				}
				if(value instanceof Long){
					long t = ((Long)value).longValue();
					if(t >=0)
						return DurataConverter.convertSystemTimeIntoString_millisecondi(t, true);
				}
			} else {
				return "N.D.";
			}
		}catch(Exception e){
			log.error(e.getMessage(), e); 
		}

		return "N.D.";
	}



}
