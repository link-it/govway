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


package org.openspcoop2.web.ctrlstat.servlet.archivi;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.pdd.logger.DriverMsgDiagnostici;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * diagnosticaTesto
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class DiagnosticaTesto extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		// Prendo il datasource
		ArchiviCore archiviCore = new ArchiviCore();
		String nomeDs = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE);
		DriverMsgDiagnostici driverMsgDiagnostici = archiviCore.getDriverMSGDiagnostici(nomeDs);

		try {
			ArchiviHelper archiviHelper = new ArchiviHelper(request, pd, session);

			String iddiagnostico = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID);
			long idLong = Long.parseLong(iddiagnostico);
			String severita = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DIAGNOSTICI_SEVERITA);
			String idfunzione = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_FUNZIONE);
			String datainizio = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO);
			String datafine = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE);
			String tipo_servizio = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO);
			String servizio = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO);
			String tipo_mittente = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE);
			String nome_mittente = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE);
			String tipo_destinatario = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO);
			String nome_destinatario = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO);
			String nome_azione = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE);
			String correlazioneApplicativa = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA);
			String idMessaggio = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO);
			String idMessaggioRisposta = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_RISPOSTA);
			String idporta = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_PORTA);
			String protocollo = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
			String identificativoMessaggioSearch = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH);

			Parameter pDs = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE, (nomeDs==null ? "" : nomeDs));
			Parameter pSev = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DIAGNOSTICI_SEVERITA,severita);
			Parameter pIdFun = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_FUNZIONE,idfunzione);
			Parameter pDataI = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO,(datainizio == null ? "" : datainizio) );
			Parameter pDataF = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE,(datafine == null ? "" : datafine) );
			Parameter pTipoM = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE,(tipo_mittente == null ? "" : tipo_mittente) );
			Parameter pNomeM = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE, (nome_mittente == null ? "" : nome_mittente));
			Parameter pTipoD = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO, (tipo_destinatario == null ? "" : tipo_destinatario) );
			Parameter pNomeD = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO,(nome_destinatario == null ? "" : nome_destinatario) );
			Parameter pTipoS = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO, (tipo_servizio == null ? "" : tipo_servizio) );
			Parameter pNomeS = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO,(servizio == null ? "" : servizio) );
			Parameter pAzione = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE,(nome_azione == null ? "" : nome_azione));
			Parameter pCorrAppl = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA,(correlazioneApplicativa == null ? "" : correlazioneApplicativa));
			Parameter pProt = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO, (protocollo == null ? "" : protocollo));
			Parameter pIdentMsg = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH, (identificativoMessaggioSearch == null ? "" : identificativoMessaggioSearch));
			Parameter pEditMode = new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME, Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END);

			// Preparo il menu
			archiviHelper.makeMenu();

			// Prendo i dati del messaggio diagnostico
			FiltroRicercaDiagnosticiConPaginazione filtro = new FiltroRicercaDiagnosticiConPaginazione();
			filtro.addProperty(org.openspcoop2.protocol.basic.diagnostica.DriverMsgDiagnostici.IDDIAGNOSTICI, idLong+"");
			List<MsgDiagnostico> list = driverMsgDiagnostici.getMessaggiDiagnostici(filtro);
			if(list.size()>1){
				throw new Exception("Esiste piu' di un messaggio diagnostico con id ["+idLong+"]???");
			}
			MsgDiagnostico msgDiag = list.get(0);

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, 
					new Parameter(ArchiviCostanti.LABEL_REPORTISTICA,null),
					new Parameter(ArchiviCostanti.LABEL_DIAGNOSTICA,ArchiviCostanti.SERVLET_NAME_ARCHIVI_DIAGNOSTICA,
							pDs, pSev,pIdFun, pDataI, pDataF, pTipoM, pNomeM, pTipoD, pNomeD, pTipoS, pNomeS, pAzione, pCorrAppl, pProt, pIdentMsg),
							new Parameter(ArchiviCostanti.LABEL_MESSAGGI,ArchiviCostanti.SERVLET_NAME_ARCHIVI_DIAGNOSTICA,
									pDs, pSev,pIdFun, pDataI, pDataF, pTipoM, pNomeM, pTipoD, pNomeD, pTipoS, pNomeS, pAzione, pCorrAppl, pProt, pIdentMsg, pEditMode),
									new Parameter(ArchiviCostanti.LABEL_TESTO_DIAGNOSTICO,null));

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati.addElement(ServletUtils.getDataElementForEditModeFinished());

			archiviHelper.addDiagnosticaTestoToDati(dati, msgDiag, idMessaggio, idMessaggioRisposta, idporta);

			pd.disableEditMode();
			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForward(mapping, ArchiviCostanti.OBJECT_NAME_ARCHIVI_DIAGNOSTICA_TESTO, 
					ArchiviCostanti.TIPO_OPERAZIONE_DIAGNOSTICA_TESTO);

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ArchiviCostanti.OBJECT_NAME_ARCHIVI_DIAGNOSTICA_TESTO,
					ArchiviCostanti.TIPO_OPERAZIONE_DIAGNOSTICA_TESTO);

		}
	}
}
