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
package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazioneRegola;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ConfigurazioneDumpConfigurazione
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13708 $, $Date: 2018-03-08 10:53:05 +0100 (Thu, 08 Mar 2018) $
 * 
 */
public class PorteApplicativeDumpConfigurazione extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		TipoOperazione tipoOperazione = TipoOperazione.OTHER;

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	


		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;

		try {

			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String id = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();

			boolean showStato = true;
			String statoDump = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_STATO);
			boolean showRealtime = porteApplicativeCore.isDump_showConfigurazioneDumpRealtime();
			String realtime = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_REALTIME);
			String statoDumpRichiesta = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_STATO);
			String statoDumpRisposta = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_STATO);
			String dumpRichiestaIngressoHeader = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS);
			String dumpRichiestaIngressoBody = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY);
			String dumpRichiestaIngressoAttachments = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS);
			String dumpRichiestaUscitaHeader = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_HEADERS);
			String dumpRichiestaUscitaBody = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_BODY);
			String dumpRichiestaUscitaAttachments = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_ATTACHMENTS);
			String dumpRispostaIngressoHeader = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_HEADERS);
			String dumpRispostaIngressoBody = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_BODY);
			String dumpRispostaIngressoAttachments = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_ATTACHMENTS);
			String dumpRispostaUscitaHeader = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_HEADERS);
			String dumpRispostaUscitaBody = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_BODY);
			String dumpRispostaUscitaAttachments = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_ATTACHMENTS);

			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = pa.getNome();

			DumpConfigurazione oldConfigurazione = pa.getDump();
			
			boolean initConfigurazione = false;
			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(CostantiControlStation.PARAMETRO_DUMP_STATO)){
					initConfigurazione = true;
				}
			}

			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE_CONFIG_DI+
						porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE_CONFIG_DI+idporta;
			}

			lstParam.add(new Parameter(labelPerPorta,  null));

			// edit in progress
			if (porteApplicativeHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);

				if(statoDump == null) {
					if(oldConfigurazione == null) {
						statoDump = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
					} else {
						statoDump = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
						realtime = oldConfigurazione.getRealtime().getValue();
						statoDumpRichiesta = porteApplicativeHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, false) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
						statoDumpRisposta = porteApplicativeHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, true) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaIngressoHeader = oldConfigurazione.getRichiestaIngresso().get_value_headers();	
						dumpRichiestaIngressoBody = oldConfigurazione.getRichiestaIngresso().get_value_body();
						dumpRichiestaIngressoAttachments = oldConfigurazione.getRichiestaIngresso().get_value_attachments();
						dumpRichiestaUscitaHeader = oldConfigurazione.getRichiestaUscita().get_value_headers();	
						dumpRichiestaUscitaBody = oldConfigurazione.getRichiestaUscita().get_value_body();
						dumpRichiestaUscitaAttachments = oldConfigurazione.getRichiestaUscita().get_value_attachments();
						dumpRispostaIngressoHeader = oldConfigurazione.getRispostaIngresso().get_value_headers();	
						dumpRispostaIngressoBody = oldConfigurazione.getRispostaIngresso().get_value_body();
						dumpRispostaIngressoAttachments = oldConfigurazione.getRispostaIngresso().get_value_attachments();
						dumpRispostaUscitaHeader = oldConfigurazione.getRispostaUscita().get_value_headers();	
						dumpRispostaUscitaBody = oldConfigurazione.getRispostaUscita().get_value_body();
						dumpRispostaUscitaAttachments = oldConfigurazione.getRispostaUscita().get_value_attachments();
					}
				}
				
				if(initConfigurazione) {
					if(statoDump.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO)) {
						oldConfigurazione = new DumpConfigurazione();
						oldConfigurazione.setRichiestaIngresso(new DumpConfigurazioneRegola());
						oldConfigurazione.setRichiestaUscita(new DumpConfigurazioneRegola());
						oldConfigurazione.setRispostaIngresso(new DumpConfigurazioneRegola());
						oldConfigurazione.setRispostaUscita(new DumpConfigurazioneRegola());
						
						realtime = oldConfigurazione.getRealtime().getValue();
						statoDumpRichiesta = porteApplicativeHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, false) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
						statoDumpRisposta = porteApplicativeHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, true) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaIngressoHeader = oldConfigurazione.getRichiestaIngresso().get_value_headers();	
						dumpRichiestaIngressoBody = oldConfigurazione.getRichiestaIngresso().get_value_body();
						dumpRichiestaIngressoAttachments = oldConfigurazione.getRichiestaIngresso().get_value_attachments();
						dumpRichiestaUscitaHeader = oldConfigurazione.getRichiestaUscita().get_value_headers();	
						dumpRichiestaUscitaBody = oldConfigurazione.getRichiestaUscita().get_value_body();
						dumpRichiestaUscitaAttachments = oldConfigurazione.getRichiestaUscita().get_value_attachments();
						dumpRispostaIngressoHeader = oldConfigurazione.getRispostaIngresso().get_value_headers();	
						dumpRispostaIngressoBody = oldConfigurazione.getRispostaIngresso().get_value_body();
						dumpRispostaIngressoAttachments = oldConfigurazione.getRispostaIngresso().get_value_attachments();
						dumpRispostaUscitaHeader = oldConfigurazione.getRispostaUscita().get_value_headers();	
						dumpRispostaUscitaBody = oldConfigurazione.getRispostaUscita().get_value_body();
						dumpRispostaUscitaAttachments = oldConfigurazione.getRispostaUscita().get_value_attachments();
					}
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				porteApplicativeHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
						dumpRichiestaIngressoHeader, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
						dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
						dumpRispostaIngressoHeader, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments, 
						dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE,	ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.checkDataConfigurazioneDump(tipoOperazione, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, dumpRichiestaIngressoHeader,
					dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody,
					dumpRichiestaUscitaAttachments, dumpRispostaIngressoHeader, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
					dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
			if (!isOk) {

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				porteApplicativeHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
						dumpRichiestaIngressoHeader, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
						dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
						dumpRispostaIngressoHeader, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments, 
						dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE,	ForwardParams.OTHER(""));
			}

			DumpConfigurazione newDumpConfigurazione = null; 
				
			if(statoDump.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO)) {
				newDumpConfigurazione = porteApplicativeHelper.getConfigurazioneDump(tipoOperazione, showStato, statoDump, showRealtime, realtime,
					statoDumpRichiesta, statoDumpRisposta, dumpRichiestaIngressoHeader, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
					dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, dumpRispostaIngressoHeader, 
					dumpRispostaIngressoBody, dumpRispostaIngressoAttachments, dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
					}
			pa.setDump(newDumpConfigurazione);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
			
			pa = porteApplicativeCore.getPortaApplicativa(idInt);
			idporta = pa.getNome();

			// Preparo la lista
			pd.setMessage(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_ARCHIVIO_MESSAGGI_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			ServletUtils.setPageDataTitle(pd, lstParam);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			// ricarico la configurazione
			
			DumpConfigurazione configurazioneAggiornata = pa.getDump();
			
			if(configurazioneAggiornata == null) {
				statoDump = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
			} else {
				statoDump = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
				realtime = configurazioneAggiornata.getRealtime().getValue();
				statoDumpRichiesta = porteApplicativeHelper.isDumpConfigurazioneAbilitato(configurazioneAggiornata, false) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
				statoDumpRisposta = porteApplicativeHelper.isDumpConfigurazioneAbilitato(configurazioneAggiornata, true) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
				dumpRichiestaIngressoHeader = configurazioneAggiornata.getRichiestaIngresso().get_value_headers();	
				dumpRichiestaIngressoBody = configurazioneAggiornata.getRichiestaIngresso().get_value_body();
				dumpRichiestaIngressoAttachments = configurazioneAggiornata.getRichiestaIngresso().get_value_attachments();
				dumpRichiestaUscitaHeader = configurazioneAggiornata.getRichiestaUscita().get_value_headers();	
				dumpRichiestaUscitaBody = configurazioneAggiornata.getRichiestaUscita().get_value_body();
				dumpRichiestaUscitaAttachments = configurazioneAggiornata.getRichiestaUscita().get_value_attachments();
				dumpRispostaIngressoHeader = configurazioneAggiornata.getRispostaIngresso().get_value_headers();	
				dumpRispostaIngressoBody = configurazioneAggiornata.getRispostaIngresso().get_value_body();
				dumpRispostaIngressoAttachments = configurazioneAggiornata.getRispostaIngresso().get_value_attachments();
				dumpRispostaUscitaHeader = configurazioneAggiornata.getRispostaUscita().get_value_headers();	
				dumpRispostaUscitaBody = configurazioneAggiornata.getRispostaUscita().get_value_body();
				dumpRispostaUscitaAttachments = configurazioneAggiornata.getRispostaUscita().get_value_attachments();
			}

			porteApplicativeHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
					dumpRichiestaIngressoHeader, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
					dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
					dumpRispostaIngressoHeader, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments, 
					dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
			
			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE, ForwardParams.OTHER(""));
		}
	}

}
