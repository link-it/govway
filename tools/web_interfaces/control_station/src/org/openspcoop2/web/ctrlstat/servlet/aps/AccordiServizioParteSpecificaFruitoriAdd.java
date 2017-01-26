/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
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
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesUtilities;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
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
	password, initcont, urlpgk, provurl, connfact, sendas, profilo,clientAuth,
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

	private String proxy_enabled, proxy_hostname,proxy_port,proxy_username,proxy_password;

	private String transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop, opzioniAvanzate;
	
	// file
	private String requestOutputFileName = null;
	private String requestOutputFileNameHeaders = null;
	private String requestOutputParentDirCreateIfNotExists = null;
	private String requestOutputOverwriteIfExists = null;
	private String responseInputMode = null;
	private String responseInputFileName = null;
	private String responseInputFileNameHeaders = null;
	private String responseInputDeleteAfterRead = null;
	private String responseInputWaitTime = null;
	

	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory= null;
	private IRegistryReader registryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	private ConsoleInterfaceType consoleInterfaceType = null;

	private BinaryParameter wsdlimpler, wsdlimplfru;


	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String superUser =  ServletUtils.getUserLoginFromSession(session);

		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		this.consoleOperationType = ConsoleOperationType.ADD;
		this.consoleInterfaceType = ProtocolPropertiesUtilities.getTipoInterfaccia(session); 

		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.ADD;

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);

			this.parametersPOST = null;

			this.editMode = apsHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);

			this.id = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			this.provider = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROVIDER);
			this.servizioApplicativo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SA);
			this.correlato = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO);
			//			this.endpointtype = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE );

			this.endpointtype = apsHelper.readEndPointType();
			this.tipoconn = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO );
			this.autenticazioneHttp = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);

			this.connettoreDebug = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);

			// proxy
			this.proxy_enabled = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			this.proxy_hostname = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			this.proxy_port = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			this.proxy_username = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			this.proxy_password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);

			// opzioni avanzate
			this.transfer_mode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			this.transfer_mode_chunk_size = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			this.redirect_mode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			this.redirect_max_hop = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			this.opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(request, this.transfer_mode, this.redirect_mode);

			// http
			this.url = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL  );
			if(TipiConnettore.HTTP.toString().equals(this.endpointtype)){
				this.user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME);
				this.password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD);
			}

			// jms
			this.nome = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			this.tipo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			this.initcont = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			this.urlpgk = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			this.provurl = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			this.connfact = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			this.sendas = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(this.endpointtype)){
				this.user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				this.password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}

			// https
			this.httpsurl = this.url;
			this.httpstipologia = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE );
			this.httpshostverifyS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			this.httpspath = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION );
			this.httpstipo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			this.httpspwd = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			this.httpsalgoritmo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			this.httpsstatoS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			this.httpskeystore = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			this.httpspwdprivatekeytrust = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			this.httpspathkey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			this.httpstipokey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			this.httpspwdkey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			this.httpspwdprivatekey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			this.httpsalgoritmokey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			if(TipiConnettore.HTTPS.toString().equals(this.endpointtype)){
				this.user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME);
				this.password = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD);
			}
			
			// file
			this.requestOutputFileName = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			this.requestOutputFileNameHeaders = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			this.requestOutputParentDirCreateIfNotExists = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			this.requestOutputOverwriteIfExists = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			this.responseInputMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			this.responseInputFileName = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			this.responseInputFileNameHeaders = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			this.responseInputDeleteAfterRead = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			this.responseInputWaitTime = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);


			this.profilo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROFILO);

			this.clientAuth = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_CLIENT_AUTH);
			this.statoPackage = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);

			this.wsdlimpler = apsHelper.getBinaryParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
			this.wsdlimplfru = apsHelper.getBinaryParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE);

			if(apsHelper.isMultipart()){
				this.decodeRequestValidazioneDocumenti = true;
			}
			//			
			//			String ct = request.getContentType();
			//			if ((ct != null) && (ct.indexOf(Costanti.MULTIPART) != -1)) {
			//				// decodeReq = true;
			//				this.decodeRequestValidazioneDocumenti = false; // init
			//				this.decodeRequest(request,apsHelper);
			//			}


			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			PddCore pddCore = new PddCore(apsCore);
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);

			String autenticazionePortaDelegataAutomatica = apsCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
			String autorizzazionePortaDelegataAutomatica = apsCore.getAutorizzazione_generazioneAutomaticaPorteDelegate();

			if(ServletUtils.isEditModeInProgress(this.editMode)){
				// primo accesso alla servlet
				this.validazioneDocumenti = true;
				if (!InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
					String tmpValidazioneDocumenti = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
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
					String tmpValidazioneDocumenti = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
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
			String nomeservizio = asps.getNome();
			String tiposervizio = asps.getTipo();
			Integer versioneservizio = asps.getVersione();

			if(this.correlato == null){
				this.correlato = ((TipologiaServizio.CORRELATO.equals(asps.getTipologiaServizio()) ?
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
			//			Soggetto soggErogatore = soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggErogatore));
			//			String tipoSoggettoErogatore = soggErogatore.getTipo();
			//			String nomesoggettoErogatore = soggErogatore.getNome();

			String tmpTitle = IDServizioFactory.getInstance().getUriFromAccordo(asps);

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

			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = soggettiCore.getRegistryReader(this.protocolFactory); 

			// ID Accordo Null per default
			IDFruizione idFruizione = null;
			this.consoleConfiguration = this.consoleDynamicConfiguration.getDynamicConfigFruizioneAccordoServizioParteSpecifica(this.consoleOperationType, this.consoleInterfaceType, this.registryReader, idFruizione  );
			this.protocolProperties = apsHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);


			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if(ServletUtils.isEditModeInProgress(this.editMode)){
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
					dati = apsHelper.addHiddenFieldsToDati(tipoOp, this.id, null, null, dati);

					if (this.provider == null) {
						this.provider = "";
						this.servizioApplicativo = "-";
						if(this.wsdlimpler.getValue() == null)
							this.wsdlimpler.setValue(new byte[1]);
						if(this.wsdlimplfru.getValue() == null)
							this.wsdlimplfru.setValue(new byte[1]); 
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
						this.httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
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
						this.httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
					}
					if(this.httpshostverifyS==null || "".equals(this.httpshostverifyS)){
						this.httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
						this.httpshostverify = true;
					}

					this.autenticazioneHttp = apsHelper.getAutenticazioneHttp(this.autenticazioneHttp, this.endpointtype, this.user);

					// update della configurazione 
					this.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, this.registryReader, idFruizione);


					dati = apsHelper.addServiziFruitoriToDati(dati, this.provider, this.wsdlimpler, this.wsdlimplfru, soggettiList,
							soggettiListLabel, "0", this.id, tipoOp, "", "", "", nomeservizio, tiposervizio, versioneservizio, this.correlato, this.statoPackage, this.statoPackage,asps.getStatoPackage(), null,this.validazioneDocumenti,
							this.servizioApplicativo,saList);

					dati = apsHelper.addFruitoreToDati(tipoOp, versioniLabel, versioniValues, this.profilo, this.clientAuth, dati,null
							,null,null,null,null,null,null,null);

					String tipoSendas = ConnettoriCostanti.TIPO_SEND_AS[0];
					String tipoJms = ConnettoriCostanti.TIPI_CODE_JMS[0];
					if (!InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
						dati = apsHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, null, 
								this.url, this.nome,
								tipoJms, this.user,
								this.password, this.initcont, this.urlpgk,
								this.provurl, this.connfact, tipoSendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,tipoOp, this.httpsurl, this.httpstipologia,
								this.httpshostverify, this.httpspath, this.httpstipo, this.httpspwd,
								this.httpsalgoritmo, this.httpsstato, this.httpskeystore,
								this.httpspwdprivatekeytrust, this.httpspathkey,
								this.httpstipokey, this.httpspwdkey, this.httpspwdprivatekey,
								this.httpsalgoritmokey, this.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD, null,
								null, null, null, null, null, null, null, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
								this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
								this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
								this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
								listExtendedConnettore);
					}else{
						//spostato dentro l'helper
					}
				}

				// aggiunta campi custom
				dati = apsHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.serviziFruitoriCheckData(tipoOp, soggettiList,
					this.id, "", "", null, "", "", this.provider,
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
					this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
					this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
					this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
					this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
					listExtendedConnettore);

			// Validazione base dei parametri custom 
			if(isOk){
				try{
					apsHelper.validaProtocolProperties(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties);
				}catch(ProtocolException e){
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			// Valido i parametri custom se ho gia' passato tutta la validazione prevista
			if(isOk){
				try{
					idFruizione = new IDFruizione();
					idFruizione.setIdServizio(apsHelper.getIDServizioFromValues(tiposervizio, nomeservizio, asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore(), versioneservizio+""));
					idFruizione.setIdFruitore(idSoggettoSelected); 
					//validazione campi dinamici
					this.consoleDynamicConfiguration.validateDynamicConfigFruizioneAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, this.protocolProperties, this.registryReader, idFruizione);
				}catch(ProtocolException e){
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}


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

				// update della configurazione 
				this.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, this.registryReader, idFruizione);

				dati = apsHelper.addHiddenFieldsToDati(tipoOp, this.id, null, null, dati);

				dati = apsHelper.addServiziFruitoriToDati(dati, this.provider, this.wsdlimpler, this.wsdlimplfru, soggettiList, soggettiListLabel, "0", this.id, tipoOp,
						"", "", "", nomeservizio, tiposervizio, versioneservizio, this.correlato, this.statoPackage, this.statoPackage,asps.getStatoPackage(),null,this.validazioneDocumenti,
						this.servizioApplicativo,saList);

				dati = apsHelper.addFruitoreToDati(tipoOp, versioniLabel, versioniValues, this.profilo, this.clientAuth,
						dati,null
						,null,null,null,null,null,null,null);

				if (!InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
					dati = apsHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, null,
							this.url, this.nome, this.tipo, this.user,
							this.password, this.initcont, this.urlpgk,
							this.provurl, this.connfact, this.sendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,tipoOp, this.httpsurl, this.httpstipologia,
							this.httpshostverify, this.httpspath, this.httpstipo,
							this.httpspwd, this.httpsalgoritmo, this.httpsstato,
							this.httpskeystore, this.httpspwdprivatekeytrust,
							this.httpspathkey, this.httpstipokey,
							this.httpspwdkey, this.httpspwdprivatekey,
							this.httpsalgoritmokey, this.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD, null,
							null, null, null, null, null, null, null, true,
							isConnettoreCustomUltimaImmagineSalvata, 
							this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
							this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
							this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
							this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
							listExtendedConnettore);
				}else{
					//spostato dentro l'helper
				}

				// aggiunta campi custom
				dati = apsHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties);

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

			apsHelper.fillConnettore(connettore, this.connettoreDebug, this.endpointtype, this.endpointtype, this.tipoconn, this.url,
					this.nome, this.tipo, this.user, this.password,
					this.initcont, this.urlpgk, this.provurl, this.connfact,
					this.sendas, this.httpsurl, this.httpstipologia,
					this.httpshostverify, this.httpspath, this.httpstipo,
					this.httpspwd, this.httpsalgoritmo, this.httpsstato,
					this.httpskeystore, this.httpspwdprivatekeytrust,
					this.httpspathkey, this.httpstipokey,
					this.httpspwdkey, this.httpspwdprivatekey,
					this.httpsalgoritmokey,
					this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
					this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
					this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
					this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
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

			String wsdlimplerS = this.wsdlimpler.getValue() != null ? new String(this.wsdlimpler.getValue()) : null; 
			fruitore.setByteWsdlImplementativoErogatore(((wsdlimplerS != null) && !wsdlimplerS.trim().replaceAll("\n", "").equals("")) ? wsdlimplerS.trim().getBytes() : null);
			String wsdlimplfruS = this.wsdlimplfru.getValue() != null ? new String(this.wsdlimplfru.getValue()) : null; 
			fruitore.setByteWsdlImplementativoFruitore(((wsdlimplfruS != null) && !wsdlimplfruS.trim().replaceAll("\n", "").equals("")) ? wsdlimplfruS.trim().getBytes() : null);

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

					// update della configurazione 
					this.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, this.registryReader, idFruizione);

					dati = apsHelper.addHiddenFieldsToDati(tipoOp, this.id, null, null, dati);

					dati = apsHelper.addServiziFruitoriToDati(dati, this.provider, this.wsdlimpler, this.wsdlimplfru, 
							soggettiList, soggettiListLabel, "0", this.id, tipoOp, "", "", "", nomeservizio, tiposervizio, versioneservizio, this.correlato, 
							this.statoPackage, this.statoPackage,asps.getStatoPackage(),null,this.validazioneDocumenti,
							this.servizioApplicativo,saList);

					dati = apsHelper.addFruitoreToDati(tipoOp, versioniLabel, versioniValues, this.profilo, this.clientAuth, dati,null
							,null,null,null,null,null,null,null);

					if (!InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
						dati = apsHelper.addEndPointToDati(dati, this.connettoreDebug, this.endpointtype, this.autenticazioneHttp, null,
								this.url, this.nome, this.tipo, this.user,
								this.password, this.initcont, this.urlpgk,
								this.provurl, this.connfact, this.sendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,tipoOp, this.httpsurl, this.httpstipologia,
								this.httpshostverify, this.httpspath, this.httpstipo,
								this.httpspwd, this.httpsalgoritmo, this.httpsstato,
								this.httpskeystore, this.httpspwdprivatekeytrust,
								this.httpspathkey, this.httpstipokey,
								this.httpspwdkey, this.httpspwdprivatekey,
								this.httpsalgoritmokey, this.tipoconn, 
								AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD, null,
								null, null, null, null, null, null, null, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								this.proxy_enabled, this.proxy_hostname, this.proxy_port, this.proxy_username, this.proxy_password,
								this.opzioniAvanzate, this.transfer_mode, this.transfer_mode_chunk_size, this.redirect_mode, this.redirect_max_hop,
								this.requestOutputFileName,this.requestOutputFileNameHeaders,this.requestOutputParentDirCreateIfNotExists,this.requestOutputOverwriteIfExists,
								this.responseInputMode, this.responseInputFileName, this.responseInputFileNameHeaders, this.responseInputDeleteAfterRead, this.responseInputWaitTime,
								listExtendedConnettore);
					}else{
						//spostato dentro l'helper
					}

					// aggiunta campi custom
					dati = apsHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties);

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
							ForwardParams.ADD());
				}
			}

			//imposto properties custom
			fruitore.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolProperties(this.protocolProperties, this.consoleOperationType,null));

			servsp.addFruitore(fruitore);
			apsCore.performUpdateOperation(superUser, apsHelper.smista(), servsp);

			// Prendo i dati del soggetto erogatore del servizio
			String mynomeprov = asps.getNomeSoggettoErogatore();
			String mytipoprov = asps.getTipoSoggettoErogatore();

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
				String nomePD = tipoFruitore + nomeFruitore + "/" + mytipoprov + mynomeprov + "/" + tiposervizio + nomeservizio + "/" + versioneservizio;
				String descr = "Invocazione servizio " + tiposervizio + nomeservizio + ":"+versioneservizio+" erogato da " + mytipoprov + mynomeprov;

				PortaDelegata portaDelegata = new PortaDelegata();
				portaDelegata.setNome(nomePD);
				portaDelegata.setDescrizione(descr);
				portaDelegata.setAutenticazione(autenticazionePortaDelegataAutomatica);
				portaDelegata.setAutorizzazione(autorizzazionePortaDelegataAutomatica);

				PortaDelegataSoggettoErogatore pdSogg = new PortaDelegataSoggettoErogatore();
				pdSogg.setNome(mynomeprov);
				pdSogg.setTipo(mytipoprov);
				portaDelegata.setSoggettoErogatore(pdSogg);

				PortaDelegataServizio pdServizio = new PortaDelegataServizio();
				pdServizio.setNome(nomeservizio);
				pdServizio.setTipo(tiposervizio);
				pdServizio.setVersione(versioneservizio);
				portaDelegata.setServizio(pdServizio);

				PortaDelegataAzione pdAzione = new PortaDelegataAzione();
				pdAzione.setIdentificazione(PortaDelegataAzioneIdentificazione.URL_BASED);
				String pattern = ".*" + nomePD + "/([^/|^?]*).*";
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
					PortaDelegataServizioApplicativo sa = new PortaDelegataServizioApplicativo();
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
				myidpd.setNome(nomePD);
				if (!porteDelegateCore.existsPortaDelegata(myidpd)){
					porteDelegateCore.performCreateOperation(superUser, apsHelper.smista(), portaDelegata);
				}
			}


			// cancello i file temporanei
			apsHelper.deleteBinaryParameters(this.wsdlimpler,this.wsdlimplfru);
			apsHelper.deleteBinaryProtocolPropertiesTmpFiles(this.protocolProperties);

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

	public void decodeRequest(HttpServletRequest request,AccordiServizioParteSpecificaHelper apsHelper) throws Exception {
		try {

			String ct = request.getContentType();
			String boundary = null;
			ContentType contentType = new ContentType(ct);
			if(contentType.getParameterList()!=null){
				Enumeration<?> enNames = contentType.getParameterList().getNames();
				while (enNames.hasMoreElements()) {
					Object object = enNames.nextElement();
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
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED+"\"") != -1) {
					line = dis.readLine();
					this.proxy_enabled = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME+"\"") != -1) {
					line = dis.readLine();
					this.proxy_hostname = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT+"\"") != -1) {
					line = dis.readLine();
					this.proxy_port = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME+"\"") != -1) {
					line = dis.readLine();
					this.proxy_username = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD+"\"") != -1) {
					line = dis.readLine();
					this.proxy_password = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE+"\"") != -1) {
					line = dis.readLine();
					this.opzioniAvanzate = dis.readLine();
					this.opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(this.opzioniAvanzate,this.transfer_mode, this.redirect_mode);
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE+"\"") != -1) {
					line = dis.readLine();
					this.transfer_mode = dis.readLine();
					this.opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(this.opzioniAvanzate,this.transfer_mode, this.redirect_mode);
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE+"\"") != -1) {
					line = dis.readLine();
					this.transfer_mode_chunk_size = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE+"\"") != -1) {
					line = dis.readLine();
					this.redirect_mode = dis.readLine();
					this.opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(this.opzioniAvanzate,this.transfer_mode, this.redirect_mode);
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP+"\"") != -1) {
					line = dis.readLine();
					this.redirect_max_hop = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_URL+"\"") != -1) {
					line = dis.readLine();
					this.url = dis.readLine();
					this.httpsurl = this.url;
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
				//				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE+"\"") != -1) {
				//					int startId = line.indexOf(Costanti.MULTIPART_FILENAME);
				//					startId = startId + 10;
				//					// int endId = line.lastIndexOf("\"");
				//					// String tmpNomeFile = line.substring(startId, endId);
				//					line = dis.readLine();
				//					line = dis.readLine();
				//					this.wsdlimpler = "";
				//					while (!line.startsWith("-----") || (line.startsWith("-----") && ((line.indexOf(Costanti.MULTIPART_BEGIN) != -1) || (line.indexOf(Costanti.MULTIPART_END) != -1)))) {
				//						if("".equals(this.wsdlimpler))
				//							this.wsdlimpler = line;
				//						else
				//							this.wsdlimpler = this.wsdlimpler + "\n" + line;
				//						line = dis.readLine();
				//					}
				//				}
				//				if (line.indexOf("\""+AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE+"\"") != -1) {
				//					int startId = line.indexOf(Costanti.MULTIPART_FILENAME);
				//					startId = startId + 10;
				//					// int endId = line.lastIndexOf("\"");
				//					// String tmpNomeFile = line.substring(startId, endId);
				//					line = dis.readLine();
				//					line = dis.readLine();
				//					this.wsdlimplfru = "";
				//					while (!line.startsWith("-----") || (line.startsWith("-----") && ((line.indexOf(Costanti.MULTIPART_BEGIN) != -1) || (line.indexOf(Costanti.MULTIPART_END) != -1)))) {
				//						if("".equals(this.wsdlimplfru))
				//							this.wsdlimplfru = line;
				//						else
				//							this.wsdlimplfru = this.wsdlimplfru + "\n" + line;
				//						line = dis.readLine();
				//					}
				//				}
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
				
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME+"\"") != -1) {
					line = dis.readLine();
					this.requestOutputFileName = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS+"\"") != -1) {
					line = dis.readLine();
					this.requestOutputFileNameHeaders = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR+"\"") != -1) {
					line = dis.readLine();
					this.requestOutputParentDirCreateIfNotExists = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME+"\"") != -1) {
					line = dis.readLine();
					this.requestOutputOverwriteIfExists = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE+"\"") != -1) {
					line = dis.readLine();
					this.responseInputMode = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME+"\"") != -1) {
					line = dis.readLine();
					this.responseInputFileName = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS+"\"") != -1) {
					line = dis.readLine();
					this.responseInputFileNameHeaders = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ+"\"") != -1) {
					line = dis.readLine();
					this.responseInputDeleteAfterRead = dis.readLine();
				}
				if (line.indexOf("\""+ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME+"\"") != -1) {
					line = dis.readLine();
					this.responseInputWaitTime = dis.readLine();
				}

				line = dis.readLine();
			}

			this.endpointtype = apsHelper.readEndPointType(this.endpointtype,this.endpointtype_check, this.endpointtype_ssl);

			bin.close();
			in.close();
		} catch (IOException ioe) {
			throw new Exception(ioe);
		}
	}
}
