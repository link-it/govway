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
package org.openspcoop2.generic_project.web.presentation.field;

import java.util.ArrayList;
import java.util.List;

/**
*
* OutputGroup Definisce un container per raccogliere le informazioni da visualizzare nelle pagine.
* idGroup = id del componente nella pagina;
* columns = numero di colonne da utilizzare;
* fields = lista dei {@link OutputField} da visualizzare;
* label = label del gruppo;
* rendered = indicazione se il componente deve essere visualizzato.
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class OutputGroup {

	private String idGroup = null;
	
	private Integer columns = null;
		
	private List<OutputField<?>> fields = null;
	
	private boolean rendered = true;
	
	private String label = null;
	
	private String columnClasses = null;
	
	private String styleClass = null;
	
	public OutputGroup(){
		this.fields = new ArrayList<OutputField<?>>();
		this.columns = 2;
		this.columnClasses = ""; //"gridContent verticalAlignTop";
		this.styleClass = "";
	}

	public String getIdGroup() {
		return this.idGroup;
	}

	public void setIdGroup(String idGroup) {
		this.idGroup = idGroup;
	}

	public Integer getColumns() {
		return this.columns;
	}

	public void setColumns(Integer columns) {
		this.columns = columns;
	}

	public List<OutputField<?>> getFields() {
		return this.fields;
	}

	public void setFields(List<OutputField<?>> listaOutput) {
		this.fields = listaOutput;
	}

	public boolean isRendered() {
		return this.rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public void addField(OutputField<?> field){
		this.fields.add(field);
	}

	public String getColumnClasses() {
		return this.columnClasses;
	}

	public void setColumnClasses(String columnClasses) {
		this.columnClasses = columnClasses;
	}

	public String getStyleClass() {
		return this.styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	
}
