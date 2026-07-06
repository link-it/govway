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
package org.openspcoop2.pdd.core.llm.pii;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;

/**
 * Risolve le Regole PII ({@code generic_properties} tipologia {@code llmPiiMasking}) da
 * applicare in base alla configurazione del Provider Binding.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class PiiConfigResolver {

	private PiiConfigResolver() {}

	/**
	 * Risolve le Regole PII applicabili:
	 * <ul>
	 *   <li>solo tipi selezionati nel binding;</li>
	 *   <li>per il tipo {@code regexp}: tutte se {@code regexpRefsAll}, altrimenti solo quelle referenziate;</li>
	 *   <li>per gli altri tipi (plugin): tutte le regole del tipo (nessuna selezione per-tipo in UI).</li>
	 * </ul>
	 */
	public static List<PiiRuleConfig> resolve(ConfigurazionePdDManager mgr, PiiBindingConfig cfg) throws PiiMaskingException {
		List<PiiRuleConfig> result = new ArrayList<>();
		if (cfg == null || !cfg.isEnabled() || cfg.getTypes().isEmpty()) {
			return result;
		}
		List<GenericProperties> all;
		try {
			all = mgr.getGenericProperties(Costanti.TIPOLOGIA);
		} catch (org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound e) {
			return result; // nessuna Regola PII censita
		} catch (Exception e) {
			throw new PiiMaskingException("Errore nel recupero delle Regole PII: " + e.getMessage(), e);
		}
		if (all == null) {
			return result;
		}
		for (GenericProperties gp : all) {
			PiiRuleConfig rule = parse(gp);
			if (rule.getDetectorType() == null || !cfg.getTypes().contains(rule.getDetectorType())) {
				continue;
			}
			if (Costanti.PII_DETECTOR_TYPE_VALUE_REGEXP.equals(rule.getDetectorType())
					&& !cfg.isRegexpRefsAll()
					&& !cfg.getRegexpRefs().contains(rule.getName())) {
				continue;
			}
			result.add(rule);
		}
		return result;
	}

	private static PiiRuleConfig parse(GenericProperties gp) {
		List<Property> props = gp.getPropertyList();
		String detectorType = find(props, Costanti.LLM_PII_DETECTOR_TYPE);
		String category = find(props, Costanti.LLM_PII_CATEGORY);
		boolean validate = isTrue(find(props, Costanti.LLM_PII_VALIDATE));
		String patternsRaw = find(props, Costanti.LLM_PII_PATTERNS);
		List<String> patterns = new ArrayList<>();
		if (patternsRaw != null && !patternsRaw.trim().isEmpty()) {
			patterns.addAll(Arrays.asList(patternsRaw.split("\\r?\\n")));
		}
		String pluginClassName = find(props, Costanti.LLM_PII_PLUGIN_CLASS_NAME);
		return new PiiRuleConfig(gp.getNome(), detectorType, category, validate, patterns, pluginClassName);
	}

	private static String find(List<Property> props, String name) {
		if (props == null || name == null) {
			return null;
		}
		for (Property p : props) {
			if (name.equals(p.getNome())) {
				return p.getValore();
			}
		}
		return null;
	}

	private static boolean isTrue(String v) {
		return "true".equalsIgnoreCase(v) || "yes".equalsIgnoreCase(v) || "abilitato".equalsIgnoreCase(v);
	}
}
