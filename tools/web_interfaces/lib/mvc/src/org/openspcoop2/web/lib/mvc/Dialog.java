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

package org.openspcoop2.web.lib.mvc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Dialog implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String titolo;
	private String icona;
	private String headerRiga1;
	private String headerRiga2;
	private List<BodyElement> body;
	private String notaFinale;
	
	private boolean resizable = false;
	private boolean draggable = false;
	private String height = "auto";
	private String width = "660px"; 
	
	public Dialog() {
		this.body = new ArrayList<Dialog.BodyElement>();
		this.icona = Costanti.ICON_DIALOG_HEADER;
	}
	
	public String getTitolo() {
		return DataElement.checkNull(this.titolo);
	}
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}
	public String getIcona() {
		return DataElement.checkNull(this.icona);
	}
	public void setIcona(String icona) {
		this.icona = icona;
	}
	public String getHeaderRiga1() {
		return DataElement.checkNull(this.headerRiga1);
	}
	public void setHeaderRiga1(String headerRiga1) {
		this.headerRiga1 = headerRiga1;
	}
	public String getHeaderRiga2() {
		return DataElement.checkNull(this.headerRiga2);
	}
	public void setHeaderRiga2(String headerRiga2) {
		this.headerRiga2 = headerRiga2;
	}
	public List<BodyElement> getBody() {
		return this.body;
	}
	public void addBodyElement(BodyElement bodyElement) {
		this.body.add(bodyElement);
	}
	public String getNotaFinale() {
		return DataElement.checkNull(this.notaFinale);
	}
	public void setNotaFinale(String notaFinale) {
		this.notaFinale = notaFinale;
	}
	
	public boolean isResizable() {
		return this.resizable;
	}
	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}
	public boolean isDraggable() {
		return this.draggable;
	}
	public void setDraggable(boolean draggable) {
		this.draggable = draggable;
	}
	public String getHeight() {
		return this.height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getWidth() {
		return this.width;
	}
	public void setWidth(String width) {
		this.width = width;
	}

	public class BodyElement extends DataElement  implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private boolean visualizzaCopyAction;
		private String tooltipCopyAction;
		private boolean resizable = false;
		
		public boolean isVisualizzaCopyAction() {
			return this.visualizzaCopyAction;
		}
		public void setVisualizzaCopyAction(boolean visualizzaCopyAction) {
			this.visualizzaCopyAction = visualizzaCopyAction;
		}
		public String getTooltipCopyAction() {
			return DataElement.checkNull(this.tooltipCopyAction);
		}
		public void setTooltipCopyAction(String tooltipCopyAction) {
			this.tooltipCopyAction = tooltipCopyAction;
		}
		public boolean isResizable() {
			return this.resizable;
		}
		public void setResizable(boolean resizable) {
			this.resizable = resizable;
		}
	}
	
	public static BodyElement newBodyElement() {
		return new Dialog(). new BodyElement();
	}
}
