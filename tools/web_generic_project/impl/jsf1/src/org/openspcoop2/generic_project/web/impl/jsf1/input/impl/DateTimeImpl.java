/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
package org.openspcoop2.generic_project.web.impl.jsf1.input.impl;

import java.util.Date;

import org.openspcoop2.generic_project.web.impl.jsf1.input.BaseFormField;
import org.openspcoop2.generic_project.web.input.DateTime;
import org.openspcoop2.generic_project.web.input.FieldType;

/***
 * 
 * Implementazione base di un elemento di tipo DateTime.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class DateTimeImpl extends BaseFormField<Date> implements DateTime {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String buttonIcon = "";
	
	public DateTimeImpl(){
		super();

		this.setType(FieldType.DATE);
		this.setPattern("dd/MM/yyyy HH:mm:ss");
		this.setButtonIcon("/images/tema_link/calendar.png"); 
	}

	public String getButtonIcon() {
		return this.buttonIcon;
	}

	public void setButtonIcon(String buttonIcon) {
		this.buttonIcon = buttonIcon;
	}
	
}
