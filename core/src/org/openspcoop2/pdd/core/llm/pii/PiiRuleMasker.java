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
 * Masker di una singola Regola PII: individua e sostituisce la PII dentro un testo,
 * coniando/riusando pseudonimi nel {@link PiiVault} e registrando i match nei
 * {@link PiiFindings}. Opera SOLO sul testo: la navigazione del canonical e la
 * reversibilità sono gestite dall'engine e dal vault.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public interface PiiRuleMasker {

	String getRuleName();

	String getDetectorType();

	/** Maschera la PII in {@code text}; restituisce il testo mascherato. */
	String mask(String text, PiiVault vault, PiiFindings findings);
}
