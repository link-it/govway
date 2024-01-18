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
package org.openspcoop2.web.ctrlstat.servlet.ruoli;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.lib.mvc.Costanti;

/**
 * RuoliCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RuoliCostanti {
	
	private RuoliCostanti() {}

	public static final String OBJECT_NAME_RUOLI = "ruoli";

	public static final String SERVLET_NAME_RUOLI_ADD = OBJECT_NAME_RUOLI+Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_RUOLI_CHANGE = OBJECT_NAME_RUOLI+Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_RUOLI_DELETE = OBJECT_NAME_RUOLI+Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_RUOLI_LIST = OBJECT_NAME_RUOLI+Costanti.STRUTS_ACTION_SUFFIX_LIST;
	private static final List<String> SERVLET_RUOLI = new ArrayList<>();
	public static List<String> getServletRuoli() {
		return SERVLET_RUOLI;
	}
	static{
		SERVLET_RUOLI.add(SERVLET_NAME_RUOLI_ADD);
		SERVLET_RUOLI.add(SERVLET_NAME_RUOLI_CHANGE);
		SERVLET_RUOLI.add(SERVLET_NAME_RUOLI_DELETE);
		SERVLET_RUOLI.add(SERVLET_NAME_RUOLI_LIST);
	}
	
	
	
	
	public static final String LABEL_RUOLO = "Ruolo";
	public static final String LABEL_RUOLI = "Ruoli";
	
	/* NOME VISTA CUSTOM */
	public static final String RUOLI_NOME_VISTA_CUSTOM_LISTA = "ruoli";
	
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_RUOLO_ID = "ruoloId";
	public static final String PARAMETRO_RUOLO_NOME = "ruoloNome";
	public static final String PARAMETRO_RUOLO_DESCRIZIONE = "ruoloDescrizione";
	public static final String PARAMETRO_RUOLO_TIPOLOGIA = "ruoloTipologia";
	public static final String PARAMETRO_RUOLO_NOME_ESTERNO = "ruoloNomeEsterno";
	public static final String PARAMETRO_RUOLO_CONTESTO = "ruoloContesto";
	
	
	/* LABEL PARAMETRI */
	
	public static final String LABEL_PARAMETRO_RUOLO_NOME = "Nome";
	public static final String LABEL_PARAMETRO_RUOLO_DESCRIZIONE = "Descrizione";
	public static final String LABEL_PARAMETRO_RUOLO_TIPOLOGIA = CostantiControlStation.LABEL_PARAMETRO_RUOLO_TIPOLOGIA;
	public static final String LABEL_PARAMETRO_RUOLO_NOME_ESTERNO = "Identificativo Esterno";
	public static final String LABEL_PARAMETRO_RUOLO_CONTESTO = CostantiControlStation.LABEL_PARAMETRO_RUOLO_CONTESTO;
	
	public static final String LABEL_RUOLI_ESPORTA_SELEZIONATI = "Esporta";
	public static final String LABEL_RUOLI_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.RUOLO.name()+"')";
	
	
	public static final String DEFAULT_VALUE_PARAMETRO_RUOLO_TIPOLOGIA = RuoloTipologia.QUALSIASI.getValue();
	public static final String DEFAULT_VALUE_PARAMETRO_RUOLO_CONTESTO_UTILIZZO = RuoloContesto.QUALSIASI.getValue();
	
	
	private static final List<String> RUOLI_TIPOLOGIA = new ArrayList<>();
	public static List<String> getRuoliTipologia() {
		return RUOLI_TIPOLOGIA;
	}
	static {
		RUOLI_TIPOLOGIA.add(RuoloTipologia.QUALSIASI.getValue());
		RUOLI_TIPOLOGIA.add(RuoloTipologia.INTERNO.getValue());
		RUOLI_TIPOLOGIA.add(RuoloTipologia.ESTERNO.getValue());
	}
	
	public static final String RUOLI_TIPOLOGIA_LABEL_INTERNO = CostantiControlStation.RUOLI_TIPOLOGIA_LABEL_INTERNO;
	public static final String RUOLI_TIPOLOGIA_LABEL_ESTERNO = CostantiControlStation.RUOLI_TIPOLOGIA_LABEL_ESTERNO;
	public static final String RUOLI_TIPOLOGIA_LABEL_QUALSIASI = CostantiControlStation.LABEL_PARAMETRO_RUOLO_TIPOLOGIA_QUALSIASI;
	private static final List<String> RUOLI_TIPOLOGIA_LABEL = new ArrayList<>();
	public static List<String> getRuoliTipologiaLabel() {
		return RUOLI_TIPOLOGIA_LABEL;
	}
	static {
		RUOLI_TIPOLOGIA_LABEL.add(RUOLI_TIPOLOGIA_LABEL_QUALSIASI);
		RUOLI_TIPOLOGIA_LABEL.add(RUOLI_TIPOLOGIA_LABEL_INTERNO);
		RUOLI_TIPOLOGIA_LABEL.add(RUOLI_TIPOLOGIA_LABEL_ESTERNO);
	}
	
	private static final List<String> RUOLI_CONTESTO_UTILIZZO = new ArrayList<>();
	public static List<String> getRuoliContestoUtilizzo() {
		return RUOLI_CONTESTO_UTILIZZO;
	}
	static {
		RUOLI_CONTESTO_UTILIZZO.add(RuoloContesto.QUALSIASI.getValue());
		RUOLI_CONTESTO_UTILIZZO.add(RuoloContesto.PORTA_APPLICATIVA.getValue());
		RUOLI_CONTESTO_UTILIZZO.add(RuoloContesto.PORTA_DELEGATA.getValue());
	}
	public static final String RUOLI_CONTESTO_UTILIZZO_LABEL_EROGAZIONE = CostantiControlStation.RUOLI_CONTESTO_UTILIZZO_LABEL_EROGAZIONE;
	public static final String RUOLI_CONTESTO_UTILIZZO_LABEL_FRUIZIONE = CostantiControlStation.RUOLI_CONTESTO_UTILIZZO_LABEL_FRUIZIONE;
	public static final String RUOLI_CONTESTO_UTILIZZO_LABEL_QUALSIASI = CostantiControlStation.LABEL_PARAMETRO_RUOLO_CONTESTO_QUALSIASI;
	private static final List<String> RUOLI_CONTESTO_UTILIZZO_LABEL = new ArrayList<>();
	public static List<String> getRuoliContestoUtilizzoLabel() {
		return RUOLI_CONTESTO_UTILIZZO_LABEL;
	}
	static {
		RUOLI_CONTESTO_UTILIZZO_LABEL.add(RUOLI_CONTESTO_UTILIZZO_LABEL_QUALSIASI);
		RUOLI_CONTESTO_UTILIZZO_LABEL.add(RUOLI_CONTESTO_UTILIZZO_LABEL_EROGAZIONE);
		RUOLI_CONTESTO_UTILIZZO_LABEL.add(RUOLI_CONTESTO_UTILIZZO_LABEL_FRUIZIONE);
	}
	
	public static final String MESSAGE_METADATI_RUOLO_IDENTIFICATIVO_ESTERNO = RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME_ESTERNO + ": {0}, ";
	public static final String MESSAGE_METADATI_RUOLO_TIPO_E_CONTESTO = CostantiControlStation.LABEL_PARAMETRO_RUOLO_TIPOLOGIA + ": {0}, "+ CostantiControlStation.LABEL_PARAMETRO_RUOLO_CONTESTO +": {1}";
	
	public static final String LABEL_IN_USO_BODY_HEADER_NESSUN_RISULTATO = "Il ruolo non risulta utilizzato in alcuna configurazione";
}
