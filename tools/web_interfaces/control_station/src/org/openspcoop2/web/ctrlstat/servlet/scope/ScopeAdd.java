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


package org.openspcoop2.web.ctrlstat.servlet.scope;

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
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.constants.ScopeContesto;
//import org.openspcoop2.core.registry.constants.ScopeTipologia;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ConfigurazioneScopeAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ScopeAdd extends Action {

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

			String nome = scopeHelper.getParameter(ScopeCostanti.PARAMETRO_SCOPE_NOME);
			String descrizione = scopeHelper.getParameter(ScopeCostanti.PARAMETRO_SCOPE_DESCRIZIONE);
			String tipologia = scopeHelper.getParameter(ScopeCostanti.PARAMETRO_SCOPE_TIPOLOGIA);
			if (tipologia == null) {
				tipologia = "";
			}
			String nomeEsterno = scopeHelper.getParameter(ScopeCostanti.PARAMETRO_SCOPE_NOME_ESTERNO);
			String contesto = scopeHelper.getParameter(ScopeCostanti.PARAMETRO_SCOPE_CONTESTO);
			if (contesto == null) {
				contesto = ScopeCostanti.DEFAULT_VALUE_PARAMETRO_SCOPE_CONTESTO_UTILIZZO;
			}


			ScopeCore scopeCore = new ScopeCore();

			// Preparo il menu
			scopeHelper.makeMenu();

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (scopeHelper.isEditModeInProgress()) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, ScopeCostanti.LABEL_SCOPE, ScopeCostanti.SERVLET_NAME_SCOPE_LIST);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());


				dati = scopeHelper.addScopeToDati(TipoOperazione.ADD, null, nome != null ? nome : "", descrizione != null ? descrizione : "",
						tipologia, nomeEsterno, contesto, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ScopeCostanti.OBJECT_NAME_SCOPE, 
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = scopeHelper.scopeCheckData(TipoOperazione.ADD, null);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, ScopeCostanti.LABEL_SCOPE, ScopeCostanti.SERVLET_NAME_SCOPE_LIST);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = scopeHelper.addScopeToDati(TipoOperazione.ADD, null, nome, descrizione, tipologia, nomeEsterno, contesto, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ScopeCostanti.OBJECT_NAME_SCOPE, 
						ForwardParams.ADD());
			}

			// Inserisco il registro nel db
			Scope scope = new Scope();
			scope.setNome(nome);
			scope.setDescrizione(descrizione);
			scope.setTipologia(tipologia);
			String n = nomeEsterno;
			if(n!=null) {
				n = n.trim();
			}
			scope.setNomeEsterno(n);
			
			scope.setContestoUtilizzo(ScopeContesto.toEnumConstant(contesto, true));
			scope.setSuperUser(userLogin);
			
			scopeCore.performCreateOperation(userLogin, scopeHelper.smista(), scope);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			if(scopeCore.isSetSearchAfterAdd()) {
				scopeCore.setSearchAfterAdd(Liste.SCOPE, scope.getNome(), session, ricerca);
			}
			
			List<Scope> lista = null;
			if(scopeCore.isVisioneOggettiGlobale(userLogin)){
				lista = scopeCore.scopeList(null, ricerca);
			}else{
				lista = scopeCore.scopeList(userLogin, ricerca);
			}
			
			scopeHelper.prepareScopeList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ScopeCostanti.OBJECT_NAME_SCOPE,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ScopeCostanti.OBJECT_NAME_SCOPE, ForwardParams.ADD());
		}
	}


}
