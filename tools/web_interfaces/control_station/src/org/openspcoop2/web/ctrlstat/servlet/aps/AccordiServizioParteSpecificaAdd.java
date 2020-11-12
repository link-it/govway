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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.GenericProperties;
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
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizio;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.OperationSintetica;
import org.openspcoop2.core.registry.beans.PortTypeSintetico;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.core.registry.driver.db.IDAccordoDB;
import org.openspcoop2.core.registry.driver.db.IDSoggettoDB;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.ApiKeyState;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * serviziAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaAdd extends Action {


	private String connettoreDebug;
	private String   nomeservizio, tiposervizio, provider, accordo,
	servcorr, endpointtype, tipoconn, url, nome, tipo, user, password, initcont,
	urlpgk, provurl, connfact, sendas, 
	profilo, portType,descrizione,
	httpsurl, httpstipologia, httpspath,
	httpstipo, httpspwd, httpsalgoritmo,
	httpskeystore, httpspwdprivatekeytrust, httpspathkey,
	httpstipokey, httpspwdkey, httpspwdprivatekey,
	httpsalgoritmokey,
	httpsKeyAlias,
	httpsTrustStoreCRLs;
	private String httpshostverifyS, httpsstatoS;
	private boolean httpshostverify, httpsstato, httpsTrustVerifyCert;
	private String nomeSoggettoErogatore = "";
	private String tipoSoggettoErogatore = "";
	String providerSoggettoFruitore = null;
	private String nomeSoggettoFruitore = "";
	private String tipoSoggettoFruitore = "";
	private boolean privato = false;
	private String statoPackage = "";
	private String versione;
	private boolean validazioneDocumenti = true;
	private boolean decodeRequestValidazioneDocumenti = false;
	private String editMode = null;
	private String nomeSA = null;
	private String oldPortType = null;
	private String autenticazioneHttp;
	private Properties parametersPOST;
	private ServiceBinding serviceBinding = null;
	private org.openspcoop2.protocol.manifest.constants.InterfaceType formatoSpecifica = null;

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

	private String controlloAccessiStato;
	
	private String erogazioneRuolo;
	private String erogazioneAutenticazione;
	private String erogazioneAutenticazioneOpzionale;
	private TipoAutenticazionePrincipal erogazioneAutenticazionePrincipal;
	private List<String> erogazioneAutenticazioneParametroList;
	private String erogazioneAutorizzazione;
	private String erogazioneAutorizzazioneAutenticati, erogazioneAutorizzazioneRuoli, erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch;
	private String erogazioneSoggettoAutenticato; 
	
	private String fruizioneServizioApplicativo;
	private String fruizioneRuolo;
	private String fruizioneAutenticazione;
	private String fruizioneAutenticazioneOpzionale;
	private TipoAutenticazionePrincipal fruizioneAutenticazionePrincipal;
	private List<String> fruizioneAutenticazioneParametroList;
	private String fruizioneAutorizzazione;
	private String fruizioneAutorizzazioneAutenticati, fruizioneAutorizzazioneRuoli, fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch;
	
	private String tipoProtocollo;
	
	private String erogazioneServizioApplicativoServer;
	private boolean erogazioneServizioApplicativoServerEnabled = false;
	
	private String canale, canaleStato;
	
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
		this.consoleOperationType = ConsoleOperationType.ADD;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.ADD;

		try {
			
			ErogazioniHelper apsHelper = new ErogazioniHelper(request, pd, session);
			
			this.parametersPOST = null;

			this.editMode = apsHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);

			this.nomeservizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO);
			this.tiposervizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO);
			this.provider = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_EROGATORE);
			this.accordo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO);
			this.servcorr = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_SERVIZIO_CORRELATO);
			// this.servpub = apsHelper.getParameter("servpub");
			//			this.endpointtype = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE );
			
			String erogazioneServizioApplicativoServerEnabledS = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ABILITA_USO_APPLICATIVO_SERVER);
			this.erogazioneServizioApplicativoServerEnabled = ServletUtils.isCheckBoxEnabled(erogazioneServizioApplicativoServerEnabledS);
			this.erogazioneServizioApplicativoServer = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_APPLICATIVO_SERVER);
			
			this.endpointtype = apsHelper.readEndPointType();
			this.tipoconn = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO );
			this.autenticazioneHttp = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);

			this.connettoreDebug = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);

			this.controlloAccessiStato = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO);
			
			this.erogazioneRuolo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_RUOLO);
			this.erogazioneAutenticazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE);
			this.erogazioneAutenticazioneOpzionale = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE_OPZIONALE);
			String erogazioneAutenticazionePrincipalTipo = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TIPO);
			this.erogazioneAutenticazionePrincipal = TipoAutenticazionePrincipal.toEnumConstant(erogazioneAutenticazionePrincipalTipo, false);
			this.erogazioneAutenticazioneParametroList = apsHelper.convertFromDataElementValue_parametroAutenticazioneList(this.erogazioneAutenticazione, this.erogazioneAutenticazionePrincipal);
			this.erogazioneAutorizzazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE);
			this.erogazioneAutorizzazioneAutenticati = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_AUTENTICAZIONE);
			this.erogazioneAutorizzazioneRuoli = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLI);
			this.erogazioneAutorizzazioneRuoliTipologia = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_TIPOLOGIA);
			this.erogazioneAutorizzazioneRuoliMatch = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_MATCH);
			this.erogazioneSoggettoAutenticato = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_SOGGETTO_AUTENTICATO);

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
			this.opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(apsHelper,this.transfer_mode, this.redirect_mode);

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
			String httpsTrustVerifyCertS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS );
			this.httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
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




			this.profilo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROFILO);

			this.wsdlimpler = apsHelper.getBinaryParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
			this.wsdlimplfru = apsHelper.getBinaryParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE);
			this.portType = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE);

			String priv = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PRIVATO);
			this.privato = ServletUtils.isCheckBoxEnabled(priv);

			this.descrizione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_DESCRIZIONE);
			this.statoPackage = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);

			this.versione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE);
			this.nomeSA = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SA);
			
			
			this.providerSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE);
			
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

			if(apsHelper.isMultipart()){
				this.decodeRequestValidazioneDocumenti = true;
			}
			
			this.canale = apsHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CANALI_CANALE);
			this.canaleStato = apsHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO);

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(apsCore);
			PddCore pddCore = new PddCore(apsCore);
			
			// carico i canali
			CanaliConfigurazione gestioneCanali = confCore.getCanaliConfigurazione(false);
			List<CanaleConfigurazione> canaleList = gestioneCanali != null ? gestioneCanali.getCanaleList() : new ArrayList<>();
			boolean gestioneCanaliEnabled = gestioneCanali != null && org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(gestioneCanali.getStato());

			String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			boolean gestioneErogatori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
					gestioneErogatori = true;
				}
				else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
			}
			
			boolean visualizzaSezioneApplicativiServerEnabled = gestioneErogatori && saCore.isApplicativiServerEnabled(apsHelper);
			
			PddTipologia pddTipologiaSoggettoAutenticati = null;
			if(gestioneErogatori) {
				pddTipologiaSoggettoAutenticati = PddTipologia.ESTERNO;
			}
			
			
			
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

			Boolean isConnettoreCustomUltimaImmagineSalvata = null;

			Connettore conTmp = null;
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, apsHelper, 
							this.parametersPOST, (this.endpointtype==null), this.endpointtype); // uso endpointtype per capire se è la prima volta che entro

			// Tipi protocollo supportati
			boolean filtraSoggettiEsistenti = true;
			boolean filtraAccordiEsistenti = true;
			List<String> listaTipiProtocollo = apcCore.getProtocolliByFilter(session, filtraSoggettiEsistenti, null, filtraAccordiEsistenti, false, true);
					
			// Preparo il menu
			apsHelper.makeMenu();

			if(listaTipiProtocollo.size()<=0) {
				
				List<String> _listaTipiProtocolloSoloSoggetti = apcCore.getProtocolliByFilter(session, filtraSoggettiEsistenti, null, !filtraAccordiEsistenti, false, true);			
				if(_listaTipiProtocolloSoloSoggetti.size()>0) {
					pd.setMessage("Non risultano registrate API", Costanti.MESSAGE_TYPE_INFO);
				}
				else {
					if(gestioneFruitori) {
						pd.setMessage("Non risultano registrati soggetti che possano erogare API", Costanti.MESSAGE_TYPE_INFO);
					}
					else {
						pd.setMessage("Non risultano registrati soggetti", Costanti.MESSAGE_TYPE_INFO);
					}
				}
				pd.disableEditMode();

				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
						ForwardParams.ADD());
			}
			
			this.tipoProtocollo = apsHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO);
			if(this.tipoProtocollo == null){
				this.tipoProtocollo = apsCore.getProtocolloDefault(session, listaTipiProtocollo);
			}
			
			boolean connettoreStatic = false;
			if(gestioneFruitori) {
				connettoreStatic = apsCore.isConnettoreStatic(this.tipoProtocollo);
			}
			
			String[] ptList = null;
			// Prendo la lista di soggetti e la metto in un array
			// Prendo la lista di accordi e la metto in un array
			String[] soggettiList = null;
			String[] soggettiListLabel = null;
			String[] accordiList = null;
			String[] accordiListLabel = null;
			String[] soggettiFruitoriList = null;
			String[] soggettiFruitoriListLabel = null;
			// int totSogg = 0, totAcc = 0;

			boolean generaPortaApplicativa = !gestioneFruitori;
			boolean generaPortaDelegata = gestioneFruitori;
			boolean accordoPrivato = false;
			String uriAccordo = null;
			IDSoggetto soggettoReferente = null;
			long idReferente = -1;

			List<IDAccordoDB> listaIdAPI = AccordiServizioParteSpecificaUtilities.getListaIdAPI(this.tipoProtocollo, userLogin, apsCore, apsHelper);

			

			int accordoPrimoAccesso = -1;

			if (listaIdAPI.size() > 0) {
				accordiList = new String[listaIdAPI.size()];
				accordiListLabel = new String[listaIdAPI.size()];
				int i = 0;
				for (IDAccordoDB as : listaIdAPI) {
					accordiList[i] = as.getId().toString();
					soggettoReferente = null;
					idReferente = -1;
					if(as.getSoggettoReferenteDB()!=null && as.getSoggettoReferenteDB().getId()!=null)
						idReferente = as.getSoggettoReferenteDB().getId();

					if(idReferente>0){
						soggettoReferente = new IDSoggetto();
						soggettoReferente.setTipo(as.getSoggettoReferente().getTipo());
						soggettoReferente.setNome(as.getSoggettoReferente().getNome());

						// se ancora non ho scelto l'accordo da mostrare quando entro
						if(accordoPrimoAccesso == -1){
							//mostro il primo accordo che ha tipo che corrisponde a quello di default
							if(apcCore.getProtocolloDefault(session,listaTipiProtocollo).equals(soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo()))){
								accordoPrimoAccesso = i;
							}
						}
					}
					accordiListLabel[i] = apsHelper.getLabelIdAccordo(this.tipoProtocollo, as);
					i++;
				}
			}

			// se ancora non ho scelto l'accordo da mostrare quando entro
			if(accordoPrimoAccesso == -1 && listaIdAPI.size() > 0){
				// Se entro in questo caso significa che tutti gli accordi di servizio parte comune esistente s
				// possiedono come soggetto referente un tipo di protocollo differente da quello di default.
				// in questo caso prendo il primo che trovo
				accordoPrimoAccesso = 0;
			}

			//			}


			String postBackElementName = apsHelper.getPostBackElementName();

			// Controllo se ho modificato l'accordo, se si allora suggerisco il referente dell'accordo
			AccordoServizioParteComuneSintetico as = null;
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO) ||
						postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROTOCOLLO)){
					
					if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROTOCOLLO)){
						this.accordo = null;
						this.versione = null;
						this.erogazioneAutenticazione = null;
						this.fruizioneAutenticazione = null;
						this.erogazioneAutorizzazione = null;
						this.fruizioneAutorizzazione = null;
						
						this.providerSoggettoFruitore = null;
					}
					
					this.erogazioneServizioApplicativoServerEnabled = false;
					this.url = null;
					
					if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO) && this.tipoProtocollo!=null) {
						boolean showReferente = apcCore.isSupportatoSoggettoReferente(this.tipoProtocollo);
						if(showReferente) {
							boolean annullaProvider = true;
							if (!gestioneFruitori && this.accordo != null && !"".equals(this.accordo)) {
								as = apcCore.getAccordoServizioSintetico(Long.parseLong(this.accordo));
								if(as.getSoggettoReferente()!=null) {
									Soggetto s = soggettiCore.getSoggettoRegistro(new IDSoggetto(as.getSoggettoReferente().getTipo(), as.getSoggettoReferente().getNome()));
									if(pddCore.isPddEsterna(s.getPortaDominio())) {
										annullaProvider = false;
									}
								}
							} 
							if(annullaProvider) {
								this.provider = null;
							}
						}
					}
					else {
						this.provider = null;
					}
					
					this.tiposervizio = null;
					
					// reset protocol properties
					apsHelper.deleteBinaryParameters(this.wsdlimpler,this.wsdlimplfru);
					apsHelper.deleteProtocolPropertiesBinaryParameters(this.wsdlimpler,this.wsdlimplfru);

					this.portType = null;
					this.nomeservizio = "";
				}  
				
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE)){
					this.nomeservizio = "";
					this.url = null;
					this.erogazioneServizioApplicativoServerEnabled = false;
				}
				
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_EROGATORE)){
					this.erogazioneServizioApplicativoServerEnabled = false;
				}
				
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ABILITA_USO_APPLICATIVO_SERVER)){
					this.erogazioneServizioApplicativoServer = null;
				}
			}

			// Lista port-type associati all'accordo di servizio
			boolean forceHttps = false;
			boolean forceHttpsClient = false;
			if(as==null) {
				if (this.accordo != null && !"".equals(this.accordo)) {
					as = apcCore.getAccordoServizioSintetico(Long.parseLong(this.accordo));
				} else {
					if (accordiList != null){
						if(accordoPrimoAccesso >= 0 && accordoPrimoAccesso < accordiList.length)
							as = apcCore.getAccordoServizioSintetico(Long.parseLong(accordiList[accordoPrimoAccesso]));
						if(as!=null)
							this.accordo = as.getId() + "";
					}
				}
			}
			if(as!=null){
				// salvo il soggetto referente
				soggettoReferente = new IDSoggetto(as.getSoggettoReferente().getTipo(), as.getSoggettoReferente().getNome());
				idReferente = as.getSoggettoReferente().getId();

				this.serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
				this.formatoSpecifica = apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica());
				

				accordoPrivato = as.getPrivato()!=null && as.getPrivato();
				uriAccordo = idAccordoFactory.getUriFromAccordo(as);

				List<PortTypeSintetico> portTypes = AccordiServizioParteSpecificaUtilities.getListaPortTypes(as, apsCore, apsHelper);
				
				if (portTypes.size() > 0) {
					ptList = new String[portTypes.size() + 1];
					ptList[0] = "-";
					int i = 1;
					for (Iterator<PortTypeSintetico> iterator = portTypes.iterator(); iterator.hasNext();) {
						PortTypeSintetico portType2 = iterator.next();
						ptList[i] = portType2.getNome();
						i++;
					}
				}

				if( apsCore.isShowCorrelazioneAsincronaInAccordi() ){
					if (this.portType != null && !"".equals(this.portType) && !"-".equals(this.portType)){
						PortTypeSintetico pt = null;
						for(int i=0; i<as.getPortType().size(); i++){
							if(this.portType.equals(as.getPortType().get(i).getNome())){
								pt = as.getPortType().get(i);
								break;
							}
						}
						boolean servizioCorrelato = false;
						if(pt!=null){
							for(int i=0; i<pt.getAzione().size(); i++){
								OperationSintetica op = pt.getAzione().get(i);
								if(op.getCorrelataServizio()!=null && !pt.getNome().equals(op.getCorrelataServizio()) && op.getCorrelata()!=null){
									servizioCorrelato = true;
									break;
								}
							}
						}
						if(servizioCorrelato){
							this.servcorr=Costanti.CHECK_BOX_ENABLED;
						}
						else{
							this.servcorr=Costanti.CHECK_BOX_DISABLED;
						}							
					}
				}
				
				if(generaPortaDelegata && apsHelper.isProfiloModIPA(this.tipoProtocollo)) {
					forceHttps = apsHelper.forceHttpsProfiloModiPA();
					forceHttpsClient = apsHelper.forceHttpsClientProfiloModiPA(IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as), this.portType);
				}

			}

			// Fix per bug che accadeva in modalita' standard quando si seleziona un servizio di un accordo operativo, poi si cambia idea e si seleziona un accordo bozza.
			// lo stato del package rimaneva operativo.
			if(this.statoPackage!=null && apsHelper.isModalitaStandard()){
				if(apsHelper.isShowGestioneWorkflowStatoDocumenti()){
					if(StatiAccordo.operativo.toString().equals(this.statoPackage) || StatiAccordo.finale.toString().equals(this.statoPackage)){
						if(as!=null && as.getStatoPackage().equals(StatiAccordo.bozza.toString()) ){
							this.statoPackage = StatiAccordo.bozza.toString(); 
						}
					}					
				}
			}
			
			// Calcolo url presente nell'API
			String urlAPI = apcCore.readEndpoint(as, this.portType, this.servcorr, this.wsdlimpler, this.wsdlimplfru);
			//System.out.println("Endpoint ricavato dall'API ["+urlSuggerita+"]");
			
			List<String> versioniProtocollo = apsCore.getVersioniProtocollo(this.tipoProtocollo);
			List<String> tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(this.tipoProtocollo);
			List<String> tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(this.tipoProtocollo,this.serviceBinding);
			boolean erogazioneIsSupportatoAutenticazioneSoggetti = soggettiCore.isSupportatoAutenticazioneSoggetti(this.tipoProtocollo);

			// calcolo soggetti compatibili con accordi
			List<Soggetto> listSoggetti = null;
			Search searchSoggetti = new Search(true);
			searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, this.tipoProtocollo);
			boolean gestioneFruitori_soggettiErogatori_escludiSoggettoFruitore = false;
			if(gestioneFruitori) {
				boolean filtraSoloEsterni = true;
				if(apsCore.isMultitenant() && apsCore.getMultitenantSoggettiFruizioni()!=null) {
					switch (apsCore.getMultitenantSoggettiFruizioni()) {
					case SOLO_SOGGETTI_ESTERNI:
						filtraSoloEsterni = true;
						break;
					case ESCLUDI_SOGGETTO_FRUITORE:
						filtraSoloEsterni = false;
						gestioneFruitori_soggettiErogatori_escludiSoggettoFruitore = true;
						break;
					case TUTTI:
						filtraSoloEsterni = false;
						break;
					}
				}
				if(filtraSoloEsterni) {
					searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE);
				}
			}
			if(gestioneErogatori) {
				searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
				if(apsHelper.isSoggettoMultitenantSelezionato()) {
					IDSoggetto idSoggettoSelezionato = soggettiCore.convertSoggettoSelezionatoToID(apsHelper.getSoggettoMultitenantSelezionato());
					listSoggetti = new ArrayList<>();
					try {
						listSoggetti.add(soggettiCore.getSoggettoRegistro(idSoggettoSelezionato));
					}catch(DriverRegistroServiziNotFound notFound) {}
				}
			}
			if(listSoggetti==null) {
				if(apsCore.isVisioneOggettiGlobale(userLogin)){
					listSoggetti = soggettiCore.soggettiRegistroList(null, searchSoggetti);
				}else{
					listSoggetti = soggettiCore.soggettiRegistroList(userLogin, searchSoggetti);
				}
			}
			
			if(listSoggetti.size()<=0) {
				if(gestioneFruitori) {
					pd.setMessage("Non risultano registrati soggetti che possano erogare API", Costanti.MESSAGE_TYPE_INFO);
				}
				else {
					pd.setMessage("Non risultano registrati soggetti", Costanti.MESSAGE_TYPE_INFO);
				}
				
				pd.disableEditMode();

				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
						ForwardParams.ADD());
			}

			boolean existsAPCCompatibili = false;
			if (listSoggetti.size() > 0) {
				List<String> soggettiListTmp = new ArrayList<String>();
				List<String> soggettiListLabelTmp = new ArrayList<String>();
				for (Soggetto soggetto : listSoggetti) {
					soggettiListTmp.add(soggetto.getId().toString());
					soggettiListLabelTmp.add(apsHelper.getLabelNomeSoggetto(this.tipoProtocollo, soggetto.getTipo() , soggetto.getNome()));
				}

				existsAPCCompatibili = listaIdAPI!=null && listaIdAPI.size()>0;

				if(soggettiListTmp.size()>0 && existsAPCCompatibili){
					soggettiList = soggettiListTmp.toArray(new String[1]);
					soggettiListLabel = soggettiListLabelTmp.toArray(new String[1]);
				}
				else{
					if(listaIdAPI.size()<=0){
						pd.setMessage("Non risultano registrate API", Costanti.MESSAGE_TYPE_INFO);
						pd.disableEditMode();

						Vector<DataElement> dati = new Vector<DataElement>();

						dati.addElement(ServletUtils.getDataElementForEditModeFinished());

						pd.setDati(dati);

						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

						return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
								ForwardParams.ADD());
					}

					// refresh di tutte le infromazioni
					versioniProtocollo = apsCore.getVersioniProtocollo(this.tipoProtocollo);
					tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(this.tipoProtocollo);
					tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(this.tipoProtocollo,this.serviceBinding);

					searchSoggetti = new Search(true);
					searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, this.tipoProtocollo);
					if(apsCore.isVisioneOggettiGlobale(userLogin)){
						listSoggetti = soggettiCore.soggettiRegistroList(null, searchSoggetti);
					}else{
						listSoggetti = soggettiCore.soggettiRegistroList(userLogin, searchSoggetti);
					}
					
					for (Soggetto soggetto : listSoggetti) {
						soggettiListTmp.add(soggetto.getId().toString());
						soggettiListLabelTmp.add(apsHelper.getLabelNomeSoggetto(this.tipoProtocollo, soggetto.getTipo() , soggetto.getNome()));
					}
					soggettiList = soggettiListTmp.toArray(new String[1]);
					soggettiListLabel = soggettiListLabelTmp.toArray(new String[1]);

					if(listaIdAPI.size()>0){
						this.accordo = listaIdAPI.get(0).getId()+"";
					}
				}
			}
			
			
			
			

			// calcolo soggetti fruitori
			List<Soggetto> listFruitori = null;
			if(gestioneFruitori) {
				
				if(apsHelper.isSoggettoMultitenantSelezionato()) {
					IDSoggetto idSoggettoSelezionato = soggettiCore.convertSoggettoSelezionatoToID(apsHelper.getSoggettoMultitenantSelezionato());
					listFruitori = new ArrayList<>();
					try {
						listFruitori.add(soggettiCore.getSoggettoRegistro(idSoggettoSelezionato));
					}catch(DriverRegistroServiziNotFound notFound) {}
				}
				else {
					Search searchSoggettiFruitori = new Search(true);
					searchSoggettiFruitori.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, this.tipoProtocollo);
					searchSoggettiFruitori.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
					if(apsCore.isVisioneOggettiGlobale(userLogin)){
						listFruitori = soggettiCore.soggettiRegistroList(null, searchSoggettiFruitori);
					}else{
						listFruitori = soggettiCore.soggettiRegistroList(userLogin, searchSoggettiFruitori);
					}
				}
				
				if (listFruitori.size() > 0) {
					List<String> soggettiListTmp = new ArrayList<String>();
					List<String> soggettiListLabelTmp = new ArrayList<String>();
					for (Soggetto soggetto : listFruitori) {
						soggettiListTmp.add(soggetto.getId().toString());
						soggettiListLabelTmp.add(apsHelper.getLabelNomeSoggetto(this.tipoProtocollo, soggetto.getTipo() , soggetto.getNome()));
					}
	
					if(soggettiListTmp.size()>0){
						soggettiFruitoriList = soggettiListTmp.toArray(new String[1]);
						soggettiFruitoriListLabel = soggettiListLabelTmp.toArray(new String[1]);
					}
					else {
						pd.setMessage("Non esistono soggetti nel dominio interno", Costanti.MESSAGE_TYPE_INFO);
						pd.disableEditMode();

						Vector<DataElement> dati = new Vector<DataElement>();

						dati.addElement(ServletUtils.getDataElementForEditModeFinished());

						pd.setDati(dati);

						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

						return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
								ForwardParams.ADD());
					}
				}
			}
			
			if(gestioneFruitori) {
				if ((this.providerSoggettoFruitore != null) && !this.providerSoggettoFruitore.equals("")) {
					long idFruitore = Long.parseLong(this.providerSoggettoFruitore);
					Soggetto soggetto = soggettiCore.getSoggettoRegistro(idFruitore);
					this.nomeSoggettoFruitore = soggetto.getNome();
					this.tipoSoggettoFruitore = soggetto.getTipo();
					//profiloSoggettoErogatore = soggetto.getVersioneProtocollo();
				}
				else {
					Soggetto soggetto = listFruitori.get(0);
					this.providerSoggettoFruitore = soggetto.getId()+"";
					this.nomeSoggettoFruitore = soggetto.getNome();
					this.tipoSoggettoFruitore = soggetto.getTipo();
				}
			}
			
			if(gestioneFruitori_soggettiErogatori_escludiSoggettoFruitore) {
				boolean found = false;
				for (int i = 0; i < listSoggetti.size(); i++) {
					Soggetto soggettoCheck = listSoggetti.get(i);
					if(soggettoCheck.getTipo().equals(this.tipoSoggettoFruitore) && soggettoCheck.getNome().equals(this.nomeSoggettoFruitore)) {
						listSoggetti.remove(i);
						found = true;
						break;
					}
				}
				if(found) {
					if(listSoggetti.size()<=0) {
						if(gestioneFruitori) {
							pd.setMessage("Non risultano registrati soggetti che possano erogare API", Costanti.MESSAGE_TYPE_INFO);
						}
						else {
							pd.setMessage("Non risultano registrati soggetti", Costanti.MESSAGE_TYPE_INFO);
						}
						
						pd.disableEditMode();
	
						Vector<DataElement> dati = new Vector<DataElement>();
	
						dati.addElement(ServletUtils.getDataElementForEditModeFinished());
	
						pd.setDati(dati);
	
						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
	
						return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
								ForwardParams.ADD());
					}
					
					// aggiorno soggetti
					List<String> soggettiListTmp = new ArrayList<String>();
					List<String> soggettiListLabelTmp = new ArrayList<String>();
					for (Soggetto soggetto : listSoggetti) {
						soggettiListTmp.add(soggetto.getId().toString());
						soggettiListLabelTmp.add(apsHelper.getLabelNomeSoggetto(this.tipoProtocollo, soggetto.getTipo() , soggetto.getNome()));
					}
					soggettiList = soggettiListTmp.toArray(new String[1]);
					soggettiListLabel = soggettiListLabelTmp.toArray(new String[1]);
	
				}
			}
			
			
			

			//String profiloSoggettoErogatore = null;
			if ((this.provider != null) && !this.provider.equals("")) {
				long idErogatore = Long.parseLong(this.provider);
				Soggetto soggetto = soggettiCore.getSoggettoRegistro(idErogatore);
				this.nomeSoggettoErogatore = soggetto.getNome();
				this.tipoSoggettoErogatore = soggetto.getTipo();
				//profiloSoggettoErogatore = soggetto.getVersioneProtocollo();
			} else {
				if(soggettoReferente != null ){
					for (Soggetto soggettoCheck : listSoggetti) {
						if(soggettoCheck.getTipo().equals(soggettoReferente.getTipo()) && soggettoCheck.getNome().equals(soggettoReferente.getNome())) {
							this.provider = idReferente + "";
							this.nomeSoggettoErogatore = soggettoReferente.getNome();
							this.tipoSoggettoErogatore = soggettoReferente.getTipo();
							break;
						}
					}
				}
				// Se ancora non l'ho trovato prendo il primo della lista nel caso di gestione erogazione
				if ((this.provider == null) || this.provider.equals("")) {
					if( (gestioneErogatori || gestioneFruitori) && listSoggetti!=null && listSoggetti.size()>0) {
						Soggetto soggetto = listSoggetti.get(0);
						this.provider = soggetto.getId() + "";
						this.nomeSoggettoErogatore = soggetto.getNome();
						this.tipoSoggettoErogatore = soggetto.getTipo();
					}
				}
			}
			
			
			if(this.tiposervizio == null){
				this.tiposervizio = apsCore.getTipoServizioDefaultProtocollo(this.tipoProtocollo,this.serviceBinding);
			}


			// La lista degli SA viene filtrata per tipo se sono abilitati gli applicativiServer.
			String tipoSA = (visualizzaSezioneApplicativiServerEnabled && gestioneErogatori) ? ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER : null;
			
			// Lista dei servizi applicativi per la creazione automatica
			List<IDServizioApplicativoDB> listaIdSA = null;
			if ((this.provider != null) && !this.provider.equals("")) {
				long idErogatore = Long.valueOf(this.provider);
				
				// I servizi applicativi da visualizzare sono quelli che hanno
				// -Integration Manager (getMessage abilitato)
				// -connettore != disabilitato
				listaIdSA = saCore.getIdServiziApplicativiWithIdErogatore(idErogatore, tipoSA, true, true);

				if(tipoSA == null) {
					List<IDServizioApplicativoDB> newListaIdSA = new ArrayList<IDServizioApplicativoDB>();
					IDServizioApplicativoDB idSA = new IDServizioApplicativoDB();
					idSA.setNome("-"); // elemento nullo di default
					idSA.setIdSoggettoProprietario(new IDSoggetto("-", "-"));
					newListaIdSA.add(idSA);
					if(listaIdSA!=null && !listaIdSA.isEmpty()) {
						newListaIdSA.addAll(listaIdSA);
					}
					listaIdSA = newListaIdSA;
				}
			}
			String [] saSoggetti = ServiziApplicativiHelper.toArray(listaIdSA);
			
			
			// ServiziApplicativi
			List<String> saFruitoriList = new ArrayList<String>();
			saFruitoriList.add("-");
			IDSoggetto idSoggettoFruitoreSelected = null;
			if(gestioneFruitori && this.nomeSoggettoFruitore!=null && this.tipoSoggettoFruitore!=null){
				try{
					
					idSoggettoFruitoreSelected = new IDSoggetto(this.tipoSoggettoFruitore, this.nomeSoggettoFruitore);
					
					String auth = this.fruizioneAutenticazione;
					if(auth==null || "".equals(auth)){
						auth = apsCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
					}
					
					org.openspcoop2.core.config.constants.CredenzialeTipo tipoAutenticazione = org.openspcoop2.core.config.constants.CredenzialeTipo.toEnumConstant(auth);
					Boolean appId = null;
					if(org.openspcoop2.core.config.constants.CredenzialeTipo.APIKEY.equals(tipoAutenticazione)) {
						ApiKeyState apiKeyState =  new ApiKeyState(null);
						appId = apiKeyState.appIdSelected;
					}
					
					List<IDServizioApplicativoDB> oldSilList = null;
					if(apsCore.isVisioneOggettiGlobale(userLogin)){
						oldSilList = saCore.soggettiServizioApplicativoList(idSoggettoFruitoreSelected,null,
								tipoAutenticazione, appId);
					}
					else {
						oldSilList = saCore.soggettiServizioApplicativoList(idSoggettoFruitoreSelected,userLogin,
								tipoAutenticazione, appId);
					}
					if(oldSilList!=null && oldSilList.size()>0){
						for (int i = 0; i < oldSilList.size(); i++) {
							saFruitoriList.add(oldSilList.get(i).getNome());		
						}
					}
				}catch(DriverConfigurazioneNotFound dNotFound){}

			}
			
			List<String> soggettiAutenticati = new ArrayList<String>();
			List<String> soggettiAutenticatiLabel = new ArrayList<String>();
			// lista soggetti autenticati per la creazione automatica
			CredenzialeTipo credenziale =  null;
			Boolean appIdSoggetti = null;
			if((this.erogazioneAutenticazione !=null && !"".equals(this.erogazioneAutenticazione)) && erogazioneIsSupportatoAutenticazioneSoggetti) {
				TipoAutenticazione tipoAutenticazione = TipoAutenticazione.toEnumConstant(this.erogazioneAutenticazione);
				credenziale = !tipoAutenticazione.equals(TipoAutenticazione.DISABILITATO) ? CredenzialeTipo.toEnumConstant(this.erogazioneAutenticazione) : null;
				if(CredenzialeTipo.APIKEY.equals(credenziale)) {
					ApiKeyState apiKeyState =  new ApiKeyState(null);
					appIdSoggetti = apiKeyState.appIdSelected;
				}
			}
			
			List<IDSoggettoDB> listSoggettiCompatibili = null;
			 
			if(apsCore.isVisioneOggettiGlobale(userLogin)){
				listSoggettiCompatibili = soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiCompatibiliAccordo, null, credenziale, appIdSoggetti, pddTipologiaSoggettoAutenticati );
			}else{
				listSoggettiCompatibili = soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiCompatibiliAccordo, userLogin, credenziale, appIdSoggetti, pddTipologiaSoggettoAutenticati);
			}
			
			if(listSoggettiCompatibili != null && listSoggettiCompatibili.size() >0 ) {
				
				soggettiAutenticati.add("-"); // elemento nullo di default
				soggettiAutenticatiLabel.add("-");
				for (IDSoggettoDB soggetto : listSoggettiCompatibili) {
					soggettiAutenticati.add(soggetto.getTipo() + "/"+ soggetto.getNome());
					soggettiAutenticatiLabel.add(apsHelper.getLabelNomeSoggetto(this.tipoProtocollo, soggetto.getTipo(), soggetto.getNome())); 
				}
			}
			

			// Controllo se il soggetto erogare appartiene ad una pdd di tipo operativo.
			if(this.tipoSoggettoErogatore!=null && !"".equals(this.tipoSoggettoErogatore) 
					&&  this.nomeSoggettoErogatore!=null && !"".equals(this.nomeSoggettoErogatore)){
				IDSoggetto idSoggettoEr = new IDSoggetto(this.tipoSoggettoErogatore, this.nomeSoggettoErogatore);
				if(!AccordiServizioParteSpecificaUtilities.isSoggettoOperativo(idSoggettoEr, apsCore)) {
					generaPortaApplicativa = false;
				}
			}
			
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.tipoProtocollo);
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = soggettiCore.getRegistryReader(this.protocolFactory); 
			this.configRegistryReader = soggettiCore.getConfigIntegrationReader(this.protocolFactory);
						
			// ID Accordo, i dati del servizio sono null, è presente solamente l'id dell'API
			IDServizio idAps = new IDServizio();
			if(as!=null) {
				idAps.setUriAccordoServizioParteComune(idAccordoFactory.getUriFromAccordo(as));
			}
			idAps.setPortType(this.portType);
			IDFruizione idFruizione = null;
			if(idSoggettoFruitoreSelected!=null) {
				idFruizione = new IDFruizione();
				idFruizione.setIdServizio(idAps);
				idFruizione.setIdFruitore(idSoggettoFruitoreSelected);
			}
			if(gestioneFruitori) {
				this.consoleConfiguration = this.consoleDynamicConfiguration.getDynamicConfigFruizioneAccordoServizioParteSpecifica(this.consoleOperationType, apsHelper, 
						this.registryReader, this.configRegistryReader, idFruizione);
			}
			else {
				this.consoleConfiguration = this.consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteSpecifica(this.consoleOperationType, apsHelper, 
						this.registryReader, this.configRegistryReader, idAps );
			}
			this.protocolProperties = apsHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);
			
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
			
			String servletList = null;
			String labelList = null;
			
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session);
			if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
				servletList = ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST;
				if(gestioneFruitori) {
					labelList = ErogazioniCostanti.LABEL_ASPS_FRUIZIONI;
				}
				else {
					labelList = ErogazioniCostanti.LABEL_ASPS_EROGAZIONI;
				}
			} else {
				servletList = AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST;
				if(gestioneFruitori) {
					labelList = AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI;
				}
				else {
					labelList = AccordiServizioParteSpecificaCostanti.LABEL_APS;
				}
			}
			
			String canaleAPI = as != null ? as.getCanale() : null;  

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento dati
			if(ServletUtils.isEditModeInProgress(this.editMode)){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, labelList,servletList);
				

				if(apsHelper.isShowGestioneWorkflowStatoDocumenti()){
					if(this.nomeservizio==null || "".equals(this.nomeservizio)){
						this.statoPackage=StatiAccordo.bozza.toString();
					}

					// Se l'accordo Comune che si riferisce ha stato operativo o finale modifico lo stato in operativo
					// Per visualizzare immediatamente all'utente
					if(as!=null && 
							(as.getStatoPackage().equals(StatiAccordo.operativo.toString()) || as.getStatoPackage().equals(StatiAccordo.finale.toString()))
							){
						this.statoPackage = StatiAccordo.operativo.toString(); 
					}

				}else{
					this.statoPackage=StatiAccordo.finale.toString();
				}

				if (this.nomeservizio == null) {
					this.nomeservizio = "";
					//					this.provider = "";
					//					this.accordo = "";
					this.servcorr = "";
					// this.servpub = "";
//					if(this.wsdlimpler.getValue() == null)
//						this.wsdlimpler.setValue(new byte[1]);
//					if(this.wsdlimplfru.getValue() == null)
//						this.wsdlimplfru.setValue(new byte[1]); 
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
					this.profilo = "-";
					if(ServiceBinding.SOAP.equals(this.serviceBinding)) {
						if(ptList!=null && ptList.length==2){
							this.portType = ptList[1]; // al posto 0 è presente '-'
							this.nomeservizio = this.portType;
						}
						else {
							this.portType = "-";
						}
					}else {
						this.portType = "-";
					}
					this.descrizione = "";
					this.httpsurl = "";
					this.httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
					this.httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
					this.httpshostverify = true;
					this.httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
					this.httpspath = "";
					this.httpstipo = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
					this.httpspwd = "";
					this.httpsstato = false;
					this.httpskeystore = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DEFAULT;
					this.httpspwdprivatekeytrust = "";
					this.httpspathkey = "";
					this.httpstipokey =ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
					this.httpspwdkey = "";
					this.httpspwdprivatekey = "";
					this.versione="1";
					this.canaleStato = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CANALE_STATO_DEFAULT;
					this.canale = "";
				}
				
				if(this.endpointtype==null) {
					if(apsHelper.isModalitaCompleta()==false) {
						this.endpointtype = TipiConnettore.HTTP.getNome();
					}
					else {
						this.endpointtype = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DISABILITATO;
					}
				}
				
				switch (this.serviceBinding) {
					case REST:
						if(this.nomeservizio==null || "".equals(this.nomeservizio)){
							this.nomeservizio = as.getNome();
						}
						
						break;
					case SOAP:
					default:
						if(this.portType!=null && !"".equals(this.portType) && !"-".equals(this.portType)){
		
							boolean ptValid = true;
		
							if(ptList!=null && ptList.length>0){
								// controllo che l'attuale port Type sia tra quelli presenti nell'accordo.
								boolean found = false;
								for (String portType : ptList) {
									if(portType.equals(this.portType)){
										found = true;
										break;
									}
								}
								if(!found){
									ptValid = false;
								}
		
							}
		
							if(ptValid){
		
								if(this.nomeservizio==null || "".equals(this.nomeservizio)){
									this.nomeservizio = this.portType;
								}
								else if(this.nomeservizio.equals(this.oldPortType)){
									this.nomeservizio = this.portType;
								}
		
								this.oldPortType = this.portType;
		
							}
							else{
		
								this.nomeservizio = null;
								this.portType = null;
								this.oldPortType = null;
		
							}
						}  else {
							if(ptList ==null || ptList.length < 1){
								this.nomeservizio = as.getNome();
							}
							else if(ptList!=null && ptList.length==2){
								this.portType = ptList[1]; // al posto 0 è presente '-'
								this.nomeservizio = this.portType;
							}
						}
		
					
					break;
				}

//				this.erogazioneServizioApplicativoServerEnabled = false;
				if(this.erogazioneServizioApplicativoServer==null)
					this.erogazioneServizioApplicativoServer = "";

				if(this.erogazioneRuolo==null || "".equals(this.erogazioneRuolo))
					this.erogazioneRuolo = "-";
				if(this.erogazioneAutenticazione==null || "".equals(this.erogazioneAutenticazione)) {
					this.erogazioneAutenticazione = apsCore.getAutenticazione_generazioneAutomaticaPorteApplicative();
					
					soggettiAutenticati = new ArrayList<String>();
					soggettiAutenticatiLabel = new ArrayList<String>();
					if(erogazioneIsSupportatoAutenticazioneSoggetti) {
						TipoAutenticazione tipoAutenticazione = TipoAutenticazione.toEnumConstant(this.erogazioneAutenticazione);
						credenziale = !tipoAutenticazione.equals(TipoAutenticazione.DISABILITATO) ? CredenzialeTipo.toEnumConstant(this.erogazioneAutenticazione) : null;
					}
					 
					if(apsCore.isVisioneOggettiGlobale(userLogin)){
						listSoggettiCompatibili = soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiCompatibiliAccordo, null, credenziale, appIdSoggetti, pddTipologiaSoggettoAutenticati );
					}else{
						listSoggettiCompatibili = soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiCompatibiliAccordo, userLogin, credenziale, appIdSoggetti, pddTipologiaSoggettoAutenticati);
					}
					
					if(listSoggettiCompatibili != null && listSoggettiCompatibili.size() >0 ) {
						soggettiAutenticati.add("-"); // elemento nullo di default
						soggettiAutenticatiLabel.add("-");
						for (IDSoggettoDB soggetto : listSoggettiCompatibili) {
							soggettiAutenticati.add(soggetto.getTipo() + "/"+ soggetto.getNome());
							soggettiAutenticatiLabel.add(apsHelper.getLabelNomeSoggetto(this.tipoProtocollo, soggetto.getTipo(), soggetto.getNome())); 
						}
					}
					
				}
				if(this.erogazioneAutorizzazione==null || "".equals(this.erogazioneAutorizzazione)){
					String tipoAutorizzazione = apsCore.getAutorizzazione_generazioneAutomaticaPorteApplicative();
					this.erogazioneAutorizzazione = AutorizzazioneUtilities.convertToStato(tipoAutorizzazione);
					if(TipoAutorizzazione.isAuthenticationRequired(tipoAutorizzazione))
						this.erogazioneAutorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
					if(TipoAutorizzazione.isRolesRequired(tipoAutorizzazione))
						this.erogazioneAutorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
					this.erogazioneAutorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(tipoAutorizzazione).getValue();
				}
				
				if(gestioneFruitori) {
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
				}
				
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
				if(httpsTrustVerifyCertS==null || "".equals(httpsTrustVerifyCertS)){
					httpsTrustVerifyCertS = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS ? Costanti.CHECK_BOX_ENABLED_TRUE : Costanti.CHECK_BOX_DISABLED;
					this.httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
				}


				String tipoSendas = ConnettoriCostanti.TIPO_SEND_AS[0];
				String tipoJms = ConnettoriCostanti.TIPI_CODE_JMS[0];

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
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				if(gestioneFruitori) {
					this.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, apsHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idFruizione);
				}
				else {
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, apsHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idAps);
				}

				dati = apsHelper.addServiziToDati(dati, this.nomeservizio, this.tiposervizio,  null, null, 
						this.provider, null, null, 
						soggettiList, soggettiListLabel, this.accordo, this.serviceBinding, this.formatoSpecifica, accordiList, accordiListLabel, this.servcorr, 
						this.wsdlimpler, this.wsdlimplfru, tipoOp, "0", tipiServizioCompatibiliAccordo, 
						this.profilo, this.portType, ptList, this.privato,uriAccordo,this.descrizione,-1l,this.statoPackage,this.statoPackage,
						this.versione,versioniProtocollo,this.validazioneDocumenti,
						saSoggetti,this.nomeSA,generaPortaApplicativa,null,
						this.controlloAccessiStato,
						this.erogazioneRuolo,this.erogazioneAutenticazione,this.erogazioneAutenticazioneOpzionale,this.erogazioneAutenticazionePrincipal, this.erogazioneAutenticazioneParametroList,this.erogazioneAutorizzazione,erogazioneIsSupportatoAutenticazioneSoggetti,
						this.erogazioneAutorizzazioneAutenticati, this.erogazioneAutorizzazioneRuoli, this.erogazioneAutorizzazioneRuoliTipologia, this.erogazioneAutorizzazioneRuoliMatch,
						soggettiAutenticati,soggettiAutenticatiLabel, this.erogazioneSoggettoAutenticato,
						this.tipoProtocollo, listaTipiProtocollo,
						soggettiFruitoriList, soggettiFruitoriListLabel, this.providerSoggettoFruitore, this.tipoSoggettoFruitore, this.nomeSoggettoFruitore,
						this.fruizioneServizioApplicativo,this.fruizioneRuolo,this.fruizioneAutenticazione,this.fruizioneAutenticazioneOpzionale,this.fruizioneAutenticazionePrincipal, this.fruizioneAutenticazioneParametroList, this.fruizioneAutorizzazione,
						this.fruizioneAutorizzazioneAutenticati, this.fruizioneAutorizzazioneRuoli, this.fruizioneAutorizzazioneRuoliTipologia, this.fruizioneAutorizzazioneRuoliMatch,
						saFruitoriList,gestioneToken, policyLabels, policyValues, 
						gestioneTokenPolicy, gestioneTokenOpzionale,
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_token,autorizzazione_tokenOptions,
						autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,false,
						this.canaleStato, canaleAPI, this.canale, canaleList, gestioneCanaliEnabled);

				// Controllo se richiedere il connettore
				
				if(!connettoreStatic) {
					boolean forceEnableConnettore = false;
					if( gestioneFruitori || generaPortaApplicativa ) {
						forceEnableConnettore = true;
					}
					
					dati = apsHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, 
							null,//(apsHelper.isModalitaCompleta() || !multitenant)?null:
							//	(generaPortaApplicativa?AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX : AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX), 
							this.url, this.nome,
							tipoJms, this.user,
							this.password, this.initcont, this.urlpgk,
							this.provurl, this.connfact, tipoSendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp, 
							this.httpsurl, this.httpstipologia,	this.httpshostverify, 
							this.httpsTrustVerifyCert, this.httpspath, this.httpstipo, this.httpspwd,
							this.httpsalgoritmo, this.httpsstato, this.httpskeystore,
							this.httpspwdprivatekeytrust, this.httpspathkey,
							this.httpstipokey, this.httpspwdkey, 
							this.httpspwdprivatekey, this.httpsalgoritmokey,
							this.httpsKeyAlias, this.httpsTrustStoreCRLs,
							this.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, null, null,
							null, null, null, null, null, null, true,
							isConnettoreCustomUltimaImmagineSalvata, 
							this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
							this.tempiRisposta_enabled, this.tempiRisposta_connectionTimeout, this.tempiRisposta_readTimeout, this.tempiRisposta_tempoMedioRisposta,
							this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
							this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
							this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
							this.autenticazioneToken,this.token_policy,
							listExtendedConnettore, forceEnableConnettore,
							this.tipoProtocollo, forceHttps, forceHttpsClient, visualizzaSezioneApplicativiServerEnabled, this.erogazioneServizioApplicativoServerEnabled,
							this.erogazioneServizioApplicativoServer, saSoggetti);
					
					// url suggerita
					if(urlAPI!=null) {
						for (DataElement dataElement : dati) {
							if(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL.equals(dataElement.getName())) {
								if(dataElement.getValue()==null || dataElement.getValue().endsWith("://")) {
									dataElement.setValue(urlAPI);
								}
								break;
							}
						}
					}
				}
					
				// aggiunta campi custom
				dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
						ForwardParams.ADD());
			}

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
			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.serviziCheckData(tipoOp, soggettiList,
					accordiList, this.nomeservizio, this.tiposervizio, 
					(this.versione!=null && !"".equals(this.versione)) ? Integer.parseInt(this.versione) : 1,
					this.nomeservizio, this.tiposervizio, this.provider,
					this.nomeSoggettoErogatore, this.tipoSoggettoErogatore,
					this.accordo, this.serviceBinding, this.servcorr, this.endpointtype,
					this.url, this.nome, this.tipo, this.user,
					this.password, this.initcont, this.urlpgk, this.provurl,
					this.connfact, this.sendas, this.wsdlimpler,
					this.wsdlimplfru, "0", this.profilo, this.portType, ptList,
					accordoPrivato,this.privato, 
					this.httpsurl, this.httpstipologia, this.httpshostverify, 
					this.httpsTrustVerifyCert, this.httpspath, this.httpstipo,
					this.httpspwd, this.httpsalgoritmo, this.httpsstato,
					this.httpskeystore, this.httpspwdprivatekeytrust,
					this.httpspathkey, this.httpstipokey,
					this.httpspwdkey, this.httpspwdprivatekey,
					this.httpsalgoritmokey,
					this.httpsKeyAlias, this.httpsTrustStoreCRLs,
					this.tipoconn,this.versione,this.validazioneDocumenti,null,this.autenticazioneHttp,
					this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
					this.tempiRisposta_enabled, this.tempiRisposta_connectionTimeout, this.tempiRisposta_readTimeout, this.tempiRisposta_tempoMedioRisposta,
					this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
					this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
					this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
					null,this.erogazioneRuolo,this.erogazioneAutenticazione,this.erogazioneAutenticazioneOpzionale,this.erogazioneAutenticazionePrincipal,this.erogazioneAutenticazioneParametroList,this.erogazioneAutorizzazione,
					this.erogazioneAutorizzazioneAutenticati, this.erogazioneAutorizzazioneRuoli, this.erogazioneAutorizzazioneRuoliTipologia, this.erogazioneAutorizzazioneRuoliMatch,erogazioneIsSupportatoAutenticazioneSoggetti,
					generaPortaApplicativa, listExtendedConnettore,
					this.fruizioneServizioApplicativo,this.fruizioneRuolo,this.fruizioneAutenticazione,this.fruizioneAutenticazioneOpzionale,this.fruizioneAutenticazionePrincipal, this.fruizioneAutenticazioneParametroList, this.fruizioneAutorizzazione,
					this.fruizioneAutorizzazioneAutenticati, this.fruizioneAutorizzazioneRuoli, this.fruizioneAutorizzazioneRuoliTipologia, this.fruizioneAutorizzazioneRuoliMatch,
					this.tipoProtocollo, allegatoXacmlPolicy, 
					this.descrizione, this.tipoSoggettoFruitore, this.nomeSoggettoFruitore,
					this.autenticazioneToken,this.token_policy,this.erogazioneServizioApplicativoServerEnabled,
					this.erogazioneServizioApplicativoServer, this.canaleStato, this.canale, gestioneCanaliEnabled);

			if(isOk){
				if(generaPortaApplicativa && apsHelper.isModalitaCompleta() && (this.nomeSA==null || "".equals(this.nomeSA) || "-".equals(this.nomeSA))){
					if(saSoggetti==null || saSoggetti.length==0 || (saSoggetti.length==1 && "-".equals(saSoggetti[0]))){
						pd.setMessage(MessageFormat.format(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_PRIMA_DI_POTER_DEFINIRE_UN_ACCORDO_PARTE_SPECIFICA_DEVE_ESSERE_CREATO_UN_SERVIZIO_APPLICATIVO_EROGATO_DAL_SOGGETTO_X_Y,
								this.tipoSoggettoErogatore, this.nomeSoggettoErogatore));
					}
					else{
						pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_NON_E_POSSIBILE_CREARE_L_ACCORDO_PARTE_SPECIFICA_SENZA_SELEZIONARE_UN_SERVIZIO_APPLICATIVO_EROGATORE);
					}
					isOk = false;
				}
			}

			// updateDynamic
			if(isOk) {
				if(gestioneFruitori) {
					this.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, apsHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idFruizione);
				}
				else {
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, apsHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idAps);
				}
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
					idAps = apsHelper.getIDServizioFromValues(this.tiposervizio, this.nomeservizio, this.provider, this.versione);
					//validazione campi dinamici
					if(gestioneFruitori) {
						this.consoleDynamicConfiguration.validateDynamicConfigFruizioneAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, apsHelper, this.protocolProperties,  
								this.registryReader, this.configRegistryReader, idFruizione);
					}
					else {
						this.consoleDynamicConfiguration.validateDynamicConfigAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, apsHelper, this.protocolProperties, 
								this.registryReader, this.configRegistryReader, idAps);
					}
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, labelList, servletList);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				// update della configurazione 
				if(gestioneFruitori) {
					this.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, apsHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idFruizione);
				}
				else {
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, apsHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idAps);
				}

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apsHelper.addServiziToDati(dati, this.nomeservizio, this.tiposervizio, null, null,  
						this.provider, null, null, 
						soggettiList, soggettiListLabel, this.accordo, this.serviceBinding, this.formatoSpecifica, accordiList, accordiListLabel,
						this.servcorr, this.wsdlimpler, this.wsdlimplfru, tipoOp, "0", tipiServizioCompatibiliAccordo, 
						this.profilo, this.portType, ptList, this.privato,uriAccordo,this.descrizione,-1l,this.statoPackage,
						this.statoPackage,this.versione,versioniProtocollo,this.validazioneDocumenti,
						saSoggetti,this.nomeSA,generaPortaApplicativa,null,
						this.controlloAccessiStato,
						this.erogazioneRuolo,this.erogazioneAutenticazione,this.erogazioneAutenticazioneOpzionale,this.erogazioneAutenticazionePrincipal, this.erogazioneAutenticazioneParametroList, this.erogazioneAutorizzazione,erogazioneIsSupportatoAutenticazioneSoggetti,
						this.erogazioneAutorizzazioneAutenticati, this.erogazioneAutorizzazioneRuoli, this.erogazioneAutorizzazioneRuoliTipologia, this.erogazioneAutorizzazioneRuoliMatch,
						soggettiAutenticati, soggettiAutenticatiLabel, this.erogazioneSoggettoAutenticato,
						this.tipoProtocollo, listaTipiProtocollo,
						soggettiFruitoriList, soggettiFruitoriListLabel, this.providerSoggettoFruitore, this.tipoSoggettoFruitore, this.nomeSoggettoFruitore,
						this.fruizioneServizioApplicativo,this.fruizioneRuolo,this.fruizioneAutenticazione,this.fruizioneAutenticazioneOpzionale,this.fruizioneAutenticazionePrincipal, this.fruizioneAutenticazioneParametroList, this.fruizioneAutorizzazione,
						this.fruizioneAutorizzazioneAutenticati, this.fruizioneAutorizzazioneRuoli, this.fruizioneAutorizzazioneRuoliTipologia, this.fruizioneAutorizzazioneRuoliMatch,
						saFruitoriList,gestioneToken, policyLabels, policyValues, 
						gestioneTokenPolicy,  gestioneTokenOpzionale,
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_token,autorizzazione_tokenOptions,
						autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,false,
						this.canaleStato, canaleAPI, this.canale, canaleList, gestioneCanaliEnabled);

				if(!connettoreStatic) {
					boolean forceEnableConnettore = false;
					if( gestioneFruitori || generaPortaApplicativa ) {
						forceEnableConnettore = true;
					}
					
					dati = apsHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, 
							null, //(apsHelper.isModalitaCompleta() || !multitenant)?null:
							//	(generaPortaApplicativa?AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX : AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX), 
							this.url, this.nome, this.tipo, this.user,
							this.password, this.initcont, this.urlpgk,
							this.provurl, this.connfact, this.sendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp, 
							this.httpsurl, this.httpstipologia, this.httpshostverify, 
							this.httpsTrustVerifyCert, this.httpspath, this.httpstipo,
							this.httpspwd, this.httpsalgoritmo, this.httpsstato,
							this.httpskeystore, this.httpspwdprivatekeytrust,
							this.httpspathkey, this.httpstipokey,
							this.httpspwdkey, this.httpspwdprivatekey,
							this.httpsalgoritmokey, 
							this.httpsKeyAlias, this.httpsTrustStoreCRLs,
							this.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, null, null,
							null, null, null, null, null, null, true,
							isConnettoreCustomUltimaImmagineSalvata, 
							this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
							this.tempiRisposta_enabled, this.tempiRisposta_connectionTimeout, this.tempiRisposta_readTimeout, this.tempiRisposta_tempoMedioRisposta,
							this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
							this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
							this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
							this.autenticazioneToken,this.token_policy,
							listExtendedConnettore, forceEnableConnettore,
							this.tipoProtocollo, forceHttps, forceHttpsClient, visualizzaSezioneApplicativiServerEnabled, this.erogazioneServizioApplicativoServerEnabled,
							this.erogazioneServizioApplicativoServer, saSoggetti);
					
					// url suggerita
					if(urlAPI!=null) {
						for (DataElement dataElement : dati) {
							if(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL.equals(dataElement.getName())) {
								if(dataElement.getValue()==null || dataElement.getValue().endsWith("://")) {
									dataElement.setValue(urlAPI);
								}
								break;
							}
						}
					}
					
				}

				// aggiunta campi custom
				dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
						ForwardParams.ADD());
			}

			// Inserisco il servizio nel db
			long idProv = Long.parseLong(this.provider);
			long idAcc = Long.parseLong(this.accordo);

			// Fix per versione
			AccordoServizioParteSpecifica asps = null;
			boolean alreadyExists = false;
			if(gestioneFruitori || gestioneErogatori) {
				if(apsCore.existsAccordoServizioParteSpecifica(idAps)) {
					asps = apsCore.getServizio(idAps);
					alreadyExists = true;
				}
				else {
					asps = new AccordoServizioParteSpecifica();
				}
			}
			else {
				asps = new AccordoServizioParteSpecifica();
			}
			
			// nome accordo
			as = apcCore.getAccordoServizioSintetico(idAcc);
			
			if(!alreadyExists) {
				asps.setNome(this.nomeservizio);
				asps.setTipo(this.tiposervizio);
				asps.setDescrizione(this.descrizione);
				
				// Che i seguenti valori siano identici vengono controllati nel check
				asps.setIdAccordo(idAcc);
				asps.setAccordoServizioParteComune(idAccordoFactory.getUriFromAccordo(as));
				
				asps.setIdSoggetto(idProv);
				asps.setNomeSoggettoErogatore(this.nomeSoggettoErogatore);
				asps.setTipoSoggettoErogatore(this.tipoSoggettoErogatore);
				
				// Tipologia di Servizio
				asps.setTipologiaServizio(((this.servcorr != null) && this.servcorr.equals(Costanti.CHECK_BOX_ENABLED)) ? TipologiaServizio.CORRELATO : TipologiaServizio.NORMALE);
				
				// Utente
				asps.setSuperUser(ServletUtils.getUserLoginFromSession(session));
				
				// Versione Protocollo
				if ("-".equals(this.profilo) == false)
					asps.setVersioneProtocollo(this.profilo);
				else
					asps.setVersioneProtocollo(null);

				// Privato
				asps.setPrivato(this.privato);

				// Wsdl
				String wsdlimplerS = this.wsdlimpler.getValue() != null ? new String(this.wsdlimpler.getValue()) : null; 
				asps.setByteWsdlImplementativoErogatore(((wsdlimplerS != null) && !wsdlimplerS.trim().replaceAll("\n", "").equals("")) ? wsdlimplerS.trim().getBytes() : null);
				String wsdlimplfruS = this.wsdlimplfru.getValue() != null ? new String(this.wsdlimplfru.getValue()) : null; 
				asps.setByteWsdlImplementativoFruitore(((wsdlimplfruS != null) && !wsdlimplfruS.trim().replaceAll("\n", "").equals("")) ? wsdlimplfruS.trim().getBytes() : null);
				
				// PortType
				if (this.portType != null && !"".equals(this.portType) && !"-".equals(this.portType))
					asps.setPortType(this.portType);
				
				// Versione
				if(apsCore.isSupportatoVersionamentoAccordiServizioParteSpecifica(this.tipoProtocollo)){
					if(this.versione!=null && !"".equals(this.versione))
						asps.setVersione(Integer.parseInt(this.versione));
					else
						asps.setVersione(1);
				}else{
					asps.setVersione(1);
				}

				// stato
				asps.setStatoPackage(this.statoPackage);
			}

			

			// Connettore
			Connettore connettore = null;
			if(!connettoreStatic) {
				connettore = new Connettore();
				// this.nomeservizio);
				if (this.endpointtype.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM))
					connettore.setTipo(this.tipoconn);
				else
					connettore.setTipo(this.endpointtype);
	
				apsHelper.fillConnettore(connettore, this.connettoreDebug, this.endpointtype, this.endpointtype, this.tipoconn, this.url,
						this.nome, this.tipo, this.user, this.password,
						this.initcont, this.urlpgk, this.url, this.connfact,
						this.sendas, this.httpsurl, this.httpstipologia, this.httpshostverify, 
						this.httpsTrustVerifyCert, this.httpspath, this.httpstipo,
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

			if(asps.getConfigurazioneServizio()==null)
				asps.setConfigurazioneServizio(new ConfigurazioneServizio());
			if(apsHelper.isModalitaCompleta() || (!generaPortaApplicativa && !gestioneFruitori)) {
				asps.getConfigurazioneServizio().setConnettore(connettore);
			}

			if(gestioneFruitori) {
				Fruitore fruitore = new Fruitore();
				fruitore.setTipo(this.tipoSoggettoFruitore);
				fruitore.setNome(this.nomeSoggettoFruitore);
				fruitore.setStatoPackage(this.statoPackage);
				fruitore.setConnettore(connettore);
				asps.addFruitore(fruitore);
			}
			
			//			Spostato sopra a livello di edit in progress			
			//			// Se l'accordo Comune che si riferisce ha stato operativo o finale modifico lo stato in operativo
			//			if(as.getStatoPackage().equals(StatiAccordo.operativo.toString()) || as.getStatoPackage().equals(StatiAccordo.finale.toString())){
			//				asps.setStatoPackage(StatiAccordo.operativo.toString()); 
			//			}

			// Check stato
			if(apsHelper.isShowGestioneWorkflowStatoDocumenti()){

				ValidazioneStatoPackageException validazione = null;
				try{
					boolean gestioneWsdlImplementativo = apcCore.showPortiAccesso(this.tipoProtocollo, this.serviceBinding, this.formatoSpecifica);
					boolean checkConnettore = !gestioneFruitori && !gestioneErogatori;
					apsCore.validaStatoAccordoServizioParteSpecifica(asps, gestioneWsdlImplementativo, checkConnettore);
				}catch(ValidazioneStatoPackageException validazioneException){
					validazione = validazioneException;
				}
				if(validazione==null && gestioneFruitori) {
					try{
						apsCore.validaStatoFruitoreAccordoServizioParteSpecifica(asps.getFruitore(0), asps);
					}catch(ValidazioneStatoPackageException validazioneException){
					}
				}
				if(validazione!=null) {

					// Setto messaggio di errore
					pd.setMessage(validazione.toString());

					// setto la barra del titolo
					ServletUtils.setPageDataTitle_ServletAdd(pd, labelList,servletList);

					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();

					// update della configurazione 
					if(gestioneFruitori) {
						this.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, apsHelper, this.protocolProperties, 
								this.registryReader, this.configRegistryReader, idFruizione);
					}
					else {
						this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, apsHelper, this.protocolProperties, 
								this.registryReader, this.configRegistryReader, idAps);
					}

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());
					
					dati = apsHelper.addServiziToDati(dati, this.nomeservizio, this.tiposervizio, null, null,  
							this.provider, null, null, 
							soggettiList, soggettiListLabel, this.accordo, this.serviceBinding, this.formatoSpecifica, accordiList, accordiListLabel, this.servcorr, 
							this.wsdlimpler, this.wsdlimplfru, tipoOp, "0", tipiServizioCompatibiliAccordo, 
							this.profilo, this.portType, ptList, this.privato,uriAccordo,this.descrizione,-1l,this.statoPackage,
							this.statoPackage,this.versione,versioniProtocollo,this.validazioneDocumenti,
							saSoggetti,this.nomeSA,generaPortaApplicativa,null,
							this.controlloAccessiStato,
							this.erogazioneRuolo,this.erogazioneAutenticazione,this.erogazioneAutenticazioneOpzionale,this.erogazioneAutenticazionePrincipal, this.erogazioneAutenticazioneParametroList, this.erogazioneAutorizzazione,erogazioneIsSupportatoAutenticazioneSoggetti,
							this.erogazioneAutorizzazioneAutenticati, this.erogazioneAutorizzazioneRuoli, this.erogazioneAutorizzazioneRuoliTipologia, this.erogazioneAutorizzazioneRuoliMatch,
							soggettiAutenticati, soggettiAutenticatiLabel, this.erogazioneSoggettoAutenticato,
							this.tipoProtocollo, listaTipiProtocollo,
							soggettiFruitoriList, soggettiFruitoriListLabel, this.providerSoggettoFruitore, this.tipoSoggettoFruitore, this.nomeSoggettoFruitore,
							this.fruizioneServizioApplicativo,this.fruizioneRuolo,this.fruizioneAutenticazione,this.fruizioneAutenticazioneOpzionale,this.fruizioneAutenticazionePrincipal, this.fruizioneAutenticazioneParametroList, this.fruizioneAutorizzazione,
							this.fruizioneAutorizzazioneAutenticati, this.fruizioneAutorizzazioneRuoli, this.fruizioneAutorizzazioneRuoliTipologia, this.fruizioneAutorizzazioneRuoliMatch,
							saFruitoriList,gestioneToken, policyLabels, policyValues, 
							gestioneTokenPolicy,  gestioneTokenOpzionale,
							gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
							autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
							autorizzazione_token,autorizzazione_tokenOptions,
							autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,false,
							this.canaleStato, canaleAPI, this.canale, canaleList, gestioneCanaliEnabled);

					if(!connettoreStatic) {
					
						boolean forceEnableConnettore = false;
						if( gestioneFruitori || generaPortaApplicativa ) {
							forceEnableConnettore = true;
						}
						
						dati = apsHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, 
								null, //(apsHelper.isModalitaCompleta() || !multitenant)?null:
								//	(generaPortaApplicativa?AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX : AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX), 
								this.url, this.nome, this.tipo, this.user,
								this.password, this.initcont, this.urlpgk,
								this.provurl, this.connfact, this.sendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp, 
								this.httpsurl, this.httpstipologia,	this.httpshostverify, 
								this.httpsTrustVerifyCert, this.httpspath, this.httpstipo,
								this.httpspwd, this.httpsalgoritmo, this.httpsstato,
								this.httpskeystore, this.httpspwdprivatekeytrust,
								this.httpspathkey, this.httpstipokey,
								this.httpspwdkey, this.httpspwdprivatekey,
								this.httpsalgoritmokey, 
								this.httpsKeyAlias, this.httpsTrustStoreCRLs,
								this.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, null, null,
								null, null, null, null, null, null, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
								this.tempiRisposta_enabled, this.tempiRisposta_connectionTimeout, this.tempiRisposta_readTimeout, this.tempiRisposta_tempoMedioRisposta,
								this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
								this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
								this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
								this.autenticazioneToken,this.token_policy,
								listExtendedConnettore, forceEnableConnettore,
								this.tipoProtocollo, forceHttps, forceHttpsClient, visualizzaSezioneApplicativiServerEnabled, this.erogazioneServizioApplicativoServerEnabled,
								this.erogazioneServizioApplicativoServer, saSoggetti);
						
					}

					// aggiunta campi custom
					dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties);

					pd.setDati(dati);


					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
							ForwardParams.ADD());

				}
			}

			IDSoggetto soggettoErogatore = new IDSoggetto(this.tipoSoggettoErogatore,this.nomeSoggettoErogatore);
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(this.tiposervizio, this.nomeservizio, soggettoErogatore, 
					(this.versione==null || "".equals(this.versione))? 1 : Integer.parseInt(this.versione));

			IDSoggetto idFruitore = null;
			if(generaPortaDelegata){
				idFruitore = new IDSoggetto(this.tipoSoggettoFruitore, this.nomeSoggettoFruitore);
			}
			
			String autenticazione = null, autenticazioneOpzionale = null;
			TipoAutenticazionePrincipal autenticazionePrincipal = null;
			List<String> autenticazioneParametroList = null;
			if(CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO_PUBBLICO.equals(this.controlloAccessiStato)) {
				autenticazione = TipoAutenticazione.DISABILITATO.getValue();
			}
			else {
				if(generaPortaApplicativa){
					
					boolean forceDisableOptional = false;
					if(apsHelper.isProfiloModIPA(this.tipoProtocollo)) {
						forceDisableOptional = apsHelper.forceHttpsClientProfiloModiPA(IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as), this.portType);
					}
					
					autenticazione = this.erogazioneAutenticazione;
					if(forceDisableOptional) {
						autenticazioneOpzionale = Costanti.CHECK_BOX_DISABLED;
					}
					else{
						if(apsHelper.isProfiloModIPA(this.tipoProtocollo)) {
							autenticazioneOpzionale = Costanti.CHECK_BOX_ENABLED;
						}
						else {
							autenticazioneOpzionale = this.erogazioneAutenticazioneOpzionale;
						}
					}
					autenticazionePrincipal = this.erogazioneAutenticazionePrincipal;
					autenticazioneParametroList = this.erogazioneAutenticazioneParametroList;
				}
				if(generaPortaDelegata){
					autenticazione = this.fruizioneAutenticazione;
					autenticazioneOpzionale = this.fruizioneAutenticazioneOpzionale;
					autenticazionePrincipal = this.fruizioneAutenticazionePrincipal;
					autenticazioneParametroList = this.fruizioneAutenticazioneParametroList;
				}
			}
			
			String autorizzazione = null, autorizzazioneAutenticati = null, autorizzazioneRuoli = null, autorizzazioneRuoliTipologia = null, autorizzazioneRuoliMatch = null;
			if(CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO_PUBBLICO.equals(this.controlloAccessiStato)) {
				autorizzazione = TipoAutorizzazione.DISABILITATO.getValue();
			}
			else {
				if(generaPortaApplicativa){
					if(apsHelper.isProfiloModIPA(this.tipoProtocollo)) {
						boolean forceDisableOptional = apsHelper.forceHttpsClientProfiloModiPA(IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as), this.portType);
						if(!forceDisableOptional) {
							autorizzazione = AutorizzazioneUtilities.STATO_DISABILITATO;
						}
						else {
							autorizzazione = this.erogazioneAutorizzazione;
						}
					}
					else {
						autorizzazione = this.erogazioneAutorizzazione;
					}
					autorizzazioneAutenticati = this.erogazioneAutorizzazioneAutenticati;
					autorizzazioneRuoli = this.erogazioneAutorizzazioneRuoli;
					autorizzazioneRuoliTipologia = this.erogazioneAutorizzazioneRuoliTipologia;
					autorizzazioneRuoliMatch = this.erogazioneAutorizzazioneRuoliMatch;
				}
				if(generaPortaDelegata){
					autorizzazione = this.fruizioneAutorizzazione;
					autorizzazioneAutenticati = this.fruizioneAutorizzazioneAutenticati;
					autorizzazioneRuoli = this.fruizioneAutorizzazioneRuoli;
					autorizzazioneRuoliTipologia = this.fruizioneAutorizzazioneRuoliTipologia;
					autorizzazioneRuoliMatch = this.fruizioneAutorizzazioneRuoliMatch;
				}
			}
		
			String servizioApplicativo = null, ruolo = null;
			String soggettoAutenticato = null;
			if(generaPortaApplicativa){
				servizioApplicativo = this.nome;
				ruolo = this.erogazioneRuolo;
				soggettoAutenticato = this.erogazioneSoggettoAutenticato;
			}
			if(generaPortaDelegata){
				servizioApplicativo = this.fruizioneServizioApplicativo;
				ruolo = this.fruizioneRuolo;
			}
			
			AccordiServizioParteSpecificaUtilities.create(asps, alreadyExists, 
					idServizio, idFruitore, this.tipoProtocollo, this.serviceBinding, 
					idProv, 
					connettore, 
					generaPortaApplicativa, generaPortaDelegata, 
					autenticazione, autenticazioneOpzionale, autenticazionePrincipal, autenticazioneParametroList,
					autorizzazione, autorizzazioneAutenticati, autorizzazioneRuoli, autorizzazioneRuoliTipologia, autorizzazioneRuoliMatch, 
					servizioApplicativo, ruolo, soggettoAutenticato, 
					autorizzazione_tokenOptions, 
					autorizzazioneScope, scope, autorizzazioneScopeMatch, allegatoXacmlPolicy,
					gestioneToken, 
					gestioneTokenPolicy, gestioneTokenOpzionale, 
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward, 
					autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail, 
					this.protocolProperties, this.consoleOperationType, 
					apsCore, apsHelper, this.erogazioneServizioApplicativoServer, this.canaleStato, this.canale, gestioneCanaliEnabled);

			// cancello i file temporanei
			apsHelper.deleteBinaryParameters(this.wsdlimpler,this.wsdlimplfru);
			apsHelper.deleteBinaryProtocolPropertiesTmpFiles(this.protocolProperties);
			
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			if(apsCore.isSetSearchAfterAdd()) {
				apsCore.setSearchAfterAdd(Liste.SERVIZI, asps.getNome(), session, ricerca);
			}
			
			boolean [] permessi = AccordiServizioParteSpecificaUtilities.getPermessiUtente(apsHelper);
			List<AccordoServizioParteSpecifica> listaAccordi = null;
			if(apsCore.isVisioneOggettiGlobale(userLogin)){
				listaAccordi = apsCore.soggettiServizioList(null, ricerca,permessi, gestioneFruitori, gestioneErogatori);
			}else{
				listaAccordi = apsCore.soggettiServizioList(userLogin, ricerca, permessi, gestioneFruitori, gestioneErogatori);
			}
			
			if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
				apsHelper.prepareErogazioniList(ricerca, listaAccordi);
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.ADD());
			}

			apsHelper.prepareServiziList(ricerca, listaAccordi);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, ForwardParams.ADD());



		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
					ForwardParams.ADD());
		}  
	}
	
}
