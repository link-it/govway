/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.web.lib.mvc.properties.utils;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.mvc.properties.Collection;
import org.openspcoop2.core.mvc.properties.Condition;
import org.openspcoop2.core.mvc.properties.Conditions;
import org.openspcoop2.core.mvc.properties.Config;
import org.openspcoop2.core.mvc.properties.Defined;
import org.openspcoop2.core.mvc.properties.Equals;
import org.openspcoop2.core.mvc.properties.Item;
import org.openspcoop2.core.mvc.properties.Property;
import org.openspcoop2.core.mvc.properties.Section;
import org.openspcoop2.core.mvc.properties.Selected;
import org.openspcoop2.core.mvc.properties.Subsection;
import org.openspcoop2.web.lib.mvc.properties.beans.BaseItemBean;
import org.openspcoop2.web.lib.mvc.properties.beans.ConfigBean;
import org.openspcoop2.web.lib.mvc.properties.beans.ItemBean;
import org.openspcoop2.web.lib.mvc.properties.beans.SectionBean;
import org.openspcoop2.web.lib.mvc.properties.beans.SubsectionBean;
import org.openspcoop2.web.lib.mvc.properties.exception.ValidationException;
import org.openspcoop2.core.mvc.properties.constants.ItemType;
import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;

/****
 * 
 * ValidationEngine validatore delle configurazioni
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ValidationEngine {
	
	

	public static boolean validateConfig(Config config)  throws ValidationException{
		
		// controllo l'univocita' delle chiavi e le conservo per riutilizzarle per verificare 
		//che gli elementi riferiti nelle conditions esistano.
		
		ConfigBean metadata = getMetadata(config);
		
		List<Section> sectionList = config.getSectionList();
		try {
			for (int i= 0; i < sectionList.size() ; i++) {
				Section section = sectionList.get(i);
				validateSection(section,metadata);
			}
		
			// controllo che tutti gli item che vanno a finire dentro una unica property abbiano lo stesso separatore
			for (String nomeProperty : metadata.getMapPropertyItem().keySet()) {
				String separatore = null;
				List<BaseItemBean<?>> list = metadata.getMapPropertyItem().get(nomeProperty);
				if(list.size() > 1) {
					for (BaseItemBean<?> itemBean : list) {
						if(itemBean.getSaveProperty()!= null && itemBean.getSaveProperty().isAppend()) {
							if(separatore == null) {
								separatore = itemBean.getSaveProperty().getAppendSeparator();
							} else {
								if(!separatore.equals(itemBean.getSaveProperty().getAppendSeparator()))
									throw new ValidationException("I separatori di append per la property ["+nomeProperty+"] devono essere tutti uguali.");
							}
						}
					}
				}
			}
		
		}catch(ValidationException e) {
			throw new ValidationException("Errore durante la validazione della configurazione ["+config.getId()+"]: "+ e.getMessage(),e);
		}
		
		return true;
	}

	private static void validateSection(Section section,ConfigBean metadata) throws ValidationException{
		try {
			validaConditions(section.getConditions(),metadata);
	
			if(section.getItemList() != null) {
				for (Item item : section.getItemList()) {
					validaItem(item,metadata);
				}
			}
			
			if(section.getSubsectionList() != null) {
				for (int i= 0; i < section.getSubsectionList().size() ; i++) {
					Subsection subSection  = section.getSubsectionList().get(i);
					validaSubsection(subSection,metadata);
				}
			}
		}catch(ValidationException e) {
			throw new ValidationException("Section ["+section.getLabel()+"] -> "+ e.getMessage());
		}
	}

	private static void validaSubsection(Subsection subSection,ConfigBean metadata) throws ValidationException {
		try {
			validaConditions(subSection.getConditions(),metadata);
	
			if(subSection.getItemList() != null) {
				for (Item item : subSection.getItemList()) {
					validaItem(item,metadata);
				}
			}
		}catch(ValidationException e) {
			throw new ValidationException("Subsection ["+subSection.getLabel()+"] -> "+ e.getMessage());
		}
	}

	private static void validaItem(Item item,ConfigBean metadata) throws ValidationException {
		try {
			validaConditions(item.getConditions(),metadata);
			
			if(item.getProperty().getProperties() != null) {
				if(!metadata.getListaNomiProperties().contains(item.getProperty().getProperties()))
					throw new ValidationException("Il nome delle properties ["+item.getProperty().getProperties()+"] indicato nella collection non e' dichiarato nella sezione collection della configurazione");
			}
			
			// il force puo' essere utilizzato solo da elementi hidden
			if(item.getProperty().isForce() && !item.getType().equals(ItemType.HIDDEN)) {
				throw new ValidationException("L'attributo Force puo' essere utilizzato solo per gli items di tipo Hidden");
			}
			
			switch(item.getType()){
			case CHECKBOX:
				validaCheckBox(item);
				break;
			case HIDDEN:
				validaHidden(item);
				break;
			case NUMBER:
				validaNumber(item,metadata);
				break;
			case SELECT:
				validaSelect(item,metadata);
				break;
			case TEXT:
				validaText(item,metadata);
				break;
			}
		}catch(Exception e) {
			throw new ValidationException("Item ["+item.getName()+"] non valido: "+ e.getMessage());
		}
	}

	private static void validaCheckBox(Item item) throws ValidationException{
		Property property = item.getProperty();
		// se e' una property di tipo append valido il separatore
		if(property.isAppend()) {
			if(property.getSelectedValue().contains(property.getAppendSeparator()))
				throw new ValidationException("Il valore indicato per l'attributo SelectedValue ["+property.getSelectedValue()+"] contiene il separatore previsto per il salvataggio ["+property.getAppendSeparator()+"]");
			
			if(property.getUnselectedValue().contains(property.getAppendSeparator()))
				throw new ValidationException("Il valore indicato per l'attributo UnselectedValue ["+property.getUnselectedValue()+"] contiene il separatore previsto per il salvataggio ["+property.getAppendSeparator()+"]");
		}
		
	}

	private static void validaHidden(Item item) throws ValidationException{
		Property property = item.getProperty();
		
		if(item.getValue() == null) {
			throw new ValidationException("L'attributo Value e' obbligatorio per gli elementi di tipo Hidden");
		}
		
		// se e' una property di tipo append valido il separatore
		if(property.isAppend()) {
			if(item.getValue().contains(property.getAppendSeparator()))
				throw new ValidationException("Il valore indicato per l'attributo Value ["+item.getValue()+"] contiene il separatore previsto per il salvataggio ["+property.getAppendSeparator()+"]");
		}
		
		
	}

	private static String getDefault(Item item,ConfigBean metadata) throws ProviderException {
		if(StringUtils.isNotEmpty(item.getDefault())) {
			return item.getDefault();
		}
		else if(metadata.getProvider()!=null) {
			return metadata.getProvider().getDefault(item.getName());
		}
		return null;
	}
	
	private static void validaNumber(Item item,ConfigBean metadata) throws ValidationException, ProviderException{
		Property property = item.getProperty();
		// se e' una property di tipo append valido il separatore
		if(property.isAppend()) {
			String defaultValue = getDefault(item, metadata);
			if(defaultValue != null && defaultValue.contains(property.getAppendSeparator()))
				throw new ValidationException("Il valore indicato per l'attributo Default ["+item.getValue()+"] contiene il separatore previsto per il salvataggio ["+property.getAppendSeparator()+"]");
		}
	}

	private static void validaSelect(Item item,ConfigBean metadata) throws ValidationException, ProviderException{
		Property property = item.getProperty();
		// se e' una property di tipo append valido il separatore
		if(property.isAppend()) {
			String defaultValue = getDefault(item, metadata);
			if(defaultValue != null && defaultValue.contains(property.getAppendSeparator()))
				throw new ValidationException("Il valore indicato per l'attributo Default ["+item.getValue()+"] contiene il separatore previsto per il salvataggio ["+property.getAppendSeparator()+"]");
		}
		
		if(item.getValues() == null || item.getValues().sizeValueList() == 0) {
			if(metadata.getProvider()==null || metadata.getProvider().getValues(item.getName())==null || metadata.getProvider().getValues(item.getName()).size()<=0) {
				throw new ValidationException("E' necessario indicare una lista di Values, o definirli in un plugins, per gli Item di tipo Select");
			}
		}
	}

	private static void validaText(Item item,ConfigBean metadata) throws ValidationException, ProviderException{
		Property property = item.getProperty();
		// se e' una property di tipo append valido il separatore
		if(property.isAppend()) {
			String defaultValue = getDefault(item, metadata);
			if(defaultValue != null &&  defaultValue.contains(property.getAppendSeparator()))
				throw new ValidationException("Il valore indicato per l'attributo Default ["+item.getValue()+"] contiene il separatore previsto per il salvataggio ["+property.getAppendSeparator()+"]");
		}
	}

	private static void validaConditions(Conditions conditions,ConfigBean metadata) throws ValidationException {
		if(conditions == null) return;
		
		for (int i = 0; i < conditions.sizeConditionList(); i ++) {
		Condition condition = conditions.getCondition(i);
			validaCondition(condition, (i+1),metadata);
		}
	}

	private static void validaCondition(Condition condition, int indice,ConfigBean metadata) throws ValidationException{ 
		if((condition.getDefinedList() == null || condition.getDefinedList().size() == 0) 
				&& (condition.getEqualsList() == null || condition.getEqualsList().size() == 0)
				&& (condition.getLessEqualsList() == null || condition.getLessEqualsList().size() == 0)
				&& (condition.getLessThenList() == null || condition.getLessThenList().size() == 0)
				&& (condition.getGreaterEqualsList() == null || condition.getGreaterEqualsList().size() == 0)
				&& (condition.getGreaterThenList() == null || condition.getGreaterThenList().size() == 0)
				&& (condition.getSelectedList() == null || condition.getSelectedList().size() == 0))
			throw new ValidationException("La condition numero ["+indice+"] non e' valida: indicare almeno un elemento tra Defined, Equals o Selected.");
		
		// controlli sulle condizioni le condizioni devono riferire a elementi presenti
		for (int i = 0; i < condition.getDefinedList().size(); i++) {
			Defined defined = condition.getDefined(i);
			if(!itemDefined(defined.getName(),metadata))
				throw new ValidationException("L'elemento Defined numero ["+(i+1)+"] della Condition numero ["
						+indice+"] non e' valido: si riferisce ad un elemento ["+defined.getName()+"] non presente nella configurazione.");
				
			BaseItemBean<?> itemToCheck = metadata.getItem(defined.getName()); 
			ItemType itemToCheckType = itemToCheck.getItemType();
			
			// la condizione defined non si puo' attivare su un elemento checkbox
			if(itemToCheckType.equals(ItemType.CHECKBOX)) {
				throw new ValidationException("L'elemento Defined numero ["+(i+1)+"] della Condition numero ["
						+indice+"] non e' valido: si riferisce ad un elemento ["+defined.getName()+"] che ha un tipo non compatibile con la regola, non si puo' controllare se una CheckBox e' Defined");
			}
		}
		
		for (int i = 0; i < condition.getEqualsList().size(); i++) {
			checkEqualsTypeCondition(condition.getEquals(i), condition, indice, metadata, i, "equals");
		}
		for (int i = 0; i < condition.getLessEqualsList().size(); i++) {
			checkEqualsTypeCondition(condition.getLessEquals(i), condition, indice, metadata, i, "lessEquals");
		}
		for (int i = 0; i < condition.getLessThenList().size(); i++) {
			checkEqualsTypeCondition(condition.getLessThen(i), condition, indice, metadata, i, "lessThen");
		}
		for (int i = 0; i < condition.getGreaterEqualsList().size(); i++) {
			checkEqualsTypeCondition(condition.getGreaterEquals(i), condition, indice, metadata, i, "greaterEquals");
		}
		for (int i = 0; i < condition.getGreaterThenList().size(); i++) {
			checkEqualsTypeCondition(condition.getGreaterThen(i), condition, indice, metadata, i, "greaterThen");
		}
		
		for (int i = 0; i < condition.getSelectedList().size(); i++) {
			Selected selected = condition.getSelected(i);
			if(!itemDefined(selected.getName(),metadata))
				throw new ValidationException("L'elemento Selected numero ["+(i+1)+"] della Condition numero ["
						+indice+"] non e' valido: si riferisce ad un elemento ["+selected.getName()+"] non presente nella configurazione.");
			
			BaseItemBean<?> itemToCheck = metadata.getItem(selected.getName()); 
			ItemType itemToCheckType = itemToCheck.getItemType();
			
			// la condizione selected si puo' attivare su un elemento checkbox
			if(!itemToCheckType.equals(ItemType.CHECKBOX)) {
				throw new ValidationException("L'elemento Selected numero ["+(i+1)+"] della Condition numero ["
						+indice+"] non e' valido: si riferisce ad un elemento ["+selected.getName()+"] che non e' di tipo CheckBox");
			}
		}
	}
	
	private static void checkEqualsTypeCondition(Equals equals, Condition condition, int indice,ConfigBean metadata, int i, String tipo) throws ValidationException {
		if(!itemDefined(equals.getName(),metadata))
			throw new ValidationException("L'elemento '"+tipo+"' numero ["+(i+1)+"] della Condition numero ["
					+indice+"] non e' valido: si riferisce ad un elemento ["+equals.getName()+"] non presente nella configurazione.");
			
		BaseItemBean<?> itemToCheck = metadata.getItem(equals.getName()); 
		ItemType itemToCheckType = itemToCheck.getItemType();
		
		// la condizione equals non si puo' attivare su un elemento checkbox
		if(itemToCheckType.equals(ItemType.CHECKBOX)) {
			throw new ValidationException("L'elemento '"+tipo+"' numero ["+(i+1)+"] della Condition numero ["
					+indice+"] non e' valido: si riferisce ad un elemento ["+equals.getName()+"] che ha un tipo non compatibile con la regola, non si puo' controllare se una CheckBox e' Equals");
		}
	}
	
	private static boolean itemDefined(String itemName, ConfigBean metadata) {
		return metadata.getListakeys().contains(itemName);
	}
	
	public static ConfigBean getMetadata(Config config)  throws ValidationException{
		IProvider provider = null;
		if(StringUtils.isNotEmpty(config.getProvider())) {
			try {
				provider = (IProvider) ClassLoaderUtilities.newInstance(config.getProvider());
			}catch(Exception e) {
				throw new ValidationException("Errore durante l'istanziazione del provider ["+config.getProvider()+"]: "+e.getMessage(),e);
			}
		}
		ConfigBean cbTmp = new ConfigBean(provider);
		cbTmp.setId(config.getId());
		
		org.openspcoop2.core.mvc.properties.Properties properties = config.getProperties();
		if(properties != null) {
			List<Collection> collectionList = properties.getCollectionList();
			for (Collection collection : collectionList) {
				cbTmp.getListaNomiProperties().add(collection.getName());
			}
		}
		
		List<Section> sectionList = config.getSectionList();
		
		for (int i= 0; i < sectionList.size() ; i++) {
			Section section = sectionList.get(i);
			getMetadataSection(section,"s"+i,cbTmp);
		}
		
		return cbTmp;
	}
	
	private static void getMetadataSection(Section section, String sectionIdx,ConfigBean cbTmp) throws ValidationException{
		SectionBean sectionBean = new SectionBean(section,sectionIdx, cbTmp.getProvider());
		cbTmp.addItem(sectionBean);
		
		if(section.getItemList() != null) {
			for (Item item : section.getItemList()) {
				ItemBean itemBean = new ItemBean(item, item.getName(), cbTmp.getProvider()); 
				cbTmp.addItem(itemBean);
			}
		}
		
		if(section.getSubsectionList() != null) {
			for (int i= 0; i < section.getSubsectionList().size() ; i++) {
				Subsection subSection  = section.getSubsectionList().get(i);
				getMetadataSubsection(subSection,sectionIdx+ "_ss"+i,cbTmp);
			}
		}
	 
	}

	private static void getMetadataSubsection(Subsection subSection, String subsectionIdx, ConfigBean cbTmp) throws ValidationException {
		SubsectionBean subsectionBean = new SubsectionBean(subSection,subsectionIdx,cbTmp.getProvider());
		cbTmp.addItem(subsectionBean);
		
		if(subSection.getItemList() != null) {
			for (Item item : subSection.getItemList()) {
				ItemBean itemBean = new ItemBean(item, item.getName(),cbTmp.getProvider()); 
				cbTmp.addItem(itemBean);
			}
		}
	}
}
