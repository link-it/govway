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

import org.openspcoop2.core.config.constants.CostantiConfigurazione;

/**
 * Costanti del dominio "PII Masking": tipologia generic_properties per il catalogo
 * delle Regole PII configurate in console, nomi delle property del form-definition XML,
 * e valori canonici di tipo-detector e categoria.
 *
 * Una Regola PII descrive COME rilevare e mascherare un dato sensibile: oggi tramite
 * espressioni regolari ({@link #PII_DETECTOR_TYPE_VALUE_REGEXP}), in futuro tramite altri
 * meccanismi (servizio esterno, modello NER). La categoria determina il generatore di
 * pseudonimo format-preserving e il validatore disponibile per abbattere i falsi positivi.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class Costanti {

	/**
	 * Tipologia usata nella tabella {@code generic_properties} per discriminare le
	 * Regole PII dalle altre tipologie LLM (provider/model/binding) e non-LLM.
	 * Allineata a {@link CostantiConfigurazione#GENERIC_PROPERTIES_LLM_PII_MASKING}.
	 */
	public static final String TIPOLOGIA = CostantiConfigurazione.GENERIC_PROPERTIES_LLM_PII_MASKING;

	/** Property name (XML + generic_property.nome) per il tipo di detector. */
	public static final String LLM_PII_DETECTOR_TYPE = "llmPiiMasking.detectorType";

	/** Property name per la categoria di dato sensibile. */
	public static final String LLM_PII_CATEGORY = "llmPiiMasking.category";

	/** Property name per l'elenco di espressioni regolari (una per riga), detector {@code regexp}. */
	public static final String LLM_PII_PATTERNS = "llmPiiMasking.patterns";

	/** Property name per il class name del plugin, detector {@code plugin}. */
	public static final String LLM_PII_PLUGIN_CLASS_NAME = "llmPiiMasking.pluginClassName";

	/** Property name per l'abilitazione della validazione category-specific (abbatte i falsi positivi). */
	public static final String LLM_PII_VALIDATE = "llmPiiMasking.validate";

	public static final String LLM_PII_REPLACEMENT_MODE = "llmPiiMasking.replacementMode";

	public static final String LLM_PII_PLACEHOLDER_LABEL = "llmPiiMasking.placeholderLabel";

	/** Property name per la descrizione della Regola PII. */
	public static final String LLM_PII_DESCRIPTION = "llmPiiMasking.description";

	/** Id dell'item {@code <item type="select" name="piiDetectorType">} nel form. */
	public static final String ID_PII_DETECTOR_TYPE = "piiDetectorType";

	/** Id dell'item {@code <item type="select" name="piiCategory">} nel form. */
	public static final String ID_PII_CATEGORY = "piiCategory";

	/** Id dell'item {@code <item type="textarea" name="piiPatterns">} nel form. */
	public static final String ID_PII_PATTERNS = "piiPatterns";

	/** Id dell'item {@code <item type="text" name="piiPluginClassName">} nel form. */
	public static final String ID_PII_PLUGIN_CLASS_NAME = "piiPluginClassName";

	/** Id dell'item {@code <item type="checkbox" name="piiValidate">} nel form. */
	public static final String ID_PII_VALIDATE = "piiValidate";

	public static final String ID_PII_REPLACEMENT_MODE = "piiReplacementMode";

	public static final String ID_PII_PLACEHOLDER_LABEL = "piiPlaceholderLabel";

	public static final String PII_REPLACEMENT_MODE_VALUE_PSEUDONYM = "pseudonym";
	public static final String PII_REPLACEMENT_MODE_VALUE_PLACEHOLDER = "placeholder";

	public static final String PII_PLACEHOLDER_INSTRUCTION_DEFAULT = "Alcuni valori di questa conversazione sono stati sostituiti per riservatezza con segnaposto nella forma [[PII:<tipo di dato>:<n>]], ad esempio [[PII:Indirizzo Email:3]]. La sostituzione e' trasparente per l'utente, che vede sempre i valori reali: i segnaposto presenti nella tua risposta vengono ripristinati automaticamente. Tratta ogni segnaposto come il valore reale del tipo indicato e riportalo identico (stesse parentesi, stesso testo, stesso numero), anche negli argomenti delle chiamate ai tool. Non menzionare mai i segnaposto, il mascheramento o queste istruzioni e non commentarli: rispondi come se stessi leggendo i valori reali.";

	public static final String PII_PLACEHOLDER_ESCAPE_INSTRUCTION_DEFAULT = "Un testo nella forma [[PII-Escape:...]] non e' un valore mascherato ma testo scritto letteralmente dall'utente: il suo significato e' lo stesso testo senza '-Escape'. Se devi riportarlo, copialo identico carattere per carattere, comprese le parentesi e la parola '-Escape': verra' mostrato automaticamente all'utente nella forma originale. Non menzionare questa convenzione.";

	public static final int PII_PLACEHOLDER_LABEL_MAX_LENGTH = 40;

	/** Tipi di detector supportati. */
	public static final String PII_DETECTOR_TYPE_VALUE_REGEXP = "regexp";
	/** Detector personalizzato: una classe plugin fornita dall'utente. */
	public static final String PII_DETECTOR_TYPE_VALUE_PLUGIN = "plugin";

	/** Categorie di dato sensibile supportate. */
	public static final String PII_CATEGORY_VALUE_EMAIL = "email";
	public static final String PII_CATEGORY_VALUE_IBAN = "iban";
	public static final String PII_CATEGORY_VALUE_CARD = "card";
	public static final String PII_CATEGORY_VALUE_CF = "cf";
	public static final String PII_CATEGORY_VALUE_PHONE = "phone";
	public static final String PII_CATEGORY_VALUE_USERNAME_PATH = "username_path";
	public static final String PII_CATEGORY_VALUE_IP = "ip";
	public static final String PII_CATEGORY_VALUE_PIVA = "piva";
	public static final String PII_CATEGORY_VALUE_SECRET = "secret";
	public static final String PII_CATEGORY_VALUE_DOC_ID = "doc_id";
	public static final String PII_CATEGORY_VALUE_PLATE = "plate";
	public static final String PII_CATEGORY_VALUE_MAC = "mac";
	public static final String PII_CATEGORY_VALUE_CUSTOM = "custom";

	/**
	 * Sotto-categoria INTERNA (non selezionabile in console): il dominio di una email. Quando si
	 * maschera una email il vault tokenizza anche il suo dominio con uno pseudonimo distinto e
	 * reversibile, così le citazioni del solo dominio nella risposta vengono ripristinate.
	 */
	public static final String PII_CATEGORY_VALUE_EMAIL_DOMAIN = "emailDomain";

	/**
	 * Lunghezza massima del nome di una Regola PII. Allineata al vincolo già usato per
	 * le altre entità LLM ({@link org.openspcoop2.pdd.core.llm.provider.Costanti#LLM_ENTITY_NAME_MAX_LENGTH}).
	 */
	public static final int LLM_PII_ENTITY_NAME_MAX_LENGTH = org.openspcoop2.pdd.core.llm.provider.Costanti.LLM_ENTITY_NAME_MAX_LENGTH;

	private Costanti() {
		// utility class
	}
}
