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

import java.util.List;

import org.openspcoop2.generic_project.web.input.SelectItem;
import org.openspcoop2.generic_project.web.input.FieldType;
import org.openspcoop2.generic_project.web.input.SelectList;

/***
 * 
 * Implementazione base di un elemento di tipo Select List.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class SelectListImpl extends SingleChoiceImpl implements SelectList{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer maxSelectItemsWidth = null; 
	private Integer defaultSelectItemsWidth = null;
	private Integer selectItemsWidth = null;
	private boolean checkItemWidth =false;

	public SelectListImpl(){
		super();

		this.setType(FieldType.SELECT_LIST);
		this.setConverterName("selectItemConverter");

		this.defaultSelectItemsWidth = this.width ;
		// Aggiungere la possibilita di configurarla
		this.maxSelectItemsWidth = 700;
		this.checkItemWidth = false;
	}

	@Override
	public void setElencoSelectItems(List<javax.faces.model.SelectItem> elencoSelectItems) {
		super.setElencoSelectItems(elencoSelectItems);

		if(this.checkItemWidth){
			this.selectItemsWidth = 0;
			for (javax.faces.model.SelectItem selectItem : elencoSelectItems) {
				Object obj	= selectItem.getValue();
				if(obj instanceof SelectItem){
					SelectItem item = (SelectItem) obj;
					String label = item.getLabel();

					int lunghezza = getFontWidth(label);
					this.selectItemsWidth = Math.max(this.selectItemsWidth,  lunghezza);
				}
			}

		}
	}

	public Integer getMaxSelectItemsWidth() {
		return this.maxSelectItemsWidth;
	}

	public void setMaxSelectItemsWidth(Integer maxSelectItemsWidth) {
		this.maxSelectItemsWidth = maxSelectItemsWidth;
	}

	public Integer getDefaultSelectItemsWidth() {
		return this.defaultSelectItemsWidth;
	}

	public void setDefaultSelectItemsWidth(Integer defaultSelectItemsWidth) {
		this.defaultSelectItemsWidth = defaultSelectItemsWidth;
	}

	public Integer getSelectItemsWidth() {
		return checkWidthLimits(this.selectItemsWidth);
	}

	public void setSelectItemsWidth(Integer selectItemsWidth) {
		this.selectItemsWidth = selectItemsWidth;
	}

	public boolean isCheckItemWidth() {
		return this.checkItemWidth;
	}

	public void setCheckItemWidth(boolean checkItemWidth) {
		this.checkItemWidth = checkItemWidth;
	}

	private Integer checkWidthLimits(Integer value){
		if(this.checkItemWidth){
			// valore deve essere minore del max ma almeno maggiore del default
			Integer toRet = Math.max(this.defaultSelectItemsWidth, value);

			toRet = Math.min(toRet, this.maxSelectItemsWidth);

			return toRet;
		}else 
			return value;
	}
}
