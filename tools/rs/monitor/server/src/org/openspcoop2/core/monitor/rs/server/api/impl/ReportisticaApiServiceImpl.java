package org.openspcoop2.core.monitor.rs.server.api.impl;

import org.openspcoop2.core.monitor.rs.server.api.*;
import org.openspcoop2.core.monitor.rs.server.model.GenerazioneReport;
import org.openspcoop2.core.monitor.rs.server.model.ReportGrafico;

import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
/**
 * GovWay Monitor API
 *
 * <p>Servizi per il monitoraggio di GovWay
 *
 */
public class ReportisticaApiServiceImpl extends BaseImpl implements ReportisticaApi {

	public ReportisticaApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(ReportisticaApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		// TODO: Implement ...
		throw new Exception("NotImplemented");
	}

    /**
     * genera report statistico
     *
     * Questa operazione consente di generare un report statistico
     *
     */
	@Override
    public ReportGrafico generaReport(GenerazioneReport body) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
        // TODO: Implement...
        
			context.getLogger().info("Invocazione completata con successo");
        return null;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
}

