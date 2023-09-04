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

import java.util.ArrayList;
import java.util.List;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
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
 * GruppiChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class GruppiChange extends Action {

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
			
			// Preparo il menu
			gruppiHelper.makeMenu();

			String id = gruppiHelper.getParametroLong(GruppiCostanti.PARAMETRO_GRUPPO_ID);
			long gruppoId = Long.parseLong(id);
			String nome = gruppiHelper.getParameter(GruppiCostanti.PARAMETRO_GRUPPO_NOME);
			String descrizione = gruppiHelper.getParameter(GruppiCostanti.PARAMETRO_GRUPPO_DESCRIZIONE);
			String serviceBinding = gruppiHelper.getParameter(GruppiCostanti.PARAMETRO_GRUPPO_SERVICE_BINDING);
			
			GruppiCore gruppiCore = new GruppiCore();

			// Prendo il gruppo
			Gruppo gruppo  = gruppiCore.getGruppo(gruppoId);
			
			// Se nomehid = null, devo visualizzare la pagina per la
			// modifica dati
			if (gruppiHelper.isEditModeInProgress()) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, GruppiCostanti.LABEL_GRUPPI, 
						GruppiCostanti.SERVLET_NAME_GRUPPI_LIST, gruppo.getNome());
				

				// Prendo i dati dal db solo se non sono stati passati
				if (nome == null) {
					nome = gruppo.getNome();
				}
				if (descrizione == null) {
					descrizione = gruppo.getDescrizione();
				}
				if (serviceBinding == null) {
					if(gruppo.getServiceBinding() == null)
						serviceBinding = GruppiCostanti.DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_QUALSIASI;
					else
						serviceBinding = gruppo.getServiceBinding().name();
				}
				
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = gruppiHelper.addGruppoToDati(TipoOperazione.CHANGE, gruppoId, nome, descrizione, serviceBinding, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						GruppiCostanti.OBJECT_NAME_GRUPPI, 
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = gruppiHelper.gruppoCheckData(TipoOperazione.CHANGE, gruppo);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, GruppiCostanti.LABEL_GRUPPI, 
						GruppiCostanti.SERVLET_NAME_GRUPPI_LIST, gruppo.getNome());

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = gruppiHelper.addGruppoToDati(TipoOperazione.CHANGE, gruppoId, nome, descrizione, serviceBinding, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						GruppiCostanti.OBJECT_NAME_GRUPPI, 
						ForwardParams.CHANGE());
			}

			// Modifico i dati del registro nel db
			Gruppo gruppoNEW = new Gruppo();
			gruppoNEW.setNome(nome);
			gruppoNEW.setDescrizione(descrizione);
			if(!serviceBinding.equals(GruppiCostanti.DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_QUALSIASI))
				gruppoNEW.setServiceBinding(ServiceBinding.valueOf(serviceBinding));
			else
				gruppoNEW.setServiceBinding(null);
			gruppoNEW.setSuperUser(userLogin);
			
			gruppoNEW.setOldIDGruppoForUpdate(new IDGruppo(nome));

			List<Object> listOggettiDaAggiornare = new ArrayList<>();
			
			listOggettiDaAggiornare.add(gruppoNEW);
			
			if(!gruppo.getNome().equals(nome)){
				
				// e' stato modificato il nome
				
				IDGruppo oldIdGruppo = gruppoNEW.getOldIDGruppoForUpdate();
				oldIdGruppo.setNome(gruppo.getNome());
				
				GruppiUtilities.findOggettiDaAggiornare(oldIdGruppo, gruppoNEW, gruppiCore, listOggettiDaAggiornare);
				
			}
			
			
			gruppiCore.performUpdateOperation(userLogin, gruppiHelper.smista(), listOggettiDaAggiornare.toArray());

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

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					GruppiCostanti.OBJECT_NAME_GRUPPI,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					GruppiCostanti.OBJECT_NAME_GRUPPI, ForwardParams.CHANGE());
		}
	}
}
