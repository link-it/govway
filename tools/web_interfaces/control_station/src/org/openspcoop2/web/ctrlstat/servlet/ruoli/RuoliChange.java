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


package org.openspcoop2.web.ctrlstat.servlet.ruoli;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * RuoliChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class RuoliChange extends Action {

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
			RuoliHelper ruoliHelper = new RuoliHelper(request, pd, session);

			String id = ruoliHelper.getParameter(RuoliCostanti.PARAMETRO_RUOLO_ID);
			long ruoloId = Long.parseLong(id);
			String nome = ruoliHelper.getParameter(RuoliCostanti.PARAMETRO_RUOLO_NOME);
			String descrizione = ruoliHelper.getParameter(RuoliCostanti.PARAMETRO_RUOLO_DESCRIZIONE);
			String tipologia = ruoliHelper.getParameter(RuoliCostanti.PARAMETRO_RUOLO_TIPOLOGIA);
			String nomeEsterno = ruoliHelper.getParameter(RuoliCostanti.PARAMETRO_RUOLO_NOME_ESTERNO);
			String contesto = ruoliHelper.getParameter(RuoliCostanti.PARAMETRO_RUOLO_CONTESTO);
			String resetElementoCacheS = ruoliHelper.getParameter(CostantiControlStation.PARAMETRO_ELIMINA_ELEMENTO_DALLA_CACHE);
			boolean resetElementoCache = ServletUtils.isCheckBoxEnabled(resetElementoCacheS);
			
			RuoliCore ruoliCore = new RuoliCore();

			// Preparo il menu
			ruoliHelper.makeMenu();

			// Prendo il ruolo
			Ruolo ruolo  = ruoliCore.getRuolo(ruoloId);
			
			// reset elemento dalla cache
			if(resetElementoCache) {
				
				// Uso lo stessoAlias
				List<String> aliases = ruoliCore.getJmxPdDAliases();
				String alias = null;
				if(aliases!=null && !aliases.isEmpty()) {
					alias = aliases.get(0);
				}
				String labelRuolo = ruolo.getNome();
				ruoliCore.invokeJmxMethodAllNodesAndSetResult(pd, ruoliCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
						ruoliCore.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheRuolo(alias),
						MessageFormat.format(CostantiControlStation.LABEL_ELIMINATO_CACHE_SUCCESSO,labelRuolo),
						MessageFormat.format(CostantiControlStation.LABEL_ELIMINATO_CACHE_FALLITO_PREFIX,labelRuolo),
						ruolo.getId());				
				
				String resetFromLista = ruoliHelper.getParameter(CostantiControlStation.PARAMETRO_RESET_CACHE_FROM_LISTA);
				boolean arrivoDaLista = "true".equalsIgnoreCase(resetFromLista);
				
				if(arrivoDaLista) {
					
					// preparo lista
					
					ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

					int idLista = Liste.RUOLI;
					
					// poiche' esistono filtri che hanno necessita di postback salvo in sessione
					List<Ruolo> lista = null;
					if(!ServletUtils.isSearchDone(ruoliHelper)) {
						lista = ServletUtils.getRisultatiRicercaFromSession(request, session, idLista,  Ruolo.class);
					}

					ricerca = ruoliHelper.checkSearchParameters(idLista, ricerca);
					
					ruoliHelper.clearFiltroSoggettoByPostBackProtocollo(RuoliHelper.POSIZIONE_FILTRO_PROTOCOLLO, ricerca, idLista);
					
					if(lista==null) {
						boolean filtroSoggetto = false;
						String filterApiContesto = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_API_CONTESTO);
						if(!Filtri.FILTRO_API_CONTESTO_VALUE_SOGGETTI.equals(filterApiContesto) && ruoliHelper.isSoggettoMultitenantSelezionato()) {
							List<String> protocolli = ruoliCore.getProtocolli(request, session,false);
							if(protocolli!=null && protocolli.size()==1) { // dovrebbe essere l'unico caso in cui un soggetto multitenant è selezionato
								filtroSoggetto = true;
							}
						}
						if(filtroSoggetto) {
							ricerca.addFilter(idLista, Filtri.FILTRO_SOGGETTO, ruoliHelper.getSoggettoMultitenantSelezionato());
						}
						
						if(ruoliCore.isVisioneOggettiGlobale(userLogin)){
							lista = ruoliCore.ruoliList(null, ricerca);
						}else{
							lista = ruoliCore.ruoliList(userLogin, ricerca);
						}
					}
					
					if(!ruoliHelper.isPostBackFilterElement()) {
						ServletUtils.setRisultatiRicercaIntoSession(request, session, idLista, lista); // salvo poiche' esistono filtri che hanno necessita di postback
					}
					
					ruoliHelper.prepareRuoliList(ricerca, lista);
					
					// salvo l'oggetto ricerca nella sessione
					ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
					
					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeFinished(mapping, RuoliCostanti.OBJECT_NAME_RUOLI, ForwardParams.CHANGE());
				}
			}
			
			// Se nomehid = null, devo visualizzare la pagina per la
			// modifica dati
			if (ruoliHelper.isEditModeInProgress()) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, RuoliCostanti.LABEL_RUOLI, 
						RuoliCostanti.SERVLET_NAME_RUOLI_LIST, ruolo.getNome());
				

				// Prendo i dati dal db solo se non sono stati passati
				if (nome == null) {
					nome = ruolo.getNome();
				}
				if (descrizione == null) {
					descrizione = ruolo.getDescrizione();
				}
				if (tipologia == null) {
					tipologia = ruolo.getTipologia().getValue();
					nomeEsterno = ruolo.getNomeEsterno();
				}
				if (contesto == null) {
					contesto = ruolo.getContestoUtilizzo().getValue();
				}


				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = ruoliHelper.addRuoloToDati(TipoOperazione.CHANGE, ruoloId, nome, descrizione, tipologia, nomeEsterno, contesto, dati, ruolo.getNome());

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						RuoliCostanti.OBJECT_NAME_RUOLI, 
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = ruoliHelper.ruoloCheckData(TipoOperazione.CHANGE, ruolo);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, RuoliCostanti.LABEL_RUOLI, 
						RuoliCostanti.SERVLET_NAME_RUOLI_LIST, ruolo.getNome());

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = ruoliHelper.addRuoloToDati(TipoOperazione.CHANGE, ruoloId, nome, descrizione, tipologia, nomeEsterno, contesto, dati, ruolo.getNome());

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						RuoliCostanti.OBJECT_NAME_RUOLI, 
						ForwardParams.CHANGE());
			}

			// Modifico i dati del registro nel db
			Ruolo ruoloNEW = new Ruolo();
			ruoloNEW.setNome(nome);
			ruoloNEW.setDescrizione(descrizione);
			ruoloNEW.setTipologia(RuoloTipologia.toEnumConstant(tipologia, true));
			if(ruoloNEW.getTipologia()!=null && (RuoloTipologia.QUALSIASI.equals(ruoloNEW.getTipologia()) || RuoloTipologia.ESTERNO.equals(ruoloNEW.getTipologia()))) {
				String n = nomeEsterno;
				if(n!=null) {
					n = n.trim();
				}
				ruoloNEW.setNomeEsterno(n);
			}
			ruoloNEW.setContestoUtilizzo(RuoloContesto.toEnumConstant(contesto, true));
			ruoloNEW.setSuperUser(userLogin);
			
			ruoloNEW.setOldIDRuoloForUpdate(new IDRuolo(nome));

			List<Object> listOggettiDaAggiornare = new ArrayList<>();
			
			listOggettiDaAggiornare.add(ruoloNEW);
			
			if(!ruolo.getNome().equals(nome)){
				
				// e' stato modificato il nome
				
				IDRuolo oldIdRuolo = ruoloNEW.getOldIDRuoloForUpdate();
				oldIdRuolo.setNome(ruolo.getNome());
				
				RuoliUtilities.findOggettiDaAggiornare(oldIdRuolo, ruoloNEW, ruoliCore, listOggettiDaAggiornare);
				
			}
					
			ruoliCore.performUpdateOperation(userLogin, ruoliHelper.smista(), listOggettiDaAggiornare.toArray());

			if(!ruolo.getNome().equals(nome)){
				ServletUtils.removeRisultatiRicercaFromSession(request, session, Liste.RUOLI);
			}
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			List<Ruolo> lista = null;
			if(ruoliCore.isVisioneOggettiGlobale(userLogin)){
				lista = ruoliCore.ruoliList(null, ricerca);
			}else{
				lista = ruoliCore.ruoliList(userLogin, ricerca);
			}
			
			ruoliHelper.prepareRuoliList(ricerca, lista);
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					RuoliCostanti.OBJECT_NAME_RUOLI,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					RuoliCostanti.OBJECT_NAME_RUOLI, ForwardParams.CHANGE());
		}
	}
}
