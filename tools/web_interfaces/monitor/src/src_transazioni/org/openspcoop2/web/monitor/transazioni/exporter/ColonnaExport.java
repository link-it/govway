/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import java.io.Serializable;

import org.openspcoop2.web.monitor.core.bean.SelectItem;

/**
 * ColonnaExport
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ColonnaExport implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean visibile = false;
	private String label = null;
	private String key= null;
	
	public ColonnaExport(){}
	
	public ColonnaExport(String key, String label, boolean visibile){
		this.key = key;
		this.label = label;
		this.visibile = visibile;
	}
	
	public boolean isVisibile() {
		return this.visibile;
	}
	public void setVisibile(boolean visibile) {
		this.visibile = visibile;
	}
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public SelectItem getSelectItem(){
		return new SelectItem(this.getKey(), this.getLabel());
	}
	
}
