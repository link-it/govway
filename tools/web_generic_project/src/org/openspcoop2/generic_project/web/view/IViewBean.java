/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.web.view;

import java.io.Serializable;
import java.util.Map;

import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactory;
import org.openspcoop2.generic_project.web.output.OutputField;

/***
 * 
 * Interfaccia base per i Bean che verranno mostrati sull'interfaccia grafica.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public interface IViewBean<DTOType,KeyType> extends Serializable  {

	// Metodo di init dei componenti del bean
	public void init() throws Exception;
	
	// Id
	public KeyType getId();
	public void setId(KeyType id);
	
	// DTO
	public void setDTO(DTOType dto);
	public DTOType getDTO();
	
	// Map con i field
	public Map<String, OutputField<?>> getFields();
	public void setFields(Map<String, OutputField<?>> fields);
	public void setField(String fieldName, OutputField<?> field);
	public void setField(OutputField<?> field);
	public OutputField<?> getField(String id) throws Exception;


	// Factory per i componenti
	public WebGenericProjectFactory getFactory() throws FactoryException;
	public void setFactory(WebGenericProjectFactory factory) throws FactoryException;

}
