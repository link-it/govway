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


package org.openspcoop2.web.ctrlstat.servlet.ac;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * accordiCooperazioneAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiCooperazioneAdd extends Action {
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		AccordiCooperazioneAddStrutsBean strutsBean = new AccordiCooperazioneAddStrutsBean();
		
		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		strutsBean.consoleOperationType = ConsoleOperationType.ADD;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.ADD; 

		try {
			AccordiCooperazioneHelper acHelper = new AccordiCooperazioneHelper(request, pd, session);
			
			// Preparo il menu
			acHelper.makeMenu();
			
			strutsBean.editMode = acHelper.getParametroEditMode(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			strutsBean.nome = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME);
			strutsBean.descr = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE);
			strutsBean.referente = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_REFERENTE);
			strutsBean.versione = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE);
			strutsBean.tipoProtocollo = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO);
			// patch per version spinner fino a che non si trova un modo piu' elegante
			/*if(ch.core.isBackwardCompatibilityAccordo11()){
				if("0".equals(strutsBean.versione))
					strutsBean.versione = "";
			}*/
			String privatoS = acHelper.getParametroBoolean(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO);
			strutsBean.privato = ServletUtils.isCheckBoxEnabled(privatoS); // privatoS != null && Costanti.CHECK_BOX_ENABLED.equals(privatoS) ? true : false;
			strutsBean.statoPackage = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_STATO);
			String tipoSICA = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_SICA);
			if("".equals(tipoSICA))
				tipoSICA = null;

			AccordiCooperazioneCore acCore = new AccordiCooperazioneCore();
			SoggettiCore soggettiCore = new SoggettiCore(acCore);

			// Tipi protocollo supportati
			List<String> listaTipiProtocollo = acCore.getProtocolliByFilter(request, session, true, false);
			
			// primo accesso inizializzo con il protocollo di default
			if(strutsBean.tipoProtocollo == null){
				strutsBean.tipoProtocollo = acCore.getProtocolloDefault(request, session, listaTipiProtocollo);
			}
			
			if(listaTipiProtocollo.isEmpty()) {
				pd.setMessage("Non risultano registrati soggetti", Costanti.MESSAGE_TYPE_INFO);
				pd.disableEditMode();

				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE, 
						ForwardParams.ADD());
			}

			//Carico la lista dei tipi di soggetti gestiti dal protocollo
			List<String> tipiSoggettiGestitiProtocollo = soggettiCore.getTipiSoggettiGestitiProtocollo(strutsBean.tipoProtocollo);

			// Prendo la lista di provider e la metto in un array
			String[] providersList = null;
			String[] providersListLabel = null;

			// Provider
			List<Soggetto> lista = null;
			if(acCore.isVisioneOggettiGlobale(userLogin)){
				lista = soggettiCore.soggettiRegistroList(null, new ConsoleSearch(true));
			}else{
				lista = soggettiCore.soggettiRegistroList(userLogin, new ConsoleSearch(true));
			}

			List<String> soggettiListTmp = new ArrayList<>();
			List<String> soggettiListLabelTmp = new ArrayList<>();
			soggettiListTmp.add("-");
			soggettiListLabelTmp.add("-");

			if (!lista.isEmpty()) {
				for (Soggetto soggetto : lista) {
					if(tipiSoggettiGestitiProtocollo.contains(soggetto.getTipo())){
						soggettiListTmp.add(soggetto.getId().toString());
						soggettiListLabelTmp.add(acHelper.getLabelNomeSoggetto(strutsBean.tipoProtocollo, soggetto.getTipo() , soggetto.getNome()));
					}
				}
			}
			providersList = soggettiListTmp.toArray(new String[1]);
			providersListLabel = soggettiListLabelTmp.toArray(new String[1]);

			strutsBean.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(strutsBean.tipoProtocollo);
			strutsBean.consoleDynamicConfiguration =  strutsBean.protocolFactory.createDynamicConfigurationConsole();
			strutsBean.registryReader = soggettiCore.getRegistryReader(strutsBean.protocolFactory); 
			strutsBean.configRegistryReader = soggettiCore.getConfigIntegrationReader(strutsBean.protocolFactory);
			
			// ID Accordo Null per default
			IDAccordo idAc = null;
			strutsBean.consoleConfiguration = strutsBean.consoleDynamicConfiguration.getDynamicConfigAccordoCooperazione(strutsBean.consoleOperationType, acHelper, 
					strutsBean.registryReader, strutsBean.configRegistryReader, idAc );
			strutsBean.protocolProperties = acHelper.estraiProtocolPropertiesDaRequest(strutsBean.consoleConfiguration, strutsBean.consoleOperationType);

			String postBackElementName = acHelper.getParameter(Costanti.POSTBACK_ELEMENT_NAME);

			// Controllo se ho modificato il protocollo, resetto il referente
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO)){
					strutsBean.referente = null;
					acHelper.deleteProtocolPropertiesBinaryParameters();
				}
			}


			// Se nome = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (ServletUtils.isEditModeInProgress(strutsBean.editMode)) {
				// setto la barra del titolo
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitleServletAdd(pd, AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE,
						AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				if(acHelper.isShowGestioneWorkflowStatoDocumenti()){
					if(strutsBean.statoPackage==null)
						strutsBean.statoPackage=StatiAccordo.bozza.toString();
				}else{
					strutsBean.statoPackage=StatiAccordo.finale.toString();
				}

				strutsBean.versione="1";

				if(strutsBean.nome == null)
					strutsBean.nome = "";

				if(strutsBean.descr == null)
					strutsBean.descr = "";

				if(strutsBean.referente == null)
					strutsBean.referente = "";

				strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoCooperazione(strutsBean.consoleConfiguration,
						strutsBean.consoleOperationType, acHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idAc);

				dati = acHelper.addAccordiCooperazioneToDati(dati, strutsBean.nome, strutsBean.descr, "0", tipoOp, strutsBean.referente,
						strutsBean.versione, providersList, providersListLabel, false,strutsBean.statoPackage,strutsBean.statoPackage, strutsBean.tipoProtocollo, listaTipiProtocollo,false);

				// aggiunta campi custom
				dati = acHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = acHelper.accordiCooperazioneCheckData(tipoOp, strutsBean.nome, strutsBean.descr, "0",
					strutsBean.referente, strutsBean.versione, strutsBean.privato, null);

			// updateDynamic
			if(isOk) {
				strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoCooperazione(strutsBean.consoleConfiguration,
						strutsBean.consoleOperationType, acHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idAc);
			}
			
			// Validazione base dei parametri custom 
			if(isOk){
				try{
					acHelper.validaProtocolProperties(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, strutsBean.protocolProperties);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			// Valido i parametri custom se ho gia' passato tutta la validazione prevista
			if(isOk){
				try{
					idAc = acHelper.getIDAccordoFromValues(strutsBean.nome, strutsBean.referente, strutsBean.versione);
					//validazione campi dinamici
					strutsBean.consoleDynamicConfiguration.validateDynamicConfigCooperazione(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, acHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idAc);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitleServletAdd(pd, AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE,
						AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoCooperazione(strutsBean.consoleConfiguration,
						strutsBean.consoleOperationType, acHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idAc);

				dati = acHelper.addAccordiCooperazioneToDati(dati, strutsBean.nome, strutsBean.descr, "0", tipoOp, 
						strutsBean.referente, strutsBean.versione, providersList, providersListLabel, strutsBean.privato,strutsBean.statoPackage,strutsBean.statoPackage, strutsBean.tipoProtocollo, listaTipiProtocollo,false);

				// aggiunta campi custom
				dati = acHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE,
						ForwardParams.ADD());
			}

			// Inserisco l'accordo nel db
			AccordoCooperazione ac = new AccordoCooperazione();
			ac.setNome(strutsBean.nome);
			ac.setDescrizione(strutsBean.descr);
			ac.setOraRegistrazione(Calendar.getInstance().getTime());
			if(strutsBean.referente!=null && !"".equals(strutsBean.referente) && !"-".equals(strutsBean.referente)){
				int idRef = 0;
				try {
					idRef = Integer.parseInt(strutsBean.referente);
				} catch (Exception e) {
					// ignore
				}
				if (idRef != 0) {
					int idReferente = Integer.parseInt(strutsBean.referente);
					Soggetto s = soggettiCore.getSoggettoRegistro(idReferente);			
					IdSoggetto acsr = new IdSoggetto();
					acsr.setTipo(s.getTipo());
					acsr.setNome(s.getNome());
					ac.setSoggettoReferente(acsr);
				}
			}else{
				ac.setSoggettoReferente(null);
			}
			if(strutsBean.versione!=null){
				ac.setVersione(Integer.parseInt(strutsBean.versione));
			}
			ac.setPrivato(strutsBean.privato ? Boolean.TRUE : Boolean.FALSE);
			ac.setSuperUser(userLogin);

			// stato	
			ac.setStatoPackage(strutsBean.statoPackage);

			// Check stato
			if(acHelper.isShowGestioneWorkflowStatoDocumenti()){

				try{
					acCore.validaStatoAccordoCooperazione(ac);
				}catch(ValidazioneStatoPackageException validazioneException){

					// Setto messaggio di errore
					pd.setMessage(validazioneException.toString());

					// setto la barra del titolo
					ServletUtils.setPageDataTitleServletAdd(pd, AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE,
							AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST);

					// preparo i campi
					List<DataElement> dati = new ArrayList<>();

					dati.add(ServletUtils.getDataElementForEditModeFinished());

					strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoCooperazione(strutsBean.consoleConfiguration,
							strutsBean.consoleOperationType, acHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idAc);

					dati = acHelper.addAccordiCooperazioneToDati(dati, strutsBean.nome, strutsBean.descr, "0", tipoOp, 
							strutsBean.referente, strutsBean.versione, providersList, providersListLabel, strutsBean.privato,strutsBean.statoPackage,strutsBean.statoPackage, strutsBean.tipoProtocollo, listaTipiProtocollo,false);

					// aggiunta campi custom
					dati = acHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties);

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE,
							ForwardParams.ADD());
				}
			}

			//imposto properties custom
			ac.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(strutsBean.protocolProperties, strutsBean.consoleOperationType,null));

			// effettuo le operazioni
			acCore.performCreateOperation(userLogin, acHelper.smista(), ac);
			
			// cancello i file temporanei
			acHelper.deleteBinaryProtocolPropertiesTmpFiles(strutsBean.protocolProperties);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			List<AccordoCooperazione> listaAC = null;
			if(acCore.isVisioneOggettiGlobale(userLogin)){
				listaAC = acCore.accordiCooperazioneList(null, ricerca);
			}else{
				listaAC = acCore.accordiCooperazioneList(userLogin, ricerca);
			}

			acHelper.prepareAccordiCooperazioneList(listaAC, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE, 
					ForwardParams.ADD());
		}  
	}
}
