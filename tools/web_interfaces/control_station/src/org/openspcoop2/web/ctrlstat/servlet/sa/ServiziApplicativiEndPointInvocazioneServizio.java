/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddTipologia;
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
			ConnettoriHelper connettoriHelper = new ConnettoriHelper(request, pd, session);
			
			String nomeservizioApplicativo = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO);
			String idsil = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);
			int idSilInt = Integer.parseInt(idsil);
			String sbustamento = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
			String sbustamentoInformazioniProtocolloRichiesta = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
			String getmsg = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
			
			String invrifRichiesta = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RICHIESTA);
			
			String risprif = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);
			
			String tipoauthRichiesta = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_CONNETTORE);
			@SuppressWarnings("unused")
			String confpwRichiesta = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_CONFERMA_PASSWORD_CONNETTORE);
			String subjectRichiesta = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_CONNETTORE);
			
			String provider = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			
			String endpointtype = connettoriHelper.readEndPointType();
			String tipoconn = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			String autenticazioneHttp = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			String user = null;
			String password = null;
			
			String connettoreDebug = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
			
			// http
			String url = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME);
				password = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD);
			}
			
			// jms
			String nome = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String initcont = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String sendas = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(endpointtype)){
				user = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				password = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}
			
			// https
			String httpsurl = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
			String httpstipologia = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
			String httpshostverifyS = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			boolean httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
			String httpspath = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			String httpstipo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			boolean httpsstato = ServletUtils.isCheckBoxEnabled(httpsstatoS);
			String httpskeystore = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME);
				password = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD);
			}
			
			// Preparo il menu
			saHelper.makeMenu();
			
			String superUser = ServletUtils.getUserLoginFromSession(session);

			// Prendo il sil
			ServiziApplicativiCore saCore = new ServiziApplicativiCore();
			PddCore pddCore = new PddCore(saCore);
			SoggettiCore soggettiCore = new SoggettiCore(saCore);

			ServizioApplicativo sa = saCore.getServizioApplicativo(idSilInt);
			InvocazioneServizio is = sa.getInvocazioneServizio();
			Credenziali cis = is.getCredenziali();
			Connettore connis = is.getConnettore();
			List<Property> cp = connis.getPropertyList();
			
			String tipoENomeSoggetto = "";
			String nomeProtocollo = soggettiCore.getProtocolloDefault();
			if(provider == null){
				provider = "-1";
			}else {
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(Long.parseLong(provider)); 
				tipoENomeSoggetto = soggetto.getTipo() + "/" + soggetto.getNome();
				nomeProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
			}
			
			long soggLong = -1;
			// se ho fatto la add 
			if(useIdSogg)
				if(provider != null && !provider.equals("")){
				soggLong = Long.parseLong(provider);
			}

			Boolean isConnettoreCustomUltimaImmagineSalvata = connis.getCustom();
			
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(connis, ConnettoreServletType.SERVIZIO_APPLICATIVO_INVOCAZIONE_SERVIZIO, saCore, 
							request, session, (endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro
			
			// Se nomehid = null, devo visualizzare la pagina per la
			// modifica dati
			if(ServletUtils.isEditModeInProgress(request)){
				
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
					PdDControlStation pdd = pddCore.getPdDControlStation(nomePdd);
					if (PddTipologia.ESTERNO.toString().equals(pdd.getTipo())) {
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
				
				if ((tipoauthRichiesta == null) && (cis != null)) {
					if(cis.getTipo()!=null)
						tipoauthRichiesta = cis.getTipo().toString();
				}
				if ((user == null) && (cis != null)) {
					user = cis.getUser();
					password = cis.getPassword();
					confpwRichiesta = password;
				}
				if ((subjectRichiesta == null) && (cis != null)) {
					subjectRichiesta = cis.getSubject();
				}
				if (endpointtype == null) {
					if ((connis.getCustom()!=null && connis.getCustom()) && !connis.getTipo().equals(TipiConnettore.HTTPS.toString())) {
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
				
				autenticazioneHttp = connettoriHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);
				
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

				if (httpsurl == null) {
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
					httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_SSLV3_TYPE;
				}
				if(httpshostverifyS==null || "".equals(httpshostverifyS)){
					httpshostverifyS = "true";
					httpshostverify = true;
				}
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				saHelper.addEndPointToDati(dati,idsil,nomeservizioApplicativo,sbustamento,sbustamentoInformazioniProtocolloRichiesta,
						getmsg,invrifRichiesta,risprif,nomeProtocollo,true,true, true);

//				dati = connettoriHelper.addCredenzialiToDati(dati, tipoauth, user, password, confpw, subject,
//						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,true,endpointtype,true);
				
				dati = connettoriHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, null,
						url, nome,
						tipo, user, password, initcont, urlpgk, provurl,
						connfact, sendas, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, TipoOperazione.CHANGE, httpsurl, httpstipologia,
						httpshostverify, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, httpspwdprivatekey,
						httpsalgoritmokey, tipoconn, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,
						nomeservizioApplicativo, idsil, null, null, null,
						null, null, true,
						isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);
				
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
				
				dati = connettoriHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, null,
						url, nome,
						tipo, user, password, initcont, urlpgk, provurl,
						connfact, sendas, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, TipoOperazione.CHANGE, httpsurl, httpstipologia,
						httpshostverify, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, httpspwdprivatekey,
						httpsalgoritmokey, tipoconn, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,
						nomeservizioApplicativo, idsil, null, null, null,
						null, null, true,
						isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);
				
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
			if (tipoauthRichiesta!=null && !tipoauthRichiesta.equals(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE.toString())) {
								
				if (cis == null) {
					cis = new Credenziali();
				}
				if(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA.equals(tipoauthRichiesta)){
					//cis.setTipo(CredenzialeTipo.toEnumConstant(CostantiConfigurazione.AUTENTICAZIONE_NONE));
					cis.setTipo(null);
				}else
					cis.setTipo(CredenzialeTipo.toEnumConstant(tipoauthRichiesta));
				if (tipoauthRichiesta.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC)) {
					cis.setUser(user);
					cis.setPassword(password);
				} else {
					cis.setUser("");
					cis.setPassword("");
				}
				if (tipoauthRichiesta.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL)) {
					cis.setSubject(subjectRichiesta);
				} else {
					cis.setSubject("");
				}
				is.setCredenziali(cis);
			}
			else if(endpointtype.equals(TipiConnettore.JMS.toString())){
				if (cis == null) {
					cis = new Credenziali();
				}
				if(user!=null && password!=null){
					cis.setTipo(CredenzialeTipo.BASIC);
					cis.setUser(user);
					cis.setPassword(password);
				}
				else{
					cis.setTipo(null);
					cis.setUser(user);
					cis.setPassword(password);
				}
				cis.setSubject(null);
				is.setCredenziali(cis);
			}
			else {
				is.setCredenziali(null);
			}
			String oldConnT = connis.getTipo();
			if ((connis.getCustom()!=null && connis.getCustom()) && !connis.getTipo().equals(TipiConnettore.HTTPS.toString()))
				oldConnT = TipiConnettore.CUSTOM.toString();
			connettoriHelper.fillConnettore(connis, connettoreDebug, endpointtype, oldConnT, tipoconn, url,
					nome, tipo, user, password,
					initcont, urlpgk, provurl, connfact,
					sendas, httpsurl, httpstipologia,
					httpshostverify, httpspath, httpstipo,
					httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust,
					httpspathkey, httpstipokey,
					httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey,
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
			
			saHelper.prepareServizioApplicativoList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI,  ServiziApplicativiCostanti.TIPO_OPERAZIONE_ENDPOINT_INVOCAZIONE_SERVIZIO);

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ServiziApplicativiCostanti.TIPO_OPERAZIONE_ENDPOINT_INVOCAZIONE_SERVIZIO);
		} 

	}
}
