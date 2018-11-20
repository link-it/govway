package org.openspcoop2.core.config.rs.server.api.impl;

//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.config.rs.server.api.SoggettiApi;
import org.openspcoop2.core.config.rs.server.model.DominioEnum;
import org.openspcoop2.core.config.rs.server.model.ListaSoggetti;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.core.config.rs.server.model.Soggetto;
//import org.openspcoop2.core.constants.CostantiDB;
//import org.openspcoop2.core.id.IDSoggetto;
//import org.openspcoop2.core.registry.Connettore;
//import org.openspcoop2.core.registry.CredenzialiSoggetto;
//import org.openspcoop2.core.registry.constants.CredenzialeTipo;
//import org.openspcoop2.core.registry.constants.PddTipologia;
//import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
//import org.openspcoop2.protocol.sdk.IProtocolFactory;
//import org.openspcoop2.protocol.sdk.ProtocolException;
//import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
//import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
//import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
//import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
//import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
//import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
//import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
//import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.jaxrs.fault.FaultCode;
import org.openspcoop2.utils.jaxrs.impl.AuthorizationConfig;
import org.openspcoop2.utils.jaxrs.impl.AuthorizationManager;
import org.openspcoop2.utils.jaxrs.impl.BaseImpl;
import org.openspcoop2.utils.jaxrs.impl.ServiceContext;
//import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
//import org.openspcoop2.web.ctrlstat.core.Search;
//import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
//import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
//import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
//import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
//import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
//import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
//import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
//import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiHelper;
//import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCore;
//import org.openspcoop2.web.lib.mvc.PageData;
//import org.openspcoop2.web.lib.mvc.TipoOperazione;
//import org.openspcoop2.web.lib.users.dao.User;
//import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesUtilities;

/**
 * GovWay Config API
 *
 * <p>Servizi per la configurazione di GovWay
 *
 */
public class SoggettiApiServiceImpl extends BaseImpl implements SoggettiApi {


	public SoggettiApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(SoggettiApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		// TODO: Implement ...
		throw new Exception("NotImplemented");
	}

    /**
     * Creazione di un soggetto
     *
     * Questa operazione consente di creare un soggetto
     *
     */
	@Override
    public void create(Object body, ProfiloEnum profilo) {
		ServiceContext context = this.getContext();
		try {
			
			context.getLogger().info("Invocazione in corso ...");     
			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			/* TODO: commentato per warning 
			
			// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
			ConsoleOperationType opType = ConsoleOperationType.ADD;
			TipoOperazione tipoOp = TipoOperazione.ADD;
			
			Soggetto soggetto = null;
			
			RuoliWrapperServlet wrap = new RuoliWrapperServlet(context.getServletRequest());			
			
			wrap.overrideParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME, soggetto.getNome());
			//wrap.overrideParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO, soggetto.get);

			//Questa è la modalità\interoperabilità: (ApiGateway, SpCoop ecc..)
			//this.protocollo = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO);
			wrap.overrideParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO, soggetto.getDominio());

			//Questo dovrò ottenerlo a seconda della modalità. (gw,spc,sdi?)
			//this.tipoprov = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO);

			
			// Erogatore | Fruitore | FruitoreErogatore
			//this.tipologia = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPOLOGIA);

			
			// Devi fare il mapping fra modalità e protocollo.
			// this.protocollo = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO);
			

			//Vuota con  Fatturazione elettronica e ApiGateway
			//this.portadom = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_PORTA);
			
			//Descrizione
			//this.descr = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DESCRIZIONE);

			//= null
			//this.pdd = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PDD);


			//= "no"
			//String is_router = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_ROUTER);
			
			
			//Non ce l'ho nel soggetto della API
			//this.codiceIpa = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_IPA);

			//soggetto.
			
			//SPCoop => "eGov1.1-lineeGuida1.1"

			//this.versioneProtocollo = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_VERSIONE_PROTOCOLLO);

			//false
			//this.isRouter = ServletUtils.isCheckBoxEnabled(is_router);

			//soggetto.get
			
			// Domanda. Perchè nel caso di servizio registri locale (cosa vuol dire?) parliamo 
			// 
			//Dopo i vari set c'è il codice che imposta la porta di dominio
			

			// Environment:
			String tipo_protocollo = profilo.toString();//ApplicativiApiHelper.getProtocolloOrDefault(modalita);
			String userLogin = context.getAuthentication().getName();
			SoggettiCore soggettiCore = new SoggettiCore();
			PddCore pddCore = new PddCore(soggettiCore);
			UtentiCore utentiCore = new UtentiCore(soggettiCore);
			PageData pd = new PageData();
			SoggettiHelper soggettiHelper = new SoggettiHelper(wrap, pd, wrap.getSession());
			boolean singlePdD = true; // TODO: Check tramite debug.
			
			
			// Sono sempre un registro servizio globale, quindi non filtro per login
			// TODO: Attento, manca l'elemento di comodo.
			List<PdDControlStation> lista = pddCore.pddList(null, new Search(true));
			String[] pddList = (String[]) lista.stream().map( pddc -> pddc.getNome()).collect(Collectors.toList()).toArray();
			
			String nomePddGestioneLocale = null;
			// Cerco una porta di dominio operativa da assegnare al soggetto.
			if (singlePdD) {
				nomePddGestioneLocale = lista.stream()
					.filter( pddc -> pddc.getTipo().equals(PddTipologia.OPERATIVO.toString()))
					.findFirst()
					.map( p -> p.getNome()).orElse(null);
			}

			String dominio = soggetto.getDominio().toString();	// TODO: Populate.
			String pdd = null;		// La porta di dominio da utilizzare per il soggetto corrente.
			
			// Gestione pdd. Se del soggetto non sono gestite le pdd allora ne assegniamo una operativa.
			// TODO TOASK, è giusta questa cosa? Se avessimo un soggetto esterno per la quale non è abilitata la gestione delle pdd?
			if(soggettiCore.isGestionePddAbilitata(soggettiHelper)==false) {
				
				if(nomePddGestioneLocale==null) {
					throw new Exception("Non è stata rilevata una pdd di tipologia 'operativo'");
				}
				
				if(SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE.equals(dominio)) {
					pdd = nomePddGestioneLocale;
				}
				else {
					pdd = null;
				}
			}
			
			//Carico la lista dei tipi di soggetti gestiti dal protocollo
			//List<String> tipiSoggetti = soggettiCore.getTipiSoggettiGestitiProtocollo(tipo_protocollo);

			String tipoprov = null;	// TODO: Questo dovrebbe essere il tipo del soggetto derivato dalla modalità
			if(tipoprov==null){
				tipoprov = soggettiCore.getTipoSoggettoDefaultProtocollo(tipo_protocollo);
			}
			
			
			// Controllo se ho modificato il protocollo, ricalcolo il default della versione del protocollo
			// Questo devo capirlo prima dal debug. Non penso serva nell'api visto il postBack.
//			if(postBackElementName != null ){
//				if(postBackElementName.equalsIgnoreCase(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO)){
//					this.versioneProtocollo = null;
//					// cancello file temporanei
//					soggettiHelper.deleteProtocolPropertiesBinaryParameters();
//				}  
//			}

			
			String versioneProtocollo = null;
			if(versioneProtocollo == null){
				versioneProtocollo = soggettiCore.getVersioneDefaultProtocollo(tipo_protocollo);
			}
			
			
			//boolean isSupportatoCodiceIPA = soggettiCore.isSupportatoCodiceIPA(tipo_protocollo); 
			//boolean isSupportatoIdentificativoPorta = soggettiCore.isSupportatoIdentificativoPorta(tipo_protocollo);
			boolean isSupportatoAutenticazioneSoggetti = soggettiCore.isSupportatoAutenticazioneSoggetti(tipo_protocollo);
			boolean isPddEsterna = pddCore.isPddEsterna(pdd);
			
			// La tipologia serve nella console solo a far comparire i parametri di autenticazione,
			// a me se arrivano ok, altrimenti vedrò
			String tipologia = "";
			String tipoauthSoggetto = null;
			
			// Se il protocollo scelto supporta l'autenticazione dei soggetti faccio ulteriori check sul tipo autenticazione.
			if(isSupportatoAutenticazioneSoggetti){
				if(isPddEsterna){
					
					if(tipologia==null || "".equals(tipologia)){
						tipologia = SoggettiCostanti.SOGGETTO_RUOLO_EROGATORE;
					}
					
					if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA.equals(tipoauthSoggetto)){
						tipoauthSoggetto = null;
					}
				}
				if (tipoauthSoggetto == null) {
					if(isPddEsterna){
						
						if(SoggettiCostanti.SOGGETTO_RUOLO_FRUITORE.equals(tipologia) || SoggettiCostanti.SOGGETTO_RUOLO_ENTRAMBI.equals(tipologia)){
							tipoauthSoggetto = soggettiCore.getAutenticazione_generazioneAutomaticaPorteApplicative();
						}else{
							tipoauthSoggetto = ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA;
						}						
					}
					else{
						tipoauthSoggetto = ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA;
					}
				}
			}
			

			// Controlli sui campi immessi
			String nomeprov = "TODO";
			String codiceIpa = "TODO, questo non cel'ho.";
			String pd_url_prefix_rewriter = "TODO";
			String pa_url_prefix_rewriter = "TODO";
			
			String descr = soggetto.getDescrizione();
			// TODO: Ho cambiato a public la visibilità di soggettiCheckData. Ricompilare il core?
			boolean isOk = soggettiHelper.soggettiCheckData(tipoOp, null, tipoprov, nomeprov, codiceIpa, pd_url_prefix_rewriter, pa_url_prefix_rewriter,
					null, false, descr);
			
			int totPdd = 0; //TODO.
			if (isOk) {
				if (!singlePdD) {
					isOk = false;
					// Controllo che pdd appartenga alla lista di pdd
					// esistenti
					for (int j = 0; j < totPdd; j++) {
						String tmpPdd = pddList[j];
						if (tmpPdd.equals(pdd) && !pdd.equals("-")) {
							isOk = true;
						}
					}
					if (!isOk) {
						pd.setMessage("La Porta di Dominio dev'essere scelta tra quelle definite nel pannello Porte di Dominio");
					}
				}
			}
			
			ConsoleOperationType consoleOperationType = ConsoleOperationType.ADD;
			ConsoleInterfaceType consoleInterfaceType = ProtocolPropertiesUtilities.getTipoInterfaccia(soggettiHelper);
			
			IDSoggetto idSoggetto = new IDSoggetto(tipoprov,nomeprov);
			IProtocolFactory<?> protocolFactory  = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipo_protocollo); 
			IConsoleDynamicConfiguration consoleDynamicConfiguration=  protocolFactory.createDynamicConfigurationConsole();
			IRegistryReader registryReader = soggettiCore.getRegistryReader(protocolFactory); 
			IConfigIntegrationReader configRegistryReader = soggettiCore.getConfigIntegrationReader(protocolFactory);
			ConsoleConfiguration consoleConfiguration = consoleDynamicConfiguration.getDynamicConfigSoggetto(consoleOperationType, consoleInterfaceType, 
					registryReader, configRegistryReader, idSoggetto);
			ProtocolProperties protocolProperties = soggettiHelper.estraiProtocolPropertiesDaRequest(consoleConfiguration, consoleOperationType);
			
			// Validazione base dei parametri custom 
			if(isOk){
				try{
					soggettiHelper.validaProtocolProperties(consoleConfiguration, consoleOperationType, consoleInterfaceType, protocolProperties);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}
			
			// Valido i parametri custom se ho gia' passato tutta la validazione prevista
			if(isOk){
				try{
					//validazione campi dinamici
					consoleDynamicConfiguration.validateDynamicConfigSoggetto(consoleConfiguration, consoleOperationType, protocolProperties, 
							registryReader, configRegistryReader, idSoggetto); 
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}
			
			// Inserisco il soggetto nel db
			if (codiceIpa==null || codiceIpa.equals("")) {
				codiceIpa = soggettiCore.getCodiceIPADefault(tipo_protocollo, idSoggetto, false);
			}

			String portadom = "TODO";
			if (portadom==null || portadom.equals("")) {
				portadom=soggettiCore.getIdentificativoPortaDefault(tipo_protocollo, idSoggetto);
			}
			
			org.openspcoop2.core.registry.Soggetto soggettoRegistro = new org.openspcoop2.core.registry.Soggetto();
			
			soggettoRegistro.setNome(nomeprov);
			soggettoRegistro.setTipo(tipoprov);
			soggettoRegistro.setDescrizione(descr);
			soggettoRegistro.setVersioneProtocollo(versioneProtocollo);
			soggettoRegistro.setIdentificativoPorta(portadom);
			soggettoRegistro.setCodiceIpa(codiceIpa);
			
			if(pddCore.isGestionePddAbilitata(soggettiHelper)==false){
				if(SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE.equals(dominio)) {
					pdd = nomePddGestioneLocale;
				}
				else {
					pdd = null;
				}
			}
			
			if(soggettiCore.isSinglePdD()){
				if (pdd==null || pdd.equals("-"))
					soggettoRegistro.setPortaDominio(null);
				else
					soggettoRegistro.setPortaDominio(pdd);
			}else{
				soggettoRegistro.setPortaDominio(pdd);
			}
			
			
			boolean privato = false; 	// TODO: CHECK
			soggettoRegistro.setSuperUser(userLogin);
			soggettoRegistro.setPrivato(privato);
//			
//			this.tipoauthSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
//			this.utenteSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
//			this.passwordSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
//			this.subjectSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
//			this.principalSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL);
			
			final String utenteSoggetto = null;
			final String principalSoggetto = null;
			final String passwordSoggetto = null;
			final String subjectSoggetto = null;

			
			if(isSupportatoAutenticazioneSoggetti){	//Se sto utilizzando un'autenticazione qualsiasi diversa da NESSUNA.
				if(tipoauthSoggetto!=null && !"".equals(tipoauthSoggetto) && !ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA.equals(tipoauthSoggetto)){
					CredenzialiSoggetto credenziali = new CredenzialiSoggetto();
					credenziali.setTipo(CredenzialeTipo.toEnumConstant(tipoauthSoggetto));
					credenziali.setUser(utenteSoggetto);
					if(principalSoggetto!=null && !"".equals(principalSoggetto)){
						credenziali.setUser(principalSoggetto); // al posto di user
					}
					credenziali.setPassword(passwordSoggetto);
					credenziali.setSubject(subjectSoggetto);
					soggettoRegistro.setCredenziali(credenziali);
				}
				else{
					soggettoRegistro.setCredenziali(null);
				}
			}
			
			Connettore connettore = new Connettore();
			connettore.setTipo(CostantiDB.CONNETTORE_TIPO_DISABILITATO);
			
			if ( !singlePdD && soggettiCore.isRegistroServiziLocale() && !pdd.equals("-")) {

				PdDControlStation aPdD = pddCore.getPdDControlStation(pdd);
				int porta = aPdD.getPorta() <= 0 ? 80 : aPdD.getPorta();

				// nel caso in cui e' stata selezionato un nal
				// e la PdD e' di tipo operativo oppure non-operativo
				// allora setto come default il tipo HTTP
				// altrimenti il connettore e' disabilitato
				String tipoPdD = aPdD.getTipo();
				if ((tipoPdD != null) && (!singlePdD) && (tipoPdD.equals(PddTipologia.OPERATIVO.toString()) || tipoPdD.equals(PddTipologia.NONOPERATIVO.toString()))) {
					String ipPdd = aPdD.getIp();

					String url = aPdD.getProtocollo() + "://" + ipPdd + ":" + porta + "/" + soggettiCore.getSuffissoConnettoreAutomatico();
					url = url.replace(CostantiControlStation.PLACEHOLDER_SOGGETTO_ENDPOINT_CREAZIONE_AUTOMATICA, 
							soggettiCore.getWebContextProtocolAssociatoTipoSoggetto(tipoprov));
					connettore.setTipo(CostantiDB.CONNETTORE_TIPO_HTTP);

					Property property = new Property();
					property.setNome(CostantiDB.CONNETTORE_HTTP_LOCATION);
					property.setValore(url);
					connettore.addProperty(property);
				}
			}
			
			soggettoRegistro.setConnettore(connettore);

			org.openspcoop2.core.config.Soggetto soggettoConfig = new org.openspcoop2.core.config.Soggetto();
			
			boolean isRouter = false; //TODO
			
			// imposto soggettoConfig
			soggettoConfig.setNome(nomeprov);
			soggettoConfig.setTipo(tipoprov);
			soggettoConfig.setDescrizione(descr);
			soggettoConfig.setIdentificativoPorta(portadom);
			soggettoConfig.setRouter(isRouter);
			soggettoConfig.setSuperUser(userLogin);

			//imposto properties custom
			soggettoRegistro.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolProperties(protocolProperties, consoleOperationType,null)); 


			SoggettoCtrlStat sog = new SoggettoCtrlStat(soggettoRegistro, soggettoConfig);
			// eseguo le operazioni
			soggettiCore.performCreateOperation(userLogin, soggettiHelper.smista(), sog);
			
			
			// Check Utenza per multi-tenant
			if(singlePdD) {
				
				List<Object> listaOggettiDaModificare = new ArrayList<Object>();

				// TODO: il codice sottostante non funziona piu' nella nuova versione, vedere come funziona
//				boolean operativo = !pddCore.isPddEsterna(pdd);
//				if(operativo) {
//					// check utenze che hanno il protocollo
//					
//					User userPerCheck = new User();
//					userPerCheck.addProtocolloSupportato(tipo_protocollo);
//					boolean forceEnableMultitenant = utentiCore.isForceEnableMultiTenant(userPerCheck, false);
//					if(forceEnableMultitenant) {
//						List<String> usersList = utentiCore.getUsersByProtocolloSupportato(tipo_protocollo, true);
//						if(usersList!=null && usersList.size()>0) {
//							for (String user : usersList) {
//								User u = utentiCore.getUser(user);
//								if(u.isPermitMultiTenant()==false) {
//									u.setPermitMultiTenant(true);
//									listaOggettiDaModificare.add(u);
//								}
//							}
//						}
//					}
//					
//				}
				
				if(listaOggettiDaModificare.size()>0) {
					soggettiCore.performUpdateOperation(userLogin, soggettiHelper.smista(), listaOggettiDaModificare.toArray());
				}
			}
			
			// cancello file temporanei
			soggettiHelper.deleteBinaryProtocolPropertiesTmpFiles(protocolProperties); 

			
*/
			
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
     * Elimina un soggetto
     *
     * Questa operazione consente di eliminare un soggetto identificato dal nome
     *
     */
	@Override
    public void delete(String nome, ProfiloEnum profilo) {
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
     * Ricerca soggetti
     *
     * Elenca i soggetti registrati
     *
     */
	@Override
    public ListaSoggetti findAll(ProfiloEnum profilo, String q, Integer limit, Integer offset, DominioEnum dominio, String ruolo) {
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
     * Restituisce il dettaglio di un soggetto
     *
     * Questa operazione consente di ottenere il dettaglio di un soggetto identificato dal nome
     *
     */
	@Override
    public Soggetto get(String nome, ProfiloEnum profilo) {
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
     * Modifica i dati di un soggetto
     *
     * Questa operazione consente di aggiornare i dati di un soggetto identificato dal nome
     *
     */
	@Override
    public void update(Object body, String nome, ProfiloEnum profilo) {
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

