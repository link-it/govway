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
package org.openspcoop2.web.lib.mvc.properties.beans;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openspcoop2.core.mvc.properties.Conditions;
import org.openspcoop2.core.mvc.properties.Item;
import org.openspcoop2.core.mvc.properties.ItemValue;
import org.openspcoop2.core.mvc.properties.ItemValues;
import org.openspcoop2.core.mvc.properties.Property;
import org.openspcoop2.core.mvc.properties.constants.ItemType;
import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.properties.exception.UserInputValidationException;

/**
 * Bean di tipo Item arricchito delle informazioni grafiche.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ItemBean extends BaseItemBean<Item>{

	public ItemBean(Item item, String name, IProvider provider) {
		super(item, name, provider);
	}

	@Override
	public void init(String value) throws ProviderException {
//		Property saveProperty = this.getSaveProperty();

		// caso value == null e non devo forzare con il valore letto dal db cerco un default
		if(value == null) {
//			if(value == null && !saveProperty.isForce()) {
			switch(this.getItem().getType()) {
			case CHECKBOX:
				this.value = this.getItem().getDefaultSelected() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
				break;
			case HIDDEN:
				this.value = this.getItem().getValue();
				break;
			case NUMBER:
			case SELECT:
			case TEXT:
			default:
				if(StringUtils.isNotEmpty(this.getItem().getDefault())) {
					this.value = this.getItem().getDefault();
				}
				else if(this.provider!=null) {
					this.value = this.provider.getDefault(this.name);
				}
				else {
					this.value = null;
				}
				break;
			}
		} else {
			switch(this.getItem().getType()) {
			case CHECKBOX:
				this.value = this.getCheckBoxValue(value);
				break;
			case HIDDEN:
			case NUMBER:
			case SELECT:
			case TEXT:
			default:
				this.value = value;
				break;
			}
		}
	}

	@Override
	public DataElement toDataElement() throws ProviderException {
		DataElement de = new DataElement();
		de.setName(this.getName());
		de.setLabel(this.getItem().getLabel()); 
		de.setPostBack(this.getItem().getReloadOnChange());
		de.setRequired(this.getItem().isRequired()); 
		de.setNote(this.getItem().getNote());

		switch(this.getItem().getType()) {
		case CHECKBOX:
			de.setSelected(this.value);
			de.setType(DataElementType.CHECKBOX);
			break;
		case HIDDEN:
			de.setValue(this.value);
			de.setType(DataElementType.HIDDEN);
			break;
		case NUMBER:
			de.setValue(this.value);
			de.setType(DataElementType.NUMBER);
			de.setMinValue(this.getItem().getMin());
			de.setMaxValue(this.getItem().getMax()); 
			break;
		case SELECT:
			de.setSelected(this.value);
			de.setType(DataElementType.SELECT);

			List<String> valuesList = new ArrayList<String>();
			List<String> labelsList = new ArrayList<String>();
			ItemValues values = this.getItem().getValues();
			if(values!=null && values.sizeValueList()>0) {
				for (ItemValue itemValue : values.getValueList()) {
					valuesList.add(itemValue.getValue());
					labelsList.add(itemValue.getLabel() != null ? itemValue.getLabel() : itemValue.getValue());
				}
			}
			else if(this.provider!=null){
				List<String> tmp = this.provider.getValues(this.name);
				if(tmp!=null && tmp.size()>0) {
					valuesList.addAll(tmp);
				}
				tmp = this.provider.getLabels(this.name);
				if(tmp!=null && tmp.size()>0) {
					labelsList.addAll(tmp);
				}
			}
			de.setValues(valuesList);
			de.setLabels(labelsList);
			break;
		case TEXT:
			de.setValue(this.value);
			de.setType(DataElementType.TEXT_EDIT);
			break;
		default:
			break;
		}

		return de;
	}

	@Override
	public void setValueFromRequest(String parameterValue) throws ProviderException {
		if(parameterValue == null && !this.isOldVisible()) {
			switch(this.getItem().getType()) {
			case CHECKBOX:
				this.value = this.getItem().getDefaultSelected() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
				break;
			case HIDDEN:
				this.value = this.getItem().getValue();
				break;
			case NUMBER:
			case SELECT:
			case TEXT:
			default:
				if(StringUtils.isNotEmpty(this.getItem().getDefault())) {
					this.value = this.getItem().getDefault();
				}
				else if(this.provider!=null) {
					this.value = this.provider.getDefault(this.name);
				}
				else {
					this.value = null;
				}
				break;
			}
		}
		else {
			switch(this.getItem().getType()) {
			case HIDDEN:
				this.value = (parameterValue == null && this.getSaveProperty().isForce()) ? this.getItem().getValue() : parameterValue;
				break;
			case CHECKBOX:
			case NUMBER:
			case SELECT:
			case TEXT:
			default:
				this.value = parameterValue;
				break;
			}
		}
		//System.out.println("ITEM: ["+this.getName()+"] REQVALUE ["+parameterValue+"] NEW VALUE["+this.getValue()+"]");
	}

	@Override
	public Property getSaveProperty() {
		return this.getItem().getProperty();
	}

	@Override
	public String getPropertyValue() { 
		switch (this.getItem().getType()) {
		case CHECKBOX:
			String valueToCheck = null;
			if(ServletUtils.isCheckBoxEnabled(this.value)) {
				valueToCheck = this.getSaveProperty().getSelectedValue() != null ? this.getSaveProperty().getSelectedValue() : null;
			} else {
				valueToCheck = this.getSaveProperty().getUnselectedValue() != null ? this.getSaveProperty().getUnselectedValue() :null;
			}
			
			if(valueToCheck == null) {
				valueToCheck = ServletUtils.isCheckBoxEnabled(this.value) ? "true" : "false";
			}
			
			return valueToCheck;
		case HIDDEN:
			return this.getSaveProperty().isForce() ? this.getItem().getValue() : this.value;
		case NUMBER:
		case SELECT:
		case TEXT:
		default:
			return this.value;
		}
	}

	public String getCheckBoxValue(String value) {
		String valueToCheck = null;

		if(this.getItem().getProperty().getSelectedValue() != null) {
			if(value.equals(this.getSaveProperty().getSelectedValue()))
				valueToCheck = Costanti.CHECK_BOX_ENABLED;
		} 
		if(valueToCheck == null) {
			if(this.getItem().getProperty().getUnselectedValue() != null) {
				if(value.equals(this.getSaveProperty().getUnselectedValue()))
					valueToCheck = Costanti.CHECK_BOX_DISABLED;
			} 
		}

		if(valueToCheck == null){
			valueToCheck = ServletUtils.isCheckBoxEnabled(value) ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
		}

		if(valueToCheck == null){
			valueToCheck = this.getItem().getDefaultSelected() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
		}

		return valueToCheck;
	}

	@Override
	public Conditions getConditions() {
		return this.item.getConditions();
	}

	@Override
	public ItemType getItemType() {
		return this.item.getType();
	}

	@Override
	public String getLabel() {
		return this.item.getLabel();
	}
	
	@Override
	public void validate() throws UserInputValidationException {
		String itemValue = this.getPropertyValue(); // valore della property
		Property saveProperty = this.getSaveProperty();

//		System.out.println("VALIDATE -> Item: Name ["+this.getName()+"] Value ["+itemValue+"]...");  

		// un elemento e' salvabile se non e' visible o e' da forzare 
		boolean save = 
				(saveProperty != null) 
				&& 
				(saveProperty.isForce() 
						|| 
						(
							this.isVisible()
							// in teoria gli hidden visibili dovrebbe essere salvabili
//							&& 
//							!org.openspcoop2.core.mvc.properties.constants.ItemType.HIDDEN.equals(this.getItemType())
						)
				);
		

//		System.out.println("VALIDATE -> Item: Name ["+this.getName()+"] Value ["+itemValue+"] Validazione Abilitata ["+save+"]");  

		// validazione solo per gli elementi da salvare
		if(save) {

			// 1. Validazione campi obbligatori
			if(this.getItem().isRequired() && StringUtils.isEmpty(itemValue)) {
				throw new UserInputValidationException("Il Campo "+this.getLabel()+" &egrave; obbligatorio");
			}

			// 2. validazione generica basata sul tipo
			switch(this.getItem().getType()) {
			case NUMBER:
				if(StringUtils.isNotEmpty(itemValue)) {
					boolean numeric = NumberUtils.isParsable(itemValue);
					if(!numeric) {
						throw new UserInputValidationException("Il Campo "+this.getLabel()+" non contiene un valore di tipo numerico");
					}
					int number = -1;
					try {
						number = Integer.valueOf(itemValue);
					}catch(Exception e) {
						throw new UserInputValidationException("Il Campo "+this.getLabel()+" non contiene un valore di tipo numerico");
					}
					if(this.getItem().getMin()!=null) {
						if(number<this.getItem().getMin().intValue()) {
							throw new UserInputValidationException("Il Campo "+this.getLabel()+" deve contenere un valore >= "+this.getItem().getMin().intValue());
						}
					}
					if(this.getItem().getMax()!=null) {
						if(number>this.getItem().getMax().intValue()) {
							throw new UserInputValidationException("Il Campo "+this.getLabel()+" deve contenere un valore <= "+this.getItem().getMax().intValue());
						}
					}
				}
				break;
			case SELECT:
				if(StringUtils.isNotEmpty(itemValue)) {
					ItemValues values = this.getItem().getValues();
					boolean found = false;
					if(values!=null && values.sizeValueList()>0) {
						for (ItemValue selectItemValue : values.getValueList()) {
							if(selectItemValue.getValue().equals(itemValue)) {
								found = true;
								break;
							}
						}
					}
					else {
						try {
							List<String> tmp = this.provider.getValues(this.name);
							if(tmp.contains(itemValue)) {
								found = true;
							}
						}catch(Exception e) {
							throw new UserInputValidationException("Errore durante la validazione del Campo "+this.getLabel()+": "+e.getMessage(),e);
						}
					}

					if(!found)
						throw new UserInputValidationException("Il Campo "+this.getLabel()+" contiene un valore non previsto");
				}
				break;
			case TEXT:
			case CHECKBOX:
			case HIDDEN:
				break;
			}

			// 3. validazione basata sul pattern
			if(this.getItem().getValidation() != null && StringUtils.isNotEmpty(itemValue)) {
				try {
					boolean match = RegularExpressionEngine.isMatch(itemValue, this.getItem().getValidation());

					if(!match)
						throw new UserInputValidationException("Il Campo "+this.getLabel()+" non rispetta il pattern di validazione previsto ("+this.getItem().getValidation()+")");

				}catch(UserInputValidationException e) {
					throw e;
				}catch(Exception e) {
					throw new UserInputValidationException("Impossibile validare il campo "+this.getLabel()+" secondo il pattern previsto nella configurazione ("+this.getItem().getValidation()+")",e);
				}
			}
		}
	}
}
