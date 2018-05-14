/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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


package org.openspcoop2.web.ctrlstat.servlet.ac;

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
import org.openspcoop2.core.registry.AccordoCooperazione;
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
 * accordiCoopAllegatiView
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiCooperazioneAllegatiView extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {

			AccordiCooperazioneHelper acHelper = new AccordiCooperazioneHelper(request, pd, session);

			String idAllegato = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ALLEGATO);
			String idAccordo = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ACCORDO);
			String nomeDocumento = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME_DOCUMENTO);
			int idAllegatoInt = Integer.parseInt(idAllegato);
			int idAccordoInt = Integer.parseInt(idAccordo);

			String tipoSICA = acHelper.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_SICA);
			if("".equals(tipoSICA))
				tipoSICA = null;

			AccordiCooperazioneCore acCore = new AccordiCooperazioneCore();
			ArchiviCore archiviCore = new ArchiviCore(acCore);

			// Preparo il menu
			acHelper.makeMenu();

			// Prendo il nome
			AccordoCooperazione ac = acCore.getAccordoCooperazione(Long.valueOf(idAccordoInt));
			String titleAS = acHelper.getLabelIdAccordoCooperazione(ac);

			Documento doc = archiviCore.getDocumento(idAllegatoInt,true);

			StringBuffer contenutoAllegato = new StringBuffer();
			String errore = Utilities.getTestoVisualizzabile(doc.getByteContenuto(),contenutoAllegato);

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();
			lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
			lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATI_DI+ titleAS,
					AccordiCooperazioneCostanti.SERVLET_NAME_AC_ALLEGATI_LIST,
					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, idAccordo),
					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME, ac.getNome())));
			lstParam.add(new Parameter(nomeDocumento, null));
			
			ServletUtils.setPageDataTitle(pd, lstParam);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati=	acHelper.addAllegatiToDati(TipoOperazione.OTHER,
					idAllegato, idAccordo, doc, contenutoAllegato, errore, dati,null,false);

			pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_AC_ALLEGATI, 
					AccordiCooperazioneCostanti.TIPO_OPERAZIONE_VIEW);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiCooperazioneCostanti.OBJECT_NAME_AC_ALLEGATI, 
					AccordiCooperazioneCostanti.TIPO_OPERAZIONE_VIEW);
		}  
	}


}
