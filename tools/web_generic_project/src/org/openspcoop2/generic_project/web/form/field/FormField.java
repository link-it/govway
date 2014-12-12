/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved.
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
package org.openspcoop2.generic_project.web.form.field;

import java.lang.reflect.Method;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.openspcoop2.generic_project.web.core.Utils;
import org.openspcoop2.generic_project.web.form.BaseForm;
import org.openspcoop2.generic_project.web.form.CostantiForm;


/**
 * FormField Tipo generico che definisce i metadati di un field di una form di ricerca.
 * name = nome del Field dell'oggetto search;
 * type = tipo di input da visualizzare (text,date,ecc..);
 * value = valore del Field dell'oggetto search;
 * value2 = valore2 del Field dell'oggetto search (utilizzato se si fa ricerca di intervalli;
 * interval = true se il field e' una ricerca tra intervalli, false se e' singolo.
 * label = label del field.
 * autocomplete = true il componente nella pagina avra' il supporto autocomplete, false altrimenti.
 * search = il form all'interno del quale si trova il field.
 * fieldsToUpdate = elenco degli id della pagina che devono ricaricati dopo una modifica del valore del field.
 * elencoSelectItems = elenco degli item, nel caso si tratti di una select choice (select list, check box, dropdownlist, ecc...).
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class FormField<T> {

	private String name;

	private String label;

	private String label2;

	private T value;

	private T value2;

	private T defaultValue;

	private T defaultValue2;

	private String type;

	private FieldType fieldType;

	private boolean interval;

	private boolean autoComplete ;

	private BaseForm form;

	private String fieldsToUpdate;

	private List<SelectItem> elencoSelectItems;

	private List<org.openspcoop2.generic_project.web.form.field.SelectItem> elencoCustomTypeSelectItems;

	private boolean rendered ;

	private boolean required ;

	private String requiredMessage;

	private String note;

	private boolean enableManualInput;

	private boolean disabled;

	private boolean confirm ;

	private boolean redisplay;
	
	private String style;
	
	private int width;	

	public boolean isAutoComplete() {
		return this.autoComplete;
	}

	public void setAutoComplete(boolean autoComplete) {
		this.autoComplete = autoComplete;
	}

	public BaseForm getForm() {
		return this.form;
	}

	public void setForm(BaseForm search) {
		this.form = search;
	}

	public String getFieldsToUpdate() {
		return this.fieldsToUpdate;
	}

	public void setFieldsToUpdate(String fieldsToUpdate) {
		this.fieldsToUpdate = fieldsToUpdate;
	}

	public List<SelectItem> getElencoSelectItems() {
		return this.elencoSelectItems;
	}

	public void setElencoSelectItems(List<SelectItem> elencoSelectItems) {
		this.elencoSelectItems = elencoSelectItems;
	}

	public FormField(){
		this.autoComplete = false;
		this.interval = false;
		this.rendered = true;
		this.disabled = false;
		this.enableManualInput =false;
		this.required = false;
		this.requiredMessage = Utils.getMessageFromCommonsResourceBundle("inputField.requiredMessageDefault");
		this.style ="width:412px;";
		this.width = 412;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public T getValue() {
		return this.value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
		this.fieldType = FieldType.toEnumConstant(type);
	}

	public FieldType getFieldType() {
		return this.fieldType;
	}

	public void setType(FieldType type) {
		this.fieldType = type;
		this.type = this.fieldType.getValue();
	}

	public boolean isInterval() {
		return this.interval;
	}

	public void setInterval(boolean interval) {
		this.interval = interval;
	}

	public String getLabel() {
		try{
			String tmp = Utils.getMessageFromResourceBundle(this.label);

			if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
				return tmp;
		}catch(Exception e){}

		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel2() {
		try{
			String tmp = Utils.getMessageFromResourceBundle(this.label2);

			if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
				return tmp;
		}catch(Exception e){}
		
		return this.label2;
	}

	public void setLabel2(String label) {
		this.label2 = label;
	}

	public T getValue2() {
		return this.value2;
	}

	public void setValue2(T value2) {
		this.value2 = value2;
	}

	public void reset(){
		this.value = this.defaultValue;
		this.value2 = this.defaultValue2;
	}

	public <C> C getValue(Class<C> clazz) {
		return clazz.cast(this.value);
	}

	public <C> C getValue2(Class<C> clazz) {
		return clazz.cast(this.value2);
	}

	public List<?> fieldAutoComplete(Object val){
		try{
			String methodName = this.getName() + CostantiForm.AUTO_COMPLETE_EVENT_HANDLER;
			Method method =  this.form.getClass().getMethod(methodName , Object.class);
			Object ret = method.invoke(this.form,val);

			if(ret != null && ret instanceof List<?>)
				return (List<?>) ret;
		}catch(Exception e){

		}
		return null;
	}

	// Event Handler per la selezione 
	public void fieldSelected(ActionEvent ae) {
		try{
			String methodName = this.getName() + CostantiForm.SELECTED_ELEMENT_EVENT_HANDLER;
			Method method =  this.form.getClass().getMethod(methodName ,ae.getClass());
			method.invoke(this.form,ae);
		}catch(Exception e){

		}
	}

	// Event Handler per l'evento OnChange 
	public void valueChanged(ActionEvent ae) {
		try{
			String methodName = this.getName() + CostantiForm.ONCHANGE_EVENT_HANDLER;
			Method method =  this.form.getClass().getMethod(methodName ,ae.getClass());
			method.invoke(this.form,ae);
		}catch(Exception e){

		}
	}

	public boolean isRendered() {
		return this.rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	public T getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public T getDefaultValue2() {
		return this.defaultValue2;
	}

	public void setDefaultValue2(T defaultValue2) {
		this.defaultValue2 = defaultValue2;
	}

	public boolean isRequired() {
		return this.required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getRequiredMessage() {
		return this.requiredMessage;
	}

	public void setRequiredMessage(String requiredMessage) {
		this.requiredMessage = requiredMessage;
	}

	public boolean isEnableManualInput() {
		return this.enableManualInput;
	}

	public void setEnableManualInput(boolean enableManualInput) {
		this.enableManualInput = enableManualInput;
	}

	public boolean isDisabled() {
		return this.disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isConfirm() {
		return this.confirm;
	}

	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}

	public List<org.openspcoop2.generic_project.web.form.field.SelectItem> getElencoCustomTypeSelectItems() {
		return this. elencoCustomTypeSelectItems;
	}

	public void setElencoCustomTypeSelectItems(
			List<org.openspcoop2.generic_project.web.form.field.SelectItem> elencoCustomTypeSelectItems) {
		this.elencoCustomTypeSelectItems = elencoCustomTypeSelectItems;
	}

	public boolean isRedisplay() {
		return this.redisplay;
	}

	public void setRedisplay(boolean redisplay) {
		this.redisplay = redisplay;
	}

	public String getStyle() {
		return this.style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}
