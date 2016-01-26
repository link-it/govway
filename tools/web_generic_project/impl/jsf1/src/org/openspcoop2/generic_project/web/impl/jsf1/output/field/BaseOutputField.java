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
package org.openspcoop2.generic_project.web.impl.jsf1.output.field;

import org.openspcoop2.generic_project.web.impl.jsf1.utils.Utils;
import org.openspcoop2.generic_project.web.output.OutputField;
import org.openspcoop2.generic_project.web.output.OutputType;

/**
 * Implementazione di un elemento di output generico.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public abstract class BaseOutputField<T> implements OutputField<T> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String name;
	
	protected String id;

	protected String label;

	protected T value;

	protected T defaultValue;

	protected OutputType type;

	protected boolean rendered;

	protected boolean insideGroup;

	protected boolean secret ;

	protected boolean escape = true;

	protected String pattern;

	protected String labelStyleClass = null;

	protected String valueStyleClass = null;

	protected String columnClasses = null;

	protected String styleClass = null;

	protected String tableColumnStyleClass = null;

	public BaseOutputField(){
		this.labelStyleClass ="outputFieldLabel";
		this.rendered = true;
		this.columnClasses = ""; //"gridContent verticalAlignTop";
		this.styleClass = "";
		this.tableColumnStyleClass = "";
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String getLabel() {
		try{
			String tmp = Utils.getInstance().getMessageFromResourceBundle(this.label);

			if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
				return tmp;
		}catch(Exception e){}

		return this.label;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getValue() {
		if(this.value != null){
			if(this.value instanceof String){
				try{
					String tmp = Utils.getInstance().getMessageFromResourceBundle((String) this.value);

					if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
						return (T) tmp;
				}catch(Exception e){}
			}
		}
		return this.value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getDefaultValue() {
		if(this.defaultValue != null){
			if(this.defaultValue instanceof String){
				try{
					String tmp = Utils.getInstance().getMessageFromResourceBundle((String) this.defaultValue);

					if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
						return (T) tmp;
				}catch(Exception e){}
			}
		}
		return this.defaultValue;
	}


	@Override
	public void setLabel(String label) {
		this.label = label;
	}


	@Override
	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public boolean isInsideGroup() {
		return this.insideGroup;
	}

	@Override
	public void setInsideGroup(boolean insideGroup) {
		this.insideGroup = insideGroup;
	}

	@Override
	public boolean isRendered() {
		return this.rendered;
	}

	@Override
	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	@Override
	public boolean isSecret() {
		return this.secret;
	}

	@Override
	public void setSecret(boolean secret) {
		this.secret = secret;
	}

	@Override
	public String getPattern() {
		return this.pattern;
	}

	@Override
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean isEscape() {
		return this.escape;
	}

	@Override
	public void setEscape(boolean escape) {
		this.escape = escape;
	}

	@Override
	public String getLabelStyleClass() {
		return this.labelStyleClass;
	}

	@Override
	public void setLabelStyleClass(String labelStyleClass) {
		this.labelStyleClass = labelStyleClass;
	}

	@Override
	public String getValueStyleClass() {
		return this.valueStyleClass;
	}

	@Override
	public void setValueStyleClass(String valueStyleClass) {
		this.valueStyleClass = valueStyleClass;
	}

	@Override
	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String getColumnClasses() {
		return this.columnClasses;
	}

	@Override
	public void setColumnClasses(String columnClasses) {
		this.columnClasses = columnClasses;
	}

	@Override
	public String getStyleClass() {
		return this.styleClass;
	}

	@Override
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	@Override
	public String getTableColumnStyleClass() {
		return this.tableColumnStyleClass;
	}

	@Override
	public void setTableColumnStyleClass(String tableColumnStyleClass) {
		this.tableColumnStyleClass = tableColumnStyleClass;
	}

	@Override
	public OutputType getType() {
		return this.type;
	}

	@Override
	public void setType(OutputType type) {
		this.type = type;
	}
	
	@Override
	public String get_value_type() {
		if(this.type == null){
	    	return null;
	    }else{
	    	return this.type.toString();
	    }
	}

	@Override
	public void set_value_type(String _value_type) {
		this.type = (OutputType) OutputType.toEnumConstantFromString(_value_type);
	}
	
	@Override
	public String getId() {
		return this.id;
	}
	
	@Override
	public void setId(String id) {
		this.id  =id;
	}
}
