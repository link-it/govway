/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.web.ctrlstat.servlet.audit;

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
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.IDBuilder;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.lib.audit.dao.Operation;
import org.openspcoop2.web.lib.audit.web.AuditCostanti;
import org.openspcoop2.web.lib.audit.web.AuditHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * auditReport
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public   class Auditing extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);


		try {
			ConsoleHelper consoleHelper = new ConsoleHelper(request, pd, session);

			String datainizio = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_DATA_INIZIO);
			String datafine = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_DATA_FINE);
			String tipooperazione = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OPERAZIONE);
			String tipooggetto = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OGGETTO);
			String id = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_ID);
			String oldid = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_OLD_ID);
			String utente = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_UTENTE);
			String statooperazione = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_STATO_OPERAZIONE);
			String contoggetto = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_CONTENUTO_OGGETTO);

			AuditingCore auditingCore = new AuditingCore();
			AuditHelper auditingHelper = new AuditHelper(request, pd, session);

			// Preparo il menu
			consoleHelper.makeMenu();
			//User user = Utilities.getLoggedUser(session);

			IDBuilder idb = new IDBuilder();
			String[] tipiOggTmp = idb.getManagedObjects(true);
			String[] tipiOgg = new String[tipiOggTmp.length+1];
			tipiOgg[0] = "-";
			for (int i = 0; i < tipiOggTmp.length; i++)
				tipiOgg[i+1] = tipiOggTmp[i];

			// Se datainizio == null, devo visualizzare la pagina con il pulsante
			if (ServletUtils.isEditModeInProgress(request)) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();

				lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_REPORTISTICA, null));
				lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = auditingHelper.addAuditReportToDati(
						"", "", "-", tipiOgg, "-", "", "", "", "-", "");

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, 
						AuditCostanti.OBJECT_NAME_AUDITING, AuditCostanti.TIPO_OPERAZIONE_AUDITING);
			}

			// Controlli sui campi immessi
			String msg = auditingHelper.auditReportCheckData(request, tipiOgg);
			if (!msg.equals("")) {
				pd.setMessage(msg);

				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();

				lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_REPORTISTICA, null));
				lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = auditingHelper.addAuditReportToDati(
						datainizio, datafine, tipooperazione, tipiOgg,
						tipooggetto, id, oldid, utente, statooperazione,
						contoggetto);

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						AuditCostanti.OBJECT_NAME_AUDITING, AuditCostanti.TIPO_OPERAZIONE_AUDITING);
			}

			// ritorno pagina a lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.AUDIT_REPORT;

			ricerca = consoleHelper.checkSearchParameters(idLista, ricerca);

			List<Operation> lista = auditingCore.auditOperationList(ricerca,
					datainizio, datafine, tipooperazione,
					tipooggetto, id, oldid, utente,
					statooperazione, contoggetto);

			auditingHelper.prepareAuditReportList(ricerca, lista, idLista);

			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(session, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					AuditCostanti.OBJECT_NAME_AUDITING, AuditCostanti.TIPO_OPERAZIONE_AUDITING);

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AuditCostanti.OBJECT_NAME_AUDITING, AuditCostanti.TIPO_OPERAZIONE_AUDITING);
		}  
	}
}
