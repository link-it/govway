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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Message;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.constants.BindingStyle;
import org.openspcoop2.core.registry.constants.BindingUse;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
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
 * accordiPorttypeOperationsChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComunePortTypeOperationsChange extends Action {

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
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			SoggettiCore soggettiCore = new SoggettiCore(apcCore);
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);
			
			String editMode = apcHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			String protocolPropertiesSet = apcHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_SET);
			String opcorrelata = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_CORRELATA);
			String servcorr = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_SERVIZIO_CORRELATO);
			String azicorr = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_AZIONE_CORRELATA);
			String id = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			int idInt = Integer.parseInt(id);
			String nomept = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME);
			String nomeop = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NOME);
			String profProtocollo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_PROFILO_BUSTA);
			String profcollop = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_PROFILO_COLLABORAZIONE);
			if(profcollop == null)
				profcollop = "";

			String filtrodupop = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_FILTRO_DUPLICATI);
			if ((filtrodupop != null) && filtrodupop.equals("null")) {
				filtrodupop = null;
			}
			String confricop = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_CONFERMA_RICEZIONE);
			if ((confricop != null) && confricop.equals("null")) {
				confricop = null;
			}
			String idcollop = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_COLLABORAZIONE);
			if ((idcollop != null) && idcollop.equals("null")) {
				idcollop = null;
			}
			String idRifRichiestaOp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_ID_RIFERIMENTO_RICHIESTA);
			if ((idRifRichiestaOp != null) && idRifRichiestaOp.equals("null")) {
				idRifRichiestaOp = null;
			}
			String consordop = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_CONSEGNA_ORDINE);
			if ((consordop != null) && consordop.equals("null")) {
				consordop = null;
			}
			String scadenzaop = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_SCADENZA);
			if (scadenzaop == null) {
				scadenzaop = "";
			}

			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;

			//parametri WSDL
			String styleOp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_STYLE);
			String soapActionOp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_SOAP_ACTION);
			String useOp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_USE);
			String opTypeOp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE);
			String nsWSDLOp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NS_WSDL);


			int messageOutputCnt = 0;
			int messageInputCnt = 0;

			String postBackElementName = apcHelper.getPostBackElementName();

			if(postBackElementName!= null){
				if(postBackElementName.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_PROFILO_COLLABORAZIONE)){
					if(profcollop.equals(CostantiRegistroServizi.SINCRONO.getValue()))
						opTypeOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_INOUT;
					else 
						opTypeOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_IN;
				}

				if(postBackElementName.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_PROFILO_BUSTA)){
					opTypeOp = null;
					profcollop = null;
					confricop = null;
					idcollop= null; 
					idRifRichiestaOp= null; 
					filtrodupop= null;
					scadenzaop= "";
					consordop= null;
				}
			}

			// Preparo il menu
			apcHelper.makeMenu();

			// Prendo il nome dal db
			AccordoServizioParteComune as = apcCore.getAccordoServizioFull(idInt);
			String uriAS = idAccordoFactory.getUriFromAccordo(as);
			String labelASTitle = apcHelper.getLabelIdAccordo(as); 
			IDAccordo idAs = idAccordoFactory.getIDAccordoFromAccordo(as);

			String protocollo = null;
			//calcolo del protocollo implementato dall'accordo
			IdSoggetto soggettoReferente = as.getSoggettoReferente();
			String tipoSoggettoReferente = soggettoReferente.getTipo();
			protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggettoReferente);

			// Prendo il port-type
			PortType pt = null;
			for (int i = 0; i < as.sizePortTypeList(); i++) {
				pt = as.getPortType(i);
				if (nomept.equals(pt.getNome()))
					break;
			}

			protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			consoleDynamicConfiguration =  protocolFactory.createDynamicConfigurationConsole();
			registryReader = soggettiCore.getRegistryReader(protocolFactory); 
			configRegistryReader = soggettiCore.getConfigIntegrationReader(protocolFactory);
			IDPortType idPt = new IDPortType();
			idPt.setIdAccordo(idAs);
			idPt.setNome(nomept);

			IDPortTypeAzione idAzione = new IDPortTypeAzione();
			idAzione.setIdPortType(idPt);
			idAzione.setNome(nomeop);
			consoleConfiguration = consoleDynamicConfiguration.getDynamicConfigOperation(consoleOperationType, apcHelper, 
					registryReader, configRegistryReader, idAzione );
			protocolProperties = apcHelper.estraiProtocolPropertiesDaRequest(consoleConfiguration, consoleOperationType);

			Operation opOLD = null;
			for (int i = 0; i < pt.sizeAzioneList(); i++) {
				Operation op = pt.getAzione(i);
				if (nomeop.equals(op.getNome())) {
					opOLD = op;
					break;
				}
			}

			if(opOLD != null)
				oldProtocolPropertyList = opOLD.getProtocolPropertyList();

			if(protocolPropertiesSet == null){
				ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(protocolProperties, oldProtocolPropertyList, consoleOperationType);
			}

			Properties propertiesProprietario = new Properties();
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, id);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_OPERATION);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, nomeop);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PARENT_PROPRIETARIO, nomept);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE,
					URLEncoder.encode( AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_CHANGE + "?" + request.getQueryString(), "UTF-8"));
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, protocollo);
			if(tipoAccordo!=null) {
				propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO, tipoAccordo);
			}
			
			
			Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo);
			Parameter pIdAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id);
			Parameter pNomeAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, uriAS);
			Parameter pNomePortTypes = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME,nomept);
			Boolean isModalitaVistaApiCustom = ServletUtils.getBooleanAttributeFromSession(ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API, session, request, false).getValue();
			List<Parameter> listaParams = apcHelper.getTitoloApc(TipoOperazione.CHANGE, as, tipoAccordo, labelASTitle, null, false);
			
			String labelPortTypes = (isModalitaVistaApiCustom!=null && isModalitaVistaApiCustom.booleanValue()) ? AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES  : AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES + " di " + labelASTitle;
			listaParams.add(new Parameter(labelPortTypes, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPES_LIST, pIdAccordo, pNomeAccordo, pTipoAccordo));
			
			String labelOperations = AccordiServizioParteComuneCostanti.LABEL_AZIONI  + " di " + nomept;
			listaParams.add(new Parameter(labelOperations, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_LIST, pIdAccordo, pNomeAccordo, pTipoAccordo,pNomePortTypes));
			listaParams.add(new Parameter(nomeop, null));

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if(ServletUtils.isEditModeInProgress(editMode)){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				// Se operation usa il profilo del port-type, si prendono le
				// informazioni dal port-type.
				// Se il port-type usa il profilo dell'accordo, si prendono le
				// informazioni dall'accordo.
				String deffiltrodupop = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati());
				String defconfricop = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione());
				String defidcollop = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione());
				String defIdRifRichiestaOp = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdRiferimentoRichiesta());
				String defconsordop = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine());
				String defscadenzaop = as.getScadenza();
				String defprofcollop = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(as.getProfiloCollaborazione());

				String profProtocolloPT = pt.getProfiloPT();
				if (!profProtocolloPT.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
					deffiltrodupop = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(pt.getFiltroDuplicati());
					defconfricop = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(pt.getConfermaRicezione());
					defidcollop = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(pt.getIdCollaborazione());
					defIdRifRichiestaOp = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(pt.getIdRiferimentoRichiesta());
					defconsordop = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(pt.getConsegnaInOrdine());
					defscadenzaop = pt.getScadenza();
					defprofcollop = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(pt.getProfiloCollaborazione());
				}

				if(opOLD != null){
					filtrodupop = filtrodupop != null && !"".equals(filtrodupop) ? filtrodupop : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(opOLD.getFiltroDuplicati());
					confricop = confricop != null && !"".equals(confricop) ? confricop : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(opOLD.getConfermaRicezione());
					idcollop = idcollop != null && !"".equals(idcollop) ? idcollop : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(opOLD.getIdCollaborazione());
					idRifRichiestaOp = idRifRichiestaOp != null && !"".equals(idRifRichiestaOp) ? idRifRichiestaOp : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(opOLD.getIdRiferimentoRichiesta());
					consordop = consordop != null && !"".equals(consordop) ? consordop : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(opOLD.getConsegnaInOrdine());
					scadenzaop = scadenzaop != null && !"".equals(scadenzaop) ? scadenzaop : opOLD.getScadenza();
					profcollop = profcollop != null && !"".equals(profcollop) ? profcollop : AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(opOLD.getProfiloCollaborazione());
					profProtocollo = profProtocollo != null && !"".equals(profProtocollo) ? profProtocollo : opOLD.getProfAzione();
					if (apcCore.isShowCorrelazioneAsincronaInAccordi()) {
						servcorr = servcorr != null && !"".equals(servcorr) ? servcorr : opOLD.getCorrelataServizio();
						if (servcorr == null)
							servcorr = "-";
						azicorr = azicorr != null && !"".equals(azicorr) ? azicorr : opOLD.getCorrelata();
						if (azicorr == null)
							azicorr = "-";
					} else {
						opcorrelata = opcorrelata != null && !"".equals(opcorrelata) ? opcorrelata : opOLD.getCorrelata();
						if (opcorrelata == null)
							opcorrelata = "-";
					}

					if(opOLD.getMessageInput() !=null)
						messageInputCnt = opOLD.getMessageInput().sizePartList();

					if(opOLD.getMessageOutput() !=null)
						messageOutputCnt = opOLD.getMessageOutput().sizePartList();

					if(styleOp == null){
						styleOp = opOLD.getStyle() != null ? opOLD.getStyle().getValue() : AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_STYLE;
					}

					if(soapActionOp == null)
						soapActionOp = opOLD.getSoapAction();

					if(useOp == null){
						useOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_USE;

						if(opOLD.getMessageInput()!= null && opOLD.getMessageInput().getUse() != null){
							useOp = opOLD.getMessageInput().getUse().getValue();
						}
					}

					if(opTypeOp == null){

						if(opOLD.getMessageInput() != null && opOLD.getMessageOutput() != null){
							opTypeOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_INOUT;
						}
						else if(ProfiloCollaborazione.SINCRONO.equals(ProfiloCollaborazione.toEnumConstant(profcollop))){
							opTypeOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_INOUT;
						}
						else if(ProfiloCollaborazione.ONEWAY.equals(ProfiloCollaborazione.toEnumConstant(profcollop))){
							opTypeOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_IN;
						}
						else if(opOLD.getMessageOutput() != null){
							opTypeOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_INOUT;	
						}
						else{
							opTypeOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_IN;
						}
						
					} 

					if(nsWSDLOp == null){
						nsWSDLOp = "";

						if(opOLD.getMessageInput() != null && opOLD.getMessageInput().getSoapNamespace()!= null)
							nsWSDLOp = opOLD.getMessageInput().getSoapNamespace();
					}
				}

				if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
					filtrodupop = deffiltrodupop;
					confricop = defconfricop;
					idcollop = defidcollop;
					idRifRichiestaOp = defIdRifRichiestaOp;
					consordop = defconsordop;
					scadenzaop = defscadenzaop;
					profcollop = defprofcollop;
				}

				// Popolo le liste necessarie
				// opCorrelateList, se !isShowCorrelazioneAsincronaInAccordi
				// servCorrList e aziCorrList, altrimenti
				ArrayList<String> opCorrelateList = null;
				ArrayList<String> servCorrList = null;
				ArrayList<String> aziCorrList = null;
				if (apcCore.isShowCorrelazioneAsincronaInAccordi()) {
					servCorrList = AccordiServizioParteComuneUtilities.selectPortTypeAsincroni(as, profcollop, nomept);
					Map<String, List<Operation>> operationsListSelezionate = AccordiServizioParteComuneUtilities.selectPortTypeOperationsListAsincrone(as, profcollop, nomept);
					aziCorrList =  AccordiServizioParteComuneUtilities.selectOperationAsincrone(as, servcorr, profProtocollo, profcollop, pt, nomeop, apcCore, operationsListSelezionate);
					// siccome sono in change, aggiungo la mia azione se e' correlata poiche' il metodo sopra non la aggiungera'
					if(azicorr!=null && !"".equals(azicorr) && !"-".equals(azicorr) ){
						if(aziCorrList==null){
							aziCorrList = new ArrayList<>();
							aziCorrList.add("-");
						}
						aziCorrList.add(azicorr);
					}
				} else {

					// Recupero le azioni del servizio con
					// profilo di collaborazione asincronoAsimmetrico
					// che non sono già state correlate
					ArrayList<String> operationCorrelateUniche = null;
					List<Operation> operationCorrelate = apcCore.accordiPorttypeOperationList(pt.getId().intValue(), AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO, new ConsoleSearch(true));

					if (!operationCorrelate.isEmpty()) {
						operationCorrelateUniche = new ArrayList<>();
						operationCorrelateUniche.add("-");
						for (Iterator<Operation> iterator = operationCorrelate.iterator(); iterator.hasNext();) {
							Operation operation = iterator.next();
							if (!nomeop.equals(operation.getNome())) {
								// se l 'operation che sto controllando e' correlata
								// allora non la visualizzo nella select list
								// altriment la visualizzo
								if ( 
										(operation.getCorrelata()==null||"".equals(operation.getCorrelata())) &&
										(!apcCore.isOperationCorrelata(pt.getId().intValue(), operation.getNome(), nomeop))
										) {
									operationCorrelateUniche.add(operation.getNome());
								}
							}
						}
					}

					if (operationCorrelateUniche != null && !operationCorrelateUniche.isEmpty())
						opCorrelateList = operationCorrelateUniche;
				}

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				consoleDynamicConfiguration.updateDynamicConfigOperation(consoleConfiguration, consoleOperationType, apcHelper, protocolProperties, 
						registryReader, configRegistryReader, idAzione);

				dati = apcHelper.addAccordiPorttypeOperationToDati(dati, id, nomept, nomeop, profProtocollo, 
						filtrodupop, deffiltrodupop, confricop, defconfricop, idcollop, defidcollop, idRifRichiestaOp, defIdRifRichiestaOp, consordop, defconsordop, scadenzaop, defscadenzaop, 
						tipoOp, defprofcollop, profcollop, opcorrelata, 
						opCorrelateList != null ? opCorrelateList.toArray(new String[opCorrelateList.size()]) : null, as.getStatoPackage(),
								tipoAccordo, servCorrList != null ? servCorrList.toArray(new String[servCorrList.size()]) : null, servcorr, 
										aziCorrList != null ? aziCorrList.toArray(new String[aziCorrList.size()]) : null, azicorr,protocollo,
												soapActionOp, styleOp, useOp, nsWSDLOp, 
												opTypeOp, messageInputCnt, messageOutputCnt,apcCore.toMessageServiceBinding(as.getServiceBinding()));
				// aggiunta campi custom
				dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, consoleConfiguration,consoleOperationType, protocolProperties,oldProtocolPropertyList,propertiesProprietario);

				pd.setDati(dati);

				if(apcHelper.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(as.getStatoPackage())){
					pd.disableEditMode();
				}

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS, ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = apcHelper.accordiPorttypeOperationCheckData(tipoOp, id, nomept, nomeop, profProtocollo, filtrodupop, confricop, idcollop, idRifRichiestaOp, consordop, scadenzaop, servcorr, azicorr, profcollop, styleOp, soapActionOp, useOp, opTypeOp, nsWSDLOp);

			// updateDynamic
			if(isOk) {
				consoleDynamicConfiguration.updateDynamicConfigOperation(consoleConfiguration, consoleOperationType, apcHelper, protocolProperties, 
						registryReader, configRegistryReader, idAzione);
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
					consoleDynamicConfiguration.validateDynamicConfigOperation(consoleConfiguration, consoleOperationType, apcHelper, protocolProperties, 
							registryReader, configRegistryReader, idAzione);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				// Popolo le liste necessarie
				// opCorrelateList, se !isShowCorrelazioneAsincronaInAccordi
				// servCorrList e aziCorrList, altrimenti
				ArrayList<String> opCorrelateList = null;
				ArrayList<String> servCorrList = null;
				ArrayList<String> aziCorrList = null;
				if (apcCore.isShowCorrelazioneAsincronaInAccordi()) {
					servCorrList = AccordiServizioParteComuneUtilities.selectPortTypeAsincroni(as, profcollop, nomept);
					Map<String, List<Operation>> operationsListSelezionate = AccordiServizioParteComuneUtilities.selectPortTypeOperationsListAsincrone(as, profcollop, nomept);
					aziCorrList =  AccordiServizioParteComuneUtilities.selectOperationAsincrone(as, servcorr, profProtocollo, profcollop, pt, nomeop, apcCore, operationsListSelezionate);
					// siccome sono in change, aggiungo la mia azione se e' correlata poiche' il metodo sopra non la aggiungera'
					if(azicorr!=null && !"".equals(azicorr) && !"-".equals(azicorr) ){
						if(aziCorrList==null){
							aziCorrList = new ArrayList<>();
							aziCorrList.add("-");
						}
						aziCorrList.add(azicorr);
					}
				} else {

					// Recupero le azioni del servizio con
					// profilo di collaborazione asincronoAsimmetrico
					// che non sono già state correlate
					ArrayList<String> operationCorrelateUniche = null;
					List<Operation> operationCorrelate = apcCore.accordiPorttypeOperationList(pt.getId().intValue(), AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO, new ConsoleSearch(true));

					if (!operationCorrelate.isEmpty()) {
						operationCorrelateUniche = new ArrayList<>();
						operationCorrelateUniche.add("-");
						for (Iterator<Operation> iterator = operationCorrelate.iterator(); iterator.hasNext();) {
							Operation operation = iterator.next();
							if (!nomeop.equals(operation.getNome())) {
								// se l 'operation che sto controllando e' correlata
								// allora non la visualizzo nella select list
								// altriment la visualizzo
								if ( 
										(operation.getCorrelata()==null||"".equals(operation.getCorrelata())) &&
										(!apcCore.isOperationCorrelata(pt.getId().intValue(), operation.getNome(), nomeop))
										) {
									operationCorrelateUniche.add(operation.getNome());
								}
							}
						}
					}

					if (operationCorrelateUniche != null && !operationCorrelateUniche.isEmpty())
						opCorrelateList = operationCorrelateUniche;
				}

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				// update della configurazione 
				consoleDynamicConfiguration.updateDynamicConfigOperation(consoleConfiguration, consoleOperationType, apcHelper, protocolProperties, 
						registryReader, configRegistryReader, idAzione);

				dati = apcHelper.addAccordiPorttypeOperationToDati(dati, id, nomept, nomeop, profProtocollo, 
						filtrodupop, filtrodupop, confricop, confricop, idcollop, idcollop, idRifRichiestaOp, idRifRichiestaOp, consordop, consordop, scadenzaop, scadenzaop, 
						tipoOp, profcollop, profcollop, opcorrelata, 
						opCorrelateList != null ? opCorrelateList.toArray(new String[opCorrelateList.size()]) : null,as.getStatoPackage(),
								tipoAccordo, servCorrList != null ? servCorrList.toArray(new String[servCorrList.size()]) : null, servcorr, 
										aziCorrList != null ? aziCorrList.toArray(new String[aziCorrList.size()]) : null, azicorr,protocollo,
												soapActionOp, styleOp, useOp, nsWSDLOp,  opTypeOp, messageInputCnt, messageOutputCnt,apcCore.toMessageServiceBinding(as.getServiceBinding()));

				// aggiunta campi custom
				dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, consoleConfiguration,consoleOperationType, protocolProperties,oldProtocolPropertyList,propertiesProprietario);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS, ForwardParams.CHANGE());
			}

			// Modifico i dati dell'operation nel db
			if(ServletUtils.isCheckBoxEnabled(filtrodupop)){
				filtrodupop = CostantiRegistroServizi.ABILITATO.toString();
			} else {
				filtrodupop = CostantiRegistroServizi.DISABILITATO.toString();
			}
			if(ServletUtils.isCheckBoxEnabled(confricop)){
				confricop = CostantiRegistroServizi.ABILITATO.toString();
			} else {
				confricop = CostantiRegistroServizi.DISABILITATO.toString();
			}
			if(ServletUtils.isCheckBoxEnabled(idcollop)){
				idcollop = CostantiRegistroServizi.ABILITATO.toString();
			} else {
				idcollop = CostantiRegistroServizi.DISABILITATO.toString();
			}
			if(ServletUtils.isCheckBoxEnabled(idRifRichiestaOp)){
				idRifRichiestaOp = CostantiRegistroServizi.ABILITATO.toString();
			} else {
				idRifRichiestaOp = CostantiRegistroServizi.DISABILITATO.toString();
			}
			if(ServletUtils.isCheckBoxEnabled(consordop)){
				consordop = CostantiRegistroServizi.ABILITATO.toString();
			} else {
				consordop = CostantiRegistroServizi.DISABILITATO.toString();
			}

			Operation op = null;
			for (int i = 0; i < pt.sizeAzioneList(); i++) {
				op = pt.getAzione(i);
				if (nomeop.equals(op.getNome())) {
					pt.removeAzione(i);
					break;
				}
			}

			Operation newOp = new Operation();
			newOp.setId(op.getId());
			newOp.setMessageInput(op.getMessageInput());
			newOp.setMessageOutput(op.getMessageOutput());

			newOp.setNome(nomeop);
			if(ServletUtils.isCheckBoxEnabled(filtrodupop)){
				newOp.setFiltroDuplicati(CostantiRegistroServizi.ABILITATO);
			} else {
				newOp.setFiltroDuplicati(CostantiRegistroServizi.DISABILITATO);
			}
			if(ServletUtils.isCheckBoxEnabled(confricop)){
				newOp.setConfermaRicezione(CostantiRegistroServizi.ABILITATO);
			} else {
				newOp.setConfermaRicezione(CostantiRegistroServizi.DISABILITATO);
			}
			if(ServletUtils.isCheckBoxEnabled(idcollop)){
				newOp.setIdCollaborazione(CostantiRegistroServizi.ABILITATO);
			} else {
				newOp.setIdCollaborazione(CostantiRegistroServizi.DISABILITATO);
			}
			if(ServletUtils.isCheckBoxEnabled(idRifRichiestaOp)){
				newOp.setIdRiferimentoRichiesta(CostantiRegistroServizi.ABILITATO);
			} else {
				newOp.setIdRiferimentoRichiesta(CostantiRegistroServizi.DISABILITATO);
			}
			if(ServletUtils.isCheckBoxEnabled(consordop)){
				newOp.setConsegnaInOrdine(CostantiRegistroServizi.ABILITATO);
			} else {
				newOp.setConsegnaInOrdine(CostantiRegistroServizi.DISABILITATO);
			}
			if ((scadenzaop != null) && (!"".equals(scadenzaop))) {
				newOp.setScadenza(scadenzaop);
			}
			if (apcCore.isShowCorrelazioneAsincronaInAccordi()) {
				if (servcorr != null && !"-".equals(servcorr))
					newOp.setCorrelataServizio(servcorr);
				if (azicorr != null && !"-".equals(azicorr))
					newOp.setCorrelata(azicorr);
			} else {
				if (opcorrelata != null && !"-".equals(opcorrelata))
					newOp.setCorrelata(opcorrelata);
			}
			newOp.setProfAzione(profProtocollo);
			newOp.setProfiloCollaborazione(ProfiloCollaborazione.toEnumConstant(profcollop));

			//Informazioni WSDL
			newOp.setSoapAction(soapActionOp);

			BindingStyle style = null;
			if(styleOp!=null && !styleOp.equalsIgnoreCase(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_STYLE))
				style = BindingStyle.toEnumConstant(styleOp);

			newOp.setStyle(style);

			BindingUse use = BindingUse.toEnumConstant(useOp); 

			if(newOp.getMessageInput() == null){
				newOp.setMessageInput(new Message());
			}
			newOp.getMessageInput().setSoapNamespace(nsWSDLOp);
			newOp.getMessageInput().setUse(use);

			if(opTypeOp == null){
				if(profcollop.equals(CostantiRegistroServizi.ONEWAY.getValue()))
					opTypeOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_IN;
				else 
					opTypeOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_INOUT;
			}  

			if(opTypeOp.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_INOUT)){
				if(newOp.getMessageOutput() == null){
					newOp.setMessageOutput(new Message());
				}
				newOp.getMessageOutput().setSoapNamespace(nsWSDLOp);
				newOp.getMessageOutput().setUse(use);
			}else {
				newOp.setMessageOutput(null); 
			}

			pt.addAzione(newOp);
			
			//imposto properties custom
			newOp.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(protocolProperties, consoleOperationType, oldProtocolPropertyList));

			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), pt);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			List<Operation> lista = apcCore.accordiPorttypeOperationList(pt.getId().intValue(), ricerca);

			apcHelper.prepareAccordiPorttypeOperationsList(ricerca, lista,id, as,tipoAccordo, pt.getNome());

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS, ForwardParams.CHANGE());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS, ForwardParams.CHANGE());
		} 
	}
}
