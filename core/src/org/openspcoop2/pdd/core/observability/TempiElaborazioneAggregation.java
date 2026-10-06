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

package org.openspcoop2.pdd.core.observability;

import java.util.List;
import java.util.Map;

/**
 * TempiElaborazioneAggregation
 *
 * Descrive come aggregare/esporre i tempi di elaborazione (fasi) come metriche:
 *  - {@link #labels}: le label (chiave→valore) da applicare alle serie;
 *  - {@link #fasi}: l'elenco delle fasi da esporre (null o vuoto = tutte le 33 fasi).
 *
 * Per adesso viene costruita con label fisse (servizio, azione, tipo_pdd) e tutte le fasi;
 * in futuro sia le label sia le fasi saranno pilotabili tramite proprietà locali/globali.
 *
 * @author Burlon Tommaso
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TempiElaborazioneAggregation {

	private final Map<String,String> labels;
	private final List<String> fasi;

	public TempiElaborazioneAggregation(Map<String,String> labels, List<String> fasi) {
		this.labels = labels;
		this.fasi = fasi;
	}

	public Map<String,String> getLabels() {
		return this.labels;
	}

	/** Fasi da esporre; null o vuoto significa "tutte le fasi". */
	public List<String> getFasi() {
		return this.fasi;
	}
}
