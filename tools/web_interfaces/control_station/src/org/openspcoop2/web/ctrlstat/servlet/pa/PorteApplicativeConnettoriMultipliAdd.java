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


package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore;
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
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils;
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
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiHelper;
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
 * PorteApplicativeConnettoriMultipliAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeConnettoriMultipliAdd extends Action {

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
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session, request);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
			
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			String idConnTab = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_CONN_TAB);
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

			String invrifRichiesta = porteApplicativeHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RICHIESTA);

			String risprif = porteApplicativeHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);

			Properties parametersPOST = null;

			String endpointtype = porteApplicativeHelper.readEndPointType();
			String tipoconn = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO );
			String autenticazioneHttp = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);

			String connettoreDebug = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);

			// token policy
			String autenticazioneTokenS = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			boolean autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			String tokenPolicy = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
			boolean forcePDND = false;
			boolean forceOAuth = false;

			// proxy
			String proxyEnabled = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxyHostname = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxyPort = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxyUsername = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxyPassword = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);

			// tempi risposta
			String tempiRispostaEnabled = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			String tempiRispostaConnectionTimeout = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			String tempiRispostaReadTimeout = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			String tempiRispostaTempoMedioRisposta = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);

			// opzioni avanzate
			String transferMode = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transferModeChunkSize = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirectMode = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirectMaxHop = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(porteApplicativeHelper, transferMode, redirectMode);

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
			String httpsTrustVerifyCertS = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS );
			boolean httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
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
			String httpsKeyAlias = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
			String httpsTrustStoreCRLs = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
			String httpsTrustStoreOCSPPolicy = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = porteApplicativeHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
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

			String erogazioneServizioApplicativoServerEnabledS = porteApplicativeHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ABILITA_USO_APPLICATIVO_SERVER);
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
			String accessoDaAPSParametro = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			if(Costanti.CHECK_BOX_ENABLED_TRUE.equals(accessoDaAPSParametro)) {
				accessoDaListaAPS = true;
			}
			
			boolean isModalitaCompleta = porteApplicativeHelper.isModalitaCompleta();
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session, request).getValue();
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

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
			
			boolean integrationManagerEnabled = !porteApplicativeHelper.isModalitaStandard() && porteApplicativeCore.isIntegrationManagerEnabled();
			boolean isSoapOneWay = false;
			if(pa!=null) {
				MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = porteApplicativeCore.getMappingErogazionePortaApplicativa(pa);
				isSoapOneWay = porteApplicativeHelper.isSoapOneWay(pa, mappingErogazionePortaApplicativa, asps, apc, serviceBinding);
			}
			if(integrationManagerEnabled) {
				if(TipoBehaviour.CONSEGNA_CON_NOTIFICHE.equals(beaBehaviourType)) {
					integrationManagerEnabled = true; // l'integration manager e' abilitato solamente se e' il connettore adibito alle notifiche; siccome siamo in add sicuramente sar' un nuovo connettore per le notifiche
				}
				else {
					integrationManagerEnabled = isSoapOneWay; // l'integration manager e' abilitato solamente se c'e' almeno una azione oneway in api soap
				}
			}
						
			boolean isApplicativiServerEnabled = apsCore.isApplicativiServerEnabled(porteApplicativeHelper);
			String filtroTipoSA = isApplicativiServerEnabled ? ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER : null;
			
			String nomeProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(pa.getTipoSoggettoProprietario());
			
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
			
			boolean initConnettore = false;
			if(postBackElementName != null &&
				postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CONNETTORE_ABILITA_USO_APPLICATIVO_SERVER) &&
				// devo resettare il connettore se passo da SA Server a Default
				(!erogazioneServizioApplicativoServerEnabled)
				) {
				tipoauthRichiesta = ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC;
				initConnettore = true;
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
			String idTabP = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			Parameter pIdTab = new Parameter(CostantiControlStation.PARAMETRO_ID_TAB, idTabP != null ? idTabP : "");
			Parameter pAccessoDaAPS = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, accessoDaAPSParametro != null ? accessoDaAPSParametro : "");
			String connettoreAccessoGruppi = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI);
			String connettoreRegistro = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO);
			Parameter pConnettoreAccessoDaGruppi = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, connettoreAccessoGruppi);
			Parameter pConnettoreAccesso = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, connettoreRegistro);
			
			listParametersConfigutazioneConnettoriMultipli.add(pIdSogg);
			listParametersConfigutazioneConnettoriMultipli.add(pIdPorta);
			listParametersConfigutazioneConnettoriMultipli.add(pNomePorta);
			listParametersConfigutazioneConnettoriMultipli.add(pIdAsps);
			listParametersConfigutazioneConnettoriMultipli.add(pIdTab);
			listParametersConfigutazioneConnettoriMultipli.add(pAccessoDaAPS);
			listParametersConfigutazioneConnettoriMultipli.add(pConnettoreAccessoDaGruppi);
			listParametersConfigutazioneConnettoriMultipli.add(pConnettoreAccesso);
			
			lstParam.add(new Parameter(labelPerPorta,  PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LIST, listParametersConfigutazioneConnettoriMultipli.toArray(new Parameter[1])));
			lstParam.add(ServletUtils.getParameterAggiungi());

			// Se nome = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				if(nomeConnettore == null) {
					nomeConnettore = "";
					descrizioneConnettore = "";
					filtriConnettore = "";
					statoConnettore = StatoFunzionalita.ABILITATO.getValue();
					initConnettore = true;
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
						/**httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;*/
						httpshostverify = true;
					}
					if(httpsTrustVerifyCertS==null || "".equals(httpsTrustVerifyCertS)){
						/**httpsTrustVerifyCertS = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS ? Costanti.CHECK_BOX_ENABLED_TRUE : Costanti.CHECK_BOX_DISABLED;*/
						httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
					}
				}
				
				// Devo cmq rileggere i valori se non definiti
				ConfigurazioneCore configCore = new ConfigurazioneCore(porteApplicativeCore);
				ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
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
				
				

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addConnettoriMultipliToDati(dati, TipoOperazione.ADD, beaBehaviourType, nomeSAConnettore,
						nomeConnettore, descrizioneConnettore, statoConnettore, behaviourConFiltri, filtriConnettore, null, null, null, null,
						null);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta,idAsps, dati);
				
				dati = porteApplicativeHelper.addInformazioniGruppiAsHiddenToDati(TipoOperazione.ADD, dati, idTabP, idConnTab, accessoDaAPSParametro != null ? accessoDaAPSParametro : "", 
						connettoreAccessoGruppi, connettoreRegistro, null);
				
				porteApplicativeHelper.addEndPointToDati(dati,idsil,nomeservizioApplicativo,sbustamento,sbustamentoInformazioniProtocolloRichiesta,
						getmsg,getmsgUsername,getmsgPassword,true,
						invrifRichiesta,risprif,nomeProtocollo,true,true, true,
						parentPA,serviceBinding, accessoDaAPSParametro, erogazioneServizioApplicativoServerEnabled,
						null, false,
						integrationManagerEnabled,
						TipoOperazione.ADD, null,null);
				
				dati = porteApplicativeHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, 
						null, //(porteApplicativeHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX , 
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
						tipoconn, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_ADD, null, null,
						null, null, null, null, null, null, true,
						isConnettoreCustomUltimaImmagineSalvata, 
						proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
						tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
						opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop,
						requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
						requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						autenticazioneToken,tokenPolicy, forcePDND, forceOAuth,
						listExtendedConnettore, forceEnableConnettore,
						protocollo,false,false, isApplicativiServerEnabled, erogazioneServizioApplicativoServerEnabled,
						erogazioneServizioApplicativoServer, ServiziApplicativiHelper.toArray(listaIdSAServer));

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi 
			boolean isOk = porteApplicativeHelper.connettoriMultipliCheckData(TipoOperazione.ADD, pa, beaBehaviourType, nomeSAConnettore,
					null, nomeConnettore, descrizioneConnettore, statoConnettore, filtriConnettore, null, null, null, null,
					getmsg, getmsgUsername, getmsgPassword, null);
			
			if(isOk) {
				isOk = porteApplicativeHelper.endPointCheckData(protocollo, true,
						endpointtype, url, nomeCodaJms, tipoJms,
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
						autenticazioneToken,tokenPolicy,
						listExtendedConnettore,erogazioneServizioApplicativoServerEnabled,	erogazioneServizioApplicativoServer);
			}
			
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addConnettoriMultipliToDati(dati, TipoOperazione.ADD, beaBehaviourType, nomeSAConnettore,
						nomeConnettore, descrizioneConnettore, statoConnettore, behaviourConFiltri, filtriConnettore, null, null, null, null,
						null);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps, dati);
				
				dati = porteApplicativeHelper.addInformazioniGruppiAsHiddenToDati(TipoOperazione.ADD, dati, idTabP, idConnTab, accessoDaAPSParametro != null ? accessoDaAPSParametro : "", 
						connettoreAccessoGruppi, connettoreRegistro, null);	
				
				porteApplicativeHelper.addEndPointToDati(dati,idsil,nomeservizioApplicativo,sbustamento,sbustamentoInformazioniProtocolloRichiesta,
						getmsg,getmsgUsername,getmsgPassword,true,
						invrifRichiesta,risprif,nomeProtocollo,true,true, true,
						parentPA,serviceBinding, accessoDaAPSParametro, erogazioneServizioApplicativoServerEnabled,
						null, false,
						integrationManagerEnabled,
						TipoOperazione.ADD, null,null);
				
				dati = porteApplicativeHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, 
						null, //(porteApplicativeHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX , 
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
						tipoconn, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_ADD, null, null,
						null, null, null, null, null, null, true,
						isConnettoreCustomUltimaImmagineSalvata, 
						proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
						tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
						opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop,
						requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
						requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						autenticazioneToken,tokenPolicy, forcePDND, forceOAuth,
						listExtendedConnettore, forceEnableConnettore,
						protocollo,false,false, isApplicativiServerEnabled, erogazioneServizioApplicativoServerEnabled,
						erogazioneServizioApplicativoServer, ServiziApplicativiHelper.toArray(listaIdSAServer));

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI, 
						ForwardParams.ADD());
			}

			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			List<Object> listaOggettiDaCreare = new ArrayList<>();
			List<Object> listaOggettiDaModificare = new ArrayList<>();
			
			PortaApplicativaServizioApplicativoConnettore datiConnettore = new PortaApplicativaServizioApplicativoConnettore();
			datiConnettore.setNotifica(true); // connettore notifica
			datiConnettore.setNome(nomeConnettore);
			datiConnettore.setDescrizione(descrizioneConnettore);
			if(statoConnettore.equals(StatoFunzionalita.ABILITATO.getValue())) {
				datiConnettore.setStato(StatoFunzionalita.ABILITATO);
			}
			else if(statoConnettore.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STATO_ABILITATO_MA_NON_SCHEDULATO)) {
				datiConnettore.setStato(StatoFunzionalita.ABILITATO);
				datiConnettore.setScheduling(StatoFunzionalita.DISABILITATO);
			}
			else {
				datiConnettore.setStato(StatoFunzionalita.DISABILITATO);
			}
			
			if(StringUtils.isNotEmpty(filtriConnettore)) {
				List<String> filtri = Arrays.asList(filtriConnettore.split(","));
				
				for (String filtro : filtri) {
					datiConnettore.addFiltro(filtro);
				}
			}
			
			if(datiConnettore.getProprietaOggetto()==null) {
				datiConnettore.setProprietaOggetto(new ProprietaOggetto());
			}
			datiConnettore.getProprietaOggetto().setUtenteRichiedente(userLogin);
			datiConnettore.getProprietaOggetto().setDataCreazione(DateManager.getDate());
			
			PortaApplicativaServizioApplicativo paSA = new PortaApplicativaServizioApplicativo();
			paSA.setDatiConnettore(datiConnettore);
			
			boolean secret = false;
			String secretPassword  = null;
			String secretUser = null;
			boolean secretAppId = false;
			
			if(erogazioneServizioApplicativoServerEnabled) {
				paSA.setNome(erogazioneServizioApplicativoServer);
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
						httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy,
						proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
						tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
						opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop,
						requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
						requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						tokenPolicy, listExtendedConnettore);
			
				// creare un servizio applicativo
				String nomeServizioApplicativoErogatore = pa.getNome() + PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SAX_PREFIX + 
						porteApplicativeHelper.getIdxNuovoConnettoreMultiplo(pa);
				
				ServizioApplicativo sa = new ServizioApplicativo();
				sa.setNome(nomeServizioApplicativoErogatore);
				sa.setTipologiaFruizione(TipologiaFruizione.DISABILITATO.getValue());
				sa.setTipologiaErogazione(TipologiaErogazione.TRASPARENTE.getValue());
				long soggInt = Long.parseLong(idsogg);
				sa.setIdSoggetto(soggInt);
				sa.setTipoSoggettoProprietario(pa.getTipoSoggettoProprietario());
				sa.setNomeSoggettoProprietario(pa.getNomeSoggettoProprietario());
	
				RispostaAsincrona rispostaAsinc = new RispostaAsincrona();
				rispostaAsinc.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
				rispostaAsinc.setGetMessage(CostantiConfigurazione.DISABILITATO);
				sa.setRispostaAsincrona(rispostaAsinc);
	
				InvocazioneServizio is = new InvocazioneServizio();
				is.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
				is.setGetMessage(CostantiConfigurazione.DISABILITATO);
				is.setConnettore(connettore.mappingIntoConnettoreConfigurazione());
				sa.setInvocazioneServizio(is);
				
				InvocazioneCredenziali cis = null;
				
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

				if(StatoFunzionalita.ABILITATO.equals(is.getGetMessage()) ||
						!TipiConnettore.DISABILITATO.toString().equals(endpointtype)){
					sa.setTipologiaErogazione(TipologiaErogazione.TRASPARENTE.getValue());
				}
				else{
					sa.setTipologiaErogazione(TipologiaErogazione.DISABILITATO.getValue());
				}
				
				if(CostantiConfigurazione.ABILITATO.toString().equals(getmsg)) {
					sa.setInvocazionePorta(new InvocazionePorta());
					InvocazionePorta invocazionePorta = sa.getInvocazionePorta();
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
	
				listaOggettiDaCreare.add(sa);
			
				paSA.setNome(nomeServizioApplicativoErogatore);
			}
			
			pa.getServizioApplicativoList().add(paSA);
			
			listaOggettiDaModificare.add(pa);
			
			porteApplicativeCore.performCreateOperation(userLogin, porteApplicativeHelper.smista(), listaOggettiDaCreare.toArray(new Object[listaOggettiDaCreare.size()]));
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), listaOggettiDaModificare.toArray(new Object[listaOggettiDaModificare.size()]));
			
			// Serve per le breadcump
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
									
			porteApplicativeHelper.preparePorteAppConnettoriMultipliList_fromAddConnettore(pa.getNome(), ricerca, listaFiltrata, pa,
					nomeConnettore);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI, 
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI,
					ForwardParams.ADD());
		}  
	}

	
}
