/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.report;

import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertyExpression;

/**
 * CustomJRField
 * Wrapper che implementa l'interfaccia net.sf.jasperreports.engine.JRField per la lettura del valore di una cella
 * dai datasource di tipo {@link net.sf.dynamicreports.report.datasource.DRDataSource}.
 * La classe mette a disposizione un iteratore per accedere alle righe e il metodo 
 * {@link net.sf.dynamicreports.report.datasource.DRDataSource#getFieldValue(JRField)} per leggere il valore della colonna desiderata.
 * La classe DRDataSource contiene una Map<String,Object> <nomeColonna,valore> per ogni riga e l'implementazione del metodo getFieldValue 
 * utilizza il metodo field.getName per avere il nome della colonna da leggere.  
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class CustomJRField implements JRField {
	
	private String name;
	
	public CustomJRField(String name) {
		this.name = name;
	}

	@Override
	public JRPropertiesHolder getParentProperties() {
		return null;
	}

	@Override
	public JRPropertiesMap getPropertiesMap() {
		return null;
	}

	@Override
	public boolean hasProperties() {
		return false;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public JRPropertyExpression[] getPropertyExpressions() {
		return new JRPropertyExpression[0];
	}

	@Override
	public Class<?> getValueClass() {
		return null;
	}

	@Override
	public String getValueClassName() {
		return null;
	}

	@Override
	public void setDescription(String arg0) {
		// donothing
	}
	
	@Override
	public Object clone()
	{
		throw new UnsupportedOperationException();
	}
}
