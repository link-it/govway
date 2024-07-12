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
package org.openspcoop2.protocol.sdk.properties;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.UtilsRuntimeException;

/**
 * AbstractConsoleItem
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractConsoleItem<T> extends BaseConsoleItem {

	private T defaultValue;
	private T defaultValueForCloseableSection; // il default value viene modificato, questo rimane impostato al valore iniziale
	private boolean reloadOnChange;
	private boolean reloadOnHttpPost;
	private boolean required;
	private String regexpr;
	private SortedMap<T> mapLabelValues;
	/**private TreeMap<String,T> mapLabelValues;*/
	private String note;
	private ConsoleItemInfo info;
	private String labelRight;

	protected AbstractConsoleItem(String id, String label, ConsoleItemType type) throws ProtocolException{
		super(id, label, type);
		/**this.mapLabelValues = new TreeMap<String,T>();*/
		this.mapLabelValues = new SortedMap<>();
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
	public boolean isReloadOnHttpPost() {
		return this.reloadOnHttpPost;
	}
	public void setReloadOnChange(boolean reloadOnChange) {
		this.setReloadOnChange(reloadOnChange, false);
	}
	public void setReloadOnChange(boolean reloadOnChange, boolean reloadOnHttpPost) {
		this.reloadOnChange = reloadOnChange;
		this.reloadOnHttpPost = reloadOnHttpPost;
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
	
	public SortedMap<T> getMapLabelValues() {
		return this.mapLabelValues;
	}
	public List<String> getLabels(){
		List<String> labels = null;
		if(this.mapLabelValues!=null && this.mapLabelValues.size()>0){	
			labels = new ArrayList<>();
			labels.addAll(this.mapLabelValues.keys());
			return labels;
		}
		return labels;
	}
	public List<T> getValues(){
		List<T> values = null;
		if(this.mapLabelValues!=null && this.mapLabelValues.size()>0){	
			values = new ArrayList<>();
			values.addAll(this.mapLabelValues.values());
			return values;
		}
		return values;
	}
	public void clearMapLabelValues(){
		this.mapLabelValues.clear();
	}
	public void addLabelValue(String key, T value) {
		try {
			if(this.mapLabelValues.containsKey(key)) {
				this.mapLabelValues.remove(key);
			}
			this.mapLabelValues.put(key, value);
		}catch(Exception e) {
			throw new UtilsRuntimeException(e.getMessage(),e);
		}
	}
	
	public void removeLabelValue(String label){
		this.mapLabelValues.remove(label);
	}
	
	public String getNote() {
		return this.note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	public ConsoleItemInfo getInfo() {
		return this.info;
	}
	public void setInfo(ConsoleItemInfo info) {
		this.info = info;
	}

	public String getLabelRight() {
		return this.labelRight;
	}
	public void setLabelRight(String labelRight) {
		this.labelRight = labelRight;
	}
	
	public void setUseDefaultValueForCloseableSection(boolean useDefaultValueForCloseableSection) throws ProtocolException {
		if(this.defaultValue==null) {
			throw new ProtocolException("Default value undefined (useDefaultValue:"+useDefaultValueForCloseableSection+")");
		}
		this.defaultValueForCloseableSection = cloneValue(this.defaultValue);
	}
	
	protected abstract T cloneValue(T value) throws ProtocolException;
	
	public T getDefaultValueForCloseableSection() {
		return this.defaultValueForCloseableSection;
	}
}
