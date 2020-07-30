/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.dao;

import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.AbstractCacheWrapper;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.slf4j.Logger;


/***
 * 
 * Funzionalita' di supporto per la gestione delle maschere di ricerca.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DynamicUtilsServiceCache extends AbstractCacheWrapper {

	public static String JMX_DOMAIN = "org.openspcoop2.monitor";
	
	public static final String CACHE_NAME_DATI_CONFIGURAZIONE = "datiConfigurazione";
	public static final String CACHE_NAME_RICERCHE_CONFIGURAZIONE = "ricercheConfigurazione";
	
	public DynamicUtilsServiceCache(String cacheName, Logger log, Integer cacheSize, CacheAlgorithm cacheAlgorithm,
			Integer itemIdleTimeSeconds, Integer itemLifeTimeSeconds) throws UtilsException {
		super(cacheName, log, cacheSize, cacheAlgorithm, itemIdleTimeSeconds, itemLifeTimeSeconds);
	}

	@Override
	public Object getDriver(Object param) throws UtilsException {
		return param; // mi passo il driver
	}

	@Override
	public boolean isCachableException(Throwable e) {
		return e instanceof NotFoundException; // per MBeanUtils
	}

	
	
}
