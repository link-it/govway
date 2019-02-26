/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.generic_project.web.impl.jsf1.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.openspcoop2.generic_project.web.impl.jsf1.utils.Utils;
//import org.openspcoop2.utils.LoggerWrapperFactory;
//import org.slf4j.Logger;

/**
 * NumberConverter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NumberConverter implements Converter{

	//private static Logger log = LoggerWrapperFactory.getLogger(NumberConverter.class.getName());

	public static String NUMBER_PATTERN ="\\d+";
	public static String TWO_DIGITS_PATTERN = "^[0-9]+(\\,[0-9]{1,2})?$";
	//private static String EURO_PATTERN = "^\\s*-?((\\d{1,3}(\\.(\\d){3})*)|\\d*)(,\\d{1,2})?\\s?(\\u20AC)?\\s*$";

	private Pattern pattern = null;

	public NumberConverter() {
		this(NUMBER_PATTERN);
	}
	
	public NumberConverter(String pattern) {
		this.pattern = Pattern.compile(pattern);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException{
		if(value == null)
			return null;

		//log.debug("getAsObject: " + value);
		
		// Provo la validazione numero intero
		Matcher m = this.pattern.matcher(value);

		if(m.matches())
			return (Number) Long.parseLong(value);

		
		String msg = Utils.getInstance().getMessageWithParamsFromResourceBundle("commons.formatoNonValidoConParametri",value);
		FacesMessage mm = new FacesMessage(FacesMessage.SEVERITY_ERROR,msg,null);
		throw new ConverterException(mm);


	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value)  throws ConverterException{

		if(value == null)
			return null;

		//log.debug("getAsString: " + value);
		return "" + value;
	}

}