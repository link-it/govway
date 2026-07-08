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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Masker basato su espressioni regolari: per ogni pattern della Regola PII individua i match,
 * eventualmente li valida (category-specific) e li sostituisce con lo pseudonimo del vault.
 *
 * <p>Caso speciale {@code username_path}: se il pattern ha un gruppo di prefisso (es.
 * {@code (/home/)(utente)}) viene sostituito solo il segmento utente, mantenendo il prefisso.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class RegexpPiiRuleMasker implements PiiRuleMasker {

	private final String ruleName;
	private final String category;
	private final boolean validate;
	private final List<Pattern> patterns = new ArrayList<>();

	public RegexpPiiRuleMasker(PiiRuleConfig rule) throws PiiMaskingException {
		this.ruleName = rule.getName();
		this.category = rule.getCategory();
		this.validate = rule.isValidate();
		for (String p : rule.getPatterns()) {
			String regex = p != null ? p.trim() : "";
			if (regex.isEmpty()) {
				continue;
			}
			try {
				this.patterns.add(Pattern.compile(regex));
			} catch (Exception e) {
				throw new PiiMaskingException("Regola PII '" + this.ruleName + "': espressione regolare non valida ('" + regex + "'): " + e.getMessage(), e);
			}
		}
	}

	@Override
	public String getRuleName() {
		return this.ruleName;
	}

	@Override
	public String getDetectorType() {
		return Costanti.PII_DETECTOR_TYPE_VALUE_REGEXP;
	}

	@Override
	public String mask(String text, PiiVault vault, PiiFindings findings) {
		if (text == null || text.isEmpty()) {
			return text;
		}
		String result = text;
		boolean usernamePath = Costanti.PII_CATEGORY_VALUE_USERNAME_PATH.equals(this.category);
		for (Pattern pattern : this.patterns) {
			Matcher m = pattern.matcher(result);
			result = m.replaceAll(mr -> {
				// username_path: mantieni il prefisso (gruppo 1), maschera solo il segmento utente (gruppo 2)
				if (usernamePath && mr.groupCount() >= 2 && mr.group(1) != null && mr.group(2) != null) {
					String user = mr.group(2);
					if (this.validate && !PiiValidators.isValid(this.category, user)) {
						return Matcher.quoteReplacement(mr.group());
					}
					boolean preEsistente = vault.containsOriginal(this.category, user);
					String pseudo = vault.tokenize(this.category, user);
					findings.record(getDetectorType(), this.ruleName, pseudo, preEsistente);
					return Matcher.quoteReplacement(mr.group(1) + pseudo);
				}
				String matched = mr.group();
				if (this.validate && !PiiValidators.isValid(this.category, matched)) {
					return Matcher.quoteReplacement(matched); // falso positivo scartato
				}
				boolean preEsistente = vault.containsOriginal(this.category, matched);
				String pseudo = vault.tokenize(this.category, matched);
				findings.record(getDetectorType(), this.ruleName, pseudo, preEsistente);
				return Matcher.quoteReplacement(pseudo);
			});
		}
		return result;
	}
}
