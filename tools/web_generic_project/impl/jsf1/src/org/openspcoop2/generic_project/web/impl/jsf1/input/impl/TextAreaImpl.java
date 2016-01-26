/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
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
package org.openspcoop2.generic_project.web.impl.jsf1.input.impl;

import org.openspcoop2.generic_project.web.input.FieldType;
import org.openspcoop2.generic_project.web.input.TextArea;

/***
 * 
 * Implementazione base di un elemento di tipo TextArea.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class TextAreaImpl extends TextImpl implements TextArea{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int columns = 30;
	private int rows = 4;

	@Override
	public int getColumns() {
		return this.columns;
	}

	@Override
	public void setColumns(int columns) {
		this.columns = columns;
	}

	@Override
	public int getRows() {
		return this.rows;
	}

	@Override
	public void setRows(int rows) {
		this.rows = rows;
	}

	public TextAreaImpl() {
		super();

		this.setType(FieldType.TEXT_AREA);
	}
}
