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
package org.openspcoop2.generic_project.web.presentation.field;

/***
 * 
 * OutputNumber Field di output di tipo numerico. 
 *
 * @param <N> Tipo del valore numerico da gestire
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OutputNumber<N extends Number> extends OutputField<Number> {

	public static final String CONVERT_TYPE_CURRENCY = "currency";
	public static final String CONVERT_TYPE_PERCENT = "percent";
	public static final String CONVERT_TYPE_NUMBER = "number";

	public static final String CURRENCY_SYMBOL_DOLLAR = "$";
	public static final String CURRENCY_SYMBOL_EURO = "€";

	public static final String CURRENCY_CODE_EURO = "EUR";
	public static final String CURRENCY_CODE_GBP = "GBP";
	public static final String CURRENCY_CODE_USA = "USD";

	public OutputNumber(){
		this.setType("number");
		this.setInsideGroup(false); 
	}

	private String currencySymbol = null;
	private String converterType = null;
	private String currencyCode = null;


	public String getCurrencySymbol() {
		return this.currencySymbol;
	}
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}
	public String getConverterType() {
		return this.converterType;
	}
	public void setConverterType(String converterType) {
		this.converterType = converterType;
	}
	public String getCurrencyCode() {
		return this.currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}



}
