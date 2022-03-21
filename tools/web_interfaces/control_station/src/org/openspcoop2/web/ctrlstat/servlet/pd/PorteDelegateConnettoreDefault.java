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
package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

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
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
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
 * PorteDelegateConnettoreDefault
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteDelegateConnettoreDefault extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session);
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

		try {
			
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			
			String idTab = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteDelegateHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();
						
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idSoggFruitore = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			String idFruizione= porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null) 
				idFruizione = "";

			Properties parametersPOST = null;
			
			String endpointtype = porteDelegateHelper.readEndPointType();
			String tipoconn = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO );
			String autenticazioneHttp = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);

			String connettoreDebug = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
			
			// token policy
			String autenticazioneTokenS = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			boolean autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			String token_policy = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
						
			// proxy
			String proxy_enabled = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxy_hostname = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxy_port = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxy_username = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxy_password = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);

			// tempi risposta
			String tempiRisposta_enabled = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			String tempiRisposta_connectionTimeout = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			String tempiRisposta_readTimeout = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			String tempiRisposta_tempoMedioRisposta = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
			
			// opzioni avanzate
			String transfer_mode = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transfer_mode_chunk_size = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirect_mode = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirect_max_hop = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(porteDelegateHelper, transfer_mode, redirect_mode);

			String user= null;
			String password =null;
			
			// http
			String url = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL  );
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}

			// jms
			String nomeCodaJms = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipoJms = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String initcont = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String tipoSendas = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(endpointtype)){
				user = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				password = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}

			// https
			String httpsurl = url;
			String httpstipologia = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE );
			String httpshostverifyS = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			String httpsTrustVerifyCertS = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS );
			boolean httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
			String httpspath = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION );
			String httpstipo = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			String httpskeystore = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			String httpsKeyAlias = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
			String httpsTrustStoreCRLs = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// file
			String requestOutputFileName = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			String requestOutputFileName_permissions = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS);
			String requestOutputFileNameHeaders = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			String requestOutputFileNameHeaders_permissions = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_PERMISSIONS);
			String requestOutputParentDirCreateIfNotExists = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			String requestOutputOverwriteIfExists = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			String responseInputMode = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			String responseInputFileName = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			String responseInputFileNameHeaders = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			String responseInputDeleteAfterRead = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			String responseInputWaitTime = porteDelegateHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);

			boolean servizioApplicativoServerEnabled = false;
			String servizioApplicativoServer = null;
			
			boolean httpshostverify = false;
			if (httpshostverifyS != null && httpshostverifyS.equals(Costanti.CHECK_BOX_ENABLED))
				httpshostverify = true;
			boolean httpsstato = false;
			if (httpsstatoS != null && httpsstatoS.equals(Costanti.CHECK_BOX_ENABLED))
				httpsstato = true;

			Boolean isConnettoreCustomUltimaImmagineSalvata = null;
			
			boolean forceEnableConnettore = false;
			if( (!porteDelegateHelper.isModalitaCompleta())) {
				forceEnableConnettore = true;
			}
			
			boolean datiInvocazione = ServletUtils.isCheckBoxEnabled(porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_DATI_INVOCAZIONE));
			
			Connettore conTmp = null;
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_PORTA_APPLICATIVA_ADD, porteDelegateHelper, 
							parametersPOST, (endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro
			
			// Prendo il nome della porta
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			
			IDSoggetto idSoggettoFruitore = null;
			if(porteDelegateCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggFruitore));
				idSoggettoFruitore = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
			}else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(Integer.parseInt(idSoggFruitore));
				idSoggettoFruitore = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
			}

			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String idporta = portaDelegata.getNome();
			
			AccordoServizioParteSpecifica asps = null;
			IDServizio idServizio = null;
			if(idAsps!=null && !"".equals(idAsps)) {
				asps = apsCore.getAccordoServizioParteSpecifica(Long.parseLong(idAsps));
				idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
			}
			else {
				idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaDelegata.getServizio().getTipo(), 
						portaDelegata.getServizio().getNome(), 
						new IDSoggetto(portaDelegata.getSoggettoErogatore().getTipo(), portaDelegata.getSoggettoErogatore().getNome()), 
						portaDelegata.getServizio().getVersione());
			}
			
			String protocollo = apsCore.getProtocolloAssociatoTipoServizio(idServizio.getTipo());
			
			boolean forceHttps = false;
			boolean forceHttpsClient = false;
			if(asps!=null && porteDelegateHelper.isProfiloModIPA(protocollo)) {
				forceHttps = porteDelegateHelper.forceHttpsProfiloModiPA();
				forceHttpsClient = porteDelegateHelper.forceHttpsClientProfiloModiPA(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()), asps.getPortType());
			}
			
			String modalita = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE);
			
			String [] modalitaLabels = { PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_DEFAULT, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_RIDEFINITO };
			String [] modalitaValues = { PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_DEFAULT, PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_RIDEFINITO };
			
			String postBackElementName = porteDelegateHelper.getPostBackElementName();
			boolean initConnettore = false;
			// Controllo se ho modificato l'azione allora ricalcolo il nome
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE)){
					// devo resettare il connettore
					if(modalita.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_RIDEFINITO)) {
						initConnettore = true;
					}
				}
			}
			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idSoggFruitore, idAsps, idFruizione);
			
			String connettoreLabelDi = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE_DEFAULT_DI;
			String connettoreLabel = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE_DEFAULT;
			if(!porteDelegateHelper.isModalitaCompleta()) {
				connettoreLabelDi = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE_DI;
				connettoreLabel = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE;
			}
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						connettoreLabelDi,
						connettoreLabel,
						portaDelegata);
			}
			else {
				labelPerPorta = connettoreLabelDi+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta,  null));
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			String servletConnettore = null;
			Parameter[] parametriServletConnettore =null;
			
			if(	porteDelegateHelper.isEditModeInProgress()){
				
				if(modalita == null) {
					modalita = PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_DEFAULT;
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
						if(porteDelegateHelper.isModalitaCompleta()==false) {
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
					if(httpsTrustVerifyCertS==null || "".equals(httpsTrustVerifyCertS)){
						httpsTrustVerifyCertS = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS ? Costanti.CHECK_BOX_ENABLED_TRUE : Costanti.CHECK_BOX_DISABLED;
						httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
					}
					
					 tipoSendas = ConnettoriCostanti.TIPO_SEND_AS[0];
					tipoJms = ConnettoriCostanti.TIPI_CODE_JMS[0];

					autenticazioneHttp = porteDelegateHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);
					
					tempiRisposta_enabled = null;
					ConfigurazioneCore configCore = new ConfigurazioneCore(soggettiCore);
					ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
					tempiRisposta_connectionTimeout = configGenerale.getTempiRispostaFruizione().getConnectionTimeout().intValue()+"";
					tempiRisposta_readTimeout = configGenerale.getTempiRispostaFruizione().getReadTimeout().intValue()+"";
					tempiRisposta_tempoMedioRisposta = configGenerale.getTempiRispostaFruizione().getTempoMedioRisposta().intValue()+"";

				}
				// Devo cmq rileggere i valori se non definiti
				if(tempiRisposta_connectionTimeout==null || "".equals(tempiRisposta_connectionTimeout) 
						|| 
						tempiRisposta_readTimeout==null || "".equals(tempiRisposta_readTimeout) 
						|| 
						tempiRisposta_tempoMedioRisposta==null || "".equals(tempiRisposta_tempoMedioRisposta) ){
					ConfigurazioneCore configCore = new ConfigurazioneCore(soggettiCore);
					ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
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
				
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				dati = porteDelegateHelper.addConnettoreDefaultRidefinitoToDati(dati,TipoOperazione.OTHER, modalita, modalitaValues,modalitaLabels,false,servletConnettore,parametriServletConnettore);
				
				if(modalita.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_RIDEFINITO)) {
					dati = porteDelegateHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, 
							null,//(porteDelegateHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX ,
							url, nomeCodaJms,
							tipoJms, user,
							password, initcont, urlpgk,
							provurl, connfact, tipoSendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,TipoOperazione.OTHER, 
							httpsurl, httpstipologia, httpshostverify, 
							httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey, 
							httpspwdprivatekey, httpsalgoritmokey, 
							httpsKeyAlias, httpsTrustStoreCRLs,
							tipoconn, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONNETTORE_DEFAULT, null, null,
							null, null, null, null, null, null, false,
							isConnettoreCustomUltimaImmagineSalvata, 
							proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
							tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
							opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
							requestOutputFileName, requestOutputFileName_permissions, requestOutputFileNameHeaders, requestOutputFileNameHeaders_permissions,
							requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
							autenticazioneToken,token_policy,
							listExtendedConnettore, forceEnableConnettore,
							protocollo, forceHttps, forceHttpsClient, false, servizioApplicativoServerEnabled,servizioApplicativoServer, null);
				}

				pd.setDati(dati);
				
				if(modalita.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_DEFAULT)) {
					pd.disableOnlyButton();
				}
				
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				// Forward control to the specified success URI
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CONNETTORE_DEFAULT, ForwardParams.OTHER(""));
			}
			

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.connettoreDefaultRidefinitoCheckData(TipoOperazione.OTHER, modalita);
			
			if(isOk && modalita.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_RIDEFINITO)) {
				isOk = porteDelegateHelper.endPointCheckData(protocollo, false,
						endpointtype, url, nomeCodaJms, tipoJms,
						user, password, initcont, urlpgk, provurl, connfact,
						tipoSendas, httpsurl, httpstipologia, httpshostverify,
						httpsTrustVerifyCert, httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
						httpskeystore, httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, 
						httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs,
						tipoconn,autenticazioneHttp,
						proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
						tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
						opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
						requestOutputFileName, requestOutputFileName_permissions, requestOutputFileNameHeaders, requestOutputFileNameHeaders_permissions,
						requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						autenticazioneToken,token_policy,
						listExtendedConnettore,servizioApplicativoServerEnabled,servizioApplicativoServer);
			}
			
			if(!isOk) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				dati = porteDelegateHelper.addConnettoreDefaultRidefinitoToDati(dati,TipoOperazione.OTHER, modalita, modalitaValues,modalitaLabels,false,servletConnettore,parametriServletConnettore);
				
				if(modalita.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_RIDEFINITO)) {
					dati = porteDelegateHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, 
							null,//(porteDelegateHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX , 
							url, nomeCodaJms,
							tipoJms, user,
							password, initcont, urlpgk,
							provurl, connfact, tipoSendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,TipoOperazione.OTHER, 
							httpsurl, httpstipologia, httpshostverify, 
							httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey, 
							httpspwdprivatekey, httpsalgoritmokey,
							httpsKeyAlias, httpsTrustStoreCRLs,
							tipoconn, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONNETTORE_DEFAULT, null, null,
							null, null, null, null, null, null, false,
							isConnettoreCustomUltimaImmagineSalvata, 
							proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
							tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
							opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
							requestOutputFileName, requestOutputFileName_permissions, requestOutputFileNameHeaders, requestOutputFileNameHeaders_permissions,
							requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
							autenticazioneToken,token_policy,
							listExtendedConnettore, forceEnableConnettore,
							protocollo, forceHttps, forceHttpsClient, false, servizioApplicativoServerEnabled,servizioApplicativoServer, null);
				}

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,	PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CONNETTORE_DEFAULT,	ForwardParams.OTHER(""));
			}
			

			// Connettore
			Connettore connettore = new Connettore();
			// this.nomeservizio);
			if (endpointtype.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM))
				connettore.setTipo(tipoconn);
			else
				connettore.setTipo(endpointtype);
	
			porteDelegateHelper.fillConnettore(connettore, connettoreDebug, endpointtype, endpointtype, tipoconn, url,
					nomeCodaJms, tipoJms, user, password,
					initcont, urlpgk, url, connfact,
					tipoSendas, httpsurl, httpstipologia, httpshostverify, 
					httpsTrustVerifyCert, httpspath, httpstipo,
					httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust,
					httpspathkey, httpstipokey,
					httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey,
					httpsKeyAlias, httpsTrustStoreCRLs,
					proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
					tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
					opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
					requestOutputFileName, requestOutputFileName_permissions, requestOutputFileNameHeaders, requestOutputFileNameHeaders_permissions,
					requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					token_policy,
					listExtendedConnettore);
			
			Fruitore fruitore = null;
			for (Fruitore fruitoreCheck : asps.getFruitoreList()) {
				if(fruitoreCheck.getTipo().equals(idSoggettoFruitore.getTipo()) && fruitoreCheck.getNome().equals(idSoggettoFruitore.getNome())) {
					fruitore = fruitoreCheck;
					break;
				}
			}
			
			ConfigurazioneServizioAzione configurazioneAzione = new ConfigurazioneServizioAzione();
			configurazioneAzione.setConnettore(connettore);
			for (int i = 0; i < portaDelegata.getAzione().sizeAzioneDelegataList(); i++) {
				configurazioneAzione.addAzione(portaDelegata.getAzione().getAzioneDelegata(i));
			}
			fruitore.addConfigurazioneAzione(configurazioneAzione);
			

			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), asps);
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);


			List<PortaDelegata> lista = null;
			int idLista = -1;
			
			switch (parentPD) {
			case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE:
				
				int idAspsInt = Integer.parseInt(idAsps);
				
				AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
				
				if(datiInvocazione) {
					
					String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
					boolean gestioneFruitori = false;
					if(tipologia!=null) {
						if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
							gestioneFruitori = true;
						}
					}
					if(gestioneFruitori) {
						
						idLista = Liste.SERVIZI;
						
						ricerca = apsHelper.checkSearchParameters(idLista, ricerca);
						
						ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE);
						
						String superUser =   ServletUtils.getUserLoginFromSession(session); 
						PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();
						boolean [] permessi = new boolean[2];
						permessi[0] = pu.isServizi();
						permessi[1] = pu.isAccordiCooperazione();
						List<AccordoServizioParteSpecifica> lista2 = null;
						if(apsCore.isVisioneOggettiGlobale(superUser)){
							lista2 = apsCore.soggettiServizioList(null, ricerca,permessi, gestioneFruitori, false);
						}else{
							lista2 = apsCore.soggettiServizioList(superUser, ricerca, permessi, gestioneFruitori, false);
						}

						apsHelper.prepareServiziList(ricerca, lista2);
						
					}
					else {
					
						idLista = Liste.SERVIZI_FRUITORI;
						ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
						
						List<Fruitore> listaFruitori = apsCore.serviziFruitoriList(idAspsInt, ricerca);
						apsHelper.prepareServiziFruitoriList(listaFruitori, idAsps, ricerca);
						
					}
				}
				else {
					idLista = Liste.CONFIGURAZIONE_FRUIZIONE;
					ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
					
					List<MappingFruizionePortaDelegata> listaMapping = apsCore.serviziFruitoriMappingList((long) Integer.parseInt(idFruizione), idSoggettoFruitore, idServizio, ricerca);
					apsHelper.serviziFruitoriMappingList(listaMapping, idAsps, idSoggFruitore, idSoggettoFruitore, idFruizione, ricerca); 
				}
				
				break;
			case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_SOGGETTO:
				idLista = Liste.PORTE_DELEGATE_BY_SOGGETTO;
				ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
				lista = porteDelegateCore.porteDelegateList(Integer.parseInt(idSoggFruitore), ricerca); 
				porteDelegateHelper.preparePorteDelegateList(ricerca, lista,idLista);
				break;
			case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE:
			default:
				idLista = Liste.PORTE_DELEGATE;
				ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
				lista = porteDelegateCore.porteDelegateList(null, ricerca);
				porteDelegateHelper.preparePorteDelegateList(ricerca, lista,idLista);
				break;
			}

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			ForwardParams fwP = ForwardParams.OTHER("");
			if(!porteDelegateHelper.isModalitaCompleta()) {
				fwP = PorteDelegateCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE;
			}
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CONNETTORE_DEFAULT, fwP);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping,	PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CONNETTORE_DEFAULT,	ForwardParams.OTHER(""));
		}  
	}
}
