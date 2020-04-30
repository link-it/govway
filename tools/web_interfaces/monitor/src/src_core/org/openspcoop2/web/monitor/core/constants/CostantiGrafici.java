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
package org.openspcoop2.web.monitor.core.constants;

/**
 * CostantiGrafici
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class CostantiGrafici {

	public static final String DAL_AL_PATTERN = " (dal {0} al {1} )";
	
	public static final String PATTERN_DD_MMMMM_YYYY = "dd MMMMM yyyy";
	public static final String PATTERN_HH_MM = "HH:mm";
	public static final String PATTERN_DD_MMMMM_YYYY_ORE_HH_MM = "dd MMMMM yyyy', ore' HH:mm";
	public static final String PATTERN_HH = "HH";
	public static final String PATTERN_EEE_DD_MM = "EEE dd/MM";
	public static final String PATTERN_DD_MM = "dd/MM";
	public static final String PATTERN_MMM_YY = "MMM/yy";
	
	public static final String LABEL_SUCC = "succ";
	public static final String LABEL_ATTUALE = "attuale";
	public static final String LABEL_PREC = "prec";
	public static final String PUNTO = ".";
	public static final String WHITE_SPACE = " ";
	
	
	public static final String DATI_NON_PRESENTI = "Non esistono transazioni per il periodo selezionato";
	public static final String ALTRI_LABEL = "Altri";
	public static final String NUMERO_ESITI_LABEL = "Numero Esiti";
	public static final String ESITO_TRANSAZIONI_LABEL = "Esito Transazioni";
	public static final String TOTALE_TRANSAZIONI_LABEL = "Totale Transazioni";
	public static final String DISTRIBUZIONE_ESITO_DEI_MESSAGGI_NELL_ULTIMO_PERIODO = "Distribuzione Esito dei messaggi nell'ultimo periodo.";
	
	public static final String TOOLTIP_SUFFIX = "_tooltip";
	
	public static final String TITOLO_KEY= "titolo";
	public static final String SOTTOTITOLO_KEY= "sottotitolo";
	public static final String Y_AXIS_LABEL_KEY= "yAxisLabel";
	public static final String X_AXIS_LABEL_DIAGONALI_KEY= "xAxisLabelDiagonali";
	public static final String CATEGORIE_KEY= "categorie";
	public static final String CATEGORIA_KEY= "categoria";
	public static final String MOSTRA_LEGENDA_KEY= "mostraLegenda";
	public static final String LABEL_KEY= "label";
	public static final String KEY_KEY= "key";
	public static final String DATI_KEY= "dati";
	public static final String DATA_KEY= "data";
	public static final String USA_COLORI_AUTOMATICI_KEY= "coloriAutomatici";
	public static final String COLORE_KEY= "colore";
	public static final String NO_DATA_KEY= "noData";
	public static final String X_AXIS_LABEL_DIREZIONE_KEY= "xAxisLabelDirezione";
	public static final String X_AXIS_GRID_LINES_KEY= "xAxisGridLines";
	public static final int DIREZIONE_LABEL_ORIZZONTALE= 0;
	public static final int DIREZIONE_LABEL_OBLIQUO= -45;
	public static final int  DIREZIONE_LABEL_VERTICALE= -90;
	public static final String DIREZIONE_LABEL_ORIZZONTALE_LABEL= "Orizzontale";
	public static final String DIREZIONE_LABEL_OBLIQUO_LABEL = "Obliquo";
	public static final String  DIREZIONE_LABEL_VERTICALE_LABEL= "Verticale";
	
	public static final String ABILITA_CLICK_LEGENDA_KEY= "clickItemLegenda";
	public static final String VALORE_REALE_TORTA_KEY= "valoreRealeTorta";
	public static final String COLONNE_LEGENDA_KEY= "colonneLegenda";
	public static final String LIMITE_COLONNE_LEGENDA_KEY= "limiteColonneLegenda";
	public static final int COLONNE_LEGENDA_DEFAULT_VALUE = 16;
	
	public static final String VISUALIZZA_VALUE_NELLA_LABEL_LEGENDA= "valueOnLegend";
	
	public static final String LIMITE_LUNGHEZZA_LABEL_GRAFICO_KEY = "limit";
	public static final int LIMITE_LUNGHEZZA_LABEL_GRAFICO_DEFAULT_VALUE = 50;
	public static final String LIMITE_LUNGHEZZA_LABEL_LEGENDA_KEY= "limitLegenda";
	public static final int LIMITE_LUNGHEZZA_LABEL_LEGENDA_DEFAULT_VALUE = 50;
	
	public static final String OK_KEY= "ok";
	public static final String OK_LABEL= "Ok";
	
	public static final String FAULT_KEY= "fault";
	public static final String FAULT_LABEL= "Fault Applicativo";
	
	public static final String ERRORE_KEY= "errore";
	public static final String ERRORE_LABEL= "Fallite";
	
	public static final String TOTALE_KEY= "totale";
	public static final String TOTALE_LABEL= "Totale";
}
