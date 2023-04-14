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
package org.openspcoop2.web.ctrlstat.servlet.scope;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.registry.constants.ScopeContesto;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.lib.mvc.Costanti;

/**
 * ScopeCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ScopeCostanti {
	
	private ScopeCostanti() {}

	public static final String OBJECT_NAME_SCOPE = "scope";

	public static final String SERVLET_NAME_SCOPE_ADD = OBJECT_NAME_SCOPE+Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_SCOPE_CHANGE = OBJECT_NAME_SCOPE+Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_SCOPE_DELETE = OBJECT_NAME_SCOPE+Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_SCOPE_LIST = OBJECT_NAME_SCOPE+Costanti.STRUTS_ACTION_SUFFIX_LIST;
	private static final List<String> SERVLET_SCOPE = new ArrayList<>();
	public static List<String> getServletScope() {
		return SERVLET_SCOPE;
	}
	static{
		SERVLET_SCOPE.add(SERVLET_NAME_SCOPE_ADD);
		SERVLET_SCOPE.add(SERVLET_NAME_SCOPE_CHANGE);
		SERVLET_SCOPE.add(SERVLET_NAME_SCOPE_DELETE);
		SERVLET_SCOPE.add(SERVLET_NAME_SCOPE_LIST);
	}
	
	
	public static final String LABEL_SCOPE = "Scope";
	public static final String LABEL_SCOPES = "Scope";
	
	/* NOME VISTA CUSTOM */
	public static final String SCOPE_NOME_VISTA_CUSTOM_LISTA = "scope";
	
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_SCOPE_ID = "scopeId";
	public static final String PARAMETRO_SCOPE_NOME = "scopeNome";
	public static final String PARAMETRO_SCOPE_DESCRIZIONE = "scopeDescrizione";
	public static final String PARAMETRO_SCOPE_TIPOLOGIA = "scopeTipologia";
	public static final String PARAMETRO_SCOPE_NOME_ESTERNO = "scopeNomeEsterno";
	public static final String PARAMETRO_SCOPE_CONTESTO = "scopeContesto";
	
	
	/* LABEL PARAMETRI */
	
	public static final String LABEL_PARAMETRO_SCOPE_NOME = "Nome";
	public static final String LABEL_PARAMETRO_SCOPE_DESCRIZIONE = "Descrizione";
	public static final String LABEL_PARAMETRO_SCOPE_TIPOLOGIA = CostantiControlStation.LABEL_PARAMETRO_SCOPE_TIPOLOGIA;
	public static final String LABEL_PARAMETRO_SCOPE_NOME_ESTERNO = "Identificativo Esterno";
	public static final String LABEL_PARAMETRO_SCOPE_CONTESTO = CostantiControlStation.LABEL_PARAMETRO_SCOPE_CONTESTO;
	
	public static final String LABEL_SCOPE_ESPORTA_SELEZIONATI = "Esporta";
	public static final String LABEL_SCOPE_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.SCOPE.name()+"')";
	
	private static final String VALUE_PARAMETRO_SCOPE_TIPOLOGIA_QUALSIASI = "qualsiasi" ;
	public static final String DEFAULT_VALUE_PARAMETRO_SCOPE_TIPOLOGIA = VALUE_PARAMETRO_SCOPE_TIPOLOGIA_QUALSIASI ;
	public static final String DEFAULT_VALUE_PARAMETRO_SCOPE_CONTESTO_UTILIZZO = ScopeContesto.QUALSIASI.getValue();
	
	
	private static final List<String> SCOPE_TIPOLOGIA = new ArrayList<>();
	public static List<String> getScopeTipologia() {
		return SCOPE_TIPOLOGIA;
	}
	static {
		SCOPE_TIPOLOGIA.add(VALUE_PARAMETRO_SCOPE_TIPOLOGIA_QUALSIASI);
		SCOPE_TIPOLOGIA.add("interno");
		SCOPE_TIPOLOGIA.add("esterno");
	}
	
	public static final String SCOPE_TIPOLOGIA_LABEL_INTERNO = CostantiControlStation.SCOPE_TIPOLOGIA_LABEL_INTERNO;
	public static final String SCOPE_TIPOLOGIA_LABEL_ESTERNO = CostantiControlStation.SCOPE_TIPOLOGIA_LABEL_ESTERNO;
	public static final String SCOPE_TIPOLOGIA_LABEL_QUALSIASI = CostantiControlStation.LABEL_PARAMETRO_SCOPE_TIPOLOGIA_QUALSIASI;
	private static final List<String> SCOPE_TIPOLOGIA_LABEL = new ArrayList<>();
	public static List<String> getScopeTipologiaLabel() {
		return SCOPE_TIPOLOGIA_LABEL;
	}
	static {
		SCOPE_TIPOLOGIA_LABEL.add(SCOPE_TIPOLOGIA_LABEL_QUALSIASI);
		SCOPE_TIPOLOGIA_LABEL.add(SCOPE_TIPOLOGIA_LABEL_INTERNO);
		SCOPE_TIPOLOGIA_LABEL.add(SCOPE_TIPOLOGIA_LABEL_ESTERNO);
	}
	
	private static final List<String> SCOPE_CONTESTO_UTILIZZO = new ArrayList<>();
	public static List<String> getScopeContestoUtilizzo() {
		return SCOPE_CONTESTO_UTILIZZO;
	}
	static {
		SCOPE_CONTESTO_UTILIZZO.add(ScopeContesto.QUALSIASI.getValue());
		SCOPE_CONTESTO_UTILIZZO.add(ScopeContesto.PORTA_APPLICATIVA.getValue());
		SCOPE_CONTESTO_UTILIZZO.add(ScopeContesto.PORTA_DELEGATA.getValue());
	}
	public static final String SCOPE_CONTESTO_UTILIZZO_LABEL_EROGAZIONE = CostantiControlStation.SCOPE_CONTESTO_UTILIZZO_LABEL_EROGAZIONE;
	public static final String SCOPE_CONTESTO_UTILIZZO_LABEL_FRUIZIONE = CostantiControlStation.SCOPE_CONTESTO_UTILIZZO_LABEL_FRUIZIONE;
	public static final String SCOPE_CONTESTO_UTILIZZO_LABEL_QUALSIASI = CostantiControlStation.LABEL_PARAMETRO_SCOPE_CONTESTO_QUALSIASI;
	private static final List<String> SCOPE_CONTESTO_UTILIZZO_LABEL = new ArrayList<>();
	public static List<String> getScopeContestoUtilizzoLabel() {
		return SCOPE_CONTESTO_UTILIZZO_LABEL;
	}
	static {
		SCOPE_CONTESTO_UTILIZZO_LABEL.add(SCOPE_CONTESTO_UTILIZZO_LABEL_QUALSIASI);
		SCOPE_CONTESTO_UTILIZZO_LABEL.add(SCOPE_CONTESTO_UTILIZZO_LABEL_EROGAZIONE);
		SCOPE_CONTESTO_UTILIZZO_LABEL.add(SCOPE_CONTESTO_UTILIZZO_LABEL_FRUIZIONE);
	}
	
	public static final String LABEL_IN_USO_BODY_HEADER_NESSUN_RISULTATO = "Lo scope non risulta utilizzato in alcuna configurazione";
	
	public static final String MESSAGE_METADATI_SCOPE_IDENTIFICATIVO_ESTERNO = ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME_ESTERNO + ": {0}, ";
	public static final String MESSAGE_METADATI_SCOPE_SOLO_CONTESTO = ScopeCostanti.LABEL_PARAMETRO_SCOPE_CONTESTO +": {0}";
	public static final String MESSAGE_METADATI_SCOPE_CON_TIPO = ScopeCostanti.LABEL_PARAMETRO_SCOPE_CONTESTO + ": {0}, "+ScopeCostanti.LABEL_PARAMETRO_SCOPE_TIPOLOGIA+": {1}";
}
