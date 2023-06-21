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
package org.openspcoop2.web.monitor.statistiche.utils;

import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.export;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.openspcoop2.core.commons.CoreException;
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
import org.openspcoop2.web.monitor.core.report.Colonna;
import org.openspcoop2.web.monitor.core.report.Templates;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiGrafici;
import org.slf4j.Logger;

import be.quodlibet.boxable.HorizontalAlignment;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperCsvExporterBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperXlsExporterBuilder;
import net.sf.dynamicreports.jasper.constant.JasperProperty;
import net.sf.dynamicreports.report.builder.column.ColumnBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;

/**
 * ExportUtils
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ExportUtils {

	private ExportUtils() {}

	private static final String HEADER_VALUE_CATEGORY_OK = "ok";
	private static final String HEADER_VALUE_LABEL_OK = "Ok";

	private static final String HEADER_VALUE_CATEGORY_FAULT_APPLICATIVO = "faultApplicativo";
	private static final String HEADER_VALUE_LABEL_FAULT_APPLICATIVO = "Fault Applicativo";
	
	private static final String HEADER_VALUE_CATEGORY_ERRORE = "errore";
	private static final String HEADER_VALUE_LABEL_ERRORE = "Fallite";
	
	private static final String HEADER_VALUE_CATEGORY_PARENT_0 = "parent_0";
	private static final String HEADER_VALUE_CATEGORY_PARENT_1 = "parent_1";
	
	
	private static final String HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_COMPLESSIVA = "occupazioneBandaComplessiva";
	private static final String HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_INTERNA = "occupazioneBandaInterna";
	private static final String HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_ESTERNA = "occupazioneBandaEsterna";
	
	private static final String HEADER_VALUE_CATEGORY_NUMERO_RICHIESTE = "numeroRichieste";
	private static final String HEADER_VALUE_LABEL_NUMERO_RICHIESTE = "Numero Transazioni";
	
	private static final String FORMAT_ANNO_MESE_GIORNO = "yyyy/MM/dd";
	private static final String FORMAT_MESE_ANNO = "MMM yyyy";
	private static final String FORMAT_ANNO_MESE_GIORNO_ORA = "yyyy/MM/dd HH";
	private static final String FORMAT_ORA = "HH";
	
	public static JasperReportBuilder creaReportAndamentoTemporale(List<Res> list,String titoloReport,Logger log,TipoVisualizzazione tipoVisualizzazione,
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza, StatisticType modalitaTemporale, boolean distribuzionePerEsiti, boolean convertRawData) throws CoreException {
		
		if(titoloReport!=null) {
			// non usato
		}
		
		JRDataSource dataSource = getDatasourceAndamentoTemporale(	list, log, tipoVisualizzazione, tipiBanda, tipiLatenza, modalitaTemporale,distribuzionePerEsiti, convertRawData);

		JasperReportBuilder builder = report();
		builder.setDataSource(dataSource);
		return builder;
	}

	public static JasperReportBuilder creaReportDistribuzione(List<ResDistribuzione> list,String titoloReport, Logger log,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza, TipoStatistica tipoStatistica, boolean convertRawData) {
		return creaReportDistribuzione(list, titoloReport, log, tipoVisualizzazione, tipiBanda, tipiLatenza, tipoStatistica, null, null, null, convertRawData);
	}

	public static JasperReportBuilder creaReportDistribuzione(List<ResDistribuzione> list,String titoloReport, Logger log,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza, TipoStatistica tipoStatistica, String tipoRiconoscimento, String identificazione, String tokenClaim, boolean convertRawData) {
		
		if(titoloReport!=null) {
			// non usato
		}
		
		JRDataSource dataSource = getDatasourceDistribuzione(list, log, tipoVisualizzazione, tipiBanda, tipiLatenza, tipoStatistica, tipoRiconoscimento, identificazione, tokenClaim, convertRawData);

		JasperReportBuilder builder = report();
		builder.setDataSource(dataSource);
		return builder;
	}

	public static JasperReportBuilder creaReportAndamentoTemporalePersonalizzato(Map<String, List<Res>> results,String titoloReport,Logger log,TipoVisualizzazione tipoVisualizzazione,
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza, StatisticType modalitaTemporale, boolean convertRawData) throws CoreException {
		
		if(titoloReport!=null) {
			// non usato
		}
		
		JRDataSource dataSource = getDatasourceAndamentoTemporalePersonalizzato(results, log, tipoVisualizzazione, tipiBanda, tipiLatenza, modalitaTemporale, convertRawData);

		JasperReportBuilder builder = report();
		builder.setDataSource(dataSource);
		return builder;
	}

	public static void esportaCsv(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica) throws CoreException {
		esportaCsv(outputStream, report, titoloReport, headerLabel, tipoVisualizzazione, tipiBanda, tipiLatenza, tipoStatistica, null, null, null); 
	}

	public static void esportaCsv(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica,String tipoRiconoscimento, String identificazione, String tokenClaim) throws CoreException {
		esportaCsv(outputStream, report, titoloReport, headerLabel, tipoVisualizzazione, tipiBanda,tipiLatenza, tipoStatistica, tipoRiconoscimento, identificazione, tokenClaim, false);
	}
	public static void esportaCsv(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica, String tipoRiconoscimento, String identificazione, String tokenClaim, boolean distribuzionePerEsiti) throws CoreException {
		
		if(titoloReport!=null) {
			// non usato
		}
		
		String headerValueLabel = "";
		String headerValueCategory = "";

		List<ColumnBuilder<?,?>> colonne = new ArrayList<ColumnBuilder<?,?>>();
		TextColumnBuilder<String> nomeColumn = col.column(headerLabel, "nome", type.stringType());
		colonne.add(nomeColumn);

		if(distribuzionePerEsiti){

			headerValueCategory = HEADER_VALUE_CATEGORY_OK;
			headerValueLabel = HEADER_VALUE_LABEL_OK;
			colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));

			headerValueCategory = HEADER_VALUE_CATEGORY_FAULT_APPLICATIVO;
			headerValueLabel = HEADER_VALUE_LABEL_FAULT_APPLICATIVO;
			colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));

			headerValueCategory = HEADER_VALUE_CATEGORY_ERRORE;
			headerValueLabel = HEADER_VALUE_LABEL_ERRORE;
			colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));

		}
		else{
			switch (tipoStatistica) {
			case DISTRIBUZIONE_ERRORI:
				headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_0;
				headerValueLabel =  MessageManager.getInstance().getMessage(Costanti.DESCRIZIONE_LABEL_KEY);
				colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
				break;
			case DISTRIBUZIONE_AZIONE:
				headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_0;
				headerValueLabel =  MessageManager.getInstance().getMessage(Costanti.API_LABEL_KEY);
				colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
				
				headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_1;
				headerValueLabel = MessageManager.getInstance().getMessage(Costanti.EROGATORE_LABEL_KEY);
				colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
				break;
			case DISTRIBUZIONE_SERVIZIO:
				headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_0;
				headerValueLabel = MessageManager.getInstance().getMessage(Costanti.EROGATORE_LABEL_KEY);
				colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
				break;
			case DISTRIBUZIONE_SERVIZIO_APPLICATIVO:
				if(tipoRiconoscimento != null && tipoRiconoscimento.equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
					headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_0;
					headerValueLabel = MessageManager.getInstance().getMessage(Costanti.SOGGETTO_LABEL_KEY);
					colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
					
					if (StringUtils.isNotBlank(identificazione) && org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(identificazione)) {
						headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_1;
						headerValueLabel = MessageManager.getInstance().getMessage(Costanti.TOKEN_CLIENT_ID_KEY);
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
					}
				}
				else if(tipoRiconoscimento != null && tipoRiconoscimento.equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO) &&
					tokenClaim!=null && StringUtils.isNotEmpty(tokenClaim)) {
					try {
						org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.valueOf(tokenClaim);
						if(org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.token_clientId.equals(tcm)) {
							
							headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_0;
							headerValueLabel = MessageManager.getInstance().getMessage(Costanti.SERVIZIO_APPLICATIVO_LABEL_KEY);
							colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
							
							headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_1;
							headerValueLabel = MessageManager.getInstance().getMessage(Costanti.SOGGETTO_LABEL_KEY);
							colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
						}
					}catch(Exception t) {
						// ignore
					}
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
						headerValueCategory = HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_COMPLESSIVA;
						headerValueLabel = "Occupazione Banda Complessiva [bytes]";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
						break;
					case INTERNA:
						headerValueCategory = HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_INTERNA;
						headerValueLabel = "Occupazione Banda Interna [bytes]";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
						break;
					case ESTERNA:
					default:
						headerValueCategory = HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_ESTERNA;
						headerValueLabel = "Occupazione Banda Esterna [bytes]";
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
						break;
					}
				}
				break;

			case NUMERO_TRANSAZIONI:
				headerValueCategory = HEADER_VALUE_CATEGORY_NUMERO_RICHIESTE;
				headerValueLabel = HEADER_VALUE_LABEL_NUMERO_RICHIESTE;
				colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
				break;

			case TEMPO_MEDIO_RISPOSTA:
				for (TipoLatenza tipoLatenza : tipiLatenza) {
					switch (tipoLatenza) {
					case LATENZA_PORTA:
						headerValueCategory = "latenzaMediaPorta";
						headerValueLabel = CostantiGrafici.LABEL_TIPO_LATENZA_LATENZA_MEDIA_PORTA_MS;
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
						break;
					case LATENZA_SERVIZIO:
						headerValueCategory = "latenzaMediaServizio";
						headerValueLabel = CostantiGrafici.LABEL_TIPO_LATENZA_LATENZA_MEDIA_SERVIZIO_MS;
						colonne.add(col.column(headerValueLabel, headerValueCategory, type.stringType()));
						break;

					case LATENZA_TOTALE:
					default:
						headerValueCategory = "latenzaMediaTotale";
						headerValueLabel = CostantiGrafici.LABEL_TIPO_LATENZA_LATENZA_MEDIA_TOTALE_MS;
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
		try {
			report.toCsv(builder);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}

	public static void esportaPdf(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica) throws IOException, JRException {
		esportaPdf(outputStream, report, titoloReport, headerLabel, tipoVisualizzazione, tipiBanda, tipiLatenza, tipoStatistica, null, null, null);
	}

	public static void esportaPdf(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica,String tipoRiconoscimento, String identificazione, String tokenClaim) throws IOException, JRException {
		esportaPdf(outputStream, report, titoloReport, headerLabel, tipoVisualizzazione, tipiBanda, tipiLatenza, tipoStatistica, tipoRiconoscimento, identificazione, tokenClaim, false);
	}
	public static void esportaPdf(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica, String tipoRiconoscimento, String identificazione, String tokenClaim,  boolean distribuzionePerEsiti) throws IOException, JRException {
		
		String headerValueLabel = "";
		String headerValueCategory = "";

		List<Colonna> colonne = new ArrayList<>();
		
		colonne.add(new Colonna("nome", headerLabel, HorizontalAlignment.CENTER));
		
		if(distribuzionePerEsiti){
			headerValueCategory = HEADER_VALUE_CATEGORY_OK;
			headerValueLabel = HEADER_VALUE_LABEL_OK;
			colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
			
			headerValueCategory = HEADER_VALUE_CATEGORY_FAULT_APPLICATIVO;
			headerValueLabel = HEADER_VALUE_LABEL_FAULT_APPLICATIVO;
			colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
			
			headerValueCategory = HEADER_VALUE_CATEGORY_ERRORE;
			headerValueLabel = HEADER_VALUE_LABEL_ERRORE;
			colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
		}
		else{
			switch (tipoStatistica) {
			case DISTRIBUZIONE_ERRORI:
				headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_0;
				headerValueLabel =  MessageManager.getInstance().getMessage(Costanti.DESCRIZIONE_LABEL_KEY);
				colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
				break;
			case DISTRIBUZIONE_AZIONE:
				headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_0;
				headerValueLabel =  MessageManager.getInstance().getMessage(Costanti.API_LABEL_KEY);
				colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
				
				headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_1;
				headerValueLabel = MessageManager.getInstance().getMessage(Costanti.EROGATORE_LABEL_KEY);
				colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
				break;
			case DISTRIBUZIONE_SERVIZIO:
				headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_0;
				headerValueLabel = MessageManager.getInstance().getMessage(Costanti.EROGATORE_LABEL_KEY);
				colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
				break;
			case DISTRIBUZIONE_SERVIZIO_APPLICATIVO:
				if(tipoRiconoscimento != null && tipoRiconoscimento.equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
					headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_0;
					headerValueLabel = MessageManager.getInstance().getMessage(Costanti.SOGGETTO_LABEL_KEY);
					colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
					
					if (StringUtils.isNotBlank(identificazione) && org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(identificazione)) {
						headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_1;
						headerValueLabel = MessageManager.getInstance().getMessage(Costanti.TOKEN_CLIENT_ID_KEY);
						colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
					}
				}
				else if(tipoRiconoscimento != null && tipoRiconoscimento.equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO) &&
					tokenClaim!=null && StringUtils.isNotEmpty(tokenClaim)) {
					try {
						org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.valueOf(tokenClaim);
						if(org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.token_clientId.equals(tcm)) {
							headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_0;
							headerValueLabel = MessageManager.getInstance().getMessage(Costanti.SERVIZIO_APPLICATIVO_LABEL_KEY);
							colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
							
							headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_1;
							headerValueLabel = MessageManager.getInstance().getMessage(Costanti.SOGGETTO_LABEL_KEY);
							colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
						}
					}catch(Exception t) {
						// ignore
					}
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
						headerValueCategory = HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_COMPLESSIVA;
						headerValueLabel = "Occupazione Banda Complessiva";
						colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
						break;
					case INTERNA:
						headerValueCategory = HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_INTERNA;
						headerValueLabel = "Occupazione Banda Interna";
						colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
						break;
					case ESTERNA:
					default:
						headerValueCategory = HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_ESTERNA;
						headerValueLabel = "Occupazione Banda Esterna";
						colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
						break;
					}
				}
				break;

			case NUMERO_TRANSAZIONI:
				headerValueCategory = HEADER_VALUE_CATEGORY_NUMERO_RICHIESTE;
				headerValueLabel = HEADER_VALUE_LABEL_NUMERO_RICHIESTE;
				colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
				break;

			case TEMPO_MEDIO_RISPOSTA:
				for (TipoLatenza tipoLatenza : tipiLatenza) {
					switch (tipoLatenza) {
					case LATENZA_PORTA:
						headerValueCategory = "latenzaMediaPorta";
						headerValueLabel = CostantiGrafici.LABEL_TIPO_LATENZA_LATENZA_MEDIA_PORTA;
						colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
						break;
					case LATENZA_SERVIZIO:
						headerValueCategory = "latenzaMediaServizio";
						headerValueLabel = CostantiGrafici.LABEL_TIPO_LATENZA_LATENZA_MEDIA_SERVIZIO;
						colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
						break;

					case LATENZA_TOTALE:
					default:
						headerValueCategory = "latenzaMediaTotale";
						headerValueLabel = CostantiGrafici.LABEL_TIPO_LATENZA_LATENZA_MEDIA_TOTALE;
						colonne.add(new Colonna(headerValueCategory, headerValueLabel, HorizontalAlignment.CENTER));
						break;
					}
				}
				break;

			}
		}

		List<List<String>> dati = Templates.estraiDatiPerTabellaPdfDaDataSource(report, colonne);
		
		PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
		
        Templates.createTitleComponent(titoloReport, "", document);
        
        Templates.createTableComponent(colonne, dati, document);
        
        document.save(outputStream);
        
	}

	public static void esportaXls(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica) throws CoreException {
		esportaXls(outputStream, report, titoloReport, headerLabel, tipoVisualizzazione, tipiBanda, tipiLatenza, tipoStatistica, null, null, null);
	}

	public static void esportaXls(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica,String tipoRiconoscimento, String identificazione, String tokenClaim) throws CoreException {
		esportaXls(outputStream, report, titoloReport, headerLabel, tipoVisualizzazione, tipiBanda, tipiLatenza, tipoStatistica, tipoRiconoscimento, identificazione, tokenClaim, false);
	}
	@SuppressWarnings("deprecation")
	private static TextColumnBuilder<String> buildColumnDeprecated(String label, String category){
		return col.column(label, category, type.stringType())
			.setStretchWithOverflow(false)
			.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true");
	}
	private static TextColumnBuilder<String> buildColumnWithoutTextAdjust(String label, String category){
		return col.column(label, category, type.stringType())
			//.setTextAdjust(net.sf.dynamicreports.report.constant.TextAdjust.STRETCH_HEIGHT)
			.addProperty(JasperProperty.PRINT_KEEP_FULL_TEXT, "true");
	}
	private static boolean useDeprecatedMethod = false;
	public static boolean isUseDeprecatedMethod() {
		return useDeprecatedMethod;
	}
	public static void setUseDeprecatedMethod(boolean useDeprecatedMethod) {
		ExportUtils.useDeprecatedMethod = useDeprecatedMethod;
	}
	private static TextColumnBuilder<String> buildColumn(String label, String category){
		if(useDeprecatedMethod) {
			return buildColumnDeprecated(label, category);
		}
		else {
			return buildColumnWithoutTextAdjust(label, category);
		}
	}
	
	public static void esportaXls(OutputStream outputStream, JasperReportBuilder report,String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza,TipoStatistica tipoStatistica,String tipoRiconoscimento, String identificazione, String tokenClaim, boolean distribuzionePerEsiti) throws CoreException {
		
		if(titoloReport!=null) {
			// non usato
		}
		
		JasperXlsExporterBuilder builder = export.xlsExporter(outputStream).setDetectCellType(true).setIgnorePageMargins(true)
				.setWhitePageBackground(false)
				.setRemoveEmptySpaceBetweenColumns(true);

		String headerValueLabel = "";
		String headerValueCategory = "";

		List<ColumnBuilder<?,?>> colonne = new ArrayList<>();
		TextColumnBuilder<String> nomeColumn = buildColumn(headerLabel, "nome");
		colonne.add(nomeColumn);

		if(distribuzionePerEsiti){

			headerValueCategory = HEADER_VALUE_CATEGORY_OK;
			headerValueLabel = HEADER_VALUE_LABEL_OK;
			colonne.add(buildColumn(headerValueLabel, headerValueCategory));

			headerValueCategory = HEADER_VALUE_CATEGORY_FAULT_APPLICATIVO;
			headerValueLabel = HEADER_VALUE_LABEL_FAULT_APPLICATIVO;
			colonne.add(buildColumn(headerValueLabel, headerValueCategory));

			headerValueCategory = HEADER_VALUE_CATEGORY_ERRORE;
			headerValueLabel = HEADER_VALUE_LABEL_ERRORE;
			colonne.add(buildColumn(headerValueLabel, headerValueCategory));

		}
		else{
			switch (tipoStatistica) {
			case DISTRIBUZIONE_ERRORI:
				headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_0;
				headerValueLabel =  MessageManager.getInstance().getMessage(Costanti.DESCRIZIONE_LABEL_KEY);
				colonne.add(buildColumn(headerValueLabel, headerValueCategory));
				break;
			case DISTRIBUZIONE_AZIONE:
				headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_0;
				headerValueLabel =  MessageManager.getInstance().getMessage(Costanti.API_LABEL_KEY);
				colonne.add(buildColumn(headerValueLabel, headerValueCategory));
				
				headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_1;
				headerValueLabel = MessageManager.getInstance().getMessage(Costanti.EROGATORE_LABEL_KEY);
				colonne.add(buildColumn(headerValueLabel, headerValueCategory));
				break;
			case DISTRIBUZIONE_SERVIZIO:
				
				headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_0;
				headerValueLabel = MessageManager.getInstance().getMessage(Costanti.EROGATORE_LABEL_KEY);
				colonne.add(buildColumn(headerValueLabel, headerValueCategory));
				break;
			case DISTRIBUZIONE_SERVIZIO_APPLICATIVO:
				if(tipoRiconoscimento != null && tipoRiconoscimento.equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
					headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_0;
					headerValueLabel = MessageManager.getInstance().getMessage(Costanti.SOGGETTO_LABEL_KEY);
					colonne.add(buildColumn(headerValueLabel, headerValueCategory));
					
					if (StringUtils.isNotBlank(identificazione) && org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(identificazione)) {
						headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_1;
						headerValueLabel = MessageManager.getInstance().getMessage(Costanti.TOKEN_CLIENT_ID_KEY);
						colonne.add(buildColumn(headerValueLabel, headerValueCategory));
					}
				}
				else if(tipoRiconoscimento != null && tipoRiconoscimento.equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO) &&
					tokenClaim!=null && StringUtils.isNotEmpty(tokenClaim)) {
					try {
						org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.valueOf(tokenClaim);
						if(org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.token_clientId.equals(tcm)) {
							
							headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_0;
							headerValueLabel = MessageManager.getInstance().getMessage(Costanti.SERVIZIO_APPLICATIVO_LABEL_KEY);
							colonne.add(buildColumn(headerValueLabel, headerValueCategory));
							
							headerValueCategory = HEADER_VALUE_CATEGORY_PARENT_1;
							headerValueLabel = MessageManager.getInstance().getMessage(Costanti.SOGGETTO_LABEL_KEY);
							colonne.add(buildColumn(headerValueLabel, headerValueCategory));
						}
					}catch(Exception t) {
						// ignore
					}
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
						headerValueCategory = HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_COMPLESSIVA;
						headerValueLabel = "Occupazione Banda Complessiva [bytes]";
						colonne.add(buildColumn(headerValueLabel, headerValueCategory));
						break;
					case INTERNA:
						headerValueCategory = HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_INTERNA;
						headerValueLabel = "Occupazione Banda Interna [bytes]";
						colonne.add(buildColumn(headerValueLabel, headerValueCategory));
						break;
					case ESTERNA:
					default:
						headerValueCategory = HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_ESTERNA;
						headerValueLabel = "Occupazione Banda Esterna [bytes]";
						colonne.add(buildColumn(headerValueLabel, headerValueCategory));
						break;
					}
				}
				break;

			case NUMERO_TRANSAZIONI:
				headerValueCategory = HEADER_VALUE_CATEGORY_NUMERO_RICHIESTE;
				headerValueLabel = HEADER_VALUE_LABEL_NUMERO_RICHIESTE;
				colonne.add(buildColumn(headerValueLabel, headerValueCategory));
				break;

			case TEMPO_MEDIO_RISPOSTA:
				for (TipoLatenza tipoLatenza : tipiLatenza) {
					switch (tipoLatenza) {
					case LATENZA_PORTA:
						headerValueCategory = "latenzaMediaPorta";
						headerValueLabel = CostantiGrafici.LABEL_TIPO_LATENZA_LATENZA_MEDIA_PORTA_MS;
						colonne.add(buildColumn(headerValueLabel, headerValueCategory));
						break;
					case LATENZA_SERVIZIO:
						headerValueCategory = "latenzaMediaServizio";
						headerValueLabel = CostantiGrafici.LABEL_TIPO_LATENZA_LATENZA_MEDIA_SERVIZIO_MS;
						colonne.add(buildColumn(headerValueLabel, headerValueCategory));
						break;

					case LATENZA_TOTALE:
					default:
						headerValueCategory = "latenzaMediaTotale";
						headerValueLabel = CostantiGrafici.LABEL_TIPO_LATENZA_LATENZA_MEDIA_TOTALE_MS;
						colonne.add(buildColumn(headerValueLabel, headerValueCategory));
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

		try {
			report.toXls(builder);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}

	}



	private static JRDataSource getDatasourceAndamentoTemporale (List<Res> list, Logger log,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza, 
			StatisticType modalitaTemporale,boolean distribuzionePerEsiti, boolean convertRawData) throws CoreException  {
		// Scittura Intestazione
		List<String> header = new ArrayList<>();

		header.add("nome");

		if(distribuzionePerEsiti){
			header.add(HEADER_VALUE_CATEGORY_OK);
			header.add(HEADER_VALUE_CATEGORY_FAULT_APPLICATIVO);
			header.add(HEADER_VALUE_CATEGORY_ERRORE);
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
						header.add(HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_COMPLESSIVA);
					break;
				case INTERNA:
					isDimensioneInterna = true;
					if(!distribuzionePerEsiti)
						header.add(HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_INTERNA);
					break;
				case ESTERNA:
				default:
					isDimensioneEsterna = true;
					if(!distribuzionePerEsiti)
						header.add(HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_ESTERNA);
					break;
				}
			}
			break;

		case NUMERO_TRANSAZIONI:
			isNumeroRichieste = true;
			if(!distribuzionePerEsiti)
				header.add(HEADER_VALUE_CATEGORY_NUMERO_RICHIESTE);
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
		SimpleDateFormat sdfLastHour = null;
		switch (modalitaTemporale) {
		case MENSILE:
			sdf = new SimpleDateFormat(FORMAT_MESE_ANNO, ApplicationBean.getInstance().getLocale());
			break;
		case SETTIMANALE:
			sdf = new SimpleDateFormat(FORMAT_ANNO_MESE_GIORNO, ApplicationBean.getInstance().getLocale());
			sdfLastHour = new SimpleDateFormat(FORMAT_ANNO_MESE_GIORNO, ApplicationBean.getInstance().getLocale());
			break;

		case ORARIA: 
			sdf = new SimpleDateFormat(FORMAT_ANNO_MESE_GIORNO_ORA, ApplicationBean.getInstance().getLocale());
			sdfLastHour = new SimpleDateFormat(FORMAT_ORA, ApplicationBean.getInstance().getLocale());
			break;

		case GIORNALIERA:
		default:
			sdf = new SimpleDateFormat(FORMAT_ANNO_MESE_GIORNO, ApplicationBean.getInstance().getLocale());
			break;
		}

		DRDataSource dataSource = new DRDataSource(header.toArray(new String[header.size()])); 

		for (int idx = 0 ; idx < list.size() ; idx ++){
			Res risultato = list.get(idx);

			List<Object> oneLine = new ArrayList<>();

			String label = "";
			Calendar c =  null;
			switch (modalitaTemporale) {
			case SETTIMANALE:
				c = Calendar.getInstance();
				c.setTime(risultato.getRisultato());
				c.add(Calendar.WEEK_OF_MONTH, 1);
				c.add(Calendar.DAY_OF_WEEK, -1);
				if(sdfLastHour==null) {
					throw new CoreException("sdf_last_hour undefined");
				}
				label = sdf.format(risultato.getRisultato())+"-"+sdfLastHour.format(c.getTime());
				break;

			case ORARIA: 
				sdf = new SimpleDateFormat(FORMAT_ANNO_MESE_GIORNO_ORA, ApplicationBean.getInstance().getLocale());
				sdfLastHour = new SimpleDateFormat(FORMAT_ORA, ApplicationBean.getInstance().getLocale());

				c = Calendar.getInstance();
				c.setTime(risultato.getRisultato());
				c.add(Calendar.HOUR, +1);
				label = sdf.format(risultato.getRisultato())+"-"+sdfLastHour.format(c.getTime());
				break;

			case MENSILE:
			case GIORNALIERA:
			default:
				label = sdf.format(risultato.getRisultato());
				break;
			}

			// Servizio
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
				/**indexDimensione++;*/
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
				/**indexLatenza++;*/
			}

			String msg = "Gruppo " + oneLine.toString();
			log.debug(msg); 

			dataSource.add(oneLine.toArray(new Object[oneLine.size()])); 

		}
		return dataSource;

	}

	public static void esportaCsvAndamentoTemporalePersonalizzato(OutputStream outputStream, JasperReportBuilder report, Map<String, List<Res>> results, 
			String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza) throws CoreException {
		
		if(titoloReport!=null) {
			// non usato
		}
		if(tipiBanda!=null) {
			// non usato
		}
		if(tipoVisualizzazione!=null) {
			// non usato
		}
		if(tipiLatenza!=null) {
			// non usato
		}
		
		List<ColumnBuilder<?,?>> colonne = new ArrayList<>();
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
		try {
			report.toCsv(builder);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}

	public static void esportaXlsAndamentoTemporalePersonalizzato(OutputStream outputStream, JasperReportBuilder report, Map<String, List<Res>> results, 
			String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza) throws CoreException {
		
		if(titoloReport!=null) {
			// non usato
		}
		if(tipoVisualizzazione!=null) {
			// non usato
		}
		if(tipiBanda!=null) {
			// non usato
		}
		if(tipiLatenza!=null) {
			// non usato
		}
		
		JasperXlsExporterBuilder builder = export.xlsExporter(outputStream).setDetectCellType(true).setIgnorePageMargins(true)
				.setWhitePageBackground(false)
				.setRemoveEmptySpaceBetweenColumns(true);

		List<ColumnBuilder<?,?>> colonne = new ArrayList<ColumnBuilder<?,?>>();
		TextColumnBuilder<String> nomeColumn = buildColumn(headerLabel, "nome");
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

		try {
			report.toXls(builder);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}

	}

	public static void esportaPdfAndamentoTemporalePersonalizzato(OutputStream outputStream, JasperReportBuilder report, Map<String, List<Res>> results, 
			String titoloReport, String headerLabel,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza) throws IOException, JRException{

		if(tipoVisualizzazione!=null) {
			// non usato
		}
		if(tipiBanda!=null) {
			// non usato
		}
		if(tipiLatenza!=null) {
			// non usato
		}
		
		List<Colonna> colonne = new ArrayList<>();
		colonne.add(new Colonna("nome", headerLabel, HorizontalAlignment.CENTER));
		
		if (results != null && results.size() > 0) {
			Set<String> keys = results.keySet();
			Iterator<String> it = keys.iterator();
			int i = 0;
			while (it.hasNext()) {
				String key = it.next();

				// aggiungo la categoria
				colonne.add(new Colonna("r"+i, key, HorizontalAlignment.CENTER));
				i++;
			}


		}
		
		List<List<String>> dati = Templates.estraiDatiPerTabellaPdfDaDataSource(report, colonne);
		
		PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
		
        Templates.createTitleComponent(titoloReport, "", document);
        
        Templates.createTableComponent(colonne, dati, document);
        
        document.save(outputStream);

	}

	private static JRDataSource getDatasourceAndamentoTemporalePersonalizzato (Map<String, List<Res>> results, Logger log,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza, StatisticType modalitaTemporale, boolean convertRawData) throws CoreException  {
		// Scittura Intestazione
		List<String> header = new ArrayList<>();
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

		String msg = "Header " + header.toString();
		log.debug(msg); 

		// Creo il datasource

		SimpleDateFormat sdf = null;
		SimpleDateFormat sdfLastHour = null;
		switch (modalitaTemporale) {
		case MENSILE:
			sdf = new SimpleDateFormat(FORMAT_MESE_ANNO, ApplicationBean.getInstance().getLocale());
			break;
		case SETTIMANALE:
			sdf = new SimpleDateFormat(FORMAT_ANNO_MESE_GIORNO, ApplicationBean.getInstance().getLocale());
			sdfLastHour = new SimpleDateFormat(FORMAT_ANNO_MESE_GIORNO, ApplicationBean.getInstance().getLocale());
			break;

		case ORARIA: 
			sdf = new SimpleDateFormat(FORMAT_ANNO_MESE_GIORNO_ORA, ApplicationBean.getInstance().getLocale());
			sdfLastHour = new SimpleDateFormat(FORMAT_ORA, ApplicationBean.getInstance().getLocale());
			break;

		case GIORNALIERA:
		default:
			sdf = new SimpleDateFormat(FORMAT_ANNO_MESE_GIORNO, ApplicationBean.getInstance().getLocale());
			break;
		}

		DRDataSource dataSource = new DRDataSource(header.toArray(new String[header.size()]));

		// ciclo sulle colonne
		for (int idx = 0 ; idx < numeroRisultati; idx ++) {
			List<Object> oneLine = new ArrayList<>();

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
						c.setTime(risultato.getRisultato());
						c.add(Calendar.WEEK_OF_MONTH, 1);
						c.add(Calendar.DAY_OF_WEEK, -1);
						
						if(sdfLastHour==null) {
							throw new CoreException("sdf_last_hour is null");
						}
						
						label = sdf.format(risultato.getRisultato())+"-"+sdfLastHour.format(c.getTime());
						break;

					case ORARIA: 
						sdf = new SimpleDateFormat(FORMAT_ANNO_MESE_GIORNO_ORA, ApplicationBean.getInstance().getLocale());
						sdfLastHour = new SimpleDateFormat(FORMAT_ORA, ApplicationBean.getInstance().getLocale());

						c = Calendar.getInstance();
						c.setTime(risultato.getRisultato());
						c.add(Calendar.HOUR, +1);
						label = sdf.format(risultato.getRisultato())+"-"+sdfLastHour.format(c.getTime());
						break;

					case MENSILE:
					case GIORNALIERA:
					default:
						label = sdf.format(risultato.getRisultato());
						break;
					}

					// Servizio
					oneLine.add(label);
					msg = "Aggiunta Data " + label + " ";
					log.debug(msg); 
				}

				msg = "Lista " + key + " Risultato Corrente " + risultato.getSomma();
				log.debug(msg);

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
					/**indexDimensione++;*/
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
					/**indexLatenza++;*/
				}

				i++;
			}

			msg = "Gruppo " + oneLine.toString();
			log.debug(msg); 

			dataSource.add(oneLine.toArray(new Object[oneLine.size()])); 

		}



		return dataSource;

	}

	private static JRDataSource getDatasourceDistribuzione (List<ResDistribuzione> list,Logger log,TipoVisualizzazione tipoVisualizzazione, 
			List<TipoBanda> tipiBanda,List<TipoLatenza> tipiLatenza, TipoStatistica tipoStatistica, String tipoRiconoscimento, String identificazione, String tokenClaim, boolean convertRawData)  {
		// Scittura Intestazione
		List<String> header = new ArrayList<>();

		header.add("nome");

		switch (tipoStatistica) {
		case DISTRIBUZIONE_ERRORI:
			header.add(HEADER_VALUE_CATEGORY_PARENT_0);
			break;
		case DISTRIBUZIONE_AZIONE:
			header.add(HEADER_VALUE_CATEGORY_PARENT_0);
			header.add(HEADER_VALUE_CATEGORY_PARENT_1);
			break;
		case DISTRIBUZIONE_SERVIZIO:
			header.add(HEADER_VALUE_CATEGORY_PARENT_0);
			break;
		case DISTRIBUZIONE_SERVIZIO_APPLICATIVO:
			if(tipoRiconoscimento != null && tipoRiconoscimento.equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				header.add(HEADER_VALUE_CATEGORY_PARENT_0);
				if (StringUtils.isNotBlank(identificazione) && org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(identificazione)) {
					header.add(HEADER_VALUE_CATEGORY_PARENT_1);
				}
			}
			else if(tipoRiconoscimento != null && tipoRiconoscimento.equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO) &&
				tokenClaim!=null && StringUtils.isNotEmpty(tokenClaim)) {
				try {
					org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.valueOf(tokenClaim);
					if(org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.token_clientId.equals(tcm)) {
						header.add(HEADER_VALUE_CATEGORY_PARENT_0);
						header.add(HEADER_VALUE_CATEGORY_PARENT_1);
					}
				}catch(Exception t) {
					// ignore
				}
			}
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
					header.add(HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_COMPLESSIVA);
					break;
				case INTERNA:
					header.add(HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_INTERNA);
					break;
				case ESTERNA:
				default:
					header.add(HEADER_VALUE_CATEGORY_OCCUPAZIONE_BANDA_ESTERNA);
					break;
				}
			}
			break;


		case NUMERO_TRANSAZIONI:
			isNumeroRichieste = true;
			header.add(HEADER_VALUE_CATEGORY_NUMERO_RICHIESTE);
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

			List<Object> oneLine = new ArrayList<>();

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

			String msg = "Gruppo " + oneLine.toString();
			log.debug(msg); 

			dataSource.add(oneLine.toArray(new Object[oneLine.size()])); 

		}
		return dataSource;

	}
}
