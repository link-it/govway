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
import java.util.List;

/**
 * Configurazione risolta di una Regola PII (una entry {@code generic_properties}
 * tipologia {@code llmPiiMasking}).
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class PiiRuleConfig {

	private final String name;
	private final String detectorType;
	private final String category;
	private final boolean validate;
	private final List<String> patterns = new ArrayList<>();
	private final String pluginClassName;

	public PiiRuleConfig(String name, String detectorType, String category, boolean validate,
			List<String> patterns, String pluginClassName) {
		this.name = name;
		this.detectorType = detectorType;
		this.category = category;
		this.validate = validate;
		if (patterns != null) {
			this.patterns.addAll(patterns);
		}
		this.pluginClassName = pluginClassName;
	}

	public String getName() { return this.name; }
	public String getDetectorType() { return this.detectorType; }
	public String getCategory() { return this.category; }
	public boolean isValidate() { return this.validate; }
	public List<String> getPatterns() { return this.patterns; }
	public String getPluginClassName() { return this.pluginClassName; }
}
