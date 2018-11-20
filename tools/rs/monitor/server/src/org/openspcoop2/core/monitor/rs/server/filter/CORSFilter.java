package org.openspcoop2.core.monitor.rs.server.filter;

import java.io.IOException;

import org.openspcoop2.core.monitor.rs.server.config.LoggerProperties;
import org.openspcoop2.core.monitor.rs.server.config.ServerProperties;
import org.openspcoop2.utils.transport.http.AbstractCORSFilter;
import org.openspcoop2.utils.transport.http.CORSFilterConfiguration;
import org.slf4j.Logger;

public class CORSFilter extends AbstractCORSFilter {

	private static CORSFilterConfiguration CORS_FILTER_CONFIGURATION;
	private static synchronized void initCORSFilterConfiguration() throws IOException {
		if(CORS_FILTER_CONFIGURATION==null) {
			try {
				ServerProperties serverProperties = ServerProperties.getInstance();
				CORS_FILTER_CONFIGURATION = new CORSFilterConfiguration();
				CORS_FILTER_CONFIGURATION.init(serverProperties.getProperties());
			}catch(Exception e) {
				throw new IOException(e.getMessage(),e);
			}
		}
	}
	private static CORSFilterConfiguration getCORSFilterConfiguration() throws IOException {
		if(CORS_FILTER_CONFIGURATION==null) {
			initCORSFilterConfiguration();
		}
		return CORS_FILTER_CONFIGURATION;
	}
	
	@Override
	protected CORSFilterConfiguration getConfig() throws IOException {
		return getCORSFilterConfiguration();
	}

	@Override
	protected Logger getLog() {
		return LoggerProperties.getLoggerCore();
	}

}
