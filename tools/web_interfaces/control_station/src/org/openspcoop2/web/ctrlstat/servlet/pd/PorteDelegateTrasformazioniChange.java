/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDBLib;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteDelegateTrasformazioniChange
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteDelegateTrasformazioniChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);
		
		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			// Preparo il menu
			porteDelegateHelper.makeMenu();
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			String id = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			
			String idAsps = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			Long idFru = Long.parseLong(idFruizione);
			
			
			String first = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_FIRST);
			String nome = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_NOME);
			String stato = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_STATO);
			String azioniAllTmp = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL);
			boolean azioniAll = azioniAllTmp==null || "".equals(azioniAllTmp) || CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUE_TRUE.equals(azioniAllTmp);
			String [] azioni = porteDelegateHelper.getParameterValues(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_APPLICABILITA_AZIONI);
			String pattern = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_APPLICABILITA_PATTERN);
			String contentType = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_APPLICABILITA_CT);
			
			String idTrasformazioneS = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_TRASFORMAZIONE);
			long idTrasformazione = Long.parseLong(idTrasformazioneS);
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFruizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);

			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);

			// Prendo nome della porta applicativa
			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String nomePorta = portaDelegata.getNome();
			
			Trasformazioni trasformazioni = portaDelegata.getTrasformazioni();
			TrasformazioneRegola oldRegola = null;
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					oldRegola = reg;
					break;
				}
			}
			if(oldRegola==null) {
				throw new Exception("TrasformazioneRegola con id '"+idTrasformazione+"' non trovata");
			}
			
			String nomeTrasformazione = oldRegola.getNome();
			Parameter pIdTrasformazione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_TRASFORMAZIONE, idTrasformazioneS);
			
			// parametri visualizzazione link
			String servletTrasformazioniRichiesta = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA;
			String servletTrasformazioniRispostaList = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_LIST;
			int numeroTrasformazioniRisposte = oldRegola.sizeRispostaList();
			
			List<Parameter> parametriInvocazioneServletTrasformazioniRichiesta = new ArrayList<>();
			parametriInvocazioneServletTrasformazioniRichiesta.add(pId);
			parametriInvocazioneServletTrasformazioniRichiesta.add(pIdSoggetto);
			parametriInvocazioneServletTrasformazioniRichiesta.add(pIdAsps);
			parametriInvocazioneServletTrasformazioniRichiesta.add(pIdFruizione);
			parametriInvocazioneServletTrasformazioniRichiesta.add(pIdTrasformazione);
			List<Parameter> parametriInvocazioneServletTrasformazioniRisposta = new ArrayList<>();
			parametriInvocazioneServletTrasformazioniRisposta.add(pId);
			parametriInvocazioneServletTrasformazioniRisposta.add(pIdSoggetto);
			parametriInvocazioneServletTrasformazioniRisposta.add(pIdAsps);
			parametriInvocazioneServletTrasformazioniRisposta.add(pIdFruizione);
			parametriInvocazioneServletTrasformazioniRisposta.add(pIdTrasformazione);
			
			String servletTrasformazioniAutorizzazioneAutenticati =  PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_LIST;
			List<Parameter> parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati = new ArrayList<>();
			parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.add(pId);
			parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.add(pIdSoggetto);
			parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.add(pIdAsps);
			parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.add(pIdFruizione);
			parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.add(pIdTrasformazione);
			int numAutenticati = oldRegola.getApplicabilita() != null ? oldRegola.getApplicabilita().sizeServizioApplicativoList() : 0;
			String servletTrasformazioniApplicativiAutenticati = null;
			List<Parameter> parametriInvocazioneServletTrasformazioniApplicativiAutenticati  = null;
			int numApplicativiAutenticati = 0;
			
			List<TrasformazioneRegolaApplicabilitaServizioApplicativo> applicabilitaApplicativi = oldRegola.getApplicabilita() != null ? oldRegola.getApplicabilita().getServizioApplicativoList() : null;
			
 			MappingFruizionePortaDelegata mappingAssociatoPorta = porteDelegateCore.getMappingFruizionePortaDelegata(portaDelegata);
			
			String[] azioniDisponibiliList = null;
			String[] azioniDisponibiliLabelList = null;
			
			List<String> azioniAssociatePorta = new ArrayList<>();
			if(portaDelegata.getAzione() != null && portaDelegata.getAzione().getAzioneDelegataList() != null)
				azioniAssociatePorta.addAll(portaDelegata.getAzione().getAzioneDelegataList());

			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(apc.getServiceBinding());
			Map<String,String> azioniAccordo = porteDelegateCore.getAzioniConLabel(asps, apc, false, true, new ArrayList<>());
			
			if(azioniAccordo!=null && azioniAccordo.size()>0) {
				// porte ridefinite
				if(!mappingAssociatoPorta.isDefault()) {
					
					azioniDisponibiliList = new String[azioniAssociatePorta.size()];
					azioniDisponibiliLabelList = new String[azioniAssociatePorta.size()];
					int i = 0;
					for (String string : azioniAccordo.keySet()) {
						if(azioniAssociatePorta.contains(string)) {
							azioniDisponibiliList[i] = string;
							azioniDisponibiliLabelList[i] = azioniAccordo.get(string);
							i++;
						}
					}
				} else { // elenco azioni non associate a nessun mapping
					IDServizio idServizioFromAccordo = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
					IDSoggetto idSoggettoFruitore = new IDSoggetto();
					String tipoSoggettoFruitore = null;
					String nomeSoggettoFruitore = null;
					if(apsCore.isRegistroServiziLocale()){
						org.openspcoop2.core.registry.Soggetto soggettoFruitore = soggettiCore.getSoggettoRegistro(Integer.parseInt(idsogg));
						tipoSoggettoFruitore = soggettoFruitore.getTipo();
						nomeSoggettoFruitore = soggettoFruitore.getNome();
					}else{
						org.openspcoop2.core.config.Soggetto soggettoFruitore = soggettiCore.getSoggetto(Integer.parseInt(idsogg));
						tipoSoggettoFruitore = soggettoFruitore.getTipo();
						nomeSoggettoFruitore = soggettoFruitore.getNome();
					}
					idSoggettoFruitore.setTipo(tipoSoggettoFruitore);
					idSoggettoFruitore.setNome(nomeSoggettoFruitore);
					
					List<MappingFruizionePortaDelegata> listaMappingErogazione = apsCore.serviziFruitoriMappingList(idFru, idSoggettoFruitore , idServizioFromAccordo, null);
					List<String> azioniOccupate = new ArrayList<>();
					int listaMappingErogazioneSize = listaMappingErogazione != null ? listaMappingErogazione.size() : 0;
					if(listaMappingErogazioneSize > 0) {
						for (int i = 0; i < listaMappingErogazione.size(); i++) {
							MappingFruizionePortaDelegata mappingErogazionePortaDelegata = listaMappingErogazione.get(i);
							// colleziono le azioni gia' configurate
							PortaDelegata portaDelegataTmp = porteDelegateCore.getPortaDelegata(mappingErogazionePortaDelegata.getIdPortaDelegata());
							if(portaDelegataTmp.getAzione() != null && portaDelegataTmp.getAzione().getAzioneDelegataList() != null)
								azioniOccupate.addAll(portaDelegataTmp.getAzione().getAzioneDelegataList());
						}
					}
					Map<String,String> azioniAccordoDisponibili = porteDelegateCore.getAzioniConLabel(asps, apc, false, true, azioniOccupate);
					
					azioniDisponibiliList = new String[azioniAccordoDisponibili.size()];
					azioniDisponibiliLabelList = new String[azioniAccordoDisponibili.size()];
					int i = 0;
					for (String string : azioniAccordoDisponibili.keySet()) {
						azioniDisponibiliList[i] = string;
						azioniDisponibiliLabelList[i] = azioniAccordoDisponibili.get(string);
						i++;
					}
				}
			}

			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI,
						portaDelegata);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_DI+nomePorta;
			}

			lstParam.add(new Parameter(labelPerPorta, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_LIST, pId, pIdSoggetto, pIdAsps, pIdFruizione));
			
			lstParam.add(new Parameter(nomeTrasformazione, null));

			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteDelegateHelper.isEditModeInProgress()) {
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				// primo accesso
				if(first == null) {
					
					nome = oldRegola.getNome();
					
					stato = DriverConfigurazioneDBLib.getValue(oldRegola.getStato());
					
					TrasformazioneRegolaApplicabilitaRichiesta applicabilita = oldRegola.getApplicabilita();
					if(applicabilita != null) {
						pattern = applicabilita.getPattern();
						
						if(applicabilita.getAzioneList() != null) {
							azioni = applicabilita.getAzioneList() .toArray(new String[applicabilita.sizeAzioneList()]);
						} else {
							azioni = new String[0];
						}
						contentType = applicabilita.getContentTypeList() != null ? StringUtils.join(applicabilita.getContentTypeList(), ",") : "";  
					}
					
					azioniAll = (azioni==null || azioni.length<=0);
				}

				dati = porteDelegateHelper.addTrasformazioneToDati(TipoOperazione.CHANGE, dati, portaDelegata, idTrasformazioneS, nome, 
						stato, azioniAll, azioniDisponibiliList, azioniDisponibiliLabelList, azioni, pattern, contentType,
						null, null, null,
						apc.getServiceBinding(),
						servletTrasformazioniRichiesta, parametriInvocazioneServletTrasformazioniRichiesta, servletTrasformazioniRispostaList, parametriInvocazioneServletTrasformazioniRisposta, numeroTrasformazioniRisposte,
						true,
						servletTrasformazioniAutorizzazioneAutenticati, parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati , numAutenticati,
						servletTrasformazioniApplicativiAutenticati,  parametriInvocazioneServletTrasformazioniApplicativiAutenticati , numApplicativiAutenticati);
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, idsogg, null, idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI,	ForwardParams.CHANGE());
			}
			
			// quando un parametro viene inviato come vuoto, sul db viene messo null, gestisco il caso
			String azioniAsString = azioni != null ? StringUtils.join(Arrays.asList(azioni), ",") : "";
			String patternDBCheck = StringUtils.isNotEmpty(pattern) ? pattern : null;
			String contentTypeDBCheck = StringUtils.isNotEmpty(contentType) ? contentType : null;
			String azioniDBCheck = StringUtils.isNotEmpty(azioniAsString) ? azioniAsString : null;
			TrasformazioneRegola trasformazioneDBCheckCriteri = porteDelegateCore.getTrasformazione(Long.parseLong(id), azioniDBCheck, patternDBCheck, contentTypeDBCheck, null,
					applicabilitaApplicativi);
			TrasformazioneRegola trasformazioneDBCheckNome = porteDelegateCore.getTrasformazione(Long.parseLong(id), nome);
			
			boolean isOk = porteDelegateHelper.trasformazioniCheckData(TipoOperazione.CHANGE, Long.parseLong(id), nome, trasformazioneDBCheckCriteri, trasformazioneDBCheckNome, oldRegola,
					serviceBinding);
			if (!isOk) {

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteDelegateHelper.addTrasformazioneToDati(TipoOperazione.CHANGE, dati, portaDelegata, idTrasformazioneS, nome, 
						stato, azioniAll, azioniDisponibiliList, azioniDisponibiliLabelList, azioni, pattern, contentType,
						null, null, null,
						apc.getServiceBinding(),
						servletTrasformazioniRichiesta, parametriInvocazioneServletTrasformazioniRichiesta, servletTrasformazioniRispostaList, parametriInvocazioneServletTrasformazioniRisposta, numeroTrasformazioniRisposte,
						true,
						servletTrasformazioniAutorizzazioneAutenticati, parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati , numAutenticati,
						servletTrasformazioniApplicativiAutenticati,  parametriInvocazioneServletTrasformazioniApplicativiAutenticati , numApplicativiAutenticati);
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, idsogg, null, idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI,	ForwardParams.CHANGE());
			}
			
			// aggiorno la regola
			trasformazioni = portaDelegata.getTrasformazioni();
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					
					reg.setNome(nome);
					
					reg.setStato(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(stato));
					
					if(reg.getApplicabilita() == null)
						reg.setApplicabilita(new TrasformazioneRegolaApplicabilitaRichiesta());
					
					reg.getApplicabilita().setPattern(pattern);
					reg.getApplicabilita().getContentTypeList().clear();
					if(contentType != null) {
						reg.getApplicabilita().getContentTypeList().addAll(Arrays.asList(contentType.split(",")));
					}
					
					reg.getApplicabilita().getAzioneList().clear();
					if(azioni != null && azioni.length > 0) {
						for (String azione : azioni) {
							reg.getApplicabilita().addAzione(azione);
						}
					}
					break;
				}
			}
			
			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
			
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			
			int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI; 
			
			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
			
			List<TrasformazioneRegola> lista = porteDelegateCore.porteDelegateTrasformazioniList(Long.parseLong(id), ricerca);
			
			porteDelegateHelper.preparePorteDelegateTrasformazioniRegolaList(ricerca, lista);
						
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI,	ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI, 
					ForwardParams.CHANGE());
		}
	}
}
