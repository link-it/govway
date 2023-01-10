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


package org.openspcoop2.web.ctrlstat.servlet.gruppi;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * GruppiAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class GruppiAdd extends Action {

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
			GruppiHelper gruppiHelper = new GruppiHelper(request, pd, session);

			String nome = gruppiHelper.getParameter(GruppiCostanti.PARAMETRO_GRUPPO_NOME);
			String descrizione = gruppiHelper.getParameter(GruppiCostanti.PARAMETRO_GRUPPO_DESCRIZIONE);
			String serviceBinding = gruppiHelper.getParameter(GruppiCostanti.PARAMETRO_GRUPPO_SERVICE_BINDING);
			if (serviceBinding == null) {
				serviceBinding = GruppiCostanti.DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_QUALSIASI;
			}


			GruppiCore gruppiCore = new GruppiCore();

			// Preparo il menu
			gruppiHelper.makeMenu();

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (gruppiHelper.isEditModeInProgress()) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, GruppiCostanti.LABEL_GRUPPI, GruppiCostanti.SERVLET_NAME_GRUPPI_LIST);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());


				dati = gruppiHelper.addGruppoToDati(TipoOperazione.ADD, null, nome != null ? nome : "", descrizione != null ? descrizione : "", serviceBinding, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	GruppiCostanti.OBJECT_NAME_GRUPPI, ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = gruppiHelper.gruppoCheckData(TipoOperazione.ADD, null);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, GruppiCostanti.LABEL_GRUPPI, GruppiCostanti.SERVLET_NAME_GRUPPI_LIST);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = gruppiHelper.addGruppoToDati(TipoOperazione.ADD, null, nome, descrizione, serviceBinding, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, GruppiCostanti.OBJECT_NAME_GRUPPI, ForwardParams.ADD());
			}

			// Inserisco il registro nel db
			Gruppo gruppo = new Gruppo();
			gruppo.setNome(nome);
			gruppo.setDescrizione(descrizione);
			if(!serviceBinding.equals(GruppiCostanti.DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_QUALSIASI))
				gruppo.setServiceBinding(ServiceBinding.valueOf(serviceBinding));
			else
				gruppo.setServiceBinding(null);
			gruppo.setSuperUser(userLogin);
			
			gruppiCore.performCreateOperation(userLogin, gruppiHelper.smista(), gruppo);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			List<Gruppo> lista = null;
			if(gruppiCore.isVisioneOggettiGlobale(userLogin)){
				lista = gruppiCore.gruppiList(null, ricerca);
			}else{
				lista = gruppiCore.gruppiList(userLogin, ricerca);
			}
			
			gruppiHelper.prepareGruppiList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, GruppiCostanti.OBJECT_NAME_GRUPPI, ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, GruppiCostanti.OBJECT_NAME_GRUPPI, ForwardParams.ADD());
		}
	}


}
