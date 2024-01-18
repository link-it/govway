/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.scope;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.constants.ScopeContesto;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ScopeChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ScopeChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		try {
			ScopeHelper scopeHelper = new ScopeHelper(request, pd, session);
			
			// Preparo il menu
			scopeHelper.makeMenu();

			String id = scopeHelper.getParametroLong(ScopeCostanti.PARAMETRO_SCOPE_ID);
			long scopeId = Long.parseLong(id);
			String nome = scopeHelper.getParameter(ScopeCostanti.PARAMETRO_SCOPE_NOME);
			String descrizione = scopeHelper.getParameter(ScopeCostanti.PARAMETRO_SCOPE_DESCRIZIONE);
			String tipologia = scopeHelper.getParameter(ScopeCostanti.PARAMETRO_SCOPE_TIPOLOGIA);
			String nomeEsterno = scopeHelper.getParameter(ScopeCostanti.PARAMETRO_SCOPE_NOME_ESTERNO);
			String contesto = scopeHelper.getParameter(ScopeCostanti.PARAMETRO_SCOPE_CONTESTO);
			String resetElementoCacheS = scopeHelper.getParametroBoolean(CostantiControlStation.PARAMETRO_ELIMINA_ELEMENTO_DALLA_CACHE);
			boolean resetElementoCache = ServletUtils.isCheckBoxEnabled(resetElementoCacheS);
			
			ScopeCore scopeCore = new ScopeCore();

			// Prendo il scope
			Scope scope  = scopeCore.getScope(scopeId);
			
			// reset elemento dalla cache
			if(resetElementoCache) {
				
				// Uso lo stessoAlias
				List<String> aliases = scopeCore.getJmxPdDAliases();
				String alias = null;
				if(aliases!=null && !aliases.isEmpty()) {
					alias = aliases.get(0);
				}
				String labelScope = scope.getNome();
				scopeCore.invokeJmxMethodAllNodesAndSetResult(pd, scopeCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
						scopeCore.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheScope(alias),
						MessageFormat.format(CostantiControlStation.LABEL_ELIMINATO_CACHE_SUCCESSO,labelScope),
						MessageFormat.format(CostantiControlStation.LABEL_ELIMINATO_CACHE_FALLITO_PREFIX,labelScope),
						scope.getId());				
				
				String resetFromLista = scopeHelper.getParameter(CostantiControlStation.PARAMETRO_RESET_CACHE_FROM_LISTA);
				boolean arrivoDaLista = "true".equalsIgnoreCase(resetFromLista);
				
				if(arrivoDaLista) {
					
					// preparo lista
					ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

					int idLista = Liste.SCOPE;

					// poiche' esistono filtri che hanno necessita di postback salvo in sessione
					List<Scope> lista = null;
					if(!ServletUtils.isSearchDone(scopeHelper)) {
						lista = ServletUtils.getRisultatiRicercaFromSession(request, session, idLista, Scope.class);
					}
					
					ricerca = scopeHelper.checkSearchParameters(idLista, ricerca);
					
					scopeHelper.clearFiltroSoggettoByPostBackProtocollo(ScopeHelper.POSIZIONE_FILTRO_PROTOCOLLO, ricerca, idLista);
					
					if(lista==null) {
						boolean filtroSoggetto = false;
						List<String> protocolli = scopeCore.getProtocolli(request, session,false);
						if(protocolli!=null && protocolli.size()==1) { // dovrebbe essere l'unico caso in cui un soggetto multitenant è selezionato
							filtroSoggetto = true;
						}
						if(filtroSoggetto) {
							ricerca.addFilter(idLista, Filtri.FILTRO_SOGGETTO, scopeHelper.getSoggettoMultitenantSelezionato());
						}
						
						if(scopeCore.isVisioneOggettiGlobale(userLogin)){
							lista = scopeCore.scopeList(null, ricerca);
						}else{
							lista = scopeCore.scopeList(userLogin, ricerca);
						}
					}
					
					if(!scopeHelper.isPostBackFilterElement()) {
						ServletUtils.setRisultatiRicercaIntoSession(request, session, idLista, lista); // salvo poiche' esistono filtri che hanno necessita di postback
					}
					
					scopeHelper.prepareScopeList(ricerca, lista);

					// salvo l'oggetto ricerca nella sessione
					ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
				
					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeFinished(mapping, ScopeCostanti.OBJECT_NAME_SCOPE , ForwardParams.CHANGE());
				}
			}
			
			// Se nomehid = null, devo visualizzare la pagina per la
			// modifica dati
			if (scopeHelper.isEditModeInProgress()) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitleServletChange(pd, ScopeCostanti.LABEL_SCOPE, 
						ScopeCostanti.SERVLET_NAME_SCOPE_LIST, scope.getNome());
				

				// Prendo i dati dal db solo se non sono stati passati
				if (nome == null) {
					nome = scope.getNome();
				}
				if (descrizione == null) {
					descrizione = scope.getDescrizione();
				}
				if (tipologia == null) {
					tipologia = scope.getTipologia();
					nomeEsterno = scope.getNomeEsterno();
				}
				if (contesto == null) {
					contesto = scope.getContestoUtilizzo().getValue();
				}


				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = scopeHelper.addScopeToDati(TipoOperazione.CHANGE, scopeId, nome, descrizione, tipologia, nomeEsterno, contesto, dati, scope.getNome());

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ScopeCostanti.OBJECT_NAME_SCOPE, 
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = scopeHelper.scopeCheckData(TipoOperazione.CHANGE, scope);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitleServletChange(pd, ScopeCostanti.LABEL_SCOPE, 
						ScopeCostanti.SERVLET_NAME_SCOPE_LIST, scope.getNome());

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = scopeHelper.addScopeToDati(TipoOperazione.CHANGE, scopeId, nome, descrizione, tipologia, nomeEsterno, contesto, dati, scope.getNome());

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ScopeCostanti.OBJECT_NAME_SCOPE, 
						ForwardParams.CHANGE());
			}

			// Modifico i dati del registro nel db
			Scope scopeNEW = new Scope();
			scopeNEW.setNome(nome);
			scopeNEW.setDescrizione(descrizione);
			scopeNEW.setTipologia(tipologia);
			String n = nomeEsterno;
			if(n!=null) {
				n = n.trim();
			}
			scopeNEW.setNomeEsterno(n);
			
			scopeNEW.setContestoUtilizzo(ScopeContesto.toEnumConstant(contesto, true));
			scopeNEW.setSuperUser(userLogin);
			
			scopeNEW.setOldIDScopeForUpdate(new IDScope(nome));

			List<Object> listOggettiDaAggiornare = new ArrayList<>();
			
			listOggettiDaAggiornare.add(scopeNEW);
			
			if(!scope.getNome().equals(nome)){
				
				// e' stato modificato il nome
				
				IDScope oldIdScope = scopeNEW.getOldIDScopeForUpdate();
				oldIdScope.setNome(scope.getNome());
				
				ScopeUtilities.findOggettiDaAggiornare(oldIdScope, scopeNEW, scopeCore, listOggettiDaAggiornare);
				
			}
			
			
			scopeCore.performUpdateOperation(userLogin, scopeHelper.smista(), listOggettiDaAggiornare.toArray());

			if(!scope.getNome().equals(nome)){
				ServletUtils.removeRisultatiRicercaFromSession(request, session, Liste.SCOPE);
			}
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			List<Scope> lista = null;
			if(scopeCore.isVisioneOggettiGlobale(userLogin)){
				lista = scopeCore.scopeList(null, ricerca);
			}else{
				lista = scopeCore.scopeList(userLogin, ricerca);
			}
			
			scopeHelper.prepareScopeList(ricerca, lista);
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ScopeCostanti.OBJECT_NAME_SCOPE,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ScopeCostanti.OBJECT_NAME_SCOPE, ForwardParams.CHANGE());
		}
	}
}
