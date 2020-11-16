/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
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
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
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

	private String editMode = null;
	private String id, descr, profcoll, filtrodup, confric, idcoll, idRifRichiesta, consord, scadenza, utilizzo, referente, versione,accordoCooperazioneId,statoPackage,
	tipoAccordo, tipoProtocollo, actionConfirm, backToStato;
	boolean utilizzoSenzaAzione, showUtilizzoSenzaAzione = false,privato ,isServizioComposto,  validazioneDocumenti = true;
	private ServiceBinding serviceBinding = null;
	private MessageType messageType = null;
	private InterfaceType interfaceType = null;
	private String gruppi, canale, canaleStato;
	
	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory= null;
	private IRegistryReader registryReader = null; 
	private IConfigIntegrationReader configRegistryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	private String protocolPropertiesSet = null;


	private BinaryParameter wsdlservcorr, wsdldef, wsdlserv, wsdlconc, wsblconc, wsblserv, wsblservcorr;

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

		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		this.consoleOperationType = ConsoleOperationType.CHANGE;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.CHANGE;
		List<ProtocolProperty> oldProtocolPropertyList = null;

		ApiHelper apcHelper = new ApiHelper(request, pd, session);
		
		this.id = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
		int idAcc = 0;
		try {
			idAcc = Integer.parseInt(this.id);
		} catch (Exception e) {
		}
		this.editMode = apcHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
		this.protocolPropertiesSet = apcHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_SET);
		this.descr = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_DESCRIZIONE);
		this.profcoll = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROFILO_COLLABORAZIONE);
		this.filtrodup = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_FILTRO_DUPLICATI);
		this.confric = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONFERMA_RICEZIONE);
		this.idcoll = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_COLLABORAZIONE);
		this.idRifRichiesta = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID_RIFERIMENTO_RICHIESTA);
		this.consord = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONSEGNA_ORDINE);
		this.scadenza = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SCADENZA);
		this.utilizzo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_UTILIZZO_SENZA_AZIONE);
		this.utilizzoSenzaAzione = ServletUtils.isCheckBoxEnabled(this.utilizzo);
		this.showUtilizzoSenzaAzione = false;
		String oldProfiloCollaborazione = "";
		this.referente = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_REFERENTE);
		this.versione = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VERSIONE);
		// patch per version spinner fino a che non si trova un modo piu' elegante
		/*if(ch.core.isBackwardCompatibilityAccordo11()){
			if("0".equals(versione))
				versione = "";
		}*/
		String privatoS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PRIVATO);
		this.privato = ServletUtils.isCheckBoxEnabled(privatoS);
		String isServizioCompostoS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_IS_SERVIZIO_COMPOSTO);
		this.isServizioComposto = ServletUtils.isCheckBoxEnabled(isServizioCompostoS);
		this.accordoCooperazioneId = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ACCORDO_COOPERAZIONE);
		this.statoPackage = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_STATO_PACKAGE);
		this.tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
		this.tipoProtocollo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO);
		this.actionConfirm = apcHelper.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);
		this.backToStato = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RIPRISTINA_STATO);
		String serviceBindingS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SERVICE_BINDING);
		this.serviceBinding = StringUtils.isNotEmpty(serviceBindingS) ? ServiceBinding.valueOf(serviceBindingS) : null;
		String messageProcessorS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_MESSAGE_TYPE);
		this.messageType = (StringUtils.isNotEmpty(messageProcessorS) && !messageProcessorS.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_DEFAULT)) 
				? MessageType.valueOf(messageProcessorS) : null;
		String formatoSpecificaS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_INTERFACE_TYPE);
		this.interfaceType = StringUtils.isNotEmpty(formatoSpecificaS) ? InterfaceType.toEnumConstant(formatoSpecificaS) : null;
		
		this.gruppi = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_GRUPPI);
		this.canale = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CANALE);
		this.canaleStato = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CANALE_STATO);

		if("".equals(this.tipoAccordo))
			this.tipoAccordo = null;
		if(this.tipoAccordo!=null){
			if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE.equals(this.tipoAccordo)){
				this.isServizioComposto = false;
			}else if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO.equals(this.tipoAccordo)){
				this.isServizioComposto = true;
			}
		}

		String tmpValidazioneDocumenti = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);

		String apiGestioneParziale = apcHelper.getParameter(ApiCostanti.PARAMETRO_APC_API_GESTIONE_PARZIALE);
		Boolean isModalitaVistaApiCustom = ServletUtils.getBooleanAttributeFromSession(ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API, session, false);
		
		boolean chiediConferma = true;
		
		boolean gestioneInformazioniGenerali = false;
		@SuppressWarnings("unused")
		boolean gestioneInformazioniProfilo = false;
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
			else if(ApiCostanti.VALORE_PARAMETRO_APC_API_PROFILO.equals(apiGestioneParziale)) {
				gestioneInformazioniProfilo = true;
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
		
		if(ServletUtils.isEditModeInProgress(this.editMode) ){

			// primo accesso alla servlet

			if(tmpValidazioneDocumenti!=null){
				this.validazioneDocumenti = ServletUtils.isCheckBoxEnabled(tmpValidazioneDocumenti);
			}else{
				this.validazioneDocumenti = true;
			}
		}else{
			this.validazioneDocumenti = ServletUtils.isCheckBoxEnabled(tmpValidazioneDocumenti);
		}

		// Preparo il menu
		apcHelper.makeMenu();

		// Prendo il nome dell'accordo
		String nome = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME);

		AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
		AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(apcCore);
		SoggettiCore soggettiCore = new SoggettiCore(apcCore);
		PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apcCore);
		AccordiCooperazioneCore acCore = new AccordiCooperazioneCore(apcCore);
		GruppiCore gruppiCore = new GruppiCore(apcCore);
		ConfigurazioneCore confCore = new ConfigurazioneCore(apcCore);
		Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
		FiltroRicercaGruppi filtroRicerca = new FiltroRicercaGruppi();
		List<String> elencoGruppi = null;

		AccordoServizioParteComune as = apcCore.getAccordoServizioFull(idAcc);
		boolean asWithAllegati = apcHelper.asWithAllegatiXsd(as);
		
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
		
		IDAccordo idAccordoOLD = idAccordoFactory.getIDAccordoFromValues(as.getNome(),BeanUtilities.getSoggettoReferenteID(as.getSoggettoReferente()),as.getVersione());

		try {

			// controllo se l'accordo e' utilizzato da qualche asps
			List<AccordoServizioParteSpecifica> asps = apsCore.serviziByAccordoFilterList(idAccordoOLD);
			used = asps != null && asps.size() > 0;

			// lista dei protocolli supportati
			listaTipiProtocollo = apcCore.getProtocolliByFilter(session, true, false);

			// primo accesso 
			if(this.tipoProtocollo == null){
				if(as!=null && as.getSoggettoReferente()!=null){
					this.tipoProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo());
				}
				else{
					this.tipoProtocollo = apsCore.getProtocolloDefault(session, listaTipiProtocollo);
				}
			}
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.tipoProtocollo);
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = soggettiCore.getRegistryReader(this.protocolFactory); 
			this.configRegistryReader = soggettiCore.getConfigIntegrationReader(this.protocolFactory);
			this.consoleConfiguration = this.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE) ? 
					this.consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteComune(this.consoleOperationType, apcHelper, 
							this.registryReader, this.configRegistryReader, idAccordoOLD)
					: this.consoleDynamicConfiguration.getDynamicConfigAccordoServizioComposto(this.consoleOperationType, apcHelper, 
							this.registryReader, this.configRegistryReader, idAccordoOLD);

			this.protocolProperties = apcHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);

			// se this.initProtocolPropertiesFromDb = true allora leggo le properties dal db... 

			try{
				AccordoServizioParteComune apcOLD = this.registryReader.getAccordoServizioParteComune(idAccordoOLD);
				oldProtocolPropertyList = apcOLD.getProtocolPropertyList(); 

			}catch(RegistryNotFound r){}

			if(this.protocolPropertiesSet == null){
				ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(this.protocolProperties, oldProtocolPropertyList, this.consoleOperationType);
			}

			List<String> tipiSoggettiGestitiProtocollo = soggettiCore.getTipiSoggettiGestitiProtocollo(this.tipoProtocollo);
			List<Soggetto> listaSoggetti=null;

			if(apcCore.isVisioneOggettiGlobale(userLogin)){
				listaSoggetti = soggettiCore.soggettiList(null, new Search(true));
			}else{
				listaSoggetti = soggettiCore.soggettiList(userLogin, new Search(true));
			}

			List<String> soggettiListTmp = new ArrayList<String>();
			List<String> soggettiListLabelTmp = new ArrayList<String>();
			soggettiListTmp.add("-");
			soggettiListLabelTmp.add("-");

			if (listaSoggetti.size() > 0) {
				for (Soggetto soggetto : listaSoggetti) {
					if(tipiSoggettiGestitiProtocollo.contains(soggetto.getTipo())){
						soggettiListTmp.add(soggetto.getId().toString());
						//soggettiListLabelTmp.add(soggetto.getTipo() + "/" + soggetto.getNome());
						soggettiListLabelTmp.add(apcHelper.getLabelNomeSoggetto(this.tipoProtocollo, soggetto.getTipo() , soggetto.getNome() ));
					}
				}
			}
			providersList = soggettiListTmp.toArray(new String[1]);
			providersListLabel = soggettiListLabelTmp.toArray(new String[1]);

			if(this.isServizioComposto) {
				List<AccordoCooperazione> lista = null;
				if(apcCore.isVisioneOggettiGlobale(userLogin)){
					lista = acCore.accordiCooperazioneList(null, new Search(true));
				}else{
					lista = acCore.accordiCooperazioneList(userLogin, new Search(true));
				}
				if (lista != null && lista.size() > 0) {
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
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
		}

		//se passo dal link diretto di ripristino stato imposto il nuovo stato
		if(this.backToStato != null && (this.actionConfirm == null || this.actionConfirm.equals(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_OK)))
			this.statoPackage = this.backToStato;

		String uriAS = idAccordoFactory.getUriFromIDAccordo(idAccordoOLD);
		String labelASTitle = apcHelper.getLabelIdAccordo(this.tipoProtocollo, idAccordoOLD); 
		String oldStatoPackage = as.getStatoPackage();			

		Properties propertiesProprietario = new Properties();
		propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, this.id);
		propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_SERVIZIO_PARTE_COMUNE);
		propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, uriAS);
		propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE, URLEncoder.encode( AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE + "?" + request.getQueryString(), "UTF-8"));
		propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, this.tipoProtocollo);
		propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO, this.tipoAccordo);
		
		List<Parameter> listaParams = apcHelper.getTitoloApc(tipoOp, as, this.tipoAccordo, labelASTitle, null, true); 

		// Se idhid = null, devo visualizzare la pagina per la modifica dati
		if(ServletUtils.isEditModeInProgress(this.editMode)){

			try {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				if(this.descr==null){
					//inizializzazione default
					this.descr = as.getDescrizione();

					if(this.tipoAccordo!=null){
						if("apc".equals(this.tipoAccordo)){
							this.isServizioComposto = false;
						}else if("asc".equals(this.tipoAccordo)){
							this.isServizioComposto = true;
						}
					}else{
						this.isServizioComposto = as.getServizioComposto()!=null ? true : false;
					}
					if(this.isServizioComposto){
						this.accordoCooperazioneId = ""+as.getServizioComposto().getIdAccordoCooperazione();
					}else{
						this.accordoCooperazioneId="-";
					}
				}

				int idReferente = -1;
				try{
					idReferente = Integer.parseInt(this.referente);
				}catch(Exception e){}
				if(idReferente<=0 && !"-".equals(this.referente)){
					IdSoggetto assr = as.getSoggettoReferente();
					if (assr != null) {
						Soggetto s = soggettiCore.getSoggetto(new IDSoggetto(assr.getTipo(),assr.getNome()));
						this.referente = "" + s.getId();
					}else{
						this.referente = "-";
					}
				}

				if(this.versione == null){
					if(as.getVersione()!=null)
						this.versione = as.getVersione().intValue()+"";
				}

				// controllo profilo collaborazione
				if(this.profcoll == null)
					this.profcoll = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(as.getProfiloCollaborazione());

				if(this.filtrodup == null)
					this.filtrodup = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati());
				if(this.confric == null)
					this.confric = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione());
				if(this.idcoll == null)
					this.idcoll = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione());
				if(this.idRifRichiesta == null)
					this.idRifRichiesta = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdRiferimentoRichiesta());
				if(this.consord == null)
					this.consord = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine());
				if(this.scadenza == null)
					this.scadenza = as.getScadenza() != null ? as.getScadenza() : "";
				if(privatoS==null){
					this.privato = as.getPrivato()!=null && as.getPrivato();
				}

				this.showUtilizzoSenzaAzione = as.sizeAzioneList() > 0;// se ci
				// sono
				// azioni
				// allora
				// visualizzo
				// il
				// checkbox
				if(this.utilizzo==null)
					this.utilizzoSenzaAzione = as.getUtilizzoSenzaAzione();	

				if(this.statoPackage==null)
					this.statoPackage = as.getStatoPackage();

				if(this.wsdlconc == null){
					this.wsdlconc = new BinaryParameter();
					this.wsdlconc.setValue(as.getByteWsdlConcettuale());
				}

				if(this.wsdldef == null){
					this.wsdldef = new BinaryParameter();
					this.wsdldef.setValue(as.getByteWsdlDefinitorio());
				}

				if(this.wsdlserv == null){
					this.wsdlserv = new BinaryParameter();
					this.wsdlserv.setValue(as.getByteWsdlLogicoErogatore());
				}

				if(this.wsdlservcorr == null){
					this.wsdlservcorr = new BinaryParameter();
					this.wsdlservcorr.setValue(as.getByteWsdlLogicoFruitore());
				}

				if(this.wsblconc == null){
					this.wsblconc = new BinaryParameter();
					this.wsblconc.setValue(as.getByteSpecificaConversazioneConcettuale());
				}

				if(this.wsblserv == null){
					this.wsblserv = new BinaryParameter();
					this.wsblserv.setValue(as.getByteSpecificaConversazioneErogatore());
				}

				if(this.wsblservcorr == null){
					this.wsblservcorr = new BinaryParameter();
					this.wsblservcorr.setValue(as.getByteSpecificaConversazioneFruitore());
				}

				if(this.serviceBinding == null){
					this.serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
				
					if(this.messageType == null){
						this.messageType = apcCore.toMessageMessageType(as.getMessageType());
					}
					
					filtroRicerca = new FiltroRicercaGruppi();
					filtroRicerca.setServiceBinding(apcCore.fromMessageServiceBinding(this.serviceBinding));
					elencoGruppi = gruppiCore.getAllGruppi(filtroRicerca);
				}
				
				if(this.interfaceType == null)
					this.interfaceType = apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica());
				
				if(this.gruppi == null) {
					// leggo i gruppi dall'accordo
					if(as.getGruppi() != null) {
						List<String> nomiGruppi = as.getGruppi().getGruppoList().stream().flatMap(e-> Stream.of(e.getNome())).collect(Collectors.toList());
						this.gruppi = StringUtils.join(nomiGruppi, ",");
					} else {
						this.gruppi = "";
					}
				}
				
				if(this.canale == null) {
					this.canale = as.getCanale();
				}
				if(this.canaleStato==null) {
					if(this.canale == null) {
						this.canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT;
					} else {
						this.canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO;
					}
				} 

			} catch (Exception ex) {
				return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), ex, pd, session, gd, mapping, 
						AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
			}

			String postBackElementName = apcHelper.getParameter(Costanti.POSTBACK_ELEMENT_NAME);

			// Controllo se ho modificato il protocollo, resetto il referente e il service binding
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SERVICE_BINDING)){
					this.interfaceType = null;
					this.messageType = null;
					
					filtroRicerca = new FiltroRicercaGruppi();
					filtroRicerca.setServiceBinding(apcCore.fromMessageServiceBinding(this.serviceBinding));
					elencoGruppi = gruppiCore.getAllGruppi(filtroRicerca);
				}
			}

			// fromato specifica
			if(this.interfaceType == null) {
				if(this.serviceBinding != null) {
					switch(this.serviceBinding) {
					case REST:
						this.interfaceType = InterfaceType.toEnumConstant(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_REST);
						break;
					case SOAP:
					default:
						this.interfaceType = InterfaceType.toEnumConstant(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_SOAP);
						break;
					}
				}
			}


			if( this.backToStato == null){
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
 
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				if(this.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration,
							this.consoleOperationType, apcHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idAccordoOLD);
				else  
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(this.consoleConfiguration,
							this.consoleOperationType, apcHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idAccordoOLD);

				dati = apcHelper.addAccordiToDati(dati, nome, this.descr, this.profcoll, this.wsdldef, this.wsdlconc, this.wsdlserv, this.wsdlservcorr,
						this.wsblconc,this.wsblserv,this.wsblservcorr,
						this.filtrodup, this.confric, this.idcoll, this.idRifRichiesta, this.consord, this.scadenza, this.id, tipoOp, 
						this.showUtilizzoSenzaAzione, this.utilizzoSenzaAzione,this.referente,this.versione,providersList,providersListLabel,
						this.privato,this.isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
						this.accordoCooperazioneId,this.statoPackage,oldStatoPackage, this.tipoAccordo, this.validazioneDocumenti,
						this.tipoProtocollo, listaTipiProtocollo,used,asWithAllegati,this.protocolFactory,this.serviceBinding,this.messageType,this.interfaceType, this.gruppi, elencoGruppi,
						false, -1, false, -1,
						false, this.canaleStato, this.canale, canaleList, gestioneCanaliEnabled);

				// aggiunta campi custom
				if(addPropertiesHidden) {
					dati = apcHelper.addProtocolPropertiesToDatiAsHidden(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}else {
					dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}
					
				pd.setDati(dati);
				
				if(apcHelper.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(as.getStatoPackage())){
					pd.disableEditMode();
				}

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);


				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
			}
		}

		boolean visibilitaAccordoCooperazione=false;
		if("-".equals(this.accordoCooperazioneId)==false && "".equals(this.accordoCooperazioneId)==false  && this.accordoCooperazioneId!=null){
			AccordoCooperazione ac = acCore.getAccordoCooperazione(Long.parseLong(this.accordoCooperazioneId));
			visibilitaAccordoCooperazione=ac.getPrivato()!=null && ac.getPrivato();
		}

		// Controlli sui campi immessi
		boolean isOk = apcHelper.accordiCheckData(tipoOp, nome, this.descr, this.profcoll, 
				this.wsdldef, this.wsdlconc, this.wsdlserv, this.wsdlservcorr,  
				this.filtrodup, this.confric, this.idcoll, this.idRifRichiesta, this.consord, 
				this.scadenza, this.id,this.referente,this.versione,this.accordoCooperazioneId,this.privato,visibilitaAccordoCooperazione,idAccordoOLD, 
				this.wsblconc,this.wsblserv,this.wsblservcorr, this.validazioneDocumenti,this.tipoProtocollo,this.backToStato,this.serviceBinding,this.messageType,this.interfaceType,
				true, this.gruppi, this.canaleStato, this.canale, gestioneCanaliEnabled);

		// updateDynamic
		if(isOk) {
			if(this.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
				this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration,
						this.consoleOperationType, apcHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idAccordoOLD);
			else 
				this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(this.consoleConfiguration,
						this.consoleOperationType, apcHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idAccordoOLD);
		}
		
		// Validazione base dei parametri custom 
		if(isOk){
			try{
				apcHelper.validaProtocolProperties(this.consoleConfiguration, this.consoleOperationType, this.protocolProperties);
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
				if(this.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
					this.consoleDynamicConfiguration.validateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration, this.consoleOperationType, apcHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idAccordoOLD);
				else 
					this.consoleDynamicConfiguration.validateDynamicConfigAccordoServizioComposto(this.consoleConfiguration, this.consoleOperationType, apcHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idAccordoOLD);
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
			Vector<DataElement> dati = new Vector<DataElement>();

			dati.addElement(ServletUtils.getDataElementForEditModeFinished());

			// update della configurazione 
			if(this.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
				this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration,
						this.consoleOperationType, apcHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idAccordoOLD);
			else 
				this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(this.consoleConfiguration,
						this.consoleOperationType, apcHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idAccordoOLD);

			dati = apcHelper.addAccordiToDati(dati, nome, this.descr, this.profcoll, this.wsdldef, this.wsdlconc, this.wsdlserv, this.wsdlservcorr,this.wsblconc,this.wsblserv,this.wsblservcorr, 
					this.filtrodup, this.confric, this.idcoll, this.idRifRichiesta, this.consord, this.scadenza, this.id, tipoOp, 
					this.showUtilizzoSenzaAzione, this.utilizzoSenzaAzione,this.referente,this.versione,providersList,providersListLabel,
					this.privato,this.isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
					this.accordoCooperazioneId,this.statoPackage,oldStatoPackage, this.tipoAccordo, this.validazioneDocumenti,
					this.tipoProtocollo, listaTipiProtocollo,used,asWithAllegati,this.protocolFactory,this.serviceBinding,this.messageType,this.interfaceType, this.gruppi, elencoGruppi,
					false, -1, false, -1,
					false, this.canaleStato, this.canale, canaleList, gestioneCanaliEnabled);

			// aggiunta campi custom
			if(addPropertiesHidden) {
				dati = apcHelper.addProtocolPropertiesToDatiAsHidden(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
			}else {
				dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
			}
			
			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
		}

		// I dati dell'utente sono validi, lo informo che l'accordo e' utilizzato da asps a meno che non sto modificando solamente la descrizione
		if(chiediConferma && this.actionConfirm == null){
			if(used || this.backToStato != null){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeInProgress());

				// update della configurazione 
				if(this.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration,
							this.consoleOperationType, apcHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idAccordoOLD);
				else 
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(this.consoleConfiguration,
							this.consoleOperationType, apcHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idAccordoOLD);

				dati = apcHelper.addAccordiToDati(dati, nome, this.descr, this.profcoll, this.wsdldef, this.wsdlconc, this.wsdlserv, this.wsdlservcorr,this.wsblconc,this.wsblserv,this.wsblservcorr, 
						this.filtrodup, this.confric, this.idcoll, this.idRifRichiesta, this.consord, this.scadenza, this.id, tipoOp, 
						this.showUtilizzoSenzaAzione, this.utilizzoSenzaAzione,this.referente,this.versione,providersList,providersListLabel,
						this.privato,this.isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
						this.accordoCooperazioneId,this.statoPackage,oldStatoPackage, this.tipoAccordo, this.validazioneDocumenti,
						this.tipoProtocollo, listaTipiProtocollo,used,asWithAllegati,this.protocolFactory,this.serviceBinding,this.messageType,this.interfaceType, this.gruppi, elencoGruppi,
						false, -1, false, -1,
						true, this.canaleStato, this.canale, canaleList, gestioneCanaliEnabled);

				dati = apcHelper.addAccordiToDatiAsHidden(dati, nome, this.descr, this.profcoll, null, null, null, null, 
						this.filtrodup, this.confric, this.idcoll, this.idRifRichiesta, this.consord, this.scadenza, this.id,						
						tipoOp, this.showUtilizzoSenzaAzione, this.utilizzoSenzaAzione, this.referente,this.versione, providersList, providersListLabel,
						this.privato,this.isServizioComposto, accordiCooperazioneEsistenti, accordiCooperazioneEsistentiLabel,
						this.accordoCooperazioneId, this.statoPackage,oldStatoPackage, this.tipoAccordo, this.validazioneDocumenti, this.tipoProtocollo, 
						listaTipiProtocollo, used, this.serviceBinding,this.messageType,this.interfaceType, this.gruppi, this.canaleStato, this.canale);
				
				if(this.backToStato!= null) {
					// backtostato per chiudere la modifica dopo la conferma
					DataElement de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
					de.setValue(this.backToStato);
					de.setType(DataElementType.HIDDEN);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RIPRISTINA_STATO);
					dati.addElement(de);
				}
				
				
				// aggiunta campi custom
				if(addPropertiesHidden) {
					dati = apcHelper.addProtocolPropertiesToDatiAsHidden(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}else {
					dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				
					// aggiunta campi custom come hidden, quelli sopra vengono bruciati dal no-edit
					dati = apcHelper.addProtocolPropertiesToDatiAsHidden(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}
					
				pd.setDati(dati);

				String uriAccordo = idAccordoFactory.getUriFromIDAccordo(idAccordoOLD);
				String msg = ""; 
				if(used)
					msg = "Attenzione, esistono delle erogazioni che riferiscono l''accordo [{0}] che si sta modificando, continuare?";

				if(this.backToStato != null){
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

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, 
						AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());	
			}
		}

		oldProfiloCollaborazione = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(as.getProfiloCollaborazione());

		as.setNome(nome);
		as.setDescrizione(this.descr);
		as.setConfermaRicezione(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(this.confric)));
		as.setConsegnaInOrdine(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(this.consord)));
		as.setFiltroDuplicati(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(this.filtrodup)));
		as.setIdCollaborazione(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(this.idcoll)));
		as.setIdRiferimentoRichiesta(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(this.idRifRichiesta)));
		as.setProfiloCollaborazione(ProfiloCollaborazione.toEnumConstant(AccordiServizioParteComuneHelper.convertProfiloCollaborazioneView2DB(this.profcoll)));
		as.setScadenza(this.scadenza);
		as.setUtilizzoSenzaAzione(as.sizeAzioneList() > 0 ? this.utilizzoSenzaAzione : true);
		if(this.referente!=null && !"".equals(this.referente) && !"-".equals(this.referente)){
			int idRef = 0;
			try {
				idRef = Integer.parseInt(this.referente);
			} catch (Exception e) {
			}
			if (idRef != 0) {
				int idReferente = Integer.parseInt(this.referente);
				Soggetto s = soggettiCore.getSoggetto(idReferente);			
				IdSoggetto assr = new IdSoggetto();
				assr.setTipo(s.getTipo());
				assr.setNome(s.getNome());
				as.setSoggettoReferente(assr);
			}
		}else{
			as.setSoggettoReferente(null);
		}
		if(this.versione!=null)
			as.setVersione(Integer.parseInt(this.versione));
		as.setPrivato(this.privato ? Boolean.TRUE : Boolean.FALSE);

		if(this.accordoCooperazioneId!=null && !"".equals(this.accordoCooperazioneId) && !"-".equals(this.accordoCooperazioneId)){
			AccordoServizioParteComuneServizioComposto assc = as.getServizioComposto();
			if(assc==null) assc = new AccordoServizioParteComuneServizioComposto();
			assc.setIdAccordoCooperazione(Long.parseLong(this.accordoCooperazioneId));
			as.setServizioComposto(assc);
		}else{
			as.setServizioComposto(null);
		}

		as.setOldIDAccordoForUpdate(idAccordoOLD);

		// servicebinding / messagetype / formatospecifica
		as.setServiceBinding(apcCore.fromMessageServiceBinding(this.serviceBinding));
		as.setMessageType(apcCore.fromMessageMessageType(this.messageType));
		as.setFormatoSpecifica(apcCore.interfaceType2FormatoSpecifica(this.interfaceType));

		// stato
		as.setStatoPackage(this.statoPackage);

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
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				if(this.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration,
							this.consoleOperationType, apcHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idAccordoOLD);
				else 
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(this.consoleConfiguration,
							this.consoleOperationType, apcHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idAccordoOLD);

				dati = apcHelper.addAccordiToDati(dati, nome, this.descr, this.profcoll, this.wsdldef, this.wsdlconc, this.wsdlserv, this.wsdlservcorr,this.wsblconc,this.wsblserv,this.wsblservcorr, 
						this.filtrodup, this.confric, this.idcoll, this.idRifRichiesta, this.consord, this.scadenza, this.id, tipoOp, 
						this.showUtilizzoSenzaAzione, this.utilizzoSenzaAzione,this.referente,this.versione,providersList,providersListLabel,
						this.privato,this.isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
						this.accordoCooperazioneId,this.statoPackage,oldStatoPackage, this.tipoAccordo, this.validazioneDocumenti,
						this.tipoProtocollo, listaTipiProtocollo,used,asWithAllegati,this.protocolFactory,this.serviceBinding,this.messageType,this.interfaceType, this.gruppi, elencoGruppi,
						false, -1, false, -1,
						false, this.canaleStato, this.canale, canaleList, gestioneCanaliEnabled);

				// aggiunta campi custom
				if(addPropertiesHidden) {
					dati = apcHelper.addProtocolPropertiesToDatiAsHidden(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}else {
					dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
			}
		}


		//imposto properties custom
		as.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(this.protocolProperties, this.consoleOperationType, oldProtocolPropertyList));

		List<Object> objectToCreate = new ArrayList<>();
		
		if(as.getGruppi() == null)
			as.setGruppi(new GruppiAccordo());
		
		// svuto la lista e riporto solo quelli che sono selezionati dall'utente.
		as.getGruppi().getGruppoList().clear();
		
		// gruppi
		if(StringUtils.isNotEmpty(this.gruppi)) {
			
		
			List<String> nomiGruppi = Arrays.asList(this.gruppi.split(","));
			
			for (String nomeGruppo : nomiGruppi) {
				if(!gruppiCore.existsGruppo(nomeGruppo)) {
					Gruppo nuovoGruppo = new Gruppo();
					nuovoGruppo.setNome(nomeGruppo);
					objectToCreate.add(nuovoGruppo);
				}
				
				GruppoAccordo gruppoAccordo = new GruppoAccordo();
				gruppoAccordo.setNome(nomeGruppo);
				as.getGruppi().addGruppo(gruppoAccordo );
			}
		}
		
		// canale
		if(gestioneCanaliEnabled) {
			if(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO.equals(this.canaleStato)) {
				as.setCanale(this.canale);
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
			if (!oldProfiloCollaborazione.equals(newProfiloCollaborazione) && oldProfiloCollaborazione.equals(CostantiRegistroServizi.ONEWAY)) {

				ArrayList<String> nomiAzioniDaEscludere = new ArrayList<String>();
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
							Vector<DataElement> dati = new Vector<DataElement>();

							dati.addElement(ServletUtils.getDataElementForEditModeFinished());

							// update della configurazione 
							if(this.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
								this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration,
										this.consoleOperationType, apcHelper, this.protocolProperties, 
										this.registryReader, this.configRegistryReader, idAccordoOLD);
							else 
								this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(this.consoleConfiguration,
										this.consoleOperationType, apcHelper, this.protocolProperties, 
										this.registryReader, this.configRegistryReader, idAccordoOLD);

							dati = apcHelper.addAccordiToDati(dati, nome, this.descr, this.profcoll, this.wsdldef, this.wsdlconc, this.wsdlserv, this.wsdlservcorr, this.wsblconc,this.wsblserv,this.wsblservcorr,
									this.filtrodup, this.confric, this.idcoll, this.idRifRichiesta, this.consord, this.scadenza, this.id, tipoOp, 
									this.showUtilizzoSenzaAzione, this.utilizzoSenzaAzione,this.referente,this.versione,providersList,providersListLabel,
									this.privato,this.isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
									this.accordoCooperazioneId,this.statoPackage,oldStatoPackage, this.tipoAccordo, this.validazioneDocumenti,
									this.tipoProtocollo, listaTipiProtocollo,used,asWithAllegati,this.protocolFactory,
									this.serviceBinding,this.messageType,this.interfaceType, this.gruppi, elencoGruppi,
									false, -1, false, -1,
									false, this.canaleStato, this.canale, canaleList, gestioneCanaliEnabled);

							// aggiunta campi custom
							if(addPropertiesHidden) {
								dati = apcHelper.addProtocolPropertiesToDatiAsHidden(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
							}else {
								dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
							}
								
							pd.setDati(dati);

							ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

							return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
						}
					}

				}

			}

			Object[] operazioniDaEffettuare = new Object[1]; // almeno 1, l'accordo esiste!
			List<Object> operazioniList = new ArrayList<Object>();
			operazioniList.add(as);


			IDAccordo idNEW = idAccordoFactory.getIDAccordoFromAccordo(as);
			if(idNEW.equals(idAccordoOLD)==false){

				AccordiServizioParteComuneUtilities.findOggettiDaAggiornare(idAccordoOLD, as, apcCore, operazioniList);

			}

			if(!objectToCreate.isEmpty()) { // salvo eventuali nuovi gruppi
				// effettuo le operazioni
				apcCore.performCreateOperation(userLogin, apcHelper.smista(), objectToCreate.toArray(new Object[objectToCreate.size()]));
			}

			operazioniDaEffettuare = operazioniList.toArray(operazioniDaEffettuare);
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), operazioniDaEffettuare);

			if(gestioneInformazioniGenerali) {
				if(idNEW.equals(idAccordoOLD)==false){
					ServletUtils.removeRisultatiRicercaFromSession(session, Liste.ACCORDI);
				}
			}
			else if(gestioneGruppi || gestioneCanale) {
				ServletUtils.removeRisultatiRicercaFromSession(session, Liste.ACCORDI);
			}
			
			// preparo lista
			List<AccordoServizioParteComuneSintetico> lista = AccordiServizioParteComuneUtilities.accordiList(apcCore, userLogin, ricerca, this.tipoAccordo);
			
			if(isModalitaVistaApiCustom) {
				apcHelper.prepareApiChange(tipoOp, as); 
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				return ServletUtils.getStrutsForwardEditModeFinished(mapping, ApiCostanti.OBJECT_NAME_APC_API, ForwardParams.CHANGE());
			}
			
			apcHelper.prepareAccordiList(lista, ricerca, this.tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());	

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
		} 

	}
}
