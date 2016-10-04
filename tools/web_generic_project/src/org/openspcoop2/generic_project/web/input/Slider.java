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
package org.openspcoop2.generic_project.web.input;

/***
 * 
 * Interfaccia che descrive un elemento di input di tipo Slider.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public interface Slider extends InputNumber {

	// Mostra o nasconde il tooltip sull'indicatore del valore.
	public boolean isShowToolTip();
	public void setShowToolTip(boolean showToolTip);

	// Mostra o nasconde la casella di input col valore
	public boolean isShowInput();
	public void setShowInput(boolean showInput);
	
	// Posizione della casella di input col valore
	public void setInputPosition(String inputPosition);
	public String getInputPosition();

	// Mostra o nasconde i valori degli estremi
	public boolean isShowBoundaryValues();
	public void setShowBoundaryValues(boolean showBoundaryValues);

	// Specifies the maximum number of digits that could be entered into the input field. 
	// The maximum number is unlimited by default.
	// If entered value exceeds the value specified in "maxValue" attribute than the slider takes a maximum value position.
	public int getMaxlength();
	public void setMaxlength(int maxlength);

	// Valore massimo dello slider.
	public Number getMaxValue();
	public void setMaxValue(Number maxValue);

	// Valore minimo dello slider.
	public Number getMinValue();
	public void setMinValue(Number minValue);

	// direzione del componente
	public void setOrientation(String orientation);
	public String getOrientation();
	
	// incremento dello slider.
	public Number getStep();
	public void setStep(Number step);
	
	

}
