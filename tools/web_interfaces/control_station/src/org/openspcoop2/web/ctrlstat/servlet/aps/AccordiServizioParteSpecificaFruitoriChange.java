/*
 * OpenSPCoop - Customizable API Gateway 
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


package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.text.MessageFormat;
import java.util.ArrayList;
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
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.InterfaceType;

/**
 * serviziFruitoriChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaFruitoriChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();

		try {
			// prendo i dati hidden del pdold e li metto nel pd attuale
			PageData pdOld = ServletUtils.getPageDataFromSession(session);
			pd.setHidden(pdOld.getHidden());

			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			ConnettoriHelper connettoriHelper = new ConnettoriHelper(request, pdOld, session);

			String idServizio = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			int idServizioInt = Integer.parseInt(idServizio);
			String idServizioFruitore = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID);// id
			// row
			// in
			// table
			// servizi_fruitori
			int idServizioFruitoreInt = Integer.parseInt(idServizioFruitore);
			String provider = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROVIDER);
			//			String endpointtype = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			String correlato = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO);
			
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
			String httpstipologia = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE );
			String httpshostverifyS = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			boolean httpshostverify = false;
			if (httpshostverifyS != null && httpshostverifyS.equals(Costanti.CHECK_BOX_ENABLED))
				httpshostverify = true;
			String httpspath = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			String httpstipo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			boolean httpsstato = false;
			if (httpsstatoS != null && httpsstatoS.equals(Costanti.CHECK_BOX_ENABLED))
				httpsstato = true;
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
			
			
			String profilo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROFILO);
			String clientAuth = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_CLIENT_AUTH);
			String statoPackage = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);
			
			String backToStato = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RIPRISTINA_STATO);
			String actionConfirm = request.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);

			boolean validazioneDocumenti = true;
			String tmpValidazioneDocumenti = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
			if(ServletUtils.isEditModeInProgress(request)){
				// primo accesso alla servlet
				if(tmpValidazioneDocumenti!=null){
					if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
						validazioneDocumenti = true;
					}
					else if("false".equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_DISABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
						validazioneDocumenti = false;
					}
				}else{
					validazioneDocumenti = true;
				}
			}else{
				if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
					validazioneDocumenti = true;
				}
				else{
					validazioneDocumenti = false;
				}
			}

			// Prendo il servizio
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);

			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idServizioInt);
			Servizio servizio = asps.getServizio();
			String nomeservizio = servizio.getNome();
			String tiposervizio = servizio.getTipo();
			
			// Prendo nome e tipo del fruitore dal db
			Fruitore servFru = apsCore.getServizioFruitore(idServizioFruitoreInt);

			Boolean isConnettoreCustomUltimaImmagineSalvata = servFru.getConnettore().getCustom();
			
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(servFru.getConnettore(), ConnettoreServletType.FRUIZIONE_ACCORDO_SERVIZIO_PARTE_SPECIFICA_CHANGE, apsCore, 
							request, session, (endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro
			
			// Prendo il soggetto erogatore del servizio
			String tipoSoggettoErogatore = servizio.getTipoSoggettoErogatore();
			String nomesoggettoErogatore = servizio.getNomeSoggettoErogatore();
			String idSoggettoErogatoreDelServizio = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			if ((idSoggettoErogatoreDelServizio == null) || idSoggettoErogatoreDelServizio.equals("")) {
				PageData oldPD = ServletUtils.getPageDataFromSession(session);

				idSoggettoErogatoreDelServizio = oldPD.getHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);

				if (idSoggettoErogatoreDelServizio == null || idSoggettoErogatoreDelServizio.equals("")) {
					IDSoggetto idSE = new IDSoggetto(tipoSoggettoErogatore, nomesoggettoErogatore);
					Soggetto SE = soggettiCore.getSoggettoRegistro(idSE);
					idSoggettoErogatoreDelServizio = "" + SE.getId();
				}
			}

			if( correlato == null){
				correlato = ((TipologiaServizio.CORRELATO.equals(servizio.getTipologiaServizio()) ?
						AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_CORRELATO :
							AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_NORMALE));
			}

			// setto i dati come campi hidden nel pd per portarmeli dietro

			// Preparo il menu
			apsHelper.makeMenu();

			String tmpTitle = tiposervizio + "/" + nomeservizio + " erogato da " + tipoSoggettoErogatore + "/" + nomesoggettoErogatore;
			// aggiorno tmpTitle
			String tmpVersione = asps.getVersione();
			if(apsCore.isShowVersioneAccordoServizioParteSpecifica()==false){
				tmpVersione = null;
			}
			tmpTitle = idAccordoFactory.getUriFromValues(asps.getNome(), 
					servizio.getTipoSoggettoErogatore(), servizio.getNomeSoggettoErogatore(), 
					tmpVersione);

			String profiloSoggettoFruitore = null;
			if ((provider != null) && !provider.equals("")) {
				String [] tmp = provider.split("/");
				if(tmp!=null && tmp.length==2){
					Soggetto soggetto = soggettiCore.getSoggettoRegistro(new IDSoggetto(tmp[0], tmp[1]));
					profiloSoggettoFruitore = soggetto.getVersioneProtocollo();
				}
			} 
			else {
				profiloSoggettoFruitore = servFru.getVersioneProtocollo();
//				Soggetto soggetto = soggettiCore.getSoggettoRegistro(new IDSoggetto(servFru.getTipo(),servFru.getNome()));
//				profiloSoggettoFruitore = soggetto.getVersioneProtocollo();
			}
			
			
			
			String profiloValue = profiloSoggettoFruitore;
			if(profilo!=null && !"".equals(profilo) && !"-".equals(profilo)){
				profiloValue = profilo;
			}

			String protocollo = apsCore.getProtocolloAssociatoTipoServizio(tiposervizio);
			List<String> versioniProtocollo = apsCore.getVersioniProtocollo(protocollo);
			List<String> tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);

			// Prendo la lista di soggetti (tranne quello del servizio)
			// e la metto in un array
			List<Soggetto> soggList = soggettiCore.soggettiRegistroList("", new Search());
			String[] soggettiList = null;
			String[] soggettiListLabel = null;
			List<String> soggettiListVector = new ArrayList<String>();
			List<String> soggettiListLabelVector = new ArrayList<String>();
			for (int i = 0; i < soggList.size(); i++) {
				Soggetto fru = soggList.get(i);
				if(tipiSoggettiCompatibiliAccordo.contains(fru.getTipo())){
					soggettiListVector.add("" + fru.getId());
					soggettiListLabelVector.add(fru.getTipo() + "/" + fru.getNome());
				}
			}
			soggettiList = soggettiListVector.toArray(new String[1]);
			soggettiListLabel = soggettiListLabelVector.toArray(new String[1]);

			// Versioni
			String[] versioniValues = new String[versioniProtocollo.size()+1];
			String[] versioniLabel = new String[versioniProtocollo.size()+1];
			versioniLabel[0] = "usa versione fruitore";
			versioniValues[0] = "-";
			for (int i = 0; i < versioniProtocollo.size(); i++) {
				String tmp = versioniProtocollo.get(i);
				versioniLabel[i+1] = tmp;
				versioniValues[i+1] = tmp;
			}

			//se passo dal link diretto di ripristino stato imposto il nuovo stato
			if(backToStato != null)
				statoPackage = backToStato;
			
			
			String nomefru = servFru.getNome();
			String tipofru = servFru.getTipo();
			IDSoggetto idSF = new IDSoggetto(tipofru, nomefru);
			Soggetto soggFru = soggettiCore.getSoggettoRegistro(idSF);
			provider = "" + soggFru.getId();
			if(statoPackage==null)
				statoPackage = servFru.getStatoPackage();
			String oldStatoPackage = servFru.getStatoPackage();	


			Connettore connettore = servFru.getConnettore();

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if (ServletUtils.isEditModeInProgress(request)) {
				// setto la barra del titolo
				List<Parameter> lstParm = new ArrayList<Parameter>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + tmpTitle, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ idServizio),
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ idSoggettoErogatoreDelServizio)
						));
				lstParm.add(new Parameter(tipofru + "/" + nomefru, null));

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );


				Map<String, String> props = connettore.getProperties();

				// Profilo
				if ((profilo==null || "".equals(profilo)) &&  servFru.getVersioneProtocollo() != null)
					profilo = servFru.getVersioneProtocollo();

				if ((clientAuth==null || "".equals(clientAuth))){
					if(servFru.getClientAuth() != null){
						if(servFru.getClientAuth()!=null){
							clientAuth = servFru.getClientAuth().toString();
						}
					}else
						clientAuth = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
				}

				if (endpointtype == null) {
					if ((connettore.getCustom()!=null && connettore.getCustom()) && !connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_HTTPS)) {
						endpointtype = ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM;
						tipoconn = connettore.getTipo();
					} else
						endpointtype = connettore.getTipo();
				}
				
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
				
				if (url == null) {
					url = props.get(CostantiDB.CONNETTORE_HTTP_LOCATION);
				}
				if (nome == null) {
					nome = props.get(CostantiDB.CONNETTORE_JMS_NOME);
					tipo = props.get(CostantiDB.CONNETTORE_JMS_TIPO);
					user = props.get(CostantiDB.CONNETTORE_USER);
					password = props.get(CostantiDB.CONNETTORE_PWD);
					initcont = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL);
					urlpgk = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG);
					provurl = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL);
					connfact = props.get(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY);
					sendas = props.get(CostantiDB.CONNETTORE_JMS_SEND_AS);
				}
				
				autenticazioneHttp = connettoriHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);
				
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
								httpspwdkey.equals(httpspwd) )
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
					httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
					httpshostverify = true;
				}

				if(backToStato == null){
					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();
					dati.addElement(ServletUtils.getDataElementForEditModeFinished());

					dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idServizio, null, null, dati);

					dati = apsHelper.addServiziFruitoriToDati(dati, provider, "", "", soggettiList, soggettiListLabel, idServizio,
							idServizioFruitore,TipoOperazione.CHANGE, idSoggettoErogatoreDelServizio, "", "", nomeservizio, tiposervizio,  correlato,
							statoPackage,oldStatoPackage,asps.getStatoPackage(),null,validazioneDocumenti,
							null,null);

					dati = apsHelper.addFruitoreToDati(TipoOperazione.CHANGE, versioniLabel, versioniValues, profilo, clientAuth, dati, 
							oldStatoPackage, idServizio, idServizioFruitore, idSoggettoErogatoreDelServizio,
							nomeservizio, tiposervizio, provider);

					if (!InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
						dati = connettoriHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, null, 
								url, nome,
								tipo, user, password, initcont, urlpgk, provurl,
								connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,TipoOperazione.CHANGE, httpsurl,
								httpstipologia, httpshostverify, httpspath, httpstipo,
								httpspwd, httpsalgoritmo, httpsstato, httpskeystore,
								httpspwdprivatekeytrust, httpspathkey, httpstipokey,
								httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
								tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, idServizio, idServizioFruitore,
								idSoggettoErogatoreDelServizio, null, null, null, null,
								oldStatoPackage, true,
								isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);
					}else{
						//spostato nell'helper
					}

					pd.setDati(dati);

					if(apsCore.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(servFru.getStatoPackage())){
						pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
					}

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
							ForwardParams.CHANGE());
				}
			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.serviziFruitoriCheckData(TipoOperazione.CHANGE,
					soggettiList, idServizio, "", "", "", "", provider,
					endpointtype, url, nome, tipo, user, password, initcont,
					urlpgk, provurl, connfact, sendas, "", "",
					idServizioFruitore, profilo, httpsurl,
					httpstipologia, httpshostverify, httpspath, httpstipo,
					httpspwd, httpsalgoritmo, httpsstato, httpskeystore,
					httpspwdprivatekeytrust, httpspathkey, httpstipokey,
					httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
					tipoconn,clientAuth,validazioneDocumenti,backToStato,autenticazioneHttp,
					listExtendedConnettore);
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParm = new ArrayList<Parameter>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + tmpTitle, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ idServizio),
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ idSoggettoErogatoreDelServizio)
						));
				lstParm.add(new Parameter(tipofru + "/" + nomefru, null));

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idServizio, null, null, dati);

				dati = apsHelper.addServiziFruitoriToDati(dati, provider, "", "", soggettiList, soggettiListLabel, idServizio,
						idServizioFruitore, TipoOperazione.CHANGE, idSoggettoErogatoreDelServizio, "", "", nomeservizio, tiposervizio,  correlato,
						statoPackage,oldStatoPackage,asps.getStatoPackage(),null,validazioneDocumenti,null,null);

				dati = apsHelper.addFruitoreToDati(TipoOperazione.CHANGE, versioniLabel, versioniValues, profiloValue, clientAuth, dati, 
						oldStatoPackage, idServizio, idServizioFruitore, idSoggettoErogatoreDelServizio, nomeservizio, tiposervizio, provider);

				if (!InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
					dati = connettoriHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, null, 
							url, nome,
							tipo, user, password, initcont, urlpgk, provurl,
							connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,TipoOperazione.CHANGE, httpsurl,
							httpstipologia, httpshostverify, httpspath, httpstipo,
							httpspwd, httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey, httpstipokey,
							httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
							tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, idServizio, idServizioFruitore,
							idSoggettoErogatoreDelServizio, null, null, null, null,
							oldStatoPackage, true,
							isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);
				}else{
					//spostato nell'helper
				}

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
						ForwardParams.CHANGE());
			}

			if(actionConfirm == null){
				if(backToStato != null){

					// setto la barra del titolo
					List<Parameter> lstParm = new ArrayList<Parameter>();

					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
					lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + tmpTitle, 
							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
							new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ idServizio),
							new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ idSoggettoErogatoreDelServizio)
							));
					lstParm.add(new Parameter(tipofru + "/" + nomefru, null));

					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, lstParm );
					
					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());

					dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idServizio, null, null, dati);
					
					dati = apsHelper.addServiziFruitoriToDatiAsHidden(dati, provider, "", "", soggettiList, soggettiListLabel, idServizio,
							idServizioFruitore, TipoOperazione.CHANGE, idSoggettoErogatoreDelServizio, "", "", nomeservizio, tiposervizio,  correlato,statoPackage,oldStatoPackage,asps.getStatoPackage(),null,validazioneDocumenti);

					dati = apsHelper.addFruitoreToDatiAsHidden(TipoOperazione.CHANGE, versioniLabel, versioniValues, profiloValue, clientAuth, dati, 
							oldStatoPackage, idServizio, idServizioFruitore, idSoggettoErogatoreDelServizio, nomeservizio, tiposervizio, provider);

					if (!InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
						dati = connettoriHelper.addEndPointToDatiAsHidden(dati, endpointtype, url, nome,
								tipo, user, password, initcont, urlpgk, provurl,
								connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,TipoOperazione.CHANGE, httpsurl,
								httpstipologia, httpshostverify, httpspath, httpstipo,
								httpspwd, httpsalgoritmo, httpsstato, httpskeystore,
								httpspwdprivatekeytrust, httpspathkey, httpstipokey,
								httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
								tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, idServizio, idServizioFruitore,
								idSoggettoErogatoreDelServizio, null, null, null, null,
								oldStatoPackage);
					}else{
						//spostato nell'helper
					}
					

					String msg = "&Egrave; stato richiesto di ripristinare lo stato dell soggetto fruitore [{0}] in operativo. Tale operazione permetter&agrave; successive modifiche all''accordo. Vuoi procedere?";
					
					pd.setMessage(MessageFormat.format(msg, tipofru + "/" + nomefru));
					
					pd.setDati(dati);
					
					String[][] bottoni = { 
							{ Costanti.LABEL_MONITOR_BUTTON_ANNULLA, 
								Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_PREFIX +
								Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_SUFFIX

							},
							{ Costanti.LABEL_MONITOR_BUTTON_OK,
								Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_PREFIX +
								Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_SUFFIX }};

					pd.setBottoni(bottoni );

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeConfirm(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
							ForwardParams.CHANGE());
					
				}
			}

			// Modifico i dati del fruitore nel db
			Connettore connettoreNew = null;
			if (!InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
				connettoreNew = new Connettore();
				connettoreNew.setNome("CNT_SF_" + tipofru + "/" + nomefru + "_" + tipoSoggettoErogatore + "/" + nomesoggettoErogatore + "_" + tiposervizio + "/" + nomeservizio);
				connettoreNew.setId(connettore.getId());
	
				String oldConnT = connettore.getTipo();
				if ((connettore.getCustom()!=null && connettore.getCustom()) && !connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_HTTPS)){
					// mantengo vecchie proprieta connettore custom
					for(int i=0; i<connettore.sizePropertyList(); i++){
						connettoreNew.addProperty(connettore.getProperty(i));
					}
					oldConnT = ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM;
				}
				connettoriHelper.fillConnettore(connettoreNew, connettoreDebug, endpointtype, oldConnT, tipoconn, url,
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
			}
			else{
				connettoreNew = connettore;
			}

			Fruitore fruitore = new Fruitore();
			fruitore.setId(new Long(provider));
			fruitore.setConnettore(connettoreNew);
			fruitore.setTipo(tipofru);
			fruitore.setNome(nomefru);
			fruitore.setByteWsdlImplementativoErogatore(servFru.getByteWsdlImplementativoErogatore());
			fruitore.setByteWsdlImplementativoFruitore(servFru.getByteWsdlImplementativoFruitore());
			if ("-".equals(profilo) == false)
				fruitore.setVersioneProtocollo(profilo);
			else
				fruitore.setVersioneProtocollo(null);
			if(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT.equals(clientAuth))
				fruitore.setClientAuth(null);
			else
				fruitore.setClientAuth(StatoFunzionalita.toEnumConstant(clientAuth));
			// Prendo i dati del soggetto erogatore del servizio
			Soggetto SE = soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggettoErogatoreDelServizio));
			tipoSoggettoErogatore = SE.getTipo();
			nomesoggettoErogatore = SE.getNome();

			AccordoServizioParteSpecifica serviziosp = apsCore.getAccordoServizioParteSpecifica(idServizioInt);

			// Elimino il vecchio fruitore ed aggiungo il nuovo
			for (int i = 0; i < serviziosp.sizeFruitoreList(); i++) {
				Fruitore tmpFru = serviziosp.getFruitore(i);
				if (tmpFru.getId().longValue() == servFru.getId().longValue()) {
					serviziosp.removeFruitore(i);
					break;
				}
			}


			// stato
			fruitore.setStatoPackage(statoPackage);

			// Check stato
			if(apsCore.isShowGestioneWorkflowStatoDocumenti()){

				try{
					apsCore.validaStatoFruitoreAccordoServizioParteSpecifica(fruitore, serviziosp);
				}catch(ValidazioneStatoPackageException validazioneException){

					// Setto messaggio di errore
					pd.setMessage(validazioneException.toString());

					List<Parameter> lstParm = new ArrayList<Parameter>();

					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
					lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + tmpTitle, 
							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
							new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ idServizio),
							new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ idSoggettoErogatoreDelServizio)
							));
					lstParm.add(new Parameter(tipofru + "/" + nomefru, null));

					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, lstParm );

					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());

					dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idServizio, null, null, dati);

					dati = apsHelper.addServiziFruitoriToDati(dati, provider, "", "", soggettiList, soggettiListLabel, idServizio, 
							idServizioFruitore, TipoOperazione.CHANGE, idSoggettoErogatoreDelServizio, "", "", nomeservizio, tiposervizio,  
							correlato,statoPackage,oldStatoPackage,asps.getStatoPackage(),null,validazioneDocumenti,
							null,null);

					dati = apsHelper.addFruitoreToDati(TipoOperazione.CHANGE, versioniLabel, versioniValues, profiloValue, clientAuth, dati, 
							oldStatoPackage, idServizio, idServizioFruitore, idSoggettoErogatoreDelServizio, nomeservizio, tiposervizio, provider);

					if (!InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
						dati = connettoriHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, null, 
								url,
								nome, tipo, user, password, initcont, urlpgk,
								provurl, connfact, sendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,TipoOperazione.CHANGE, httpsurl,
								httpstipologia, httpshostverify, httpspath,
								httpstipo, httpspwd, httpsalgoritmo, httpsstato,
								httpskeystore, httpspwdprivatekeytrust,
								httpspathkey, httpstipokey, httpspwdkey,
								httpspwdprivatekey, httpsalgoritmokey,
								tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, idServizio, idServizioFruitore,
								idSoggettoErogatoreDelServizio, null, null, null, null,
								oldStatoPackage, true,
								isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);
					}else{
						//spostato nell'helper
					}

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
							ForwardParams.CHANGE());
				}
			}

			serviziosp.addFruitore(fruitore);
			String superUser = ServletUtils.getUserLoginFromSession(session);
			apsCore.performUpdateOperation(superUser, apsHelper.smista(), serviziosp);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.SERVIZI_FRUITORI;

			ricerca = apsHelper.checkSearchParameters(idLista, ricerca);

			List<Fruitore> lista = apsCore.serviziFruitoriList(idServizioInt, ricerca);

			apsHelper.prepareServiziFruitoriList(lista, idServizio, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished( mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
					ForwardParams.CHANGE());


		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
					ForwardParams.CHANGE());
		} 
	}
}
