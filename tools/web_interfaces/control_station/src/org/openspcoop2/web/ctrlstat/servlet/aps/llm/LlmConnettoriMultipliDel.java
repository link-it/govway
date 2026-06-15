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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * Servlet di rimozione di uno o piu' {@code ConnettoreLlmProviderRef} dal container LLM
 * "corrente" (cfr. {@link LlmConnettoriMultipliUtils}).
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class LlmConnettoriMultipliDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);
		PageData pd = new PageData();
		GeneralHelper generalHelper = new GeneralHelper(session);
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			apsHelper.makeMenu();

			String idAspsParam = apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_ASPS);
			String idFruitoreParam = apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_FRUITORE);
			String idSoggettoFruitoreParam = apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_SOGGETTO_FRUITORE);
			String idPortaParam = apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA);
			String idPortaDelegataParam = apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA_DELEGATA);
			if (idAspsParam == null) {
				throw new IllegalArgumentException("Parametro " + LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_ASPS + " mancante");
			}

			Set<String> nomiDaRimuovere = new HashSet<>();
			String singolo = apsHelper.getParameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_PROVIDER_NOME);
			if (singolo != null && !singolo.isEmpty()) {
				nomiDaRimuovere.add(singolo);
			}
			String objToRemove = apsHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			if (objToRemove != null && !objToRemove.isEmpty()) {
				List<String> ids = Utilities.parseIdsToRemove(objToRemove);
				if (ids != null) {
					nomiDaRimuovere.addAll(ids);
				}
			}

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			PorteApplicativeCore paCore = new PorteApplicativeCore(apsCore);
			PorteDelegateCore pdCore = new PorteDelegateCore(apsCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
			AccordoServizioParteSpecifica asps = LlmConnettoriMultipliUtils.loadAsps(apsCore, idAspsParam);

			boolean fruitoreCtx = LlmConnettoriMultipliUtils.isFruitoreContext(idFruitoreParam, idSoggettoFruitoreParam);
			boolean useRegistry = fruitoreCtx && (idPortaDelegataParam == null || idPortaDelegataParam.isEmpty());

			List<String> rimossi = new ArrayList<>();
			List<String> nonTrovati = new ArrayList<>();
			String user = ServletUtils.getUserLoginFromSession(session);

			if (useRegistry) {
				Fruitore fr = LlmConnettoriMultipliUtils.resolveFruitore(asps, idFruitoreParam, idSoggettoFruitoreParam);
				if (fr == null || fr.getConnettore() == null || fr.getConnettore().getConnettoreLlm() == null) {
					throw new IllegalStateException("Container LLM non trovato");
				}
				List<org.openspcoop2.core.registry.ConnettoreLlmProviderRef> providerList = fr.getConnettore().getConnettoreLlm().getProviderList();
				int rimanenti = providerList.size() - countMatches(providerList, nomiDaRimuovere);
				if (rimanenti < 1) {
					LlmConnettoriMultipliUtils.setPendingListMessage(session,
							"Operazione annullata: deve restare almeno un provider LLM configurato", Costanti.MESSAGE_TYPE_ERROR);
					return LlmConnettoriMultipliUtils.redirectToList(apsHelper);
				}
				for (String nome : nomiDaRimuovere) {
					if (removeRegistry(providerList, nome)) rimossi.add(nome);
					else nonTrovati.add(nome);
				}
				if (!rimossi.isEmpty()) {
					apsCore.performUpdateOperation(user, apsHelper.smista(), asps);
				}
			} else {
				org.openspcoop2.core.config.ServizioApplicativo sa = fruitoreCtx
						? LlmConnettoriMultipliUtils.resolveServizioApplicativoFruizione(idPortaDelegataParam, pdCore, saCore)
						: LlmConnettoriMultipliUtils.resolveServizioApplicativoErogazione(asps, idPortaParam, paCore, saCore);
				if (sa == null || sa.getInvocazioneServizio() == null || sa.getInvocazioneServizio().getConnettore() == null
						|| sa.getInvocazioneServizio().getConnettore().getConnettoreLlm() == null) {
					throw new IllegalStateException("Container LLM non trovato");
				}
				List<org.openspcoop2.core.config.ConnettoreLlmProviderRef> providerList = sa.getInvocazioneServizio().getConnettore().getConnettoreLlm().getProviderList();
				int rimanenti = providerList.size() - countMatchesConfig(providerList, nomiDaRimuovere);
				if (rimanenti < 1) {
					LlmConnettoriMultipliUtils.setPendingListMessage(session,
							"Operazione annullata: deve restare almeno un provider LLM configurato", Costanti.MESSAGE_TYPE_ERROR);
					return LlmConnettoriMultipliUtils.redirectToList(apsHelper);
				}
				for (String nome : nomiDaRimuovere) {
					if (removeConfig(providerList, nome)) rimossi.add(nome);
					else nonTrovati.add(nome);
				}
				if (!rimossi.isEmpty()) {
					saCore.performUpdateOperation(user, apsHelper.smista(), sa);
				}
			}

			if (rimossi.isEmpty()) {
				LlmConnettoriMultipliUtils.setPendingListMessage(session,
						"Nessun provider rimosso" + (nonTrovati.isEmpty() ? "" : " (" + nonTrovati.size() + " non trovati)"),
						Costanti.MESSAGE_TYPE_ERROR);
			} else {
				LlmConnettoriMultipliUtils.setPendingListMessage(session,
						rimossi.size() + " provider rimosso/i correttamente"
								+ (nonTrovati.isEmpty() ? "" : " (" + nonTrovati.size() + " non trovati)"),
						Costanti.MESSAGE_TYPE_INFO);
			}

			return LlmConnettoriMultipliUtils.redirectToList(apsHelper);

		} catch (Exception e) {
			ControlStationCore.logError("Errore esecuzione " + LlmConnettoriMultipliCostanti.OBJECT_NAME_LLM_CONNETTORI_MULTIPLI + " Del", e);
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping,
					LlmConnettoriMultipliCostanti.OBJECT_NAME_LLM_CONNETTORI_MULTIPLI, ForwardParams.DEL());
		}
	}

	private static boolean removeConfig(List<org.openspcoop2.core.config.ConnettoreLlmProviderRef> list, String nome) {
		Iterator<org.openspcoop2.core.config.ConnettoreLlmProviderRef> it = list.iterator();
		while (it.hasNext()) {
			if (nome.equals(it.next().getNome())) {
				it.remove();
				return true;
			}
		}
		return false;
	}

	private static int countMatchesConfig(List<org.openspcoop2.core.config.ConnettoreLlmProviderRef> list, java.util.Set<String> nomi) {
		int c = 0;
		for (org.openspcoop2.core.config.ConnettoreLlmProviderRef p : list) {
			if (nomi.contains(p.getNome())) c++;
		}
		return c;
	}

	private static int countMatches(List<org.openspcoop2.core.registry.ConnettoreLlmProviderRef> list, java.util.Set<String> nomi) {
		int c = 0;
		for (org.openspcoop2.core.registry.ConnettoreLlmProviderRef p : list) {
			if (nomi.contains(p.getNome())) c++;
		}
		return c;
	}

	private static boolean removeRegistry(List<org.openspcoop2.core.registry.ConnettoreLlmProviderRef> list, String nome) {
		Iterator<org.openspcoop2.core.registry.ConnettoreLlmProviderRef> it = list.iterator();
		while (it.hasNext()) {
			if (nome.equals(it.next().getNome())) {
				it.remove();
				return true;
			}
		}
		return false;
	}
}
