/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
import java.util.Hashtable;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
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
import org.slf4j.Logger;

import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.TransazioneExport;
import org.openspcoop2.core.transazioni.constants.DeleteState;
import org.openspcoop2.core.transazioni.constants.ExportState;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.MimeTypeUtils;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.core.UtilityTransazioni;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniExportService;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;

/**
 * MultiFileExporter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class MultiFileExporter implements IExporter{

	private transient Logger log = null;

	private boolean exportTracce = false;
	private boolean exportDiagnostici = false;
	private boolean exportContenuti = false;
	private boolean enableHeaderInfo = false;
	private boolean mimeThrowExceptionIfNotFound = false;
	private boolean abilitaMarcamentoTemporale = false;
	private int maxTransactionPerFile;

	private ITransazioniService transazioniService;
	private ITracciaDriver tracciamentoService;
	private IDiagnosticDriver diagnosticiService;
	private ITransazioniExportService transazioniExporterService;

	private String filePrefix=null;
	private String exportDir=null;

	private void _MultiFileExporter(MultiExporterProperties properties, ITransazioniService transazioniService, ITracciaDriver tracciamentoService,IDiagnosticDriver diagnosticiService,ITransazioniExportService transazioniExport) {
		this.enableHeaderInfo = properties.isEnableHeaderInfo();

		this.exportTracce = properties.isExportTracce();
		this.exportDiagnostici = properties.isExportDiagnostici();
		this.exportContenuti = properties.isExportContenuti();
		this.mimeThrowExceptionIfNotFound = properties.isMimeThrowExceptionIfNotFound();
		this.abilitaMarcamentoTemporale = properties.isAbilitaMarcamentoTemporaleEsportazione();

		this.maxTransactionPerFile = properties.getMaxTransazioniPerFile()>0 ? properties.getMaxTransazioniPerFile() : 100;

		this.tracciamentoService = tracciamentoService;
		this.transazioniService = transazioniService;
		this.diagnosticiService = diagnosticiService;
		this.transazioniExporterService = transazioniExport;

		this.log.info("Multi File Exporter inizializzato:");
		this.log.info("\t -esportazione Tracce      abilitata: "+this.exportTracce);
		this.log.info("\t -esportazione Contenuti   abilitata: "+this.exportContenuti);
		this.log.info("\t -esportazione Diagnostici abilitata: "+this.exportDiagnostici);
		this.log.info("\t -max transazioni per file : "+this.maxTransactionPerFile);
		this.log.info("\t -abilita marcamento temporale : "+this.abilitaMarcamentoTemporale);
		this.log.info("\t -MimeType handling (mime.throwExceptionIfMappingNotFound):"+this.mimeThrowExceptionIfNotFound);
	}

	public MultiFileExporter(String exportDir,String fileName,MultiExporterProperties properties,ITransazioniService transazioniService, 
			ITracciaDriver tracciamentoService,IDiagnosticDriver diagnosticiService,ITransazioniExportService transazioniExport, Logger log) throws Exception{
		this.log = log;
		
		if(this.log == null)
			this.log =  LoggerManager.getPddMonitorCoreLogger();
		
		_MultiFileExporter(properties, transazioniService, tracciamentoService, diagnosticiService,transazioniExport);

		if(fileName.endsWith(".zip")){
			this.filePrefix = fileName.substring(0, fileName.lastIndexOf("."));
		}else{
			this.filePrefix = fileName;
		}


		this.exportDir = exportDir;
	}


	public MultiFileExporter(String exportDir,String fileName,MultiExporterProperties properties,ITransazioniService transazioniService,
			ITracciaDriver tracciamentoService,IDiagnosticDriver diagnosticiService,ITransazioniExportService transazioniExport) throws Exception{
		this(exportDir, fileName, properties, transazioniService, tracciamentoService, diagnosticiService, transazioniExport, null);
	}

	private void export(String rootDir, ZipOutputStream zip ,List<TransazioneBean> transazioni) throws Exception{

		byte[] buf = new byte[1024];

		InputStream in = null;

		for(TransazioneBean t: transazioni){

			String transazioneDir = rootDir+t.getIdTransazione()+File.separatorChar;

			//manifest
			if(this.enableHeaderInfo){
				//scrivo il manifest
				try{
					zip.putNextEntry(new ZipEntry(transazioneDir+"manifest.xml"));
					UtilityTransazioni.writeManifestTransazione(t,zip);
					zip.flush();
					zip.closeEntry();
				}catch(Exception ioe){
					String msg = "Si e' verificato un errore durante l'esportazione della transazione con id:"+t.getIdTransazione();
					msg+=" Non sono riuscito a creare il file manifest.xml ("+ioe.getMessage()+")";
					this.log.error(msg,ioe);
					throw new ExportException(msg, ioe);
				}
			}

			//tracce
			if(this.exportTracce){
				//devo impostare solo l'idtransazione
				//filter.setIdEgov(this.diagnosticiBean.getIdEgov());	
				Hashtable<String, String> properties = new Hashtable<String, String>();
				properties.put("id_transazione", t.getIdTransazione());

				Traccia tracciaRichiesta = null;
				Traccia tracciaRisposta  = null;
				ArrayList<Traccia> tracce = new ArrayList<Traccia>();
				try{
					tracciaRichiesta=this.tracciamentoService.getTraccia(RuoloMessaggio.RICHIESTA,properties);
					tracce.add(tracciaRichiesta);
				}catch(DriverTracciamentoException e){
					String msg = "Si e' verificato un errore durante l'esportazione della transazione con id:"+t.getIdTransazione();
					msg+=" Non sono riuscito a recuperare la traccia di richiesta ("+e.getMessage()+")";
					throw new ExportException(msg, e);
				}catch(DriverTracciamentoNotFoundException e){
					//ignore
				}
				try{
					tracciaRisposta = this.tracciamentoService.getTraccia(RuoloMessaggio.RISPOSTA,properties);
					tracce.add(tracciaRisposta);
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
						zip.putNextEntry(new ZipEntry(transazioneDir+"tracce.xml"));

						for (int j = 0; j < tracce.size(); j++) {
							Traccia tr = tracce.get(j);
							String newLine = j > 0 ? "\n\n" : "";

							IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tr.getProtocollo());
							ITracciaSerializer tracciaBuilder = pf.createTracciaSerializer();
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
						String msg = "Si e' verificato un errore durante l'esportazione della transazione con id:"+t.getIdTransazione();
						msg+=" Non sono riuscito a creare il file tracce.xml ("+e.getMessage()+")";
						throw new ExportException(msg, e);
					}

				}
			}

			//diagnostici
			if(this.exportDiagnostici){
				try{
					FiltroRicercaDiagnosticiConPaginazione filter = new FiltroRicercaDiagnosticiConPaginazione();


					//devo impostare solo l'idtransazione
					//filter.setIdEgov(this.diagnosticiBean.getIdEgov());	
					Hashtable<String, String> properties = new Hashtable<String, String>();
					properties.put("id_transazione", t.getIdTransazione());
					filter.setProperties(properties);

					//non necessario, id_transazione e' sufficiente
					//filter.setIdentificativoPorta(search.getIdentificativoPorta());

					List<MsgDiagnostico> list = this.diagnosticiService.getMessaggiDiagnostici(filter);
					// Add ZIP entry to output stream.
					zip.putNextEntry(new ZipEntry(transazioneDir+"diagnostici.xml"));

					for (int j = 0; j < list.size(); j++) {
						MsgDiagnostico msg = list.get(j);
						String newLine = j > 0 ? "\n\n" : "";

						IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(msg.getProtocollo());
						IDiagnosticSerializer diagnosticoBuilder = pf.createDiagnosticSerializer();
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
					String msg = "Si e' verificato un errore durante l'esportazione della transazione con id:"+t.getIdTransazione();
					msg+=" Non sono riuscito a creare il file diagnostici.xml ("+e.getMessage()+")";
					throw new ExportException(msg, e);
				}
			}
			//contenuti e fault
			if(this.exportContenuti){

				//fault - integrazione
				String fault = t.getFaultIntegrazione();
				if(StringUtils.isNotBlank(fault)){
					try{
						zip.putNextEntry(new ZipEntry(transazioneDir+"faultIntegrazione.xml"));
						zip.write((fault != null ? fault.getBytes() : "".getBytes()));
						zip.flush();
						zip.closeEntry();
					}catch(Exception ioe){
						String msg = "Si e' verificato un errore durante l'esportazione dei contenuti della transazione con id:"+t.getIdTransazione();
						msg+=" Non sono riuscito a creare il file faultIntegrazione.xml ("+ioe.getMessage()+")";
						this.log.error(msg,ioe);
						throw new ExportException(msg, ioe);
					}

				}
				
				//fault - cooperazione
				fault = t.getFaultCooperazione();
				if(StringUtils.isNotBlank(fault)){
					try{
						zip.putNextEntry(new ZipEntry(transazioneDir+"faultCooperazione.xml"));
						zip.write((fault != null ? fault.getBytes() : "".getBytes()));
						zip.flush();
						zip.closeEntry();
					}catch(Exception ioe){
						String msg = "Si e' verificato un errore durante l'esportazione dei contenuti della transazione con id:"+t.getIdTransazione();
						msg+=" Non sono riuscito a creare il file faultCooperazione.xml ("+ioe.getMessage()+")";
						this.log.error(msg,ioe);
						throw new ExportException(msg, ioe);
					}

				}

				//contenuti

				exportContenuti(t, zip, transazioneDir, this.transazioniService, TipoMessaggio.RICHIESTA_INGRESSO);
				exportContenuti(t, zip, transazioneDir, this.transazioniService, TipoMessaggio.RICHIESTA_USCITA);
				exportContenuti(t, zip, transazioneDir, this.transazioniService, TipoMessaggio.RISPOSTA_INGRESSO);
				exportContenuti(t, zip, transazioneDir, this.transazioniService, TipoMessaggio.RISPOSTA_USCITA);
			}

		}//chiudo for transazioni
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
			this.log.debug("Avvio esportazione ...");
			this.log.debug("Inizio esportazione alle:"+time.format(startTime));
			List<TransazioneBean> transazioni = new ArrayList<TransazioneBean>();

			int totCount = this.transazioniService.totalCount();

			double div = ((double)totCount)/((double)this.maxTransactionPerFile);
			double totFile = Math.ceil(div);

			File exportDir = new File(this.exportDir);
			if(!exportDir.exists()){
				exportDir.mkdir();
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

			this.log.debug("Ho trovato "+totCount+" transazioni che verranno inserite su "+totFile+" file con max "+this.maxTransactionPerFile+" transazioni per file.");

			//utilizzo un 'buffer' di 100 righe per volta, se il maxtransaction e' minore allora
			//significa che leggero tutte le transazioni ammesse per questo file in una sola volta
			int limit = this.maxTransactionPerFile > 100 ? 100 : this.maxTransactionPerFile;

			int remaining = totCount;
			for(int i=0; i < totFile ; i++){

				//int i = 0;// progressivo per evitare entry duplicate nel file zip
				// Create a buffer for reading the files
				String fileName = this.filePrefix+"_"+(i+1)+".zip";

				String filesDirPath = exportDir+File.separator+this.filePrefix;
				File filesDir = new File(filesDirPath);

				if(!filesDir.exists()){
					this.log.debug("creo directory "+filesDir.getAbsolutePath());
					filesDir.mkdir();
				}

				//search filter
				File searchFilter = new File(filesDirPath+File.separator+"SearchFilter.xml");
				if(!searchFilter.exists()){
					FileOutputStream fos = new FileOutputStream(searchFilter);
					UtilityTransazioni.writeSearchFilterXml(this.transazioniService.getSearch(),fos);
					fos.flush();
					fos.close();
				}

				String filePath = exportDir+File.separator+this.filePrefix+File.separator+fileName;
				File f = new File(filePath);
				this.log.debug("creo nuovo file "+f.getAbsolutePath());
				FileOutputStream fos = new FileOutputStream(f);
				ZipOutputStream zip = new ZipOutputStream(fos);

				String rootDir = "TransazioniPdD"+File.separatorChar;

				int lette = 0;

				int start = i*this.maxTransactionPerFile;
				while((transazioni=this.transazioniService.findAll(start, limit,SortOrder.ASC)).size()>0){

					//transazioni = this.transazioniService.findAll(start, limit,OrderBy.ASC);
					this.log.debug(" lette [ "+start+" - "+(start+limit)+"] di "+totCount+" transazioni da inserire in file "+fileName+"...");

					export(rootDir,zip,transazioni);

					this.log.debug(" inserite ["+transazioni.size()+"] nel file "+fileName+".");



					start+=limit;
					remaining-=transazioni.size();
					lette += transazioni.size();
					//se ho gia' letto tutte le transazioni
					//che posso includere in questo file
					if(lette>=this.maxTransactionPerFile){
						this.log.debug("Ho inserito il numero massimo di transazioni ammesse ("+lette+") nel file "+fileName+" chiudo file.");
						break;
					}
					this.log.debug(" transazioni rimanenti da processare "+remaining);
					//leggo le ultime che mi rimangono
					if(remaining<=limit){
						limit=remaining;
						this.log.debug("devo leggere ancora "+remaining+" transazioni partendo da offset:"+start);
					}

				}

				zip.flush();
				zip.close();
			}

			Date dataFine = Calendar.getInstance().getTime();

			if(this.abilitaMarcamentoTemporale){
				te.setExportState(ExportState.COMPLETED);
				te.setExportTimeEnd(dataFine);
				this.transazioniExporterService.store(te);
			}
			this.log.debug("Fine esportazione alle:"+formatter.format(Calendar.getInstance().getTime()));
			this.log.debug("Esportazione completata.");

		}catch(Exception e){
			this.log.error("Errore durante esportazione su file",e);
			if(this.abilitaMarcamentoTemporale){
				try{
					te.setExportState(ExportState.ERROR);
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					te.setExportError(sw.toString());

					this.transazioniExporterService.store(te);
				}catch(Exception ex){
					this.log.error("Errore durante il marcamento temporale.",ex);
				}
			}
			throw e;
		}
	}

	private void exportContenuti(Transazione t, ZipOutputStream zip,String dirPath,ITransazioniService service,TipoMessaggio tipo) throws ExportException{

		String contenutiDir = dirPath+File.separator+"contenuti"+File.separator;

		DumpMessaggio dump=null;
		try {
			dump = service.getDumpMessaggio(t.getIdTransazione(), tipo);
		} catch (Exception e) {
			String msg = "Si e' verificato un errore durante l'esportazione dei contenuti ("+tipo.toString()+") della transazione con id:"+t.getIdTransazione();
			msg+=" Non sono riuscito a recuperare il messaggio di dump ("+e.getMessage()+")";
			this.log.error(msg,e);
			throw new ExportException(msg, e);
		}
		if(dump!=null){
			String dir = contenutiDir+tipo.toString()+File.separator;

			//envelope
			if(dump.getBody()!=null){
				try{
					zip.putNextEntry(new ZipEntry(dir+"envelope.xml"));
					zip.write(dump.getBody());
					zip.flush();
					zip.closeEntry();
				}catch(Exception ioe){
					String msg = "Si e' verificato un errore durante l'esportazione dei contenuti ("+tipo.toString()+") della transazione con id:"+t.getIdTransazione();
					msg+=" Non sono riuscito a creare il file envelope.xml ("+ioe.getMessage()+")";
					this.log.error(msg,ioe);
					throw new ExportException(msg, ioe);
				}
			}
			//header trasporto
			List<DumpHeaderTrasporto> headers = service.getHeaderTrasporto(dump.getIdTransazione(), dump.getTipoMessaggio(), dump.getId());
			if(headers.size()>0){
				try{
					zip.putNextEntry(new ZipEntry(dir+"header.xml"));
					UtilityTransazioni.writeHeadersTrasportoXml(headers,zip);
					zip.flush();
					zip.closeEntry();
				}catch(Exception ioe){
					String msg = "Si e' verificato un errore durante l'esportazione dei contenuti ("+tipo.toString()+") della transazione con id:"+t.getIdTransazione();
					msg+=" Non sono riuscito a creare il file header.xml ("+ioe.getMessage()+")";
					this.log.error(msg,ioe);
					throw new ExportException(msg, ioe);
				}

			}
			//contenuti
			List<DumpContenuto> contenuti = service.getContenutiSpecifici(dump.getIdTransazione(), dump.getTipoMessaggio(), dump.getId());
			if(contenuti.size()>0){
				try{
					zip.putNextEntry(new ZipEntry(dir+"contenuti.xml"));

					UtilityTransazioni.writeContenutiXml(contenuti,zip);
					zip.flush();
					zip.closeEntry();
				}catch(Exception ioe){
					String msg = "Si e' verificato un errore durante l'esportazione dei contenuti ("+tipo.toString()+") della transazione con id:"+t.getIdTransazione();
					msg+=" Non sono riuscito a creare il file contenuti.xml ("+ioe.getMessage()+")";
					this.log.error(msg,ioe);
					throw new ExportException(msg, ioe);
				}

			}
			//allegati
			List<DumpAllegato> allegati = service.getAllegatiMessaggio(dump.getIdTransazione(), dump.getTipoMessaggio(), dump.getId());
			if(allegati.size()>0){
				try{
					for (int i = 0; i < allegati.size(); i++) {
						DumpAllegato allegato = allegati.get(i);
						String iEsimoAllegato=dir+"allegati"+File.separator+"allegato_"+(i+1)+File.separator;
						zip.putNextEntry(new ZipEntry(iEsimoAllegato+"manifest.xml"));
						UtilityTransazioni.writeManifestAllegatoXml(allegato, zip);
						zip.flush();
						zip.closeEntry();

						//salvo il file
						String fileName = "allegato";

						String ext = MimeTypeUtils.fileExtensionForMIMEType(allegato.getContentType());

						fileName+="."+ext;

						zip.putNextEntry(new ZipEntry(iEsimoAllegato+fileName));
						zip.write(allegato.getAllegato());	
						zip.flush();
						zip.closeEntry();
					}
				}catch(Exception ioe){
					String msg = "Si e' verificato un errore durante l'esportazione dei contenuti ("+tipo.toString()+") della transazione con id:"+t.getIdTransazione();
					msg+=" Errore durante la gestione degli allegati ("+ioe.getMessage()+")";
					this.log.error(msg,ioe);
					throw new ExportException(msg, ioe);
				}
			}
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
