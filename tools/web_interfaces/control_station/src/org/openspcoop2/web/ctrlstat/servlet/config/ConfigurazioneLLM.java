/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * Entry-point della sezione "LLM" del menu di configurazione: hub che mostra
 * i tre sotto-link (Provider, Modelli, Provider Binding) con il conteggio
 * delle istanze configurate in {@code generic_properties}.
 *
 * Pattern aderente a {@link ConfigurazioneControlloTraffico}: la pagina e' in
 * sola visualizzazione (nessun campo editabile), il rendering e' delegato a
 * {@code ConfigurazioneHelper.addConfigurazioneLLMToDati}.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class ConfigurazioneLLM extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		TipoOperazione tipoOperazione = TipoOperazione.OTHER;

		HttpSession session = request.getSession(true);

		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);

			confHelper.makeMenu();

			ConfigurazioneCore confCore = new ConfigurazioneCore();

			long sizeProvider = confCore.gestorePolicyTokenList(null,
					org.openspcoop2.pdd.core.llm.provider.Costanti.TIPOLOGIA, null).size();
			long sizeModel = confCore.gestorePolicyTokenList(null,
					org.openspcoop2.pdd.core.llm.provider.Costanti.TIPOLOGIA_MODEL, null).size();
			long sizeProviderBinding = confCore.gestorePolicyTokenList(null,
					org.openspcoop2.pdd.core.llm.provider.Costanti.TIPOLOGIA_PROVIDER_BINDING, null).size();

			List<Parameter> lstParam = new ArrayList<>();
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_LLM, null));

			ServletUtils.setPageDataTitle(pd, lstParam);

			List<DataElement> dati = new ArrayList<>();
			dati.add(ServletUtils.getDataElementForEditModeFinished());

			confHelper.addConfigurazioneLLMToDati(dati, tipoOperazione, sizeProvider, sizeModel, sizeProviderBinding);

			pd.setDati(dati);
			pd.disableEditMode();

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_LLM, ForwardParams.OTHER(""));

		} catch (Exception e) {
			ControlStationCore.logError("ConfigurazioneLLM (hub): " + e.getMessage(), e);
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_LLM, ForwardParams.OTHER(""));
		}
	}
}
