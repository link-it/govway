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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.pdd.core.autenticazione.ApiKey;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
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
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * soggettiAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class SoggettiAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		SoggettiAddStrutsBean strutsBean = new SoggettiAddStrutsBean();
		
		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		strutsBean.consoleOperationType = ConsoleOperationType.ADD;

		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.ADD; 

		try {
			SoggettiHelper soggettiHelper = new SoggettiHelper(request, pd, session);
			
			// Preparo il menu
			soggettiHelper.makeMenu();
			
			strutsBean.protocollo = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO);
			strutsBean.nomeprov = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME);
			strutsBean.tipoprov = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO);
			strutsBean.tipologia = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPOLOGIA);
			strutsBean.portadom = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_PORTA);
			strutsBean.descr = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DESCRIZIONE);
			strutsBean.versioneProtocollo = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_VERSIONE_PROTOCOLLO);
			strutsBean.pdd = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PDD);
			String isRouterParameter = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_ROUTER);
			String isPrivatoParameter = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_PRIVATO);
			strutsBean.privato = ServletUtils.isCheckBoxEnabled(isPrivatoParameter);
			strutsBean.codiceIpa = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_IPA);
			strutsBean.pdUrlPrefixRewriter = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER);
			strutsBean.paUrlPrefixRewriter = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER);
			strutsBean.isRouter = ServletUtils.isCheckBoxEnabled(isRouterParameter);
			strutsBean.dominio = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO);

			String userLogin = ServletUtils.getUserLoginFromSession(session);
			strutsBean.singlePdD = ServletUtils.getObjectFromSession(request, session, Boolean.class, CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);

			strutsBean.editMode = soggettiHelper.getParametroEditMode(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);

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
			if (
					(tipoCredenzialiSSLVerificaTuttiICampi == null || StringUtils.isEmpty(tipoCredenzialiSSLVerificaTuttiICampi))
					&&
					( (soggettiHelper.isEditModeInProgress() && soggettiHelper.getPostBackElementName()==null) 
						||
						 SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO.equalsIgnoreCase(soggettiHelper.getPostBackElementName())
						 ||
						 SoggettiCostanti.PARAMETRO_SOGGETTO_TIPOLOGIA.equalsIgnoreCase(soggettiHelper.getPostBackElementName())
					)
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
			String servletCredenzialiList = null;
			List<Parameter> parametersServletCredenzialiList = null;
			Integer numeroCertificati = 0;
			String servletCredenzialiAdd = null;
			
			boolean isRouter = ServletUtils.isCheckBoxEnabled(isRouterParameter);

			// Prendo la lista di pdd e la metto in un array
			String[] pddList = null;
			String[] pddEsterneList = null;
			List<String> tipiSoggetti = null;
			String nomePddGestioneLocale = null;
			List<String> versioniProtocollo = null;

			SoggettiCore soggettiCore = new SoggettiCore();
			PddCore pddCore = new PddCore(soggettiCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(soggettiCore);

			// Tipi protocollo supportati
			List<String> listaTipiProtocollo = soggettiCore.getProtocolli(request, session);
			// primo accesso
			if(strutsBean.protocollo == null){
				strutsBean.protocollo = soggettiCore.getProtocolloDefault(request, session, null);
			}

			if(soggettiCore.isRegistroServiziLocale()){
				List<PdDControlStation> lista = new ArrayList<>();

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
				List<String> pddEsterne = new ArrayList<>();
				pddEsterne.add("-");
				for (PdDControlStation pddTmp : lista) {
					pddList[i] = pddTmp.getNome();
					i++;
					if(strutsBean.singlePdD!=null && strutsBean.singlePdD.booleanValue() && (nomePddGestioneLocale==null) && (PddTipologia.OPERATIVO.toString().equals(pddTmp.getTipo())) ){
						nomePddGestioneLocale = pddTmp.getNome();
					}
					if(strutsBean.singlePdD!=null && strutsBean.singlePdD.booleanValue() && PddTipologia.ESTERNO.toString().equals(pddTmp.getTipo())){
						pddEsterne.add(pddTmp.getNome());
					}
				}
				pddEsterneList = pddEsterne.toArray(new String[1]);

				// Gestione pdd
				if(!soggettiCore.isGestionePddAbilitata(soggettiHelper)) {

					if(nomePddGestioneLocale==null) {
						throw new ControlStationCoreException("Non è stata rilevata una pdd di tipologia 'operativo'");
					}

					if(SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE.equals(strutsBean.dominio)) {
						strutsBean.pdd = nomePddGestioneLocale;
					}
					else {
						strutsBean.pdd = null;
					}
				}
			}

			//Carico la lista dei tipi di soggetti gestiti dal protocollo
			tipiSoggetti = soggettiCore.getTipiSoggettiGestitiProtocollo(strutsBean.protocollo);

			// lista tipi
			//			tipiSoggetti = soggettiCore.getTipiSoggettiGestiti(); // all tipi soggetti gestiti
			if(strutsBean.tipoprov==null){
				strutsBean.tipoprov = soggettiCore.getTipoSoggettoDefaultProtocollo(strutsBean.protocollo);
			}

			String postBackElementName = soggettiHelper.getPostBackElementName();
			String labelButtonSalva = Costanti.LABEL_MONITOR_BUTTON_INVIA;

			// Controllo se ho modificato il protocollo, ricalcolo il default della versione del protocollo
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO)){
					strutsBean.tipoauthSoggetto = null;
					strutsBean.tipologia = SoggettiCostanti.SOGGETTO_RUOLO_EROGATORE;
				}
				
				if(postBackElementName.equalsIgnoreCase(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO)){
					strutsBean.versioneProtocollo = null;
					// cancello file temporanei
					soggettiHelper.deleteProtocolPropertiesBinaryParameters();
				}  
				
				if(postBackElementName.equalsIgnoreCase(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPOLOGIA)){
					strutsBean.tipoauthSoggetto = null;
				}  

				// tipo autenticazione
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE)){
					if(strutsBean.tipoauthSoggetto!=null && strutsBean.tipoauthSoggetto.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)) {
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
					} else {
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD;
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
				
				// apikey
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS)) {
					appId = null;
					apiKey = null;
				}
			}

			if(strutsBean.versioneProtocollo == null){
				strutsBean.versioneProtocollo = soggettiCore.getVersioneDefaultProtocollo(strutsBean.protocollo);
			}

			if(soggettiHelper.isModalitaAvanzata()){
				versioniProtocollo = soggettiCore.getVersioniProtocollo(strutsBean.protocollo);
			}else {
				versioniProtocollo = new ArrayList<>();
				versioniProtocollo.add(strutsBean.versioneProtocollo);
			}
			boolean isSupportatoCodiceIPA = soggettiCore.isSupportatoCodiceIPA(strutsBean.protocollo); 
			boolean isSupportatoIdentificativoPorta = soggettiCore.isSupportatoIdentificativoPorta(strutsBean.protocollo);

			boolean isSupportatoAutenticazioneSoggetti = soggettiCore.isSupportatoAutenticazioneSoggetti(strutsBean.protocollo);
			boolean isPddEsterna = pddCore.isPddEsterna(strutsBean.pdd);
			if(isSupportatoAutenticazioneSoggetti){
				if(isPddEsterna){

					if(strutsBean.tipologia==null || "".equals(strutsBean.tipologia)){
						strutsBean.tipologia = SoggettiCostanti.SOGGETTO_RUOLO_EROGATORE;
					}

					if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA.equals(strutsBean.tipoauthSoggetto) && 
							!saCore.isSupportatoAutenticazioneApplicativiEsterniErogazione(strutsBean.protocollo)){
						strutsBean.tipoauthSoggetto = null;
					}
				}
				if (strutsBean.tipoauthSoggetto == null) {
					if(isPddEsterna){

						if(SoggettiCostanti.SOGGETTO_RUOLO_FRUITORE.equals(strutsBean.tipologia) || SoggettiCostanti.SOGGETTO_RUOLO_ENTRAMBI.equals(strutsBean.tipologia)){
							strutsBean.tipoauthSoggetto = soggettiCore.getAutenticazione_generazioneAutomaticaPorteApplicative();
						}else{
							strutsBean.tipoauthSoggetto = ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA;
						}						
					}
					else{
						strutsBean.tipoauthSoggetto = ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA;
					}
				}
			}

			boolean checkWizard = false;
			if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL.equals(strutsBean.tipoauthSoggetto)) {
				if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO)) {
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
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO;
					}
				}
				
				if(StringUtils.isNotEmpty(tipoCredenzialiSSLWizardStep) && ( tipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO)
						||tipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_PASSWORD_NON_PRESENTE) 
						|| tipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO)
						)) {
					if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO)) {
						labelButtonSalva = ConnettoriCostanti.LABEL_BUTTON_INVIA_CARICA_CERTIFICATO;
					}
				}else { 
					labelButtonSalva = Costanti.LABEL_MONITOR_BUTTON_INVIA;
				}
			} else {
				labelButtonSalva = Costanti.LABEL_MONITOR_BUTTON_INVIA;
			}
			
			if(labelButtonSalva!= null) {
				pd.setLabelBottoneInvia(labelButtonSalva);
			}

			IDSoggetto idSoggetto = new IDSoggetto(strutsBean.tipoprov,strutsBean.nomeprov);
			strutsBean.protocolFactory  = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(strutsBean.protocollo); 
			strutsBean.consoleDynamicConfiguration =  strutsBean.protocolFactory.createDynamicConfigurationConsole();
			strutsBean.registryReader = soggettiCore.getRegistryReader(strutsBean.protocolFactory); 
			strutsBean.configRegistryReader = soggettiCore.getConfigIntegrationReader(strutsBean.protocolFactory);
			strutsBean.consoleConfiguration = strutsBean.consoleDynamicConfiguration.getDynamicConfigSoggetto(strutsBean.consoleOperationType, soggettiHelper, 
					strutsBean.registryReader, strutsBean.configRegistryReader, idSoggetto);
			strutsBean.protocolProperties = soggettiHelper.estraiProtocolPropertiesDaRequest(strutsBean.consoleConfiguration, strutsBean.consoleOperationType);

			boolean multipleApiKeysEnabled = false;
			boolean appIdModificabile = ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID_MODIFICABILE;
			if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(strutsBean.tipoauthSoggetto)) {
				multipleApiKeysEnabled = ServletUtils.isCheckBoxEnabled(multipleApiKey);
				if(appIdModificabile && multipleApiKeysEnabled) {
					boolean soggettoDefined =
							idSoggetto!=null && 
							idSoggetto.getTipo()!=null && !"".equals(idSoggetto.getTipo()) && 
							idSoggetto.getNome()!=null && !"".equals(idSoggetto.getNome());
					if(appId==null || "".equals(appId)) {
						if(soggettoDefined) {
							appId = soggettiCore.toAppId(strutsBean.protocollo, idSoggetto, multipleApiKeysEnabled);
						}
					}
				}
			}
			
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento dati
			
			if(ServletUtils.isEditModeInProgress(strutsBean.editMode) || checkWizard){

				// setto la barra del titolo
				ServletUtils.setPageDataTitleServletAdd(pd, SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				if(strutsBean.nomeprov==null){
					strutsBean.nomeprov = "";
					idSoggetto = new IDSoggetto(strutsBean.tipoprov,strutsBean.nomeprov);
				}
				if(strutsBean.portadom==null){
					strutsBean.portadom = "";
				}
				if(strutsBean.descr==null){
					strutsBean.descr = "";
				}

				// update della configurazione 
				strutsBean.consoleDynamicConfiguration.updateDynamicConfigSoggetto(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, soggettiHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idSoggetto); 
				dati = soggettiHelper.addSoggettiToDati(TipoOperazione.ADD,dati, strutsBean.nomeprov, strutsBean.tipoprov, strutsBean.portadom, strutsBean.descr, 
						isRouter, tipiSoggetti, strutsBean.versioneProtocollo, strutsBean.privato,strutsBean.codiceIpa,versioniProtocollo,
						isSupportatoCodiceIPA, isSupportatoIdentificativoPorta,
						pddList,pddEsterneList,nomePddGestioneLocale, strutsBean.pdd, 
						listaTipiProtocollo, strutsBean.protocollo ,
						isSupportatoAutenticazioneSoggetti,strutsBean.utenteSoggetto,strutsBean.passwordSoggetto,strutsBean.subjectSoggetto,strutsBean.principalSoggetto,strutsBean.tipoauthSoggetto,
						isPddEsterna,strutsBean.tipologia,strutsBean.dominio,
						tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword, listaAliasEstrattiCertificato, 
						tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
						tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
						tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter, 
						tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuerSoggetto,tipoCredenzialiSSLWizardStep,
						changepwd,
						multipleApiKey, appId, apiKey, 
						visualizzaModificaCertificato, visualizzaAddCertificato, servletCredenzialiList, parametersServletCredenzialiList, numeroCertificati, servletCredenzialiAdd);

				// aggiunta campi custom
				dati = soggettiHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = soggettiHelper.soggettiCheckData(tipoOp, null, strutsBean.tipoprov, strutsBean.nomeprov, strutsBean.codiceIpa, strutsBean.pdUrlPrefixRewriter, strutsBean.paUrlPrefixRewriter,
					null, false, strutsBean.descr);

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
				ServletUtils.setPageDataTitleServletAdd(pd, SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				strutsBean.consoleDynamicConfiguration.updateDynamicConfigSoggetto(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, soggettiHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idSoggetto); 

				dati = soggettiHelper.addSoggettiToDati(TipoOperazione.ADD,dati, strutsBean.nomeprov, strutsBean.tipoprov, strutsBean.portadom, strutsBean.descr, 
						isRouter, tipiSoggetti, strutsBean.versioneProtocollo, strutsBean.privato,strutsBean.codiceIpa,versioniProtocollo,
						isSupportatoCodiceIPA, isSupportatoIdentificativoPorta,
						pddList,pddEsterneList,nomePddGestioneLocale, strutsBean.pdd,  
						listaTipiProtocollo, strutsBean.protocollo,
						isSupportatoAutenticazioneSoggetti,strutsBean.utenteSoggetto,strutsBean.passwordSoggetto,strutsBean.subjectSoggetto,strutsBean.principalSoggetto,strutsBean.tipoauthSoggetto,
						isPddEsterna,strutsBean.tipologia,strutsBean.dominio,
						tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword, listaAliasEstrattiCertificato, 
						tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
						tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
						tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter, 
						tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuerSoggetto,tipoCredenzialiSSLWizardStep,
						changepwd,
						multipleApiKey, appId, apiKey, 
						visualizzaModificaCertificato, visualizzaAddCertificato, servletCredenzialiList, parametersServletCredenzialiList, numeroCertificati, servletCredenzialiAdd);

				// aggiunta campi custom
				dati = soggettiHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.ADD());
			}

			// Inserisco il soggetto nel db
			if (strutsBean.codiceIpa==null || strutsBean.codiceIpa.equals("")) {
				strutsBean.codiceIpa = soggettiCore.getCodiceIPADefault(strutsBean.protocollo, idSoggetto, false);
			}

			if (strutsBean.portadom==null || strutsBean.portadom.equals("")) {
				strutsBean.portadom=soggettiCore.getIdentificativoPortaDefault(strutsBean.protocollo, idSoggetto);
			}

			boolean secret = false;
			String secretPassword  = null;
			String secretUser = null;
			boolean secretAppId = false;
			
			// utilizzo il soggetto del registro che e' un
			// sovrainsieme di quello del config
			Soggetto soggettoRegistro = null;
			if(soggettiCore.isRegistroServiziLocale()){
				soggettoRegistro = new Soggetto();
			}
			org.openspcoop2.core.config.Soggetto soggettoConfig = new org.openspcoop2.core.config.Soggetto();

			// imposto soggettoRegistro
			if(soggettiCore.isRegistroServiziLocale()){
				soggettoRegistro.setNome(strutsBean.nomeprov);
				soggettoRegistro.setTipo(strutsBean.tipoprov);
				soggettoRegistro.setDescrizione(strutsBean.descr);
				soggettoRegistro.setVersioneProtocollo(strutsBean.versioneProtocollo);
				soggettoRegistro.setIdentificativoPorta(strutsBean.portadom);
				soggettoRegistro.setCodiceIpa(strutsBean.codiceIpa);

				if(!pddCore.isGestionePddAbilitata(soggettiHelper)){
					if(SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE.equals(strutsBean.dominio)) {
						strutsBean.pdd = nomePddGestioneLocale;
					}
					else {
						strutsBean.pdd = null;
					}
				}

				if(soggettiCore.isSinglePdD()){
					if (strutsBean.pdd==null || strutsBean.pdd.equals("-"))
						soggettoRegistro.setPortaDominio(null);
					else
						soggettoRegistro.setPortaDominio(strutsBean.pdd);
				}else{
					soggettoRegistro.setPortaDominio(strutsBean.pdd);
				}
				soggettoRegistro.setSuperUser(userLogin);
				soggettoRegistro.setPrivato(strutsBean.privato);

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
						if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(strutsBean.tipoauthSoggetto)) {
							credenziali.setAppId(multipleApiKeysEnabled);
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
						}
						else {
							credenziali.setAppId(false);
						}
						
						if(soggettiCore.isSoggettiPasswordEncryptEnabled()) {
							if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC.equals(strutsBean.tipoauthSoggetto) || ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(strutsBean.tipoauthSoggetto)) {
								secret = true;
								secretUser = credenziali.getUser();
								if (apiKeyGenerated!=null) {
									secretPassword = apiKeyGenerated.getApiKey();
								}
								else {
									secretPassword = credenziali.getPassword();
								}
								secretAppId = credenziali.isAppId();
							}
						}
						
						if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL.equals(strutsBean.tipoauthSoggetto)) {
							if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO)) {
								Certificate cSelezionato = null;
								if(tipoCredenzialiSSLTipoArchivio.equals(ArchiveType.CER)) {
									cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLFileCertificato.getValue());
								}else {
									cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato.getValue(), tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLFileCertificatoPassword);
								}

								credenziali.setCertificateStrictVerification(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi)); 
								credenziali.setCnIssuer(cSelezionato.getCertificate().getIssuer().getCN());
								credenziali.setCnSubject(cSelezionato.getCertificate().getSubject().getCN()); 
								credenziali.setCertificate(cSelezionato.getCertificate().getCertificate().getEncoded());
							} else { // configurazione manuale
								credenziali.setSubject(strutsBean.subjectSoggetto);
								if(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLConfigurazioneManualeSelfSigned)) {
									credenziali.setIssuer(strutsBean.subjectSoggetto);
								} else {
									credenziali.setIssuer(issuerSoggetto);
								}
							}
						}

						soggettoRegistro.addCredenziali(credenziali);
					}
					else{
						soggettoRegistro.getCredenzialiList().clear();
					}
				}
			}

			Connettore connettore = null;
			if(soggettiCore.isRegistroServiziLocale()){
				connettore = new Connettore();
				connettore.setTipo(CostantiDB.CONNETTORE_TIPO_DISABILITATO);
			}

			if(soggettiCore.isRegistroServiziLocale()){
				soggettoRegistro.setConnettore(connettore);
			}

			// imposto soggettoConfig
			soggettoConfig.setNome(strutsBean.nomeprov);
			soggettoConfig.setTipo(strutsBean.tipoprov);
			soggettoConfig.setDescrizione(strutsBean.descr);
			soggettoConfig.setIdentificativoPorta(strutsBean.portadom);
			soggettoConfig.setRouter(strutsBean.isRouter);
			soggettoConfig.setSuperUser(userLogin);

			//imposto properties custom
			if(soggettoRegistro!=null) {
				soggettoRegistro.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(strutsBean.protocolProperties, strutsBean.consoleOperationType,null));
			}


			SoggettoCtrlStat sog = new SoggettoCtrlStat(soggettoRegistro, soggettoConfig);
			// eseguo le operazioni
			soggettiCore.performCreateOperation(userLogin, soggettiHelper.smista(), sog);

			// cancello file temporanei
			soggettiHelper.deleteBinaryProtocolPropertiesTmpFiles(strutsBean.protocolProperties);
			soggettiHelper.deleteBinaryParameters(tipoCredenzialiSSLFileCertificato); 

			// Messaggio 'Please Copy'
			if(secret) {
				soggettiHelper.setSecretPleaseCopy(secretPassword, secretUser, secretAppId, strutsBean.tipoauthSoggetto, OggettoDialogEnum.SOGGETTO, sog.getNome());
			}
									
			// recupero la lista dei soggetti
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			if(soggettiCore.isSetSearchAfterAdd()) {
				soggettiCore.setSearchAfterAdd(Liste.SOGGETTI, soggettoConfig.getNome(), request, session, ricerca);
			}
			
			// la lista dei soggetti del registro e' un sovrainsieme
			// di quella di config
			// cioe' ha piu informazioni, ma lo stesso numero di
			// soggetti.
			// quindi posso utilizzare solo quella
			if(soggettiCore.isRegistroServiziLocale()){
				List<Soggetto> listaSoggettiRegistro = null;
				if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
					listaSoggettiRegistro = soggettiCore.soggettiRegistroList(null, ricerca);
				}else{
					listaSoggettiRegistro = soggettiCore.soggettiRegistroList(userLogin, ricerca);
				}

				soggettiHelper.prepareSoggettiList(listaSoggettiRegistro, ricerca);
			}
			else{
				List<org.openspcoop2.core.config.Soggetto> listaSoggettiConfig = null;
				if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
					listaSoggettiConfig = soggettiCore.soggettiList(null, ricerca);
				}else{
					listaSoggettiConfig = soggettiCore.soggettiList(userLogin, ricerca);
				}

				soggettiHelper.prepareSoggettiConfigList(listaSoggettiConfig, ricerca);
			}

			if(!pddCore.isPddEsterna(strutsBean.pdd)) {
				generalHelper = new GeneralHelper(session);
				gd = generalHelper.initGeneralData(request); // re-inizializzo per ricalcolare il menu in alto a destra
			}

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.ADD());
		}
	}
}
