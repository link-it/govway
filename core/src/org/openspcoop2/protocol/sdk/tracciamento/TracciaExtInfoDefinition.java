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

package org.openspcoop2.protocol.sdk.tracciamento;

/**
 * Bean Contenente le informazioni relative alle sezioni contenenti informazioni extra
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TracciaExtInfoDefinition {

	private String prefixId;
	private String label;
	private boolean order;
	

	public boolean isOrder() {
		return this.order;
	}
	public void setOrder(boolean order) {
		this.order = order;
	}
	
	public String getPrefixId() {
		return this.prefixId;
	}
	public void setPrefixId(String prefixId) {
		this.prefixId = prefixId;
	}
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
}
