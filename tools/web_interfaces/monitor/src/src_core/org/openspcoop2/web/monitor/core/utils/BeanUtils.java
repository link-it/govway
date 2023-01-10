/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.utils;

import java.util.List;

import org.openspcoop2.utils.beans.BlackListElement;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;

/****
 * 
 * Implementa funzionalita' di gestione dei bean.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class BeanUtils {

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	/*****
	 * 
	 * Implementa la funzione di copia del valore delle properties dell'oggetto
	 * sorgente in quello destinazione.
	 * 
	 * E' possibile specificare un elenco di metodi setter che si vogliono non
	 * invocare.
	 * 
	 * @param oggettoDestinazione
	 * @param oggettoOriginale
	 * @param metodiEsclusi
	 */
	public static void copy(Object oggettoDestinazione,
			Object oggettoOriginale, List<BlackListElement> metodiEsclusi) {

		org.openspcoop2.utils.beans.BeanUtils.copy(log, oggettoDestinazione, oggettoOriginale, metodiEsclusi);
		
	}
}
