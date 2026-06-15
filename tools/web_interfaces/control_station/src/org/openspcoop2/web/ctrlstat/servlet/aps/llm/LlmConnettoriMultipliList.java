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
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * Servlet di visualizzazione dei {@code ConnettoreLlmProviderRef} appesi al container LLM
 * dell'erogazione (SA Erogatore di una PA specifica/default) o della fruizione
 * (Fruitore registry oppure SA di una PD ridefinita).
 *
 * <p>Vista resa con il template custom {@code attributeAuthority.jsp} (lo stesso usato per
 * le altre liste LLM Provider/Model/Binding): titolo cliccabile + sottotitolo con i modelli
 * abilitati, checkbox per delete batch.</p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class LlmConnettoriMultipliList extends Action {

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
			if (idAspsParam == null || idAspsParam.isEmpty()) {
				throw new IllegalArgumentException("Parametro " + LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_ASPS + " mancante");
			}

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			PorteApplicativeCore paCore = new PorteApplicativeCore(apsCore);
			PorteDelegateCore pdCore = new PorteDelegateCore(apsCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
			AccordoServizioParteSpecifica asps = LlmConnettoriMultipliUtils.loadAsps(apsCore, idAspsParam);

			List<org.openspcoop2.core.registry.ConnettoreLlmProviderRef> registryRefs = null;
			List<org.openspcoop2.core.config.ConnettoreLlmProviderRef> configRefs = null;

			boolean fruitoreCtx = LlmConnettoriMultipliUtils.isFruitoreContext(idFruitoreParam, idSoggettoFruitoreParam);
			if (fruitoreCtx) {
				if (idPortaDelegataParam != null && !idPortaDelegataParam.isEmpty()) {
					org.openspcoop2.core.config.ServizioApplicativo sa =
							LlmConnettoriMultipliUtils.resolveServizioApplicativoFruizione(idPortaDelegataParam, pdCore, saCore);
					configRefs = extractConfigRefs(sa);
				} else {
					Fruitore fr = LlmConnettoriMultipliUtils.resolveFruitore(asps, idFruitoreParam, idSoggettoFruitoreParam);
					registryRefs = extractRegistryRefs(fr == null ? null : fr.getConnettore());
				}
			} else {
				org.openspcoop2.core.config.ServizioApplicativo sa =
						LlmConnettoriMultipliUtils.resolveServizioApplicativoErogazione(asps, idPortaParam, paCore, saCore);
				configRefs = extractConfigRefs(sa);
			}

			List<List<DataElement>> dati = new ArrayList<>();
			if (registryRefs != null) {
				for (org.openspcoop2.core.registry.ConnettoreLlmProviderRef ref : registryRefs) {
					dati.add(buildRowRegistry(ref, idAspsParam, idFruitoreParam, idSoggettoFruitoreParam, idPortaParam, idPortaDelegataParam));
				}
			}
			if (configRefs != null) {
				for (org.openspcoop2.core.config.ConnettoreLlmProviderRef ref : configRefs) {
					dati.add(buildRowConfig(ref, idAspsParam, idFruitoreParam, idSoggettoFruitoreParam, idPortaParam, idPortaDelegataParam));
				}
			}

			// Consuma eventuale messaggio pending lasciato da Add/Change/Del (sopravvive al redirect)
			LlmConnettoriMultipliUtils.consumePendingListMessage(session, pd);

			pd.setCustomListViewName(ConfigurazioneCostanti.CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_NOME_VISTA_CUSTOM_LISTA_ATTRIBUTE_AUTHORITY);
			pd.setLabels(new String[]{ LlmConnettoriMultipliCostanti.LABEL_LLM_CONNETTORI_MULTIPLI });
			pd.setDati(dati);
			pd.setIndex(0);
			pd.setPageSize(dati.size());
			pd.setNumEntries(dati.size());
			pd.setAddButton(true);
			pd.setRemoveButton(true);

			ServletUtils.setPageDataTitle(pd, LlmConnettoriMultipliUtils.buildBreadcrumb(apsHelper, asps, fruitoreCtx,
					LlmConnettoriMultipliCostanti.LABEL_LLM_CONNETTORI_MULTIPLI));

			// Registra in sessione il ListElement con il nostro object name, cosi' il
			// listElement.jsp sa che il bottone Elimina deve puntare a llmConnettoriMultipliDel.do
			// (altrimenti viene riusato il nomeServlet della lista precedente, es. aspsErogazioni).
			List<Parameter> linkParams = new ArrayList<>();
			linkParams.add(new Parameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_ASPS, idAspsParam));
			if (idFruitoreParam != null && !idFruitoreParam.isEmpty()) linkParams.add(new Parameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_FRUITORE, idFruitoreParam));
			if (idSoggettoFruitoreParam != null && !idSoggettoFruitoreParam.isEmpty()) linkParams.add(new Parameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_SOGGETTO_FRUITORE, idSoggettoFruitoreParam));
			if (idPortaParam != null && !idPortaParam.isEmpty()) linkParams.add(new Parameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA, idPortaParam));
			if (idPortaDelegataParam != null && !idPortaDelegataParam.isEmpty()) linkParams.add(new Parameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA_DELEGATA, idPortaDelegataParam));
			ServletUtils.addListElementIntoSession(request, session, LlmConnettoriMultipliCostanti.OBJECT_NAME_LLM_CONNETTORI_MULTIPLI, linkParams);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			return ServletUtils.getStrutsForward(mapping, LlmConnettoriMultipliCostanti.OBJECT_NAME_LLM_CONNETTORI_MULTIPLI,
					ForwardParams.LIST());

		} catch (Exception e) {
			ControlStationCore.logError("Errore esecuzione " + LlmConnettoriMultipliCostanti.OBJECT_NAME_LLM_CONNETTORI_MULTIPLI + " List", e);
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping,
					LlmConnettoriMultipliCostanti.OBJECT_NAME_LLM_CONNETTORI_MULTIPLI, ForwardParams.LIST());
		}
	}

	private static List<org.openspcoop2.core.config.ConnettoreLlmProviderRef> extractConfigRefs(org.openspcoop2.core.config.ServizioApplicativo sa) {
		if (sa == null || sa.getInvocazioneServizio() == null) return null;
		org.openspcoop2.core.config.Connettore c = sa.getInvocazioneServizio().getConnettore();
		if (c == null || c.getConnettoreLlm() == null) return null;
		return c.getConnettoreLlm().getProviderList();
	}

	private static List<org.openspcoop2.core.registry.ConnettoreLlmProviderRef> extractRegistryRefs(org.openspcoop2.core.registry.Connettore c) {
		if (c == null || c.getConnettoreLlm() == null) return null;
		return c.getConnettoreLlm().getProviderList();
	}

	private List<DataElement> buildRowConfig(org.openspcoop2.core.config.ConnettoreLlmProviderRef ref,
			String idAspsParam, String idFruitoreParam, String idSoggettoFruitoreParam,
			String idPortaParam, String idPortaDelegataParam) {
		String providerName = findConfigProperty(ref, CostantiConnettori.CONNETTORE_LLM_POLICY);
		if (providerName == null || providerName.isEmpty()) providerName = ref.getNome() != null ? ref.getNome() : "-";
		String modelli = formatConfigBindings(ref);
		return buildRow(ref.getNome(), providerName, modelli,
				idAspsParam, idFruitoreParam, idSoggettoFruitoreParam, idPortaParam, idPortaDelegataParam);
	}

	private List<DataElement> buildRowRegistry(org.openspcoop2.core.registry.ConnettoreLlmProviderRef ref,
			String idAspsParam, String idFruitoreParam, String idSoggettoFruitoreParam,
			String idPortaParam, String idPortaDelegataParam) {
		String providerName = findRegistryProperty(ref, CostantiConnettori.CONNETTORE_LLM_POLICY);
		if (providerName == null || providerName.isEmpty()) providerName = ref.getNome() != null ? ref.getNome() : "-";
		String modelli = formatRegistryBindings(ref);
		return buildRow(ref.getNome(), providerName, modelli,
				idAspsParam, idFruitoreParam, idSoggettoFruitoreParam, idPortaParam, idPortaDelegataParam);
	}

	private List<DataElement> buildRow(String refNome, String providerName, String modelli,
			String idAspsParam, String idFruitoreParam, String idSoggettoFruitoreParam,
			String idPortaParam, String idPortaDelegataParam) {
		List<DataElement> row = new ArrayList<>();

		Parameter[] changeParams = buildContextParams(idAspsParam, idFruitoreParam, idSoggettoFruitoreParam, idPortaParam, idPortaDelegataParam, refNome);

		// Titolo cliccabile -> Change
		DataElement deTitolo = new DataElement();
		deTitolo.setValue(providerName);
		deTitolo.setIdToRemove(refNome);
		deTitolo.setUrl(LlmConnettoriMultipliCostanti.SERVLET_NAME_LLM_CONNETTORI_MULTIPLI_CHANGE, changeParams);
		row.add(deTitolo);

		// Sottotitolo: lista modelli abilitati
		DataElement deSottotitolo = new DataElement();
		deSottotitolo.setValue("Modelli: " + (modelli != null && !modelli.isEmpty() ? modelli : "-"));
		deSottotitolo.setType(DataElementType.SUBTITLE);
		row.add(deSottotitolo);

		return row;
	}

	private Parameter[] buildContextParams(String idAspsParam, String idFruitoreParam, String idSoggettoFruitoreParam,
			String idPortaParam, String idPortaDelegataParam, String refNome) {
		List<Parameter> params = new ArrayList<>();
		params.add(new Parameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_ASPS, idAspsParam));
		if (idFruitoreParam != null && !idFruitoreParam.isEmpty()) {
			params.add(new Parameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_FRUITORE, idFruitoreParam));
		}
		if (idSoggettoFruitoreParam != null && !idSoggettoFruitoreParam.isEmpty()) {
			params.add(new Parameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_SOGGETTO_FRUITORE, idSoggettoFruitoreParam));
		}
		if (idPortaParam != null && !idPortaParam.isEmpty()) {
			params.add(new Parameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA, idPortaParam));
		}
		if (idPortaDelegataParam != null && !idPortaDelegataParam.isEmpty()) {
			params.add(new Parameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA_DELEGATA, idPortaDelegataParam));
		}
		params.add(new Parameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_PROVIDER_NOME, refNome));
		return params.toArray(new Parameter[0]);
	}

	private static String findConfigProperty(org.openspcoop2.core.config.ConnettoreLlmProviderRef ref, String name) {
		if (ref == null || ref.getPropertyList() == null || name == null) return null;
		for (org.openspcoop2.core.config.Property p : ref.getPropertyList()) {
			if (name.equals(p.getNome())) return p.getValore();
		}
		return null;
	}

	private static String findRegistryProperty(org.openspcoop2.core.registry.ConnettoreLlmProviderRef ref, String name) {
		if (ref == null || ref.getPropertyList() == null || name == null) return null;
		for (org.openspcoop2.core.registry.Property p : ref.getPropertyList()) {
			if (name.equals(p.getNome())) return p.getValore();
		}
		return null;
	}

	private static String formatConfigBindings(org.openspcoop2.core.config.ConnettoreLlmProviderRef ref) {
		if (ref == null || ref.getBindingList() == null || ref.getBindingList().isEmpty()) return null;
		StringBuilder sb = new StringBuilder();
		for (org.openspcoop2.core.config.ConnettoreLlmBinding b : ref.getBindingList()) {
			if (sb.length() > 0) sb.append(", ");
			sb.append(b.getNome());
		}
		return sb.toString();
	}

	private static String formatRegistryBindings(org.openspcoop2.core.registry.ConnettoreLlmProviderRef ref) {
		if (ref == null || ref.getBindingList() == null || ref.getBindingList().isEmpty()) return null;
		StringBuilder sb = new StringBuilder();
		for (org.openspcoop2.core.registry.ConnettoreLlmBinding b : ref.getBindingList()) {
			if (sb.length() > 0) sb.append(", ");
			sb.append(b.getNome());
		}
		return sb.toString();
	}

}
