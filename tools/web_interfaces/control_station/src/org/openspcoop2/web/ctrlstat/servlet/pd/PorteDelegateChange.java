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


package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataLocalForward;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.utils.PropertiesSerializator;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.autorizzazione.CostantiAutorizzazione;
import org.openspcoop2.pdd.core.integrazione.GruppoIntegrazione;
import org.openspcoop2.pdd.core.integrazione.TipoIntegrazione;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniHelper;
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
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

/**
 * porteDelegateChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session);
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);

			String idPorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String nomePorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA);
			String idsogg = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String descr = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_DESCRIZIONE);
			String statoPorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_STATO_PORTA);
			String tipoSoggettoErogatore = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SP);
			String nomeSoggettoErogatore = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SP);
			String idSoggettoErogatore = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SOGGETTO_ID);
			String tiposervizio = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SERVIZIO);
			String servizio = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO);
			String versioneServizio = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_VERSIONE_SERVIZIO);
			String servid = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_ID);
			String modeaz = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_AZIONE);
			String azione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
			String azid = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE_ID);
			String stateless = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_STATELESS);
			String localForward = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD );
			String paLocalForward = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD_PA);
			String gestBody = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_BODY);
			String gestManifest = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_MANIFEST);
			String ricsim = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA);
			String ricasim = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			String scadcorr = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SCADENZA_CORRELAZIONE_APPLICATIVA);
			String forceWsdlBased = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_FORCE_INTERFACE_BASED);

			String messageEngine = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_MESSAGE_ENGINE);
						
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			BinaryParameter allegatoXacmlPolicy = porteDelegateHelper.getBinaryParameter(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
			
			String idFruizione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			String idTab = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteDelegateHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			String serviceBindingS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVICE_BINDING);
			ServiceBinding serviceBinding = null;
			if(StringUtils.isNotEmpty(serviceBindingS))
				serviceBinding = ServiceBinding.valueOf(serviceBindingS);
			
			boolean datiInvocazione = ServletUtils.isCheckBoxEnabled(porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_DATI_INVOCAZIONE));
				
			boolean datiAltroPorta = ServletUtils.isCheckBoxEnabled(porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_ALTRO_PORTA));
			boolean datiAltroApi = ServletUtils.isCheckBoxEnabled(porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_ALTRO_API));
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session);
			if(parentPD == null) {
				if(datiInvocazione) {
					parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE;
					ServletUtils.setObjectIntoSession(session, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT);
				}
				else {
					parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
				}
			}

			// check su oldNomePD
			PageData pdOld =  ServletUtils.getPageDataFromSession(session);
			String oldNomePD = pdOld.getHidden(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_OLD_NOME_PD);
			oldNomePD = (((oldNomePD != null) && !oldNomePD.equals("")) ? oldNomePD : nomePorta);
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			String postBackElementName = porteDelegateHelper.getPostBackElementName();
			if(postBackElementName!=null && PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_AZIONE.equals(postBackElementName)) {
				// ho cambiato modalita', elimino il valore
				azione = null;
			}
			
			// Prendo nome e tipo del soggetto
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);

			
			IDSoggetto idSoggettoFruitore = null;
			if(porteDelegateCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				idSoggettoFruitore = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
			}else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				idSoggettoFruitore = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
			}

			boolean usataInConfigurazioni = false;
			boolean usataInConfigurazioneDefault = false;
			boolean addTrattinoSelezioneNonEffettuata = false;
			
			String integrazioneStato = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_INTEGRAZIONE_STATO);
			String integrazione = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_INTEGRAZIONE);
			String[] integrazioneGruppi = porteDelegateHelper.getParameterValues(CostantiControlStation.PARAMETRO_PORTE_METADATI_GRUPPO);
		
			List<GruppoIntegrazione> integrazioneGruppiDaVisualizzare = new ArrayList<GruppoIntegrazione>();  
			Map<String, List<String>> integrazioneGruppiValoriDeiGruppi = new HashMap<String, List<String>>();
			boolean isConfigurazione = parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE; 
			boolean visualizzaSezioneOpzioniAvanzate = !(porteDelegateHelper.isModalitaStandard() || (isConfigurazione && !datiAltroPorta));

			// dal secondo accesso in poi il calcolo dei gruppi da visualizzare avviene leggendo i parametri dalla richiesta
			if(integrazioneStato != null && visualizzaSezioneOpzioniAvanzate) {
				if(integrazioneStato.equals(CostantiControlStation.VALUE_PARAMETRO_PORTE_INTEGRAZIONE_STATO_RIDEFINITO)) {
					if(integrazioneGruppi != null) {
						for (String gruppoSelezionato : integrazioneGruppi) {
							integrazioneGruppiDaVisualizzare.add(GruppoIntegrazione.toEnumConstant(gruppoSelezionato));
						}
						
						// leggere i valori selezionati per ogni gruppo selezionato
						for (GruppoIntegrazione group : integrazioneGruppiDaVisualizzare) {
							List<String> valoriGruppoList = new ArrayList<String>();
							if(group.isMulti()) {
								String[] valoriGruppo = porteDelegateHelper.getParameterValues(CostantiControlStation.PARAMETRO_PORTE_METADATI_GRUPPO_SINGOLO+group.getValue());
								if(valoriGruppo != null) {
									valoriGruppoList.addAll(Arrays.asList(valoriGruppo));
								}
							} else {
								String valoreGruppo = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_METADATI_GRUPPO_SINGOLO+group.getValue());
								if(valoreGruppo != null) {
									valoriGruppoList.add(valoreGruppo);
								}
							}
							
							integrazioneGruppiValoriDeiGruppi.put(group.getValue(), valoriGruppoList);							
						}
					}
				}
			}
			
			// Prendo la porta delegata
			IDPortaDelegata idpd = new IDPortaDelegata();
			idpd.setNome(oldNomePD);
			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idpd);
			
			Long idAspsLong = -1L;
			if(idAsps.equals("")) {
				PortaDelegataServizio servizio2 = pde.getServizio();
				idAspsLong = servizio2.getId();
			} else {
				idAspsLong = Long.parseLong(idAsps);
			}
			
			// controllo se la porta e' usata in qualche configurazione e se e' usata nella configurazione di default
			MappingFruizionePortaDelegata mappingFruizione = new MappingFruizionePortaDelegata();
			mappingFruizione.setIdFruitore(idSoggettoFruitore);
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idAspsLong);
			IDServizio idServizioCheck = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
			mappingFruizione.setIdServizio(idServizioCheck);
			mappingFruizione.setIdPortaDelegata(idpd );
			usataInConfigurazioni = porteDelegateCore.existsMappingFruizionePortaDelegata(mappingFruizione);
			
			IDPortaDelegata idPortaDelegataAssociataDefault = porteDelegateCore.getIDPortaDelegataAssociataDefault(idServizioCheck, idSoggettoFruitore);
			usataInConfigurazioneDefault = (idPortaDelegataAssociataDefault != null && idPortaDelegataAssociataDefault.getNome().equals(idpd.getNome()));
			
			// Prendo il numero di correlazioni applicative
			CorrelazioneApplicativa ca = pde.getCorrelazioneApplicativa();
			int numCorrApp = 0;
			if (ca != null) {
				numCorrApp = ca.sizeElementoList();
			}

			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(idSoggettoFruitore.getTipo());
			List<String> tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);

			// Informazioni sul numero di ServiziApplicativi, Correlazione Applicativa e stato Message-Security
			int numSA =pde.sizeServizioApplicativoList();
			int numRuoli = 0;
			if(pde.getRuoli()!=null){
				numRuoli = pde.getRuoli().sizeRuoloList();
			}
			int numScope = 0;
			if(pde.getScope()!=null){
				numScope = pde.getScope().sizeScopeList();
			}
			String statoMessageSecurity  = pde.getStatoMessageSecurity() ;

			// Stato MTOM
			boolean isMTOMAbilitatoReq = false;
			boolean isMTOMAbilitatoRes= false;
			if(pde.getMtomProcessor()!= null){
				if(pde.getMtomProcessor().getRequestFlow() != null){
					if(pde.getMtomProcessor().getRequestFlow().getMode() != null){
						MTOMProcessorType mode = pde.getMtomProcessor().getRequestFlow().getMode();
						if(!mode.equals(MTOMProcessorType.DISABLE))
							isMTOMAbilitatoReq = true;
					}
				}

				if(pde.getMtomProcessor().getResponseFlow() != null){
					if(pde.getMtomProcessor().getResponseFlow().getMode() != null){
						MTOMProcessorType mode = pde.getMtomProcessor().getResponseFlow().getMode();
						if(!mode.equals(MTOMProcessorType.DISABLE))
							isMTOMAbilitatoRes = true;
					}
				}
			}

			String statoMessageMTOM  = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_DISABILITATO;

			if(isMTOMAbilitatoReq || isMTOMAbilitatoRes)
				statoMessageMTOM  = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_ABILITATO;



			int numCorrelazioneReq =0; 
			if(pde.getCorrelazioneApplicativa() != null)
				numCorrelazioneReq  = pde.getCorrelazioneApplicativa().sizeElementoList();

			int numCorrelazioneRes =0;
			if(pde.getCorrelazioneApplicativaRisposta() != null)
				numCorrelazioneRes = pde.getCorrelazioneApplicativaRisposta().sizeElementoList();

			boolean riusoID = false;
			if(numCorrelazioneReq>0){
				for (int i = 0; i < numCorrelazioneReq; i++) {
					if(StatoFunzionalita.ABILITATO.equals(pde.getCorrelazioneApplicativa().getElemento(i).getRiusoIdentificativo())){
						riusoID = true;
						break;
					}
				}
			}
			
			String autenticazione = pde.getAutenticazione();
			String autenticazioneCustom = null;
			if (autenticazione != null &&
					!TipoAutenticazione.getValues().contains(autenticazione)) {
				autenticazioneCustom = autenticazione;
				autenticazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM;
			}
			String	autenticazioneOpzionale = "";
			if(pde.getAutenticazioneOpzionale()!=null){
				if (pde.getAutenticazioneOpzionale().equals(StatoFunzionalita.ABILITATO)) {
					autenticazioneOpzionale = Costanti.CHECK_BOX_ENABLED;
				}
			}
			TipoAutenticazionePrincipal autenticazionePrincipal = porteDelegateCore.getTipoAutenticazionePrincipal(pde.getProprietaAutenticazioneList());
			List<String> autenticazioneParametroList = porteDelegateCore.getParametroAutenticazione(autenticazione, pde.getProprietaAutenticazioneList());

			String autorizzazione = null;
			String autorizzazioneCustom = null;
			String autorizzazioneAutenticati = null;
			String autorizzazioneRuoli = null;
			String autorizzazioneRuoliTipologia = null;
			String ruoloMatch = null;
			String autorizzazioneContenuti = pde.getAutorizzazioneContenuto();
			String autorizzazioneContenutiProperties = null;
			String autorizzazioneContenutiStato = null;
			
			if(autorizzazioneContenuti == null) {
				autorizzazioneContenutiStato = StatoFunzionalita.DISABILITATO.getValue();
			} else if(autorizzazioneContenuti.equals(CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN)) {
				autorizzazioneContenutiStato = StatoFunzionalita.ABILITATO.getValue();
				List<Proprieta> proprietaAutorizzazioneContenutoList = pde.getProprietaAutorizzazioneContenutoList();
				StringBuilder sb = new StringBuilder();
				for (Proprieta proprieta : proprietaAutorizzazioneContenutoList) {
					if(sb.length() >0)
						sb.append("\n");
					
					sb.append(proprieta.getNome()).append("=").append(proprieta.getValore()); 
				}						
				
				autorizzazioneContenutiProperties = sb.toString();
			} else { // custom
				autorizzazioneContenutiStato = CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_CUSTOM;
			}
			
			if (pde.getAutorizzazione() != null &&
					!TipoAutorizzazione.getAllValues().contains(pde.getAutorizzazione())) {
				autorizzazioneCustom = pde.getAutorizzazione();
				autorizzazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
			}
			else{
				autorizzazione = AutorizzazioneUtilities.convertToStato(pde.getAutorizzazione());
				if(TipoAutorizzazione.isAuthenticationRequired(pde.getAutorizzazione()))
					autorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
				if(TipoAutorizzazione.isRolesRequired(pde.getAutorizzazione()))
					autorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
				autorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(pde.getAutorizzazione()).getValue();
			}
			
			if(pde.getRuoli()!=null && pde.getRuoli().getMatch()!=null){
				ruoloMatch = pde.getRuoli().getMatch().getValue();
			}
			
			String statoValidazione = null;
			String tipoValidazione = null;
			String applicaMTOM = "";
			ValidazioneContenutiApplicativi vx = pde.getValidazioneContenutiApplicativi();
			if (vx == null) {
				statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
				tipoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_INTERFACE;
			} else {
				if(vx.getStato()!=null)
					statoValidazione = vx.getStato().toString();
				if ((statoValidazione == null) || "".equals(statoValidazione)) {
					statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
				}
				
				if(vx.getTipo()!=null)
					tipoValidazione = vx.getTipo().toString();
				if (tipoValidazione == null || "".equals(tipoValidazione)) {
					tipoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_INTERFACE ;
				}
				
				if(vx.getAcceptMtomMessage()!=null)
					if (vx.getAcceptMtomMessage().equals(StatoFunzionalita.ABILITATO)) 
						applicaMTOM = Costanti.CHECK_BOX_ENABLED;
			}
			
			String gestioneToken = null;
			String gestioneTokenPolicy = null;
			String gestioneTokenOpzionale = null;
			String gestioneTokenValidazioneInput = null;
			String gestioneTokenIntrospection = null;
			String gestioneTokenUserInfo = null;
			String gestioneTokenTokenForward = null;
			
			String autenticazioneTokenIssuer = null;
			String autenticazioneTokenClientId = null;
			String autenticazioneTokenSubject = null;
			String autenticazioneTokenUsername = null;
			String autenticazioneTokenEMail = null;
			
			String autorizzazione_token = null;
			String autorizzazione_tokenOptions = null;
			
			String identificazioneAttributiStato = null;
			String [] attributeAuthoritySelezionate = null;
			String attributeAuthorityAttributi = null;
			
			if(pde.getGestioneToken() != null) {
				gestioneTokenPolicy = pde.getGestioneToken().getPolicy();
				if(gestioneTokenPolicy == null) {
					gestioneToken = StatoFunzionalita.DISABILITATO.getValue();
					gestioneTokenPolicy = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
				} else {
					gestioneToken = StatoFunzionalita.ABILITATO.getValue();
				}
				
				StatoFunzionalita tokenOpzionale = pde.getGestioneToken().getTokenOpzionale();
				if(tokenOpzionale == null) {
					gestioneTokenOpzionale = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_OPZIONALE;
				}else { 
					gestioneTokenOpzionale = tokenOpzionale.getValue();
				}
				
				StatoFunzionalitaConWarning validazione = pde.getGestioneToken().getValidazione();
				if(validazione == null) {
					gestioneTokenValidazioneInput = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_VALIDAZIONE_INPUT;
				}else { 
					gestioneTokenValidazioneInput = validazione.getValue();
				}
				
				StatoFunzionalitaConWarning introspection = pde.getGestioneToken().getIntrospection();
				if(introspection == null) {
					gestioneTokenIntrospection = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_INTROSPECTION;
				}else { 
					gestioneTokenIntrospection = introspection.getValue();
				}
				
				StatoFunzionalitaConWarning userinfo = pde.getGestioneToken().getUserInfo();
				if(userinfo == null) {
					gestioneTokenUserInfo = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_USER_INFO;
				}else { 
					gestioneTokenUserInfo = userinfo.getValue();
				}
				
				StatoFunzionalita tokenForward = pde.getGestioneToken().getForward();
				if(tokenForward == null) {
					gestioneTokenTokenForward = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TOKEN_FORWARD;
				}else { 
					gestioneTokenTokenForward = tokenForward.getValue();
				}
				
				autorizzazione_tokenOptions = pde.getGestioneToken().getOptions();
				if((autorizzazione_tokenOptions!=null && !"".equals(autorizzazione_tokenOptions))) {
					autorizzazione_token = Costanti.CHECK_BOX_ENABLED;
				}
				else {
					autorizzazione_token = Costanti.CHECK_BOX_DISABLED;
				}
				
				if(pde.getGestioneToken().getAutenticazione() != null) {
					
					StatoFunzionalita issuer = pde.getGestioneToken().getAutenticazione().getIssuer();
					if(issuer == null) {
						autenticazioneTokenIssuer = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_ISSUER;
					}else { 
						autenticazioneTokenIssuer = issuer.getValue();
					}
					
					StatoFunzionalita clientId = pde.getGestioneToken().getAutenticazione().getClientId();
					if(clientId == null) {
						autenticazioneTokenClientId = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_CLIENT_ID;
					}else { 
						autenticazioneTokenClientId = clientId.getValue();
					}
					
					StatoFunzionalita subject = pde.getGestioneToken().getAutenticazione().getSubject();
					if(subject == null) {
						autenticazioneTokenSubject = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_SUBJECT;
					}else { 
						autenticazioneTokenSubject = subject.getValue();
					}
					
					StatoFunzionalita username = pde.getGestioneToken().getAutenticazione().getUsername();
					if(username == null) {
						autenticazioneTokenUsername = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_USERNAME;
					}else { 
						autenticazioneTokenUsername = username.getValue();
					}
					
					StatoFunzionalita mailTmp = pde.getGestioneToken().getAutenticazione().getEmail();
					if(mailTmp == null) {
						autenticazioneTokenEMail = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_EMAIL;
					}else { 
						autenticazioneTokenEMail = mailTmp.getValue();
					}
					
				}
				else {
					autenticazioneTokenIssuer = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_ISSUER;
					autenticazioneTokenClientId = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_CLIENT_ID;
					autenticazioneTokenSubject = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_SUBJECT;
					autenticazioneTokenUsername = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_USERNAME;
					autenticazioneTokenEMail = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_EMAIL;
				}
			}
			else {
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
			
			String autorizzazioneScope = null;
			String autorizzazioneScopeMatch = null;
			
			if(pde.getScope() != null) {
				autorizzazioneScope =  pde.getScope().getStato().equals(StatoFunzionalita.ABILITATO) ? Costanti.CHECK_BOX_ENABLED : ""; 
								
				if(pde.getScope()!=null && pde.getScope().getMatch()!=null){
					autorizzazioneScopeMatch = pde.getScope().getMatch().getValue();
				}
			} else {
				autorizzazioneScope = "";
			}
			
			if(identificazioneAttributiStato==null) {
				identificazioneAttributiStato = pde.sizeAttributeAuthorityList()>0 ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
				if(pde.sizeAttributeAuthorityList()>0) {
					attributeAuthoritySelezionate = porteDelegateCore.buildAuthorityArrayString(pde.getAttributeAuthorityList());
					attributeAuthorityAttributi = porteDelegateCore.buildAttributesStringFromAuthority(pde.getAttributeAuthorityList());
				}
			}
			
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
			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			
			String nomeBreadCrumb = oldNomePD;
			
			if(parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE) {
//				List<MappingFruizionePortaDelegata> mappingServiziPorteAppList = apsCore.serviziFruitoriMappingList(Long.parseLong(idFruizione), idSoggettoFruitore, idServizioCheck, null);
//						
//				MappingFruizionePortaDelegata mappingFruizionePortaDelegata = null;
//				for (MappingFruizionePortaDelegata mappingFruizionePortaDelegataTmp : mappingServiziPorteAppList) {
//					if(mappingFruizionePortaDelegataTmp.getIdPortaDelegata().getNome().equals(oldNomePD)) {
//						mappingFruizionePortaDelegata = mappingFruizionePortaDelegataTmp;
//						break;
//					}
//				}
//				
//				if(mappingFruizionePortaDelegata.isDefault()) {
//					nomeBreadCrumb = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_NOME_DEFAULT;
//				} else {
//					nomeBreadCrumb = mappingFruizionePortaDelegata.getNome(); 
//				}
				
				if(datiInvocazione) {
					lstParam.remove(lstParam.size()-1);
					if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
						lstParam.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_PORTE_DELEGATE_MODIFICA_DATI_INVOCAZIONE,null));
					} else {
						lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_DATI_INVOCAZIONE_DI + porteDelegateHelper.getLabelIdServizio(asps),null));
					}
					nomeBreadCrumb=null;
				}
				else if(datiAltroPorta) {
					String labelPerPorta = null;
					if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
						labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
								PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_OPZIONI_AVANZATE_DI,
								PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_OPZIONI_AVANZATE,
								pde);
					}
					else {
						lstParam.remove(lstParam.size()-1);
						labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_OPZIONI_AVANZATE_DI+pde.getNome();
					}				
					lstParam.add(new Parameter(labelPerPorta,  null));
					//lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_OPZIONI_AVANZATE_DI + porteDelegateHelper.getLabelIdServizio(asps),null));
					nomeBreadCrumb=null;
				}
				else if(datiAltroApi) {
					lstParam.remove(lstParam.size()-1);
					if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
						lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_OPZIONI_AVANZATE,null));
					} else {
						lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_OPZIONI_AVANZATE_DI + porteDelegateHelper.getLabelIdServizio(asps),null));
					}
					nomeBreadCrumb=null;
				}
				else {
					nomeBreadCrumb = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(null,null,pde);
				}
			}
			
			if(nomeBreadCrumb!=null) {
				lstParam.add(new Parameter(nomeBreadCrumb , null));
			}

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if (porteDelegateHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				String patternErogatore = nomeSoggettoErogatore;
				String patternServizio = servizio;
				String patternAzione = azione;

				if (descr == null) {
					descr = pde.getDescrizione();
				}
				
				if(idSoggettoErogatore == null) {
					PortaDelegataSoggettoErogatore pdsse = pde.getSoggettoErogatore();
					tipoSoggettoErogatore = pdsse.getTipo();
					nomeSoggettoErogatore = pdsse.getNome();
					idSoggettoErogatore = tipoSoggettoErogatore + "/" + nomeSoggettoErogatore;
				}
				
				if(servid == null) {
					PortaDelegataServizio pds = pde.getServizio();
					tiposervizio = pds.getTipo();
					servizio = pds.getNome();
					servid = pds.getTipo() + "/" + pds.getNome() + "/" + pds.getVersione().intValue();
				}
				
				if (statoPorta == null) {
					statoPorta = pde.getStato()!=null ? pde.getStato().getValue() : CostantiConfigurazione.ABILITATO.getValue();
				}
				if (stateless == null) {
					if(pde.getStateless()!=null){
						stateless = pde.getStateless().toString();
					}
				}
				if (localForward == null) {
					if(pde.getLocalForward()!=null && pde.getLocalForward().getStato()!=null){
						localForward = pde.getLocalForward().getStato().toString();
					}
					if (localForward == null) {
						localForward = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD_DISABILITATO;
					}
					if(pde.getLocalForward()!=null && pde.getLocalForward().getPortaApplicativa()!=null) {
						paLocalForward = pde.getLocalForward().getPortaApplicativa();
					}
				}
				if (gestBody == null) {
					String allegaBody = null;
					if(pde.getAllegaBody()!=null){
						allegaBody = pde.getAllegaBody().toString();
					}
					String scartaBody = null;
					if(pde.getScartaBody()!=null){
						scartaBody = pde.getScartaBody().toString();
					}
					if (PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_ABILITATO.equals(allegaBody) &&
							PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO.equals(scartaBody))
						gestBody = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_ALLEGA;
					else if (PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO.equals(allegaBody) &&
							PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_ABILITATO.equals(scartaBody))
						gestBody = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_SCARTA;
					else if (PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO.equals(allegaBody) && 
							PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO.equals(scartaBody))
						gestBody = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_NONE; 
				}
				if (gestManifest == null) {
					if(pde.getGestioneManifest()!=null)
						gestManifest = pde.getGestioneManifest().toString();
				}
				if (ricsim == null) {
					if(pde.getRicevutaAsincronaSimmetrica()!=null)
						ricsim = pde.getRicevutaAsincronaSimmetrica().toString();
					if ((ricsim == null) || "".equals(ricsim)) {
						ricsim = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA_ABILITATO;

					}
				}
				if (ricasim == null) {
					if(pde.getRicevutaAsincronaAsimmetrica()!=null)
						ricasim = pde.getRicevutaAsincronaAsimmetrica().toString();
					if ((ricasim == null) || "".equals(ricasim)) {
						ricasim = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA_ABILITATO; 
					}
				}
				
				if (integrazioneStato == null) {
					if(pde.getIntegrazione() == null) {
						integrazioneStato = CostantiControlStation.VALUE_PARAMETRO_PORTE_INTEGRAZIONE_STATO_DEFAULT;
					} else if(TipoIntegrazione.DISABILITATO.getValue().equals(pde.getIntegrazione())) {
						integrazioneStato = CostantiControlStation.VALUE_PARAMETRO_PORTE_INTEGRAZIONE_STATO_DISABILITATO;
					} else {
						integrazioneStato = CostantiControlStation.VALUE_PARAMETRO_PORTE_INTEGRAZIONE_STATO_RIDEFINITO;
					}
				}

				if (integrazione == null) {
					integrazione = pde.getIntegrazione();
					
					List<String> integrazioneGruppiList = new ArrayList<String>();
					
					if(integrazioneStato.equals(CostantiControlStation.VALUE_PARAMETRO_PORTE_INTEGRAZIONE_STATO_RIDEFINITO)) {
						// decodificare il contenuto di integrazione per generare gli elementi grafici necessari.
						List<String> valoriIntegrazione = integrazione != null ? Arrays.asList(integrazione.split(",")) : new ArrayList<String>();
						for (String valoreIntegrazione : valoriIntegrazione) {
							TipoIntegrazione tipoIntegrazione = TipoIntegrazione.toEnumConstant(valoreIntegrazione);
							GruppoIntegrazione group = tipoIntegrazione != null ? tipoIntegrazione.getGroup() : GruppoIntegrazione.PLUGIN;
							String gruppoValore = group.getValue();
							
							List<String> valoriIntegrazionePerGruppo = null;
							if(integrazioneGruppiValoriDeiGruppi.containsKey(gruppoValore)) {
								valoriIntegrazionePerGruppo = integrazioneGruppiValoriDeiGruppi.remove(gruppoValore);
							} else {
								valoriIntegrazionePerGruppo = new ArrayList<String>();
							}
							
							valoriIntegrazionePerGruppo.add(valoreIntegrazione);
							integrazioneGruppiValoriDeiGruppi.put(gruppoValore, valoriIntegrazionePerGruppo);
							 
							if(!integrazioneGruppiDaVisualizzare.contains(group)) {
								integrazioneGruppiDaVisualizzare.add(group);
								integrazioneGruppiList.add(gruppoValore);
							}
						}
						
						integrazioneGruppi = integrazioneGruppiList.size() > 0 ? integrazioneGruppiList.toArray(new String[integrazioneGruppiList.size()]) : null;
					}
				}
				
				if (messageEngine == null) {
					if(pde.getOptions()!=null) {
						Map<String, List<String>> props = PropertiesSerializator.convertoFromDBColumnValue(pde.getOptions());
						if(props!=null && props.size()>0) {
							String msgFactory = TransportUtils.getFirstValue(props,CostantiPdD.OPTIONS_MESSAGE_FACTORY);
							if(msgFactory!=null) {
								messageEngine = msgFactory;
							}
						}
					}
				}

				if ((scadcorr == null) && (ca != null)) {
					scadcorr = ca.getScadenza();
				}
				if (modeaz == null) {
					PortaDelegataAzione pda = pde.getAzione();
					if (pda == null) {
						modeaz = "";
						azione = "";
						patternAzione = "";
					} else {
						if(pda.getIdentificazione()!=null)
							modeaz = pda.getIdentificazione().toString();
						azione = pda.getNome();
						patternAzione = pda.getPattern();
						azid = "" + pda.getId();
					}
					if ((modeaz == null) || "".equals(modeaz)) {
						modeaz = PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT;
					}
					boolean useForceWSDLBased = true;
					// assegno costante
					if (modeaz != null) {
						switch (PortaDelegataAzioneIdentificazione.toEnumConstant(modeaz)) {
						case STATIC:
							useForceWSDLBased = false;
							int azidInt = 0;
							if (azid != null)
								azidInt = Integer.parseInt(azid);
							if ( (azidInt == -2) || (azidInt>0) ) {
//								modeaz = IdentificazioneView.REGISTER_INPUT.toString();
								azid = azione;
							} else {
//								modeaz = IdentificazioneView.USER_INPUT.toString();
							}
							break;
						case INTERFACE_BASED:
							useForceWSDLBased = false;
							break;
						case CONTENT_BASED:
						case INPUT_BASED:
						case HEADER_BASED:
						case URL_BASED:
						case SOAP_ACTION_BASED:
						default:
							break;
						}

						if(useForceWSDLBased){
							StatoFunzionalita forceWsdlBased2 = pda.getForceInterfaceBased();

							if(forceWsdlBased2 != null && forceWsdlBased2.equals(StatoFunzionalita.ABILITATO)){
								forceWsdlBased = "yes";
							} else {
								forceWsdlBased = "";
							}
						}
					}
				}

				if ((modeaz != null) && !modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
						(azione == null)) {
					azione = "";
				}

				if(!usataInConfigurazioneDefault) {
					PortaDelegataAzione pda = pde.getAzione();
					if(pda != null) {
						List<String> azioneDelegataList = pda.getAzioneDelegataList();
						StringBuilder sb = new StringBuilder();
						
						for (String aD : azioneDelegataList) {
							if(sb.length() > 0) sb.append(", ");
							sb.append(aD);
						}
						azione = sb.toString();
					}
				}
				
				// Se modesp = register-input, prendo la lista di tutti i
				// soggetti e la metto in un array
				String[] soggettiList = null;
				String[] soggettiListLabel = null;
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

				// Se modeservizio = register-input, prendo la lista di tutti i
				// servizi e la metto in un array
				String[] serviziList = null;
				String[] serviziListLabel = null;
				if ((idSoggettoErogatore != null && !"".equals(idSoggettoErogatore) && idSoggettoErogatore.contains("/"))) {
					IDSoggetto idSoggetto = new IDSoggetto(idSoggettoErogatore.split("/")[0], idSoggettoErogatore.split("/")[1]);
					FiltroRicercaServizi filtro = new FiltroRicercaServizi();
					filtro.setTipoSoggettoErogatore(idSoggetto.getTipo());
					filtro.setNomeSoggettoErogatore(idSoggetto.getNome());
					List<IDServizio> listIdServ = null;
					try{
						listIdServ = apsCore.getAllIdServizi(filtro);
					}catch(DriverRegistroServiziNotFound dNotFound){}
					
					if(listIdServ!=null && listIdServ.size()>0){
						List<String> serviziListTmp = new ArrayList<String>();
						Map<String, IDServizio> serviziMapTmp = new HashMap<String,IDServizio>();
						for (IDServizio idServizio : listIdServ) {
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
						(idSoggettoErogatore != null && !"".equals(idSoggettoErogatore) && idSoggettoErogatore.contains("/"))
						) {
					idSoggetto = new IDSoggetto(idSoggettoErogatore.split("/")[0], idSoggettoErogatore.split("/")[1]);
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
				
				AccordoServizioParteComuneSintetico as = null;
				if ( servS!=null ) {
					IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(servS.getAccordoServizioParteComune());
					as = apcCore.getAccordoServizioSintetico(idAccordo);
					
					if(serviceBinding == null) {
						serviceBinding = porteDelegateCore.toMessageServiceBinding(as.getServiceBinding());
					}
					
				}
				
				// Se modeaz = register-input, prendo la lista delle azioni
				// associate a servid e la metto in un array
				String[] azioniList = null;
				String[] azioniListLabel = null;
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

				// setto oldNomePD
				pd.addHidden(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_OLD_NOME_PD, oldNomePD);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addPorteDelegateToDati(TipoOperazione.CHANGE, idsogg, nomePorta, dati,
						idPorta, descr, autenticazione,
						autorizzazione, 
						idSoggettoErogatore, soggettiList, soggettiListLabel,
						nomeSoggettoErogatore, tipoSoggettoErogatore,
						patternErogatore, servid, serviziList,
						serviziListLabel, servizio, tiposervizio, versioneServizio,
						patternServizio, modeaz, azid, azioniListLabel,
						azioniList, azione, patternAzione, numAzioni,
						stateless, localForward, paLocalForward, ricsim, ricasim, statoValidazione,
						tipoValidazione, numCorrApp, scadcorr, gestBody,
						gestManifest, integrazioneStato, integrazione,
						integrazioneGruppi, integrazioneGruppiDaVisualizzare, integrazioneGruppiValoriDeiGruppi,
						autenticazioneOpzionale, autenticazionePrincipal, autenticazioneParametroList, autenticazioneCustom, 
						autorizzazioneCustom,autorizzazioneAutenticati,autorizzazioneRuoli,autorizzazioneRuoliTipologia,
						autorizzazioneContenutiStato, autorizzazioneContenuti,autorizzazioneContenutiProperties,
						idsogg,protocollo,numSA,numRuoli,ruoloMatch,
						statoMessageSecurity,statoMessageMTOM,
						numCorrelazioneReq,numCorrelazioneRes,
						forceWsdlBased,applicaMTOM,riusoID,
						servS, as,serviceBinding,
						statoPorta,usataInConfigurazioni,usataInConfigurazioneDefault,
						StatoFunzionalita.ABILITATO.equals(pde.getRicercaPortaAzioneDelegata()), 
						(pde.getAzione()!=null ? pde.getAzione().getNomePortaDelegante() : null), gestioneToken,null,null,
						gestioneTokenPolicy,gestioneTokenOpzionale,
						gestioneTokenValidazioneInput,gestioneTokenIntrospection,gestioneTokenUserInfo,gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_token,autorizzazione_tokenOptions,
						autorizzazioneScope,numScope, autorizzazioneScopeMatch,allegatoXacmlPolicy,
						messageEngine, pde.getCanale(),
						identificazioneAttributiStato, null,null, attributeAuthoritySelezionate, attributeAuthorityAttributi);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, null, null, null, idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE, ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi

			boolean isOk = porteDelegateHelper.porteDelegateCheckData(TipoOperazione.CHANGE, oldNomePD, datiAltroPorta);

			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// Se modesp = register-input, prendo la lista di tutti i
				// soggetti e la metto in un array
				String[] soggettiList = null;
				String[] soggettiListLabel = null;
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
				if ((idSoggettoErogatore != null && !"".equals(idSoggettoErogatore) && idSoggettoErogatore.contains("/"))) {
					IDSoggetto idSoggetto = new IDSoggetto(idSoggettoErogatore.split("/")[0], idSoggettoErogatore.split("/")[1]);
					FiltroRicercaServizi filtro = new FiltroRicercaServizi();
					filtro.setTipoSoggettoErogatore(idSoggetto.getTipo());
					filtro.setNomeSoggettoErogatore(idSoggetto.getNome());
					List<IDServizio> listTmp = null;
					try{
						listTmp = apsCore.getAllIdServizi(filtro);
					}catch(DriverRegistroServiziNotFound dNotFound){}
					if(listTmp!=null && listTmp.size()>0){
						List<String> serviziListTmp = new ArrayList<String>();
						List<String> serviziListLabelTmp = new ArrayList<String>();

						Map<String, IDServizio> serviziMapTmp = new HashMap<String,IDServizio>();
						for (IDServizio idServizio : listTmp) {
							if(tipiServizioCompatibiliAccordo.contains(idServizio.getTipo())){
								String keyServizio = idServizio.getTipo() + "/" + idServizio.getNome() + "/" + idServizio.getVersione();
								serviziListTmp.add(keyServizio);
								serviziMapTmp.put(keyServizio, idServizio);
								serviziListLabelTmp.add(idServizio.getTipo() + "/" + idServizio.getNome()+ "/" + idServizio.getVersione().intValue());
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
						(idSoggettoErogatore != null && !"".equals(idSoggettoErogatore) && idSoggettoErogatore.contains("/"))
						) {
					idSoggetto = new IDSoggetto(idSoggettoErogatore.split("/")[0], idSoggettoErogatore.split("/")[1]);
					idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(servid.split("/")[0], servid.split("/")[1], idSoggetto, 
							Integer.parseInt(servid.split("/")[2])); 
					try{
						servS = apsCore.getServizio(idServizio);
					}catch(DriverRegistroServiziNotFound dNotFound){
					}
				}
				
				AccordoServizioParteComuneSintetico as = null;
				if(servS!=null){
					IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(servS.getAccordoServizioParteComune());
					as = apcCore.getAccordoServizioSintetico(idAccordo);
				}
				
				// Se modeaz = register-input, prendo la lista delle azioni
				// associate a servid e la metto in un array
				String[] azioniList = null;
				String[] azioniListLabel = null;
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

				// setto oldNomePD
				pd.addHidden(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_OLD_NOME_PD, oldNomePD);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				// i pattern sono i nomi

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addPorteDelegateToDati(TipoOperazione.CHANGE, idsogg, nomePorta, dati,
						idPorta, descr, autenticazione,
						autorizzazione, 
						idSoggettoErogatore, soggettiList, soggettiListLabel,
						nomeSoggettoErogatore, tipoSoggettoErogatore,
						nomeSoggettoErogatore, servid,
						serviziList, serviziListLabel, servizio,
						tiposervizio, servizio, versioneServizio, modeaz, azid,
						azioniListLabel, azioniList, azione, azione,
						numAzioni,  stateless, localForward, paLocalForward, ricsim, ricasim,
						statoValidazione, tipoValidazione, numCorrApp, scadcorr, gestBody,
						gestManifest, integrazioneStato, integrazione,
						integrazioneGruppi, integrazioneGruppiDaVisualizzare, integrazioneGruppiValoriDeiGruppi,
						autenticazioneOpzionale, autenticazionePrincipal, autenticazioneParametroList, autenticazioneCustom, 
						autorizzazioneCustom,autorizzazioneAutenticati,autorizzazioneRuoli,autorizzazioneRuoliTipologia,
						autorizzazioneContenutiStato, autorizzazioneContenuti,autorizzazioneContenutiProperties,
						idsogg,protocollo,numSA,numRuoli,ruoloMatch,
						statoMessageSecurity,statoMessageMTOM,
						numCorrelazioneReq,numCorrelazioneRes,forceWsdlBased,applicaMTOM,riusoID,
						servS, as,serviceBinding,
						statoPorta,usataInConfigurazioni,usataInConfigurazioneDefault,
						StatoFunzionalita.ABILITATO.equals(pde.getRicercaPortaAzioneDelegata()), 
						(pde.getAzione()!=null ? pde.getAzione().getNomePortaDelegante() : null), gestioneToken,null,null,
						gestioneTokenPolicy,gestioneTokenOpzionale,
						gestioneTokenValidazioneInput,gestioneTokenIntrospection,gestioneTokenUserInfo,gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_token,autorizzazione_tokenOptions,
						autorizzazioneScope,numScope, autorizzazioneScopeMatch,allegatoXacmlPolicy,
						messageEngine, pde.getCanale(),
						identificazioneAttributiStato, null,null, attributeAuthoritySelezionate, attributeAuthorityAttributi);
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, null, null, null, idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE,
						ForwardParams.CHANGE());

			}

			// Modifico i dati della porta delegata nel db

			nomeSoggettoErogatore = idSoggettoErogatore.split("/")[1];
			tipoSoggettoErogatore = idSoggettoErogatore.split("/")[0];
			
			versioneServizio = servid.split("/")[2];
			servizio = servid.split("/")[1];
			tiposervizio = servid.split("/")[0];
			
			if (modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				azione = "";
			} else {
				azid = "";
			}

			long idPA = pde.getId();

			PortaDelegata oldPD = porteDelegateCore.getPortaDelegata(idPA);

			PortaDelegata portaDelegata = (PortaDelegata) oldPD.clone();// new
			// PortaDelegata();
			portaDelegata.setId(idPA);

			portaDelegata.setNome(nomePorta);
			IDPortaDelegata oldIDPortaDelegataForUpdate = new IDPortaDelegata();
			oldIDPortaDelegataForUpdate.setNome(oldPD.getNome());
			portaDelegata.setOldIDPortaDelegataForUpdate(oldIDPortaDelegataForUpdate);
			portaDelegata.setDescrizione(descr);
			if(statoPorta==null || "".equals(statoPorta) || CostantiConfigurazione.ABILITATO.toString().equals(statoPorta)){
				portaDelegata.setStato(StatoFunzionalita.ABILITATO);
			}
			else{
				portaDelegata.setStato(StatoFunzionalita.DISABILITATO);
			}
			
			if (stateless!=null && stateless.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_STATELESS_DEFAULT))
				portaDelegata.setStateless(null);
			else
				portaDelegata.setStateless(StatoFunzionalita.toEnumConstant(stateless));
			if (PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_ALLEGA.equals(gestBody))
				portaDelegata.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_ABILITATO));
			else
				portaDelegata.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO));
			if (PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_SCARTA.equals(gestBody))
				portaDelegata.setScartaBody(StatoFunzionalita.toEnumConstant(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_ABILITATO));
			else
				portaDelegata.setScartaBody(StatoFunzionalita.toEnumConstant(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO));
			if (gestManifest!=null && gestManifest.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_STATELESS_DEFAULT))
				portaDelegata.setGestioneManifest(null);
			else
				portaDelegata.setGestioneManifest(StatoFunzionalita.toEnumConstant(gestManifest));
			portaDelegata.setRicevutaAsincronaSimmetrica(StatoFunzionalita.toEnumConstant(ricsim));
			portaDelegata.setRicevutaAsincronaAsimmetrica(StatoFunzionalita.toEnumConstant(ricasim));
			if(localForward!=null){
				portaDelegata.setLocalForward(new PortaDelegataLocalForward());
				portaDelegata.getLocalForward().setStato(StatoFunzionalita.toEnumConstant(localForward));
				portaDelegata.getLocalForward().setPortaApplicativa(paLocalForward);
			}
			
			Map<String, List<String>> props = PropertiesSerializator.convertoFromDBColumnValue(portaDelegata.getOptions());
			props.remove(CostantiPdD.OPTIONS_MESSAGE_FACTORY);
			if(messageEngine!=null && !"".equals(messageEngine) && !CostantiControlStation.GESTIONE_MESSAGE_ENGINE_DEFAULT.equals(messageEngine)) {
				TransportUtils.put(props,CostantiPdD.OPTIONS_MESSAGE_FACTORY, messageEngine,false);
			}
			if(props.size()>0) {
				PropertiesSerializator ps = new PropertiesSerializator(props);
				portaDelegata.setOptions(ps.convertToDBColumnValue());
			}
			else {
				portaDelegata.setOptions(null);
			}

			PortaDelegataSoggettoErogatore pdSogg = new PortaDelegataSoggettoErogatore();
			IDSoggetto idSoggErogatore = new IDSoggetto(tipoSoggettoErogatore, nomeSoggettoErogatore);
			pdSogg.setId(soggettiCore.getSoggettoRegistro(idSoggErogatore).getId());
			if(pdSogg.getId()<=0){
				pdSogg.setId(-2l);
			}
			pdSogg.setNome(nomeSoggettoErogatore);
			pdSogg.setTipo(tipoSoggettoErogatore);

			portaDelegata.setSoggettoErogatore(pdSogg);

			PortaDelegataServizio pdServizio = new PortaDelegataServizio();
			IDServizio idServizio = null;
			idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tiposervizio, servizio, idSoggErogatore, Integer.parseInt(versioneServizio)); 
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
			pdServizio.setVersione(Integer.parseInt(versioneServizio));

			portaDelegata.setServizio(pdServizio);

			// se l azione e' settata allora creo il bean
			if(modeaz!=null && modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_DELEGATED_BY)) {
				// non modifico paAzione
			}
			else if (((azione != null) || (azid != null) || modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) ||
							modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SOAP_ACTION_BASED) ||
							modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_INTERFACE_BASED)) &&
							(!azione.equals("") || !azid.equals("") || 
									(modeaz != null && (modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) || 
											modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SOAP_ACTION_BASED) ||
											modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_INTERFACE_BASED))))) {
				PortaDelegataAzione pdAzione = new PortaDelegataAzione();

				if (modeaz.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
					azione = azid;
//					IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
//					AccordoServizioParteComune as = apcCore.getAccordoServizio(idAccordo);
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
//							throw new Exception("Accordo servizio parte specifica ["+idServizio.toString()+"] possiede un port type ["+asps.getPortType()+"] che non risulta essere registrato nell'accordo di servizio parte comune ["+asps.getAccordoServizioParteComune()+"]");
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
				} else {
					pdAzione.setForceInterfaceBased(null);
				}


				portaDelegata.setAzione(pdAzione);
			} else {
				portaDelegata.setAzione(null);
			}

			// soggetto proprietario
			SoggettoCtrlStat soggettoCS = soggettiCore.getSoggettoCtrlStat(soggInt);
			portaDelegata.setIdSoggetto(soggettoCS.getId());
			portaDelegata.setTipoSoggettoProprietario(soggettoCS.getTipo());
			portaDelegata.setNomeSoggettoProprietario(soggettoCS.getNome());
			// servizi applicativi
			// portaDelegata.setServizioApplicativoList(oldPD.getServizioApplicativoList());

			// ws sec
			// portaDelegata.setMessageSecurity(oldPD.getMessageSecurity());

			// Cambio i dati della vecchia CorrelazioneApplicativa
			// Non ne creo una nuova, altrimenti mi perdo le vecchie entry
			if (ca != null)
				ca.setScadenza(scadcorr);
			portaDelegata.setCorrelazioneApplicativa(ca);

			if(integrazioneStato.equals(CostantiControlStation.VALUE_PARAMETRO_PORTE_INTEGRAZIONE_STATO_DEFAULT)) {
				portaDelegata.setIntegrazione(null);
			} else if(integrazioneStato.equals(CostantiControlStation.VALUE_PARAMETRO_PORTE_INTEGRAZIONE_STATO_DISABILITATO)) {
				portaDelegata.setIntegrazione(CostantiControlStation.VALUE_PARAMETRO_PORTE_INTEGRAZIONE_STATO_DISABILITATO);
			} else {
				List<String> valoriFinaliIntegrazione = new ArrayList<String>();
				for (GruppoIntegrazione group : integrazioneGruppiDaVisualizzare) {
					valoriFinaliIntegrazione.addAll(integrazioneGruppiValoriDeiGruppi.get(group.getValue()));
				}
				portaDelegata.setIntegrazione(StringUtils.join(valoriFinaliIntegrazione.toArray(new String[valoriFinaliIntegrazione.size()]), ","));
			}

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);


			int idLista = -1;
			List<PortaDelegata> lista = null;
			
			switch (parentPD) {
			case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE:
				
				int idAspsInt = Integer.parseInt(idAsps);
				asps = apsCore.getAccordoServizioParteSpecifica(idAspsInt);
				IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
				
				AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
				
				if(datiInvocazione || datiAltroApi) {
					if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
						ErogazioniHelper erogazioniHelper = new ErogazioniHelper(request, pd, session);
						erogazioniHelper.prepareErogazioneChange(TipoOperazione.CHANGE, asps, 
								new IDSoggetto(portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario()));
						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
						return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.CHANGE());
					}
					
					String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
					boolean gestioneFruitori = false;
					if(tipologia!=null) {
						if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
							gestioneFruitori = true;
						}
					}
					if(gestioneFruitori) {
						
						idLista = Liste.SERVIZI;
						
						ricerca = apsHelper.checkSearchParameters(idLista, ricerca);
						
						ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE);
						
						String superUser =   ServletUtils.getUserLoginFromSession(session); 
						PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();
						boolean [] permessi = new boolean[2];
						permessi[0] = pu.isServizi();
						permessi[1] = pu.isAccordiCooperazione();
						List<AccordoServizioParteSpecifica> lista2 = null;
						if(apsCore.isVisioneOggettiGlobale(superUser)){
							lista2 = apsCore.soggettiServizioList(null, ricerca,permessi, gestioneFruitori, false);
						}else{
							lista2 = apsCore.soggettiServizioList(superUser, ricerca, permessi, gestioneFruitori, false);
						}

						apsHelper.prepareServiziList(ricerca, lista2);
						
					}
					else {
					
						idLista = Liste.SERVIZI_FRUITORI;
						ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
						
						List<Fruitore> listaFruitori = apsCore.serviziFruitoriList(idAspsInt, ricerca);
						apsHelper.prepareServiziFruitoriList(listaFruitori, idAsps, ricerca);
						
					}
				}
				else {
					idLista = Liste.CONFIGURAZIONE_FRUIZIONE;
					ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
					
					List<MappingFruizionePortaDelegata> listaMapping = apsCore.serviziFruitoriMappingList((long) Integer.parseInt(idFruizione), idSoggettoFruitore, idServizio2, ricerca);
					apsHelper.serviziFruitoriMappingList(listaMapping, idAsps, idsogg, idSoggettoFruitore, idFruizione, ricerca); 
				}
				
				break;
			case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_SOGGETTO:
				idLista = Liste.PORTE_DELEGATE_BY_SOGGETTO;
				ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
				lista = porteDelegateCore.porteDelegateList(soggInt, ricerca);
				porteDelegateHelper.preparePorteDelegateList(ricerca, lista,idLista);
				break;
			case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE:
			default:
				idLista = Liste.PORTE_DELEGATE;
				ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
				lista = porteDelegateCore.porteDelegateList(null, ricerca);
				porteDelegateHelper.preparePorteDelegateList(ricerca, lista,idLista);
				break;
			}

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			ForwardParams fwP = ForwardParams.CHANGE();
			
			if(datiAltroPorta && !porteDelegateHelper.isModalitaCompleta()) {
				fwP = PorteDelegateCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE;
			}
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE, fwP);

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE, 
					ForwardParams.CHANGE());
		}  
	}
}
