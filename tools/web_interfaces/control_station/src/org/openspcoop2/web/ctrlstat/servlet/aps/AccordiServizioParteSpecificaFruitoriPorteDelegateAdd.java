/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

import java.util.ArrayList;
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
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.config.Subscription;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateHelper;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
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
 * AccordiServizioParteSpecificaPorteDelegateAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaFruitoriPorteDelegateAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session);
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

		try {
			boolean multitenant = ServletUtils.getUserFromSession(session).isPermitMultiTenant(); 
			
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			String idAsps = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			String idFruizione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID);
			Long idFru = Long.parseLong(idFruizione);
			
			String idSoggFruitoreDelServizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO);
			Long idSoggFru = Long.parseLong(idSoggFruitoreDelServizio);
			
			//String azione = apsHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
			String [] azioni = apsHelper.getParameterValues(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONI);
			String nome = apsHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME);
		
			String modeCreazione = apsHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_CREAZIONE);
			String modeCreazioneConnettore = apsHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_CREAZIONE_CONNETTORE);
			String identificazione = apsHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_IDENTIFICAZIONE);
			String mappingPD = apsHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MAPPING);

			String fruizioneServizioApplicativo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUIZIONE_NOME_SA);
			String fruizioneRuolo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_RUOLO);
			String fruizioneAutenticazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE);
			String fruizioneAutenticazioneOpzionale = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE_OPZIONALE);
			String fruizioneAutorizzazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE);
			String fruizioneAutorizzazioneAutenticati = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_AUTENTICAZIONE);
			String fruizioneAutorizzazioneRuoli = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLI);
			String fruizioneAutorizzazioneRuoliTipologia = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_TIPOLOGIA);
			String fruizioneAutorizzazioneRuoliMatch = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_MATCH);

			String nomeSA = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SA);
			
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
			
			String autorizzazione_tokenOptions = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
			String autorizzazioneScope = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
			String autorizzazioneScopeMatch = apsHelper.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
			String scope = apsHelper.getParameter(CostantiControlStation.PARAMETRO_SCOPE);
			
			Properties parametersPOST = null;
			
			String endpointtype = apsHelper.readEndPointType();
			String tipoconn = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO );
			String autenticazioneHttp = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);

			String connettoreDebug = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);

			// proxy
			String proxy_enabled = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxy_hostname = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxy_port = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxy_username = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxy_password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);

			// tempi risposta
			String tempiRisposta_enabled = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			String tempiRisposta_connectionTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			String tempiRisposta_readTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			String tempiRisposta_tempoMedioRisposta = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
			
			// opzioni avanzate
			String transfer_mode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transfer_mode_chunk_size = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirect_mode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirect_max_hop = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(apsHelper, transfer_mode, redirect_mode);

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

			Connettore conTmp = null;
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_PORTA_APPLICATIVA_ADD, apsHelper, 
							parametersPOST, (endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro

			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			
			// Preparo il menu
			apsHelper.makeMenu();

			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(porteDelegateCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(porteDelegateCore);
			int idServizio = Integer.parseInt(idAsps);
			AccordoServizioParteSpecifica asps  =apsCore.getAccordoServizioParteSpecifica(idServizio);
			IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
			IDSoggetto idSoggettoFruitore = new IDSoggetto();
			String tipoSoggettoFruitore = null;
			String nomeSoggettoFruitore = null;
			if(apsCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggettoFruitore = soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggFruitoreDelServizio));
				tipoSoggettoFruitore = soggettoFruitore.getTipo();
				nomeSoggettoFruitore = soggettoFruitore.getNome();
			}else{
				org.openspcoop2.core.config.Soggetto soggettoFruitore = soggettiCore.getSoggetto(Integer.parseInt(idSoggFruitoreDelServizio));
				tipoSoggettoFruitore = soggettoFruitore.getTipo();
				nomeSoggettoFruitore = soggettoFruitore.getNome();
			}
			idSoggettoFruitore.setTipo(tipoSoggettoFruitore);
			idSoggettoFruitore.setNome(nomeSoggettoFruitore);
			List<MappingFruizionePortaDelegata> listaMappingFruizione = apsCore.serviziFruitoriMappingList(idFru, idSoggettoFruitore, idServizio2, null);
			
			MappingFruizionePortaDelegata mappingSelezionato = null, mappingDefault = null;

			String mappingLabel = "";
			String[] listaMappingLabels = null;
			String[] listaMappingValues = null;
			List<String> azioniOccupate = new ArrayList<>();
			String nomeNuovaConfigurazione = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_AZIONE_SPECIFIC_PREFIX + "1";
			int idxConfigurazione = 0;
			int listaMappingFruizioneSize = listaMappingFruizione != null ? listaMappingFruizione.size() : 0;
			if(listaMappingFruizioneSize > 0) {
				for (int i = 0; i < listaMappingFruizione.size(); i++) {
					MappingFruizionePortaDelegata mappingFruizionePortaDelegata = listaMappingFruizione.get(i);
					if(mappingFruizionePortaDelegata.isDefault()) {
						mappingDefault = mappingFruizionePortaDelegata;
						break;
					}
				}
				
				if(mappingPD != null) {
					for (int i = 0; i < listaMappingFruizione.size(); i++) {
						MappingFruizionePortaDelegata mappingFruizionePortaDelegata = listaMappingFruizione.get(i);
						if(mappingFruizionePortaDelegata.getNome().equals(mappingPD)) {
							mappingSelezionato = mappingFruizionePortaDelegata;
							break;
						}
					}
				}

				if(mappingSelezionato == null) {
					mappingSelezionato = mappingDefault;
				}
				
				if(!mappingSelezionato.isDefault()) {
					PortaDelegata pdMapping = porteDelegateCore.getPortaDelegata(mappingSelezionato.getIdPortaDelegata());
					mappingLabel = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(pdMapping,Integer.MAX_VALUE);
				}

				listaMappingLabels = new String[listaMappingFruizioneSize];
				listaMappingValues = new String[listaMappingFruizioneSize];
				for (int i = 0; i < listaMappingFruizione.size(); i++) {
					MappingFruizionePortaDelegata mappingFruizionePortaDelegata = listaMappingFruizione.get(i);
					//String nomeMappingNoDefault = mappingFruizionePortaDelegata.getNome();
					String nomeMappingNoDefault = null;
					if(!mappingFruizionePortaDelegata.isDefault()) {
						PortaDelegata pdMapping = porteDelegateCore.getPortaDelegata(mappingFruizionePortaDelegata.getIdPortaDelegata());
						nomeMappingNoDefault = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(pdMapping,70);
					}
					listaMappingLabels[i] = mappingFruizionePortaDelegata.isDefault()? 
							PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_NOME_DEFAULT: nomeMappingNoDefault;
					listaMappingValues[i] = mappingFruizionePortaDelegata.getNome();
					
					// calcolo del nome automatico
					if(!mappingFruizionePortaDelegata.isDefault())  {
						int idx = mappingFruizionePortaDelegata.getNome().indexOf(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_AZIONE_SPECIFIC_PREFIX);
						if(idx > -1) {
							String idxTmp = mappingFruizionePortaDelegata.getNome().substring(idx + PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_AZIONE_SPECIFIC_PREFIX.length());
							int idxMax = -1;
							try {
								idxMax = Integer.parseInt(idxTmp);
							}catch(Exception e) {
								idxMax = 0;
							}
							idxConfigurazione = Math.max(idxConfigurazione, idxMax);
						}
					}
					
					// colleziono le azioni gia' configurate
					PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(mappingFruizionePortaDelegata.getIdPortaDelegata());
					if(portaDelegata.getAzione() != null && portaDelegata.getAzione().getAzioneDelegataList() != null)
						azioniOccupate.addAll(portaDelegata.getAzione().getAzioneDelegataList());
				}
			}
			
			nomeNuovaConfigurazione = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_AZIONE_SPECIFIC_PREFIX + (++ idxConfigurazione);

			AccordoServizioParteComune as = null;
			ServiceBinding serviceBinding = null;
			if (asps != null) {
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
				as = apcCore.getAccordoServizio(idAccordo);
				serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
			}

			// Prendo le azioni  disponibili
			boolean addTrattinoSelezioneNonEffettuata = false;
			int sogliaAzioni = addTrattinoSelezioneNonEffettuata ? 1 : 0;
			Map<String,String> azioniS = porteDelegateCore.getAzioniConLabel(asps, as, addTrattinoSelezioneNonEffettuata, true, azioniOccupate);
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
			
			String postBackElementName = apsHelper.getPostBackElementName();
			boolean initConnettore = false;
			// Controllo se ho modificato l'azione allora ricalcolo il nome
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_CREAZIONE_CONNETTORE)){
					// devo resettare il connettore
					if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
						initConnettore = true;
					}
				}
			}
			
			// ServiziApplicativi
			List<String> saList = new ArrayList<String>();
			saList.add("-");
			if(idSoggettoFruitore!=null){
				try{
					String auth = fruizioneAutenticazione;
					if(auth==null || "".equals(auth)){
						auth = apsCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
					}
					List<ServizioApplicativo> oldSilList = null;
					if(apsCore.isVisioneOggettiGlobale(userLogin)){
						oldSilList = saCore.soggettiServizioApplicativoList(idSoggettoFruitore,null,
								CredenzialeTipo.toEnumConstant(auth));
					}
					else {
						oldSilList = saCore.soggettiServizioApplicativoList(idSoggettoFruitore,userLogin,
								CredenzialeTipo.toEnumConstant(auth));
					}
					if(oldSilList!=null && oldSilList.size()>0){
						for (int i = 0; i < oldSilList.size(); i++) {
							saList.add(oldSilList.get(i).getNome());		
						}
					}
				}catch(DriverConfigurazioneNotFound dNotFound){}

			}

			List<Parameter> lstParm = porteDelegateHelper.getTitoloPD(parentPD, idSoggFruitoreDelServizio, idAsps, idFruizione);

			lstParm.add(ServletUtils.getParameterAggiungi());
			
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
			if (apsHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd,lstParm); 

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				if(azioniDisponibiliList==null || azioniDisponibiliList.length <= sogliaAzioni) {
					pd.setMessage(porteDelegateHelper.getLabelAllAzioniConfigurate(serviceBinding), Costanti.MESSAGE_TYPE_INFO);
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
						modeCreazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_EREDITA;
										
					if(modeCreazioneConnettore == null) {
						modeCreazioneConnettore = Costanti.CHECK_BOX_DISABLED;
					}
						
					if(mappingPD==null) {
						mappingPD = listaMappingValues[listaMappingValues.length-1]; // sono ordinati all'incontrario
					}
						
					if(fruizioneServizioApplicativo==null || "".equals(fruizioneServizioApplicativo))
						fruizioneServizioApplicativo = "-";
					if(fruizioneRuolo==null || "".equals(fruizioneRuolo))
						fruizioneRuolo = "-";
					if(fruizioneAutenticazione==null || "".equals(fruizioneAutenticazione))
						fruizioneAutenticazione = apsCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
					if(fruizioneAutorizzazione==null || "".equals(fruizioneAutorizzazione)){
						String tipoAutorizzazione = apsCore.getAutorizzazione_generazioneAutomaticaPorteDelegate();
						fruizioneAutorizzazione = AutorizzazioneUtilities.convertToStato(tipoAutorizzazione);
						if(TipoAutorizzazione.isAuthenticationRequired(tipoAutorizzazione))
							fruizioneAutorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
						if(TipoAutorizzazione.isRolesRequired(tipoAutorizzazione))
							fruizioneAutorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
						fruizioneAutorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(tipoAutorizzazione).getValue();
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
							if(apsHelper.isModalitaCompleta()==false) {
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

						autenticazioneHttp = apsHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);
						
						tempiRisposta_enabled=null;
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
					
	
					dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idAsps, idSoggFruitoreDelServizio, null, null, idFruizione, dati);
					dati = apsHelper.addConfigurazioneFruizioneToDati(TipoOperazione.ADD, dati, nome, azioni, azioniDisponibiliList, azioniDisponibiliLabelList, idAsps, idSoggettoFruitore,
							identificazione, asps, as, serviceBinding, modeCreazione, modeCreazioneConnettore, listaMappingLabels, listaMappingValues,
							mappingPD, mappingLabel, saList, nomeSA, fruizioneAutenticazione, fruizioneAutenticazioneOpzionale, 
							true, fruizioneAutorizzazione, fruizioneAutorizzazioneAutenticati, 
							fruizioneAutorizzazioneRuoli, fruizioneRuolo, fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch, fruizioneServizioApplicativo,
							gestioneToken, policyLabels, policyValues, 
							gestioneTokenPolicy, gestioneTokenOpzionale,
							gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
							autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
							autorizzazione_tokenOptions,
							autorizzazioneScope,scope,autorizzazioneScopeMatch);
					
					if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
						dati = apsHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, 
								(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX , 
								url, nomeCodaJms,
								tipoJms, user,
								password, initcont, urlpgk,
								provurl, connfact, tipoSendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,TipoOperazione.ADD, httpsurl, httpstipologia,
								httpshostverify, httpspath, httpstipo, httpspwd,
								httpsalgoritmo, httpsstato, httpskeystore,
								httpspwdprivatekeytrust, httpspathkey,
								httpstipokey, httpspwdkey, httpspwdprivatekey,
								httpsalgoritmokey, tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_ADD, null, null,
								null, null, null, null, null, null, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
								tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
								opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
								requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
								responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
								listExtendedConnettore, forceEnableConnettore);
					}
				}
					
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE,
						ForwardParams.ADD());

			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.configurazioneFruizioneCheckData(TipoOperazione.ADD, nome, azioni, asps, azioniOccupate,modeCreazione,null,true);
			
			// controllo endpoint
			if(isOk && ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
				isOk = apsHelper.endPointCheckData(endpointtype, url, nome, tipoJms,
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
			
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd,lstParm); 

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idAsps, idSoggFruitoreDelServizio, null, null, idFruizione, dati);

				dati = apsHelper.addConfigurazioneFruizioneToDati(TipoOperazione.ADD, dati, nome, azioni, azioniDisponibiliList, azioniDisponibiliLabelList, idAsps, idSoggettoFruitore,
						identificazione, asps, as, serviceBinding, modeCreazione, modeCreazioneConnettore, listaMappingLabels, listaMappingValues,
						mappingPD, mappingLabel, saList, nomeSA, fruizioneAutenticazione, fruizioneAutenticazioneOpzionale, 
						true, fruizioneAutorizzazione, fruizioneAutorizzazioneAutenticati, 
						fruizioneAutorizzazioneRuoli, fruizioneRuolo, fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch, fruizioneServizioApplicativo,
						gestioneToken, policyLabels, policyValues, 
						gestioneTokenPolicy, gestioneTokenOpzionale,
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_tokenOptions,
						autorizzazioneScope,scope,autorizzazioneScopeMatch);
				
				if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
					dati = apsHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, 
							(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX , 
							url, nomeCodaJms,
							tipoJms, user,
							password, initcont, urlpgk,
							provurl, connfact, tipoSendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,TipoOperazione.ADD, httpsurl, httpstipologia,
							httpshostverify, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey, httpspwdprivatekey,
							httpsalgoritmokey, tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_ADD, null, null,
							null, null, null, null, null, null, true,
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

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE, 
						ForwardParams.ADD());
			}

			List<Object> listaOggettiDaCreare = new ArrayList<Object>();
			List<Object> listaOggettiDaModificare = new ArrayList<Object>();

			PortaDelegata portaDelegataDefault = porteDelegateCore.getPortaDelegata(mappingDefault.getIdPortaDelegata());
			String protocollo = apsCore.getProtocolloAssociatoTipoServizio(idServizio2.getTipo());
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
		
			Connettore connettore = null;
			if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
				connettore = new Connettore();
				// this.nomeservizio);
				if (endpointtype.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM))
					connettore.setTipo(tipoconn);
				else
					connettore.setTipo(endpointtype);

				apsHelper.fillConnettore(connettore, connettoreDebug, endpointtype, endpointtype, tipoconn, url,
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
			}
			
			
			Subscription subscription = null;
			if(modeCreazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_EREDITA)) {
				PortaDelegata portaDelegataDaCopiare = porteDelegateCore.getPortaDelegata(mappingSelezionato.getIdPortaDelegata());
				subscription = protocolFactory.createProtocolIntegrationConfiguration().createSubscription(serviceBinding, idSoggettoFruitore, idServizio2, 
						portaDelegataDefault, portaDelegataDaCopiare, nome, azioni);
				
			}
			else {
				subscription = protocolFactory.createProtocolIntegrationConfiguration().createSubscription(serviceBinding, idSoggettoFruitore, idServizio2, 
						portaDelegataDefault, nome, azioni);
				
			}
			
			if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
				
				Fruitore fruitore = null;
				for (Fruitore fruitoreCheck : asps.getFruitoreList()) {
					if(fruitoreCheck.getTipo().equals(tipoSoggettoFruitore) && fruitoreCheck.getNome().equals(nomeSoggettoFruitore)) {
						fruitore = fruitoreCheck;
						break;
					}
				}
				
				ConfigurazioneServizioAzione configurazioneAzione = new ConfigurazioneServizioAzione();
				configurazioneAzione.setConnettore(connettore);
				for (int i = 0; i < azioni.length; i++) {
					configurazioneAzione.addAzione(azioni[i]);
				}
				fruitore.addConfigurazioneAzione(configurazioneAzione);
			}
			
			PortaDelegata portaDelegata = subscription.getPortaDelegata();
			MappingFruizionePortaDelegata mappingFruizione = subscription.getMapping();
			portaDelegata.setIdSoggetto((long) idSoggFru);
			
			if(!modeCreazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_EREDITA)) {
				porteDelegateCore.configureControlloAccessiPortaDelegata(portaDelegata, 
						fruizioneAutenticazione, fruizioneAutenticazioneOpzionale,
						fruizioneAutorizzazione, fruizioneAutorizzazioneAutenticati, fruizioneAutorizzazioneRuoli, fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch,
						fruizioneServizioApplicativo, fruizioneRuolo,
						autorizzazione_tokenOptions,
						autorizzazioneScope,scope,autorizzazioneScopeMatch);
				
				porteDelegateCore.configureControlloAccessiGestioneToken(portaDelegata, gestioneToken, 
						gestioneTokenPolicy, gestioneTokenOpzionale,
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_tokenOptions
						);
			}
			
			listaOggettiDaCreare.add(portaDelegata);
			listaOggettiDaCreare.add(mappingFruizione);

			porteDelegateCore.performCreateOperation(userLogin, porteDelegateHelper.smista(), listaOggettiDaCreare.toArray());

			if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
				
				listaOggettiDaModificare.add(asps);
				
			}
			
			if(listaOggettiDaModificare.size()>0) {
				porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), listaOggettiDaModificare.toArray());
			}
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_FRUIZIONE;

			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);

			List<MappingFruizionePortaDelegata> lista = apsCore.serviziFruitoriMappingList(idFru, idSoggettoFruitore , idServizio2, ricerca);
			
			apsHelper.serviziFruitoriMappingList(lista, idAsps, idSoggFruitoreDelServizio, idFruizione, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE, 
					ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE,
					ForwardParams.ADD());
		}  
	}
}
