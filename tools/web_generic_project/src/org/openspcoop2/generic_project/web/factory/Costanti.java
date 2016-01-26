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
package org.openspcoop2.generic_project.web.factory;

/***
 * 
 * Classe di costanti per la factory.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public class Costanti {

	// Nome del file di configurazione
	public static final String WEB_GENERIC_PROJECT_FACTORY_IMPL_PROPERTIES = "webGenericProjectImpl.properties";

	// Field del file di properties
	public static final String IMPL_FACTORY_CLASS = "webGenericProjectFactoryImplClass";

	// Costanti output numerici
	public static final String CONVERT_TYPE_CURRENCY = "currency";
	public static final String CONVERT_TYPE_PERCENT = "percent";
	public static final String CONVERT_TYPE_NUMBER = "number";

	public static final String CURRENCY_SYMBOL_DOLLAR = "$";
	public static final String CURRENCY_SYMBOL_EURO = "€";

	public static final String CURRENCY_CODE_EURO = "EUR";
	public static final String CURRENCY_CODE_GBP = "GBP";
	public static final String CURRENCY_CODE_USA = "USD";

	// Costanti Slider
	// orintamento
	public static final String SLIDER_ORIENTATION_HORIZONTAL = "horizontal";
	public static final String SLIDER_ORIENTATION_VERTICAL = "vertical";
	// posizione box input
	public static final String SLIDER_INPUT_POSITION_LEFT = "left";
	public static final String SLIDER_INPUT_POSITION_RIGHT = "right";
	public static final String SLIDER_INPUT_POSITION_TOP = "top";
	public static final String SLIDER_INPUT_POSITION_BOTTOM = "bottom";

	// Costanti CheckBox
	// orintamento
	public static final String CHOICE_ORIENTATION_HORIZONTAL = "lineDirection";
	public static final String CHOICE_ORIENTATION_VERTICAL = "pageDirection";
	
	// Costanti ListBox
	//Numero Righe default
	public static final int LISTBOX_NUMERO_RIGHE_DA_VISUALIZZARE = 5;
	
	//Costanti Table
	//Posizione della colonna di dettaglio generata automaticamente.
	public static final String TABLE_DETAIL_COLUMN_LEFT = "left";
	public static final String TABLE_DETAIL_COLUMN_RIGHT = "right";

}
