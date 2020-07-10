/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
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
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.Subscription;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.ApiKeyState;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * serviziFruitoriAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaFruitoriAdd extends Action {

	private String connettoreDebug;
	private String    id, idSoggettoFruitore, endpointtype, tipoconn, url, nome, tipo, user,
	password, initcont, urlpgk, provurl, connfact, sendas, 
	httpsurl, httpstipologia, httpspath,
	httpstipo, httpspwd, httpsalgoritmo,
	httpskeystore, httpspwdprivatekeytrust, httpspathkey,
	httpstipokey, httpspwdkey, httpspwdprivatekey,
	httpsalgoritmokey,
	httpsKeyAlias,
	httpsTrustStoreCRLs;
	private String httpshostverifyS, httpsstatoS;
	private boolean httpshostverify, httpsstato;
	private String statoPackage;
	private boolean validazioneDocumenti = true;
	private boolean decodeRequestValidazioneDocumenti = false;
	private String editMode = null;
	private String correlato = null;
	private String autenticazioneHttp;
	private String controlloAccessiStato;
	private String fruizioneServizioApplicativo;
	private String fruizioneRuolo;
	private String fruizioneAutenticazione;
	private String fruizioneAutenticazioneOpzionale;
	private TipoAutenticazionePrincipal fruizioneAutenticazionePrincipal;
	private List<String> fruizioneAutenticazioneParametroList;
	private String fruizioneAutorizzazione;
	private String fruizioneAutorizzazioneAutenticati, fruizioneAutorizzazioneRuoli, fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch;
	private Properties parametersPOST;

	private boolean autenticazioneToken = false;
	private String token_policy = null;
	
	private String proxy_enabled, proxy_hostname,proxy_port,proxy_username,proxy_password;
	
	private String tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta;

	private String transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop, opzioniAvanzate;
	
	// file
	private String requestOutputFileName = null;
	private String requestOutputFileNameHeaders = null;
	private String requestOutputParentDirCreateIfNotExists = null;
	private String requestOutputOverwriteIfExists = null;
	private String responseInputMode = null;
	private String responseInputFileName = null;
	private String responseInputFileNameHeaders = null;
	private String responseInputDeleteAfterRead = null;
	private String responseInputWaitTime = null;
	

	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory= null;
	private IRegistryReader registryReader = null; 
	private IConfigIntegrationReader configRegistryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	
	private BinaryParameter wsdlimpler, wsdlimplfru;


	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String superUser =  ServletUtils.getUserLoginFromSession(session);

		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		this.consoleOperationType = ConsoleOperationType.ADD;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.ADD;

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			
			this.parametersPOST = null;

			this.editMode = apsHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);

			this.id = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			this.idSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE);
			this.correlato = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO);
			//			this.endpointtype = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE );

			this.controlloAccessiStato = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO);
			
			this.fruizioneServizioApplicativo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUIZIONE_NOME_SA);
			this.fruizioneRuolo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_RUOLO);
			this.fruizioneAutenticazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE);
			this.fruizioneAutenticazioneOpzionale = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE_OPZIONALE);
			String fruizioneAutenticazionePrincipalTipo = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TIPO);
			this.fruizioneAutenticazionePrincipal = TipoAutenticazionePrincipal.toEnumConstant(fruizioneAutenticazionePrincipalTipo, false);
			this.fruizioneAutenticazioneParametroList = apsHelper.convertFromDataElementValue_parametroAutenticazioneList(this.fruizioneAutenticazione, this.fruizioneAutenticazionePrincipal);
			this.fruizioneAutorizzazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE);
			this.fruizioneAutorizzazioneAutenticati = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_AUTENTICAZIONE);
			this.fruizioneAutorizzazioneRuoli = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLI);
			this.fruizioneAutorizzazioneRuoliTipologia = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_TIPOLOGIA);
			this.fruizioneAutorizzazioneRuoliMatch = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_MATCH);
			
			String gestioneToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN);
			String gestioneTokenPolicy = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY);
			String gestioneTokenOpzionale = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_OPZIONALE);
			String gestioneTokenValidazioneInput = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT);
			String gestioneTokenIntrospection = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION);
			String gestioneTokenUserInfo = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO);
			String gestioneTokenTokenForward = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
			
			String autenticazioneTokenIssuer = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_ISSUER);
			String autenticazioneTokenClientId = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_CLIENT_ID);
			String autenticazioneTokenSubject = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_SUBJECT);
			String autenticazioneTokenUsername = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_USERNAME);
			String autenticazioneTokenEMail = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_MAIL);
			
			String autorizzazione_token = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
			String autorizzazione_tokenOptions = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
			String autorizzazioneScope = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
			String autorizzazioneScopeMatch = apsHelper.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
			String scope = apsHelper.getParameter(CostantiControlStation.PARAMETRO_SCOPE);
			
			BinaryParameter allegatoXacmlPolicy = apsHelper.getBinaryParameter(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
			
			this.endpointtype = apsHelper.readEndPointType();
			this.tipoconn = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO );
			this.autenticazioneHttp = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);

			this.connettoreDebug = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
			
			// token policy
			String autenticazioneTokenS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			this.autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			this.token_policy = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
			
			// proxy
			this.proxy_enabled = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			this.proxy_hostname = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			this.proxy_port = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			this.proxy_username = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			this.proxy_password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);

			// tempi risposta
			this.tempiRisposta_enabled = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			this.tempiRisposta_connectionTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			this.tempiRisposta_readTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			this.tempiRisposta_tempoMedioRisposta = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
			
			// opzioni avanzate
			this.transfer_mode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			this.transfer_mode_chunk_size = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			this.redirect_mode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			this.redirect_max_hop = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			this.opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(apsHelper, this.transfer_mode, this.redirect_mode);

			// http
			this.url = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL  );
			if(TipiConnettore.HTTP.toString().equals(this.endpointtype)){
				this.user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				this.password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}

			// jms
			this.nome = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			this.tipo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			this.initcont = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			this.urlpgk = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			this.provurl = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			this.connfact = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			this.sendas = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(this.endpointtype)){
				this.user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				this.password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}

			// https
			this.httpsurl = this.url;
			this.httpstipologia = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE );
			this.httpshostverifyS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			this.httpspath = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION );
			this.httpstipo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			this.httpspwd = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			this.httpsalgoritmo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			this.httpsstatoS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			this.httpskeystore = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			this.httpspwdprivatekeytrust = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			this.httpspathkey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			this.httpstipokey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			this.httpspwdkey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			this.httpspwdprivatekey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			this.httpsalgoritmokey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			this.httpsKeyAlias = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
			this.httpsTrustStoreCRLs = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
			if(TipiConnettore.HTTPS.toString().equals(this.endpointtype)){
				this.user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				this.password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// file
			this.requestOutputFileName = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			this.requestOutputFileNameHeaders = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			this.requestOutputParentDirCreateIfNotExists = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			this.requestOutputOverwriteIfExists = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			this.responseInputMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			this.responseInputFileName = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			this.responseInputFileNameHeaders = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			this.responseInputDeleteAfterRead = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			this.responseInputWaitTime = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);


			this.statoPackage = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);

			this.wsdlimpler = apsHelper.getBinaryParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
			this.wsdlimplfru = apsHelper.getBinaryParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE);

			if(apsHelper.isMultipart()){
				this.decodeRequestValidazioneDocumenti = true;
			}
			//			
			//			String ct = request.getContentType();
			//			if ((ct != null) && (ct.indexOf(Costanti.MULTIPART) != -1)) {
			//				// decodeReq = true;
			//				this.decodeRequestValidazioneDocumenti = false; // init
			//				this.decodeRequest(request,apsHelper);
			//			}


			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			PddCore pddCore = new PddCore(apsCore);
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(apsCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);

			if(ServletUtils.isEditModeInProgress(this.editMode)){
				// primo accesso alla servlet
				this.validazioneDocumenti = true;
				if (apsHelper.isModalitaAvanzata()) {
					String tmpValidazioneDocumenti = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
					if(tmpValidazioneDocumenti!=null){
						if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
							this.validazioneDocumenti = true;
						}else{
							this.validazioneDocumenti = false;
						}
					}
				}
			}else{
				if(!this.decodeRequestValidazioneDocumenti){
					String tmpValidazioneDocumenti = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
					if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
						this.validazioneDocumenti = true;
					}else{
						this.validazioneDocumenti = false;
					}
				}
			}

			this.httpshostverify = false;
			if (this.httpshostverifyS != null && this.httpshostverifyS.equals(Costanti.CHECK_BOX_ENABLED))
				this.httpshostverify = true;
			this.httpsstato = false;
			if (this.httpsstatoS != null && this.httpsstatoS.equals(Costanti.CHECK_BOX_ENABLED))
				this.httpsstato = true;

			int idInt = Integer.parseInt(this.id);

			Boolean isConnettoreCustomUltimaImmagineSalvata = null;

			Connettore conTmp = null;
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.FRUIZIONE_ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, apsHelper, 
							this.parametersPOST, (this.endpointtype==null), this.endpointtype); // uso endpointtype per capire se è la prima volta che entro

			// prendo l'id del soggetto erogatore lo propago
			// lo metto nel pd come campo hidden
			PageData oldPD = ServletUtils.getPageDataFromSession(session);
			pd.setHidden(oldPD.getHidden());

			String idSoggErogatore = oldPD.getHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);

			// Preparo il menu
			apsHelper.makeMenu();

			// Prendo nome e tipo dal db

			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Long.valueOf(idInt));
			String nomeservizio = asps.getNome();
			String tiposervizio = asps.getTipo();
			Integer versioneservizio = asps.getVersione();

			IDServizio idServizioObject = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
			idServizioObject.setUriAccordoServizioParteComune(asps.getAccordoServizioParteComune());
			idServizioObject.setPortType(asps.getPortType());
			
			IDFruizione idFruizione = new IDFruizione();
			idFruizione.setIdServizio(idServizioObject);
			
			
			if(this.correlato == null){
				this.correlato = ((TipologiaServizio.CORRELATO.equals(asps.getTipologiaServizio()) ?
						AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_CORRELATO :
							AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_NORMALE));
			}

			//String profiloSoggettoFruitore = null;
			//if ((this.provider != null) && !this.provider.equals("")) {
			//	long idFruitore = Long.parseLong(this.provider);
			//	Soggetto soggetto = soggettiCore.getSoggettoRegistro(idFruitore);
			//	profiloSoggettoFruitore = soggetto.getVersioneProtocollo();
			//}
			//String profiloValue = profiloSoggettoFruitore;
			//if(this.profilo!=null && !"".equals(this.profilo) && !"-".equals(this.profilo)){
			//	profiloValue = this.profilo;
			//}

			String protocollo = apsCore.getProtocolloAssociatoTipoServizio(tiposervizio);
			List<String> versioniProtocollo = apsCore.getVersioniProtocollo(protocollo);
			List<String> tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);

			String tmpTitle = apsHelper.getLabelIdServizio(asps);

			// Soggetti fruitori
			// tutti i soggetti anche il soggetto attuale
			// tranne quelli già registrati come fruitori
			String[] soggettiList = null;
			String[] soggettiListLabel = null;
			List<Fruitore> fruList1 = apsCore.getSoggettiWithServizioNotFruitori(idInt,true,null);
			Hashtable<String, Fruitore> mapFruitori = new Hashtable<>();
			List<String> keyFruitori = new ArrayList<>();
			if(fruList1!=null && fruList1.size()>0){
				for (Fruitore fr : fruList1) {
					String key = fr.getTipo()+""+fr.getNome();
					if(keyFruitori.contains(key)==false){
						keyFruitori.add(key);
						mapFruitori.put(key, fr);
					}
				}
			}
			if(keyFruitori.size()>0){
				Collections.sort(keyFruitori);
			}
			
			List<String> soggettiListVector = new ArrayList<String>();
			List<String> soggettiListLabelVector = new ArrayList<String>();
			IDSoggetto idSoggettoSelected = null;
			IDSoggetto idSoggettoFirst = null;
			for (int i = 0; i < keyFruitori.size(); i++) {
				String tipoNome = keyFruitori.get(i);
				Fruitore fru = mapFruitori.get(tipoNome);
				if(tipiSoggettiCompatibiliAccordo.contains(fru.getTipo())){
					soggettiListVector.add("" + fru.getId());
					soggettiListLabelVector.add(apsHelper.getLabelNomeSoggetto(protocollo, fru.getTipo() , fru.getNome()));
					if(idSoggettoFirst==null) {
						idSoggettoFirst = new IDSoggetto(fru.getTipo(), fru.getNome());
					}
					if(this.idSoggettoFruitore!=null && !"".equals(this.idSoggettoFruitore)){
						long idProvider = Long.parseLong(this.idSoggettoFruitore);
						if(fru.getId()==idProvider){
							idSoggettoSelected = new IDSoggetto(fru.getTipo(), fru.getNome());
						}
					}
				}
			}
			if(soggettiListVector.size()>0){
				soggettiList = soggettiListVector.toArray(new String[1]);
				soggettiListLabel = soggettiListLabelVector.toArray(new String[1]);

				if(idSoggettoSelected==null){
					// prendo il primo soggetto se esiste
					if(idSoggettoFirst!=null){
						idSoggettoSelected = idSoggettoFirst;
					}
				}
			}

			idFruizione.setIdFruitore(idSoggettoSelected);

			// Versioni
			String[] versioniValues = new String[versioniProtocollo.size()+1];
			String[] versioniLabel = new String[versioniProtocollo.size()+1];
			versioniLabel[0] = "usa versione fruitore";
			versioniValues[0] = "-";
			for (int i = 0; i < versioniProtocollo.size(); i++) {
				String tmp = versioniProtocollo.get(i);
				versioniLabel[i+1] = tmp;
				versioniValues[i+1] = tmp;
			}


			// ServiziApplicativi
			List<String> saList = new ArrayList<String>();
			saList.add("-");
			if(idSoggettoSelected!=null){
				try{
					String auth = this.fruizioneAutenticazione;
					if(auth==null || "".equals(auth)){
						auth = apsCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
					}
					CredenzialeTipo credenziale = CredenzialeTipo.toEnumConstant(auth);
					Boolean appId = null;
					if(CredenzialeTipo.APIKEY.equals(credenziale)) {
						ApiKeyState apiKeyState =  new ApiKeyState(null);
						appId = apiKeyState.appIdSelected;
					}
					List<IDServizioApplicativoDB> oldSilList = null;
					if(apsCore.isVisioneOggettiGlobale(superUser)){
						oldSilList = saCore.soggettiServizioApplicativoList(idSoggettoSelected,null,
								credenziale, appId);
					}
					else {
						oldSilList = saCore.soggettiServizioApplicativoList(idSoggettoSelected,superUser,
								credenziale, appId);
					}
					if(oldSilList!=null && oldSilList.size()>0){
						for (int i = 0; i < oldSilList.size(); i++) {
							saList.add(oldSilList.get(i).getNome());		
						}
					}
				}catch(DriverConfigurazioneNotFound dNotFound){}

			}

			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = soggettiCore.getRegistryReader(this.protocolFactory); 
			this.configRegistryReader = soggettiCore.getConfigIntegrationReader(this.protocolFactory);
			
			// ID Accordo Null per default
			this.consoleConfiguration = this.consoleDynamicConfiguration.getDynamicConfigFruizioneAccordoServizioParteSpecifica(this.consoleOperationType, apsHelper, 
					this.registryReader, this.configRegistryReader, idFruizione  );
			this.protocolProperties = apsHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);

			AccordoServizioParteComuneSintetico as = apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			
			boolean forceHttps = false;
			boolean forceHttpsClient = false;
			if(apsHelper.isProfiloModIPA(protocollo)) {
				forceHttps = apsHelper.forceHttpsProfiloModiPA();
				forceHttpsClient = apsHelper.forceHttpsClientProfiloModiPA(IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as), asps.getPortType());
			}
			
			List<GenericProperties> gestorePolicyTokenList = confCore.gestorePolicyTokenList(null, ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN, null);
			String [] policyLabels = new String[gestorePolicyTokenList.size() + 1];
			String [] policyValues = new String[gestorePolicyTokenList.size() + 1];
			
			policyLabels[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
			policyValues[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
			
			for (int i = 0; i < gestorePolicyTokenList.size(); i++) {
			GenericProperties genericProperties = gestorePolicyTokenList.get(i);
				policyLabels[(i+1)] = genericProperties.getNome();
				policyValues[(i+1)] = genericProperties.getNome();
			}
			
			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if(ServletUtils.isEditModeInProgress(this.editMode)){
				// setto la barra del titolo
				List<Parameter> lstParm = new ArrayList<Parameter>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + tmpTitle, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ this.id),
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ idSoggErogatore)
						));
				lstParm.add(ServletUtils.getParameterAggiungi());

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				if(soggettiListVector.size()<=0){

					pd.setMessage(AccordiServizioParteSpecificaCostanti.LABEL_AGGIUNTA_FRUITORI_COMPLETATA, Costanti.MESSAGE_TYPE_INFO);

					pd.disableEditMode();

				}

				else{
					dati = apsHelper.addHiddenFieldsToDati(tipoOp, this.id, null, null, dati);

					if (this.idSoggettoFruitore == null) {
						this.idSoggettoFruitore = "";
//						if(this.wsdlimpler.getValue() == null)
//							this.wsdlimpler.setValue(new byte[1]);
//						if(this.wsdlimplfru.getValue() == null)
//							this.wsdlimplfru.setValue(new byte[1]); 
						this.endpointtype = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DISABILITATO;
						this.tipoconn = "";
						this.url = "";
						this.nome = "";
						this.tipo = ConnettoriCostanti.TIPI_CODE_JMS[0];
						this.user = "";
						this.password = "";
						this.initcont = "";
						this.urlpgk = "";
						this.provurl = "";
						this.connfact = "";
						this.sendas = ConnettoriCostanti.TIPO_SEND_AS[0];
						this.httpsurl = "";
						this.httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
						this.httpshostverify = true;
						this.httpspath = "";
						this.httpstipo = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
						this.httpspwd = "";
						this.httpsalgoritmo = "";
						this.httpsstato = false;
						this.httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
						this.httpspwdprivatekeytrust = "";
						this.httpspathkey = "";
						this.httpstipokey = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
						this.httpspwdkey = "";
						this.httpspwdprivatekey = "";
						this.httpsalgoritmokey = "";

						if(apsHelper.isShowGestioneWorkflowStatoDocumenti()){
							if(this.statoPackage==null || "".equals(this.statoPackage)){
								//if(serviziFruitoriAdd.generazioneAutomaticaPorteDelegate){
								this.statoPackage=StatiAccordo.bozza.toString();
								/*}else{
									this.statoPackage=servizio.getStatoPackage();
								}*/
							}

							//Se l'ASPS riferito e' in stato operativo o finale allora setto la fruizione come operativa.
							if(asps.getStatoPackage().equals(StatiAccordo.operativo.toString()) || asps.getStatoPackage().equals(StatiAccordo.finale.toString())){
								this.statoPackage=StatiAccordo.operativo.toString();
							}

						}else{
							this.statoPackage=StatiAccordo.finale.toString();
						}
					}
					
					if(this.fruizioneServizioApplicativo==null || "".equals(this.fruizioneServizioApplicativo))
						this.fruizioneServizioApplicativo = "-";
					if(this.fruizioneRuolo==null || "".equals(this.fruizioneRuolo))
						this.fruizioneRuolo = "-";
					if(this.fruizioneAutenticazione==null || "".equals(this.fruizioneAutenticazione))
						this.fruizioneAutenticazione = apsCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
					if(this.fruizioneAutorizzazione==null || "".equals(this.fruizioneAutorizzazione)){
						String tipoAutorizzazione = apsCore.getAutorizzazione_generazioneAutomaticaPorteDelegate();
						this.fruizioneAutorizzazione = AutorizzazioneUtilities.convertToStato(tipoAutorizzazione);
						if(TipoAutorizzazione.isAuthenticationRequired(tipoAutorizzazione))
							this.fruizioneAutorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
						if(TipoAutorizzazione.isRolesRequired(tipoAutorizzazione))
							this.fruizioneAutorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
						this.fruizioneAutorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(tipoAutorizzazione).getValue();
					}
					if(gestioneToken == null) {
						gestioneToken = StatoFunzionalita.DISABILITATO.getValue();
						gestioneTokenPolicy = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
						gestioneTokenOpzionale = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_OPZIONALE;
						gestioneTokenValidazioneInput = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_VALIDAZIONE_INPUT;
						gestioneTokenIntrospection = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_INTROSPECTION;
						gestioneTokenUserInfo = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_USER_INFO;
						gestioneTokenTokenForward = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TOKEN_FORWARD;
						autenticazioneTokenIssuer = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_ISSUER;
						autenticazioneTokenClientId = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_CLIENT_ID;
						autenticazioneTokenSubject = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_SUBJECT;
						autenticazioneTokenUsername = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_USERNAME;
						autenticazioneTokenEMail = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_EMAIL;
					}
					if(scope ==null || "".equals(scope))
						scope = "-";
					if(autorizzazioneScope ==null)
						autorizzazioneScope = "";
	
					// default
					if(this.httpsalgoritmo==null || "".equals(this.httpsalgoritmo)){
						this.httpsalgoritmo = TrustManagerFactory.getDefaultAlgorithm();
					}
					if(this.httpsalgoritmokey==null || "".equals(this.httpsalgoritmokey)){
						this.httpsalgoritmokey = KeyManagerFactory.getDefaultAlgorithm();
					}
					if(this.httpstipologia==null || "".equals(this.httpstipologia)){
						this.httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
					}
					if(this.httpshostverifyS==null || "".equals(this.httpshostverifyS)){
						this.httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
						this.httpshostverify = true;
					}

					this.autenticazioneHttp = apsHelper.getAutenticazioneHttp(this.autenticazioneHttp, this.endpointtype, this.user);

					if(this.tempiRisposta_connectionTimeout==null || "".equals(this.tempiRisposta_connectionTimeout) 
							|| 
							this.tempiRisposta_readTimeout==null || "".equals(this.tempiRisposta_readTimeout) 
							|| 
							this.tempiRisposta_tempoMedioRisposta==null || "".equals(this.tempiRisposta_tempoMedioRisposta) ){
						
						ConfigurazioneCore configCore = new ConfigurazioneCore(soggettiCore);
						ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
						
						if(this.tempiRisposta_connectionTimeout==null || "".equals(this.tempiRisposta_connectionTimeout) ) {
							this.tempiRisposta_connectionTimeout = configGenerale.getTempiRispostaFruizione().getConnectionTimeout().intValue()+"";
						}
						if(this.tempiRisposta_readTimeout==null || "".equals(this.tempiRisposta_readTimeout) ) {
							this.tempiRisposta_readTimeout = configGenerale.getTempiRispostaFruizione().getReadTimeout().intValue()+"";
						}
						if(this.tempiRisposta_tempoMedioRisposta==null || "".equals(this.tempiRisposta_tempoMedioRisposta) ) {
							this.tempiRisposta_tempoMedioRisposta = configGenerale.getTempiRispostaFruizione().getTempoMedioRisposta().intValue()+"";
						}
						
					}
					
					// update della configurazione 
					this.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, apsHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idFruizione);


					dati = apsHelper.addServiziFruitoriToDati(dati, this.idSoggettoFruitore, this.wsdlimpler, this.wsdlimplfru, soggettiList,
							soggettiListLabel, "0", this.id, tipoOp, "", "", "", nomeservizio, tiposervizio, versioneservizio, this.correlato, this.statoPackage, this.statoPackage,asps.getStatoPackage(), null,this.validazioneDocumenti,
							this.controlloAccessiStato,
							this.fruizioneServizioApplicativo,this.fruizioneRuolo,this.fruizioneAutenticazione,this.fruizioneAutenticazioneOpzionale,this.fruizioneAutenticazionePrincipal, this.fruizioneAutenticazioneParametroList, this.fruizioneAutorizzazione,
							this.fruizioneAutorizzazioneAutenticati, this.fruizioneAutorizzazioneRuoli, this.fruizioneAutorizzazioneRuoliTipologia, this.fruizioneAutorizzazioneRuoliMatch,
							saList,apcCore.toMessageServiceBinding(as.getServiceBinding()), apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica()),
							null, null, null, null,
							gestioneToken, policyLabels, policyValues, 
							gestioneTokenPolicy, gestioneTokenOpzionale,
							gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
							autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
							autorizzazione_token, autorizzazione_tokenOptions,
							autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy);
	
					dati = apsHelper.addFruitoreToDati(TipoOperazione.ADD, versioniLabel, versioniValues, dati,null
							,null,null,null,null,null,null,null,null,null);

					String tipoSendas = ConnettoriCostanti.TIPO_SEND_AS[0];
					String tipoJms = ConnettoriCostanti.TIPI_CODE_JMS[0];
					if (apsHelper.isModalitaAvanzata()) {
						dati = apsHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, null, 
								this.url, this.nome,
								tipoJms, this.user,
								this.password, this.initcont, this.urlpgk,
								this.provurl, this.connfact, tipoSendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,tipoOp, this.httpsurl, this.httpstipologia,
								this.httpshostverify, this.httpspath, this.httpstipo, this.httpspwd,
								this.httpsalgoritmo, this.httpsstato, this.httpskeystore,
								this.httpspwdprivatekeytrust, this.httpspathkey,
								this.httpstipokey, this.httpspwdkey, 
								this.httpspwdprivatekey, this.httpsalgoritmokey,
								this.httpsKeyAlias, this.httpsTrustStoreCRLs,
								this.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD, null,
								null, null, null, null, null, null, null, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
								this.tempiRisposta_enabled, this.tempiRisposta_connectionTimeout, this.tempiRisposta_readTimeout, this.tempiRisposta_tempoMedioRisposta,
								this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
								this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
								this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
								this.autenticazioneToken,this.token_policy,
								listExtendedConnettore, false,
								protocollo, forceHttps, forceHttpsClient, false, false, null, null);
					}else{
						//spostato dentro l'helper
					}
				}

				// aggiunta campi custom
				dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.serviziFruitoriCheckData(TipoOperazione.ADD, soggettiList,
					this.id, "", "", null, "", "", this.idSoggettoFruitore,
					this.endpointtype, this.url, this.nome, this.tipo,
					this.user, this.password, this.initcont, this.urlpgk,
					this.provurl, this.connfact, this.sendas,
					this.wsdlimpler, this.wsdlimplfru, "0",
					this.httpsurl, this.httpstipologia,
					this.httpshostverify, this.httpspath, this.httpstipo,
					this.httpspwd, this.httpsalgoritmo, this.httpsstato,
					this.httpskeystore, this.httpspwdprivatekeytrust,
					this.httpspathkey, this.httpstipokey,
					this.httpspwdkey, this.httpspwdprivatekey,
					this.httpsalgoritmokey, 
					this.httpsKeyAlias, this.httpsTrustStoreCRLs,
					this.tipoconn,this.validazioneDocumenti,null,this.autenticazioneHttp,
					this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
					this.tempiRisposta_enabled, this.tempiRisposta_connectionTimeout, this.tempiRisposta_readTimeout, this.tempiRisposta_tempoMedioRisposta,
					this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
					this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
					this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
					this.fruizioneServizioApplicativo,this.fruizioneRuolo,this.fruizioneAutenticazione,this.fruizioneAutenticazioneOpzionale,this.fruizioneAutenticazionePrincipal, this.fruizioneAutenticazioneParametroList, this.fruizioneAutorizzazione,
					this.fruizioneAutorizzazioneAutenticati, this.fruizioneAutorizzazioneRuoli, this.fruizioneAutorizzazioneRuoliTipologia, this.fruizioneAutorizzazioneRuoliMatch,
					allegatoXacmlPolicy,
					this.autenticazioneToken,this.token_policy,
					listExtendedConnettore);

			// updateDynamic
			if(isOk) {
				this.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, apsHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idFruizione);		
			}
			
			// Validazione base dei parametri custom 
			if(isOk){
				try{
					apsHelper.validaProtocolProperties(this.consoleConfiguration, this.consoleOperationType, this.protocolProperties);
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
					this.consoleDynamicConfiguration.validateDynamicConfigFruizioneAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, apsHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idFruizione);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}


			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParm = new ArrayList<Parameter>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + tmpTitle, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ this.id)
						//				,
						//						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ idSoggErogatore)
						));
				lstParm.add(ServletUtils.getParameterAggiungi());

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				this.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, apsHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idFruizione);

				dati = apsHelper.addHiddenFieldsToDati(tipoOp, this.id, null, null, dati);

				dati = apsHelper.addServiziFruitoriToDati(dati, this.idSoggettoFruitore, this.wsdlimpler, this.wsdlimplfru, soggettiList, soggettiListLabel, "0", this.id, tipoOp,
						"", "", "", nomeservizio, tiposervizio, versioneservizio, this.correlato, this.statoPackage, this.statoPackage,asps.getStatoPackage(),null,this.validazioneDocumenti,
						this.controlloAccessiStato,
						this.fruizioneServizioApplicativo,this.fruizioneRuolo,this.fruizioneAutenticazione,this.fruizioneAutenticazioneOpzionale,this.fruizioneAutenticazionePrincipal, this.fruizioneAutenticazioneParametroList, this.fruizioneAutorizzazione,
						this.fruizioneAutorizzazioneAutenticati, this.fruizioneAutorizzazioneRuoli, this.fruizioneAutorizzazioneRuoliTipologia, this.fruizioneAutorizzazioneRuoliMatch,
						saList,apcCore.toMessageServiceBinding(as.getServiceBinding()), apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica()),
						null, null, null, null,gestioneToken, policyLabels, policyValues, 
						gestioneTokenPolicy, gestioneTokenOpzionale,
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_token, autorizzazione_tokenOptions,
						autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy);

				dati = apsHelper.addFruitoreToDati(tipoOp, versioniLabel, versioniValues, 
						dati,null
						,null,null,null,null,null,null,null,null,null);

				if (apsHelper.isModalitaAvanzata()) {
					dati = apsHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, null,
							this.url, this.nome, this.tipo, this.user,
							this.password, this.initcont, this.urlpgk,
							this.provurl, this.connfact, this.sendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,tipoOp, this.httpsurl, this.httpstipologia,
							this.httpshostverify, this.httpspath, this.httpstipo,
							this.httpspwd, this.httpsalgoritmo, this.httpsstato,
							this.httpskeystore, this.httpspwdprivatekeytrust,
							this.httpspathkey, this.httpstipokey,
							this.httpspwdkey, this.httpspwdprivatekey,
							this.httpsalgoritmokey, 
							this.httpsKeyAlias, this.httpsTrustStoreCRLs,
							this.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD, null,
							null, null, null, null, null, null, null, true,
							isConnettoreCustomUltimaImmagineSalvata, 
							this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
							this.tempiRisposta_enabled, this.tempiRisposta_connectionTimeout, this.tempiRisposta_readTimeout, this.tempiRisposta_tempoMedioRisposta,
							this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
							this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
							this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
							this.autenticazioneToken,this.token_policy,
							listExtendedConnettore, false,
							protocollo, forceHttps, forceHttpsClient, false, false, null, null);
				}else{
					//spostato dentro l'helper
				}

				// aggiunta campi custom
				dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
						ForwardParams.ADD());
			}

			// Inserisco il fruitore nel db
			int idProv = Integer.parseInt(this.idSoggettoFruitore);

			// prendo il nome e il tipo del soggetto proprietario della
			// porta delegata
			// che sarebbe il soggetto fruitore di questo servizio
			// Soggetto Fruitore
			Soggetto sogFru = soggettiCore.getSoggettoRegistro(idProv);
			String nomeFruitore = sogFru.getNome();
			String tipoFruitore = sogFru.getTipo();
			String pdd = sogFru.getPortaDominio();

			// soggetto erogatore servizio
//			Soggetto sogEr = soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggErogatore));
//			String nomeErogatore = sogEr.getNome();
//			String tipoErogatore = sogEr.getTipo();

			Connettore connettore = null;
			if (apsHelper.isModalitaAvanzata()) {
				connettore = new Connettore();
				
				apsHelper.fillConnettore(connettore, this.connettoreDebug, this.endpointtype, this.endpointtype, this.tipoconn, this.url,
						this.nome, this.tipo, this.user, this.password,
						this.initcont, this.urlpgk, this.provurl, this.connfact,
						this.sendas, this.httpsurl, this.httpstipologia,
						this.httpshostverify, this.httpspath, this.httpstipo,
						this.httpspwd, this.httpsalgoritmo, this.httpsstato,
						this.httpskeystore, this.httpspwdprivatekeytrust,
						this.httpspathkey, this.httpstipokey,
						this.httpspwdkey, this.httpspwdprivatekey,
						this.httpsalgoritmokey,
						this.httpsKeyAlias, this.httpsTrustStoreCRLs,
						this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
						this.tempiRisposta_enabled, this.tempiRisposta_connectionTimeout, this.tempiRisposta_readTimeout, this.tempiRisposta_tempoMedioRisposta,
						this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
						this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
						this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
						this.token_policy,
						listExtendedConnettore);
			}

			Fruitore fruitore = new Fruitore();
			fruitore.setId(Long.valueOf(idProv));
			fruitore.setTipo(tipoFruitore);
			fruitore.setNome(nomeFruitore);
			fruitore.setConnettore(connettore);

			String wsdlimplerS = this.wsdlimpler.getValue() != null ? new String(this.wsdlimpler.getValue()) : null; 
			fruitore.setByteWsdlImplementativoErogatore(((wsdlimplerS != null) && !wsdlimplerS.trim().replaceAll("\n", "").equals("")) ? wsdlimplerS.trim().getBytes() : null);
			String wsdlimplfruS = this.wsdlimplfru.getValue() != null ? new String(this.wsdlimplfru.getValue()) : null; 
			fruitore.setByteWsdlImplementativoFruitore(((wsdlimplfruS != null) && !wsdlimplfruS.trim().replaceAll("\n", "").equals("")) ? wsdlimplfruS.trim().getBytes() : null);

			AccordoServizioParteSpecifica servsp = apsCore.getAccordoServizioParteSpecifica(idInt);

			// stato
			fruitore.setStatoPackage(this.statoPackage);

			//			Spostato sopra a livello di edit in progress			
			//			//Se l'ASPS riferito e' in stato operativo o finale allora setto la fruizione come operativa.
			//			if(asps.getStatoPackage().equals(StatiAccordo.operativo.toString()) || asps.getStatoPackage().equals(StatiAccordo.finale.toString())){
			//				fruitore.setStatoPackage(StatiAccordo.operativo.toString());
			//			}

			// Check stato
			if(apsHelper.isShowGestioneWorkflowStatoDocumenti()){

				try{
					apsCore.validaStatoFruitoreAccordoServizioParteSpecifica(fruitore, servsp);
				}catch(ValidazioneStatoPackageException validazioneException){

					// Setto messaggio di errore
					pd.setMessage(validazioneException.toString());

					// setto la barra del titolo
					List<Parameter> lstParm = new ArrayList<Parameter>();

					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + tmpTitle, 
							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
							new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ this.id)
							//					,
							//							new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ idSoggErogatore)
							));
					lstParm.add(ServletUtils.getParameterAggiungi());

					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, lstParm );

					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());

					// update della configurazione 
					this.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, apsHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idFruizione);

					dati = apsHelper.addHiddenFieldsToDati(tipoOp, this.id, null, null, dati);

					dati = apsHelper.addServiziFruitoriToDati(dati, this.idSoggettoFruitore, this.wsdlimpler, this.wsdlimplfru, 
							soggettiList, soggettiListLabel, "0", this.id, tipoOp, "", "", "", nomeservizio, tiposervizio, versioneservizio, this.correlato, 
							this.statoPackage, this.statoPackage,asps.getStatoPackage(),null,this.validazioneDocumenti,
							this.controlloAccessiStato,
							this.fruizioneServizioApplicativo,this.fruizioneRuolo,this.fruizioneAutenticazione,this.fruizioneAutenticazioneOpzionale,this.fruizioneAutenticazionePrincipal, this.fruizioneAutenticazioneParametroList, this.fruizioneAutorizzazione,
							this.fruizioneAutorizzazioneAutenticati, this.fruizioneAutorizzazioneRuoli, this.fruizioneAutorizzazioneRuoliTipologia, this.fruizioneAutorizzazioneRuoliMatch,
							saList,apcCore.toMessageServiceBinding(as.getServiceBinding()), apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica()),
							null, null, null, null,gestioneToken, policyLabels, policyValues, 
							gestioneTokenPolicy, gestioneTokenOpzionale,
							gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
							autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
							autorizzazione_token, autorizzazione_tokenOptions,
							autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy);

					dati = apsHelper.addFruitoreToDati(TipoOperazione.ADD, versioniLabel, versioniValues, dati,null
							,null,null,null,null,null,null,null,null,null);

					if (apsHelper.isModalitaAvanzata()) {
						dati = apsHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, null,
								this.url, this.nome, this.tipo, this.user,
								this.password, this.initcont, this.urlpgk,
								this.provurl, this.connfact, this.sendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,tipoOp, this.httpsurl, this.httpstipologia,
								this.httpshostverify, this.httpspath, this.httpstipo,
								this.httpspwd, this.httpsalgoritmo, this.httpsstato,
								this.httpskeystore, this.httpspwdprivatekeytrust,
								this.httpspathkey, this.httpstipokey,
								this.httpspwdkey, this.httpspwdprivatekey,
								this.httpsalgoritmokey, 
								this.httpsKeyAlias, this.httpsTrustStoreCRLs,
								this.tipoconn, 
								AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD, null,
								null, null, null, null, null, null, null, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
								this.tempiRisposta_enabled, this.tempiRisposta_connectionTimeout, this.tempiRisposta_readTimeout, this.tempiRisposta_tempoMedioRisposta,
								this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
								this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
								this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
								this.autenticazioneToken,this.token_policy,
								listExtendedConnettore, false,
								protocollo, forceHttps, forceHttpsClient, false, false, null, null);
					}else{
						//spostato dentro l'helper
					}

					// aggiunta campi custom
					dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties);

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
							ForwardParams.ADD());
				}
			}

			//imposto properties custom
			fruitore.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(this.protocolProperties, this.consoleOperationType,null));

			servsp.addFruitore(fruitore);
			apsCore.performUpdateOperation(superUser, apsHelper.smista(), servsp);

			// Prendo i dati del soggetto erogatore del servizio
			String mynomeprov = asps.getNomeSoggettoErogatore();
			String mytipoprov = asps.getTipoSoggettoErogatore();

			// creo la porta delegata in automatico uso i dati nella
			// sessione
			// String tipoSoggettoErogatore = (String)
			// session.getAttribute("tipoSoggettoErogatore");
			// String nomeSoggettoErogatore = (String)
			// session.getAttribute("nomeSoggettoErogatore");

			boolean generazionePortaDelegata = true;
			/*
			 * bug-fix #61 Se il soggetto (fruitore) afferisce a una porta di
			 * dominio di tipo 'esterno', la porta delegata non deve essere
			 * creata.
			 */
			try{
				if(pddCore.isPddEsterna(pdd)){
					generazionePortaDelegata = false;
				}
			}catch(DriverControlStationNotFound dNT){
				// In singlePdD la porta di dominio e' opzionale.
				if(apsCore.isSinglePdD()==false){
					throw dNT;
				}
			}
			if (generazionePortaDelegata) {
				
				List<Object> listaOggettiDaCreare = new ArrayList<Object>();
				
				IDSoggetto idFruitore = new IDSoggetto(tipoFruitore, nomeFruitore);
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tiposervizio, nomeservizio, mytipoprov, mynomeprov, versioneservizio);
				ServiceBinding serviceBinding = org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(as.getServiceBinding()) ?
						ServiceBinding.REST : ServiceBinding.SOAP;
				Subscription subscriptionDefault = this.protocolFactory.createProtocolIntegrationConfiguration().
						createDefaultSubscription(serviceBinding, idFruitore, idServizio);
				
				PortaDelegata portaDelegata = subscriptionDefault.getPortaDelegata();
				MappingFruizionePortaDelegata mappingFruizione = subscriptionDefault.getMapping();
				portaDelegata.setIdSoggetto((long) idProv);

				if(CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO_PUBBLICO.equals(this.controlloAccessiStato)) {
					this.fruizioneAutenticazione = TipoAutenticazione.DISABILITATO.getValue();
					this.fruizioneAutenticazioneOpzionale = null;
					this.fruizioneAutenticazionePrincipal = null;
					this.fruizioneAutenticazioneParametroList = null;
				}
				
				if(CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO_PUBBLICO.equals(this.controlloAccessiStato)) {
					this.fruizioneAutorizzazione = TipoAutorizzazione.DISABILITATO.getValue();
					this.fruizioneAutorizzazioneAutenticati = null;
					this.fruizioneAutorizzazioneRuoli = null;
					this.fruizioneAutorizzazioneRuoliTipologia = null;
					this.fruizioneAutorizzazioneRuoliMatch = null;
				}
				
				porteDelegateCore.configureControlloAccessiPortaDelegata(portaDelegata, 
						this.fruizioneAutenticazione, this.fruizioneAutenticazioneOpzionale,this.fruizioneAutenticazionePrincipal, this.fruizioneAutenticazioneParametroList,
						this.fruizioneAutorizzazione, this.fruizioneAutorizzazioneAutenticati, this.fruizioneAutorizzazioneRuoli, this.fruizioneAutorizzazioneRuoliTipologia, this.fruizioneAutorizzazioneRuoliMatch,
						this.fruizioneServizioApplicativo, this.fruizioneRuolo,
						autorizzazione_tokenOptions,
						autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy);
				
				porteDelegateCore.configureControlloAccessiGestioneToken(portaDelegata, gestioneToken, 
						gestioneTokenPolicy, gestioneTokenOpzionale,
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_tokenOptions
						);
							
				// Verifico prima che la porta delegata non esista già
				if (!porteDelegateCore.existsPortaDelegata(mappingFruizione.getIdPortaDelegata())){
					listaOggettiDaCreare.add(portaDelegata);
				}
				listaOggettiDaCreare.add(mappingFruizione);
				
				porteDelegateCore.performCreateOperation(superUser, apsHelper.smista(), listaOggettiDaCreare.toArray());
			}


			// cancello i file temporanei
			apsHelper.deleteBinaryParameters(this.wsdlimpler,this.wsdlimplfru);
			apsHelper.deleteBinaryProtocolPropertiesTmpFiles(this.protocolProperties);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.SERVIZI_FRUITORI;

			ricerca = apsHelper.checkSearchParameters(idLista, ricerca);

			List<Fruitore> lista = apsCore.serviziFruitoriList(Integer.parseInt(this.id), ricerca);

			apsHelper.prepareServiziFruitoriList(lista, this.id, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished( mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
					ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
					ForwardParams.ADD());
		}  
	}

	
}
