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
package org.openspcoop2.generic_project.web.input.factory;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.form.Form;
import org.openspcoop2.generic_project.web.input.BooleanCheckBox;
import org.openspcoop2.generic_project.web.input.DateTime;
import org.openspcoop2.generic_project.web.input.InputNumber;
import org.openspcoop2.generic_project.web.input.InputSecret;
import org.openspcoop2.generic_project.web.input.MultipleCheckBox;
import org.openspcoop2.generic_project.web.input.MultipleChoice;
import org.openspcoop2.generic_project.web.input.MultipleListBox;
import org.openspcoop2.generic_project.web.input.PickList;
import org.openspcoop2.generic_project.web.input.RadioButton;
import org.openspcoop2.generic_project.web.input.SelectItem;
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
	public Text createText(Form form) throws FactoryException;
	public Text createText(String name, String label, String initialValue, boolean required) throws FactoryException;
	public Text createText(String name, String label, String initialValue, boolean required,Form form) throws FactoryException;
	public Text createTextInterval(String name, String label, String initialValueStart, String initialValueEnd, boolean required) throws FactoryException;
	public Text createTextInterval(String name, String label, String initialValueStart, String initialValueEnd, boolean required,Form form) throws FactoryException;

	public TextArea createTextArea() throws FactoryException;
	public TextArea createTextArea(Form form) throws FactoryException;
	public TextArea createTextArea(String name, String label, String initialValue, boolean required) throws FactoryException;
	public TextArea createTextArea(String name, String label, String initialValue, boolean required,Form form) throws FactoryException;

	public InputSecret createInputSecret() throws FactoryException;
	public InputSecret createInputSecret(Form form) throws FactoryException;
	public InputSecret createInputSecret(String name, String label, String initialValue, boolean required) throws FactoryException;
	public InputSecret createInputSecret(String name, String label, String initialValue, boolean required,Form form) throws FactoryException;

	// Costruttori Elementi Date
	public DateTime createDateTime() throws FactoryException;
	public DateTime createDateTime(Form form) throws FactoryException;
	public DateTime createDateTime(String name, String label, Date initialValue, boolean required) throws FactoryException;
	public DateTime createDateTime(String name, String label, Date initialValue, boolean required,Form form) throws FactoryException;
	public DateTime createDateTime(String name, String label, String pattern, Date initialValue, boolean required) throws FactoryException;
	public DateTime createDateTime(String name, String label, String pattern, Date initialValue, boolean required,Form form) throws FactoryException;
	public DateTime createDateTimeInterval(String name, String label, Date initialValueStart, Date initialValueEnd, boolean required) throws FactoryException;
	public DateTime createDateTimeInterval(String name, String label, Date initialValueStart, Date initialValueEnd, boolean required,Form form) throws FactoryException;
	public DateTime createDateTimeInterval(String name, String label, String pattern, Date initialValueStart, Date initialValueEnd, boolean required) throws FactoryException;
	public DateTime createDateTimeInterval(String name, String label, String pattern, Date initialValueStart, Date initialValueEnd, boolean required,Form form) throws FactoryException;


	//Costruttori Elementi Numerici
	public InputNumber createNumber() throws FactoryException;
	public InputNumber createNumber(Form form) throws FactoryException;
	public InputNumber createNumber(String name, String label, Number initialValue, boolean required) throws FactoryException;
	public InputNumber createNumber(String name, String label, Number initialValue, boolean required,Form form) throws FactoryException;
	public InputNumber createNumberInterval(String name, String label, Number initialValueStart, Number initialValueEnd, boolean required) throws FactoryException;
	public InputNumber createNumberInterval(String name, String label, Number initialValueStart, Number initialValueEnd, boolean required,Form form) throws FactoryException;

	public Spinner createSpinner() throws FactoryException;
	public Spinner createSpinner(Form form) throws FactoryException;
	public Spinner createSpinner(String name, String label, Number initialValue, boolean required) throws FactoryException;
	public Spinner createSpinner(String name, String label, Number initialValue, boolean required,Form form) throws FactoryException;

	public Slider createSlider() throws FactoryException;
	public Slider createSlider(Form form) throws FactoryException;
	public Slider createSlider(String name, String label, Number initialValue, boolean required) throws FactoryException;
	public Slider createSlider(String name, String label, Number initialValue, boolean required,Form form) throws FactoryException;

	// Costruttore Elemento checkboxboolean
	public BooleanCheckBox createBooleanCheckBox() throws FactoryException;
	public BooleanCheckBox createBooleanCheckBox(Form form) throws FactoryException;
	public BooleanCheckBox createBooleanCheckBox(String name, String label, Boolean initialValue, boolean required) throws FactoryException;
	public BooleanCheckBox createBooleanCheckBox(String name, String label, Boolean initialValue, boolean required,Form form) throws FactoryException;

	// Costruttori Elementi di scelta singola
	public  SingleChoice createSingleChoice() throws FactoryException; 
	public  SingleChoice createSingleChoice(Form form) throws FactoryException; 
	public  SingleChoice createSingleChoice(String name, String label, SelectItem initialValue, boolean required) throws FactoryException;
	public  SingleChoice createSingleChoice(String name, String label, SelectItem initialValue, boolean required,Form form) throws FactoryException;

	public  SelectList   createSelectList() throws FactoryException;
	public  SelectList   createSelectList(Form form) throws FactoryException;
	public  SelectList   createSelectList(String name, String label, SelectItem initialValue, boolean required) throws FactoryException;
	public  SelectList   createSelectList(String name, String label, SelectItem initialValue, boolean required,Form form) throws FactoryException;

	public  RadioButton   createRadioButton() throws FactoryException;
	public  RadioButton   createRadioButton(Form form) throws FactoryException;
	public  RadioButton createRadioButton(String name, String label, SelectItem initialValue, boolean required) throws FactoryException;
	public  RadioButton createRadioButton(String name, String label, SelectItem initialValue, boolean required,Form form) throws FactoryException;

	public  SingleListBox  createSingleListBox() throws FactoryException;
	public  SingleListBox  createSingleListBox(Form form) throws FactoryException;
	public  SingleListBox createSingleListBox(String name, String label, SelectItem initialValue, boolean required) throws FactoryException;
	public  SingleListBox createSingleListBox(String name, String label, SelectItem initialValue, boolean required,Form form) throws FactoryException;

	// Costruttori Elementi di scelta multipla
	public  MultipleChoice  createMultipleChoice() throws FactoryException; 
	public  MultipleChoice  createMultipleChoice(Form form) throws FactoryException; 
	public  MultipleChoice  createMultipleChoice(String name, String label, List<SelectItem> initialValue, boolean required) throws FactoryException;
	public  MultipleChoice  createMultipleChoice(String name, String label, List<SelectItem> initialValue, boolean required,Form form) throws FactoryException;

	public  PickList  createPickList() throws FactoryException;
	public  PickList  createPickList(Form form) throws FactoryException;
	public  PickList  createPickList(String name, String label, List<SelectItem> initialValue, boolean required) throws FactoryException;
	public  PickList  createPickList(String name, String label, List<SelectItem> initialValue, boolean required,Form form) throws FactoryException;

	public  MultipleCheckBox  createMultipleCheckBox() throws FactoryException;
	public  MultipleCheckBox  createMultipleCheckBox(Form form) throws FactoryException;
	public  MultipleCheckBox  createMultipleCheckBox(String name, String label, List<SelectItem> initialValue, boolean required) throws FactoryException;
	public  MultipleCheckBox  createMultipleCheckBox(String name, String label, List<SelectItem> initialValue, boolean required,Form form) throws FactoryException;

	public  MultipleListBox  createMultipleListBox() throws FactoryException;
	public  MultipleListBox  createMultipleListBox(Form form) throws FactoryException;
	public  MultipleListBox  createMultipleListBox(String name, String label, List<SelectItem> initialValue, boolean required) throws FactoryException;
	public  MultipleListBox  createMultipleListBox(String name, String label, List<SelectItem> initialValue, boolean required,Form form) throws FactoryException;


}
