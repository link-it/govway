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


package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.config.Subscription;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateHelper;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
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
 * AccordiServizioParteSpecificaPorteDelegateAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaFruitoriPorteDelegateAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session);
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			String idAsps = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			String idFruizione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID);
			Long idFru = Long.parseLong(idFruizione);
			
			String idSoggFruitoreDelServizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO);
			Long idSoggFru = Long.parseLong(idSoggFruitoreDelServizio);
			
			//String azione = apsHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
			String [] azioni = apsHelper.getParameterValues(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONI);
			String nome = apsHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME);
		
			String modeCreazione = apsHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_CREAZIONE);
			String identificazione = apsHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_IDENTIFICAZIONE);
			String mappingPD = apsHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MAPPING);

			String fruizioneServizioApplicativo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUIZIONE_NOME_SA);
			String fruizioneRuolo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_RUOLO);
			String fruizioneAutenticazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE);
			String fruizioneAutenticazioneOpzionale = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE_OPZIONALE);
			String fruizioneAutorizzazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE);
			String fruizioneAutorizzazioneAutenticati = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_AUTENTICAZIONE);
			String fruizioneAutorizzazioneRuoli = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLI);
			String fruizioneAutorizzazioneRuoliTipologia = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_TIPOLOGIA);
			String fruizioneAutorizzazioneRuoliMatch = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_MATCH);

			String nomeSA = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SA);

			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);

			String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
			}
			
			// Preparo il menu
			apsHelper.makeMenu();

			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(porteDelegateCore);
			int idServizio = Integer.parseInt(idAsps);
			AccordoServizioParteSpecifica asps  =apsCore.getAccordoServizioParteSpecifica(idServizio);
			IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
			IDSoggetto idSoggettoFruitore = new IDSoggetto();
			String tipoSoggettoFruitore = null;
			String nomeSoggettoFruitore = null;
			if(apsCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggettoFruitore = soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggFruitoreDelServizio));
				tipoSoggettoFruitore = soggettoFruitore.getTipo();
				nomeSoggettoFruitore = soggettoFruitore.getNome();
			}else{
				org.openspcoop2.core.config.Soggetto soggettoFruitore = soggettiCore.getSoggetto(Integer.parseInt(idSoggFruitoreDelServizio));
				tipoSoggettoFruitore = soggettoFruitore.getTipo();
				nomeSoggettoFruitore = soggettoFruitore.getNome();
			}
			idSoggettoFruitore.setTipo(tipoSoggettoFruitore);
			idSoggettoFruitore.setNome(nomeSoggettoFruitore);
			List<MappingFruizionePortaDelegata> listaMappingFruizione = apsCore.serviziFruitoriMappingList(idFru, idSoggettoFruitore, idServizio2, null);
			
			MappingFruizionePortaDelegata mappingSelezionato = null, mappingDefault = null;

			String mappingLabel = "";
			String[] listaMappingLabels = null;
			String[] listaMappingValues = null;
			List<String> azioniOccupate = new ArrayList<>();
			String nomeNuovaConfigurazione = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_AZIONE_SPECIFIC_PREFIX + "1";
			int idxConfigurazione = 0;
			int listaMappingFruizioneSize = listaMappingFruizione != null ? listaMappingFruizione.size() : 0;
			if(listaMappingFruizioneSize > 0) {
				for (int i = 0; i < listaMappingFruizione.size(); i++) {
					MappingFruizionePortaDelegata mappingFruizionePortaDelegata = listaMappingFruizione.get(i);
					if(mappingFruizionePortaDelegata.isDefault()) {
						mappingDefault = mappingFruizionePortaDelegata;
						break;
					}
				}
				
				if(mappingPD != null) {
					for (int i = 0; i < listaMappingFruizione.size(); i++) {
						MappingFruizionePortaDelegata mappingFruizionePortaDelegata = listaMappingFruizione.get(i);
						if(mappingFruizionePortaDelegata.getNome().equals(mappingPD)) {
							mappingSelezionato = mappingFruizionePortaDelegata;
							break;
						}
					}
				}

				if(mappingSelezionato == null) {
					mappingSelezionato = mappingDefault;
				}
				
				if(!mappingSelezionato.isDefault()) {
					PortaDelegata pdMapping = porteDelegateCore.getPortaDelegata(mappingSelezionato.getIdPortaDelegata());
					mappingLabel = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(pdMapping,Integer.MAX_VALUE);
				}

				listaMappingLabels = new String[listaMappingFruizioneSize];
				listaMappingValues = new String[listaMappingFruizioneSize];
				for (int i = 0; i < listaMappingFruizione.size(); i++) {
					MappingFruizionePortaDelegata mappingFruizionePortaDelegata = listaMappingFruizione.get(i);
					//String nomeMappingNoDefault = mappingFruizionePortaDelegata.getNome();
					String nomeMappingNoDefault = null;
					if(!mappingFruizionePortaDelegata.isDefault()) {
						PortaDelegata pdMapping = porteDelegateCore.getPortaDelegata(mappingFruizionePortaDelegata.getIdPortaDelegata());
						nomeMappingNoDefault = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(pdMapping,70);
					}
					listaMappingLabels[i] = mappingFruizionePortaDelegata.isDefault()? 
							PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_NOME_DEFAULT: nomeMappingNoDefault;
					listaMappingValues[i] = mappingFruizionePortaDelegata.getNome();
					
					// calcolo del nome automatico
					if(!mappingFruizionePortaDelegata.isDefault())  {
						int idx = mappingFruizionePortaDelegata.getNome().indexOf(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_AZIONE_SPECIFIC_PREFIX);
						if(idx > -1) {
							String idxTmp = mappingFruizionePortaDelegata.getNome().substring(idx + PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_AZIONE_SPECIFIC_PREFIX.length());
							int idxMax = -1;
							try {
								idxMax = Integer.parseInt(idxTmp);
							}catch(Exception e) {
								idxMax = 0;
							}
							idxConfigurazione = Math.max(idxConfigurazione, idxMax);
						}
					}
					
					// colleziono le azioni gia' configurate
					PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(mappingFruizionePortaDelegata.getIdPortaDelegata());
					if(portaDelegata.getAzione() != null && portaDelegata.getAzione().getAzioneDelegataList() != null)
						azioniOccupate.addAll(portaDelegata.getAzione().getAzioneDelegataList());
				}
			}
			
			nomeNuovaConfigurazione = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_AZIONE_SPECIFIC_PREFIX + (++ idxConfigurazione);

			AccordoServizioParteComune as = null;
			ServiceBinding serviceBinding = null;
			if (asps != null) {
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
				as = apcCore.getAccordoServizio(idAccordo);
				serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
			}

			// Prendo le azioni  disponibili
			boolean addTrattinoSelezioneNonEffettuata = false;
			int sogliaAzioni = addTrattinoSelezioneNonEffettuata ? 1 : 0;
			List<String> azioniS = porteDelegateCore.getAzioni(asps, as, addTrattinoSelezioneNonEffettuata, true, azioniOccupate);
			String[] azioniDisponibiliList = null;
			if(azioniS!=null && azioniS.size()>0) {
				azioniDisponibiliList = new String[azioniS.size()];
				for (int i = 0; i < azioniS.size(); i++) {
					azioniDisponibiliList[i] = azioniS.get(i);
				}
			}
			
			String postBackElementName = apsHelper.getPostBackElementName();

			// Controllo se ho modificato l'azione allora ricalcolo il nome
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_CREAZIONE)){
					// 
				}
			}
			
			// ServiziApplicativi
			List<String> saList = new ArrayList<String>();
			saList.add("-");
			if(idSoggettoFruitore!=null){
				try{
					String auth = fruizioneAutenticazione;
					if(auth==null || "".equals(auth)){
						auth = apsCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
					}
					List<ServizioApplicativo> oldSilList = null;
					if(apsCore.isVisioneOggettiGlobale(userLogin)){
						oldSilList = saCore.soggettiServizioApplicativoList(idSoggettoFruitore,null,
								CredenzialeTipo.toEnumConstant(auth));
					}
					else {
						oldSilList = saCore.soggettiServizioApplicativoList(idSoggettoFruitore,userLogin,
								CredenzialeTipo.toEnumConstant(auth));
					}
					if(oldSilList!=null && oldSilList.size()>0){
						for (int i = 0; i < oldSilList.size(); i++) {
							saList.add(oldSilList.get(i).getNome());		
						}
					}
				}catch(DriverConfigurazioneNotFound dNotFound){}

			}

			List<Parameter> lstParm = porteDelegateHelper.getTitoloPD(parentPD, idSoggFruitoreDelServizio, idAsps, idFruizione);

			lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (apsHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd,lstParm); 

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				if(azioniDisponibiliList==null || azioniDisponibiliList.length <= sogliaAzioni) {
					pd.setMessage(AccordiServizioParteSpecificaCostanti.LABEL_AGGIUNTA_AZIONI_COMPLETATA, Costanti.MESSAGE_TYPE_INFO);
					pd.disableEditMode();
				}
				else {
				
					if(azioni == null) {
						azioni = new String[0];
					}
	
					if(nome == null) {
						// nome mapping calcolato in base al numero id configurazioni non di default presenti
						nome = nomeNuovaConfigurazione;
	
						if(identificazione == null)
							identificazione = PortaApplicativaAzioneIdentificazione.DELEGATED_BY.toString();
					}
					
					if(modeCreazione == null)
						modeCreazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_EREDITA;
	
					if(fruizioneServizioApplicativo==null || "".equals(fruizioneServizioApplicativo))
						fruizioneServizioApplicativo = "-";
					if(fruizioneRuolo==null || "".equals(fruizioneRuolo))
						fruizioneRuolo = "-";
					if(fruizioneAutenticazione==null || "".equals(fruizioneAutenticazione))
						fruizioneAutenticazione = apsCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
					if(fruizioneAutorizzazione==null || "".equals(fruizioneAutorizzazione)){
						String tipoAutorizzazione = apsCore.getAutorizzazione_generazioneAutomaticaPorteDelegate();
						fruizioneAutorizzazione = AutorizzazioneUtilities.convertToStato(tipoAutorizzazione);
						if(TipoAutorizzazione.isAuthenticationRequired(tipoAutorizzazione))
							fruizioneAutorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
						if(TipoAutorizzazione.isRolesRequired(tipoAutorizzazione))
							fruizioneAutorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
						fruizioneAutorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(tipoAutorizzazione).getValue();
					}
					
	
					dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idAsps, idSoggFruitoreDelServizio, null, null, idFruizione, dati);
					dati = apsHelper.addConfigurazioneFruizioneToDati(TipoOperazione.ADD, dati, nome, azioni, azioniDisponibiliList, idAsps, idSoggettoFruitore,
							identificazione, asps, as, serviceBinding, modeCreazione, listaMappingLabels, listaMappingValues,
							mappingPD, mappingLabel, saList, nomeSA, fruizioneAutenticazione, fruizioneAutenticazioneOpzionale, 
							true, fruizioneAutorizzazione, fruizioneAutorizzazioneAutenticati, 
							fruizioneAutorizzazioneRuoli, fruizioneRuolo, fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch, fruizioneServizioApplicativo);
				}
					
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE,
						ForwardParams.ADD());

			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.configurazioneFruizioneCheckData(TipoOperazione.ADD, nome, azioni, asps, azioniOccupate,modeCreazione,null,true);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd,lstParm); 

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idAsps, idSoggFruitoreDelServizio, null, null, idFruizione, dati);

				dati = apsHelper.addConfigurazioneFruizioneToDati(TipoOperazione.ADD, dati, nome, azioni, azioniDisponibiliList, idAsps, idSoggettoFruitore,
						identificazione, asps, as, serviceBinding, modeCreazione, listaMappingLabels, listaMappingValues,
						mappingPD, mappingLabel, saList, nomeSA, fruizioneAutenticazione, fruizioneAutenticazioneOpzionale, 
						true, fruizioneAutorizzazione, fruizioneAutorizzazioneAutenticati, 
						fruizioneAutorizzazioneRuoli, fruizioneRuolo, fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch, fruizioneServizioApplicativo);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE, 
						ForwardParams.ADD());
			}

			List<Object> listaOggettiDaCreare = new ArrayList<Object>();
			List<Object> listaOggettiDaModificare = new ArrayList<Object>();

			PortaDelegata portaDelegataDefault = porteDelegateCore.getPortaDelegata(mappingDefault.getIdPortaDelegata());
			String protocollo = apsCore.getProtocolloAssociatoTipoServizio(idServizio2.getTipo());
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
		
			Fruitore fruitore = null;
			if(gestioneFruitori) {
				for (Fruitore fruitoreCheck : asps.getFruitoreList()) {
					if(fruitoreCheck.getTipo().equals(tipoSoggettoFruitore) && fruitoreCheck.getNome().equals(nomeSoggettoFruitore)) {
						fruitore = fruitoreCheck;
						break;
					}
				}
			}
			
			Connettore connettore = null;
			Subscription subscription = null;
			if(modeCreazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_EREDITA)) {
				PortaDelegata portaDelegataDaCopiare = porteDelegateCore.getPortaDelegata(mappingSelezionato.getIdPortaDelegata());
				subscription = protocolFactory.createProtocolIntegrationConfiguration().createSubscription(serviceBinding, idSoggettoFruitore, idServizio2, 
						portaDelegataDefault, portaDelegataDaCopiare, nome, azioni);
				
				if(gestioneFruitori) {
					if(mappingSelezionato.isDefault()) {
						connettore = (Connettore) fruitore.getConnettore().clone();
					}
					else {
						for (int j = 0; j < fruitore.sizeConfigurazioneAzioneList(); j++) {
							ConfigurazioneServizioAzione config = fruitore.getConfigurazioneAzione(j);
							if(config!=null) {
								String azione = portaDelegataDaCopiare.getAzione().getAzioneDelegata(0); // prendo la prima
								if(config.getAzioneList().contains(azione)) {
									connettore = config.getConnettore();
									break;
								}
							}
						}
					}
				}
			}
			else {
				subscription = protocolFactory.createProtocolIntegrationConfiguration().createSubscription(serviceBinding, idSoggettoFruitore, idServizio2, 
						portaDelegataDefault, nome, azioni);
				
				if(gestioneFruitori) {
					connettore = null; // TODO
				}
			}
			
			if(gestioneFruitori) {
				ConfigurazioneServizioAzione configurazioneAzione = new ConfigurazioneServizioAzione();
				configurazioneAzione.setConnettore(connettore);
				for (int i = 0; i < azioni.length; i++) {
					configurazioneAzione.addAzione(azioni[i]);
				}
				fruitore.addConfigurazioneAzione(configurazioneAzione);
			}
			
			PortaDelegata portaDelegata = subscription.getPortaDelegata();
			MappingFruizionePortaDelegata mappingFruizione = subscription.getMapping();
			portaDelegata.setIdSoggetto((long) idSoggFru);
			
			if(!modeCreazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_EREDITA)) {
				porteDelegateCore.configureControlloAccessiPortaDelegata(portaDelegata, 
						fruizioneAutenticazione, fruizioneAutenticazioneOpzionale,
						fruizioneAutorizzazione, fruizioneAutorizzazioneAutenticati, fruizioneAutorizzazioneRuoli, fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch,
						fruizioneServizioApplicativo, fruizioneRuolo);
			}
			
			listaOggettiDaCreare.add(portaDelegata);
			listaOggettiDaCreare.add(mappingFruizione);

			porteDelegateCore.performCreateOperation(userLogin, porteDelegateHelper.smista(), listaOggettiDaCreare.toArray());

			if(gestioneFruitori) {
				
				listaOggettiDaModificare.add(asps);
				
			}
			
			if(listaOggettiDaModificare.size()>0) {
				porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), listaOggettiDaModificare.toArray());
			}
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_FRUIZIONE;

			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);

			List<MappingFruizionePortaDelegata> lista = apsCore.serviziFruitoriMappingList(idFru, idSoggettoFruitore , idServizio2, ricerca);
			
			apsHelper.serviziFruitoriMappingList(lista, idAsps, idSoggFruitoreDelServizio, idFruizione, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE, 
					ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE,
					ForwardParams.ADD());
		}  
	}
}
