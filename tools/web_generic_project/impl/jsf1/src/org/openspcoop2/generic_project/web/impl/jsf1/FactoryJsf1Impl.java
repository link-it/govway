/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
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
package org.openspcoop2.generic_project.web.impl.jsf1;

import org.slf4j.Logger;

import java.awt.Font;

import org.openspcoop2.generic_project.web.core.Utils;
import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactory;
import org.openspcoop2.generic_project.web.impl.jsf1.input.factory.impl.Jsf1InputFieldFactoryImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.output.factory.impl.Jsf1OutputFieldFactoryImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.table.factory.impl.Jsf1TableFactoryImpl;
import org.openspcoop2.generic_project.web.input.factory.InputFieldFactory;
import org.openspcoop2.generic_project.web.output.factory.OutputFieldFactory;
import org.openspcoop2.generic_project.web.table.factory.TableFactory;

/***
 * 
 * Implementazione JSF1 della Factory.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class FactoryJsf1Impl implements WebGenericProjectFactory{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String factoryName = null;
	private transient Logger log= null;
	
	private String fontName = "";
	private int fontStyle, fontSize;
	
	public FactoryJsf1Impl(Logger log){
		this(CostantiJsf1Impl.FACTORY_NAME, log);
	}
	
	public FactoryJsf1Impl(String name,Logger log){
		this.log = log;
		this.factoryName = name;
		this.log.debug("Factory ["+this.factoryName+"] Inizializzata."); 
		this.fontName = "Verdana";
		this.fontSize = 11;
		this.fontStyle = Font.PLAIN;
	}

	@Override
	public OutputFieldFactory getOutputFieldFactory() throws FactoryException {
		return new Jsf1OutputFieldFactoryImpl(this,this.log);
	}

	@Override
	public InputFieldFactory getInputFieldFactory() throws FactoryException {
		return new Jsf1InputFieldFactoryImpl(this,this.log);
	}
	
	@Override
	public TableFactory getTableFactory() throws FactoryException {
		return new Jsf1TableFactoryImpl(this,this.log); 
	}

	@Override
	public String getFactoryName() {
		return this.factoryName;
	}

	@Override
	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}

	@Override
	public Utils getUtils() throws FactoryException {
		return org.openspcoop2.generic_project.web.impl.jsf1.utils.Utils.getInstance();
	}

	@Override
	public String getFontName() {
		return this.fontName;
	}
	@Override
	public void setFontName(String fontName) {
		this.fontName = fontName;
	}
	@Override
	public int getFontStyle() {
		return this.fontStyle;
	}
	@Override
	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}
	@Override
	public int getFontSize() {
		return this.fontSize;
	}
	@Override
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
}
