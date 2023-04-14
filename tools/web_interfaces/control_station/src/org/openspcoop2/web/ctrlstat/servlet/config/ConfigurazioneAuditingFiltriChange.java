/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.config;

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
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.IDBuilder;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.audit.dao.Filtro;
import org.openspcoop2.web.lib.audit.log.constants.Stato;
import org.openspcoop2.web.lib.audit.log.constants.Tipologia;
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
 * filtriChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneAuditingFiltriChange extends Action {

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

			String id = confHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_ID);
			int idFiltro = Integer.parseInt(id);

			String utente = confHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_UTENTE);
			String tipooperazione = confHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OPERAZIONE);
			String tipooggetto = confHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OGGETTO);
			String statooperazione = confHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_STATO_OPERAZIONE);
			String stato = confHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_STATO);
			String tipofiltro = confHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_FILTRO);
			String dump = confHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_DUMP);
			String statoazione = confHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_STATO_AZIONE);
			String dumpazione = confHelper.getParameter(AuditCostanti.PARAMETRO_AUDIT_DUMP_AZIONE);

			ConfigurazioneCore confCore = new ConfigurazioneCore();
			AuditHelper ah = new AuditHelper(request, pd, session);

			IDBuilder idb = new IDBuilder();
			String[] tipiOggTmp = idb.getManagedObjects(true);
			String[] tipiOgg = new String[tipiOggTmp.length+1];
			tipiOgg[0] = "-";
			for (int i = 0; i < tipiOggTmp.length; i++)
				tipiOgg[i+1] = tipiOggTmp[i];

			// Preparo il menu
			confHelper.makeMenu();

			// Prendo il filtro
			Filtro f = confCore.getFiltro((Long.valueOf(idFiltro)).longValue());

			// Se idhid = null, devo visualizzare la pagina per la modifica
			// dati
			if (confHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<>();

				lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT, 
						AuditCostanti.SERVLET_NAME_AUDIT));
				lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_FILTRI, 
						AuditCostanti.SERVLET_NAME_AUDIT_FILTRI_LIST));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_MODIFICA, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				if (utente == null) {
					utente = f.getUsername();
					tipooperazione = f.getTipoOperazione() != null ?
							f.getTipoOperazione().toString() : "-";
							tipooggetto = f.getTipoOggettoInModifica();
							statooperazione = f.getStatoOperazione() != null ?
									f.getStatoOperazione().toString() : "-";
									stato = f.getDump() != null ? AuditCostanti.DEFAULT_VALUE_ABILITATO : AuditCostanti.DEFAULT_VALUE_DISABILITATO;
									tipofiltro = f.isDumpExprRegular() ?
											AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_TIPO_FILTRO_ESPRESSIONE_REGOLARE 
											: AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_TIPO_FILTRO_NORMALE;
									dump = f.getDump();
									statoazione = f.isAuditEnabled() ? AuditCostanti.DEFAULT_VALUE_ABILITATO : AuditCostanti.DEFAULT_VALUE_DISABILITATO;
									dumpazione = f.isDumpEnabled() ? AuditCostanti.DEFAULT_VALUE_ABILITATO : AuditCostanti.DEFAULT_VALUE_DISABILITATO;
				}

				// preparo i campi
				List<DataElement> dati = ah.addFiltroToDati(
						Tipologia.CHANGE, utente, tipooperazione, tipiOgg,
						tipooggetto, statooperazione, stato, tipofiltro,
						dump, statoazione, dumpazione, id);

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						AuditCostanti.OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI,
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			String msg = ah.filtriCheckData(utente, tipooperazione,
					tipooggetto, statooperazione,
					stato, tipofiltro,
					dump, statoazione,
					dumpazione, tipiOgg);
			if (!msg.equals("")) {
				pd.setMessage(msg);

				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<>();

				lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT, 
						AuditCostanti.SERVLET_NAME_AUDIT));
				lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_FILTRI, 
						AuditCostanti.SERVLET_NAME_AUDIT_FILTRI_LIST));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_MODIFICA, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = ah.addFiltroToDati(
						Tipologia.CHANGE, utente, tipooperazione, tipiOgg,
						tipooggetto, statooperazione, stato, tipofiltro,
						dump, statoazione, dumpazione, id);

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						AuditCostanti.OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI,
						ForwardParams.CHANGE());
			}

			// Aggiorno il filtro nel db
			if(utente!=null && !"".equals(utente))
				f.setUsername(utente);
			else
				f.setUsername(null);
			if (tipooperazione != null && !tipooperazione.equals("-"))
				f.setTipoOperazione(Tipologia.toEnumConstant(tipooperazione));
			else
				f.setTipoOperazione(null);
			if (tipooggetto != null && !tipooggetto.equals("-"))
				f.setTipoOggettoInModifica(tipooggetto);
			else
				f.setTipoOggettoInModifica(null);
			if (statooperazione != null && !statooperazione.equals("-"))
				f.setStatoOperazione(Stato.toEnumConstant(statooperazione));
			else
				f.setStatoOperazione(null);
			if (stato.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO)) {
				f.setDumpExprRegular(!tipofiltro.equals(AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_TIPO_FILTRO_NORMALE));
				f.setDump(dump);
			} else {
				f.setDumpExprRegular(false);
				f.setDump(null);
			}
			f.setAuditEnabled(statoazione.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO));
			f.setDumpEnabled(dumpazione.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO));

			confCore.performUpdateOperation(userLogin, confHelper.smista(), f);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			List<Filtro> lista = null;
			lista = confCore.filtriList(ricerca);

			ah.prepareFiltriList(ricerca, lista, Liste.FILTRI);
			pd.setMessage(AuditCostanti.LABEL_AUDIT_CONFIGURAZIONE_MODIFICATA, Costanti.MESSAGE_TYPE_INFO);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					AuditCostanti.OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AuditCostanti.OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI, ForwardParams.CHANGE());
		}  
	}
}
