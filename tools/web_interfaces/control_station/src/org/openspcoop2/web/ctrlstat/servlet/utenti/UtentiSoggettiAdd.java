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
package org.openspcoop2.web.ctrlstat.servlet.utenti;

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
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.User;

/**     
 * UtentiSoggettiAdd
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UtentiSoggettiAdd extends Action {

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
			UtentiHelper utentiHelper = new UtentiHelper(request, pd, session);
			String nomesu = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME);
			String soggetto = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_SOGGETTO);
			String protocollo = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_PROTOCOLLO);

			// Preparo il menu
			utentiHelper.makeMenu();

			UtentiCore utentiCore = new UtentiCore();
			SoggettiCore soggettiCore = new SoggettiCore(utentiCore);
			User user = utentiCore.getUser(nomesu);

			ConsoleSearch searchSoggetti = new ConsoleSearch(true);
			List<String> protocolli = user.getProtocolliSupportati();
			
			if(protocollo == null &&
				protocolli!=null && !protocolli.isEmpty()) {
				protocollo = utentiCore.getProtocolloDefault(request, null, protocolli);
			}
			
			searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, protocollo);
			searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
			List<Soggetto> listaSoggettiRegistro = soggettiCore.soggettiRegistroList(null, searchSoggetti);

			List<Soggetto> listaSoggettiNonUtilizzati = new ArrayList<>();

			for (Soggetto sog : listaSoggettiRegistro) {
				boolean found = false;
				for (IDSoggetto idSog : user.getSoggetti()) {
					if(sog.getTipo().equals(idSog.getTipo()) && sog.getNome().equals(idSog.getNome()) && sog.getIdentificativoPorta().equals(idSog.getCodicePorta())) {
						found = true;
						break;
					}
				}	
				
				if(!found) {
					listaSoggettiNonUtilizzati.add(sog);
				}
			}

			String [] soggettiLabels = new String [listaSoggettiNonUtilizzati.size() + 1];
			String [] soggettiValues = new String [listaSoggettiNonUtilizzati.size() + 1];

			soggettiLabels[0] = CostantiControlStation.LABEL_LIST_VALORE_NON_PRESENTE;
			soggettiValues[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;

			for (int i = 0; i < listaSoggettiNonUtilizzati.size(); i++) {
				Soggetto soggetto2 = listaSoggettiNonUtilizzati.get(i);
				String protocolloTmp = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto2.getTipo());
				soggettiLabels[(i+1)] = utentiHelper.getLabelNomeSoggetto(protocolloTmp, soggetto2.getTipo(), soggetto2.getNome());
				soggettiValues[(i+1)] = soggetto2.getId() + "";
			}

			List<Parameter> lstParam = new ArrayList<>();
			lstParam.add(new Parameter(UtentiCostanti.LABEL_UTENTI ,UtentiCostanti.SERVLET_NAME_UTENTI_LIST));
			lstParam.add(new Parameter(nomesu, UtentiCostanti.SERVLET_NAME_UTENTI_CHANGE, new Parameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME, nomesu)));
			lstParam.add(new Parameter(UtentiCostanti.LABEL_UTENTI_SOGGETTI, UtentiCostanti.SERVLET_NAME_UTENTI_SOGGETTI_LIST, new Parameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME, nomesu)));
			lstParam.add(ServletUtils.getParameterAggiungi());

			if(utentiHelper.isEditModeInProgress()){
				ServletUtils.setPageDataTitle(pd,lstParam);
				
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				if(soggetto==null){
					soggetto = "";
				}
				
				if(listaSoggettiNonUtilizzati.isEmpty()){
					if(protocolli.size() == 1) {
						pd.setMessage(UtentiCostanti.LABEL_UTENTI_SOGGETTI_DISPONIBILI_ESAURITI, MessageType.INFO);
						pd.disableEditMode();
						pd.setDati(dati);
						ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
						return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	UtentiCostanti.OBJECT_NAME_UTENTI_SOGGETTI,ForwardParams.ADD());
					} else {
						String msf = MessageFormat.format(UtentiCostanti.LABEL_UTENTI_SOGGETTI_DISPONIBILI_ESAURITI_PER_LA_MODALITA_XX, utentiHelper.getLabelProtocollo(protocollo));
						pd.setMessage(msf, MessageType.INFO);
						pd.disableOnlyButton();
					}
				}
				
				utentiHelper.addUtentiSoggettiToDati(dati, TipoOperazione.ADD,nomesu,soggetto,soggettiValues,soggettiLabels, protocolli,protocollo);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, UtentiCostanti.OBJECT_NAME_UTENTI_SOGGETTI,ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = utentiHelper.utentiSoggettiCheckData(TipoOperazione.ADD,nomesu,soggetto);
			if (!isOk) {
				ServletUtils.setPageDataTitle(pd,lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				utentiHelper.addUtentiSoggettiToDati(dati, TipoOperazione.ADD,nomesu,soggetto,soggettiValues,soggettiLabels,protocolli,protocollo);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, UtentiCostanti.OBJECT_NAME_UTENTI_SOGGETTI,ForwardParams.ADD());
			}

			// aggiungo il nuovo soggetto alla lista
			Long newId = Long.parseLong(soggetto);
			IDSoggetto idSoggetto = soggettiCore.getIdSoggettoRegistro(newId);
			user.getSoggetti().add(idSoggetto);

			// salvataggio sul db
			utentiCore.performUpdateOperation(userLogin, utentiHelper.smista(), user); 

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session,ConsoleSearch.class);

			int idLista = Liste.UTENTI_SOGGETTI;

			ricerca = utentiHelper.checkSearchParameters(idLista, ricerca);

			List<IDSoggetto> lista = utentiCore.utentiSoggettiList(nomesu, ricerca);

			utentiHelper.prepareUtentiSoggettiList(ricerca, lista, user);

			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, UtentiCostanti.OBJECT_NAME_UTENTI_SOGGETTI, ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					UtentiCostanti.OBJECT_NAME_UTENTI_SOGGETTI, ForwardParams.ADD());
		} 
	}

}
