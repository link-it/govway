/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import org.openspcoop2.web.monitor.core.logger.LoggerManager;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.slf4j.Logger;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.xml.XPathExpressionEngine;
import org.openspcoop2.utils.xml.XPathNotValidException;

/**
 * XPathValidator
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class XPathValidator implements Validator {

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {

		try{
			XPathExpressionEngine xpathEngine = new XPathExpressionEngine(OpenSPCoop2MessageFactory.getDefaultMessageFactory());
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
