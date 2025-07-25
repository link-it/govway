/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import java.util.ArrayList;
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
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.constants.BindingStyle;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
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
 * accordiPorttypeChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComunePortTypesChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		// Inizializzo GeneralData
		GeneralHelper generalHelper = new GeneralHelper(session);

		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();

		// Protocol Properties
		IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
		ConsoleConfiguration consoleConfiguration =null;
		ProtocolProperties protocolProperties = null;
		IProtocolFactory<?> protocolFactory= null;
		IRegistryReader registryReader = null; 
		IConfigIntegrationReader configRegistryReader = null; 
		ConsoleOperationType consoleOperationType = null;
		
		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		consoleOperationType = ConsoleOperationType.CHANGE;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.CHANGE;
		List<ProtocolProperty> oldProtocolPropertyList = null;

		try {
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);
			
			// Preparo il menu
			apcHelper.makeMenu();
			
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			SoggettiCore soggettiCore = new SoggettiCore(apcCore);
			
			String editMode = apcHelper.getParametroEditMode(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			String protocolPropertiesSet = apcHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_SET);
			
			String id = apcHelper.getParametroLong(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			long idAccordoLong = Long.parseLong(id);
			String nomept = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME);
			if (nomept == null) {
				nomept = "";
			}
			String descr = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_DESCRIZIONE);
			
			String messageProcessorS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_MESSAGE_TYPE);
			MessageType messageType = (StringUtils.isNotEmpty(messageProcessorS) && !messageProcessorS.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_DEFAULT)) ? MessageType.valueOf(messageProcessorS) : null;
					
			String profProtocollo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_PROFILO_BUSTA);
			String profcollpt = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_PROFILO_COLLABORAZIONE);
			if(profcollpt == null)
				profcollpt = "";

			String filtroduppt = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_FILTRO_DUPLICATI);
			if ((filtroduppt != null) && filtroduppt.equals("null")) {
				filtroduppt = null;
			}
			String confricpt = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_CONFERMA_RICEZIONE);
			if ((confricpt != null) && confricpt.equals("null")) {
				confricpt = null;
			}
			String idcollpt = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_COLLABORAZIONE);
			if ((idcollpt != null) && idcollpt.equals("null")) {
				idcollpt = null;
			}
			String idRifRichiestaPt = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_ID_RIFERIMENTO_RICHIESTA);
			if ((idRifRichiestaPt != null) && idRifRichiestaPt.equals("null")) {
				idRifRichiestaPt = null;
			}
			String consordpt = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_CONSEGNA_ORDINE);
			if ((consordpt != null) && consordpt.equals("null")) {
				consordpt = null;
			}
			String scadenzapt = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_SCADENZA);
			if (scadenzapt == null) {
				scadenzapt = "";
			}

			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;

			String servizioStyle = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_STYLE);

			// Prendo il nome
			AccordoServizioParteComune as = apcCore.getAccordoServizioFull(idAccordoLong);
			String labelASTitle = apcHelper.getLabelIdAccordo(as); 
			IDAccordo idAs = idAccordoFactory.getIDAccordoFromAccordo(as);

			String protocollo = null;
			//calcolo del protocollo implementato dall'accordo
			IdSoggetto soggettoReferente = as.getSoggettoReferente();
			String tipoSoggettoReferente = soggettoReferente.getTipo();
			protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggettoReferente);

			protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			consoleDynamicConfiguration =  protocolFactory.createDynamicConfigurationConsole();
			registryReader = soggettiCore.getRegistryReader(protocolFactory); 
			configRegistryReader = soggettiCore.getConfigIntegrationReader(protocolFactory);
			IDPortType idPt = new IDPortType();
			idPt.setIdAccordo(idAs);
			idPt.setNome(nomept);
			consoleConfiguration = consoleDynamicConfiguration.getDynamicConfigPortType(consoleOperationType, apcHelper, 
					registryReader, configRegistryReader, idPt);
			protocolProperties = apcHelper.estraiProtocolPropertiesDaRequest(consoleConfiguration, consoleOperationType);

			PortType ptOLD = null;

			for (int i = 0; i < as.sizePortTypeList(); i++) {
				PortType pt = as.getPortType(i);
				if (nomept.equals(pt.getNome())) {
					ptOLD = pt;
					break;
				}
			}

			if(ptOLD != null)
				oldProtocolPropertyList = ptOLD.getProtocolPropertyList();

			if(protocolPropertiesSet == null){
				ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(protocolProperties, oldProtocolPropertyList, consoleOperationType);
			}

			Properties propertiesProprietario = new Properties();
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, id);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_PORT_TYPE);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, nomept);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE,
					URLEncoder.encode( AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPES_CHANGE + "?" + request.getQueryString(), "UTF-8"));
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, protocollo);
			if(tipoAccordo!=null) {
				propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO, tipoAccordo);
			}
			
			
			Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo);
			Parameter pIdAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id);
			Boolean isModalitaVistaApiCustom = ServletUtils.getBooleanAttributeFromSession(ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API, session, request, false).getValue();
			List<Parameter> listaParams = apcHelper.getTitoloApc(TipoOperazione.CHANGE, as, tipoAccordo, labelASTitle, null, false);
			
			String labelPortTypes = (isModalitaVistaApiCustom!=null && isModalitaVistaApiCustom.booleanValue()) ? AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES : AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES + " di " + labelASTitle;
		 	listaParams.add(new Parameter(labelPortTypes, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPES_LIST, pIdAccordo, pTipoAccordo));
		 	listaParams.add(new Parameter(nomept, null));

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if(ServletUtils.isEditModeInProgress(editMode)){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				// Prendo i dati dell'accordo
				String deffiltroduppt = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati());
				String defconfricpt = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione());
				String defidcollpt = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione());
				String defIdRifRichiestaPt = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdRiferimentoRichiesta());
				String defconsordpt = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine());
				String defscadenzapt = as.getScadenza();
				String defprofcollpt = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(as.getProfiloCollaborazione());

				if(ptOLD != null){
					filtroduppt = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(ptOLD.getFiltroDuplicati());
					confricpt = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(ptOLD.getConfermaRicezione());
					idcollpt = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(ptOLD.getIdCollaborazione());
					idRifRichiestaPt = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(ptOLD.getIdRiferimentoRichiesta());
					consordpt = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(ptOLD.getConsegnaInOrdine());
					scadenzapt = ptOLD.getScadenza();
					profcollpt = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(ptOLD.getProfiloCollaborazione());
					if (profProtocollo == null) {
						profProtocollo = ptOLD.getProfiloPT();
					}
					descr = ptOLD.getDescrizione();
					messageType = apcCore.toMessageMessageType(ptOLD.getMessageType());
					servizioStyle = ptOLD.getStyle() != null ? ptOLD.getStyle().getValue() : AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPES_STYLE;
				}

				if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
					filtroduppt = deffiltroduppt;
					confricpt = defconfricpt;
					idcollpt = defidcollpt;
					idRifRichiestaPt = defIdRifRichiestaPt;
					consordpt = defconsordpt;
					scadenzapt = defscadenzapt;
					profcollpt = defprofcollpt;
				}

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				consoleDynamicConfiguration.updateDynamicConfigPortType(consoleConfiguration, consoleOperationType, apcHelper, protocolProperties, 
						registryReader, configRegistryReader, idPt);

				dati = apcHelper.addAccordiPorttypeToDati(dati, id, nomept, profProtocollo, 
						filtroduppt, deffiltroduppt, confricpt, defconfricpt, idcollpt, defidcollpt, idRifRichiestaPt, defIdRifRichiestaPt, consordpt, defconsordpt, scadenzapt, 
						defscadenzapt, tipoOp, defprofcollpt, profcollpt, descr, as.getStatoPackage(),
						tipoAccordo,protocollo,servizioStyle,apcCore.toMessageServiceBinding(as.getServiceBinding()),
						protocolFactory, messageType);

				// aggiunta campi custom
				dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, consoleConfiguration,consoleOperationType, protocolProperties,oldProtocolPropertyList,propertiesProprietario);

				pd.setDati(dati);

				if(apcHelper.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(as.getStatoPackage())){
					pd.disableEditMode();
				}

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPES, ForwardParams.CHANGE());

			}

			// Controlli sui campi immessi
			boolean isOk = apcHelper.accordiPorttypeCheckData(tipoOp, id, nomept, descr, profProtocollo, filtroduppt, confricpt, idcollpt, idRifRichiestaPt, consordpt, scadenzapt);

			// updateDynamic
			if(isOk) {
				consoleDynamicConfiguration.updateDynamicConfigPortType(consoleConfiguration, consoleOperationType, apcHelper, protocolProperties, 
						registryReader, configRegistryReader, idPt);
			}
			
			// Validazione base dei parametri custom 
			if(isOk){
				try{
					apcHelper.validaProtocolProperties(consoleConfiguration, consoleOperationType, protocolProperties);
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
					consoleDynamicConfiguration.validateDynamicConfigPortType(consoleConfiguration, consoleOperationType, apcHelper, protocolProperties, 
							registryReader, configRegistryReader, idPt);
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

				// update della configurazione 
				consoleDynamicConfiguration.updateDynamicConfigPortType(consoleConfiguration, consoleOperationType, apcHelper, protocolProperties, 
						registryReader, configRegistryReader, idPt);

				dati = apcHelper.addAccordiPorttypeToDati(dati, id, nomept, profProtocollo, 
						filtroduppt, filtroduppt, confricpt, confricpt, idcollpt, idcollpt, idRifRichiestaPt, idRifRichiestaPt, consordpt, consordpt, scadenzapt, scadenzapt, 
						tipoOp, profcollpt, profcollpt, descr, as.getStatoPackage(),
						tipoAccordo,protocollo,servizioStyle,apcCore.toMessageServiceBinding(as.getServiceBinding()),
						protocolFactory, messageType);

				// aggiunta campi custom
				dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, consoleConfiguration,consoleOperationType, protocolProperties,oldProtocolPropertyList,propertiesProprietario);


				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPES, ForwardParams.CHANGE());
			}

			// Modifico i dati del port-type nel db
			if(ServletUtils.isCheckBoxEnabled(filtroduppt)){
				filtroduppt = CostantiRegistroServizi.ABILITATO.toString();
			} else {
				filtroduppt = CostantiRegistroServizi.DISABILITATO.toString();
			}
			if(ServletUtils.isCheckBoxEnabled(confricpt)){
				confricpt = CostantiRegistroServizi.ABILITATO.toString();
			} else {
				confricpt = CostantiRegistroServizi.DISABILITATO.toString();
			}
			if(ServletUtils.isCheckBoxEnabled(idcollpt)){
				idcollpt = CostantiRegistroServizi.ABILITATO.toString();
			} else {
				idcollpt = CostantiRegistroServizi.DISABILITATO.toString();
			}
			if(ServletUtils.isCheckBoxEnabled(idRifRichiestaPt)){
				idRifRichiestaPt = CostantiRegistroServizi.ABILITATO.toString();
			} else {
				idRifRichiestaPt = CostantiRegistroServizi.DISABILITATO.toString();
			}
			if(ServletUtils.isCheckBoxEnabled(consordpt)){
				consordpt = CostantiRegistroServizi.ABILITATO.toString();
			} else {
				consordpt = CostantiRegistroServizi.DISABILITATO.toString();
			}

			PortType oldPt = null;
			for (int i = 0; i < as.sizePortTypeList(); i++) {
				PortType pt = as.getPortType(i);
				if (nomept.equals(pt.getNome())) {
					oldPt = pt;
					as.removePortType(i);
					break;
				}
			}

			PortType newPT = new PortType();
			newPT.setNome(nomept);
			newPT.setDescrizione(descr);
			newPT.setMessageType(apcCore.fromMessageMessageType(messageType));
			if(ServletUtils.isCheckBoxEnabled(filtroduppt)){
				newPT.setFiltroDuplicati(CostantiRegistroServizi.ABILITATO);
			} else {
				newPT.setFiltroDuplicati(CostantiRegistroServizi.DISABILITATO);
			}
			if(ServletUtils.isCheckBoxEnabled(confricpt)){
				newPT.setConfermaRicezione(CostantiRegistroServizi.ABILITATO);
			} else {
				newPT.setConfermaRicezione(CostantiRegistroServizi.DISABILITATO);
			}
			if(ServletUtils.isCheckBoxEnabled(idcollpt)){
				newPT.setIdCollaborazione(CostantiRegistroServizi.ABILITATO);
			} else {
				newPT.setIdCollaborazione(CostantiRegistroServizi.DISABILITATO);
			}
			if(ServletUtils.isCheckBoxEnabled(idRifRichiestaPt)){
				newPT.setIdRiferimentoRichiesta(CostantiRegistroServizi.ABILITATO);
			} else {
				newPT.setIdRiferimentoRichiesta(CostantiRegistroServizi.DISABILITATO);
			}
			if(ServletUtils.isCheckBoxEnabled(consordpt)){
				newPT.setConsegnaInOrdine(CostantiRegistroServizi.ABILITATO);
			} else {
				newPT.setConsegnaInOrdine(CostantiRegistroServizi.DISABILITATO);
			}
			if ((scadenzapt != null) && (!"".equals(scadenzapt))) {
				newPT.setScadenza(scadenzapt);
			}
			newPT.setProfiloPT(profProtocollo);
			newPT.setProfiloCollaborazione(ProfiloCollaborazione.toEnumConstant(profcollpt));

			// ricopio le operation che aveva quel porttye
			if(oldPt!=null) {
				newPT.setAzioneList(oldPt.getAzioneList());
			}

			// style
			BindingStyle style = BindingStyle.toEnumConstant(servizioStyle);
			newPT.setStyle(style);

			as.addPortType(newPT);
			
			//imposto properties custom
			newPT.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(protocolProperties, consoleOperationType, oldProtocolPropertyList));

			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			List<PortType> lista = apcCore.accordiPorttypeList(idAccordoLong, ricerca);

			// Devo rileggere l'accordo dal db, perche' altrimenti
			// manca l'id dei nuovi port-type
			as = apcCore.getAccordoServizioFull(idAccordoLong);

			apcHelper.prepareAccordiPorttypeList(id,as, lista, ricerca,tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPES, ForwardParams.CHANGE());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPES, ForwardParams.CHANGE());
		} 
	}
}
