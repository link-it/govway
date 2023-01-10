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
package org.openspcoop2.web.monitor.transazioni.exporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.XMLRootElement;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticDriver;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticSerializer;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaDriver;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiException;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiNotFoundException;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.slf4j.Logger;

import org.openspcoop2.core.transazioni.TransazioneExport;
import org.openspcoop2.core.transazioni.constants.DeleteState;
import org.openspcoop2.core.transazioni.constants.ExportState;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.converter.DurataConverter;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.report.Templates;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.core.manifest.RuoloType;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniExportService;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperCsvExporterBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperXlsExporterBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.ColumnBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * SingleCsvFileExporter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class SingleCsvFileExporter implements IExporter{

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private boolean exportTracce = false;
	private boolean exportDiagnostici = false;
	private boolean exportContenuti = false;
	private boolean enableHeaderInfo = false;
	private boolean mimeThrowExceptionIfNotFound = false;
	private boolean abilitaMarcamentoTemporale = false;
	private String formato = null;
	private List<String> colonneSelezionate = null;
	private boolean useCount = true;

	private ITransazioniService transazioniService;
	private ITracciaDriver tracciamentoService;
	private IDiagnosticDriver diagnosticiService;
	private ITransazioniExportService transazioniExporterService;

	private String fileName=null;
	private OutputStream outStream = null;
	private SimpleDateFormat _sdfDataTransazioni = new SimpleDateFormat(CostantiExport.PATTERN_DATA_TRANSAZIONI);
	private String formatDate(Date date) {
		String s = this._sdfDataTransazioni.format(date);
		return s.replace(" ", "T");
	}
	
	private SingleCsvFileExporter(ExporterCsvProperties properties, ITransazioniService transazioniService, ITracciaDriver tracciamentoService,IDiagnosticDriver diagnosticiService, ITransazioniExportService transazioniExport) throws Exception{
		this.enableHeaderInfo = properties.isEnableHeaderInfo();

		this.exportTracce = properties.isExportTracce();
		this.exportDiagnostici = properties.isExportDiagnostici();
		this.exportContenuti = properties.isExportContenuti();
		this.mimeThrowExceptionIfNotFound = properties.isMimeThrowExceptionIfNotFound();
		this.abilitaMarcamentoTemporale = properties.isAbilitaMarcamentoTemporaleEsportazione();
		this.formato = properties.getFormato();
		this.colonneSelezionate = properties.getColonneSelezionate();
		this.useCount = properties.isUseCount();

		this.tracciamentoService = tracciamentoService;
		this.transazioniService = transazioniService;
		this.diagnosticiService = diagnosticiService;
		this.transazioniExporterService = transazioniExport;
		
		SingleCsvFileExporter.log.info("Single File Exporter inizializzato:");
		SingleCsvFileExporter.log.info("\t -esportazione Tracce      abilitata: "+this.exportTracce);
		SingleCsvFileExporter.log.info("\t -esportazione Contenuti   abilitata: "+this.exportContenuti);
		SingleCsvFileExporter.log.info("\t -esportazione Diagnostici abilitata: "+this.exportDiagnostici);
		SingleCsvFileExporter.log.info("\t -enable header info abilitato: "+this.enableHeaderInfo);
		SingleCsvFileExporter.log.info("\t -formato scelto: "+this.formato);
		SingleCsvFileExporter.log.info("\t -usa count: "+this.useCount);
		if(!this.useCount) {
			SingleCsvFileExporter.log.info("\t -numero massimo elementi esportati: "+Costanti.SELECT_ITEM_VALORE_MASSIMO_ENTRIES);
		}

		SingleCsvFileExporter.log.info("\t -MimeType handling (mime.throwExceptionIfMappingNotFound):"+this.mimeThrowExceptionIfNotFound);
	}

	public SingleCsvFileExporter(OutputStream outStream,ExporterCsvProperties properties, ITransazioniService transazioniService, ITracciaDriver tracciamentoService,IDiagnosticDriver diagnosticiService, ITransazioniExportService transazioniExport) throws Exception{
		this(properties, transazioniService, tracciamentoService, diagnosticiService,transazioniExport);
		this.outStream = outStream;
	}

	public SingleCsvFileExporter(File destFile,ExporterCsvProperties properties, ITransazioniService transazioniService, ITracciaDriver tracciamentoService,IDiagnosticDriver diagnosticiService,ITransazioniExportService transazioniExport) throws Exception{
		this(properties,transazioniService,tracciamentoService,diagnosticiService,transazioniExport);
		this.outStream = new FileOutputStream(destFile);
		this.fileName = destFile.getName();
		SingleCsvFileExporter.log.info("\n\t -Esportazione su file:"+destFile.getAbsolutePath());
	}

	public SingleCsvFileExporter(String pathToFile,ExporterCsvProperties properties, ITransazioniService transazioniService, ITracciaDriver tracciamentoService,IDiagnosticDriver diagnosticiService, ITransazioniExportService transazioniExport) throws Exception{
		this(new File(pathToFile), properties, transazioniService, tracciamentoService, diagnosticiService,transazioniExport);
	}

	private void popolaDataSourceExport(DRDataSource dataSource, List<TransazioneBean> transazioni) throws ExportException{

		for(TransazioneBean t: transazioni){
			// elemento della lista che deve contenere i valori scelti dall'utente
			List<Object> oneLine = getLine(t);

			// aggiungo la entry al datasource
			dataSource.add(oneLine.toArray(new Object[oneLine.size()])); 
		}//chiudo for transazioni
	}

	private void scriviDiagnostici(TransazioneBean t, List<Object> oneLine) throws ExportException {
		//diagnostici
		if(this.exportDiagnostici){
			try{
				FiltroRicercaDiagnosticiConPaginazione filter = new FiltroRicercaDiagnosticiConPaginazione();

				//devo impostare solo l'idtransazione
				//filter.setIdEgov(this.diagnosticiBean.getIdEgov());	
				Map<String, String> properties = new HashMap<String, String>();
				properties.put("id_transazione", t.getIdTransazione());
				filter.setProperties(properties);
				List<MsgDiagnostico> list = this.diagnosticiService.getMessaggiDiagnostici(filter);
				StringBuilder sbDiagnostici = new StringBuilder();
				String tail = null;
				for (int j = 0; j < list.size(); j++) {
					MsgDiagnostico msg = list.get(j);
					String newLine = j > 0 ? "\n\n" : CostantiExport.EMPTY_STRING;

					IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(msg.getProtocollo());
					IDiagnosticSerializer diagnosticoBuilder = pf.createDiagnosticSerializer();
					
					if(j==0){
						XMLRootElement xmlRootElement = diagnosticoBuilder.getXMLRootElement();
						if(xmlRootElement!=null){
							String head = xmlRootElement.getAsStringStartTag();
							if(head!=null && !"".equals(head)){
								head = head +"\n\n";
								sbDiagnostici.append(head);
								tail = xmlRootElement.getAsStringEndTag();
    							if(tail!=null && !"".equals(tail)){
    								tail = "\n\n" + tail;
    							}
							}
						}
					}
					
					String msgDiagnostico = diagnosticoBuilder.toString(msg,TipoSerializzazione.DEFAULT);
					sbDiagnostici.append(newLine).append(msgDiagnostico);
				}
				if(tail!=null && !"".equals(tail)){
					sbDiagnostici.append(tail);
				}

				oneLine.add(sbDiagnostici.toString());
			}catch (DriverMsgDiagnosticiException e) {
				String msg = "Si e' verificato un errore durante l'esportazione della transazione con id:"+t.getIdTransazione();
				msg+=" Non sono riuscito a creare il file diagnostici.xml ("+e.getMessage()+")";
				throw new ExportException(msg, e);
			} catch (DriverMsgDiagnosticiNotFoundException e) {
				// diagnostici non presenti
				oneLine.add(CostantiExport.EMPTY_STRING);
			} catch (ProtocolException e) {
				String msg = "Si e' verificato un errore durante l'esportazione della transazione con id:"+t.getIdTransazione();
				msg+=" Non sono riuscito a creare il file diagnostici.xml ("+e.getMessage()+")";
				throw new ExportException(msg, e);
			}
		}
	}

	private void scriviTraccia(TransazioneBean t,List<Object> oneLine, boolean isRisposta) throws ExportException {
		//tracce
		if(this.exportTracce){
			//devo impostare solo l'idtransazione
			//filter.setIdEgov(this.diagnosticiBean.getIdEgov());	
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("id_transazione", t.getIdTransazione());

			Traccia tracciaRichiesta = null;
			Traccia tracciaRisposta  = null;
			ArrayList<Traccia> tracce = new ArrayList<Traccia>();
			try{
				if(!isRisposta){
					tracciaRichiesta=this.tracciamentoService.getTraccia(RuoloMessaggio.RICHIESTA,properties);
					tracce.add(tracciaRichiesta);
				}
			}catch(DriverTracciamentoException e){
				String msg = "Si e' verificato un errore durante l'esportazione della transazione con id:"+t.getIdTransazione();
				msg+=" Non sono riuscito a recuperare la traccia di richiesta ("+e.getMessage()+")";
				throw new ExportException(msg, e);
			}catch(DriverTracciamentoNotFoundException e){
				//ignore
			}
			try{
				if(isRisposta) {
					tracciaRisposta = this.tracciamentoService.getTraccia(RuoloMessaggio.RISPOSTA,properties);
					tracce.add(tracciaRisposta);
				}
			}catch(DriverTracciamentoException e){
				String msg = "Si e' verificato un errore durante l'esportazione della transazione con id:"+t.getIdTransazione();
				msg+=" Non sono riuscito a recuperare la traccia di risposta ("+e.getMessage()+")";
				throw new ExportException(msg, e);
			}catch(DriverTracciamentoNotFoundException e){
				//ignore
			}

			if(tracce.size()>0){
				// Add ZIP entry to output stream.
				try{
					for (int j = 0; j < tracce.size(); j++) {
						Traccia tr = tracce.get(j);
						//						String newLine = j > 0 ? "\n\n" : EMPTY_STRING;

						IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tr.getProtocollo());
						ITracciaSerializer tracciaBuilder = pf.createTracciaSerializer();

						try {
							String traccia = tracciaBuilder.toString(tr,TipoSerializzazione.DEFAULT);
							oneLine.add(traccia);
						} catch (ProtocolException e) {
							String idTransazione = t.getIdTransazione();
							String tipoTraccia = tr.getTipoMessaggio().getTipo();
							String bustaAsString = tr.getBustaAsString();
							String messaggioErrore = e.getMessage();

							StringBuilder sb = new StringBuilder(0);
							sb.append("Impossibile leggere il contenuto della traccia:").append("\n");
							sb.append("ID Transazione: ").append(idTransazione)
							.append("\n");
							sb.append("Tipo Traccia: ").append(tipoTraccia)
							.append("\n");
							sb.append("Busta: \n").append(bustaAsString)
							.append("\n\n");
							sb.append("Errore: \n").append(messaggioErrore)
							.append("\n");

							oneLine.add(sb.toString());
						}
					}

					// se si sono riscontrati degli errori nella produzione delle tracce
					// creo un file che contiene la descrizione di tali errori.
					//					if (errori.size() > 0) {
					//						// Add ZIP entry to output stream.
					//						this.zip.putNextEntry(new ZipEntry(transazioneDir+"tracce.xml.error"));
					//
					//						for (int i = 0; i < errori.size(); i++) {
					//							String errore = errori.get(i);
					//
					//							String newLine = i > 0 ? "\n\n" : EMPTY_STRING;
					//
					//							in = new ByteArrayInputStream((newLine + errore).getBytes());
					//
					//							// Transfer bytes from the input stream to the ZIP file
					//							int len;
					//							while ((len = in.read(buf)) > 0) {
					//								this.zip.write(buf, 0, len);
					//							}
					//						}
					//
					//						// Complete the entry
					//						this.zip.closeEntry();
					//						this.zip.flush();
					//						if (in != null) {
					//							in.close();
					//						}
					//					}
				}catch(ProtocolException e){
					String msg = "Si e' verificato un errore durante l'esportazione della transazione con id:"+t.getIdTransazione();
					msg+=" Non sono riuscito a creare il file tracce.xml ("+e.getMessage()+")";
					throw new ExportException(msg, e);
				}
			} else {
				oneLine.add(CostantiExport.EMPTY_STRING);
			}
		}
	}

	private List<Object> getLine(TransazioneBean t) throws ExportException {
		List<Object> oneLine = new ArrayList<Object>();
		try {
			
			EsitoUtils esitoUtils = new EsitoUtils(SingleCsvFileExporter.log, t.getProtocollo());
			
			for (String keyColonna : this.colonneSelezionate) {
				if(keyColonna.equals(CostantiExport.KEY_COL_AZIONE)){
					if(!t.getPddRuolo().equals(PddRuolo.INTEGRATION_MANAGER)){
						if(StringUtils.isNotEmpty(t.getAzione())){
							oneLine.add(t.getAzione());
						} else {
							oneLine.add(CostantiExport.EMPTY_STRING);
						}
					}else {
						if(StringUtils.isNotEmpty(t.getOperazioneIm())){
							oneLine.add(CostantiExport.INTEGRATION_MANAGER_LABEL_CON_PARANTISI+ CostantiExport.WHITE_SPACE + t.getOperazioneIm());
						} else {
							oneLine.add(CostantiExport.EMPTY_STRING);
						}
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_CLUSTER_ID)){
					if(StringUtils.isNotEmpty(t.getClusterId())){
						oneLine.add(t.getClusterId());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_CREDENZIALI)){
					if(StringUtils.isNotEmpty(t.getCredenziali())){
						oneLine.add(t.getCredenziali());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_DATA_ID_MSG_RICHIESTA)){
					if(t.getDataIdMsgRichiesta()!= null){
						oneLine.add(this.formatDate(t.getDataIdMsgRichiesta())); 
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_DATA_ID_MSG_RISPOSTA)){
					if(t.getDataIdMsgRisposta() != null){
						oneLine.add(this.formatDate(t.getDataIdMsgRisposta())); 
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_DATA_ACCETTAZIONE_RICHIESTA)){
					if(t.getDataAccettazioneRichiesta() != null){
						oneLine.add(this.formatDate(t.getDataAccettazioneRichiesta())); 
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_DATA_INGRESSO_RICHIESTA)){
					if(t.getDataIngressoRichiesta() != null){
						oneLine.add(this.formatDate(t.getDataIngressoRichiesta())); 
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_DATA_ACCETTAZIONE_RISPOSTA)){
					if(t.getDataAccettazioneRisposta() != null){
						oneLine.add(this.formatDate(t.getDataAccettazioneRisposta())); 
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_DATA_INGRESSO_RISPOSTA)){
					if(t.getDataIngressoRisposta() != null){
						oneLine.add(this.formatDate(t.getDataIngressoRisposta())); 
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_DATA_USCITA_RICHIESTA)){
					if(t.getDataUscitaRichiesta() != null){
						oneLine.add(this.formatDate(t.getDataUscitaRichiesta())); 
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_DATA_USCITA_RISPOSTA)){
					if(t.getDataUscitaRisposta() != null){
						oneLine.add(this.formatDate(t.getDataUscitaRisposta())); 
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_DIAGNOSTICI)){
					if(this.exportDiagnostici){
						this.scriviDiagnostici(t, oneLine); 
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_DIGEST_RICHIESTA)){
					if(StringUtils.isNotEmpty(t.getDigestRichiesta())){
						oneLine.add(t.getDigestRichiesta());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_DIGEST_RISPOSTA)){
					if(StringUtils.isNotEmpty(t.getDigestRisposta())){
						oneLine.add(t.getDigestRisposta());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_DUPLICATI_RICHIESTA)){
					if(t.getDuplicatiRichiesta() >= 0){
						oneLine.add(t.getDuplicatiRichiesta() + "");
					} else {
						oneLine.add(CostantiExport.DUPLICATA_VALUE);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_DUPLICATI_RISPOSTA)){
					if(t.getDuplicatiRisposta() >= 0){
						oneLine.add(t.getDuplicatiRisposta() + "");
					} else {
						oneLine.add(CostantiExport.DUPLICATA_VALUE);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_ESITO)){
					oneLine.add(esitoUtils.getEsitoLabelFromValue(t.getEsito(),false));
				} else if(keyColonna.equals(CostantiExport.KEY_COL_ESITO_CONTESTO)){
					if(StringUtils.isNotEmpty(t.getEsitoContesto())){
						oneLine.add(esitoUtils.getEsitoContestoLabelFromValue(t.getEsitoContesto()));
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_EVENTI_GESTIONE)){
					if(StringUtils.isNotEmpty(t.getEventiGestione())){
						oneLine.add(t.getEventiGestione());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_FAULT_COOPERAZIONE)){
					if(StringUtils.isNotEmpty(t.getFaultCooperazione())){
						oneLine.add(t.getFaultCooperazionePretty());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_FAULT_INTEGRAZIONE)){
					if(StringUtils.isNotEmpty(t.getFaultIntegrazione())){
						oneLine.add(t.getFaultIntegrazionePretty());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_HEADER_PROTOCOLLO_RICHIESTA)){
					if(StringUtils.isNotEmpty(t.getHeaderProtocolloRichiesta())){
						oneLine.add(t.getHeaderProtocolloRichiesta());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_HEADER_PROTOCOLLO_RISPOSTA)){
					if(StringUtils.isNotEmpty(t.getHeaderProtocolloRisposta())){
						oneLine.add(t.getHeaderProtocolloRisposta());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_ID_ASINCRONO)){
					if(StringUtils.isNotEmpty(t.getIdAsincrono())){
						oneLine.add(t.getIdAsincrono());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_ID_COLLABORAZIONE)){
					if(StringUtils.isNotEmpty(t.getIdCollaborazione())){
						oneLine.add(t.getIdCollaborazione());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_ID_CORRELAZIONE_APPLICATIVA)){
					if(StringUtils.isNotEmpty(t.getIdCorrelazioneApplicativa())){
						oneLine.add(t.getIdCorrelazioneApplicativa());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_ID_CORRELAZIONEAPPLICATIVARISPOSTA)){
					if(StringUtils.isNotEmpty(t.getIdCorrelazioneApplicativaRisposta())){
						oneLine.add(t.getIdCorrelazioneApplicativaRisposta());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_ID_MESSAGGIO_RICHIESTA)){
					if(StringUtils.isNotEmpty(t.getIdMessaggioRichiesta())){
						oneLine.add(t.getIdMessaggioRichiesta());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_ID_MESSAGGIO_RISPOSTA)){
					if(StringUtils.isNotEmpty(t.getIdMessaggioRisposta())){
						oneLine.add(t.getIdMessaggioRisposta());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_ID_PORTA_SOGGETTO_EROGATORE)){
					if(StringUtils.isNotEmpty(t.getIdportaSoggettoErogatore())){
						oneLine.add(t.getIdportaSoggettoErogatore());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_ID_PORTA_SOGGETTO_FRUITORE)){
					if(StringUtils.isNotEmpty(t.getIdportaSoggettoFruitore())){
						oneLine.add(t.getIdportaSoggettoFruitore());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_ID_TRANSAZIONE)){
					if(StringUtils.isNotEmpty(t.getIdTransazione())){
						oneLine.add(t.getIdTransazione());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_INDIRIZZO_SOGGETTO_EROGATORE)){
					if(StringUtils.isNotEmpty(t.getIndirizzoSoggettoErogatore())){
						oneLine.add(t.getIndirizzoSoggettoErogatore());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_INDIRIZZO_SOGGETTO_FRUITORE)){
					if(StringUtils.isNotEmpty(t.getIndirizzoSoggettoFruitore())){
						oneLine.add(t.getIndirizzoSoggettoFruitore());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_LATENZA_PORTA)){
					if(t.getLatenzaPorta() != null){
						long v = t.getLatenzaPorta().longValue();
						if(v >=0)
							oneLine.add(DurataConverter.convertSystemTimeIntoString_millisecondi(t.getLatenzaPorta(), true));
						else 
							oneLine.add(CostantiExport.N_D);
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_LATENZA_SERVIZIO)){
					if(t.getLatenzaServizio() != null){
						long v = t.getLatenzaServizio().longValue();
						if(v >=0)
							oneLine.add(DurataConverter.convertSystemTimeIntoString_millisecondi(t.getLatenzaServizio(), true)); 
						else 
							oneLine.add(CostantiExport.N_D);
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_LATENZA_TOTALE)){
					if(t.getLatenzaTotale() != null){
						long v = t.getLatenzaTotale().longValue();
						if(v >=0)
							oneLine.add(DurataConverter.convertSystemTimeIntoString_millisecondi(t.getLatenzaTotale(), true)); 
						else 
							oneLine.add(CostantiExport.N_D);
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_LOCATION_CONNETTORE)){
					if(StringUtils.isNotEmpty(t.getLocationConnettore())){
						oneLine.add(t.getLocationConnettore());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_LOCATION_RICHIESTA)){
					if(StringUtils.isNotEmpty(t.getLocationRichiesta())){
						oneLine.add(t.getLocationRichiesta());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_LOCATION_RISPOSTA)){
					if(StringUtils.isNotEmpty(t.getLocationRisposta())){
						oneLine.add(t.getLocationRisposta());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_NOME_PORTA)){
					if(StringUtils.isNotEmpty(t.getNomePorta())){
						oneLine.add(t.getNomePorta());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_OPERAZIONE_IM)){
					if(StringUtils.isNotEmpty(t.getOperazioneIm())){
						oneLine.add(t.getOperazioneIm());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_PDD_CODICE)){
					if(StringUtils.isNotEmpty(t.getPddCodice())){
						oneLine.add(t.getPddCodice());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_PDD_RUOLO)){
					if(t.getPddRuolo()!= null){
						switch(t.getPddRuolo()){
						case APPLICATIVA:
							oneLine.add(CostantiExport.RUOLO_EROGAZIONE_LABEL);
							break;
						case DELEGATA:
							oneLine.add(CostantiExport.RUOLO_FUIZIONE_LABEL);
							break;
						case INTEGRATION_MANAGER:
							oneLine.add(CostantiExport.RUOLO_INTEGRATION_MANAGER_LABEL);
							break;
						case ROUTER:
							oneLine.add(CostantiExport.RUOLO_ROUTER_LABEL);
							break;
						}
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_PDD_SOGGETTO)){
					if(StringUtils.isNotEmpty(t.getPddNomeSoggetto())){
						oneLine.add(t.getSoggettoPdd());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_PROFILO_COLLABORAZIONE_OP2)){
					if(StringUtils.isNotEmpty(t.getProfiloCollaborazioneOp2())){
						oneLine.add(t.getProfiloCollaborazioneOp2());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_PROFILO_COLLABORAZIONE_PROT)){
					if(StringUtils.isNotEmpty(t.getProfiloCollaborazioneProt())){
						oneLine.add(t.getProfiloCollaborazioneProt());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_PROTOCOLLO)){
					if(StringUtils.isNotEmpty(t.getProtocollo())){
						oneLine.add(t.getProtocolloLabel());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_PROTOCOLLO_EXT_INFO_RICHIESTA)){
					if(StringUtils.isNotEmpty(t.getProtocolloExtInfoRichiesta())){
						oneLine.add(t.getProtocolloExtInfoRichiesta());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_PROTOCOLLO_EXT_INFO_RISPOSTA)){
					if(StringUtils.isNotEmpty(t.getProtocolloExtInfoRisposta())){
						oneLine.add(t.getProtocolloExtInfoRisposta());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_RICHIESTA_INGRESSO_BYTES)){
					if(t.getRichiestaIngressoBytes() != null){
						oneLine.add(Utility.fileSizeConverter(t.getRichiestaIngressoBytes())); 
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_RICHIESTA_USCITA_BYTES)){
					if(t.getRichiestaUscitaBytes() != null){
						oneLine.add(Utility.fileSizeConverter(t.getRichiestaUscitaBytes())); 
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_RISPOSTA_INGRESSO_BYTES)){
					if(t.getRispostaIngressoBytes() != null){
						oneLine.add(Utility.fileSizeConverter(t.getRispostaIngressoBytes())); 
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_RISPOSTA_USCITA_BYTES)){
					if(t.getRispostaUscitaBytes() != null){
						oneLine.add(Utility.fileSizeConverter(t.getRispostaUscitaBytes())); 
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_RUOLO_TRANSAZIONE)){
					if(t.getRuoloTransazione()==-1){
						oneLine.add(RuoloType.SCONOSCIUTO.toString());
					}
					else if(t.getRuoloTransazione()==1){
						oneLine.add(RuoloType.INVOCAZIONE_ONEWAY.toString());
					}
					else if(t.getRuoloTransazione()==2){
						oneLine.add(RuoloType.INVOCAZIONE_SINCRONA.toString());
					}
					else if(t.getRuoloTransazione()==3){
						oneLine.add(RuoloType.INVOCAZIONE_ASINCRONA_SIMMETRICA.toString());
					}
					else if(t.getRuoloTransazione()==4){
						oneLine.add(RuoloType.RISPOSTA_ASINCRONA_SIMMETRICA.toString());
					}
					else if(t.getRuoloTransazione()==5){
						oneLine.add(RuoloType.INVOCAZIONE_ASINCRONA_ASIMMETRICA.toString());
					}
					else if(t.getRuoloTransazione()==6){
						oneLine.add(RuoloType.RICHIESTA_STATO_ASINCRONA_ASIMMETRICA.toString());
					}
					else if(t.getRuoloTransazione()==7){
						oneLine.add(RuoloType.INTEGRATION_MANAGER.toString());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_SERVIZIO)){
					if(t.getPddRuolo().equals(PddRuolo.INTEGRATION_MANAGER)){
						if(StringUtils.isNotEmpty(t.getServizioApplicativoErogatore())){
							oneLine.add(t.getServizioApplicativoErogatore());
						} else {
							oneLine.add(CostantiExport.EMPTY_STRING);
						}
					} else {
						if(StringUtils.isNotEmpty(t.getNomeServizio())){
							oneLine.add(NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(t.getProtocollo(), t.getTipoServizio(), t.getNomeServizio(), t.getVersioneServizio()));
						} else {
							oneLine.add(CostantiExport.EMPTY_STRING);
						}
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_SERVIZIO_APPLICATIVO)){
					if(t.getPddRuolo().equals(PddRuolo.DELEGATA)) {
						if(StringUtils.isNotEmpty(t.getServizioApplicativoFruitore())){
							oneLine.add(t.getServizioApplicativoFruitore());
						} else {
							oneLine.add(CostantiExport.EMPTY_STRING);
						}
					} else {
						if(StringUtils.isNotEmpty(t.getServizioApplicativoErogatore())){
							oneLine.add(t.getServizioApplicativoErogatore());
						} else {
							oneLine.add(CostantiExport.EMPTY_STRING);
						}
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_SERVIZIO_APPLICATIVO_EROGATORE)){
					if(StringUtils.isNotEmpty(t.getServizioApplicativoErogatore())){
						oneLine.add(t.getServizioApplicativoErogatore());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_SERVIZIO_APPLICATIVO_FRUITORE)){
					if(StringUtils.isNotEmpty(t.getServizioApplicativoFruitore())){
						oneLine.add(t.getServizioApplicativoFruitore());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_SERVIZIO_CORRELATO)){
					if(StringUtils.isNotEmpty(t.getNomeServizioCorrelato())){
						oneLine.add(t.getTipoServizioCorrelato()+ CostantiExport.SEPARATORE_TIPO_NOME + t.getNomeServizioCorrelato());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_SOGGETTO_EROGATORE)){
					if(StringUtils.isNotEmpty(t.getNomeSoggettoErogatore())){
						oneLine.add(NamingUtils.getLabelSoggetto(t.getProtocollo(), t.getTipoSoggettoErogatore() , t.getNomeSoggettoErogatore()));
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_SOGGETTO_FRUITORE)){
					if(StringUtils.isNotEmpty(t.getNomeSoggettoFruitore())){
						oneLine.add(NamingUtils.getLabelSoggetto(t.getProtocollo(), t.getTipoSoggettoFruitore(), t.getNomeSoggettoFruitore()));
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_STATO)){
					if(StringUtils.isNotEmpty(t.getStato())){
						oneLine.add(t.getStato());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_TRACCIA_RICHIESTA)){
					if(this.exportTracce){
						this.scriviTraccia(t, oneLine, false);
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_TRACCIA_RISPOSTA)){
					if(this.exportTracce){
						this.scriviTraccia(t, oneLine, true);
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_URI_ACCORDO_SERVIZIO)){
					if(StringUtils.isNotEmpty(t.getUriAccordoServizio())){
						oneLine.add(t.getUriAccordoServizio());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				}  else if(keyColonna.equals(CostantiExport.KEY_COL_TIPO_API)){
					if(StringUtils.isNotEmpty(t.getTipoApiLabel())){
						oneLine.add(t.getTipoApiLabel());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				}  else if(keyColonna.equals(CostantiExport.KEY_COL_TAGS)){
					if(StringUtils.isNotEmpty(t.getGruppi())){
						oneLine.add(t.getGruppi());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_URL_INVOCAZIONE)){
					if(StringUtils.isNotEmpty(t.getUrlInvocazione())){
						oneLine.add(t.getUrlInvocazione());
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_INDIRIZZO_CLIENT)){
					if(StringUtils.isNotEmpty(t.getSocketClientAddress())){
						oneLine.add(t.getSocketClientAddress() + "");
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_X_FORWARDED_FOR)){
					if(StringUtils.isNotEmpty(t.getTransportClientAddress())){
						oneLine.add(t.getTransportClientAddress() + "");
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_TIPO_RICHIESTA)){
					if(StringUtils.isNotEmpty(t.getTipoRichiesta())){
						oneLine.add(t.getTipoRichiesta() + "");
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_CODICE_RISPOSTA_INGRESSO)){
					if(StringUtils.isNotEmpty(t.getCodiceRispostaIngresso())){
						oneLine.add(t.getCodiceRispostaIngresso() + "");
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_CODICE_RISPOSTA_USCITA)){
					if(StringUtils.isNotEmpty(t.getCodiceRispostaUscita())){
						oneLine.add(t.getCodiceRispostaUscita() + "");
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_RICHIEDENTE)){
					String richiedente = t.getRichiedente();
					if(StringUtils.isNotEmpty(richiedente)){
						oneLine.add(richiedente + "");
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_DETTAGLIO_ERRORE)){
					String dettaglio = t.getDettaglioErrore();
					if(StringUtils.isNotEmpty(dettaglio)){
						oneLine.add(dettaglio + "");
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_TOKEN_ISSUER)){
					if(StringUtils.isNotEmpty(t.getTokenIssuerLabel())){
						oneLine.add(t.getTokenIssuerLabel() + "");
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_TOKEN_SUBJECT)){
					if(StringUtils.isNotEmpty(t.getTokenSubjectLabel())){
						oneLine.add(t.getTokenSubjectLabel() + "");
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_TOKEN_CLIENT)){
					if(StringUtils.isNotEmpty(t.getTokenClientIdLabel())){
						oneLine.add(t.getTokenClientIdLabel() + "");
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_TOKEN_USERNAME)){
					if(StringUtils.isNotEmpty(t.getTokenUsernameLabel())){
						oneLine.add(t.getTokenUsernameLabel() + "");
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_TOKEN_EMAIL)){
					if(StringUtils.isNotEmpty(t.getTokenMailLabel())){
						oneLine.add(t.getTokenMailLabel() + "");
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_TOKEN_CLIENT_APPLICATIVO)){
					if(StringUtils.isNotEmpty(t.getTokenClientNameLabel())){
						oneLine.add(t.getTokenClientNameLabel() + "");
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else if(keyColonna.equals(CostantiExport.KEY_COL_TOKEN_CLIENT_SOGGETTO)){
					if(StringUtils.isNotEmpty(t.getTokenClientOrganizationNameLabel())){
						oneLine.add(t.getTokenClientOrganizationNameLabel() + "");
					} else {
						oneLine.add(CostantiExport.EMPTY_STRING);
					}
				} else {
					// colonna non riconosciuta
					throw new ExportException("Colonna ["+keyColonna+"] non definita.");
				}
			}
		} catch (ExportException e) {
			throw e;
		} catch (Exception e) {
			throw new ExportException("Errore durante la creazione della entry per la transazione ID ["+t.getIdTransazione()+"].",e);
		}

		return oneLine;
	}

	public void export(List<String> idtransazioni) throws ExportException{
		Date startTime = Calendar.getInstance().getTime();
		TransazioneExport te = null;
		try{
			DRDataSource dataSource = creaDatasourceTransazioni(this.colonneSelezionate, SingleCsvFileExporter.log);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

			SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
			SingleCsvFileExporter.log.debug("Avvio esportazione ...");
			SingleCsvFileExporter.log.debug("Inizio esportazione alle:"+time.format(startTime));
			List<TransazioneBean> transazioni = new ArrayList<TransazioneBean>();

			for (String id : idtransazioni) {
				transazioni.add(this.transazioniService.findByIdTransazione(id));
			}

			if(this.abilitaMarcamentoTemporale){
				te = this.transazioniExporterService.getByIntervallo(this.transazioniService.getSearch().getDataInizio(), this.transazioniService.getSearch().getDataFine());
				te.setExportState(ExportState.EXECUTING);
				te.setExportTimeStart(startTime);
				te.setDeleteState(DeleteState.UNDEFINED);

				if(this.fileName!=null)
					te.setNome(this.fileName);

				this.transazioniExporterService.store(te);
			}

			popolaDataSourceExport(dataSource,transazioni);

			JasperReportBuilder reportBuilder = creaReportBuilder(dataSource, SingleCsvFileExporter.log);

			if(this.formato.equals(CostantiExport.FORMATO_CSV_VALUE)){
				this.esportaCsv(this.outStream, reportBuilder, this.colonneSelezionate);
			} else if(this.formato.equals(CostantiExport.FORMATO_XLS_VALUE)){
				this.esportaXls(this.outStream, reportBuilder, this.colonneSelezionate);
			} else {
				throw new ExportException("Formato export ["+this.formato+"] non valido.");
			}

			Date dataFine = Calendar.getInstance().getTime();

			if(this.abilitaMarcamentoTemporale){
				te.setExportState(ExportState.COMPLETED);
				te.setExportTimeEnd(dataFine);
				this.transazioniExporterService.store(te);
			}

			SingleCsvFileExporter.log.debug("Fine esportazione alle:"+formatter.format(Calendar.getInstance().getTime()));
			SingleCsvFileExporter.log.debug("Esportazione completata.");

		}catch(ExportException e){
			SingleCsvFileExporter.log.error("Errore durante esportazione su file",e);

			if(this.abilitaMarcamentoTemporale){
				try{
					te.setExportState(ExportState.ERROR);
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					te.setExportError(sw.toString());

					this.transazioniExporterService.store(te);
				}catch(Exception ex){
					SingleCsvFileExporter.log.error("Errore durante il marcamento temporale.",ex);
				}
			}

			throw e;
		}catch(Exception e){
			SingleCsvFileExporter.log.error("Errore durante esportazione su file",e);
			throw new ExportException("Errore durante esportazione su file", e);
		}
	}

	/**
	 * Effettua l'esportazione utilizzando il searchform presente nel {@link ITransazioniService} transazioniService
	 * @throws Exception
	 */
	public void export() throws ExportException{

		Date startTime = Calendar.getInstance().getTime();
		TransazioneExport te = null;
		try{
			DRDataSource dataSource = creaDatasourceTransazioni(this.colonneSelezionate, SingleCsvFileExporter.log);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

			int start = 0;
			int limit = 100;

			SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
			SingleCsvFileExporter.log.debug("Avvio esportazione ...");
			SingleCsvFileExporter.log.debug("Inizio esportazione alle:"+time.format(startTime));

			if(this.abilitaMarcamentoTemporale){
				te = this.transazioniExporterService.getByIntervallo(this.transazioniService.getSearch().getDataInizio(), this.transazioniService.getSearch().getDataFine());
				te.setExportState(ExportState.EXECUTING);
				te.setExportTimeStart(startTime);
				te.setDeleteState(DeleteState.UNDEFINED);

				if(this.fileName!=null)
					te.setNome(this.fileName);

				this.transazioniExporterService.store(te);
			}

			List<TransazioneBean> transazioni = new ArrayList<TransazioneBean>();

			transazioni = this.transazioniService.findAll(start, limit);
			
			int totale = transazioni.size();
			boolean stopExport = false;

			try{
				while(transazioni.size()>0 && !stopExport){

					popolaDataSourceExport(dataSource,transazioni);

					start+=limit;
					
					// se la console non utilizza la count devo far l'export del numero massimo di transazioni previste in configurazione
					// puo' succedere che il numero di risultati totali non sia divisibile per il limit e l'ultima ricerca potrebbe aggingere un numero 
					// di risultati superiore a quanto previsto, riduco il limit al numero dei risultanti mancanti
					if(!this.useCount) {
						int residui = Costanti.SELECT_ITEM_VALORE_MASSIMO_ENTRIES - totale;
						if(residui > 0 && limit >= residui) {
							limit = residui;
						}
					}

					transazioni = this.transazioniService.findAll(start, limit);
					
					totale += transazioni.size();
					
					if(!this.useCount) {
						if(totale >= Costanti.SELECT_ITEM_VALORE_MASSIMO_ENTRIES) {
							stopExport = true;
							popolaDataSourceExport(dataSource,transazioni); // altrimenti l'ultima lista recuperata non viene inserita
						}
					}
				}

				JasperReportBuilder reportBuilder = creaReportBuilder(dataSource, SingleCsvFileExporter.log);

				if(this.formato.equals(CostantiExport.FORMATO_CSV_VALUE)){
					this.esportaCsv(this.outStream, reportBuilder, this.colonneSelezionate);
				} else if(this.formato.equals(CostantiExport.FORMATO_XLS_VALUE)){
					this.esportaXls(this.outStream, reportBuilder, this.colonneSelezionate);
				} else {
					throw new ExportException("Formato export ["+this.formato+"] non valido.");
				}

			}catch(IOException ioe){
				String msg = "Si e' verificato un errore durante l'esportazione delle transazioni";
				msg+=" Non sono riuscito a creare il file SearchFilter.xml ("+ioe.getMessage()+")";
				SingleCsvFileExporter.log.error(msg,ioe);
				throw new ExportException(msg, ioe);
			}

			Date dataFine = Calendar.getInstance().getTime();

			if(this.abilitaMarcamentoTemporale){
				te.setExportState(ExportState.COMPLETED);
				te.setExportTimeEnd(dataFine);
				this.transazioniExporterService.store(te);
			}

			SingleCsvFileExporter.log.debug("Fine esportazione alle:"+formatter.format(dataFine));
			SingleCsvFileExporter.log.debug("Esportazione completata.");

		}catch(ExportException e){
			SingleCsvFileExporter.log.error("Errore durante esportazione su file",e);

			if(this.abilitaMarcamentoTemporale){
				try{
					te.setExportState(ExportState.ERROR);
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					te.setExportError(sw.toString());

					this.transazioniExporterService.store(te);
				}catch(Exception ex){
					SingleCsvFileExporter.log.error("Errore durante il marcamento temporale.",ex);
				}
			}

			throw e;
		}catch(Exception e){
			SingleCsvFileExporter.log.error("Errore durante esportazione su file",e);
			throw new ExportException("Errore durante esportazione su file", e);
		}
	}


	@Override
	public String getFileName() {
		return this.fileName;
	}

	@Override
	public boolean isAbilitaMarcamentoTemporale() {
		return this.abilitaMarcamentoTemporale;
	}

	public void esportaXls(OutputStream outputStream, JasperReportBuilder report,List<String> colonneSelezionate) throws Exception{
		JasperXlsExporterBuilder builder = DynamicReports.export.xlsExporter(outputStream).setDetectCellType(true).setIgnorePageMargins(true)
				.setWhitePageBackground(false)
				.setRemoveEmptySpaceBetweenColumns(true);

		List<ColumnBuilder<?,?>> colonne = new ArrayList<ColumnBuilder<?,?>>();

		// generazione delle label delle colonne
		for (String keyColonna : colonneSelezionate) {
			ColonnaExport defColonna = ColonnaExportManager.getInstance().getColonna(keyColonna);
			String label = defColonna != null ? defColonna.getLabel() : keyColonna;
			TextColumnBuilder<String> nomeColumn = DynamicReports.col.column(label, keyColonna, DynamicReports.type.stringType());
			colonne.add(nomeColumn);
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

	public void esportaCsv(OutputStream outputStream, JasperReportBuilder report,List<String> colonneSelezionate) throws Exception{
		List<ColumnBuilder<?,?>> colonne = new ArrayList<ColumnBuilder<?,?>>();

		// generazione delle label delle colonne
		for (String keyColonna : colonneSelezionate) {
			ColonnaExport defColonna = ColonnaExportManager.getInstance().getColonna(keyColonna);
			String label = defColonna != null ? defColonna.getLabel() : keyColonna;
			TextColumnBuilder<String> nomeColumn = DynamicReports.col.column(label, keyColonna, DynamicReports.type.stringType());
			colonne.add(nomeColumn);
		}

		report
		.setColumnTitleStyle(Templates.columnTitleStyle)
		.addProperty("net.sf.jasperreports.export.csv.exclude.origin.keep.first.band.1", "columnHeader")
		.ignorePageWidth()
		.ignorePagination()
		.columns(colonne.toArray(new ColumnBuilder[colonne.size()]));


		JasperCsvExporterBuilder builder = DynamicReports.export.csvExporter(outputStream);
		report.toCsv(builder); 
	}

	public JasperReportBuilder creaReportBuilder(JRDataSource dataSource,Logger log) throws Exception{
		JasperReportBuilder builder = DynamicReports.report();
		builder.setDataSource(dataSource);
		return builder;
	}

	private DRDataSource creaDatasourceTransazioni (List<String> colonneSelezionate,Logger log) throws Exception {
		// Scittura Intestazione sono le chiavi delle colonne scelte
		List<String> header = new ArrayList<String>();
		header.addAll(colonneSelezionate);

		DRDataSource dataSource = new DRDataSource(header.toArray(new String[header.size()])); 

		//		// scorro le transazioni e in base alla colonna selezionata aggiungo il field corrispondente nell'ordine delle colonne scelto dall'utente
		//		for (TransazioneBean transazione : transazioni) {
		//			
		//			List<Object> oneLine = new ArrayList<Object>();
		//			
		//			dataSource.add(oneLine.toArray(new Object[oneLine.size()])); 
		//		}

		return dataSource;
	}
}
