/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.soggetti;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.lib.mvc.ForwardParams;

/**
 * SoggettiCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoggettiCostanti {
	
	private SoggettiCostanti() {}

	/* OBJECT NAME */

	public static final String OBJECT_NAME_SOGGETTI = "soggetti";

	public static final ForwardParams TIPO_OPERAZIONE_ENDPOINT = ForwardParams.OTHER("EndPoint");
	
	public static final String OBJECT_NAME_SOGGETTI_RUOLI = "soggettiRuoli";
	
	public static final String OBJECT_NAME_SOGGETTI_CREDENZIALI = "soggettiCredenziali";
	
	public static final String OBJECT_NAME_SOGGETTI_PROPRIETA = "soggettiProprieta";
	
	public static final String OBJECT_NAME_SOGGETTI_VERIFICA_CERTIFICATI = "soggettiVerificaCertificati";
	
	/* SERVLET NAME */

	public static final String SERVLET_NAME_SOGGETTI_ADD = OBJECT_NAME_SOGGETTI
			+ org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_SOGGETTI_CHANGE = OBJECT_NAME_SOGGETTI
			+ org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_SOGGETTI_DELETE = OBJECT_NAME_SOGGETTI
			+ org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_SOGGETTI_LIST = OBJECT_NAME_SOGGETTI
			+ org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final String SERVLET_NAME_SOGGETTI_ENDPOINT = OBJECT_NAME_SOGGETTI
			+ TIPO_OPERAZIONE_ENDPOINT.getOtherContext()+".do";
	private static final List<String> SERVLET_SOGGETTI = new ArrayList<>();
	public static List<String> getServletSoggetti() {
		return SERVLET_SOGGETTI;
	}
	static {
		SERVLET_SOGGETTI.add(SERVLET_NAME_SOGGETTI_ADD);
		SERVLET_SOGGETTI.add(SERVLET_NAME_SOGGETTI_CHANGE);
		SERVLET_SOGGETTI.add(SERVLET_NAME_SOGGETTI_DELETE);
		SERVLET_SOGGETTI.add(SERVLET_NAME_SOGGETTI_LIST);
		SERVLET_SOGGETTI.add(SERVLET_NAME_SOGGETTI_ENDPOINT);
	}
	
	public static final String SERVLET_NAME_SOGGETTI_RUOLI_ADD = OBJECT_NAME_SOGGETTI_RUOLI
			+ org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_SOGGETTI_RUOLI_DELETE = OBJECT_NAME_SOGGETTI_RUOLI
			+ org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_SOGGETTI_RUOLI_LIST = OBJECT_NAME_SOGGETTI_RUOLI
			+ org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	private static final List<String> SERVLET_SOGGETTI_RUOLI = new ArrayList<>();
	public static List<String> getServletSoggettiRuoli() {
		return SERVLET_SOGGETTI_RUOLI;
	}
	static {
		SERVLET_SOGGETTI_RUOLI.add(SERVLET_NAME_SOGGETTI_RUOLI_ADD);
		SERVLET_SOGGETTI_RUOLI.add(SERVLET_NAME_SOGGETTI_RUOLI_DELETE);
		SERVLET_SOGGETTI_RUOLI.add(SERVLET_NAME_SOGGETTI_RUOLI_LIST);
	}
	
	public static final String SERVLET_NAME_SOGGETTI_CREDENZIALI_ADD = OBJECT_NAME_SOGGETTI_CREDENZIALI + org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_SOGGETTI_CREDENZIALI_CHANGE = OBJECT_NAME_SOGGETTI_CREDENZIALI + org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_SOGGETTI_CREDENZIALI_DELETE = OBJECT_NAME_SOGGETTI_CREDENZIALI + org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_SOGGETTI_CREDENZIALI_LIST = OBJECT_NAME_SOGGETTI_CREDENZIALI + org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	private static final List<String> SERVLET_SOGGETTI_CREDENZIALI = new ArrayList<>();
	public static List<String> getServletSoggettiCredenziali() {
		return SERVLET_SOGGETTI_CREDENZIALI;
	}
	static {
		SERVLET_SOGGETTI_CREDENZIALI.add(SERVLET_NAME_SOGGETTI_CREDENZIALI_ADD);
		SERVLET_SOGGETTI_CREDENZIALI.add(SERVLET_NAME_SOGGETTI_CREDENZIALI_CHANGE);
		SERVLET_SOGGETTI_CREDENZIALI.add(SERVLET_NAME_SOGGETTI_CREDENZIALI_DELETE);
		SERVLET_SOGGETTI_CREDENZIALI.add(SERVLET_NAME_SOGGETTI_CREDENZIALI_LIST);
	}
	
	public static final String SERVLET_NAME_SOGGETTI_PROPRIETA_ADD = OBJECT_NAME_SOGGETTI_PROPRIETA + org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_SOGGETTI_PROPRIETA_CHANGE = OBJECT_NAME_SOGGETTI_PROPRIETA + org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_SOGGETTI_PROPRIETA_DELETE = OBJECT_NAME_SOGGETTI_PROPRIETA + org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_SOGGETTI_PROPRIETA_LIST = OBJECT_NAME_SOGGETTI_PROPRIETA + org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	private static final List<String> SERVLET_SOGGETTI_PROPRIETA = new ArrayList<>();
	public static List<String> getServletSoggettiProprieta() {
		return SERVLET_SOGGETTI_PROPRIETA;
	}
	static {
		SERVLET_SOGGETTI_PROPRIETA.add(SERVLET_NAME_SOGGETTI_PROPRIETA_ADD);
		SERVLET_SOGGETTI_PROPRIETA.add(SERVLET_NAME_SOGGETTI_PROPRIETA_CHANGE);
		SERVLET_SOGGETTI_PROPRIETA.add(SERVLET_NAME_SOGGETTI_PROPRIETA_DELETE);
		SERVLET_SOGGETTI_PROPRIETA.add(SERVLET_NAME_SOGGETTI_PROPRIETA_LIST);
	}
	
	public static final String SERVLET_NAME_SOGGETTI_VERIFICA_CERTIFICATI = OBJECT_NAME_SOGGETTI_VERIFICA_CERTIFICATI+".do";
	
	/* NOME VISTA CUSTOM */
	public static final String SOGGETTI_NOME_VISTA_CUSTOM_LISTA = "soggetti";

	/* LABEL GENERALI */

	public static final String LABEL_SOGGETTI = "Soggetti";
	public static final String LABEL_SOGGETTO = "Soggetto";
	public static final String LABEL_SOGGETTO_EROGATORE = "Soggetto Erogatore";
	public static final String LABEL_CLIENT = "Porte Delegate";
	public static final String LABEL_SERVER = "Porte Applicative";
	public static final String LABEL_SOGGETTI_ESPORTA_SELEZIONATI = "Esporta";
	public static final String LABEL_SOGGETTI_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.SOGGETTO.name()+"')";
	public static final String LABEL_SOGGETTI_MENU_VISUALE_AGGREGATA = "Soggetti";
	public static final String LABEL_SOGGETTI_RISULTATI_RICERCA = "Risultati ricerca";
	public static final String LABEL_SOGGETTI_VERIFICA_CERTIFICATI = CostantiControlStation.LABEL_VERIFICA_CERTIFICATI;
	public static final String LABEL_SOGGETTI_VERIFICA_CERTIFICATI_DI = CostantiControlStation.LABEL_VERIFICA_CERTIFICATI_DI;

	/* PARAMETRI */

	public static final String PARAMETRO_SOGGETTO_ID = "id";
	public static final String PARAMETRO_SOGGETTO_RUOLI_ACCESSO_DA_CHANGE = CostantiControlStation.PARAMETRO_ACCESSO_DA_CHANGE;
	public static final String PARAMETRO_SOGGETTO_PROTOCOLLO = CostantiControlStation.PARAMETRO_PROTOCOLLO;
	public static final String PARAMETRO_SOGGETTO_DOMINIO = Costanti.CONSOLE_PARAMETRO_SOGGETTO_DOMINIO;
	public static final String PARAMETRO_SOGGETTO_NOME = "nomeprov";
	public static final String PARAMETRO_SOGGETTO_TIPO = "tipoprov";
	public static final String PARAMETRO_SOGGETTO_TIPOLOGIA = "tipologia";
	public static final String PARAMETRO_SOGGETTO_CODICE_PORTA = "portadom";
	public static final String PARAMETRO_SOGGETTO_DESCRIZIONE = "descr";
	public static final String PARAMETRO_SOGGETTO_VERSIONE_PROTOCOLLO = "profilo";
	public static final String PARAMETRO_SOGGETTO_PDD = "pdd";
	public static final String PARAMETRO_SOGGETTO_IS_ROUTER = "is_router";
	public static final String PARAMETRO_SOGGETTO_IS_PRIVATO = "privato";
	public static final String PARAMETRO_SOGGETTO_CODICE_IPA = "codice_ipa";
	public static final String PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER = "pd_url_prefix_rewriter";
	public static final String PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER = "pa_url_prefix_rewriter";
	public static final String PARAMETRO_SOGGETTO_FILTER_DOMINIO_INTERNO= "internalDomain";
	
	public static final String PARAMETRO_SOGGETTO_RUOLO_ID = "ruoloId";
	
	public static final String PARAMETRO_SOGGETTO_MODIFICA_OPERATIVO = "modificaDatiOperativo";
	
	public static final String PARAMETRO_SOGGETTI_PROP_NOME = "propNome";
	public static final String PARAMETRO_SOGGETTI_PROP_VALORE = "propValore";
	
	/* LABEL PARAMETRI */
	public static final String LABEL_PARAMETRO_SOGGETTO_DOMINIO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	public static final String LABEL_PARAMETRO_SOGGETTO_DOMINIO = "Dominio";
	public static final String LABEL_PARAMETRO_SOGGETTO_NOME = "Nome";
	public static final String LABEL_PARAMETRO_SOGGETTO_TIPO = "Tipo";
	public static final String LABEL_PARAMETRO_SOGGETTO_TIPOLOGIA = "Tipologia";
	public static final String LABEL_PARAMETRO_SOGGETTO_PROTOCOLLO = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO;
	public static final String LABEL_PARAMETRO_SOGGETTO_PROTOCOLLO_COMPACT = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO_COMPACT;
	public static final String LABEL_PARAMETRO_SOGGETTO_CODICE_PORTA = "Identificativo Porta";
	public static final String LABEL_PARAMETRO_SOGGETTO_DESCRIZIONE = "Descrizione";
	public static final String LABEL_PARAMETRO_SOGGETTO_VERSIONE_PROTOCOLLO = "Versione Protocollo";
	public static final String LABEL_PARAMETRO_SOGGETTO_IS_ROUTER = "Router";
	public static final String LABEL_PARAMETRO_SOGGETTO_IS_PRIVATO = "Privato";
	public static final String LABEL_PARAMETRO_SOGGETTO_CODICE_IPA = "Codice IPA";
	public static final String LABEL_PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER = "UrlPrefix rewriter";
	public static final String LABEL_PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER = "UrlPrefix rewriter";
	
	public static final String LABEL_PARAMETRO_SOGGETTI_PROPRIETA = "Propriet&agrave;";
	public static final String LABEL_PARAMETRO_SOGGETTI_PROP_NOME = "Nome";
	public static final String LABEL_PARAMETRO_SOGGETTI_PROP_VALORE = CostantiControlStation.LABEL_PARAMETRO_VALORE;

	/* DEFAULT VALUE PARAMETRI */

	public static final String DEFAULT_VALUE_PARAMETRO_SOGGETTO_DOMINIO_QUALSIASI = "";

	public static final String SOGGETTO_RUOLO_ENTRAMBI = CostantiRegistroServizi.SOGGETTO_TIPOLOGIA_ENTRAMBI;
	public static final String SOGGETTO_RUOLO_FRUITORE = CostantiRegistroServizi.SOGGETTO_TIPOLOGIA_FRUITORE;
	public static final String SOGGETTO_RUOLO_EROGATORE = CostantiRegistroServizi.SOGGETTO_TIPOLOGIA_EROGATORE;
	private static final String[] SOGGETTI_RUOLI = { SOGGETTO_RUOLO_EROGATORE, 
		SOGGETTO_RUOLO_FRUITORE, SOGGETTO_RUOLO_ENTRAMBI };
	public static String[] getSoggettiRuoli() {
		return SOGGETTI_RUOLI;
	}

	public static final String LABEL_PARAMETRO_FILTRO_SOGGETTO_TIPO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	public static final String DEFAULT_VALUE_PARAMETRO_FILTRO_SOGGETTO_TIPO_QUALSIASI = "";
	private static final String[] LABELS_SOGGETTO_RUOLO_TIPO = { CostantiRegistroServizi.SOGGETTO_TIPOLOGIA_FRUITORE, CostantiRegistroServizi.SOGGETTO_TIPOLOGIA_EROGATORE };
	public static String[] getLabelsSoggettoRuoloTipo() {
		return LABELS_SOGGETTO_RUOLO_TIPO;
	}
	private static final String[] VALUES_SOGGETTO_RUOLO_TIPO = { CostantiRegistroServizi.SOGGETTO_TIPOLOGIA_FRUITORE, CostantiRegistroServizi.SOGGETTO_TIPOLOGIA_EROGATORE };
	public static String[] getValuesSoggettoRuoloTipo() {
		return VALUES_SOGGETTO_RUOLO_TIPO;
	}

	public static final String SOGGETTO_DOMINIO_OPERATIVO_VALUE = PddTipologia.OPERATIVO.toString();
	public static final String SOGGETTO_DOMINIO_ESTERNO_VALUE = PddTipologia.ESTERNO.toString();
	public static final String SOGGETTO_DOMINIO_OPERATIVO_LABEL = "Interno";
	public static final String SOGGETTO_DOMINIO_ESTERNO_LABEL = "Esterno";
	private static final String[] SOGGETTI_DOMINI_LABEL = { SOGGETTO_DOMINIO_ESTERNO_LABEL, 
			SOGGETTO_DOMINIO_OPERATIVO_LABEL };
	public static String[] getSoggettiDominiLabel() {
		return SOGGETTI_DOMINI_LABEL;
	}
	private static final String[] SOGGETTI_DOMINI_VALUE = { SOGGETTO_DOMINIO_ESTERNO_VALUE, 
			SOGGETTO_DOMINIO_OPERATIVO_VALUE};
	public static String[] getSoggettiDominiValue() {
		return SOGGETTI_DOMINI_VALUE;
	}

	public static final String MESSAGE_METADATI_SOGGETTO_VUOTI = "";
	public static final String MESSAGE_METADATI_SOGGETTO_SOLO_PROFILO = "Profilo Interoperabilit&agrave;: {0}";
	public static final String MESSAGE_METADATI_SOGGETTO_CON_PROFILO = "Profilo Interoperabilit&agrave;: {0}, Dominio: {1}";
	public static final String MESSAGE_METADATI_SOGGETTO_SENZA_PROFILO = "Dominio: {0}";
	
	public static final String LABEL_IN_USO_BODY_HEADER_NESSUN_RISULTATO = "Il soggetto non risulta utilizzato in alcuna configurazione";

	public static final String MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX = "Dati incompleti. &Egrave; necessario indicare: {0}";
	public static final String MESSAGGIO_ERRORE_LA_PROPRIETA_XX_E_GIA_STATO_ASSOCIATA_AL_SA_YY = "La propriet&agrave; {0} &egrave; gi&agrave; stata associata all''applicativo {1}";
	
}
