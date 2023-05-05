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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.db.IDSoggettoDB;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.ApiKeyState;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeHelper;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiHelper;
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
 * AccordiServizioParteSpecificaPorteApplicativeAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaPorteApplicativeAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);
		
		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session, request);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
			
			String idAsps = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			String idSoggettoErogatoreDelServizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			if ((idSoggettoErogatoreDelServizio == null) || idSoggettoErogatoreDelServizio.equals("")) {
				PageData oldPD = ServletUtils.getPageDataFromSession(request, session);

				idSoggettoErogatoreDelServizio = oldPD.getHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			}
			String[] azioni = apsHelper.getParameterValues(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONI);
			
			String nome = apsHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME);
			String nomeGruppo = apsHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_GRUPPO);
			
			String modeCreazione = apsHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CREAZIONE);
			String modeCreazioneConnettore = apsHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CREAZIONE_CONNETTORE);
			String identificazione = apsHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_IDENTIFICAZIONE);
			String mappingPA = apsHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MAPPING);

			String controlloAccessiStato = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO);
			
			String erogazioneRuolo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_RUOLO);
			String erogazioneAutenticazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE);
			String erogazioneAutenticazioneOpzionale = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE_OPZIONALE);
			String erogazioneAutenticazionePrincipalTipo = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TIPO);
			TipoAutenticazionePrincipal erogazioneAutenticazionePrincipal = TipoAutenticazionePrincipal.toEnumConstant(erogazioneAutenticazionePrincipalTipo, false);
			List<String> erogazioneAutenticazioneParametroList = apsHelper.convertFromDataElementValue_parametroAutenticazioneList(erogazioneAutenticazione, erogazioneAutenticazionePrincipal);
			String erogazioneAutorizzazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE);
			String erogazioneAutorizzazioneAutenticati = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_AUTENTICAZIONE);
			String erogazioneAutorizzazioneRuoli = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLI);
			String erogazioneAutorizzazioneRuoliTipologia = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_TIPOLOGIA);
			String erogazioneAutorizzazioneRuoliMatch = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_MATCH);
			String erogazioneSoggettoAutenticato = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_SOGGETTO_AUTENTICATO);

			String erogazioneServizioApplicativoServerEnabledS = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ABILITA_USO_APPLICATIVO_SERVER);
			boolean erogazioneServizioApplicativoServerEnabled = ServletUtils.isCheckBoxEnabled(erogazioneServizioApplicativoServerEnabledS);
			String erogazioneServizioApplicativoServer = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_APPLICATIVO_SERVER);
			  
			String nomeSA = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SA);
			
			String gestioneToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN);
			String gestioneTokenPolicy = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY);
			String gestioneTokenOpzionale = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_OPZIONALE);
			String gestioneTokenValidazioneInput = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT);
			String gestioneTokenIntrospection = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION);
			String gestioneTokenUserInfo = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO);
			String gestioneTokenForward = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
			
			String autenticazioneTokenIssuer = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_ISSUER);
			String autenticazioneTokenClientId = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_CLIENT_ID);
			String autenticazioneTokenSubject = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_SUBJECT);
			String autenticazioneTokenUsername = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_USERNAME);
			String autenticazioneTokenEMail = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_MAIL);
			
			String autorizzazioneAutenticatiToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_TOKEN);
			String autorizzazioneRuoliToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI_TOKEN);
			String autorizzazioneRuoliTipologiaToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA_TOKEN);
			String autorizzazioneRuoliMatchToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_MATCH_TOKEN);
			
			String autorizzazioneToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
			String autorizzazioneTokenOptions = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
			String autorizzazioneScope = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
			String autorizzazioneScopeMatch = apsHelper.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
			String scope = apsHelper.getParameter(CostantiControlStation.PARAMETRO_SCOPE);
			
			BinaryParameter allegatoXacmlPolicy = apsHelper.getBinaryParameter(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
			
			String identificazioneAttributiStato = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_STATO);
			String [] attributeAuthoritySelezionate = apsHelper.getParameterValues(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY);
			String attributeAuthorityAttributi = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY_ATTRIBUTI);
			
			Properties parametersPOST = null;
			
			String endpointtype = apsHelper.readEndPointType();
			String tipoconn = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO );
			String autenticazioneHttp = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);

			String connettoreDebug = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);

			// token policy
			String autenticazioneTokenS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			boolean autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			String tokenPolicy = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
			boolean forcePDND = false;
			boolean forceOAuth = false;
			
			// proxy
			String proxyEnabled = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxyHostname = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxyPort = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxyUsername = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxyPassword = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);

			// tempi risposta
			String tempiRispostaEnabled = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			String tempiRispostaConnectionTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			String tempiRispostaReadTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			String tempiRispostaTempoMedioRisposta = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
			
			// opzioni avanzate
			String transferMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transferModeChunkSize = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirectMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirectMaxHop = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(apsHelper, transferMode, redirectMode);

			String user= null;
			String password =null;
			
			// http
			String url = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL  );
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}

			// jms
			String nomeCodaJms = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipoJms = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String initcont = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String tipoSendas = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(endpointtype)){
				user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}

			// https
			String httpsurl = url;
			String httpstipologia = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE );
			String httpshostverifyS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			String httpsTrustVerifyCertS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS );
			boolean httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
			String httpspath = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION );
			String httpstipo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			String httpskeystore = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			String httpsKeyAlias = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
			String httpsTrustStoreCRLs = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
			String httpsTrustStoreOCSPPolicy = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// file
			String requestOutputFileName = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			String requestOutputFileNamePermissions = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS);
			String requestOutputFileNameHeaders = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			String requestOutputFileNameHeadersPermissions = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_PERMISSIONS);
			String requestOutputParentDirCreateIfNotExists = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			String requestOutputOverwriteIfExists = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			String responseInputMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			String responseInputFileName = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			String responseInputFileNameHeaders = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			String responseInputDeleteAfterRead = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			String responseInputWaitTime = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
			


			boolean httpshostverify = false;
			if (httpshostverifyS != null && httpshostverifyS.equals(Costanti.CHECK_BOX_ENABLED))
				httpshostverify = true;
			boolean httpsstato = false;
			if (httpsstatoS != null && httpsstatoS.equals(Costanti.CHECK_BOX_ENABLED))
				httpsstato = true;

			Boolean isConnettoreCustomUltimaImmagineSalvata = null;
			
			boolean forceEnableConnettore = false;
			if( (!apsHelper.isModalitaCompleta())) {
				forceEnableConnettore = true;
			}

			String tipologia = ServletUtils.getObjectFromSession(request, session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneErogatori = false;
			if(tipologia!=null &&
				AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
				gestioneErogatori = true;
			}
			
			PddTipologia pddTipologiaSoggettoAutenticati = null;
			if(gestioneErogatori) {
				pddTipologiaSoggettoAutenticati = PddTipologia.ESTERNO;
			}
			
			Connettore conTmp = null;
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_PORTA_APPLICATIVA_ADD, apsHelper, 
							parametersPOST, (endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro


			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);

			// Preparo il menu
			apsHelper.makeMenu();

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(porteApplicativeCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(porteApplicativeCore);
			int idServizio = Integer.parseInt(idAsps);
			AccordoServizioParteSpecifica asps  = apsCore.getAccordoServizioParteSpecifica(idServizio);
			IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
			int soggInt = idSoggettoErogatoreDelServizio!=null ? Integer.parseInt(idSoggettoErogatoreDelServizio) : -1;
			
			boolean isApplicativiServerEnabled = apsCore.isApplicativiServerEnabled(apsHelper);
			// La lista degli SA viene filtrata per tipo se sono abilitati gli applicativiServer.
			String tipoSA = (isApplicativiServerEnabled && gestioneErogatori) ? ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER : null;
			
			AccordiServizioParteSpecificaPorteApplicativeMappingInfo mappingInfo = AccordiServizioParteSpecificaUtilities.getMappingInfo(mappingPA, asps, apsCore);
			MappingErogazionePortaApplicativa mappingSelezionato = mappingInfo.getMappingSelezionato();
			MappingErogazionePortaApplicativa mappingDefault = mappingInfo.getMappingDefault();
			String mappingLabel = mappingInfo.getMappingLabel();
			String[] listaMappingLabels = mappingInfo.getListaMappingLabels();
			String[] listaMappingValues = mappingInfo.getListaMappingValues();
			List<String> azioniOccupate = mappingInfo.getAzioniOccupate();
			String nomeNuovaConfigurazione = mappingInfo.getNomeNuovaConfigurazione();
			boolean paMappingSelezionatoMulti = mappingInfo.isPaMappingSelezionatoMulti();

			// Prendo nome, tipo e pdd del soggetto
			String tipoSoggettoProprietario = null;
			if(porteApplicativeCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				tipoSoggettoProprietario = soggetto.getTipo();
			}else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				tipoSoggettoProprietario = soggetto.getTipo();
			}

			AccordoServizioParteComuneSintetico as = null;
			ServiceBinding serviceBinding = null;
			IDAccordo idAccordo = null;
			if (asps != null) {
				idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
				as = apcCore.getAccordoServizioSintetico(idAccordo);
				serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
			}
			
			// Prendo le azioni  disponibili
			boolean addTrattinoSelezioneNonEffettuata = false;
			int sogliaAzioni = addTrattinoSelezioneNonEffettuata ? 1 : 0;
			Map<String,String> azioniS = porteApplicativeCore.getAzioniConLabel(asps, as, addTrattinoSelezioneNonEffettuata, true, azioniOccupate);
			String[] azioniDisponibiliList = null;
			String[] azioniDisponibiliLabelList = null;
			if(azioniS!=null && azioniS.size()>0) {
				azioniDisponibiliList = new String[azioniS.size()];
				azioniDisponibiliLabelList = new String[azioniS.size()];
				int i = 0;
				for (String string : azioniS.keySet()) {
					azioniDisponibiliList[i] = string;
					azioniDisponibiliLabelList[i] = azioniS.get(string);
					i++;
				}
			}
			
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggettoProprietario);
			List<String> tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
			boolean erogazioneIsSupportatoAutenticazioneSoggetti = soggettiCore.isSupportatoAutenticazioneSoggetti(protocollo);

			String postBackElementName = apsHelper.getPostBackElementName();

			boolean initConnettore = false;
			// Controllo se ho modificato l'azione allora ricalcolo il nome
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CREAZIONE_CONNETTORE)){
					// devo resettare il connettore
					if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
						initConnettore = true;
					}
				}
				
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ABILITA_USO_APPLICATIVO_SERVER)){
					erogazioneServizioApplicativoServer = null;
					// devo resettare il connettore se passo da SA Server a Default
					if(!erogazioneServizioApplicativoServerEnabled) {
						initConnettore = true;
					}
				}
			}

			// Lista dei servizi applicativi per la creazione automatica
			List<IDServizioApplicativoDB> listaIdSA = null;
			if ((idSoggettoErogatoreDelServizio != null) && !idSoggettoErogatoreDelServizio.equals("")) {
				long idErogatore = Long.parseLong(idSoggettoErogatoreDelServizio);
				
				// I servizi applicativi da visualizzare sono quelli che hanno
				// -Integration Manager (getMessage abilitato)
				// -connettore != disabilitato
				listaIdSA = saCore.getIdServiziApplicativiWithIdErogatore(idErogatore, tipoSA, true, true);

			}
			String [] saSoggetti = ServiziApplicativiHelper.toArray(listaIdSA);
		
			
			List<String> soggettiAutenticati = new ArrayList<>();
			List<String> soggettiAutenticatiLabel = new ArrayList<>();
			// lista soggetti autenticati per la creazione automatica
			CredenzialeTipo credenziale =  null;
			Boolean appIdSoggetti = null;
			if((erogazioneAutenticazione !=null && !"".equals(erogazioneAutenticazione)) && erogazioneIsSupportatoAutenticazioneSoggetti) {
				TipoAutenticazione tipoAutenticazione = TipoAutenticazione.toEnumConstant(erogazioneAutenticazione);
				credenziale = !tipoAutenticazione.equals(TipoAutenticazione.DISABILITATO) ? CredenzialeTipo.toEnumConstant(erogazioneAutenticazione) : null;
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
			
			if(listSoggettiCompatibili != null && !listSoggettiCompatibili.isEmpty() ) {
				
				soggettiAutenticati.add("-"); // elemento nullo di default
				soggettiAutenticatiLabel.add("-");
				for (IDSoggettoDB soggetto : listSoggettiCompatibili) {
					soggettiAutenticati.add(soggetto.getTipo() + "/"+ soggetto.getNome());
					soggettiAutenticatiLabel.add(apsHelper.getLabelNomeSoggetto(protocollo, soggetto.getTipo(), soggetto.getNome())); 
					
				}
			}

			List<Parameter> lstParm = porteApplicativeHelper.getTitoloPA(parentPA, idSoggettoErogatoreDelServizio, idAsps);
			
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

			lstParm.add(ServletUtils.getParameterAggiungi());

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (apsHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd,lstParm); 

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				if(azioniDisponibiliList==null || azioniDisponibiliList.length<= sogliaAzioni) {
					// si controlla 1 poiche' c'e' il trattino nelle azioni disponibili
					
					pd.setMessage(porteApplicativeHelper.getLabelAllAzioniConfigurate(serviceBinding), Costanti.MESSAGE_TYPE_INFO);

					pd.disableEditMode();
					
				}
				else {
				
					if(azioni == null) {
						azioni = new String[0];
					}
	
					if(nome == null) {
						// nome mapping calcolato in base al numero id configurazioni non di default presenti
						nome = nomeNuovaConfigurazione;
	
						if(identificazione == null)
							identificazione = PortaApplicativaAzioneIdentificazione.DELEGATED_BY.toString();
					}
					
					if(modeCreazione == null)
						modeCreazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_EREDITA;
					
					if(modeCreazioneConnettore == null) {
						modeCreazioneConnettore = Costanti.CHECK_BOX_DISABLED;
					}
	
					if(mappingPA==null) {
						mappingPA = listaMappingValues[listaMappingValues.length-1]; // sono ordinati all'incontrario
					}
					
					if(erogazioneRuolo==null || "".equals(erogazioneRuolo))
						erogazioneRuolo = "-";
					if(erogazioneAutenticazione==null || "".equals(erogazioneAutenticazione)) {
						erogazioneAutenticazione = apsCore.getAutenticazione_generazioneAutomaticaPorteApplicative();
						
						soggettiAutenticati = new ArrayList<>();
						soggettiAutenticatiLabel = new ArrayList<>();
						if(erogazioneIsSupportatoAutenticazioneSoggetti) {
							TipoAutenticazione tipoAutenticazione = TipoAutenticazione.toEnumConstant(erogazioneAutenticazione);
							credenziale = !tipoAutenticazione.equals(TipoAutenticazione.DISABILITATO) ? CredenzialeTipo.toEnumConstant(erogazioneAutenticazione) : null;
						}
						 
						if(apsCore.isVisioneOggettiGlobale(userLogin)){
							listSoggettiCompatibili = soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiCompatibiliAccordo, null, credenziale, appIdSoggetti, pddTipologiaSoggettoAutenticati );
						}else{
							listSoggettiCompatibili = soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiCompatibiliAccordo, userLogin, credenziale, appIdSoggetti, pddTipologiaSoggettoAutenticati);
						}
						
						if(listSoggettiCompatibili != null && !listSoggettiCompatibili.isEmpty() ) {
							soggettiAutenticati.add("-"); // elemento nullo di default
							soggettiAutenticatiLabel.add("-");
							for (IDSoggettoDB soggetto : listSoggettiCompatibili) {
								soggettiAutenticati.add(soggetto.getTipo() + "/"+ soggetto.getNome());
								soggettiAutenticatiLabel.add(apsHelper.getLabelNomeSoggetto(protocollo, soggetto.getTipo(), soggetto.getNome())); 
							}
						}
					}
					if(erogazioneAutorizzazione==null || "".equals(erogazioneAutorizzazione)){
						String tipoAutorizzazione = apsCore.getAutorizzazione_generazioneAutomaticaPorteApplicative();
						erogazioneAutorizzazione = AutorizzazioneUtilities.convertToStato(tipoAutorizzazione);
						if(TipoAutorizzazione.isAuthenticationRequired(tipoAutorizzazione))
							erogazioneAutorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
						if(TipoAutorizzazione.isRolesRequired(tipoAutorizzazione))
							erogazioneAutorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
						erogazioneAutorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(tipoAutorizzazione).getValue();
					} 
					if(gestioneToken == null) {
						gestioneToken = StatoFunzionalita.DISABILITATO.getValue();
						gestioneTokenPolicy = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
						gestioneTokenOpzionale = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_OPZIONALE;
						gestioneTokenValidazioneInput = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_VALIDAZIONE_INPUT;
						gestioneTokenIntrospection = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_INTROSPECTION;
						gestioneTokenUserInfo = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_USER_INFO;
						gestioneTokenForward = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TOKEN_FORWARD;
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
					// solo in modalita' nuova
					if(initConnettore) {
						tipoconn = "";
						url = "";
						nomeCodaJms = "";
						tipoJms = ConnettoriCostanti.TIPI_CODE_JMS[0];
						user = "";
						password = "";
						initcont = "";
						urlpgk = "";
						provurl = "";
						connfact = "";
						tipoSendas = ConnettoriCostanti.TIPO_SEND_AS[0];
						httpsurl = "";
						httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
						httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
						httpshostverify = true;
						httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
						httpspath = "";
						httpstipo = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
						httpspwd = "";
						httpsstato = false;
						httpskeystore = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DEFAULT;
						httpspwdprivatekeytrust = "";
						httpspathkey = "";
						httpstipokey =ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
						httpspwdkey = "";
						httpspwdprivatekey = "";
						
						if(endpointtype==null) {
							if(!apsHelper.isModalitaCompleta()) {
								endpointtype = TipiConnettore.HTTP.getNome();
							}
							else {
								endpointtype = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DISABILITATO;
							}
						}
												
						tipoSendas = ConnettoriCostanti.TIPO_SEND_AS[0];
						tipoJms = ConnettoriCostanti.TIPI_CODE_JMS[0];

						autenticazioneHttp = apsHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);
						
						tempiRispostaEnabled=null;
						ConfigurazioneCore configCore = new ConfigurazioneCore(soggettiCore);
						ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
						tempiRispostaConnectionTimeout = configGenerale.getTempiRispostaErogazione().getConnectionTimeout().intValue()+"";
						tempiRispostaReadTimeout = configGenerale.getTempiRispostaErogazione().getReadTimeout().intValue()+"";
						tempiRispostaTempoMedioRisposta = configGenerale.getTempiRispostaErogazione().getTempoMedioRisposta().intValue()+"";
							
					}
					
					// default (da inizializzare cmq)
					if (TipiConnettore.HTTPS.toString().equals(endpointtype)) {
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
						if(httpsTrustVerifyCertS==null || "".equals(httpsTrustVerifyCertS)){
							httpsTrustVerifyCertS = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS ? Costanti.CHECK_BOX_ENABLED_TRUE : Costanti.CHECK_BOX_DISABLED;
							httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
						}
					}
					
					// Devo cmq rileggere i valori se non definiti
					if(tempiRispostaConnectionTimeout==null || "".equals(tempiRispostaConnectionTimeout) 
							|| 
							tempiRispostaReadTimeout==null || "".equals(tempiRispostaReadTimeout) 
							|| 
							tempiRispostaTempoMedioRisposta==null || "".equals(tempiRispostaTempoMedioRisposta) ){
						ConfigurazioneCore configCore = new ConfigurazioneCore(soggettiCore);
						ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
						if(tempiRispostaConnectionTimeout==null || "".equals(tempiRispostaConnectionTimeout) ) {
							tempiRispostaConnectionTimeout = configGenerale.getTempiRispostaErogazione().getConnectionTimeout().intValue()+"";
						}
						if(tempiRispostaReadTimeout==null || "".equals(tempiRispostaReadTimeout) ) {
							tempiRispostaReadTimeout = configGenerale.getTempiRispostaErogazione().getReadTimeout().intValue()+"";
						}
						if(tempiRispostaTempoMedioRisposta==null || "".equals(tempiRispostaTempoMedioRisposta) ) {
							tempiRispostaTempoMedioRisposta = configGenerale.getTempiRispostaErogazione().getTempoMedioRisposta().intValue()+"";
						}
					}
					
	
					dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idAsps, null, null, dati);
					dati = apsHelper.addConfigurazioneErogazioneToDati(TipoOperazione.ADD, dati, nome, nomeGruppo, azioni, azioniDisponibiliList, azioniDisponibiliLabelList, idAsps, idSoggettoErogatoreDelServizio,
							identificazione, asps, as, serviceBinding, modeCreazione, modeCreazioneConnettore, listaMappingLabels, listaMappingValues,
							mappingPA, mappingLabel, paMappingSelezionatoMulti, nomeSA, saSoggetti, 
							controlloAccessiStato,
							erogazioneAutenticazione, erogazioneAutenticazioneOpzionale, erogazioneAutenticazionePrincipal, erogazioneAutenticazioneParametroList,
							erogazioneIsSupportatoAutenticazioneSoggetti, erogazioneAutorizzazione, erogazioneAutorizzazioneAutenticati, 
							erogazioneAutorizzazioneRuoli, erogazioneRuolo, erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch,soggettiAutenticati,soggettiAutenticatiLabel,erogazioneSoggettoAutenticato,
							gestioneToken, policyLabels, policyValues, 
							gestioneTokenPolicy, gestioneTokenOpzionale,
							gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward,
							autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
							autorizzazioneToken,autorizzazioneTokenOptions,
							autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
							identificazioneAttributiStato, attributeAuthorityLabels, attributeAuthorityValues, attributeAuthoritySelezionate, attributeAuthorityAttributi,
							autorizzazioneAutenticatiToken, 
							autorizzazioneRuoliToken, autorizzazioneRuoliTipologiaToken, autorizzazioneRuoliMatchToken);
					
//					apsHelper.isModalitaCompleta()?null:(generaPACheckSoggetto?AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX : AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX)
					
					if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
						dati = apsHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, 
								null, //(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX , 
								url, nomeCodaJms,
								tipoJms, user,
								password, initcont, urlpgk,
								provurl, connfact, tipoSendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,TipoOperazione.ADD, 
								httpsurl, httpstipologia, httpshostverify, 
								httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
								httpsalgoritmo, httpsstato, httpskeystore,
								httpspwdprivatekeytrust, httpspathkey,
								httpstipokey, httpspwdkey, 
								httpspwdprivatekey, httpsalgoritmokey,
								httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy,
								tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_ADD, null, null,
								null, null, null, null, null, null, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
								tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
								opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop,
								requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
								requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
								responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
								autenticazioneToken, tokenPolicy, forcePDND, forceOAuth,
								listExtendedConnettore, forceEnableConnettore,
								protocollo,false,false, isApplicativiServerEnabled, erogazioneServizioApplicativoServerEnabled,
								erogazioneServizioApplicativoServer, saSoggetti);
					}
				}
					
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,
						ForwardParams.ADD());

			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.configurazioneErogazioneCheckData(TipoOperazione.ADD, nome, nomeGruppo, azioni, asps, azioniOccupate,modeCreazione,null,erogazioneIsSupportatoAutenticazioneSoggetti, mappingInfo);
			// controllo endpoint
			if(isOk && ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
				isOk = apsHelper.endPointCheckData(protocollo, true,
						endpointtype, url, nome, tipoJms,
						user, password, initcont, urlpgk, provurl, connfact,
						tipoSendas, httpsurl, httpstipologia, httpshostverify,
						httpsTrustVerifyCert, httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
						httpskeystore, httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, 
						httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy,
						tipoconn,autenticazioneHttp,
						proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
						tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
						opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop,
						requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
						requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						autenticazioneToken, tokenPolicy,
						listExtendedConnettore,erogazioneServizioApplicativoServerEnabled,
						erogazioneServizioApplicativoServer);
			}
			
			
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd,lstParm); 

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idAsps, null, null, dati);

				dati = apsHelper.addConfigurazioneErogazioneToDati(TipoOperazione.ADD, dati, nome, nomeGruppo, azioni, azioniDisponibiliList, azioniDisponibiliLabelList, idAsps, idSoggettoErogatoreDelServizio,
						identificazione, asps, as, serviceBinding, modeCreazione, modeCreazioneConnettore, listaMappingLabels, listaMappingValues,
						mappingPA, mappingLabel, paMappingSelezionatoMulti, nomeSA, saSoggetti, 
						controlloAccessiStato,
						erogazioneAutenticazione, erogazioneAutenticazioneOpzionale, erogazioneAutenticazionePrincipal, erogazioneAutenticazioneParametroList,
						erogazioneIsSupportatoAutenticazioneSoggetti, erogazioneAutorizzazione, erogazioneAutorizzazioneAutenticati, 
						erogazioneAutorizzazioneRuoli, erogazioneRuolo, erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch,soggettiAutenticati,soggettiAutenticatiLabel,erogazioneSoggettoAutenticato,
						gestioneToken, policyLabels, policyValues, 
						gestioneTokenPolicy, gestioneTokenOpzionale,
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazioneToken,autorizzazioneTokenOptions,
						autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
						identificazioneAttributiStato, attributeAuthorityLabels, attributeAuthorityValues, attributeAuthoritySelezionate, attributeAuthorityAttributi,
						autorizzazioneAutenticatiToken, 
						autorizzazioneRuoliToken, autorizzazioneRuoliTipologiaToken, autorizzazioneRuoliMatchToken);
				
				if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
					dati = apsHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, 
							null, //(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX , 
							url, nomeCodaJms,
							tipoJms, user,
							password, initcont, urlpgk,
							provurl, connfact, tipoSendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,TipoOperazione.ADD, 
							httpsurl, httpstipologia, httpshostverify, 
							httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey, 
							httpspwdprivatekey, httpsalgoritmokey,
							httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy,
							tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_ADD, null, null,
							null, null, null, null, null, null, true,
							isConnettoreCustomUltimaImmagineSalvata, 
							proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
							tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
							opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop,
							requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
							requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
							autenticazioneToken, tokenPolicy, forcePDND, forceOAuth,
							listExtendedConnettore, forceEnableConnettore,
							protocollo,false,false, isApplicativiServerEnabled, erogazioneServizioApplicativoServerEnabled,
							erogazioneServizioApplicativoServer, saSoggetti);
				}

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE, 
						ForwardParams.ADD());
			}

			if(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_NUOVA.equals(modeCreazione)
					&& CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO_PUBBLICO.equals(controlloAccessiStato)) {
				
				erogazioneAutenticazione = TipoAutenticazione.DISABILITATO.getValue();
				erogazioneAutenticazioneOpzionale = null;
				erogazioneAutenticazionePrincipal = null;
				erogazioneAutenticazioneParametroList = null;
				
				erogazioneAutorizzazione = TipoAutorizzazione.DISABILITATO.getValue();
				erogazioneAutorizzazioneAutenticati = null;
				erogazioneAutorizzazioneRuoli = null;
				erogazioneAutorizzazioneRuoliTipologia = null;
				erogazioneAutorizzazioneRuoliMatch = null;
				
			}
						
			if (apsHelper.isProfiloModIPA(protocollo) && asps != null) {
				BooleanNullable forceHttpsClientWrapper = BooleanNullable.NULL(); 
				BooleanNullable forcePDNDWrapper = BooleanNullable.NULL(); 
				BooleanNullable forceOAuthWrapper = BooleanNullable.NULL(); 
				
				List<String> azioniList = null;
				if(azioni!=null && azioni.length>0) {
					azioniList = Arrays.asList(azioni);
				}
				apsHelper.readModIConfiguration(forceHttpsClientWrapper, forcePDNDWrapper, forceOAuthWrapper, 
						idAccordo,asps.getPortType(), 
						azioniList);
				
				boolean forceDisableOptional = false;
				if(forceHttpsClientWrapper.getValue()!=null) {
					forceDisableOptional = forceHttpsClientWrapper.getValue().booleanValue();
				}
				if(forcePDNDWrapper.getValue()!=null) {
					forcePDND = forcePDNDWrapper.getValue().booleanValue();
				}
				if(forceOAuthWrapper.getValue()!=null) {
					forceOAuth = forceOAuthWrapper.getValue().booleanValue();
				}
				
				erogazioneAutenticazioneOpzionale = forceDisableOptional ? Costanti.CHECK_BOX_DISABLED : Costanti.CHECK_BOX_ENABLED;
				if(!forceDisableOptional) {
					erogazioneAutorizzazione = AutorizzazioneUtilities.STATO_DISABILITATO;
				}
				
				if(forcePDND || forceOAuth) {
					
					gestioneToken = StatoFunzionalita.ABILITATO.getValue();
					
					if(forcePDND) {
						List<String> tokenPolicies = apsHelper.getTokenPolicyGestione(true, false, 
								false); // alla posizione 0 NON viene aggiunto -
						if(tokenPolicies!=null && !tokenPolicies.isEmpty() &&
							(gestioneTokenPolicy==null || StringUtils.isEmpty(gestioneTokenPolicy)) 
							){
							gestioneTokenPolicy = tokenPolicies.get(0);  // dovrebbe già essere stata selezionata prima
						}
					}
					else {
						List<String> tokenPolicies = apsHelper.getTokenPolicyGestione(false, true, 
								false); // alla posizione 0 NON viene aggiunto -
						if(tokenPolicies!=null && !tokenPolicies.isEmpty() &&
							(gestioneTokenPolicy==null || StringUtils.isEmpty(gestioneTokenPolicy)) 
						){
							gestioneTokenPolicy = tokenPolicies.get(0);  // dovrebbe già essere stata selezionata prima
						}
					}
					
					gestioneTokenOpzionale = StatoFunzionalita.DISABILITATO.getValue();
					
					if(gestioneTokenPolicy!=null && StringUtils.isNotEmpty(gestioneTokenPolicy) && 
							!CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO.equals(gestioneTokenPolicy)) {
						GenericProperties gp = confCore.getGenericProperties(gestioneTokenPolicy, CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION, false);
						if(gp!=null && gp.sizePropertyList()>0) {
							for (Property p : gp.getPropertyList()) {
								if(org.openspcoop2.pdd.core.token.Costanti.POLICY_VALIDAZIONE_STATO.equals(p.getNome())) {
									if("true".equalsIgnoreCase(p.getValore())) {
										gestioneTokenValidazioneInput = StatoFunzionalita.ABILITATO.getValue();
									}
									else {
										gestioneTokenValidazioneInput = StatoFunzionalita.DISABILITATO.getValue();
									}
								}
								else if(org.openspcoop2.pdd.core.token.Costanti.POLICY_INTROSPECTION_STATO.equals(p.getNome())) {
									if("true".equalsIgnoreCase(p.getValore())) {
										gestioneTokenIntrospection = StatoFunzionalita.ABILITATO.getValue();
									}
									else {
										gestioneTokenIntrospection = StatoFunzionalita.DISABILITATO.getValue();
									}
								}
								else if(org.openspcoop2.pdd.core.token.Costanti.POLICY_USER_INFO_STATO.equals(p.getNome())) {
									if("true".equalsIgnoreCase(p.getValore())) {
										gestioneTokenUserInfo = StatoFunzionalita.ABILITATO.getValue();
									}
									else {
										gestioneTokenUserInfo = StatoFunzionalita.DISABILITATO.getValue();
									}
								}
								else if(org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_FORWARD_STATO.equals(p.getNome())) {
									if("true".equalsIgnoreCase(p.getValore())) {
										gestioneTokenForward = StatoFunzionalita.ABILITATO.getValue();
									}
									else {
										gestioneTokenForward = StatoFunzionalita.DISABILITATO.getValue();
									}
								}
							}
						}
					}
				}
			}
			
			AccordiServizioParteSpecificaUtilities.addAccordoServizioParteSpecificaPorteApplicative(mappingDefault,
					mappingSelezionato,
					nome, nomeGruppo, azioni, modeCreazione, modeCreazioneConnettore,
					endpointtype, tipoconn, autenticazioneHttp,
					connettoreDebug,
					url,
					nomeCodaJms, tipoJms, 
					initcont, urlpgk, provurl, connfact, tipoSendas, 
					user, password,
					httpsurl, httpstipologia, httpshostverify,
					httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
					httpsalgoritmo, httpsstato, httpskeystore,
					httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey,
					httpspwdprivatekey, httpsalgoritmokey,
					httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy,
					proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
					tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
					opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop,
					requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
					requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					autenticazioneToken, tokenPolicy,
					listExtendedConnettore,
					erogazioneAutenticazione, erogazioneAutenticazioneOpzionale, erogazioneAutenticazionePrincipal, erogazioneAutenticazioneParametroList,
					erogazioneAutorizzazione, erogazioneAutorizzazioneAutenticati, erogazioneAutorizzazioneRuoli, erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch,
					nomeSA, erogazioneRuolo, erogazioneSoggettoAutenticato, 
					autorizzazioneAutenticatiToken, 
					autorizzazioneRuoliToken, autorizzazioneRuoliTipologiaToken, autorizzazioneRuoliMatchToken,
					autorizzazioneTokenOptions,
					autorizzazioneScope, scope, autorizzazioneScopeMatch,allegatoXacmlPolicy,
					gestioneToken, 
					gestioneTokenPolicy,  gestioneTokenOpzionale,  
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward,
					autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
					asps, 
					protocollo, userLogin,
					apsCore, apsHelper,erogazioneServizioApplicativoServer,
					identificazioneAttributiStato, attributeAuthoritySelezionate, attributeAuthorityAttributi);
			
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.CONFIGURAZIONE_EROGAZIONE;

			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);

			if(asps==null) {
				throw new DriverControlStationException("Asps is null");
			}
			List<MappingErogazionePortaApplicativa> lista = apsCore.mappingServiziPorteAppList(idServizio2,asps.getId(), ricerca);

			apsHelper.prepareServiziConfigurazioneList(lista, idAsps, null, ricerca);
			
			// reset posizione tab
			if(!apsHelper.isModalitaCompleta())
				ServletUtils.setObjectIntoSession(request, session, "0", CostantiControlStation.PARAMETRO_ID_TAB);	

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			ForwardParams fwP = apsHelper.isModalitaCompleta() ? ForwardParams.ADD() : AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE;
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE, 
					fwP);

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,
					ForwardParams.ADD());
		}  
	}
}
