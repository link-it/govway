package org.openspcoop2.core.monitor.rs.server.api.impl;

import org.joda.time.DateTime;
import org.openspcoop2.core.monitor.rs.server.api.MonitoraggioApi;
import org.openspcoop2.core.monitor.rs.server.model.RisultatoRicercaTransazioni;
import org.openspcoop2.core.monitor.rs.server.model.TransazioneDetail;
import org.openspcoop2.core.monitor.rs.server.model.TransazioneSearchFilter;
import org.openspcoop2.utils.jaxrs.fault.FaultCode;
import org.openspcoop2.utils.jaxrs.impl.AuthorizationConfig;
import org.openspcoop2.utils.jaxrs.impl.AuthorizationManager;
import org.openspcoop2.utils.jaxrs.impl.BaseImpl;
import org.openspcoop2.utils.jaxrs.impl.ServiceContext;
/**
 * GovWay Monitor API
 *
 * <p>Servizi per il monitoraggio di GovWay
 *
 */
public class MonitoraggioApiServiceImpl extends BaseImpl implements MonitoraggioApi {

	public MonitoraggioApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(MonitoraggioApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		// TODO: Implement ...
		throw new Exception("NotImplemented");
	}

    /**
     * Ricerca semplificata delle transazioni in base ai parametri di uso più comune
     *
     * Permette di recuperare i dettagli delle transazioni, gestite su GovWay, ricercandole in base a parametri di filtro di uso più comune
     *
     */
	@Override
    public RisultatoRicercaTransazioni baseSearchTransaction(String profilo, DateTime dataDa, DateTime dataA, String soggettoLocale, String soggettoRemoto, String servizio, String azione, String esito, String idApplicativo, String idMessaggio, String idT, Integer pagina, Integer risultatiPerPagina, String ordinamento) {
		ServiceContext context = this.getContext();
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
		catch(Exception e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * dettaglio della transazione
     *
     * Questa operazione consente di ottenere il dettaglio di una transazione
     *
     */
	@Override
    public TransazioneDetail getTransazione(String id) {
		ServiceContext context = this.getContext();
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
		catch(Exception e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * ricerca transazioni
     *
     * Questa operazione consente di effettuare una ricerca nell&#x27;archivio delle transazioni specificando i criteri di filtro
     *
     */
	@Override
    public Object searchTransazioni(TransazioneSearchFilter body, Integer pagina, Integer risultatiPerPagina, String ordinamento) {
		ServiceContext context = this.getContext();
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
		catch(Exception e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
}

