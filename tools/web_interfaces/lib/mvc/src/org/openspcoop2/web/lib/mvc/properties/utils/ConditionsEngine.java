/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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

import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.properties.Condition;
import org.openspcoop2.web.lib.mvc.properties.Conditions;
import org.openspcoop2.web.lib.mvc.properties.Defined;
import org.openspcoop2.web.lib.mvc.properties.Equals;
import org.openspcoop2.web.lib.mvc.properties.Selected;
import org.openspcoop2.web.lib.mvc.properties.beans.BaseItemBean;
import org.openspcoop2.web.lib.mvc.properties.beans.ConfigBean;

/***
 * 
 * Motore che serve a decidere le condizioni di visualizzazione di un elemento
 * 
 * @author pintori
 *
 */
public class ConditionsEngine {

	public static boolean resolve(Conditions conditions, ConfigBean configBean) throws Exception {
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
			throw new Exception("Errore durante la risoluzione delle Conditions: " + conditions.toString(), e);
		}
	}

	public static boolean resolve(Condition condition, ConfigBean configBean) throws Exception {
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
				boolean resCondition = resolveEquals(equals,configBean);

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
			throw new Exception("Errore durante la risoluzione della Condition: " + condition.toString(), e);
		}
	}

	public static boolean resolveSelected(Selected selected, ConfigBean configBean) throws Exception { 
		try {
			String elementName = selected.getName();
			boolean isNot = selected.getNot();
			BaseItemBean<?> item = configBean.getItem(elementName);

			boolean esito = ServletUtils.isCheckBoxEnabled(item.getValue());

			// eventuale NOT della condizione
			return isNot ? !esito : esito;
		}catch(Exception e) {
			throw new Exception("Errore durante la risoluzione della condizione Selected: " + selected.toString(), e);
		}
	}

	public static boolean resolveEquals(Equals equals, ConfigBean configBean) throws Exception {
		try {
			String elementName = equals.getName();
			boolean isNot = equals.getNot();
			String value = equals.getValue(); 

			BaseItemBean<?> item = configBean.getItem(elementName);

			boolean esito = value.equals(item.getValue());

			// eventuale NOT della condizione
			return isNot ? !esito : esito;
		}catch(Exception e) {
			throw new Exception("Errore durante la risoluzione della condizione Equals: " + equals.toString(), e);
		}
	}

	public static boolean resolveDefined(Defined defined, ConfigBean configBean) throws Exception {
		try {
			String elementName = defined.getName();
			boolean isNot = defined.getNot();

			BaseItemBean<?> item = configBean.getItem(elementName);

			boolean esito = item.getValue() != null;

			// eventuale NOT della condizione
			return isNot ? !esito : esito;
		}catch(Exception e) {
			throw new Exception("Errore durante la risoluzione della condizione Defined: " + defined.toString(), e);
		}
	}


}
