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
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
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

			ConnettoriHelper connettoriHelper = new ConnettoriHelper(request, pd, session);
			SoggettiHelper soggettiHelper = new SoggettiHelper(request, pd, session);
			
			String id = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ID);
			int idInt = Integer.parseInt(id);
				
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
			connettoriHelper.makeMenu();

			// Prendo nome, tipo e pdd dal db
			SoggettiCore soggettiCore = new SoggettiCore();			
			
			SoggettoCtrlStat scs = soggettiCore.getSoggettoCtrlStat(idInt);
			Soggetto ss = scs.getSoggettoReg();
			org.openspcoop2.core.config.Soggetto ssconf = scs.getSoggettoConf();
			String nomeprov = scs.getNome();
			String tipoprov = scs.getTipo();
			String tmpTitle = tipoprov + "/" + nomeprov;
			// String pdd = ss.getServer();
			Connettore c = ss.getConnettore();
			
			Boolean isConnettoreCustomUltimaImmagineSalvata = c.getCustom();

			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(c, ConnettoreServletType.SOGGETTO, soggettiCore, 
							request, session, (endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro
			
			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if(ServletUtils.isEditModeInProgress(request)){
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, SoggettiCostanti.LABEL_SOGGETTI, 
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST, "Connettore di " + tmpTitle);
			
				if (endpointtype == null) {
					if ( (c.getCustom()!=null && c.getCustom()) && !c.getTipo().equals(TipiConnettore.HTTPS.toString())) {
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
					httpshostverifyS = Costanti.CHECK_BOX_ENABLED;
					httpshostverify = true;
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

				dati = connettoriHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, null,
						url, nome,
						tipo, user, password, initcont, urlpgk, provurl, 
						connfact, sendas, SoggettiCostanti.OBJECT_NAME_SOGGETTI,TipoOperazione.CHANGE, httpsurl, httpstipologia,
						httpshostverify, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, httpspwdprivatekey,
						httpsalgoritmokey, tipoconn, SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT, id, nomeprov,
						tipoprov, null, null, null, null, true, 
						isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, SoggettiCostanti.TIPO_OPERAZIONE_ENDPOINT);
			}

			// Controlli sui campi immessi
			boolean isOk = soggettiHelper.soggettiEndPointCheckData(TipoOperazione.CHANGE,listExtendedConnettore);
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

				dati = connettoriHelper.addEndPointToDati(dati,  connettoreDebug, endpointtype, autenticazioneHttp, null,
						url, nome,
						tipo, user, password, initcont, urlpgk, provurl,
						connfact, sendas, SoggettiCostanti.OBJECT_NAME_SOGGETTI,TipoOperazione.CHANGE, httpsurl, httpstipologia,
						httpshostverify, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, httpspwdprivatekey,
						httpsalgoritmokey, tipoconn, SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT, id, nomeprov,
						tipoprov, null, null, null, null, true,
						isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, SoggettiCostanti.TIPO_OPERAZIONE_ENDPOINT);
			}

			// Modifico i dati del soggetto nel db
			String oldConnT = c.getTipo();
			if ((c.getCustom()!=null && c.getCustom()) && !c.getTipo().equals(TipiConnettore.HTTPS.toString()))
				oldConnT = TipiConnettore.CUSTOM.toString();
			connettoriHelper.fillConnettore(c, connettoreDebug, endpointtype, oldConnT, tipoconn, url, nome, tipo, user,
					password, initcont, urlpgk, provurl, connfact, sendas,
					httpsurl, httpstipologia, httpshostverify, httpspath,
					httpstipo, httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey,
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
