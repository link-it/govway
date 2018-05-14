package org.openspcoop2.web.monitor.core.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class EmailValidator implements Validator{
	
	public static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	
	Pattern emailPattern = null;
	
	
	public EmailValidator() {
		this.emailPattern = Pattern.compile(EMAIL_PATTERN);
	}	
	
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value)	throws ValidatorException {

		validaEMail(value, this.emailPattern, context);
		 
	}


	public static void validaEMail(Object value, Pattern emailPattern, FacesContext context){
		String errorMsg = null;
		
		if(value == null){
			errorMsg = "Il campo Email non può essere vuoto.";
		}
		
		String email = (String) value;
		Matcher matcher = emailPattern.matcher(email);
		
        if(!matcher.matches()){
        	errorMsg = "Il campo Email non contiene un valore valido.";
        }

		if(errorMsg != null) {
			Utils.throwValidatorException(context, errorMsg);
		}
	}
	
}
