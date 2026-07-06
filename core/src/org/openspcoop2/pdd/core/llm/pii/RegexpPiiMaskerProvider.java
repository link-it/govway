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

/**
 * Provider built-in per il tipo detector {@code regexp}.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class RegexpPiiMaskerProvider implements PiiMaskerProvider {

	@Override
	public String getType() {
		return Costanti.PII_DETECTOR_TYPE_VALUE_REGEXP;
	}

	@Override
	public PiiRuleMasker create(PiiRuleConfig rule) throws PiiMaskingException {
		return new RegexpPiiRuleMasker(rule);
	}
}
