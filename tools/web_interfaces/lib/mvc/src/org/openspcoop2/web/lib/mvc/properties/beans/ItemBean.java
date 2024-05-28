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
package org.openspcoop2.web.lib.mvc.properties.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openspcoop2.core.mvc.properties.Conditions;
import org.openspcoop2.core.mvc.properties.Item;
import org.openspcoop2.core.mvc.properties.ItemValue;
import org.openspcoop2.core.mvc.properties.ItemValues;
import org.openspcoop2.core.mvc.properties.Property;
import org.openspcoop2.core.mvc.properties.constants.ItemType;
import org.openspcoop2.core.mvc.properties.provider.ExternalResources;
import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderInfo;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementInfo;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.byok.LockUtilities;
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
	public void init(String value, ExternalResources externalResources) throws ProviderException {
		/**Property saveProperty = this.getSaveProperty();*/

		// caso value == null e non devo forzare con il valore letto dal db cerco un default
		if(value == null) {
			/**if(value == null && !saveProperty.isForce()) {*/
			switch(this.getItem().getType()) {
			case CHECKBOX:
				this.value = this.getItem().getDefaultSelected() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
				break;
			case HIDDEN:
			case LOCK_HIDDEN:
				this.value = this.getItem().getValue();
				break;
			case NUMBER:
			case SELECT:
			case TEXT:
			case TEXTAREA:
			case LOCK:
			default:
				if(StringUtils.isNotEmpty(this.getItem().getDefault())) {
					this.value = this.getItem().getDefault();
				}
				else if(this.provider!=null) {
					this.value = this.provider.getDefault(this.name, externalResources);
				}
				else {
					this.value = null;
				}
				/**System.out.println("init default ["+this.name+"] value '"+this.value+"'");*/
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
			case TEXTAREA:
			case LOCK:
			case LOCK_HIDDEN:
			default:
				this.value = value;
				/**System.out.println("init ["+this.name+"] value '"+this.value+"'");*/
				break;
			}
		}
	}

	@Override
	public DataElement toDataElement(ConfigBean config, Map<String, String> mapNameValue, ExternalResources externalResources,
			LockUtilities lockUtilities) throws ProviderException {

		if(this.provider!=null){
			this.value = this.provider.dynamicUpdate(config.getListaItemSDK(), mapNameValue, this.getItem(), this.value, externalResources);
		}
		
		mapNameValue.put(this.name, this.value);
		
		DataElement de = new DataElement();
		de.setName(this.getName());
		de.setLabel(this.getItem().getLabel()); 
		/**de.setPostBack(this.getItem().getReloadOnChange());*/
		de.setPostBack_viaPOST(true); // per la cifratura
		de.setRequired(this.getItem().isRequired()); 
		
		if(this.getItem().getNote()!=null && StringUtils.isNotEmpty(this.getItem().getNote())) {
			de.setNote(this.getItem().getNote());
		}
		else if(this.provider!=null){
			de.setNote(this.provider.getNote(this.name, this.value));
		}

		if(this.provider!=null){
			addProviderInfo(de);
		}
		
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
			processSelectElement(de, externalResources);
			break;
		case TEXT:
			de.setValue(this.value);
			de.setType(DataElementType.TEXT_EDIT);
			break;
		case TEXTAREA:
			de.setValue(this.value);
			de.setType(DataElementType.TEXT_AREA);
			if(this.getItem().getMax()!=null && this.getItem().getMax().intValue()>0) {
				de.setRows(this.getItem().getMax().intValue());
			}
			else {
				de.setRows(3);
			}
			break;
		case LOCK_HIDDEN:
			/**System.out.println("DATAELEMENT LOCK_HIDDEN ["+this.name+"] value '"+this.value+"'");*/
			try {
				lockUtilities.lockHidden(de, this.value);
			}catch(Exception e) {
				throw new ProviderException(e.getMessage(),e);
			}
			break;
		case LOCK:
			/**System.out.println("DATAELEMENT LOCK ["+this.name+"] value '"+this.value+"'");*/
			try {
				lockUtilities.lock(de, this.value);
			}catch(Exception e) {
				throw new ProviderException(e.getMessage(),e);
			}
			break;
		default:
			break;
		}
		
		return de;
	}
	private void addProviderInfo(DataElement de) throws ProviderException {
		ProviderInfo pInfo = this.provider.getProviderInfo(this.name); 
		if(pInfo!=null) {
			DataElementInfo dInfo = new DataElementInfo(pInfo.getHeaderFinestraModale()!=null ? pInfo.getHeaderFinestraModale() : this.getItem().getLabel());
			dInfo.setBody(pInfo.getBody());
			dInfo.setHeaderBody(pInfo.getHeaderBody());
			dInfo.setListBody(pInfo.getListBody());
			de.setInfo(dInfo);
		}
	}
	private void processSelectElement(DataElement de, ExternalResources externalResources) throws ProviderException {
		de.setSelected(this.value);
		de.setType(DataElementType.SELECT);

		List<String> valuesList = new ArrayList<>();
		List<String> labelsList = new ArrayList<>();
		ItemValues values = this.getItem().getValues();
		if(values!=null && values.sizeValueList()>0) {
			for (ItemValue itemValue : values.getValueList()) {
				valuesList.add(itemValue.getValue());
				labelsList.add(itemValue.getLabel() != null ? itemValue.getLabel() : itemValue.getValue());
			}
		}
		else if(this.provider!=null){
			List<String> tmp = this.provider.getValues(this.name, externalResources);
			if(tmp!=null && !tmp.isEmpty()) {
				valuesList.addAll(tmp);
			}
			tmp = this.provider.getLabels(this.name, externalResources);
			if(tmp!=null && !tmp.isEmpty()) {
				labelsList.addAll(tmp);
			}
		}
		de.setValues(valuesList);
		de.setLabels(labelsList);
	}

	@Override
	public void setValueFromRequest(String parameterValue, ExternalResources externalResources, LockUtilities lockUtilities) throws ProviderException {
		if(parameterValue == null && !this.isOldVisible()) {
			setDefaultValueFromRequest(externalResources);
		}
		else {
			switch(this.getItem().getType()) {
			case HIDDEN:
			case LOCK_HIDDEN:
				this.value = (parameterValue == null && this.getSaveProperty().isForce()) ? this.getItem().getValue() : parameterValue;
				break;
			case CHECKBOX:
			case NUMBER:
			case SELECT:
			case TEXT:
			case TEXTAREA:
			case LOCK:
			default:
				/**System.out.println("setValueFromRequest ["+this.name+"] value '"+parameterValue+"'");*/
				this.value = parameterValue;
				break;
			}
		}
		
		if(ItemType.LOCK.equals(this.getItem().getType()) ||
				ItemType.LOCK_HIDDEN.equals(this.getItem().getType())) {
			try {
				this.value = lockUtilities.getDriverBYOKUtilities().wrap(this.value);
			}catch(Exception e) {
				throw new ProviderException(e.getMessage(),e);
			}
		}
		
		/**System.out.println("ITEM: ["+this.getName()+"] REQVALUE ["+parameterValue+"] NEW VALUE["+this.getValue()+"]");*/
	}
	private void setDefaultValueFromRequest(ExternalResources externalResources) throws ProviderException {
		switch(this.getItem().getType()) {
		case CHECKBOX:
			this.value = this.getItem().getDefaultSelected() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
			break;
		case HIDDEN:
		case LOCK_HIDDEN:
			this.value = this.getItem().getValue();
			break;
		case NUMBER:
		case SELECT:
		case TEXT:
		case TEXTAREA:
		case LOCK:
		default:
			if(StringUtils.isNotEmpty(this.getItem().getDefault())) {
				this.value = this.getItem().getDefault();
			}
			else if(this.provider!=null) {
				this.value = this.provider.getDefault(this.name, externalResources);
			}
			else {
				this.value = null;
			}
			/**System.out.println("setDefaultValueFromRequest ["+this.name+"] value '"+this.value+"'");*/
			break;
		}
	}

	@Override
	public Property getSaveProperty() {
		return this.getItem().getProperty();
	}

	@Override
	public String getPropertyValue() { 
		switch (this.getItem().getType()) {
		case CHECKBOX:
			return getCheckboxPropertyValue();
		case HIDDEN:
		case LOCK_HIDDEN:
			return this.getSaveProperty().isForce() ? this.getItem().getValue() : this.value;
		case NUMBER:
		case SELECT:
		case TEXT:
		case TEXTAREA:
		case LOCK:
		default:
			/**if(ItemType.LOCK.equals(this.getItem().getType())) {
				System.out.println("getPropertyValue ["+this.name+"] value '"+this.value+"'");
			}*/
			return this.value;
		}
	}
	private String getCheckboxPropertyValue() { 
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
	}

	public String getCheckBoxValue(String value) {
		String valueToCheck = null;

		if(this.getItem().getProperty().getSelectedValue() != null &&
			value.equals(this.getSaveProperty().getSelectedValue())) {
			valueToCheck = Costanti.CHECK_BOX_ENABLED;
		} 
		if(valueToCheck == null &&
			this.getItem().getProperty().getUnselectedValue() != null &&
			value.equals(this.getSaveProperty().getUnselectedValue())) {
			valueToCheck = Costanti.CHECK_BOX_DISABLED;
		}

		if(valueToCheck == null){
			valueToCheck = ServletUtils.isCheckBoxEnabled(value) ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
		}

/**		if(valueToCheck == null){
//			valueToCheck = this.getItem().getDefaultSelected() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
//		}*/

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
	public void validate(ExternalResources externalResources) throws UserInputValidationException {
		String itemValue = this.getPropertyValue(); // valore della property
		Property saveProperty = this.getSaveProperty();

		/**System.out.println("VALIDATE -> Item: Name ["+this.getName()+"] Value ["+itemValue+"]...");*/  

		// un elemento e' salvabile se non e' visible o e' da forzare 
		boolean save = 
				(saveProperty != null) 
				&& 
				(saveProperty.isForce() 
						|| 
						(
							this.isVisible()
							// in teoria gli hidden visibili dovrebbe essere salvabili
							/**&& 
							!org.openspcoop2.core.mvc.properties.constants.ItemType.HIDDEN.equals(this.getItemType())*/
						)
				);
		

		/**System.out.println("VALIDATE -> Item: Name ["+this.getName()+"] Value ["+itemValue+"] Validazione Abilitata ["+save+"]");*/  

		// validazione solo per gli elementi da salvare
		if(save) {

			String prefixIlCampo = "Il Campo "+this.getLabel();
			
			// 1. Validazione campi obbligatori
			if(this.getItem().isRequired() && StringUtils.isEmpty(itemValue)) {
				throw new UserInputValidationException(prefixIlCampo+" &egrave; obbligatorio");
			}

			// 2. validazione generica basata sul tipo
			switch(this.getItem().getType()) {
			case NUMBER:
				if(StringUtils.isNotEmpty(itemValue)) {
					boolean numeric = NumberUtils.isParsable(itemValue);
					if(!numeric) {
						throw new UserInputValidationException(prefixIlCampo+" non contiene un valore di tipo numerico");
					}
					int number = -1;
					try {
						number = Integer.valueOf(itemValue);
					}catch(Exception e) {
						throw new UserInputValidationException(prefixIlCampo+" non contiene un valore di tipo numerico");
					}
					if(this.getItem().getMin()!=null &&
						number<this.getItem().getMin().intValue()) {
						throw new UserInputValidationException(prefixIlCampo+" deve contenere un valore >= "+this.getItem().getMin().intValue());
					}
					if(this.getItem().getMax()!=null &&
						number>this.getItem().getMax().intValue()) {
						throw new UserInputValidationException(prefixIlCampo+" deve contenere un valore <= "+this.getItem().getMax().intValue());
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
							List<String> tmp = this.provider.getValues(this.name, externalResources);
							if(tmp.contains(itemValue)) {
								found = true;
							}
						}catch(Exception e) {
							throw new UserInputValidationException("Errore durante la validazione del Campo "+this.getLabel()+": "+e.getMessage(),e);
						}
					}

					if(!found)
						throw new UserInputValidationException(prefixIlCampo+" contiene un valore non previsto");
				}
				break;
			case TEXT:
			case TEXTAREA:
				if(itemValue!=null && itemValue.length()>4000) {
					throw new UserInputValidationException(prefixIlCampo+" non deve contenere più di 4000 caratteri");
				}
				if(itemValue!=null && (itemValue.startsWith(" ") || itemValue.startsWith("\t"))) {
					throw new UserInputValidationException("Il valore inserito nel Campo "+this.getLabel()+" non può iniziare con uno spazio");
				}
				if(itemValue!=null && (itemValue.endsWith(" ") || itemValue.endsWith("\t"))) {
					throw new UserInputValidationException("Il valore inserito nel Campo "+this.getLabel()+" non può terminare con uno spazio");
				}
				break;
			case LOCK:
			case LOCK_HIDDEN:
			case CHECKBOX:
			case HIDDEN:
				break;
			}

			// 3. validazione basata sul pattern
			if(this.getItem().getValidation() != null && StringUtils.isNotEmpty(itemValue)) {
				try {
					boolean match = RegularExpressionEngine.isMatch(itemValue, this.getItem().getValidation());

					if(!match)
						throw new UserInputValidationException(prefixIlCampo+" non rispetta il pattern di validazione previsto ("+this.getItem().getValidation()+")");

				}catch(UserInputValidationException e) {
					throw e;
				}catch(Exception e) {
					throw new UserInputValidationException("Impossibile validare il campo "+this.getLabel()+" secondo il pattern previsto nella configurazione ("+this.getItem().getValidation()+")",e);
				}
			}
		}
	}
}
