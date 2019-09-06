/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.protocol.sdk.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;

/**
 * AbstractConsoleItem
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractConsoleItem<T> extends BaseConsoleItem {

	private T defaultValue;
	private boolean reloadOnChange;
	private boolean required;
	private String regexpr;
	private TreeMap<String,T> mapLabelValues;
	private String note;

	protected AbstractConsoleItem(String id, String label, ConsoleItemType type) throws ProtocolException{
		super(id, label, type);
		this.mapLabelValues = new TreeMap<String,T>();
	}

	public T getDefaultValue() {
		return this.defaultValue;
	}
	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isReloadOnChange() {
		return this.reloadOnChange;
	}
	public void setReloadOnChange(boolean reloadOnChange) {
		this.reloadOnChange = reloadOnChange;
	}

	public boolean isRequired() {
		return this.required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getRegexpr() {
		return this.regexpr;
	}
	public void setRegexpr(String regexpr) {
		this.regexpr = regexpr;
	}
	
	public TreeMap<String, T> getMapLabelValues() {
		return this.mapLabelValues;
	}
	public List<String> getLabels(){
		if(this.mapLabelValues!=null && this.mapLabelValues.size()>0){	
			List<String> labeles = new ArrayList<String>();
			labeles.addAll(this.mapLabelValues.keySet());
			return labeles;
		}
		return null;
	}
	public List<T> getValues(){
		if(this.mapLabelValues!=null && this.mapLabelValues.size()>0){	
			List<T> values = new ArrayList<T>();
			values.addAll(this.mapLabelValues.values());
			return values;
		}
		return null;
	}
	public void clearMapLabelValues(){
		this.mapLabelValues.clear();
	}
	public void addLabelValue(String key, T value){
		this.mapLabelValues.put(key, value);
	}
	
	public String getNote() {
		return this.note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
}
