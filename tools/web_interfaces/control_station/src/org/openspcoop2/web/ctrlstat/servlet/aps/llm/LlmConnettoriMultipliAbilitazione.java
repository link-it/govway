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
package org.openspcoop2.web.ctrlstat.servlet.aps.llm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * Servlet di "abilitazione" della modalita' multi-provider LLM. In questa
 * iterazione la modalita' e' implicita dal numero di {@code ConnettoreLlmProviderRef}
 * salvati sul container: questa servlet fa da entry point dalla rotella
 * "Configurazione Connettori LLM" della maschera connettore e inoltra
 * direttamente alla {@link LlmConnettoriMultipliList}.
 *
 * <p>In futuro potra' esporre un toggle persistente (es. property
 * {@code llm-multi-mode} sul container) se serve mantenere la "vista lista"
 * anche con un solo provider.</p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class LlmConnettoriMultipliAbilitazione extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);
		PageData pd = new PageData();
		GeneralHelper generalHelper = new GeneralHelper(session);
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			apsHelper.makeMenu();

			// Per ora la modalita' multi-provider e' attiva implicitamente quando il container
			// LLM contiene almeno un provider: forward diretto alla list. Lasciamo il punto di
			// estensione per supportare in futuro un toggle persistente (vedi javadoc classe).
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			return LlmConnettoriMultipliUtils.redirectToList(apsHelper);

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping,
					LlmConnettoriMultipliCostanti.OBJECT_NAME_LLM_CONNETTORI_MULTIPLI_ABILITAZIONE, ForwardParams.OTHER(TipoOperazione.OTHER.toString()));
		}
	}
}
