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
package org.openspcoop2.generic_project.web.impl.jsf1.input.factory.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactory;
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
		return new TextImpl();
	}
	
	@Override
	public Text createText(String name, String label, String initialValue, boolean required) throws FactoryException {
		Text input = createText();
		
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		return input;
	}
	
	@Override
	public Text createTextInterval(String name, String label,String initialValueStart, String initialValueEnd, boolean required)	throws FactoryException {
		Text input = createText();
		
		input.setLabel(label); 
		input.setDefaultValue(initialValueStart);
		input.setDefaultValue2(initialValueEnd);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(true);
		
		return input;
	}

	@Override
	public TextArea createTextArea() throws FactoryException {
		return new TextAreaImpl();
	}
	
	@Override
	public TextArea createTextArea(String name, String label, String initialValue, boolean required) throws FactoryException {
		TextArea input = createTextArea();
		
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		return input;
	}
	

	@Override
	public InputSecret createInputSecret() throws FactoryException {
		return new InputSecretImpl();
	}
	
	@Override
	public InputSecret createInputSecret(String name, String label, String initialValue, boolean required) throws FactoryException {
		InputSecret input = createInputSecret();
		
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		return input;
	}

	@Override
	public BooleanCheckBox createBooleanCheckBox() throws FactoryException {
		return new BooleanCheckBoxImpl();
	}
	
	@Override
	public BooleanCheckBox createBooleanCheckBox(String name, String label, Boolean initialValue, boolean required) throws FactoryException {
		BooleanCheckBox input = createBooleanCheckBox();
		
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		return input;
	}
	
	

	@Override
	public DateTime createDateTime() throws FactoryException {
		return new DateTimeImpl();
	}
	
	@Override
	public DateTime createDateTime(String name, String label, Date initialValue, boolean required) throws FactoryException {
		DateTime input = createDateTime(name, label, null, initialValue, required);
		return input;
	}
	
	@Override
	public DateTime createDateTime(String name, String label, String pattern,Date initialValue, boolean required) throws FactoryException {
		DateTime input = createDateTime();
		
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		input.setPattern(pattern);
		
		return input;
	}
	
	@Override
	public DateTime createDateTimeInterval(String name, String label,Date initialValueStart, Date initialValueEnd, boolean required)	throws FactoryException {
		DateTime input = createDateTimeInterval(name, label, null, initialValueStart, initialValueEnd, required);
		return input;
	}
	
	@Override
	public DateTime createDateTimeInterval(String name, String label, String pattern, Date initialValueStart, Date initialValueEnd,	boolean required) throws FactoryException {
		DateTime input = createDateTime();
		
		input.setLabel(label); 
		input.setDefaultValue(initialValueStart);
		input.setDefaultValue2(initialValueEnd);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(true);
		input.setPattern(pattern);
		
		return input;
	}

	@Override
	public InputNumber createNumber() throws FactoryException {
		return new NumberImpl();
	}
	
	@Override
	public InputNumber createNumber(String name, String label, Number initialValue, boolean required) throws FactoryException {
		InputNumber input = createNumber();
		
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		return input;
	}
	
	@Override
	public InputNumber createNumberInterval(String name, String label,Number initialValueStart, Number initialValueEnd, boolean required)	throws FactoryException {
		InputNumber input = createNumber();
		
		input.setLabel(label); 
		input.setDefaultValue(initialValueStart);
		input.setDefaultValue2(initialValueEnd);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(true);
		
		return input;
	}
	
	@Override
	public Slider createSlider() throws FactoryException {
		return new SliderImpl();
	}
	
	@Override
	public Slider createSlider(String name, String label, Number initialValue, boolean required) throws FactoryException {
		Slider input = createSlider();
		
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		return input;
	}
	
	@Override
	public Spinner createSpinner() throws FactoryException {
		return new SpinnerImpl();
	}
	
	@Override
	public Spinner createSpinner(String name, String label, Number initialValue, boolean required) throws FactoryException {
		Spinner input = createSpinner();
		
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		return input;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <OptionType extends HtmlOption> MultipleCheckBox<OptionType> createMultipleCheckBox()
			throws FactoryException {
		return (MultipleCheckBox<OptionType>) new MultipleCheckBoxImpl();
	}
	
	@Override
	public <OptionType extends HtmlOption> MultipleCheckBox<OptionType> createMultipleCheckBox(
			String name, String label, List<OptionType> initialValue,	boolean required) throws FactoryException {
		
		MultipleCheckBox<OptionType> input = createMultipleCheckBox();
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		return input;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <OptionType extends HtmlOption> MultipleChoice<OptionType> createMultipleChoice()
			throws FactoryException {
		return (MultipleChoice<OptionType>) new MultipleChoiceImpl();
	}
	
	@Override
	public <OptionType extends HtmlOption> MultipleChoice<OptionType> createMultipleChoice(	String name, String label, List<OptionType> initialValue,	boolean required) throws FactoryException {
		MultipleChoice<OptionType> input = createMultipleChoice();
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		return input;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <OptionType extends HtmlOption> PickList<OptionType> createPickList()
			throws FactoryException {
		return (PickList<OptionType>) new PickListImpl(); 
	}
	
	@Override
	public <OptionType extends HtmlOption> PickList<OptionType> createPickList(	String name, String label, List<OptionType> initialValue,	boolean required) throws FactoryException {
		PickList<OptionType> input = createPickList();
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		return input;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <OptionType extends HtmlOption> RadioButton<OptionType> createRadioButton()
			throws FactoryException {
		return (RadioButton<OptionType>) new RadioButtonImpl();
	}
	
	@Override
	public <OptionType extends HtmlOption> RadioButton<OptionType> createRadioButton(
			String name, String label, OptionType initialValue, boolean required)
			throws FactoryException {
		RadioButton<OptionType> input = createRadioButton();
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		return input;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <OptionType extends HtmlOption> SelectList<OptionType> createSelectList()
			throws FactoryException {
		return (SelectList<OptionType>) new SelectListImpl();
	}
	
	@Override
	public <OptionType extends HtmlOption> SelectList<OptionType> createSelectList(
			String name, String label, OptionType initialValue, boolean required)
			throws FactoryException {
		SelectList<OptionType> input = createSelectList();
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		return input;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <OptionType extends HtmlOption> SingleChoice<OptionType> createSingleChoice()
			throws FactoryException {
		return (SingleChoice<OptionType>) new SingleChoiceImpl();
	}
	
	@Override
	public <OptionType extends HtmlOption> SingleChoice<OptionType> createSingleChoice(
			String name, String label, OptionType initialValue, boolean required)
			throws FactoryException {
		SingleChoice<OptionType> input = createSingleChoice();
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		return input;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <OptionType extends HtmlOption> MultipleListBox<OptionType> createMultipleListBox()
			throws FactoryException {
			return (MultipleListBox<OptionType>) new MultipleListBoxImpl();
	}
	
	@Override
	public <OptionType extends HtmlOption> MultipleListBox<OptionType> createMultipleListBox(
			String name, String label, List<OptionType> initialValue,
			boolean required) throws FactoryException {
		MultipleListBox<OptionType> input = createMultipleListBox();
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		return input;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <OptionType extends HtmlOption> SingleListBox<OptionType> createSingleListBox()
			throws FactoryException {
		return (SingleListBox<OptionType>) new SingleListBoxImpl();
	}
	
	@Override
	public <OptionType extends HtmlOption> SingleListBox<OptionType> createSingleListBox(
			String name, String label, OptionType initialValue, boolean required)
			throws FactoryException {
		SingleListBox<OptionType> input = createSingleListBox();
		input.setLabel(label); 
		input.setDefaultValue(initialValue);
		input.setRequired(required);
		input.setName(name);
		input.setInterval(false);
		
		return input;
	}
}
