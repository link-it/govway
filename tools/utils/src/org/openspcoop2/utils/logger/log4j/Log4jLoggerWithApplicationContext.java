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

package org.openspcoop2.utils.logger.log4j;

import java.util.List;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.logger.beans.context.application.ApplicationContext;
import org.openspcoop2.utils.logger.beans.context.application.ApplicationTransaction;
import org.openspcoop2.utils.logger.beans.context.core.AbstractContext;
import org.openspcoop2.utils.logger.beans.context.core.BaseClient;
import org.openspcoop2.utils.logger.beans.context.core.BaseServer;
import org.openspcoop2.utils.logger.config.DiagnosticConfig;
import org.openspcoop2.utils.logger.config.Log4jConfig;
import org.openspcoop2.utils.logger.config.MultiLoggerConfig;

/**
 * Log4jLoggerWithApplicationContext
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Log4jLoggerWithApplicationContext extends AbstractLog4JLoggerWithContext  {

	public Log4jLoggerWithApplicationContext(MultiLoggerConfig config) throws UtilsException {
		super(config);
	}

	public Log4jLoggerWithApplicationContext(DiagnosticConfig diagnosticConfig, Log4jConfig logConfig) throws UtilsException {
		super(diagnosticConfig, logConfig);
	}

	@Override
	protected AbstractContext newContext() {
		return new ApplicationContext();
	}

	@Override
	protected BaseClient getClient() {
		if(this.context==null) {
			return null;
		}
		if(this.context instanceof ApplicationContext) {
			ApplicationContext appContext = (ApplicationContext) this.context;
			if(appContext.getTransaction()!=null) {
				return ((ApplicationTransaction)appContext.getTransaction()).getClient();
			}
		}
		return null;
	}

	@Override
	protected List<BaseServer> getServers() {
		if(this.context==null) {
			return null;
		}
		if(this.context instanceof ApplicationContext) {
			ApplicationContext appContext = (ApplicationContext) this.context;
			if(appContext.getTransaction()!=null) {
				return ((ApplicationTransaction)appContext.getTransaction()).getServers();
			}
		}
		return null;
	}
	
	
}
