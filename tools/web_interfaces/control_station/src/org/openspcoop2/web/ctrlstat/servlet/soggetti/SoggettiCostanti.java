/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import java.util.Vector;

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

	/* OBJECT NAME */

	public final static String OBJECT_NAME_SOGGETTI = "soggetti";

	public final static ForwardParams TIPO_OPERAZIONE_ENDPOINT = ForwardParams.OTHER("EndPoint");
	
	public final static String OBJECT_NAME_SOGGETTI_RUOLI = "soggettiRuoli";
	
	/* SERVLET NAME */

	public final static String SERVLET_NAME_SOGGETTI_ADD = OBJECT_NAME_SOGGETTI
			+ "Add.do";
	public final static String SERVLET_NAME_SOGGETTI_CHANGE = OBJECT_NAME_SOGGETTI
			+ "Change.do";
	public final static String SERVLET_NAME_SOGGETTI_DELETE = OBJECT_NAME_SOGGETTI
			+ "Del.do";
	public final static String SERVLET_NAME_SOGGETTI_LIST = OBJECT_NAME_SOGGETTI
			+ "List.do";
	public final static String SERVLET_NAME_SOGGETTI_ENDPOINT = OBJECT_NAME_SOGGETTI
			+ TIPO_OPERAZIONE_ENDPOINT.getOtherContext()+".do";
	public final static Vector<String> SERVLET_SOGGETTI = new Vector<String>();
	static {
		SERVLET_SOGGETTI.add(SERVLET_NAME_SOGGETTI_ADD);
		SERVLET_SOGGETTI.add(SERVLET_NAME_SOGGETTI_CHANGE);
		SERVLET_SOGGETTI.add(SERVLET_NAME_SOGGETTI_DELETE);
		SERVLET_SOGGETTI.add(SERVLET_NAME_SOGGETTI_LIST);
		SERVLET_SOGGETTI.add(SERVLET_NAME_SOGGETTI_ENDPOINT);
	}
	
	public final static String SERVLET_NAME_SOGGETTI_RUOLI_ADD = OBJECT_NAME_SOGGETTI_RUOLI
			+ "Add.do";
	public final static String SERVLET_NAME_SOGGETTI_RUOLI_DELETE = OBJECT_NAME_SOGGETTI_RUOLI
			+ "Del.do";
	public final static String SERVLET_NAME_SOGGETTI_RUOLI_LIST = OBJECT_NAME_SOGGETTI_RUOLI
			+ "List.do";
	public final static Vector<String> SERVLET_SOGGETTI_RUOLI = new Vector<String>();
	static {
		SERVLET_SOGGETTI_RUOLI.add(SERVLET_NAME_SOGGETTI_RUOLI_ADD);
		SERVLET_SOGGETTI_RUOLI.add(SERVLET_NAME_SOGGETTI_RUOLI_DELETE);
		SERVLET_SOGGETTI_RUOLI.add(SERVLET_NAME_SOGGETTI_RUOLI_LIST);
	}
	
	/* NOME VISTA CUSTOM */
	public final static String SOGGETTI_NOME_VISTA_CUSTOM_LISTA = "soggetti";

	/* LABEL GENERALI */

	public final static String LABEL_SOGGETTI = "Soggetti";
	public final static String LABEL_SOGGETTO = "Soggetto";
	//public final static String LABEL_CLIENT = "Fruizioni";
	//public final static String LABEL_SERVER = "Erogazioni";
	public final static String LABEL_CLIENT = "Porte Delegate";
	public final static String LABEL_SERVER = "Porte Applicative";
	public final static String LABEL_SOGGETTI_ESPORTA_SELEZIONATI = "Esporta";
	public final static String LABEL_SOGGETTI_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.SOGGETTO.name()+"')";
	public final static String LABEL_SOGGETTI_MENU_VISUALE_AGGREGATA = "Soggetti";

	/* PARAMETRI */

	public final static String PARAMETRO_SOGGETTO_ID = "id";
	public final static String PARAMETRO_SOGGETTO_RUOLI_ACCESSO_DA_CHANGE = CostantiControlStation.PARAMETRO_ACCESSO_DA_CHANGE;
	public final static String PARAMETRO_SOGGETTO_PROTOCOLLO = CostantiControlStation.PARAMETRO_PROTOCOLLO;
	public final static String PARAMETRO_SOGGETTO_DOMINIO = Costanti.CONSOLE_PARAMETRO_SOGGETTO_DOMINIO;
	public final static String PARAMETRO_SOGGETTO_NOME = "nomeprov";
	public final static String PARAMETRO_SOGGETTO_TIPO = "tipoprov";
	public final static String PARAMETRO_SOGGETTO_TIPOLOGIA = "tipologia";
	public final static String PARAMETRO_SOGGETTO_CODICE_PORTA = "portadom";
	public final static String PARAMETRO_SOGGETTO_DESCRIZIONE = "descr";
	public final static String PARAMETRO_SOGGETTO_VERSIONE_PROTOCOLLO = "profilo";
	public final static String PARAMETRO_SOGGETTO_PDD = "pdd";
	public final static String PARAMETRO_SOGGETTO_IS_ROUTER = "is_router";
	public final static String PARAMETRO_SOGGETTO_IS_PRIVATO = "privato";
	public final static String PARAMETRO_SOGGETTO_CODICE_IPA = "codice_ipa";
	public final static String PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER = "pd_url_prefix_rewriter";
	public final static String PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER = "pa_url_prefix_rewriter";
	public final static String PARAMETRO_SOGGETTO_FILTER_DOMINIO_INTERNO= "internalDomain";
	
	public final static String PARAMETRO_SOGGETTO_RUOLO_ID = "ruoloId";
	
	public final static String PARAMETRO_SOGGETTO_MODIFICA_OPERATIVO = "modificaDatiOperativo";
	
	/* LABEL PARAMETRI */
	public final static String LABEL_PARAMETRO_SOGGETTO_DOMINIO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	public final static String LABEL_PARAMETRO_SOGGETTO_DOMINIO = "Dominio";
	public final static String LABEL_PARAMETRO_SOGGETTO_NOME = "Nome";
	public final static String LABEL_PARAMETRO_SOGGETTO_TIPO = "Tipo";
	public final static String LABEL_PARAMETRO_SOGGETTO_TIPOLOGIA = "Tipologia";
	public final static String LABEL_PARAMETRO_SOGGETTO_PROTOCOLLO = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO;
	public final static String LABEL_PARAMETRO_SOGGETTO_PROTOCOLLO_COMPACT = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO_COMPACT;
	public final static String LABEL_PARAMETRO_SOGGETTO_CODICE_PORTA = "Identificativo Porta";
	public final static String LABEL_PARAMETRO_SOGGETTO_DESCRIZIONE = "Descrizione";
	public final static String LABEL_PARAMETRO_SOGGETTO_VERSIONE_PROTOCOLLO = "Versione Protocollo";
	public final static String LABEL_PARAMETRO_SOGGETTO_IS_ROUTER = "Router";
	public final static String LABEL_PARAMETRO_SOGGETTO_IS_PRIVATO = "Privato";
	public final static String LABEL_PARAMETRO_SOGGETTO_CODICE_IPA = "Codice IPA";
	public final static String LABEL_PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER = "UrlPrefix rewriter";
	public final static String LABEL_PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER = "UrlPrefix rewriter";
	

	/* DEFAULT VALUE PARAMETRI */

	public final static String DEFAULT_VALUE_PARAMETRO_SOGGETTO_DOMINIO_QUALSIASI = "";

	public final static String SOGGETTO_RUOLO_ENTRAMBI = CostantiRegistroServizi.SOGGETTO_TIPOLOGIA_ENTRAMBI;
	public final static String SOGGETTO_RUOLO_FRUITORE = CostantiRegistroServizi.SOGGETTO_TIPOLOGIA_FRUITORE;
	public final static String SOGGETTO_RUOLO_EROGATORE = CostantiRegistroServizi.SOGGETTO_TIPOLOGIA_EROGATORE;
	public final static String[] SOGGETTI_RUOLI = { SOGGETTO_RUOLO_EROGATORE, 
		SOGGETTO_RUOLO_FRUITORE, SOGGETTO_RUOLO_ENTRAMBI };
	
	public final static String LABEL_PARAMETRO_FILTRO_SOGGETTO_TIPO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	public final static String DEFAULT_VALUE_PARAMETRO_FILTRO_SOGGETTO_TIPO_QUALSIASI = "";
	public final static String[] LABELS_SOGGETTO_RUOLO_TIPO = { CostantiRegistroServizi.SOGGETTO_TIPOLOGIA_FRUITORE, CostantiRegistroServizi.SOGGETTO_TIPOLOGIA_EROGATORE };
	public final static String[] VALUES_SOGGETTO_RUOLO_TIPO = { CostantiRegistroServizi.SOGGETTO_TIPOLOGIA_FRUITORE, CostantiRegistroServizi.SOGGETTO_TIPOLOGIA_EROGATORE };
	
	
	public final static String SOGGETTO_DOMINIO_OPERATIVO_VALUE = PddTipologia.OPERATIVO.toString();
	public final static String SOGGETTO_DOMINIO_ESTERNO_VALUE = PddTipologia.ESTERNO.toString();
	public final static String SOGGETTO_DOMINIO_OPERATIVO_LABEL = "Interno";
	public final static String SOGGETTO_DOMINIO_ESTERNO_LABEL = "Esterno";
	public final static String[] SOGGETTI_DOMINI_LABEL = { SOGGETTO_DOMINIO_ESTERNO_LABEL, 
			SOGGETTO_DOMINIO_OPERATIVO_LABEL };
	public final static String[] SOGGETTI_DOMINI_VALUE = { SOGGETTO_DOMINIO_ESTERNO_VALUE, 
			SOGGETTO_DOMINIO_OPERATIVO_VALUE};

	public final static String MESSAGE_METADATI_SOGGETTO_VUOTI = "";
	public final static String MESSAGE_METADATI_SOGGETTO_SOLO_PROFILO = "Profilo Interoperabilit&agrave;: {0}";
	public final static String MESSAGE_METADATI_SOGGETTO_CON_PROFILO = "Profilo Interoperabilit&agrave;: {0}, Dominio: {1}";
	public final static String MESSAGE_METADATI_SOGGETTO_SENZA_PROFILO = "Dominio: {0}";
	
	public final static String LABEL_IN_USO_BODY_HEADER_NESSUN_RISULTATO = "Il soggetto non risulta utilizzato in alcuna configurazione";

}
