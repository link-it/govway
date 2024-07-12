/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.sa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

/**
 * servizioApplicativoEndPointRisposta
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ServiziApplicativiEndPointRispostaAsincrona extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		try {
			ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(request, pd, session);
			
			// Preparo il menu
			saHelper.makeMenu();
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
			Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, session, request);
			if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			boolean useIdSogg = (parentSA!=null && parentSA.intValue() == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO);
			
			String nomeservizioApplicativo = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO);
			String idsil = saHelper.getParametroLong(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);
			int idSilInt = Integer.parseInt(idsil);
			String sbustamento = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
			String sbustamentoInformazioniProtocolloRichiesta = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
			String getmsg = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
			String getmsgUsername = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
			String getmsgPassword = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			
			String invrifRichiesta = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RICHIESTA);
			
			String risprif = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);
			
			String tipoauthRichiesta = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_TIPO_AUTENTICAZIONE);
			
			String provider = saHelper.getParametroLong(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			if(provider == null){
				provider = "";
			} 
			String idPorta = saHelper.getParametroLong(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_PORTA);
			if(idPorta == null)
				idPorta = "";
			
			String idAsps = saHelper.getParametroLong(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String endpointtype = saHelper.readEndPointType();
			String tipoconn = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			String autenticazioneHttp = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			String user = null;
			String password = null;
			
			String connettoreDebug = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);

			// token policy
			String autenticazioneTokenS = saHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			boolean autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			String tokenPolicy = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
			boolean forcePDND = false;
			boolean forceOAuth = false;
						
			// proxy
			String proxyEnabled = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxyHostname = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxyPort = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxyUsername = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxyPassword = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);
				
			// tempi risposta
			String tempiRispostaEnabled = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			String tempiRispostaConnectionTimeout = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			String tempiRispostaReadTimeout = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			String tempiRispostaTempoMedioRisposta = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
			
			// opzioni avanzate
			String transferMode = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transferModeChunkSize = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirectMode = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirectMaxHop = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(saHelper, transferMode, redirectMode);
			
			// http
			String url = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// api key
			String autenticazioneApiKey = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_API_KEY);
			String apiKeyHeader = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_HEADER);
			if(apiKeyHeader==null || StringUtils.isEmpty(apiKeyHeader)) {
				apiKeyHeader = CostantiConnettori.DEFAULT_HEADER_API_KEY;
			}
			String apiKeyValue = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_VALUE);
			String appIdHeader = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_APP_ID_HEADER);
			if(appIdHeader==null || StringUtils.isEmpty(appIdHeader)) {
				appIdHeader = CostantiConnettori.DEFAULT_HEADER_APP_ID;
			}
			String appIdValue = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_APP_ID_VALUE);
			String useOAS3NamesTmp = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_NOMI_OAS);
			boolean useOAS3Names=true;
			if(useOAS3NamesTmp!=null && StringUtils.isNotEmpty(useOAS3NamesTmp)) {
				useOAS3Names = ServletUtils.isCheckBoxEnabled(useOAS3NamesTmp);
			}
			else {
				useOAS3Names = saHelper.isAutenticazioneApiKeyUseOAS3Names(apiKeyHeader, appIdHeader);
			}
			String useAppIdTmp = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_USE_APP_ID);
			boolean useAppId=false;
			if(useAppIdTmp!=null && StringUtils.isNotEmpty(useAppIdTmp)) {
				useAppId = ServletUtils.isCheckBoxEnabled(useAppIdTmp);
			}
			else {
				useAppId = saHelper.isAutenticazioneApiKeyUseAppId(appIdValue);
			}
			
			// jms
			String nome = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipo = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String initcont = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String sendas = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(endpointtype)){
				user = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				password = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}
			
			// https
			String httpsurl = url;
			String httpstipologia = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
			String httpshostverifyS = saHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			boolean httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
			String httpsTrustVerifyCertS = saHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS );
			boolean httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
			String httpspath = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			String httpstipo = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = saHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			boolean httpsstato = ServletUtils.isCheckBoxEnabled(httpsstatoS);
			String httpskeystore = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			String httpsKeyAlias = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
			String httpsTrustStoreCRLs = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
			String httpsTrustStoreOCSPPolicy = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
			String httpsKeyStoreBYOKPolicy = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_BYOK_POLICY);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// file
			String requestOutputFileName = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			String requestOutputFileNamePermissions = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS);
			String requestOutputFileNameHeaders = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			String requestOutputFileNameHeadersPermissions = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_PERMISSIONS);
			String requestOutputParentDirCreateIfNotExists = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			String requestOutputOverwriteIfExists = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			String responseInputMode = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			String responseInputFileName = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			String responseInputFileNameHeaders = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			String responseInputDeleteAfterRead = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			String responseInputWaitTime = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
			
			
			String tipologia = ServletUtils.getObjectFromSession(request, session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			@SuppressWarnings("unused")
			boolean gestioneErogatori = false;
			if(tipologia!=null &&
				AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
				gestioneErogatori = true;
			}

			boolean accessoDaListaAPS = false;
			String accessoDaAPSParametro = null;
			// nell'erogazione vale sempre
			accessoDaAPSParametro = saHelper.getParametroBoolean(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			if(Costanti.CHECK_BOX_ENABLED_TRUE.equals(accessoDaAPSParametro)) {
				accessoDaListaAPS = true;
			}
			
			boolean erogazioneServizioApplicativoServerEnabled = false;
			
			boolean postBackViaPost = true;
			
			String superUser = ServletUtils.getUserLoginFromSession(session);

			// Prendo il sil
			ServiziApplicativiCore saCore = new ServiziApplicativiCore();
			ServizioApplicativo sa = saCore.getServizioApplicativo(idSilInt);
			SoggettiCore soggettiCore = new SoggettiCore(saCore);
			PddCore pddCore = new PddCore(saCore);
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(sa.getTipoSoggettoProprietario());
			InvocazionePorta invocazionePorta = sa.getInvocazionePorta();
			RispostaAsincrona ra = sa.getRispostaAsincrona();
			if(ra==null) {
				throw new CoreException("ServizioApplicativo con id '"+idSilInt+"' senza RispostaAsincrona");
			}
			InvocazioneCredenziali cra = ra.getCredenziali();
			Connettore connra = ra.getConnettore();
			if(connra==null) {
				throw new CoreException("ServizioApplicativo con id '"+idSilInt+"' senza connettore in RispostaAsincrona");
			}
			List<Property> cp = connra.getPropertyList();
			
			boolean forceEnabled = false;
			if(parentSA!=null && (parentSA.intValue() == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_CONFIGURAZIONE)) {
				if(
						//va visualizzato comunque se già configurato:!saHelper.isModalitaStandard() && 
						( (getmsg!=null && CostantiConfigurazione.ABILITATO.toString().equals(getmsg)) 
								||
						  (getmsg==null && ra!=null && ra.getGetMessage()!=null && StatoFunzionalita.ABILITATO.equals(ra.getGetMessage()))
						)
					) {
					forceEnabled = false;
				}
				else {
					forceEnabled = true;
					if(endpointtype==null) {
						if( connra==null || connra.getTipo()==null || TipiConnettore.DISABILITATO.getNome().equals(connra.getTipo()) ) {
							endpointtype = TipiConnettore.HTTP.getNome();
						}
					}
					else if(TipiConnettore.DISABILITATO.getNome().equals(endpointtype)) {
						endpointtype = TipiConnettore.HTTP.getNome();
					}
				}
			}
			
			String nomeProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(sa.getTipoSoggettoProprietario());
			
			long soggLong = -1;
			// se ho fatto la add 
			if(useIdSogg &&
				provider != null && !provider.equals("")){
				soggLong = Long.parseLong(provider);
			}

			Boolean isConnettoreCustomUltimaImmagineSalvata = connra.getCustom();
			
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(connra, ConnettoreServletType.SERVIZIO_APPLICATIVO_RISPOSTA_ASINCRONA, saHelper, 
							(endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro
			
			
			List<Parameter> lstParm = saHelper.getTitoloSA(parentSA, provider, idAsps, idPorta);
			if(!saHelper.isModalitaCompleta()) {
				IDSoggetto idSoggettoProprietario = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
				Soggetto soggettoProprietario = soggettiCore.getSoggettoRegistro(idSoggettoProprietario);
				String dominio = pddCore.isPddEsterna(soggettoProprietario.getPortaDominio()) ? SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE : SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE;
			
				List<Parameter> parametersServletSAChange = new ArrayList<>();
				Parameter pIdSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId()+"");
				parametersServletSAChange.add(pIdSA);
				Parameter pIdSoggettoSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto()+"");
				parametersServletSAChange.add(pIdSoggettoSA);
				if(dominio != null) {
					Parameter pDominio = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO, dominio);
					parametersServletSAChange.add(pDominio);
				}
				
				lstParm.add(new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])));
			}
			
			boolean integrationManagerEnabled = !saHelper.isModalitaStandard() && saCore.isIntegrationManagerEnabled();
			
			ServiceBinding serviceBinding = null;
			String labelPerPorta = null;
			if(parentSA!=null && (parentSA.intValue() == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_CONFIGURAZIONE)) {
				
				PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(saCore);
				PortaApplicativa pa = null;		
				
				if(accessoDaListaAPS) {
					labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE;
				}
				else {
					pa = porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta)); 
					labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DI,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE,
							pa);
				}
				
				AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(soggettiCore);
				AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
				AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);
				AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
				serviceBinding = apcCore.toMessageServiceBinding(apc.getServiceBinding());
				
				boolean isSoapOneWay = false;
				if(pa!=null) {
					MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = porteApplicativeCore.getMappingErogazionePortaApplicativa(pa);
					isSoapOneWay = saHelper.isSoapOneWay(pa, mappingErogazionePortaApplicativa, asps, apc, serviceBinding);
				}
				integrationManagerEnabled = integrationManagerEnabled && isSoapOneWay;
			}
			else {
				labelPerPorta = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_ASINCRONA_DI+nomeservizioApplicativo;
				if(!saHelper.isModalitaCompleta()) {
					labelPerPorta = ServiziApplicativiCostanti.LABEL_RISPOSTA_ASINCRONA;
				}
			}
			
			if(accessoDaListaAPS) {
				lstParm.remove(lstParm.size()-1);
			}
			lstParm.add(new Parameter(labelPerPorta,null));
			
			// Se nomehid = null, devo visualizzare la pagina per la
			// modifica dati
			if(saHelper.isEditModeInProgress()){
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm);
				
				// Prendo i dati dal db solo se non sono stati passati
				if (sbustamento == null &&
					ra.getSbustamentoSoap()!=null) {
					sbustamento = ra.getSbustamentoSoap().toString();
				}
				if (sbustamentoInformazioniProtocolloRichiesta == null &&
					ra.getSbustamentoInformazioniProtocollo()!=null) {
					sbustamentoInformazioniProtocolloRichiesta = ra.getSbustamentoInformazioniProtocollo().toString();
				}
				if (getmsg == null &&
					ra.getGetMessage()!=null) {
					getmsg = ra.getGetMessage().toString();
					if(CostantiConfigurazione.ABILITATO.toString().equals(getmsg) &&
						invocazionePorta!=null && invocazionePorta.sizeCredenzialiList()>0) {
						for (int i = 0; i < invocazionePorta.sizeCredenzialiList(); i++) {
							Credenziali c = invocazionePorta.getCredenziali(i);
							if(CredenzialeTipo.BASIC.equals(c.getTipo())) {
								getmsgUsername = c.getUser();
								getmsgPassword = c.getPassword();
								break;
							}
						}
					}
				}
				
				if(!integrationManagerEnabled && CostantiConfigurazione.ABILITATO.toString().equals(getmsg)) {
					// faccio vedere I.M. anche con interfaccia standard
					integrationManagerEnabled = true;
				}
				
				if (invrifRichiesta == null) {
					if(ra.getInvioPerRiferimento()!=null)
						invrifRichiesta = ra.getInvioPerRiferimento().toString();
					if ((invrifRichiesta == null) || "".equals(invrifRichiesta)) {
						invrifRichiesta = CostantiConfigurazione.DISABILITATO.toString();
					}
				}
				if (risprif == null) {
					if(ra.getRispostaPerRiferimento()!=null)
						risprif = ra.getRispostaPerRiferimento().toString();
					if ((risprif == null) || "".equals(risprif)) {
						risprif = CostantiConfigurazione.DISABILITATO.toString();
					}
				}
				
				if ((tipoauthRichiesta == null) && (ra != null) && ra.getAutenticazione()!=null) {
					tipoauthRichiesta = ra.getAutenticazione().getValue();
				}
				if ((tipoauthRichiesta == null) || "".equals(tipoauthRichiesta)) {
					tipoauthRichiesta = CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE.toString();
				}
				if ((user == null) && (cra != null)) {
					user = cra.getUser();
					password = cra.getPassword();
				}
				if (endpointtype == null) {
					if ((connra.getCustom()!=null && connra.getCustom()) && 
							!connra.getTipo().equals(TipiConnettore.HTTPS.toString()) && 
							!connra.getTipo().equals(TipiConnettore.FILE.toString())) {
						endpointtype = TipiConnettore.CUSTOM.toString();
						tipoconn = connra.getTipo();
					} else
						endpointtype = connra.getTipo();
				}
				
				Map<String, String> props = null;
				if(ra!=null && ra.getConnettore()!=null)
					props = ra.getConnettore().getProperties();
				
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
					
					if(props!=null) {
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
				
				if(tokenPolicy==null && props!=null){
					String v = props.get(CostantiDB.CONNETTORE_TOKEN_POLICY);
					if(v!=null && !"".equals(v)){
						tokenPolicy = v;
						autenticazioneToken = true;
					}
				}
				
				opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(saHelper, transferMode, redirectMode);
				
				autenticazioneHttp = saHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);
				
				if(autenticazioneApiKey==null || StringUtils.isEmpty(autenticazioneApiKey)) {
					for (int i = 0; i < connra.sizePropertyList(); i++) {
						Property singlecp = cp.get(i);
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
					
					autenticazioneApiKey = saHelper.getAutenticazioneApiKey(autenticazioneApiKey, endpointtype, apiKeyValue);
					if(ServletUtils.isCheckBoxEnabled(autenticazioneApiKey)) {
						useOAS3Names = saHelper.isAutenticazioneApiKeyUseOAS3Names(apiKeyHeader, appIdHeader);
						useAppId = saHelper.isAutenticazioneApiKeyUseAppId(appIdValue);
					}
					else {
						apiKeyValue=null;
						apiKeyHeader=null;
						appIdHeader=null;
						appIdValue=null;
					}
				}
				
				for (int i = 0; i < connra.sizePropertyList(); i++) {
					Property singlecp = cp.get(i);
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_HTTP_LOCATION) &&
						url == null) {
						url = singlecp.getValore();
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_NOME) &&
						nome == null) {
						nome = singlecp.getValore();
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_TIPO) &&
						tipo == null) {
						tipo = singlecp.getValore();
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY) &&
						connfact == null) {
						connfact = singlecp.getValore();
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_SEND_AS) &&
						sendas == null) {
						sendas = singlecp.getValore();
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
								httpspwdkey.equals(httpspwd))
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
					httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
					httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
				}
				if(httpsTrustVerifyCertS==null || "".equals(httpsTrustVerifyCertS)){
					httpsTrustVerifyCertS = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS ? Costanti.CHECK_BOX_ENABLED_TRUE : Costanti.CHECK_BOX_DISABLED;
					httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
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
				
				
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				saHelper.addEndPointToDati(dati,idsil,nomeservizioApplicativo,sbustamento,sbustamentoInformazioniProtocolloRichiesta,
						getmsg,getmsgUsername,getmsgPassword,true,
						invrifRichiesta,risprif,nomeProtocollo,true,false,true,
						parentSA,serviceBinding, accessoDaAPSParametro, erogazioneServizioApplicativoServerEnabled,
						null, false,
						integrationManagerEnabled,
						TipoOperazione.CHANGE, null,null,
						postBackViaPost);
				
				dati = saHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, null,
						url, nome,
						tipo, user, password, initcont, urlpgk, provurl,
						connfact, sendas, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, TipoOperazione.CHANGE, 
						httpsurl, httpstipologia, httpshostverify, 
						httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, 
						httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
						tipoconn, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA,
						nomeservizioApplicativo, idsil, null, null, null, null,
						null, null, true,
						isConnettoreCustomUltimaImmagineSalvata, 
						proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
						tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
						opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop,
						requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
						requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						autenticazioneToken,tokenPolicy, forcePDND, forceOAuth,
						listExtendedConnettore, forceEnabled,
						nomeProtocollo, false, false
						, false, erogazioneServizioApplicativoServerEnabled, null, null,
						autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
						postBackViaPost
						);
				
				dati = saHelper.addHiddenFieldsToDati(dati, provider, idAsps, idPorta);
				
				dati = saHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, null, null, idPorta, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, 
						ServiziApplicativiCostanti.TIPO_OPERAZIONE_ENDPOINT_RISPOSTA_ASINCRONA);
			}

			// Controlli sui campi immessi
			boolean isOk = saHelper.servizioApplicativoEndPointCheckData(protocollo, listExtendedConnettore, sa);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				saHelper.addEndPointToDati(dati,idsil,nomeservizioApplicativo,sbustamento,sbustamentoInformazioniProtocolloRichiesta,
						getmsg,getmsgUsername,getmsgPassword,true,
						invrifRichiesta,risprif,nomeProtocollo,true,false,true,
						parentSA,serviceBinding, accessoDaAPSParametro, erogazioneServizioApplicativoServerEnabled,
						null, false,
						integrationManagerEnabled,
						TipoOperazione.CHANGE, null,null,
						postBackViaPost);
				
				dati = saHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, null,
						url, nome,
						tipo, "", "", initcont, urlpgk, provurl, connfact,
						sendas, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, TipoOperazione.CHANGE, 
						httpsurl, httpstipologia, httpshostverify, 
						httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, 
						httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
						tipoconn, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA,
						nomeservizioApplicativo, idsil, null, null, null, null,
						null, null, true,
						isConnettoreCustomUltimaImmagineSalvata, 
						proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
						tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
						opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop,
						requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
						requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						autenticazioneToken,tokenPolicy, forcePDND, forceOAuth,
						listExtendedConnettore, forceEnabled,
						nomeProtocollo, false, false
						, false, erogazioneServizioApplicativoServerEnabled, null, null,
						autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
						postBackViaPost
						);
				
				dati = saHelper.addHiddenFieldsToDati(dati, provider, idAsps, idPorta);
				
				dati = saHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, null, null, idPorta, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, 
						ServiziApplicativiCostanti.TIPO_OPERAZIONE_ENDPOINT_RISPOSTA_ASINCRONA);
			}

			// Modifico i dati del servizioApplicativo nel db
			if(sbustamento==null){
				ra.setSbustamentoSoap(CostantiConfigurazione.DISABILITATO);
			}else{
				ra.setSbustamentoSoap(StatoFunzionalita.toEnumConstant(sbustamento));
			}
			if(sbustamentoInformazioniProtocolloRichiesta==null){
				ra.setSbustamentoInformazioniProtocollo(CostantiConfigurazione.ABILITATO);
			}else{
				ra.setSbustamentoInformazioniProtocollo(StatoFunzionalita.toEnumConstant(sbustamentoInformazioniProtocolloRichiesta));
			}
			ra.setGetMessage(StatoFunzionalita.toEnumConstant(getmsg));
			ra.setInvioPerRiferimento(StatoFunzionalita.toEnumConstant(invrifRichiesta));
			ra.setRispostaPerRiferimento(StatoFunzionalita.toEnumConstant(risprif));
			if (tipoauthRichiesta!=null && tipoauthRichiesta.equals(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString())) {
				if (cra == null) {
					cra = new InvocazioneCredenziali();
				}
				cra.setUser(user);
				cra.setPassword(password);
				ra.setCredenziali(cra);
				ra.setAutenticazione(InvocazioneServizioTipoAutenticazione.BASIC);
			} 
			else if(endpointtype.equals(TipiConnettore.JMS.toString())){
				if(user!=null && password!=null){
					if (cra == null) {
						cra = new InvocazioneCredenziali();
					}
					cra.setUser(user);
					cra.setPassword(password);
				}
				ra.setCredenziali(cra);
				ra.setAutenticazione(InvocazioneServizioTipoAutenticazione.BASIC);
			}
			else {
				ra.setCredenziali(null);
				ra.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
			}
			String oldConnT = connra.getTipo();
			if ((connra.getCustom()!=null && connra.getCustom()) && 
					!connra.getTipo().equals(TipiConnettore.HTTPS.toString()) && 
					!connra.getTipo().equals(TipiConnettore.FILE.toString()))
				oldConnT = TipiConnettore.CUSTOM.toString();
			saHelper.fillConnettore(connra, connettoreDebug, endpointtype, oldConnT, tipoconn, url,
					nome, tipo, user, password,
					initcont, urlpgk, provurl, connfact,
					sendas, httpsurl, httpstipologia, httpshostverify, 
					httpsTrustVerifyCert, httpspath, httpstipo,
					httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust,
					httpspathkey, httpstipokey,
					httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey,
					httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
					proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
					tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
					opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop,
					requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
					requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					tokenPolicy,
					apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
					listExtendedConnettore);
			ra.setConnettore(connra);
			sa.setRispostaAsincrona(ra);

			if(CostantiConfigurazione.ABILITATO.toString().equals(getmsg)) {
				boolean found = false;
				if(invocazionePorta!=null && invocazionePorta.sizeCredenzialiList()>0) {
					for (int i = 0; i < invocazionePorta.sizeCredenzialiList(); i++) {
						Credenziali c = invocazionePorta.getCredenziali(i);
						if(CredenzialeTipo.BASIC.equals(c.getTipo())) {
							c.setUser(getmsgUsername);
							c.setPassword(getmsgPassword);
							found = true;
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
					invocazionePorta.addCredenziali(c);
				}
			}
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			saCore.performUpdateOperation(userLogin, saHelper.smista(), sa);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			
			List<ServizioApplicativo> lista = null;
			int idLista = -1;
			switch (parentSA) { 
			case ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_CONFIGURAZIONE:
				if(accessoDaListaAPS) {
					idLista = Liste.SERVIZI;
					ricerca = saHelper.checkSearchParameters(idLista, ricerca);
					if(gestioneErogatori) {
						ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
					}
					boolean [] permessi = new boolean[2];
					PermessiUtente pu = ServletUtils.getUserFromSession(request, session).getPermessi();
					permessi[0] = pu.isServizi();
					permessi[1] = pu.isAccordiCooperazione();
					List<AccordoServizioParteSpecifica> listaS = null;
					AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(saCore);
					if(apsCore.isVisioneOggettiGlobale(superUser)){
						listaS = apsCore.soggettiServizioList(null, ricerca,permessi,session, request);
					}else{
						listaS = apsCore.soggettiServizioList(superUser, ricerca,permessi,session, request);
					}
					AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
					apsHelper.prepareServiziList(ricerca, listaS);
				}
				else {
					idLista = Liste.CONFIGURAZIONE_EROGAZIONE;
					ricerca = saHelper.checkSearchParameters(idLista, ricerca);
					int idServizio = Integer.parseInt(idAsps);
					AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(saCore);
					AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idServizio);
					IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
					Long idSoggetto = asps.getIdSoggetto() != null ? asps.getIdSoggetto() : -1L;
					List<MappingErogazionePortaApplicativa> lista2 = apsCore.mappingServiziPorteAppList(idServizio2,asps.getId(),ricerca);
					AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
					apsHelper.prepareServiziConfigurazioneList(lista2, idAsps, idSoggetto+"", ricerca);
				}
				break;
			case ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO:
				idLista = Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO;
				ricerca = saHelper.checkSearchParameters(idLista, ricerca);
				lista = saCore.soggettiServizioApplicativoList(ricerca,soggLong);
				saHelper.prepareServizioApplicativoList(ricerca, lista, useIdSogg);
				break;
			case ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE:
			default:
				idLista = Liste.SERVIZIO_APPLICATIVO;
				ricerca = saHelper.checkSearchParameters(idLista, ricerca);
				
				if(saCore.isVisioneOggettiGlobale(superUser)){
					lista = saCore.soggettiServizioApplicativoList(null, ricerca);
				}else{
					lista = saCore.soggettiServizioApplicativoList(superUser, ricerca);
				}
				saHelper.prepareServizioApplicativoList(ricerca, lista, useIdSogg);
				break;
			}

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, 
					ServiziApplicativiCostanti.TIPO_OPERAZIONE_ENDPOINT_RISPOSTA_ASINCRONA);

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI,ServiziApplicativiCostanti.TIPO_OPERAZIONE_ENDPOINT_RISPOSTA_ASINCRONA);
		} 

	}
}
