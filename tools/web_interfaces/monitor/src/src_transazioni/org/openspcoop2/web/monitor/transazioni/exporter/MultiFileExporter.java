/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.transazioni.TransazioneExport;
import org.openspcoop2.core.transazioni.constants.DeleteState;
import org.openspcoop2.core.transazioni.constants.ExportState;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.protocol.basic.archive.ZIPUtils;
import org.openspcoop2.protocol.basic.diagnostica.DiagnosticSerializer;
import org.openspcoop2.protocol.basic.tracciamento.TracciaSerializer;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticDriver;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticSerializer;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaDriver;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneApplicativoServerBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.core.UtilityTransazioni;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniApplicativoServerService;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniExportService;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;
import org.slf4j.Logger;

/**
 * MultiFileExporter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class MultiFileExporter implements IExporter{

	/** Classe non più utilizzata, veniva agganciata all'export, valutarne l'eliminazione */
	
	private static final String ERRORE_EXPORT = "Si è verificato un errore durante l'esportazione della transazione con id:";
	
	private Logger log = null;
	private void logDebug(String msg) {
		if(this.log!=null) {
			this.log.debug(msg);
		}
	}
	private void logInfo(String msg) {
		if(this.log!=null) {
			this.log.info(msg);
		}
	}
	private void logError(String msg, Exception e) {
		if(this.log!=null) {
			this.log.error(msg,e);
		}
	}

	private boolean exportTracce = false;
	private boolean exportTracceUseProtocolSerialization = false;
	private boolean exportDiagnostici = false;
	private boolean exportDiagnosticiUseProtocolSerialization = false;
	private boolean exportContenuti = false;
	private boolean enableHeaderInfo = false;
	private boolean enableConsegneInfo = false;
	private boolean abilitaMarcamentoTemporale = false;
	private int maxTransactionPerFile;
	private boolean headersAsProperties = true;
	private boolean contenutiAsProperties = false;

	private ITransazioniService transazioniService;
	private ITransazioniApplicativoServerService transazioniApplicativoService;
	private ITracciaDriver tracciamentoService;
	private IDiagnosticDriver diagnosticiService;
	private ITransazioniExportService transazioniExporterService;

	private String filePrefix=null;
	private String exportDir=null;

	private void initMultiFileExporter(MultiExporterProperties properties, ITransazioniService transazioniService, ITracciaDriver tracciamentoService,IDiagnosticDriver diagnosticiService,ITransazioniExportService transazioniExport) {
		this.enableHeaderInfo = properties.isEnableHeaderInfo();
		this.enableConsegneInfo = properties.isEnableConsegneInfo();

		this.exportTracce = properties.isExportTracce();
		this.exportTracceUseProtocolSerialization = properties.isExportTracceUseProtocolSerialization();
		this.exportDiagnostici = properties.isExportDiagnostici();
		this.exportDiagnosticiUseProtocolSerialization = properties.isExportDiagnosticiUseProtocolSerialization();
		this.exportContenuti = properties.isExportContenuti();
		this.abilitaMarcamentoTemporale = properties.isAbilitaMarcamentoTemporaleEsportazione();
		this.headersAsProperties = properties.isHeadersAsProperties();
		this.contenutiAsProperties = properties.isContenutiAsProperties();

		this.maxTransactionPerFile = properties.getMaxTransazioniPerFile()>0 ? properties.getMaxTransazioniPerFile() : 100;

		this.tracciamentoService = tracciamentoService;
		this.transazioniService = transazioniService;
		this.transazioniApplicativoService = transazioniService.getTransazioniApplicativoServerService();
		this.diagnosticiService = diagnosticiService;
		this.transazioniExporterService = transazioniExport;

		this.logInfo("Multi File Exporter inizializzato:");
		this.logInfo("\t -esportazione Consegne abilitata: "+this.enableConsegneInfo);
		this.logInfo("\t -esportazione Tracce abilitata (useProtocolSerialization:"+this.exportTracceUseProtocolSerialization+"): "+this.exportTracce);
		this.logInfo("\t -esportazione Diagnostici abilitata (exportDiagnosticiUseProtocolSerialization:"+this.exportDiagnosticiUseProtocolSerialization+"): "+this.exportDiagnostici);
		this.logInfo("\t -esportazione Contenuti abilitata: "+this.exportContenuti);
		this.logInfo("\t -max transazioni per file: "+this.maxTransactionPerFile);
		this.logInfo("\t -abilita marcamento temporale: "+this.abilitaMarcamentoTemporale);
	}

	public MultiFileExporter(String exportDir,String fileName,MultiExporterProperties properties,ITransazioniService transazioniService, 
			ITracciaDriver tracciamentoService,IDiagnosticDriver diagnosticiService,ITransazioniExportService transazioniExport, Logger log) {
		this.log = log;
		
		if(this.log == null)
			this.log =  LoggerManager.getPddMonitorCoreLogger();
		
		initMultiFileExporter(properties, transazioniService, tracciamentoService, diagnosticiService,transazioniExport);

		if(fileName.endsWith(".zip")){
			this.filePrefix = fileName.substring(0, fileName.lastIndexOf("."));
		}else{
			this.filePrefix = fileName;
		}


		this.exportDir = exportDir;
	}


	public MultiFileExporter(String exportDir,String fileName,MultiExporterProperties properties,ITransazioniService transazioniService,
			ITracciaDriver tracciamentoService,IDiagnosticDriver diagnosticiService,ITransazioniExportService transazioniExport) {
		this(exportDir, fileName, properties, transazioniService, tracciamentoService, diagnosticiService, transazioniExport, null);
	}

	private void export(String rootDir, ZipOutputStream zip ,List<TransazioneBean> transazioni) throws Exception{

		byte[] buf = new byte[1024];

		InputStream in = null;

		for(TransazioneBean t: transazioni){

			String transazioneDir = rootDir+t.getIdTransazione()+File.separatorChar;

			boolean consegnaMultipla = false;
			try{
				EsitiProperties esitiProperties = EsitiProperties.getInstanceFromProtocolName(this.log, t.getProtocollo());
				EsitoTransazioneName esitoTransactionName = esitiProperties.getEsitoTransazioneName(t.getEsito());
				if(EsitoTransazioneName.isConsegnaMultipla(esitoTransactionName)) {
					consegnaMultipla = true;
				}
			} catch (Exception e) {
				String msg = ERRORE_EXPORT+t.getIdTransazione();
				msg+=" Non sono riuscito a comprendere l'esito della transazione ("+e.getMessage()+")";
				this.logError(msg,e);
				throw new ExportException(msg, e);
			}
			
			
			//manifest
			if(this.enableHeaderInfo){
				//scrivo il manifest
				try{
					zip.putNextEntry(new ZipEntry(transazioneDir+"manifest.xml"));
					UtilityTransazioni.writeManifestTransazione(t,zip);
					zip.flush();
					zip.closeEntry();
				}catch(Exception ioe){
					String msg = ERRORE_EXPORT+t.getIdTransazione();
					msg+=" Non sono riuscito a creare il file manifest.xml ("+ioe.getMessage()+")";
					this.logError(msg,ioe);
					throw new ExportException(msg, ioe);
				}
			}
			
			// manifest consegne
			if(consegnaMultipla && this.enableConsegneInfo){
								
				try{
					String dir = transazioneDir+"consegne"+File.separator;
					
					this.transazioniApplicativoService.setIdTransazione(t.getIdTransazione());
					this.transazioniApplicativoService.setProtocollo(t.getProtocollo());
					List<TransazioneApplicativoServerBean> listConsegne = this.transazioniApplicativoService.findAll();					
					if(listConsegne!=null && !listConsegne.isEmpty()) {
						for (TransazioneApplicativoServerBean tAS : listConsegne) {
							
							String connettoreNome = ZIPUtils.convertNameToSistemaOperativoCompatible(tAS.getNomeConnettoreDirectoryInArchivioZip());
							String dirConsegna = dir+connettoreNome+File.separator;
							
							zip.putNextEntry(new ZipEntry(dirConsegna+"manifest.xml"));
							UtilityTransazioni.writeManifestTransazioneApplicativoServer(t,tAS,zip);
							zip.flush();
							zip.closeEntry();
							
							if(this.exportDiagnostici) {
								//diagnostici
								exportDiagnostici(zip, t, dirConsegna, false, tAS.getServizioApplicativoErogatore());
							}
							
							if(this.exportContenuti){
								
								//fault
								String fault = tAS.getFault();
								if(StringUtils.isNotBlank(fault)){
									try{
										String ext = UtilityTransazioni.getExtension(tAS.getFormatoFault());
										zip.putNextEntry(new ZipEntry(dirConsegna+"fault."+ext));
										zip.write((fault != null ? fault.getBytes() : "".getBytes()));
										zip.flush();
										zip.closeEntry();
									}catch(Exception ioe){
										String msg = "Si e' verificato un errore durante l'esportazione dei contenuti della transazione con id:"+t.getIdTransazione();
										msg+=" Non sono riuscito a creare il file fault ("+ioe.getMessage()+")";
										this.logError(msg,ioe);
										throw new ExportException(msg, ioe);
									}				
								}
								
								//faultultimo errore
								String faultErrore = tAS.getFaultUltimoErrore();
								if(StringUtils.isNotBlank(faultErrore)){
									try{
										String ext = UtilityTransazioni.getExtension(tAS.getFormatoFaultUltimoErrore());
										zip.putNextEntry(new ZipEntry(dirConsegna+"faultUltimoErrore."+ext));
										zip.write((faultErrore != null ? faultErrore.getBytes() : "".getBytes()));
										zip.flush();
										zip.closeEntry();
									}catch(Exception ioe){
										String msg = "Si e' verificato un errore durante l'esportazione dei contenuti della transazione con id:"+t.getIdTransazione();
										msg+=" Non sono riuscito a creare il file faultUltimoErrore ("+ioe.getMessage()+")";
										this.logError(msg,ioe);
										throw new ExportException(msg, ioe);
									}				
								}
								
							}
						}
					}
				}catch(Exception e){
					String msg = ERRORE_EXPORT+t.getIdTransazione();
					msg+=" Non sono riuscito a recuperare le informazioni sulle consegne ("+e.getMessage()+")";
					this.logError(msg,e);
					throw new ExportException(msg, e);
				}
				
			}

			//tracce
			if(this.exportTracce){
				//devo impostare solo l'idtransazione
				/**filter.setIdMessaggio(this.diagnosticiBean.getIdMessaggio());*/	
				Map<String, String> properties = new HashMap<>();
				properties.put("id_transazione", t.getIdTransazione());

				Traccia tracciaRichiesta = null;
				Traccia tracciaRisposta  = null;
				ArrayList<Traccia> tracce = new ArrayList<>();
				try{
					tracciaRichiesta=this.tracciamentoService.getTraccia(RuoloMessaggio.RICHIESTA,properties);
					tracce.add(tracciaRichiesta);
				}catch(DriverTracciamentoException e){
					String msg = ERRORE_EXPORT+t.getIdTransazione();
					msg+=" Non sono riuscito a recuperare la traccia di richiesta ("+e.getMessage()+")";
					throw new ExportException(msg, e);
				}catch(DriverTracciamentoNotFoundException e){
					//ignore
				}
				try{
					tracciaRisposta = this.tracciamentoService.getTraccia(RuoloMessaggio.RISPOSTA,properties);
					tracce.add(tracciaRisposta);
				}catch(DriverTracciamentoException e){
					String msg = ERRORE_EXPORT+t.getIdTransazione();
					msg+=" Non sono riuscito a recuperare la traccia di risposta ("+e.getMessage()+")";
					throw new ExportException(msg, e);
				}catch(DriverTracciamentoNotFoundException e){
					//ignore
				}
				if(!tracce.isEmpty()){
					// Add ZIP entry to output stream.
					try{
						zip.putNextEntry(new ZipEntry(transazioneDir+"tracce.xml"));

						for (int j = 0; j < tracce.size(); j++) {
							Traccia tr = tracce.get(j);
							String newLine = j > 0 ? "\n\n" : "";

							IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tr.getProtocollo());
							ITracciaSerializer tracciaBuilder = null;
							if(this.exportTracceUseProtocolSerialization) {
								tracciaBuilder = pf.createTracciaSerializer();
							}
							else {
								tracciaBuilder = new TracciaSerializer(pf);
							}
							tracciaBuilder.setOmitXmlDeclaration(true);
							String traccia = tracciaBuilder.toString(tr,TipoSerializzazione.DEFAULT);

							in = new ByteArrayInputStream((newLine + traccia).getBytes());

							// Transfer bytes from the input stream to the ZIP file
							int len;
							while ((len = in.read(buf)) > 0) {
								zip.write(buf, 0, len);
							}
						}

						// Complete the entry
						zip.flush();
						zip.closeEntry();
						if(in!=null)
							in.close();
					}catch(Exception e){
						String msg = ERRORE_EXPORT+t.getIdTransazione();
						msg+=" Non sono riuscito a creare il file tracce.xml ("+e.getMessage()+")";
						throw new ExportException(msg, e);
					}

				}
			}

			//diagnostici
			if(this.exportDiagnostici){
				exportDiagnostici(zip, t, transazioneDir, consegnaMultipla, null);
			}
			//contenuti e fault
			if(this.exportContenuti){

				//fault - integrazione
				String fault = t.getFaultIntegrazione();
				if(StringUtils.isNotBlank(fault)){
					try{
						String ext = UtilityTransazioni.getExtension(t.getFormatoFaultIntegrazione());
						zip.putNextEntry(new ZipEntry(transazioneDir+"faultIntegrazione."+ext));
						zip.write((fault != null ? fault.getBytes() : "".getBytes()));
						zip.flush();
						zip.closeEntry();
					}catch(Exception ioe){
						String msg = "Si e' verificato un errore durante l'esportazione dei contenuti della transazione con id:"+t.getIdTransazione();
						msg+=" Non sono riuscito a creare il file faultIntegrazione ("+ioe.getMessage()+")";
						this.logError(msg,ioe);
						throw new ExportException(msg, ioe);
					}

				}
				
				//fault - cooperazione
				fault = t.getFaultCooperazione();
				if(StringUtils.isNotBlank(fault)){
					try{
						String ext = UtilityTransazioni.getExtension(t.getFormatoFaultCooperazione());
						zip.putNextEntry(new ZipEntry(transazioneDir+"faultCooperazione."+ext));
						zip.write((fault != null ? fault.getBytes() : "".getBytes()));
						zip.flush();
						zip.closeEntry();
					}catch(Exception ioe){
						String msg = "Si e' verificato un errore durante l'esportazione dei contenuti della transazione con id:"+t.getIdTransazione();
						msg+=" Non sono riuscito a creare il file faultCooperazione ("+ioe.getMessage()+")";
						this.logError(msg,ioe);
						throw new ExportException(msg, ioe);
					}

				}

				//contenuti

				TipoMessaggio [] listTipiDaEsportare = TipoMessaggio.values();
				for (int i = 0; i < listTipiDaEsportare.length; i++) {
					SingleFileExporter.exportContenuti(this.log, t, zip, transazioneDir, true, this.transazioniService, listTipiDaEsportare[i],
							this.headersAsProperties, this.contenutiAsProperties);
				}

			}

		}//chiudo for transazioni
	}

	private void exportDiagnostici( ZipOutputStream zip , TransazioneBean t, String dir, boolean forceNullApplicativo, String servizioApplicativo) throws ExportException {
		InputStream in = null;
		byte[] buf = new byte[1024];
		try{
			FiltroRicercaDiagnosticiConPaginazione filter = new FiltroRicercaDiagnosticiConPaginazione();


			//devo impostare solo l'idtransazione
			/**filter.setIdMessaggio(this.diagnosticiBean.getIdMessaggio());*/	
			Map<String, String> properties = new HashMap<>();
			properties.put("id_transazione", t.getIdTransazione());
			filter.setProperties(properties);

			//non necessario, id_transazione e' sufficiente
			/**filter.setIdentificativoPorta(search.getIdentificativoPorta());*/

			List<MsgDiagnostico> list = this.diagnosticiService.getMessaggiDiagnostici(filter);
			// Add ZIP entry to output stream.
			zip.putNextEntry(new ZipEntry(dir+"diagnostici.xml"));

			for (int j = 0; j < list.size(); j++) {
				MsgDiagnostico msg = list.get(j);
				String newLine = j > 0 ? "\n\n" : "";

				IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(msg.getProtocollo());
				IDiagnosticSerializer diagnosticoBuilder = null;
				if(this.exportDiagnosticiUseProtocolSerialization) {
					diagnosticoBuilder = pf.createDiagnosticSerializer();
				}
				else {
					diagnosticoBuilder = new DiagnosticSerializer(pf);
				}
				diagnosticoBuilder.setOmitXmlDeclaration(true);
				String diagnostico = diagnosticoBuilder.toString(msg,TipoSerializzazione.XML);

				in = new ByteArrayInputStream((newLine + diagnostico).getBytes());

				// Transfer bytes from the input stream to the ZIP file
				int len;
				while ((len = in.read(buf)) > 0) {
					zip.write(buf, 0, len);
				}

			}
			// Complete the entry
			zip.flush();
			zip.closeEntry();
			if(in!=null)
				in.close();
		}catch(Exception e){
			String msg = ERRORE_EXPORT+t.getIdTransazione();
			msg+=" Non sono riuscito a creare il file diagnostici.xml ("+e.getMessage()+")";
			throw new ExportException(msg, e);
		}
	}

	/**
	 * Effettua l'esportazione utilizzando il searchform presente nel {@link ITransazioniService} transazioniService
	 * @throws Exception
	 */
	public void export() throws Exception{

		Date startTime = Calendar.getInstance().getTime();
		TransazioneExport te = null;

		try{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

			SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			this.logDebug("Avvio esportazione ...");
			this.logDebug("Inizio esportazione alle:"+time.format(startTime));
			List<TransazioneBean> transazioni = new ArrayList<>();

			int totCount = this.transazioniService.totalCount();

			double div = ((double)totCount)/((double)this.maxTransactionPerFile);
			double totFile = Math.ceil(div);

			File fileExportDir = new File(this.exportDir);
			if(!fileExportDir.exists()){
				fileExportDir.mkdir();
			}

			if(this.abilitaMarcamentoTemporale){
				te = this.transazioniExporterService.getByIntervallo((this.transazioniService.getSearch()).getDataInizio(), (this.transazioniService.getSearch()).getDataFine());
				te.setExportState(ExportState.EXECUTING);
				te.setExportTimeStart(startTime);
				te.setDeleteState(DeleteState.UNDEFINED);

				if(this.filePrefix!=null)
					te.setNome(this.filePrefix);

				this.transazioniExporterService.store(te);
			}

			this.logDebug("Ho trovato "+totCount+" transazioni che verranno inserite su "+totFile+" file con max "+this.maxTransactionPerFile+" transazioni per file.");

			//utilizzo un 'buffer' di 100 righe per volta, se il maxtransaction e' minore allora
			//significa che leggero tutte le transazioni ammesse per questo file in una sola volta
			int limit = this.maxTransactionPerFile > 100 ? 100 : this.maxTransactionPerFile;

			int remaining = totCount;
			for(int i=0; i < totFile ; i++){

				//int i = 0;// progressivo per evitare entry duplicate nel file zip
				// Create a buffer for reading the files
				String fileName = this.filePrefix+"_"+(i+1)+".zip";

				String filesDirPath = fileExportDir+File.separator+this.filePrefix;
				File filesDir = new File(filesDirPath);

				if(!filesDir.exists()){
					this.logDebug("creo directory "+filesDir.getAbsolutePath());
					filesDir.mkdir();
				}

				//search filter
				File searchFilter = new File(filesDirPath+File.separator+"SearchFilter.xml");
				if(!searchFilter.exists()){
					try(FileOutputStream fos = new FileOutputStream(searchFilter)){
						UtilityTransazioni.writeSearchFilterXml(this.transazioniService.getSearch(),fos);
						fos.flush();
					}
				}

				String filePath = fileExportDir+File.separator+this.filePrefix+File.separator+fileName;
				File f = new File(filePath);
				this.logDebug("creo nuovo file "+f.getAbsolutePath());

				try (FileOutputStream fos = new FileOutputStream(f);
					ZipOutputStream	zip = new ZipOutputStream(fos);){
				
					String rootDir = "Transazioni"+File.separatorChar;
	
					int lette = 0;
	
					int start = i*this.maxTransactionPerFile;
					while(!(transazioni=this.transazioniService.findAll(start, limit,SortOrder.ASC)).isEmpty()){
	
						/**transazioni = this.transazioniService.findAll(start, limit,OrderBy.ASC);*/
						this.logDebug(" lette [ "+start+" - "+(start+limit)+"] di "+totCount+" transazioni da inserire in file "+fileName+"...");
	
						export(rootDir,zip,transazioni);
	
						this.logDebug(" inserite ["+transazioni.size()+"] nel file "+fileName+".");
	
	
	
						start+=limit;
						remaining-=transazioni.size();
						lette += transazioni.size();
						//se ho gia' letto tutte le transazioni
						//che posso includere in questo file
						if(lette>=this.maxTransactionPerFile){
							this.logDebug("Ho inserito il numero massimo di transazioni ammesse ("+lette+") nel file "+fileName+" chiudo file.");
							break;
						}
						this.logDebug(" transazioni rimanenti da processare "+remaining);
						//leggo le ultime che mi rimangono
						if(remaining<=limit){
							limit=remaining;
							this.logDebug("devo leggere ancora "+remaining+" transazioni partendo da offset:"+start);
						}
	
					}
	
					zip.flush();
				}
				
			}

			Date dataFine = Calendar.getInstance().getTime();

			if(this.abilitaMarcamentoTemporale){
				te.setExportState(ExportState.COMPLETED);
				te.setExportTimeEnd(dataFine);
				this.transazioniExporterService.store(te);
			}
			this.logDebug("Fine esportazione alle:"+formatter.format(Calendar.getInstance().getTime()));
			this.logDebug("Esportazione completata.");

		}catch(Exception e){
			this.logError("Errore durante esportazione su file",e);
			if(this.abilitaMarcamentoTemporale){
				try{
					te.setExportState(ExportState.ERROR);
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					te.setExportError(sw.toString());

					this.transazioniExporterService.store(te);
				}catch(Exception ex){
					this.logError("Errore durante il marcamento temporale.",ex);
				}
			}
			throw e;
		}
	}


	@Override
	public String getFileName() {
		return this.filePrefix;
	}


	@Override
	public boolean isAbilitaMarcamentoTemporale() {
		return this.abilitaMarcamentoTemporale;
	}
}
