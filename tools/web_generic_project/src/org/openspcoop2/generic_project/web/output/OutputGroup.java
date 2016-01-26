/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.generic_project.web.output;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/***
 * 
 * Interfaccia che descrive un elemento di output di tipo Group, un contenitore per gruppi di elementi.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public interface OutputGroup extends Serializable {

	
	// Id html del container
	public String getId();
	public void setId(String id);

	// Numero di colonne in cui vengono divisi i componenti
	public Integer getColumns();
	public void setColumns(Integer columns);

	// Lista dei field da visualizzare
	public List<OutputField<?>> getFields();
	public void setFields(List<OutputField<?>> listaOutput);

	// flag visualizzato si/no
	public boolean isRendered();
	public void setRendered(boolean rendered);

	// Label opzionale del gruppo
	public String getLabel();
	public void setLabel(String label);

	// aggiungi field
	public void addField(OutputField<?> field);
	
	public Map<String, OutputField<?>> getFieldsMap();
	public void setFieldsMap(Map<String, OutputField<?>> fields);
	public void setField(String fieldName, OutputField<?> field);
	public void setField(OutputField<?> field);
	public OutputField<?> getField(String id) throws Exception;

	// classi css per le colonne 
	public String getColumnClasses();
	public void setColumnClasses(String columnClasses);

	// classi css per il container
	public String getStyleClass();
	public void setStyleClass(String styleClass);
	
}
