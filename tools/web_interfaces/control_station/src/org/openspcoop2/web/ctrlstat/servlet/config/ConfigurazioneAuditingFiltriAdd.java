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


package org.openspcoop2.web.ctrlstat.servlet.config;

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
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.audit.AuditingCore;
import org.openspcoop2.web.lib.audit.costanti.StatoOperazione;
import org.openspcoop2.web.lib.audit.costanti.TipoOperazione;
import org.openspcoop2.web.lib.audit.dao.Configurazione;
import org.openspcoop2.web.lib.audit.dao.Filtro;
import org.openspcoop2.web.lib.audit.web.AuditCostanti;
import org.openspcoop2.web.lib.audit.web.AuditHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * filtriAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneAuditingFiltriAdd extends Action {

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
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);

			String utente = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_UTENTE);
			String tipooperazione = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OPERAZIONE);
			String tipooggetto = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OGGETTO);
			String statooperazione = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_STATO_OPERAZIONE);
			String stato = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_STATO);
			String tipofiltro = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_FILTRO);
			String dump = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_DUMP);
			String statoazione = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_STATO_AZIONE);
			String dumpazione = request.getParameter(AuditCostanti.PARAMETRO_AUDIT_DUMP_AZIONE);

			ConfigurazioneCore confCore = new ConfigurazioneCore();
			AuditingCore auditingCore = new AuditingCore(confCore);
			AuditHelper ah = new AuditHelper(request, pd, session);

			IDBuilder idb = new IDBuilder();
			String[] tipiOggTmp = idb.getManagedObjects(true);
			String[] tipiOgg = new String[tipiOggTmp.length+1];
			tipiOgg[0] = "-";
			for (int i = 0; i < tipiOggTmp.length; i++)
				tipiOgg[i+1] = tipiOggTmp[i];

			// Preparo il menu
			confHelper.makeMenu();

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (ServletUtils.isEditModeInProgress(request)) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();

				lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_CONFIGURAZIONE, null));
				lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT, 
						AuditCostanti.SERVLET_NAME_AUDIT));
				lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_FILTRI, 
						AuditCostanti.SERVLET_NAME_AUDIT_FILTRI_LIST));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				if (utente == null) {
					Configurazione conf = auditingCore.getConfigurazioneAudit();
					utente = "";
					tipooperazione = "-";
					tipooggetto = "-";
					statooperazione = "-";
					stato = AuditCostanti.DEFAULT_VALUE_DISABILITATO;
					tipofiltro = AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_TIPO_FILTRO_NORMALE;
					dump = "";
					statoazione = conf.isAuditEnabled() ? 
							AuditCostanti.DEFAULT_VALUE_ABILITATO : AuditCostanti.DEFAULT_VALUE_DISABILITATO;
					dumpazione = conf.isDumpEnabled() ?
							AuditCostanti.DEFAULT_VALUE_ABILITATO : AuditCostanti.DEFAULT_VALUE_DISABILITATO;
				}
				Vector<DataElement> dati = ah.addFiltroToDati(
						TipoOperazione.ADD, utente, tipooperazione, tipiOgg,
						tipooggetto, statooperazione, stato, tipofiltro,
						dump, statoazione, dumpazione, "0");

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						AuditCostanti.OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			String msg = ah.filtriCheckData(request, tipiOgg);
			if (!msg.equals("")) {
				pd.setMessage(msg);

				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();

				lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_CONFIGURAZIONE, null));
				lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT, 
						AuditCostanti.SERVLET_NAME_AUDIT));
				lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_FILTRI, 
						AuditCostanti.SERVLET_NAME_AUDIT_FILTRI_LIST));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = ah.addFiltroToDati(
						TipoOperazione.ADD, utente, tipooperazione, tipiOgg,
						tipooggetto, statooperazione, stato, tipofiltro,
						dump, statoazione, dumpazione, "0");

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						AuditCostanti.OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI,
						ForwardParams.ADD());
			}

			// Inserisco il filtro nel db
			Filtro f = new Filtro();
			if (utente != null && !utente.equals(""))
				f.setUsername(utente);
			if (tipooperazione != null && !tipooperazione.equals("-"))
				f.setTipoOperazione(TipoOperazione.parseString(tipooperazione));
			if (tipooggetto != null && !tipooggetto.equals("-"))
				f.setTipoOggettoInModifica(tipooggetto);
			if (statooperazione != null && !statooperazione.equals("-"))
				f.setStatoOperazione(StatoOperazione.parseString(statooperazione));
			if (stato.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO)) {
				if (tipofiltro.equals(AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_TIPO_FILTRO_NORMALE))
					f.setDumpExprRegular(false);
				else
					f.setDumpExprRegular(true);
				f.setDump(dump);
			} else
				f.setDumpExprRegular(false);
			f.setAuditEnabled(statoazione.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO) ? true : false);
			f.setDumpEnabled(dumpazione.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO) ? true : false);

			confCore.performCreateOperation(userLogin, confHelper.smista(), f);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<Filtro> lista = null;
			lista = confCore.filtriList(ricerca);

			ah.prepareFiltriList(ricerca, lista, Liste.FILTRI);
			pd.setMessage(AuditCostanti.LABEL_AUDIT_CONFIGURAZIONE_MODIFICATA);
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					AuditCostanti.OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AuditCostanti.OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI, ForwardParams.ADD());
		}
	}
}
