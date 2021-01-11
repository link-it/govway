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

package org.openspcoop2.core.monitor.rs.server.filter;

import java.io.IOException;

import org.openspcoop2.core.monitor.rs.server.config.LoggerProperties;
import org.openspcoop2.core.monitor.rs.server.config.ServerProperties;
import org.openspcoop2.utils.transport.http.AbstractCORSFilter;
import org.openspcoop2.utils.transport.http.CORSFilterConfiguration;
import org.slf4j.Logger;

/**
 * CORSFilter
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class CORSFilter extends AbstractCORSFilter {

	private static CORSFilterConfiguration CORS_FILTER_CONFIGURATION;
	private static synchronized void initCORSFilterConfiguration() throws IOException {
		if(CORSFilter.CORS_FILTER_CONFIGURATION==null) {
			try {
				ServerProperties serverProperties = ServerProperties.getInstance();
				CORSFilter.CORS_FILTER_CONFIGURATION = new CORSFilterConfiguration();
				CORSFilter.CORS_FILTER_CONFIGURATION.init(serverProperties.getProperties());
			}catch(Exception e) {
				throw new IOException(e.getMessage(),e);
			}
		}
	}
	private static CORSFilterConfiguration getCORSFilterConfiguration() throws IOException {
		if(CORSFilter.CORS_FILTER_CONFIGURATION==null) {
			CORSFilter.initCORSFilterConfiguration();
		}
		return CORSFilter.CORS_FILTER_CONFIGURATION;
	}
	
	@Override
	protected CORSFilterConfiguration getConfig() throws IOException {
		return CORSFilter.getCORSFilterConfiguration();
	}

	@Override
	protected Logger getLog() {
		return LoggerProperties.getLoggerCore();
	}

}
