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

package org.openspcoop2.utils.cache.test;

import java.sql.Connection;

import org.slf4j.Logger;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.AbstractCacheWrapper;
import org.openspcoop2.utils.cache.CacheType;

/**
 * Cache
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExampleConfigCacheWrapper extends AbstractCacheWrapper {

	public ExampleConfigCacheWrapper(boolean initializeCache, Logger log) throws UtilsException {
		super(CacheType.JCS, "config", initializeCache, log);
	}

	@Override
	public Object getDriver(Object param) throws UtilsException {
		return new ExampleConfigDummyDriver((Connection)param);
	}

	@Override
	public boolean isCachableException(Throwable e) {
		return e instanceof ExampleExceptionNotFound;
	}

}
