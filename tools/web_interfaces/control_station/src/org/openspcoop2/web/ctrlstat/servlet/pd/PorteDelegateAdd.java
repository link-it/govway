/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.AutorizzazioneRuoli;
import org.openspcoop2.core.config.AutorizzazioneScope;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataLocalForward;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.ScopeTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * porteDelegateAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String nomePD = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA);
			//			String idPorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			//			String nomePorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA);
			String idsogg = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String descr = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_DESCRIZIONE);
			String statoPorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_STATO_PORTA);
			String autenticazione = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE);
			String autenticazioneOpzionale = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE);
			String autenticazioneCustom = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM);
			String autorizzazione = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE);
			String autorizzazioneCustom = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM);
			String soggid = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SOGGETTO_ID);
			String tiposp = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SP);
			String sp = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SP);
			String servid = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_ID);
			String tiposervizio = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SERVIZIO);
			String servizio = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO);
			String versioneServizio = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_VERSIONE_SERVIZIO);
			String azid = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE_ID);
			String modeaz = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_AZIONE);
			String azione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
			String stateless = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_STATELESS);
			String localForward = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD);
			String paLocalForward = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD_PA);
			String gestBody = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_BODY);
			String gestManifest = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_MANIFEST);
			String ricsim = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA);
			String ricasim = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			String statoValidazione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_XSD);
			String tipoValidazione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE);
			String autorizzazioneContenuti = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI);
			String forceWsdlBased = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_FORCE_INTERFACE_BASED);
			String applicaMTOM = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_APPLICA_MTOM);

			String autorizzazioneAutenticati = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE);
			String autorizzazioneRuoli = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI);
			String autorizzazioneRuoliTipologia = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA);
			String ruoloMatch = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_MATCH);
			
			String gestioneToken = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN);
			String gestioneTokenPolicy = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY);
			String gestioneTokenOpzionale = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_OPZIONALE);
			String gestioneTokenValidazioneInput = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT);
			String gestioneTokenIntrospection = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION);
			String gestioneTokenUserInfo = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO);
			String gestioneTokenTokenForward = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
			
			String autenticazioneTokenIssuer = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_ISSUER);
			String autenticazioneTokenClientId = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_CLIENT_ID);
			String autenticazioneTokenSubject = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_SUBJECT);
			String autenticazioneTokenUsername = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_USERNAME);
			String autenticazioneTokenEMail = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_MAIL);
			
			String autorizzazione_token = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
			String autorizzazione_tokenOptions = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
			String autorizzazioneScope = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
			String autorizzazioneScopeMatch = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
			
			BinaryParameter allegatoXacmlPolicy = porteDelegateHelper.getBinaryParameter(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
			
			if(sp == null) {
				tiposp = "";
				sp = "";
			}
			
			if(servizio == null) {
				tiposervizio = "";
				servizio = "";
			}
			
			if(modeaz == null) {
				modeaz = PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT;
			}
			 
			if ((modeaz != null) && !modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && (azione == null)) {
				azione = "";
			}
			
			String serviceBindingS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVICE_BINDING);
			ServiceBinding serviceBinding = null;
			if(StringUtils.isNotEmpty(serviceBindingS))
				serviceBinding = ServiceBinding.valueOf(serviceBindingS);

			// Informazioni sul numero di ServiziApplicativi, Correlazione Applicativa e stato Message-Security
			int numSA =0;
			int numRuoli =0;
			int numScope = 0;
			String statoMessageSecurity  = "";
			String statoMTOM  = "";
			int numCorrelazioneReq =0; 
			int numCorrelazioneRes =0;

			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo nome e tipo del soggetto
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore( );
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(porteDelegateCore);

			String tmpTitle = null, protocollo = null;
			if(porteDelegateCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
				tmpTitle = porteDelegateHelper.getLabelNomeSoggetto(protocollo, soggetto.getTipo() , soggetto.getNome());
			}else{
				org.openspcoop2.core.config.Soggetto tmpSogg = soggettiCore.getSoggetto(soggInt);
				protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tmpSogg.getTipo());
				tmpTitle = porteDelegateHelper.getLabelNomeSoggetto(protocollo, tmpSogg.getTipo() , tmpSogg.getNome());
			}

			String postBackElementName = porteDelegateHelper.getPostBackElementName();
			
			// se ho modificato il soggetto ricalcolo il servizio e il service binding
			if (postBackElementName != null) {
				if(postBackElementName.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SOGGETTO_ID)) {
					servid = null;
					serviceBinding = null;
				} else if(postBackElementName.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_ID)) {
					serviceBinding = null;
				} 
			}
			
			List<String> tipiServizioCompatibiliAccordo = new ArrayList<String>();
			if(serviceBinding == null) {
				List<ServiceBinding> serviceBindingListProtocollo = apsCore.getServiceBindingListProtocollo(protocollo);
				
				if(serviceBindingListProtocollo != null && serviceBindingListProtocollo.size() > 0) {
					for (ServiceBinding serviceBinding2 : serviceBindingListProtocollo) {
						List<String> tipiServizioCompatibiliAccordoTmp = apsCore.getTipiServiziGestitiProtocollo(protocollo,serviceBinding2);
						
						for (String tipoTmp : tipiServizioCompatibiliAccordoTmp) {
							if(!tipiServizioCompatibiliAccordo.contains(tipoTmp))
								tipiServizioCompatibiliAccordo.add(tipoTmp);
						}
					}
				}
			} else {
				tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(protocollo,serviceBinding);
			}

			// Se modesp = register-input, prendo la lista di tutti i soggetti
			// e la metto in un array
			String[] soggettiList = null;
			String[] soggettiListLabel = null;

			List<String> tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);

			List<IDSoggetto> list = soggettiCore.getAllIdSoggettiRegistro(new FiltroRicercaSoggetti());
			if (list!=null && list.size() > 0) {

				List<String> soggettiListTmp = new ArrayList<String>();
				Map<String, String> soggettiMapTmp = new HashMap<String,String>();
				
				for (IDSoggetto soggetto : list) {
					if(tipiSoggettiCompatibiliAccordo.contains(soggetto.getTipo())){
						String keyIdSog = soggetto.getTipo() + "/" + soggetto.getNome();
						soggettiListTmp.add(keyIdSog);
						soggettiMapTmp.put(keyIdSog, porteDelegateHelper.getLabelNomeSoggetto(protocollo, soggetto.getTipo(), soggetto.getNome()));
					}
				}
				Collections.sort(soggettiListTmp);
				soggettiList = soggettiListTmp.toArray(new String[1]);
				soggettiListLabel = new String[soggettiList.length];
				
				for (int i = 0; i < soggettiList.length; i++) {
					String keyIdSog = soggettiList[i];
					soggettiListLabel[i] = soggettiMapTmp.get(keyIdSog);
				}
			}

			// Se modeservizio = register-input, prendo la lista di tutti i servizi
			// e la metto in un array
			String[] serviziList = null;
			String[] serviziListLabel = null;
			if ( (soggid != null && !"".equals(soggid) && soggid.contains("/")) ) {
				IDSoggetto idSoggetto = new IDSoggetto(soggid.split("/")[0], soggid.split("/")[1]);
				FiltroRicercaServizi filtro = new FiltroRicercaServizi();
				filtro.setTipoSoggettoErogatore(idSoggetto.getTipo());
				filtro.setNomeSoggettoErogatore(idSoggetto.getNome());
				List<IDServizio> listServTmp = null;
				try{
					listServTmp = apsCore.getAllIdServizi(filtro);
				}catch(DriverRegistroServiziNotFound dNotFound){}
				if(listServTmp!=null && listServTmp.size()>0){
					List<String> serviziListTmp = new ArrayList<String>();
					Map<String, IDServizio> serviziMapTmp = new HashMap<String,IDServizio>();
					for (IDServizio idServizio : listServTmp) {
						if(tipiServizioCompatibiliAccordo.contains(idServizio.getTipo())){
							String keyServizio = idServizio.getTipo() + "/" + idServizio.getNome() + "/" + idServizio.getVersione();
							serviziListTmp.add(keyServizio);
							serviziMapTmp.put(keyServizio, idServizio); 
						}
					}

					Collections.sort(serviziListTmp);
					serviziList = serviziListTmp.toArray(new String[1]);
					serviziListLabel = new String[serviziList.length];
					for (int i = 0; i < serviziList.length; i++) {
						String idServTmp = serviziList[i];
						serviziListLabel[i] = porteDelegateHelper.getLabelIdServizio(protocollo, serviziMapTmp.get(idServTmp));
					}
				}
			}
			
			
			IDSoggetto idSoggetto = null;
			IDServizio idServizio = null;
			AccordoServizioParteSpecifica servS = null;
			if (	(servid != null && !"".equals(servid) && servid.contains("/"))
					&& 
					(soggid != null && !"".equals(soggid) && soggid.contains("/"))
					) {
				idSoggetto = new IDSoggetto(soggid.split("/")[0], soggid.split("/")[1]);
				idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(servid.split("/")[0], servid.split("/")[1], idSoggetto, 
						Integer.parseInt(servid.split("/")[2])); 
				try{
					servS = apsCore.getServizio(idServizio);
				}catch(DriverRegistroServiziNotFound dNotFound){
				}
				if(servS==null){
					// è cambiato il soggetto erogatore. non è più valido il servizio
					servid = null;
					idServizio = null;
					if(serviziList!=null && serviziList.length>0){
						servid = serviziList[0];
						idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(servid.split("/")[0], servid.split("/")[1], idSoggetto, 
								Integer.parseInt(servid.split("/")[2])); 
						try{
							servS = apsCore.getServizio(idServizio);
						}catch(DriverRegistroServiziNotFound dNotFound){
						}
						if(servS==null){
							servid = null;
							idServizio = null;
						}
					}
				}
			}
			
			AccordoServizioParteComune as = null;
			if ( servS!=null ) {
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(servS.getAccordoServizioParteComune());
				as = apcCore.getAccordoServizio(idAccordo);
				
				if(serviceBinding == null) {
					serviceBinding = porteDelegateCore.toMessageServiceBinding(as.getServiceBinding());
				}
				
			}
			
			// Se modeaz = register-input, prendo la lista delle azioni
			// associate a servid e la metto in un array
			String[] azioniList = null;
			String[] azioniListLabel = null;
			boolean addTrattinoSelezioneNonEffettuata = false;
			List<String> filtraAzioniUtilizzate = new ArrayList<String>(); // TODO controllare
			if ((modeaz != null) && modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
			
				Map<String,String> azioni = porteDelegateCore.getAzioniConLabel(servS, as, addTrattinoSelezioneNonEffettuata , true, filtraAzioniUtilizzate);
				if(azioni != null && azioni.size() > 0) {
					azioniList = new String[azioni.size()];
					azioniListLabel = new String[azioni.size()];
					int i = 0;
					for (String string : azioni.keySet()) {
						azioniList[i] = string;
						azioniListLabel[i] = azioni.get(string);
						i++;
					}
				}
			}
			int numAzioni = 0;
			if (azioniList != null)
				numAzioni = azioniList.length;
			
			List<GenericProperties> gestorePolicyTokenList = confCore.gestorePolicyTokenList(null, ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN, null);
			String [] policyLabels = new String[gestorePolicyTokenList.size() + 1];
			String [] policyValues = new String[gestorePolicyTokenList.size() + 1];
			
			policyLabels[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
			policyValues[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
			
			for (int i = 0; i < gestorePolicyTokenList.size(); i++) {
			GenericProperties genericProperties = gestorePolicyTokenList.get(i);
				policyLabels[(i+1)] = genericProperties.getNome();
				policyValues[(i+1)] = genericProperties.getNome();
			}

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteDelegateHelper.isEditModeInProgress()) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle, 
								PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
								),
								ServletUtils.getParameterAggiungi()
						);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				if (nomePD == null) {
					nomePD = "";
				}
				if (descr == null) {
					descr = "";
				}
				if (autenticazione == null) {
					autenticazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE;
				}
				if (autorizzazione == null) {
					String defaultAutorizzazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE;
					if (defaultAutorizzazione != null &&
							!TipoAutorizzazione.getAllValues().contains(defaultAutorizzazione)) {
						autorizzazioneCustom = defaultAutorizzazione;
						autorizzazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
					}
					else{
						autorizzazione = AutorizzazioneUtilities.convertToStato(defaultAutorizzazione);
						if(TipoAutorizzazione.isAuthenticationRequired(defaultAutorizzazione))
							autorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
						if(TipoAutorizzazione.isRolesRequired(defaultAutorizzazione))
							autorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
						autorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(defaultAutorizzazione).getValue();
					}		
				}
				if (stateless == null) {
					stateless = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_STATELESS_DEFAULT;
				}
				if (localForward == null) {
					localForward = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD_DISABILITATO;
				}
				if (gestBody == null) {
					gestBody =  PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_NONE;
				}
				if (gestManifest == null) {
					gestManifest = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_MANIFEST_DEFAULT;
				}
				if (ricsim == null) {
					ricsim =  PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA_ABILITATO;
				}
				if (ricasim == null) {
					ricasim = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA_ABILITATO;
				}

				if (statoValidazione == null) {
					if(porteDelegateCore.isSinglePdD()){
						Configurazione config = porteDelegateCore.getConfigurazioneGenerale();
						if(config.getValidazioneContenutiApplicativi()!=null){
							if(config.getValidazioneContenutiApplicativi().getStato()!=null){
								statoValidazione = config.getValidazioneContenutiApplicativi().getStato().toString();
							}
							if(config.getValidazioneContenutiApplicativi().getTipo()!=null){
								tipoValidazione = config.getValidazioneContenutiApplicativi().getTipo().toString();
							}
							if(StatoFunzionalita.ABILITATO.equals(config.getValidazioneContenutiApplicativi().getAcceptMtomMessage())){
								applicaMTOM = Costanti.CHECK_BOX_ENABLED_ABILITATO;
							}
						}
					}
				}

				if (statoValidazione == null) {
					statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
				}
				if (tipoValidazione == null) {
					tipoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_INTERFACE;
				}

				if (applicaMTOM == null) {
					applicaMTOM = "";
				}
				
				if(gestioneToken == null) {
					gestioneToken = StatoFunzionalita.DISABILITATO.getValue();
					gestioneTokenPolicy = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
					gestioneTokenOpzionale = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_OPZIONALE;
					gestioneTokenValidazioneInput = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_VALIDAZIONE_INPUT;
					gestioneTokenIntrospection = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_INTROSPECTION;
					gestioneTokenUserInfo = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_USER_INFO;
					gestioneTokenTokenForward = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TOKEN_FORWARD;
					autenticazioneTokenIssuer = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_ISSUER;
					autenticazioneTokenClientId = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_CLIENT_ID;
					autenticazioneTokenSubject = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_SUBJECT;
					autenticazioneTokenUsername = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_USERNAME;
					autenticazioneTokenEMail = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_EMAIL;
				}
				
				if(autorizzazioneScope == null) {
					autorizzazioneScope = "";
				}

				// i pattern sono i nomi
				dati = porteDelegateHelper.addPorteDelegateToDati(TipoOperazione.ADD, 
						PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID,
						nomePD,
						dati, PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID,
						descr, autenticazione, autorizzazione,
						soggid, soggettiList, soggettiListLabel,
						sp, tiposp, sp, servid, serviziList,
						serviziListLabel, servizio, tiposervizio, versioneServizio, servizio,
						modeaz, azid, azioniListLabel, azioniList, azione,
						azione, numAzioni,  stateless, localForward, paLocalForward, ricsim, ricasim,
						statoValidazione, tipoValidazione, 0, "", gestBody, gestManifest,
						null, autenticazioneOpzionale, autenticazioneCustom, 
						autorizzazioneCustom,autorizzazioneAutenticati,autorizzazioneRuoli,autorizzazioneRuoliTipologia,autorizzazioneContenuti,idsogg,protocollo,
						numSA,numRuoli, ruoloMatch,
						statoMessageSecurity,statoMTOM,numCorrelazioneReq,numCorrelazioneRes,
						forceWsdlBased,applicaMTOM,false,
						servS,as,serviceBinding,
						statoPorta,false,false,
						false,null,
						gestioneToken,policyLabels, policyValues,
						gestioneTokenPolicy,gestioneTokenOpzionale,
						gestioneTokenValidazioneInput,gestioneTokenIntrospection,gestioneTokenUserInfo,gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_token, autorizzazione_tokenOptions,
						autorizzazioneScope,numScope, autorizzazioneScopeMatch,allegatoXacmlPolicy);

				pd.setDati(dati);


				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE, ForwardParams.ADD());
				//				(mapping.findForward("AddPortaDelegataForm"));
			}
			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.porteDelegateCheckData(TipoOperazione.ADD, "");
			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle,
								PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
								),
								ServletUtils.getParameterAggiungi()
						);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addPorteDelegateToDati(TipoOperazione.ADD,
						PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID,
						nomePD,
						dati, PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID,
						descr, autenticazione, autorizzazione,
						soggid, soggettiList, soggettiListLabel,
						sp, tiposp, sp, servid, serviziList,
						serviziListLabel, servizio, tiposervizio,versioneServizio, servizio,
						modeaz, azid, azioniListLabel, azioniList, azione,
						azione, numAzioni, stateless, localForward, paLocalForward, ricsim, ricasim,
						statoValidazione, tipoValidazione, 0, "", gestBody, gestManifest,
						null, autenticazioneOpzionale, autenticazioneCustom, 
						autorizzazioneCustom,autorizzazioneAutenticati,autorizzazioneRuoli,autorizzazioneRuoliTipologia,autorizzazioneContenuti ,idsogg,protocollo,
						numSA,numRuoli, ruoloMatch,
						statoMessageSecurity,statoMTOM,numCorrelazioneReq,numCorrelazioneRes,
						forceWsdlBased,applicaMTOM,false,
						servS,as,serviceBinding,
						statoPorta,false,false,
						false,null,
						gestioneToken,policyLabels, policyValues,
						gestioneTokenPolicy,gestioneTokenOpzionale,
						gestioneTokenValidazioneInput,gestioneTokenIntrospection,gestioneTokenUserInfo,gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_token, autorizzazione_tokenOptions,
						autorizzazioneScope,numScope, autorizzazioneScopeMatch,allegatoXacmlPolicy);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE, ForwardParams.ADD());
			}

			// Inserisco la porta delegata nel db
			sp = soggid.split("/")[1];
			tiposp = soggid.split("/")[0];

			versioneServizio = servid.split("/")[2];
			servizio = servid.split("/")[1];
			tiposervizio = servid.split("/")[0];
			
			if (modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				azione = "";
			} else {
				azid = "";
			}

			PortaDelegata portaDelegata = new PortaDelegata();
			portaDelegata.setNome(nomePD);
			portaDelegata.setDescrizione(descr);
			if(statoPorta==null || "".equals(statoPorta) || CostantiConfigurazione.ABILITATO.toString().equals(statoPorta)){
				portaDelegata.setStato(StatoFunzionalita.ABILITATO);
			}
			else{
				portaDelegata.setStato(StatoFunzionalita.DISABILITATO);
			}
			if (autenticazione == null ||
					!autenticazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM))
				portaDelegata.setAutenticazione(autenticazione);
			else
				portaDelegata.setAutenticazione(autenticazioneCustom);
			if(autenticazioneOpzionale != null){
				if(ServletUtils.isCheckBoxEnabled(autenticazioneOpzionale))
					portaDelegata.setAutenticazioneOpzionale(StatoFunzionalita.ABILITATO);
				else 
					portaDelegata.setAutenticazioneOpzionale(StatoFunzionalita.DISABILITATO);
			} else 
				portaDelegata.setAutenticazioneOpzionale(null);
			if (autorizzazione == null || 
					!autorizzazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM))
				portaDelegata.setAutorizzazione(AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(autorizzazione, 
						ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati), 
						ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli), 
						ServletUtils.isCheckBoxEnabled(autorizzazioneScope),
						autorizzazione_tokenOptions,
						RuoloTipologia.toEnumConstant(autorizzazioneRuoliTipologia)));
			else
				portaDelegata.setAutorizzazione(autorizzazioneCustom);
			
			if(ruoloMatch!=null && !"".equals(ruoloMatch)){
				RuoloTipoMatch tipoRuoloMatch = RuoloTipoMatch.toEnumConstant(ruoloMatch);
				if(tipoRuoloMatch!=null){
					if(portaDelegata.getRuoli()==null){
						portaDelegata.setRuoli(new AutorizzazioneRuoli());
					}
					portaDelegata.getRuoli().setMatch(tipoRuoloMatch);
				}
			}
			if(ServletUtils.isCheckBoxEnabled(autorizzazioneScope )) {
				if(portaDelegata.getScope() == null)
					portaDelegata.setScope(new AutorizzazioneScope());
				
				portaDelegata.getScope().setStato(StatoFunzionalita.ABILITATO); 
			}
			else {
				portaDelegata.setScope(null);
			}
			if(autorizzazioneScopeMatch!=null && !"".equals(autorizzazioneScopeMatch)){
				ScopeTipoMatch scopeTipoMatch = ScopeTipoMatch.toEnumConstant(autorizzazioneScopeMatch);
				if(scopeTipoMatch!=null){
					if(portaDelegata.getScope()==null){
						portaDelegata.setScope(new AutorizzazioneScope());
					}
					portaDelegata.getScope().setMatch(scopeTipoMatch);
				}
			}
			
			if(portaDelegata.getGestioneToken() == null)
				portaDelegata.setGestioneToken(new GestioneToken());
			
			if(gestioneToken.equals(StatoFunzionalita.ABILITATO.getValue())) {
				portaDelegata.getGestioneToken().setPolicy(gestioneTokenPolicy);
				portaDelegata.getGestioneToken().setTokenOpzionale(StatoFunzionalita.toEnumConstant(gestioneTokenOpzionale)); 
				portaDelegata.getGestioneToken().setValidazione(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenValidazioneInput));
				portaDelegata.getGestioneToken().setIntrospection(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenIntrospection));
				portaDelegata.getGestioneToken().setUserInfo(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenUserInfo));
				portaDelegata.getGestioneToken().setForward(StatoFunzionalita.toEnumConstant(gestioneTokenTokenForward)); 
				portaDelegata.getGestioneToken().setOptions(autorizzazione_tokenOptions);
				if(portaDelegata.getGestioneToken().getAutenticazione()==null) {
					portaDelegata.getGestioneToken().setAutenticazione(new GestioneTokenAutenticazione());
				}
				portaDelegata.getGestioneToken().getAutenticazione().setIssuer(ServletUtils.isCheckBoxEnabled(autenticazioneTokenIssuer) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenIssuer)); 
				portaDelegata.getGestioneToken().getAutenticazione().setClientId(ServletUtils.isCheckBoxEnabled(autenticazioneTokenClientId) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenClientId)); 
				portaDelegata.getGestioneToken().getAutenticazione().setSubject(ServletUtils.isCheckBoxEnabled(autenticazioneTokenSubject) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenSubject)); 
				portaDelegata.getGestioneToken().getAutenticazione().setUsername(ServletUtils.isCheckBoxEnabled(autenticazioneTokenUsername) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenUsername)); 
				portaDelegata.getGestioneToken().getAutenticazione().setEmail(ServletUtils.isCheckBoxEnabled(autenticazioneTokenEMail) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenEMail)); 
			} else {
				portaDelegata.getGestioneToken().setPolicy(null);
				portaDelegata.getGestioneToken().setTokenOpzionale(StatoFunzionalita.DISABILITATO); 
				portaDelegata.getGestioneToken().setValidazione(StatoFunzionalitaConWarning.DISABILITATO);
				portaDelegata.getGestioneToken().setIntrospection(StatoFunzionalitaConWarning.DISABILITATO);
				portaDelegata.getGestioneToken().setUserInfo(StatoFunzionalitaConWarning.DISABILITATO);
				portaDelegata.getGestioneToken().setForward(StatoFunzionalita.DISABILITATO); 
				portaDelegata.getGestioneToken().setOptions(null);
				if(portaDelegata.getGestioneToken().getAutenticazione()!=null) {
					portaDelegata.getGestioneToken().setAutenticazione(null);
				}
			}
			
			if (stateless !=null && !stateless.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_STATELESS_DEFAULT))
				portaDelegata.setStateless(StatoFunzionalita.toEnumConstant(stateless));
			if (PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_ALLEGA.equals(gestBody))
				portaDelegata.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_ABILITATO));
			else
				portaDelegata.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO));
			if (PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_SCARTA.equals(gestBody))
				portaDelegata.setScartaBody(StatoFunzionalita.toEnumConstant(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_ABILITATO));
			else
				portaDelegata.setScartaBody(StatoFunzionalita.toEnumConstant(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO));
			if (gestManifest !=null && !gestManifest.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_STATELESS_DEFAULT))
				portaDelegata.setGestioneManifest(StatoFunzionalita.toEnumConstant(gestManifest));
			portaDelegata.setRicevutaAsincronaSimmetrica(StatoFunzionalita.toEnumConstant(ricsim));
			portaDelegata.setRicevutaAsincronaAsimmetrica(StatoFunzionalita.toEnumConstant(ricasim));
			if(localForward!=null){
				portaDelegata.setLocalForward(new PortaDelegataLocalForward());
				portaDelegata.getLocalForward().setStato(StatoFunzionalita.toEnumConstant(localForward));
				portaDelegata.getLocalForward().setPortaApplicativa(paLocalForward);
			}

			PortaDelegataSoggettoErogatore pdSogg = new PortaDelegataSoggettoErogatore();
			IDSoggetto idSoggettoErogatore = new IDSoggetto(tiposp, sp);
			
			pdSogg.setId(soggettiCore.getSoggettoRegistro(idSoggettoErogatore).getId());
			if(pdSogg.getId()<=0){
				pdSogg.setId(-2l);
			}
			
			pdSogg.setNome(sp);
			pdSogg.setTipo(tiposp);

			portaDelegata.setSoggettoErogatore(pdSogg);

			PortaDelegataServizio pdServizio = new PortaDelegataServizio();
			AccordoServizioParteSpecifica asps = null;
			idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tiposervizio, servizio, idSoggettoErogatore, Integer.parseInt(versioneServizio)); 
			try{
				asps = apsCore.getServizio(idServizio);
				pdServizio.setId(asps.getId());
			}catch(DriverRegistroServiziNotFound dNotFound){
			}
			if(pdServizio.getId()<=0){
				pdServizio.setId(-2l);
			}
			pdServizio.setNome(servizio);
			pdServizio.setTipo(tiposervizio);

			portaDelegata.setServizio(pdServizio);

			// se l azione e' settata allora creo il bean
			if (((!azione.equals("") || 
							modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) ||
							modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SOAP_ACTION_BASED) ||
							modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_INTERFACE_BASED))) ||
							!azid.equals("")) {
				PortaDelegataAzione pdAzione = new PortaDelegataAzione();

				if (modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
					azione = azid;
//					IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
//					as = apcCore.getAccordoServizio(idAccordo);
//
//					if(asps.getPortType()!=null){
//						// Bisogna prendere le operations del port type
//						PortType pt = null;
//						for (int i = 0; i < as.sizePortTypeList(); i++) {
//							if(as.getPortType(i).getNome().equals(asps.getPortType())){
//								pt = as.getPortType(i);
//								break;
//							}
//						}
//						if(pt==null){
//							throw new Exception("Accordo di servizio parte specifica ["+idServizio.toString()+"] possiede un port type ["+asps.getPortType()+"] che non risulta essere registrato nell'accordo di servizio parte comune ["+asps.getAccordoServizioParteComune()+"]");
//						}
//						if(pt.sizeAzioneList()>0){
//							for (int i = 0; i < pt.sizeAzioneList(); i++) {
//								if(pt.getAzione(i).getNome().equals(azione)){
//									pdAzione.setId(pt.getAzione(i).getId());
//									break;
//								}
//							}
//						}
//					}else{
//						if(as.sizeAzioneList()>0){
//							for (int i = 0; i < as.sizeAzioneList(); i++) {
//								if(as.getAzione(i).getNome().equals(azione)){
//									pdAzione.setId(as.getAzione(i).getId());
//									break;
//								}
//							}
//						}
//					}

					if(pdAzione.getId()<=0){
						pdAzione.setId(-2l);
					}
				}
				pdAzione.setNome(azione);
				pdAzione.setIdentificazione(PortaDelegataAzioneIdentificazione.toEnumConstant(modeaz));
				pdAzione.setPattern(azione);

				//FORCE WSDL BASED
				if(!modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
						!modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_INTERFACE_BASED)){

					if(forceWsdlBased != null && (ServletUtils.isCheckBoxEnabled(forceWsdlBased))){
						pdAzione.setForceInterfaceBased(StatoFunzionalita.ABILITATO);
					}else {
						pdAzione.setForceInterfaceBased(StatoFunzionalita.DISABILITATO);
					}
				}else {
					pdAzione.setForceInterfaceBased(null);
				}

				portaDelegata.setAzione(pdAzione);
			}

			// soggetto proprietario
			SoggettoCtrlStat soggettoCS = soggettiCore.getSoggettoCtrlStat(soggInt);
			portaDelegata.setIdSoggetto(soggettoCS.getId());
			portaDelegata.setTipoSoggettoProprietario(soggettoCS.getTipo());
			portaDelegata.setNomeSoggettoProprietario(soggettoCS.getNome());

			ValidazioneContenutiApplicativi vx = new ValidazioneContenutiApplicativi();
			vx.setStato(StatoFunzionalitaConWarning.toEnumConstant(statoValidazione));
			vx.setTipo(ValidazioneContenutiApplicativiTipo.toEnumConstant(tipoValidazione));
			if(applicaMTOM != null){
				if(ServletUtils.isCheckBoxEnabled(applicaMTOM))
					vx.setAcceptMtomMessage(StatoFunzionalita.ABILITATO);
				else 
					vx.setAcceptMtomMessage(StatoFunzionalita.DISABILITATO);
			} else 
				vx.setAcceptMtomMessage(null);
			
			portaDelegata.setValidazioneContenutiApplicativi(vx);

			portaDelegata.setAutorizzazioneContenuto(autorizzazioneContenuti);
			
			if(autorizzazione != null && autorizzazione.equals(AutorizzazioneUtilities.STATO_XACML_POLICY) && allegatoXacmlPolicy.getValue() != null) {
				portaDelegata.setXacmlPolicy(new String(allegatoXacmlPolicy.getValue()));
			} else {
				portaDelegata.setXacmlPolicy(null);
			}

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performCreateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.PORTE_DELEGATE_BY_SOGGETTO;
			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
			List<PortaDelegata> lista = porteDelegateCore.porteDelegateList(soggInt, ricerca);

			porteDelegateHelper.preparePorteDelegateList(ricerca, lista,idLista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE, ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE, 
					ForwardParams.ADD());
		}  
	}
}
