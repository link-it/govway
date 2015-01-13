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

import java.util.List;


/**
 * MultipleChoiceField input field multivalore di tipo PickList, RadioButton, ecc.. 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MultipleChoiceField extends FormField<List<SelectItem>> {

	private String targetCaptionLabel;
	private String sourceCaptionLabel;
	private boolean fastMoveControlsVisible;
	private boolean moveControlsVisible;
	private boolean fastOrderControlsVisible;
	private boolean orderControlsVisible;
	
	private String suggestionValue = null;

	public MultipleChoiceField(){
		super();

		this.setType(FieldType.PICKLIST);
		
		// Default visualizzo solo i controlli per spostare gli elementi tra le due liste.
		this.fastMoveControlsVisible = true;
		this.fastOrderControlsVisible = false;
		this.moveControlsVisible = true;
		this.orderControlsVisible = false;
		this.suggestionValue = null;
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

	public String getSuggestionValue() {
		return this.suggestionValue;
	}

	public void setSuggestionValue(String suggestionValue) {
		this.suggestionValue = suggestionValue;
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

