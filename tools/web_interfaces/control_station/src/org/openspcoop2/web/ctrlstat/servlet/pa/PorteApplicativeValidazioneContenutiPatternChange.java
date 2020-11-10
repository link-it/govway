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
package org.openspcoop2.web.ctrlstat.servlet.pa;

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
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiPatternRegola;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiRichiesta;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiRisposta;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteApplicativeValidazioneContenutiPatternChange
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteApplicativeValidazioneContenutiPatternChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);
		
		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;

		try {
			
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			long idInt = Long.parseLong(idPorta);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			String idRegolaS = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_ID_PATTERN);
			Long idRegola = Long.parseLong(idRegolaS);
			String nome = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_NOME);
			String regola = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_REGOLE);
			String patternAndS = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_AND);
			String patternNotS = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_NOT); 
			
			String idParent = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_ID_RISPOSTA);
			String tipoParent = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT);
			
			if(tipoParent == null) {
				tipoParent = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_PORTA;
			}

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo nome della porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String nomePorta = pa.getNome();
			
			ValidazioneContenutiApplicativiPatternRegola oldPattern = null;
			if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RICHIESTA)) {
				for (int j = 0; j < pa.getValidazioneContenutiApplicativi().sizeRichiestaList(); j++) {
					ValidazioneContenutiApplicativiRichiesta richiesta = pa.getValidazioneContenutiApplicativi().getRichiesta(j);
					if (richiesta.getId().longValue() == Long.parseLong(idParent)) {
						for (int z = 0; z < richiesta.getConfigurazione().getConfigurazionePattern().sizePatternList(); z++) {
							ValidazioneContenutiApplicativiPatternRegola pattern = richiesta.getConfigurazione().getConfigurazionePattern().getPattern(z);
							if (pattern.getId().longValue() == idRegola.longValue()) {
								oldPattern = pattern;
								break;
							}
						}
						break;
					}
				}
			} else if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA)) {
				for (int j = 0; j < pa.getValidazioneContenutiApplicativi().sizeRispostaList(); j++) {
					ValidazioneContenutiApplicativiRisposta risposta = pa.getValidazioneContenutiApplicativi().getRisposta(j);
					if (risposta.getId().longValue() == Long.parseLong(idParent)) {
						for (int z = 0; z < risposta.getConfigurazione().getConfigurazionePattern().sizePatternList(); z++) {
							ValidazioneContenutiApplicativiPatternRegola pattern = risposta.getConfigurazione().getConfigurazionePattern().getPattern(z);
							if (pattern.getId().longValue() == idRegola.longValue()) {
								oldPattern = pattern;
								break;
							}
						}
						break;
					}
				}
			} else {
				for (int j = 0; j < pa.getValidazioneContenutiApplicativi().getConfigurazione().getConfigurazionePattern().sizePatternList(); j++) {
					ValidazioneContenutiApplicativiPatternRegola pattern = pa.getValidazioneContenutiApplicativi().getConfigurazione().getConfigurazionePattern().getPattern(j);
					if (pattern.getId().longValue() == idRegola.longValue()) {
						oldPattern = pattern;
						break;
					}
				}
			}
			
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_CONFIG_DI+nomePorta;
			}
			
			Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
			Parameter pIdSoggetto = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
			Parameter pIdParent = new Parameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_ID_RISPOSTA, idParent);
			Parameter pIdPatternParent = new Parameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT, tipoParent);
			
			lstParam.add(new Parameter(labelPerPorta,  PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, pIdSoggetto, pIdPorta, pIdAsps));
			
			if(!"".equals(tipoParent)) {
				if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RICHIESTA)) {
					lstParam.add(new Parameter(CostantiControlStation.LABEL_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RICHIESTE, 
							PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RICHIESTA_LIST, pIdSoggetto, pIdPorta, pIdAsps));
					
					ValidazioneContenutiApplicativiRichiesta oldRichiesta = null;
					for (int j = 0; j < pa.getValidazioneContenutiApplicativi().sizeRichiestaList(); j++) {
						ValidazioneContenutiApplicativiRichiesta richiesta = pa.getValidazioneContenutiApplicativi().getRichiesta(j);
						if (richiesta.getId().longValue() == Long.parseLong(idParent)) {
							oldRichiesta = richiesta;
							break;
						}
					}
					
					lstParam.add(new Parameter(oldRichiesta.getNome(), PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RICHIESTA_CHANGE,
							pIdSoggetto, pIdPorta, pIdAsps, pIdParent));
					
				} else if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA)) {
					lstParam.add(new Parameter(CostantiControlStation.LABEL_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTE,  
							PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RISPOSTA_LIST, pIdSoggetto, pIdPorta, pIdAsps));
					
					ValidazioneContenutiApplicativiRisposta oldRisposta = null;
					for (int j = 0; j < pa.getValidazioneContenutiApplicativi().sizeRispostaList(); j++) {
						ValidazioneContenutiApplicativiRisposta risposta = pa.getValidazioneContenutiApplicativi().getRisposta(j);
						if (risposta.getId().longValue() == Long.parseLong(idParent)) {
							oldRisposta = risposta;
							break;
						}
					}
					
					lstParam.add(new Parameter(oldRisposta.getNome(), PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RISPOSTA_CHANGE,
							pIdSoggetto, pIdPorta, pIdAsps, pIdParent));
				}
			}
			
			lstParam.add(new Parameter(CostantiControlStation.LABEL_PATTERN, 
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_PATTERN_LIST, pIdSoggetto, pIdPorta, pIdAsps, pIdParent, pIdPatternParent));
			lstParam.add(new Parameter(oldPattern.getNome(), null));

			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				// primo accesso
				if(nome == null) {
					nome = oldPattern.getNome();
					regola = oldPattern.getRegola();
					patternAndS = ServletUtils.boolToCheckBoxStatus(oldPattern.getAnd());
					patternNotS = ServletUtils.boolToCheckBoxStatus(oldPattern.getNot());
				}

				dati = porteApplicativeHelper.addValidazioneContenutiPatternToDati(dati, TipoOperazione.CHANGE, oldPattern, idRegolaS, nome, regola, patternAndS, patternNotS); 
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta,idAsps,  dati);
				
				dati = porteApplicativeHelper.addHiddenFieldsPatternParentToDati(TipoOperazione.CHANGE, idParent, tipoParent, dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_PATTERN,	ForwardParams.CHANGE());
			}
			
				
			boolean isOk = porteApplicativeHelper.validazioneContenutiPatternCheckData(TipoOperazione.CHANGE, oldPattern, false, pa.getId());
			if (!isOk) {

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addValidazioneContenutiPatternToDati(dati, TipoOperazione.CHANGE, oldPattern, idRegolaS, nome, regola, patternAndS, patternNotS);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta,idAsps,  dati);
				
				dati = porteApplicativeHelper.addHiddenFieldsPatternParentToDati(TipoOperazione.CHANGE, idParent, tipoParent, dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_PATTERN,	ForwardParams.CHANGE());
			}
			
			// aggiorno la regola
			String parentName = null;
			if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RICHIESTA)) {
				for (int j = 0; j < pa.getValidazioneContenutiApplicativi().sizeRichiestaList(); j++) {
					ValidazioneContenutiApplicativiRichiesta richiesta = pa.getValidazioneContenutiApplicativi().getRichiesta(j);
					if (richiesta.getId().longValue() == Long.parseLong(idParent)) {
						parentName = richiesta.getNome();
						for (int z = 0; z < richiesta.getConfigurazione().getConfigurazionePattern().sizePatternList(); z++) {
							ValidazioneContenutiApplicativiPatternRegola pattern = richiesta.getConfigurazione().getConfigurazionePattern().getPattern(z);
							if (pattern.getId().longValue() == idRegola.longValue()) {
								pattern.setNome(nome);
								pattern.setRegola(regola);
								pattern.setAnd(ServletUtils.isCheckBoxEnabled(patternAndS));
								pattern.setNot(ServletUtils.isCheckBoxEnabled(patternNotS));
								break;
							}
						}
						break;
					}
				}
			} else if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA)) {
				for (int j = 0; j < pa.getValidazioneContenutiApplicativi().sizeRispostaList(); j++) {
					ValidazioneContenutiApplicativiRisposta risposta = pa.getValidazioneContenutiApplicativi().getRisposta(j);
					if (risposta.getId().longValue() == Long.parseLong(idParent)) {
						parentName = risposta.getNome();
						for (int z = 0; z < risposta.getConfigurazione().getConfigurazionePattern().sizePatternList(); z++) {
							ValidazioneContenutiApplicativiPatternRegola pattern = risposta.getConfigurazione().getConfigurazionePattern().getPattern(z);
							if (pattern.getId().longValue() == idRegola.longValue()) {
								pattern.setNome(nome);
								pattern.setRegola(regola);
								pattern.setAnd(ServletUtils.isCheckBoxEnabled(patternAndS));
								pattern.setNot(ServletUtils.isCheckBoxEnabled(patternNotS));
								break;
							}
						}
						break;
					}
				}
			} else {
				for (int j = 0; j < pa.getValidazioneContenutiApplicativi().getConfigurazione().getConfigurazionePattern().sizePatternList(); j++) {
					ValidazioneContenutiApplicativiPatternRegola pattern = pa.getValidazioneContenutiApplicativi().getConfigurazione().getConfigurazionePattern().getPattern(j);
					if (pattern.getId().longValue() == idRegola.longValue()) {
						pattern.setNome(nome);
						pattern.setRegola(regola);
						pattern.setAnd(ServletUtils.isCheckBoxEnabled(patternAndS));
						pattern.setNot(ServletUtils.isCheckBoxEnabled(patternNotS));
						break;
					}
				}
			}
			
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
			
			pa = porteApplicativeCore.getPortaApplicativa(idInt);
			
			// ricarico parent
			if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RICHIESTA)) {
				for (int j = 0; j < pa.getValidazioneContenutiApplicativi().sizeRichiestaList(); j++) {
					ValidazioneContenutiApplicativiRichiesta richiesta = pa.getValidazioneContenutiApplicativi().getRichiesta(j);
					if (richiesta.getNome().equals(parentName)) {
						idParent = richiesta.getId()+"";
						break;
					}
				}
			} else if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA)) {
				for (int j = 0; j < pa.getValidazioneContenutiApplicativi().sizeRispostaList(); j++) {
					ValidazioneContenutiApplicativiRisposta risposta = pa.getValidazioneContenutiApplicativi().getRisposta(j);
					if (risposta.getNome().equals(parentName)) {
						idParent = risposta.getId()+"";
						break;
					}
				}
			} 
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_PATTERN; 
			
			if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RICHIESTA)) {
				idLista = Liste.PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RICHIESTA_PATTERN;
			} else if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA)) {
				idLista = Liste.PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RISPOSTA_PATTERN;
			}
			
			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
			
			List<ValidazioneContenutiApplicativiPatternRegola> lista = null;
			if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RICHIESTA)) {
				lista = porteApplicativeCore.listaPatternValidazioneContenutiRichiesta(Long.parseLong(idParent), ricerca);
			} else if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA)) {
				lista = porteApplicativeCore.listaPatternValidazioneContenutiRisposta(Long.parseLong(idParent), ricerca);
			} else {
				lista = porteApplicativeCore.listaPatternValidazioneContenuti(Long.parseLong(idPorta), ricerca);
			}
			
			porteApplicativeHelper.preparePorteAppValidazioneContenutiPatternList(ricerca, lista, idParent, tipoParent); 
						
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_PATTERN,	ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_PATTERN, 
					ForwardParams.CHANGE());
		}
	}
}
