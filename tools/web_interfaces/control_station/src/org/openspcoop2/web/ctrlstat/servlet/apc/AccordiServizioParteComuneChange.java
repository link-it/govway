/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.GruppiAccordo;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.GruppoAccordo;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.FiltroRicercaGruppi;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.gruppi.GruppiCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * accordiChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneChange extends Action {

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

		AccordiServizioParteComuneChangeStrutsBean strutsBean = new AccordiServizioParteComuneChangeStrutsBean();
		
		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		strutsBean.consoleOperationType = ConsoleOperationType.CHANGE;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.CHANGE;
		List<ProtocolProperty> oldProtocolPropertyList = null;

		ApiHelper apcHelper = new ApiHelper(request, pd, session);
		
		// Preparo il menu
		apcHelper.makeMenu();
		
		strutsBean.id = apcHelper.getParametroLong(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
		
		strutsBean.editMode = apcHelper.getParametroEditMode(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
		strutsBean.protocolPropertiesSet = apcHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_SET);
		strutsBean.descr = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_DESCRIZIONE);
		strutsBean.profcoll = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROFILO_COLLABORAZIONE);
		strutsBean.filtrodup = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_FILTRO_DUPLICATI);
		strutsBean.confric = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONFERMA_RICEZIONE);
		strutsBean.idcoll = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_COLLABORAZIONE);
		strutsBean.idRifRichiesta = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID_RIFERIMENTO_RICHIESTA);
		strutsBean.consord = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONSEGNA_ORDINE);
		strutsBean.scadenza = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SCADENZA);
		strutsBean.utilizzo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_UTILIZZO_SENZA_AZIONE);
		strutsBean.utilizzoSenzaAzione = ServletUtils.isCheckBoxEnabled(strutsBean.utilizzo);
		strutsBean.showUtilizzoSenzaAzione = false;
		String oldProfiloCollaborazione = "";
		strutsBean.referente = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_REFERENTE);
		strutsBean.versione = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VERSIONE);
		// patch per version spinner fino a che non si trova un modo piu' elegante
		/**if(ch.core.isBackwardCompatibilityAccordo11()){
			if("0".equals(versione))
				versione = "";
		}*/
		String privatoS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PRIVATO);
		strutsBean.privato = ServletUtils.isCheckBoxEnabled(privatoS);
		String isServizioCompostoS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_IS_SERVIZIO_COMPOSTO);
		strutsBean.isServizioComposto = ServletUtils.isCheckBoxEnabled(isServizioCompostoS);
		strutsBean.accordoCooperazioneId = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ACCORDO_COOPERAZIONE);
		strutsBean.statoPackage = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_STATO_PACKAGE);
		strutsBean.tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
		strutsBean.tipoProtocollo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO);
		strutsBean.actionConfirm = apcHelper.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);
		strutsBean.backToStato = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RIPRISTINA_STATO);
		String serviceBindingS = apcHelper.getParametroServiceBinding(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SERVICE_BINDING);
		strutsBean.serviceBinding = StringUtils.isNotEmpty(serviceBindingS) ? ServiceBinding.valueOf(serviceBindingS) : null;
		String messageProcessorS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_MESSAGE_TYPE);
		strutsBean.messageType = (StringUtils.isNotEmpty(messageProcessorS) && !messageProcessorS.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_DEFAULT)) 
				? MessageType.valueOf(messageProcessorS) : null;
		String formatoSpecificaS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_INTERFACE_TYPE);
		strutsBean.interfaceType = StringUtils.isNotEmpty(formatoSpecificaS) ? InterfaceType.toEnumConstant(formatoSpecificaS) : null;
		
		strutsBean.gruppi = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_GRUPPI);
		strutsBean.canale = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CANALE);
		strutsBean.canaleStato = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CANALE_STATO);

		if("".equals(strutsBean.tipoAccordo))
			strutsBean.tipoAccordo = null;
		if(strutsBean.tipoAccordo!=null){
			if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE.equals(strutsBean.tipoAccordo)){
				strutsBean.isServizioComposto = false;
			}else if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO.equals(strutsBean.tipoAccordo)){
				strutsBean.isServizioComposto = true;
			}
		}

		String tmpValidazioneDocumenti = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);

		String apiGestioneParziale = apcHelper.getParametroApiGestioneParziale(ApiCostanti.PARAMETRO_APC_API_GESTIONE_PARZIALE);
		Boolean isModalitaVistaApiCustom = ServletUtils.getBooleanAttributeFromSession(ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API, session, request, false).getValue();
		
		boolean chiediConferma = true;
		
		boolean gestioneInformazioniGenerali = false;
		boolean gestioneSoggettoReferente = false;
		boolean gestioneDescrizione = false;
		boolean gestioneSpecificaInterfacce = false;
		boolean gestioneInformazioniProtocollo = false;
		boolean gestioneGruppi = false;
		boolean gestioneCanale = false;
		if(isModalitaVistaApiCustom!=null && isModalitaVistaApiCustom) {
			if(ApiCostanti.VALORE_PARAMETRO_APC_API_INFORMAZIONI_GENERALI.equals(apiGestioneParziale)) {
				gestioneInformazioniGenerali = true;
			}
			else if(ApiCostanti.VALORE_PARAMETRO_APC_API_SOGGETTO_REFERENTE.equals(apiGestioneParziale)) {
				gestioneSoggettoReferente = true;
			}
			else if(ApiCostanti.VALORE_PARAMETRO_APC_API_DESCRIZIONE.equals(apiGestioneParziale)) {
				gestioneDescrizione = true;
				chiediConferma = false;
			}
			else if(ApiCostanti.VALORE_PARAMETRO_APC_API_GESTIONE_SPECIFICA_INTERFACCE.equals(apiGestioneParziale)) {
				gestioneSpecificaInterfacce = true;
			}
			else if(ApiCostanti.VALORE_PARAMETRO_APC_API_OPZIONI_AVANZATE.equals(apiGestioneParziale)) {
				gestioneInformazioniProtocollo = true;
			}
			else if(ApiCostanti.VALORE_PARAMETRO_APC_API_GRUPPI.equals(apiGestioneParziale)) {
				gestioneGruppi = true;
			}
			else if(ApiCostanti.VALORE_PARAMETRO_APC_API_CANALE.equals(apiGestioneParziale)) {
				gestioneCanale = true;
			}
			
		}
		
		boolean addPropertiesHidden = false;
		if(gestioneInformazioniGenerali || gestioneSoggettoReferente || gestioneDescrizione || gestioneSpecificaInterfacce || gestioneInformazioniProtocollo || gestioneGruppi|| gestioneCanale) {
			addPropertiesHidden = true;
		}
		
		if(ServletUtils.isEditModeInProgress(strutsBean.editMode) ){

			// primo accesso alla servlet

			if(tmpValidazioneDocumenti!=null){
				strutsBean.validazioneDocumenti = ServletUtils.isCheckBoxEnabled(tmpValidazioneDocumenti);
			}else{
				strutsBean.validazioneDocumenti = true;
			}
		}else{
			strutsBean.validazioneDocumenti = ServletUtils.isCheckBoxEnabled(tmpValidazioneDocumenti);
		}

		// Prendo il nome dell'accordo
		String nome = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME);

		AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
		AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(apcCore);
		SoggettiCore soggettiCore = new SoggettiCore(apcCore);
		PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apcCore);
		AccordiCooperazioneCore acCore = new AccordiCooperazioneCore(apcCore);
		GruppiCore gruppiCore = new GruppiCore(apcCore);
		ConfigurazioneCore confCore = new ConfigurazioneCore(apcCore);
		ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
		FiltroRicercaGruppi filtroRicerca = null;
		List<String> elencoGruppi = null;

		boolean asWithAllegati = false;
		
		// carico i canali
		CanaliConfigurazione gestioneCanali = confCore.getCanaliConfigurazione(false);
		List<CanaleConfigurazione> canaleList = gestioneCanali != null ? gestioneCanali.getCanaleList() : new ArrayList<>();
		boolean gestioneCanaliEnabled = gestioneCanali != null && org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(gestioneCanali.getStato());

		String[] providersList = null;
		String[] providersListLabel = null;
		String[] accordiCooperazioneEsistenti=null;
		String[] accordiCooperazioneEsistentiLabel=null;
		List<String> listaTipiProtocollo = null;

		boolean used = false;

		AccordoServizioParteComune as = null;
		IDAccordo idAccordoOLD = null;

		try {

			long idAccordoLong = Long.parseLong(strutsBean.id);

			as = apcCore.getAccordoServizioFull(idAccordoLong);
			
			if(as==null) {
				throw new DriverControlStationException("AccordoServizioParteComune con id '"+idAccordoLong+"' non trovato");
			}
			
			idAccordoOLD = idAccordoFactory.getIDAccordoFromValues(as.getNome(),BeanUtilities.getSoggettoReferenteID(as.getSoggettoReferente()),as.getVersione());

			asWithAllegati = apcHelper.asWithAllegatiXsd(as);
			
			// controllo se l'accordo e' utilizzato da qualche asps
			List<AccordoServizioParteSpecifica> asps = apsCore.serviziByAccordoFilterList(idAccordoOLD);
			used = asps != null && !asps.isEmpty();

			// lista dei protocolli supportati
			listaTipiProtocollo = apcCore.getProtocolliByFilter(request, session, true, false);

			// primo accesso 
			if(strutsBean.tipoProtocollo == null){
				if(as!=null && as.getSoggettoReferente()!=null){
					strutsBean.tipoProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo());
				}
				else{
					strutsBean.tipoProtocollo = apsCore.getProtocolloDefault(request, session, listaTipiProtocollo);
				}
			}
			strutsBean.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(strutsBean.tipoProtocollo);
			strutsBean.consoleDynamicConfiguration =  strutsBean.protocolFactory.createDynamicConfigurationConsole();
			strutsBean.registryReader = soggettiCore.getRegistryReader(strutsBean.protocolFactory); 
			strutsBean.configRegistryReader = soggettiCore.getConfigIntegrationReader(strutsBean.protocolFactory);
			strutsBean.consoleConfiguration = (strutsBean.tipoAccordo==null || strutsBean.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE)) ? 
					strutsBean.consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteComune(strutsBean.consoleOperationType, apcHelper, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idAccordoOLD)
					: strutsBean.consoleDynamicConfiguration.getDynamicConfigAccordoServizioComposto(strutsBean.consoleOperationType, apcHelper, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idAccordoOLD);

			strutsBean.protocolProperties = apcHelper.estraiProtocolPropertiesDaRequest(strutsBean.consoleConfiguration, strutsBean.consoleOperationType);

			// se this.initProtocolPropertiesFromDb = true allora leggo le properties dal db... 

			try{
				AccordoServizioParteComune apcOLD = strutsBean.registryReader.getAccordoServizioParteComune(idAccordoOLD);
				oldProtocolPropertyList = apcOLD.getProtocolPropertyList(); 

			}catch(RegistryNotFound r){
				// ignore
			}

			if(strutsBean.protocolPropertiesSet == null){
				ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(strutsBean.protocolProperties, oldProtocolPropertyList, strutsBean.consoleOperationType);
			}

			List<String> tipiSoggettiGestitiProtocollo = soggettiCore.getTipiSoggettiGestitiProtocollo(strutsBean.tipoProtocollo);
			List<Soggetto> listaSoggetti=null;

			if(apcCore.isVisioneOggettiGlobale(userLogin)){
				listaSoggetti = soggettiCore.soggettiList(null, new ConsoleSearch(true));
			}else{
				listaSoggetti = soggettiCore.soggettiList(userLogin, new ConsoleSearch(true));
			}

			List<String> soggettiListTmp = new ArrayList<>();
			List<String> soggettiListLabelTmp = new ArrayList<>();
			soggettiListTmp.add("-");
			soggettiListLabelTmp.add("-");

			if (!listaSoggetti.isEmpty()) {
				for (Soggetto soggetto : listaSoggetti) {
					if(tipiSoggettiGestitiProtocollo.contains(soggetto.getTipo())){
						soggettiListTmp.add(soggetto.getId().toString());
						/** soggettiListLabelTmp.add(soggetto.getTipo() + "/" + soggetto.getNome()); */
						soggettiListLabelTmp.add(apcHelper.getLabelNomeSoggetto(strutsBean.tipoProtocollo, soggetto.getTipo() , soggetto.getNome() ));
					}
				}
			}
			providersList = soggettiListTmp.toArray(new String[1]);
			providersListLabel = soggettiListLabelTmp.toArray(new String[1]);

			if(strutsBean.isServizioComposto) {
				List<AccordoCooperazione> lista = null;
				if(apcCore.isVisioneOggettiGlobale(userLogin)){
					lista = acCore.accordiCooperazioneList(null, new ConsoleSearch(true));
				}else{
					lista = acCore.accordiCooperazioneList(userLogin, new ConsoleSearch(true));
				}
				if (lista != null && !lista.isEmpty()) {
					accordiCooperazioneEsistenti = new String[lista.size()+1];
					accordiCooperazioneEsistentiLabel = new String[lista.size()+1];
					int i = 1;
					accordiCooperazioneEsistenti[0]="-";
					accordiCooperazioneEsistentiLabel[0]="-";
					Iterator<AccordoCooperazione> itL = lista.iterator();
					while (itL.hasNext()) {
						AccordoCooperazione singleAC = itL.next();
						accordiCooperazioneEsistenti[i] = "" + singleAC.getId();
						accordiCooperazioneEsistentiLabel[i] = apcHelper.getLabelIdAccordoCooperazione(acCore.getAccordoCooperazione(singleAC.getId())); 
						i++;
					}
				} else {
					accordiCooperazioneEsistenti = new String[1];
					accordiCooperazioneEsistentiLabel = new String[1];
					accordiCooperazioneEsistenti[0]="-";
					accordiCooperazioneEsistentiLabel[0]="-";
				}
			}

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
		}

		//se passo dal link diretto di ripristino stato imposto il nuovo stato
		if(strutsBean.backToStato != null && (strutsBean.actionConfirm == null || strutsBean.actionConfirm.equals(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_OK)))
			strutsBean.statoPackage = strutsBean.backToStato;

		String uriAS = idAccordoFactory.getUriFromIDAccordo(idAccordoOLD);
		String labelASTitle = apcHelper.getLabelIdAccordo(strutsBean.tipoProtocollo, idAccordoOLD); 
		String oldStatoPackage = as.getStatoPackage();			

		Properties propertiesProprietario = new Properties();
		propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, strutsBean.id);
		propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_SERVIZIO_PARTE_COMUNE);
		propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, uriAS);
		propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE, URLEncoder.encode( AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE + "?" + request.getQueryString(), "UTF-8"));
		propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, strutsBean.tipoProtocollo);
		if(strutsBean.tipoAccordo!=null) {
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO, strutsBean.tipoAccordo);
		}
		
		List<Parameter> listaParams = apcHelper.getTitoloApc(tipoOp, as, strutsBean.tipoAccordo, labelASTitle, null, true); 

		// Se idhid = null, devo visualizzare la pagina per la modifica dati
		if(ServletUtils.isEditModeInProgress(strutsBean.editMode)){

			try {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				if(strutsBean.descr==null){
					//inizializzazione default
					strutsBean.descr = as.getDescrizione();

					if(strutsBean.tipoAccordo!=null){
						if("apc".equals(strutsBean.tipoAccordo)){
							strutsBean.isServizioComposto = false;
						}else if("asc".equals(strutsBean.tipoAccordo)){
							strutsBean.isServizioComposto = true;
						}
					}else{
						strutsBean.isServizioComposto = as.getServizioComposto()!=null ? true : false;
					}
					if(strutsBean.isServizioComposto){
						strutsBean.accordoCooperazioneId = ""+as.getServizioComposto().getIdAccordoCooperazione();
					}else{
						strutsBean.accordoCooperazioneId="-";
					}
				}

				int idReferente = -1;
				try{
					idReferente = Integer.parseInt(strutsBean.referente);
				}catch(Exception e){
					// ignore
				}
				if(idReferente<=0 && !"-".equals(strutsBean.referente)){
					IdSoggetto assr = as.getSoggettoReferente();
					if (assr != null) {
						Soggetto s = soggettiCore.getSoggetto(new IDSoggetto(assr.getTipo(),assr.getNome()));
						strutsBean.referente = "" + s.getId();
					}else{
						strutsBean.referente = "-";
					}
				}

				if(strutsBean.versione == null &&
						as.getVersione()!=null) {
					strutsBean.versione = as.getVersione().intValue()+"";
				}

				// controllo profilo collaborazione
				if(strutsBean.profcoll == null)
					strutsBean.profcoll = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(as.getProfiloCollaborazione());

				if(strutsBean.filtrodup == null)
					strutsBean.filtrodup = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati());
				if(strutsBean.confric == null)
					strutsBean.confric = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione());
				if(strutsBean.idcoll == null)
					strutsBean.idcoll = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione());
				if(strutsBean.idRifRichiesta == null)
					strutsBean.idRifRichiesta = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdRiferimentoRichiesta());
				if(strutsBean.consord == null)
					strutsBean.consord = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine());
				if(strutsBean.scadenza == null)
					strutsBean.scadenza = as.getScadenza() != null ? as.getScadenza() : "";
				if(privatoS==null){
					strutsBean.privato = as.getPrivato()!=null && as.getPrivato();
				}

				strutsBean.showUtilizzoSenzaAzione = as.sizeAzioneList() > 0;// se ci
				// sono
				// azioni
				// allora
				// visualizzo
				// il
				// checkbox
				if(strutsBean.utilizzo==null)
					strutsBean.utilizzoSenzaAzione = as.getUtilizzoSenzaAzione();	

				if(strutsBean.statoPackage==null)
					strutsBean.statoPackage = as.getStatoPackage();

				if(strutsBean.wsdlconc == null){
					strutsBean.wsdlconc = new BinaryParameter();
					strutsBean.wsdlconc.setValue(as.getByteWsdlConcettuale());
				}

				if(strutsBean.wsdldef == null){
					strutsBean.wsdldef = new BinaryParameter();
					strutsBean.wsdldef.setValue(as.getByteWsdlDefinitorio());
				}

				if(strutsBean.wsdlserv == null){
					strutsBean.wsdlserv = new BinaryParameter();
					strutsBean.wsdlserv.setValue(as.getByteWsdlLogicoErogatore());
				}

				if(strutsBean.wsdlservcorr == null){
					strutsBean.wsdlservcorr = new BinaryParameter();
					strutsBean.wsdlservcorr.setValue(as.getByteWsdlLogicoFruitore());
				}

				if(strutsBean.wsblconc == null){
					strutsBean.wsblconc = new BinaryParameter();
					strutsBean.wsblconc.setValue(as.getByteSpecificaConversazioneConcettuale());
				}

				if(strutsBean.wsblserv == null){
					strutsBean.wsblserv = new BinaryParameter();
					strutsBean.wsblserv.setValue(as.getByteSpecificaConversazioneErogatore());
				}

				if(strutsBean.wsblservcorr == null){
					strutsBean.wsblservcorr = new BinaryParameter();
					strutsBean.wsblservcorr.setValue(as.getByteSpecificaConversazioneFruitore());
				}

				if(strutsBean.serviceBinding == null){
					strutsBean.serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
				
					if(strutsBean.messageType == null){
						strutsBean.messageType = apcCore.toMessageMessageType(as.getMessageType());
					}
					
					filtroRicerca = new FiltroRicercaGruppi();
					filtroRicerca.setServiceBinding(apcCore.fromMessageServiceBinding(strutsBean.serviceBinding));
					elencoGruppi = gruppiCore.getAllGruppi(filtroRicerca);
				}
				
				if(strutsBean.interfaceType == null)
					strutsBean.interfaceType = apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica());
				
				if(strutsBean.gruppi == null) {
					// leggo i gruppi dall'accordo
					if(as.getGruppi() != null) {
						List<String> nomiGruppi = as.getGruppi().getGruppoList().stream().flatMap(e-> Stream.of(e.getNome())).collect(Collectors.toList());
						strutsBean.gruppi = StringUtils.join(nomiGruppi, ",");
					} else {
						strutsBean.gruppi = "";
					}
				}
				
				if(strutsBean.canale == null) {
					strutsBean.canale = as.getCanale();
				}
				if(strutsBean.canaleStato==null) {
					if(strutsBean.canale == null) {
						strutsBean.canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT;
					} else {
						strutsBean.canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO;
					}
				} 

			} catch (Exception ex) {
				return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), ex, pd, request, session, gd, mapping, 
						AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
			}

			String postBackElementName = apcHelper.getParameter(Costanti.POSTBACK_ELEMENT_NAME);

			// Controllo se ho modificato il protocollo, resetto il referente e il service binding
			if(postBackElementName != null &&
				postBackElementName.equalsIgnoreCase(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SERVICE_BINDING)){
				strutsBean.interfaceType = null;
				strutsBean.messageType = null;
					
				filtroRicerca = new FiltroRicercaGruppi();
				filtroRicerca.setServiceBinding(apcCore.fromMessageServiceBinding(strutsBean.serviceBinding));
				elencoGruppi = gruppiCore.getAllGruppi(filtroRicerca);
			}

			// fromato specifica
			if(strutsBean.interfaceType == null &&
					strutsBean.serviceBinding != null) {
				switch(strutsBean.serviceBinding) {
				case REST:
					strutsBean.interfaceType = InterfaceType.toEnumConstant(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_REST);
					break;
				case SOAP:
				default:
					strutsBean.interfaceType = InterfaceType.toEnumConstant(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_SOAP);
					break;
				}
			}


			if( strutsBean.backToStato == null){
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
 
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				if(strutsBean.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(strutsBean.consoleConfiguration,
							strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idAccordoOLD);
				else  
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(strutsBean.consoleConfiguration,
							strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idAccordoOLD);

				dati = apcHelper.addAccordiToDati(dati, nome, strutsBean.descr, strutsBean.profcoll, strutsBean.wsdldef, strutsBean.wsdlconc, strutsBean.wsdlserv, strutsBean.wsdlservcorr,
						strutsBean.wsblconc,strutsBean.wsblserv,strutsBean.wsblservcorr,
						strutsBean.filtrodup, strutsBean.confric, strutsBean.idcoll, strutsBean.idRifRichiesta, strutsBean.consord, strutsBean.scadenza, strutsBean.id, tipoOp, 
						strutsBean.showUtilizzoSenzaAzione, strutsBean.utilizzoSenzaAzione,strutsBean.referente,strutsBean.versione,providersList,providersListLabel,
						strutsBean.privato,strutsBean.isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
						strutsBean.accordoCooperazioneId,strutsBean.statoPackage,oldStatoPackage, strutsBean.tipoAccordo, strutsBean.validazioneDocumenti,
						strutsBean.tipoProtocollo, listaTipiProtocollo,used,asWithAllegati,strutsBean.protocolFactory,strutsBean.serviceBinding,strutsBean.messageType,strutsBean.interfaceType, strutsBean.gruppi, elencoGruppi,
						false, -1, false, -1,
						false, strutsBean.canaleStato, strutsBean.canale, canaleList, gestioneCanaliEnabled);

				// aggiunta campi custom
				if(addPropertiesHidden) {
					dati = apcHelper.addProtocolPropertiesToDatiAsHidden(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}else {
					dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}
					
				pd.setDati(dati);
				
				if(apcHelper.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(as.getStatoPackage())){
					pd.disableEditMode();
				}

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);


				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
			}
		}

		boolean visibilitaAccordoCooperazione=false;
		if(!"-".equals(strutsBean.accordoCooperazioneId) && "".equals(strutsBean.accordoCooperazioneId)==false  && strutsBean.accordoCooperazioneId!=null){
			AccordoCooperazione ac = acCore.getAccordoCooperazione(Long.parseLong(strutsBean.accordoCooperazioneId));
			visibilitaAccordoCooperazione=ac.getPrivato()!=null && ac.getPrivato();
		}

		// Controlli sui campi immessi
		boolean isOk = apcHelper.accordiCheckData(tipoOp, nome, strutsBean.descr, strutsBean.profcoll, 
				strutsBean.wsdldef, strutsBean.wsdlconc, strutsBean.wsdlserv, strutsBean.wsdlservcorr,  
				strutsBean.filtrodup, strutsBean.confric, strutsBean.idcoll, strutsBean.idRifRichiesta, strutsBean.consord, 
				strutsBean.scadenza, strutsBean.id,strutsBean.referente,strutsBean.versione,strutsBean.accordoCooperazioneId,strutsBean.privato,visibilitaAccordoCooperazione,idAccordoOLD, 
				strutsBean.wsblconc,strutsBean.wsblserv,strutsBean.wsblservcorr, strutsBean.validazioneDocumenti,strutsBean.tipoProtocollo,strutsBean.backToStato,strutsBean.serviceBinding,strutsBean.messageType,strutsBean.interfaceType,
				true, strutsBean.gruppi, strutsBean.canaleStato, strutsBean.canale, gestioneCanaliEnabled);

		// updateDynamic
		if(isOk) {
			if(strutsBean.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
				strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(strutsBean.consoleConfiguration,
						strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idAccordoOLD);
			else 
				strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(strutsBean.consoleConfiguration,
						strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idAccordoOLD);
		}
		
		// Validazione base dei parametri custom 
		if(isOk){
			try{
				apcHelper.validaProtocolProperties(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, strutsBean.protocolProperties);
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
				if(strutsBean.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
					strutsBean.consoleDynamicConfiguration.validateDynamicConfigAccordoServizioParteComune(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idAccordoOLD);
				else 
					strutsBean.consoleDynamicConfiguration.validateDynamicConfigAccordoServizioComposto(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idAccordoOLD);
			}catch(ProtocolException e){
				ControlStationCore.getLog().error(e.getMessage(),e);
				pd.setMessage(e.getMessage());
				isOk = false;
			}
		}

		if (!isOk) {

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, listaParams);

			// preparo i campi
			List<DataElement> dati = new ArrayList<>();

			dati.add(ServletUtils.getDataElementForEditModeFinished());

			// update della configurazione 
			if(strutsBean.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
				strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(strutsBean.consoleConfiguration,
						strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idAccordoOLD);
			else 
				strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(strutsBean.consoleConfiguration,
						strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idAccordoOLD);

			dati = apcHelper.addAccordiToDati(dati, nome, strutsBean.descr, strutsBean.profcoll, strutsBean.wsdldef, strutsBean.wsdlconc, strutsBean.wsdlserv, strutsBean.wsdlservcorr,strutsBean.wsblconc,strutsBean.wsblserv,strutsBean.wsblservcorr, 
					strutsBean.filtrodup, strutsBean.confric, strutsBean.idcoll, strutsBean.idRifRichiesta, strutsBean.consord, strutsBean.scadenza, strutsBean.id, tipoOp, 
					strutsBean.showUtilizzoSenzaAzione, strutsBean.utilizzoSenzaAzione,strutsBean.referente,strutsBean.versione,providersList,providersListLabel,
					strutsBean.privato,strutsBean.isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
					strutsBean.accordoCooperazioneId,strutsBean.statoPackage,oldStatoPackage, strutsBean.tipoAccordo, strutsBean.validazioneDocumenti,
					strutsBean.tipoProtocollo, listaTipiProtocollo,used,asWithAllegati,strutsBean.protocolFactory,strutsBean.serviceBinding,strutsBean.messageType,strutsBean.interfaceType, strutsBean.gruppi, elencoGruppi,
					false, -1, false, -1,
					false, strutsBean.canaleStato, strutsBean.canale, canaleList, gestioneCanaliEnabled);

			// aggiunta campi custom
			if(addPropertiesHidden) {
				dati = apcHelper.addProtocolPropertiesToDatiAsHidden(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
			}else {
				dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
			}
			
			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
		}

		// I dati dell'utente sono validi, lo informo che l'accordo e' utilizzato da asps a meno che non sto modificando solamente la descrizione
		if(chiediConferma && strutsBean.actionConfirm == null){
			if(used || strutsBean.backToStato != null){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeInProgress());

				// update della configurazione 
				if(strutsBean.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(strutsBean.consoleConfiguration,
							strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idAccordoOLD);
				else 
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(strutsBean.consoleConfiguration,
							strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idAccordoOLD);

				dati = apcHelper.addAccordiToDati(dati, nome, strutsBean.descr, strutsBean.profcoll, strutsBean.wsdldef, strutsBean.wsdlconc, strutsBean.wsdlserv, strutsBean.wsdlservcorr,strutsBean.wsblconc,strutsBean.wsblserv,strutsBean.wsblservcorr, 
						strutsBean.filtrodup, strutsBean.confric, strutsBean.idcoll, strutsBean.idRifRichiesta, strutsBean.consord, strutsBean.scadenza, strutsBean.id, tipoOp, 
						strutsBean.showUtilizzoSenzaAzione, strutsBean.utilizzoSenzaAzione,strutsBean.referente,strutsBean.versione,providersList,providersListLabel,
						strutsBean.privato,strutsBean.isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
						strutsBean.accordoCooperazioneId,strutsBean.statoPackage,oldStatoPackage, strutsBean.tipoAccordo, strutsBean.validazioneDocumenti,
						strutsBean.tipoProtocollo, listaTipiProtocollo,used,asWithAllegati,strutsBean.protocolFactory,strutsBean.serviceBinding,strutsBean.messageType,strutsBean.interfaceType, strutsBean.gruppi, elencoGruppi,
						false, -1, false, -1,
						true, strutsBean.canaleStato, strutsBean.canale, canaleList, gestioneCanaliEnabled);

				dati = apcHelper.addAccordiToDatiAsHidden(dati, nome, strutsBean.descr, strutsBean.profcoll, null, null, null, null, 
						strutsBean.filtrodup, strutsBean.confric, strutsBean.idcoll, strutsBean.idRifRichiesta, strutsBean.consord, strutsBean.scadenza, strutsBean.id,						
						tipoOp, strutsBean.showUtilizzoSenzaAzione, strutsBean.utilizzoSenzaAzione, strutsBean.referente,strutsBean.versione, providersList, providersListLabel,
						strutsBean.privato,strutsBean.isServizioComposto, accordiCooperazioneEsistenti, accordiCooperazioneEsistentiLabel,
						strutsBean.accordoCooperazioneId, strutsBean.statoPackage,oldStatoPackage, strutsBean.tipoAccordo, strutsBean.validazioneDocumenti, strutsBean.tipoProtocollo, 
						listaTipiProtocollo, used, strutsBean.serviceBinding,strutsBean.messageType,strutsBean.interfaceType, strutsBean.gruppi, strutsBean.canaleStato, strutsBean.canale);
				
				if(strutsBean.backToStato!= null) {
					// backtostato per chiudere la modifica dopo la conferma
					DataElement de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
					de.setValue(strutsBean.backToStato);
					de.setType(DataElementType.HIDDEN);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RIPRISTINA_STATO);
					dati.add(de);
				}
				
				
				// aggiunta campi custom
				if(addPropertiesHidden) {
					dati = apcHelper.addProtocolPropertiesToDatiAsHidden(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}else {
					dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				
					// aggiunta campi custom come hidden, quelli sopra vengono bruciati dal no-edit
					dati = apcHelper.addProtocolPropertiesToDatiAsHidden(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}
					
				pd.setDati(dati);

				String uriAccordo = idAccordoFactory.getUriFromIDAccordo(idAccordoOLD);
				String msg = ""; 
				if(used)
					msg = "Attenzione, esistono delle erogazioni che riferiscono l''accordo [{0}] che si sta modificando, continuare?";

				if(strutsBean.backToStato != null){
					msg = "&Egrave; stato richiesto di ripristinare lo stato dell''accordo [{0}] in operativo. Tale operazione permetter&agrave; successive modifiche all''accordo. Vuoi procedere?";
				}
				String pre = Costanti.HTML_MODAL_SPAN_PREFIX;
				String post = Costanti.HTML_MODAL_SPAN_SUFFIX;

				pd.setMessage(pre + MessageFormat.format(msg, uriAccordo) + post, Costanti.MESSAGE_TYPE_CONFIRM);

				String[][] bottoni = { 
						{ Costanti.LABEL_MONITOR_BUTTON_ANNULLA, 
							Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_PREFIX +
							Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_SUFFIX

						},
						{ Costanti.LABEL_MONITOR_BUTTON_CONFERMA,
							Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_PREFIX +
							Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_SUFFIX }};

				pd.setBottoni(bottoni );
				
				// disabilito la form
				pd.disableEditMode();

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, 
						AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());	
			}
		}

		oldProfiloCollaborazione = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(as.getProfiloCollaborazione());

		as.setNome(nome);
		as.setDescrizione(strutsBean.descr);
		as.setConfermaRicezione(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(strutsBean.confric)));
		as.setConsegnaInOrdine(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(strutsBean.consord)));
		as.setFiltroDuplicati(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(strutsBean.filtrodup)));
		as.setIdCollaborazione(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(strutsBean.idcoll)));
		as.setIdRiferimentoRichiesta(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(strutsBean.idRifRichiesta)));
		as.setProfiloCollaborazione(ProfiloCollaborazione.toEnumConstant(AccordiServizioParteComuneHelper.convertProfiloCollaborazioneView2DB(strutsBean.profcoll)));
		as.setScadenza(strutsBean.scadenza);
		as.setUtilizzoSenzaAzione(as.sizeAzioneList() > 0 ? strutsBean.utilizzoSenzaAzione : true);
		if(strutsBean.referente!=null && !"".equals(strutsBean.referente) && !"-".equals(strutsBean.referente)){
			int idRef = 0;
			try {
				idRef = Integer.parseInt(strutsBean.referente);
			} catch (Exception e) {
			}
			if (idRef != 0) {
				int idReferente = Integer.parseInt(strutsBean.referente);
				Soggetto s = soggettiCore.getSoggetto(idReferente);			
				IdSoggetto assr = new IdSoggetto();
				assr.setTipo(s.getTipo());
				assr.setNome(s.getNome());
				as.setSoggettoReferente(assr);
			}
		}else{
			as.setSoggettoReferente(null);
		}
		if(strutsBean.versione!=null)
			as.setVersione(Integer.parseInt(strutsBean.versione));
		as.setPrivato(strutsBean.privato ? Boolean.TRUE : Boolean.FALSE);

		if(strutsBean.accordoCooperazioneId!=null && !"".equals(strutsBean.accordoCooperazioneId) && !"-".equals(strutsBean.accordoCooperazioneId)){
			AccordoServizioParteComuneServizioComposto assc = as.getServizioComposto();
			if(assc==null) assc = new AccordoServizioParteComuneServizioComposto();
			assc.setIdAccordoCooperazione(Long.parseLong(strutsBean.accordoCooperazioneId));
			as.setServizioComposto(assc);
		}else{
			as.setServizioComposto(null);
		}

		as.setOldIDAccordoForUpdate(idAccordoOLD);

		// servicebinding / messagetype / formatospecifica
		as.setServiceBinding(apcCore.fromMessageServiceBinding(strutsBean.serviceBinding));
		as.setMessageType(apcCore.fromMessageMessageType(strutsBean.messageType));
		as.setFormatoSpecifica(apcCore.interfaceType2FormatoSpecifica(strutsBean.interfaceType));

		// stato
		as.setStatoPackage(strutsBean.statoPackage);

		// Check stato
		if(apcHelper.isShowGestioneWorkflowStatoDocumenti()){

			try{
				boolean utilizzoAzioniDiretteInAccordoAbilitato = apcCore.isShowAccordiColonnaAzioni();
				apcCore.validaStatoAccordoServizio(as, utilizzoAzioniDiretteInAccordoAbilitato, true);
			}catch(ValidazioneStatoPackageException validazioneException){

				// Setto messaggio di errore
				pd.setMessage(validazioneException.toString());

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				if(strutsBean.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(strutsBean.consoleConfiguration,
							strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idAccordoOLD);
				else 
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(strutsBean.consoleConfiguration,
							strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idAccordoOLD);

				dati = apcHelper.addAccordiToDati(dati, nome, strutsBean.descr, strutsBean.profcoll, strutsBean.wsdldef, strutsBean.wsdlconc, strutsBean.wsdlserv, strutsBean.wsdlservcorr,strutsBean.wsblconc,strutsBean.wsblserv,strutsBean.wsblservcorr, 
						strutsBean.filtrodup, strutsBean.confric, strutsBean.idcoll, strutsBean.idRifRichiesta, strutsBean.consord, strutsBean.scadenza, strutsBean.id, tipoOp, 
						strutsBean.showUtilizzoSenzaAzione, strutsBean.utilizzoSenzaAzione,strutsBean.referente,strutsBean.versione,providersList,providersListLabel,
						strutsBean.privato,strutsBean.isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
						strutsBean.accordoCooperazioneId,strutsBean.statoPackage,oldStatoPackage, strutsBean.tipoAccordo, strutsBean.validazioneDocumenti,
						strutsBean.tipoProtocollo, listaTipiProtocollo,used,asWithAllegati,strutsBean.protocolFactory,strutsBean.serviceBinding,strutsBean.messageType,strutsBean.interfaceType, strutsBean.gruppi, elencoGruppi,
						false, -1, false, -1,
						false, strutsBean.canaleStato, strutsBean.canale, canaleList, gestioneCanaliEnabled);

				// aggiunta campi custom
				if(addPropertiesHidden) {
					dati = apcHelper.addProtocolPropertiesToDatiAsHidden(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}else {
					dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
			}
		}


		//imposto properties custom
		as.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(strutsBean.protocolProperties, strutsBean.consoleOperationType, oldProtocolPropertyList));

		List<Object> objectToCreate = new ArrayList<>();
		
		if(as.getGruppi() == null)
			as.setGruppi(new GruppiAccordo());
		
		// svuto la lista e riporto solo quelli che sono selezionati dall'utente.
		as.getGruppi().getGruppoList().clear();
		
		// gruppi
		if(StringUtils.isNotEmpty(strutsBean.gruppi)) {
			
		
			List<String> nomiGruppi = Arrays.asList(strutsBean.gruppi.split(","));
			
			for (String nomeGruppo : nomiGruppi) {
				if(!gruppiCore.existsGruppo(nomeGruppo)) {
					Gruppo nuovoGruppo = new Gruppo();
					nuovoGruppo.setNome(nomeGruppo);
					nuovoGruppo.setSuperUser(userLogin);
					objectToCreate.add(nuovoGruppo);
				}
				
				GruppoAccordo gruppoAccordo = new GruppoAccordo();
				gruppoAccordo.setNome(nomeGruppo);
				as.getGruppi().addGruppo(gruppoAccordo );
			}
		}
		
		// canale
		if(gestioneCanaliEnabled) {
			if(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO.equals(strutsBean.canaleStato)) {
				as.setCanale(strutsBean.canale);
			} else {
				as.setCanale(null);
			}
		} else {
			as.setCanale(null);
		}

		// Modifico i dati dell'accordo nel db
		try {

			// Controllo su cambio del profilo di collaborazione.
			/*
			 * Nel caso in cui si voglia cambiare il profilo di collaborazione
			 * dell'accordo in uno diverso da oneway bisogna controllare che non
			 * esistano Porte Applicative, associate ai Servizi che implementano
			 * l'Accordo, con piu di un Servizio Applicativo associato in questo
			 * caso e' possibile cambiare il profilo da oneway in un altro
			 * 
			 * Nota: Se un Accordo contiene azioni allora bisogna tener conto
			 * che l'azione puo aver ridefinito il profilo e quindi vanno prese
			 * le Porte Applicative associate ai Servizi che implementano
			 * l'Accordo il quale sia senza azioni oppure con azioni con profilo
			 * di default
			 */
			String newProfiloCollaborazione = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(as.getProfiloCollaborazione());
			// controllo se profilo e' cambiato e se e' cambiato da oneway ad
			// altro devo effettuare i controlli
			if (!oldProfiloCollaborazione.equals(newProfiloCollaborazione) && oldProfiloCollaborazione.equals(CostantiRegistroServizi.ONEWAY.getValue())) {

				ArrayList<String> nomiAzioniDaEscludere = new ArrayList<>();
				// imposto le azioni per il filtro
				for (int i = 0; i < as.sizeAzioneList(); i++) {
					Azione azione = as.getAzione(i);
					String profiloAzione = azione.getProfAzione();
					// se il profilo dell'azione e' ridefinito allora dovro
					// escludere le porte delegate che hanno questa azione
					if (profiloAzione != null && profiloAzione.equals(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO)) {
						nomiAzioniDaEscludere.add(azione.getNome());
					}

				}

				// recupero i servizi che implementano questo accordo
				List<AccordoServizioParteSpecifica> listaServizi = apsCore.serviziWithIdAccordoList(as.getId());
				// per ogni servizio trovato prendo le porte applicative
				for (AccordoServizioParteSpecifica servizio : listaServizi) {
					List<PortaApplicativa> listPA = porteApplicativeCore.porteAppWithIdServizio(servizio.getId());
					// per ogni porta applicativa controllo se ha piu di un
					// servizio applicativo associato
					for (PortaApplicativa portaApplicativa : listPA) {

						// controllo se escludere o no il controllo di questa
						// porta applicativa in base
						// al nome dell'azione
						PortaApplicativaAzione azione = portaApplicativa.getAzione();
						String nomeAzione = azione != null ? azione.getNome() : null;
						// se il nome e' presente tra quelli da escludere
						// allora salto il controllo di questa porta
						if (nomiAzioniDaEscludere.contains(nomeAzione))
							continue;

						// nessuna esclusione allora controllo se ha piu di un
						// servizio applicativo
						if (portaApplicativa.sizeServizioApplicativoList() > 1) {
							// trovata porta applicativa con piu di un servizio
							// applicativo associato
							String msg = "Impossibile cambiare il profilo di collaborazione da {0} a {1} " + "in quanto esiste almeno una Porta Applicativa [" + portaApplicativa.getNome() + "] con piu' di un Servizio Applicativo associato.";
							pd.setMessage(MessageFormat.format(msg, oldProfiloCollaborazione, newProfiloCollaborazione));

							// setto la barra del titolo
							ServletUtils.setPageDataTitle(pd, listaParams);
							
							// preparo i campi
							List<DataElement> dati = new ArrayList<>();

							dati.add(ServletUtils.getDataElementForEditModeFinished());

							// update della configurazione 
							if(strutsBean.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
								strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(strutsBean.consoleConfiguration,
										strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
										strutsBean.registryReader, strutsBean.configRegistryReader, idAccordoOLD);
							else 
								strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(strutsBean.consoleConfiguration,
										strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
										strutsBean.registryReader, strutsBean.configRegistryReader, idAccordoOLD);

							dati = apcHelper.addAccordiToDati(dati, nome, strutsBean.descr, strutsBean.profcoll, strutsBean.wsdldef, strutsBean.wsdlconc, strutsBean.wsdlserv, strutsBean.wsdlservcorr, strutsBean.wsblconc,strutsBean.wsblserv,strutsBean.wsblservcorr,
									strutsBean.filtrodup, strutsBean.confric, strutsBean.idcoll, strutsBean.idRifRichiesta, strutsBean.consord, strutsBean.scadenza, strutsBean.id, tipoOp, 
									strutsBean.showUtilizzoSenzaAzione, strutsBean.utilizzoSenzaAzione,strutsBean.referente,strutsBean.versione,providersList,providersListLabel,
									strutsBean.privato,strutsBean.isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
									strutsBean.accordoCooperazioneId,strutsBean.statoPackage,oldStatoPackage, strutsBean.tipoAccordo, strutsBean.validazioneDocumenti,
									strutsBean.tipoProtocollo, listaTipiProtocollo,used,asWithAllegati,strutsBean.protocolFactory,
									strutsBean.serviceBinding,strutsBean.messageType,strutsBean.interfaceType, strutsBean.gruppi, elencoGruppi,
									false, -1, false, -1,
									false, strutsBean.canaleStato, strutsBean.canale, canaleList, gestioneCanaliEnabled);

							// aggiunta campi custom
							if(addPropertiesHidden) {
								dati = apcHelper.addProtocolPropertiesToDatiAsHidden(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
							}else {
								dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
							}
								
							pd.setDati(dati);

							ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

							return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
						}
					}

				}

			}

			Object[] operazioniDaEffettuare = new Object[1]; // almeno 1, l'accordo esiste!
			List<Object> operazioniList = new ArrayList<>();
			operazioniList.add(as);


			IDAccordo idNEW = idAccordoFactory.getIDAccordoFromAccordo(as);
			if(!idNEW.equals(idAccordoOLD)){

				AccordiServizioParteComuneUtilities.findOggettiDaAggiornare(idAccordoOLD, as, apcCore, operazioniList);

			}

			if(!objectToCreate.isEmpty()) { // salvo eventuali nuovi gruppi
				// effettuo le operazioni
				apcCore.performCreateOperation(userLogin, apcHelper.smista(), objectToCreate.toArray(new Object[objectToCreate.size()]));
			}

			operazioniDaEffettuare = operazioniList.toArray(operazioniDaEffettuare);
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), operazioniDaEffettuare);

			if(gestioneInformazioniGenerali) {
				if(!idNEW.equals(idAccordoOLD)){
					ServletUtils.removeRisultatiRicercaFromSession(request, session, Liste.ACCORDI);
				}
			}
			else if(gestioneGruppi || gestioneCanale) {
				ServletUtils.removeRisultatiRicercaFromSession(request, session, Liste.ACCORDI);
			}
			
			// preparo lista
			List<AccordoServizioParteComuneSintetico> lista = AccordiServizioParteComuneUtilities.accordiList(apcCore, userLogin, ricerca, strutsBean.tipoAccordo);
			
			if(isModalitaVistaApiCustom) {
				apcHelper.prepareApiChange(tipoOp, as); 
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				return ServletUtils.getStrutsForwardEditModeFinished(mapping, ApiCostanti.OBJECT_NAME_APC_API, ForwardParams.CHANGE());
			}
			
			apcHelper.prepareAccordiList(lista, ricerca, strutsBean.tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());	

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
		} 

	}
}
