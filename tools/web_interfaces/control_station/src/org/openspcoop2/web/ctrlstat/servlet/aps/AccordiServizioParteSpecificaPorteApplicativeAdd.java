/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.config.Implementation;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeHelper;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
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

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;

		try {
			boolean multitenant = ServletUtils.getUserFromSession(session).isPermitMultiTenant(); 
			
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			String idAsps = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			String idSoggettoErogatoreDelServizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			if ((idSoggettoErogatoreDelServizio == null) || idSoggettoErogatoreDelServizio.equals("")) {
				PageData oldPD = ServletUtils.getPageDataFromSession(session);

				idSoggettoErogatoreDelServizio = oldPD.getHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			}
			String[] azioni = apsHelper.getParameterValues(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONI);
			
			String nome = apsHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME);
			String modeCreazione = apsHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CREAZIONE);
			String modeCreazioneConnettore = apsHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CREAZIONE_CONNETTORE);
			String identificazione = apsHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_IDENTIFICAZIONE);
			String mappingPA = apsHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MAPPING);

			String erogazioneRuolo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_RUOLO);
			String erogazioneAutenticazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE);
			String erogazioneAutenticazioneOpzionale = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE_OPZIONALE);
			String erogazioneAutorizzazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE);
			String erogazioneAutorizzazioneAutenticati = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_AUTENTICAZIONE);
			String erogazioneAutorizzazioneRuoli = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLI);
			String erogazioneAutorizzazioneRuoliTipologia = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_TIPOLOGIA);
			String erogazioneAutorizzazioneRuoliMatch = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_MATCH);
			String erogazioneSoggettoAutenticato = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_SOGGETTO_AUTENTICATO);

			String nomeSA = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SA);
			
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

			String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneErogatori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
					gestioneErogatori = true;
				}
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
			int idServizio = Integer.parseInt(idAsps);
			AccordoServizioParteSpecifica asps  =apsCore.getAccordoServizioParteSpecifica(idServizio);
			IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
			int soggInt = Integer.parseInt(idSoggettoErogatoreDelServizio);
			List<MappingErogazionePortaApplicativa> listaMappingErogazione = apsCore.mappingServiziPorteAppList(idServizio2,asps.getId(), null);
			MappingErogazionePortaApplicativa mappingSelezionato = null, mappingDefault = null;

			String mappingLabel = "";
			String[] listaMappingLabels = null;
			String[] listaMappingValues = null;
			List<String> azioniOccupate = new ArrayList<>();
			String nomeNuovaConfigurazione = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_AZIONE_SPECIFIC_PREFIX + "1";
			int idxConfigurazione = 0;
			int listaMappingErogazioneSize = listaMappingErogazione != null ? listaMappingErogazione.size() : 0;
			if(listaMappingErogazioneSize > 0) {
				for (int i = 0; i < listaMappingErogazione.size(); i++) {
					MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = listaMappingErogazione.get(i);
					if(mappingErogazionePortaApplicativa.isDefault()) {
						mappingDefault = mappingErogazionePortaApplicativa;
						break;
					}
				}
				
				if(mappingPA != null) {
					for (int i = 0; i < listaMappingErogazione.size(); i++) {
						MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = listaMappingErogazione.get(i);
						if(mappingErogazionePortaApplicativa.getNome().equals(mappingPA)) {
							mappingSelezionato = mappingErogazionePortaApplicativa;
							break;
						}
					}
				}

				if(mappingSelezionato == null) {
					mappingSelezionato = mappingDefault;
				}

				if(!mappingSelezionato.isDefault()) {
					PortaApplicativa paMapping = porteApplicativeCore.getPortaApplicativa(mappingSelezionato.getIdPortaApplicativa());
					mappingLabel = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(paMapping,Integer.MAX_VALUE);
				}
				
				listaMappingLabels = new String[listaMappingErogazioneSize];
				listaMappingValues = new String[listaMappingErogazioneSize];
				for (int i = 0; i < listaMappingErogazione.size(); i++) {
					MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = listaMappingErogazione.get(i);
					//String nomeMappingNoDefault = mappingErogazionePortaApplicativa.getNome();
					String nomeMappingNoDefault = null;
					if(!mappingErogazionePortaApplicativa.isDefault()) {
						PortaApplicativa paMapping = porteApplicativeCore.getPortaApplicativa(mappingErogazionePortaApplicativa.getIdPortaApplicativa());
						nomeMappingNoDefault = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(paMapping,70);
					}
					listaMappingLabels[i] = mappingErogazionePortaApplicativa.isDefault()?
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_NOME_DEFAULT: nomeMappingNoDefault;
					listaMappingValues[i] = mappingErogazionePortaApplicativa.getNome();
					
					// calcolo del nome automatico
					if(!mappingErogazionePortaApplicativa.isDefault())  {
						int idx = mappingErogazionePortaApplicativa.getNome().indexOf(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_AZIONE_SPECIFIC_PREFIX);
						if(idx > -1) {
							String idxTmp = mappingErogazionePortaApplicativa.getNome().substring(idx + PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_AZIONE_SPECIFIC_PREFIX.length());
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
					PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(mappingErogazionePortaApplicativa.getIdPortaApplicativa());
					if(portaApplicativa.getAzione() != null && portaApplicativa.getAzione().getAzioneDelegataList() != null)
						azioniOccupate.addAll(portaApplicativa.getAzione().getAzioneDelegataList());
				}
			}
			
			nomeNuovaConfigurazione = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_AZIONE_SPECIFIC_PREFIX + ( ++ idxConfigurazione);

			// Prendo nome, tipo e pdd del soggetto
			String tipoSoggettoProprietario = null;
			if(porteApplicativeCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				tipoSoggettoProprietario = soggetto.getTipo();
			}else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				tipoSoggettoProprietario = soggetto.getTipo();
			}

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
			List<String> azioniS = porteApplicativeCore.getAzioni(asps, as, addTrattinoSelezioneNonEffettuata, true, azioniOccupate);
			String[] azioniDisponibiliList = null;
			if(azioniS!=null && azioniS.size()>0) {
				azioniDisponibiliList = new String[azioniS.size()];
				for (int i = 0; i < azioniS.size(); i++) {
					azioniDisponibiliList[i] = azioniS.get(i);
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
			}

			String [] saSoggetti = null;
			if ((idSoggettoErogatoreDelServizio != null) && !idSoggettoErogatoreDelServizio.equals("")) {
				int idErogatore = Integer.parseInt(idSoggettoErogatoreDelServizio);

				List<ServizioApplicativo> listaSA = saCore.getServiziApplicativiByIdErogatore(Long.valueOf(idErogatore));

				// rif bug #45
				// I servizi applicativi da visualizzare sono quelli che hanno
				// -Integration Manager (getMessage abilitato)
				// -connettore != disabilitato
				ArrayList<ServizioApplicativo> validSA = new ArrayList<ServizioApplicativo>();
				for (ServizioApplicativo sa : listaSA) {
					InvocazioneServizio invServizio = sa.getInvocazioneServizio();
					org.openspcoop2.core.config.Connettore connettore = invServizio != null ? invServizio.getConnettore() : null;
					StatoFunzionalita getMessage = invServizio != null ? invServizio.getGetMessage() : null;

					if ((connettore != null && !TipiConnettore.DISABILITATO.getNome().equals(connettore.getTipo())) || CostantiConfigurazione.ABILITATO.equals(getMessage)) {
						// il connettore non e' disabilitato oppure il get
						// message e' abilitato
						// Lo aggiungo solo se gia' non esiste tra quelli
						// aggiunti
						validSA.add(sa);
					}
				}

				// Prendo la lista di servizioApplicativo associati al soggetto
				// e la metto in un array
				saSoggetti = new String[validSA.size()+1];
				saSoggetti[0] = "-"; // elemento nullo di default
				for (int i = 0; i < validSA.size(); i++) {
					ServizioApplicativo sa = validSA.get(i);
					saSoggetti[i+1] = sa.getNome();
				}
			}
			
			List<String> soggettiAutenticati = new ArrayList<String>();
			List<String> soggettiAutenticatiLabel = new ArrayList<String>();
			// lista soggetti autenticati per la creazione automatica
			CredenzialeTipo credenziale =  null;
			if((erogazioneAutenticazione !=null && !"".equals(erogazioneAutenticazione)) && erogazioneIsSupportatoAutenticazioneSoggetti) {
				TipoAutenticazione tipoAutenticazione = TipoAutenticazione.toEnumConstant(erogazioneAutenticazione);
				credenziale = !tipoAutenticazione.equals(TipoAutenticazione.DISABILITATO) ? CredenzialeTipo.toEnumConstant(erogazioneAutenticazione) : null;
			}
			
			List<org.openspcoop2.core.registry.Soggetto> listSoggettiCompatibili = null;
			 
			if(apsCore.isVisioneOggettiGlobale(userLogin)){
				listSoggettiCompatibili = soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiCompatibiliAccordo, null, credenziale, pddTipologiaSoggettoAutenticati );
			}else{
				listSoggettiCompatibili = soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiCompatibiliAccordo, userLogin, credenziale, pddTipologiaSoggettoAutenticati);
			}
			
			if(listSoggettiCompatibili != null && listSoggettiCompatibili.size() >0 ) {
				
				soggettiAutenticati.add("-"); // elemento nullo di default
				soggettiAutenticatiLabel.add("-");
				for (Soggetto soggetto : listSoggettiCompatibili) {
					soggettiAutenticati.add(soggetto.getTipo() + "/"+ soggetto.getNome());
					soggettiAutenticatiLabel.add(apsHelper.getLabelNomeSoggetto(protocollo, soggetto.getTipo(), soggetto.getNome())); 
					
				}
			}

			List<Parameter> lstParm = porteApplicativeHelper.getTitoloPA(parentPA, idSoggettoErogatoreDelServizio, idAsps);

			lstParm.add(ServletUtils.getParameterAggiungi());

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (apsHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd,lstParm); 

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

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
						
						soggettiAutenticati = new ArrayList<String>();
						soggettiAutenticatiLabel = new ArrayList<String>();
						if(erogazioneIsSupportatoAutenticazioneSoggetti) {
							TipoAutenticazione tipoAutenticazione = TipoAutenticazione.toEnumConstant(erogazioneAutenticazione);
							credenziale = !tipoAutenticazione.equals(TipoAutenticazione.DISABILITATO) ? CredenzialeTipo.toEnumConstant(erogazioneAutenticazione) : null;
						}
						 
						if(apsCore.isVisioneOggettiGlobale(userLogin)){
							listSoggettiCompatibili = soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiCompatibiliAccordo, null, credenziale, pddTipologiaSoggettoAutenticati );
						}else{
							listSoggettiCompatibili = soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiCompatibiliAccordo, userLogin, credenziale, pddTipologiaSoggettoAutenticati);
						}
						
						if(listSoggettiCompatibili != null && listSoggettiCompatibili.size() >0 ) {
							soggettiAutenticati.add("-"); // elemento nullo di default
							soggettiAutenticatiLabel.add("-");
							for (Soggetto soggetto : listSoggettiCompatibili) {
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
						ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloCongestione();
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
						ConfigurazioneCore configCore = new ConfigurazioneCore(soggettiCore);
						ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloCongestione();
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
					
	
					dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idAsps, null, null, dati);
					dati = apsHelper.addConfigurazioneErogazioneToDati(TipoOperazione.ADD, dati, nome, azioni, azioniDisponibiliList, idAsps, idSoggettoErogatoreDelServizio,
							identificazione, asps, as, serviceBinding, modeCreazione, modeCreazioneConnettore, listaMappingLabels, listaMappingValues,
							mappingPA, mappingLabel, nomeSA, saSoggetti, erogazioneAutenticazione, erogazioneAutenticazioneOpzionale, 
							erogazioneIsSupportatoAutenticazioneSoggetti, erogazioneAutorizzazione, erogazioneAutorizzazioneAutenticati, 
							erogazioneAutorizzazioneRuoli, erogazioneRuolo, erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch,soggettiAutenticati,soggettiAutenticatiLabel,erogazioneSoggettoAutenticato);
					
//					apsHelper.isModalitaCompleta()?null:(generaPACheckSoggetto?AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX : AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX)
					
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

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,
						ForwardParams.ADD());

			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.configurazioneErogazioneCheckData(TipoOperazione.ADD, nome, azioni, asps, azioniOccupate,modeCreazione,null,erogazioneIsSupportatoAutenticazioneSoggetti);
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

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idAsps, null, null, dati);

				dati = apsHelper.addConfigurazioneErogazioneToDati(TipoOperazione.ADD, dati, nome, azioni, azioniDisponibiliList, idAsps, idSoggettoErogatoreDelServizio,
						identificazione, asps, as, serviceBinding, modeCreazione, modeCreazioneConnettore, listaMappingLabels, listaMappingValues,
						mappingPA, mappingLabel, nomeSA, saSoggetti, erogazioneAutenticazione, erogazioneAutenticazioneOpzionale, 
						erogazioneIsSupportatoAutenticazioneSoggetti, erogazioneAutorizzazione, erogazioneAutorizzazioneAutenticati, 
						erogazioneAutorizzazioneRuoli, erogazioneRuolo, erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch,soggettiAutenticati,soggettiAutenticatiLabel,erogazioneSoggettoAutenticato);
				
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

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE, 
						ForwardParams.ADD());
			}

			List<Object> listaOggettiDaCreare = new ArrayList<Object>();

			PortaApplicativa portaApplicativaDefault = porteApplicativeCore.getPortaApplicativa(mappingDefault.getIdPortaApplicativa());
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			Implementation implementation = null;
			
			if(modeCreazione.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_EREDITA)) {
				PortaApplicativa portaApplicativaDaCopiare = porteApplicativeCore.getPortaApplicativa(mappingSelezionato.getIdPortaApplicativa());
				implementation = protocolFactory.createProtocolIntegrationConfiguration().createImplementation(serviceBinding, idServizio2, 
						portaApplicativaDefault, portaApplicativaDaCopiare, nome, azioni);
			}
			else {
				implementation = protocolFactory.createProtocolIntegrationConfiguration().createImplementation(serviceBinding, idServizio2, 
						portaApplicativaDefault, nome, azioni);
			}
			
			PortaApplicativa portaApplicativa = implementation.getPortaApplicativa();
			MappingErogazionePortaApplicativa mappingErogazione = implementation.getMapping();
			portaApplicativa.setIdSoggetto((long) soggInt);
			
			Connettore connettore = null;
			if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
				// Connettore
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
			
			if(!modeCreazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_EREDITA)) {
							
				String nomeServizioApplicativoErogatore = portaApplicativaDefault.getServizioApplicativo(0).getNome();
				
				if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
									
					nomeServizioApplicativoErogatore = portaApplicativa.getNome();
					
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
				}
				
				IDSoggetto idSoggettoAutenticatoErogazione = null;
				if(erogazioneSoggettoAutenticato != null && !"".equals(erogazioneSoggettoAutenticato) && !"-".equals(erogazioneSoggettoAutenticato)) {
					String [] splitSoggetto = erogazioneSoggettoAutenticato.split("/");
					if(splitSoggetto != null) {
						idSoggettoAutenticatoErogazione = new IDSoggetto();
						if(splitSoggetto.length == 2) {
							idSoggettoAutenticatoErogazione.setTipo(splitSoggetto[0]);
							idSoggettoAutenticatoErogazione.setNome(splitSoggetto[1]);
						} else {
							idSoggettoAutenticatoErogazione.setNome(splitSoggetto[0]);
						}
					}
				}
				
				porteApplicativeCore.configureControlloAccessiPortaApplicativa(portaApplicativa,
						erogazioneAutenticazione, erogazioneAutenticazioneOpzionale,
						erogazioneAutorizzazione, erogazioneAutorizzazioneAutenticati, erogazioneAutorizzazioneRuoli, erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch,
						nomeServizioApplicativoErogatore, erogazioneRuolo,idSoggettoAutenticatoErogazione);
				
			}
			else {
				
				portaApplicativa.getServizioApplicativoList().clear();
				
				if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
					PortaApplicativa portaApplicativaSelezionata = porteApplicativeCore.getPortaApplicativa(mappingSelezionato.getIdPortaApplicativa());
					for (PortaApplicativaServizioApplicativo paSADefault : portaApplicativaSelezionata.getServizioApplicativoList()) {
						IDServizioApplicativo idServizioApplicativoDefault = new IDServizioApplicativo();
						idServizioApplicativoDefault.setNome(paSADefault.getNome());
						idServizioApplicativoDefault.setIdSoggettoProprietario(new IDSoggetto(portaApplicativaSelezionata.getTipoSoggettoProprietario(), portaApplicativaSelezionata.getNomeSoggettoProprietario()));
						ServizioApplicativo saDefault = saCore.getServizioApplicativo(idServizioApplicativoDefault);
						ServizioApplicativo sa = (ServizioApplicativo) saDefault.clone();
						sa.setNome(portaApplicativa.getNome());
						sa.getInvocazioneServizio().setConnettore(connettore.mappingIntoConnettoreConfigurazione());
						PortaApplicativaServizioApplicativo paSa = new PortaApplicativaServizioApplicativo();
						paSa.setNome(sa.getNome());
						portaApplicativa.getServizioApplicativoList().add(paSa);
						listaOggettiDaCreare.add(sa);
					}
				}
				else {
					
					// assegno sempre il connettore della pa di default in caso di eredita'
					for (PortaApplicativaServizioApplicativo paSADefault : portaApplicativaDefault.getServizioApplicativoList()) {
						PortaApplicativaServizioApplicativo paSa = new PortaApplicativaServizioApplicativo();
						paSa.setNome(paSADefault.getNome());
						portaApplicativa.getServizioApplicativoList().add(paSa);
					}
					
				}
			}
			
			listaOggettiDaCreare.add(portaApplicativa);
			listaOggettiDaCreare.add(mappingErogazione);
			

			porteApplicativeCore.performCreateOperation(userLogin, porteApplicativeHelper.smista(), listaOggettiDaCreare.toArray());

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_EROGAZIONE;

			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);

			List<MappingErogazionePortaApplicativa> lista = apsCore.mappingServiziPorteAppList(idServizio2,asps.getId(), ricerca);

			apsHelper.prepareServiziConfigurazioneList(lista, idAsps, null, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE, 
					ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,
					ForwardParams.ADD());
		}  
	}
}
