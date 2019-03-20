/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

package org.openspcoop2.core.monitor.rs.server.api.impl.utils;

import org.openspcoop2.web.monitor.statistiche.constants.CostantiExporter;

/**
 * TipoReport
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public enum TipoReport {

	temporale(CostantiExporter.TIPO_DISTRIBUZIONE_TEMPORALE),
	esiti(CostantiExporter.TIPO_DISTRIBUZIONE_ESITI),
	soggetto_remoto(CostantiExporter.TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO),
	soggetto_locale(CostantiExporter.TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE),
	api(CostantiExporter.TIPO_DISTRIBUZIONE_SERVIZIO),
	azione(CostantiExporter.TIPO_DISTRIBUZIONE_AZIONE),
	applicativo(CostantiExporter.TIPO_DISTRIBUZIONE_APPLICATIVO),
	identificativo_autenticato(CostantiExporter.TIPO_DISTRIBUZIONE_IDENTIFICATIVO_AUTENTICATO),
	token_info(CostantiExporter.TIPO_DISTRIBUZIONE_TOKEN_INFO);
	
	
	TipoReport(String v) {
		this.value = v;
	}
	
	private String value;

	public String getValue() {
		return this.value;
	}
	
}
