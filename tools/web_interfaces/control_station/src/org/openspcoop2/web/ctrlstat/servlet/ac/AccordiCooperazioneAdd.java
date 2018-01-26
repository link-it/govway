/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesUtilities;
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

	private String nome, descr, referente, versione;
	private boolean privato;// showPrivato;
	private String statoPackage;

	private String editMode = null;

	private String tipoProtocollo = null;
	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory= null;
	private IRegistryReader registryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	private ConsoleInterfaceType consoleInterfaceType = null;

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		this.consoleOperationType = ConsoleOperationType.ADD;
		this.consoleInterfaceType = ProtocolPropertiesUtilities.getTipoInterfaccia(session); 

		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.ADD; 

		try {
			AccordiCooperazioneHelper acHelper = new AccordiCooperazioneHelper(request, pd, session);

			this.editMode = acHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			this.nome = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME);
			this.descr = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE);
			this.referente = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_REFERENTE);
			this.versione = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE);
			this.tipoProtocollo = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO);
			// patch per version spinner fino a che non si trova un modo piu' elegante
			/*if(ch.core.isBackwardCompatibilityAccordo11()){
				if("0".equals(this.versione))
					this.versione = "";
			}*/
			String privatoS = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO);
			this.privato = ServletUtils.isCheckBoxEnabled(privatoS); // privatoS != null && Costanti.CHECK_BOX_ENABLED.equals(privatoS) ? true : false;
			this.statoPackage = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_STATO);
			String tipoSICA = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_SICA);
			if("".equals(tipoSICA))
				tipoSICA = null;

			AccordiCooperazioneCore acCore = new AccordiCooperazioneCore();
			SoggettiCore soggettiCore = new SoggettiCore(acCore);

			// Preparo il menu
			acHelper.makeMenu();

			// Tipi protocollo supportati
			List<String> listaTipiProtocollo = acCore.getProtocolli();
			// primo accesso inizializzo con il protocollo di default
			if(this.tipoProtocollo == null){
				this.tipoProtocollo = acCore.getProtocolloDefault();
			}

			//Carico la lista dei tipi di soggetti gestiti dal protocollo
			List<String> tipiSoggettiGestitiProtocollo = soggettiCore.getTipiSoggettiGestitiProtocollo(this.tipoProtocollo);

			// Prendo la lista di provider e la metto in un array
			String[] providersList = null;
			String[] providersListLabel = null;

			// Provider
			/*
				int totProv = ch.getCounterFromDB("soggetti", "superuser", userLogin);
				if (totProv != 0) {
					providersList = new String[totProv+1];
					providersListLabel = new String[totProv+1];
					int i = 1;
					providersList[0]="-";
					providersListLabel[0]="-";

					String[] selectFields = new String[3];
					selectFields[0] = "id";
					selectFields[1] = "tipo_soggetto";
					selectFields[2] = "nome_soggetto";
					this.queryString = ch.getQueryStringForList(selectFields, "soggetti", "superuser", userLogin, "", 0, 0);
					this.stmt = con.prepareStatement(this.queryString);
					this.risultato = this.stmt.executeQuery();
					while (this.risultato.next()) {
						providersList[i] = "" + this.risultato.getInt("id");
						providersListLabel[i] = this.risultato.getString("tipo_soggetto") + "/" + this.risultato.getString("nome_soggetto");
						i++;
					}
					this.risultato.close();
					this.stmt.close();
				}else{
					providersList = new String[1];
					providersListLabel = new String[1];
					providersList[0]="-";
					providersListLabel[0]="-";
				}
			 */
			List<Soggetto> lista = null;
			if(acCore.isVisioneOggettiGlobale(userLogin)){
				lista = soggettiCore.soggettiRegistroList(null, new Search(true));
			}else{
				lista = soggettiCore.soggettiRegistroList(userLogin, new Search(true));
			}

			List<String> soggettiListTmp = new ArrayList<String>();
			List<String> soggettiListLabelTmp = new ArrayList<String>();
			soggettiListTmp.add("-");
			soggettiListLabelTmp.add("-");

			if (lista.size() > 0) {
				for (Soggetto soggetto : lista) {
					if(tipiSoggettiGestitiProtocollo.contains(soggetto.getTipo())){
						soggettiListTmp.add(soggetto.getId().toString());
						soggettiListLabelTmp.add(soggetto.getTipo() + "/" + soggetto.getNome());
					}
				}
			}
			providersList = soggettiListTmp.toArray(new String[1]);
			providersListLabel = soggettiListLabelTmp.toArray(new String[1]);

			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.tipoProtocollo);
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = soggettiCore.getRegistryReader(this.protocolFactory); 

			// ID Accordo Null per default
			IDAccordo idAc = null;
			this.consoleConfiguration = this.consoleDynamicConfiguration.getDynamicConfigAccordoCooperazione(this.consoleOperationType, this.consoleInterfaceType, this.registryReader, idAc );
			this.protocolProperties = acHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);

			String postBackElementName = acHelper.getParameter(Costanti.POSTBACK_ELEMENT_NAME);

			// Controllo se ho modificato il protocollo, resetto il referente
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO)){
					this.referente = null;
					acHelper.deleteProtocolPropertiesBinaryParameters();
				}
			}


			// Se nome = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (ServletUtils.isEditModeInProgress(this.editMode)) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				if(acCore.isShowGestioneWorkflowStatoDocumenti()){
					if(this.statoPackage==null)
						this.statoPackage=StatiAccordo.bozza.toString();
				}else{
					this.statoPackage=StatiAccordo.finale.toString();
				}

				//if(core.isBackwardCompatibilityAccordo11()){
				//	this.versione="0";
				//}else{
				this.versione="1";
				//}

				if(this.nome == null)
					this.nome = "";

				if(this.descr == null)
					this.descr = "";

				if(this.referente == null)
					this.referente = "";

				this.consoleDynamicConfiguration.updateDynamicConfigAccordoCooperazione(this.consoleConfiguration,
						this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, this.registryReader, idAc);

				dati = acHelper.addAccordiCooperazioneToDati(dati, this.nome, this.descr, "0", tipoOp, this.referente,
						this.versione, providersList, providersListLabel, false,this.statoPackage,this.statoPackage, this.tipoProtocollo, listaTipiProtocollo,false);

				// aggiunta campi custom
				dati = acHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = acHelper.accordiCooperazioneCheckData(tipoOp, this.nome, this.descr, "0",
					this.referente, this.versione, this.privato, null);

			// Validazione base dei parametri custom 
			if(isOk){
				try{
					acHelper.validaProtocolProperties(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			// Valido i parametri custom se ho gia' passato tutta la validazione prevista
			if(isOk){
				try{
					idAc = acHelper.getIDAccordoFromValues(this.nome, this.referente, this.versione);
					//validazione campi dinamici
					this.consoleDynamicConfiguration.validateDynamicConfigCooperazione(this.consoleConfiguration, this.consoleOperationType, this.protocolProperties, this.registryReader, idAc);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				this.consoleDynamicConfiguration.updateDynamicConfigAccordoCooperazione(this.consoleConfiguration,
						this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, this.registryReader, idAc);

				dati = acHelper.addAccordiCooperazioneToDati(dati, this.nome, this.descr, "0", tipoOp, 
						this.referente, this.versione, providersList, providersListLabel, this.privato,this.statoPackage,this.statoPackage, this.tipoProtocollo, listaTipiProtocollo,false);

				// aggiunta campi custom
				dati = acHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE,
						ForwardParams.ADD());
			}

			// Inserisco l'accordo nel db
			AccordoCooperazione ac = new AccordoCooperazione();
			ac.setNome(this.nome);
			ac.setDescrizione(this.descr);
			ac.setOraRegistrazione(Calendar.getInstance().getTime());
			if(this.referente!=null && !"".equals(this.referente) && !"-".equals(this.referente)){
				int idRef = 0;
				try {
					idRef = Integer.parseInt(this.referente);
				} catch (Exception e) {
				}
				if (idRef != 0) {
					int idReferente = Integer.parseInt(this.referente);
					Soggetto s = soggettiCore.getSoggettoRegistro(idReferente);			
					IdSoggetto acsr = new IdSoggetto();
					acsr.setTipo(s.getTipo());
					acsr.setNome(s.getNome());
					ac.setSoggettoReferente(acsr);
				}
			}else{
				ac.setSoggettoReferente(null);
			}
			if(this.versione!=null){
				ac.setVersione(Integer.parseInt(this.versione));
			}
			ac.setPrivato(this.privato ? Boolean.TRUE : Boolean.FALSE);
			ac.setSuperUser(userLogin);

			// stato
			ac.setStatoPackage(this.statoPackage);

			// Check stato
			if(acCore.isShowGestioneWorkflowStatoDocumenti()){

				try{
					acCore.validaStatoAccordoCooperazione(ac);
				}catch(ValidazioneStatoPackageException validazioneException){

					// Setto messaggio di errore
					pd.setMessage(validazioneException.toString());

					// setto la barra del titolo
					List<Parameter> lstParam = new ArrayList<Parameter>();
					lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, null));
					lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

					ServletUtils.setPageDataTitle(pd, lstParam);

					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());

					this.consoleDynamicConfiguration.updateDynamicConfigAccordoCooperazione(this.consoleConfiguration,
							this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, this.registryReader, idAc);

					dati = acHelper.addAccordiCooperazioneToDati(dati, this.nome, this.descr, "0", tipoOp, 
							this.referente, this.versione, providersList, providersListLabel, this.privato,this.statoPackage,this.statoPackage, this.tipoProtocollo, listaTipiProtocollo,false);

					// aggiunta campi custom
					dati = acHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties);

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE,
							ForwardParams.ADD());
				}
			}

			//imposto properties custom
			ac.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolProperties(this.protocolProperties, this.consoleOperationType,null));

			// effettuo le operazioni
			acCore.performCreateOperation(userLogin, acHelper.smista(), ac);
			
			// cancello i file temporanei
			acHelper.deleteBinaryProtocolPropertiesTmpFiles(this.protocolProperties);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<AccordoCooperazione> listaAC = null;
			if(acCore.isVisioneOggettiGlobale(userLogin)){
				listaAC = acCore.accordiCooperazioneList(null, ricerca);
			}else{
				listaAC = acCore.accordiCooperazioneList(userLogin, ricerca);
			}

			acHelper.prepareAccordiCooperazioneList(listaAC, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE, 
					ForwardParams.ADD());
		}  
	}
}
