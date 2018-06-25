/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * MultiEmailValidator
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
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
