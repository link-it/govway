/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.transazioni.exporter;

import java.util.List;

/**
 * ExporterCsvProperties
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ExporterCsvProperties extends ExporterProperties {

	
	private String formato =null;
	private List<String> colonneSelezionate= null;
	
	public String getFormato() {
		return this.formato;
	}
	public void setFormato(String formato) {
		this.formato = formato;
	}
	public List<String> getColonneSelezionate() {
		return this.colonneSelezionate;
	}
	public void setColonneSelezionate(List<String> colonneSelezionate) {
		this.colonneSelezionate = colonneSelezionate;
	}
	
	
}
