/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.controllo_traffico.driver;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;

/**
 * PolicyGroupByActiveThreadsInMemoryEnum 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum PolicyGroupByActiveThreadsType {

	LOCAL, 
	LOCAL_DIVIDED_BY_NODES, 
	DATABASE;
	
	public String toLabel() {
		switch (this) {
		case LOCAL:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_LOCALE;
		case LOCAL_DIVIDED_BY_NODES:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_LOCALE_SUDDIVISA_TRA_NODI;
		case DATABASE:
			return CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA+" - "+CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_DATABASE;
		}
		return null;
	}
	
	public List<TipoRisorsaPolicyAttiva> getSupportedResources(){
		List<TipoRisorsaPolicyAttiva> all = new ArrayList<TipoRisorsaPolicyAttiva>();
		for (TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva : TipoRisorsaPolicyAttiva.values()) {
			all.add(tipoRisorsaPolicyAttiva);
		}
		switch (this) {
		case LOCAL:
			return all;
		case LOCAL_DIVIDED_BY_NODES:
			all.remove(TipoRisorsaPolicyAttiva.OCCUPAZIONE_BANDA);
			all.remove(TipoRisorsaPolicyAttiva.TEMPO_COMPLESSIVO_RISPOSTA);
			all.remove(TipoRisorsaPolicyAttiva.TEMPO_MEDIO_RISPOSTA);
			return all;
		case DATABASE:
			return all;
		}
		return null;
	}
	public boolean isSupportedResource(TipoRisorsaPolicyAttiva resource) {
		return this.getSupportedResources().contains(resource);
	}
}
