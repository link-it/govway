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
package org.openspcoop2.web.monitor.statistiche.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.statistiche.constants.Colors;
import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.web.monitor.core.datamodel.Res;
import org.openspcoop2.web.monitor.core.datamodel.ResBase;
import org.openspcoop2.web.monitor.core.datamodel.ResDistribuzione;
import org.openspcoop2.web.monitor.statistiche.bean.StatsSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiGrafici;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * JsonStatsUtils
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class JsonStatsUtils {

	/* ******* PIE CHART ********** */

	public static JSONObject getJsonPieChartDistribuzione(List<ResDistribuzione> list, StatsSearchForm search, String caption, String subCaption, Integer slice){
		JSONObject grafico = new JSONObject();
		
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

		JSONArray dati = new JSONArray();

		int maxLenghtLabel = 0;
		if(list!=null  && list.size()>0){
			int i = 0;
			long altri_sum=0;
			int altri_sum_numeroItem=0;

			for (ResDistribuzione entry : list) {
				String r = entry.getRisultato();
				Number sum = entry.getSomma();

				if(++i<=slice) {
					if(r.length() > maxLenghtLabel)
						maxLenghtLabel = r.length();
					
					String toolText = StatsUtils.getToolText(search,sum); 
					if(!entry.getParentMap().isEmpty())
						toolText = StatsUtils.getToolTextConParent(search, r ,entry.getParentMap(), sum);

					JSONObject spicchio = new JSONObject();
					spicchio.put(CostantiGrafici.LABEL_KEY, escapeJsonLabel(r));
					spicchio.put(CostantiGrafici.TOOLTIP_KEY, toolText);
					spicchio.put(CostantiGrafici.VALUE_KEY, sum);

					dati.add(spicchio);
				} else{
					altri_sum+=sum.longValue();
					altri_sum_numeroItem++;
				}
			}

			if(i>slice){

				long v = altri_sum;
				if(altri_sum_numeroItem>1 && (occupazioneBanda || tempoMedio)){
					v = v / altri_sum_numeroItem;
				}

				String toolText = StatsUtils.getToolText(search,v); 

				JSONObject spicchio = new JSONObject();
				spicchio.put(CostantiGrafici.LABEL_KEY, CostantiGrafici.ALTRI_LABEL);
				spicchio.put(CostantiGrafici.TOOLTIP_KEY, toolText);
				spicchio.put(CostantiGrafici.VALUE_KEY, v);

				dati.add(spicchio);
			}
			grafico.put(CostantiGrafici.DATI_KEY, dati);
			
			if(maxLenghtLabel > CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_LEGENDA_DEFAULT_VALUE)
				grafico.put(CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_LEGENDA_KEY, CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_LEGENDA_DEFAULT_VALUE);
		}
		else{
			grafico.put(CostantiGrafici.NO_DATA_KEY, CostantiGrafici.DATI_NON_PRESENTI);
		}
		

		return grafico;		

	}


	/* ************** BAR CHART **************** */

	public static JSONObject getJsonBarChartDistribuzione(List<ResDistribuzione> list, StatsSearchForm search, String caption, String subCaption, String direzioneLabelParam, Integer slice){
		List<ResBase> listI = new ArrayList<ResBase>();
		listI.addAll(list);
		return _getJsonBarChartDistribuzione(listI, search, caption, subCaption, direzioneLabelParam, slice,null);
	}
	public static JSONObject getJsonBarChartDistribuzione(List<Res> list, StatsSearchForm search, String caption, String subCaption, String direzioneLabelParam, Integer slice, StatisticType tempo){
		List<ResBase> listI = new ArrayList<ResBase>();
		listI.addAll(list);
		return _getJsonBarChartDistribuzione(listI, search, caption, subCaption, direzioneLabelParam, slice,tempo);
	}

	private static JSONObject _getJsonBarChartDistribuzione(List<ResBase> list, StatsSearchForm search, String caption, String subCaption, String direzioneLabelParam, Integer sliceParam, StatisticType tempo
			,		String ... series
			){
		JSONObject grafico = new JSONObject();

		grafico.put(CostantiGrafici.USA_COLORI_AUTOMATICI_KEY, false);
		grafico.put(CostantiGrafici.X_AXIS_GRID_LINES_KEY,true);
		grafico.put(CostantiGrafici.TITOLO_KEY, caption);
		grafico.put(CostantiGrafici.SOTTOTITOLO_KEY, subCaption);
		grafico.put(CostantiGrafici.X_AXIS_LABEL_DIREZIONE_KEY, getDirezioneLabel(direzioneLabelParam));

		SimpleDateFormat sdf = null;
		SimpleDateFormat sdf_last = null;
		if(tempo!=null) {
			sdf_last = new SimpleDateFormat(CostantiGrafici.PATTERN_HH, Locale.ITALIAN);
			if (StatisticType.ORARIA.equals(tempo)) {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY_HH, Locale.ITALIAN);
			} else {
				sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY, Locale.ITALIAN);
			}
		}

		JSONArray categorie = new JSONArray();

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
		//		String color = null;
		if(list!=null  && list.size()>0){
			if(series!=null && series.length>0){
				numeroCategorie = series.length;
				for (int i = 0 ; i < series.length ; i++) {
					String string = series[i];
					JSONObject categoria = new JSONObject();
					categoria.put(CostantiGrafici.KEY_KEY , string);
					categoria.put(CostantiGrafici.LABEL_KEY , string);
					categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_TOTALE);
					categorie.add(categoria);
				}

				showSeries = true;
				//				color = Colors.CODE_TOTALE; // TODO gestire i colori in questo caso. Comunque per ora è un caso non usato, il metodo con le series come parametro è commentato
			}
			else {
				if(distribuzionePerEsiti){
					numeroCategorie = 3;
					showSeries = true;

					JSONObject categoria = new JSONObject();
					categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.OK_KEY);
					categoria.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.OK_LABEL);
					categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_OK);
					categorie.add(categoria);

					JSONObject categoria2 = new JSONObject();
					categoria2.put(CostantiGrafici.KEY_KEY , CostantiGrafici.FAULT_KEY);
					categoria2.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.FAULT_LABEL);
					categoria2.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_FAULT_APPLICATIVO);
					categorie.add(categoria2);

					JSONObject categoria3 = new JSONObject();
					categoria3.put(CostantiGrafici.KEY_KEY , CostantiGrafici.ERRORE_KEY);
					categoria3.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.ERRORE_LABEL);
					categoria3.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_ERROR);
					categorie.add(categoria3);

				}
				else if(occupazioneBanda) {
					numeroCategorie = search.getTipiBandaImpostati().size();
					showSeries = numeroCategorie > 1 ? true : false;
					for (int i = 0; i < numeroCategorie; i++) {
						String[] strings = search.getTipiBanda();
						if(strings != null && strings.length == numeroCategorie){
							String tipoLat = strings[i];
							if(tipoLat != null){
								if(tipoLat.equals("0")){
									if(showSeries){
										JSONObject categoria = new JSONObject();
										categoria.put(CostantiGrafici.KEY_KEY , TipoBanda.COMPLESSIVA.getValue().toLowerCase().replace(" ", "_"));
										categoria.put(CostantiGrafici.LABEL_KEY , TipoBanda.COMPLESSIVA.getValue());
										categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_BANDA_COMPLESSIVA);
										categorie.add(i,categoria);
									}
								}
								else if(tipoLat.equals("1")){
									if(showSeries){
										JSONObject categoria = new JSONObject();
										categoria.put(CostantiGrafici.KEY_KEY , TipoBanda.INTERNA.getValue().toLowerCase().replace(" ", "_"));
										categoria.put(CostantiGrafici.LABEL_KEY , TipoBanda.INTERNA.getValue());
										categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_BANDA_INTERNA);
										categorie.add(i,categoria);
									}
								}
								else if(tipoLat.equals("2")){
									if(showSeries){
										JSONObject categoria = new JSONObject();
										categoria.put(CostantiGrafici.KEY_KEY , TipoBanda.ESTERNA.getValue().toLowerCase().replace(" ", "_"));
										categoria.put(CostantiGrafici.LABEL_KEY , TipoBanda.ESTERNA.getValue());
										categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_BANDA_ESTERNA);
										categorie.add(i,categoria);
									}
								}
							}
						}
					}
				}
				else if(tempoMedio) {
					numeroCategorie = search.getTipiLatenzaImpostati().size();
					showSeries = numeroCategorie > 1 ? true : false;
					for (int i = 0; i < numeroCategorie; i++) {
						String[] strings = search.getTipiLatenza();
						if(strings != null && strings.length == numeroCategorie){
							String tipoLat = strings[i];
							if(tipoLat != null){
								if(tipoLat.equals("0")){
									if(showSeries){
										JSONObject categoria = new JSONObject();
										categoria.put(CostantiGrafici.KEY_KEY , TipoLatenza.LATENZA_TOTALE.getValue().toLowerCase().replace(" ", "_"));
										categoria.put(CostantiGrafici.LABEL_KEY , TipoLatenza.LATENZA_TOTALE.getValue());
										categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_LATENZA_TOTALE);
										categorie.add(i,categoria);
									}
								}
								else if(tipoLat.equals("1")){
									if(showSeries){
										JSONObject categoria = new JSONObject();
										categoria.put(CostantiGrafici.KEY_KEY , TipoLatenza.LATENZA_SERVIZIO.getValue().toLowerCase().replace(" ", "_"));
										categoria.put(CostantiGrafici.LABEL_KEY , TipoLatenza.LATENZA_SERVIZIO.getValue());
										categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_LATENZA_SERVIZIO);
										categorie.add(i,categoria);
									}
								}
								else if(tipoLat.equals("2")){
									if(showSeries){
										JSONObject categoria = new JSONObject();
										categoria.put(CostantiGrafici.KEY_KEY , TipoLatenza.LATENZA_PORTA.getValue().toLowerCase().replace(" ", "_"));
										categoria.put(CostantiGrafici.LABEL_KEY , TipoLatenza.LATENZA_PORTA.getValue());
										categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_LATENZA_PORTA);
										categorie.add(i,categoria);
									}
								}
							}
						}
					}
				}
			}
		}
		if(categorie.isEmpty()){
			JSONObject categoria = new JSONObject();
			categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.TOTALE_KEY);
			categoria.put(CostantiGrafici.LABEL_KEY , StatsUtils.getSubCaption(search,true));
			categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_TOTALE);
			categorie.add(categoria);
		}

		// Inserisco le catergorie del grafico
		grafico.put(CostantiGrafici.CATEGORIE_KEY, categorie);

		grafico.put(CostantiGrafici.MOSTRA_LEGENDA_KEY, showSeries);

		String yAxisName = StatsUtils.getSubCaption(search,true);

		grafico.put(CostantiGrafici.Y_AXIS_LABEL_KEY, yAxisName);

		JSONArray dati = new JSONArray();
		// colleziono i dati
		if(list!=null  && list.size()>0){
			int iterazione = 0;
			long altri_sum_serie1=0;
			int altri_sum_serie1_numeroItem=0;
			long altri_sum_serie2=0;
			int altri_sum_serie2_numeroItem=0;
			long altri_sum_serie3=0;
			int altri_sum_serie3_numeroItem=0;



			int slice = sliceParam;
			if(slice != Integer.MAX_VALUE) // viene usato il maxValue per "disattivare" lo slice
				slice = slice * numeroCategorie;
			
			int maxLenghtLabel = 0;

			for (int z = 0 ; z <list.size() ; z++) {
				ResBase entry = list.get(z);
				JSONObject bar = new JSONObject();

				for (int j = 0; j < numeroCategorie; j++) {
					// key che identifica la serie
					String key = categorie.getJSONObject(j).getString(CostantiGrafici.KEY_KEY);

					Number sum = null;
					if(entry instanceof ResDistribuzione){
						sum = entry.getSomma();
					}
					else if(entry instanceof Res){
						sum = entry.getSomme().get(j);
					}

					if(++iterazione<=slice) {

						String r = null;
						if(entry instanceof ResDistribuzione){
							r = ((ResDistribuzione)entry).getRisultato();
						}
						else if(entry instanceof Res){
							
							Date rDate = ((Res)entry).getRisultato();
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
							altri_sum_serie1+=sum.longValue();
							altri_sum_serie1_numeroItem++;
						}
						else if(j==1){
							altri_sum_serie2+=sum.longValue();
							altri_sum_serie2_numeroItem++;
						}
						else if(j==2){
							altri_sum_serie3+=sum.longValue();
							altri_sum_serie3_numeroItem++;
						}
					}
				}

				
			}
			if(iterazione>slice){
				JSONObject bar = new JSONObject();
				bar.put(CostantiGrafici.DATA_KEY, CostantiGrafici.ALTRI_LABEL);

				for (int j = 0; j < numeroCategorie; j++) {
					// key che identifica la serie
					String key = categorie.getJSONObject(j).getString(CostantiGrafici.KEY_KEY);

					long v = -1;
					if(j==0){
						v = altri_sum_serie1;
						if(altri_sum_serie1_numeroItem>1 && (occupazioneBanda || tempoMedio)){
							v = v / altri_sum_serie1_numeroItem;
						}
					}
					else if(j==1){
						v = altri_sum_serie2;
						if(altri_sum_serie2_numeroItem>1 && (occupazioneBanda || tempoMedio)){
							v = v / altri_sum_serie2_numeroItem;
						}
					}
					else if(j==2){
						v = altri_sum_serie3;
						if(altri_sum_serie3_numeroItem>1 && (occupazioneBanda || tempoMedio)){
							v = v / altri_sum_serie3_numeroItem;
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
			grafico.put(CostantiGrafici.DATI_KEY, dati);
		}
		else{
			// reset categorie
			categorie.clear();
			JSONObject categoria = new JSONObject();
			categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.TOTALE_KEY);
			categoria.put(CostantiGrafici.LABEL_KEY , StatsUtils.getSubCaption(search,true));
			categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_TOTALE);
			categorie.add(categoria);
			
			grafico.put(CostantiGrafici.NO_DATA_KEY, CostantiGrafici.DATI_NON_PRESENTI);
		}
		return grafico;
	}

	public static JSONObject getJsonAndamentoTemporale(List<Res> list, StatsSearchForm search, String caption, String subCaption,StatisticType tempo,String direzioneLabelParam){
		JSONObject grafico = new JSONObject();

		SimpleDateFormat sdf;
		SimpleDateFormat sdf_last = new SimpleDateFormat(CostantiGrafici.PATTERN_HH, Locale.ITALIAN);
		if (StatisticType.ORARIA.equals(tempo)) {
			sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY_HH, Locale.ITALIAN);
		} else {
			sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY, Locale.ITALIAN);
		}

		grafico.put(CostantiGrafici.USA_COLORI_AUTOMATICI_KEY, false);
		grafico.put(CostantiGrafici.X_AXIS_GRID_LINES_KEY,true);
		grafico.put(CostantiGrafici.TITOLO_KEY, caption);
		grafico.put(CostantiGrafici.SOTTOTITOLO_KEY, subCaption);
		grafico.put(CostantiGrafici.X_AXIS_LABEL_DIREZIONE_KEY, getDirezioneLabel(direzioneLabelParam));		
		JSONArray categorie = new JSONArray();

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
		if (list != null && list.size()>0) {
			if(distribuzionePerEsiti){
				numeroCategorie = 3;
				showSeries = true;

				JSONObject categoria = new JSONObject();
				categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.OK_KEY);
				categoria.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.OK_LABEL);
				categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_OK);
				categorie.add(categoria);

				JSONObject categoria2 = new JSONObject();
				categoria2.put(CostantiGrafici.KEY_KEY , CostantiGrafici.FAULT_KEY);
				categoria2.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.FAULT_LABEL);
				categoria2.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_FAULT_APPLICATIVO);
				categorie.add(categoria2);

				JSONObject categoria3 = new JSONObject();
				categoria3.put(CostantiGrafici.KEY_KEY , CostantiGrafici.ERRORE_KEY);
				categoria3.put(CostantiGrafici.LABEL_KEY , CostantiGrafici.ERRORE_LABEL);
				categoria3.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_ERROR);
				categorie.add(categoria3);

			}
			else if(occupazioneBanda) {
				numeroCategorie = search.getTipiBandaImpostati().size();
				showSeries = numeroCategorie > 1 ? true : false;
				for (int i = 0; i < numeroCategorie; i++) {
					String[] strings = search.getTipiBanda();
					if(strings != null && strings.length == numeroCategorie){
						String tipoLat = strings[i];
						if(tipoLat != null){
							if(tipoLat.equals("0")){
								if(showSeries){
									JSONObject categoria = new JSONObject();
									categoria.put(CostantiGrafici.KEY_KEY , TipoBanda.COMPLESSIVA.getValue().toLowerCase().replace(" ", "_"));
									categoria.put(CostantiGrafici.LABEL_KEY , TipoBanda.COMPLESSIVA.getValue());
									categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_BANDA_COMPLESSIVA);
									categorie.add(i,categoria);
								}
							}
							else if(tipoLat.equals("1")){
								if(showSeries){
									JSONObject categoria = new JSONObject();
									categoria.put(CostantiGrafici.KEY_KEY , TipoBanda.INTERNA.getValue().toLowerCase().replace(" ", "_"));
									categoria.put(CostantiGrafici.LABEL_KEY , TipoBanda.INTERNA.getValue());
									categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_BANDA_INTERNA);
									categorie.add(i,categoria);
								}
							}
							else if(tipoLat.equals("2")){
								if(showSeries){
									JSONObject categoria = new JSONObject();
									categoria.put(CostantiGrafici.KEY_KEY , TipoBanda.ESTERNA.getValue().toLowerCase().replace(" ", "_"));
									categoria.put(CostantiGrafici.LABEL_KEY , TipoBanda.ESTERNA.getValue());
									categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_BANDA_ESTERNA);
									categorie.add(i,categoria);
								}
							}
						}
					}
				}
			}
			else if(tempoMedio) {
				numeroCategorie = search.getTipiLatenzaImpostati().size();
				showSeries = numeroCategorie > 1 ? true : false;
				for (int i = 0; i < numeroCategorie; i++) {
					String[] strings = search.getTipiLatenza();
					if(strings != null && strings.length == numeroCategorie){
						String tipoLat = strings[i];
						if(tipoLat != null){
							if(tipoLat.equals("0")){
								if(showSeries){
									JSONObject categoria = new JSONObject();
									categoria.put(CostantiGrafici.KEY_KEY , TipoLatenza.LATENZA_TOTALE.getValue().toLowerCase().replace(" ", "_"));
									categoria.put(CostantiGrafici.LABEL_KEY , TipoLatenza.LATENZA_TOTALE.getValue());
									categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_LATENZA_TOTALE);
									categorie.add(i,categoria);
								}
							}
							else if(tipoLat.equals("1")){
								if(showSeries){
									JSONObject categoria = new JSONObject();
									categoria.put(CostantiGrafici.KEY_KEY , TipoLatenza.LATENZA_SERVIZIO.getValue().toLowerCase().replace(" ", "_"));
									categoria.put(CostantiGrafici.LABEL_KEY , TipoLatenza.LATENZA_SERVIZIO.getValue());
									categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_LATENZA_SERVIZIO);
									categorie.add(i,categoria);
								}
							}
							else if(tipoLat.equals("2")){
								if(showSeries){
									JSONObject categoria = new JSONObject();
									categoria.put(CostantiGrafici.KEY_KEY , TipoLatenza.LATENZA_PORTA.getValue().toLowerCase().replace(" ", "_"));
									categoria.put(CostantiGrafici.LABEL_KEY , TipoLatenza.LATENZA_PORTA.getValue());
									categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_LATENZA_PORTA);
									categorie.add(i,categoria);
								}
							}
						}
					}
				}
			}
		}
		if(categorie.isEmpty()){
			JSONObject categoria = new JSONObject();
			categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.TOTALE_KEY);
			categoria.put(CostantiGrafici.LABEL_KEY , StatsUtils.getSubCaption(search,true));
			categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_TOTALE);
			categorie.add(categoria);
		}

		// Inserisco le catergorie del grafico
		grafico.put(CostantiGrafici.CATEGORIE_KEY, categorie);
		grafico.put(CostantiGrafici.MOSTRA_LEGENDA_KEY, showSeries);
		String yAxisName = StatsUtils.getSubCaption(search,true);
		grafico.put(CostantiGrafici.Y_AXIS_LABEL_KEY, yAxisName);

		JSONArray dati = new JSONArray();

		if (list != null && list.size()>0) {
			// check estremi in modo da visualizzare sempre gli estremi ed eliminare il problema
			// dell'unico risultato con il "puntino" difficilmente visualizzabile
			list = StatsUtils.checkEstremi(list, search, tempo, sdf);

			for (Res entry : list) {
				JSONObject point = new JSONObject();

				Date r = entry.getRisultato();
				Calendar c = Calendar.getInstance();
				c.setTime(r);

				StringBuilder sb = new StringBuilder();
				if (StatisticType.ORARIA.equals(tempo)) {
					sdf_last.applyPattern(CostantiGrafici.PATTERN_HH);
					c.add(Calendar.HOUR, 1);
					sb.append(sdf.format(r) + "-").append( sdf_last.format(c.getTime()));
				} else if (StatisticType.SETTIMANALE.equals(tempo)) {
					// settimanale
					sdf.applyPattern(CostantiGrafici.PATTERN_DD_MM_YY);
					sdf_last.applyPattern(CostantiGrafici.PATTERN_DD_MM_YY);

					c.add(Calendar.WEEK_OF_MONTH, 1);
					c.add(Calendar.DAY_OF_WEEK, -1);

					sb.append(sdf.format(r) + "-").append( sdf_last.format(c.getTime()));

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

				for (int j = 0; j < numeroCategorie; j++) {
					// key che identifica la serie
					String key = categorie.getJSONObject(j).getString(CostantiGrafici.KEY_KEY);
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
			grafico.put(CostantiGrafici.DATI_KEY, dati);
		} else {
			// reset categorie
			categorie.clear();
			JSONObject categoria = new JSONObject();
			categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.TOTALE_KEY);
			categoria.put(CostantiGrafici.LABEL_KEY , StatsUtils.getSubCaption(search,true));
			categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_TOTALE);

			grafico.put(CostantiGrafici.NO_DATA_KEY, CostantiGrafici.DATI_NON_PRESENTI);
		}
		return grafico;
	}


	/*** STATISTICHE PERSONALIZZATE ***/

	public static JSONObject getJsonAndamentoTemporaleStatPersonalizzate(SimpleDateFormat sdf,
			SimpleDateFormat sdf_last_hour, StatisticType tempo, Map<String, List<Res>> results, StatsSearchForm search, String caption, String subCaption, String direzioneLabelParam) {

		JSONObject grafico = new JSONObject();

		grafico.put(CostantiGrafici.TITOLO_KEY, caption);
		grafico.put(CostantiGrafici.SOTTOTITOLO_KEY, subCaption);
		grafico.put(CostantiGrafici.X_AXIS_LABEL_DIREZIONE_KEY, getDirezioneLabel(direzioneLabelParam));
		grafico.put(CostantiGrafici.USA_COLORI_AUTOMATICI_KEY, true);
		grafico.put(CostantiGrafici.X_AXIS_GRID_LINES_KEY,true);
		grafico.put(CostantiGrafici.COLONNE_LEGENDA_KEY, CostantiGrafici.COLONNE_LEGENDA_DEFAULT_VALUE);
		grafico.put(CostantiGrafici.LIMITE_COLONNE_LEGENDA_KEY, CostantiGrafici.COLONNE_LEGENDA_DEFAULT_VALUE);

		JSONArray categorie = new JSONArray();

		// Inserisco le categorie del grafico
		String yAxisName = StatsUtils.getSubCaption(search,true);

		
		grafico.put(CostantiGrafici.MOSTRA_LEGENDA_KEY, true);
		grafico.put(CostantiGrafici.Y_AXIS_LABEL_KEY, yAxisName);

		JSONArray dati = new JSONArray();

		if (results != null && results.size()>0) {

			Set<String> keys = results.keySet();
			Iterator<String> it = keys.iterator();
			int i = 0;
			while (it.hasNext()) {
				String key = it.next();
				String catKey = "cat_"+i;

				List<Res> list = results.get(key);
				
				JSONObject categoria = new JSONObject();
				categoria.put(CostantiGrafici.KEY_KEY , catKey);
				categoria.put(CostantiGrafici.LABEL_KEY , escapeJsonLabel(key));
				categorie.add(categoria);

				// check estremi in modo da visualizzare sempre gli estremi ed eliminare il problema
				// dell'unico risultato con il "puntino" difficilmente visualizzabile
				list = StatsUtils.checkEstremi(list,search, tempo, sdf);

				// imposto le category solo la prima volta
				if (i < 1) {
					for (Res entry : list) {
						JSONObject point = new JSONObject();
						Date r = entry.getRisultato();
						StringBuilder sb = new StringBuilder();
						if (StatisticType.ORARIA.equals(tempo)) {
							Calendar c = Calendar.getInstance();
							c.setTime(r);
							c.add(Calendar.HOUR, +1);
							sb.append(sdf.format(r) + "-"
									+ sdf_last_hour.format(c.getTime()));
						} else {
							sb.append(sdf.format(r));
						}
						
						point.put(CostantiGrafici.DATA_KEY, sb.toString());
						dati.add(point);
					}
				}

				for (int j = 0; j < list.size(); j++) {
					Res entry = list.get(j);
					JSONObject point = dati.getJSONObject(j);
					
					Number sum = entry.getSomma();
					String toolText = StatsUtils.getToolText(search,sum); 
					String value = StatsUtils.getValue(search,sum);
					
					point.put(catKey, value);
					point.put(catKey+CostantiGrafici.TOOLTIP_SUFFIX, toolText);
					
				}
				i++;
			}// fine ciclo dataset
			
			grafico.put(CostantiGrafici.DATI_KEY, dati);
		} else {
			// reset categorie
			categorie.clear();
			JSONObject categoria = new JSONObject();
			categoria.put(CostantiGrafici.KEY_KEY , CostantiGrafici.TOTALE_KEY);
			categoria.put(CostantiGrafici.LABEL_KEY , StatsUtils.getSubCaption(search,true));
			categoria.put(CostantiGrafici.COLORE_KEY , Colors.CSS_COLOR_TOTALE);
			categorie.add(categoria);

			grafico.put(CostantiGrafici.NO_DATA_KEY, CostantiGrafici.DATI_NON_PRESENTI);
		}

		// Inserisco le catergorie del grafico
		grafico.put(CostantiGrafici.CATEGORIE_KEY, categorie);
		return grafico;
	}

	public static JSONObject getJsonPieChartStatistichePersonalizzate( List<ResDistribuzione> list,  StatsSearchForm search, int slice, String caption, String subCaption) {
		JSONObject grafico = new JSONObject();
		
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

		JSONArray dati = new JSONArray();

		int maxLenghtLabel = 0;
		if(list!=null  && list.size()>0){
			int i = 0;
			long altri_sum=0;
			int altri_sum_numeroItem=0;

			for (ResDistribuzione entry : list) {
				String r = entry.getRisultato();
				Number sum = entry.getSomma();
				
				if(++i<=slice) {
					if(r.length() > maxLenghtLabel)
						maxLenghtLabel = r.length();
					
					String toolText = StatsUtils.getToolText(search,sum); 
					JSONObject spicchio = new JSONObject();
					spicchio.put(CostantiGrafici.LABEL_KEY, escapeJsonLabel(r));
					spicchio.put(CostantiGrafici.TOOLTIP_KEY, toolText);
					spicchio.put(CostantiGrafici.VALUE_KEY, sum);

					dati.add(spicchio);
				} else{
					altri_sum+=sum.longValue();
					altri_sum_numeroItem++;
				}
			}

			if(i>slice){

				long v = altri_sum;
				if(altri_sum_numeroItem>1 && (occupazioneBanda || tempoMedio)){
					v = v / altri_sum_numeroItem;
				}

				String toolText = StatsUtils.getToolText(search,v); 

				JSONObject spicchio = new JSONObject();
				spicchio.put(CostantiGrafici.LABEL_KEY, CostantiGrafici.ALTRI_LABEL);
				spicchio.put(CostantiGrafici.TOOLTIP_KEY, toolText);
				spicchio.put(CostantiGrafici.VALUE_KEY, v);
				
				dati.add(spicchio);
			}
			
			if(maxLenghtLabel > CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_LEGENDA_DEFAULT_VALUE)
				grafico.put(CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_LEGENDA_KEY, CostantiGrafici.LIMITE_LUNGHEZZA_LABEL_LEGENDA_DEFAULT_VALUE);
			
			grafico.put(CostantiGrafici.DATI_KEY, dati);
		}
		else{
			grafico.put(CostantiGrafici.NO_DATA_KEY, CostantiGrafici.DATI_NON_PRESENTI);
		}

		grafico.put(CostantiGrafici.DATI_KEY, dati);
		return grafico;	
	}

	public static JSONObject getJsonBarChartStatistichePersonalizzate( List<ResDistribuzione> list,  StatsSearchForm search, String direzioneLabelParam, int slice, String caption, String subCaption) {
		JSONObject grafico = new JSONObject();

		grafico.put(CostantiGrafici.TITOLO_KEY, caption);
		grafico.put(CostantiGrafici.SOTTOTITOLO_KEY, subCaption);
		grafico.put(CostantiGrafici.X_AXIS_LABEL_DIREZIONE_KEY, getDirezioneLabel(direzioneLabelParam));
		grafico.put(CostantiGrafici.USA_COLORI_AUTOMATICI_KEY, false);
		grafico.put(CostantiGrafici.X_AXIS_GRID_LINES_KEY,true);
		
		JSONArray categorie = new JSONArray();

		JSONObject categoria = new JSONObject();
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
		grafico.put(CostantiGrafici.CATEGORIE_KEY, categorie);
		grafico.put(CostantiGrafici.MOSTRA_LEGENDA_KEY, false);
		grafico.put(CostantiGrafici.Y_AXIS_LABEL_KEY, yAxisName);

		JSONArray dati = new JSONArray();

		int maxLenghtLabel = 0;
		if(list!=null  && list.size()>0){
			int iterazione = 0;
			long altri_sum_serie1=0;
			int altri_sum_serie1_numeroItem=0;

			if(slice != Integer.MAX_VALUE) // viene usato il maxValue per "disattivare" lo slice
				slice = slice * numeroCategorie;

			for (int z = 0 ; z <list.size() ; z++) {
				ResDistribuzione entry = list.get(z);
				JSONObject bar = new JSONObject();

				for (int j = 0; j < numeroCategorie; j++) {
					// key che identifica la serie
					String key = categorie.getJSONObject(j).getString(CostantiGrafici.KEY_KEY);
					Number sum =  entry.getSomma();

					if(++iterazione<=slice) {

						String r = entry.getRisultato();

						if(j==0){
							if(r.length() > maxLenghtLabel)
								maxLenghtLabel = r.length();
							
							bar.put(CostantiGrafici.DATA_KEY, escapeJsonLabel(r));
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
							altri_sum_serie1+=sum.longValue();
							altri_sum_serie1_numeroItem++;
						}
					}
				}
				
			}

			if(iterazione>slice){
				JSONObject bar = new JSONObject();
				bar.put(CostantiGrafici.DATA_KEY, CostantiGrafici.ALTRI_LABEL);

				for (int j = 0; j < numeroCategorie; j++) {
					// key che identifica la serie
					String key = categorie.getJSONObject(j).getString(CostantiGrafici.KEY_KEY);

					long v = -1;
					if(j==0){
						v = altri_sum_serie1;
						if(altri_sum_serie1_numeroItem>1 && (occupazioneBanda || tempoMedio)){
							v = v / altri_sum_serie1_numeroItem;
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
			grafico.put(CostantiGrafici.DATI_KEY, dati);
		} else{
			// reset categorie
			categorie.clear();
			categoria = new JSONObject();
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
}
