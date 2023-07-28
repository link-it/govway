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
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.HttpMethod;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.archive.APIUtils;
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
 * accordiPorttypeAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneResourcesAdd extends Action {

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

		// Protocol Properties
		IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
		ConsoleConfiguration consoleConfiguration =null;
		ProtocolProperties protocolProperties = null;
		IProtocolFactory<?> protocolFactory= null;
		IRegistryReader registryReader = null; 
		IConfigIntegrationReader configRegistryReader = null; 
		ConsoleOperationType consoleOperationType = null;
		
		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		consoleOperationType = ConsoleOperationType.ADD;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.ADD; 


		try {
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			SoggettiCore soggettiCore = new SoggettiCore(apcCore);

			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);
			
			String editMode = apcHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);

			String id = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			long idAccordoLong = Long.parseLong(id);
			String nomeRisorsa = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_NOME);
			if (nomeRisorsa == null) {
				nomeRisorsa = "";
			}
			String descr = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_DESCRIZIONE);
			if (descr == null) {
				descr = "";
			}
			
			String messageProcessorS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_MESSAGE_TYPE);
			MessageType messageType = (StringUtils.isNotEmpty(messageProcessorS) && !messageProcessorS.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_DEFAULT)) ? MessageType.valueOf(messageProcessorS) : null;
		
			String messageProcessorReqS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_MESSAGE_TYPE_REQUEST);
			MessageType messageTypeRequest = (StringUtils.isNotEmpty(messageProcessorReqS) && !messageProcessorReqS.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_DEFAULT)) ? MessageType.valueOf(messageProcessorReqS) : null;
			
			String messageProcessorResS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_MESSAGE_TYPE_RESPONSE);
			MessageType messageTypeResponse = (StringUtils.isNotEmpty(messageProcessorResS) && !messageProcessorResS.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_DEFAULT)) ? MessageType.valueOf(messageProcessorResS) : null;
		
			String path = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_PATH);
			if (path == null) {
				path = "";
			}
			String httpMethod = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_HTTP_METHOD); 
			
			String profProtocollo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_PROFILO_BUSTA);
			String filtrodupaz = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_FILTRO_DUPLICATI);
			if ((filtrodupaz != null) && filtrodupaz.equals("null")) {
				filtrodupaz = null;
			}
			String confricaz = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_CONFERMA_RICEZIONE);
			if ((confricaz != null) && confricaz.equals("null")) {
				confricaz = null;
			}
			String idcollaz = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_COLLABORAZIONE);
			if ((idcollaz != null) && idcollaz.equals("null")) {
				idcollaz = null;
			}
			String idRifRichiestaAz = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_ID_RIFERIMENTO_RICHIESTA);
			if ((idRifRichiestaAz != null) && idRifRichiestaAz.equals("null")) {
				idRifRichiestaAz = null;
			}
			String consordaz = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_CONSEGNA_ORDINE);
			if ((consordaz != null) && consordaz.equals("null")) {
				consordaz = null;
			}
			String scadenzaaz = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_SCADENZA);
			if (scadenzaaz == null) {
				scadenzaaz = "";
			}
			
			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;

			// Preparo il menu
			apcHelper.makeMenu();

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
			
			ServiceBinding serviceBinding = null;
			//calcolo del serviceBinding dall'accordo
			serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());

			IDResource idRisorsa = new IDResource();
			idRisorsa.setIdAccordo(idAs);
			idRisorsa.setNome(nomeRisorsa);
			consoleConfiguration = consoleDynamicConfiguration.getDynamicConfigResource(consoleOperationType, apcHelper, 
					registryReader, configRegistryReader, idRisorsa, httpMethod, path );
			protocolProperties = apcHelper.estraiProtocolPropertiesDaRequest(consoleConfiguration, consoleOperationType);
			
			String uriAS = idAccordoFactory.getUriFromAccordo(as);
			Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo);
			Parameter pIdAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id );
			Parameter pNomeAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, uriAS);

			Boolean isModalitaVistaApiCustom = ServletUtils.getBooleanAttributeFromSession(ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API, session, request, false).getValue();
			List<Parameter> listaParams = apcHelper.getTitoloApc(TipoOperazione.ADD, as, tipoAccordo, labelASTitle, null, false);
			
			String labelRisorse = (isModalitaVistaApiCustom!=null && isModalitaVistaApiCustom.booleanValue()) ? AccordiServizioParteComuneCostanti.LABEL_RISORSE : AccordiServizioParteComuneCostanti.LABEL_RISORSE + " di " + labelASTitle;
			listaParams.add(new Parameter(labelRisorse, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_RESOURCES_LIST, pIdAccordo, pNomeAccordo, pTipoAccordo));
			listaParams.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));


			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if(ServletUtils.isEditModeInProgress(editMode)){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				if (profProtocollo == null) {
					profProtocollo = AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT;
				}

				// Prende i default stabiliti nell'accordo relativo
				filtrodupaz = filtrodupaz != null && !"".equals(filtrodupaz) ? filtrodupaz : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati());
				confricaz = confricaz != null && !"".equals(confricaz) ? confricaz : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione());
				idcollaz = idcollaz != null && !"".equals(idcollaz) ? idcollaz : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione());
				idRifRichiestaAz = idRifRichiestaAz != null && !"".equals(idRifRichiestaAz) ? idRifRichiestaAz : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdRiferimentoRichiesta());
				consordaz = consordaz != null && !"".equals(consordaz) ? consordaz : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine());
				scadenzaaz = scadenzaaz != null && !"".equals(scadenzaaz) ? scadenzaaz : as.getScadenza();
				
				
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				consoleDynamicConfiguration.updateDynamicConfigResource(consoleConfiguration, consoleOperationType, apcHelper, protocolProperties, 
						registryReader, configRegistryReader, idRisorsa, httpMethod, path);

				dati = apcHelper.addAccordiResourceToDati(tipoOp, dati, id, null, nomeRisorsa, descr, path,httpMethod, messageType,  as.getStatoPackage(),tipoAccordo,protocollo, 
						protocolFactory,serviceBinding,messageTypeRequest,messageTypeResponse,
						profProtocollo, 
						filtrodupaz, filtrodupaz, confricaz, confricaz, idcollaz, idcollaz, idRifRichiestaAz, idRifRichiestaAz, consordaz, consordaz, scadenzaaz, scadenzaaz, false);

				// aggiunta campi custom
				dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, consoleConfiguration,consoleOperationType, protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES, ForwardParams.ADD());
			}

			if (profProtocollo == null) {
				profProtocollo = AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT;
			}
			
			boolean isOk = true;
			
			// controllo valori method e path
			if(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_HTTP_METHOD_QUALSIASI.equals(httpMethod) || "".equals(httpMethod)) {
				httpMethod = null;
			}
			
			// 1. se il path non inizia per '/' aggiungo all'inizio della stringa
			String pathNormalizzato = path;
			if(pathNormalizzato!=null && !"".equals(pathNormalizzato)) {
				pathNormalizzato = apcHelper.normalizePathEmpty(path);
			}
			if(pathNormalizzato!=null && !"".equals(pathNormalizzato)) {
				pathNormalizzato = pathNormalizzato.trim();
				if(!pathNormalizzato.startsWith("/")) {
					pathNormalizzato = "/" + pathNormalizzato;
				}
				if(pathNormalizzato.length()>1 && pathNormalizzato.endsWith("/")) {
					pathNormalizzato = pathNormalizzato.substring(0, pathNormalizzato.length()-1);
				}
			}
			
			// 2. se il nome non e; stato impostato allora genero un nome automatico
			String nomeRisorsaProposto = nomeRisorsa;
			if(StringUtils.isEmpty(nomeRisorsaProposto)) {
				boolean httpMethodAndPathQualsiasi = apcCore.isApiResourceHttpMethodAndPathQualsiasiEnabled();
				if(httpMethodAndPathQualsiasi) {
					if(pathNormalizzato==null || "".equals(pathNormalizzato)) {
						pd.setMessage("Il campo '"+AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_NOME+"' deve essere indicato se il campo '"+AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_RESOURCES_PATH+"' è definito come qualsiasi");
						isOk = false;
					}
					else {
						nomeRisorsaProposto = APIUtils.normalizeResourceName(HttpMethod.toEnumConstant(httpMethod), pathNormalizzato);
					}
				}
				else {
					if(httpMethod==null) {
						pd.setMessage("Il campo '"+AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_NOME+"' non è stato definito");
						isOk = false;
					}
					else {
						nomeRisorsaProposto = APIUtils.normalizeResourceName(HttpMethod.toEnumConstant(httpMethod), pathNormalizzato);
					}
				}
			}
			
			// Controlli sui campi immessi
			if(isOk){
				isOk = apcHelper.accordiResourceCheckData(tipoOp, id, nomeRisorsa, nomeRisorsaProposto, pathNormalizzato, httpMethod, descr, messageType, null,null,null,null,
						profProtocollo, filtrodupaz, confricaz, idcollaz, idRifRichiestaAz, consordaz, scadenzaaz);
			}

			// updateDynamic
			if(isOk) {
				consoleDynamicConfiguration.updateDynamicConfigResource(consoleConfiguration, consoleOperationType, apcHelper, protocolProperties, 
						registryReader, configRegistryReader, idRisorsa, httpMethod, path);
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
					consoleDynamicConfiguration.validateDynamicConfigResource(consoleConfiguration, consoleOperationType, apcHelper, protocolProperties, 
							registryReader, configRegistryReader, idRisorsa, httpMethod, path);
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

				consoleDynamicConfiguration.updateDynamicConfigResource(consoleConfiguration, consoleOperationType, apcHelper, protocolProperties, 
						registryReader, configRegistryReader, idRisorsa, httpMethod, path);

				dati = apcHelper.addAccordiResourceToDati(tipoOp, dati, id, null, nomeRisorsa, descr,path,httpMethod, messageType, as.getStatoPackage(),tipoAccordo,protocollo, protocolFactory,serviceBinding,messageTypeRequest,messageTypeResponse,
						profProtocollo, 
						filtrodupaz, filtrodupaz, confricaz, confricaz, idcollaz, idcollaz, idRifRichiestaAz, idRifRichiestaAz, consordaz, consordaz, scadenzaaz, scadenzaaz, false);

				// aggiunta campi custom
				dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, consoleConfiguration,consoleOperationType, protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES, ForwardParams.ADD());
			}

			// Inserisco la risorsa nel db

			Resource newRes = new Resource();
			newRes.setNome(nomeRisorsaProposto);
			newRes.setDescrizione(descr);
			newRes.setPath("".equals(pathNormalizzato) ? null : pathNormalizzato);
			newRes.setMethodRawEnumValue("".equals(httpMethod) ? null : httpMethod);
			newRes.setMessageType(apcCore.fromMessageMessageType(messageType));
			newRes.setRequestMessageType(apcCore.fromMessageMessageType(messageTypeRequest));
			newRes.setResponseMessageType(apcCore.fromMessageMessageType(messageTypeResponse));
			
			newRes.setFiltroDuplicati(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(filtrodupaz)));
			newRes.setConfermaRicezione(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(confricaz)));
			newRes.setIdCollaborazione(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(idcollaz)));
			newRes.setIdRiferimentoRichiesta(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(idRifRichiestaAz)));
			newRes.setConsegnaInOrdine(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(consordaz)));
			newRes.setScadenza(scadenzaaz);
			newRes.setProfAzione(profProtocollo.equals(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT) ? profProtocollo : CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
		
			as.addResource(newRes);
			
			//imposto properties custom
			newRes.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(protocolProperties, consoleOperationType,null));

			AccordiServizioParteComuneUtilities.createResource(apcCore.isEnableAutoMappingWsdlIntoAccordo(), apcCore, apcHelper, as, userLogin);
			
			// cancello i file temporanei
			apcHelper.deleteBinaryProtocolPropertiesTmpFiles(protocolProperties);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			List<Resource> lista = apcCore.accordiResourceList(idAccordoLong, ricerca);

			// Devo rileggere l'accordo dal db, perche' altrimenti
			// manca l'id delle risorse
			as = apcCore.getAccordoServizioFull(idAccordoLong);

			apcHelper.prepareAccordiResourcesList(id,as, lista, ricerca,tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES, ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES, ForwardParams.ADD());
		}
	}
}
