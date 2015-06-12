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
package org.openspcoop2.generic_project.web.impl.jsf1.table.impl;

import org.openspcoop2.generic_project.web.table.Table;

/***
 * 
 * Implementazione base per una tabella che presenta una lista di valori.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * 
 * @param <V> Lista dei valori da visualizzare.
 */
public class BaseTable<V> implements Table<V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	protected V value;
	protected String id;
	protected String header;
	protected boolean rendered;
	protected String width;
	protected Integer rowsToDisplay;


	@Override
	public V getValue() {
		return this.value;
	}
	@Override
	public void setValue(V value) {
		this.value = value;
	}
	@Override
	public String getId() {
		return this.id;
	}
	@Override
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String getHeader() {
		return this.header;
	}
	@Override
	public void setHeader(String header) {
		this.header = header;
	}
	@Override
	public boolean isRendered() {
		return this.rendered;
	}
	@Override
	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}
	@Override
	public String getWidth() {
		return this.width;
	}
	@Override
	public void setWidth(String width) {
		this.width = width;
	}
	@Override
	public Integer getRowsToDisplay() {
		return this.rowsToDisplay;
	}
	@Override
	public void setRowsToDisplay(Integer rowsToDisplay) {
		this.rowsToDisplay = rowsToDisplay;
	}



}
