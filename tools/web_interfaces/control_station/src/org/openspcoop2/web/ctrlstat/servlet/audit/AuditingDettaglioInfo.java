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
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.audit.dao.Operation;
import org.openspcoop2.web.lib.audit.web.AuditCostanti;
import org.openspcoop2.web.lib.audit.web.AuditHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * auditReportInfo
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AuditingDettaglioInfo extends Action {

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

			String type = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_TYPE);
			String idop = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_OPERATION_ID_OP);
			long idlong = Long.parseLong(idop);
			String datainizio = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_DATA_INIZIO);
			String datafine = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_DATA_FINE);
			String tipooperazione = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OPERAZIONE);
			String tipooggetto = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OGGETTO);
			String id = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_ID);
			String oldid = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_OLD_ID);
			String utente = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_UTENTE);
			String statooperazione = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_STATO_OPERAZIONE);
			String contoggetto = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_CONTENUTO_OGGETTO);

			Parameter pDataInizio = new Parameter(AuditCostanti.PARAMETRO_AUDIT_DATA_INIZIO, datainizio);
			Parameter pDataFine = new Parameter(AuditCostanti.PARAMETRO_AUDIT_DATA_FINE, datafine);
			Parameter pTipoOperazione = new Parameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OPERAZIONE, tipooperazione);
			Parameter pTipoOggetto = new Parameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OGGETTO, tipooggetto);
			Parameter pId = new Parameter(AuditCostanti.PARAMETRO_AUDIT_ID, id);
			Parameter pOldId = new Parameter(AuditCostanti.PARAMETRO_AUDIT_OLD_ID, oldid);
			Parameter pUtente = new Parameter(AuditCostanti.PARAMETRO_AUDIT_UTENTE, utente);
			Parameter pStatoOperazione = new Parameter(AuditCostanti.PARAMETRO_AUDIT_STATO_OPERAZIONE, statooperazione);
			Parameter pContoggetto = new Parameter(AuditCostanti.PARAMETRO_AUDIT_CONTENUTO_OGGETTO, contoggetto);
			Parameter pIdOp = new Parameter(AuditCostanti.PARAMETRO_AUDIT_OPERATION_ID_OP, idop);
			Parameter pEnd = new Parameter(ServletUtils.getDataElementForEditModeFinished().getName(), ServletUtils.getDataElementForEditModeFinished().getValue());

			// Preparo il menu
			consoleHelper.makeMenu();

			AuditingCore auditingCore = new AuditingCore();
			AuditHelper ah = new AuditHelper(request, pd, session);

			// setto la barra del titolo
			String label = "";
			if (type.equals(AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_TYPE_ERROR))
				label = AuditCostanti.LABEL_AUDIT_MOTIVO_ERRORE + " di " + idop;
			else
				label = AuditCostanti.LABEL_AUDIT_DETTAGLIO_OGGETTO + " di " + idop;

			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_REPORTISTICA, null));
			lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT, AuditCostanti.SERVLET_NAME_AUDITING));
			lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_OPERAZIONI, AuditCostanti.SERVLET_NAME_AUDITING,
					pDataInizio, pDataFine, pTipoOperazione, pTipoOggetto, pId, pOldId, pUtente, pStatoOperazione, pContoggetto, pEnd));
			lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_DETTAGLIO +" di " + idop, 
					AuditCostanti.SERVLET_NAME_AUDITING_DETTAGLIO, pIdOp, pDataInizio, pDataFine, pTipoOperazione, pTipoOggetto,
					pId, pOldId, pUtente, pStatoOperazione, pContoggetto, pEnd
					)); 
			lstParam.add(new Parameter(label, null));

			ServletUtils.setPageDataTitle(pd, lstParam);

			// Prendo l'operazione
			Operation singleOp = auditingCore.getAuditOperation(idlong);

			// preparo i campi
			Vector<DataElement> dati = null;
			if (type.equals(AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_TYPE_ERROR))
				dati = ah.addAuditReportInfoToDati(
						type, singleOp.getError());
			else
				dati = ah.addAuditReportInfoToDati(
						type, singleOp.getObjectDetails());

			pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, AuditCostanti.OBJECT_NAME_AUDITING_DETTAGLIO, AuditCostanti.TIPO_OPERAZIONE_AUDITING_DETTAGLIO_INFO);

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AuditCostanti.OBJECT_NAME_AUDITING_DETTAGLIO, AuditCostanti.TIPO_OPERAZIONE_AUDITING_DETTAGLIO_INFO);
		}
	}
}
