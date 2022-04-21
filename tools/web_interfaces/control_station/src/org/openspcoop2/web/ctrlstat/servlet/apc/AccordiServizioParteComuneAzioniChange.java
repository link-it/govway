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


package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.net.URLEncoder;
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
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoAzione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
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
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
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
 * accordiAzioniChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneAzioniChange extends Action {

	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory= null;
	private IRegistryReader registryReader = null; 
	private IConfigIntegrationReader configRegistryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	private String editMode = null;
	private String protocolPropertiesSet = null;

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();

		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		this.consoleOperationType = ConsoleOperationType.CHANGE;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.CHANGE;
		List<ProtocolProperty> oldProtocolPropertyList = null;

		try {
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);
			
			this.editMode = apcHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			this.protocolPropertiesSet = apcHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_SET);

			String id = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			int idAccordo = 0;
			try {
				idAccordo = Integer.parseInt(id);
			} catch (Exception e) {
			}
			String nomeaz = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_NOME);
			if (nomeaz == null) {
				nomeaz = "";
			}
			String azicorr = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_CORRELATA);
			String profProtocollo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_PROFILO_BUSTA);
			String profcoll = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_PROFILO_COLLABORAZIONE);
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

			// Prendo il nome dal db
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(apcCore);
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);

			AccordoServizioParteComune as = apcCore.getAccordoServizioFull(Long.valueOf(idAccordo));
			String uriAS = idAccordoFactory.getUriFromAccordo(as);
			String labelASTitle = apcHelper.getLabelIdAccordo(as); 
			IDAccordo idAs = idAccordoFactory.getIDAccordoFromAccordo(as);

			String protocollo = null;
			//calcolo del protocollo implementato dall'accordo
			IdSoggetto soggettoReferente = as.getSoggettoReferente();
			String tipoSoggettoReferente = soggettoReferente.getTipo();
			protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggettoReferente);

			// Prendo la lista di azioni dell'accordo
			// (tranne quella che sto modificando)
			// e ne metto i nomi in un array di stringhe
			// prendo la lista delle azioni correlate con profilo
			// asincronoAsimmetrico
			List<Azione> azioniCorrelate = apcCore.accordiAzioniList(idAccordo, CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.toString(), new Search(true));
			List<String> azioniCorrelateUniche = null;
			String[] azioniList = null;
			if (azioniCorrelate.size() > 0) {
				azioniCorrelateUniche = new ArrayList<String>();
				azioniCorrelateUniche.add("-");
				for (Iterator<Azione> iterator = azioniCorrelate.iterator(); iterator.hasNext();) {
					Azione azione = iterator.next();
					if (!nomeaz.equals(azione.getNome())) {
						if ( 
								(azione.getCorrelata()==null||"".equals(azione.getCorrelata())) &&
								(!apcCore.isAzioneCorrelata(idAccordo, azione.getNome(), nomeaz))
								) {
							azioniCorrelateUniche.add(azione.getNome());
						}
					}
				}
			}

			if (azioniCorrelateUniche != null)
				azioniList = azioniCorrelateUniche.toArray(new String[azioniCorrelateUniche.size()]);

			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = soggettiCore.getRegistryReader(this.protocolFactory); 
			this.configRegistryReader = soggettiCore.getConfigIntegrationReader(this.protocolFactory);
			IDAccordoAzione idAzioneOld = new IDAccordoAzione();
			idAzioneOld.setIdAccordo(idAs);
			idAzioneOld.setNome(nomeaz); 
			this.consoleConfiguration = this.consoleDynamicConfiguration.getDynamicConfigAzione(this.consoleOperationType, apcHelper, 
					this.registryReader, this.configRegistryReader, idAzioneOld );
			this.protocolProperties = apcHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);


			// cerco l'azione e leggo le protocol properties
			Azione azioneOLD = null;
			for (int i = 0; i < as.sizeAzioneList(); i++) {
				Azione az = as.getAzione(i);
				if (nomeaz.equals(az.getNome())) {
					azioneOLD = az;
					break;
				}
			}

			if(azioneOLD != null)
				oldProtocolPropertyList = azioneOLD.getProtocolPropertyList();

			if(this.protocolPropertiesSet == null){
				ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(this.protocolProperties, oldProtocolPropertyList, this.consoleOperationType);
			}

			Properties propertiesProprietario = new Properties();
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, id);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_AZIONE_ACCORDO);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, nomeaz);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE,
					URLEncoder.encode( AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_AZIONI_CHANGE + "?" + request.getQueryString(), "UTF-8"));
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, protocollo);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO, tipoAccordo);
			
			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if(ServletUtils.isEditModeInProgress(this.editMode)){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
						new Parameter(AccordiServizioParteComuneCostanti.LABEL_AZIONI + " di " + labelASTitle, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_AZIONI_LIST+"?"+
										AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+id+"&"+
										AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME+"="+uriAS+"&"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
						new Parameter(nomeaz, null)
						);


				// Prendo i dati dell'accordo
				String defprofcoll = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(as.getProfiloCollaborazione());
				String deffiltrodupaz = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati());
				String defconfricaz = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione());
				String defidcollaz = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione());
				String defIdRifRichiestaAz = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdRiferimentoRichiesta());
				String defconsordaz = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine());
				String defscadenzaaz = as.getScadenza();

				if(azioneOLD != null) {
					filtrodupaz = filtrodupaz != null && !"".equals(filtrodupaz) ? filtrodupaz : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(azioneOLD.getFiltroDuplicati());
					confricaz = confricaz != null && !"".equals(confricaz) ? confricaz : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(azioneOLD.getConfermaRicezione());
					idcollaz = idcollaz != null && !"".equals(idcollaz) ? idcollaz : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(azioneOLD.getIdCollaborazione());
					idRifRichiestaAz = idRifRichiestaAz != null && !"".equals(idRifRichiestaAz) ? idRifRichiestaAz : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(azioneOLD.getIdRiferimentoRichiesta());
					consordaz = consordaz != null && !"".equals(consordaz) ? consordaz : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(azioneOLD.getConsegnaInOrdine());
					scadenzaaz = scadenzaaz != null && !"".equals(scadenzaaz) ? scadenzaaz : azioneOLD.getScadenza();
					profcoll = profcoll != null && !"".equals(profcoll) ? profcoll : AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(azioneOLD.getProfiloCollaborazione());
					azicorr = azicorr != null && !"".equals(azicorr) ? azicorr : azioneOLD.getCorrelata();
					if (azicorr == null)
						azicorr = "-";
					if (profProtocollo == null) {
						profProtocollo = azioneOLD.getProfAzione();
					}
				}

				if ((profProtocollo == null) || profProtocollo.equals("")) {
					profProtocollo = AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT;
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				// update della configurazione 
				this.consoleDynamicConfiguration.updateDynamicConfigAzione(this.consoleConfiguration, this.consoleOperationType, apcHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idAzioneOld);

				dati = apcHelper.addAccordiAzioniToDati(dati, id, nomeaz, profProtocollo, 
						filtrodupaz, deffiltrodupaz, confricaz, defconfricaz, idcollaz, defidcollaz, idRifRichiestaAz, defIdRifRichiestaAz, consordaz, defconsordaz, scadenzaaz, 
						defscadenzaaz, defprofcoll, profcoll, tipoOp, 
						azicorr, azioniList, as.getStatoPackage(),tipoAccordo,protocollo,apcCore.toMessageServiceBinding(as.getServiceBinding()));
				
				// aggiunta campi custom
				dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);

				pd.setDati(dati);

				if(apcHelper.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(as.getStatoPackage())){
					pd.disableEditMode();
				}

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_AZIONI, ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = apcHelper.accordiAzioniCheckData(tipoOp, id, nomeaz, profProtocollo, filtrodupaz, confricaz, idcollaz, idRifRichiestaAz, consordaz, scadenzaaz);
			
			// updateDynamic
			if(isOk) {
				this.consoleDynamicConfiguration.updateDynamicConfigAzione(this.consoleConfiguration, this.consoleOperationType, apcHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idAzioneOld);
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
						this.consoleDynamicConfiguration.validateDynamicConfigAzione(this.consoleConfiguration, this.consoleOperationType, apcHelper, this.protocolProperties, 
								this.registryReader, this.configRegistryReader, idAzioneOld);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}
			
			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
						new Parameter(AccordiServizioParteComuneCostanti.LABEL_AZIONI + " di " + labelASTitle, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_AZIONI_LIST+"?"+
										AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+id+"&"+
										AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME+"="+uriAS+"&"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
						new Parameter(nomeaz, null)
						);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				// update della configurazione 
				this.consoleDynamicConfiguration.updateDynamicConfigAzione(this.consoleConfiguration, this.consoleOperationType, apcHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idAzioneOld);

				dati = apcHelper.addAccordiAzioniToDati(dati, id, nomeaz, profProtocollo, 
						filtrodupaz, filtrodupaz, confricaz, confricaz, idcollaz, idcollaz,idRifRichiestaAz,idRifRichiestaAz, consordaz, consordaz, scadenzaaz, scadenzaaz, 
						profcoll, profcoll, tipoOp, 
						azicorr, azioniList, as.getStatoPackage(),tipoAccordo,protocollo,apcCore.toMessageServiceBinding(as.getServiceBinding()));
				
				// aggiunta campi custom
				dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_AZIONI, ForwardParams.CHANGE());
			}

			// Modifico i dati dell'azione nel db
			String userLogin = ServletUtils.getUserLoginFromSession(session);

			if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
				filtrodupaz = null;
				confricaz = null;
				idcollaz = null;
				idRifRichiestaAz = null;
				consordaz = null;
				scadenzaaz = null;
			}

			for (int i = 0; i < as.sizeAzioneList(); i++) {
				Azione az = as.getAzione(i);
				if (nomeaz.equals(az.getNome())) {
					as.removeAzione(i);
					break;
				}
			}

			Azione newAz = new Azione();
			newAz.setNome(nomeaz);
			if (!azicorr.equals("-"))
				newAz.setCorrelata(azicorr);
			newAz.setFiltroDuplicati(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(filtrodupaz)));
			newAz.setConfermaRicezione(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(confricaz)));
			newAz.setIdCollaborazione(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(idcollaz)));
			newAz.setIdRiferimentoRichiesta(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(idRifRichiestaAz)));
			newAz.setConsegnaInOrdine(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(consordaz)));
			newAz.setScadenza(scadenzaaz);
			newAz.setProfiloCollaborazione(ProfiloCollaborazione.toEnumConstant(AccordiServizioParteComuneHelper.convertProfiloCollaborazioneView2DB(profcoll)));
			newAz.setProfAzione(profProtocollo);
			as.addAzione(newAz);
			
			//imposto properties custom
			newAz.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(this.protocolProperties, this.consoleOperationType, oldProtocolPropertyList));

			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);

			// devo aggiornare la lista dei servizi(serviziCorrelati) che
			// implementano l'accordo a cui e' stata aggiunta l'azione
			// basta fare un update del servizio per attivare le operazioni
			// necessarie all'aggiornamento
			List<AccordoServizioParteSpecifica> listaServizi = apsCore.serviziWithIdAccordoList(idAccordo);
			for (AccordoServizioParteSpecifica servizio : listaServizi) {
				apcCore.performUpdateOperation(userLogin, apcHelper.smista(), servizio);
			}

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(request, session, Search.class);

			int idLista = Liste.ACCORDI_AZIONI;

			ricerca = apcHelper.checkSearchParameters(idLista, ricerca);
			List<Azione> lista = apcCore.accordiAzioniList(idAccordo, ricerca);
			apcHelper.prepareAccordiAzioniList(as, lista, ricerca,id,tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_AZIONI, ForwardParams.CHANGE());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_AZIONI, ForwardParams.CHANGE());
		}
	}
}
