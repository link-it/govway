/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import org.openspcoop2.core.mvc.properties.Condition;
import org.openspcoop2.core.mvc.properties.Conditions;
import org.openspcoop2.core.mvc.properties.Config;
import org.openspcoop2.core.mvc.properties.Defined;
import org.openspcoop2.core.mvc.properties.Equals;
import org.openspcoop2.core.mvc.properties.Item;
import org.openspcoop2.core.mvc.properties.Section;
import org.openspcoop2.core.mvc.properties.Selected;
import org.openspcoop2.core.mvc.properties.Subsection;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.properties.beans.BaseItemBean;
import org.openspcoop2.web.lib.mvc.properties.beans.ConfigBean;
import org.openspcoop2.web.lib.mvc.properties.exception.ConditionException;

/***
 * 
 * Motore che serve a decidere le condizioni di visualizzazione di un elemento
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ConditionsEngine {

	public static boolean resolve(Conditions conditions, ConfigBean configBean) throws ConditionException {
		try {
			if(conditions == null)
				return true;
			
			boolean isAnd = conditions.getAnd(); 
			boolean isNot = conditions.getNot();

			// Valore di partenza dell'esito totale e'
			// TRUE se devo controllare l'and delle condizioni
			// FALSE se devo controllare l'or delle condizioni
			boolean esito = isAnd ? true : false;

			for (Condition condition : conditions.getConditionList()) {
				boolean resCondition = resolve(condition,configBean);

				// aggiorno l'esito in base all'operazione da aggregare AND o OR
				esito = isAnd ? (esito && resCondition) : (esito || resCondition);
			}

			// eventuale NOT della condizione
			return isNot ? !esito : esito;
		}catch(Exception e) {
			throw new ConditionException("Errore durante la risoluzione delle Conditions: " + conditions.toString(), e);
		}
	}

	public static boolean resolve(Condition condition, ConfigBean configBean) throws ConditionException {
		try {
			boolean isAnd = condition.getAnd(); 
			boolean isNot = condition.getNot();

			// Valore di partenza dell'esito totale e'
			// TRUE se devo controllare l'and delle condizioni
			// FALSE se devo controllare l'or delle condizioni
			boolean esito = isAnd ? true : false;

			for (Defined defined : condition.getDefinedList()) {
				boolean resCondition = resolveDefined(defined,configBean);

				// aggiorno l'esito in base all'operazione da aggregare AND o OR
				esito = isAnd ? (esito && resCondition) : (esito || resCondition);
			}

			for (Equals equals: condition.getEqualsList()) {
				boolean resCondition = resolveEquals(EqualsType.EQUALS, equals,configBean);

				// aggiorno l'esito in base all'operazione da aggregare AND o OR
				esito = isAnd ? (esito && resCondition) : (esito || resCondition);
			}
			
			for (Equals equals: condition.getLessThenList()) {
				boolean resCondition = resolveEquals(EqualsType.LESS_THEN, equals,configBean);

				// aggiorno l'esito in base all'operazione da aggregare AND o OR
				esito = isAnd ? (esito && resCondition) : (esito || resCondition);
			}
			
			for (Equals equals: condition.getLessEqualsList()) {
				boolean resCondition = resolveEquals(EqualsType.LESS_EQUALS, equals,configBean);

				// aggiorno l'esito in base all'operazione da aggregare AND o OR
				esito = isAnd ? (esito && resCondition) : (esito || resCondition);
			}
			
			for (Equals equals: condition.getGreaterThenList()) {
				boolean resCondition = resolveEquals(EqualsType.GREATER_THEN, equals,configBean);

				// aggiorno l'esito in base all'operazione da aggregare AND o OR
				esito = isAnd ? (esito && resCondition) : (esito || resCondition);
			}
			
			for (Equals equals: condition.getGreaterEqualsList()) {
				boolean resCondition = resolveEquals(EqualsType.GREATER_EQUALS, equals,configBean);

				// aggiorno l'esito in base all'operazione da aggregare AND o OR
				esito = isAnd ? (esito && resCondition) : (esito || resCondition);
			}

			for (Equals equals: condition.getStartsWithList()) {
				boolean resCondition = resolveEquals(EqualsType.STARTS_WITH, equals,configBean);

				// aggiorno l'esito in base all'operazione da aggregare AND o OR
				esito = isAnd ? (esito && resCondition) : (esito || resCondition);
			}
			
			for (Equals equals: condition.getEndsWithList()) {
				boolean resCondition = resolveEquals(EqualsType.ENDS_WITH, equals,configBean);

				// aggiorno l'esito in base all'operazione da aggregare AND o OR
				esito = isAnd ? (esito && resCondition) : (esito || resCondition);
			}
			
			for (Selected selected : condition.getSelectedList()) {
				boolean resCondition = resolveSelected(selected,configBean);

				// aggiorno l'esito in base all'operazione da aggregare AND o OR
				esito = isAnd ? (esito && resCondition) : (esito || resCondition);
			}

			// eventuale NOT della condizione
			return isNot ? !esito : esito;
		}catch(Exception e) {
			throw new ConditionException("Errore durante la risoluzione della Condition: " + condition.toString(), e);
		}
	}

	public static boolean resolveSelected(Selected selected, ConfigBean configBean) throws ConditionException { 
		try {
			String elementName = selected.getName();
			boolean isNot = selected.getNot();
			BaseItemBean<?> item = configBean.getItem(elementName);

			boolean esito = item.isVisible() &&  ServletUtils.isCheckBoxEnabled(item.getValue());

			// eventuale NOT della condizione
			return isNot ? !esito : esito;
		}catch(Exception e) {
			throw new ConditionException("Errore durante la risoluzione della condizione Selected: " + selected.toString(), e);
		}
	}

	public static boolean resolveEquals(EqualsType equalsType, Equals equals, ConfigBean configBean) throws ConditionException {
		try {
			String elementName = equals.getName();
			boolean isNot = equals.getNot();
			String value = equals.getValue(); 

			BaseItemBean<?> item = configBean.getItem(elementName);

			boolean opValue = false;
			if(item.isVisible()) {
				switch (equalsType) {
				case EQUALS:
					opValue = value.equals(item.getValue());
					break;
				case LESS_THEN:
					opValue = (item.getValue()!=null && value.compareTo(item.getValue())<0);
					break;
				case LESS_EQUALS:
					opValue = value.equals(item.getValue()) || (item.getValue()!=null && value.compareTo(item.getValue())<0);
					break;
				case GREATER_THEN:
					opValue = (item.getValue()!=null && value.compareTo(item.getValue())>0);
					break;
				case GREATER_EQUALS:
					opValue = value.equals(item.getValue()) || (item.getValue()!=null && value.compareTo(item.getValue())>0);
					break;
				case STARTS_WITH:
					opValue = item.getValue()!=null && value!=null && item.getValue().startsWith(value);
					break;
				case ENDS_WITH:
					opValue = item.getValue()!=null && value!=null && item.getValue().endsWith(value);
					break;
				}
			}
			
			boolean esito = item.isVisible() && opValue;

			// eventuale NOT della condizione
			return isNot ? !esito : esito;
		}catch(Exception e) {
			throw new ConditionException("Errore durante la risoluzione della condizione Equals: " + equals.toString(), e);
		}
	}

	public static boolean resolveDefined(Defined defined, ConfigBean configBean) throws ConditionException {
		try {
			String elementName = defined.getName();
			boolean isNot = defined.getNot();

			BaseItemBean<?> item = configBean.getItem(elementName);

			boolean esito = item.isVisible() && item.getValue() != null;

			// eventuale NOT della condizione
			return isNot ? !esito : esito;
		}catch(Exception e) {
			throw new ConditionException("Errore durante la risoluzione della condizione Defined: " + defined.toString(), e);
		}
	}

	public static boolean controllaSezioniDaNascondere(Config config,ConfigBean cbTmp)  throws ConditionException{
		List<Section> sectionList = config.getSectionList();
		
		boolean show = true;
		for (int i= 0; i < sectionList.size() ; i++) {
			Section section = sectionList.get(i);
			show = showSection(section,"s"+i,cbTmp);
		}
		
		return show;
	}
	
	private static boolean showSection(Section section, String sectionIdx,ConfigBean cbTmp) throws ConditionException{
		BaseItemBean<?> itemSection = cbTmp.getItem(sectionIdx);
		
		// 1. se la sezione e' nascosta nascondo tutti gli elementi
		boolean show = itemSection.isVisible();

		if(!show) {
			if(section.getItemList() != null) {
				for (Item item : section.getItemList()) {
					BaseItemBean<?> item3 = cbTmp.getItem(item.getName());
					item3.setVisible(show);
				}
			}
			if(section.getSubsectionList() != null) {
				for (int i= 0; i < section.getSubsectionList().size() ; i++) {
					Subsection subSection  = section.getSubsectionList().get(i);
					showSubsection(subSection,sectionIdx+ "_ss"+i,cbTmp,show);
				}
			}
		} else {
			boolean allItemHidden = true;
			boolean allSubSectionHidden = true;
			
			if(section.getItemList() != null) {
				for (Item item : section.getItemList()) {
					BaseItemBean<?> item3 = cbTmp.getItem(item.getName());
					if(item3.isVisible()) {
						allItemHidden = false;
						break;
					}
				}
			}
			
			if(section.getSubsectionList() != null) {
				for (int i= 0; i < section.getSubsectionList().size() ; i++) {
					Subsection subSection  = section.getSubsectionList().get(i);
					if(showSubsection(subSection,sectionIdx+ "_ss"+i,cbTmp,show)) {
						allSubSectionHidden = allSubSectionHidden && false;
					}
				}
			}
			
			show = !(allItemHidden && allSubSectionHidden);
		}
		
	 
		// 2. se tutti gli elementi sono nascosti nascondo la sezione
		itemSection.setVisible(show);
		
		return show;
	}

	private static boolean showSubsection(Subsection subSection, String subsectionIdx, ConfigBean cbTmp, boolean showParent) throws ConditionException {
		BaseItemBean<?> itemSubSection = cbTmp.getItem(subsectionIdx);
		
		// 1. se la sezione e' nascosta nascondo tutti gli elementi
		boolean show = !showParent ? false : itemSubSection.isVisible();
		
		if(!show) {
			for (Item item : subSection.getItemList()) {
				BaseItemBean<?> item3 = cbTmp.getItem(item.getName());
				item3.setVisible(show);
			}
		} else {
			boolean allHidden = true;
			for (Item item : subSection.getItemList()) {
				BaseItemBean<?> item3 = cbTmp.getItem(item.getName());
				if(item3.isVisible()) {
					allHidden = false;
					break;
				}
			}
			
			show = !allHidden;
		}
		// 2. se tutti gli elementi sono nascosti nascondo la sezione
		itemSubSection.setVisible(show);
		
		return show;
	}
}

enum EqualsType{
	
	EQUALS, LESS_EQUALS, LESS_THEN, GREATER_EQUALS, GREATER_THEN, STARTS_WITH, ENDS_WITH
	
}
