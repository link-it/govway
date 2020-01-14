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
package org.openspcoop2.web.monitor.core.bean;

import java.io.Serializable;

/****
 * MenuModalitaItem
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class MenuModalitaItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String value = null;
	private String label = null;
	private String icon = null;
	private Integer labelWidth = null;
	private boolean disabled = false;
	private String tooltip = null;
	private String id = "voce_1";
	
	public MenuModalitaItem(String value, String label, String icon) {
		this.setIcon(icon);
		this.setLabel(label);
		this.setValue(value);
	}

	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getIcon() {
		return this.icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public Integer getLabelWidth() {
		return this.labelWidth;
	}
	public void setLabelWidth(Integer labelWidth) {
		this.labelWidth = labelWidth;
	}
	public boolean isDisabled() {
		return this.disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public String getTooltip() {
		return this.tooltip;
	}
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
}

