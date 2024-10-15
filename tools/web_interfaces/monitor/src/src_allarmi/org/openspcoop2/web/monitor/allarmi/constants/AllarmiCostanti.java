/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.web.monitor.allarmi.constants;

/**
 * AllarmiCostanti 
 *
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmiCostanti {

	private AllarmiCostanti() {
		throw new IllegalStateException("Costanti class");
	}

	public static final String SEARCH_TIPOLOGIA_CONFIGURAZIONE_LABEL_KEY = "allarmi.search.tipologia.configurazione.label";
	public static final String SEARCH_TIPOLOGIA_EROGAZIONE_LABEL_KEY = "allarmi.search.tipologia.erogazione.label";
	public static final String SEARCH_TIPOLOGIA_FRUIZIONE_LABEL_KEY = "allarmi.search.tipologia.fruizione.label";

	public static final String CRITERI_ACKNOWLEDGE_LABEL_KEY = "allarmi.criteriAcknowledge.label";

	public static final String[] SEARCH_FORM_FIELDS_DA_NON_SALVARE= {
			"statoDefault",
			"STATO_DEFAULT_PAGINA_STATO_ALLARMI",
			"TIPOLOGIA_APPLICATIVA",
			"TIPOLOGIA_CONFIGURAZIONE",
			"TIPOLOGIA_DELEGATA",
			"STATO_DEFAULT_PAGINA_CONF_ALLARMI",
	};
}
