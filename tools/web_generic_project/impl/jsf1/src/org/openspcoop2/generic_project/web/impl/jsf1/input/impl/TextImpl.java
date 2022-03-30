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
package org.openspcoop2.generic_project.web.impl.jsf1.input.impl;

import org.openspcoop2.generic_project.web.impl.jsf1.input.BaseFormField;
import org.openspcoop2.generic_project.web.impl.jsf1.utils.Utils;
import org.openspcoop2.generic_project.web.input.FieldType;
import org.openspcoop2.generic_project.web.input.Text;

/***
 * 
 * Implementazione base di un elemento di tipo Text.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class TextImpl extends BaseFormField<String> implements Text{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TextImpl() {
		super();

		this.setType(FieldType.TEXT);
	}
	
	@Override
	public String getDefaultValue() {
		String dV = super.getDefaultValue();
		if(dV != null){
			try{
				String tmp = Utils.getInstance().getMessageFromResourceBundle(dV);

				if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
					return tmp;
			}catch(Exception e){}
		}
		return dV;
	}

	@Override
	public String getDefaultValue2() {
		String dV = super.getDefaultValue2();
		if(dV != null){
			try{
				String tmp = Utils.getInstance().getMessageFromResourceBundle(dV);

				if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
					return tmp;
			}catch(Exception e){}
		}
		return dV;
	}
}
