package org.openspcoop2.web.monitor.core.validator;

import org.openspcoop2.web.monitor.core.logger.LoggerManager;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.slf4j.Logger;
import org.openspcoop2.message.xml.XPathExpressionEngine;
import org.openspcoop2.utils.xml.XPathNotValidException;


public class XPathValidator implements Validator {

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {

		try{
			XPathExpressionEngine xpathEngine = new XPathExpressionEngine();
			String xpath = (String)value;
			xpathEngine.validate(xpath);
		}catch(XPathNotValidException e){
			XPathValidator.log.error(e.getMessage(), e);
			FacesMessage message = new FacesMessage();
			message.setSummary(e.getMessage());
			//message.setDetail(nve.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}

	}

}
