/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.ProprietaOggetto;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.TipoBehaviour;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneMultiDeliver;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils;
import org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils;
import org.openspcoop2.pdd.core.behaviour.conditional.ConfigurazioneSelettoreCondizioneRegola;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationCoreException;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.OggettoDialogEnum;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoreStatusParams;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiHelper;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiUtilities;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteApplicativeConnettoriMultipliChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeConnettoriMultipliChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {


		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session, request);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
			String idPorta = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";

			String idConnTab = porteApplicativeHelper.getParametroInteger(CostantiControlStation.PARAMETRO_ID_CONN_TAB);
			if(StringUtils.isNotEmpty(idConnTab)) {
				ServletUtils.setObjectIntoSession(request, session, idConnTab, CostantiControlStation.PARAMETRO_ID_CONN_TAB);
			}

			String nomeSAConnettore = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME_SA);
			String nomeConnettore = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME);
			String descrizioneConnettore = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DESCRIZIONE);
			String statoConnettore = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO);
			String filtriConnettore = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_FILTRI);

			String nomeservizioApplicativo = porteApplicativeHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO);
			if(nomeservizioApplicativo == null)
				nomeservizioApplicativo = "";
			String idsil = porteApplicativeHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);

			String tipoauthRichiesta = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_TIPO_AUTENTICAZIONE);

			String sbustamento = porteApplicativeHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
			String sbustamentoInformazioniProtocolloRichiesta = porteApplicativeHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
			String getmsg = porteApplicativeHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
			String getmsgUsername = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
			String getmsgPassword = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			String changepwd = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD);
			String tipoCredenzialiSSLVerificaTuttiICampi = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
			if (tipoCredenzialiSSLVerificaTuttiICampi == null) {
				tipoCredenzialiSSLVerificaTuttiICampi = ConnettoriCostanti.DEFAULT_VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI;
			}
			
			String invrifRichiesta = porteApplicativeHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RICHIESTA);

			String risprif = porteApplicativeHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);

			Properties parametersPOST = null;

			String endpointtype = porteApplicativeHelper.readEndPointType();
			String tipoconn = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO );
			String autenticazioneHttp = porteApplicativeHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);

			String connettoreDebug = porteApplicativeHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);

			// token policy
			String autenticazioneTokenS = porteApplicativeHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			boolean autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			String tokenPolicy = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
			boolean forcePDND = false;
			boolean forceOAuth = false;

			// proxy
			String proxyEnabled = porteApplicativeHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxyHostname = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxyPort = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxyUsername = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxyPassword = porteApplicativeHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);

			// tempi risposta
			String tempiRispostaEnabled = porteApplicativeHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			String tempiRispostaConnectionTimeout = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			String tempiRispostaReadTimeout = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			String tempiRispostaTempoMedioRisposta = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);

			// opzioni avanzate
			String transferMode = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transferModeChunkSize = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirectMode = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirectMaxHop = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String httpImpl = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_HTTP_IMPL);
			String opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(porteApplicativeHelper, transferMode, redirectMode, httpImpl);

			String user= null;
			String password =null;

			// http
			String url = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL  );
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = porteApplicativeHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}

			// api key
			String autenticazioneApiKey = porteApplicativeHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_API_KEY);
			String apiKeyHeader = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_HEADER);
			if(apiKeyHeader==null || StringUtils.isEmpty(apiKeyHeader)) {
				apiKeyHeader = CostantiConnettori.DEFAULT_HEADER_API_KEY;
			}
			String apiKeyValue = porteApplicativeHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_VALUE);
			String appIdHeader = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_APP_ID_HEADER);
			if(appIdHeader==null || StringUtils.isEmpty(appIdHeader)) {
				appIdHeader = CostantiConnettori.DEFAULT_HEADER_APP_ID;
			}
			String appIdValue = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_APP_ID_VALUE);
			String useOAS3NamesTmp = porteApplicativeHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_NOMI_OAS);
			boolean useOAS3Names=true;
			if(useOAS3NamesTmp!=null && StringUtils.isNotEmpty(useOAS3NamesTmp)) {
				useOAS3Names = ServletUtils.isCheckBoxEnabled(useOAS3NamesTmp);
			}
			else {
				useOAS3Names = porteApplicativeHelper.isAutenticazioneApiKeyUseOAS3Names(apiKeyHeader, appIdHeader);
			}
			String useAppIdTmp = porteApplicativeHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_USE_APP_ID);
			boolean useAppId=false;
			if(useAppIdTmp!=null && StringUtils.isNotEmpty(useAppIdTmp)) {
				useAppId = ServletUtils.isCheckBoxEnabled(useAppIdTmp);
			}
			else {
				useAppId = porteApplicativeHelper.isAutenticazioneApiKeyUseAppId(appIdValue);
			}
			
			// jms
			String nomeCodaJms = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipoJms = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String initcont = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String tipoSendas = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(endpointtype)){
				user = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				password = porteApplicativeHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}

			// https
			String httpsurl = url;
			String httpstipologia = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE );
			String httpshostverifyS = porteApplicativeHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			String httpsTrustVerifyCertS = porteApplicativeHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS );
			boolean httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
			String httpspath = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION );
			String httpstipo = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = porteApplicativeHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = porteApplicativeHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			String httpskeystore = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = porteApplicativeHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = porteApplicativeHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = porteApplicativeHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			String httpsKeyAlias = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
			String httpsTrustStoreCRLs = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
			String httpsTrustStoreOCSPPolicy = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
			String httpsKeyStoreBYOKPolicy = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_BYOK_POLICY);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = porteApplicativeHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}

			// file
			String requestOutputFileName = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			String requestOutputFileNamePermissions = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS);
			String requestOutputFileNameHeaders = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			String requestOutputFileNameHeadersPermissions = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_PERMISSIONS);
			String requestOutputParentDirCreateIfNotExists = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			String requestOutputOverwriteIfExists = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			String responseInputMode = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			String responseInputFileName = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			String responseInputFileNameHeaders = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			String responseInputDeleteAfterRead = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			String responseInputWaitTime = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);

			// status
			ConnettoreStatusParams connettoreStatusParams = ConnettoreStatusParams.fillFrom(porteApplicativeHelper);
						
			String erogazioneServizioApplicativoServerEnabledS = porteApplicativeHelper.getParametroBoolean(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ABILITA_USO_APPLICATIVO_SERVER);
			boolean erogazioneServizioApplicativoServerEnabled = ServletUtils.isCheckBoxEnabled(erogazioneServizioApplicativoServerEnabledS);
			String erogazioneServizioApplicativoServer = porteApplicativeHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_APPLICATIVO_SERVER);

			boolean httpshostverify = false;
			if (httpshostverifyS != null && httpshostverifyS.equals(Costanti.CHECK_BOX_ENABLED))
				httpshostverify = true;
			boolean httpsstato = false;
			if (httpsstatoS != null && httpsstatoS.equals(Costanti.CHECK_BOX_ENABLED))
				httpsstato = true;

			Boolean isConnettoreCustomUltimaImmagineSalvata = null;

			Connettore conTmp = null;
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_PORTA_APPLICATIVA_ADD, porteApplicativeHelper, 
							parametersPOST, (endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro

			boolean accessoDaListaAPS = false;
			String accessoDaAPSParametro = porteApplicativeHelper.getParametroBoolean(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			if(Costanti.CHECK_BOX_ENABLED_TRUE.equals(accessoDaAPSParametro)) {
				accessoDaListaAPS = true;
			}

			boolean isModalitaCompleta = porteApplicativeHelper.isModalitaCompleta();
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session, request).getValue();

			boolean postBackViaPost = true;

			// Prendo nome, tipo e pdd del soggetto
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);

			// Prendo nome della porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			if(pa==null) {
				throw new ControlStationCoreException("PortaApplicativa con id '"+idInt+"' non trovata");
			}
			boolean behaviourConFiltri = ConditionalUtils.isConfigurazioneCondizionaleByFilter(pa, ControlStationCore.getLog());
			String idporta = pa.getNome();
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(pa.getTipoSoggettoProprietario());
			TipoBehaviour beaBehaviourType = TipoBehaviour.toEnumConstant(pa.getBehaviour().getNome());
			long idAspsLong = Long.parseLong(idAsps);
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idAspsLong);
			AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(apc.getServiceBinding());

			// valora iniziale della configurazione
			PortaApplicativaServizioApplicativo oldPaSA = null;
			for (PortaApplicativaServizioApplicativo paSATmp : pa.getServizioApplicativoList()) {
				if(paSATmp.getNome().equals(nomeSAConnettore)) {
					oldPaSA = paSATmp;					
				}
			}
			if(oldPaSA==null) {
				throw new Exception("ServizioApplicativo con nome '"+nomeSAConnettore+"' non trovato");
			}
			
			boolean integrationManagerEnabled = !porteApplicativeHelper.isModalitaStandard() && porteApplicativeCore.isIntegrationManagerEnabled();
			boolean isSoapOneWay = false;
			if(pa!=null) {
				MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = porteApplicativeCore.getMappingErogazionePortaApplicativa(pa);
				isSoapOneWay = porteApplicativeHelper.isSoapOneWay(pa, mappingErogazionePortaApplicativa, asps, apc, serviceBinding);
			}
			if(integrationManagerEnabled) {
				if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.equals(beaBehaviourType)) {
					ConfigurazioneMultiDeliver confMultiDeliver = MultiDeliverUtils.read(pa);
					String nomeConnettoreSincrono = null;
					if(confMultiDeliver!=null) {
						nomeConnettoreSincrono = confMultiDeliver.getTransazioneSincrona_nomeConnettore();
					}
					String oldNomeConnettore = porteApplicativeHelper.getLabelNomePortaApplicativaServizioApplicativo(oldPaSA);
					integrationManagerEnabled = !oldNomeConnettore.equals(nomeConnettoreSincrono); // l'integration manager e' abilitato solamente se e' il connettore adibito alle notifiche
				}
				else {
					integrationManagerEnabled = isSoapOneWay; // l'integration manager e' abilitato solamente se c'e' almeno una azione oneway in api soap
				}
			}
			
			boolean isApplicativiServerEnabled = apsCore.isApplicativiServerEnabled(porteApplicativeHelper);
			String filtroTipoSA = isApplicativiServerEnabled ? ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER : null;

			String nomeProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(pa.getTipoSoggettoProprietario());

			PortaApplicativaServizioApplicativoConnettore oldDatiConnettore = oldPaSA.getDatiConnettore();
			IDServizioApplicativo idSA = new IDServizioApplicativo();
			idSA.setNome(oldPaSA.getNome());
			idSA.setIdSoggettoProprietario(new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
			IDServizioApplicativoDB idSADB = new IDServizioApplicativoDB(idSA);
			idSADB.setId(oldPaSA.getId());
			ServizioApplicativo oldSA = saCore.getServizioApplicativo(idSA );
			if(oldSA==null) {
				throw new ControlStationCoreException("ServizioApplicativo con id '"+idSA+"' non trovata");
			}
			InvocazionePorta invocazionePorta = oldSA.getInvocazionePorta();
			InvocazioneServizio oldIS = oldSA.getInvocazioneServizio();
			if(oldIS==null) {
				throw new ControlStationCoreException("ServizioApplicativo con id '"+idSA+"' senza InvocazioneServizio");
			}
			InvocazioneCredenziali oldCis = oldIS.getCredenziali();
			Connettore oldConnis = oldIS.getConnettore();
			isConnettoreCustomUltimaImmagineSalvata = oldConnis.getCustom();
			List<Property> oldCP = oldConnis.getPropertyList();
			String oldTipoSA = oldSA.getTipo();

			// Lista dei servizi applicativi per la creazione automatica
			List<IDServizioApplicativoDB> listaIdSAServer = null;	
			if ((idsogg != null) && !idsogg.equals("")) {
				long idErogatore = Long.parseLong(idsogg);

				// I servizi applicativi da visualizzare sono quelli che hanno
				// -Integration Manager (getMessage abilitato)
				// -connettore != disabilitato
				listaIdSAServer = saCore.getIdServiziApplicativiWithIdErogatore(idErogatore, filtroTipoSA, integrationManagerEnabled, true);

			}

			String postBackElementName = porteApplicativeHelper.getPostBackElementName();

			boolean initConnettoreFromSA = false;
			boolean initConnettore = false;
			if(postBackElementName != null ){
				
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CONNETTORE_ABILITA_USO_APPLICATIVO_SERVER)){
					// devo resettare il connettore se passo da SA Server a Default
					if(!erogazioneServizioApplicativoServerEnabled) {
						
						boolean isDefault = true;
						if(oldDatiConnettore != null) {
							isDefault = !oldDatiConnettore.isNotifica();
						}
												
						// vecchio SA era un Server allora devo fare il reinit del connettore
						if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(oldTipoSA)) {
							
							if(isDefault) {
								
								idSA.setNome(pa.getServizioApplicativoDefault());
								idSADB.setNome(pa.getServizioApplicativoDefault());
								oldSA = saCore.getServizioApplicativo(idSA);
								idSADB.setId(oldSA.getId());
								invocazionePorta = oldSA.getInvocazionePorta();
								oldIS = oldSA.getInvocazioneServizio();
								oldCis = oldIS.getCredenziali();
								oldConnis = oldIS.getConnettore();
								oldCP = oldConnis.getPropertyList();
								oldTipoSA = oldSA.getTipo();
								
								// reset dei fields
								erogazioneServizioApplicativoServer = null;
								
								sbustamento = null;
								sbustamentoInformazioniProtocolloRichiesta = null;
								getmsg = null;
								getmsgUsername = null;
								getmsgPassword = null;
								invrifRichiesta = null;
								risprif = null;
								tipoauthRichiesta = null;
								endpointtype = porteApplicativeHelper.readEndPointType();
								tipoconn = null;
								autenticazioneHttp = null;
								user = null;
								password = null;
								connettoreDebug = null;
	
								// token policy
								autenticazioneTokenS = null;
								autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
								tokenPolicy = null;
	
								// api key
								autenticazioneApiKey=null;
								apiKeyValue=null;
								apiKeyHeader=null;
								appIdValue=null;
								appIdHeader=null;
								
								// proxy
								proxyEnabled = null;
								proxyHostname = null;
								proxyPort = null;
								proxyUsername = null;
								proxyPassword = null;
	
								// tempi risposta
								tempiRispostaEnabled = null;
								tempiRispostaConnectionTimeout = null;
								tempiRispostaReadTimeout = null;
								tempiRispostaTempoMedioRisposta = null;
	
								// opzioni avanzate
								transferMode = null;
								transferModeChunkSize = null;
								redirectMode = null;
								redirectMaxHop = null;
								httpImpl = null;
								opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(porteApplicativeHelper, transferMode, redirectMode, httpImpl);
	
								// http
								url = null;
	
								// jms
								nomeCodaJms = null;
								tipoJms = null;
								initcont = null;
								urlpgk = null;
								provurl = null;
								connfact = null;
								tipoSendas = null;
	
								// https
								httpsurl = url;
								httpstipologia = null;
								httpshostverifyS = null;
								httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
								httpsTrustVerifyCertS=null;
								httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
								httpspath = null;
								httpstipo = null;
								httpspwd = null;
								httpsalgoritmo = null;
								httpsstatoS = null;
								httpsstato = ServletUtils.isCheckBoxEnabled(httpsstatoS);
								httpskeystore = null;
								httpspwdprivatekeytrust = null;
								httpspathkey = null;
								httpstipokey = null;
								httpspwdkey = null;
								httpspwdprivatekey = null;
								httpsalgoritmokey = null;
								httpsKeyAlias = null;
								httpsTrustStoreCRLs = null;
								httpsTrustStoreOCSPPolicy = null;
								httpsKeyStoreBYOKPolicy = null;
	
								// file
								requestOutputFileName = null;
								requestOutputFileNamePermissions = null;
								requestOutputFileNameHeaders = null;
								requestOutputFileNameHeadersPermissions = null;
								requestOutputParentDirCreateIfNotExists = null;
								requestOutputOverwriteIfExists = null;
								responseInputMode = null;
								responseInputFileName = null;
								responseInputFileNameHeaders = null;
								responseInputDeleteAfterRead = null;
								responseInputWaitTime = null;
								
								initConnettoreFromSA = true; // devo rileggerlo dal nuovo SA
								
							}
							else {
								initConnettore = true;
							}
							
						} else {
							initConnettoreFromSA = true;
						}
					} else {
						// vecchio SA non era un Server allora devo fare il reinit del connettore
						if(!ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(oldTipoSA)) {
							initConnettoreFromSA = true;
						} else {
							initConnettore = true;
						} 
					}
				}
				
				// Tempi di risposta
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE) &&
					(tempiRispostaConnectionTimeout==null || "".equals(tempiRispostaConnectionTimeout) 
							|| 
							tempiRispostaReadTimeout==null || "".equals(tempiRispostaReadTimeout) 
							|| 
							tempiRispostaTempoMedioRisposta==null || "".equals(tempiRispostaTempoMedioRisposta) )
					){
						
					ConfigurazioneCore configCore = new ConfigurazioneCore(porteApplicativeCore);
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
				
				
				// Change Password basic/api
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD) &&
					(!ServletUtils.isCheckBoxEnabled(changepwd)) &&
					(invocazionePorta != null && invocazionePorta.sizeCredenzialiList()>0)
					){
					getmsgPassword = invocazionePorta.getCredenziali(0).getPassword();
				}
			}

			boolean forceEnableConnettore = false;
			if(getmsg!=null && CostantiConfigurazione.ABILITATO.toString().equals(getmsg)) {
				forceEnableConnettore = false;
			}
			else {
				forceEnableConnettore = true;
				if(TipiConnettore.DISABILITATO.getNome().equals(endpointtype)) {
					endpointtype = TipiConnettore.HTTP.getNome();
				}
			}

			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				if(accessoDaListaAPS) {
					if(!isModalitaCompleta) {
						if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
							labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI;
						} else {
							labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DI+
									porteApplicativeHelper.getLabelIdServizio(asps);
						}
					}
					else {
						labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI;
					}
				}
				else {
					labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DI,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI,
							pa);
				}
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DI+idporta;
			}

			if(accessoDaListaAPS) {
				lstParam.remove(lstParam.size()-1);
			}

			List<Parameter> listParametersConfigutazioneConnettoriMultipli = new ArrayList<>();
			Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
			Parameter pNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, pa.getNome());
			Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
			String idTabP = porteApplicativeHelper.getParametroInteger(CostantiControlStation.PARAMETRO_ID_TAB);
			Parameter pIdTab = new Parameter(CostantiControlStation.PARAMETRO_ID_TAB, idTabP != null ? idTabP : "");
			Parameter pIdConnTab = new Parameter(CostantiControlStation.PARAMETRO_ID_CONN_TAB, idConnTab != null ? idConnTab : "");
			Parameter pAccessoDaAPS = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, accessoDaAPSParametro != null ? accessoDaAPSParametro : "");
			String connettoreAccessoGruppi = porteApplicativeHelper.getParametroBoolean(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI);
			String connettoreRegistro = porteApplicativeHelper.getParametroBoolean(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO);
			Parameter pConnettoreAccessoDaGruppi = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, connettoreAccessoGruppi);
			Parameter pConnettoreAccesso = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, connettoreRegistro);
			String connettoreAccessoListaConnettori = porteApplicativeHelper.getParametroBoolean(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_LISTA_CONNETTORI_MULTIPLI);

			String idConnTabP = porteApplicativeHelper.getParametroInteger(CostantiControlStation.PARAMETRO_ID_CONN_TAB);
			Parameter pIdConTab = new Parameter(CostantiControlStation.PARAMETRO_ID_CONN_TAB, idConnTabP != null ? idConnTabP : "");
			
			String visualizzaDatiGenerali = porteApplicativeHelper.getParametroBoolean(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_DATI_GENERALI);
			String visualizzaDescrizione = porteApplicativeHelper.getParametroBoolean(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_DESCRIZIONE);
			String visualizzaConnettore = porteApplicativeHelper.getParametroBoolean(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_CONNETTORE);
			String visualizzaFiltri = porteApplicativeHelper.getParametroBoolean(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIGURAZIONE_FILTRO);
			boolean visualizzaSezioneDatiGenerali = ServletUtils.isCheckBoxEnabled(visualizzaDatiGenerali);
			boolean visualizzaSezioneDescrizione = ServletUtils.isCheckBoxEnabled(visualizzaDescrizione);
			boolean visualizzaSezioneConnettore = ServletUtils.isCheckBoxEnabled(visualizzaConnettore);
			boolean visualizzaSezioneFiltri = ServletUtils.isCheckBoxEnabled(visualizzaFiltri);
			
			String nomeConnettoreChangeListBreadcump = null;
			if(visualizzaSezioneDatiGenerali) {
				nomeConnettoreChangeListBreadcump = nomeConnettore;
			}
			else {
				
				// valora iniziale della configurazione
				PortaApplicativaServizioApplicativo paSA = null;
				for (PortaApplicativaServizioApplicativo paSATmp : pa.getServizioApplicativoList()) {
					if(paSATmp.getNome().equals(nomeSAConnettore)) {
						paSA = paSATmp;					
					}
				}

				PortaApplicativaServizioApplicativoConnettore datiConnettore = paSA.getDatiConnettore();
				
				if(datiConnettore!=null) {
					nomeConnettoreChangeListBreadcump = datiConnettore.getNome();
				}
				if(nomeConnettoreChangeListBreadcump==null) {
					nomeConnettoreChangeListBreadcump = CostantiConfigurazione.NOME_CONNETTORE_DEFAULT;
				}
			}
			Parameter pChangeNomeConnettoreBreadcump = new Parameter(CostantiControlStation.PARAMETRO_FROM_BREADCUMP_CHANGE_NOME_CONNETTORE, nomeConnettoreChangeListBreadcump);
						
			listParametersConfigutazioneConnettoriMultipli.add(pIdSogg);
			listParametersConfigutazioneConnettoriMultipli.add(pIdPorta);
			listParametersConfigutazioneConnettoriMultipli.add(pNomePorta);
			listParametersConfigutazioneConnettoriMultipli.add(pIdAsps);
			listParametersConfigutazioneConnettoriMultipli.add(pIdTab);
			listParametersConfigutazioneConnettoriMultipli.add(pIdConnTab);
			listParametersConfigutazioneConnettoriMultipli.add(pAccessoDaAPS);
			listParametersConfigutazioneConnettoriMultipli.add(pConnettoreAccessoDaGruppi);
			listParametersConfigutazioneConnettoriMultipli.add(pConnettoreAccesso);
			listParametersConfigutazioneConnettoriMultipli.add(pChangeNomeConnettoreBreadcump);

			lstParam.add(new Parameter(labelPerPorta,  PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LIST, listParametersConfigutazioneConnettoriMultipli.toArray(new Parameter[1])));

			// Label diversa in base all'operazione
			String oldNomeConnettore = porteApplicativeHelper.getLabelNomePortaApplicativaServizioApplicativo(oldPaSA);
			String labelPagina = oldNomeConnettore;




			lstParam.add(new Parameter(labelPagina, null));

			// Se nome = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				if(nomeConnettore == null) {
					nomeConnettore = oldNomeConnettore;
					descrizioneConnettore = oldDatiConnettore != null ? oldDatiConnettore.getDescrizione() : "";
					filtriConnettore = "";
					if(oldDatiConnettore != null) {
						filtriConnettore = StringUtils.join(oldDatiConnettore.getFiltroList(), ",");
					}

					statoConnettore = oldDatiConnettore != null ? oldDatiConnettore.getStato().getValue() : StatoFunzionalita.ABILITATO.getValue();
					initConnettoreFromSA = true;
				}

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
						if(!porteApplicativeHelper.isModalitaCompleta()) {
							endpointtype = TipiConnettore.HTTP.getNome();
						}
						else {
							endpointtype = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DISABILITATO;
						}
					}

					autenticazioneHttp = porteApplicativeHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);

					tempiRispostaEnabled = null;
					ConfigurazioneCore configCore = new ConfigurazioneCore(porteApplicativeCore);
					ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
					tempiRispostaConnectionTimeout = configGenerale.getTempiRispostaErogazione().getConnectionTimeout().intValue()+"";
					tempiRispostaReadTimeout = configGenerale.getTempiRispostaErogazione().getReadTimeout().intValue()+"";
					tempiRispostaTempoMedioRisposta = configGenerale.getTempiRispostaErogazione().getTempoMedioRisposta().intValue()+"";


					// Devo cmq rileggere i valori se non definiti
					if(tempiRispostaConnectionTimeout==null || "".equals(tempiRispostaConnectionTimeout) 
							|| 
							tempiRispostaReadTimeout==null || "".equals(tempiRispostaReadTimeout) 
							|| 
							tempiRispostaTempoMedioRisposta==null || "".equals(tempiRispostaTempoMedioRisposta) ){
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

				if(initConnettoreFromSA) {
					if (erogazioneServizioApplicativoServer == null && isApplicativiServerEnabled &&
						// se in configurazione ho selezionato un server
						(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(oldTipoSA)) 
						){
						erogazioneServizioApplicativoServer = oldSA.getNome();
						erogazioneServizioApplicativoServerEnabled = true;
					}

					if (sbustamento == null &&
						oldIS.getSbustamentoSoap()!=null) {
						sbustamento = oldIS.getSbustamentoSoap().toString();
					}
					if (sbustamentoInformazioniProtocolloRichiesta == null &&
						oldIS.getSbustamentoInformazioniProtocollo()!=null) {
						sbustamentoInformazioniProtocolloRichiesta = oldIS.getSbustamentoInformazioniProtocollo().toString();
					}
					if (getmsg == null &&
						(oldIS.getGetMessage()!=null) 
						){
						getmsg = oldIS.getGetMessage().toString();
						if(CostantiConfigurazione.ABILITATO.toString().equals(getmsg) &&
							(invocazionePorta!=null && invocazionePorta.sizeCredenzialiList()>0) 
							){
							for (int i = 0; i < invocazionePorta.sizeCredenzialiList(); i++) {
								Credenziali c = invocazionePorta.getCredenziali(i);
								if(CredenzialeTipo.BASIC.equals(c.getTipo())) {
									getmsgUsername = c.getUser();
									getmsgPassword = c.getPassword();
									tipoCredenzialiSSLVerificaTuttiICampi = c.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
									break;
								}
							}
						}
					}
										
					if(getmsg!=null && CostantiConfigurazione.ABILITATO.toString().equals(getmsg)) {
						forceEnableConnettore = false;
					}

					if (invrifRichiesta == null) {
						if(oldIS.getInvioPerRiferimento()!=null)
							invrifRichiesta = oldIS.getInvioPerRiferimento().toString();
						if ((invrifRichiesta == null) || "".equals(invrifRichiesta)) {
							invrifRichiesta = CostantiConfigurazione.DISABILITATO.toString();
						}
					}
					if (risprif == null) {
						if(oldIS.getRispostaPerRiferimento()!=null)
							risprif = oldIS.getRispostaPerRiferimento().toString();
						if ((risprif == null) || "".equals(risprif)) {
							risprif = CostantiConfigurazione.DISABILITATO.toString();
						}
					}

					if ((tipoauthRichiesta == null) && (oldIS != null) && oldIS.getAutenticazione()!=null) {
						tipoauthRichiesta = oldIS.getAutenticazione().getValue();
						if(tipoauthRichiesta!=null) {
							// nop
						}
					}
					if ((user == null) && (oldCis != null)) {
						user = oldCis.getUser();
						password = oldCis.getPassword();
					}
					if (endpointtype == null) {
						if ((oldConnis.getCustom()!=null && oldConnis.getCustom()) && 
								!oldConnis.getTipo().equals(TipiConnettore.HTTPS.toString()) && 
								!oldConnis.getTipo().equals(TipiConnettore.FILE.toString()) &&
								!oldConnis.getTipo().equals(TipiConnettore.STATUS.toString())) {
							endpointtype = TipiConnettore.CUSTOM.toString();
							tipoconn = oldConnis.getTipo();
						} else
							endpointtype = oldConnis.getTipo();
					}

					Map<String, String> props = null;
					if(oldIS!=null && oldIS.getConnettore()!=null)
						props = oldIS.getConnettore().getProperties();

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

					if(proxyEnabled==null && props!=null){
						String v = props.get(CostantiDB.CONNETTORE_PROXY_TYPE);
						if(v!=null && !"".equals(v)){
							proxyEnabled = Costanti.CHECK_BOX_ENABLED_TRUE;

							// raccolgo anche altre proprietà
							v = props.get(CostantiDB.CONNETTORE_PROXY_HOSTNAME);
							if(v!=null && !"".equals(v)){
								proxyHostname = v.trim();
							}
							v = props.get(CostantiDB.CONNETTORE_PROXY_PORT);
							if(v!=null && !"".equals(v)){
								proxyPort = v.trim();
							}
							v = props.get(CostantiDB.CONNETTORE_PROXY_USERNAME);
							if(v!=null && !"".equals(v)){
								proxyUsername = v.trim();
							}
							v = props.get(CostantiDB.CONNETTORE_PROXY_PASSWORD);
							if(v!=null && !"".equals(v)){
								proxyPassword = v.trim();
							}
						}
					}

					if(tempiRispostaEnabled == null ||
							tempiRispostaConnectionTimeout==null || "".equals(tempiRispostaConnectionTimeout) 
							|| 
							tempiRispostaReadTimeout==null || "".equals(tempiRispostaReadTimeout) 
							|| 
							tempiRispostaTempoMedioRisposta==null || "".equals(tempiRispostaTempoMedioRisposta) ){

						ConfigurazioneCore configCore = new ConfigurazioneCore(soggettiCore);
						ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();

						if( props!=null ) {
							if(tempiRispostaConnectionTimeout==null || "".equals(tempiRispostaConnectionTimeout) ) {
								String v = props.get(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT);
								if(v!=null && !"".equals(v)){
									tempiRispostaConnectionTimeout = v.trim();
									tempiRispostaEnabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
								}
								else {
									tempiRispostaConnectionTimeout = configGenerale.getTempiRispostaErogazione().getConnectionTimeout().intValue()+"";
								}
							}

							if(tempiRispostaReadTimeout==null || "".equals(tempiRispostaReadTimeout) ) {
								String v = props.get(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT);
								if(v!=null && !"".equals(v)){
									tempiRispostaReadTimeout = v.trim();
									tempiRispostaEnabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
								}
								else {
									tempiRispostaReadTimeout = configGenerale.getTempiRispostaErogazione().getReadTimeout().intValue()+"";
								}
							}

							if(tempiRispostaTempoMedioRisposta==null || "".equals(tempiRispostaTempoMedioRisposta) ) {
								String v = props.get(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA);
								if(v!=null && !"".equals(v)){
									tempiRispostaTempoMedioRisposta = v.trim();
									tempiRispostaEnabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
								}
								else {
									tempiRispostaTempoMedioRisposta = configGenerale.getTempiRispostaErogazione().getTempoMedioRisposta().intValue()+"";
								}
							}
						}
						else {
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
					}

					if(transferMode==null && props!=null){
						String v = props.get(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE);
						if(v!=null && !"".equals(v)){

							transferMode = v.trim();

							if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome().equals(transferMode)){
								// raccolgo anche altra proprietà correlata
								v = props.get(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE);
								if(v!=null && !"".equals(v)){
									transferModeChunkSize = v.trim();
								}
							}

						}
					}

					if(redirectMode==null && props!=null){
						String v = props.get(CostantiDB.CONNETTORE_HTTP_REDIRECT_FOLLOW);
						if(v!=null && !"".equals(v)){

							if("true".equalsIgnoreCase(v.trim()) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v.trim())){
								redirectMode = CostantiConfigurazione.ABILITATO.getValue();
							}
							else{
								redirectMode = CostantiConfigurazione.DISABILITATO.getValue();
							}					

							if(CostantiConfigurazione.ABILITATO.getValue().equals(redirectMode)){
								// raccolgo anche altra proprietà correlata
								v = props.get(CostantiDB.CONNETTORE_HTTP_REDIRECT_MAX_HOP);
								if(v!=null && !"".equals(v)){
									redirectMaxHop = v.trim();
								}
							}

						}
					}

					if(httpImpl==null && props!=null){
						String v = props.get(CostantiDB.CONNETTORE_HTTP_IMPL);
						if(v!=null && !"".equals(v)){

							httpImpl = v.trim();

						}
					}
					
					opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(porteApplicativeHelper, transferMode, redirectMode, httpImpl);
					
					if(tokenPolicy==null && props!=null){
						String v = props.get(CostantiDB.CONNETTORE_TOKEN_POLICY);
						if(v!=null && !"".equals(v)){
							tokenPolicy = v;
							autenticazioneToken = true;
						}
					}

					autenticazioneHttp = porteApplicativeHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);

					if(autenticazioneApiKey==null || StringUtils.isEmpty(autenticazioneApiKey)) {
						for (int i = 0; i < oldConnis.sizePropertyList(); i++) {
							Property singlecp = oldCP.get(i);
							if (singlecp.getNome().equals(CostantiDB.CONNETTORE_APIKEY_HEADER)) {
								apiKeyHeader = singlecp.getValore();
							}
							else if (singlecp.getNome().equals(CostantiDB.CONNETTORE_APIKEY)) {
								apiKeyValue = singlecp.getValore();
							}
							else if (singlecp.getNome().equals(CostantiDB.CONNETTORE_APIKEY_APPID_HEADER)) {
								appIdHeader = singlecp.getValore();
							}
							else if (singlecp.getNome().equals(CostantiDB.CONNETTORE_APIKEY_APPID)) {
								appIdValue = singlecp.getValore();
							}
						}
						
						autenticazioneApiKey = porteApplicativeHelper.getAutenticazioneApiKey(autenticazioneApiKey, endpointtype, apiKeyValue);
						if(ServletUtils.isCheckBoxEnabled(autenticazioneApiKey)) {
							useOAS3Names = porteApplicativeHelper.isAutenticazioneApiKeyUseOAS3Names(apiKeyHeader, appIdHeader);
							useAppId = porteApplicativeHelper.isAutenticazioneApiKeyUseAppId(appIdValue);
						}
						else {
							apiKeyValue=null;
							apiKeyHeader=null;
							appIdHeader=null;
							appIdValue=null;
						}
					}
					
					for (int i = 0; i < oldConnis.sizePropertyList(); i++) {
						Property singlecp = oldCP.get(i);
						if (singlecp.getNome().equals(CostantiDB.CONNETTORE_HTTP_LOCATION) &&
							url == null) {
							url = singlecp.getValore();
						}
						if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_NOME) &&
							nomeCodaJms == null) {
							nomeCodaJms = singlecp.getValore();
						}
						if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_TIPO) &&
							tipoJms == null) {
							tipoJms = singlecp.getValore();
						}
						if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY) &&
							connfact == null) {
							connfact = singlecp.getValore();
						}
						if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_SEND_AS) &&
							tipoSendas == null) {
							tipoSendas = singlecp.getValore();
						}
						if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL) &&
							initcont == null) {
							initcont = singlecp.getValore();
						}
						if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG) &&
							urlpgk == null) {
							urlpgk = singlecp.getValore();
						}
						if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL) &&
							provurl == null) {
							provurl = singlecp.getValore();
						}
					}

					if (httpstipologia == null) {
						httpsurl = props.get(CostantiDB.CONNETTORE_HTTPS_LOCATION);
						httpstipologia = props.get(CostantiDB.CONNETTORE_HTTPS_SSL_TYPE);
						httpshostverifyS = props.get(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER);
						if(httpshostverifyS!=null){
							httpshostverify = Boolean.valueOf(httpshostverifyS);
						}
						httpsTrustVerifyCertS = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_ALL_CERTS);
						if(httpsTrustVerifyCertS!=null){
							httpsTrustVerifyCert = !Boolean.valueOf(httpsTrustVerifyCertS);
						}
						else {
							httpsTrustVerifyCert = true; // backward compatibility
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
						httpsKeyAlias = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_ALIAS);
						httpsTrustStoreCRLs = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_CRLS);
						httpsTrustStoreOCSPPolicy = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
						httpsKeyStoreBYOKPolicy = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_BYOK_POLICY);
						if (httpspathkey == null) {
							httpsstato = false;
							httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
						} else {
							httpsstato = true;
							if (httpspathkey.equals(httpspath) &&
									httpstipokey.equals(httpstipo) &&
									(
										(httpspwdkey!=null && httpspwdkey.equals(httpspwd))
										||
										(httpspwdkey==null && httpspwd==null)
									)
								)
								httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
							else
								httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;
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
						/**httpshostverifyS = "true";*/
						httpshostverify = true;
					}
					if(httpsTrustVerifyCertS==null || "".equals(httpsTrustVerifyCertS)){
						/**httpsTrustVerifyCertS = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS ? Costanti.CHECK_BOX_ENABLED_TRUE : Costanti.CHECK_BOX_DISABLED;*/
						httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
					}

					// file
					if(responseInputMode==null && props!=null){

						requestOutputFileName = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE);	
						requestOutputFileNamePermissions = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_PERMISSIONS);	
						requestOutputFileNameHeaders = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS);	
						requestOutputFileNameHeadersPermissions = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS_PERMISSIONS);	
						String v = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
						if(v!=null && !"".equals(v) &&
							("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) )
							){
							requestOutputParentDirCreateIfNotExists = Costanti.CHECK_BOX_ENABLED_TRUE;
						}					
						v = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE);
						if(v!=null && !"".equals(v) &&
							("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) )
							){
							requestOutputOverwriteIfExists = Costanti.CHECK_BOX_ENABLED_TRUE;
						}	

						v = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_MODE);
						if(v!=null && !"".equals(v) &&
							("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) )
							){
							responseInputMode = CostantiConfigurazione.ABILITATO.getValue();
						}
						if(CostantiConfigurazione.ABILITATO.getValue().equals(responseInputMode)){						
							responseInputFileName = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE);
							responseInputFileNameHeaders = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS);
							v = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_DELETE_AFTER_READ);
							if(v!=null && !"".equals(v) &&
								("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) )
								){
								responseInputDeleteAfterRead = Costanti.CHECK_BOX_ENABLED_TRUE;
							}						
							responseInputWaitTime = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);						
						}

					}

				} // init connettore from sa
				
				if(!integrationManagerEnabled && CostantiConfigurazione.ABILITATO.toString().equals(getmsg)) {
					// faccio vedere I.M. anche con interfaccia standard
					integrationManagerEnabled = true;
					if(erogazioneServizioApplicativoServerEnabled && ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(oldTipoSA)) {
						if(listaIdSAServer==null) {
							listaIdSAServer = new ArrayList<>();
						}
						boolean found = false;
						for (IDServizioApplicativoDB idServizioApplicativo : listaIdSAServer) {
							if(idServizioApplicativo.getNome().equals(idSADB.getNome())) {
								found = true;
								break;
							}
						}
						if(!found) {
							listaIdSAServer.add(idSADB);
						}
					}
				}


				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addConnettoriMultipliToDati(dati, TipoOperazione.CHANGE, beaBehaviourType, nomeSAConnettore,
						nomeConnettore, descrizioneConnettore, statoConnettore, behaviourConFiltri, filtriConnettore, visualizzaDatiGenerali, visualizzaDescrizione, visualizzaFiltri, visualizzaConnettore,
						oldPaSA);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta,idAsps, dati);

				dati = porteApplicativeHelper.addInformazioniGruppiAsHiddenToDati(TipoOperazione.CHANGE, dati, idTabP, idConnTab, accessoDaAPSParametro != null ? accessoDaAPSParametro : "", 
						connettoreAccessoGruppi, connettoreRegistro, connettoreAccessoListaConnettori);

				if(visualizzaSezioneConnettore) {

					porteApplicativeHelper.addEndPointToDati(dati,(idsil!=null ? idsil : oldSA.getId()+""),nomeservizioApplicativo,sbustamento,sbustamentoInformazioniProtocolloRichiesta,
							getmsg,getmsgUsername,getmsgPassword,true,
							invrifRichiesta,risprif,nomeProtocollo,true,true, true,
							parentPA,serviceBinding, accessoDaAPSParametro, erogazioneServizioApplicativoServerEnabled,
							null, false,
							integrationManagerEnabled,
							TipoOperazione.CHANGE, tipoCredenzialiSSLVerificaTuttiICampi, changepwd,
							postBackViaPost);

					dati = porteApplicativeHelper.addEndPointToDati(dati, serviceBinding, connettoreDebug, endpointtype, autenticazioneHttp, 
							null, //(porteApplicativeHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX , 
							url, nomeCodaJms,
							tipoJms, user,
							password, initcont, urlpgk,
							provurl, connfact, tipoSendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,TipoOperazione.CHANGE, 
							httpsurl, httpstipologia, httpshostverify, 
							httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey, 
							httpspwdprivatekey, httpsalgoritmokey,
							httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
							tipoconn, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CHANGE, 
							idPorta, // elem1 
							idsogg, // elem2
							idAsps, // elem3
							nomeSAConnettore, // elem4 
							pa.getNome(), // elem5 
							oldSA.getId()+"", // elem6 
							pIdConTab.getValue(), // elem7 
							null, // elem8 
							true,
							isConnettoreCustomUltimaImmagineSalvata, 
							proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
							tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
							opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
							requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
							requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
							autenticazioneToken,tokenPolicy, forcePDND, forceOAuth,
							listExtendedConnettore, forceEnableConnettore,
							protocollo,false,false, isApplicativiServerEnabled, erogazioneServizioApplicativoServerEnabled,
							erogazioneServizioApplicativoServer, ServiziApplicativiHelper.toArray(listaIdSAServer),
							autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
							connettoreStatusParams,
							postBackViaPost);
				} else {

					porteApplicativeHelper.addEndPointToDati(dati,(idsil!=null ? idsil : oldSA.getId()+""),nomeservizioApplicativo,sbustamento,sbustamentoInformazioniProtocolloRichiesta,
							getmsg,getmsgUsername,getmsgPassword,true,
							invrifRichiesta,risprif,nomeProtocollo,true,true, true,
							parentPA,serviceBinding, accessoDaAPSParametro, true,
							null, false,
							integrationManagerEnabled,
							TipoOperazione.CHANGE, tipoCredenzialiSSLVerificaTuttiICampi, changepwd,
							postBackViaPost);

					dati = porteApplicativeHelper.addEndPointSAServerToDatiAsHidden(dati, erogazioneServizioApplicativoServerEnabled, erogazioneServizioApplicativoServer);

					dati = porteApplicativeHelper.addEndPointToDatiAsHidden(dati, serviceBinding, connettoreDebug,
							endpointtype, autenticazioneHttp,
							url, nomeCodaJms, tipoJms,
							user, password, initcont, urlpgk,
							provurl, connfact, tipoSendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,TipoOperazione.CHANGE,
							httpsurl, httpstipologia, httpshostverify,
							httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey,
							httpspwdprivatekey, httpsalgoritmokey,
							httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
							tipoconn, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CHANGE, 
							idPorta, // elem1 
							idsogg, // elem2
							idAsps, // elem3
							nomeSAConnettore, // elem4 
							pa.getNome(), // elem5 
							oldSA.getId()+"", // elem6 
							pIdConTab.getValue(), // elem7 
							null, // elem8 
							proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
							tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
							opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
							requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
							requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
							autenticazioneToken,tokenPolicy,
							autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
							connettoreStatusParams);
				}

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI,
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi 
			boolean isOk = porteApplicativeHelper.connettoriMultipliCheckData(TipoOperazione.CHANGE, pa, beaBehaviourType, nomeSAConnettore,
					oldNomeConnettore, nomeConnettore, descrizioneConnettore, statoConnettore, filtriConnettore,  visualizzaDatiGenerali, visualizzaDescrizione, visualizzaFiltri, visualizzaConnettore,
					getmsg, getmsgUsername, getmsgPassword, oldSA);

			if(isOk) {
				isOk = porteApplicativeHelper.endPointCheckData(serviceBinding, protocollo, true,
						endpointtype, url, nomeCodaJms, tipoJms,
						user, password, initcont, urlpgk, provurl, connfact,
						tipoSendas, httpsurl, httpstipologia, httpshostverify,
						httpsTrustVerifyCert, httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
						httpskeystore, httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, 
						httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
						tipoconn,autenticazioneHttp,
						proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
						tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
						opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
						requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
						requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						autenticazioneToken,tokenPolicy,
						autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
						listExtendedConnettore,erogazioneServizioApplicativoServerEnabled,	erogazioneServizioApplicativoServer);
			}

			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addConnettoriMultipliToDati(dati, TipoOperazione.CHANGE, beaBehaviourType, nomeSAConnettore,
						nomeConnettore, descrizioneConnettore, statoConnettore, behaviourConFiltri, filtriConnettore,  visualizzaDatiGenerali, visualizzaDescrizione, visualizzaFiltri, visualizzaConnettore,
						oldPaSA);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta, idAsps, dati);

				dati = porteApplicativeHelper.addInformazioniGruppiAsHiddenToDati(TipoOperazione.CHANGE, dati, idTabP, idConnTab, accessoDaAPSParametro != null ? accessoDaAPSParametro : "", 
						connettoreAccessoGruppi, connettoreRegistro, connettoreAccessoListaConnettori);	

				if(visualizzaSezioneConnettore) {

					porteApplicativeHelper.addEndPointToDati(dati,(idsil!=null ? idsil : oldSA.getId()+""),nomeservizioApplicativo,sbustamento,sbustamentoInformazioniProtocolloRichiesta,
							getmsg,getmsgUsername,getmsgPassword,true,
							invrifRichiesta,risprif,nomeProtocollo,true,true, true,
							parentPA,serviceBinding, accessoDaAPSParametro, erogazioneServizioApplicativoServerEnabled,
							null, false,
							integrationManagerEnabled,
							TipoOperazione.CHANGE, tipoCredenzialiSSLVerificaTuttiICampi, changepwd,
							postBackViaPost);

					dati = porteApplicativeHelper.addEndPointToDati(dati, serviceBinding, connettoreDebug, endpointtype, autenticazioneHttp, 
							null, //(porteApplicativeHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX , 
							url, nomeCodaJms,
							tipoJms, user,
							password, initcont, urlpgk,
							provurl, connfact, tipoSendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,TipoOperazione.CHANGE, 
							httpsurl, httpstipologia, httpshostverify, 
							httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey, 
							httpspwdprivatekey, httpsalgoritmokey,
							httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
							tipoconn, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CHANGE,
							idPorta, // elem1 
							idsogg, // elem2
							idAsps, // elem3
							nomeSAConnettore, // elem4 
							pa.getNome(), // elem5 
							oldSA.getId()+"", // elem6 
							pIdConTab.getValue(), // elem7 
							null, // elem8 
							true,
							isConnettoreCustomUltimaImmagineSalvata, 
							proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
							tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
							opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
							requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
							requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
							autenticazioneToken,tokenPolicy, forcePDND, forceOAuth,
							listExtendedConnettore, forceEnableConnettore,
							protocollo,false,false, isApplicativiServerEnabled, erogazioneServizioApplicativoServerEnabled,
							erogazioneServizioApplicativoServer, ServiziApplicativiHelper.toArray(listaIdSAServer),
							autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
							connettoreStatusParams,
							postBackViaPost);
				} else {

					porteApplicativeHelper.addEndPointToDati(dati,(idsil!=null ? idsil : oldSA.getId()+""),nomeservizioApplicativo,sbustamento,sbustamentoInformazioniProtocolloRichiesta,
							getmsg,getmsgUsername,getmsgPassword,true,
							invrifRichiesta,risprif,nomeProtocollo,true,true, true,
							parentPA,serviceBinding, accessoDaAPSParametro, true,
							null, false,
							integrationManagerEnabled,
							TipoOperazione.CHANGE, tipoCredenzialiSSLVerificaTuttiICampi, changepwd,
							postBackViaPost);

					dati = porteApplicativeHelper.addEndPointSAServerToDatiAsHidden(dati, erogazioneServizioApplicativoServerEnabled, erogazioneServizioApplicativoServer);

					dati = porteApplicativeHelper.addEndPointToDatiAsHidden(dati, serviceBinding, connettoreDebug,
							endpointtype, autenticazioneHttp,
							url, nomeCodaJms, tipoJms,
							user, password, initcont, urlpgk,
							provurl, connfact, tipoSendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,TipoOperazione.CHANGE,
							httpsurl, httpstipologia, httpshostverify,
							httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey,
							httpspwdprivatekey, httpsalgoritmokey,
							httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
							tipoconn, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CHANGE,
							idPorta, // elem1 
							idsogg, // elem2
							idAsps, // elem3
							nomeSAConnettore, // elem4 
							pa.getNome(), // elem5 
							oldSA.getId()+"", // elem6 
							pIdConTab.getValue(), // elem7 
							null, // elem8 
							proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
							tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
							opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
							requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
							requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
							autenticazioneToken,tokenPolicy,
							autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
							connettoreStatusParams);
				}

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI, 
						ForwardParams.CHANGE());
			}

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			List<Object> listaOggettiDaCreare = new ArrayList<>();
			List<Object> listaOggettiDaModificare = new ArrayList<>();
			List<Object> listaOggettiDaEliminare = new ArrayList<>();

			// valora iniziale della configurazione
			PortaApplicativaServizioApplicativo paSA = null;
			for (PortaApplicativaServizioApplicativo paSATmp : pa.getServizioApplicativoList()) {
				if(paSATmp.getNome().equals(nomeSAConnettore)) {
					paSA = paSATmp;					
				}
			}

			PortaApplicativaServizioApplicativoConnettore datiConnettore = paSA.getDatiConnettore();

			if(datiConnettore == null) { // succede solo se e' la prima volta che modifico la configurazione di default
				datiConnettore = new PortaApplicativaServizioApplicativoConnettore();
				datiConnettore.setNome(CostantiConfigurazione.NOME_CONNETTORE_DEFAULT);
			}

			if(datiConnettore.getProprietaOggetto()==null) {
				datiConnettore.setProprietaOggetto(new ProprietaOggetto());
			}
			datiConnettore.getProprietaOggetto().setUtenteUltimaModifica(userLogin);
			datiConnettore.getProprietaOggetto().setDataUltimaModifica(DateManager.getDate());
			
			paSA.setDatiConnettore(datiConnettore);

			boolean isDefault = true;
			if(	datiConnettore != null ) { 
				isDefault = !datiConnettore.isNotifica();
			}

			String nomeConnettoreChangeList = null;
			
			if(visualizzaSezioneDatiGenerali) {
				datiConnettore.setNome(nomeConnettore);

				nomeConnettoreChangeList = nomeConnettore;
				
				if(!nomeConnettore.equals(oldNomeConnettore) &&
					
					(pa.getBehaviour() != null) 
					){

					TipoBehaviour behaviourType = TipoBehaviour.toEnumConstant(pa.getBehaviour().getNome());

					boolean consegnaCondizionale = false;
					if(behaviourType.equals(TipoBehaviour.CONSEGNA_MULTIPLA)
							|| behaviourType.equals(TipoBehaviour.CONSEGNA_CON_NOTIFICHE)
							|| behaviourType.equals(TipoBehaviour.CONSEGNA_CONDIZIONALE)
							|| behaviourType.equals(TipoBehaviour.CONSEGNA_LOAD_BALANCE)) {
						consegnaCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.isConfigurazioneCondizionale(pa, ControlStationCore.getLog());

						if( behaviourType.equals(TipoBehaviour.CONSEGNA_CON_NOTIFICHE)) {
							org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneMultiDeliver configurazioneMultiDeliver = 
									org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils.read(pa);

							if(configurazioneMultiDeliver != null &&
								configurazioneMultiDeliver.getTransazioneSincrona_nomeConnettore() != null &&
									configurazioneMultiDeliver.getTransazioneSincrona_nomeConnettore().equals(oldNomeConnettore)) {
								// modifica riferimento
								configurazioneMultiDeliver.setTransazioneSincrona_nomeConnettore(nomeConnettore);
								boolean differenziazioneConsegnaDaNotifiche = TipoBehaviour.CONSEGNA_CON_NOTIFICHE.equals(behaviourType);
								org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils.save(pa, configurazioneMultiDeliver, differenziazioneConsegnaDaNotifiche);
							}
						}

						if(consegnaCondizionale) {
							boolean save = false;
							org.openspcoop2.pdd.core.behaviour.conditional.ConfigurazioneCondizionale configurazioneCondizionale = 
									org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.read(pa, ControlStationCore.getLog());

							for (String nomeRegola : configurazioneCondizionale.getRegoleOrdinate()) {
								ConfigurazioneSelettoreCondizioneRegola regola = configurazioneCondizionale.getRegola(nomeRegola);
								if(!configurazioneCondizionale.isByFilter() &&
									regola.getStaticInfo() != null &&
										regola.getStaticInfo().equals(nomeConnettore)) {
									regola.setStaticInfo(nomeConnettore);
								}
							}

							org.openspcoop2.pdd.core.behaviour.conditional.IdentificazioneFallitaConfigurazione condizioneNonIdentificata =
									configurazioneCondizionale.getCondizioneNonIdentificata();

							if(condizioneNonIdentificata.getNomeConnettore() != null &&
								condizioneNonIdentificata.getNomeConnettore().equals(oldNomeConnettore)) {
								// modifica riferimento
								condizioneNonIdentificata.setNomeConnettore(nomeConnettore);
								save = true;
							}

							org.openspcoop2.pdd.core.behaviour.conditional.IdentificazioneFallitaConfigurazione connettoreNonTrovato = 
									configurazioneCondizionale.getNessunConnettoreTrovato();

							if(connettoreNonTrovato.getNomeConnettore() != null &&
								connettoreNonTrovato.getNomeConnettore().equals(oldNomeConnettore)) {
								// modifica riferimento
								connettoreNonTrovato.setNomeConnettore(nomeConnettore);
								save = true;
							}

							if(save) {
								org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.save(pa, configurazioneCondizionale);
							}
						}
					}
				}
			}
			else {
				nomeConnettoreChangeList = datiConnettore.getNome();
				if(nomeConnettoreChangeList==null) {
					nomeConnettoreChangeList = CostantiConfigurazione.NOME_CONNETTORE_DEFAULT;
				}
			}

			if(visualizzaSezioneDescrizione)
				datiConnettore.setDescrizione(descrizioneConnettore);

			if(visualizzaSezioneFiltri) {
				
				datiConnettore.getFiltroList().clear();
				
				if(StringUtils.isNotEmpty(filtriConnettore)) {
					
					List<String> filtri = Arrays.asList(filtriConnettore.split(","));

					for (String filtro : filtri) {
						datiConnettore.addFiltro(filtro);
					}
				}
			}

			boolean secret = false;
			String secretPassword  = null;
			String secretUser = null;
			boolean secretAppId = false;
			
			if(visualizzaSezioneConnettore) {
				// la modifica del connettore  
				idSA = new IDServizioApplicativo();
				idSA.setNome(paSA.getNome());
				idSA.setIdSoggettoProprietario(new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
				ServizioApplicativo sa = saCore.getServizioApplicativo(idSA );
				invocazionePorta = sa.getInvocazionePorta();
				InvocazioneServizio is = sa.getInvocazioneServizio();
				InvocazioneCredenziali cis = is.getCredenziali();
				Connettore connis = is.getConnettore();

				// ho scelto un SA server
				// 1. cambio un SA server con un altro
				// 2. non cambio 
				// 3. Sostituzione di un connettore definito nella form con uno SA server

				if(erogazioneServizioApplicativoServerEnabled) {
					if(!erogazioneServizioApplicativoServer.equals(sa.getNome())) {
	
						if(!isDefault) {
							
							paSA.setNome(erogazioneServizioApplicativoServer);
							
							// se non sono il connettore di default imposto il nome dell SA Server e cancello il SA collegato al connettore definito nella form
							if(!ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo()))
								listaOggettiDaEliminare.add(sa);
							
						} else {

							// se sono il connettore di defautl allora devo comportarmi come il connettore nel caso non multiplo
							// prelevo l'associazione con il vecchio servizio applicativo
							PortaApplicativaServizioApplicativo paSAtmp = null;
							for (PortaApplicativaServizioApplicativo paSA2 : pa.getServizioApplicativoList()) {
								if(paSA2.getNome().equals(sa.getNome())) {
									paSAtmp = paSA2;
									break;
								}
							}
	
							if(paSAtmp!= null) {
								// se ho modificato il server che sto utilizzando lo rimuovo
								if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo())){
									pa.getServizioApplicativoList().remove(paSAtmp); 	
								} else {
									// SA di default da conservare
									pa.getServizioApplicativoList().remove(paSAtmp);
									pa.setServizioApplicativoDefault(sa.getNome());
								}
							}

							// nuovo SA da aggiungere
							paSA.setNome(erogazioneServizioApplicativoServer);
							pa.getServizioApplicativoList().add(paSA);

							// aggiorno eventuali connettori associati alla conigurazioni non default
							porteApplicativeHelper.impostaSAServerAlleConfigurazioniCheUsanoConnettoreDelMappingDiDefault(idAsps, erogazioneServizioApplicativoServer, pa, sa, listaOggettiDaModificare);
						}
					} 
				}else {
					// ho salvato un connettore form
					// 1. modifico il vecchio connettore form
					// 2. avevo associato un SAServer lo disassocio e inserisco quello Form

					if(!isDefault) {
						// in questo caso devo solo creare il nuovo connettore.
					} else {
						// se sono nella configurazione di default devo ripristinare la situazione precedente 
						if(pa.getServizioApplicativoDefault() != null) {

							String oldServizioApplicativoDefault = pa.getServizioApplicativoDefault();

							// prelevo l'associazione con il vecchio servizio applicativo server
							PortaApplicativaServizioApplicativo paSAtmp = null;
							for (PortaApplicativaServizioApplicativo paSA2 : pa.getServizioApplicativoList()) {
								if(paSA2.getNome().equals(sa.getNome())) {
									paSAtmp = paSA2;
									break;
								}
							}

							if(paSAtmp!= null &&
								// se ho modificato il server che sto utilizzando lo rimuovo
								(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo()))
								){
								pa.getServizioApplicativoList().remove(paSAtmp); 	
							}

							paSA.setNome(oldServizioApplicativoDefault);
							pa.getServizioApplicativoList().add(paSA);
							pa.setServizioApplicativoDefault(null);

							porteApplicativeHelper.impostaSADefaultAlleConfigurazioniCheUsanoConnettoreDelMappingDiDefault(idAsps, pa, sa, listaOggettiDaModificare);

							// rileggo la vecchia configurazione dal db di default
							idSA = new IDServizioApplicativo();
							idSA.setNome(oldServizioApplicativoDefault);
							IDSoggetto idSoggettoProprietario = new IDSoggetto();
							idSoggettoProprietario.setTipo(pa.getTipoSoggettoProprietario());
							idSoggettoProprietario.setNome(pa.getNomeSoggettoProprietario());
							idSA.setIdSoggettoProprietario(idSoggettoProprietario );
							sa = saCore.getServizioApplicativo(idSA);
							invocazionePorta = sa.getInvocazionePorta();
							is = sa.getInvocazioneServizio();
							cis = is.getCredenziali();
							connis = is.getConnettore();
						}


					}

					if(!ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo())){
						// Modifico i dati del servizioApplicativo nel db
						if(sbustamento==null){
							is.setSbustamentoSoap(CostantiConfigurazione.DISABILITATO);
						}else{
							is.setSbustamentoSoap(StatoFunzionalita.toEnumConstant(sbustamento));
						}
						if(sbustamentoInformazioniProtocolloRichiesta==null){
							is.setSbustamentoInformazioniProtocollo(CostantiConfigurazione.ABILITATO);
						}else{
							is.setSbustamentoInformazioniProtocollo(StatoFunzionalita.toEnumConstant(sbustamentoInformazioniProtocolloRichiesta));
						}
						is.setGetMessage(StatoFunzionalita.toEnumConstant(getmsg));
						is.setInvioPerRiferimento(StatoFunzionalita.toEnumConstant(invrifRichiesta));
						is.setRispostaPerRiferimento(StatoFunzionalita.toEnumConstant(risprif));
						if (tipoauthRichiesta!=null && tipoauthRichiesta.equals(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString())) {
							if (cis == null) {
								cis = new InvocazioneCredenziali();
							}
							cis.setUser(user);
							cis.setPassword(password);
							is.setCredenziali(cis);
							is.setAutenticazione(InvocazioneServizioTipoAutenticazione.BASIC);
						}
						else if(endpointtype.equals(TipiConnettore.JMS.toString())){
							if(user!=null && password!=null){
								if (cis == null) {
									cis = new InvocazioneCredenziali();
								}
								cis.setUser(user);
								cis.setPassword(password);
							}
							is.setCredenziali(cis);
							is.setAutenticazione(InvocazioneServizioTipoAutenticazione.BASIC);
						}
						else {
							is.setCredenziali(null);
							is.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
						}
						String oldConnT = connis.getTipo();
						if ((connis.getCustom()!=null && connis.getCustom()) && 
								!connis.getTipo().equals(TipiConnettore.HTTPS.toString()) && 
								!connis.getTipo().equals(TipiConnettore.FILE.toString()) &&
								!oldConnis.getTipo().equals(TipiConnettore.STATUS.toString()))
							oldConnT = TipiConnettore.CUSTOM.toString();
						porteApplicativeHelper.fillConnettore(connis, connettoreDebug, endpointtype, oldConnT, tipoconn, url,
								nomeCodaJms, tipoJms, user, password,
								initcont, urlpgk, provurl, connfact,
								tipoSendas, httpsurl, httpstipologia, httpshostverify, 
								httpsTrustVerifyCert, httpspath, httpstipo,
								httpspwd, httpsalgoritmo, httpsstato,
								httpskeystore, httpspwdprivatekeytrust,
								httpspathkey, httpstipokey,
								httpspwdkey, httpspwdprivatekey,
								httpsalgoritmokey,
								httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
								proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
								tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
								opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
								requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
								requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
								responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
								tokenPolicy,
								apiKeyHeader, apiKeyValue, appIdHeader, appIdValue, 
								connettoreStatusParams,
								listExtendedConnettore);
						is.setConnettore(connis);
						sa.setInvocazioneServizio(is);

						if(StatoFunzionalita.ABILITATO.equals(is.getGetMessage()) ||
								!TipiConnettore.DISABILITATO.toString().equals(endpointtype)){
							sa.setTipologiaErogazione(TipologiaErogazione.TRASPARENTE.getValue());
						}
						else{
							sa.setTipologiaErogazione(TipologiaErogazione.DISABILITATO.getValue());
						}

						// rif bug#45
						// se il connettore e' disabilitato oppure il Salvataggio in MessageBox e'
						// disabilitato
						// bisogna controllare che il servizio applicativo non sia in uso in
						// porte applicative
						StringBuilder inUsoMessage = new StringBuilder();
						ServiziApplicativiUtilities.checkStatoConnettore(saCore, sa, connis, inUsoMessage, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						if(inUsoMessage.length()>0) {
							pd.setMessage(inUsoMessage.toString());
							ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
							return ServletUtils.getStrutsForwardGeneralError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI, ForwardParams.CHANGE());
						}

						if(CostantiConfigurazione.ABILITATO.toString().equals(getmsg)) {
							boolean found = false;
							boolean encryptOldPlainPwd = false;
							if(invocazionePorta!=null && invocazionePorta.sizeCredenzialiList()>0) {
								for (int i = 0; i < invocazionePorta.sizeCredenzialiList(); i++) {
									Credenziali c = invocazionePorta.getCredenziali(i);
									if(CredenzialeTipo.BASIC.equals(c.getTipo())) {
										c.setUser(getmsgUsername);
										c.setPassword(getmsgPassword);
										found = true;
										
										encryptOldPlainPwd = !c.isCertificateStrictVerification() && saCore.isApplicativiPasswordEncryptEnabled();
										
										if(ServletUtils.isCheckBoxEnabled(changepwd)) {
											c.setCertificateStrictVerification(false); // se è abilitata la cifratura, verrà impostata a true nel perform update
											if(saCore.isApplicativiPasswordEncryptEnabled()) {
												secret = true;
											}
										}
										else if(encryptOldPlainPwd) {
											secret = true;
										}
										else {
											c.setCertificateStrictVerification(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi));
										}
										
										if(secret) {
											secretUser = c.getUser();
											secretPassword = c.getPassword();
											secretAppId = c.isAppId();
										}
									}
								}
							}
							if(!found) {
								if(invocazionePorta==null) {
									sa.setInvocazionePorta(new InvocazionePorta());
									invocazionePorta = sa.getInvocazionePorta();
								}
								Credenziali c = new Credenziali();
								c.setTipo(CredenzialeTipo.BASIC);
								c.setUser(getmsgUsername);
								c.setPassword(getmsgPassword);
								
								c.setCertificateStrictVerification(false); // se è abilitata la cifratura, verrà impostata a true nel perform update
								if(saCore.isApplicativiPasswordEncryptEnabled()) {
									secret = true;
								}
								
								if(secret) {
									secretUser = c.getUser();
									secretPassword = c.getPassword();
									secretAppId = c.isAppId();
								}
								
								invocazionePorta.addCredenziali(c);
							}
						}
						else {
							// Fix: altrimenti rimaneva assegnate le credenziali quando si disabilitava l'integration manager
							if(!porteApplicativeHelper.isModalitaCompleta() &&
								invocazionePorta!=null) {
								while (invocazionePorta.sizeCredenzialiList()>0) {
									invocazionePorta.removeCredenziali(0);
								}
							}
						}

						listaOggettiDaModificare.add(sa);
					} else {
						// 1. Creo connettore
						Connettore connettore = null;

						// Connettore
						connettore = new Connettore();
						if (endpointtype.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM))
							connettore.setTipo(tipoconn);
						else
							connettore.setTipo(endpointtype);

						porteApplicativeHelper.fillConnettore(connettore, connettoreDebug, endpointtype, endpointtype, tipoconn, url,
								nomeCodaJms, tipoJms, user, password,
								initcont, urlpgk, provurl, connfact,
								tipoSendas, httpsurl, httpstipologia, httpshostverify, 
								httpsTrustVerifyCert, httpspath, httpstipo,
								httpspwd, httpsalgoritmo, httpsstato,
								httpskeystore, httpspwdprivatekeytrust,
								httpspathkey, httpstipokey,
								httpspwdkey, httpspwdprivatekey,
								httpsalgoritmokey,
								httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
								proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
								tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
								opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
								requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
								requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
								responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
								tokenPolicy,
								apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,  
								connettoreStatusParams,
								listExtendedConnettore);

						// creare un servizio applicativo
						String nomeServizioApplicativoErogatore = pa.getNome() + PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SAX_PREFIX + 
								porteApplicativeHelper.getIdxNuovoConnettoreMultiplo(pa);

						ServizioApplicativo nuovoSA = new ServizioApplicativo();
						nuovoSA.setNome(nomeServizioApplicativoErogatore);
						nuovoSA.setTipologiaFruizione(TipologiaFruizione.DISABILITATO.getValue());
						nuovoSA.setTipologiaErogazione(TipologiaErogazione.TRASPARENTE.getValue());
						long soggInt = Long.parseLong(idsogg);
						nuovoSA.setIdSoggetto(soggInt);
						nuovoSA.setTipoSoggettoProprietario(pa.getTipoSoggettoProprietario());
						nuovoSA.setNomeSoggettoProprietario(pa.getNomeSoggettoProprietario());

						RispostaAsincrona rispostaAsinc = new RispostaAsincrona();
						rispostaAsinc.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
						rispostaAsinc.setGetMessage(CostantiConfigurazione.DISABILITATO);
						nuovoSA.setRispostaAsincrona(rispostaAsinc);

						InvocazioneServizio nuovaIS = new InvocazioneServizio();
						nuovaIS.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
						nuovaIS.setGetMessage(CostantiConfigurazione.DISABILITATO);
						nuovaIS.setConnettore(connettore);
						nuovoSA.setInvocazioneServizio(nuovaIS);

						InvocazioneCredenziali nuovaCIS = null;

						// Modifico i dati del servizioApplicativo nel db
						if(sbustamento==null){
							nuovaIS.setSbustamentoSoap(CostantiConfigurazione.DISABILITATO);
						}else{
							nuovaIS.setSbustamentoSoap(StatoFunzionalita.toEnumConstant(sbustamento));
						}
						if(sbustamentoInformazioniProtocolloRichiesta==null){
							nuovaIS.setSbustamentoInformazioniProtocollo(CostantiConfigurazione.ABILITATO);
						}else{
							nuovaIS.setSbustamentoInformazioniProtocollo(StatoFunzionalita.toEnumConstant(sbustamentoInformazioniProtocolloRichiesta));
						}
						nuovaIS.setGetMessage(StatoFunzionalita.toEnumConstant(getmsg));
						nuovaIS.setInvioPerRiferimento(StatoFunzionalita.toEnumConstant(invrifRichiesta));
						nuovaIS.setRispostaPerRiferimento(StatoFunzionalita.toEnumConstant(risprif));
						if (tipoauthRichiesta!=null && tipoauthRichiesta.equals(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString())) {
							if (nuovaCIS == null) {
								nuovaCIS = new InvocazioneCredenziali();
							}
							nuovaCIS.setUser(user);
							nuovaCIS.setPassword(password);
							nuovaIS.setCredenziali(nuovaCIS);
							nuovaIS.setAutenticazione(InvocazioneServizioTipoAutenticazione.BASIC);
						}
						else if(endpointtype.equals(TipiConnettore.JMS.toString())){
							if(user!=null && password!=null){
								if (nuovaCIS == null) {
									nuovaCIS = new InvocazioneCredenziali();
								}
								nuovaCIS.setUser(user);
								nuovaCIS.setPassword(password);
							}
							nuovaIS.setCredenziali(nuovaCIS);
							nuovaIS.setAutenticazione(InvocazioneServizioTipoAutenticazione.BASIC);
						}
						else {
							nuovaIS.setCredenziali(null);
							nuovaIS.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
						}

						if(StatoFunzionalita.ABILITATO.equals(nuovaIS.getGetMessage()) ||
								!TipiConnettore.DISABILITATO.toString().equals(endpointtype)){
							nuovoSA.setTipologiaErogazione(TipologiaErogazione.TRASPARENTE.getValue());
						}
						else{
							nuovoSA.setTipologiaErogazione(TipologiaErogazione.DISABILITATO.getValue());
						}

						listaOggettiDaCreare.add(nuovoSA);
						paSA.setNome(nomeServizioApplicativoErogatore);
					}
				}
			}


			listaOggettiDaModificare.add(pa);

			if(!listaOggettiDaCreare.isEmpty())
				porteApplicativeCore.performCreateOperation(userLogin, porteApplicativeHelper.smista(), listaOggettiDaCreare.toArray(new Object[listaOggettiDaCreare.size()]));

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), listaOggettiDaModificare.toArray(new Object[listaOggettiDaModificare.size()]));

			// L'eliminazione degli oggetti non più necessari deve essere effettuata dopo l'update, altrimenti si ha un errore di foreign key violata
			if(!listaOggettiDaEliminare.isEmpty())
				porteApplicativeCore.performDeleteOperation(userLogin, porteApplicativeHelper.smista(), listaOggettiDaEliminare.toArray(new Object[listaOggettiDaEliminare.size()]));
			
			ServletUtils.removeRisultatiRicercaFromSession(request, session, Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI);
			
			// Messaggio 'Please Copy'
			if(secret) {
				porteApplicativeHelper.setSecretPleaseCopy(secretPassword, secretUser, secretAppId, ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC, OggettoDialogEnum.CONNETTORE_MULTIPLO, nomeConnettore);
			}
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI;

			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);

			// ricarico la configurazione
			pa = porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			IDSoggetto idSoggettoProprietario = new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario());
			List<PortaApplicativaServizioApplicativo> listaFiltrata = porteApplicativeHelper.applicaFiltriRicercaConnettoriMultipli(ricerca, idLista, pa.getServizioApplicativoList(), idSoggettoProprietario);
			
			porteApplicativeHelper.preparePorteAppConnettoriMultipliList_fromChangeConnettore(ricerca, listaFiltrata, pa, 
					nomeConnettoreChangeList);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI, 
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI,
					ForwardParams.CHANGE());
		}  
	}
}
