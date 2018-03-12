/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesUtilities;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

/**
 * serviziChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaChange extends Action {

	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory= null;
	private IRegistryReader registryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	private ConsoleInterfaceType consoleInterfaceType = null;
	private String protocolPropertiesSet = null;
	private String editMode = null;

	private BinaryParameter wsdlimpler, wsdlimplfru;
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		this.consoleOperationType = ConsoleOperationType.CHANGE;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.CHANGE;
		List<ProtocolProperty> oldProtocolPropertyList = null;

		try {
			boolean multitenant = ServletUtils.getUserFromSession(session).isPermitMultiTenant(); 
			
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			this.consoleInterfaceType = ProtocolPropertiesUtilities.getTipoInterfaccia(apsHelper); 

			this.editMode = apsHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			this.protocolPropertiesSet = apsHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_SET);
			String id = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			// int idInt = Integer.parseInt(id);
			String nomeservizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO);
			String tiposervizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO);
			String accordo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO);
			//			String accordoLabel = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO_LABEL);
			String servcorr = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_SERVIZIO_CORRELATO);
			// String servpub = apsHelper.getParameter("servpub");
			//String endpointtype = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE );
			
			String endpointtype = apsHelper.readEndPointType();
			String tipoconn = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO );
			String autenticazioneHttp = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			String user = null;
			String password = null;
			
			String connettoreDebug = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);		
			
			// proxy
			String proxy_enabled = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxy_hostname = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxy_port = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxy_username = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxy_password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);
			
			// opzioni avanzate
			String transfer_mode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transfer_mode_chunk_size = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirect_mode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirect_max_hop = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(apsHelper, transfer_mode, redirect_mode);
			
			// http
			String url = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL  );
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// jms
			String nome = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String initcont = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String sendas = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(endpointtype)){
				user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}
			
			// https
			String httpsurl = url;
			String httpstipologia = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE );
			String httpshostverifyS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			boolean httpshostverify = false;
			if (httpshostverifyS != null && httpshostverifyS.equals(Costanti.CHECK_BOX_ENABLED))
				httpshostverify = true;
			String httpspath = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION );
			String httpstipo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			boolean httpsstato = false;
			if (httpsstatoS != null && httpsstatoS.equals(Costanti.CHECK_BOX_ENABLED))
				httpsstato = true;
			String httpskeystore = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// file
			String requestOutputFileName = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			String requestOutputFileNameHeaders = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			String requestOutputParentDirCreateIfNotExists = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			String requestOutputOverwriteIfExists = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			String responseInputMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			String responseInputFileName = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			String responseInputFileNameHeaders = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			String responseInputDeleteAfterRead = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			String responseInputWaitTime = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
			
			String profilo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROFILO);

			
			String priv = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PRIVATO);
			Boolean privato = ServletUtils.isCheckBoxEnabled(priv);
			
			String portType = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE);
			String descrizione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_DESCRIZIONE);
			String statoPackage = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);
			
			String versione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE);
			
			String backToStato = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RIPRISTINA_STATO);
			String actionConfirm = apsHelper.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);

			boolean validazioneDocumenti = true;
			String tmpValidazioneDocumenti = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
			if(ServletUtils.isEditModeInProgress(this.editMode)){
				// primo accesso alla servlet
				if(tmpValidazioneDocumenti!=null){
					if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
						validazioneDocumenti = true;
					}
					else if("false".equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_DISABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
						validazioneDocumenti = false;
					}
				}else{
					validazioneDocumenti = true;
				}
			}else{
				if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
					validazioneDocumenti = true;
				}
				else{
					validazioneDocumenti = false;
				}
			}
			
			// Preparo il menu
			apsHelper.makeMenu();

			String[] ptList = null;
			// Prendo nome, tipo e provider dal db
			// Prendo la lista di soggetti e la metto in un array
			// Prendo la lista di accordi e la metto in un array

			String provider = "";
			String[] soggettiList = null;
			String[] soggettiListLabel = null;
			String[] accordiList = null;
			String[] accordiListLabel = null;
			AccordiServizioParteSpecificaCore apsCore = null;
			SoggettiCore soggettiCore = null;
			AccordiServizioParteComuneCore apcCore = null;
			PorteApplicativeCore porteApplicativeCore = null;
			PorteDelegateCore porteDelegateCore = null;
			String nomeSoggettoErogatore = "";
			String tipoSoggettoErogatore = "";
			AccordoServizioParteSpecifica asps = null;

			String oldversioneaccordo = null;
			String oldtiposervizio = null;
			String oldnomeservizio = null;
			String oldtiposoggetto = null;
			String oldnomesoggetto = null;
			String oldStatoPackage = null;

			boolean accordoPrivato = false;
			String uriAccordo = null;

			Soggetto soggettoErogatoreID = null;
			List<String> versioniProtocollo = null;
			List<String> tipiSoggettiCompatibiliAccordo = null;
			List<String> tipiServizioCompatibiliAccordo = null;

			apsCore = new AccordiServizioParteSpecificaCore();
			soggettiCore = new SoggettiCore(apsCore);
			apcCore = new AccordiServizioParteComuneCore(apsCore);
			porteApplicativeCore = new PorteApplicativeCore(apsCore);
			porteDelegateCore = new PorteDelegateCore(apsCore);
			PddCore pddCore = new PddCore(apsCore);

			String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			boolean gestioneErogatori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
				else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
					gestioneErogatori = true;
				}
			}
			
			boolean multiTenant = ServletUtils.getUserFromSession(session).isPermitMultiTenant();
			
			PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();

			boolean soggettoOperativo = true;
			boolean generaPACheckSoggetto = true; 
			boolean [] permessi = new boolean[2];
			permessi[0] = pu.isServizi();
			permessi[1] = pu.isAccordiCooperazione();
			List<AccordoServizioParteComune> listaTmp =  
					AccordiServizioParteComuneUtilities.accordiListFromPermessiUtente(apcCore, userLogin, new Search(true), permessi);
			List<AccordoServizioParteComune> lista = null;
			if(apsHelper.isModalitaCompleta()) {
				lista = listaTmp;
			}
			else {
				// filtro accordi senza risorse o senza pt/operation
				lista = new ArrayList<AccordoServizioParteComune>();
				for (AccordoServizioParteComune accordoServizioParteComune : listaTmp) {
					if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(accordoServizioParteComune.getServiceBinding())) {
						if(accordoServizioParteComune.sizeResourceList()>0) {
							lista.add(accordoServizioParteComune);	
						}
					}
					else {
						boolean ptValido = false;
						for (PortType pt : accordoServizioParteComune.getPortTypeList()) {
							if(pt.sizeAzioneList()>0) {
								ptValido = true;
								break;
							}
						}
						if(ptValido) {
							lista.add(accordoServizioParteComune);	
						}
					}
				}
			}

			if (lista.size() > 0) {
				accordiList = new String[lista.size()];
				accordiListLabel = new String[lista.size()];
				int i = 0;
				for (AccordoServizioParteComune as : lista) {
					accordiList[i] = as.getId().toString();
					IDSoggetto soggettoReferente = null;
					int idReferente = -1;
					if(as.getSoggettoReferente()!=null && as.getSoggettoReferente().getId()!=null)
						idReferente = as.getSoggettoReferente().getId().intValue();

					if(idReferente>0){
						Soggetto sRef = soggettiCore.getSoggettoRegistro(idReferente);
						soggettoReferente = new IDSoggetto();
						soggettoReferente.setTipo(sRef.getTipo());
						soggettoReferente.setNome(sRef.getNome());
					}
					accordiListLabel[i] = idAccordoFactory.getUriFromValues(as.getNome(),soggettoReferente,as.getVersione());
					i++;
				}
			}
			//				}

			// Servizio
			asps = apsCore.getAccordoServizioParteSpecifica(Long.parseLong(id));

			String providerSoggettoFruitore = null;
			String tipoSoggettoFruitore = null;
			String nomeSoggettoFruitore = null;
			if(gestioneFruitori) {
				// In questa modalità ci deve essere solo un fruitore
				Fruitore fruitore = asps.getFruitore(0);
				providerSoggettoFruitore = fruitore.getId()+"";
				tipoSoggettoFruitore = fruitore.getTipo();
				nomeSoggettoFruitore = fruitore.getNome();
			}
			
			String tipoProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(asps.getTipoSoggettoErogatore());
			
			String tmpTitle = apsHelper.getLabelIdServizio(asps);
			
			Boolean isConnettoreCustomUltimaImmagineSalvata = asps.getConfigurazioneServizio()!=null &&
					asps.getConfigurazioneServizio().getConnettore()!=null &&
					asps.getConfigurazioneServizio().getConnettore().getCustom();
			
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(asps.getConfigurazioneServizio().getConnettore(), ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_CHANGE, apsHelper, 
							(endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro
			
			// Lista port-type associati all'accordo di servizio
			// se l'accordo e' selezionato allora prendo quello selezionato
			// altrimenti il primo
			// della lista
			AccordoServizioParteComune as = null;
			if (accordo != null && !"".equals(accordo)) {
				as = apcCore.getAccordoServizio(Long.parseLong(accordo));
			} else {
				as = apcCore.getAccordoServizio(idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
				portType = asps.getPortType();
			}
		
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
			org.openspcoop2.protocol.manifest.constants.InterfaceType formatoSpecifica = apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica());

			// Lista di Accordi Compatibili
			List<AccordoServizioParteComune> asParteComuneCompatibili = null;
			try{
				asParteComuneCompatibili = apsCore.findAccordiParteComuneBySoggettoAndNome(as.getNome(), 
						new IDSoggetto(as.getSoggettoReferente().getTipo(), as.getSoggettoReferente().getNome()));
			}catch(Exception e){
				ControlStationCore.logError("Errore durante la ricerca degli accordi parte comune compatibili", e);
			}
			
			// Versione
			versioniProtocollo = apsCore.getVersioniProtocollo(tipoProtocollo);
			tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(tipoProtocollo);
			tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(tipoProtocollo,serviceBinding);

			// calcolo soggetti compatibili con accordi
			List<Soggetto> list = null;
			if(apsCore.isVisioneOggettiGlobale(userLogin)){
				list = soggettiCore.soggettiRegistroList(null, new Search(true));
			}else{
				list = soggettiCore.soggettiRegistroList(userLogin, new Search(true));
			}

			if (list.size() > 0) {
				List<String> soggettiListTmp = new ArrayList<String>();
				List<String> soggettiListLabelTmp = new ArrayList<String>();
				for (Soggetto soggetto : list) {
					if(tipiSoggettiCompatibiliAccordo.contains(soggetto.getTipo())){
						soggettiListTmp.add(soggetto.getId().toString());
						soggettiListLabelTmp.add(soggetto.getTipo() + "/" + soggetto.getNome());
					}
				}
				soggettiList = soggettiListTmp.toArray(new String[1]);
				soggettiListLabel = soggettiListLabelTmp.toArray(new String[1]);
			}


			// if (provider != null && !provider.equals(""))
			// {
			// long idErogatore = Long.parseLong(provider);
			// Soggetto soggetto =
			// core.getSoggettoRegistro(idErogatore);
			// nomeSoggettoErogatore = soggetto.getNome();
			// tipoSoggettoErogatore = soggetto.getTipo();
			// provString = tipoSoggettoErogatore+"/"+nomeSoggettoErogatore;
			// }

			nomeSoggettoErogatore = asps.getNomeSoggettoErogatore();
			tipoSoggettoErogatore = asps.getTipoSoggettoErogatore();

			if(asps.getVersione()!=null)
				oldversioneaccordo = asps.getVersione()+"";
			oldtiposervizio = asps.getTipo();
			oldnomeservizio = asps.getNome();
			oldtiposoggetto = asps.getTipoSoggettoErogatore();
			oldnomesoggetto = asps.getNomeSoggettoErogatore();
			oldStatoPackage = asps.getStatoPackage();		

			// aggiorno tmpTitle
			@SuppressWarnings("unused")
			String tmpVersione = IDServizioFactory.getInstance().getUriFromAccordo(asps);

			soggettoErogatoreID = soggettiCore.getSoggettoRegistro(new IDSoggetto(tipoSoggettoErogatore,nomeSoggettoErogatore));

			if(versione == null)
				versione = oldversioneaccordo;

			if(as!=null){
				accordoPrivato = as.getPrivato()!=null && as.getPrivato();
				uriAccordo = idAccordoFactory.getUriFromAccordo(as);

				if( apsCore.isShowCorrelazioneAsincronaInAccordi() ){
					if (portType != null && !"".equals(portType) && !"-".equals(portType)){
						PortType pt = null;
						for(int i=0; i<as.sizePortTypeList(); i++){
							if(portType.equals(as.getPortType(i).getNome())){
								pt = as.getPortType(i);
								break;
							}
						}
						boolean servizioCorrelato = false;
						if(pt!=null){
							for(int i=0; i<pt.sizeAzioneList(); i++){
								Operation op = pt.getAzione(i);
								if(op.getCorrelataServizio()!=null && !pt.getNome().equals(op.getCorrelataServizio()) && op.getCorrelata()!=null){
									servizioCorrelato = true;
									break;
								}
							}
						}
						if(servizioCorrelato){
							servcorr=Costanti.CHECK_BOX_ENABLED;
						}
						else{
							servcorr=Costanti.CHECK_BOX_DISABLED;
						}							
					}
				}
			}

			List<PortType> portTypesTmp = apcCore.accordiPorttypeList(as.getId().intValue(), new Search(true));
			List<PortType> portTypes = null;
			
			if(apsHelper.isModalitaCompleta()) {
				portTypes = portTypesTmp;
			}
			else {
				// filtro pt senza op
				portTypes = new ArrayList<PortType>();
				for (PortType portTypeCheck : portTypesTmp) {
					if(portTypeCheck.sizeAzioneList()>0) {
						portTypes.add(portTypeCheck);
					}
				}
			}
			
			
			if (portTypes.size() > 0) {
				ptList = new String[portTypes.size() + 1];
				ptList[0] = "-";
				int i = 1;
				for (Iterator<PortType> iterator = portTypes.iterator(); iterator.hasNext();) {
					PortType portType2 = iterator.next();
					ptList[i] = portType2.getNome();
					i++;
				}
			}

			//se passo dal link diretto di ripristino stato (e poi con conferma == ok) imposto il nuovo stato
			if(backToStato != null && (actionConfirm == null || actionConfirm.equals(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_OK))) 
				statoPackage = backToStato;
			
			
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipoProtocollo);
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = soggettiCore.getRegistryReader(this.protocolFactory); 
			IDServizio idAps = apsHelper.getIDServizioFromValues(tiposervizio, nomeservizio, tipoSoggettoErogatore,nomeSoggettoErogatore, versione);
			this.consoleConfiguration = this.consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteSpecifica(this.consoleOperationType, this.consoleInterfaceType, this.registryReader, idAps );
			this.protocolProperties = apsHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);

			oldProtocolPropertyList = asps.getProtocolPropertyList(); 

			if(this.protocolPropertiesSet == null){
				ProtocolPropertiesUtils.mergeProtocolProperties(this.protocolProperties, oldProtocolPropertyList, this.consoleOperationType);
			}

			
			Properties propertiesProprietario = new Properties();
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, id);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_SERVIZIO_PARTE_SPECIFICA);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, uriAccordo);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE, URLEncoder.encode( AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE + "?" + request.getQueryString(), "UTF-8"));
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, tipoProtocollo);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO, "");
			
			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if (ServletUtils.isEditModeInProgress(this.editMode)) {
				// setto la barra del titolo
				List<Parameter> lstParm = new ArrayList<Parameter>();

				if(gestioneFruitori) {
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				}
				else {
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				}
				lstParm.add(new Parameter(tmpTitle, null));

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );


				nomeSoggettoErogatore = asps.getNomeSoggettoErogatore();
				tipoSoggettoErogatore = asps.getTipoSoggettoErogatore();
				if (servcorr == null) {
					if(TipologiaServizio.CORRELATO.equals(asps.getTipologiaServizio()))
						servcorr = Costanti.CHECK_BOX_ENABLED;
					else
						servcorr = Costanti.CHECK_BOX_DISABLED;
				}
				if (accordo == null)
					accordo = asps.getIdAccordo().toString();
				if (profilo == null)
					profilo = asps.getVersioneProtocollo();
				if (descrizione == null)
					descrizione = asps.getDescrizione();
				if(statoPackage==null)
					statoPackage = asps.getStatoPackage();

				if(versione==null){
					if(asps.getVersione()!=null)
						versione=asps.getVersione().intValue()+"";
				}

				if(tiposervizio==null){
					tiposervizio = asps.getTipo();
				}
				if(nomeservizio==null){
					nomeservizio = asps.getNome();
				}


				// Controllo se il soggetto erogare appartiene ad una pdd di tipo operativo.
				IDSoggetto idSoggettoEr = new IDSoggetto( tipoSoggettoErogatore,  nomeSoggettoErogatore);
				Soggetto soggetto = soggettiCore.getSoggettoRegistro(idSoggettoEr );
				if(pddCore.isPddEsterna(soggetto.getPortaDominio())){
					soggettoOperativo = false;
					generaPACheckSoggetto = soggettoOperativo;
				}


				Connettore connettore = asps.getConfigurazioneServizio().getConnettore();

				// if(endpointtype==null || endpointtype.equals(""))
				// endpointtype = connettore.getTipo();
				//			
				// Map<String,String> properties =
				// connettore.getProperties();

				if ((endpointtype == null) || (url == null) || (nome == null)) {
					Map<String, String> props = connettore.getProperties();

					if (endpointtype == null) {
						if ((connettore.getCustom()!=null && connettore.getCustom()) && 
								!connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_HTTPS) && 
								!connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_FILE)) {
							endpointtype = ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM;
							tipoconn = connettore.getTipo();
						} else
							endpointtype = connettore.getTipo();
					}
					
					if(connettoreDebug==null && props!=null){
						String v = props.get(CostantiDB.CONNETTORE_DEBUG);
						if(v!=null){
							if("true".equals(v)){
								connettoreDebug = Costanti.CHECK_BOX_ENABLED;
							}
							else{
								connettoreDebug = Costanti.CHECK_BOX_DISABLED;
							}
						}
					}
					
					if(proxy_enabled==null && props!=null){
						String v = props.get(CostantiDB.CONNETTORE_PROXY_TYPE);
						if(v!=null && !"".equals(v)){
							proxy_enabled = Costanti.CHECK_BOX_ENABLED_TRUE;
							
							// raccolgo anche altre proprietà
							v = props.get(CostantiDB.CONNETTORE_PROXY_HOSTNAME);
							if(v!=null && !"".equals(v)){
								proxy_hostname = v.trim();
							}
							v = props.get(CostantiDB.CONNETTORE_PROXY_PORT);
							if(v!=null && !"".equals(v)){
								proxy_port = v.trim();
							}
							v = props.get(CostantiDB.CONNETTORE_PROXY_USERNAME);
							if(v!=null && !"".equals(v)){
								proxy_username = v.trim();
							}
							v = props.get(CostantiDB.CONNETTORE_PROXY_PASSWORD);
							if(v!=null && !"".equals(v)){
								proxy_password = v.trim();
							}
						}
					}
					
					if(transfer_mode==null && props!=null){
						String v = props.get(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE);
						if(v!=null && !"".equals(v)){
							
							transfer_mode = v.trim();
							
							if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome().equals(transfer_mode)){
								// raccolgo anche altra proprietà correlata
								v = props.get(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE);
								if(v!=null && !"".equals(v)){
									transfer_mode_chunk_size = v.trim();
								}
							}
							
						}
					}
					
					if(redirect_mode==null && props!=null){
						String v = props.get(CostantiDB.CONNETTORE_HTTP_REDIRECT_FOLLOW);
						if(v!=null && !"".equals(v)){
							
							if("true".equalsIgnoreCase(v.trim()) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v.trim())){
								redirect_mode = CostantiConfigurazione.ABILITATO.getValue();
							}
							else{
								redirect_mode = CostantiConfigurazione.DISABILITATO.getValue();
							}					
							
							if(CostantiConfigurazione.ABILITATO.getValue().equals(redirect_mode)){
								// raccolgo anche altra proprietà correlata
								v = props.get(CostantiDB.CONNETTORE_HTTP_REDIRECT_MAX_HOP);
								if(v!=null && !"".equals(v)){
									redirect_max_hop = v.trim();
								}
							}
							
						}
					}
					
					opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(apsHelper, transfer_mode, redirect_mode);
					
					if (url == null) {
						url = props.get(CostantiDB.CONNETTORE_HTTP_LOCATION);
					}
					if (nome == null) {
						nome = props.get(CostantiDB.CONNETTORE_JMS_NOME);
						String userTmp = props.get(CostantiDB.CONNETTORE_USER);
						if(userTmp!=null && !"".equals(userTmp)){
							user = userTmp;
						}
						String passwordTmp = props.get(CostantiDB.CONNETTORE_PWD);
						if(passwordTmp!=null && !"".equals(passwordTmp)){
							password = passwordTmp;
						}
						tipo = props.get(CostantiDB.CONNETTORE_JMS_TIPO);
						initcont = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL);
						urlpgk = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG);
						provurl = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL);
						connfact = props.get(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY);
						sendas = props.get(CostantiDB.CONNETTORE_JMS_SEND_AS);
					}
					
					autenticazioneHttp = apsHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);
					
					if (httpstipologia == null) {
						httpsurl = props.get(CostantiDB.CONNETTORE_HTTPS_LOCATION);
						httpstipologia = props.get(CostantiDB.CONNETTORE_HTTPS_SSL_TYPE);
						httpshostverifyS = props.get(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER);
						if(httpshostverifyS!=null){
							httpshostverify = Boolean.valueOf(httpshostverifyS);
						}
						httpspath = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
						httpstipo = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_TYPE);
						httpspwd = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
						httpsalgoritmo = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
						httpspwdprivatekeytrust = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD);
						httpspathkey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION);
						httpstipokey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_TYPE);
						httpspwdkey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
						httpspwdprivatekey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD);
						httpsalgoritmokey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
						if (httpspathkey == null) {
							httpsstato = false;
							httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
						} else {
							httpsstato = true;
							if (httpspathkey.equals(httpspath) &&
									httpstipokey.equals(httpstipo) &&
									httpspwdkey.equals(httpspwd))
								httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
							else
								httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;
						}
					}
					
					// file
					if(responseInputMode==null && props!=null){
						
						requestOutputFileName = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE);	
						requestOutputFileNameHeaders = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS);	
						String v = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
						if(v!=null && !"".equals(v)){
							if("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) ){
								requestOutputParentDirCreateIfNotExists = Costanti.CHECK_BOX_ENABLED_TRUE;
							}
						}					
						v = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE);
						if(v!=null && !"".equals(v)){
							if("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) ){
								requestOutputOverwriteIfExists = Costanti.CHECK_BOX_ENABLED_TRUE;
							}
						}	
						
						v = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_MODE);
						if(v!=null && !"".equals(v)){
							if("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) ){
								responseInputMode = CostantiConfigurazione.ABILITATO.getValue();
							}
						}
						if(CostantiConfigurazione.ABILITATO.getValue().equals(responseInputMode)){						
							responseInputFileName = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE);
							responseInputFileNameHeaders = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS);
							v = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_DELETE_AFTER_READ);
							if(v!=null && !"".equals(v)){
								if("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) ){
									responseInputDeleteAfterRead = Costanti.CHECK_BOX_ENABLED_TRUE;
								}
							}						
							responseInputWaitTime = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);						
						}
						
					}
				}

				// default
				if(httpsalgoritmo==null || "".equals(httpsalgoritmo)){
					httpsalgoritmo = TrustManagerFactory.getDefaultAlgorithm();
				}
				if(httpsalgoritmokey==null || "".equals(httpsalgoritmokey)){
					httpsalgoritmokey = KeyManagerFactory.getDefaultAlgorithm();
				}
				if(httpstipologia==null || "".equals(httpstipologia)){
					httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
				}
				if(httpshostverifyS==null || "".equals(httpshostverifyS)){
					httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
					httpshostverify = true;
				}

				portType = (portType != null && !"".equals(portType) ? portType : asps.getPortType());

				privato = asps.getPrivato()!=null && asps.getPrivato();
				
				if(this.wsdlimpler == null){
					this.wsdlimpler = new BinaryParameter();
					this.wsdlimpler.setValue(asps.getByteWsdlImplementativoErogatore());
				}
				
				if(this.wsdlimplfru == null){
					this.wsdlimplfru = new BinaryParameter();
					this.wsdlimplfru.setValue(asps.getByteWsdlImplementativoFruitore());
				}

				if(backToStato == null){
					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();
					dati.addElement(ServletUtils.getDataElementForEditModeFinished());
					
					// update della configurazione 
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties,
							this.registryReader, idAps);

					dati = apsHelper.addHiddenFieldsToDati(tipoOp, id, null, null, dati);

					dati = apsHelper.addServiziToDati(dati, nomeservizio, tiposervizio, oldnomeservizio, oldtiposervizio,
							provider, tipoSoggettoErogatore, nomeSoggettoErogatore,
							soggettiList, soggettiListLabel, accordo,serviceBinding, formatoSpecifica, accordiList, accordiListLabel, servcorr, this.wsdlimpler, this.wsdlimplfru, tipoOp, 
							id, tipiServizioCompatibiliAccordo, profilo, portType, ptList,  privato,uriAccordo, descrizione, 
							soggettoErogatoreID.getId(),statoPackage,oldStatoPackage
							,versione,versioniProtocollo,validazioneDocumenti,
							null,null,generaPACheckSoggetto,asParteComuneCompatibili,
							null,null,null,null,false,
							null,null,null,null,null,null,null,
							tipoProtocollo,null,
							null, null, providerSoggettoFruitore, tipoSoggettoFruitore, nomeSoggettoFruitore,
							null, null, null, null, null,
							null, null, null, null,
							null);

					if(multiTenant || apsHelper.isModalitaCompleta() || (!soggettoOperativo && !gestioneFruitori)) {
					
						boolean forceEnableConnettore = false;
//						if( apsHelper.isModalitaStandard() && !TipiConnettore.DISABILITATO.getNome().equals(endpointtype) ) {
//							forceEnableConnettore = true;
//						}
						
						dati = apsHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp,  
								(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX,
								url,nome, tipo, user, password, initcont, urlpgk,
								provurl, connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp,
								httpsurl, httpstipologia, httpshostverify,
								httpspath, httpstipo, httpspwd, httpsalgoritmo,
								httpsstato, httpskeystore,
								httpspwdprivatekeytrust, httpspathkey,
								httpstipokey, httpspwdkey, httpspwdprivatekey,
								httpsalgoritmokey, tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
								nomeservizio, tiposervizio, null, null, null,
								null, oldStatoPackage, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
								opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
								requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
								responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
								listExtendedConnettore, forceEnableConnettore);
						
					}
					else {
						
						dati = apsHelper.addEndPointToDatiAsHidden(dati,
								endpointtype, url, nome, tipo,
								user, password, initcont, urlpgk,
								provurl, connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp,
								httpsurl, httpstipologia, httpshostverify,
								httpspath, httpstipo, httpspwd,
								httpsalgoritmo, httpsstato, httpskeystore,
								httpspwdprivatekeytrust, httpspathkey,
								httpstipokey, httpspwdkey,
								httpspwdprivatekey, httpsalgoritmokey,
								tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
								nomeservizio, tiposervizio, null, null, null,
								null, oldStatoPackage,
								requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
								responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime);
						
					}
					
					// aggiunta campi custom
					dati = apsHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);

					pd.setDati(dati);

					if(apsCore.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(asps.getStatoPackage())){
						pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
					}

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
							ForwardParams.CHANGE());
				}
			}

			if (apsHelper.isModalitaStandard() && (nomeservizio==null || "".equals(nomeservizio))) {
				switch (serviceBinding) {
				case REST:
					// il nome del servizio e' quello dell'accordo
					nomeservizio = as.getNome();
					break;
				case SOAP:
				default:
					// il nome del servizio e' quello del porttype selezionato
					nomeservizio = portType;
					break;
				}
			}
			
			// Controlli sui campi immessi
			boolean isOk = apsHelper.serviziCheckData(tipoOp, soggettiList,
					accordiList, asps.getNome(), asps.getTipo(), asps.getVersione(),
					nomeservizio, tiposervizio, provider,
					nomeSoggettoErogatore, tipoSoggettoErogatore, accordo, serviceBinding, 
					servcorr, endpointtype, url, nome, tipo, user, password,
					initcont, urlpgk, provurl, connfact, sendas, this.wsdlimpler, this.wsdlimplfru, id,
					profilo, portType,ptList,accordoPrivato,privato,
					httpsurl, httpstipologia, httpshostverify,
					httpspath, httpstipo, httpspwd, httpsalgoritmo,
					httpsstato, httpskeystore,
					httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey, tipoconn,versione,validazioneDocumenti,backToStato,autenticazioneHttp,
					proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
					opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
					requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					null,null,null,null,null,
					null, null, null, null,false,
					generaPACheckSoggetto, listExtendedConnettore,
					null,null,null,null,null,
					null, null, null, null,
					false);
			
			// Validazione base dei parametri custom 
			if(isOk){
				try{
					apsHelper.validaProtocolProperties(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties);
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
					this.consoleDynamicConfiguration.validateDynamicConfigAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, this.protocolProperties, this.registryReader, idAps);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}
			
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParm = new ArrayList<Parameter>();

				if(gestioneFruitori) {
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				}
				else {
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				}
				lstParm.add(new Parameter(tmpTitle, null));

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				portType = (portType != null && !"".equals(portType) ? portType : asps.getPortType());

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				// update della configurazione 
				this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties,
						this.registryReader, idAps);

				dati = apsHelper.addHiddenFieldsToDati(tipoOp, id, null, null, dati);

				dati = apsHelper.addServiziToDati(dati, nomeservizio, tiposervizio, oldnomeservizio, oldtiposervizio, 
						provider, tipoSoggettoErogatore, nomeSoggettoErogatore, soggettiList, 
						soggettiListLabel, accordo, serviceBinding,formatoSpecifica, 
						accordiList, accordiListLabel, servcorr, this.wsdlimpler, this.wsdlimplfru, tipoOp, 
						id, tipiServizioCompatibiliAccordo, profilo, portType, ptList, privato,uriAccordo, descrizione, soggettoErogatoreID.getId(),
						statoPackage,oldStatoPackage,versione,versioniProtocollo,validazioneDocumenti,
						null,null,generaPACheckSoggetto,asParteComuneCompatibili,
						null,null,null,null,false,
						null,null,null,null,null,null,null,
						tipoProtocollo,null,
						null, null, providerSoggettoFruitore, tipoSoggettoFruitore, nomeSoggettoFruitore,
						null, null, null, null, null,
						null, null, null, null,
						null);

				if(multiTenant || apsHelper.isModalitaCompleta() || (!soggettoOperativo && !gestioneFruitori)) {
				
					boolean forceEnableConnettore = false;
//					if( apsHelper.isModalitaStandard() && !TipiConnettore.DISABILITATO.getNome().equals(endpointtype) ) {
//						forceEnableConnettore = true;
//					}
					
					dati = apsHelper.addEndPointToDati(dati, connettoreDebug,  endpointtype, autenticazioneHttp, 
							(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX,
							url, nome,
							tipo, user, password, initcont, urlpgk, provurl,
							connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp, httpsurl, httpstipologia,
							httpshostverify, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey, httpspwdprivatekey,
							httpsalgoritmokey, tipoconn,AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
							nomeservizio, tiposervizio, null, null, null,
							null, oldStatoPackage, true,
							isConnettoreCustomUltimaImmagineSalvata, 
							proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
							opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
							requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
							listExtendedConnettore, forceEnableConnettore);
					
				}
				else {
					
					dati = apsHelper.addEndPointToDatiAsHidden(dati,
							endpointtype, url, nome, tipo,
							user, password, initcont, urlpgk,
							provurl, connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp,
							httpsurl, httpstipologia, httpshostverify,
							httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey,
							httpspwdprivatekey, httpsalgoritmokey,
							tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
							nomeservizio, tiposervizio, null, null, null,
							null, oldStatoPackage,
							requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime);
					
				}
				
				// aggiunta campi custom
				dati = apsHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
						ForwardParams.CHANGE());

			}

			// I dati dell'utente sono validi, se ha scelto di modificare lo stato da finale ad operativo visualizzo la schermata di conferma
			if( actionConfirm == null){
				if(  backToStato != null){
					// setto la barra del titolo
					List<Parameter> lstParm = new ArrayList<Parameter>();

					if(gestioneFruitori) {
						lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
					}
					else {
						lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
					}
					lstParm.add(new Parameter(tmpTitle, null));

					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, lstParm );

					portType = (portType != null && !"".equals(portType) ? portType : asps.getPortType());

					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());
					
					// update della configurazione 
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties,
							this.registryReader, idAps);
					
					dati = apsHelper.addHiddenFieldsToDati(tipoOp, id, null, null, dati);

					dati = apsHelper.addServiziToDati(dati, nomeservizio, tiposervizio, oldnomeservizio, oldtiposervizio, 
							provider, tipoSoggettoErogatore, nomeSoggettoErogatore, soggettiList, 
							soggettiListLabel, accordo, serviceBinding,formatoSpecifica, 
							accordiList, accordiListLabel, servcorr, this.wsdlimpler, this.wsdlimplfru, tipoOp, 
							id, tipiServizioCompatibiliAccordo, profilo, portType, ptList, privato,uriAccordo, descrizione, soggettoErogatoreID.getId(),
							statoPackage,oldStatoPackage,versione,versioniProtocollo,validazioneDocumenti,
							null,null,generaPACheckSoggetto,asParteComuneCompatibili,
							null,null,null,null,false,
							null,null,null,null,null,null,null,
							tipoProtocollo,null,
							null, null, providerSoggettoFruitore, tipoSoggettoFruitore, nomeSoggettoFruitore,
							null, null, null, null, null,
							null, null, null, null,
							null);

					if(multiTenant || apsHelper.isModalitaCompleta() || (!soggettoOperativo && !gestioneFruitori)) {
						boolean forceEnableConnettore = false;
						if( apsHelper.isModalitaStandard() && !TipiConnettore.DISABILITATO.getNome().equals(endpointtype) ) {
							forceEnableConnettore = true;
						}
						
						dati = apsHelper.addEndPointToDati(dati, connettoreDebug,  endpointtype, autenticazioneHttp, 
								(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX,
								url, nome,
								tipo, user, password, initcont, urlpgk, provurl,
								connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp, httpsurl, httpstipologia,
								httpshostverify, httpspath, httpstipo, httpspwd,
								httpsalgoritmo, httpsstato, httpskeystore,
								httpspwdprivatekeytrust, httpspathkey,
								httpstipokey, httpspwdkey, httpspwdprivatekey,
								httpsalgoritmokey, tipoconn,AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
								nomeservizio, tiposervizio, null, null, null,
								null, oldStatoPackage, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
								opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
								requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
								responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
								listExtendedConnettore, forceEnableConnettore);
						
					}
					
					dati = apsHelper.addServiziToDatiAsHidden(dati, nomeservizio, tiposervizio, provider, tipoSoggettoErogatore, nomeSoggettoErogatore, soggettiList,
							soggettiListLabel, accordo, serviceBinding,accordiList, accordiListLabel, servcorr, "", "", tipoOp, 
							id, tipiServizioCompatibiliAccordo, profilo, portType, ptList, privato,uriAccordo, descrizione, soggettoErogatoreID.getId(),
							statoPackage,oldStatoPackage,versione,versioniProtocollo,validazioneDocumenti,
							null,null,tipoProtocollo,generaPACheckSoggetto);

					dati = apsHelper.addEndPointToDatiAsHidden(dati, endpointtype, url, nome,
							tipo, user, password, initcont, urlpgk, provurl,
							connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp, httpsurl, httpstipologia,
							httpshostverify, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey, httpspwdprivatekey,
							httpsalgoritmokey, tipoconn,AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
							nomeservizio, tiposervizio, null, null, null,
							null, oldStatoPackage,
							requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime
							);
					
					// backtostato per chiudere la modifica dopo la conferma
					DataElement de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
					de.setValue(backToStato);
					de.setType(DataElementType.HIDDEN);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RIPRISTINA_STATO);
					dati.addElement(de);
					
					// aggiunta campi custom
					dati = apsHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
					
					String msg = "&Egrave; stato richiesto di ripristinare lo stato dell''accordo [{0}] in operativo. Tale operazione permetter&agrave; successive modifiche all''accordo. Vuoi procedere?";
					
					String pre = Costanti.HTML_MODAL_SPAN_PREFIX;
					String post = Costanti.HTML_MODAL_SPAN_SUFFIX;
					pd.setMessage(pre + MessageFormat.format(msg, uriAccordo) + post, Costanti.MESSAGE_TYPE_CONFIRM);
					
					pd.setDati(dati);
					
					String[][] bottoni = { 
							{ Costanti.LABEL_MONITOR_BUTTON_ANNULLA, 
								Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_PREFIX +
								Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_SUFFIX

							},
							{ Costanti.LABEL_MONITOR_BUTTON_CONFERMA,
								Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_PREFIX +
								Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_SUFFIX }};

					pd.setBottoni(bottoni);
					
					// disabilito la form
					pd.disableEditMode();

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
							ForwardParams.CHANGE());
				}
			}

			// Modifico i dati del servizio nel db

			asps = apsCore.getAccordoServizioParteSpecifica(Long.parseLong(id));

			// idErogatoreServizio
			Soggetto soggettoErogatore = soggettiCore.getSoggettoRegistro(new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()));
			
			// nuovi valori
			asps.setNome(nomeservizio);
			asps.setTipo(tiposervizio);
			asps.setDescrizione(descrizione);
			if ("-".equals(profilo) == false)
				asps.setVersioneProtocollo(profilo);
			else
				asps.setVersioneProtocollo(null);

			asps.setPrivato(privato);

			if (portType != null && !"".equals(portType) && !"-".equals(portType))
				asps.setPortType(portType);
			else
				asps.setPortType(null);

			// Connettore
			Connettore newConnettore = new Connettore();
			newConnettore.setId(asps.getConfigurazioneServizio().getConnettore().getId());
			newConnettore.setNome(asps.getConfigurazioneServizio().getConnettore().getNome());
			if (endpointtype.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM))
				newConnettore.setTipo(tipoconn);
			else
				newConnettore.setTipo(endpointtype);


			String oldConnT = asps.getConfigurazioneServizio().getConnettore().getTipo();
			if ((asps.getConfigurazioneServizio().getConnettore().getCustom()!=null && asps.getConfigurazioneServizio().getConnettore().getCustom()) && 
					!asps.getConfigurazioneServizio().getConnettore().getTipo().equals(CostantiDB.CONNETTORE_TIPO_HTTPS) && 
					!asps.getConfigurazioneServizio().getConnettore().getTipo().equals(CostantiDB.CONNETTORE_TIPO_FILE)){

				oldConnT = ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM;
				// mantengo vecchie proprieta connettore custom
				for(int i=0; i<asps.getConfigurazioneServizio().getConnettore().sizePropertyList(); i++){
					newConnettore.addProperty(asps.getConfigurazioneServizio().getConnettore().getProperty(i));
				}
			}
			apsHelper.fillConnettore(newConnettore, connettoreDebug, endpointtype, oldConnT,
					tipoconn, url, nome,
					tipo, user, password, initcont, urlpgk, provurl,
					connfact, sendas, httpsurl, httpstipologia,
					httpshostverify, httpspath, httpstipo, httpspwd,
					httpsalgoritmo, httpsstato, httpskeystore,
					httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey,
					proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
					opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
					requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					listExtendedConnettore);

			asps.getConfigurazioneServizio().setConnettore(newConnettore);

			// Accordo
			as = apcCore.getAccordoServizio(Long.parseLong(accordo));
			asps.setAccordoServizioParteComune(idAccordoFactory.getUriFromAccordo(as));
			asps.setIdAccordo(as.getId());

			asps.setTipologiaServizio(((servcorr != null) && servcorr.equals(Costanti.CHECK_BOX_ENABLED)) ? TipologiaServizio.CORRELATO : TipologiaServizio.NORMALE);

			IDServizio oldIDServizioForUpdate = 
					IDServizioFactory.getInstance().getIDServizioFromValues(oldtiposervizio, oldnomeservizio,
							oldtiposoggetto, oldnomesoggetto, 
							(oldversioneaccordo!=null && !"".equals(oldversioneaccordo)) ? Integer.parseInt(oldversioneaccordo) : 1);
			asps.setOldIDServizioForUpdate(oldIDServizioForUpdate);
			
			// Versione
			if(apsCore.isSupportatoVersionamentoAccordiServizioParteSpecifica(tipoProtocollo)){
				if(versione!=null && !"".equals(versione)){
					asps.setVersione(Integer.parseInt(versione));
				}
				else{
					asps.setVersione(1);
				}
			}else{
				asps.setVersione(1);
			}

			// stato
			asps.setStatoPackage(statoPackage);


			// Check stato
			if(apsCore.isShowGestioneWorkflowStatoDocumenti()){

				try{
					boolean gestioneWsdlImplementativo = apcCore.showPortiAccesso(tipoProtocollo, serviceBinding, formatoSpecifica);
					boolean checkConnettore = !gestioneFruitori && !gestioneErogatori;
					apsCore.validaStatoAccordoServizioParteSpecifica(asps, gestioneWsdlImplementativo, checkConnettore);
				}catch(ValidazioneStatoPackageException validazioneException){

					// Setto messaggio di errore
					pd.setMessage(validazioneException.toString());

					// setto la barra del titolo
					List<Parameter> lstParm = new ArrayList<Parameter>();

					if(gestioneFruitori) {
						lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
					}
					else {
						lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
					}
					lstParm.add(new Parameter(tmpTitle, null));

					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, lstParm );

					portType = (portType != null && !"".equals(portType) ? portType : asps.getPortType());

					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());
					
					// update della configurazione 
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties,
							this.registryReader, idAps);

					dati = apsHelper.addHiddenFieldsToDati(tipoOp, id, null, null, dati);

					dati = apsHelper.addServiziToDati(dati, nomeservizio, tiposervizio, oldnomeservizio, oldtiposervizio, 
							provider, tipoSoggettoErogatore, nomeSoggettoErogatore, soggettiList,
							soggettiListLabel, accordo, serviceBinding,formatoSpecifica, 
							accordiList, accordiListLabel, servcorr, this.wsdlimpler, this.wsdlimplfru, tipoOp, 
							id, tipiServizioCompatibiliAccordo, profilo, portType, ptList, privato,uriAccordo, descrizione, 
							soggettoErogatoreID.getId(),statoPackage,oldStatoPackage,versione,versioniProtocollo,validazioneDocumenti,
							null,null,generaPACheckSoggetto,asParteComuneCompatibili,
							null,null,null,null,false,
							null,null,null,null,null,null,null,
							tipoProtocollo,null,
							null, null, providerSoggettoFruitore, tipoSoggettoFruitore, nomeSoggettoFruitore,
							null, null, null, null, null,
							null, null, null, null,
							null);

					if(multiTenant || apsHelper.isModalitaCompleta() || (!soggettoOperativo && !gestioneFruitori)) {
					
						boolean forceEnableConnettore = false;
//						if( apsHelper.isModalitaStandard() && !TipiConnettore.DISABILITATO.getNome().equals(endpointtype) ) {
//							forceEnableConnettore = true;
//						}
						
						dati = apsHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, 
								(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX,
								url,
								nome, tipo, user, password, initcont, urlpgk,
								provurl, connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp, httpsurl,
								httpstipologia, httpshostverify, httpspath,
								httpstipo, httpspwd, httpsalgoritmo, httpsstato,
								httpskeystore, httpspwdprivatekeytrust,
								httpspathkey, httpstipokey, httpspwdkey,
								httpspwdprivatekey, httpsalgoritmokey, tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
								nomeservizio, tiposervizio, null, null, null,
								null,
								oldStatoPackage, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
								opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
								requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
								responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
								listExtendedConnettore, forceEnableConnettore);
						
					}
					else {
						
						dati = apsHelper.addEndPointToDatiAsHidden(dati,
								endpointtype, url, nome, tipo,
								user, password, initcont, urlpgk,
								provurl, connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp,
								httpsurl, httpstipologia, httpshostverify,
								httpspath, httpstipo, httpspwd,
								httpsalgoritmo, httpsstato, httpskeystore,
								httpspwdprivatekeytrust, httpspathkey,
								httpstipokey, httpspwdkey,
								httpspwdprivatekey, httpsalgoritmokey,
								tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
								nomeservizio, tiposervizio, null, null, null,
								null, oldStatoPackage,
								requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
								responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime);
						
					}
					
					// aggiunta campi custom
					dati = apsHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardGeneralError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
							ForwardParams.CHANGE());
				}
			}


			String superUser = ServletUtils.getUserLoginFromSession(session);

			// Se sono cambiati il tipo o il nome allora devo aggiornare
			// anche le porte delegate e porte applicative
			List<PortaDelegata> listaPD = new ArrayList<PortaDelegata>();
			List<PortaApplicativa> listaPA = new ArrayList<PortaApplicativa>();
			// Lista di id per tenere traccia delle Porte Delegate inserite
			List<Long> idListPD = new ArrayList<Long>();
			List<Long> idListPA = new ArrayList<Long>();
			String newUri = IDServizioFactory.getInstance().getUriFromAccordo(asps);
			String oldUri = IDServizioFactory.getInstance().getUriFromIDServizio(asps.getOldIDServizioForUpdate());
			if (!newUri.equals(oldUri)) {
				List<PortaDelegata> tmpListPD = null;
				// recupero lo porte delegate per nome e le aggiorno
				String locationPrefix = "";
				String locationSuffix = "/" + asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo() + asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome() + 
						"/" + asps.getOldIDServizioForUpdate().getTipo() + asps.getOldIDServizioForUpdate().getNome() +
						"/" + asps.getOldIDServizioForUpdate().getVersione().intValue();
				for (Fruitore fruitore : asps.getFruitoreList()) {
					locationPrefix = fruitore.getTipo() + fruitore.getNome();
					String location = locationPrefix + locationSuffix;
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(location);
					PortaDelegata tmpPorta = porteDelegateCore.getPortaDelegata(idPD);
					if(tmpPorta!=null){
						Long idPorta = tmpPorta.getId();
						if (!idListPD.contains(idPorta)) {
							// new locationSuffix
							String newLocationSuffix = "/" + asps.getTipoSoggettoErogatore() + asps.getNomeSoggettoErogatore() + 
									"/" + asps.getTipo() + asps.getNome() +
									"/" + asps.getVersione().intValue();
							String newLocation = locationPrefix + newLocationSuffix;
							idListPD.add(idPorta);
							IDPortaDelegata oldIDPortaDelegataForUpdate = new IDPortaDelegata();
							oldIDPortaDelegataForUpdate.setNome(tmpPorta.getNome());
							tmpPorta.setOldIDPortaDelegataForUpdate(oldIDPortaDelegataForUpdate);
							tmpPorta.setNome(newLocation);
							// aggiorno la descrizione della porta
							String descrizionePD = tmpPorta.getDescrizione();
							if (descrizionePD != null && !descrizionePD.equals("")) {
								// pattern descrizione: Invocazione
								// servizio(.*)erogato da(.*) (old
								// tipo/nome soggetto)
								String descrRegex = "Invocazione servizio(.*)erogato da(.*)";
								if (descrizionePD.matches(descrRegex)) {
									descrizionePD = descrizionePD.replaceFirst((asps.getOldIDServizioForUpdate().getTipo() + asps.getOldIDServizioForUpdate().getNome()+":"+asps.getOldIDServizioForUpdate().getVersione().intValue()), 
											(asps.getTipo() + asps.getNome()+":"+asps.getVersione().intValue()));
								}

								tmpPorta.setDescrizione(descrizionePD);
							}
							// aggiorno anche il servizio
							PortaDelegataServizio servizioPD = tmpPorta.getServizio();
							servizioPD.setTipo(asps.getTipo());
							servizioPD.setNome(asps.getNome());
							servizioPD.setVersione(asps.getVersione());
							tmpPorta.setServizio(servizioPD);

							/*
							 * CONTROLLO PATTERN AZIONE inoltre va
							 * controllato anche il pattern dell'azione
							 * in caso il pattern azione fosse URLBASED
							 * e fosse quello di default allora va
							 * cambiato
							 */
							String regex = "(.*)\\/(.*)\\/(.*)";
							PortaDelegataAzione pdAzione = tmpPorta.getAzione();
							PortaDelegataAzioneIdentificazione identificazione = pdAzione != null ? pdAzione.getIdentificazione() : null;
							String patterAzione = pdAzione != null ? (pdAzione.getPattern() != null ? pdAzione.getPattern() : "") : "";
							String patternAzionePrefix = ".*";
							String patternAzioneSuffix = "/([^/|^?]*).*";
							// se identificazione urlbased procedo con i
							// controlli
							if (PortaDelegataAzioneIdentificazione.URL_BASED.equals(identificazione)) {
								if (patterAzione.startsWith(patternAzionePrefix) && patterAzione.endsWith(patternAzioneSuffix)) {
									int startidx = patternAzionePrefix.length();
									int endidx = patterAzione.lastIndexOf(patternAzioneSuffix);
									String tmp = patterAzione.substring(startidx, endidx);
									// a questo punto ottengo una
									// stringa del tipo
									// (fruitore)/(erogatore)/(servizio)
									// se rispetta la regex allora vuol
									// dire che il pattern azione e'
									// quello di default
									// e devo effettuare i cambiamenti
									if (tmp.matches(regex)) {
										// il nuovo pattern sara' come
										// quello della location di
										// default
										String newPatternAzione = patternAzionePrefix + newLocation + patternAzioneSuffix;
										pdAzione.setPattern(newPatternAzione);
										tmpPorta.setAzione(pdAzione);

									}
								}
							}// fine controllo azione

							listaPD.add(tmpPorta);

						}
					}
				}
				// recupero le porte delegate per id servizio
				// aggiorno il tipo e il nome servizio
				tmpListPD = porteDelegateCore.getPorteDelegateWithServizio(asps.getId());
				for (PortaDelegata tmpPorta : tmpListPD) {
					Long idPorta = tmpPorta.getId();
					// se la porta non e' gia in lista
					if (!idListPD.contains(idPorta)) {
						idListPD.add(idPorta);
						IDPortaDelegata oldIDPortaDelegataForUpdate = new IDPortaDelegata();
						oldIDPortaDelegataForUpdate.setNome(tmpPorta.getNome());
						tmpPorta.setOldIDPortaDelegataForUpdate(oldIDPortaDelegataForUpdate);
						PortaDelegataServizio servizioPD = tmpPorta.getServizio();
						servizioPD.setTipo(asps.getTipo());
						servizioPD.setNome(asps.getNome());
						servizioPD.setVersione(asps.getVersione());
						tmpPorta.setServizio(servizioPD);
						
						String nomeGeneratoAutomaticamente = tmpPorta.getTipoSoggettoProprietario()+tmpPorta.getNomeSoggettoProprietario()+ "/" + 
								asps.getTipoSoggettoErogatore() + asps.getNomeSoggettoErogatore() + "/" + 
								asps.getOldIDServizioForUpdate().getTipo() + asps.getOldIDServizioForUpdate().getNome();
						String nomeAttuale = tmpPorta.getNome();
						if(nomeAttuale.startsWith(nomeGeneratoAutomaticamente+"/")){					
							// La PD è stata creata manualmente, e nel nome è stato indicato un pattern simile a quello standard all'inizio.
							// in tal caso viene effettuato lo switch del nome.
							String rimanente = "";
							int lengthParteRimanente = (nomeGeneratoAutomaticamente+"/").length();
							if(nomeAttuale.length()>lengthParteRimanente){
								rimanente = nomeAttuale.substring(lengthParteRimanente);
							}						
							String newNome = tmpPorta.getTipoSoggettoProprietario()+tmpPorta.getNomeSoggettoProprietario()+ "/" + 
									asps.getTipoSoggettoErogatore()+asps.getNomeSoggettoErogatore()+"/"+
									asps.getTipo() + asps.getNome()+"/"+rimanente;
							oldIDPortaDelegataForUpdate.setNome(nomeAttuale);
							tmpPorta.setOldIDPortaDelegataForUpdate(oldIDPortaDelegataForUpdate);
							tmpPorta.setNome(newNome);
							
							PortaDelegataAzione pdAzione = tmpPorta.getAzione();
							PortaDelegataAzioneIdentificazione identificazione = pdAzione != null ? pdAzione.getIdentificazione() : null;
							String patterAzione = pdAzione != null ? (pdAzione.getPattern() != null ? pdAzione.getPattern() : "") : "";
							// se identificazione urlbased procedo con i
							// controlli
							if (PortaDelegataAzioneIdentificazione.URL_BASED.equals(identificazione)) {
								if (patterAzione.contains(nomeAttuale)) {
									String newPatternAzione = patterAzione.replace(nomeAttuale, newNome);
									pdAzione.setPattern(newPatternAzione);
								}
							}// fine controllo azione
						}
						
						listaPD.add(tmpPorta);
					}
				}

				// recupero le porte delegate per tipo e nome servizio
				// aggiorno il tipo e il nome servizio
				tmpListPD = porteDelegateCore.getPorteDelegateWithServizio(new Long(0),
						asps.getOldIDServizioForUpdate().getTipo(), asps.getOldIDServizioForUpdate().getNome(), asps.getOldIDServizioForUpdate().getVersione(), 
						soggettoErogatore.getId(), asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore());
				for (PortaDelegata tmpPorta : tmpListPD) {
					Long idPorta = tmpPorta.getId();
					if (!idListPD.contains(idPorta)) {
						idListPD.add(idPorta);
						IDPortaDelegata oldIDPortaDelegataForUpdate = new IDPortaDelegata();
						oldIDPortaDelegataForUpdate.setNome(tmpPorta.getNome());
						tmpPorta.setOldIDPortaDelegataForUpdate(oldIDPortaDelegataForUpdate);
						PortaDelegataServizio servizioPD = tmpPorta.getServizio();
						servizioPD.setTipo(asps.getTipo());
						servizioPD.setNome(asps.getNome());
						servizioPD.setVersione(asps.getVersione());
						tmpPorta.setServizio(servizioPD);
						
						String nomeGeneratoAutomaticamente = tmpPorta.getTipoSoggettoProprietario()+tmpPorta.getNomeSoggettoProprietario()+ "/" + 
								asps.getTipoSoggettoErogatore() + asps.getNomeSoggettoErogatore() + "/" + 
								asps.getOldIDServizioForUpdate().getTipo() + asps.getOldIDServizioForUpdate().getNome();
						String nomeAttuale = tmpPorta.getNome();
						if(nomeAttuale.startsWith(nomeGeneratoAutomaticamente+"/")){					
							// La PD è stata creata manualmente, e nel nome è stato indicato un pattern simile a quello standard all'inizio.
							// in tal caso viene effettuato lo switch del nome.
							String rimanente = "";
							int lengthParteRimanente = (nomeGeneratoAutomaticamente+"/").length();
							if(nomeAttuale.length()>lengthParteRimanente){
								rimanente = nomeAttuale.substring(lengthParteRimanente);
							}						
							String newNome = tmpPorta.getTipoSoggettoProprietario()+tmpPorta.getNomeSoggettoProprietario()+ "/" + 
									asps.getTipoSoggettoErogatore()+asps.getNomeSoggettoErogatore()+"/"+
									asps.getTipo() + asps.getNome()+"/"+rimanente;
							oldIDPortaDelegataForUpdate.setNome(nomeAttuale);
							tmpPorta.setOldIDPortaDelegataForUpdate(oldIDPortaDelegataForUpdate);
							tmpPorta.setNome(newNome);
							
							PortaDelegataAzione pdAzione = tmpPorta.getAzione();
							PortaDelegataAzioneIdentificazione identificazione = pdAzione != null ? pdAzione.getIdentificazione() : null;
							String patterAzione = pdAzione != null ? (pdAzione.getPattern() != null ? pdAzione.getPattern() : "") : "";
							// se identificazione urlbased procedo con i
							// controlli
							if (PortaDelegataAzioneIdentificazione.URL_BASED.equals(identificazione)) {
								if (patterAzione.contains(nomeAttuale)) {
									String newPatternAzione = patterAzione.replace(nomeAttuale, newNome);
									pdAzione.setPattern(newPatternAzione);
								}
							}// fine controllo azione
						}
						
						listaPD.add(tmpPorta);
					}

				}

				// recupero le porte applicative per id
				List<PortaApplicativa> tmpListPA = porteApplicativeCore.porteAppWithIdServizio(asps.getId());
				for (PortaApplicativa portaApplicativa : tmpListPA) {
					Long idPA = portaApplicativa.getId();
					if (!idListPA.contains(idPA)) {
						
						idListPA.add(idPA);
						
						PortaApplicativaServizio paServizio = portaApplicativa.getServizio();
						paServizio.setNome(asps.getNome());
						paServizio.setTipo(asps.getTipo());
						paServizio.setVersione(asps.getVersione());
						portaApplicativa.setServizio(paServizio);
						
						String nomePA = portaApplicativa.getNome();
						// se il nome e' quello di default cioe' (erogatore)/(servizio)/(versioneServizio)
						String regex = "(.*)\\/(.*)\\/(.*)";
						if (nomePA.matches(regex)) {

							// Check che siamo nel caso di nome generato automaticamente
							String nomeGeneratoAutomaticamente = asps.getTipoSoggettoErogatore()+asps.getNomeSoggettoErogatore()+"/"+
									asps.getOldIDServizioForUpdate().getTipo() + asps.getOldIDServizioForUpdate().getNome();
							if(nomeGeneratoAutomaticamente.equals(nomePA)){
								String newNome = asps.getTipoSoggettoErogatore()+asps.getNomeSoggettoErogatore()+"/"+
										asps.getTipo() + asps.getNome();
								
								portaApplicativa.setNome(newNome);
								IDPortaApplicativa oldIDPortaApplicativaForUpdate = new IDPortaApplicativa();
								oldIDPortaApplicativaForUpdate.setNome(nomePA);
								portaApplicativa.setOldIDPortaApplicativaForUpdate(oldIDPortaApplicativaForUpdate);
								
								// modifica della descrizione
								String descrizionePA = portaApplicativa.getDescrizione();
								if (descrizionePA != null && !descrizionePA.equals("")) {

									// caso 1
									// pattern descrizione: Invocazione
									// servizio(.*)erogato da(.*) (pat1)
									String descrRegex = "Servizio(.*)erogato da(.*)";
									if (descrizionePA.matches(descrRegex)) {
										descrizionePA = descrizionePA.replaceFirst((asps.getOldIDServizioForUpdate().getTipo() + asps.getOldIDServizioForUpdate().getNome()), 
												(asps.getTipo() + asps.getNome()));
									}

									portaApplicativa.setDescrizione(descrizionePA);
								}
							}
							else if(nomePA.startsWith(nomeGeneratoAutomaticamente+"/")){
								
								// La PA è stata creata manualmente, e nel nome è stato indicato un pattern simile a quello standard all'inizio.
								// in tal caso viene effettuato lo switch del nome.
								
								String rimanente = "";
								int lengthParteRimanente = (nomeGeneratoAutomaticamente+"/").length();
								if(nomePA.length()>lengthParteRimanente){
									rimanente = nomePA.substring(lengthParteRimanente);
								}
								
								String newNome = asps.getTipoSoggettoErogatore()+asps.getNomeSoggettoErogatore()+"/"+
										asps.getTipo() + asps.getNome()+"/"+rimanente;
								
								portaApplicativa.setNome(newNome);
								IDPortaApplicativa oldIDPortaApplicativaForUpdate = new IDPortaApplicativa();
								oldIDPortaApplicativaForUpdate.setNome(nomePA);
								portaApplicativa.setOldIDPortaApplicativaForUpdate(oldIDPortaApplicativaForUpdate);
								
							}
							
						}// fine controllo nome
						
						listaPA.add(portaApplicativa);
					}
				}
				// recupero porte applicative per tipo/nome servizio
				tmpListPA = porteApplicativeCore.porteAppWithServizio(soggettoErogatore.getId(), 
						asps.getOldIDServizioForUpdate().getTipo(),
						asps.getOldIDServizioForUpdate().getNome(),
						asps.getOldIDServizioForUpdate().getVersione());
				for (PortaApplicativa portaApplicativa : tmpListPA) {
					Long idPA = portaApplicativa.getId();
					if (!idListPA.contains(idPA)) {
						
						idListPA.add(idPA);
												
						PortaApplicativaServizio paServizio = portaApplicativa.getServizio();
						paServizio.setNome(asps.getNome());
						paServizio.setTipo(asps.getTipo());
						paServizio.setVersione(asps.getVersione());
						portaApplicativa.setServizio(paServizio);
						
						String nomePA = portaApplicativa.getNome();
						// se il nome e' quello di default cioe' (erogatore)/(servizio)/(versioneServizio)
						String regex = "(.*)\\/(.*)\\/(.*)";
						if (nomePA.matches(regex)) {

							String[] val = nomePA.split("\\/");
							String pat1 = val[0];
							String pat2 = val[1];
							String pat3= val[2];

							// servizio
							if (pat2.equals(asps.getOldIDServizioForUpdate().getTipo() + asps.getOldIDServizioForUpdate().getNome())) {
								pat2 = asps.getTipo() + asps.getNome();
							}
							
							// versioneServizio
							if(pat3.equals(asps.getOldIDServizioForUpdate().getVersione().intValue()+"")){
								pat3 = asps.getVersione().intValue()+"";
							}

							String newNome = pat1 + "/" + pat2 + "/" + pat3;
							
							portaApplicativa.setNome(newNome);
							IDPortaApplicativa oldIDPortaApplicativaForUpdate = new IDPortaApplicativa();
							oldIDPortaApplicativaForUpdate.setNome(nomePA);
							portaApplicativa.setOldIDPortaApplicativaForUpdate(oldIDPortaApplicativaForUpdate);

							// modifica della descrizione
							String descrizionePA = portaApplicativa.getDescrizione();
							if (descrizionePA != null && !descrizionePA.equals("")) {

								// caso 1
								// pattern descrizione: Invocazione
								// servizio(.*)erogato da(.*) (pat1)
								String descrRegex = "Servizio(.*)erogato da(.*)";
								if (descrizionePA.matches(descrRegex)) {
									descrizionePA = descrizionePA.replaceFirst((asps.getOldIDServizioForUpdate().getTipo() + asps.getOldIDServizioForUpdate().getNome()+":"+asps.getOldIDServizioForUpdate().getVersione().intValue()), 
											(asps.getTipo() + asps.getNome()+":"+asps.getVersione().intValue()));
								}

								portaApplicativa.setDescrizione(descrizionePA);
							}
						}// fine controllo nome
						
						listaPA.add(portaApplicativa);
					}
				}
			}

			List<Object> oggettiDaAggiornare = new ArrayList<Object>();

			// Aggiorno il servizio
			oggettiDaAggiornare.add(asps);

			// Aggiorno le eventuali porte delegate
			for (PortaDelegata portaDelegata : listaPD) {
				oggettiDaAggiornare.add(portaDelegata);
			}

			// aggiorno le eventuali porte applicative
			for (PortaApplicativa portaApplicativa : listaPA) {
				oggettiDaAggiornare.add(portaApplicativa);
			}

			// Se ho cambiato i dati significativi del servizio devo effettuare anche l'update degli accordi di servizio
			// che includono questi servizi come servizi componenti.
			if (!newUri.equals(oldUri)) {

				IDServizio idServizioOLD =  asps.getOldIDServizioForUpdate();
				String uriOLD = IDServizioFactory.getInstance().getUriFromIDServizio(idServizioOLD);
				List<AccordoServizioParteComune> ass = apcCore.accordiServizio_serviziComponenti(idServizioOLD);
				for(int i=0; i<ass.size(); i++){
					AccordoServizioParteComune accordoServizioComposto = ass.get(i);
					if(accordoServizioComposto.getServizioComposto()!=null){
						for(int j=0;j<accordoServizioComposto.getServizioComposto().sizeServizioComponenteList();j++){
							IDServizio idServizioComponente = IDServizioFactory.getInstance().
									getIDServizioFromValues(accordoServizioComposto.getServizioComposto().getServizioComponente(j).getTipo(), accordoServizioComposto.getServizioComposto().getServizioComponente(j).getNome(), 
											accordoServizioComposto.getServizioComposto().getServizioComponente(j).getTipoSoggetto(),accordoServizioComposto.getServizioComposto().getServizioComponente(j).getNomeSoggetto(), 
											accordoServizioComposto.getServizioComposto().getServizioComponente(j).getVersione());
							String uriServizioComponente = IDServizioFactory.getInstance().getUriFromIDServizio(idServizioComponente);
							if(uriServizioComponente.equals(uriOLD)){
								accordoServizioComposto.getServizioComposto().getServizioComponente(j).setTipoSoggetto(asps.getTipoSoggettoErogatore());
								accordoServizioComposto.getServizioComposto().getServizioComponente(j).setNomeSoggetto(asps.getNomeSoggettoErogatore());
								accordoServizioComposto.getServizioComposto().getServizioComponente(j).setTipo(asps.getTipo());
								accordoServizioComposto.getServizioComposto().getServizioComponente(j).setNome(asps.getNome());
								accordoServizioComposto.getServizioComposto().getServizioComponente(j).setVersione(asps.getVersione());
							}
						}
						oggettiDaAggiornare.add(accordoServizioComposto);
						//System.out.println("As SERVIZIO COMPONENTE ["+IDAccordo.getUriFromAccordo(accordoServizioComposto)+"]");
					}
				}
			}
			
			//imposto properties custom
			asps.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolProperties(this.protocolProperties, this.consoleOperationType, oldProtocolPropertyList));

			// eseguo l'aggiornamento
			apsCore.performUpdateOperation(superUser, apsHelper.smista(), oggettiDaAggiornare.toArray());

			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			//				PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();
			//				
			//				boolean [] permessi = new boolean[2];
			//				permessi[0] = pu.isServizi();
			//				permessi[1] = pu.isAccordiCooperazione();

			List<AccordoServizioParteSpecifica> listaServizi = null;
			if(apsCore.isVisioneOggettiGlobale(superUser)){
				listaServizi = apsCore.soggettiServizioList(null, ricerca,permessi, gestioneFruitori);
			}else{
				listaServizi = apsCore.soggettiServizioList(superUser, ricerca,permessi, gestioneFruitori);
			}

			apsHelper.prepareServiziList(ricerca, listaServizi);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
					ForwardParams.CHANGE());
		}  

	}
}