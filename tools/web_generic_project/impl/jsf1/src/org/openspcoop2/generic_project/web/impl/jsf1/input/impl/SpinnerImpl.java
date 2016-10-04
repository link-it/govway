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

import org.openspcoop2.generic_project.web.input.FieldType;
import org.openspcoop2.generic_project.web.input.Spinner;

/***
 * 
 * Implementazione base di un elemento di tipo Spinner.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class SpinnerImpl extends NumberImpl implements Spinner{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Number maxValue = 100;
	private Number minValue = 0;
	private Number step = 1;
	private boolean cycled = true;
	
	public SpinnerImpl(){
		super();

		this.setType(FieldType.SPINNER);
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
	public Number getStep() {
		return this.step;
	}

	@Override
	public void setStep(Number step) {
		this.step = step;
	}

	@Override
	public boolean isCycled() {
		return this.cycled;
	}

	@Override
	public void setCycled(boolean cycled) {
		this.cycled = cycled;
	}

	
}
