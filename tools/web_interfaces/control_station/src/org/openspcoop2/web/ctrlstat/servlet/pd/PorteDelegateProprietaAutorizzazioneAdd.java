/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteDelegateProprietaAutorizzazioneAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateProprietaAutorizzazioneAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
 
		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session);
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String idPorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(idPorta);
			String nome = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME);
			String valore = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_VALORE);
			String idSoggFruitore = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			String idFruizione= porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null) 
				idFruizione = "";
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo nome, tipo e pdd del soggetto
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();

			// Prendo nome della porta applicativa
			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idInt);
			String idporta = pde.getNome();
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + pde.getId());
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, pde.getIdSoggetto() + "");
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
			Parameter[] urlParms = { pId,pIdSoggetto,pIdAsps,pIdFrizione };
			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idSoggFruitore, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI,
						pde);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI, 
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + pde.getId()),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA, pde.getNome()),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, pde.getIdSoggetto() + ""),
					pIdAsps, new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione+ "")));
			
			String labelPagLista = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_PROPRIETA; 
			
			lstParam.add(new Parameter(labelPagLista, 
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_AUTORIZZAZIONE_CUSTOM_PROPERTIES_LIST,urlParms));
			lstParam.add(ServletUtils.getParameterAggiungi());
			
			// Se nome = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteDelegateHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addProprietaAutorizzazioneCustomToDati(dati, TipoOperazione.ADD, nome ,valore);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idSoggFruitore,  null,idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AUTORIZZAZIONE_CUSTOM_PROPERTIES,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.proprietaAutorizzazioneCheckData(TipoOperazione.ADD, idPorta, nome, valore);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteDelegateHelper.addProprietaAutorizzazioneCustomToDati(dati, TipoOperazione.ADD, nome ,valore);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idSoggFruitore,  null,idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AUTORIZZAZIONE_CUSTOM_PROPERTIES, 
						ForwardParams.ADD());
			}

			// Inserisco la property della porta applicativa nel db
			Proprieta ssp = new Proprieta();
			ssp.setNome(nome);
			ssp.setValore(valore);
			pde.addProprietaAutorizzazione(ssp);

			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), pde);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.PORTE_DELEGATE_PROPRIETA_AUTORIZZAZIONE;

			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);

			List<Proprieta> lista = porteDelegateCore.porteDelegateAutorizzazioneCustomPropList(Integer.parseInt(idPorta), ricerca);

			porteDelegateHelper.preparePorteDelegateAutorizzazioneCustomPropList(idporta, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AUTORIZZAZIONE_CUSTOM_PROPERTIES, 
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AUTORIZZAZIONE_CUSTOM_PROPERTIES,
					ForwardParams.ADD());
		}  
	}

	
}
