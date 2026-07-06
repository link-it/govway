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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openspcoop2.core.config.Property;
import org.openspcoop2.pdd.core.llm.provider.Costanti;

/**
 * Configurazione PII risolta dal Provider Binding (property {@code llmProviderBinding.pii*}).
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class PiiBindingConfig {

	private boolean enabled;
	private final Set<String> types = new LinkedHashSet<>();
	private boolean regexpRefsAll;
	private final List<String> regexpRefs = new ArrayList<>();
	private boolean maskToolResult;
	private boolean maskToolUse;
	private boolean sessionCache;
	private final List<String> sessionKeyHeaders = new ArrayList<>();
	private Integer sessionTtlSeconds;

	public boolean isEnabled() { return this.enabled; }
	public Set<String> getTypes() { return this.types; }
	public boolean isRegexpRefsAll() { return this.regexpRefsAll; }
	public List<String> getRegexpRefs() { return this.regexpRefs; }
	public boolean isMaskToolResult() { return this.maskToolResult; }
	public boolean isMaskToolUse() { return this.maskToolUse; }
	public boolean isSessionCache() { return this.sessionCache; }
	public List<String> getSessionKeyHeaders() { return this.sessionKeyHeaders; }
	public Integer getSessionTtlSeconds() { return this.sessionTtlSeconds; }

	/** Costruisce la config dai property del binding. */
	public static PiiBindingConfig fromProperties(List<Property> props) {
		PiiBindingConfig cfg = new PiiBindingConfig();
		cfg.enabled = isTrue(find(props, Costanti.LLM_PROVIDER_BINDING_PII_ENABLED));
		addCsv(cfg.types, find(props, Costanti.LLM_PROVIDER_BINDING_PII_TYPE));
		cfg.regexpRefsAll = isTrue(find(props, Costanti.LLM_PROVIDER_BINDING_PII_REGEXP_REFS_ALL));
		addCsv(cfg.regexpRefs, find(props, Costanti.LLM_PROVIDER_BINDING_PII_REGEXP_REFS));
		cfg.maskToolResult = isTrue(find(props, Costanti.LLM_PROVIDER_BINDING_PII_MASK_TOOL_RESULT));
		cfg.maskToolUse = isTrue(find(props, Costanti.LLM_PROVIDER_BINDING_PII_MASK_TOOL_USE));
		cfg.sessionCache = isTrue(find(props, Costanti.LLM_PROVIDER_BINDING_PII_SESSION_CACHE));
		addCsv(cfg.sessionKeyHeaders, find(props, Costanti.LLM_PROVIDER_BINDING_PII_SESSION_KEY_HEADERS));
		String ttl = find(props, Costanti.LLM_PROVIDER_BINDING_PII_SESSION_TTL);
		if (ttl != null && !ttl.trim().isEmpty()) {
			try {
				cfg.sessionTtlSeconds = Integer.valueOf(ttl.trim());
			} catch (NumberFormatException e) {
				cfg.sessionTtlSeconds = null;
			}
		}
		return cfg;
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

	private static void addCsv(java.util.Collection<String> sink, String csv) {
		if (csv == null) {
			return;
		}
		for (String t : csv.split(",")) {
			String v = t.trim();
			if (!v.isEmpty()) {
				sink.add(v);
			}
		}
	}
}
