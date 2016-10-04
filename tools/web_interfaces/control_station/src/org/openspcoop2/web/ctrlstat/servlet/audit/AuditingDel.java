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
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.lib.audit.dao.Operation;
import org.openspcoop2.web.lib.audit.web.AuditCostanti;
import org.openspcoop2.web.lib.audit.web.AuditHelper;
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
			ConsoleHelper consoleHelper = new ConsoleHelper(request, pd, session);

			AuditingCore auditingCore = new AuditingCore();
			AuditHelper auditingHelper = new AuditHelper(request, pd, session);

			String datainizio = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_DATA_INIZIO);
			String datafine = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_DATA_FINE);
			String tipooperazione = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OPERAZIONE);
			String tipooggetto = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OGGETTO);
			String id = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_ID);
			String oldid = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_OLD_ID);
			String utente = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_UTENTE);
			String statooperazione = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_STATO_OPERAZIONE);
			String contoggetto = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_CONTENUTO_OGGETTO);

			String objToRemove =  request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 

			// Elimino le operazioni dal db
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);

			for (int i = 0; i < idsToRemove.size(); i++) {

				Operation singleOp = auditingCore.getAuditOperation(Long.parseLong(idsToRemove.get(i)));

				// Elimino l'operazione
				auditingCore.performDeleteOperation(userLogin, consoleHelper.smista(), singleOp);
			}

			// Preparo il menu
			consoleHelper.makeMenu();

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<Operation> lista = auditingCore.auditOperationList(ricerca,
					datainizio, datafine, tipooperazione,
					tipooggetto, id, oldid, utente,
					statooperazione, contoggetto);

			auditingHelper.prepareAuditReportList(ricerca, lista, Liste.AUDIT_REPORT);
 
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, AuditCostanti.OBJECT_NAME_AUDITING,
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AuditCostanti.OBJECT_NAME_AUDITING, ForwardParams.DEL());
		}
	}
}
