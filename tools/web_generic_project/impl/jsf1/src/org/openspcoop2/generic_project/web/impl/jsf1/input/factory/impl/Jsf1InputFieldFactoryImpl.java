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

import org.apache.log4j.Logger;
import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactory;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.BooleanCheckBoxImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.DateTimeImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.InputSecretImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.MultipleCheckBoxImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.MultipleChoiceImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.NumberImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.PickListImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.RadioButtonImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.SelectListImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.SingleChoiceImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.TextAreaImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.TextImpl;
import org.openspcoop2.generic_project.web.input.BooleanCheckBox;
import org.openspcoop2.generic_project.web.input.DateTime;
import org.openspcoop2.generic_project.web.input.HtmlOption;
import org.openspcoop2.generic_project.web.input.InputNumber;
import org.openspcoop2.generic_project.web.input.InputSecret;
import org.openspcoop2.generic_project.web.input.MultipleCheckBox;
import org.openspcoop2.generic_project.web.input.MultipleChoice;
import org.openspcoop2.generic_project.web.input.PickList;
import org.openspcoop2.generic_project.web.input.RadioButton;
import org.openspcoop2.generic_project.web.input.SelectList;
import org.openspcoop2.generic_project.web.input.SingleChoice;
import org.openspcoop2.generic_project.web.input.Text;
import org.openspcoop2.generic_project.web.input.TextArea;
import org.openspcoop2.generic_project.web.input.factory.InputFieldFactory;

/***
 * 
 * Implementazione della factory  JSF1  degli elementi di input.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
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
	public TextArea createTextArea() throws FactoryException {
		return new TextAreaImpl();
	}

	@Override
	public InputSecret createInputSecret() throws FactoryException {
		return new InputSecretImpl();
	}

	@Override
	public BooleanCheckBox createBooleanCheckBox() throws FactoryException {
		return new BooleanCheckBoxImpl();
	}

	@Override
	public DateTime createDateTime() throws FactoryException {
		return new DateTimeImpl();
	}

	@Override
	public InputNumber createNumber() throws FactoryException {
		return new NumberImpl();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <OptionType extends HtmlOption> MultipleCheckBox<OptionType> createMultipleCheckBox()
			throws FactoryException {
		return (MultipleCheckBox<OptionType>) new MultipleCheckBoxImpl();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <OptionType extends HtmlOption> MultipleChoice<OptionType> createMultipleChoice()
			throws FactoryException {
		return (MultipleChoice<OptionType>) new MultipleChoiceImpl();
	}
	@SuppressWarnings("unchecked")
	@Override
	public <OptionType extends HtmlOption> PickList<OptionType> createPickList()
			throws FactoryException {
		return (PickList<OptionType>) new PickListImpl(); 
	}
	@SuppressWarnings("unchecked")
	@Override
	public <OptionType extends HtmlOption> RadioButton<OptionType> createRadioButton()
			throws FactoryException {
		return (RadioButton<OptionType>) new RadioButtonImpl();
	}
	@SuppressWarnings("unchecked")
	@Override
	public <OptionType extends HtmlOption> SelectList<OptionType> createSelectList()
			throws FactoryException {
		return (SelectList<OptionType>) new SelectListImpl();
	}
	@SuppressWarnings("unchecked")
	@Override
	public <OptionType extends HtmlOption> SingleChoice<OptionType> createSingleChoice()
			throws FactoryException {
		return (SingleChoice<OptionType>) new SingleChoiceImpl();
	}
	

}
