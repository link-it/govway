package org.openspcoop2.generic_project.web.impl.jsf1.converter;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.generic_project.web.impl.jsf1.utils.Utils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

public class DateConverter  implements Converter {
	public static String DATE_PATTERN = "dd/MM/yyyy";
	//eg 02/02/2012 12:00  (note Rich:calendar has no support for seconds)

	private boolean consentiDataVuota;
	private static Logger log = LoggerWrapperFactory.getLogger(DateConverter.class.getName());
	
	public DateConverter(){
		init(false);
	}
	
	public DateConverter(boolean consentiDataVuota){
		init(consentiDataVuota);
	}
	
	private void init(boolean consentiDataVuota){
		this.consentiDataVuota= consentiDataVuota;
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value)
			throws ConverterException {
		Date result = null;
		log.debug("getAsObject ["+value+"]"); 

		if(StringUtils.isEmpty(value)){
			if(!this.isConsentiDataVuota()){
				String msg = Utils.getInstance().getMessageWithParamsFromResourceBundle("commons.dataObbligatoria",value);
				FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,msg,null);
				throw new ConverterException(m);
			}
			else 
				return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		if(value!= null && value.length() > 0) {
			try {
				result = sdf.parse(value);
			} catch (Exception e) {
				log.error(e.getMessage());
				String msg = Utils.getInstance().getMessageWithParamsFromResourceBundle("commons.formatoDataNonValido",value, DATE_PATTERN);
				FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,msg,null);
				throw new ConverterException(m);
			}
		}

		return result;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value)
			throws ConverterException {

		log.debug("getAsString ["+value+"]");
		String result = "";
		if(value == null)
			return result;

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		if(value instanceof String){
			String valueStr = (String) value;

			if (valueStr!= null && valueStr.length() > 0) {
				try {
					Date date = sdf.parse(valueStr);
					result = sdf.format(date);
				} catch (Exception e) {
					log.error(e.getMessage());
					String msg = Utils.getInstance().getMessageWithParamsFromResourceBundle("commons.formatoDataNonValido",value, DATE_PATTERN);
					FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,msg,null);
					throw new ConverterException(m);
				}
			}

		}

		if(value instanceof Date){
			try {
				result = sdf.format(value);
			} catch (Exception e) {
				String msg = Utils.getInstance().getMessageWithParamsFromResourceBundle("commons.formatoDataNonValido",value, DATE_PATTERN);
				FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,msg,null);
				throw new ConverterException(m);
			}
		}
		return result;
	}

	public boolean isConsentiDataVuota() {
		return this.consentiDataVuota;
	}

	public void setConsentiDataVuota(boolean consentiDataVuota) {
		this.consentiDataVuota = consentiDataVuota;
	}

}