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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.GruppiAccordo;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.GruppoAccordo;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.driver.FiltroRicercaGruppi;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.gruppi.GruppiCore;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * accordiAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneAdd extends Action {

	

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		AccordiServizioParteComuneAddStrutsBean strutsBean = new AccordiServizioParteComuneAddStrutsBean();
		
		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		strutsBean.consoleOperationType = ConsoleOperationType.ADD;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.ADD; 

		try {
			ApiHelper apcHelper = new ApiHelper(request, pd, session);
			
			// Preparo il menu
			apcHelper.makeMenu();
			
			Boolean isModalitaVistaApiCustom = ServletUtils.getBooleanAttributeFromSession(ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API, session, request, false).getValue();
			
			strutsBean.editMode = apcHelper.getParametroEditMode(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			strutsBean.nome = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME);
			strutsBean.descr = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_DESCRIZIONE);
			strutsBean.profcoll = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROFILO_COLLABORAZIONE);
			
			
			strutsBean.wsdldef = apcHelper.getBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO);
			strutsBean.wsdlconc = apcHelper.getBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE);
			strutsBean.wsdlserv = apcHelper.getBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE);
			strutsBean.wsdlservcorr = apcHelper.getBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE);

			strutsBean.wsblconc = apcHelper.getBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE);
			strutsBean.wsblserv = apcHelper.getBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE);
			strutsBean.wsblservcorr = apcHelper.getBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE);

			strutsBean.filtrodup = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_FILTRO_DUPLICATI);
			strutsBean.confric = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONFERMA_RICEZIONE);
			strutsBean.idcoll = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_COLLABORAZIONE);
			strutsBean.idRifRichiesta = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID_RIFERIMENTO_RICHIESTA);
			strutsBean.consord = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONSEGNA_ORDINE);
			strutsBean.scadenza = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SCADENZA);
			strutsBean.referente = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_REFERENTE);
			strutsBean.versione = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VERSIONE);
			
			String serviceBindingS = apcHelper.getParametroServiceBinding(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SERVICE_BINDING);
			strutsBean.serviceBinding = StringUtils.isNotEmpty(serviceBindingS) ? ServiceBinding.valueOf(serviceBindingS) : null;
			String messageProcessorS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_MESSAGE_TYPE);
			strutsBean.messageType = (StringUtils.isNotEmpty(messageProcessorS) && !messageProcessorS.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_DEFAULT)) ? MessageType.valueOf(messageProcessorS) : null;
			String formatoSpecificaS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_INTERFACE_TYPE);
			strutsBean.interfaceType = StringUtils.isNotEmpty(formatoSpecificaS) ? InterfaceType.toEnumConstant(formatoSpecificaS) : null;
			
			strutsBean.gruppi = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_GRUPPI);
			strutsBean.canale = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CANALE);
			strutsBean.canaleStato = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CANALE_STATO);

			// patch per version spinner fino a che non si trova un modo piu' elegante
			// Veniva sempre impostato false, lo lascio commentato in questo punto perche' veniva passato nella decode della richiesta multipart...
			/**			if(this.isBackwardCompatibilityAccordo11){
			//				if("0".equals(strutsBean.versione))
			//					strutsBean.versione = "";
			//			} */

			String priv = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PRIVATO);
			strutsBean.privato = ServletUtils.isCheckBoxEnabled(priv);

			String isServComp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_IS_SERVIZIO_COMPOSTO);
			strutsBean.isServizioComposto = ServletUtils.isCheckBoxEnabled(isServComp);

			strutsBean.accordoCooperazione = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ACCORDO_COOPERAZIONE);
			strutsBean.statoPackage = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_STATO_PACKAGE);

			strutsBean.tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(strutsBean.tipoAccordo))
				strutsBean.tipoAccordo = null;
			if(strutsBean.tipoAccordo!=null){
				if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE.equals(strutsBean.tipoAccordo)){
					strutsBean.isServizioComposto = false;
				}else if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO.equals(strutsBean.tipoAccordo)){
					strutsBean.isServizioComposto = true;
				}
			}

			if (apcHelper.isMultipart()) {
				/** this.decodeRequest(request,ch.core.isBackwardCompatibilityAccordo11());
				//				strutsBean.decodeRequestValidazioneDocumenti = false; // init
				//				this.decodeRequest(request,false); */

				//Quando trovava la linea corrispondente a AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI  
				//eseguiva questo codice 
				strutsBean.decodeRequestValidazioneDocumenti = true;
				String tmpValidazioneDocumenti = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);
				strutsBean.validazioneDocumenti = ServletUtils.isCheckBoxEnabled(tmpValidazioneDocumenti);
			}

			if(ServletUtils.isEditModeInProgress(strutsBean.editMode)){
				// primo accesso alla servlet
				strutsBean.validazioneDocumenti = true;
			}else{
				if(!strutsBean.decodeRequestValidazioneDocumenti){
					String tmpValidazioneDocumenti = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);
					strutsBean.validazioneDocumenti = ServletUtils.isCheckBoxEnabled(tmpValidazioneDocumenti);
				}
			}

			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			SoggettiCore soggettiCore = new SoggettiCore(apcCore);
			AccordiCooperazioneCore acCore = new AccordiCooperazioneCore(apcCore);
			GruppiCore gruppiCore = new GruppiCore(apcCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(apcCore);
			String labelAccordoServizio = AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(strutsBean.tipoAccordo);
			
			// carico i canali
			CanaliConfigurazione gestioneCanali = confCore.getCanaliConfigurazione(false);
			List<CanaleConfigurazione> canaleList = gestioneCanali != null ? gestioneCanali.getCanaleList() : new ArrayList<>();
			boolean gestioneCanaliEnabled = gestioneCanali != null && org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(gestioneCanali.getStato());
			
			// Tipi protocollo supportati
			// Controllo comunque quelli operativi, almeno uno deve esistere
			List<String> listaTipiProtocollo = apcCore.getProtocolliByFilter(request, session, true, PddTipologia.OPERATIVO, false, strutsBean.isServizioComposto);

			// primo accesso 
			strutsBean.tipoProtocollo =  apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO);
			if(strutsBean.tipoProtocollo == null){
				strutsBean.tipoProtocollo = apcCore.getProtocolloDefault(request, session, listaTipiProtocollo);
			}
			
			String nuovaVersioneTmp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_API_NUOVA_VERSIONE);
			strutsBean.nuovaVersione = ServletUtils.isCheckBoxEnabled(nuovaVersioneTmp);
			int gestioneNuovaVersioneMin = 1;
			boolean nuovaVersioneRidefinisciInterfaccia = true;
			long gestioneNuovaVersioneOldIdApc = -1;
			List<ProtocolProperty> gestioneNuovaVersioneOldProtocolProperties = null;
			if(strutsBean.nuovaVersione) {
				
				String nuovaVersioneTmpMinVersion = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_API_NUOVA_VERSIONE_MIN);
				if(!StringUtils.isEmpty(nuovaVersioneTmpMinVersion)) {
					gestioneNuovaVersioneMin = Integer.parseInt(nuovaVersioneTmpMinVersion);
				}
								
				String tmpIdPrecedenteAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
				if(!StringUtils.isEmpty(tmpIdPrecedenteAccordo)) {
					
					// ci entro solamente la prima volta al "click" sulla nuova versione
					
					nuovaVersioneRidefinisciInterfaccia = true; // default
					
					// fisso alcuni valori
					long idAccordoPrec = Long.parseLong(tmpIdPrecedenteAccordo);
					gestioneNuovaVersioneOldIdApc = idAccordoPrec;
					AccordoServizioParteComune aspc = apcCore.getAccordoServizioFull(idAccordoPrec);
					gestioneNuovaVersioneOldProtocolProperties = aspc.getProtocolPropertyList();
					IDAccordo idAccordoPrecedente = IDAccordoFactory.getInstance().getIDAccordoFromAccordo(aspc);
					strutsBean.nome = aspc.getNome();
					strutsBean.referente = soggettiCore.getSoggetto(aspc.getSoggettoReferente().toIDSoggetto()).getId().longValue()+"";
					strutsBean.tipoProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(aspc.getSoggettoReferente().getTipo());
					strutsBean.serviceBinding = apcCore.toMessageServiceBinding(aspc.getServiceBinding());
					if(aspc.getMessageType()!=null) {
						strutsBean.messageType = apcCore.toMessageMessageType(aspc.getMessageType());
					}
					if(aspc.getServizioComposto()!=null && aspc.getServizioComposto().getAccordoCooperazione()!=null && !"".equals(aspc.getServizioComposto().getAccordoCooperazione())) {
						IDAccordoCooperazione idAccordoCooperazione = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromUri(aspc.getServizioComposto().getAccordoCooperazione());
						strutsBean.accordoCooperazione = acCore.getAccordoCooperazione(idAccordoCooperazione).getId().longValue()+"";
						strutsBean.isServizioComposto = true;
					}
					if(strutsBean.interfaceType==null) {
						strutsBean.interfaceType = apcCore.formatoSpecifica2InterfaceType(aspc.getFormatoSpecifica());
					}
					if(strutsBean.descr==null || StringUtils.isEmpty(strutsBean.descr)) {
						strutsBean.descr = aspc.getDescrizione();
					}
					if((strutsBean.gruppi==null || StringUtils.isEmpty(strutsBean.gruppi)) &&
						aspc.getGruppi()!=null && aspc.getGruppi().getGruppoList()!=null && !aspc.getGruppi().getGruppoList().isEmpty()) {
						List<String> nomiGruppi = aspc.getGruppi().getGruppoList().stream().flatMap(e-> Stream.of(e.getNome())).collect(Collectors.toList());
						strutsBean.gruppi = StringUtils.join(nomiGruppi, ",");
					}
					if(strutsBean.profcoll==null || StringUtils.isEmpty(strutsBean.profcoll)) {
						strutsBean.profcoll = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(aspc.getProfiloCollaborazione());
					}
					if(strutsBean.filtrodup==null || StringUtils.isEmpty(strutsBean.filtrodup)) {
						strutsBean.filtrodup = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(aspc.getFiltroDuplicati());
					}
					if(strutsBean.confric==null || StringUtils.isEmpty(strutsBean.confric)) {
						strutsBean.confric = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(aspc.getConfermaRicezione());
					}
					if(strutsBean.idcoll==null || StringUtils.isEmpty(strutsBean.idcoll)) {
						strutsBean.idcoll = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(aspc.getIdCollaborazione());
					}
					if(strutsBean.idRifRichiesta==null || StringUtils.isEmpty(strutsBean.idRifRichiesta)) {
						strutsBean.idRifRichiesta = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(aspc.getIdRiferimentoRichiesta());
					}
					if(strutsBean.consord==null || StringUtils.isEmpty(strutsBean.consord)) {
						strutsBean.consord = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(aspc.getConsegnaInOrdine());
					}
					if(strutsBean.scadenza==null || StringUtils.isEmpty(strutsBean.scadenza)) {
						strutsBean.scadenza = aspc.getScadenza() != null ? aspc.getScadenza() : "";
					}
					
					gestioneNuovaVersioneMin = apcCore.getAccordoServizioParteComuneNextVersion(idAccordoPrecedente);
					if(strutsBean.versione==null || StringUtils.isEmpty(strutsBean.versione)) {
						strutsBean.versione = gestioneNuovaVersioneMin+"";
					}
					if(strutsBean.canale==null || StringUtils.isEmpty(strutsBean.canale)) {
						strutsBean.canale = aspc.getCanale();
					}
					if(strutsBean.canaleStato==null || StringUtils.isEmpty(strutsBean.canaleStato)) {
						if(strutsBean.canale == null) {
							strutsBean.canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT;
						} else {
							strutsBean.canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO;
						}
					} 
				}
				else {
					String nuovaVersioneRidefinisciInterfacciaTmp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_API_NUOVA_VERSIONE_RIDEFINISCI_INTERFACCIA);
					nuovaVersioneRidefinisciInterfaccia = ServletUtils.isCheckBoxEnabled(nuovaVersioneRidefinisciInterfacciaTmp);
				
					String nuovaVersioneOldIdApcTmp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_API_NUOVA_VERSIONE_OLD_ID_APC);
					gestioneNuovaVersioneOldIdApc = Long.valueOf(nuovaVersioneOldIdApcTmp);
				}
			}
			
			strutsBean.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(strutsBean.tipoProtocollo);
			strutsBean.consoleDynamicConfiguration =  strutsBean.protocolFactory.createDynamicConfigurationConsole();
			strutsBean.registryReader = soggettiCore.getRegistryReader(strutsBean.protocolFactory); 
			strutsBean.configRegistryReader = soggettiCore.getConfigIntegrationReader(strutsBean.protocolFactory);
			
			// service binding 
			if(strutsBean.serviceBinding == null) {
				strutsBean.serviceBinding  = soggettiCore.getDefaultServiceBinding(strutsBean.protocolFactory);
			}
			
			FiltroRicercaGruppi filtroRicerca = new FiltroRicercaGruppi();
			filtroRicerca.setServiceBinding(apcCore.fromMessageServiceBinding(strutsBean.serviceBinding));
			List<String> elencoGruppi = gruppiCore.getAllGruppi(filtroRicerca);

			boolean showReferente = false;
			if(strutsBean.isServizioComposto){
				showReferente = true;
			}
			else {
				showReferente = apcCore.isSupportatoSoggettoReferente(strutsBean.tipoProtocollo);
			}
			
			// ID Accordo Null per default
			IDAccordo idApc = null;			
			strutsBean.consoleConfiguration = (strutsBean.tipoAccordo==null || strutsBean.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE)) ? 
					strutsBean.consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteComune(strutsBean.consoleOperationType, apcHelper, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idApc)
					: strutsBean.consoleDynamicConfiguration.getDynamicConfigAccordoServizioComposto(strutsBean.consoleOperationType, apcHelper, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idApc);
					
			strutsBean.protocolProperties = apcHelper.estraiProtocolPropertiesDaRequest(strutsBean.consoleConfiguration, strutsBean.consoleOperationType);
			
			if(strutsBean.nuovaVersione && gestioneNuovaVersioneOldProtocolProperties!=null && !gestioneNuovaVersioneOldProtocolProperties.isEmpty()){
				ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(strutsBean.protocolProperties, gestioneNuovaVersioneOldProtocolProperties, strutsBean.consoleOperationType);
			}

			// Flag per controllare il mapping automatico di porttype e operation
			boolean enableAutoMapping = apcCore.isEnableAutoMappingWsdlIntoAccordo();
			boolean enableAutoMappingEstraiXsdSchemiFromWsdlTypes = apcCore.isEnableAutoMappingWsdlIntoAccordoEstrazioneSchemiInWsdlTypes();

			
			
			//Carico la lista dei tipi di soggetti gestiti dal protocollo
			List<String> tipiSoggettiGestitiProtocollo = soggettiCore.getTipiSoggettiGestitiProtocollo(strutsBean.tipoProtocollo);
				
			if(listaTipiProtocollo.isEmpty()) {
				boolean msg = true;
				if(strutsBean.isServizioComposto) {
					List<String> listaTipiProtocolloSenzaAccordiCooperazione = apcCore.getProtocolliByFilter(request, session, true, PddTipologia.OPERATIVO, false, false);
					if(!listaTipiProtocolloSenzaAccordiCooperazione.isEmpty()) {
						pd.setMessage("Non risultano registrati accordi di cooperazione", Costanti.MESSAGE_TYPE_INFO);
						msg = false;
					}
				}
				if(msg) {
					if(soggettiCore.isGestionePddAbilitata(apcHelper)) {
						pd.setMessage("Non risultano registrati soggetti associati a porte di dominio di tipo operativo", Costanti.MESSAGE_TYPE_INFO);
					}
					else {
						pd.setMessage("Non risultano registrati soggetti di dominio interno", Costanti.MESSAGE_TYPE_INFO);
					}
				}
				pd.disableEditMode();

				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, 
						ForwardParams.ADD());
			}
			
			List<Soggetto> listaSoggetti=null;
			if(apcCore.isVisioneOggettiGlobale(userLogin)){
				listaSoggetti = soggettiCore.soggettiList(null, new ConsoleSearch(true));
			}else{
				listaSoggetti = soggettiCore.soggettiList(userLogin, new ConsoleSearch(true));
			}
			String[] providersList = null;
			String[] providersListLabel = null;

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

			String[] accordiCooperazioneEsistenti=null;
			String[] accordiCooperazioneEsistentiLabel=null;

 
			String postBackElementName = apcHelper.getParameter(Costanti.POSTBACK_ELEMENT_NAME);

			// Controllo se ho modificato il protocollo, resetto il referente e il service binding
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO)){
					strutsBean.referente = null;
					apcHelper.deleteBinaryParameters(strutsBean.wsdlconc,strutsBean.wsdldef,strutsBean.wsdlserv,strutsBean.wsdlservcorr,strutsBean.wsblconc,strutsBean.wsblserv,strutsBean.wsblservcorr);
					apcHelper.deleteProtocolPropertiesBinaryParameters(strutsBean.wsdlconc,strutsBean.wsdldef,strutsBean.wsdlserv,strutsBean.wsdlservcorr,strutsBean.wsblconc,strutsBean.wsblserv,strutsBean.wsblservcorr);
					strutsBean.serviceBinding  = soggettiCore.getDefaultServiceBinding(strutsBean.protocolFactory);
					strutsBean.interfaceType = null;
					strutsBean.messageType = null;
					
					strutsBean.wsdldef = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO);
					strutsBean.wsdlconc = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE);
					strutsBean.wsdlserv = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE);
					strutsBean.wsdlservcorr = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE);

					strutsBean.wsblconc = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE);
					strutsBean.wsblserv = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE);
					strutsBean.wsblservcorr = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE);
					
					filtroRicerca = new FiltroRicercaGruppi();
					filtroRicerca.setServiceBinding(apcCore.fromMessageServiceBinding(strutsBean.serviceBinding));
					elencoGruppi = gruppiCore.getAllGruppi(filtroRicerca);
				}
				else if(postBackElementName.equalsIgnoreCase(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SERVICE_BINDING)){
					strutsBean.interfaceType = null;
					strutsBean.messageType = null;
					
					strutsBean.wsdldef = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO);
					strutsBean.wsdlconc = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE);
					strutsBean.wsdlserv = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE);
					strutsBean.wsdlservcorr = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE);

					strutsBean.wsblconc = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE);
					strutsBean.wsblserv = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE);
					strutsBean.wsblservcorr = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE);
					
					filtroRicerca = new FiltroRicercaGruppi();
					filtroRicerca.setServiceBinding(apcCore.fromMessageServiceBinding(strutsBean.serviceBinding));
					elencoGruppi = gruppiCore.getAllGruppi(filtroRicerca);
				}
			}

			org.openspcoop2.core.registry.Soggetto soggettoReferente = null;
			try{
				soggettoReferente = soggettiCore.getSoggettoRegistro(Long.valueOf(strutsBean.referente));
			}catch(Exception e){
				// ignore
			}
			List<String> tipiSoggettiCompatibili = new ArrayList<>();
			if(soggettoReferente!=null){
				String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggettoReferente.getTipo());
				tipiSoggettiCompatibili = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
			}

			if(strutsBean.isServizioComposto) {
				List<AccordoCooperazione> listaTmp = null;
				if(apcCore.isVisioneOggettiGlobale(userLogin)){
					listaTmp = acCore.accordiCooperazioneList(null, new ConsoleSearch(true));
				}else{
					listaTmp = acCore.accordiCooperazioneList(userLogin, new ConsoleSearch(true));
				}
				List<AccordoCooperazione> listaAccordoCooperazione = new ArrayList<>();
				for (AccordoCooperazione accordoCooperazione : listaTmp) {
					if(accordoCooperazione.getSoggettoReferente()!=null &&
						tipiSoggettiCompatibili!=null && tipiSoggettiCompatibili.contains(accordoCooperazione.getSoggettoReferente().getTipo())){
						listaAccordoCooperazione.add(accordoCooperazione);
					}
				}
				if (listaAccordoCooperazione != null && !listaAccordoCooperazione.isEmpty()) {
					accordiCooperazioneEsistenti = new String[listaAccordoCooperazione.size()+1];
					accordiCooperazioneEsistentiLabel = new String[listaAccordoCooperazione.size()+1];
					int i = 1;
					accordiCooperazioneEsistenti[0]="-";
					accordiCooperazioneEsistentiLabel[0]="-";
					Iterator<AccordoCooperazione> itL = listaAccordoCooperazione.iterator();
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
			
			// message type resta null
			// fromato specifica
			if(strutsBean.interfaceType == null) {
				if(strutsBean.serviceBinding != null) {
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
			}

			// Se nome = null, devo visualizzare la pagina per l'inserimento dati
			String servletNameApcList = (isModalitaVistaApiCustom!=null && isModalitaVistaApiCustom.booleanValue()) ? ApiCostanti.SERVLET_NAME_APC_API_LIST : AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST; 
			Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(strutsBean.tipoAccordo);
			List<Parameter> listaParams = new ArrayList<>();
			listaParams.add(new Parameter(labelAccordoServizio, servletNameApcList, pTipoAccordo));
			listaParams.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI,null));
			
			
			if(ServletUtils.isEditModeInProgress(strutsBean.editMode)){ 

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				if(strutsBean.nome==null){
					strutsBean.nome = "";
					strutsBean.accordoCooperazione = "";
					strutsBean.confric  = "";
					strutsBean.consord = "";
					strutsBean.descr = "";
					strutsBean.filtrodup = "yes";
					strutsBean.idcoll = "";
					strutsBean.idRifRichiesta = "";
					strutsBean.profcoll = "sincrono";
					
					if(strutsBean.serviceBinding != null) {
						switch(strutsBean.serviceBinding) {
						case REST:
							if( strutsBean.protocolFactory.createProtocolConfiguration().isSupportato(strutsBean.serviceBinding, ProfiloDiCollaborazione.ONEWAY)) {
								strutsBean.profcoll = "oneway"; // es. as4
							}
							else {
								strutsBean.profcoll = "sincrono";
							}
							break;
						case SOAP:
						default:
							break;
						}
					}
					
					strutsBean.accordoCooperazione = "-1";
					strutsBean.scadenza= "";
					strutsBean.privato = false;
					if(strutsBean.tipoProtocollo == null){
						strutsBean.tipoProtocollo = apcCore.getProtocolloDefault(request, session, listaTipiProtocollo);
					}
					strutsBean.referente= "";

					if(strutsBean.tipoAccordo!=null){
						if("apc".equals(strutsBean.tipoAccordo)){
							strutsBean.isServizioComposto = false;
						}else if("asc".equals(strutsBean.tipoAccordo)){
							strutsBean.isServizioComposto = true;
						}
					}else{
						strutsBean.isServizioComposto=false;
					}
					if(apcHelper.isShowGestioneWorkflowStatoDocumenti()){
						if(strutsBean.statoPackage==null)
							strutsBean.statoPackage=StatiAccordo.bozza.toString();
					}else{
						strutsBean.statoPackage=StatiAccordo.finale.toString();
					}
					/**if(core.isBackwardCompatibilityAccordo11()){
					//	strutsBean.versione="0";
					//}else{*/
					strutsBean.versione="1";
					
					strutsBean.gruppi = "";
					strutsBean.canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT;
					strutsBean.canale = "";
				}

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				if(strutsBean.tipoAccordo==null || strutsBean.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(strutsBean.consoleConfiguration,
						strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idApc);
				else 
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(strutsBean.consoleConfiguration,
							strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idApc);

				dati = apcHelper.addAccordiToDati(dati, strutsBean.nome, strutsBean.descr, strutsBean.profcoll, strutsBean.wsdldef, strutsBean.wsdlconc, strutsBean.wsdlserv, strutsBean.wsdlservcorr, strutsBean.wsblconc,strutsBean.wsblserv,strutsBean.wsblservcorr,
						strutsBean.filtrodup, strutsBean.confric, strutsBean.idcoll, strutsBean.idRifRichiesta, strutsBean.consord, strutsBean.scadenza, "0", tipoOp, 
						false, true, strutsBean.referente, strutsBean.versione, providersList, providersListLabel, 
						strutsBean.privato, strutsBean.isServizioComposto, accordiCooperazioneEsistenti, accordiCooperazioneEsistentiLabel, 
						strutsBean.accordoCooperazione, strutsBean.statoPackage, strutsBean.statoPackage, strutsBean.tipoAccordo, strutsBean.validazioneDocumenti, 
						strutsBean.tipoProtocollo, listaTipiProtocollo,false,false,strutsBean.protocolFactory,
						strutsBean.serviceBinding,strutsBean.messageType,strutsBean.interfaceType, strutsBean.gruppi, elencoGruppi,
						strutsBean.nuovaVersione, gestioneNuovaVersioneMin, nuovaVersioneRidefinisciInterfaccia, gestioneNuovaVersioneOldIdApc,
						false, strutsBean.canaleStato, strutsBean.canale, canaleList, gestioneCanaliEnabled);

				// aggiunta campi custom
				dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC,
						ForwardParams.ADD());
			}

			boolean visibilitaAccordoCooperazione=false;
			if(!"-".equals(strutsBean.accordoCooperazione) && !"".equals(strutsBean.accordoCooperazione)  && strutsBean.accordoCooperazione!=null){
				AccordoCooperazione ac = acCore.getAccordoCooperazione(Long.parseLong(strutsBean.accordoCooperazione));
				visibilitaAccordoCooperazione=ac.getPrivato()!=null && ac.getPrivato();
			}

			// Controlli sui campi immessi
			boolean isOk = apcHelper.accordiCheckData(tipoOp, strutsBean.nome, strutsBean.descr, strutsBean.profcoll, 
					strutsBean.wsdldef, strutsBean.wsdlconc, strutsBean.wsdlserv, strutsBean.wsdlservcorr, 
					strutsBean.filtrodup, strutsBean.confric, strutsBean.idcoll, strutsBean.idRifRichiesta, strutsBean.consord, 
					strutsBean.scadenza, "0",strutsBean.referente, strutsBean.versione,strutsBean.accordoCooperazione,strutsBean.privato,visibilitaAccordoCooperazione,null,
					strutsBean.wsblconc,strutsBean.wsblserv,strutsBean.wsblservcorr, strutsBean.validazioneDocumenti, strutsBean.tipoProtocollo,null,strutsBean.serviceBinding,strutsBean.messageType,strutsBean.interfaceType,
					showReferente, strutsBean.gruppi, strutsBean.canaleStato, strutsBean.canale, gestioneCanaliEnabled);

			// updateDynamic
			if(isOk) {
				if(strutsBean.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(strutsBean.consoleConfiguration,
						strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idApc);
				else 
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(strutsBean.consoleConfiguration,
							strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idApc);
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
					idApc = apcHelper.getIDAccordoFromValues(strutsBean.nome, strutsBean.referente, strutsBean.versione, visibilitaAccordoCooperazione);
					//validazione campi dinamici
					if(strutsBean.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
						strutsBean.consoleDynamicConfiguration.validateDynamicConfigAccordoServizioParteComune(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
								strutsBean.registryReader, strutsBean.configRegistryReader, idApc);
					else 
						strutsBean.consoleDynamicConfiguration.validateDynamicConfigAccordoServizioComposto(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
								strutsBean.registryReader, strutsBean.configRegistryReader, idApc);
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
						strutsBean.registryReader, strutsBean.configRegistryReader, idApc);
				else 
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(strutsBean.consoleConfiguration,
							strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idApc);

				dati = apcHelper.addAccordiToDati(dati, strutsBean.nome, strutsBean.descr, strutsBean.profcoll, strutsBean.wsdldef, strutsBean.wsdlconc, strutsBean.wsdlserv, strutsBean.wsdlservcorr, strutsBean.wsblconc,strutsBean.wsblserv,strutsBean.wsblservcorr,
						strutsBean.filtrodup, strutsBean.confric, strutsBean.idcoll, strutsBean.idRifRichiesta, strutsBean.consord, strutsBean.scadenza, "0", tipoOp, 
						false, true,strutsBean.referente, strutsBean.versione, providersList, providersListLabel,
						strutsBean.privato, strutsBean.isServizioComposto, accordiCooperazioneEsistenti, accordiCooperazioneEsistentiLabel, 
						strutsBean.accordoCooperazione, strutsBean.statoPackage, strutsBean.statoPackage, strutsBean.tipoAccordo, strutsBean.validazioneDocumenti, 
						strutsBean.tipoProtocollo, listaTipiProtocollo,false,false,strutsBean.protocolFactory,
						strutsBean.serviceBinding,strutsBean.messageType,strutsBean.interfaceType, strutsBean.gruppi, elencoGruppi,
						strutsBean.nuovaVersione, gestioneNuovaVersioneMin, nuovaVersioneRidefinisciInterfaccia, gestioneNuovaVersioneOldIdApc,
						false, strutsBean.canaleStato, strutsBean.canale, canaleList, gestioneCanaliEnabled);

				// aggiunta campi custom
				dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.ADD());
			}
			
			List<Object> objectToCreate = new ArrayList<>();

			AccordoServizioParteComune as = new AccordoServizioParteComune();

			// preparo l'oggetto
			as.setNome(strutsBean.nome);
			as.setDescrizione(strutsBean.descr);

			// profilo collaborazione
			// controllo profilo collaborazione
			strutsBean.profcoll = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneView2DB(strutsBean.profcoll);
			as.setProfiloCollaborazione(ProfiloCollaborazione.toEnumConstant(strutsBean.profcoll));

			FormatoSpecifica formato = apcCore.interfaceType2FormatoSpecifica(strutsBean.interfaceType);
			
			String wsdlconcS = strutsBean.wsdlconc.getValue() != null ? new String(strutsBean.wsdlconc.getValue()) : null; 
			as.setByteWsdlConcettuale(apcCore.getInterfaceAsByteArray(formato, wsdlconcS));
			String wsdldefS = strutsBean.wsdldef.getValue() != null ? new String(strutsBean.wsdldef.getValue()) : null; 
			as.setByteWsdlDefinitorio(apcCore.getInterfaceAsByteArray(formato, wsdldefS));
			String wsdlservS = strutsBean.wsdlserv.getValue() != null ? new String(strutsBean.wsdlserv.getValue()) : null; 
			as.setByteWsdlLogicoErogatore(apcCore.getInterfaceAsByteArray(formato, wsdlservS));
			String wsdlservcorrS = strutsBean.wsdlservcorr.getValue() != null ? new String(strutsBean.wsdlservcorr.getValue()) : null; 
			as.setByteWsdlLogicoFruitore(apcCore.getInterfaceAsByteArray(formato, wsdlservcorrS));

			// Se un utente ha impostato solo il logico erogatore (avviene automaticamente nel caso non venga visualizzato il campo concettuale)
			// imposto lo stesso wsdl anche per il concettuale. Tanto Rappresenta la stessa informazione, ma e' utile per lo stato dell'accordo
			boolean facilityUnicoWSDLInterfacciaStandard = false;
			if(as.getByteWsdlLogicoErogatore()!=null && as.getByteWsdlLogicoFruitore()==null && as.getByteWsdlConcettuale()==null){
				as.setByteWsdlConcettuale(as.getByteWsdlLogicoErogatore());
				facilityUnicoWSDLInterfacciaStandard = true;
			}

			// Conversione Abilitato/Disabilitato
			as.setFiltroDuplicati(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(strutsBean.filtrodup)));
			as.setConfermaRicezione(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(strutsBean.confric)));
			as.setConsegnaInOrdine(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(strutsBean.consord)));
			as.setIdCollaborazione(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(strutsBean.idcoll)));
			as.setIdRiferimentoRichiesta(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(strutsBean.idRifRichiesta)));
			if ((strutsBean.scadenza != null) && (!"".equals(strutsBean.scadenza)))
				as.setScadenza(strutsBean.scadenza);
			as.setSuperUser(userLogin);
			as.setUtilizzoSenzaAzione(true);// default true
			String wsblconcS = strutsBean.wsblconc.getValue() != null ? new String(strutsBean.wsblconc.getValue()) : null; 
			as.setByteSpecificaConversazioneConcettuale(apcCore.getInterfaceAsByteArray(formato, wsblconcS));
			String wsblservS = strutsBean.wsblserv.getValue() != null ? new String(strutsBean.wsblserv.getValue()) : null;
			as.setByteSpecificaConversazioneErogatore(apcCore.getInterfaceAsByteArray(formato, wsblservS));
			String wsblservcorrS = strutsBean.wsblservcorr.getValue() != null ? new String(strutsBean.wsblservcorr.getValue()) : null;
			as.setByteSpecificaConversazioneFruitore(apcCore.getInterfaceAsByteArray(formato, wsblservcorrS));
			
			// servicebinding / messagetype / formatospecifica
			as.setServiceBinding(apcCore.fromMessageServiceBinding(strutsBean.serviceBinding));
			as.setMessageType(apcCore.fromMessageMessageType(strutsBean.messageType));
			as.setFormatoSpecifica(formato);
			
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
				if(!showReferente) {
					// Recupero Soggetto Default
					IDSoggetto idSoggetto = apcCore.getSoggettoOperativoDefault(userLogin, strutsBean.tipoProtocollo);
					Soggetto s = soggettiCore.getSoggetto(idSoggetto);
					IdSoggetto assr = new IdSoggetto();
					assr.setTipo(s.getTipo());
					assr.setNome(s.getNome());
					as.setSoggettoReferente(assr);
				}
				else {
					as.setSoggettoReferente(null);
				}
			}
			if(strutsBean.versione!=null){
				as.setVersione(Integer.parseInt(strutsBean.versione));
			}
			as.setPrivato(strutsBean.privato ? Boolean.TRUE : Boolean.FALSE);

			if(strutsBean.accordoCooperazione!=null && !"".equals(strutsBean.accordoCooperazione) && !"-".equals(strutsBean.accordoCooperazione)){
				AccordoServizioParteComuneServizioComposto assc = new AccordoServizioParteComuneServizioComposto();
				assc.setIdAccordoCooperazione(Long.parseLong(strutsBean.accordoCooperazione));
				as.setServizioComposto(assc);
			}

			// stato
			as.setStatoPackage(strutsBean.statoPackage);

			// gruppi
			if(StringUtils.isNotEmpty(strutsBean.gruppi)) {
				if(as.getGruppi() == null)
					as.setGruppi(new GruppiAccordo());
			
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

			if(strutsBean.nuovaVersione && !nuovaVersioneRidefinisciInterfaccia) {
				IDAccordo idAcc = apcCore.getIdAccordoServizio(gestioneNuovaVersioneOldIdApc);
				AccordoServizioParteComune aspcOld = apcCore.getAccordoServizioFull(idAcc, true); // devo leggere anche gli allegati
				as.setAllegatoList(aspcOld.getAllegatoList());
				as.setSpecificaSemiformaleList(aspcOld.getSpecificaSemiformaleList());
				as.setAzioneList(aspcOld.getAzioneList());
				as.setPortTypeList(aspcOld.getPortTypeList());
				as.setResourceList(aspcOld.getResourceList());
				as.setByteWsdlDefinitorio(aspcOld.getByteWsdlDefinitorio());
				as.setByteWsdlConcettuale(aspcOld.getByteWsdlConcettuale());
				as.setByteWsdlLogicoErogatore(aspcOld.getByteWsdlLogicoErogatore());
				as.setByteWsdlLogicoFruitore(aspcOld.getByteWsdlLogicoFruitore());
				as.setByteSpecificaConversazioneConcettuale(aspcOld.getByteSpecificaConversazioneConcettuale());
				as.setByteSpecificaConversazioneErogatore(aspcOld.getByteSpecificaConversazioneErogatore());
				as.setByteSpecificaConversazioneFruitore(aspcOld.getByteSpecificaConversazioneFruitore());
			}
			

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
							strutsBean.registryReader, strutsBean.configRegistryReader, idApc);
					else 
						strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(strutsBean.consoleConfiguration,
								strutsBean.consoleOperationType, apcHelper, strutsBean.protocolProperties, 
								strutsBean.registryReader, strutsBean.configRegistryReader, idApc);

					dati = apcHelper.addAccordiToDati(dati, strutsBean.nome, strutsBean.descr, strutsBean.profcoll, strutsBean.wsdldef, strutsBean.wsdlconc, strutsBean.wsdlserv, strutsBean.wsdlservcorr, strutsBean.wsblconc,strutsBean.wsblserv,strutsBean.wsblservcorr,
							strutsBean.filtrodup, strutsBean.confric, strutsBean.idcoll, strutsBean.idRifRichiesta, strutsBean.consord, strutsBean.scadenza, "0", tipoOp, 
							false, true,strutsBean.referente, strutsBean.versione, providersList, providersListLabel,
							strutsBean.privato, strutsBean.isServizioComposto, accordiCooperazioneEsistenti, accordiCooperazioneEsistentiLabel, 
							strutsBean.accordoCooperazione, strutsBean.statoPackage, strutsBean.statoPackage, strutsBean.tipoAccordo, strutsBean.validazioneDocumenti, 
							strutsBean.tipoProtocollo, listaTipiProtocollo,false,false,strutsBean.protocolFactory,
							strutsBean.serviceBinding,strutsBean.messageType,strutsBean.interfaceType, strutsBean.gruppi, elencoGruppi,
							strutsBean.nuovaVersione, gestioneNuovaVersioneMin, nuovaVersioneRidefinisciInterfaccia, gestioneNuovaVersioneOldIdApc,
							false, strutsBean.canaleStato, strutsBean.canale, canaleList, gestioneCanaliEnabled);

					// aggiunta campi custom
					dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties);

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.ADD());

				}
			}

			// Automapping
			if(!strutsBean.nuovaVersione || nuovaVersioneRidefinisciInterfaccia) {
				AccordiServizioParteComuneUtilities.mapppingAutomaticoInterfaccia(as, apcCore, 
						enableAutoMapping, strutsBean.validazioneDocumenti, enableAutoMappingEstraiXsdSchemiFromWsdlTypes, facilityUnicoWSDLInterfacciaStandard, 
						strutsBean.tipoProtocollo, strutsBean.interfaceType);
			}

			//imposto properties custom
			as.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(strutsBean.protocolProperties, strutsBean.consoleOperationType,null));
			
			objectToCreate.add(as);
			// effettuo le operazioni
			apcCore.performCreateOperation(userLogin, apcHelper.smista(), objectToCreate.toArray(new Object[objectToCreate.size()]));
			
			// cancello i file temporanei
			apcHelper.deleteBinaryParameters(strutsBean.wsdlconc,strutsBean.wsdldef,strutsBean.wsdlserv,strutsBean.wsdlservcorr,strutsBean.wsblconc,strutsBean.wsblserv,strutsBean.wsblservcorr);
			apcHelper.deleteBinaryProtocolPropertiesTmpFiles(strutsBean.protocolProperties);

			// Verifico stato
			boolean incomplete = apcHelper.setMessageWarningStatoConsistenzaAccordo(true, as);
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			if(incomplete || apcCore.isSetSearchAfterAdd()) {
				apcCore.setSearchAfterAdd(Liste.ACCORDI, as.getNome(), request, session, ricerca);
			}
			
			List<AccordoServizioParteComuneSintetico> lista = AccordiServizioParteComuneUtilities.accordiList(apcCore, userLogin, ricerca, strutsBean.tipoAccordo);

			if(isModalitaVistaApiCustom!=null && isModalitaVistaApiCustom.booleanValue()) {
				apcHelper.prepareApiList(lista, ricerca, strutsBean.tipoAccordo); 
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				return ServletUtils.getStrutsForwardEditModeFinished(mapping, ApiCostanti.OBJECT_NAME_APC_API, ForwardParams.ADD());
			}
			
			apcHelper.prepareAccordiList(lista, ricerca, strutsBean.tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.ADD());
		} 

	}
}
