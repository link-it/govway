package org.openspcoop2.core.config.rs.server.api.impl;

import org.openspcoop2.core.config.rs.server.api.FruizioniGruppiApi;
import org.openspcoop2.core.config.rs.server.model.Gruppo;
import org.openspcoop2.core.config.rs.server.model.GruppoAzioni;
import org.openspcoop2.core.config.rs.server.model.GruppoNome;
import org.openspcoop2.core.config.rs.server.model.ListaGruppi;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.utils.jaxrs.fault.FaultCode;
import org.openspcoop2.utils.jaxrs.impl.AuthorizationConfig;
import org.openspcoop2.utils.jaxrs.impl.AuthorizationManager;
import org.openspcoop2.utils.jaxrs.impl.BaseImpl;
import org.openspcoop2.utils.jaxrs.impl.ServiceContext;
/**
 * GovWay Config API
 *
 * <p>Servizi per la configurazione di GovWay
 *
 */
public class FruizioniGruppiApiServiceImpl extends BaseImpl implements FruizioniGruppiApi {

	public FruizioniGruppiApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(FruizioniGruppiApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		// TODO: Implement ...
		throw new Exception("NotImplemented");
	}

    /**
     * Aggiunta di azioni o risorse dell&#x27;API al gruppo
     *
     * Questa operazione consente di aggiungere azioni o risorse dell&#x27;API al gruppo
     *
     */
	@Override
    public void addAzioni(GruppoAzioni body, String erogatore, String nome, Integer versione, String nomeGruppo, ProfiloEnum profilo, String soggetto) {
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
        // TODO: Implement...
        
			context.getLogger().info("Invocazione completata con successo");
        
     
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
    
    /**
     * Creazione di un gruppo di azioni o risorse dell&#x27;API fruita
     *
     * Questa operazione consente di creare un gruppo di azioni o risorse dell&#x27;API fruita
     *
     */
	@Override
    public void create(Gruppo body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
        // TODO: Implement...
        
			context.getLogger().info("Invocazione completata con successo");
        
     
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
    
    /**
     * Elimina il gruppo identificato dal nome
     *
     * Questa operazione consente di eliminare il gruppo identificato dal nome
     *
     */
	@Override
    public void delete(String erogatore, String nome, Integer versione, String nomeGruppo, ProfiloEnum profilo, String soggetto) {
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
        // TODO: Implement...
        
			context.getLogger().info("Invocazione completata con successo");
        
     
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
    
    /**
     * Elimina azioni o risorse dell&#x27;API associate al gruppo
     *
     * Questa operazione consente di eliminare azioni o risorse dell&#x27;API associate al gruppo
     *
     */
	@Override
    public void deleteAzioni(String erogatore, String nome, Integer versione, String nomeGruppo, ProfiloEnum profilo, String soggetto) {
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
        // TODO: Implement...
        
			context.getLogger().info("Invocazione completata con successo");
        
     
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
    
    /**
     * Ricerca i gruppi in cui sono stati classificate le azioni o le risorse dell&#x27;API
     *
     * Elenca i gruppi in cui sono stati classificate le azioni o le risorse dell&#x27;API
     *
     */
	@Override
    public ListaGruppi findAll(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, Integer limit, Integer offset, String azione) {
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
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Restituisce azioni/risorse associate al gruppo identificato dal nome
     *
     * Questa operazione consente di ottenere le azioni associate al gruppo identificato dal nome
     *
     */
	@Override
    public GruppoAzioni getAzioni(String erogatore, String nome, Integer versione, String nomeGruppo, ProfiloEnum profilo, String soggetto) {
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
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Consente di modificare il nome del gruppo
     *
     * Questa operazione consente di aggiornare il nome di un gruppo
     *
     */
	@Override
    public void updateNome(GruppoNome body, String erogatore, String nome, Integer versione, String nomeGruppo, ProfiloEnum profilo, String soggetto) {
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
        // TODO: Implement...
        
			context.getLogger().info("Invocazione completata con successo");
        
     
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

