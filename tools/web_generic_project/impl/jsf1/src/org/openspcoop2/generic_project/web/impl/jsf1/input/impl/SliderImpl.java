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

import org.openspcoop2.generic_project.web.factory.Costanti;
import org.openspcoop2.generic_project.web.input.FieldType;
import org.openspcoop2.generic_project.web.input.Slider;

/***
 * 
 * Implementazione base di un elemento di tipo Slider.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class SliderImpl extends NumberImpl implements Slider{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean showToolTip = true;
	private boolean showInput = false;
	private String inputPosition = Costanti.SLIDER_INPUT_POSITION_RIGHT;
	private boolean showBoundaryValues = true;
	private int maxlength = Integer.MAX_VALUE;
	private Number maxValue = 100;
	private Number minValue = 0;
	private Number step = 1;
	private String orientation = Costanti.SLIDER_ORIENTATION_HORIZONTAL;
	
	public SliderImpl(){
		super();
		this.setType(FieldType.SLIDER);
	}

	@Override
	public boolean isShowToolTip() {
		return this.showToolTip;
	}

	@Override
	public void setShowToolTip(boolean showToolTip) {
		this.showToolTip = showToolTip;
	}

	@Override
	public boolean isShowInput() {
		return this.showInput;
	}

	@Override
	public void setShowInput(boolean showInput) {
		this.showInput = showInput;
	}

	@Override
	public String getInputPosition() {
		return this.inputPosition;
	}

	@Override
	public void setInputPosition(String inputPosition) {
		this.inputPosition = inputPosition;
	}

	@Override
	public boolean isShowBoundaryValues() {
		return this.showBoundaryValues;
	}

	@Override
	public void setShowBoundaryValues(boolean showBoundaryValues) {
		this.showBoundaryValues = showBoundaryValues;
	}

	@Override
	public int getMaxlength() {
		return this.maxlength;
	}

	@Override
	public void setMaxlength(int maxlength) {
		this.maxlength = maxlength;
	}

	@Override
	public Number getMaxValue() {
		return this.maxValue;
	}

	@Override
	public void setMaxValue(Number maxValue) {
		this.maxValue = maxValue;
	}

	@Override
	public Number getMinValue() {
		return this.minValue;
	}

	@Override
	public void setMinValue(Number minValue) {
		this.minValue = minValue;
	}

	@Override
	public String getOrientation() {
		return this.orientation;
	}

	@Override
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	@Override
	public Number getStep() {
		return this.step;
	}

	@Override
	public void setStep(Number step) {
		this.step = step;
	}
}
