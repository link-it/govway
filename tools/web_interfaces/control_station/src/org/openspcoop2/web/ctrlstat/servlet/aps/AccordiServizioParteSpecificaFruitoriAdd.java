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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
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
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataServizioIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataSoggettoErogatoreIdentificazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDPortaDelegata;
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
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.dao.PoliticheSicurezza;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCostanti;
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

/**
 * serviziFruitoriAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaFruitoriAdd extends Action {

	private String connettoreDebug;
	private String    id, provider, endpointtype, endpointtype_check, endpointtype_ssl, tipoconn, url, nome, tipo, user,
	password, initcont, urlpgk, provurl, connfact, sendas, wsdlimpler,
	wsdlimplfru, profilo,clientAuth,
	httpsurl, httpstipologia, httpspath,
	httpstipo, httpspwd, httpsalgoritmo,
	httpskeystore, httpspwdprivatekeytrust, httpspathkey,
	httpstipokey, httpspwdkey, httpspwdprivatekey,
	httpsalgoritmokey;
	private String httpshostverifyS, httpsstatoS;
	private boolean httpshostverify, httpsstato;
	private String statoPackage;
	private boolean validazioneDocumenti = true;
	private boolean decodeRequestValidazioneDocumenti = false;
	private String editMode = null;
	private String correlato = null;
	private String autenticazioneHttp;
	private String servizioApplicativo;
	private Properties parametersPOST;

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();

		String superUser =  ServletUtils.getUserLoginFromSession(session);

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			ConnettoriHelper connettoriHelper = new ConnettoriHelper(request, pd, session);

			this.parametersPOST = null;
			
			this.editMode = null;

			this.id = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			this.provider = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROVIDER);
			this.servizioApplicativo = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SA);
			this.correlato = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO);
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
			
			this.clientAuth = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_CLIENT_AUTH);
			this.statoPackage = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);
			

			String ct = request.getContentType();
			if ((ct != null) && (ct.indexOf(Costanti.MULTIPART) != -1)) {
				// decodeReq = true;
				this.decodeRequestValidazioneDocumenti = false; // init
				this.decodeRequest(request,connettoriHelper);
			}

		
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			PddCore pddCore = new PddCore(apsCore);
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);

			String autenticazionePortaDelegataAutomatica = apsCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
			String autorizzazionePortaDelegataAutomatica = apsCore.getAutorizzazione_generazioneAutomaticaPorteDelegate();
			
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

			int idInt = Integer.parseInt(this.id);

			Boolean isConnettoreCustomUltimaImmagineSalvata = null;
			
			Connettore conTmp = null;
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.FRUIZIONE_ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, apsCore, 
							request, session, this.parametersPOST, (this.endpointtype==null), this.endpointtype); // uso endpointtype per capire se è la prima volta che entro
			
			// prendo l'id del soggetto erogatore lo propago
			// lo metto nel pd come campo hidden
			PageData oldPD = ServletUtils.getPageDataFromSession(session);
			pd.setHidden(oldPD.getHidden());

			String idSoggErogatore = oldPD.getHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);

			// Preparo il menu
			apsHelper.makeMenu();

			// Prendo nome e tipo dal db

			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(new Long(idInt));
			Servizio servizio = asps.getServizio();
			String nomeservizio = servizio.getNome();
			String tiposervizio = servizio.getTipo();

			if(this.correlato == null){
				this.correlato = ((TipologiaServizio.CORRELATO.equals(servizio.getTipologiaServizio()) ?
						AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_CORRELATO :
							AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_NORMALE));
			}

			//String profiloSoggettoFruitore = null;
			//if ((this.provider != null) && !this.provider.equals("")) {
			//	long idFruitore = Long.parseLong(this.provider);
			//	Soggetto soggetto = soggettiCore.getSoggettoRegistro(idFruitore);
			//	profiloSoggettoFruitore = soggetto.getVersioneProtocollo();
			//}
			//String profiloValue = profiloSoggettoFruitore;
			//if(this.profilo!=null && !"".equals(this.profilo) && !"-".equals(this.profilo)){
			//	profiloValue = this.profilo;
			//}

			String protocollo = apsCore.getProtocolloAssociatoTipoServizio(tiposervizio);
			List<String> versioniProtocollo = apsCore.getVersioniProtocollo(protocollo);
			List<String> tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);

			// Prendo il nome e il tipo del soggetto erogatore del servizio
			Soggetto soggErogatore = soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggErogatore));
			String tipoSoggettoErogatore = soggErogatore.getTipo();
			String nomesoggettoErogatore = soggErogatore.getNome();

			String tmpTitle = tiposervizio + "/" + nomeservizio + " erogato da " + tipoSoggettoErogatore + "/" + nomesoggettoErogatore;
			// aggiorno tmpTitle
			String tmpVersione = asps.getVersione();
			if(apsCore.isShowVersioneAccordoServizioParteSpecifica()==false){
				tmpVersione = null;
			}
			tmpTitle = idAccordoFactory.getUriFromValues(asps.getNome(), 
					servizio.getTipoSoggettoErogatore(), servizio.getNomeSoggettoErogatore(), 
					tmpVersione);

			// Soggetti fruitori
			// tutti i soggetti anche il soggetto attuale
			// tranne quelli già registrati come fruitori
			String[] soggettiList = null;
			String[] soggettiListLabel = null;
			List<Fruitore> fruList = apsCore.getSoggettiWithServizioNotFruitori(idInt);
			List<String> soggettiListVector = new ArrayList<String>();
			List<String> soggettiListLabelVector = new ArrayList<String>();
			IDSoggetto idSoggettoSelected = null;
			for (int i = 0; i < fruList.size(); i++) {
				Fruitore fru = fruList.get(i);
				if(tipiSoggettiCompatibiliAccordo.contains(fru.getTipo())){
					soggettiListVector.add("" + fru.getId());
					soggettiListLabelVector.add(fru.getTipo() + "/" + fru.getNome());
					if(this.provider!=null && !"".equals(this.provider)){
						long idProvider = Long.parseLong(this.provider);
						if(fru.getId()==idProvider){
							idSoggettoSelected = new IDSoggetto(fru.getTipo(), fru.getNome());
						}
					}
				}
			}
			if(soggettiListVector.size()>0){
				soggettiList = soggettiListVector.toArray(new String[1]);
				soggettiListLabel = soggettiListLabelVector.toArray(new String[1]);
				
				if(idSoggettoSelected==null){
					// prendo il primo soggetto se esiste
					if(soggettiListLabel!=null && soggettiListLabel.length>0){
						String [] tmp = soggettiListLabel[0].split("/");
						idSoggettoSelected = new IDSoggetto(tmp[0], tmp[1]);
					}
				}
			}
				

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

			
			// ServiziApplicativi
			List<String> saList = new ArrayList<String>();
			saList.add("-");
			if(apsCore.isGenerazioneAutomaticaPorteDelegate() && idSoggettoSelected!=null){
				try{					
					List<ServizioApplicativo> oldSilList = saCore.soggettiServizioApplicativoList(idSoggettoSelected,superUser,
							CredenzialeTipo.toEnumConstant(autenticazionePortaDelegataAutomatica));
					if(oldSilList!=null && oldSilList.size()>0){
						for (int i = 0; i < oldSilList.size(); i++) {
							saList.add(oldSilList.get(i).getNome());		
						}
					}
				}catch(DriverConfigurazioneNotFound dNotFound){}

			}
			

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if(ServletUtils.isEditModeInProgress(this.editMode) && ServletUtils.isEditModeInProgress(request)){
				// setto la barra del titolo
				List<Parameter> lstParm = new ArrayList<Parameter>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + tmpTitle, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ this.id),
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ idSoggErogatore)
						));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				if(soggettiListVector.size()<=0){
					
					pd.setMessage(AccordiServizioParteSpecificaCostanti.LABEL_AGGIUNTA_FRUITORI_COMPLETATA);
					
					pd.disableEditMode();
					
				}
				
				else{
					dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.ADD, this.id, null, null, dati);
	
					if (this.provider == null) {
						this.provider = "";
						this.servizioApplicativo = "-";
						this.wsdlimpler = "";
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
						this.clientAuth= ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
						this.httpsurl = "";
						this.httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_SSLV3_TYPE;
						this.httpshostverify = true;
						this.httpspath = "";
						this.httpstipo = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
						this.httpspwd = "";
						this.httpsalgoritmo = "";
						this.httpsstato = false;
						this.httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
						this.httpspwdprivatekeytrust = "";
						this.httpspathkey = "";
						this.httpstipokey = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
						this.httpspwdkey = "";
						this.httpspwdprivatekey = "";
						this.httpsalgoritmokey = "";
	
						if(apsCore.isShowGestioneWorkflowStatoDocumenti()){
							if(this.statoPackage==null || "".equals(this.statoPackage)){
								//if(serviziFruitoriAdd.generazioneAutomaticaPorteDelegate){
								this.statoPackage=StatiAccordo.bozza.toString();
								/*}else{
									this.statoPackage=servizio.getStatoPackage();
								}*/
							}
							
							//Se l'ASPS riferito e' in stato operativo o finale allora setto la fruizione come operativa.
							if(asps.getStatoPackage().equals(StatiAccordo.operativo.toString()) || asps.getStatoPackage().equals(StatiAccordo.finale.toString())){
								this.statoPackage=StatiAccordo.operativo.toString();
							}
							
						}else{
							this.statoPackage=StatiAccordo.finale.toString();
						}
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
	
					this.autenticazioneHttp = connettoriHelper.getAutenticazioneHttp(this.autenticazioneHttp, this.endpointtype, this.user);
					
					dati = apsHelper.addServiziFruitoriToDati(dati, this.provider, this.wsdlimpler, this.wsdlimplfru, soggettiList,
							soggettiListLabel, "0", this.id, TipoOperazione.ADD, "", "", "", nomeservizio, tiposervizio, this.correlato, this.statoPackage, this.statoPackage,asps.getStatoPackage(), null,this.validazioneDocumenti,
							this.servizioApplicativo,saList);
	
					dati = apsHelper.addFruitoreToDati(TipoOperazione.ADD, versioniLabel, versioniValues, this.profilo, this.clientAuth, dati,null
							,null,null,null,null,null,null);
	
					String tipoSendas = ConnettoriCostanti.TIPO_SEND_AS[0];
					String tipoJms = ConnettoriCostanti.TIPI_CODE_JMS[0];
					if (!InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
						dati = connettoriHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, null, 
								this.url, this.nome,
								tipoJms, this.user,
								this.password, this.initcont, this.urlpgk,
								this.provurl, this.connfact, tipoSendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,TipoOperazione.ADD, this.httpsurl, this.httpstipologia,
								this.httpshostverify, this.httpspath, this.httpstipo, this.httpspwd,
								this.httpsalgoritmo, this.httpsstato, this.httpskeystore,
								this.httpspwdprivatekeytrust, this.httpspathkey,
								this.httpstipokey, this.httpspwdkey, this.httpspwdprivatekey,
								this.httpsalgoritmokey, this.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD, null,
								null, null, null, null, null, null, true,
								isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);
					}else{
						//spostato dentro l'helper
					}
				}


				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.serviziFruitoriCheckData(TipoOperazione.ADD, soggettiList,
					this.id, "", "", "", "", this.provider,
					this.endpointtype, this.url, this.nome, this.tipo,
					this.user, this.password, this.initcont, this.urlpgk,
					this.provurl, this.connfact, this.sendas,
					this.wsdlimpler, this.wsdlimplfru, "0", this.profilo,
					this.httpsurl, this.httpstipologia,
					this.httpshostverify, this.httpspath, this.httpstipo,
					this.httpspwd, this.httpsalgoritmo, this.httpsstato,
					this.httpskeystore, this.httpspwdprivatekeytrust,
					this.httpspathkey, this.httpstipokey,
					this.httpspwdkey, this.httpspwdprivatekey,
					this.httpsalgoritmokey, this.tipoconn,this.clientAuth,this.validazioneDocumenti,null,this.autenticazioneHttp,
					listExtendedConnettore);
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParm = new ArrayList<Parameter>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + tmpTitle, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ this.id)
				//				,
				//						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ idSoggErogatore)
						));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.ADD, this.id, null, null, dati);

				dati = apsHelper.addServiziFruitoriToDati(dati, this.provider, this.wsdlimpler, this.wsdlimplfru, soggettiList, soggettiListLabel, "0", this.id, TipoOperazione.ADD,
						"", "", "", nomeservizio, tiposervizio, this.correlato, this.statoPackage, this.statoPackage,asps.getStatoPackage(),null,this.validazioneDocumenti,
						this.servizioApplicativo,saList);

				dati = apsHelper.addFruitoreToDati(TipoOperazione.ADD, versioniLabel, versioniValues, this.profilo, this.clientAuth,
						dati,null
						,null,null,null,null,null,null);

				if (!InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
					dati = connettoriHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, null,
							this.url, this.nome, this.tipo, this.user,
							this.password, this.initcont, this.urlpgk,
							this.provurl, this.connfact, this.sendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,TipoOperazione.ADD, this.httpsurl, this.httpstipologia,
							this.httpshostverify, this.httpspath, this.httpstipo,
							this.httpspwd, this.httpsalgoritmo, this.httpsstato,
							this.httpskeystore, this.httpspwdprivatekeytrust,
							this.httpspathkey, this.httpstipokey,
							this.httpspwdkey, this.httpspwdprivatekey,
							this.httpsalgoritmokey, this.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD, null,
							null, null, null, null, null, null, true,
							isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);
				}else{
					//spostato dentro l'helper
				}

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
						ForwardParams.ADD());
			}

			// Inserisco il fruitore nel db
			int idProv = Integer.parseInt(this.provider);

			// prendo il nome e il tipo del soggetto proprietario della
			// porta delegata
			// che sarebbe il soggetto fruitore di questo servizio
			// Soggetto Fruitore
			Soggetto sogFru = soggettiCore.getSoggettoRegistro(idProv);
			String nomeFruitore = sogFru.getNome();
			String tipoFruitore = sogFru.getTipo();
			String pdd = sogFru.getPortaDominio();

			// soggetto erogatore servizio
			Soggetto sogEr = soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggErogatore));
			String nomeErogatore = sogEr.getNome();
			String tipoErogatore = sogEr.getTipo();

			Connettore connettore = new Connettore();
			connettore.setNome("CNT_SF_" + tipoFruitore + "/" + nomeFruitore + "_" + tipoErogatore + "/" + nomeErogatore + "_" + tiposervizio + "/" + nomeservizio);

			connettoriHelper.fillConnettore(connettore, this.connettoreDebug, this.endpointtype, this.endpointtype, this.tipoconn, this.url,
					this.nome, this.tipo, this.user, this.password,
					this.initcont, this.urlpgk, this.provurl, this.connfact,
					this.sendas, this.httpsurl, this.httpstipologia,
					this.httpshostverify, this.httpspath, this.httpstipo,
					this.httpspwd, this.httpsalgoritmo, this.httpsstato,
					this.httpskeystore, this.httpspwdprivatekeytrust,
					this.httpspathkey, this.httpstipokey,
					this.httpspwdkey, this.httpspwdprivatekey,
					this.httpsalgoritmokey,
					listExtendedConnettore);

			Fruitore fruitore = new Fruitore();
			fruitore.setId(new Long(idProv));
			fruitore.setTipo(tipoFruitore);
			fruitore.setNome(nomeFruitore);
			fruitore.setConnettore(connettore);
			if ("-".equals(this.profilo) == false)
				fruitore.setVersioneProtocollo(this.profilo);
			else
				fruitore.setVersioneProtocollo(null);
			fruitore.setByteWsdlImplementativoErogatore(this.wsdlimpler != null && !this.wsdlimpler.trim().replaceAll("\n", "").equals("") ? this.wsdlimpler.trim().getBytes() : null);
			fruitore.setByteWsdlImplementativoFruitore(this.wsdlimplfru != null && !this.wsdlimplfru.trim().replaceAll("\n", "").equals("") ? this.wsdlimplfru.trim().getBytes() : null);
			if(ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT.equals(this.clientAuth))
				fruitore.setClientAuth(null);
			else
				fruitore.setClientAuth(StatoFunzionalita.toEnumConstant(this.clientAuth));
			AccordoServizioParteSpecifica servsp = apsCore.getAccordoServizioParteSpecifica(idInt);

			// stato
			fruitore.setStatoPackage(this.statoPackage);

//			Spostato sopra a livello di edit in progress			
//			//Se l'ASPS riferito e' in stato operativo o finale allora setto la fruizione come operativa.
//			if(asps.getStatoPackage().equals(StatiAccordo.operativo.toString()) || asps.getStatoPackage().equals(StatiAccordo.finale.toString())){
//				fruitore.setStatoPackage(StatiAccordo.operativo.toString());
//			}

			// Check stato
			if(apsCore.isShowGestioneWorkflowStatoDocumenti()){

				try{
					apsCore.validaStatoFruitoreAccordoServizioParteSpecifica(fruitore, servsp);
				}catch(ValidazioneStatoPackageException validazioneException){
					
					// Setto messaggio di errore
					pd.setMessage(validazioneException.toString());
					
					// setto la barra del titolo
					List<Parameter> lstParm = new ArrayList<Parameter>();

					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
					lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + tmpTitle, 
							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
							new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ this.id)
					//					,
					//							new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ idSoggErogatore)
							));
					lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, lstParm );
					
					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());

					dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.ADD, this.id, null, null, dati);

					dati = apsHelper.addServiziFruitoriToDati(dati, this.provider, this.wsdlimpler, this.wsdlimplfru, 
							soggettiList, soggettiListLabel, "0", this.id, TipoOperazione.ADD, "", "", "", nomeservizio, tiposervizio, this.correlato, 
							this.statoPackage, this.statoPackage,asps.getStatoPackage(),null,this.validazioneDocumenti,
							this.servizioApplicativo,saList);

					dati = apsHelper.addFruitoreToDati(TipoOperazione.ADD, versioniLabel, versioniValues, this.profilo, this.clientAuth, dati,null
							,null,null,null,null,null,null);

					if (!InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
						dati = connettoriHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, null,
								this.url, this.nome, this.tipo, this.user,
								this.password, this.initcont, this.urlpgk,
								this.provurl, this.connfact, this.sendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,TipoOperazione.ADD, this.httpsurl, this.httpstipologia,
								this.httpshostverify, this.httpspath, this.httpstipo,
								this.httpspwd, this.httpsalgoritmo, this.httpsstato,
								this.httpskeystore, this.httpspwdprivatekeytrust,
								this.httpspathkey, this.httpstipokey,
								this.httpspwdkey, this.httpspwdprivatekey,
								this.httpsalgoritmokey, this.tipoconn, 
								AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD, null,
								null, null, null, null, null, null, true,
								isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);
					}else{
						//spostato dentro l'helper
					}

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
							ForwardParams.ADD());
				}
			}


			servsp.addFruitore(fruitore);
			apsCore.performUpdateOperation(superUser, apsHelper.smista(), servsp);

			// Prendo i dati del soggetto erogatore del servizio
			String mynomeprov = asps.getServizio().getNomeSoggettoErogatore();
			String mytipoprov = asps.getServizio().getTipoSoggettoErogatore();

			// creo la porta delegata in automatico uso i dati nella
			// sessione
			// String tipoSoggettoErogatore = (String)
			// session.getAttribute("tipoSoggettoErogatore");
			// String nomeSoggettoErogatore = (String)
			// session.getAttribute("nomeSoggettoErogatore");

			boolean generazionePortaDelegata = apsCore.isGenerazioneAutomaticaPorteDelegate();
			/*
			 * bug-fix #61 Se il soggetto (fruitore) afferisce a una porta di
			 * dominio di tipo 'esterno', la porta delegata non deve essere
			 * creata.
			 */
			try{
				PdDControlStation pddFruitore = pddCore.getPdDControlStation(pdd);
				String tipoPddFruitore = pddFruitore != null ? pddFruitore.getTipo() : "";
				String pddOp = PddCostanti.LABEL_TIPI_SOLO_OPERATIVI[0];
				String pddNonOp = PddCostanti.LABEL_TIPI_SOLO_OPERATIVI[1];
				generazionePortaDelegata = (pddOp.equals(tipoPddFruitore) || pddNonOp.equals(tipoPddFruitore));
			}catch(DriverControlStationNotFound dNT){
				// In singlePdD la porta di dominio e' opzionale.
				if(apsCore.isSinglePdD()==false){
					throw dNT;
				}
			}
			if (generazionePortaDelegata) {
				String nomePD = tipoFruitore + nomeFruitore + "/" + mytipoprov + mynomeprov + "/" + tiposervizio + nomeservizio;
				String descr = "Invocazione servizio " + tiposervizio + nomeservizio + " erogato da " + mytipoprov + mynomeprov;
				String urlinv = tipoFruitore + nomeFruitore + "/" + mytipoprov + mynomeprov + "/" + tiposervizio + nomeservizio;
				if(porteDelegateCore.isShowPortaDelegataUrlInvocazione()==false){
					urlinv = nomePD;
				}
				PortaDelegataSoggettoErogatoreIdentificazione modesp = PortaDelegataSoggettoErogatoreIdentificazione.STATIC;

				PortaDelegata portaDelegata = new PortaDelegata();
				portaDelegata.setNome(nomePD);
				portaDelegata.setDescrizione(descr);
				portaDelegata.setLocation(urlinv);
				portaDelegata.setAutenticazione(autenticazionePortaDelegataAutomatica);
				portaDelegata.setAutorizzazione(autorizzazionePortaDelegataAutomatica);

				PortaDelegataSoggettoErogatore pdSogg = new PortaDelegataSoggettoErogatore();
				pdSogg.setNome(mynomeprov);
				pdSogg.setTipo(mytipoprov);
				pdSogg.setIdentificazione(modesp);
				// se l'identificazioe e' urlbased/contentbased ho il pattern
				// nella
				// variabile utilizzata per il nome
				pdSogg.setPattern(mynomeprov);
				portaDelegata.setSoggettoErogatore(pdSogg);

				PortaDelegataServizio pdServizio = new PortaDelegataServizio();
				pdServizio.setNome(nomeservizio);
				pdServizio.setTipo(tiposervizio);
				pdServizio.setIdentificazione(PortaDelegataServizioIdentificazione.STATIC);
				pdServizio.setPattern(nomeservizio);
				portaDelegata.setServizio(pdServizio);

				PortaDelegataAzione pdAzione = new PortaDelegataAzione();
				pdAzione.setIdentificazione(PortaDelegataAzioneIdentificazione.URL_BASED);
				String pattern = ".*" + urlinv + "/([^/|^?]*).*";
				pdAzione.setPattern(pattern);
				
				if(apsCore.isForceWsdlBasedAzione_generazioneAutomaticaPorteDelegate()){
					pdAzione.setForceWsdlBased(org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO);
				} else {
					pdAzione.setForceWsdlBased(org.openspcoop2.core.config.constants.StatoFunzionalita.DISABILITATO);
				}
				
				portaDelegata.setAzione(pdAzione);

				// soggetto proprietario
				SoggettoCtrlStat soggetto = soggettiCore.getSoggettoCtrlStat(idProv);
				portaDelegata.setIdSoggetto(soggetto.getId());
				portaDelegata.setTipoSoggettoProprietario(soggetto.getTipo());
				portaDelegata.setNomeSoggettoProprietario(soggetto.getNome());

				// servizioApplicativo
				if(this.servizioApplicativo!=null && !"".equals(this.servizioApplicativo) && !"-".equals(this.servizioApplicativo)){
					ServizioApplicativo sa = new ServizioApplicativo();
					sa.setNome(this.servizioApplicativo);
					portaDelegata.addServizioApplicativo(sa);
					
					PoliticheSicurezza polSic = new PoliticheSicurezza();
					polSic.setNomeServizioApplicativo(this.servizioApplicativo);
					polSic.setIdServizio(idInt);
					polSic.setIdFruitore(idProv);
					apsCore.performCreateOperation(superUser, apsHelper.smista(), polSic);
				}
				
				// Verifico prima che la porta delegata non esista già
				IDPortaDelegata myidpd = new IDPortaDelegata();
				IDSoggetto ids = new IDSoggetto(tipoFruitore, nomeFruitore);
				myidpd.setSoggettoFruitore(ids);
				myidpd.setLocationPD(nomePD);
				if (!porteDelegateCore.existsPortaDelegata(myidpd)){
					porteDelegateCore.performCreateOperation(superUser, apsHelper.smista(), portaDelegata);
				}
			}

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.SERVIZI_FRUITORI;

			ricerca = apsHelper.checkSearchParameters(idLista, ricerca);

			List<Fruitore> lista = apsCore.serviziFruitoriList(Integer.parseInt(this.id), ricerca);

			apsHelper.prepareServiziFruitoriList(lista, this.id, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished( mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
					ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
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
				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID+"\"") != -1) {
					line = dis.readLine();
					this.id = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_PROVIDER+"\"") != -1) {
					line = dis.readLine();
					this.provider = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SA+"\"") != -1) {
					line = dis.readLine();
					this.servizioApplicativo = dis.readLine();
				}
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
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO+"\"") != -1) {
					line = dis.readLine();
					this.correlato = dis.readLine();
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
					while (!line.startsWith("-----") || (line.startsWith("-----") && ((line.indexOf(Costanti.MULTIPART_BEGIN) != -1) || (line.indexOf(Costanti.MULTIPART_END) != -1)))) {
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
					while (!line.startsWith("-----") || (line.startsWith("-----") && ((line.indexOf(Costanti.MULTIPART_BEGIN) != -1) || (line.indexOf(Costanti.MULTIPART_END) != -1)))) {
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
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_CLIENT_AUTH+"\"") != -1) {
					line = dis.readLine();
					this.clientAuth = dis.readLine();
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
