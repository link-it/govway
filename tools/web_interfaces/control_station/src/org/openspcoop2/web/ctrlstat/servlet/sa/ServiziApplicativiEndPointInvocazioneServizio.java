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


package org.openspcoop2.web.ctrlstat.servlet.sa;

import java.util.HashMap;
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
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
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
		
		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_USA_ID_SOGGETTO , session);


		try {
			ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(request, pd, session);
			
			String nomeservizioApplicativo = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO);
			String idsil = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);
			int idSilInt = Integer.parseInt(idsil);
			String sbustamento = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
			String sbustamentoInformazioniProtocolloRichiesta = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
			String getmsg = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
			
			String invrifRichiesta = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RICHIESTA);
			
			String risprif = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);
			
			String tipoauthRichiesta = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_TIPO_AUTENTICAZIONE);
			
			String provider = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			
			String endpointtype = saHelper.readEndPointType();
			String tipoconn = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			String autenticazioneHttp = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			String user = null;
			String password = null;
			
			String connettoreDebug = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
			
			// proxy
			String proxy_enabled = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxy_hostname = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxy_port = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxy_username = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxy_password = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);
			
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
			
			
			// Preparo il menu
			saHelper.makeMenu();
			
			String superUser = ServletUtils.getUserLoginFromSession(session);

			// Prendo il sil
			ServiziApplicativiCore saCore = new ServiziApplicativiCore();
			PddCore pddCore = new PddCore(saCore);
			SoggettiCore soggettiCore = new SoggettiCore(saCore);

			ServizioApplicativo sa = saCore.getServizioApplicativo(idSilInt);
			InvocazioneServizio is = sa.getInvocazioneServizio();
			InvocazioneCredenziali cis = is.getCredenziali();
			Connettore connis = is.getConnettore();
			List<Property> cp = connis.getPropertyList();
			
			String tipoENomeSoggetto = "";
			String nomeProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(sa.getTipoSoggettoProprietario());
			if(provider == null){
				provider = "-1";
			}else {
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(Long.parseLong(provider)); 
				tipoENomeSoggetto = saHelper.getLabelNomeSoggetto(nomeProtocollo, soggetto.getTipo() , soggetto.getNome());
			}
			
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
			
			// Se nomehid = null, devo visualizzare la pagina per la
			// modifica dati
			if(saHelper.isEditModeInProgress()){
				
				// setto la barra del titolo
//				ServletUtils.setPageDataTitle_ServletChange(pd, ServiziApplicativiCostanti.LABEL_SERVIZIO_APPLICATIVO, 
//						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST, "Invocazione servizio di " + nomeservizioApplicativo);
				
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI + tipoENomeSoggetto,
									ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)),								
									new Parameter( "Invocazione servizio di " + nomeservizioApplicativo, null)
							);
				}else {
					ServletUtils.setPageDataTitle_ServletChange(pd, ServiziApplicativiCostanti.LABEL_SERVIZIO_APPLICATIVO, 
							ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST, "Invocazione servizio di " + nomeservizioApplicativo);
				}
				
				
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
				
				if (sbustamento == null) {
					if(is.getSbustamentoSoap()!=null)
						sbustamento = is.getSbustamentoSoap().toString();
				}
				if (sbustamentoInformazioniProtocolloRichiesta == null) {
					if(is.getSbustamentoInformazioniProtocollo()!=null)
						sbustamentoInformazioniProtocolloRichiesta = is.getSbustamentoInformazioniProtocollo().toString();
				}
				if (getmsg == null){
					if(is.getGetMessage()!=null)
						getmsg = is.getGetMessage().toString();
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
						getmsg,invrifRichiesta,risprif,nomeProtocollo,true,true, true);

//				dati = connettoriHelper.addCredenzialiToDati(dati, tipoauth, user, password, confpw, subject,
//						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,true,endpointtype,true);
				
				dati = saHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, null,
						url, nome,
						tipo, user, password, initcont, urlpgk, provurl,
						connfact, sendas, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, TipoOperazione.CHANGE, httpsurl, httpstipologia,
						httpshostverify, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, httpspwdprivatekey,
						httpsalgoritmokey, tipoconn, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,
						nomeservizioApplicativo, idsil, null, null, null, null,
						null, null, true,
						isConnettoreCustomUltimaImmagineSalvata, 
						proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
						opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
						requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						listExtendedConnettore);
				
				dati = saHelper.addHiddenFieldsToDati(dati, provider);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, 
						ServiziApplicativiCostanti.TIPO_OPERAZIONE_ENDPOINT_INVOCAZIONE_SERVIZIO);
			}

			// Controlli sui campi immessi
			boolean isOk = saHelper.servizioApplicativoEndPointCheckData(listExtendedConnettore);
			if (!isOk) {
				
				// setto la barra del titolo
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI + tipoENomeSoggetto,
									ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)),								
									new Parameter( "Invocazione servizio di " + nomeservizioApplicativo, null)
							);
				}else {
					ServletUtils.setPageDataTitle_ServletChange(pd, ServiziApplicativiCostanti.LABEL_SERVIZIO_APPLICATIVO, 
							ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST, "Invocazione servizio di " + nomeservizioApplicativo);
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				saHelper.addEndPointToDati(dati,idsil,nomeservizioApplicativo,sbustamento,sbustamentoInformazioniProtocolloRichiesta,
						getmsg,invrifRichiesta,risprif,nomeProtocollo,true,true, true);
				
//				dati = connettoriHelper.addCredenzialiToDati(dati, tipoauth, user, password, confpw, subject, 
//						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,true,endpointtype,true);
				
				dati = saHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, null,
						url, nome,
						tipo, user, password, initcont, urlpgk, provurl,
						connfact, sendas, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, TipoOperazione.CHANGE, httpsurl, httpstipologia,
						httpshostverify, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, httpspwdprivatekey,
						httpsalgoritmokey, tipoconn, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,
						nomeservizioApplicativo, idsil, null, null, null, null,
						null, null, true,
						isConnettoreCustomUltimaImmagineSalvata, 
						proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
						opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
						requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						listExtendedConnettore);
				
				dati = saHelper.addHiddenFieldsToDati(dati, provider);

				pd.setDati(dati);
				
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, 
						ServiziApplicativiCostanti.TIPO_OPERAZIONE_ENDPOINT_INVOCAZIONE_SERVIZIO);
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
					proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
					opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
					requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
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
			InvocazioneServizio invServizio = sa.getInvocazioneServizio();
			StatoFunzionalita getMessage = invServizio != null ? invServizio.getGetMessage() : null;
			if (TipiConnettore.DISABILITATO.getNome().equals(connis.getTipo()) && CostantiConfigurazione.DISABILITATO.equals(getMessage)) {
				Map<ErrorsHandlerCostant, String> whereIsInUso = new HashMap<ErrorsHandlerCostant, String>();
				if (saCore.isServizioApplicativoInUso(sa, whereIsInUso)) {

					// se in uso in porte applicative allora impossibile
					// disabilitare connettore o getmessage
					if (whereIsInUso.containsKey(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE)) {
						pd.setMessage("Impossibile disabilitare il GetMessage o il Connettore in quanto il Servizio Applicativo [" + sa.getNome() + "]<br>" + " e' in uso in Porta Applicative " + whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE));
						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
						return ServletUtils.getStrutsForwardGeneralError(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, 
								ServiziApplicativiCostanti.TIPO_OPERAZIONE_ENDPOINT_INVOCAZIONE_SERVIZIO);
					}

				}
			}

			String userLogin = ServletUtils.getUserLoginFromSession(session);
			saCore.performUpdateOperation(userLogin, saHelper.smista(), sa);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			List<ServizioApplicativo> lista = null;
			if(!useIdSogg){
				int idLista = Liste.SERVIZIO_APPLICATIVO;
				ricerca = saHelper.checkSearchParameters(idLista, ricerca);
				
				if(saCore.isVisioneOggettiGlobale(superUser)){
					lista = saCore.soggettiServizioApplicativoList(null, ricerca);
				}else{
					lista = saCore.soggettiServizioApplicativoList(superUser, ricerca);
				}
			}else {
				int idLista = Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO;
				ricerca = saHelper.checkSearchParameters(idLista, ricerca);
				lista = saCore.soggettiServizioApplicativoList(ricerca,soggLong);
			}
			
			saHelper.prepareServizioApplicativoList(ricerca, lista, useIdSogg);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI,  ServiziApplicativiCostanti.TIPO_OPERAZIONE_ENDPOINT_INVOCAZIONE_SERVIZIO);

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ServiziApplicativiCostanti.TIPO_OPERAZIONE_ENDPOINT_INVOCAZIONE_SERVIZIO);
		} 

	}
}
