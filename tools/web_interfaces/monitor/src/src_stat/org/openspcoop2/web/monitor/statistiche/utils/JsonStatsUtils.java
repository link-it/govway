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
package org.openspcoop2.web.monitor.statistiche.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.statistiche.constants.Colors;
import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.web.monitor.core.bean.ApplicationBean;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.datamodel.Res;
import org.openspcoop2.web.monitor.core.datamodel.ResBase;
import org.openspcoop2.web.monitor.core.datamodel.ResDistribuzione;
import org.openspcoop2.web.monitor.core.datamodel.ResDistribuzione3D;
import org.openspcoop2.web.monitor.core.datamodel.ResDistribuzione3DCustom;
import org.openspcoop2.web.monitor.statistiche.bean.StatsSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiGrafici;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * JsonStatsUtils
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class JsonStatsUtils {
	
	private JsonStatsUtils() {}

	/* ******* PIE CHART ********** */

	public static ObjectNode getJsonPieChartDistribuzione(List<ResDistribuzione> list, StatsSearchForm search, String caption, String subCaption, Integer slice) throws UtilsException{
				
		JSONUtils jsonUtils = JSONUtils.getInstance();
		
		ObjectNode grafico = jsonUtils.newObjectNode();
		
		grafico.put(CostantiGrafici.USA_COLORI_AUTOMATICI_KEY, true);
		grafico.put(CostantiGrafici.X_AXIS_GRID_LINES_KEY,true);
		grafico.put(CostantiGrafici.TITOLO_KEY, caption);
		grafico.put(CostantiGrafici.SOTTOTITOLO_KEY, subCaption);
		grafico.put(CostantiGrafici.ABILITA_CLICK_LEGENDA_KEY, false);
		grafico.put(CostantiGrafici.VALORE_REALE_TORTA_KEY,true);
		grafico.put(CostantiGrafici.COLONNE_LEGENDA_KEY, CostantiGrafici.COLONNE_LEGENDA_DEFAULT_VALUE);
		grafico.put(CostantiGrafici.LIMITE_COLONNE_LEGENDA_KEY, CostantiGrafici.COLONNE_LEGENDA_DEFAULT_VALUE);
		grafico.put(CostantiGrafici.VISUALIZZA_VALUE_NELLA_LABEL_LEGENDA, true);

		boolean occupazioneBanda = false;
		boolean tempoMedio = false;
		TipoVisualizzazione tipoVisualizzazione = search.getTipoVisualizzazione();
		if(tipoVisualizzazione.equals(TipoVisualizzazione.DIMENSIONE_TRANSAZIONI)) {
			occupazioneBanda = true;
		}
		else if(tipoVisualizzazione.equals(TipoVisualizzazione.TEMPO_MEDIO_RISPOSTA)) {
			tempoMedio = true;
		}

		ArrayNode dati = jsonUtils.newArrayNode();

		int maxLenghtLabel = 0;
		if(list!=null  && !list.isEmpty()){
			int i = 0;
			long altriSum=0;
			int altriSumNumeroItem=0;

			for (ResDistribuzione entry : list) {
				String r = entry.getRisultato();
				Number sum = entry.getSomma();

				if(++i<=slice) {
					if(r.length() > maxLenghtLabel)
						maxLenghtLabel = r.length();

					String toolText = StatsUtils.getToolText(search,sum); 
					if(!entry.getParentMap().isEmpty())
						toolText = StatsUtils.getToolTextConParent(search, r ,entry.getParentMap(), sum);

					ObjectNode spicchio = jsonUtils.newObjectNode();
					spicchio.put(CostantiGrafici.LABEL_KEY, escapeJsonLabel(r));
					spicchio.put(CostantiGrafici.TOOLTIP_KEY, toolText);
					if(sum instanceof Long v) {
						spicchio.put(CostantiGrafici.VALUE_KEY, v);
					}
					else if(sum instanceof Integer vi) {
						spicchio.put(CostantiGrafici.VALUE_KEY, vi);
					}
					else if(sum instanceof Float vf) {
						spicchio.put(CostantiGrafici.VALUE_KEY, vf);
					}
					else if(sum instanceof Double vd) {
						spicchio.put(CostantiGrafici.VALUE_KEY, vd);
					}

					dati.add(spicchio);
				} else{
					altriSum+=sum.longValue();
					altriSumNumeroItem++;
				}
			}

			if(i>slice){

				long v = altriSum;
				if(altriSumNumeroItem>1 && (occupazioneBanda || tempoMedio) &&
					(altriSumNumeroItem>0)
					){
					v = v / altriSumNumeroItem;
				}

				String toolText = StatsUtils.getToolText(search,v); 

				ObjectNode spicchio = jsonUtils.newObjectNode();
				spicchio.put(CostantiGrafici.LABEL_KEY, CostantiGrafici.ALTRI_LABEL);
				spicchio.put(CostantiGrafici.TOOLTIP_KEY, toolText);
				spicchio.put(CostantiGrafici.VALUE_KEY, v);

				dati.add(spicchio);
			}
			grafico.set(CostantiGrafici.DATI_KEY, dati);
			
			if(maxLenghtLabel > CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_LEGENDA_DEFAULT_VALUE)
				grafico.put(CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_LEGENDA_KEY, CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_LEGENDA_DEFAULT_VALUE);
		}
		else{
			grafico.put(CostantiGrafici.NO_DATA_KEY, CostantiGrafici.DATI_NON_PRESENTI);
		}
		

		return grafico;		

	}


	/* ************** BAR CHART **************** */

	public static ObjectNode getJsonBarChartDistribuzione(List<ResDistribuzione> list, StatsSearchForm search, String caption, String subCaption, String direzioneLabelParam, Integer slice) throws UtilsException{
		List<ResBase> listI = new ArrayList<>();
		listI.addAll(list);
		return getJsonBarChartDistribuzioneEngine(listI, search, caption, subCaption, direzioneLabelParam, slice, null, null);
	}
	public static ObjectNode getJsonBarChartDistribuzione(List<Res> list, StatsSearchForm search, String caption, String subCaption, String direzioneLabelParam, Integer slice, Integer numeroLabel, StatisticType tempo) throws UtilsException{
		List<ResBase> listI = new ArrayList<>();
		listI.addAll(list);
		return getJsonBarChartDistribuzioneEngine(listI, search, caption, subCaption, direzioneLabelParam, slice, numeroLabel, tempo);
	}

	private static ObjectNode getJsonBarChartDistribuzioneEngine(List<ResBase> list, StatsSearchForm search, String caption, String subCaption, String direzioneLabelParam, Integer sliceParam, Integer numeroLabel, StatisticType tempo
			,		String ... series
			) throws UtilsException{
		JSONUtils jsonUtils = JSONUtils.getInstance();
		
		ObjectNode grafico = jsonUtils.newObjectNode();

		grafico.put(CostantiGrafici.USA_COLORI_AUTOMATICI_KEY, false);
		grafico.put(CostantiGrafici.X_AXIS_GRID_LINES_KEY,true);
		grafico.put(CostantiGrafici.TITOLO_KEY, caption);
		grafico.put(CostantiGrafici.SOTTOTITOLO_KEY, subCaption);
		grafico.put(CostantiGrafici.X_AXIS_LABEL_DIREZIONE_KEY, getDirezioneLabel(direzioneLabelParam));

		SimpleDateFormat sdf = null;
		SimpleDateFormat sdfLast = null;
		if(tempo!=null) {
			sdfLast = new SimpleDateFormat(CostantiGrafici.PATTERN_HH, ApplicationBean.getInstance().getLocale());
			if (StatisticType.ORARIA.equals(tempo)) {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY_HH, ApplicationBean.getInstance().getLocale());
			} else {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY, ApplicationBean.getInstance().getLocale());
			}
		}

		ArrayNode categorie = jsonUtils.newArrayNode();

		// calcolo series
		boolean occupazioneBanda = false;
		boolean tempoMedio = false;
		boolean distribuzionePerEsiti = false;
		if(search.isAndamentoTemporalePerEsiti()){
			distribuzionePerEsiti = true;
		}
		else{
			TipoVisualizzazione tipoVisualizzazione = search.getTipoVisualizzazione();
			if(tipoVisualizzazione.equals(TipoVisualizzazione.DIMENSIONE_TRANSAZIONI)) {
				occupazioneBanda = true;
			}
			else if(tipoVisualizzazione.equals(TipoVisualizzazione.TEMPO_MEDIO_RISPOSTA)) {
				tempoMedio = true;
			}
		}

		int numeroCategorie = 1;
		boolean showSeries = false;
		if(list!=null  && !list.isEmpty()){
			if(series!=null && series.length>0){
				numeroCategorie = series.length;
				for (int i = 0 ; i < series.length ; i++) {
					String string = series[i];
					ObjectNode categoria = jsonUtils.newObjectNode();
					categoria.put(CostantiGrafici.KEY_KEY , string);
					categoria.put(CostantiGrafici.LABEL_KEY , string);
					categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_TOTALE);
					categorie.add(categoria);
				}

				showSeries = true;
				/**				color = Colors.CODE_TOTALE;  Gestire i colori in questo caso. Comunque per ora è un caso non usato, il metodo con le series come parametro è commentato*/
			}
			else {
				if(distribuzionePerEsiti){
					numeroCategorie = 3;
					showSeries = true;

					ObjectNode categoria = jsonUtils.newObjectNode();
					categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.OK_KEY);
					categoria.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.OK_LABEL);
					categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_OK);
					categorie.add(categoria);

					ObjectNode categoria2 = jsonUtils.newObjectNode();
					categoria2.put(CostantiGrafici.KEY_KEY , CostantiGrafici.FAULT_KEY);
					categoria2.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.FAULT_LABEL);
					categoria2.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_FAULT_APPLICATIVO);
					categorie.add(categoria2);

					ObjectNode categoria3 = jsonUtils.newObjectNode();
					categoria3.put(CostantiGrafici.KEY_KEY , CostantiGrafici.ERRORE_KEY);
					categoria3.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.ERRORE_LABEL);
					categoria3.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_ERROR);
					categorie.add(categoria3);

				}
				else if(occupazioneBanda) {
					numeroCategorie = search.getTipiBandaImpostati().size();
					showSeries = numeroCategorie > 1;
					for (int i = 0; i < numeroCategorie; i++) {
						String[] strings = search.getTipiBanda();
						if(strings != null && strings.length == numeroCategorie){
							String tipoLat = strings[i];
							if(tipoLat != null){
								if(tipoLat.equals("0")){
									if(showSeries){
										ObjectNode categoria = jsonUtils.newObjectNode();
										categoria.put(CostantiGrafici.KEY_KEY , TipoBanda.COMPLESSIVA.getValue().toLowerCase().replace(" ", "_"));
										categoria.put(CostantiGrafici.LABEL_KEY , TipoBanda.COMPLESSIVA.getValue());
										categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_BANDA_COMPLESSIVA);
										categorie.set(i,categoria);
									}
								}
								else if(tipoLat.equals("1")){
									if(showSeries){
										ObjectNode categoria = jsonUtils.newObjectNode();
										categoria.put(CostantiGrafici.KEY_KEY , TipoBanda.INTERNA.getValue().toLowerCase().replace(" ", "_"));
										categoria.put(CostantiGrafici.LABEL_KEY , TipoBanda.INTERNA.getValue());
										categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_BANDA_INTERNA);
										categorie.set(i,categoria);
									}
								}
								else if(tipoLat.equals("2")){
									if(showSeries){
										ObjectNode categoria = jsonUtils.newObjectNode();
										categoria.put(CostantiGrafici.KEY_KEY , TipoBanda.ESTERNA.getValue().toLowerCase().replace(" ", "_"));
										categoria.put(CostantiGrafici.LABEL_KEY , TipoBanda.ESTERNA.getValue());
										categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_BANDA_ESTERNA);
										categorie.set(i,categoria);
									}
								}
							}
						}
					}
				}
				else if(tempoMedio) {
					numeroCategorie = search.getTipiLatenzaImpostati().size();
					showSeries = numeroCategorie > 1;
					for (int i = 0; i < numeroCategorie; i++) {
						String[] strings = search.getTipiLatenza();
						if(strings != null && strings.length == numeroCategorie){
							String tipoLat = strings[i];
							if(tipoLat != null && showSeries){
								if(tipoLat.equals("0")){
									if(showSeries){
										ObjectNode categoria = jsonUtils.newObjectNode();
										categoria.put(CostantiGrafici.KEY_KEY , TipoLatenza.LATENZA_TOTALE.getValue().toLowerCase().replace(" ", "_"));
										categoria.put(CostantiGrafici.LABEL_KEY , TipoLatenza.LATENZA_TOTALE.getValue());
										categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_LATENZA_TOTALE);
										categorie.set(i,categoria);
									}
								}
								else if(tipoLat.equals("1")){
									if(showSeries){
										ObjectNode categoria = jsonUtils.newObjectNode();
										categoria.put(CostantiGrafici.KEY_KEY , TipoLatenza.LATENZA_SERVIZIO.getValue().toLowerCase().replace(" ", "_"));
										categoria.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.LABEL_TIPO_LATENZA_LATENZA_SERVIZIO);
										categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_LATENZA_SERVIZIO);
										categorie.set(i,categoria);
									}
								}
								else if(tipoLat.equals("2")){
									if(showSeries){
										ObjectNode categoria = jsonUtils.newObjectNode();
										categoria.put(CostantiGrafici.KEY_KEY , TipoLatenza.LATENZA_PORTA.getValue().toLowerCase().replace(" ", "_"));
										categoria.put(CostantiGrafici.LABEL_KEY , TipoLatenza.LATENZA_PORTA.getValue());
										categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_LATENZA_PORTA);
										categorie.set(i,categoria);
									}
								}
							}
						}
					}
				}
			}
		}
		if(categorie.isEmpty()){
			ObjectNode categoria = jsonUtils.newObjectNode();
			categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.TOTALE_KEY);
			categoria.put(CostantiGrafici.LABEL_KEY , StatsUtils.getSubCaption(search,true));
			categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_TOTALE);
			categorie.add(categoria);
		}

		// Inserisco le catergorie del grafico
		grafico.set(CostantiGrafici.CATEGORIE_KEY, categorie);

		grafico.put(CostantiGrafici.MOSTRA_LEGENDA_KEY, showSeries);

		String yAxisName = StatsUtils.getSubCaption(search,true);

		grafico.put(CostantiGrafici.Y_AXIS_LABEL_KEY, yAxisName);

		ArrayNode dati = jsonUtils.newArrayNode();
		// colleziono i dati
		if(list!=null  && !list.isEmpty()){
			int iterazione = 0;
			long altriSumSerie1=0;
			int altriSumSerie1NumeroItem=0;
			long altriSumSerie2=0;
			int altriSumSerie2NumeroItem=0;
			long altriSumSerie3=0;
			int altriSumSerie3NumeroItem=0;



			int slice = sliceParam;
			if(slice != Integer.MAX_VALUE) // viene usato il maxValue per "disattivare" lo slice
				slice = slice * numeroCategorie;

			int maxLenghtLabel = 0;

			List<Integer> posizioniDaVisualizzare = getListaIndiciLabelDaVisualizzare(list.size(), numeroLabel);
			boolean nascondiLabel = posizioniDaVisualizzare != null;

			for (int z = 0 ; z <list.size() ; z++) {
				ResBase entry = list.get(z);
				ObjectNode bar = jsonUtils.newObjectNode();

				for (int j = 0; j < numeroCategorie; j++) {
					// key che identifica la serie
					JsonNode keyNode = categorie.get(j).get(CostantiGrafici.KEY_KEY);
					String key = keyNode.asText();

					Number sum = null;
					if(entry instanceof ResDistribuzione){
						sum = entry.getSomma();
					}
					else if(entry instanceof Res){
						sum = entry.getSomme().get(j);
					}

					if(++iterazione<=slice) {

						String r = null;
						if(entry instanceof ResDistribuzione resDistribuzione){
							r = resDistribuzione.getRisultato();
						}
						else if(entry instanceof Res res){
							
							Date rDate = res.getRisultato();
							Calendar c = Calendar.getInstance();
							c.setTime(rDate);
							
							StringBuilder sb = new StringBuilder();
							if (StatisticType.ORARIA.equals(tempo)) {
								sdfLast.applyPattern(CostantiGrafici.PATTERN_HH);
								c.add(Calendar.HOUR, 1);
								sb.append(sdf.format(rDate) + "-").append( sdfLast.format(c.getTime()));
							} else if (StatisticType.SETTIMANALE.equals(tempo)) {
								// settimanale
								sdf.applyPattern(CostantiGrafici.PATTERN_DD_MM_YY);
								sdfLast.applyPattern(CostantiGrafici.PATTERN_DD_MM_YY);

								c.add(Calendar.WEEK_OF_MONTH, 1);
								c.add(Calendar.DAY_OF_WEEK, -1);

								sb.append(sdf.format(rDate) + "-").append( sdfLast.format(c.getTime()));

							} else if (StatisticType.MENSILE.equals(tempo)) {
								// mensile
								sdf.applyPattern(CostantiGrafici.PATTERN_MMM_YY);
								sb.append(sdf.format(rDate));

							} else {
								// giornaliero
								sb.append(sdf.format(rDate));
							}
							r = sb.toString();
						}
						else{
							r = "ERROR: TIPO ["+entry.getClass().getName()+"] NON GESTITO";
						}

						// Iterazione 1 memorizzo la label della barra
						if(j==0){
							if(r.length() > maxLenghtLabel)
								maxLenghtLabel = r.length();

							bar.put(CostantiGrafici.DATA_KEY, escapeJsonLabel(r));
							if(!nascondiLabel) {
								bar.put(CostantiGrafici.DATA_LABEL_KEY, escapeJsonLabel(r));
							} else {
								if(posizioniDaVisualizzare.contains(Integer.valueOf(z))) {
									bar.put(CostantiGrafici.DATA_LABEL_KEY, escapeJsonLabel(r));
								} else { 
									bar.put(CostantiGrafici.DATA_LABEL_KEY, "");
								}
							}
						}

						// calcolo il tooltip
						String toolText = StatsUtils.getToolText(search,sum);

						// tooltip arricchito da informazioni come servizio/azione/sa ecc..
						if(!entry.getParentMap().isEmpty()){
							toolText = StatsUtils.getToolTextConParent(search,r, entry.getParentMap(), sum);
						}

						// valore da visualizzare nel grafico
						String value = StatsUtils.getValue(search,sum);

						bar.put(key, value);
						bar.put(key+CostantiGrafici.TOOLTIP_SUFFIX, toolText);

						if(j == numeroCategorie-1)
							dati.add(bar);

					} else{

						if(j==0){
							if(sum!=null) {
								altriSumSerie1+=sum.longValue();
							}
							altriSumSerie1NumeroItem++;
						}
						else if(j==1){
							if(sum!=null) {
								altriSumSerie2+=sum.longValue();
							}
							altriSumSerie2NumeroItem++;
						}
						else if(j==2){
							if(sum!=null) {
								altriSumSerie3+=sum.longValue();
							}
							altriSumSerie3NumeroItem++;
						}
					}
				}


			}
			if(iterazione>slice){
				ObjectNode bar = jsonUtils.newObjectNode();
				bar.put(CostantiGrafici.DATA_KEY, CostantiGrafici.ALTRI_LABEL);
				bar.put(CostantiGrafici.DATA_LABEL_KEY, CostantiGrafici.ALTRI_LABEL);

				for (int j = 0; j < numeroCategorie; j++) {
					// key che identifica la serie
					JsonNode keyNode = categorie.get(j).get(CostantiGrafici.KEY_KEY);
					String key = keyNode.asText();

					long v = -1;
					if(j==0){
						v = altriSumSerie1;
						if(altriSumSerie1NumeroItem>1 && (occupazioneBanda || tempoMedio)){
							v = v / altriSumSerie1NumeroItem;
						}
					}
					else if(j==1){
						v = altriSumSerie2;
						if(altriSumSerie2NumeroItem>1 && (occupazioneBanda || tempoMedio)){
							v = v / altriSumSerie2NumeroItem;
						}
					}
					else if(j==2){
						v = altriSumSerie3;
						if(altriSumSerie3NumeroItem>1 && (occupazioneBanda || tempoMedio)){
							v = v / altriSumSerie3NumeroItem;
						}
					}

					// Tooltip text
					String toolText = StatsUtils.getToolText(search,v); 
					String value = StatsUtils.getValue(search,v);

					bar.put(key, value);
					bar.put(key+CostantiGrafici.TOOLTIP_SUFFIX, toolText);
				}
				dati.add(bar);
			}

			if(maxLenghtLabel > CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_GRAFICO_DEFAULT_VALUE)
				grafico.put(CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_GRAFICO_KEY, CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_GRAFICO_DEFAULT_VALUE);

			// inserisco l'array dei dati calcolati nel JSON
			grafico.set(CostantiGrafici.DATI_KEY, dati);
		}
		else{
			// reset categorie
			categorie.removeAll();
			ObjectNode categoria = jsonUtils.newObjectNode();
			categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.TOTALE_KEY);
			categoria.put(CostantiGrafici.LABEL_KEY , StatsUtils.getSubCaption(search,true));
			categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_TOTALE);
			categorie.add(categoria);
			
			grafico.put(CostantiGrafici.NO_DATA_KEY, CostantiGrafici.DATI_NON_PRESENTI);
		}
		return grafico;
	}

	/* ************** HEATMAP CHART **************** */

	public static ObjectNode getJsonHeatmapChartDistribuzione(List<ResDistribuzione> list, StatsSearchForm search, 
			String caption, String subCaption, String direzioneLabelParam, Integer slice, StatisticType tempo, boolean visualizzaTotaleNelleCelleGraficoHeatmap,
			Logger log) throws UtilsException{
		return _getJsonHeatmapChartDistribuzione(list, search, caption, subCaption, direzioneLabelParam, slice, null, tempo, visualizzaTotaleNelleCelleGraficoHeatmap, log);
	}

	private static String buildKeyJsonHeatmapChartDistribuzione(ResDistribuzione res) {
		StringBuilder sb = new StringBuilder(res.getRisultato());
		if(res.getParentMap()!=null && !res.getParentMap().isEmpty()) {
			int i = 0;
			for (Entry<String, String> entry : res.getParentMap().entrySet()) {
				sb.append("_");
				sb.append(i);
				sb.append(":");
				sb.append(entry.getValue());
				i++;
			}
		}
		return sb.toString();
	}
	private static ObjectNode _getJsonHeatmapChartDistribuzione(List<ResDistribuzione> list, StatsSearchForm search, 
			String caption, String subCaption, String direzioneLabelParam, Integer sliceParam, Integer numeroLabel, StatisticType tempo, boolean visualizzaTotaleNelleCelleGraficoHeatmap,
			Logger log
			) throws UtilsException{
		JSONUtils jsonUtils = JSONUtils.getInstance();
		
		ObjectNode grafico = jsonUtils.newObjectNode();

		grafico.put(CostantiGrafici.USA_COLORI_AUTOMATICI_KEY, false);
		grafico.put(CostantiGrafici.X_AXIS_GRID_LINES_KEY,true);
		grafico.put(CostantiGrafici.TITOLO_KEY, caption);
		grafico.put(CostantiGrafici.SOTTOTITOLO_KEY, subCaption);
		grafico.put(CostantiGrafici.X_AXIS_LABEL_DIREZIONE_KEY, getDirezioneLabel(direzioneLabelParam));
		grafico.put(CostantiGrafici.VISUALIZZA_VALUE_NELLA_CELLA_GRAFICO_HEATMAP, visualizzaTotaleNelleCelleGraficoHeatmap);
		try {
			grafico.put(CostantiGrafici.VISUALIZZA_VALORE_ZERO_NEL_GRAFICO_HEATMAP, PddMonitorProperties.getInstance(log).isStatisticheVisualizzaValoreZeroNelGraficoHeatmap());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		

		SimpleDateFormat sdf = null;
		SimpleDateFormat sdf_last = null;
		if(tempo!=null) {
			sdf_last = new SimpleDateFormat(CostantiGrafici.PATTERN_HH, ApplicationBean.getInstance().getLocale());
			if (StatisticType.ORARIA.equals(tempo)) {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY_HH, ApplicationBean.getInstance().getLocale());
			} else {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY, ApplicationBean.getInstance().getLocale());
			}
		}

		ArrayNode categorie = jsonUtils.newArrayNode();

		boolean showSeries = false;
		if(categorie.isEmpty()){
			ObjectNode categoria = jsonUtils.newObjectNode();
			categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.TOTALE_KEY);
			categoria.put(CostantiGrafici.LABEL_KEY , StatsUtils.getSubCaption(search,true));
			categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_TOTALE);
			categorie.add(categoria);
		}

		// Inserisco le catergorie del grafico
		grafico.set(CostantiGrafici.CATEGORIE_KEY, categorie);

		grafico.put(CostantiGrafici.MOSTRA_LEGENDA_KEY, showSeries);

		String yAxisName = StatsUtils.getSubCaption(search,true);

		grafico.put(CostantiGrafici.Y_AXIS_LABEL_KEY, yAxisName);

		Number valoreMinimo = 0;
		Number valoreMassimo = 0;

		ArrayNode dati = jsonUtils.newArrayNode();
		// colleziono i dati
		if(list!=null  && !list.isEmpty()){
//			int iterazione = 0;
//			long altri_sum_serie1=0;

			int slice = sliceParam;
			int maxLenghtLabel = 0;
			
			// 1. Valorizzare tutti dati mancanti, genero eventuali 0 cosi tutti i calcoli successivi si faranno su un numero di elementi dell'asse y uguale 
			list = generaElementiMancanti(list, log);

			// 2. Ordinare i risultati per categoria con somma valori piu' alti 
			Map<String, List<ResDistribuzione>> elementiPerCategoria = new HashMap<>();
			Map<String, Number> totaliPerCategoria = new HashMap<>();

			for (ResDistribuzione res : list) {
				String key = buildKeyJsonHeatmapChartDistribuzione(res);
				List<ResDistribuzione> remove = elementiPerCategoria.remove(key);

				if(remove == null) {
					remove = new ArrayList<>();
				} 

				remove.add(res);
				
				/**if(res instanceof ResDistribuzione3D) {
					System.out.println("KEY ["+key+"] sizeList:"+remove.size()+" value["+res.getSomma()+"] data["+org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(((ResDistribuzione3D)res).getData())+"] dataFormattata["+((ResDistribuzione3D)res).getDataFormattata()+"]");
				}
				else {
					System.out.println("KEY ["+key+"] sizeList:"+remove.size()+" value["+res.getSomma()+"] data["+org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(((ResDistribuzione3DCustom)res).getDatoCustom())+"]");
				}*/
				
				elementiPerCategoria.put(key, remove);

				totaliPerCategoria.put(key, StatsUtils.sum(search, totaliPerCategoria.getOrDefault(key, 0) , res.getSomma()));
			}
			
			// ordinamento delle liste per data cosi che l'eventuale creazione dell'elemento 'altri' risulti corretto
			for (List<ResDistribuzione> lista : elementiPerCategoria.values()) {
				ordinaElementiAsseYPerData(search, lista);
			}
			
			
			List<Map.Entry<String, Number>> totaliPerCategoriaEntryList = new ArrayList<>(totaliPerCategoria.entrySet());

			// 2a. eseguo ordinamento chiavi
			ordinaElementiAsseXPerSommaDecrescente(search, totaliPerCategoriaEntryList);
			
			// 3. sostituisco la lista con quella ordinata per chiave
			list = valorizzaListaRisultatiConElementiOrdinatiPerSommaDecrescente(elementiPerCategoria, totaliPerCategoriaEntryList);
						
			// 4. a questo punto ho le categorie ordinate per somma decrescente, controllo se il numero delle chiavi e' superiore allo slice
			if(totaliPerCategoriaEntryList.size() > slice) {
				// ora devo creare un elenco dei risultati contenente la categoria altri 
				
				// prendo tutte le chiavi ordinate
		        List<String> sortedKeys = new ArrayList<>();
		        for (Map.Entry<String, Number> entry : totaliPerCategoriaEntryList) {
		            sortedKeys.add(entry.getKey());
		        }
				
		        // estraggo quelle da accorpare come 'altri'
		        List<String> categorieDaAccorpareComeAltri = sortedKeys.subList(slice, sortedKeys.size());
		        
		        // aggiorno contenuto list
		        list = valorizzaListaRisultatiConElementiOrdinatiPerSommaDecrescenteConElementoAltri(elementiPerCategoria, totaliPerCategoriaEntryList, categorieDaAccorpareComeAltri, search, log);
			}

			// una volta che ho la lista degli elementi da visualizzare gia' calcolata, comprensiva dell'eventuale elemento 'altri' si tratta solo di creare i rettangoli json
			for (int z = 0 ; z <list.size() ; z++) {
				// le  entries sono tutte di tipo 3D
				ResDistribuzione entry = list.get(z);
				ObjectNode rectangle = jsonUtils.newObjectNode();

				Number sum = entry.getSomma();

				String r = entry.getRisultato();
				String dateS = null;

				if(entry instanceof ResDistribuzione3D) {
					Date rDate =  ((ResDistribuzione3D)entry).getData();
					Calendar c = Calendar.getInstance();
					c.setTime(rDate);
	
					StringBuilder sb = new StringBuilder();
					if (StatisticType.ORARIA.equals(tempo)) {
						sdf_last.applyPattern(CostantiGrafici.PATTERN_HH);
						c.add(Calendar.HOUR, 1);
						sb.append(sdf.format(rDate) + "-").append( sdf_last.format(c.getTime()));
					} else if (StatisticType.SETTIMANALE.equals(tempo)) {
						// settimanale
						sdf.applyPattern(CostantiGrafici.PATTERN_DD_MM_YY);
						sdf_last.applyPattern(CostantiGrafici.PATTERN_DD_MM_YY);
	
						c.add(Calendar.WEEK_OF_MONTH, 1);
						c.add(Calendar.DAY_OF_WEEK, -1);
	
						sb.append(sdf.format(rDate) + "-").append( sdf_last.format(c.getTime()));
	
					} else if (StatisticType.MENSILE.equals(tempo)) {
						// mensile
						sdf.applyPattern(CostantiGrafici.PATTERN_MMM_YY);
						sb.append(sdf.format(rDate));
	
					} else {
						// giornaliero
						sb.append(sdf.format(rDate));
					}
					dateS = sb.toString();
				}
				else if(entry instanceof ResDistribuzione3DCustom) {
					dateS = ((ResDistribuzione3DCustom)entry).getDatoCustom();
				}

				// Iterazione 1 memorizzo la label della barra
				if(r.length() > maxLenghtLabel)
					maxLenghtLabel = r.length();

				// informazini asse X
				String key = buildKeyJsonHeatmapChartDistribuzione(entry);
				rectangle.put(CostantiGrafici.X_KEY, escapeJsonLabel(key));
				rectangle.put(CostantiGrafici.X_LABEL_KEY, escapeJsonLabel(r));

				// informazini asse Y
				rectangle.put(CostantiGrafici.Y_KEY, escapeJsonLabel(dateS));
				rectangle.put(CostantiGrafici.Y_LABEL_KEY, escapeJsonLabel(dateS));

				// calcolo il tooltip
				String toolText = StatsUtils.getToolText(search,sum);

				// tooltip arricchito da informazioni come servizio/azione/sa ecc..
				if(!entry.getParentMap().isEmpty()){
					toolText = StatsUtils.getToolTextConParent(search,r, entry.getParentMap(), sum);
				}
				// tooltip categoria altri
				if(r.equals(CostantiGrafici.ALTRI_LABEL)) {
					 toolText = StatsUtils.getToolTextCategoriaAltri(search,sum);
				}

				// valore da visualizzare nel grafico
				String value = StatsUtils.getValue(search,sum);

				rectangle.put(CostantiGrafici.TOTALE_KEY, value);
				rectangle.put(CostantiGrafici.TOTALE_KEY+CostantiGrafici.TOOLTIP_SUFFIX, toolText);
				rectangle.put(CostantiGrafici.TOTALE_KEY+CostantiGrafici.LABEL_SUFFIX, StatsUtils.getToolText(search,sum));

				dati.add(rectangle);

				// calcolo valore massimo
				valoreMassimo = StatsUtils.getMax(search, sum, valoreMassimo);
			}

			if(maxLenghtLabel > CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_GRAFICO_DEFAULT_VALUE)
				grafico.put(CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_GRAFICO_KEY, CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_GRAFICO_DEFAULT_VALUE);

			// inserisco l'array dei dati calcolati nel JSON
			grafico.set(CostantiGrafici.DATI_KEY, dati);
		}
		else{
			// reset categorie
			categorie.removeAll();
			ObjectNode categoria = jsonUtils.newObjectNode();
			categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.TOTALE_KEY);
			categoria.put(CostantiGrafici.LABEL_KEY , StatsUtils.getSubCaption(search,true));
			categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_TOTALE);
			categorie.add(categoria);

			grafico.put(CostantiGrafici.NO_DATA_KEY, CostantiGrafici.DATI_NON_PRESENTI);
		}

		// imposto scala valori 
		ObjectNode scalaValori = jsonUtils.newObjectNode();

		// MIN
		ObjectNode min = jsonUtils.newObjectNode();
		min.put(CostantiGrafici.COLORE_KEY, Colors.CSS_COLOR_WHITE);
		min.put(CostantiGrafici.VALORE_KEY, ""+valoreMinimo);
		scalaValori.set(CostantiGrafici.MIN_KEY, min);

		//MAX
		ObjectNode max = jsonUtils.newObjectNode();
		max.put(CostantiGrafici.COLORE_KEY, Colors.CSS_COLOR_TOTALE);
		max.put(CostantiGrafici.VALORE_KEY, StatsUtils.getValue(search,valoreMassimo));
		scalaValori.set(CostantiGrafici.MAX_KEY, max);

		grafico.set(CostantiGrafici.SCALA_VALORI_KEY, scalaValori);
		
		// Label della legenda in funzione dei valori min e max calcolati
		ArrayNode labelsLegenda = jsonUtils.newArrayNode();
		
		// visualizzazione di 5 label
		// 0, max/4 , max/2, 3 max/4, max 
		
		// MIN LAbel
		ObjectNode minLabel = jsonUtils.newObjectNode();
		minLabel.put(CostantiGrafici.VALORE_KEY, StatsUtils.getValue(search,valoreMinimo));
		minLabel.put(CostantiGrafici.LABEL_KEY, StatsUtils.getToolText(search,valoreMinimo));
		labelsLegenda.add(minLabel);
		
		// 1/4 max
		Number unQuarto = StatsUtils.avg(search, valoreMassimo, 4);
		ObjectNode unQuartoLabel = jsonUtils.newObjectNode();
		unQuartoLabel.put(CostantiGrafici.VALORE_KEY, StatsUtils.getValue(search,unQuarto));
		unQuartoLabel.put(CostantiGrafici.LABEL_KEY, StatsUtils.getToolText(search,unQuarto));
		labelsLegenda.add(unQuartoLabel);
		
		// 1/2 max
		Number unMezzo = StatsUtils.avg(search, valoreMassimo, 2);
		ObjectNode unMezzoLabel = jsonUtils.newObjectNode();
		unMezzoLabel.put(CostantiGrafici.VALORE_KEY, StatsUtils.getValue(search,unMezzo));
		unMezzoLabel.put(CostantiGrafici.LABEL_KEY, StatsUtils.getToolText(search,unMezzo));
		labelsLegenda.add(unMezzoLabel);
				
		// 3/4 max ((maxLegend / 2 + maxLegend) / 2)
		Number treQuarti = StatsUtils.avg(search, StatsUtils.sum(search, valoreMassimo, unMezzo), 2);
		ObjectNode treQuartiLabel = jsonUtils.newObjectNode();
		treQuartiLabel.put(CostantiGrafici.VALORE_KEY, StatsUtils.getValue(search,treQuarti));
		treQuartiLabel.put(CostantiGrafici.LABEL_KEY, StatsUtils.getToolText(search,treQuarti));
		labelsLegenda.add(treQuartiLabel);
				
		// MAX LAbel
		ObjectNode maxLabel = jsonUtils.newObjectNode();
		maxLabel.put(CostantiGrafici.VALORE_KEY, StatsUtils.getValue(search,valoreMassimo));
		maxLabel.put(CostantiGrafici.LABEL_KEY, StatsUtils.getToolText(search,valoreMassimo));
		labelsLegenda.add(maxLabel);

		grafico.set(CostantiGrafici.LABEL_LEGENDA_GRAFICO_HEATMAP, labelsLegenda);

		return grafico;
	}

	public static List<ResDistribuzione> generaElementiMancanti(List<ResDistribuzione> origList, Logger log){
		List<ResDistribuzione> lNull = null;
		if(origList == null) 
			return lNull;

		List<ResDistribuzione> destList = new ArrayList<>();

		// Trova i valori minimi e massimi di x, y nei risultati esistenti

		Set<String> existingXValues = new HashSet<>();
		Map<String,String> existingXValuesOriginalKey = new HashMap<>();

		Set<Date> existingYDateValues = new HashSet<>();
		Set<String> existingYCustomValues = new HashSet<>();
		boolean customValues = false;

		Map<String,TreeMap<String, String>> mapParentMaps = new HashMap<>();
		
		for (ResDistribuzione res : origList) {
			ResDistribuzione resDistribuzione = res;
			// salvo il dato nella lista destinazione
			destList.add(resDistribuzione);

			// colleziono le X esistenti
			String key = buildKeyJsonHeatmapChartDistribuzione(resDistribuzione);
			existingXValues.add(key);
			existingXValuesOriginalKey.put(key, resDistribuzione.getRisultato());
			
			// colleziono le parentmap
			mapParentMaps.put(key, resDistribuzione.getParentMap());

			// colleziono le Y esistenti
			if(resDistribuzione instanceof ResDistribuzione3D) {
				existingYDateValues.add(((ResDistribuzione3D)resDistribuzione).getData());
			}
			else if(resDistribuzione instanceof ResDistribuzione3DCustom) {
				customValues = true;
				existingYCustomValues.add(((ResDistribuzione3DCustom)resDistribuzione).getDatoCustom());
			}
		}

		// Genera e aggiungi le triple mancanti
		List<ResDistribuzione> missingTriples = new ArrayList<>();
		for (String x : existingXValues) {
			if(customValues) {
				for(String y : existingYCustomValues) {
					// Aggiungi una tripla solo se non esiste già una tripla con la stessa x e y
					if (!containsTriple(destList, x, y)) {
						String originalKey = existingXValuesOriginalKey.get(x);
						ResDistribuzione3DCustom resDistribuzione3D = new ResDistribuzione3DCustom(originalKey, y, 0);
						// copio informazioni parentMap per costruzione tooltip
						resDistribuzione3D.getParentMap().putAll(mapParentMaps.get(x));
						missingTriples.add(resDistribuzione3D);
					}
				}
			}
			else {
				for(Date y : existingYDateValues) {
					// Aggiungi una tripla solo se non esiste già una tripla con la stessa x e y
					if (!containsTriple(destList, x, y)) {
						String originalKey = existingXValuesOriginalKey.get(x);
						ResDistribuzione3D resDistribuzione3D = new ResDistribuzione3D(originalKey, y, 0);
						// copio informazioni parentMap per costruzione tooltip
						resDistribuzione3D.getParentMap().putAll(mapParentMaps.get(x));
						missingTriples.add(resDistribuzione3D);
					}
				}
			}
		}

		// aggiungo tutti i risultati mancanti
		destList.addAll(missingTriples);

		long existingYValues = 0;
		if(customValues) {
			existingYValues = existingYCustomValues.size();
		}
		else {
			existingYValues = existingYDateValues.size();
		}
		long xYsize = (existingXValues.size()) * (existingYValues);
		if(destList.size()!=xYsize) {
			String msg = "generaElementiMancanti destListSize:"+destList.size()+" xYsize:"+xYsize+" existingXValues:"+existingXValues+" existingYValues:"+existingYValues+"";
			log.error(msg);
		}
		
		return destList;
	}

	private static boolean containsTriple(List<ResDistribuzione> valuesList, String x, Date y) {
		for (ResDistribuzione res : valuesList) {
			ResDistribuzione3D resDistribuzione3D = (ResDistribuzione3D) res;
			String key = buildKeyJsonHeatmapChartDistribuzione(resDistribuzione3D);
			if (key.equals(x) && resDistribuzione3D.getData().equals(y)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean containsTriple(List<ResDistribuzione> valuesList, String x, String y) {
		for (ResDistribuzione res : valuesList) {
			ResDistribuzione3DCustom resDistribuzione3D = (ResDistribuzione3DCustom) res;
			String key = buildKeyJsonHeatmapChartDistribuzione(resDistribuzione3D);
			if (key.equals(x) && resDistribuzione3D.getDatoCustom().equals(y)) {
				return true;
			}
		}
		return false;
	}

	private static List<ResDistribuzione> valorizzaListaRisultatiConElementiOrdinatiPerSommaDecrescente(Map<String, List<ResDistribuzione>> elementiPerCategoria,
			List<Map.Entry<String, Number>> entryList) {
		// colleziona i valori ordinati per chiavi con somma decrescente
		List<ResDistribuzione> destList = new ArrayList<>();
		for (Map.Entry<String, Number> entry : entryList) {
			destList.addAll(elementiPerCategoria.get(entry.getKey()));
		}

		return destList;
	}
	
	private static List<ResDistribuzione> valorizzaListaRisultatiConElementiOrdinatiPerSommaDecrescenteConElementoAltri(
			Map<String, List<ResDistribuzione>> elementiPerCategoria,
			List<Map.Entry<String, Number>> entryList, List<String> categorieDaAccorpareComeAltri, StatsSearchForm search,
			Logger log) {
		// colleziona i valori ordinati per chiavi con somma decrescente
		List<ResDistribuzione> destList = new ArrayList<>();
		for (Map.Entry<String, Number> entry : entryList) {
			// inserisco solo quelli da non accorpare come altri
			if(!categorieDaAccorpareComeAltri.contains(entry.getKey())) {
				destList.addAll(elementiPerCategoria.get(entry.getKey()));
			}
		}
		
		// calcolo categoria altri
		List<ResDistribuzione> colonnaAltri = new ArrayList<>();
		boolean occupazioneBanda = false;
		boolean tempoMedio = false;
		TipoVisualizzazione tipoVisualizzazione = search.getTipoVisualizzazione();
		if(tipoVisualizzazione.equals(TipoVisualizzazione.DIMENSIONE_TRANSAZIONI)) {
			occupazioneBanda = true;
		}
		else if(tipoVisualizzazione.equals(TipoVisualizzazione.TEMPO_MEDIO_RISPOSTA)) {
			tempoMedio = true;
		}
		
		for (String categoria : categorieDaAccorpareComeAltri) {
			List<ResDistribuzione> categoriaDaAccorpare = elementiPerCategoria.get(categoria);
			
			/**log.error("---- Categoria '"+categoria+"' possiede una dimensione '"+categoriaDaAccorpare.size()+"' --- attesa '"+colonnaAltri.size()+"'");*/
			
			if(!colonnaAltri.isEmpty() &&
					categoriaDaAccorpare.size()!=colonnaAltri.size()) {
				log.error("Categoria '"+categoria+"' possiede una dimensione '"+categoriaDaAccorpare.size()+"' diversa da quella attesa '"+colonnaAltri.size()+"'");
			}
			
			// prima iterazione aggiungo direttamente tutti valori modificando la label in altri
			if(colonnaAltri.isEmpty()) {
				for (ResDistribuzione resDistribuzione : categoriaDaAccorpare) {
					
					/**log.error("---- Categoria '"+categoria+"' AGGIUNTA PER ALTRI!");*/
					
					resDistribuzione.setRisultato(CostantiGrafici.ALTRI_LABEL);
					// cancellazione della mappa parent per evitare visualizzare informazioni sul dettaglio della categoria
					if(resDistribuzione.getParentMap() != null) {
						resDistribuzione.getParentMap().clear();
					}
					colonnaAltri.add(resDistribuzione);
				}
			} else {
				// a questo punto faccio il ciclo per posizione perche' le colonne da unificare hanno la stessa data nella stessa posizione.
				for (int i = 0; i < colonnaAltri.size(); i++) {
					ResDistribuzione resDistribuzioneColonnaAltri = colonnaAltri.get(i);
					ResDistribuzione resDistribuzioneDaAccorpare = categoriaDaAccorpare.get(i);
					
					// per occupazione banda e latenza si deve fare la media, in questa fase calcolo la somma che divido per il numero di categorie che accorpo
					resDistribuzioneColonnaAltri.setSomma(StatsUtils.sum(search, resDistribuzioneDaAccorpare.getSomma(), resDistribuzioneColonnaAltri.getSomma()));
					
				}
			}
		}
		
		// in caso di latenza o occupazione band c'e' da fare la media di valori
		if(occupazioneBanda || tempoMedio) {
			for (int i = 0; i < colonnaAltri.size(); i++) {
				ResDistribuzione resDistribuzioneColonnaAltri = colonnaAltri.get(i);
				resDistribuzioneColonnaAltri.setSomma(StatsUtils.avg(search, resDistribuzioneColonnaAltri.getSomma(), categorieDaAccorpareComeAltri.size()));
			}
		}
		
		// aggiungo colonna altri
		destList.addAll(colonnaAltri);

		return destList;
	}


	private static void ordinaElementiAsseXPerSommaDecrescente(StatsSearchForm search, List<Map.Entry<String, Number>> entryList) {
		// Ordina la lista di voci in base ai valori decrescenti
		Collections.sort(entryList, new Comparator<Map.Entry<String, Number>>() {
			@Override
			public int compare(Map.Entry<String, Number> entry1, Map.Entry<String, Number> entry2) {
				TipoVisualizzazione tipoVisualizzazione = null;
				if(search!=null) {
					tipoVisualizzazione = search.getTipoVisualizzazione();
				}
				
				if(tipoVisualizzazione!=null) {

					switch (tipoVisualizzazione) {
			
					case NUMERO_TRANSAZIONI:
					case TEMPO_MEDIO_RISPOSTA:
						return Long.compare(entry2.getValue().longValue(), entry1.getValue().longValue());
					case DIMENSIONE_TRANSAZIONI:
					default:
						return Double.compare(entry2.getValue().doubleValue(), entry1.getValue().doubleValue());
					}
				}
				
				// Ordina in ordine decrescente in base ai valori
				return Long.compare(entry2.getValue().longValue(), entry1.getValue().longValue());
			}
		});
	}
	
	private static void ordinaElementiAsseYPerData(StatsSearchForm search, List<ResDistribuzione> list) {
		// Ordina la lista di voci in base ai valori decrescenti
		Collections.sort(list, new Comparator<ResDistribuzione>() {
			@Override
			public int compare(ResDistribuzione entry1, ResDistribuzione entry2) {
				if(entry1 instanceof ResDistribuzione3D) {
					Date date1 = ((ResDistribuzione3D)entry1).getData();
					Date date2 = ((ResDistribuzione3D)entry2).getData();
					
					 // Ordina in ordine crescente
			        return date1.compareTo(date2);
				}
				else {
					String date1 = ((ResDistribuzione3DCustom)entry1).getDatoCustom();
					String date2 = ((ResDistribuzione3DCustom)entry2).getDatoCustom();
					
					 // Ordina in ordine crescente
			        return date1.compareTo(date2);
				}
			}
		});
	}

	public static ObjectNode getJsonAndamentoTemporale(List<Res> list, StatsSearchForm search, String caption, String subCaption,StatisticType tempo,String direzioneLabelParam, Integer numeroLabel) throws UtilsException{
		
		JSONUtils jsonUtils = JSONUtils.getInstance();
		
		ObjectNode grafico = jsonUtils.newObjectNode();

		SimpleDateFormat sdf;
		SimpleDateFormat sdfLast = new SimpleDateFormat(CostantiGrafici.PATTERN_HH, ApplicationBean.getInstance().getLocale());
		if (StatisticType.ORARIA.equals(tempo)) {
			sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY_HH, ApplicationBean.getInstance().getLocale());
		} else {
			sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY, ApplicationBean.getInstance().getLocale());
		}

		grafico.put(CostantiGrafici.USA_COLORI_AUTOMATICI_KEY, false);
		grafico.put(CostantiGrafici.X_AXIS_GRID_LINES_KEY,true);
		grafico.put(CostantiGrafici.TITOLO_KEY, caption);
		grafico.put(CostantiGrafici.SOTTOTITOLO_KEY, subCaption);
		grafico.put(CostantiGrafici.X_AXIS_LABEL_DIREZIONE_KEY, getDirezioneLabel(direzioneLabelParam));		
		ArrayNode categorie = jsonUtils.newArrayNode();

		// calcolo series
		boolean occupazioneBanda = false;
		boolean tempoMedio = false;
		boolean distribuzionePerEsiti = false;
		if(search.isAndamentoTemporalePerEsiti()){
			distribuzionePerEsiti = true;
		}
		else{
			TipoVisualizzazione tipoVisualizzazione = search.getTipoVisualizzazione();
			if(tipoVisualizzazione.equals(TipoVisualizzazione.DIMENSIONE_TRANSAZIONI)) {
				occupazioneBanda = true;
			}
			else if(tipoVisualizzazione.equals(TipoVisualizzazione.TEMPO_MEDIO_RISPOSTA)) {
				tempoMedio = true;
			}
		}

		int numeroCategorie = 1;
		boolean showSeries = false;
		if (list != null && !list.isEmpty()) {
			if(distribuzionePerEsiti){
				numeroCategorie = 3;
				showSeries = true;

				ObjectNode categoria = jsonUtils.newObjectNode();
				categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.OK_KEY);
				categoria.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.OK_LABEL);
				categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_OK);
				categorie.add(categoria);

				ObjectNode categoria2 = jsonUtils.newObjectNode();
				categoria2.put(CostantiGrafici.KEY_KEY , CostantiGrafici.FAULT_KEY);
				categoria2.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.FAULT_LABEL);
				categoria2.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_FAULT_APPLICATIVO);
				categorie.add(categoria2);

				ObjectNode categoria3 = jsonUtils.newObjectNode();
				categoria3.put(CostantiGrafici.KEY_KEY , CostantiGrafici.ERRORE_KEY);
				categoria3.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.ERRORE_LABEL);
				categoria3.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_ERROR);
				categorie.add(categoria3);

			}
			else if(occupazioneBanda) {
				numeroCategorie = search.getTipiBandaImpostati().size();
				showSeries = numeroCategorie > 1;
				for (int i = 0; i < numeroCategorie; i++) {
					String[] strings = search.getTipiBanda();
					if(strings != null && strings.length == numeroCategorie){
						String tipoLat = strings[i];
						if(tipoLat != null && showSeries){
							if(tipoLat.equals("0")){
								if(showSeries){
									ObjectNode categoria = jsonUtils.newObjectNode();
									categoria.put(CostantiGrafici.KEY_KEY , TipoBanda.COMPLESSIVA.getValue().toLowerCase().replace(" ", "_"));
									categoria.put(CostantiGrafici.LABEL_KEY , TipoBanda.COMPLESSIVA.getValue());
									categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_BANDA_COMPLESSIVA);
									categorie.set(i,categoria);
								}
							}
							else if(tipoLat.equals("1")){
								if(showSeries){
									ObjectNode categoria = jsonUtils.newObjectNode();
									categoria.put(CostantiGrafici.KEY_KEY , TipoBanda.INTERNA.getValue().toLowerCase().replace(" ", "_"));
									categoria.put(CostantiGrafici.LABEL_KEY , TipoBanda.INTERNA.getValue());
									categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_BANDA_INTERNA);
									categorie.set(i,categoria);
								}
							}
							else if(tipoLat.equals("2")){
								if(showSeries){
									ObjectNode categoria = jsonUtils.newObjectNode();
									categoria.put(CostantiGrafici.KEY_KEY , TipoBanda.ESTERNA.getValue().toLowerCase().replace(" ", "_"));
									categoria.put(CostantiGrafici.LABEL_KEY , TipoBanda.ESTERNA.getValue());
									categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_BANDA_ESTERNA);
									categorie.set(i,categoria);
								}
							}
						}
					}
				}
			}
			else if(tempoMedio) {
				numeroCategorie = search.getTipiLatenzaImpostati().size();
				showSeries = numeroCategorie > 1;
				for (int i = 0; i < numeroCategorie; i++) {
					String[] strings = search.getTipiLatenza();
					if(strings != null && strings.length == numeroCategorie){
						String tipoLat = strings[i];
						if(tipoLat != null && showSeries){
							if(tipoLat.equals("0")){
								if(showSeries){
									ObjectNode categoria = jsonUtils.newObjectNode();
									categoria.put(CostantiGrafici.KEY_KEY , TipoLatenza.LATENZA_TOTALE.getValue().toLowerCase().replace(" ", "_"));
									categoria.put(CostantiGrafici.LABEL_KEY , TipoLatenza.LATENZA_TOTALE.getValue());
									categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_LATENZA_TOTALE);
									categorie.set(i,categoria);
								}
							}
							else if(tipoLat.equals("1")){
								if(showSeries){
									ObjectNode categoria = jsonUtils.newObjectNode();
									categoria.put(CostantiGrafici.KEY_KEY , TipoLatenza.LATENZA_SERVIZIO.getValue().toLowerCase().replace(" ", "_"));
									categoria.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.LABEL_TIPO_LATENZA_LATENZA_SERVIZIO);
									categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_LATENZA_SERVIZIO);
									categorie.set(i,categoria);
								}
							}
							else if(tipoLat.equals("2")){
								if(showSeries){
									ObjectNode categoria = jsonUtils.newObjectNode();
									categoria.put(CostantiGrafici.KEY_KEY , TipoLatenza.LATENZA_PORTA.getValue().toLowerCase().replace(" ", "_"));
									categoria.put(CostantiGrafici.LABEL_KEY , TipoLatenza.LATENZA_PORTA.getValue());
									categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_LATENZA_PORTA);
									categorie.set(i,categoria);
								}
							}
						}
					}
				}
			}
		}
		if(categorie.isEmpty()){
			ObjectNode categoria = jsonUtils.newObjectNode();
			categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.TOTALE_KEY);
			categoria.put(CostantiGrafici.LABEL_KEY , StatsUtils.getSubCaption(search,true));
			categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_TOTALE);
			categorie.add(categoria);
		}

		// Inserisco le catergorie del grafico
		grafico.set(CostantiGrafici.CATEGORIE_KEY, categorie);
		grafico.put(CostantiGrafici.MOSTRA_LEGENDA_KEY, showSeries);
		String yAxisName = StatsUtils.getSubCaption(search,true);
		grafico.put(CostantiGrafici.Y_AXIS_LABEL_KEY, yAxisName);

		ArrayNode dati = jsonUtils.newArrayNode();

		if (list != null && !list.isEmpty()) {
			// check estremi in modo da visualizzare sempre gli estremi ed eliminare il problema
			// dell'unico risultato con il "puntino" difficilmente visualizzabile
			list = StatsUtils.checkEstremi(list, search, tempo, sdf);
			
			List<Integer> posizioniDaVisualizzare = getListaIndiciLabelDaVisualizzare(list.size(), numeroLabel);
			boolean nascondiLabel = posizioniDaVisualizzare != null;

			for (int z = 0; z < list.size(); z++) {
				Res entry = list.get(z);

				ObjectNode point = jsonUtils.newObjectNode();

				Date r = entry.getRisultato();
				Calendar c = Calendar.getInstance();
				c.setTime(r);

				StringBuilder sb = new StringBuilder();
				if (StatisticType.ORARIA.equals(tempo)) {
					sdfLast.applyPattern(CostantiGrafici.PATTERN_HH);
					c.add(Calendar.HOUR, 1);
					sb.append(sdf.format(r) + "-").append( sdfLast.format(c.getTime()));
				} else if (StatisticType.SETTIMANALE.equals(tempo)) {
					// settimanale
					sdf.applyPattern(CostantiGrafici.PATTERN_DD_MM_YY);
					sdfLast.applyPattern(CostantiGrafici.PATTERN_DD_MM_YY);

					c.add(Calendar.WEEK_OF_MONTH, 1);
					c.add(Calendar.DAY_OF_WEEK, -1);

					sb.append(sdf.format(r) + "-").append( sdfLast.format(c.getTime()));

				} else if (StatisticType.MENSILE.equals(tempo)) {
					// mensile
					sdf.applyPattern(CostantiGrafici.PATTERN_MMM_YY);
					sb.append(sdf.format(r));

				} else {
					// giornaliero
					sb.append(sdf.format(r));
				}

				String label = sb.toString();
				point.put(CostantiGrafici.DATA_KEY, escapeJsonLabel(label));

				if(!nascondiLabel) {
					point.put(CostantiGrafici.DATA_LABEL_KEY, escapeJsonLabel(label));
				} else {
					if(posizioniDaVisualizzare.contains(Integer.valueOf(z))) {
						point.put(CostantiGrafici.DATA_LABEL_KEY, escapeJsonLabel(label));
					} else { 
						point.put(CostantiGrafici.DATA_LABEL_KEY, "");
					}
				}

				for (int j = 0; j < numeroCategorie; j++) {
					// key che identifica la serie
					JsonNode keyNode = categorie.get(j).get(CostantiGrafici.KEY_KEY);
					String key = keyNode.asText();
					Number sum = entry.getSomme().get(j);

					// calcolo il tooltip
					String toolText = StatsUtils.getToolText(search,sum);
					// valore da visualizzare nel grafico
					String value = StatsUtils.getValue(search,sum);

					point.put(key, value);
					point.put(key+CostantiGrafici.TOOLTIP_SUFFIX, toolText);
				}
				dati.add(point);
			}

			// inserisco l'array dei dati calcolati nel JSON
			grafico.set(CostantiGrafici.DATI_KEY, dati);
		} else {
			// reset categorie
			categorie.removeAll();
			ObjectNode categoria = jsonUtils.newObjectNode();
			categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.TOTALE_KEY);
			categoria.put(CostantiGrafici.LABEL_KEY , StatsUtils.getSubCaption(search,true));
			categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_TOTALE);
			/** NO categorie.add(categoria); */
			
			grafico.put(CostantiGrafici.NO_DATA_KEY, CostantiGrafici.DATI_NON_PRESENTI);
		}
		return grafico;
	}


	/*** STATISTICHE PERSONALIZZATE 
	 * @throws UtilsException ***/

	public static ObjectNode getJsonAndamentoTemporaleStatPersonalizzate(SimpleDateFormat sdf,
			SimpleDateFormat sdfLastHour, StatisticType tempo, Map<String, List<Res>> results, StatsSearchForm search, String caption, String subCaption, String direzioneLabelParam) throws UtilsException {

		JSONUtils jsonUtils = JSONUtils.getInstance();
		
		ObjectNode grafico = jsonUtils.newObjectNode();

		grafico.put(CostantiGrafici.TITOLO_KEY, caption);
		grafico.put(CostantiGrafici.SOTTOTITOLO_KEY, subCaption);
		grafico.put(CostantiGrafici.X_AXIS_LABEL_DIREZIONE_KEY, getDirezioneLabel(direzioneLabelParam));
		grafico.put(CostantiGrafici.USA_COLORI_AUTOMATICI_KEY, true);
		grafico.put(CostantiGrafici.X_AXIS_GRID_LINES_KEY,true);
		grafico.put(CostantiGrafici.COLONNE_LEGENDA_KEY, CostantiGrafici.COLONNE_LEGENDA_DEFAULT_VALUE);
		grafico.put(CostantiGrafici.LIMITE_COLONNE_LEGENDA_KEY, CostantiGrafici.COLONNE_LEGENDA_DEFAULT_VALUE);

		ArrayNode categorie = jsonUtils.newArrayNode();

		// Inserisco le categorie del grafico
		String yAxisName = StatsUtils.getSubCaption(search,true);


		grafico.put(CostantiGrafici.MOSTRA_LEGENDA_KEY, true);
		grafico.put(CostantiGrafici.Y_AXIS_LABEL_KEY, yAxisName);

		ArrayNode dati = jsonUtils.newArrayNode();

		if (results != null && results.size()>0) {

			Set<String> keys = results.keySet();
			Iterator<String> it = keys.iterator();
			int i = 0;
			while (it.hasNext()) {
				String key = it.next();
				String catKey = "cat_"+i;

				List<Res> list = results.get(key);
				
				ObjectNode categoria = jsonUtils.newObjectNode();
				categoria.put(CostantiGrafici.KEY_KEY , catKey);
				categoria.put(CostantiGrafici.LABEL_KEY , escapeJsonLabel(key));
				categorie.add(categoria);

				// check estremi in modo da visualizzare sempre gli estremi ed eliminare il problema
				// dell'unico risultato con il "puntino" difficilmente visualizzabile
				list = StatsUtils.checkEstremi(list,search, tempo, sdf);

				// imposto le category solo la prima volta
				if (i < 1) {
					for (Res entry : list) {
						ObjectNode point = jsonUtils.newObjectNode();
						Date r = entry.getRisultato();
						StringBuilder sb = new StringBuilder();
						if (StatisticType.ORARIA.equals(tempo)) {
							Calendar c = Calendar.getInstance();
							c.setTime(r);
							c.add(Calendar.HOUR, +1);
							sb.append(sdf.format(r) + "-"
									+ sdfLastHour.format(c.getTime()));
						} else {
							sb.append(sdf.format(r));
						}

						point.put(CostantiGrafici.DATA_KEY, sb.toString());
						point.put(CostantiGrafici.DATA_LABEL_KEY, sb.toString());
						dati.add(point);
					}
				}

				for (int j = 0; j < list.size(); j++) {
					Res entry = list.get(j);
					ObjectNode point = (ObjectNode) dati.get(j);
					
					Number sum = entry.getSomma();
					String toolText = StatsUtils.getToolText(search,sum); 
					String value = StatsUtils.getValue(search,sum);
					
					point.put(catKey, value);
					point.put(catKey+CostantiGrafici.TOOLTIP_SUFFIX, toolText);
					
				}
				i++;
			}// fine ciclo dataset
			
			grafico.set(CostantiGrafici.DATI_KEY, dati);
		} else {
			// reset categorie
			categorie.removeAll();
			ObjectNode categoria = jsonUtils.newObjectNode();
			categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.TOTALE_KEY);
			categoria.put(CostantiGrafici.LABEL_KEY , StatsUtils.getSubCaption(search,true));
			categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_TOTALE);
			categorie.add(categoria);

			grafico.put(CostantiGrafici.NO_DATA_KEY, CostantiGrafici.DATI_NON_PRESENTI);
		}

		// Inserisco le catergorie del grafico
		grafico.set(CostantiGrafici.CATEGORIE_KEY, categorie);
		return grafico;
	}

	public static ObjectNode getJsonPieChartStatistichePersonalizzate( List<ResDistribuzione> list,  StatsSearchForm search, int slice, String caption, String subCaption) throws UtilsException {
		
		JSONUtils jsonUtils = JSONUtils.getInstance();
		
		ObjectNode grafico = jsonUtils.newObjectNode();
		
		grafico.put(CostantiGrafici.TITOLO_KEY, caption);
		grafico.put(CostantiGrafici.SOTTOTITOLO_KEY, subCaption);
		grafico.put(CostantiGrafici.USA_COLORI_AUTOMATICI_KEY, true);
		grafico.put(CostantiGrafici.X_AXIS_GRID_LINES_KEY,true);
		grafico.put(CostantiGrafici.ABILITA_CLICK_LEGENDA_KEY, false);
		grafico.put(CostantiGrafici.VALORE_REALE_TORTA_KEY,true);
		grafico.put(CostantiGrafici.COLONNE_LEGENDA_KEY, CostantiGrafici.COLONNE_LEGENDA_DEFAULT_VALUE);
		grafico.put(CostantiGrafici.LIMITE_COLONNE_LEGENDA_KEY, CostantiGrafici.COLONNE_LEGENDA_DEFAULT_VALUE);
		grafico.put(CostantiGrafici.VISUALIZZA_VALUE_NELLA_LABEL_LEGENDA, true);

		// calcolo series
		boolean occupazioneBanda = false;
		boolean tempoMedio = false;
		TipoVisualizzazione tipoVisualizzazione = search.getTipoVisualizzazione();
		if(tipoVisualizzazione.equals(TipoVisualizzazione.DIMENSIONE_TRANSAZIONI)) {
			occupazioneBanda = true;
		}
		else if(tipoVisualizzazione.equals(TipoVisualizzazione.TEMPO_MEDIO_RISPOSTA)) {
			tempoMedio = true;
		}

		ArrayNode dati = jsonUtils.newArrayNode();

		int maxLenghtLabel = 0;
		if(list!=null  && !list.isEmpty()){
			int i = 0;
			long altriSum=0;
			int altriSumNumeroItem=0;

			for (ResDistribuzione entry : list) {
				String r = entry.getRisultato();
				Number sum = entry.getSomma();

				if(++i<=slice) {
					if(r.length() > maxLenghtLabel)
						maxLenghtLabel = r.length();

					String toolText = StatsUtils.getToolText(search,sum); 
					ObjectNode spicchio = jsonUtils.newObjectNode();
					spicchio.put(CostantiGrafici.LABEL_KEY, escapeJsonLabel(r));
					spicchio.put(CostantiGrafici.TOOLTIP_KEY, toolText);
					if(sum instanceof Long v) {
						spicchio.put(CostantiGrafici.VALUE_KEY, v);
					}
					else if(sum instanceof Integer vi) {
						spicchio.put(CostantiGrafici.VALUE_KEY, vi);
					}
					else if(sum instanceof Float vf) {
						spicchio.put(CostantiGrafici.VALUE_KEY, vf);
					}
					else if(sum instanceof Double vd) {
						spicchio.put(CostantiGrafici.VALUE_KEY, vd);
					}

					dati.add(spicchio);
				} else{
					altriSum+=sum.longValue();
					altriSumNumeroItem++;
				}
			}

			if(i>slice){

				long v = altriSum;
				if(altriSumNumeroItem>1 && (occupazioneBanda || tempoMedio) &&
					(altriSumNumeroItem>0) 
				){
					v = v / altriSumNumeroItem;
				}

				String toolText = StatsUtils.getToolText(search,v); 

				ObjectNode spicchio = jsonUtils.newObjectNode();
				spicchio.put(CostantiGrafici.LABEL_KEY, CostantiGrafici.ALTRI_LABEL);
				spicchio.put(CostantiGrafici.TOOLTIP_KEY, toolText);
				spicchio.put(CostantiGrafici.VALUE_KEY, v);
				
				dati.add(spicchio);
			}
			
			if(maxLenghtLabel > CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_LEGENDA_DEFAULT_VALUE)
				grafico.put(CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_LEGENDA_KEY, CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_LEGENDA_DEFAULT_VALUE);
			
			grafico.set(CostantiGrafici.DATI_KEY, dati);
		}
		else{
			grafico.put(CostantiGrafici.NO_DATA_KEY, CostantiGrafici.DATI_NON_PRESENTI);
		}

		grafico.set(CostantiGrafici.DATI_KEY, dati);
		return grafico;	
	}

	public static ObjectNode getJsonBarChartStatistichePersonalizzate( List<ResDistribuzione> list,  StatsSearchForm search, String direzioneLabelParam, int slice, String caption, String subCaption) throws UtilsException {
		JSONUtils jsonUtils = JSONUtils.getInstance();
		
		ObjectNode grafico = jsonUtils.newObjectNode();

		grafico.put(CostantiGrafici.TITOLO_KEY, caption);
		grafico.put(CostantiGrafici.SOTTOTITOLO_KEY, subCaption);
		grafico.put(CostantiGrafici.X_AXIS_LABEL_DIREZIONE_KEY, getDirezioneLabel(direzioneLabelParam));
		grafico.put(CostantiGrafici.USA_COLORI_AUTOMATICI_KEY, false);
		grafico.put(CostantiGrafici.X_AXIS_GRID_LINES_KEY,true);
		
		ArrayNode categorie = jsonUtils.newArrayNode();

		ObjectNode categoria = jsonUtils.newObjectNode();
		categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.TOTALE_KEY);
		categoria.put(CostantiGrafici.LABEL_KEY , StatsUtils.getSubCaption(search,true));
		categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_TOTALE);
		categorie.add(categoria);

		// calcolo series
		boolean occupazioneBanda = false;
		boolean tempoMedio = false;
		int numeroCategorie = 1;

		String yAxisName = StatsUtils.getSubCaption(search,true);

		// Inserisco le catergorie del grafico
		grafico.set(CostantiGrafici.CATEGORIE_KEY, categorie);
		grafico.put(CostantiGrafici.MOSTRA_LEGENDA_KEY, false);
		grafico.put(CostantiGrafici.Y_AXIS_LABEL_KEY, yAxisName);

		ArrayNode dati = jsonUtils.newArrayNode();

		int maxLenghtLabel = 0;
		if(list!=null  && !list.isEmpty()){
			int iterazione = 0;
			long altriSumSerie1=0;
			int altriSumSerie1NumeroItem=0;

			if(slice != Integer.MAX_VALUE) // viene usato il maxValue per "disattivare" lo slice
				slice = slice * numeroCategorie;

			for (int z = 0 ; z <list.size() ; z++) {
				ResDistribuzione entry = list.get(z);
				ObjectNode bar = jsonUtils.newObjectNode();

				for (int j = 0; j < numeroCategorie; j++) {
					// key che identifica la serie
					JsonNode keyNode = categorie.get(j).get(CostantiGrafici.KEY_KEY);
					String key = keyNode.asText();
					Number sum =  entry.getSomma();

					if(++iterazione<=slice) {

						String r = entry.getRisultato();

						if(j==0){
							if(r.length() > maxLenghtLabel)
								maxLenghtLabel = r.length();

							bar.put(CostantiGrafici.DATA_KEY, escapeJsonLabel(r));
							bar.put(CostantiGrafici.DATA_LABEL_KEY, escapeJsonLabel(r));
						}

						// valore da visualizzare nel grafico
						String value = StatsUtils.getValue(search,sum);
						String toolText =StatsUtils.getToolText(search,sum); 

						bar.put(key, value);
						bar.put(key+CostantiGrafici.TOOLTIP_SUFFIX, toolText);

						if(j== numeroCategorie-1){
							dati.add(bar);
						}
					} else{
						if(j==0){
							altriSumSerie1+=sum.longValue();
							altriSumSerie1NumeroItem++;
						}
					}
				}

			}

			if(iterazione>slice){
				ObjectNode bar = jsonUtils.newObjectNode();
				bar.put(CostantiGrafici.DATA_KEY, CostantiGrafici.ALTRI_LABEL);
				bar.put(CostantiGrafici.DATA_LABEL_KEY, CostantiGrafici.ALTRI_LABEL);

				for (int j = 0; j < numeroCategorie; j++) {
					// key che identifica la serie
					JsonNode keyNode = categorie.get(j).get(CostantiGrafici.KEY_KEY);
					String key = keyNode.asText();

					long v = -1;
					if(j==0){
						v = altriSumSerie1;
						if(altriSumSerie1NumeroItem>1 && (occupazioneBanda || tempoMedio)){
							v = v / altriSumSerie1NumeroItem;
						}
					}

					// Tooltip text
					String toolText = StatsUtils.getToolText(search,v); 
					String value = StatsUtils.getValue(search,v);

					bar.put(key, value);
					bar.put(key+CostantiGrafici.TOOLTIP_SUFFIX, toolText);

				}
				dati.add(bar);
			}

			if(maxLenghtLabel > CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_GRAFICO_DEFAULT_VALUE)
				grafico.put(CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_GRAFICO_KEY, CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_GRAFICO_DEFAULT_VALUE);

			// inserisco l'array dei dati calcolati nel JSON
			grafico.set(CostantiGrafici.DATI_KEY, dati);
		} else{
			// reset categorie
			categorie.removeAll();
			categoria = jsonUtils.newObjectNode();
			categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.TOTALE_KEY);
			categoria.put(CostantiGrafici.LABEL_KEY , StatsUtils.getSubCaption(search,true));
			categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_TOTALE);
			categorie.add(categoria);
			grafico.put(CostantiGrafici.NO_DATA_KEY, CostantiGrafici.DATI_NON_PRESENTI);
		}

		return grafico;
	}
	
	public static int getDirezioneLabel(String direzione) {
		if(StringUtils.isNotEmpty(direzione)){
			if(CostantiGrafici.DIREZIONE_LABEL_ORIZZONTALE_LABEL.equals(direzione))
				return CostantiGrafici.DIREZIONE_LABEL_ORIZZONTALE;
			
			if(CostantiGrafici.DIREZIONE_LABEL_OBLIQUO_LABEL.equals(direzione))
				return CostantiGrafici.DIREZIONE_LABEL_OBLIQUO;
			
			if(CostantiGrafici.DIREZIONE_LABEL_VERTICALE_LABEL.equals(direzione))
				return CostantiGrafici.DIREZIONE_LABEL_VERTICALE;
		}
		
		return CostantiGrafici.DIREZIONE_LABEL_ORIZZONTALE;
	}
	
	public static String escapeJsonLabel(String label) {
		String escaped = StringEscapeUtils.escapeXml(label);
		
		escaped = escaped.replace("\\", "\\\\");
		
		return escaped;
	}
	
	public static List<Integer> getListaIndiciLabelDaVisualizzare(int size, Integer numeroLabel){
		List<Integer> posizioni = null;
		
		if(numeroLabel != null &&
			size > numeroLabel.intValue()) {
				
			if(numeroLabel < 2)
				numeroLabel = 2;
			
			posizioni = new ArrayList<>();
			// estremo di sinistra
			posizioni.add(Integer.valueOf(0));
			
			int denom = numeroLabel - 1;
			int numeroPosizioni = numeroLabel - 2;
			
			for (int j = 1; j <= numeroPosizioni; j++) {
				int pos = (j * size / denom);
				posizioni.add(Integer.valueOf(pos));
			}
			
			// estremo di dx
			posizioni.add(Integer.valueOf(size -1));
			
		} 
		
		return posizioni;
	}
}
