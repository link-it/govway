/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.soggetti;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.pdd.core.autenticazione.ApiKey;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationCoreException;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.OggettoDialogEnum;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCore;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.User;

/**
 * soggettiChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class SoggettiChange extends Action {
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		SoggettiChangeStrutsBean strutsBean = new SoggettiChangeStrutsBean();
		
		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		strutsBean.consoleOperationType = ConsoleOperationType.CHANGE;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.CHANGE; 
		List<ProtocolProperty> oldProtocolPropertyList = null;
		try {
			SoggettiHelper soggettiHelper = new SoggettiHelper(request, pd, session);
			
			// Preparo il menu
			soggettiHelper.makeMenu();
			
			strutsBean.id = soggettiHelper.getParametroLong(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
			if(strutsBean.id==null) {
				throw new ControlStationCoreException("Identificativo soggetto non fornito");
			}
			long idSogg = Long.parseLong(strutsBean.id);
			strutsBean.nomeprov = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME);
			strutsBean.tipoprov = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO);
			strutsBean.portadom = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_PORTA);
			strutsBean.descr = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DESCRIZIONE);
			strutsBean.versioneProtocollo = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_VERSIONE_PROTOCOLLO);
			strutsBean.pdd = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PDD);
			String isRouterParameter = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_ROUTER);
			strutsBean.isRouter = ServletUtils.isCheckBoxEnabled(isRouterParameter);
			strutsBean.dominio = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO);
			String isPrivatoParameter = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_PRIVATO);
			strutsBean.privato = ServletUtils.isCheckBoxEnabled(isPrivatoParameter);
			strutsBean.codiceIpa = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_IPA);
			strutsBean.pd_url_prefix_rewriter = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER);
			strutsBean.pa_url_prefix_rewriter = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER);

			strutsBean.editMode = soggettiHelper.getParametroEditMode(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			strutsBean.protocolPropertiesSet = soggettiHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_SET);
			
			strutsBean.tipoauthSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
			strutsBean.utenteSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
			strutsBean.passwordSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			strutsBean.subjectSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
			strutsBean.principalSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL);
			
			String changepwd = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD);
						
			String tipoCredenzialiSSLSorgente = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL);
			if(tipoCredenzialiSSLSorgente == null) {
				tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO;
			}
			String tipoCredenzialiSSLTipoArchivioS = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO);
			org.openspcoop2.utils.certificate.ArchiveType tipoCredenzialiSSLTipoArchivio= null;
			if(tipoCredenzialiSSLTipoArchivioS == null) {
				tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.CER; 
			} else {
				tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.valueOf(tipoCredenzialiSSLTipoArchivioS);
			}
			BinaryParameter tipoCredenzialiSSLFileCertificato = soggettiHelper.getBinaryParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO);
			String tipoCredenzialiSSLFileCertificatoPassword = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD);
			List<String> listaAliasEstrattiCertificato = new ArrayList<>();
			String tipoCredenzialiSSLAliasCertificato = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO);
			if (tipoCredenzialiSSLAliasCertificato == null) {
				tipoCredenzialiSSLAliasCertificato = "";
			}
			String tipoCredenzialiSSLAliasCertificatoSubject= soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SUBJECT);
			String tipoCredenzialiSSLAliasCertificatoIssuer= soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_ISSUER);
			String tipoCredenzialiSSLAliasCertificatoType= soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_TYPE);
			String tipoCredenzialiSSLAliasCertificatoVersion= soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_VERSION);
			String tipoCredenzialiSSLAliasCertificatoSerialNumber= soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SERIAL_NUMBER);
			String tipoCredenzialiSSLAliasCertificatoSelfSigned= soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SELF_SIGNED);
			String tipoCredenzialiSSLAliasCertificatoNotBefore= soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_BEFORE);
			String tipoCredenzialiSSLAliasCertificatoNotAfter = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_AFTER); 
			String tipoCredenzialiSSLVerificaTuttiICampi = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
			if ( ((tipoCredenzialiSSLVerificaTuttiICampi == null || StringUtils.isEmpty(tipoCredenzialiSSLVerificaTuttiICampi))
					||
					 SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO.equalsIgnoreCase(soggettiHelper.getPostBackElementName())
					 ||
					 SoggettiCostanti.PARAMETRO_SOGGETTO_TIPOLOGIA.equalsIgnoreCase(soggettiHelper.getPostBackElementName())
					) &&
				(soggettiHelper.isEditModeInProgress() && soggettiHelper.getPostBackElementName()==null) 
				){ // prima volta
				tipoCredenzialiSSLVerificaTuttiICampi = ConnettoriCostanti.DEFAULT_VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI;
			}
			String tipoCredenzialiSSLConfigurazioneManualeSelfSigned= soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_MANUALE_SELF_SIGNED);
			if (tipoCredenzialiSSLConfigurazioneManualeSelfSigned == null) {
				tipoCredenzialiSSLConfigurazioneManualeSelfSigned = Costanti.CHECK_BOX_DISABLED;
			}
			String issuerSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER);

			String tipoCredenzialiSSLWizardStep = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP);
			if (tipoCredenzialiSSLWizardStep == null) {
				tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD;
			}
			String oldTipoCredenzialiSSLWizardStep = tipoCredenzialiSSLWizardStep;
			
			String multipleApiKey = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS);
			String appId = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID);
			String apiKey = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY);
			
			boolean visualizzaModificaCertificato = false;
			boolean visualizzaAddCertificato = false;
			String servletCredenzialiList = SoggettiCostanti.SERVLET_NAME_SOGGETTI_CREDENZIALI_LIST;
			List<Parameter> parametersServletCredenzialiList = null;
			Integer numeroCertificati = 0;
			String servletCredenzialiAdd = SoggettiCostanti.SERVLET_NAME_SOGGETTI_CREDENZIALI_ADD;
			
			strutsBean.modificaOperativo = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_MODIFICA_OPERATIVO);
			
			String resetElementoCacheS = soggettiHelper.getParameter(CostantiControlStation.PARAMETRO_ELIMINA_ELEMENTO_DALLA_CACHE);
			boolean resetElementoCache = ServletUtils.isCheckBoxEnabled(resetElementoCacheS);

			// Prendo i vecchi nome e tipo
			String oldnomeprov = "";
			String oldtipoprov = "";
			String oldpdd = null;

			Boolean contaListe = ServletUtils.getContaListeFromSession(session);
			String userLogin = ServletUtils.getUserLoginFromSession(session);

			Soggetto soggettoRegistry = null;
			org.openspcoop2.core.config.Soggetto soggettoConfig = null;
			List<String> tipiSoggetti = null;
			int numPA = 0;
			int numPD = 0;
			int numSA = 0;
			String[] pddList = null;
			String[] pddEsterneList = null;
			List<String> versioniProtocollo = null;

			SoggettiCore soggettiCore = new SoggettiCore();
			PddCore pddCore = new PddCore(soggettiCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(soggettiCore);
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(soggettiCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(soggettiCore);
			UtentiCore utentiCore = new UtentiCore(soggettiCore);

			String nomePddGestioneLocale = null;
			if(!pddCore.isGestionePddAbilitata(soggettiHelper)){
				nomePddGestioneLocale = pddCore.getNomePddOperativa();
				if(nomePddGestioneLocale==null) {
					throw new ControlStationCoreException("Non è stata rilevata una pdd di tipologia 'operativo'");
				}
			}

			if(soggettiCore.isRegistroServiziLocale()){
				soggettoRegistry = soggettiCore.getSoggettoRegistro(idSogg);// core.getSoggettoRegistro(new
			}

			soggettoConfig = soggettiCore.getSoggetto(idSogg);// core.getSoggetto(new

			if(soggettiCore.isRegistroServiziLocale()){
				oldnomeprov = soggettoRegistry.getNome();
				oldtipoprov = soggettoRegistry.getTipo();
				oldpdd = soggettoRegistry.getPortaDominio();
			}
			else{
				oldnomeprov = soggettoConfig.getNome();
				oldtipoprov = soggettoConfig.getTipo();
			}
			
			if(strutsBean.tipoprov == null) {
				strutsBean.tipoprov = oldtipoprov;
			}
			
			if(strutsBean.nomeprov == null) {
				strutsBean.nomeprov = oldnomeprov;
			}
			
			Parameter pIdSoggetto = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID, strutsBean.id);
			parametersServletCredenzialiList = new ArrayList<>();
			parametersServletCredenzialiList.add(pIdSoggetto);

			boolean encryptOldPlainPwd = false;
			if(soggettoRegistry!=null && soggettoRegistry.sizeCredenzialiList()>0 && 
					org.openspcoop2.core.registry.constants.CredenzialeTipo.BASIC.equals(soggettoRegistry.getCredenziali(0).getTipo())) {
				encryptOldPlainPwd = !soggettoRegistry.getCredenziali(0).isCertificateStrictVerification() && soggettiCore.isSoggettiPasswordEncryptEnabled(); 
			}
			
			// Tipi protocollo supportati
			List<String> listaTipiProtocollo = soggettiCore.getProtocolli(request, session);
			//tipiSoggetti = soggettiCore.getTipiSoggettiGestiti(versioneProtocollo); // all tipi soggetti gestiti
			// Nella change non e' piu' possibile cambiare il protocollo
			strutsBean.protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(strutsBean.tipoprov);
			tipiSoggetti = soggettiCore.getTipiSoggettiGestitiProtocollo(strutsBean.protocollo);

			if(soggettiHelper.isModalitaAvanzata()){
				versioniProtocollo = soggettiCore.getVersioniProtocollo(strutsBean.protocollo);
			}else {
				versioniProtocollo = new ArrayList<>();
				strutsBean.versioneProtocollo = soggettiCore.getVersioneDefaultProtocollo(strutsBean.protocollo);
				versioniProtocollo.add(strutsBean.versioneProtocollo);
			}
			boolean isSupportatoAutenticazioneSoggetti = soggettiCore.isSupportatoAutenticazioneSoggetti(strutsBean.protocollo);

			boolean isSupportatoCodiceIPA = soggettiCore.isSupportatoCodiceIPA(strutsBean.protocollo); 
			boolean isSupportatoIdentificativoPorta = soggettiCore.isSupportatoIdentificativoPorta(strutsBean.protocollo);


			boolean isPddEsterna = pddCore.isPddEsterna(strutsBean.pdd);
			if(isSupportatoAutenticazioneSoggetti &&
				isPddEsterna &&
					strutsBean.tipoauthSoggetto==null && ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA.equals(strutsBean.tipoauthSoggetto)){
				strutsBean.tipoauthSoggetto = soggettiCore.getAutenticazione_generazioneAutomaticaPorteApplicative();
			}
			
			if (contaListe!=null && contaListe.booleanValue()) {
				// Conto il numero di porte applicative
				IDSoggetto soggetto = new IDSoggetto(soggettoConfig.getTipo(),soggettoConfig.getNome());
				ConsoleSearch searchForCount = new ConsoleSearch(true,1);
				porteApplicativeCore.porteAppList(idSogg, searchForCount);
				numPA = searchForCount.getNumEntries(Liste.PORTE_APPLICATIVE_BY_SOGGETTO);
				porteDelegateCore.porteDelegateList(idSogg, searchForCount);
				numPD = searchForCount.getNumEntries(Liste.PORTE_DELEGATE_BY_SOGGETTO);
				saCore.servizioApplicativoList(soggetto, searchForCount);
				numSA = searchForCount.getNumEntries(Liste.SERVIZIO_APPLICATIVO);
			}

			if(soggettiCore.isSinglePdD() &&
				(soggettiCore.isRegistroServiziLocale())
				){
				// Prendo la lista di pdd e la metto in un array
				// In pratica se un soggetto e' associato ad una PdD Operativa,
				// e possiede gia' delle PD o PA o SA,
				// non e' piu' possibile cambiargli la porta di dominio in una esterna.

				boolean pddOperativa = false;
				if(soggettoRegistry.getPortaDominio()!=null && !"".equals(soggettoRegistry.getPortaDominio())){
					PdDControlStation pddCtrlstat = pddCore.getPdDControlStation(soggettoRegistry.getPortaDominio());
					pddOperativa = PddTipologia.OPERATIVO.toString().equals(pddCtrlstat.getTipo());
				}

				List<PdDControlStation> lista = new ArrayList<>();
				if( (numPA<=0 && numPD<=0 && numSA<=0) || !pddOperativa ){
					
					List<String> pddEsterne = new ArrayList<>();
					pddEsterne.add("-");
					
					// aggiungo un elemento di comodo
					PdDControlStation tmp = new PdDControlStation();
					tmp.setNome("-");
					lista.add(tmp);
					// aggiungo gli altri elementi
					if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
						lista.addAll(pddCore.pddList(null, new ConsoleSearch(true)));
					}else{
						lista.addAll(pddCore.pddList(userLogin, new ConsoleSearch(true)));
					}
					pddList = new String[lista.size()];
					int i = 0;
					for (PdDControlStation pddTmp : lista) {
						pddList[i] = pddTmp.getNome();
						i++;
						
						if(PddTipologia.ESTERNO.toString().equals(pddTmp.getTipo())){
							pddEsterne.add(pddTmp.getNome());
						}
					}
					
					pddEsterneList = pddEsterne.toArray(new String[1]);
				}
				else{
					// non posso modificare la pdd. Lascio solo quella operativa
					pddList = new String[1];
					pddList[0] = soggettoRegistry.getPortaDominio();
				}
			}

			org.openspcoop2.core.registry.Connettore connettore = null;
			if(soggettiCore.isRegistroServiziLocale()){
				connettore = soggettoRegistry.getConnettore();
			}


			IDSoggetto idSoggetto = new IDSoggetto(oldtipoprov,oldnomeprov);
			// Protocol Properties	
			strutsBean.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(strutsBean.protocollo);
			strutsBean.consoleDynamicConfiguration =  strutsBean.protocolFactory.createDynamicConfigurationConsole();
			strutsBean.registryReader = soggettiCore.getRegistryReader(strutsBean.protocolFactory); 
			strutsBean.configRegistryReader = soggettiCore.getConfigIntegrationReader(strutsBean.protocolFactory);
			strutsBean.consoleConfiguration = strutsBean.consoleDynamicConfiguration.getDynamicConfigSoggetto(strutsBean.consoleOperationType, soggettiHelper, 
					strutsBean.registryReader, strutsBean.configRegistryReader, idSoggetto);
			strutsBean.protocolProperties = soggettiHelper.estraiProtocolPropertiesDaRequest(strutsBean.consoleConfiguration, strutsBean.consoleOperationType);
			
			CredenzialiSoggetto oldCredenziali = null;
			try{
				Soggetto soggetto = strutsBean.registryReader.getSoggetto(idSoggetto);
				oldProtocolPropertyList = soggetto.getProtocolPropertyList(); 
				numeroCertificati = soggetto.sizeCredenzialiList();
				if(soggetto.sizeCredenzialiList()>0) {
					oldCredenziali = soggetto.getCredenziali(0);
					
					visualizzaAddCertificato = true;
					if(soggetto.sizeCredenzialiList() == 1) {  // se ho definito solo un certificato c'e' il link diretto alla modifica
						visualizzaModificaCertificato = true;
					}
				}
			}catch(RegistryNotFound r){
				// ignore
			}

			Properties propertiesProprietario = new Properties();
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, strutsBean.id);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_SOGGETTO);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, oldtipoprov + "/" + oldnomeprov);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE, URLEncoder.encode( SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE + "?" + request.getQueryString(), "UTF-8"));
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, strutsBean.protocollo);
			
			String postBackElementName = soggettiHelper.getPostBackElementName();
			String labelButtonSalva = Costanti.LABEL_MONITOR_BUTTON_INVIA;

			// Controllo se ho modificato il protocollo, ricalcolo il default della versione del protocollo
			boolean postBackTipoAuthSoggetto = false;
			if(postBackElementName != null ){
				// tipo autenticazione
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE)){
					
					postBackTipoAuthSoggetto = true;
					
					if(strutsBean.tipoauthSoggetto != null && strutsBean.tipoauthSoggetto.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)) {
						
						if(oldCredenziali!=null && oldCredenziali.getTipo()!=null && oldCredenziali.getTipo().getValue().equals(strutsBean.tipoauthSoggetto)){
							
							tipoCredenzialiSSLWizardStep  = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD;
							
							// il certificato non e' cambiato 
							if(tipoCredenzialiSSLAliasCertificatoSubject==null || StringUtils.isEmpty(tipoCredenzialiSSLAliasCertificatoSubject)) {
								if(oldCredenziali.getCertificate()!=null) {
									try {
										Certificate cSelezionato = ArchiveLoader.load(oldCredenziali.getCertificate());
										tipoCredenzialiSSLAliasCertificatoIssuer = cSelezionato.getCertificate().getIssuer().getNameNormalized();
										tipoCredenzialiSSLAliasCertificatoSubject = cSelezionato.getCertificate().getSubject().getNameNormalized();
										tipoCredenzialiSSLAliasCertificatoSelfSigned = cSelezionato.getCertificate().isSelfSigned() ? CostantiControlStation.LABEL_SI : CostantiControlStation.LABEL_NO;
										tipoCredenzialiSSLAliasCertificatoSerialNumber = cSelezionato.getCertificate().getSerialNumber() + "";
										tipoCredenzialiSSLAliasCertificatoType = cSelezionato.getCertificate().getType();
										tipoCredenzialiSSLAliasCertificatoVersion = cSelezionato.getCertificate().getVersion() + "";
										tipoCredenzialiSSLAliasCertificatoNotBefore = soggettiHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotBefore());
										tipoCredenzialiSSLAliasCertificatoNotAfter = soggettiHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotAfter());
									}catch(UtilsException e) {
										pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
										tipoCredenzialiSSLAliasCertificato = "";
										tipoCredenzialiSSLAliasCertificatoSubject= "";
										tipoCredenzialiSSLAliasCertificatoIssuer= "";
										tipoCredenzialiSSLAliasCertificatoType= "";
										tipoCredenzialiSSLAliasCertificatoVersion= "";
										tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
										tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
										tipoCredenzialiSSLAliasCertificatoNotBefore= "";
										tipoCredenzialiSSLAliasCertificatoNotAfter = "";
									}
								}
								tipoCredenzialiSSLVerificaTuttiICampi = oldCredenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED :Costanti.CHECK_BOX_DISABLED;
							}
						}
						else {
						
							// reset impostazioni sezione ssl
							tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO;
							tipoCredenzialiSSLTipoArchivio = ArchiveType.CER;
							tipoCredenzialiSSLVerificaTuttiICampi = ConnettoriCostanti.DEFAULT_VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI;
							tipoCredenzialiSSLAliasCertificato = "";
							tipoCredenzialiSSLAliasCertificatoSubject= "";
							tipoCredenzialiSSLAliasCertificatoIssuer= "";
							tipoCredenzialiSSLAliasCertificatoType= "";
							tipoCredenzialiSSLAliasCertificatoVersion= "";
							tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
							tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
							tipoCredenzialiSSLAliasCertificatoNotBefore= "";
							tipoCredenzialiSSLAliasCertificatoNotAfter = "";
							listaAliasEstrattiCertificato = new ArrayList<>();
							tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO;
							
						}
							
					} else {
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD;
						
						if(oldCredenziali!=null && oldCredenziali.getTipo()!=null) {
							if(oldCredenziali.getTipo().getValue().equals(strutsBean.tipoauthSoggetto)) {
								if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC.equals(strutsBean.tipoauthSoggetto)) {
									strutsBean.utenteSoggetto = oldCredenziali.getUser();
									strutsBean.passwordSoggetto = oldCredenziali.getPassword();
									tipoCredenzialiSSLVerificaTuttiICampi = oldCredenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
								}
								if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(strutsBean.tipoauthSoggetto)) {
									strutsBean.utenteSoggetto = oldCredenziali.getUser();
									strutsBean.passwordSoggetto = oldCredenziali.getPassword();
									tipoCredenzialiSSLVerificaTuttiICampi = oldCredenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
									multipleApiKey = oldCredenziali.isAppId() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
									appId = oldCredenziali.getUser();
									apiKey = oldCredenziali.getPassword();
								}
								if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL.equals(strutsBean.tipoauthSoggetto)) {
									strutsBean.principalSoggetto = oldCredenziali.getUser();
								}
							}
							else {
								if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC.equals(strutsBean.tipoauthSoggetto) || 
										ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(strutsBean.tipoauthSoggetto)) {
									tipoCredenzialiSSLVerificaTuttiICampi = Costanti.CHECK_BOX_DISABLED; // dovrà essere re-effettuata la cifratura
								}
							}
						}

					}
				}

				// tipo di configurazione SSL
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL) || 
						postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_MODIFICA)) {
					listaAliasEstrattiCertificato = new ArrayList<>();
					tipoCredenzialiSSLTipoArchivio = ArchiveType.CER;
					tipoCredenzialiSSLVerificaTuttiICampi = ConnettoriCostanti.DEFAULT_VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI;
					tipoCredenzialiSSLAliasCertificato = "";
					tipoCredenzialiSSLAliasCertificatoSubject= "";
					tipoCredenzialiSSLAliasCertificatoIssuer= "";
					tipoCredenzialiSSLAliasCertificatoType= "";
					tipoCredenzialiSSLAliasCertificatoVersion= "";
					tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
					tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
					tipoCredenzialiSSLAliasCertificatoNotBefore= "";
					tipoCredenzialiSSLAliasCertificatoNotAfter = "";
					soggettiHelper.deleteBinaryParameters(tipoCredenzialiSSLFileCertificato); 
					tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO;

					if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE)) {
						strutsBean.subjectSoggetto = "";
						issuerSoggetto = "";
						tipoCredenzialiSSLConfigurazioneManualeSelfSigned = Costanti.CHECK_BOX_DISABLED;
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD;
					}
				}

				// cambio il tipo archivio butto via il vecchio certificato				
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO)) {
					soggettiHelper.deleteBinaryParameters(tipoCredenzialiSSLFileCertificato); 
					tipoCredenzialiSSLAliasCertificato = "";
					tipoCredenzialiSSLAliasCertificatoSubject= "";
					tipoCredenzialiSSLAliasCertificatoIssuer= "";
					tipoCredenzialiSSLAliasCertificatoType= "";
					tipoCredenzialiSSLAliasCertificatoVersion= "";
					tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
					tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
					tipoCredenzialiSSLAliasCertificatoNotBefore= "";
					tipoCredenzialiSSLAliasCertificatoNotAfter = "";
					listaAliasEstrattiCertificato = new ArrayList<>();
					tipoCredenzialiSSLVerificaTuttiICampi = ConnettoriCostanti.DEFAULT_VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI;
					tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO;
				}

				// selezione alias
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO)) {
					if(StringUtils.isNotEmpty(tipoCredenzialiSSLAliasCertificato)) {
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK;
					} else {
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ALIAS_NON_SCELTO;
					}
					tipoCredenzialiSSLAliasCertificatoSubject= "";
					tipoCredenzialiSSLAliasCertificatoIssuer= "";
					tipoCredenzialiSSLAliasCertificatoType= "";
					tipoCredenzialiSSLAliasCertificatoVersion= "";
					tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
					tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
					tipoCredenzialiSSLAliasCertificatoNotBefore= "";
					tipoCredenzialiSSLAliasCertificatoNotAfter = "";
				}
				
				// Change Password basic/api
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD)) {
					if(!ServletUtils.isCheckBoxEnabled(changepwd)) {
						if (oldCredenziali != null){
							if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC.equals(strutsBean.tipoauthSoggetto)) {
								strutsBean.passwordSoggetto = oldCredenziali.getPassword();
								tipoCredenzialiSSLVerificaTuttiICampi = oldCredenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
							}
							if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(strutsBean.tipoauthSoggetto)) {
								strutsBean.passwordSoggetto = oldCredenziali.getPassword();
								tipoCredenzialiSSLVerificaTuttiICampi = oldCredenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
								multipleApiKey = oldCredenziali.isAppId() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
								appId = oldCredenziali.getUser();
								apiKey = oldCredenziali.getPassword();
							}
						}
					}
					else {
						appId = null;
						apiKey = null;
					}
				}
				
				// apikey
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS)) {
					appId = null;
					apiKey = null;
				}
			}
			if(postBackTipoAuthSoggetto) {
				// nop
			}
			
			// reset elemento dalla cache
			if(resetElementoCache) {
				
				// Uso lo stessoAlias
				List<String> aliases = soggettiCore.getJmxPdDAliases();
				String alias = null;
				if(aliases!=null && !aliases.isEmpty()) {
					alias = aliases.get(0);
				}
				String labelSoggetto = soggettiHelper.getLabelNomeSoggetto(strutsBean.protocollo, oldtipoprov , oldnomeprov);
				soggettiCore.invokeJmxMethodAllNodesAndSetResult(pd, soggettiCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
						soggettiCore.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheSoggetto(alias),
						MessageFormat.format(CostantiControlStation.LABEL_ELIMINATO_CACHE_SUCCESSO,labelSoggetto),
						MessageFormat.format(CostantiControlStation.LABEL_ELIMINATO_CACHE_FALLITO_PREFIX,labelSoggetto),
						idSogg);				
				
				String resetFromLista = soggettiHelper.getParameter(CostantiControlStation.PARAMETRO_RESET_CACHE_FROM_LISTA);
				boolean arrivoDaLista = "true".equalsIgnoreCase(resetFromLista);
				
				if(arrivoDaLista) {
					
					// preparo lista
					
					String filterDominioInterno = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_FILTER_DOMINIO_INTERNO);
					boolean forceFilterDominioInterno = false;
					if("true".equalsIgnoreCase(filterDominioInterno)) {
						forceFilterDominioInterno = true;
					}
					
					boolean multiTenant = soggettiCore.isMultitenant();
					
					ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
					
					int idLista = Liste.SOGGETTI;
					
					// poiche' esistono filtri che hanno necessita di postback salvo in sessione
					List<Soggetto> lista = null;
					if(soggettiCore.isRegistroServiziLocale() &&
						!ServletUtils.isSearchDone(soggettiHelper)) {
						lista = ServletUtils.getRisultatiRicercaFromSession(request, session, idLista,  Soggetto.class);
					}
					
					ricerca = soggettiHelper.checkSearchParameters(idLista, ricerca);
					
					if(forceFilterDominioInterno) {
						ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
					}
					else if(!multiTenant && !soggettiHelper.isModalitaCompleta()) {
						ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE);
					}
					
					if(soggettiCore.isRegistroServiziLocale()){
						if(lista==null) {
							if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
								lista = soggettiCore.soggettiRegistroList(null, ricerca);
							}else{
								lista = soggettiCore.soggettiRegistroList(userLogin, ricerca);
							}
						}
						
						if(!soggettiHelper.isPostBackFilterElement()) {
							ServletUtils.setRisultatiRicercaIntoSession(request, session, idLista, lista); // salvo poiche' esistono filtri che hanno necessita di postback
						}
						
						soggettiHelper.prepareSoggettiList(lista, ricerca);
					}
					else{
						List<org.openspcoop2.core.config.Soggetto> listaSoggetti = null;
						if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
							listaSoggetti = soggettiCore.soggettiList(null, ricerca);
						}else{
							listaSoggetti = soggettiCore.soggettiList(userLogin, ricerca);
						}
						soggettiHelper.prepareSoggettiConfigList(listaSoggetti, ricerca);
					}
						
					ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
					
					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeFinished(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.CHANGE());
				}
			}
			
			boolean checkWizard = false;
			if(
					//!postBackTipoAuthSoggetto &&  // Fix: non veniva attivato il wizard quando da una credenziale diversa da https (anche nessuna) si impostava https  
					strutsBean.tipoauthSoggetto != null && strutsBean.tipoauthSoggetto.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)) {
				
				boolean changeTipoDominio = false;
				if(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO.equalsIgnoreCase(postBackElementName)) {
					changeTipoDominio = true;
				}
				
				if(!changeTipoDominio && tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO) &&
						!ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI.equals(postBackElementName)) {
					if(tipoCredenzialiSSLFileCertificato.getValue() != null && tipoCredenzialiSSLFileCertificato.getValue().length > 0) {
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ALIAS_NON_SCELTO;
						if(!tipoCredenzialiSSLTipoArchivio.equals(ArchiveType.CER)) {
							if(StringUtils.isNotEmpty(tipoCredenzialiSSLFileCertificatoPassword)) {
								try {
									listaAliasEstrattiCertificato = ArchiveLoader.readAliases(tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato.getValue(), tipoCredenzialiSSLFileCertificatoPassword);
									Collections.sort(listaAliasEstrattiCertificato);
	
									//se ho un solo alias lo imposto
									if(!listaAliasEstrattiCertificato.isEmpty() && listaAliasEstrattiCertificato.size() == 1) {
										tipoCredenzialiSSLAliasCertificato = listaAliasEstrattiCertificato.get(0);
									}
									
									// ho appena caricato il file devo solo far vedere la form senza segnalare il messaggio di errore
									if(oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO)
											||oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_PASSWORD_NON_PRESENTE) 
											|| oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO)) {
										checkWizard = true;
										if(listaAliasEstrattiCertificato.size() > 1) {
											pd.setMessage("Il file caricato contiene pi&ugrave; certificati, selezionare un'"+ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO, MessageType.INFO);
										}  
									}
								}catch(UtilsException e) {
									pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
									tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO;
									tipoCredenzialiSSLAliasCertificato = "";
									tipoCredenzialiSSLAliasCertificatoSubject= "";
									tipoCredenzialiSSLAliasCertificatoIssuer= "";
									tipoCredenzialiSSLAliasCertificatoType= "";
									tipoCredenzialiSSLAliasCertificatoVersion= "";
									tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
									tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
									tipoCredenzialiSSLAliasCertificatoNotBefore= "";
									tipoCredenzialiSSLAliasCertificatoNotAfter = "";
								}
	
								if(!listaAliasEstrattiCertificato.isEmpty() && StringUtils.isNotEmpty(tipoCredenzialiSSLAliasCertificato)) {
									try {
										Certificate cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato.getValue(), tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLFileCertificatoPassword);
										tipoCredenzialiSSLAliasCertificatoIssuer = cSelezionato.getCertificate().getIssuer().getNameNormalized();
										tipoCredenzialiSSLAliasCertificatoSubject = cSelezionato.getCertificate().getSubject().getNameNormalized();
										tipoCredenzialiSSLAliasCertificatoSelfSigned = cSelezionato.getCertificate().isSelfSigned() ? CostantiControlStation.LABEL_SI : CostantiControlStation.LABEL_NO;
										tipoCredenzialiSSLAliasCertificatoSerialNumber = cSelezionato.getCertificate().getSerialNumber() + "";
										tipoCredenzialiSSLAliasCertificatoType = cSelezionato.getCertificate().getType();
										tipoCredenzialiSSLAliasCertificatoVersion = cSelezionato.getCertificate().getVersion() + "";
										tipoCredenzialiSSLAliasCertificatoNotBefore = strutsBean.sdf.format(cSelezionato.getCertificate().getNotBefore());
										tipoCredenzialiSSLAliasCertificatoNotAfter = strutsBean.sdf.format(cSelezionato.getCertificate().getNotAfter());
										tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK;
									}catch(UtilsException e) {
										pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
										tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO;
										tipoCredenzialiSSLAliasCertificato = "";
										tipoCredenzialiSSLAliasCertificatoSubject= "";
										tipoCredenzialiSSLAliasCertificatoIssuer= "";
										tipoCredenzialiSSLAliasCertificatoType= "";
										tipoCredenzialiSSLAliasCertificatoVersion= "";
										tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
										tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
										tipoCredenzialiSSLAliasCertificatoNotBefore= "";
										tipoCredenzialiSSLAliasCertificatoNotAfter = "";
									}
								} 
							} else {
								tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_PASSWORD_NON_PRESENTE;
							}
						} else {
							try {
								Certificate cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLFileCertificato.getValue());
								tipoCredenzialiSSLAliasCertificatoIssuer = cSelezionato.getCertificate().getIssuer().getNameNormalized();
								tipoCredenzialiSSLAliasCertificatoSubject = cSelezionato.getCertificate().getSubject().getNameNormalized();
								tipoCredenzialiSSLAliasCertificatoSelfSigned = cSelezionato.getCertificate().isSelfSigned() ? CostantiControlStation.LABEL_SI : CostantiControlStation.LABEL_NO;
								tipoCredenzialiSSLAliasCertificatoSerialNumber = cSelezionato.getCertificate().getSerialNumber() + "";
								tipoCredenzialiSSLAliasCertificatoType = cSelezionato.getCertificate().getType();
								tipoCredenzialiSSLAliasCertificatoVersion = cSelezionato.getCertificate().getVersion() + "";
								tipoCredenzialiSSLAliasCertificatoNotBefore = strutsBean.sdf.format(cSelezionato.getCertificate().getNotBefore());
								tipoCredenzialiSSLAliasCertificatoNotAfter = strutsBean.sdf.format(cSelezionato.getCertificate().getNotAfter());
								
								// dalla seconda volta che passo, posso salvare, la prima mostro il recap del certificato estratto
								
								if(oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK_TIPO_CER)||
										oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK)) {
									tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK;
								} else {
									tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK_TIPO_CER;
									checkWizard = true;
								}
							}catch(UtilsException e) {
								pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
								tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO;
								tipoCredenzialiSSLAliasCertificato = "";
								tipoCredenzialiSSLAliasCertificatoSubject= "";
								tipoCredenzialiSSLAliasCertificatoIssuer= "";
								tipoCredenzialiSSLAliasCertificatoType= "";
								tipoCredenzialiSSLAliasCertificatoVersion= "";
								tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
								tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
								tipoCredenzialiSSLAliasCertificatoNotBefore= "";
								tipoCredenzialiSSLAliasCertificatoNotAfter = "";
							}
						}
					} else {
						if(tipoCredenzialiSSLAliasCertificatoSubject==null || StringUtils.isEmpty(tipoCredenzialiSSLAliasCertificatoSubject)) {
							tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO;
						}
					}
				}
				
				if(StringUtils.isNotEmpty(tipoCredenzialiSSLWizardStep) && ( tipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO)
						||tipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_PASSWORD_NON_PRESENTE) 
						|| tipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO)
						)) {
					if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO)) {
						labelButtonSalva = ConnettoriCostanti.LABEL_BUTTON_INVIA_CARICA_CERTIFICATO;
					}
				}
				else { 
					labelButtonSalva = Costanti.LABEL_MONITOR_BUTTON_INVIA;
				}
			} else {
				labelButtonSalva = Costanti.LABEL_MONITOR_BUTTON_INVIA;
			}
			
			if(postBackElementName == null && oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD))
				tipoCredenzialiSSLWizardStep  = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD;
			
			if(labelButtonSalva!= null) {
				pd.setLabelBottoneInvia(labelButtonSalva);
			}
			
			boolean multipleApiKeysEnabled = false;
			boolean appIdModificabile = ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID_MODIFICABILE;
			if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(strutsBean.tipoauthSoggetto)) {
				multipleApiKeysEnabled = ServletUtils.isCheckBoxEnabled(multipleApiKey);
				if(appIdModificabile && multipleApiKeysEnabled) {
					boolean soggettoDefined =
							idSoggetto!=null && 
							idSoggetto.getTipo()!=null && !"".equals(idSoggetto.getTipo()) && 
							idSoggetto.getNome()!=null && !"".equals(idSoggetto.getNome());
					if( (appId==null || "".equals(appId)) 
							&&
						soggettoDefined){
						appId = soggettiCore.toAppId(strutsBean.protocollo, idSoggetto, multipleApiKeysEnabled);
					}
				}
			}
			
			boolean modificataTipoAutenticazione = false;
			if(oldCredenziali!=null && oldCredenziali.getTipo()!=null) {
				if(!oldCredenziali.getTipo().getValue().equals(strutsBean.tipoauthSoggetto)) {
					modificataTipoAutenticazione = true;
				}
			}
			else {
				if(strutsBean.tipoauthSoggetto!=null && !"".equals(strutsBean.tipoauthSoggetto) && !ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA.equals(strutsBean.tipoauthSoggetto)) {
					modificataTipoAutenticazione = true;
				}
			}
			
			// Se nomehid = null, devo visualizzare la pagina per la modifica dati
			if(ServletUtils.isEditModeInProgress(strutsBean.editMode) || checkWizard){

				// setto la barra del titolo
				ServletUtils.setPageDataTitleServletChange(pd, SoggettiCostanti.LABEL_SOGGETTI, 
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST, 
						soggettiHelper.getLabelNomeSoggetto(strutsBean.protocollo, oldtipoprov , oldnomeprov));

				if (isRouterParameter == null) 
					strutsBean.isRouter = soggettoConfig.getRouter();
				if(soggettiCore.isRegistroServiziLocale()){
					if(strutsBean.portadom==null){
						strutsBean.portadom = soggettoRegistry.getIdentificativoPorta();
					}else{
						String oldProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(oldtipoprov);
						if(!oldProtocollo.equals(strutsBean.protocollo)){
							// e' cambiato il protocollo: setto a empty il portadom
							strutsBean.portadom = null;
						}
					}
					if(strutsBean.descr==null)
						strutsBean.descr = soggettoRegistry.getDescrizione();
					if(strutsBean.pdd==null)
						strutsBean.pdd = soggettoRegistry.getPortaDominio();
					if(!pddCore.isGestionePddAbilitata(soggettiHelper)){
						if(strutsBean.dominio==null) {
							if(pddCore.isPddEsterna(strutsBean.pdd)) {
								strutsBean.dominio = SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE;
							}
							else {
								strutsBean.dominio = SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE;
							}
						}
						else {
							if(SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE.equals(strutsBean.dominio)) {
								strutsBean.pdd = nomePddGestioneLocale;
							}
							else {
								strutsBean.pdd = null;
							}
						}
					}
					if(strutsBean.versioneProtocollo==null)
						strutsBean.versioneProtocollo = soggettoRegistry.getVersioneProtocollo();
					if(soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_PRIVATO) == null){
						strutsBean.privato = soggettoRegistry.getPrivato()!=null && soggettoRegistry.getPrivato();
					}
					if(strutsBean.codiceIpa==null){
						strutsBean.codiceIpa = soggettoRegistry.getCodiceIpa();
					}
					if(isSupportatoAutenticazioneSoggetti){
						if (strutsBean.tipoauthSoggetto == null){
							CredenzialiSoggetto credenziali = null;
							if(soggettoRegistry.sizeCredenzialiList()>0) {
								credenziali = soggettoRegistry.getCredenziali(0);
							}
							if (credenziali != null){
								if(credenziali.getTipo()!=null)
									strutsBean.tipoauthSoggetto = credenziali.getTipo().toString();
								strutsBean.utenteSoggetto = credenziali.getUser();
								strutsBean.passwordSoggetto = credenziali.getPassword();
								if(strutsBean.tipoauthSoggetto!=null && ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC.equals(strutsBean.tipoauthSoggetto)){
									tipoCredenzialiSSLVerificaTuttiICampi = credenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
								}
								else if(strutsBean.tipoauthSoggetto!=null && ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(strutsBean.tipoauthSoggetto)){
									tipoCredenzialiSSLVerificaTuttiICampi = credenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
									multipleApiKey = credenziali.isAppId() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
									appId = credenziali.getUser();
									apiKey = credenziali.getPassword();
								}
								strutsBean.principalSoggetto = credenziali.getUser();
								
								if(credenziali.getCertificate() != null) {
									tipoCredenzialiSSLFileCertificato.setValue(credenziali.getCertificate());
									tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO;
									tipoCredenzialiSSLVerificaTuttiICampi = credenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED :Costanti.CHECK_BOX_DISABLED;
									
									try {
										Certificate cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLFileCertificato.getValue());
										tipoCredenzialiSSLAliasCertificatoIssuer = cSelezionato.getCertificate().getIssuer().getNameNormalized();
										tipoCredenzialiSSLAliasCertificatoSubject = cSelezionato.getCertificate().getSubject().getNameNormalized();
										tipoCredenzialiSSLAliasCertificatoSelfSigned = cSelezionato.getCertificate().isSelfSigned() ? CostantiControlStation.LABEL_SI : CostantiControlStation.LABEL_NO;
										tipoCredenzialiSSLAliasCertificatoSerialNumber = cSelezionato.getCertificate().getSerialNumber() + "";
										tipoCredenzialiSSLAliasCertificatoType = cSelezionato.getCertificate().getType();
										tipoCredenzialiSSLAliasCertificatoVersion = cSelezionato.getCertificate().getVersion() + "";
										tipoCredenzialiSSLAliasCertificatoNotBefore = strutsBean.sdf.format(cSelezionato.getCertificate().getNotBefore());
										tipoCredenzialiSSLAliasCertificatoNotAfter = strutsBean.sdf.format(cSelezionato.getCertificate().getNotAfter());
									}catch(UtilsException e) {
										pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
										tipoCredenzialiSSLAliasCertificato = "";
										tipoCredenzialiSSLAliasCertificatoSubject= "";
										tipoCredenzialiSSLAliasCertificatoIssuer= "";
										tipoCredenzialiSSLAliasCertificatoType= "";
										tipoCredenzialiSSLAliasCertificatoVersion= "";
										tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
										tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
										tipoCredenzialiSSLAliasCertificatoNotBefore= "";
										tipoCredenzialiSSLAliasCertificatoNotAfter = "";
									}
								} else {
									strutsBean.subjectSoggetto = credenziali.getSubject();
									issuerSoggetto = credenziali.getIssuer();
									tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE;
									tipoCredenzialiSSLConfigurazioneManualeSelfSigned = ( strutsBean.subjectSoggetto != null && strutsBean.subjectSoggetto.equals(issuerSoggetto)) ? Costanti.CHECK_BOX_ENABLED :Costanti.CHECK_BOX_DISABLED;
								}
							}
						}
						if (strutsBean.tipoauthSoggetto == null) {
							strutsBean.tipoauthSoggetto = ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA;
						}
					}

				}
				else{
					if(strutsBean.portadom==null){
						strutsBean.portadom = soggettoConfig.getIdentificativoPorta();
					}else{
						String oldProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(oldtipoprov);
						if(!oldProtocollo.equals(strutsBean.protocollo)){
							// e' cambiato il protocollo: setto a empty il portadom
							strutsBean.portadom = null;
						}
					}
					if(strutsBean.descr==null)
						strutsBean.descr = soggettoConfig.getDescrizione();
					if (isRouterParameter == null) 
						strutsBean.isRouter = soggettoConfig.getRouter();
				}

				if(strutsBean.pd_url_prefix_rewriter==null){
					strutsBean.pd_url_prefix_rewriter = soggettoConfig.getPdUrlPrefixRewriter();
				}
				if(strutsBean.pa_url_prefix_rewriter==null){
					strutsBean.pa_url_prefix_rewriter = soggettoConfig.getPaUrlPrefixRewriter();
				}
				if(strutsBean.tipoprov==null){
					strutsBean.tipoprov=oldtipoprov;
				}
				if(strutsBean.nomeprov==null){
					strutsBean.nomeprov=oldnomeprov;
				}
				idSoggetto = new IDSoggetto(strutsBean.tipoprov,strutsBean.nomeprov);
				
				try{
					Soggetto soggetto = strutsBean.registryReader.getSoggetto(idSoggetto);
					oldProtocolPropertyList = soggetto.getProtocolPropertyList();
				}catch(RegistryNotFound r){
					// ignore
				}
				
				// Inizializzazione delle properties da db al primo accesso alla pagina
				if(strutsBean.protocolPropertiesSet == null){
					ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(strutsBean.protocolProperties, oldProtocolPropertyList, strutsBean.consoleOperationType);
				}

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());


				// update della configurazione 
				strutsBean.consoleDynamicConfiguration.updateDynamicConfigSoggetto(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, soggettiHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idSoggetto); 
				
				dati = soggettiHelper.addSoggettiToDati(tipoOp,dati, strutsBean.nomeprov, strutsBean.tipoprov, strutsBean.portadom, strutsBean.descr, 
						strutsBean.isRouter, tipiSoggetti, strutsBean.versioneProtocollo, strutsBean.privato, strutsBean.codiceIpa, versioniProtocollo,
						isSupportatoCodiceIPA, isSupportatoIdentificativoPorta,
						pddList,pddEsterneList,nomePddGestioneLocale,strutsBean.pdd,strutsBean.id,oldnomeprov,oldtipoprov,connettore,
						numPD,strutsBean.pd_url_prefix_rewriter,numPA,strutsBean.pa_url_prefix_rewriter,listaTipiProtocollo,strutsBean.protocollo,
						isSupportatoAutenticazioneSoggetti,strutsBean.utenteSoggetto,strutsBean.passwordSoggetto,strutsBean.subjectSoggetto,strutsBean.principalSoggetto,strutsBean.tipoauthSoggetto,
						isPddEsterna,null,strutsBean.dominio,tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword, listaAliasEstrattiCertificato, 
						tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
						tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
						tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter, 
						tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuerSoggetto,tipoCredenzialiSSLWizardStep,
						changepwd,
						multipleApiKey, appId, apiKey, 
						visualizzaModificaCertificato, visualizzaAddCertificato, servletCredenzialiList, parametersServletCredenzialiList, numeroCertificati, servletCredenzialiAdd, soggettoRegistry.sizeProprietaList());

				// aggiunta campi custom
				dati = soggettiHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);

				// aggiunta opzione per modifica soggetto da configurazione
				if(strutsBean.modificaOperativo!=null && !"".equals(strutsBean.modificaOperativo)) {
					DataElement de = new DataElement();
					de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_MODIFICA_OPERATIVO);
					de.setType(DataElementType.HIDDEN);
					de.setValue(strutsBean.modificaOperativo);
					dati.add(de);
				}
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.CHANGE());

			}

			// Controlli sui campi immessi
			boolean isOk = SoggettiUtilities.soggettiCheckData(
					soggettiCore, soggettiHelper,
					org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE,
					oldnomeprov, oldtipoprov, strutsBean.privato,
					tipoOp, strutsBean.id, strutsBean.tipoprov, strutsBean.nomeprov, strutsBean.codiceIpa, strutsBean.pd_url_prefix_rewriter, strutsBean.pa_url_prefix_rewriter,
					soggettoRegistry, isSupportatoAutenticazioneSoggetti, strutsBean.descr, strutsBean.pdd);

			// updateDynamic
			if(isOk) {
				strutsBean.consoleDynamicConfiguration.updateDynamicConfigSoggetto(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, soggettiHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idSoggetto); 		
			}
			
			// Validazione base dei parametri custom 
			if(isOk){
				try{
					soggettiHelper.validaProtocolProperties(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, strutsBean.protocolProperties);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			// Valido i parametri custom se ho gia' passato tutta la validazione prevista
			if(isOk){
				try{
					//validazione campi dinamici
					strutsBean.consoleDynamicConfiguration.validateDynamicConfigSoggetto(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, soggettiHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idSoggetto); 
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitleServletChange(pd, SoggettiCostanti.LABEL_SOGGETTI, 
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST, 
						soggettiHelper.getLabelNomeSoggetto(strutsBean.protocollo, oldtipoprov , oldnomeprov));

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				strutsBean.consoleDynamicConfiguration.updateDynamicConfigSoggetto(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, soggettiHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idSoggetto); 

				dati = soggettiHelper.addSoggettiToDati(tipoOp,dati, strutsBean.nomeprov, strutsBean.tipoprov, strutsBean.portadom, strutsBean.descr, 
						strutsBean.isRouter, tipiSoggetti, strutsBean.versioneProtocollo, strutsBean.privato, strutsBean.codiceIpa, versioniProtocollo,
						isSupportatoCodiceIPA, isSupportatoIdentificativoPorta,
						pddList,pddEsterneList,nomePddGestioneLocale,strutsBean.pdd,strutsBean.id,oldnomeprov,oldtipoprov,connettore,
						numPD,strutsBean.pd_url_prefix_rewriter,numPA,strutsBean.pa_url_prefix_rewriter,listaTipiProtocollo,strutsBean.protocollo,
						isSupportatoAutenticazioneSoggetti,strutsBean.utenteSoggetto,strutsBean.passwordSoggetto,strutsBean.subjectSoggetto,strutsBean.principalSoggetto,strutsBean.tipoauthSoggetto,
						isPddEsterna,null,strutsBean.dominio,tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword, listaAliasEstrattiCertificato, 
						tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
						tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
						tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter, 
						tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuerSoggetto,tipoCredenzialiSSLWizardStep,
						changepwd,
						multipleApiKey, appId, apiKey, 
						visualizzaModificaCertificato, visualizzaAddCertificato, servletCredenzialiList, parametersServletCredenzialiList, numeroCertificati, servletCredenzialiAdd, soggettoRegistry.sizeProprietaList());

				// aggiunta campi custom
				dati = soggettiHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.CHANGE());

			}

			// Modifico i dati del soggetto nel db

			// Se portadom = "", lo imposto
			// se identificativo porta e' di default allora aggiorno il nome
			String identificativoPortaCalcolato = null;
			String identificativoPortaAttuale = strutsBean.portadom;
			if(!soggettiCore.isRegistroServiziLocale()){
				identificativoPortaAttuale = soggettoConfig.getIdentificativoPorta();
			}
			idSoggetto = new IDSoggetto(strutsBean.tipoprov,strutsBean.nomeprov);
			if(strutsBean.portadom!=null && !strutsBean.portadom.equals("")){

				IDSoggetto oldSoggetto = new IDSoggetto(oldtipoprov,oldnomeprov);
				String oldIdentificativoPorta = soggettiCore.getIdentificativoPortaDefault(strutsBean.protocollo, oldSoggetto);
				if (oldIdentificativoPorta.equals(strutsBean.portadom)) {
					// gli identificativi porta sono rimasti invariati
					// setto l identificativo porta di default (in caso sia
					// cambiato il nome)
					identificativoPortaCalcolato = soggettiCore.getIdentificativoPortaDefault(strutsBean.protocollo, idSoggetto);
				} else {					
					// in questo caso ho cambiato l'identificativo porta
					// e il valore inserito nel campo va inserito
					identificativoPortaCalcolato = identificativoPortaAttuale;
				}
			}else{
				identificativoPortaCalcolato = soggettiCore.getIdentificativoPortaDefault(strutsBean.protocollo, idSoggetto);
			}

			boolean secret = false;
			String secretPassword  = null;
			String secretUser = null;
			boolean secretAppId = false;
			
			if(soggettiCore.isRegistroServiziLocale()){

				soggettoRegistry.setIdentificativoPorta(identificativoPortaCalcolato);
				soggettoRegistry.setNome(strutsBean.nomeprov);
				soggettoRegistry.setTipo(strutsBean.tipoprov);
				if(oldtipoprov!=null && oldnomeprov!=null){
					soggettoRegistry.setOldIDSoggettoForUpdate(new IDSoggetto(oldtipoprov, oldnomeprov));
				}
				soggettoRegistry.setDescrizione(strutsBean.descr);
				soggettoRegistry.setVersioneProtocollo(strutsBean.versioneProtocollo);
				soggettoRegistry.setPrivato(strutsBean.privato);
				soggettoRegistry.setPortaDominio(strutsBean.pdd);

				if(isSupportatoAutenticazioneSoggetti){
					if(strutsBean.tipoauthSoggetto!=null && !"".equals(strutsBean.tipoauthSoggetto) && !ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA.equals(strutsBean.tipoauthSoggetto)){
						CredenzialiSoggetto credenziali = new CredenzialiSoggetto();
						credenziali.setTipo(CredenzialeTipo.toEnumConstant(strutsBean.tipoauthSoggetto));
						credenziali.setUser(strutsBean.utenteSoggetto);
						if(strutsBean.principalSoggetto!=null && !"".equals(strutsBean.principalSoggetto)){
							credenziali.setUser(strutsBean.principalSoggetto); // al posto di user
						}
						credenziali.setPassword(strutsBean.passwordSoggetto);
						
						ApiKey apiKeyGenerated = null;
						
						if(strutsBean.tipoauthSoggetto!=null && ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC.equals(strutsBean.tipoauthSoggetto)){
							if(ServletUtils.isCheckBoxEnabled(changepwd) || modificataTipoAutenticazione) {
								credenziali.setCertificateStrictVerification(false); // se è abilitata la cifratura, verrà impostata a true nel perform update
								if(soggettiCore.isSoggettiPasswordEncryptEnabled()) {
									secret = true;
								}
							}
							else if(encryptOldPlainPwd) {
								secret = true;
							}
							else {
								credenziali.setCertificateStrictVerification(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi));
							}
						}	
						else if(strutsBean.tipoauthSoggetto!=null && ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(strutsBean.tipoauthSoggetto)){
							credenziali.setAppId(multipleApiKeysEnabled);
							credenziali.setUser(appId);
							credenziali.setPassword(apiKey);
							if(ServletUtils.isCheckBoxEnabled(changepwd) || modificataTipoAutenticazione) {
								credenziali.setCertificateStrictVerification(false); // se è abilitata la cifratura, verrà impostata a true nel perform update
								if(multipleApiKeysEnabled) {
									apiKeyGenerated = soggettiCore.newMultipleApiKey();
									if(!appIdModificabile) {
										appId = soggettiCore.toAppId(strutsBean.protocollo, idSoggetto, multipleApiKeysEnabled);
									}
								}
								else {
									appId = soggettiCore.toAppId(strutsBean.protocollo, idSoggetto, multipleApiKeysEnabled);
									apiKeyGenerated = soggettiCore.newApiKey(strutsBean.protocollo, idSoggetto);
								}
								credenziali.setUser(appId);
								credenziali.setPassword(apiKeyGenerated.getPassword());
								if(soggettiCore.isSoggettiPasswordEncryptEnabled()) {
									secret = true;
								}
							}
							else {
								credenziali.setCertificateStrictVerification(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi));
							}
						}
						else if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL.equals(strutsBean.tipoauthSoggetto)) {
							if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO)) {
								Certificate cSelezionato = null;
								
								// il certificato non e' cambiato 
								if(tipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD)) {
									cSelezionato = ArchiveLoader.load(oldCredenziali.getCertificate());
								} else {
									if(tipoCredenzialiSSLTipoArchivio.equals(ArchiveType.CER)) {
										cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLFileCertificato.getValue());
									}else {
										cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato.getValue(), tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLFileCertificatoPassword);
									}
								}
								
								credenziali.setCnIssuer(cSelezionato.getCertificate().getIssuer().getCN());
								credenziali.setCnSubject(cSelezionato.getCertificate().getSubject().getCN()); 
								credenziali.setCertificate(cSelezionato.getCertificate().getCertificate().getEncoded());
								credenziali.setCertificateStrictVerification(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi));
							} else { // configurazione manuale
								credenziali.setSubject(strutsBean.subjectSoggetto);
								if(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLConfigurazioneManualeSelfSigned)) {
									credenziali.setIssuer(strutsBean.subjectSoggetto);
								} else {
									credenziali.setIssuer(issuerSoggetto);
								}
							}
						}
						else if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL.equals(strutsBean.tipoauthSoggetto)) {
							credenziali.setUser(strutsBean.principalSoggetto);
						} 
						
						if(secret) {
							secretUser = credenziali.getUser();
							if (apiKeyGenerated!=null) {
								secretPassword = apiKeyGenerated.getApiKey();
							}
							else {
								secretPassword = credenziali.getPassword();
							}
							secretAppId = credenziali.isAppId();
						}
						
						if (strutsBean.tipoauthSoggetto!=null && strutsBean.tipoauthSoggetto.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)) {
							if(soggettoRegistry.sizeCredenzialiList()>0) {
								soggettoRegistry.removeCredenziali(0);
							}
						}
						else {
							if(soggettoRegistry.getCredenzialiList()!=null) {
								soggettoRegistry.getCredenzialiList().clear();
							}
						}
						
						if(soggettoRegistry.getCredenzialiList()==null) {
							soggettoRegistry.setCredenzialiList(new ArrayList<>());
						}
						if(soggettoRegistry.getCredenzialiList().isEmpty()) {
							soggettoRegistry.getCredenzialiList().add(credenziali);
						}
						else {
							soggettoRegistry.getCredenzialiList().add(0,credenziali); 
						}
					}
					else{
						soggettoRegistry.getCredenzialiList().clear();
					}
				}
				
			}

			if(soggettiCore.isRegistroServiziLocale()){
				if ((strutsBean.codiceIpa != null && !"".equals(strutsBean.codiceIpa))) {
					String oldCodiceIpa = soggettiCore.getCodiceIPADefault(strutsBean.protocollo, new IDSoggetto(oldtipoprov,oldnomeprov), false);
					if (oldCodiceIpa.equals(strutsBean.codiceIpa)) {
						// il codice ipa e' rimasto invariato
						// setto il codice ipa di default (in caso sia cambiato il nome)
						soggettoRegistry.setCodiceIpa(soggettiCore.getCodiceIPADefault(strutsBean.protocollo, new IDSoggetto(strutsBean.tipoprov,strutsBean.nomeprov), false));
					} else {
						// in questo caso ho cambiato il codice ipa e il valore inserito nel campo va inserito
						soggettoRegistry.setCodiceIpa(strutsBean.codiceIpa);
					}
				} else {
					strutsBean.codiceIpa = soggettiCore.getCodiceIPADefault(strutsBean.protocollo, new IDSoggetto(strutsBean.tipoprov,strutsBean.nomeprov), false);
					soggettoRegistry.setCodiceIpa(strutsBean.codiceIpa);
				}
			}
		
			if(!pddCore.isGestionePddAbilitata(soggettiHelper)){
				if(SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE.equals(strutsBean.dominio)) {
					strutsBean.pdd = nomePddGestioneLocale;
				}
				else {
					strutsBean.pdd = null;
				}
			}
			
			if(soggettiCore.isRegistroServiziLocale()){
				if(soggettiCore.isSinglePdD()){
					if (strutsBean.pdd==null || strutsBean.pdd.equals("-"))
						soggettoRegistry.setPortaDominio(null);
					else
						soggettoRegistry.setPortaDominio(strutsBean.pdd);
				}else{
					soggettoRegistry.setPortaDominio(strutsBean.pdd);
				}
			}

			if(oldtipoprov!=null && oldnomeprov!=null){
				soggettoConfig.setOldIDSoggettoForUpdate(new IDSoggetto(oldtipoprov, oldnomeprov));
			}
			soggettoConfig.setDescrizione(strutsBean.descr);
			soggettoConfig.setTipo(strutsBean.tipoprov);
			soggettoConfig.setNome(strutsBean.nomeprov);
			soggettoConfig.setDescrizione(strutsBean.descr);
			soggettoConfig.setIdentificativoPorta(identificativoPortaCalcolato);
			soggettoConfig.setRouter(strutsBean.isRouter);
			soggettoConfig.setPdUrlPrefixRewriter(strutsBean.pd_url_prefix_rewriter);
			soggettoConfig.setPaUrlPrefixRewriter(strutsBean.pa_url_prefix_rewriter);

			//imposto properties custom
			soggettoRegistry.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(strutsBean.protocolProperties, strutsBean.consoleOperationType, oldProtocolPropertyList)); 

			SoggettoCtrlStat sog = new SoggettoCtrlStat(soggettoRegistry, soggettoConfig);
			sog.setOldNomeForUpdate(oldnomeprov);
			sog.setOldTipoForUpdate(oldtipoprov);

			// eseguo l'aggiornamento
			List<Object> listOggettiDaAggiornare = SoggettiUtilities.getOggettiDaAggiornare(soggettiCore, oldnomeprov, strutsBean.nomeprov, oldtipoprov, strutsBean.tipoprov, sog);
			soggettiCore.performUpdateOperation(userLogin, soggettiHelper.smista(), listOggettiDaAggiornare.toArray());

			// sistemo utenze dopo l'aggiornamento
			// se la pdd è diventata esterna o se sono cambiati i dati identificativi
			IDSoggetto idSoggettoSelezionato = new IDSoggetto(oldtipoprov, oldnomeprov);
			if(oldpdd!=null && !oldpdd.equals(strutsBean.pdd) && pddCore.isPddEsterna(strutsBean.pdd)) {
				utentiCore.modificaSoggettoUtilizzatoConsole(idSoggettoSelezionato.toString(), null); // annullo selezione
			}
			else if(!strutsBean.tipoprov.equals(oldtipoprov) || !strutsBean.nomeprov.equals(oldnomeprov)) {
				IDSoggetto idNuovoSoggettoSelezionato = new IDSoggetto(strutsBean.tipoprov, strutsBean.nomeprov);
				utentiCore.modificaSoggettoUtilizzatoConsole(idSoggettoSelezionato.toString(), idNuovoSoggettoSelezionato.toString()); // modifico i dati
			}
			
			soggettiHelper.deleteBinaryParameters(tipoCredenzialiSSLFileCertificato); 
			
			// preparo lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
						
			if (  (oldnomeprov!=null && !oldnomeprov.equals(strutsBean.nomeprov)) 
					|| 
				  (oldtipoprov!=null && !oldtipoprov.equals(strutsBean.tipoprov))
				 ) {
				ServletUtils.removeRisultatiRicercaFromSession(request, session, Liste.SOGGETTI);
				
				User user = ServletUtils.getUserFromSession(request, session);
				String oldSog = oldtipoprov+"/"+oldnomeprov;
				if(user!=null && oldSog.equals(user.getSoggettoSelezionatoPddConsole())) {
					user.setSoggettoSelezionatoPddConsole(strutsBean.tipoprov+"/"+strutsBean.nomeprov);
					ricerca.clearFilter(Liste.SERVIZI, Filtri.FILTRO_SOGGETTO); // re-inizializzo per far vedere il nuovo nome
				}
			}
			
			// Messaggio 'Please Copy'
			if(secret) {
				soggettiHelper.setSecretPleaseCopy(secretPassword, secretUser, secretAppId, strutsBean.tipoauthSoggetto, OggettoDialogEnum.SOGGETTO, sog.getNome());
			}
			
			if(strutsBean.modificaOperativo!=null && !"".equals(strutsBean.modificaOperativo)) {
				
				List<DataElement> dati = new ArrayList<>();
				
				pd.setDati(dati);
				
				pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SOGGETTO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

				pd.disableEditMode();
				
				// sempre, anche quando passo da operativo ad esterno
				generalHelper = new GeneralHelper(session);
				gd = generalHelper.initGeneralData(request); // re-inizializzo per ricalcolare il menu in alto a destra
				
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeFinished(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_GENERALE,
						ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_GENERALE);
				
			}
			else {
			
				if(soggettiCore.isRegistroServiziLocale()){
					List<Soggetto> listaSoggetti = null;
					if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
						listaSoggetti = soggettiCore.soggettiRegistroList(null, ricerca);
					}else{
						listaSoggetti = soggettiCore.soggettiRegistroList(userLogin, ricerca);
					}
					soggettiHelper.prepareSoggettiList(listaSoggetti, ricerca);
				}
				else{
					List<org.openspcoop2.core.config.Soggetto> listaSoggetti = null;
					if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
						listaSoggetti = soggettiCore.soggettiList(null, ricerca);
					}else{
						listaSoggetti = soggettiCore.soggettiList(userLogin, ricerca);
					}
					soggettiHelper.prepareSoggettiConfigList(listaSoggetti, ricerca);
				}

				// sempre, anche quando passo da operativo ad esterno
				generalHelper = new GeneralHelper(session);
				gd = generalHelper.initGeneralData(request); // re-inizializzo per ricalcolare il menu in alto a destra
				
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeFinished(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.CHANGE());
				
			}

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.CHANGE());
		} 
	}
}
