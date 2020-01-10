/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import java.text.MessageFormat;
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
import org.openspcoop2.core.statistiche.constants.TipoStatistica;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.datamodel.Res;
import org.openspcoop2.web.monitor.core.datamodel.ResBase;
import org.openspcoop2.web.monitor.core.datamodel.ResDistribuzione;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.openspcoop2.web.monitor.statistiche.bean.StatsSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiGrafici;
import org.openspcoop2.web.monitor.statistiche.constants.StatisticheCostanti;

/**
 * StatsUtils
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class StatsUtils {

	/* ******* PIE CHART ********** */

	public static String getXmlPieChartDistribuzione(List<ResDistribuzione> list, StatsSearchForm search, String caption, String subCaption, Integer slice){
		StringBuilder sb = new StringBuilder();

		sb.append("<chart caption='");
		sb.append(caption);
		sb.append( "' labelSepChar=':' subcaption='")
		.append(subCaption)
		.append( "'  decimalSeparator=',' thousandSeparator='.'  showPercentageValues='1' formatNumberScale='0' palette='1' ");
		sb.append(" decimals='0' enableSmartLabels='1' enableRotation='1' bgColor='99CCFF,FFFFFF' ");
		sb.append(" bgAlpha='40,100' bgRatio='0,100' bgAngle='360' showBorder='1' startingAngle='70' >");

		boolean occupazioneBanda = false;
		boolean tempoMedio = false;
		TipoVisualizzazione tipoVisualizzazione = search.getTipoVisualizzazione();
		if(tipoVisualizzazione.equals(TipoVisualizzazione.DIMENSIONE_TRANSAZIONI)) {
			occupazioneBanda = true;
		}
		else if(tipoVisualizzazione.equals(TipoVisualizzazione.TEMPO_MEDIO_RISPOSTA)) {
			tempoMedio = true;
		}

		if(list!=null  && list.size()>0){
			int i = 0;
			long altri_sum=0;
			int altri_sum_numeroItem=0;

			for (ResDistribuzione entry : list) {
				String r = entry.getRisultato();
				Number sum = entry.getSomma();

				if(++i<=slice) {
					String toolText =getToolText(search,sum); 
					if(!entry.getParentMap().isEmpty())
						toolText = StatsUtils.getToolTextConParent(search, r ,entry.getParentMap(), sum);

					sb.append("<set label='");
					sb.append(StringEscapeUtils.escapeXml(r));
					sb.append("' value='");
					sb.append(sum);
					sb.append("' toolText='");
					sb.append(toolText);
					sb.append("'/>");
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

				String toolText = getToolText(search,v); 
				sb.append("<set label='" + CostantiGrafici.ALTRI_LABEL + "' value='");
				sb.append(v);
				sb.append("' toolText='");
				sb.append(toolText); 
				sb.append("'/>");
			}
		}
		else{
			sb.append("<set label='"+CostantiGrafici.DATI_NON_PRESENTI+"' value='1'");
			sb.append(" toolText='"+CostantiGrafici.DATI_NON_PRESENTI+"'/>");
		}

		sb.append("</chart>");

		return sb.toString();
	}



	/* ************** BAR CHART **************** */

	public static String getXmlBarChartDistribuzione(List<ResDistribuzione> list, StatsSearchForm search, String caption, String subCaption, Integer slice){
		List<ResBase> listI = new ArrayList<ResBase>();
		listI.addAll(list);
		return _getXmlBarChartDistribuzione(listI, search, caption, subCaption, slice,null);
	}
	public static String getXmlBarChartDistribuzione(List<Res> list, StatsSearchForm search, String caption, String subCaption, Integer slice, StatisticType tempo){
		List<ResBase> listI = new ArrayList<ResBase>();
		listI.addAll(list);
		return _getXmlBarChartDistribuzione(listI, search, caption, subCaption, slice,tempo);
	}
	//	public static String getXmlBarChartDistribuzione(List<Res> list, StatsSearchForm search, String caption, String subCaption, Integer slice, StatisticType tempo, String ... series){
	//		List<ResBase> listI = new ArrayList<ResBase>();
	//		listI.addAll(list);
	//		return _getXmlBarChartDistribuzione(listI, search, caption, subCaption, slice,tempo,series);
	//	}
	private static String _getXmlBarChartDistribuzione(List<ResBase> list, StatsSearchForm search, String caption, String subCaption, Integer sliceParam, StatisticType tempo,
			String ... series){
		StringBuilder sb = new StringBuilder();

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
		String color = null;
		if(list!=null  && list.size()>0){
			if(series!=null && series.length>0){
				showSeries = true;
				numeroCategorie = series.length;
				color = Colors.CODE_TOTALE; // TODO gestire i colori in questo caso. Comunque per ora è un caso non usato, il metodo con le series come parametro è commentato
			}
			else {
				if(distribuzionePerEsiti){
					numeroCategorie = 3;
					series = new String[numeroCategorie];
					showSeries = true;
					series[0] = CostantiGrafici.OK_LABEL;
					series[1] = CostantiGrafici.FAULT_LABEL;
					series[2] = CostantiGrafici.ERRORE_LABEL;
					color = Colors.CODE_OK+","+Colors.CODE_FAULT_APPLICATIVO+","+Colors.CODE_ERROR;
				}
				else if(occupazioneBanda) {
					numeroCategorie = search.getTipiBandaImpostati().size();
					showSeries = numeroCategorie > 1 ? true : false;
					if(showSeries){
						series = new String[numeroCategorie];
					}
					for (int i = 0; i < numeroCategorie; i++) {
						String[] strings = search.getTipiBanda();
						if(strings != null && strings.length == numeroCategorie){
							String tipoLat = strings[i];
							if(tipoLat != null){
								if(tipoLat.equals("0")){
									if(showSeries){
										series[i] = TipoBanda.COMPLESSIVA.getValue();
									}
									if(color!=null){
										color = color + ",";
										color = color + Colors.CODE_BANDA_COMPLESSIVA;
									}
									else{
										color = Colors.CODE_BANDA_COMPLESSIVA;
									}
								}
								else if(tipoLat.equals("1")){
									if(showSeries){
										series[i] = TipoBanda.INTERNA.getValue();
									}
									if(color!=null){
										color = color + ",";
										color = color + Colors.CODE_BANDA_INTERNA;
									}
									else{
										color = Colors.CODE_BANDA_INTERNA;
									}
								}
								else if(tipoLat.equals("2")){
									if(showSeries){
										series[i] = TipoBanda.ESTERNA.getValue();
									}
									if(color!=null){
										color = color + ",";
										color = color + Colors.CODE_BANDA_ESTERNA;
									}
									else{
										color = Colors.CODE_BANDA_ESTERNA;
									}
								}
							}
						}
					}
				}
				else if(tempoMedio) {
					numeroCategorie = search.getTipiLatenzaImpostati().size();
					showSeries = numeroCategorie > 1 ? true : false;
					if(showSeries){
						series = new String[numeroCategorie];
					}
					for (int i = 0; i < numeroCategorie; i++) {
						String[] strings = search.getTipiLatenza();
						if(strings != null && strings.length == numeroCategorie){
							String tipoLat = strings[i];
							if(tipoLat != null){
								if(tipoLat.equals("0")){
									if(showSeries){
										series[i] = TipoLatenza.LATENZA_TOTALE.getValue();
									}
									if(color!=null){
										color = color + ",";
										color = color + Colors.CODE_LATENZA_TOTALE;
									}
									else{
										color = Colors.CODE_LATENZA_TOTALE;
									}
								}
								else if(tipoLat.equals("1")){
									if(showSeries){
										series[i] = TipoLatenza.LATENZA_SERVIZIO.getValue();
									}
									if(color!=null){
										color = color + ",";
										color = color + Colors.CODE_LATENZA_SERVIZIO;
									}
									else{
										color = Colors.CODE_LATENZA_SERVIZIO;
									}
								}
								else if(tipoLat.equals("2")){
									if(showSeries){
										series[i] = TipoLatenza.LATENZA_PORTA.getValue();
									}
									if(color!=null){
										color = color + ",";
										color = color + Colors.CODE_LATENZA_PORTA;
									}
									else{
										color = Colors.CODE_LATENZA_PORTA;
									}
								}
							}
						}
					}
				}
			}
		}
		if(color==null){
			color = Colors.CODE_TOTALE;
		}
		color = "paletteColors='"+ color + "' ";

		String xName = "Data";
		String xNameDirective = "xAxisName='"+xName+"'";
		xNameDirective = ""; // informazione inutile, la x si capisce da sola

		String yAxisName = StatsUtils.getSubCaption(search,true);

		sb.append("<chart pallete='3' caption='");
		sb.append(caption);
		sb.append("' subCaption='");
		sb.append(subCaption);
		sb.append("' "+xNameDirective+" yAxisName='");
		sb.append( yAxisName);
		sb.append("' decimalSeparator=',' thousandSeparator='.'  decimals='0' useRoundEdges='1' showSum='1' formatNumber='1' formatNumberScale='0' labelDisplay='Rotate' slantLabels='1' "+color+">");

		SimpleDateFormat sdf = null;


		if(list!=null  && list.size()>0){
			int iterazione = 0;
			long altri_sum_serie1=0;
			int altri_sum_serie1_numeroItem=0;
			long altri_sum_serie2=0;
			int altri_sum_serie2_numeroItem=0;
			long altri_sum_serie3=0;
			int altri_sum_serie3_numeroItem=0;

			StringBuilder labelsSB = new StringBuilder();
			StringBuilder barSB_serie1 = new StringBuilder();
			StringBuilder barSB_serie2 = new StringBuilder();
			StringBuilder barSB_serie3 = new StringBuilder();
			//			StringBuilder barErrorSB = new StringBuilder();

			labelsSB.append("<categories>");

			if(showSeries==false){
				barSB_serie1.append("<dataset seriesName='' showValues='1' showLabels='1'>");
			}
			else{
				barSB_serie1.append("<dataset seriesName='"+series[0]+"' showValues='1' showLabels='1'>");
				if(numeroCategorie>1){
					barSB_serie2.append("<dataset seriesName='"+series[1]+"' showValues='1' showLabels='1'>");
				}
				if(numeroCategorie>2){
					barSB_serie3.append("<dataset seriesName='"+series[2]+"' showValues='1' showLabels='1'>");
				}
			}


			//			barErrorSB.append("<dataset seriesName='' showValues='1' showLabels='1'>");

			int slice = sliceParam;
			if(slice != Integer.MAX_VALUE) // viene usato il maxValue per "disattivare" lo slice
				slice = slice * numeroCategorie;

			for (ResBase entry : list) {

				for (int j = 0; j < numeroCategorie; j++) {

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
							if(sdf==null){
								if (StatisticType.ORARIA.equals(tempo)) {
									sdf = new SimpleDateFormat("dd/MM/yy HH", Locale.ITALIAN);
								} 
								else if (StatisticType.GIORNALIERA.equals(tempo)) {
									sdf = new SimpleDateFormat("dd/MM/yy", Locale.ITALIAN);
								} 
								else if (StatisticType.SETTIMANALE.equals(tempo)) {
									sdf = new SimpleDateFormat("dd/MM/yy", Locale.ITALIAN);
								} 
								else if (StatisticType.MENSILE.equals(tempo)) {
									sdf = new SimpleDateFormat("MMM/yy", Locale.ITALIAN);
								} 
							}
							Date rDate = ((Res)entry).getRisultato();
							r = sdf.format(rDate);
						}
						else{
							r = "ERROR: TIPO ["+entry.getClass().getName()+"] NON GESTITO";
						}

						if(j==0){
							labelsSB.append("<category label='");
							labelsSB.append(StringEscapeUtils.escapeXml(r));
							labelsSB.append("' />");
						}

						String toolText = StatsUtils.getToolText(search,sum);

						if(!entry.getParentMap().isEmpty()){
							toolText = StatsUtils.getToolTextConParent(search,r, entry.getParentMap(), sum);
						}

						String value = StatsUtils.getValue(search,sum);
						//					String toolTextErr =getToolText(search,10); 

						StringBuilder barSB = null;
						if(j==0){
							barSB = barSB_serie1;
						}
						else if(j==1){
							barSB = barSB_serie2;
						}
						else if(j==2){
							barSB = barSB_serie3;
						}

						barSB.append("<set value='");
						barSB.append(value);
						barSB.append("' toolText='");
						barSB.append(toolText);
						barSB.append("' />");

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
				String label = CostantiGrafici.ALTRI_LABEL;

				labelsSB.append("<category label='");
				labelsSB.append(label);
				labelsSB.append("' />");

				for (int j = 0; j < numeroCategorie; j++) {

					StringBuilder barSB = null;
					long v = -1;
					if(j==0){
						barSB = barSB_serie1;
						v = altri_sum_serie1;
						if(altri_sum_serie1_numeroItem>1 && (occupazioneBanda || tempoMedio)){
							v = v / altri_sum_serie1_numeroItem;
						}
					}
					else if(j==1){
						barSB = barSB_serie2;
						v = altri_sum_serie2;
						if(altri_sum_serie2_numeroItem>1 && (occupazioneBanda || tempoMedio)){
							v = v / altri_sum_serie2_numeroItem;
						}
					}
					else if(j==2){
						barSB = barSB_serie3;
						v = altri_sum_serie3;
						if(altri_sum_serie3_numeroItem>1 && (occupazioneBanda || tempoMedio)){
							v = v / altri_sum_serie3_numeroItem;
						}
					}

					String toolText = getToolText(search,v); 
					String value = StatsUtils.getValue(search,v);

					barSB.append("<set value='");
					barSB.append(value);
					barSB.append("' toolText='");
					barSB.append(toolText);
					barSB.append("' />");

				}

			}

			labelsSB.append("</categories>");
			barSB_serie1.append("</dataset>");
			if(numeroCategorie>1){
				barSB_serie2.append("</dataset>");
			}
			if(numeroCategorie>2){
				barSB_serie3.append("</dataset>");
			}
			//			barErrorSB.append("</dataset>");

			sb.append(labelsSB.toString());
			//			sb.append(barErrorSB.toString());
			sb.append(barSB_serie1.toString());
			if(barSB_serie2.length()>0){
				sb.append(barSB_serie2.toString());
			}
			if(barSB_serie3.length()>0){
				sb.append(barSB_serie3.toString());
			}
		}
		else{
			sb.append( "<categories> ");
			sb.append("     <category label='-'/>");
			sb.append( " </categories>");
			sb.append( "<dataset seriesName='' showValues='1' showLabels='1' >");
			sb.append("     <set value='0' label='"+CostantiGrafici.DATI_NON_PRESENTI+"' toolText='"+CostantiGrafici.DATI_NON_PRESENTI+"' />");
			sb.append( " </dataset>");
		}

		sb.append("</chart> ");

		return sb.toString();
	}




	/* ****************** ANDAMENTO TEMPORALE ******************** */

	public static List<Res> checkEstremi(List<Res> list, StatsSearchForm search, StatisticType tempo, SimpleDateFormat sdf){
		Date dataInizio = search.getDataInizio();
		Date dataFine = search.getDataFine();
		Date risultatoLower = list.get(0).getRisultato();
		Date risultatoHigh = list.get(list.size()-1).getRisultato();
		int sizeRisultati = 0;
		if(list.get(0).getSomme()!=null){
			sizeRisultati = list.get(0).getSomme().size();
		}
		String dataInizioAsString = null;
		String dataFineAsString = null;
		String risultatoLowerAsString = null;
		String risultatoHighAsString = null;
		if (StatisticType.ORARIA.equals(tempo)) {
			dataInizioAsString = sdf.format(dataInizio);
			dataFineAsString = sdf.format(dataFine);
			risultatoLowerAsString = sdf.format(risultatoLower);
			risultatoHighAsString = sdf.format(risultatoHigh);
		} else if (StatisticType.SETTIMANALE.equals(tempo)) {
			// settimanale
			sdf.applyPattern("dd/MM/yy");
			dataInizioAsString = sdf.format(dataInizio);
			dataFineAsString = sdf.format(dataFine);
			risultatoLowerAsString = sdf.format(risultatoLower);
			risultatoHighAsString = sdf.format(risultatoHigh);
		} else if (StatisticType.MENSILE.equals(tempo)) {
			// mensile
			sdf.applyPattern("MMM/yy");
			dataInizioAsString = sdf.format(dataInizio);
			dataFineAsString = sdf.format(dataFine);
			risultatoLowerAsString = sdf.format(risultatoLower);
			risultatoHighAsString = sdf.format(risultatoHigh);
		} else {
			// giornaliero
			dataInizioAsString = sdf.format(dataInizio);
			dataFineAsString = sdf.format(dataFine);
			risultatoLowerAsString = sdf.format(risultatoLower);
			risultatoHighAsString = sdf.format(risultatoHigh);
		}

		if(!dataInizioAsString.equals(risultatoLowerAsString)){
			Res r = new Res();
			r.setRisultato(dataInizio);
			r.setSomma(0);
			ArrayList<Number> l = new ArrayList<Number>();
			for (int i = 0; i < sizeRisultati; i++) {
				l .add(0);
			}
			r.setSomme(l);
			List<Res> newList = new ArrayList<Res>();
			newList.add(r);
			newList.addAll(list);
			list = newList;
		}
		if(!dataFineAsString.equals(risultatoHighAsString)){
			Res r = new Res();
			//			if (StatisticType.ORARIA.equals(tempo)) {
			//				Calendar c = Calendar.getInstance();
			//				c.setTime((Date)dataFine.clone());
			//				c.add(Calendar.HOUR, -1);
			//				r.setRisultato(c.getTime());
			//			}
			//			else{
			r.setRisultato(dataFine);
			//}
			r.setSomma(0);
			ArrayList<Number> l = new ArrayList<Number>();
			for (int i = 0; i < sizeRisultati; i++) {
				l .add(0);
			}
			r.setSomme(l);
			list.add(r);
		}
		return list;
	}

	public static String getXmlAndamentoTemporale(List<Res> list, StatsSearchForm search, String caption, String subCaption,StatisticType tempo){
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat sdf;
		SimpleDateFormat sdf_last = new SimpleDateFormat(CostantiGrafici.PATTERN_HH, Locale.ITALIAN);
		if (StatisticType.ORARIA.equals(tempo)) {
			sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY_HH, Locale.ITALIAN);
		} else {
			sdf = new SimpleDateFormat(CostantiGrafici.PATTERN_DD_MM_YY, Locale.ITALIAN);
		}

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
		boolean visualizzaSeriesName = false;
		String [] series = null;
		String color = null;
		if (list != null && list.size()>0) {
			if(distribuzionePerEsiti){
				numeroCategorie = 3;
				visualizzaSeriesName = true;
				series = new String[numeroCategorie];
				series[0] = CostantiGrafici.OK_LABEL;
				series[1] = CostantiGrafici.FAULT_LABEL;
				series[2] = CostantiGrafici.ERRORE_LABEL;
				color = Colors.CODE_OK+","+Colors.CODE_FAULT_APPLICATIVO+","+Colors.CODE_ERROR;
			}
			else if(occupazioneBanda) {
				numeroCategorie = search.getTipiBandaImpostati().size();
				visualizzaSeriesName = numeroCategorie > 1 ? true : false;
				if(visualizzaSeriesName){
					series = new String[numeroCategorie];
				}
				for (int i = 0; i < numeroCategorie; i++) {
					String[] strings = search.getTipiBanda();
					if(strings != null && strings.length == numeroCategorie){
						String tipoLat = strings[i];
						if(tipoLat != null){
							if(tipoLat.equals("0")){
								if(visualizzaSeriesName){
									series[i] = TipoBanda.COMPLESSIVA.getValue();
								}
								if(color!=null){
									color = color + ",";
									color = color + Colors.CODE_BANDA_COMPLESSIVA;
								}
								else{
									color = Colors.CODE_BANDA_COMPLESSIVA;
								}
							}
							else if(tipoLat.equals("1")){
								if(visualizzaSeriesName){
									series[i] = TipoBanda.INTERNA.getValue();
								}
								if(color!=null){
									color = color + ",";
									color = color + Colors.CODE_BANDA_INTERNA;
								}
								else{
									color = Colors.CODE_BANDA_INTERNA;
								}
							}
							else if(tipoLat.equals("2")){
								if(visualizzaSeriesName){
									series[i] = TipoBanda.ESTERNA.getValue();
								}
								if(color!=null){
									color = color + ",";
									color = color + Colors.CODE_BANDA_ESTERNA;
								}
								else{
									color = Colors.CODE_BANDA_ESTERNA;
								}
							}
						}
					}
				}
			}
			else if(tempoMedio) {
				numeroCategorie = search.getTipiLatenzaImpostati().size();
				visualizzaSeriesName = numeroCategorie > 1 ? true : false;
				if(visualizzaSeriesName){
					series = new String[numeroCategorie];
				}
				for (int i = 0; i < numeroCategorie; i++) {
					String[] strings = search.getTipiLatenza();
					if(strings != null && strings.length == numeroCategorie){
						String tipoLat = strings[i];
						if(tipoLat != null){
							if(tipoLat.equals("0")){
								if(visualizzaSeriesName){
									series[i] = TipoLatenza.LATENZA_TOTALE.getValue();
								}
								if(color!=null){
									color = color + ",";
									color = color + Colors.CODE_LATENZA_TOTALE;
								}
								else{
									color = Colors.CODE_LATENZA_TOTALE;
								}
							}
							else if(tipoLat.equals("1")){
								if(visualizzaSeriesName){
									series[i] = TipoLatenza.LATENZA_SERVIZIO.getValue();
								}
								if(color!=null){
									color = color + ",";
									color = color + Colors.CODE_LATENZA_SERVIZIO;
								}
								else{
									color = Colors.CODE_LATENZA_SERVIZIO;
								}
							}
							else if(tipoLat.equals("2")){
								if(visualizzaSeriesName){
									series[i] = TipoLatenza.LATENZA_PORTA.getValue();
								}
								if(color!=null){
									color = color + ",";
									color = color + Colors.CODE_LATENZA_PORTA;
								}
								else{
									color = Colors.CODE_LATENZA_PORTA;
								}
							}
						}
					}
				}
			}
		}
		if(color==null){
			color = Colors.CODE_TOTALE;
		}
		color = "paletteColors='"+ color + "' ";

		String xName = "Data";
		String xNameDirective = "xAxisName='"+xName+"'";
		xNameDirective = ""; // informazione inutile, la x si capisce da sola

		String yAxisName = StatsUtils.getSubCaption(search,true);
		sb.append( "<chart caption='")
		.append(caption)
		.append( "' labelSepChar=':' subcaption='")
		.append( subCaption)
		.append( "' palette='3' numVisiblePlot='30' "+xNameDirective+" yAxisName='")
		.append( yAxisName)
		.append( "'   decimalSeparator=',' thousandSeparator='.'  lineThickness='2' showValues='0'  rotateNames='1' slantLabels='1' areaOverColumns='0' formatNumberScale='0' "+color+">");

		if (list != null && list.size()>0) {


			// check estremi in modo da visualizzare sempre gli estremi ed eliminare il problema
			// dell'unico risultato con il "puntino" difficilmente visualizzabile
			list = checkEstremi(list, search, tempo, sdf);

			sb.append( "<categories>");

			for (Res entry : list) {


				Date r = entry.getRisultato();
				Calendar c = Calendar.getInstance();
				c.setTime(r);

				if (StatisticType.ORARIA.equals(tempo)) {
					sdf_last.applyPattern(CostantiGrafici.PATTERN_HH);

					c.add(Calendar.HOUR, 1);

					sb.append( "	<category label='" + sdf.format(r) + "-")
					.append( sdf_last.format(c.getTime()) + "' />");
				} else if (StatisticType.SETTIMANALE.equals(tempo)) {
					// settimanale
					sdf.applyPattern(CostantiGrafici.PATTERN_DD_MM_YY);
					sdf_last.applyPattern(CostantiGrafici.PATTERN_DD_MM_YY);

					c.add(Calendar.WEEK_OF_MONTH, 1);
					c.add(Calendar.DAY_OF_WEEK, -1);

					sb.append( "	<category label='" + sdf.format(r) + "-")
					.append( sdf_last.format(c.getTime()) + "' />");

				} else if (StatisticType.MENSILE.equals(tempo)) {
					// mensile
					sdf.applyPattern(CostantiGrafici.PATTERN_MMM_YY);

					sb.append("	<category label='" + sdf.format(r) + "' />");

				} else {
					// giornaliero
					sb.append( "	<category label='" + sdf.format(r) + "' />");
				}

			}
			sb.append("</categories>");

			for (int i = 0; i < numeroCategorie; i++) {
				sb.append( "<dataset renderAs='Line' ");

				if(visualizzaSeriesName){
					sb.append("seriesname='").append(series[i]).append("' ");
				}

				sb.append(">");
				for (Res entry : list) {
					Number sum = entry.getSomme().get(i);
					//				Number sum = entry.getSomma();

					String toolText = StatsUtils.getToolText(search,sum);
					String value = StatsUtils.getValue(search,sum);
					sb.append( "<set value='"	+ value + "' toolText='" + toolText + "' />");

				}

				sb.append("</dataset>");
			}
		}
		else{
			sb.append( "<categories> ");
			sb.append( "    <category label='"+sdf.format(search.getDataInizio())+"' />	");
			sb.append( "    <category label='"+sdf.format(search.getDataFine())+"' />");
			sb.append( " </categories>");
			sb.append( "<dataset renderAs='Line' >");
			sb.append( "   <set value='0' toolText='"+CostantiGrafici.DATI_NON_PRESENTI+"'/>");
			sb.append( "   <set value='0' toolText='"+CostantiGrafici.DATI_NON_PRESENTI+"'/>");
			sb.append( " </dataset>");
		}
		sb.append( "</chart>");

		return sb.toString();		
	}

	/*** STATISTICHE PERSONALIZZATE ***/

	public static String getXmlAndamentoTemporaleStatPersonalizzate(SimpleDateFormat sdf,
			SimpleDateFormat sdf_last_hour, StatisticType tempo, Map<String, List<Res>> results, StatsSearchForm search, String caption, String subCaption) {
		StringBuffer sb = new StringBuffer();

		String xName = "Data";
		String xNameDirective = "xAxisName='"+xName+"'";
		xNameDirective = ""; // informazione inutile, la x si capisce da sola

		String yAxisName = StatsUtils.getSubCaption(search,true);

		sb.append("<chart caption='");
		sb.append(caption);
		sb.append("' labelSepChar=':' subcaption='");
		sb.append(subCaption);
		sb.append( "' palette='3' numVisiblePlot='30' "+xNameDirective+" yAxisName='");
		sb.append( yAxisName);
		sb.append( "'   decimalSeparator=',' thousandSeparator='.'  lineThickness='2' showValues='0'  rotateNames='1' slantLabels='1' areaOverColumns='0' formatNumberScale='0'>");

		if (results != null && results.size() > 0) {

			sb.append("<categories>");

			Set<String> keys = results.keySet();
			Iterator<String> it = keys.iterator();
			int i = 0;
			while (it.hasNext()) {
				String key = it.next();

				List<Res> list = results.get(key);

				// check estremi in modo da visualizzare sempre gli estremi ed eliminare il problema
				// dell'unico risultato con il "puntino" difficilmente visualizzabile
				list = StatsUtils.checkEstremi(list,search, tempo, sdf);

				// imposto le category solo la prima volta
				if (i < 1) {
					for (Res entry : list) {
						Date r = entry.getRisultato();
						if (StatisticType.ORARIA.equals(tempo)) {
							Calendar c = Calendar.getInstance();
							c.setTime(r);
							c.add(Calendar.HOUR, +1);
							sb.append( "	<category label='"
									+ sdf.format(r) + "-"
									+ sdf_last_hour.format(c.getTime())
									+ "' />");
						} else {
							sb.append( "	<category label='"	+ sdf.format(r) + "' />");
						}

					}
					sb.append( "</categories>");
				}

				// imposto i dataset
				// sb.append("<dataset renderAs='Line' seriesname='"+StringEscapeUtils.escapeXml(key)+"' >");
				sb.append("<dataset renderAs='Line' seriesname='"+key+"' >");
				for (Res entry : list) {
					Number sum = entry.getSomma();
					String toolText = StatsUtils.getToolText(search,sum); 
					String value = StatsUtils.getValue(search,sum);
					sb.append("<set value='" 	+ value + "' toolText='"	+ toolText + "'/>");
				}

				sb.append("</dataset>");
				i++;
			}// fine ciclo dataset
		}
		else{
			sb.append( "<categories> ");
			sb.append( "    <category label='"+sdf.format(search.getDataInizio())+"' />	");
			sb.append( "    <category label='"+sdf.format(search.getDataFine())+"' />");
			sb.append( " </categories>");
			sb.append( "<dataset renderAs='Line' >");
			sb.append( "   <set value='0' toolText='"+CostantiGrafici.DATI_NON_PRESENTI+"'/>");
			sb.append( "   <set value='0' toolText='"+CostantiGrafici.DATI_NON_PRESENTI+"'/>");
			sb.append( " </dataset>");
		}

		sb.append("</chart>");

		return sb.toString();
	}

	public static String getXmlPieChartStatistichePersonalizzate( List<ResDistribuzione> list,  StatsSearchForm search, int slice, String caption, String subCaption) {
		StringBuffer sb = new StringBuffer();
		sb.append("<chart caption='");
		sb.append(caption);
		sb.append("' labelSepChar=':' subcaption='");
		sb.append(subCaption);
		sb.append( "'  decimalSeparator=',' thousandSeparator='.'  showPercentageValues='1' formatNumberScale='0' palette='1'  decimals='0' enableSmartLabels='1' enableRotation='1' bgColor='99CCFF,FFFFFF' bgAlpha='40,100' bgRatio='0,100' bgAngle='360' showBorder='1' startingAngle='70' >");

		if (list != null && list.size()>0) {

			boolean occupazioneBanda = false;
			boolean tempoMedio = false;
			TipoVisualizzazione tipoVisualizzazione = search.getTipoVisualizzazione();
			if(tipoVisualizzazione.equals(TipoVisualizzazione.DIMENSIONE_TRANSAZIONI)) {
				occupazioneBanda = true;
			}
			else if(tipoVisualizzazione.equals(TipoVisualizzazione.TEMPO_MEDIO_RISPOSTA)) {
				tempoMedio = true;
			}

			int i = 0;
			long altri_sum = 0;
			int altri_sum_numeroItem=0;

			for (ResDistribuzione entry : list) {
				String r = entry.getRisultato();
				Number sum = entry.getSomma();

				if (++i <= slice) {
					String toolText = StatsUtils.getToolText(search,sum); 
					sb.append("<set label='" + StringEscapeUtils.escapeXml(r) + "' value='" + sum + "' toolText='" + toolText + "'/>");
				} else{
					altri_sum += sum.longValue();
					altri_sum_numeroItem++;
				}
			}
			if (i > slice) {

				long v = altri_sum;
				if(altri_sum_numeroItem>1 && (occupazioneBanda || tempoMedio)){
					v = v / altri_sum_numeroItem;
				}

				String toolText = StatsUtils.getToolText(search,v); 
				sb.append("<set label='"+CostantiGrafici.ALTRI_LABEL+"' value='" + v+ "' toolText='"	+ toolText + "'/>");
			}
		}

		else{
			sb.append("<set label='"+CostantiGrafici.DATI_NON_PRESENTI+"' value='1'");
			sb.append(" toolText='"+CostantiGrafici.DATI_NON_PRESENTI+"'/>");
		}

		sb.append("</chart>");


		return sb.toString();
	}

	public static String getXmlBarChartStatistichePersonalizzate( List<ResDistribuzione> list,  StatsSearchForm search, int slice, String caption, String subCaption) {
		StringBuffer sb = new StringBuffer();

		// calcolo series
		boolean occupazioneBanda = false;
		boolean tempoMedio = false;
		String color = Colors.CODE_TOTALE;
		//		TipoVisualizzazione tipoVisualizzazione = search.getTipoVisualizzazione();
		//		if(tipoVisualizzazione.equals(TipoVisualizzazione.TEMPO_MEDIO_RISPOSTA)) {
		//			TipoLatenza tipoLatenza = search.getTipoLatenza();
		//
		//			switch(tipoLatenza){
		//			case LATENZA_PORTA: color = Colors.CODE_LATENZA_PORTA; break;
		//			case LATENZA_SERVIZIO: color = Colors.CODE_LATENZA_SERVIZIO; break;
		//			case LATENZA_TOTALE: color = Colors.CODE_LATENZA_TOTALE; break;
		//			}
		//
		//		}

		int numeroCategorie = 1;

		color = "paletteColors='"+ color + "' ";

		String xName = "Data";
		String xNameDirective = "xAxisName='"+xName+"'";
		xNameDirective = ""; // informazione inutile, la x si capisce da sola

		String yAxisName = StatsUtils.getSubCaption(search,true);

		sb.append("<chart pallete='3' caption='");
		sb.append(caption);
		sb.append("' subCaption='");
		sb.append(subCaption);
		sb.append("' "+xNameDirective+" yAxisName='");
		sb.append( yAxisName);
		sb.append("' decimalSeparator=',' thousandSeparator='.'  decimals='0' useRoundEdges='1' showSum='1' formatNumber='1' formatNumberScale='0' labelDisplay='Rotate' slantLabels='1' "+color+">");


		if(list!=null  && list.size()>0){
			int iterazione = 0;
			long altri_sum_serie1=0;
			int altri_sum_serie1_numeroItem=0;

			StringBuilder labelsSB = new StringBuilder();
			StringBuilder barSB_serie1 = new StringBuilder();

			labelsSB.append("<categories>");

			barSB_serie1.append("<dataset seriesName='' showValues='1' showLabels='1'>");

			if(slice != Integer.MAX_VALUE) // viene usato il maxValue per "disattivare" lo slice
				slice = slice * numeroCategorie;

			for (ResDistribuzione entry : list) {

				for (int j = 0; j < numeroCategorie; j++) {

					Number sum =  entry.getSomma();

					if(++iterazione<=slice) {

						String r = entry.getRisultato();

						if(j==0){
							labelsSB.append("<category label='");
							labelsSB.append(StringEscapeUtils.escapeXml(r));
							labelsSB.append("' />");
						}

						String value = StatsUtils.getValue(search,sum);
						String toolText =getToolText(search,sum); 
						//					String toolTextErr =getToolText(search,10); 

						StringBuilder barSB = null;
						if(j==0){
							barSB = barSB_serie1;
						}

						barSB.append("<set value='");
						barSB.append(value);
						barSB.append("' toolText='");
						barSB.append(toolText);
						barSB.append("' />");

					} else{

						if(j==0){
							altri_sum_serie1+=sum.longValue();
							altri_sum_serie1_numeroItem++;
						}
					}
				}
			}

			if(iterazione>slice){
				String label = CostantiGrafici.ALTRI_LABEL;

				labelsSB.append("<category label='");
				labelsSB.append(label);
				labelsSB.append("' />");

				for (int j = 0; j < numeroCategorie; j++) {

					StringBuilder barSB = null;
					long v = -1;
					if(j==0){
						barSB = barSB_serie1;
						v = altri_sum_serie1;
						if(altri_sum_serie1_numeroItem>1 && (occupazioneBanda || tempoMedio)){
							v = v / altri_sum_serie1_numeroItem;
						}
					}

					String value = StatsUtils.getValue(search,v);
					String toolText = getToolText(search,v); 

					barSB.append("<set value='");
					barSB.append(value);
					barSB.append("' toolText='");
					barSB.append(toolText);
					barSB.append("' />");

				}

			}

			labelsSB.append("</categories>");
			barSB_serie1.append("</dataset>");
			sb.append(labelsSB.toString());
			sb.append(barSB_serie1.toString());
		} else{
			sb.append( "<categories> ");
			sb.append("     <category label='-'/>");
			sb.append( " </categories>");
			sb.append( "<dataset seriesName='' showValues='1' showLabels='1' >");
			sb.append("     <set value='0' label='"+CostantiGrafici.DATI_NON_PRESENTI+"' toolText='"+CostantiGrafici.DATI_NON_PRESENTI+"' />");
			sb.append( " </dataset>");
		}

		sb.append("</chart> ");

		return sb.toString();
	}




	/* ***************** GENERIC UTILS ********************** */

	public static String getSubCaption(StatsSearchForm form){
		return getSubCaption(form, false);
	}
	public static String getSubCaption(StatsSearchForm form,boolean showUnitaMisura){
		TipoVisualizzazione tipoVisualizzazione = form.getTipoVisualizzazione();

		switch (tipoVisualizzazione) {

		case NUMERO_TRANSAZIONI:
			return CostantiGrafici.NUMERO_TRANSAZIONI_LABEL + CostantiGrafici.WHITE_SPACE;
		case TEMPO_MEDIO_RISPOSTA:
			String tipoLatenzaLabel = getTipoLatenzaServizioLabel(form);
			if(showUnitaMisura){
				return CostantiGrafici.TEMPO_MEDIO_RISPOSTA_LABEL+tipoLatenzaLabel+CostantiGrafici.WHITE_SPACE + CostantiGrafici.MS_LABEL_CON_QUADRE;
			}
			else{
				return CostantiGrafici.TEMPO_MEDIO_RISPOSTA_LABEL+tipoLatenzaLabel+CostantiGrafici.WHITE_SPACE;//[ms] ";
			}
		case DIMENSIONE_TRANSAZIONI:
		default:
			String tipoBandaLabel = getTipoBandaLabel(form);
			if(showUnitaMisura){
				return CostantiGrafici.OCCUPAZIONE_BANDA_LABEL+tipoBandaLabel+CostantiGrafici.WHITE_SPACE + CostantiGrafici.KB_LABEL_CON_QUADRE;
			}else{
				return CostantiGrafici.OCCUPAZIONE_BANDA_LABEL+tipoBandaLabel+CostantiGrafici.WHITE_SPACE;//[kb] ";
			}
		}
	}

	private static String getTipoLatenzaServizioLabel(StatsSearchForm form){
		String tipoLatenzaLabel = "";
		TipoLatenza tipoLatenza = null;
		if(TipoStatistica.ANDAMENTO_TEMPORALE.equals(form.getTipoStatistica()) &&
				!form.isAndamentoTemporalePerEsiti()){
			if(form.getTipiLatenzaImpostati()!=null && form.getTipiLatenzaImpostati().size()>0){
				if(form.getTipiLatenzaImpostati().size()==1){
					tipoLatenza = form.getTipiLatenzaImpostati().get(0);
				}
			}
		}
		else{
			tipoLatenza = form.getTipoLatenza();
		}
		if(tipoLatenza!=null){
			switch (tipoLatenza) {
			case LATENZA_PORTA:
				tipoLatenzaLabel = CostantiGrafici.WHITE_SPACE + CostantiGrafici.PORTA_LABEL;
				break;
			case LATENZA_SERVIZIO:
				// TODO Parlare con Poli
				tipoLatenzaLabel = CostantiGrafici.WHITE_SPACE + "Servizio";
				break;
			case LATENZA_TOTALE:
				tipoLatenzaLabel = CostantiGrafici.WHITE_SPACE + CostantiGrafici.TOTALE_LABEL;
				break;
			}
		}
		return tipoLatenzaLabel;
	}
	
	private static String getTipoBandaLabel(StatsSearchForm form){
		String tipoBandaLabel = "";
		TipoBanda tipoBanda = null;
		if(TipoStatistica.ANDAMENTO_TEMPORALE.equals(form.getTipoStatistica()) &&
				!form.isAndamentoTemporalePerEsiti()){
			if(form.getTipiBandaImpostati()!=null && form.getTipiBandaImpostati().size()>0){
				if(form.getTipiBandaImpostati().size()==1){
					tipoBanda = form.getTipiBandaImpostati().get(0);
				}
			}
		}
		else{
			tipoBanda = form.getTipoBanda();
		}
		if(tipoBanda!=null){
			switch (tipoBanda) {
			case COMPLESSIVA:
				tipoBandaLabel = CostantiGrafici.WHITE_SPACE + CostantiGrafici.COMPLESSIVA_LABEL;
				break;
			case ESTERNA:
				tipoBandaLabel = CostantiGrafici.WHITE_SPACE + CostantiGrafici.ESTERNA_LABEL;
				break;
			case INTERNA:
				tipoBandaLabel = CostantiGrafici.WHITE_SPACE + CostantiGrafici.INTERNA_LABEL;
				break;
			}
		}
		return tipoBandaLabel;
	}
	
	public static String getValue(StatsSearchForm form,Number value){
		TipoVisualizzazione tipoVisualizzazione = form.getTipoVisualizzazione();

		switch (tipoVisualizzazione) {

		case NUMERO_TRANSAZIONI:
			return "" + value;
		case TEMPO_MEDIO_RISPOSTA:
			return  "" + value;
		case DIMENSIONE_TRANSAZIONI:
		default:
			double d = value.doubleValue();
			double dRes = d/1024; 
			return dRes + "";
		}

	}

	public static String getToolText(StatsSearchForm form,Number value){
		TipoVisualizzazione tipoVisualizzazione = form.getTipoVisualizzazione();

		switch (tipoVisualizzazione) {

		case NUMERO_TRANSAZIONI:
			return Utility.numberConverter(value);
		case TEMPO_MEDIO_RISPOSTA:
			return  Utilities.convertSystemTimeIntoString_millisecondi(value.longValue(), true);
		case DIMENSIONE_TRANSAZIONI:
		default:
			return Utility.fileSizeConverter(value);
		}

	}

	public static String getToolTextConParent(StatsSearchForm form,String risultato,Map<String, String> parent,Number value){
		TipoVisualizzazione tipoVisualizzazione = form.getTipoVisualizzazione();

		StringBuilder sb = new StringBuilder();
		String valore = null;
		String labelValore = null;
		switch (tipoVisualizzazione) {
		case NUMERO_TRANSAZIONI:
			valore =  Utility.numberConverter(value); 
			labelValore = CostantiGrafici.NUMERO_TRANSAZIONI_LABEL;
			break;
		case TEMPO_MEDIO_RISPOSTA:
			valore = Utilities.convertSystemTimeIntoString_millisecondi(value.longValue(), true);
			String tipoLatenzaLabel = getTipoLatenzaServizioLabel(form);
			labelValore = CostantiGrafici.TEMPO_MEDIO_RISPOSTA_LABEL+tipoLatenzaLabel;
			break;
		case DIMENSIONE_TRANSAZIONI:
		default:
			valore = Utility.fileSizeConverter(value);
			String tipoBandaLabel = getTipoBandaLabel(form);
			labelValore = CostantiGrafici.OCCUPAZIONE_BANDA_LABEL+tipoBandaLabel;
			break;
		}

		TipoStatistica tipoStatistica = form.getTipoStatistica();

		
		
		switch (tipoStatistica) {
		case DISTRIBUZIONE_SERVIZIO:
			if(form.isUseGraficiSVG()) {
				String labelTooltipDistribuzioneServizioSvgPattern = MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_TOOLTIP_SVG_PATTERN_KEY);
				sb.append(MessageFormat.format(labelTooltipDistribuzioneServizioSvgPattern, labelValore,valore,risultato,parent.get("0")));
			} else {
				String tooltipSectionErogatore =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_EROGATORE_PATTERN_KEY);
				String tooltipSectionServizio = MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_SERVIZIO_PATTERN_KEY);
				sb.append(labelValore).append(": ").append(valore).append(tooltipSectionServizio).append(risultato).append(tooltipSectionErogatore).append(parent.get("0"));
			}
			break;
		case DISTRIBUZIONE_AZIONE:
			if(form.isUseGraficiSVG()) {
				String labelTooltipDistribuzioneAzioneSvgPattern = MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_AZIONE_TOOLTIP_SVG_PATTERN_KEY);
				sb.append(MessageFormat.format(labelTooltipDistribuzioneAzioneSvgPattern, labelValore,valore,risultato,parent.get("0"),parent.get("1")));
			} else {
				String tooltipSectionErogatore =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_EROGATORE_PATTERN_KEY);
				String tooltipSectionServizio = MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_SERVIZIO_PATTERN_KEY);
				String tooltipSectionAzione = MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_AZIONE_PATTERN_KEY);
				sb.append(labelValore).append(": ").append(valore).append(tooltipSectionAzione).append(risultato).append(tooltipSectionServizio).append(parent.get("0")).append(tooltipSectionErogatore).append(parent.get("1"));
			}
			
			break;
		case DISTRIBUZIONE_SERVIZIO_APPLICATIVO:
			if(form.isUseGraficiSVG()) {
				if(StringUtils.isNotEmpty(form.getRiconoscimento())) {
					if(form.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
						String labelTooltipDistribuzioneSaSvgPattern =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO_TOOLTIP_SVG_PATTERN_KEY);
						sb.append(MessageFormat.format(labelTooltipDistribuzioneSaSvgPattern, labelValore,valore,risultato,parent.get("0")));
					} else if(form.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO)) {
						String labelTooltipDistribuzioneIdentificativoAutenticatoSvgPattern =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_IDENTIFICATIVO_AUTENTICATO_TOOLTIP_SVG_PATTERN_KEY);
						sb.append(MessageFormat.format(labelTooltipDistribuzioneIdentificativoAutenticatoSvgPattern, labelValore,valore,risultato));
					} else if(form.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP)) {
						String labelTooltipDistribuzioneIndirizzoIPSvgPattern =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_INDIRIZZO_IP_TOOLTIP_SVG_PATTERN_KEY);
						sb.append(MessageFormat.format(labelTooltipDistribuzioneIndirizzoIPSvgPattern, labelValore,valore,risultato));
					} else { // token
						if (StringUtils.isNotEmpty(form.getTokenClaim())) {
							org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.valueOf(form.getTokenClaim());
							String labelTooltipDistribuzioneTokenInfoSvgPattern = null;
							switch (tcm) {
							case token_clientId:
								labelTooltipDistribuzioneTokenInfoSvgPattern =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKENINFO_CLIENTID_TOOLTIP_SVG_PATTERN_KEY);
								break;
							case token_eMail:
								labelTooltipDistribuzioneTokenInfoSvgPattern =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKENINFO_EMAIL_TOOLTIP_SVG_PATTERN_KEY);
								break;
							case token_issuer:
								labelTooltipDistribuzioneTokenInfoSvgPattern =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKENINFO_ISSUER_TOOLTIP_SVG_PATTERN_KEY);
								break;
							case token_subject:
								labelTooltipDistribuzioneTokenInfoSvgPattern =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKENINFO_SUBJECT_TOOLTIP_SVG_PATTERN_KEY);
								break;
							case token_username:
								labelTooltipDistribuzioneTokenInfoSvgPattern =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKENINFO_USERNAME_TOOLTIP_SVG_PATTERN_KEY);
								break;
							case trasporto:
							default:
								// caso impossibile
								break; 
							}
							
							sb.append(MessageFormat.format(labelTooltipDistribuzioneTokenInfoSvgPattern, labelValore,valore,risultato));
						} 
					}
				}
			} else {
				if(StringUtils.isNotEmpty(form.getRiconoscimento())) {
					if(form.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
						String tooltipSectionApplicativo =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_APPLICATIVO_PATTERN_KEY);
						String tooltipSectionSoggetto = MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_SOGGETTO_PATTERN_KEY);
						sb.append(labelValore).append(": ").append(valore).append(tooltipSectionApplicativo).append(risultato).append(tooltipSectionSoggetto).append(parent.get("0"));
					}  else if(form.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO)) {
						String tooltipSectionIdentificativoAutenticato =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_IDENTIFICATIVO_AUTENTICATO_PATTERN_KEY);
						sb.append(labelValore).append(": ").append(valore).append(tooltipSectionIdentificativoAutenticato).append(risultato);
					} else if(form.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP)) {
						String tooltipSectionIndirizzoIP =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_INDIRIZZO_IP_PATTERN_KEY);
						sb.append(labelValore).append(": ").append(valore).append(tooltipSectionIndirizzoIP).append(risultato);
					} else { // token
						if (StringUtils.isNotEmpty(form.getTokenClaim())) {
							org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.valueOf(form.getTokenClaim());
							String tooltipSectionTokenInfo = null;
							
							switch (tcm) {
							case token_clientId:
								tooltipSectionTokenInfo =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_TOKENINFO_CLIENTID_PATTERN_KEY);
								break;
							case token_eMail:
								tooltipSectionTokenInfo =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_TOKENINFO_EMAIL_PATTERN_KEY);
								break;
							case token_issuer:
								tooltipSectionTokenInfo =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_TOKENINFO_ISSUER_PATTERN_KEY);
								break;
							case token_subject:
								tooltipSectionTokenInfo =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_TOKENINFO_SUBJECT_PATTERN_KEY);
								break;
							case token_username:
								tooltipSectionTokenInfo =  MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_TOKENINFO_USERNAME_PATTERN_KEY);
								break;
							case trasporto:
							default:
								// caso impossibile
								break; 
							}
							
							sb.append(labelValore).append(": ").append(valore).append(tooltipSectionTokenInfo).append(risultato);
						} 
					}
				}
			}
			
			break;
		case DISTRIBUZIONE_SOGGETTO:
		case STATISTICA_PERSONALIZZATA:
		case ANDAMENTO_TEMPORALE:
		default:
			// default restituisco il valore;
			sb.append(valore);
			break;
		}

		return sb.toString();
	}

	public static String sommaColumnHeader(StatsSearchForm form,String suffix){
		TipoVisualizzazione tipoVisualizzazione = form.getTipoVisualizzazione();
		switch (tipoVisualizzazione) {
		case NUMERO_TRANSAZIONI:
			return CostantiGrafici.TOTALE_LABEL + CostantiGrafici.WHITE_SPACE + suffix;
		case TEMPO_MEDIO_RISPOSTA:
			return  CostantiGrafici.TEMPO_MEDIO_RISPOSTA_LABEL;
		case DIMENSIONE_TRANSAZIONI:
		default:
			return CostantiGrafici.DIMENSIONE_LABEL + CostantiGrafici.WHITE_SPACE + suffix;
		}

	}

	public static Number converToNumber(Object o){
		if(o!=null && (o instanceof Number) ){
			return (Number) o;
		}
		return null;
	}

}
