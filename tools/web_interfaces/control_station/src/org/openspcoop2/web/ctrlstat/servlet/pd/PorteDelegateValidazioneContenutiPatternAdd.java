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
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiPatternRegola;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiRichiesta;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiRisposta;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteDelegateValidazioneContenutiPatternAdd
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteDelegateValidazioneContenutiPatternAdd extends Action {

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
			
			String idRegolaS = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_ID_PATTERN);
			String nome = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_NOME);
			String regola = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_REGOLE);
			String patternAndS = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_AND);
			String patternNotS = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_NOT); 
			
			String idParent = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_ID_RISPOSTA);
			String tipoParent = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT);
			
			if(tipoParent == null) {
				tipoParent = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_PORTA;
			}
			
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFruizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
			Parameter pIdParent = new Parameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_ID_RISPOSTA, idParent);
			Parameter pIdPatternParent = new Parameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT, tipoParent);

			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo nome della porta applicativa
			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String nomePorta = portaDelegata.getNome();

			
			// setto il titolo
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
			
			if(!"".equals(tipoParent)) {
				if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RICHIESTA)) {
					lstParam.add(new Parameter(CostantiControlStation.LABEL_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTE,  PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RICHIESTA_LIST,
							pId, pIdSoggetto, pIdAsps, pIdFruizione));
					
					ValidazioneContenutiApplicativiRichiesta oldRichiesta = null;
					for (int j = 0; j < portaDelegata.getValidazioneContenutiApplicativi().sizeRichiestaList(); j++) {
						ValidazioneContenutiApplicativiRichiesta richiesta = portaDelegata.getValidazioneContenutiApplicativi().getRichiesta(j);
						if (richiesta.getId().longValue() == Long.parseLong(idParent)) {
							oldRichiesta = richiesta;
							break;
						}
					}
					
					lstParam.add(new Parameter(oldRichiesta.getNome(), PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RICHIESTA_CHANGE,
							pId, pIdSoggetto, pIdAsps, pIdFruizione, pIdParent));
					
				} else if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA)) {
					lstParam.add(new Parameter(CostantiControlStation.LABEL_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTE, 
							PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RISPOSTA_LIST,
							pId, pIdSoggetto, pIdAsps, pIdFruizione));
					
					ValidazioneContenutiApplicativiRisposta oldRisposta = null;
					for (int j = 0; j < portaDelegata.getValidazioneContenutiApplicativi().sizeRispostaList(); j++) {
						ValidazioneContenutiApplicativiRisposta risposta = portaDelegata.getValidazioneContenutiApplicativi().getRisposta(j);
						if (risposta.getId().longValue() == Long.parseLong(idParent)) {
							oldRisposta = risposta;
							break;
						}
					}
					
					lstParam.add(new Parameter(oldRisposta.getNome(), PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RISPOSTA_CHANGE,
							pId, pIdSoggetto, pIdAsps, pIdFruizione, pIdParent));
				}
			}
			
			lstParam.add(new Parameter(CostantiControlStation.LABEL_PATTERN,  PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_PATTERN_LIST,
					pId, pIdSoggetto, pIdAsps, pIdFruizione, pIdParent, pIdPatternParent));
			
			
			lstParam.add(ServletUtils.getParameterAggiungi());

			ServletUtils.setPageDataTitle(pd, lstParam);

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteDelegateHelper.isEditModeInProgress()) {
				
				if(nome == null) {
					nome = "";
					regola = "";
					patternAndS = Costanti.CHECK_BOX_ENABLED;
					patternNotS = Costanti.CHECK_BOX_DISABLED;
				}
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteDelegateHelper.addValidazioneContenutiPatternToDati(dati, TipoOperazione.ADD, null, idRegolaS, nome, regola, patternAndS, patternNotS);
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, id, idsogg, null, idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				dati = porteDelegateHelper.addHiddenFieldsPatternParentToDati(TipoOperazione.ADD, idParent, tipoParent, dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_PATTERN, ForwardParams.ADD());
			}
			
			boolean isOk = porteDelegateHelper.validazioneContenutiPatternCheckData(TipoOperazione.ADD, null, true, portaDelegata.getId());
			if (!isOk) {

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteDelegateHelper.addValidazioneContenutiPatternToDati(dati, TipoOperazione.ADD, null, idRegolaS, nome, regola, patternAndS, patternNotS);
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, id, idsogg, null, idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				dati = porteDelegateHelper.addHiddenFieldsPatternParentToDati(TipoOperazione.ADD, idParent, tipoParent, dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_PATTERN,	ForwardParams.ADD());
			}
			
			// salvataggio nuova regola
			ValidazioneContenutiApplicativiPatternRegola pattern = new ValidazioneContenutiApplicativiPatternRegola();
			pattern.setNome(nome);
			pattern.setRegola(regola);
			pattern.setAnd(ServletUtils.isCheckBoxEnabled(patternAndS));
			pattern.setNot(ServletUtils.isCheckBoxEnabled(patternNotS));
			
			String parentName = null;
			if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RICHIESTA)) {
				for (int j = 0; j < portaDelegata.getValidazioneContenutiApplicativi().sizeRichiestaList(); j++) {
					ValidazioneContenutiApplicativiRichiesta richiesta = portaDelegata.getValidazioneContenutiApplicativi().getRichiesta(j);
					if (richiesta.getId().longValue() == Long.parseLong(idParent)) {
						parentName = richiesta.getNome();
						richiesta.getConfigurazione().getConfigurazionePattern().addPattern(pattern);
						break;
					}
				}
			} else if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA)) {
				for (int j = 0; j < portaDelegata.getValidazioneContenutiApplicativi().sizeRispostaList(); j++) {
					ValidazioneContenutiApplicativiRisposta risposta = portaDelegata.getValidazioneContenutiApplicativi().getRisposta(j);
					if (risposta.getId().longValue() == Long.parseLong(idParent)) {
						parentName = risposta.getNome();
						risposta.getConfigurazione().getConfigurazionePattern().addPattern(pattern);
						break;
					}
				}
			} else {
				portaDelegata.getValidazioneContenutiApplicativi().getConfigurazione().getConfigurazionePattern().addPattern(pattern);
			}
			
			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
			
			portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			
			// ricarico parent
			if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RICHIESTA)) {
				for (int j = 0; j < portaDelegata.getValidazioneContenutiApplicativi().sizeRichiestaList(); j++) {
					ValidazioneContenutiApplicativiRichiesta richiesta = portaDelegata.getValidazioneContenutiApplicativi().getRichiesta(j);
					if (richiesta.getNome().equals(parentName)) {
						idParent = richiesta.getId()+"";
						break;
					}
				}
			} else if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA)) {
				for (int j = 0; j < portaDelegata.getValidazioneContenutiApplicativi().sizeRispostaList(); j++) {
					ValidazioneContenutiApplicativiRisposta risposta = portaDelegata.getValidazioneContenutiApplicativi().getRisposta(j);
					if (risposta.getNome().equals(parentName)) {
						idParent = risposta.getId()+"";
						break;
					}
				}
			}  
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_PATTERN; 
			
			if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RICHIESTA)) {
				idLista = Liste.PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RICHIESTA_PATTERN;
			} else if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA)) {
				idLista = Liste.PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RISPOSTA_PATTERN;
			}
			
			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
			
			List<ValidazioneContenutiApplicativiPatternRegola> lista = null;
			if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RICHIESTA)) {
				lista = porteDelegateCore.listaPatternValidazioneContenutiRichiesta(Long.parseLong(idParent), ricerca);
			} else if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA)) {
				lista = porteDelegateCore.listaPatternValidazioneContenutiRisposta(Long.parseLong(idParent), ricerca);
			} else {
				lista = porteDelegateCore.listaPatternValidazioneContenuti(Long.parseLong(id), ricerca);
			}
			
			porteDelegateHelper.preparePorteDelegateValidazioneContenutiPatternList(ricerca, lista, idParent, tipoParent);
						
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_PATTERN, ForwardParams.ADD());


		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_PATTERN, ForwardParams.ADD());
		} 
	}
}
