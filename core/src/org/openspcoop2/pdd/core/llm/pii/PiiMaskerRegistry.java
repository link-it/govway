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

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Registry dei {@link PiiMaskerProvider}: risolve un {@link PiiRuleMasker} a partire dal
 * config di una Regola PII. I tipi built-in ({@code regexp}) sono registrati staticamente;
 * il tipo {@code plugin} carica per nome la classe indicata (che deve implementare
 * {@link PiiMaskerProvider}).
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class PiiMaskerRegistry {

	private static final Map<String, PiiMaskerProvider> BUILTIN = new ConcurrentHashMap<>();
	/** Cache dei provider plugin caricati per nome-classe. */
	private static final Map<String, PiiMaskerProvider> PLUGIN_CACHE = new ConcurrentHashMap<>();

	static {
		register(new RegexpPiiMaskerProvider());
	}

	private PiiMaskerRegistry() {}

	public static void register(PiiMaskerProvider provider) {
		BUILTIN.put(provider.getType(), provider);
	}

	/** Costruisce il masker per la Regola PII indicata, risolvendo il provider dal tipo. */
	public static PiiRuleMasker createMasker(PiiRuleConfig rule) throws PiiMaskingException {
		if (rule == null || rule.getDetectorType() == null) {
			throw new PiiMaskingException("Regola PII senza tipo detector");
		}
		String type = rule.getDetectorType();
		if (Costanti.PII_DETECTOR_TYPE_VALUE_PLUGIN.equals(type)) {
			return loadPlugin(rule).create(rule);
		}
		PiiMaskerProvider provider = BUILTIN.get(type);
		if (provider == null) {
			throw new PiiMaskingException("Tipo detector PII non supportato: '" + type + "' (regola '" + rule.getName() + "')");
		}
		return provider.create(rule);
	}

	private static PiiMaskerProvider loadPlugin(PiiRuleConfig rule) throws PiiMaskingException {
		String className = rule.getPluginClassName();
		if (className == null || className.trim().isEmpty()) {
			throw new PiiMaskingException("Regola PII '" + rule.getName() + "' di tipo Personalizzato senza classe plugin");
		}
		String key = className.trim();
		PiiMaskerProvider cached = PLUGIN_CACHE.get(key);
		if (cached != null) {
			return cached;
		}
		try {
			Class<?> clazz = Class.forName(key);
			Object instance = clazz.getDeclaredConstructor().newInstance();
			if (!(instance instanceof PiiMaskerProvider)) {
				throw new PiiMaskingException("La classe plugin '" + key + "' non implementa " + PiiMaskerProvider.class.getName());
			}
			PiiMaskerProvider provider = (PiiMaskerProvider) instance;
			PLUGIN_CACHE.put(key, provider);
			return provider;
		} catch (PiiMaskingException e) {
			throw e;
		} catch (Exception e) {
			throw new PiiMaskingException("Impossibile caricare la classe plugin PII '" + key + "': " + e.getMessage(), e);
		}
	}
}
