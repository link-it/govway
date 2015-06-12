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
import org.openspcoop2.generic_project.web.output.OutputGroup;
import org.openspcoop2.generic_project.web.output.OutputNumber;
import org.openspcoop2.generic_project.web.output.Text;
import org.openspcoop2.generic_project.web.output.factory.OutputFieldFactory;

/**
 * Implementazione della factory per gli elementi di output.
 * 
 * @author Pintori Giuliano (pintori@link.it)
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
	public DateTime createDateTime() {
		return new DateTimeImpl();
	}

	@Override
	public OutputNumber createNumber() {
		return new NumberImpl();
	}

	@Override
	public Image createImage() {
		return new ImageImpl();
	}

	@Override
	public Button createButton() {
		return new ButtonImpl();
	}
	
	@Override
	public OutputGroup createOutputGroup() {
		return new OutputGroupImpl();
	}

}
