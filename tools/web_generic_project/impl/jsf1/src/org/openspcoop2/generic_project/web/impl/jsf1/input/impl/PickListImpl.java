/*
 * OpenSPCoop - Customizable API Gateway 
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
import org.openspcoop2.generic_project.web.input.PickList;

/***
 * 
 * Implementazione base di un elemento di tipo PickList.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class PickListImpl extends MultipleChoiceImpl implements PickList{ 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String targetCaptionLabel;
	private String sourceCaptionLabel;
	private boolean fastMoveControlsVisible;
	private boolean moveControlsVisible;
	private boolean fastOrderControlsVisible;
	private boolean orderControlsVisible;

	private SelectItem suggestionValue = null;

	// Impostazioni riguardanti la suggestion box
	private String suggestionBoxStyle = null;
	private Integer suggestionBoxWidth = null;
	private Integer maxSuggestionBoxItemsWidth = null; 
	private Integer defaultSuggestionBoxItemsWidth = null;
	private Integer suggestionBoxItemsWidth = null;

	// Altezza delle liste dei dati
	private Integer listsHeight = null;
	private Integer defaultListsHeight = null;
	private Integer maxListsHeight = null; 

	// lunghezza delle liste dei dati
	private Integer sourceListWidth = null;
	private Integer targetListWidth = null;
	private Integer defaultListWidth = null;
	private Integer maxListWidth = null; 

	// flag che controlla la gestione delle dimensioni
	private boolean checkDimensions =false;
	
	public PickListImpl(){
		super();

		this.setType(FieldType.PICKLIST);

		// Default visualizzo solo i controlli per spostare gli elementi tra le due liste.
		this.fastMoveControlsVisible = true;
		this.fastOrderControlsVisible = false;
		this.moveControlsVisible = true;
		this.orderControlsVisible = false;
		this.suggestionValue = null;
		this.suggestionBoxStyle = null;

		// Lunghezza di default della suggestion box e lista dei risultati
		this.suggestionBoxWidth = 180;
		this.suggestionBoxItemsWidth = 180;
		this.defaultSuggestionBoxItemsWidth = this.suggestionBoxItemsWidth ;
		// Aggiungere la possibilita di configurarla
		this.maxSuggestionBoxItemsWidth = 700;

		this.checkDimensions = false;

		this.listsHeight = 170;
		this.defaultListsHeight = 170;
		this.maxListsHeight = 500;


		this.sourceListWidth = 180;
		this.targetListWidth = 180;
		this.defaultListWidth = 180;
		this.maxListWidth = 400;
	}

	public String getSuggestionBoxStyle() {
		if(this.suggestionBoxStyle == null)
			return "margin-left: 8px; width:"+this.suggestionBoxWidth+"px;";

		return this.suggestionBoxStyle;
	}
	public void setSuggestionBoxStyle(String suggestionBoxStyle) {
		this.suggestionBoxStyle = suggestionBoxStyle;
	}
	public String getTargetCaptionLabel() {
		return this.targetCaptionLabel;
	}

	public void setTargetCaptionLabel(String targetCaptionLabel) {
		this.targetCaptionLabel = targetCaptionLabel;
	}

	public String getSourceCaptionLabel() {
		return this.sourceCaptionLabel;
	}

	public void setSourceCaptionLabel(String sourceCaptionLabel) {
		this.sourceCaptionLabel = sourceCaptionLabel;
	}

	public boolean isFastMoveControlsVisible() {
		return this.fastMoveControlsVisible;
	}

	public void setFastMoveControlsVisible(boolean fastMoveControlsVisible) {
		this.fastMoveControlsVisible = fastMoveControlsVisible;
	}

	public boolean isMoveControlsVisible() {
		return this.moveControlsVisible;
	}

	public void setMoveControlsVisible(boolean moveControlsVisible) {
		this.moveControlsVisible = moveControlsVisible;
	}

	public boolean isFastOrderControlsVisible() {
		return this.fastOrderControlsVisible;
	}

	public void setFastOrderControlsVisible(boolean fastOrderControlsVisible) {
		this.fastOrderControlsVisible = fastOrderControlsVisible;
	}

	public boolean isOrderControlsVisible() {
		return this.orderControlsVisible;
	}

	public void setOrderControlsVisible(boolean orderControlsVisible) {
		this.orderControlsVisible = orderControlsVisible;
	}

	public SelectItem getSuggestionValue() {
		return this.suggestionValue;
	}

	public void setSuggestionValue(SelectItem suggestionValue) {
//		if(suggestionValue != null){
//			log.debug("setSuggestionValue ["+suggestionValue.toString()+"]");
//		}else 
//			log.debug("setSuggestionValue [NULL]");
		
		this.suggestionValue = suggestionValue;
	}

	public Integer getSuggestionBoxWidth() {
		return this.suggestionBoxWidth;
	}

	public void setSuggestionBoxWidth(Integer suggestionBoxWidth) {
		this.suggestionBoxWidth = suggestionBoxWidth;
	}

	public Integer getListsHeight() {
		return checkListHeightsLimits(this.listsHeight);
	}

	public void setListsHeight(Integer listsHeight) {
		this.listsHeight = listsHeight;
	}

	public Integer getSourceListWidth() {
		return checkListWidthLimits(this.sourceListWidth);
	}

	public void setSourceListWidth(Integer sourceListWidth) {
		this.sourceListWidth = sourceListWidth;
	}

	public Integer getTargetListWidth() {
		return checkListWidthLimits(this.targetListWidth);
	}

	public void setTargetListWidth(Integer targetListWidth) {
		this.targetListWidth = targetListWidth;
	}


	public boolean isCheckDimensions() {
		return this.checkDimensions;
	}

	public void setCheckDimensions(boolean checkDimensions) {
		this.checkDimensions = checkDimensions;
	}

	public Integer getMaxSuggestionBoxItemsWidth() {
		return this.maxSuggestionBoxItemsWidth;
	}

	public void setMaxSuggestionBoxItemsWidth(Integer maxSuggestionBoxItemsWidth) {
		this.maxSuggestionBoxItemsWidth = maxSuggestionBoxItemsWidth;
	}

	public Integer getDefaultSuggestionBoxItemsWidth() {
		return this.defaultSuggestionBoxItemsWidth;
	}

	public void setDefaultSuggestionBoxItemsWidth(
			Integer defaultSuggestionBoxItemsWidth) {
		this.defaultSuggestionBoxItemsWidth = defaultSuggestionBoxItemsWidth;
	}

	public Integer getSuggestionBoxItemsWidth() {
		return checkSuggestionBoxLimits(this.suggestionBoxItemsWidth);
	}

	public void setSuggestionBoxItemsWidth(Integer suggestionBoxItemsWidth) {
		this.suggestionBoxItemsWidth = suggestionBoxItemsWidth;
	}

	public Integer getDefaultListsHeight() {
		return this.defaultListsHeight;
	}

	public void setDefaultListsHeight(Integer defaultListsHeight) {
		this.defaultListsHeight = defaultListsHeight;
	}

	public Integer getMaxListsHeight() {
		return this.maxListsHeight;
	}

	public void setMaxListsHeight(Integer maxListsHeight) {
		this.maxListsHeight = maxListsHeight;
	}

	public Integer getDefaultListWidth() {
		return this.defaultListWidth;
	}

	public void setDefaultListWidth(Integer defaultListWidth) {
		this.defaultListWidth = defaultListWidth;
	}

	public Integer getMaxListWidth() {
		return this.maxListWidth;
	}

	public void setMaxListWidth(Integer maxListWidth) {
		this.maxListWidth = maxListWidth;
	}



	private Integer checkListWidthLimits(Integer value){
		if(this.checkDimensions){
			// valore deve essere minore del max ma almeno maggiore del default
			Integer toRet = Math.max(this.defaultListWidth, value);

			toRet = Math.min(toRet, this.maxListWidth);

			return toRet;
		}else 
			return value;
	}

	private Integer checkListHeightsLimits(Integer value){
		if(this.checkDimensions){
			// valore deve essere minore del max ma almeno maggiore del default
			Integer toRet = Math.max(this.defaultListsHeight, value);

			toRet = Math.min(toRet, this.maxListsHeight);

			return toRet;
		}else 
			return value;
	}

	private Integer checkSuggestionBoxLimits(Integer value){
		if(this.checkDimensions){
			// valore deve essere minore del max ma almeno maggiore del default
			Integer toRet = Math.max(this.defaultSuggestionBoxItemsWidth, value);

			toRet = Math.min(toRet, this.maxSuggestionBoxItemsWidth);

			return toRet;
		}else 
			return value;
	}

	@Override
	public void setOptions(java.util.List<SelectItem> elencoOptions) {
		super.setOptions(elencoOptions);

		//		if(this.checkDimensions){
		//			this.sourceListWidth = 0;
		//			for (SelectItem item : elencoOptions) {
		//					String label = item.getLabel();
		//
		//					int lunghezza = getFontWidth(label);
		//					this.sourceListWidth = Math.max(this.sourceListWidth,  lunghezza);
		//			}
		//
		//		}
	}

	@Override
	public void setValue(List<SelectItem> value) {
		super.setValue(value);

		//		if(this.checkDimensions){
		//			this.targetListWidth = 0;
		//			for (SelectItem item : value) {
		//				String label = item.getLabel();
		//
		//				int lunghezza = getFontWidth(label);
		//				this.targetListWidth = Math.max(this.targetListWidth,  lunghezza);
		//			}
		//
		//		}

	}

	public void checkDimensions(){
		if(this.checkDimensions){

			// elementi di sx
			this.sourceListWidth = 0;
			if(this.elencoHtmlOptions != null && this.elencoHtmlOptions.size() > 0){
				for (SelectItem item : this.elencoHtmlOptions) {
					String label = item.getLabel();

					int lunghezza = getFontWidth(label);
					this.sourceListWidth = Math.max(this.sourceListWidth,  lunghezza);
				}
			} 

			//elementi di dx
			this.targetListWidth = 0;
			if(this.value != null && this.value.size() > 0){
				for (SelectItem item : this.value) {
					String label = item.getLabel();

					int lunghezza = getFontWidth(label);
					this.targetListWidth = Math.max(this.targetListWidth,  lunghezza);
				}
			}

			// imposto la dimensione massima trovata per entrambi

			Integer mxc = Math.max(this.targetListWidth, this.sourceListWidth);
			this.targetListWidth = mxc;
			this.sourceListWidth = mxc;
			this.suggestionBoxItemsWidth = mxc;
		}
	}

	@Override
	public void setElencoSelectItems(List<javax.faces.model.SelectItem> elencoSelectItems) {
		super.setElencoSelectItems(elencoSelectItems);

		//		if(this.checkDimensions){
		//			this.sourceListWidth = 0;
		//			for (javax.faces.model.SelectItem selectItem : elencoSelectItems) {
		//				Object obj	= selectItem.getValue();
		//				if(obj instanceof SelectItem){
		//					SelectItem item = (SelectItem) obj;
		//					String label = item.getLabel();
		//
		//					int lunghezza = getFontWidth(label);
		//					this.sourceListWidth = Math.max(this.sourceListWidth,  lunghezza);
		//				}
		//			}
		//
		//		}
	}


	@Override
	public List<?> fieldAutoComplete(Object val) {
		List<?> fieldAutoComplete = super.fieldAutoComplete(val);

		// calcolare la dimensione della suggestion box

		if(this.checkDimensions){
			this.suggestionBoxWidth = 0;
			for (Object object : fieldAutoComplete) {
				if(object instanceof SelectItem){
					SelectItem item = (SelectItem) object;
					String label = item.getLabel();

					int lunghezza = getFontWidth(label);
					this.suggestionBoxWidth = Math.max(this.suggestionBoxWidth,  lunghezza);

				}
			}
		}


		return fieldAutoComplete;
	}

	
	@Override
	public List<SelectItem> getValue() {
	
		List<SelectItem> value3 = super.getValue();
//		System.out.println("VALUE IS NOT NULL["+(value3 != null)+"]");
		
		return value3;
	}
	
	@Override
	public List<SelectItem> getOptions() {
		List<SelectItem> value3 = super.getOptions();
		
//		System.out.println("SOURCE IS NOT NULL["+(value3 != null)+"]");
		return value3;
	}
	/*

	 <rich:pickList id="input_pckLst_#{field.name}"
				converter="multipleChoiceItemConverter" value="#{field.value}"
				required="#{required}" label="#{field.label}"
				sourceListWidth="160px"
				copyAllControlLabel="#{commonsMsg['inpuPickList.component.copyAllControlLabel']}"
				copyControlLabel="#{commonsMsg['inpuPickList.component.copyControlLabel']}"
				removeAllControlLabel="#{commonsMsg['inpuPickList.component.removeAllControlLabel']}"
				removeControlLabel="#{commonsMsg['inpuPickList.component.removeControlLabel']}">
				<f:selectItems value="#{field.elencoSelectItems}" />
				<a4j:support event="onlistchange" ajaxSingle="true" />
			</rich:pickList>

	 */
}
