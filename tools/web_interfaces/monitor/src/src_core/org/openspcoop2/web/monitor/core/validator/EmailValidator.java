/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.web.monitor.core.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * EmailValidator
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class EmailValidator implements Validator{
	
	protected static final String EMAIL_PATTERN =
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

	private static String checkLenghtMail(String mail) {
		// Refactor this repetition that can lead to a stack overflow for large inputs.
		if(mail!=null) {
			if(mail.length()>300) {
				return "La lunghezza massima permessa di un indirizzo mail sono 300 caratteri";
			}
		}
		return null;
	}

	public static void validaEMail(Object value, Pattern emailPattern, FacesContext context){
		String errorMsg = null;
		
		if(value == null){
			errorMsg = "Il campo Email non può essere vuoto.";
		}
		
		if(value == null){
			errorMsg = "Il campo Email non può essere vuoto.";
		}
		
		String email = (String) value;
		
		String errorLength = checkLenghtMail(email);
		if(errorLength!=null) {
			Utils.throwValidatorException(context, errorLength);
		}
		else {
			Matcher matcher = emailPattern.matcher(email);
			
	        if(!matcher.matches()){
	        	errorMsg = "Il campo Email non contiene un valore valido.";
	        }
	
			if(errorMsg != null) {
				Utils.throwValidatorException(context, errorMsg);
			}
		}
	}
	
}
