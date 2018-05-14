package org.openspcoop2.web.monitor.core.validator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

public class Utils {
	
	public static void throwValidatorException(FacesContext context, String errorMessage){
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		message.setSummary(errorMessage);
		context.addMessage(null, message);
		throw new ValidatorException(message);
	}
}
