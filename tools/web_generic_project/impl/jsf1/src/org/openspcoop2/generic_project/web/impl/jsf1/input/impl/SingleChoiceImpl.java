/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import org.openspcoop2.generic_project.web.impl.jsf1.input.ChoiceFormField;
import org.openspcoop2.generic_project.web.input.SelectItem;
import org.openspcoop2.generic_project.web.input.FieldType;
import org.openspcoop2.generic_project.web.input.SingleChoice;

/***
 * 
 * Implementazione base di un elemento di tipo Single Choice.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class SingleChoiceImpl extends ChoiceFormField<SelectItem> implements SingleChoice{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String converterName = null;
	
	public SingleChoiceImpl(){
		super();
		setDirezione(Costanti.CHOICE_ORIENTATION_HORIZONTAL);
		setType(FieldType.SINGLE_CHOICE);
	}
	
	public String getConverterName() {
		return this.converterName;
	}

	public void setConverterName(String converterName) {
		this.converterName = converterName;
	}
}
