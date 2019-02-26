/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import org.openspcoop2.web.lib.audit.log.Operation;
import org.openspcoop2.web.lib.audit.web.AuditCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * auditReportDettaglio
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AuditingDettaglio extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			ConsoleHelper auditingHelper = new ConsoleHelper(request, pd, session);

			String idop = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_OPERATION_ID_OP);
			long idlong = Long.parseLong(idop);
			String datainizio = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_DATA_INIZIO);
			String datafine = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_DATA_FINE);
			String tipooperazione = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OPERAZIONE);
			String tipooggetto = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OGGETTO);
			String id = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_ID);
			String oldid = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_OLD_ID);
			String utente = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_UTENTE);
			String statooperazione = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_STATO_OPERAZIONE);
			String contoggetto = auditingHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_CONTENUTO_OGGETTO);

			Parameter pDataInizio = new Parameter(AuditCostanti.PARAMETRO_AUDIT_DATA_INIZIO, datainizio);
			Parameter pDataFine = new Parameter(AuditCostanti.PARAMETRO_AUDIT_DATA_FINE, datafine);
			Parameter pTipoOperazione = new Parameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OPERAZIONE, tipooperazione);
			Parameter pTipoOggetto = new Parameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OGGETTO, tipooggetto);
			Parameter pId = new Parameter(AuditCostanti.PARAMETRO_AUDIT_ID, id);
			Parameter pOldId = new Parameter(AuditCostanti.PARAMETRO_AUDIT_OLD_ID, oldid);
			Parameter pUtente = new Parameter(AuditCostanti.PARAMETRO_AUDIT_UTENTE, utente);
			Parameter pStatoOperazione = new Parameter(AuditCostanti.PARAMETRO_AUDIT_STATO_OPERAZIONE, statooperazione);
			Parameter pContoggetto = new Parameter(AuditCostanti.PARAMETRO_AUDIT_CONTENUTO_OGGETTO, contoggetto);
			Parameter pEnd = new Parameter(ServletUtils.getDataElementForEditModeFinished().getName(), ServletUtils.getDataElementForEditModeFinished().getValue());
			
			// Preparo il menu
			auditingHelper.makeMenu();

			AuditingCore auditingCore = new AuditingCore();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT, AuditCostanti.SERVLET_NAME_AUDITING));
			lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_OPERAZIONI, AuditCostanti.SERVLET_NAME_AUDITING,
					pDataInizio, pDataFine, pTipoOperazione, pTipoOggetto, pId, pOldId, pUtente, pStatoOperazione, pContoggetto, pEnd));
			lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_DETTAGLIO +" di " + idop, null));

			ServletUtils.setPageDataTitle(pd, lstParam);

			// Prendo l'operazione
			Operation singleOp = auditingCore.getAuditOperation(idlong);

			// preparo i campi
			boolean showLinkDocumentiBinari = !auditingCore.isAuditingRegistrazioneElementiBinari();
			Vector<DataElement> dati = auditingHelper.getAuditHelper().addAuditReportDettaglioToDati(
					singleOp, showLinkDocumentiBinari, 
					pDataInizio, pDataFine, pTipoOperazione, pTipoOggetto, pId, pOldId, pUtente, pStatoOperazione, pContoggetto);

			pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward(mapping, AuditCostanti.OBJECT_NAME_AUDITING_DETTAGLIO, AuditCostanti.TIPO_OPERAZIONE_AUDITING_DETTAGLIO);

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AuditCostanti.OBJECT_NAME_AUDITING_DETTAGLIO, AuditCostanti.TIPO_OPERAZIONE_AUDITING_DETTAGLIO);
		}
	}
}
