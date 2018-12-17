package org.openspcoop2.core.config.rs.server.api.impl;

import java.io.InputStream;

import org.openspcoop2.core.config.rs.server.api.ErogazioniApi;
import org.openspcoop2.core.config.rs.server.model.ApiImplAllegato;
import org.openspcoop2.core.config.rs.server.model.ApiImplInformazioniGenerali;
import org.openspcoop2.core.config.rs.server.model.ApiImplInformazioniGeneraliView;
import org.openspcoop2.core.config.rs.server.model.ApiImplUrlInvocazioneView;
import org.openspcoop2.core.config.rs.server.model.ApiImplVersioneApi;
import org.openspcoop2.core.config.rs.server.model.ApiImplVersioneApiView;
import org.openspcoop2.core.config.rs.server.model.Connettore;
import org.openspcoop2.core.config.rs.server.model.Erogazione;
import org.openspcoop2.core.config.rs.server.model.ErogazioneViewItem;
import org.openspcoop2.core.config.rs.server.model.ListaApiImplAllegati;
import org.openspcoop2.core.config.rs.server.model.ListaErogazioni;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.utils.jaxrs.fault.FaultCode;
import org.openspcoop2.utils.jaxrs.impl.AuthorizationConfig;
import org.openspcoop2.utils.jaxrs.impl.AuthorizationManager;
import org.openspcoop2.utils.jaxrs.impl.BaseImpl;
import org.openspcoop2.utils.jaxrs.impl.ServiceContext;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
/**
 * GovWay Config API
 *
 * <p>Servizi per la configurazione di GovWay
 *
 */
public class ErogazioniApiServiceImpl extends BaseImpl implements ErogazioniApi {

	public ErogazioniApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(ErogazioniApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		// TODO: Implement ...
		throw new Exception("NotImplemented");
	}

    /**
     * Creazione di un&#x27;erogazione di API
     *
     * Questa operazione consente di creare una erogazione di API
     *
     */
	@Override
    public void create(Object body, ProfiloEnum profilo, String soggetto) {
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			Erogazione ero = null;
            try{
            	ero = JSONUtils.getInstance().getAsObject(((InputStream)body), Erogazione.class);
            }catch(Throwable e) {
                    throw FaultCode.RICHIESTA_NON_VALIDA.toException(e);
            }
            
            
            ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
            
			AccordoServizioParteSpecifica asps = null;
			AccordoServizioParteComune as = null;	// TODO: Recuperare l'as da ero.getApiNome ecc.. 
		
		

            
            String[] soggettiCompatibili = new String[1];
            String[] accordiList = new String[1];
            String[] ptList = new String[1];
            boolean accordoPrivato = as.getPrivato()!=null && as.getPrivato();
            
            // TODO: Wrapper http servlet per calcolo endpointtype 
            String endpointtype = env.apsHelper.readEndPointType();
            
         /*   env.apsHelper.serviziCheckData(
            		TipoOperazione.ADD, 
            		soggettiCompatibili,		// TODO: Questa deve rifattorizzarla andrea.
            		accordiList, 		 		// TODO: Rifattorizzarla andrea
            		asps.getNome(),				// oldnome
            		asps.getTipo(),				// oldtipo
            		asps.getVersione(),			// oldversione
            		asps.getNome(),
            		asps.getTipo(),
            		env.idSoggetto.getId().toString(), 	// idSoggErogatore
            		env.idSoggetto.getNome(),
            		env.idSoggetto.getTipo(), 
            		as.getId().toString(),
            		as.getServiceBinding(),
            		null,  //servcorr,
            		endpointtype, // endpointtype,
            		ero.getConnettore().getEndpoint(),	// TODO: Usare il nome dell'oggetto finale e non del json.
            		null, 	// nome JMS
            		null, 	// tipo JMS,
            		null,	// user, TODO: recuperarlo autenticazione
            		null,   // password, TODO: recuperarlo autenticazione 
            		null,   // initcont JMS,
            		null,   // urlpgk JMS,
            		null,   // provurl JMS 
            		null,   // connfact JMS
            		null, 	// sendas JMS, 
            		"",		//  TODO: wsdlimpler, Recuperare il wsdl implementativo dell'erogatore.
            		"",		//  TODO: wsdlimplfru, sarà lo stesso di sopra?
            		"0", 	//  Se nella Add questo è a zero, bisognerà settarlo in una update.
            		env.tipo_protocollo, // TODO check,
            		"",		//  TODO: Questo è il servizio scelto in caso di SOAP, 
            		ptList,	//  TODO: Valorizzare. 
            		accordoPrivato,
            		false, 	//  TODO check da console this.privato,
            		httpsurl, 
            		httpstipologia, 
            		httpshostverify, 
            		httpspath, 
            		httpstipo,
            		httpspwd,
            		httpsalgoritmo,
            		httpsstato,
            		httpskeystore, 
            		httpspwdprivatekeytrust, 
            		httpspathkey,
            		httpstipokey, 
            		httpspwdkey, 
            		httpspwdprivatekey, 
            		httpsalgoritmokey,
            		tipoconn, 	// Boh forse asps.getTipo
            		versione, 	
            		validazioneDocumenti, 
            		backToStato,
            		autenticazioneHttp,
            		proxyEnabled, 
            		proxyHost,
            		proxyPort,
            		proxyUsername, 
            		proxyPassword, 
            		tempiRisposta_enabled, 
            		tempiRisposta_connectionTimeout, 
            		tempiRisposta_readTimeout, 
            		tempiRisposta_tempoMedioRisposta,
            		opzioniAvanzate, 
            		transfer_mode, 
            		transfer_mode_chunk_size, 
            		redirect_mode, 
            		redirect_max_hop, 
            		requestOutputFileName,
            		requestOutputFileNameHeaders, 
            		requestOutputParentDirCreateIfNotExists, 
            		requestOutputOverwriteIfExists,
            		responseInputMode,
            		responseInputFileName,
            		responseInputFileNameHeaders,
            		responseInputDeleteAfterRead,
            		responseInputWaitTime,
            		erogazioneSoggetto,
            		erogazioneRuolo, 
            		erogazioneAutenticazione,
            		erogazioneAutenticazioneOpzionale,
            		erogazioneAutorizzazione,
            		erogazioneAutorizzazioneAutenticati, 
            		erogazioneAutorizzazioneRuoli, 
            		erogazioneAutorizzazioneRuoliTipologia,
            		erogazioneAutorizzazioneRuoliMatch, 
            		isSupportatoAutenticazione,
            		generaPACheckSoggetto, 
            		listExtendedConnettore,
            		fruizioneServizioApplicativo,
            		fruizioneRuolo, 
            		fruizioneAutenticazione,
            		fruizioneAutenticazioneOpzionale, 
            		fruizioneAutorizzazione, 
            		fruizioneAutorizzazioneAutenticati, 
            		fruizioneAutorizzazioneRuoli,
            		fruizioneAutorizzazioneRuoliTipologia,
            		fruizioneAutorizzazioneRuoliMatch,
            		protocollo, 
            		allegatoXacmlPolicy,
            		descrizione,
            		tipoFruitore,
            		nomeFruitore
            	);
            */
            //ero.get

            /* Sostituendolo con la properties deternino il nome 
            if (apsHelper.isModalitaStandard()) {
				switch (this.serviceBinding) {
				case REST:
					// il nome del servizio e' quello dell'accordo
					this.nomeservizio = as.getNome();
					break;
				case SOAP:
				default:
					// il nome del servizio e' quello del porttype selezionato
					this.nomeservizio = this.portType;
					break;
				}
			}*/

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
     * Creazione di un allegato nell&#x27;erogazione di API
     *
     * Questa operazione consente di aggiungere un allegato all&#x27;erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public void createAllegato(ApiImplAllegato body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
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
     * Elimina un&#x27;erogazione di api
     *
     * Questa operazione consente di eliminare un&#x27;erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public void delete(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
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
     * Elimina un allegato dall&#x27;erogazione
     *
     * Questa operazione consente di eliminare un&#x27;allegato dell&#x27;erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public void deleteAllegato(String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo, String soggetto) {
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
     * Restituisce l&#x27;allegato di una erogazione
     *
     * Questa operazione consente di ottenere l&#x27;allegato di un&#x27;erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public byte[] downloadAllegato(String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo, String soggetto) {
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
     * Ricerca erogazioni di api
     *
     * Elenca le erogazioni di API
     *
     */
	@Override
    public ListaErogazioni findAll(ProfiloEnum profilo, String soggetto, String q, Integer limit, Integer offset, TipoApiEnum tipoApi) {
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
     * Elenco allegati di un&#x27;erogazione di API
     *
     * Questa operazione consente di ottenere gli allegati di un&#x27;erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public ListaApiImplAllegati findAllAllegati(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String q, Integer limit, Integer offset) {
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
     * Restituisce i dettagli di un&#x27;erogazione di API
     *
     * Questa operazione consente di ottenere i dettagli di una erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public ErogazioneViewItem get(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		ServiceContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			ErogazioneViewItem ret = new ErogazioneViewItem();
			//ret.set
                        
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
     * Restituisce le informazioni sull&#x27;API implementata dall&#x27;erogazione
     *
     * Questa operazione consente di ottenere le informazioni sull&#x27;API implementata dall&#x27;erogazione identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiImplVersioneApiView getAPI(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
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
     * Restituisce il dettaglio di un allegato dell&#x27;erogazione
     *
     * Questa operazione consente di ottenere il dettaglio di un allegato dell&#x27;erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiImplAllegato getAllegato(String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo, String soggetto) {
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
     * Restituisce le informazioni su connettore associato all&#x27;erogazione
     *
     * Questa operazione consente di ottenere le informazioni sul connettore associato all&#x27;erogazione identificata dal nome e dalla versione
     *
     */
	@Override
    public Connettore getConnettore(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
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
     * Restituisce le informazioni generali di un&#x27;erogazione di API
     *
     * Questa operazione consente di ottenere le informazioni generali di un&#x27;erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiImplInformazioniGeneraliView getInformazioniGenerali(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
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
     * Restituisce le informazioni sull&#x27;url di invocazione necessaria ad invocare l&#x27;erogazione
     *
     * Questa operazione consente di ottenere le informazioni sull&#x27;url di invocazione necessaria ad invocare l&#x27;erogazione identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiImplUrlInvocazioneView getUrlInvocazione(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
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
     * Consente di modificare la versione dell&#x27;API implementata dall&#x27;erogazione
     *
     * Questa operazione consente di aggiornare la versione dell&#x27;API implementata dall&#x27;erogazione identificata dal nome e dalla versione
     *
     */
	@Override
    public void updateAPI(ApiImplVersioneApi body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
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
     * Modifica i dati di un allegato dell&#x27;erogazione
     *
     * Questa operazione consente di aggiornare i dettagli di un allegato dell&#x27;erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public void updateAllegato(ApiImplAllegato body, String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo, String soggetto) {
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
     * Consente di modificare la configurazione del connettore associato all&#x27;erogazione
     *
     * Questa operazione consente di aggiornare la configurazione del connettore associato all&#x27;erogazione identificata dal nome e dalla versione
     *
     */
	@Override
    public void updateConnettore(Connettore body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
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
     * Consente di modificare le informazioni generali di un&#x27;erogazione di API
     *
     * Questa operazione consente di aggiornare le informazioni generali di un&#x27;erogazione di API identificata dal nome e dalla versione
     *
     */
	@Override
    public void updateInformazioniGenerali(ApiImplInformazioniGenerali body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
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
     * Consente di modificare la configurazione utilizzata per identificare l&#x27;azione invocata dell&#x27;API implementata dall&#x27;erogazione
     *
     * Questa operazione consente di aggiornare la configurazione utilizzata dal Gateway per identificare l&#x27;azione invocata
     *
     */
	@Override
    public void updateUrlInvocazione(Object body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
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

