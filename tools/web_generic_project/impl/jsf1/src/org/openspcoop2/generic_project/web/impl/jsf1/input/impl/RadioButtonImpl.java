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
package org.openspcoop2.generic_project.web.impl.jsf1.input.impl;

import org.openspcoop2.generic_project.web.factory.Costanti;
import org.openspcoop2.generic_project.web.input.FieldType;
import org.openspcoop2.generic_project.web.input.RadioButton;

/***
 * 
 * Implementazione base di un elemento di tipo Radio Button.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class RadioButtonImpl extends SingleChoiceImpl implements RadioButton {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RadioButtonImpl(){
		super();
		this.setDirezione(Costanti.CHOICE_ORIENTATION_HORIZONTAL);
		this.setType(FieldType.RADIO_BUTTON);
		this.setConverterName("selectItemConverter");
	}
}
