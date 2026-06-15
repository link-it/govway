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
package org.openspcoop2.web.ctrlstat.servlet.aps.llm;

import org.openspcoop2.web.lib.mvc.Costanti;

/**
 * Costanti delle servlet di gestione dei "Connettori Multipli LLM" agganciati a
 * un'erogazione o a una fruizione di API LLM. Definite separatamente dalle
 * {@code PorteApplicativeCostanti} perche' questa famiglia di servlet vive
 * trasversalmente fra erogazioni (APS) e fruizioni (APS Fruitori).
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class LlmConnettoriMultipliCostanti {

	private LlmConnettoriMultipliCostanti() {
		// utility class
	}

	// Object name (radice servlet, usato per i forward + struts-config)
	public static final String OBJECT_NAME_LLM_CONNETTORI_MULTIPLI = "llmConnettoriMultipli";
	public static final String OBJECT_NAME_LLM_CONNETTORI_MULTIPLI_ABILITAZIONE = "llmConnettoriMultipliAbilitazione";

	// Servlet URL (.do)
	public static final String SERVLET_NAME_LLM_CONNETTORI_MULTIPLI_LIST = OBJECT_NAME_LLM_CONNETTORI_MULTIPLI + Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final String SERVLET_NAME_LLM_CONNETTORI_MULTIPLI_ADD = OBJECT_NAME_LLM_CONNETTORI_MULTIPLI + Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_LLM_CONNETTORI_MULTIPLI_CHANGE = OBJECT_NAME_LLM_CONNETTORI_MULTIPLI + Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_LLM_CONNETTORI_MULTIPLI_DELETE = OBJECT_NAME_LLM_CONNETTORI_MULTIPLI + Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_LLM_CONNETTORI_MULTIPLI_ABILITAZIONE = OBJECT_NAME_LLM_CONNETTORI_MULTIPLI_ABILITAZIONE + ".do";

	// Parametri request
	/** ID dell'AccordoServizioParteSpecifica (erogazione di riferimento). Obbligatorio. */
	public static final String PARAMETRO_LLM_ID_ASPS = "idAsps";
	/** ID del Fruitore (presente quando il contesto e' una fruizione, assente per erogazione). */
	public static final String PARAMETRO_LLM_ID_FRUITORE = "idFruitore";
	/** ID del soggetto fruitore (alternativa a PARAMETRO_LLM_ID_FRUITORE; usato quando la rotella
	 * della maschera Fruitori non dispone direttamente dell'id del Fruitore ma solo dell'id del soggetto). */
	public static final String PARAMETRO_LLM_ID_SOGGETTO_FRUITORE = "idSoggettoFruitore";
	/** ID della PortaApplicativa specifica (per erogazioni con piu' gruppi/azioni: identifica il gruppo).
	 * Assente -> la PA default mappata all'APS. */
	public static final String PARAMETRO_LLM_ID_PORTA = "idPorta";
	/** ID della PortaDelegata specifica (per fruizioni con piu' gruppi/azioni: identifica il gruppo).
	 * Assente -> il connettore vive su {@code fr.getConnettore()}. */
	public static final String PARAMETRO_LLM_ID_PORTA_DELEGATA = "idPortaDelegata";
	/** Nome del ConnettoreLlmProviderRef selezionato dall'utente per change/delete. */
	public static final String PARAMETRO_LLM_PROVIDER_NOME = "llmProviderNome";

	// Label
	public static final String LABEL_LLM_CONNETTORI_MULTIPLI = "Connettori";
	public static final String LABEL_LLM_CONNETTORE_LLM_PROVIDER = "Provider";
	public static final String LABEL_LLM_CONNETTORE_LLM_BINDINGS = "Modelli";
	public static final String LABEL_LLM_CONNETTORE_LLM_ENDPOINT = "Endpoint";
}
