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
package org.openspcoop2.generic_project.web.factory;

import java.io.Serializable;

import org.openspcoop2.generic_project.web.core.Utils;
import org.openspcoop2.generic_project.web.input.factory.InputFieldFactory;
import org.openspcoop2.generic_project.web.output.factory.OutputFieldFactory;
import org.openspcoop2.generic_project.web.table.factory.TableFactory;

/***
 * 
 * Interfaccia base che definisce la factory della libreria.
 * Descrive le interfacce dei tipi di factory disponibili.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public interface WebGenericProjectFactory extends Serializable{
	
	public String getFactoryName();
	public void setFactoryName(String name);
	
	// Factory che definisce gli elementi di output
	public OutputFieldFactory getOutputFieldFactory() throws FactoryException; 

	// Factory che definisce gli elementi di input
	public InputFieldFactory getInputFieldFactory() throws FactoryException;
	
	// Factory che definisce gli elementi di tipo tabella
	public TableFactory getTableFactory() throws FactoryException;
	
	public Utils getUtils() throws FactoryException;
}
