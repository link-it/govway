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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.mail.internet.ContentType;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddTipologia;
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
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

/**
 * serviziAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaAdd extends Action {


	private String connettoreDebug;
	private String   nomeservizio, tiposervizio, provider, accordo,
	servcorr, endpointtype, endpointtype_check, endpointtype_ssl, tipoconn, url, nome, tipo, user, password, initcont,
	urlpgk, provurl, connfact, sendas, wsdlimpler, wsdlimplfru,
	profilo, portType,descrizione,
	httpsurl, httpstipologia, httpspath,
	httpstipo, httpspwd, httpsalgoritmo,
	httpskeystore, httpspwdprivatekeytrust, httpspathkey,
	httpstipokey, httpspwdkey, httpspwdprivatekey,
	httpsalgoritmokey;
	private String httpshostverifyS, httpsstatoS;
	private boolean httpshostverify, httpsstato;
	private String nomeSoggettoErogatore = "";
	private String tipoSoggettoErogatore = "";
	private boolean privato = false;
	private String statoPackage = "";
	private String nome_aps,versione;
	private boolean validazioneDocumenti = true;
	private boolean decodeRequestValidazioneDocumenti = false;
	private String editMode = null;
	private String nomePA, nomeSA = null;
	private String oldPortType, oldTipoSoggettoErogatore, oldNomeSoggettoErogatore = null;
	private String autenticazioneHttp;
	private Properties parametersPOST;

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			ConnettoriHelper connettoriHelper = new ConnettoriHelper(request, pd, session);

			this.parametersPOST = null;
			
			this.editMode = null;

			this.nomeservizio = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO);
			this.tiposervizio = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO);
			this.provider = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROVIDER);
			this.accordo = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO);
			this.servcorr = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_SERVIZIO_CORRELATO);
			// this.servpub = request.getParameter("servpub");
			//			this.endpointtype = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE );
			
			this.endpointtype = connettoriHelper.readEndPointType();
			this.tipoconn = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO );
			this.autenticazioneHttp = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			
			this.connettoreDebug = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
			
			// http
			this.url = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL  );
			if(TipiConnettore.HTTP.toString().equals(this.endpointtype)){
				this.user = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME);
				this.password = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD);
			}
			
			// jms
			this.nome = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			this.tipo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			this.initcont = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			this.urlpgk = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			this.provurl = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			this.connfact = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			this.sendas = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(this.endpointtype)){
				this.user = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				this.password = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}
			
			// https
			this.httpsurl = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
			this.httpstipologia = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE );
			this.httpshostverifyS = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			this.httpspath = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION );
			this.httpstipo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			this.httpspwd = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			this.httpsalgoritmo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			this.httpsstatoS = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			this.httpskeystore = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			this.httpspwdprivatekeytrust = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			this.httpspathkey = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			this.httpstipokey = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			this.httpspwdkey = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			this.httpspwdprivatekey = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			this.httpsalgoritmokey = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			if(TipiConnettore.HTTPS.toString().equals(this.endpointtype)){
				this.user = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME);
				this.password = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD);
			}
			
			
			
			this.profilo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROFILO);
			
			this.wsdlimpler = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
			this.wsdlimplfru = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE);
			this.portType = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE);
			this.privato = (request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PRIVATO) != null) &&
					Costanti.CHECK_BOX_ENABLED.equals(request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PRIVATO)) ? true : false;
			this.descrizione = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_DESCRIZIONE);
			this.statoPackage = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);
						
			this.nome_aps = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_APS);
			this.versione = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE);
			this.nomePA = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_PA);
			this.nomeSA = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SA);

			// boolean decodeReq = false;
			String ct = request.getContentType();
			if ((ct != null) && (ct.indexOf(Costanti.MULTIPART) != -1)) {
				// decodeReq = true;
				this.decodeRequestValidazioneDocumenti = false; // init
				this.decodeRequest(request,connettoriHelper);
			}

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
			PddCore pddCore = new PddCore(apsCore);

			if(ServletUtils.isEditModeInProgress(this.editMode) && ServletUtils.isEditModeInProgress(request)){
				// primo accesso alla servlet
				this.validazioneDocumenti = true;
				if (!InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
					String tmpValidazioneDocumenti = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
					if(tmpValidazioneDocumenti!=null){
						if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
							this.validazioneDocumenti = true;
						}else{
							this.validazioneDocumenti = false;
						}
					}
				}
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

			this.httpshostverify = false;
			if (this.httpshostverifyS != null && this.httpshostverifyS.equals(Costanti.CHECK_BOX_ENABLED))
				this.httpshostverify = true;
			this.httpsstato = false;
			if (this.httpsstatoS != null && this.httpsstatoS.equals(Costanti.CHECK_BOX_ENABLED))
				this.httpsstato = true;

			Boolean isConnettoreCustomUltimaImmagineSalvata = null;
			
			Connettore conTmp = null;
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, apsCore, 
							request, session, this.parametersPOST, (this.endpointtype==null), this.endpointtype); // uso endpointtype per capire se è la prima volta che entro
			
			// Preparo il menu
			apsHelper.makeMenu();

			String[] ptList = null;
			// Prendo la lista di soggetti e la metto in un array
			// Prendo la lista di accordi e la metto in un array
			String[] soggettiList = null;
			String[] soggettiListLabel = null;
			String[] accordiList = null;
			String[] accordiListLabel = null;
			// int totSogg = 0, totAcc = 0;

			boolean generaPACheckSoggetto = true;
			boolean accordoPrivato = false;
			String uriAccordo = null;
			IDSoggetto soggettoReferente = null;
			int idReferente = -1;
			// accordi
			//			if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
			//
			//				List<String[]> lstAccordiList = apsCore.getAccordiListLabels(userLogin);
			//
			//				accordiList = lstAccordiList.get(0);
			//				accordiListLabel = lstAccordiList.get(1);
			//
			//			} else {

			PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();

			boolean [] permessi = new boolean[2];
			permessi[0] = pu.isServizi();
			permessi[1] = pu.isAccordiCooperazione();
			List<AccordoServizioParteComune> lista =  
					AccordiServizioParteComuneUtilities.accordiListFromPermessiUtente(apcCore, userLogin, new Search(true), permessi);
			
			int accordoPrimoAccesso = -1;

			if (lista.size() > 0) {
				accordiList = new String[lista.size()];
				accordiListLabel = new String[lista.size()];
				int i = 0;
				for (AccordoServizioParteComune as : lista) {
					accordiList[i] = as.getId().toString();
					soggettoReferente = null;
					idReferente = -1;
					if(as.getSoggettoReferente()!=null && as.getSoggettoReferente().getId()!=null)
						idReferente = as.getSoggettoReferente().getId().intValue();

					if(idReferente>0){
						Soggetto sRef = soggettiCore.getSoggettoRegistro(idReferente);
						soggettoReferente = new IDSoggetto();
						soggettoReferente.setTipo(sRef.getTipo());
						soggettoReferente.setNome(sRef.getNome());
						
						// se ancora non ho scelto l'accordo da mostrare quando entro
						if(accordoPrimoAccesso == -1){
							//mostro il primo accordo che ha tipo che corrisponde a quello di default
							if(apcCore.getProtocolloDefault().equals(soggettiCore.getProtocolloAssociatoTipoSoggetto(sRef.getTipo()))){
								accordoPrimoAccesso = i;
							}
						}
					}
					accordiListLabel[i] = idAccordoFactory.getUriFromValues(as.getNome(),soggettoReferente,as.getVersione());
					i++;
				}
			}
			
			// se ancora non ho scelto l'accordo da mostrare quando entro
			if(accordoPrimoAccesso == -1 && lista.size() > 0){
				// Se entro in questo caso significa che tutti gli accordi di servizio parte comune esistente s
				// possiedono come soggetto referente un tipo di protocollo differente da quello di default.
				// in questo caso prendo il primo che trovo
				accordoPrimoAccesso = 0;
			}
			
			//			}


			String postBackElementName = ServletUtils.getPostBackElementName(request);

			// Controllo se ho modificato l'accordo, se si allora suggerisco il referente dell'accordo
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO)){
					this.provider = null;
					this.tiposervizio = null;
				}  
			}


			// Lista port-type associati all'accordo di servizio
			AccordoServizioParteComune as = null;
			if (this.accordo != null && !"".equals(this.accordo)) {
				as = apcCore.getAccordoServizio(Long.parseLong(this.accordo));
			} else {
				if (accordiList != null){
					if(accordoPrimoAccesso >= 0 && accordoPrimoAccesso < accordiList.length)
					as = apcCore.getAccordoServizio(Long.parseLong(accordiList[accordoPrimoAccesso]));
					if(as!=null)
						this.accordo = as.getId() + "";
				}
			}
			if(as!=null){
				// salvo il soggetto referente
				soggettoReferente = new IDSoggetto(as.getSoggettoReferente().getTipo(), as.getSoggettoReferente().getNome());

				accordoPrivato = as.getPrivato()!=null && as.getPrivato();
				uriAccordo = idAccordoFactory.getUriFromAccordo(as);

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

				if( apsCore.isShowCorrelazioneAsincronaInAccordi() ){
					if (this.portType != null && !"".equals(this.portType) && !"-".equals(this.portType)){
						PortType pt = null;
						for(int i=0; i<as.sizePortTypeList(); i++){
							if(this.portType.equals(as.getPortType(i).getNome())){
								pt = as.getPortType(i);
								break;
							}
						}
						boolean servizioCorrelato = false;
						if(pt!=null){
							for(int i=0; i<pt.sizeAzioneList(); i++){
								Operation op = pt.getAzione(i);
								if(op.getCorrelataServizio()!=null && !pt.getNome().equals(op.getCorrelataServizio()) && op.getCorrelata()!=null){
									servizioCorrelato = true;
									break;
								}
							}
						}
						if(servizioCorrelato){
							this.servcorr=Costanti.CHECK_BOX_ENABLED;
						}
						else{
							this.servcorr=Costanti.CHECK_BOX_DISABLED;
						}							
					}
				}
				
			}


			//String profiloValue = profiloSoggettoErogatore;
			//if(this.profilo!=null && !"".equals(this.profilo) && !"-".equals(this.profilo)){
			//	profiloValue = this.profilo;
			//}

			// Versione
			//String profiloReferente = core.getSoggettoRegistro(new IDSoggetto(as.getSoggettoReferente().getTipo(),as.getSoggettoReferente().getNome())).getProfilo();

			String protocollo = null;
			if(as!=null && as.getSoggettoReferente()!=null){
				protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo());
			}
			else{
				protocollo = apsCore.getProtocolloDefault();
			}
			List<String> versioniProtocollo = apsCore.getVersioniProtocollo(protocollo);
			List<String> tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
			List<String> tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(protocollo);

			// calcolo soggetti compatibili con accordi
			List<Soggetto> list = null;
			if(apsCore.isVisioneOggettiGlobale(userLogin)){
				list = soggettiCore.soggettiRegistroList(null, new Search(true));
			}else{
				list = soggettiCore.soggettiRegistroList(userLogin, new Search(true));
			}

			if (list.size() > 0) {
				List<String> soggettiListTmp = new ArrayList<String>();
				List<String> soggettiListLabelTmp = new ArrayList<String>();
				for (Soggetto soggetto : list) {
					if(tipiSoggettiCompatibiliAccordo.contains(soggetto.getTipo())){
						soggettiListTmp.add(soggetto.getId().toString());
						soggettiListLabelTmp.add(soggetto.getTipo() + "/" + soggetto.getNome());
					}
				}
				
				boolean existsAPCCompatibili = false;
				for (AccordoServizioParteComune aspc : lista) {
					if(tipiSoggettiCompatibiliAccordo.contains(aspc.getSoggettoReferente().getTipo())){
						existsAPCCompatibili = true;
						break;
					}
				}
				
				if(soggettiListTmp.size()>0 && existsAPCCompatibili){
					soggettiList = soggettiListTmp.toArray(new String[1]);
					soggettiListLabel = soggettiListLabelTmp.toArray(new String[1]);
				}
				else{
					if(lista.size()>0){
						protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(lista.get(0).getSoggettoReferente().getTipo());
					}
					else{
						pd.setMessage("Non esistono accordi di servizio parte comune");
						pd.disableEditMode();
						
						Vector<DataElement> dati = new Vector<DataElement>();

						dati.addElement(ServletUtils.getDataElementForEditModeFinished());
						
						pd.setDati(dati);

						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

						return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
								ForwardParams.ADD());
					}
						
					// refresh di tutte le infromazioni
					versioniProtocollo = apsCore.getVersioniProtocollo(protocollo);
					tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
					tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(protocollo);
					
					for (Soggetto soggetto : list) {
						if(tipiSoggettiCompatibiliAccordo.contains(soggetto.getTipo())){
							soggettiListTmp.add(soggetto.getId().toString());
							soggettiListLabelTmp.add(soggetto.getTipo() + "/" + soggetto.getNome());
						}
					}
					soggettiList = soggettiListTmp.toArray(new String[1]);
					soggettiListLabel = soggettiListLabelTmp.toArray(new String[1]);
					
					if(lista.size()>0){
						this.accordo = lista.get(0).getId()+"";
					}
				}
			}

			//String profiloSoggettoErogatore = null;
			if ((this.provider != null) && !this.provider.equals("")) {
				long idErogatore = Long.parseLong(this.provider);
				Soggetto soggetto = soggettiCore.getSoggettoRegistro(idErogatore);
				this.nomeSoggettoErogatore = soggetto.getNome();
				this.tipoSoggettoErogatore = soggetto.getTipo();
				//profiloSoggettoErogatore = soggetto.getVersioneProtocollo();
			} else {
				if(soggettoReferente != null ){
					Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggettoReferente);
					this.provider = soggetto.getId() + "";
					this.nomeSoggettoErogatore = soggetto.getNome();
					this.tipoSoggettoErogatore = soggetto.getTipo();
				}
			}
			
			if(this.tiposervizio == null){
				this.tiposervizio = apsCore.getTipoServizioDefaultProtocollo(protocollo);
			}

			

			// Lista dei servizi applicativi per la creazione automatica
			String [] saSoggetti = null;	
			if(saCore.isGenerazioneAutomaticaPorteApplicative()){
				if ((this.provider != null) && !this.provider.equals("")) {
					int idErogatore = Integer.parseInt(this.provider);

					List<ServizioApplicativo> listaSA = saCore.getServiziApplicativiByIdErogatore(new Long(idErogatore));

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
			}

			// Controllo se il soggetto erogare appartiene ad una pdd di tipo operativo.
			if(this.tipoSoggettoErogatore!=null && !"".equals(this.tipoSoggettoErogatore) 
					&&  this.nomeSoggettoErogatore!=null && !"".equals(this.nomeSoggettoErogatore)){
				IDSoggetto idSoggettoEr = new IDSoggetto(this.tipoSoggettoErogatore, this.nomeSoggettoErogatore);
				Soggetto soggetto = soggettiCore.getSoggettoRegistro(idSoggettoEr );
				
				if (soggetto.getPortaDominio() != null) {
					String nomePdd = soggetto.getPortaDominio();
					
					PdDControlStation portaDominio = pddCore.getPdDControlStation(nomePdd);
					
					if(portaDominio.getTipo().equals(PddTipologia.ESTERNO.toString()))
						generaPACheckSoggetto = false;
					
				} else {
					// se non ho una porta di domini non devo generare la porta applicativa
					generaPACheckSoggetto  =false;
				}
			}



			// Se nomehid = null, devo visualizzare la pagina per l'inserimento dati
			if(ServletUtils.isEditModeInProgress(this.editMode) && ServletUtils.isEditModeInProgress(request)){
				// setto la barra del titolo
				List<Parameter> lstParm = new ArrayList<Parameter>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				if(apsCore.isShowGestioneWorkflowStatoDocumenti()){
					if(this.nomeservizio==null || "".equals(this.nomeservizio)){
						this.statoPackage=StatiAccordo.bozza.toString();
					}
					
					// Se l'accordo Comune che si riferisce ha stato operativo o finale modifico lo stato in operativo
					// Per visualizzare immediatamente all'utente
					if(as!=null && 
							(as.getStatoPackage().equals(StatiAccordo.operativo.toString()) || as.getStatoPackage().equals(StatiAccordo.finale.toString()))
						){
						this.statoPackage = StatiAccordo.operativo.toString(); 
					}
					
				}else{
					this.statoPackage=StatiAccordo.finale.toString();
				}

				if (this.nomeservizio == null) {
					this.nomeservizio = "";
					this.tiposervizio = apsCore.getTipoServizioDefault();
//					this.provider = "";
//					this.accordo = "";
					this.servcorr = "";
					// this.servpub = "";
					if(this.wsdlimpler==null)
						this.wsdlimpler = "";
					if(this.wsdlimplfru==null)
						this.wsdlimplfru = "";
					this.endpointtype = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DISABILITATO;
					this.tipoconn = "";
					this.url = "";
					this.nome = "";
					this.tipo = ConnettoriCostanti.TIPI_CODE_JMS[0];
					this.user = "";
					this.password = "";
					this.initcont = "";
					this.urlpgk = "";
					this.provurl = "";
					this.connfact = "";
					this.sendas = ConnettoriCostanti.TIPO_SEND_AS[0];
					this.profilo = "-";
					this.portType = "-";
					this.descrizione = "";
					this.httpsurl = "";
					this.httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_SSLV3_TYPE;
					this.httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
					this.httpshostverify = true;
					this.httpspath = "";
					this.httpstipo = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
					this.httpspwd = "";
					this.httpsstato = false;
					this.httpskeystore = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DEFAULT;
					this.httpspwdprivatekeytrust = "";
					this.httpspathkey = "";
					this.httpstipokey =ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
					this.httpspwdkey = "";
					this.httpspwdprivatekey = "";
					this.nome_aps="";
					this.versione="1";
				}

				if(this.portType!=null && !"".equals(this.portType) && !"-".equals(this.portType)){

					boolean ptValid = true;
					
					if(ptList!=null && ptList.length>0){
						// controllo che l'attuale port Type sia tra quelli presenti nell'accordo.
						boolean found = false;
						for (String portType : ptList) {
							if(portType.equals(this.portType)){
								found = true;
								break;
							}
						}
						if(!found){
							ptValid = false;
						}
						
					}
					
					if(ptValid){
					
						if(this.nome_aps==null || "".equals(this.nome_aps)){
							this.nome_aps = this.portType;
						}
						else if(this.nome_aps.equals(this.oldPortType)){
							this.nome_aps = this.portType;
						}
	
						if(this.nomeservizio==null || "".equals(this.nomeservizio)){
							this.nomeservizio = this.portType;
						}
						else if(this.nomeservizio.equals(this.oldPortType)){
							this.nomeservizio = this.portType;
						}
	
						if(this.nomePA==null || "".equals(this.nomePA)){
							this.nomePA = this.tipoSoggettoErogatore+this.nomeSoggettoErogatore+"/"+this.tiposervizio+this.portType;
						}
						else if(this.nomePA.equals(this.tipoSoggettoErogatore+this.nomeSoggettoErogatore+"/"+this.tiposervizio+this.oldPortType)){
							this.nomePA = this.tipoSoggettoErogatore+this.nomeSoggettoErogatore+"/"+this.tiposervizio+this.portType;
						}
	
						this.oldPortType = this.portType;
						
					}
					else{
						
						this.nome_aps = null;
						this.nomeservizio = null;
						this.nomePA = null;
						this.portType = null;
						this.oldPortType = null;
						
					}
				}


				if(this.nomePA!=null && !"".equals(this.nomePA)){
					if(this.nomePA.equals(this.oldTipoSoggettoErogatore+this.oldNomeSoggettoErogatore+"/"+this.tiposervizio+this.portType)){
						this.nomePA = this.tipoSoggettoErogatore+this.nomeSoggettoErogatore+"/"+this.tiposervizio+this.portType;
					}
					this.oldTipoSoggettoErogatore = this.tipoSoggettoErogatore;
					this.oldNomeSoggettoErogatore = this.nomeSoggettoErogatore;
				}


				// default
				if(this.httpsalgoritmo==null || "".equals(this.httpsalgoritmo)){
					this.httpsalgoritmo = TrustManagerFactory.getDefaultAlgorithm();
				}
				if(this.httpsalgoritmokey==null || "".equals(this.httpsalgoritmokey)){
					this.httpsalgoritmokey = KeyManagerFactory.getDefaultAlgorithm();
				}
				if(this.httpstipologia==null || "".equals(this.httpstipologia)){
					this.httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_SSLV3_TYPE;
				}
				if(this.httpshostverifyS==null || "".equals(this.httpshostverifyS)){
					this.httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
					this.httpshostverify = true;
				}
				
				String tipoSendas = ConnettoriCostanti.TIPO_SEND_AS[0];
				String tipoJms = ConnettoriCostanti.TIPI_CODE_JMS[0];

				this.autenticazioneHttp = connettoriHelper.getAutenticazioneHttp(this.autenticazioneHttp, this.endpointtype, this.user);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apsHelper.addServiziToDati(dati, this.nomeservizio, this.tiposervizio, this.provider, "", 
						soggettiList, soggettiListLabel, this.accordo, accordiList, accordiListLabel, this.servcorr, 
						this.wsdlimpler, this.wsdlimplfru, TipoOperazione.ADD, "0", tipiServizioCompatibiliAccordo, 
						this.profilo, this.portType, ptList, this.privato,uriAccordo,this.descrizione,-1l,this.statoPackage,this.statoPackage,
						this.nome_aps,this.versione,versioniProtocollo,this.validazioneDocumenti,
						this.nomePA,saSoggetti,this.nomeSA,protocollo,generaPACheckSoggetto,null);

				dati = connettoriHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, null, 
						this.url, this.nome,
						tipoJms, this.user,
						this.password, this.initcont, this.urlpgk,
						this.provurl, this.connfact, tipoSendas,
						AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,TipoOperazione.ADD, this.httpsurl, this.httpstipologia,
						this.httpshostverify, this.httpspath, this.httpstipo, this.httpspwd,
						this.httpsalgoritmo, this.httpsstato, this.httpskeystore,
						this.httpspwdprivatekeytrust, this.httpspathkey,
						this.httpstipokey, this.httpspwdkey, this.httpspwdprivatekey,
						this.httpsalgoritmokey, this.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, null, null,
						null, null, null, null, null, true,
						isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
						ForwardParams.ADD());
			}

			if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
				// il nome del servizio e' quello del porttype selezionato
				this.nomeservizio = this.portType;
			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.serviziCheckData(TipoOperazione.ADD, soggettiList,
					accordiList, this.nomeservizio, this.tiposervizio,
					this.nomeservizio, this.tiposervizio, this.provider,
					this.nomeSoggettoErogatore, this.tipoSoggettoErogatore,
					this.accordo, this.servcorr, this.endpointtype,
					this.url, this.nome, this.tipo, this.user,
					this.password, this.initcont, this.urlpgk, this.provurl,
					this.connfact, this.sendas, this.wsdlimpler,
					this.wsdlimplfru, "0", this.profilo, this.portType,
					accordoPrivato,this.privato, this.httpsurl, this.httpstipologia,
					this.httpshostverify, this.httpspath, this.httpstipo,
					this.httpspwd, this.httpsalgoritmo, this.httpsstato,
					this.httpskeystore, this.httpspwdprivatekeytrust,
					this.httpspathkey, this.httpstipokey,
					this.httpspwdkey, this.httpspwdprivatekey,
					this.httpsalgoritmokey, this.tipoconn,this.nome_aps,this.versione,this.validazioneDocumenti,this.nomePA,null,this.autenticazioneHttp,
					listExtendedConnettore);
			
			if(isOk){
				if(this.nomePA!=null && !"".equals(this.nomePA) && !"-".equals(this.nomePA)){
					if(this.nomeSA==null || "".equals(this.nomeSA) || "-".equals(this.nomeSA)){
						if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
							if(saSoggetti==null || saSoggetti.length==0 || (saSoggetti.length==1 && "-".equals(saSoggetti[0]))){
								pd.setMessage("Prima di poter definire un accordo parte specifica deve essere creato un servizio applicativo erogato dal soggetto "+this.tipoSoggettoErogatore+"/"+this.nomeSoggettoErogatore);
							}
							else{
								pd.setMessage("Non e' possibile creare l'accordo parte specifica senza selezionare un servizio applicativo erogatore");
							}
						}
						else{
							if(saSoggetti==null || saSoggetti.length==0 || (saSoggetti.length==1 && "-".equals(saSoggetti[0]))){
								pd.setMessage("Prima di poter definire un accordo parte specifica deve essere creato un servizio applicativo erogato dal soggetto "+
										this.tipoSoggettoErogatore+"/"+this.nomeSoggettoErogatore+ " od il alternativa non indicare un nome di porta applicativa (non verrà creata)");
							}
							else{
								pd.setMessage("Non e' possibile creare l'accordo parte specifica senza selezionare un servizio applicativo erogatore. In alternativa non indicare un nome di porta applicativa (non verrà creata)");
							}
						}
						isOk = false;
					}
				}
			}
			
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParm = new ArrayList<Parameter>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apsHelper.addServiziToDati(dati, this.nomeservizio, this.tiposervizio, this.provider, "", 
						soggettiList, soggettiListLabel, this.accordo, accordiList, accordiListLabel,
						this.servcorr, this.wsdlimpler, this.wsdlimplfru, TipoOperazione.ADD, "0", tipiServizioCompatibiliAccordo, 
						this.profilo, this.portType, ptList, this.privato,uriAccordo,this.descrizione,-1l,this.statoPackage,
						this.statoPackage,this.nome_aps,this.versione,versioniProtocollo,this.validazioneDocumenti,
						this.nomePA,saSoggetti,this.nomeSA,protocollo,generaPACheckSoggetto,null);

				dati = connettoriHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, null,
						this.url, this.nome, this.tipo, this.user,
						this.password, this.initcont, this.urlpgk,
						this.provurl, this.connfact, this.sendas,
						AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,TipoOperazione.ADD, this.httpsurl, this.httpstipologia,
						this.httpshostverify, this.httpspath, this.httpstipo,
						this.httpspwd, this.httpsalgoritmo, this.httpsstato,
						this.httpskeystore, this.httpspwdprivatekeytrust,
						this.httpspathkey, this.httpstipokey,
						this.httpspwdkey, this.httpspwdprivatekey,
						this.httpsalgoritmokey, this.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, null, null,
						null, null, null, null, null, true,
						isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
						ForwardParams.ADD());
			}

			// Inserisco il servizio nel db
			long idProv = Long.parseLong(this.provider);
			long idAcc = Long.parseLong(this.accordo);

			AccordoServizioParteSpecifica asps = new AccordoServizioParteSpecifica();
			Servizio servizio = new Servizio();
			asps.setServizio(servizio);
			servizio.setNome(this.nomeservizio);
			servizio.setTipo(this.tiposervizio);
			asps.setDescrizione(this.descrizione);
			asps.setIdAccordo(idAcc);
			// nome accordo
			as = apcCore.getAccordoServizio(idAcc);
			asps.setAccordoServizioParteComune(idAccordoFactory.getUriFromAccordo(as));
			asps.setIdSoggetto(idProv);
			servizio.setNomeSoggettoErogatore(this.nomeSoggettoErogatore);
			servizio.setTipoSoggettoErogatore(this.tipoSoggettoErogatore);
			servizio.setTipologiaServizio(((this.servcorr != null) && this.servcorr.equals(Costanti.CHECK_BOX_ENABLED)) ? TipologiaServizio.CORRELATO : TipologiaServizio.NORMALE);
			asps.setSuperUser(ServletUtils.getUserLoginFromSession(session));
			if ("-".equals(this.profilo) == false)
				asps.setVersioneProtocollo(this.profilo);
			else
				asps.setVersioneProtocollo(null);

			asps.setPrivato(this.privato);

			asps.setByteWsdlImplementativoErogatore(((this.wsdlimpler != null) && !this.wsdlimpler.trim().replaceAll("\n", "").equals("")) ? this.wsdlimpler.trim().getBytes() : null);
			asps.setByteWsdlImplementativoFruitore(((this.wsdlimplfru != null) && !this.wsdlimplfru.trim().replaceAll("\n", "").equals("")) ? this.wsdlimplfru.trim().getBytes() : null);
			if (this.portType != null && !"".equals(this.portType) && !"-".equals(this.portType))
				asps.setPortType(this.portType);

			// Connettore
			Connettore connettore = new Connettore();
			// connettore.setNome("CNT_" + this.tiposervizio + "_" +
			// this.nomeservizio);
			if (this.endpointtype.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM))
				connettore.setTipo(this.tipoconn);
			else
				connettore.setTipo(this.endpointtype);

			connettoriHelper.fillConnettore(connettore, this.connettoreDebug, this.endpointtype, this.endpointtype, this.tipoconn, this.url,
					this.nome, this.tipo, this.user, this.password,
					this.initcont, this.urlpgk, this.url, this.connfact,
					this.sendas, this.httpsurl, this.httpstipologia,
					this.httpshostverify, this.httpspath, this.httpstipo,
					this.httpspwd, this.httpsalgoritmo, this.httpsstato,
					this.httpskeystore, this.httpspwdprivatekeytrust,
					this.httpspathkey, this.httpstipokey,
					this.httpspwdkey, this.httpspwdprivatekey,
					this.httpsalgoritmokey,
					listExtendedConnettore);

			servizio.setConnettore(connettore);

			// Accordo di servizio parte specifica
			asps.setNome(this.nome_aps);
			if(apsCore.isShowPulsantiImportExport()){
				asps.setVersione(this.versione);
			}else{
				asps.setVersione("1");
			}

			// stato
			asps.setStatoPackage(this.statoPackage);
			
//			Spostato sopra a livello di edit in progress			
//			// Se l'accordo Comune che si riferisce ha stato operativo o finale modifico lo stato in operativo
//			if(as.getStatoPackage().equals(StatiAccordo.operativo.toString()) || as.getStatoPackage().equals(StatiAccordo.finale.toString())){
//				asps.setStatoPackage(StatiAccordo.operativo.toString()); 
//			}
			
			// Check stato
			if(apsCore.isShowGestioneWorkflowStatoDocumenti()){

				try{
					apsCore.validaStatoAccordoServizioParteSpecifica(asps);
				}catch(ValidazioneStatoPackageException validazioneException){

					// Setto messaggio di errore
					pd.setMessage(validazioneException.toString());
					
					List<Parameter> lstParm = new ArrayList<Parameter>();

					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
					lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, lstParm );

					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());

					dati = apsHelper.addServiziToDati(dati, this.nomeservizio, this.tiposervizio, this.provider, "", 
							soggettiList, soggettiListLabel, this.accordo, accordiList, accordiListLabel, this.servcorr, 
							this.wsdlimpler, this.wsdlimplfru, TipoOperazione.ADD, "0", tipiServizioCompatibiliAccordo, 
							this.profilo, this.portType, ptList, this.privato,uriAccordo,this.descrizione,-1l,this.statoPackage,
							this.statoPackage,this.nome_aps,this.versione,versioniProtocollo,this.validazioneDocumenti,
							this.nomePA,saSoggetti,this.nomeSA,protocollo,generaPACheckSoggetto,null);

					dati = connettoriHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, null,
							this.url, this.nome, this.tipo, this.user,
							this.password, this.initcont, this.urlpgk,
							this.provurl, this.connfact, this.sendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,TipoOperazione.ADD, this.httpsurl, this.httpstipologia,
							this.httpshostverify, this.httpspath, this.httpstipo,
							this.httpspwd, this.httpsalgoritmo, this.httpsstato,
							this.httpskeystore, this.httpspwdprivatekeytrust,
							this.httpspathkey, this.httpstipokey,
							this.httpspwdkey, this.httpspwdprivatekey,
							this.httpsalgoritmokey, this.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, null, null,
							null, null, null, null, null, true,
							isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);

					pd.setDati(dati);


					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
							ForwardParams.ADD());

				}
			}



			List<Object> listaOggettiDaCreare = new ArrayList<Object>();
			listaOggettiDaCreare.add(asps);

			
			// Creo Porta Applicativa (opzione??)
			PortaApplicativa pa = null;
			if(apsCore.isGenerazioneAutomaticaPorteApplicative() && generaPACheckSoggetto){

				if(this.nomePA!=null && !"".equals(this.nomePA) && !"-".equals(this.nomePA)){

					pa = new PortaApplicativa();
					pa.setNome(this.nomePA);
					
					pa.setNomeSoggettoProprietario(this.nomeSoggettoErogatore);
					pa.setTipoSoggettoProprietario(this.tipoSoggettoErogatore);
					pa.setDescrizione("Servizio "+this.tiposervizio+this.nomeservizio+" erogato da "+this.tipoSoggettoErogatore+this.nomeSoggettoErogatore);
					pa.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO));
					pa.setScartaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO));
					// Devo lasciare a null !! pa.setGestioneManifest(CostantiConfigurazione.ABILITATO);
					pa.setRicevutaAsincronaSimmetrica(CostantiConfigurazione.ABILITATO);
					pa.setRicevutaAsincronaAsimmetrica(CostantiConfigurazione.ABILITATO);

					PortaApplicativaServizio pas = new PortaApplicativaServizio();
					pas.setTipo(this.tiposervizio);
					pas.setNome(this.nomeservizio);
					pa.setServizio(pas);

					pa.setStatoMessageSecurity(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO);
					pa.setIdSoggetto(idProv);

					if(this.nomeSA!=null && !"".equals(this.nomeSA) && !"-".equals(this.nomeSA)){
						ServizioApplicativo sa = new ServizioApplicativo();
						sa.setNome(this.nomeSA);
						pa.addServizioApplicativo(sa);
					}

					listaOggettiDaCreare.add(pa);

				}
			}


			apsCore.performCreateOperation(asps.getSuperUser(), apsHelper.smista(), listaOggettiDaCreare.toArray());

			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			//			PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();

			//			boolean [] permessi = new boolean[2];
			//			permessi[0] = pu.isServizi();
			//			permessi[1] = pu.isAccordiCooperazione();

			List<AccordoServizioParteSpecifica> listaAccordi = null;
			if(apsCore.isVisioneOggettiGlobale(userLogin)){
				listaAccordi = apsCore.soggettiServizioList(null, ricerca,permessi);
			}else{
				listaAccordi = apsCore.soggettiServizioList(userLogin, ricerca, permessi);
			}

			apsHelper.prepareServiziList(ricerca, listaAccordi);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
					ForwardParams.ADD());



		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
					ForwardParams.ADD());
		}  
	}

	public void decodeRequest(HttpServletRequest request,ConnettoriHelper connettoriHelper) throws Exception {
		try {
			
			String ct = request.getContentType();
			String boundary = null;
			ContentType contentType = new ContentType(ct);
			if(contentType.getParameterList()!=null){
				Enumeration<?> enNames = contentType.getParameterList().getNames();
				while (enNames.hasMoreElements()) {
					Object object = (Object) enNames.nextElement();
					if(object instanceof String){
						if("boundary".equals(object)){
							boundary = contentType.getParameterList().get((String)object);
						}
						//System.out.println("["+object+"]["+object.getClass().getName()+"]=["+contentType.getParameterList().get((String)object)+"]");
					}
				}
			}
			
			ServletInputStream in = request.getInputStream();
			byte [] post = Utilities.getAsByteArray(in);
			
			ByteArrayInputStream bin = new ByteArrayInputStream(post);
			this.parametersPOST = new Properties();
			BufferedReader dis = new BufferedReader(new InputStreamReader(bin));
			String key = dis.readLine();
			while (key != null) {
				
				if(boundary==null){
					// suppongo che il primo sia il boundary
					boundary = key;
					key = dis.readLine();
					continue;
				}
				
				if(key.endsWith(boundary)){
					key = dis.readLine();
					continue;
				}
				
				dis.readLine();
				String value = dis.readLine();
				if(key!=null && value!=null)
					this.parametersPOST.put(key, value);
				key = dis.readLine();
			}
			bin.close();
			
			bin = new ByteArrayInputStream(post);
			dis = new BufferedReader(new InputStreamReader(bin));
			String line = dis.readLine();
			while (line != null) {
				if (line.indexOf("\""+Costanti.DATA_ELEMENT_EDIT_MODE_NAME+"\"") != -1) {
					line = dis.readLine();
					this.editMode = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO+"\"") != -1) {
					line = dis.readLine();
					this.nomeservizio = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO+"\"") != -1) {
					line = dis.readLine();
					this.tiposervizio = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PRIVATO+"\"") != -1) {
					line = dis.readLine();
					this.privato = Costanti.CHECK_BOX_ENABLED.equals(dis.readLine().trim());
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_PROVIDER+"\"") != -1) {
					line = dis.readLine();
					this.provider = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO+"\"") != -1) {
					line = dis.readLine();
					this.accordo = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_SERVIZIO_CORRELATO+"\"") != -1) {
					line = dis.readLine();
					this.servcorr = dis.readLine();
				}
				/*
				 * if (line.indexOf("\"servpub\"") != -1) { line =
				 * dis.readLine(); this.servpub = dis.readLine(); }
				 */
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE+"\"") != -1) {
					line = dis.readLine();
					this.endpointtype = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_CHECK+"\"") != -1) {
					line = dis.readLine();
					this.endpointtype_check = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTPS+"\"") != -1) {
					line = dis.readLine();
					this.endpointtype_ssl = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO+"\"") != -1) {
					line = dis.readLine();
					this.tipoconn = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP+"\"") != -1) {
					line = dis.readLine();
					this.autenticazioneHttp = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG+"\"") != -1) {
					line = dis.readLine();
					this.connettoreDebug = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_URL+"\"") != -1) {
					line = dis.readLine();
					this.url = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA+"\"") != -1) {
					line = dis.readLine();
					this.nome = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA+"\"") != -1) {
					line = dis.readLine();
					this.tipo = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME+"\"") != -1) {
					line = dis.readLine();
					if(this.user==null)
						this.user = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD+"\"") != -1) {
					line = dis.readLine();
					if(this.password==null)
						this.password = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME+"\"") != -1) {
					line = dis.readLine();
					if(this.user==null)
						this.user = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD+"\"") != -1) {
					line = dis.readLine();
					if(this.password==null)
						this.password = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX+"\"") != -1) {
					line = dis.readLine();
					this.initcont = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG+"\"") != -1) {
					line = dis.readLine();
					this.urlpgk = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL+"\"") != -1) {
					line = dis.readLine();
					this.provurl = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY+"\"") != -1) {
					line = dis.readLine();
					this.connfact = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS+"\"") != -1) {
					line = dis.readLine();
					this.sendas = dis.readLine();
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
				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE+"\"") != -1) {
					int startId = line.indexOf(Costanti.MULTIPART_FILENAME);
					startId = startId + 10;
					// int endId = line.lastIndexOf("\"");
					// String tmpNomeFile = line.substring(startId, endId);
					line = dis.readLine();
					line = dis.readLine();
					this.wsdlimpler = "";
					while (!line.startsWith("-----") || (line.startsWith("-----") && ((line.indexOf(Costanti.MULTIPART_BEGIN) != -1) || 
							(line.indexOf(Costanti.MULTIPART_END) != -1)))) {
						if("".equals(this.wsdlimpler))
							this.wsdlimpler = line;
						else
							this.wsdlimpler = this.wsdlimpler + "\n" + line;
						line = dis.readLine();
					}
				}
				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE+"\"") != -1) {
					int startId = line.indexOf(Costanti.MULTIPART_FILENAME);
					startId = startId + 10;
					// int endId = line.lastIndexOf("\"");
					// String tmpNomeFile = line.substring(startId, endId);
					line = dis.readLine();
					line = dis.readLine();
					this.wsdlimplfru = "";
					while (!line.startsWith("-----") || (line.startsWith("-----") && ((line.indexOf(Costanti.MULTIPART_BEGIN) != -1) || 
							(line.indexOf(Costanti.MULTIPART_END) != -1)))) {
						if("".equals(this.wsdlimplfru))
							this.wsdlimplfru = line;
						else
							this.wsdlimplfru = this.wsdlimplfru + "\n" + line;
						line = dis.readLine();
					}
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_PROFILO+"\"") != -1) {
					line = dis.readLine();
					this.profilo = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE+"\"") != -1) {
					line = dis.readLine();
					this.portType = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_DESCRIZIONE+"\"") != -1) {
					line = dis.readLine();
					this.descrizione = dis.readLine();
				}if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_PROFILO+"\"") != -1) {
					line = dis.readLine();
					this.profilo = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE+"\"") != -1) {
					line = dis.readLine();
					this.statoPackage = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL+"\"") != -1) {
					line = dis.readLine();
					this.httpsurl = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE+"\"") != -1) {
					line = dis.readLine();
					this.httpstipologia = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY+"\"") != -1) {
					line = dis.readLine();
					this.httpshostverifyS = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION+"\"") != -1) {
					line = dis.readLine();
					this.httpspath = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE+"\"") != -1) {
					line = dis.readLine();
					this.httpstipo = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD+"\"") != -1) {
					line = dis.readLine();
					this.httpspwd = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM+"\"") != -1) {
					line = dis.readLine();
					this.httpsalgoritmo = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO+"\"") != -1) {
					line = dis.readLine();
					this.httpsstatoS = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE+"\"") != -1) {
					line = dis.readLine();
					this.httpskeystore = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE+"\"") != -1) {
					line = dis.readLine();
					this.httpspwdprivatekeytrust = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION+"\"") != -1) {
					line = dis.readLine();
					this.httpspathkey = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE+"\"") != -1) {
					line = dis.readLine();
					this.httpstipokey = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD+"\"") != -1) {
					line = dis.readLine();
					this.httpspwdkey = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE+"\"") != -1) {
					line = dis.readLine();
					this.httpspwdprivatekey = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM+"\"") != -1) {
					line = dis.readLine();
					this.httpsalgoritmokey = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_APS+"\"") != -1) {
					line = dis.readLine();
					this.nome_aps = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE+"\"") != -1) {
					line = dis.readLine();
					this.versione = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_PA+"\"") != -1) {
					line = dis.readLine();
					this.nomePA = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SA+"\"") != -1) {
					line = dis.readLine();
					this.nomeSA = dis.readLine();
				}
				line = dis.readLine();
			}

			this.endpointtype = connettoriHelper.readEndPointType(this.endpointtype,this.endpointtype_check, this.endpointtype_ssl);
			
			bin.close();
			in.close();
		} catch (IOException ioe) {
			throw new Exception(ioe);
		}
	}
}
