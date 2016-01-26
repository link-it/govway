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
package org.openspcoop2.generic_project.web.form;



/**
* CostantiForm Classe di costanti.
* 
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class CostantiForm {

	/** Suffissi comuni dei nomi degli event handler */
	public static final String SELECTED_ELEMENT_EVENT_HANDLER = "SelectListener";
	public static final String ONCHANGE_EVENT_HANDLER = "OnChangeListener";
	public static final String VALUE_CHANGED_EVENT_HANDLER = "ValueChanged";
	public static final String AUTO_COMPLETE_EVENT_HANDLER = "AutoComplete";
	public static final String ONTRANSFER_EVENT_HANDLER = "OnTransfer";
	
	
	
	public static final String DEFAULT_FORM_ID = "form";
	public static final String DEFAULT_SEARCH_FORM_ID = "formSearch";
	public static final String NON_SELEZIONATO = "--";
	public static final String ALL = "*";
	
	
	/** Costanti per i nomi dei messaggi di errore */
	public static final String FIELD_NON_PUO_ESSERE_VUOTO= "form.valoreVuoto";
	public static final String SELECT_VALORE_NON_VALIDO = "form.select.valoreNonValido";
	public static final String INPUT_VALORE_NON_VALIDO= "form.input.valoreNonValido";
	public static final String INPUT_VALORE_NUMERICO_NON_VALIDO= "form.input.valoreNumericoNonValido";
	public static final String INPUT_VALORE_NON_VALIDO_CON_FORMATO= "form.input.valoreNonValidoConFormato";
	public static final String INPUT_VALORE_CONFERMA_NON_UGUALE= "form.input.valoreConfermaNonCorrisponde";
	
}
