/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.audit;

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
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.audit.log.Operation;
import org.openspcoop2.web.lib.audit.web.AuditCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * auditReportDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AuditingDel extends Action {

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
			AuditingCore auditingCore = new AuditingCore();
			ConsoleHelper auditingHelper = new ConsoleHelper(request, pd, session);

			String datainizio = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_DATA_INIZIO);
			String datafine = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_DATA_FINE);
			String tipooperazione = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OPERAZIONE);
			String tipooggetto = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OGGETTO);
			String id = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_ID);
			String oldid = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_OLD_ID);
			String utente = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_UTENTE);
			String statooperazione = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_STATO_OPERAZIONE);
			String contoggetto = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_CONTENUTO_OGGETTO);

			String objToRemove =  auditingHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 

			// Elimino le operazioni dal db
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);

			for (int i = 0; i < idsToRemove.size(); i++) {

				Operation singleOp = auditingCore.getAuditOperation(Long.parseLong(idsToRemove.get(i)));

				// Elimino l'operazione
				auditingCore.performDeleteOperation(userLogin, auditingHelper.smista(), singleOp);
			}

			// Preparo il menu
			auditingHelper.makeMenu();

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			List<Operation> lista = auditingCore.auditOperationList(ricerca,
					datainizio, datafine, tipooperazione,
					tipooggetto, id, oldid, utente,
					statooperazione, contoggetto);

			auditingHelper.getAuditHelper().prepareAuditReportList(ricerca, lista, Liste.AUDIT_REPORT,
					datainizio, datafine,
					tipooperazione, tipooggetto,
					id, oldid,
					utente, statooperazione,
					contoggetto);
 
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, AuditCostanti.OBJECT_NAME_AUDITING,
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AuditCostanti.OBJECT_NAME_AUDITING, ForwardParams.DEL());
		}
	}
}
