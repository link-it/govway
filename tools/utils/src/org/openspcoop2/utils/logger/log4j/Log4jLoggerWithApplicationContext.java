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
