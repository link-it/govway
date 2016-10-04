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
package org.openspcoop2.generic_project.web.impl.jsf1.input.factory.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactory;
import org.openspcoop2.generic_project.web.form.Form;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.BooleanCheckBoxImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.DateTimeImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.InputSecretImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.MultipleCheckBoxImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.MultipleChoiceImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.MultipleListBoxImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.NumberImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.PickListImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.RadioButtonImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.SelectListImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.SingleChoiceImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.SingleListBoxImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.SliderImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.SpinnerImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.TextAreaImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.TextImpl;
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
import org.openspcoop2.generic_project.web.input.factory.InputFieldFactory;

/***
 * 
 * Implementazione della factory  JSF1  degli elementi di input.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class Jsf1InputFieldFactoryImpl implements InputFieldFactory{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@SuppressWarnings("unused")
	private transient WebGenericProjectFactory webGenericProjectFactory;
	@SuppressWarnings("unused")
	private transient Logger log = null;

	public Jsf1InputFieldFactoryImpl(WebGenericProjectFactory factory,Logger log){
		this.webGenericProjectFactory = factory;
		this.log = log;
	}

	@Override
	public Text createText() throws FactoryException {
		return createText(null); 
	}

	@Override
	public Text createText(Form form) throws FactoryException {
		Text input = createText(null, null, null, false, form);
		return input;
	}

	@Override
	public Text createText(String name, String label, String initialValue, boolean required) throws FactoryException {
		Text input = createText(name, label, initialValue, required, null);
		return input;
	}

	@Override
	public Text createText(String name, String label, String initialValue,	boolean required, Form form) throws FactoryException {
		Text input =  new TextImpl();

		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);

		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}

	@Override
	public Text createTextInterval(String name, String label,String initialValueStart, String initialValueEnd, boolean required)	throws FactoryException {
		Text input = createTextInterval(name, label, initialValueStart, initialValueEnd, required, null);
		return input;
	}

	@Override
	public Text createTextInterval(String name, String label,	String initialValueStart, String initialValueEnd, boolean required,	Form form) throws FactoryException {
		Text input =  new TextImpl();

		input.setLabel(label); 
		input.setDefaultValue(initialValueStart);
		input.setDefaultValue2(initialValueEnd);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(true);

		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}

	@Override
	public TextArea createTextArea() throws FactoryException {
		return createTextArea(null);
	}

	@Override
	public TextArea createTextArea(Form form) throws FactoryException {
		TextArea input = createTextArea(null, null, null, false, form);
		return input;
	}

	@Override
	public TextArea createTextArea(String name, String label, String initialValue, boolean required) throws FactoryException {
		TextArea input = createTextArea(name, label, initialValue, required, null);
		return input;
	}

	@Override
	public TextArea createTextArea(String name, String label,	String initialValue, boolean required, Form form)	throws FactoryException {
		TextArea input = new TextAreaImpl( );

		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);

		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}


	@Override
	public InputSecret createInputSecret() throws FactoryException {
		return createInputSecret(null);
	}

	@Override
	public InputSecret createInputSecret(Form form) throws FactoryException {
		InputSecret input = createInputSecret(null, null, null, false, form);
		return input;
	}

	@Override
	public InputSecret createInputSecret(String name, String label, String initialValue, boolean required) throws FactoryException {
		InputSecret input = createInputSecret(name, label, initialValue, required, null);
		return input;
	}

	@Override
	public InputSecret createInputSecret(String name, String label, String initialValue, boolean required, Form form) throws FactoryException {
		InputSecret input = new InputSecretImpl();

		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);

		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}

	@Override
	public BooleanCheckBox createBooleanCheckBox() throws FactoryException {
		return createBooleanCheckBox(null);
	}

	@Override
	public BooleanCheckBox createBooleanCheckBox(Form form) throws FactoryException {
		BooleanCheckBox input = createBooleanCheckBox(null, null, null, false, form);
		return input;
	}

	@Override
	public BooleanCheckBox createBooleanCheckBox(String name, String label, Boolean initialValue, boolean required) throws FactoryException {
		BooleanCheckBox input = createBooleanCheckBox(name, label, initialValue, required, null);
		return input;
	}

	@Override
	public BooleanCheckBox createBooleanCheckBox(String name, String label, Boolean initialValue, boolean required,Form form) throws FactoryException {
		BooleanCheckBox input =  new BooleanCheckBoxImpl();

		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);

		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}



	@Override
	public DateTime createDateTime() throws FactoryException {
		return createDateTime(null);
	}

	@Override
	public DateTime createDateTime(Form form) throws FactoryException {
		DateTime input = createDateTime(null, null, null, null, false, form);
		return input;
	}

	@Override
	public DateTime createDateTime(String name, String label, Date initialValue, boolean required) throws FactoryException {
		DateTime input = createDateTime(name, label, null, initialValue, required,null);
		return input;
	}

	@Override
	public DateTime createDateTime(String name, String label, Date initialValue, boolean required,Form form) throws FactoryException {
		DateTime input = createDateTime(name, label, null, initialValue, required,form);
		return input;
	}

	@Override
	public DateTime createDateTime(String name, String label, String pattern,Date initialValue, boolean required) throws FactoryException {
		DateTime input = createDateTime(name, label, pattern, initialValue, required, null);
		return input;
	}

	@Override
	public DateTime createDateTime(String name, String label, String pattern,Date initialValue, boolean required,Form form) throws FactoryException {
		DateTime input = new DateTimeImpl();

		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		input.setPattern(pattern);

		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}

	@Override
	public DateTime createDateTimeInterval(String name, String label,Date initialValueStart, Date initialValueEnd, boolean required)	throws FactoryException {
		DateTime input = createDateTimeInterval(name, label, null, initialValueStart, initialValueEnd, required,null);
		return input;
	}

	@Override
	public DateTime createDateTimeInterval(String name, String label,Date initialValueStart, Date initialValueEnd, boolean required,Form form)	throws FactoryException {
		DateTime input = createDateTimeInterval(name, label, null, initialValueStart, initialValueEnd, required,form);
		return input;
	}

	@Override
	public DateTime createDateTimeInterval(String name, String label, String pattern, Date initialValueStart, Date initialValueEnd,	boolean required) throws FactoryException {
		DateTime input = createDateTimeInterval(name, label, pattern, initialValueStart, initialValueEnd, required, null);
		return input;
	}


	@Override
	public DateTime createDateTimeInterval(String name, String label, String pattern, Date initialValueStart, Date initialValueEnd,	boolean required,Form form) throws FactoryException {
		DateTime input = new DateTimeImpl();

		input.setLabel(label); 
		input.setDefaultValue(initialValueStart);
		input.setDefaultValue2(initialValueEnd);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(true);
		input.setPattern(pattern);

		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}

	@Override
	public InputNumber createNumber() throws FactoryException {
		return createNumber(null);
	}

	@Override
	public InputNumber createNumber(Form form) throws FactoryException {
		InputNumber input = createNumber(null, null, null, false, form);
		return input;
	}

	@Override
	public InputNumber createNumber(String name, String label, Number initialValue, boolean required) throws FactoryException {
		InputNumber input = createNumber(name, label, initialValue, required, null);
		return input;
	}

	@Override
	public InputNumber createNumber(String name, String label, Number initialValue, boolean required,Form form) throws FactoryException {
		InputNumber input = new NumberImpl();

		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);

		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}

	@Override
	public InputNumber createNumberInterval(String name, String label,Number initialValueStart, Number initialValueEnd, boolean required)	throws FactoryException {
		InputNumber input = createNumberInterval(name, label, initialValueStart, initialValueEnd, required, null);
		return input;
	}

	@Override
	public InputNumber createNumberInterval(String name, String label,Number initialValueStart, Number initialValueEnd, boolean required,Form form)	throws FactoryException {
		InputNumber input = new NumberImpl();

		input.setLabel(label); 
		input.setDefaultValue(initialValueStart);
		input.setDefaultValue2(initialValueEnd);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(true);

		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}

	@Override
	public Slider createSlider() throws FactoryException {
		return createSlider(null);
	}

	@Override
	public Slider createSlider(Form form) throws FactoryException {
		Slider input = createSlider(null, null, null, false, form);
		return input;
	}

	@Override
	public Slider createSlider(String name, String label, Number initialValue, boolean required) throws FactoryException {
		Slider input = createSlider(name, label, initialValue, required, null);
		return input;
	}

	@Override
	public Slider createSlider(String name, String label, Number initialValue, boolean required,Form form) throws FactoryException {
		Slider input = new SliderImpl();

		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);

		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}

	@Override
	public Spinner createSpinner() throws FactoryException {
		return createSpinner(null);
	}

	@Override
	public Spinner createSpinner(Form form) throws FactoryException {
		Spinner input = createSpinner(null, null, null, false, form);
		return input;
	}

	@Override
	public Spinner createSpinner(String name, String label, Number initialValue, boolean required) throws FactoryException {
		Spinner input = createSpinner(name, label, initialValue, required, null);
		return input;
	}

	@Override
	public Spinner createSpinner(String name, String label, Number initialValue, boolean required,Form form) throws FactoryException {
		Spinner input = new SpinnerImpl();

		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);

		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}


	@Override
	public  MultipleCheckBox createMultipleCheckBox() throws FactoryException {
		return createMultipleCheckBox(null);
	}

	@Override
	public  MultipleCheckBox createMultipleCheckBox(Form form) throws FactoryException {
		MultipleCheckBox input =createMultipleCheckBox(null, null, null, false, form);
		return input;
	}

	@Override
	public  MultipleCheckBox createMultipleCheckBox(String name, String label, List<SelectItem> initialValue,	boolean required) throws FactoryException {
		MultipleCheckBox input = createMultipleCheckBox(name, label, initialValue, required, null);
		return input;
	}

	@Override
	public  MultipleCheckBox createMultipleCheckBox(String name, String label, List<SelectItem> initialValue,	boolean required, Form form) throws FactoryException {
		MultipleCheckBox input = new MultipleCheckBoxImpl();
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);

		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}


	@Override
	public  MultipleChoice createMultipleChoice()
			throws FactoryException {
		MultipleChoice input = createMultipleChoice(null, null, null, false, null);
		return input;
	}

	@Override
	public  MultipleChoice createMultipleChoice(Form form)	throws FactoryException {
		MultipleChoice input = createMultipleChoice(null, null, null, false, form);
		return input;
	}

	@Override
	public  MultipleChoice createMultipleChoice(	String name, String label, List<SelectItem> initialValue,	boolean required) throws FactoryException {
		MultipleChoice input = createMultipleChoice(name, label, initialValue, required, null);
		return input;
	}

	@Override
	public  MultipleChoice createMultipleChoice(	String name, String label, List<SelectItem> initialValue,	boolean required,Form form) throws FactoryException {
		MultipleChoice input = new MultipleChoiceImpl();
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);

		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}


	@Override
	public  PickList  createPickList() throws FactoryException {
		return createPickList(null, null, null, false, null);
	}


	@Override
	public  PickList  createPickList(Form form)	throws FactoryException {
		PickList  input = createPickList(null, null, null, false, form);
		return input;
	}

	@Override
	public  PickList  createPickList(	String name, String label, List<SelectItem> initialValue,	boolean required) throws FactoryException {
		PickList  input = createPickList(name, label, initialValue, required, null);
		return input;
	}

	@Override
	public  PickList  createPickList(	String name, String label, List<SelectItem> initialValue,	boolean required,Form form) throws FactoryException {
		PickList  input = new PickListImpl();
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);

		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}


	@Override
	public  RadioButton createRadioButton()
			throws FactoryException {
		RadioButton input =  createRadioButton(null, null, null, false, null);
		return input;
	}

	@Override
	public  RadioButton createRadioButton(Form form) throws FactoryException {
		RadioButton input =  createRadioButton(null, null, null, false, form);
		return input;
	}

	@Override
	public  RadioButton  createRadioButton(	String name, String label, SelectItem initialValue, boolean required) throws FactoryException {
		RadioButton input = createRadioButton(name, label, initialValue, required, null);
		return input;
	}

	@Override
	public  RadioButton  createRadioButton(	String name, String label, SelectItem initialValue, boolean required,Form form) throws FactoryException {
		RadioButton input = new RadioButtonImpl();
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);

		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}

	@Override
	public  SelectList  createSelectList()	throws FactoryException {
		return  createSelectList(null, null, null, false, null);
	}
	
	@Override
	public SelectList createSelectList(Form form) throws FactoryException {
		return  createSelectList(null, null, null, false, form);
	}

	@Override
	public  SelectList createSelectList(String name, String label, SelectItem initialValue, boolean required) 	throws FactoryException {
		SelectList input = createSelectList(name, label, initialValue, required, null);
		return input;
	}
	
	@Override
	public  SelectList createSelectList(String name, String label, SelectItem initialValue, boolean required,Form form)		throws FactoryException {
		SelectList input =  new SelectListImpl();
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}


	@Override
	public  SingleChoice createSingleChoice() throws FactoryException {
		return createSingleChoice(null, null, null, false, null);
	}
	
	@Override
	public SingleChoice createSingleChoice(Form form) throws FactoryException {
		return createSingleChoice(null, null, null, false, form);
	}

	@Override
	public  SingleChoice createSingleChoice( String name, String label, SelectItem initialValue, boolean required)	throws FactoryException {
		SingleChoice input = createSingleChoice(name, label, initialValue, required, null);
		return input;
	}
	
	@Override
	public  SingleChoice createSingleChoice(String name, String label, SelectItem initialValue, boolean required, Form form ) throws FactoryException {
		SingleChoice input = new SingleChoiceImpl();
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}


	@Override
	public  MultipleListBox createMultipleListBox()			throws FactoryException {
		return createMultipleListBox(null, null, null, false,null);
	}
	
	@Override
	public  MultipleListBox createMultipleListBox(Form form) throws FactoryException {
		MultipleListBox input =  createMultipleListBox(null, null, null, false, form);
		return input;
	}

	@Override
	public  MultipleListBox createMultipleListBox( String name, String label, List<SelectItem> initialValue, boolean required) throws FactoryException {
		MultipleListBox input = createMultipleListBox(name, label, initialValue, required, null);
		return input;
	}
	
	@Override
	public  MultipleListBox createMultipleListBox(		String name, String label, List<SelectItem> initialValue,		boolean required, Form form) throws FactoryException {
		MultipleListBox input =  new MultipleListBoxImpl();
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		if(form != null)
			form.setField(input);

		input.setForm(form);

		return input;
	}


	@Override
	public  SingleListBox createSingleListBox()	throws FactoryException {
		return createSingleListBox(null, null, null, false,null);
	}
	
	@Override
	public  SingleListBox createSingleListBox(Form form) throws FactoryException {
		SingleListBox input =  createSingleListBox(null, null, null, false, form);
		return input;
	}

	@Override
	public  SingleListBox createSingleListBox(	String name, String label, SelectItem initialValue, boolean required)	throws FactoryException {
		SingleListBox input = createSingleListBox(name, label, initialValue, required, null);
		return input;
	}
	
	@Override
	public  SingleListBox createSingleListBox(	String name, String label, SelectItem initialValue, boolean required, Form form) throws FactoryException {
		SingleListBox input = new SingleListBoxImpl();
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		if(form != null)
			form.setField(input);

		input.setForm(form);


		return input;
	}
}
