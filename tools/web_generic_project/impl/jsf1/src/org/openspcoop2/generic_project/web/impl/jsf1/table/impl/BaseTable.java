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
package org.openspcoop2.generic_project.web.impl.jsf1.table.impl;

import org.openspcoop2.generic_project.web.factory.Costanti;
import org.openspcoop2.generic_project.web.impl.jsf1.utils.Utils;
import org.openspcoop2.generic_project.web.table.Table;

/***
 * 
 * Implementazione base per una tabella che presenta una lista di valori.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
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
	protected String headerText;
	protected boolean rendered;
	protected String width;
	protected Integer rowsToDisplay;

	protected String detailColumnPosition = Costanti.TABLE_DETAIL_COLUMN_RIGHT;
	protected boolean showDetailColumn = false;
	protected String detailLinkText = null;

	protected Object metadata = null;
	
	protected String styleClass;
	protected String headerClass;
	protected String footerClass;


	public BaseTable(){
		this.detailLinkText = Utils.getInstance().getMessageFromCommonsResourceBundle("commons.button.dettaglio.title");
	}

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
	public String getHeaderText() {
		try{
			String tmp = Utils.getInstance().getMessageFromResourceBundle(this.headerText);

			if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
				return tmp;
		}catch(Exception e){}
		
		return this.headerText;
	}
	@Override
	public void setHeaderText(String headerText) {
		this.headerText = headerText;
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
	@Override
	public String getDetailColumnPosition() {
		return this.detailColumnPosition;
	}
	@Override
	public void setDetailColumnPosition(String detailColumnPosition) {
		this.detailColumnPosition = detailColumnPosition;
	}
	@Override
	public boolean isShowDetailColumn() {
		return this.showDetailColumn;
	}
	@Override
	public void setShowDetailColumn(boolean showDetailColumn) {
		this.showDetailColumn = showDetailColumn;
	}
	@Override
	public String getDetailLinkText() {
		try{
			String tmp = Utils.getInstance().getMessageFromResourceBundle(this.detailLinkText);

			if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
				return tmp;
		}catch(Exception e){}
		
		return this.detailLinkText;
	}
	@Override
	public void setDetailLinkText(String detailLinkText) {
		this.detailLinkText = detailLinkText;
	}
	@Override
	public Object getMetadata() {
		return this.metadata;
	}
	
	@Override
	public void setMetadata(Object metadata) {
		this.metadata = metadata;
	}

	@Override
	public String getStyleClass() {
		return this.styleClass;
	}

	@Override
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	@Override
	public String getHeaderClass() {
		return this.headerClass;
	}

	@Override
	public void setHeaderClass(String headerClass) {
		this.headerClass = headerClass;
	}

	@Override
	public String getFooterClass() {
		return this.footerClass;
	}

	@Override
	public void setFooterClass(String footerClass) {
		this.footerClass = footerClass;
	}


}
