/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.web.input;

/***
 * 
 * Interfaccia che descrive un elemento di input di tipo Spinner.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public interface Spinner extends InputNumber {
	
	// incremento dello slider.
	public Number getStep();
	public void setStep(Number step);
	
	// Valore massimo dello slider.
	public Number getMaxValue();
	public void setMaxValue(Number maxValue);

	// Valore minimo dello slider.
	public Number getMinValue();
	public void setMinValue(Number minValue);
	
	// indica se quando si supera il valore max/min si riprende dall'altro estremo.
	public boolean isCycled();
	public void setCycled(boolean cycled);

}
