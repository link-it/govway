/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.export;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoStatistica;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.web.monitor.core.bean.ApplicationBean;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.converter.DurataConverter;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.datamodel.Res;
import org.openspcoop2.web.monitor.core.datamodel.ResDistribuzione;
import org.openspcoop2.web.monitor.core.report.Templates;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.slf4j.Logger;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperCsvExporterBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperPdfExporterBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperXlsExporterBuilder;
import net.sf.dynamicreports.jasper.constant.JasperProperty;
import net.sf.dynamicreports.report.builder.column.ColumnBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * ExportUtils
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ExportUtils {


	public static JasperReportBuilder creaReportAndamentoTemporale(List<Res> list,String titoloReport,Logger log,TipoVisualizzazione tipoVisualizzazione,
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza, StatisticType modalitaTemporale, boolean distribuzionePerEsiti, boolean convertRawData) throws Exception{
		JRDataSource dataSource = getDatasourceAndamentoTemporale(	list, log, tipoVisualizzazione, tipiBanda, tipiLatenza, modalitaTemporale,distribuzionePerEsiti, convertRawData);

		JasperReportBuilder builder = report();
		builder.setDataSource(dataSource);
		return builder;
	}

	public static JasperReportBuilder creaReportDistribuzione(List<ResDistribuzione> list,String titoloReport, Logger log,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza, TipoStatistica tipoStatistica, boolean convertRawData) throws Exception{
		return creaReportDistribuzione(list, titoloReport, log, tipoVisualizzazione, tipiBanda, tipiLatenza, tipoStatistica, null, convertRawData);
	}

	public static JasperReportBuilder creaReportDistribuzione(List<ResDistribuzione> list,String titoloReport, Logger log,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza, TipoStatistica tipoStatistica, String tipoRiconoscimento, boolean convertRawData) throws Exception{
		JRDataSource dataSource = getDatasourceDistribuzione(list, log, tipoVisualizzazione, tipiBanda, tipiLatenza, tipoStatistica, tipoRiconoscimento, convertRawData);

		JasperReportBuilder builder = report();
		builder.setDataSource(dataSource);
		return builder;
	}

	public static JasperReportBuilder creaReportAndamentoTemporalePersonalizzato(Map<String, List<Res>> results,String titoloReport,Logger log,TipoVisualizzazione tipoVisualizzazione,
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza, StatisticType modalitaTemporale, boolean convertRawData) throws Exception{
		JRDataSource dataSource = getDatasourceAndamentoTemporalePersonalizzato(results, log, tipoVisualizzazione, tipiBanda, tipiLatenza, modalitaTemporale, convertRawData);

		JasperReportBuilder builder = report();
		builder.setDataSource(dataSource);
		return builder;
	}

	public static void esportaCsv(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica) throws Exception{
		esportaCsv(outputStream, report, titoloReport, headerLabel, tipoVisualizzazione, tipiBanda, tipiLatenza, tipoStatistica, null); 
	}

	public static void esportaCsv(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica,String tipoRiconoscimento) throws Exception{
		esportaCsv(outputStream, report, titoloReport, headerLabel, tipoVisualizzazione, tipiBanda,tipiLatenza, tipoStatistica, tipoRiconoscimento, false);
	}
	public static void esportaCsv(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica, String tipoRiconoscimento, boolean distribuzionePerEsiti) throws Exception{
		String headerValueLabel = "";
		String headerValueCategory = "";

		List<ColumnBuilder<?,?>> colonne = new ArrayList<ColumnBuilder<?,?>>();
		TextColumnBuilder<String> nomeColumn = col.column(headerLabel, "nome", type.stringType());
		colonne.add(nomeColumn);

		if(distribuzionePerEsiti){

			headerValueCategory = "ok";
			headerValueLabel = "Ok";
			colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));

			headerValueCategory = "faultApplicativo";
			headerValueLabel = "Fault Applicativo";
			colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));

			headerValueCategory = "errore";
			headerValueLabel = "Fallite";
			colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));

		}
		else{
			switch (tipoStatistica) {
			case DISTRIBUZIONE_AZIONE:
				headerValueCategory = "parent_0";
				headerValueLabel =  MessageManager.getInstance().getMessage(Costanti.API_LABEL_KEY);
				colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
				
				headerValueCategory = "parent_1";
				headerValueLabel = MessageManager.getInstance().getMessage(Costanti.EROGATORE_LABEL_KEY);
				colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
				break;
			case DISTRIBUZIONE_SERVIZIO:
				headerValueCategory = "parent_0";
				headerValueLabel = MessageManager.getInstance().getMessage(Costanti.EROGATORE_LABEL_KEY);
				colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
				break;
			case DISTRIBUZIONE_SERVIZIO_APPLICATIVO:
				if(tipoRiconoscimento != null && tipoRiconoscimento.equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
					headerValueCategory = "parent_0";
					headerValueLabel = MessageManager.getInstance().getMessage(Costanti.SOGGETTO_LABEL_KEY);
					colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
				}
				break;
			case ANDAMENTO_TEMPORALE:
			case DISTRIBUZIONE_SOGGETTO:
			case STATISTICA_PERSONALIZZATA:
			default:
				break;
			}
			
			
			switch (tipoVisualizzazione) {
			case DIMENSIONE_TRANSAZIONI:
				for (TipoBanda tipoBanda : tipiBanda) {
					switch (tipoBanda) {
					case COMPLESSIVA:
						headerValueCategory = "occupazioneBandaComplessiva";
						headerValueLabel = "Occupazione Banda Complessiva [bytes]";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
						break;
					case INTERNA:
						headerValueCategory = "occupazioneBandaInterna";
						headerValueLabel = "Occupazione Banda Interna [bytes]";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
						break;
					case ESTERNA:
					default:
						headerValueCategory = "occupazioneBandaEsterna";
						headerValueLabel = "Occupazione Banda Esterna [bytes]";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
						break;
					}
				}
				break;

			case NUMERO_TRANSAZIONI:
				headerValueCategory = "numeroRichieste";
				headerValueLabel = "Numero Transazioni";
				colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
				break;

			case TEMPO_MEDIO_RISPOSTA:
				for (TipoLatenza tipoLatenza : tipiLatenza) {
					switch (tipoLatenza) {
					case LATENZA_PORTA:
						headerValueCategory = "latenzaMediaPorta";
						headerValueLabel = "Latenza Media Porta [ms]";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
						break;
					case LATENZA_SERVIZIO:
						headerValueCategory = "latenzaMediaServizio";
						headerValueLabel = "Latenza Media Servizio [ms]";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
						break;

					case LATENZA_TOTALE:
					default:
						headerValueCategory = "latenzaMediaTotale";
						headerValueLabel = "Latenza Media Totale [ms]";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
						break;
					}
				}
				break;
			}
		}

		report
		.setColumnTitleStyle(Templates.columnTitleStyle)
		.addProperty("net.sf.jasperreports.export.csv.exclude.origin.keep.first.band.1", "columnHeader")
		.ignorePageWidth()
		.ignorePagination()
		.columns(colonne.toArray(new ColumnBuilder[colonne.size()]));


		JasperCsvExporterBuilder builder =export.csvExporter(outputStream);
		report.toCsv(builder); 
	}

	public static void esportaPdf(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica) throws Exception{
		esportaPdf(outputStream, report, titoloReport, headerLabel, tipoVisualizzazione, tipiBanda, tipiLatenza, tipoStatistica, null);
	}

	public static void esportaPdf(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica,String tipoRiconoscimento) throws Exception{
		esportaPdf(outputStream, report, titoloReport, headerLabel, tipoVisualizzazione, tipiBanda, tipiLatenza, tipoStatistica, tipoRiconoscimento, false);
	}
	public static void esportaPdf(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica, String tipoRiconoscimento,  boolean distribuzionePerEsiti) throws Exception{
		JasperPdfExporterBuilder builder = export.pdfExporter(outputStream);

		String headerValueLabel = "";
		String headerValueCategory = "";

		List<ColumnBuilder<?,?>> colonne = new ArrayList<ColumnBuilder<?,?>>();
		TextColumnBuilder<String> nomeColumn = col.column(headerLabel, "nome", type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		colonne.add(nomeColumn);

		if(distribuzionePerEsiti){

			headerValueCategory = "ok";
			headerValueLabel = "Ok";
			colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));

			headerValueCategory = "faultApplicativo";
			headerValueLabel = "Fault Applicativo";
			colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));

			headerValueCategory = "errore";
			headerValueLabel = "Fallite";
			colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));

		}
		else{
			switch (tipoStatistica) {
			case DISTRIBUZIONE_AZIONE:
				headerValueCategory = "parent_0";
				headerValueLabel =  MessageManager.getInstance().getMessage(Costanti.API_LABEL_KEY);
				colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));
				
				headerValueCategory = "parent_1";
				headerValueLabel = MessageManager.getInstance().getMessage(Costanti.EROGATORE_LABEL_KEY);
				colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));
				break;
			case DISTRIBUZIONE_SERVIZIO:
				
				headerValueCategory = "parent_0";
				headerValueLabel = MessageManager.getInstance().getMessage(Costanti.EROGATORE_LABEL_KEY);
				colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));
				break;
			case DISTRIBUZIONE_SERVIZIO_APPLICATIVO:
				if(tipoRiconoscimento != null && tipoRiconoscimento.equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
					headerValueCategory = "parent_0";
					headerValueLabel = MessageManager.getInstance().getMessage(Costanti.SOGGETTO_LABEL_KEY);
					colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));
				}
				break;
			case ANDAMENTO_TEMPORALE:
			case DISTRIBUZIONE_SOGGETTO:
			case STATISTICA_PERSONALIZZATA:
			default:
				break;
			}
			
			switch (tipoVisualizzazione) {
			case DIMENSIONE_TRANSAZIONI:				
				for (TipoBanda tipoBanda : tipiBanda) {
					switch (tipoBanda) {
					case COMPLESSIVA:
						headerValueCategory = "occupazioneBandaComplessiva";
						headerValueLabel = "Occupazione Banda Complessiva";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));
						break;
					case INTERNA:
						headerValueCategory = "occupazioneBandaInterna";
						headerValueLabel = "Occupazione Banda Interna";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));
						break;
					case ESTERNA:
					default:
						headerValueCategory = "occupazioneBandaEsterna";
						headerValueLabel = "Occupazione Banda Esterna";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));
						break;
					}
				}
				break;

			case NUMERO_TRANSAZIONI:
				headerValueCategory = "numeroRichieste";
				headerValueLabel = "Numero Transazioni";
				colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));
				break;

			case TEMPO_MEDIO_RISPOSTA:
				for (TipoLatenza tipoLatenza : tipiLatenza) {
					switch (tipoLatenza) {
					case LATENZA_PORTA:
						headerValueCategory = "latenzaMediaPorta";
						headerValueLabel = "Latenza Media Porta";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));
						break;
					case LATENZA_SERVIZIO:
						headerValueCategory = "latenzaMediaServizio";
						headerValueLabel = "Latenza Media Servizio";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));
						break;

					case LATENZA_TOTALE:
					default:
						headerValueCategory = "latenzaMediaTotale";
						headerValueLabel = "Latenza Media Totale";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));
						break;
					}
				}
				break;

			}
		}

		report
		.title(Templates.createTitleComponent(titoloReport,""))
		.setTemplate(Templates.reportTemplate)
		.columns(colonne.toArray(new ColumnBuilder[colonne.size()]));

		report.toPdf(builder);
	}

	public static void esportaXls(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica) throws Exception{
		esportaXls(outputStream, report, titoloReport, headerLabel, tipoVisualizzazione, tipiBanda, tipiLatenza, tipoStatistica, null);
	}

	public static void esportaXls(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica,String tipoRiconoscimento) throws Exception{
		esportaXls(outputStream, report, titoloReport, headerLabel, tipoVisualizzazione, tipiBanda, tipiLatenza, tipoStatistica, tipoRiconoscimento, false);
	}
	public static void esportaXls(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica,String tipoRiconoscimento, boolean distribuzionePerEsiti) throws Exception{
		JasperXlsExporterBuilder builder = export.xlsExporter(outputStream).setDetectCellType(true).setIgnorePageMargins(true)
				.setWhitePageBackground(false)
				.setRemoveEmptySpaceBetweenColumns(true);

		String headerValueLabel = "";
		String headerValueCategory = "";

		List<ColumnBuilder<?,?>> colonne = new ArrayList<ColumnBuilder<?,?>>();
		TextColumnBuilder<String> nomeColumn = col.column(headerLabel, "nome", type.stringType()).setStretchWithOverflow(false)
				.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true");
		colonne.add(nomeColumn);

		if(distribuzionePerEsiti){

			headerValueCategory = "ok";
			headerValueLabel = "Ok";
			colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setStretchWithOverflow(false)
					.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true"));

			headerValueCategory = "faultApplicativo";
			headerValueLabel = "Fault Applicativo";
			colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setStretchWithOverflow(false)
					.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true"));

			headerValueCategory = "errore";
			headerValueLabel = "Fallite";
			colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setStretchWithOverflow(false)
					.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true"));

		}
		else{
			switch (tipoStatistica) {
			case DISTRIBUZIONE_AZIONE:
				headerValueCategory = "parent_0";
				headerValueLabel =  MessageManager.getInstance().getMessage(Costanti.API_LABEL_KEY);
				colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setStretchWithOverflow(false)
						.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true"));
				
				headerValueCategory = "parent_1";
				headerValueLabel = MessageManager.getInstance().getMessage(Costanti.EROGATORE_LABEL_KEY);
				colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setStretchWithOverflow(false)
						.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true"));
				break;
			case DISTRIBUZIONE_SERVIZIO:
				
				headerValueCategory = "parent_0";
				headerValueLabel = MessageManager.getInstance().getMessage(Costanti.EROGATORE_LABEL_KEY);
				colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setStretchWithOverflow(false)
						.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true"));
				break;
			case DISTRIBUZIONE_SERVIZIO_APPLICATIVO:
				if(tipoRiconoscimento != null && tipoRiconoscimento.equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
					headerValueCategory = "parent_0";
					headerValueLabel = MessageManager.getInstance().getMessage(Costanti.SOGGETTO_LABEL_KEY);
					colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setStretchWithOverflow(false)
							.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true"));
				}
				break;
			case ANDAMENTO_TEMPORALE:
			case DISTRIBUZIONE_SOGGETTO:
			case STATISTICA_PERSONALIZZATA:
			default:
				break;
			}
			
			switch (tipoVisualizzazione) {
			case DIMENSIONE_TRANSAZIONI:				
				for (TipoBanda tipoBanda : tipiBanda) {
					switch (tipoBanda) {
					case COMPLESSIVA:
						headerValueCategory = "occupazioneBandaComplessiva";
						headerValueLabel = "Occupazione Banda Complessiva [bytes]";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setStretchWithOverflow(false)
								.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true"));
						break;
					case INTERNA:
						headerValueCategory = "occupazioneBandaInterna";
						headerValueLabel = "Occupazione Banda Interna [bytes]";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setStretchWithOverflow(false)
								.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true"));
						break;
					case ESTERNA:
					default:
						headerValueCategory = "occupazioneBandaEsterna";
						headerValueLabel = "Occupazione Banda Esterna [bytes]";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setStretchWithOverflow(false)
								.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true"));
						break;
					}
				}
				break;

			case NUMERO_TRANSAZIONI:
				headerValueCategory = "numeroRichieste";
				headerValueLabel = "Numero Transazioni";
				colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setStretchWithOverflow(false)
						.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true"));
				break;

			case TEMPO_MEDIO_RISPOSTA:
				for (TipoLatenza tipoLatenza : tipiLatenza) {
					switch (tipoLatenza) {
					case LATENZA_PORTA:
						headerValueCategory = "latenzaMediaPorta";
						headerValueLabel = "Latenza Media Porta [ms]";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setStretchWithOverflow(false)
								.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true"));
						break;
					case LATENZA_SERVIZIO:
						headerValueCategory = "latenzaMediaServizio";
						headerValueLabel = "Latenza Media Servizio [ms]";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setStretchWithOverflow(false)
								.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true"));
						break;

					case LATENZA_TOTALE:
					default:
						headerValueCategory = "latenzaMediaTotale";
						headerValueLabel = "Latenza Media Totale [ms]";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()).setStretchWithOverflow(false)
								.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true"));
						break;
					}
				}
				break;
			}

		}

		report
		.setColumnTitleStyle(Templates.columnTitleStyle)
		.addProperty("net.sf.jasperreports.export.csv.exclude.origin.keep.first.band.1", "columnHeader")
		//		.addProperty(JasperProperty.EXPORT_XLS_FREEZE_ROW, "2")
		.ignorePageWidth()
		.ignorePagination()
		.columns(colonne.toArray(new ColumnBuilder[colonne.size()]));


		report.toXls(builder);

	}



	private static JRDataSource getDatasourceAndamentoTemporale (List<Res> list, Logger log,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza, 
			StatisticType modalitaTemporale,boolean distribuzionePerEsiti, boolean convertRawData) throws Exception {
		// Scittura Intestazione
		List<String> header = new ArrayList<String>();

		header.add("nome");

		if(distribuzionePerEsiti){
			header.add("ok");
			header.add("faultApplicativo");
			header.add("errore");
		}

		boolean isLatenzaTotale = false;	
		boolean isLatenzaServizio = false;
		boolean isLatenzaPorta = false;
		boolean isNumeroRichieste = false;
		boolean isDimensioneComplessiva = false;
		boolean isDimensioneInterna = false;
		boolean isDimensioneEsterna = false;
		switch (tipoVisualizzazione) {
		case DIMENSIONE_TRANSAZIONI:
			for (TipoBanda tipoBanda : tipiBanda) {
				switch (tipoBanda) {
				case COMPLESSIVA:
					isDimensioneComplessiva = true;
					if(!distribuzionePerEsiti)
						header.add("occupazioneBandaComplessiva");
					break;
				case INTERNA:
					isDimensioneInterna = true;
					if(!distribuzionePerEsiti)
						header.add("occupazioneBandaInterna");
					break;
				case ESTERNA:
				default:
					isDimensioneEsterna = true;
					if(!distribuzionePerEsiti)
						header.add("occupazioneBandaEsterna");
					break;
				}
			}
			break;

		case NUMERO_TRANSAZIONI:
			isNumeroRichieste = true;
			if(!distribuzionePerEsiti)
				header.add("numeroRichieste");
			break;

		case TEMPO_MEDIO_RISPOSTA:
			for (TipoLatenza tipoLatenza : tipiLatenza) {
				switch (tipoLatenza) {
				case LATENZA_PORTA:
					isLatenzaPorta = true;
					if(!distribuzionePerEsiti)
						header.add("latenzaMediaPorta");
					break;
				case LATENZA_SERVIZIO:
					isLatenzaServizio = true;
					if(!distribuzionePerEsiti)
						header.add("latenzaMediaServizio");
					break;

				case LATENZA_TOTALE:
				default:
					isLatenzaTotale = true;
					if(!distribuzionePerEsiti)
						header.add("latenzaMediaTotale");
					break;
				}
			}
			break;
		}

		SimpleDateFormat sdf = null;
		SimpleDateFormat sdf_last_hour = null;
		switch (modalitaTemporale) {
		case MENSILE:
			sdf = new SimpleDateFormat("MMM yyyy", ApplicationBean.getInstance().getLocale());
			break;
		case SETTIMANALE:
			sdf = new SimpleDateFormat("yyyy/MM/dd", ApplicationBean.getInstance().getLocale());
			sdf_last_hour = new SimpleDateFormat("yyyy/MM/dd", ApplicationBean.getInstance().getLocale());
			break;

		case ORARIA: 
			sdf = new SimpleDateFormat("yyyy/MM/dd HH", ApplicationBean.getInstance().getLocale());
			sdf_last_hour = new SimpleDateFormat("HH", ApplicationBean.getInstance().getLocale());
			break;

		case GIORNALIERA:
		default:
			sdf = new SimpleDateFormat("yyyy/MM/dd", ApplicationBean.getInstance().getLocale());
			break;
		}

		DRDataSource dataSource = new DRDataSource(header.toArray(new String[header.size()])); 

		for (int idx = 0 ; idx < list.size() ; idx ++){
			Res risultato = list.get(idx);

			List<Object> oneLine = new ArrayList<Object>();

			String label = "";
			Calendar c =  null;
			switch (modalitaTemporale) {
			case SETTIMANALE:
				c = Calendar.getInstance();
				c.setTime((Date)risultato.getRisultato());
				c.add(Calendar.WEEK_OF_MONTH, 1);
				c.add(Calendar.DAY_OF_WEEK, -1);
				label = sdf.format(risultato.getRisultato())+"-"+sdf_last_hour.format(c.getTime());
				break;

			case ORARIA: 
				sdf = new SimpleDateFormat("yyyy/MM/dd HH", ApplicationBean.getInstance().getLocale());
				sdf_last_hour = new SimpleDateFormat("HH", ApplicationBean.getInstance().getLocale());

				c = Calendar.getInstance();
				c.setTime((Date)risultato.getRisultato());
				c.add(Calendar.HOUR, +1);
				label = sdf.format(risultato.getRisultato())+"-"+sdf_last_hour.format(c.getTime());
				break;

			case MENSILE:
			case GIORNALIERA:
			default:
				label = sdf.format(risultato.getRisultato());
				break;
			}

			// Servizio
			//			String label = risultato.getRisultato() != null ? sdf.format(risultato.getRisultato()) : "";
			oneLine.add(label);

			if(isNumeroRichieste){
				if(distribuzionePerEsiti){
					if(convertRawData){
						oneLine.add( Utility.numberConverter( risultato.getObjectsMap().get("0").longValue()) );
						oneLine.add( Utility.numberConverter( risultato.getObjectsMap().get("1").longValue()) );
						oneLine.add( Utility.numberConverter( risultato.getObjectsMap().get("2").longValue()) );
					}
					else{
						oneLine.add( risultato.getObjectsMap().get("0").longValue()+"");
						oneLine.add( risultato.getObjectsMap().get("1").longValue()+"");
						oneLine.add( risultato.getObjectsMap().get("2").longValue()+"");
					}
				}
				else{
					if(convertRawData){
						oneLine.add( Utility.numberConverter( risultato.getSomma()) );
					}
					else{
						oneLine.add(risultato.getSomma()+"");
					}
				}
			}

			int indexDimensione = 0;

			if(isDimensioneComplessiva){
				if(distribuzionePerEsiti){
					if(convertRawData){
						oneLine.add( Utility.fileSizeConverter(risultato.getObjectsMap().get("0").longValue()));
						oneLine.add( Utility.fileSizeConverter(risultato.getObjectsMap().get("1").longValue()));
						oneLine.add( Utility.fileSizeConverter(risultato.getObjectsMap().get("2").longValue()));
					}
					else{
						oneLine.add( risultato.getObjectsMap().get("0").longValue()+"" );
						oneLine.add( risultato.getObjectsMap().get("1").longValue()+"" );
						oneLine.add( risultato.getObjectsMap().get("2").longValue()+"" );
					}
				}
				else{
					if(convertRawData){
						oneLine.add(Utility.fileSizeConverter(risultato.getObjectsMap().get(indexDimensione+"").longValue()));
					}
					else{
						oneLine.add(risultato.getObjectsMap().get(indexDimensione+"").longValue()+"");
					}
				}
				indexDimensione++;
			}

			if(isDimensioneInterna){
				if(distribuzionePerEsiti){
					if(convertRawData){
						oneLine.add( Utility.fileSizeConverter(risultato.getObjectsMap().get("0").longValue()));
						oneLine.add( Utility.fileSizeConverter(risultato.getObjectsMap().get("1").longValue()));
						oneLine.add( Utility.fileSizeConverter(risultato.getObjectsMap().get("2").longValue()));
					}
					else{
						oneLine.add( risultato.getObjectsMap().get("0").longValue()+"" );
						oneLine.add( risultato.getObjectsMap().get("1").longValue()+"" );
						oneLine.add( risultato.getObjectsMap().get("2").longValue()+"" );
					}
				}
				else{
					if(convertRawData){
						oneLine.add(Utility.fileSizeConverter(risultato.getObjectsMap().get(indexDimensione+"").longValue()));
					}
					else{
						oneLine.add(risultato.getObjectsMap().get(indexDimensione+"").longValue()+"");
					}
				}
				indexDimensione++;
			}

			if(isDimensioneEsterna){
				if(distribuzionePerEsiti){
					if(convertRawData){
						oneLine.add( Utility.fileSizeConverter(risultato.getObjectsMap().get("0").longValue()));
						oneLine.add( Utility.fileSizeConverter(risultato.getObjectsMap().get("1").longValue()));
						oneLine.add( Utility.fileSizeConverter(risultato.getObjectsMap().get("2").longValue()));
					}
					else{
						oneLine.add( risultato.getObjectsMap().get("0").longValue()+"" );
						oneLine.add( risultato.getObjectsMap().get("1").longValue()+"" );
						oneLine.add( risultato.getObjectsMap().get("2").longValue()+"" );
					}
				}
				else{
					if(convertRawData){
						oneLine.add(Utility.fileSizeConverter(risultato.getObjectsMap().get(indexDimensione+"").longValue()));
					}
					else{
						oneLine.add(risultato.getObjectsMap().get(indexDimensione+"").longValue()+"");
					}
				}
				indexDimensione++;
			}

			int indexLatenza = 0;

			if(isLatenzaTotale){
				if(distribuzionePerEsiti){
					if(convertRawData){
						oneLine.add( DurataConverter.convertSystemTimeIntoString_millisecondi( risultato.getObjectsMap().get("0").longValue(), true));
						oneLine.add( DurataConverter.convertSystemTimeIntoString_millisecondi( risultato.getObjectsMap().get("1").longValue(), true));
						oneLine.add( DurataConverter.convertSystemTimeIntoString_millisecondi( risultato.getObjectsMap().get("2").longValue(), true));
					}
					else{
						oneLine.add( risultato.getObjectsMap().get("0").longValue()+"" );
						oneLine.add( risultato.getObjectsMap().get("1").longValue()+"" );
						oneLine.add( risultato.getObjectsMap().get("2").longValue()+"" );
					}
				}
				else{
					if(convertRawData){
						oneLine.add( DurataConverter.convertSystemTimeIntoString_millisecondi(risultato.getObjectsMap().get(indexLatenza+"").longValue(), true));
					}
					else{
						oneLine.add( risultato.getObjectsMap().get(indexLatenza+"").longValue()+"");
					}
				}
				indexLatenza++;
			}

			if(isLatenzaServizio){
				if(distribuzionePerEsiti){
					if(convertRawData){
						oneLine.add( DurataConverter.convertSystemTimeIntoString_millisecondi( risultato.getObjectsMap().get("0").longValue(), true));
						oneLine.add( DurataConverter.convertSystemTimeIntoString_millisecondi( risultato.getObjectsMap().get("1").longValue(), true));
						oneLine.add( DurataConverter.convertSystemTimeIntoString_millisecondi( risultato.getObjectsMap().get("2").longValue(), true));
					}
					else{
						oneLine.add( risultato.getObjectsMap().get("0").longValue()+"");
						oneLine.add( risultato.getObjectsMap().get("1").longValue()+"");
						oneLine.add( risultato.getObjectsMap().get("2").longValue()+"");
					}
				}
				else{
					if(convertRawData){
						oneLine.add( DurataConverter.convertSystemTimeIntoString_millisecondi(risultato.getObjectsMap().get(indexLatenza+"").longValue(), true));
					}
					else{
						oneLine.add( risultato.getObjectsMap().get(indexLatenza+"").longValue()+"");
					}
				}
				indexLatenza++;
			}

			if(isLatenzaPorta){
				if(distribuzionePerEsiti){
					if(convertRawData){
						oneLine.add( DurataConverter.convertSystemTimeIntoString_millisecondi( risultato.getObjectsMap().get("0").longValue(), true));
						oneLine.add( DurataConverter.convertSystemTimeIntoString_millisecondi( risultato.getObjectsMap().get("1").longValue(), true));
						oneLine.add( DurataConverter.convertSystemTimeIntoString_millisecondi( risultato.getObjectsMap().get("2").longValue(), true));
					}
					else{
						oneLine.add( risultato.getObjectsMap().get("0").longValue()+"");
						oneLine.add( risultato.getObjectsMap().get("1").longValue()+"");
						oneLine.add( risultato.getObjectsMap().get("2").longValue()+"");
					}
				}
				else{
					if(convertRawData){
						oneLine.add( DurataConverter.convertSystemTimeIntoString_millisecondi(risultato.getObjectsMap().get(indexLatenza+"").longValue(), true));
					}
					else{
						oneLine.add( risultato.getObjectsMap().get(indexLatenza+"").longValue()+"");
					}
				}
				indexLatenza++;
			}

			log.debug("Gruppo " + oneLine.toString()); 

			dataSource.add(oneLine.toArray(new Object[oneLine.size()])); 

		}
		return dataSource;

	}

	public static void esportaCsvAndamentoTemporalePersonalizzato(OutputStream outputStream, JasperReportBuilder report, Map<String, List<Res>> results, 
			String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza) throws Exception{
		List<ColumnBuilder<?,?>> colonne = new ArrayList<ColumnBuilder<?,?>>();
		TextColumnBuilder<String> nomeColumn = col.column(headerLabel, "nome", type.stringType());
		colonne.add(nomeColumn);

		if (results != null && results.size() > 0) {
			Set<String> keys = results.keySet();
			Iterator<String> it = keys.iterator();
			int i = 0;
			while (it.hasNext()) {
				String key = it.next();

				// aggiungo la categoria
				colonne.add(col.column(key, "r"+i, type.stringType()));
				i++;
			}


		}

		report
		.setColumnTitleStyle(Templates.columnTitleStyle)
		.addProperty("net.sf.jasperreports.export.csv.exclude.origin.keep.first.band.1", "columnHeader")
		.ignorePageWidth()
		.ignorePagination()
		.columns(colonne.toArray(new ColumnBuilder[colonne.size()]));


		JasperCsvExporterBuilder builder =export.csvExporter(outputStream);
		report.toCsv(builder); 
	}

	public static void esportaXlsAndamentoTemporalePersonalizzato(OutputStream outputStream, JasperReportBuilder report, Map<String, List<Res>> results, 
			String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza) throws Exception{
		JasperXlsExporterBuilder builder = export.xlsExporter(outputStream).setDetectCellType(true).setIgnorePageMargins(true)
				.setWhitePageBackground(false)
				.setRemoveEmptySpaceBetweenColumns(true);

		List<ColumnBuilder<?,?>> colonne = new ArrayList<ColumnBuilder<?,?>>();
		TextColumnBuilder<String> nomeColumn = col.column(headerLabel, "nome", type.stringType()).setStretchWithOverflow(false)
				.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true");
		colonne.add(nomeColumn);

		if (results != null && results.size() > 0) {
			Set<String> keys = results.keySet();
			Iterator<String> it = keys.iterator();
			int i = 0;
			while (it.hasNext()) {
				String key = it.next();

				// aggiungo la categoria
				colonne.add(col.column(key, "r"+i, type.stringType()));
				i++;
			}


		}

		report
		.setColumnTitleStyle(Templates.columnTitleStyle)
		.addProperty("net.sf.jasperreports.export.csv.exclude.origin.keep.first.band.1", "columnHeader")
		//		.addProperty(JasperProperty.EXPORT_XLS_FREEZE_ROW, "2")
		.ignorePageWidth()
		.ignorePagination()
		.columns(colonne.toArray(new ColumnBuilder[colonne.size()]));


		report.toXls(builder);

	}

	public static void esportaPdfAndamentoTemporalePersonalizzato(OutputStream outputStream, JasperReportBuilder report, Map<String, List<Res>> results, 
			String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza) throws Exception{
		JasperPdfExporterBuilder builder = export.pdfExporter(outputStream);

		List<ColumnBuilder<?,?>> colonne = new ArrayList<ColumnBuilder<?,?>>();
		TextColumnBuilder<String> nomeColumn = col.column(headerLabel, "nome", type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		colonne.add(nomeColumn);

		if (results != null && results.size() > 0) {
			Set<String> keys = results.keySet();
			Iterator<String> it = keys.iterator();
			int i = 0;
			while (it.hasNext()) {
				String key = it.next();

				// aggiungo la categoria
				colonne.add(col.column(key, "r"+i, type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));
				i++;
			}


		}

		report
		.title(Templates.createTitleComponent(titoloReport,""))
		.setTemplate(Templates.reportTemplate)
		.columns(colonne.toArray(new ColumnBuilder[colonne.size()]));

		report.toPdf(builder);
	}

	private static JRDataSource getDatasourceAndamentoTemporalePersonalizzato (Map<String, List<Res>> results, Logger log,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza, StatisticType modalitaTemporale, boolean convertRawData) throws Exception {
		// Scittura Intestazione
		List<String> header = new ArrayList<String>();
		boolean isLatenzaTotale = false;	
		boolean isLatenzaServizio = false;
		boolean isLatenzaPorta = false;
		boolean isNumeroRichieste = false;
		boolean isDimensioneComplessiva = false;
		boolean isDimensioneInterna = false;
		boolean isDimensioneEsterna = false;
		switch (tipoVisualizzazione) {
		case DIMENSIONE_TRANSAZIONI:
			for (TipoBanda tipoBanda : tipiBanda) {
				switch (tipoBanda) {
				case COMPLESSIVA:
					isDimensioneComplessiva = true;
					break;
				case INTERNA:
					isDimensioneInterna = true;
					break;
				case ESTERNA:
				default:
					isDimensioneEsterna = true;
					break;
				}
			}
			break;

		case NUMERO_TRANSAZIONI:
			isNumeroRichieste = true;
			break;

		case TEMPO_MEDIO_RISPOSTA:
			for (TipoLatenza tipoLatenza : tipiLatenza) {
				switch (tipoLatenza) {
				case LATENZA_PORTA:
					isLatenzaPorta = true;
					break;
				case LATENZA_SERVIZIO:
					isLatenzaServizio = true;
					break;

				case LATENZA_TOTALE:
				default:
					isLatenzaTotale = true;
					break;
				}
			}
			break;
		}

		int numeroRisultati = 0;

		if (results != null && results.size() > 0) {
			Set<String> keys = results.keySet();
			Iterator<String> it = keys.iterator();
			int i = 0;

			// Aggiungo la prima colonna 'DATA' solo la prima volta
			header.add("nome");
			while (it.hasNext()) {
				String key = it.next();
				List<Res> list = results.get(key);
				numeroRisultati = list.size();
				// aggiungo la categoria
				header.add("r"+i);
				i++;
			}
		}

		log.debug("Header " + header.toString()); 

		// Creo il datasource

		SimpleDateFormat sdf = null;
		SimpleDateFormat sdf_last_hour = null;
		switch (modalitaTemporale) {
		case MENSILE:
			sdf = new SimpleDateFormat("MMM yyyy", ApplicationBean.getInstance().getLocale());
			break;
		case SETTIMANALE:
			sdf = new SimpleDateFormat("yyyy/MM/dd", ApplicationBean.getInstance().getLocale());
			sdf_last_hour = new SimpleDateFormat("yyyy/MM/dd", ApplicationBean.getInstance().getLocale());
			break;

		case ORARIA: 
			sdf = new SimpleDateFormat("yyyy/MM/dd HH", ApplicationBean.getInstance().getLocale());
			sdf_last_hour = new SimpleDateFormat("HH", ApplicationBean.getInstance().getLocale());
			break;

		case GIORNALIERA:
		default:
			sdf = new SimpleDateFormat("yyyy/MM/dd", ApplicationBean.getInstance().getLocale());
			break;
		}

		DRDataSource dataSource = new DRDataSource(header.toArray(new String[header.size()]));

		// ciclo sulle colonne
		for (int idx = 0 ; idx < numeroRisultati; idx ++) {
			List<Object> oneLine = new ArrayList<Object>();

			Set<String> keys = results.keySet();
			Iterator<String> it = keys.iterator();
			int i = 0;
			while (it.hasNext()) {
				String key = it.next();
				List<Res> list = results.get(key);
				Res risultato = list.get(idx);

				if(i < 1){

					String label = "";
					Calendar c =  null;
					switch (modalitaTemporale) {
					case SETTIMANALE:
						c = Calendar.getInstance();
						c.setTime((Date)risultato.getRisultato());
						c.add(Calendar.WEEK_OF_MONTH, 1);
						c.add(Calendar.DAY_OF_WEEK, -1);
						label = sdf.format(risultato.getRisultato())+"-"+sdf_last_hour.format(c.getTime());
						break;

					case ORARIA: 
						sdf = new SimpleDateFormat("yyyy/MM/dd HH", ApplicationBean.getInstance().getLocale());
						sdf_last_hour = new SimpleDateFormat("HH", ApplicationBean.getInstance().getLocale());

						c = Calendar.getInstance();
						c.setTime((Date)risultato.getRisultato());
						c.add(Calendar.HOUR, +1);
						label = sdf.format(risultato.getRisultato())+"-"+sdf_last_hour.format(c.getTime());
						break;

					case MENSILE:
					case GIORNALIERA:
					default:
						label = sdf.format(risultato.getRisultato());
						break;
					}

					// Servizio
					//			String label = risultato.getRisultato() != null ? sdf.format(risultato.getRisultato()) : "";
					oneLine.add(label);
					log.debug("Aggiunta Data " + label + " "); 
				}

				log.debug("Lista " + key + " Risultato Corrente " + risultato.getSomma());

				if(isNumeroRichieste){
					if(convertRawData){
						oneLine.add(Utility.numberConverter(risultato.getSomma()));
					}
					else{
						oneLine.add(risultato.getSomma()+"");
					}
				}

				int indexDimensione = 0;

				if(isDimensioneComplessiva){
					if(convertRawData){
						oneLine.add(Utility.fileSizeConverter(risultato.getObjectsMap().get(indexDimensione+"").longValue()));
					}
					else{
						oneLine.add(risultato.getObjectsMap().get(indexDimensione+"").longValue()+"");
					}
					indexDimensione++;
				}

				if(isDimensioneInterna){
					if(convertRawData){
						oneLine.add(Utility.fileSizeConverter(risultato.getObjectsMap().get(indexDimensione+"").longValue()));
					}
					else{
						oneLine.add(risultato.getObjectsMap().get(indexDimensione+"").longValue()+"");
					}
					indexDimensione++;
				}

				if(isDimensioneEsterna){
					if(convertRawData){
						oneLine.add(Utility.fileSizeConverter(risultato.getObjectsMap().get(indexDimensione+"").longValue()));
					}
					else{
						oneLine.add(risultato.getObjectsMap().get(indexDimensione+"").longValue()+"");
					}
					indexDimensione++;
				}

				int indexLatenza = 0;

				if(isLatenzaTotale){
					if(convertRawData){
						oneLine.add( DurataConverter.convertSystemTimeIntoString_millisecondi(risultato.getObjectsMap().get(indexLatenza+"").longValue(), true));
					}
					else{
						oneLine.add( risultato.getObjectsMap().get(indexLatenza+"").longValue() +"");
					}
					indexLatenza++;
				}

				if(isLatenzaServizio){
					if(convertRawData){
						oneLine.add( DurataConverter.convertSystemTimeIntoString_millisecondi(risultato.getObjectsMap().get(indexLatenza+"").longValue(), true));
					}
					else{
						oneLine.add( risultato.getObjectsMap().get(indexLatenza+"").longValue() +"");
					}
					indexLatenza++;
				}

				if(isLatenzaPorta){
					if(convertRawData){
						oneLine.add( DurataConverter.convertSystemTimeIntoString_millisecondi(risultato.getObjectsMap().get(indexLatenza+"").longValue(), true));
					}
					else{
						oneLine.add( risultato.getObjectsMap().get(indexLatenza+"").longValue() +"");
					}
					indexLatenza++;
				}

				i++;
			}

			log.debug("Gruppo " + oneLine.toString()); 

			dataSource.add(oneLine.toArray(new Object[oneLine.size()])); 

		}



		return dataSource;

	}

	private static JRDataSource getDatasourceDistribuzione (List<ResDistribuzione> list,Logger log,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza, TipoStatistica tipoStatistica, String tipoRiconoscimento, boolean convertRawData) throws Exception {
		// Scittura Intestazione
		List<String> header = new ArrayList<String>();

		header.add("nome");

		switch (tipoStatistica) {
		case DISTRIBUZIONE_AZIONE:
			header.add("parent_0");
			header.add("parent_1");
			break;
		case DISTRIBUZIONE_SERVIZIO:
			header.add("parent_0");
			break;
		case DISTRIBUZIONE_SERVIZIO_APPLICATIVO:
			if(tipoRiconoscimento != null && tipoRiconoscimento.equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO))
				header.add("parent_0");
			break;
		case ANDAMENTO_TEMPORALE:
		case DISTRIBUZIONE_SOGGETTO:
		case STATISTICA_PERSONALIZZATA:
		default:
			break;
		}

		boolean isLatenza = false;	
		boolean isNumeroRichieste = false;
		boolean isDimensione = false;
		switch (tipoVisualizzazione) {
		case DIMENSIONE_TRANSAZIONI:

			isDimensione = true;

			for (TipoBanda tipoBanda : tipiBanda) {
				switch (tipoBanda) {
				case COMPLESSIVA:
					header.add("occupazioneBandaComplessiva");
					break;
				case INTERNA:
					header.add("occupazioneBandaInterna");
					break;
				case ESTERNA:
				default:
					header.add("occupazioneBandaEsterna");
					break;
				}
			}
			break;


		case NUMERO_TRANSAZIONI:
			isNumeroRichieste = true;
			header.add("numeroRichieste");
			break;

		case TEMPO_MEDIO_RISPOSTA:{
			isLatenza = true;
			for (TipoLatenza tipoLatenza : tipiLatenza) {
				switch (tipoLatenza) {
				case LATENZA_PORTA:
					header.add("latenzaMediaPorta");
					break;
				case LATENZA_SERVIZIO:
					header.add("latenzaMediaServizio");
					break;

				case LATENZA_TOTALE:
				default:
					header.add("latenzaMediaTotale");
					break;
				}
			}
		}
		}

		DRDataSource dataSource = new DRDataSource(header.toArray(new String[header.size()])); 

		for (int idx = 0 ; idx < list.size() ; idx ++){
			ResDistribuzione risultato = list.get(idx);

			List<Object> oneLine = new ArrayList<Object>();

			// Servizio
			String label = risultato.getRisultato() != null ? risultato.getRisultato() : "";
			oneLine.add(label);
			
			if(!risultato.getParentMap().isEmpty()){
				Set<String> keySet = risultato.getParentMap().keySet();
				for (String parentCol : keySet) {
					oneLine.add(risultato.getParentMap().get(parentCol)); 
				}
			}
			

			if(isNumeroRichieste){
				if(convertRawData){
					oneLine.add(Utility.numberConverter( risultato.getSomma()) );
				}
				else{
					oneLine.add(risultato.getSomma()+"");
				}
			}

			if(isDimensione){
				if(convertRawData){
					oneLine.add(Utility.fileSizeConverter(risultato.getSomma().longValue()));
				}
				else{
					oneLine.add(risultato.getSomma().longValue()+"");
				}
			}

			if(isLatenza){
				if(convertRawData){
					oneLine.add( DurataConverter.convertSystemTimeIntoString_millisecondi(risultato.getSomma().longValue(), true));
				}
				else{
					oneLine.add( risultato.getSomma().longValue()+"");
				}
			}

			log.debug("Gruppo " + oneLine.toString()); 

			dataSource.add(oneLine.toArray(new Object[oneLine.size()])); 

		}
		return dataSource;

	}
}
