/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
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
 * diagnosticaAppenderChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneDiagnosticaAppenderChange extends Action {

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
			
			// Preparo il menu
			confHelper.makeMenu();

			String id = confHelper.getParametroLong(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID);
			int idInt = Integer.parseInt(id);
			String tipo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO);

			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Prendo l'appender
			Configurazione newConfigurazione = confCore.getConfigurazioneGenerale();
			MessaggiDiagnostici md = newConfigurazione.getMessaggiDiagnostici();
			OpenspcoopAppender oa = null;
			for (int j = 0; j < md.sizeOpenspcoopAppenderList(); j++) {
				oa = md.getOpenspcoopAppender(j);
				if (idInt == oa.getId().intValue()) {
					break;
				}
			}

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if (confHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<>();

				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_APPENDER_MESSAGGI_DIAGNOSTICI , 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_APPENDER_LIST));
				lstParam.add(new Parameter(oa.getTipo(), null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// Prendo i dati dal db
				tipo = oa.getTipo();

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = confHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, null, null, dati);

				dati = confHelper.addTipoDiagnosticaAppenderToDati(TipoOperazione.CHANGE, oa.getTipo(), dati, oa.getId() + "", oa.sizePropertyList());

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DIAGNOSTICA_APPENDER, 
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.diagnosticaAppenderCheckData(TipoOperazione.CHANGE);
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<>();

				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_APPENDER_MESSAGGI_DIAGNOSTICI , 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_APPENDER_LIST));
				lstParam.add(new Parameter(oa.getTipo(), null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, null, null, dati);

				dati = confHelper.addTipoDiagnosticaAppenderToDati(TipoOperazione.CHANGE, tipo,
						dati, oa.getId() + "", oa.sizePropertyList());


				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DIAGNOSTICA_APPENDER, 
						ForwardParams.CHANGE());
			}

			// Modifico i dati dell'appender nel db
			for (int i = 0; i < md.sizeOpenspcoopAppenderList(); i++) {
				OpenspcoopAppender tmpOa = md.getOpenspcoopAppender(i);
				if (idInt == tmpOa.getId().intValue()) {
					md.removeOpenspcoopAppender(i);
					break;
				}
			}

			OpenspcoopAppender newOa = new OpenspcoopAppender();
			newOa.setTipo(tipo);
			md.addOpenspcoopAppender(newOa);
			newConfigurazione.setMessaggiDiagnostici(md);

			confCore.performUpdateOperation(userLogin, confHelper.smista(), newConfigurazione);

			// Preparo la lista
			newConfigurazione = confCore.getConfigurazioneGenerale();
			md = newConfigurazione.getMessaggiDiagnostici();

			confHelper.prepareDiagnosticaAppenderList(md.getOpenspcoopAppenderList());

			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_APPENDER_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DIAGNOSTICA_APPENDER,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DIAGNOSTICA_APPENDER, ForwardParams.CHANGE());
		}
	}
}
