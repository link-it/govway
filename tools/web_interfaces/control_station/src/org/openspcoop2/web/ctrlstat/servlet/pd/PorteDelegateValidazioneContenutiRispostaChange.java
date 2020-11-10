/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import java.util.Vector;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiPattern;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiRisposta;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiRispostaApplicabilita;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiStato;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
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
 * PorteDelegateValidazioneContenutiRispostaChange
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteDelegateValidazioneContenutiRispostaChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);
		
		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session);
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

		try {
			
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			Long idFru = Long.parseLong(idFruizione);
			
			String idRispostaS = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_ID_RISPOSTA);
			Long idRisposta = Long.parseLong(idRispostaS);
			String nome = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_NOME);
			// configurazione stato (ereditata da validazionecontenuti)
			String statoValidazione = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_STATO);
			String tipoValidazione = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_TIPO);
			String applicaMTOM = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_MTOM);

			String jsonSchema = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_JSON_SCHEMA);
			
			String patternAndS = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_AND);
			String patternNotS = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_NOT); 
			// configurazione applicabilita (ereditata dalle trasformazioni)
			String azioniAllTmp = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_APPLICABILITA_AZIONI_ALL);
			boolean azioniAll = azioniAllTmp==null || "".equals(azioniAllTmp) || CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUE_TRUE.equals(azioniAllTmp);
			String [] azioni = porteDelegateHelper.getParameterValues(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_APPLICABILITA_AZIONI);
			String pattern = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_APPLICABILITA_PATTERN);
			String contentType = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTA_APPLICABILITA_CT);
			
			String returnCode = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_APPLICABILITA_STATUS);
			if(returnCode == null)
				returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI;
			
			String statusMin = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_APPLICABILITA_STATUS_MIN);
			if(statusMin == null)
				statusMin = "";
			String statusMax = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_APPLICABILITA_STATUS_MAX);
			if(statusMax == null)
				statusMax = "";
			String emptyResponse = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_APPLICABILITA_EMPTY_RESPONSE);
			String problemDetail = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_APPLICABILITA_PROBLEM_DETAIL);
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFruizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
			Parameter pIdRisposta = new Parameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_ID_RISPOSTA, idRispostaS);
			Parameter pIdPatternParent = new Parameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT,
					CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA);

			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo nome della porta applicativa
			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String nomePorta = portaDelegata.getNome();
			
			ValidazioneContenutiApplicativiRisposta oldRisposta = null;
			for (int j = 0; j < portaDelegata.getValidazioneContenutiApplicativi().sizeRispostaList(); j++) {
				ValidazioneContenutiApplicativiRisposta risposta = portaDelegata.getValidazioneContenutiApplicativi().getRisposta(j);
				if (risposta.getId().longValue() == idRisposta.longValue()) {
					oldRisposta = risposta;
					break;
				}
			}
			
			ValidazioneContenutiApplicativiStato validazioneContenutiApplicativiStato = oldRisposta != null ? oldRisposta.getConfigurazione() : null; 
			ValidazioneContenutiApplicativiRispostaApplicabilita validazioneContenutiApplicativiApplicabilita = oldRisposta != null ? oldRisposta.getApplicabilita() : null; 
			
			MappingFruizionePortaDelegata mappingAssociatoPorta = porteDelegateCore.getMappingFruizionePortaDelegata(portaDelegata);
			
			String[] azioniDisponibiliList = null;
			String[] azioniDisponibiliLabelList = null;
			
			
			List<String> azioniAssociatePorta = new ArrayList<>();
			if(portaDelegata.getAzione() != null && portaDelegata.getAzione().getAzioneDelegataList() != null)
				azioniAssociatePorta.addAll(portaDelegata.getAzione().getAzioneDelegataList());

			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			ServiceBinding serviceBinding = ServiceBinding.valueOf(apc.getServiceBinding().name());
			Map<String,String> azioniAccordo = porteDelegateCore.getAzioniConLabel(asps, apc, false, true, new ArrayList<String>());
			
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
					
					List<MappingFruizionePortaDelegata> listaMappingFruizione = apsCore.serviziFruitoriMappingList(idFru, idSoggettoFruitore , idServizioFromAccordo, null);
					List<String> azioniOccupate = new ArrayList<>();
					int listaMappingErogazioneSize = listaMappingFruizione != null ? listaMappingFruizione.size() : 0;
					if(listaMappingErogazioneSize > 0) {
						for (int i = 0; i < listaMappingFruizione.size(); i++) {
							MappingFruizionePortaDelegata mappingErogazionePortaDelegata = listaMappingFruizione.get(i);
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
			
			boolean tipoValidazioneJsonEnabled = false;
			boolean visualizzaLinkPattern = validazioneContenutiApplicativiStato != null ? validazioneContenutiApplicativiStato.getTipo().equals(ValidazioneContenutiApplicativiTipo.PATTERN) : false;
			String servletPatternList = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_PATTERN_LIST;
			List<Parameter> paramsPatternList = new ArrayList<Parameter>();
			paramsPatternList.add(pId);
			paramsPatternList.add(pIdSoggetto);
			paramsPatternList.add(pIdAsps);
			paramsPatternList.add(pIdFruizione);
			paramsPatternList.add(pIdRisposta);
			paramsPatternList.add(pIdPatternParent);
			List<String> listaJsonSchema = new ArrayList<String>();
			int numeroPattern = porteDelegateCore.numeroPatternValidazioneContenutiRisposta(idRisposta);
			
			String postBackElementName = porteDelegateHelper.getPostBackElementName();
			
			// se ho modificato il soggetto ricalcolo il servizio e il service binding
			if (postBackElementName != null) {
				if(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_TIPO.equals(postBackElementName)) {
					if (tipoValidazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_JSON)) { 
						jsonSchema = null;
					}
					
					if (tipoValidazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_PATTERN)) {
						patternAndS = null;
						patternNotS = null;
					}
				}
				
				if(postBackElementName.equals(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_APPLICABILITA_STATUS)) {
					statusMin = "";
					statusMax = "";
				}
			}
			
			if(serviceBinding.equals(ServiceBinding.REST)) {
				ISearch ricerca = new Search();
				ricerca.addFilter(Liste.ACCORDI_ALLEGATI, Filtri.FILTRO_RUOLO_DOCUMENTO, RuoliDocumento.specificaSemiformale.toString());
				// ricaricare la lista dei json schema
				List<Documento> accordiAllegatiList = apcCore.accordiAllegatiList(apc.getId(), ricerca); 
				if(accordiAllegatiList != null && accordiAllegatiList.size() > 0) {
					tipoValidazioneJsonEnabled = true;
					listaJsonSchema = accordiAllegatiList.stream().map(doc -> doc.getFile()).collect(Collectors.toList());
				}
			}

			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI,
						portaDelegata);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_CONFIG_DI+nomePorta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI,  pId, pIdSoggetto, pIdAsps, pIdFruizione));
			lstParam.add(new Parameter(CostantiControlStation.LABEL_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTE,  PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RISPOSTA_LIST,
					pId, pIdSoggetto, pIdAsps, pIdFruizione));
			
			lstParam.add(new Parameter(oldRisposta.getNome(), null));

			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteDelegateHelper.isEditModeInProgress()) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				// primo accesso
				if(nome == null) {
					nome = oldRisposta.getNome();
					if (statoValidazione == null) {
						if (validazioneContenutiApplicativiStato == null) {
							statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
						} else {
							if(validazioneContenutiApplicativiStato.getStato()!=null)
								statoValidazione = validazioneContenutiApplicativiStato.getStato().toString();
							if ((statoValidazione == null) || "".equals(statoValidazione)) {
								statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
							}
						}
					}
					
					if (tipoValidazione == null) {
						if (validazioneContenutiApplicativiStato == null) {
							tipoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_INTERFACE;
						} else {
							if(validazioneContenutiApplicativiStato.getTipo()!=null && !StatoFunzionalitaConWarning.DISABILITATO.equals(validazioneContenutiApplicativiStato.getStato()))
								tipoValidazione = validazioneContenutiApplicativiStato.getTipo().toString();
							if (tipoValidazione == null || "".equals(tipoValidazione)) {
								tipoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_INTERFACE ;
							}
						}
					}
					if (applicaMTOM == null) {
						applicaMTOM = "";
						if (validazioneContenutiApplicativiStato != null) {
							if(validazioneContenutiApplicativiStato.getAcceptMtomMessage()!=null)
								if (validazioneContenutiApplicativiStato.getAcceptMtomMessage().equals(StatoFunzionalita.ABILITATO)) 
									applicaMTOM = Costanti.CHECK_BOX_ENABLED;
						}
					}
					
					if(jsonSchema == null) {
						jsonSchema = "";
						if (validazioneContenutiApplicativiStato != null) {
							jsonSchema = validazioneContenutiApplicativiStato.getJsonSchema();
						}
					}
					
//					if(soapAction == null) {
//						if (validazioneContenutiApplicativiStato == null) {
//							soapAction = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_ABILITATO;
//						} else {
//							if(validazioneContenutiApplicativiStato.getSoapAction()!=null)
//								soapAction = validazioneContenutiApplicativiStato.getSoapAction().toString();
//							if ((soapAction == null) || "".equals(soapAction)) {
//								soapAction = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_ABILITATO;
//							}
//						}
//					}
					
					if(patternAndS == null) {
						if (validazioneContenutiApplicativiStato == null) {
							patternAndS = Costanti.CHECK_BOX_ENABLED;
						} else {
							ValidazioneContenutiApplicativiPattern configurazionePattern = validazioneContenutiApplicativiStato.getConfigurazionePattern();
							if(configurazionePattern != null) {
								patternAndS = ServletUtils.boolToCheckBoxStatus(configurazionePattern.getAnd());
							} else {
								patternAndS = Costanti.CHECK_BOX_ENABLED;
							}
						}
					}
					
					if(patternNotS == null) {
						if (validazioneContenutiApplicativiStato == null) {
							patternNotS = Costanti.CHECK_BOX_DISABLED;
						} else {
							ValidazioneContenutiApplicativiPattern configurazionePattern = validazioneContenutiApplicativiStato.getConfigurazionePattern();
							if(configurazionePattern != null) {
								patternNotS = ServletUtils.boolToCheckBoxStatus(configurazionePattern.getNot());
							} else {
								patternNotS = Costanti.CHECK_BOX_DISABLED;
							}
						}
					}
					
					if(validazioneContenutiApplicativiApplicabilita != null) {
						pattern = validazioneContenutiApplicativiApplicabilita.getMatch();
						
						if(validazioneContenutiApplicativiApplicabilita.getAzioneList() != null) {
							azioni = validazioneContenutiApplicativiApplicabilita.getAzioneList() .toArray(new String[validazioneContenutiApplicativiApplicabilita.sizeAzioneList()]);
						} else {
							azioni = new String[0];
						}
						contentType = validazioneContenutiApplicativiApplicabilita.getContentTypeList() != null ? StringUtils.join(validazioneContenutiApplicativiApplicabilita.getContentTypeList(), ",") : "";  
					}
					
					azioniAll = (azioni==null || azioni.length<=0);
					
					Integer statusMinInteger = validazioneContenutiApplicativiApplicabilita != null ? validazioneContenutiApplicativiApplicabilita.getReturnCodeMin() : null;
					Integer statusMaxInteger = validazioneContenutiApplicativiApplicabilita != null ? validazioneContenutiApplicativiApplicabilita.getReturnCodeMax() : null;
					
					if(statusMinInteger != null) {
						statusMin = statusMinInteger +"";
					}
					
					if(statusMaxInteger != null) {
						statusMax = statusMaxInteger +"";
					}
					
					// se e' stato salvato il valore 0 lo tratto come null
					if(statusMinInteger != null && statusMinInteger.intValue() <= 0) {
						statusMinInteger = null;
					}
					
					if(statusMaxInteger != null && statusMaxInteger.intValue() <= 0) {
						statusMaxInteger = null;
					}
					
					// Intervallo
					if(statusMinInteger != null && statusMaxInteger != null) {
						if(statusMaxInteger.longValue() == statusMinInteger.longValue()) // esatto
							returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO;
						else 
							returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_INTERVALLO;
					} else if(statusMinInteger != null && statusMaxInteger == null) { // definito solo l'estremo inferiore
						returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_INTERVALLO;
					} else if(statusMinInteger == null && statusMaxInteger != null) { // definito solo l'estremo superiore
						returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_INTERVALLO;
					} else { //entrambi null 
						returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI;
					}
					
					if (emptyResponse == null) {
						if (validazioneContenutiApplicativiApplicabilita == null) {
							emptyResponse = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
						} else {
							if(validazioneContenutiApplicativiApplicabilita.getRestEmptyResponse()!=null)
								emptyResponse = validazioneContenutiApplicativiApplicabilita.getRestEmptyResponse().toString();
							if ((emptyResponse == null) || "".equals(emptyResponse)) {
								emptyResponse = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
							}
						}
					}
					
					if (problemDetail == null) {
						if (validazioneContenutiApplicativiApplicabilita == null) {
							problemDetail = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
						} else {
							if(validazioneContenutiApplicativiApplicabilita.getRestProblemDetail()!=null)
								problemDetail = validazioneContenutiApplicativiApplicabilita.getRestProblemDetail().toString();
							if ((problemDetail == null) || "".equals(problemDetail)) {
								problemDetail = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
							}
						}
					}
				}

				dati = porteDelegateHelper.addValidazioneContenutiRispostaToDati(dati, TipoOperazione.CHANGE, oldRisposta.getId()+"", 
						idRispostaS, nome, true, statoValidazione, tipoValidazione, applicaMTOM, 
						serviceBinding, apc.getFormatoSpecifica(), tipoValidazioneJsonEnabled, jsonSchema, listaJsonSchema, 
						patternAndS, patternNotS, numeroPattern, servletPatternList, paramsPatternList, visualizzaLinkPattern, 
						azioniAll, azioniDisponibiliList, azioniDisponibiliLabelList, azioniDisponibiliLabelList, pattern, contentType, 
						returnCode, statusMin, statusMax, problemDetail, emptyResponse);
						
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, idsogg, null, idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RISPOSTA,	ForwardParams.CHANGE());
			}
			
			boolean isOk = porteDelegateHelper.validazioneContenutiRispostaCheckData(TipoOperazione.CHANGE, oldRisposta, true, portaDelegata.getId());
			if (!isOk) {

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteDelegateHelper.addValidazioneContenutiRispostaToDati(dati, TipoOperazione.CHANGE, oldRisposta.getId()+"", 
						idRispostaS, nome, true, statoValidazione, tipoValidazione, applicaMTOM, 
						serviceBinding, apc.getFormatoSpecifica(), tipoValidazioneJsonEnabled, jsonSchema, listaJsonSchema, 
						patternAndS, patternNotS, numeroPattern, servletPatternList, paramsPatternList, visualizzaLinkPattern, 
						azioniAll, azioniDisponibiliList, azioniDisponibiliLabelList, azioniDisponibiliLabelList, pattern, contentType, 
						returnCode, statusMin, statusMax, problemDetail, emptyResponse); 
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, idsogg, null, idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RISPOSTA,	ForwardParams.CHANGE());
			}
			
			// aggiorno la regola
			for (int j = 0; j < portaDelegata.getValidazioneContenutiApplicativi().sizeRispostaList(); j++) {
				ValidazioneContenutiApplicativiRisposta risposta = portaDelegata.getValidazioneContenutiApplicativi().getRisposta(j);
				if (risposta.getId().longValue() == idRisposta.longValue()) {
					risposta.setNome(nome);
					
					// stato
					ValidazioneContenutiApplicativiStato vxStato = new ValidazioneContenutiApplicativiStato();
					
					vxStato.setStato(StatoFunzionalitaConWarning.toEnumConstant(statoValidazione));
					ValidazioneContenutiApplicativiTipo validazioneContenutiApplicativiTipo = ValidazioneContenutiApplicativiTipo.toEnumConstant(tipoValidazione);
					vxStato.setTipo(validazioneContenutiApplicativiTipo);
					if(applicaMTOM != null){
						if(ServletUtils.isCheckBoxEnabled(applicaMTOM))
							vxStato.setAcceptMtomMessage(StatoFunzionalita.ABILITATO);
						else 
							vxStato.setAcceptMtomMessage(StatoFunzionalita.DISABILITATO);
					} else 
						vxStato.setAcceptMtomMessage(null);
					
					if(validazioneContenutiApplicativiTipo != null) {
						switch (validazioneContenutiApplicativiTipo) {
						case INTERFACE:
						case OPENSPCOOP:
							if(serviceBinding.equals(ServiceBinding.SOAP)) {
							//	vxStato.setSoapAction(StatoFunzionalita.toEnumConstant(soapAction));
							}
							break;
						case JSON:
							if(serviceBinding.equals(ServiceBinding.REST)) {
								vxStato.setJsonSchema(jsonSchema);
							}
							break;
						case PATTERN:
							if(vxStato.getConfigurazionePattern() == null)
								vxStato.setConfigurazionePattern(new ValidazioneContenutiApplicativiPattern());
							
							vxStato.getConfigurazionePattern().setAnd(ServletUtils.isCheckBoxEnabled(patternAndS));
							vxStato.getConfigurazionePattern().setNot(ServletUtils.isCheckBoxEnabled(patternNotS));
							break;
						case XSD:
						default:
							break;
						}
					}
					
					risposta.setConfigurazione(vxStato);
					
					// applicabilita
					ValidazioneContenutiApplicativiRispostaApplicabilita applicabilita = new ValidazioneContenutiApplicativiRispostaApplicabilita();
					
					applicabilita.setMatch(pattern);
					if(contentType != null) {
						applicabilita.getContentTypeList().addAll(Arrays.asList(contentType.split(",")));
					}
					
					if(azioni != null && azioni.length > 0) {
						for (String azione : azioni) {
							applicabilita.addAzione(azione);
						}
					}
					
					if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI)) {
						applicabilita.setReturnCodeMin(null);
						applicabilita.setReturnCodeMax(null);
					} else if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO)) {
						applicabilita.setReturnCodeMin(StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null);
						applicabilita.setReturnCodeMax(StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null);
					} else { // intervallo
						applicabilita.setReturnCodeMin(StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null);
						applicabilita.setReturnCodeMax(StringUtils.isNotEmpty(statusMax) ? Integer.parseInt(statusMax) : null);
					}
					
					if(problemDetail != null) {
						applicabilita.setRestProblemDetail(StatoFunzionalita.toEnumConstant(problemDetail));
					}
					
					if(emptyResponse != null) {
						applicabilita.setRestEmptyResponse(StatoFunzionalita.toEnumConstant(emptyResponse));
					}
					
					risposta.setApplicabilita(applicabilita);
					break;
				}
			}
			
			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
			
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RISPOSTA; 
			
			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
			
			List<ValidazioneContenutiApplicativiRisposta> lista = porteDelegateCore.listaRisposteValidazioneContenuti(Long.parseLong(id), ricerca);
			
			porteDelegateHelper.preparePorteDelegateValidazioneContenutiRispostaList(ricerca, lista);  
						
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RISPOSTA,	ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RISPOSTA, 
					ForwardParams.CHANGE());
		}
	}
}
