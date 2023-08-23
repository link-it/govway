/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.sa;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * servizioApplicativoList
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ServiziApplicativiList extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(request, pd, session);
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
			Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, session, request);
			if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			Boolean useIdSogg = parentSA == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO;
			
			String resetCacheFromLista = saHelper.getParameter(CostantiControlStation.PARAMETRO_RESET_CACHE_FROM_LISTA);
			String verificaCertificatiFromLista = saHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CERTIFICATI_FROM_LISTA);
			boolean arrivoDaLista = "true".equalsIgnoreCase(resetCacheFromLista) || "true".equalsIgnoreCase(verificaCertificatiFromLista);
						
			String idsogg = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			long soggLong = -1;
			// Posso arrivare a questa pagina anche dal menu' senza specificare il soggetto
			if(idsogg != null && !arrivoDaLista){
				soggLong = Long.parseLong(idsogg);
				useIdSogg = true;
			} else {
				useIdSogg = false;
			}
			
			parentSA = useIdSogg ? ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO : ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;

			// salvo il punto di ingresso
			ServletUtils.setObjectIntoSession(request, session, parentSA, ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT);

			// Preparo il menu
			saHelper.makeMenu();

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			
			ServiziApplicativiCore saCore = new ServiziApplicativiCore();
						
			String userLogin = (String) ServletUtils.getUserLoginFromSession(session);

			List<ServizioApplicativo> lista = null;
			int idLista = -1;
			if(!useIdSogg){
				idLista = Liste.SERVIZIO_APPLICATIVO;
			}
			else {
				idLista = Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO;
			}
			
			// poiche' esistono filtri che hanno necessita di postback salvo in sessione
			if(!ServletUtils.isSearchDone(saHelper)) {
				lista = ServletUtils.getRisultatiRicercaFromSession(request, session, idLista,  ServizioApplicativo.class);
			}
			
			ricerca = saHelper.checkSearchParameters(idLista, ricerca);
			
			saHelper.clearFiltroSoggettoByPostBackProtocollo(0, ricerca, idLista);
			
			if(!useIdSogg){
				boolean filtroSoggetto = false;
				if(saHelper.isSoggettoMultitenantSelezionato()) {
					List<String> protocolli = saCore.getProtocolli(request, session,false);
					if(protocolli!=null && protocolli.size()==1) { // dovrebbe essere l'unico caso in cui un soggetto multitenant è selezionato
						String protocollo = protocolli.get(0);
						filtroSoggetto = !saCore.isSupportatoAutenticazioneApplicativiEsterniErogazione(protocollo);  // devono essere fatti vedere anche quelli
					}
				}
				if(filtroSoggetto) {
					ricerca.addFilter(idLista, Filtri.FILTRO_SOGGETTO, saHelper.getSoggettoMultitenantSelezionato());
				}
				
				if(lista==null) {
					if(saCore.isVisioneOggettiGlobale(userLogin)){
						lista = saCore.soggettiServizioApplicativoList(null, ricerca);
					}else{
						lista = saCore.soggettiServizioApplicativoList(userLogin, ricerca);
					}
				}
			}else {
				if(lista==null) {
					lista = saCore.soggettiServizioApplicativoList(ricerca,soggLong);
				}
			}
			
			if(!saHelper.isPostBackFilterElement()) {
				ServletUtils.setRisultatiRicercaIntoSession(request, session, idLista, lista); // salvo poiche' esistono filtri che hanno necessita di postback
			}
			
			saHelper.prepareServizioApplicativoList(ricerca, lista, useIdSogg);

			String msg = saHelper.getParameter(Costanti.PARAMETER_NAME_MSG_ERROR_EXPORT);
			if(msg!=null && !"".equals(msg)){
				pd.setMessage("Errore durante esportazione: "+msg);
			}
			
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForward(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.LIST());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.LIST());
		}
	}
}
