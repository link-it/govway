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
package org.openspcoop2.generic_project.web.impl.jsf2.output.factory.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactory;
import org.openspcoop2.generic_project.web.impl.jsf2.output.impl.ButtonImpl;
import org.openspcoop2.generic_project.web.impl.jsf2.output.impl.DateTimeImpl;
import org.openspcoop2.generic_project.web.impl.jsf2.output.impl.ImageImpl;
import org.openspcoop2.generic_project.web.impl.jsf2.output.impl.NumberImpl;
import org.openspcoop2.generic_project.web.impl.jsf2.output.impl.OutputGroupImpl;
import org.openspcoop2.generic_project.web.impl.jsf2.output.impl.TextImpl;
import org.openspcoop2.generic_project.web.output.Button;
import org.openspcoop2.generic_project.web.output.DateTime;
import org.openspcoop2.generic_project.web.output.Image;
import org.openspcoop2.generic_project.web.output.OutputField;
import org.openspcoop2.generic_project.web.output.OutputGroup;
import org.openspcoop2.generic_project.web.output.OutputNumber;
import org.openspcoop2.generic_project.web.output.Text;
import org.openspcoop2.generic_project.web.output.factory.OutputFieldFactory;

/**
 * Implementazione della factory per gli elementi di output.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public class Jsf2OutputFieldFactoryImpl implements OutputFieldFactory{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private transient WebGenericProjectFactory webGenericProjectFactory;
	@SuppressWarnings("unused")
	private transient Logger log = null;

	public Jsf2OutputFieldFactoryImpl(WebGenericProjectFactory factory,Logger log){
		this.webGenericProjectFactory = factory;
		this.log = log;
	}

	@Override
	public Text createText() {
		return new TextImpl();
	}

	@Override
	public Text createText(String name, String label) {
		Text t = createText(name, label, null);
		return t;
	}

	@Override
	public Text createText(String name, String label, String value) {
		Text t = createText();
		t.setName(name);
		t.setLabel(label);
		t.setValue(value); 
		return t;
	}

	@Override
	public DateTime createDateTime() {
		return new DateTimeImpl();
	}

	@Override
	public DateTime createDateTime(String name, String label) {
		DateTime d = createDateTime(name, label, null,null);
		return d;
	}

	@Override
	public DateTime createDateTime(String name, String label, Date value) {
		DateTime d = createDateTime(name,label,null,value);
		return d;
	}
	
	@Override
	public DateTime createDateTime(String name, String label, String pattern) {
		DateTime d = createDateTime(name,label,pattern,null);
		return d;
	}
	
	@Override
	public DateTime createDateTime(String name, String label, String pattern, Date value) {
		DateTime d = createDateTime();
		d.setName(name);
		d.setLabel(label);
		d.setValue(value);
		d.setPattern(pattern); 

		return d;
	}

	@Override
	public OutputNumber createNumber() {
		return new NumberImpl();
	}
	
	@Override
	public OutputNumber createNumber(String name, String label) {
		OutputNumber num = createNumber(name, label, null);
		return num;
	}

	@Override
	public OutputNumber createNumber(String name, String label, Number value) {
		OutputNumber num = createNumber();
		num.setName(name);
		num.setLabel(label);
		num.setValue(value);

		return num;
	}

	@Override
	public Image createImage() {
		return new ImageImpl();
	}
	
	@Override
	public Image createImage(String name, String label) {
		Image elem = createImage(name, label, null, null, null);
		return elem;
	}
	
	@Override
	public Image createImage(String name, String label, String image) {
		Image elem = createImage(name, label, image, null, null);
		return elem;
	}
	
	@Override
	public Image createImage(String name, String label, String image,String title) {
		Image elem = createImage(name, label, image, title, title);
		return elem;
	}
	
	@Override
	public Image createImage(String name, String label, String image,
			String title, String alt) {
		Image elem = createImage();
		elem.setName(name);
		elem.setLabel(label);
		elem.setImage(image);
		elem.setTitle(title);
		elem.setAlt(alt);
		
		return elem;
	}

	@Override
	public Button createButton() {
		return new ButtonImpl();
	}
	
	@Override
	public Button createButton(String name, String label) {
		Button button = createButton(name, label, null,null,null,null);
		return button;
	}
	
	@Override
	public Button createButton(String name, String label, String href) {
		Button button = createButton(name, label, href, null,null,null);
		return button;
	}
	
	@Override
	public Button createButton(String name, String label, String href, String image) {
		Button button = createButton(name, label, href, image, null,null);
		return button;
	}
	
	@Override
	public Button createButton(String name, String label, String href, String image, String title) {
		Button button = createButton(name, label, href, image, title, title);
		return button;
	}
	
	@Override
	public Button createButton(String name, String label, String href,String image, String title, String alt) {
		Button button = createButton();
		button.setName(name);
		button.setLabel(label);
		button.setImage(image);
		button.setTitle(title);
		button.setAlt(alt);
		button.setHref(href); 
		
		return button;
	}

	@Override
	public OutputGroup createOutputGroup() {
		return new OutputGroupImpl();
	}
	
	@Override
	public OutputGroup createOutputGroup(String id) {
		OutputGroup group = createOutputGroup(id,null,null,new ArrayList<OutputField<?>>()); 
		return group;
	}

	@Override
	public OutputGroup createOutputGroup(String id, Integer columns) {
		OutputGroup group =  createOutputGroup(id,null,columns,new ArrayList<OutputField<?>>()); 
		return group;
	}

	@Override
	public OutputGroup createOutputGroup(String id, Integer columns,	List<OutputField<?>> listaOutput) {
		OutputGroup group =  createOutputGroup(id,null,columns,listaOutput); 
		return group;
	}
	
	@Override
	public OutputGroup createOutputGroup(String id,	List<OutputField<?>> listaOutput) {
		OutputGroup group = createOutputGroup(id,null,null,listaOutput);
		return group;
	}
	
	@Override
	public OutputGroup createOutputGroup(String id, String label) {
		OutputGroup group = createOutputGroup(id,label,null,new ArrayList<OutputField<?>>()); 
		return group;
	}
	
	@Override
	public OutputGroup createOutputGroup(String id, String label,Integer columns) {
		OutputGroup group = createOutputGroup(id,label,columns,new ArrayList<OutputField<?>>()); 
		return group;
	}
	
	@Override
	public OutputGroup createOutputGroup(String id, String label,
			Integer columns, List<OutputField<?>> listaOutput) {
		OutputGroup group = createOutputGroup();

		group.setId(id);
		group.setLabel(label);
		group.setFields(listaOutput);
		group.setColumns(columns); 

		return group;
	}

}
