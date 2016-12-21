/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import java.util.Iterator;
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
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
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
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.dao.Ruolo;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesUtilities;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
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
	private String id, descr, profcoll, filtrodup, confric, idcoll, consord, scadenza, utilizzo, referente, versione,accordoCooperazioneId,statoPackage,
	tipoAccordo, tipoProtocollo, actionConfirm, backToStato;
	boolean utilizzoSenzaAzione, showUtilizzoSenzaAzione = false,privato ,isServizioComposto,  validazioneDocumenti = true;

	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory= null;
	private IRegistryReader registryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	private ConsoleInterfaceType consoleInterfaceType = null;
	private String protocolPropertiesSet = null;

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
		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();

		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		this.consoleOperationType = ConsoleOperationType.CHANGE;
		this.consoleInterfaceType = ProtocolPropertiesUtilities.getTipoInterfaccia(session); 

		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.CHANGE;
		List<ProtocolProperty> oldProtocolPropertyList = null;

		AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);
		
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
		PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apcCore);
		PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apcCore);
		AccordiCooperazioneCore acCore = new AccordiCooperazioneCore(apcCore);
		String labelAccordoServizio = AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(this.tipoAccordo);

		Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

		AccordoServizioParteComune as = apcCore.getAccordoServizio(idAcc);
		boolean asWithAllegati = (as.sizeAllegatoList()>0 || as.sizeSpecificaSemiformaleList()>0 || as.getByteWsdlDefinitorio()!=null);

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
			listaTipiProtocollo = apcCore.getProtocolli();

			// se il protocollo e' null (primo accesso ) lo ricavo dall'accordo di servizio
			if(this.tipoProtocollo == null){
				if(as!=null && as.getSoggettoReferente()!=null){
					this.tipoProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo());
				}
				else{
					this.tipoProtocollo = apsCore.getProtocolloDefault();
				}
			}

			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.tipoProtocollo);
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = soggettiCore.getRegistryReader(this.protocolFactory); 
			this.consoleConfiguration = this.consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteComune(this.consoleOperationType, this.consoleInterfaceType, this.registryReader, idAccordoOLD );
			this.protocolProperties = apcHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);

			// se this.initProtocolPropertiesFromDb = true allora leggo le properties dal db... 

			try{
				AccordoServizioParteComune apcOLD = this.registryReader.getAccordoServizioParteComune(idAccordoOLD);
				oldProtocolPropertyList = apcOLD.getProtocolPropertyList(); 

			}catch(RegistryNotFound r){}

			if(this.protocolPropertiesSet == null){
				ProtocolPropertiesUtils.mergeProtocolProperties(this.protocolProperties, oldProtocolPropertyList, this.consoleOperationType);
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
						soggettiListLabelTmp.add(soggetto.getTipo() + "/" + soggetto.getNome());
					}
				}
			}
			providersList = soggettiListTmp.toArray(new String[1]);
			providersListLabel = soggettiListLabelTmp.toArray(new String[1]);

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
					accordiCooperazioneEsistentiLabel[i] = idAccordoCooperazioneFactory.getUriFromAccordo(acCore.getAccordoCooperazione(singleAC.getId()));
					i++;
				}
			} else {
				accordiCooperazioneEsistenti = new String[1];
				accordiCooperazioneEsistentiLabel = new String[1];
				accordiCooperazioneEsistenti[0]="-";
				accordiCooperazioneEsistentiLabel[0]="-";
			}

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
		}

		//se passo dal link diretto di ripristino stato imposto il nuovo stato
		if(this.backToStato != null)
			this.statoPackage = this.backToStato;

		String uriAS = idAccordoFactory.getUriFromIDAccordo(idAccordoOLD);
		String oldStatoPackage = as.getStatoPackage();			
		
		Properties propertiesProprietario = new Properties();
		propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, this.id);
		propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_SERVIZIO_PARTE_COMUNE);
		propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, uriAS);
		propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE, URLEncoder.encode( AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE + "?" + request.getQueryString(), "UTF-8"));
		propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, this.tipoProtocollo);
		propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO, this.tipoAccordo);

		// Se idhid = null, devo visualizzare la pagina per la modifica dati
		if(ServletUtils.isEditModeInProgress(this.editMode)){

			try {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, labelAccordoServizio,
						AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
								AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getName()+"="+
								AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getValue(),
								uriAS);

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

			} catch (Exception ex) {
				return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), ex, pd, session, gd, mapping, 
						AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
			}


			if( this.backToStato == null){
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration,
						this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, this.registryReader, idAccordoOLD); 

				dati = apcHelper.addAccordiToDati(dati, nome, this.descr, this.profcoll, "", "", "", "", 
						this.filtrodup, this.confric, this.idcoll, this.consord, this.scadenza, this.id, tipoOp, 
						this.showUtilizzoSenzaAzione, this.utilizzoSenzaAzione,this.referente,this.versione,providersList,providersListLabel,
						this.privato,this.isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
						this.accordoCooperazioneId,this.statoPackage,oldStatoPackage, this.tipoAccordo, this.validazioneDocumenti,
						this.tipoProtocollo, listaTipiProtocollo,used,asWithAllegati);

				// aggiunta campi custom
				dati = apcHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);

				pd.setDati(dati);

				if(apcCore.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(as.getStatoPackage())){
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
				"", "", "", "", 
				this.filtrodup, this.confric, this.idcoll, this.consord, 
				this.scadenza, this.id,this.referente,this.versione,this.accordoCooperazioneId,this.privato,visibilitaAccordoCooperazione,idAccordoOLD, 
				"", "", "", this.validazioneDocumenti,this.tipoProtocollo,this.backToStato);

		// Validazione base dei parametri custom 
		if(isOk){
			try{
				apcHelper.validaProtocolProperties(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties);
			}catch(ProtocolException e){
				pd.setMessage(e.getMessage());
				isOk = false;
			}
		}

		// Valido i parametri custom se ho gia' passato tutta la validazione prevista
		if(isOk){
			try{
				//validazione campi dinamici
				this.consoleDynamicConfiguration.validateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration, this.consoleOperationType, this.protocolProperties, this.registryReader, idAccordoOLD); 
			}catch(ProtocolException e){
				pd.setMessage(e.getMessage());
				isOk = false;
			}
		}

		if (!isOk) {

			// setto la barra del titolo
			ServletUtils.setPageDataTitle_ServletChange(pd, labelAccordoServizio,
					AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getName()+"="+
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getValue(),
							uriAS);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati.addElement(ServletUtils.getDataElementForEditModeFinished());

			// update della configurazione 
			this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration,
					this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, this.registryReader, idAccordoOLD); 

			dati = apcHelper.addAccordiToDati(dati, nome, this.descr, this.profcoll, "", "", "", "", 
					this.filtrodup, this.confric, this.idcoll, this.consord, this.scadenza, this.id, tipoOp, 
					this.showUtilizzoSenzaAzione, this.utilizzoSenzaAzione,this.referente,this.versione,providersList,providersListLabel,
					this.privato,this.isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
					this.accordoCooperazioneId,this.statoPackage,oldStatoPackage, this.tipoAccordo, this.validazioneDocumenti,
					this.tipoProtocollo, listaTipiProtocollo,used,asWithAllegati);

			// aggiunta campi custom
			dati = apcHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
		}

		// I dati dell'utente sono validi, lo informo che l'accordo e' utilizzato da asps 
		if( this.actionConfirm == null){
			if(used || this.backToStato != null){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, labelAccordoServizio,
						AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
								AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getName()+"="+
								AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getValue(),
								uriAS);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeInProgress());

				// update della configurazione 
				this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration,
						this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, this.registryReader, idAccordoOLD); 

				dati = apcHelper.addAccordiToDatiAsHidden(dati, nome, this.descr, this.profcoll, "", "", "", "", 
						this.filtrodup, this.confric, this.idcoll, this.consord, this.scadenza, this.id, tipoOp, 
						this.showUtilizzoSenzaAzione, this.utilizzoSenzaAzione,this.referente,this.versione,providersList,providersListLabel,
						this.privato,this.isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
						this.accordoCooperazioneId,this.statoPackage,oldStatoPackage, this.tipoAccordo, this.validazioneDocumenti,this.tipoProtocollo, listaTipiProtocollo,used);

				// aggiunta campi custom
				dati = apcHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);

				pd.setDati(dati);

				String uriAccordo = idAccordoFactory.getUriFromIDAccordo(idAccordoOLD);
				String msg = ""; 
				if(used)
					msg = "Attenzione, esistono delle erogazioni che riferiscono l''accordo [{0}] che si sta modificando, continuare?";

				if(this.backToStato != null){
					msg = "&Egrave; stato richiesto di ripristinare lo stato dell''accordo [{0}] in operativo. Tale operazione permetter&agrave; successive modifiche all''accordo. Vuoi procedere?";
				}

				pd.setMessage(MessageFormat.format(msg, uriAccordo));

				String[][] bottoni = { 
						{ Costanti.LABEL_MONITOR_BUTTON_ANNULLA, 
							Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_PREFIX +
							Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_SUFFIX

						},
						{ Costanti.LABEL_MONITOR_BUTTON_OK,
							Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_PREFIX +
							Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_SUFFIX }};

				pd.setBottoni(bottoni );

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeConfirm(mapping, 
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



		// stato
		as.setStatoPackage(this.statoPackage);

		// Check stato
		if(apcCore.isShowGestioneWorkflowStatoDocumenti()){

			try{
				boolean utilizzoAzioniDiretteInAccordoAbilitato = apcCore.isShowAccordiColonnaAzioni();
				apcCore.validaStatoAccordoServizio(as, utilizzoAzioniDiretteInAccordoAbilitato);
			}catch(ValidazioneStatoPackageException validazioneException){

				// Setto messaggio di errore
				pd.setMessage(validazioneException.toString());

				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, labelAccordoServizio,
						AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
								AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getName()+"="+
								AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getValue(),
								uriAS);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration,
						this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, this.registryReader, idAccordoOLD); 

				dati = apcHelper.addAccordiToDati(dati, nome, this.descr, this.profcoll, "", "", "", "", 
						this.filtrodup, this.confric, this.idcoll, this.consord, this.scadenza, this.id, tipoOp, 
						this.showUtilizzoSenzaAzione, this.utilizzoSenzaAzione,this.referente,this.versione,providersList,providersListLabel,
						this.privato,this.isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
						this.accordoCooperazioneId,this.statoPackage,oldStatoPackage, this.tipoAccordo, this.validazioneDocumenti,
						this.tipoProtocollo, listaTipiProtocollo,used,asWithAllegati);

				// aggiunta campi custom
				dati = apcHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
			}
		}


		//imposto properties custom
		as.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolProperties(this.protocolProperties, this.consoleOperationType, oldProtocolPropertyList));


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
							ServletUtils.setPageDataTitle_ServletChange(pd, labelAccordoServizio,
									AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
											AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getName()+"="+
											AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getValue(),
											uriAS);

							// preparo i campi
							Vector<DataElement> dati = new Vector<DataElement>();

							dati.addElement(ServletUtils.getDataElementForEditModeFinished());

							// update della configurazione 
							this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration,
									this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, this.registryReader, idAccordoOLD); 

							dati = apcHelper.addAccordiToDati(dati, nome, this.descr, this.profcoll, "", "", "", "", 
									this.filtrodup, this.confric, this.idcoll, this.consord, this.scadenza, this.id, tipoOp, 
									this.showUtilizzoSenzaAzione, this.utilizzoSenzaAzione,this.referente,this.versione,providersList,providersListLabel,
									this.privato,this.isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
									this.accordoCooperazioneId,this.statoPackage,oldStatoPackage, this.tipoAccordo, this.validazioneDocumenti,
									this.tipoProtocollo, listaTipiProtocollo,used,asWithAllegati);

							// aggiunta campi custom
							dati = apcHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);

							pd.setDati(dati);

							ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

							return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
						}
					}

				}

			}

			Object[] operazioniDaEffettuare = new Object[1]; // almeno 1, l'accordo esiste!
			Vector<Object> operazioniList = new Vector<Object>();
			operazioniList.add(as);


			IDAccordo idNEW = idAccordoFactory.getIDAccordoFromAccordo(as);
			if(idNEW.equals(idAccordoOLD)==false){

				String newURI = idAccordoFactory.getUriFromAccordo(as);

				// Cerco i servizi in cui devo cambiare la URI dell'accordo
				List<AccordoServizioParteSpecifica> servizi = apsCore.serviziByAccordoFilterList(idAccordoOLD);
				if(servizi!=null && servizi.size()>0){
					while(servizi.size()>0){
						AccordoServizioParteSpecifica s = servizi.remove(0);
						s.setAccordoServizioParteComune(newURI);
						operazioniList.add(s);
					}
				}

				// Cerco le porte delegate create automaticamente per il ruolo associato ad un servizio applicativo
				List<PortaDelegata> porteDelegate = porteDelegateCore.getPorteDelegateByAccordoRuolo(idAccordoOLD);
				if(porteDelegate!=null && porteDelegate.size()>0){

					IDAccordo idAccordoCorrelatoOLD = idAccordoFactory.getIDAccordoFromValues(idAccordoOLD.getNome()+"Correlato", idAccordoOLD.getSoggettoReferente(), idAccordoOLD.getVersione());
					IDAccordo idAccordoCorrelatoNEW = idAccordoFactory.getIDAccordoFromValues(idNEW.getNome()+"Correlato", idNEW.getSoggettoReferente(), idNEW.getVersione());

					while(porteDelegate.size()>0){
						PortaDelegata portaDelegata = porteDelegate.remove(0);

						String oldName = portaDelegata.getNome();

						String newName = null;
						String nomeRuoloNEW = null;
						String nomeRuoloOLD = null;
						String correlatoOLD = Ruolo.getNomeRuoloByIDAccordo(idAccordoCorrelatoOLD);
						String vecchioOLD = Ruolo.getNomeRuoloByIDAccordo(idAccordoOLD);
						if( oldName.indexOf(correlatoOLD) >= 0 ){
							// Il ruolo e' Correlato
							nomeRuoloNEW = Ruolo.getNomeRuoloByIDAccordo(idAccordoCorrelatoNEW);
							nomeRuoloOLD = correlatoOLD;
						}else if( oldName.indexOf(vecchioOLD) >= 0){
							// Il ruolo non e' Correlato
							nomeRuoloNEW = Ruolo.getNomeRuoloByIDAccordo(idNEW);
							nomeRuoloOLD = vecchioOLD;
						}else{
							// porta delegata da non considerare. Non dovrei mai entrare in questo caso
							continue;
						}

						// nome e location PD
						IDPortaDelegata oldIDPortaDelegataForUpdate = new IDPortaDelegata();
						oldIDPortaDelegataForUpdate.setNome(portaDelegata.getNome());
						portaDelegata.setOldIDPortaDelegataForUpdate(oldIDPortaDelegataForUpdate);
						newName = oldName.replace(nomeRuoloOLD, nomeRuoloNEW);
						portaDelegata.setNome(newName);

						// descrizione
						if(portaDelegata.getDescrizione()!=null){
							portaDelegata.setDescrizione(portaDelegata.getDescrizione().replace(nomeRuoloOLD, nomeRuoloNEW));
						}

						// Azione
						if(portaDelegata.getAzione()!=null && 
								portaDelegata.getAzione().getPattern()!=null &&
								PortaDelegataAzioneIdentificazione.URL_BASED.equals(portaDelegata.getAzione().getIdentificazione()) ){
							portaDelegata.getAzione().setPattern(portaDelegata.getAzione().getPattern().replace(nomeRuoloOLD, nomeRuoloNEW));
						}else{
							// porta delegata da non considerare. La porta delegata non rispetti i criteri automatici di creazione.
							continue;
						}

						operazioniList.add(portaDelegata);

					}
				}
			}


			operazioniDaEffettuare = operazioniList.toArray(operazioniDaEffettuare);
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), operazioniDaEffettuare);

			// preparo lista
			List<AccordoServizioParteComune> lista = AccordiServizioParteComuneUtilities.accordiList(apcCore, userLogin, ricerca, this.tipoAccordo);
			//			if(apcCore.isVisioneOggettiGlobale(userLogin)){
			//				lista = apcCore.accordiServizioParteComuneList(null, ricerca);
			//			}else{
			//				lista = apcCore.accordiServizioParteComuneList(userLogin, ricerca);
			//			}
			apcHelper.prepareAccordiList(lista, ricerca, this.tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());	

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
		} 

	}
}
