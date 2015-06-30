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
package org.openspcoop2.generic_project.web.input.factory;

import java.io.Serializable;

import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.input.BooleanCheckBox;
import org.openspcoop2.generic_project.web.input.DateTime;
import org.openspcoop2.generic_project.web.input.HtmlOption;
import org.openspcoop2.generic_project.web.input.InputNumber;
import org.openspcoop2.generic_project.web.input.InputSecret;
import org.openspcoop2.generic_project.web.input.MultipleCheckBox;
import org.openspcoop2.generic_project.web.input.MultipleChoice;
import org.openspcoop2.generic_project.web.input.MultipleListBox;
import org.openspcoop2.generic_project.web.input.PickList;
import org.openspcoop2.generic_project.web.input.RadioButton;
import org.openspcoop2.generic_project.web.input.SelectList;
import org.openspcoop2.generic_project.web.input.SingleChoice;
import org.openspcoop2.generic_project.web.input.SingleListBox;
import org.openspcoop2.generic_project.web.input.Slider;
import org.openspcoop2.generic_project.web.input.Spinner;
import org.openspcoop2.generic_project.web.input.Text;
import org.openspcoop2.generic_project.web.input.TextArea;

/***
 * 
 * Interfaccia base che definisce la factory degli elementi di input.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public interface InputFieldFactory extends Serializable {

	// Costruttori Elementi Text
	public Text createText() throws FactoryException;
	
	public TextArea createTextArea() throws FactoryException;
	
	public InputSecret createInputSecret() throws FactoryException;

	// Costruttori Elementi Date
	public DateTime createDateTime() throws FactoryException;

	//Costruttori Elementi Numerici
	public InputNumber createNumber() throws FactoryException;
	
	public Spinner createSpinner() throws FactoryException;
	
	public Slider createSlider() throws FactoryException;
	
	// Costruttore Elemento checkboxboolean
	public BooleanCheckBox createBooleanCheckBox() throws FactoryException;

	// Costruttori Elementi di scelta singola
	public  <OptionType extends HtmlOption> SingleChoice <OptionType>  createSingleChoice() throws FactoryException; 

	public  <OptionType extends HtmlOption> SelectList<OptionType>  createSelectList() throws FactoryException;

	public  <OptionType extends HtmlOption> RadioButton<OptionType>  createRadioButton() throws FactoryException;
	
	public  <OptionType extends HtmlOption> SingleListBox<OptionType>  createSingleListBox() throws FactoryException; 

	// Costruttori Elementi di scleta multipla
	public  <OptionType extends HtmlOption> MultipleChoice<OptionType>  createMultipleChoice() throws FactoryException; 

	public  <OptionType extends HtmlOption> PickList<OptionType>  createPickList() throws FactoryException;

	public  <OptionType extends HtmlOption> MultipleCheckBox<OptionType>  createMultipleCheckBox() throws FactoryException;	
	
	public  <OptionType extends HtmlOption> MultipleListBox<OptionType>  createMultipleListBox() throws FactoryException;
	

}
