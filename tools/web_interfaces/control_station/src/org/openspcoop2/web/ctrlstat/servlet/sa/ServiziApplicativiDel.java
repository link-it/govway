/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.DBOggettiInUsoUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * servizioApplicativoDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ServiziApplicativiDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Salvo il vecchio PageData
		// PageData pdold = (PageData) session.getAttribute("PageData");

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_USA_ID_SOGGETTO , session);


		try {
			String provider = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			
			long soggLong = -1;
			// se ho fatto la add 
			if(useIdSogg)
				if(provider != null && !provider.equals("")){
				soggLong = Long.parseLong(provider);
			}
			
			ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(request, pd, session);
			
			String objToRemove = request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			// Elimino i servizioApplicativo dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }

			ServiziApplicativiCore saCore = new ServiziApplicativiCore();
			
			boolean isInUso = false;
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			String msg = "";

			for (int i = 0; i < idsToRemove.size(); i++) {

				// DataElement de = (DataElement) ((Vector<?>) pdold.getDati()
				// .elementAt(idToRemove[i])).elementAt(0);
				// idString = de.getValue();
				// idServApp = Integer.parseInt(idString);

				// Prendo il nome del servizio applicativo
				ServizioApplicativo sa = saCore.getServizioApplicativo(Long.parseLong(idsToRemove.get(i)));
				IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
				idServizioApplicativo.setNome(sa.getNome());
				idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
				
				// Controllo che il sil non sia in uso
				Map<ErrorsHandlerCostant, List<String>> whereIsInUso = new Hashtable<ErrorsHandlerCostant, List<String>>();
				boolean saInUso  = saCore.isServizioApplicativoInUso(idServizioApplicativo, whereIsInUso, saCore.isRegistroServiziLocale());
				
				if (saInUso) {
					isInUso = true;
					msg += DBOggettiInUsoUtils.toString(idServizioApplicativo, whereIsInUso, true, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
					msg += org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE;

				} else {

					// Elimino il sil
					saCore.performDeleteOperation(userLogin, saHelper.smista(), sa);
				}
			}

			if (isInUso) {
				pd.setMessage(msg);
			}

			// Preparo il menu
			saHelper.makeMenu();

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<ServizioApplicativo> lista = null;
			
			if(!useIdSogg){
				int idLista = Liste.SERVIZIO_APPLICATIVO;

				ricerca = saHelper.checkSearchParameters(idLista, ricerca);
				
				if(saCore.isVisioneOggettiGlobale(userLogin)){
					lista = saCore.soggettiServizioApplicativoList(null, ricerca);
				}else{
					lista = saCore.soggettiServizioApplicativoList(userLogin, ricerca);
				}
			}else {
				int idLista = Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO;

				ricerca = saHelper.checkSearchParameters(idLista, ricerca);
				
				lista = saCore.soggettiServizioApplicativoList(ricerca,soggLong);
			}

			saHelper.prepareServizioApplicativoList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForward(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.DEL());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.DEL());
		}
	}
}
