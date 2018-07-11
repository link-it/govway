/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.web.monitor.statistiche.bean;

import java.io.Serializable;

import org.openspcoop2.core.statistiche.constants.TipoReport;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiGrafici;

/**
 * AnalisiStatistica
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author: apoli $
 * @version $Rev: 14213 $, $Date: 2018-06-25 12:27:04 +0200 (Mon, 25 Jun 2018) $
 *
 */
public class AnalisiStatistica implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String label;
	private String value;
	private TipoReport tipoReport;
	private boolean rendered;
	
	public AnalisiStatistica(String value, String label, TipoReport tipoReport) {
		this(value, label, tipoReport, true);
	}
	
	public AnalisiStatistica(String value, String label, TipoReport tipoReport, boolean rendered) {
		this.label = label;
		this.value = value;
		this.rendered = rendered;
		this.tipoReport = tipoReport;
	}

	public String getLabel() {
		return this.label;
	}

	public String getValue() {
		return this.value;
	}

	public TipoReport getTipoReport() {
		return this.tipoReport;
	}

	public boolean isRendered() {
		return this.rendered;
	}

	public String getIcona() {
		switch (this.tipoReport) {
		case ANDAMENTO_TEMPORALE:
			return CostantiGrafici.ICONA_ANDAMENTO_TEMPORALE;
		case LINE_CHART:
			return CostantiGrafici.ICONA_LINE_CHART;
		case PIE_CHART:
			return CostantiGrafici.ICONA_PIE_CHART;
		case TABELLA:
			return CostantiGrafici.ICONA_TABELLA;
		case BAR_CHART:
		default:
			return CostantiGrafici.ICONA_BAR_CHART;
		}
	}
	
	public String get_value_tipoReport() {
		if(this.tipoReport == null){
			return null;
		}else{
			return this.tipoReport.toString();
		}
	}
}
