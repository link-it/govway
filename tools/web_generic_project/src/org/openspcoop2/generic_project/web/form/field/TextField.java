/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.generic_project.web.form.field;

import org.openspcoop2.generic_project.web.core.Utils;


/**
 * TextField input field di tipo testuale/generico.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TextField extends FormField<String>{

	private int columns = 30;
	private int rows = 4;

	public int getColumns() {
		return this.columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public int getRows() {
		return this.rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public TextField() {
		super();

		this.setType(FieldType.TEXT);
	}

	@Override
	public String getDefaultValue() {
		String dV = super.getDefaultValue();
		if(dV != null){
			try{
				String tmp = Utils.getMessageFromResourceBundle(dV);

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
				String tmp = Utils.getMessageFromResourceBundle(dV);

				if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
					return tmp;
			}catch(Exception e){}
		}
		return dV;
	}
}
