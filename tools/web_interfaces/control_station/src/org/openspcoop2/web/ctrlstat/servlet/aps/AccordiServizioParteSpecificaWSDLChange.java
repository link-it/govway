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


package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

/**
 * serviziWSDLChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaWSDLChange extends Action {

	private String   id, tipo, wsdl;
	private boolean validazioneDocumenti = true;
	private boolean decodeRequestValidazioneDocumenti = false;
	private String editMode = null;

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

			this.editMode = null;

			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			ConnettoriHelper connettoriHelper = new ConnettoriHelper(request, pd, session);
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);

			this.id = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			this.tipo = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO);
			this.wsdl = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL);

			// boolean decodeReq = false;
			String ct = request.getContentType();
			if ((ct != null) && (ct.indexOf(Costanti.MULTIPART) != -1)) {
				// decodeReq = true;
				this.decodeRequestValidazioneDocumenti = false; // init
				this.decodeRequest(request);
			}

			if(ServletUtils.isEditModeInProgress(this.editMode) && ServletUtils.isEditModeInProgress(request)){
				// primo accesso alla servlet
				this.validazioneDocumenti = true;
			}else{
				if(!this.decodeRequestValidazioneDocumenti){
					String tmpValidazioneDocumenti = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
					if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
						this.validazioneDocumenti = true;
					}else{
						this.validazioneDocumenti = false;
					}
				}
			}

			int idServ = Integer.parseInt(this.id);

			// Preparo il menu
			apsHelper.makeMenu();

			// Prendo il nome, il tipo e il wsdl attuale del servizio
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);

			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idServ);
			Servizio ss = asps.getServizio();
			String nomeservizio = ss.getNome();
			String tiposervizio = ss.getTipo();
			String tmpTitle = tiposervizio + "/" + nomeservizio;
			// aggiorno tmpTitle
			String tmpVersione = asps.getVersione();
			if(apsCore.isShowVersioneAccordoServizioParteSpecifica()==false){
				tmpVersione = null;
			}
			tmpTitle = idAccordoFactory.getUriFromValues(asps.getNome(), 
					ss.getTipoSoggettoErogatore(), ss.getNomeSoggettoErogatore(), 
					tmpVersione);

			// Prendo Accordo di servizio parte comune
			AccordoServizioParteComune as = apcCore.getAccordoServizio(idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));

			List<String> versioniProtocollo = null;
			//String profiloReferente = soggettiCore.getSoggettoRegistro(new IDSoggetto(as.getSoggettoReferente().getTipo(),as.getSoggettoReferente().getNome())).getVersioneProtocollo();
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo());
			versioniProtocollo = apsCore.getVersioniProtocollo(protocollo);

			String oldwsdl = "";
			byte[] wsdlbyte = null;
			String tipologiaDocumentoScaricare = null; 
			if (this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_EROGATORE)) {
				wsdlbyte = asps.getByteWsdlImplementativoErogatore();
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_EROGATORE;
			}
			if (this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_FRUITORE)) {
				wsdlbyte = asps.getByteWsdlImplementativoFruitore();
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_FRUITORE;
			}
			if (wsdlbyte != null) {
				oldwsdl = new String(wsdlbyte);
			}
			Soggetto soggettoErogatoreID = soggettiCore.getSoggettoRegistro(new IDSoggetto(ss.getTipoSoggettoErogatore(),ss.getNomeSoggettoErogatore()));

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (ServletUtils.isEditModeInProgress(this.editMode) && ServletUtils.isEditModeInProgress(request)) {

				List<Parameter> lstParm = new ArrayList<Parameter>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter( tmpTitle, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE ,
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+this.id)
				//				,
				//						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, nomeservizio),
				//						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, tiposervizio)
						));
				if(apcCore.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo)){
					if(this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_EROGATORE))
						lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_WSDL_IMPLEMENTATIVO_EROGATORE_DI + tmpTitle , null));
					if(this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_FRUITORE))
						lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_WSDL_IMPLEMENTATIVO_FRUITORE_DI + tmpTitle , null));
				}else 
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_WSDL_IMPLEMENTATIVO_DI + tmpTitle , null));

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, this.id, null, null, dati);


				dati = apsHelper.addWSDLToDati(TipoOperazione.OTHER, apsHelper.getSize(), asps, oldwsdl, this.tipo, this.validazioneDocumenti,
						dati, tipologiaDocumentoScaricare);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
						AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.accordiParteSpecificaWSDLCheckData(pd, this.tipo, this.wsdl, asps, as, this.validazioneDocumenti);
			if (!isOk) {
				List<Parameter> lstParm = new ArrayList<Parameter>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter( tmpTitle, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE ,
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+this.id)
				//				,
				//						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, nomeservizio),
				//						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, tiposervizio)
						));
				if(this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_EROGATORE))
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_WSDL_IMPLEMENTATIVO_EROGATORE_DI + tmpTitle , null));
				if(this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_FRUITORE))
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_WSDL_IMPLEMENTATIVO_FRUITORE_DI + tmpTitle , null));

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati =apcHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, this.id, null, null, dati);

				dati = apsHelper.addWSDLToDati(TipoOperazione.OTHER,  apsHelper.getSize(), asps, oldwsdl, this.tipo, this.validazioneDocumenti, dati, tipologiaDocumentoScaricare);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
						AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
			}

			// Modifico i dati del wsdl del servizio nel db
			if (this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_EROGATORE)) {
				asps.setByteWsdlImplementativoErogatore(this.wsdl.getBytes());
			}
			if (this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_FRUITORE)) {
				asps.setByteWsdlImplementativoFruitore(this.wsdl.getBytes());
			}

			// effettuo le operazioni
			String superUser = ServletUtils.getUserLoginFromSession(session);
			apsCore.performUpdateOperation(superUser, apsHelper.smista(), asps);

			// visualizzo la schermata di modifica del servizio
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, 
					new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null),
					new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST),
					new Parameter(tmpTitle, null)					);

			AccordoServizioParteSpecifica aspsT = apsCore.getAccordoServizioParteSpecifica(Long.parseLong(this.id));
			Servizio servizio = aspsT.getServizio();

			String[] ptList = null;
			String[] soggettiList = null;
			String[] soggettiListLabel = null;
			String[] accordiList = null;
			String[] accordiListLabel = null;
			// soggetti
			List<Soggetto> list = soggettiCore.soggettiRegistroList("", new Search(true));
			List<String> tipiServizi = apsCore.getTipiServiziGestiti();
			if (list.size() > 0) {
				soggettiList = new String[list.size()];
				soggettiListLabel = new String[list.size()];
				int i = 0;
				for (Soggetto soggetto : list) {
					soggettiList[i] = soggetto.getId().toString();
					soggettiListLabel[i] = soggetto.getTipo() + "/" + soggetto.getNome();
					i++;
				}
			}

			// accordi
			PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();

			boolean [] permessi = new boolean[2];
			permessi[0] = pu.isServizi();
			permessi[1] = pu.isAccordiCooperazione();
			List<AccordoServizioParteComune> listAccordi =  
					AccordiServizioParteComuneUtilities.accordiListFromPermessiUtente(apcCore, superUser, new Search(true), permessi);

			//				List<AccordoServizioParteComune> listAccordi = apcCore.accordiList("", new Search(true));
			if (listAccordi.size() > 0) {
				accordiList = new String[listAccordi.size()];
				accordiListLabel = new String[listAccordi.size()];
				int i = 0;
				for (AccordoServizioParteComune accordoServizio : listAccordi) {
					accordiList[i] = accordoServizio.getId().toString();
					accordiListLabel[i] = accordoServizio.getNome();
					i++;
				}
			}

			String nomeSoggettoErogatore = servizio.getNomeSoggettoErogatore();
			String tipoSoggettoErogatore = servizio.getTipoSoggettoErogatore();
			String provString = tipoSoggettoErogatore + "/" + nomeSoggettoErogatore;
			String servcorr = "";
			if (TipologiaServizio.CORRELATO.equals(servizio.getTipologiaServizio()))
				servcorr = Costanti.CHECK_BOX_ENABLED;
			else
				servcorr = Costanti.CHECK_BOX_DISABLED;
			String accordo = aspsT.getIdAccordo().toString();
			String profilo = aspsT.getVersioneProtocollo();
			String portType = aspsT.getPortType();
			String descrizione = aspsT.getDescrizione();
			String statoPackage = aspsT.getStatoPackage();
			String nome_aps = aspsT.getNome();
			String versione = aspsT.getVersione();

			Connettore connettore = servizio.getConnettore();

			// if(endpointtype==null || endpointtype.equals(""))
			// endpointtype = connettore.getTipo();
			//			
			// Map<String,String> properties =
			// connettore.getProperties();
			String connettoreDebug = null;
			String endpointtype = null;
			String tipoconn = null;
			String autenticazioneHttp = null;
			String url = null;
			String nome = null;
			String initcont = null;
			String urlpgk = null;
			String provurl = null;
			String connfact = null;
			String sendas = null;
			String provider = "";
			String user = "";
			String password = "";
			String httpsurl = null;
			String httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_SSLV3_TYPE ;
			boolean httpshostverify = true;
			String httpspath = "";
			String httpstipo = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
			String httpspwd = "";
			String httpsalgoritmo = "";
			boolean httpsstato = false;
			String httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
			String httpspwdprivatekeytrust = "";
			String httpspathkey = "";
			String httpstipokey = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
			String httpspwdkey = "";
			String httpspwdprivatekey = "";
			String httpsalgoritmokey = "";
			if ((endpointtype == null) || (url == null) || (nome == null)) {
				Map<String, String> props = connettore.getProperties();

				if (endpointtype == null) {
					if ((connettore.getCustom()!=null && connettore.getCustom()) && !connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_HTTPS)) {
						endpointtype = ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM;
						tipoconn = connettore.getTipo();
					} else
						endpointtype = connettore.getTipo();
				}
				autenticazioneHttp = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
				user = props.get(CostantiDB.CONNETTORE_USER);
				password = props.get(CostantiDB.CONNETTORE_PWD);
				autenticazioneHttp = connettoriHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);
				
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
				
				// http
				if (url == null) {
					url = props.get(CostantiDB.CONNETTORE_HTTP_LOCATION);
				}
				
				// jms
				if (nome == null) {
					nome = props.get(CostantiDB.CONNETTORE_JMS_NOME);
					this.tipo = props.get(CostantiDB.CONNETTORE_JMS_TIPO);
					initcont = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL);
					urlpgk = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG);
					provurl = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL);
					connfact = props.get(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY);
					sendas = props.get(CostantiDB.CONNETTORE_JMS_SEND_AS);
					
				}
				if (httpsurl == null) {
					httpsurl = props.get(CostantiDB.CONNETTORE_HTTPS_LOCATION);
					httpstipologia = props.get(CostantiDB.CONNETTORE_HTTPS_SSL_TYPE);
					httpshostverify = Boolean.valueOf(props.get(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER));
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
								httpspwdkey.equals(httpspwd) &&
								httpsalgoritmokey.equals(httpsalgoritmo))
							httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
						else
							httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;
					}
				}
			}

			Boolean isConnettoreCustomUltimaImmagineSalvata = connettore.getCustom();
			
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(connettore, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_CHANGE, apsCore, 
							request, session, true, null);
			
			// Lista port-type associati all'accordo di servizio
			// se l'accordo e' selezionato allora prendo quello selezionato
			// altrimenti il primo
			// della lista
			if (accordo != null && !"".equals(accordo)) {
				as = apcCore.getAccordoServizio(Long.parseLong(accordo));
			} else {
				as = apcCore.getAccordoServizio(idAccordoFactory.getIDAccordoFromUri(aspsT.getAccordoServizioParteComune()));
			}

			List<PortType> portTypes = apcCore.accordiPorttypeList(as.getId().intValue(), new Search(true));
			if (portTypes.size() > 0) {
				ptList = new String[portTypes.size() + 1];
				ptList[0] = "-";
				int i = 1;
				for (Iterator<PortType> iterator = portTypes.iterator(); iterator.hasNext();) {
					PortType portType2 = iterator.next();
					ptList[i] = portType2.getNome();
					i++;
				}
			}

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, this.id, null, null, dati);

			dati = apsHelper.addServiziToDati(dati, nomeservizio, tiposervizio, provider, provString, soggettiList,
					soggettiListLabel, accordo, accordiList, accordiListLabel, servcorr, "", "",
					TipoOperazione.CHANGE, this.id, tipiServizi, profilo, portType, ptList,
					(aspsT.getPrivato()!=null && aspsT.getPrivato()),idAccordoFactory.getUriFromAccordo(as),
					descrizione,soggettoErogatoreID.getId(), statoPackage,statoPackage,
					nome_aps, versione, versioniProtocollo,this.validazioneDocumenti,
					null,null,null,protocollo,true,null);

			dati = connettoriHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, null, 
					url, nome,
					this.tipo, user, password, initcont, urlpgk, provurl,
					connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,TipoOperazione.CHANGE, httpsurl, httpstipologia,
					httpshostverify, httpspath, httpstipo, httpspwd,
					httpsalgoritmo, httpsstato, httpskeystore,
					httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey, tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, this.id,
					nomeservizio, tiposervizio, null, null, null, null, true,
					isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);

			pd.setDati(dati);


			// imposto la baseurl per il redirect
			gd = generalHelper.initGeneralData(request,AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
					AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
					AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
		}  
	}



	public void decodeRequest(HttpServletRequest request) throws Exception {
		try {
			ServletInputStream in = request.getInputStream();
			BufferedReader dis = new BufferedReader(new InputStreamReader(in));
			String line = dis.readLine();
			while (line != null) {
				if (line.indexOf("\""+Costanti.DATA_ELEMENT_EDIT_MODE_NAME+"\"") != -1) {
					line = dis.readLine();
					this.editMode = dis.readLine();
				}
				if (line.indexOf("Content-Disposition: form-data; name=\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID+"\"") != -1) {
					line = dis.readLine();
					this.id = dis.readLine();
				}
				if (line.indexOf("Content-Disposition: form-data; name=\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO+"\"") != -1) {
					line = dis.readLine();
					this.tipo = dis.readLine();
				}
				if (line.indexOf("Content-Disposition: form-data; name=\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL+"\"") != -1) {
					int startId = line.indexOf(Costanti.MULTIPART_FILENAME );
					startId = startId + 10;
					// int endId = line.lastIndexOf("\"");
					// String tmpNomeFile = line.substring(startId, endId);
					line = dis.readLine();
					line = dis.readLine();
					this.wsdl = "";
					while (!line.startsWith("-----") || (line.startsWith("-----") && ((line.indexOf(Costanti.MULTIPART_BEGIN) != -1) ||
							(line.indexOf(Costanti.MULTIPART_END) != -1)))) {
						if("".equals(this.wsdl))
							this.wsdl = line;
						else
							this.wsdl = this.wsdl + "\n" + line;
						line = dis.readLine();
					}
				}
				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI+"\"") != -1) {
					this.decodeRequestValidazioneDocumenti = true;
					line = dis.readLine();
					String tmpValidazioneDocumenti = dis.readLine();
					if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
						this.validazioneDocumenti = true;
					}
					else{
						this.validazioneDocumenti = false;
					}
				}
				line = dis.readLine();
			}
			in.close();
		} catch (IOException ioe) {
			throw new Exception(ioe);
		}
	}
}
