/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.soggetti;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.registry.RuoliSoggetto;
import org.openspcoop2.core.registry.RuoloSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * SoggettiRuoliAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12564 $, $Date: 2017-01-11 14:31:31 +0100 (Wed, 11 Jan 2017) $
 * 
 */
public final class SoggettiRuoliAdd extends Action {

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
			SoggettiHelper soggettiHelper = new SoggettiHelper(request, pd, session);

			String id = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
			int idSogg = Integer.parseInt(id);
			
			String nome = request.getParameter(CostantiControlStation.PARAMETRO_RUOLO);


			SoggettiCore soggettiCore = new SoggettiCore();

			Soggetto soggettoRegistry = soggettiCore.getSoggettoRegistro(idSogg);
			String tmpTitle = soggettoRegistry.getTipo() + "/" + soggettoRegistry.getNome();
			
			FiltroRicercaRuoli filtroRuoli = new FiltroRicercaRuoli();
			filtroRuoli.setContesto(RuoloContesto.PORTA_APPLICATIVA);
			filtroRuoli.setTipologia(RuoloTipologia.INTERNO);
			
			List<String> ruoli = new ArrayList<>();
			if(soggettoRegistry.getRuoli()!=null && soggettoRegistry.getRuoli().getRuoloList()!=null && soggettoRegistry.getRuoli().getRuoloList().size()>0){
				for (RuoloSoggetto ruoloSoggetto : soggettoRegistry.getRuoli().getRuoloList()) {
					ruoli.add(ruoloSoggetto.getNome());	
				}
			}
			
			
			// Preparo il menu
			soggettiHelper.makeMenu();

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (ServletUtils.isEditModeInProgress(request)) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, SoggettiCostanti.LABEL_SOGGETTI, 
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST, "Ruoli di " + tmpTitle);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				DataElement de = new DataElement();
				de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
				de.setValue(id);
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
				
				dati = soggettiHelper.addRuoliToDati(TipoOperazione.ADD, dati, false, filtroRuoli, nome, ruoli, false, true);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						SoggettiCostanti.OBJECT_NAME_SOGGETTI_RUOLI, 
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = soggettiHelper.ruoloCheckData(TipoOperazione.ADD, nome, ruoli);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, SoggettiCostanti.LABEL_SOGGETTI, 
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST, "Ruoli di " + tmpTitle);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				DataElement de = new DataElement();
				de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
				de.setValue(id);
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
				
				dati = soggettiHelper.addRuoliToDati(TipoOperazione.ADD, dati, false, filtroRuoli, nome, ruoli, false, true);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						SoggettiCostanti.OBJECT_NAME_SOGGETTI_RUOLI, 
						ForwardParams.ADD());
			}

			// Inserisco
			RuoloSoggetto ruoloSoggetto = new RuoloSoggetto();
			ruoloSoggetto.setNome(nome);
			if(soggettoRegistry.getRuoli()==null){
				soggettoRegistry.setRuoli(new RuoliSoggetto());
			}
			soggettoRegistry.getRuoli().addRuolo(ruoloSoggetto);
			
			soggettiCore.performUpdateOperation(userLogin, soggettiHelper.smista(), soggettoRegistry);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<String> lista = soggettiCore.soggettiRuoliList(idSogg, ricerca);
					
			soggettiHelper.prepareRuoliList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					SoggettiCostanti.OBJECT_NAME_SOGGETTI_RUOLI,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					SoggettiCostanti.OBJECT_NAME_SOGGETTI_RUOLI, ForwardParams.ADD());
		}
	}


}
