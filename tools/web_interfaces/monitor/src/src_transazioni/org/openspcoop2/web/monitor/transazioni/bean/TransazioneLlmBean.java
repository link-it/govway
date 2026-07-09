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

package org.openspcoop2.web.monitor.transazioni.bean;

import java.util.Locale;

import org.openspcoop2.core.transazioni.TransazioneLlm;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.web.monitor.core.bean.ApplicationBean;

/**
 * Wrapper di visualizzazione per i dati LLM di una transazione ({@link TransazioneLlm}):
 * espone i valori grezzi (provider/model/binding) e quelli formattati (token, costo).
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class TransazioneLlmBean {

	private final TransazioneLlm llm;

	public TransazioneLlmBean(TransazioneLlm llm) {
		this.llm = llm;
	}

	private static Locale getLocaleAttivo() {
		return ApplicationBean.getInstance().getLocale();
	}

	public String getLlmProvider() {
		return this.llm.getLlmProvider();
	}

	public String getLlmModel() {
		return this.llm.getLlmModel();
	}

	public String getLlmProviderBinding() {
		return this.llm.getLlmProviderBinding();
	}

	public String getTokenInputFormattato() {
		return this.llm.getTokenInput() != null ? Utilities.formatNumber(this.llm.getTokenInput(), getLocaleAttivo()) : null;
	}

	public String getTokenOutputFormattato() {
		return this.llm.getTokenOutput() != null ? Utilities.formatNumber(this.llm.getTokenOutput(), getLocaleAttivo()) : null;
	}

	public String getTokenTotaleFormattato() {
		if (this.llm.getTokenInput() == null && this.llm.getTokenOutput() == null) {
			return null;
		}
		long tot = (this.llm.getTokenInput() != null ? this.llm.getTokenInput() : 0L)
				+ (this.llm.getTokenOutput() != null ? this.llm.getTokenOutput() : 0L);
		return Utilities.formatNumber(tot, getLocaleAttivo());
	}

	public String getCostoFormattato() {
		// Se il pricing del binding non e' configurato il costo stimato resta 0: in tal caso
		// non mostriamo la riga (la include usa rendered="#{not empty ...}").
		if (this.llm.getCostEstimated() <= 0d) {
			return null;
		}
		return Utilities.formatCurrencyUSD(this.llm.getCostEstimated(), getLocaleAttivo());
	}
}
