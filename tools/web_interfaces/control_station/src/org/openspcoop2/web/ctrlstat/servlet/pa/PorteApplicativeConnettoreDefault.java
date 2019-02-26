/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

/**     
 * PorteApplicativeConnettoreDefault
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicativeConnettoreDefault extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;

		try {
			
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			// Preparo il menu
			porteApplicativeHelper.makeMenu();
			
			String id = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";

			Properties parametersPOST = null;
			
			String endpointtype = porteApplicativeHelper.readEndPointType();
			String tipoconn = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO );
			String autenticazioneHttp = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);

			String connettoreDebug = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);

			// proxy
			String proxy_enabled = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxy_hostname = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxy_port = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxy_username = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxy_password = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);

			// tempi risposta
			String tempiRisposta_enabled = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			String tempiRisposta_connectionTimeout = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			String tempiRisposta_readTimeout = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			String tempiRisposta_tempoMedioRisposta = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
			
			// opzioni avanzate
			String transfer_mode = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transfer_mode_chunk_size = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirect_mode = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirect_max_hop = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(porteApplicativeHelper, transfer_mode, redirect_mode);

			String user= null;
			String password =null;
			
			// http
			String url = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL  );
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
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
				password = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}

			// https
			String httpsurl = url;
			String httpstipologia = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE );
			String httpshostverifyS = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			String httpspath = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION );
			String httpstipo = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			String httpskeystore = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// file
			String requestOutputFileName = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			String requestOutputFileNameHeaders = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			String requestOutputParentDirCreateIfNotExists = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			String requestOutputOverwriteIfExists = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			String responseInputMode = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			String responseInputFileName = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			String responseInputFileNameHeaders = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			String responseInputDeleteAfterRead = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			String responseInputWaitTime = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);

			boolean httpshostverify = false;
			if (httpshostverifyS != null && httpshostverifyS.equals(Costanti.CHECK_BOX_ENABLED))
				httpshostverify = true;
			boolean httpsstato = false;
			if (httpsstatoS != null && httpsstatoS.equals(Costanti.CHECK_BOX_ENABLED))
				httpsstato = true;

			Boolean isConnettoreCustomUltimaImmagineSalvata = null;
			
			boolean forceEnableConnettore = false;
			if( (!porteApplicativeHelper.isModalitaCompleta())) {
				forceEnableConnettore = true;
			}
			
			Connettore conTmp = null;
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_PORTA_APPLICATIVA_ADD, porteApplicativeHelper, 
							parametersPOST, (endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro
			
			// Prendo il nome della porta
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			
			PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = portaApplicativa.getNome();
			
			String modalita = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE);
			
			String [] modalitaLabels = { PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE_DEFAULT, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE_RIDEFINITO };
			String [] modalitaValues = { PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE_DEFAULT, PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE_RIDEFINITO };
			
			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			boolean initConnettore = false;
			// Controllo se ho modificato l'azione allora ricalcolo il nome
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE)){
					// devo resettare il connettore
					if(modalita.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE_RIDEFINITO)) {
						initConnettore = true;
					}
				}
			}
			
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DEFAULT_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DEFAULT,
						portaApplicativa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DEFAULT_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta,  null));
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			String servletConnettore = null;
			Parameter[] parametriServletConnettore =null;
			
			if(	porteApplicativeHelper.isEditModeInProgress()){
				
				if(modalita == null) {
					modalita = PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE_DEFAULT;
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
						if(porteApplicativeHelper.isModalitaCompleta()==false) {
							endpointtype = TipiConnettore.HTTP.getNome();
						}
						else {
							endpointtype = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DISABILITATO;
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
					
					 tipoSendas = ConnettoriCostanti.TIPO_SEND_AS[0];
					tipoJms = ConnettoriCostanti.TIPI_CODE_JMS[0];

					autenticazioneHttp = porteApplicativeHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);
					
					tempiRisposta_enabled = null;
					ConfigurazioneCore configCore = new ConfigurazioneCore(porteApplicativeCore);
					ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
					tempiRisposta_connectionTimeout = configGenerale.getTempiRispostaErogazione().getConnectionTimeout().intValue()+"";
					tempiRisposta_readTimeout = configGenerale.getTempiRispostaErogazione().getReadTimeout().intValue()+"";
					tempiRisposta_tempoMedioRisposta = configGenerale.getTempiRispostaErogazione().getTempoMedioRisposta().intValue()+"";
					
				}
				// Devo cmq rileggere i valori se non definiti
				if(tempiRisposta_connectionTimeout==null || "".equals(tempiRisposta_connectionTimeout) 
						|| 
						tempiRisposta_readTimeout==null || "".equals(tempiRisposta_readTimeout) 
						|| 
						tempiRisposta_tempoMedioRisposta==null || "".equals(tempiRisposta_tempoMedioRisposta) ){
					ConfigurazioneCore configCore = new ConfigurazioneCore(porteApplicativeCore);
					ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
					if(tempiRisposta_connectionTimeout==null || "".equals(tempiRisposta_connectionTimeout) ) {
						tempiRisposta_connectionTimeout = configGenerale.getTempiRispostaErogazione().getConnectionTimeout().intValue()+"";
					}
					if(tempiRisposta_readTimeout==null || "".equals(tempiRisposta_readTimeout) ) {
						tempiRisposta_readTimeout = configGenerale.getTempiRispostaErogazione().getReadTimeout().intValue()+"";
					}
					if(tempiRisposta_tempoMedioRisposta==null || "".equals(tempiRisposta_tempoMedioRisposta) ) {
						tempiRisposta_tempoMedioRisposta = configGenerale.getTempiRispostaErogazione().getTempoMedioRisposta().intValue()+"";
					}
				}
				
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,idAsps, dati);
				
				dati = porteApplicativeHelper.addConnettoreDefaultRidefinitoToDati(dati,TipoOperazione.OTHER, modalita, modalitaValues,modalitaLabels,false,servletConnettore,parametriServletConnettore);
				
				if(modalita.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE_RIDEFINITO)) {
					dati = porteApplicativeHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, 
							null, //(porteApplicativeHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX , 
							url, nomeCodaJms,
							tipoJms, user,
							password, initcont, urlpgk,
							provurl, connfact, tipoSendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,TipoOperazione.OTHER, httpsurl, httpstipologia,
							httpshostverify, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey, httpspwdprivatekey,
							httpsalgoritmokey, tipoconn, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORE_DEFAULT, null, null,
							null, null, null, null, null, null, false,
							isConnettoreCustomUltimaImmagineSalvata, 
							proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
							tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
							opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
							requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
							listExtendedConnettore, forceEnableConnettore);
				}

				pd.setDati(dati);
				
				if(modalita.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE_DEFAULT)) {
					pd.disableOnlyButton();
				}
				
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				// Forward control to the specified success URI
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORE_DEFAULT, 
						ForwardParams.OTHER(""));
			}
			
			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.connettoreDefaultRidefinitoCheckData(TipoOperazione.OTHER, modalita);
			
			if(isOk && modalita.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE_RIDEFINITO)) {
				isOk = porteApplicativeHelper.endPointCheckData(endpointtype, url, nomeCodaJms, tipoJms,
						user, password, initcont, urlpgk, provurl, connfact,
						tipoSendas, httpsurl, httpstipologia, httpshostverify,
						httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
						httpskeystore, httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, httpspwdprivatekey,
						httpsalgoritmokey, tipoconn,autenticazioneHttp,
						proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
						tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
						opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
						requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						listExtendedConnettore);
			}
			
			if(!isOk) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,idAsps, dati);
				
				dati = porteApplicativeHelper.addConnettoreDefaultRidefinitoToDati(dati,TipoOperazione.OTHER, modalita, modalitaValues,modalitaLabels,false,servletConnettore,parametriServletConnettore);
				
				if(modalita.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE_RIDEFINITO)) {
					dati = porteApplicativeHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, 
							null, // (porteApplicativeHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX , 
							url, nomeCodaJms,
							tipoJms, user,
							password, initcont, urlpgk,
							provurl, connfact, tipoSendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,TipoOperazione.OTHER, httpsurl, httpstipologia,
							httpshostverify, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey, httpspwdprivatekey,
							httpsalgoritmokey, tipoconn, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORE_DEFAULT, null, null,
							null, null, null, null, null, null, false,
							isConnettoreCustomUltimaImmagineSalvata, 
							proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
							tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
							opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
							requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
							listExtendedConnettore, forceEnableConnettore);
				}

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORE_DEFAULT,
						ForwardParams.OTHER(""));
			}

			
			// Connettore
			Connettore connettore = new Connettore();
			// this.nomeservizio);
			if (endpointtype.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM))
				connettore.setTipo(tipoconn);
			else
				connettore.setTipo(endpointtype);

			porteApplicativeHelper.fillConnettore(connettore, connettoreDebug, endpointtype, endpointtype, tipoconn, url,
					nomeCodaJms, tipoJms, user, password,
					initcont, urlpgk, url, connfact,
					tipoSendas, httpsurl, httpstipologia,
					httpshostverify, httpspath, httpstipo,
					httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust,
					httpspathkey, httpstipokey,
					httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey,
					proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
					tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
					opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
					requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					listExtendedConnettore);
			
			List<Object> listaOggettiDaCreare = new ArrayList<Object>();
			List<Object> listaOggettiDaModificare = new ArrayList<Object>();
			
			// creare un servizio applicativo
			String nomeServizioApplicativoErogatore = portaApplicativa.getNome();
			
			ServizioApplicativo sa = new ServizioApplicativo();
			sa.setNome(nomeServizioApplicativoErogatore);
			sa.setTipologiaFruizione(TipologiaFruizione.DISABILITATO.getValue());
			sa.setTipologiaErogazione(TipologiaErogazione.TRASPARENTE.getValue());
			sa.setIdSoggetto((long) soggInt);
			sa.setTipoSoggettoProprietario(portaApplicativa.getTipoSoggettoProprietario());
			sa.setNomeSoggettoProprietario(portaApplicativa.getNomeSoggettoProprietario());
			
			RispostaAsincrona rispostaAsinc = new RispostaAsincrona();
			rispostaAsinc.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
			rispostaAsinc.setGetMessage(CostantiConfigurazione.DISABILITATO);
			sa.setRispostaAsincrona(rispostaAsinc);
			
			InvocazioneServizio invServizio = new InvocazioneServizio();
			invServizio.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
			invServizio.setGetMessage(CostantiConfigurazione.DISABILITATO);
			invServizio.setConnettore(connettore.mappingIntoConnettoreConfigurazione());
			sa.setInvocazioneServizio(invServizio);
			
			listaOggettiDaCreare.add(sa);
			
			
			portaApplicativa.getServizioApplicativoList().clear();
			PortaApplicativaServizioApplicativo paSA = new PortaApplicativaServizioApplicativo();
			paSA.setNome(nomeServizioApplicativoErogatore);
			portaApplicativa.getServizioApplicativoList().add(paSA);
			
			listaOggettiDaModificare.add(portaApplicativa);
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performCreateOperation(userLogin, porteApplicativeHelper.smista(), listaOggettiDaCreare.toArray());
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), listaOggettiDaModificare.toArray());
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);


			List<PortaApplicativa> lista = null;
			int idLista = -1;
			
		
			switch (parentPA) {
			case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE:
				
				boolean datiInvocazione = ServletUtils.isCheckBoxEnabled(porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_DATI_INVOCAZIONE));
				if(datiInvocazione) {
					idLista = Liste.SERVIZI;
					ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
					
					String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
					if(tipologia!=null) {
						if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
							ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
						}
					}
					
					boolean [] permessi = new boolean[2];
					PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();
					permessi[0] = pu.isServizi();
					permessi[1] = pu.isAccordiCooperazione();
					List<AccordoServizioParteSpecifica> listaS = null;
					String superUser   = ServletUtils.getUserLoginFromSession(session);
					if(apsCore.isVisioneOggettiGlobale(superUser)){
						listaS = apsCore.soggettiServizioList(null, ricerca,permessi,session);
					}else{
						listaS = apsCore.soggettiServizioList(superUser, ricerca,permessi,session);
					}
					AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
					apsHelper.prepareServiziList(ricerca, listaS);
				}
				else {			
					idLista = Liste.CONFIGURAZIONE_EROGAZIONE;
					ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
					int idServizio = Integer.parseInt(idAsps);
					AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idServizio);
					IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
					Long idSoggetto = asps.getIdSoggetto() != null ? asps.getIdSoggetto() : -1L;
					List<MappingErogazionePortaApplicativa> lista2 = apsCore.mappingServiziPorteAppList(idServizio2,asps.getId(),ricerca);
					AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
					apsHelper.prepareServiziConfigurazioneList(lista2, idAsps, idSoggetto+"", ricerca);
				}
				break;
			case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_SOGGETTO:
				idLista = Liste.PORTE_APPLICATIVE_BY_SOGGETTO;
				ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
				lista = porteApplicativeCore.porteAppList(soggInt, ricerca);
				porteApplicativeHelper.preparePorteAppList(ricerca, lista,idLista);
				break;
			case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE:
			default:
				idLista = Liste.PORTE_APPLICATIVE;
				ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
				lista = porteApplicativeCore.porteAppList(null, ricerca);
				porteApplicativeHelper.preparePorteAppList(ricerca, lista,idLista);
				break;
			}
			
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORE_DEFAULT, 
					ForwardParams.OTHER(""));
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORE_DEFAULT , 
					ForwardParams.OTHER(""));
		} 
	}

}
