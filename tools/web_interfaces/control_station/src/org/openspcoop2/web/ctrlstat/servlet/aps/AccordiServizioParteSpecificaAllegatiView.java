/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.aps;

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
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * serviziAllegatiView
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaAllegatiView extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		//	@SuppressWarnings("unused")
		//	String userLogin = ServletUtils.getUserLoginFromSession(session);

		try {

			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);

			String idAllegato = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_ALLEGATO);
			String idServizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			String nomeDocumento = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_DOCUMENTO);
			int idAllegatoInt = Integer.parseInt(idAllegato);
			int idServizioInt = Integer.parseInt(idServizio);

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			ArchiviCore archiviCore = new ArchiviCore(apsCore);

			String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
			}
			
			// Preparo il menu
			apsHelper.makeMenu();

			// Prendo il nome
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Long.valueOf(idServizioInt));

			String tmpTitle = apsHelper.getLabelIdServizio(asps);

			Documento doc = archiviCore.getDocumento(idAllegatoInt,true);

			StringBuffer contenutoAllegato = new StringBuffer();
			String errore = Utilities.getTestoVisualizzabile(doc.getByteContenuto(),contenutoAllegato);

			// setto la barra del titolo
			List<Parameter> lstParm = new ArrayList<Parameter>();

			if(gestioneFruitori) {
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			}
			else {
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			}
			lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI_DI + tmpTitle,
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ALLEGATI_LIST, 
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, idServizio)
					));
			lstParm.add(new Parameter(nomeDocumento, null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParm );


			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, idServizio, null, null, dati);

			dati = apsHelper.addViewAllegatiToDati(TipoOperazione.OTHER, idAllegato, idServizio, doc, contenutoAllegato, errore,
					dati);

			pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI,
					AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_VIEW);

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI,
					AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_VIEW);
		}  
	}


}
