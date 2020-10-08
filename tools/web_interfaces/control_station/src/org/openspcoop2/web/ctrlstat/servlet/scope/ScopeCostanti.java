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
package org.openspcoop2.web.ctrlstat.servlet.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openspcoop2.core.registry.constants.ScopeContesto;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
//import org.openspcoop2.core.registry.constants.ScopeTipologia;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;

/**
 * ScopeCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ScopeCostanti {

	public final static String OBJECT_NAME_SCOPE = "scope";

	public final static String SERVLET_NAME_SCOPE_ADD = OBJECT_NAME_SCOPE+"Add.do";
	public final static String SERVLET_NAME_SCOPE_CHANGE = OBJECT_NAME_SCOPE+"Change.do";
	public final static String SERVLET_NAME_SCOPE_DELETE = OBJECT_NAME_SCOPE+"Del.do";
	public final static String SERVLET_NAME_SCOPE_LIST = OBJECT_NAME_SCOPE+"List.do";
	public final static Vector<String> SERVLET_SCOPE = new Vector<String>();
	static{
		SERVLET_SCOPE.add(SERVLET_NAME_SCOPE_ADD);
		SERVLET_SCOPE.add(SERVLET_NAME_SCOPE_CHANGE);
		SERVLET_SCOPE.add(SERVLET_NAME_SCOPE_DELETE);
		SERVLET_SCOPE.add(SERVLET_NAME_SCOPE_LIST);
	}
	
	
	public final static String LABEL_SCOPE = "Scope";
	public final static String LABEL_SCOPES = "Scope";
	
	/* NOME VISTA CUSTOM */
	public final static String SCOPE_NOME_VISTA_CUSTOM_LISTA = "scope";
	
	
	/* PARAMETRI */
	
	public final static String PARAMETRO_SCOPE_ID = "scopeId";
	public final static String PARAMETRO_SCOPE_NOME = "scopeNome";
	public final static String PARAMETRO_SCOPE_DESCRIZIONE = "scopeDescrizione";
	public final static String PARAMETRO_SCOPE_TIPOLOGIA = "scopeTipologia";
	public final static String PARAMETRO_SCOPE_NOME_ESTERNO = "scopeNomeEsterno";
	public final static String PARAMETRO_SCOPE_CONTESTO = "scopeContesto";
	
	
	/* LABEL PARAMETRI */
	
	public final static String LABEL_PARAMETRO_SCOPE_NOME = "Nome";
	public final static String LABEL_PARAMETRO_SCOPE_DESCRIZIONE = "Descrizione";
	public final static String LABEL_PARAMETRO_SCOPE_TIPOLOGIA = CostantiControlStation.LABEL_PARAMETRO_SCOPE_TIPOLOGIA;
	public final static String LABEL_PARAMETRO_SCOPE_NOME_ESTERNO = "Identificativo Esterno";
	public final static String LABEL_PARAMETRO_SCOPE_CONTESTO = CostantiControlStation.LABEL_PARAMETRO_SCOPE_CONTESTO;
	
	public final static String LABEL_SCOPE_ESPORTA_SELEZIONATI = "Esporta";
	public final static String LABEL_SCOPE_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.SCOPE.name()+"')";
	
	
	public final static String DEFAULT_VALUE_PARAMETRO_SCOPE_TIPOLOGIA = "qualsiasi" ; //ScopeTipologia.QUALSIASI.getValue();
	public final static String DEFAULT_VALUE_PARAMETRO_SCOPE_CONTESTO_UTILIZZO = ScopeContesto.QUALSIASI.getValue();
	
	
	public final static List<String> SCOPE_TIPOLOGIA = new ArrayList<String>();
	static {
//		SCOPE_TIPOLOGIA.add(ScopeTipologia.QUALSIASI.getValue());
//		SCOPE_TIPOLOGIA.add(ScopeTipologia.INTERNO.getValue());
//		SCOPE_TIPOLOGIA.add(ScopeTipologia.ESTERNO.getValue());
		SCOPE_TIPOLOGIA.add("qualsiasi");
		SCOPE_TIPOLOGIA.add("interno");
		SCOPE_TIPOLOGIA.add("esterno");
	}
	
	public final static String SCOPE_TIPOLOGIA_LABEL_INTERNO = CostantiControlStation.SCOPE_TIPOLOGIA_LABEL_INTERNO;
	public final static String SCOPE_TIPOLOGIA_LABEL_ESTERNO = CostantiControlStation.SCOPE_TIPOLOGIA_LABEL_ESTERNO;
	public final static String SCOPE_TIPOLOGIA_LABEL_QUALSIASI = CostantiControlStation.LABEL_PARAMETRO_SCOPE_TIPOLOGIA_QUALSIASI;
	public final static List<String> SCOPE_TIPOLOGIA_LABEL = new ArrayList<String>();
	static {
		SCOPE_TIPOLOGIA_LABEL.add(SCOPE_TIPOLOGIA_LABEL_QUALSIASI);
		SCOPE_TIPOLOGIA_LABEL.add(SCOPE_TIPOLOGIA_LABEL_INTERNO);
		SCOPE_TIPOLOGIA_LABEL.add(SCOPE_TIPOLOGIA_LABEL_ESTERNO);
	}
	
	public final static List<String> SCOPE_CONTESTO_UTILIZZO = new ArrayList<String>();
	static {
		SCOPE_CONTESTO_UTILIZZO.add(ScopeContesto.QUALSIASI.getValue());
		SCOPE_CONTESTO_UTILIZZO.add(ScopeContesto.PORTA_APPLICATIVA.getValue());
		SCOPE_CONTESTO_UTILIZZO.add(ScopeContesto.PORTA_DELEGATA.getValue());
	}
	public final static String SCOPE_CONTESTO_UTILIZZO_LABEL_EROGAZIONE = CostantiControlStation.SCOPE_CONTESTO_UTILIZZO_LABEL_EROGAZIONE;
	public final static String SCOPE_CONTESTO_UTILIZZO_LABEL_FRUIZIONE = CostantiControlStation.SCOPE_CONTESTO_UTILIZZO_LABEL_FRUIZIONE;
	public final static String SCOPE_CONTESTO_UTILIZZO_LABEL_QUALSIASI = CostantiControlStation.LABEL_PARAMETRO_SCOPE_CONTESTO_QUALSIASI;
	public final static List<String> SCOPE_CONTESTO_UTILIZZO_LABEL = new ArrayList<String>();
	static {
		SCOPE_CONTESTO_UTILIZZO_LABEL.add(SCOPE_CONTESTO_UTILIZZO_LABEL_QUALSIASI);
		SCOPE_CONTESTO_UTILIZZO_LABEL.add(SCOPE_CONTESTO_UTILIZZO_LABEL_EROGAZIONE);
		SCOPE_CONTESTO_UTILIZZO_LABEL.add(SCOPE_CONTESTO_UTILIZZO_LABEL_FRUIZIONE);
	}
	
	public final static String LABEL_IN_USO_BODY_HEADER_NESSUN_RISULTATO = "Lo scope non risulta utilizzato in alcuna configurazione";
	
	public final static String MESSAGE_METADATI_SCOPE_SOLO_CONTESTO = ScopeCostanti.LABEL_PARAMETRO_SCOPE_CONTESTO +": {0}";
	public final static String MESSAGE_METADATI_SCOPE_CON_TIPO = ScopeCostanti.LABEL_PARAMETRO_SCOPE_CONTESTO + ": {0}, "+ScopeCostanti.LABEL_PARAMETRO_SCOPE_TIPOLOGIA+": {1}";
}
