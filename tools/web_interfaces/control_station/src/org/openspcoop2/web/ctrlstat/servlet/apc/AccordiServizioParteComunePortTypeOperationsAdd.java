/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.openspcoop2.core.registry.constants.BindingStyle;
import org.openspcoop2.core.registry.constants.BindingUse;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
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
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * accordiPorttypeOperationsAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComunePortTypeOperationsAdd extends Action {

	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory= null;
	private IRegistryReader registryReader = null; 
	private IConfigIntegrationReader configRegistryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	
	private String editMode = null;

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

		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		this.consoleOperationType = ConsoleOperationType.ADD;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.ADD; 

		try {
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			SoggettiCore soggettiCore = new SoggettiCore(apcCore);
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);
			
			this.editMode = apcHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);

			String opcorrelata = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_CORRELATA);
			String servcorr = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_SERVIZIO_CORRELATO);
			String azicorr = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_AZIONE_CORRELATA);
			String id = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			int idInt = Integer.parseInt(id);
			String nomept = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME);
			String nomeop = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NOME);
			if (nomeop == null) {
				nomeop = "";
			}
			String profProtocollo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_PROFILO_BUSTA);

			String profcollop = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_PROFILO_COLLABORAZIONE);
			if(profcollop == null)
				profcollop  = "";

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
			if (opcorrelata == null || opcorrelata.equals(""))
				opcorrelata = "-";
			if (servcorr == null || servcorr.equals(""))
				servcorr = "-";
			if (azicorr == null || azicorr.equals(""))
				azicorr = "-";

			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;

			//parametri WSDL
			String styleOp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_STYLE);
			if(styleOp == null)
				styleOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_STYLE;
			String soapActionOp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_SOAP_ACTION);
			if(soapActionOp == null)
				soapActionOp = "";
			String useOp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_USE);
			if(useOp == null)
				useOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_USE;
			String opTypeOp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE);
			String nsWSDLOp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NS_WSDL);
			if(nsWSDLOp == null)
				nsWSDLOp = "";

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

			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = soggettiCore.getRegistryReader(this.protocolFactory);
			this.configRegistryReader = soggettiCore.getConfigIntegrationReader(this.protocolFactory);
			
			IDPortType idPt = new IDPortType();
			idPt.setIdAccordo(idAs);
			idPt.setNome(pt.getNome());
			IDPortTypeAzione idAzione = new IDPortTypeAzione();
			idAzione.setIdPortType(idPt);
			idAzione.setNome(nomeop);

			this.consoleConfiguration = this.consoleDynamicConfiguration.getDynamicConfigOperation(this.consoleOperationType, apcHelper, 
					this.registryReader, this.configRegistryReader, idAzione );
			this.protocolProperties = apcHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);


			int messageOutputCnt = 0;
			int messageInputCnt = 0;

			String postBackElementName = apcHelper.getParameter(Costanti.POSTBACK_ELEMENT_NAME); 

			if(postBackElementName!= null){
				if(postBackElementName.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_PROFILO_COLLABORAZIONE)){
					opTypeOp = null;
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

				// [TODO] aggiungere delete file temporanei
			}
			
			Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo);
			Parameter pIdAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id);
			Parameter pNomeAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, uriAS);
			Parameter pNomePortTypes = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME,nomept);
			Boolean isModalitaVistaApiCustom = ServletUtils.getBooleanAttributeFromSession(ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API, session, false);
			List<Parameter> listaParams = apcHelper.getTitoloApc(TipoOperazione.ADD, as, tipoAccordo, labelASTitle, null, false);
			
			String labelPortTypes = isModalitaVistaApiCustom ? AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES  : AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES + " di " + labelASTitle;
			listaParams.add(new Parameter(labelPortTypes, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPES_LIST, pIdAccordo, pNomeAccordo, pTipoAccordo));
			
			String labelOperations = AccordiServizioParteComuneCostanti.LABEL_AZIONI  + " di " + nomept;
			listaParams.add(new Parameter(labelOperations, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_LIST, pIdAccordo, pNomeAccordo, pTipoAccordo,pNomePortTypes));
			listaParams.add(ServletUtils.getParameterAggiungi());

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if(ServletUtils.isEditModeInProgress(this.editMode)){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				if (profProtocollo == null || "".equals(profProtocollo)) {
					profProtocollo = AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT;
				}

				// Se il port-type usa il profilo dell'accordo, si prendono i
				// default stabiliti nell'accordo relativo
				// In caso contrario si prendono quelli del port-type
				/*
				 * filtrodupop = as.getFiltroDuplicati(); confricop =
				 * as.getConfermaRicezione(); idcollop =
				 * as.getIdCollaborazione(); consordop =
				 * as.getConfermaRicezione(); idRifRichiestaOp =
				 * as.getConsegnaInOrdine(); scadenzaop = as.getScadenza();
				 * profcollop = as.getProfiloCollaborazione();
				 */

				if(profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO)){
					profcollop = profcollop != null && !"".equals(profcollop) ? profcollop : AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(pt.getProfiloCollaborazione());
				}else{
					String profProtocolloPT = pt.getProfiloPT();
					if (!profProtocolloPT.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
						// filtrodupop = pt.getFiltroDuplicati();
						// confricop = pt.getConfermaRicezione();
						// idcollop = pt.getIdCollaborazione();
						// idRifRichiestaOp = pt.getIdRiferimentoRichiesta();
						// consordop = pt.getConsegnaInOrdine();
						// scadenzaop = pt.getScadenza();
						// profcollop = pt.getProfiloCollaborazione();
						filtrodupop = filtrodupop != null && !"".equals(filtrodupop) ? filtrodupop : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(pt.getFiltroDuplicati());
						confricop = confricop != null && !"".equals(confricop) ? confricop : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(pt.getConfermaRicezione());
						idcollop = idcollop != null && !"".equals(idcollop) ? idcollop : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(pt.getIdCollaborazione());
						idRifRichiestaOp = idRifRichiestaOp != null && !"".equals(idRifRichiestaOp) ? idRifRichiestaOp : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(pt.getIdRiferimentoRichiesta());
						consordop = consordop != null && !"".equals(consordop) ? consordop : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(pt.getConsegnaInOrdine());
						scadenzaop = scadenzaop != null && !"".equals(scadenzaop) ? scadenzaop : pt.getScadenza();
						profcollop = profcollop != null && !"".equals(profcollop) ? profcollop : AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(pt.getProfiloCollaborazione());
						profProtocollo = profProtocollo != null && !"".equals(profProtocollo) ? profProtocollo : pt.getProfiloPT();
					} else {
						filtrodupop = filtrodupop != null && !"".equals(filtrodupop) ? filtrodupop : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati());
						confricop = confricop != null && !"".equals(confricop) ? confricop : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione());
						idcollop = idcollop != null && !"".equals(idcollop) ? idcollop : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione());
						idRifRichiestaOp = idRifRichiestaOp != null && !"".equals(idRifRichiestaOp) ? idRifRichiestaOp : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdRiferimentoRichiesta());
						consordop = consordop != null && !"".equals(consordop) ? consordop : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine());
						scadenzaop = scadenzaop != null && !"".equals(scadenzaop) ? scadenzaop : as.getScadenza();
						profcollop = profcollop != null && !"".equals(profcollop) ? profcollop : AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(as.getProfiloCollaborazione());
					}
				}


				// setto valore dell'operationType di default (Profilo sincrono e' input/output, gli altri metto Input)
				if(opTypeOp == null){
					if(profcollop.equals(CostantiRegistroServizi.ONEWAY.getValue()))
						opTypeOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_IN;
					else 
						opTypeOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_INOUT;
				}  

				// Popolo le liste necessarie
				// opCorrelateList, se !isShowCorrelazioneAsincronaInAccordi
				// servCorrList e aziCorrList, altrimenti
				ArrayList<String> opCorrelateList = null;
				ArrayList<String> servCorrList = null;
				ArrayList<String> aziCorrList = null;
				if (apcCore.isShowCorrelazioneAsincronaInAccordi()) {
					servCorrList = AccordiServizioParteComuneUtilities.selectPortTypeAsincroni(as, profcollop, nomept);
					Hashtable<String, List<Operation>> operationsListSelezionate = AccordiServizioParteComuneUtilities.selectPortTypeOperationsListAsincrone(as, profcollop, nomept);
					aziCorrList =  AccordiServizioParteComuneUtilities.selectOperationAsincrone(as, servcorr, profProtocollo, profcollop, pt, nomeop, apcCore, operationsListSelezionate);
				} else {

					// Recupero le azioni del servizio con
					// profilo di collaborazione asincronoAsimmetrico
					// che non sono già state correlate
					ArrayList<String> operationCorrelateUniche = null;
					List<Operation> operationCorrelate = apcCore.accordiPorttypeOperationList(pt.getId().intValue(), AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO, new Search(true));

					if (operationCorrelate.size() > 0) {
						operationCorrelateUniche = new ArrayList<String>();
						operationCorrelateUniche.add("-");
						for (Iterator<Operation> iterator = operationCorrelate.iterator(); iterator.hasNext();) {
							Operation operation = iterator.next();
							if (!nomeop.equals(operation.getNome())) {
								// se l 'operation che sto controllando e' correlata
								// allora non la visualizzo nella select list
								// altriment la visualizzo
								// if(!core.isOperationCorrelata(pt.getId().intValue(),
								// operation.getCorrelata(),nomeop)){
								if ( 
										(operation.getCorrelata()==null||"".equals(operation.getCorrelata())) &&
										(!apcCore.isOperationCorrelata(pt.getId().intValue(), operation.getNome(), nomeop))
										) {
									operationCorrelateUniche.add(operation.getNome());
								}
							}
						}
					}

					if (operationCorrelateUniche != null && operationCorrelateUniche.size() > 0)
						opCorrelateList = operationCorrelateUniche;
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				this.consoleDynamicConfiguration.updateDynamicConfigOperation(this.consoleConfiguration, this.consoleOperationType, apcHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idAzione);

				dati = apcHelper.addAccordiPorttypeOperationToDati(dati, id, nomept, nomeop, profProtocollo, 
						filtrodupop, filtrodupop, confricop, confricop, idcollop, idcollop, idRifRichiestaOp, idRifRichiestaOp, consordop, consordop, scadenzaop, scadenzaop, 
						tipoOp, profcollop, profcollop, "", 
						opCorrelateList != null ? opCorrelateList.toArray(new String[opCorrelateList.size()]) : null, as.getStatoPackage(),
								tipoAccordo, servCorrList != null ? servCorrList.toArray(new String[servCorrList.size()]) : null, servcorr, 
										aziCorrList != null ? aziCorrList.toArray(new String[aziCorrList.size()]) : null, azicorr,protocollo,
												soapActionOp, styleOp, useOp, nsWSDLOp,  
												opTypeOp, messageInputCnt, messageOutputCnt,apcCore.toMessageServiceBinding(as.getServiceBinding()));

				// aggiunta campi custom
				dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS, ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = apcHelper.accordiPorttypeOperationCheckData(tipoOp, id, nomept, nomeop, profProtocollo, filtrodupop, confricop, idcollop, idRifRichiestaOp, consordop, scadenzaop, servcorr, azicorr, profcollop, styleOp, soapActionOp, useOp, opTypeOp, nsWSDLOp);

			// updateDynamic
			if(isOk) {
				this.consoleDynamicConfiguration.updateDynamicConfigOperation(this.consoleConfiguration, this.consoleOperationType, apcHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idAzione);
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
					this.consoleDynamicConfiguration.validateDynamicConfigOperation(this.consoleConfiguration, this.consoleOperationType, apcHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idAzione);
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
					Hashtable<String, List<Operation>> operationsListSelezionate = AccordiServizioParteComuneUtilities.selectPortTypeOperationsListAsincrone(as, profcollop, nomept);
					aziCorrList =  AccordiServizioParteComuneUtilities.selectOperationAsincrone(as, servcorr, profProtocollo, profcollop, pt, nomeop, apcCore, operationsListSelezionate);
				} else {

					// Recupero le azioni del servizio con
					// profilo di collaborazione asincronoAsimmetrico
					// che non sono già state correlate
					ArrayList<String> operationCorrelateUniche = null;
					List<Operation> operationCorrelate = apcCore.accordiPorttypeOperationList(pt.getId().intValue(), AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO, new Search(true));

					if (operationCorrelate.size() > 0) {
						operationCorrelateUniche = new ArrayList<String>();
						operationCorrelateUniche.add("-");
						for (Iterator<Operation> iterator = operationCorrelate.iterator(); iterator.hasNext();) {
							Operation operation = iterator.next();
							if (!nomeop.equals(operation.getNome())) {
								// se l 'operation che sto controllando e' correlata
								// allora non la visualizzo nella select list
								// altriment la visualizzo
								// if(!core.isOperationCorrelata(pt.getId().intValue(),
								// operation.getCorrelata(),nomeop)){
								if ( 
										(operation.getCorrelata()==null||"".equals(operation.getCorrelata())) &&
										(!apcCore.isOperationCorrelata(pt.getId().intValue(), operation.getNome(), nomeop))
										) {
									operationCorrelateUniche.add(operation.getNome());
								}
							}
						}
					}

					if (operationCorrelateUniche != null && operationCorrelateUniche.size() > 0)
						opCorrelateList = operationCorrelateUniche;
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				this.consoleDynamicConfiguration.updateDynamicConfigOperation(this.consoleConfiguration, this.consoleOperationType, apcHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idAzione);

				dati = apcHelper.addAccordiPorttypeOperationToDati(dati, id, nomept, nomeop, profProtocollo, 
						filtrodupop, filtrodupop, confricop, confricop, idcollop, idcollop, idRifRichiestaOp, idRifRichiestaOp, consordop, consordop, scadenzaop, scadenzaop, 
						tipoOp, profcollop, profcollop, opcorrelata, 
						opCorrelateList != null ? opCorrelateList.toArray(new String[opCorrelateList.size()]) : null, as.getStatoPackage(),
								tipoAccordo, servCorrList != null ? servCorrList.toArray(new String[servCorrList.size()]) : null, servcorr, 
										aziCorrList != null ? aziCorrList.toArray(new String[aziCorrList.size()]) : null, azicorr,protocollo,
												soapActionOp, styleOp, useOp, nsWSDLOp,  
												opTypeOp, messageInputCnt, messageOutputCnt,apcCore.toMessageServiceBinding(as.getServiceBinding()));

				// aggiunta campi custom
				dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS, ForwardParams.ADD());
			}

			// Inserisco l'operation nel db
			// if (profProtocolloPT.equals("default")) {
			// filtrodupop = null;
			// confricop = null;
			// idcollop = null;
			// idRifRichiestaOp = null;
			// consordop = null;
			// scadenzaop = null;
			// } else {
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
			// }

			Operation newOp = new Operation();
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
			if(!styleOp.equalsIgnoreCase(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_STYLE))
				style = BindingStyle.toEnumConstant(styleOp);

			newOp.setStyle(style);

			BindingUse use = BindingUse.toEnumConstant(useOp); 

			if(newOp.getMessageInput() == null){
				newOp.setMessageInput(new Message());
				newOp.getMessageInput().setSoapNamespace(nsWSDLOp);
				newOp.getMessageInput().setUse(use);
			}

			// setto valore dell'operationType di default (Profilo sincrono e' input/output, gli altri metto Input)
			if(opTypeOp == null){
				if(profcollop.equals(CostantiRegistroServizi.ONEWAY.getValue()))
					opTypeOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_IN;
				else 
					opTypeOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_INOUT;
			}  

			if(opTypeOp.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_INOUT)){
				if(newOp.getMessageOutput() == null){
					newOp.setMessageOutput(new Message());
					newOp.getMessageOutput().setSoapNamespace(nsWSDLOp);
					newOp.getMessageOutput().setUse(use);
				}
			}else {
				newOp.setMessageOutput(null); 
			}

			pt.addAzione(newOp);
			
			//imposto properties custom
			newOp.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(this.protocolProperties, this.consoleOperationType,null));
			
			boolean updateAccordo = 
					AccordiServizioParteComuneUtilities.createPortTypeOperation(apcCore.isEnableAutoMappingWsdlIntoAccordo(), apcCore, apcHelper, as, pt, userLogin);
			
			// cancello i file temporanei
			apcHelper.deleteBinaryProtocolPropertiesTmpFiles(this.protocolProperties);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idPT = pt.getId().intValue();
			if(updateAccordo) {		
				// ricarico l'accordo	
				as = apcCore.getAccordoServizioFull(idInt);
				for (PortType ptCheck : as.getPortTypeList()) {
					if(ptCheck.getNome().equals(pt.getNome())) {
						idPT = ptCheck.getId().intValue();
					}
				}
			}
			
			List<Operation> lista = apcCore.accordiPorttypeOperationList(idPT, ricerca);

			apcHelper.prepareAccordiPorttypeOperationsList(ricerca, lista, id, as,tipoAccordo,pt.getNome());

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS, ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS, ForwardParams.ADD());
		} 
	}
}
