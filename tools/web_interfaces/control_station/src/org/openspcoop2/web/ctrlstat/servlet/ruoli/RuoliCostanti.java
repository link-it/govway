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
package org.openspcoop2.web.ctrlstat.servlet.ruoli;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;

/**
 * RuoliCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RuoliCostanti {

	public final static String OBJECT_NAME_RUOLI = "ruoli";

	public final static String SERVLET_NAME_RUOLI_ADD = OBJECT_NAME_RUOLI+"Add.do";
	public final static String SERVLET_NAME_RUOLI_CHANGE = OBJECT_NAME_RUOLI+"Change.do";
	public final static String SERVLET_NAME_RUOLI_DELETE = OBJECT_NAME_RUOLI+"Del.do";
	public final static String SERVLET_NAME_RUOLI_LIST = OBJECT_NAME_RUOLI+"List.do";
	public final static Vector<String> SERVLET_RUOLI = new Vector<String>();
	static{
		SERVLET_RUOLI.add(SERVLET_NAME_RUOLI_ADD);
		SERVLET_RUOLI.add(SERVLET_NAME_RUOLI_CHANGE);
		SERVLET_RUOLI.add(SERVLET_NAME_RUOLI_DELETE);
		SERVLET_RUOLI.add(SERVLET_NAME_RUOLI_LIST);
	}
	
	
	
	
	public final static String LABEL_RUOLO = "Ruolo";
	public final static String LABEL_RUOLI = "Ruoli";
	
	/* NOME VISTA CUSTOM */
	public final static String RUOLI_NOME_VISTA_CUSTOM_LISTA = "ruoli";
	
	
	/* PARAMETRI */
	
	public final static String PARAMETRO_RUOLO_ID = "ruoloId";
	public final static String PARAMETRO_RUOLO_NOME = "ruoloNome";
	public final static String PARAMETRO_RUOLO_DESCRIZIONE = "ruoloDescrizione";
	public final static String PARAMETRO_RUOLO_TIPOLOGIA = "ruoloTipologia";
	public final static String PARAMETRO_RUOLO_NOME_ESTERNO = "ruoloNomeEsterno";
	public final static String PARAMETRO_RUOLO_CONTESTO = "ruoloContesto";
	
	
	/* LABEL PARAMETRI */
	
	public final static String LABEL_PARAMETRO_RUOLO_NOME = "Nome";
	public final static String LABEL_PARAMETRO_RUOLO_DESCRIZIONE = "Descrizione";
	public final static String LABEL_PARAMETRO_RUOLO_TIPOLOGIA = CostantiControlStation.LABEL_PARAMETRO_RUOLO_TIPOLOGIA;
	public final static String LABEL_PARAMETRO_RUOLO_NOME_ESTERNO = "Identificativo Esterno";
	public final static String LABEL_PARAMETRO_RUOLO_CONTESTO = CostantiControlStation.LABEL_PARAMETRO_RUOLO_CONTESTO;
	
	public final static String LABEL_RUOLI_ESPORTA_SELEZIONATI = "Esporta";
	public final static String LABEL_RUOLI_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.RUOLO.name()+"')";
	
	
	public final static String DEFAULT_VALUE_PARAMETRO_RUOLO_TIPOLOGIA = RuoloTipologia.QUALSIASI.getValue();
	public final static String DEFAULT_VALUE_PARAMETRO_RUOLO_CONTESTO_UTILIZZO = RuoloContesto.QUALSIASI.getValue();
	
	
	public final static List<String> RUOLI_TIPOLOGIA = new ArrayList<String>();
	static {
		RUOLI_TIPOLOGIA.add(RuoloTipologia.QUALSIASI.getValue());
		RUOLI_TIPOLOGIA.add(RuoloTipologia.INTERNO.getValue());
		RUOLI_TIPOLOGIA.add(RuoloTipologia.ESTERNO.getValue());
	}
	
	public final static String RUOLI_TIPOLOGIA_LABEL_INTERNO = CostantiControlStation.RUOLI_TIPOLOGIA_LABEL_INTERNO;
	public final static String RUOLI_TIPOLOGIA_LABEL_ESTERNO = CostantiControlStation.RUOLI_TIPOLOGIA_LABEL_ESTERNO;
	public final static String RUOLI_TIPOLOGIA_LABEL_QUALSIASI = CostantiControlStation.LABEL_PARAMETRO_RUOLO_TIPOLOGIA_QUALSIASI;
	public final static List<String> RUOLI_TIPOLOGIA_LABEL = new ArrayList<String>();
	static {
		RUOLI_TIPOLOGIA_LABEL.add(RUOLI_TIPOLOGIA_LABEL_QUALSIASI);
		RUOLI_TIPOLOGIA_LABEL.add(RUOLI_TIPOLOGIA_LABEL_INTERNO);
		RUOLI_TIPOLOGIA_LABEL.add(RUOLI_TIPOLOGIA_LABEL_ESTERNO);
	}
	
	public final static List<String> RUOLI_CONTESTO_UTILIZZO = new ArrayList<String>();
	static {
		RUOLI_CONTESTO_UTILIZZO.add(RuoloContesto.QUALSIASI.getValue());
		RUOLI_CONTESTO_UTILIZZO.add(RuoloContesto.PORTA_APPLICATIVA.getValue());
		RUOLI_CONTESTO_UTILIZZO.add(RuoloContesto.PORTA_DELEGATA.getValue());
	}
	public final static String RUOLI_CONTESTO_UTILIZZO_LABEL_EROGAZIONE = CostantiControlStation.RUOLI_CONTESTO_UTILIZZO_LABEL_EROGAZIONE;
	public final static String RUOLI_CONTESTO_UTILIZZO_LABEL_FRUIZIONE = CostantiControlStation.RUOLI_CONTESTO_UTILIZZO_LABEL_FRUIZIONE;
	public final static String RUOLI_CONTESTO_UTILIZZO_LABEL_QUALSIASI = CostantiControlStation.LABEL_PARAMETRO_RUOLO_CONTESTO_QUALSIASI;
	public final static List<String> RUOLI_CONTESTO_UTILIZZO_LABEL = new ArrayList<String>();
	static {
		RUOLI_CONTESTO_UTILIZZO_LABEL.add(RUOLI_CONTESTO_UTILIZZO_LABEL_QUALSIASI);
		RUOLI_CONTESTO_UTILIZZO_LABEL.add(RUOLI_CONTESTO_UTILIZZO_LABEL_EROGAZIONE);
		RUOLI_CONTESTO_UTILIZZO_LABEL.add(RUOLI_CONTESTO_UTILIZZO_LABEL_FRUIZIONE);
	}
	
	public final static String MESSAGE_METADATI_RUOLO_TIPO_E_CONTESTO = CostantiControlStation.LABEL_PARAMETRO_RUOLO_TIPOLOGIA + ": {0}, "+ CostantiControlStation.LABEL_PARAMETRO_RUOLO_CONTESTO +": {1}";
	
	public final static String LABEL_IN_USO_BODY_HEADER_NESSUN_RISULTATO = "Il ruolo non risulta utilizzato in alcuna configurazione";
}
