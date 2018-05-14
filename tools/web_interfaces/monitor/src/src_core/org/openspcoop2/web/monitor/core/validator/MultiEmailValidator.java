package org.openspcoop2.web.monitor.core.validator;

import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class MultiEmailValidator implements Validator{
	
	Pattern emailPattern = null;
	
	public MultiEmailValidator() {
		this.emailPattern = Pattern.compile(EmailValidator.EMAIL_PATTERN);
	}	
	
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value)	throws ValidatorException {

		if(value == null){
			Utils.throwValidatorException(context, "Il campo Email non può essere vuoto.");
		}
		
		String email = (String) value;
		String [] tmp = email.split(",");
		for (int i = 0; i < tmp.length; i++) {
			EmailValidator.validaEMail(tmp[i].trim(), this.emailPattern, context);	
		}
		 
	}


}
