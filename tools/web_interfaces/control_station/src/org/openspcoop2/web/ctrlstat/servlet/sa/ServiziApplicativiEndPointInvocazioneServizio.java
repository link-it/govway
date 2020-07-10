/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
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
 * servizioApplicativoEndPoint
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ServiziApplicativiEndPointInvocazioneServizio extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
		Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, session);
		if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
		Boolean useIdSogg = parentSA == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO;
		Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session);
		try {

			ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(request, pd, session);

			boolean isModalitaCompleta = saHelper.isModalitaCompleta();

			String nomeservizioApplicativo = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO);
			String idsil = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);
			int idSilInt = Integer.parseInt(idsil);
			String sbustamento = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
			String sbustamentoInformazioniProtocolloRichiesta = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
			String getmsg = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
			String getmsgUsername = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
			String getmsgPassword = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			String changepwd = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD);
			String tipoCredenzialiSSLVerificaTuttiICampi = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
			if (tipoCredenzialiSSLVerificaTuttiICampi == null) {
				tipoCredenzialiSSLVerificaTuttiICampi = Costanti.CHECK_BOX_DISABLED;
			}
			
			String invrifRichiesta = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RICHIESTA);

			String risprif = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);

			String tipoauthRichiesta = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_TIPO_AUTENTICAZIONE);

			String provider = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			if(provider == null){
				provider = "";
			} 
			String idPorta = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_PORTA);
			if(idPorta == null)
				idPorta = "";

			String idAsps = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String endpointtype = saHelper.readEndPointType();
			String tipoconn = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			String autenticazioneHttp = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			String user = null;
			String password = null;

			String connettoreDebug = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);

			// token policy
			String autenticazioneTokenS = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			boolean autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			String token_policy = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);

			// proxy
			String proxy_enabled = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxy_hostname = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxy_port = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxy_username = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxy_password = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);

			// tempi risposta
			String tempiRisposta_enabled = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			String tempiRisposta_connectionTimeout = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			String tempiRisposta_readTimeout = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			String tempiRisposta_tempoMedioRisposta = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);

			// opzioni avanzate
			String transfer_mode = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transfer_mode_chunk_size = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirect_mode = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirect_max_hop = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(saHelper, transfer_mode, redirect_mode);

			// http
			String url = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
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
				password = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}

			// https
			String httpsurl = url;
			String httpstipologia = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
			String httpshostverifyS = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			boolean httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
			String httpspath = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			String httpstipo = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			boolean httpsstato = ServletUtils.isCheckBoxEnabled(httpsstatoS);
			String httpskeystore = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			String httpsKeyAlias = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
			String httpsTrustStoreCRLs = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}

			// file
			String requestOutputFileName = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			String requestOutputFileNameHeaders = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			String requestOutputParentDirCreateIfNotExists = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			String requestOutputOverwriteIfExists = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			String responseInputMode = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			String responseInputFileName = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			String responseInputFileNameHeaders = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			String responseInputDeleteAfterRead = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			String responseInputWaitTime = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);

			String erogazioneServizioApplicativoServerEnabledS = saHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ABILITA_USO_APPLICATIVO_SERVER);
			boolean erogazioneServizioApplicativoServerEnabled = ServletUtils.isCheckBoxEnabled(erogazioneServizioApplicativoServerEnabledS);
			String erogazioneServizioApplicativoServer = saHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_APPLICATIVO_SERVER);

			String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneErogatori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
					gestioneErogatori = true;
				}
			}

			boolean accessoDaListaAPS = false;
			String accessoDaAPSParametro = null;
			// nell'erogazione vale sempre
			//if(gestioneErogatori) {
			accessoDaAPSParametro = saHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			if(Costanti.CHECK_BOX_ENABLED_TRUE.equals(accessoDaAPSParametro)) {
				accessoDaListaAPS = true;
			}
			//}

			boolean fromConfig = (parentSA!=null && (parentSA.intValue() == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_CONFIGURAZIONE)); 
			String idTab = saHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(fromConfig && !saHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			// Preparo il menu
			saHelper.makeMenu();

			String superUser = ServletUtils.getUserLoginFromSession(session);

			// Prendo il sil
			ServiziApplicativiCore saCore = new ServiziApplicativiCore();
			PddCore pddCore = new PddCore(saCore);
			SoggettiCore soggettiCore = new SoggettiCore(saCore);
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(saCore);
			PortaApplicativa pa = null;
			if(StringUtils.isNotBlank(idPorta)) {
				pa = porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta));
			}

			boolean isApplicativiServerEnabled = saCore.isApplicativiServerEnabled(saHelper);
			if(!isApplicativiServerEnabled) {
				erogazioneServizioApplicativoServerEnabled = false;
			}

			// La lista degli SA viene filtrata per tipo se sono abilitati gli applicativiServer.
			String filtroTipoSA = (isApplicativiServerEnabled && gestioneErogatori) ? ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER : null;

			ServizioApplicativo sa = saCore.getServizioApplicativo(idSilInt);
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(sa.getTipoSoggettoProprietario());
			InvocazionePorta invocazionePorta = sa.getInvocazionePorta();
			InvocazioneServizio is = sa.getInvocazioneServizio();
			InvocazioneCredenziali cis = is.getCredenziali();
			Connettore connis = is.getConnettore();
			List<Property> cp = connis.getPropertyList();
			String tipoSA = sa.getTipo();
			
			String postBackElementName = saHelper.getPostBackElementName();
			// Controllo se ho modificato l'azione allora ricalcolo il nome
			if(postBackElementName != null ){
				
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ABILITA_USO_APPLICATIVO_SERVER)){
					// se il vecchio SA era di tipo server e ripristino il default allora resetto la creazione del connettore
					if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo()) && !erogazioneServizioApplicativoServerEnabled) {
						// 1. leggo SA di default
						
						IDServizioApplicativo idSA = new IDServizioApplicativo();
						idSA.setNome(pa.getServizioApplicativoDefault());
						IDSoggetto idSoggettoProprietario = new IDSoggetto();
						idSoggettoProprietario.setTipo(pa.getTipoSoggettoProprietario());
						idSoggettoProprietario.setNome(pa.getNomeSoggettoProprietario());
						idSA.setIdSoggettoProprietario(idSoggettoProprietario );
						sa = saCore.getServizioApplicativo(idSA);
						invocazionePorta = sa.getInvocazionePorta();
						is = sa.getInvocazioneServizio();
						cis = is.getCredenziali();
						connis = is.getConnettore();
						cp = connis.getPropertyList();
						tipoSA = sa.getTipo();
						
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
						endpointtype = saHelper.readEndPointType();
						tipoconn = null;
						autenticazioneHttp = null;
						user = null;
						password = null;
						connettoreDebug = null;

						// token policy
						autenticazioneTokenS = null;
						autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
						token_policy = null;

						// proxy
						proxy_enabled = null;
						proxy_hostname = null;
						proxy_port = null;
						proxy_username = null;
						proxy_password = null;

						// tempi risposta
						tempiRisposta_enabled = null;
						tempiRisposta_connectionTimeout = null;
						tempiRisposta_readTimeout = null;
						tempiRisposta_tempoMedioRisposta = null;

						// opzioni avanzate
						transfer_mode = null;
						transfer_mode_chunk_size = null;
						redirect_mode = null;
						redirect_max_hop = null;
						opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(saHelper, transfer_mode, redirect_mode);

						// http
						url = null;

						// jms
						nome = null;
						tipo = null;
						initcont = null;
						urlpgk = null;
						provurl = null;
						connfact = null;
						sendas = null;

						// https
						httpsurl = url;
						httpstipologia = null;
						httpshostverifyS = null;
						httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
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

						// file
						requestOutputFileName = null;
						requestOutputFileNameHeaders = null;
						requestOutputParentDirCreateIfNotExists = null;
						requestOutputOverwriteIfExists = null;
						responseInputMode = null;
						responseInputFileName = null;
						responseInputFileNameHeaders = null;
						responseInputDeleteAfterRead = null;
						responseInputWaitTime = null;
					}
				}
				
				// Change Password basic/api
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD)) {
					if(!ServletUtils.isCheckBoxEnabled(changepwd)) {
						if (invocazionePorta != null && invocazionePorta.sizeCredenzialiList()>0){
							getmsgPassword = invocazionePorta.getCredenziali(0).getPassword();
						}
					}
				}
			}

			boolean forceEnabled = false;
			if(parentSA!=null && (parentSA.intValue() == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_CONFIGURAZIONE)) {
				if(
						//va visualizzato comunque se già configurato: !saHelper.isModalitaStandard() && 
						( (getmsg!=null && CostantiConfigurazione.ABILITATO.toString().equals(getmsg)) 
								||
								(getmsg==null && is!=null && is.getGetMessage()!=null && StatoFunzionalita.ABILITATO.equals(is.getGetMessage()))
								)
						) {
					forceEnabled = false;
				}
				else {
					forceEnabled = true;
					if(endpointtype==null) {
						if( connis==null || connis.getTipo()==null || TipiConnettore.DISABILITATO.getNome().equals(connis.getTipo()) ) {
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
			if(useIdSogg)
				if(provider != null && !provider.equals("")){
					soggLong = Long.parseLong(provider);
				}

			Boolean isConnettoreCustomUltimaImmagineSalvata = connis.getCustom();

			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(connis, ConnettoreServletType.SERVIZIO_APPLICATIVO_INVOCAZIONE_SERVIZIO, saHelper, 
							(endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro


			List<Parameter> lstParm = saHelper.getTitoloSA(parentSA, provider, idAsps, idPorta);

			boolean integrationManagerEnabled = !saHelper.isModalitaStandard() && saCore.isIntegrationManagerEnabled();
						
			ServiceBinding serviceBinding = null;
			String labelPerPorta = null;
			if(parentSA!=null && (parentSA.intValue() == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_CONFIGURAZIONE)) {

				AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(saCore);
				AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));

				if(accessoDaListaAPS) {
					if(!isModalitaCompleta) {
						if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
							labelPerPorta = ErogazioniCostanti.LABEL_ASPS_PORTE_APPLICATIVE_MODIFICA_CONNETTORE;
						} else {
							labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE_DI+
									saHelper.getLabelIdServizio(asps);
						}
					}
					else {
						labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE;
					}
				}
				else {
					
					labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DI,
							PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE,
							pa);
				}

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
				labelPerPorta = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_INVOCAZIONE_SERVIZIO_DI+nomeservizioApplicativo;
			}

			// Lista dei servizi applicativi per la creazione automatica
			List<IDServizioApplicativoDB> listaIdSAServer = null;
			//String [] saSoggetti = null;	
			if ((provider != null) && !provider.equals("")) {
				int idErogatore = Integer.parseInt(provider);

				// I servizi applicativi da visualizzare sono quelli che hanno
				// -Integration Manager (getMessage abilitato)
				// -connettore != disabilitato
				listaIdSAServer = saCore.getIdServiziApplicativiWithIdErogatore(Long.valueOf(idErogatore), filtroTipoSA, integrationManagerEnabled, true);

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
				// controllo se servizio applicativo appartiene a soggetto con
				// pdd esterna
				long idProprietario = sa.getIdSoggetto();
				if(saCore.isRegistroServiziLocale()){
					Soggetto soggetto = soggettiCore.getSoggettoRegistro(idProprietario);
					String nomePdd = soggetto.getPortaDominio();
					if(pddCore.isPddEsterna(nomePdd)){
						pd.setMessage("Impossibile Effettuare operazioni su Connettore.<br>Questo Servizio Applicativo appartiene ad un Soggetto associato ad una Porta di Dominio con tipo 'esterno'");
						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
						return ServletUtils.getStrutsForwardGeneralError(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, 
								ServiziApplicativiCostanti.TIPO_OPERAZIONE_ENDPOINT_INVOCAZIONE_SERVIZIO);
					}
				}

				if (erogazioneServizioApplicativoServer == null && isApplicativiServerEnabled) {
					// se in configurazione ho selezionato un server
					if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(tipoSA)) {
						erogazioneServizioApplicativoServer = sa.getNome();
						erogazioneServizioApplicativoServerEnabled = true;
					}
				}

				if (sbustamento == null) {
					if(is.getSbustamentoSoap()!=null)
						sbustamento = is.getSbustamentoSoap().toString();
				}
				if (sbustamentoInformazioniProtocolloRichiesta == null) {
					if(is.getSbustamentoInformazioniProtocollo()!=null)
						sbustamentoInformazioniProtocolloRichiesta = is.getSbustamentoInformazioniProtocollo().toString();
				}
				if (getmsg == null){
					if(is.getGetMessage()!=null) {
						getmsg = is.getGetMessage().toString();
						if(CostantiConfigurazione.ABILITATO.toString().equals(getmsg)) {
							if(invocazionePorta!=null && invocazionePorta.sizeCredenzialiList()>0) {
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
					}
				}
				
				if(!integrationManagerEnabled && CostantiConfigurazione.ABILITATO.toString().equals(getmsg)) {
					// faccio vedere I.M. anche con interfaccia standard
					integrationManagerEnabled = true;
					
					forceEnabled = false;
					
					if(erogazioneServizioApplicativoServerEnabled && ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo())) {
						if(listaIdSAServer==null) {
							listaIdSAServer = new ArrayList<IDServizioApplicativoDB>();
						}
						boolean found = false;
						for (IDServizioApplicativo idServizioApplicativo : listaIdSAServer) {
							if(idServizioApplicativo.getNome().equals(sa.getNome())) {
								found = true;
								break;
							}
						}
						if(!found) {
							IDServizioApplicativoDB idSA = new IDServizioApplicativoDB();
							idSA.setNome(sa.getNome());
							idSA.setIdSoggettoProprietario(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
							idSA.setId(sa.getId());
							listaIdSAServer.add(idSA);
						}
					}
					
				}

				if (invrifRichiesta == null) {
					if(is.getInvioPerRiferimento()!=null)
						invrifRichiesta = is.getInvioPerRiferimento().toString();
					if ((invrifRichiesta == null) || "".equals(invrifRichiesta)) {
						invrifRichiesta = CostantiConfigurazione.DISABILITATO.toString();
					}
				}
				if (risprif == null) {
					if(is.getRispostaPerRiferimento()!=null)
						risprif = is.getRispostaPerRiferimento().toString();
					if ((risprif == null) || "".equals(risprif)) {
						risprif = CostantiConfigurazione.DISABILITATO.toString();
					}
				}

				if ((tipoauthRichiesta == null) && (is != null) && is.getAutenticazione()!=null) {
					tipoauthRichiesta = is.getAutenticazione().getValue();
				}
				if ((user == null) && (cis != null)) {
					user = cis.getUser();
					password = cis.getPassword();
				}
				if (endpointtype == null) {
					if ((connis.getCustom()!=null && connis.getCustom()) && 
							!connis.getTipo().equals(TipiConnettore.HTTPS.toString()) && 
							!connis.getTipo().equals(TipiConnettore.FILE.toString())) {
						endpointtype = TipiConnettore.CUSTOM.toString();
						tipoconn = connis.getTipo();
					} else
						endpointtype = connis.getTipo();
				}

				Map<String, String> props = null;
				if(is!=null && is.getConnettore()!=null)
					props = is.getConnettore().getProperties();

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
								tempiRisposta_connectionTimeout = configGenerale.getTempiRispostaErogazione().getConnectionTimeout().intValue()+"";
							}
						}

						if(tempiRisposta_readTimeout==null || "".equals(tempiRisposta_readTimeout) ) {
							String v = props.get(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT);
							if(v!=null && !"".equals(v)){
								tempiRisposta_readTimeout = v.trim();
								tempiRisposta_enabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
							}
							else {
								tempiRisposta_readTimeout = configGenerale.getTempiRispostaErogazione().getReadTimeout().intValue()+"";
							}
						}

						if(tempiRisposta_tempoMedioRisposta==null || "".equals(tempiRisposta_tempoMedioRisposta) ) {
							String v = props.get(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA);
							if(v!=null && !"".equals(v)){
								tempiRisposta_tempoMedioRisposta = v.trim();
								tempiRisposta_enabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
							}
							else {
								tempiRisposta_tempoMedioRisposta = configGenerale.getTempiRispostaErogazione().getTempoMedioRisposta().intValue()+"";
							}
						}
					}
					else {
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

				if(token_policy==null && props!=null){
					String v = props.get(CostantiDB.CONNETTORE_TOKEN_POLICY);
					if(v!=null && !"".equals(v)){
						token_policy = v;
						autenticazioneToken = true;
					}
				}

				opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(saHelper, transfer_mode, redirect_mode);

				autenticazioneHttp = saHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);

				for (int i = 0; i < connis.sizePropertyList(); i++) {
					Property singlecp = cp.get(i);
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_HTTP_LOCATION)) {
						if (url == null) {
							url = singlecp.getValore();
						}
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_NOME)) {
						if (nome == null) {
							nome = singlecp.getValore();
						}
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_TIPO)) {
						if (tipo == null) {
							tipo = singlecp.getValore();
						}
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY)) {
						if (connfact == null) {
							connfact = singlecp.getValore();
						}
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_SEND_AS)) {
						if (sendas == null) {
							sendas = singlecp.getValore();
						}
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL)) {
						if (initcont == null) {
							initcont = singlecp.getValore();
						}
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG)) {
						if (urlpgk == null) {
							urlpgk = singlecp.getValore();
						}
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL)) {
						if (provurl == null) {
							provurl = singlecp.getValore();
						}
					}
				}

				if (httpstipologia == null) {
					httpsurl = props.get(CostantiDB.CONNETTORE_HTTPS_LOCATION);
					httpstipologia = props.get(CostantiDB.CONNETTORE_HTTPS_SSL_TYPE);
					httpshostverifyS = props.get(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER);
					if(httpshostverifyS!=null){
						httpshostverify = Boolean.valueOf(httpshostverifyS);
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
					httpshostverifyS = "true";
					httpshostverify = true;
				}

				// file
				if(responseInputMode==null && props!=null){

					requestOutputFileName = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE);	
					requestOutputFileNameHeaders = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS);	
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

				saHelper.addEndPointToDati(dati,idsil,nomeservizioApplicativo,sbustamento,sbustamentoInformazioniProtocolloRichiesta,
						getmsg,getmsgUsername,getmsgPassword,true,
						invrifRichiesta,risprif,nomeProtocollo,true,true, true,
						parentSA,serviceBinding, accessoDaAPSParametro, erogazioneServizioApplicativoServerEnabled,
						null, false,
						integrationManagerEnabled,
						TipoOperazione.CHANGE, tipoCredenzialiSSLVerificaTuttiICampi, changepwd);
				
				//				dati = connettoriHelper.addCredenzialiToDati(dati, tipoauth, user, password, confpw, subject,
				//						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,true,endpointtype,true);

				dati = saHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, null,
						url, nome,
						tipo, user, password, initcont, urlpgk, provurl,
						connfact, sendas, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, TipoOperazione.CHANGE, httpsurl, httpstipologia,
						httpshostverify, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, 
						httpspwdprivatekey, httpsalgoritmokey, 
						httpsKeyAlias, httpsTrustStoreCRLs,
						tipoconn, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,
						nomeservizioApplicativo, idsil, idAsps, idPorta, null, null,
						null, null, true,
						isConnettoreCustomUltimaImmagineSalvata, 
						proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
						tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
						opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
						requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						autenticazioneToken,token_policy,
						listExtendedConnettore, forceEnabled,
						nomeProtocollo, false, false
						, isApplicativiServerEnabled, erogazioneServizioApplicativoServerEnabled,
						erogazioneServizioApplicativoServer, ServiziApplicativiHelper.toArray(listaIdSAServer)
						);

				dati = saHelper.addHiddenFieldsToDati(dati, provider, idAsps, idPorta);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, 
						ServiziApplicativiCostanti.TIPO_OPERAZIONE_ENDPOINT_INVOCAZIONE_SERVIZIO);
			}

			// Controlli sui campi immessi
			boolean isOk = saHelper.servizioApplicativoEndPointCheckData(protocollo, listExtendedConnettore, sa);
			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				saHelper.addEndPointToDati(dati,idsil,nomeservizioApplicativo,sbustamento,sbustamentoInformazioniProtocolloRichiesta,
						getmsg,getmsgUsername,getmsgPassword,true,
						invrifRichiesta,risprif,nomeProtocollo,true,true, true,
						parentSA,serviceBinding, accessoDaAPSParametro, erogazioneServizioApplicativoServerEnabled,
						null, false,
						integrationManagerEnabled,
						TipoOperazione.CHANGE, tipoCredenzialiSSLVerificaTuttiICampi, changepwd);
				
				//				dati = connettoriHelper.addCredenzialiToDati(dati, tipoauth, user, password, confpw, subject, 
				//						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,true,endpointtype,true);

				dati = saHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, null,
						url, nome,
						tipo, user, password, initcont, urlpgk, provurl,
						connfact, sendas, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, TipoOperazione.CHANGE, httpsurl, httpstipologia,
						httpshostverify, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, 
						httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs,
						tipoconn, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,
						nomeservizioApplicativo, idsil, null, null, null, null,
						null, null, true,
						isConnettoreCustomUltimaImmagineSalvata, 
						proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
						tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
						opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
						requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						autenticazioneToken,token_policy,
						listExtendedConnettore, forceEnabled,
						nomeProtocollo, false, false, isApplicativiServerEnabled, erogazioneServizioApplicativoServerEnabled,
						erogazioneServizioApplicativoServer, ServiziApplicativiHelper.toArray(listaIdSAServer)
						);

				dati = saHelper.addHiddenFieldsToDati(dati, provider, idAsps, idPorta);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, 
						ServiziApplicativiCostanti.TIPO_OPERAZIONE_ENDPOINT_INVOCAZIONE_SERVIZIO);
			}

			// rileggo la vecchia configurazione dal db (puo' essere stata riletta durante la postback)
			sa = saCore.getServizioApplicativo(idSilInt);
			invocazionePorta = sa.getInvocazionePorta();
			is = sa.getInvocazioneServizio();
			cis = is.getCredenziali();
			connis = is.getConnettore();
			cp = connis.getPropertyList();

			boolean secret = false;
			String secret_password  = null;
			String secret_user = null;
			boolean secret_appId = false;
			
			List<Object> oggettiDaAggiornare = new ArrayList<>();
			// se ho selezionato un servizio applicativo Server allora devo associarlo alla porta al posto del vecchio 
			if(erogazioneServizioApplicativoServerEnabled) {
				// solo se ho cambiato il valore del servizio server oppure ho scelto un server al posto del default
				if(!erogazioneServizioApplicativoServer.equals(sa.getNome())) {
					
					// prelevo l'associazione con il vecchio servizio applicativo
					PortaApplicativaServizioApplicativo paSAtmp = null;
					for (PortaApplicativaServizioApplicativo paSA : pa.getServizioApplicativoList()) {
						if(paSA.getNome().equals(sa.getNome())) {
							paSAtmp = paSA;
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
					PortaApplicativaServizioApplicativo paSA = new PortaApplicativaServizioApplicativo();
					paSA.setNome(erogazioneServizioApplicativoServer);
					pa.getServizioApplicativoList().add(paSA);

					oggettiDaAggiornare.add(pa);
					
					saHelper.impostaSAServerAlleConfigurazioniCheUsanoConnettoreDelMappingDiDefault(idAsps, erogazioneServizioApplicativoServer, pa, sa, oggettiDaAggiornare);
					
				}
			} else {
				
				if(isApplicativiServerEnabled) {
				
					// caso normale
					// se avevo salvato un server e ritorno ad una configurazione di default
					if(pa.getServizioApplicativoDefault() != null) {
						
						String oldServizioApplicativoDefault = pa.getServizioApplicativoDefault();
						String oldNomeSA = sa.getNome();
						String oldTipoSA = sa.getTipo();
						
						// prelevo l'associazione con il vecchio servizio applicativo server
						PortaApplicativaServizioApplicativo paSAtmp = null;
						for (PortaApplicativaServizioApplicativo paSA : pa.getServizioApplicativoList()) {
							if(paSA.getNome().equals(oldNomeSA)) {
								paSAtmp = paSA;
								break;
							}
						}
	
						if(paSAtmp!= null) {
							// se ho modificato il server che sto utilizzando lo rimuovo
							if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(oldTipoSA)){
								pa.getServizioApplicativoList().remove(paSAtmp); 	
							}
						}
	
						PortaApplicativaServizioApplicativo paSA = new PortaApplicativaServizioApplicativo();
						paSA.setNome(oldServizioApplicativoDefault);
	//					paSA.setNome(pa.getNome());
						pa.getServizioApplicativoList().add(paSA);
						pa.setServizioApplicativoDefault(null);
	
						oggettiDaAggiornare.add(pa);
						
						saHelper.impostaSADefaultAlleConfigurazioniCheUsanoConnettoreDelMappingDiDefault(idAsps, pa, sa, oggettiDaAggiornare);
						
						// rileggo la vecchia configurazione dal db di default
						IDServizioApplicativo idSA = new IDServizioApplicativo();
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
						cp = connis.getPropertyList();
					}
				}

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
						!connis.getTipo().equals(TipiConnettore.FILE.toString()))
					oldConnT = TipiConnettore.CUSTOM.toString();
				saHelper.fillConnettore(connis, connettoreDebug, endpointtype, oldConnT, tipoconn, url,
						nome, tipo, user, password,
						initcont, urlpgk, provurl, connfact,
						sendas, httpsurl, httpstipologia,
						httpshostverify, httpspath, httpstipo,
						httpspwd, httpsalgoritmo, httpsstato,
						httpskeystore, httpspwdprivatekeytrust,
						httpspathkey, httpstipokey,
						httpspwdkey, httpspwdprivatekey,
						httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs,
						proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
						tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
						opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
						requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						token_policy,
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
					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
					return ServletUtils.getStrutsForwardGeneralError(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, 
							ServiziApplicativiCostanti.TIPO_OPERAZIONE_ENDPOINT_INVOCAZIONE_SERVIZIO);
				}

				if(CostantiConfigurazione.ABILITATO.toString().equals(getmsg)) {
					boolean found = false;
					if(invocazionePorta!=null && invocazionePorta.sizeCredenzialiList()>0) {
						for (int i = 0; i < invocazionePorta.sizeCredenzialiList(); i++) {
							Credenziali c = invocazionePorta.getCredenziali(i);
							if(CredenzialeTipo.BASIC.equals(c.getTipo())) {
								c.setUser(getmsgUsername);
								c.setPassword(getmsgPassword);
								found = true;
								
								boolean encryptOldPlainPwd = !c.isCertificateStrictVerification() && saCore.isApplicativiPasswordEncryptEnabled(); 
								
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
									secret_user = c.getUser();
									secret_password = c.getPassword();
									secret_appId = c.isAppId();
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
							secret_user = c.getUser();
							secret_password = c.getPassword();
							secret_appId = c.isAppId();
						}
												
						invocazionePorta.addCredenziali(c);
					}
				}
				else {
					// Fix: altrimenti rimaneva assegnate le credenziali quando si disabilitava l'integration manager
					if(!saHelper.isModalitaCompleta()) {
						if(invocazionePorta!=null && invocazionePorta.sizeCredenzialiList()>0) {
							while (invocazionePorta.sizeCredenzialiList()>0) {
								invocazionePorta.removeCredenziali(0);
							}
						}
					}
				}

				oggettiDaAggiornare.add(sa);
			}

			String userLogin = ServletUtils.getUserLoginFromSession(session);
			if(oggettiDaAggiornare.size() > 0)
				saCore.performUpdateOperation(userLogin, saHelper.smista(), oggettiDaAggiornare.toArray(new Object[oggettiDaAggiornare.size()]));

			// Messaggio 'Please Copy'
			if(secret) {
				saHelper.setSecretPleaseCopy(secret_password, secret_user, secret_appId, ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC, false, null);
			}
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<ServizioApplicativo> lista = null;
			int idLista = -1;
			switch (parentSA) { 
			case ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_CONFIGURAZIONE:
				if(accessoDaListaAPS) {
					if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
						ErogazioniHelper erogazioniHelper = new ErogazioniHelper(request, pd, session);
						int idServizio = Integer.parseInt(idAsps);
						AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(saCore);
						AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idServizio);
						erogazioniHelper.prepareErogazioneChange(TipoOperazione.CHANGE, asps, null);
						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
						return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.CHANGE());
					}

					idLista = Liste.SERVIZI;
					ricerca = saHelper.checkSearchParameters(idLista, ricerca);
					if(gestioneErogatori) {
						ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
					}
					boolean [] permessi = new boolean[2];
					PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();
					permessi[0] = pu.isServizi();
					permessi[1] = pu.isAccordiCooperazione();
					List<AccordoServizioParteSpecifica> listaS = null;
					AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(saCore);
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

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			ForwardParams fwP = ServiziApplicativiCostanti.TIPO_OPERAZIONE_ENDPOINT_INVOCAZIONE_SERVIZIO;
			if(fromConfig && !saHelper.isModalitaCompleta()) {
				fwP = PorteApplicativeCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE;
			}

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI,  fwP);

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ServiziApplicativiCostanti.TIPO_OPERAZIONE_ENDPOINT_INVOCAZIONE_SERVIZIO);
		} 

	}
	
}
