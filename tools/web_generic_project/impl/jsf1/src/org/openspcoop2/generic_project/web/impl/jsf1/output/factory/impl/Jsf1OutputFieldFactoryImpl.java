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
package org.openspcoop2.generic_project.web.impl.jsf1.output.factory.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactory;
import org.openspcoop2.generic_project.web.impl.jsf1.output.impl.ButtonImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.output.impl.DateTimeImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.output.impl.ImageImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.output.impl.NumberImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.output.impl.OutputGroupImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.output.impl.TextImpl;
import org.openspcoop2.generic_project.web.output.Button;
import org.openspcoop2.generic_project.web.output.DateTime;
import org.openspcoop2.generic_project.web.output.Image;
import org.openspcoop2.generic_project.web.output.OutputField;
import org.openspcoop2.generic_project.web.output.OutputGroup;
import org.openspcoop2.generic_project.web.output.OutputNumber;
import org.openspcoop2.generic_project.web.output.Text;
import org.openspcoop2.generic_project.web.output.factory.OutputFieldFactory;
import org.openspcoop2.generic_project.web.view.IViewBean;
/***
 * 
 * Implementazione della factory JSF1 per gli elementi di output.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class Jsf1OutputFieldFactoryImpl implements OutputFieldFactory{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private transient WebGenericProjectFactory webGenericProjectFactory;
	@SuppressWarnings("unused")
	private transient Logger log = null;

	public Jsf1OutputFieldFactoryImpl(WebGenericProjectFactory factory,Logger log){
		this.webGenericProjectFactory = factory;
		this.log = log;
	}

	@Override
	public Text createText() {
		return  createText(null, null, null,null);
	}
	
	@Override
	public Text createText(IViewBean<?, ?> viewBean) {
		return  createText(null, null, null,viewBean);
	}

	@Override
	public Text createText(String name, String label) {
		Text t = createText(name, label, null,null);
		return t;
	}
	
	@Override
	public Text createText(String name, String label,IViewBean<?, ?> viewBean) {
		Text t = createText(name, label, null,viewBean);
		return t;
	}

	@Override
	public Text createText(String name, String label, String value) {
		Text t = createText(name, label, value, null);
		return t;
	}
	
	@Override
	public Text createText(String name, String label, String value,IViewBean<?, ?> viewBean) {
		Text t = new TextImpl();
		t.setName(name);
		t.setLabel(label);
		t.setValue(value);
		
		if(viewBean != null)
			viewBean.setField(t); 
			
		return t;
	}

	@Override
	public DateTime createDateTime() {
		DateTime d = createDateTime(null, null, null,null,null);
		return d;
	}
	
	@Override
	public DateTime createDateTime(IViewBean<?, ?> viewBean) {
		DateTime d = createDateTime(null, null, null,null,viewBean);
		return d;
	}

	@Override
	public DateTime createDateTime(String name, String label) {
		DateTime d = createDateTime(name, label, null,null,null);
		return d;
	}
	
	@Override
	public DateTime createDateTime(String name, String label,IViewBean<?, ?> viewBean) {
		DateTime d = createDateTime(name, label, null,null,viewBean);
		return d;
	}

	@Override
	public DateTime createDateTime(String name, String label, Date value) {
		DateTime d = createDateTime(name,label,null,value,null);
		return d;
	}
	
	@Override
	public DateTime createDateTime(String name, String label, Date value,IViewBean<?, ?> viewBean) {
		DateTime d = createDateTime(name,label,null,value,viewBean);
		return d;
	}
	
	@Override
	public DateTime createDateTime(String name, String label, String pattern) {
		DateTime d = createDateTime(name,label,pattern,null,null);
		return d;
	}
	
	@Override
	public DateTime createDateTime(String name, String label, String pattern,IViewBean<?, ?> viewBean) {
		DateTime d = createDateTime(name,label,pattern,null,viewBean);
		return d;
	}
	
	@Override
	public DateTime createDateTime(String name, String label, String pattern, Date value) {
		DateTime d = createDateTime(name, label, pattern, value, null);
		return d;
	}
	
	@Override
	public DateTime createDateTime(String name, String label, String pattern, Date value,IViewBean<?, ?> viewBean) {
		DateTime d = new DateTimeImpl();
		d.setName(name);
		d.setLabel(label);
		d.setValue(value);
		d.setPattern(pattern); 
		
		if(viewBean != null)
			viewBean.setField(d); 

		return d;
	}

	@Override
	public OutputNumber createNumber() {
		OutputNumber num = createNumber(null, null, null,null);
		return num;
	}
	
	@Override
	public OutputNumber createNumber(IViewBean<?, ?> viewBean) {
		OutputNumber num = createNumber(null, null, null,viewBean);
		return num;
	}
	
	@Override
	public OutputNumber createNumber(String name, String label) {
		OutputNumber num = createNumber(name, label, null,null);
		return num;
	}
	
	@Override
	public OutputNumber createNumber(String name, String label,IViewBean<?, ?> viewBean) {
		OutputNumber num = createNumber(name, label, null,viewBean);
		return num;
	}

	@Override
	public OutputNumber createNumber(String name, String label, Number value) {
		OutputNumber num = createNumber(name, label, value, null);
		return num;
	}
	
	@Override
	public OutputNumber createNumber(String name, String label, Number value,IViewBean<?, ?> viewBean) {
		OutputNumber num = new NumberImpl();
		num.setName(name);
		num.setLabel(label);
		num.setValue(value);
		
		if(viewBean != null)
			viewBean.setField(num); 

		return num;
	}

	@Override
	public Image createImage() {
		Image elem = createImage(null, null, null, null, null,null);
		return elem;
	}
	
	@Override
	public Image createImage(IViewBean<?, ?> viewBean) {
		Image elem = createImage(null, null, null, null, null,viewBean);
		return elem;
	}
	
	@Override
	public Image createImage(String name, String label) {
		Image elem = createImage(name, label, null, null, null,null);
		return elem;
	}
	
	@Override
	public Image createImage(String name, String label,IViewBean<?, ?> viewBean) {
		Image elem = createImage(name, label, null, null, null,viewBean);
		return elem;
	}
	
	@Override
	public Image createImage(String name, String label, String image) {
		Image elem = createImage(name, label, image, null, null,null);
		return elem;
	}
	
	@Override
	public Image createImage(String name, String label, String image,IViewBean<?, ?> viewBean) {
		Image elem = createImage(name, label, image, null, null,viewBean);
		return elem;
	}
	
	@Override
	public Image createImage(String name, String label, String image,String title) {
		Image elem = createImage(name, label, image, title, title,null);
		return elem;
	}
	
	@Override
	public Image createImage(String name, String label, String image,String title,IViewBean<?, ?> viewBean) {
		Image elem = createImage(name, label, image, title, title,viewBean);
		return elem;
	}
	
	@Override
	public Image createImage(String name, String label, String image, String title, String alt) {
		Image elem = createImage(name, label, image, title, alt, null); 
		return elem;
	}
	
	@Override
	public Image createImage(String name, String label, String image, String title, String alt,IViewBean<?, ?> viewBean) {
		Image elem = new ImageImpl();
		elem.setName(name);
		elem.setLabel(label);
		elem.setImage(image);
		elem.setTitle(title);
		elem.setAlt(alt);
		

		if(viewBean != null)
			viewBean.setField(elem);
		
		return elem;
	}

	@Override
	public Button createButton() {
		Button button = createButton(null, null, null,null,null,null,null);
		return button;
	}
	
	@Override
	public Button createButton(IViewBean<?, ?> viewBean) {
		Button button = createButton(null, null, null,null,null,null,viewBean);
		return button;
	}
	
	@Override
	public Button createButton(String name, String label) {
		Button button = createButton(name, label, null,null,null,null,null);
		return button;
	}
	
	@Override
	public Button createButton(String name, String label,IViewBean<?, ?> viewBean) {
		Button button = createButton(name, label, null,null,null,null,viewBean);
		return button;
	}
	
	@Override
	public Button createButton(String name, String label, String href) {
		Button button = createButton(name, label, href, null,null,null, null);
		return button;
	}
	
	@Override
	public Button createButton(String name, String label, String href,IViewBean<?, ?> viewBean) {
		Button button = createButton(name, label, href, null,null,null,viewBean);
		return button;
	}
	
	@Override
	public Button createButton(String name, String label, String href, String image) {
		Button button = createButton(name, label, href, image, null,null,null);
		return button;
	}
	
	@Override
	public Button createButton(String name, String label, String href, String image,IViewBean<?, ?> viewBean) {
		Button button = createButton(name, label, href, image, null,null,viewBean);
		return button;
	}
	
	@Override
	public Button createButton(String name, String label, String href, String image, String title) {
		Button button = createButton(name, label, href, image, title, title,null);
		return button;
	}
	
	@Override
	public Button createButton(String name, String label, String href, String image, String title,IViewBean<?, ?> viewBean) {
		Button button = createButton(name, label, href, image, title, title,viewBean);
		return button;
	}
	
	@Override
	public Button createButton(String name, String label, String href,String image, String title, String alt) {
		Button button = createButton(name, label, href, image, title, alt, null);
		return button;
	}
	
	@Override
	public Button createButton(String name, String label, String href,String image, String title, String alt,IViewBean<?, ?> viewBean) {
		Button button =   new ButtonImpl();
		button.setName(name);
		button.setLabel(label);
		button.setImage(image);
		button.setTitle(title);
		button.setAlt(alt);
		button.setHref(href); 
		
		if(viewBean != null)
			viewBean.setField(button);
		
		return button;
	}

	@Override
	public OutputGroup createOutputGroup() {
		OutputGroup group = createOutputGroup(null,null,null,new ArrayList<OutputField<?>>(),null); 
		return group;
	}
	
	@Override
	public OutputGroup createOutputGroup(IViewBean<?, ?> viewBean) {
		OutputGroup group = createOutputGroup(null,null,null,new ArrayList<OutputField<?>>(),viewBean); 
		return group;
	}
	
	@Override
	public OutputGroup createOutputGroup(String id) {
		OutputGroup group = createOutputGroup(id,null,null,new ArrayList<OutputField<?>>(),null); 
		return group;
	}
	
	@Override
	public OutputGroup createOutputGroup(String id,IViewBean<?, ?> viewBean) {
		OutputGroup group = createOutputGroup(id,null,null,new ArrayList<OutputField<?>>(),viewBean); 
		return group;
	}

	@Override
	public OutputGroup createOutputGroup(String id, Integer columns) {
		OutputGroup group =  createOutputGroup(id,null,columns,new ArrayList<OutputField<?>>(),null); 
		return group;
	}
	
	@Override
	public OutputGroup createOutputGroup(String id, Integer columns,IViewBean<?, ?> viewBean) {
		OutputGroup group =  createOutputGroup(id,null,columns,new ArrayList<OutputField<?>>(),viewBean); 
		return group;
	}

	@Override
	public OutputGroup createOutputGroup(String id, Integer columns,	List<OutputField<?>> listaOutput) {
		OutputGroup group =  createOutputGroup(id,null,columns,listaOutput,null); 
		return group;
	}
	
	@Override
	public OutputGroup createOutputGroup(String id, Integer columns,	List<OutputField<?>> listaOutput,IViewBean<?, ?> viewBean) {
		OutputGroup group =  createOutputGroup(id,null,columns,listaOutput,viewBean); 
		return group;
	}
	
	@Override
	public OutputGroup createOutputGroup(String id,	List<OutputField<?>> listaOutput) {
		OutputGroup group = createOutputGroup(id,null,null,listaOutput,null);
		return group;
	}
	
	@Override
	public OutputGroup createOutputGroup(String id,	List<OutputField<?>> listaOutput,IViewBean<?, ?> viewBean) {
		OutputGroup group = createOutputGroup(id,null,null,listaOutput,viewBean);
		return group;
	}
	
	@Override
	public OutputGroup createOutputGroup(String id, String label) {
		OutputGroup group = createOutputGroup(id,label,null,new ArrayList<OutputField<?>>(),null); 
		return group;
	}
	
	@Override
	public OutputGroup createOutputGroup(String id, String label,IViewBean<?, ?> viewBean) {
		OutputGroup group = createOutputGroup(id,label,null,new ArrayList<OutputField<?>>(),viewBean); 
		return group;
	}
	
	@Override
	public OutputGroup createOutputGroup(String id, String label,Integer columns) {
		OutputGroup group = createOutputGroup(id,label,columns,new ArrayList<OutputField<?>>(),null); 
		return group;
	}
	
	@Override
	public OutputGroup createOutputGroup(String id, String label,Integer columns,IViewBean<?, ?> viewBean) {
		OutputGroup group = createOutputGroup(id,label,columns,new ArrayList<OutputField<?>>(),viewBean); 
		return group;
	}
	
	@Override
	public OutputGroup createOutputGroup(String id, String label, Integer columns, List<OutputField<?>> listaOutput) {
		OutputGroup group = createOutputGroup(id, label, columns, listaOutput, null);
		return group;
	}
	
	@Override
	public OutputGroup createOutputGroup(String id, String label, Integer columns, List<OutputField<?>> listaOutput,IViewBean<?, ?> viewBean) {
		OutputGroup group = new OutputGroupImpl();

		group.setId(id);
		group.setLabel(label);
		group.setFields(listaOutput);
		group.setColumns(columns); 
		
		return group;
	}

}
