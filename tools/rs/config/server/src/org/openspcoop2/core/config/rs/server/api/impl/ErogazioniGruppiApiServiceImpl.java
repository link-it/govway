package org.openspcoop2.core.config.rs.server.api.impl;

import org.openspcoop2.core.config.rs.server.api.ErogazioniGruppiApi;
import org.openspcoop2.core.config.rs.server.model.Gruppo;
import org.openspcoop2.core.config.rs.server.model.GruppoAzioni;
import org.openspcoop2.core.config.rs.server.model.GruppoNome;
import org.openspcoop2.core.config.rs.server.model.GruppoNuovaConfigurazione;
import org.openspcoop2.core.config.rs.server.model.ListaGruppi;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.utils.jaxrs.fault.FaultCode;
import org.openspcoop2.utils.jaxrs.impl.AuthorizationConfig;
import org.openspcoop2.utils.jaxrs.impl.AuthorizationManager;
import org.openspcoop2.utils.jaxrs.impl.BaseImpl;
import org.openspcoop2.utils.jaxrs.impl.ServiceContext;
import org.openspcoop2.web.lib.mvc.ServletUtils;
/**
 * GovWay Config API
 *
 * <p>Servizi per la configurazione di GovWay
 *
 */
public class ErogazioniGruppiApiServiceImpl extends BaseImpl implements ErogazioniGruppiApi {

	public ErogazioniGruppiApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(ErogazioniGruppiApiServiceImpl.class));
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
    public void addAzioni(GruppoAzioni body, String nome, Integer versione, String nomeGruppo, ProfiloEnum profilo, String soggetto) {
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
     * Creazione di un gruppo di azioni o risorse dell&#x27;API erogata
     *
     * Questa operazione consente di creare un gruppo di azioni o risorse dell&#x27;API erogata
     *
     */
	@Override
    public void create(Gruppo body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			// Probabilmente tradurre con la fromMap la configurazione in 
			// 	"GruppoEreditaNuovaConfigurazione" o "GruppoNuovaConfigurazione"
			body.getConfigurazione();
			
			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     

			GruppoNuovaConfigurazione g = new GruppoNuovaConfigurazione();
			
			
			//  Questo è true quando voglio definire un nuovo connettore, e lo è solo in modalità avanzata.
			//	if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) { 

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
    public void delete(String nome, Integer versione, String nomeGruppo, ProfiloEnum profilo, String soggetto) {
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
    public void deleteAzioni(String nome, Integer versione, String nomeGruppo, ProfiloEnum profilo, String soggetto) {
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
    public ListaGruppi findAll(String nome, Integer versione, ProfiloEnum profilo, String soggetto, Integer limit, Integer offset, String azione) {
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
    public GruppoAzioni getAzioni(String nome, Integer versione, String nomeGruppo, ProfiloEnum profilo, String soggetto) {
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
    public void updateNome(GruppoNome body, String nome, Integer versione, String nomeGruppo, ProfiloEnum profilo, String soggetto) {
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

