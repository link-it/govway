package org.openspcoop2.core.config.rs.server.api.impl.api;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.rs.server.api.ApiApi;
import org.openspcoop2.core.config.rs.server.api.impl.HttpRequestWrapper;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.Api;
import org.openspcoop2.core.config.rs.server.model.ApiAllegato;
import org.openspcoop2.core.config.rs.server.model.ApiAzione;
import org.openspcoop2.core.config.rs.server.model.ApiDescrizione;
import org.openspcoop2.core.config.rs.server.model.ApiInformazioniGenerali;
import org.openspcoop2.core.config.rs.server.model.ApiInformazioniGeneraliView;
import org.openspcoop2.core.config.rs.server.model.ApiInterfaccia;
import org.openspcoop2.core.config.rs.server.model.ApiInterfacciaView;
import org.openspcoop2.core.config.rs.server.model.ApiItem;
import org.openspcoop2.core.config.rs.server.model.ApiReferenteView;
import org.openspcoop2.core.config.rs.server.model.ApiRisorsa;
import org.openspcoop2.core.config.rs.server.model.ApiServizio;
import org.openspcoop2.core.config.rs.server.model.ApiViewItem;
import org.openspcoop2.core.config.rs.server.model.HttpMethodEnum;
import org.openspcoop2.core.config.rs.server.model.ListaApi;
import org.openspcoop2.core.config.rs.server.model.ListaApiAllegati;
import org.openspcoop2.core.config.rs.server.model.ListaApiAzioni;
import org.openspcoop2.core.config.rs.server.model.ListaApiRisorse;
import org.openspcoop2.core.config.rs.server.model.ListaApiServizi;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.core.config.rs.server.model.RuoloAllegatoAPI;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import org.openspcoop2.core.config.rs.server.utils.WrapperFormFile;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.constants.HttpMethod;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.basic.archive.APIUtils;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviHelper;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
/**
 * GovWay Config API
 *
 * <p>Servizi per la configurazione di GovWay
 *
 */
public class ApiApiServiceImpl extends BaseImpl implements ApiApi {

	public ApiApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(ApiApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		return new AuthorizationConfig(ServerProperties.getInstance().getProperties());
	}

    /**
     * Creazione di un&#x27;API
     *
     * Questa operazione consente di creare una API
     *
     */
	@Override
    public void create(Api body, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");
			
			ApiApiHelper.correggiDeserializzazione(body); // TODO: Fare i controlli sulla coppia tipo/formato?
				
			if (body.getReferente() == null || "".equals(body.getReferente().trim())) {
				body.setReferente(soggetto);
			}
			
			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.accordoApiToRegistro(body, env);
			
			if (env.apcCore.existsAccordoServizio(env.idAccordoFactory.getIDAccordoFromAccordo(as)))
				throw FaultCode.CONFLITTO.toException("Api già esistente");

			boolean validazioneDocumenti = ServerProperties.getInstance().isValidazioneDocumenti();
					
			if (!env.apcHelper.accordiCheckData(
					TipoOperazione.ADD,
					as.getNome(),
					as.getDescrizione(),
					as.getProfiloCollaborazione().toString(),
					Helper.toBinaryParameter(as.getByteWsdlDefinitorio()), // wsdldef
					Helper.toBinaryParameter(as.getByteWsdlConcettuale()), // wsdlconc, 
					Helper.toBinaryParameter(as.getByteWsdlLogicoErogatore()),//wsdlserv
					Helper.toBinaryParameter(as.getByteWsdlLogicoFruitore()), // wsdlservcorr,
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine()),
					"",		// scadenza
					"0",	// Id intero del servizio applicativo
					as.getSoggettoReferente().getNome(),
					as.getVersione().toString(),
					null,	// accordoCooperazione
					false,	// visibilitaAccordoServizio
					false,	// visibilitaAccordoCooperazione
					null,	// ID AccordoOld 
					new BinaryParameter(),	// wsblconc
					new BinaryParameter(), 	// wsblserv
					new BinaryParameter(), 	// wsblservcorr
					validazioneDocumenti, 
					env.tipo_protocollo,
					null,	// backToStato
					env.apcCore.toMessageServiceBinding(as.getServiceBinding()),
					null,	// messageType 
					ApiApiHelper.interfaceTypeFromFormatoSpecifica.get(as.getFormatoSpecifica()),
					env.gestisciSoggettoReferente)) {
				
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}
			
			env.apcCore.performCreateOperation(env.userLogin, false, as);
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
     * Creazione di un allegato di una API
     *
     * Questa operazione consente di aggiungere un allegato alla API identificata dal nome e dalla versione
     *
     */
	@Override
    public void createAllegato(ApiAllegato body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Body non presente");
			
			ApiEnv env = new ApiEnv(profilo, soggetto,context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api corrisponde ai parametri indicati");
			
			Documento documento = ApiApiHelper.apiAllegatoToDocumento(body, as, env);
			
			if (body.getRuolo() == RuoloAllegatoAPI.ALLEGATO)
				as.addAllegato(documento);
			else if (body.getRuolo() == RuoloAllegatoAPI.SPECIFICASEMIFORMALE)
				as.addSpecificaSemiformale(documento);
			else
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Ruolo " + body.getRuolo() + " per allegato sconosciuto");
			
			ArchiviCore archiviCore = new ArchiviCore(env.stationCore);
			WrapperFormFile filewrap = new WrapperFormFile(documento.getFile(), documento.getByteContenuto());
			
			HttpRequestWrapper wrap = new HttpRequestWrapper(context.getServletRequest());
			wrap.overrideParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_RUOLO, documento.getRuolo());
			
			ArchiviHelper archiviHelper = new ArchiviHelper(env.stationCore, wrap, env.pd, wrap.getSession());
			
			boolean documentoUnivocoIndipendentementeTipo = true;
			if (archiviCore.existsDocumento(documento,ProprietariDocumento.accordoServizio,documentoUnivocoIndipendentementeTipo)){
				throw FaultCode.CONFLITTO.toException("Allegato con nome " + documento.getFile() + " già presente nella API");
			}
			
			if (! archiviHelper.accordiAllegatiCheckData(
					TipoOperazione.ADD,
					filewrap, 
					documento,
					ProprietariDocumento.accordoServizio, 
					env.protocolFactory
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}
			
			env.apcCore.performUpdateOperation(env.userLogin, false, as);
                                
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
     * Creazione di un&#x27;azione di una API
     *
     * Questa operazione consente di aggiungere una azione al servizio della API identificata dal nome e dalla versione
     *
     */
	@Override
    public void createAzione(ApiAzione body, String nome, Integer versione, String nomeServizio, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		//body.
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");
			
			ApiEnv env = new ApiEnv(profilo,soggetto,context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione,env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api con nome: " + nome + " e versione " + versione );
			
			PortType pt = as.getPortTypeList().stream()
					.filter( p -> nomeServizio.equals(p.getNome()))
					.findFirst()
					.orElse(null);
			
			if (pt == null)
				throw FaultCode.NOT_FOUND.toException("Nessun Servizio con nome: " + nomeServizio);
			
			Operation newOp = ApiApiHelper.apiAzioneToOperazione(body, pt);

			if (env.apcCore.existsAccordoServizioPorttypeOperation(newOp.getNome(), pt.getId()))
				throw FaultCode.CONFLITTO.toException("L'azione " + newOp.getNome() + " è già stata associata alla Api");

			pt.addAzione(newOp);
			
			if (! env.apcHelper.accordiPorttypeOperationCheckData(
					TipoOperazione.ADD,
					as.getId().toString(),
					newOp.getProfiloCollaborazione().toString(),
					newOp.getNome(),
					newOp.getProfAzione(),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getConfermaRicezione()),	
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getConsegnaInOrdine()),
					newOp.getScadenza() != null ? newOp.getScadenza() : "",
					"-", 				// Servizio Correlato
					"-",				// Azione Correlata
					ApiApiHelper.profiloCollaborazioneFromApiEnum.get(body.getProfiloCollaborazione()).toString(), 
					"0", 	//styleOp
					"",		//soapActionOp,
					"literal",	//useOp,
					null,	//opTypeOp, 
					""		//nsWSDLOp
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}
			
			
			AccordiServizioParteComuneUtilities.createPortTypeOperation(
					env.apcCore.isEnableAutoMappingWsdlIntoAccordo(),
					env.apcCore,
					env.apcHelper,
					as,
					pt,
					env.userLogin);

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
     * Creazione di una risorsa di una API
     *
     * Questa operazione consente di aggiungere una risorsa alla API identificata dal nome e dalla versione
     *
     */
	@Override
    public void createRisorsa(ApiRisorsa body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");
			
			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api con nome: " + nome + " e versione " + versione );
			
			Resource newRes = ApiApiHelper.apiRisorsaToRegistro(body, as);

			if (StringUtils.isEmpty(newRes.getNome()) && newRes.getMethod() == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il campo nome non è stato definito");
			
			if ( env.apcCore.existsAccordoServizioResource(newRes.get_value_method(), newRes.getPath(), as.getId()))
				throw FaultCode.CONFLITTO.toException("La risorsa " + newRes.getNome() + " è già stata associata alla Api");
					
			if (!env.apcHelper.accordiResourceCheckData(
					TipoOperazione.ADD,
					as.getId().toString(),
					body.getNome() != null ? body.getNome() : "",
					newRes.getNome(),
					newRes.getPath(),
					newRes.get_value_method(),
					newRes.getDescrizione(),
					env.apcCore.toMessageMessageType(newRes.getMessageType()),
					null,	// oldNomeRisorsa
					null,	// oldNomeRisorsaGenerato
					null,	// oldPath
					null,	// oldHttpMethod
					AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT,
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getConfermaRicezione()),	//confricaz,
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getConsegnaInOrdine()), 	//consordaz,
					""		//scadenzaaz
				))  {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}
			
			as.addResource(newRes);
			
			AccordiServizioParteComuneUtilities.createResource(
					env.apcCore.isEnableAutoMappingWsdlIntoAccordo(),
					env.apcCore,
					env.apcHelper,
					as,
					env.userLogin
				);

                                
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
     * Creazione di un servizio di una API
     *
     * Questa operazione consente di aggiungere un servizio alla API identificata dal nome e dalla versione
     *
     */
	@Override
    public void createServizio(ApiServizio body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");
			
			ApiEnv env = new ApiEnv(profilo,soggetto,context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api con nome: " + nome + " e versione " + versione );
			
			// Il Servizio assumiamo che di base RIDEFINISCA il profilo della api, dato che
			// abbiamo nei campi dei valori che dalla console sono configurabili sono in quel caso.
			PortType newPT = ApiApiHelper.apiServizioToRegistro(body,as,env);
			
			if (env.apcCore.existsAccordoServizioPorttype(newPT.getNome(), as.getId()))
				throw FaultCode.CONFLITTO.toException("Il servizio " + newPT.getNome() + " è già stato associato alla API");
			
			if (!env.apcHelper.accordiPorttypeCheckData(TipoOperazione.ADD,
					as.getId().toString(),
					newPT.getNome(),
					newPT.getDescrizione(),
					AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO,
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPT.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPT.getConfermaRicezione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPT.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPT.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPT.getConsegnaInOrdine()), 
					newPT.getScadenza()
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}
			as.addPortType(newPT);
		
			env.apcCore.performUpdateOperation(env.userLogin, false, as);

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
     * Elimina un&#x27;api
     *
     * Questa operazione consente di eliminare un API identificata dal nome e dalla versione
     *
     */
	@Override
    public void delete(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome,versione,env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Api non trovata");
			
			StringBuffer inUsoMessage = new StringBuffer(); 
			AccordiServizioParteComuneUtilities.deleteAccordoServizioParteComune(as, env.userLogin, env.apcCore, env.apcHelper, 
					inUsoMessage, "\n");
			if (inUsoMessage.length() > 0)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(inUsoMessage.toString());
        
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
     * Elimina un allegato di una API
     *
     * Questa operazione consente di eliminare un&#x27;allegato della API identificata dal nome e dalla versione
     *
     */
	@Override
    public void deleteAllegato(String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			
			final AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Api non trovata");
			
			Documento toDel = null;
			try {
				toDel = env.archiviCore.getDocumento(nomeAllegato, null, null, as.getId(), false, ProprietariDocumento.accordoServizio);
			} catch (Exception e) {	}

			if (toDel == null)
				throw FaultCode.NOT_FOUND.toException("Allegato con nome " + nomeAllegato + " non presente per il servizio applicativo scelto."); 
			
			AccordiServizioParteComuneUtilities.deleteAccordoServizioParteComuneAllegati(
					as, 
					env.userLogin, 
					env.apcCore, 
					env.apcHelper, 
					Arrays.asList(toDel.getId())
				);
			
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
     * Elimina un&#x27;azione del servizio di una API
     *
     * Questa operazione consente di eliminare un&#x27;azione del servizio della API identificata dal nome e dalla versione
     *
     */
	@Override
    public void deleteAzione(String nome, Integer versione, String nomeServizio, String nomeAzione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");   
			
			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Api non trovata");
			
			PortType pt = null;
			for (int i = 0; i < as.sizePortTypeList(); i++) {
				pt = as.getPortType(i);
				if (nomeServizio.equals(pt.getNome()))
					break;
			}
			
			if (pt == null)
				throw FaultCode.NOT_FOUND.toException("Nessun Servizio registrato sulla api con nome " + nomeServizio);
			
			final AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(env.apcCore);

			List<IDServizio> idServiziWithPortType = null;
			try{
				IDPortType idPT = new IDPortType();
				idPT.setNome(pt.getNome());
				idPT.setIdAccordo(IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as));
				idServiziWithPortType = apsCore.getIdServiziWithPortType(idPT);
			}catch(DriverRegistroServiziNotFound dNotF){}
			
			StringBuffer inUsoMessage = new StringBuffer();
                      
			AccordiServizioParteComuneUtilities.deleteAccordoServizioParteComuneOperations(
					as,
					env.userLogin,
					env.apcCore,
					env.apcHelper,
					inUsoMessage, 
					"\n", 
					pt, 
					idServiziWithPortType, 
					Arrays.asList(nomeAzione)
				);
			
			if (inUsoMessage.length() > 0)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(inUsoMessage.toString());
        
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
     * Elimina una risorsa di una API
     *
     * Questa operazione consente di eliminare una risorsa della API identificata dal nome e dalla versione
     *
     */
	@Override
    public void deleteRisorsa(String nome, Integer versione, String nomeRisorsa, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			final AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(env.apcCore);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Api non trovata");
            
			List<IDServizio> idServiziWithAccordo = null;
			try{
				idServiziWithAccordo = apsCore.getIdServiziWithAccordo(IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as),true);
			}catch(DriverRegistroServiziNotFound dNotF){}
			
			final StringBuffer inUsoMessage = new StringBuffer();
			AccordiServizioParteComuneUtilities.deleteAccordoServizioParteComuneRisorse(as, env.userLogin, env.apcCore, env.apcHelper, 
					inUsoMessage, "\n", idServiziWithAccordo, Arrays.asList(nomeRisorsa));
			
			if (inUsoMessage.length() > 0)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(inUsoMessage.toString());
       
        
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
     * Elimina un servizio di una API
     *
     * Questa operazione consente di eliminare un servizio della API identificata dal nome e dalla versione
     *
     */
	@Override
    public void deleteServizio(String nome, Integer versione, String nomeServizio, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if ( as == null )
				throw FaultCode.NOT_FOUND.toException("Api non trovata");
			
			final IDPortType idPT = new IDPortType();
			idPT.setIdAccordo(env.idAccordoFactory.getIDAccordoFromAccordo(as));
			
			final StringBuffer inUsoMessage = new StringBuffer();
			AccordiServizioParteComuneUtilities.deleteAccordoServizioParteComunePortTypes(as, env.userLogin, env.apcCore, env.apcHelper, 
					inUsoMessage, "\n", idPT, Arrays.asList(nomeServizio));
			
			if ( inUsoMessage.length() > 0 ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(inUsoMessage.toString());
			}
			
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
     * Restituisce l&#x27;allegato di una API
     *
     * Questa operazione consente di ottenere l&#x27;allegato di una API identificata dal nome e dalla versione
     *
     */
	@Override
    public byte[] downloadAllegato(String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if ( as == null )
				throw FaultCode.NOT_FOUND.toException("Api non trovata");
			
            Documento doc = as.getAllegatoList().stream()
            	.filter( a -> nomeAllegato.equals(a.getFile()))
             	.findFirst()
            	.orElse(null);
            
            if (doc == null)
            	throw FaultCode.NOT_FOUND.toException("Nessun allegato con nome " + nomeAllegato + " registrato per la Api specificata");
            
            doc = env.archiviCore.getDocumento(
            		doc.getFile(), 
            		doc.getTipo(),
            		doc.getRuolo(), 
            		doc.getIdProprietarioDocumento(), 
            		true, 
            		ProprietariDocumento.accordoServizio
            	);
            		
			context.getLogger().info("Invocazione completata con successo");
			
			return doc.getByteContenuto();     
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
     * Restituisce l&#x27;interfaccia di una API
     *
     * Questa operazione consente di ottenere l&#x27;interfaccia di una API identificata dal nome e dalla versione
     *
     */
	@Override
    public byte[] downloadInterfaccia(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			//ApiInterfacciaView a = new ApiInterfacciaView();
			
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if ( as == null )
				throw FaultCode.NOT_FOUND.toException("Api non trovata");
		
			context.getLogger().info("Invocazione completata con successo");
			
			switch (as.getServiceBinding()) {
			case REST:
				return as.getByteWsdlConcettuale();
			case SOAP:
				return as.getByteWsdlLogicoErogatore();
			}
		

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
     * Ricerca api
     *
     * Elenca le API registrate
     *
     */
	@Override
    public ListaApi findAll(ProfiloEnum profilo, String soggetto, String q, Integer limit, Integer offset, TipoApiEnum tipoApi) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			
			final int idLista = Liste.ACCORDI;
			final Search ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista);
			
			if ( profilo != null )	// TODO: Forse non serve ne profilo ne soggetto.
				ricerca.addFilter(idLista, Filtri.FILTRO_PROTOCOLLO, Helper.tipoProtocolloFromProfilo.get(profilo));
			if ( tipoApi != null )
				ricerca.addFilter(idLista, Filtri.FILTRO_SERVICE_BINDING, ApiApiHelper.serviceBindingFromTipo.get(tipoApi).toString());
			if ( !StringUtils.isEmpty(soggetto))
				ricerca.addFilter(idLista, Filtri.FILTRO_SOGGETTO, soggetto);
			
			final String tipoAccordo = "apc"; // Dal debug.
			final List<AccordoServizioParteComune> lista = AccordiServizioParteComuneUtilities.accordiList(env.apcCore, env.userLogin, ricerca, tipoAccordo);
			final ListaApi ret = Helper.costruisciListaPaginata(
					context.getServletRequest().getRequestURI(),
					offset, 
					limit, 
					ricerca.getNumEntries(idLista), 
					ListaApi.class
				); 
			
			lista.forEach( as -> 
				ret.addItemsItem( ApiApiHelper.apiToItem(ApiApiHelper.accordoSpcRegistroToApi(as, env.soggettiCore), as,  env) )
				);
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
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
     * Elenco allegati di una API
     *
     * Questa operazione consente di ottenere gli allegati di una API identificata dal nome e dalla versione
     *
     */
	@Override
    public ListaApiAllegati findAllAllegati(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String q, Integer limit, Integer offset) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if ( as == null )
				throw FaultCode.NOT_FOUND.toException("Api non trovata");
			
			int idLista = Liste.ACCORDI_ALLEGATI;
			Search ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista);
			
			if ( profilo != null )	//TODO: Forse il profilo non serve, già l'accordo ce l'ha collegato.
				ricerca.addFilter(idLista, Filtri.FILTRO_PROTOCOLLO, Helper.tipoProtocolloFromProfilo.get(profilo));
			
			if ( !StringUtils.isEmpty(soggetto))
				ricerca.addFilter(idLista, Filtri.FILTRO_SOGGETTO, soggetto);
			
			List<Documento> docsRegistro = env.apcCore.accordiAllegatiList(as.getId().intValue(), ricerca);
			
			if (docsRegistro.size() == 0)
				throw FaultCode.NOT_FOUND.toException("Nessun allegato dell'Api specificata corrisponde ai criteri di ricerca");
			
			final ListaApiAllegati ret = Helper.costruisciListaPaginata(
					context.getServletRequest().getRequestURI(),
					offset, 
					limit, 
					ricerca.getNumEntries(idLista), 
					ListaApiAllegati.class
				); 
			docsRegistro.forEach( d -> ret.addItemsItem(ApiApiHelper.documentoToApiAllegatoItem(d)));
			        
			context.getLogger().info("Invocazione completata con successo");
			
			return ret;
     
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
     * Elenco servizi di una API
     *
     * Questa operazione consente di ottenere le azioni di un servizio della API identificata dal nome e dalla versione
     *
     */
	@Override
    public ListaApiAzioni findAllAzioni(String nome, Integer versione, String nomeServizio, ProfiloEnum profilo, String soggetto, String q, Integer limit, Integer offset) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			
			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if ( as == null )
				throw FaultCode.NOT_FOUND.toException("Api non trovata");
			
			int idLista = Liste.ACCORDI_PORTTYPE_AZIONI;
			Search ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista);
			
			PortType pt = as.getPortTypeList().stream()
					.filter( p -> nomeServizio.equals(p.getNome()))
					.findFirst()
					.orElse(null);
			
			if (pt == null)
				throw FaultCode.NOT_FOUND.toException("Non è stato trovato alcun servizio con nome " + nomeServizio + " legato alla Api");
			
			List<Operation> azioniServizio = env.apcCore.accordiPorttypeOperationList(pt.getId().intValue(), ricerca);
			
			if (azioniServizio.size() == 0)
				throw FaultCode.NOT_FOUND.toException("Nessua azione dell'Api specificata corrisponde ai criteri di ricerca");
			
			final ListaApiAzioni ret = Helper.costruisciListaPaginata(
					context.getServletRequest().getRequestURI(),
					offset, 
					limit, 
					ricerca.getNumEntries(idLista), 
					ListaApiAzioni.class
				); 
		
			azioniServizio.forEach( op -> ret.addItemsItem(ApiApiHelper.operazioneToApiAzione(op)));
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
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
     * Elenco risorse di una API
     *
     * Questa operazione consente di ottenere le risorse di una API identificata dal nome e dalla versione
     *
     */
	@Override
    public ListaApiRisorse findAllRisorse(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String q, Integer limit, Integer offset, HttpMethodEnum httpMethod) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if ( as == null )
				throw FaultCode.NOT_FOUND.toException("Api non trovata");                        
        
			int idLista = Liste.ACCORDI_API_RESOURCES;
			
			Search ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista);
			
			if (httpMethod != null)
				ricerca.addFilter(idLista, Filtri.FILTRO_HTTP_METHOD, httpMethod.toString());
			
			final ListaApiRisorse ret = Helper.costruisciListaPaginata(
					context.getServletRequest().getRequestURI(),
					offset, 
					limit, 
					ricerca.getNumEntries(idLista), 
					ListaApiRisorse.class
				); 
			List<Resource> risorse = env.apcCore.accordiResourceList(as.getId().intValue(), ricerca);
			
			if (risorse.size() == 0)
				throw FaultCode.NOT_FOUND.toException("Nessun allegato dell'Api specificata corrisponde ai criteri di ricerca");
			
			risorse.forEach( r -> ret.addItemsItem(ApiApiHelper.risorsaRegistroToApi(r)));
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
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
     * Elenco servizi di una API
     *
     * Questa operazione consente di ottenere i servizi di una API identificata dal nome e dalla versione
     *
     */
	@Override
    public ListaApiServizi findAllServizi(String nome, Integer versione, ProfiloEnum profilo, String soggetto, String q, Integer limit, Integer offset) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
		
			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if ( as == null )
				throw FaultCode.NOT_FOUND.toException("Api non trovata");                        
        
			int idLista = Liste.ACCORDI_PORTTYPE;
			
			Search ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista);
			
			final ListaApiServizi ret = Helper.costruisciListaPaginata(
					context.getServletRequest().getRequestURI(),
					offset, 
					limit, 
					ricerca.getNumEntries(idLista), 
					ListaApiServizi.class
				); 
			List<PortType> servizi = env.apcCore.accordiPorttypeList(as.getId().intValue(), ricerca);
			
			if (servizi.size() == 0)
				throw FaultCode.NOT_FOUND.toException("Nessun allegato dell'Api specificata corrisponde ai criteri di ricerca");
			
			servizi.forEach( s -> ret.addItemsItem(ApiApiHelper.servizioRegistroToApi(s)));
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
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
     * Restituisce i dettagli di una API
     *
     * Questa operazione consente di ottenere i dettagli di una API identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiViewItem get(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");    
			
			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);
			
			ApiItem item = ApiApiHelper.apiToItem(ApiApiHelper.accordoSpcRegistroToApi(as, env.soggettiCore), as, env);
			ApiViewItem ret = new ApiViewItem();
			
			ret.setDescrizione(item.getDescrizione());
			ret.setFormato(item.getFormato());
			ret.setNome(item.getNome());
			ret.setProfilo(item.getProfilo());
			ret.setReferente(item.getSoggetto());
			ret.setSoggetto(item.getSoggetto());
			ret.setTipo(item.getTipo());
			ret.setVersione(item.getVersione());
		
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
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
     * Restituisce il dettaglio di un allegato di una API
     *
     * Questa operazione consente di ottenere il dettaglio di un allegato della API identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiAllegato getAllegato(String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
		
			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);
			
            ApiAllegato ret = as.getAllegatoList().stream()
            	.filter( a -> nomeAllegato.equals(a.getFile()))
            	.map( a -> {
					try {
						return ApiApiHelper.documentoToApiAllegato(
								env.archiviCore.getDocumento(a.getId(), true)
							);
					} catch (DriverRegistroServiziException | DriverRegistroServiziNotFound e) {
						return null;
					}
				})
             	.findFirst()
            	.orElse(null);
            
            if (ret == null)
            	throw FaultCode.NOT_FOUND.toException("Nessun allegato con nome " + nomeAllegato + " registrato per la Api specificata");
                 
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
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
     * Restituisce il dettaglio di un&#x27;azione di un  servizio della API
     *
     * Questa operazione consente di ottenere il dettaglio di un&#x27;azione nel servizio della API identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiAzione getAzione(String nome, Integer versione, String nomeServizio, String nomeAzione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);
			
			PortType pt = as.getPortTypeList().stream()
					.filter( p -> nomeServizio.equals(p.getNome()))
					.findFirst()
					.orElse(null);
			
			if (pt == null)
				throw FaultCode.NOT_FOUND.toException("Nessun Servizio con nome: " + nomeServizio);
			
			ApiAzione ret = pt.getAzioneList().stream()
					.filter( a -> nomeAzione.equals(a.getNome()))
					.map( a -> ApiApiHelper.operazioneToApiAzione(a))
					.findFirst()
					.orElse(null);
			
			  if (ret == null)
	            	throw FaultCode.NOT_FOUND.toException("Nessuna azione con nome " + nomeAzione + " registrato per il servizio " + nomeServizio);
                 
			context.getLogger().info("Invocazione completata con successo");
			return ret;
 
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
     * Restituisce la descrizione di una API
     *
     * Questa operazione consente di ottenere la descrizione di una API identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiDescrizione getDescrizione(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);
            
			ApiDescrizione ret = new ApiDescrizione();
			ret.setDescrizione(as.getDescrizione());
        
			context.getLogger().info("Invocazione completata con successo");
        	return ret;
        	
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
     * Restituisce le informazioni generali di una API
     *
     * Questa operazione consente di ottenere le informazioni generali di una API identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiInformazioniGeneraliView getInformazioniGenerali(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);
            
			ApiInformazioniGeneraliView ret = new ApiInformazioniGeneraliView();
			
			ret.setNome(as.getNome());
			ret.setProfilo(env.profilo);
			ret.setVersione(as.getVersione());
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
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
     * Restituisce l&#x27;interfaccia di una API
     *
     * Questa operazione consente di ottenere l&#x27;interfaccia di una API identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiInterfacciaView getInterfaccia(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                                    
			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);
			
			ApiInterfacciaView ret = new ApiInterfacciaView();
			
			ret.setProfilo(env.profilo);
			ret.setTipo( as.getServiceBinding() == ServiceBinding.REST 
					? TipoApiEnum.REST
					: TipoApiEnum.SOAP
				);
					
			
			switch (ret.getTipo()) {
			case REST:
				ret.setFormato(ApiApiHelper.formatoRestFromSpecifica.get(as.getFormatoSpecifica()));
				ret.setInterfaccia(as.getByteWsdlConcettuale());
				break;
			case SOAP:
				ret.setFormato(ApiApiHelper.formatoSoapFromSpecifica.get(as.getFormatoSpecifica()));
				ret.setInterfaccia(as.getByteWsdlLogicoErogatore());
				break;
			default:
				throw FaultCode.ERRORE_INTERNO.toException("Tipologia interfaccia registro sconosciuta: " + as.getServiceBinding().name());
			}
	
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
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
     * Restituisce il nome del soggetto referente dell&#x27;api
     *
     * Questa operazione consente di ottenere il nome del soggetto referente dell&#x27;API identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiReferenteView getReferente(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
            
			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);
			
			ApiReferenteView ret = new ApiReferenteView();
			ret.setProfilo(env.profilo);
			ret.setReferente(as.getSoggettoReferente().getNome());
        
			context.getLogger().info("Invocazione completata con successo");
			return ret;
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
     * Restituisce il dettaglio di una risorsa di una API
     *
     * Questa operazione consente di ottenere il dettaglio di una risorsa della API identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiRisorsa getRisorsa(String nome, Integer versione, String nomeRisorsa, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);
			
			ApiRisorsa ret = as.getResourceList().stream()
				.filter( r -> nomeRisorsa.equals(r.getNome()))
				.map( r -> ApiApiHelper.risorsaRegistroToApi(r))
				.findFirst()
				.orElse(null);
			
			if (ret == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna risorsa con nome " + nomeRisorsa + " è registrata per la Api indicata");

			context.getLogger().info("Invocazione completata con successo");
			return ret;
 
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
     * Restituisce il dettaglio di un servizio di una API
     *
     * Questa operazione consente di ottenere il dettaglio di un servizio della API identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiServizio getServizio(String nome, Integer versione, String nomeServizio, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);
			
			ApiServizio ret = as.getPortTypeList().stream()
					.filter( p -> nomeServizio.equals(p.getNome()))
					.map( p -> ApiApiHelper.servizioRegistroToApi(p))
					.findFirst()
					.orElse(null);
			
			if (ret == null)
				throw FaultCode.NOT_FOUND.toException("Nessun Servizio con nome: " + nomeServizio);
	
			context.getLogger().info("Invocazione completata con successo");
			return ret;
			     
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
     * Modifica i dati di un allegato di una API
     *
     * Questa operazione consente di aggiornare i dettagli di un allegato della API identificata dal nome e dalla versione
     *
     */
	@Override
    public void updateAllegato(ApiAllegato body, String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     
		
			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
		
			if ( body == null ) 
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");
				
			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);
		
			Documento oldDoc = null;
			
			try {
				oldDoc = env.archiviCore.getDocumento(nomeAllegato, null, null, as.getId(), false, ProprietariDocumento.accordoServizio);
			} catch (Exception e) {	}
			
			if (oldDoc == null)
				throw FaultCode.NOT_FOUND.toException("Nessun allegato con di nome " + nomeAllegato + " trovato per la API scelta.");
			
			final Documento newDoc = ApiApiHelper.apiAllegatoToDocumento(body, as, env);
			newDoc.setId(oldDoc.getId());
			
			if (!newDoc.getRuolo().equals(oldDoc.getRuolo()))
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non puoi modificare il ruolo di un allegato");
		
			WrapperFormFile filewrap = new WrapperFormFile(newDoc.getFile(), newDoc.getByteContenuto());
			HttpRequestWrapper wrap = new HttpRequestWrapper(context.getServletRequest());
			wrap.overrideParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_RUOLO, newDoc.getRuolo());
		
			ArchiviHelper archiviHelper = new ArchiviHelper(env.stationCore, wrap, env.pd, wrap.getSession());	
			
			if (! archiviHelper.accordiAllegatiCheckData(
					TipoOperazione.CHANGE,
					filewrap, 
					newDoc,
					ProprietariDocumento.accordoServizio, 
					env.protocolFactory
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}

			AccordiServizioParteComuneUtilities.updateAccordoServizioParteComuneAllegati(as, oldDoc, newDoc);
					
			env.apcCore.performUpdateOperation(env.userLogin, false, as);

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
     * Modifica i dati di un&#x27;azione nel servizio di una API
     *
     * Questa operazione consente di aggiornare i dettagli di un&#x27;azione della API identificata dal nome e dalla versione
     *
     */
	@Override
    public void updateAzione(ApiAzione body, String nome, Integer versione, String nomeServizio, String nomeAzione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			if ( body == null ) 
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");
				
			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);
		
			final PortType pt = as.getPortTypeList().stream()
					.filter( p -> nomeServizio.equals(p.getNome()))
					.findFirst()
					.orElse(null);
		
			if (pt == null)
				throw FaultCode.NOT_FOUND.toException("Nessun Servizio con nome: " + nomeServizio);
			
			
			final Operation oldOp = Helper.findAndRemoveFirst(pt.getAzioneList(), ( op -> nomeAzione.equals(op.getNome())) );
			
			if (oldOp == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Azione con nome: " + nomeAzione + " associata al servizio " + nomeServizio);
			
			if (!nomeAzione.equals(oldOp.getNome())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non puoi modificare il nome dell'azione");
			}
			
			ApiApiHelper.updateOperation(body, pt, oldOp);
			final Operation newOp = oldOp;
			
			if (! env.apcHelper.accordiPorttypeOperationCheckData(
					TipoOperazione.CHANGE,
					as.getId().toString(),
					newOp.getProfiloCollaborazione().toString(),
					newOp.getNome(),
					newOp.getProfAzione(),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getConfermaRicezione()),	
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getConsegnaInOrdine()),
					newOp.getScadenza() != null ? newOp.getScadenza() : "", 				// Scadenza operazione
					"-", 				// Servizio Correlato
					"-",				// Azione Correlata
					ApiApiHelper.profiloCollaborazioneFromApiEnum.get(body.getProfiloCollaborazione()).toString(), 
					"0", 	//styleOp
					"",		//soapActionOp,
					"literal",	//useOp,
					null,	//opTypeOp, 
					""		//nsWSDLOp
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}
			
			
			// Dopo aver fatto i controlli sui dati aggiungo la nuova azione
			pt.addAzione(newOp);
			
			AccordiServizioParteComuneUtilities.createPortTypeOperation(
					env.apcCore.isEnableAutoMappingWsdlIntoAccordo(),
					env.apcCore,
					env.apcHelper,
					as,
					pt,
					env.userLogin);	
	        
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
     * Consente di modificare la descrizione di una API
     *
     * Questa operazione consente di aggiornare la descrizione di una API identificata dal nome e dalla versione
     *
     */
	@Override
    public void updateDescrizione(ApiDescrizione body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			

			if ( body == null ) 
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");
				
			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			final IDAccordo oldIdAccordo = env.idAccordoFactory.getIDAccordoFromAccordo(as);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);
			
			as.setDescrizione(body.getDescrizione());
			
			boolean validazioneDocumenti = ServerProperties.getInstance().isValidazioneDocumenti();
			
			BinaryParameter wsdlserv = new BinaryParameter();
			wsdlserv.setValue(as.getByteWsdlLogicoErogatore());
		
			BinaryParameter wsdlconc = new BinaryParameter();
			wsdlconc.setValue(as.getByteWsdlConcettuale());
			
			BinaryParameter wsdldef = new BinaryParameter();
			wsdldef.setValue(as.getByteWsdlDefinitorio());
			
			/*boolean visibilitaAccordoCooperazione=false;
			if("-".equals(this.accordoCooperazioneId)==false && "".equals(this.accordoCooperazioneId)==false  && this.accordoCooperazioneId!=null){
				AccordoCooperazione ac = acCore.getAccordoCooperazione(Long.parseLong(this.accordoCooperazioneId));
				visibilitaAccordoCooperazione=ac.getPrivato()!=null && ac.getPrivato();
			}*/

			if (!env.apcHelper.accordiCheckData(
					TipoOperazione.CHANGE,
					as.getNome(),
					as.getDescrizione(),
					as.getProfiloCollaborazione().toString(),
					Helper.toBinaryParameter(as.getByteWsdlDefinitorio()),
					Helper.toBinaryParameter(as.getByteWsdlConcettuale()),
					Helper.toBinaryParameter(as.getByteWsdlLogicoErogatore()),
					Helper.toBinaryParameter(as.getByteWsdlLogicoFruitore()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine()),
					as.getScadenza() == null ? "" : as.getScadenza(),
					as.getId().toString(),
					as.getSoggettoReferente().getNome(),
					as.getVersione().toString(),
					null,	// AccordoCooperazioneID 
					false,  // privato false, null, 
					false,	// visibilitaAccordoCooperazione
					oldIdAccordo,	//idAccordoOld
					null,	//wsblconc
					null, 	//wsblserv
					null,	//wsblservrorr
					validazioneDocumenti,
					env.tipo_protocollo,
					null,	//backToStato
					env.apcCore.toMessageServiceBinding(as.getServiceBinding()),
					null,	//MessageType 
					ApiApiHelper.interfaceTypeFromFormatoSpecifica.get(as.getFormatoSpecifica()),
					env.gestisciSoggettoReferente)) {
				
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}
                        
      
			// Il profilo di collaborazione di base è Sincrono. 
			// Quindi Nella AccordiServizioPArteComuneChange così come nella Add possiamo assumerlo costante.  
			// Sono i Servizi e le Azioni ad avere un profilo di collaborazione
			
			as.setOldIDAccordoForUpdate(oldIdAccordo);
			List<Object> operazioniList = Arrays.asList(as);			
			
 			// Questa roba non serve qui perchè la updateDescrizione non cambia il nome e quindi nemmeno l'ID.
 			IDAccordo idNEW = env.idAccordoFactory.getIDAccordoFromAccordo(as);
			if(idNEW.equals(oldIdAccordo)==false){
				AccordiServizioParteComuneUtilities.findOggettiDaAggiornare(oldIdAccordo, as, env.apcCore, operazioniList);
			}

			env.apcCore.performUpdateOperation(env.userLogin, false, operazioniList.toArray());
        
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
     * Consente di modificare le informazioni generali di una API
     *
     * Questa operazione consente di aggiornare le informazioni generali di una API identificata dal nome e dalla versione
     *
     */
	@Override
    public void updateInformazioniGenerali(ApiInformazioniGenerali body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     
		
			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			if ( body == null ) 
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");
				
			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);
			
			final IDAccordo oldIdAccordo = env.idAccordoFactory.getIDAccordoFromAccordo(as);
			
			as.setNome(body.getNome());
			as.setVersione(body.getVersione());
			
			boolean validazioneDocumenti = ServerProperties.getInstance().isValidazioneDocumenti();
		
			
			/*boolean visibilitaAccordoCooperazione=false;
			if("-".equals(this.accordoCooperazioneId)==false && "".equals(this.accordoCooperazioneId)==false  && this.accordoCooperazioneId!=null){
				AccordoCooperazione ac = acCore.getAccordoCooperazione(Long.parseLong(this.accordoCooperazioneId));
				visibilitaAccordoCooperazione=ac.getPrivato()!=null && ac.getPrivato();
			}*/

			if (!env.apcHelper.accordiCheckData(
					TipoOperazione.CHANGE,
					as.getNome(),
					as.getDescrizione(),
					as.getProfiloCollaborazione().toString(),
					Helper.toBinaryParameter(as.getByteWsdlDefinitorio()),
					Helper.toBinaryParameter(as.getByteWsdlConcettuale()),
					Helper.toBinaryParameter(as.getByteWsdlLogicoErogatore()),
					Helper.toBinaryParameter(as.getByteWsdlLogicoFruitore()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine()),
					as.getScadenza() == null ? "" : as.getScadenza(),
					as.getId().toString(),
					as.getSoggettoReferente().getNome(),
					as.getVersione().toString(),
					null,	// AccordoCooperazioneID 
					false,  // privato false, null, 
					false,	// visibilitaAccordoCooperazione
					oldIdAccordo,	//idAccordoOld
					null,	//wsblconc
					null, 	//wsblserv
					null,	//wsblservrorr
					validazioneDocumenti,
					env.tipo_protocollo,
					null,	//backToStato
					env.apcCore.toMessageServiceBinding(as.getServiceBinding()),
					null,	//MessageType 
					ApiApiHelper.interfaceTypeFromFormatoSpecifica.get(as.getFormatoSpecifica()),
					env.gestisciSoggettoReferente)) {
				
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}
                   			
			as.setOldIDAccordoForUpdate(oldIdAccordo);
			List<Object> operazioniList = Arrays.asList(as);			
			
 			IDAccordo idNEW = env.idAccordoFactory.getIDAccordoFromAccordo(as);
			if(idNEW.equals(oldIdAccordo)==false){
				AccordiServizioParteComuneUtilities.findOggettiDaAggiornare(oldIdAccordo, as, env.apcCore, operazioniList);
			}

			env.apcCore.performUpdateOperation(env.userLogin, false, operazioniList.toArray());			
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
     * Consente di modificare l&#x27;interfaccia di una API
     *
     * Questa operazione consente di aggiornare l&#x27;interfaccia di una API identificata dal nome e dalla versione
     *
     */
	@Override
    public void updateInterfaccia(ApiInterfaccia body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);
			
			final String tipoWsdl = as.getServiceBinding() == ServiceBinding.REST 
					? AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE
					: AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE;
			
			final String wsdl = new String(body.getInterfaccia());
					
			final boolean validazioneDocumenti = ServerProperties.getInstance().isValidazioneDocumenti();
						
			if (! env.apcHelper.accordiWSDLCheckData(env.pd, tipoWsdl, wsdl, as, validazioneDocumenti, env.tipo_protocollo) ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}
			
			final boolean facilityUnicoWSDL_interfacciaStandard = as.getServiceBinding() == ServiceBinding.SOAP; 

			ServerProperties properties = ServerProperties.getInstance();
			
			AccordiServizioParteComuneUtilities.updateInterfacciaAccordoServizioParteComune(
					tipoWsdl,
					wsdl,
					as,
					properties.isEnabledAutoMapping(),
					properties.isValidazioneDocumenti(),
					properties.isEnabledAutoMappingEstraiXsdSchemiFromWsdlTypes(),
					facilityUnicoWSDL_interfacciaStandard,
					env.tipo_protocollo, 
					env.apcCore
				);

			// effettuo le operazioni
			env.apcCore.performUpdateOperation(env.userLogin, false, as);

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
     * Modifica i dati di una risorsa di una API
     *
     * Questa operazione consente di aggiornare i dettagli di una risorsa della API identificata dal nome e dalla versione
     *
     */
	@Override
    public void updateRisorsa(ApiRisorsa body, String nome, Integer versione, String nomeRisorsa, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			if ( body == null ) 
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");
				
			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);
			
			Resource oldResource = Helper.findAndRemoveFirst(as.getResourceList(), r -> nomeRisorsa.equals(r.getNome()));
			
			if (oldResource == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna risorsa trovata con nome " + nomeRisorsa);
			
			final String  oldPath = oldResource.getPath();
			final String  oldHttpMethod = oldResource.get_value_method();
			final String  oldNomeRisorsaGenerato = oldHttpMethod!=null 
					? APIUtils.normalizeResourceName(HttpMethod.toEnumConstant(oldHttpMethod), oldPath)
					: null;
			
			ApiApiHelper.updateRisorsa(body, oldResource);
			final Resource newRes = oldResource;
						
			if (!env.apcHelper.accordiResourceCheckData(
					TipoOperazione.CHANGE,
					as.getId().toString(),
					body.getNome() != null ? body.getNome() : "",
					newRes.getNome(),
					newRes.getPath(),
					newRes.get_value_method(),
					newRes.getDescrizione(),
					env.apcCore.toMessageMessageType(newRes.getMessageType()),
					nomeRisorsa,
					oldNomeRisorsaGenerato,
					oldPath,
					oldHttpMethod,
					AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO,
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getConfermaRicezione()),	//confricaz,
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getConsegnaInOrdine()), 	//consordaz,
					newRes.getScadenza() != null ? newRes.getScadenza() : ""
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}
			
			as.addResource(newRes);
			
			AccordiServizioParteComuneUtilities.createResource(
					env.apcCore.isEnableAutoMappingWsdlIntoAccordo(),
					env.apcCore,
					env.apcHelper,
					as,
					env.userLogin
				);
	        
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
     * Modifica i dati di un servizio di una API
     *
     * Questa operazione consente di aggiornare i dettagli di un servizio della API identificata dal nome e dalla versione
     *
     */
	@Override
    public void updateServizio(ApiServizio body, String nome, Integer versione, String nomeServizio, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");    
		
			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordo(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);
			
			
			final PortType oldPt = Helper.findAndRemoveFirst(as.getPortTypeList(), ( p -> nomeServizio.equals(p.getNome()) ));
					
		
			if (oldPt == null)
				throw FaultCode.NOT_FOUND.toException("Nessun Servizio registrato con nome: " + nomeServizio);
			
			ApiApiHelper.updatePortType(body, oldPt, env);
			final PortType newPt = oldPt;
			
			if (!env.apcHelper.accordiPorttypeCheckData(
					TipoOperazione.CHANGE,
					as.getId().toString(),
					newPt.getNome(),
					newPt.getDescrizione(),
					AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO,
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPt.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPt.getConfermaRicezione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPt.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPt.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPt.getConsegnaInOrdine()),
					newPt.getScadenza() != null ? newPt.getScadenza() : ""
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}
			
			as.addPortType(newPt);

			env.apcCore.performUpdateOperation(env.userLogin, false, as);

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

