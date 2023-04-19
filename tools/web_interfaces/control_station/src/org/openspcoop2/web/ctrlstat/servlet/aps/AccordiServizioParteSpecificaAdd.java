/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
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
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
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

		AccordiServizioParteSpecificaAddStrutsBean strutsBean = new AccordiServizioParteSpecificaAddStrutsBean();
		
		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		strutsBean.consoleOperationType = ConsoleOperationType.ADD;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.ADD;

		try {
			
			ErogazioniHelper apsHelper = new ErogazioniHelper(request, pd, session);
			
			strutsBean.editMode = apsHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);

			strutsBean.nomeservizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO);
			strutsBean.tiposervizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO);
			strutsBean.provider = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_EROGATORE);
			strutsBean.accordo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO);
			strutsBean.servcorr = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_SERVIZIO_CORRELATO);
			// this.servpub = apsHelper.getParameter("servpub");
			//			strutsBean.endpointtype = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE );
			
			String erogazioneServizioApplicativoServerEnabledS = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ABILITA_USO_APPLICATIVO_SERVER);
			strutsBean.erogazioneServizioApplicativoServerEnabled = ServletUtils.isCheckBoxEnabled(erogazioneServizioApplicativoServerEnabledS);
			strutsBean.erogazioneServizioApplicativoServer = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_APPLICATIVO_SERVER);
			
			strutsBean.endpointtype = apsHelper.readEndPointType();
			strutsBean.tipoconn = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO );
			strutsBean.autenticazioneHttp = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);

			strutsBean.connettoreDebug = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);

			strutsBean.controlloAccessiStato = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO);
			
			strutsBean.erogazioneRuolo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_RUOLO);
			strutsBean.erogazioneAutenticazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE);
			strutsBean.erogazioneAutenticazioneOpzionale = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE_OPZIONALE);
			String erogazioneAutenticazionePrincipalTipo = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TIPO);
			strutsBean.erogazioneAutenticazionePrincipal = TipoAutenticazionePrincipal.toEnumConstant(erogazioneAutenticazionePrincipalTipo, false);
			strutsBean.erogazioneAutenticazioneParametroList = apsHelper.convertFromDataElementValue_parametroAutenticazioneList(strutsBean.erogazioneAutenticazione, strutsBean.erogazioneAutenticazionePrincipal);
			strutsBean.erogazioneAutorizzazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE);
			strutsBean.erogazioneAutorizzazioneAutenticati = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_AUTENTICAZIONE);
			strutsBean.erogazioneAutorizzazioneRuoli = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLI);
			strutsBean.erogazioneAutorizzazioneRuoliTipologia = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_TIPOLOGIA);
			strutsBean.erogazioneAutorizzazioneRuoliMatch = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_MATCH);
			strutsBean.erogazioneSoggettoAutenticato = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_SOGGETTO_AUTENTICATO);

			// token policy
			String autenticazioneTokenS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			strutsBean.autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			strutsBean.token_policy = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
			
			// proxy
			strutsBean.proxy_enabled = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			strutsBean.proxy_hostname = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			strutsBean.proxy_port = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			strutsBean.proxy_username = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			strutsBean.proxy_password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);

			// tempi risposta
			strutsBean.tempiRisposta_enabled = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			strutsBean.tempiRisposta_connectionTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			strutsBean.tempiRisposta_readTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			strutsBean.tempiRisposta_tempoMedioRisposta = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
			
			// opzioni avanzate
			strutsBean.transfer_mode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			strutsBean.transfer_mode_chunk_size = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			strutsBean.redirect_mode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			strutsBean.redirect_max_hop = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			strutsBean.opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(apsHelper,strutsBean.transfer_mode, strutsBean.redirect_mode);

			// http
			strutsBean.url = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL  );
			if(TipiConnettore.HTTP.toString().equals(strutsBean.endpointtype)){
				strutsBean.user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				strutsBean.password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}

			// jms
			strutsBean.nome = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			strutsBean.tipo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			strutsBean.initcont = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			strutsBean.urlpgk = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			strutsBean.provurl = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			strutsBean.connfact = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			strutsBean.sendas = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(strutsBean.endpointtype)){
				strutsBean.user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				strutsBean.password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}

			// https
			strutsBean.httpsurl = strutsBean.url;
			strutsBean.httpstipologia = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE );
			strutsBean.httpshostverifyS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			String httpsTrustVerifyCertS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS );
			strutsBean.httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
			strutsBean.httpspath = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION );
			strutsBean.httpstipo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			strutsBean.httpspwd = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			strutsBean.httpsalgoritmo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			strutsBean.httpsstatoS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			strutsBean.httpskeystore = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			strutsBean.httpspwdprivatekeytrust = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			strutsBean.httpspathkey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			strutsBean.httpstipokey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			strutsBean.httpspwdkey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			strutsBean.httpspwdprivatekey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			strutsBean.httpsalgoritmokey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			strutsBean.httpsKeyAlias = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
			strutsBean.httpsTrustStoreCRLs = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
			strutsBean.httpsTrustStoreOCSPPolicy = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
			if(TipiConnettore.HTTPS.toString().equals(strutsBean.endpointtype)){
				strutsBean.user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				strutsBean.password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// file
			strutsBean.requestOutputFileName = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			strutsBean.requestOutputFileName_permissions = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS);
			strutsBean.requestOutputFileNameHeaders = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			strutsBean.requestOutputFileNameHeaders_permissions = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_PERMISSIONS);
			strutsBean.requestOutputParentDirCreateIfNotExists = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			strutsBean.requestOutputOverwriteIfExists = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			strutsBean.responseInputMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			strutsBean.responseInputFileName = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			strutsBean.responseInputFileNameHeaders = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			strutsBean.responseInputDeleteAfterRead = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			strutsBean.responseInputWaitTime = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);




			strutsBean.profilo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROFILO);

			strutsBean.wsdlimpler = apsHelper.getBinaryParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
			strutsBean.wsdlimplfru = apsHelper.getBinaryParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE);
			strutsBean.portType = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE);

			String priv = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PRIVATO);
			strutsBean.privato = ServletUtils.isCheckBoxEnabled(priv);

			strutsBean.descrizione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_DESCRIZIONE);
			strutsBean.statoPackage = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);

			strutsBean.versione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE);
			strutsBean.nomeSA = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SA);
			
			
			strutsBean.providerSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE);
			
			strutsBean.fruizioneServizioApplicativo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUIZIONE_NOME_SA);
			strutsBean.fruizioneRuolo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_RUOLO);
			strutsBean.fruizioneAutenticazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE);
			strutsBean.fruizioneAutenticazioneOpzionale = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE_OPZIONALE);
			String fruizioneAutenticazionePrincipalTipo = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TIPO);
			strutsBean.fruizioneAutenticazionePrincipal = TipoAutenticazionePrincipal.toEnumConstant(fruizioneAutenticazionePrincipalTipo, false);
			strutsBean.fruizioneAutenticazioneParametroList = apsHelper.convertFromDataElementValue_parametroAutenticazioneList(strutsBean.fruizioneAutenticazione, strutsBean.fruizioneAutenticazionePrincipal);
			strutsBean.fruizioneAutorizzazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE);
			strutsBean.fruizioneAutorizzazioneAutenticati = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_AUTENTICAZIONE);
			strutsBean.fruizioneAutorizzazioneRuoli = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLI);
			strutsBean.fruizioneAutorizzazioneRuoliTipologia = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_TIPOLOGIA);
			strutsBean.fruizioneAutorizzazioneRuoliMatch = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_MATCH);
			
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
			
			String autorizzazioneAutenticatiToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_TOKEN);
			String autorizzazioneRuoliToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI_TOKEN);
			String autorizzazioneRuoliTipologiaToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA_TOKEN);
			String autorizzazioneRuoliMatchToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_MATCH_TOKEN);
			
			String autorizzazione_token = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
			String autorizzazione_tokenOptions = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
			String autorizzazioneScope = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
			String autorizzazioneScopeMatch = apsHelper.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
			String scope = apsHelper.getParameter(CostantiControlStation.PARAMETRO_SCOPE);
			
			BinaryParameter allegatoXacmlPolicy = apsHelper.getBinaryParameter(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
			
			String identificazioneAttributiStato = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_STATO);
			String [] attributeAuthoritySelezionate = apsHelper.getParameterValues(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY);
			String attributeAuthorityAttributi = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY_ATTRIBUTI);

			if(apsHelper.isMultipart()){
				strutsBean.decodeRequestValidazioneDocumenti = true;
			}
			
			strutsBean.canale = apsHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CANALI_CANALE);
			strutsBean.canaleStato = apsHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO);

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

			String tipologia = ServletUtils.getObjectFromSession(request, session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
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
			
			
			
			if(ServletUtils.isEditModeInProgress(strutsBean.editMode)){
				// primo accesso alla servlet
				strutsBean.validazioneDocumenti = true;
				if (apsHelper.isModalitaAvanzata()) {
					String tmpValidazioneDocumenti = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
					if(tmpValidazioneDocumenti!=null){
						if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
							strutsBean.validazioneDocumenti = true;
						}else{
							strutsBean.validazioneDocumenti = false;
						}
					}
				}
			}else{
				if(!strutsBean.decodeRequestValidazioneDocumenti){
					String tmpValidazioneDocumenti = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
					if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
						strutsBean.validazioneDocumenti = true;
					}else{
						strutsBean.validazioneDocumenti = false;
					}
				}
			}

			strutsBean.httpshostverify = false;
			if (strutsBean.httpshostverifyS != null && strutsBean.httpshostverifyS.equals(Costanti.CHECK_BOX_ENABLED))
				strutsBean.httpshostverify = true;
			strutsBean.httpsstato = false;
			if (strutsBean.httpsstatoS != null && strutsBean.httpsstatoS.equals(Costanti.CHECK_BOX_ENABLED))
				strutsBean.httpsstato = true;

			Boolean isConnettoreCustomUltimaImmagineSalvata = null;

			Connettore conTmp = null;
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, apsHelper, 
							null, //strutsBean.parametersPOST, 
							(strutsBean.endpointtype==null), strutsBean.endpointtype); // uso endpointtype per capire se è la prima volta che entro

			// Tipi protocollo supportati
			boolean filtraSoggettiEsistenti = true;
			boolean filtraAccordiEsistenti = true;
			List<String> listaTipiProtocollo = apcCore.getProtocolliByFilter(request, session, filtraSoggettiEsistenti, null, filtraAccordiEsistenti, false, true);
					
			// Preparo il menu
			apsHelper.makeMenu();

			if(listaTipiProtocollo.size()<=0) {
				
				List<String> _listaTipiProtocolloSoloSoggetti = apcCore.getProtocolliByFilter(request, session, filtraSoggettiEsistenti, null, !filtraAccordiEsistenti, false, true);			
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

				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
						ForwardParams.ADD());
			}
			
			strutsBean.tipoProtocollo = apsHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO);
			if(strutsBean.tipoProtocollo == null){
				strutsBean.tipoProtocollo = apsCore.getProtocolloDefault(request, session, listaTipiProtocollo);
			}
			
			boolean connettoreStatic = false;
			if(gestioneFruitori) {
				connettoreStatic = apsCore.isConnettoreStatic(strutsBean.tipoProtocollo);
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

			List<IDAccordoDB> listaIdAPI = AccordiServizioParteSpecificaUtilities.getListaIdAPI(strutsBean.tipoProtocollo, userLogin, apsCore, apsHelper);

			

			int accordoPrimoAccesso = -1;

			if (listaIdAPI!=null && listaIdAPI.size() > 0) {
				int i = 0;
				if(listaIdAPI.size() > 1) {
					accordiList = new String[listaIdAPI.size()+1];
					accordiListLabel = new String[listaIdAPI.size()+1];
					accordiList[0] = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_ACCORDO_NON_SELEZIONATO;
					accordiListLabel[0] = AccordiServizioParteSpecificaCostanti.LABEL_DEFAULT_VALUE_PARAMETRO_ACCORDO_NON_SELEZIONATO;
					i = 1;
				} else {
					accordiList = new String[listaIdAPI.size()];
					accordiListLabel = new String[listaIdAPI.size()];
				}
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
							if(apcCore.getProtocolloDefault(request, session,listaTipiProtocollo).equals(soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo()))){
								accordoPrimoAccesso = i;
							}
						}
					}
					accordiListLabel[i] = apsHelper.getLabelIdAccordo(strutsBean.tipoProtocollo, as);
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
			
			// dopo il primo accesso le variabili della classe rimangono inizializzate
			strutsBean.serviceBinding = null;
			strutsBean.formatoSpecifica = null;
			
			String postBackElementName = apsHelper.getPostBackElementName();

			// Controllo se ho modificato l'accordo, se si allora suggerisco il referente dell'accordo
			AccordoServizioParteComuneSintetico as = null;
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO) ||
						postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROTOCOLLO)){
					
					if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROTOCOLLO)){
						strutsBean.accordo = null;
						strutsBean.versione = null;
						strutsBean.erogazioneAutenticazione = null;
						strutsBean.fruizioneAutenticazione = null;
						strutsBean.erogazioneAutorizzazione = null;
						strutsBean.fruizioneAutorizzazione = null;
						
						strutsBean.providerSoggettoFruitore = null;
					}
					
					strutsBean.erogazioneServizioApplicativoServerEnabled = false;
					strutsBean.url = null;
					
					if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO) && strutsBean.tipoProtocollo!=null) {
						boolean showReferente = apcCore.isSupportatoSoggettoReferente(strutsBean.tipoProtocollo);
						if(showReferente) {
							boolean annullaProvider = true;
							if (!gestioneFruitori && strutsBean.accordo != null && !"".equals(strutsBean.accordo)) {
								as = apcCore.getAccordoServizioSintetico(Long.parseLong(strutsBean.accordo));
								if(as.getSoggettoReferente()!=null) {
									Soggetto s = soggettiCore.getSoggettoRegistro(new IDSoggetto(as.getSoggettoReferente().getTipo(), as.getSoggettoReferente().getNome()));
									if(pddCore.isPddEsterna(s.getPortaDominio())) {
										annullaProvider = false;
									}
								}
							} 
							if(annullaProvider) {
								strutsBean.provider = null;
							}
						}
					}
					else {
						strutsBean.provider = null;
					}
					
					strutsBean.tiposervizio = null;
					
					// reset protocol properties
					apsHelper.deleteBinaryParameters(strutsBean.wsdlimpler,strutsBean.wsdlimplfru);
					apsHelper.deleteProtocolPropertiesBinaryParameters(strutsBean.wsdlimpler,strutsBean.wsdlimplfru);

					strutsBean.portType = null;
					strutsBean.nomeservizio = "";
				}  
				
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE)){
					strutsBean.nomeservizio = "";
					strutsBean.url = null;
					strutsBean.erogazioneServizioApplicativoServerEnabled = false;
				}
				
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_EROGATORE)){
					strutsBean.erogazioneServizioApplicativoServerEnabled = false;
				}
				
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ABILITA_USO_APPLICATIVO_SERVER)){
					strutsBean.erogazioneServizioApplicativoServer = null;
				}
			}

			// Lista port-type associati all'accordo di servizio
			boolean forceHttps = false;
			boolean forceHttpsClient = false;
			if(as==null) {
				if (strutsBean.accordo != null && !"".equals(strutsBean.accordo)) {
					as = apcCore.getAccordoServizioSintetico(Long.parseLong(strutsBean.accordo));
				} else {
					strutsBean.accordo = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_ACCORDO_NON_SELEZIONATO;
					if (accordiList != null){
						if(accordiList.length == 1) 
							as = apcCore.getAccordoServizioSintetico(Long.parseLong(accordiList[accordoPrimoAccesso]));
						
//						if(accordoPrimoAccesso >= 0 && accordoPrimoAccesso < accordiList.length)
//							as = apcCore.getAccordoServizioSintetico(Long.parseLong(accordiList[accordoPrimoAccesso]));
						if(as!=null)
							strutsBean.accordo = as.getId() + "";
					}
				}
			}
			if(as!=null){
				// salvo il soggetto referente
				soggettoReferente = new IDSoggetto(as.getSoggettoReferente().getTipo(), as.getSoggettoReferente().getNome());
				idReferente = as.getSoggettoReferente().getId();

				strutsBean.serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
				strutsBean.formatoSpecifica = apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica());
				

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
					if (strutsBean.portType != null && !"".equals(strutsBean.portType) && !"-".equals(strutsBean.portType)){
						PortTypeSintetico pt = null;
						for(int i=0; i<as.getPortType().size(); i++){
							if(strutsBean.portType.equals(as.getPortType().get(i).getNome())){
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
							strutsBean.servcorr=Costanti.CHECK_BOX_ENABLED;
						}
						else{
							strutsBean.servcorr=Costanti.CHECK_BOX_DISABLED;
						}							
					}
				}
				
				if(generaPortaDelegata && apsHelper.isProfiloModIPA(strutsBean.tipoProtocollo)) {
					forceHttps = apsHelper.forceHttpsProfiloModiPA();
					forceHttpsClient = apsHelper.forceHttpsClientProfiloModiPA(IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as), strutsBean.portType);
				}

			}

			// Fix per bug che accadeva in modalita' standard quando si seleziona un servizio di un accordo operativo, poi si cambia idea e si seleziona un accordo bozza.
			// lo stato del package rimaneva operativo.
			if(strutsBean.statoPackage!=null && apsHelper.isModalitaStandard()){
				if(apsHelper.isShowGestioneWorkflowStatoDocumenti()){
					if(StatiAccordo.operativo.toString().equals(strutsBean.statoPackage) || StatiAccordo.finale.toString().equals(strutsBean.statoPackage)){
						if(as!=null && as.getStatoPackage().equals(StatiAccordo.bozza.toString()) ){
							strutsBean.statoPackage = StatiAccordo.bozza.toString(); 
						}
					}					
				}
			}
			
			// Calcolo url presente nell'API
			String urlAPI = apcCore.readEndpoint(as, strutsBean.portType, strutsBean.servcorr, strutsBean.wsdlimpler, strutsBean.wsdlimplfru);
			//System.out.println("Endpoint ricavato dall'API ["+urlSuggerita+"]");
			
			List<String> versioniProtocollo = apsCore.getVersioniProtocollo(strutsBean.tipoProtocollo);
			List<String> tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(strutsBean.tipoProtocollo);
			List<String> tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(strutsBean.tipoProtocollo,strutsBean.serviceBinding);
			boolean erogazioneIsSupportatoAutenticazioneSoggetti = soggettiCore.isSupportatoAutenticazioneSoggetti(strutsBean.tipoProtocollo);

			// calcolo soggetti compatibili con accordi
			List<Soggetto> listSoggetti = null;
			ConsoleSearch searchSoggetti = new ConsoleSearch(true);
			searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, strutsBean.tipoProtocollo);
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
			
			if(listSoggetti==null || listSoggetti.size()<=0) {
				if(gestioneFruitori) {
					pd.setMessage("Non risultano registrati soggetti che possano erogare API", Costanti.MESSAGE_TYPE_INFO);
				}
				else {
					pd.setMessage("Non risultano registrati soggetti", Costanti.MESSAGE_TYPE_INFO);
				}
				
				pd.disableEditMode();

				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
						ForwardParams.ADD());
			}

			boolean existsAPCCompatibili = false;
			if (listSoggetti.size() > 0) {
				List<String> soggettiListTmp = new ArrayList<String>();
				List<String> soggettiListLabelTmp = new ArrayList<String>();
				for (Soggetto soggetto : listSoggetti) {
					soggettiListTmp.add(soggetto.getId().toString());
					soggettiListLabelTmp.add(apsHelper.getLabelNomeSoggetto(strutsBean.tipoProtocollo, soggetto.getTipo() , soggetto.getNome()));
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

						List<DataElement> dati = new ArrayList<>();

						dati.add(ServletUtils.getDataElementForEditModeFinished());

						pd.setDati(dati);

						ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

						return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
								ForwardParams.ADD());
					}

					// refresh di tutte le infromazioni
					versioniProtocollo = apsCore.getVersioniProtocollo(strutsBean.tipoProtocollo);
					tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(strutsBean.tipoProtocollo);
					tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(strutsBean.tipoProtocollo,strutsBean.serviceBinding);

					searchSoggetti = new ConsoleSearch(true);
					searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, strutsBean.tipoProtocollo);
					if(apsCore.isVisioneOggettiGlobale(userLogin)){
						listSoggetti = soggettiCore.soggettiRegistroList(null, searchSoggetti);
					}else{
						listSoggetti = soggettiCore.soggettiRegistroList(userLogin, searchSoggetti);
					}
					
					for (Soggetto soggetto : listSoggetti) {
						soggettiListTmp.add(soggetto.getId().toString());
						soggettiListLabelTmp.add(apsHelper.getLabelNomeSoggetto(strutsBean.tipoProtocollo, soggetto.getTipo() , soggetto.getNome()));
					}
					soggettiList = soggettiListTmp.toArray(new String[1]);
					soggettiListLabel = soggettiListLabelTmp.toArray(new String[1]);

//					if(listaIdAPI.size()>0){
//						strutsBean.accordo = listaIdAPI.get(0).getId()+"";
//					}
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
					ConsoleSearch searchSoggettiFruitori = new ConsoleSearch(true);
					searchSoggettiFruitori.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, strutsBean.tipoProtocollo);
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
						soggettiListLabelTmp.add(apsHelper.getLabelNomeSoggetto(strutsBean.tipoProtocollo, soggetto.getTipo() , soggetto.getNome()));
					}
	
					if(soggettiListTmp.size()>0){
						soggettiFruitoriList = soggettiListTmp.toArray(new String[1]);
						soggettiFruitoriListLabel = soggettiListLabelTmp.toArray(new String[1]);
					}
					else {
						pd.setMessage("Non esistono soggetti nel dominio interno", Costanti.MESSAGE_TYPE_INFO);
						pd.disableEditMode();

						List<DataElement> dati = new ArrayList<>();

						dati.add(ServletUtils.getDataElementForEditModeFinished());

						pd.setDati(dati);

						ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

						return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
								ForwardParams.ADD());
					}
				}
			}
			
			if(gestioneFruitori) {
				if ((strutsBean.providerSoggettoFruitore != null) && !strutsBean.providerSoggettoFruitore.equals("")) {
					long idFruitore = Long.parseLong(strutsBean.providerSoggettoFruitore);
					Soggetto soggetto = soggettiCore.getSoggettoRegistro(idFruitore);
					strutsBean.nomeSoggettoFruitore = soggetto.getNome();
					strutsBean.tipoSoggettoFruitore = soggetto.getTipo();
					//profiloSoggettoErogatore = soggetto.getVersioneProtocollo();
				}
				else {
					Soggetto soggetto = listFruitori.get(0);
					strutsBean.providerSoggettoFruitore = soggetto.getId()+"";
					strutsBean.nomeSoggettoFruitore = soggetto.getNome();
					strutsBean.tipoSoggettoFruitore = soggetto.getTipo();
				}
			}
			
			if(gestioneFruitori_soggettiErogatori_escludiSoggettoFruitore) {
				boolean found = false;
				for (int i = 0; i < listSoggetti.size(); i++) {
					Soggetto soggettoCheck = listSoggetti.get(i);
					if(soggettoCheck.getTipo().equals(strutsBean.tipoSoggettoFruitore) && soggettoCheck.getNome().equals(strutsBean.nomeSoggettoFruitore)) {
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
	
						List<DataElement> dati = new ArrayList<>();
	
						dati.add(ServletUtils.getDataElementForEditModeFinished());
	
						pd.setDati(dati);
	
						ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
	
						return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
								ForwardParams.ADD());
					}
					
					// aggiorno soggetti
					List<String> soggettiListTmp = new ArrayList<String>();
					List<String> soggettiListLabelTmp = new ArrayList<String>();
					for (Soggetto soggetto : listSoggetti) {
						soggettiListTmp.add(soggetto.getId().toString());
						soggettiListLabelTmp.add(apsHelper.getLabelNomeSoggetto(strutsBean.tipoProtocollo, soggetto.getTipo() , soggetto.getNome()));
					}
					soggettiList = soggettiListTmp.toArray(new String[1]);
					soggettiListLabel = soggettiListLabelTmp.toArray(new String[1]);
	
				}
			}
			
			
			

			//String profiloSoggettoErogatore = null;
			if ((strutsBean.provider != null) && !strutsBean.provider.equals("")) {
				long idErogatore = Long.parseLong(strutsBean.provider);
				Soggetto soggetto = soggettiCore.getSoggettoRegistro(idErogatore);
				strutsBean.nomeSoggettoErogatore = soggetto.getNome();
				strutsBean.tipoSoggettoErogatore = soggetto.getTipo();
				//profiloSoggettoErogatore = soggetto.getVersioneProtocollo();
			} else {
				if(soggettoReferente != null ){
					for (Soggetto soggettoCheck : listSoggetti) {
						if(soggettoCheck.getTipo().equals(soggettoReferente.getTipo()) && soggettoCheck.getNome().equals(soggettoReferente.getNome())) {
							strutsBean.provider = idReferente + "";
							strutsBean.nomeSoggettoErogatore = soggettoReferente.getNome();
							strutsBean.tipoSoggettoErogatore = soggettoReferente.getTipo();
							break;
						}
					}
				}
				// Se ancora non l'ho trovato prendo il primo della lista nel caso di gestione erogazione
				if ((strutsBean.provider == null) || strutsBean.provider.equals("")) {
					if( (gestioneErogatori || gestioneFruitori) && listSoggetti!=null && listSoggetti.size()>0) {
						Soggetto soggetto = listSoggetti.get(0);
						strutsBean.provider = soggetto.getId() + "";
						strutsBean.nomeSoggettoErogatore = soggetto.getNome();
						strutsBean.tipoSoggettoErogatore = soggetto.getTipo();
					}
				}
			}
			
			
			if(strutsBean.tiposervizio == null){
				strutsBean.tiposervizio = apsCore.getTipoServizioDefaultProtocollo(strutsBean.tipoProtocollo,strutsBean.serviceBinding);
			}


			// La lista degli SA viene filtrata per tipo se sono abilitati gli applicativiServer.
			String tipoSA = (visualizzaSezioneApplicativiServerEnabled && gestioneErogatori) ? ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER : null;
			
			// Lista dei servizi applicativi per la creazione automatica
			List<IDServizioApplicativoDB> listaIdSA = null;
			if ((strutsBean.provider != null) && !strutsBean.provider.equals("")) {
				long idErogatore = Long.valueOf(strutsBean.provider);
				
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
			
			boolean escludiSAServer = saCore.isApplicativiServerEnabled(apsHelper);
			String filtroTipoSA = escludiSAServer ? ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT : null;
						
			List<String> saFruitoriList = new ArrayList<String>();
			saFruitoriList.add("-");
			IDSoggetto idSoggettoFruitoreSelected = null;
			if(gestioneFruitori && strutsBean.nomeSoggettoFruitore!=null && strutsBean.tipoSoggettoFruitore!=null){
				try{
					
					idSoggettoFruitoreSelected = new IDSoggetto(strutsBean.tipoSoggettoFruitore, strutsBean.nomeSoggettoFruitore);
					
					String auth = strutsBean.fruizioneAutenticazione;
					if(auth==null || "".equals(auth)){
						auth = apsCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
					}
					
					org.openspcoop2.core.config.constants.CredenzialeTipo tipoAutenticazione = org.openspcoop2.core.config.constants.CredenzialeTipo.toEnumConstant(auth);
					Boolean appId = null;
					if(org.openspcoop2.core.config.constants.CredenzialeTipo.APIKEY.equals(tipoAutenticazione)) {
						ApiKeyState apiKeyState =  new ApiKeyState(null);
						appId = apiKeyState.appIdSelected;
					}
					boolean bothSslAndToken = false;
					String tokenPolicy = null;
					boolean tokenPolicyOR = false;
					
					List<IDServizioApplicativoDB> oldSilList = null;
					if(apsCore.isVisioneOggettiGlobale(userLogin)){
						oldSilList = saCore.soggettiServizioApplicativoList(idSoggettoFruitoreSelected,null,
								tipoAutenticazione, appId, filtroTipoSA, 
								bothSslAndToken, tokenPolicy, tokenPolicyOR);
					}
					else {
						oldSilList = saCore.soggettiServizioApplicativoList(idSoggettoFruitoreSelected,userLogin,
								tipoAutenticazione, appId, filtroTipoSA, 
								bothSslAndToken, tokenPolicy, tokenPolicyOR);
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
			if((strutsBean.erogazioneAutenticazione !=null && !"".equals(strutsBean.erogazioneAutenticazione)) && erogazioneIsSupportatoAutenticazioneSoggetti) {
				TipoAutenticazione tipoAutenticazione = TipoAutenticazione.toEnumConstant(strutsBean.erogazioneAutenticazione);
				credenziale = !tipoAutenticazione.equals(TipoAutenticazione.DISABILITATO) ? CredenzialeTipo.toEnumConstant(strutsBean.erogazioneAutenticazione) : null;
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
					soggettiAutenticatiLabel.add(apsHelper.getLabelNomeSoggetto(strutsBean.tipoProtocollo, soggetto.getTipo(), soggetto.getNome())); 
				}
			}
			

			// Controllo se il soggetto erogare appartiene ad una pdd di tipo operativo.
			if(strutsBean.tipoSoggettoErogatore!=null && !"".equals(strutsBean.tipoSoggettoErogatore) 
					&&  strutsBean.nomeSoggettoErogatore!=null && !"".equals(strutsBean.nomeSoggettoErogatore)){
				IDSoggetto idSoggettoEr = new IDSoggetto(strutsBean.tipoSoggettoErogatore, strutsBean.nomeSoggettoErogatore);
				if(!AccordiServizioParteSpecificaUtilities.isSoggettoOperativo(idSoggettoEr, apsCore)) {
					generaPortaApplicativa = false;
				}
			}
			
			strutsBean.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(strutsBean.tipoProtocollo);
			strutsBean.consoleDynamicConfiguration =  strutsBean.protocolFactory.createDynamicConfigurationConsole();
			strutsBean.registryReader = soggettiCore.getRegistryReader(strutsBean.protocolFactory); 
			strutsBean.configRegistryReader = soggettiCore.getConfigIntegrationReader(strutsBean.protocolFactory);
						
			// ID Accordo, i dati del servizio sono null, è presente solamente l'id dell'API
			IDServizio idAps = new IDServizio();
			if(as!=null) {
				idAps.setUriAccordoServizioParteComune(idAccordoFactory.getUriFromAccordo(as));
			}
			idAps.setPortType(strutsBean.portType);
			IDFruizione idFruizione = null;
			if(idSoggettoFruitoreSelected!=null) {
				idFruizione = new IDFruizione();
				idFruizione.setIdServizio(idAps);
				idFruizione.setIdFruitore(idSoggettoFruitoreSelected);
			}
			if(gestioneFruitori) {
				strutsBean.consoleConfiguration = strutsBean.consoleDynamicConfiguration.getDynamicConfigFruizioneAccordoServizioParteSpecifica(strutsBean.consoleOperationType, apsHelper, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idFruizione);
			}
			else {
				strutsBean.consoleConfiguration = strutsBean.consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteSpecifica(strutsBean.consoleOperationType, apsHelper, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idAps );
			}
			strutsBean.protocolProperties = apsHelper.estraiProtocolPropertiesDaRequest(strutsBean.consoleConfiguration, strutsBean.consoleOperationType);
			
			// Token Policy
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
			
			// AttributeAuthority
			List<GenericProperties> attributeAuthorityList = confCore.gestorePolicyTokenList(null, ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_ATTRIBUTE_AUTHORITY, null);
			String [] attributeAuthorityLabels = new String[attributeAuthorityList.size()];
			String [] attributeAuthorityValues = new String[attributeAuthorityList.size()];
			for (int i = 0; i < attributeAuthorityList.size(); i++) {
				GenericProperties genericProperties = attributeAuthorityList.get(i);
				attributeAuthorityLabels[i] = genericProperties.getNome();
				attributeAuthorityValues[i] = genericProperties.getNome();
			}
			
			String servletList = null;
			String labelList = null;
			
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session, request).getValue();
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
			if(ServletUtils.isEditModeInProgress(strutsBean.editMode)){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, labelList,servletList);
				

				if(apsHelper.isShowGestioneWorkflowStatoDocumenti()){
					if(strutsBean.nomeservizio==null || "".equals(strutsBean.nomeservizio)){
						strutsBean.statoPackage=StatiAccordo.bozza.toString();
					}

					// Se l'accordo Comune che si riferisce ha stato operativo o finale modifico lo stato in operativo
					// Per visualizzare immediatamente all'utente
					if(as!=null && 
							(as.getStatoPackage().equals(StatiAccordo.operativo.toString()) || as.getStatoPackage().equals(StatiAccordo.finale.toString()))
							){
						strutsBean.statoPackage = StatiAccordo.operativo.toString(); 
					}

				}else{
					strutsBean.statoPackage=StatiAccordo.finale.toString();
				}

				if (strutsBean.nomeservizio == null) {
					strutsBean.nomeservizio = "";
					//					strutsBean.provider = "";
					//					strutsBean.accordo = "";
					strutsBean.servcorr = "";
					// this.servpub = "";
//					if(strutsBean.wsdlimpler.getValue() == null)
//						strutsBean.wsdlimpler.setValue(new byte[1]);
//					if(strutsBean.wsdlimplfru.getValue() == null)
//						strutsBean.wsdlimplfru.setValue(new byte[1]); 
					strutsBean.tipoconn = "";
					strutsBean.url = "";
					strutsBean.nome = "";
					strutsBean.tipo = ConnettoriCostanti.TIPI_CODE_JMS[0];
					strutsBean.user = "";
					strutsBean.password = "";
					strutsBean.initcont = "";
					strutsBean.urlpgk = "";
					strutsBean.provurl = "";
					strutsBean.connfact = "";
					strutsBean.sendas = ConnettoriCostanti.TIPO_SEND_AS[0];
					strutsBean.profilo = "-";
					if(ServiceBinding.SOAP.equals(strutsBean.serviceBinding)) {
						if(ptList!=null && ptList.length==2){
							strutsBean.portType = ptList[1]; // al posto 0 è presente '-'
							strutsBean.nomeservizio = strutsBean.portType;
						}
						else {
							strutsBean.portType = "-";
						}
					}else {
						strutsBean.portType = "-";
					}
					strutsBean.descrizione = "";
					strutsBean.httpsurl = "";
					strutsBean.httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
					strutsBean.httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
					strutsBean.httpshostverify = true;
					strutsBean.httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
					strutsBean.httpspath = "";
					strutsBean.httpstipo = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
					strutsBean.httpspwd = "";
					strutsBean.httpsstato = false;
					strutsBean.httpskeystore = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DEFAULT;
					strutsBean.httpspwdprivatekeytrust = "";
					strutsBean.httpspathkey = "";
					strutsBean.httpstipokey =ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
					strutsBean.httpspwdkey = "";
					strutsBean.httpspwdprivatekey = "";
					strutsBean.versione="1";
					strutsBean.canaleStato = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CANALE_STATO_DEFAULT;
					strutsBean.canale = "";
				}
				
				if(strutsBean.endpointtype==null) {
					if(apsHelper.isModalitaCompleta()==false) {
						strutsBean.endpointtype = TipiConnettore.HTTP.getNome();
					}
					else {
						strutsBean.endpointtype = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DISABILITATO;
					}
				}
				
				if(strutsBean.serviceBinding != null) {
					switch (strutsBean.serviceBinding) {
						case REST:
							if(strutsBean.nomeservizio==null || "".equals(strutsBean.nomeservizio)){
								strutsBean.nomeservizio = as.getNome();
							}
							
							break;
						case SOAP:
						default:
							if(strutsBean.portType!=null && !"".equals(strutsBean.portType) && !"-".equals(strutsBean.portType)){
			
								boolean ptValid = true;
			
								if(ptList!=null && ptList.length>0){
									// controllo che l'attuale port Type sia tra quelli presenti nell'accordo.
									boolean found = false;
									for (String portType : ptList) {
										if(portType.equals(strutsBean.portType)){
											found = true;
											break;
										}
									}
									if(!found){
										ptValid = false;
									}
			
								}
			
								if(ptValid){
			
									if(strutsBean.nomeservizio==null || "".equals(strutsBean.nomeservizio)){
										strutsBean.nomeservizio = strutsBean.portType;
									}
									else if(strutsBean.nomeservizio.equals(strutsBean.oldPortType)){
										strutsBean.nomeservizio = strutsBean.portType;
									}
			
									strutsBean.oldPortType = strutsBean.portType;
			
								}
								else{
			
									strutsBean.nomeservizio = null;
									strutsBean.portType = null;
									strutsBean.oldPortType = null;
			
								}
							}  else {
								if(ptList ==null || ptList.length < 1){
									strutsBean.nomeservizio = as.getNome();
								}
								else if(ptList!=null && ptList.length==2){
									strutsBean.portType = ptList[1]; // al posto 0 è presente '-'
									strutsBean.nomeservizio = strutsBean.portType;
								}
							}
			
						
						break;
					}
				}
				
//				strutsBean.erogazioneServizioApplicativoServerEnabled = false;
				if(strutsBean.erogazioneServizioApplicativoServer==null)
					strutsBean.erogazioneServizioApplicativoServer = "";

				if(strutsBean.erogazioneRuolo==null || "".equals(strutsBean.erogazioneRuolo))
					strutsBean.erogazioneRuolo = "-";
				if(strutsBean.erogazioneAutenticazione==null || "".equals(strutsBean.erogazioneAutenticazione)) {
					strutsBean.erogazioneAutenticazione = apsCore.getAutenticazione_generazioneAutomaticaPorteApplicative();
					
					soggettiAutenticati = new ArrayList<String>();
					soggettiAutenticatiLabel = new ArrayList<String>();
					if(erogazioneIsSupportatoAutenticazioneSoggetti) {
						TipoAutenticazione tipoAutenticazione = TipoAutenticazione.toEnumConstant(strutsBean.erogazioneAutenticazione);
						credenziale = !tipoAutenticazione.equals(TipoAutenticazione.DISABILITATO) ? CredenzialeTipo.toEnumConstant(strutsBean.erogazioneAutenticazione) : null;
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
							soggettiAutenticatiLabel.add(apsHelper.getLabelNomeSoggetto(strutsBean.tipoProtocollo, soggetto.getTipo(), soggetto.getNome())); 
						}
					}
					
				}
				if(strutsBean.erogazioneAutorizzazione==null || "".equals(strutsBean.erogazioneAutorizzazione)){
					String tipoAutorizzazione = apsCore.getAutorizzazione_generazioneAutomaticaPorteApplicative();
					strutsBean.erogazioneAutorizzazione = AutorizzazioneUtilities.convertToStato(tipoAutorizzazione);
					if(TipoAutorizzazione.isAuthenticationRequired(tipoAutorizzazione))
						strutsBean.erogazioneAutorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
					if(TipoAutorizzazione.isRolesRequired(tipoAutorizzazione))
						strutsBean.erogazioneAutorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
					strutsBean.erogazioneAutorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(tipoAutorizzazione).getValue();
				}
				
				if(gestioneFruitori) {
					if(strutsBean.fruizioneServizioApplicativo==null || "".equals(strutsBean.fruizioneServizioApplicativo))
						strutsBean.fruizioneServizioApplicativo = "-";
					if(strutsBean.fruizioneRuolo==null || "".equals(strutsBean.fruizioneRuolo))
						strutsBean.fruizioneRuolo = "-";
					if(strutsBean.fruizioneAutenticazione==null || "".equals(strutsBean.fruizioneAutenticazione))
						strutsBean.fruizioneAutenticazione = apsCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
					if(strutsBean.fruizioneAutorizzazione==null || "".equals(strutsBean.fruizioneAutorizzazione)){
						String tipoAutorizzazione = apsCore.getAutorizzazione_generazioneAutomaticaPorteDelegate();
						strutsBean.fruizioneAutorizzazione = AutorizzazioneUtilities.convertToStato(tipoAutorizzazione);
						if(TipoAutorizzazione.isAuthenticationRequired(tipoAutorizzazione))
							strutsBean.fruizioneAutorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
						if(TipoAutorizzazione.isRolesRequired(tipoAutorizzazione))
							strutsBean.fruizioneAutorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
						strutsBean.fruizioneAutorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(tipoAutorizzazione).getValue();
					}
				}
				
				// default
				if(strutsBean.httpsalgoritmo==null || "".equals(strutsBean.httpsalgoritmo)){
					strutsBean.httpsalgoritmo = TrustManagerFactory.getDefaultAlgorithm();
				}
				if(strutsBean.httpsalgoritmokey==null || "".equals(strutsBean.httpsalgoritmokey)){
					strutsBean.httpsalgoritmokey = KeyManagerFactory.getDefaultAlgorithm();
				}
				if(strutsBean.httpstipologia==null || "".equals(strutsBean.httpstipologia)){
					strutsBean.httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
				}
				if(strutsBean.httpshostverifyS==null || "".equals(strutsBean.httpshostverifyS)){
					strutsBean.httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
					strutsBean.httpshostverify = true;
				}
				if(httpsTrustVerifyCertS==null || "".equals(httpsTrustVerifyCertS)){
					httpsTrustVerifyCertS = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS ? Costanti.CHECK_BOX_ENABLED_TRUE : Costanti.CHECK_BOX_DISABLED;
					strutsBean.httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
				}


				String tipoSendas = ConnettoriCostanti.TIPO_SEND_AS[0];
				String tipoJms = ConnettoriCostanti.TIPI_CODE_JMS[0];

				strutsBean.autenticazioneHttp = apsHelper.getAutenticazioneHttp(strutsBean.autenticazioneHttp, strutsBean.endpointtype, strutsBean.user);

				if(strutsBean.tempiRisposta_connectionTimeout==null || "".equals(strutsBean.tempiRisposta_connectionTimeout) 
						|| 
						strutsBean.tempiRisposta_readTimeout==null || "".equals(strutsBean.tempiRisposta_readTimeout) 
						|| 
						strutsBean.tempiRisposta_tempoMedioRisposta==null || "".equals(strutsBean.tempiRisposta_tempoMedioRisposta) ){
					
					ConfigurazioneCore configCore = new ConfigurazioneCore(soggettiCore);
					ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
					
					if(strutsBean.tempiRisposta_connectionTimeout==null || "".equals(strutsBean.tempiRisposta_connectionTimeout) ) {
						strutsBean.tempiRisposta_connectionTimeout = configGenerale.getTempiRispostaFruizione().getConnectionTimeout().intValue()+"";
					}
					if(strutsBean.tempiRisposta_readTimeout==null || "".equals(strutsBean.tempiRisposta_readTimeout) ) {
						strutsBean.tempiRisposta_readTimeout = configGenerale.getTempiRispostaFruizione().getReadTimeout().intValue()+"";
					}
					if(strutsBean.tempiRisposta_tempoMedioRisposta==null || "".equals(strutsBean.tempiRisposta_tempoMedioRisposta) ) {
						strutsBean.tempiRisposta_tempoMedioRisposta = configGenerale.getTempiRispostaFruizione().getTempoMedioRisposta().intValue()+"";
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
				
				if(identificazioneAttributiStato==null) {
					identificazioneAttributiStato = StatoFunzionalita.DISABILITATO.getValue();
				}
				
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				if(gestioneFruitori) {
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idFruizione);
				}
				else {
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idAps);
				}

				dati = apsHelper.addServiziToDati(dati, strutsBean.nomeservizio, strutsBean.tiposervizio,  null, null, 
						strutsBean.provider, null, null, 
						soggettiList, soggettiListLabel, strutsBean.accordo, strutsBean.serviceBinding, strutsBean.formatoSpecifica, accordiList, accordiListLabel, strutsBean.servcorr, 
						strutsBean.wsdlimpler, strutsBean.wsdlimplfru, tipoOp, "0", tipiServizioCompatibiliAccordo, 
						strutsBean.profilo, strutsBean.portType, ptList, strutsBean.privato,uriAccordo,strutsBean.descrizione,-1l,strutsBean.statoPackage,strutsBean.statoPackage,
						strutsBean.versione,versioniProtocollo,strutsBean.validazioneDocumenti,
						saSoggetti,strutsBean.nomeSA,generaPortaApplicativa,null,
						strutsBean.controlloAccessiStato,
						strutsBean.erogazioneRuolo,strutsBean.erogazioneAutenticazione,strutsBean.erogazioneAutenticazioneOpzionale,strutsBean.erogazioneAutenticazionePrincipal, strutsBean.erogazioneAutenticazioneParametroList,strutsBean.erogazioneAutorizzazione,erogazioneIsSupportatoAutenticazioneSoggetti,
						strutsBean.erogazioneAutorizzazioneAutenticati, strutsBean.erogazioneAutorizzazioneRuoli, strutsBean.erogazioneAutorizzazioneRuoliTipologia, strutsBean.erogazioneAutorizzazioneRuoliMatch,
						soggettiAutenticati,soggettiAutenticatiLabel, strutsBean.erogazioneSoggettoAutenticato,
						strutsBean.tipoProtocollo, listaTipiProtocollo,
						soggettiFruitoriList, soggettiFruitoriListLabel, strutsBean.providerSoggettoFruitore, strutsBean.tipoSoggettoFruitore, strutsBean.nomeSoggettoFruitore,
						strutsBean.fruizioneServizioApplicativo,strutsBean.fruizioneRuolo,strutsBean.fruizioneAutenticazione,strutsBean.fruizioneAutenticazioneOpzionale,strutsBean.fruizioneAutenticazionePrincipal, strutsBean.fruizioneAutenticazioneParametroList, strutsBean.fruizioneAutorizzazione,
						strutsBean.fruizioneAutorizzazioneAutenticati, strutsBean.fruizioneAutorizzazioneRuoli, strutsBean.fruizioneAutorizzazioneRuoliTipologia, strutsBean.fruizioneAutorizzazioneRuoliMatch,
						saFruitoriList,gestioneToken, policyLabels, policyValues, 
						gestioneTokenPolicy, gestioneTokenOpzionale,
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_token,autorizzazione_tokenOptions,
						autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,false,
						strutsBean.canaleStato, canaleAPI, strutsBean.canale, canaleList, gestioneCanaliEnabled,
						identificazioneAttributiStato, attributeAuthorityLabels, attributeAuthorityValues, attributeAuthoritySelezionate, attributeAuthorityAttributi,
						autorizzazioneAutenticatiToken, 
						autorizzazioneRuoliToken,  autorizzazioneRuoliTipologiaToken, autorizzazioneRuoliMatchToken);

				// Controllo se richiedere il connettore
				
				if(!connettoreStatic) {
					boolean forceEnableConnettore = false;
					if( gestioneFruitori || generaPortaApplicativa ) {
						forceEnableConnettore = true;
					}
					
					if(!(strutsBean.accordo == null || strutsBean.accordo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_ACCORDO_NON_SELEZIONATO))) {
					
						dati = apsHelper.addEndPointToDati(dati, strutsBean.connettoreDebug, strutsBean.endpointtype, strutsBean.autenticazioneHttp, 
								null,//(apsHelper.isModalitaCompleta() || !multitenant)?null:
								//	(generaPortaApplicativa?AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX : AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX), 
								strutsBean.url, strutsBean.nome,
								tipoJms, strutsBean.user,
								strutsBean.password, strutsBean.initcont, strutsBean.urlpgk,
								strutsBean.provurl, strutsBean.connfact, tipoSendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp, 
								strutsBean.httpsurl, strutsBean.httpstipologia,	strutsBean.httpshostverify, 
								strutsBean.httpsTrustVerifyCert, strutsBean.httpspath, strutsBean.httpstipo, strutsBean.httpspwd,
								strutsBean.httpsalgoritmo, strutsBean.httpsstato, strutsBean.httpskeystore,
								strutsBean.httpspwdprivatekeytrust, strutsBean.httpspathkey,
								strutsBean.httpstipokey, strutsBean.httpspwdkey, 
								strutsBean.httpspwdprivatekey, strutsBean.httpsalgoritmokey,
								strutsBean.httpsKeyAlias, strutsBean.httpsTrustStoreCRLs, strutsBean.httpsTrustStoreOCSPPolicy,
								strutsBean.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, null, null,
								null, null, null, null, null, null, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								strutsBean.proxy_enabled, strutsBean.proxy_hostname, strutsBean.proxy_port, strutsBean.proxy_username, strutsBean.proxy_password,
								strutsBean.tempiRisposta_enabled, strutsBean.tempiRisposta_connectionTimeout, strutsBean.tempiRisposta_readTimeout, strutsBean.tempiRisposta_tempoMedioRisposta,
								strutsBean.opzioniAvanzate, strutsBean.transfer_mode, strutsBean.transfer_mode_chunk_size, strutsBean.redirect_mode, strutsBean.redirect_max_hop,
								strutsBean.requestOutputFileName, strutsBean.requestOutputFileName_permissions, strutsBean.requestOutputFileNameHeaders, strutsBean.requestOutputFileNameHeaders_permissions,
								strutsBean.requestOutputParentDirCreateIfNotExists,strutsBean.requestOutputOverwriteIfExists,
								strutsBean.responseInputMode, strutsBean.responseInputFileName, strutsBean.responseInputFileNameHeaders, strutsBean.responseInputDeleteAfterRead, strutsBean.responseInputWaitTime,
								strutsBean.autenticazioneToken,strutsBean.token_policy,
								listExtendedConnettore, forceEnableConnettore,
								strutsBean.tipoProtocollo, forceHttps, forceHttpsClient, visualizzaSezioneApplicativiServerEnabled, strutsBean.erogazioneServizioApplicativoServerEnabled,
								strutsBean.erogazioneServizioApplicativoServer, saSoggetti);
						
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
				}
					
				if(!(strutsBean.accordo == null || strutsBean.accordo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_ACCORDO_NON_SELEZIONATO))) {
					// 	aggiunta campi custom
					dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties);
				}
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
						ForwardParams.ADD());
			}

			if (apsHelper.isModalitaStandard() && strutsBean.serviceBinding != null) {
					switch (strutsBean.serviceBinding) {
					case REST:
						// il nome del servizio e' quello dell'accordo
						strutsBean.nomeservizio = as.getNome();
						break;
					case SOAP:
					default:
						// il nome del servizio e' quello del porttype selezionato
						strutsBean.nomeservizio = strutsBean.portType;
						break;
					}
			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.serviziCheckData(tipoOp, soggettiList,
					accordiList, strutsBean.nomeservizio, strutsBean.tiposervizio, 
					(strutsBean.versione!=null && !"".equals(strutsBean.versione)) ? Integer.parseInt(strutsBean.versione) : 1,
					strutsBean.nomeservizio, strutsBean.tiposervizio, strutsBean.provider,
					strutsBean.nomeSoggettoErogatore, strutsBean.tipoSoggettoErogatore,
					strutsBean.accordo, strutsBean.serviceBinding, strutsBean.servcorr, strutsBean.endpointtype,
					strutsBean.url, strutsBean.nome, strutsBean.tipo, strutsBean.user,
					strutsBean.password, strutsBean.initcont, strutsBean.urlpgk, strutsBean.provurl,
					strutsBean.connfact, strutsBean.sendas, strutsBean.wsdlimpler,
					strutsBean.wsdlimplfru, "0", strutsBean.profilo, strutsBean.portType, ptList,
					accordoPrivato,strutsBean.privato, 
					strutsBean.httpsurl, strutsBean.httpstipologia, strutsBean.httpshostverify, 
					strutsBean.httpsTrustVerifyCert, strutsBean.httpspath, strutsBean.httpstipo,
					strutsBean.httpspwd, strutsBean.httpsalgoritmo, strutsBean.httpsstato,
					strutsBean.httpskeystore, strutsBean.httpspwdprivatekeytrust,
					strutsBean.httpspathkey, strutsBean.httpstipokey,
					strutsBean.httpspwdkey, strutsBean.httpspwdprivatekey,
					strutsBean.httpsalgoritmokey,
					strutsBean.httpsKeyAlias, strutsBean.httpsTrustStoreCRLs, strutsBean.httpsTrustStoreOCSPPolicy,
					strutsBean.tipoconn,strutsBean.versione,strutsBean.validazioneDocumenti,null,strutsBean.autenticazioneHttp,
					strutsBean.proxy_enabled, strutsBean.proxy_hostname, strutsBean.proxy_port, strutsBean.proxy_username, strutsBean.proxy_password,
					strutsBean.tempiRisposta_enabled, strutsBean.tempiRisposta_connectionTimeout, strutsBean.tempiRisposta_readTimeout, strutsBean.tempiRisposta_tempoMedioRisposta,
					strutsBean.opzioniAvanzate, strutsBean.transfer_mode, strutsBean.transfer_mode_chunk_size, strutsBean.redirect_mode, strutsBean.redirect_max_hop,
					strutsBean.requestOutputFileName, strutsBean.requestOutputFileName_permissions, strutsBean.requestOutputFileNameHeaders, strutsBean.requestOutputFileNameHeaders_permissions,
					strutsBean.requestOutputParentDirCreateIfNotExists,strutsBean.requestOutputOverwriteIfExists,
					strutsBean.responseInputMode, strutsBean.responseInputFileName, strutsBean.responseInputFileNameHeaders, strutsBean.responseInputDeleteAfterRead, strutsBean.responseInputWaitTime,
					null,strutsBean.erogazioneRuolo,strutsBean.erogazioneAutenticazione,strutsBean.erogazioneAutenticazioneOpzionale,strutsBean.erogazioneAutenticazionePrincipal,strutsBean.erogazioneAutenticazioneParametroList,strutsBean.erogazioneAutorizzazione,
					strutsBean.erogazioneAutorizzazioneAutenticati, strutsBean.erogazioneAutorizzazioneRuoli, strutsBean.erogazioneAutorizzazioneRuoliTipologia, strutsBean.erogazioneAutorizzazioneRuoliMatch,erogazioneIsSupportatoAutenticazioneSoggetti,
					generaPortaApplicativa, listExtendedConnettore,
					strutsBean.fruizioneServizioApplicativo,strutsBean.fruizioneRuolo,strutsBean.fruizioneAutenticazione,strutsBean.fruizioneAutenticazioneOpzionale,strutsBean.fruizioneAutenticazionePrincipal, strutsBean.fruizioneAutenticazioneParametroList, strutsBean.fruizioneAutorizzazione,
					strutsBean.fruizioneAutorizzazioneAutenticati, strutsBean.fruizioneAutorizzazioneRuoli, strutsBean.fruizioneAutorizzazioneRuoliTipologia, strutsBean.fruizioneAutorizzazioneRuoliMatch,
					strutsBean.tipoProtocollo, allegatoXacmlPolicy, 
					strutsBean.descrizione, strutsBean.tipoSoggettoFruitore, strutsBean.nomeSoggettoFruitore,
					strutsBean.autenticazioneToken,strutsBean.token_policy,strutsBean.erogazioneServizioApplicativoServerEnabled,
					strutsBean.erogazioneServizioApplicativoServer, strutsBean.canaleStato, strutsBean.canale, gestioneCanaliEnabled);

			if(isOk){
				if(generaPortaApplicativa && apsHelper.isModalitaCompleta() && (strutsBean.nomeSA==null || "".equals(strutsBean.nomeSA) || "-".equals(strutsBean.nomeSA))){
					if(saSoggetti==null || saSoggetti.length==0 || (saSoggetti.length==1 && "-".equals(saSoggetti[0]))){
						pd.setMessage(MessageFormat.format(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_PRIMA_DI_POTER_DEFINIRE_UN_ACCORDO_PARTE_SPECIFICA_DEVE_ESSERE_CREATO_UN_SERVIZIO_APPLICATIVO_EROGATO_DAL_SOGGETTO_X_Y,
								strutsBean.tipoSoggettoErogatore, strutsBean.nomeSoggettoErogatore));
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
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idFruizione);
				}
				else {
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idAps);
				}
			}
			
			// Validazione base dei parametri custom 
			if(isOk){
				try{
					apsHelper.validaProtocolProperties(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, strutsBean.protocolProperties);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			// Valido i parametri custom se ho gia' passato tutta la validazione prevista
			if(isOk){
				try{
					idAps = apsHelper.getIDServizioFromValues(strutsBean.tiposervizio, strutsBean.nomeservizio, strutsBean.provider, strutsBean.versione);
					idAps.setUriAccordoServizioParteComune(uriAccordo);
					//validazione campi dinamici
					if(gestioneFruitori) {
						strutsBean.consoleDynamicConfiguration.validateDynamicConfigFruizioneAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties,  
								strutsBean.registryReader, strutsBean.configRegistryReader, idFruizione);
					}
					else {
						strutsBean.consoleDynamicConfiguration.validateDynamicConfigAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
								strutsBean.registryReader, strutsBean.configRegistryReader, idAps);
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
				List<DataElement> dati = new ArrayList<>();

				// update della configurazione 
				if(gestioneFruitori) {
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idFruizione);
				}
				else {
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idAps);
				}

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = apsHelper.addServiziToDati(dati, strutsBean.nomeservizio, strutsBean.tiposervizio, null, null,  
						strutsBean.provider, null, null, 
						soggettiList, soggettiListLabel, strutsBean.accordo, strutsBean.serviceBinding, strutsBean.formatoSpecifica, accordiList, accordiListLabel,
						strutsBean.servcorr, strutsBean.wsdlimpler, strutsBean.wsdlimplfru, tipoOp, "0", tipiServizioCompatibiliAccordo, 
						strutsBean.profilo, strutsBean.portType, ptList, strutsBean.privato,uriAccordo,strutsBean.descrizione,-1l,strutsBean.statoPackage,
						strutsBean.statoPackage,strutsBean.versione,versioniProtocollo,strutsBean.validazioneDocumenti,
						saSoggetti,strutsBean.nomeSA,generaPortaApplicativa,null,
						strutsBean.controlloAccessiStato,
						strutsBean.erogazioneRuolo,strutsBean.erogazioneAutenticazione,strutsBean.erogazioneAutenticazioneOpzionale,strutsBean.erogazioneAutenticazionePrincipal, strutsBean.erogazioneAutenticazioneParametroList, strutsBean.erogazioneAutorizzazione,erogazioneIsSupportatoAutenticazioneSoggetti,
						strutsBean.erogazioneAutorizzazioneAutenticati, strutsBean.erogazioneAutorizzazioneRuoli, strutsBean.erogazioneAutorizzazioneRuoliTipologia, strutsBean.erogazioneAutorizzazioneRuoliMatch,
						soggettiAutenticati, soggettiAutenticatiLabel, strutsBean.erogazioneSoggettoAutenticato,
						strutsBean.tipoProtocollo, listaTipiProtocollo,
						soggettiFruitoriList, soggettiFruitoriListLabel, strutsBean.providerSoggettoFruitore, strutsBean.tipoSoggettoFruitore, strutsBean.nomeSoggettoFruitore,
						strutsBean.fruizioneServizioApplicativo,strutsBean.fruizioneRuolo,strutsBean.fruizioneAutenticazione,strutsBean.fruizioneAutenticazioneOpzionale,strutsBean.fruizioneAutenticazionePrincipal, strutsBean.fruizioneAutenticazioneParametroList, strutsBean.fruizioneAutorizzazione,
						strutsBean.fruizioneAutorizzazioneAutenticati, strutsBean.fruizioneAutorizzazioneRuoli, strutsBean.fruizioneAutorizzazioneRuoliTipologia, strutsBean.fruizioneAutorizzazioneRuoliMatch,
						saFruitoriList,gestioneToken, policyLabels, policyValues, 
						gestioneTokenPolicy,  gestioneTokenOpzionale,
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_token,autorizzazione_tokenOptions,
						autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,false,
						strutsBean.canaleStato, canaleAPI, strutsBean.canale, canaleList, gestioneCanaliEnabled,
						identificazioneAttributiStato, attributeAuthorityLabels, attributeAuthorityValues, attributeAuthoritySelezionate, attributeAuthorityAttributi,
						autorizzazioneAutenticatiToken, 
						autorizzazioneRuoliToken,  autorizzazioneRuoliTipologiaToken, autorizzazioneRuoliMatchToken);

				if(!connettoreStatic) {
					boolean forceEnableConnettore = false;
					if( gestioneFruitori || generaPortaApplicativa ) {
						forceEnableConnettore = true;
					}
					
					if(!(strutsBean.accordo == null || strutsBean.accordo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_ACCORDO_NON_SELEZIONATO))) {
						dati = apsHelper.addEndPointToDati(dati, strutsBean.connettoreDebug, strutsBean.endpointtype, strutsBean.autenticazioneHttp, 
								null, //(apsHelper.isModalitaCompleta() || !multitenant)?null:
								//	(generaPortaApplicativa?AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX : AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX), 
								strutsBean.url, strutsBean.nome, strutsBean.tipo, strutsBean.user,
								strutsBean.password, strutsBean.initcont, strutsBean.urlpgk,
								strutsBean.provurl, strutsBean.connfact, strutsBean.sendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp, 
								strutsBean.httpsurl, strutsBean.httpstipologia, strutsBean.httpshostverify, 
								strutsBean.httpsTrustVerifyCert, strutsBean.httpspath, strutsBean.httpstipo,
								strutsBean.httpspwd, strutsBean.httpsalgoritmo, strutsBean.httpsstato,
								strutsBean.httpskeystore, strutsBean.httpspwdprivatekeytrust,
								strutsBean.httpspathkey, strutsBean.httpstipokey,
								strutsBean.httpspwdkey, strutsBean.httpspwdprivatekey,
								strutsBean.httpsalgoritmokey, 
								strutsBean.httpsKeyAlias, strutsBean.httpsTrustStoreCRLs, strutsBean.httpsTrustStoreOCSPPolicy,
								strutsBean.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, null, null,
								null, null, null, null, null, null, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								strutsBean.proxy_enabled, strutsBean.proxy_hostname, strutsBean.proxy_port, strutsBean.proxy_username, strutsBean.proxy_password,
								strutsBean.tempiRisposta_enabled, strutsBean.tempiRisposta_connectionTimeout, strutsBean.tempiRisposta_readTimeout, strutsBean.tempiRisposta_tempoMedioRisposta,
								strutsBean.opzioniAvanzate, strutsBean.transfer_mode, strutsBean.transfer_mode_chunk_size, strutsBean.redirect_mode, strutsBean.redirect_max_hop,
								strutsBean.requestOutputFileName, strutsBean.requestOutputFileName_permissions, strutsBean.requestOutputFileNameHeaders, strutsBean.requestOutputFileNameHeaders_permissions,
								strutsBean.requestOutputParentDirCreateIfNotExists,strutsBean.requestOutputOverwriteIfExists,
								strutsBean.responseInputMode, strutsBean.responseInputFileName, strutsBean.responseInputFileNameHeaders, strutsBean.responseInputDeleteAfterRead, strutsBean.responseInputWaitTime,
								strutsBean.autenticazioneToken,strutsBean.token_policy,
								listExtendedConnettore, forceEnableConnettore,
								strutsBean.tipoProtocollo, forceHttps, forceHttpsClient, visualizzaSezioneApplicativiServerEnabled, strutsBean.erogazioneServizioApplicativoServerEnabled,
								strutsBean.erogazioneServizioApplicativoServer, saSoggetti);
						
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
					
				}
				if(!(strutsBean.accordo == null || strutsBean.accordo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_ACCORDO_NON_SELEZIONATO))) {
					// aggiunta campi custom
					dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties);
				}
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
						ForwardParams.ADD());
			}

			// Inserisco il servizio nel db
			long idProv = Long.parseLong(strutsBean.provider);
			long idAcc = Long.parseLong(strutsBean.accordo);

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
				asps.setNome(strutsBean.nomeservizio);
				asps.setTipo(strutsBean.tiposervizio);
				asps.setDescrizione(strutsBean.descrizione);
				
				// Che i seguenti valori siano identici vengono controllati nel check
				asps.setIdAccordo(idAcc);
				asps.setAccordoServizioParteComune(idAccordoFactory.getUriFromAccordo(as));
				
				asps.setIdSoggetto(idProv);
				asps.setNomeSoggettoErogatore(strutsBean.nomeSoggettoErogatore);
				asps.setTipoSoggettoErogatore(strutsBean.tipoSoggettoErogatore);
				
				// Tipologia di Servizio
				asps.setTipologiaServizio(((strutsBean.servcorr != null) && strutsBean.servcorr.equals(Costanti.CHECK_BOX_ENABLED)) ? TipologiaServizio.CORRELATO : TipologiaServizio.NORMALE);
				
				// Utente
				asps.setSuperUser(ServletUtils.getUserLoginFromSession(session));
				
				// Versione Protocollo
				if ("-".equals(strutsBean.profilo) == false)
					asps.setVersioneProtocollo(strutsBean.profilo);
				else
					asps.setVersioneProtocollo(null);

				// Privato
				asps.setPrivato(strutsBean.privato);

				// Wsdl
				FormatoSpecifica formato = null;
				if(as!=null) {
					formato = as.getFormatoSpecifica();
				}
				String wsdlimplerS = strutsBean.wsdlimpler.getValue() != null ? new String(strutsBean.wsdlimpler.getValue()) : null; 
				asps.setByteWsdlImplementativoErogatore(apsCore.getInterfaceAsByteArray(formato, wsdlimplerS));
				String wsdlimplfruS = strutsBean.wsdlimplfru.getValue() != null ? new String(strutsBean.wsdlimplfru.getValue()) : null; 
				asps.setByteWsdlImplementativoFruitore(apsCore.getInterfaceAsByteArray(formato, wsdlimplfruS));
				
				// PortType
				if (strutsBean.portType != null && !"".equals(strutsBean.portType) && !"-".equals(strutsBean.portType))
					asps.setPortType(strutsBean.portType);
				
				// Versione
				if(apsCore.isSupportatoVersionamentoAccordiServizioParteSpecifica(strutsBean.tipoProtocollo)){
					if(strutsBean.versione!=null && !"".equals(strutsBean.versione))
						asps.setVersione(Integer.parseInt(strutsBean.versione));
					else
						asps.setVersione(1);
				}else{
					asps.setVersione(1);
				}

				// stato
				asps.setStatoPackage(strutsBean.statoPackage);
			}

			

			// Connettore
			Connettore connettore = null;
			if(!connettoreStatic) {
				connettore = new Connettore();
				// strutsBean.nomeservizio);
				if (strutsBean.endpointtype.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM))
					connettore.setTipo(strutsBean.tipoconn);
				else
					connettore.setTipo(strutsBean.endpointtype);
	
				apsHelper.fillConnettore(connettore, strutsBean.connettoreDebug, strutsBean.endpointtype, strutsBean.endpointtype, strutsBean.tipoconn, strutsBean.url,
						strutsBean.nome, strutsBean.tipo, strutsBean.user, strutsBean.password,
						strutsBean.initcont, strutsBean.urlpgk, strutsBean.url, strutsBean.connfact,
						strutsBean.sendas, strutsBean.httpsurl, strutsBean.httpstipologia, strutsBean.httpshostverify, 
						strutsBean.httpsTrustVerifyCert, strutsBean.httpspath, strutsBean.httpstipo,
						strutsBean.httpspwd, strutsBean.httpsalgoritmo, strutsBean.httpsstato,
						strutsBean.httpskeystore, strutsBean.httpspwdprivatekeytrust,
						strutsBean.httpspathkey, strutsBean.httpstipokey,
						strutsBean.httpspwdkey, strutsBean.httpspwdprivatekey,
						strutsBean.httpsalgoritmokey,
						strutsBean.httpsKeyAlias, strutsBean.httpsTrustStoreCRLs, strutsBean.httpsTrustStoreOCSPPolicy,
						strutsBean.proxy_enabled, strutsBean.proxy_hostname, strutsBean.proxy_port, strutsBean.proxy_username, strutsBean.proxy_password,
						strutsBean.tempiRisposta_enabled, strutsBean.tempiRisposta_connectionTimeout, strutsBean.tempiRisposta_readTimeout, strutsBean.tempiRisposta_tempoMedioRisposta,
						strutsBean.opzioniAvanzate, strutsBean.transfer_mode, strutsBean.transfer_mode_chunk_size, strutsBean.redirect_mode, strutsBean.redirect_max_hop,
						strutsBean.requestOutputFileName, strutsBean.requestOutputFileName_permissions, strutsBean.requestOutputFileNameHeaders, strutsBean.requestOutputFileNameHeaders_permissions,
						strutsBean.requestOutputParentDirCreateIfNotExists,strutsBean.requestOutputOverwriteIfExists,
						strutsBean.responseInputMode, strutsBean.responseInputFileName, strutsBean.responseInputFileNameHeaders, strutsBean.responseInputDeleteAfterRead, strutsBean.responseInputWaitTime,
						strutsBean.token_policy,
						listExtendedConnettore);
			}

			if(asps.getConfigurazioneServizio()==null)
				asps.setConfigurazioneServizio(new ConfigurazioneServizio());
			if(apsHelper.isModalitaCompleta() || (!generaPortaApplicativa && !gestioneFruitori)) {
				asps.getConfigurazioneServizio().setConnettore(connettore);
			}

			if(gestioneFruitori) {
				Fruitore fruitore = new Fruitore();
				fruitore.setTipo(strutsBean.tipoSoggettoFruitore);
				fruitore.setNome(strutsBean.nomeSoggettoFruitore);
				fruitore.setStatoPackage(strutsBean.statoPackage);
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
					boolean gestioneWsdlImplementativo = apcCore.showPortiAccesso(strutsBean.tipoProtocollo, strutsBean.serviceBinding, strutsBean.formatoSpecifica);
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
					List<DataElement> dati = new ArrayList<>();

					// update della configurazione 
					if(gestioneFruitori) {
						strutsBean.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
								strutsBean.registryReader, strutsBean.configRegistryReader, idFruizione);
					}
					else {
						strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
								strutsBean.registryReader, strutsBean.configRegistryReader, idAps);
					}

					dati.add(ServletUtils.getDataElementForEditModeFinished());
					
					dati = apsHelper.addServiziToDati(dati, strutsBean.nomeservizio, strutsBean.tiposervizio, null, null,  
							strutsBean.provider, null, null, 
							soggettiList, soggettiListLabel, strutsBean.accordo, strutsBean.serviceBinding, strutsBean.formatoSpecifica, accordiList, accordiListLabel, strutsBean.servcorr, 
							strutsBean.wsdlimpler, strutsBean.wsdlimplfru, tipoOp, "0", tipiServizioCompatibiliAccordo, 
							strutsBean.profilo, strutsBean.portType, ptList, strutsBean.privato,uriAccordo,strutsBean.descrizione,-1l,strutsBean.statoPackage,
							strutsBean.statoPackage,strutsBean.versione,versioniProtocollo,strutsBean.validazioneDocumenti,
							saSoggetti,strutsBean.nomeSA,generaPortaApplicativa,null,
							strutsBean.controlloAccessiStato,
							strutsBean.erogazioneRuolo,strutsBean.erogazioneAutenticazione,strutsBean.erogazioneAutenticazioneOpzionale,strutsBean.erogazioneAutenticazionePrincipal, strutsBean.erogazioneAutenticazioneParametroList, strutsBean.erogazioneAutorizzazione,erogazioneIsSupportatoAutenticazioneSoggetti,
							strutsBean.erogazioneAutorizzazioneAutenticati, strutsBean.erogazioneAutorizzazioneRuoli, strutsBean.erogazioneAutorizzazioneRuoliTipologia, strutsBean.erogazioneAutorizzazioneRuoliMatch,
							soggettiAutenticati, soggettiAutenticatiLabel, strutsBean.erogazioneSoggettoAutenticato,
							strutsBean.tipoProtocollo, listaTipiProtocollo,
							soggettiFruitoriList, soggettiFruitoriListLabel, strutsBean.providerSoggettoFruitore, strutsBean.tipoSoggettoFruitore, strutsBean.nomeSoggettoFruitore,
							strutsBean.fruizioneServizioApplicativo,strutsBean.fruizioneRuolo,strutsBean.fruizioneAutenticazione,strutsBean.fruizioneAutenticazioneOpzionale,strutsBean.fruizioneAutenticazionePrincipal, strutsBean.fruizioneAutenticazioneParametroList, strutsBean.fruizioneAutorizzazione,
							strutsBean.fruizioneAutorizzazioneAutenticati, strutsBean.fruizioneAutorizzazioneRuoli, strutsBean.fruizioneAutorizzazioneRuoliTipologia, strutsBean.fruizioneAutorizzazioneRuoliMatch,
							saFruitoriList,gestioneToken, policyLabels, policyValues, 
							gestioneTokenPolicy,  gestioneTokenOpzionale,
							gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
							autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
							autorizzazione_token,autorizzazione_tokenOptions,
							autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,false,
							strutsBean.canaleStato, canaleAPI, strutsBean.canale, canaleList, gestioneCanaliEnabled,
							identificazioneAttributiStato, attributeAuthorityLabels, attributeAuthorityValues, attributeAuthoritySelezionate, attributeAuthorityAttributi,
							autorizzazioneAutenticatiToken, 
							autorizzazioneRuoliToken,  autorizzazioneRuoliTipologiaToken, autorizzazioneRuoliMatchToken);

					if(!connettoreStatic) {
					
						boolean forceEnableConnettore = false;
						if( gestioneFruitori || generaPortaApplicativa ) {
							forceEnableConnettore = true;
						}
						
						dati = apsHelper.addEndPointToDati(dati, strutsBean.connettoreDebug, strutsBean.endpointtype, strutsBean.autenticazioneHttp, 
								null, //(apsHelper.isModalitaCompleta() || !multitenant)?null:
								//	(generaPortaApplicativa?AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX : AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX), 
								strutsBean.url, strutsBean.nome, strutsBean.tipo, strutsBean.user,
								strutsBean.password, strutsBean.initcont, strutsBean.urlpgk,
								strutsBean.provurl, strutsBean.connfact, strutsBean.sendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp, 
								strutsBean.httpsurl, strutsBean.httpstipologia,	strutsBean.httpshostverify, 
								strutsBean.httpsTrustVerifyCert, strutsBean.httpspath, strutsBean.httpstipo,
								strutsBean.httpspwd, strutsBean.httpsalgoritmo, strutsBean.httpsstato,
								strutsBean.httpskeystore, strutsBean.httpspwdprivatekeytrust,
								strutsBean.httpspathkey, strutsBean.httpstipokey,
								strutsBean.httpspwdkey, strutsBean.httpspwdprivatekey,
								strutsBean.httpsalgoritmokey, 
								strutsBean.httpsKeyAlias, strutsBean.httpsTrustStoreCRLs, strutsBean.httpsTrustStoreOCSPPolicy,
								strutsBean.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, null, null,
								null, null, null, null, null, null, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								strutsBean.proxy_enabled, strutsBean.proxy_hostname, strutsBean.proxy_port, strutsBean.proxy_username, strutsBean.proxy_password,
								strutsBean.tempiRisposta_enabled, strutsBean.tempiRisposta_connectionTimeout, strutsBean.tempiRisposta_readTimeout, strutsBean.tempiRisposta_tempoMedioRisposta,
								strutsBean.opzioniAvanzate, strutsBean.transfer_mode, strutsBean.transfer_mode_chunk_size, strutsBean.redirect_mode, strutsBean.redirect_max_hop,
								strutsBean.requestOutputFileName, strutsBean.requestOutputFileName_permissions, strutsBean.requestOutputFileNameHeaders, strutsBean.requestOutputFileNameHeaders_permissions,
								strutsBean.requestOutputParentDirCreateIfNotExists,strutsBean.requestOutputOverwriteIfExists,
								strutsBean.responseInputMode, strutsBean.responseInputFileName, strutsBean.responseInputFileNameHeaders, strutsBean.responseInputDeleteAfterRead, strutsBean.responseInputWaitTime,
								strutsBean.autenticazioneToken,strutsBean.token_policy,
								listExtendedConnettore, forceEnableConnettore,
								strutsBean.tipoProtocollo, forceHttps, forceHttpsClient, visualizzaSezioneApplicativiServerEnabled, strutsBean.erogazioneServizioApplicativoServerEnabled,
								strutsBean.erogazioneServizioApplicativoServer, saSoggetti);
						
					}

					// aggiunta campi custom
					dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties);

					pd.setDati(dati);


					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
							ForwardParams.ADD());

				}
			}

			IDSoggetto soggettoErogatore = new IDSoggetto(strutsBean.tipoSoggettoErogatore,strutsBean.nomeSoggettoErogatore);
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(strutsBean.tiposervizio, strutsBean.nomeservizio, soggettoErogatore, 
					(strutsBean.versione==null || "".equals(strutsBean.versione))? 1 : Integer.parseInt(strutsBean.versione));

			IDSoggetto idFruitore = null;
			if(generaPortaDelegata){
				idFruitore = new IDSoggetto(strutsBean.tipoSoggettoFruitore, strutsBean.nomeSoggettoFruitore);
			}
			
			String autenticazione = null, autenticazioneOpzionale = null;
			TipoAutenticazionePrincipal autenticazionePrincipal = null;
			List<String> autenticazioneParametroList = null;
			if(CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO_PUBBLICO.equals(strutsBean.controlloAccessiStato)) {
				autenticazione = TipoAutenticazione.DISABILITATO.getValue();
			}
			else {
				if(generaPortaApplicativa){
					
					boolean forceDisableOptional = false;
					if(apsHelper.isProfiloModIPA(strutsBean.tipoProtocollo)) {
						forceDisableOptional = apsHelper.forceHttpsClientProfiloModiPA(IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as), strutsBean.portType);
					}
					
					autenticazione = strutsBean.erogazioneAutenticazione;
					if(forceDisableOptional) {
						autenticazioneOpzionale = Costanti.CHECK_BOX_DISABLED;
					}
					else{
						if(apsHelper.isProfiloModIPA(strutsBean.tipoProtocollo)) {
							autenticazioneOpzionale = Costanti.CHECK_BOX_ENABLED;
						}
						else {
							autenticazioneOpzionale = strutsBean.erogazioneAutenticazioneOpzionale;
						}
					}
					autenticazionePrincipal = strutsBean.erogazioneAutenticazionePrincipal;
					autenticazioneParametroList = strutsBean.erogazioneAutenticazioneParametroList;
				}
				if(generaPortaDelegata){
					autenticazione = strutsBean.fruizioneAutenticazione;
					autenticazioneOpzionale = strutsBean.fruizioneAutenticazioneOpzionale;
					autenticazionePrincipal = strutsBean.fruizioneAutenticazionePrincipal;
					autenticazioneParametroList = strutsBean.fruizioneAutenticazioneParametroList;
				}
			}
			
			String autorizzazione = null, autorizzazioneAutenticati = null, autorizzazioneRuoli = null, autorizzazioneRuoliTipologia = null, autorizzazioneRuoliMatch = null;
			if(CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO_PUBBLICO.equals(strutsBean.controlloAccessiStato)) {
				autorizzazione = TipoAutorizzazione.DISABILITATO.getValue();
			}
			else {
				if(generaPortaApplicativa){
					if(apsHelper.isProfiloModIPA(strutsBean.tipoProtocollo)) {
						boolean forceDisableOptional = apsHelper.forceHttpsClientProfiloModiPA(IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as), strutsBean.portType);
						if(!forceDisableOptional) {
							autorizzazione = AutorizzazioneUtilities.STATO_DISABILITATO;
						}
						else {
							autorizzazione = strutsBean.erogazioneAutorizzazione;
						}
					}
					else {
						autorizzazione = strutsBean.erogazioneAutorizzazione;
					}
					autorizzazioneAutenticati = strutsBean.erogazioneAutorizzazioneAutenticati;
					autorizzazioneRuoli = strutsBean.erogazioneAutorizzazioneRuoli;
					autorizzazioneRuoliTipologia = strutsBean.erogazioneAutorizzazioneRuoliTipologia;
					autorizzazioneRuoliMatch = strutsBean.erogazioneAutorizzazioneRuoliMatch;
				}
				if(generaPortaDelegata){
					autorizzazione = strutsBean.fruizioneAutorizzazione;
					autorizzazioneAutenticati = strutsBean.fruizioneAutorizzazioneAutenticati;
					autorizzazioneRuoli = strutsBean.fruizioneAutorizzazioneRuoli;
					autorizzazioneRuoliTipologia = strutsBean.fruizioneAutorizzazioneRuoliTipologia;
					autorizzazioneRuoliMatch = strutsBean.fruizioneAutorizzazioneRuoliMatch;
				}
			}
		
			String servizioApplicativo = null, ruolo = null;
			String soggettoAutenticato = null;
			if(generaPortaApplicativa){
				servizioApplicativo = strutsBean.nome;
				ruolo = strutsBean.erogazioneRuolo;
				soggettoAutenticato = strutsBean.erogazioneSoggettoAutenticato;
			}
			if(generaPortaDelegata){
				servizioApplicativo = strutsBean.fruizioneServizioApplicativo;
				ruolo = strutsBean.fruizioneRuolo;
			}
			
			AccordiServizioParteSpecificaUtilities.create(asps, alreadyExists, 
					idServizio, idFruitore, strutsBean.tipoProtocollo, strutsBean.serviceBinding, 
					idProv, 
					connettore, 
					generaPortaApplicativa, generaPortaDelegata, 
					autenticazione, autenticazioneOpzionale, autenticazionePrincipal, autenticazioneParametroList,
					autorizzazione, autorizzazioneAutenticati, autorizzazioneRuoli, autorizzazioneRuoliTipologia, autorizzazioneRuoliMatch, 
					servizioApplicativo, ruolo, soggettoAutenticato, 
					autorizzazioneAutenticatiToken, 
					autorizzazioneRuoliToken,  autorizzazioneRuoliTipologiaToken, autorizzazioneRuoliMatchToken,
					autorizzazione_tokenOptions, 
					autorizzazioneScope, scope, autorizzazioneScopeMatch, allegatoXacmlPolicy,
					gestioneToken, 
					gestioneTokenPolicy, gestioneTokenOpzionale, 
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward, 
					autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail, 
					strutsBean.protocolProperties, strutsBean.consoleOperationType, 
					apsCore, apsHelper, strutsBean.erogazioneServizioApplicativoServer, strutsBean.canaleStato, strutsBean.canale, gestioneCanaliEnabled,
					identificazioneAttributiStato, attributeAuthoritySelezionate, attributeAuthorityAttributi);

			// cancello i file temporanei
			apsHelper.deleteBinaryParameters(strutsBean.wsdlimpler,strutsBean.wsdlimplfru);
			apsHelper.deleteBinaryProtocolPropertiesTmpFiles(strutsBean.protocolProperties);
			
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			
			if(apsCore.isSetSearchAfterAdd()) {
				apsCore.setSearchAfterAdd(Liste.SERVIZI, asps.getNome(), request, session, ricerca);
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
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.ADD());
			}

			apsHelper.prepareServiziList(ricerca, listaAccordi);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, ForwardParams.ADD());



		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
					ForwardParams.ADD());
		}  
	}
	
}
