/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
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
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
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
 * accordiCooperazioneChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiCooperazioneChange extends Action {

	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory= null;
	private IRegistryReader registryReader = null; 
	private IConfigIntegrationReader configRegistryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	private String protocolPropertiesSet = null;
	private String editMode = null;
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();

		List<String> listaTipiProtocollo = null;

		boolean used = false;
		
		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		this.consoleOperationType = ConsoleOperationType.CHANGE;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.CHANGE;
		List<ProtocolProperty> oldProtocolPropertyList = null;


		try {
			AccordiCooperazioneHelper acHelper = new AccordiCooperazioneHelper(request, pd, session);
			
			this.editMode = acHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			this.protocolPropertiesSet = acHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_SET);

			String id = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID);
			int idAcc = Integer.parseInt(id);
			String descr = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE);
			String referente = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_REFERENTE);
			String versione = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE);
			String tipoProtocollo = acHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO);
			String actionConfirm = acHelper.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);
			// patch per version spinner fino a che non si trova un modo piu' elegante
			/*if(ch.core.isBackwardCompatibilityAccordo11()){
				if("0".equals(versione))
					versione = "";
			}*/
			String privatoS = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO);
			boolean privato = ServletUtils.isCheckBoxEnabled(privatoS);
			String statoPackage = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_STATO);

			String tipoSICA = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_SICA);
			if("".equals(tipoSICA))
				tipoSICA = null;

			AccordiCooperazioneCore acCore = new AccordiCooperazioneCore();
			SoggettiCore soggettiCore = new SoggettiCore(acCore);

			// Preparo il menu
			acHelper.makeMenu();

			// prelevo l'accordo
			AccordoCooperazione ac = acCore.getAccordoCooperazione(idAcc);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(acCore);
			IDAccordoCooperazione idAccordoOLD = idAccordoCooperazioneFactory.getIDAccordoFromValues(ac.getNome(),
					BeanUtilities.getSoggettoReferenteID(ac.getSoggettoReferente()),ac.getVersione());
			String uriAS = idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordoOLD);
			String titleAS = acHelper.getLabelIdAccordoCooperazione(ac);
			String oldStatoPackage = ac.getStatoPackage();	

			// Prendo il nome dell'accordo
			String nome = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME);
			if ((nome == null) || nome.equals("")) {
				pd.setMessage(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_NOME_ACCORDO_NECESSARIO);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				return ServletUtils.getStrutsForwardGeneralError(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE, 
						ForwardParams.CHANGE());
			}

			try{
				FiltroRicercaAccordi filtroRicerca = new FiltroRicercaAccordi();
				filtroRicerca.setServizioComposto(true);
				filtroRicerca.setIdAccordoCooperazione(idAccordoOLD);

				List<IDAccordo> allIdAccordiServizio = apcCore.getAllIdAccordiServizio(filtroRicerca);

				if(allIdAccordiServizio != null && allIdAccordiServizio.size() > 0)
					used = true;
				
			}catch(DriverRegistroServiziNotFound de){
				used = false;
			}catch(Exception e){
				// in caso di eccezione per sicurezza non faccio modificare l'accordo
				used = true;
			}

			// lista dei protocolli supportati
			listaTipiProtocollo = acCore.getProtocolliByFilter(session, true, false);

			// se il protocollo e' null (primo accesso ) lo ricavo dall'accordo di servizio
			if(tipoProtocollo == null){
				if(ac!=null && ac.getSoggettoReferente()!=null){
					tipoProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(ac.getSoggettoReferente().getTipo());
				}
				else{
					tipoProtocollo = acCore.getProtocolloDefault(session, listaTipiProtocollo);
				}
			}

			List<String> tipiSoggettiGestitiProtocollo = soggettiCore.getTipiSoggettiGestitiProtocollo(tipoProtocollo);

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
					String queryString = ch.getQueryStringForList(selectFields, "soggetti", "superuser", userLogin, "", 0, 0);
					PreparedStatement stmt = con.prepareStatement(queryString);
					ResultSet risultato = stmt.executeQuery();
					while (risultato.next()) {
						providersList[i] = "" + risultato.getInt("id");
						providersListLabel[i] = risultato.getString("tipo_soggetto") + "/" + risultato.getString("nome_soggetto");
						i++;
					}
					risultato.close();
					stmt.close();
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
						soggettiListLabelTmp.add(acHelper.getLabelNomeSoggetto(tipoProtocollo, soggetto.getTipo() , soggetto.getNome()));
					}
				}
			}
			providersList = soggettiListTmp.toArray(new String[1]);
			providersListLabel = soggettiListLabelTmp.toArray(new String[1]);
			
			IdSoggetto acsr = ac.getSoggettoReferente();
			if (acsr != null) {
				Soggetto s = soggettiCore.getSoggettoRegistro(new IDSoggetto(acsr.getTipo(),acsr.getNome()));
				referente = "" + s.getId();
			}else{
				referente = "-";
			}
			
			if(versione == null && ac.getVersione()!=null)
				versione = ac.getVersione().intValue()+"";
			
			IDAccordo idAcOLD = acHelper.getIDAccordoFromValues(nome, referente, versione);
			
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipoProtocollo);
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = soggettiCore.getRegistryReader(this.protocolFactory); 
			this.configRegistryReader = soggettiCore.getConfigIntegrationReader(this.protocolFactory);
			this.consoleConfiguration = this.consoleDynamicConfiguration.getDynamicConfigAccordoCooperazione(this.consoleOperationType, acHelper, 
					this.registryReader, this.configRegistryReader, idAcOLD);
					
			this.protocolProperties = acHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);
			
			oldProtocolPropertyList = ac.getProtocolPropertyList();
			
			if(this.protocolPropertiesSet == null){
				ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(this.protocolProperties, oldProtocolPropertyList, this.consoleOperationType);
			}
			
			Properties propertiesProprietario = new Properties();
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, id);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_COOPERAZIONE);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, uriAS);
			
			Parameter urlOrig = new Parameter(uriAS,
					AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_CHANGE+"?"+
							AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID+"="+id+"&"+
							AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME+"="+nome);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE, URLEncoder.encode( urlOrig.getValue(), "UTF-8"));
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, tipoProtocollo);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO, "");

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if (ServletUtils.isEditModeInProgress(this.editMode)) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
				lstParam.add(new Parameter(titleAS, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				if(descr==null)
					descr = ac.getDescrizione();

				privato = ac.getPrivato()!=null && ac.getPrivato();
				if(statoPackage==null)
					statoPackage = ac.getStatoPackage();

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				
				// update della configurazione 
				this.consoleDynamicConfiguration.updateDynamicConfigAccordoCooperazione(this.consoleConfiguration, this.consoleOperationType, acHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idAcOLD);

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = acHelper.addHiddenFieldsToDati(tipoOp, id, null, null, dati);

				dati = acHelper.
						addAccordiCooperazioneToDati(dati, nome, descr, id, tipoOp, referente,
								versione, providersList, providersListLabel, privato,statoPackage,oldStatoPackage
								,tipoProtocollo, listaTipiProtocollo,used);
				
				// aggiunta campi custom
				dati = acHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);


				pd.setDati(dati);

				if(acHelper.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(ac.getStatoPackage())){
					pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
				}

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE,
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = acHelper.accordiCooperazioneCheckData(tipoOp, nome, descr, id, referente, versione,privato,idAccordoOLD);
			
			// updateDynamic
			if(isOk) {
				this.consoleDynamicConfiguration.updateDynamicConfigAccordoCooperazione(this.consoleConfiguration, this.consoleOperationType, acHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idAcOLD);
			}
			
			// Validazione base dei parametri custom 
			if(isOk){
				try{
					acHelper.validaProtocolProperties(this.consoleConfiguration, this.consoleOperationType,  this.protocolProperties);
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
					this.consoleDynamicConfiguration.validateDynamicConfigCooperazione(this.consoleConfiguration, this.consoleOperationType, acHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idAcOLD);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}
			
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
				lstParam.add(new Parameter(titleAS, null));

				ServletUtils.setPageDataTitle(pd, lstParam);
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				
				// update della configurazione 
				this.consoleDynamicConfiguration.updateDynamicConfigAccordoCooperazione(this.consoleConfiguration, this.consoleOperationType, acHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idAcOLD);

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = acHelper.addHiddenFieldsToDati(tipoOp, id, null, null, dati);

				dati = acHelper.addAccordiCooperazioneToDati(dati, nome, descr, id,
						tipoOp, referente, versione, providersList, providersListLabel,
						privato,statoPackage,oldStatoPackage,tipoProtocollo, listaTipiProtocollo,used);
				
				// aggiunta campi custom
				dati = acHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE,
						ForwardParams.CHANGE());
			}


			// I dati dell'utente sono validi, lo informo che l'accordo e' utilizzato da accordi di servizio composti 
			if(used && actionConfirm == null){

				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
				lstParam.add(new Parameter(titleAS, null));

				ServletUtils.setPageDataTitle(pd, lstParam);
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				
				// update della configurazione 
				this.consoleDynamicConfiguration.updateDynamicConfigAccordoCooperazione(this.consoleConfiguration, this.consoleOperationType, acHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idAcOLD);

				dati.addElement(ServletUtils.getDataElementForEditModeInProgress());

				dati = acHelper.addHiddenFieldsToDati(tipoOp, id, null, null, dati);
				
				dati = acHelper.addAccordiCooperazioneToDati(dati, nome, descr, id,
						tipoOp, referente, versione, providersList, providersListLabel,
						privato,statoPackage,oldStatoPackage,tipoProtocollo, listaTipiProtocollo,used);

				dati = acHelper.addAccordiCooperazioneToDatiAsHidden(dati, nome, descr, id,
						tipoOp, referente, versione, providersList, providersListLabel,
						privato,statoPackage,oldStatoPackage,tipoProtocollo, listaTipiProtocollo,used);
			
				// aggiunta campi custom
				dati = acHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				
				// aggiunta campi custom come hidden, quelli sopra vengono bruciati dal no-edit
				dati = acHelper.addProtocolPropertiesToDatiAsHidden(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				
				String msg = "Attenzione, esistono Accordi di Servizio Composto che riferiscono l''Accordo di Cooperazione [{0}] che si sta modificando, continuare?";
				String uriAccordo = idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordoOLD);

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

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return  ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE,
						ForwardParams.CHANGE());
			}

			// Modifico i dati dell'accordo nel db
			ac.setNome(nome);
			ac.setDescrizione(descr);
			if(referente!=null && !"".equals(referente) && !"-".equals(referente)){
				int idRef = 0;
				try {
					idRef = Integer.parseInt(referente);
				} catch (Exception e) {
				}
				if (idRef != 0) {
					int idReferente = Integer.parseInt(referente);
					Soggetto s = soggettiCore.getSoggettoRegistro(idReferente);			
					acsr = new IdSoggetto();
					acsr.setTipo(s.getTipo());
					acsr.setNome(s.getNome());
					ac.setSoggettoReferente(acsr);
				}
			}else{
				ac.setSoggettoReferente(null);
			}
			if(versione!=null){
				ac.setVersione(Integer.parseInt(versione));
			}
			ac.setOraRegistrazione(Calendar.getInstance().getTime());
			ac.setPrivato(privato ? Boolean.TRUE : Boolean.FALSE);
			ac.setSuperUser(userLogin);

			ac.setOldIDAccordoForUpdate(idAccordoOLD);


			// stato
			ac.setStatoPackage(statoPackage);


			//  Check stato
			if(acHelper.isShowGestioneWorkflowStatoDocumenti()){

				try{
					acCore.validaStatoAccordoCooperazione(ac);
				}catch(ValidazioneStatoPackageException validazioneException){

					// Setto messaggio di errore
					pd.setMessage(validazioneException.toString());

					// setto la barra del titolo
					List<Parameter> lstParam = new ArrayList<Parameter>();
					lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
					lstParam.add(new Parameter(titleAS, null));

					ServletUtils.setPageDataTitle(pd, lstParam);

					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();
					
					// update della configurazione 
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoCooperazione(this.consoleConfiguration, this.consoleOperationType, acHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idAcOLD);

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());

					dati = acHelper.addHiddenFieldsToDati(tipoOp, id, null, null, dati);

					dati = acHelper.addAccordiCooperazioneToDati(dati, nome, descr, id,
							tipoOp, referente, versione, providersList, providersListLabel,
							privato,statoPackage,oldStatoPackage,tipoProtocollo, listaTipiProtocollo,used);
					
					// aggiunta campi custom
					dati = acHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE,
							ForwardParams.CHANGE());					
				}
			}




			//  Oggetti da modificare
			List<Object> oggettiDaAggiornare = new ArrayList<Object>();


			// Update accordo
			oggettiDaAggiornare.add(ac);



			// Aggiornamento accordi di servizio che possiedono tale accordo di cooperazione come riferimento
			// Essendo servizi composti
			if(idAccordoCooperazioneFactory.getUriFromAccordo(ac).equals(idAccordoCooperazioneFactory.getUriFromIDAccordo(ac.getOldIDAccordoForUpdate()))==false){

				List<AccordoServizioParteComune> ass = apcCore.accordiServizioWithAccordoCooperazione(ac.getOldIDAccordoForUpdate());
				for(int i=0; i<ass.size(); i++){
					AccordoServizioParteComune as = ass.get(i);
					if(as.getServizioComposto()!=null){
						as.getServizioComposto().setAccordoCooperazione(idAccordoCooperazioneFactory.getUriFromAccordo(ac));
						oggettiDaAggiornare.add(as);
						//System.out.println("As SERVIZIO COMPONENTE ["+IDAccordo.getUriFromAccordo(as)+"]");
					}
				}

			}

			//imposto properties custom
			ac.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(this.protocolProperties, this.consoleOperationType, oldProtocolPropertyList));

			acCore.performUpdateOperation(userLogin, acHelper.smista(), oggettiDaAggiornare.toArray());

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(request, session, Search.class);

			List<AccordoCooperazione> lista2 = null;
			if(acCore.isVisioneOggettiGlobale(userLogin)){
				lista2 = acCore.accordiCooperazioneList(null, ricerca);
			}else{
				lista2 = acCore.accordiCooperazioneList(userLogin, ricerca);
			}

			acHelper.prepareAccordiCooperazioneList(lista2, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE, 
					ForwardParams.CHANGE());
		}  
	}
}
