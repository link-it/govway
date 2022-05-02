/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.soggetti;

import java.util.List;
import java.util.Map;
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
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * soggettiEndPoint
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class SoggettiEndPoint extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {

			SoggettiHelper soggettiHelper = new SoggettiHelper(request, pd, session);
			
			String id = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ID);
			int idInt = Integer.parseInt(id);
				
			String endpointtype = soggettiHelper.readEndPointType();
			String tipoconn = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			String autenticazioneHttp = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			String user = null;
			String password = null;
			
			String connettoreDebug = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
			
			// token policy
			String autenticazioneTokenS = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			boolean autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			String token_policy = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
			
			// proxy
			String proxy_enabled = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxy_hostname = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxy_port = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxy_username = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxy_password = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);
			
			// tempi risposta
			String tempiRisposta_enabled = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			String tempiRisposta_connectionTimeout = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			String tempiRisposta_readTimeout = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			String tempiRisposta_tempoMedioRisposta = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
			
			// opzioni avanzate
			String transfer_mode = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transfer_mode_chunk_size = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirect_mode = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirect_max_hop = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String clientLibrary = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_CLIENT_LIBRARY);
			String opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(soggettiHelper,transfer_mode, redirect_mode, clientLibrary);
			
			// http
			String url = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// jms
			String nome = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipo = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String initcont = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String sendas = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(endpointtype)){
				user = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				password = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}
			
			// https
			String httpsurl = url;
			String httpstipologia = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
			String httpshostverifyS = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			boolean httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
			String httpsTrustVerifyCertS = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS );
			boolean httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
			String httpspath = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			String httpstipo = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			boolean httpsstato = ServletUtils.isCheckBoxEnabled(httpsstatoS);
			String httpskeystore = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			String httpsKeyAlias = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
			String httpsTrustStoreCRLs = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// file
			String requestOutputFileName = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			String requestOutputFileName_permissions = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS);
			String requestOutputFileNameHeaders = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			String requestOutputFileNameHeaders_permissions = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_PERMISSIONS);
			String requestOutputParentDirCreateIfNotExists = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			String requestOutputOverwriteIfExists = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			String responseInputMode = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			String responseInputFileName = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			String responseInputFileNameHeaders = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			String responseInputDeleteAfterRead = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			String responseInputWaitTime = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);

			
			// Preparo il menu
			soggettiHelper.makeMenu();

			// Prendo nome, tipo e pdd dal db
			SoggettiCore soggettiCore = new SoggettiCore();			
			
			SoggettoCtrlStat scs = soggettiCore.getSoggettoCtrlStat(idInt);
			Soggetto ss = scs.getSoggettoReg();
			org.openspcoop2.core.config.Soggetto ssconf = scs.getSoggettoConf();
			String nomeprov = scs.getNome();
			String tipoprov = scs.getTipo();
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoprov);
			String tmpTitle = soggettiHelper.getLabelNomeSoggetto(protocollo, tipoprov , nomeprov);
			// String pdd = ss.getServer();
			Connettore c = ss.getConnettore();
			
			Boolean isConnettoreCustomUltimaImmagineSalvata = c.getCustom();

			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(c, ConnettoreServletType.SOGGETTO, soggettiHelper, 
							(endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro
			
			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if(soggettiHelper.isEditModeInProgress()){
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, SoggettiCostanti.LABEL_SOGGETTI, 
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST, "Connettore di " + tmpTitle);
			
				if (endpointtype == null) {
					if ( (c.getCustom()!=null && c.getCustom()) && 
							!c.getTipo().equals(TipiConnettore.HTTPS.toString()) && 
							!c.getTipo().equals(TipiConnettore.FILE.toString())) {
						endpointtype = TipiConnettore.CUSTOM.toString();
						tipoconn = c.getTipo();
					} else
						endpointtype = c.getTipo();
				}
				Map<String, String> props = c.getProperties();
				
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
				
				if(tempiRisposta_enabled == null ||
						tempiRisposta_connectionTimeout==null || "".equals(tempiRisposta_connectionTimeout) 
						|| 
						tempiRisposta_readTimeout==null || "".equals(tempiRisposta_readTimeout) 
						|| 
						tempiRisposta_tempoMedioRisposta==null || "".equals(tempiRisposta_tempoMedioRisposta) ){
					
					ConfigurazioneCore configCore = new ConfigurazioneCore(soggettiCore);
					ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
					
					if( props!=null ) {
						if(tempiRisposta_connectionTimeout==null || "".equals(tempiRisposta_connectionTimeout) ) {
							String v = props.get(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT);
							if(v!=null && !"".equals(v)){
								tempiRisposta_connectionTimeout = v.trim();
								tempiRisposta_enabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
							}
							else {
								tempiRisposta_connectionTimeout = configGenerale.getTempiRispostaFruizione().getConnectionTimeout().intValue()+"";
							}
						}
						if(tempiRisposta_readTimeout==null || "".equals(tempiRisposta_readTimeout) ) {
							String v = props.get(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT);
							if(v!=null && !"".equals(v)){
								tempiRisposta_readTimeout = v.trim();
								tempiRisposta_enabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
							}
							else {
								tempiRisposta_readTimeout = configGenerale.getTempiRispostaFruizione().getReadTimeout().intValue()+"";
							}
						}
						if(tempiRisposta_tempoMedioRisposta==null || "".equals(tempiRisposta_tempoMedioRisposta) ) {
							String v = props.get(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA);
							if(v!=null && !"".equals(v)){
								tempiRisposta_tempoMedioRisposta = v.trim();
								tempiRisposta_enabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
							}
							else {
								tempiRisposta_tempoMedioRisposta = configGenerale.getTempiRispostaFruizione().getTempoMedioRisposta().intValue()+"";
							}
						}
					}
					else {
						if(tempiRisposta_connectionTimeout==null || "".equals(tempiRisposta_connectionTimeout) ) {
							tempiRisposta_connectionTimeout = configGenerale.getTempiRispostaFruizione().getConnectionTimeout().intValue()+"";
						}
						if(tempiRisposta_readTimeout==null || "".equals(tempiRisposta_readTimeout) ) {
							tempiRisposta_readTimeout = configGenerale.getTempiRispostaFruizione().getReadTimeout().intValue()+"";
						}
						if(tempiRisposta_tempoMedioRisposta==null || "".equals(tempiRisposta_tempoMedioRisposta) ) {
							tempiRisposta_tempoMedioRisposta = configGenerale.getTempiRispostaFruizione().getTempoMedioRisposta().intValue()+"";
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
				
				if(clientLibrary==null && props!=null){
					String v = props.get(CostantiDB.CONNETTORE_HTTP_CLIENT_LIBRARY);
					if(v!=null && !"".equals(v)){
						clientLibrary = v.trim();
					}
				}
				
				if(token_policy==null && props!=null){
					String v = props.get(CostantiDB.CONNETTORE_TOKEN_POLICY);
					if(v!=null && !"".equals(v)){
						token_policy = v;
						autenticazioneToken = true;
					}
				}
				
				opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(soggettiHelper,transfer_mode, redirect_mode, clientLibrary);
				
				if (url == null) {
					url = props.get(CostantiDB.CONNETTORE_HTTP_LOCATION);
				}
				
				if (nome == null) {
					nome = props.get(CostantiDB.CONNETTORE_JMS_NOME);
					tipo = props.get(CostantiDB.CONNETTORE_JMS_TIPO);
					String userTmp = props.get(CostantiDB.CONNETTORE_USER);
					if(userTmp!=null && !"".equals(userTmp)){
						user = userTmp;
					}
					String passwordTmp = props.get(CostantiDB.CONNETTORE_PWD);
					if(passwordTmp!=null && !"".equals(passwordTmp)){
						password = passwordTmp;
					}
					initcont = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL);
					urlpgk = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG);
					provurl = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL);
					connfact = props.get(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY);
					sendas = props.get(CostantiDB.CONNETTORE_JMS_SEND_AS);
				}
				
				autenticazioneHttp = soggettiHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);
				
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
					httpsTrustStoreCRLs = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_CRLs);
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
					httpshostverifyS = Costanti.CHECK_BOX_ENABLED;
					httpshostverify = true;
				}
				if(httpsTrustVerifyCertS==null || "".equals(httpsTrustVerifyCertS)){
					httpsTrustVerifyCertS = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS ? Costanti.CHECK_BOX_ENABLED_TRUE : Costanti.CHECK_BOX_DISABLED;
					httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
				}

				// file
				if(responseInputMode==null && props!=null){
					
					requestOutputFileName = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE);	
					requestOutputFileName_permissions = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_PERMISSIONS);
					requestOutputFileNameHeaders = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS);	
					requestOutputFileNameHeaders_permissions = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS_PERMISSIONS);
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
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				DataElement de = new DataElement();
				de.setLabel(ConnettoriCostanti.PARAMETRO_CONNETTORE_ID);
				de.setValue(id);
				de.setType(DataElementType.HIDDEN);
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ID);
				dati.addElement(de);

				dati = soggettiHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, null,
						url, nome,
						tipo, user, password, initcont, urlpgk, provurl, 
						connfact, sendas, SoggettiCostanti.OBJECT_NAME_SOGGETTI,TipoOperazione.CHANGE, 
						httpsurl, httpstipologia, httpshostverify, 
						httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, 
						httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs,
						tipoconn, SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT, id, nomeprov,
						tipoprov, null, null, null, null, null, true, 
						isConnettoreCustomUltimaImmagineSalvata, 
						proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
						tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
						opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop, clientLibrary,
						requestOutputFileName, requestOutputFileName_permissions, requestOutputFileNameHeaders, requestOutputFileNameHeaders_permissions,
						requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						autenticazioneToken,token_policy,
						listExtendedConnettore, false,
						protocollo, false, false
						, false, false, null, null
						);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, SoggettiCostanti.TIPO_OPERAZIONE_ENDPOINT);
			}

			// Controlli sui campi immessi
			boolean isOk = soggettiHelper.soggettiEndPointCheckData(TipoOperazione.CHANGE,listExtendedConnettore,tipoprov,nomeprov);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, SoggettiCostanti.LABEL_SOGGETTI, 
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST, "Connettore di " + tmpTitle);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				DataElement de = new DataElement();
				de.setLabel(ConnettoriCostanti.PARAMETRO_CONNETTORE_ID);
				de.setValue(id);
				de.setType(DataElementType.HIDDEN);
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ID);
				dati.addElement(de);

				dati = soggettiHelper.addEndPointToDati(dati,  connettoreDebug, endpointtype, autenticazioneHttp, null,
						url, nome,
						tipo, user, password, initcont, urlpgk, provurl,
						connfact, sendas, SoggettiCostanti.OBJECT_NAME_SOGGETTI,TipoOperazione.CHANGE, 
						httpsurl, httpstipologia, httpshostverify, 
						httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, 
						httpspwdprivatekey,	httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs,
						tipoconn, SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT, id, nomeprov,
						tipoprov, null, null, null, null, null, true,
						isConnettoreCustomUltimaImmagineSalvata, 
						proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
						tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
						opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop, clientLibrary,
						requestOutputFileName, requestOutputFileName_permissions, requestOutputFileNameHeaders, requestOutputFileNameHeaders_permissions,
						requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						autenticazioneToken,token_policy,
						listExtendedConnettore, false,
						protocollo, false, false
						, false, false, null, null
						);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, SoggettiCostanti.TIPO_OPERAZIONE_ENDPOINT);
			}

			// Modifico i dati del soggetto nel db
			String oldConnT = c.getTipo();
			if ((c.getCustom()!=null && c.getCustom()) && 
					!c.getTipo().equals(TipiConnettore.HTTPS.toString()) && 
					!c.getTipo().equals(TipiConnettore.FILE.toString()))
				oldConnT = TipiConnettore.CUSTOM.toString();
			soggettiHelper.fillConnettore(c, connettoreDebug, endpointtype, oldConnT, tipoconn, url, nome, tipo, user,
					password, initcont, urlpgk, provurl, connfact, sendas,
					httpsurl, httpstipologia, httpshostverify, 
					httpsTrustVerifyCert, httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey, 
					httpspwdprivatekey,	httpsalgoritmokey,
					httpsKeyAlias, httpsTrustStoreCRLs,
					proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
					tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
					opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop, clientLibrary,
					requestOutputFileName, requestOutputFileName_permissions, requestOutputFileNameHeaders, requestOutputFileNameHeaders_permissions,
					requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					token_policy,
					listExtendedConnettore);
			ss.setConnettore(c);
			SoggettoCtrlStat newCsc = new SoggettoCtrlStat(ss, ssconf);

			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			soggettiCore.performUpdateOperation(userLogin, soggettiHelper.smista(), newCsc);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			

			int idLista = Liste.SOGGETTI;
			ricerca = soggettiHelper.checkSearchParameters(idLista, ricerca);
			if(soggettiCore.isRegistroServiziLocale()){
				List<Soggetto> lista = null;
				if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
					lista = soggettiCore.soggettiRegistroList(null, ricerca);
				}else{
					lista = soggettiCore.soggettiRegistroList(userLogin, ricerca);
				}
				soggettiHelper.prepareSoggettiList(lista, ricerca);
			}
			else{
				List<org.openspcoop2.core.config.Soggetto> lista = null;
				if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
					lista = soggettiCore.soggettiList(null, ricerca);
				}else{
					lista = soggettiCore.soggettiList(userLogin, ricerca);
				}
				soggettiHelper.prepareSoggettiConfigList(lista, ricerca);
			}
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, SoggettiCostanti.TIPO_OPERAZIONE_ENDPOINT);

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					SoggettiCostanti.OBJECT_NAME_SOGGETTI,SoggettiCostanti.TIPO_OPERAZIONE_ENDPOINT);
		}
	}

}
