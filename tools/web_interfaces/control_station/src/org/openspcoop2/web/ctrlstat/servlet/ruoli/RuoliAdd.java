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


package org.openspcoop2.web.ctrlstat.servlet.ruoli;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ConfigurazioneRuoliAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class RuoliAdd extends Action {

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

			String nome = ruoliHelper.getParameter(RuoliCostanti.PARAMETRO_RUOLO_NOME);
			String descrizione = ruoliHelper.getParameter(RuoliCostanti.PARAMETRO_RUOLO_DESCRIZIONE);
			String tipologia = ruoliHelper.getParameter(RuoliCostanti.PARAMETRO_RUOLO_TIPOLOGIA);
			if (tipologia == null) {
				tipologia = RuoliCostanti.DEFAULT_VALUE_PARAMETRO_RUOLO_TIPOLOGIA;
			}
			String nomeEsterno = ruoliHelper.getParameter(RuoliCostanti.PARAMETRO_RUOLO_NOME_ESTERNO);
			String contesto = ruoliHelper.getParameter(RuoliCostanti.PARAMETRO_RUOLO_CONTESTO);
			if (contesto == null) {
				contesto = RuoliCostanti.DEFAULT_VALUE_PARAMETRO_RUOLO_CONTESTO_UTILIZZO;
			}


			RuoliCore ruoliCore = new RuoliCore();

			// Preparo il menu
			ruoliHelper.makeMenu();

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (ruoliHelper.isEditModeInProgress()) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, RuoliCostanti.LABEL_RUOLI, RuoliCostanti.SERVLET_NAME_RUOLI_LIST);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());


				dati = ruoliHelper.addRuoloToDati(TipoOperazione.ADD, null, nome != null ? nome : "", descrizione != null ? descrizione : "",
						tipologia, nomeEsterno, contesto, dati, null);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						RuoliCostanti.OBJECT_NAME_RUOLI, 
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = ruoliHelper.ruoloCheckData(TipoOperazione.ADD, null);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, RuoliCostanti.LABEL_RUOLI, RuoliCostanti.SERVLET_NAME_RUOLI_LIST);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = ruoliHelper.addRuoloToDati(TipoOperazione.ADD, null, nome, descrizione, tipologia, nomeEsterno, contesto, dati, null);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						RuoliCostanti.OBJECT_NAME_RUOLI, 
						ForwardParams.ADD());
			}

			// Inserisco il registro nel db
			Ruolo ruolo = new Ruolo();
			ruolo.setNome(nome);
			ruolo.setDescrizione(descrizione);
			ruolo.setTipologia(RuoloTipologia.toEnumConstant(tipologia, true));
			if(ruolo.getTipologia()!=null && (RuoloTipologia.QUALSIASI.equals(ruolo.getTipologia()) || RuoloTipologia.ESTERNO.equals(ruolo.getTipologia()))) {
				String n = nomeEsterno;
				if(n!=null) {
					n = n.trim();
				}
				ruolo.setNomeEsterno(n);
			}
			ruolo.setContestoUtilizzo(RuoloContesto.toEnumConstant(contesto, true));
			ruolo.setSuperUser(userLogin);
			
			ruoliCore.performCreateOperation(userLogin, ruoliHelper.smista(), ruolo);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			if(ruoliCore.isSetSearchAfterAdd()) {
				ruoliCore.setSearchAfterAdd(Liste.RUOLI, ruolo.getNome(), request, session, ricerca);
			}
			
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
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					RuoliCostanti.OBJECT_NAME_RUOLI, ForwardParams.ADD());
		}
	}


}
