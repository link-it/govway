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
package org.openspcoop2.generic_project.web.impl.jsf1;

import org.slf4j.Logger;
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
	
	public FactoryJsf1Impl(Logger log){
		this(CostantiJsf1Impl.FACTORY_NAME, log);
	}
	
	public FactoryJsf1Impl(String name,Logger log){
		this.log = log;
		this.factoryName = name;
		this.log.debug("Factory ["+this.factoryName+"] Inizializzata."); 
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

}
